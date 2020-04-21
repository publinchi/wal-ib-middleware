package com.cobiscorp.ecobis.orchestration.core.ib.timedeposits;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositCatalogRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositCatalogResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsHistoricalsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsHistoricalsResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsMovementsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsMovementsResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsPayableInterestsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsPayableInterestsResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsPaymentDetailScheduleRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsPaymentDetailScheduleResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsPaymentScheduleRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsPaymentScheduleResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BalanceDetailPayment;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.LoanAmortization;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.TimeDeposit;
import com.cobiscorp.ecobis.ib.orchestration.dtos.TimeDepositCatalog;
import com.cobiscorp.ecobis.ib.orchestration.dtos.TimeDepositsHistoricals;
import com.cobiscorp.ecobis.ib.orchestration.dtos.TimeDepositsMovements;
import com.cobiscorp.ecobis.ib.orchestration.dtos.TimeDepositsPayableInterests;
import com.cobiscorp.ecobis.ib.orchestration.dtos.TimeDepositsPaymentDetailSchedule;
import com.cobiscorp.ecobis.ib.orchestration.dtos.TimeDepositsPaymentSchedule;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceTimeDeposits;

@Component(name = "TimeDepositQuery", immediate = false)
@Service(value = { ICoreServiceTimeDeposits.class })
@Properties(value = { @Property(name = "service.description", value = "TimeDepositQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TimeDepositQuery") })
public class TimeDepositQuery extends SPJavaOrchestrationBase implements ICoreServiceTimeDeposits {

	private static ILogger logger = LogFactory.getLogger(TimeDepositQuery.class);
	private static final String SP_NAME = "cob_pfijo..sp_tr14_cons_cdts";
	private static final int COL_PRODUCT_NUMBER = 0;
	private static final int COL_OPENNING_DATE = 1;
	private static final int COL_EXPIRATION_DATE = 2;
	private static final int COL_CAPITAL_BALANCE = 3;
	private static final int COL_TOTAL_INTEREST_INCOME = 4;
	private static final int COL_RATE = 5;
	private static final int COL_TERM = 6;
	private static final int COL_CAPITAL_BALANCE_MATURI = 7;
	private static final int COL_AUTOMATIC_RENEWAL = 8;
	private static final int COL_IS_COMPOUNDED = 9;
	private static final int COL_FRECUENCY_OF_PAYMENT = 10;
	private static final int COL_ACOOUNT_OFFICER = 11;
	// private static final int COL_VALUE_DATE = 12;
	private static final int COL_CALCULATION_BASE = 12;
	private static final int COL_PRODUCT_ABBREVIATION = 13;
	private static final int COL_PRODUCT_ALIAS = 14;

	private static final String SP_NAME_MOVEMENT = "cob_pfijo..sp_consulta_mov_mon";
	private static final int COL_DATE = 0;
	private static final int COL_TRANSACTION_NAME = 1;
	private static final int COL_PAY_FORMAT = 2;
	private static final int COL_CURRENCY = 3;
	private static final int COL_INTERNATIONAL_AMOUNT = 4;
	private static final int COL_AMOUNT = 5;
	private static final int COL_STATUS = 6;
	private static final int COL_SEQUENCE = 7;
	private static final int COL_ACCOUNT = 8;
	private static final int COL_BENEFICIARY = 9;
	private static final int COL_VALUE_DATE1 = 10;
	private static final int COL_TRANSACTION_NUMBER = 12;
	private static final int COL_SUBSEQUENCE = 13;
	private static final String SP_NAME_CATALOG = "cobis..sp_catalogo";
	private static final String SP_NAME_CATALOG_TIMEDEPOSITE = "cobis..sp_bv_tipo_deposito";
	private static final int COL_CODE = 0;
	private static final int COL_CATALOG = 1;
	private static final String SP_NAME_HISTORICALS = "cob_pfijo..sp_conhis";
	private static final int COL_SEQUENCE_HIST = 0;
	private static final int COL_COUPON = 1;
	private static final int COL_TRANSACTIONDATE = 2;
	private static final int COL_TRANSACTIONCODE = 3;
	private static final int COL_DESCRIPTION = 4;
	private static final int COL_VALUE = 5;
	private static final int COL_OBSERVATION = 6;
	private static final int COL_FUNCTIONARY = 7;
	private static final int COL_RATE_HIST = 8;
	private static final String SP_NAME_PAYMENT_SCHEDULE = "cob_pfijo..sp_cons_cuota";
	private static final int COL_QUOTA = 0;
	private static final int COL_PAYMENT_DATE = 1;
	private static final int COL_QUOTA_AMOUNT = 2;
	private static final int COL_ENTITY = 3;
	private static final int COL_OPERATION_DESCRIPTION = 4;
	private static final int COL_ADDRESS_DESCRIPTION = 5;
	private static final int COL_OFFICE_NAME = 6;
	private static final int COL_BANK_NUMBER_OPERATION = 7;
	private static final int COL_DEPOSIT_TYPE_DESCRIPTION = 8;
	private static final int COL_AMOUNT1 = 9;
	private static final int COL_PAYMENT_DESCRIPTION = 10;
	private static final int COL_CURRENCY1 = 11;
	private static final int COL_RATE1 = 12;
	private static final int COL_EXPIRATION_DATE1 = 13;
	private static final int COL_STATUS1 = 14;
	private static final int COL_OPERATION_DAYS_NUMBER = 15;
	private static final int COL_INSERT_DATE = 16;
	private static final int COL_QUOTA_VALUE = 17;
	private static final int COL_QUOTA_DAYS_NUMBER = 18;
	private static final int COL_LAST_PAYMENT_DATE = 19;
	private static final int COL_INTEREST_EARNED = 20;
	private static final String SP_NAME_PAYABLEINTERESTS = "cobis..sp_bv_consulta_cuotas_dpf";
	private static final int COL_PAY_NUMBER = 0;
	private static final int COL_PRE_PRINT_NUMBER = 1;
	private static final int COL_EXPIRATION_DATE_P = 2;
	private static final int COL_APPROXIMATE_VALUE = 10;
	private static final int COL_VALUE_P = 3;
	private static final int COL_TAX = 11;
	private static final int COL_DATE_BOX = 4;
	private static final int COL_STATUS_P = 5;
	private static final int COL_PRINT_NUMBER = 6;
	private static final int COL_DETAINED = 7;
	private static final int COL_COUPON_NUMBER = 8;
	private static final int COL_CURRENCY_P = 9;
	private static final int COL_INITIAL_DATE = 12;
	private static final String SP_NAME_PAYMENT_DETAIL_SCHEDULE = "cob_pfijo..sp_consulta_oper";
	private static final int COL_MONT = 53;
	private static final int COL_RATE_DETAIL = 12;
	private static final int COL_COMPOUNDED = 19;
	private static final int COL_PAYMENTTYPE = 20;
	private static final int COL_PAYDAY = 39;
	private static final int COL_STATUS_DETAIL = 24;
	private static final int COL_BASECALCULATE = 100;
	private static final int COL_EARNEDINTEREST = 13;
	private static final int COL_AMOUNTPAIEDINTEREST = 9;
	private static final int COL_EXPIRATEDATE = 30;
	private static final int COL_ENTITY_DETAIL = 1;
	private static final int COL_VALUEDATE = 29;
	private static final int COL_DAYSNUMBER = 7;
	private static final int COL_REALDAYS = 4;

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
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceTimeDeposits
	 * #getTimeDepositDetail(com.cobiscorp.ecobis.ib.application.dtos.
	 * TimeDepositRequest)
	 */
	@Override
	public TimeDepositResponse getTimeDepositDetail(TimeDepositRequest aTimeDepositRequest)
			throws CTSServiceException, CTSInfrastructureException {
		// TODO Auto-generated method stub
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getTimeDepositDetail");
		}

		IProcedureResponse pResponse = Execution(SP_NAME, aTimeDepositRequest, "getTimeDepositDetail");
		TimeDepositResponse timeDepositResponse = transformToTimeDepositResponse(pResponse, "getTimeDepositDetail");
		return timeDepositResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceTimeDeposits
	 * #getTimeDepositMovements(com.cobiscorp.ecobis.ib.application.dtos.
	 * TimeDepositsMovementsRequest)
	 */
	@Override
	public TimeDepositsMovementsResponse getTimeDepositMovements(TimeDepositsMovementsRequest timeDepositMovement)
			throws CTSServiceException, CTSInfrastructureException {
		// TODO Auto-generated method stub

		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getTimeDepositMovements");
		}
		IProcedureResponse pResponseCatalog = ExecutionCatalog(SP_NAME_CATALOG, timeDepositMovement);
		Map<String, String> map = transformToMap(pResponseCatalog);

		IProcedureResponse pResponse = Execution(SP_NAME_MOVEMENT, timeDepositMovement, "getTimeDepositMovements");
		TimeDepositsMovementsResponse timeDepositsMovementsResp = transformToTimeDepositsMovementsResponse(pResponse,
				"getTimeDepositMovements", map);
		return timeDepositsMovementsResp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceTimeDeposits
	 * #getTimeDepositMovements(com.cobiscorp.ecobis.ib.application.dtos.
	 * TimeDepositsMovementsRequest)
	 */
	@Override
	public TimeDepositsHistoricalsResponse getTimeDepositHistoricals(
			TimeDepositsHistoricalsRequest timeDepositHistorical)
			throws CTSServiceException, CTSInfrastructureException {
		// TODO Auto-generated method stub

		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getTimeDepositHistoricals");
		}

		IProcedureResponse pResponse = Execution(SP_NAME_HISTORICALS, timeDepositHistorical,
				"getTimeDepositHistoricals");
		TimeDepositsHistoricalsResponse timeDepositsHistoricalsResp = transformToTimeDepositsHistoricalsResponse(
				pResponse, "getTimeDepositHistoricals");
		return timeDepositsHistoricalsResp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceTimeDeposits
	 * #getTimeDepositsPaymentSchedule(com.cobiscorp.ecobis.ib.application.dtos.
	 * TimeDepositsMovementsRequest)
	 */
	@Override
	public TimeDepositsPaymentScheduleResponse getTimeDepositsPaymentSchedule(
			TimeDepositsPaymentScheduleRequest timeDepositsPaymentSchedule)
			throws CTSServiceException, CTSInfrastructureException {
		// TODO Auto-generated method stub
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getTimeDepositsPaymentSchedule");
		}
		IProcedureResponse pResponse = Execution(SP_NAME_PAYMENT_SCHEDULE, timeDepositsPaymentSchedule,
				"getTimeDepositsPaymentSchedule");
		TimeDepositsPaymentScheduleResponse TimeDepositsPaymentScheduleResp = transformToTimeDepositsPaymentScheduleResponse(
				pResponse, "getTimeDepositsPaymentSchedule");
		return TimeDepositsPaymentScheduleResp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceTimeDeposits
	 * #
	 * getTimeDepositsPayableInterests(com.cobiscorp.ecobis.ib.application.dtos.
	 * TimeDepositsPayableInterestsRequest)
	 */
	@Override
	public TimeDepositsPayableInterestsResponse getTimeDepositsPayableInterests(
			TimeDepositsPayableInterestsRequest timeDepositPayableInterests)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getTimeDepositsPayableInterests");
		}
		IProcedureResponse pResponse = Execution(SP_NAME_PAYABLEINTERESTS, timeDepositPayableInterests,
				"getTimeDepositsPayableInterests");
		TimeDepositsPayableInterestsResponse timeDepositPayableResponse = transformToTimeDepositPayableResponse(
				pResponse, "getTimeDepositsPayableInterests");
		return timeDepositPayableResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceTimeDeposits
	 * #
	 * getTimeDepositsPaymentDetailSchedule(com.cobiscorp.ecobis.ib.application.
	 * dtos.TimeDepositsPaymentDetailScheduleRequest)
	 */
	@Override
	public TimeDepositsPaymentDetailScheduleResponse getTimeDepositsPaymentDetailSchedule(
			TimeDepositsPaymentDetailScheduleRequest timeDepositsPaymentDetailSchedule)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getTimeDepositsPaymentDetailSchedule");
		}
		IProcedureResponse pResponse = Execution(SP_NAME_PAYMENT_DETAIL_SCHEDULE, timeDepositsPaymentDetailSchedule,
				"getTimeDepositsPaymentDetailSchedule");
		TimeDepositsPaymentDetailScheduleResponse TimeDepositsPaymentDetailScheduleResp = null;

		if (timeDepositsPaymentDetailSchedule.getNext().equals("N")) {

			TimeDepositsPaymentDetailScheduleResp = transformToTimeDepositsPaymentDetailScheduleResponse(pResponse,
					"getTimeDepositsPaymentDetailSchedule");
		} else if (timeDepositsPaymentDetailSchedule.getNext().equals("S")) {

			TimeDepositsPaymentDetailScheduleResp = transformToTimeDepositsPaymentDetailScheduleResponse2(pResponse,
					"getTimeDepositsPaymentDetailSchedule");
		}

		return TimeDepositsPaymentDetailScheduleResp;
	}

	/**
	 * @param spNamePayableInterests
	 * @param timeDepositPayableInterests
	 * @param method
	 * @return
	 */
	private IProcedureResponse Execution(String spNamePaymentDetailSchedule,
			TimeDepositsPaymentDetailScheduleRequest timeDepositsPaymentDetailScheduleRequest, String method)
			throws CTSServiceException, CTSInfrastructureException {

		IProcedureRequest request = initProcedureRequest(timeDepositsPaymentDetailScheduleRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "14158");
		request.setSpName(spNamePaymentDetailSchedule);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "14158");

		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "Q");
		request.addInputParam("@i_formato_fecha", ICTSTypes.SQLINT4,
				timeDepositsPaymentDetailScheduleRequest.getDateFormat().toString());
		request.addInputParam("@i_cuenta", ICTSTypes.SQLVARCHAR,
				timeDepositsPaymentDetailScheduleRequest.getProduct().getProductNumber());

		if (logger.isDebugEnabled()) {
			logger.logDebug("@i_cuenta: " + timeDepositsPaymentDetailScheduleRequest.getProduct().getProductNumber());
			logger.logDebug("@i_formato_fecha: " + timeDepositsPaymentDetailScheduleRequest.getDateFormat().toString());

		}
		if (logger.isDebugEnabled()) {
			logger.logDebug("Request Corebanking: " + request.getProcedureRequestAsString());
		}

		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug(
					"*** Response TimeDepositsPaymentDetailSchedule: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response TimeDepositsPaymentDetailSchedule*** ");
		}

		return pResponse;
	}

	/**
	 * @param pResponse
	 * @param string
	 * @return
	 */
	private TimeDepositsPaymentDetailScheduleResponse transformToTimeDepositsPaymentDetailScheduleResponse(
			IProcedureResponse pResponse, String method) {
		TimeDepositsPaymentDetailScheduleResponse depositsPaymentDetailScheduleResponse = new TimeDepositsPaymentDetailScheduleResponse();
		TimeDepositsPaymentDetailSchedule depositsPaymentDetailSchedule = new TimeDepositsPaymentDetailSchedule();
		List<TimeDepositsPaymentDetailSchedule> depositsPaymentDetailScheduleList = null;

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ANTES DE ProcedureResponse: ***" + pResponse);
			logger.logDebug("*** ProcedureResponse: ***" + pResponse.getProcedureResponseAsString());
		}
		IResultSetRow[] rowsTimeDepositPayable = pResponse.getResultSet(1).getData().getRowsAsArray();
		if (method.equals("getTimeDepositsPaymentDetailSchedule")) {
			depositsPaymentDetailScheduleList = new ArrayList<TimeDepositsPaymentDetailSchedule>();

			for (int i = 0; i < rowsTimeDepositPayable.length; i++) {
				IResultSetRow iResultSetRow = rowsTimeDepositPayable[i];
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

				depositsPaymentDetailSchedule.setMonth(
						columns[COL_MONT].getValue() == null ? 0 : Integer.parseInt(columns[COL_MONT].getValue()));
				depositsPaymentDetailSchedule.setRate(columns[COL_RATE_DETAIL].getValue() == null ? 0
						: Double.parseDouble(columns[COL_RATE_DETAIL].getValue()));
				depositsPaymentDetailSchedule.setCompounded(columns[COL_COMPOUNDED].getValue());
				depositsPaymentDetailSchedule.setPaymentType(columns[COL_PAYMENTTYPE].getValue());
				depositsPaymentDetailSchedule.setPayDay(
						columns[COL_PAYDAY].getValue() == null ? 0 : Integer.parseInt(columns[COL_PAYDAY].getValue()));
				depositsPaymentDetailSchedule.setStatus(columns[COL_STATUS_DETAIL].getValue());
				depositsPaymentDetailSchedule.setBaseCalculate(columns[COL_BASECALCULATE].getValue() == null ? 0
						: Integer.parseInt(columns[COL_BASECALCULATE].getValue()));
				depositsPaymentDetailSchedule.setEarnedInterest(columns[COL_EARNEDINTEREST].getValue() == null ? 0
						: Double.parseDouble(columns[COL_EARNEDINTEREST].getValue()));
				depositsPaymentDetailSchedule.setAmountPaiedInterest(columns[COL_AMOUNTPAIEDINTEREST].getValue() == null
						? 0 : Double.parseDouble(columns[COL_AMOUNTPAIEDINTEREST].getValue()));
				depositsPaymentDetailSchedule.setExpirateDate(columns[COL_EXPIRATEDATE].getValue());
				depositsPaymentDetailSchedule.setEntity(columns[COL_ENTITY_DETAIL].getValue() == null ? 0
						: Integer.parseInt(columns[COL_ENTITY_DETAIL].getValue()));
				depositsPaymentDetailSchedule.setValueDate(columns[COL_VALUEDATE].getValue());
				depositsPaymentDetailSchedule.setDaysNumber(columns[COL_DAYSNUMBER].getValue() == null ? 0
						: Integer.parseInt(columns[COL_DAYSNUMBER].getValue()));
				// depositsPaymentDetailSchedule.setRate(0.0);
				depositsPaymentDetailScheduleList.add(depositsPaymentDetailSchedule);
			}
		}

		depositsPaymentDetailScheduleResponse.setReturnCode(pResponse.getReturnCode());

		Message[] message = Utils.returnArrayMessage(pResponse);
		depositsPaymentDetailScheduleResponse.setMessages(message);

		depositsPaymentDetailScheduleResponse.setDepositsPaymentDetailSchedule(depositsPaymentDetailScheduleList);
		return depositsPaymentDetailScheduleResponse;
	}

	/**
	 * @param pResponse
	 * @param string
	 * @return
	 */
	private TimeDepositsPaymentDetailScheduleResponse transformToTimeDepositsPaymentDetailScheduleResponse2(
			IProcedureResponse pResponse, String method) {
		TimeDepositsPaymentDetailScheduleResponse depositsPaymentDetailScheduleResponse = new TimeDepositsPaymentDetailScheduleResponse();
		TimeDepositsPaymentDetailSchedule depositsPaymentDetailSchedule = new TimeDepositsPaymentDetailSchedule();
		List<TimeDepositsPaymentDetailSchedule> depositsPaymentDetailScheduleList = null;
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse2: ***" + pResponse.getProcedureResponseAsString());
		}
		IResultSetRow[] rowsTimeDepositPayable = pResponse.getResultSet(1).getData().getRowsAsArray();
		if (method.equals("getTimeDepositsPaymentDetailSchedule")) {
			depositsPaymentDetailScheduleList = new ArrayList<TimeDepositsPaymentDetailSchedule>();

			for (int i = 0; i < rowsTimeDepositPayable.length; i++) {
				IResultSetRow iResultSetRow = rowsTimeDepositPayable[i];
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				depositsPaymentDetailSchedule.setRealDays(columns[COL_REALDAYS].getValue());

				depositsPaymentDetailScheduleList.add(depositsPaymentDetailSchedule);
			}
		}

		depositsPaymentDetailScheduleResponse.setReturnCode(pResponse.getReturnCode());

		Message[] message = Utils.returnArrayMessage(pResponse);
		depositsPaymentDetailScheduleResponse.setMessages(message);

		depositsPaymentDetailScheduleResponse.setDepositsPaymentDetailSchedule(depositsPaymentDetailScheduleList);
		return depositsPaymentDetailScheduleResponse;
	}

	// ******************************************
	/**
	 * @param spNamePayableInterests
	 * @param timeDepositPayableInterests
	 * @param method
	 * @return
	 */
	private IProcedureResponse Execution(String spNamePayableInterests,
			TimeDepositsPayableInterestsRequest timeDepositPayableInterests, String method)
			throws CTSServiceException, CTSInfrastructureException {

		IProcedureRequest request = initProcedureRequest(timeDepositPayableInterests.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "14452");
		request.setSpName(spNamePayableInterests);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "14452");

		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "S");
		request.addInputParam("@i_formato_fecha", ICTSTypes.SQLINT4,
				timeDepositPayableInterests.getDateFormatId().toString());
		request.addInputParam("@i_cuenta", ICTSTypes.SQLVARCHAR,
				timeDepositPayableInterests.getProductNumber().getProductNumber());
		request.addInputParam("@i_cuota", ICTSTypes.SQLINT4,
				timeDepositPayableInterests.getSequential().getSecuential());
		request.addInputParam("@i_modo", ICTSTypes.SQLINT4, "1");

		if (logger.isDebugEnabled()) {
			logger.logDebug("@i_cuenta: " + timeDepositPayableInterests.getProductNumber().getProductNumber());
			logger.logDebug("@i_cuota: " + timeDepositPayableInterests.getSequential().getSecuential());
			logger.logDebug("@i_formato_fecha: " + timeDepositPayableInterests.getDateFormatId().toString());

		}
		if (logger.isDebugEnabled()) {
			logger.logDebug("Request Corebanking: " + request.getProcedureRequestAsString());
		}

		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug(
					"*** Response TimeDepositsPayableInterests: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response TimeDepositsPayableInterests*** ");
		}

		return pResponse;
	}

	/**
	 * @param pResponse
	 * @param string
	 * @return
	 */
	private TimeDepositsPayableInterestsResponse transformToTimeDepositPayableResponse(IProcedureResponse pResponse,
			String method) {
		TimeDepositsPayableInterestsResponse depositsPayableResponse = new TimeDepositsPayableInterestsResponse();
		TimeDepositsPayableInterests depositsPayableInterests = null;
		List<TimeDepositsPayableInterests> depositsPayableInterestsList = null;

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + pResponse.getProcedureResponseAsString());
		}
		IResultSetRow[] rowsTimeDepositPayable = pResponse.getResultSet(1).getData().getRowsAsArray();
		if (method.equals("getTimeDepositsPayableInterests")) {
			depositsPayableInterestsList = new ArrayList<TimeDepositsPayableInterests>();
			for (int i = 0; i < rowsTimeDepositPayable.length; i++) {
				IResultSetRow iResultSetRow = rowsTimeDepositPayable[i];
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				depositsPayableInterests = new TimeDepositsPayableInterests();
				BalanceDetailPayment balanceDetailPayment = new BalanceDetailPayment();
				Currency currency = new Currency();
				LoanAmortization tax = new LoanAmortization();

				depositsPayableInterests.setPayNumber(columns[COL_PAY_NUMBER].getValue() == null ? 0
						: Integer.parseInt(columns[COL_PAY_NUMBER].getValue()));
				depositsPayableInterests.setPrePrintNumber(columns[COL_PRE_PRINT_NUMBER].getValue() == null ? 0
						: Integer.parseInt(columns[COL_PRE_PRINT_NUMBER].getValue()));

				balanceDetailPayment.setExpirationDate(columns[COL_EXPIRATION_DATE_P].getValue());
				depositsPayableInterests.setApproximateValue(columns[COL_APPROXIMATE_VALUE].getValue() == null ? 0
						: Double.parseDouble(columns[COL_APPROXIMATE_VALUE].getValue()));
				depositsPayableInterests.setValue(columns[COL_VALUE_P].getValue() == null ? 0
						: Double.parseDouble(columns[COL_VALUE_P].getValue()));
				tax.setTax(columns[COL_TAX].getValue() == null ? 0 : Double.parseDouble(columns[COL_TAX].getValue()));
				depositsPayableInterests.setTax(tax);
				depositsPayableInterests.setDateBox(columns[COL_DATE_BOX].getValue());
				balanceDetailPayment.setStatus(columns[COL_STATUS_P].getValue());
				depositsPayableInterests.setPrintNumber(columns[COL_PRINT_NUMBER].getValue() == null ? 0
						: Integer.parseInt(columns[COL_PRINT_NUMBER].getValue()));
				depositsPayableInterests.setDetained(columns[COL_DETAINED].getValue());
				depositsPayableInterests.setCouponNumber(columns[COL_COUPON_NUMBER].getValue());
				currency.setCurrencyDescription(columns[COL_CURRENCY_P].getValue());
				depositsPayableInterests.setCurrency(currency);
				balanceDetailPayment.setInitialDate(columns[COL_INITIAL_DATE].getValue());
				depositsPayableInterests.setBalanceDetailPayment(balanceDetailPayment);

				depositsPayableInterestsList.add(depositsPayableInterests);
			}
		}

		depositsPayableResponse.setReturnCode(pResponse.getReturnCode());
		Message[] message = Utils.returnArrayMessage(pResponse);
		depositsPayableResponse.setMessages(message);
		depositsPayableResponse.setPayableInterests(depositsPayableInterestsList);
		return depositsPayableResponse;

	}

	// ************************************************************************************************

	/**
	 * @param spNamePaymentSchedule
	 * @param timeDepositsPaymentSchedule
	 * @param method
	 * @return
	 */
	private IProcedureResponse Execution(String spNamePaymentSchedule,
			TimeDepositsPaymentScheduleRequest timeDepositsPaymentSchedule, String method)
			throws CTSServiceException, CTSInfrastructureException {
		// TODO Auto-generated method stub
		IProcedureRequest request = initProcedureRequest(timeDepositsPaymentSchedule.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "14463");
		request.setSpName(spNamePaymentSchedule);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "14463");

		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "S");
		request.addInputParam("@i_op_num_banco", ICTSTypes.SQLVARCHAR,
				timeDepositsPaymentSchedule.getProduct().getProductNumber());

		request.addInputParam("@i_cuota", ICTSTypes.SQLINT4,
				timeDepositsPaymentSchedule.getSecuential().getSecuential());

		if (logger.isDebugEnabled()) {
			logger.logDebug("@i_op_num_banco: " + timeDepositsPaymentSchedule.getProduct().getProductNumber());
			logger.logDebug("@i_cuota: " + timeDepositsPaymentSchedule.getSecuential().getSecuential());

		}
		if (logger.isDebugEnabled()) {
			logger.logDebug("Request Corebanking: " + request.getProcedureRequestAsString());
		}

		IProcedureResponse pRespon = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response TimeDepositsPaymentSchedule: *** " + pRespon.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response TimeDepositsPaymentSchedule*** ");
		}

		return pRespon;
	}

	// ************************************************************************************************
	/**
	 * @param pResponse
	 * @param string
	 * @return
	 */
	private TimeDepositsPaymentScheduleResponse transformToTimeDepositsPaymentScheduleResponse(
			IProcedureResponse pRespon, String Method) {
		TimeDepositsPaymentScheduleResponse timeDepositsPaymentSchedule = new TimeDepositsPaymentScheduleResponse();
		TimeDepositsPaymentSchedule timeDepositPaymentSchedule = null;
		List<TimeDepositsPaymentSchedule> listTimeDepositsPaymentSchedule = null;

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + pRespon.getProcedureResponseAsString());
		}
		IResultSetRow[] rowsTimeDepositMov = pRespon.getResultSet(1).getData().getRowsAsArray();
		if (Method.equals("getTimeDepositsPaymentSchedule")) {
			listTimeDepositsPaymentSchedule = new ArrayList<TimeDepositsPaymentSchedule>();

			for (int i = 0; i < rowsTimeDepositMov.length; i++) {
				IResultSetRow iResultSetRow = rowsTimeDepositMov[i];
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				timeDepositPaymentSchedule = new TimeDepositsPaymentSchedule();

				timeDepositPaymentSchedule.setQuota(
						columns[COL_QUOTA].getValue() == null ? 0 : Integer.parseInt(columns[COL_QUOTA].getValue()));
				timeDepositPaymentSchedule.setPaymentDate(columns[COL_PAYMENT_DATE].getValue());
				timeDepositPaymentSchedule.setQuotaAmount(columns[COL_QUOTA_AMOUNT].getValue() == null ? 0
						: Double.parseDouble(columns[COL_QUOTA_AMOUNT].getValue()));
				timeDepositPaymentSchedule.setEntity(
						columns[COL_ENTITY].getValue() == null ? 0 : Integer.parseInt(columns[COL_ENTITY].getValue()));
				timeDepositPaymentSchedule.setOperationDescription(columns[COL_OPERATION_DESCRIPTION].getValue());
				timeDepositPaymentSchedule.setAddressDescription(columns[COL_ADDRESS_DESCRIPTION].getValue());
				timeDepositPaymentSchedule.setOfficeName(columns[COL_OFFICE_NAME].getValue());
				timeDepositPaymentSchedule.setBankNumberOperation(columns[COL_BANK_NUMBER_OPERATION].getValue());
				timeDepositPaymentSchedule.setDepositTypeDescription(columns[COL_DEPOSIT_TYPE_DESCRIPTION].getValue());
				timeDepositPaymentSchedule.setAmount(columns[COL_AMOUNT1].getValue() == null ? 0
						: Double.parseDouble(columns[COL_AMOUNT1].getValue()));
				timeDepositPaymentSchedule.setPaymentDescription(columns[COL_PAYMENT_DESCRIPTION].getValue());
				timeDepositPaymentSchedule.setCurrency(columns[COL_CURRENCY1].getValue() == null ? 0
						: Integer.parseInt(columns[COL_CURRENCY1].getValue()));
				timeDepositPaymentSchedule.setRate(
						columns[COL_RATE1].getValue() == null ? 0 : Double.parseDouble(columns[COL_RATE1].getValue()));
				timeDepositPaymentSchedule.setExpirationDate(columns[COL_EXPIRATION_DATE1].getValue());
				timeDepositPaymentSchedule.setStatus(columns[COL_STATUS1].getValue());
				timeDepositPaymentSchedule.setOperationDaysNumber(columns[COL_OPERATION_DAYS_NUMBER].getValue() == null
						? 0 : Integer.parseInt(columns[COL_OPERATION_DAYS_NUMBER].getValue()));
				timeDepositPaymentSchedule.setInsertDate(columns[COL_INSERT_DATE].getValue());
				timeDepositPaymentSchedule.setQuotaValue(columns[COL_QUOTA_VALUE].getValue());
				timeDepositPaymentSchedule.setQuotaDaysNumber(columns[COL_QUOTA_DAYS_NUMBER].getValue() == null ? 0
						: Integer.parseInt(columns[COL_QUOTA_DAYS_NUMBER].getValue()));
				timeDepositPaymentSchedule.setLastPaymentDate(columns[COL_LAST_PAYMENT_DATE].getValue());
				timeDepositPaymentSchedule.setInterestEarned(columns[COL_INTEREST_EARNED].getValue() == null ? 0
						: Double.parseDouble(columns[COL_INTEREST_EARNED].getValue()));

				listTimeDepositsPaymentSchedule.add(timeDepositPaymentSchedule);
			}

		}

		timeDepositsPaymentSchedule.setReturnCode(pRespon.getReturnCode());

		Message[] message = Utils.returnArrayMessage(pRespon);

		timeDepositsPaymentSchedule.setMessages(message);
		timeDepositsPaymentSchedule.setDepositsPaymentSchedule(listTimeDepositsPaymentSchedule);

		return timeDepositsPaymentSchedule;
	}

	// ************************************************************************************************
	/**
	 * @param spNameMovement
	 * @param timeDepositMovement
	 * @param string
	 * @return
	 */
	private IProcedureResponse Execution(String spNameMovement, TimeDepositsMovementsRequest timeDepositMovement,
			String Method) throws CTSServiceException, CTSInfrastructureException {
		;
		IProcedureRequest request = initProcedureRequest(timeDepositMovement.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18000");
		request.setSpName(spNameMovement);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "14639");

		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "H");
		request.addInputParam("@i_tipo", ICTSTypes.SQLCHAR, "T");
		request.addInputParam("@i_num_banco", ICTSTypes.SQLVARCHAR,
				timeDepositMovement.getTimeDepositsMovements().getProduct().getProductNumber());

		request.addInputParam("@i_secuencia", ICTSTypes.SQLINT4, timeDepositMovement.getSecuential().getSecuential());
		request.addInputParam("@i_formato_fecha", ICTSTypes.SQLINT4, timeDepositMovement.getDateFormat().toString());

		if (logger.isDebugEnabled()) {
			logger.logDebug(
					"@i_num_banco: " + timeDepositMovement.getTimeDepositsMovements().getProduct().getProductNumber());
			logger.logDebug("@i_secuencia: " + timeDepositMovement.getSecuential().getSecuential());
			logger.logDebug("@i_formato_fecha: " + timeDepositMovement.getDateFormat().toString());

		}
		if (logger.isDebugEnabled()) {
			logger.logDebug("Request Corebanking: " + request.getProcedureRequestAsString());
		}

		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response TimeDepositMovement: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response TimeDepositMovement*** ");
		}

		return pResponse;
	}

	private IProcedureResponse ExecutionCatalog(String SpName, TimeDepositsMovementsRequest timeDepositMovement)
			throws CTSServiceException, CTSInfrastructureException {

		IProcedureRequest request = initProcedureRequest(timeDepositMovement.getOriginalRequest());

		// IProcedureRequest request = new ProcedureRequestAS();
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1563");

		request.setSpName(SpName);

		request.addInputParam("@t_trn", ICTSTypes.SYBINT4, "1563");
		request.addInputParam("@i_operacion", ICTSTypes.SYBCHAR, "Q");

		request.addInputParam("@i_tabla", ICTSTypes.SYBVARCHAR, "bv_estado_autorizacion");
		request.addInputParam("@i_modo", ICTSTypes.SYBINT4, "0");

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

	private Map<String, String> transformToMap(IProcedureResponse aProcedureResponse) {
		Map<String, String> map = new HashMap<String, String>();

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + aProcedureResponse.getProcedureResponseAsString());
		}

		if (aProcedureResponse.getReturnCode() == 0) {

			IResultSetRow[] rowsSubType = aProcedureResponse.getResultSet(2).getData().getRowsAsArray();

			for (IResultSetRow iResultSetRow : rowsSubType) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

				map.put(columns[COL_CODE].getValue().trim(), columns[COL_CATALOG].getValue());

			}
		}
		return map;
	}

	// *********************************************************************************
	/**
	 * @param pResponse
	 * @param string
	 * @return
	 */
	private TimeDepositsMovementsResponse transformToTimeDepositsMovementsResponse(IProcedureResponse pResponse,
			String Method, Map<String, String> map) {
		// TODO Auto-generated method stub TimeDepositsMovements
		TimeDepositsMovementsResponse timeDepositMovResp = new TimeDepositsMovementsResponse();
		TimeDepositsMovements timeDepositMov = null;
		List<TimeDepositsMovements> listTimeDepositMovements = null;
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + pResponse.getProcedureResponseAsString());
		}
		IResultSetRow[] rowsTimeDepositMov = pResponse.getResultSet(1).getData().getRowsAsArray();
		if (Method.equals("getTimeDepositMovements")) {
			listTimeDepositMovements = new ArrayList<TimeDepositsMovements>();

			for (int i = 0; i < rowsTimeDepositMov.length; i++) {
				timeDepositMov = new TimeDepositsMovements();
				IResultSetRow iResultSetRow = rowsTimeDepositMov[i];
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

				timeDepositMov.setDate(columns[COL_DATE].getValue());
				timeDepositMov.setTransactionName(columns[COL_TRANSACTION_NAME].getValue());
				timeDepositMov.setPayFormat(columns[COL_PAY_FORMAT].getValue());
				timeDepositMov.setCurrency(columns[COL_CURRENCY].getValue());
				timeDepositMov.setInternationalAmount(columns[COL_INTERNATIONAL_AMOUNT].getValue() == null ? 0
						: Double.parseDouble(columns[COL_INTERNATIONAL_AMOUNT].getValue()));
				timeDepositMov.setAmount(columns[COL_AMOUNT].getValue() == null ? 0
						: Double.parseDouble(columns[COL_AMOUNT].getValue()));
				timeDepositMov.setStatus(
						columns[COL_STATUS].getValue() == null ? "" : map.get(columns[COL_STATUS].getValue()));
				timeDepositMov.setSequence(columns[COL_SEQUENCE].getValue() == null ? 0
						: Integer.parseInt(columns[COL_SEQUENCE].getValue()));
				timeDepositMov.setAccount(columns[COL_ACCOUNT].getValue());
				timeDepositMov.setBeneficiary(columns[COL_BENEFICIARY].getValue());
				timeDepositMov.setValueDate(columns[COL_VALUE_DATE1].getValue());
				timeDepositMov.setTransactionNumber(columns[COL_TRANSACTION_NUMBER].getValue() == null ? 0
						: Integer.parseInt(columns[COL_TRANSACTION_NUMBER].getValue()));
				timeDepositMov.setSubsequence(columns[COL_SUBSEQUENCE].getValue() == null ? 0
						: Integer.parseInt(columns[COL_SUBSEQUENCE].getValue()));

				listTimeDepositMovements.add(timeDepositMov);
			}

		}

		timeDepositMovResp.setReturnCode(pResponse.getReturnCode());

		Message[] message = Utils.returnArrayMessage(pResponse);

		timeDepositMovResp.setMessages(message);
		timeDepositMovResp.setDepositsMovements(listTimeDepositMovements);

		return timeDepositMovResp;
	}

	/*
	 * Execute Stored Procedure and Return IProcedureResponse
	 * ********************
	 * ******************************************************
	 * *******************************************************************
	 */
	private IProcedureResponse Execution(String SpName, TimeDepositRequest aTimeDepositRequest, String Method)
			throws CTSServiceException, CTSInfrastructureException {
		;
		IProcedureRequest request = initProcedureRequest(aTimeDepositRequest.getOriginalRequest());
		// IProcedureRequest request = new ProcedureRequestAS();
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800022");
		request.setSpName(SpName);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "18422");
		// request.addInputParam("@i_cliente", ICTSTypes.SQLINT4,
		// aTimeDepositRequest.getCustomerCode().toString());
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR,
				aTimeDepositRequest.getTimeDeposit().getProduct().getProductNumber());
		request.addInputParam("@i_formato_fecha", ICTSTypes.SQLINT4, aTimeDepositRequest.getDateFormat().toString());
		//
		if (aTimeDepositRequest.getCustomerCode() != null) {
			request.addInputParam("@i_cliente", ICTSTypes.SQLINT4, aTimeDepositRequest.getCustomerCode().toString());
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("@i_cliente: " + aTimeDepositRequest.getCustomerCode().toString());
			logger.logDebug("@i_cta: " + aTimeDepositRequest.getTimeDeposit().getAccountOfficer());
			logger.logDebug("@i_formato_fecha: " + aTimeDepositRequest.getDateFormat().toString());
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request Corebanking: " + request.getProcedureRequestAsString());
		}

		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response TimeDeposit: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response TimeDeposit*** ");
		}

		return pResponse;

	}

	/*
	 * 
	 * *************************************************************************
	 * ********************************************************************
	 */
	private TimeDepositResponse transformToTimeDepositResponse(IProcedureResponse aProcedureResponse, String Method) {
		TimeDepositResponse timeDepositResp = new TimeDepositResponse();
		// List<TimeDeposit> timeCollection = new ArrayList<Check>();
		TimeDeposit aTimeDeposit = null;
		Product aProduct = null;
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + aProcedureResponse.getProcedureResponseAsString());
		}

		if (aProcedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsTimeDeposit = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();
			aTimeDeposit = new TimeDeposit();
			aProduct = new Product();
			if (Method.equals("getTimeDepositDetail")) {
				IResultSetRow iResultSetRow = rowsTimeDeposit[0];
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				aProduct.setProductNumber(
						columns[COL_PRODUCT_NUMBER].getValue() == null ? "0" : columns[COL_PRODUCT_NUMBER].getValue());
				aTimeDeposit.setProduct(aProduct);
				aTimeDeposit.setOpeningDate(columns[COL_OPENNING_DATE].getValue());
				aTimeDeposit.setExpirationDate(columns[COL_EXPIRATION_DATE].getValue());
				aTimeDeposit.setAmount(columns[COL_CAPITAL_BALANCE].getValue() == null ? 0
						: Double.parseDouble(columns[COL_CAPITAL_BALANCE].getValue()));
				aTimeDeposit.setTotalRateEstimed(columns[COL_TOTAL_INTEREST_INCOME].getValue() == null ? 0
						: Double.parseDouble(columns[COL_TOTAL_INTEREST_INCOME].getValue()));
				aTimeDeposit.setRate(columns[COL_RATE].getValue());
				aTimeDeposit.setTerm(
						columns[COL_TERM].getValue() == null ? 0 : Integer.parseInt(columns[COL_TERM].getValue()));
				aTimeDeposit.setAmountEstimed(columns[COL_CAPITAL_BALANCE_MATURI].getValue() == null ? 0
						: Double.parseDouble(columns[COL_CAPITAL_BALANCE_MATURI].getValue()));
				aTimeDeposit.setAutomaticRenewal(columns[COL_AUTOMATIC_RENEWAL].getValue());
				aTimeDeposit.setIsCompounded(columns[COL_IS_COMPOUNDED].getValue());
				aTimeDeposit.setFrecuencyOfPayment(columns[COL_FRECUENCY_OF_PAYMENT].getValue());
				aTimeDeposit.setAccountOfficer(columns[COL_ACOOUNT_OFFICER].getValue());
				aTimeDeposit.setValueDate(columns[COL_EXPIRATION_DATE].getValue());
				aTimeDeposit.setCalculationBase(columns[COL_CALCULATION_BASE].getValue() == null ? 0
						: Integer.parseInt(columns[COL_CALCULATION_BASE].getValue()));
				aTimeDeposit.getProduct().setProductNemonic(columns[COL_PRODUCT_ABBREVIATION].getValue());
				aTimeDeposit.getProduct().setProductAlias(columns[COL_PRODUCT_ALIAS].getValue());
			}
		}
		timeDepositResp.setReturnCode(aProcedureResponse.getReturnCode());
		Message[] message = Utils.returnArrayMessage(aProcedureResponse);
		timeDepositResp.setMessages(message);
		timeDepositResp.setTimeDeposit(aTimeDeposit);
		;
		return timeDepositResp;
	}

	/*
	 * Execute Stored Procedure and Return IProcedureResponse
	 * ********************
	 * ******************************************************
	 * *******************************************************************
	 */
	private IProcedureResponse Execution(String spNameHistoricals,
			TimeDepositsHistoricalsRequest aTimeDepositHistRequest, String Method)
			throws CTSServiceException, CTSInfrastructureException {
		;
		IProcedureRequest request = initProcedureRequest(aTimeDepositHistRequest.getOriginalRequest());
		// IProcedureRequest request = new ProcedureRequestAS();
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "14805");
		request.setSpName(spNameHistoricals);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "14805");

		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "S");
		request.addInputParam("@i_modo", ICTSTypes.SQLINT4, "1");
		request.addInputParam("@i_num_banco", ICTSTypes.SQLVARCHAR,
				aTimeDepositHistRequest.getProduct().getProductNumber());
		request.addInputParam("@i_secuencial", ICTSTypes.SQLINT2,
				(aTimeDepositHistRequest.getSecuential().getSecuential() == null ? "0"
						: aTimeDepositHistRequest.getSecuential().getSecuential()));

		request.addInputParam("@i_formato_fecha", ICTSTypes.SQLINT4,
				aTimeDepositHistRequest.getDateFormat().toString());

		if (logger.isDebugEnabled()) {
			logger.logDebug("@i_num_banco: " + aTimeDepositHistRequest.getProduct().getProductNumber());
			logger.logDebug("@i_secuencial: " + aTimeDepositHistRequest.getSecuential().getSecuential());
			logger.logDebug("@i_formato_fecha: " + aTimeDepositHistRequest.getDateFormat().toString());
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug(
					"Request Corebanking TimeDepositsHistoricalsRequest: " + request.getProcedureRequestAsString());
		}

		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug(
					"*** Response TimeDepositsHistoricalsRequest: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response TimeDepositsHistoricalsRequest*** ");
		}

		return pResponse;

	}

	// *********************************************************************************
	/**
	 * @param pResponse
	 * @param string
	 * @return
	 */
	private TimeDepositsHistoricalsResponse transformToTimeDepositsHistoricalsResponse(IProcedureResponse pResponse,
			String Method) {
		// TODO Auto-generated method stub TimeDepositsMovements
		TimeDepositsHistoricalsResponse timeDepositHistResp = new TimeDepositsHistoricalsResponse();
		TimeDepositsHistoricals timeDepositHist = null;
		List<TimeDepositsHistoricals> listTimeDepositHistoricals = null;
		if (logger.isDebugEnabled()) {
			logger.logDebug(
					"*** transformToTimeDepositsHistoricalsResponse: ***" + pResponse.getProcedureResponseAsString());
		}
		IResultSetRow[] rowsTimeDepositHist = pResponse.getResultSet(1).getData().getRowsAsArray();

		if (Method.equals("getTimeDepositHistoricals")) {
			listTimeDepositHistoricals = new ArrayList<TimeDepositsHistoricals>();

			for (int i = 0; i < rowsTimeDepositHist.length; i++) {
				IResultSetRow iResultSetRow = rowsTimeDepositHist[i];
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				timeDepositHist = new TimeDepositsHistoricals();
				timeDepositHist.setSequence(columns[COL_SEQUENCE_HIST].getValue() == null ? 0
						: Integer.parseInt(columns[COL_SEQUENCE_HIST].getValue()));
				timeDepositHist.setCoupon(
						columns[COL_COUPON].getValue() == null ? 0 : Integer.parseInt(columns[COL_COUPON].getValue()));
				timeDepositHist.setTransactionDate(columns[COL_TRANSACTIONDATE].getValue());
				timeDepositHist.setTransactionCode(columns[COL_TRANSACTIONCODE].getValue() == null ? 0
						: Integer.parseInt(columns[COL_TRANSACTIONCODE].getValue()));
				timeDepositHist.setDescription(columns[COL_DESCRIPTION].getValue());
				timeDepositHist.setValue(
						columns[COL_VALUE].getValue() == null ? 0 : Double.parseDouble(columns[COL_VALUE].getValue()));
				timeDepositHist.setObservation(columns[COL_OBSERVATION].getValue());
				timeDepositHist.setFunctionary(columns[COL_FUNCTIONARY].getValue());
				timeDepositHist.setRate(columns[COL_RATE_HIST].getValue() == null ? 0
						: Double.parseDouble(columns[COL_RATE_HIST].getValue()));

				listTimeDepositHistoricals.add(timeDepositHist);
			}

		}

		timeDepositHistResp.setReturnCode(pResponse.getReturnCode());

		Message[] message = Utils.returnArrayMessage(pResponse);

		timeDepositHistResp.setMessages(message);
		timeDepositHistResp.setDepositsHistoricals(listTimeDepositHistoricals);

		return timeDepositHistResp;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceTimeDeposits
	 * #getTimeDepositCatalog(com.cobiscorp.ecobis.ib.application.dtos.
	 * TimeDepositCatalogRequest)
	 */
	@Override
	public TimeDepositCatalogResponse getTimeDepositCatalog(TimeDepositCatalogRequest timeDepositCatalogRequest)
			throws CTSServiceException, CTSInfrastructureException {

		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getTimeDepositCatalog " + timeDepositCatalogRequest);
		}
		IProcedureResponse pResponse = Execution(SP_NAME_CATALOG_TIMEDEPOSITE, timeDepositCatalogRequest);
		TimeDepositCatalogResponse TimeDepositCatalogResponse = transformToTimeDepositCatalogResponse(pResponse);
		return TimeDepositCatalogResponse;
	}

	/**
	 * @param spName
	 * @param timeDepositCatalogRequest
	 * @return
	 */
	private IProcedureResponse Execution(String spName, TimeDepositCatalogRequest timeDepositCatalogRequest)
			throws CTSServiceException, CTSInfrastructureException {
		// TODO Auto-generated method stub
		IProcedureRequest request = initProcedureRequest(timeDepositCatalogRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800270");
		request.setSpName(spName);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1800270");

		request.addInputParam("@i_tipo_persona", ICTSTypes.SQLVARCHAR, timeDepositCatalogRequest.getTypePerson());

		if (logger.isDebugEnabled()) {
			logger.logDebug("@i_tipo_persona: " + timeDepositCatalogRequest.getTypePerson());
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request Corebanking: " + request.getProcedureRequestAsString());
		}

		IProcedureResponse pRespon = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response getTimeDepositCatalog: *** " + pRespon.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response getTimeDepositCatalog*** ");
		}

		return pRespon;
	}

	/**
	 * @param pResponse
	 * @return
	 */
	private TimeDepositCatalogResponse transformToTimeDepositCatalogResponse(IProcedureResponse pRespon) {
		TimeDepositCatalogResponse timeDepositCatalogResponse = new TimeDepositCatalogResponse();
		TimeDepositCatalog timeDepositCatalog = null;
		List<TimeDepositCatalog> listTimeDepositCatalog = new ArrayList<TimeDepositCatalog>();

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** TimeDeposite ProcedureResponse: ***" + pRespon.getProcedureResponseAsString());
		}
		if (pRespon.getReturnCode() == 0) {
			IResultSetRow[] rowsTimeDepositC = pRespon.getResultSet(1).getData().getRowsAsArray();

			for (IResultSetRow iResultSetRow : rowsTimeDepositC) {

				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				timeDepositCatalog = new TimeDepositCatalog();
				timeDepositCatalog.setCode(columns[COL_CODE].getValue());
				timeDepositCatalog.setDescription(columns[COL_CATALOG].getValue());
				listTimeDepositCatalog.add(timeDepositCatalog);
			}

			timeDepositCatalogResponse.setCollectionTimeDepositCatalog(listTimeDepositCatalog);

		} else {
			Message[] message = Utils.returnArrayMessage(pRespon);
			timeDepositCatalogResponse.setMessages(message);
		}

		timeDepositCatalogResponse.setReturnCode(pRespon.getReturnCode());

		return timeDepositCatalogResponse;
	}

}
