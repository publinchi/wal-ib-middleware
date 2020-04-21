package com.cobiscorp.ecobis.orchestration.core.ib.query.modules;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.ContractRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ContractResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ModuleRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ModuleResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Contract;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Module;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceContract;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.IWSModules;
import com.cobiscorp.ecobis.orchestration.ws.base.SintesisBaseTemplate;

/**
 * Credit Lines
 * 
 * @since Jul 07, 2015
 * @author dmorla
 * @version 1.0.0
 * 
 */
@Component(name = "ModulesOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "ModulesOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ModulesOrchestrationCore") })
public class ModulesOrchestrationCore extends SintesisBaseTemplate {

	private static ILogger logger = LogFactory.getLogger(ModulesOrchestrationCore.class);
	private static final String CLASS_NAME = "ModulesOrchestrationCore--->";
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	static final String ORIGINAL_RESPONSE = "ORIGINAL_RESPONSE";
	static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	static final String INTERFAZ_LINEA = "L";
	private java.util.Properties properties;

	/**
	 * Read configuration of parent component
	 */

	public void loadConfiguration(IConfigurationReader arg0) {
		super.loadConfiguration(arg0);
		this.properties = arg0.getProperties("//property");
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = IWSModules.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceModules", unbind = "unbindCoreServiceModules")
	protected IWSModules coreServiceModules;

	/**
	 * Instance Service Interface
	 * 
	 * @param service
	 */
	public void bindCoreServiceModules(IWSModules service) {
		coreServiceModules = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */

	public void unbindCoreServiceModules(IWSModules service) {
		coreServiceModules = null;
	}

	@Reference(referenceInterface = ICoreServiceContract.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceContract coreServiceContract;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreServiceContract service) {
		coreServiceContract = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreServiceContract service) {
		coreServiceContract = null;
	}

	/**
	 * /** Execute transfer first step of service
	 * <p>
	 * This method is the main executor of transactional contains the original
	 * input parameters.
	 * 
	 * @param anOriginalRequest
	 *            - Information original sended by user's.
	 * @param aBagSPJavaOrchestration
	 *            - Object dictionary transactional steps.
	 * 
	 * @return
	 *         <ul>
	 *         <li>IProcedureResponse - Represents the service execution.</li>
	 *         </ul>
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeJavaOrchestration");
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		try {
			Map<String, Object> mapInterfaces = new HashMap<String, Object>();
			mapInterfaces.put("coreServiceModules", coreServiceModules);
			mapInterfaces.put("coreServiceContract", coreServiceContract);
			Utils.validateComponentInstance(mapInterfaces);

			if (anOriginalRequest == null)
				if (logger.isInfoEnabled())
					logger.logInfo(CLASS_NAME + "Original Request ISNULL");

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
	public IProcedureResponse executeWSMethod(Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		IProcedureRequest anOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		IProcedureResponse responseProc = null;
		try {

			// consulta de stocks
			responseProc = queryProviderTransaction(anOriginalRequest, aBagSPJavaOrchestration);

			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseProc);

			if (Utils.flowError("updateProviderTransaction", responseProc)) {

				return responseProc;
			}

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
	private IProcedureResponse queryProviderTransaction(IProcedureRequest anOriginalRequest, Map<String, Object> aBag) {

		ModuleResponse wModuleResp = new ModuleResponse();
		ModuleRequest wModuleRequest = transformModuleRequest(anOriginalRequest.clone());

		try {
			wModuleRequest.setOriginalRequest(anOriginalRequest);
			wModuleResp = coreServiceModules.getModule(wModuleRequest, aBag, properties);
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}
		// union DMO
		ContractResponse contractResponse = new ContractResponse();
		ContractRequest contractRequest = transformRequestToDto(anOriginalRequest.clone());
		try {
			contractRequest.setOriginalRequest(anOriginalRequest);
			contractResponse = coreServiceContract.getContracts(contractRequest);
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}
		return transformModuleResponse(wModuleResp, contractResponse, aBag);
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

	/******************
	 * Transformación de ProcedureRequest a StockRequest
	 ********************/

	private ModuleRequest transformModuleRequest(IProcedureRequest aRequest) {
		ModuleRequest wModuleRequest = new ModuleRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());
		if (aRequest.readValueParam("@i_id_operativo") != null)
			wModuleRequest.setIdOperation(aRequest.readValueParam("@i_id_operativo"));
		// else
		// wModuleRequest.setIdOperation(idOperativo);
		return wModuleRequest;
	}

	private ContractRequest transformRequestToDto(IProcedureRequest aRequest) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Entrada Contract ");
		// IProcedureRequest wOriginalRequest = (ProcedureRequestAS)
		// aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

		ContractRequest contractRequest = new ContractRequest();
		contractRequest.setContractServiceId(aRequest.readValueParam("@i_canal"));
		contractRequest.setContractCategoryId(aRequest.readValueParam("@i_categoria"));
		// contractRequest.setOriginalRequest(wOriginalRequest);
		return contractRequest;
	};

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformModuleResponse(ModuleResponse aModuleResponse,
			ContractResponse aContractResponse, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse sintesisOriginalResponse = (IProcedureResponse) aBagSPJavaOrchestration
				.get(ORIGINAL_RESPONSE);
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		if (aModuleResponse.getReturnCode() != 0) {
			if (logger.isDebugEnabled())
				logger.logDebug("MSG ERROR " + aModuleResponse.getmessageError());
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException(aModuleResponse.getMessages()));
		} else {

			Integer secuencial = 1;

			// Agregar Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();
			IResultSetData dataSort = new ResultSetData();
			IResultSetRow row = new ResultSetRow();

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

			if (sintesisOriginalResponse.readValueParam("@o_coderror").equals("0")) {
				for (Module aModule : aModuleResponse.getModuleCollection()) {
					if (aModule.getType().equals("P")) { // solo los tipo P
															// Sintesis
						row = new ResultSetRow();

						row.addRowData(1, new ResultSetRowColumnData(false, secuencial.toString()));
						row.addRowData(2, new ResultSetRowColumnData(false, aModule.getDescription()));
						row.addRowData(3, new ResultSetRowColumnData(false, "L" + aModule.getCodModule().toString()));
						row.addRowData(4, new ResultSetRowColumnData(false, aModule.getDescription()));
						row.addRowData(5, new ResultSetRowColumnData(false, "V"));
						row.addRowData(6, new ResultSetRowColumnData(false, ""));
						row.addRowData(7, new ResultSetRowColumnData(false, ""));
						row.addRowData(8, new ResultSetRowColumnData(false, "L"));
						row.addRowData(9, new ResultSetRowColumnData(false, ""));
						row.addRowData(10, new ResultSetRowColumnData(false, aModule.getType()));
						row.addRowData(11, new ResultSetRowColumnData(false, ""));
						row.addRowData(12, new ResultSetRowColumnData(false, ""));

						data.addRow(row);
						secuencial = secuencial + 1;
					}
				}
			}

			if (aContractResponse != null && aContractResponse.getListContract().size() > 0) {

				for (Contract obj : aContractResponse.getListContract()) {
					if (!obj.getTipoInterfaz().equals(INTERFAZ_LINEA)) {
						row = new ResultSetRow();
						row.addRowData(1, new ResultSetRowColumnData(false, secuencial.toString()));
						row.addRowData(2, new ResultSetRowColumnData(false, obj.getDescripcionConvenio()));
						row.addRowData(3,
								new ResultSetRowColumnData(false, obj.getTipoInterfaz() + obj.getCodigoConvenio()));
						row.addRowData(4, new ResultSetRowColumnData(false, obj.getDescripcionConvenio()));
						row.addRowData(5, new ResultSetRowColumnData(false, obj.getEstadoConvenio()));
						row.addRowData(6, new ResultSetRowColumnData(false, obj.getClienteConvenio()));
						row.addRowData(7, new ResultSetRowColumnData(false, obj.getNombreLargo()));
						row.addRowData(8, new ResultSetRowColumnData(false, obj.getTipoInterfaz()));
						row.addRowData(9, new ResultSetRowColumnData(false, obj.getFechaMod()));
						row.addRowData(10, new ResultSetRowColumnData(false, obj.getUsuarioMod()));
						row.addRowData(11, new ResultSetRowColumnData(false, obj.getIdMoneda()));
						row.addRowData(12, new ResultSetRowColumnData(false, obj.getMoneda()));
						data.addRow(row);
						secuencial = secuencial + 1;
					}
				}
				// resultBlock = new ResultSetBlock(metaData, data);
				// pResponse.addResponseBlock(resultBlock);
			}

			List<ResultSetRow> listSort = data.getRows();

			// Ordenamiento MOdulos DMO
			Collections.sort(listSort, new Comparator<ResultSetRow>() {

				@Override
				public int compare(ResultSetRow l1, ResultSetRow l2) {
					// TODO Auto-generated method stub
					IResultSetRowColumnData[] columns = l1.getColumnsAsArray();
					IResultSetRowColumnData[] columns2 = l2.getColumnsAsArray();
					String a = columns[1].getValue();
					String b = columns2[1].getValue();

					return a.compareTo(b);
				}
			});
			Iterator<ResultSetRow> itListaOrdenada = listSort.iterator();
			while (itListaOrdenada.hasNext()) {
				ResultSetRow elementoLista = (ResultSetRow) itListaOrdenada.next();
				dataSort.addRow(elementoLista);
			}

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, dataSort);

			wProcedureResponse.addResponseBlock(resultBlock);

		}

		wProcedureResponse.setReturnCode(aModuleResponse.getReturnCode());

		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;

	}

}
