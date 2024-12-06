/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.accountdebitoperation;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.components.ComponentLocator;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.exceptions.COBISInfrastructureRuntimeException;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.cts.reentry.api.IReentryPersister;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.OfflineApiTemplate;

/**
 * @author cecheverria
 * @since Sep 2, 2014
 * @version 1.0.0
 */
@Component(name = "AccountDebitOperationOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "AccountDebitOperationOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "AccountDebitOperationOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_debit_operation_api")
})
public class AccountDebitOperationOrchestrationCore extends OfflineApiTemplate {// SPJavaOrchestrationBase
	
	private ILogger logger = (ILogger) this.getLogger();
	private IResultSetRowColumnData[] columnsToReturn;

	private static final int ERROR40004 = 40004;
	private static final int ERROR40003 = 40003;
	private static final int ERROR40002 = 40002;

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
		
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		logger.logDebug("Begin flow, AccountDebitOperation start.");		
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		
		aBagSPJavaOrchestration.put("REENTRY_SSN", anOriginalRequest.readValueFieldInHeader("REENTRY_SSN_TRX"));
		
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setChannelId("8");
		ServerResponse responseServer = null;
		try {
			responseServer = getServerStatus(serverRequest);
		} catch (CTSServiceException e) {
			logger.logError(e.toString());
		} catch (CTSInfrastructureException e) {
			logger.logError(e.toString());
		}
		
		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();
		Boolean flowRty = evaluateExecuteReentry(anOriginalRequest);
		aBagSPJavaOrchestration.put("flowRty", flowRty);
		logger.logDebug("Response Online: " + responseServer.getOnLine() + " Response flowRty" + flowRty);
		if (responseServer != null && !responseServer.getOnLine()) {
			aBagSPJavaOrchestration.put("IsReentry", "S");
			if (!flowRty){
				logger.logDebug("evaluateExecuteReentry");
				anProcedureResponse = saveReentry(anOriginalRequest, aBagSPJavaOrchestration);

				executeOfflineTransacction(aBagSPJavaOrchestration, anOriginalRequest);
			}
			else{
				logger.logDebug("evaluateExecuteReentry FALSE");
				anProcedureResponse = Utils.returnException(40004, "NO EJECUTA REENTRY POR ESTAR EN OFFLINE!!!");
				logger.logDebug("Respose Exeption:: " + anProcedureResponse.toString());
				aBagSPJavaOrchestration.clear();
				aBagSPJavaOrchestration.put("50041", "NO EJECUTA REENTRY POR ESTAR EN OFFLINE!!!");
				return anProcedureResponse;				
			}
			
			logger.logDebug("Res IsReentry:: " + "S");
		} else {
			aBagSPJavaOrchestration.put("IsReentry", "N");
			logger.logDebug("Res IsReentry:: " + "N");
			queryAccountDebitOperation(aBagSPJavaOrchestration, anOriginalRequest);
		}
		
		
		return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}
	
	public boolean evaluateExecuteReentry(IProcedureRequest anOriginalRequest){		
		if (!Utils.isNull(anOriginalRequest.readValueFieldInHeader("reentryExecution"))){
			if (anOriginalRequest.readValueFieldInHeader("reentryExecution").equals("Y")){
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}
	public IProcedureResponse saveReentry(IProcedureRequest wQueryRequest, Map<String, Object> aBagSPJavaOrchestration) {
		String REENTRY_FILTER = "(service.impl=ReentrySPPersisterServiceImpl)";
		IProcedureRequest request = wQueryRequest.clone();
		ComponentLocator componentLocator = null;
	    IReentryPersister reentryPersister = null;
	    componentLocator = ComponentLocator.getInstance(this);
	    
	    aBagSPJavaOrchestration.put("rty_ssn",request.readValueFieldInHeader("ssn"));
	    
	    reentryPersister = (IReentryPersister) componentLocator.find(IReentryPersister.class, REENTRY_FILTER);
        if (reentryPersister == null)
            throw new COBISInfrastructureRuntimeException("Service IReentryPersister was not found");
        
        request.removeFieldInHeader("sessionId");
        request.addFieldInHeader("reentryPriority", 'S', "5");
        request.addFieldInHeader("REENTRY_SSN_TRX", 'S', request.readValueFieldInHeader("ssn"));
        request.addFieldInHeader("targetId", 'S', "local");
        request.removeFieldInHeader("serviceMethodName");
        request.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', request.readValueFieldInHeader("trn"));
        request.removeParam("@t_rty");
        
        Boolean reentryResponse = reentryPersister.addTransaction(request);

        IProcedureResponse response = initProcedureResponse(request);
        if (!reentryResponse.booleanValue()) {
        	logger.logDebug("Ending flow, saveReentry failed");
            response.addFieldInHeader("executionResult", 'S', "1");
            response.addMessage(1, "Ocurrio un error al tratar de registrar la transaccion en el Reentry CORE COBIS");
        } else {
        	logger.logDebug("Ending flow, saveReentry success");
            response.addFieldInHeader("executionResult", 'S', "0");
        }

        return response;
	}
	
	public ServerResponse getServerStatus(ServerRequest serverRequest) throws CTSServiceException, CTSInfrastructureException {

		IProcedureRequest aServerStatusRequest = new ProcedureRequestAS();
		aServerStatusRequest.setSpName("cobis..sp_server_status");
		aServerStatusRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800039");
		aServerStatusRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1800039");
		aServerStatusRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, "central");
		aServerStatusRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		aServerStatusRequest.setValueParam("@s_servicio", serverRequest.getChannelId());
		aServerStatusRequest.addInputParam("@i_cis", ICTSTypes.SYBCHAR, "S");
		aServerStatusRequest.addOutputParam("@o_en_linea", ICTSTypes.SYBCHAR, "S");
		aServerStatusRequest.addOutputParam("@o_fecha_proceso", ICTSTypes.SYBVARCHAR, "XXXX");

		if (logger.isDebugEnabled())
			logger.logDebug("Request Corebanking TTPA: " + aServerStatusRequest.getProcedureRequestAsString());

		IProcedureResponse wServerStatusResp = executeCoreBanking(aServerStatusRequest);

		if (logger.isDebugEnabled())
			logger.logDebug("Response Corebanking TTPA: " + wServerStatusResp.getProcedureResponseAsString());

		ServerResponse serverResponse = new ServerResponse();
		
		serverResponse.setSuccess(true);
		Utils.transformIprocedureResponseToBaseResponse(serverResponse, wServerStatusResp);
		serverResponse.setReturnCode(wServerStatusResp.getReturnCode());

		if (wServerStatusResp.getReturnCode() == 0) {
			serverResponse.setOfflineWithBalances(true);

			if (wServerStatusResp.readValueParam("@o_en_linea") != null)
				serverResponse.setOnLine(wServerStatusResp.readValueParam("@o_en_linea").equals("S") ? true : false);

			if (wServerStatusResp.readValueParam("@o_fecha_proceso") != null) {
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				try {
					serverResponse.setProcessDate(formatter.parse(wServerStatusResp.readValueParam("@o_fecha_proceso")));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		} else if (wServerStatusResp.getReturnCode() == ERROR40002 || wServerStatusResp.getReturnCode() == ERROR40003 || wServerStatusResp.getReturnCode() == ERROR40004) {
			serverResponse.setOnLine(false);
			serverResponse.setOfflineWithBalances(wServerStatusResp.getReturnCode() == ERROR40002 ? false : true);
		}

		if (logger.isDebugEnabled())
			logger.logDebug("Respuesta Devuelta: " + serverResponse);
		if (logger.isInfoEnabled())
			logger.logInfo("TERMINANDO SERVICIO");

		return serverResponse;
	}
	
	private void executeOfflineTransacction(Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
		logger.logDebug("execute executeOfflineTransacction: ");
		aBagSPJavaOrchestration.clear();
		String idCustomer = anOriginalRequest.readValueParam("@i_externalCustomerId");
		String accountNumber = anOriginalRequest.readValueParam("@i_accountNumber");
		String referenceNumber = anOriginalRequest.readValueParam("@i_referenceNumber");
		String debitReason = anOriginalRequest.readValueParam("@i_debitReason");
		BigDecimal amount = new BigDecimal(anOriginalRequest.readValueParam("@i_amount"));
		int originCode = 0;
		String originCodeStr = anOriginalRequest.readValueParam("@i_originCode");

		if (originCodeStr != null && !originCodeStr.isEmpty() && !originCodeStr.equals("null")) {
			originCode = Integer.parseInt(originCodeStr);
		}
		
		if (amount.compareTo(new BigDecimal("0")) != 1) {
			aBagSPJavaOrchestration.put("40107", "amount must be greater than 0");
			return;
		}
		
		if (accountNumber.isEmpty()) {
			aBagSPJavaOrchestration.put("40082", "accountNumber must not be empty");
			return;
		}
		
		if (referenceNumber.isEmpty()) {
			aBagSPJavaOrchestration.put("40092", "referenceNumber must not be empty");
			return;
		}
		
		if (referenceNumber.length() != 6) {
			aBagSPJavaOrchestration.put("40104", "referenceNumber must have 6 digits");
			return;
		}
		
		if (debitReason.trim().isEmpty()) {
			aBagSPJavaOrchestration.put("40123", "debitReason must not be empty");
			return;
		}

		if(debitReason.trim().equals("Card delivery fee")){
			debitReason = "8110";
		}else if(debitReason.trim().equals("False chargeback claim")){
			debitReason = "3101";
		}else{
			aBagSPJavaOrchestration.put("40124", "debit reason not found");
			return;
		}

		if(!originCodeStr.equals("null") && originCode <= 0 || originCode > 3){
			aBagSPJavaOrchestration.put("40125", "origin code not found");
			return;
		}
				
		logger.logDebug("Begin flow, queryAccountDebitOperation Offline with id: " + idCustomer);
		
		IProcedureRequest reqTMPCentral = (initProcedureRequest(anOriginalRequest));		
		reqTMPCentral.setSpName("cob_bvirtual..sp_account_operation_val_api");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', IMultiBackEndResolverService.TARGET_LOCAL);
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500118");
		reqTMPCentral.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
		reqTMPCentral.addInputParam("@i_accountNumber",ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_accountNumber"));
		reqTMPCentral.addInputParam("@i_amount",ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_amount"));
		//reqTMPCentral.addInputParam("@i_commission",ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_commission"));	 
	    //eqTMPCentral.addInputParam("@i_debitConcept",ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_debditConcept"));
	    // reqTMPCentral.addInputParam("@i_originCode",ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_originCode"));
	    
	    reqTMPCentral.addOutputParam("@o_ente_bv", ICTSTypes.SQLINT4, "0");
	    reqTMPCentral.addOutputParam("@o_login", ICTSTypes.SQLVARCHAR, "X");
	    reqTMPCentral.addOutputParam("@o_prod", ICTSTypes.SQLINT4, "0");
	    reqTMPCentral.addOutputParam("@o_mon", ICTSTypes.SQLINT4, "0");
		
	    IProcedureResponse wProcedureResponseVal = executeCoreBanking(reqTMPCentral);
		
	    aBagSPJavaOrchestration.put("o_prod", wProcedureResponseVal.readValueParam("@o_prod"));
		aBagSPJavaOrchestration.put("o_mon", wProcedureResponseVal.readValueParam("@o_mon"));
		aBagSPJavaOrchestration.put("o_login", wProcedureResponseVal.readValueParam("@o_login"));
		aBagSPJavaOrchestration.put("o_ente_bv", wProcedureResponseVal.readValueParam("@o_ente_bv"));
		
		if (logger.isInfoEnabled()) {
			logger.logDebug("Ending flow, queryAccountDebitOperation Offline with wProcedureResponseCentral: " + wProcedureResponseVal.getProcedureResponseAsString());
		}
		
		if (!wProcedureResponseVal.hasError()) {			
			IResultSetRow resultSetRow = wProcedureResponseVal.getResultSet(1).getData().getRowsAsArray()[0];
			IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();
			
			if (columns[0].getValue().equals("true")) {
				
				if (logger.isInfoEnabled()){
					logger.logInfo("Ejecutando transferencia Offline a terceros CORE COBIS" + anOriginalRequest);
					logger.logInfo("********** CAUSALES DE TRANSFERENCIA *************");
					logger.logInfo("********** CAUSA ORIGEN --->>> " + "4060");
					logger.logInfo("********** CLIENTE CORE --->>> " + aBagSPJavaOrchestration.get("ente_mis"));

				}
				//IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
				anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
				anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
						IMultiBackEndResolverService.TARGET_LOCAL);
				anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
				anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500111");

				anOriginalRequest.setSpName("cob_bvirtual..sp_bv_transaccion_off_api"); 

				anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500118");
				anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SYBINT4, "1");
				anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
				anOriginalRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0:0:0:0:0:0:0:1");
				anOriginalRequest.addInputParam("@i_causa_org", ICTSTypes.SQLVARCHAR, debitReason);
				//anOriginalRequest.addInputParam("@i_causa_des", ICTSTypes.SQLVARCHAR, "4050");
				anOriginalRequest.addInputParam("@i_servicio_costo", ICTSTypes.SQLVARCHAR, "CTRT");
				anOriginalRequest.addInputParam("@s_servicio", ICTSTypes.SYBINT4, "8");
				anOriginalRequest.addInputParam("@s_filial", ICTSTypes.SQLINT4, "1");
				anOriginalRequest.addInputParam("@s_cliente", ICTSTypes.SQLINT4, (String)aBagSPJavaOrchestration.get("o_ente_bv"));
				anOriginalRequest.addInputParam("@i_ente", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_externalCustomerId"));
				anOriginalRequest.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_accountNumber"));
				anOriginalRequest.addInputParam("@i_val", ICTSTypes.SQLMONEY4, anOriginalRequest.readValueParam("@i_amount"));
				//anOriginalRequest.addInputParam("@i_comision", ICTSTypes.SQLMONEY4, anOriginalRequest.readValueParam("@i_commission"));
				anOriginalRequest.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_creditConcept"));
				
				anOriginalRequest.addInputParam("@i_mon", ICTSTypes.SQLINT2, aBagSPJavaOrchestration.get("o_mon").toString());
				anOriginalRequest.addInputParam("@i_prod", ICTSTypes.SQLINT2, aBagSPJavaOrchestration.get("o_prod").toString());
				//anOriginalRequest.addInputParam("@i_mon_des", ICTSTypes.SQLINT2, aBagSPJavaOrchestration.get("o_mon_des").toString());
				//anOriginalRequest.addInputParam("@i_prod_des", ICTSTypes.SQLINT2, aBagSPJavaOrchestration.get("o_prod_des").toString());

				anOriginalRequest.addInputParam("@t_rty", ICTSTypes.SYBCHAR, "S");
				anOriginalRequest.addInputParam("@i_type_response", ICTSTypes.SYBCHAR, "S");
				
				anOriginalRequest.addInputParam("@i_genera_clave", ICTSTypes.SYBCHAR, "N");
				anOriginalRequest.addInputParam("@i_tipo_notif", ICTSTypes.SYBCHAR, "F");
				anOriginalRequest.addInputParam("@i_graba_notif", ICTSTypes.SYBCHAR, "N");
				anOriginalRequest.addInputParam("@i_graba_log", ICTSTypes.SYBCHAR, "N");
				anOriginalRequest.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("o_login"));
				anOriginalRequest.addInputParam("@i_bank_name", ICTSTypes.SQLVARCHAR, "CASHI");
				//anOriginalRequest.addInputParam("@i_beneficiary", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("o_nom_beneficiary"));
				
				if (logger.isDebugEnabled())
					logger.logDebug("Se envia Comission:" + anOriginalRequest.readValueParam("@i_comision"));
				anOriginalRequest.addInputParam("@i_comision", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_comision"));
				
				anOriginalRequest.addOutputParam("@o_fecha_tran", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXX");

				if (logger.isDebugEnabled())
					logger.logDebug("Data enviada a ejecutar api:" + anOriginalRequest);
				IProcedureResponse response = executeCoreBanking(anOriginalRequest);

				if (logger.isInfoEnabled())
					logger.logInfo("Respuesta Devuelta del Core api:" + response.getProcedureResponseAsString());

				logger.logInfo("Parametro @o_fecha_tran: " + response.readValueParam("@o_fecha_tran"));
				response.readValueParam("@o_fecha_tran");
				
				logger.logInfo("Parametro @ssn: " + response.readValueFieldInHeader("ssn"));
				if(response.readValueFieldInHeader("ssn")!=null)
				aBagSPJavaOrchestration.put("ssn", response.readValueFieldInHeader("ssn"));
				
				if (!response.hasError()) {

					resultSetRow = response.getResultSet(1).getData().getRowsAsArray()[0];
					columns = resultSetRow.getColumnsAsArray();
					
					if (columns[0].getValue().equals("true")) {
						this.columnsToReturn = columns;
						logger.logInfo("DCO LOG COLUMNS[1]: " + this.columnsToReturn[1].getValue());
						
						for(int i = 0; i< this.columnsToReturn.length;i++)
							logger.logInfo("DCO LOG COLUMNS["+i+"]: " + this.columnsToReturn[i].getValue());
						
						
						aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
						return;
						
					} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("50041")) {
						
						aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
						return;
					} 
					
				} else {
					aBagSPJavaOrchestration.put("50045", "Error account debit operation");
					return;
				}
								
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40012")) {
				
				aBagSPJavaOrchestration.put(columns[1].getValue(), "Customer with externalCustomerId: " + idCustomer + " does not exist");
				return;
				
			} else {
				
				aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
				return;
			}
				
			 
		} else {
			aBagSPJavaOrchestration.put("50045", "Error account debit operation");
			return;
		}
	}

	
	private void queryAccountDebitOperation(Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest wQueryRequest) {
		
		String reentryCode = (String)aBagSPJavaOrchestration.get("REENTRY_SSN");
		
		aBagSPJavaOrchestration.clear();
		String idCustomer = wQueryRequest.readValueParam("@i_externalCustomerId");
		String accountNumber = wQueryRequest.readValueParam("@i_accountNumber");
		String referenceNumber = wQueryRequest.readValueParam("@i_referenceNumber");
		String debitReason = wQueryRequest.readValueParam("@i_debitReason");
		BigDecimal amount = new BigDecimal(wQueryRequest.readValueParam("@i_amount"));
		int originCode = 0;
		String originCodeStr = wQueryRequest.readValueParam("@i_originCode");

		if (originCodeStr != null && !originCodeStr.isEmpty() && !originCodeStr.equals("null")) {
			originCode = Integer.parseInt(originCodeStr);
		}
		
		if (amount.compareTo(new BigDecimal("0")) != 1) {
			aBagSPJavaOrchestration.put("40107", "amount must be greater than 0");
			return;
		}
		
		if (accountNumber.isEmpty()) {
			aBagSPJavaOrchestration.put("40082", "accountNumber must not be empty");
			return;
		}
		
		if (referenceNumber.isEmpty()) {
			aBagSPJavaOrchestration.put("40092", "referenceNumber must not be empty");
			return;
		}
		
		if (referenceNumber.length() != 6) {
			aBagSPJavaOrchestration.put("40104", "referenceNumber must have 6 digits");
			return;
		}

		if (debitReason.trim().isEmpty()) {
			aBagSPJavaOrchestration.put("40123", "debitReason must not be empty");
			return;
		}

		if(debitReason.trim().equals("Card delivery fee")){
			debitReason = "8110";
		}else if(debitReason.trim().equals("False chargeback claim")){
			debitReason = "3101";
		}else{
			aBagSPJavaOrchestration.put("40124", "debit reason not found");
			return;
		}

		if(!originCodeStr.equals("null") && originCode <= 0 || originCode > 3){
			aBagSPJavaOrchestration.put("40125", "origin code not found");
			return;
		}
			
		logger.logDebug("Begin flow, queryAccountDebitOperation with id: " + idCustomer);
		
		IProcedureRequest reqTMPCentral = wQueryRequest;	
		
		if(reentryCode!=null){
			logger.logDebug("Flow: " + reentryCode);
			reqTMPCentral.setValueFieldInHeader(ICOBISTS.HEADER_SSN, reentryCode);
		}
			
		reqTMPCentral.setSpName("cobis..sp_account_debit_operation_central_api");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "central");
		reqTMPCentral.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500118");
		reqTMPCentral.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		reqTMPCentral.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
		reqTMPCentral.addInputParam("@i_accountNumber",ICTSTypes.SQLVARCHAR, wQueryRequest.readValueParam("@i_accountNumber"));
		reqTMPCentral.addInputParam("@i_amount",ICTSTypes.SQLMONEY, wQueryRequest.readValueParam("@i_amount"));
		//reqTMPCentral.addInputParam("@i_commission",ICTSTypes.SQLMONEY, wQueryRequest.readValueParam("@i_commission"));	 
	    //reqTMPCentral.addInputParam("@i_debitConcept",ICTSTypes.SQLVARCHAR, wQueryRequest.readValueParam("@i_debitConcept"));
	    reqTMPCentral.addInputParam("@i_originCode",ICTSTypes.SQLINT4, originCodeStr);
		reqTMPCentral.addInputParam("@i_debitReason",ICTSTypes.SQLVARCHAR, debitReason);
		aBagSPJavaOrchestration.put("ssn", wQueryRequest.readValueFieldInHeader("ssn"));
	    
	    IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);
		
		if (logger.isInfoEnabled()) {
			logger.logDebug("Ending flow, queryAccountDebitOperation with wProcedureResponseCentral: " + wProcedureResponseCentral.getProcedureResponseAsString());
		}
		
		IProcedureResponse wProcedureResponseLocal;
		if (!wProcedureResponseCentral.hasError()) {			
			IResultSetRow resultSetRow = wProcedureResponseCentral.getResultSet(wProcedureResponseCentral.getResultSetListSize()).getData().getRowsAsArray()[0];
			IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();
			
			if (columns[0].getValue().equals("true")) {
				this.columnsToReturn = columns;
				IProcedureRequest reqTMPLocal = (initProcedureRequest(wQueryRequest));
	
				reqTMPLocal.setSpName("cob_bvirtual..sp_account_debit_operation_local_api");
				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "local");
				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500118");
				reqTMPLocal.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
				reqTMPLocal.addInputParam("@i_accountNumber",ICTSTypes.SQLVARCHAR, wQueryRequest.readValueParam("@i_accountNumber"));
				reqTMPLocal.addInputParam("@i_amount",ICTSTypes.SQLMONEY, wQueryRequest.readValueParam("@i_amount"));
				//reqTMPLocal.addInputParam("@i_commission",ICTSTypes.SQLMONEY, wQueryRequest.readValueParam("@i_commission"));
				//reqTMPLocal.addInputParam("@i_latitude",ICTSTypes.SQLFLT8i, wQueryRequest.readValueParam("@i_latitude"));
				//reqTMPLocal.addInputParam("@i_longitude",ICTSTypes.SQLFLT8i, wQueryRequest.readValueParam("@i_longitude"));
				reqTMPLocal.addInputParam("@i_referenceNumber",ICTSTypes.SQLVARCHAR, wQueryRequest.readValueParam("@i_referenceNumber"));
				//reqTMPLocal.addInputParam("@i_debitConcept",ICTSTypes.SQLVARCHAR, wQueryRequest.readValueParam("@i_debitConcept"));
				// reqTMPLocal.addInputParam("@i_originCode",ICTSTypes.SQLINT4, wQueryRequest.readValueParam("@i_originCode"));
				
				wProcedureResponseLocal = executeCoreBanking(reqTMPLocal);
				if (logger.isInfoEnabled()) {
					logger.logDebug("Ending flow, queryAccountDebitOperation with wProcedureResponseLocal: " + wProcedureResponseLocal.getProcedureResponseAsString());
				}

				if (!wProcedureResponseLocal.hasError()) {
					
					resultSetRow = wProcedureResponseLocal.getResultSet(1).getData().getRowsAsArray()[0];
					columns = resultSetRow.getColumnsAsArray();
					
					if (columns[0].getValue().equals("true")) {
						
						aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
						return;
						
					} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("50045")) {
						
						aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
						return;
					} 
					
				} else {
					
					aBagSPJavaOrchestration.put("50045", "Error account debit operation");
					return;
				}
								
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40012")) {
				
				aBagSPJavaOrchestration.put(columns[1].getValue(), "Customer with externalCustomerId: " + idCustomer + " does not exist");
				return;
			} else {
				
				aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
				return;
			}
			 
		} else {
			aBagSPJavaOrchestration.put("50045", "Error account debit operation");
			return;
		}
		
		/* registerAllTransactionSuccess("AccountDebitOperationOrchestrationCore", wQueryRequest,"4060",
		            (String) aBagSPJavaOrchestration.get("ssn"));*/
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
		metaData.addColumnMetaData(new ResultSetHeaderColumn("referenceCode", ICTSTypes.SYBVARCHAR, 255));
		
		if (keyList.get(0).equals("0")) {
			logger.logDebug("Ending flow, processResponse success with code: " + keyList.get(0));
			row.addRowData(1, new ResultSetRowColumnData(false, this.columnsToReturn[0].getValue()));
			row.addRowData(2, new ResultSetRowColumnData(false, this.columnsToReturn[1].getValue()));
			row.addRowData(3, new ResultSetRowColumnData(false, this.columnsToReturn[2].getValue()));
			row.addRowData(4, new ResultSetRowColumnData(false, this.columnsToReturn[3].getValue()));
			data.addRow(row);

		} else {
			logger.logDebug("Ending flow, processResponse failed with code: " + keyList.get(0));
			row.addRowData(1, new ResultSetRowColumnData(false, "false"));
			row.addRowData(2, new ResultSetRowColumnData(false, keyList.get(0)));
			row.addRowData(3, new ResultSetRowColumnData(false, (String) aBagSPJavaOrchestration.get(keyList.get(0))));
			row.addRowData(4, new ResultSetRowColumnData(false, null));
			data.addRow(row);
		}
		
		 logger.logInfo("Llamo al metodo registrar CMFJ");
	       /* registerAllTransactionSuccess("AccountDebitOperationOrchestrationCore", anOriginalRequest,"4060",
	            (String) aBagSPJavaOrchestration.get("ssn"));*/

		
		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
		wProcedureResponse.addResponseBlock(resultBlock);			
		return wProcedureResponse;		
	}
	
	public IProcedureResponse saveReentry(IProcedureRequest wQueryRequest) {
		String REENTRY_FILTER = "(service.impl=ReentrySPPersisterServiceImpl)";
		IProcedureRequest request = wQueryRequest.clone();
		ComponentLocator componentLocator = null;
	    IReentryPersister reentryPersister = null;
	    componentLocator = ComponentLocator.getInstance(this);
	    
	    /*String originCode = request.readValueParam("@i_originCode");
	    logger.logDebug("@i_originCode = " + originCode);
		if (originCode == null) {
			logger.logDebug("Entre @i_originCode");
			request.addInputParam("@i_originCode",ICTSTypes.SQLINT4, "");
		}*/
	    
	    Utils.addInputParam(request, "@i_externalCustomerId", ICTSTypes.SQLINT4,  request.readValueParam("@i_externalCustomerId"));
        
	    reentryPersister = (IReentryPersister) componentLocator.find(IReentryPersister.class, REENTRY_FILTER);
        if (reentryPersister == null)
            throw new COBISInfrastructureRuntimeException("Service IReentryPersister was not found");
        
        request.removeFieldInHeader("sessionId");
        request.addFieldInHeader("reentryPriority", 'S', "5");
        request.addFieldInHeader("REENTRY_SSN_TRX", 'S', request.readValueFieldInHeader("ssn"));
        request.addFieldInHeader("targetId", 'S', "local");
        request.removeFieldInHeader("serviceMethodName");
        request.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', request.readValueFieldInHeader("trn"));
        request.removeParam("@t_rty");
        
        Boolean reentryResponse = reentryPersister.addTransaction(request);

        IProcedureResponse response = initProcedureResponse(request);
        if (!reentryResponse.booleanValue()) {
        	logger.logDebug("Ending flow, saveReentry failed");
            response.addFieldInHeader("executionResult", 'S', "1");
            response.addMessage(1, "Ocurrio un error al tratar de registrar la transaccion en el Reentry CORE COBIS");
        } else {
        	logger.logDebug("Ending flow, saveReentry success");
            response.addFieldInHeader("executionResult", 'S', "0");
        }

        return response;
	}

	@Override
	protected void loadDataCustomer(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		
	}

}
