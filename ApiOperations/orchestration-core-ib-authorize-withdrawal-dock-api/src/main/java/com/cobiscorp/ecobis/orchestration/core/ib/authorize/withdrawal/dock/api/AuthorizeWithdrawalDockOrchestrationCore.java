/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.authorize.withdrawal.dock.api;

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
 * @author Sochoa
 * @since Jul 11, 2023
 * @version 1.0.0
 */
@Component(name = "AuthorizeWithdrawalDockOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "AuthorizeWithdrawalDockOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "AuthorizeWithdrawalDockOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_auth_withdrawal_dock_api")})
public class AuthorizeWithdrawalDockOrchestrationCore extends OfflineApiTemplate {
	
	private ILogger logger = (ILogger) this.getLogger();
	private static final String CLASS_NAME = "AuthorizeWithdrawalDockOrchestrationCore";
	protected static final String CHANNEL_REQUEST = "8";
	protected static final String MODE_OPERATION = "PYS";

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		if(logger.isDebugEnabled()) {
			logger.logDebug("Begin flow, AuthorizeWithdrawalDock starts...");
		}
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
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
		if(logger.isDebugEnabled()) {
			logger.logDebug("Response Online: " + serverStatus);
			logger.logDebug("Response flowRty: " + flowRty);
		}
		if (serverStatus != null && !serverStatus) {
			aBagSPJavaOrchestration.put("IsReentry", "S");
			if (!flowRty){
				if(logger.isDebugEnabled()) {
					logger.logDebug("evaluateExecuteReentry False");
				}
				anProcedureResponse = valDataLocal(anOriginalRequest, aBagSPJavaOrchestration);
				if(anProcedureResponse.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
					if(logger.isDebugEnabled()) {
						logger.logDebug("Code Error local" + anProcedureResponse.getResultSetRowColumnData(2, 1, 2));
					}
					anProcedureResponse = valTranDataCentralOff(anOriginalRequest, aBagSPJavaOrchestration);
					
					if(anProcedureResponse.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
						anProcedureResponse = saveReentry(anOriginalRequest, aBagSPJavaOrchestration);
						anProcedureResponse = executeOfflineWithdrawalCobis(anOriginalRequest, aBagSPJavaOrchestration);
						if(logger.isDebugEnabled()){
							logger.logDebug("Code Error central" + anProcedureResponse.getResultSetRowColumnData(2, 1, 2));
							logger.logDebug("executeOfflineWithdrawalCobis " + anProcedureResponse.toString() );
						}
					}
				}				
			}
			else{
				IProcedureResponse resp = Utils.returnException(40004, "NO EJECUTA REENTRY POR ESTAR EN OFFLINE!!!");
				if(logger.isDebugEnabled()){
					logger.logDebug("evaluateExecuteReentry True");
					logger.logDebug("Respose Exeption:: " + resp.toString());
				}
				return resp;
			}
		} else {
			aBagSPJavaOrchestration.put("IsReentry", "N");
			if(logger.isDebugEnabled()) {
				logger.logDebug("Res IsReentry:: " + "N");
			}
			anProcedureResponse = authorizeWithdrawalDock(anOriginalRequest, aBagSPJavaOrchestration);
		}
		
		return processResponseApi(anOriginalRequest, anProcedureResponse,aBagSPJavaOrchestration);
	}
	
	private IProcedureResponse executeOfflineWithdrawalCobis(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled()){
			logger.logInfo(CLASS_NAME + "Ejecutando executeOfflineWithdrawalCobis CORE COBIS" + anOriginalRequest);
			logger.logInfo("********** CAUSALES DE TRANSFERENCIA *************");
			logger.logInfo("********** CAUSA ORIGEN --->>> " + aBagSPJavaOrchestration.get("@o_causal"));
			
			logger.logInfo("********** CLIENTE CORE --->>> " + aBagSPJavaOrchestration.get("@o_externalCustomerId"));

		}
		//IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18306");

		anOriginalRequest.setSpName("cob_bvirtual..sp_bv_transaccion_off_api"); 

		anOriginalRequest.addInputParam("@i_type_response", ICTSTypes.SYBCHAR, "M");
		anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SYBINT4, "1");
		anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		anOriginalRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0:0:0:0:0:0:0:1");
		anOriginalRequest.addInputParam("@i_causa_org", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("@o_causal"));
		anOriginalRequest.addInputParam("@i_servicio_costo", ICTSTypes.SQLVARCHAR, "CTRT");
		anOriginalRequest.addInputParam("@s_servicio", ICTSTypes.SYBINT4, "8");
		anOriginalRequest.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("cta"));
		anOriginalRequest.addInputParam("@i_val", ICTSTypes.SQLMONEY4, anOriginalRequest.readValueParam("@i_source_value"));
		anOriginalRequest.addInputParam("@s_filial", ICTSTypes.SQLINT4, "1");		
		anOriginalRequest.addInputParam("@i_ente", ICTSTypes.SQLINT4, (String)aBagSPJavaOrchestration.get("ente"));
		anOriginalRequest.addInputParam("@s_cliente", ICTSTypes.SQLINT4, (String)aBagSPJavaOrchestration.get("ente_bv"));		
		anOriginalRequest.addInputParam("@i_mon", ICTSTypes.SQLINT2, "0");
		anOriginalRequest.addInputParam("@i_prod", ICTSTypes.SQLINT2, "4");
		anOriginalRequest.addInputParam("@t_rty", ICTSTypes.SYBCHAR, "S");		
		anOriginalRequest.addInputParam("@i_genera_clave", ICTSTypes.SYBCHAR, "N");
		anOriginalRequest.addInputParam("@i_tipo_notif", ICTSTypes.SYBCHAR, "F");
		anOriginalRequest.addInputParam("@i_graba_notif", ICTSTypes.SYBCHAR, "N");
		anOriginalRequest.addInputParam("@i_graba_log", ICTSTypes.SYBCHAR, "N");
		//anOriginalRequest.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("o_login"));
		anOriginalRequest.addInputParam("@i_bank_name", ICTSTypes.SQLVARCHAR, "CASHI");

		anOriginalRequest.addOutputParam("@o_ssn", ICTSTypes.SQLINTN, "0");

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Se envia Comission:" + anOriginalRequest.readValueParam("@i_comision"));
		anOriginalRequest.addInputParam("@i_comision", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_comision"));
		
		anOriginalRequest.addOutputParam("@o_fecha_tran", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXX");

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Data enviada a ejecutar api:" + anOriginalRequest);
		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta del Core api off:" + response.getProcedureResponseAsString());

		if(response.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			response.readValueParam("@o_fecha_tran");
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + "Parametro @o_fecha_tran: " + response.readValueParam("@o_fecha_tran"));
				logger.logInfo(CLASS_NAME + "Parametro @ssn: " + response.readValueFieldInHeader("ssn"));
			}

			if(response.readValueFieldInHeader("ssn")!=null){
				aBagSPJavaOrchestration.put("@o_ssn_host", response.readValueFieldInHeader("ssn"));
				aBagSPJavaOrchestration.put("authorizationCode", response.readValueParam("@o_ssn"));
				aBagSPJavaOrchestration.put("@o_ssn_branch", response.readValueFieldInHeader("ssn_branch"));
			}
		}
		else{
			aBagSPJavaOrchestration.put("code_error", response.getResultSetRowColumnData(2, 1, 1).getValue());
			aBagSPJavaOrchestration.put("message_error", response.getResultSetRowColumnData(2, 1, 2).getValue());
			if(logger.isDebugEnabled()) {
				logger.logDebug("Code Error" + aBagSPJavaOrchestration.get("code_error"));
			}
		}
		
		return response;
	}
	
	private IProcedureResponse authorizeWithdrawalDock(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en authorizeWithdrawalDock: ");
		}
		
		IProcedureResponse wAuthValDataLocal = new ProcedureResponseAS();
		wAuthValDataLocal = valDataLocal(aRequest, aBagSPJavaOrchestration);
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " code resp auth: " + wAuthValDataLocal.getResultSetRowColumnData(2, 1, 1).getValue());
		}
		if (wAuthValDataLocal.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			
			IProcedureResponse wAuthTrnDataCentral = new ProcedureResponseAS();
			wAuthTrnDataCentral = trnDataCentral(aRequest, aBagSPJavaOrchestration);
			
			return wAuthTrnDataCentral;
		}
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Response " + wAuthValDataLocal.toString());
			logger.logInfo(CLASS_NAME + " Saliendo de authorizeWithdrawalDock...");
		}

		return wAuthValDataLocal;
	}
	
	private IProcedureResponse valDataLocal(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en valDataLocal");
		}
		
		String s_amount = aRequest.readValueParam("@i_source_value");
		String b_amount = aRequest.readValueParam("@i_billing_value");
		String gtm_date_time = aRequest.readValueParam("@i_transmission_date_time_gmt");
		String date = aRequest.readValueParam("@i_terminal_date");
		String time = aRequest.readValueParam("@i_terminal_time");
		String exp_date = aRequest.readValueParam("@i_card_expiration_date");
		String establishment = aRequest.readValueParam("@i_establishment");
		
		
		if (s_amount != null && !s_amount.isEmpty() && !isNumeric(s_amount)) {
			s_amount = "";
		}
		
		if (b_amount != null && !b_amount.isEmpty() && !isNumeric(b_amount)) {
			b_amount = "";
		}
		
		if(gtm_date_time.equals("null")){
			gtm_date_time  = "";
		} else if (gtm_date_time != null && !gtm_date_time.isEmpty() && !isGtmDateTime(gtm_date_time)) {
			gtm_date_time = "I";
		}
		
		if(date.equals("null")){
			date = "";
		} else if (date != null && !date.isEmpty() && !isDate(date)) {
			date = "I";
		}
		
		if(time.equals("null")){
			time = "";
		} else if (time != null && !time.isEmpty() && !isTime(time)) {
			time = "I";
		}
		
		if(exp_date.equals("null")){
			exp_date = "";
		} else if (exp_date != null && !exp_date.isEmpty() && !isExpDate(exp_date)) {
			exp_date = "I";
		}
		
		
		request.setSpName("cob_atm..sp_bv_val_trn_atm_dock_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		//headers
		request.addInputParam("@x_legacy_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_legacy_id"));
		request.addInputParam("@x_client_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_client_id"));
		request.addInputParam("@x_uuid", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_uuid"));
		request.addInputParam("@x_apigw_api_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_apigw_api_id"));
		
		request.addInputParam("@i_card_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_card_id"));
		request.addInputParam("@i_person_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_person_id"));
		request.addInputParam("@i_account_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account_id"));
		request.addInputParam("@i_transmission_date_time_gmt", ICTSTypes.SQLVARCHAR, gtm_date_time);
		request.addInputParam("@i_date", ICTSTypes.SQLVARCHAR, date);
		request.addInputParam("@i_time", ICTSTypes.SQLVARCHAR, time);
		request.addInputParam("@i_mti", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_mti"));
		request.addInputParam("@i_processing_type", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_type"));
		request.addInputParam("@i_origin_account_type", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_origin_account_type"));
		request.addInputParam("@i_destiny_account_type", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_destiny_account_type"));
		request.addInputParam("@i_processing_code", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_processing_code"));
		request.addInputParam("@i_nsu", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_nsu"));
		request.addInputParam("@i_card_expiration_date", ICTSTypes.SQLVARCHAR, exp_date);
		request.addInputParam("@i_transaction_origin", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_transaction_origin"));
		request.addInputParam("@i_card_entry_code", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_card_entry_code"));
		request.addInputParam("@i_pin", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_pin"));
		request.addInputParam("@i_mode", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_mode"));
		request.addInputParam("@i_merchant_category_code", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_merchant_category_code"));
		request.addInputParam("@i_source_currency_code", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_source_currency_code"));
		request.addInputParam("@i_billing_currency_code", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_billing_currency_code"));
		request.addInputParam("@i_source_value", ICTSTypes.SQLMONEY, s_amount);
		request.addInputParam("@i_billing_value", ICTSTypes.SQLMONEY, b_amount);
		request.addInputParam("@i_terminal_code", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_terminal_code"));
		request.addInputParam("@i_establishment_code", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_establishment_code"));
		request.addInputParam("@i_establishment", ICTSTypes.SQLVARCHAR, establishment);
		request.addInputParam("@i_brand_response_code", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_brand_response_code"));
		request.addInputParam("@i_external_account_id", ICTSTypes.SQLDECIMAL, aRequest.readValueParam("@i_external_account_id"));
		request.addInputParam("@i_dest_asset_code", ICTSTypes.SQLDECIMAL, aRequest.readValueParam("@i_dest_asset_code"));
		request.addInputParam("@i_date_time_gmt", ICTSTypes.SQLDECIMAL, aRequest.readValueParam("@i_date_time_gmt"));
		request.addInputParam("@i_final_billing_value", ICTSTypes.SQLDECIMAL, aRequest.readValueParam("@i_final_billing_value"));
		request.addInputParam("@i_card_status", ICTSTypes.SQLDECIMAL, aRequest.readValueParam("@i_card_status"));
		request.addInputParam("@i_account_status", ICTSTypes.SQLDECIMAL, aRequest.readValueParam("@i_account_status"));
		request.addInputParam("@i_is_only_supports_purchase", ICTSTypes.SQLDECIMAL, aRequest.readValueParam("@i_is_only_supports_purchase"));
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "WITHDRAWAL");
		request.addInputParam("@i_acquirer_country_code", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_acquirer_country_code"));
		if(aBagSPJavaOrchestration.get("IsReentry").equals("S"))
			request.addInputParam("@i_reentry", ICTSTypes.SQLCHAR, "S");
		if(aBagSPJavaOrchestration.get("flowRty").equals(true))
			request.addInputParam("@i_val_uuid", ICTSTypes.SQLCHAR, "S");
		request.addOutputParam("@o_ente", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_ente_bv", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_cta", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_seq", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_reentry", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_type_transaction", ICTSTypes.SQLVARCHAR, "X");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("ente es " +  wProductsQueryResp.readValueParam("@o_ente"));
			logger.logDebug("ente_bv es " +  wProductsQueryResp.readValueParam("@o_ente_bv"));
			logger.logDebug("cta es " +  wProductsQueryResp.readValueParam("@o_cta"));
			logger.logDebug("secuencial es " +  wProductsQueryResp.readValueParam("@o_seq"));
			logger.logDebug("reentry es " +  wProductsQueryResp.readValueParam("@o_reentry"));
		}
		
		aBagSPJavaOrchestration.put("amount", aRequest.readValueParam("@i_source_value"));
		aBagSPJavaOrchestration.put("ente", wProductsQueryResp.readValueParam("@o_ente"));
		aBagSPJavaOrchestration.put("ente_bv", wProductsQueryResp.readValueParam("@o_ente_bv"));
		aBagSPJavaOrchestration.put("cta", wProductsQueryResp.readValueParam("@o_cta"));
		aBagSPJavaOrchestration.put("seq", wProductsQueryResp.readValueParam("@o_seq"));
		aBagSPJavaOrchestration.put("reentry", wProductsQueryResp.readValueParam("@o_reentry"));
		aBagSPJavaOrchestration.put("o_type_transaction", wProductsQueryResp.readValueParam("@o_type_transaction"));
		
		if(!wProductsQueryResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			aBagSPJavaOrchestration.put("code_error", wProductsQueryResp.getResultSetRowColumnData(2, 1, 1).getValue());
			aBagSPJavaOrchestration.put("message_error", wProductsQueryResp.getResultSetRowColumnData(2, 1, 2).getValue());
			if(logger.isDebugEnabled()) {
				logger.logDebug("Code Error local" + aBagSPJavaOrchestration.get("code_error"));
			}
		}
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking valDataLocal: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de valDataLocal");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse trnDataCentral(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = initProcedureRequest(aRequest);

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en trnDataCentral");
		}

		request.setSpName("cob_cuentas..sp_retiro_atm_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		if(aBagSPJavaOrchestration.get("flowRty").equals(true))
			request.addInputParam("@t_rty", ICTSTypes.SQLCHAR, "S");
		
		if(aBagSPJavaOrchestration.get("REENTRY_SSN")!=null){
			request.setValueFieldInHeader(ICOBISTS.HEADER_SSN, (String)aBagSPJavaOrchestration.get("REENTRY_SSN"));
			request.addInputParam("@i_find_json", ICTSTypes.SQLVARCHAR, "Y");
		}
		else{
			request.addInputParam("@i_request_trn", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_json_req"));
		}
		request.removeParam("@i_json_req");
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, aRequest.readValueParam("@t_trn"));
		
		request.addInputParam("@i_cta_deb", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("cta"));
		request.addInputParam("@i_mon_deb", ICTSTypes.SQLINT4, "0");
		request.addInputParam("@i_prod_deb", ICTSTypes.SQLINT4, "4");
		request.addInputParam("@i_val_deb", ICTSTypes.SQLMONEY, (String) aBagSPJavaOrchestration.get("amount")); 
		request.addInputParam("@i_tarjeta_mascara", ICTSTypes.SQLVARCHAR, "");
		request.addInputParam("@i_cliente", ICTSTypes.SQLINTN, (String) aBagSPJavaOrchestration.get("ente"));
		request.addInputParam("@i_comision", ICTSTypes.SQLMONEY, "0"); 
		request.addInputParam("@i_srv", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_srv"));
		request.addInputParam("@i_ofi", ICTSTypes.SQLINTN, aRequest.readValueParam("@s_ofi"));
		request.addInputParam("@i_user", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_user"));
		request.addInputParam("@i_canal", ICTSTypes.SQLINTN, "0");
		request.addInputParam("@i_uuid", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_uuid"));
		request.addInputParam("@i_request_trn", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_json_req"));
		request.addInputParam("@t_trn", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@t_trn"));
		request.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_srv"));
		request.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_user"));
		request.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_term"));
		request.addInputParam("@i_origen", ICTSTypes.SQLVARCHAR, "D");
		request.addInputParam("@i_processing_type", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("o_type_transaction"));

		request.addOutputParam("@o_ssn_host", ICTSTypes.SQLINTN, "0");
		request.addOutputParam("@o_ssn_branch", ICTSTypes.SQLINTN, "0");
		request.addOutputParam("@o_causal", ICTSTypes.SQLINTN, "0");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("ssn host es " +  wProductsQueryResp.readValueParam("@o_ssn_host"));
			logger.logDebug("ssn branch es " +  wProductsQueryResp.readValueParam("@o_ssn_branch"));
		}
		
		aBagSPJavaOrchestration.put("@o_causal", wProductsQueryResp.readValueParam("@o_causal"));
		aBagSPJavaOrchestration.put("@o_ssn_host", wProductsQueryResp.readValueParam("@o_ssn_host"));		
		aBagSPJavaOrchestration.put("authorizationCode", wProductsQueryResp.getResultSetRowColumnData(3, 1, 1).isNull()?"0":wProductsQueryResp.getResultSetRowColumnData(3, 1, 1).getValue());
		aBagSPJavaOrchestration.put("@o_ssn_branch", wProductsQueryResp.readValueParam("@o_ssn_branch"));
		
		if(wProductsQueryResp.readValueParam("@o_ssn_host").equals("0")){
			aBagSPJavaOrchestration.put("code_error", wProductsQueryResp.getResultSetRowColumnData(2, 1, 1).getValue());
			aBagSPJavaOrchestration.put("message_error", wProductsQueryResp.getResultSetRowColumnData(2, 1, 2).getValue());
			if(logger.isDebugEnabled()) {
				logger.logDebug("Code Error" + aBagSPJavaOrchestration.get("code_error"));
			}
		}
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de trnDataCentral");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse valTranDataCentralOff(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en trnDataCentral");
		}

		request.setSpName("cob_cuentas..sp_val_central_data_off");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_cta_deb", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("cta").toString());
		request.addInputParam("@i_mon_deb", ICTSTypes.SQLINTN, "0");
		request.addInputParam("@i_prod_deb", ICTSTypes.SQLINTN, "4");
		request.addInputParam("@i_val_deb", ICTSTypes.SQLMONEY, aBagSPJavaOrchestration.get("amount").toString()); 
		request.addInputParam("@i_tarjeta_mascara", ICTSTypes.SQLVARCHAR, "");
		request.addInputParam("@i_cliente", ICTSTypes.SQLINTN, aBagSPJavaOrchestration.get("ente").toString());
		request.addInputParam("@i_comision", ICTSTypes.SQLMONEY, "0"); 
		request.addInputParam("@i_srv", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_srv"));
		request.addInputParam("@i_ofi", ICTSTypes.SQLINTN, aRequest.readValueParam("@s_ofi"));
		request.addInputParam("@i_user", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_user"));
		request.addInputParam("@i_canal", ICTSTypes.SQLINTN, "0");
		request.addInputParam("@i_uuid", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_uuid"));
		request.addInputParam("@i_request_trn", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_json_req"));
		request.addInputParam("@i_origen", ICTSTypes.SQLVARCHAR, "D");
		request.addInputParam("@t_trn", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@t_trn"));
		request.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_srv"));
		request.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_user"));
		request.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_term"));
		
		request.addOutputParam("@o_causal", ICTSTypes.SQLINTN, "0");
		request.addOutputParam("@o_ssn_host", ICTSTypes.SQLINTN, "0");
		request.addOutputParam("@o_ssn_branch", ICTSTypes.SQLINTN, "0");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		aBagSPJavaOrchestration.put("@o_causal", wProductsQueryResp.readValueParam("@o_causal"));
		wProductsQueryResp.setReturnCode(0);
		
		if (!wProductsQueryResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			aBagSPJavaOrchestration.put("code_error", wProductsQueryResp.getResultSetRowColumnData(2, 1, 1).getValue());
			aBagSPJavaOrchestration.put("message_error", wProductsQueryResp.getResultSetRowColumnData(2, 1, 2).getValue());
			if(logger.isDebugEnabled()) {
				logger.logDebug("Code Error" + aBagSPJavaOrchestration.get("code_error"));
			}
		}
				
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking trnValDataCentralOff: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de trnValDataCentralOff");
		}

		return wProductsQueryResp;
	}
	
	private void registerLogBd(IProcedureRequest aRequest, IProcedureResponse aResponse, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en registerLogBd");
		}

		request.setSpName("cob_ahorros..sp_insert_data_trn_aut");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		LocalDateTime fechaHoraActual = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String fechaActual = fechaHoraActual.format(formato);
		 
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aBagSPJavaOrchestration.get("ente").toString());
		request.addInputParam("@i_fecha_reg", ICTSTypes.SQLVARCHAR, fechaActual);
		request.addInputParam("@i_fecha_mod", ICTSTypes.SQLVARCHAR, fechaActual);
		request.addInputParam("@i_modo", ICTSTypes.SQLVARCHAR, "RTA");

		request.addInputParam("@i_external_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_uuid"));
		request.addInputParam("@i_request_trn", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_json_req"));
		request.addInputParam("@i_transacion", ICTSTypes.SQLINT4, (String) aBagSPJavaOrchestration.get("@o_ssn_host"));
		request.addInputParam("@i_reverse_uuid", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_origin_uuid"));
		request.addInputParam("@i_transaction_type", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_mti"));
		request.addInputParam("@i_estado", ICTSTypes.SQLVARCHAR, "V");
		
		request.addOutputParam("@o_seq_tran", ICTSTypes.SQLINTN, "0");
		if(logger.isDebugEnabled()) {
			logger.logDebug("Request Corebanking registerLog: " + request.toString());
		}
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("secuencial es " +  wProductsQueryResp.readValueParam("@o_seq_tran"));
		}
		
		aBagSPJavaOrchestration.put("@o_seq_tran", wProductsQueryResp.readValueParam("@o_seq_tran"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking registerLog: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de registerLogBd");
		}
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
		request.addInputParam("@i_movementId", ICTSTypes.SQLINTN, aBagSPJavaOrchestration.containsKey("@o_ssn_host")?aBagSPJavaOrchestration.get("@o_ssn_host").toString():null);
		
		request.addInputParam("@i_error", ICTSTypes.SQLINTN, aBagSPJavaOrchestration.containsKey("code_error")?aBagSPJavaOrchestration.get("code_error").toString():null);
		request.addOutputParam("@o_codigo", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_mensaje", ICTSTypes.SQLVARCHAR, "X");
		if(logger.isDebugEnabled()) {
			logger.logDebug("Request Corebanking registerLog: " + request.toString());
		}
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
	
	public IProcedureResponse processResponseApi(IProcedureRequest aRequest, IProcedureResponse anOriginalProcedureRes, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled()) {
			logger.logInfo("processResponseApi [INI] --->");
		}
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		Integer codeReturn = anOriginalProcedureRes.getReturnCode();
		String executionStatus = null;
		String authorizationCode = null;
		if (logger.isInfoEnabled()) {
			logger.logInfo("return code resp--->" + codeReturn);
		}
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("approved_value", ICTSTypes.SQLMONEY4, 50));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("settlement_value", ICTSTypes.SQLMONEY4, 50));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("cardholder_billing_value", ICTSTypes.SQLVARCHAR, 50));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("response", ICTSTypes.SQLVARCHAR, 1500));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("reason", ICTSTypes.SQLBIT, 100));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("available_limit", ICTSTypes.SQLMONEY4, 25));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("authorizationCode", ICTSTypes.SQLINT4, 6));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("seq", ICTSTypes.SQLVARCHAR, 20));
		
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));

		
		if (codeReturn == 0) {		
			
			if(aBagSPJavaOrchestration.containsKey("code_error")){
				if(logger.isDebugEnabled()) {
					logger.logDebug("Ending flow, processResponse error with code: " + aBagSPJavaOrchestration.get("code_error"));
				}
				executionStatus = "ERROR";
				if(aBagSPJavaOrchestration.get("flowRty").equals(false))
					updateTrnStatus(anOriginalProcedureRes, aBagSPJavaOrchestration, executionStatus);
				
				IResultSetRow row = new ResultSetRow();
	
				row.addRowData(1, new ResultSetRowColumnData(false, "0"));
				row.addRowData(2, new ResultSetRowColumnData(false, "0"));
				row.addRowData(3, new ResultSetRowColumnData(false, "0"));
				row.addRowData(4, new ResultSetRowColumnData(false, (String) aBagSPJavaOrchestration.get("message_error")));
				row.addRowData(5, new ResultSetRowColumnData(false, (String) aBagSPJavaOrchestration.get("code_error")));
				row.addRowData(6, new ResultSetRowColumnData(false, "0"));
				row.addRowData(7, new ResultSetRowColumnData(false, null));
				row.addRowData(8, new ResultSetRowColumnData(false, null));
				
				data.addRow(row);
				
			} else {
				if(logger.isDebugEnabled()) {
					logger.logDebug("Ending flow, processResponse successful...");
					logger.logDebug("Numero de autorizacion OK JC");
					logger.logDebug(aBagSPJavaOrchestration.get("authorizationCode"));
				}
				
				executionStatus = "CORRECT";
				authorizationCode = aBagSPJavaOrchestration.containsKey("authorizationCode")?(String)aBagSPJavaOrchestration.get("authorizationCode"):"0";
				
				notifyWithdrawalDock(aRequest, aBagSPJavaOrchestration);
				
				if(aBagSPJavaOrchestration.get("flowRty").equals(false)){
					registerLogBd(aRequest, anOriginalProcedureRes, aBagSPJavaOrchestration);
					updateTrnStatus(anOriginalProcedureRes, aBagSPJavaOrchestration, executionStatus);
				}
				
				IResultSetRow row = new ResultSetRow();
				
				row.addRowData(1, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get("amount").toString()));
				row.addRowData(2, new ResultSetRowColumnData(false, "0"));
				row.addRowData(3, new ResultSetRowColumnData(false, "0"));
				row.addRowData(4, new ResultSetRowColumnData(false, "APPROVED"));
				row.addRowData(5, new ResultSetRowColumnData(false, "0"));  // aBagSPJavaOrchestration.get("@o_ssn_host").toString())
				row.addRowData(6, new ResultSetRowColumnData(false, "0"));
				row.addRowData(7, new ResultSetRowColumnData(false, authorizationCode));
				row.addRowData(8, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.containsKey("@o_seq_tran")?(String)aBagSPJavaOrchestration.get("@o_seq_tran"):"0"));
				
				// Se agrega el AuthorizationCode al request para Webhook
				aRequest.addInputParam("@i_authorization_code", ICTSTypes.SQLVARCHAR, authorizationCode);
				
				registerTransactionSuccess("Authorize Withdrawal Dock", "DOCK", aRequest, 
										(String)aBagSPJavaOrchestration.get("@o_ssn_host"), 
										(String)aBagSPJavaOrchestration.get("@o_causal"),
										aBagSPJavaOrchestration.get("ente").toString()
										);
				
				data.addRow(row);	
			}
			
		} else {
			if(logger.isDebugEnabled()) {
				logger.logDebug("Ending flow, processResponse failed with code: ");
			}
			executionStatus = "ERROR";
			if(aBagSPJavaOrchestration.get("flowRty").equals(false))
				updateTrnStatus(anOriginalProcedureRes, aBagSPJavaOrchestration, executionStatus);
			
			String codeError = aBagSPJavaOrchestration.containsKey("code_error")?aBagSPJavaOrchestration.get("code_error").toString(): codeReturn.toString();
			String mesageError = aBagSPJavaOrchestration.containsKey("message_error")?aBagSPJavaOrchestration.get("message_error").toString():"SYSTEM_ERROR";
			
			IResultSetRow row = new ResultSetRow();
			
			row.addRowData(1, new ResultSetRowColumnData(false, "0"));
			row.addRowData(2, new ResultSetRowColumnData(false, "0"));
			row.addRowData(3, new ResultSetRowColumnData(false, "0"));
			row.addRowData(4, new ResultSetRowColumnData(false, mesageError));
			row.addRowData(5, new ResultSetRowColumnData(false, codeError));
			row.addRowData(6, new ResultSetRowColumnData(false, "0"));
			row.addRowData(7, new ResultSetRowColumnData(false, null));
			row.addRowData(8, new ResultSetRowColumnData(false, null));
			
			data.addRow(row);
		}
		
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		
		wProcedureResponse.addResponseBlock(resultsetBlock);
		wProcedureResponse.addResponseBlock(resultsetBlock2);
		
		return wProcedureResponse;		
	}

	private void notifyWithdrawalDock(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en notifyWithdrawalDock...");
		}

		request.setSpName("cob_bvirtual..sp_bv_enviar_notif_ib_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		request.addInputParam("@s_culture", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_culture"));
		request.addInputParam("@s_date", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_date"));

		request.addInputParam("@i_titulo", ICTSTypes.SQLVARCHAR, "Withdrawal Dock VI Card");
		request.addInputParam("@i_notificacion", ICTSTypes.SQLVARCHAR, "N42");
		request.addInputParam("@i_servicio", ICTSTypes.SQLINTN, "8");
		request.addInputParam("@i_producto", ICTSTypes.SQLINTN, "18");
		request.addInputParam("@i_tipo", ICTSTypes.SQLVARCHAR, "M");
		request.addInputParam("@i_tipo_mensaje", ICTSTypes.SQLVARCHAR, "F");
		request.addInputParam("@i_print", ICTSTypes.SQLVARCHAR, "S");
		request.addInputParam("@i_r", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("authorizationCode").toString());
		request.addInputParam("@i_aux1", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_establishment"));
		request.addInputParam("@i_ente_mis", ICTSTypes.SQLINTN, aBagSPJavaOrchestration.get("ente").toString());
		request.addInputParam("@i_ente_ib", ICTSTypes.SQLINTN, "0");
		request.addInputParam("@i_c1", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("cta").toString());
		request.addInputParam("@i_v2", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("amount").toString());
		request.addInputParam("@i_s", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("@o_ssn_host").toString());

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de notifyWithdrawalDock...");
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
