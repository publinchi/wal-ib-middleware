package com.cobiscorp.ecobis.orchestration.core.ib.query.accounts;

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
import com.cobiscorp.ecobis.ib.application.dtos.BalanceProductRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BalanceProductResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BalanceProduct;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceQuery;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;

@Component(name = "CcAccountBalanceQueryOrchestationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "CcAccountBalanceQueryOrchestationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CcAccountBalanceQueryOrchestationCore") })
public class CcAccountBalanceQueryOrchestationCore extends QueryBaseTemplate {

	private static final String CLASS_NAME = " >-----> ";
	/**
	 * Instance logger component
	 */
	private static ILogger logger = LogFactory.getLogger(CcAccountBalanceQueryOrchestationCore.class);

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceQuery.class, cardinality = ReferenceCardinality.MANDATORY_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	private ICoreServiceQuery coreServiceQuery;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreService(ICoreServiceQuery service) {
		coreServiceQuery = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreService(ICoreServiceQuery service) {
		coreServiceQuery = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		if (logger.isInfoEnabled())
			logger.logInfo(
					"Component-Name: " + this.getClass().getSimpleName() + ", Component-Version: $$project.version$$");
	}

	public IProcedureResponse executeTransactionOrchestation(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + " executeTransactionOrchestation");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Params Original Request: " + anOriginalRequest);
		BalanceProductRequest balanceProductRequest = new BalanceProductRequest();

		Product product = new Product();
		Currency currency = new Currency();

		product.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod").toString()));
		product.setProductNumber(anOriginalRequest.readValueParam("@i_cta").toString());
		currency.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon").toString()));

		product.setCurrency(currency);
		balanceProductRequest.setProduct(product);
		balanceProductRequest.setChannelId(anOriginalRequest.readValueParam("@s_servicio").toString());

		try {
			BalanceProductResponse wCcBalanceAccount = coreServiceQuery
					.getCheckingAccountBalanceByAccount(balanceProductRequest);

			if (wCcBalanceAccount == null) {
				if (logger.isDebugEnabled())
					logger.logDebug(CLASS_NAME + "executeTransactionOrchestation: OBJECTO DE RESPUESA VACIO");
				return null;
			}

			if (logger.isDebugEnabled())
				logger.logDebug(CLASS_NAME + "executeTransactionOrchestation:" + wCcBalanceAccount);

			IProcedureResponse wAhBalanceAccountResponse = transformDtoToProcedureResponse(wCcBalanceAccount);
			if ((wCcBalanceAccount != null) && (wCcBalanceAccount.getSuccess()))
				Utils.addResultSetDataAsParam(2, wAhBalanceAccountResponse, "H");

			return wAhBalanceAccountResponse;
		} catch (CTSServiceException e) {
			e.printStackTrace();
			if (logger.isInfoEnabled())
				logger.logDebug("executeTransactionOrchestation: ERROR EN EJECUCION DEL SERVICIO");

			return null;
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
			if (logger.isInfoEnabled())
				logger.logDebug("executeTransactionOrchestation: ERROR EN EJECUCION DEL SERVICIO");

			return null;
		}
	}

	private IProcedureResponse transformDtoToProcedureResponse(BalanceProductResponse balanceProductResponse) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Objeto a transformar:" + balanceProductResponse);

		IProcedureResponse response = new ProcedureResponseAS();
		Utils.transformBaseResponseToIprocedureResponse(balanceProductResponse, response);

		if (balanceProductResponse.getSuccess()) {
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("ProductName", ICTSTypes.SQLVARCHAR, 60));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("DateLastMovent", ICTSTypes.SQLCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("State", ICTSTypes.SQLVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("AccountingBalance", ICTSTypes.SQLMONEY, 21));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("InExchangeBalance", ICTSTypes.SQLMONEY, 21));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("AvailableBalance", ICTSTypes.SQLMONEY, 21));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Identification", ICTSTypes.SQLCHAR, 30));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Balance24H", ICTSTypes.SQLMONEY, 21));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Balance12H", ICTSTypes.SQLMONEY, 21));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("RotateBalance", ICTSTypes.SQLMONEY, 21));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("OpeningDate", ICTSTypes.SQLCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("OverdraftBalance", ICTSTypes.SQLMONEY, 21));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("BlockedNumber", ICTSTypes.SQLINT4, 11));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("setBlockedNumber", ICTSTypes.SQLMONEY, 21));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("DeliveryAddress", ICTSTypes.SQLVARCHAR, 60));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("CheckBalance", ICTSTypes.SQLMONEY, 21));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("EmbargoedBalance", ICTSTypes.SQLMONEY, 21));

			IResultSetRow row = new ResultSetRow();

			BalanceProduct balance = balanceProductResponse.getBalanceProduct();
			Product product = balance.getProduct();
			Client client = balance.getClient();

			row.addRowData(1, new ResultSetRowColumnData(false, product.getProductName().toString()));
			row.addRowData(2, new ResultSetRowColumnData(false, balance.getDateLastMovent().toString()));
			row.addRowData(3, new ResultSetRowColumnData(false, balance.getState().toString()));
			row.addRowData(4, new ResultSetRowColumnData(false, balance.getAccountingBalance().toString()));
			row.addRowData(5, new ResultSetRowColumnData(false, balance.getInExchangeBalance().toString()));
			row.addRowData(6, new ResultSetRowColumnData(false, balance.getAvailableBalance().toString()));
			row.addRowData(7, new ResultSetRowColumnData(false, client.getIdentification().toString()));
			row.addRowData(8, new ResultSetRowColumnData(false, balance.getBalance24H().toString()));
			row.addRowData(9, new ResultSetRowColumnData(false, balance.getBalance12H().toString()));
			row.addRowData(10, new ResultSetRowColumnData(false, balance.getRotateBalance().toString()));
			row.addRowData(11, new ResultSetRowColumnData(false, balance.getOpeningDate().toString()));
			row.addRowData(12, new ResultSetRowColumnData(false, balance.getOverdraftBalance().toString()));
			row.addRowData(13, new ResultSetRowColumnData(false, balance.getBlockedNumber().toString()));
			row.addRowData(14, new ResultSetRowColumnData(false, balance.getBlockedAmmount().toString()));
			row.addRowData(15, new ResultSetRowColumnData(false, balance.getDeliveryAddress().toString()));
			row.addRowData(16, new ResultSetRowColumnData(false, balance.getCheckBalance().toString()));
			row.addRowData(17, new ResultSetRowColumnData(false, balance.getEmbargoedBalance().toString()));
			data.addRow(row);

			// Agregar Data - Consolidado General
			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			response.addResponseBlock(resultBlock);
		}

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Objeto devuelto:" + response.getProcedureResponseAsString());
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Finalizando transformacion");
		return response;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		Map<String, Object> components = new HashMap<String, Object>();
		components.put("ICoreServiceQuery", coreServiceQuery);
		com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils.validateComponentInstance(components);

		try {
			aBagSPJavaOrchestration.put(LOG_MESSAGE, "CONSULTA DE SALDOS ");
			IProcedureResponse wProcedureResponse = executeStepsQueryBase(anOriginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			// TODO Auto-generated catch block
			return Utils.returnExceptionService(anOriginalRequest, e);
		} catch (CTSInfrastructureException e) {
			// TODO Auto-generated catch block
			return Utils.returnExceptionService(anOriginalRequest, e);
		}

		return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}

	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		return executeTransactionOrchestation(request, aBagSPJavaOrchestration);
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return (IProcedureResponse) arg1.get(RESPONSE_TRANSACTION);
	}

}
