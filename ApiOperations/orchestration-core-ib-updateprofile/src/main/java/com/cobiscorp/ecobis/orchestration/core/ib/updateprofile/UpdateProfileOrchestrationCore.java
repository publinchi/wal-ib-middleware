/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.updateprofile;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
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

/**
 * @author cecheverria
 * @since Sep 2, 2014
 * @version 1.0.0
 */
@Component(name = "UpdateProfileOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "UpdateProfileOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "UpdateProfileOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_updateProfile")
})
public class UpdateProfileOrchestrationCore extends SPJavaOrchestrationBase {// SPJavaOrchestrationBase
	
	private ILogger logger = (ILogger) this.getLogger();

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
		
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		logger.logDebug("Begin flow, UpdateProfileOrchestrationCore start.");		
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		queryUpdateProfile(aBagSPJavaOrchestration);
		return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}
	
	private void queryUpdateProfile(Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureRequest wQueryUpdateProfileRequest = (IProcedureRequest) aBagSPJavaOrchestration.get("anOriginalRequest");
		aBagSPJavaOrchestration.clear();
		String idCustomer = wQueryUpdateProfileRequest.readValueParam("@i_externalCustomerId");
		String mail = wQueryUpdateProfileRequest.readValueParam("@i_email");
		String phone = wQueryUpdateProfileRequest.readValueParam("@i_phoneNumber");
		
		if (mail.isEmpty()) {
			aBagSPJavaOrchestration.put("40037", "email must not be empty");
			return;
		}
		
		if (!isValidMail(mail)) {
			aBagSPJavaOrchestration.put("40122", "email is not valid");
			return;
		}
			
		if (phone.isEmpty()) {			
			aBagSPJavaOrchestration.put("40038", "phoneNumber must not  be empty");
			return;
		}
		
		if (phone.length() != 10) {
			aBagSPJavaOrchestration.put("40039", "phoneNumber must be 10 characters");	
			return;
		}
	
		logger.logDebug("Begin flow, queryUpdateProfile with id: " + idCustomer);
		
		IProcedureRequest reqTMPCentral = (initProcedureRequest(wQueryUpdateProfileRequest));
		reqTMPCentral.setSpName("cobis..sp_updateProfile");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "central");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500095");
		reqTMPCentral.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
		reqTMPCentral.addInputParam("@i_email", ICTSTypes.SQLVARCHAR, mail);
		reqTMPCentral.addInputParam("@i_phoneNumber", ICTSTypes.SQLVARCHAR, phone);
		IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);
		if (logger.isInfoEnabled()) {
			logger.logDebug("Ending flow, queryUpdateProfile with wProcedureResponseCentral: " + wProcedureResponseCentral.getProcedureResponseAsString());
		}
		
		IProcedureResponse wProcedureResponseLocal;
		if (!wProcedureResponseCentral.hasError()) {
			IResultSetRow resultSetRow = wProcedureResponseCentral.getResultSet(1).getData().getRowsAsArray()[0];
			IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();
			
			if (columns[0].getValue().equals("true")) {
				IProcedureRequest reqTMPLocal = (initProcedureRequest(wQueryUpdateProfileRequest));
				reqTMPLocal.setSpName("cob_bvirtual..sp_updateProfile");
				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "local");
				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500095");
				reqTMPLocal.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
				reqTMPLocal.addInputParam("@i_email", ICTSTypes.SQLVARCHAR, mail);
				reqTMPLocal.addInputParam("@i_phoneNumber", ICTSTypes.SQLVARCHAR, phone);
				wProcedureResponseLocal = executeCoreBanking(reqTMPLocal);
				if (logger.isInfoEnabled()) {
					logger.logDebug("Ending flow, queryUpdateProfile with wProcedureResponseLocal: " + wProcedureResponseLocal.getProcedureResponseAsString());
				}

				if (!wProcedureResponseLocal.hasError()) {
					resultSetRow = wProcedureResponseLocal.getResultSet(1).getData().getRowsAsArray()[0];
					columns = resultSetRow.getColumnsAsArray();
					
					if (columns[1].getValue().equals("0")) {
						aBagSPJavaOrchestration.put("0", "Success");
						return;
						
					} else if (columns[1].getValue().equals("18053")) {
						
						aBagSPJavaOrchestration.put("18053", "The customer information has been updated but it is not affiliated");
						return;
					}					
				} else {
					
					aBagSPJavaOrchestration.put("50004", "Error updating information about the customer");
					return;
				}
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40012")) {
				
				aBagSPJavaOrchestration.put("40012", "Customer with externalCustomerId: " + idCustomer + " does not exist");
				return;
				
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40040")) {
				
				aBagSPJavaOrchestration.put("40040", "The phone number was already registered with another customer");
				return;
			}
			 
			
		} else {
			aBagSPJavaOrchestration.put("50004", "Error updating information about the customer");
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
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SYBINT4, 2));
		
		if (keyList.get(0).equals("0") || keyList.get(0).equals("18053")) {
			logger.logDebug("Ending flow, processResponse success with code: " + keyList.get(0));
			row.addRowData(1, new ResultSetRowColumnData(false, "true"));
			row.addRowData(2, new ResultSetRowColumnData(false, (String) aBagSPJavaOrchestration.get(keyList.get(0))));
			row.addRowData(3, new ResultSetRowColumnData(false, keyList.get(0)));
			data.addRow(row);

		} else {
			logger.logDebug("Ending flow, processResponse failed with code: " + keyList.get(0));
			row.addRowData(1, new ResultSetRowColumnData(false, "false"));
			row.addRowData(2, new ResultSetRowColumnData(false, (String) aBagSPJavaOrchestration.get(keyList.get(0))));
			row.addRowData(3, new ResultSetRowColumnData(false,  keyList.get(0)));
			data.addRow(row);
		}
		
		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
		wProcedureResponse.addResponseBlock(resultBlock);			
		return wProcedureResponse;
	}
	
	public boolean isValidMail(String email)
    {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                            "[a-zA-Z0-9_+&*-]+)*@" +
                            "(?:[a-zA-Z0-9-]+\\.)+[a-z]{2,7}$";
                              
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}
