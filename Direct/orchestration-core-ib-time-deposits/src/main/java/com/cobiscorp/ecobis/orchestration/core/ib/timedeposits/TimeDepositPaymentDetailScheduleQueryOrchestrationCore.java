package com.cobiscorp.ecobis.orchestration.core.ib.timedeposits;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsPaymentDetailScheduleRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsPaymentDetailScheduleResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
//import com.cobiscorp.ecobis.ib.orchestration.dtos.Secuential;
import com.cobiscorp.ecobis.ib.orchestration.dtos.TimeDepositsPaymentDetailSchedule;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceTimeDeposits;

/**
 * 
 * @author areinoso
 *
 */
@Component(name = "TimeDepositPaymentDetailScheduleQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = {
		@Property(name = "service.description", value = "TimeDepositPaymentDetailScheduleQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TimeDepositPaymentDetailScheduleQueryOrchestrationCore") })

public class TimeDepositPaymentDetailScheduleQueryOrchestrationCore extends QueryBaseTemplate {
	@Reference(referenceInterface = ICoreServiceTimeDeposits.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceTimeDeposits coreServiceTimeDeposit;
	ILogger logger = this.getLogger();

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreServiceTimeDeposits service) {
		coreServiceTimeDeposit = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreServiceTimeDeposits service) {
		coreServiceTimeDeposit = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {

		String messageError = null;
		String messageLog = null;
		String queryName = null;

		TimeDepositsPaymentDetailScheduleResponse aPaymentDetailScheduleResponse = null;
		TimeDepositsPaymentDetailScheduleRequest aPaymentDetailScheduleRequest = transformTimeDepositsPaymentDetailScheduleRequest(
				request.clone());

		if (logger.isDebugEnabled())
			logger.logDebug("OBJ: " + aPaymentDetailScheduleRequest.getProduct().getProductNumber());

		try {
			if (logger.isDebugEnabled())
				logger.logDebug("executeQuery");
			messageError = "get: ERROR EXECUTING SERVICE";
			if (logger.isDebugEnabled())
				logger.logDebug("request PaymentDetailSchedule: " + request);
			messageLog = "getTimeDepositsPaymentDetailSchedule: " + aPaymentDetailScheduleRequest.getProductId();
			queryName = "getTimeDepositsPaymentDetailSchedule";
			aPaymentDetailScheduleRequest.setOriginalRequest(request);
			aPaymentDetailScheduleResponse = coreServiceTimeDeposit
					.getTimeDepositsPaymentDetailSchedule(aPaymentDetailScheduleRequest);
		} catch (CTSServiceException e) {
			e.printStackTrace();
			if (logger.isDebugEnabled())
				logger.logDebug(messageError);
			return null;
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
			if (logger.isDebugEnabled())
				logger.logDebug(messageError);
			return null;
		}

		aBagSPJavaOrchestration.put(LOG_MESSAGE, messageLog);
		aBagSPJavaOrchestration.put(QUERY_NAME, queryName);
		if (logger.isDebugEnabled())
			logger.logDebug(" return Transform Procedure Response :" + aPaymentDetailScheduleResponse.toString());
		IProcedureResponse atransformProcedureResponse = null;

		if (aPaymentDetailScheduleRequest.getNext().equals("N")) {
			atransformProcedureResponse = transformProcedureResponse(aPaymentDetailScheduleResponse);// "reverent
																										// of
																										// failed";
		} else if (aPaymentDetailScheduleRequest.getNext().equals("S")) {

			atransformProcedureResponse = transformProcedureResponse2(aPaymentDetailScheduleResponse);
		}
		return atransformProcedureResponse;

	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {

		return null;

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration TimeDepositPaymentSchedule");
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreServiceTimeDeposit", coreServiceTimeDeposit);
		Utils.validateComponentInstance(mapInterfaces);
		try {
			return executeStepsQueryBase(anOrginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		}
	}

	/******************
	 * Transformación de ProcedureRequest a TimeDepositsPaymentScheduleRequest
	 ********************/

	private TimeDepositsPaymentDetailScheduleRequest transformTimeDepositsPaymentDetailScheduleRequest(
			IProcedureRequest aRequest) {

		TimeDepositsPaymentDetailScheduleRequest timeDepositsPaymentDetailScheduleRequest = new TimeDepositsPaymentDetailScheduleRequest();
		// Secuential secuential = new Secuential();
		Product wProduct = new Product();
		Currency wCurrency = new Currency();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure TimeDepositsPaymentScheduleRequest to Transform->"
					+ aRequest.getProcedureRequestAsString());
		String messageError = null;
		messageError = aRequest.readValueParam("@i_cuenta") == null ? " - @i_cuenta can't be null" : "";
		// messageError += aRequest.readValueParam("@i_secuencia") == null ? " -
		// @i_secuencia can't be null":"";
		// messageError += aRequest.readValueParam("@i_formato_fecha") == null ?
		// " - @i_formato_fecha can't be null":"";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		wCurrency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));

		wProduct.setCurrency(wCurrency);
		wProduct.setProductNumber(aRequest.readValueParam("@i_cta"));

		timeDepositsPaymentDetailScheduleRequest.setProduct(wProduct);

		timeDepositsPaymentDetailScheduleRequest.setProductId(Integer.parseInt(aRequest.readValueParam("@i_prod")));
		timeDepositsPaymentDetailScheduleRequest
				.setDateFormat(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha")));
		timeDepositsPaymentDetailScheduleRequest.setNext(aRequest.readValueParam("@i_siguientes"));

		return timeDepositsPaymentDetailScheduleRequest;

	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformProcedureResponse(
			TimeDepositsPaymentDetailScheduleResponse aPaymentDetailScheduleResponse) {
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response : " + aPaymentDetailScheduleResponse.toString());
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		// Agregar Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("entity", ICTSTypes.SQLINT4, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty1", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty2", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty3", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("realDays", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty5", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("daysNumber", ICTSTypes.SQLINT4, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty6", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("amountPaiedInterest", ICTSTypes.SYBDECIMAL, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty7", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty8", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("rate", ICTSTypes.SYBDECIMAL, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("earnedInterest", ICTSTypes.SYBDECIMAL, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty9", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty10", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty11", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty12", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty13", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("compounded", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("paymentType", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty14", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty15", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty16", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("status", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty17", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty18", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty19", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty20", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("valueDate", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("expirateDate", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty21", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty22", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty23", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty24", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty25", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty26", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty27", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty28", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("payDay", ICTSTypes.SQLINT4, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty29", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty30", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty31", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty32", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty33", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty34", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty35", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty36", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty37", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty38", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty39", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty40", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty41", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("month", ICTSTypes.SQLINT4, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty42", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty43", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty44", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty45", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty46", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty47", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty48", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty49", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty50", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty51", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty52", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty53", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty54", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty55", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty56", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty57", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty58", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty59", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty60", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty61", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty62", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty63", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty64", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty65", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty66", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty67", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty68", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty69", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty70", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty71", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty72", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty73", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty74", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty75", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty76", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty77", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty78", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty79", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty80", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty81", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty82", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty83", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty84", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty85", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty86", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty87", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("baseCalculate", ICTSTypes.SQLINT4, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty88", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty89", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty90", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty91", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty92", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty93", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty94", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty95", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty96", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty97", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty98", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty99", ICTSTypes.SYBVARCHAR, 10));

		if (aPaymentDetailScheduleResponse.getReturnCode() == 0) {

			for (TimeDepositsPaymentDetailSchedule atimeDepositsPaymentDetailSchedule : aPaymentDetailScheduleResponse
					.getDepositsPaymentDetailSchedule()) {

				// if
				// (!IsValidTimeDepositsPaymentScheduleResponse(atimeDepositsPaymentDetailSchedule))
				// return null;
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, ""));
				row.addRowData(2,
						new ResultSetRowColumnData(false, atimeDepositsPaymentDetailSchedule.getEntity().toString()));
				row.addRowData(3, new ResultSetRowColumnData(false, ""));
				row.addRowData(4, new ResultSetRowColumnData(false, ""));
				row.addRowData(5, new ResultSetRowColumnData(false, ""));
				row.addRowData(6, new ResultSetRowColumnData(false, ""));// atimeDepositsPaymentDetailSchedule.getRealDays();
				row.addRowData(7, new ResultSetRowColumnData(false, ""));
				row.addRowData(8, new ResultSetRowColumnData(false,
						atimeDepositsPaymentDetailSchedule.getDaysNumber().toString()));
				row.addRowData(9, new ResultSetRowColumnData(false, ""));
				row.addRowData(10, new ResultSetRowColumnData(false,
						atimeDepositsPaymentDetailSchedule.getAmountPaiedInterest().toString()));
				row.addRowData(11, new ResultSetRowColumnData(false, ""));
				row.addRowData(12, new ResultSetRowColumnData(false, ""));
				row.addRowData(13,
						new ResultSetRowColumnData(false, atimeDepositsPaymentDetailSchedule.getRate().toString()));
				row.addRowData(14, new ResultSetRowColumnData(false,
						atimeDepositsPaymentDetailSchedule.getEarnedInterest().toString()));
				row.addRowData(15, new ResultSetRowColumnData(false, ""));
				row.addRowData(16, new ResultSetRowColumnData(false, ""));
				row.addRowData(17, new ResultSetRowColumnData(false, ""));
				row.addRowData(18, new ResultSetRowColumnData(false, ""));
				row.addRowData(19, new ResultSetRowColumnData(false, ""));
				row.addRowData(20,
						new ResultSetRowColumnData(false, atimeDepositsPaymentDetailSchedule.getCompounded()));
				row.addRowData(21,
						new ResultSetRowColumnData(false, atimeDepositsPaymentDetailSchedule.getPaymentType()));
				row.addRowData(22, new ResultSetRowColumnData(false, ""));
				row.addRowData(23, new ResultSetRowColumnData(false, ""));
				row.addRowData(24, new ResultSetRowColumnData(false, ""));
				row.addRowData(25, new ResultSetRowColumnData(false, atimeDepositsPaymentDetailSchedule.getStatus()));
				row.addRowData(26, new ResultSetRowColumnData(false, ""));
				row.addRowData(27, new ResultSetRowColumnData(false, ""));
				row.addRowData(28, new ResultSetRowColumnData(false, ""));
				row.addRowData(29, new ResultSetRowColumnData(false, ""));
				row.addRowData(30,
						new ResultSetRowColumnData(false, atimeDepositsPaymentDetailSchedule.getValueDate()));
				row.addRowData(31,
						new ResultSetRowColumnData(false, atimeDepositsPaymentDetailSchedule.getExpirateDate()));
				row.addRowData(32, new ResultSetRowColumnData(false, ""));
				row.addRowData(33, new ResultSetRowColumnData(false, ""));
				row.addRowData(34, new ResultSetRowColumnData(false, ""));
				row.addRowData(35, new ResultSetRowColumnData(false, ""));
				row.addRowData(36, new ResultSetRowColumnData(false, ""));
				row.addRowData(37, new ResultSetRowColumnData(false, ""));
				row.addRowData(38, new ResultSetRowColumnData(false, ""));
				row.addRowData(39, new ResultSetRowColumnData(false, ""));
				row.addRowData(40,
						new ResultSetRowColumnData(false, atimeDepositsPaymentDetailSchedule.getPayDay().toString()));
				row.addRowData(41, new ResultSetRowColumnData(false, ""));
				row.addRowData(42, new ResultSetRowColumnData(false, ""));
				row.addRowData(43, new ResultSetRowColumnData(false, ""));
				row.addRowData(44, new ResultSetRowColumnData(false, ""));
				row.addRowData(45, new ResultSetRowColumnData(false, ""));
				row.addRowData(46, new ResultSetRowColumnData(false, ""));
				row.addRowData(47, new ResultSetRowColumnData(false, ""));
				row.addRowData(48, new ResultSetRowColumnData(false, ""));
				row.addRowData(49, new ResultSetRowColumnData(false, ""));
				row.addRowData(50, new ResultSetRowColumnData(false, ""));
				row.addRowData(51, new ResultSetRowColumnData(false, ""));
				row.addRowData(52, new ResultSetRowColumnData(false, ""));
				row.addRowData(53, new ResultSetRowColumnData(false, ""));
				row.addRowData(54,
						new ResultSetRowColumnData(false, atimeDepositsPaymentDetailSchedule.getMonth().toString()));
				row.addRowData(55, new ResultSetRowColumnData(false, ""));
				row.addRowData(56, new ResultSetRowColumnData(false, ""));
				row.addRowData(57, new ResultSetRowColumnData(false, ""));
				row.addRowData(58, new ResultSetRowColumnData(false, ""));
				row.addRowData(59, new ResultSetRowColumnData(false, ""));
				row.addRowData(60, new ResultSetRowColumnData(false, ""));
				row.addRowData(61, new ResultSetRowColumnData(false, ""));
				row.addRowData(62, new ResultSetRowColumnData(false, ""));
				row.addRowData(63, new ResultSetRowColumnData(false, ""));
				row.addRowData(64, new ResultSetRowColumnData(false, ""));
				row.addRowData(65, new ResultSetRowColumnData(false, ""));
				row.addRowData(66, new ResultSetRowColumnData(false, ""));
				row.addRowData(67, new ResultSetRowColumnData(false, ""));
				row.addRowData(68, new ResultSetRowColumnData(false, ""));
				row.addRowData(69, new ResultSetRowColumnData(false, ""));
				row.addRowData(70, new ResultSetRowColumnData(false, ""));
				row.addRowData(71, new ResultSetRowColumnData(false, ""));
				row.addRowData(72, new ResultSetRowColumnData(false, ""));
				row.addRowData(73, new ResultSetRowColumnData(false, ""));
				row.addRowData(74, new ResultSetRowColumnData(false, ""));
				row.addRowData(75, new ResultSetRowColumnData(false, ""));
				row.addRowData(76, new ResultSetRowColumnData(false, ""));
				row.addRowData(77, new ResultSetRowColumnData(false, ""));
				row.addRowData(78, new ResultSetRowColumnData(false, ""));
				row.addRowData(79, new ResultSetRowColumnData(false, ""));
				row.addRowData(80, new ResultSetRowColumnData(false, ""));
				row.addRowData(81, new ResultSetRowColumnData(false, ""));
				row.addRowData(82, new ResultSetRowColumnData(false, ""));
				row.addRowData(83, new ResultSetRowColumnData(false, ""));
				row.addRowData(84, new ResultSetRowColumnData(false, ""));
				row.addRowData(85, new ResultSetRowColumnData(false, ""));
				row.addRowData(86, new ResultSetRowColumnData(false, ""));
				row.addRowData(87, new ResultSetRowColumnData(false, ""));
				row.addRowData(88, new ResultSetRowColumnData(false, ""));
				row.addRowData(89, new ResultSetRowColumnData(false, ""));
				row.addRowData(90, new ResultSetRowColumnData(false, ""));
				row.addRowData(91, new ResultSetRowColumnData(false, ""));
				row.addRowData(92, new ResultSetRowColumnData(false, ""));
				row.addRowData(93, new ResultSetRowColumnData(false, ""));
				row.addRowData(94, new ResultSetRowColumnData(false, ""));
				row.addRowData(95, new ResultSetRowColumnData(false, ""));
				row.addRowData(96, new ResultSetRowColumnData(false, ""));
				row.addRowData(97, new ResultSetRowColumnData(false, ""));
				row.addRowData(98, new ResultSetRowColumnData(false, ""));
				row.addRowData(99, new ResultSetRowColumnData(false, ""));
				row.addRowData(100, new ResultSetRowColumnData(false, ""));
				row.addRowData(101, new ResultSetRowColumnData(false,
						atimeDepositsPaymentDetailSchedule.getBaseCalculate().toString()));
				row.addRowData(102, new ResultSetRowColumnData(false, ""));
				row.addRowData(103, new ResultSetRowColumnData(false, ""));
				row.addRowData(104, new ResultSetRowColumnData(false, ""));
				row.addRowData(105, new ResultSetRowColumnData(false, ""));
				row.addRowData(106, new ResultSetRowColumnData(false, ""));
				row.addRowData(107, new ResultSetRowColumnData(false, ""));
				row.addRowData(108, new ResultSetRowColumnData(false, ""));
				row.addRowData(109, new ResultSetRowColumnData(false, ""));
				row.addRowData(110, new ResultSetRowColumnData(false, ""));
				row.addRowData(111, new ResultSetRowColumnData(false, ""));
				row.addRowData(112, new ResultSetRowColumnData(false, ""));
				row.addRowData(113, new ResultSetRowColumnData(false, ""));

				data.addRow(row);
			}

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock);

		} else {
			wProcedureResponse = Utils.returnException(aPaymentDetailScheduleResponse.getMessages());
		}
		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse 2
	 ***********************/

	private IProcedureResponse transformProcedureResponse2(
			TimeDepositsPaymentDetailScheduleResponse aPaymentDetailScheduleResponse) {
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response : " + aPaymentDetailScheduleResponse.toString());
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response 2");

		// Agregar Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty1", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty2", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty3", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("realDays", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty4", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty5", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty6", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty7", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty8", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty9", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty10", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty11", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty12", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty13", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty14", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty15", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("empty16", ICTSTypes.SYBVARCHAR, 10));

		for (TimeDepositsPaymentDetailSchedule atimeDepositsPaymentDetailSchedule : aPaymentDetailScheduleResponse
				.getDepositsPaymentDetailSchedule()) {

			IResultSetRow row = new ResultSetRow();
			row.addRowData(1, new ResultSetRowColumnData(false, ""));
			row.addRowData(2, new ResultSetRowColumnData(false, ""));
			row.addRowData(3, new ResultSetRowColumnData(false, ""));
			row.addRowData(4, new ResultSetRowColumnData(false, ""));
			row.addRowData(5, new ResultSetRowColumnData(false, atimeDepositsPaymentDetailSchedule.getRealDays()));
			row.addRowData(6, new ResultSetRowColumnData(false, ""));
			row.addRowData(7, new ResultSetRowColumnData(false, ""));
			row.addRowData(8, new ResultSetRowColumnData(false, ""));
			row.addRowData(9, new ResultSetRowColumnData(false, ""));
			row.addRowData(10, new ResultSetRowColumnData(false, ""));
			row.addRowData(11, new ResultSetRowColumnData(false, ""));
			row.addRowData(12, new ResultSetRowColumnData(false, ""));
			row.addRowData(13, new ResultSetRowColumnData(false, ""));
			row.addRowData(14, new ResultSetRowColumnData(false, ""));
			row.addRowData(15, new ResultSetRowColumnData(false, ""));
			row.addRowData(16, new ResultSetRowColumnData(false, ""));
			row.addRowData(17, new ResultSetRowColumnData(false, ""));
			row.addRowData(18, new ResultSetRowColumnData(false, ""));

			data.addRow(row);
		}

		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
		wProcedureResponse.addResponseBlock(resultBlock);

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}
}
