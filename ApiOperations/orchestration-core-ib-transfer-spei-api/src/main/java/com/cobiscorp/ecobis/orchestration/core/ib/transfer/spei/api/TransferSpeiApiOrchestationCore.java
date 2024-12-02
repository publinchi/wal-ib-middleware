package com.cobiscorp.ecobis.orchestration.core.ib.transfer.spei.api;

import static com.cobiscorp.cobis.cts.domains.ICOBISTS.COBIS_HOME;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.TimeZone;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobis.trfspeiservice.bsl.dto.RegisterSpeiSpResponse;
import com.cobis.trfspeiservice.bsl.dto.SpeiMappingRequest;
import com.cobis.trfspeiservice.bsl.dto.SpeiMappingResponse;
import com.cobis.trfspeiservice.bsl.serv.ISendSpei;
import com.cobis.trfspeiservice.bsl.serv.ISpeiServiceOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.CISResponseManagmentHelper;
import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.crypt.ReadAlgn;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
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
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.cobisv.commons.exceptions.BusinessException;
import com.cobiscorp.ecobis.admintoken.dto.DataTokenRequest;
import com.cobiscorp.ecobis.admintoken.dto.DataTokenResponse;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.utils.commons.AESCrypt;
import com.cobiscorp.ecobis.ib.orchestration.base.utils.commons.JKeyStore;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Notification;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NotificationDetail;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceReexecutionComponent;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;
import com.cobiscorp.ecobis.orchestration.core.ib.transfer.spei.api.dto.AccendoConnectionData;
import com.cobiscorp.ecobis.orchestration.core.ib.transfer.spei.api.util.Methods;
import com.cobiscorp.ecobis.orchestration.core.ib.transfer.template.TransferOfflineTemplate;
import com.google.gson.JsonObject;
import com.cobiscorp.ecobis.orchestration.core.ib.api.template.OfflineApiTemplate;
import com.cobiscorp.ecobis.admintoken.interfaces.IAdminTokenUser;
import com.cobiscorp.cobis.csp.domains.ICSP;

/**
 * Register Account
 * 
 * @since Abr 17, 2023
 * @author jcos, nelson, santiago
 * @version 1.0.0
 * 
 */
@Component(name = "TransferSpeiApiOrchestationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "TransferSpeiApiOrchestationCore"),
        @Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
        @Property(name = "service.identifier", value = "TransferSpeiApiOrchestationCore") })
public class TransferSpeiApiOrchestationCore extends TransferOfflineTemplate {

    private static final String S_SSN_BRANCH = "@s_ssn_branch";
    private static final String I_NOMBRE_BENEF = "@i_nombre_benef";
    private static final String T_RTY = "@t_rty";
    private static final String T_EJEC = "@t_ejec";
    private static final String S_OFI = "@s_ofi";
    private static final String S_DATE_LOCAL = "@s_date";
    private static final String S_SRV = "@s_srv";
    private static final String S_ROL = "@s_rol";
    private static final String S_TERM = "@s_term";
    private static final String S_USER = "@s_user";
    private static final String I_MON_DES_LOCAL = "@i_mon_des";
    private static final String I_PROD_DES_LOCAL = "@i_prod_des";
    private static final String I_CTA_DES_LOCAL = "@i_cta_des";
    private static final String I_BANCO_BEN = "@i_banco_ben";
    private static final String I_DOC_BENEF = "@i_doc_benef";
    private static final String I_CONCEPTO_LOCAL = "@i_concepto";
    private static final String I_VAL_LOCAL = "@i_val";
    private static final String I_MON_LOCAL = "@i_mon";
    private static final String I_CTA_LOCAL = "@i_cta";
    private static final String S_SERVICIO_LOCAL = "@s_servicio";
    private static final String I_PROD_LOCAL = "@i_prod";
    private static final String CANCEL_OPERATION = "0";
    private static final String OPERATING_INSTITUTION = "90715";
    private boolean successConnector = false;
    private int returnCode = 0;

    private static ILogger logger = LogFactory.getLogger(TransferSpeiApiOrchestationCore.class);
    private static final String CLASS_NAME = "TransferSpeiApiOrchestationCore--->";

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

	private ISendSpei sendSpeiSERVImpl;
	public ISendSpei getSendSpeiSERVImpl() { return sendSpeiSERVImpl; }
	public void setSendSpeiSERVImpl(ISendSpei sendSpeiSERVImpl) { this.sendSpeiSERVImpl = sendSpeiSERVImpl; }
    /**
     * Instance plugin to use services other core banking
     */
    @Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
    protected ICoreServer coreServer;

    protected void bindCoreServer(ICoreServer service) {
        coreServer = service;
    }

    protected void unbindCoreServer(ICoreServer service) {
        coreServer = null;
    }

    @Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
    protected ICoreService coreService;

    public void bindCoreService(ICoreService service) {
        coreService = service;
    }

    public void unbindCoreService(ICoreService service) {
        coreService = null;
    }

    /**
     * Instance plugin to use services other core banking
     */
    @Reference(referenceInterface = ICoreServiceSendNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceNotification", unbind = "unbindCoreServiceNotification")
    public ICoreServiceSendNotification coreServiceNotification;

    public void bindCoreServiceNotification(ICoreServiceSendNotification service) {
        coreServiceNotification = service;
    }

    public void unbindCoreServiceNotification(ICoreServiceSendNotification service) {
        coreServiceNotification = null;
    }

    private static final String CORESERVICEMONETARYTRANSACTION = "coreServiceMonetaryTransaction";
    private static final String TYPE_REENTRY_OFF = "OFF_LINE";
    private static final String TYPE_REENTRY_OFF_SPI = "S";
    private static final String ERROR_SPEI = "ERROR_EN_TRANSFERENCIA_SPEI";

    @Reference(referenceInterface = ICoreServiceMonetaryTransaction.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceMonetaryTransaction", unbind = "unbindCoreServiceMonetaryTransaction")
    protected ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction;

    public void bindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
        coreServiceMonetaryTransaction = service;
    }

    public void unbindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
        coreServiceMonetaryTransaction = null;
    }

    @Reference(referenceInterface = ISpeiServiceOrchestration.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindSpeiOrchestration", unbind = "unbindSpeiOrchestration")
    protected ISpeiServiceOrchestration speiOrchestration;

    public void bindSpeiOrchestration(ISpeiServiceOrchestration service) {
        speiOrchestration = service;
    }

    public void unbindSpeiOrchestration(ISpeiServiceOrchestration service) {
        speiOrchestration = null;
    }
    
	private java.util.Properties properties;
	private AESCrypt cryptaes;
	private JKeyStore jks;
	
	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
		properties = aConfigurationReader.getProperties("//property");
		jks = new JKeyStore();
		String jkey = "";
		
		String ctsPath = System.getProperty(COBIS_HOME);
		if (properties != null && properties.get("jksalgncon") != null) {
			Map<String, String> wSecret = getAlgnCredentials(ctsPath+(String)properties.get("jksalgncon"));
			if (wSecret != null) 
			{
				properties.put("user", wSecret.get("user"));
				properties.put("pss", wSecret.get("pass"));
				
				jkey = jks.getSecretKeyStringFromKeyStore(ctsPath+(String)properties.get("jks"), wSecret.get("pass"), wSecret.get("user"));
				
				if(logger.isDebugEnabled())
				{
					logger.logDebug("jks private:"+jkey);
				}
				cryptaes = new AESCrypt(jkey);
			}
		}
		if(logger.isDebugEnabled())
		{
			logger.logDebug("pathprivateKey:"+jkey);
		}
	}
	private Map<String, String> getAlgnCredentials(String algnPath) {

		if(logger.isInfoEnabled())
			logger.logInfo("algn path: " + algnPath);
		if (algnPath == null || "".equals(algnPath)) {
			if(logger.isWarningEnabled())
				logger.logWarning("No secret param in configuration file. Default secret will be used");
			return null;
		}

		String wAlgnPath = algnPath;
		if (!new File(wAlgnPath).exists()) {
			if(logger.isErrorEnabled())
		 		logger.logError("The algn file specified does not exist. Default secret will be used:" + wAlgnPath);
			return null;
		}

		ReadAlgn algn = new ReadAlgn(wAlgnPath);
		if(logger.isInfoEnabled())
			logger.logInfo("Reading algn properties...");
		java.util.Properties propertiesAlgn = algn.leerParametros();
		Map<String, String> secret = new HashMap<String, String>();
		secret.put("user", propertiesAlgn.getProperty("l"));
		secret.put("pass", propertiesAlgn.getProperty("p"));
		secret.put("serv",propertiesAlgn.getProperty("s"));
		return secret;
	}

    /**
     * /** Execute transfer first step of service
     * <p>
     * This method is the main executor of transactional contains the original input
     * parameters.
     * 
     * @param anOriginalRequest       - Information original sended by user's.
     * @param aBagSPJavaOrchestration - Object dictionary transactional steps.
     * 
     * @return
     *         <ul>
     *         <li>IProcedureResponse - Represents the service execution.</li>
     *         </ul>
     */
    @Override
    public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
            Map<String, Object> aBagSPJavaOrchestration) {

        aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);

        IProcedureResponse anProcedureResponse = new ProcedureResponseAS();
        
      //  String decrypt = cryptaes.decryptData("aqui var tarjeta encriptada");
        
        anProcedureResponse = validateCardAccount(anOriginalRequest, aBagSPJavaOrchestration);
        if(anProcedureResponse.getReturnCode()==0)
        {
        	anProcedureResponse = transferSpei(anOriginalRequest, aBagSPJavaOrchestration);
        }
        return processResponseTransfer(anOriginalRequest, anProcedureResponse, aBagSPJavaOrchestration);

    }

    private IProcedureResponse transferSpei(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

        if (logger.isInfoEnabled()) {
            logger.logInfo(CLASS_NAME + " Entrando en transferSpei");
        }
        //String destAccoutNumber = request.readValueParam("@i_destination_account_number");
        
		String evaluarRiesgo = getParam(aRequest, "ACEVRI", "BVI");
        String evaluarRiesgoMobile = getParam(aRequest, "AERIMB", "BVI");
        String evaluarRiesgoSystem = getParam(aRequest, "AERISY", "BVI");
        String valorRiesgo = "";
		String codigoRiesgo = "";
		String mensajeRiesgo = "";
		Boolean estadoRiesgo = false;
		String evaluaRiesgo = aRequest.readValueParam("@i_autoActionExecution") != null ? aRequest.readValueParam("@i_autoActionExecution").toString() : "false";

        String channel = aRequest.readValueParam("@i_channel").toString() != null ? aRequest.readValueParam("@i_channel").toString() : "SYSTEM";

        IProcedureResponse wAccountsResp = new ProcedureResponseAS();
        
        try {
            callGetLimits(aRequest, aBagSPJavaOrchestration);
            if(aBagSPJavaOrchestration.get("successGetLimits").equals("true")){
                obtainLimits(aRequest, aBagSPJavaOrchestration);

                if (aBagSPJavaOrchestration.containsKey("isDailyLimitExceeded") && 
                    (Boolean)aBagSPJavaOrchestration.get("isDailyLimitExceeded")) {
                    IProcedureResponse resp = Utils.returnException(18056, "Importe máximo diario excedido");
                    logger.logDebug("Respose Exeption: " + resp.toString());
                    return resp;
                } 
                
                if (aBagSPJavaOrchestration.containsKey("isMaxTxnLimitExceeded") && 
                    (Boolean)aBagSPJavaOrchestration.get("isMaxTxnLimitExceeded")) {
                    IProcedureResponse resp = Utils.returnException(18057, "Importe máximo por operación excedido");
                    logger.logDebug("Respose Exeption: " + resp.toString());
                    return resp;
                } 
            }   
        } catch (Exception e) {
			e.printStackTrace();
			logger.logInfo(CLASS_NAME +" Error Catastrofico en validacion Limites");
			logger.logError(e);
		}  

        wAccountsResp = getDataTransfSpeiReq(aRequest, aBagSPJavaOrchestration);
        logger.logInfo(CLASS_NAME + " zczc " + wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue());

        if (wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")) {

            IProcedureResponse wTransferResponse = new ProcedureResponseAS();
            logger.logInfo(CLASS_NAME + " JCOS " + aBagSPJavaOrchestration.get("o_prod")
                    + aBagSPJavaOrchestration.get("o_mon") + aBagSPJavaOrchestration.get("o_prod_des")
                    + aBagSPJavaOrchestration.get("o_mon_des") + aBagSPJavaOrchestration.get("o_prod_alias")
                    + aBagSPJavaOrchestration.get("o_nom_beneficiary") + aBagSPJavaOrchestration.get("o_login")
                    + aBagSPJavaOrchestration.get("o_ente_bv"));


            if ( evaluaRiesgo.equals("true") && (
                        ( evaluarRiesgo.equals("true") && channel.equals("DESKTOP_BROWSER")) || 
                        (evaluarRiesgoMobile.equals("true") && channel.equals("MOBILE_BROWSER")) ||
                        (evaluarRiesgoSystem.equals("true") && channel.equals("SYSTEM"))
                    )
            ) {
            	//agregar el llamado al orquestador de evaluationrisk
                IProcedureResponse wConectorRiskResponseConn = executeRiskEvaluation(aRequest, aBagSPJavaOrchestration);
                
                // Obtengo los valores de la evaluación de riesgo
    			if (aBagSPJavaOrchestration.get("success_risk") != null) {
    				valorRiesgo = aBagSPJavaOrchestration.get("success_risk").toString();
    				
    				if (aBagSPJavaOrchestration.get("responseCode") != null) {	
    					codigoRiesgo = aBagSPJavaOrchestration.get("responseCode").toString();
    				}
    				
    				if (aBagSPJavaOrchestration.get("responseCode") != null) {	
    					mensajeRiesgo = aBagSPJavaOrchestration.get("message").toString();
    				}
    				
    				logger.logDebug("Respuesta RiskEvaluation: " + valorRiesgo + " Código: " + codigoRiesgo + " Mensaje: " + mensajeRiesgo );
    				
    				if (aBagSPJavaOrchestration.get("isOperationAllowed") != null) {	
    					estadoRiesgo = Boolean.parseBoolean((String) aBagSPJavaOrchestration.get("isOperationAllowed"));
    				}
    				
    				logger.logDebug("Respuesta RiskEvaluation: " + valorRiesgo + " Código: " + codigoRiesgo + " Estado: " + estadoRiesgo + " Mensaje: " + mensajeRiesgo );

    				if (valorRiesgo.equals("true") && estadoRiesgo) {
    					logger.logInfo(CLASS_NAME + "Parametro2 @ssn: " + aRequest.readValueFieldInHeader("ssn"));
    					logger.logInfo(CLASS_NAME + "Parametro3 @ssn: " + aRequest.readValueParam("@s_ssn"));
    					logger.logInfo("Continua flujo spei-out");
    					wTransferResponse = executeTransferApi(aRequest, aBagSPJavaOrchestration);
    				} else {
    					IProcedureResponse resp = Utils.returnException(18054, "OPERACION NO PERMITIDA");
    					logger.logDebug("Response Exeption: " + resp.toString());
    					return resp;
    				}
                } else {
    				IProcedureResponse resp = Utils.returnException(18055, "ERROR AL EJECUTAR LA EVALUACIÓN DE RIESGO");
    				logger.logDebug("Response Exeption: " + resp.toString());
    				return resp;
    			}
                
                return wTransferResponse;
            } else {
            	wTransferResponse = executeTransferApi(aRequest, aBagSPJavaOrchestration);
            	return wTransferResponse;
            }
        }
        
        return wAccountsResp;
    }

    private IProcedureResponse getDataTransfSpeiReq(IProcedureRequest aRequest,
            Map<String, Object> aBagSPJavaOrchestration) {

        IProcedureRequest request = new ProcedureRequestAS();

        if (logger.isInfoEnabled()) {
            logger.logInfo(CLASS_NAME + " Entrando en getDataTransfSpeiReq");
        }
        
        String xRequestId = aRequest.readValueParam("@x_request_id");
        String xEndUserRequestDateTime = aRequest.readValueParam("@x_end_user_request_date");
        String xEndUserIp = aRequest.readValueParam("@x_end_user_ip"); 
        String xChannel = aRequest.readValueParam("@x_channel");
        String account = aRequest.readValueParam("@i_origin_account_number");
        String destinyAccount = aRequest.readValueParam("@i_destination_account_number");
        String bankId = aRequest.readValueParam("@i_bank_id");
        String bankName = aRequest.readValueParam("@i_bank_name");
        String destinyOwnerName = aRequest.readValueParam("@i_destination_account_owner_name");
        String referenceNumber = aRequest.readValueParam("@i_reference_number");
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
        
        if (bankId.equals("null") || bankId.trim().isEmpty()) {
            bankId = "E";
        }
        
        if (bankName.equals("null") || bankName.trim().isEmpty()) {
            bankName = "E";
        }
        
        if (destinyOwnerName.equals("null") || destinyOwnerName.trim().isEmpty()) {
            destinyOwnerName = "E";
        }
        
        if (referenceNumber.equals("null") || referenceNumber.trim().isEmpty()) {
            referenceNumber = "E";
        } else if (!referenceNumber.trim().matches("\\d{1,7}")) {
            referenceNumber = "L";
        }
        
        if (otpCode!=null && !otpCode.equals("null") && !otpCode.trim().isEmpty()) {

			getLoginById(aRequest, aBagSPJavaOrchestration);
			
			login = aBagSPJavaOrchestration.get("o_login").toString();
			
			logger.logDebug("User login: "+login);
			
			if (!login.equals("X")) {
			
				DataTokenResponse  wResponseOtp = validateOTPCode(aRequest, aBagSPJavaOrchestration);
					
				logger.logDebug("ValidateOTP response: "+wResponseOtp.getSuccess());
				
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
        
        request.setSpName("cob_bvirtual..sp_get_data_transf_spei_api");

        request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
                IMultiBackEndResolverService.TARGET_LOCAL);
        request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
        
        //headers
        request.addInputParam("@x_request_id", ICTSTypes.SQLVARCHAR, xRequestId);
        request.addInputParam("@x_end_user_request_date", ICTSTypes.SQLVARCHAR, xEndUserRequestDateTime);
        request.addInputParam("@x_end_user_ip", ICTSTypes.SQLVARCHAR, xEndUserIp);
        request.addInputParam("@x_channel", ICTSTypes.SQLVARCHAR, xChannel);
        
        request.addInputParam("@i_external_customer_id", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_external_customer_id"));
        
        request.addInputParam("@i_origin_account_number", ICTSTypes.SQLVARCHAR, account);
        request.addInputParam("@i_destination_account_number", ICTSTypes.SQLVARCHAR, destinyAccount);
        request.addInputParam("@i_amount", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_amount"));
        request.addInputParam("@i_bank_id", ICTSTypes.SQLVARCHAR, bankId);
        request.addInputParam("@i_bank_name", ICTSTypes.SQLVARCHAR, bankName);
        request.addInputParam("@i_destination_account_owner_name", ICTSTypes.SQLVARCHAR, destinyOwnerName);
        request.addInputParam("@i_destination_type_account", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_destination_type_account"));
        
        request.addInputParam("@i_owner_name", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_owner_name"));
        request.addInputParam("@i_detail", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_detail"));
        request.addInputParam("@i_otp_return_code", ICTSTypes.SQLVARCHAR, otpReturnCode);
        request.addInputParam("@i_commission", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_commission"));
        request.addInputParam("@i_latitude", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_latitude"));
        request.addInputParam("@i_longitude", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_longitude"));
        request.addInputParam("@i_reference_number", ICTSTypes.SQLVARCHAR, referenceNumber);
         
        request.addOutputParam("@o_seq", ICTSTypes.SQLINT4, "0");
        request.addOutputParam("@o_reentry", ICTSTypes.SQLVARCHAR, "X");
        request.addOutputParam("@o_prod", ICTSTypes.SQLINT4, "0");
        request.addOutputParam("@o_prod_des", ICTSTypes.SQLINT4, "0");
        request.addOutputParam("@o_mon", ICTSTypes.SQLINT4, "0");
        request.addOutputParam("@o_mon_des", ICTSTypes.SQLINT4, "0");
        request.addOutputParam("@o_ente_bv", ICTSTypes.SQLINT4, "0");
        request.addOutputParam("@o_login", ICTSTypes.SQLVARCHAR, "X");
        request.addOutputParam("@o_prod_alias", ICTSTypes.SQLVARCHAR, "X");
        request.addOutputParam("@o_nom_beneficiary", ICTSTypes.SQLVARCHAR, "X");

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

        if (logger.isDebugEnabled()) {
            logger.logDebug("Response Corebanking getDataTransfSpeiReq DCO : "
                    + wProductsQueryResp.getProcedureResponseAsString());
        }

        if (logger.isInfoEnabled()) {
            logger.logInfo(CLASS_NAME + " Saliendo de getDataTransfSpeiReq");
        }

        return wProductsQueryResp;
    }
    
    private IProcedureResponse getLoginById(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		
    	IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getLoginById..."); 
			}
		
		request.setSpName("cob_bvirtual..sp_cons_ente_med_envio");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		if (aBagSPJavaOrchestration.get("card_id_dock") != null){			
			request.addInputParam("@i_card_id", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("card_id_dock"));
			
		} else {		
			request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_external_customer_id"));
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

    private IProcedureResponse executeTransferApi(IProcedureRequest aRequest,
            Map<String, Object> aBagSPJavaOrchestration) {

        IProcedureRequest request = initProcedureRequest(aRequest);
        final String METHOD_NAME = "[executeTransferSpei]";

        if (logger.isInfoEnabled()) {
            logger.logInfo(CLASS_NAME + " Entrando en executeTransfer");
        }

        /*
         * curp = (String) aBagSPJavaOrchestration.get("o_curp"); beneficiary = (String)
         * aBagSPJavaOrchestration.get("o_beneficiary"); product = (String)
         * aBagSPJavaOrchestration.get("o_producto");
         */

        request.addInputParam("@s_cliente", ICTSTypes.SQLINT4,
                aBagSPJavaOrchestration.get("o_ente_bv").toString().trim());
        request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_external_customer_id"));
        request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR,
                aBagSPJavaOrchestration.get("o_login").toString().trim());

        request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_origin_account_number"));
        request.addInputParam("@i_mon", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_mon").toString().trim());
        request.addInputParam("@i_prod", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_prod").toString().trim());

        request.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR,
                aRequest.readValueParam("@i_destination_account_number"));
        request.addInputParam("@i_mon_des", ICTSTypes.SQLINT4,
                aBagSPJavaOrchestration.get("o_mon_des").toString().trim());
        request.addInputParam("@i_prod_des", ICTSTypes.SQLINT4,
                aBagSPJavaOrchestration.get("o_prod_des").toString().trim());
        request.addInputParam("@i_nombre_benef", ICTSTypes.SQLVARCHAR,
                aBagSPJavaOrchestration.get("o_nom_beneficiary").toString().trim());
        // request.addInputParam("@i_nom_cliente", ICTSTypes.SQLINT4,
        // aBagSPJavaOrchestration.get("o_prod_alias").toString().trim());

        request.addInputParam("@i_val", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_amount"));
        // request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR,
        // aRequest.readValueParam("@i_concept"));
        String detail = "WAL_VAL_DEF_X";
        if (!aRequest.readValueParam("@i_detail").equals("null") && !aRequest.readValueParam("@i_detail").trim().isEmpty()) {
            detail = aRequest.readValueParam("@i_detail");
        }
        request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, detail);// poner en el CWC
        request.addInputParam("@i_banco_ben", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_bank_id"));
        // request.addInputParam("@i_nom_banco_des", ICTSTypes.SQLVARCHAR,
        // aRequest.readValueParam("@i_bank_name"));
        request.addInputParam("@i_nombre_cta_dest", ICTSTypes.SQLVARCHAR,
                aRequest.readValueParam("@i_destination_account_owner_name"));
        request.addInputParam("@i_detail", ICTSTypes.SQLVARCHAR, detail);
        request.addInputParam("@i_comision", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_commission"));
        request.addInputParam("@i_latitud", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_latitude"));
        request.addInputParam("@i_longitud", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_longitude"));
        request.addInputParam("@i_reference_number", ICTSTypes.SQLINTN,
                aRequest.readValueParam("@i_reference_number"));
        
        
        request.addInputParam("@s_ssn", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_ssn"));
        request.addInputParam("@s_ssn_branch", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_ssn_branch"));
        request.addInputParam("@s_sesn", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_sesn"));
        request.addInputParam("@t_ssn_corr", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@t_ssn_corr"));
        request.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_srv"));
        request.addInputParam("@s_lsrv", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_lsrv"));
        request.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_user"));
        request.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_term"));
        request.addInputParam("@s_date", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_date"));
        request.addInputParam("@s_ofi", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_ofi"));
        request.addInputParam("@s_rol", ICTSTypes.SQLVARCHAR,  aRequest.readValueParam("@s_rol"));
        request.addInputParam("@s_sev", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_sev"));
        request.addInputParam("@s_org", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("U"));
        request.addInputParam("@t_filial", ICTSTypes.SQLVARCHAR, "1");
        request.addInputParam("@t_corr", ICTSTypes.SQLVARCHAR, "N");
        request.addInputParam("@t_ejec", ICTSTypes.SQLVARCHAR, "R");
        request.addInputParam("@t_debug", ICTSTypes.SQLVARCHAR, "S");
        request.addInputParam("@t_rty", ICTSTypes.SQLVARCHAR, "N");
        request.addInputParam("@t_trn", ICTSTypes.SQLVARCHAR, "1870013");
        request.addInputParam("@s_servicio", ICTSTypes.SQLVARCHAR, "8");
        
        // 18500115
        logger.logInfo(METHOD_NAME + " Datos Cabecera");
        // Date fecha = new Date();
        request.setSpName("cob_procesador..sp_tr04_transferencia_ob");
        // request.addFieldInHeader(ICOBISTS.HEADER_PROCESS_DATE,
        // ICOBISTS.HEADER_DATE_TYPE, forma.format("01/05/2023"));
        request.addFieldInHeader(ICOBISTS.HEADER_MESSAGE_TYPE, ICOBISTS.HEADER_STRING_TYPE, "ProcedureRequest");
        request.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
                "(service.identifier=SPITransferOrchestrationCore)");
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
        request.addFieldInHeader("sesn", ICOBISTS.HEADER_NUMBER_TYPE, aRequest.readValueParam("@s_sesn"));
        request.addFieldInHeader("authorizationService", ICOBISTS.HEADER_STRING_TYPE, "COBISBV");
        request.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
        request.addFieldInHeader("supportOffline", ICOBISTS.HEADER_CHARACTER_TYPE, "N");
        request.addFieldInHeader("term", ICOBISTS.HEADER_STRING_TYPE, aRequest.readValueParam("@s_term"));
        request.addFieldInHeader("serviceId", ICOBISTS.HEADER_STRING_TYPE,
                "InternetBanking.WebApp.Transfers.Service.Transfer.TransferSPI");
        request.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingOrchestrator");
        request.addFieldInHeader("filial", ICOBISTS.HEADER_NUMBER_TYPE, "1");
        request.addFieldInHeader("servicio", ICOBISTS.HEADER_NUMBER_TYPE, "8");
        request.addFieldInHeader("org", ICOBISTS.HEADER_STRING_TYPE, "U");
        request.addFieldInHeader("contextId", ICOBISTS.HEADER_STRING_TYPE, "COBISBV");
        // request.addFieldInHeader("sessionId", ICOBISTS.HEADER_STRING_TYPE,
        // "S");
        request.addFieldInHeader("serviceName", ICOBISTS.HEADER_STRING_TYPE,
                "cob_procesador..sp_tr04_transferencia_ob");
        request.addFieldInHeader("perfil", ICOBISTS.HEADER_NUMBER_TYPE, "13");
        // request.addFieldInHeader("ssn_branch", ICOBISTS.HEADER_NUMBER_TYPE,
        // request.readValueParam("@ssn_branch"));
        request.addFieldInHeader("target", ICOBISTS.HEADER_STRING_TYPE, "SPExecutor");

        request.addFieldInHeader("trn", ICOBISTS.HEADER_NUMBER_TYPE, "1870013");
        request.addFieldInHeader("ofi", ICOBISTS.HEADER_NUMBER_TYPE, "1");
        // request.addFieldInHeader("serviceExecutionId",
        // ICOBISTS.HEADER_STRING_TYPE, request.readValueParam("@ssn_branch"));
        request.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
        request.addFieldInHeader("srv", ICOBISTS.HEADER_STRING_TYPE, aRequest.readValueParam("@s_srv"));
        request.addFieldInHeader("culture", ICOBISTS.HEADER_STRING_TYPE, "ES-EC");
        request.addFieldInHeader("spType", ICOBISTS.HEADER_STRING_TYPE, "Sybase");
        request.addFieldInHeader("lsrv", ICOBISTS.HEADER_STRING_TYPE, aRequest.readValueParam("@s_lsrv"));
        request.addFieldInHeader("user", ICOBISTS.HEADER_STRING_TYPE, aRequest.readValueParam("@s_user"));

        request.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "1870013");
        request.addFieldInHeader("trn_origen", ICOBISTS.HEADER_STRING_TYPE, "API_IN");

        logger.logInfo(request);

        //IProcedureResponse responseTransferSpei = executeCoreBanking(request);
        
        Map<String, Object> mapInterfaces = new HashMap<String, Object>();
        mapInterfaces.put("coreServer", coreServer);
        mapInterfaces.put("coreService", coreService);
        mapInterfaces.put("coreServiceNotification", coreServiceNotification);
        Utils.validateComponentInstance(mapInterfaces);
        aBagSPJavaOrchestration.put(TRANSFER_NAME, "TRANFERENCIA SPI");
        aBagSPJavaOrchestration.put(CORESERVICEMONETARYTRANSACTION, coreServiceMonetaryTransaction);
        aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, request);
        
        
        
        if (logger.isInfoEnabled()) {
            logger.logInfo(" start executeBanpay--->");
            logger.logInfo("xdcxv --->" + aBagSPJavaOrchestration);
        }
        
        try {
            executeStepsTransactionsBase(request, aBagSPJavaOrchestration);
        } catch (CTSServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CTSInfrastructureException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    
        
        return processResponse(request, aBagSPJavaOrchestration);
        
        //IProcedureResponse responseTransferSpei = executeTransfer(aBagSPJavaOrchestration);

        /*if (logger.isDebugEnabled()) {
            logger.logDebug("Response Corebanking DCO API: " + responseTransferSpei.getProcedureResponseAsString());
        }

        if (logger.isInfoEnabled()) {
            logger.logInfo(CLASS_NAME + " Saliendo de executeTransfer");
        }

        return responseTransferSpei;*/
    }

    public IProcedureResponse processResponseTransfer(IProcedureRequest aRequest, IProcedureResponse anOriginalProcedureRes, Map<String, Object> aBagSPJavaOrchestration) {
        
        if (logger.isInfoEnabled()) {
            logger.logInfo(" start processResponseAccounts--->");
            logger.logInfo("xdcxv --->" + anOriginalProcedureRes.readValueParam("@o_referencia"));
        }

        IProcedureRequest anOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get("anOriginalRequest");
        IProcedureResponse anOriginalProcedureResponse = new ProcedureResponseAS();
        String code = null, message, success, referenceCode = null, trackingKey = null, movementId = null, 
        executionStatus = null;
        Integer codeReturn = anOriginalProcedureRes.getReturnCode();
        
        if (logger.isInfoEnabled()) {
            logger.logInfo("Mensaje NJ_912006");
            logger.logInfo(anOriginalProcedureRes.getProcedureResponseAsString());
            logger.logInfo(anOriginalProcedureRes.toString());
            logger.logInfo("The code return is: " + codeReturn.toString());
            logger.logInfo("successConnector is: " + successConnector);
            logger.logInfo("movementId: " + anOriginalProcedureRes.readValueParam("@o_referencia"));
            logger.logInfo("ssn_branch: " + anOriginalRequest.readValueParam("@s_ssn_branch"));
            logger.logInfo("referenceCode: " + (String) aBagSPJavaOrchestration.get(Constants.I_CODIGO_ACC));
            logger.logInfo("trackingKey: " + (String) aBagSPJavaOrchestration.get(Constants.I_CLAVE_RASTREO));           
            
        }
       
        
        movementId = anOriginalProcedureRes.readValueParam("@o_referencia");
        if (codeReturn == 50000) {
			movementId = aRequest.readValueParam("@s_ssn");
			aBagSPJavaOrchestration.put("ssn_branch_offline", movementId);
		}
        
        

		logger.logInfo("xdcxv2 --->" + movementId);
		if (codeReturn == 0 || codeReturn == 50000 || codeReturn == 1) {
			if (null != movementId) {
				IProcedureResponse responseDataSpei = getDataSpei(movementId, aBagSPJavaOrchestration);
				
				if (this.successConnector) {
					
					executionStatus = "CORRECT";
					updateTransferStatus(anOriginalProcedureRes, aBagSPJavaOrchestration, executionStatus);
					trnRegistration(aRequest, anOriginalProcedureRes, aBagSPJavaOrchestration);
					
					code = "0";
					message = "Success";
					success = "true";
					referenceCode = (String) aBagSPJavaOrchestration.get(Constants.I_CODIGO_ACC);
					trackingKey = (String) aBagSPJavaOrchestration.get(Constants.I_CLAVE_RASTREO);
					 
					logger.logInfo("Llamo al metodo registrar SPEI_OUT");
				        registerAllTransactionSuccess("SPEI_OUT", anOriginalRequest,"2040",
				        		anOriginalRequest.readValueParam("@s_ssn_branch"));

					
					
					logger.logInfo("bnbn true--->" + movementId);
					
				} else {
					
					executionStatus = "ERROR";
					updateTransferStatus(anOriginalProcedureRes, aBagSPJavaOrchestration, executionStatus);
					
					if (codeReturn == 1) {
						code = "500024";
						message = "Error connecting with SPEI provider";
						success = "false";
					} else {
						code = (String) aBagSPJavaOrchestration.get("@o_id_error");
						message = (String) aBagSPJavaOrchestration.get("@o_mensaje_error");
						success = "false";
					}
					
					movementId = null;
					
					logger.logInfo("bnbn false--->" + movementId);
				}
				
			} else {
				
				executionStatus = "ERROR";
				updateTransferStatus(anOriginalProcedureRes, aBagSPJavaOrchestration, executionStatus);
				
				code = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).getValue();
				message = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).getValue();
				success = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue();
				
				logger.logInfo("bnbn false--->" + movementId);
			}

        } else {
            
            logger.logInfo("bnbn false--->" + this.returnCode);
            executionStatus = "ERROR";
            updateTransferStatus(anOriginalProcedureRes, aBagSPJavaOrchestration, executionStatus);
            
            if (this.returnCode == 1875285 || this.returnCode == 2600069) {
                code = "400178";
                message = "The amount to be transferred exceeds the current account balance";       
                success = "false";
            } else if (this.returnCode == 400177) {
                code = "400177";
                message = "The source account has a debit block";       
                success = "false";
            }
            else {
                logger.logInfo("bnbn false2--->" + this.returnCode);
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
        
        // Agregar Header y data 4
        IResultSetHeader metaData4 = new ResultSetHeader();
        IResultSetData data4 = new ResultSetData();
        // Agregar resulBlock
        IResultSetBlock resultsetBlock4 = new ResultSetBlock(metaData4, data4);

        if (movementId != null) {

            metaData3.addColumnMetaData(new ResultSetHeaderColumn("movementId", ICTSTypes.SQLINTN, 5));

            // Agregar info 3
            IResultSetRow row3 = new ResultSetRow();
            row3.addRowData(1, new ResultSetRowColumnData(false,
                    movementId));
            data3.addRow(row3);
        }
        
        if (trackingKey != null) {

            metaData4.addColumnMetaData(new ResultSetHeaderColumn("trackingKey", ICTSTypes.SQLINTN, 5));
            metaData4.addColumnMetaData(new ResultSetHeaderColumn("referenceCode", ICTSTypes.SQLINTN, 5));

            // Agregar info 4
            IResultSetRow row4 = new ResultSetRow();
            row4.addRowData(1, new ResultSetRowColumnData(false,
                    trackingKey));      
            row4.addRowData(2, new ResultSetRowColumnData(false,
                    referenceCode));
            
            data4.addRow(row4);
        }

        anOriginalProcedureResponse.setReturnCode(200);
        anOriginalProcedureResponse.addResponseBlock(resultsetBlock2);
        anOriginalProcedureResponse.addResponseBlock(resultsetBlock);
        anOriginalProcedureResponse.addResponseBlock(resultsetBlock3);
        anOriginalProcedureResponse.addResponseBlock(resultsetBlock4);

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
        
        request.addInputParam("@i_trn", ICTSTypes.SQLINTN, "18500115");
        request.addInputParam("@i_ente", ICTSTypes.SQLINTN, (String) aBagSPJavaOrchestration.get("o_ente_bv"));
        request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_origin_account_number"));
        request.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_destination_account_number"));
        request.addInputParam("@i_beneficiary", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_destination_account_owner_name"));
        request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("o_login"));
        request.addInputParam("@i_bank_name", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_bank_name"));
        request.addInputParam("@i_prod", ICTSTypes.SQLINTN, (String) aBagSPJavaOrchestration.get("o_prod"));
        request.addInputParam("@i_prod_des", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_destination_type_account"));
        request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_detail"));
        request.addInputParam("@i_val", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_amount"));
        request.addInputParam("@i_comision", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_commission"));
        request.addInputParam("@i_ssn_branch", ICTSTypes.SQLINTN, (String) aBagSPJavaOrchestration.get("@i_ssn_branch"));
        request.addInputParam("@i_reference_number", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_reference_number"));
        request.addInputParam("@i_latitud", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_latitude"));
        request.addInputParam("@i_longitud", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_longitude"));
        
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
		if  (aResponse.readValueParam("@o_referencia") != null) {
		request.addInputParam("@i_movementId", ICTSTypes.SQLINTN, Integer.parseInt(aResponse.readValueParam("@o_referencia")) == 0  ? (String) aBagSPJavaOrchestration.get("ssn_branch_offline") : (String) aResponse.readValueParam("@o_referencia"));
		} else {
			request.addInputParam("@i_movementId", ICTSTypes.SQLINTN, aResponse.readValueParam("@o_referencia"));
		}
		
		
		logger.logDebug("Request Corebanking registerLog: " + request.toString());
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking updateTransferStatus: " + wProductsQueryResp.getProcedureResponseAsString());
		}

        if (logger.isInfoEnabled()) {
            logger.logInfo(CLASS_NAME + " Saliendo de updateTransferStatus");
        }
    }
    
    private IProcedureResponse getDataSpei(String referenceCode, Map<String, Object> aBagSPJavaOrchestration) {

        IProcedureRequest request = new ProcedureRequestAS();

        if (logger.isInfoEnabled()) {
            logger.logInfo(CLASS_NAME + " Entrando en getDataSpei");
        }

        request.setSpName("cob_bvirtual..sp_get_data_spei_api");

        request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
                IMultiBackEndResolverService.TARGET_LOCAL);
        request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

        request.addInputParam("@i_reference_code", ICTSTypes.SQLVARCHAR,
                referenceCode);

        request.addOutputParam("@o_clave_ratreo", ICTSTypes.SQLVARCHAR, "X");
        request.addOutputParam("@o_mensaje_error", ICTSTypes.SQLVARCHAR, "X");
        request.addOutputParam("@o_id_error", ICTSTypes.SQLINT4, "0");

        IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

        aBagSPJavaOrchestration.put("@o_clave_ratreo", wProductsQueryResp.readValueParam("@o_clave_ratreo"));
        aBagSPJavaOrchestration.put("@o_mensaje_error", wProductsQueryResp.readValueParam("@o_mensaje_error"));
        aBagSPJavaOrchestration.put("@o_id_error", wProductsQueryResp.readValueParam("@o_id_error"));


        if (logger.isDebugEnabled()) {
            logger.logDebug("Response Corebanking getDataSpei DCO : "
                    + wProductsQueryResp.getProcedureResponseAsString());
        }

        if (logger.isInfoEnabled()) {
            logger.logInfo(CLASS_NAME + " Saliendo de getDataSpei");
        }

        return wProductsQueryResp;
    }

    @Override
    protected IProcedureResponse executeTransfer(Map<String, Object> aBagSPJavaOrchestration) {
        if (logger.isDebugEnabled()) {
            logger.logDebug("Inicia executeTransfer API");
        }
        IProcedureResponse responseTransfer = null;
        String idTransaccion = "";
        String idMovement = "";
        String refBranch = "";
        try {
            IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
            
            if (originalRequest != null) {
                logger.logDebug("Inicia originalRequest no es null" +originalRequest.toString());
                
                 if (originalRequest.readValueParam(S_USER) != null) {
                     logger.logDebug(S_USER + " no es null");
                 }
                 
                 if (originalRequest.readValueParam(S_TERM) != null) {
                     logger.logDebug(S_TERM + " no es null");
                 }
                 
                if (originalRequest.readValueParam(S_ROL) != null) {
                    logger.logDebug(S_ROL + " no es null");              
                }
                
                if (originalRequest.readValueParam(S_SRV) != null) {
                    logger.logDebug(S_SRV + " no es null");
                }
                
                if (originalRequest.readValueParam(S_DATE_LOCAL) != null) {
                    logger.logDebug(S_DATE_LOCAL + " no es null");
                }
                
                if (originalRequest.readValueParam(S_OFI) != null) {
                    logger.logDebug(S_OFI + " no es null");
                }
                
                if (originalRequest.readValueParam(S_SRV) != null) {
                    logger.logDebug(S_SRV + " no es null");
                }
                
                if (originalRequest.readValueParam(T_EJEC) != null) {
                    logger.logDebug(T_EJEC + " no es null");
                }
                
                if (originalRequest.readValueParam(T_RTY) != null) {
                    logger.logDebug(T_RTY + " no es null");
                }
            }
            
            ServerResponse serverResponse = (ServerResponse) aBagSPJavaOrchestration.get(RESPONSE_SERVER);
            IProcedureRequest originalRequestClone = originalRequest.clone();
            // SE EJECUTA LA NOTA DE DEBITO CENTRAL

            logger.logDebug("Aplicando Transacción " + idTransaccion +"--" +aBagSPJavaOrchestration.toString());

            if (aBagSPJavaOrchestration.containsKey("origin_spei")
                    && aBagSPJavaOrchestration.get("origin_spei") != null) {
                logger.logDebug("On Origin Spei ");
                String appliedOrigin = aBagSPJavaOrchestration.get("origin_spei").toString();
                logger.logDebug("On Origin Spei do " + appliedOrigin);
                if (appliedOrigin.equals("MASSIVE")) {
                    logger.logDebug("On massive function");
                    idTransaccion = "040";
                }
            }

            responseTransfer = this.executeTransferSPI(originalRequestClone, aBagSPJavaOrchestration);

            if (!(idTransaccion != null && idTransaccion.equals("040"))) {
                logger.logDebug("Normal transacction");
                
                if (responseTransfer.readValueParam("@o_referencia") != null
                        && responseTransfer.readValueParam("@o_referencia") != null)
                    idMovement = responseTransfer.readValueParam("@o_referencia");

                if (responseTransfer.readValueParam("@o_ref_branch") != null
                        && responseTransfer.readValueParam("@o_ref_branch") != null) {
                    refBranch = responseTransfer.readValueParam("@o_ref_branch");
                }

                if (logger.isDebugEnabled()) {
                    logger.logDebug("ref_branch" + refBranch);
                    logger.logDebug("idMovement:: " + idMovement);
                    logger.logDebug("idTransaccion:: " + idTransaccion);
                }
                
                if (refBranch.equals("0")) {
                    this.returnCode = responseTransfer.getReturnCode(); 
                }

                responseTransfer = transformToProcedureResponse(responseTransfer, aBagSPJavaOrchestration,
                        idTransaccion);
                
            } else {
                logger.logDebug("On massive transacction");
                idMovement = aBagSPJavaOrchestration.get("ssn_operation").toString();
            }

            originalRequestClone.addInputParam("@i_ssn_branch", ICTSTypes.SQLINT4, refBranch);
            aBagSPJavaOrchestration.put("@i_ssn_branch", refBranch);

            // JCOS VALIDACION PARA FL
            if (serverResponse.getOnLine()) {

                IProcedureResponse tran = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
                idTransaccion = idMovement;

                if (logger.isDebugEnabled()) {
                    logger.logDebug(":::: Referencia XDX " + idTransaccion);
                }

                if (logger.isDebugEnabled()) {
                	logger.logDebug(":::: Se aplicara transaccion reetry o on line SPEI " + originalRequestClone.toString());
                }
                logger.logInfo(":::: Contiene parametro " + originalRequestClone.readValueParam("@i_type_reentry"));

                if ((originalRequestClone.readValueParam("@i_type_reentry") == null  
                        || !originalRequestClone.readValueParam("@i_type_reentry").equals(TYPE_REENTRY_OFF))) {// VALIDACION
                                                                                                                // REENTRY

                    if (idTransaccion != null && !"".equals(idTransaccion)) {
                        if (logger.isDebugEnabled()) {
                            logger.logDebug(":::: Ahorros OK Transfer Banpay " + idTransaccion);
                        }

                        aBagSPJavaOrchestration.put("APPLY_DATE", originalRequestClone.readValueParam("@o_fecha_tran"));

                        int transacctionApplied = Integer.parseInt(idTransaccion.trim());
                        if (transacctionApplied > 0) {

                            originalRequestClone.addInputParam("@i_transaccion_spei", ICTSTypes.SQLVARCHAR,
                                    String.valueOf(transacctionApplied));
                            aBagSPJavaOrchestration.put("@i_transaccion_spei", String.valueOf(transacctionApplied));

                            if (logger.isDebugEnabled()) {
                                logger.logDebug("Spei Armed");
                            }
                            SpeiMappingRequest requestSpei = mappingBagToSpeiRequest(aBagSPJavaOrchestration,
                                    responseTransfer, originalRequestClone);

                            if (logger.isDebugEnabled()) {
                                logger.logDebug("Spei do it");
                            }
                            
                            String typeConnector = getParam(originalRequest, "COSPEI", "AHO");
                            
                            try {

	                            if (typeConnector != null && typeConnector.equals("KARPAY")) {
	                            	responseTransfer = executeBanpay(aBagSPJavaOrchestration, responseTransfer, originalRequest);
	                            } else if (typeConnector != null && typeConnector.equals("STP")) {                           
	                            	SpeiMappingResponse responseSpei = speiOrchestration.sendSpei(requestSpei);
	                                responseTransfer = mappingResponseSpeiToProcedure(responseSpei, responseTransfer, aBagSPJavaOrchestration);                            	
	                            } 
                            } catch (Exception e) {
                            	if (logger.isDebugEnabled()) {
									logger.logDebug("Fin executeTransfer 3");
								}
								
                            	reverseSpei(requestSpei, aBagSPJavaOrchestration);  															
								
								responseTransfer.setReturnCode(1);
                            }

                        } else
                            logger.logDebug(":::: No Aplica Transaccion no valida " + idTransaccion);
                    } else {

                        if (logger.isDebugEnabled()) {
                            logger.logDebug(":::: No Aplica Transaccion Cancel jcos " + idTransaccion);
                        }
                    }
                }

            } else if (originalRequestClone.readValueParam("@i_type_reentry") == null && !serverResponse.getOnLine()) {

                if (logger.isDebugEnabled()) {
                    logger.logDebug("Se envia a reentry por fuera de linea JCOS");
                }
                // si el saldo disponible le alcanza se aplica transaccion con el proveedor JCOS
                // TODO
                IProcedureResponse validationData = (IProcedureResponse) aBagSPJavaOrchestration
                        .get(RESPONSE_LOCAL_VALIDATION);

                if (validationData != null) {

                    if (validationData != null && originalRequestClone.readValueParam(T_RTY).equals("N")
                            && validationData.readValueParam("@o_aplica_tran").equals("S")) {

                        if (logger.isDebugEnabled()) {
                            logger.logDebug(":::: Se aplicara servicio spei por que tiene saldo en local");
                        }

                        idTransaccion = idMovement;
                        SpeiMappingRequest requestSpei = mappingBagToSpeiRequest(aBagSPJavaOrchestration,
                                responseTransfer, originalRequestClone);
                        
                        try {
                        	String typeConnector = getParam(originalRequest, "COSPEI", "AHO");
                            if (typeConnector != null && typeConnector.equals("KARPAY")) {
                            	
                            	SpeiMappingResponse responseTransferOff = sendSpeiOfflineBanpay(requestSpei, responseTransfer, aBagSPJavaOrchestration, originalRequest);
                            	responseTransfer = mappingResponseSpeiToProcedureOffline(responseTransferOff, responseTransfer, aBagSPJavaOrchestration);
                            	
                            } else if (typeConnector != null && typeConnector.equals("STP")) {  
                                 
                                 SpeiMappingResponse responseSpei = speiOrchestration.sendSpeiOffline(requestSpei);
                                 mappingResponseSpeiToProcedureOffline(responseSpei, responseTransfer, aBagSPJavaOrchestration);
                                                             	
                            } 
                        } catch (Exception e) {
                        	if (logger.isDebugEnabled()) {
								logger.logDebug("Fin executeTransfer 3");
							}
							
                        	reverseSpei(requestSpei, aBagSPJavaOrchestration);  															
							
							responseTransfer.setReturnCode(1);
                        }
                        logger.logDebug("Se registro Movimiento OffLine");
                        responseTransfer.addParam("@i_register_off_mov",ICTSTypes.SQLVARCHAR,1,"S");
                        responseTransfer.addParam("@i_type_reentry", ICTSTypes.SQLVARCHAR, 1, TYPE_REENTRY_OFF_SPI);
                    }
                } else {

                    if (logger.isDebugEnabled()) {
                        logger.logDebug("DATA VALIDATE IS NULL!!!");
                    }
                }

            }

        } catch (CTSServiceException e) {

            logger.logError(e);
        } catch (CTSInfrastructureException e) {
            logger.logError(e);
        } finally {
            if (logger.isDebugEnabled()) {
                logger.logDebug("Fin executeTransfer");
            }

        }

        if (idTransaccion != null && idTransaccion != "") {

            if (logger.isDebugEnabled()) {
                logger.logDebug("Almacenadox !!! " + idTransaccion);
            }
            responseTransfer.addParam("@o_idTransaccion", ICTSTypes.SQLVARCHAR, idTransaccion.length(), idTransaccion);
        }

        logger.logDebug("Responde devuelto a TransferOfflineTemplate " + responseTransfer);
		return responseTransfer;
	}

    public SpeiMappingResponse sendSpeiOfflineBanpay(SpeiMappingRequest request, IProcedureResponse responseTransfer,
			Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
		String wInfo = "[TransferSpeiApiOrchestationCore][sendSpeiOfflineBanpay] ";
		logger.logInfo(wInfo + "INIT_ORCHESTRATION");

		SpeiMappingResponse mappingResponse = new SpeiMappingResponse();
		try {
			//ConnectionData connectionData = sendSpeiSERVImpl.getConnectionData();
			//mappingResponse = sendSpeiSERVImpl.getAdditionalData(request);
			//mappingResponse.setClaveRastreo(sendSpeiSERVImpl.generateTrackingId(request, connectionData));

			//SpeiTransactionRequest transactionRequest = sendSpeiSERVImpl.createTransactionRequest(request, mappingResponse, connectionData);
			//SpeiTransactionResponse transactionResponse = sendSpeiSERVImpl.sendSpeiInternal(request,transactionRequest, connectionData, mappingResponse);
			
			aBagSPJavaOrchestration.put("@o_referencia", responseTransfer.readValueParam("@o_referencia"));
			logger.logInfo("VALOR @o_referencia: " + responseTransfer.readValueParam("@o_referencia"));
	        // SE LLAMA LA SERVICIO DE BANPAY REVERSA DE REVERSA
	        List<String> respuesta = banpayExecution(anOriginalRequest, aBagSPJavaOrchestration);
	        
	        logger.logInfo("Ver data bag:::  "+ (String) aBagSPJavaOrchestration.get("clave_rastreo"));
	        mappingResponse.setClaveRastreo((String) aBagSPJavaOrchestration.get("clave_rastreo"));
	        mappingResponse.setNombreOrdenante((String) aBagSPJavaOrchestration.get("nombre_ordenante"));
	        mappingResponse.setRfcCurpOrdenante((String) aBagSPJavaOrchestration.get("curp_ordenante"));
	        mappingResponse.setTipoCuentaOrdenante((String) aBagSPJavaOrchestration.get("tipo_cuenta_ordenante"));
	        mappingResponse.setCuentaClabe((String) aBagSPJavaOrchestration.get("cuenta_clab"));
	        mappingResponse.setInstitucionOrdenante((String) aBagSPJavaOrchestration.get("institucion_ordenante"));
            
	        if (respuesta != null)
	        {
	            if (!respuesta.get(0).equals("00"))
	            {
	            	mappingResponse.setErrorCode(Integer.parseInt(respuesta.get(0)));
					mappingResponse.setErrorMessage(respuesta.get(1));
					mappingResponse.setMensajeAcc(respuesta.get(1));
	                persistDataLocalOnFailureSpei(anOriginalRequest, aBagSPJavaOrchestration);

	                if (logger.isDebugEnabled())
	                {
	                    logger.logDebug("Error SPEI");
	                }
	                
	                this.successConnector = false;
	                //return Utils.returnException(1, ERROR_SPEI);
	            } else
	            {
	                if (logger.isDebugEnabled())
	                {
	                    logger.logDebug("Paso exitoso");
	                }
	                // SE ADJUNTA LA CLAVE DE RASTREO
	                responseTransfer.addParam("@o_clave_rastreo", ICTSTypes.SQLVARCHAR, respuesta.get(2).length(),
	                        respuesta.get(2));
	                
	                this.successConnector = true;
	                aBagSPJavaOrchestration.put(Constants.I_CLAVE_RASTREO, respuesta.get(2));

	                /*
	                 * String wPrcessingSpeiMessage = "PENDIENTE";
	                 * responseTransfer.addParam("@o_clave_rastreo", ICTSTypes.SQLVARCHAR,
	                 * wPrcessingSpeiMessage.length(),wPrcessingSpeiMessage);
	                 */

	            }
	        } else
	        {
	            if (logger.isDebugEnabled())
	            {
	                logger.logDebug("List<String> respuesta error o null");
	            }
	            mappingResponse.setErrorCode(Integer.parseInt(respuesta.get(0)));
				mappingResponse.setErrorMessage(respuesta.get(1));
				mappingResponse.setMensajeAcc(respuesta.get(1));
	            persistDataLocalOnFailureSpei(anOriginalRequest, aBagSPJavaOrchestration);
	            
	            this.successConnector = false;

	            //return Utils.returnException(1, ERROR_SPEI);
	        }
	        
	        /*
			if(transactionResponse.getResponse() != null && transactionResponse.getResponse().getId() != null){ //Si existe error en procesamiento
				mappingResponse.setErrorCode(Integer.parseInt(transactionResponse.getResponse().getId()));
				mappingResponse.setErrorMessage(transactionResponse.getResponse().getDescripcion());
				mappingResponse.setMensajeAcc(transactionResponse.getResponse().getDescripcion());
				sendSpeiSERVImpl.persistDataLocalOnFailureSpei(request, mappingResponse);
			}else{
				int responseFromProvider = transactionResponse.getResultado().getId();
				String responseMessage = transactionResponse.getResultado().getDescripcionError();
				mappingResponse.setCodigoAcc(String.valueOf(responseFromProvider));

				if(responseFromProvider > 999){ //si respuesta tiene mas de 3 digitos
					mappingResponse.setMensajeAcc("Transferencia exitosa");
				}else{
					mappingResponse.setMensajeAcc( responseMessage != null ? responseMessage : "Error en transferencia");
					sendSpeiSERVImpl.persistDataLocalOnFailureSpei(request, mappingResponse);
					sendSpeiSERVImpl.setErrorResponse(mappingResponse, responseFromProvider, "Error en transferencia");
				}

			}*/

		}catch (BusinessException be){
			logger.logError(wInfo+"ERROR ",be);
			sendSpeiSERVImpl.persistDataLocalOnFailureSpei(request, mappingResponse);
			mappingResponse.setErrorCode(be.getClientErrorCode());
			mappingResponse.setErrorMessage(be.getClientErrorMessage());
		}catch (Exception ex){
			logger.logError(wInfo+"ERROR ",ex);
			sendSpeiSERVImpl.persistDataLocalOnFailureSpei(request, mappingResponse);
			mappingResponse.setErrorCode(1);
			mappingResponse.setErrorMessage(null != ex.getCause() ? ex.getCause().getMessage() : "ERROR EN TRANSFERENCIA SPEI");
		}

		logger.logInfo(wInfo + "End Orchestration---->");
		return mappingResponse;
	}
    
	private void reverseSpei(SpeiMappingRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		RegisterSpeiSpResponse requestSp = new RegisterSpeiSpResponse();
        requestSp.setMonto(String.valueOf(request.getMonto()));
        requestSp.setMoneda("0");
        requestSp.setProcesoOrigen(Constants.ORIGIN_PROCESS_SINGLE_SPEI);
        requestSp.setTransactionCore(request.getSsnDebito());
        requestSp.setServicio(request.getServicio());
        requestSp.setTipoError(Constants.SPEI_ERROR_TYPE);
        requestSp.setCuentaOrigen(request.getCuentaOrdenante());

        requestSp.setTrnOrigen(request.getTrnOrigen());
        requestSp.setUser(request.getUser());
        requestSp.setOffice(request.getOffice());
        requestSp.setServer(request.getServer());
        requestSp.setTerminal(request.getTerminal());
        requestSp.setComision(originalRequest.readValueParam("@i_comision"));
        requestSp.setConcepto(Constants.ERROR_SPEI);
        IProcedureRequest wProcedureRequest = new ProcedureRequestAS();
        SpReverseSpeiSent.initializeSpReverseSpei(wProcedureRequest, requestSp);
        
        IProcedureResponse wProductsQueryResp = executeCoreBanking(wProcedureRequest);
        
        if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking reverseSpei: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de reverseSpei");
		}
	}

    public IProcedureResponse executeTransferSPI(IProcedureRequest anOriginalRequest,
            Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
        if (logger.isDebugEnabled()) {
            logger.logDebug("Inicia executeTransferSPI");
        }
        IProcedureResponse response = new ProcedureResponseAS();
        IProcedureResponse responseBank = executeCoreBanking(this.getRequestBank(anOriginalRequest));
        if (logger.isDebugEnabled()) {
            logger.logDebug("Response Bank --> " + responseBank.getProcedureResponseAsString());
        }

        response.setReturnCode(responseBank.getReturnCode());
        if (responseBank.getReturnCode() != 0) {
            response = Utils.returnException(Utils.returnArrayMessage(responseBank));

        }

        if (responseBank.getReturnCode() == 0 && responseBank.getResultSetListSize() > 0) {

            IResultSetRow[] rows = responseBank.getResultSet(responseBank.getResultSetListSize()).getData()
                    .getRowsAsArray();
            IResultSetRowColumnData[] columns = rows[0].getColumnsAsArray();

            IProcedureResponse responseLocalValidation = (IProcedureResponse) aBagSPJavaOrchestration
                    .get(RESPONSE_LOCAL_VALIDATION);
            IProcedureRequest requestTransfer = this.getRequestTransfer(anOriginalRequest, responseLocalValidation);

            requestTransfer.addInputParam("@i_nom_banco_des", ICTSTypes.SYBVARCHAR, columns[0].getValue());
            aBagSPJavaOrchestration.put("@i_banco_dest", columns[0].getValue());
            requestTransfer.addInputParam("@i_ruta_trans", ICTSTypes.SYBVARCHAR, columns[2].getValue());
            requestTransfer.addOutputParam("@o_fecha_tran", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXX");
            if (aBagSPJavaOrchestration.containsKey("origin_spei") && aBagSPJavaOrchestration.get("origin_spei") != null
                    && aBagSPJavaOrchestration.get("origin_spei").equals("MASSIVE")) {

                logger.logDebug("go to exit Spei Transaction");

                return response;
            }
            response = executeCoreBanking(requestTransfer);
            if (logger.isDebugEnabled()) {
                logger.logDebug("Request accountTransfer: " + anOriginalRequest.getProcedureRequestAsString());
                logger.logDebug("aBagSPJavaOrchestration SPEI: " + aBagSPJavaOrchestration.toString());
            }
        }

        if (logger.isDebugEnabled()) {
            logger.logDebug("Response accountTransfer:" + response.getProcedureResponseAsString());
            logger.logDebug("Fin executeTransferSPI "+ response.getReturnCode());
        }        	
        
        return response;
    }
    
    
    
    /**
     * Permite obtener el request para obtener los datos del banco
     *
     * @param anOriginalRequest
     * @return
     */
    private IProcedureRequest getRequestBank(IProcedureRequest anOriginalRequest) {
        IProcedureRequest requestBank = new ProcedureRequestAS();

        requestBank.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1870013");
        requestBank.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
                IMultiBackEndResolverService.TARGET_LOCAL);
        requestBank.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
        requestBank.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");

        requestBank.setSpName("cob_bvirtual..sp_mant_ifis");
        requestBank.addInputParam("@t_online", ICTSTypes.SQLCHAR, "S");
        requestBank.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1870009");

        requestBank.addInputParam("@i_cod_ban", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_banco_ben"));
        requestBank.addInputParam("@i_grupo", ICTSTypes.SQLINT4, "1");
        requestBank.addInputParam("@i_tip_tran", ICTSTypes.SQLVARCHAR, "S");
        requestBank.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "S");

        return requestBank;
    }
    
    /**
     * Método que permite crear un request para ser enviado al Corebanking
     *
     * @param anOriginalRequest       Request original
     * @param lastResponse            Último response recibido.
     * @param aBagSPJavaOrchestration Objetos que son resultado de la ejecución de
     *                                los métodos.
     */
    private IProcedureRequest getRequestTransfer(IProcedureRequest anOriginalRequest,
            IProcedureResponse responseLocalValidation) {
        if (logger.isInfoEnabled()) {
            logger.logInfo("Inicia transfer SPI");
        }

        IProcedureRequest requestTransfer = new ProcedureRequestAS();

        requestTransfer.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1870013");
        requestTransfer.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
                IMultiBackEndResolverService.TARGET_CENTRAL);
        requestTransfer.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
        requestTransfer.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");

        requestTransfer.setSpName("cob_ahorros..sp_tr04_transferencia_ob");
        requestTransfer.addInputParam("@t_online", ICTSTypes.SQLCHAR, "S");
        requestTransfer.addInputParam("@t_trn", ICTSTypes.SYBINTN, "18340");

        requestTransfer.addInputParam(S_USER, anOriginalRequest.readParam(S_USER).getDataType(),
                anOriginalRequest.readValueParam(S_USER));
        requestTransfer.addInputParam(S_TERM, anOriginalRequest.readParam(S_TERM).getDataType(),
                anOriginalRequest.readValueParam(S_TERM));
        requestTransfer.addInputParam(S_ROL, anOriginalRequest.readParam(S_ROL).getDataType(),
                anOriginalRequest.readValueParam(S_ROL));
        requestTransfer.addInputParam(S_SRV, anOriginalRequest.readParam(S_SRV).getDataType(),
                anOriginalRequest.readValueParam(S_SRV));
        requestTransfer.addInputParam(S_DATE_LOCAL, anOriginalRequest.readParam(S_DATE_LOCAL).getDataType(),
                anOriginalRequest.readValueParam(S_DATE_LOCAL));
        requestTransfer.addInputParam(S_OFI, anOriginalRequest.readParam(S_OFI).getDataType(),
                anOriginalRequest.readValueParam(S_OFI));
        requestTransfer.addInputParam(S_SRV, anOriginalRequest.readParam(S_SRV).getDataType(),
                anOriginalRequest.readValueParam(S_SRV));
        requestTransfer.addInputParam(T_EJEC, anOriginalRequest.readParam(T_EJEC).getDataType(),
                anOriginalRequest.readValueParam(T_EJEC));
        requestTransfer.addInputParam(T_RTY, anOriginalRequest.readParam(T_RTY).getDataType(),
                anOriginalRequest.readValueParam(T_RTY));

        anOriginalRequest.addOutputParam("@o_fecha_tran", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXX");

        if (logger.isInfoEnabled())
            logger.logInfo("PRE COMISION --->   RECUPERADA");

        if (anOriginalRequest != null && anOriginalRequest.readValueParam("@i_comision") != null) {

            logger.logInfo("ENTRA VALIDACION COMISION");

            if (logger.isInfoEnabled())
                logger.logInfo(
                        "Llegada de comisiom 3.1416 SPEIDO ---> " + anOriginalRequest.readValueParam("@i_comision"));

            requestTransfer.addInputParam("@i_comision", ICTSTypes.SYBMONEY,
                    anOriginalRequest.readValueParam("@i_comision"));
        } else {
            logger.logInfo("NO ENTRA VALIDACION COMISION > 0");
            requestTransfer.addInputParam("@i_comision", ICTSTypes.SYBMONEY, "0");
        }

        // jcos recuperacion de SSN TRANSACCIONAL
        requestTransfer.addOutputParam("@o_referencia", ICTSTypes.SYBINT4, "0");
        requestTransfer.addOutputParam("@o_ref_branch", ICTSTypes.SYBINT4, "0");

        if ("1".equals(anOriginalRequest.readValueParam(S_SERVICIO_LOCAL))
                || "8".equals(anOriginalRequest.readValueParam(S_SERVICIO_LOCAL))
                || "10".equals(anOriginalRequest.readValueParam(S_SERVICIO_LOCAL))) {

            logger.logInfo("ENTRA VALIDACION TIPO SERVICIO 1,8,10");
            // CUENTA ORIGEN
            requestTransfer.addInputParam(I_CTA_LOCAL, anOriginalRequest.readParam(I_CTA_LOCAL).getDataType(),
                    anOriginalRequest.readValueParam(I_CTA_LOCAL));
            requestTransfer.addInputParam(I_PROD_LOCAL, anOriginalRequest.readParam(I_PROD_LOCAL).getDataType(),
                    anOriginalRequest.readValueParam(I_PROD_LOCAL));
            requestTransfer.addInputParam(I_MON_LOCAL, anOriginalRequest.readParam(I_MON_LOCAL).getDataType(),
                    anOriginalRequest.readValueParam(I_MON_LOCAL));

            // CUENTA DESTINO
            requestTransfer.addInputParam(I_CTA_DES_LOCAL, anOriginalRequest.readParam(I_CTA_DES_LOCAL).getDataType(),
                    anOriginalRequest.readValueParam(I_CTA_DES_LOCAL));

            if (anOriginalRequest.readValueParam(I_PROD_DES_LOCAL) != null) {
                logger.logInfo("ENTRA VALIDACION I_PROD_DES_LOCAL");
                requestTransfer.addInputParam(I_PROD_DES_LOCAL,
                        anOriginalRequest.readParam(I_PROD_DES_LOCAL).getDataType(),
                        anOriginalRequest.readValueParam(I_PROD_DES_LOCAL));
            }

            requestTransfer.addInputParam(I_MON_DES_LOCAL, anOriginalRequest.readParam(I_MON_DES_LOCAL).getDataType(),
                    anOriginalRequest.readValueParam(I_MON_DES_LOCAL));

            // VALORES DE TRANSACCION
            requestTransfer.addInputParam(I_VAL_LOCAL, anOriginalRequest.readParam(I_VAL_LOCAL).getDataType(),
                    anOriginalRequest.readValueParam(I_VAL_LOCAL));
            requestTransfer.addInputParam(I_CONCEPTO_LOCAL, anOriginalRequest.readParam(I_CONCEPTO_LOCAL).getDataType(),
                    anOriginalRequest.readValueParam(I_CONCEPTO_LOCAL));
            requestTransfer.addInputParam(I_NOMBRE_BENEF, anOriginalRequest.readParam(I_NOMBRE_BENEF).getDataType(),
                    anOriginalRequest.readValueParam(I_NOMBRE_BENEF));
            /*
             * requestTransfer.addInputParam("@i_ced_ruc_ben",
             * anOriginalRequest.readParam(I_DOC_BENEF).getDataType(),
             * anOriginalRequest.readValueParam(I_DOC_BENEF));
             */
            requestTransfer.addInputParam(I_BANCO_BEN, anOriginalRequest.readParam(I_BANCO_BEN).getDataType(),
                    anOriginalRequest.readValueParam(I_BANCO_BEN));
            requestTransfer.addInputParam("@i_servicio", anOriginalRequest.readParam(S_SERVICIO_LOCAL).getDataType(),
                    anOriginalRequest.readValueParam(S_SERVICIO_LOCAL));
        }

        if ("6".equals(anOriginalRequest.readValueParam(S_SERVICIO_LOCAL))
                || "7".equals(anOriginalRequest.readValueParam(S_SERVICIO_LOCAL))) {
            logger.logInfo("ENTRA VALIDACION TIPO SERVICIO 6,7");
            requestTransfer.addInputParam(I_MON_LOCAL, responseLocalValidation.readParam("@o_mon").getDataType(),
                    responseLocalValidation.readValueParam("@o_mon"));
            requestTransfer.addInputParam("@i_prod_org", responseLocalValidation.readParam("@o_prod").getDataType(),
                    responseLocalValidation.readValueParam("@o_prod"));
            requestTransfer.addInputParam("@i_cta_org", responseLocalValidation.readParam("@o_cta").getDataType(),
                    responseLocalValidation.readValueParam("@o_cta"));
            requestTransfer.addInputParam(I_PROD_DES_LOCAL,
                    responseLocalValidation.readParam("@o_prod_des").getDataType(),
                    responseLocalValidation.readValueParam("@o_prod_des"));
            requestTransfer.addInputParam(I_CTA_DES_LOCAL,
                    responseLocalValidation.readParam("@o_cta_des").getDataType(),
                    responseLocalValidation.readValueParam("@o_cta_des"));
        }

        if (logger.isInfoEnabled()) {
            logger.logInfo("Fin transfer SPI");
        }
        
        return requestTransfer;
    }
    
    /**
     * Arma la respuesta al servicio
     *
     * @param responseTransfer
     * @param aBagSPJavaOrchestration
     * @return
     */
    private IProcedureResponse transformToProcedureResponse(IProcedureResponse responseTransfer,
            Map<String, Object> aBagSPJavaOrchestration, String idTransaccion) {
        if (logger.isDebugEnabled()) {
            logger.logDebug("Inicia transformToProcedureResponse");
        }

        IProcedureResponse response = new ProcedureResponseAS();
        response.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
        IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
        ServerResponse serverResponse = (ServerResponse) aBagSPJavaOrchestration.get(RESPONSE_SERVER);

        response.setReturnCode(responseTransfer.getReturnCode());
        if (serverResponse.getOnLine() && responseTransfer.getReturnCode() != 0) {
            // ONLINE Y HUBO ERROR
            response = Utils.returnException(Utils.returnArrayMessage(responseTransfer));

        } else {
            response.addParam("@o_referencia", ICTSTypes.SYBINT4, 0,
                    String.valueOf(responseTransfer.readValueParam("@o_referencia")));

            // response.addParam("@o_ref_branch", ICTSTypes.SYBINT4, 0,
            // String.valueOf(originalRequest.readValueParam(S_SSN_BRANCH)));

            response.setReturnCode(responseTransfer.getReturnCode());
            aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, response);

            idTransaccion = String.valueOf(originalRequest.readValueParam(S_SSN_BRANCH));

            logger.logDebug(CLASS_NAME + "Respuesta TRANSACCION ID --> " + idTransaccion);
        }

        aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, response);

        if (logger.isDebugEnabled()) {
            logger.logDebug(CLASS_NAME + "Respuesta transformToProcedureResponse -->"
                    + response.getProcedureResponseAsString());
            logger.logDebug("Fin transformToProcedureResponse");
        }
        return response;
    }
    
    private IProcedureResponse mappingResponseSpeiToProcedure(SpeiMappingResponse response,
            IProcedureResponse responseTransfer, Map<String, Object> aBagSPJavaOrchestration) {
        String wInfo = "[SPITransferOrchestrationCore][mappingResponseSpeiToProcedure] ";
        logger.logInfo(wInfo + Constants.INIT_TASK);
        logger.logInfo(wInfo + "response de entrada spei: " + response.toString());

        if (response.getErrorCode() != null) {
            this.successConnector = false;
            return Utils.returnException(1, ERROR_SPEI);
        } else {
            this.successConnector = true;
        }

        logger.logInfo(wInfo + Constants.END_TASK);

        return putSpeiResponseOnBag(response, responseTransfer, aBagSPJavaOrchestration);
    }

    private IProcedureResponse mappingResponseSpeiToProcedureOffline(SpeiMappingResponse response,
            IProcedureResponse responseTransfer, Map<String, Object> aBagSPJavaOrchestration) {
        String wInfo = "[SPITransferOrchestrationCore][mappingResponseSpeiToProcedureOffline] ";
        logger.logInfo(wInfo + Constants.INIT_TASK);
        logger.logInfo(wInfo + " "+response.getErrorCode()+ " "+"response de entrada: " + response.toString());

        aBagSPJavaOrchestration.put("@i_transaccion_spei", response.getCodigoAcc());

        if (response.getErrorCode() != null) {
            responseTransfer.addParam(Constants.I_FAIL_PROVIDER, ICTSTypes.SQLVARCHAR, 1, "S");
            return responseTransfer;
        }

        logger.logInfo(wInfo + Constants.END_TASK);

        return putSpeiResponseOnBag(response, responseTransfer, aBagSPJavaOrchestration);

    }

    private IProcedureResponse putSpeiResponseOnBag(SpeiMappingResponse response, IProcedureResponse responseTransfer,
            Map<String, Object> aBagSPJavaOrchestration) {
        String wInfo = "[SPITransferOrchestrationCore][putSpeiResponseOnBag] ";
        logger.logInfo(wInfo + "init task ---->");
        logger.logInfo(wInfo + "response de entrada: " + response.toString());

        responseTransfer.addParam(Constants.O_CLAVE_RASTREO, ICTSTypes.SQLVARCHAR, response.getClaveRastreo().length(),
                response.getClaveRastreo());

        aBagSPJavaOrchestration.put(Constants.I_CLAVE_RASTREO, response.getClaveRastreo());
        aBagSPJavaOrchestration.put(Constants.I_MENSAJE_ACC, response.getMensajeAcc());
        aBagSPJavaOrchestration.put(Constants.I_ID_SPEI_ACC, response.getCodigoAcc());
        aBagSPJavaOrchestration.put(Constants.I_CODIGO_ACC, response.getCodigoAcc());

        aBagSPJavaOrchestration.put(Constants.O_SPEI_REQUEST, response.getSpeiRequest());
        aBagSPJavaOrchestration.put(Constants.O_SPEI_RESPONSE, response.getSpeiResponse());

        logger.logInfo(wInfo + "end task ---->");

        return responseTransfer;
    }

    private SpeiMappingRequest mappingBagToSpeiRequest(Map<String, Object> aBagSPJavaOrchestration,
            IProcedureResponse responseTransfer, IProcedureRequest anOriginalRequest) {
        String wInfo = "[SPITransferOrchestrationCore][transformBagToSpeiRequest] ";
        logger.logInfo(wInfo + Constants.INIT_TASK);
        
        String clearCard="";
        
        logger.logInfo("jc logger spei");
      //  logger.logInfo(aBagSPJavaOrchestration.get("o_prod_des").toString());
      //  logger.logInfo(aBagSPJavaOrchestration.get("clear_card").toString());
        
    	int prod_des =  Integer.parseInt((String) aBagSPJavaOrchestration.get("o_prod_des"));    	
    	if (prod_des == 3 && aBagSPJavaOrchestration.get("clear_card")!=null) {
    		 clearCard=aBagSPJavaOrchestration.get("clear_card").toString();
    	}

        SpeiMappingRequest request = new SpeiMappingRequest();
        request.setConceptoPago(anOriginalRequest.readValueParam(Constants.I_CONCEPTO));
        request.setCuentaOrdenante(anOriginalRequest.readValueParam(Constants.I_CUENTA));
        logger.logInfo(request);
        if (prod_des == 3) {
        	request.setCuentaClabeBeneficiario(clearCard);
        }else {
        request.setCuentaClabeBeneficiario(anOriginalRequest.readValueParam(Constants.I_CUENTA_DESTINO));
        }
        request.setNombreBeneficiario(anOriginalRequest.readValueParam(Constants.I_NOMBRE_BENEFICIARIO));
        request.setInstitucionContraparte(anOriginalRequest.readValueParam(Constants.I_BANCO_BENEFICIARIO));
        request.setBancoDestino(aBagSPJavaOrchestration.get(Constants.I_BANCO_DESTINO) != null
                ? aBagSPJavaOrchestration.get(Constants.I_BANCO_DESTINO).toString()
                : "");

        BigDecimal monto = new BigDecimal(anOriginalRequest.readValueParam(Constants.I_VALOR));
        request.setMonto(monto.setScale(2, RoundingMode.CEILING));
        request.setRfcCurpBeneficiario("ND");
        request.setTipoCuentaBeneficiario(anOriginalRequest.readValueParam("@i_prod_des"));
        request.setEnteBancaVirtual(anOriginalRequest.readValueParam("@s_cliente"));
        request.setLogin(anOriginalRequest.readValueParam("@i_login"));
        request.setReferenceNumber(anOriginalRequest.readValueParam("@i_reference_number"));
        request.setServicio(anOriginalRequest.readValueParam(S_SERVICIO_LOCAL));

//TRANSACCIONALIDAD
        String transaccionSpei = anOriginalRequest.readValueParam("@i_transaccion_spei");
        if (null == transaccionSpei) { // Si esta en offline no hay ssn de debito
            transaccionSpei = anOriginalRequest.readValueParam("@s_ssn"); // se obtiene ssn de CTS
        }

        request.setSsnDebito(transaccionSpei);
        request.setSsnBranchDebito(anOriginalRequest.readValueParam("@s_ssn_branch"));

//CTS VARIABLE
        request.setCtsSsn(anOriginalRequest.readValueParam("@s_ssn"));
        request.setCtsServ(anOriginalRequest.readValueParam("@s_srv"));
        request.setCtsUser(anOriginalRequest.readValueParam("@s_user"));
        request.setCtsTerm(anOriginalRequest.readValueParam("@s_term"));
        request.setCtsRol(anOriginalRequest.readValueParam("@s_rol"));
        request.setCtsDate(anOriginalRequest.readValueParam("@s_date"));

// VARIABLE DE ORIGEN
        logger.logInfo(wInfo + " trn_origen: " + anOriginalRequest.readValueFieldInHeader("trn_origen"));
        request.setTrnOrigen(anOriginalRequest.readValueFieldInHeader("trn_origen"));
        request.setUser(anOriginalRequest.readValueFieldInHeader("user"));
        request.setOffice(anOriginalRequest.readValueFieldInHeader("ofi"));
        request.setServer(anOriginalRequest.readValueFieldInHeader("srv"));
        request.setTerminal(anOriginalRequest.readValueFieldInHeader("term"));

        logger.logInfo(wInfo + Constants.END_TASK);

        return request;

    }

    @Override
    public ICoreServiceReexecutionComponent getCoreServiceReexecutionComponent() {
        return null;
    }

    @Override
    protected ICoreServiceSendNotification getCoreServiceNotification() {
        return coreServiceNotification;
    }

    @Override
    public ICoreService getCoreService() {
        return coreService;
    }

    @Override
    public ICoreServer getCoreServer() {
        return coreServer;
    }

    @Override
    protected IProcedureResponse validateCentralExecution(IProcedureRequest request,
            Map<String, Object> aBagSPJavaOrchestration) {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.cobiscorp.ecobis.ib.orchestration.base.transfers.TransferBaseTemplate
     * #transformNotificationRequest(com.cobiscorp.cobis.cts.domains.
     * IProcedureRequest,
     * com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse,
     * java.util.Map)
     */
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
            notification.setId("N90");
        else
            notification.setId("N91");

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
        if (!Utils.isNull(anOriginalRequest.readParam(S_SSN_BRANCH)))
            notificationDetail.setReference(anOriginalRequest.readValueParam(S_SSN_BRANCH));
        if (!Utils.isNull(anOriginalRequest.readParam(I_NOMBRE_BENEF)))
            notificationDetail.setAuxiliary1(anOriginalRequest.readValueParam(I_NOMBRE_BENEF));

        notificationRequest.setClient(client);
        notificationRequest.setNotification(notification);
        notificationRequest.setNotificationDetail(notificationDetail);
        notificationRequest.setOriginProduct(product);
        return notificationRequest;
    }

    @Override
    protected void addParametersRequestUpdateLocal(IProcedureRequest aProcedureRequest,
            IProcedureRequest anOriginalRequest) {
    }

    @Override
    public IProcedureResponse processResponse(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
        return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
    }
    
    private IProcedureResponse executeBanpay(Map<String, Object> aBagSPJavaOrchestration,
            IProcedureResponse responseTransfer, IProcedureRequest originalRequest)
    {
    	
    	aBagSPJavaOrchestration.put("@o_referencia", responseTransfer.readValueParam("@o_referencia"));
        // SE LLAMA LA SERVICIO DE BANPAY REVERSA DE REVERSA
        List<String> respuesta = banpayExecution(originalRequest, aBagSPJavaOrchestration);
        // SE ACTUALIZA TABLA DE SECUENCIAL SPEI
        speiSec(originalRequest, aBagSPJavaOrchestration);
        // SE HACE LA VALIDACION DE LA RESPUESTA
        if (respuesta != null)
        {
            if (!respuesta.get(0).equals("00"))
            {
                // SE CAMBIA ESTADO DE REGISTRO
                speiGetDataRB(originalRequest, aBagSPJavaOrchestration);
                // SE HACELA REVERSA DE LA NOTA DE DEBITO
                speiRollback(originalRequest, aBagSPJavaOrchestration);
                //SE REGISTRA EN LOCAL CASO DE ERROR
                persistDataLocalOnFailureSpei(originalRequest, aBagSPJavaOrchestration);

                if (logger.isDebugEnabled())
                {
                    logger.logDebug("Error SPEI");
                }
                
                this.successConnector = false;
                return Utils.returnException(1, ERROR_SPEI);
            } else
            {
                if (logger.isDebugEnabled())
                {
                    logger.logDebug("Paso exitoso");
                }
                // SE ADJUNTA LA CLAVE DE RASTREO
                responseTransfer.addParam("@o_clave_rastreo", ICTSTypes.SQLVARCHAR, respuesta.get(2).length(),
                        respuesta.get(2));
                
                this.successConnector = true;
                aBagSPJavaOrchestration.put(Constants.I_CLAVE_RASTREO, respuesta.get(2));

                /*
                 * String wPrcessingSpeiMessage = "PENDIENTE";
                 * responseTransfer.addParam("@o_clave_rastreo", ICTSTypes.SQLVARCHAR,
                 * wPrcessingSpeiMessage.length(),wPrcessingSpeiMessage);
                 */

            }
        } else
        {
            if (logger.isDebugEnabled())
            {
                logger.logDebug("List<String> respuesta error o null");
            }
            // SE CAMBIA ESTADO DE REGISTRO
            speiGetDataRB(originalRequest, aBagSPJavaOrchestration);
            // SE HACELA REVERSA DE LA NOTA DE DEBITO
            speiRollback(originalRequest, aBagSPJavaOrchestration);
            //SE REGISTRA EN LOCAL CASO DE ERROR
            persistDataLocalOnFailureSpei(originalRequest, aBagSPJavaOrchestration);
            
            this.successConnector = false;

            return Utils.returnException(1, ERROR_SPEI);
        }
        
        return responseTransfer;
    }
    
    protected List<String>  banpayExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
        // SE INICIALIZA VARIABLE
        List<String> response = null;

        if (logger.isInfoEnabled()) {
            logger.logInfo("Entrando a banpayExecution");
        }
        try {
        	 String ctaDestino = "";
             String opTcClaveBen = "40";//en caso de ser nulo 
             if(bag.get("opTcClaveBen")!=null)
             {
            	 opTcClaveBen = bag.get("opTcClaveBen").toString();
             }
         	
        	 if(bag.get("card_destination")!=null && bag.get("codTarDeb")!=null &&"03".equals(bag.get("codTarDeb")))
        	 {
        		 ctaDestino = bag.get("clear_card").toString();
        	 }else
        		 ctaDestino = anOriginalRequest.readValueParam("@i_cta_des");
             
            // SE SETEAN LOS PARAMETROS DE ENTRADA
            anOriginalRequest.addInputParam("@i_concepto_pago", ICTSTypes.SQLVARCHAR,
                    anOriginalRequest.readValueParam("@i_concepto"));
            anOriginalRequest.addInputParam("@i_cuenta_beneficiario", ICTSTypes.SQLVARCHAR,
            		ctaDestino);
            anOriginalRequest.addInputParam("@i_cuenta_ordenante", ICTSTypes.SQLVARCHAR,
                    anOriginalRequest.readValueParam("@i_cta"));

            // SE OBTIENE LA DATA FALTANTE
            List<String> data = speiData(anOriginalRequest, bag);
            
            bag.put("nombre_ordenante",  data.get(1));
            bag.put("curp_ordenante", data.get(2));
            bag.put("tipo_cuenta_ordenante", data.get(3));
            bag.put("cuenta_clab", data.get(6));
            bag.put("institucion_ordenante", data.get(0));
            //FECHA
            String proccessDate = getParam(anOriginalRequest, "PRODAK", "AHO");
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date fecha = dateFormat.parse(proccessDate);
            SimpleDateFormat forma = new SimpleDateFormat("yyyyMMdd");
            anOriginalRequest.addInputParam("@i_fecha_operacion", ICTSTypes.SQLVARCHAR, forma.format(fecha));
            anOriginalRequest.addInputParam("@i_institucion_contraparte", ICTSTypes.SQLVARCHAR,
                    anOriginalRequest.readValueParam("@i_banco_ben"));
            anOriginalRequest.addInputParam("@i_institucion_operante", ICTSTypes.SQLVARCHAR, data.get(0));
            anOriginalRequest.addInputParam("@i_monto", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_val"));
            anOriginalRequest.addInputParam("@i_nombre_beneficiario", ICTSTypes.SQLVARCHAR,
                    anOriginalRequest.readValueParam("@i_nombre_benef"));
            anOriginalRequest.addInputParam("@i_nombre_ordenante", ICTSTypes.SQLVARCHAR, data.get(1));
            anOriginalRequest.addInputParam("@i_referencia_numerica", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_reference_number")); // OPCIONAL
            anOriginalRequest.addInputParam("@i_rfc_curp_beneficiario", ICTSTypes.SQLVARCHAR, "ND"); // OPCIONAL
            anOriginalRequest.addInputParam("@i_rfc_curp_ordenante", ICTSTypes.SQLVARCHAR, data.get(2));
            anOriginalRequest.addInputParam("@i_tipo_cuenta_beneficiario", ICTSTypes.SQLINT1,
            		opTcClaveBen);
            anOriginalRequest.addInputParam("@i_tipo_cuenta_ordenante", ICTSTypes.SQLINT1, data.get(3));

            anOriginalRequest.addInputParam("@i_tipo_pago", ICTSTypes.SQLINT1, "1");
            anOriginalRequest.addInputParam("@i_id", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_ssn"));

            //DatosAccendo
            anOriginalRequest.addInputParam("@i_beneficiario_cc", ICTSTypes.SQLINT1, data.get(4));
            anOriginalRequest.addInputParam("@i_tercer_ordenante", ICTSTypes.SQLINT1, data.get(5));

            anOriginalRequest.addInputParam("@i_cuenta_clabe", ICTSTypes.SQLVARCHAR, data.get(6));
            // VARIABLES DE SALIDA
            anOriginalRequest.addOutputParam("@o_cod_respuesta", ICTSTypes.SQLVARCHAR, "X");
            anOriginalRequest.addOutputParam("@o_msj_respuesta", ICTSTypes.SQLVARCHAR, "X");
            anOriginalRequest.addOutputParam("@o_clave_rastreo", ICTSTypes.SQLVARCHAR, "X");
            anOriginalRequest.addOutputParam("@o_id", ICTSTypes.SQLINT1, "0");
            anOriginalRequest.addOutputParam("@o_descripcion_error", ICTSTypes.SQLVARCHAR, "X");

            AccendoConnectionData loadded= retrieveAccendoConnectionData();

            anOriginalRequest.addInputParam("@i_empresa", ICTSTypes.SQLVARCHAR, loadded.getCompanyId());
            anOriginalRequest.addInputParam("@i_algotih", ICTSTypes.SQLVARCHAR, "SHA256withRSA");
            anOriginalRequest.addInputParam("@i_prefijo_rastreo", ICTSTypes.SQLVARCHAR, loadded.getTrackingKeyPrefix());
            anOriginalRequest.addInputParam("@i_base_url", ICTSTypes.SQLVARCHAR, loadded.getBaseUrl());

            String claveRastreo = loadded.getTrackingKeyPrefix()+Methods.getActualDateYyyymmdd()+bag.get("@o_referencia");
            anOriginalRequest.addInputParam("@i_clave_rastreo_connection", ICTSTypes.SQLVARCHAR,claveRastreo) ;
            bag.put("@i_clave_rastreo", claveRastreo);
            bag.put("clave_rastreo",  claveRastreo);

            anOriginalRequest.addInputParam("@i_transaccion_spei", ICTSTypes.SQLVARCHAR, (String) bag.get("@o_referencia"));
            anOriginalRequest.addInputParam("@i_ssn_branch", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_ssn_branch"));
            
            //idenficador de operacion se deberia
            anOriginalRequest.addInputParam("@i_categoria", ICTSTypes.SQLVARCHAR, "CARGAR_ODP");
            
            anOriginalRequest.addInputParam("@i_estado", ICTSTypes.SQLVARCHAR, "A");
            anOriginalRequest.addInputParam("@i_tipo_orden", ICTSTypes.SQLVARCHAR, "E");
            anOriginalRequest.addInputParam("@i_prioridad", ICTSTypes.SQLVARCHAR, "0");
            anOriginalRequest.addInputParam("@i_op_topologia", ICTSTypes.SQLVARCHAR, "V");
            anOriginalRequest.addInputParam("@i_op_me_clave", ICTSTypes.SQLVARCHAR, "9");
            
            anOriginalRequest.addInputParam("@i_operatingInstitution", ICTSTypes.SQLVARCHAR, getParam(anOriginalRequest, "CBCCDK", "AHO"));
            
            // SE HACE LA LLAMADA AL CONECTOR
            // SE HACE LA LLAMADA AL CONECTOR
 			bag.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorSpei)");
 			anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500115");
 			anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500115");
            // SE EJECUTA
            IProcedureResponse connectorSpeiResponse = executeProvider(anOriginalRequest, bag);

            //se regresan a la trn original, para el registro de spei
            anOriginalRequest.removeParam("@t_trn");
            anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "1870013");
            anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1870013");
            // SE VALIDA LA RESPUESTA
            if (!connectorSpeiResponse.hasError()) {
                if (logger.isDebugEnabled()) {
                    logger.logDebug("success CISConnectorSpei: true");
                    logger.logDebug("connectorSpeiResponse: " + connectorSpeiResponse.getParams());
                }
                // SE MAPEAN LAS VARIABLES DE SALIDA
                response = new ArrayList<String>();
                String codRespuesta=connectorSpeiResponse.readValueParam("@o_cod_respuesta");

                response.add(connectorSpeiResponse.readValueParam("@o_cod_respuesta"));
                logger.logDebug("readValueParam @o_cod_respuesta: " + connectorSpeiResponse.readValueParam("@o_cod_respuesta"));
                logger.logDebug("readValueParam @o_msj_respuesta: " + connectorSpeiResponse.readValueParam("@o_msj_respuesta"));
                response.add(connectorSpeiResponse.readValueParam("@o_msj_respuesta"));

                response.add(connectorSpeiResponse.readValueParam("@o_clave_rastreo"));
                response.add(connectorSpeiResponse.readValueParam("@o_id"));
                response.add(connectorSpeiResponse.readValueParam("@o_descripcion_error"));

                response.add(connectorSpeiResponse.readValueParam("@i_mensaje_acc"));
                response.add(connectorSpeiResponse.readValueParam("@i_id_spei_acc"));
                response.add(connectorSpeiResponse.readValueParam("@i_codigo_acc"));
                response.add(anOriginalRequest.readValueParam("@i_transaccion_spei"));

                response.add(connectorSpeiResponse.readValueParam("@o_spei_request"));
                response.add(connectorSpeiResponse.readValueParam("@o_spei_response"));

                if (logger.isDebugEnabled()) {
                    logger.logDebug("CODIGO RASTREO DX"+connectorSpeiResponse.readValueParam("@o_clave_rastreo"));
                    logger.logDebug("connectorSpeiResponse: " + connectorSpeiResponse.getParams());
                }

                // SE ALMACENA EL DATO DE CLAVE DE RASTREO
                String rastreo = connectorSpeiResponse.readValueParam("@o_clave_rastreo");
                if(null == rastreo){
                    rastreo = anOriginalRequest.readValueParam("i_clave_rastreo_connection");
                }
                bag.put("@i_clave_rastreo", rastreo);
                bag.put("@i_msj_respuesta", connectorSpeiResponse.readValueParam("@o_msj_respuesta"));
                bag.put("@i_cod_respuesta", connectorSpeiResponse.readValueParam("@o_cod_respuesta"));
                bag.put("@i_id", connectorSpeiResponse.readValueParam("@o_id"));
                bag.put("@i_descripcion_error", connectorSpeiResponse.readValueParam("@o_descripcion_error"));

                bag.put("@i_mensaje_acc", connectorSpeiResponse.readValueParam("@i_mensaje_acc"));
                bag.put("@i_id_spei_acc", connectorSpeiResponse.readValueParam("@i_id_spei_acc"));
                bag.put("@i_codigo_acc", connectorSpeiResponse.readValueParam("@i_codigo_acc"));
                logger.logDebug("transaccion Spei " +  anOriginalRequest.readValueParam("@i_transaccion_spei"));
                bag.put("@i_transaccion_spei", anOriginalRequest.readValueParam("@i_transaccion_spei"));
                
                if (logger.isDebugEnabled()) {                  
                    logger.logDebug("i_ssn_branch origin" + anOriginalRequest.readValueParam("@i_ssn_branch"));
                }               
                bag.put("@i_ssn_branch", anOriginalRequest.readValueParam("@i_ssn_branch"));

                bag.put("@o_spei_request", connectorSpeiResponse.readValueParam("@o_spei_request"));
                bag.put("@o_spei_response", connectorSpeiResponse.readValueParam("@o_spei_response"));

                bag.put("@o_transaccion_spei",anOriginalRequest.readValueParam("@i_transaccion_spei"));
                data = null;
            } else {

                if (logger.isDebugEnabled()) {
                    logger.logDebug("Error Catastrifico respuesta de BANPAY");
                    logger.logDebug("Error connectorSpeiResponse Catastrifico: " + connectorSpeiResponse);
                }
            }
        } catch (Exception e) {
            logger.logError(e);
            logger.logInfo("Error Catastrofico de banpayExecution");
            e.printStackTrace();
            response = null;
            logger.logInfo("Error Catastrofico de banpayExecution");

        } finally {
            if (logger.isInfoEnabled()) {
                logger.logInfo("Saliendo de banpayExecution");
            }
        }
        // SE REGRESA RESPUESTA
        return response;
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

            anOriginalRequest.addInputParam("@i_customer_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_external_customer_id"));

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

            registerRequestBlockOperation(connectorBlockOperationResponse, jsonRequest.toString(), aRequest.readValueParam("@i_external_customer_id"));
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
        request.addInputParam("@i_ente", ICTSTypes.SQLINTN, customerId);
        request.addInputParam("@i_error_message", ICTSTypes.SQLVARCHAR, message);

        IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

        if (logger.isDebugEnabled()) {
            logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
        }
    }

	private AccendoConnectionData retrieveAccendoConnectionData(){
        String wInfo = "[RegisterAccountsJobImpl][retrieveSpeiConnectionData] ";

        logger.logInfo(wInfo + "init task ------>");


        IProcedureResponse response = null;
        int sequence = 20;
        int accumulated = 0;

        response = getAccendoProfile();

        AccendoConnectionData accendoConnectionData = new AccendoConnectionData();

        if(response!=null && response.getResultSetListSize() > 0) {
            logger.logInfo("jcos V2 resultados validacion");

            IResultSetBlock block= response.getResultSet(1);
            if(block!=null &&  block.getData().getRowsNumber()>=1) {
                IResultSetData data = block.getData();
                for(IResultSetRow row :data.getRowsAsArray()) {
                    logger.logInfo("jcos Access to Data parameter");

                    logger.logInfo("names "+ this.getString(row, 2));
                    logger.logInfo("valor "+ this.getString(row, 3));

                    if( this.getString(row, 2).equals(Constants.COMPANY_ID)){
                        accendoConnectionData.setCompanyId(this.getString(row, 3));
                    }

                    if( this.getString(row, 2).equals(Constants.BASE_URL)){
                        accendoConnectionData.setBaseUrl(this.getString(row, 3));
                    }

                    if( this.getString(row, 2).equals(Constants.TRACKING_KEY_PREFIX)){
                        accendoConnectionData.setTrackingKeyPrefix(this.getString(row, 3));
                    }
                }
            }
        }

        logger.logInfo(wInfo + "end task ------>");

        return accendoConnectionData;

    }
    
    public  String getString(IResultSetRow row, int col) {
        String resultado = null;
        Object obj = null;
        try {
            obj = getObject(row, col);
            resultado = (null == obj) ? null : obj.toString();
            if (null != resultado) {
                resultado = (resultado.trim().equalsIgnoreCase("null") ? null : resultado);
            }
        } catch (Exception e) {
            logger.logError("[getString] Error obteniendo cadena de respuesta CTS.", e);
        }
        return resultado;
    }
    
    public static Object getObject(IResultSetRow row, int col) {
        IResultSetRowColumnData iResultColumnData = null;
        iResultColumnData = row.getRowData(col);
        return (null == iResultColumnData) ? null : iResultColumnData.getValue();
    }
    
    private IProcedureResponse getAccendoProfile() {
        if (logger.isInfoEnabled()) {
            logger.logInfo(CLASS_NAME + "Initialize method jcos getAccendoProfile");
        }
        IProcedureRequest request = new ProcedureRequestAS();
        request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500065");
        request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
                IMultiBackEndResolverService.TARGET_CENTRAL);
        request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
        request.setSpName("cob_bvirtual..sp_get_catalog_by_table");
        request.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500065");
        request.addInputParam("@tabla", ICTSTypes.SYBVARCHAR, "cl_spei_io");
        request.addInputParam("@operacion", ICTSTypes.SYBVARCHAR, "Q");

        IProcedureResponse response = executeCoreBanking(request);
        if (logger.isInfoEnabled()) {
            logger.logInfo("Finalize method jcos getAccendoProfile");
        }

        return response;
    }

    protected boolean speiGetDataRB(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
        // SE INICIALIZA VARIABLE
        boolean response = false;
        if (logger.isInfoEnabled()) {
            logger.logInfo("Entrando a speiGetDataRB");
        }
        try {
            IProcedureRequest request = initProcedureRequest(anOriginalRequest.clone());

            // SE SETEAN DATOS
            request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_CENTRAL);
            request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
            request.setSpName("cob_bvirtual..sp_secuencial_spei");
            request.addInputParam("@t_trn", ICTSTypes.SQLINTN, "18011");
            request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "C");

            logger.logInfo("@i_clave_rastreo bag: " + bag.get("@i_clave_rastreo"));
            request.addInputParam("@i_clave_rastreo", ICTSTypes.SQLVARCHAR, bag.get("@i_clave_rastreo").toString());

            request.addInputParam("@i_cuenta_ori", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cta"));

            if(null != bag.get("@i_cod_respuesta")){
                request.addInputParam("@i_estatus_respuesta", ICTSTypes.SQLINTN, bag.get("@i_cod_respuesta").toString());
            }

            request.addInputParam("@i_descripcion_error", ICTSTypes.SQLVARCHAR, ERROR_SPEI); //
            //SE SETEAN VARIABLES DE SALIDA
            request.addOutputParam("@o_cuenta_ori", ICTSTypes.SQLVARCHAR, "XXXX");
            request.addOutputParam("@o_monto", ICTSTypes.SQLMONEY, "0");
            request.addOutputParam("@o_mon", ICTSTypes.SQLINTN, "0");
            request.addOutputParam("@o_comision", ICTSTypes.SQLMONEY, "0");
            request.addOutputParam("@o_ssn_branch", ICTSTypes.SQLINTN, "0");
            request.addOutputParam("@o_tipo_error", ICTSTypes.SQLINTN, "0");
            request.addOutputParam("@o_ssn_branch", ICTSTypes.SQLINTN, "0");

            // SE EJECUTA Y SE OBTIENE LA RESPUESTA
            IProcedureResponse pResponse = executeCoreBanking(request);

            //SE OBTIENEN LAS VARIABLES DE SALIDA
            bag.put("@o_cuenta_ori", pResponse.readValueParam("@o_cuenta_ori"));
            bag.put("@o_monto", pResponse.readValueParam("@o_monto"));
            bag.put("@o_mon", pResponse.readValueParam("@o_mon"));
            bag.put("@o_comision", pResponse.readValueParam("@o_comision"));
            bag.put("@o_proceso_origen", pResponse.readValueParam("@o_proceso_origen"));
            bag.put("@o_tipo_error", pResponse.readValueParam("@o_tipo_error"));
            bag.put("@o_ssn_branch", pResponse.readValueParam("@o_ssn_branch"));

            logger.logInfo("@o_cuenta_ori bag: " + bag.get("@o_cuenta_ori"));
            logger.logInfo("@o_monto bag: " + bag.get("@o_monto"));
            logger.logInfo("@o_mon bag: " + bag.get("@o_mon"));
            logger.logInfo("@o_comision bag: " + bag.get("@o_comision"));
            logger.logInfo("@o_proceso_origen bag: " + bag.get("@o_proceso_origen"));
            logger.logInfo("@o_tipo_error bag: " + bag.get("@o_tipo_error"));
            logger.logInfo("@o_ssn_branch bag: " + bag.get("@o_ssn_branch"));

            response = true;
        } catch (Exception e) {

            logger.logError(e);
            e.printStackTrace();
            logger.logError(e);
            response = false;
            logger.logInfo("Error de speiGetDataRB");
        } finally {
            if (logger.isInfoEnabled()) {
                logger.logInfo("Saliendo de speiGetDataRB");
            }
        }
        // SE REGRESA RESPUESTA
        return response;
    }
    
    protected boolean speiSec(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
        // SE INICIALIZA VARIABLE
        boolean response = false;
        if (logger.isInfoEnabled()) {
            logger.logInfo("Entrando a speiSec");
        }
        try {
            IProcedureRequest request = initProcedureRequest(anOriginalRequest.clone());

            // SE SETEAN DATOS
            request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
                    IMultiBackEndResolverService.TARGET_CENTRAL);
            request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
            request.setSpName("cob_bvirtual..sp_secuencial_spei");

            request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "B");
            request.addInputParam("@t_trn", ICTSTypes.SQLINTN, "18011");
            //@t_ssn_corr
            request.addInputParam("@i_cuenta_ori", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cta"));


            request.addInputParam("@i_clave_rastreo", ICTSTypes.SQLVARCHAR, bag.get("@i_clave_rastreo").toString());
            logger.logInfo("@i_clave_rastreo bag: " + bag.get("@i_clave_rastreo"));

            // SE EJECUTA Y SE OBTIENE LA RESPUESTA
            IProcedureResponse pResponse = executeCoreBanking(request);

            response = true;
        } catch (Exception e) {

            logger.logError(e);
            e.printStackTrace();
            logger.logInfo("Error de speiSec");
            response = false;
        } finally {
            if (logger.isInfoEnabled()) {
                logger.logInfo("Saliendo de speiSec");
            }
        }
        // SE REGRESA RESPUESTA
        return response;
    }
    
    protected boolean speiRollback(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
        // SE INICIALIZA VARIABLE
        boolean response = false;
        if (logger.isInfoEnabled()) {
            logger.logInfo("Entrando a speiRollback");
        }
        try {
            IProcedureRequest request = initProcedureRequest(anOriginalRequest.clone());

            // SE SETEAN DATOS
            request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_CENTRAL);
            request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
            request.setSpName("cob_bvirtual..sp_reverso_spei");
            request.addInputParam("@t_trn", ICTSTypes.SQLINTN, "18009");
            	
            
            // DATOS CUENTA ORIGEN
            request.addInputParam("@i_cuenta_ori", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cta"));
            request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, ERROR_SPEI);
            request.addInputParam("@i_monto", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_val"));
            request.addInputParam("@i_mon", ICTSTypes.SQLINT1, bag.get("@o_mon").toString());
            request.addInputParam("@i_servicio", ICTSTypes.SQLINT1, anOriginalRequest.readValueParam(S_SERVICIO_LOCAL));
            request.addInputParam("@i_tipo_error", ICTSTypes.SQLINTN, "7");
            logger.logInfo("Reversando transaccion::: "+ anOriginalRequest.readValueParam("@i_transaccion_spei"));
            request.addInputParam("@t_ssn_corr", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_transaccion_spei"));
            //VALIDA COMISION
            if (bag.get("@o_comision") != null) {
                request.addInputParam("@i_comision", ICTSTypes.SQLMONEY, bag.get("@o_comision").toString());
            } else {
                request.addInputParam("@i_comision", ICTSTypes.SQLMONEY, "0");
            }
            request.addInputParam("@s_ssn_branch", ICTSTypes.SQLINTN, bag.get("@o_ssn_branch").toString());
            // CLAVE DE RASTREO
            request.addInputParam("@i_clave_rastreo", ICTSTypes.SQLVARCHAR, bag.get("@i_clave_rastreo").toString());
            logger.logInfo("@i_clave_rastreo bag: " + bag.get("@i_clave_rastreo"));
            request.addInputParam("@i_proceso_origen", ICTSTypes.SQLINT4, "1");
            request.addInputParam("@i_transaccion_core", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_transaccion_spei"));

            //VARIABLES DE CTS
            request.addInputParam("@s_ssn", ICTSTypes.SQLINTN, anOriginalRequest.readValueParam("@s_ssn"));
            request.addInputParam("@s_srv", ICTSTypes.SQLINTN, anOriginalRequest.readValueParam("@s_srv"));
            request.addInputParam("@s_user", ICTSTypes.SQLINTN, anOriginalRequest.readValueParam("@s_user"));
            request.addInputParam("@s_term", ICTSTypes.SQLINTN, anOriginalRequest.readValueParam("@s_term"));
            request.addInputParam("@s_rol", ICTSTypes.SQLINTN, anOriginalRequest.readValueParam("@s_rol"));
            request.addInputParam("@s_date", ICTSTypes.SQLDATETIME, anOriginalRequest.readValueParam("@s_date"));
            //reversa nuevo campo envio de transaccion para reversa
            request.addInputParam("@i_transaction_core", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_clave_rastreo"));


            // SE EJECUTA Y SE OBTIENE LA RESPUESTA
            IProcedureResponse pResponse = executeCoreBanking(request);

            response = true;
        } catch (Exception e) {
            logger.logInfo("Error de speiRollback");
            logger.logError(e);
            response = false;
        } finally {
            if (logger.isInfoEnabled()) {
                logger.logInfo("Saliendo de speiRollback");
            }
        }
        // SE REGRESA RESPUESTA
        return response;
    }

    private IProcedureResponse persistDataLocalOnFailureSpei(IProcedureRequest anOriginalRequest, Map<String, Object> bag){
        String wInfo = "[SPITransferOrchestrationCore][persistDataLocalOnFailureSpei] ";
        logger.logInfo(wInfo + "init task ----> ");
        IProcedureRequest request = initProcedureRequest(anOriginalRequest.clone());

        // SE SETEAN DATOS
        request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
                IMultiBackEndResolverService.TARGET_LOCAL);
        request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
        request.setSpName("cob_bvirtual..sp_registra_spei");
        request.addInputParam("@t_trn", ICTSTypes.SQLINTN, "18010");
        request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "L");
        request.addInputParam("@i_ente_bv", ICTSTypes.SYBINT4, anOriginalRequest.readValueParam("@s_cliente"));
        request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR,anOriginalRequest.readValueParam("@i_login"));
        request.addInputParam("@i_canal", ICTSTypes.SQLVARCHAR,anOriginalRequest.readValueParam(S_SERVICIO_LOCAL));
        request.addInputParam("@i_cuenta_ori", ICTSTypes.SQLVARCHAR,anOriginalRequest.readValueParam("@i_cta"));
        request.addInputParam("@i_cuenta_des", ICTSTypes.SQLVARCHAR,anOriginalRequest.readValueParam("@i_cta_des"));
        request.addInputParam("@i_monto", ICTSTypes.SQLVARCHAR,anOriginalRequest.readValueParam("@i_val"));
        request.addInputParam("@i_moneda", ICTSTypes.SQLVARCHAR,anOriginalRequest.readValueParam("@i_mon"));
        request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR,anOriginalRequest.readValueParam("@i_concepto"));
        request.addInputParam("@i_banco_dest", ICTSTypes.SQLVARCHAR,bag.get("@i_banco_dest") != null ? bag.get("@i_banco_dest").toString() : "");
        request.addInputParam("@i_cuenta_clabe_dest", ICTSTypes.SQLVARCHAR,anOriginalRequest.readValueParam("@i_cta_des"));
        request.addInputParam("@i_estado", ICTSTypes.SQLVARCHAR,"F");
        request.addInputParam("@i_clave_rastreo", ICTSTypes.SQLVARCHAR, bag.get("@i_clave_rastreo") != null ? bag.get("@i_clave_rastreo").toString() : "");
        request.addInputParam("@i_proceso_origen", ICTSTypes.SQLINT1, "1");
        request.addInputParam("@i_mensaje_acc", ICTSTypes.SQLVARCHAR,bag.get("@i_mensaje_acc") != null ? bag.get("@i_mensaje_acc").toString() : "");
        request.addInputParam("@i_codigo_acc", ICTSTypes.SQLVARCHAR, bag.get("@i_codigo_acc") != null ? bag.get("@i_codigo_acc").toString() : "");
        request.addInputParam("@i_transaccion_spei", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_transaccion_spei"));
        request.addInputParam("@i_spei_request", ICTSTypes.SQLVARCHAR, bag.get("@o_spei_request") != null ? bag.get("@o_spei_request").toString() : "");
        request.addInputParam("@i_spei_response", ICTSTypes.SQLVARCHAR, bag.get("@o_spei_response") != null ? bag.get("@o_spei_response").toString(): "");      
        request.addInputParam("@i_reference_number", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_reference_number"));

        // SE SETEA VARIABLE DE SALIDA
        request.addOutputParam("@o_salida", ICTSTypes.SYBVARCHAR, "0");

        // SE EJECUTA Y SE OBTIENE LA RESPUESTA
        IProcedureResponse pResponse = executeCoreBanking(request);

        logger.logInfo(wInfo + "end task ----> ");

        return pResponse;

    }
    
    protected List<String> speiData(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
        // SE INICIALIZA LA LISTA DE STRINGS
        List<String> fres = new ArrayList<String>();
        if (logger.isInfoEnabled()) {
            logger.logInfo("Entrando a speiData");
        }
        try {
            IProcedureRequest request = initProcedureRequest(anOriginalRequest.clone());

            // SE SETEAN DATOS
            request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
                    IMultiBackEndResolverService.TARGET_LOCAL);
            request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
            request.setSpName("cob_bvirtual..sp_registra_spei");
            request.addInputParam("@t_trn", ICTSTypes.SQLINTN, "18010");
            request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "E");
            request.addInputParam("@i_ente_bv", ICTSTypes.SYBINT4, anOriginalRequest.readValueParam("@s_cliente"));
            request.addInputParam("@i_cuenta_benef", ICTSTypes.SQLVARCHAR,anOriginalRequest.readValueParam("@i_cta_des"));
            request.addInputParam("@i_cuenta_ori", ICTSTypes.SQLVARCHAR,anOriginalRequest.readValueParam("@i_cta"));

            // SE SETEA VARIABLE DE SALIDA
            request.addOutputParam("@o_salida", ICTSTypes.SYBVARCHAR, "XXX");
            request.addOutputParam("@o_nom_ordenante", ICTSTypes.SYBVARCHAR, "XXX");
            request.addOutputParam("@o_curp_ordenante", ICTSTypes.SYBVARCHAR, "XXX");
            request.addOutputParam("@o_tipo_cuenta_ord", ICTSTypes.SYBVARCHAR, "XXX");
            request.addOutputParam("@o_cuenta_clabe", ICTSTypes.SYBVARCHAR, "XXX");

            request.addOutputParam("@o_benef_acc", ICTSTypes.SYBVARCHAR, "0");
            request.addOutputParam("@o_cuenta_acc", ICTSTypes.SYBVARCHAR, "0");

            // SE EJECUTA Y SE OBTIENE LA RESPUESTA
            IProcedureResponse pResponse = executeCoreBanking(request);

            // SE OBTIENE LA RESPUESTA
            fres.add(pResponse.readValueParam("@o_salida"));
            fres.add(pResponse.readValueParam("@o_nom_ordenante"));
            fres.add(pResponse.readValueParam("@o_curp_ordenante"));
            fres.add(pResponse.readValueParam("@o_tipo_cuenta_ord"));

            fres.add(pResponse.readValueParam("@o_benef_acc"));
            fres.add(pResponse.readValueParam("@o_cuenta_acc"));
            fres.add(pResponse.readValueParam("@o_cuenta_clabe"));

            logger.logInfo("Id Beneficiario Accendo "+pResponse.readValueParam("@o_benef_acc"));
            logger.logInfo("Id Tercero Ordenante Accendo "+pResponse.readValueParam("@o_cuenta_acc"));
            logger.logInfo("Cuenta clabe: "+pResponse.readValueParam("@o_cuenta_clabe"));

        } catch (Exception e) {
            logger.logInfo("Error de speiData");
            logger.logError(e);
            e.printStackTrace();
            logger.logInfo("Error de speiData");
        } finally {
            if (logger.isInfoEnabled()) {
                logger.logInfo("Saliendo de speiData");
            }
        }
        // SE REGRESA RESPUESTA
        return fres;
    }
    
    private IProcedureResponse findCardByPanConector(IProcedureRequest anOriginalReq, Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureResponse connectorAccountResponse = null;

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		aBagSPJavaOrchestration.remove("trn_virtual");
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en findCardByPanConector");
		}
		try {
			// PARAMETROS DE ENTRADA		
			anOriginalRequest.addInputParam("@i_destination_account_number", ICTSTypes.SQLVARCHAR, anOriginalReq.readValueParam("@i_destination_account_number"));
			anOriginalRequest.addInputParam("@i_operation", ICTSTypes.SQLVARCHAR, "FCP");

			anOriginalRequest.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
					"(service.identifier=TransferSpeiApiOrchestationCore)");
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

			if (connectorAccountResponse.readValueParam("@o_card_number") != null)
				aBagSPJavaOrchestration.put("@o_card_number", connectorAccountResponse.readValueParam("@o_card_number"));
			else
				aBagSPJavaOrchestration.put("@o_card_number", "null");
			
			if (connectorAccountResponse.readValueParam("@o_id_card") != null)
				aBagSPJavaOrchestration.put("@card_id_dock", connectorAccountResponse.readValueParam("@o_id_card"));
			else
				aBagSPJavaOrchestration.put("@card_id_dock", "null");


		} catch (Exception e) {
			e.printStackTrace();
			connectorAccountResponse = null;
			logger.logInfo(CLASS_NAME +" Error Catastrofico de findCardByPanConector");

		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + "--> findCardByPanConector");
			}
		}

		return connectorAccountResponse	;

	}
	private IProcedureResponse validateCardAccount(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration)
	{
		Integer opTcclaveBenAux  = Integer.parseInt(request.readValueParam("@i_destination_type_account"));
		String opTcClaveBen = String.format("%02d", opTcclaveBenAux);
		String destAccoutNumber = request.readValueParam("@i_destination_account_number");
		String codTarDeb = getParam(request, "CODTAR", "BVI");
		aBagSPJavaOrchestration.put("codTarDeb", codTarDeb);
		aBagSPJavaOrchestration.put("opTcClaveBen", opTcClaveBen);
	    Integer code = 0;
        String message = "success";
        String result = "true";
		
	    if(!validateAccountType(request, opTcClaveBen, aBagSPJavaOrchestration ))
		{
	    	code = 400602;
	    	message = "El tipo de destino no existe en el catalogo [bv_tipo_cuenta_spei].";
	    	result = "false";
		}else
		{ 
			logger.logDebug(" JC VALIDANDO SI ES TARJETA");
			
			logger.logDebug("tarjeta prueba 1 "+opTcClaveBen);
			
			if(opTcClaveBen.equals("03"))
			{
				
				logger.logDebug("tarjeta prueba 1 "+opTcClaveBen);

				
				IProcedureResponse respomse3=queryCardAccount(request,aBagSPJavaOrchestration);
				
				String tarjeta= respomse3.readValueParam("@o_card_crypt");
				
				logger.logDebug("tarjeta prueba 2 "+tarjeta);
				
	         	String tarjetaClaro = cryptaes.decryptData(tarjeta);   
	         	
	         	aBagSPJavaOrchestration.put("clear_card", tarjetaClaro);
	         	aBagSPJavaOrchestration.put("card_destination", "3");
				
				logger.logDebug("tarjeta prueba 3 "+tarjeta);
	         	
				if( !digitValidateNum(tarjetaClaro))
				{
					code = 34;
			    	message = "La cuenta del beneficiario solo puede ser numérica";
			    	result = "false";
				}else
					if(!(tarjetaClaro.length()==16))
					{
						code = 38;
				    	message = "Para tipo de cuenta Tarjeta de Debito la cuenta del beneficiario debe ser de 16 dígitos.";
				    	result = "false";
					}
			}
				
		}
	    //result 1
		IResultSetHeader headerRs0 = new ResultSetHeader();
		IResultSetData data0 = new ResultSetData();
		IResultSetRow row0 = new ResultSetRow();
		//result 2		
		IResultSetHeader headerRs1 = new ResultSetHeader();
		IResultSetData data1 = new ResultSetData();
		IResultSetRow row1 = new ResultSetRow();
	  		
  		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();

        row0.addRowData(1, new ResultSetRowColumnData(false, result));
		data0.addRow(row0);
		
        row1.addRowData(1, new ResultSetRowColumnData(false, String.valueOf(code)));
		row1.addRowData(2, new ResultSetRowColumnData(false,message));
		data1.addRow(row1);
		
		headerRs0.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));
		headerRs1.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		headerRs1.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));
		
		
		IResultSetBlock resultsetBlock0 = new ResultSetBlock(headerRs0, data0);
		IResultSetBlock resultsetBlock1 = new ResultSetBlock(headerRs1, data1);
		
		anProcedureResponse.addResponseBlock(resultsetBlock0);	
		anProcedureResponse.addResponseBlock(resultsetBlock1);	
		anProcedureResponse.setReturnCode(code);
		anProcedureResponse.addMessage(code, message);
		
		return anProcedureResponse;
	}
	
	private boolean validateAccountType(IProcedureRequest anOriginalRequest, String accountType, Map<String, Object> aBagSPJavaOrchestration) 
	{
		if (logger.isDebugEnabled()) 
		{
			logger.logDebug("Begin validateAccountType");
		}
		boolean validate = true ;
		IProcedureRequest reqTMPCentral = (initProcedureRequest(anOriginalRequest));		
		reqTMPCentral.setSpName("cob_bvirtual..sp_valida_tipo_destino");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID,  ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500163");
		reqTMPCentral.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500163");
		reqTMPCentral.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "V");
		reqTMPCentral.addInputParam("@i_tipo_destino", ICTSTypes.SQLVARCHAR, accountType);

	    IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);
		
		if (logger.isDebugEnabled()) 
		{
			logger.logDebug("Ending flow, validateAccountType with wProcedureResponseCentral: " + wProcedureResponseCentral.getProcedureResponseAsString());
		}
		
		if (wProcedureResponseCentral.hasError()) {
			validate = false;
		}else
		{
			if (wProcedureResponseCentral.getResultSetListSize() > 0) {
			
				IResultSetRow[] resultSetRows = wProcedureResponseCentral.getResultSet(1).getData().getRowsAsArray();
				
				if (resultSetRows.length > 0) {
					IResultSetRowColumnData[] columns = resultSetRows[0].getColumnsAsArray();
					aBagSPJavaOrchestration.put("tipoDestino", columns[0].getValue());
				} 
			} 
		}
		
		return validate;
	}
	
	public boolean digitValidateNum(String cadena) 
	{
	    Pattern patron = Pattern.compile("^\\d+$");
	    return patron.matcher(cadena).matches();
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
		procedureRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "S");
		procedureRequest.addInputParam("@i_uuid", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_destination_account_number")) ;
		
		procedureRequest.addOutputParam("@o_unique_id", ICTSTypes.SQLVARCHAR, "X");
		procedureRequest.addOutputParam("@o_card_id", ICTSTypes.SQLVARCHAR, "X");
		procedureRequest.addOutputParam("@o_card_crypt", ICTSTypes.SQLVARCHAR, "X");
	    
		IProcedureResponse wProcedureResponseLocal = executeCoreBanking(procedureRequest);
		
	    if (logger.isDebugEnabled()) 
		{
			logger.logDebug("Query card PAN :" + wProcedureResponseLocal.getProcedureResponseAsString());
		}
	    return wProcedureResponseLocal;
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
		
		procedureRequest.addInputParam("@i_customerDetails_externalCustomerId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_external_customer_id"));
		procedureRequest.addInputParam("@i_operation", ICTSTypes.SQLVARCHAR, "SPEI_DEBIT");
		
		procedureRequest.addInputParam("@i_channelDetails_channel", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_channel").toString());//se obtiene con el response del f1
		
		procedureRequest.addInputParam("@i_channelDetails_userAgent", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_userAgent").toString());//se obtiene con el response del f1
		procedureRequest.addInputParam("@i_channelDetails_userSessionDetails_userSessionId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_userSessionId"));//se obtiene del session id de cashi web
		procedureRequest.addInputParam("@i_channelDetails_userSessionDetails_riskEvaluationId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_riskEvaluationId"));//se obtiene del metodo f5
		procedureRequest.addInputParam("@i_channelDetails_userSessionDetails_authenticationMethod", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_authenticationMethod"));//preguntar, en la doc dice los posibles valores
		procedureRequest.addInputParam("@i_channelDetails_userSessionDetails_location_latitude", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_latitude"));
		procedureRequest.addInputParam("@i_channelDetails_userSessionDetails_location_longitude", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_longitude"));
		procedureRequest.addInputParam("@i_channelDetails_userSessionDetails_location_accuracy", ICTSTypes.SQLINT4, aRequest.readValueParam("@i_accuracy"));//no se de donde sale este valor
		procedureRequest.addInputParam("@i_channelDetails_userSessionDetails_location_capturedTime", ICTSTypes.SQLVARCHAR,aRequest.readValueParam("@i_capturedTime"));//no se de donde sale este valor
		procedureRequest.addInputParam("@i_channelDetails_userSessionDetails_ipAddress", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_end_user_ip"));//signIp del response del f1
		procedureRequest.addInputParam("@i_transaction_transactionId", ICTSTypes.SQLVARCHAR, aRequest.readValueFieldInHeader("ssn"));//movement id
        String transactionDate = unifyDateFormat(aRequest.readValueParam("@i_capturedTime"));
		procedureRequest.addInputParam("@i_transaction_transactionDate", ICTSTypes.SQLVARCHAR, transactionDate);
		procedureRequest.addInputParam("@i_transaction_transaction_currency", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_currency"));
		procedureRequest.addInputParam("@i_transaction_transaction_amount", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_amount"));
		
		if (aBagSPJavaOrchestration.get("card_id_dock") != null) {
			procedureRequest.addInputParam("@i_creditorAccount_identification", ICTSTypes.SQLVARCHAR, (String)aBagSPJavaOrchestration.get("card_id_dock"));
			procedureRequest.addInputParam("@i_creditorAccount_identificationType", ICTSTypes.SQLVARCHAR, "CARD_ID");
		} else {
			procedureRequest.addInputParam("@i_creditorAccount_identification", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_destination_account_number"));
			int lengthCtades = aRequest.readValueParam("@i_destination_account_number").length();
			String identificationType = null;
			
			if (lengthCtades == 18) {
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
        String newDate = dateString;

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

        if (date != null) {
        	newDate = unifiedFormat.format(date);
        }
        
        return newDate;
    }

    private void obtainLimits(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration){
		try{
			JsonParser jsonParser = new JsonParser();
			String jsonRequestStringClean = aBagSPJavaOrchestration.get("responseBodyGetLimits").toString().replace("&quot;", "\"");
			JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonRequestStringClean);
			JsonArray transactionLimits = jsonObject.getAsJsonArray("transactionLimits");
			double transactionAmount =  Double.parseDouble(aRequest.readValueParam("@i_amount"));// Monto de la transacción
	
			// Inicializar variables para límites
			Double dailyLimit = null;
			Double montlyLimit = null;
			Double balanceAmountMontly = null;
			Double maxTxnLimit = null;

			for (JsonElement limitElement : transactionLimits) {
				JsonArray subTypeLimits = limitElement.getAsJsonObject().getAsJsonArray("transactionSubTypeLimits");
				for (JsonElement subTypeElement : subTypeLimits) {
					String limitType = subTypeElement.getAsJsonObject().get("transactionLimitsType").getAsString();
					
					if ("DAILY".equals(limitType)) {
						if (subTypeElement.getAsJsonObject().has("userConfiguredLimit")) {
							dailyLimit = subTypeElement.getAsJsonObject()
								.getAsJsonObject("userConfiguredLimit")
								.get("amount").getAsDouble();
							
							boolean isDailyLimitExceeded = transactionAmount > (dailyLimit != null ? dailyLimit : 0);
							aBagSPJavaOrchestration.put("isDailyLimitExceeded", isDailyLimitExceeded);
						}
					}else if("MONTHLY".equals(limitType)){
						if (subTypeElement.getAsJsonObject().has("userConfiguredLimit")) {
							montlyLimit = subTypeElement.getAsJsonObject()
								.getAsJsonObject("userConfiguredLimit")
								.get("amount").getAsDouble();
						}
						if (subTypeElement.getAsJsonObject().has("balanceAmount")) {
							balanceAmountMontly = subTypeElement.getAsJsonObject()
								.getAsJsonObject("balanceAmount")
								.get("amount").getAsDouble();
						}
					} else if ("MAX_TXN_LIMIT".equals(limitType)) {
						if (subTypeElement.getAsJsonObject().has("userConfiguredLimit")) {
							maxTxnLimit = subTypeElement.getAsJsonObject()
								.getAsJsonObject("userConfiguredLimit")
								.get("amount").getAsDouble();

							boolean isMaxTxnLimitExceeded = transactionAmount > (maxTxnLimit != null ? maxTxnLimit : 0);
							aBagSPJavaOrchestration.put("isMaxTxnLimitExceeded", isMaxTxnLimitExceeded);
						}
					}
				}
			}
		
			boolean isMontlyLimitExceeded = transactionAmount + balanceAmountMontly > (montlyLimit != null ? montlyLimit : 0);

			aBagSPJavaOrchestration.put("isMontlyLimitExceeded", isMontlyLimitExceeded);
	
			if (logger.isDebugEnabled()) {
				logger.logDebug("dailyLimit:: " + dailyLimit);
				logger.logDebug("dailyLimit:: " + dailyLimit);
				logger.logDebug("maxTxnLimit:: " + maxTxnLimit);
				logger.logDebug("isMontlyLimitExceeded:: " + isMontlyLimitExceeded);
			}
		} catch (JsonSyntaxException e) {
			logger.logError("Error parsing JSON: Invalid JSON syntax", e);
		} catch (IllegalStateException e) {
			logger.logError("Error parsing JSON: Illegal state", e);
		} catch (Exception e) {
			logger.logError("Unexpected error while parsing JSON", e);
		}
	}
 
    
	private void callGetLimits(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration){
		if(logger.isDebugEnabled())
			logger.logDebug("Spei callGetLimitsConn [INI]");

		try {

			IProcedureRequest anOriginalRequestLimits = new ProcedureRequestAS();

			anOriginalRequestLimits.addInputParam("@i_transactionType", ICTSTypes.SQLVARCHAR, "DEBIT");
			anOriginalRequestLimits.addInputParam("@i_transactionSubType", ICTSTypes.SQLVARCHAR, "SPEI_DEBIT");
			anOriginalRequestLimits.addInputParam("@i_externalCustomerId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_external_customer_id"));

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
	
			IProcedureResponse connectorGetLimitsResponse = executeProvider(anOriginalRequestLimits, aBagSPJavaOrchestration);

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

		}catch (Exception e) {
			logger.logError(" Error en callGetLimitsConn: " + e.getMessage());
		}
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
		request.addInputParam("@i_servicio", ICTSTypes.SQLVARCHAR, "fetchTransactionLimit");
        logger.logInfo("SSNNN:: " + aRequest.readValueParam("@s_ssn"));
		request.addInputParam("@i_ssn", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@s_ssn"));
        
		
		request.addInputParam("@i_request", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("queryString").toString());
		request.addInputParam("@i_response", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("responseBodyGetLimits").toString());
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);		
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response registerResponse get: " + wProductsQueryResp.getProcedureResponseAsString());
		}
	}

}