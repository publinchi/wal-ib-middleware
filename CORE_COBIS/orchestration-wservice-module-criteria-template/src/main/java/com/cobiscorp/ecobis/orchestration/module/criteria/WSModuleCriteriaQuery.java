package com.cobiscorp.ecobis.orchestration.module.criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
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
import com.cobiscorp.ecobis.ib.orchestration.interfaces.IWSCriteriaModules;
import com.cobiscorp.ecobis.ib.application.dtos.ModuleCriteriaRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ModuleCriteriaResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Criteria;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Label;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ModuleCriteria;

@Component(name = "WSModuleCriteriaQuery", immediate = false)
@Service(value = { IWSCriteriaModules.class })
@Properties(value = { @Property(name = "service.description", value = "WSModuleCriteriaQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "WSModuleCriteriaQuery") })

public class WSModuleCriteriaQuery extends SPJavaOrchestrationBase implements IWSCriteriaModules {
	private static ILogger logger = LogFactory.getLogger(WSModuleCriteriaQuery.class);
	private String codeError;
	private java.util.Properties properties;
	static final String ORIGINAL_RESPONSE = "ORIGINAL_RESPONSE";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.IWSCriteriaModules#
	 * getModuleCriteria(com.cobiscorp.ecobis.ib.application.dtos.
	 * ModuleCriteriaRequest)
	 ***********************************************************************************************************************************************/
	@Override
	public ModuleCriteriaResponse getModuleCriteria(ModuleCriteriaRequest aModuleCriteriaRequest,
			Map<String, Object> aBag, java.util.Properties inProperties)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getModuleCriteria");
		}

		properties = inProperties;
		IProcedureResponse pResponse = Execution(aModuleCriteriaRequest, aBag);
		ModuleCriteriaResponse mcResponse = transformToMCResponse(pResponse);
		return mcResponse;
	}

	private IProcedureResponse Execution(ModuleCriteriaRequest aModuleCriteriaReq,
			Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		;

		IProcedureResponse wGetModuleCriteriaResp;
		IProcedureRequest wGetModuleCriteriaTMP = initProcedureRequest(aModuleCriteriaReq.getOriginalRequest());
		wGetModuleCriteriaTMP.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "689");

		wGetModuleCriteriaTMP.addInputParam("@i_operacion_connector", ICTSTypes.SYBINT4, "689");
		wGetModuleCriteriaTMP.addInputParam("@i_id_operativo", ICTSTypes.SYBVARCHAR,
				aModuleCriteriaReq.getOperativeId());
		wGetModuleCriteriaTMP.addInputParam("@i_codmodulo", ICTSTypes.SYBINT4,
				aModuleCriteriaReq.getModuleCode().toString());
		wGetModuleCriteriaTMP.addOutputParam("@o_coderror", ICTSTypes.SYBINT4, "0");
		wGetModuleCriteriaTMP.addOutputParam("@o_mensaje", ICTSTypes.SQLVARCHAR, "            ");

		wGetModuleCriteriaTMP.addFieldInHeader(ICOBISTS.HEADER_TIMEOUT, ICOBISTS.HEADER_STRING_TYPE,
				((String) properties.get("HEADER_TIMEOUT")));
		wGetModuleCriteriaTMP.addFieldInHeader(IProvider.HEADER_CATALOG_PROVIDER, ICOBISTS.HEADER_STRING_TYPE,
				((String) properties.get("HEADER_CATALOG_PROVIDER")));
		codeError = ((String) this.properties.get("CODE_ERROR_SESSION"));

		aBagSPJavaOrchestration.put(ICISSPBaseOrchestration.CONNECTOR_TYPE,
				((String) properties.get("CONNECTOR_TYPE")));
		wGetModuleCriteriaResp = executeProvider(wGetModuleCriteriaTMP, aBagSPJavaOrchestration);
		aBagSPJavaOrchestration.put("wObtenerCriteriosParaModuloResp", wGetModuleCriteriaResp);

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response: *** " + wGetModuleCriteriaResp.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response *** ");
		}
		aBagSPJavaOrchestration.put(ORIGINAL_RESPONSE, wGetModuleCriteriaResp);

		return wGetModuleCriteriaResp;
	}

	private ModuleCriteriaResponse transformToMCResponse(IProcedureResponse aProcedureResponse) {
		ModuleCriteriaResponse MCriteriaResp = new ModuleCriteriaResponse();
		List<ModuleCriteria> mCriteriaCollection = new ArrayList<ModuleCriteria>();
		List<ModuleCriteria> mLabelCollection = new ArrayList<ModuleCriteria>();
		ModuleCriteria aModuleCriteria = null;
		ModuleCriteria aModuleLabel = null;
		Criteria aCriteria = null;
		Label aLabel = null;
		boolean errorWS = false;

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + aProcedureResponse.getProcedureResponseAsString());
		}

		for (String codigos : codeError.split(",")) {
			if (logger.isInfoEnabled())
				logger.logInfo("Codigos de Error ==> " + codigos);
			if (aProcedureResponse.readValueParam("@o_coderror").equals(codigos)) {
				errorWS = true;
			}
		}

		if (errorWS) {
			MCriteriaResp.setReturnCode(aProcedureResponse.getReturnCode());
			MCriteriaResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		} else {

			if (aProcedureResponse.getReturnCode() == 0) {
				IResultSetRow[] rowsMCriteria = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();
				IResultSetRow[] rowsMCriteria2 = aProcedureResponse.getResultSet(2).getData().getRowsAsArray();
				//

				for (IResultSetRow iResultSetRow : rowsMCriteria) {
					aModuleCriteria = new ModuleCriteria();
					aCriteria = new Criteria();
					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
					if (logger.isDebugEnabled()) {
						logger.logDebug("aCriteria 0 " + columns[0].getValue());
						logger.logDebug("aCriteria 1 " + columns[1].getValue());
					}
					aCriteria.setCriteriaCode(columns[0].getValue());
					aCriteria.setDescription(columns[1].getValue());

					aModuleCriteria.setCriteria(aCriteria);
					mCriteriaCollection.add(aModuleCriteria);

				}
				for (IResultSetRow iResultSetRow2 : rowsMCriteria2) {
					aModuleLabel = new ModuleCriteria();

					IResultSetRowColumnData[] columns2 = iResultSetRow2.getColumnsAsArray();
					if (logger.isDebugEnabled()) {
						logger.logDebug("aLabel 0 " + columns2[0].getValue());
						logger.logDebug("aLabel 1 " + columns2[1].getValue());
						logger.logDebug("aLabel 2 " + columns2[2].getValue());
					}
					aLabel = new Label();
					aLabel.setCodeCriteria(columns2[0].getValue());
					aLabel.setLabel(columns2[1].getValue());
					aLabel.setCriteriaType(columns2[2].getValue());
					// aCriteria.setLabel(aLabel);

					aModuleLabel.setLabel(aLabel);
					mLabelCollection.add(aModuleLabel);

				}
				aModuleCriteria.setErrorCode(0);
				MCriteriaResp.setmCriteriaCollection(mCriteriaCollection);
				MCriteriaResp.setmLabelCollection(mLabelCollection);
			} else {

				MCriteriaResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));
			}

			MCriteriaResp.setReturnCode(aProcedureResponse.getReturnCode());
		}
		if (logger.isDebugEnabled())
			logger.logDebug("*** MCriteriaResp: ***" + MCriteriaResp);
		return MCriteriaResp;
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
		return null;
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

}
