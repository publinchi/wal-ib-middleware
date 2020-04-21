package com.cobiscorp.ecobis.orchestration.core.ib.cashierscheck;

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
import com.cobiscorp.ecobis.ib.application.dtos.CashiersCheckRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CashiersCheckResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCashiersCheck;

@Component(name = "CashiersCheckAplication", immediate = false)
@Service(value = { ICoreServiceCashiersCheck.class })
@Properties(value = { @Property(name = "service.description", value = "CashiersCheckAplication"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CashiersCheckAplication") })

public class CashiersCheckAplication extends SPJavaOrchestrationBase implements ICoreServiceCashiersCheck {
	private static ILogger logger = LogFactory.getLogger(CashiersCheckAplication.class);
	private static final String SP_NAME = "cobis..sp_bv_transaccion_sb";
	private static final int COL_REFERENCE = 0;
	private static final int COL_BATCH_ID = 1;
	private static final int COL_AUTHORIZATION_REQUIRED = 2;
	private static final int COL_BRANCH_SSN = 3;
	private static final int COL_CONDITION_ID = 4;

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

	@Override
	public CashiersCheckResponse aplicationCashiersCheck(CashiersCheckRequest aCashiersCheck)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("Iniciando Servicio DUMMY ACOPLADO CORE COBIS: aplicationCashiersCheck");
		}
		IProcedureResponse pResponse = Execution(SP_NAME, aCashiersCheck, "aplicationCashiersCheck");
		CashiersCheckResponse cashiersCheckResponse = transformToCashiersCheckResponse(pResponse,
				"aplicationCashiersCheck");
		return cashiersCheckResponse;
	}

	private IProcedureResponse Execution(String SpName, CashiersCheckRequest aCashiersCheckRequest, String Method)
			throws CTSServiceException, CTSInfrastructureException {
		IProcedureRequest request = initProcedureRequest(aCashiersCheckRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800120");
		request.setSpName(SpName);
		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "I");
		request.addInputParam("@i_producto", ICTSTypes.SQLINT1,
				aCashiersCheckRequest.getProduct().getProductType().toString());
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aCashiersCheckRequest.getProduct().getProductNumber());
		request.addInputParam("@i_mon", ICTSTypes.SQLINT4,
				aCashiersCheckRequest.getProduct().getCurrency().getCurrencyId().toString());
		request.addInputParam("@i_monto", ICTSTypes.SQLMONEY,
				aCashiersCheckRequest.getManagerCheck().getAmount().toString());
		request.addInputParam("@i_beneficiario", ICTSTypes.SQLVARCHAR,
				aCashiersCheckRequest.getManagerCheck().getBeneficiary());
		request.addInputParam("@i_cod_cliente", ICTSTypes.SQLINT4,
				aCashiersCheckRequest.getEntity().getCodCustomer().toString());
		request.addInputParam("@i_cod_ben", ICTSTypes.SQLVARCHAR,
				aCashiersCheckRequest.getManagerCheck().getBeneficiaryId());
		request.addInputParam("@i_tipo_id_ben", ICTSTypes.SQLVARCHAR,
				aCashiersCheckRequest.getManagerCheck().getBeneficiaryTypeId());
		request.addInputParam("@i_id_ben", ICTSTypes.SQLVARCHAR,
				aCashiersCheckRequest.getManagerCheck().getBeneficiaryTypeId());
		request.addInputParam("@i_tel_benef", ICTSTypes.SQLVARCHAR,
				aCashiersCheckRequest.getManagerCheck().getAuthorizedPhoneNumber());
		request.addInputParam("@i_ofi_destino", ICTSTypes.SQLINT2,
				aCashiersCheckRequest.getManagerCheck().getDestinationOfficeId().toString());
		request.addInputParam("@i_retira_id", ICTSTypes.SQLVARCHAR,
				aCashiersCheckRequest.getManagerCheck().getAuthorizedTypeId());
		request.addInputParam("@i_retira_telef", ICTSTypes.SQLVARCHAR,
				aCashiersCheckRequest.getManagerCheck().getAuthorizedPhoneNumber());
		request.addInputParam("@i_retira_id", ICTSTypes.SQLVARCHAR,
				aCashiersCheckRequest.getManagerCheck().getAuthorizedId());
		request.addInputParam("@i_retira_nombre", ICTSTypes.SQLVARCHAR,
				aCashiersCheckRequest.getManagerCheck().getAuthorized());
		request.addInputParam("@i_retira_correo", ICTSTypes.SQLVARCHAR,
				aCashiersCheckRequest.getManagerCheck().getEmail());
		request.addInputParam("@i_proposito", ICTSTypes.SQLVARCHAR,
				aCashiersCheckRequest.getManagerCheck().getPurpose());
		request.addInputParam("@i_ente", ICTSTypes.SQLINT4, aCashiersCheckRequest.getEntity().getEnte().toString());
		request.addInputParam("@i_moneda_monto", ICTSTypes.SQLINT4,
				aCashiersCheckRequest.getManagerCheck().getCurrencyId().toString());

		request.addInputParam("@i_causa_org", ICTSTypes.SQLVARCHAR, aCashiersCheckRequest.getCause());
		if (aCashiersCheckRequest.getCauseComi() != null) {
			request.addInputParam("@i_causa_comi", ICTSTypes.SQLVARCHAR, aCashiersCheckRequest.getCauseComi());
			request.addInputParam("@i_servicio_costo", ICTSTypes.SQLVARCHAR, aCashiersCheckRequest.getServiceCost());
		}

		request.addOutputParam("@o_referencia", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_idlote", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_moneda", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_oficina", ICTSTypes.SQLVARCHAR,
				"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		request.addOutputParam("@o_autorizacion", ICTSTypes.SQLVARCHAR, "XX");
		request.addOutputParam("@o_ssn_branch", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_condicion", ICTSTypes.SQLINT4, "0");

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request Corebanking: " + request.getProcedureRequestAsString());
		}

		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response CashierCheck: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response CashierCheck*** ");
		}

		return pResponse;

	}

	private CashiersCheckResponse transformToCashiersCheckResponse(IProcedureResponse aProcedureResponse,
			String Method) {
		CashiersCheckResponse cashiersCheckResponse = new CashiersCheckResponse();

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: transformToCashiersCheckResponse***"
					+ aProcedureResponse.getProcedureResponseAsString());
		}

		if (Method.equals("aplicationCashiersCheck")) {
			cashiersCheckResponse.setReference(aProcedureResponse.readValueParam("@o_referencia") == null ? 0
					: Integer.parseInt(aProcedureResponse.readValueParam("@o_referencia")));
			if (aProcedureResponse.readValueParam("@o_idlote") != null) {
				cashiersCheckResponse.setBatchId(Integer.parseInt(aProcedureResponse.readValueParam("@o_idlote")));
			}
			cashiersCheckResponse.setAuthorizationRequired(aProcedureResponse.readValueParam("@o_autorizacion"));// ;(aProcedureResponse.readValueParam("@o_descripcion_moneda"));
			cashiersCheckResponse.setBranchSSN(aProcedureResponse.readValueParam("@o_ssn_branch") == null ? 0
					: Integer.parseInt(aProcedureResponse.readValueParam("@o_ssn_branch")));
			cashiersCheckResponse.setConditionId(aProcedureResponse.readValueParam("@o_condicion") == null ? 0
					: Integer.parseInt(aProcedureResponse.readValueParam("@o_condicion")));
			if (logger.isInfoEnabled()) {
				logger.logInfo(aProcedureResponse.readValueParam("@o_oficina") == null ? "LLEGO VACIO ESTA WEA"
						: aProcedureResponse.readValueParam("@o_oficina").toString());
			}
			cashiersCheckResponse.setOffice(aProcedureResponse.readValueParam("@o_oficina") == null ? ""
					: aProcedureResponse.readValueParam("@o_oficina").toString());
		}
		cashiersCheckResponse.setReturnCode(aProcedureResponse.getReturnCode());
		Message[] message = Utils.returnArrayMessage(aProcedureResponse);
		cashiersCheckResponse.setMessages(message);
		return cashiersCheckResponse;
	}
}
