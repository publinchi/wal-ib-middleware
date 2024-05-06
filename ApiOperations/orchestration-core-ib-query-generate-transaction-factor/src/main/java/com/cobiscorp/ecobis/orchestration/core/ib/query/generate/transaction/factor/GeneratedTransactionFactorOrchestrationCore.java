package com.cobiscorp.ecobis.orchestration.core.ib.query.generate.transaction.factor;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.CISResponseManagmentHelper;
import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
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
import com.cobiscorp.ecobis.admintoken.dto.DataTokenRequest;
import com.cobiscorp.ecobis.admintoken.dto.DataTokenResponse;
import com.cobiscorp.ecobis.admintoken.interfaces.IAdminTokenUser;
import com.cobiscorp.mobile.model.Message;
import com.cobiscorp.mobile.model.TransactionFactorResponse;
import com.cobiscorp.cobisv.commons.exceptions.BusinessException;

import Utils.ConstantsMessageResponse;

/**
 * Generated Transaction Factor
 * 
 * @since Jan 10, 2023
 * @author dcollaguazo
 * @version 1.0.0
 * 
 */
@Component(name = "GeneratedTransactionFactorOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "GeneratedTransactionFactorOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "GeneratedTransactionFactorOrchestrationCore") })
public class GeneratedTransactionFactorOrchestrationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(GeneratedTransactionFactorOrchestrationCore.class);
	private static final String CLASS_NAME = "GeneratedTransactionFactorOrchestrationCore--->";

	CISResponseManagmentHelper cisResponseHelper = new CISResponseManagmentHelper();

	protected static final int CHANNEL_REQUEST = 8;

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
	public void loadConfiguration(IConfigurationReader arg0) {
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
		TransactionFactorResponse responseOtp = new TransactionFactorResponse();
		Map<String, String> login = new HashMap<String, String>();
		Message message = new Message();
		
		String ente = anOriginalRequest.readValueParam("@i_external_customer_id");
		String cardId = anOriginalRequest.readValueParam("@i_card_id");
		String codigoOtp = anOriginalRequest.readValueParam("@i_otp");

		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		aBagSPJavaOrchestration.put("ente", ente);
		aBagSPJavaOrchestration.put("otp", codigoOtp);
		
		logger.logInfo("Ente es: "+ente);
		logger.logInfo("Card Id es: "+cardId);
		
		if (ente.equals("null") && cardId.equals("null")) {
			
			message.setCode(String.valueOf(ConstantsMessageResponse.MSG40024.getIdMessage()));
			message.setMessage(ConstantsMessageResponse.MSG40024.getDescriptionMessage());
			
			responseOtp.setSuccess(false);
			responseOtp.setMessage(message);
			
			return processResponseOtp(responseOtp);
			
		} else if (!ente.equals("null") && !cardId.equals("null")) {
			
			message.setCode(String.valueOf(ConstantsMessageResponse.MSG40022.getIdMessage()));
			message.setMessage(ConstantsMessageResponse.MSG40022.getDescriptionMessage());
			
			responseOtp.setSuccess(false);
			responseOtp.setMessage(message);
			
			return processResponseOtp(responseOtp);
			
		} else if (codigoOtp.length() != 4 && cardId.equals("null")) {
			
			message.setCode(String.valueOf(ConstantsMessageResponse.MSG40023.getIdMessage()));
			message.setMessage(ConstantsMessageResponse.MSG40023.getDescriptionMessage());
			
			responseOtp.setSuccess(false);
			responseOtp.setMessage(message);
			
			return processResponseOtp(responseOtp);
		}

		// Obtener el login del ente
		login = getLoginById(anOriginalRequest);
		
		logger.logInfo("Código OTP: "+codigoOtp);
		
		if (login.containsKey("o_login")) {

			if (!login.containsKey("o_num_phone") || !login.containsKey("o_mail")) {
				
				message.setCode(String.valueOf(ConstantsMessageResponse.MSG40021.getIdMessage()));
				message.setMessage(ConstantsMessageResponse.MSG40021.getDescriptionMessage());
				
				responseOtp.setSuccess(false);
				responseOtp.setMessage(message);
				
				return processResponseOtp(responseOtp);
			}
			
			DataTokenRequest tokenRequest = new DataTokenRequest();
			
			tokenRequest.setLogin(login.get("o_login"));
			tokenRequest.setClientId(Integer.parseInt(login.get("o_ente")));
			tokenRequest.setChannel(8);
			tokenRequest.setToken(codigoOtp);
			
			if(cardId.equals("null")) {
				
				logger.logInfo("GENERA OTP...");
				
				generateTransactionFactor(tokenRequest);
				
			} else {
			
				logger.logInfo("ENVÍA NOTIFICACIÓN...");
				
				notifyTokenUser(tokenRequest);
			
			}
		
			message.setCode(String.valueOf(ConstantsMessageResponse.MSG000.getIdMessage()));
			message.setMessage(ConstantsMessageResponse.MSG000.getDescriptionMessage());
			
			responseOtp.setSuccess(true);
			responseOtp.setMessage(message);

			return processResponseOtp(responseOtp);
			
		} else {
			
			message.setCode(String.valueOf(ConstantsMessageResponse.MSG40020.getIdMessage()));
			message.setMessage(ConstantsMessageResponse.MSG40020.getDescriptionMessage());
			
			responseOtp.setSuccess(false);
			responseOtp.setMessage(message);
		
			return processResponseOtp(responseOtp);
		}

	}

	protected Map<String, String> getLoginById(IProcedureRequest aRequest) {

		IProcedureRequest request = new ProcedureRequestAS();
		String login, numPhone, mail, ente = null;
		Map<String, String> responseLogin = new HashMap<String, String>();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getLoginById");
		}

		request.setSpName("cob_bvirtual..sp_cons_ente_med_envio");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		if (!aRequest.readValueParam("@i_card_id").equals("null")){
			
			request.addInputParam("@i_card_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_card_id"));
			
		} else {
		
			request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_external_customer_id"));
		}
		request.addInputParam("@i_card_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_card_id"));
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_external_customer_id"));
		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "S");
		request.addInputParam("@i_servicio", ICTSTypes.SQLINTN, "8");
		
		request.addOutputParam("@o_login", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_mail_ente", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_num_phone", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_ente", ICTSTypes.SQLVARCHAR, "X");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getLoginById");
		}

		logger.logDebug("readValueParam @o_login: " + wProductsQueryResp.readValueParam("@o_login"));
		
		login = wProductsQueryResp.readValueParam("@o_login");
		numPhone = wProductsQueryResp.readValueParam("@o_num_phone");
		mail = wProductsQueryResp.readValueParam("@o_mail_ente");
		ente = wProductsQueryResp.readValueParam("@o_ente");

		if (!login.equals("X")) {
			responseLogin.put("o_login", login);
		}
		
		if (!numPhone.equals("X")) {
			responseLogin.put("o_num_phone", numPhone);
		}
		
		if (!mail.equals("X")) {
			responseLogin.put("o_mail", mail);
		}
		
		if (!ente.equals("X")) {
			responseLogin.put("o_ente", ente);
		}
		
		return responseLogin;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {

		return null;
	}

	public TransactionFactorResponse generateTransactionFactor(DataTokenRequest tokenRequest) {

		if (logger.isDebugEnabled()) {
			logger.logDebug("API generateTransactionFactor INICIA: ");
		}

		//tokenRequest.setChannel(8);
		if (logger.isDebugEnabled()) {
			logger.logDebug("Ejecución de generateTokenUser INICIA: ");
		}
		
		TransactionFactorResponse response = new TransactionFactorResponse();
		DataTokenResponse tokenResponse = this.tokenService.generateTokenUser(tokenRequest);
		
		response.setSuccess(tokenResponse.getSuccess());
		
		if (!tokenResponse.getSuccess()) {
			
			Message message = new Message();
			
			message.setCode(tokenResponse.getMessage().getCode());
			message.setMessage(tokenResponse.getMessage().getDescription());
			
			response.setMessage(message);
		}
		
		return response;
	}

	public IProcedureResponse processResponseOtp(TransactionFactorResponse responseOtp) {

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		// Agregar Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("SUCCES", ICTSTypes.SYBVARCHAR, 64));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("CODE", ICTSTypes.SYBINT2, 2));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("MESSAGE", ICTSTypes.SYBVARCHAR, 64));

		IResultSetRow row = new ResultSetRow();

		row.addRowData(1, new ResultSetRowColumnData(false, responseOtp.getSuccess().toString()));
		row.addRowData(2, new ResultSetRowColumnData(false, responseOtp.getMessage().getCode()));
		row.addRowData(3, new ResultSetRowColumnData(false, responseOtp.getMessage().getMessage()));
		
		data.addRow(row);

		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

		wProcedureResponse.addResponseBlock(resultBlock);

		wProcedureResponse.setReturnCode(Integer.parseInt(responseOtp.getMessage().getCode()));
		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;
	}

	private void notifyTokenUser(DataTokenRequest dataIn) {
		
		try{
		
			if (logger.isDebugEnabled()) {
				logger.logDebug("Empieza notifyTokenUser Method");
			}

			IProcedureRequest wProcedureRequest = new ProcedureRequestAS();
			
			wProcedureRequest.setSpName("cob_bvirtual..sp_se_generar_notif");
			wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, "local");

			wProcedureRequest.addInputParam("@i_banco", ICTSTypes.SQLVARCHAR, dataIn.getBankName());
			wProcedureRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "1875901");
			wProcedureRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "N"); //puede ser Q consulta, N notifica
			wProcedureRequest.addInputParam("@i_canal", ICTSTypes.SQLINT4, "8");
			wProcedureRequest.addInputParam("@i_correo_orig", ICTSTypes.SQLVARCHAR, dataIn.getOriginMail());
			wProcedureRequest.addInputParam("@i_correo_dest", ICTSTypes.SQLVARCHAR, dataIn.getDestinationMail());
			wProcedureRequest.addInputParam("@i_token", ICTSTypes.SQLVARCHAR, dataIn.getToken());
			wProcedureRequest.addInputParam("@i_cliente", ICTSTypes.SQLINT4, (null != dataIn.getClientId() && !"".equals(dataIn.getClientId().toString()) ? dataIn.getClientId().toString() : "0"));
			wProcedureRequest.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, dataIn.getLogin());
			wProcedureRequest.addInputParam("@i_bandera_sms", ICTSTypes.SQLVARCHAR, dataIn.getSmsFlag());
			wProcedureRequest.addInputParam("@i_num_telf", ICTSTypes.SQLVARCHAR, dataIn.getClientPhoneNumber());

			IProcedureResponse wProcedureResponseCentral = executeCoreBanking(wProcedureRequest);

			logger.logDebug("<<<<<<<<<< IProcedureResponse notifyTokenUser >>>>>>>>>> " + wProcedureResponseCentral);

		} catch (Exception e) {
			
			logger.logError("notifyTokenUser.002:" + e.getMessage());
			
			throw new BusinessException(1887674, "An error occurred sending token");
			
		} finally {
			
			logger.logDebug("notifyTokenUser.002" + "notifyTokenUser");
		}
	}

}
