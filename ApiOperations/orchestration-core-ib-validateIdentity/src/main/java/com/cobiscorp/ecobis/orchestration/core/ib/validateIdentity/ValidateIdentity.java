
package com.cobiscorp.ecobis.orchestration.core.ib.validateIdentity;
import java.util.HashMap;
import java.util.Map;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.csp.util.CSPUtil;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseWSAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.QueryProducts;
import com.cobiscorp.cobis.cis.sp.java.orchestration.CISResponseManagmentHelper;
import com.cobiscorp.cobis.csp.services.IProvider;
import com.cobiscorp.cobis.csp.services.IThreshold;

@Component(name = "ValidateIdentity", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class 
})
@Properties(value = {
 @Property(name = "service.description", value = "ValidateIdentity"),
 @Property(name = "service.vendor", value = "COBISCORP"),
 @Property(name = "service.version", value = "1.0.0"),
 @Property(name = "service.identifier", value =  "ValidateIdentity"),
 @Property(name = "service.spName", value = "cob_procesador..sp_validate_identity")
 })
public class ValidateIdentity extends SPJavaOrchestrationBase {
	 
	 ILogger logger = this.getLogger();
	 private static final String CLASS_NAME = "ValidateIdentity";
	 private static final String CONNECTOR_TRANSACTION = "18500081";
	 private static final String MAIN_TRANSACTION = "18500091";
	 private static final String PARAMS_IN_TYPE = "@i_type";
	 private static final String PARAMS_IN_IMAGE_ANVERSO = "@i_imageAnverso";
	 private static final String PARAMS_IN_IMAGE_REVERSO = "@i_imageReverso";
	 private static final String PARAMS_IN_IMAGE_DOMICILE = "@i_imageDomicile";
	 private String errorCode;
	 private String errorMessage;
	 private String verificationNumber;
	 private String eventName;
	 //private static final String PARAMS_OUT_SALIDA = "@o_salida";
	 
	 
	 public void loadConfiguration(IConfigurationReader aConfigurationReader) {
 
	 }
 
	 @Override
	 public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		 boolean isFailed=false;
		 
		 
		 if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + " Start-executeJavaOrchestration ValidateIdentity--->");
		 }
		 
		//Agregar Header bloque Success
		ResultSetHeader metaDataSuccess = new ResultSetHeader();
		metaDataSuccess.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));
		IResultSetData dataSuccess = new ResultSetData();
		IResultSetRow rowSuccess = new ResultSetRow();
		
		// Agregar Header bloque Message
		IResultSetHeader metaDataMessage = new ResultSetHeader();
		metaDataMessage.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaDataMessage.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));
		IResultSetData dataMessage = new ResultSetData();
		IResultSetRow rowMessagge = new ResultSetRow();
			
		// Agregar Header bloque VerificationNumber
		
		IResultSetHeader metaDataVerificationNumber = new ResultSetHeader();
		metaDataVerificationNumber.addColumnMetaData(new ResultSetHeaderColumn("verificationNumber", ICTSTypes.SQLVARCHAR, 100));
		IResultSetData dataVerificationNumber = new ResultSetData();
		IResultSetRow rowVerificationNumber = new ResultSetRow();
		
		// Agregar Header bloque EventName
		IResultSetHeader metaDataEventName = new ResultSetHeader();
		metaDataEventName.addColumnMetaData(new ResultSetHeaderColumn("eventName", ICTSTypes.SQLVARCHAR, 1000));
		IResultSetData dataEventName = new ResultSetData();
		IResultSetRow rowEventName = new ResultSetRow();
		
		
		IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
		
		
		
		 try {
			 if (logger.isInfoEnabled()) {
					logger.logInfo(CLASS_NAME + " Request original:" + anOriginalRequest.getProcedureRequestAsString());
			 }
			 
			 aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
			 
			
			 /* OPERACION 1 - VALIDAR DATOS DE ENTRADA---------------------------------------- */
			if (anOriginalRequest.readValueParam(PARAMS_IN_TYPE) == null
					|| anOriginalRequest.readValueParam(PARAMS_IN_TYPE).trim().equals("")) {
				errorCode = Utility.codeNullType;
				errorMessage = Utility.messageNullValidateIdentityType;
				isFailed = true;
							
			}else if (anOriginalRequest.readValueParam(PARAMS_IN_IMAGE_ANVERSO) == null
					|| anOriginalRequest.readValueParam(PARAMS_IN_IMAGE_ANVERSO).trim().equals("")) {
				errorCode = Utility.codeEmptyImageAnverso;
				errorMessage = Utility.messageNullValidateIdentityImageAnverso;
				isFailed = true;
					
			}else{
				
				isFailed = false;
			}	
			
			/* OPERACION 2 - CONNECTOR------------------------------------------------------ */
			if (!isFailed) {
				aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
				if (connectorExecution(aBagSPJavaOrchestration, anOriginalRequest)) {
					if (logger.isInfoEnabled()) {
						logger.logDebug("Finaliza Orchestration ValidateIdentity");
					}					 
					
				} else {
					if (logger.isInfoEnabled()) {
						logger.logDebug("ERROR AL EJECUTAR ORQUESTACION ValidateIdentity");
					}
				}
				
			}else {
				//Agregar Header bloque Success
				
				rowSuccess.addRowData(1, new ResultSetRowColumnData(false, "false"));
				dataSuccess.addRow(rowSuccess);
				IResultSetBlock resultsetBlockSuccess = new ResultSetBlock(metaDataSuccess, dataSuccess);
				wProcedureRespFinal.addResponseBlock(resultsetBlockSuccess);
				
				
				// Agregar Header bloque Message
				
				rowMessagge.addRowData(1, new ResultSetRowColumnData(false, errorCode));
				rowMessagge.addRowData(2, new ResultSetRowColumnData(false, errorMessage));
				dataMessage.addRow(rowMessagge);
				IResultSetBlock resultsetBlockMessage = new ResultSetBlock(metaDataMessage, dataMessage);
				wProcedureRespFinal.addResponseBlock(resultsetBlockMessage);
				
				// Agregar Header bloque VerificationNumber
				
				rowVerificationNumber.addRowData(1, new ResultSetRowColumnData(false, "0"));
				dataVerificationNumber.addRow(rowVerificationNumber);
				IResultSetBlock resultsetBlockVerificationNumber= new ResultSetBlock(metaDataVerificationNumber, dataVerificationNumber);
				wProcedureRespFinal.addResponseBlock(resultsetBlockVerificationNumber);
				
				// Agregar Header bloque EventName
				
				rowEventName.addRowData(1, new ResultSetRowColumnData(false, "0"));
				dataEventName.addRow(rowEventName);
				IResultSetBlock resultsetBlockEventName= new ResultSetBlock(metaDataEventName, dataEventName);
				wProcedureRespFinal.addResponseBlock(resultsetBlockEventName);
				
				wProcedureRespFinal.setReturnCode(400);		
				wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, ICSP.ERROR_EXECUTION_SERVICE);
				return wProcedureRespFinal;	
				
			}
			 
			
			 return processResponse(anOriginalRequest, aBagSPJavaOrchestration);	
			 
			 
			 
		 }catch (Exception e) {
			
			logger.logError(CLASS_NAME +" *********  Error in " + e.getMessage(), e);
			
			e.printStackTrace();
						
			
			
			//Agregar Header bloque Success
			
			rowSuccess.addRowData(1, new ResultSetRowColumnData(false, "false"));
			dataSuccess.addRow(rowSuccess);
			IResultSetBlock resultsetBlockSuccess = new ResultSetBlock(metaDataSuccess, dataSuccess);
			wProcedureRespFinal.addResponseBlock(resultsetBlockSuccess);
			
			
			// Agregar Header bloque Message
			
			rowMessagge.addRowData(1, new ResultSetRowColumnData(false, Utility.codeInternalError));
			rowMessagge.addRowData(2, new ResultSetRowColumnData(false, Utility.messagInternalError));
			dataMessage.addRow(rowMessagge);
			IResultSetBlock resultsetBlockMessage = new ResultSetBlock(metaDataMessage, dataMessage);
			wProcedureRespFinal.addResponseBlock(resultsetBlockMessage);
			
			// Agregar Header bloque VerificationNumber
			
			rowVerificationNumber.addRowData(1, new ResultSetRowColumnData(false, "0"));
			dataVerificationNumber.addRow(rowVerificationNumber);
			IResultSetBlock resultsetBlockVerificationNumber= new ResultSetBlock(metaDataVerificationNumber, dataVerificationNumber);
			wProcedureRespFinal.addResponseBlock(resultsetBlockVerificationNumber);
			
			// Agregar Header bloque EventName
			
			rowEventName.addRowData(1, new ResultSetRowColumnData(false, "0"));
			dataEventName.addRow(rowEventName);
			IResultSetBlock resultsetBlockEventName= new ResultSetBlock(metaDataEventName, dataEventName);
			wProcedureRespFinal.addResponseBlock(resultsetBlockEventName);
			
						
			wProcedureRespFinal.setReturnCode(500);
			
			
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, ICSP.ERROR_EXECUTION_SERVICE);
			return wProcedureRespFinal;				
		 }	
		 
	 }

	 private boolean connectorExecution(Map<String, Object> aBagSPJavaOrchestration,
				IProcedureRequest anOriginalRequest) {
			return executeConnector(aBagSPJavaOrchestration, "(service.identifier=MatiConnector)", CONNECTOR_TRANSACTION,
					anOriginalRequest);
	}
	 
	 
	 private boolean executeConnector(Map<String, Object> aBagSPJavaOrchestration, String serviceIdentifier,
				String transactionNumber, IProcedureRequest anOriginalRequest) {
			if (logger.isDebugEnabled()) {
				logger.logDebug("Inicia metodo executeConnector ValidateIdentity");
			}

			IProcedureRequest procedureRequest = null;
			procedureRequest = anOriginalRequest;

			setGenericBindingHeaderFields(procedureRequest, transactionNumber);
			aBagSPJavaOrchestration.put(CONNECTOR_TYPE, serviceIdentifier);

			IProcedureResponse connectorResponse = executeProvider(procedureRequest, aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put("connectorResponse", connectorResponse);

			logger.logInfo("executeConnector response ValidateIdentity---------------------------------------");
			logger.logInfo(connectorResponse.getProcedureResponseAsString());

			if (logger.isDebugEnabled()) {
				logger.logDebug("Finaliza metodo executeConnector ValidateIdentity");
			}
			return !connectorResponse.hasError();
		}
	 
	 private void setGenericBindingHeaderFields(IProcedureRequest procedureRequest, String transactionNumber) {
			if (logger.isDebugEnabled()) {
				logger.logDebug("Inicia metodo setGenericBindingHeaderFields ValidateIdentity");
			}
			procedureRequest.addFieldInHeader(IProvider.EXTERNAL_PROVIDER, ICOBISTS.HEADER_STRING_TYPE, "0");
			procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TIMEOUT, ICOBISTS.HEADER_STRING_TYPE, "30000");
			procedureRequest.addFieldInHeader(IThreshold.CHANNEL, ICOBISTS.HEADER_STRING_TYPE, "E");
			procedureRequest.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, MAIN_TRANSACTION);
			procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, transactionNumber);
			if (logger.isDebugEnabled()) {
				logger.logDebug("Finaliza metodo setGenericBindingHeaderFields ValidateIdentity");
			}
		}

	 @Override
		public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
				java.util.Map aBagSPJavaOrchestration) {
		 
		 	//Agregar Header bloque Success
			ResultSetHeader metaDataSuccess = new ResultSetHeader();
			metaDataSuccess.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));
			IResultSetData dataSuccess = new ResultSetData();
			IResultSetRow rowSuccess = new ResultSetRow();
			
			// Agregar Header bloque Message
			IResultSetHeader metaDataMessage = new ResultSetHeader();
			metaDataMessage.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
			metaDataMessage.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));
			IResultSetData dataMessage = new ResultSetData();
			IResultSetRow rowMessagge = new ResultSetRow();
				
			// Agregar Header bloque VerificationNumber
			
			IResultSetHeader metaDataVerificationNumber = new ResultSetHeader();
			metaDataVerificationNumber.addColumnMetaData(new ResultSetHeaderColumn("verificationNumber", ICTSTypes.SQLVARCHAR, 100));
			IResultSetData dataVerificationNumber = new ResultSetData();
			IResultSetRow rowVerificationNumber = new ResultSetRow();
			
			// Agregar Header bloque EventName
			IResultSetHeader metaDataEventName = new ResultSetHeader();
			metaDataEventName.addColumnMetaData(new ResultSetHeaderColumn("eventName", ICTSTypes.SQLVARCHAR, 1000));
			IResultSetData dataEventName = new ResultSetData();
			IResultSetRow rowEventName = new ResultSetRow();
		 
			// OPERACION 3 - OBTENER DATA-----------------------------------------
			IProcedureResponse procedureResponse = (IProcedureResponse) aBagSPJavaOrchestration.get("connectorResponse");
			logger.logDebug("Datos ResulSet en Response ValidateIdentity: "+procedureResponse.getProcedureResponseAsString());

			CSPUtil.copyHeaderFields((IProcedureRequest) aBagSPJavaOrchestration.get("anOriginalRequest"),procedureResponse);
					
			if (procedureResponse != null && procedureResponse.getResultSetListSize() > 0) {
				logger.logDebug("Datos ResulSet de la respuesta final ValidateIdentity : "+procedureResponse.getProcedureResponseAsString());
				
				try {
					IResultSetRow[] rowsTemp = procedureResponse.getResultSet(0).getData().getRowsAsArray();
					IResultSetRowColumnData[] rows = rowsTemp[0].getColumnsAsArray();
					
					verificationNumber = String.valueOf(rows[0].getValue());//procedureResponse.getResultSetRows(0).get(0));
					eventName=String.valueOf(rows[1].getValue());//procedureResponse.getResultSetRows(0).get(1)
					logger.logDebug("Seteando la respuesta final ValidateIdentity... verificationNumber: "+verificationNumber+" , eventName: "+eventName);

				}catch(Exception e) {					
					verificationNumber=String.valueOf(1);
					eventName=String.valueOf(1);				
					logger.logDebug("Error en el seteo de la respuesta final ValidateIdentity... verificationNumber: "+verificationNumber+" , eventName: "+eventName);

				}
								
				//Agregar Header bloque Success
				rowSuccess.addRowData(1, new ResultSetRowColumnData(false, "true"));
				dataSuccess.addRow(rowSuccess);
				IResultSetBlock resultsetBlockSuccess = new ResultSetBlock(metaDataSuccess, dataSuccess);
				procedureResponse.addResponseBlock(resultsetBlockSuccess);
				
				
				// Agregar Header bloque Message
				
				rowMessagge.addRowData(1, new ResultSetRowColumnData(false,"0"));
				rowMessagge.addRowData(2, new ResultSetRowColumnData(false, "SUCCESS "));
				dataMessage.addRow(rowMessagge);
				IResultSetBlock resultsetBlockMessage = new ResultSetBlock(metaDataMessage, dataMessage);
				procedureResponse.addResponseBlock(resultsetBlockMessage);
				
				// Agregar Header bloque VerificationNumber
				
				rowVerificationNumber.addRowData(1, new ResultSetRowColumnData(false, verificationNumber));
				dataVerificationNumber.addRow(rowVerificationNumber);
				IResultSetBlock resultsetBlockVerificationNumber= new ResultSetBlock(metaDataVerificationNumber, dataVerificationNumber);
				procedureResponse.addResponseBlock(resultsetBlockVerificationNumber);
				
				// Agregar Header bloque EventName
				
				rowEventName.addRowData(1, new ResultSetRowColumnData(false, eventName));
				dataEventName.addRow(rowEventName);
				IResultSetBlock resultsetBlockEventName= new ResultSetBlock(metaDataEventName, dataEventName);
				procedureResponse.addResponseBlock(resultsetBlockEventName);
				
				procedureResponse.setReturnCode(204);
	
				procedureResponse.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, ICSP.SUCCESS);
				
				
			}else {
				logger.logDebug("Sin datos ResulSet de la respuesta final ValidateIdentity");

				//Agregar Header bloque Success
				rowSuccess.addRowData(1, new ResultSetRowColumnData(false, "false"));
				dataSuccess.addRow(rowSuccess);
				IResultSetBlock resultsetBlockSuccess = new ResultSetBlock(metaDataSuccess, dataSuccess);
				procedureResponse.addResponseBlock(resultsetBlockSuccess);
				
				
				// Agregar Header bloque Message
				
				rowMessagge.addRowData(1, new ResultSetRowColumnData(false,"40008"));
				rowMessagge.addRowData(2, new ResultSetRowColumnData(false, "There is no information for the ValidateIdentity"));
				dataMessage.addRow(rowMessagge);
				IResultSetBlock resultsetBlockMessage = new ResultSetBlock(metaDataMessage, dataMessage);
				procedureResponse.addResponseBlock(resultsetBlockMessage);
				
				// Agregar Header bloque VerificationNumber
				
				rowVerificationNumber.addRowData(1, new ResultSetRowColumnData(false, "0"));
				dataVerificationNumber.addRow(rowVerificationNumber);
				IResultSetBlock resultsetBlockVerificationNumber= new ResultSetBlock(metaDataVerificationNumber, dataVerificationNumber);
				procedureResponse.addResponseBlock(resultsetBlockVerificationNumber);
				
				// Agregar Header bloque EventName
				
				rowEventName.addRowData(1, new ResultSetRowColumnData(false, "0"));
				dataEventName.addRow(rowEventName);
				IResultSetBlock resultsetBlockEventName= new ResultSetBlock(metaDataEventName, dataEventName);
				procedureResponse.addResponseBlock(resultsetBlockEventName);
				
				procedureResponse.setReturnCode(204);
	
				procedureResponse.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, ICSP.ERROR_EXECUTION_SERVICE);
				
			}
	
				
				return procedureResponse;
		}
			
			
	
	 
	 


}

