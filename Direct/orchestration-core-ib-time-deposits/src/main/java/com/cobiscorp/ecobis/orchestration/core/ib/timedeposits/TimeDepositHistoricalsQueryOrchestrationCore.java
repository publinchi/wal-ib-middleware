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
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsHistoricalsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositsHistoricalsResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Secuential;
import com.cobiscorp.ecobis.ib.orchestration.dtos.TimeDepositsHistoricals;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceTimeDeposits;

/**
 * 
 * @author jchonillo
 *
 */
@Component(name = "TimeDepositHistoricalsQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "TimeDepositHistoricalsQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TimeDepositHistoricalsQueryOrchestrationCore") })
public class TimeDepositHistoricalsQueryOrchestrationCore extends QueryBaseTemplate {

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

		TimeDepositsHistoricalsResponse aHistoricalsResponse = null;
		TimeDepositsHistoricalsRequest aHistoricalsRequest = transformTimeDepositsHistoricalsRequest(request.clone());
		if (logger.isDebugEnabled())
			logger.logDebug("OBJ: " + aHistoricalsRequest.getProduct().getProductNumber());

		try {
			if (logger.isDebugEnabled())
				logger.logDebug("executeQuery");
			messageError = "get: ERROR EXECUTING SERVICE";
			if (logger.isDebugEnabled())
				logger.logDebug("request historicals: " + request);
			messageLog = "getTimeDepositHistoricals: " + aHistoricalsRequest.getSecuential().getSecuential();
			queryName = "getTimeDepositHistoricals";
			aHistoricalsRequest.setOriginalRequest(request);
			aHistoricalsResponse = coreServiceTimeDeposit.getTimeDepositHistoricals(aHistoricalsRequest);

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

		return transformProcedureResponse(aHistoricalsResponse);
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration TimeDepositHistoricals");
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
	 * Transformación de ProcedureRequest a TimeDepositsMovementsRequest
	 ********************/

	private TimeDepositsHistoricalsRequest transformTimeDepositsHistoricalsRequest(IProcedureRequest aRequest) {

		TimeDepositsHistoricalsRequest timeDeposHistRequest = new TimeDepositsHistoricalsRequest();

		Currency wCurrency = new Currency();
		Product wProduct = new Product();
		Secuential secuential = new Secuential();

		if (logger.isDebugEnabled())
			logger.logDebug(
					"Procedure TimeDepositsHistoricalsRequest to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;
		messageError = aRequest.readValueParam("@i_num_banco") == null ? " - @i_num_banco can't be null" : "";
		messageError += aRequest.readValueParam("@i_secuencial") == null ? " - @i_secuencial can't be null" : "";
		messageError += aRequest.readValueParam("@i_formato_fecha") == null ? " - @i_formato_fecha can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		wCurrency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));

		wProduct.setCurrency(wCurrency);
		wProduct.setProductNumber(aRequest.readValueParam("@i_cta"));

		secuential.setSecuential(aRequest.readValueParam("@i_secuencial"));

		timeDeposHistRequest.setSecuential(secuential);
		timeDeposHistRequest.setProductId(Integer.parseInt(aRequest.readValueParam("@i_prod")));
		timeDeposHistRequest.setDateFormat(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha")));
		timeDeposHistRequest.setProduct(wProduct);

		// timeDeposMoveRequest.setTimeDepositsMovements(timeDeposMove);
		return timeDeposHistRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformProcedureResponse(
			TimeDepositsHistoricalsResponse aTimeDepositsHistoricalsResponse) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		// Agregar Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("sequence", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("coupon", ICTSTypes.SYBVARCHAR, 100));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("transactionDate", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("transactionCode", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("description", ICTSTypes.SYBVARCHAR, 100));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("value", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("observation", ICTSTypes.SYBVARCHAR, 100));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("funcionary", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("rate", ICTSTypes.SYBVARCHAR, 10));

		if (aTimeDepositsHistoricalsResponse.getReturnCode() == 0) {

			for (TimeDepositsHistoricals aTimeDepositsHistoricals : aTimeDepositsHistoricalsResponse
					.getDepositsHistoricals()) {

				if (!IsValidTimeDepositsHistoricalResponse(aTimeDepositsHistoricals))
					return null;

				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, aTimeDepositsHistoricals.getSequence().toString()));
				row.addRowData(2, new ResultSetRowColumnData(false, aTimeDepositsHistoricals.getCoupon().toString()));
				row.addRowData(3, new ResultSetRowColumnData(false, aTimeDepositsHistoricals.getTransactionDate()));
				row.addRowData(4,
						new ResultSetRowColumnData(false, aTimeDepositsHistoricals.getTransactionCode().toString()));
				row.addRowData(5, new ResultSetRowColumnData(false, aTimeDepositsHistoricals.getDescription()));
				row.addRowData(6, new ResultSetRowColumnData(false, aTimeDepositsHistoricals.getValue().toString()));
				row.addRowData(7, new ResultSetRowColumnData(false, aTimeDepositsHistoricals.getObservation()));
				row.addRowData(8, new ResultSetRowColumnData(false, aTimeDepositsHistoricals.getFunctionary()));
				row.addRowData(9, new ResultSetRowColumnData(false, aTimeDepositsHistoricals.getRate().toString()));

				data.addRow(row);
			}

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock);
		} else {
			wProcedureResponse = Utils.returnException(aTimeDepositsHistoricalsResponse.getMessages());
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

	private boolean IsValidTimeDepositsHistoricalResponse(TimeDepositsHistoricals timeDepositsHistoricals) {
		String messageError = null;
		String msgErr = null;

		messageError = timeDepositsHistoricals.getSequence() == null ? " Sequence can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsHistoricals.getCoupon() == null ? " Coupon can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsHistoricals.getTransactionDate() == null ? " TransactionDate can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsHistoricals.getTransactionCode() == null ? " TransactionCode can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsHistoricals.getDescription() == null ? " Description can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsHistoricals.getValue() == null ? " Value can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsHistoricals.getObservation() == null ? " Observation can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsHistoricals.getFunctionary() == null ? " Functionary can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositsHistoricals.getRate() == null ? " Rate can't be null," : "OK";
		msgErr = msgErr + messageError;

		if (!messageError.equals("OK"))
			// throw new IllegalArgumentException(messageError);
			throw new IllegalArgumentException(msgErr);
		return true;
	}

}
