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
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TimeDepositResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.TimeDeposit;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceTimeDeposits;

/**
 * 
 * @author jveloz
 *
 */
@Component(name = "TimeDepositDetailQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "TimeDepositDetailQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TimeDepositDetailQueryOrchestrationCore") })
public class TimeDepositDetailQueryOrchestrationCore extends QueryBaseTemplate {

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

		TimeDepositResponse aTimeDepositResponse = null;
		TimeDepositRequest aTimeDepositRequest = transformTimeDepositRequest(request.clone());

		if (logger.isDebugEnabled())
			logger.logDebug("OBJ: " + aTimeDepositRequest.getTimeDeposit().getProduct().getProductNumber());

		try {
			if (logger.isDebugEnabled())
				logger.logDebug("executeQuery");
			messageError = "get: ERROR EXECUTING SERVICE";
			messageLog = "getTimeDepositDetail: "
					+ aTimeDepositRequest.getTimeDeposit().getProduct().getProductNumber();
			queryName = "getTimeDepositDetail";
			IProcedureResponse aProcedureResponse = (IProcedureResponse) aBagSPJavaOrchestration
					.get(QueryBaseTemplate.RESPONSE_VALIDATE_LOCAL);
			aTimeDepositRequest.setOriginalRequest(request);
			aTimeDepositRequest.setCustomerCode(Integer.parseInt(aProcedureResponse.readValueParam("@o_cliente_mis")));
			aTimeDepositResponse = coreServiceTimeDeposit.getTimeDepositDetail(aTimeDepositRequest);
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

		return transformProcedureResponse(aTimeDepositResponse);
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// Valida Inyección de dependencias
		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration");
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreServiceTimeDeposit", coreServiceTimeDeposit);
		Utils.validateComponentInstance(mapInterfaces);
		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration");
		try {
			if (logger.isDebugEnabled())
				logger.logDebug("INICIO> anOrginalRequest" + anOrginalRequest);
			executeStepsQueryBase(anOrginalRequest, aBagSPJavaOrchestration);
			if (logger.isDebugEnabled())
				logger.logDebug("executeJavaOrchestration");
			return processResponse(anOrginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {

			return Utils.returnExceptionService(anOrginalRequest, e);
		}

	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = (IProcedureResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION");
		return response;

	}

	/******************
	 * Transformación de ProcedureRequest a TimeDepositRequest
	 ********************/

	private TimeDepositRequest transformTimeDepositRequest(IProcedureRequest aRequest) {
		TimeDepositRequest wTimeDepositRequest = new TimeDepositRequest();
		TimeDeposit wTimeDeposit = new TimeDeposit();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;
		messageError = aRequest.readValueParam("@i_cta") == null ? " - @i_cta can't be null" : "";
		messageError += aRequest.readValueParam("@i_prod") == null ? " - @i_prod can't be null" : "";
		messageError += aRequest.readValueParam("@i_mon") == null ? " - @i_mon can't be null" : "";
		messageError += aRequest.readValueParam("@i_login") == null ? " - @i_login can't be null" : "";
		messageError += aRequest.readValueParam("@i_formato_fecha") == null ? " - @i_formato_fecha can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		Product wProduct = new Product();
		Currency wCurrency = new Currency();
		wCurrency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));
		wProduct.setCurrency(wCurrency);
		wProduct.setProductNumber(aRequest.readValueParam("@i_cta"));
		wTimeDeposit.setProduct(wProduct);

		wTimeDepositRequest.setTimeDeposit(wTimeDeposit);
		wTimeDepositRequest.setUserName(aRequest.readValueParam("@i_login"));
		wTimeDepositRequest.setDateFormat(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha")));
		wTimeDepositRequest.setProductId(Integer.parseInt(aRequest.readValueParam("@i_prod")));

		return wTimeDepositRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformProcedureResponse(TimeDepositResponse aTimeDepositResponse) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		// Agregar Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("productNumber", ICTSTypes.SQLVARCHAR, 30));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("openningDate", ICTSTypes.SQLVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("expirationDate", ICTSTypes.SQLVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("capitalBalance", ICTSTypes.SQLMONEY, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("totalInterestIncome", ICTSTypes.SQLDECIMAL, 3));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("rate", ICTSTypes.SQLVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("term", ICTSTypes.SQLINT4, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("capitalBalanceMaturity", ICTSTypes.SQLMONEY, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("automaticRenewal", ICTSTypes.SQLVARCHAR, 1));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("isCompounded", ICTSTypes.SQLVARCHAR, 1));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("frecuencyOfPayment", ICTSTypes.SQLVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("accountOfficer", ICTSTypes.SQLINT4, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("valueDate", ICTSTypes.SQLVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("calculationBase", ICTSTypes.SQLINT4, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("productAbbreviation", ICTSTypes.SQLVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("productAlias", ICTSTypes.SQLVARCHAR, 10));

		if (aTimeDepositResponse.getReturnCode() == 0) {

			if (!IsValidTimeDepositsDetailResponse(aTimeDepositResponse))
				return null;

			IResultSetRow row = new ResultSetRow();

			row.addRowData(1, new ResultSetRowColumnData(false,
					aTimeDepositResponse.getTimeDeposit().getProduct().getProductNumber().toString())); // productNumber
			row.addRowData(2, new ResultSetRowColumnData(false,
					aTimeDepositResponse.getTimeDeposit().getOpeningDate().toString()));
			row.addRowData(3,
					new ResultSetRowColumnData(false, aTimeDepositResponse.getTimeDeposit().getExpirationDate()));
			row.addRowData(4,
					new ResultSetRowColumnData(false, aTimeDepositResponse.getTimeDeposit().getAmount().toString()));
			row.addRowData(5, new ResultSetRowColumnData(false,
					aTimeDepositResponse.getTimeDeposit().getTotalRateEstimed().toString()));
			row.addRowData(6,
					new ResultSetRowColumnData(false, aTimeDepositResponse.getTimeDeposit().getRate().toString()));
			row.addRowData(7,
					new ResultSetRowColumnData(false, aTimeDepositResponse.getTimeDeposit().getTerm().toString()));
			row.addRowData(8, new ResultSetRowColumnData(false,
					aTimeDepositResponse.getTimeDeposit().getAmountEstimed().toString()));
			row.addRowData(9,
					new ResultSetRowColumnData(false, aTimeDepositResponse.getTimeDeposit().getAutomaticRenewal()));
			row.addRowData(10,
					new ResultSetRowColumnData(false, aTimeDepositResponse.getTimeDeposit().getIsCompounded()));
			row.addRowData(11,
					new ResultSetRowColumnData(false, aTimeDepositResponse.getTimeDeposit().getFrecuencyOfPayment()));
			row.addRowData(12,
					new ResultSetRowColumnData(false, aTimeDepositResponse.getTimeDeposit().getAccountOfficer()));
			row.addRowData(13, new ResultSetRowColumnData(false, aTimeDepositResponse.getTimeDeposit().getValueDate()));
			row.addRowData(14, new ResultSetRowColumnData(false,
					aTimeDepositResponse.getTimeDeposit().getCalculationBase().toString()));
			row.addRowData(15, new ResultSetRowColumnData(false,
					aTimeDepositResponse.getTimeDeposit().getProduct().getProductNemonic()));
			row.addRowData(16, new ResultSetRowColumnData(false,
					aTimeDepositResponse.getTimeDeposit().getProduct().getProductAlias()));

			data.addRow(row);

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock);

		} else {
			wProcedureResponse = Utils.returnException(aTimeDepositResponse.getMessages());
		}
		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;
	}

	private boolean IsValidTimeDepositsDetailResponse(TimeDepositResponse timeDepositResponse) {
		String messageError = null;
		String msgErr = null;

		messageError = timeDepositResponse.getTimeDeposit().getProduct().getProductNumber() == null
				? " Product Number can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositResponse.getTimeDeposit().getOpeningDate() == null ? " Openning Date can't be null,"
				: "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositResponse.getTimeDeposit().getExpirationDate() == null
				? " Expiration Date can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositResponse.getTimeDeposit().getAmount() == null ? " Amount can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositResponse.getTimeDeposit().getTotalRateEstimed() == null
				? " Total Rate Estimed can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositResponse.getTimeDeposit().getRate() == null ? " Rate can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositResponse.getTimeDeposit().getTerm() == null ? " Term can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositResponse.getTimeDeposit().getAmountEstimed() == null
				? " Amount Estimed can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositResponse.getTimeDeposit().getAutomaticRenewal() == null
				? " AutomaticRenewal can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositResponse.getTimeDeposit().getIsCompounded() == null ? " Is Compounded can't be null,"
				: "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositResponse.getTimeDeposit().getFrecuencyOfPayment() == null
				? " FrecuencyOfPayment can't be null" : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositResponse.getTimeDeposit().getAccountOfficer() == null
				? " Account Officer can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositResponse.getTimeDeposit().getValueDate() == null ? " Value Date can't be null" : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositResponse.getTimeDeposit().getCalculationBase() == null
				? " Calculation Base can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositResponse.getTimeDeposit().getProduct().getProductNemonic() == null
				? " Product Nemonic can't be null" : "OK";
		msgErr = msgErr + messageError;
		messageError = timeDepositResponse.getTimeDeposit().getProduct().getProductAlias() == null
				? " Product Alias can't be null," : "OK";
		msgErr = msgErr + messageError;

		if (!messageError.equals("OK"))
			// throw new IllegalArgumentException(messageError);
			throw new IllegalArgumentException(msgErr);

		return true;
	}
}
