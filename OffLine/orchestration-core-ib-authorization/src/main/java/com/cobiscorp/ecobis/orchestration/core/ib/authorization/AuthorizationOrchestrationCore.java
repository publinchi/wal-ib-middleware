package com.cobiscorp.ecobis.orchestration.core.ib.authorization;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.PendingTransactionRequest;
import com.cobiscorp.ecobis.ib.application.dtos.PendingTransactionResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceAuthorization;

/**
 * Plugin of between accounts transfers
 *
 * @since Ago 25, 2020
 * @author tbaidal
 * @version 1.0.0
 *
 */
@Component(name = "AuthorizationOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "AuthorizationOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "AuthorizationOrchestrationCore") })

public class AuthorizationOrchestrationCore extends SPJavaOrchestrationBase {

	protected static final String CLASS_NAME = " >-----> ";
	protected static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	protected static final String RESPONSE_SERVER = "RESPONSE_SERVER";
	private static ILogger logger = LogFactory.getLogger(AuthorizationOrchestrationCore.class);
	private static final String CORESERVICEAUTHORIZATION = "coreServiceAuthorization";
	private static final String CORE_SERVER = "coreServer";
	protected static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";

	@Reference(referenceInterface = ICoreServiceAuthorization.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceAuthorization", unbind = "unbindCoreServiceAuthorization")
	protected ICoreServiceAuthorization coreServiceAuthorization;

	public void bindCoreServiceAuthorization(ICoreServiceAuthorization service) {
		coreServiceAuthorization = service;
	}

	public void unbindCoreServiceAuthorization(ICoreServiceAuthorization service) {
		coreServiceAuthorization = null;
	}

	public ICoreServer getCoreServer() {
		return coreServer;
	}

	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

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

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = null;
		if (logger.isInfoEnabled())
			logger.logInfo("AuthorizationOrchestrationCore: executeJavaOrchestration");

		Map<String, Object> mapInterfaces = new HashMap<String, Object>();

		mapInterfaces.put(CORESERVICEAUTHORIZATION, coreServiceAuthorization);
		mapInterfaces.put(CORE_SERVER, coreServer);

		Utils.validateComponentInstance(mapInterfaces);

		aBagSPJavaOrchestration.put(CORESERVICEAUTHORIZATION, coreServiceAuthorization);
		aBagSPJavaOrchestration.put(CORE_SERVER, coreServer);

		try {

			executeStepsTransactionsBase(anOriginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}
		return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}

	protected IProcedureResponse executeStepsTransactionsBase(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + " Ejecutando mÃ©todo executeStepsTransactionsBase: " + anOriginalRequest);

		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		ICoreServiceAuthorization coreServiceAuthorization = (ICoreServiceAuthorization) aBagSPJavaOrchestration
				.get(CORESERVICEAUTHORIZATION);

		StringBuilder messageErrorTransfer = new StringBuilder();
		messageErrorTransfer.append((String) aBagSPJavaOrchestration.get("AUTHORIZATION_TRN"));

		PendingTransactionRequest pendingTransactionRequest = transformToPendingTransactionRequest(
				anOriginalRequest.clone());
/*		
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setChannelId(anOriginalRequest.readValueFieldInHeader("servicio"));
		ServerResponse responseServer = getCoreServer().getServerStatus(serverRequest);
		aBagSPJavaOrchestration.put(RESPONSE_SERVER, responseServer);
*/
		PendingTransactionResponse pendingTransactionResponse = coreServiceAuthorization.changeTransactionStatus(pendingTransactionRequest);

		if (logger.isInfoEnabled())
			logger.logInfo("RESPONSE AUTHORIZATION -->" + pendingTransactionResponse.getReturnCode());

		if (!pendingTransactionResponse.getSuccess()) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, pendingTransactionResponse);
			return Utils.returnException(pendingTransactionResponse.getReturnCode(),
					new StringBuilder(messageErrorTransfer).append(pendingTransactionResponse.getMessage()).toString());
		}

		if (logger.isInfoEnabled())
			logger.logInfo(new StringBuilder(CLASS_NAME).append("Respuesta metodo executeStepsTransactionsBase: "
					+ aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION)).toString());

		return transformProcedureResponse(pendingTransactionResponse, anOriginalRequest);

	}

	private IProcedureResponse transformProcedureResponse(PendingTransactionResponse pendingTransactionResponse,
			IProcedureRequest request) {
		if (logger.isInfoEnabled())
			logger.logInfo("transformProcedureResponse " + pendingTransactionResponse.toString());

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		if (pendingTransactionResponse.getReturnCode() != 0) {
			// Si estamos en linea y hubo error
			wProcedureResponse = Utils.returnException(pendingTransactionResponse.getMessages());
			wProcedureResponse.setReturnCode(pendingTransactionResponse.getReturnCode());

			return wProcedureResponse;
		}

		if (!pendingTransactionResponse.getSuccess()) {

			wProcedureResponse = Utils.returnException(pendingTransactionResponse.getMessages());
			wProcedureResponse.setReturnCode(pendingTransactionResponse.getReturnCode());
		}
		
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta transformToProcedureResponse -->"
					+ wProcedureResponse.getProcedureResponseAsString());

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final ApplicationBankGuaranteeResponse --> "
					+ wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;
	}

	private PendingTransactionRequest transformToPendingTransactionRequest(IProcedureRequest aRequest) {
		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;
		messageError = aRequest.readValueParam("@s_cliente") == null ? " - @s_cliente can't be null" : "";
		messageError += aRequest.readValueParam("@s_ssn") == null ? " - @s_ssn can't be null" : "";
		messageError += aRequest.readValueParam("@s_ssn_branch") == null ? " - @s_ssn_branch can't be null" : "";
		messageError += aRequest.readValueParam("@s_servicio") == null ? " - @s_servicio can't be null" : "";
		messageError += aRequest.readValueParam("@i_operacion") == null ? " - @i_operacion can't be null" : "";
		messageError += aRequest.readValueParam("@i_trn_autorizador") == null ? " - @i_trn_autorizador can't be null"
				: "";
		messageError += aRequest.readValueParam("@i_login") == null ? " - @i_login can't be null" : "";
		messageError += aRequest.readValueParam("@i_motivo") == null ? " - @i_motivo can't be null" : "";
		messageError += aRequest.readValueParam("@i_formato_fecha") == null ? " - @i_formato_fecha can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		PendingTransactionRequest pendingTransactionRequest = new PendingTransactionRequest();

		pendingTransactionRequest.setReferenceNumber(aRequest.readValueParam("@s_ssn"));
		pendingTransactionRequest.setReferenceNumberBranch(aRequest.readValueParam("@s_ssn_branch"));
		pendingTransactionRequest.setChannelId(aRequest.readValueParam("@s_servicio"));
		pendingTransactionRequest.setEntityId(aRequest.readValueParam("@s_cliente"));
		pendingTransactionRequest.setLogin(aRequest.readValueParam("@i_login"));
		pendingTransactionRequest.setTransactionId(aRequest.readValueParam("@i_trn_autorizador"));
		pendingTransactionRequest.setOperation(aRequest.readValueParam("@i_operacion"));
		pendingTransactionRequest.setReason(aRequest.readValueParam("@i_motivo"));

		return pendingTransactionRequest;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
	}

}
