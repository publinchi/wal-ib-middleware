/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.authorize.deposit.dock.api;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
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

/**
 * @author Sochoa
 * @since Jul 11, 2023
 * @version 1.0.0
 */
@Component(name = "AuthorizeDepositDockOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "AuthorizeDepositDockOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "AuthorizeDepositDockOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_auth_deposit_dock_api")})
public class AuthorizeDepositDockOrchestrationCore extends SPJavaOrchestrationBase {
	
	private ILogger logger = (ILogger) this.getLogger();
	private static final String CLASS_NAME = "AuthorizeDepositDockOrchestrationCore";
	protected static final String CHANNEL_REQUEST = "8";
	protected static final String MODE_OPERATION = "PYS";

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		logger.logDebug("Begin flow, AuthorizeDepositDock starts...");		
		
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		
		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();
		
		anProcedureResponse = authorizeDepositDock(anOriginalRequest, aBagSPJavaOrchestration);
		
		return processResponseApi(anOriginalRequest, anProcedureResponse,aBagSPJavaOrchestration);
	}
	
	private IProcedureResponse authorizeDepositDock(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en authorizeDepositDock: ");
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
			logger.logInfo(CLASS_NAME + " Saliendo de authorizeDepositDock...");
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
		String gtm_date_time = aRequest.readValueParam("@i_transmission_date_time_gtm");
		String date = aRequest.readValueParam("@i_terminal_date");
		String time = aRequest.readValueParam("@i_terminal_time");
		String exp_date = aRequest.readValueParam("@i_card_expiration_date");
		String pos_id = aRequest.readValueParam("@i_pos_id");
		String cashier = aRequest.readValueParam("@i_cashier");
		String transaction = aRequest.readValueParam("@i_transaction");
		String pinpad = aRequest.readValueParam("@i_pinpad");
		String establishment = aRequest.readValueParam("@i_establishment");
		String retrieval_reference_number = aRequest.readValueParam("@i_retrieval_reference_number");

				
		
		if (s_amount != null && !s_amount.isEmpty() && !isNumeric(s_amount)) {
			s_amount = "";
		}
		
		if (b_amount != null && !b_amount.isEmpty() && !isNumeric(b_amount)) {
			b_amount = "";
		}
		
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
		
		if (establishment == null || establishment.trim().isEmpty()) {
			establishment = "E";
		}
		
		if (retrieval_reference_number == null || retrieval_reference_number.trim().isEmpty()) {
			retrieval_reference_number = "E";
		}
		
		/*if (pos_id == null || pos_id.trim().isEmpty()) {
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
		}*/
			
		
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
		request.addInputParam("@i_affiliation_number", ICTSTypes.SQLDECIMAL, aRequest.readValueParam("@i_affiliation_number"));
		request.addInputParam("@i_store_number", ICTSTypes.SQLDECIMAL, aRequest.readValueParam("@i_store_number"));
		request.addInputParam("@i_pos_id", ICTSTypes.SQLVARCHAR, pos_id);
		request.addInputParam("@i_cashier", ICTSTypes.SQLVARCHAR, cashier);
		request.addInputParam("@i_transaction", ICTSTypes.SQLVARCHAR, transaction);
		request.addInputParam("@i_pinpad", ICTSTypes.SQLVARCHAR, pinpad);
		request.addInputParam("@i_creation_date", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_creation_date"));
		request.addInputParam("@i_retrieval_reference_number", ICTSTypes.SQLVARCHAR, retrieval_reference_number);
		request.addInputParam("@i_establishment", ICTSTypes.SQLVARCHAR, establishment);
		request.addInputParam("@i_dest_asset_code", ICTSTypes.SQLDECIMAL, aRequest.readValueParam("@i_dest_asset_code"));
		request.addInputParam("@i_date_time_gmt", ICTSTypes.SQLDECIMAL, aRequest.readValueParam("@i_date_time_gmt"));
		request.addInputParam("@i_final_billing_value", ICTSTypes.SQLDECIMAL, aRequest.readValueParam("@i_final_billing_value"));
		request.addInputParam("@i_origin_asset_code", ICTSTypes.SQLDECIMAL, aRequest.readValueParam("@i_origin_asset_code"));
		request.addInputParam("@i_rate", ICTSTypes.SQLDECIMAL, aRequest.readValueParam("@i_rate"));
		request.addInputParam("@i_spread_percent", ICTSTypes.SQLDECIMAL, aRequest.readValueParam("@i_spread_percent"));
		
		
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "DEPOSIT");
			
		request.addOutputParam("@o_ente", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_cta", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_seq", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_reentry", ICTSTypes.SQLVARCHAR, "X");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
			
		if (logger.isDebugEnabled()) {
			logger.logDebug("ente es " +  wProductsQueryResp.readValueParam("@o_ente"));
			logger.logDebug("cta es " +  wProductsQueryResp.readValueParam("@o_cta"));
			logger.logDebug("secuencial es " +  wProductsQueryResp.readValueParam("@o_seq"));
			logger.logDebug("reentry es " +  wProductsQueryResp.readValueParam("@o_reentry"));
		}
		
		aBagSPJavaOrchestration.put("amount", aRequest.readValueParam("@i_source_value"));
		aBagSPJavaOrchestration.put("ente", wProductsQueryResp.readValueParam("@o_ente"));
		aBagSPJavaOrchestration.put("cta", wProductsQueryResp.readValueParam("@o_cta"));
		aBagSPJavaOrchestration.put("seq", wProductsQueryResp.readValueParam("@o_seq"));
		aBagSPJavaOrchestration.put("reentry", wProductsQueryResp.readValueParam("@o_reentry"));
		
		if(!wProductsQueryResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			aBagSPJavaOrchestration.put("code_error", wProductsQueryResp.getResultSetRowColumnData(2, 1, 1).getValue());
			aBagSPJavaOrchestration.put("message_error", wProductsQueryResp.getResultSetRowColumnData(2, 1, 2).getValue());
			
			logger.logDebug("Code Error local" +aBagSPJavaOrchestration.get("code_error"));
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

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en trnDataCentral");
		}

		request.setSpName("cob_cuentas..sp_retiro_atm_api");

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
		request.addInputParam("@t_trn", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@t_trn"));
		request.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_srv"));
		request.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_user"));
		request.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_term"));
		
		request.addOutputParam("@o_ssn_host", ICTSTypes.SQLINTN, "0");
		request.addOutputParam("@o_ssn_branch", ICTSTypes.SQLINTN, "0");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("ssn host es " +  wProductsQueryResp.readValueParam("@o_ssn_host"));
			logger.logDebug("ssn branch es " +  wProductsQueryResp.readValueParam("@o_ssn_branch"));
		}
		
		aBagSPJavaOrchestration.put("@o_ssn_host", wProductsQueryResp.readValueParam("@o_ssn_host"));
		aBagSPJavaOrchestration.put("@o_ssn_branch", wProductsQueryResp.readValueParam("@o_ssn_branch"));
		
		if(wProductsQueryResp.readValueParam("@o_ssn_host").equals("0")){
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
		 
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aBagSPJavaOrchestration.get("ente").toString());
		request.addInputParam("@i_fecha_reg", ICTSTypes.SQLVARCHAR, fechaActual);
		request.addInputParam("@i_fecha_mod", ICTSTypes.SQLVARCHAR, fechaActual);
		request.addInputParam("@i_modo", ICTSTypes.SQLVARCHAR, "RTA");

		request.addInputParam("@i_external_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_uuid"));
		request.addInputParam("@i_request_trn", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_json_req"));
		request.addInputParam("@i_transacion", ICTSTypes.SQLINT4, aResponse.readValueParam("@o_ssn_host"));
		request.addInputParam("@i_reverse_uuid", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_origin_uuid"));
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
		
		request.addInputParam("@i_seq", ICTSTypes.SQLINTN, (String) aBagSPJavaOrchestration.get("seq"));
		request.addInputParam("@i_reentry", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("reentry"));
		request.addInputParam("@i_exe_status", ICTSTypes.SQLVARCHAR, executionStatus);
		request.addInputParam("@i_movementId", ICTSTypes.SQLINTN, aBagSPJavaOrchestration.containsKey("@o_ssn_host")?aBagSPJavaOrchestration.get("@o_ssn_host").toString():null);
		
		logger.logDebug("Request Corebanking registerLog: " + request.toString());
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking updateTransferStatus: " + wProductsQueryResp.getProcedureResponseAsString());
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
				
				logger.logDebug("Ending flow, processResponse error with code: " + aBagSPJavaOrchestration.get("code_error"));
				
				executionStatus = "ERROR";
				updateTrnStatus(anOriginalProcedureRes, aBagSPJavaOrchestration, executionStatus);
				
				IResultSetRow row = new ResultSetRow();
				
				row.addRowData(1, new ResultSetRowColumnData(false, "0"));
				row.addRowData(2, new ResultSetRowColumnData(false, "0"));
				row.addRowData(3, new ResultSetRowColumnData(false, "0"));
				row.addRowData(4, new ResultSetRowColumnData(false, "SYSTEM_ERROR"));
				row.addRowData(5, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get("message_error").toString() + " [" + aBagSPJavaOrchestration.get("code_error").toString() + "]"));
				row.addRowData(6, new ResultSetRowColumnData(false, "0"));
				row.addRowData(7, new ResultSetRowColumnData(false, null));
				row.addRowData(8, new ResultSetRowColumnData(false, null));
				
				data.addRow(row);
				
			} else {
				 
				logger.logDebug("Ending flow, processResponse successful...");
				
				executionStatus = "CORRECT";
				updateTrnStatus(anOriginalProcedureRes, aBagSPJavaOrchestration, executionStatus);
				
				registerLogBd(aRequest, anOriginalProcedureRes, aBagSPJavaOrchestration);
				
				IResultSetRow row = new ResultSetRow();
				
				row.addRowData(1, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get("amount").toString()));
				row.addRowData(2, new ResultSetRowColumnData(false, "0"));
				row.addRowData(3, new ResultSetRowColumnData(false, "0"));
				row.addRowData(4, new ResultSetRowColumnData(false, "APPROVED"));
				row.addRowData(5, new ResultSetRowColumnData(false, "Transaction "+ aBagSPJavaOrchestration.get("@o_ssn_host").toString()));
				row.addRowData(6, new ResultSetRowColumnData(false, "0"));
				row.addRowData(7, new ResultSetRowColumnData(false, anOriginalProcedureRes.readValueParam("@o_ssn_branch")));
				row.addRowData(8, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get("@o_seq_tran").toString()));
				
				data.addRow(row);
			}
			
		} else {
			
			logger.logDebug("Ending flow, processResponse failed with code: ");
			
			executionStatus = "ERROR";
			updateTrnStatus(anOriginalProcedureRes, aBagSPJavaOrchestration, executionStatus);
			
			IResultSetRow row = new ResultSetRow();
			
			row.addRowData(1, new ResultSetRowColumnData(false, "false"));
			data.addRow(row);
			
			IResultSetRow row2 = new ResultSetRow();
			
			row2.addRowData(1, new ResultSetRowColumnData(false, codeReturn.toString()));
			row2.addRowData(2, new ResultSetRowColumnData(false, anOriginalProcedureRes.getMessage(1).getMessageText()));
			
			data2.addRow(row2);
			
			wProcedureResponse.setReturnCode(1);
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
}
