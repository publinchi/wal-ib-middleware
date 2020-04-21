package com.cobiscorp.ecobis.orchestration.core.admin;

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
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.CurrencyRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CurrencyResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceGetCurrencies;
import com.cobiscorp.ecobis.orchestration.core.accounts.MovementsAccountsQueryOrchestrationCore;

@Component(name = "GetCurrenciesQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "GetCurrenciesQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "GetCurrenciesQueryOrchestrationCore") })

public class GetCurrenciesQueryOrchestrationCore extends SPJavaOrchestrationBase {
	private static ILogger logger = LogFactory.getLogger(MovementsAccountsQueryOrchestrationCore.class);
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	private static final String CLASS_NAME = "--->";
	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceGetCurrencies.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	private ICoreServiceGetCurrencies coreService;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreServiceGetCurrencies service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreServiceGetCurrencies service) {
		coreService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled())
			logger.logInfo("INICIO ===================================>> executeJavaOrchestration - Orquestacion");

		if (anOriginalRequest != null)
			aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		Utils.validateComponentInstance(mapInterfaces);

		Map<String, Object> wprocedureResponse1 = procedureResponse(anOriginalRequest, aBagSPJavaOrchestration);
		Boolean wSuccessExecutionOperation1 = (Boolean) wprocedureResponse1.get("SuccessExecutionOperation");
		IProcedureResponse wIProcedureResponse1 = (IProcedureResponse) wprocedureResponse1.get("IProcedureResponse");
		IProcedureResponse wErrorProcedureResponse = (IProcedureResponse) wprocedureResponse1
				.get("ErrorProcedureResponse");

		if (wErrorProcedureResponse != null) {
			return wErrorProcedureResponse;
		}
		if (!wSuccessExecutionOperation1) {
			return wIProcedureResponse1;
		}
		wIProcedureResponse1 = (IProcedureResponse) aBagSPJavaOrchestration.get("GET_CURRENCIES_RESPONSE");
		if (logger.isDebugEnabled())
			logger.logInfo("FIN ===================================>> executeJavaOrchestration - Orquestacion");
		return wIProcedureResponse1;
	}

	private Map<String, Object> procedureResponse(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled()) {
			logger.logInfo(CLASS_NAME + "getMasks");
			logger.logInfo("INICIO ===================================>> procedureResponse - Orquestacion");
		}
		HashMap<String, Object> ret = new HashMap<String, Object>();
		ret.put("IProcedureResponse", null);
		ret.put("SuccessExecutionOperation", null);
		ret.put("ErrorProcedureResponse", null);

		boolean wSuccessExecutionOperation1 = executeGetCurrencies(anOriginalRequest, aBagSPJavaOrchestration);
		ret.put("SuccessExecutionOperation", wSuccessExecutionOperation1);

		if (logger.isDebugEnabled())
			logger.logDebug("result exeuction operation 1: " + wSuccessExecutionOperation1);

		IProcedureResponse wProcedureResponseOperation1 = initProcedureResponse(anOriginalRequest);
		wProcedureResponseOperation1.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE,
				ICSP.ERROR_EXECUTION_SERVICE);
		ret.put("IProcedureResponse", wProcedureResponseOperation1);
		if (logger.isDebugEnabled())
			logger.logInfo("FIN ===================================>> procedureResponse - Orquestacion");
		return ret;
	}

	private boolean executeGetCurrencies(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled())
			logger.logInfo("INICIO ===================================>> executeGetCurrencies - Orquestacion");
		IProcedureResponse wProcedureResponse = initProcedureResponse(anOriginalRequest);
		CurrencyResponse aCurrencyResponse = new CurrencyResponse();

		try {
			CurrencyRequest aCurrencyRequest = transformCurrencyRequest(aBagSPJavaOrchestration);
			aCurrencyResponse = coreService.GetCurrencies(aCurrencyRequest);
			wProcedureResponse = transformProcedureResponse(aCurrencyResponse, aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put("GET_CURRENCIES_RESPONSE", wProcedureResponse);
			if (logger.isDebugEnabled())
				logger.logInfo("FIN ===================================>> executeGetCurrencies - Orquestacion");
			return !wProcedureResponse.hasError();
		} catch (CTSServiceException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSServiceException:" + e.getMessage());
			e.printStackTrace();
			aBagSPJavaOrchestration.put("GET_CURRENCIES_RESPONSE", null);
			return false;
		} catch (CTSInfrastructureException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSInfrastructureException:" + e.getMessage());
			aBagSPJavaOrchestration.put("GET_CURRENCIES_RESPONSE", null);
			return false;
		}
	}

	private IProcedureResponse transformProcedureResponse(CurrencyResponse aCurrencyResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled())
			logger.logInfo(
					"INICIO ===================================>> transformProcedureResponse GetCurrencies - Orquestacion");
		IProcedureResponse wProcedureResponse = initProcedureResponse(
				(IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		// Agregar Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 3));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("description", ICTSTypes.SQLVARCHAR, 64));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("symbol", ICTSTypes.SQLVARCHAR, 64));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("nemonic", ICTSTypes.SQLINT4, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("countryCode", ICTSTypes.SQLINT4, 3));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("country", ICTSTypes.SQLVARCHAR, 64));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("state", ICTSTypes.SQLVARCHAR, 64));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("hasDecimal", ICTSTypes.SQLVARCHAR, 10));

		for (Currency aCurrency : aCurrencyResponse.getCurrencyCollection()) {
			IResultSetRow row = new ResultSetRow();

			if (aCurrency.getCurrencyId() != null) {
				row.addRowData(1, new ResultSetRowColumnData(false, aCurrency.getCurrencyId().toString()));
			} else {
				row.addRowData(1, new ResultSetRowColumnData(false, " "));
			}
			if (aCurrency.getCurrencyDescription() != null) {
				row.addRowData(2, new ResultSetRowColumnData(false, aCurrency.getCurrencyDescription()));
			} else {
				row.addRowData(2, new ResultSetRowColumnData(false, " "));
			}
			if (aCurrency.getCurrencySymbol() != null) {
				row.addRowData(3, new ResultSetRowColumnData(false, aCurrency.getCurrencySymbol()));
			} else {
				row.addRowData(3, new ResultSetRowColumnData(false, " "));
			}
			if (aCurrency.getCurrencyNemonic() != null) {
				row.addRowData(4, new ResultSetRowColumnData(false, aCurrency.getCurrencyNemonic()));
			} else {
				row.addRowData(4, new ResultSetRowColumnData(false, " "));
			}
			if (aCurrency.getCountryCode() != null) {
				row.addRowData(5, new ResultSetRowColumnData(false, aCurrency.getCountryCode().toString()));
			} else {
				row.addRowData(5, new ResultSetRowColumnData(false, " "));
			}
			if (aCurrency.getCountry() != null) {
				row.addRowData(6, new ResultSetRowColumnData(false, aCurrency.getCountry()));
			} else {
				row.addRowData(6, new ResultSetRowColumnData(false, " "));
			}
			if (aCurrency.getState() != null) {
				row.addRowData(7, new ResultSetRowColumnData(false, aCurrency.getState()));
			} else {
				row.addRowData(7, new ResultSetRowColumnData(false, " "));
			}
			if (aCurrency.getHasDecimal() != null) {
				row.addRowData(8, new ResultSetRowColumnData(false, aCurrency.getHasDecimal()));
			} else {
				row.addRowData(8, new ResultSetRowColumnData(false, " "));
			}

			data.addRow(row);
		}

		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
		wProcedureResponse.addResponseBlock(resultBlock);
		if (logger.isDebugEnabled()) {
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());
			logger.logInfo(
					"FIN ===================================>> transformProcedureResponse GetCurrencies - Orquestacion");
		}
		return wProcedureResponse;
	}

	private CurrencyRequest transformCurrencyRequest(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled())
			logger.logInfo("INICIO ===================================>> transformCurrencyRequest - Orquestacion");
		CurrencyRequest aCurrencyRequest = new CurrencyRequest();
		IProcedureRequest aRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;

		messageError = aRequest.readValueParam("@i_modo") == null ? " - @i_modo can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		aCurrencyRequest.setMode(Integer.parseInt(aRequest.readValueParam("@i_modo")));
		if (logger.isDebugEnabled())
			logger.logInfo("FIN ===================================>> transformCurrencyRequest - Orquestacion");

		return aCurrencyRequest;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
