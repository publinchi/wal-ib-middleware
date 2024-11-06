package com.cobiscorp.ecobis.orchestration.core.ib.transfer.third.party.account.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;
import java.util.Date;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.CISResponseManagmentHelper;
import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.components.ComponentLocator;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.exceptions.COBISInfrastructureRuntimeException;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
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
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.cts.reentry.api.IReentryPersister;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.admintoken.dto.DataTokenRequest;
import com.cobiscorp.ecobis.admintoken.dto.DataTokenResponse;
import com.cobiscorp.ecobis.admintoken.interfaces.IAdminTokenUser;

/**
 * Register Account
 * 
 * @since Abr 1, 2023
 * @author dcollaguazo
 * @version 1.0.0
 * 
 */
@Component(name = "TransferThirdPartyAccountApiOrchestationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "TransferThirdPartyAccountApiOrchestationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TransferThirdPartyAccountApiOrchestationCore") })
public class TransferThirdPartyAccountApiOrchestationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(TransferThirdPartyAccountApiOrchestationCore.class);
	private static final String CLASS_NAME = "TransferThirdPartyAccountApiOrchestationCore--->";
	protected static final String COBIS_CONTEXT = "COBIS";
	private java.util.Properties properties;
	
	private static final int ERROR40004 = 40004;
	private static final int ERROR40003 = 40003;
	private static final int ERROR40002 = 40002;
	
	CISResponseManagmentHelper cisResponseHelper = new CISResponseManagmentHelper();

	protected static final String CHANNEL_REQUEST = "8";

	@Reference(bind = "setTokenService", unbind = "unsetTokenService", cardinality = ReferenceCardinality.OPTIONAL_UNARY)
	private IAdminTokenUser tokenService;

	public void setTokenService(IAdminTokenUser tokenService) {
		this.tokenService = tokenService;
	}

	public void unsetTokenService(IAdminTokenUser tokenService) {
		this.tokenService = null;
	}

	/**
	 * Read configuration of parent component
	 */
	@Override
	public void loadConfiguration(IConfigurationReader configurationReader)	{
		logger.logInfo(" loadConfiguration INI TransferThirdPartyAccountApiOrchestationCore");
		properties = configurationReader.getProperties("//property");
		logger.logInfo("imp--: " + properties.toString());
	}

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
		logger.logDebug("Begin flow, TransferThirdParty [INI]: ");
		
		Boolean flowRty = evaluateExecuteReentry(anOriginalRequest);
		aBagSPJavaOrchestration.put("flowRty", flowRty);
		
		String ctaDest = anOriginalRequest.readValueParam("@i_cta_des");
		String account = null;
		aBagSPJavaOrchestration.put("tipo_cta_des", "");
		aBagSPJavaOrchestration.put("unique_id_card", "");
		int lengthCtades = ctaDest.length();
		String evaluaRiesgo = anOriginalRequest.readValueParam("@i_autoActionExecution") != null ? anOriginalRequest.readValueParam("@i_autoActionExecution").toString() : "false";
		String valorRiesgo = "";
		String codigoRiesgo = "";
		String mensajeRiesgo = "";
		String estadoRiesgo = "";
		String responseBody = "";
		
		String evaluarRiesgo = getParam(anOriginalRequest, "ACEVRI", "BVI");
		String evaluarRiesgoMobile = getParam(anOriginalRequest, "AERIMB", "BVI");
		String evaluarRiesgoSystem = getParam(anOriginalRequest, "AERISY", "BVI");
		
		String channel = anOriginalRequest.readValueParam("@i_channel") != null ? anOriginalRequest.readValueParam("@i_channel").toString() : "SYSTEM";
		
		if(logger.isDebugEnabled())
			logger.logDebug("length account: "+ lengthCtades);
		
		//inicio valida tarjeta debito por unique id 	
		if(lengthCtades == 36)
		{
			//buscamos la cuenta asociadaa la tarjeta
			IProcedureResponse anProcedureResPan =  queryCardAccount(anOriginalRequest, aBagSPJavaOrchestration);
			
			if(anProcedureResPan!=null){
				account = anProcedureResPan.readValueParam("@o_cuenta");
				if(logger.isDebugEnabled())
					logger.logDebug("account by id card: "+ account);
				
				if ("X".equals(account) || account == null ){
					anProcedureResPan.setReturnCode(50201);
					return processResponseTransfer(anOriginalRequest, anProcedureResPan, aBagSPJavaOrchestration);
				}else
				{//se asigna la cuenta de ahorros al destination
					anOriginalRequest.setValueParam("@i_cta_des", account);
					aBagSPJavaOrchestration.put("tipo_cta_des", "3");
					aBagSPJavaOrchestration.put("unique_id_card", ctaDest);
					aBagSPJavaOrchestration.put("card_id_dock",  anProcedureResPan.readValueParam("@o_card_id")); 
				}
				
			}else
			{
				anProcedureResPan = new ProcedureResponseAS();
				anProcedureResPan.setReturnCode(50201);
				return processResponseTransfer(anOriginalRequest, anProcedureResPan,aBagSPJavaOrchestration);
			}
		}
		//fin valida tarjeta por unique id
		
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		aBagSPJavaOrchestration.put("REENTRY_SSN", anOriginalRequest.readValueFieldInHeader("REENTRY_SSN_TRX"));
		
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setChannelId("8");
		ServerResponse responseServer = null;
		try {
			responseServer = getServerStatus(serverRequest);
		} catch (CTSServiceException e) {
			logger.logError(e.toString());
		} catch (CTSInfrastructureException e) {
			logger.logError(e.toString());
		}
		
		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();
		logger.logDebug("Response Online: " + responseServer.getOnLine());
		
		if (responseServer != null && !responseServer.getOnLine()) {
			aBagSPJavaOrchestration.put("IsReentry", "S");
			if (!flowRty){
				logger.logDebug("evaluateExecuteReentry FALSE");
				anProcedureResponse = saveReentry(anOriginalRequest, aBagSPJavaOrchestration);
				
				IProcedureResponse wAccountsResp = new ProcedureResponseAS();
				IProcedureResponse wAccountsRespVal = new ProcedureResponseAS();
				
				wAccountsResp = getDataAccountReq(anOriginalRequest, aBagSPJavaOrchestration);		
				logger.logInfo(CLASS_NAME + " dataLocal "+ wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue());
				if (wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")) {
					if(logger.isDebugEnabled())
					{
						logger.logInfo("BER cta:"+wAccountsResp.getResultSetRowColumnData(3, 1, 1).getValue());
					}
					anOriginalRequest.removeParam("@i_cta_des");
					anOriginalRequest.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR, wAccountsResp.getResultSetRowColumnData(3, 1, 1).getValue());
					
					wAccountsRespVal = getValAccountReq(anOriginalRequest, aBagSPJavaOrchestration);		
					logger.logInfo(CLASS_NAME + " validaCentral "+ wAccountsRespVal.getResultSetRowColumnData(2, 1, 1).getValue());
					if (!wAccountsRespVal.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
						return wAccountsRespVal;
					}
				}
				else
				{
					return wAccountsResp;
				}
				
				if ( evaluaRiesgo.equals("true") && (
					( evaluarRiesgo.equals("true") && channel.equals("DESKTOP_BROWSER")) || 
					(evaluarRiesgoMobile.equals("true") && channel.equals("MOBILE_BROWSER")) ||
					(evaluarRiesgoSystem.equals("true") && channel.equals("SYSTEM"))
					)
				) {
					IProcedureResponse wConectorRiskResponseConn = executeRiskEvaluation(anOriginalRequest, aBagSPJavaOrchestration);
				
					// Obtengo los valores de la evaluación de riesgo
					if (aBagSPJavaOrchestration.get("success_risk") != null) {				
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
						
						logger.logDebug("Respuesta RiskEvaluation: " + valorRiesgo + " Código: " + codigoRiesgo + " Estado: " + estadoRiesgo + " Mensaje: " + mensajeRiesgo );
		
						if (valorRiesgo.equals("true") && estadoRiesgo.equals("true")) {
							logger.logInfo(CLASS_NAME + "Parametro2 @ssn: " + anOriginalRequest.readValueFieldInHeader("ssn"));
							logger.logInfo(CLASS_NAME + "Parametro3 @ssn: " + anOriginalRequest.readValueParam("@s_ssn"));
							logger.logInfo("Continua flujo p2p");
							anProcedureResponse = executeOfflineThirdAccountTransferCobis(anOriginalRequest, aBagSPJavaOrchestration);
						} else {
							IProcedureResponse resp = Utils.returnException(18054, "OPERACIÓN NO PERMITIDA");
							logger.logDebug("Respose Exeption: " + resp.toString());
							return resp;
						}	
					}else {
						IProcedureResponse resp = Utils.returnException(18055, "OPERACIÓN NO PERMITIDA");
						logger.logDebug("Respose Exeption: " + resp.toString());
						return resp;
					}	
				}else {
					logger.logInfo(CLASS_NAME + "Parametro2 @ssn: " + anOriginalRequest.readValueFieldInHeader("ssn"));
					logger.logInfo(CLASS_NAME + "Parametro3 @ssn: " + anOriginalRequest.readValueParam("@s_ssn"));
					anProcedureResponse = executeOfflineThirdAccountTransferCobis(anOriginalRequest, aBagSPJavaOrchestration);
					/*
					if(anProcedureResponse.getReturnCode()==0){
						anOriginalRequest.removeParam("@o_fecha_tran");
						anProcedureResponse = saveReentry((IProcedureRequest)aBagSPJavaOrchestration.get("anOriginalRequest"), (Map<String, Object>) aBagSPJavaOrchestration.get("aBagSPJavaOrchestration"));
					}
					else
						return anProcedureResponse;
					*/
				}				
				//aBagSPJavaOrchestration.put(RESPONSE_OFFLINE, responseOffline);
			}
			else{
				logger.logDebug("evaluateExecuteReentry FALSE");
				IProcedureResponse resp = Utils.returnException(40004, "NO EJECUTA REENTRY POR ESTAR EN OFFLINE!!!");
				logger.logDebug("Respose Exeption:: " + resp.toString());
				return resp;
			}
			
			logger.logDebug("Res IsReentry:: " + "S");
		} else {
			aBagSPJavaOrchestration.put("IsReentry", "N");
			logger.logDebug("Res IsReentry:: " + "N");

			logger.logDebug("Evaluar riesgo P2P: " + evaluaRiesgo);
			if ( evaluaRiesgo.equals("true") && (
					( evaluarRiesgo.equals("true") && channel.equals("DESKTOP_BROWSER")) || 
					(evaluarRiesgoMobile.equals("true") && channel.equals("MOBILE_BROWSER")) ||
					(evaluarRiesgoSystem.equals("true") && channel.equals("SYSTEM"))
					)
			) {
				IProcedureResponse wConectorRiskResponseConn = executeRiskEvaluation(anOriginalRequest, aBagSPJavaOrchestration);
				
				if (aBagSPJavaOrchestration.get("success_risk") != null) {				
					valorRiesgo = aBagSPJavaOrchestration.get("success_risk").toString();
					
					if (aBagSPJavaOrchestration.get("responseCode") != null) {	
						codigoRiesgo = aBagSPJavaOrchestration.get("responseCode").toString();
					}
					
					if (aBagSPJavaOrchestration.get("message") != null) {	
						mensajeRiesgo = aBagSPJavaOrchestration.get("message").toString();
					}
					logger.logDebug("Antes del if responseBody");

					if(aBagSPJavaOrchestration.get("responseBody") != null) {
						logger.logDebug("Objeto de responseBody riskEvaluation:: " + aBagSPJavaOrchestration.get("responseBody").toString());
						responseBody = aBagSPJavaOrchestration.get("responseBody").toString();
						logger.logDebug("Objeto de respuesta message riskEvaluation:: " + responseBody);
						/*JsonObject messageObject = JsonParser.parseString(mensajeRiesgo).getAsJsonObject();
						logger.logDebug("Objeto de respuesta de riskEvaluation:: " + messageObject);

						if (messageObject.has("riskDetails")) {
							JsonObject riskDetails = messageObject.getAsJsonObject("riskDetails");
							if (riskDetails.has("riskStatus")) {
								String riskStatus = riskDetails.get("riskStatus").getAsString();
								logger.logDebug("Estado riskEvaluation:: " + riskStatus);
								if(riskStatus.contains("HIGH")) {
									//llamar al sp de update status

									//llamar al api blockOperation api
								}
							} else {
								logger.logError("No se encontró riskStatus en el objeto");
							}
						} else {
							logger.logError("No se encontró riskDetails en el objeto");
						}*/


					}

					if (aBagSPJavaOrchestration.get("isOperationAllowed") != null) {	
						estadoRiesgo = aBagSPJavaOrchestration.get("isOperationAllowed").toString();
					}
					
					logger.logDebug("Respuesta RiskEvaluation: " + valorRiesgo + " Código: " + codigoRiesgo + " Estado: " + estadoRiesgo + " Mensaje: " + mensajeRiesgo );
		
					if (valorRiesgo.equals("true") && estadoRiesgo.equals("true")) {
						anProcedureResponse = transferThirdAccount(anOriginalRequest, aBagSPJavaOrchestration);
					} else {
						IProcedureResponse resp = Utils.returnException(18054, "OPERACIÓN NO PERMITIDA");
						logger.logDebug("Respose Exeption:: " + resp.toString());
						return resp;
					}
				}
				else {
					IProcedureResponse resp = Utils.returnException(18055, "OPERACIÓN NO PERMITIDA");
					logger.logDebug("Respose Exeption: " + resp.toString());
					return resp;
				}
			}else {
				anProcedureResponse = transferThirdAccount(anOriginalRequest, aBagSPJavaOrchestration);
			}
		}
		
		return processResponseTransfer(anOriginalRequest, anProcedureResponse,aBagSPJavaOrchestration);
	}
	
	public boolean evaluateExecuteReentry(IProcedureRequest anOriginalRequest){		
		if (!Utils.isNull(anOriginalRequest.readValueFieldInHeader("reentryExecution"))){
			if (anOriginalRequest.readValueFieldInHeader("reentryExecution").equals("Y")){
				return true;
			}
			else
				return false;
		}
		else
			return false;
	}
	private IProcedureResponse saveReentry(IProcedureRequest wQueryRequest, Map<String, Object> aBagSPJavaOrchestration) {
		String REENTRY_FILTER = "(service.impl=ReentrySPPersisterServiceImpl)";
		IProcedureRequest request = wQueryRequest.clone();
		ComponentLocator componentLocator = null;
	    IReentryPersister reentryPersister = null;
	    componentLocator = ComponentLocator.getInstance(this);
	    
	    /*String originCode = request.readValueParam("@i_originCode");
	    logger.logDebug("@i_originCode = " + originCode);
		if (originCode == null) {
			logger.logDebug("Entre @i_originCode");
			request.addInputParam("@i_originCode",ICTSTypes.SQLINT4, "");
		}*/
	    
	    //Utils.addInputParam(request, "@i_externalCustomerId", ICTSTypes.SQLINT4,  request.readValueParam("@i_externalCustomerId"));
	    aBagSPJavaOrchestration.put("rty_ssn",request.readValueFieldInHeader("ssn"));
	    
	    reentryPersister = (IReentryPersister) componentLocator.find(IReentryPersister.class, REENTRY_FILTER);
        if (reentryPersister == null)
            throw new COBISInfrastructureRuntimeException("Service IReentryPersister was not found");
        
        request.removeFieldInHeader("sessionId");
        request.addFieldInHeader("reentryPriority", 'S', "5");
        request.addFieldInHeader("REENTRY_SSN_TRX", 'S', request.readValueFieldInHeader("ssn"));
        request.addFieldInHeader("targetId", 'S', "local");
        request.removeFieldInHeader("serviceMethodName");
        request.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', request.readValueFieldInHeader("trn"));
        request.removeParam("@t_rty");
        
        Boolean reentryResponse = reentryPersister.addTransaction(request);

        IProcedureResponse response = initProcedureResponse(request);
        if (!reentryResponse.booleanValue()) {
        	logger.logDebug("Ending flow, saveReentry failed");
            response.addFieldInHeader("executionResult", 'S', "1");
            response.addMessage(1, "Ocurrio un error al tratar de registrar la transaccion en el Reentry CORE COBIS");
        } else {
        	logger.logDebug("Ending flow, saveReentry success");
            response.addFieldInHeader("executionResult", 'S', "0");
        }

        return response;
	}
	
	public ServerResponse getServerStatus(ServerRequest serverRequest) throws CTSServiceException, CTSInfrastructureException {

		IProcedureRequest aServerStatusRequest = new ProcedureRequestAS();
		aServerStatusRequest.setSpName("cobis..sp_server_status");
		aServerStatusRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800039");
		aServerStatusRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1800039");
		aServerStatusRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, "central");
		aServerStatusRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		aServerStatusRequest.setValueParam("@s_servicio", serverRequest.getChannelId());
		aServerStatusRequest.addInputParam("@i_cis", ICTSTypes.SYBCHAR, "S");
		aServerStatusRequest.addOutputParam("@o_en_linea", ICTSTypes.SYBCHAR, "S");
		aServerStatusRequest.addOutputParam("@o_fecha_proceso", ICTSTypes.SYBVARCHAR, "XXXX");

		if (logger.isDebugEnabled())
			logger.logDebug("Request Corebanking TTPA: " + aServerStatusRequest.getProcedureRequestAsString());

		IProcedureResponse wServerStatusResp = executeCoreBanking(aServerStatusRequest);

		if (logger.isDebugEnabled())
			logger.logDebug("Response Corebanking TTPA: " + wServerStatusResp.getProcedureResponseAsString());

		ServerResponse serverResponse = new ServerResponse();
		
		serverResponse.setSuccess(true);
		Utils.transformIprocedureResponseToBaseResponse(serverResponse, wServerStatusResp);
		serverResponse.setReturnCode(wServerStatusResp.getReturnCode());

		if (wServerStatusResp.getReturnCode() == 0) {
			serverResponse.setOfflineWithBalances(true);

			if (wServerStatusResp.readValueParam("@o_en_linea") != null)
				serverResponse.setOnLine(wServerStatusResp.readValueParam("@o_en_linea").equals("S") ? true : false);

			if (wServerStatusResp.readValueParam("@o_fecha_proceso") != null) {
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				try {
					serverResponse.setProcessDate(formatter.parse(wServerStatusResp.readValueParam("@o_fecha_proceso")));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		} else if (wServerStatusResp.getReturnCode() == ERROR40002 || wServerStatusResp.getReturnCode() == ERROR40003 || wServerStatusResp.getReturnCode() == ERROR40004) {
			serverResponse.setOnLine(false);
			serverResponse.setOfflineWithBalances(wServerStatusResp.getReturnCode() == ERROR40002 ? false : true);
		}

		if (logger.isDebugEnabled())
			logger.logDebug("Respuesta Devuelta: " + serverResponse);
		if (logger.isInfoEnabled())
			logger.logInfo("TERMINANDO SERVICIO");

		return serverResponse;
	}

	public IProcedureResponse processResponseTransfer(IProcedureRequest aRequest, IProcedureResponse anOriginalProcedureRes, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(" start processResponseAccounts--->");
			logger.logInfo("xdcxv --->" + aBagSPJavaOrchestration.get("ssn") );
		}
		
		IProcedureResponse anOriginalProcedureResponse = new ProcedureResponseAS();
		String code,message,success,referenceCode, executionStatus = null;
		Integer codeReturn = anOriginalProcedureRes.getReturnCode();
		String reety = null;
		referenceCode = aBagSPJavaOrchestration.containsKey("ssn")?aBagSPJavaOrchestration.get("ssn").toString():null;
		
		//referenceCode =  aBagSPJavaOrchestration.get("ssn").toString();
		logger.logInfo("xdcxv2 --->" + referenceCode );
		logger.logInfo("xdcxv3 --->" + codeReturn );
		if(!aBagSPJavaOrchestration.containsKey("IsReentry"))
			reety = "N";
		else
			reety = (String) aBagSPJavaOrchestration.get("IsReentry");
		
		if (codeReturn == 0){
			if (null != referenceCode || reety.equals("S") ) {
				
				executionStatus = "CORRECT";
				if(aBagSPJavaOrchestration.get("flowRty").equals(false))
				updateTransferStatus(anOriginalProcedureRes, aBagSPJavaOrchestration, executionStatus);
				
				code = "0";
				message = "Success";
				success = "true";

				if(aBagSPJavaOrchestration.get("IsReentry").equals("S")){
					referenceCode = aBagSPJavaOrchestration.containsKey("rty_ssn")?aBagSPJavaOrchestration.get("rty_ssn").toString():"0";
					aBagSPJavaOrchestration.put("ssn",referenceCode);
				}
				else{
					trnRegistration(aRequest, anOriginalProcedureRes, aBagSPJavaOrchestration);
				}

				// Notificacion debito
				notifyThirdPartyTransfer(aRequest, aBagSPJavaOrchestration, "N11");
				// Notificacion credito
				notifyThirdPartyTransfer(aRequest, aBagSPJavaOrchestration, "N146");

			} else {
				
				executionStatus = "ERROR";
				
				if(aBagSPJavaOrchestration.get("flowRty").equals(false)) {
				updateLimitStatus(aBagSPJavaOrchestration);
				updateTransferStatus(anOriginalProcedureRes, aBagSPJavaOrchestration, executionStatus);
				}
					
				
				code = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).getValue();
				message = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).getValue();
				success = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue();
			}
			
		} else {
			
			executionStatus = "ERROR";
			referenceCode = null;
			
			
			if(aBagSPJavaOrchestration.get("flowRty").equals(false)) {
			updateLimitStatus(aBagSPJavaOrchestration);
			updateTransferStatus(anOriginalProcedureRes, aBagSPJavaOrchestration, executionStatus);
			}
				
			if (codeReturn == 250046)
			{
				code = String.valueOf(500010);
				message = "Destination account is blocked against deposit and withdrawal";
				success = "false";
			}
			else if (codeReturn == 252077)
			{
				code = String.valueOf(50059);
				message = "The credit to the account exceeds the maximum balance allowed";
				success = "false";
			}
			else if (codeReturn == 251002)
			{
				code = String.valueOf(500023);
				message = "The origin account or the destination number is blocked";
				success = "false";
			}
			else if (codeReturn == 251033)
			{
				code = String.valueOf(500008);
				message = "Account without funds";
				success = "false";
			}
			else if(codeReturn == 50201){
				code = String.valueOf(50201);
				message = "Non-existent card number";
				success = "false";
			}
			else if(codeReturn == 50200){
				code = String.valueOf(50200);
				message = "Connection failure with provider";
				success = "false";
			}
			else {
				code = String.valueOf(codeReturn);
				message = anOriginalProcedureRes.getMessage(1).getMessageText();
				success = "false";
			}
		}
		
		// Agregar Header y data 1
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));

		// Agregar Header y data 2
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));

		// Agregar info 1
		IResultSetRow row = new ResultSetRow();
		row.addRowData(1, new ResultSetRowColumnData(false, code));
		row.addRowData(2, new ResultSetRowColumnData(false, message));
		data.addRow(row);

		// Agregar info 2
		IResultSetRow row2 = new ResultSetRow();
		row2.addRowData(1, new ResultSetRowColumnData(false, success));
		data2.addRow(row2);

		// Agregar resulBlock
		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);

		// Agregar Header y data 3
		IResultSetHeader metaData3 = new ResultSetHeader();
		IResultSetData data3 = new ResultSetData();
		// Agregar resulBlock
		IResultSetBlock resultsetBlock3 = new ResultSetBlock(metaData3, data3);
		
		if (referenceCode != null) {
			
			if (codeReturn != 250046) { 
				metaData3.addColumnMetaData(new ResultSetHeaderColumn("referenceCode", ICTSTypes.SQLINTN, 5));

				// Agregar info 3
				IResultSetRow row3 = new ResultSetRow();
				row3.addRowData(1, new ResultSetRowColumnData(false,referenceCode));
				data3.addRow(row3);			
			}		
		}

		anOriginalProcedureResponse.setReturnCode(200);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock2);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock3);
		
		return anOriginalProcedureResponse;
	}
	
	private void trnRegistration(IProcedureRequest aRequest, IProcedureResponse aResponse, Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en transactionRegister");
		}

		request.setSpName("cob_bvirtual..sp_bv_transaction_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@s_date", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_date"));
		request.addInputParam("@s_culture", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_culture"));
		
		request.addInputParam("@i_trn", ICTSTypes.SQLINTN, "18500114");
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, (String) aBagSPJavaOrchestration.get("o_ente_bv"));
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta"));
		request.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta_des"));
		request.addInputParam("@i_beneficiary", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("o_nom_beneficiary"));
		request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("o_login"));
		request.addInputParam("@i_bank_name", ICTSTypes.SQLVARCHAR, "CASHI");
		request.addInputParam("@i_prod", ICTSTypes.SQLINTN, (String) aBagSPJavaOrchestration.get("o_prod"));
		request.addInputParam("@i_prod_des", ICTSTypes.SQLINTN, (String) aBagSPJavaOrchestration.get("o_prod_des"));
		request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_concepto"));
		request.addInputParam("@i_val", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_val"));
		request.addInputParam("@i_comision", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_comision"));
		request.addInputParam("@i_ssn_branch", ICTSTypes.SQLINTN, (String) aBagSPJavaOrchestration.get("o_ssn_branch"));
		request.addInputParam("@i_latitud", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_latitud"));
		request.addInputParam("@i_longitud", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_longitud"));
		
		logger.logDebug("Request Corebanking registerLog: " + request.toString());
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking transactionRegister: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de transactionRegister");
		}
	}
	
	private void updateTransferStatus(IProcedureResponse aResponse, Map<String, Object> aBagSPJavaOrchestration, String executionStatus) {
		
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
		request.addInputParam("@i_movementId", ICTSTypes.SQLINTN, aBagSPJavaOrchestration.containsKey("ssn")?aBagSPJavaOrchestration.get("ssn").toString():null);
		
		logger.logDebug("Request Corebanking registerLog: " + request.toString());
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking updateTransferStatus: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de updateTransferStatus");
		}
	}

	private void updateLimitStatus(Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en updateLimitStatus");
		}

		request.setSpName("cob_bvirtual..sp_update_bv_acumulado");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_seq_limite", ICTSTypes.SQLINTN, (String) aBagSPJavaOrchestration.get("o_seq_limite_out"));
		request.addInputParam("@i_seq_limite_2", ICTSTypes.SQLINTN, (String) aBagSPJavaOrchestration.get("o_seq_limite_in"));
		
		logger.logDebug("Request Corebanking registerLog: " + request.toString());
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking updateTransferStatus: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de updateTransferStatus");
		}
	}

	private IProcedureResponse transferThirdAccount(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en transferThirdAccount");
		}
			
		IProcedureResponse wAccountsResp = new ProcedureResponseAS();
		IProcedureResponse wAccountsRespVal = new ProcedureResponseAS();
		
		wAccountsResp = getDataAccountReq(aRequest, aBagSPJavaOrchestration);		
		logger.logInfo(CLASS_NAME + " dataLocal "+ wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue());
		
		if (wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")) {
			aRequest.removeParam("@i_cta_des");
			aRequest.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR, wAccountsResp.getResultSetRowColumnData(3, 1, 1).getValue());
			wAccountsRespVal = getValAccountReq(aRequest, aBagSPJavaOrchestration);		
			logger.logInfo(CLASS_NAME + " validaCentral "+ wAccountsRespVal.getResultSetRowColumnData(2, 1, 1).getValue());
		}
		else
		{
			return wAccountsResp;
		}
		
		if (wAccountsRespVal.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			
			IProcedureResponse wTransferResponse = new ProcedureResponseAS();
			
			logger.logInfo(CLASS_NAME + "Parametro2 @ssn: " + aRequest.readValueFieldInHeader("ssn"));
			logger.logInfo(CLASS_NAME + "Parametro3 @ssn: " + aRequest.readValueParam("@s_ssn"));
			logger.logInfo(CLASS_NAME + " XDCX " + aBagSPJavaOrchestration.get("o_prod") +
			aBagSPJavaOrchestration.get("o_mon") +
			aBagSPJavaOrchestration.get("o_prod_des") +
			aBagSPJavaOrchestration.get("o_mon_des") +
			aBagSPJavaOrchestration.get("o_prod_alias") +
			aBagSPJavaOrchestration.get("o_nom_beneficiary") +
			aBagSPJavaOrchestration.get("o_login") +
			aBagSPJavaOrchestration.get("o_ente_bv"));
			
			wTransferResponse = executeThirdAccountTransferCobis(aRequest, aBagSPJavaOrchestration);
			
			//wTransferResponse = executeTransfer(aRequest, aBagSPJavaOrchestration);
			return wTransferResponse; 
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Response " + wAccountsResp.toString());
			logger.logInfo(CLASS_NAME + " Saliendo de transferThirdAccount");
		}

		return wAccountsRespVal;
	}
	
	private IProcedureResponse executeRiskEvaluation(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en executeRiskEvaluation");
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
		procedureRequest.addInputParam("@i_customerDetails_externalCustomerId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_ente"));
		procedureRequest.addInputParam("@i_operation", ICTSTypes.SQLVARCHAR, "P2P_DEBIT");
		
		procedureRequest.addInputParam("@i_channelDetails_channel", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_channel").toString());//se obtiene con el response del f1
		
		procedureRequest.addInputParam("@i_channelDetails_userAgent", ICTSTypes.SQLVARCHAR, "channel:Chrome,userAgent:Windows,10,Windows,x86,rv:3.48.0");//se obtiene con el response del f1
		procedureRequest.addInputParam("@i_channelDetails_userSessionDetails_userSessionId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_userSessionId"));//se obtiene del session id de cashi web
		procedureRequest.addInputParam("@i_channelDetails_userSessionDetails_riskEvaluationId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_riskEvaluationId"));//se obtiene del metodo f5
		procedureRequest.addInputParam("@i_channelDetails_userSessionDetails_authenticationMethod", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_authenticationMethod"));
		procedureRequest.addInputParam("@i_channelDetails_userSessionDetails_location_latitude", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_latitud"));
		procedureRequest.addInputParam("@i_channelDetails_userSessionDetails_location_longitude", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_longitud"));
		procedureRequest.addInputParam("@i_channelDetails_userSessionDetails_location_accuracy", ICTSTypes.SQLINT4, aRequest.readValueParam("@i_accuracy"));
		procedureRequest.addInputParam("@i_channelDetails_userSessionDetails_location_capturedTime", ICTSTypes.SQLVARCHAR,aRequest.readValueParam("@i_capturedTime"));
		procedureRequest.addInputParam("@i_channelDetails_userSessionDetails_ipAddress", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_end_user_ip"));//signIp del response del f1
		procedureRequest.addInputParam("@i_transaction_transactionId", ICTSTypes.SQLVARCHAR, aRequest.readValueFieldInHeader("ssn"));//movement id
		String transactionDate = unifyDateFormat(aRequest.readValueParam("@x_end_user_request_date"));
		procedureRequest.addInputParam("@i_transaction_transactionDate", ICTSTypes.SQLVARCHAR, transactionDate);
		procedureRequest.addInputParam("@i_transaction_transaction_currency", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_currency"));
		procedureRequest.addInputParam("@i_transaction_transaction_amount", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_val"));
		
		
		if (aBagSPJavaOrchestration.get("card_id_dock") != null) {
			procedureRequest.addInputParam("@i_creditorAccount_identification", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("card_id_dock"));
			procedureRequest.addInputParam("@i_creditorAccount_identificationType", ICTSTypes.SQLVARCHAR, "CARD_ID");
		} else {
			procedureRequest.addInputParam("@i_creditorAccount_identification", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta_des"));
			int lengthCtades = aRequest.readValueParam("@i_cta_des").length();
			String identificationType = null;
			
			if (lengthCtades == 12) {
				identificationType = "PHONE";	
			} else if (lengthCtades == 18) {
				identificationType = "CLABE";
			} else {
				identificationType = "ACCOUNT_NUMBER";
			}

			procedureRequest.addInputParam("@i_creditorAccount_identificationType", ICTSTypes.SQLVARCHAR, identificationType);
		}

		procedureRequest.addInputParam("@i_debitorAccount_identification", ICTSTypes.SQLVARCHAR,  aRequest.readValueParam("@i_cta"));
		procedureRequest.addInputParam("@i_debitorAccount_identificationType", ICTSTypes.SQLVARCHAR,  "ACCOUNT_NUMBER");

		procedureRequest.addInputParam("@i_autoActionExecution", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_autoActionExecution") ); 

		IProcedureResponse connectorRiskEvaluationResponse = executeCoreBanking(procedureRequest);
		
		if (connectorRiskEvaluationResponse.readValueParam("@o_responseCode") != null)
			aBagSPJavaOrchestration.put("responseCode", connectorRiskEvaluationResponse.readValueParam("@o_responseCode"));

		if (connectorRiskEvaluationResponse.readValueParam("@o_message") != null)
			aBagSPJavaOrchestration.put("message", connectorRiskEvaluationResponse.readValueParam("@o_message"));

		if (connectorRiskEvaluationResponse.readValueParam("@o_responseBody") != null)
			aBagSPJavaOrchestration.put("responseBody", connectorRiskEvaluationResponse.readValueParam("@o_responseBody"));

		if (connectorRiskEvaluationResponse.readValueParam("@o_success") != null) {
			aBagSPJavaOrchestration.put("success_risk", connectorRiskEvaluationResponse.readValueParam("@o_success"));
			aBagSPJavaOrchestration.put("isOperationAllowed", connectorRiskEvaluationResponse.readValueParam("@o_isOperationAllowed"));
		}
		else {
			aBagSPJavaOrchestration.put("success_risk", "false");
			aBagSPJavaOrchestration.put("isOperationAllowed", "false");
		}

		if(logger.isDebugEnabled())
			logger.logInfo("Response executeRiskEvaluation: "+ connectorRiskEvaluationResponse.getProcedureResponseAsString());
		
		return connectorRiskEvaluationResponse;
	}

	private IProcedureResponse getDataAccountReq(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getDataAccountReq");
		}
		
		String xRequestId = aRequest.readValueParam("@x_request_id");
		String xEndUserRequestDateTime = aRequest.readValueParam("@x_end_user_request_date");
		String xEndUserIp = aRequest.readValueParam("@x_end_user_ip"); 
		String xChannel = aRequest.readValueParam("@x_channel");
		String account = aRequest.readValueParam("@i_cta");
		String destinyAccount = aRequest.readValueParam("@i_cta_des");
		String otpCode = aRequest.readValueParam("@i_otp_code");
		String otpReturnCode = null;
		String otpReturnCodeNew = null;
		String login = null;
		
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
		
		if (account.equals("null") || account.trim().isEmpty()) {
			account = "E";
		}
		
		if (destinyAccount.equals("null") || destinyAccount.trim().isEmpty()) {
			destinyAccount = "E";
		}

		if (!otpCode.equals("null") && !otpCode.trim().isEmpty()) {

			getLoginById(aRequest, aBagSPJavaOrchestration);
			
			login = aBagSPJavaOrchestration.get("o_login").toString();
			
			logger.logDebug("User login: "+login);
			
			if (!login.equals("X")) {
			
				DataTokenResponse  wResponseOtp = validateOTPCode(aRequest, aBagSPJavaOrchestration);
					
				if (logger.isDebugEnabled()) {	
				logger.logDebug("ValidateOTP response: "+wResponseOtp.getSuccess());
				}
				if(!wResponseOtp.getSuccess()) {
					
					otpReturnCode = wResponseOtp.getMessage().getCode();
					
					aBagSPJavaOrchestration.put("o_codErrorOTP", otpReturnCode);
					
					if (logger.isDebugEnabled()) {
					logger.logDebug("ValidateOTP return code: "+otpReturnCode);}
					
				} else {
					otpReturnCode = "0";
					
					if (logger.isDebugEnabled()) {
					logger.logDebug("ValidateOTP successful code: "+otpReturnCode);}
				}
			}else {
				logger.logDebug("No consulto el login");
			}
		}
					
					
		// Validamos si el error fue de otp invalido
		if (otpReturnCode != null) {
			if ( otpReturnCode.equals("1890000") ) {
				try {
					// Ejecutamos el servicio de generación de token
					DataTokenResponse  wResponseGOtp = generareOTPCode(aRequest, aBagSPJavaOrchestration);
					if (logger.isDebugEnabled()) {	
						logger.logDebug("GeneracionOTP dinámica response: "+wResponseGOtp.getSuccess());
						}
					
					if(!wResponseGOtp.getSuccess()) {				
						otpReturnCodeNew = wResponseGOtp.getMessage().getCode();
						if (logger.isDebugEnabled()) {
						logger.logDebug("GeneracionOTP dinámica no exitosa: "+ otpReturnCodeNew);}				
					} else {
						registerRequestType(login);
						if (logger.isDebugEnabled()) {
						logger.logDebug("GeneracionOTP dinámica exitosa: "+otpReturnCodeNew);}
					}					
				}catch(Exception ex) {
					aBagSPJavaOrchestration.replace("o_codErrorOTP", "1890010");
					logger.logError(ex.toString());
				}		
				//Ingresamos el log de OTP ingresadas fallidas por sistema
				registrosFallidos(aBagSPJavaOrchestration);		
			}else {
				if ( otpReturnCode.equals("1890004") || otpReturnCode.equals("1890005")) {
					//Ingresamos el log de OTP ingresadas fallidas por el usuario en bloqueo y asistencia requerida
					registrosFallidos(aBagSPJavaOrchestration);
				}
			}
			
			//Validacion para llamar al conector blockOperation
			if(otpReturnCode.equals("1890005")){
				IProcedureResponse wConectorBlockOperationResponseConn = executeBlockOperationConnector(aRequest, aBagSPJavaOrchestration);
			}
		}
		
		request.setSpName("cob_bvirtual..sp_get_data_account_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		//headers
		request.addInputParam("@x_request_id", ICTSTypes.SQLVARCHAR, xRequestId);
		request.addInputParam("@x_end_user_request_date", ICTSTypes.SQLVARCHAR, xEndUserRequestDateTime);
		request.addInputParam("@x_end_user_ip", ICTSTypes.SQLVARCHAR, xEndUserIp);
		request.addInputParam("@x_channel", ICTSTypes.SQLVARCHAR, xChannel);
		
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_ente"));
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, account);
		request.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR, destinyAccount);
		request.addInputParam("@i_val", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_val"));
		request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_concepto"));
		request.addInputParam("@i_detalle", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_detalle"));
		request.addInputParam("@i_otp_return_code", ICTSTypes.SQLVARCHAR, otpReturnCode);
		request.addInputParam("@i_comision", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_comision"));
		request.addInputParam("@i_latitud", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_latitud"));
		request.addInputParam("@i_longitud", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_longitud"));
		request.addInputParam("@i_unique_id_card", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("unique_id_card").toString());
		request.addInputParam("@i_tipo_destino", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("tipo_cta_des").toString());
		
		if(aBagSPJavaOrchestration.get("IsReentry").equals("S"))
			request.addInputParam("@i_reentry", ICTSTypes.SQLCHAR, "S");
			
		if(aBagSPJavaOrchestration.get("flowRty").equals(true))
			request.addInputParam("@i_val_uuid", ICTSTypes.SQLCHAR, "S");
		
		if(aBagSPJavaOrchestration.containsKey("valTercero"))
			request.addInputParam("@i_val_tercero", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("valTercero"));
		
		request.addOutputParam("@o_seq", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_reentry", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_prod", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_prod_des", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_mon", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_mon_des", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_ente_bv_des", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_login_des", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_ente_bv", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_login", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_prod_alias", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_nom_beneficiary", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_seq_limite_out", ICTSTypes.SQLVARCHAR, "0");
		request.addOutputParam("@o_seq_limite_in", ICTSTypes.SQLVARCHAR, "0");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("secuencial es " +  wProductsQueryResp.readValueParam("@o_seq"));
			logger.logDebug("reentry es " +  wProductsQueryResp.readValueParam("@o_reentry"));
		}
		
		aBagSPJavaOrchestration.put("o_seq", wProductsQueryResp.readValueParam("@o_seq"));
		aBagSPJavaOrchestration.put("o_reentry", wProductsQueryResp.readValueParam("@o_reentry"));
		aBagSPJavaOrchestration.put("o_prod", wProductsQueryResp.readValueParam("@o_prod"));
		aBagSPJavaOrchestration.put("o_mon", wProductsQueryResp.readValueParam("@o_mon"));
		aBagSPJavaOrchestration.put("o_prod_des", wProductsQueryResp.readValueParam("@o_prod_des"));
		aBagSPJavaOrchestration.put("o_mon_des", wProductsQueryResp.readValueParam("@o_mon_des"));
		aBagSPJavaOrchestration.put("o_prod_alias", wProductsQueryResp.readValueParam("@o_prod_alias"));
		aBagSPJavaOrchestration.put("o_nom_beneficiary", wProductsQueryResp.readValueParam("@o_nom_beneficiary"));
		aBagSPJavaOrchestration.put("o_login", wProductsQueryResp.readValueParam("@o_login"));
		aBagSPJavaOrchestration.put("o_ente_bv", wProductsQueryResp.readValueParam("@o_ente_bv"));
		aBagSPJavaOrchestration.put("o_login_des", wProductsQueryResp.readValueParam("@o_login_des"));
		aBagSPJavaOrchestration.put("o_ente_bv_des", wProductsQueryResp.readValueParam("@o_ente_bv_des"));
		aBagSPJavaOrchestration.put("o_seq_limite_out", wProductsQueryResp.readValueParam("@o_seq_limite_out"));
		aBagSPJavaOrchestration.put("o_seq_limite_in", wProductsQueryResp.readValueParam("@o_seq_limite_in"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking  DCO : " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getDataAccountReq");
		}

		return wProductsQueryResp;
	}

	private IProcedureResponse getLoginById(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {		
		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getLoginById...");		}
		
		request.setSpName("cob_bvirtual..sp_cons_ente_med_envio");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		if (aBagSPJavaOrchestration.get("card_id_dock") != null){			
			request.addInputParam("@i_card_id", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("card_id_dock"));
			
		} else {		
			request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_ente"));
		}
		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "S");
		request.addInputParam("@i_servicio", ICTSTypes.SQLINTN, "8");
		
		request.addOutputParam("@o_login", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_mail_ente", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_num_phone", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_ente", ICTSTypes.SQLVARCHAR, "X");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("login es: " +  wProductsQueryResp.readValueParam("@o_login"));
			logger.logDebug("phone es: " +  wProductsQueryResp.readValueParam("@o_num_phone"));
		}
		
		aBagSPJavaOrchestration.put("o_login", wProductsQueryResp.readValueParam("@o_login"));
		aBagSPJavaOrchestration.put("o_phone", wProductsQueryResp.readValueParam("@o_num_phone"));
		aBagSPJavaOrchestration.put("o_entebv", wProductsQueryResp.readValueParam("@o_ente"));

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking getLoginById: " + wProductsQueryResp.getProcedureResponseAsString());
		}
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getLoginById...");
		}
		
		return wProductsQueryResp;
	}
	
	private IProcedureResponse registrosFallidos(Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en registrosFallidos...");
		}
		
		request.setSpName("cob_bvirtual..sp_log_ingfallo_2FA");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "I");
		request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("o_login"));
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, (String) aBagSPJavaOrchestration.get("o_entebv"));
		request.addInputParam("@i_canal", ICTSTypes.SQLINT1, "8" );
		request.addInputParam("@i_cod_error", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_codErrorOTP").toString());
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking registrosFallidos: " + wProductsQueryResp.getProcedureResponseAsString());
		}
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de registrosFallidos...");
		}
		
		return wProductsQueryResp;
	}
	
	private DataTokenResponse validateOTPCode(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) { 

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en validateOTPCode...");
		}
		
		DataTokenRequest tokenRequest = new DataTokenRequest();
		
		tokenRequest.setLogin(aBagSPJavaOrchestration.get("o_login").toString());
		tokenRequest.setToken(aRequest.readValueParam("@i_otp_code"));
		tokenRequest.setChannel(8);
		
		DataTokenResponse tokenResponse = this.tokenService.validateTokenUser(tokenRequest);
		
		logger.logDebug("Token response: "+tokenResponse.getSuccess());
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de validateOTPCode...");
		}
		
		return tokenResponse;
	}

	private DataTokenResponse generareOTPCode (IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) { 

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en generarOTPCode...");
		}

		DataTokenRequest tokenRequest = new DataTokenRequest();
	
		tokenRequest.setLogin(aBagSPJavaOrchestration.get("o_login").toString());
		tokenRequest.setToken(aRequest.readValueParam("@i_otp_code"));
		tokenRequest.setChannel(8);
		
		DataTokenResponse tokenResponseG = this.tokenService.generateTokenUser(tokenRequest);
		
		logger.logDebug("Token response: "+tokenResponseG.getSuccess());
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de generarOTPCode...");
		}
		
		return tokenResponseG;
	}
	
	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {

		return null;
	}

	private IProcedureResponse getValAccountReq(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
	
		IProcedureRequest request = new ProcedureRequestAS();
	
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getValAccountReq");
		}
	
		request.setSpName("cobis..sp_val_data_account_api");
	
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_ente"));
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta"));
		request.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta_des"));
		request.addInputParam("@i_val", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_val"));
		request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_concepto"));
		request.addInputParam("@i_detalle", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_detalle"));
		request.addInputParam("@i_comision", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_comision"));
		request.addInputParam("@i_latitud", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_latitud"));
		request.addInputParam("@i_longitud", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_longitud"));
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking getValAccountReq DCO : " + wProductsQueryResp.getProcedureResponseAsString());
		}
	
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getValAccountReq");
		}
	
		return wProductsQueryResp;
	}

	private IProcedureResponse executeThirdAccountTransferCobis(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando transferencia a terceros CORE COBIS" + request);

		String concepto = request.readValueParam("@i_concepto");
		if(concepto == null || concepto.trim().isEmpty()){
			concepto = "Transferencia Cashi";
		}

		IProcedureRequest anOriginalRequest = request;
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		
		if(aBagSPJavaOrchestration.get("REENTRY_SSN")!=null)
			anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_SSN, (String)aBagSPJavaOrchestration.get("REENTRY_SSN"));
		
		anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18306");

		anOriginalRequest.setSpName("cob_cuentas..sp_tr03_pago_terceros"); 

		anOriginalRequest.addInputParam("@t_online", ICTSTypes.SYBCHAR, "S");
		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18306");
		anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SYBINT4, "1");
		anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		anOriginalRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0:0:0:0:0:0:0:1");
		anOriginalRequest.addInputParam("@i_causa_org", ICTSTypes.SQLVARCHAR, "1010");
		anOriginalRequest.addInputParam("@i_causa_des", ICTSTypes.SQLVARCHAR, "1020");
		//anOriginalRequest.addInputParam("@i_causa_comi", ICTSTypes.SQLVARCHAR, "185");
		if (logger.isInfoEnabled()) {
			logger.logInfo("********** CAUSALES DE TRANSFERENCIA *************");
			logger.logInfo("********** CAUSA ORIGEN --->>> " + "1010");
			logger.logInfo("********** CAUSA COMISI --->>> " + "185");
			logger.logInfo("********** CAUSA DESTIN --->>> " + "1020");

			logger.logInfo("********** CLIENTE CORE --->>> " + aBagSPJavaOrchestration.get("ente_mis"));
			//logger.logInfo("********** ORIGEN --->>> " + request.getOriginatorFunds());
			//logger.logInfo("********** DESTINO --->>> " + request.getReceiverFunds());
		}
		/*if (request.getServiceCost() != null) {
			if (logger.isInfoEnabled())
				logger.logInfo("********** SERVICIO COSTO --->>> " + request.getServiceCost());
			anOriginalRequest.addInputParam("@i_servicio_costo", ICTSTypes.SQLVARCHAR, request.getServiceCost());
		}*/

		anOriginalRequest.addInputParam("@i_servicio_costo", ICTSTypes.SQLVARCHAR, "CTRT");
		
		anOriginalRequest.addInputParam("@i_ente", ICTSTypes.SQLINT4, request.readValueParam("@i_ente"));
		
		if (request.readValueParam("@i_origen_fondos") != null)
			anOriginalRequest.addInputParam("@i_origen_fondos", ICTSTypes.SQLVARCHAR,
					request.readValueParam("@i_origen_fondos"));
		if (request.readValueParam("@i_dest_fondos") != null)
			anOriginalRequest.addInputParam("@i_dest_fondos", ICTSTypes.SQLVARCHAR,
					request.readValueParam("@i_dest_fondos"));

		anOriginalRequest.addInputParam("@i_mon", ICTSTypes.SQLINT2, aBagSPJavaOrchestration.get("o_mon").toString());
		
		anOriginalRequest.addInputParam("@i_prod_org", ICTSTypes.SQLINT2, aBagSPJavaOrchestration.get("o_prod").toString());
		
		anOriginalRequest.addInputParam("@i_cta_org", ICTSTypes.SQLVARCHAR, request.readValueParam("@i_cta"));

		anOriginalRequest.addInputParam("@i_mon_des", ICTSTypes.SQLINT2, aBagSPJavaOrchestration.get("o_mon_des").toString());
		
		anOriginalRequest.addInputParam("@i_prod_des", ICTSTypes.SQLINT2, aBagSPJavaOrchestration.get("o_prod_des").toString());
		
		anOriginalRequest.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR, request.readValueParam("@i_cta_des"));
		
		anOriginalRequest.addInputParam("@i_val", ICTSTypes.SQLMONEY, request.readValueParam("@i_val"));
		
		anOriginalRequest.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, concepto);
		
		if (request.readValueParam("@i_reference_number") != null)
			anOriginalRequest.addInputParam("@i_reference_number", ICTSTypes.SQLVARCHAR, request.readValueParam("@i_reference_number"));	
		
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Se envia Comission:" + request.readValueParam("@i_comision"));
		anOriginalRequest.addInputParam("@i_comision", ICTSTypes.SQLMONEY, request.readValueParam("@i_comision"));
		
		anOriginalRequest.addInputParam("@i_servicio", ICTSTypes.SYBINT4, "8");
		
		anOriginalRequest.addOutputParam("@o_ssn_branch", ICTSTypes.SQLINTN, "0");
		anOriginalRequest.addOutputParam("@o_fecha_tran", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXX");

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Data enviada a ejecutar api:" + anOriginalRequest.toString());
		IProcedureResponse response = executeCoreBanking(anOriginalRequest);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("ssn branch es " +  response.readValueParam("@o_ssn_branch"));
		}

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta del Core api:" + response.getProcedureResponseAsString());

		logger.logInfo(CLASS_NAME + "Parametro @o_fecha_tran: " + response.readValueParam("@o_fecha_tran"));
		response.readValueParam("@o_fecha_tran");
		
		logger.logInfo(CLASS_NAME + "Parametro @ssn: " + response.readValueFieldInHeader("ssn"));
		if(response.readValueFieldInHeader("ssn")!=null)
		aBagSPJavaOrchestration.put("ssn", response.readValueFieldInHeader("ssn"));
		aBagSPJavaOrchestration.put("o_ssn_branch", response.readValueParam("@o_ssn_branch"));
		
		return response;
	}
	
	private IProcedureResponse executeOfflineThirdAccountTransferCobis(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled()){
			logger.logInfo(CLASS_NAME + "Ejecutando transferencia Offline a terceros CORE COBIS" + anOriginalRequest);
			logger.logInfo("********** CAUSALES DE TRANSFERENCIA *************");
			logger.logInfo("********** CAUSA ORIGEN --->>> " + "1010");
			logger.logInfo("********** CAUSA COMISI --->>> " + "185");
			logger.logInfo("********** CAUSA DESTIN --->>> " + "1020");

			logger.logInfo("********** CLIENTE CORE --->>> " + aBagSPJavaOrchestration.get("ente_mis"));
		}
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		
		anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18306");

		anOriginalRequest.setSpName("cob_bvirtual..sp_bv_transaccion_off_api"); 
		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18306");
		anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SYBINT4, "1");
		anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		anOriginalRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0:0:0:0:0:0:0:1");
		anOriginalRequest.addInputParam("@i_causa_org", ICTSTypes.SQLVARCHAR, "1010");
		anOriginalRequest.addInputParam("@i_causa_des", ICTSTypes.SQLVARCHAR, "1020");
		//anOriginalRequest.addInputParam("@i_causa_comi", ICTSTypes.SQLVARCHAR, "185");
		anOriginalRequest.addInputParam("@i_servicio_costo", ICTSTypes.SQLVARCHAR, "CTRT");
		anOriginalRequest.addInputParam("@s_servicio", ICTSTypes.SYBINT4, "8");
		anOriginalRequest.addInputParam("@s_filial", ICTSTypes.SQLINT4, "1");
		anOriginalRequest.addInputParam("@s_cliente", ICTSTypes.SQLINT4, (String)aBagSPJavaOrchestration.get("o_ente_bv"));
		anOriginalRequest.addInputParam("@i_mon", ICTSTypes.SQLINT2, aBagSPJavaOrchestration.get("o_mon").toString());
		anOriginalRequest.addInputParam("@i_prod", ICTSTypes.SQLINT2, aBagSPJavaOrchestration.get("o_prod").toString());
		anOriginalRequest.addInputParam("@i_mon_des", ICTSTypes.SQLINT2, aBagSPJavaOrchestration.get("o_mon_des").toString());
		anOriginalRequest.addInputParam("@i_prod_des", ICTSTypes.SQLINT2, aBagSPJavaOrchestration.get("o_prod_des").toString());
		
		anOriginalRequest.addInputParam("@t_rty", ICTSTypes.SYBCHAR, "S");
		
		anOriginalRequest.addInputParam("@i_genera_clave", ICTSTypes.SYBCHAR, "N");
		anOriginalRequest.addInputParam("@i_tipo_notif", ICTSTypes.SYBCHAR, "F");
		anOriginalRequest.addInputParam("@i_graba_notif", ICTSTypes.SYBCHAR, "N");
		anOriginalRequest.addInputParam("@i_graba_log", ICTSTypes.SYBCHAR, "N");
		
		//-anOriginalRequest.addInputParam("@i_latitud", ICTSTypes.SQLMONEY, request.readValueParam("@i_latitud"));
		//-anOriginalRequest.addInputParam("@i_longitud", ICTSTypes.SQLMONEY, request.readValueParam("@i_longitud"));
		anOriginalRequest.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("o_login"));
		anOriginalRequest.addInputParam("@i_bank_name", ICTSTypes.SQLVARCHAR, "CASHI");
		anOriginalRequest.addInputParam("@i_beneficiary", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("o_nom_beneficiary"));
		
		
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Se envia Comission:" + anOriginalRequest.readValueParam("@i_comision"));
		anOriginalRequest.addInputParam("@i_comision", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_comision"));
		
		
		anOriginalRequest.addOutputParam("@o_fecha_tran", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXX");

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Data enviada a ejecutar api:" + anOriginalRequest);
		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta del Core api:" + response.getProcedureResponseAsString());

		logger.logInfo(CLASS_NAME + "Parametro @o_fecha_tran: " + response.readValueParam("@o_fecha_tran"));
		response.readValueParam("@o_fecha_tran");
		
		logger.logInfo(CLASS_NAME + "Parametro @ssn: " + response.readValueFieldInHeader("ssn"));
		if(response.readValueFieldInHeader("ssn")!=null)
		aBagSPJavaOrchestration.put("ssn", response.readValueFieldInHeader("ssn"));
		
		return response;
	}
	
	private void notifyThirdPartyTransfer (IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration, String notify) {

		try {

			String referenceCode = aBagSPJavaOrchestration.containsKey("ssn")?aBagSPJavaOrchestration.get("ssn").toString():null;
			
			logger.logInfo("Enviando notificacion cuentas terceros API: " + notify);

			IProcedureRequest procedureRequest = initProcedureRequest(anOriginalRequest);

			procedureRequest.setSpName("cob_bvirtual..sp_bv_enviar_notif_ib");
			procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S',"local");

			//procedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, trn);
			procedureRequest.addInputParam("@i_servicio", ICTSTypes.SQLINT1, "8");
			// procedureRequest.addInputParam("@i_num_producto", Types.VARCHAR, "");
			procedureRequest.addInputParam("@i_tipo_mensaje", ICTSTypes.SQLCHAR, "F");
			procedureRequest.addInputParam("@i_notificacion", ICTSTypes.SYBVARCHAR, notify);    		
			procedureRequest.addInputParam("@i_tipo", ICTSTypes.SQLVARCHAR, "I");
			
			procedureRequest.addInputParam("@i_canal", ICTSTypes.SQLINT1, "8");
			procedureRequest.addInputParam("@i_origen", ICTSTypes.SQLVARCHAR, "A");
			//procedureRequest.addInputParam("@i_nom_cliente_benef", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_nom_beneficiary").toString());
			procedureRequest.addInputParam("@i_s", ICTSTypes.SQLVARCHAR, referenceCode);   //anOriginalRequest.readValueParam("@i_referenciaNumerica"));
			
			procedureRequest.addInputParam("@i_v2", ICTSTypes.SQLVARCHAR, String.valueOf(anOriginalRequest.readValueParam("@i_val")));
			procedureRequest.addInputParam("@i_r", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_concepto"));
			procedureRequest.addInputParam("@i_m", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_mon").toString());
			//procedureRequest.addInputParam("@i_aux9", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_claveRastreo"));
			//procedureRequest.addInputParam("@i_aux8", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_nombreOrdenante"));
			
			if(notify.equals("N11")){
				procedureRequest.addInputParam("@i_c2", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cta"));
				procedureRequest.addInputParam("@i_c1", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cta_des"));
				procedureRequest.addInputParam("@i_ente_ib", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_ente_bv").toString());
				procedureRequest.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_login").toString());
				procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N',"1875053");
				procedureRequest.addInputParam("@i_producto", ICTSTypes.SQLINT1, "4");
			}
			else{
				procedureRequest.addInputParam("@i_c1", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cta"));
				procedureRequest.addInputParam("@i_c2", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cta_des"));
				procedureRequest.addInputParam("@i_ente_ib", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_ente_bv_des").toString());
				procedureRequest.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_login_des").toString());
				procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N',"1800195");
				procedureRequest.addInputParam("@i_producto", ICTSTypes.SQLINT1, "18");
			}
			
			IProcedureResponse procedureResponseLocal = executeCoreBanking(procedureRequest);
			
			logger.logInfo("Proceso de notificacion API terminado");
			
			if (logger.isDebugEnabled())
				logger.logInfo("Response Notification: " + procedureResponseLocal.toString());
			
		}catch(Exception xe) {

			logger.logInfo("Error en la notificacion cuentas terceros");
			logger.logError(xe);
		}
	}
	
	private IProcedureResponse queryCardAccount(IProcedureRequest anOriginalRequest,  Map<String, Object> aBagSPJavaOrchestration) 
	{
		if (logger.isDebugEnabled()) 
		{
			logger.logDebug("Begin Query card PAN");
		}
		
		IProcedureRequest procedureRequest = (initProcedureRequest(anOriginalRequest));		
		procedureRequest.setSpName("cob_bvirtual..sp_card_pan");
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID,  ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500165");
		procedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500165");
		procedureRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "Q");
		procedureRequest.addInputParam("@i_uuid", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cta_des")) ;
		
		procedureRequest.addOutputParam("@o_unique_id", ICTSTypes.SQLVARCHAR, "0");
		procedureRequest.addOutputParam("@o_card_id", ICTSTypes.SQLVARCHAR, "X");
		procedureRequest.addOutputParam("@o_cuenta", ICTSTypes.SQLVARCHAR, "X");
	    
		IProcedureResponse wProcedureResponseLocal = executeCoreBanking(procedureRequest);
		
	    if (logger.isDebugEnabled()) 
		{
			logger.logDebug("Query card PAN :" + wProcedureResponseLocal.getProcedureResponseAsString());
		}
	    return wProcedureResponseLocal;
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
		
		if (!wProcedureResponseCentral.hasError()) {
			
			if (wProcedureResponseCentral.getResultSetListSize() > 0) {
				IResultSetRow[] resultSetRows = wProcedureResponseCentral.getResultSet(1).getData().getRowsAsArray();
				
				if (resultSetRows.length > 0) {
					IResultSetRowColumnData[] columns = resultSetRows[0].getColumnsAsArray();
					return columns[2].getValue();
				} 
			} 			
		} 
		
		return "";
	}

	private IProcedureResponse executeBlockOperationConnector(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en executeBlockOperation");
		}
		String phoneNumber = null;
		Integer phoneCode = 52;
		String channel = null;

		IProcedureResponse connectorBlockOperationResponse = null;

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		aBagSPJavaOrchestration.remove("trn_virtual");

		if(logger.isDebugEnabled())
			logger.logDebug("aRequest execute blockOperation: " + aRequest);

		try {
			//Parametros de entrada
			anOriginalRequest.addFieldInHeader("com.cobiscorp.cobis.csp.services.ICSPExecutorConnector", ICOBISTS.HEADER_STRING_TYPE,
					"(service.identifier=CISConnectorBlockOperation)");
			anOriginalRequest.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "executeTransaction");
			anOriginalRequest.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, "");
			anOriginalRequest.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequest.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequest.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingOrchestrator");

			anOriginalRequest.addFieldInHeader("trn", ICOBISTS.HEADER_STRING_TYPE, "18700122");
			anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18700122");

			anOriginalRequest.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "18700122");

			anOriginalRequest.addInputParam("@i_customer_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_ente"));

			if(aRequest.readValueParam("@i_channel").toString().contains("DESKTOP_BROWSER")) {
				channel = "web";
			}
			anOriginalRequest.addInputParam("@i_channel", ICTSTypes.SQLVARCHAR, channel);

			//Construccion del body para el conector
			JsonObject jsonRequest = new JsonObject();

			//Validacion del numero de telefono
			if(aBagSPJavaOrchestration.get("o_phone") != null) {
				phoneNumber = aBagSPJavaOrchestration.get("o_phone").toString();
			}
			jsonRequest.addProperty("phoneNumber", phoneCode + phoneNumber);
			anOriginalRequest.addInputParam("@i_phone_header", ICTSTypes.SQLVARCHAR, phoneCode + phoneNumber);

			//Validacion del blockCode
			jsonRequest.addProperty("blockCode", "21");

			//Validacion de blockResason
			jsonRequest.addProperty("blockReason", "Token bloqueado por exceder limite de intentos");

			anOriginalRequest.addInputParam("@i_json_request", ICTSTypes.SQLVARCHAR, jsonRequest.toString());

			//Se llama al conector de blockOperation
			aBagSPJavaOrchestration.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorBlockOperation)");
			anOriginalRequest.setSpName("cob_procesador..sp_conne_block_operation");

			// SE EJECUTA CONECTOR
			connectorBlockOperationResponse = executeProvider(anOriginalRequest, aBagSPJavaOrchestration);

			if (connectorBlockOperationResponse.readValueParam("@o_responseCode") != null)
				aBagSPJavaOrchestration.put("responseCode", connectorBlockOperationResponse.readValueParam("@o_responseCode"));

			if (connectorBlockOperationResponse.readValueParam("@o_message") != null)
				aBagSPJavaOrchestration.put("message", connectorBlockOperationResponse.readValueParam("@o_message"));

			if (connectorBlockOperationResponse.readValueParam("@o_success") != null) {
				aBagSPJavaOrchestration.put("success_block_operation", connectorBlockOperationResponse.readValueParam("@o_success"));
			}
			else {
				aBagSPJavaOrchestration.put("success_block_operation", "false");
			}

			if(logger.isDebugEnabled())
				logger.logDebug("Response executeBlockOperationConnector: "+ connectorBlockOperationResponse.getProcedureResponseAsString());

			registerRequestBlockOperation(connectorBlockOperationResponse, jsonRequest.toString(), aRequest.readValueParam("@i_ente"));
		} catch (Exception e) {
			e.printStackTrace();
			connectorBlockOperationResponse = null;
			logger.logInfo(CLASS_NAME +" Error Catastrofico de executeBlockOperationConnector");

		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + "--> executeBlockOperationConnector");
			}
		}

		return connectorBlockOperationResponse;
	}

	private void registerRequestBlockOperation(IProcedureResponse wProcedureResponse, String requestSend, String customerId){
		IProcedureRequest request = new ProcedureRequestAS();
		final String METHOD_NAME = "[registerRequestBlockOperationError]";

		if (logger.isInfoEnabled()) {
			logger.logInfo( " Entrando en registerRequestBlockOperationError");
		}

		String bodyResponse = wProcedureResponse.readValueParam("@o_body_response");

		String success = wProcedureResponse.getResultSetRowColumnData(1, 1, 1).isNull()?"false":wProcedureResponse.getResultSetRowColumnData(1, 1, 1).getValue();

		String code = wProcedureResponse.getResultSetRowColumnData(1, 1, 2).isNull()?"":wProcedureResponse.getResultSetRowColumnData(1, 1, 2).getValue();
		String message = wProcedureResponse.getResultSetRowColumnData(1, 1, 3).isNull()?"":wProcedureResponse.getResultSetRowColumnData(1, 1, 3).getValue();

		logger.logInfo("code:: " + code);
		logger.logInfo("message:: " + message);
		logger.logInfo("bodyResponse:: " + bodyResponse);

		request.setSpName("cob_bvirtual..sp_log_ingfallo_2FA");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "B");
		request.addInputParam("@i_request_block_operation", ICTSTypes.SQLVARCHAR, requestSend);
		request.addInputParam("@i_response_block_operation", ICTSTypes.SQLVARCHAR, bodyResponse);
		request.addInputParam("@i_cod_error", ICTSTypes.SQLVARCHAR, code);
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, customerId.toString());
		request.addInputParam("@i_error_message", ICTSTypes.SQLVARCHAR, message);

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}
	}

	private void registerRequestType(String login) {
		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo( " Entrando en registerRequestType");
		}

		request.setSpName("cob_bvirtual..sp_solicitud_OTP");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, login);
		request.addInputParam("@i_tipo", ICTSTypes.SQLVARCHAR, "S");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}
	}

	private String unifyDateFormat(String dateString) {
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
			"yyyy/MM/dd HH:mm:ssXXX"
        };

        Date date = null;

        for (String format : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // Establecer zona horaria si es necesario
                date = sdf.parse(dateString);
                break; // Si se analiza correctamente, salir del bucle
            } catch (ParseException ignored) {
                // Ignorar y continuar con el siguiente formato
            }
        }

        SimpleDateFormat unifiedFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        return unifiedFormat.format(date);
    }

}
