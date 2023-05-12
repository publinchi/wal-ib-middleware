/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.updatecredentials;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.exceptions.COBISInfrastructureRuntimeException;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.cts.reentry.api.IReentryPersister;
import com.cobiscorp.cobis.crypt.ICobisCrypt;
import com.cobiscorp.cobis.commons.components.ComponentLocator;

import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.mobile.services.impl.utils.SimpleRSA;

/**
 * @author cecheverria
 * @since Sep 2, 2014
 * @version 1.0.0
 */
@Component(name = "UpdateCredentialsOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "UpdateCredentialsOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "UpdateCredentialsOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_updateCredentials")
})
public class UpdateCredentialsOrchestrationCore extends SPJavaOrchestrationBase {// SPJavaOrchestrationBase
	
	private ILogger logger = (ILogger) this.getLogger();
	private IResultSetRowColumnData[] columnsToReturn;
	private ICobisCrypt cobisCrypt;

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
		
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		logger.logDebug("Begin flow, UpdateCredentials start.");		
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		queryUpdateCredentials(aBagSPJavaOrchestration);
		return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}
	
	private void queryUpdateCredentials(Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureRequest wQueryRequest = (IProcedureRequest) aBagSPJavaOrchestration.get("anOriginalRequest");
		aBagSPJavaOrchestration.clear();
		String idCustomer = wQueryRequest.readValueParam("@i_externalCustomerId");
		String userName = wQueryRequest.readValueParam("@i_userName");
		String password = wQueryRequest.readValueParam("@i_password");
		String oldPassword = wQueryRequest.readValueParam("@i_oldPassword");
		String currentUser;
		
		if (userName.isEmpty()) {
			aBagSPJavaOrchestration.put("40109", "userName must not be empty");
			return;
		}
		
		if (password.isEmpty()) {
			aBagSPJavaOrchestration.put("40110", "password must not be empty");
			return;
		}
		
		if (oldPassword.isEmpty()) {
			aBagSPJavaOrchestration.put("40115", "oldPassword must not be empty");
			return;
		}
		
		if (userName.length() < 5) {
			aBagSPJavaOrchestration.put("40111", "userName must be at least 5 characters");
			return;
		}
		
		if (userName.length() > 12) {
			aBagSPJavaOrchestration.put("40112", "userName must have a maximum of 12 characters");
			return;
		}
		
		if (password.length() < 4) {
			aBagSPJavaOrchestration.put("40113", "password must be at least 4 characters");
			return;
		}
		
		if (password.length() > 12) {
			aBagSPJavaOrchestration.put("40114", "password must have a maximum of 12 characters");
			return;
		}
		
		if (oldPassword.length() < 4) {
			aBagSPJavaOrchestration.put("40116", "oldPassword must be at least 4 characters");
			return;
		}
		
		if (oldPassword.length() > 12) {
			aBagSPJavaOrchestration.put("40117", "oldPassword must have a maximum of 12 characters");
			return;
		}
		
		if (!containsDigit(password)) {
			aBagSPJavaOrchestration.put("50052", "password must have at least one number");
			return;
		}
		
		if (!checkStringHasAtLeastOneCapitalLetter(password)) {
			aBagSPJavaOrchestration.put("50051", "password must have at least one capital letter");
			return;
		}
		
		if (!containsSpecialCharacters(password)) {
			aBagSPJavaOrchestration.put("50053", "password must have at least one special character");
			return;
		}
		
		if (!containsDigit(oldPassword)) {
			aBagSPJavaOrchestration.put("50054", "oldPassword must have at least one number");
			return;
		}
		
		if (!checkStringHasAtLeastOneCapitalLetter(oldPassword)) {
			aBagSPJavaOrchestration.put("50055", "oldPassword must have at least one capital letter");
			return;
		}
		
		if (!containsSpecialCharacters(oldPassword)) {
			aBagSPJavaOrchestration.put("50056", "oldPassword must have at least one special character");
			return;
		}
		
		IProcedureRequest reqTMPCentral = (initProcedureRequest(wQueryRequest));		
		reqTMPCentral.setSpName("cobis..sp_updateCredentials_central_api");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "central");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500125");
		reqTMPCentral.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
	    IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);
		
		if (logger.isInfoEnabled()) {
			logger.logDebug("Ending flow, queryUpdateCredentials with wProcedureResponseCentral: " + wProcedureResponseCentral.getProcedureResponseAsString());
		}
		
		IProcedureResponse wProcedureResponseLocal;
		if (!wProcedureResponseCentral.hasError()) {			
			IResultSetRow resultSetRow = wProcedureResponseCentral.getResultSet(wProcedureResponseCentral.getResultSetListSize()).getData().getRowsAsArray()[0];
			IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();
			
			if (columns[0].getValue().equals("true")) {
				IProcedureRequest reqTMPLocal = (initProcedureRequest(wQueryRequest));
				
				reqTMPLocal.setSpName("cob_bvirtual..sp_validate_get_login_api");
				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "local");
				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500125");
				reqTMPLocal.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
				wProcedureResponseLocal = executeCoreBanking(reqTMPLocal);
				if (logger.isInfoEnabled()) {
					logger.logDebug("Ending flow, queryUpdateCredentials with wProcedureResponseLocal: " + wProcedureResponseLocal.getProcedureResponseAsString());
				}

				if (!wProcedureResponseLocal.hasError()) {
					
					resultSetRow = wProcedureResponseLocal.getResultSet(1).getData().getRowsAsArray()[0];
					columns = resultSetRow.getColumnsAsArray();
					
					if (columns[0].getValue().equals("true")) {
						currentUser = columns[3].getValue();
						
						reqTMPLocal = (initProcedureRequest(wQueryRequest));
						
						reqTMPLocal.setSpName("cob_bvirtual..sp_validate_credentials_api");
						reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "local");
						reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500125");
						reqTMPLocal.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
						reqTMPLocal.addInputParam("@i_oldPassword",ICTSTypes.SQLVARCHAR, createKey(currentUser, oldPassword));
						wProcedureResponseLocal = executeCoreBanking(reqTMPLocal);
						if (logger.isInfoEnabled()) {
							logger.logDebug("Ending flow, queryUpdateCredentials with wProcedureResponseLocal: " + wProcedureResponseLocal.getProcedureResponseAsString());
						}

						if (!wProcedureResponseLocal.hasError()) {
							
							resultSetRow = wProcedureResponseLocal.getResultSet(1).getData().getRowsAsArray()[0];
							columns = resultSetRow.getColumnsAsArray();
							
							if (columns[0].getValue().equals("true")) {
								String tokenOld = createKey(currentUser, oldPassword);
								String tokenNew = createKey(currentUser, password);
								
								if (tokenOld.equals(tokenNew)) {
									aBagSPJavaOrchestration.put("50057", "The new password must be different to the previous one");
									return;
								}
								
								reqTMPLocal = (initProcedureRequest(wQueryRequest));
								
								reqTMPLocal.setSpName("cob_bvirtual..sp_updateCredentials_local_api");
								reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "local");
								reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500125");
								reqTMPLocal.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
								reqTMPLocal.addInputParam("@i_userName",ICTSTypes.SQLVARCHAR, userName);
								reqTMPLocal.addInputParam("@i_password",ICTSTypes.SQLVARCHAR, createKey(userName, password));
					
								wProcedureResponseLocal = executeCoreBanking(reqTMPLocal);
								if (logger.isInfoEnabled()) {
									logger.logDebug("Ending flow, queryUpdateCredentials with wProcedureResponseLocal: " + wProcedureResponseLocal.getProcedureResponseAsString());
								}

								if (!wProcedureResponseLocal.hasError()) {
									
									resultSetRow = wProcedureResponseLocal.getResultSet(1).getData().getRowsAsArray()[0];
									columns = resultSetRow.getColumnsAsArray();
									
									if (columns[0].getValue().equals("true")) {
										
										aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
										this.columnsToReturn = columns;
										return;
										
									} else {
										
										aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
										return;
									} 
									
								} else {
									
									aBagSPJavaOrchestration.put("50050", "Error updating credentials");
									return;
								}
								
							} else {
								
								aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
								return;
							} 
							
						} else {
							
							aBagSPJavaOrchestration.put("50050", "Error updating credentials");
							return;
						}
						
					} else {
						
						aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
						return;
					} 
					
				} else {
					
					aBagSPJavaOrchestration.put("50050", "Error updating credentials");
					return;
				}
								
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40012")) {
				
				aBagSPJavaOrchestration.put(columns[1].getValue(), "Customer with externalCustomerId: " + idCustomer + " does not exist");
				return;
			}
			
		} else {
			aBagSPJavaOrchestration.put("50050", "Error updating credentials");
			return;
		}
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		ArrayList<String> keyList = new ArrayList<String>(aBagSPJavaOrchestration.keySet());
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		IResultSetRow row = new ResultSetRow();
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SYBINT4, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SYBVARCHAR, 255));
		
		if (keyList.get(0).equals("0")) {
			logger.logDebug("Ending flow, processResponse success with code: " + keyList.get(0));
			row.addRowData(1, new ResultSetRowColumnData(false, this.columnsToReturn[0].getValue()));
			row.addRowData(2, new ResultSetRowColumnData(false, this.columnsToReturn[1].getValue()));
			row.addRowData(3, new ResultSetRowColumnData(false, this.columnsToReturn[2].getValue()));
			data.addRow(row);

		} else {
			logger.logDebug("Ending flow, processResponse failed with code: " + keyList.get(0));
			row.addRowData(1, new ResultSetRowColumnData(false, "false"));
			row.addRowData(2, new ResultSetRowColumnData(false, keyList.get(0)));
			row.addRowData(3, new ResultSetRowColumnData(false, (String) aBagSPJavaOrchestration.get(keyList.get(0))));
			data.addRow(row);
		}
		
		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
		wProcedureResponse.addResponseBlock(resultBlock);			
		return wProcedureResponse;		
	}
	
	public boolean checkStringHasAtLeastOneCapitalLetter(String str) {
		Pattern p = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$");
		Matcher m = p.matcher(str);
		return m.find();
	}
	
	public boolean containsDigit(String s) {
	    boolean containsDigit = false;

	    if (s != null && !s.isEmpty()) {
	        for (char c : s.toCharArray()) {
	            if (containsDigit = Character.isDigit(c)) {
	                break;
	            }
	        }
	    }

	    return containsDigit;
	}
	
	public boolean containsSpecialCharacters(String s) {
		Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(s);
		return m.find();
	}
	
	private String createKey(String user, String password) {
		ComponentLocator componentLocator = ComponentLocator.getInstance(getClass());
		cobisCrypt = componentLocator.find(ICobisCrypt.class);
		return cobisCrypt.enCrypt(user, password);
	}

}
