/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.ecobis.ib.application.dtos.PendingTransactionRequest;
import com.cobiscorp.ecobis.ib.application.dtos.PendingTransactionResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceAuthorization;

/**
 * @author tbaidal
 *
 */

@Component(name = "AuthorizationBase", immediate = false)
@Service(value = { ICoreServiceAuthorization.class })
@Properties(value = { @Property(name = "service.description", value = "AuthorizationBase"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "AuthorizationBase") })
public class AuthorizationBase extends SPJavaOrchestrationBase implements ICoreServiceAuthorization{

	private static ILogger logger = LogFactory.getLogger(AuthorizationBase.class);
	private static final String CLASS_NAME = " >-----> ";
	protected static final String COBIS_CONTEXT = "COBIS";
	protected static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public PendingTransactionResponse changeTransactionStatus(PendingTransactionRequest rendingTransactionRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando rechazo de transaccion LOCAL COBIS.");

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_SSN, ICOBISTS.HEADER_NUMBER_TYPE,
				String.valueOf(rendingTransactionRequest.getReferenceNumber()));
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH, ICOBISTS.HEADER_NUMBER_TYPE,
				String.valueOf(rendingTransactionRequest.getReferenceNumberBranch()));
		anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18790");
		anOriginalRequest.setSpName("cob_bvirtual..sp_autoriza_pendientes_cw_bv");

		anOriginalRequest.addInputParam("@s_cliente", ICTSTypes.SYBINT4, rendingTransactionRequest.getEntityId());
		anOriginalRequest.addInputParam("@s_servicio", ICTSTypes.SYBINT2, rendingTransactionRequest.getChannelId());
		anOriginalRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, rendingTransactionRequest.getOperation());
		anOriginalRequest.addInputParam("@i_trn_autorizador", ICTSTypes.SQLVARCHAR, rendingTransactionRequest.getTransactionId());
		anOriginalRequest.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, rendingTransactionRequest.getLogin());
		anOriginalRequest.addInputParam("@i_motivo", ICTSTypes.SQLVARCHAR, rendingTransactionRequest.getReason());
		
		
		IProcedureResponse response = executeCoreBanking(anOriginalRequest);
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta del Core:" + response.getProcedureResponseAsString());

		PendingTransactionResponse pendingTransactionResponse = transformResponse(response);
		return pendingTransactionResponse;
	}

	private PendingTransactionResponse transformResponse(IProcedureResponse response) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando Transformacion de Respuesta");

		PendingTransactionResponse pendingTransactionResponse = new PendingTransactionResponse();
		
		if (response == null) {
			if (logger.isInfoEnabled())
				logger.logInfo("PendingTransactionResponse --> Response null");
			return null;
		}

		if (response.getReturnCode() != 0)
			pendingTransactionResponse.setMessages(Utils.returnArrayMessage(response));

		pendingTransactionResponse.setReturnCode(response.getReturnCode());
		pendingTransactionResponse.setSuccess(response.getReturnCode() == 0 ? true : false);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta -> " + pendingTransactionResponse);
		return pendingTransactionResponse;
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}
	
	



}
