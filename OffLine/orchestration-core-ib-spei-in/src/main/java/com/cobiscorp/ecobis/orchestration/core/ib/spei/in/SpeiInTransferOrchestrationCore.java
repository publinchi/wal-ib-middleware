package com.cobiscorp.ecobis.orchestration.core.ib.spei.in;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.ecobis.orchestration.core.ib.transfer.template.TransferInOfflineTemplate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.utils.transfers.EncryptData;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Notification;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NotificationDetail;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceReexecutionComponent;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSelfAccountTransfers;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;

/**
 * Plugin of between accounts transfers
 *
 * @since Dec 05, 2014
 * @author mvelez
 * @version 1.0.0
 *
 */
@Component(name = "SpeiInTransferOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "SpeiInTransferOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "SpeiInTransferOrchestrationCore") })
public class SpeiInTransferOrchestrationCore extends TransferInOfflineTemplate {

	/**
	 * Read configuration of parent component
	 */

	private static String privateKeyAes="";

	private java.util.Properties properties;
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(" loadConfiguration INI SpeiInTransferOrchestrationCore");
		}
		properties = arg0.getProperties("//property");
	}
	/**
	 * Instance logger component
	 */
	private static ILogger logger = LogFactory.getLogger(SpeiInTransferOrchestrationCore.class);
	private static final String CORESERVICEMONETARYTRANSACTION = "coreServiceMonetaryTransaction";
	private String validaRiesgo = "";


	@Reference(referenceInterface = ICoreServiceMonetaryTransaction.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceMonetaryTransaction", unbind = "unbindCoreServiceMonetaryTransaction")
	protected ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction;
	
	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceSelfAccountTransfers.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceSelfAccountTransfers", unbind = "unbindCoreServiceSelfAccountTransfers")
	private ICoreServiceSelfAccountTransfers coreServiceSelfAccountTransfers;

	@Override
	public ICoreService getCoreService() {
		return coreService;
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreService coreService;

	@Reference(referenceInterface = ICoreServiceSendNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceNotification", unbind = "unbindCoreServiceNotification")
	public ICoreServiceSendNotification coreServiceNotification;

	/**
	 * /** Execute transfer first step of service
	 * <p>
	 * This method is the main executor of transactional contains the original
	 * input parameters.
	 *
	 * @param anOriginalRequest
	 *            - Information original sended by user's.
	 * @param aBagSPJavaOrchestration
	 *            - Object dictionary transactional steps.
	 *
	 * @return
	 *         <ul>
	 *         <li>IProcedureResponse - Represents the service execution.</li>
	 *         </ul>
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = null;
		
		if (logger.isInfoEnabled())
			logger.logInfo("SpeiInTransferOrchestrationCore: executeJavaOrchestration");

		Map<String, Object> mapInterfaces = new HashMap<String, Object>();

		mapInterfaces.put("coreServer", coreServer);
		mapInterfaces.put("coreService", coreService);
		mapInterfaces.put("coreServiceNotification", coreServiceNotification);
		mapInterfaces.put("coreServiceSelfAccountTransfers", coreServiceSelfAccountTransfers);

		Utils.validateComponentInstance(mapInterfaces);
		aBagSPJavaOrchestration.put(TRANSFER_NAME, "TRANFERENCIA SPEI IN");
		aBagSPJavaOrchestration.put(CORESERVICEMONETARYTRANSACTION, coreServiceMonetaryTransaction);	
		try 
		{
			response = executeStepsTransactionsBase(anOriginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}

		if (response != null && !response.hasError() && response.getReturnCode() == 0) {
			String idDevolucion = response.readValueParam("@o_id_causa_devolucion");
			if(null == idDevolucion || "0".equals(idDevolucion)){
				notifySpei(anOriginalRequest, aBagSPJavaOrchestration);
				registerWebhook(anOriginalRequest, aBagSPJavaOrchestration);
			}
	
		}

		return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}

	private void registerWebhook(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration){

		int lengthCtades = anOriginalRequest.readValueParam("@i_cuentaBeneficiario").length();
		String identificationType = null;
		
		if (lengthCtades == 18) {
			identificationType = "clabe";
		} else {
			identificationType = "account number";
		}
		aBagSPJavaOrchestration.put("destinationAccountType", identificationType);

		IProcedureRequest procedureRequest = new ProcedureRequestAS();

		procedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500069");
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);

		procedureRequest.setSpName("cob_ahorros..sp_ah_spei_entrante");
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "253");
		procedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "253");
		procedureRequest.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cuentaBeneficiario"));

		procedureRequest.addInputParam("@i_cuenta_beneficiario", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cuentaBeneficiario"));
	
		procedureRequest.addInputParam("@i_operacion", ICTSTypes.SYBCHAR, "C");
		procedureRequest.addInputParam("@i_tipo_cuenta_dest", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_tipoCuentaBeneficiario"));
		procedureRequest.addInputParam("@i_tipo_cuenta_orig", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_tipoCuentaOrdenante"));
	
		IProcedureResponse ccProcedureResponse =  executeCoreBanking(procedureRequest);

		IResultSetRow resultSetRow = ccProcedureResponse.getResultSet(1).getData().getRowsAsArray()[0];
		IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();

		String clientCode = ccProcedureResponse.getResultSetRowColumnData(1, 1, 1).isNull()?"":ccProcedureResponse.getResultSetRowColumnData(1, 1, 1).getValue();
		String cuentaOrig = ccProcedureResponse.getResultSetRowColumnData(1, 1, 2).isNull()?"":ccProcedureResponse.getResultSetRowColumnData(1, 1, 2).getValue();
		String cuentaDest = ccProcedureResponse.getResultSetRowColumnData(1, 1, 3).isNull()?"":ccProcedureResponse.getResultSetRowColumnData(1, 1, 3).getValue();
		String sl_fecha = ccProcedureResponse.getResultSetRowColumnData(1, 1, 4).isNull()?"":ccProcedureResponse.getResultSetRowColumnData(1, 1, 4).getValue();

		aBagSPJavaOrchestration.put("destinationAccountType", cuentaDest);
		aBagSPJavaOrchestration.put("originAccountType", cuentaOrig);
		aBagSPJavaOrchestration.put("externalCustId", clientCode);
		aBagSPJavaOrchestration.put("fechaTrn", sl_fecha);
				
		registerAllTransactionSuccess("SPEI_CREDIT", anOriginalRequest,"2040", aBagSPJavaOrchestration);	
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> aBagSPJavaOrchestration) {
		return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
	}

	@Override
	protected ICoreServiceSendNotification getCoreServiceNotification() {
		return coreServiceNotification;
	}

	@Override
	public ICoreServer getCoreServer() {
		return coreServer;
	}

	public IProcedureResponse executeTransfer(Map<String, Object> aBagSPJavaOrchestration) {// throws
																							// CTSServiceException,
																							// CTSInfrastructureException
		IProcedureResponse response = null;
		String codeError = "";
		
		try {
			IProcedureRequest anOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
			response = mappingResponse(executeTransferSpeiIn(anOriginalRequest, aBagSPJavaOrchestration), aBagSPJavaOrchestration);
						
			//Validamos la respuesta para ingresar la transacción fallida en Webhook
			if (aBagSPJavaOrchestration.get("@s_error") != null) {
				
				codeError = aBagSPJavaOrchestration.get("@s_error").toString();
				
				if (!codeError.equals("0")) {
					if (logger.isDebugEnabled()) {
						logger.logDebug("ERROR SPEI IN: "+ aBagSPJavaOrchestration.get("@s_error").toString());															
					}
					IProcedureResponse consulClienteRes = this.consultaCliente(anOriginalRequest);
					
					aBagSPJavaOrchestration.put("code_error", codeError);
					if(aBagSPJavaOrchestration.containsKey("@s_message") && aBagSPJavaOrchestration.get("@s_message") != null){
						aBagSPJavaOrchestration.put("message_error", aBagSPJavaOrchestration.get("@s_message").toString());
					}else{
						aBagSPJavaOrchestration.put("message_error", response.readValueParam("@o_descripcion"));
					}
		        	aBagSPJavaOrchestration.put("destinationAccountType", consulClienteRes.readValueParam("@o_tipo_cuenta_dest"));
					aBagSPJavaOrchestration.put("originAccountType", consulClienteRes.readValueParam("@o_tipo_cuenta_orig"));
					aBagSPJavaOrchestration.put("externalCustId", consulClienteRes.readValueParam("@o_client_code"));
					aBagSPJavaOrchestration.put("causal", "2040");
					
		        	registerTransactionFailed("SPEI_CREDIT", anOriginalRequest, aBagSPJavaOrchestration);
				}
			}
			
		} catch (Exception e){
			logger.logError("AN ERROR OCURRED: ", e);
		}

		return response;
	}

	private IProcedureResponse mappingResponse(IProcedureResponse aResponse, Map<String, Object> aBagSPJavaOrchestration){
		String wInfo = CLASS_NAME+"[mappingResponse] ";
		logger.logInfo(wInfo + INIT_TASK);

		if (aResponse != null && aResponse.getMessageListSize() != 0 && aResponse.readValueParam("@o_descripcion_error") != null) {
			aBagSPJavaOrchestration.put("@s_error", String.valueOf(aResponse.getReturnCode()));
			aBagSPJavaOrchestration.put("@s_message", aResponse.readValueFieldInHeader("messageError").split(":")[1]);
			
			String causaDevolucion = aResponse.readValueParam("@o_id_causa_devolucion");

			if (null != causaDevolucion && !"0".equals(causaDevolucion)) {
				return Util.returnCorrectResponse(aResponse);
			}

			return  Util.returnException(aResponse.getReturnCode(), aResponse.readValueFieldInHeader("messageError").split(":")[1]);

		}

		aResponse.addParam("@o_resultado", ICTSTypes.SQLINT4, 50, String.valueOf(aResponse.getReturnCode()));
		aResponse.addParam("@o_folio", ICTSTypes.SQLVARCHAR, 50, aResponse.readValueParam("@o_id_interno"));
		aResponse.addParam("@o_descripcion", ICTSTypes.SQLVARCHAR, 50, aResponse.readValueParam("@o_descripcion"));
		aResponse.addParam("@o_id_causa_devolucion", ICTSTypes.SQLVARCHAR, 50, aResponse.readValueParam("@o_id_causa_devolucion"));
		aBagSPJavaOrchestration.put("@s_error", aResponse.getReturnCode());

		return aResponse;
	}

	private IProcedureResponse executeTransferSpeiIn(IProcedureRequest anOriginalRequest,
													 Map<String, Object> aBagSPJavaOrchestration){
		String wInfo = CLASS_NAME+"[executeTransferSpeiIn] ";
		logger.logInfo(wInfo+INIT_TASK);
		IProcedureResponse response = new ProcedureResponseAS();
		String valorRiesgo = "";
		String codigoRiesgo = "";
		String mensajeRiesgo = "";
		String estadoRiesgo = "";
		Integer code = 0;
        String message = "success";
		validaRiesgo = getParam(anOriginalRequest, "AERISY", "BVI");
        
		if (logger.isDebugEnabled())
			logger.logDebug("@i_tipoCuentaBeneficiario: " + anOriginalRequest.readValueParam("@i_tipoCuentaBeneficiario"));		
		
		Integer opTcclaveBenAux  = Integer.parseInt(anOriginalRequest.readValueParam("@i_tipoCuentaBeneficiario"));
		String opTcClaveBen = String.format("%02d", opTcclaveBenAux);
		String codTarDeb = getParam(anOriginalRequest, "CODTAR", "BVI");
		String codTelDeb = getParam(anOriginalRequest, "CODTEL", "BVI");
		aBagSPJavaOrchestration.put("codTarDeb", codTarDeb);
		
		if (logger.isInfoEnabled())
			logger.logInfo("codTarDeb: "+codTarDeb);
		
		if (logger.isInfoEnabled())
			logger.logInfo("codTelDeb: "+codTelDeb);

		ServerResponse serverResponse = (ServerResponse) aBagSPJavaOrchestration.get(RESPONSE_SERVER);

		if (logger.isDebugEnabled()){
			logger.logDebug("Status Servidor 2");
			logger.logDebug(serverResponse);
		}

		if(getFromReentryExcecution(aBagSPJavaOrchestration) && serverResponse.getOnLine()){

			if(logger.isInfoEnabled())
			{
				logger.logInfo("Ejecución en reentry - ONLINE");
			}
			IProcedureRequest requestTransfer = this.getRequestTransfer(anOriginalRequest);
			response = executeCoreBanking(requestTransfer);

			if(logger.isInfoEnabled())
			{
				logger.logInfo("Finaliza ejecución directa");
			}

		}else if(!getFromReentryExcecution(aBagSPJavaOrchestration)) {

			if (logger.isInfoEnabled()) {
				logger.logInfo("codTar:" + codTarDeb + " opTcClaveBen:" + opTcClaveBen);
			}
			
			if (logger.isInfoEnabled()) {
				logger.logInfo("codTar:" + codTarDeb + " opTcClaveBen:" + opTcClaveBen);
			}
			
			if (codTarDeb.equals(opTcClaveBen))//validacion de tarjeta de debito llamado a dock JCOS
			{
				response = validateCardAccount(anOriginalRequest, aBagSPJavaOrchestration);
			}
			else if(codTelDeb.equals(opTcClaveBen)) {
				
				response = validateAccountPhone(anOriginalRequest);
			}
			if (response.getReturnCode() != 0) {
				return response;
			}

			// Validar el risk
			if (validaRiesgo.equals("true")) {
				
				if (logger.isDebugEnabled()) {					
					logger.logDebug("Flujo, Valida Resgo");					
				}
				
				response = executeRiskEvaluation(anOriginalRequest, aBagSPJavaOrchestration);
				if(response.getReturnCode() != 0 && !aBagSPJavaOrchestration.containsKey("success_risk")) {
					return response;
				}

				if (aBagSPJavaOrchestration.get("success_risk") != null && !aBagSPJavaOrchestration.get("success_risk").equals("false")) {
					valorRiesgo = aBagSPJavaOrchestration.get("success_risk").toString();

					if (aBagSPJavaOrchestration.get("responseCode") != null) {
						codigoRiesgo = aBagSPJavaOrchestration.get("responseCode").toString();
					}

					if (aBagSPJavaOrchestration.get("message") != null) {
						mensajeRiesgo = aBagSPJavaOrchestration.get("message").toString();
					}

					if (aBagSPJavaOrchestration.get("isOperationAllowed") != null) {
						estadoRiesgo = aBagSPJavaOrchestration.get("isOperationAllowed").toString();
					}
					
					if (logger.isDebugEnabled()) {	
						logger.logDebug("Respuesta RiskEvaluation: " + valorRiesgo + " Código: " + codigoRiesgo + " Estado: " + estadoRiesgo + " Mensaje: " + mensajeRiesgo);
					}
					if (valorRiesgo.equals("true") && estadoRiesgo.equals("true")) {
						response = this.validaLimite(anOriginalRequest, aBagSPJavaOrchestration);

						if (response.getReturnCode() != 0) {
							return response;
						}

						IProcedureRequest requestTransfer = this.getRequestTransfer(anOriginalRequest);

						if (logger.isDebugEnabled()) {
							logger.logDebug(wInfo + "Request accountTransfer: " + requestTransfer.getProcedureRequestAsString());
						}	
							response = executeCoreBanking(requestTransfer);		
							
							if (logger.isDebugEnabled()) {
								logger.logDebug(wInfo + "aBagSPJavaOrchestration SPEI: " + aBagSPJavaOrchestration.toString());
								logger.logDebug(wInfo + "response de central: " + response);
							}
						if(!serverResponse.getOnLine()) { 
							
					        IProcedureResponse responseLocalExecution = updateLocalExecution(anOriginalRequest, aBagSPJavaOrchestration);
					        aBagSPJavaOrchestration.put(RESPONSE_UPDATE_LOCAL, responseLocalExecution);
							
							if (logger.isDebugEnabled()) {
								logger.logDebug(wInfo + "aBagSPJavaOrchestration SPEI: " + aBagSPJavaOrchestration.toString());
								logger.logDebug(wInfo + "response de local: " + response);
							}							
						}

					} else {
						message = "OPERACIÓN NO PERMITIDA";
						code = 2;
						response.setReturnCode(code);
						response.addParam("@o_descripcion", ICTSTypes.SQLVARCHAR, 50, message);
						response.addParam("@o_id_causa_devolucion", ICTSTypes.SQLVARCHAR, 50, code.toString());

						return response;
					}
				} else {
					message = "OPERACIÓN NO PERMITIDA";
					code = 2;
					response.setReturnCode(code);
					response.addParam("@o_descripcion", ICTSTypes.SQLVARCHAR, 50, message);
					response.addParam("@o_id_causa_devolucion", ICTSTypes.SQLVARCHAR, 50, code.toString());

					return response;
				}
			} else {
				
				if (logger.isDebugEnabled()) {					
					logger.logInfo("Flujo, No Valida Resgo");					
				}
				
				response = this.validaLimite(anOriginalRequest, aBagSPJavaOrchestration);

				if (response.getReturnCode() != 0) {
					return response;
				}

				IProcedureRequest requestTransfer = this.getRequestTransfer(anOriginalRequest);			


				if (logger.isDebugEnabled()) {
					logger.logDebug(wInfo + "Request accountTransfer: " + requestTransfer.getProcedureRequestAsString());
				}			
					
					logger.logInfo("Aplicando transaccion online");

					response = executeCoreBanking(requestTransfer);		
					
					if (logger.isDebugEnabled()) {
						logger.logDebug(wInfo + "aBagSPJavaOrchestration SPEI: " + aBagSPJavaOrchestration.toString());
						logger.logDebug(wInfo + "response de central: " + response);
					}
				if(!serverResponse.getOnLine()) { 
					
					if (logger.isDebugEnabled()) {
						logger.logDebug("Aplicando transaccion offline");
					}
					
			        IProcedureResponse responseLocalExecution = updateLocalExecution(anOriginalRequest, aBagSPJavaOrchestration);
			        aBagSPJavaOrchestration.put(RESPONSE_UPDATE_LOCAL, responseLocalExecution);
					
					if (logger.isDebugEnabled()) {
						logger.logDebug(wInfo + "aBagSPJavaOrchestration SPEI: " + aBagSPJavaOrchestration.toString());
						logger.logDebug(wInfo + "response de local: " + response);
					}
				}
				

				if (logger.isDebugEnabled()) {
					logger.logDebug(wInfo + "aBagSPJavaOrchestration SPEI: " + aBagSPJavaOrchestration.toString());
					logger.logDebug(wInfo + "response de central: " + response);
				}
			}
		}
		
		logger.logInfo(wInfo+END_TASK);



		return response;
	}
	
	private IProcedureResponse executeRiskEvaluation(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en executeRiskEvaluation");
		}

		String userSessionId = Util.sessionID();
		
		//Consulta el código del cliente
		IProcedureResponse consultaCliente = this.consultaCliente(aRequest);
		
		if(consultaCliente.getReturnCode() != 0) {
			return consultaCliente;
		}
		
		IProcedureRequest procedureRequest = initProcedureRequest(aRequest);
		
		procedureRequest.setSpName("cob_procesador..sp_conn_risk_evaluation");		
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18700119");
		procedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18700119");
		procedureRequest.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "18700119");
		procedureRequest.addFieldInHeader("trn_origen", ICOBISTS.HEADER_STRING_TYPE, "API_IN");
		procedureRequest.addFieldInHeader("target", ICOBISTS.HEADER_STRING_TYPE, "SPExecutor");
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_MESSAGE_TYPE, ICOBISTS.HEADER_STRING_TYPE, "ProcedureRequest");
		procedureRequest.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
				"(service.identifier=RiskEvaluationOrchestrationCore)");
		procedureRequest.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "executeTransaction");
		procedureRequest.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingOrchestrator");
		
		procedureRequest.addInputParam("@i_customerDetails_externalCustomerId", ICTSTypes.SQLVARCHAR, consultaCliente.readValueParam("@o_client_code"));
		procedureRequest.addInputParam("@i_operation", ICTSTypes.SQLVARCHAR, "SPEI_CREDIT");
		procedureRequest.addInputParam("@i_channelDetails_channel", ICTSTypes.SQLVARCHAR, "SYSTEM");
		procedureRequest.addInputParam("@i_channelDetails_userSessionDetails_userSessionId", ICTSTypes.SQLVARCHAR, userSessionId);
		procedureRequest.addInputParam("@i_transaction_transactionId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_ssn"));
		String transactionDate = unifyDateFormat(aRequest.readValueParam("@i_fechaOperacion"));
		procedureRequest.addInputParam("@i_transaction_transactionDate", ICTSTypes.SQLVARCHAR, transactionDate);
		procedureRequest.addInputParam("@i_transaction_transaction_currency", ICTSTypes.SQLVARCHAR, "MXN");
		procedureRequest.addInputParam("@i_transaction_transaction_amount", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_monto"));
		
		
		if (aBagSPJavaOrchestration.get("@o_id_card") != null) {
			procedureRequest.addInputParam("@i_creditorAccount_identification", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("@o_id_card"));
			procedureRequest.addInputParam("@i_creditorAccount_identificationType", ICTSTypes.SQLVARCHAR, "CARD_ID");
		} else {
			procedureRequest.addInputParam("@i_creditorAccount_identification", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cuentaBeneficiario"));
			int lengthCtades = aRequest.readValueParam("@i_cuentaBeneficiario").length();
			String identificationType = null;
			
			if (lengthCtades == 18) {
				identificationType = "CLABE";
			} else {
				identificationType = "ACCOUNT_NUMBER";
			}

			procedureRequest.addInputParam("@i_creditorAccount_identificationType", ICTSTypes.SQLVARCHAR, identificationType);
		}

		procedureRequest.addInputParam("@i_debitorAccount_identification", ICTSTypes.SQLVARCHAR,  aRequest.readValueParam("@i_cuentaOrdenante"));
		procedureRequest.addInputParam("@i_debitorAccount_identificationType", ICTSTypes.SQLVARCHAR,  "ACCOUNT_NUMBER");

		procedureRequest.addInputParam("@i_autoActionExecution", ICTSTypes.SQLVARCHAR, validaRiesgo ); 

		IProcedureResponse connectorRiskEvaluationResponse = executeCoreBanking(procedureRequest);
		
		if (connectorRiskEvaluationResponse.readValueParam("@o_responseCode") != null)
			aBagSPJavaOrchestration.put("responseCode", connectorRiskEvaluationResponse.readValueParam("@o_responseCode"));

		if (connectorRiskEvaluationResponse.readValueParam("@o_message") != null)
			aBagSPJavaOrchestration.put("message", connectorRiskEvaluationResponse.readValueParam("@o_message"));

		if (connectorRiskEvaluationResponse.readValueParam("@o_success") != null) {
			aBagSPJavaOrchestration.put("success_risk", connectorRiskEvaluationResponse.readValueParam("@o_success"));
			aBagSPJavaOrchestration.put("isOperationAllowed", connectorRiskEvaluationResponse.readValueParam("@o_isOperationAllowed"));
		}
		else{
			aBagSPJavaOrchestration.put("success_risk", "false");
			aBagSPJavaOrchestration.put("isOperationAllowed", "false");
		}

		if(logger.isDebugEnabled())
			logger.logInfo("Response executeRiskEvaluation: "+ connectorRiskEvaluationResponse.getProcedureResponseAsString());
		
		return connectorRiskEvaluationResponse;
	}
	
	private IProcedureResponse validaLimite(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		
		String wInfo = CLASS_NAME+"[validaLimite] ";
		
		if(logger.isInfoEnabled()) {
			logger.logInfo(wInfo + INIT_TASK);
		}
		
		IProcedureRequest procedureRequest = initProcedureRequest(anOriginalRequest);
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		
		Integer tipoCuentaBeneficiario  = Integer.parseInt(anOriginalRequest.readValueParam("@i_tipoCuentaBeneficiario"));
		String tipoCuentaBeneficiarioS = String.format("%02d", tipoCuentaBeneficiario);
		String cuentaBeneficiario = anOriginalRequest.readValueParam("@i_cuentaBeneficiario");
		String codTarDeb = (String)aBagSPJavaOrchestration.get("codTarDeb");

		procedureRequest.setSpName("cob_bvirtual..sp_bv_valida_limites");
		procedureRequest.addInputParam("@i_trn", ICTSTypes.SQLINT4, "18500069");
		procedureRequest.addInputParam("@i_tipo_trn", ICTSTypes.SQLINT4, "253");
		procedureRequest.addInputParam("@i_causal", ICTSTypes.SQLINT4, "2040");
		procedureRequest.addInputParam("@i_monto", ICTSTypes.SYBMONEY, anOriginalRequest.readValueParam("@i_monto"));		
		
		/*if ("10".equals(tipoCuentaBeneficiarioS)) {
			procedureRequest.addInputParam("@i_telefono", ICTSTypes.SQLVARCHAR, cuentaBeneficiario);
		}
		else {*/
		if ("40".equals(tipoCuentaBeneficiarioS)) {
			procedureRequest.addInputParam("@i_clabe", ICTSTypes.SQLVARCHAR, cuentaBeneficiario);
		}else if("03".equals(tipoCuentaBeneficiarioS) || "10".equals(tipoCuentaBeneficiarioS)){
			
			procedureRequest.addInputParam("@i_cuenta", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cuentaBeneficiario"));
		}
		
		Integer code = 0;
        String message = "success";
		IProcedureResponse anProcedureResponse =  executeCoreBanking(procedureRequest);
		
		logDebug("anProcedureResponse: " + anProcedureResponse);
		
		if(anProcedureResponse.getReturnCode() != 0){			
			code = anProcedureResponse.getReturnCode();
			message = anProcedureResponse.getMessage(1).getMessageText();
			anProcedureResponse.addParam("@o_descripcion", ICTSTypes.SQLVARCHAR, 50, message);
			anProcedureResponse.addParam("@o_id_causa_devolucion", ICTSTypes.SQLVARCHAR, 50, code.toString());	
			
			logger.logDebug("Code Error local" + code);
			logger.logDebug("Message Error local" + message);
		}
	
		anProcedureResponse.setReturnCode(code);
		if(code!=0)
			anProcedureResponse.addMessage(code, message);
		
		logger.logInfo(wInfo + END_TASK);
		
		return anProcedureResponse;	
	}

	private IProcedureResponse consultaCliente(IProcedureRequest anOriginalRequest) {
		Integer code = 0;
        String message = "success";
		String wInfo = CLASS_NAME+"[consultaCliente] ";
		
		logger.logInfo(wInfo + INIT_TASK);
		IProcedureRequest procedureRequest = initProcedureRequest(anOriginalRequest);
		procedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500069");
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		procedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		procedureRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		boolean isReentryExecution = "Y".equals(anOriginalRequest.readValueFieldInHeader(REENTRY_EXE));

		procedureRequest.setSpName("cob_ahorros..sp_ah_spei_entrante");
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "253");
		procedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "253");
		procedureRequest.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cuentaBeneficiario"));
		procedureRequest.addInputParam("@i_mon", ICTSTypes.SYBINT4, "0");
		procedureRequest.addInputParam("@i_canal", ICTSTypes.SYBINT4, "8");

		procedureRequest.addInputParam("@i_cuenta_beneficiario", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cuentaBeneficiario"));
		procedureRequest.addInputParam("@i_cuenta_ordenante", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cuentaOrdenante"));
		procedureRequest.addInputParam("@i_clave_rastreo", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_claveRastreo"));
		procedureRequest.addInputParam("@i_operacion", ICTSTypes.SYBCHAR, "V");
		procedureRequest.addInputParam("@i_tipo_cuenta_dest", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_tipoCuentaBeneficiario"));
		procedureRequest.addInputParam("@i_tipo_cuenta_orig", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_tipoCuentaOrdenante"));

		procedureRequest.addOutputParam("@o_id_interno", ICTSTypes.SQLINT4, "");
		procedureRequest.addOutputParam("@o_client_code", ICTSTypes.SQLINT4, "");
		procedureRequest.addOutputParam("@o_cuenta_cobis", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_descripcion_error", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_resultado_error", ICTSTypes.SQLINT4, "");
		procedureRequest.addOutputParam("@o_id_causa_devolucion", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_descripcion", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_tipo_cuenta_dest", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_tipo_cuenta_orig", ICTSTypes.SQLVARCHAR, "");

			
		IProcedureResponse ccProcedureResponse =  executeCoreBanking(procedureRequest);
		
		logDebug("ccProcedureResponse: " + ccProcedureResponse);
		
		if(ccProcedureResponse.getReturnCode() != 0){			
			code = ccProcedureResponse.getReturnCode();
			message = ccProcedureResponse.getMessage(1).getMessageText();
			ccProcedureResponse.addParam("@o_descripcion", ICTSTypes.SQLVARCHAR, 50, message);
			ccProcedureResponse.addParam("@o_id_causa_devolucion", ICTSTypes.SQLVARCHAR, 50, code.toString());	
			
			if (logger.isDebugEnabled()) {
				logger.logDebug("Code Error consultaCliente" + code);
				logger.logDebug("Message Error consultaCliente" + message);
			}
		}
	
		ccProcedureResponse.setReturnCode(code);
		
		if(code!=0)
			ccProcedureResponse.addMessage(code, message);
		
		logger.logInfo(wInfo + END_TASK);
		
		return ccProcedureResponse;	
	}
	
	private IProcedureRequest getRequestTransfer(IProcedureRequest anOriginalRequest) {

		String wInfo = CLASS_NAME+" [getRequestTransfer] ";


		if (logger.isInfoEnabled()){

			logger.logInfo(wInfo + INIT_TASK);
			logger.logInfo(wInfo + INIT_TASK);
			if(anOriginalRequest.readValueFieldInHeader("REENTRY_SSN_TRX")!=null)
				logger.logInfo("JC Apply REENTRY_SSN_TRX" + anOriginalRequest.readValueFieldInHeader("REENTRY_SSN_TRX"));
		}

		IProcedureRequest procedureRequest = initProcedureRequest(anOriginalRequest);
		procedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500069");
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		procedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		if(anOriginalRequest.readValueFieldInHeader("REENTRY_SSN_TRX")!=null)
			procedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_SSN, anOriginalRequest.readValueFieldInHeader("REENTRY_SSN_TRX"));
		procedureRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		boolean isReentryExecution = "Y".equals(anOriginalRequest.readValueFieldInHeader(REENTRY_EXE));

		procedureRequest.setSpName("cob_ahorros..sp_ah_spei_entrante");
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "253");
		procedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "253");
		procedureRequest.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cuentaBeneficiario"));
		procedureRequest.addInputParam("@i_val", ICTSTypes.SYBMONEY, anOriginalRequest.readValueParam("@i_monto"));
		procedureRequest.addInputParam("@i_causa", ICTSTypes.SYBVARCHAR, "2010");
		procedureRequest.addInputParam("@i_causa_comi", ICTSTypes.SYBVARCHAR, "2011");
		procedureRequest.addInputParam("@i_mon", ICTSTypes.SYBINT4, "0");
		procedureRequest.addInputParam("@i_fecha", ICTSTypes.SYBDATETIME, anOriginalRequest.readValueParam("@i_fechaOperacion"));
		procedureRequest.addInputParam("@i_canal", ICTSTypes.SYBINT4, "9");
		if(anOriginalRequest.readValueFieldInHeader("REENTRY_SSN_TRX")!=null)
			procedureRequest.addInputParam("@s_ssn", ICTSTypes.SYBINT4, anOriginalRequest.readValueFieldInHeader("REENTRY_SSN_TRX"));

		procedureRequest.addInputParam("@i_cuenta_beneficiario", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cuentaBeneficiario"));
		procedureRequest.addInputParam("@i_cuenta_ordenante", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cuentaOrdenante"));
		procedureRequest.addInputParam("@i_concepto_pago", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_conceptoPago"));
		procedureRequest.addInputParam("@i_monto", ICTSTypes.SQLMONEY4, anOriginalRequest.readValueParam("@i_monto"));
		procedureRequest.addInputParam("@i_institucion_ordenante", ICTSTypes.SYBINT4, anOriginalRequest.readValueParam("@i_institucionOrdenante"));
		procedureRequest.addInputParam("@i_institucion_beneficiaria", ICTSTypes.SYBINT4, anOriginalRequest.readValueParam("@i_institucionBeneficiaria"));
		procedureRequest.addInputParam("@i_id_spei", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_idSpei"));
		procedureRequest.addInputParam("@i_clave_rastreo", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_claveRastreo"));
		procedureRequest.addInputParam("@i_nombre_ordenante", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_nombreOrdenante"));
		procedureRequest.addInputParam("@i_rfc_curp_ordenante", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_rfcCurpOrdenante"));
		procedureRequest.addInputParam("@i_referencia_numerica", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_referenciaNumerica"));
		procedureRequest.addInputParam("@i_tipo", ICTSTypes.SYBINT4, anOriginalRequest.readValueParam("@i_idTipoPago"));
		procedureRequest.addInputParam("@i_tipo_destino", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_tipoCuentaBeneficiario"));
		procedureRequest.addInputParam("@i_operacion", ICTSTypes.SYBCHAR, "I");
		procedureRequest.addInputParam("@i_xml_request", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_string_request"));
		procedureRequest.addInputParam("@i_tipo_ejecucion", ICTSTypes.SQLVARCHAR, isReentryExecution ? "F" : "L");

		procedureRequest.addOutputParam("@o_id_interno", ICTSTypes.SQLINT4, "");
		procedureRequest.addOutputParam("@o_nombre_beneficiario", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_rfc_curp_beneficiario", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_descripcion_error", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_resultado_error", ICTSTypes.SQLINT4, "");
		procedureRequest.addOutputParam("@o_id_causa_devolucion", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_descripcion", ICTSTypes.SQLVARCHAR, "");


		logger.logInfo(wInfo + END_TASK);

		return procedureRequest;
	}

	private void notifySpei (IProcedureRequest anOriginalRequest, java.util.Map map) {

		try {
			logger.logInfo(CLASS_NAME + "REENTRY_EXE" + anOriginalRequest.readValueFieldInHeader(REENTRY_EXE));
			if(Boolean.TRUE.equals("Y".equals(anOriginalRequest.readValueFieldInHeader(REENTRY_EXE)))){
				return;
			}

			logger.logInfo(CLASS_NAME + "Enviando notificacion spei");

			IProcedureRequest procedureRequest = initProcedureRequest(anOriginalRequest);

			String cuentaClabe = anOriginalRequest.readValueParam("@i_cuentaBeneficiario");

			logger.logInfo(CLASS_NAME + "using clabe account account " + cuentaClabe);

			procedureRequest.setSpName("cob_bvirtual..sp_bv_enviar_notif_ib");
			procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S',"local");
			procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N',"1800195");

			procedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "1800195");
			procedureRequest.addInputParam("@i_servicio", ICTSTypes.SQLINT1, "8");
			procedureRequest.addInputParam("@i_tipo_mensaje", ICTSTypes.SQLCHAR, "F");
			procedureRequest.addInputParam("@i_notificacion", ICTSTypes.SYBVARCHAR, "N145");
			procedureRequest.addInputParam("@i_tipo", ICTSTypes.SQLVARCHAR, "I");
			procedureRequest.addInputParam("@i_producto", ICTSTypes.SQLINT1, "18");
			procedureRequest.addInputParam("@i_transaccion_id", ICTSTypes.SQLINT1, "0");
			procedureRequest.addInputParam("@i_canal", ICTSTypes.SQLINT1, "8");
			procedureRequest.addInputParam("@i_origen", ICTSTypes.SQLVARCHAR, "spei");
			procedureRequest.addInputParam("@i_clabe", ICTSTypes.SQLVARCHAR, cuentaClabe);
			procedureRequest.addInputParam("@i_s", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_referenciaNumerica"));
			procedureRequest.addInputParam("@i_c1", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cuentaOrdenante"));
			procedureRequest.addInputParam("@i_c2", ICTSTypes.SQLVARCHAR, cuentaClabe);
			procedureRequest.addInputParam("@i_v2", ICTSTypes.SQLVARCHAR, String.valueOf(  anOriginalRequest.readValueParam("@i_monto")));
			procedureRequest.addInputParam("@i_r", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_conceptoPago"));
			procedureRequest.addInputParam("@i_aux9", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_claveRastreo"));
			procedureRequest.addInputParam("@i_aux8", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_nombreOrdenante"));

			IProcedureResponse procedureResponseLocal = executeCoreBanking(procedureRequest);

			logger.logInfo("jcos proceso de notificaciom terminado");

		}catch(Exception xe) {
			logger.logError("Error en la notficación de spei recibida", xe);
		}
	}

	private void logDebug(Object aMessage){
		if(logger.isDebugEnabled()){
			logger.logDebug(aMessage);
		}
	}

	@Override
	public ICoreServiceReexecutionComponent getCoreServiceReexecutionComponent() {
		return null;
	}

	@Override
	protected IProcedureResponse validateCentralExecution(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		return null;
	}

	@Override
	public NotificationRequest transformNotificationRequest(IProcedureRequest anOriginalRequest,
			OfficerByAccountResponse anOfficer, Map<String, Object> aBagSPJavaOrchestration) {
		
		NotificationRequest notificationRequest = new NotificationRequest();
		notificationRequest.setOriginalRequest(anOriginalRequest);
		Notification notification = new Notification();

		Client client = new Client();
		client.setIdCustomer(anOriginalRequest.readValueParam("@s_cliente"));

		Product product = new Product();
		product.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")));
		if (!Utils.isNull(anOriginalRequest.readParam("@i_cta"))) {
			product.setProductNumber(anOriginalRequest.readValueParam("@i_cta"));
		}
		if (product.getProductType() == 3)
			notification.setId("N19");
		else
			notification.setId("N20");

		NotificationDetail notificationDetail = new NotificationDetail();

		if (!Utils.isNull(anOfficer.getOfficer().getOfficerEmailAdress()))
			notificationDetail.setEmailClient(anOfficer.getOfficer().getOfficerEmailAdress());
		if (!Utils.isNull(anOfficer.getOfficer().getAcountEmailAdress()))
			notificationDetail.setEmailOficial(anOfficer.getOfficer().getAcountEmailAdress());

		if (!Utils.isNull(anOriginalRequest.readParam("@i_cta")))
			notificationDetail.setAccountNumberDebit(anOriginalRequest.readValueParam("@i_cta"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_cta_des")))
			notificationDetail.setAccountNumberCredit(anOriginalRequest.readValueParam("@i_cta_des"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_concepto")))
			notificationDetail.setNote(anOriginalRequest.readValueParam("@i_concepto"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_mon"))) {
			notificationDetail.setCurrencyId1(anOriginalRequest.readValueParam("@i_mon"));
			notificationDetail.setCurrencyId2(anOriginalRequest.readValueParam("@i_mon"));
		}

		if (!Utils.isNull(anOriginalRequest.readParam("@i_val")))
			notificationDetail.setValue(anOriginalRequest.readValueParam("@i_val"));

		if (!Utils.isNull(anOriginalRequest.readParam("@s_date")))
			notificationDetail.setDateNotification(anOriginalRequest.readValueParam("@s_date"));
		if (!Utils.isNull(anOriginalRequest.readParam("@s_ssn")))
			notificationDetail.setReference(anOriginalRequest.readValueParam("@s_ssn"));
		if (!Utils.isNull(anOriginalRequest.readParam("@i_nom_cliente_benef")))
			notificationDetail.setAuxiliary1(anOriginalRequest.readValueParam("@i_nom_cliente_benef"));

		notificationRequest.setClient(client);
		notificationRequest.setNotification(notification);
		notificationRequest.setNotificationDetail(notificationDetail);
		notificationRequest.setOriginProduct(product);



		return notificationRequest;
	}

	@Override
	protected void addParametersRequestUpdateLocal(IProcedureRequest aProcedureRequest,
			IProcedureRequest anOriginalRequest) {
		// TODO Auto-generated method stub

	}
	private String getParam(IProcedureRequest anOriginalRequest, String nemonico, String producto) {
    	logger.logDebug("Begin flow, getOperatingInstitutionFromParameters");
		
		IProcedureRequest reqTMPCentral = (initProcedureRequest(anOriginalRequest));		
		reqTMPCentral.setSpName("cobis..sp_parametro");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "central");
		reqTMPCentral.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "Q");
		reqTMPCentral.addInputParam("@i_nemonico",ICTSTypes.SQLVARCHAR, nemonico);
		reqTMPCentral.addInputParam("@i_producto",ICTSTypes.SQLVARCHAR, producto);	 
	    reqTMPCentral.addInputParam("@i_modo",ICTSTypes.SQLINT4, "4");

	    IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);
		
		if (logger.isInfoEnabled()) {
			logger.logDebug("Ending flow, getOperatingInstitutionFromParameters with wProcedureResponseCentral: " + wProcedureResponseCentral.getProcedureResponseAsString());
		}
		
		if (!wProcedureResponseCentral.hasError() && wProcedureResponseCentral.getResultSetListSize() > 0) {
			
			IResultSetRow[] resultSetRows = wProcedureResponseCentral.getResultSet(1).getData().getRowsAsArray();
			
			if (resultSetRows.length > 0) {
				IResultSetRowColumnData[] columns = resultSetRows[0].getColumnsAsArray();
				return columns[2].getValue();
			} 
		} 
		
		return "";
	}
	
	
	private IProcedureResponse findCardByPanConector(Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureResponse connectorAccountResponse = null;

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		aBagSPJavaOrchestration.remove("trn_virtual");
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en findCardByPanConector");
		}
		try {
			// PARAMETROS DE ENTRADA		
			anOriginalRequest.addInputParam("@i_pan", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("pan"));
			anOriginalRequest.addInputParam("@i_iv", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("iv"));
			anOriginalRequest.addInputParam("@i_aes", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("aes"));
			anOriginalRequest.addInputParam("@i_mode", ICTSTypes.SQLVARCHAR, "GCM");
			anOriginalRequest.addInputParam("@i_operation", ICTSTypes.SQLVARCHAR, "FCP");

			anOriginalRequest.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
					"(service.identifier=SpeiInTransferOrchestrationCore)");
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
			anOriginalRequest.setSpName("cob_procesador..sp_transfer_spei_api");

			anOriginalRequest.addInputParam("@trn_virtual", ICTSTypes.SYBINT4, "18500112");
			anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500112");			
			
			// SE EJECUTA CONECTOR
			connectorAccountResponse = executeProvider(anOriginalRequest, aBagSPJavaOrchestration);

			if (logger.isDebugEnabled())
				logger.logDebug("findCardByPanConector response: " + connectorAccountResponse);

			if (connectorAccountResponse.readValueParam("@o_status") != null)
				aBagSPJavaOrchestration.put("@o_status", connectorAccountResponse.readValueParam("@o_status"));
			else
				aBagSPJavaOrchestration.put("@o_status", "null");
			
			if (connectorAccountResponse.readValueParam("@o_id_card") != null)
				aBagSPJavaOrchestration.put("@o_id_card", connectorAccountResponse.readValueParam("@o_id_card"));
			else
				aBagSPJavaOrchestration.put("@o_id_card", "null");
			
			if (connectorAccountResponse.readValueParam("@o_response_find_card") != null)
				aBagSPJavaOrchestration.put("@o_response_find_card", connectorAccountResponse.readValueParam("@o_response_find_card"));
			else
				aBagSPJavaOrchestration.put("@o_response_find_card", "null");
			
			if (connectorAccountResponse.readValueParam("@o_request_find_card") != null)
				aBagSPJavaOrchestration.put("@o_request_find_card", connectorAccountResponse.readValueParam("@o_request_find_card"));
			else
				aBagSPJavaOrchestration.put("@o_request_find_card", "null");


		} catch (Exception e) {
			e.printStackTrace();
			aBagSPJavaOrchestration.put("@o_card_number", "null");
			connectorAccountResponse = null;
			logger.logInfo(CLASS_NAME +" Error Catastrofico de findCardByPanConector");

		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + "--> findCardByPanConector");
			}
		}
		return connectorAccountResponse;
	}
	
	private IProcedureResponse findCardId(IProcedureResponse aResponse, Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en findCardId");
		}

		request.setSpName("cob_bvirtual..sp_val_data_tarjeta");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_id_dock", ICTSTypes.SQLVARCHAR, aResponse.readValueParam("@o_id_card"));
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "C");
		
		request.addOutputParam("@o_account", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_type_card", ICTSTypes.SQLVARCHAR, "X");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("o_account es " +  wProductsQueryResp.readValueParam("@o_account"));
		}
		
		aBagSPJavaOrchestration.put("o_account", wProductsQueryResp.readValueParam("@o_account"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking findCardId DCO : " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de findCardId");
		}
		return wProductsQueryResp;
	}
	
	private IProcedureResponse findAccountByPhone(String phoneNumber) {
		
		
		String wInfo = CLASS_NAME+"[findAccountByPhone] ";		
		
		if(logger.isInfoEnabled()) {
			logger.logInfo(wInfo + INIT_TASK);
		}

		IProcedureRequest procedureRequest = new ProcedureRequestAS();		
		
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);		

		procedureRequest.setSpName("cob_bvirtual..sp_bv_account_phone");
		
		procedureRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "P");
		procedureRequest.addInputParam("@i_phone", ICTSTypes.SQLVARCHAR, phoneNumber);
		procedureRequest.addInputParam("@i_tipo", ICTSTypes.SQLVARCHAR, "SMS");
		procedureRequest.addOutputParam("@o_account", ICTSTypes.SQLVARCHAR, "XXXXX");	
		procedureRequest.addOutputParam("@o_ente", ICTSTypes.SQLINT4, "0");	
		
		IProcedureResponse ProcedureResponse =  executeCoreBanking(procedureRequest);
		
		return ProcedureResponse;
	}
	
	private IProcedureResponse validateAccountPhone(IProcedureRequest anOriginalRequest) {
		
		String phoneNumber=anOriginalRequest.readValueParam("@i_cuentaBeneficiario");
		
        if(logger.isInfoEnabled())
		{
			logger.logInfo("validateAccountPhone:"+ phoneNumber );
		}
        
        IProcedureResponse anProcedureResponse= this.findAccountByPhone(phoneNumber);   
        
        anOriginalRequest.setValueParam("@i_cuentaBeneficiario", (String) anProcedureResponse.readValueParam("@o_account"));
		
        if(logger.isInfoEnabled())
		{
			logger.logInfo("Cuenta beneficiario phone : "+ anOriginalRequest.readValueParam("@i_cuentaBeneficiario") );
		}
        
        
		return anProcedureResponse;
		
	}
	
	private IProcedureResponse validateCardAccount(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration)
	{
	    Integer code = 0;
        String message = "success";
        String idCard = null;
        String ctaBen = anOriginalRequest.readValueParam("@i_cuentaBeneficiario");
					
        if(logger.isInfoEnabled())
		{
			logger.logInfo("ctaBen:"+ctaBen );
		}
		IProcedureResponse anProcedureResponse =  new ProcedureResponseAS();

		if(logger.isDebugEnabled())
		{
			logger.logDebug("AES: "+privateKeyAes );
		}

		
		Map<String, Object> dataMapEncrypt = EncryptData.encryptWithAESGCM(ctaBen, properties.get("publicKey").toString());
		if(logger.isDebugEnabled())
		{
			logger.logDebug("[res]: + ctaDestEncrypt " + dataMapEncrypt);
			logger.logDebug("JC tmp Send pan " + ctaBen);
		}

		aBagSPJavaOrchestration.putAll(dataMapEncrypt);
	
		IProcedureResponse anProcedureResPan = findCardByPanConector(aBagSPJavaOrchestration);
		
		if(anProcedureResPan != null){
			
			idCard = anProcedureResPan.readValueParam("@o_id_card");
			if(logger.isDebugEnabled())
			{
				logger.logDebug("[res]: + idCard: " + idCard);
			}
			if (idCard == null || "Non-existent".equals(idCard) ){
				code = 50201;
				message = "Non-existent card number";
			}else
			{
				IProcedureResponse anProcedureResFind = findCardId(anProcedureResPan,aBagSPJavaOrchestration);
				
				if (anProcedureResFind.getResultSets() != null && anProcedureResFind.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
					anOriginalRequest.setValueParam("@i_cuentaBeneficiario", (String) aBagSPJavaOrchestration.get("o_account"));
					
					if(logger.isDebugEnabled())
					{
						logger.logDebug("ACCOUNT RESPONSE:: " + anOriginalRequest.readValueParam("@i_cuentaBeneficiario"));
					}
				}else{
					code = 50201;
					message = "Non-existent card number";
				}
			}
		}else{
			code = 50200;
			message = "Connection failure with provider";
		}
		
	    //result 1
		anProcedureResponse.addParam("@o_descripcion", ICTSTypes.SQLVARCHAR, 50, message);
		anProcedureResponse.addParam("@o_id_causa_devolucion", ICTSTypes.SQLVARCHAR, 50, code.toString());
		
		anProcedureResponse.setReturnCode(code);
		if(code!=0)
		{
			anProcedureResponse.addMessage(code, message);
		}
		return anProcedureResponse;
	}
	
	private String unifyDateFormat(String dateString) {
		Date date = null;
		String horMinSeg = "";
		String newDate = "";
		
        String[] formats = {
            "yyyy-MM-dd HH:mm:ssZ",
			"yyyy/MM/dd HH:mm:ssZ",
			"yyyy-MM-dd HH:mm:ss.SSSZ", 
			"yyyy/MM/dd HH:mm:ss.SSSZ",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", 
			"yyyy/MM/dd'T'HH:mm:ss.SSS'Z'", 
            "yyyy-MM-dd HH:mm:ssZ",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
			"yyyy/MM/dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss.SSSXXX",
			"yyyy/MM/dd HH:mm:ss.SSSXXX",
            "yyyy-MM-dd HH:mm:ssXXX",
			"yyyy/MM/dd HH:mm:ssXXX",
			"MM/dd/yyyy",
            "yyyyMMdd",
            "EEE MMM dd HH:mm:ss z yyyy"
        };

        if (dateString != null) {
        	try {
        		if (dateString.length() > 10) {
		        	horMinSeg = dateString.substring(10, dateString.length()).trim();
		        	if ( horMinSeg.equals("00:00:00.000")){
		        		dateString = dateString.substring(0, 10);
		        	}
        		}
        	}catch(Exception e) {
        		if(logger.isDebugEnabled()){
					logger.logDebug("Fecha: "+ dateString);
				}
        	}
        }
        
        newDate = dateString;

        for (String format : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // Establecer zona horaria si es necesario
                date = sdf.parse(dateString);
                date.setMinutes(date.getMinutes() + date.getTimezoneOffset());
                break; // Si se analiza correctamente, salir del bucle
            } catch (ParseException ignored) {
                // Ignorar y continuar con el siguiente formato
            }
        }

        SimpleDateFormat unifiedFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        if (date != null) {
        	newDate = unifiedFormat.format(date);
        }
        
        return newDate;
    }
	
	public void bindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = service;
	}

	public void unbindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = null;
	}
	
	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreServer(ICoreServer service) {
		coreServer = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreServer(ICoreServer service) {
		coreServer = null;
	}
	
	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreServiceSelfAccountTransfers(ICoreServiceSelfAccountTransfers service) {
		coreServiceSelfAccountTransfers = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreServiceSelfAccountTransfers(ICoreServiceSelfAccountTransfers service) {
		coreServiceSelfAccountTransfers = null;
	}
	
	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreService service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreService service) {
		coreService = null;
	}
	
	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = null;
	}

}
