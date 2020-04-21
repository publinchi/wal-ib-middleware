package com.cobiscorp.ecobis.orchestration.core.ib.timedeposits.config;

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
import com.cobiscorp.ecobis.ib.application.dtos.CdPeriodicityResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CdRateResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CdSimulationResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CdTypeResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CertificateDepositCommonRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CertificateDepositResponse;
import com.cobiscorp.ecobis.ib.application.dtos.DetailCdResponse;
import com.cobiscorp.ecobis.ib.application.dtos.SimulationExpirationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SimulationExpirationResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Category;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CertificateDeposit;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CertificateDepositResult;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Parameters;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Periodicity;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Rate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.SimulationExpiration;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Type;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceTimeDepositConfig;

@Component(name = "TimeDepositSimulation ", immediate = false)
@Service(value = { ICoreServiceTimeDepositConfig.class })
@Properties(value = { @Property(name = "service.description", value = "TimeDepositQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TimeDepositQuery") })

public class TimeDepositConfig extends SPJavaOrchestrationBase implements ICoreServiceTimeDepositConfig {
	private static ILogger logger = LogFactory.getLogger(TimeDepositConfig.class);
	private static final String SP_NAME_SIMULATION = "cobis..sp_bv_simulador_DPFs";
	private static final String OPERATION_EXE_SIMULATION = "E";
	private static final String OPERATION_GET_DETAIL = "S";
	private static final String OPERATION_GET_CD_TYPE = "H";
	private static final String OPERATION_GET_PERIODICITY = "P";
	private static final String OPERATION_GET_TERM = "T";//
	private static final String OPERATION_GET_RATE = "C";//
	private static final int COL_INTEREST_ESTIMATED = 0;
	private static final int COL_INTEREST_ESTIMATED_TOTAL = 1;
	private static final int COL_INTEREST_PAY_DAY = 3;
	private static final int COL_NUMBER_OF_PAY = 5;
	private static final int COL_EFECTIVE_RATE = 6;
	private static final int COL_TYPE_DPF = 0;
	private static final int COL_DESCRIPTION1 = 1;
	private static final int COL_PAY_WAY = 2;
	private static final int COL_CAPITALIZE = 3;
	private static final int COL_CAL_BASE = 5;
	private static final int COL_AUTO_EXTENSION = 10;
	private static final int COL_GRACE_DAYS = 11;
	private static final int COL_NUM_GRACE_DAYS = 12;
	private static final int COL_TAX_RETENTION = 17;
	// Periodicity
	private static final int COL_VALUE = 0;
	private static final int COL_DESCRIPTION2 = 1;
	private static final int COL_FACTOR = 2;
	private static final int COL_PERCENTAJE = 3;
	private static final int COL_DAY_FACTOR = 4;
	// Rate
	private static final int COL_RATE = 0;
	private static final int COL_MAX_RATE = 1;
	private static final int COL_MIN_RATE = 2;
	private static final int COL_RATE_DESC = 3;
	private static final int COL_RATE_AUT = 4;
	private static final String SP_NAME_SIM_EXPIRATION = "cob_pfijo..sp_valida_fecha";
	// ExpirationDate
	private static final int COL_RESULT = 0;
	private static final int COL_ADDITIONALDAYS = 1;
	private static final int COL_PROCESSDATE = 2;
	private static final int COL_EXPIRATIONDATE = 3;
	private static final int COL_PROCESSDATEHOLD = 4;
	private static final int COL_EXPIRATIONDATEHOLD = 5;
	private static final int COL_TERMHOLD = 6;

	private static final int COL_EXPIRATIONDATEHOLD1 = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceTimeDepositConfig#getSimulationExpiration(com.cobiscorp.
	 * ecobis.ib.application.dtos.CertificateDepositCommonRequest)
	 */
	@Override
	public SimulationExpirationResponse getSimulationExpiration(SimulationExpirationRequest simulationExpiration)
			throws CTSServiceException, CTSInfrastructureException {
		
		if (logger.isInfoEnabled()) {            
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getSimulationExpiration");
		}

		IProcedureResponse pResponse = Execution(SP_NAME_SIM_EXPIRATION, simulationExpiration,
				"getSimulationExpiration");
		SimulationExpirationResponse aExpirationResponse = transformSimulationExpirationResponse(pResponse,
				"getDetailCertificateDeposit");
		return aExpirationResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceTimeDepositConfig#getDetailCertificateDeposit(com.cobiscorp.
	 * ecobis.ib.application.dtos.CertificateDepositCommonRequest)
	 */
	@Override
	public DetailCdResponse getDetailCertificateDeposit(CertificateDepositCommonRequest certificateDeposit)
			throws CTSServiceException, CTSInfrastructureException {
		
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getDetailCertificateDeposit");
		}

		IProcedureResponse pResponse = Execution(SP_NAME_SIMULATION, certificateDeposit, "getDetailCertificateDeposit");
		DetailCdResponse wdetailCdResponse = transformDetailCdResponse(pResponse, "getDetailCertificateDeposit");
		return wdetailCdResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceTimeDepositConfig#getCertificateDepositType(com.cobiscorp.
	 * ecobis.ib.application.dtos.CertificateDepositCommonRequest)
	 */
	@Override
	public CdTypeResponse getCertificateDepositType(CertificateDepositCommonRequest certificateDeposit)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getCertificateDepositType");
		}

		IProcedureResponse pResponse = Execution(SP_NAME_SIMULATION, certificateDeposit, "getCertificateDepositType");
		CdTypeResponse cdTypeResponse = transformDCdTypeResponse(pResponse, "getCertificateDepositType");
		return cdTypeResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceTimeDepositConfig#getCertificateDepositPeriodicity(com.
	 * cobiscorp.ecobis.ib.application.dtos.CertificateDepositCommonRequest)
	 */
	@Override
	public CdPeriodicityResponse getCertificateDepositPeriodicity(CertificateDepositCommonRequest certificateDeposit)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getCertificateDepositPeriodicity");
		}

		IProcedureResponse pResponse = Execution(SP_NAME_SIMULATION, certificateDeposit,
				"getCertificateDepositPeriodicity");
		CdPeriodicityResponse cdPeriodicityResponse = transformcdPeriodicityResponse(pResponse,
				"getCertificateDepositPeriodicity");
		return cdPeriodicityResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceTimeDepositConfig#executeSimulation(com.cobiscorp.ecobis.ib.
	 * application.dtos.CertificateDepositCommonRequest)
	 */
	@Override
	public CdSimulationResponse executeSimulation(CertificateDepositCommonRequest certificateDeposit)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: executeSimulation ");
		}
		IProcedureResponse pResponse = Execution(SP_NAME_SIMULATION, certificateDeposit, "executeSimulation");
		CdSimulationResponse wsimulationResponse = transformToSimulationResponse(pResponse, "executeSimulation");
		return wsimulationResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceTimeDepositConfig#getCertificateDepositTerm(com.cobiscorp.
	 * ecobis.ib.application.dtos.CertificateDepositCommonRequest)
	 */
	@Override
	public CertificateDepositResponse getCertificateDepositTerm(CertificateDepositCommonRequest certificateDeposit)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getCertificateDepositTerm");
		}
		IProcedureResponse pResponse = Execution(SP_NAME_SIMULATION, certificateDeposit, "getCertificateDepositTerm");
		CertificateDepositResponse wCertificateDepositResponse = transformTocdTermResponse(pResponse,
				"getCertificateDepositTerm");
		return wCertificateDepositResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceTimeDepositConfig#getCertificateDepositRate(com.cobiscorp.
	 * ecobis.ib.application.dtos.CertificateDepositCommonRequest)
	 */
	@Override
	public CdRateResponse getCertificateDepositRate(CertificateDepositCommonRequest certificateDeposit)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getCertificateDepositRate");
		}
		IProcedureResponse pResponse = Execution(SP_NAME_SIMULATION, certificateDeposit, "getCertificateDepositRate");
		CdRateResponse wCdRateResponse = transformTocdRateResponse(pResponse, "getCertificateDepositRate");
		return wCdRateResponse;
	}

	/**
	 * @param spName
	 * @param aIdentificationRequest
	 * @param string
	 * @return
	 */
	private IProcedureResponse Execution(String spName, CertificateDepositCommonRequest certificateRequest,
			String method) {
		IProcedureRequest request = initProcedureRequest(certificateRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800141");
		request.setSpName(spName);

		request.addInputParam("@i_nemonico", ICTSTypes.SQLVARCHAR,
				certificateRequest.getCertificateDeposit().getNemonic());

		if (method == "getDetailCertificateDeposit") {
			request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, OPERATION_GET_DETAIL);
		}
		;

		if (method == "getCertificateDepositType") {
			request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, OPERATION_GET_CD_TYPE);
		}
		;

		if (method == "getCertificateDepositPeriodicity") {
			request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, OPERATION_GET_PERIODICITY);
			request.addInputParam("@i_tipo_reg", ICTSTypes.SQLVARCHAR,
					certificateRequest.getCertificateDeposit().getRegType());
		}
		;

		if (method == "executeSimulation") {
			request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, OPERATION_EXE_SIMULATION);
			request.addInputParam("@i_monto", ICTSTypes.SQLFLTNi,
					certificateRequest.getCertificateDeposit().getAmount().toString());
			request.addInputParam("@i_plazo", ICTSTypes.SQLINT4,
					certificateRequest.getCertificateDeposit().getTerm().toString());
			request.addInputParam("@i_tasa", ICTSTypes.SQLFLTNi,
					String.valueOf(certificateRequest.getCertificateDeposit().getRate().getRate()));
			request.addInputParam("@i_moneda", ICTSTypes.SQLINT2,
					certificateRequest.getCertificateDeposit().getMoney().toString());
			request.addInputParam("@i_categoria", ICTSTypes.SQLVARCHAR,
					certificateRequest.getCertificateDeposit().getCategory());
			request.addInputParam("@i_fecha_valor", ICTSTypes.SQLDATETIME,
					certificateRequest.getCertificateDeposit().getProcessDate());
			request.addInputParam("@i_ente", ICTSTypes.SQLINT4,
					certificateRequest.getEntity().getCodCustomer().toString());
			request.addInputParam("@i_dia_pago", ICTSTypes.SQLINT1,
					certificateRequest.getCertificateDeposit().getPayDay().toString());
		}
		;

		if (method == "getCertificateDepositTerm") {
			request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, OPERATION_GET_TERM);
			request.addInputParam("@i_plazo", ICTSTypes.SQLINT4,
					certificateRequest.getCertificateDeposit().getTerm().toString());
			request.addInputParam("@i_fecha_valor", ICTSTypes.SQLDATETIME,
					certificateRequest.getCertificateDeposit().getProcessDate());//
			request.addInputParam("@i_fecha_ven", ICTSTypes.SQLDATETIME,
					certificateRequest.getCertificateDeposit().getExpiration());
			request.addInputParam("@i_fecha_plazo", ICTSTypes.SQLVARCHAR,
					certificateRequest.getCertificateDeposit().getTermDate());
		}
		;

		if (method == "getCertificateDepositRate") {
			request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, OPERATION_GET_RATE);
			request.addInputParam("@i_oficina", ICTSTypes.SQLVARCHAR,
					certificateRequest.getCertificateDeposit().getOffice());
			request.addInputParam("@i_monto", ICTSTypes.SQLFLT8i,
					certificateRequest.getCertificateDeposit().getAmount().toString());
			request.addInputParam("@i_plazo", ICTSTypes.SQLINT4,
					certificateRequest.getCertificateDeposit().getTerm().toString());
			request.addInputParam("@i_moneda", ICTSTypes.SQLINT2,
					certificateRequest.getCertificateDeposit().getMoney().toString());
			// ITO
			request.addInputParam("@i_tipo_reg", ICTSTypes.SQLVARCHAR,
					certificateRequest.getCertificateDeposit().getRegType());
		}
		;

		request.addOutputParam("@o_plazo", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_fecha_ven", ICTSTypes.SQLVARCHAR, "");

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request Corebanking: " + request.getProcedureRequestAsString());
		}

		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("	 *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response Validation Certificate Deposit*** ");
		}

		return pResponse;
	}

	/**
	 * @param spName
	 * @param ExpirationDate
	 * @param string
	 * @return
	 */
	private IProcedureResponse Execution(String spName, SimulationExpirationRequest aSimulationExpirationRequest,
			String Method) throws CTSServiceException, CTSInfrastructureException {
		IProcedureRequest request = initProcedureRequest(aSimulationExpirationRequest.getOriginalRequest());

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "14446");
		request.setSpName(spName);

		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "14446");
		request.addInputParam("@i_plazo", ICTSTypes.SQLINT4,
				aSimulationExpirationRequest.getCertificateDeposit().getTerm().toString());
		request.addInputParam("@i_fecha", ICTSTypes.SQLDATETIME,
				aSimulationExpirationRequest.getCertificateDeposit().getProcessDate());

		if (aSimulationExpirationRequest.getDateFormat() != null) {
			request.addInputParam("@i_formato_fecha", ICTSTypes.SQLINT4,
					aSimulationExpirationRequest.getDateFormat().toString());
		}
		if (aSimulationExpirationRequest.getCertificateDeposit().getCalendarDays() != null) {
			request.addInputParam("@i_dias_reales", ICTSTypes.SQLCHAR,
					aSimulationExpirationRequest.getCertificateDeposit().getCalendarDays().toString());
		}

		request.addOutputParam("@o_num_dias_labor", ICTSTypes.SQLINT4, "0");

		if (logger.isDebugEnabled()) {
			logger.logDebug("@i_plazo: " + aSimulationExpirationRequest.getCertificateDeposit().getTerm().toString());
			logger.logDebug(
					"@i_fecha: " + aSimulationExpirationRequest.getCertificateDeposit().getProcessDate().toString());
		}

		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug(
					"*** Response Validation Certificate Deposit: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response Validation Certificate Deposit*** ");
		}

		return pResponse;

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

	public CdSimulationResponse transformToSimulationResponse(IProcedureResponse procedureResponse, String methodName) {
		CdSimulationResponse simulationResponse = new CdSimulationResponse();
		List<CertificateDepositResult> listCertificateDeposit = new ArrayList<CertificateDepositResult>();
		IResultSetRow[] rowsSimulation = procedureResponse.getResultSet(1).getData().getRowsAsArray();
		if (methodName == "executeSimulation") {
			if (procedureResponse.getReturnCode() == 0) {
				for (int i = 0; i < rowsSimulation.length; i++) {
					CertificateDepositResult certificateDeposit = new CertificateDepositResult();
					IResultSetRow iResultSetRow = rowsSimulation[i];
					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

					certificateDeposit.setInterestEstimated(Double.valueOf(columns[COL_INTEREST_ESTIMATED].getValue()));
					certificateDeposit.setInterestEstimatedTotal(
							Double.valueOf(columns[COL_INTEREST_ESTIMATED_TOTAL].getValue()));
					certificateDeposit.setInterestPayDay(columns[COL_INTEREST_PAY_DAY].getValue());
					certificateDeposit.setNumberOfPayment(Integer.valueOf(columns[COL_NUMBER_OF_PAY].getValue()));
					certificateDeposit.setRate(Double.valueOf(columns[COL_EFECTIVE_RATE].getValue()));
					listCertificateDeposit.add(certificateDeposit);
				}

			}
			simulationResponse.setCertificateDepositResponse(new CertificateDepositResponse());
			simulationResponse.getCertificateDepositResponse()
					.setTerm(Integer.valueOf(procedureResponse.readValueParam("@o_plazo")));
			simulationResponse.getCertificateDepositResponse()
					.setExpirationDate(procedureResponse.readValueParam("@o_fecha_ven"));
			simulationResponse.setListCertificateDepositResult(listCertificateDeposit);
			simulationResponse.setSuccess(true);
		} else {
			Message[] message = Utils.returnArrayMessage(procedureResponse);
			simulationResponse.setMessages(message);
			simulationResponse.setSuccess(false);
		}
		;
		simulationResponse.setReturnCode(procedureResponse.getReturnCode());

		return simulationResponse;
	}

	/**
	 * @param pResponse
	 * @param string
	 * @return
	 */
	private SimulationExpirationResponse transformSimulationExpirationResponse(IProcedureResponse procedureResponse,
			String methodName) {

		SimulationExpirationResponse expirationResponse = new SimulationExpirationResponse();
		SimulationExpiration simulationExpiration = null;
		String dateHold = new String();

		if (procedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsDateHold = procedureResponse.getResultSet(1).getData().getRowsAsArray();
			expirationResponse.setSuccess(true);
			for (int i = 0; i < rowsDateHold.length; i++) {
				// ExpirationDate expirationDate = new ExpirationDate();
				IResultSetRow iResultSetRow = rowsDateHold[i];
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

				dateHold = columns[COL_EXPIRATIONDATEHOLD1].getValue().toString();
				// expirationDate.setExpirationDateHold(columns[COL_EXPIRATIONDATEHOLD1].getValue().toString());
				// listDatehold.add(expirationDate.getExpirationDateHold());
			}

			IResultSetRow[] rowsSimulation = procedureResponse.getResultSet(2).getData().getRowsAsArray();
			for (int i = 0; i < rowsSimulation.length; i++) {
				simulationExpiration = new SimulationExpiration();
				IResultSetRow iResultSetRow = rowsSimulation[i];
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

				simulationExpiration.setResult(columns[COL_RESULT].getValue().toString());
				simulationExpiration.setAdditionalDays(columns[COL_ADDITIONALDAYS].getValue() == null ? 0
						: Integer.parseInt(columns[COL_ADDITIONALDAYS].getValue()));
				simulationExpiration.setProcessDate(columns[COL_PROCESSDATE].getValue().toString());
				simulationExpiration.setExpirationDate(columns[COL_EXPIRATIONDATE].getValue().toString());
				simulationExpiration.setProcessDateHold(columns[COL_PROCESSDATEHOLD].getValue().toString());
				simulationExpiration.setExpirationDateHold(columns[COL_EXPIRATIONDATEHOLD].getValue().toString());
				simulationExpiration.setTermHold(columns[COL_TERMHOLD].getValue() == null ? 0
						: Integer.parseInt(columns[COL_TERMHOLD].getValue()));
			}

			simulationExpiration
					.setNumberOfLaborsDays(Integer.valueOf(procedureResponse.readValueParam("@o_num_dias_labor")));

			expirationResponse.setSimulationExpiration(simulationExpiration);
			expirationResponse.setExpirationDate(dateHold);
		} else {
			Message[] message = Utils.returnArrayMessage(procedureResponse);
			expirationResponse.setMessages(message);
			expirationResponse.setSuccess(false);
		}
		;
		expirationResponse.setReturnCode(procedureResponse.getReturnCode());

		// expirationResponse.setSimulationExpiration(new
		// SimulationExpirationResponse());

		return expirationResponse;
	}

	/**
	 * @param pResponse
	 * @param string
	 * @return
	 */
	private DetailCdResponse transformDetailCdResponse(IProcedureResponse procedureResponse, String methodName) {
		DetailCdResponse detailResponse = new DetailCdResponse();
		List<CertificateDeposit> listCertificateDeposit = new ArrayList<CertificateDeposit>();
		if (procedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsSimulation = procedureResponse.getResultSet(1).getData().getRowsAsArray();
			for (int i = 0; i < rowsSimulation.length; i++) {
				CertificateDeposit certificateDeposit = new CertificateDeposit();
				IResultSetRow iResultSetRow = rowsSimulation[i];
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

				certificateDeposit.setType(columns[COL_TYPE_DPF].getValue());
				certificateDeposit.setNemonic(columns[COL_DESCRIPTION1].getValue());
				certificateDeposit.setMethodOfPayment(columns[COL_PAY_WAY].getValue());
				certificateDeposit.setCapitalize(columns[COL_CAPITALIZE].getValue());
				certificateDeposit.setCalculationBase(columns[COL_CAL_BASE].getValue());
				certificateDeposit.setExtendedAut(columns[COL_AUTO_EXTENSION].getValue());
				certificateDeposit.setGraceDays(columns[COL_GRACE_DAYS].getValue());
				certificateDeposit.setGraceDaysNum(columns[COL_NUM_GRACE_DAYS].getValue());
				certificateDeposit.setTaxRetention(columns[COL_TAX_RETENTION].getValue());

				listCertificateDeposit.add(certificateDeposit);
			}
			detailResponse.setListCertificateDeposit(listCertificateDeposit);
			detailResponse.setCertificateDepositResponse(new CertificateDepositResponse());
			detailResponse.getCertificateDepositResponse()
					.setTerm(Integer.valueOf(procedureResponse.readValueParam("@o_plazo")));
			detailResponse.getCertificateDepositResponse()
					.setExpirationDate(procedureResponse.readValueParam("@o_fecha_ven"));
			detailResponse.setSuccess(true);
		} else {
			Message[] message = Utils.returnArrayMessage(procedureResponse);
			detailResponse.setMessages(message);
			detailResponse.setSuccess(false);
		}
		;
		detailResponse.setReturnCode(procedureResponse.getReturnCode());
		return detailResponse;
	}

	private CdTypeResponse transformDCdTypeResponse(IProcedureResponse procedureResponse, String methosName) {
		CdTypeResponse cdTypeResponse = new CdTypeResponse();
		List<Type> listType = new ArrayList<Type>();
		List<Parameters> listParameters = new ArrayList<Parameters>();
		List<Category> listCategory = new ArrayList<Category>();
		if (procedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsType = procedureResponse.getResultSet(1).getData().getRowsAsArray();
			IResultSetRow[] rowsParameter = procedureResponse.getResultSet(2).getData().getRowsAsArray();
			IResultSetRow[] rowsCategory = procedureResponse.getResultSet(3).getData().getRowsAsArray();
			for (int i = 0; i < rowsType.length; i++) {
				Type type = new Type();
				IResultSetRow iResultSetRow = rowsType[i];
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

				type.setType(columns[COL_TYPE_DPF].getValue());

				listType.add(type);
			}

			for (int i = 0; i < rowsParameter.length; i++) {
				Parameters parameter = new Parameters();
				IResultSetRow iResultSetRow = rowsParameter[i];
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

				parameter.setName(columns[COL_VALUE].getValue());

				listParameters.add(parameter);
			}

			for (int i = 0; i < rowsCategory.length; i++) {
				Category category = new Category();
				IResultSetRow iResultSetRow = rowsCategory[i];
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

				category.setName(columns[COL_VALUE].getValue());

				listCategory.add(category);
			}

			cdTypeResponse.setListCategory(listCategory);
			cdTypeResponse.setListCdType(listType);
			cdTypeResponse.setListParameters(listParameters);

			cdTypeResponse.setCertificateDepositResponse(new CertificateDepositResponse());
			cdTypeResponse.getCertificateDepositResponse()
					.setTerm(Integer.valueOf(procedureResponse.readValueParam("@o_plazo")));
			cdTypeResponse.getCertificateDepositResponse()
					.setExpirationDate(procedureResponse.readValueParam("@o_fecha_ven"));
			cdTypeResponse.setSuccess(true);
		} else {
			Message[] message = Utils.returnArrayMessage(procedureResponse);
			cdTypeResponse.setMessages(message);
			cdTypeResponse.setSuccess(false);
		}
		;
		cdTypeResponse.setReturnCode(procedureResponse.getReturnCode());

		//
		return cdTypeResponse;
	};

	/**
	 * @param pResponse
	 * @param string
	 * @return
	 */
	private CdPeriodicityResponse transformcdPeriodicityResponse(IProcedureResponse procedureResponse,
			String methodName) {
		CdPeriodicityResponse cdPeriodicityResponse = new CdPeriodicityResponse();
		List<Periodicity> listPeriodicity = new ArrayList<Periodicity>();
		if (procedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsPeriodicity = procedureResponse.getResultSet(1).getData().getRowsAsArray();
			for (int i = 0; i < rowsPeriodicity.length; i++) {
				Periodicity periodicity = new Periodicity();
				IResultSetRow iResultSetRow = rowsPeriodicity[i];
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

				periodicity.setValue(columns[COL_VALUE].getValue());
				periodicity.setDescription(columns[COL_DESCRIPTION2].getValue());
				periodicity.setFactor(columns[COL_FACTOR].getValue());
				periodicity.setPercentage(columns[COL_PERCENTAJE].getValue());
				if (columns.length == 5)
					periodicity.setDaysFactor(columns[COL_DAY_FACTOR].getValue());
				else {
					int dayFactor = Integer.parseInt(columns[COL_FACTOR].getValue()) * 30;
					periodicity.setDaysFactor(String.valueOf(dayFactor));
				}

				listPeriodicity.add(periodicity);
			}

			cdPeriodicityResponse.setListPeriodicity(listPeriodicity);

			cdPeriodicityResponse.setCertificateDepositResponse(new CertificateDepositResponse());
			cdPeriodicityResponse.getCertificateDepositResponse()
					.setTerm(Integer.valueOf(procedureResponse.readValueParam("@o_plazo")));
			cdPeriodicityResponse.getCertificateDepositResponse()
					.setExpirationDate(procedureResponse.readValueParam("@o_fecha_ven"));
			cdPeriodicityResponse.setSuccess(true);
		} else {
			cdPeriodicityResponse.setSuccess(false);
			Message[] message = Utils.returnArrayMessage(procedureResponse);
			cdPeriodicityResponse.setMessages(message);
		}
		;

		cdPeriodicityResponse.setReturnCode(procedureResponse.getReturnCode());

		//
		return cdPeriodicityResponse;
	};

	/*********************
	 * Transformación de Response a CertificateDepositResponse
	 ***********************/
	private CertificateDepositResponse transformTocdTermResponse(IProcedureResponse procedureResponse,
			String methodName) {
		CertificateDepositResponse wCertificateDepositResponse = new CertificateDepositResponse();
		if (procedureResponse.getReturnCode() == 0) {
			wCertificateDepositResponse.setTerm(procedureResponse.readValueParam("@o_plazo") == null ? 0
					: Integer.valueOf(procedureResponse.readValueParam("@o_plazo")));
			wCertificateDepositResponse.setExpirationDate(procedureResponse.readValueParam("@o_fecha_ven"));
		} else {
			Message[] message = Utils.returnArrayMessage(procedureResponse);
			wCertificateDepositResponse.setMessages(message);
			wCertificateDepositResponse.setSuccess(false);
		}
		;
		wCertificateDepositResponse.setReturnCode(procedureResponse.getReturnCode());

		//
		return wCertificateDepositResponse;
	};

	/*********************
	 * Transformación de Response a CdRateResponse
	 ***********************/
	private CdRateResponse transformTocdRateResponse(IProcedureResponse procedureResponse, String methodName) {
		CdRateResponse wCdRateResponse = new CdRateResponse();
		List<Rate> listRate = new ArrayList<Rate>();
		if (procedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsRate = procedureResponse.getResultSet(1).getData().getRowsAsArray();
			for (int i = 0; i < rowsRate.length; i++) {
				Rate wRate = new Rate();
				IResultSetRow iResultSetRow = rowsRate[i];
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

				wRate.setRate(Double.valueOf(columns[COL_RATE].getValue()));
				wRate.setMaxRate(Double.valueOf(columns[COL_MAX_RATE].getValue()));
				wRate.setMinRate(Double.valueOf(columns[COL_MIN_RATE].getValue()));
				wRate.setRateDesc(columns[COL_RATE_DESC].getValue());
				wRate.setRateAuthorization((columns[COL_RATE_AUT].getValue()));
				listRate.add(wRate);
			}

			wCdRateResponse.setListRate(listRate);

			wCdRateResponse.setCertificateDepositResponse(new CertificateDepositResponse());
			wCdRateResponse.getCertificateDepositResponse()
					.setTerm(Integer.valueOf(procedureResponse.readValueParam("@o_plazo")));
			wCdRateResponse.getCertificateDepositResponse()
					.setExpirationDate(procedureResponse.readValueParam("@o_fecha_ven"));
			wCdRateResponse.setSuccess(true);
		} else {
			wCdRateResponse.setSuccess(false);
			wCdRateResponse.setMessages(Utils.returnArrayMessage(procedureResponse));
		}
		;
		wCdRateResponse.setReturnCode(procedureResponse.getReturnCode());

		//
		return wCdRateResponse;
	}

}
