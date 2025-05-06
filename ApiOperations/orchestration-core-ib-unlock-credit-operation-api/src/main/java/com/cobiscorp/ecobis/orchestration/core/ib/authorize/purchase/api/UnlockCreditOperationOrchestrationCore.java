/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.authorize.purchase.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
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
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.OfflineApiTemplate;

/**
 * @author D. Collaguazo
 * @since Apr 10, 2025
 * @version 1.0.0
 */
@Component(name = "UnlockCreditOperationOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "UnlockCreditOperationOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "UnlockCreditOperationOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_desbloquea_remesas")})
public class UnlockCreditOperationOrchestrationCore extends OfflineApiTemplate {
	
	private ILogger logger = (ILogger) this.getLogger();
	private static final String CLASS_NAME = "UnlockCreditOperationOrchestrationCore";
	protected static final String CHANNEL_REQUEST = "8";
	protected static final String MODE_OPERATION = "PYS";
	protected static final String UNLOCK = "UNLOCK";
	protected static final String TIPO_OPERACION = "L";
	protected static final String CAUSAL_DEBLOQUEO = "10";
	protected static final String SOLICITANTE = "user";
	protected static final String AUTORIZANTE = "user";
	protected static final String MONEDA = "0";
	protected static final String CODIGO_NOTIFICACION = "N157";
	protected static final String TIPO_OPE_LOG = "UBS";

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		
		if (logger.isDebugEnabled()){logger.logDebug("Begin flow, UnlockCreditOperationOrchestrationCore starts...");}		
		
		aBagSPJavaOrchestration.put("REENTRY_SSN", anOriginalRequest.readValueFieldInHeader("REENTRY_SSN_TRX"));
		Boolean serverStatus = null;

		try {
			serverStatus = getServerStatus();
		} catch (CTSServiceException e) {
			logger.logError(e.toString());
		} catch (CTSInfrastructureException e) {
			logger.logError(e.toString());
		}
		
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);
		
		/* Validar comportamiento transaccion */
		if(!validateContextTransacction(aBagSPJavaOrchestration,serverStatus)) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException(this.MESSAGE_RESPONSE));
			return Utils.returnException(this.MESSAGE_RESPONSE);
		}
		
		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();
		Boolean flowRty = evaluateExecuteReentry(anOriginalRequest);
		aBagSPJavaOrchestration.put("flowRty", flowRty);
		aBagSPJavaOrchestration.put("serverStatus", serverStatus);
		
		if (logger.isDebugEnabled()){
			logger.logDebug("Response Online: " + serverStatus);
			logger.logDebug("Response flowRty: " + flowRty);
		}
		
		if (serverStatus != null && !serverStatus) {
			aBagSPJavaOrchestration.put("IsReentry", "S");
			if (!flowRty){				
				anProcedureResponse = valDataOperationRemesas(anOriginalRequest, aBagSPJavaOrchestration);
				if(anProcedureResponse.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
					logger.logInfo(CLASS_NAME + " anProcedureResponse ofline " + anProcedureResponse);
					
					if (logger.isDebugEnabled()){logger.logDebug("Code Error local" + anProcedureResponse.getResultSetRowColumnData(2, 1, 2));}
					
					anProcedureResponse = saveReentry(anOriginalRequest, aBagSPJavaOrchestration);
						
					if (logger.isDebugEnabled()){logger.logDebug("executeOfflineUnlockCreditOperation " + anProcedureResponse.toString() );}
						
					anProcedureResponse = executeOfflineUnlockCreditOperation(anOriginalRequest, aBagSPJavaOrchestration);
					
				}				
			}
			else{
				IProcedureResponse resp = Utils.returnException(40004, "NO EJECUTA REENTRY POR ESTAR EN OFFLINE!!!");
				if (logger.isDebugEnabled()){
					logger.logDebug("evaluateExecuteReentry True");
					logger.logDebug("Respose Exeption:: " + resp.toString());
				}
				return resp;
			}
		} else {
			if (logger.isDebugEnabled()){logger.logDebug("Res IsReentry:: " + "N");}
			aBagSPJavaOrchestration.put("IsReentry", "N");			
			anProcedureResponse = unlockCreditOperation(anOriginalRequest, aBagSPJavaOrchestration);
		}
		
		return processResponseApi(anOriginalRequest, anProcedureResponse,aBagSPJavaOrchestration);
	}
	
	private IProcedureResponse unlockCreditOperation(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en unlockCreditOperation: ");
		}
		
		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();
		IProcedureResponse wAuthValDataLocal = new ProcedureResponseAS();
		wAuthValDataLocal = valDataOperationRemesas(aRequest, aBagSPJavaOrchestration);
		
		if (logger.isInfoEnabled()){logger.logInfo(CLASS_NAME + " code resp auth: " + wAuthValDataLocal.getResultSetRowColumnData(2, 1, 1).getValue());}
		if (wAuthValDataLocal.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			
			
			if(aBagSPJavaOrchestration.get("o_tran_status_credit").equals("O") && aBagSPJavaOrchestration.get("o_tran_status_reentry").equals("P")){
				if (logger.isDebugEnabled()){logger.logInfo(CLASS_NAME + " force reentry tran ");}
				
				anProcedureResponse = saveReentry(aRequest, aBagSPJavaOrchestration);
					
				if (logger.isDebugEnabled()){logger.logDebug("executeOfflineUnlockCreditOperation " + anProcedureResponse.toString() );}
					
				anProcedureResponse = executeOfflineUnlockCreditOperation(aRequest, aBagSPJavaOrchestration);
				
			}
			else{
			IProcedureResponse wAuthTrnDataCentral = new ProcedureResponseAS();
			wAuthTrnDataCentral = executeUnlockCreditOperation(aRequest, aBagSPJavaOrchestration);
			
			return wAuthTrnDataCentral;
			}
		}
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Response " + wAuthValDataLocal.toString());
			logger.logInfo(CLASS_NAME + " Saliendo de unlockCreditOperation...");
		}

		return wAuthValDataLocal;
	}
	
	private IProcedureResponse valDataOperationRemesas(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureRequest request = new ProcedureRequestAS();
		
		if(logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en valDataOperationRemesas");
		}

		request.setSpName("cob_ahorros..sp_bv_val_credit_operation");
		
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_external_customer_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_external_customer_id"));
		request.addInputParam("@i_account_number", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account_number"));
		request.addInputParam("@i_reference_number", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_reference_number"));
		request.addInputParam("@i_movement_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_movement_id"));
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, UNLOCK);
		if(aBagSPJavaOrchestration.get("IsReentry").equals("S"))
			request.addInputParam("@i_reentry", ICTSTypes.SQLCHAR, "S");
		if(aBagSPJavaOrchestration.get("flowRty").equals(true))
			request.addInputParam("@i_val_uuid", ICTSTypes.SQLCHAR, "S");
		
		//Header
		request.addInputParam("@x_legacy_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_legacy-id"));
		request.addInputParam("@x_client_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_client-id"));
		request.addInputParam("@x_uuid", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_uuid"));
		request.addInputParam("@x_apigw_api_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_apigw-api-id"));
		
		request.addOutputParam("@o_secuencial", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_seq", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_reentry", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_monto", ICTSTypes.SQLMONEY4, "0");
		request.addOutputParam("@o_sender_name", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_money_transmitter", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_credit_concept", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_tran_status_credit", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_tran_status_reentry", ICTSTypes.SQLVARCHAR, "X");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("secuencial es " +  wProductsQueryResp.readValueParam("@o_secuencial"));
			logger.logDebug("monto es " +  wProductsQueryResp.readValueParam("@o_monto"));
			logger.logDebug("seq es " +  wProductsQueryResp.readValueParam("@o_seq"));
			logger.logDebug("reentry es " +  wProductsQueryResp.readValueParam("@o_reentry"));
			logger.logDebug("sender name " +  wProductsQueryResp.readValueParam("@o_sender_name"));
			logger.logDebug("money transmitter " +  wProductsQueryResp.readValueParam("@o_money_transmitter"));
			logger.logDebug("credit concept " +  wProductsQueryResp.readValueParam("@o_credit_concept"));
			logger.logDebug("tran status credit " +  wProductsQueryResp.readValueParam("@o_tran_status_credit"));
			logger.logDebug("tran status reentry " +  wProductsQueryResp.readValueParam("@o_tran_status_reentry"));
		}
		
		aBagSPJavaOrchestration.put("seq", wProductsQueryResp.readValueParam("@o_seq"));
		aBagSPJavaOrchestration.put("reentry", wProductsQueryResp.readValueParam("@o_reentry"));
		aBagSPJavaOrchestration.put("o_secuencial", wProductsQueryResp.readValueParam("@o_secuencial"));
		aBagSPJavaOrchestration.put("o_monto", wProductsQueryResp.readValueParam("@o_monto"));
		aBagSPJavaOrchestration.put("o_sender_name", wProductsQueryResp.readValueParam("@o_sender_name"));
		aBagSPJavaOrchestration.put("o_money_transmitter", wProductsQueryResp.readValueParam("@o_money_transmitter"));
		aBagSPJavaOrchestration.put("o_credit_concept", wProductsQueryResp.readValueParam("@o_credit_concept"));
		aBagSPJavaOrchestration.put("o_tran_status_credit", wProductsQueryResp.readValueParam("@o_tran_status_credit"));
		aBagSPJavaOrchestration.put("o_tran_status_reentry", wProductsQueryResp.readValueParam("@o_tran_status_reentry"));
		
		if(!wProductsQueryResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			aBagSPJavaOrchestration.put("code_error", wProductsQueryResp.getResultSetRowColumnData(2, 1, 1).getValue());
			aBagSPJavaOrchestration.put("message_error", wProductsQueryResp.getResultSetRowColumnData(2, 1, 2).getValue());
			if (logger.isDebugEnabled()){logger.logDebug("Code Error local" +aBagSPJavaOrchestration.get("code_error"));}
		}
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking valDataOperationRemesas: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de valDataOperationRemesas BAG:" + aBagSPJavaOrchestration);
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse executeUnlockCreditOperation(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureRequest request = initProcedureRequest(aRequest);

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en executeUnlockCreditOperation");
		}
		
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);

		if(aBagSPJavaOrchestration.get("flowRty").equals(true))
			request.addInputParam("@t_rty", ICTSTypes.SQLCHAR, "S");
		
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "218");
		
		request.setSpName("cob_ahorros..sp_bloq_val_ah_api");
		
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account_number"));
		request.addInputParam("@i_mon", ICTSTypes.SQLINT1, MONEDA);
		request.addInputParam("@i_accion", ICTSTypes.SQLVARCHAR, TIPO_OPERACION);
		request.addInputParam("@i_causa", ICTSTypes.SQLVARCHAR, CAUSAL_DEBLOQUEO);
		request.addInputParam("@i_sec", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_secuencial").toString());
		request.addInputParam("@i_valor", ICTSTypes.SQLMONEY, aBagSPJavaOrchestration.get("o_monto").toString());
		request.addInputParam("@i_aut", ICTSTypes.SQLVARCHAR, AUTORIZANTE);
		request.addInputParam("@i_solicit", ICTSTypes.SQLVARCHAR, SOLICITANTE );		
		
		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, "218");
		
		if (logger.isDebugEnabled()){logger.logDebug("request DCO PRE:: " + request.toString());}
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("ssn branch es " +  wProductsQueryResp.readValueFieldInHeader("ssn_branch"));
		}
		
		aBagSPJavaOrchestration.put("o_movement_id", wProductsQueryResp.readValueFieldInHeader("ssn_branch"));
		
		if(!wProductsQueryResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			aBagSPJavaOrchestration.put("code_error", wProductsQueryResp.getResultSetRowColumnData(2, 1, 1).getValue());
			aBagSPJavaOrchestration.put("message_error", wProductsQueryResp.getResultSetRowColumnData(2, 1, 2).getValue());
			if (logger.isDebugEnabled()){logger.logDebug("Code Error" +aBagSPJavaOrchestration.get("code_error"));}
		}else{
			aBagSPJavaOrchestration.put("o_success", "true");
		}
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de trnDataCentral");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse executeOfflineUnlockCreditOperation(IProcedureRequest anOrgRequest, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled()){
			logger.logInfo(CLASS_NAME + "Ejecutando executeOfflineUnlockCreditOperation CORE COBIS" + anOrgRequest);
		}

		IProcedureRequest request = initProcedureRequest(anOrgRequest);
		
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);

		if(aBagSPJavaOrchestration.get("flowRty").equals(true))
			request.addInputParam("@t_rty", ICTSTypes.SQLCHAR, "S");
		
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "218");
		
		request.setSpName("cob_ahorros..sp_bloq_val_ah_api_local");
		
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, anOrgRequest.readValueParam("@i_account_number"));
		request.addInputParam("@i_mon", ICTSTypes.SQLINT1, MONEDA);
		request.addInputParam("@i_accion", ICTSTypes.SQLVARCHAR, TIPO_OPERACION);
		request.addInputParam("@i_causa", ICTSTypes.SQLVARCHAR, CAUSAL_DEBLOQUEO);
		request.addInputParam("@i_sec", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_secuencial").toString());
		request.addInputParam("@i_valor", ICTSTypes.SQLMONEY, aBagSPJavaOrchestration.get("o_monto").toString());
		request.addInputParam("@i_aut", ICTSTypes.SQLVARCHAR, AUTORIZANTE);
		request.addInputParam("@i_solicit", ICTSTypes.SQLVARCHAR, SOLICITANTE);
		request.addInputParam("@i_server_online", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("serverStatus").toString());
		
		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, "218");
		
		if (logger.isDebugEnabled()){logger.logDebug("request DCO PRE:: " + request.toString());}
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("ssn branch es " +  wProductsQueryResp.readValueFieldInHeader("ssn_branch"));
		}
		
		aBagSPJavaOrchestration.put("o_movement_id", wProductsQueryResp.readValueFieldInHeader("ssn_branch"));
		
		if(!wProductsQueryResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			aBagSPJavaOrchestration.put("code_error", wProductsQueryResp.getResultSetRowColumnData(2, 1, 1).getValue());
			aBagSPJavaOrchestration.put("message_error", wProductsQueryResp.getResultSetRowColumnData(2, 1, 2).getValue());
			if (logger.isDebugEnabled()){logger.logDebug("Code Error" +aBagSPJavaOrchestration.get("code_error"));}
		}else{
			aBagSPJavaOrchestration.put("o_success", "true");
		}
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de trnDataCentral");
		}

		return wProductsQueryResp;
	}

	private void updateTrnStatus(IProcedureResponse aResponse, Map<String, Object> aBagSPJavaOrchestration, String executionStatus) {
		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en updateTransferStatus");
		}

		request.setSpName("cob_bvirtual..sp_update_transfer_status");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_seq", ICTSTypes.SQLINTN, (String) aBagSPJavaOrchestration.get("seq"));
		request.addInputParam("@i_reentry", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("reentry"));
		request.addInputParam("@i_exe_status", ICTSTypes.SQLVARCHAR, executionStatus);
		request.addInputParam("@i_movementId", ICTSTypes.SQLINTN, aBagSPJavaOrchestration.containsKey("o_movement_id")?aBagSPJavaOrchestration.get("o_movement_id").toString():null);
		
		request.addInputParam("@i_error", ICTSTypes.SQLINTN, aBagSPJavaOrchestration.containsKey("code_error")?aBagSPJavaOrchestration.get("code_error").toString():null);
		request.addOutputParam("@o_codigo", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_mensaje", ICTSTypes.SQLVARCHAR, "X");
		
		if (logger.isDebugEnabled()){logger.logDebug("Request Corebanking registerLog: " + request.toString());}
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking updateTransferStatus: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if(wProductsQueryResp.readValueParam("@o_mensaje")!=null && !wProductsQueryResp.readValueParam("@o_mensaje").equals("X"))
		{
			aBagSPJavaOrchestration.put("code_error", wProductsQueryResp.readValueParam("@o_codigo"));
			aBagSPJavaOrchestration.put("message_error", wProductsQueryResp.readValueParam("@o_mensaje"));
		}
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de updateTransferStatus");
		}
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		return null;
	}
	
	private void registerRemesasLogBd(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration, String estado) {
		IProcedureRequest request = new ProcedureRequestAS();
		LocalDateTime fechaHoraActual = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String fechaACtual = fechaHoraActual.format(formato);
        String movement_id_rem = null;
        
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en registerRemesasLogBd " + aRequest);
		}

		request.setSpName("cob_ahorros..sp_insert_log_rem_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		if(aBagSPJavaOrchestration.get("flowRty").equals(true))
			request.addInputParam("@t_rty", ICTSTypes.SQLCHAR, "S");
		
		request.addInputParam("@i_external_customer_id", ICTSTypes.SQLINT4, aRequest.readValueParam("@i_external_customer_id"));
		request.addInputParam("@i_account_number", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account_number"));
		request.addInputParam("@i_fecha_mod", ICTSTypes.SQLVARCHAR, fechaACtual);
		request.addInputParam("@i_modo", ICTSTypes.SQLVARCHAR, TIPO_OPE_LOG);
		request.addInputParam("@i_movement_id", ICTSTypes.SQLINT4, aRequest.readValueParam("@i_movement_id"));
		request.addInputParam("@i_reference_number", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_reference_number"));
		request.addInputParam("@i_state", ICTSTypes.SQLVARCHAR, estado);
	
		if(estado.equals("E"))
			movement_id_rem = "0";
		else if (estado.equals("D"))
			movement_id_rem = aBagSPJavaOrchestration.get("o_movement_id").toString();
		
		request.addInputParam("@i_movement_id_unlock", ICTSTypes.SQLVARCHAR, movement_id_rem);

		if (logger.isDebugEnabled()) {logger.logDebug("Request Corebanking registerRemesasLogBd: " + request.toString());}
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking registerRemesasLogBd DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de registerRemesasLogBd");
		}
	}
	
	public IProcedureResponse processResponseApi(IProcedureRequest aRequest, IProcedureResponse anOriginalProcedureRes, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled()){logger.logInfo("processResponseApi [INI] --->" );}
		
		String executionStatus = null;
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		Integer codeReturn = anOriginalProcedureRes.getReturnCode();
		
		if (logger.isInfoEnabled()){logger.logInfo("return code resp Conector --->" + codeReturn );}

		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));
		
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("movementId", ICTSTypes.SQLVARCHAR, 80));

		IResultSetHeader metaData3 = new ResultSetHeader();
		IResultSetData data3 = new ResultSetData();
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 10));
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));
		
		//if(!aBagSPJavaOrchestration.containsKey("flag_log")) {
			//registerLogBd(anOriginalProcedureRes, aBagSPJavaOrchestration);}
		
		if (codeReturn == 0) {
			Boolean flag = aBagSPJavaOrchestration.containsKey("o_success");
			
			if (logger.isDebugEnabled()) {
				logger.logDebug("return o_movement_id: " + aBagSPJavaOrchestration.get("o_movement_id"));
			}
		
			if(flag == true){
				if (logger.isDebugEnabled()) {logger.logDebug("Ending flow, processResponse success with code: ");}
				
				executionStatus = "CORRECT";
				if(aBagSPJavaOrchestration.get("flowRty").equals(false)){
					updateTrnStatus(anOriginalProcedureRes, aBagSPJavaOrchestration, executionStatus);
					registerRemesasLogBd(aRequest, aBagSPJavaOrchestration, "D");
					notifyUnlockCredit(aRequest, aBagSPJavaOrchestration);
				}
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, "true"));
				data.addRow(row);
				
				if(!aBagSPJavaOrchestration.get("o_movement_id").toString().equals("X")){
					IResultSetRow row2 = new ResultSetRow();
					row2.addRowData(1, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get("o_movement_id").toString()));
					data2.addRow(row2);
				}
				
				IResultSetRow row3 = new ResultSetRow();
				row3.addRowData(1, new ResultSetRowColumnData(false, "0"));
				row3.addRowData(2, new ResultSetRowColumnData(false, "Success"));
				data3.addRow(row3);
								
			}
			else{
				if (logger.isDebugEnabled()) {logger.logDebug("Ending flow, processResponse error");}
				
				executionStatus = "ERROR";
				if(aBagSPJavaOrchestration.get("flowRty").equals(false)){
					updateTrnStatus(anOriginalProcedureRes, aBagSPJavaOrchestration, executionStatus);
					
					if (aBagSPJavaOrchestration.containsKey("o_movement_id"))
						registerRemesasLogBd(aRequest, aBagSPJavaOrchestration, "E");
				}
				
				String message = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).isNull()?"Service execution error":anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).getValue();
				String success = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).isNull()?"false":anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue();
				String code = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).isNull()?"400218":anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).getValue();
				
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, success));
				data.addRow(row);
				
				IResultSetRow row2 = new ResultSetRow();
				row2.addRowData(1, new ResultSetRowColumnData(false, "0"));
				data2.addRow(row2);
				
				IResultSetRow row3 = new ResultSetRow();
				row3.addRowData(1, new ResultSetRowColumnData(false, code));
				row3.addRowData(2, new ResultSetRowColumnData(false, message));
				data3.addRow(row3);
			}
		} else {
			if (logger.isDebugEnabled()) {logger.logDebug("Ending flow, processResponse failed with code: ");}
			
			executionStatus = "ERROR";
			if(aBagSPJavaOrchestration.get("flowRty").equals(false)) {
				updateTrnStatus(anOriginalProcedureRes, aBagSPJavaOrchestration, executionStatus);
				registerRemesasLogBd(aRequest, aBagSPJavaOrchestration, "E");
			}
			IResultSetRow row = new ResultSetRow();
			row.addRowData(1, new ResultSetRowColumnData(false, "false"));
			data.addRow(row);
			
			IResultSetRow row2 = new ResultSetRow();
			row2.addRowData(1, new ResultSetRowColumnData(false, "0"));
			data2.addRow(row2);
			
			IResultSetRow row3 = new ResultSetRow();
			row3.addRowData(1, new ResultSetRowColumnData(false, codeReturn.toString()));
			row3.addRowData(2, new ResultSetRowColumnData(false, anOriginalProcedureRes.getMessage(1).getMessageText()));
			data3.addRow(row3);
		}
		
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		IResultSetBlock resultsetBlock3 = new ResultSetBlock(metaData3, data3);
		
		wProcedureResponse.addResponseBlock(resultsetBlock);
		wProcedureResponse.addResponseBlock(resultsetBlock2);
		wProcedureResponse.addResponseBlock(resultsetBlock3);
		
		return wProcedureResponse;		
	}
	
	private void notifyUnlockCredit(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
        IProcedureRequest request = new ProcedureRequestAS();

        if (logger.isInfoEnabled()) {
            logger.logInfo(CLASS_NAME + " Entrando en notifyUnlockCredit...");
        }
        
        request.setSpName("cob_bvirtual..sp_bv_enviar_notif_ib_api");

        request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
                IMultiBackEndResolverService.TARGET_LOCAL);
        request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
        
        request.addInputParam("@s_culture", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_culture"));
		request.addInputParam("@s_date", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_date"));
        
        request.addInputParam("@i_notificacion", ICTSTypes.SQLVARCHAR, CODIGO_NOTIFICACION);
        request.addInputParam("@i_servicio", ICTSTypes.SQLINTN, CHANNEL_REQUEST);
        request.addInputParam("@i_producto", ICTSTypes.SQLINTN, "4");
        request.addInputParam("@i_tipo", ICTSTypes.SQLVARCHAR, "M");
        request.addInputParam("@i_tipo_mensaje", ICTSTypes.SQLVARCHAR, "F");
        request.addInputParam("@i_print", ICTSTypes.SQLVARCHAR, "S");
        request.addInputParam("@i_ente_mis", ICTSTypes.SQLINTN, anOriginalRequest.readValueParam("@i_external_customer_id").toString());
        request.addInputParam("@i_ente_ib", ICTSTypes.SQLINTN, "0");
        request.addInputParam("@i_c1", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_account_number").toString());
        request.addInputParam("@i_v2", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_monto").toString());
        request.addInputParam("@i_s", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_movement_id").toString());
        request.addInputParam("@i_r", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_credit_concept").toString());
        
        request.addInputParam("@i_aux9", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_money_transmitter").toString());
        request.addInputParam("@i_aux10", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_reference_number").toString());
        request.addInputParam("@i_aux12", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_sender_name").toString());
        
        IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
        
        if (logger.isDebugEnabled()) {
            logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
        }

        if (logger.isInfoEnabled()) {
            logger.logInfo(CLASS_NAME + " Saliendo de notifyUnlockCredit...");
        }
    }
	
	public boolean isNumeric(String strNum) {
		Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

		if (strNum == null) {

			return false;
		}
		return pattern.matcher(strNum).matches();
	}

	public static boolean isGtmDateTime(String gtmDateTime) {
        try {
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
            
            dateTimeFormat.setLenient(false);
            dateTimeFormat.parse(gtmDateTime);
            
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

	public static boolean isDate(String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd");
            
            dateFormat.setLenient(false);
            dateFormat.parse(date);
            
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
	
	public static boolean isTime(String time) {
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
            
            timeFormat.setLenient(false);
            timeFormat.parse(time);
            
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
	
	public static boolean isExpDate(String expDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyMM");
            
            dateFormat.setLenient(false);
            dateFormat.parse(expDate);
            
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

	@Override
	protected void loadDataCustomer(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		
	}
}
