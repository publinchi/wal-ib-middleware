/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.batch.compensation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.json.JSONObject;

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
import com.cobiscorp.ecobis.orchestration.core.batch.compensation.Compensation.Content;
import com.cobiscorp.ecobis.orchestration.core.batch.compensation.Compensation.Content.Clearing;
import com.cobiscorp.ecobis.orchestration.core.batch.compensation.Compensation.Content.Transaction;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;

/**
 * @author nelsonJ
 * @since May 2, 2023
 * @version 1.0.0
 */
@Component(name = "BatchCompensationOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "BatchCompensationOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "BatchCompensationOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_batch_compensation")
})
public class BatchCompensationOrchestrationCore extends SPJavaOrchestrationBase {
	
	private ILogger logger = (ILogger) this.getLogger();
	private static final String CLASS_NAME = "BatchCompensationOrchestrationCore";
	protected static final String CHANNEL_REQUEST = "8";
	protected static final String RUTA_COMPENSATION	= "/cobis/cobishome/compensation";

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
		
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		logger.logDebug("Begin flow, BatchCompensationOrchestrationCore start.");		
		
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		
		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();
		
		anProcedureResponse = executeCompensation(anOriginalRequest, aBagSPJavaOrchestration);
		/*
		if(anProcedureResponse.getReturnCode()==0){
			
			anProcedureResponse = processResponseApi(anProcedureResponse,aBagSPJavaOrchestration);
		}
		*/
		return anProcedureResponse;
		//return processResponseCardAppl(anProcedureResponse);
	}
	
	private IProcedureResponse executeCompensation(IProcedureRequest aRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en executeCompensation: " );
		}
		//aBagSPJavaOrchestration.put("ente_mis", aRequest.readValueParam("@i_ente"));
		//aBagSPJavaOrchestration.put("account_number", aRequest.readValueParam("@i_account_number"));
		
		IProcedureResponse wAccountsResp = new ProcedureResponseAS();
		
		deleteFiles(aBagSPJavaOrchestration);
		
		execDownloadFile(aRequest, aBagSPJavaOrchestration);
		
		jsonProcess(aRequest, aBagSPJavaOrchestration);
		//processFiles(aRequest, aBagSPJavaOrchestration);

		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Response " + wAccountsResp.toString());
			logger.logInfo(CLASS_NAME + " Saliendo de executeCompensation");
		}

		return wAccountsResp;
	}
	
	private IProcedureResponse executeUpdateCard(IProcedureRequest anOriginalReq, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse connectorCardResponse = null;
		String idCardDock = null, status = null, reasonStatus = null, acccountNumber = null;
		
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		aBagSPJavaOrchestration.remove("trn_virtual");
		
		idCardDock = aBagSPJavaOrchestration.containsKey("o_id_card_dock")? aBagSPJavaOrchestration.get("o_id_card_dock").toString():null;
		status = aBagSPJavaOrchestration.containsKey("o_detail_status")? aBagSPJavaOrchestration.get("o_detail_status").toString():null;
		reasonStatus = aBagSPJavaOrchestration.containsKey("o_det_reason_stat")? aBagSPJavaOrchestration.get("o_det_reason_stat").toString():"X";
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en executeUpdateCard " + acccountNumber);
		}
		try {
			// PARAMETROS DE ENTRADA
			anOriginalRequest.addInputParam("@i_ente", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("ente_mis").toString());
			anOriginalRequest.addInputParam("@i_id_card_dock", ICTSTypes.SQLVARCHAR, idCardDock);
			anOriginalRequest.addInputParam("@i_status", ICTSTypes.SQLVARCHAR, status);
			anOriginalRequest.addInputParam("@i_reason_status", ICTSTypes.SQLVARCHAR, reasonStatus);
			anOriginalRequest.addInputParam("@i_type_card", ICTSTypes.SQLVARCHAR, anOriginalReq.readValueParam("@i_type_card"));
			anOriginalRequest.addInputParam("@i_operation", ICTSTypes.SQLVARCHAR, "UCS");
			
			// VARIABLES DE SALIDA
			anOriginalRequest.addOutputParam("@o_cod_respuesta", ICTSTypes.SQLVARCHAR, "0");
			anOriginalRequest.addOutputParam("@o_desc_respuesta", ICTSTypes.SQLVARCHAR, "X");
			
			anOriginalRequest.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
					"(service.identifier=BatchCompensationOrchestrationCore)");
			anOriginalRequest.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "executeTransaction");
			anOriginalRequest.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, "");
			anOriginalRequest.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequest.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequest.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingOrchestrator");

			anOriginalRequest.addFieldInHeader("trn", ICOBISTS.HEADER_STRING_TYPE, "18500112");
			anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500112");

			anOriginalRequest.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "18500112");
			
			// SE HACE LA LLAMADA AL CONECTOR
			aBagSPJavaOrchestration.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorDock)");
			anOriginalRequest.setSpName("cob_procesador..sp_card_status_api");

			anOriginalRequest.addInputParam("@trn_virtual", ICTSTypes.SYBINT4, "18500112");
			anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500112");

			logger.logDebug("cardDock--> request update card app: " + anOriginalRequest.toString());
			// SE EJECUTA CONECTOR
			connectorCardResponse = executeProvider(anOriginalRequest, aBagSPJavaOrchestration);

			if (logger.isDebugEnabled())
				logger.logDebug("jcos--> connectorUpdateCardApplicationResponse: " + connectorCardResponse);

			if (connectorCardResponse.readValueParam("@o_card_id") != null)
				aBagSPJavaOrchestration.put("o_card_id", connectorCardResponse.readValueParam("@o_card_id"));
			else
				aBagSPJavaOrchestration.put("o_card_id", "null");

			if (connectorCardResponse.readValueParam("@o_success") != null)
				aBagSPJavaOrchestration.put("o_success", connectorCardResponse.readValueParam("@o_card_id"));
			
		} catch (Exception e) {
			e.printStackTrace();
			connectorCardResponse = null;
			logger.logInfo(CLASS_NAME +" Error Catastrofico de updateCardStatusExecution");

		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + "--> Saliendo de updateCardStatusExecution");
			}
		}

		return connectorCardResponse;

	}

	private IProcedureResponse executeAssingCard(IProcedureRequest anOriginalReq, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse connectorCardResponse = null;
		String typeCard = null;
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		
		aBagSPJavaOrchestration.remove("trn_virtual");
		typeCard = anOriginalReq.readValueParam("@i_type_card").equals("VI")?"VIRTUAL":"PHYSICAL";
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en executeAssingCard typeCard: " + typeCard);
		}
		try {
			// PARAMETROS DE ENTRADA
			anOriginalRequest.addInputParam("@i_ente", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("ente_mis").toString());
			if (typeCard.equals("PHYSICAL")){
				anOriginalRequest.addInputParam("@i_id_card_dock", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_card_available").toString());
			}else{
				anOriginalRequest.addInputParam("@i_id_card_dock", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_id_card_dock").toString());
				anOriginalRequest.addInputParam("@i_status", ICTSTypes.SQLVARCHAR, "NORMAL");
			}
			anOriginalRequest.addInputParam("@i_reason_status", ICTSTypes.SQLVARCHAR, "X");
			anOriginalRequest.addInputParam("@i_id_person_dock", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_id_person_dock").toString());
			anOriginalRequest.addInputParam("@i_id_account_dock", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_id_account_dock").toString());
			
			anOriginalRequest.addInputParam("@i_type_card", ICTSTypes.SQLVARCHAR, typeCard);
			anOriginalRequest.addInputParam("@i_operation", ICTSTypes.SQLVARCHAR, "ASC");
			anOriginalRequest.addInputParam("@i_account", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_account_number").toString());
			
			// VARIABLES DE SALIDA
			anOriginalRequest.addOutputParam("@o_cod_respuesta", ICTSTypes.SQLVARCHAR, "0");
			anOriginalRequest.addOutputParam("@o_desc_respuesta", ICTSTypes.SQLVARCHAR, "X");
			
			anOriginalRequest.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
					"(service.identifier=BatchCompensationOrchestrationCore)");
			anOriginalRequest.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "executeTransaction");
			anOriginalRequest.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, "");
			anOriginalRequest.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequest.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequest.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingOrchestrator");

			anOriginalRequest.addFieldInHeader("trn", ICOBISTS.HEADER_STRING_TYPE, "18500112");
			anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500112");

			anOriginalRequest.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "18500112");
			
			// SE HACE LA LLAMADA AL CONECTOR
			aBagSPJavaOrchestration.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorDock)");
			anOriginalRequest.setSpName("cob_procesador..sp_card_status_api");

			anOriginalRequest.addInputParam("@trn_virtual", ICTSTypes.SYBINT4, "18500112");
			anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500112");

			logger.logDebug("cardDock--> request executeAssingCard app: " + anOriginalRequest.toString());
			// SE EJECUTA CONECTOR
			connectorCardResponse = executeProvider(anOriginalRequest, aBagSPJavaOrchestration);

			if (logger.isDebugEnabled())
				logger.logDebug("Dock--> connectorUpdateCardApplicationResponse: " + connectorCardResponse);

			if (connectorCardResponse.readValueParam("@o_person_id") != null)
				aBagSPJavaOrchestration.put("o_person_id", connectorCardResponse.readValueParam("@o_person_id"));
			else
				aBagSPJavaOrchestration.put("o_person_id", "null");
			
			if (connectorCardResponse.readValueParam("@o_account_id") != null)
				aBagSPJavaOrchestration.put("o_account_id", connectorCardResponse.readValueParam("@o_account_id"));
			else
				aBagSPJavaOrchestration.put("o_account_id", "null");
			
			if (connectorCardResponse.readValueParam("@o_card_id") != null)
				aBagSPJavaOrchestration.put("o_card_id", connectorCardResponse.readValueParam("@o_card_id"));
			else
				aBagSPJavaOrchestration.put("o_card_id", "null");
			
			if (connectorCardResponse.readValueParam("@o_card_type") != null)
				aBagSPJavaOrchestration.put("o_card_type", connectorCardResponse.readValueParam("@o_card_type"));
			else
				aBagSPJavaOrchestration.put("o_card_type", "null");
			
			if (connectorCardResponse.readValueParam("@o_assign_date") != null)
				aBagSPJavaOrchestration.put("o_assign_date", connectorCardResponse.readValueParam("@o_assign_date"));
			else
				aBagSPJavaOrchestration.put("o_assign_date", "null");
			
			if (connectorCardResponse.readValueParam("@o_requestAssingCard") != null)
				aBagSPJavaOrchestration.put("o_requestAssingCard", connectorCardResponse.readValueParam("@o_requestAssingCard"));
			else
				aBagSPJavaOrchestration.put("o_requestAssingCard", "null");
			
			if (connectorCardResponse.readValueParam("@o_responseAssingCard") != null)
				aBagSPJavaOrchestration.put("o_responseAssingCard", connectorCardResponse.readValueParam("@o_responseAssingCard"));
			else
				aBagSPJavaOrchestration.put("o_responseAssingCard", "null");
			
			if (connectorCardResponse.readValueParam("@o_card_status") != null)
				aBagSPJavaOrchestration.put("o_card_status", connectorCardResponse.readValueParam("@o_card_status"));
			else
				aBagSPJavaOrchestration.put("o_card_status", "null");
			
			if (connectorCardResponse.readValueParam("@o_requestUpdateCard") != null)
				aBagSPJavaOrchestration.put("o_requestUpdateCard", connectorCardResponse.readValueParam("@o_requestUpdateCard"));
			else
				aBagSPJavaOrchestration.put("o_requestUpdateCard", "null");
			
			if (connectorCardResponse.readValueParam("@o_responseUpdateCard") != null)
				aBagSPJavaOrchestration.put("o_responseUpdateCard", connectorCardResponse.readValueParam("@o_responseUpdateCard"));
			else
				aBagSPJavaOrchestration.put("o_responseUpdateCard", "null");
			

			if (connectorCardResponse.readValueParam("@o_success") != null)
				aBagSPJavaOrchestration.put("o_success", connectorCardResponse.readValueParam("@o_success"));
			else
				aBagSPJavaOrchestration.put("o_success", "null");

			
		} catch (Exception e) {
			e.printStackTrace();
			connectorCardResponse = null;
			logger.logInfo(CLASS_NAME +" Error Catastrofico de executeAssingCard");

		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + "--> Saliendo de executeAssingCard");
			}
		}

		return connectorCardResponse;

	}

	private Compensation processFiles(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		
		Compensation jsonObject = null;
		Map<String, Object> responseTransactionBag = new HashMap<String, Object>();
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en processFiles");
		}

		
		// String filePath = "D:\\Walmart\\Varios\\data_compensation.json";

	       String folderPath = "/cob/cobhome/compensation"; // Ruta de la carpeta que contiene los archivos .json

	        File folder = new File(folderPath);

	        if (folder.exists() && folder.isDirectory()) {
	            File[] files = folder.listFiles();

	            if (files != null) {
	                Gson gson = new Gson();
	                
	                for (File file : files) {
	                    if (file.isFile() && file.getName().endsWith(".json")) {
	                        try {
	                            FileReader reader = new FileReader(file);
	                            // Lee el archivo JSON y conviértelo a un objeto Compensation
	                            Compensation compensation = gson.fromJson(reader, Compensation.class);
	                            logger.logInfo("Nombre del archivo: " + file.getName());

	                            for (Content content : compensation.getCONTENT()) {
	            	                Transaction transaction = content.getTRANSACTION();
	            	                Clearing clearing = content.getCLEARING();
	            	                
	            	                //valCardDock(content, aRequest, aBagSPJavaOrchestration);
	            	                
	            	                if(clearing.isDEBIT()!=null && clearing.isDEBIT()){
	            	                	logger.logInfo("DEBIT OPERATION");
	            	                	responseTransactionBag = queryAccountDebitOperation(content, aBagSPJavaOrchestration);
	            	                }
	            	                else if(clearing.isCREDIT()!=null && clearing.isCREDIT()){
	            	                	logger.logInfo("CREDIT OPERATION");
	            	                	responseTransactionBag = queryAccountCreditOperation(content, aBagSPJavaOrchestration);
	            	                }
	            	                		
	            	                logger.logInfo("Bag response: " + responseTransactionBag.toString()); 
	            	                logger.logInfo("ARN: " + transaction.getARN());
	            	                logger.logInfo("ID_CARDBRAND: " + transaction.getID_CARDBRAND());
	            	                
	            	            }
	                            
	                            reader.close(); // Cierra el FileReader cuando hayas terminado con él
	                        } catch (IOException e) {
	                        	logger.logInfo("Error al procesar el archivo: " + file.getName());
	                            e.printStackTrace();
	                        }
	                    }
	                }
	            }
	            else{
	            	logger.logInfo("No existe archivos a procesar. ");
	            }
	        } else {
	        	logger.logInfo("La carpeta especificada no existe o no es una carpeta válida.");
	        }
	        
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de processFiles");
		}

		return jsonObject;
	}
	
	private void jsonProcess(IProcedureRequest anOriginalProcedureRes, Map<String, Object> aBagSPJavaOrchestration) {
		
		logger.logInfo(CLASS_NAME + " Entrando en jsonProcess: " );
		aBagSPJavaOrchestration.put("concept", "Exchange difference adjustment");
		
		Map<String, Object> responseTransactionBag = new HashMap<String, Object>();
        String folderPath = RUTA_COMPENSATION;
        Integer count = 0;
        File folder = new File(folderPath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();

            if (files != null) {
                Gson gson = new Gson();

                for (File file : files) {
                    if (file.isFile() && file.getName().endsWith(".json")) {
                    	JsonReader reader = null;
                        try {
                        	reader = new JsonReader(new FileReader(file));
                        	
                            JsonElement rootElement = gson.fromJson(reader, JsonElement.class);

                            // Verifica si el objeto raíz es un objeto JSON
                            if (rootElement.isJsonObject()) {
                                JsonObject rootObject = rootElement.getAsJsonObject();

                                // Accede a los campos específicos que necesitas
                                String fileId = getStringOrNull(rootObject, "FILE_ID");
                                aBagSPJavaOrchestration.put("fileId", fileId);
                                int recordsTotal = getIntOrDefault(rootObject, "RECORDS_TOTAL", 0);
                                aBagSPJavaOrchestration.put("recordsTotal", recordsTotal);
                                String fileName = getStringOrNull(rootObject, "FILENAME");
                                aBagSPJavaOrchestration.put("fileName", fileName);
                                String referenceDate = getStringOrNull(rootObject, "REFERENCE_DATE");
                                aBagSPJavaOrchestration.put("referenceDate", referenceDate);
                                int sequence = getIntOrDefault(rootObject, "SEQUENCE",0);
                                aBagSPJavaOrchestration.put("sequence", sequence);

                                registerInitialLogBd(aBagSPJavaOrchestration);
                                
                                // Objeto Content
                                if (rootObject.has("CONTENT")) {
                                    JsonElement contentElement = rootObject.get("CONTENT");
                                    if (contentElement.isJsonArray()) {
                                    	
                                    	
                                        for (JsonElement content : contentElement.getAsJsonArray()) {
                                        	count = count + 1;
                                            if (content.isJsonObject()) {
                                                JsonObject contentObject = content.getAsJsonObject();
                                                String contentId = getStringOrNull(contentObject, "ID");
                                                aBagSPJavaOrchestration.put("contentId", contentId);
                                                // Objeto Transaction
                                                if (contentObject.has("TRANSACTION")) {
                                                    JsonObject transactionObject = contentObject.getAsJsonObject("TRANSACTION");
                                                    
                                                    String cardId = getStringOrNull(transactionObject, "CARD_ID");
                                                    aBagSPJavaOrchestration.put("cardId", cardId);
                                                    
                                                    String gmtDate = getStringOrNull(transactionObject, "GMT_DATE");
                                                    aBagSPJavaOrchestration.put("gmtDate", gmtDate);
                                                    
                                                    String authorization = getStringOrNull(transactionObject, "AUTHORIZATION");
                                                    aBagSPJavaOrchestration.put("referenceNumber", authorization);
                                                    
                                                    int sourceCurrency = getIntOrDefault(transactionObject, "SOURCE_CURRENCY", 0);
                                                    aBagSPJavaOrchestration.put("sourceCurrency", sourceCurrency);
                                                    
                                                    double sourceValue = getDoubleOrDefault(transactionObject, "SOURCE_VALUE", 0.0);
                                                    aBagSPJavaOrchestration.put("sourceValue", sourceValue);
                                                    
                                                    int destCurrency = getIntOrDefault(transactionObject, "DEST_CURRENCY", 0);
                                                    aBagSPJavaOrchestration.put("destCurrency", destCurrency);
                                                    
                                                    double destValue = getDoubleOrDefault(transactionObject, "DEST_VALUE", 0.0);
                                                    aBagSPJavaOrchestration.put("destValue", destValue);

                                                    Double result = sourceValue - destValue;
                                                    Double amountAbs = Math.abs(result);
                                                    aBagSPJavaOrchestration.put("amount", amountAbs);
                                                    
                                                    if(result < 0){
                                                    	valCardDock(anOriginalProcedureRes, aBagSPJavaOrchestration);
                                                    	responseTransactionBag = queryAccountDebitOperation(aBagSPJavaOrchestration);
                                                    }else if(result > 0){
                                                    	valCardDock(anOriginalProcedureRes, aBagSPJavaOrchestration);
                                                    	responseTransactionBag = queryAccountCreditOperation(aBagSPJavaOrchestration);
                                                    }
                                                    
                                                    // Hacer algo con los valores obtenidos
                                                    logger.logInfo("FILE_ID: " + fileId);
                                                    logger.logInfo("Content ID: " + contentId);
                                                    logger.logInfo("AMOUNT : " + result);
                                                     logger.logInfo("Transaction - CARD_ID: " + cardId);
                                                     logger.logInfo("Transaction - GMT_DATE: " + gmtDate);
                                                     logger.logInfo("Transaction - AUTHORIZATION: " + authorization);
                                                     logger.logInfo("Transaction - SOURCE_CURRENCY: " + sourceCurrency);
                                                     logger.logInfo("Transaction - SOURCE_VALUE: " + sourceValue);
                                                     logger.logInfo("Transaction - DEST_CURRENCY: " + destCurrency);
                                                     logger.logInfo("Transaction - DEST_VALUE: " + destValue);
                                                     logger.logInfo("Transaction - RECORDS_TOTAL: " + recordsTotal);
                                                     
                                                     logger.logInfo("responseTransactionBag: " + responseTransactionBag);
                                                     
                                                     registerTransactionLogBd(aBagSPJavaOrchestration, responseTransactionBag);
                                                }
                                            }
                                        }
                                    }
                                }
                                aBagSPJavaOrchestration.putAll(responseTransactionBag);
                                aBagSPJavaOrchestration.put("registrosProcesados", count);
                                registerFinalLogBd(aBagSPJavaOrchestration);
                            } // Fin validate json object
                        } catch (IOException e) {
                        	 logger.logInfo("Error al procesar el archivo: " + file.getName() + "--" + e);
                            e.printStackTrace();
                        }
                        
                    } //Fin filtro json
                }
            }
        } else {
        	 logger.logInfo("La carpeta especificada no existe o no es una carpeta válida.");
        }
        
        logger.logInfo(CLASS_NAME + " Saliendo de jsonProcess: " );
    }
	
	 // Métodos de utilidad para obtener valores de campos JSON o valores predeterminados
    private static String getStringOrNull(JsonObject jsonObject, String fieldName) {
        if (jsonObject.has(fieldName) && jsonObject.get(fieldName).isJsonPrimitive()) {
            return jsonObject.get(fieldName).getAsString();
        }
        return null;
    }

    private static int getIntOrDefault(JsonObject jsonObject, String fieldName, int defaultValue) {
        if (jsonObject.has(fieldName) && jsonObject.get(fieldName).isJsonPrimitive()) {
            return jsonObject.get(fieldName).getAsInt();
        }
        return defaultValue;
    }

    private static double getDoubleOrDefault(JsonObject jsonObject, String fieldName, double defaultValue) {
        if (jsonObject.has(fieldName) && jsonObject.get(fieldName).isJsonPrimitive()) {
            return jsonObject.get(fieldName).getAsDouble();
        }
        return defaultValue;
    }
    
	private IProcedureResponse execDownloadFile(IProcedureRequest anOriginalReq, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse connectorCardResponse = new ProcedureResponseAS();
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		
		aBagSPJavaOrchestration.remove("trn_virtual");

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en execDownloadFile");
		}
		try {
			// PARAMETROS DE ENTRADA
			//anOriginalRequest.addInputParam("@i_phone", ICTSTypes.SQLVARCHAR, anOriginalReq.readValueParam("@i_phone"));
			//anOriginalRequest.addInputParam("@i_pass", ICTSTypes.SQLVARCHAR, anOriginalReq.readValueParam("@i_pass"));
			
			// VARIABLES DE SALIDA
			//anOriginalRequest.addOutputParam("@o_cod_respuesta", ICTSTypes.SQLVARCHAR, "0");
			//anOriginalRequest.addOutputParam("@o_desc_respuesta", ICTSTypes.SQLVARCHAR, "X");
			
			anOriginalRequest.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
					"(service.identifier=BatchCompensationOrchestrationCore)");
			anOriginalRequest.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "executeTransaction");
			anOriginalRequest.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, "");
			anOriginalRequest.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequest.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequest.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingOrchestrator");

			anOriginalRequest.addFieldInHeader("trn", ICOBISTS.HEADER_STRING_TYPE, "18500144");
			anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500144");

			anOriginalRequest.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "18500144");
			
			// SE HACE LA LLAMADA AL CONECTOR
			aBagSPJavaOrchestration.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorCompensacion)");
			anOriginalRequest.setSpName("cob_procesador..sp_batch_compensation");

			anOriginalRequest.addInputParam("@trn_virtual", ICTSTypes.SYBINT4, "18500144");
			anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500144");

			if (logger.isDebugEnabled())
			logger.logDebug("Compensation--> request execDownloadFile app: " + anOriginalRequest.toString());
			// SE EJECUTA CONECTOR
			connectorCardResponse = executeProvider(anOriginalRequest, aBagSPJavaOrchestration);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.logInfo(CLASS_NAME +" Error Catastrofico de execDownloadFile");

		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + "--> Saliendo de valCredentialConector");
			}
		}

		return connectorCardResponse;

	}

	private IProcedureResponse valCardDock( IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if(logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en valCardDock");
		}
		
		request.setSpName("cob_atm..sp_bv_trn_data_compensation");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_card_id", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("cardId").toString());//content.getTRANSACTION().getCARD_ID());
		request.addInputParam("@i_value", ICTSTypes.SQLMONEY4, aBagSPJavaOrchestration.get("amount").toString()); //content.getCLEARING().getVALUE().toString());
		//request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "PURCHASE");
		
		request.addOutputParam("@o_ente", ICTSTypes.SQLINT4, "0");		
		request.addOutputParam("@o_cta", ICTSTypes.SQLVARCHAR, "X");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		aBagSPJavaOrchestration.put("ente", wProductsQueryResp.readValueParam("@o_ente"));
		aBagSPJavaOrchestration.put("account", wProductsQueryResp.readValueParam("@o_cta"));
		
		if(!wProductsQueryResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			aBagSPJavaOrchestration.put("codeErrorApi", wProductsQueryResp.getResultSetRowColumnData(2, 1, 1).getValue());
			aBagSPJavaOrchestration.put("messageError", wProductsQueryResp.getResultSetRowColumnData(2, 1, 2).getValue());
			logger.logDebug("Code Error local" +aBagSPJavaOrchestration.get("codeErrorApi"));
		}
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking valDatavalDataLocalDockLocal: " + wProductsQueryResp.getProcedureResponseAsString());
			
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de valDataLocalDock BAG:" + aBagSPJavaOrchestration);
		}

		return wProductsQueryResp;
	}

	private Map<String, Object> queryAccountCreditOperation(Map<String, Object> aBagSPJavaOrchestration) {
		
		aBagSPJavaOrchestration.put("transactionType","CREDIT");
		IProcedureRequest wQueryRequest = (IProcedureRequest) aBagSPJavaOrchestration.get("anOriginalRequest");
		//aBagSPJavaOrchestration.clear();
		String idCustomer = aBagSPJavaOrchestration.get("ente").toString();
		String accountNumber = aBagSPJavaOrchestration.get("account").toString();
		String referenceNumber = aBagSPJavaOrchestration.get("referenceNumber").toString(); //content.getTRANSACTION().getAUTHORIZATION();
		String creditConcept = aBagSPJavaOrchestration.get("concept").toString();
		String amount = aBagSPJavaOrchestration.get("amount").toString();//String.valueOf(content.getTRANSACTION().getDEST_VALUE());
		String commission = "0";
		
		Map<String, Object> responseBag = new HashMap<String, Object>();
				
		logger.logDebug("Begin flow, queryAccountCreditOperation with id: " + idCustomer);
		
		IProcedureRequest reqTMPCentral = (initProcedureRequest(wQueryRequest));		
		reqTMPCentral.setSpName("cobis..sp_account_credit_operation_central_api");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "central");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500111");
		reqTMPCentral.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
		reqTMPCentral.addInputParam("@i_accountNumber",ICTSTypes.SQLVARCHAR, accountNumber);
		reqTMPCentral.addInputParam("@i_amount",ICTSTypes.SQLMONEY, amount);
		reqTMPCentral.addInputParam("@i_commission",ICTSTypes.SQLMONEY, commission);	 
	    reqTMPCentral.addInputParam("@i_creditConcept",ICTSTypes.SQLVARCHAR, creditConcept);
	    reqTMPCentral.addInputParam("@i_originCode",ICTSTypes.SQLINT4, referenceNumber);
	    
	    reqTMPCentral.addInputParam("@s_ofi",ICTSTypes.SQLINT4, "1");
	    reqTMPCentral.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
	    reqTMPCentral.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0.0.0.0");
	    reqTMPCentral.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, "");
	    
	    IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);
	    
	    if(wProcedureResponseCentral.getReturnCode() == 0 && wProcedureResponseCentral.getResultSets().size()>0){
	    	
	    	logger.logDebug("response CREDIT wProcedureResponseCentral: " + wProcedureResponseCentral.getResultSetRowColumnData(1, 1, 4).getValue());
	    	aBagSPJavaOrchestration.put("referenceCode",wProcedureResponseCentral.getResultSetRowColumnData(1, 1, 4).getValue());
	    }
		
		if (logger.isInfoEnabled()) {
			logger.logDebug("Ending flow, queryAccountCreditOperation with wProcedureResponseCentral: " + wProcedureResponseCentral.getProcedureResponseAsString());
		}
		
		IProcedureResponse wProcedureResponseLocal;
		if (!wProcedureResponseCentral.hasError()) {			
			IResultSetRow resultSetRow = wProcedureResponseCentral.getResultSet(1).getData().getRowsAsArray()[0];
			IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();
			
			if (columns[0].getValue().equals("true")) {
				//this.columnsToReturn = columns;
				IProcedureRequest reqTMPLocal = (initProcedureRequest(wQueryRequest));
				logger.logDebug("xxdcxx CREDIT " + columns.toString());
				reqTMPLocal.setSpName("cob_bvirtual..sp_account_credit_operation_local_api");
				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "local");
				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500111");
				reqTMPLocal.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
				reqTMPLocal.addInputParam("@i_accountNumber",ICTSTypes.SQLVARCHAR, accountNumber);
				reqTMPLocal.addInputParam("@i_amount",ICTSTypes.SQLMONEY, amount);
				reqTMPLocal.addInputParam("@i_commission",ICTSTypes.SQLMONEY, commission);
				//reqTMPLocal.addInputParam("@i_latitude",ICTSTypes.SQLFLT8i, wQueryRequest.readValueParam("@i_latitude"));
				//reqTMPLocal.addInputParam("@i_longitude",ICTSTypes.SQLFLT8i, wQueryRequest.readValueParam("@i_longitude"));
				reqTMPLocal.addInputParam("@i_referenceNumber",ICTSTypes.SQLVARCHAR, referenceNumber);
				reqTMPLocal.addInputParam("@i_creditConcept",ICTSTypes.SQLVARCHAR, creditConcept);
				//reqTMPLocal.addInputParam("@i_originCode",ICTSTypes.SQLINT4, wQueryRequest.readValueParam("@i_originCode"));
				
				wProcedureResponseLocal = executeCoreBanking(reqTMPLocal);
				if (logger.isInfoEnabled()) {
					logger.logDebug("Ending flow, queryAccountCreditOperation with wProcedureResponseLocal: " + wProcedureResponseLocal.getProcedureResponseAsString());
				}

				if (!wProcedureResponseLocal.hasError()) {
					
					resultSetRow = wProcedureResponseLocal.getResultSet(1).getData().getRowsAsArray()[0];
					columns = resultSetRow.getColumnsAsArray();
					
					if (columns[0].getValue().equals("true")) {
						
						responseBag.put(columns[1].getValue(), columns[2].getValue());
						return responseBag;
						
					} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("50041")) {
						
						responseBag.put(columns[1].getValue(), columns[2].getValue());
						return responseBag;
					} 
					
				} else {
					
					responseBag.put("50041", "Error account credit operation");
					return responseBag;
				}
								
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40012")) {
				
				responseBag.put(columns[1].getValue(), "Customer with externalCustomerId: " + idCustomer + " does not exist");
				return responseBag;
				
			} else {
				
				responseBag.put(columns[1].getValue(), columns[2].getValue());
				return responseBag;
			}
				
			 
		} else {
			responseBag.put("50041", "Error account credit operation");
			return responseBag;
		}
		return responseBag;
	}

private Map<String, Object> queryAccountCreditOperation(Content content, Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureRequest wQueryRequest = (IProcedureRequest) aBagSPJavaOrchestration.get("anOriginalRequest");
		//aBagSPJavaOrchestration.clear();
		String idCustomer = aBagSPJavaOrchestration.get("ente").toString();
		String accountNumber = aBagSPJavaOrchestration.get("account").toString();
		String referenceNumber = aBagSPJavaOrchestration.get("referenceNumber").toString(); //content.getTRANSACTION().getAUTHORIZATION();
		String creditConcept = "Exchange difference adjustment";
		String amount = aBagSPJavaOrchestration.get("amount").toString();//String.valueOf(content.getTRANSACTION().getDEST_VALUE());
		String commission = "0";
		
		Map<String, Object> responseBag = new HashMap<String, Object>();
				
		logger.logDebug("Begin flow, queryAccountCreditOperation with id: " + idCustomer);
		
		IProcedureRequest reqTMPCentral = (initProcedureRequest(wQueryRequest));		
		reqTMPCentral.setSpName("cobis..sp_account_credit_operation_central_api");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "central");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500111");
		reqTMPCentral.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
		reqTMPCentral.addInputParam("@i_accountNumber",ICTSTypes.SQLVARCHAR, accountNumber);
		reqTMPCentral.addInputParam("@i_amount",ICTSTypes.SQLMONEY, amount);
		reqTMPCentral.addInputParam("@i_commission",ICTSTypes.SQLMONEY, commission);	 
	    reqTMPCentral.addInputParam("@i_creditConcept",ICTSTypes.SQLVARCHAR, creditConcept);
	    reqTMPCentral.addInputParam("@i_originCode",ICTSTypes.SQLINT4, referenceNumber);
	    
	    reqTMPCentral.addInputParam("@s_ofi",ICTSTypes.SQLINT4, "1");
	    reqTMPCentral.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
	    reqTMPCentral.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0.0.0.0");
	    reqTMPCentral.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, "");
	    IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);
		
		if (logger.isInfoEnabled()) {
			logger.logDebug("Ending flow, queryAccountCreditOperation with wProcedureResponseCentral: " + wProcedureResponseCentral.getProcedureResponseAsString());
		}
		
		IProcedureResponse wProcedureResponseLocal;
		if (!wProcedureResponseCentral.hasError()) {			
			IResultSetRow resultSetRow = wProcedureResponseCentral.getResultSet(1).getData().getRowsAsArray()[0];
			IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();
			
			if (columns[0].getValue().equals("true")) {
				//this.columnsToReturn = columns;
				IProcedureRequest reqTMPLocal = (initProcedureRequest(wQueryRequest));
				logger.logDebug("xxdcxx DEBIT " + columns.toString());
				reqTMPLocal.setSpName("cob_bvirtual..sp_account_credit_operation_local_api");
				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "local");
				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500111");
				reqTMPLocal.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
				reqTMPLocal.addInputParam("@i_accountNumber",ICTSTypes.SQLVARCHAR, accountNumber);
				reqTMPLocal.addInputParam("@i_amount",ICTSTypes.SQLMONEY, amount);
				reqTMPLocal.addInputParam("@i_commission",ICTSTypes.SQLMONEY, commission);
				//reqTMPLocal.addInputParam("@i_latitude",ICTSTypes.SQLFLT8i, wQueryRequest.readValueParam("@i_latitude"));
				//reqTMPLocal.addInputParam("@i_longitude",ICTSTypes.SQLFLT8i, wQueryRequest.readValueParam("@i_longitude"));
				reqTMPLocal.addInputParam("@i_referenceNumber",ICTSTypes.SQLVARCHAR, referenceNumber);
				reqTMPLocal.addInputParam("@i_creditConcept",ICTSTypes.SQLVARCHAR, creditConcept);
				//reqTMPLocal.addInputParam("@i_originCode",ICTSTypes.SQLINT4, wQueryRequest.readValueParam("@i_originCode"));
				
				wProcedureResponseLocal = executeCoreBanking(reqTMPLocal);
				if (logger.isInfoEnabled()) {
					logger.logDebug("Ending flow, queryAccountCreditOperation with wProcedureResponseLocal: " + wProcedureResponseLocal.getProcedureResponseAsString());
				}

				if (!wProcedureResponseLocal.hasError()) {
					
					resultSetRow = wProcedureResponseLocal.getResultSet(1).getData().getRowsAsArray()[0];
					columns = resultSetRow.getColumnsAsArray();
					
					if (columns[0].getValue().equals("true")) {
						
						responseBag.put(columns[1].getValue(), columns[2].getValue());
						return responseBag;
						
					} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("50041")) {
						
						responseBag.put(columns[1].getValue(), columns[2].getValue());
						return responseBag;
					} 
					
				} else {
					
					responseBag.put("50041", "Error account credit operation");
					return responseBag;
				}
								
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40012")) {
				
				responseBag.put(columns[1].getValue(), "Customer with externalCustomerId: " + idCustomer + " does not exist");
				return responseBag;
				
			} else {
				
				responseBag.put(columns[1].getValue(), columns[2].getValue());
				return responseBag;
			}
				
			 
		} else {
			responseBag.put("50041", "Error account credit operation");
			return responseBag;
		}
		return responseBag;
	}

	private Map<String, Object> queryAccountDebitOperation(Map<String, Object> aBagSPJavaOrchestration) {
		
		aBagSPJavaOrchestration.put("transactionType","DEBIT");
		IProcedureRequest wQueryRequest = (IProcedureRequest) aBagSPJavaOrchestration.get("anOriginalRequest");
		Map<String, Object> responseBag = new HashMap<String, Object>();
		//aBagSPJavaOrchestration.clear();
		String idCustomer = aBagSPJavaOrchestration.get("ente").toString();
		String accountNumber = aBagSPJavaOrchestration.get("account").toString();
		String referenceNumber = aBagSPJavaOrchestration.get("referenceNumber").toString(); //content.getTRANSACTION().getAUTHORIZATION();
		String debitConcept = aBagSPJavaOrchestration.get("concept").toString();
		String amount = aBagSPJavaOrchestration.get("amount").toString();//String.valueOf(content.getTRANSACTION().getDEST_VALUE());
		String commission = "0";
		
		logger.logDebug("Begin flow, queryAccountDebitOperation with id: " + idCustomer);
		
		IProcedureRequest reqTMPCentral = (initProcedureRequest(wQueryRequest));		
		reqTMPCentral.setSpName("cobis..sp_account_debit_operation_central_api");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500118");
		reqTMPCentral.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
		reqTMPCentral.addInputParam("@i_accountNumber",ICTSTypes.SQLVARCHAR, accountNumber);
		reqTMPCentral.addInputParam("@i_amount",ICTSTypes.SQLMONEY, amount);
		reqTMPCentral.addInputParam("@i_commission",ICTSTypes.SQLMONEY, commission);	 
	    reqTMPCentral.addInputParam("@i_debitConcept",ICTSTypes.SQLVARCHAR, debitConcept);
	    reqTMPCentral.addInputParam("@i_originCode",ICTSTypes.SQLINT4, referenceNumber);
	    
	    reqTMPCentral.addInputParam("@s_ofi",ICTSTypes.SQLINT4, "1");
	    reqTMPCentral.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
	    reqTMPCentral.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0.0.0.0");
	    reqTMPCentral.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, "");
		
	    
	    IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);
	    
	    if(wProcedureResponseCentral.getReturnCode() == 0 && wProcedureResponseCentral.getResultSets().size()>0){
	    	
	    	logger.logDebug("response DEBIT wProcedureResponseCentral: " + wProcedureResponseCentral.getResultSetRowColumnData(1, 1, 4).getValue());
	    	aBagSPJavaOrchestration.put("referenceCode",wProcedureResponseCentral.getResultSetRowColumnData(1, 1, 4).getValue());
	    }
	    
		if (logger.isInfoEnabled()) {
			logger.logDebug("Ending flow, queryAccountDebitOperation with wProcedureResponseCentral: " + wProcedureResponseCentral.getProcedureResponseAsString());
		}
		
		IProcedureResponse wProcedureResponseLocal;
		if (!wProcedureResponseCentral.hasError()) {			
			IResultSetRow resultSetRow = wProcedureResponseCentral.getResultSet(wProcedureResponseCentral.getResultSetListSize()).getData().getRowsAsArray()[0];
			IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();
			
			if (columns[0].getValue().equals("true")) {
				//this.columnsToReturn = columns;
				IProcedureRequest reqTMPLocal = (initProcedureRequest(wQueryRequest));
	
				reqTMPLocal.setSpName("cob_bvirtual..sp_account_debit_operation_local_api");
				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "local");
				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500118");
				reqTMPLocal.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
				reqTMPLocal.addInputParam("@i_accountNumber",ICTSTypes.SQLVARCHAR, accountNumber);
				reqTMPLocal.addInputParam("@i_amount",ICTSTypes.SQLMONEY, amount);
				reqTMPLocal.addInputParam("@i_commission",ICTSTypes.SQLMONEY, commission);
				//reqTMPLocal.addInputParam("@i_latitude",ICTSTypes.SQLFLT8i, wQueryRequest.readValueParam("@i_latitude"));
				//reqTMPLocal.addInputParam("@i_longitude",ICTSTypes.SQLFLT8i, wQueryRequest.readValueParam("@i_longitude"));
				reqTMPLocal.addInputParam("@i_referenceNumber",ICTSTypes.SQLVARCHAR, referenceNumber);
				reqTMPLocal.addInputParam("@i_debitConcept",ICTSTypes.SQLVARCHAR, debitConcept);
				//reqTMPLocal.addInputParam("@i_originCode",ICTSTypes.SQLINT4, wQueryRequest.readValueParam("@i_originCode"));
				
				wProcedureResponseLocal = executeCoreBanking(reqTMPLocal);
				if (logger.isInfoEnabled()) {
					logger.logDebug("Ending flow, queryAccountDebitOperation with wProcedureResponseLocal: " + wProcedureResponseLocal.getProcedureResponseAsString());
				}

				if (!wProcedureResponseLocal.hasError()) {
					
					resultSetRow = wProcedureResponseLocal.getResultSet(1).getData().getRowsAsArray()[0];
					columns = resultSetRow.getColumnsAsArray();
					logger.logDebug("xxdcxx: " + columns.toString());
					if (columns[0].getValue().equals("true")) {
						
						responseBag.put(columns[1].getValue(), columns[2].getValue());
						return responseBag;
						
					} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("50045")) {
						
						responseBag.put(columns[1].getValue(), columns[2].getValue());
						
						return responseBag;
					} 
					
				} else {
					//aBagSPJavaOrchestration.put("succes","false");
					responseBag.put("50045", "Error account debit operation");
					return responseBag;
				}
								
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40012")) {
				
				responseBag.put(columns[1].getValue(), "Customer with externalCustomerId: " + idCustomer + " does not exist");
				return responseBag;
			} else {
				//aBagSPJavaOrchestration.put("succes","false");
				responseBag.put(columns[1].getValue(), columns[2].getValue());
				return responseBag;
			}
			 
		} else {
			//aBagSPJavaOrchestration.put("succes","false");
			responseBag.put("50045", "Error account debit operation");
			return responseBag;
		}
		return responseBag;
	}

private Map<String, Object> queryAccountDebitOperation(Content content, Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureRequest wQueryRequest = (IProcedureRequest) aBagSPJavaOrchestration.get("anOriginalRequest");
		Map<String, Object> responseBag = new HashMap<String, Object>();
		//aBagSPJavaOrchestration.clear();
		String idCustomer = aBagSPJavaOrchestration.get("ente").toString();
		String accountNumber = aBagSPJavaOrchestration.get("account").toString();
		String referenceNumber = aBagSPJavaOrchestration.get("referenceNumber").toString(); //content.getTRANSACTION().getAUTHORIZATION();
		String debitConcept = "Exchange difference adjustment";
		String amount = aBagSPJavaOrchestration.get("amount").toString();//String.valueOf(content.getTRANSACTION().getDEST_VALUE());
		String commission = "0";
			
		logger.logDebug("Begin flow, queryAccountDebitOperation with id: " + idCustomer);
		
		IProcedureRequest reqTMPCentral = (initProcedureRequest(wQueryRequest));		
		reqTMPCentral.setSpName("cobis..sp_account_debit_operation_central_api");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500118");
		reqTMPCentral.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
		reqTMPCentral.addInputParam("@i_accountNumber",ICTSTypes.SQLVARCHAR, accountNumber);
		reqTMPCentral.addInputParam("@i_amount",ICTSTypes.SQLMONEY, amount);
		reqTMPCentral.addInputParam("@i_commission",ICTSTypes.SQLMONEY, commission);	 
	    reqTMPCentral.addInputParam("@i_debitConcept",ICTSTypes.SQLVARCHAR, debitConcept);
	    reqTMPCentral.addInputParam("@i_originCode",ICTSTypes.SQLINT4, referenceNumber);
	    
	    reqTMPCentral.addInputParam("@s_ofi",ICTSTypes.SQLINT4, "1");
	    reqTMPCentral.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
	    reqTMPCentral.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0.0.0.0");
	    reqTMPCentral.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, "");
		
	    
	    IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);
	    
		if (logger.isInfoEnabled()) {
			logger.logDebug("Ending flow, queryAccountDebitOperation with wProcedureResponseCentral: " + wProcedureResponseCentral.getProcedureResponseAsString());
		}
		
		IProcedureResponse wProcedureResponseLocal;
		if (!wProcedureResponseCentral.hasError()) {			
			IResultSetRow resultSetRow = wProcedureResponseCentral.getResultSet(wProcedureResponseCentral.getResultSetListSize()).getData().getRowsAsArray()[0];
			IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();
			
			if (columns[0].getValue().equals("true")) {
				//this.columnsToReturn = columns;
				IProcedureRequest reqTMPLocal = (initProcedureRequest(wQueryRequest));
	
				reqTMPLocal.setSpName("cob_bvirtual..sp_account_debit_operation_local_api");
				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "local");
				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500118");
				reqTMPLocal.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT4, idCustomer);
				reqTMPLocal.addInputParam("@i_accountNumber",ICTSTypes.SQLVARCHAR, accountNumber);
				reqTMPLocal.addInputParam("@i_amount",ICTSTypes.SQLMONEY, amount);
				reqTMPLocal.addInputParam("@i_commission",ICTSTypes.SQLMONEY, commission);
				//reqTMPLocal.addInputParam("@i_latitude",ICTSTypes.SQLFLT8i, wQueryRequest.readValueParam("@i_latitude"));
				//reqTMPLocal.addInputParam("@i_longitude",ICTSTypes.SQLFLT8i, wQueryRequest.readValueParam("@i_longitude"));
				reqTMPLocal.addInputParam("@i_referenceNumber",ICTSTypes.SQLVARCHAR, referenceNumber);
				reqTMPLocal.addInputParam("@i_debitConcept",ICTSTypes.SQLVARCHAR, debitConcept);
				//reqTMPLocal.addInputParam("@i_originCode",ICTSTypes.SQLINT4, wQueryRequest.readValueParam("@i_originCode"));
				
				wProcedureResponseLocal = executeCoreBanking(reqTMPLocal);
				if (logger.isInfoEnabled()) {
					logger.logDebug("Ending flow, queryAccountDebitOperation with wProcedureResponseLocal: " + wProcedureResponseLocal.getProcedureResponseAsString());
				}

				if (!wProcedureResponseLocal.hasError()) {
					
					resultSetRow = wProcedureResponseLocal.getResultSet(1).getData().getRowsAsArray()[0];
					columns = resultSetRow.getColumnsAsArray();
					logger.logDebug("xxdcxx: " + columns.toString());
					if (columns[0].getValue().equals("true")) {
						
						responseBag.put(columns[1].getValue(), columns[2].getValue());
						return responseBag;
						
					} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("50045")) {
						
						responseBag.put(columns[1].getValue(), columns[2].getValue());
						
						return responseBag;
					} 
					
				} else {
					//aBagSPJavaOrchestration.put("succes","false");
					responseBag.put("50045", "Error account debit operation");
					return responseBag;
				}
								
			} else if (columns[0].getValue().equals("false") && columns[1].getValue().equals("40012")) {
				
				responseBag.put(columns[1].getValue(), "Customer with externalCustomerId: " + idCustomer + " does not exist");
				return responseBag;
			} else {
				//aBagSPJavaOrchestration.put("succes","false");
				aBagSPJavaOrchestration.put(columns[1].getValue(), columns[2].getValue());
				return responseBag;
			}
			 
		} else {
			//aBagSPJavaOrchestration.put("succes","false");
			aBagSPJavaOrchestration.put("50045", "Error account debit operation");
			return responseBag;
		}
		return responseBag;
	}
	private IProcedureResponse updateStatusAtm(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();
		Integer trn = 0;
		String process = null, reason = null;

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en updateStatusAtm");
		}

		if(aRequest.readValueParam("@i_card_status").equals("N"))
		{
			trn = 16537;
			process = "LBW";
			reason = "SCL";
		}else if (aRequest.readValueParam("@i_card_status").equals("B")){
			trn = 16507;
			process = "BLW";
			reason = "SCL";
		}else{
			trn = 16507;
			process = "BLW";
			reason = "SCL";
		}
		
		
		request.setSpName("cob_atm..sp_atm_bloqueo_tarjeta");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		//request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_ente"));
		request.addInputParam("@i_proceso", ICTSTypes.SQLVARCHAR, process);
		request.addInputParam("@i_banco", ICTSTypes.SQLINT4, "1");
		request.addInputParam("@i_tarjeta", ICTSTypes.SQLVARCHAR,aBagSPJavaOrchestration.get("o_id_card_atm").toString());
		request.addInputParam("@i_motivo", ICTSTypes.SQLVARCHAR, reason);
		request.addInputParam("@i_oficina", ICTSTypes.SQLINT4, "1");
		request.addInputParam("@i_retener", ICTSTypes.SQLVARCHAR, "N");
		
		request.addInputParam("@s_ofi", ICTSTypes.SQLINT4, "1");
		request.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		request.addInputParam("@t_trn", ICTSTypes.SQLVARCHAR, String.valueOf(trn));
		
		request.addOutputParam("@o_secuencial", ICTSTypes.SQLVARCHAR, "0");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking updateStatusAtm: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de updateStatusAtm");
		}

		return wProductsQueryResp;
	}
	
	private void registerInitialLogBd(Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en registerInitialLogBd");
		}

		request.setSpName("cob_bvirtual..sp_bv_trn_ins_log_comp");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		LocalDateTime fechaHoraActual = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String fechaACtual = fechaHoraActual.format(formato);
        
		request.addInputParam("@i_register_date", ICTSTypes.SQLVARCHAR, fechaACtual);
		request.addInputParam("@i_modo", ICTSTypes.SQLVARCHAR, "IF");		
		request.addInputParam("@i_file_id", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("fileId").toString());
		request.addInputParam("@i_file_name", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("fileName").toString());
		request.addInputParam("@i_date_time_gmt", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("referenceDate").toString());
		request.addInputParam("@i_sequence", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("sequence").toString());
		request.addInputParam("@i_records_total", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("recordsTotal").toString());
		request.addInputParam("@i_status", ICTSTypes.SQLVARCHAR, "P"); //Pendiente
		request.addInputParam("@i_status_reasson", ICTSTypes.SQLVARCHAR, "PENDIENTE"); 
		
		//request.addInputParam("@i_card_id", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("account").toString());
		
		logger.logDebug("Request Corebanking registerLog: " + request.toString());
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de registerInitialLogBd");
		}
	}

	private void registerFinalLogBd(Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en registerFinalLogBd");
		}

		request.setSpName("cob_bvirtual..sp_bv_trn_ins_log_comp");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		LocalDateTime fechaHoraActual = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String fechaACtual = fechaHoraActual.format(formato);

		request.addInputParam("@i_modify_date", ICTSTypes.SQLVARCHAR, fechaACtual);
		request.addInputParam("@i_modo", ICTSTypes.SQLVARCHAR, "UF");		
		request.addInputParam("@i_status", ICTSTypes.SQLVARCHAR, "C");
		request.addInputParam("@i_status_reasson", ICTSTypes.SQLVARCHAR, "COMPLETADO");
		request.addInputParam("@i_file_id", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("fileId").toString());
		request.addInputParam("@i_records_procesados", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("registrosProcesados").toString());
		request.addInputParam("@i_concept", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("concept").toString());
		
		logger.logDebug("Request Corebanking registerLog: " + request.toString());
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de registerFinalLogBd");
		}
	}
	
	private void registerTransactionLogBd(Map<String, Object> aBagSPJavaOrchestration, Map<String, Object> aBagResponseTransaction) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en registerTransactionLogBd");
		}

		request.setSpName("cob_bvirtual..sp_bv_trn_ins_log_comp");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		LocalDateTime fechaHoraActual = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String fechaACtual = fechaHoraActual.format(formato);
        
        request.addInputParam("@i_ente", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("ente").toString());
		request.addInputParam("@i_account", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("account").toString());
		request.addInputParam("@i_register_date", ICTSTypes.SQLVARCHAR, fechaACtual);
		request.addInputParam("@i_modify_date", ICTSTypes.SQLVARCHAR, fechaACtual);
		request.addInputParam("@i_modo", ICTSTypes.SQLVARCHAR, "IT");		
		request.addInputParam("@i_file_id", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("fileId").toString());
		request.addInputParam("@i_date_time_gmt", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("gmtDate").toString());
		request.addInputParam("@i_card_id", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("cardId").toString());
		request.addInputParam("@i_content_id", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("contentId").toString());
		request.addInputParam("@i_amount", ICTSTypes.SQLMONEY4, aBagSPJavaOrchestration.get("amount").toString());
		request.addInputParam("@i_dest_currency", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("destCurrency").toString());
		request.addInputParam("@i_dest_value", ICTSTypes.SQLMONEY4, aBagSPJavaOrchestration.get("destValue").toString());
		request.addInputParam("@i_source_curren", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("sourceCurrency").toString());
		request.addInputParam("@i_source_value", ICTSTypes.SQLMONEY4, aBagSPJavaOrchestration.get("sourceValue").toString());
		
		request.addInputParam("@i_reference_number", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("referenceNumber").toString());
		request.addInputParam("@i_concept", ICTSTypes.SQLVARCHAR,aBagSPJavaOrchestration.get("concept").toString());
		request.addInputParam("@i_transaction_type", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("transactionType").toString());
		
		JSONObject jsonO = new JSONObject();
		
		ArrayList<String> keyList = new ArrayList<String>(aBagResponseTransaction.keySet());
		logger.logInfo(CLASS_NAME + " keyList log: " + keyList.toString());
		if (keyList.get(0).equals("0")) {
			jsonO.put("success", "true");
			jsonO.put("code","0");
			jsonO.put("message","Succes");
			jsonO.put("referenceCode", aBagSPJavaOrchestration.get("referenceCode").toString());
			request.addInputParam("@i_status", ICTSTypes.SQLVARCHAR, "C"); //Pendiente
			request.addInputParam("@i_status_reasson", ICTSTypes.SQLVARCHAR, "COMPLETO");
			
		}
		else{
			keyList.get(0);
			aBagSPJavaOrchestration.get(keyList.get(0));
			
	        jsonO.put("success", "false");
	        jsonO.put("code", keyList.get(0));
	        jsonO.put("message", aBagResponseTransaction.get(keyList.get(0)));	        
			request.addInputParam("@i_status", ICTSTypes.SQLVARCHAR, "E"); //Pendiente
			request.addInputParam("@i_status_reasson", ICTSTypes.SQLVARCHAR, jsonO.toString());
		}
		
		//request.addInputParam("@i_card_id", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("account").toString());
		
		logger.logDebug("Request Corebanking registerLog: " + request.toString());
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de registerTransactionLogBd");
		}
	}
	
	private void registerAssingLogBd(IProcedureResponse reponseCard, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en registerAssingLogBd");
		}

		request.setSpName("cob_atm..sp_insert_data_dock_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		LocalDateTime fechaHoraActual = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String fechaACtual = fechaHoraActual.format(formato);
		 
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aBagSPJavaOrchestration.get("ente_mis").toString());
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("account_number").toString());
		request.addInputParam("@i_fecha_reg", ICTSTypes.SQLVARCHAR, fechaACtual);
		request.addInputParam("@i_fecha_mod", ICTSTypes.SQLVARCHAR, fechaACtual);
		request.addInputParam("@i_modo", ICTSTypes.SQLVARCHAR, "CCA");
		request.addInputParam("@i_tarjeta_id", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_card_id"));
		
		request.addInputParam("@i_asig_date", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_assign_date"));
		request.addInputParam("@i_request_ad", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_requestAssingCard"));
		request.addInputParam("@i_response_ad", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_responseAssingCard"));
	
		String typeCard = aBagSPJavaOrchestration.get("o_card_type")!=null?aBagSPJavaOrchestration.get("o_card_type").toString():null;
		request.addInputParam("@i_tipo_tarjeta", ICTSTypes.SQLVARCHAR, typeCard);
		
		logger.logDebug("Request Corebanking registerAssingLogBd: " + request.toString());
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking registerAssingLogBd: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de registerAssingLogBd");
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
		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SYBINT4, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("cardId", ICTSTypes.SYBINT4, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("cardNumber", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("customerName", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("cardApplication", ICTSTypes.SYBINT4, 255));
		
		if (keyList.get(0).equals("0")) {
			logger.logDebug("Ending flow, processResponse success with code: " + keyList.get(0));
			row.addRowData(1, new ResultSetRowColumnData(false, "true"));
			row.addRowData(2, new ResultSetRowColumnData(false, "0"));
			row.addRowData(3, new ResultSetRowColumnData(false, "Success"));
			row.addRowData(4, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get("o_cod_respuesta").toString()));
			row.addRowData(5, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get("o_desc_respuesta").toString()));
			row.addRowData(6, new ResultSetRowColumnData(false, null));
			row.addRowData(7, new ResultSetRowColumnData(false, null));
			data.addRow(row);

		} else {
			logger.logDebug("Ending flow, processResponse failed with code: " + keyList.get(0));
			row.addRowData(1, new ResultSetRowColumnData(false, "false"));
			row.addRowData(2, new ResultSetRowColumnData(false, keyList.get(0)));
			row.addRowData(3, new ResultSetRowColumnData(false, (String) aBagSPJavaOrchestration.get(keyList.get(0))));
			row.addRowData(4, new ResultSetRowColumnData(false, null));
			row.addRowData(5, new ResultSetRowColumnData(false, null));
			row.addRowData(6, new ResultSetRowColumnData(false, null));
			row.addRowData(7, new ResultSetRowColumnData(false, null));
			data.addRow(row);
		}
		
		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
		wProcedureResponse.addResponseBlock(resultBlock);			
		return wProcedureResponse;		
	}
	
	public IProcedureResponse processResponseApi(IProcedureResponse anOriginalProcedureRes, Map<String, Object> aBagSPJavaOrchestration) {
		logger.logInfo("processResponseApi [INI] --->" );
		
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		Integer codeReturn = anOriginalProcedureRes.getReturnCode();
		
		logger.logInfo("return code resp Conector --->" + codeReturn );

		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));

		
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));

		IResultSetHeader metaData3 = new ResultSetHeader();
		IResultSetData data3 = new ResultSetData();
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("cardId", ICTSTypes.SQLVARCHAR, 80));
		
		/*
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SYBINT4, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SYBVARCHAR, 255));
		*/
		//if(!aBagSPJavaOrchestration.containsKey("flag_log"))
		//	registerLogBd(anOriginalProcedureRes, aBagSPJavaOrchestration);
		
		if (codeReturn == 0) {
			
		Boolean flag = aBagSPJavaOrchestration.containsKey("o_success");
		
		logger.logDebug("response conector dock: " + anOriginalProcedureRes.toString());
		logger.logDebug("code o_assign_date: " + flag);
		logger.logDebug("retunr code response: " + anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1));
		logger.logDebug("retunr o_card_available: " + aBagSPJavaOrchestration.get("o_card_available"));
		logger.logDebug("retunr o_id_card_dock: " + aBagSPJavaOrchestration.get("o_id_card_dock"));
		logger.logDebug("request mode: " + aBagSPJavaOrchestration.get("mode"));
		
		
			if(flag == true){
				logger.logDebug("Ending flow, processResponse success with code: ");
				
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, "true"));
				data.addRow(row);
				
				IResultSetRow row2 = new ResultSetRow();
				row2.addRowData(1, new ResultSetRowColumnData(false, "0"));
				row2.addRowData(2, new ResultSetRowColumnData(false, "Success"));
				data2.addRow(row2);
				
				if(!aBagSPJavaOrchestration.get("o_card_available").toString().equals("X")){
					IResultSetRow row3 = new ResultSetRow();
					row3.addRowData(1, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get("o_card_available").toString()));
					data3.addRow(row3);
				}
				
				if(!aBagSPJavaOrchestration.get("o_id_card_dock").toString().equals("X")){
					IResultSetRow row3 = new ResultSetRow();
					row3.addRowData(1, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get("o_id_card_dock").toString()));
					data3.addRow(row3);
				}
								
			}
			else{
				logger.logDebug("Ending flow, processResponse error");
				
				String message = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).isNull()?"Service execution error":anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).getValue();
				String success = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).isNull()?"false":anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue();
				String code = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).isNull()?"400218":anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).getValue();
				
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
			
			IResultSetRow row = new ResultSetRow();
			row.addRowData(1, new ResultSetRowColumnData(false, "false"));
			data.addRow(row);
			
			IResultSetRow row2 = new ResultSetRow();
			row2.addRowData(1, new ResultSetRowColumnData(false, codeReturn.toString()));
			row2.addRowData(2, new ResultSetRowColumnData(false, anOriginalProcedureRes.getMessage(1).getMessageText()));
			data2.addRow(row2);
		}

		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);

		wProcedureResponse.addResponseBlock(resultsetBlock);
		wProcedureResponse.addResponseBlock(resultsetBlock2);
		
		if(!aBagSPJavaOrchestration.get("o_card_available").toString().equals("X") && aBagSPJavaOrchestration.get("mode").toString().equals("N")){
			IResultSetBlock resultsetBlock3 = new ResultSetBlock(metaData3, data3);
			wProcedureResponse.addResponseBlock(resultsetBlock3);
		}

		if(!aBagSPJavaOrchestration.get("o_id_card_dock").toString().equals("X") && aBagSPJavaOrchestration.get("mode").toString().equals("N")){
			IResultSetBlock resultsetBlock3 = new ResultSetBlock(metaData3, data3);
			wProcedureResponse.addResponseBlock(resultsetBlock3);
		}
		
		return wProcedureResponse;		
	}
	
	private IProcedureResponse registerAtmCobis(Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en registerAtmCobis");
		}
		
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
				
		wProcedureResponse = getApplicationCard(aBagSPJavaOrchestration);
		
		wProcedureResponse = grabarSolicitud(aBagSPJavaOrchestration);
		
		if(wProcedureResponse.getReturnCode()!=0)
			return wProcedureResponse;
		
		if(wProcedureResponse.readValueParam("@o_numero").equals("0") || wProcedureResponse.getReturnCode()!=0)
			return wProcedureResponse;
		
		wProcedureResponse = aprobacionDefault(aBagSPJavaOrchestration);
		
		wProcedureResponse = getCustomerCardByAccount(aBagSPJavaOrchestration);
		
		//IProcedureResponse wProcedureRes = getActivationState(aBagSPJavaOrchestration);
		
		//if(null==wProcedureRes){
		wProcedureResponse = setActivationState(aBagSPJavaOrchestration);
		//}
		
		wProcedureResponse = insertCardActivationApplication(aBagSPJavaOrchestration);
		
		wProcedureResponse = activateCardAtm(aBagSPJavaOrchestration);
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Response: " + wProcedureResponse.toString());
			logger.logInfo(CLASS_NAME + " Saliendo de registerAtmCobis");
		}
		
		return wProcedureResponse;
		
	}

	private IProcedureResponse getApplicationCard(Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getApplicationCard");
		}

		request.setSpName("cob_bvirtual..sp_consulta_solicitud");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "Q");
		request.addInputParam("@i_cliente", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("ente_mis").toString());
		request.addInputParam("@i_modo", ICTSTypes.SQLINT4, "0");

		request.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		request.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0.0.0.0");
		request.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, "");
		request.addInputParam("@s_ofi", ICTSTypes.SQLINT2, "1");
		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, "18500048");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getApplicationCard ->getResultSetListSize " + wProductsQueryResp.getResultSetListSize() + "  getResultSets: "+wProductsQueryResp.getResultSets().size());
		}

		if (null!=wProductsQueryResp.getResultSetRowColumnData(1, 1, 1)) {
			logger.logInfo(CLASS_NAME + " Data(1, 1, 1) cod_application"+ wProductsQueryResp.getResultSetRowColumnData(1, 1, 1).getValue());
			aBagSPJavaOrchestration.put("cod_application",wProductsQueryResp.getResultSetRowColumnData(1, 1, 1).getValue());
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse grabarSolicitud(Map<String, Object> aBagSPJavaOrchestration) {
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Inicia grabarSolicitud");
		}

		ProcedureRequestAS wProcedureRequest = new ProcedureRequestAS();
		
		wProcedureRequest.setSpName("cob_atm..sp_atm_graba_solicitud");

		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		wProcedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, "16503");
		wProcedureRequest.addInputParam("@t_trn", 56, "16503");
		wProcedureRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		
		wProcedureRequest.addInputParam("@i_numero", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("cod_application").toString());
		wProcedureRequest.addInputParam("@i_num_detalles", ICTSTypes.SQLINT2, "1");
		wProcedureRequest.addInputParam("@i_num_producto", ICTSTypes.SQLINT2, "1");
		wProcedureRequest.addInputParam("@i_num_excepcion", ICTSTypes.SQLINT2, "0");
		wProcedureRequest.addInputParam("@i_ofi_entrega", ICTSTypes.SQLINT2, "1");
		wProcedureRequest.addInputParam("@i_autorizado", ICTSTypes.SQLVARCHAR, "usuariobv");
		wProcedureRequest.addInputParam("@i_tipo_ent", ICTSTypes.SQLCHAR, "O");
		wProcedureRequest.addInputParam("@i_motivo", ICTSTypes.SQLVARCHAR, "ETI");
		wProcedureRequest.addInputParam("@i_chip", ICTSTypes.SQLVARCHAR, "N");
		wProcedureRequest.addInputParam("@i_oficial_neg", ICTSTypes.SQLVARCHAR, "usuariobv");
		String cardIdDock = aBagSPJavaOrchestration.get("o_type_card").equals("VIRTUAL")?aBagSPJavaOrchestration.get("o_id_card_dock").toString():aBagSPJavaOrchestration.get("o_card_available").toString();
		wProcedureRequest.addInputParam("@i_num_plastico", ICTSTypes.SQLVARCHAR, cardIdDock);

		wProcedureRequest.addOutputParam("@o_numero", ICTSTypes.SQLINT4, "0");
		wProcedureRequest.addOutputParam("@o_valor_costo", ICTSTypes.SQLMONEY, "0000");
		wProcedureRequest.addOutputParam("@o_cta_debito", ICTSTypes.SQLVARCHAR, "000000000000000000000000");
		wProcedureRequest.addOutputParam("@o_prod_debito", ICTSTypes.SQLINT2, "0");
		wProcedureRequest.addOutputParam("@o_moneda_deb", ICTSTypes.SQLINT2, "0");
		wProcedureRequest.addOutputParam("@o_causal", ICTSTypes.SQLVARCHAR, "0000000000000000");
		wProcedureRequest.addOutputParam("@o_ofi_comprobante", ICTSTypes.SQLINT2, "00000"); 
		wProcedureRequest.addOutputParam("@o_categoria", ICTSTypes.SQLCHAR, "X");
		wProcedureRequest.addOutputParam("@o_tipo_solicitud", ICTSTypes.SQLVARCHAR, "XXXX");
		wProcedureRequest.addOutputParam("@o_param_oficina", ICTSTypes.SQLVARCHAR, "XXXX");
		wProcedureRequest.addOutputParam("@o_tarjeta", ICTSTypes.SQLINT4, "0");
		wProcedureRequest.addOutputParam("@o_valor_seguro", ICTSTypes.SQLMONEY, "0000");
		wProcedureRequest.addOutputParam("@o_causal_seguro", ICTSTypes.SQLVARCHAR, "0000000000000000");
		wProcedureRequest.addOutputParam("@o_simbolo_mon", ICTSTypes.SQLVARCHAR, "XXXXXXXXXX");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(wProcedureRequest);

		aBagSPJavaOrchestration.put("o_categoria", wProductsQueryResp.readValueParam("@o_categoria"));
		aBagSPJavaOrchestration.put("o_numero", wProductsQueryResp.readValueParam("@o_numero"));
		aBagSPJavaOrchestration.put("o_tipo_solicitud", wProductsQueryResp.readValueParam("@o_tipo_solicitud"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking grabarSolicitud: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de grabarSolicitud");
		}

		return wProductsQueryResp;
	}
	
	protected IProcedureResponse aprobacionDefault(Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Inicia aprobacionDefault");
		}

		ProcedureRequestAS wProcedureRequest = new ProcedureRequestAS();
		wProcedureRequest.setSpName("cob_atm..sp_atm_aprobacion_default");
		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		wProcedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, "18503");
		
		wProcedureRequest.addInputParam("@i_categoria", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_categoria").toString());
		wProcedureRequest.addInputParam("@i_num_solicitud", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_numero").toString());
		wProcedureRequest.addInputParam("@i_tipo_solicitud",ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_tipo_solicitud").toString());
		wProcedureRequest.addInputParam("@i_motivo", ICTSTypes.SQLVARCHAR, "ETI");
		wProcedureRequest.addInputParam("@i_chip", ICTSTypes.SQLVARCHAR, "N");
		
		wProcedureRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		wProcedureRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0.0.0.0");
		wProcedureRequest.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, "");
		wProcedureRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, "1");
		
		wProcedureRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "18503");
		
		wProcedureRequest.addOutputParam("@o_cod_mascara", ICTSTypes.SQLVARCHAR, "000000000000000000000000");
		wProcedureRequest.addOutputParam("@o_tarjeta", ICTSTypes.SQLINT4, "0");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(wProcedureRequest);

		aBagSPJavaOrchestration.put("o_categoria", wProductsQueryResp.readValueParam("@o_categoria"));
		aBagSPJavaOrchestration.put("o_numero", wProductsQueryResp.readValueParam("@o_numero"));
		aBagSPJavaOrchestration.put("o_tipo_solicitud", wProductsQueryResp.readValueParam("@o_tipo_solicitud"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking aprobacionDefault: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de aprobacionDefault");
		}

		return wProductsQueryResp;
	}
	
	protected IProcedureResponse getCustomerCardByAccount(Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Inicia getCustomerCardByAccount");
		}

		ProcedureRequestAS wProcedureRequest = new ProcedureRequestAS();
		wProcedureRequest.setSpName("cob_atm..sp_atm_cons_tarjeta");
		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		wProcedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, "16520");
		wProcedureRequest.addInputParam("@i_valor", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_account_number").toString());
		wProcedureRequest.addInputParam("@i_valortiny", ICTSTypes.SQLINT2, "4");
		wProcedureRequest.addInputParam("@i_tipo",ICTSTypes.SQLINT2, "6");
		wProcedureRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		wProcedureRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0.0.0.0");
		wProcedureRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, "1");

		wProcedureRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "16520");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(wProcedureRequest);

		if(!wProductsQueryResp.getResultSetRowColumnData(1, 1, 1).isNull())
		{	
			logger.logInfo(CLASS_NAME + " Data(1, 1, 1) num_tarj" + wProductsQueryResp.getResultSetRowColumnData(1, 1, 1).getValue());
			logger.logInfo(CLASS_NAME + " Data(1, 1, 2) mask" + wProductsQueryResp.getResultSetRowColumnData(1, 1, 2).getValue());
			aBagSPJavaOrchestration.put("o_card_id", wProductsQueryResp.getResultSetRowColumnData(1, 1, 1).getValue());
			aBagSPJavaOrchestration.put("o_mask", wProductsQueryResp.getResultSetRowColumnData(1, 1, 2).getValue());
		}
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking getCustomerCardByAccount(Map<String, Object>): " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getCustomerCardByAccount(Map<String, Object>)");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse setActivationState(Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Inicia setActivationState");
		}

		ProcedureRequestAS wProcedureRequest = new ProcedureRequestAS();
		wProcedureRequest.setSpName("cob_bvirtual..sp_activa_tarjeta");
		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);

		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, "18500047");
		wProcedureRequest.addInputParam("@i_tarjeta", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_card_id").toString());
		wProcedureRequest.addInputParam("@i_cliente", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("ente_mis").toString());
		wProcedureRequest.addInputParam("@i_operacion",ICTSTypes.SQLCHAR, "I");
		wProcedureRequest.addInputParam("@i_modo",ICTSTypes.SQLINT2, "0");
		wProcedureRequest.addInputParam("@i_cacao_actua",ICTSTypes.SQLCHAR, "S");
		wProcedureRequest.addInputParam("@i_cacao_activ",ICTSTypes.SQLCHAR, "S");
		
		wProcedureRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		wProcedureRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0.0.0.0");
		wProcedureRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, "1");

		wProcedureRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "18500047");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(wProcedureRequest);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking setActivationState: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de setActivationState");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse insertCardActivationApplication(Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Inicia insertCardActivationApplication");
		}

		ProcedureRequestAS wProcedureRequest = new ProcedureRequestAS();
		wProcedureRequest.setSpName("cob_atm..sp_atm_sol_activacion");
		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);

		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, "16759");
		wProcedureRequest.addInputParam("@i_grupo1", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_mask").toString() 
				+ "@" + aBagSPJavaOrchestration.get("o_card_id").toString() + "@");
		wProcedureRequest.addInputParam("@i_oficina", ICTSTypes.SQLINT4, "1");
		wProcedureRequest.addInputParam("@i_operacion",ICTSTypes.SQLCHAR, "I");
		wProcedureRequest.addInputParam("@i_tipo",ICTSTypes.SQLINT2, "0");
		
		wProcedureRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		wProcedureRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0.0.0.0");
		//wProcedureRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, "1");

		wProcedureRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "16759");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(wProcedureRequest);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking insertCardActivationApplication: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de insertCardActivationApplication");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse activateCardAtm(Map<String, Object> aBagSPJavaOrchestration) {

		ProcedureRequestAS request = new ProcedureRequestAS();
		
		final String METHOD_NAME = "[activateCardAtm]";

		logger.logInfo(METHOD_NAME + "INICIA INVOCACION");

		IProcedureResponse connectorSpeiResponse = null;

		request.addInputParam("@i_util_sobre", ICTSTypes.SQLCHAR, "S");
		request.addInputParam("@i_tarjeta", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_card_id").toString());
		request.addInputParam("@i_servicio", ICTSTypes.SQLVARCHAR, "MAN");
		request.addInputParam("@i_proceso", ICTSTypes.SQLVARCHAR, "ENA");
		request.addInputParam("@i_operacion_pin", ICTSTypes.SQLINT2, "0");
		request.addInputParam("@i_oficina", ICTSTypes.SQLINT2, "1");
		request.addInputParam("@i_motivo", ICTSTypes.SQLVARCHAR, "EMI");
		request.addInputParam("@i_comentario", ICTSTypes.SQLVARCHAR, "ACTIVACION API");
		request.addInputParam("@i_banco", ICTSTypes.SQLINT2, "1");

		request.setSpName("cob_procesador..sp_exec_atm_activacion");
		// request.addFieldInHeader(ICOBISTS.HEADER_PROCESS_DATE,
		// ICOBISTS.HEADER_DATE_TYPE, forma.format(fecha));
		request.addFieldInHeader(ICOBISTS.HEADER_MESSAGE_TYPE, ICOBISTS.HEADER_STRING_TYPE, "ProcedureRequest");
		request.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
				"(service.identifier=cob_procesador..sp_exec_atm_activacion)");
		request.addFieldInHeader(ICOBISTS.HEADER_SOURCE, ICOBISTS.HEADER_NUMBER_TYPE, "13");
		request.addFieldInHeader(ICOBISTS.HEADER_TROL, ICOBISTS.HEADER_NUMBER_TYPE, "96");
		request.addFieldInHeader(ICOBISTS.HEADER_LOGIN, ICOBISTS.HEADER_STRING_TYPE, "COBISBV"); // *
		request.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "executeTransaction");
		request.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, ""); // *
		request.addFieldInHeader("rol", ICOBISTS.HEADER_NUMBER_TYPE, "96");
		// request.addFieldInHeader("ssn", ICOBISTS.HEADER_NUMBER_TYPE,
		// request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("originalRequestIsCobProcesador", ICOBISTS.HEADER_STRING_TYPE, "true");
		// request.addFieldInHeader("ssnLog", ICOBISTS.HEADER_NUMBER_TYPE,
		// request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("sesn", ICOBISTS.HEADER_NUMBER_TYPE, "0");
		request.addFieldInHeader("authorizationService", ICOBISTS.HEADER_STRING_TYPE, "COBISBV");
		request.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
		request.addFieldInHeader("supportOffline", ICOBISTS.HEADER_CHARACTER_TYPE, "N");
		request.addFieldInHeader("term", ICOBISTS.HEADER_STRING_TYPE, "0:0:0:0:0:0:0:1");
		request.addFieldInHeader("serviceId", ICOBISTS.HEADER_STRING_TYPE,
				"InternetBanking.WebApp.Enquiries.Service.Enquiries.ExecuteCardActivation");
		request.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingOrchestrator");
		request.addFieldInHeader("filial", ICOBISTS.HEADER_NUMBER_TYPE, "1");
		request.addFieldInHeader("servicio", ICOBISTS.HEADER_NUMBER_TYPE, "8");
		request.addFieldInHeader("org", ICOBISTS.HEADER_STRING_TYPE, "U");
		request.addFieldInHeader("contextId", ICOBISTS.HEADER_STRING_TYPE, "COBISBV");
		// request.addFieldInHeader("sessionId", ICOBISTS.HEADER_STRING_TYPE,
		// "S");
		request.addFieldInHeader("serviceName", ICOBISTS.HEADER_STRING_TYPE, "cob_procesador..sp_exec_atm_activacion");
		request.addFieldInHeader("perfil", ICOBISTS.HEADER_NUMBER_TYPE, "13");
		// request.addFieldInHeader("ssn_branch", ICOBISTS.HEADER_NUMBER_TYPE,
		// request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("target", ICOBISTS.HEADER_STRING_TYPE, "SPExecutor");

		request.addFieldInHeader("trn", ICOBISTS.HEADER_NUMBER_TYPE, "16061");
		request.addFieldInHeader("ofi", ICOBISTS.HEADER_NUMBER_TYPE, "1");
		// request.addFieldInHeader("serviceExecutionId",
		// ICOBISTS.HEADER_STRING_TYPE, request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
		request.addFieldInHeader("srv", ICOBISTS.HEADER_STRING_TYPE, "BRANCHSRV");
		request.addFieldInHeader("culture", ICOBISTS.HEADER_STRING_TYPE, "ES-EC");
		request.addFieldInHeader("spType", ICOBISTS.HEADER_STRING_TYPE, "Sybase");
		request.addFieldInHeader("lsrv", ICOBISTS.HEADER_STRING_TYPE, "CTSSRV");
		request.addFieldInHeader("user", ICOBISTS.HEADER_STRING_TYPE, "usuariobv");

		request.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "16061");
		request.addFieldInHeader("trn_origen", ICOBISTS.HEADER_STRING_TYPE, "API_IN");

		logger.logInfo("REQUEST activateCardAtm" + request);

		connectorSpeiResponse = executeCoreBanking(request);

		logger.logInfo(METHOD_NAME + "TERMINA ORQUESTRATOR activateCardAtm");

		logger.logInfo(METHOD_NAME + "RESPONSE activateCardAtm: " + connectorSpeiResponse);

		logger.logInfo(METHOD_NAME + "CODE RETURN activateCardAtm " + connectorSpeiResponse.getReturnCode());
		
		return connectorSpeiResponse;

	}
	
	private void deleteFiles(Map<String, Object> aBagSPJavaOrchestration) {
		
		logger.logInfo(CLASS_NAME + " Entrando en deleteFiles: " );
		
		String folderPath = RUTA_COMPENSATION; 

		File folder = new File(folderPath);

		// Verificar si la carpeta existe
		if (folder.exists() && folder.isDirectory()) {
			File[] files = folder.listFiles();

			if (files != null) {
				for (File file : files) {
					if (file.isFile()) {
						// Eliminar el archivo
						if (file.delete()) {
							logger.logDebug("Archivo eliminado: " + file.getName());
							aBagSPJavaOrchestration.put(file.getName(), file.getName());
						} else {
							logger.logDebug("No se pudo eliminar el archivo: " + file.getName());
						}
					}
				}
			}
		} else {
			logger.logDebug("La carpeta especificada no existe o no es una carpeta válida.");
		}
	}
	
}
