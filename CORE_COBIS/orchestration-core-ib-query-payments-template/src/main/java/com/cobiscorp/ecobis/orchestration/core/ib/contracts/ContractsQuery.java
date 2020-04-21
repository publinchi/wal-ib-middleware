package com.cobiscorp.ecobis.orchestration.core.ib.contracts;

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
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.ecobis.ib.application.dtos.ContractReferencesRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ContractReferencesResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ContractRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ContractResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Contract;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ContractReferences;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceContract;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceContractReferences;
import com.cobiscorp.ecobis.orchestration.core.ib.contractreferences.ContractReferencesQuery;

/**
 * @author jveloz
 * @since Jan 22, 2015
 * @version 1.0.0
 */
@Component(name = "ContractsQuery", immediate = false)
@Service(value = { ICoreServiceContract.class })
@Properties(value = { @Property(name = "service.description", value = "ContractsQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ContractsQuery") })

public class ContractsQuery extends SPJavaOrchestrationBase implements ICoreServiceContract {
	private static ILogger logger = LogFactory.getLogger(ContractsQuery.class);
	private static final String SP_NAME = "cobis..sp_bv_consulta_convenios";
	private static final int COL_CODIGO_CONVENIO = 0;
	private static final int COL_NOMBRE_CONVENIO = 1;
	private static final int COL_NEMONICO_CONVENIO = 2;
	private static final int COL_DESCRIPCION_CONVENIO = 3;
	private static final int COL_ESTADO_CONVENIO = 4;
	private static final int COL_CLIENTE_CONVENIO = 5;
	private static final int COL_NOMBRE_LARGO = 6;
	private static final int COL_TIPO_INTERFAZ = 7;
	private static final int COL_FECHA_MOD = 8;
	private static final int COL_USUARIO_MOD = 9;
	private static final int COL_ID_MONEDA = 10;
	private static final int COL_MONEDA = 11;
	private static final int COL_PARAMETRO = 0;
	private static final int COL_VALOR = 1;

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
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceContract#
	 * getContracts(com.cobiscorp.ecobis.ib.application.dtos.ContractRequest)
	 */
	@Override
	public ContractResponse getContracts(ContractRequest aContractRequest)
			throws CTSServiceException, CTSInfrastructureException {
		// TODO Auto-generated method stub
		if (logger.isInfoEnabled()) {
			logger.logInfo("Iniciando Servicio DUMMY ACOPLADO CORE COBIS: getContractReference");
		}
		IProcedureResponse wResponse = Execution(SP_NAME, aContractRequest);
		ContractResponse wContractResponse = transformToContractResponse(wResponse, "getContracts");
		return wContractResponse;

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

	private IProcedureResponse Execution(String SpName, ContractRequest aContractRequest)
			throws CTSServiceException, CTSInfrastructureException {
		IProcedureRequest request = new ProcedureRequestAS();
		String versionRecaudoIB = null;
		String wParameterName = null;
		// leo el parametro de la version de recaudo
		IProcedureResponse wParametro = parameterByName("RECIB", "BVI");
		IResultSetRow[] rowsParameters = wParametro.getResultSet(1).getData().getRowsAsArray();
		IResultSetRow iResultSetRow = rowsParameters[0];
		IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
		wParameterName = columns[COL_PARAMETRO].getValue();
		versionRecaudoIB = columns[COL_VALOR].getValue();		
		
		if (logger.isDebugEnabled())
			logger.logDebug("<<<METHOD: Execution>>>");

		// IProcedureRequest request = initProcedureRequest(anOriginalRequest);
		if (versionRecaudoIB.equals("S")) {
			request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
					IMultiBackEndResolverService.TARGET_LOCAL);
		} else {
			request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
					IMultiBackEndResolverService.TARGET_CENTRAL);
		}
		;
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1801037");
		request.setSpName(SpName);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1801037");
		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "S");
		request.addInputParam("@i_canal", ICTSTypes.SQLVARCHAR, aContractRequest.getContractServiceId());
		request.addInputParam("@i_categoria", ICTSTypes.SQLVARCHAR, aContractRequest.getContractCategoryId());

		if (logger.isDebugEnabled()) {
			logger.logDebug("<<<Request: >>>" + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("<<<Start Request>>>");
		}

		IProcedureResponse wResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("<<IMPLEMENTATION getContracts: Response >>>" + wResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("<<<Finalize Response>>>");
		}
		return wResponse;
	};

	private ContractResponse transformToContractResponse(IProcedureResponse aProcedureResponse, String Method) {
		ContractResponse wContractResponse = new ContractResponse();
		List<Contract> listContract = new ArrayList<Contract>();

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: transformToContractResponse***"
					+ aProcedureResponse.getProcedureResponseAsString());
		}

		if (Method.equals("getContracts")) {
			if (logger.isDebugEnabled()) {
				logger.logDebug("<<<ProcedureResponse: >>>" + aProcedureResponse.getProcedureResponseAsString());
			}
			IResultSetRow[] rowsContractReferences = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();
			for (IResultSetRow iResultSetRow : rowsContractReferences) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				Contract wContract = new Contract();
				wContract.setCodigoConvenio(columns[COL_CODIGO_CONVENIO].getValue());
				wContract.setNombreConvenio(columns[COL_NOMBRE_CONVENIO].getValue());
				wContract.setNemonicoConvenio(columns[COL_NEMONICO_CONVENIO].getValue());
				wContract.setDescripcionConvenio(columns[COL_DESCRIPCION_CONVENIO].getValue());
				wContract.setEstadoConvenio(columns[COL_ESTADO_CONVENIO].getValue());
				wContract.setClienteConvenio(columns[COL_CLIENTE_CONVENIO].getValue());
				wContract.setNombreLargo(columns[COL_NOMBRE_LARGO].getValue());
				wContract.setTipoInterfaz(columns[COL_TIPO_INTERFAZ].getValue());
				wContract.setFechaMod(columns[COL_FECHA_MOD].getValue());
				wContract.setUsuarioMod(columns[COL_USUARIO_MOD].getValue());
				wContract.setIdMoneda(columns[COL_ID_MONEDA].getValue());
				wContract.setMoneda(columns[COL_MONEDA].getValue());
				listContract.add(wContract);
			}
			wContractResponse.setListContract(listContract);
		}
		;

		wContractResponse.setReturnCode(aProcedureResponse.getReturnCode());
		Message[] message = Utils.returnArrayMessage(aProcedureResponse);
		wContractResponse.setMessages(message);
		return wContractResponse;
	};

	// metodo para obtener parametro de la cl_parametro
	private IProcedureResponse parameterByName(String nemonico, String producto)
			throws CTSServiceException, CTSInfrastructureException {
		IProcedureRequest request = new ProcedureRequestAS();

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setSpName("cob_bvirtual..sp_param_ini_bv");
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "V");
		request.addInputParam("@i_producto", ICTSTypes.SQLVARCHAR, producto);
		request.addInputParam("@i_nemonico", ICTSTypes.SQLVARCHAR, nemonico);
		IProcedureResponse wResponse = executeCoreBanking(request);
		return wResponse;
	}
}
