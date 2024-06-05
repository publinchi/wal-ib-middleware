/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.authorize.reversal.api;

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
 * @since Jun 30, 2023
 * @version 1.0.0
 */
@Component(name = "AuthorizeReversalDockOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "AuthorizeReversalDockOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "AuthorizeReversalDockOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_auth_reversal_dock_api")})
public class AuthorizeReversalDockOrchestrationCore extends OfflineApiTemplate {
	
	private ILogger logger = (ILogger) this.getLogger();
	private static final String CLASS_NAME = "AuthorizeReversalDockOrchestrationCore";

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		logger.logDebug("Begin flow, AuthorizeReversal starts...");		
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
		
		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();
		Boolean flowRty = evaluateExecuteReentry(anOriginalRequest);
		aBagSPJavaOrchestration.put("flowRty", flowRty);
		logger.logDebug("Response Online: " + serverStatus);
		logger.logDebug("Response flowRty: " + flowRty);
		if (serverStatus != null && !serverStatus) {
			aBagSPJavaOrchestration.put("IsReentry", "S");
			if (!flowRty){
				logger.logDebug("evaluateExecuteReentry False");
				
				anProcedureResponse = valDataLocal(anOriginalRequest, aBagSPJavaOrchestration);
				if(anProcedureResponse.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
					
					logger.logDebug("Code Error local" + anProcedureResponse.getResultSetRowColumnData(2, 1, 2));
					anProcedureResponse = valTranDataCentralOff(anOriginalRequest, aBagSPJavaOrchestration);		
					
					if(anProcedureResponse.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
						logger.logDebug("Code Error central" + anProcedureResponse.getResultSetRowColumnData(2, 1, 2));
						anProcedureResponse = saveReentry(anOriginalRequest, aBagSPJavaOrchestration);
						logger.logDebug("executeOfflineReversalCobis " + anProcedureResponse.toString() );
						anProcedureResponse = executeOfflineReversalCobis(anOriginalRequest, aBagSPJavaOrchestration);if(anProcedureResponse.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
							
						}	
					}
				}				
			}
			else{
				logger.logDebug("evaluateExecuteReentry True");
				IProcedureResponse resp = Utils.returnException(40004, "NO EJECUTA REENTRY POR ESTAR EN OFFLINE!!!");
				logger.logDebug("Respose Exeption:: " + resp.toString());
				return resp;
			}
		} else {
			aBagSPJavaOrchestration.put("IsReentry", "N");
			logger.logDebug("Res IsReentry:: " + "N");
			anProcedureResponse = authorizeReversalDock(anOriginalRequest, aBagSPJavaOrchestration);
		}
		
		return processResponseApi(anOriginalRequest, anProcedureResponse, aBagSPJavaOrchestration);
	}
	
	private IProcedureResponse valTranDataCentralOff(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en valTranDataCentralOff");
		}
		
		request.setSpName("cob_cuentas..sp_val_central_data_off");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_original_transaction_data_institution_code", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_original_transaction_data_institution_code"));
		request.addInputParam("@i_original_transaction_data_institutiion_name", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_original_transaction_data_institutiion_name"));
		request.addInputParam("@i_original_transaction_data_retrieval_reference_number", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_original_transaction_data_retrieval_reference_number"));
		request.addInputParam("@i_nsu", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_original_transaction_data_nsu"));
		
		request.addInputParam("@i_cta_deb", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("@o_accountNumber"));
		request.addInputParam("@i_mon_deb", ICTSTypes.SQLINTN, "0");
		request.addInputParam("@i_prod_deb", ICTSTypes.SQLINTN, "4");
		request.addInputParam("@i_val_deb", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_values_source_value")); 
		request.addInputParam("@i_tarjeta_mascara", ICTSTypes.SQLVARCHAR, "");
		request.addInputParam("@i_cliente", ICTSTypes.SQLINTN, (String) aBagSPJavaOrchestration.get("@o_externalCustomerId"));
		request.addInputParam("@i_comision", ICTSTypes.SQLMONEY, "0"); 
		request.addInputParam("@i_srv", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_srv"));
		request.addInputParam("@i_ofi", ICTSTypes.SQLINTN, aRequest.readValueParam("@s_ofi"));
		request.addInputParam("@i_user", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_user"));
		request.addInputParam("@i_canal", ICTSTypes.SQLINTN, "0");
		request.addInputParam("@i_uuid", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_uuid"));
		request.addInputParam("@i_origin_uuid", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_original_transaction_data_transaction_uuid"));
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
			
			logger.logDebug("Code Error" +aBagSPJavaOrchestration.get("code_error"));
		}
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking AuthRever: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de valTranDataCentralOff");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse executeOfflineReversalCobis(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled()){
			logger.logInfo(CLASS_NAME + "Ejecutando executeOfflineReversalCobis CORE COBIS" + anOriginalRequest);
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
		anOriginalRequest.addInputParam("@i_causa_des", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("@o_causal"));
		anOriginalRequest.addInputParam("@i_servicio_costo", ICTSTypes.SQLVARCHAR, "CTRT");
		anOriginalRequest.addInputParam("@s_servicio", ICTSTypes.SYBINT4, "8");
		anOriginalRequest.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("@o_accountNumber"));
		anOriginalRequest.addInputParam("@i_val", ICTSTypes.SQLMONEY4, anOriginalRequest.readValueParam("@i_values_source_value"));
		anOriginalRequest.addInputParam("@s_filial", ICTSTypes.SQLINT4, "1");		
		anOriginalRequest.addInputParam("@i_ente", ICTSTypes.SQLINT4, (String)aBagSPJavaOrchestration.get("@o_externalCustomerId"));
		anOriginalRequest.addInputParam("@s_cliente", ICTSTypes.SQLINT4, (String)aBagSPJavaOrchestration.get("@o_ente_bv"));		
		anOriginalRequest.addInputParam("@i_mon_des", ICTSTypes.SQLINT2, (String)aBagSPJavaOrchestration.get("@o_mon"));
		anOriginalRequest.addInputParam("@i_prod_des", ICTSTypes.SQLINT2, (String)aBagSPJavaOrchestration.get("@o_prod"));
		anOriginalRequest.addInputParam("@t_rty", ICTSTypes.SYBCHAR, "S");		
		anOriginalRequest.addInputParam("@i_genera_clave", ICTSTypes.SYBCHAR, "N");
		anOriginalRequest.addInputParam("@i_tipo_notif", ICTSTypes.SYBCHAR, "F");
		anOriginalRequest.addInputParam("@i_graba_notif", ICTSTypes.SYBCHAR, "N");
		anOriginalRequest.addInputParam("@i_graba_log", ICTSTypes.SYBCHAR, "N");
		//anOriginalRequest.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("o_login"));
		anOriginalRequest.addInputParam("@i_bank_name", ICTSTypes.SQLVARCHAR, "CASHI");

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Se envia Comission:" + anOriginalRequest.readValueParam("@i_comision"));
		anOriginalRequest.addInputParam("@i_comision", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_comision"));
		
		
		anOriginalRequest.addOutputParam("@o_fecha_tran", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXX");

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Data enviada a ejecutar api:" + anOriginalRequest);
		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta del Core api:" + response.getProcedureResponseAsString());

		if(response.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			logger.logInfo(CLASS_NAME + "Parametro @o_fecha_tran: " + response.readValueParam("@o_fecha_tran"));
			response.readValueParam("@o_fecha_tran");
			
			logger.logInfo(CLASS_NAME + "Parametro @ssn: " + response.readValueFieldInHeader("ssn"));
			if(response.readValueFieldInHeader("ssn")!=null){
				aBagSPJavaOrchestration.put("@o_ssn_host", response.readValueFieldInHeader("ssn"));
				aBagSPJavaOrchestration.put("authorizationCode", response.readValueParam("@o_ssn"));
				aBagSPJavaOrchestration.put("@o_ssn_branch", response.readValueFieldInHeader("ssn_branch"));
			}
		}
		else{
			aBagSPJavaOrchestration.put("code_error", response.getResultSetRowColumnData(2, 1, 1).getValue());
			aBagSPJavaOrchestration.put("message_error", response.getResultSetRowColumnData(2, 1, 2).getValue());
				
			logger.logDebug("Code Error" +aBagSPJavaOrchestration.get("code_error"));
		}
		
		return response;
	}
	
	private IProcedureResponse authorizeReversalDock(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en authorizeReversalDock: ");
		}
		
		IProcedureResponse wAuthValDataLocal = new ProcedureResponseAS();
		wAuthValDataLocal = valDataLocal(aRequest, aBagSPJavaOrchestration);
		
		logger.logInfo(CLASS_NAME + " code resp auth: " + wAuthValDataLocal.getResultSetRowColumnData(2, 1, 1).getValue());
		if (wAuthValDataLocal.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			
			IProcedureResponse wAuthTrnDataCentral = new ProcedureResponseAS();
			wAuthTrnDataCentral = trnDataCentral(aRequest, aBagSPJavaOrchestration);
			
			return wAuthTrnDataCentral;
		}
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Response " + wAuthValDataLocal.toString());
			logger.logInfo(CLASS_NAME + " Saliendo de authorizeReversal...");
		}

		return wAuthValDataLocal;
	}
	
	private IProcedureResponse valDataLocal(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en valDataLocal");
		}
		
		String values_1 = aRequest.readValueParam("@i_values_source_currency_code");
		String values_2 = aRequest.readValueParam("@i_values_billing_currency_code");
		String values_3 = aRequest.readValueParam("@i_values_source_value");
		String values_4 = aRequest.readValueParam("@i_values_billing_value");
		String gtm_date_time = aRequest.readValueParam("@i_transmission_date_time");
		String date = aRequest.readValueParam("@i_terminal_date");
		String time = aRequest.readValueParam("@i_terminal_time");
		String exp_date = aRequest.readValueParam("@i_card_expiration_date");
		String original_gtm_date_time = aRequest.readValueParam("@i_original_transaction_data_transmission_date_time_gmt");
		String pos_id = aRequest.readValueParam("@i_pos_id");
		String cashier = aRequest.readValueParam("@i_cashier");
		String transaction = aRequest.readValueParam("@i_transaction");
		String pinpad = aRequest.readValueParam("@i_pinpad");
	    String dest_asset_code = aRequest.readValueParam("@i_exchange_rate_dest_asset_code");
		String date_time_gmt = aRequest.readValueParam("@i_exchange_rate_date_time_gmt");
		String final_billing_value = aRequest.readValueParam("@i_exchange_rate_final_billing_value");	
		
		if (values_3 != null && !values_3.isEmpty() && !isNumeric(values_3)) {
			values_3 = "";
		}
		
		if (values_4 != null && !values_4.isEmpty() && !isNumeric(values_4)) {
			values_4 = "";
		}
		
		if(gtm_date_time.equals("null")){

            gtm_date_time  = "";

        } else if (gtm_date_time != null && !gtm_date_time.isEmpty() && !isGtmDateTime(gtm_date_time)) {

            gtm_date_time = "I";

        }
		
		if(date.equals("null")){

			date  = "";

        } else if (date != null && !date.isEmpty() && !isDate(date)) {

        	date = "I";

        }
		
		if(time.equals("null")){

			time  = "";

        } else if (time != null && !time.isEmpty() && !isTime(time)) {

        	time = "I";

        }
		
		if(exp_date.equals("null")){

			exp_date  = "";

        } else if (exp_date != null && !exp_date.isEmpty() && !isExpDate(exp_date)) {

        	exp_date = "I";

        }
		
		if(original_gtm_date_time.equals("null")){

			original_gtm_date_time  = "";

        } else if (original_gtm_date_time != null && !original_gtm_date_time.isEmpty() && !isGtmDateTime(original_gtm_date_time)) {

        	original_gtm_date_time = "I";

        }
		
		if (pos_id == null || pos_id.trim().isEmpty()) {
			pos_id = "E";
		}
		
		if (cashier == null || cashier.trim().isEmpty()) {
			cashier = "E";
		}
		
		if (transaction == null || transaction.trim().isEmpty()) {
			transaction = "E";
		}
		
		if (pinpad == null || pinpad.trim().isEmpty()) {
			pinpad = "E";
		}

		if (dest_asset_code == null || dest_asset_code.trim().isEmpty()) {
			dest_asset_code = "E";
		}

		if (date_time_gmt == null || date_time_gmt.trim().isEmpty()) {
			date_time_gmt = "E";
		}
		
		if (final_billing_value == null || final_billing_value.trim().isEmpty()) {
			final_billing_value = "E";
		}
		
		request.setSpName("cob_atm..sp_bv_valida_dock_reversal_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_transmission_date_time", ICTSTypes.SQLVARCHAR, gtm_date_time);
		request.addInputParam("@i_terminal_date", ICTSTypes.SQLVARCHAR, date);
		request.addInputParam("@i_terminal_time", ICTSTypes.SQLVARCHAR, time);
		request.addInputParam("@i_card_expiration_date", ICTSTypes.SQLVARCHAR, exp_date);
		request.addInputParam("@i_original_transaction_data_transmission_date_time_gmt", ICTSTypes.SQLVARCHAR, original_gtm_date_time);
		request.addInputParam("@i_person_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_person_id"));
		request.addInputParam("@i_account_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account_id"));
		request.addInputParam("@i_card_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_card_id"));
		request.addInputParam("@i_mti", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_mti"));
		request.addInputParam("@i_processing_type", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_processing_type"));
		request.addInputParam("@i_processing_origin_account_type", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_processing_origin_account_type"));
		request.addInputParam("@i_processing_destiny_account_type", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_processing_destiny_account_type"));
		request.addInputParam("@i_processing_code", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_processing_code"));
		request.addInputParam("@i_nsu", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_nsu"));
		request.addInputParam("@i_authorization_code", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_authorization_code"));
		request.addInputParam("@i_transaction_origin", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_transaction_origin"));
		request.addInputParam("@i_card_entry_code", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_card_entry_code"));
		request.addInputParam("@i_card_entry_pin", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_card_entry_pin"));
		request.addInputParam("@i_card_entry_mode", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_card_entry_mode"));
		request.addInputParam("@i_merchant_category_code", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_mechant_category_code"));
		request.addInputParam("@i_values_source_currency_code", ICTSTypes.SQLMONEY, values_1);
		request.addInputParam("@i_values_billing_currency_code", ICTSTypes.SQLMONEY, values_2);
		request.addInputParam("@i_values_source_value", ICTSTypes.SQLMONEY, values_3);
		request.addInputParam("@i_values_billing_value", ICTSTypes.SQLMONEY, values_4);
		request.addInputParam("@i_terminal_code", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_terminal_code"));
		request.addInputParam("@i_establishment_code", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_establishment_code"));
		// request.addInputParam("@i_brand_response_code", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_brand_response_code"));
		request.addInputParam("@i_affiliation_number", ICTSTypes.SQLDECIMAL, aRequest.readValueParam("@i_affiliation_number"));
		request.addInputParam("@i_store_number", ICTSTypes.SQLDECIMAL, aRequest.readValueParam("@i_store_number"));
		request.addInputParam("@i_pos_id", ICTSTypes.SQLVARCHAR, pos_id);
		request.addInputParam("@i_cashier", ICTSTypes.SQLVARCHAR, cashier);
		request.addInputParam("@i_transaction", ICTSTypes.SQLVARCHAR, transaction);
		request.addInputParam("@i_pinpad", ICTSTypes.SQLVARCHAR, pinpad);
		request.addInputParam("@i_original_transaction_data_transaction_uuid", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_original_transaction_data_transaction_uuid"));
		request.addInputParam("@i_original_transaction_data_nsu", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_original_transaction_data_nsu"));
		request.addInputParam("@i_original_transaction_data_mti", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_original_transaction_data_mti"));
		request.addInputParam("@i_original_transaction_data_institution_code", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_original_transaction_data_institution_code"));
		request.addInputParam("@i_original_transaction_data_institution_name", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_original_transaction_data_institutiion_name"));
		request.addInputParam("@i_original_transaction_data_retrieval_reference_number", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_original_transaction_data_retrieval_reference_number"));
		request.addInputParam("@i_legacy_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_legacy-id"));
		request.addInputParam("@i_client_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_client-id"));
		request.addInputParam("@i_uuid", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_uuid"));
		request.addInputParam("@i_x_apigw_api_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_x-apigw-api-id"));
		request.addInputParam("@i_exchange_rate_dest_asset_code", ICTSTypes.SQLVARCHAR, dest_asset_code);
		request.addInputParam("@i_exchange_rate_date_time_gmt", ICTSTypes.SQLVARCHAR, date_time_gmt);
		request.addInputParam("@i_exchange_rate_final_billing_value", ICTSTypes.SQLVARCHAR, final_billing_value);
		
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "REVERSAL");
		if(aBagSPJavaOrchestration.get("IsReentry").equals("S"))
			request.addInputParam("@i_reentry", ICTSTypes.SQLCHAR, "S");
		if(aBagSPJavaOrchestration.get("flowRty").equals(true))
			request.addInputParam("@i_val_uuid", ICTSTypes.SQLCHAR, "S");
		
		request.addOutputParam("@o_accountNumber", ICTSTypes.SQLVARCHAR, "X");		
		request.addOutputParam("@o_externalCustomerId", ICTSTypes.SQLINTN, "0");		
		request.addOutputParam("@o_card_mask", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_ente_bv", ICTSTypes.SQLINTN, "0");
		request.addOutputParam("@o_mon", ICTSTypes.SQLINTN, "0");
		request.addOutputParam("@o_prod", ICTSTypes.SQLINTN, "0");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("@o_accountNumber es " +  wProductsQueryResp.readValueParam("@o_accountNumber"));
			logger.logDebug("@o_externalCustomerId es " +  wProductsQueryResp.readValueParam("@o_externalCustomerId"));
			logger.logDebug("@o_ente_bv es " +  wProductsQueryResp.readValueParam("@o_ente_bv"));
		}
		
		aBagSPJavaOrchestration.put("@o_accountNumber", wProductsQueryResp.readValueParam("@o_accountNumber"));
		aBagSPJavaOrchestration.put("@o_externalCustomerId", wProductsQueryResp.readValueParam("@o_externalCustomerId"));
		aBagSPJavaOrchestration.put("@o_ente_bv", wProductsQueryResp.readValueParam("@o_ente_bv"));
		aBagSPJavaOrchestration.put("@o_mon", wProductsQueryResp.readValueParam("@o_mon"));
		aBagSPJavaOrchestration.put("@o_prod", wProductsQueryResp.readValueParam("@o_prod"));
		aBagSPJavaOrchestration.put("monto", aRequest.readValueParam("@i_values_source_value"));
		
		if(!wProductsQueryResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			aBagSPJavaOrchestration.put("code_error", wProductsQueryResp.getResultSetRowColumnData(2, 1, 1).getValue());
			aBagSPJavaOrchestration.put("message_error", wProductsQueryResp.getResultSetRowColumnData(2, 1, 2).getValue());
			
			logger.logDebug("Code Error local" +aBagSPJavaOrchestration.get("code_error"));
		}
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking valDataLocal AW: " + wProductsQueryResp.getProcedureResponseAsString());
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
		
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, aRequest.readValueParam("@t_trn"));
		
		if(aBagSPJavaOrchestration.get("REENTRY_SSN")!=null){
			request.setValueFieldInHeader(ICOBISTS.HEADER_SSN, (String)aBagSPJavaOrchestration.get("REENTRY_SSN"));
			request.addInputParam("@i_find_json", ICTSTypes.SQLVARCHAR, "Y");
		}
		
		if(aBagSPJavaOrchestration.get("flowRty").equals(true)){
			request.addInputParam("@i_val_rev", ICTSTypes.SQLVARCHAR, "N");
		}
		request.addInputParam("@i_original_transaction_data_institution_code", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_original_transaction_data_institution_code"));
		request.addInputParam("@i_original_transaction_data_institutiion_name", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_original_transaction_data_institutiion_name"));
		request.addInputParam("@i_original_transaction_data_retrieval_reference_number", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_original_transaction_data_retrieval_reference_number"));
		request.addInputParam("@i_nsu", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_original_transaction_data_nsu"));
		
		request.addInputParam("@i_cta_deb", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("@o_accountNumber"));
		request.addInputParam("@i_mon_deb", ICTSTypes.SQLINTN, "0");
		request.addInputParam("@i_prod_deb", ICTSTypes.SQLINTN, "4");
		request.addInputParam("@i_val_deb", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_values_source_value")); 
		request.addInputParam("@i_tarjeta_mascara", ICTSTypes.SQLVARCHAR, "");
		request.addInputParam("@i_cliente", ICTSTypes.SQLINTN, (String) aBagSPJavaOrchestration.get("@o_externalCustomerId"));
		request.addInputParam("@i_comision", ICTSTypes.SQLMONEY, "0"); 
		request.addInputParam("@i_srv", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_srv"));
		request.addInputParam("@i_ofi", ICTSTypes.SQLINTN, aRequest.readValueParam("@s_ofi"));
		request.addInputParam("@i_user", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_user"));
		request.addInputParam("@i_canal", ICTSTypes.SQLINTN, "0");
		request.addInputParam("@i_uuid", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_uuid"));
		request.addInputParam("@i_origin_uuid", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_original_transaction_data_transaction_uuid"));
		request.addInputParam("@t_trn", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@t_trn"));
		request.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_srv"));
		request.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_user"));
		request.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_term"));
		request.addInputParam("@i_origen", ICTSTypes.SQLVARCHAR, "D");

		request.addOutputParam("@o_ssn_host", ICTSTypes.SQLINTN, "0");
		request.addOutputParam("@o_ssn_branch", ICTSTypes.SQLINTN, "0");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("ssn host es " +  wProductsQueryResp.readValueParam("@o_ssn_host"));
			logger.logDebug("ssn branch es " +  wProductsQueryResp.readValueParam("@o_ssn_branch"));
		}
		
		aBagSPJavaOrchestration.put("@o_ssn_host", wProductsQueryResp.readValueParam("@o_ssn_host"));
		aBagSPJavaOrchestration.put("authorizationCode", wProductsQueryResp.getResultSetRowColumnData(3, 1, 1).isNull()?"0":wProductsQueryResp.getResultSetRowColumnData(3, 1, 1).getValue());
		aBagSPJavaOrchestration.put("@o_ssn_branch", wProductsQueryResp.readValueParam("@o_ssn_branch"));
		
		if(!wProductsQueryResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			aBagSPJavaOrchestration.put("code_error", wProductsQueryResp.getResultSetRowColumnData(2, 1, 1).getValue());
			aBagSPJavaOrchestration.put("message_error", wProductsQueryResp.getResultSetRowColumnData(2, 1, 2).getValue());
				
			logger.logDebug("Code Error" +aBagSPJavaOrchestration.get("code_error"));
		}
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de trnDataCentral");
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
		 
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, (String) aBagSPJavaOrchestration.get("@o_externalCustomerId"));
		request.addInputParam("@i_fecha_reg", ICTSTypes.SQLVARCHAR, fechaActual);
		request.addInputParam("@i_fecha_mod", ICTSTypes.SQLVARCHAR, fechaActual);
		request.addInputParam("@i_modo", ICTSTypes.SQLVARCHAR, "RTA");

		request.addInputParam("@i_external_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_uuid"));
		request.addInputParam("@i_request_trn", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_json_req"));
		request.addInputParam("@i_transacion", ICTSTypes.SQLINT4, (String) aBagSPJavaOrchestration.get("@o_ssn_host"));
		request.addInputParam("@i_reverse_uuid", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_original_transaction_data_transaction_uuid"));
		request.addInputParam("@i_transaction_type", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_mti"));
		request.addInputParam("@i_estado", ICTSTypes.SQLVARCHAR, "V");
		
		request.addOutputParam("@o_seq_tran", ICTSTypes.SQLINTN, "0");
		
		logger.logDebug("Request Corebanking registerLog: " + request.toString());
		
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
		
		request.addInputParam("@i_seq", ICTSTypes.SQLINTN, (String) aBagSPJavaOrchestration.get("@o_seq"));
		request.addInputParam("@i_reentry", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("@o_reentry"));
		request.addInputParam("@i_exe_status", ICTSTypes.SQLVARCHAR, executionStatus);
		request.addInputParam("@i_movementId", ICTSTypes.SQLINTN, aBagSPJavaOrchestration.containsKey("@o_ssn_host")?aBagSPJavaOrchestration.get("@o_ssn_host").toString():null);
		
		request.addInputParam("@i_error", ICTSTypes.SQLINTN, aBagSPJavaOrchestration.containsKey("code_error")?aBagSPJavaOrchestration.get("code_error").toString():null);
		request.addOutputParam("@o_codigo", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_mensaje", ICTSTypes.SQLVARCHAR, "X");
		
		logger.logDebug("Request Corebanking registerLog: " + request.toString());
		
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
		
		logger.logInfo("processResponseApi [INI] --->" );
		
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		Integer codeReturn = anOriginalProcedureRes.getReturnCode();
		String executionStatus = null;
		
		logger.logInfo("return code resp--->" + codeReturn );

		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("response", ICTSTypes.SQLVARCHAR, 1500));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("reason", ICTSTypes.SQLBIT, 100));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("available_limit", ICTSTypes.SQLMONEY4, 25));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("authorizationCode", ICTSTypes.SQLINT4, 6));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("approved_value", ICTSTypes.SQLMONEY4, 50));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("settlement_value", ICTSTypes.SQLMONEY4, 50));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("cardholder_billing_value", ICTSTypes.SQLVARCHAR, 50));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("seq", ICTSTypes.SQLVARCHAR, 20));
		
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));

		
		if (codeReturn == 0) {		
			
			if(aBagSPJavaOrchestration.containsKey("code_error")){
				
				logger.logDebug("Ending flow, processResponse error with code: " + aBagSPJavaOrchestration.get("code_error"));
				
				executionStatus = "ERROR";
				updateTrnStatus(anOriginalProcedureRes, aBagSPJavaOrchestration, executionStatus);
				
				IResultSetRow row = new ResultSetRow();
	
				row.addRowData(1, new ResultSetRowColumnData(false, (String) aBagSPJavaOrchestration.get("message_error")));
				row.addRowData(2, new ResultSetRowColumnData(false, (String) aBagSPJavaOrchestration.get("code_error")));
				row.addRowData(3, new ResultSetRowColumnData(false, "0"));
				row.addRowData(4, new ResultSetRowColumnData(false, null));
				row.addRowData(5, new ResultSetRowColumnData(false, "0"));
				row.addRowData(6, new ResultSetRowColumnData(false, "0"));
				row.addRowData(7, new ResultSetRowColumnData(false, "0"));
				row.addRowData(8, new ResultSetRowColumnData(false, null));
				
				data.addRow(row);
				
			} else {
				 
				logger.logDebug("Ending flow, processResponse successful...");
				
				executionStatus = "CORRECT";
				updateTrnStatus(anOriginalProcedureRes, aBagSPJavaOrchestration, executionStatus);
				
				if(aBagSPJavaOrchestration.get("flowRty").equals(false)){
					registerLogBd(aRequest, anOriginalProcedureRes, aBagSPJavaOrchestration);
				}
				
				IResultSetRow row = new ResultSetRow();
				
				row.addRowData(1, new ResultSetRowColumnData(false, "APPROVED"));
				row.addRowData(2, new ResultSetRowColumnData(false, "0"));
				row.addRowData(3, new ResultSetRowColumnData(false, "0"));
				row.addRowData(4, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.containsKey("authorizationCode")?(String)aBagSPJavaOrchestration.get("authorizationCode"):"0"));
				row.addRowData(5, new ResultSetRowColumnData(false, (String) aBagSPJavaOrchestration.get("monto")));
				row.addRowData(6, new ResultSetRowColumnData(false, "0"));
				row.addRowData(7, new ResultSetRowColumnData(false, "0"));
				if(aBagSPJavaOrchestration.containsKey("@o_seq_tran"))
					row.addRowData(8, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get("@o_seq_tran").toString()));
		
				
				data.addRow(row);	
			}
			
		} else {
			
			logger.logDebug("Ending flow, processResponse failed with code: ");
			
			executionStatus = "ERROR";
			updateTrnStatus(anOriginalProcedureRes, aBagSPJavaOrchestration, executionStatus);
			
			String codeError = aBagSPJavaOrchestration.containsKey("code_error")?aBagSPJavaOrchestration.get("code_error").toString(): codeReturn.toString();
			String mesageError = aBagSPJavaOrchestration.containsKey("message_error")?aBagSPJavaOrchestration.get("message_error").toString():"SYSTEM_ERROR";
			
			
			IResultSetRow row = new ResultSetRow();
			
			row.addRowData(1, new ResultSetRowColumnData(false, mesageError));
			row.addRowData(2, new ResultSetRowColumnData(false, codeError));
			row.addRowData(3, new ResultSetRowColumnData(false, null));
			row.addRowData(4, new ResultSetRowColumnData(false, null));
			
			data.addRow(row);
		}
		
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		
		wProcedureResponse.addResponseBlock(resultsetBlock);
		wProcedureResponse.addResponseBlock(resultsetBlock2);
		
		return wProcedureResponse;		
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
