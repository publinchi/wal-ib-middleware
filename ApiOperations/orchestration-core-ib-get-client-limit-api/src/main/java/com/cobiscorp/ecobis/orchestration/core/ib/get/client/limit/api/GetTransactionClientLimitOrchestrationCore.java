/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.get.client.limit.api;

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
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * @author Sochoa
 * @since Ene 17, 2024
 * @version 1.0.0
 */
@Component(name = "GetTransactionClientLimitOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "GetTransactionClientLimitOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "GetTransactionClientLimitOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_get_transaction_limit_api")})
public class GetTransactionClientLimitOrchestrationCore extends SPJavaOrchestrationBase {
	
	private ILogger logger = (ILogger) this.getLogger();

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		
		logger.logDebug("Begin flow, GetTransactionLimit starts...");		
		
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		
		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();

		String operation = anOriginalRequest.readValueParam("@i_operation");
		aBagSPJavaOrchestration.put("operation", operation);
		if(operation.equals("C")){
			anProcedureResponse = callGetLimits(anOriginalRequest, aBagSPJavaOrchestration);
			if(!anProcedureResponse.getResultSetRowColumnData(1, 1, 1).getValue().equals("true")){
				logger.logInfo("se va por el error");
				return processResponseError(anProcedureResponse);
			}
			anProcedureResponse = processTransforResponse(anProcedureResponse, aBagSPJavaOrchestration);
			logger.logInfo("pasoooo");
		} else if (operation.equals("S")) {
			JsonObject requestBody = createRequestBody(aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put("requestBody", requestBody);
			anProcedureResponse = callSaveLimits(anOriginalRequest, aBagSPJavaOrchestration);
			if(!anProcedureResponse.getResultSetRowColumnData(1, 1, 1).getValue().equals("true")){
				logger.logInfo("se va por el error");
				return processResponseError(anProcedureResponse);
			}
			anProcedureResponse = processTransforResponse(anProcedureResponse, aBagSPJavaOrchestration);
			// hacer llamado Conector to do
		}

		//anProcedureResponse = getTransactionLimit(anOriginalRequest, aBagSPJavaOrchestration);
		logger.logInfo("retorna claro que si");
		//return processResponseError(anProcedureResponse);
		return  anProcedureResponse; //processResponseApi(anOriginalRequest, anProcedureResponse, aBagSPJavaOrchestration);
	}

	private IProcedureResponse callSaveLimits(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration){
		if(logger.isDebugEnabled())
			logger.logDebug(" callUpdateLimitsConn [INI]");
		
		IProcedureResponse connectorUpdateLimitsResponse = new ProcedureResponseAS();

		try {

			IProcedureRequest anOriginalRequestLimits = new ProcedureRequestAS();

			anOriginalRequestLimits.addInputParam("@i_jsonRequest", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("requestBody").toString());

			anOriginalRequestLimits.addOutputParam("@o_responseCode", ICTSTypes.SQLVARCHAR, "X");
			anOriginalRequestLimits.addOutputParam("@o_message", ICTSTypes.SQLVARCHAR, "X");
			anOriginalRequestLimits.addOutputParam("@o_success", ICTSTypes.SQLVARCHAR, "X");
			anOriginalRequestLimits.addOutputParam("@o_responseBody", ICTSTypes.SQLVARCHAR, "X");

			anOriginalRequestLimits.addFieldInHeader("com.cobiscorp.cobis.csp.services.ICSPExecutorConnector", ICOBISTS.HEADER_STRING_TYPE, "(service.identifier=CISConnectorUpdateLimits)");
			anOriginalRequestLimits.addFieldInHeader("csp.skip.transformation", ICOBISTS.HEADER_STRING_TYPE, "Y");
			anOriginalRequestLimits.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, ICSP.SUCCESS);

			anOriginalRequestLimits.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "transformAndSend");
			anOriginalRequestLimits.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, "");
			anOriginalRequestLimits.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequestLimits.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequestLimits.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingTransformationProvider");

			// SE HACE LA LLAMADA AL CONECTOR
			aBagSPJavaOrchestration.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorUpdateLimits)");
			anOriginalRequestLimits.setSpName("cob_procesador..sp_conn_update_limits");

			anOriginalRequestLimits.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18700126");
			anOriginalRequestLimits.addFieldInHeader("trn", ICOBISTS.HEADER_STRING_TYPE, "18700126");
			anOriginalRequestLimits.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "18700126");

			connectorUpdateLimitsResponse = executeProvider(anOriginalRequestLimits, aBagSPJavaOrchestration);

			if (logger.isDebugEnabled()){
				logger.logDebug("connectorUpdateLimitsResponse ->" + connectorUpdateLimitsResponse.toString());
			}


			String responseCode = connectorUpdateLimitsResponse.readValueParam("@o_responseCode") == null ? "0" : connectorUpdateLimitsResponse.readValueParam("@o_responseCode");
			String message = connectorUpdateLimitsResponse.readValueParam("@o_message") == null ? "Error" : connectorUpdateLimitsResponse.readValueParam("@o_message");
			String success = connectorUpdateLimitsResponse.readValueParam("@o_success") == null ? "false" : connectorUpdateLimitsResponse.readValueParam("@o_success");
			String responseBody = connectorUpdateLimitsResponse.readValueParam("@o_responseBody") == null ? "{}" : connectorUpdateLimitsResponse.readValueParam("@o_responseBody");


			aBagSPJavaOrchestration.put("responseCodeGetLimits", responseCode);
			aBagSPJavaOrchestration.put("messageSaveLimits", message);
			aBagSPJavaOrchestration.put("successSaveLimits", success);
			aBagSPJavaOrchestration.put("responseBodySaveLimits", responseBody);


			if(logger.isDebugEnabled()){
				logger.logDebug("responseCode:: " + responseCode);
				logger.logDebug("message:: " + message);
				logger.logDebug("success:: " + success);
				logger.logDebug("responseBody:: " + responseBody);
			}

			registerResponse(aRequest, aBagSPJavaOrchestration);

		}catch (Exception e) {
			logger.logError(" Error en callSaveLimitsConn: " + e.getMessage());
		}
		return connectorUpdateLimitsResponse;
	}

	private IProcedureResponse callGetLimits(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration){
		if(logger.isDebugEnabled())
			logger.logDebug(" callGetLimitsConn [INI]");
		
		IProcedureResponse connectorGetLimitsResponse = new ProcedureResponseAS();
		try {

			IProcedureRequest anOriginalRequestLimits = new ProcedureRequestAS();

			anOriginalRequestLimits.addInputParam("@i_transactionType", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_transType"));
			anOriginalRequestLimits.addInputParam("@i_transactionSubType", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_transSubType"));

			anOriginalRequestLimits.addInputParam("@i_externalCustomerId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_externalCustomerId"));
			//anOriginalRequestLimits.addInputParam("@i_contactId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_contactId"));

			anOriginalRequestLimits.addOutputParam("@o_responseCode", ICTSTypes.SQLVARCHAR, "X");
			anOriginalRequestLimits.addOutputParam("@o_message", ICTSTypes.SQLVARCHAR, "X");
			anOriginalRequestLimits.addOutputParam("@o_success", ICTSTypes.SQLVARCHAR, "X");

			anOriginalRequestLimits.addFieldInHeader("com.cobiscorp.cobis.csp.services.ICSPExecutorConnector", ICOBISTS.HEADER_STRING_TYPE, "(service.identifier=CISConnectorGetLimits)");
			anOriginalRequestLimits.addFieldInHeader("csp.skip.transformation", ICOBISTS.HEADER_STRING_TYPE, "Y");
			anOriginalRequestLimits.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, ICSP.SUCCESS);

			anOriginalRequestLimits.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "transformAndSend");
			anOriginalRequestLimits.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, "");
			anOriginalRequestLimits.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequestLimits.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequestLimits.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingTransformationProvider");

			// SE HACE LA LLAMADA AL CONECTOR
			aBagSPJavaOrchestration.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorGetLimits)");
			anOriginalRequestLimits.setSpName("cob_procesador..sp_conn_get_limits");

			anOriginalRequestLimits.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18700128");
			anOriginalRequestLimits.addFieldInHeader("trn", ICOBISTS.HEADER_STRING_TYPE, "18700128");
			anOriginalRequestLimits.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "18700128");

			connectorGetLimitsResponse = executeProvider(anOriginalRequestLimits, aBagSPJavaOrchestration);

			if (logger.isDebugEnabled()){
				logger.logDebug("connectorGetLimitsResponse ->" + connectorGetLimitsResponse.toString());
			}

			String responseCode = connectorGetLimitsResponse.readValueParam("@o_responseCode") == null ? "0" : connectorGetLimitsResponse.readValueParam("@o_responseCode");
			String message = connectorGetLimitsResponse.readValueParam("@o_message") == null ? "Error" : connectorGetLimitsResponse.readValueParam("@o_message");
			String success = connectorGetLimitsResponse.readValueParam("@o_success") == null ? "false" : connectorGetLimitsResponse.readValueParam("@o_success");
			String responseBody = connectorGetLimitsResponse.readValueParam("@o_responseBody") == null ? "{}" : connectorGetLimitsResponse.readValueParam("@o_responseBody");
			String queryString = connectorGetLimitsResponse.readValueParam("@o_queryString") == null ? "" : connectorGetLimitsResponse.readValueParam("@o_queryString");

			aBagSPJavaOrchestration.put("responseCodeGetLimits", responseCode);
			aBagSPJavaOrchestration.put("messageGetLimits", message);
			aBagSPJavaOrchestration.put("successGetLimits", success);
			aBagSPJavaOrchestration.put("responseBodyGetLimits", responseBody);
			aBagSPJavaOrchestration.put("queryString", queryString);

			if(logger.isDebugEnabled()){
				logger.logDebug("responseCode:: " + responseCode);
				logger.logDebug("message:: " + message);
				logger.logDebug("success:: " + success);
				logger.logDebug("responseBody:: " + responseBody);
				logger.logDebug("queryString:: " + queryString);
			}

			registerResponse(aRequest, aBagSPJavaOrchestration);
			return connectorGetLimitsResponse;

		}catch (Exception e) {
			logger.logError(" Error en callGetLimitsConn: " + e.getMessage());
		}
		return connectorGetLimitsResponse;
	}

	private JsonObject createRequestBody(Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureRequest originalProcedureRequest = (IProcedureRequest) aBagSPJavaOrchestration.get("originalProcedureRequest");
		//String accion =  originalProcedureRequest.readValueParam("@i_accion");

		JsonObject jsonRequest = new JsonObject();

		jsonRequest.addProperty("externalCustomerId", Integer.parseInt(originalProcedureRequest.readValueParam("@i_externalCustomerId")));
		jsonRequest.addProperty("accountNumber", originalProcedureRequest.readValueParam("@i_accountNumber"));
		jsonRequest.addProperty("limitType", originalProcedureRequest.readValueParam("@i_limitType"));

		String amount = originalProcedureRequest.readValueParam("@i_amount");
		String currency = originalProcedureRequest.readValueParam("@i_currency");

		if ( (amount != null && !amount.isEmpty()) && (currency != null && !currency.isEmpty()) ) {
			JsonObject limit = new JsonObject();
			limit.addProperty("amount", Double.parseDouble(originalProcedureRequest.readValueParam("@i_amount")));
			limit.addProperty("currency", originalProcedureRequest.readValueParam("@i_currency"));
			jsonRequest.add("limit", limit);
		}
		return jsonRequest;
	}

	private void registerResponse(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(" Entrando en registerResponse get");
		}

		request.setSpName("cob_bvirtual..sp_log_configuracion_limite");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "I");
		if(aBagSPJavaOrchestration.get("operation").equals("C")) {
			request.addInputParam("@i_servicio", ICTSTypes.SQLVARCHAR, "fetchTransactionLimit");
			//request.addInputParam("@i_ssn", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_ssn"));
			request.addInputParam("@i_request", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("queryString").toString());
			request.addInputParam("@i_response", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("responseBodyGetLimits").toString());
		} else if (aBagSPJavaOrchestration.get("operation").equals("S")) {
			request.addInputParam("@i_servicio", ICTSTypes.SQLVARCHAR, "saveTransactionLimit");
			//request.addInputParam("@i_ssn", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_ssn"));
			request.addInputParam("@i_request", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("requestBody").toString());
			request.addInputParam("@i_response", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("responseBodySaveLimits").toString());
		}

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response registerResponse get: " + wProductsQueryResp.getProcedureResponseAsString());
		}
	}


	public IProcedureResponse processResponseError(IProcedureResponse anOriginalProcedureRes) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(" start processResponseAccounts--->");
		}

		IProcedureResponse anOriginalProcedureResponse = new ProcedureResponseAS();

		// Agregar Header 1
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData2.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));

		// Agregar Header 2
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));

		String message = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 3).isNull() ? "Service execution error" : anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 3).getValue();
		String success = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).isNull() ? "false" : anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue();
		String code = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 2).isNull() ? "400218" : anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 2).getValue();


		IResultSetRow row = new ResultSetRow();
		row.addRowData(1, new ResultSetRowColumnData(false, success));
		data.addRow(row);
		
		IResultSetRow row2 = new ResultSetRow();
		row2.addRowData(1, new ResultSetRowColumnData(false, code));
		row2.addRowData(2, new ResultSetRowColumnData(false, message));
		data2.addRow(row2);

		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);

		anOriginalProcedureResponse.setReturnCode(200);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock2);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock);
		logger.logInfo("no se rompe");
		return anOriginalProcedureResponse;
	}

	public IProcedureResponse processTransforResponse(IProcedureResponse anOriginalProcedureRes, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(" start processTransforResponse getClientLimits--->");
		}



		IProcedureResponse anOriginalProcedureResponse = new ProcedureResponseAS();

		// Agregar Header 1
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData2.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));

		// Agregar Header 2
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));

		// Agregar Header 3
		IResultSetHeader metaData3 = new ResultSetHeader();
		IResultSetData data3 = new ResultSetData();

		// Agregar Header 4
		IResultSetHeader metaData4 = new ResultSetHeader();
		IResultSetData data4 = new ResultSetData();

		String requestBody = null;
		if(aBagSPJavaOrchestration.get("responseBodyGetLimits") != null  && !aBagSPJavaOrchestration.get("responseBodyGetLimits").toString().equals("{}")){
			requestBody = aBagSPJavaOrchestration.get("responseBodyGetLimits").toString();
		}

		if(requestBody != null){
			JsonParser jsonParser = new JsonParser();

			String jsonRequestStringClean = requestBody.replace("&quot;", "\"");
			logger.logInfo("jsonRequestStringClean:: " + jsonRequestStringClean );
			JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonRequestStringClean);
			logger.logInfo("jsonObject:: " + jsonObject );


			//metaData3 = new ResultSetHeader();
			//data3 = new ResultSetData();

			metaData3.addColumnMetaData(new ResultSetHeaderColumn("externalCustomerId", ICTSTypes.SQLINT4, 8));
			metaData3.addColumnMetaData(new ResultSetHeaderColumn("accountNumber", ICTSTypes.SQLVARCHAR, 100));
			metaData3.addColumnMetaData(new ResultSetHeaderColumn("transactionType", ICTSTypes.SQLVARCHAR, 100));


			//metaData4 = new ResultSetHeader();
			//data4 = new ResultSetData();

			metaData4.addColumnMetaData(new ResultSetHeaderColumn("transactionSubType", ICTSTypes.SQLVARCHAR, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("transactionLimitsType", ICTSTypes.SQLVARCHAR, 100));
			metaData4.addColumnMetaData(new ResultSetHeaderColumn("configuredLimit", ICTSTypes.SQLVARCHAR, 100));

			String externalCustomerId = jsonObject.get("externalCustomerId").getAsString();
			String 	accountNumber = jsonObject.get("accountNumber").getAsString();
			String 	transactionType = jsonObject.get("transactionType").getAsString();
			logger.logInfo("externalCustomerId::: " + externalCustomerId);
			logger.logInfo("accountNumber::: " + accountNumber);
			logger.logInfo("transactionType::: " + transactionType);

			JsonArray transactionLimits = jsonObject.getAsJsonArray("transactionLimits");
			logger.logInfo("LENGTH::" + transactionLimits.size());

			Double dailyLimit = null;
			Double montlyLimit = null;
			Double balanceAmountMontly = null;
			Double maxTxnLimit = null;


			IResultSetRow row3 = new ResultSetRow();
			row3.addRowData(1, new ResultSetRowColumnData(false, externalCustomerId));
			row3.addRowData(2, new ResultSetRowColumnData(false, accountNumber));
			row3.addRowData(3, new ResultSetRowColumnData(false, transactionType));
			data3.addRow(row3);

			int contador = 0;
			for (JsonElement limitElement : transactionLimits) {

				JsonArray subTypeLimits = limitElement.getAsJsonObject().getAsJsonArray("transactionSubTypeLimits");
				IResultSetRow[] limitRows = new IResultSetRow[subTypeLimits.size()];
				for (JsonElement subTypeElement : subTypeLimits) {
					IResultSetRow limitRow = new ResultSetRow();
					limitRow.addRowData(1, new ResultSetRowColumnData(false, "MISMO"));
					// logger.logInfo("CONTADOR: " + contador);
					String limitType = subTypeElement.getAsJsonObject().get("transactionLimitsType").getAsString();
					limitRow.addRowData(2, new ResultSetRowColumnData(false, limitType));

					//subTypeElement.getAsJsonObject().getAsJsonObject("configuredLimit").get("amount").getAsDouble();
					//subTypeElement.getAsJsonObject().getAsJsonObject("configuredLimit").get("currency").getAsDouble();

					limitRow.addRowData(3, new ResultSetRowColumnData(false, subTypeElement.getAsJsonObject().getAsJsonObject("configuredLimit").get("amount").getAsString())); // configure LIMIT
					limitRow.addRowData(4, new ResultSetRowColumnData(false, subTypeElement.getAsJsonObject().getAsJsonObject("configuredLimit").get("currency").getAsString())); // configure LIMIT
					limitRow.addRowData(5, new ResultSetRowColumnData(false, subTypeElement.getAsJsonObject().getAsJsonObject("balanceAmount").get("amount").getAsString())); // balance ammount
					limitRow.addRowData(6, new ResultSetRowColumnData(false, subTypeElement.getAsJsonObject().getAsJsonObject("balanceAmount").get("currency").getAsString())); // balance ammount

					if (subTypeElement.getAsJsonObject().has("userConfiguredLimit")) {
						limitRow.addRowData(7, new ResultSetRowColumnData(false, subTypeElement.getAsJsonObject().getAsJsonObject("userConfiguredLimit").get("amount").getAsString())); // user configured limit amount
						limitRow.addRowData(8, new ResultSetRowColumnData(false, subTypeElement.getAsJsonObject().getAsJsonObject("userConfiguredLimit").get("currency").getAsString())); // user configured limit currency
					}

					data4.addRow(limitRow);
					/*
					if ("DAILY".equals(limitType)) {

						dailyLimit = subTypeElement.getAsJsonObject()
								.getAsJsonObject("userConfiguredLimit")
								.get("amount").getAsDouble();
					}else if("MONTHLY".equals(limitType)){
						montlyLimit = subTypeElement.getAsJsonObject()
								.getAsJsonObject("userConfiguredLimit")
								.get("amount").getAsDouble();

						balanceAmountMontly = subTypeElement.getAsJsonObject()
								.getAsJsonObject("balanceAmount")
								.get("amount").getAsDouble();
					} else if ("MAX_TXN_LIMIT".equals(limitType)) {
						maxTxnLimit = subTypeElement.getAsJsonObject()
								.getAsJsonObject("userConfiguredLimit")
								.get("amount").getAsDouble();
					} else if("MIN_TXN_LIMIT".equals(limitType)){

					}
					*/
					contador ++;
				}
			}

			/*
			IResultSetRow[] limitRows = new IResultSetRow[transactionLimits.size()];

			for (int i = 0; i < transactionLimits.size(); i++) {
				//JsonObject limitElement = transactionLimits.get(i).getAsJsonObject();

				// Crea una nueva fila para cada límite
				IResultSetRow limitRow = new ResultSetRow();

				// Extrae el subtipo de transacción

				// Llena la fila con los datos relevantes
				limitRow.addRowData(1, new ResultSetRowColumnData(false, "hola")); // transactionSubType
				limitRow.addRowData(2, new ResultSetRowColumnData(false, "hola1")); // transactionLimitsType -DAYLY, MONTLY
				limitRow.addRowData(3, new ResultSetRowColumnData(false, "23")); // configure LIMIT
				limitRow.addRowData(4, new ResultSetRowColumnData(false, "MXN")); // configure LIMIT
				limitRow.addRowData(4, new ResultSetRowColumnData(false, "28")); // balance ammount
				limitRow.addRowData(4, new ResultSetRowColumnData(false, "MXN")); // balance ammount

				// Agrega la fila al conjunto de datos
				data4.addRow(limitRow);
			}
			*/
		}


		String message = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 3).isNull() ? "Service execution error" : anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 3).getValue();
		String success = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).isNull() ? "false" : anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue();
		String code = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 2).isNull() ? "400218" : anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 2).getValue();


		IResultSetRow row = new ResultSetRow();
		row.addRowData(1, new ResultSetRowColumnData(false, success));
		data.addRow(row);

		IResultSetRow row2 = new ResultSetRow();
		row2.addRowData(1, new ResultSetRowColumnData(false, code));
		row2.addRowData(2, new ResultSetRowColumnData(false, message));
		data2.addRow(row2);



		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);

		IResultSetBlock resultsetBlock3 = null;
		IResultSetBlock resultsetBlock4 = null;
		if(requestBody != null) {
			resultsetBlock3 = new ResultSetBlock(metaData3, data3);
			resultsetBlock4 = new ResultSetBlock(metaData4, data4);
		}

		anOriginalProcedureResponse.setReturnCode(200);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock2);
		if(requestBody != null) {
			anOriginalProcedureResponse.addResponseBlock(resultsetBlock3);
			anOriginalProcedureResponse.addResponseBlock(resultsetBlock4);
		}

		return anOriginalProcedureResponse;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		return null;
	}	

}
