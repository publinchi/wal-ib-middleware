/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.payment.services;

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
import com.cobiscorp.ecobis.ib.application.dtos.ContractRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ContractResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Contract;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceContract;

/**
 * @author jveloz
 *
 */
@Component(name = "ContractQueryOrchestationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "ContractQueryOrchestationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ContractQueryOrchestationCore") })

public class ContractQueryOrchestationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(ContractQueryOrchestationCore.class);
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	private static final String CLASS_NAME = "--->";

	@Reference(referenceInterface = ICoreServiceContract.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	private ICoreServiceContract coreServiceContract;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreService(ICoreServiceContract service) {
		coreServiceContract = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreService(ICoreServiceContract service) {
		coreServiceContract = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		if (logger.isInfoEnabled())
			logger.logInfo(
					"Component-Name: " + this.getClass().getSimpleName() + ", Component-Version: $$project.version$$");
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		if (logger.isInfoEnabled())
			aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration");
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreServiceContract", coreServiceContract);
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
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeGetContracts");
		HashMap<String, Object> ret = new HashMap<String, Object>();
		ret.put("IProcedureResponse", null);
		ret.put("SuccessExecutionOperation", null);
		ret.put("ErrorProcedureResponse", null);

		boolean wSuccessExecutionOperation1 = executeGetContracts(anOriginalRequest, aBagSPJavaOrchestration);
		ret.put("SuccessExecutionOperation", wSuccessExecutionOperation1);

		if (logger.isDebugEnabled())
			logger.logDebug("result exeuction operation 1: " + wSuccessExecutionOperation1);

		IProcedureResponse wProcedureResponseOperation1 = initProcedureResponse(anOriginalRequest);
		wProcedureResponseOperation1.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE,
				ICSP.ERROR_EXECUTION_SERVICE);
		ret.put("IProcedureResponse", wProcedureResponseOperation1);

		return ret;
	}

	protected boolean executeGetContracts(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeOperation1");

		IProcedureResponse wProcedureResponse = initProcedureResponse(anOriginalRequest);
		ContractResponse contractResponse = new ContractResponse();
		try {
			ContractRequest contractRequest = transformRequestToDto(aBagSPJavaOrchestration);

			contractResponse = coreServiceContract.getContracts(contractRequest);

			wProcedureResponse = transformDtoToResponse(contractResponse, aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put("GET_CONTRACT_RESPONSE", wProcedureResponse);

			return !wProcedureResponse.hasError();
		} catch (CTSServiceException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSServiceException:" + e.getMessage());
			e.printStackTrace();
			aBagSPJavaOrchestration.put("GET_CONTRACT_RESPONSE", null);
			return false;
		} catch (CTSInfrastructureException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSInfrastructureException:" + e.getMessage());
			aBagSPJavaOrchestration.put("GET_CONTRACT_RESPONSE", null);
			return false;
		}
	};

	private ContractRequest transformRequestToDto(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Entrada Contract ");
		IProcedureRequest wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

		ContractRequest contractRequest = new ContractRequest();
		contractRequest.setContractServiceId(wOriginalRequest.readValueParam("@i_canal"));
		contractRequest.setContractCategoryId(wOriginalRequest.readValueParam("@i_categoria"));
		contractRequest.setOriginalRequest(wOriginalRequest);
		return contractRequest;
	};

	private IProcedureResponse transformDtoToResponse(ContractResponse aContractResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(
					CLASS_NAME + "Transformando Dto de Salida ContractResponse :" + aContractResponse.toString());
		IProcedureResponse pResponse = initProcedureResponse(
				(IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));

		IResultSetHeader metaData = new ResultSetHeader();
		metaData = new ResultSetHeader();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("NUMBER_OF_RESULTS", ICTSTypes.SQLINT4, 4));

		IResultSetRow row = new ResultSetRow();
		IResultSetBlock resultBlock;

		IResultSetData data = new ResultSetData();
		if (aContractResponse != null && aContractResponse.getListContract().size() > 0) {
			metaData = new ResultSetHeader();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("CODIGO_CONVENIO", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("NOMBRE_CONVENIO", ICTSTypes.SQLVARCHAR, 60));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("NEMONICO_CONVENIO", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("DESCRIPCION_CONVENIO", ICTSTypes.SQLVARCHAR, 60));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("ESTADO_CONVENIO", ICTSTypes.SQLVARCHAR, 5));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("CLIENTE_CONVENIO", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("NOMBRE_LARGO", ICTSTypes.SQLVARCHAR, 60));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("TIPO_INTERFAZ", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("FECHA_MOD", ICTSTypes.SQLVARCHAR, 15));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("USUARIO_MOD", ICTSTypes.SQLVARCHAR, 15));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("ID_MONEDA", ICTSTypes.SQLINT4, 4));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("MONEDA", ICTSTypes.SQLVARCHAR, 15));

			for (Contract obj : aContractResponse.getListContract()) {
				row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, obj.getCodigoConvenio()));
				row.addRowData(2, new ResultSetRowColumnData(false, obj.getNombreConvenio()));
				row.addRowData(3, new ResultSetRowColumnData(false, obj.getNemonicoConvenio()));
				row.addRowData(4, new ResultSetRowColumnData(false, obj.getDescripcionConvenio()));
				row.addRowData(5, new ResultSetRowColumnData(false, obj.getEstadoConvenio()));
				row.addRowData(6, new ResultSetRowColumnData(false, obj.getClienteConvenio()));
				row.addRowData(7, new ResultSetRowColumnData(false, obj.getNombreLargo()));
				row.addRowData(8, new ResultSetRowColumnData(false, obj.getTipoInterfaz()));
				row.addRowData(9, new ResultSetRowColumnData(false, obj.getFechaMod()));
				row.addRowData(7, new ResultSetRowColumnData(false, obj.getUsuarioMod()));
				row.addRowData(8, new ResultSetRowColumnData(false, obj.getIdMoneda()));
				row.addRowData(9, new ResultSetRowColumnData(false, obj.getMoneda()));
				data.addRow(row);
			}
			resultBlock = new ResultSetBlock(metaData, data);
			pResponse.addResponseBlock(resultBlock);
		}

		return pResponse;
	};

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
