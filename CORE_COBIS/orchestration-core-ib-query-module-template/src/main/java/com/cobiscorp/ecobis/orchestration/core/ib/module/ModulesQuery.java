/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.module;

import java.util.ArrayList;
//import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
//import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IProvider;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.ModuleCriteriaRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ModuleCriteriaResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ModuleRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ModuleResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Module;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.IWSModules;

@Component(name = "ModulesQuery", immediate = false)
@Service(value = { IWSModules.class })
@Properties(value = { @Property(name = "service.description", value = "ModulesQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ModulesQuery") })
public class ModulesQuery extends SPJavaOrchestrationBase implements IWSModules {
	private static ILogger logger = LogFactory.getLogger(ModulesQuery.class);
	private java.util.Properties properties;
	private static final int COL_COD_MODULE = 0;
	private static final int COL_DESCRIPTION = 1;
	private static final int COL_TYPE = 2;
	static final String ORIGINAL_RESPONSE = "ORIGINAL_RESPONSE";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.IWSModules#getModule
	 * (com.cobiscorp.ecobis.ib.application.dtos.ModuleRequest)
	 * ******************
	 * ********************************************************
	 * *******************************************************************
	 */
	@Override
	public ModuleResponse getModule(ModuleRequest aModuleRequest, Map<String, Object> aBagSPJavaOrchestration,
			java.util.Properties properties) throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getModule");
		}
		this.properties = properties;
		IProcedureResponse pResponse = Execution(aModuleRequest, aBagSPJavaOrchestration);
		ModuleResponse moduleResponse = transformToStockResponse(pResponse);
		return moduleResponse;
	}
    
	private IProcedureResponse Execution(ModuleRequest aModuleRequest, Map<String, Object> aBagSPJavaOrchestration)
			throws CTSServiceException, CTSInfrastructureException {
		;
		IProcedureRequest request = initProcedureRequest(aModuleRequest.getOriginalRequest());
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "688");
		request.addInputParam("@i_id_operativo", ICTSTypes.SQLVARCHAR, aModuleRequest.getIdOperation());
		request.addOutputParam("@o_coderror", ICTSTypes.SYBINT4, "0");
		request.addOutputParam("@o_mensaje", ICTSTypes.SQLVARCHAR, "            ");
		request.addInputParam("@i_operacion_connector", ICTSTypes.SYBINT4, "688");
		request.removeFieldInHeader("trn_virtual");
		request.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "688");
		request.addFieldInHeader(ICOBISTS.HEADER_TIMEOUT, ICOBISTS.HEADER_STRING_TYPE,
				((String) this.properties.get("HEADER_TIMEOUT")));
		request.addFieldInHeader(IProvider.HEADER_CATALOG_PROVIDER, ICOBISTS.HEADER_STRING_TYPE,
				((String) this.properties.get("HEADER_CATALOG_PROVIDER")));
		aBagSPJavaOrchestration.put(ICISSPBaseOrchestration.CONNECTOR_TYPE,
				((String) this.properties.get("CONNECTOR_TYPE")));
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Request:*** " + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Start Request *** ");
		}

		IProcedureResponse pResponse = executeProvider(request, aBagSPJavaOrchestration);

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response: *** " + pResponse.getProcedureResponseAsString());
		}

		aBagSPJavaOrchestration.put(ORIGINAL_RESPONSE, pResponse);
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response *** ");
		}
		return pResponse;
	}
    
	private ModuleResponse transformToStockResponse(IProcedureResponse aProcedureResponse) {
		ModuleResponse ModuleResp = new ModuleResponse();
		List<Module> moduleCollection = new ArrayList<Module>();
		Module aModule = null;
		int error = 0;

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + aProcedureResponse.getProcedureResponseAsString());
		}

		error = Integer.parseInt(aProcedureResponse.readValueParam("@o_coderror"));

		if (aProcedureResponse.getReturnCode() == 0 && error == 0) {

			IResultSetRow[] rowsStock = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

			for (IResultSetRow iResultSetRow : rowsStock) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				aModule = new Module();
				aModule.setCodModule(Integer.parseInt(columns[COL_COD_MODULE].getValue()));
				aModule.setDescription(columns[COL_DESCRIPTION].getValue());
				aModule.setType(columns[COL_TYPE].getValue());
				moduleCollection.add(aModule);
			}
			ModuleResp.setModuleCollection(moduleCollection);
		} else {

			ModuleResp.setCodError(error);
			ModuleResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		}
		ModuleResp.setReturnCode(aProcedureResponse.getReturnCode());

		return ModuleResp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.commons.components.ICOBISComponent#loadConfiguration
	 * (com.cobiscorp.cobis.commons.configuration.IConfigurationReader)
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase
	 * #executeJavaOrchestration
	 * (com.cobiscorp.cobis.cts.domains.IProcedureRequest, java.util.Map)
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase
	 * #processResponse(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		return null;
	}
}
