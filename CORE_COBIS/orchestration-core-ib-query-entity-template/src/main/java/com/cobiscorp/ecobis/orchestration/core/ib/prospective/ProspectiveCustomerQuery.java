package com.cobiscorp.ecobis.orchestration.core.ib.prospective;

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
import com.cobiscorp.ecobis.ib.application.dtos.EntityRequest;
import com.cobiscorp.ecobis.ib.application.dtos.EntityResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.FullEntity;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCustomerProspective;

@Component(name = "ProspectiveCustomerQuery", immediate = false)
@Service(value = { ICoreServiceCustomerProspective.class })
@Properties(value = { @Property(name = "service.description", value = "ProspectiveCustomerQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ProspectiveCustomerQuery") })

public class ProspectiveCustomerQuery extends SPJavaOrchestrationBase implements ICoreServiceCustomerProspective {
	private static final String COBIS_CONTEXT = "COBIS";
	private static ILogger logger = LogFactory.getLogger(ProspectiveCustomerQuery.class);
	private static final int COL_NUMBER = 0;
	private static final int COL_FIRST_LAST = 1;
	private static final int COL_SECOND_NAME = 2;
	private static final int COL_MARRIED_SURNAME = 3;
	private static final int COL_FIRST_NAME = 4;
	private static final int COL_MIDDLE_NAME = 5;
	private static final int COL_ID = 6;
	private static final int COL_TYPE_ID = 7;
	private static final int COL_OFFICIAL = 8;
	private static final int COL_OFFICIAL_NAME = 9;
	private static final int COL_STATUS = 10;

	@Override
	public EntityResponse getProspectiveCustomer(EntityRequest aEntityRequest)
			throws CTSServiceException, CTSInfrastructureException {

		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: getProspectiveCustomer");
			logger.logInfo("RESPUESTA CORE COBIS GENERADA");
		}
		IProcedureRequest request = new ProcedureRequestAS();
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1318");

		request.setSpName("sp_prospectos_ofi");

		request.addInputParam("@t_trn", ICTSTypes.SQLINT1, "1318");
		request.addInputParam("@i_tipo", ICTSTypes.SQLINT2, aEntityRequest.getTipo().toString());
		request.addInputParam("@i_subtipo", ICTSTypes.SQLVARCHAR, aEntityRequest.getSubtipo());
		request.addInputParam("@i_modo", ICTSTypes.SQLINT2, aEntityRequest.getModo().toString());
		if (aEntityRequest.getValor() != null)
			request.addInputParam("@i_valor", ICTSTypes.SQLVARCHAR, aEntityRequest.getValor());
		if (aEntityRequest.getEnte() != null)
			request.addInputParam("@i_ente", ICTSTypes.SQLINT4, aEntityRequest.getEnte().toString());
		if (aEntityRequest.getNombre() != null)
			request.addInputParam("@i_nombre", ICTSTypes.SQLVARCHAR, aEntityRequest.getNombre());
		if (aEntityRequest.getP_apellido() != null)
			request.addInputParam("@i_p_apellido", ICTSTypes.SQLVARCHAR, aEntityRequest.getP_apellido());
		if (aEntityRequest.getS_apellido() != null)
			request.addInputParam("@i_s_apellido", ICTSTypes.SQLVARCHAR, aEntityRequest.getS_apellido());
		if (aEntityRequest.getC_apellido() != null)
			request.addInputParam("@i_c_apellido", ICTSTypes.SQLVARCHAR, aEntityRequest.getC_apellido());
		if (aEntityRequest.getCed_ruc() != null)
			request.addInputParam("@i_ced_ruc", ICTSTypes.SQLVARCHAR, aEntityRequest.getCed_ruc());
		if (aEntityRequest.getPasaporte() != null)
			request.addInputParam("@i_pasaporte", ICTSTypes.SQLVARCHAR, aEntityRequest.getPasaporte());
		if (aEntityRequest.getDepartamento() != null)
			request.addInputParam("@i_departamento", ICTSTypes.SQLVARCHAR, aEntityRequest.getDepartamento());

		/*** FIN AQUI EL CODIGO ****/
		if (logger.isDebugEnabled()) {
			logger.logDebug("Request: " + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Start Request");
		}

		/* Ejecuta y obtiene la respuesta */
		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response: " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Finalize Response");
		}

		EntityResponse entityResponse = transformToEntityResponse(pResponse, aEntityRequest.getSubtipo());
		return entityResponse;
	}

	private EntityResponse transformToEntityResponse(IProcedureResponse aProcedureResponse, String aSubtipo) {
		EntityResponse EntityResp = new EntityResponse();
		List<FullEntity> fullEntityCollection = new ArrayList<FullEntity>();
		FullEntity aFullEntity = null;
		if (logger.isDebugEnabled()) {
			logger.logDebug("ProcedureResponse: " + aProcedureResponse.getProcedureResponseAsString());
		}
		IResultSetRow[] rowsEntity = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

		for (IResultSetRow iResultSetRow : rowsEntity) {
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
			aFullEntity = new FullEntity();

			aFullEntity.setNumber(columns[COL_NUMBER].getValue());
			if (aSubtipo.equals("P")) {
				aFullEntity.setFirstLast(columns[COL_FIRST_LAST].getValue());
				aFullEntity.setSecondName(columns[COL_SECOND_NAME].getValue());
				aFullEntity.setMarriedSurname(columns[COL_MARRIED_SURNAME].getValue());
				aFullEntity.setFirstName(columns[COL_FIRST_NAME].getValue());
				aFullEntity.setMiddleName(columns[COL_MIDDLE_NAME].getValue());
				aFullEntity.setId(columns[COL_ID].getValue());
				aFullEntity.setTypeId(columns[COL_TYPE_ID].getValue());
				aFullEntity.setOfficial(columns[COL_OFFICIAL].getValue());
				aFullEntity.setOfficialName(columns[COL_OFFICIAL_NAME].getValue());
				aFullEntity.setLocked(columns[COL_STATUS].getValue());
			} else {
				aFullEntity.setCompanyName(columns[1].getValue());
				aFullEntity.setBusinessName(columns[2].getValue());
				aFullEntity.setId(columns[3].getValue());
				aFullEntity.setTypeId(columns[4].getValue());
				aFullEntity.setOfficial(columns[5].getValue());
				aFullEntity.setOfficialName(columns[6].getValue());
				aFullEntity.setLocked(columns[7].getValue());
			}
			fullEntityCollection.add(aFullEntity);
		}
		EntityResp.setEntityCollection(fullEntityCollection);
		return EntityResp;
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
}
