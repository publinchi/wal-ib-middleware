package com.cobiscorp.ecobis.orchestration.core.ib.cards.querys;

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
import com.cobiscorp.ecobis.ib.application.dtos.CreditCardBalanceRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CreditCardBalanceResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CreditCardPrizeRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CreditCardPrizeResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CreditCardBalance;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CreditCardPrize;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCardsQuery;

@Component(name = "CreditCardInformationOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "CreditCardInformationOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CreditCardInformationOrchestrationCore") })

public class CreditCardInformationOrchestrationCore extends QueryBaseTemplate {
	@Reference(referenceInterface = ICoreServiceCardsQuery.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceCardsQuery coreService;
	ILogger logger = LogFactory.getLogger(CreditCardInformationOrchestrationCore.class);

	// private static final String COBIS_CONTEXT = "COBIS";
	// private static final String LOCAL_UPDATE = "LOCAL_UPDATE";
	private static final int TRN_GET_BALANCE = 1801029;
	private static final int TRN_GET_PRIZE = 1875036;

	public void bindCoreService(ICoreServiceCardsQuery service) {
		coreService = service;
	}

	@Override
	protected IProcedureResponse validateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		// TODO Auto-generated method stub
		IProcedureResponse response = new ProcedureResponseAS();
		response.setReturnCode(0);
		return response;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreServiceCardsQuery service) {
		coreService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		Object object = new Object();
		IProcedureResponse wResponseCreditCard = null;
		String messageError = null;
		String messageLog = null;
		String queryName = null;
		CreditCardBalanceResponse aCreditCardBalanceResponse = null;
		CreditCardPrizeResponse creditCardPrizeResponse = null;
		int wTrn = Integer.parseInt(request.readValueParam("@t_trn"));
		showParameters(request.clone(), wTrn);
		switch (wTrn) {
		case TRN_GET_BALANCE:
			try {
				CreditCardBalanceRequest aCreditCardBalanceRequest = transformCreditCardBalanceRequest(request.clone());
				messageError = "getBalance: ERROR EXECUTING SERVICE";
				messageLog = "getBalance " + aCreditCardBalanceRequest.getCard();
				queryName = "getBalance";

				aCreditCardBalanceRequest.setOriginalRequest(request);
				messageError = "GetBalanceCreditCard: ERROR EN EJECUCION DEL SERVICIO";
				aCreditCardBalanceResponse = coreService.getBalanceCreditCard(aCreditCardBalanceRequest);
				aBagSPJavaOrchestration.put("RESPONSE_TRN", "RESPONSE_BALANCE_CREDIT_CARD");
				aBagSPJavaOrchestration.put("MESSAGE_LOG", "CONSULTA DE BALANCE DE TARJETA DE CREDITO");
				aBagSPJavaOrchestration.put("RESPONSE_BALANCE_CREDIT_CARD", wResponseCreditCard);
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
			object = transformProcedureResponse(aCreditCardBalanceResponse);
			break;
		case TRN_GET_PRIZE:
			try {
				CreditCardPrizeRequest creditCardPrizeRequest = transformCreditCardPrizeRequest(request.clone());

				messageError = "GetPrize: ERROR EN EJECUCION DEL SERVICIO";
				messageLog = "GetPrize " + creditCardPrizeRequest.getProductNumber();
				queryName = "GetPrize";

				creditCardPrizeRequest.setOriginalRequest(request);
				messageError = "GetPrize : ERROR EN EJECUCION DEL SERVICIO";
				creditCardPrizeResponse = coreService.getPrize(creditCardPrizeRequest);
				aBagSPJavaOrchestration.put("RESPONSE_TRN", "RESPONSE_PRIZE_CREDIT_CARD");
				aBagSPJavaOrchestration.put("MESSAGE_LOG", "CONSULTA DE MILLAS DE TARJETA DE CREDITO");
				aBagSPJavaOrchestration.put("RESPONSE_PRIZE_CREDIT_CARD", wResponseCreditCard);
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
			object = transformCreditCardPrizeResponse(creditCardPrizeResponse);
			break;

		default:
			break;
		}

		return (IProcedureResponse) object;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// Valida Inyección de dependencias

		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		Utils.validateComponentInstance(mapInterfaces);

		try {
			aBagSPJavaOrchestration.put(LOG_MESSAGE, "CONSULTA DE TARJETA DE CRÉDITO Y MILLAS ");
			executeStepsQueryBase(anOrginalRequest, aBagSPJavaOrchestration);

			return processResponse(anOrginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {

			return Utils.returnExceptionService(anOrginalRequest, e);
		}
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = (IProcedureResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION");
		return response;
	}

	private boolean IsValidtransformCreditCardPrize(CreditCardPrize aCreditCardPrizeResponse) {

		String messageError = null;

		messageError = aCreditCardPrizeResponse.getCutoffDate() == null ? " - CutoffDate can't be null" : "";
		messageError = aCreditCardPrizeResponse.getCard() == null ? " - Card can't be null" : "";
		messageError = aCreditCardPrizeResponse.getTotalPrize() == null ? " - TotalPrize can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		return true;
	}

	private CreditCardPrizeRequest transformCreditCardPrizeRequest(IProcedureRequest aRequest) {
		CreditCardPrizeRequest creditCardPrizeRequest = new CreditCardPrizeRequest();
		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;

		messageError = aRequest.readValueParam("@i_formato_fecha") == null ? " - @i_formato_fecha can't be null" : "";
		messageError = aRequest.readValueParam("@i_tarjeta") == null ? " - @i_tarjeta can't be null" : "";
		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		creditCardPrizeRequest.setDateFormatId(aRequest.readValueParam("@i_formato_fecha"));
		creditCardPrizeRequest.setProductNumber(aRequest.readValueParam("@i_tarjeta"));
		creditCardPrizeRequest.setTransactionNumber(aRequest.readValueParam("@t_trn"));

		return creditCardPrizeRequest;
	}

	private IProcedureResponse transformCreditCardPrizeResponse(CreditCardPrizeResponse aRequest) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		// Agregar Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("card", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("totalPrize", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("cutoffDate", ICTSTypes.SQLVARCHAR, 20));

		CreditCardPrize aCreditCardPrize = aRequest.getaCreditCardPrizeResponse();

		if (!IsValidtransformCreditCardPrize(aCreditCardPrize))
			return null;

		IResultSetRow row = new ResultSetRow();
		row.addRowData(1, new ResultSetRowColumnData(false, aCreditCardPrize.getCard().toString()));
		row.addRowData(2, new ResultSetRowColumnData(false, aCreditCardPrize.getTotalPrize().toString()));
		row.addRowData(3, new ResultSetRowColumnData(false, aCreditCardPrize.getCutoffDate().toString()));

		data.addRow(row);

		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

		wProcedureResponse.addResponseBlock(resultBlock);

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;
	}

	private CreditCardBalanceRequest transformCreditCardBalanceRequest(IProcedureRequest aRequest) {
		CreditCardBalanceRequest creditCardBalanceRequest = new CreditCardBalanceRequest();
		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;

		messageError = aRequest.readValueParam("@i_usuario") == null ? " - @i_usuario can't be null" : "";
		messageError = aRequest.readValueParam("@i_tarjeta") == null ? " - @i_tarjeta can't be null" : "";
		messageError = aRequest.readValueParam("@i_prod") == null ? " - @i_prod can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);
		creditCardBalanceRequest.setCard(aRequest.readValueParam("@i_tarjeta"));
		creditCardBalanceRequest.setName(aRequest.readValueParam("@i_usuario"));
		creditCardBalanceRequest.setProductId(Integer.parseInt(aRequest.readValueParam("@i_prod")));
		return creditCardBalanceRequest;
	}

	private IProcedureResponse transformProcedureResponse(CreditCardBalanceResponse aCreditCardBalanceResponse) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		// Agregar Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("localbalance", ICTSTypes.SQLMONEYN, 34));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("localMinimumPayment", ICTSTypes.SQLMONEYN, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("cashPaymentLocalCurrency", ICTSTypes.SQLMONEYN, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("availableLocal", ICTSTypes.SQLMONEYN, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("internationalBalance", ICTSTypes.SQLMONEYN, 34));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("internationalMinimumPayment", ICTSTypes.SQLMONEYN, 8));
		metaData.addColumnMetaData(
				new ResultSetHeaderColumn("cashPaymentInternationalCurrency", ICTSTypes.SQLMONEYN, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("availableInternational", ICTSTypes.SQLMONEYN, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("paymentDate", ICTSTypes.SQLVARCHAR, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("availableLocalEF", ICTSTypes.SQLMONEYN, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("availableInternationalEF", ICTSTypes.SQLMONEYN, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("debitLocalTransit", ICTSTypes.SQLMONEYN, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("debitInternationalTransit", ICTSTypes.SQLMONEYN, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("responseCode", ICTSTypes.SQLVARCHAR, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));

		CreditCardBalance aCreditCardBalance = aCreditCardBalanceResponse.getCreditCardBalanceCollection();

		if (!IsValidCreditCardBalance(aCreditCardBalance))
			return null;

		IResultSetRow row = new ResultSetRow();

		row.addRowData(1, new ResultSetRowColumnData(false, ""));
		row.addRowData(2, new ResultSetRowColumnData(false, ""));
		row.addRowData(3, new ResultSetRowColumnData(false, ""));
		row.addRowData(4, new ResultSetRowColumnData(false, ""));
		row.addRowData(5, new ResultSetRowColumnData(false, aCreditCardBalance.getLocalBalance().toString()));
		row.addRowData(6, new ResultSetRowColumnData(false, aCreditCardBalance.getLocalMinimutmPayment().toString()));
		row.addRowData(7,
				new ResultSetRowColumnData(false, aCreditCardBalance.getCashPaymentLocalCurrency().toString()));
		row.addRowData(8, new ResultSetRowColumnData(false, aCreditCardBalance.getAvailableLocal().toString()));
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
		row.addRowData(19, new ResultSetRowColumnData(false, ""));
		row.addRowData(20, new ResultSetRowColumnData(false, ""));
		row.addRowData(21, new ResultSetRowColumnData(false, aCreditCardBalance.getInternationalBalance().toString()));
		row.addRowData(22,
				new ResultSetRowColumnData(false, aCreditCardBalance.getInternationalMinimumPayment().toString()));
		row.addRowData(23,
				new ResultSetRowColumnData(false, aCreditCardBalance.getCashPaymentInternationalCurrency().toString()));
		row.addRowData(24,
				new ResultSetRowColumnData(false, aCreditCardBalance.getAvailableInternational().toString()));
		row.addRowData(25, new ResultSetRowColumnData(false, ""));
		row.addRowData(26, new ResultSetRowColumnData(false, ""));
		row.addRowData(27, new ResultSetRowColumnData(false, ""));
		row.addRowData(28, new ResultSetRowColumnData(false, ""));
		row.addRowData(29, new ResultSetRowColumnData(false, ""));
		row.addRowData(30, new ResultSetRowColumnData(false, ""));
		row.addRowData(31, new ResultSetRowColumnData(false, ""));
		row.addRowData(32, new ResultSetRowColumnData(false, ""));
		row.addRowData(33, new ResultSetRowColumnData(false, ""));
		row.addRowData(34, new ResultSetRowColumnData(false, ""));
		row.addRowData(35, new ResultSetRowColumnData(false, ""));
		row.addRowData(36, new ResultSetRowColumnData(false, ""));
		row.addRowData(37, new ResultSetRowColumnData(false, ""));
		row.addRowData(38, new ResultSetRowColumnData(false, ""));
		row.addRowData(39, new ResultSetRowColumnData(false, ""));
		row.addRowData(40, new ResultSetRowColumnData(false, ""));
		row.addRowData(41, new ResultSetRowColumnData(false, ""));
		row.addRowData(42, new ResultSetRowColumnData(false, ""));
		row.addRowData(43, new ResultSetRowColumnData(false, ""));
		row.addRowData(44, new ResultSetRowColumnData(false, ""));
		row.addRowData(45, new ResultSetRowColumnData(false, aCreditCardBalance.getPaymentDate().toString()));
		row.addRowData(46, new ResultSetRowColumnData(false, ""));
		row.addRowData(47, new ResultSetRowColumnData(false, ""));
		row.addRowData(48, new ResultSetRowColumnData(false, ""));
		row.addRowData(49, new ResultSetRowColumnData(false, ""));
		row.addRowData(50, new ResultSetRowColumnData(false, ""));
		row.addRowData(51, new ResultSetRowColumnData(false, aCreditCardBalance.getAvailableLocalEF().toString()));
		row.addRowData(52, new ResultSetRowColumnData(false, ""));
		row.addRowData(53, new ResultSetRowColumnData(false, ""));
		row.addRowData(54, new ResultSetRowColumnData(false, ""));
		row.addRowData(55, new ResultSetRowColumnData(false, ""));
		row.addRowData(56, new ResultSetRowColumnData(false, ""));
		row.addRowData(57,
				new ResultSetRowColumnData(false, aCreditCardBalance.getAvailableInternationalEF().toString()));
		row.addRowData(58, new ResultSetRowColumnData(false, ""));
		row.addRowData(59, new ResultSetRowColumnData(false, ""));
		row.addRowData(60, new ResultSetRowColumnData(false, ""));
		row.addRowData(61, new ResultSetRowColumnData(false, ""));
		row.addRowData(62, new ResultSetRowColumnData(false, ""));
		row.addRowData(63, new ResultSetRowColumnData(false, ""));
		row.addRowData(64, new ResultSetRowColumnData(false, ""));
		row.addRowData(65, new ResultSetRowColumnData(false, aCreditCardBalance.getDebitLocalTransit().toString()));
		row.addRowData(66, new ResultSetRowColumnData(false, ""));
		row.addRowData(67,
				new ResultSetRowColumnData(false, aCreditCardBalance.getDebitInternationalTransit().toString()));
		row.addRowData(68, new ResultSetRowColumnData(false, ""));
		row.addRowData(69, new ResultSetRowColumnData(false, ""));
		row.addRowData(70, new ResultSetRowColumnData(false, ""));
		row.addRowData(71, new ResultSetRowColumnData(false, ""));
		row.addRowData(72, new ResultSetRowColumnData(false, ""));
		row.addRowData(73, new ResultSetRowColumnData(false, ""));
		row.addRowData(74, new ResultSetRowColumnData(false, ""));
		row.addRowData(75, new ResultSetRowColumnData(false, aCreditCardBalance.getResponseCode().toString()));
		row.addRowData(76, new ResultSetRowColumnData(false, ""));

		data.addRow(row);

		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

		wProcedureResponse.addResponseBlock(resultBlock);
		wProcedureResponse.addParam("@o_fechavencimiento", ICTSTypes.SQLVARCHAR, 0,
				aCreditCardBalance.getFechavencimiento());
		wProcedureResponse.addParam("@o_pagominimolocal", ICTSTypes.SQLMONEY4, 0,
				aCreditCardBalance.getPagominimolocal().toString());
		wProcedureResponse.addParam("@o_pagocontadolocal", ICTSTypes.SQLMONEY4, 0,
				aCreditCardBalance.getPagocontadolocal().toString());
		wProcedureResponse.addParam("@o_pagominimoint", ICTSTypes.SQLMONEY4, 0,
				aCreditCardBalance.getPagominimoint().toString());
		wProcedureResponse.addParam("@o_pagocontadoint", ICTSTypes.SQLMONEY4, 0,
				aCreditCardBalance.getPagocontadoint().toString());
		wProcedureResponse.addParam("@o_saldolocal", ICTSTypes.SQLMONEY4, 0,
				aCreditCardBalance.getSaldolocal().toString());
		wProcedureResponse.addParam("@o_saldointernacional", ICTSTypes.SQLMONEY4, 0,
				aCreditCardBalance.getSaldointernacional().toString());
		wProcedureResponse.addParam("@o_saltotcortelocal", ICTSTypes.SQLMONEY4, 0,
				aCreditCardBalance.getSaltotcortelocal().toString());
		wProcedureResponse.addParam("@o_saltotcorteinter", ICTSTypes.SQLMONEY4, 0,
				aCreditCardBalance.getSaltotcorteinter().toString());
		wProcedureResponse.addParam("@o_fechacorte", ICTSTypes.SQLVARCHAR, 0, aCreditCardBalance.getFechacorte());
		wProcedureResponse.addParam("@o_error", ICTSTypes.SQLINT4, 0, aCreditCardBalance.getError().toString());
		wProcedureResponse.addParam("@o_descripcion", ICTSTypes.SQLVARCHAR, 0, aCreditCardBalance.getDescripcion());
		wProcedureResponse.addParam("@o_disponibleeflocal", ICTSTypes.SQLMONEY4, 0,
				aCreditCardBalance.getDisponibleeflocal().toString());
		wProcedureResponse.addParam("@o_disponibleefinter", ICTSTypes.SQLMONEY4, 0,
				aCreditCardBalance.getDisponibleefinter().toString());
		wProcedureResponse.addParam("@o_debitotransitolocal", ICTSTypes.SQLMONEY4, 0,
				aCreditCardBalance.getDebitotransitolocal().toString());
		wProcedureResponse.addParam("@o_debitotransitointer", ICTSTypes.SQLMONEY4, 0,
				aCreditCardBalance.getDebitotransitointer().toString());

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;
	}

	public void showParameters(IProcedureRequest aRequest, int aTrn) {
		if (logger.isInfoEnabled())
			logger.logInfo("TRANSACTION " + String.valueOf(aTrn) + " -->" + aRequest.getProcedureRequestAsString());
	}

	private boolean IsValidCreditCardBalance(CreditCardBalance aCreditCardBalance) {
		String messageError = null;

		messageError = aCreditCardBalance.getLocalBalance() == null ? " - LocalBalance can't be null" : "";
		messageError += aCreditCardBalance.getLocalMinimutmPayment() == null ? " - MinimutmPayment can't be null" : "";
		messageError += aCreditCardBalance.getCashPaymentLocalCurrency() == null
				? " - CashPaymentLocalCurrency can't be null" : "";
		messageError += aCreditCardBalance.getAvailableLocal() == null ? " - AvailableLocal can't be null" : "";
		messageError += aCreditCardBalance.getInternationalBalance() == null ? " - InternationalBalance can't be null"
				: "";
		messageError += aCreditCardBalance.getInternationalMinimumPayment() == null
				? " - InternationalMinimumPayment can't be null" : "";
		messageError += aCreditCardBalance.getCashPaymentInternationalCurrency() == null
				? " - CashPaymentInternationalCurrency can't be null" : "";
		messageError += aCreditCardBalance.getAvailableInternational() == null
				? " - AvailableInternational can't be null" : "";
		messageError += aCreditCardBalance.getPaymentDate() == null ? " - PaymentDate can't be null" : "";
		messageError += aCreditCardBalance.getAvailableLocalEF() == null ? " - AvailableLocalEF can't be null" : "";
		messageError += aCreditCardBalance.getAvailableInternationalEF() == null
				? " - AvailableInternationalEF can't be null" : "";
		messageError += aCreditCardBalance.getDebitLocalTransit() == null ? " - DebitLocalTransit can't be null" : "";
		messageError += aCreditCardBalance.getDebitInternationalTransit() == null
				? " - DebitInternationalTransit can't be null" : "";
		messageError += aCreditCardBalance.getResponseCode() == null ? " - ResponseCode can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		return true;
	}

}
