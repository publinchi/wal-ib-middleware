package com.cobiscorp.ecobis.ib.orchestration.transaction.cost;

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
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
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
import com.cobiscorp.ecobis.ib.application.dtos.AccountingParameterRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AccountingParameterResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TransactionCostRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TransactionCostResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountingParameter;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceTransactionCost;

@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Component(name = "TransactionCostQueryCore", immediate = false)
@Properties(value = { @Property(name = "service.description", value = "TransactionCostQueryCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TransactionCostQueryCore") })
public class TransactionCostQueryCore extends QueryBaseTemplate {
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	private static final String CLASS_NAME = " >-----> ";
	private static ILogger logger = LogFactory.getLogger(TransactionCostQueryCore.class);

	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	public void bindCoreServer(ICoreServer service) {
		coreServer = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreServer(ICoreServer service) {
		coreServer = null;
	}

	@Reference(referenceInterface = ICoreServiceTransactionCost.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindcoreServiceCost", unbind = "unbindcoreServiceCost")
	private ICoreServiceTransactionCost coreServiceCost;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindcoreServiceCost(ICoreServiceTransactionCost service) {
		coreServiceCost = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindcoreServiceCost(ICoreServiceTransactionCost service) {
		coreServiceCost = null;
	}

	@Reference(referenceInterface = ICoreServiceMonetaryTransaction.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceMonetaryTransaction", unbind = "unbindCoreServiceMonetaryTransaction")
	protected ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction;

	public void bindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = service;
	}

	public void unbindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate#
	 * executeQuery(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		String messageError = null;
		String messageLog = null;
		String queryName = null;
		ServerRequest requestServer = new ServerRequest();
		ServerResponse responseServer = new ServerResponse();

		try {
			responseServer = coreServer.getServerStatus(requestServer);
		} catch (CTSServiceException e) {
			e.printStackTrace();
			if (logger.isErrorEnabled())
				logger.logError(CLASS_NAME + "ERROR EN EJECUCION DEL SERVICIO");
			if (logger.isDebugEnabled())
				logger.logDebug(CLASS_NAME + "ERROR EN EJECUCION DEL SERVICIO");

			return null;
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
			if (logger.isErrorEnabled())
				logger.logError(CLASS_NAME + "ERROR EN EJECUCION DEL SERVICIO");
			if (logger.isDebugEnabled())
				logger.logDebug(CLASS_NAME + "ERROR EN EJECUCION DEL SERVICIO");

			return null;
		}

		try {
			messageError = "getTransactionCost: ERROR EXECUTING SERVICE";
			messageLog = "getTransactionCost";
			queryName = "getTransactionCost";
			aBagSPJavaOrchestration.put(LOG_MESSAGE, messageLog);
			aBagSPJavaOrchestration.put(QUERY_NAME, queryName);

			if (responseServer.getOnLine()
					|| (!responseServer.getOnLine() && responseServer.getOfflineWithBalances())) {
				if (logger.isDebugEnabled())
					logger.logDebug("antes de transformToTransactionCost");

				TransactionCostResponse aTransactionCostResponse = null;
				TransactionCostRequest aTransactionCostRequest = transformToTransactionCost(request.clone());

				aTransactionCostRequest.setOriginalRequest(request);
				AccountingParameterRequest anAccountingParameterRequest = new AccountingParameterRequest();

				anAccountingParameterRequest.setOriginalRequest(request);
				anAccountingParameterRequest.setTransaction(
						Utils.getTransactionMenu(Integer.parseInt(request.readValueParam("@i_transaccion"))));

				AccountingParameterResponse accountingParameterResponse = coreServiceMonetaryTransaction
						.getAccountingParameter(anAccountingParameterRequest);

				if (!accountingParameterResponse.getSuccess())
					return buildOfflineResponse();

				Map<String, AccountingParameter> map = null;
				if (anAccountingParameterRequest.getTransaction() != 18862)
					map = existsAccountingParameter(accountingParameterResponse,
							Integer.parseInt(anAccountingParameterRequest.getTransaction() != 18862
									? request.readValueParam("@i_producto") : "9"),
							"C", "D");

				if (map != null) {

					if (map.get("ACCOUNTING_PARAM").getService() != null)
						aTransactionCostRequest.setServiceId(map.get("ACCOUNTING_PARAM").getService());
					if (map.get("ACCOUNTING_PARAM").getCause() != null)
						aTransactionCostRequest.setEntryId(map.get("ACCOUNTING_PARAM").getCause());
				}

				aTransactionCostResponse = coreServiceCost.getTransactionCost(aTransactionCostRequest);

				return transformProcedureResponse(aTransactionCostResponse, aBagSPJavaOrchestration);
			} else
				return buildOfflineResponse();

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

	}

	/**
	 * buildOfflineResponse: Builds offline response
	 * 
	 * @return IProcedureResponse
	 */
	private IProcedureResponse buildOfflineResponse() {
		IProcedureResponse wIProcedureResponse = new ProcedureResponseAS();

		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		IResultSetRow row = new ResultSetRow();
		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

		wIProcedureResponse.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
		metaData.addColumnMetaData(new ResultSetHeaderColumn("cost", ICTSTypes.SQLVARCHAR, 12));
		row.addRowData(1, new ResultSetRowColumnData(false, "0.00"));
		data.addRow(row);
		resultBlock = new ResultSetBlock(metaData, data);
		wIProcedureResponse.addResponseBlock(resultBlock);
		wIProcedureResponse.addParam("@o_costo", ICTSTypes.SYBMONEY, 0, "0.00");

		return wIProcedureResponse;
	}

	/**
	 * transformToTransactionCost: Transforms procedure request to trn cost
	 * request
	 * 
	 * @param aRequest
	 * @return TransactionCostRequest
	 */
	private TransactionCostRequest transformToTransactionCost(IProcedureRequest aRequest) {
		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;
		messageError = aRequest.readValueParam("@i_cuenta") == null ? " - @i_cuenta can't be null" : "";
		messageError += messageError + aRequest.readValueParam("@i_producto") == null ? " - @i_producto can't be null"
				: "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		TransactionCostRequest aTransactionCostRequest = new TransactionCostRequest();
		Product aProduct = new Product();

		if (aRequest.readValueParam("@i_moneda") != null) {
			Currency aCurrency = new Currency();
			aCurrency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_moneda")));
			aProduct.setCurrency(aCurrency);
		}
		aProduct.setProductNumber(aRequest.readValueParam("@i_cuenta"));
		aProduct.setProductType(Integer.parseInt(aRequest.readValueParam("@i_producto")));
		aTransactionCostRequest.setAccount(aProduct);

		if (aRequest.readValueParam("@i_cliente") != null) {
			Client client = new Client();
			client.setId(aRequest.readValueParam("@i_cliente"));
			aTransactionCostRequest.setClient(client);
		}

		if (aRequest.readValueParam("@i_rubro") != null)
			aTransactionCostRequest.setEntryId(aRequest.readValueParam("@i_rubro"));
		aTransactionCostRequest.setServiceId(aRequest.readValueParam("@i_servicio"));
		aTransactionCostRequest.setTrnId(Integer.parseInt(aRequest.readValueParam("@i_transaccion")));
		if (aRequest.readValueParam("@i_operacion") != null)
			aTransactionCostRequest.setOperation(aRequest.readValueParam("@i_operacion"));

		return aTransactionCostRequest;
	}

	/**
	 * transformProcedureResponse: Transforms trx cost response to procedure
	 * response
	 * 
	 * @param aTrnCostResponse
	 * @param aBagSPJavaOrchestration
	 * @return IProcedureResponse
	 */
	private IProcedureResponse transformProcedureResponse(TransactionCostResponse aTrnCostResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = new ProcedureResponseAS();
		response.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");

		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		IResultSetRow row = new ResultSetRow();
		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

		response.setReturnCode(aTrnCostResponse.getReturnCode());

		if (aTrnCostResponse.getReturnCode() == 0) {
			metaData.addColumnMetaData(new ResultSetHeaderColumn("cost", ICTSTypes.SQLVARCHAR, 12));
			row.addRowData(1, new ResultSetRowColumnData(false, String.valueOf(aTrnCostResponse.getCost())));
			data.addRow(row);
			resultBlock = new ResultSetBlock(metaData, data);
			response.addResponseBlock(resultBlock);
			response.addParam("@o_costo", ICTSTypes.SYBMONEY, 0, String.valueOf(aTrnCostResponse.getCost()));
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, response);
		} else {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException(aTrnCostResponse.getMessages())); // COLOCA
																														// ERRORES
																														// COMO
																														// RESPONSE
																														// DE
																														// LA
																														// TRANSACCIÃ“N
			response = Utils.returnException(aTrnCostResponse.getMessages());
		}

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + response.getProcedureResponseAsString());

		return response;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		String messageErrorQuery = null;
		messageErrorQuery = (String) aBagSPJavaOrchestration.get(QUERY_NAME);

		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreServer", coreServer);
		mapInterfaces.put("coreServiceCost", coreServiceCost);
		mapInterfaces.put("coreServiceMonetaryTransaction", coreServiceMonetaryTransaction);

		Utils.validateComponentInstance(mapInterfaces);

		try {

			IProcedureResponse responseExecuteQuery = executeQuery(anOrginalRequest, aBagSPJavaOrchestration);

			if (Utils.flowError(messageErrorQuery + " --> executeQuery", responseExecuteQuery)) {
				if (logger.isInfoEnabled())
					logger.logInfo(CLASS_NAME + messageErrorQuery);
			}
			;
			if (logger.isDebugEnabled())
				logger.logDebug(
						"transformProcedureResponse Final -->" + responseExecuteQuery.getProcedureResponseAsString());
			return responseExecuteQuery;

		} catch (Exception e) {
			return null;
		}
	}

	private Map<String, AccountingParameter> existsAccountingParameter(
			AccountingParameterResponse anAccountingParameterResponse, int product, String type, String sign) {

		Map<String, AccountingParameter> map = null;
		if (anAccountingParameterResponse == null)
			return map;

		if (anAccountingParameterResponse.getAccountingParameters().size() == 0)
			return map;

		for (AccountingParameter parameter : anAccountingParameterResponse.getAccountingParameters()) {

			if (parameter.getTypeCost().equals(type) && parameter.getProductId() == product
					&& parameter.getSign().equals(sign)) {
				if (logger.isDebugEnabled())
					logger.logDebug("SI HAY TRN: " + String.valueOf(parameter.getTransaction()) + " CAUSA: "
							+ parameter.getCause() + " TIPO :" + parameter.getTypeCost());
				map = new HashMap<String, AccountingParameter>();
				map.put("ACCOUNTING_PARAM", parameter);
				break;
			}
		}
		return map;
	}
}
