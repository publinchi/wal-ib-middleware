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
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsPayableInterestsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsPayableInterestsResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Secuential;
import com.cobiscorp.ecobis.ib.orchestration.dtos.TimeDepositsPayableInterests;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceTimeDeposits;

/**
 * 
 * @author jmoreta
 *
 */
@Component(name = "TimeDepositPayableInterestsQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = {
		@Property(name = "service.description", value = "TimeDepositPayableInterestsQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TimeDepositPayableInterestsQueryOrchestrationCore") })
public class TimeDepositPayableInterestsQueryOrchestrationCore extends QueryBaseTemplate {

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

		TimeDepositsPayableInterestsResponse aPayableResponse = null;
		TimeDepositsPayableInterestsRequest aPayableRequest = transformTimeDepositsPayableInterestsRequest(
				request.clone());

		if (logger.isDebugEnabled())
			logger.logDebug("OBJ: " + aPayableRequest.getProductNumber().getProductNumber());

		try {
			if (logger.isDebugEnabled())
				logger.logDebug("executeQuery");
			messageError = "get: ERROR EXECUTING SERVICE";
			if (logger.isDebugEnabled())
				logger.logDebug("request PayableInterests: " + request);
			messageLog = "getTimeDepositsPayableInterests: " + aPayableRequest.getProductNumber().getProductNumber();
			queryName = "getTimeDepositsPayableInterests";
			aPayableRequest.setOriginalRequest(request);
			aPayableResponse = coreServiceTimeDeposit.getTimeDepositsPayableInterests(aPayableRequest);
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

		return transformProcedureResponse(aPayableResponse);
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration TimeDepositPayableInterests");
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
	 * Transformación de ProcedureRequest a TimeDepositsPayableInterestsRequest
	 ********************/

	private TimeDepositsPayableInterestsRequest transformTimeDepositsPayableInterestsRequest(
			IProcedureRequest aRequest) {

		TimeDepositsPayableInterestsRequest timeDeposPayRequest = new TimeDepositsPayableInterestsRequest();
		Product wProduct = new Product();
		Secuential secuential = new Secuential();
		Currency wCurrency = new Currency();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure TimeDepositsPayableInterestsRequest to Transform->"
					+ aRequest.getProcedureRequestAsString());

		String messageError = null;
		messageError = aRequest.readValueParam("@i_cuenta") == null ? " - @i_cuenta can't be null" : "";
		messageError += aRequest.readValueParam("@i_formato_fecha") == null ? " - @i_formato_fecha can't be null" : "";
		messageError += aRequest.readValueParam("@i_cuota") == null ? " - @i_cuota can't be null" : "";
		messageError += aRequest.readValueParam("@i_cta") == null ? " - @i_cta can't be null" : "";
		messageError += aRequest.readValueParam("@i_prod") == null ? " - @i_prod can't be null" : "";
		messageError += aRequest.readValueParam("@i_mon") == null ? " - @i_mon can't be null" : "";
		messageError += aRequest.readValueParam("@i_login") == null ? " - @i_login can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);
		wCurrency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));
		wProduct.setCurrency(wCurrency);
		wProduct.setProductNumber(aRequest.readValueParam("@i_cta"));
		secuential.setSecuential(aRequest.readValueParam("@i_cuota"));

		timeDeposPayRequest.setProductNumber(wProduct);
		timeDeposPayRequest.setSequential(secuential);
		timeDeposPayRequest.setDateFormatId(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha")));
		timeDeposPayRequest.setProductId(Integer.parseInt(aRequest.readValueParam("@i_prod")));
		timeDeposPayRequest.setUserName(aRequest.readValueParam("@i_login"));

		return timeDeposPayRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformProcedureResponse(
			TimeDepositsPayableInterestsResponse timeDepositsPayableInterestsResponse) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		// Agregar Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("approximateValue", ICTSTypes.SQLDECIMAL, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("couponNumber", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("currency", ICTSTypes.SYBVARCHAR, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("dateBox", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("detained", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("expirationDate", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("startDate", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("status", ICTSTypes.SYBVARCHAR, 3));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("payNumber", ICTSTypes.SQLINT4, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("prePrintNumber", ICTSTypes.SQLINT4, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("printNumber", ICTSTypes.SQLINT4, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("tax", ICTSTypes.SQLDECIMAL, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("value", ICTSTypes.SQLDECIMAL, 8));

		if (timeDepositsPayableInterestsResponse.getReturnCode() == 0) {

			for (TimeDepositsPayableInterests timeDepositsPayableInterests : timeDepositsPayableInterestsResponse
					.getPayableInterests()) {

				if (!IsValidTimeDepositsPayableInterestsResponse(timeDepositsPayableInterests))
					return null;

				IResultSetRow row = new ResultSetRow();
				row.addRowData(1,
						new ResultSetRowColumnData(false, timeDepositsPayableInterests.getPayNumber().toString()));
				row.addRowData(2,
						new ResultSetRowColumnData(false, timeDepositsPayableInterests.getPrePrintNumber().toString()));
				row.addRowData(3, new ResultSetRowColumnData(false,
						timeDepositsPayableInterests.getBalanceDetailPayment().getExpirationDate()));
				row.addRowData(4, new ResultSetRowColumnData(false,
						timeDepositsPayableInterests.getApproximateValue().toString()));
				row.addRowData(5,
						new ResultSetRowColumnData(false, timeDepositsPayableInterests.getValue().toString()));
				row.addRowData(6,
						new ResultSetRowColumnData(false, timeDepositsPayableInterests.getTax().getTax().toString()));
				row.addRowData(7, new ResultSetRowColumnData(false, timeDepositsPayableInterests.getDateBox()));
				row.addRowData(8, new ResultSetRowColumnData(false,
						timeDepositsPayableInterests.getBalanceDetailPayment().getStatus()));
				row.addRowData(9,
						new ResultSetRowColumnData(false, timeDepositsPayableInterests.getPrintNumber().toString()));
				row.addRowData(10, new ResultSetRowColumnData(false, timeDepositsPayableInterests.getDetained()));
				row.addRowData(11, new ResultSetRowColumnData(false, timeDepositsPayableInterests.getCouponNumber()));
				row.addRowData(12, new ResultSetRowColumnData(false,
						timeDepositsPayableInterests.getCurrency().getCurrencyDescription()));
				row.addRowData(13, new ResultSetRowColumnData(false,
						timeDepositsPayableInterests.getBalanceDetailPayment().getInitialDate()));

				data.addRow(row);
			}

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock);
		} else {
			wProcedureResponse = Utils.returnException(timeDepositsPayableInterestsResponse.getMessages());
		}
		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;

	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean IsValidTimeDepositsPayableInterestsResponse(
			TimeDepositsPayableInterests timeDepositsPayableInterests) {
		String messageError = null;
		String msgErr = null;

		messageError = timeDepositsPayableInterests.getCouponNumber() == null ? " CouponNumber can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPayableInterests.getDateBox() == null ? " DateBox can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPayableInterests.getApproximateValue() == null ? " ApproximateValue can't be null,"
				: "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPayableInterests.getCurrency() == null ? " Currency can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPayableInterests.getDetained() == null ? " Detained can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPayableInterests.getBalanceDetailPayment() == null
				? " BalanceDetailPayment can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPayableInterests.getPayNumber() == null ? " PayNumber can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPayableInterests.getPrePrintNumber() == null ? " PrePrintNumber can't be null,"
				: "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPayableInterests.getPrintNumber() == null ? " PrintNumber can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPayableInterests.getTax() == null ? " Tax can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPayableInterests.getValue() == null ? " Value can't be null" : "OK";
		msgErr = msgErr + messageError;

		if (!messageError.equals("OK"))
			// throw new IllegalArgumentException(messageError);
			throw new IllegalArgumentException(msgErr);
		return true;
	}

}
