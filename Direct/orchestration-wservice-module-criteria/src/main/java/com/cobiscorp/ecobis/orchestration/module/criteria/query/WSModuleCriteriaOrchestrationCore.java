package com.cobiscorp.ecobis.orchestration.module.criteria.query;

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
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.ModuleCriteriaRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ModuleCriteriaResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ModuleCriteria;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.IWSCriteriaModules;
import com.cobiscorp.ecobis.orchestration.ws.base.SintesisBaseTemplate;

/**
 * Get criteria modules for service payments
 * 
 * @since Jun 30, 2015
 * @author gyagual
 * @version 1.0.0
 * 
 */
@Component(name = "WSModuleCriteriaOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "WSModuleCriteriaOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "WSModuleCriteriaOrchestrationCore") })
public class WSModuleCriteriaOrchestrationCore extends SintesisBaseTemplate {

	private static ILogger logger = LogFactory.getLogger(WSModuleCriteriaOrchestrationCore.class);
	private static final String CLASS_NAME = "WSModuleCriteriaOrchestrationCore--->";
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	static final String ORIGINAL_RESPONSE = "ORIGINAL_RESPONSE";
	static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	static final String RESPONSE_LOCAL_UPDATE = "RESPONSE_LOCAL_UPDATE";
	static final String RESPONSE_PROVIDER = "RESPONSE_PROVIDER";
	public static final String NUMERO = "numero";
	public static final String TEXTO = "texto";
	public static String DATATYPE;

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = IWSCriteriaModules.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreModuleCriteria", unbind = "unbindCoreModuleCriteria")
	protected IWSCriteriaModules coreModuleCriteria;

	/**
	 * Instance Service Interface
	 * 
	 * @param service
	 */
	public void bindCoreModuleCriteria(IWSCriteriaModules service) {
		coreModuleCriteria = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	public void unbindCoreModuleCriteria(IWSCriteriaModules service) {
		coreModuleCriteria = null;
	}

	public void loadConfiguration(IConfigurationReader arg0) {
		super.loadConfiguration(arg0);
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeJavaOrchestration");

		try {
			Map<String, Object> mapInterfaces = new HashMap<String, Object>();
			mapInterfaces.put("coreModuleCriteria", coreModuleCriteria);

			Utils.validateComponentInstance(mapInterfaces);

			if (anOriginalRequest == null)
				if (logger.isInfoEnabled())
					logger.logInfo(CLASS_NAME + "Original Request ISNULL");

			aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);
			executeSteps(aBagSPJavaOrchestration);
			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);

		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.logInfo("*********  Error en " + e.getMessage(), e);
			}

			IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
			ErrorBlock eb = new ErrorBlock(-1, "Service is not available");
			wProcedureRespFinal.addResponseBlock(eb);
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
			wProcedureRespFinal.setReturnCode(-1);
			return wProcedureRespFinal;
		}
	}

	@Override
	public IProcedureResponse executeWSMethod(Map<String, Object> aBag) {

		IProcedureRequest anOriginalRequest = (IProcedureRequest) aBag.get(ORIGINAL_REQUEST);
		IProcedureResponse responseProc = null;
		try {
			logger.logInfo("Realiza consulta");
			responseProc = queryProviderTransaction(aBag);

			aBag.put(RESPONSE_TRANSACTION, responseProc);

			return responseProc;
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.logInfo("*********  Error en " + e.getMessage(), e);
			}

			IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
			ErrorBlock eb = new ErrorBlock(-1, "Service is not available");
			wProcedureRespFinal.addResponseBlock(eb);
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
			wProcedureRespFinal.setReturnCode(-1);
			return wProcedureRespFinal;
		}

	}

	/**
	 * name executeProviderTransaction
	 * 
	 * @param anOriginalRequest
	 * @param aBag
	 * @return IProcedureResponse
	 */
	private IProcedureResponse queryProviderTransaction(Map<String, Object> aBag) {

		IProcedureRequest anOriginalRequest = (IProcedureRequest) aBag.get(ORIGINAL_REQUEST);
		ModuleCriteriaResponse wMCriteriaResp = new ModuleCriteriaResponse();

		ModuleCriteriaRequest wMCriteriaReq = transformMCriteriaRequest(anOriginalRequest.clone());

		try {
			wMCriteriaReq.setOriginalRequest(anOriginalRequest);
			wMCriteriaResp = coreModuleCriteria.getModuleCriteria(wMCriteriaReq, aBag, this.properties);
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}
		logger.logDebug("antes del transform responsewMCriteriaResp" + wMCriteriaResp);
		return transformMCriteriaResponse(wMCriteriaResp, aBag);
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
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> aBagSPJavaOrchestration) {

		return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
	}

	private ModuleCriteriaRequest transformMCriteriaRequest(IProcedureRequest aRequest) {
		ModuleCriteriaRequest wMCriteriaRequest = new ModuleCriteriaRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		if (!(aRequest.readValueParam("@i_convenio") == null))
			wMCriteriaRequest.setModuleCode(Integer.parseInt(aRequest.readValueParam("@i_convenio")));

		if (!(aRequest.readValueParam("@i_id_operativo") == null))
			wMCriteriaRequest.setOperativeId(aRequest.readValueParam("@i_id_operativo"));

		return wMCriteriaRequest;
	}

	private IProcedureResponse transformMCriteriaResponse(ModuleCriteriaResponse aModuleCriteriaResponse,
			Map<String, Object> aBagSPJavaOrchestration) {

		String dataType, paramData;

		IProcedureResponse sintesisOriginalResponse = (IProcedureResponse) aBagSPJavaOrchestration
				.get(ORIGINAL_RESPONSE);

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response" + aModuleCriteriaResponse);

		if (aModuleCriteriaResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aModuleCriteriaResponse.getMessages()));
		} else {

			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("referenceField", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("referenceLabel", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("referenceType", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("referenceEnabled", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("referenceMandatory", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("referenceDatatype", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("referenceLength", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("referenceDefault", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("referenceCatalog", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("referenceRedigitar", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("referenceVisible", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("referenceOrder", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("referenceColumn", ICTSTypes.SYBVARCHAR, 64));

			int contador = 0;

			if (sintesisOriginalResponse.readValueParam("@o_coderror").equals("0")) {
				for (ModuleCriteria aModuleCriteria : aModuleCriteriaResponse.getmLabelCollection()) {

					IResultSetRow row = new ResultSetRow();

					row.addRowData(1, new ResultSetRowColumnData(false,
							aModuleCriteria.getLabel().getCodeCriteria() + "_TXT" + contador));
					row.addRowData(2, new ResultSetRowColumnData(false, aModuleCriteria.getLabel().getLabel()));
					row.addRowData(3, new ResultSetRowColumnData(false, "B"));
					row.addRowData(4, new ResultSetRowColumnData(false, "S"));
					row.addRowData(5, new ResultSetRowColumnData(false, "S"));

					dataType = aModuleCriteria.getLabel().getCriteriaType().toString();
					paramData = "texto";

					if (dataType.equals("N")) {
						paramData = "numero";
					}
					if (dataType.equals("C")) {
						paramData = "texto";
					}
					row.addRowData(6, new ResultSetRowColumnData(false, paramData));
					row.addRowData(7, new ResultSetRowColumnData(false, "100"));
					row.addRowData(8, new ResultSetRowColumnData(false, "null"));
					row.addRowData(9, new ResultSetRowColumnData(false, " "));
					row.addRowData(10, new ResultSetRowColumnData(false, "N"));
					row.addRowData(11, new ResultSetRowColumnData(false, "S"));
					row.addRowData(12, new ResultSetRowColumnData(false, "0"));
					row.addRowData(13, new ResultSetRowColumnData(false, "0"));

					data.addRow(row);
					contador = contador + 1;
				}
				IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

				IResultSetHeader metaData2 = new ResultSetHeader();
				IResultSetData data2 = new ResultSetData();

				metaData2.addColumnMetaData(new ResultSetHeaderColumn("CRITERIOCODE", ICTSTypes.SYBINT4, 64));
				metaData2.addColumnMetaData(new ResultSetHeaderColumn("DESCRIPCION", ICTSTypes.SYBVARCHAR, 64));
				for (ModuleCriteria aModuleCriteria : aModuleCriteriaResponse.getmCriteriaCollection()) {

					IResultSetRow row = new ResultSetRow();

					row.addRowData(1, new ResultSetRowColumnData(false,
							aModuleCriteria.getCriteria().getCriteriaCode().toString()));
					row.addRowData(2, new ResultSetRowColumnData(false,
							aModuleCriteria.getCriteria().getDescription().toString()));
					data2.addRow(row);
				}

				IResultSetBlock resultBlock2 = new ResultSetBlock(metaData2, data2);

				wProcedureResponse.addResponseBlock(resultBlock);
				wProcedureResponse.addResponseBlock(resultBlock2);
			}

		}
		wProcedureResponse.setReturnCode(aModuleCriteriaResponse.getReturnCode());
		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final aModuleCriteriaResponse-->"
					+ wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;

	}

}
