package com.cobiscorp.ecobis.orchestration.core.ib.checks;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
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
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceChecksQuery;
import com.cobiscorp.ecobis.ib.application.dtos.CheckRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CheckResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Check;

@Component(name = "ChecksQuery", immediate = false)
@Service(value = { ICoreServiceChecksQuery.class })
@Properties(value = { @Property(name = "service.description", value = "ChecksQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ChecksQuery") })

public class ChecksQuery extends SPJavaOrchestrationBase implements ICoreServiceChecksQuery {
	private static ILogger logger = LogFactory.getLogger(ChecksQuery.class);
	private static final String SP_NAME = "cob_cuentas..sp_tr03_consulta_cheque";
	private static final String SP_NAME_VALIDATION = "cobis..sp_bv_valida_estado_cheque";

	/**** Output: getChecks *****/
	private static final int COL_CHECK_NUMBER1 = 0;
	private static final int COL_AMOUNT1 = 1;
	private static final int COL_STATUS1 = 2;
	private static final int COL_DATE_PAYMENT1 = 3;
	private static final int COL_OFFICE_PAYMENT1 = 4;
	private static final int COL_USER_NAME1 = 5;
	private static final int COL_HOUR1 = 6;
	/**** Output: getChecksbyNumber *****/
	private static final int COL_CHECK_NUMBER2 = 0;
	private static final int COL_STATUS_ID = 1;
	private static final int COL_STATUS2 = 2;
	private static final int COL_DATE_PAYMENT2 = 3;
	private static final int COL_HOUR2 = 4;
	private static final int COL_AMOUNT2 = 5;
	private static final int COL_OFFICE_PAYMENT2 = 6;
	private static final int COL_USER_NAME2 = 7;
	private static final int COL_BENEFICIARY = 8;
	private static final int COL_DESC_OFICCE = 9;
	private static final int COL_NAME_ACCOUNT = 10;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceChecksQuery#
	 * getChecks(com.cobiscorp.ecobis.ib.application.dtos.CheckRequest)
	 ***********************************************************************************************************************************************/
	@Override
	public CheckResponse getChecks(CheckRequest aCheckRequest) throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getChecks");
		}

		IProcedureResponse pResponse = Execution(SP_NAME, aCheckRequest, "getChecks");
		CheckResponse checkResponse = transformToCheckResponse(pResponse, "getChecks");
		return checkResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceChecksQuery#
	 * getChecksbyNumber(com.cobiscorp.ecobis.ib.application.dtos.CheckRequest)
	 ***********************************************************************************************************************************************/
	@Override
	public CheckResponse getChecksbyNumber(CheckRequest aCheckRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getChecksbyNumber");
		}

		IProcedureResponse pResponse = Execution(SP_NAME, aCheckRequest, "getChecksbyNumber");
		CheckResponse checkResponse = transformToCheckResponse(pResponse, "getChecksbyNumber");
		return checkResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceChecksQuery#
	 * validateCheckStatus(com.cobiscorp.ecobis.ib.application.dtos.
	 * CheckRequest)
	 ***********************************************************************************************************************************************/
	@Override
	public CheckResponse validateCheckStatus(CheckRequest aCheckRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: validateCheckStatus");
		}

		IProcedureResponse pResponse = Validation(SP_NAME_VALIDATION, aCheckRequest, "validateCheckStatus");
		CheckResponse checkResponse = transformValidationResponse(pResponse, "validateCheckStatus");
		return checkResponse;
	}

	/*
	 * Execute Stored Procedure and Return IProcedureResponse
	 ***********************************************************************************************************************************************/
	private IProcedureResponse Execution(String SpName, CheckRequest aCheckRequest, String Method)
			throws CTSServiceException, CTSInfrastructureException {
		;
		IProcedureRequest request = initProcedureRequest(aCheckRequest.getOriginalRequest());

		// IProcedureRequest request = new ProcedureRequestAS();
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1801019");

		request.setSpName(SpName);

		request.addInputParam("@t_ejec", ICTSTypes.SQLCHAR, aCheckRequest.getEjec());
		request.addInputParam("@t_rty", ICTSTypes.SQLCHAR, aCheckRequest.getRty());
		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, aCheckRequest.getCodeTransactionalIdentifier());

		request.addInputParam("@i_prod", ICTSTypes.SQLINT1, aCheckRequest.getProductId().getProductType().toString());
		request.addInputParam("@i_mon", ICTSTypes.SQLINT2, aCheckRequest.getCurrency().getCurrencyId().toString());
		request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, aCheckRequest.getUserName());
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aCheckRequest.getProductId().getProductNumber());
		request.addInputParam("@i_chq", ICTSTypes.SQLINT4, aCheckRequest.getCheckNumber().getCheckNumber());
		request.addInputParam("@i_formato_fecha", ICTSTypes.SQLINT1, aCheckRequest.getDateFormatId().toString());
		request.addInputParam("@i_servicio", ICTSTypes.SQLINT1,
				aCheckRequest.getOriginalRequest().readValueParam("@s_servicio"));

		if (Method.equals("getChecks")) {

			request.addInputParam("@i_opcion", ICTSTypes.SQLCHAR, aCheckRequest.getCriteria());
			request.addInputParam("@i_fecha_ini", ICTSTypes.SQLVARCHAR, aCheckRequest.getStringInitialDate());
			request.addInputParam("@i_fecha_fin", ICTSTypes.SQLVARCHAR, aCheckRequest.getStringFinalDate());
			if (aCheckRequest.getInitialAmount() != null) {
				request.addInputParam("@i_monto_ini", ICTSTypes.SQLMONEY, aCheckRequest.getInitialAmount().toString());
			}
			if (aCheckRequest.getFinalAmount() != null) {
				request.addInputParam("@i_monto_fin", ICTSTypes.SQLMONEY, aCheckRequest.getFinalAmount().toString());
			}
			request.addInputParam("@i_chq_ini", ICTSTypes.SQLINT4, aCheckRequest.getInitialCheck());
			request.addInputParam("@i_chq_fin", ICTSTypes.SQLINT4, aCheckRequest.getFinalCheck());
			request.addInputParam("@i_chq_estado", ICTSTypes.SQLCHAR, aCheckRequest.getStatusCheck());
		}
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Request:*** " + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Start Request *** ");
		}

		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response *** ");
		}
		return pResponse;
	}

	private IProcedureResponse Validation(String SpName, CheckRequest aCheckRequest, String Method)
			throws CTSServiceException, CTSInfrastructureException {
		;
		// IProcedureRequest request =
		// initProcedureRequest(aCheckRequest.getOriginalRequest());
		IProcedureRequest request = new ProcedureRequestAS();
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800026");

		request.setSpName(SpName);

		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aCheckRequest.getProductId().getProductNumber());
		request.addInputParam("@i_desde", ICTSTypes.SQLINT2, aCheckRequest.getCheckNumber().getCheckNumber());
		request.addInputParam("@i_num_cheque", ICTSTypes.SQLINT4, aCheckRequest.getFinalCheck().toString());
		request.addInputParam("@i_estado", ICTSTypes.SQLVARCHAR, aCheckRequest.getCheckNumber().getStatus());

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Request:*** " + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Start Request *** ");
		}

		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response *** ");
		}
		return pResponse;
	}

	private CheckResponse transformToCheckResponse(IProcedureResponse aProcedureResponse, String Method) {
		CheckResponse CheckResp = new CheckResponse();
		List<Check> checkCollection = new ArrayList<Check>();
		Check aCheck = null;

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + aProcedureResponse.getProcedureResponseAsString());
		}

		if (aProcedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsCheck = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();
			if (Method.equals("getChecks")) {
				for (IResultSetRow iResultSetRow : rowsCheck) {
					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
					aCheck = new Check();
					aCheck.setCheckNumber(columns[COL_CHECK_NUMBER1].getValue());
					aCheck.setAmount(columns[COL_AMOUNT1].getValue() == null ? new BigDecimal(0)
							: new BigDecimal(columns[COL_AMOUNT1].getValue()));
					aCheck.setStatus(columns[COL_STATUS1].getValue() == null ? "" : columns[COL_STATUS1].getValue());
					aCheck.setDatePayment(columns[COL_DATE_PAYMENT1].getValue() == null ? ""
							: columns[COL_DATE_PAYMENT1].getValue().toString());
					aCheck.setAmount(columns[COL_AMOUNT1].getValue() == null ? new BigDecimal(0)
							: new BigDecimal(columns[COL_AMOUNT1].getValue()));
					aCheck.setOfficePayment(columns[COL_OFFICE_PAYMENT1].getValue() == null ? 0
							: Integer.parseInt(columns[COL_OFFICE_PAYMENT1].getValue()));
					aCheck.setUserName(
							columns[COL_USER_NAME1].getValue() == null ? "" : columns[COL_USER_NAME1].getValue());
					if (columns[COL_HOUR1].getValue() == null) {
						aCheck.setHour("00:00:00");
					}
					if (columns[COL_HOUR1].getValue() != null) {
						aCheck.setHour(columns[COL_HOUR1].getValue().toString());
					}

					checkCollection.add(aCheck);
				}
			}
			if (Method.equals("getChecksbyNumber")) {
				for (IResultSetRow iResultSetRow : rowsCheck) {
					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
					aCheck = new Check();
					aCheck.setCheckNumber(columns[COL_CHECK_NUMBER2].getValue());
					aCheck.setStatusId(
							columns[COL_STATUS_ID].getValue() == null ? "" : columns[COL_STATUS_ID].getValue());
					aCheck.setStatus(columns[COL_STATUS2].getValue() == null ? "" : columns[COL_STATUS2].getValue());
					aCheck.setDatePayment(columns[COL_DATE_PAYMENT2].getValue() == null ? ""
							: columns[COL_DATE_PAYMENT2].getValue().toString());
					aCheck.setHour(
							columns[COL_HOUR2].getValue() == null ? "00:00" : columns[COL_HOUR2].getValue().toString());
					aCheck.setAmount(columns[COL_AMOUNT2].getValue() == null ? new BigDecimal(0)
							: new BigDecimal(columns[COL_AMOUNT2].getValue()));
					aCheck.setOfficePayment(columns[COL_OFFICE_PAYMENT2].getValue() == null ? 0
							: Integer.parseInt(columns[COL_OFFICE_PAYMENT2].getValue()));
					aCheck.setUserName(
							columns[COL_USER_NAME2].getValue() == null ? "" : columns[COL_USER_NAME2].getValue());
					aCheck.setBeneficiary(
							columns[COL_BENEFICIARY].getValue() == null ? "" : columns[COL_BENEFICIARY].getValue());
					aCheck.setDescriptionOffice(
							columns[COL_DESC_OFICCE].getValue() == null ? "" : columns[COL_DESC_OFICCE].getValue());
					aCheck.setNameAccount(
							columns[COL_NAME_ACCOUNT].getValue() == null ? "" : columns[COL_NAME_ACCOUNT].getValue());
					checkCollection.add(aCheck);
				}
			}
			CheckResp.setCheckCollection(checkCollection);
		} else {
			CheckResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		}

		CheckResp.setReturnCode(aProcedureResponse.getReturnCode());

		return CheckResp;
	}

	private CheckResponse transformValidationResponse(IProcedureResponse aProcedureResponse, String Method) {
		CheckResponse CheckResp = new CheckResponse();

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + aProcedureResponse.getProcedureResponseAsString());
		}

		if (aProcedureResponse.getReturnCode() != 0) {
			CheckResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		}

		CheckResp.setReturnCode(aProcedureResponse.getReturnCode());

		return CheckResp;
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
		return null;
	}
}
