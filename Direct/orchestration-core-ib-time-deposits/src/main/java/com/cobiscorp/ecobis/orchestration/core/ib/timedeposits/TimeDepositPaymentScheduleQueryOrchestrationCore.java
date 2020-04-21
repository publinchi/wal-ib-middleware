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
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
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
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsPaymentScheduleRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsPaymentScheduleResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Secuential;
import com.cobiscorp.ecobis.ib.orchestration.dtos.TimeDepositsPaymentSchedule;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceTimeDeposits;

/**
 * 
 * @author areinoso
 *
 */
@Component(name = "TimeDepositPaymentScheduleQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = {
		@Property(name = "service.description", value = "TimeDepositPaymentScheduleQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TimeDepositPaymentScheduleQueryOrchestrationCore") })

public class TimeDepositPaymentScheduleQueryOrchestrationCore extends SPJavaOrchestrationBase {
	@Reference(referenceInterface = ICoreServiceTimeDeposits.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceTimeDeposits coreServiceTimeDeposit;
	private static ILogger logger = LogFactory.getLogger(TimeDepositPaymentScheduleQueryOrchestrationCore.class);

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

	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		String messageError = null;
		String messageLog = null;
		String queryName = null;

		TimeDepositsPaymentScheduleResponse aPaymentScheduleResponse = null;
		TimeDepositsPaymentScheduleRequest aPaymentScheduleRequest = transformTimeDepositsPaymentScheduleRequest(
				request.clone());
		if (logger.isDebugEnabled())
			logger.logDebug("OBJ: " + aPaymentScheduleRequest.getProduct().getProductNumber());

		try {
			if (logger.isDebugEnabled())
				logger.logDebug("executeQuery");
			messageError = "get: ERROR EXECUTING SERVICE";
			if (logger.isDebugEnabled())
				logger.logDebug("request historicals: " + request);
			messageLog = "getTimeDepositHistoricals: " + aPaymentScheduleRequest.getSecuential().getSecuential();
			queryName = "getTimeDepositHistoricals";
			aPaymentScheduleRequest.setOriginalRequest(request);
			aPaymentScheduleResponse = coreServiceTimeDeposit.getTimeDepositsPaymentSchedule(aPaymentScheduleRequest);

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

		aBagSPJavaOrchestration.put("LOG_MESSAGE", messageLog);
		aBagSPJavaOrchestration.put("QUERY_NAME", queryName);

		return transformProcedureResponse(aPaymentScheduleResponse);
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {

		String messageError = null;
		String messageLog = null;
		String queryName = null;

		TimeDepositsPaymentScheduleResponse aPaymentScheduleResponse = null;
		TimeDepositsPaymentScheduleRequest aPaymentScheduleRequest = transformTimeDepositsPaymentScheduleRequest(
				request.clone());

		if (logger.isDebugEnabled())
			logger.logDebug("OBJ: " + aPaymentScheduleRequest.getCulture());

		try {
			if (logger.isDebugEnabled())
				logger.logDebug("executeQuery");
			messageError = "get: ERROR EXECUTING SERVICE";
			if (logger.isDebugEnabled())
				logger.logDebug("request movements: " + request);
			messageLog = "getTimeDepositsPaymentSchedule: " + aPaymentScheduleRequest.getProduct().getProductNumber();
			queryName = "getTimeDepositsPaymentSchedule";
			aPaymentScheduleRequest.setOriginalRequest(request);
			aPaymentScheduleResponse = coreServiceTimeDeposit.getTimeDepositsPaymentSchedule(aPaymentScheduleRequest);
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

		aBagSPJavaOrchestration.put("LOG_MESSAGE", messageLog);
		aBagSPJavaOrchestration.put("QUERY_NAME", queryName);

		return transformProcedureResponse(aPaymentScheduleResponse);
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration TimeDepositPaymentSchedule");
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreServiceTimeDeposit", coreServiceTimeDeposit);
		Utils.validateComponentInstance(mapInterfaces);

		return executeQuery(anOrginalRequest, aBagSPJavaOrchestration);

	}

	/******************
	 * Transformación de ProcedureRequest a TimeDepositsPaymentScheduleRequest
	 ********************/
	private TimeDepositsPaymentScheduleRequest transformTimeDepositsPaymentScheduleRequest(IProcedureRequest aRequest) {

		TimeDepositsPaymentScheduleRequest timeDeposPaymentScheduleRequest = new TimeDepositsPaymentScheduleRequest();
		Secuential secuential = new Secuential();
		Product wProduct = new Product();
		Currency wCurrency = new Currency();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure TimeDepositsPaymentScheduleRequest to Transform->"
					+ aRequest.getProcedureRequestAsString());

		String messageError = null;
		messageError = aRequest.readValueParam("@i_cta") == null ? " - @i_cta can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		wCurrency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));

		wProduct.setCurrency(wCurrency);
		wProduct.setProductNumber(aRequest.readValueParam("@i_cta"));

		secuential.setSecuential(aRequest.readValueParam("@i_cuota"));

		timeDeposPaymentScheduleRequest.setProduct(wProduct);
		timeDeposPaymentScheduleRequest.setSecuential(secuential);
		timeDeposPaymentScheduleRequest.setProductId(Integer.parseInt(aRequest.readValueParam("@i_prod")));

		return timeDeposPaymentScheduleRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformProcedureResponse(
			TimeDepositsPaymentScheduleResponse aPaymentScheduleResponse) {

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		// Agregar Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("quota", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("paymentDate", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("quotaAmount", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("entity", ICTSTypes.SYBVARCHAR, 100));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("operationDescription", ICTSTypes.SYBVARCHAR, 100));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("addressDescription", ICTSTypes.SYBVARCHAR, 100));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("officeName", ICTSTypes.SYBVARCHAR, 100));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("bankNumberOperation", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("depositTypeDescription", ICTSTypes.SYBVARCHAR, 50));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("amount", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("paymentDescription", ICTSTypes.SYBVARCHAR, 100));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("currency", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("rate", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("expirationDate", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("status", ICTSTypes.SYBVARCHAR, 3));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("operationDaysNumber", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("insertDate", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("quotaValue", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("quotaDaysNumber", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("lastPaymentDate", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("interestEarned", ICTSTypes.SYBVARCHAR, 50));

		if (aPaymentScheduleResponse.getReturnCode() == 0) {

			for (TimeDepositsPaymentSchedule aTimeDepositsPaymentSchedule : aPaymentScheduleResponse
					.getDepositsPaymentSchedule()) {

				if (!IsValidTimeDepositsPaymentScheduleResponse(aTimeDepositsPaymentSchedule))

					return null;

				IResultSetRow row = new ResultSetRow();
				row.addRowData(1,
						new ResultSetRowColumnData(false, aTimeDepositsPaymentSchedule.getQuota().toString()));
				row.addRowData(2, new ResultSetRowColumnData(false, aTimeDepositsPaymentSchedule.getPaymentDate()));
				row.addRowData(3,
						new ResultSetRowColumnData(false, aTimeDepositsPaymentSchedule.getQuotaAmount().toString()));
				row.addRowData(4,
						new ResultSetRowColumnData(false, aTimeDepositsPaymentSchedule.getEntity().toString()));
				row.addRowData(5,
						new ResultSetRowColumnData(false, aTimeDepositsPaymentSchedule.getOperationDescription()));
				row.addRowData(6,
						new ResultSetRowColumnData(false, aTimeDepositsPaymentSchedule.getAddressDescription()));
				row.addRowData(7, new ResultSetRowColumnData(false, aTimeDepositsPaymentSchedule.getOfficeName()));
				row.addRowData(8,
						new ResultSetRowColumnData(false, aTimeDepositsPaymentSchedule.getBankNumberOperation()));
				row.addRowData(9,
						new ResultSetRowColumnData(false, aTimeDepositsPaymentSchedule.getDepositTypeDescription()));
				row.addRowData(10,
						new ResultSetRowColumnData(false, aTimeDepositsPaymentSchedule.getAmount().toString()));
				row.addRowData(11,
						new ResultSetRowColumnData(false, aTimeDepositsPaymentSchedule.getPaymentDescription()));
				row.addRowData(12,
						new ResultSetRowColumnData(false, aTimeDepositsPaymentSchedule.getCurrency().toString()));
				row.addRowData(13,
						new ResultSetRowColumnData(false, aTimeDepositsPaymentSchedule.getRate().toString()));
				row.addRowData(14, new ResultSetRowColumnData(false, aTimeDepositsPaymentSchedule.getExpirationDate()));
				row.addRowData(15, new ResultSetRowColumnData(false, aTimeDepositsPaymentSchedule.getStatus()));
				row.addRowData(16, new ResultSetRowColumnData(false,
						aTimeDepositsPaymentSchedule.getOperationDaysNumber().toString()));
				row.addRowData(17, new ResultSetRowColumnData(false, aTimeDepositsPaymentSchedule.getInsertDate()));
				row.addRowData(18, new ResultSetRowColumnData(false, aTimeDepositsPaymentSchedule.getQuotaValue()));
				row.addRowData(19, new ResultSetRowColumnData(false,
						aTimeDepositsPaymentSchedule.getQuotaDaysNumber().toString()));
				row.addRowData(20,
						new ResultSetRowColumnData(false, aTimeDepositsPaymentSchedule.getLastPaymentDate()));
				row.addRowData(21,
						new ResultSetRowColumnData(false, aTimeDepositsPaymentSchedule.getInterestEarned().toString()));

				data.addRow(row);
			}

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock);
		} else {
			wProcedureResponse = Utils.returnException(aPaymentScheduleResponse.getMessages());
		}

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}

	private boolean IsValidTimeDepositsPaymentScheduleResponse(
			TimeDepositsPaymentSchedule timeDepositsPaymentSchedule) {
		String messageError = null;
		String msgErr = null;

		messageError = timeDepositsPaymentSchedule.getAddressDescription() == null ? " ddressDescription can't be null,"
				: "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPaymentSchedule.getAmount() == null ? " Amount can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPaymentSchedule.getBankNumberOperation() == null
				? " BankNumberOperation can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPaymentSchedule.getCurrency() == null ? " Currency can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPaymentSchedule.getDepositTypeDescription() == null
				? " DepositTypeDescription can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPaymentSchedule.getEntity() == null ? " Entity can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPaymentSchedule.getExpirationDate() == null ? " ExpirationDate can't be null,"
				: "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPaymentSchedule.getInsertDate() == null ? " InsertDate can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPaymentSchedule.getInterestEarned() == null ? " InterestEarned can't be null,"
				: "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPaymentSchedule.getLastPaymentDate() == null ? " LastPaymentDate can't be null,"
				: "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPaymentSchedule.getOfficeName() == null ? " OfficeName can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPaymentSchedule.getOperationDaysNumber() == null
				? " OperationDaysNumber can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPaymentSchedule.getOperationDescription() == null
				? " OperationDescription can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPaymentSchedule.getPaymentDate() == null ? " PaymentDate can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPaymentSchedule.getPaymentDescription() == null
				? " PaymentDescription can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPaymentSchedule.getQuota() == null ? " Quota can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPaymentSchedule.getQuotaAmount() == null ? " QuotaAmount can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPaymentSchedule.getQuotaDaysNumber() == null ? " QuotaDaysNumber can't be null,"
				: "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPaymentSchedule.getQuotaValue() == null ? " QuotaValue can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPaymentSchedule.getRate() == null ? " Rate can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsPaymentSchedule.getStatus() == null ? " Status can't be null," : "OK";
		msgErr = msgErr + messageError;

		if (!messageError.equals("OK"))
			// throw new IllegalArgumentException(messageError);
			throw new IllegalArgumentException(msgErr);
		return true;
	}
}
