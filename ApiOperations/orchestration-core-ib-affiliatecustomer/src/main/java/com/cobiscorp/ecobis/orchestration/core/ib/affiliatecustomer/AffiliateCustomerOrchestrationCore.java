/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.affiliatecustomer;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

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

import com.cobiscorp.cobis.crypt.ICobisCrypt;
import com.cobiscorp.cobis.commons.components.ComponentLocator;

/**
 * @author cecheverria
 * @since Sep 2, 2014
 * @version 1.0.0
 */
@Component(name = "AffiliateCustomerOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "AffiliateCustomerOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "AffiliateCustomerOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_affiliate_customer")
})
public class AffiliateCustomerOrchestrationCore extends SPJavaOrchestrationBase {// SPJavaOrchestrationBase
	
	private ILogger logger = (ILogger) this.getLogger();
	private ICobisCrypt cobisCrypt;
	private String loginId;
	private String userCreated;
	private String cardId;
	private String clabe;

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
		
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		logger.logDebug("Begin flow, AffiliateCustomerOrchestrationCore starts...");
		
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		queryAffiliateCustomer(aBagSPJavaOrchestration);
		return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}
	
	private void queryAffiliateCustomer(Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureRequest wQueryRequest = (IProcedureRequest) aBagSPJavaOrchestration.get("anOriginalRequest");
		
		aBagSPJavaOrchestration.clear();
		
		String xRequestId = wQueryRequest.readValueParam("@x_request_id");
		String xEndUserRequestDateTime = wQueryRequest.readValueParam("@x_end_user_request_date");
		String xEndUserIp = wQueryRequest.readValueParam("@x_end_user_ip"); 
		String xChannel = wQueryRequest.readValueParam("@x_channel");
		String idCustomer = wQueryRequest.readValueParam("@i_external_customer_id");
		String accountNumber = wQueryRequest.readValueParam("@i_accountNumber");
		String clabe="";
		String clabeSec="";
		String proveedor="";
		String categoria="";
		String clabeStatus="";
		String clabeError="";
		String tercerOden="";
		
		if (xRequestId.equals("null") || xRequestId.trim().isEmpty()) {
			aBagSPJavaOrchestration.put("400324", "x-request-id header is required");
			return;
		}
		
		if (xEndUserRequestDateTime.equals("null") || xEndUserRequestDateTime.trim().isEmpty()) {
			aBagSPJavaOrchestration.put("400325", "x-end-user-request-date-time header is required");
			return;
		}
		
		if (xEndUserIp.equals("null") || xEndUserIp.trim().isEmpty()) {
			aBagSPJavaOrchestration.put("400326", "x-end-user-ip header is required");
			return;
		}
		
		if (xChannel.equals("null") || xChannel.trim().isEmpty()) {
			aBagSPJavaOrchestration.put("400327", "x-channel header is required");
			return;
		}
		
		if (accountNumber.isEmpty()) {
			aBagSPJavaOrchestration.put("40082", "accountNumber must not be empty");
			return;
		}
				
		logger.logDebug("Begin flow, queryAffiliateCustomer with id: " + idCustomer);
				IProcedureRequest reqTMPCentral = (initProcedureRequest(wQueryRequest));

		
		reqTMPCentral.setSpName("cobis..sp_affi_custom_data_api_complete");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "central");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500101");
		
		reqTMPCentral.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
		reqTMPCentral.addInputParam("@i_accountNumber", ICTSTypes.SQLVARCHAR, accountNumber);
		
		reqTMPCentral.addOutputParam("@o_clabe", ICTSTypes.SQLVARCHAR, "X");
		reqTMPCentral.addOutputParam("@o_clabe_sec", ICTSTypes.SQLVARCHAR, "X");
		reqTMPCentral.addOutputParam("@o_categoria", ICTSTypes.SQLVARCHAR, "X");
		reqTMPCentral.addOutputParam("@o_clabe_status", ICTSTypes.SQLVARCHAR, "X");	
		reqTMPCentral.addOutputParam("@o_clabe_error", ICTSTypes.SQLVARCHAR, "X");
		reqTMPCentral.addOutputParam("@o_tercer_orden", ICTSTypes.SQLVARCHAR, "X");
		reqTMPCentral.addOutputParam("@o_proveedor", ICTSTypes.SQLVARCHAR, "X");
		
		IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("CLABE: JCOS " +  wProcedureResponseCentral.readValueParam("@o_clabe"));
			
			logger.logDebug("CLABE: " +  wProcedureResponseCentral.readValueParam("@o_clabe"));
			logger.logDebug("CLABE SEC: " +  wProcedureResponseCentral.readValueParam("@o_clabe_sec"));
			logger.logDebug("CLABE PROV: " +  wProcedureResponseCentral.readValueParam("@o_proveedor"));
			logger.logDebug("CLABE: " +  wProcedureResponseCentral.readValueParam("@o_categoria"));
			logger.logDebug("CLABE: " +  wProcedureResponseCentral.readValueParam("@o_clabe_status"));
			logger.logDebug("CLABE: " +  wProcedureResponseCentral.readValueParam("@o_clabe_error"));
			logger.logDebug("CLABE: " +  wProcedureResponseCentral.readValueParam("@o_tercer_orden"));
		}
		
		clabe = wProcedureResponseCentral.readValueParam("@o_clabe");
		clabeSec = wProcedureResponseCentral.readValueParam("@o_clabe_sec");
		proveedor = wProcedureResponseCentral.readValueParam("@o_proveedor");
		categoria= wProcedureResponseCentral.readValueParam("@o_categoria");
		clabeStatus=wProcedureResponseCentral.readValueParam("@o_clabe_status");
		clabeError=wProcedureResponseCentral.readValueParam("@o_clabe_error");
		tercerOden=wProcedureResponseCentral.readValueParam("@o_tercer_orden");		
		
		if(proveedor.equals("STP"))
			this.clabe=clabe;
		else
			this.clabe=clabeSec;
		
		
		if (logger.isInfoEnabled()) {
			logger.logDebug("Ending flow, queryAffiliateCustomer with wProcedureResponseCentral: " + wProcedureResponseCentral.getProcedureResponseAsString());
		}
		
		IProcedureResponse wProcedureResponseLocal;
		if (!wProcedureResponseCentral.hasError()) {
			
			IResultSetRow resultSetRow = wProcedureResponseCentral.getResultSet(1).getData().getRowsAsArray()[0];
			IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();
			
			if (columns[0].getValue().equals("true")) {
				
				IProcedureRequest reqTMPLocal = (initProcedureRequest(wQueryRequest));
				
				String mail = columns[11].getValue();
				String phone = columns[12].getValue();
				String user = createUser();
				String password = createPassword();
				
				reqTMPLocal.setSpName("cob_bvirtual..sp_affiliate_customer_validate_and_add_affiliate_api");
				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "local");
				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500101");
				
				reqTMPLocal.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
				reqTMPLocal.addInputParam("@i_accountNumber", ICTSTypes.SQLVARCHAR, accountNumber);
				reqTMPLocal.addInputParam("@i_pc_subtipo", ICTSTypes.SQLVARCHAR, columns[2].getValue());
				reqTMPLocal.addInputParam("@i_pc_nombre", ICTSTypes.SQLVARCHAR, columns[3].getValue());
				reqTMPLocal.addInputParam("@i_pc_ced_ruc", ICTSTypes.SQLVARCHAR, columns[4].getValue());
				reqTMPLocal.addInputParam("@i_pc_oficial", ICTSTypes.SQLINT4, columns[5].getValue());
				reqTMPLocal.addInputParam("@i_pc_oficina", ICTSTypes.SQLINT4, columns[6].getValue());
				reqTMPLocal.addInputParam("@i_pc_pnombre", ICTSTypes.SQLVARCHAR, columns[7].getValue());
				reqTMPLocal.addInputParam("@i_pc_papellido", ICTSTypes.SQLVARCHAR, columns[8].getValue());
				reqTMPLocal.addInputParam("@i_pc_sapellido", ICTSTypes.SQLVARCHAR, columns[9].getValue());
				reqTMPLocal.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, user);
				reqTMPLocal.addInputParam("@i_password", ICTSTypes.SQLVARCHAR, password);
				reqTMPLocal.addInputParam("@i_clave", ICTSTypes.SQLVARCHAR, createKey(user, password));
				reqTMPLocal.addInputParam("@i_medios_envio", ICTSTypes.SQLVARCHAR, getMedia(mail, phone));
				reqTMPLocal.addInputParam("@i_productos", ICTSTypes.SQLVARCHAR, getProducts(accountNumber,clabe,categoria,tercerOden,clabeStatus,clabeError,clabeSec));
				reqTMPLocal.addInputParam("@i_pc_fecha_nac", ICTSTypes.SQLDATETIME, columns[10].getValue());
				reqTMPLocal.addInputParam("@i_mail", ICTSTypes.SQLVARCHAR, mail);
				
				wProcedureResponseLocal = executeCoreBanking(reqTMPLocal);
				if (logger.isInfoEnabled()) {
					logger.logDebug("Ending flow, queryAffiliateCustomer with wProcedureResponseLocal: " + wProcedureResponseLocal.getProcedureResponseAsString());
				}

				if (!wProcedureResponseLocal.hasError()) {
					
					resultSetRow = wProcedureResponseLocal.getResultSet(wProcedureResponseLocal.getResultSetListSize()).getData().getRowsAsArray()[0];
					columns = resultSetRow.getColumnsAsArray();
					
					if (columns[1].getValue().equals("0")) {
						
						loginId = columns[2].getValue();
						userCreated = columns[3].getValue();
						cardId = columns[4].getValue();
						
						aBagSPJavaOrchestration.put("0", "Success");
						return;
						
					} else if (columns[1].getValue().equals("10001")) {
						
						loginId = columns[2].getValue();
						userCreated = columns[3].getValue();
						cardId = columns[4].getValue();
											
						aBagSPJavaOrchestration.put("10001", "Already affiliated customer");
						return;
					}					
				} else {
					
					aBagSPJavaOrchestration.put("50007", "Error affiliating a customer");
					return;
				}
				
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40012")) {
				
				aBagSPJavaOrchestration.put("40012", "Customer with externalCustomerId: " + idCustomer + " does not exist");
				return;
				
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40080")) {
				
				aBagSPJavaOrchestration.put("40080", "accountNumber does not exist");
				return;
				
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40081")) {
				
				aBagSPJavaOrchestration.put("40081", "The account number does not correspond to the customer id");
				return;
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40105")) {
				
				aBagSPJavaOrchestration.put("40105", "phoneNumber or mail is empty, you must update the information in the service updateProfile");
				return;
			} 
			 
		} else {
			aBagSPJavaOrchestration.put("50007", "Error affiliating a customer");
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
		
		metaData.addColumnMetaData(new ResultSetHeaderColumn("loginId", ICTSTypes.SYBINT4, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("userCreated", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SYBINT4, 2));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("clabe", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("cardId", ICTSTypes.SYBVARCHAR, 255));
		
		if (keyList.get(0).equals("0") || keyList.get(0).equals("10001")) {
			
			logger.logDebug("Ending flow, processResponse success with code: " + keyList.get(0));
			
			row.addRowData(1, new ResultSetRowColumnData(false, loginId));
			row.addRowData(2, new ResultSetRowColumnData(false, userCreated));
			row.addRowData(3, new ResultSetRowColumnData(false, "true"));
			row.addRowData(4, new ResultSetRowColumnData(false, (String) aBagSPJavaOrchestration.get(keyList.get(0))));
			row.addRowData(5, new ResultSetRowColumnData(false, keyList.get(0)));
			row.addRowData(6, new ResultSetRowColumnData(false, clabe));
			row.addRowData(7, new ResultSetRowColumnData(false, cardId));
			
			data.addRow(row);

		} else {
			
			logger.logDebug("Ending flow, processResponse failed with code: " + keyList.get(0));
			
			row.addRowData(1, new ResultSetRowColumnData(false, null));
			row.addRowData(2, new ResultSetRowColumnData(false, null));
			row.addRowData(3, new ResultSetRowColumnData(false, "false"));
			row.addRowData(4, new ResultSetRowColumnData(false, (String) aBagSPJavaOrchestration.get(keyList.get(0))));
			row.addRowData(5, new ResultSetRowColumnData(false,  keyList.get(0)));
			row.addRowData(6, new ResultSetRowColumnData(false, null));
			row.addRowData(7, new ResultSetRowColumnData(false, null));
			
			data.addRow(row);
		}
		
		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
		
		wProcedureResponse.addResponseBlock(resultBlock);			
		return wProcedureResponse;
	}
	
	private String createUser() {

		String login = null;
		try {
			Random ran = new Random();
			login = new BigInteger(130, ran).toString(32);
			login = login.substring(0, 7);

		} catch (Exception ex) {
			if (logger.isErrorEnabled()) {
				logger.logError("ERROR : AUTO AFILIA Exeption in createUser");
				logger.logError(ex);
			}
		}
		return login;
	}
	
	private String createPassword() {
		SecureRandom random = new SecureRandom();
		String password = new BigInteger(130, random).toString(32);
		return password.substring(0, 12);
	}
	
	private String createKey(String user, String password) {
		ComponentLocator componentLocator = ComponentLocator.getInstance(getClass());
		cobisCrypt = componentLocator.find(ICobisCrypt.class);
		return cobisCrypt.enCrypt(user, password);
	}
	
	public String getMedia(String mail, String phone) {

		String medios = "";

		if (mail != null && mail != "") {
			medios = medios += mail + ",MAIL,S,";
		}

		if (phone != null && phone != "") {

			medios = medios += phone + ",SMS,S";
		}

		return medios;
	}
	
	public String getProducts(String accounNumber,String clabe,String categoria,String ordenante,String statusClabe,String clabeError, String claveSec) {

		String producto = "";

		if (accounNumber != null && accounNumber != "") {

			producto = accounNumber + ",4,0,S,"+clabe+","+categoria+","+ordenante+","+statusClabe+","+clabeError+","+claveSec;
		}
		return producto;
	}
	
	
	
	
}
