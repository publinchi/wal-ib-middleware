package com.cobiscorp.ecobis.orchestration.core.ib.authenticate.impl;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO;
import cobiscorp.ecobis.commons.dto.ServiceResponseTO;

import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.converters.ByteConverter;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.crypt.CryptorDecryptorForRC4;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.service.executor.external.authenticator.api.IExternalAuthenticator;
//import com.cobiscorp.cobis.cts.service.executor.external.authenticator.api.IExternalAuthenticatorProvider;
import com.cobiscorp.cobis.cts.services.session.ISessionManager;
import com.cobiscorp.ecobis.ib.authenticate.interfaces.IAuthenticateImplService;

/**
 * 
 * @author dmorla
 *
 */
@Component(name = "AuthenticateOrchestrationCore")
@Service(IExternalAuthenticator.class)
@Properties(value = { @Property(name = "service.description", value = "AuthenticateOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "AuthenticateOrchestrationCore") })
public class AuthenticateOrchestrationCore implements IExternalAuthenticator {

	/**
	 * Variable de logger
	 */
	private static final ILogger logger = LogFactory.getLogger(AuthenticateOrchestrationCore.class);

	/**
	 * Variable para recuperar datos de la session
	 */
	@Reference(referenceInterface = ISessionManager.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindSessionManager", unbind = "unbindSessionManager")
	private ISessionManager sessionManager;

	/**
	 * Variable que contiene la implementacion que se encargara de llamar a los
	 * servicios de VASCO
	 */
	@Reference(referenceInterface = IAuthenticateImplService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindAuthenticateImplService", unbind = "unbindAuthenticateImplService")
	private IAuthenticateImplService AuthenticateImplService;

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

	protected void bindAuthenticateImplService(IAuthenticateImplService service) {
		if (logger.isDebugEnabled())
			logger.logDebug("binding IAuthenticateImplService: " + service);
		AuthenticateImplService = service;
	}

	protected void unbindAuthenticateImplService(IAuthenticateImplService service) {
		if (logger.isDebugEnabled())
			logger.logDebug("un binding IAuthenticateImplService: " + service);
		AuthenticateImplService = null;
	}

	public ServiceResponseTO authenticate(ServiceRequestTO request) throws CTSInfrastructureException {

		if (logger.isDebugEnabled())
			logger.logDebug("executing external authentication using VASCO impl");

		IProcedureRequest procedureReq = new ProcedureRequestAS();
		// desencripto el sessionId
		String sessionIdTemp = request.getSessionId().substring(3);
		String sessionId2 = new String(ByteConverter.tranformFromHex(sessionIdTemp));
		CryptorDecryptorForRC4 cryptor = CryptorDecryptorForRC4.getInstance();
		sessionId2 = "ID:" + cryptor.decriptWithRC4(sessionId2);

		procedureReq.addFieldInHeader(ICOBISTS.HEADER_SESSION_ID, ICOBISTS.HEADER_STRING_TYPE, sessionId2);
		// poner los parametros de sesion del usuario en el serviceRequest
		try {
			sessionManager.setSessionParameters(procedureReq);
		} catch (CTSServiceException e) {
			throw new CTSInfrastructureException("Error: " + e.getClientErrorCode() + ", " + e.getMessage());
		} catch (CTSInfrastructureException e) {
			throw new CTSInfrastructureException("Error: " + e.getClientErrorCode() + ", " + e.getMessage());
		}
		if (logger.isDebugEnabled()) {
			logger.logDebug("procedureReq: " + procedureReq);
		}
		// a�adir el parametro AuthenticateImplService.userId con el valor de
		// login en HEADER
		request.addValue(ICOBISTS.HEADER_LOGIN, procedureReq.readValueFieldInHeader(ICOBISTS.HEADER_LOGIN));

		// valida el reto de autenticacion
		return AuthenticateImplService.validateChallenge(request);
	}

	public boolean hasExternalAuthenticator(ServiceRequestTO request) {
		ServiceResponseTO response = new ServiceResponseTO();
		if (logger.isDebugEnabled()) {
			// logger.logDebug("getSessionId: " + request.getSessionId());
			// logger.logDebug("getServiceId: " + request.getServiceId());
			logger.logDebug("AuthenticateImplService: " + AuthenticateImplService);
		}
		if (request.getSessionId() == null
		// ||
		// request.getServiceId().equalsIgnoreCase("CtsService.vasco.validateChallenge")
		// ||
		// request.getServiceId().equalsIgnoreCase("CtsService.hasExternalAuthenticator")
		) {
			return false;
		} else {
			if (AuthenticateImplService != null) {
				if (AuthenticateImplService.hasTransactionDoubleAuthentication(request)) {
					IProcedureRequest procedureReq = new ProcedureRequestAS();
					// desencripto el sessionId
					String sessionIdTemp = request.getSessionId().substring(3);
					String sessionId2 = new String(ByteConverter.tranformFromHex(sessionIdTemp));
					CryptorDecryptorForRC4 cryptor = CryptorDecryptorForRC4.getInstance();
					sessionId2 = "ID:" + cryptor.decriptWithRC4(sessionId2);

					procedureReq.addFieldInHeader(ICOBISTS.HEADER_SESSION_ID, ICOBISTS.HEADER_STRING_TYPE, sessionId2);
					// poner los parametros de sesion del usuario en el
					// serviceRequest
					try {
						sessionManager.setSessionParameters(procedureReq);
					} catch (CTSServiceException e) {
						logger.logError("Error: " + e.getClientErrorCode() + ", " + e.getMessage());
					} catch (CTSInfrastructureException e) {
						logger.logError("Error: " + e.getClientErrorCode() + ", " + e.getMessage());
					}
					if (logger.isDebugEnabled()) {
						logger.logDebug("procedureReq: " + procedureReq);
					}
					// SE va a utilizar esto solo para Banca Virtual.
					if (!"COBISBV".equalsIgnoreCase(procedureReq.readValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID))) {
						return false;
					}
					// a�adir el parametro AuthenticateImplService.userId con el
					// valor de login en HEADER
					request.addValue(ICOBISTS.HEADER_LOGIN, procedureReq.readValueFieldInHeader(ICOBISTS.HEADER_LOGIN));
					// validar si el usuario tiene licencia para 2ble
					// autenticacion
					response = AuthenticateImplService.getCurrentAuthType(request);
					String authType = (String) response.getValue(IAuthenticateImplService.CURR_TRX_AUTH_TYPE);
					if (authType != null && !authType.equalsIgnoreCase("NONE")) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

}
