/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.executives;

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
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.ecobis.ib.application.dtos.ExecutivesRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ExecutivesResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Executives;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceExecutives;

@Component(name = "ExecutivesQuery", immediate = false)
@Service(value = { ICoreServiceExecutives.class })
@Properties(value = { @Property(name = "service.description", value = "ExecutivesQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ExecutivesQuery") })

public class ExecutivesQuery extends SPJavaOrchestrationBase implements ICoreServiceExecutives {

	private static final String COBIS_CONTEXT = "COBIS";

	/**
	 * Instance logger component
	 */
	private static ILogger logger = LogFactory.getLogger(ExecutivesQuery.class);

	private static final int COL_NAME = 0;
	private static final int COL_EMAIL = 1;

	private ExecutivesResponse transformToExecutivesResponse(IProcedureResponse aProcedureResponse) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("--->>>START--->>>transformToExecutivesResponse");
		}

		ExecutivesResponse executivesResp = new ExecutivesResponse();
		List<Executives> aExecutivesCollection = new ArrayList<Executives>();
		Executives aExecutives = null;
		if (logger.isInfoEnabled())
			logger.logInfo("GCO: Request: " + aProcedureResponse.getResultSetListSize());
		if (logger.isDebugEnabled()) {
			logger.logDebug("ProcedureResponse: " + aProcedureResponse.getProcedureResponseAsString());
		}

		IResultSetRow[] rowsExecutives = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

		for (IResultSetRow iResultSetRow : rowsExecutives) {
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
			aExecutives = new Executives();

			if (logger.isDebugEnabled()) {
				logger.logDebug("---->>>>>Size columns:" + columns.length);
			}
			if (logger.isDebugEnabled()) {
				logger.logDebug("---->>>>COL_NANE:" + columns[COL_NAME].getValue());
			}
			if (logger.isDebugEnabled()) {
				logger.logDebug("---->>>>COL_EMAIL:" + columns[COL_EMAIL].getValue());
			}

			aExecutives.setName(columns[COL_NAME].getValue());
			aExecutives.setEmail(columns[COL_EMAIL].getValue());
			aExecutivesCollection.add(aExecutives);
		}

		executivesResp.setExecutivesCollection(aExecutivesCollection);
		executivesResp.setReturnCode(0);
		executivesResp.setSuccess(true);
		if (logger.isDebugEnabled()) {
			logger.logDebug("---->>>>executivesResp:" + executivesResp);
		}
		return executivesResp;

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
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceExecutives#
	 * GetExecutives(com.cobiscorp.ecobis.ib.application.dtos.ExecutivesRequest)
	 */
	@Override
	public ExecutivesResponse GetExecutives(ExecutivesRequest wExecutivesRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: GetExecutives");
			logger.logInfo("RESPUESTA CORE COBIS GENERADA");
		}

		IProcedureRequest request = new ProcedureRequestAS();

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);

		request.setSpName("cobis..sp_consulta_oficiales_ente");
		request.addInputParam("@i_ente", ICTSTypes.SQLINT4, wExecutivesRequest.getClient().getId());

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request: " + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Start Request");
		}

		/* Ejecuta y obtiene la respuesta */
		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("--->>>>ResponseCOREBANKING: " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Finalize Response");
		}

		ExecutivesResponse wExecutivesResponse = transformToExecutivesResponse(pResponse);
		if (logger.isInfoEnabled()) {
			logger.logInfo("---->>>>TransformResponse--->>>>" + wExecutivesResponse);
		}
		return wExecutivesResponse;

	}
}
