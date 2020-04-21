package com.cobiscorp.ecobis.orchestration.core.ib.stocks;

import java.util.ArrayList;
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
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceStock;
import com.cobiscorp.ecobis.ib.application.dtos.StockRequest;
import com.cobiscorp.ecobis.ib.application.dtos.StockResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Stock;

@Component(name = "StocksQuery", immediate = false)
@Service(value = { ICoreServiceStock.class })
@Properties(value = { @Property(name = "service.description", value = "StocksQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "StocksQuery") })

public class StocksQuery extends SPJavaOrchestrationBase implements ICoreServiceStock {
	private static ILogger logger = LogFactory.getLogger(StocksQuery.class);
	private static final String SP_NAME = "cobis..sp_consulta_stock";

	/**** Output: getStocks *****/
	private static final int COL_SEQUENCY = 0;
	private static final int COL_COD_REGION = 1;
	private static final int COL_REGION = 2;
	private static final int COL_COD_OFFICE = 3;
	private static final int COL_OFFICE = 4;
	private static final int COL_NO_ASSIGNED = 5;
	private static final int COL_ASSIGNED = 6;
	
	@Override
	public StockResponse getStock(StockRequest aStockRequest) throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getStocks");
		}

		IProcedureResponse pResponse = Execution(SP_NAME, aStockRequest);
		StockResponse stockResponse = transformToStockResponse(pResponse);
		return stockResponse;
	}

	
	@Override
	public StockResponse getStockbyDate(StockRequest aStockRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getStocksbyDate");
		}

		IProcedureResponse pResponse = ExecutionbyDate(SP_NAME, aStockRequest);
		StockResponse stockResponse = transformToStockbyDateResponse(pResponse);
		return stockResponse;
	}
    
	private IProcedureResponse Execution(String SpName, StockRequest aStockRequest)
			throws CTSServiceException, CTSInfrastructureException {
		;
		IProcedureRequest request = initProcedureRequest(aStockRequest.getOriginalRequest());		
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800171");

		request.setSpName(SpName);

		request.addInputParam("@i_operacion", ICTSTypes.SYBCHAR, "S");		
		if (aStockRequest.getRegion() != null) {			
			request.addInputParam("@i_region", ICTSTypes.SYBVARCHAR, aStockRequest.getRegion().getId());

		}
		if (aStockRequest.getOffice() != null)
			request.addInputParam("@i_oficina", ICTSTypes.SYBINT2, aStockRequest.getOffice().getId().toString());

		if (aStockRequest.getSequential() != null)
			request.addInputParam("@i_secuencial", ICTSTypes.SYBINT4, aStockRequest.getSequential().toString());
		request.addOutputParam("@o_num_filas", ICTSTypes.SYBINT2, "0");
		request.addOutputParam("@o_stock", ICTSTypes.SYBINT2, "0");

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
    
	private StockResponse transformToStockResponse(IProcedureResponse aProcedureResponse) {
		StockResponse StockResp = new StockResponse();
		List<Stock> stockCollection = new ArrayList<Stock>();
		Stock aStock = null;

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + aProcedureResponse.getProcedureResponseAsString());
		}

		if (aProcedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsStock = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

			for (IResultSetRow iResultSetRow : rowsStock) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				StockResp.setRows(Integer.parseInt(columns[0].getValue()));
			}

			IResultSetRow[] rowsStock2 = aProcedureResponse.getResultSet(2).getData().getRowsAsArray();

			for (IResultSetRow iResultSetRow2 : rowsStock2) {
				IResultSetRowColumnData[] columns2 = iResultSetRow2.getColumnsAsArray();
				aStock = new Stock();
				aStock.setSequential(Integer.parseInt(columns2[COL_SEQUENCY].getValue()));
				aStock.setCod_region(columns2[COL_COD_REGION].getValue());
				aStock.setRegion(columns2[COL_REGION].getValue());
				aStock.setCod_office(Integer.parseInt(columns2[COL_COD_OFFICE].getValue()));
				aStock.setOffice(columns2[COL_OFFICE].getValue());
				aStock.setNo_assigned(Integer.parseInt(columns2[COL_NO_ASSIGNED].getValue()));
				aStock.setAssigned(Integer.parseInt(columns2[COL_ASSIGNED].getValue()));

				stockCollection.add(aStock);
			}
			StockResp.setTotalStock(Integer.parseInt(aProcedureResponse.readValueParam("@o_stock")));

			StockResp.setStockCollection(stockCollection);
		} else {
			StockResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		}
		StockResp.setReturnCode(aProcedureResponse.getReturnCode());
		return StockResp;
	}
	
	private IProcedureResponse ExecutionbyDate(String SpName, StockRequest aStockRequest)
			throws CTSServiceException, CTSInfrastructureException {
		;
		IProcedureRequest request = initProcedureRequest(aStockRequest.getOriginalRequest());	
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800172");

		request.setSpName(SpName);

		request.addInputParam("@i_operacion", ICTSTypes.SYBCHAR, "Q");		
		request.addInputParam("@i_fecha", ICTSTypes.SQLDATETIME, aStockRequest.getDate());
		if (aStockRequest.getSequential() != null)
			request.addInputParam("@i_secuencial", ICTSTypes.SYBINT4, aStockRequest.getSequential().toString());
		request.addOutputParam("@o_num_filas", ICTSTypes.SYBINT2, "0");

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

	private StockResponse transformToStockbyDateResponse(IProcedureResponse aProcedureResponse) {
		StockResponse StockResp = new StockResponse();
		List<Stock> stockCollection = new ArrayList<Stock>();
		Stock aStock = null;

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + aProcedureResponse.getProcedureResponseAsString());
		}

		if (aProcedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsStock = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

			for (IResultSetRow iResultSetRow : rowsStock) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

				StockResp.setRows(Integer.parseInt(columns[0].getValue()));
			}
			IResultSetRow[] rowsStock2 = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();
			for (IResultSetRow iResultSetRow : rowsStock2) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				aStock = new Stock();
				aStock.setSequential(Integer.parseInt(columns[0].getValue()));
				aStock.setCod_region(columns[1].getValue());
				aStock.setRegion(columns[2].getValue());
				aStock.setCod_office(Integer.parseInt(columns[3].getValue()));
				aStock.setOffice(columns[4].getValue());
				aStock.setStock(Integer.parseInt(columns[5].getValue()));
				aStock.setFecha(columns[6].getValue());
				aStock.setTipo(columns[7].getValue());
			}

			StockResp.setStockCollection(stockCollection);
		} else {

			StockResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		}
		StockResp.setReturnCode(aProcedureResponse.getReturnCode());
		return StockResp;
	}

	
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		return null;
	}
	
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		return null;
	}
}
