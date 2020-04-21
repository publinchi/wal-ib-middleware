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
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.0"),
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
		String queryName = null;
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

		aExchangeRateRequest = (ExchangeRateRequest) Utils.setSessionParameters(aExchangeRateRequest,
				anOriginalRequest);

		if (responseServer.getOnLine()) {
			aExchangeRateRequest.setTarget(IMultiBackEndResolverService.TARGET_CENTRAL);
			aExchangeRateRequest.setTrnCode(1801008);
			aExchangeRateRequest.setSpName("cob_bvirtual..sp_tr42_tipo_cambio_div");
		} else {
			aExchangeRateRequest.setTarget(IMultiBackEndResolverService.TARGET_LOCAL);
			// aExchangeRateRequest.setTrnCode(1801009);
			aExchangeRateRequest.setTrnCode(1890010);
			aExchangeRateRequest.setSpName("cob_bvirtual..sp_tr42_tipo_cambio_div_bv");
		}

		try {
			if (logger.isInfoEnabled())
				logger.logInfo("******* Antes del llamado coreServiceForeignExchange executeQuery ************ ");
			aExchangeRateResponse = coreServiceForeignExchange.getExchangeRates(aExchangeRateRequest);
			if (logger.isInfoEnabled())
				logger.logInfo("******* Luego del llamado coreServiceForeignExchange executeQuery ************ ");

			if (!responseServer.getOnLine() && aExchangeRateResponse == null) {
				IProcedureResponse wIProcedureResponse = new ProcedureResponseAS();

				wIProcedureResponse.addParam("@o_cotizacion_com", ICTSTypes.SQLFLT8i, 0, "0");
				wIProcedureResponse.addParam("@o_cotizacion_ven", ICTSTypes.SQLFLT8i, 0, "0");

				wIProcedureResponse.setReturnCode(0);
				aBagSPJavaOrchestration.put(LOG_MESSAGE, "CONSULTA DE TASAS DE DIVISAS");
				aBagSPJavaOrchestration.put(QUERY_NAME, queryName);
				return wIProcedureResponse;
			}

		} catch (CTSServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		aBagSPJavaOrchestration.put(LOG_MESSAGE, "CONSULTA DE TASAS DE DIVISAS");
		aBagSPJavaOrchestration.put(QUERY_NAME, queryName);

		return transformProcedureResponse(aExchangeRateResponse);
	}

	private IProcedureResponse transformProcedureResponse(ExchangeRateResponse aExchangeRateResponse) {
		if (logger.isInfoEnabled())
			logger.logInfo("******* INICIANDO TRANSFORM PROCEDURE RESPONSE MMM ************ ");
		// buying =

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
