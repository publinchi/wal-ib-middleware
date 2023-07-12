/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.authorize.reversal.api;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

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
@Component(name = "AuthorizeReversalDockOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "AuthorizeReversalDockOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "AuthorizeReversalDockOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_auth_reversal_dock_api")})
public class AuthorizeReversalDockOrchestrationCore extends SPJavaOrchestrationBase {
	
	private ILogger logger = (ILogger) this.getLogger();
	private static final String CLASS_NAME = "AuthorizeReversalDockOrchestrationCore";

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		logger.logDebug("Begin flow, AuthorizeReversal starts...");		
		
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		
		
		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();
		
		anProcedureResponse = authorizeReversalDock(anOriginalRequest, aBagSPJavaOrchestration);
		
		return processResponseApi(anOriginalRequest, anProcedureResponse, aBagSPJavaOrchestration);
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
		
		if (values_1 != null && !values_1.isEmpty() && !values_1.matches("[0-9]+[\\.]?[0-9]*")) {
			values_1 = "";
		}
		
		if (values_2 != null && !values_2.isEmpty() && !values_2.matches("[0-9]+[\\.]?[0-9]*")) {
			values_2 = "";
		}
		
		if (values_3 != null && !values_3.isEmpty() && !values_3.matches("[0-9]+[\\.]?[0-9]*")) {
			values_3 = "";
		}
		
		if (values_4 != null && !values_4.isEmpty() && !values_4.matches("[0-9]+[\\.]?[0-9]*")) {
			values_4 = "";
		}
		

		request.setSpName("cob_atm..sp_bv_valida_dock_reversal_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
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
		request.addInputParam("@i_merchant_category_code", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_merchant_category_code"));
		request.addInputParam("@i_values_source_currency_code", ICTSTypes.SQLMONEY, values_1);
		request.addInputParam("@i_values_billing_currency_code", ICTSTypes.SQLMONEY, values_2);
		request.addInputParam("@i_values_source_value", ICTSTypes.SQLMONEY, values_3);
		request.addInputParam("@i_values_billing_value", ICTSTypes.SQLMONEY, values_4);
		request.addInputParam("@i_terminal_code", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_terminal_code"));
		request.addInputParam("@i_establishment_code", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_establishment_code"));
		request.addInputParam("@i_brand_response_code", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_brand_response_code"));
		request.addInputParam("@i_original_transaction_data_transaction_uuid", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_original_transaction_data_transaction_uuid"));
		request.addInputParam("@i_original_transaction_data_nsu", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_original_transaction_data_nsu"));
		request.addInputParam("@i_original_transaction_data_mti", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_original_transaction_data_mti"));
		request.addInputParam("@i_original_transaction_data_institution_code", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_original_transaction_data_institution_code"));
		request.addInputParam("@i_original_transaction_data_institution_name", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_original_transaction_data_institution_name"));
		request.addInputParam("@i_original_transaction_data_retrieval_reference_number", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_original_transaction_data_retrieval_reference_number"));
		request.addInputParam("@i_client-id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_card_id"));
		request.addInputParam("@i_uuid", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_card_id"));
		request.addInputParam("@i_x-apigw-api-id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_card_id"));
		
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "REVERSAL");
		
		request.addOutputParam("@o_accountNumber", ICTSTypes.SQLVARCHAR, "X");		
		request.addOutputParam("@o_externalCustomerId", ICTSTypes.SQLINTN, "X");		
		request.addOutputParam("@o_card_mask", ICTSTypes.SQLVARCHAR, "X");		
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("@o_accountNumber es " +  wProductsQueryResp.readValueParam("@o_accountNumber"));
		}
		
		aBagSPJavaOrchestration.put("@o_accountNumber", wProductsQueryResp.readValueParam("@o_accountNumber"));
		aBagSPJavaOrchestration.put("@o_externalCustomerId", wProductsQueryResp.readValueParam("@o_externalCustomerId"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking valDataLocal AW: " + wProductsQueryResp.getProcedureResponseAsString());
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
		
		request.addInputParam("@i_cta_deb", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("@o_account_number"));
		request.addInputParam("@i_mon_deb", ICTSTypes.SQLINTN, "0");
		request.addInputParam("@i_prod_deb", ICTSTypes.SQLINTN, "4");
		request.addInputParam("@i_val_deb", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_values_billing_value")); 
		request.addInputParam("@i_tarjeta_mascara", ICTSTypes.SQLVARCHAR, "");
		request.addInputParam("@i_cliente", ICTSTypes.SQLINTN, (String) aBagSPJavaOrchestration.get("@o_externalCustomerId"));
		request.addInputParam("@i_comision", ICTSTypes.SQLMONEY, "0"); 
		request.addInputParam("@i_srv", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_srv"));
		request.addInputParam("@i_ofi", ICTSTypes.SQLINTN, aRequest.readValueParam("@s_ofi"));
		request.addInputParam("@i_user", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_user"));
		request.addInputParam("@i_canal", ICTSTypes.SQLINTN, "0");
		request.addInputParam("@i_trn_cen", ICTSTypes.SQLINTN, "");
		request.addInputParam("@i_causa", ICTSTypes.SQLVARCHAR, "");
		request.addInputParam("@i_origin_uuid", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_original_transaction_data_transaction_uuid"));
		request.addInputParam("@t_trn", ICTSTypes.SQLINTN, "");
		request.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_srv"));
		request.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_user"));
		request.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_term"));
		
		request.addOutputParam("@o_ssn_host", ICTSTypes.SQLINTN, "0");		
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("ssn host es " +  wProductsQueryResp.readValueParam("@o_ssn_host"));
		}
		
		aBagSPJavaOrchestration.put("@o_ssn_host", wProductsQueryResp.readValueParam("@o_ssn_host"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de trnDataCentral");
		}

		return wProductsQueryResp;
	}
	
	private void registerLogBd(IProcedureRequest aRequest, IProcedureResponse reponseAccount, Map<String, Object> aBagSPJavaOrchestration) {

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
		request.addInputParam("@i_transacion", ICTSTypes.SQLINT4, reponseAccount.readValueParam("@o_ssn_host"));
		request.addInputParam("@i_reverse_uuid", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_original_transaction_data_transaction_uuid"));
		request.addInputParam("@i_transaction_type", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_mti"));
		request.addInputParam("@i_estado", ICTSTypes.SQLVARCHAR, "V");
		
		logger.logDebug("Request Corebanking registerLog: " + request.toString());
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking registerLog: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de registerLogBd");
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
		
		logger.logInfo("return code resp--->" + codeReturn );

		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("response", ICTSTypes.SQLVARCHAR, 100));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("reason", ICTSTypes.SQLVARCHAR, 100));
		
		if (codeReturn == 0) {
			
			if (anOriginalProcedureRes.readValueParam("@o_ssn_host") != null && !anOriginalProcedureRes.readValueParam("@o_ssn_host").equals("0"))
				registerLogBd(aRequest, anOriginalProcedureRes, aBagSPJavaOrchestration);
			
			if(anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).equals("0")){
				
				logger.logDebug("return code response: " + anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1));
				logger.logDebug("Ending flow, processResponse success with code: ");
				
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, "0"));
				row.addRowData(2, new ResultSetRowColumnData(false, "Success"));
				data.addRow(row);
			}
			 else {
				logger.logDebug("Ending flow, processResponse error");
				
				String code = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).isNull()?"400218":anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).getValue();
				String message = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).isNull()?"Service execution error":anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).getValue();
							
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, code));
				row.addRowData(2, new ResultSetRowColumnData(false, message));
				data.addRow(row);
			}
		} else {
			
			logger.logDebug("Ending flow, processResponse failed with code: ");
			
			IResultSetRow row = new ResultSetRow();
			row.addRowData(1, new ResultSetRowColumnData(false, codeReturn.toString()));
			row.addRowData(2, new ResultSetRowColumnData(false, anOriginalProcedureRes.getMessage(1).getMessageText()));
			data.addRow(row);
		}

		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);

		wProcedureResponse.addResponseBlock(resultsetBlock);
		
		return wProcedureResponse;		
	}
}
