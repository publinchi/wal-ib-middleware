package com.cobiscorp.ecobis.orchestration.core.ib.authenticate.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import cobiscorp.ecobis.commons.dto.MessageTO;
import cobiscorp.ecobis.commons.dto.ServiceRequestTO;
import cobiscorp.ecobis.commons.dto.ServiceResponseTO;
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionContextCIB;
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.UserContext;

import com.cobiscorp.cobis.cache.ICache;
import com.cobiscorp.cobis.cache.ICacheManager;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.converters.ByteConverter;
import com.cobiscorp.cobis.commons.db.IDBServiceFactory;
import com.cobiscorp.cobis.commons.db.IDBServiceProvider;
import com.cobiscorp.cobis.commons.exceptions.COBISInfrastructureRuntimeException;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.crypt.CryptorDecryptorForRC4;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ServiceRequest;
//import com.cobiscorp.cobis.cts.service.executor.external.authenticator.utils.Decryptor;
import com.cobiscorp.cobis.cts.services.execution.IExecutorServiceFactory;
import com.cobiscorp.cobis.cts.services.execution.ISPExecutorService;
import com.cobiscorp.cobis.cts.services.session.ISessionManager;
import com.cobiscorp.ecobis.ib.authenticate.interfaces.IAuthenticateImplService;

/**
 * @author dmorla
 * 
 */

@Component(name = "AuthenticateImpl")
@Service(IAuthenticateImplService.class)
@Properties(value = { @Property(name = "service.description", value = "AuthenticateImpl"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "AuthenticateImpl") })
public class AuthenticateImpl implements IAuthenticateImplService {

	// ESA: query para obtener informacion de autenticacion externa del usuario
	private static final String QUERY_GET_CURRENT_AUTH_TYPE = "SELECT la_trx_auth_type,la_trx_state,la_trx_retry,la_trx_serial_number FROM cob_bvirtual{0}bv_login_authentication WHERE la_login =?";
	// ESA: query para recuperar informacion de autenticacion externa para una
	// transaccion
	private static final String QUERY_HAS_TRANSACTION_DOUBLE_AUTH = "SELECT tr_autenticacion FROM cob_bvirtual{0}bv_transaccion WHERE tr_transaccion=?";
	// ESA: query para actualizar el valor de la_trx_retry
	private static final String QUERY_UPDATE_AUTH_TRANSACTION_RETRY = "update cob_bvirtual{0}bv_login_authentication SET la_trx_retry=? WHERE la_login=?";

	// ESA: query para actualizar el valor de la_trx_state
	private static final String QUERY_UPDATE_AUTH_STATUS_RETRY = "update cob_bvirtual{0}bv_login_authentication SET la_trx_state=? WHERE la_login=?";

	private static final String QUERY_LOG_AUTH_STATUS = "insert into cob_bvirtual{0}bv_authentication_log values (?,?,?,?,?,?,?,?,?)";

	// private static final String CLASS_NAME = "[AuthenticateImpl]";
	private static final ILogger logger = LogFactory.getLogger(AuthenticateImpl.class);

	private static boolean isLoaded = true;

	@Reference(referenceInterface = IDBServiceFactory.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindDbServiceFactory", unbind = "unbindDbServiceFactory")
	private IDBServiceFactory dbServiceFactory;

	@Reference(referenceInterface = IExecutorServiceFactory.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "setExecutorServiceFactory", unbind = "unsetExecutorServiceFactory")
	private IExecutorServiceFactory spExecutorFactory;

	/**
	 * Variable para recuperar datos de la session
	 */
	@Reference(referenceInterface = ISessionManager.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindSessionManager", unbind = "unbindSessionManager")
	private ISessionManager sessionManager;

	protected void bindSessionManager(ISessionManager service) {
		if (logger.isDebugEnabled())
			logger.logDebug("binding ISessionManager: " + service);
		sessionManager = service;
	}

	protected void unbindSessionManager(ISessionManager service) {
		if (logger.isDebugEnabled())
			logger.logDebug("unbinding ISessionManager: " + service);
		sessionManager = null;
	}

	public void setExecutorServiceFactory(IExecutorServiceFactory spExecutorFactory) {
		if (logger.isInfoEnabled())
			logger.logInfo("Adding service[" + spExecutorFactory + "]");
		this.spExecutorFactory = spExecutorFactory;
	}

	public void unsetExecutorServiceFactory(IExecutorServiceFactory spExecutorFactory) {
		this.spExecutorFactory = null;
	}

	/** Cache Manager */
	@Reference(bind = "setCacheManager", unbind = "unsetCacheManager")
	private ICacheManager cacheManager;

	/**
	 * Servicio para obtener el nombre del backend local
	 */
	@Reference(bind = "bindMultiBackEndResolverService", unbind = "unbindMultiBackEndResolverService")
	private IMultiBackEndResolverService multibackendService;

	private String dbmsName;
	/**
	 * Tipo del proveedor de bdd
	 */
	private static final String DBMS_PROVIDER_TYPE = "DataSource";

	private String dbmsSeparator;
	private IDBServiceProvider dbServiceProvider;

	/**
	 * Variable que almacena el numero maximo de intentos que soportara el
	 * servicio
	 **/
	private int maxAuthRetries = 3;

	protected void bindDbServiceFactory(IDBServiceFactory service) {
		dbServiceFactory = service;
	}

	protected void unbindDbServiceFactory(IDBServiceFactory service) {
		dbServiceFactory = null;
	}

	public void setCacheManager(ICacheManager cacheManager) {
		if (logger.isInfoEnabled())
			logger.logInfo("binding productService: " + cacheManager);

		this.cacheManager = cacheManager;
	}

	public void unsetCacheManager(ICacheManager cacheManager) {
		if (logger.isInfoEnabled())
			logger.logInfo("unbinding productService: " + this.cacheManager);
		this.cacheManager = null;
	}

	private String getDbmsSeparator() {
		if (dbmsSeparator == null) {
			dbmsSeparator = dbServiceFactory.getSeparator(dbmsName);
		}
		return dbmsSeparator;
	}

	private IDBServiceProvider getDbServiceProvider() {
		if (dbServiceProvider == null) {
			dbServiceProvider = dbServiceFactory.getDBServiceProvider(getDbmsName(), DBMS_PROVIDER_TYPE);
		}
		return dbServiceProvider;
	}

	public ServiceResponseTO getCurrentAuthType(ServiceRequestTO request) {
		// se arma un objeto ServiceResponseTO con el tipo de autenticacion
		// en base al valor de userId del header del ServiceRequestTO
		String user = getUserIdAsString(request);

		String currentAuthStatusRetry = getCurrentAuthType(user);

		if (currentAuthStatusRetry != null) {
			String[] resp = currentAuthStatusRetry.split(",");
			ServiceResponseTO response = new ServiceResponseTO();
			response.setSuccess(true);
			response.addValue(CURR_TRX_AUTH_TYPE, resp[0]);
			// response.addValue(CURR_TRX_AUTH_STATUS, resp[1]);
			// response.addValue(CURR_TRX_AUTH_RETRY, resp[2]);
			return response;
		} else {
			return getResponseOnFailure("1875213", "Usuario: " + user
					+ "  Sin licencia de doble autenticacion para transacciones. Comuniquese con el Banco.");

		}

	}

	public boolean hasTransactionDoubleAuthentication(ServiceRequestTO request) {
		boolean response = false;

		// Tomo el valor del trn, del DTO inTransactionContextCIB
		Map<String, Object> challengeParamsMap = getChallengeParams(request);
		int transactionId = 0;
		if (challengeParamsMap.get("transactionId") != null) {
			transactionId = (Integer) challengeParamsMap.get("transactionId");
		}
		if (transactionId != 0) {
			return hasTransactionDoubleAuth(transactionId);
		}

		return response;
	}

	public IProcedureResponse validateChallenge(String vascoUser, String authType, String challenge) {
		if (!isLoaded) {
			logger.logError("Configurations have not been loaded correctly.....");
			throw new COBISInfrastructureRuntimeException("Configurations have not been loaded correctly.....");
		}
		// challenge = "1A|2B|5C|-" + challenge;
		String[] splChallenge = challenge.split("-");

		IProcedureRequest procedureReq = new ProcedureRequestAS();
		procedureReq.setSpName("cob_bvirtual..sp_valida_trn_aut");

		procedureReq.addInputParam("@i_login", 39, vascoUser);
		procedureReq.addInputParam("@i_tipo", 39, authType);
		procedureReq.addInputParam("@i_code", 39, splChallenge[0]);
		procedureReq.addInputParam("@i_value", 39, splChallenge[1]);
		procedureReq.addOutputParam("@o_error", ICTSTypes.SQLINT4, "0");
		procedureReq.addOutputParam("@o_mensaje", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXX");

		ISPExecutorService spExecutorService = this.spExecutorFactory.getSPExecutor();
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Request:*** " + procedureReq.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Start Request *** ");
		}
		IProcedureResponse response = null;
		try {
			response = spExecutorService.execute(procedureReq, getDbmsName());
		} catch (CTSServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response: *** " + response.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response *** ");
		}
		return response;

	}

	public ServiceResponseTO hasExternalAuthenticator(ServiceRequestTO request) throws CTSInfrastructureException {

		IProcedureRequest procedureReq = new ProcedureRequestAS();
		// desencripto el sessionId
		String sessionIdTemp = request.getSessionId().substring(3);
		String sessionId2 = new String(ByteConverter.tranformFromHex(sessionIdTemp));
		CryptorDecryptorForRC4 cryptor = CryptorDecryptorForRC4.getInstance();
		sessionId2 = "ID:" + cryptor.decriptWithRC4(sessionId2);
		if (logger.isDebugEnabled()) {
			logger.logDebug("session Id was : " + sessionId2);
		}
		procedureReq.addFieldInHeader(ICOBISTS.HEADER_SESSION_ID, ICOBISTS.HEADER_STRING_TYPE, sessionId2);
		ServiceRequest serviceRequest = new ServiceRequest();
		serviceRequest.setServiceRequestTO(request);
		// poner los parametros de sesion del usuario en el serviceRequest
		try {
			sessionManager.setSessionParameters(procedureReq);
		} catch (CTSServiceException e) {
			throw new CTSInfrastructureException("Error: " + e.getClientErrorCode() + ", " + e.getMessage());
		} catch (CTSInfrastructureException e) {
			throw new CTSInfrastructureException("Error: " + e.getClientErrorCode() + ", " + e.getMessage());
		}
		if (logger.isDebugEnabled()) {
			logger.logDebug("procedureRequest: " + procedureReq.getCTSMessageAsString());
		}
		// A�ado los datos del usuario
		request.addValue(ICOBISTS.HEADER_LOGIN, procedureReq.readFieldInHeader(ICOBISTS.HEADER_LOGIN).getValue());

		ServiceResponseTO response = new ServiceResponseTO();
		Boolean result = false;
		if (!request.getServiceId().equalsIgnoreCase("CtsService.vasco.validateChallenge")) {
			boolean hasTrxExternal = hasTransactionDoubleAuthentication(request);
			if (logger.isDebugEnabled()) {
				logger.logDebug("service CTSService.hasExternalAuthtentication for trn is: " + hasTrxExternal);
			}
			if (hasTrxExternal) {
				// validar si el usuario tiene licencia para 2ble autenticacion
				response = getCurrentAuthType(request);
				String authType = (String) response.getValue(IAuthenticateImplService.CURR_TRX_AUTH_TYPE);
				if (authType != null && !authType.equalsIgnoreCase("NONE")) {
					result = true;
				}
			}

		}
		response.addValue(IAuthenticateImplService.RESPONSE, result);
		response.setSuccess(true);
		return response;
	}

	public ServiceResponseTO validateChallenge(ServiceRequestTO request) {
		ServiceResponseTO response = new ServiceResponseTO();
		String User = "";
		// Contador de reintentos
		int retryCounter = 0;

		User = getUserIdFromRequest(request);
		if (User == null) {
			User = getUserIdAsString(request);
		}
		if (User == null) {
			return getResponseOnFailure(" ", "Usuario no registrado.");
		}

		// valores de status, tipo y reintentos de autenticacion
		String[] respCurrAuthStatusRetry = null;
		// recupero el tipo de autenticacion y el estado
		String userInfo = getCurrentAuthType(User);
		if (userInfo == null) {
			return getResponseOnFailure("1875213", "Usuario: " + User
					+ "  Sin licencia de doble autenticacion para transacciones. Comuniquese con el Banco.");

		}

		respCurrAuthStatusRetry = userInfo.split(",");
		if (!respCurrAuthStatusRetry[0].equalsIgnoreCase("NONE") && respCurrAuthStatusRetry[1].equalsIgnoreCase("A")) {

			String retryNumString = respCurrAuthStatusRetry[2];
			if (retryNumString != null) {
				retryCounter = Integer.parseInt(retryNumString);
			}
			if (logger.isDebugEnabled()) {
				logger.logDebug("validating challenge for user: " + User + ", serviceId: " + request.getServiceId()
						+ ", authType: " + respCurrAuthStatusRetry[0] + ",retryNum: " + retryCounter);
			}

			// validar si se ha llegado al numero maximo de intentos
			if (validateRetryCounter(retryCounter)) {
				// cambiar el estado de la cuenta de autenticacion a
				// bloqueado
				updateBVUser(User, false);
				response = getResponseOnFailure("99999",
						"Se ha llegado al numero maximo de intentos, cuenta bloqueada. Comuniquese con el Banco.");
				return response;
			}

			Map<String, Object> challengeParams = getChallengeParams(request);

			// String deviceId = respCurrAuthStatusRetry[3];
			String challenge = (String) challengeParams.get("challenge");

			IProcedureResponse pResponse = validateChallenge(User, respCurrAuthStatusRetry[0], challenge);
			// validar el retorno del codigo de error o capturar la
			// excepcion de la ejecucion del SP
			int returnCode = Integer.parseInt(pResponse.readValueParam("@o_error"));
			if (returnCode == 0) {
				retryCounter = 0;
				updateAuthUserInfo(User, retryCounter);
				response = getResponseOnSuccessVoid();
			} else {
				updateAuthUserInfo(User, ++retryCounter);
				if (logger.isDebugEnabled()) {
					logger.logError(
							"An error occurred validating challenge: -->" + pResponse.readValueParam("@o_mensaje"));
				}
				response = getResponseOnFailure("" + returnCode, pResponse.readValueParam("@o_mensaje"));
			}
			response.addValue(IAuthenticateImplService.CURR_TRX_AUTH_TYPE, respCurrAuthStatusRetry[0]);
			response.addValue(IAuthenticateImplService.CURR_TRX_AUTH_RETRY, retryCounter);
			// ESA: 17 05 2013 Se vuelve a colocar el valor de login para
			// registrar las autenticaciones en bdd.
			request.addValue(ICOBISTS.HEADER_LOGIN, User);
			logTransactionStatus(request, response);
			return response;

		} else if (respCurrAuthStatusRetry[0].equalsIgnoreCase("NONE")) {
			// si no tiene ninguna doble autenticacion
			return getResponseOnSuccessVoid();
		} else {
			return getResponseOnFailure("1875214",
					"Licencia de doble autenticacion bloqueada. Comuniquese con el Banco.");
		}

	}

	public String getCurrentAuthType(String userId) {

		if (logger.isDebugEnabled()) {
			logger.logDebug("Retrieving authentication information for user: " + userId);
		}

		String resp = null;
		Connection connection = getDbServiceProvider().getDBConnection();

		try {
			PreparedStatement statement = connection
					.prepareStatement(MessageFormat.format(QUERY_GET_CURRENT_AUTH_TYPE, getDbmsSeparator()));
			statement.setString(1, userId);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				// tomo el valor del auth_type, el estado del usuario el
				// num de reintentos y el id del dispositivo.
				resp = resultSet.getString(1) + "," + resultSet.getString(2) + "," + resultSet.getString(3) + ","
						+ resultSet.getString(4);
			}
		} catch (Exception e) {
			throw new COBISInfrastructureRuntimeException("An error occurred retrieving current auth information.", e);
		} finally {
			if (connection != null) {
				getDbServiceProvider().closeDBConnection(connection);
			}
		}

		return resp;
	}

	public boolean hasTransactionDoubleAuth(int trn) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Verifying double authentication for transaction: " + trn);
		}
		Connection connection = getDbServiceProvider().getDBConnection();
		ICache cache = null;
		String resp = "";
		if (cacheManager != null) {
			cache = cacheManager.getCache("CTSBVAuthenticateTrnContext");
		}
		if (cache != null) {
			resp = (String) cache.get(trn);
			if (logger.isDebugEnabled() && cache.get(trn) != null) {
				logger.logDebug("Getting transacction information from cache for trn: " + trn);
			}
			if (resp == null) {
				try {
					PreparedStatement statement = connection.prepareStatement(
							MessageFormat.format(QUERY_HAS_TRANSACTION_DOUBLE_AUTH, getDbmsSeparator()));
					statement.setInt(1, trn);
					ResultSet resultSet = statement.executeQuery();
					if (resultSet.next()) {
						resp = resultSet.getString(1);
					}

					if (resp == null) {
						resp = "N";
					}
					cache.put(trn, resp);
					if (logger.isDebugEnabled()) {
						logger.logDebug("Setting information in cache for transaction: " + trn);
					}
				} catch (Exception e) {
					throw new COBISInfrastructureRuntimeException(
							"An error occurred retrieving double auth for transaction.", e);
				} finally {
					if (connection != null) {
						getDbServiceProvider().closeDBConnection(connection);
					}
				}

			}
		} else {

			if (logger.isDebugEnabled()) {
				logger.logWarning("Working without cache Manager [CTSBVAuthenticateTrnContext] for virtual Banking.");
			}
			try {
				PreparedStatement statement = connection
						.prepareStatement(MessageFormat.format(QUERY_HAS_TRANSACTION_DOUBLE_AUTH, getDbmsSeparator()));
				statement.setInt(1, trn);
				ResultSet resultSet = statement.executeQuery();
				if (resultSet.next()) {
					resp = resultSet.getString(1);
				}
			} catch (Exception e) {
				throw new COBISInfrastructureRuntimeException(
						"An error occurred retrieving double auth for transaction.", e);
			} finally {
				if (connection != null) {
					getDbServiceProvider().closeDBConnection(connection);
				}
			}
		}
		if (resp == null) {
			return false;
		}
		if (resp.equalsIgnoreCase("S"))
			return true;

		return false;
	}

	public Map<String, Object> getChallengeParams(ServiceRequestTO request) {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("serviceId", request.getServiceId());
		TransactionContextCIB transactionDTO = (TransactionContextCIB) request.getValue("inTransactionContextCIB");
		if (logger.isDebugEnabled()) {
			logger.logDebug("Transaction DTO key:inTransactionContextCIB in ServiceRequestTO: " + transactionDTO);
		}
		if (transactionDTO == null) {
			return params;
		}
		if (transactionDTO.getTransactionId() != 0 && transactionDTO.getAuthenticationMethod() == null) {
			params.put("transactionId", transactionDTO.getTransactionId());
			return params;

		}
		if (logger.isDebugEnabled()) {
			logger.logDebug("transactionId value sent by request: " + transactionDTO.getTransactionId());
		}

		if (transactionDTO.getAuthenticationMethod() != null) {
			params.put("transactionId", transactionDTO.getTransactionId());
			params.put("authType", "TCOORD");
			// Esa: se supone que se valida como string, ya no se usa el array
			// como en grid
			params.put("challenge", transactionDTO.getAuthenticationMethod());
		}
		return params;

	}

	public void updateBVUser(String userBv, boolean status) {

		String userStatus = (status) ? "A" : "B";
		Connection connection = getDbServiceProvider().getDBConnection();
		if (logger.isDebugEnabled()) {
			logger.logDebug("Updating authentication status for user: " + userBv + ", to: " + userStatus);
		}
		try {
			PreparedStatement statement = connection
					.prepareStatement(MessageFormat.format(QUERY_UPDATE_AUTH_STATUS_RETRY, getDbmsSeparator()));
			statement.setString(1, userStatus);
			statement.setString(2, userBv);

			statement.execute();

		} catch (Exception e) {
			throw new COBISInfrastructureRuntimeException("An error occurred updating authentication status.", e);
		} finally {
			if (connection != null) {
				getDbServiceProvider().closeDBConnection(connection);
			}
		}

	}

	public void updateAuthUserInfo(String userBv, int counter) {

		Connection connection = getDbServiceProvider().getDBConnection();
		if (logger.isDebugEnabled()) {
			logger.logDebug("Updating retry authentication for user: " + userBv + ", to: " + counter);
		}
		try {
			PreparedStatement statement = connection
					.prepareStatement(MessageFormat.format(QUERY_UPDATE_AUTH_TRANSACTION_RETRY, getDbmsSeparator()));
			statement.setInt(1, counter);
			statement.setString(2, userBv);
			statement.execute();

		} catch (Exception e) {
			throw new COBISInfrastructureRuntimeException("An error occurred updating transaction retry.", e);
		} finally {
			if (connection != null) {
				getDbServiceProvider().closeDBConnection(connection);
			}
		}
	}

	public boolean validateRetryCounter(int retryCounter) {

		return (retryCounter >= maxAuthRetries) ? true : false;
	}

	public void logTransactionStatus(ServiceRequestTO request, ServiceResponseTO response) {

		Connection connection = getDbServiceProvider().getDBConnection();

		// aqui se debe recuperar del request el DTO de Transaccion para logear.
		TransactionContextCIB transactionDTO = (TransactionContextCIB) request.getValue("inTransactionContextCIB");

		String user = getUserIdFromRequest(request);
		if (user == null) {
			user = (String) request.getValue(ICOBISTS.HEADER_LOGIN);
		}
		int trn = 0;
		trn = transactionDTO.getTransactionId();

		StringBuilder messagesAsString = new StringBuilder("");

		List<MessageTO> messages = response.getMessages();
		for (MessageTO messageTO : messages) {
			messagesAsString.append(messageTO.getCode() + " - " + messageTO.getMessage());
		}
		String respFromTO = messagesAsString.toString();
		if (respFromTO.length() > 100)
			respFromTO = respFromTO.substring(0, 100);

		int retry = (Integer) response.getValue(IAuthenticateImplService.CURR_TRX_AUTH_RETRY);

		String authType = (String) response.getValue(IAuthenticateImplService.CURR_TRX_AUTH_TYPE);

		int returnVal = (response.isSuccess() == true) ? 0 : 1;

		if (logger.isDebugEnabled()) {
			logger.logDebug("Login transaction result for virtual banking. Trn: " + trn + ", user: " + user);
		}

		try {
			PreparedStatement statement = connection
					.prepareStatement(MessageFormat.format(QUERY_LOG_AUTH_STATUS, getDbmsSeparator()));
			statement.setString(1, user); // login
			statement.setInt(2, 1); // id cliente
			statement.setInt(3, 1); // id canal
			statement.setInt(4, trn); // id transaccion
			statement.setString(5, authType); // tipo auth
			Calendar currentTime = Calendar.getInstance();
			statement.setObject(6, new java.sql.Timestamp(currentTime.getTime().getTime())); // fecha
																								// auth
			statement.setObject(7, returnVal); // codigo de retorno
			statement.setString(8, respFromTO); // mensaje de retorno
			statement.setInt(9, retry); // numero de reintentos

			statement.execute();

		} catch (Exception e) {
			throw new COBISInfrastructureRuntimeException("An error occurred logging transaction status.", e);
		} finally {
			if (connection != null) {
				getDbServiceProvider().closeDBConnection(connection);
			}
		}

	}

	/*
	 * Esa: este metodo no funcionaria para las llamadas a metodos de
	 * Administracion de entrust ya que el valor de login vendria dentro de un
	 * DTO y no directo en el mapa del ServiceRequestTO
	 */
	private String getUserIdAsString(ServiceRequestTO request) {
		String login = null;
		login = (String) request.getValue(ICOBISTS.HEADER_LOGIN);
		if (logger.isDebugEnabled()) {
			logger.logDebug("User in ServiceRequestTO header login param : " + login);
		}
		return login;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cts.vasco.api.IVascoAuthService#getUserIdFromRequest
	 * ()
	 */
	@Override
	public String getUserIdFromRequest(ServiceRequestTO request) {
		UserContext userDto = (UserContext) request.getValue("inUserContext");
		if (userDto != null) {
			if (logger.isDebugEnabled()) {
				logger.logDebug("userDto DTO key:inuserDto sent in ServiceRequestTO: " + userDto.getUserId());
			}
			return userDto.getUserId();
		}
		return null;
	}

	/**
	 * Metodo para recuperar el nombre del DBMS
	 * 
	 * @return
	 */
	private String getDbmsName() {
		if (dbmsName == null) {
			// SE recuperara el dbms Local
			dbmsName = multibackendService.getDBMS(null, IMultiBackEndResolverService.TARGET_LOCAL, false);
		}
		return dbmsName;
	}

	protected void bindMultiBackEndResolverService(IMultiBackEndResolverService service) {
		multibackendService = service;
	}

	protected void unbindMultiBackEndResolverService(IMultiBackEndResolverService service) {
		multibackendService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	private ServiceResponseTO getResponseOnFailure(String errorCode, String errorMessage) {
		ServiceResponseTO response = new ServiceResponseTO();
		response.setSuccess(false);
		MessageTO message = new MessageTO();
		message.setCode(errorCode);
		message.setMessage(errorMessage);
		response.addMessage(message);
		return response;
	}

	private ServiceResponseTO getResponseOnSuccessVoid() {
		ServiceResponseTO response = new ServiceResponseTO();
		response.setSuccess(true);
		response.addValue(RESPONSE, "");
		return response;
	}

}
