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
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
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
import com.cobiscorp.ecobis.ib.application.dtos.IdentificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.IdentificationResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Identification;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMask;
import com.cobiscorp.ecobis.ib.utils.dtos.Utils;

@Component(name = "MaskQueryOrchestationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "MaskQueryOrchestationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "MaskQueryOrchestationCore") })

public class MaskQueryOrchestationCore extends SPJavaOrchestrationBase {
	private static ILogger logger = LogFactory.getLogger(MaskQueryOrchestationCore.class);
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	private static final String CLASS_NAME = "--->";

	@Reference(referenceInterface = ICoreServiceMask.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	private ICoreServiceMask coreServiceMaskQuery;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreService(ICoreServiceMask service) {
		coreServiceMaskQuery = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreService(ICoreServiceMask service) {
		coreServiceMaskQuery = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
		if (logger.isInfoEnabled())
			logger.logInfo(
					"Component-Name: " + this.getClass().getSimpleName() + ", Component-Version: $$project.version$$");
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration");
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreServiceMaskQuery", coreServiceMaskQuery);
		mapInterfaces.put("coreServer", coreServer);
		com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils.validateComponentInstance(mapInterfaces);

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
		wIProcedureResponse1 = (IProcedureResponse) aBagSPJavaOrchestration.get("GET_MASK_RESPONSE");
		return wIProcedureResponse1;
	}

	protected Map<String, Object> procedureResponse(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		HashMap<String, Object> ret = new HashMap<String, Object>();
		ret.put("IProcedureResponse", null);
		ret.put("SuccessExecutionOperation", null);
		ret.put("ErrorProcedureResponse", null);

		boolean wSuccessExecutionOperation1 = executeGetMasks(anOriginalRequest, aBagSPJavaOrchestration);
		ret.put("SuccessExecutionOperation", wSuccessExecutionOperation1);

		if (logger.isDebugEnabled())
			logger.logDebug("result exeuction operation 1: " + wSuccessExecutionOperation1);

		IProcedureResponse wProcedureResponseOperation1 = initProcedureResponse(anOriginalRequest);
		wProcedureResponseOperation1.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE,
				ICSP.ERROR_EXECUTION_SERVICE);
		ret.put("IProcedureResponse", wProcedureResponseOperation1);

		return ret;
	}

	protected boolean executeGetMasks(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeOperation1");

		IProcedureResponse wProcedureResponse = initProcedureResponse(anOriginalRequest);
		IdentificationResponse identificationtResponse = new IdentificationResponse();
		try {

			ServerResponse serverResponse = this.validateServerStatus(anOriginalRequest);
			aBagSPJavaOrchestration.put("SERVER_RESPONSE", serverResponse);

			IdentificationRequest identificationRequest = transformRequestToDto(aBagSPJavaOrchestration);
			if (serverResponse.getOnLine()
					|| (!serverResponse.getOnLine() && serverResponse.getOfflineWithBalances())) {
				identificationRequest.setTarget(IMultiBackEndResolverService.TARGET_CENTRAL);
			} else {
				identificationRequest.setTarget(IMultiBackEndResolverService.TARGET_LOCAL);
			}

			identificationtResponse = coreServiceMaskQuery.getMask(identificationRequest);
			wProcedureResponse = transformDtoToResponse(identificationtResponse, aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put("GET_MASK_RESPONSE", wProcedureResponse);

			return !wProcedureResponse.hasError();
		} catch (CTSServiceException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSServiceException:" + e.getMessage());
			e.printStackTrace();
			aBagSPJavaOrchestration.put("GET_MASK_RESPONSE", null);
			return false;
		} catch (CTSInfrastructureException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSInfrastructureException:" + e.getMessage());
			aBagSPJavaOrchestration.put("GET_MASK_RESPONSE", null);
			return false;
		}
	}

	private IdentificationRequest transformRequestToDto(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Entrada");
		IProcedureRequest wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

		IdentificationRequest identificationRequest = new IdentificationRequest();
		identificationRequest.setTypePerson(wOriginalRequest.readValueParam("@i_tpersona"));
		identificationRequest.setOriginalRequest(wOriginalRequest);

		return identificationRequest;
	}

	private IProcedureResponse transformDtoToResponse(IdentificationResponse identificationResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Salida:" + identificationResponse.toString());
		IProcedureResponse pResponse = initProcedureResponse(
				(IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));

		IResultSetHeader metaData = new ResultSetHeader();
		metaData = new ResultSetHeader();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("NUMBER_OF_RESULTS", ICTSTypes.SQLINT4, 4));

		IResultSetRow row = new ResultSetRow();
		IResultSetBlock resultBlock;

		IResultSetData data = new ResultSetData();
		if (identificationResponse != null && identificationResponse.getListIdentification().size() > 0) {
			metaData = new ResultSetHeader();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("TYPE", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("NAME", ICTSTypes.SQLVARCHAR, 30));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("MASK", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("CUSTOMERTYPE", ICTSTypes.SQLVARCHAR, 5));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("PROVINCEVALIDATE", ICTSTypes.SQLVARCHAR, 5));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("QUICKOPENNING", ICTSTypes.SQLVARCHAR, 5));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("LOCKCUSTOMER", ICTSTypes.SQLVARCHAR, 5));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("NATIONALITY", ICTSTypes.SQLINT4, 6));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("CHECKSUM", ICTSTypes.SQLVARCHAR, 5));

			for (Identification obj : identificationResponse.getListIdentification()) {
				row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, obj.getType()));
				row.addRowData(2, new ResultSetRowColumnData(false, obj.getName()));
				row.addRowData(3, new ResultSetRowColumnData(false, obj.getMask()));
				row.addRowData(4, new ResultSetRowColumnData(false, obj.getCustomerType()));
				row.addRowData(5, new ResultSetRowColumnData(false, obj.getProvinceValidate()));
				row.addRowData(6, new ResultSetRowColumnData(false, obj.getQuickOpening()));
				row.addRowData(7, new ResultSetRowColumnData(false, obj.getLockCustomer()));
				row.addRowData(8, new ResultSetRowColumnData(false, obj.getNationality()));
				row.addRowData(9, new ResultSetRowColumnData(false, obj.getCheckSum()));
				data.addRow(row);
			}
			resultBlock = new ResultSetBlock(metaData, data);
			pResponse.addResponseBlock(resultBlock);
		}

		return pResponse;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/******* inyeccion dependencias server *****/
	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	public void bindCoreServer(ICoreServer service) {
		coreServer = service;
	}

	public void unbindCoreServer(ICoreServer service) {
		coreServer = null;
	}

	protected ICoreServer getCoreServer() {
		return coreServer;
	}

	protected ServerResponse validateServerStatus(IProcedureRequest anOriginalRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Iniciando consulta de Estado del Servidor");
		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setChannelId(anOriginalRequest.readValueFieldInHeader("servicio"));
		ServerResponse responseServer = getCoreServer().getServerStatus(serverRequest);
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "RESPUESTA CORE validateServerStatus:" + responseServer);
		return responseServer;
	}

}
