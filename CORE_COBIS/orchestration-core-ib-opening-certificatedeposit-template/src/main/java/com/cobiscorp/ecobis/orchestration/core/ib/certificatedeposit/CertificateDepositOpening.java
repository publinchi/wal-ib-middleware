/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.certificatedeposit;

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
import com.cobiscorp.ecobis.ib.application.dtos.CertificateDepositRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CertificateDepositResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceOpeningCertificateDeposit;

@Component(name = "CertificateDepositOpening", immediate = false)
@Service(value = { ICoreServiceOpeningCertificateDeposit.class })
@Properties(value = { @Property(name = "service.description", value = "CertificateDepositOpening"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CertificateDepositOpening") })

public class CertificateDepositOpening extends SPJavaOrchestrationBase
		implements ICoreServiceOpeningCertificateDeposit {

	private static ILogger logger = LogFactory.getLogger(CertificateDepositOpening.class);
	private static final String SP_NAME = "cobis..sp_tr14_apertura_CDP";

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceCashiersCheck#aplicationCashiersCheck(com.cobiscorp.ecobis.ib
	 * .application.dtos.CashiersCheckRequest)
	 */
	@Override
	public CertificateDepositResponse aplicationOpenningCertificateDeposit(
			CertificateDepositRequest aCertificateDeposit) throws CTSServiceException, CTSInfrastructureException {

		if (logger.isInfoEnabled()) {
			logger.logInfo("aplicationOpenningCertificateDeposit " + aCertificateDeposit);
			logger.logInfo("Iniciando Servicio DUMMY ACOPLADO CORE COBIS: aplicationOpenningCertificateDeposit");
		}
		IProcedureResponse pResponse = Execution(SP_NAME, aCertificateDeposit, "aplicationOpenningCertificateDeposit");
		CertificateDepositResponse wCertificateDepositResponse = transformToCertificateDepositResponse(pResponse,
				"aplicationOpenningCertificateDeposit");
		return wCertificateDepositResponse;
	}

	/*
	 * Execute Stored Procedure and Return IProcedureResponse
	 ***********************************************************************************************************************************************/
	private IProcedureResponse Execution(String SpName, CertificateDepositRequest aCertificateDepositRequest,
			String Method) throws CTSServiceException, CTSInfrastructureException {
		IProcedureRequest request = initProcedureRequest(aCertificateDepositRequest.getOriginalRequest());

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1801018");
		request.setSpName(SpName);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1801018");
		request.addInputParam("@i_ente", ICTSTypes.SQLINT4,
				aCertificateDepositRequest.getEntity().getEnte().toString());
		request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, aCertificateDepositRequest.getUserName());
		request.addInputParam("@i_doble_autorizacion", ICTSTypes.SQLVARCHAR,
				aCertificateDepositRequest.getAuthorizationRequired());

		request.addInputParam("@i_cta_debito", ICTSTypes.SQLVARCHAR,
				aCertificateDepositRequest.getProduct().getProductNumber());
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR,
				aCertificateDepositRequest.getProduct().getProductNumber());// *************
		request.addInputParam("@i_moneda", ICTSTypes.SQLINT2,
				aCertificateDepositRequest.getProduct().getCurrency().getCurrencyId().toString());
		request.addInputParam("@i_mon", ICTSTypes.SQLINT2,
				aCertificateDepositRequest.getProduct().getCurrency().getCurrencyId().toString());// *************
		request.addInputParam("@i_pro_debito", ICTSTypes.SQLINT4,
				aCertificateDepositRequest.getProduct().getProductType().toString());
		request.addInputParam("@i_prod", ICTSTypes.SQLINT4,
				aCertificateDepositRequest.getProduct().getProductType().toString());// *************

		request.addInputParam("@i_capitaliza", ICTSTypes.SQLVARCHAR,
				aCertificateDepositRequest.getCertificateDeposit().getCapitalize());
		request.addInputParam("@i_plazo", ICTSTypes.SQLINT4,
				aCertificateDepositRequest.getCertificateDeposit().getTerm().toString());
		request.addInputParam("@i_monto", ICTSTypes.SQLFLT8i,
				aCertificateDepositRequest.getCertificateDeposit().getAmount().toString());
		request.addInputParam("@i_nemonico", ICTSTypes.SQLVARCHAR,
				aCertificateDepositRequest.getCertificateDeposit().getNemonic());
		request.addInputParam("@i_tasa", ICTSTypes.SQLFLT8i,
				String.valueOf(aCertificateDepositRequest.getCertificateDeposit().getRate().getRate()));
		request.addInputParam("@i_fecha_valor", ICTSTypes.SQLVARCHAR,
				aCertificateDepositRequest.getCertificateDeposit().getProcessDate());
		request.addInputParam("@i_dia_pago", ICTSTypes.SQLINT1,
				aCertificateDepositRequest.getCertificateDeposit().getPayDay().toString());
		request.addInputParam("@i_forma_pago", ICTSTypes.SQLVARCHAR,
				aCertificateDepositRequest.getCertificateDeposit().getMethodOfPayment());
		request.addInputParam("@i_mail", ICTSTypes.SQLVARCHAR,
				aCertificateDepositRequest.getCertificateDeposit().getMail());
		request.addInputParam("@i_oficina", ICTSTypes.SQLINT2,
				aCertificateDepositRequest.getCertificateDeposit().getOffice().toString());
		request.addInputParam("@i_periodicidad", ICTSTypes.SQLVARCHAR,
				aCertificateDepositRequest.getCertificateDeposit().getPeriodicityId());
		request.addOutputParam("@o_retorno", ICTSTypes.SQLVARCHAR, "AAAAAAAAAAAAAA");
		request.addOutputParam("@o_body1", ICTSTypes.SQLVARCHAR, "");
		request.addOutputParam("@o_body2", ICTSTypes.SQLVARCHAR, "");
		request.addOutputParam("@o_body3", ICTSTypes.SQLVARCHAR, "");
		request.addOutputParam("@o_body4", ICTSTypes.SQLVARCHAR, "");

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request Corebanking: " + request.getProcedureRequestAsString());
		}

		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response OpeningCertificateDeposit: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response OpeningCertificateDeposit*** ");
		}

		return pResponse;

	}

	private CertificateDepositResponse transformToCertificateDepositResponse(IProcedureResponse aProcedureResponse,
			String Method) {
		CertificateDepositResponse wCertificateDepositResponse = new CertificateDepositResponse();
		String bodyMessage = "";

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: transformToCertificateDepositResponse***"
					+ aProcedureResponse.getProcedureResponseAsString());
		}

		if (Method.equals("aplicationOpenningCertificateDeposit")) {
			wCertificateDepositResponse.setOperationNumber(aProcedureResponse.readValueParam("@o_retorno"));
			if (aProcedureResponse.readValueParam("@o_body1") != null)
				bodyMessage = aProcedureResponse.readValueParam("@o_body1");
			if (aProcedureResponse.readValueParam("@o_body2") != null)
				bodyMessage = bodyMessage + aProcedureResponse.readValueParam("@o_body2");
			if (aProcedureResponse.readValueParam("@o_body3") != null)
				bodyMessage = bodyMessage + aProcedureResponse.readValueParam("@o_body3");
			if (aProcedureResponse.readValueParam("@o_body4") != null)
				bodyMessage = bodyMessage + aProcedureResponse.readValueParam("@o_body4");

			wCertificateDepositResponse.setBody(bodyMessage);
		}

		wCertificateDepositResponse.setReturnCode(aProcedureResponse.getReturnCode());
		Message[] message = Utils.returnArrayMessage(aProcedureResponse);
		wCertificateDepositResponse.setMessages(message);
		return wCertificateDepositResponse;
	}
}
