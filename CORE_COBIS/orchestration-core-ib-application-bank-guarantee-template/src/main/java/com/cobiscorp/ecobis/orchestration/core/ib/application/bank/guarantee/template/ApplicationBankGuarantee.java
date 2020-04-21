package com.cobiscorp.ecobis.orchestration.core.ib.application.bank.guarantee.template;

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
import com.cobiscorp.ecobis.ib.application.dtos.ApplicationBankGuaranteeRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ApplicationBankGuaranteeResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceApplicationBankGuarantee;

@Component(name = "ApplicationBankGuarantee", immediate = false)
@Service(value = { ICoreServiceApplicationBankGuarantee.class })
@Properties(value = { @Property(name = "service.description", value = "ApplicationBankGuarantee"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ApplicationBankGuarantee") })
public class ApplicationBankGuarantee extends SPJavaOrchestrationBase implements ICoreServiceApplicationBankGuarantee {

	private static ILogger logger = LogFactory.getLogger(ApplicationBankGuarantee.class);

	@Override
	public ApplicationBankGuaranteeResponse executeApplication(
			ApplicationBankGuaranteeRequest aApplicationBankGuaranteeRequest)
			throws CTSServiceException, CTSInfrastructureException {

		IProcedureRequest request = initProcedureRequest(aApplicationBankGuaranteeRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1801046");
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1801046");
		request.setSpName("cobis..sp_bv_boleta_garantia");
		if (aApplicationBankGuaranteeRequest.getCreditLine() != null)
			request.addInputParam("@i_linea_credito", ICTSTypes.SYBVARCHAR,
					aApplicationBankGuaranteeRequest.getCreditLine());
		request.addInputParam("@i_monto", ICTSTypes.SYBMONEY, aApplicationBankGuaranteeRequest.getAmount().toString());
		request.addInputParam("@i_moneda", ICTSTypes.SYBINT2,
				aApplicationBankGuaranteeRequest.getCurrency().toString());
		request.addInputParam("@i_pplazo", ICTSTypes.SYBINT2,
				aApplicationBankGuaranteeRequest.getGuaranteeTerm().toString());
		request.addInputParam("@i_beneficiario", ICTSTypes.SYBVARCHAR,
				aApplicationBankGuaranteeRequest.getBeneficiary());
		request.addInputParam("@i_clase_garantia", ICTSTypes.SYBVARCHAR,
				aApplicationBankGuaranteeRequest.getGuaranteeClass());
		request.addInputParam("@i_tipo_garantia", ICTSTypes.SYBVARCHAR,
				aApplicationBankGuaranteeRequest.getGuaranteeType());
		request.addInputParam("@i_ente", ICTSTypes.SYBINT4, aApplicationBankGuaranteeRequest.getEntity().toString());
		request.addInputParam("@i_fecha_expiracion", ICTSTypes.SYBDATETIME,
				aApplicationBankGuaranteeRequest.getExpirationDate());
		request.addInputParam("@i_fecha_crea", ICTSTypes.SYBDATETIME,
				aApplicationBankGuaranteeRequest.getCreationDate());
		request.addInputParam("@i_motivo1", ICTSTypes.SYBVARCHAR, aApplicationBankGuaranteeRequest.getCause());
		if (aApplicationBankGuaranteeRequest.getFixedTerm() != null)
			request.addInputParam("@i_plazo_fijo", ICTSTypes.SYBVARCHAR,
					aApplicationBankGuaranteeRequest.getFixedTerm());
		if (aApplicationBankGuaranteeRequest.getGuaranteeTypeApp() != null)
			request.addInputParam("@i_tipo_gar_tr", ICTSTypes.SYBVARCHAR,
					aApplicationBankGuaranteeRequest.getGuaranteeTypeApp());
		if (aApplicationBankGuaranteeRequest.getAddress() != null)
			request.addInputParam("@i_dirpcd", ICTSTypes.SQLVARCHAR, aApplicationBankGuaranteeRequest.getAddress());
		request.addInputParam("@i_agencia", ICTSTypes.SYBINT4, aApplicationBankGuaranteeRequest.getAgency().toString());
		request.addOutputParam("@o_siguiente", ICTSTypes.SYBVARCHAR, "0");

		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Request aApplicationBankGuaranteeRequest: *** " + aApplicationBankGuaranteeRequest);
		}
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response Response: *** " + pResponse.getProcedureResponseAsString());
		}

		return transformToApplicationBankGuaranteeResponse(pResponse);
	}

	private ApplicationBankGuaranteeResponse transformToApplicationBankGuaranteeResponse(IProcedureResponse pResponse) {

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: transformToRequestCheckbookResponse***"
					+ pResponse.getProcedureResponseAsString());
		}

		ApplicationBankGuaranteeResponse creditLineResponse = new ApplicationBankGuaranteeResponse();

		if (pResponse.getReturnCode() != 0 && pResponse.getReturnCode() != 40002)
			creditLineResponse.setMessages(Utils.returnArrayMessage(pResponse));

		creditLineResponse.setBankGuarantee(pResponse.readValueParam("@o_siguiente"));

		creditLineResponse.setSuccess(pResponse.getReturnCode() == 0 ? true : false);
		creditLineResponse.setReturnCode(pResponse.getReturnCode());

		return creditLineResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.commons.components.ICOBISComponent#loadConfiguration
	 * (com.cobiscorp.cobis.commons.configuration.IConfigurationReader)
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase
	 * #executeJavaOrchestration
	 * (com.cobiscorp.cobis.cts.domains.IProcedureRequest, java.util.Map)
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
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase
	 * #processResponse(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
