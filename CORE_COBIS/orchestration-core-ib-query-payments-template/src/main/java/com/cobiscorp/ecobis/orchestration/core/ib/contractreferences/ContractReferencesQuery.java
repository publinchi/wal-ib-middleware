package com.cobiscorp.ecobis.orchestration.core.ib.contractreferences;

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
import com.cobiscorp.ecobis.ib.application.dtos.CheckBookPreAuthRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CheckBookPreAuthResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CheckbookRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CheckbookResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CheckbookSuspendResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ContractReferencesRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ContractReferencesResponse;
import com.cobiscorp.ecobis.ib.application.dtos.NextLaborDayRequest;
import com.cobiscorp.ecobis.ib.application.dtos.NextLaborDayResponse;
import com.cobiscorp.ecobis.ib.application.dtos.NoPaycheckOrderRequest;
import com.cobiscorp.ecobis.ib.application.dtos.RequestCheckbookRequest;
import com.cobiscorp.ecobis.ib.application.dtos.RequestCheckbookResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TypesOfCheckbookRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TypesOfCheckbookResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ContractReferences;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCheckbook;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceContractReferences;

@Component(name = "ContractReferencesQuery", immediate = false)
@Service(value = { ICoreServiceContractReferences.class })
@Properties(value = { @Property(name = "service.description", value = "ContractReferencesQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ContractReferencesQuery") })

public class ContractReferencesQuery extends SPJavaOrchestrationBase implements ICoreServiceContractReferences {

	private static ILogger logger = LogFactory.getLogger(ContractReferencesQuery.class);

	private static final String SP_NAME = "..sp_conv_referencia";
	private static final String TRN_CONTRACT_REFERENCES = "659";
	private static final String TRN_CONTRACT_REFERENCES_IB = "18159";
	private static final int COL_CAMPO = 0;
	private static final int COL_ETIQUETA = 1;
	private static final int COL_TIPO = 2;
	private static final int COL_HABILITADO = 3;
	private static final int COL_OBLIGATORIO = 4;
	private static final int COL_TIPO_DATO = 5;
	private static final int COL_LONGITUD = 6;
	private static final int COL_CAMPO_DEFAULT = 7;
	private static final int COL_CATALOGO = 8;
	private static final int COL_REDIGITAR = 9;
	private static final int COL_VISIBLE = 10;
	private static final int COL_ORDEN = 11;

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
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceContractReferences#getContractReference(com.cobiscorp.ecobis.
	 * ib.application.dtos.ContractReferencesRequest)
	 */
	@Override
	public ContractReferencesResponse getContractReference(ContractReferencesRequest aContractReferencesRequest)
			throws CTSServiceException, CTSInfrastructureException {
		// TODO Auto-generated method stub
		if (logger.isInfoEnabled()) {
			logger.logInfo("Iniciando Servicio DUMMY ACOPLADO CORE COBIS: getContractReference");
		}
		IProcedureResponse wResponse = Execution(SP_NAME, aContractReferencesRequest);
		ContractReferencesResponse wContractReferencesResponse = transformToContractReferencesResponse(wResponse,
				"getContractReference");
		return wContractReferencesResponse;
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

	private IProcedureResponse Execution(String SpName, ContractReferencesRequest aContractReferencesRequest)
			throws CTSServiceException, CTSInfrastructureException {
		IProcedureRequest request = new ProcedureRequestAS();
		String base = null;
		String trn = null;
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

		if (versionRecaudoIB.equals("S")) {
			trn = TRN_CONTRACT_REFERENCES_IB;
			base = "cob_bvirtual";
			request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
					IMultiBackEndResolverService.TARGET_LOCAL);
		} else {
			trn = TRN_CONTRACT_REFERENCES;
			base = "cob_remesas";
			request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
					IMultiBackEndResolverService.TARGET_CENTRAL);
		}
		;
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1801027");
		request.setSpName(base + SpName);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, trn);
		request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, aContractReferencesRequest.getUserName());
		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "S");
		request.addInputParam("@i_convenio", ICTSTypes.SQLINT4, aContractReferencesRequest.getContractId().toString());

		if (logger.isDebugEnabled()) {
			logger.logDebug("<<<Request: >>>" + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("<<<Start Request>>>");
		}

		IProcedureResponse wResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug(
					"<<IMPLEMENTATION getContractReference: Response >>>" + wResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("<<<Finalize Response>>>");
		}
		return wResponse;
	}

	private ContractReferencesResponse transformToContractReferencesResponse(IProcedureResponse aProcedureResponse,
			String Method) {
		ContractReferencesResponse wContractReferencesResponse = new ContractReferencesResponse();
		List<ContractReferences> listContractReferences = new ArrayList<ContractReferences>();

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: transformToContractReferencesResponse***"
					+ aProcedureResponse.getProcedureResponseAsString());
		}

		if (Method.equals("getContractReference")) {
			if (logger.isDebugEnabled()) {
				logger.logDebug("<<<ProcedureResponse: >>>" + aProcedureResponse.getProcedureResponseAsString());
			}
			IResultSetRow[] rowsContractReferences = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();
			for (IResultSetRow iResultSetRow : rowsContractReferences) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				ContractReferences wContractReferences = new ContractReferences();
				wContractReferences.setCampo(columns[COL_CAMPO].getValue());
				wContractReferences.setEtiqueta(columns[COL_ETIQUETA].getValue());
				wContractReferences.setTipo(columns[COL_TIPO].getValue());
				wContractReferences.setHabilitado(columns[COL_HABILITADO].getValue());
				wContractReferences.setObligatorio(columns[COL_OBLIGATORIO].getValue());
				wContractReferences.setTipoDato(columns[COL_TIPO_DATO].getValue());
				wContractReferences.setLongitud(columns[COL_LONGITUD].getValue());
				wContractReferences.setCampoDefault(columns[COL_CAMPO_DEFAULT].getValue());
				wContractReferences.setCatalogo(columns[COL_CATALOGO].getValue());
				wContractReferences.setRedigitar(columns[COL_REDIGITAR].getValue());
				wContractReferences.setVisible(columns[COL_VISIBLE].getValue());
				wContractReferences.setOrden(columns[COL_ORDEN].getValue());

				listContractReferences.add(wContractReferences);
			}
			wContractReferencesResponse.setListContractLabel(listContractReferences);
		}		

		wContractReferencesResponse.setReturnCode(aProcedureResponse.getReturnCode());
		Message[] message = Utils.returnArrayMessage(aProcedureResponse);
		wContractReferencesResponse.setMessages(message);
		return wContractReferencesResponse;
	}

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
