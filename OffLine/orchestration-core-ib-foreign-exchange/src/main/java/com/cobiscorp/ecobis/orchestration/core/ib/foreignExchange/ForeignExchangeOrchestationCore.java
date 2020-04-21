package com.cobiscorp.ecobis.orchestration.core.ib.foreignExchange;

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
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.ExchangeRateRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ExchangeRateResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceForeignExchange;

@Component(name = "ForeignExchangeOrchestationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "ForeignExchangeOrchestationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ForeignExchangeOrchestationCore") })
public class ForeignExchangeOrchestationCore extends QueryBaseTemplate {

	@Reference(referenceInterface = ICoreServiceForeignExchange.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	private ICoreServiceForeignExchange coreServiceForeignExchange;

	private static ILogger logger = LogFactory.getLogger(ForeignExchangeOrchestationCore.class);

	protected void bindCoreService(ICoreServiceForeignExchange service) {
		coreServiceForeignExchange = service;
	}

	protected void unbindCoreService(ICoreServiceForeignExchange service) {
		coreServiceForeignExchange = null;
	}

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

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo("******* INICIANDO executeQuery ************ ");
		String messageError = null;
		String messageLog = null;
		String queryName = "getExchangeRates";

		ExchangeRateRequest aExchangeRateRequest = new ExchangeRateRequest();
		ExchangeRateResponse aExchangeRateResponse = new ExchangeRateResponse();
		IProcedureRequest anOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get("ORIGINAL_REQUEST");

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

		aExchangeRateRequest = transformExchangeRequest(anOriginalRequest);

		try {
			if (logger.isInfoEnabled())
				logger.logInfo("******* Antes del llamado coreServiceForeignExchange executeQuery ************ ");
			if (responseServer.getOnLine()
					|| (!responseServer.getOnLine() && responseServer.getOfflineWithBalances())) {
				aExchangeRateResponse = coreServiceForeignExchange.getExchangeRatesCore(aExchangeRateRequest);

			} else {
				IProcedureResponse wIProcedureResponse = getExchangeRatesLocal(aExchangeRateRequest);

				if (wIProcedureResponse == null) {
					wIProcedureResponse = new ProcedureResponseAS();

					wIProcedureResponse.addParam("@o_cotizacion_com", ICTSTypes.SQLFLT8i, 0, "0");
					wIProcedureResponse.addParam("@o_cotizacion_ven", ICTSTypes.SQLFLT8i, 0, "0");

					wIProcedureResponse.setReturnCode(0);
				}
				aBagSPJavaOrchestration.put(LOG_MESSAGE, "CONSULTA DE TASAS DE DIVISAS LOCAL");
				aBagSPJavaOrchestration.put(QUERY_NAME, queryName);
				return wIProcedureResponse;
			}
			if (logger.isInfoEnabled())
				logger.logInfo("******* Luego del llamado coreServiceForeignExchange executeQuery ************ ");

		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}

		aBagSPJavaOrchestration.put(LOG_MESSAGE, "CONSULTA DE TASAS DE DIVISAS");
		aBagSPJavaOrchestration.put(QUERY_NAME, queryName);

		return transformProcedureResponse(aExchangeRateResponse);

	}

	/**
	 * name transformExchangeRequest
	 * 
	 * @param anOriginalRequest
	 * @return ExchangeRateRequest
	 */
	private ExchangeRateRequest transformExchangeRequest(IProcedureRequest anOriginalRequest) {
		ExchangeRateRequest exchangeReq = new ExchangeRateRequest();

		exchangeReq = (ExchangeRateRequest) Utils.setSessionParameters(exchangeReq, anOriginalRequest);

		if (anOriginalRequest.readValueParam("@i_moneda1") != null)
			exchangeReq.setOrgCurrency(anOriginalRequest.readValueParam("@i_moneda1"));
		if (anOriginalRequest.readValueParam("@i_moneda2") != null)
			exchangeReq.setDestCurrency(anOriginalRequest.readValueParam("@i_moneda2"));

		return exchangeReq;

	}

	/**
	 * @param aExchangeRateResponse
	 * @return IProcedureResponse
	 */
	private IProcedureResponse transformProcedureResponse(ExchangeRateResponse aExchangeRateResponse) {
		if (logger.isInfoEnabled())
			logger.logInfo("******* INICIANDO TRANSFORM PROCEDURE RESPONSE MMM ************ ");

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		wProcedureResponse.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
		wProcedureResponse.setReturnCode(0);

		if (aExchangeRateResponse.getReturnCode() == 0) {
			if (String.valueOf(aExchangeRateResponse.getBuyingRate()) != null) {
				wProcedureResponse.addParam("@o_cotizacion_com", ICTSTypes.SQLFLT8i, 0,
						String.valueOf(aExchangeRateResponse.getBuyingRate()));
			}
			if (String.valueOf(aExchangeRateResponse.getSalesRate()) != null) {
				wProcedureResponse.addParam("@o_cotizacion_ven", ICTSTypes.SQLFLT8i, 0,
						String.valueOf(aExchangeRateResponse.getSalesRate()));
			}
		} else {
			wProcedureResponse = Utils.returnException(aExchangeRateResponse.getMessages());
		}
		return wProcedureResponse;
	}

	/**
	 * @param exchangeRateRequest
	 * @return
	 * @throws CTSServiceException
	 * @throws CTSInfrastructureException
	 */
	private IProcedureResponse getExchangeRatesLocal(ExchangeRateRequest exchangeRateRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo("Inicio ===========================>> getExchangeRatesLocal");

		IProcedureRequest aProcRequest = new ProcedureRequestAS();
		aProcRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1890010");

		aProcRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		aProcRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1890010");
		aProcRequest.setSpName("cob_bvirtual..sp_tr42_tipo_cambio_div");

		aProcRequest.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, exchangeRateRequest.getServer());
		aProcRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, String.valueOf(exchangeRateRequest.getOfficeCode()));
		aProcRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, exchangeRateRequest.getUser());
		aProcRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, exchangeRateRequest.getTerminal());
		aProcRequest.addInputParam("@s_org", ICTSTypes.SQLVARCHAR, exchangeRateRequest.getCulture());
		aProcRequest.addInputParam("@s_date", ICTSTypes.SQLDATETIME, exchangeRateRequest.getProcessDate());

		aProcRequest.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "T");
		aProcRequest.addInputParam("@i_moneda1", ICTSTypes.SQLINT2, "0");
		aProcRequest.addInputParam("@i_moneda2", ICTSTypes.SQLINT2, "0");
		aProcRequest.addOutputParam("@o_cotizacion_com", ICTSTypes.SQLFLT8i, "000.00");
		aProcRequest.addOutputParam("@o_cotizacion_ven", ICTSTypes.SQLFLT8i, "000.00");

		IProcedureResponse response = executeCoreBanking(aProcRequest);
		if (logger.isInfoEnabled())
			logger.logInfo("Fin ===========================>> getExchangeRatesLocal");
		return response;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo("******* INICIANDO EXECUTE JAVA ORCHESTRATION MMM ************ ");
		try {

			Map<String, Object> mapInterfaces = new HashMap<String, Object>();
			mapInterfaces.put("coreServiceForeignExchange", coreServiceForeignExchange);
			// Utils.validateComponentInstance(mapInterfaces);
			aBagSPJavaOrchestration.put(LOG_MESSAGE, "CONSULTA DE TASAS DE DIVISAS");
			executeStepsQueryBase(anOrginalRequest, aBagSPJavaOrchestration);
			return processResponse(anOrginalRequest, aBagSPJavaOrchestration);

		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {

			return Utils.returnExceptionService(anOrginalRequest, e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate#
	 * validateLocalExecution(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	protected IProcedureResponse validateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		// TODO Auto-generated method stub
		IProcedureResponse response = new ProcedureResponseAS();

		response.setReturnCode(0);

		return response;

	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		IProcedureResponse response = (IProcedureResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION");
		return response;
	}

	@Override
	protected IProcedureResponse updateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		// TODO Auto-generated method stub
		IProcedureResponse response = new ProcedureResponseAS();

		response.setReturnCode(0);

		return response;
	}

}
