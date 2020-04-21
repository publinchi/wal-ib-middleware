package com.cobiscorp.ecobis.orchestration.core.ib.application.credit.line.template;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ApplicationCreditLineRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ApplicationCreditLineResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceApplicationCreditLine;

@Component(name = "ApplicationDisbursementCreditLine", immediate = false)
@Service(value = { ICoreServiceApplicationCreditLine.class })
@Properties(value = { @Property(name = "service.description", value = "ApplicationDisbursementCreditLine"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ApplicationDisbursementCreditLine") })

public class ApplicationDisbursementCreditLine extends SPJavaOrchestrationBase
		implements ICoreServiceApplicationCreditLine {

	private static ILogger logger = LogFactory.getLogger(ApplicationDisbursementCreditLine.class);

	@Override
	public ApplicationCreditLineResponse getApplicationCreditLine(
			ApplicationCreditLineRequest aApplicationCreditLineRequest)
			throws CTSServiceException, CTSInfrastructureException {

		IProcedureRequest request = initProcedureRequest(aApplicationCreditLineRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800264");
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1800264");
		request.setSpName("cobis..sp_bv_solic_desembolso_LC");

		request.addInputParam("@i_ente", ICTSTypes.SQLINT1, aApplicationCreditLineRequest.getBeneficiaryId());
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR,
				aApplicationCreditLineRequest.getProductCreditLine().getProductNumber());
		request.addInputParam("@i_mon", ICTSTypes.SQLINT1,
				aApplicationCreditLineRequest.getProductCreditLine().getCurrency().getCurrencyId().toString());
		request.addInputParam("@i_monto", ICTSTypes.SQLMONEY,
				aApplicationCreditLineRequest.getMontoSolicitado().toString());
		request.addInputParam("@i_monto_disponible", ICTSTypes.SQLMONEY,
				aApplicationCreditLineRequest.getMontoDisponible().toString());

		request.addOutputParam("@o_siguiente", ICTSTypes.SQLINT1, "0");

		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Request aApplicationCreditLineRequest: *** " + aApplicationCreditLineRequest);
		}
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response Response: *** " + pResponse.getProcedureResponseAsString());
		}

		return transformToApplicationCreditLineResponse(pResponse);
	}

	private ApplicationCreditLineResponse transformToApplicationCreditLineResponse(IProcedureResponse pResponse) {

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: transformToRequestCheckbookResponse***"
					+ pResponse.getProcedureResponseAsString());
		}

		ApplicationCreditLineResponse creditLineResponse = new ApplicationCreditLineResponse();
		if (pResponse.getReturnCode() != 0 && pResponse.getReturnCode() != 40002)
			creditLineResponse.setMessages(Utils.returnArrayMessage(pResponse));
		creditLineResponse.setIdSolicitud(Integer.parseInt(pResponse.readValueParam("@o_siguiente")));
		creditLineResponse.setSuccess(pResponse.getReturnCode() == 0 ? true : false);
		creditLineResponse.setReturnCode(pResponse.getReturnCode());

		return creditLineResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.commons.components.ICOBISComponent#loadConfiguration(
	 * com.cobiscorp.cobis.commons.configuration.IConfigurationReader)
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase#
	 * executeJavaOrchestration(com.cobiscorp.cobis.cts.domains.
	 * IProcedureRequest, java.util.Map)
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase#
	 * processResponse(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}
