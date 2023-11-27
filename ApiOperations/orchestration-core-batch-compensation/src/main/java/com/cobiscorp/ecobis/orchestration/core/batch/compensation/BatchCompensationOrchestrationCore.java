/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.batch.compensation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
		
		IProcedureResponse wAccountsResp = new ProcedureResponseAS();
		
		deleteFiles(aBagSPJavaOrchestration);
		
		execDownloadFile(aRequest, aBagSPJavaOrchestration);
		
		jsonProcess(aRequest, aBagSPJavaOrchestration);
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Response " + wAccountsResp.toString());
			logger.logInfo(CLASS_NAME + " Saliendo de executeCompensation");
		}

		return wAccountsResp;
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

                                boolean cont = registerInitialLogBd(aBagSPJavaOrchestration);
                                
                                // Objeto Content
                                if (rootObject.has("CONTENT") && cont) {
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
                                                    }else{
                                                    	aBagSPJavaOrchestration.put("ente", 0);
                                                		aBagSPJavaOrchestration.put("account", 0);
                                                		aBagSPJavaOrchestration.put("codeErrorApi", "-1");
                                                		aBagSPJavaOrchestration.put("messageError", "No genera transaccion");
                                                		aBagSPJavaOrchestration.put("referenceCode","0");
                                                		aBagSPJavaOrchestration.put("transactionType","NO APLICA");
                                                		responseTransactionBag.put("-1", "No genera transaccion");
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
            }else{
            	logger.logInfo("No existe archivos a procesar");
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
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "16875");
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
				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "16876");
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
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "16875");
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
				reqTMPLocal.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "16876");
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

	
	private boolean registerInitialLogBd(Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();
		boolean valida = true;
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
		if(wProductsQueryResp.getReturnCode()!=0){
			valida = false;
			logger.logInfo(CLASS_NAME + " ARCHIVO DUPLICADO NO PROCESADO: " + aBagSPJavaOrchestration.get("fileName").toString());
			aBagSPJavaOrchestration.put("registrosProcesados", 0);
			
		}
		
		return valida;
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
