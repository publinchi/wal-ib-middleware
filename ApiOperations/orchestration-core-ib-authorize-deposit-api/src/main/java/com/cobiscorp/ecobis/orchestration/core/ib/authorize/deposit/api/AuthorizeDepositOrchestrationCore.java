/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.authorize.deposit.api;

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
 * @since Jun 30, 2023
 * @version 1.0.0
 */
@Component(name = "AuthorizeDepositOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "AuthorizeDepositOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "AuthorizeDepositOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_auth_deposit_api")})
public class AuthorizeDepositOrchestrationCore extends SPJavaOrchestrationBase {
	
	private ILogger logger = (ILogger) this.getLogger();
	private static final String CLASS_NAME = "AuthorizeDepositOrchestrationCore";
	protected static final String CHANNEL_REQUEST = "8";
	protected static final String AUTHORIZE_PURCHASE= "AUTHORIZE_DEPOSIT";
	protected static final String MODE_OPERATION = "PYS";

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		logger.logDebug("Begin flow, AuthorizeDeposit starts...");		
		
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		
		
		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();
		
		anProcedureResponse = authorizeDeposit(anOriginalRequest, aBagSPJavaOrchestration);
		
		return processResponseApi(anOriginalRequest, anProcedureResponse,aBagSPJavaOrchestration);
	}
	
	private IProcedureResponse authorizeDeposit(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en authorizeDeposit: ");
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
			logger.logInfo(CLASS_NAME + " Saliendo de authorizeDeposit...");
		}

		return wAuthValDataLocal;
	}
	
	private IProcedureResponse valDataLocal(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en valDataLocal");
		}
		
		String xRequestId = aRequest.readValueParam("@x_request_id");
		String xEndUserRequestDateTime = aRequest.readValueParam("@x_end_user_request_date");
		String xEndUserIp = aRequest.readValueParam("@x_end_user_ip"); 
		String xChannel = aRequest.readValueParam("@x_channel");
		String monto = aRequest.readValueParam("@i_amount");		                                 
		String gtmDateTime = aRequest.readValueParam("@i_transmission_date_time_gmt");
		String date = aRequest.readValueParam("@i_date");
		String time = aRequest.readValueParam("@i_time");
		String posId = aRequest.readValueParam("@i_pos_id");
		String cashier = aRequest.readValueParam("@i_cashier");
		String transaction = aRequest.readValueParam("@i_transaction");
		String pinpad = aRequest.readValueParam("@i_pinpad");
		
		if (xRequestId.equals("null") || xRequestId.trim().isEmpty()) {
			xRequestId = "E";
		}
		
		if (xEndUserRequestDateTime.equals("null") || xEndUserRequestDateTime.trim().isEmpty()) {
			xEndUserRequestDateTime = "E";
		}
		
		if (xEndUserIp.equals("null") || xEndUserIp.trim().isEmpty()) {
			xEndUserIp = "E";
		}
		
		if (xChannel.equals("null") || xChannel.trim().isEmpty()) {
			xChannel = "E";
		}
		
		if (monto != null && !monto.isEmpty() && !isNumeric(monto)) {
			monto = "";
		}
		
		if (gtmDateTime != null && !gtmDateTime.isEmpty() && !isGtmDateTime(gtmDateTime)) {
			gtmDateTime = "I";
		}
		
		if (date != null && !date.isEmpty() && !isDate(date)) {
			date = "I";
		}
		
		if (time != null && !time.isEmpty() && !isTime(time)) {
			time = "I";
		}
		
		if (posId == null || posId.trim().isEmpty()) {
			posId = "E";
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
		
		request.setSpName("cob_atm..sp_bv_valida_trn_atm_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@x_request_id", ICTSTypes.SQLVARCHAR, xRequestId);
		request.addInputParam("@x_end_user_request_date", ICTSTypes.SQLVARCHAR, xEndUserRequestDateTime);
		request.addInputParam("@x_end_user_ip", ICTSTypes.SQLVARCHAR, xEndUserIp);
		request.addInputParam("@x_channel", ICTSTypes.SQLVARCHAR, xChannel);
		
		request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_external_customer_id"));
		request.addInputParam("@i_uuid", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_uuid"));
		request.addInputParam("@i_orderId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_order_id"));
		request.addInputParam("@i_accountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account_number"));
		request.addInputParam("@i_transmissionDateTimeGmt", ICTSTypes.SQLVARCHAR, gtmDateTime);
		request.addInputParam("@i_date", ICTSTypes.SQLVARCHAR, date);
		request.addInputParam("@i_time", ICTSTypes.SQLVARCHAR, time);
		request.addInputParam("@i_mti", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_mti"));
		request.addInputParam("@i_type", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_type"));
		request.addInputParam("@i_processingCode", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_processing_code"));
		request.addInputParam("@i_nsu", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_nsu"));
		request.addInputParam("@i_merchantCategoryCode", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_merchant_category_code"));
		request.addInputParam("@i_sourceCurrencyCode", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_source_currency_code"));
		request.addInputParam("@i_settlementCurrencyCode", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_settlement_currency_code"));
		request.addInputParam("@i_monto", ICTSTypes.SQLMONEY, monto);
		request.addInputParam("@i_institutionName", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_institution_name"));
		request.addInputParam("@i_terminalCode", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_terminal_code"));
		request.addInputParam("@i_retrievalReferenceNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_retrieval_reference_number"));
		request.addInputParam("@i_acquirerCountryCode", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_acquirer_country_code"));
		request.addInputParam("@i_affiliationNumber", ICTSTypes.SQLDECIMAL, aRequest.readValueParam("@i_affiliation_number"));
		request.addInputParam("@i_storeNumber", ICTSTypes.SQLDECIMAL, aRequest.readValueParam("@i_store_number"));
		request.addInputParam("@i_pos_id", ICTSTypes.SQLVARCHAR, posId);
		request.addInputParam("@i_cashier", ICTSTypes.SQLVARCHAR, cashier);
		request.addInputParam("@i_transaction", ICTSTypes.SQLVARCHAR, transaction);
		request.addInputParam("@i_pinpad", ICTSTypes.SQLVARCHAR, pinpad);
		
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "DEPOSIT");
		
		request.addOutputParam("@o_seq", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_reentry", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_type_transaction", ICTSTypes.SQLVARCHAR, "X");
			
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("secuencial es " +  wProductsQueryResp.readValueParam("@o_seq"));
			logger.logDebug("reentry es " +  wProductsQueryResp.readValueParam("@o_reentry"));
		}
		
		aBagSPJavaOrchestration.put("o_seq", wProductsQueryResp.readValueParam("@o_seq"));
		aBagSPJavaOrchestration.put("o_reentry", wProductsQueryResp.readValueParam("@o_reentry"));
		aBagSPJavaOrchestration.put("o_type_transaction", wProductsQueryResp.readValueParam("@o_type_transaction"));
			
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
		
		request.addInputParam("@i_cta_deb", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account_number"));
		request.addInputParam("@i_mon_deb", ICTSTypes.SQLINTN, "0");
		request.addInputParam("@i_prod_deb", ICTSTypes.SQLINTN, "4");
		request.addInputParam("@i_val_deb", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_amount")); 
		request.addInputParam("@i_tarjeta_mascara", ICTSTypes.SQLVARCHAR, "");
		request.addInputParam("@i_cliente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_external_customer_id"));
		request.addInputParam("@i_comision", ICTSTypes.SQLMONEY, "0"); 
		request.addInputParam("@i_srv", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_srv"));
		request.addInputParam("@i_ofi", ICTSTypes.SQLINTN, aRequest.readValueParam("@s_ofi"));
		request.addInputParam("@i_user", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_user"));
		request.addInputParam("@i_canal", ICTSTypes.SQLINTN, "0");
		request.addInputParam("@i_uuid", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_uuid"));
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
		}
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("ssn branch es " +  wProductsQueryResp.readValueParam("@o_ssn_branch"));
		}
		
		aBagSPJavaOrchestration.put("@o_ssn_host", wProductsQueryResp.readValueParam("@o_ssn_host"));
		aBagSPJavaOrchestration.put("@o_ssn_branch", wProductsQueryResp.readValueParam("@o_ssn_branch"));
		
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
		 
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_external_customer_id"));
		request.addInputParam("@i_fecha_reg", ICTSTypes.SQLVARCHAR, fechaActual);
		request.addInputParam("@i_fecha_mod", ICTSTypes.SQLVARCHAR, fechaActual);
		request.addInputParam("@i_modo", ICTSTypes.SQLVARCHAR, "RTA");

		request.addInputParam("@i_external_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_uuid"));
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
		
		request.addInputParam("@i_seq", ICTSTypes.SQLINTN, (String) aBagSPJavaOrchestration.get("o_seq"));
		request.addInputParam("@i_reentry", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("o_reentry"));
		request.addInputParam("@i_exe_status", ICTSTypes.SQLVARCHAR, executionStatus);
		request.addInputParam("@i_movementId", ICTSTypes.SQLINTN, aResponse.readValueParam("@o_ssn_host"));
		
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
		
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));

		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));
		
		IResultSetHeader metaData3 = new ResultSetHeader();
		IResultSetData data3 = new ResultSetData();
		
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("authorizationCode", ICTSTypes.SQLINT4, 6));
		
		IResultSetHeader metaData4 = new ResultSetHeader();
		IResultSetData data4 = new ResultSetData();
		
		metaData4.addColumnMetaData(new ResultSetHeaderColumn("seq", ICTSTypes.SQLVARCHAR, 20));
		
		IResultSetHeader metaData5 = new ResultSetHeader();
		IResultSetData data5 = new ResultSetData();
		
		metaData5.addColumnMetaData(new ResultSetHeaderColumn("movementId", ICTSTypes.SQLINT4, 10));

		
		if (codeReturn == 0) {
			
			if (anOriginalProcedureRes.readValueParam("@o_ssn_host") != null && !anOriginalProcedureRes.readValueParam("@o_ssn_host").equals("0"))
				registerLogBd(aRequest, anOriginalProcedureRes, aBagSPJavaOrchestration);
			
			if(anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
				
				logger.logDebug("Return code response: " + anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1));
				logger.logDebug("Ending flow, processResponse successful...");
				
				String authorizationCode = anOriginalProcedureRes.getResultSetRowColumnData(3, 1, 1).isNull()?"0":anOriginalProcedureRes.getResultSetRowColumnData(3, 1, 1).getValue();
				String seq = aBagSPJavaOrchestration.get("@o_seq_tran").toString();
				String movementId = anOriginalProcedureRes.readValueParam("@o_ssn_host");
				
				executionStatus = "CORRECT";
				updateTrnStatus(anOriginalProcedureRes, aBagSPJavaOrchestration, executionStatus);
				
				notifyDeposit(aRequest, aBagSPJavaOrchestration);
				
				IResultSetRow row = new ResultSetRow();
				
				row.addRowData(1, new ResultSetRowColumnData(false, "true"));
				data.addRow(row);
				
				IResultSetRow row2 = new ResultSetRow();
				
				row2.addRowData(1, new ResultSetRowColumnData(false, "0"));
				row2.addRowData(2, new ResultSetRowColumnData(false, "Success"));
				data2.addRow(row2);
				
				IResultSetRow row3 = new ResultSetRow();
				
				row3.addRowData(1, new ResultSetRowColumnData(false, authorizationCode));
				data3.addRow(row3);
				
				IResultSetRow row4 = new ResultSetRow();
				
				row4.addRowData(1, new ResultSetRowColumnData(false, seq));
				data4.addRow(row4);
				
				IResultSetRow row5 = new ResultSetRow();
				
				row5.addRowData(1, new ResultSetRowColumnData(false, movementId));
				data5.addRow(row5);
				
			} else {
				
				logger.logDebug("Ending flow, processResponse error");
				
				String success = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).isNull()?"false":anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue();
				String code = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).isNull()?"400218":anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).getValue();
				String message = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).isNull()?"Service execution error":anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).getValue();
				
				executionStatus = "ERROR";
				updateTrnStatus(anOriginalProcedureRes, aBagSPJavaOrchestration, executionStatus);
				
				IResultSetRow row = new ResultSetRow();
				
				row.addRowData(1, new ResultSetRowColumnData(false, success));
				data.addRow(row);
				
				IResultSetRow row2 = new ResultSetRow();
				
				row2.addRowData(1, new ResultSetRowColumnData(false, code));
				row2.addRowData(2, new ResultSetRowColumnData(false, message));
				data2.addRow(row2);
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
		}

		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		IResultSetBlock resultsetBlock3 = new ResultSetBlock(metaData3, data3);
		IResultSetBlock resultsetBlock4 = new ResultSetBlock(metaData4, data4);
		IResultSetBlock resultsetBlock5 = new ResultSetBlock(metaData5, data5);

		wProcedureResponse.addResponseBlock(resultsetBlock);
		wProcedureResponse.addResponseBlock(resultsetBlock2);
		wProcedureResponse.addResponseBlock(resultsetBlock3);
		wProcedureResponse.addResponseBlock(resultsetBlock4);
		wProcedureResponse.addResponseBlock(resultsetBlock5);
		
		return wProcedureResponse;		
	}
	
	private void notifyDeposit(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
        
        IProcedureRequest request = new ProcedureRequestAS();

        if (logger.isInfoEnabled()) {
            logger.logInfo(CLASS_NAME + " Entrando en notifyDeposit...");
        }
        
        request.setSpName("cob_bvirtual..sp_bv_enviar_notif_ib_api");

        request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
                IMultiBackEndResolverService.TARGET_LOCAL);
        request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
        
        request.addInputParam("@s_culture", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_culture"));
		request.addInputParam("@s_date", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_date"));
        
        request.addInputParam("@i_titulo", ICTSTypes.SQLVARCHAR, "Deposit IDC");
        request.addInputParam("@i_notificacion", ICTSTypes.SQLVARCHAR, "N42");
        request.addInputParam("@i_servicio", ICTSTypes.SQLINTN, "8");
        request.addInputParam("@i_producto", ICTSTypes.SQLINTN, "18");
        request.addInputParam("@i_tipo", ICTSTypes.SQLVARCHAR, "M");
        request.addInputParam("@i_tipo_mensaje", ICTSTypes.SQLVARCHAR, "F");
        request.addInputParam("@i_print", ICTSTypes.SQLVARCHAR, "S");
        request.addInputParam("@i_aux1", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_institution_name"));
        request.addInputParam("@i_ente_mis", ICTSTypes.SQLINTN, anOriginalRequest.readValueParam("@i_external_customer_id"));
        request.addInputParam("@i_ente_ib", ICTSTypes.SQLINTN, "0");
        request.addInputParam("@i_c1", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_account_number"));
        request.addInputParam("@i_v2", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_amount"));
        request.addInputParam("@i_s", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("@o_ssn_host").toString());
        request.addInputParam("@i_r", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("@o_ssn_branch").toString());
        
        IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
        
        if (logger.isDebugEnabled()) {
            logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
        }

        if (logger.isInfoEnabled()) {
            logger.logInfo(CLASS_NAME + " Saliendo de notifyDeposit...");
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
        	
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            
            dateFormat.setLenient(false);
            dateFormat.parse(date);
            
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
	
	public static boolean isTime(String time) {
        try {
        	
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            
            timeFormat.setLenient(false);
            timeFormat.parse(time);
            
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}
