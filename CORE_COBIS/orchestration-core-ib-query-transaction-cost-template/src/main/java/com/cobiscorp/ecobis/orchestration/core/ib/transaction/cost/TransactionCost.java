package com.cobiscorp.ecobis.orchestration.core.ib.transaction.cost;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.ExchangeRateResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TransactionCostRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TransactionCostResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceTransactionCost;

@Service(value = { ICoreServiceTransactionCost.class, ICISSPBaseOrchestration.class, IOrchestrator.class })
@Component(name = "TransactionCost", immediate = false)
@Properties(value = { @Property(name = "service.description", value = "TransactionCost"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TransactionCost") })
public class TransactionCost extends SPJavaOrchestrationBase implements ICoreServiceTransactionCost {

	private static ILogger logger = LogFactory.getLogger(TransactionCost.class);

	private TransactionCostResponse transformToTransactionCostResponse(IProcedureResponse aProcedureResponse) {
		TransactionCostResponse aTransactionCostResponse = new TransactionCostResponse();

		if (logger.isInfoEnabled())
			logger.logInfo("Ejecutando Transformacion de Respuesta Transaction Cost");

		if (aProcedureResponse.getResultSetListSize() > 0) {
			IResultSetRow[] rows = aProcedureResponse.getResultSet(aProcedureResponse.getResultSetListSize()).getData()
					.getRowsAsArray();
			IResultSetRowColumnData[] columns = rows[0].getColumnsAsArray();

			aTransactionCostResponse
					.setCost(columns[0].getValue() == null ? 0.00 : Double.parseDouble(columns[0].getValue()));
		} else if (aProcedureResponse.readValueParam("@o_costo") != null)
			aTransactionCostResponse.setCost(Double.parseDouble(aProcedureResponse.readValueParam("@o_costo")));

		aTransactionCostResponse.setReturnCode(aProcedureResponse.getReturnCode());
		aTransactionCostResponse.setSuccess(aProcedureResponse.getReturnCode() == 0 ? true : false);
		if (aTransactionCostResponse.getReturnCode() != 0 && aTransactionCostResponse.getReturnCode() != 40002)
			aTransactionCostResponse.setMessages(Utils.returnArrayMessage(aProcedureResponse));

		if (logger.isInfoEnabled())
			logger.logInfo("Respuesta Devuelta: " + aTransactionCostResponse);
		return aTransactionCostResponse;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceTransactionCost#getTransactionCost(com.cobiscorp.ecobis.ib.
	 * application.dtos.TransactionCostRequest)
	 */
	@Override
	public TransactionCostResponse getTransactionCost(TransactionCostRequest aCostRequest)
		throws CTSServiceException, CTSInfrastructureException {
		IProcedureRequest anOriginalRequest = initProcedureRequest(aCostRequest.getOriginalRequest());

		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800125");
		anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");

		anOriginalRequest.setSpName("cobis..sp_bv_calc_cost_terc");
		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1800125");
		anOriginalRequest.addInputParam("@i_cuenta", ICTSTypes.SQLVARCHAR,
				aCostRequest.getAccount().getProductNumber());
		anOriginalRequest.addInputParam("@i_producto", ICTSTypes.SQLINT4,
				String.valueOf(aCostRequest.getAccount().getProductType()));
		if (aCostRequest.getTrnId() != 0)
			anOriginalRequest.addInputParam("@i_transaccion", ICTSTypes.SQLINT4,
					String.valueOf(aCostRequest.getTrnId()));
		if (aCostRequest.getServiceId() != null)
			anOriginalRequest.addInputParam("@i_servicio", ICTSTypes.SQLVARCHAR, aCostRequest.getServiceId());
		if (aCostRequest.getEntryId() != null)
			anOriginalRequest.addInputParam("@i_rubro", ICTSTypes.SQLVARCHAR, aCostRequest.getEntryId());
		if (aCostRequest.getOperation() != null)
			anOriginalRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, aCostRequest.getOperation());
		if (aCostRequest.getClient() != null)
			anOriginalRequest.addInputParam("@i_cliente", ICTSTypes.SQLINT4,
					String.valueOf(aCostRequest.getClient().getId()));
		if (aCostRequest.getAccount().getCurrency() != null)
			anOriginalRequest.addInputParam("@i_moneda", ICTSTypes.SQLINT1,
					String.valueOf(aCostRequest.getAccount().getCurrency().getCurrencyId()));

		anOriginalRequest.addOutputParam("@o_costo", ICTSTypes.SQLMONEY, "0.00");

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);
		return transformToTransactionCostResponse(response);
	}

}
