/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.entity;

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
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Entity;
import com.cobiscorp.ecobis.ib.orchestration.dtos.FullEntity;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceEntity;

@Component(name = "FullEntityQuery", immediate = false)
@Service(value = { ICoreServiceEntity.class })
@Properties(value = { @Property(name = "service.description", value = "FullEntityQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "FullEntityQuery") })

public class FullEntityQuery extends SPJavaOrchestrationBase implements ICoreServiceEntity {

	private static final String COBIS_CONTEXT = "COBIS";

	/**
	 * Instance logger component
	 */
	private static ILogger logger = LogFactory.getLogger(FullEntityQuery.class);

	/* PERSON */
	private static final int COL_NUMBER_P = 0;
	private static final int COL_FIRST_LAST_P = 1;
	private static final int COL_SECOND_NAME_P = 2;
	private static final int COL_MARRIED_SURNAME_P = 3;
	private static final int COL_FIRST_NAME_P = 4;
	private static final int COL_MIDDLE_NAME_P = 5;
	private static final int COL_ID_P = 6;
	private static final int COL_TYPE_ID_P = 7;
	private static final int COL_OFFICIAL_P = 8;
	private static final int COL_OFFICIAL_NAME_P = 9;
	private static final int COL_LOCKED_P = 10;
	private static final int COL_STATUS_P = 11;
	private static final int COL_CUSTOMER_P = 12;
	private static final int COL_DESCRIPTION_STATUS_P = 13;
	/* COMPANY */
	private static final int COL_NUMBER_C = 0;
	private static final int COL_COMPANY_NAME_C = 1;
	private static final int COL_MARRIED_SURNAME_C = 2;
	private static final int COL_FIRST_NAME_C = 3;
	private static final int COL_MIDDLE_NAME_C = 4;
	private static final int COL_BUSINESS_NAME_C = 5;
	private static final int COL_ID_C = 6;
	private static final int COL_TYPE_ID_C = 7;
	private static final int COL_OFFICIAL_C = 8;
	private static final int COL_OFFICIAL_NAME_C = 9;
	private static final int COL_LOCKED_C = 10;
	private static final int COL_CUSTOMER_C = 11;
	private static final int COL_STATUS_C = 12;
	private static final int COL_DESCRIPTION_STATUS_C = 13;

	private static final String SP_NAME = "cobis..sp_cons_entes_bv";

	@Override
	public EntityResponse GetEntity(EntityRequest aEntityRequest)
			throws CTSServiceException, CTSInfrastructureException {

		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: GetEntity");
			logger.logInfo("RESPUESTA CORE COBIS GENERADA");
		}
		IProcedureRequest request = new ProcedureRequestAS();

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");

		request.setSpName("cobis..sp_se_ente");
		request.addInputParam("@t_trn", ICTSTypes.SQLINT1, "1182");
		if (aEntityRequest.getSubtipo() != null)
			request.addInputParam("@i_subtipo", ICTSTypes.SQLVARCHAR, aEntityRequest.getSubtipo());
		if (aEntityRequest.getTipo() != null)
			request.addInputParam("@i_tipo", ICTSTypes.SQLINT2, aEntityRequest.getTipo().toString());
		if (aEntityRequest.getModo() != null)
			request.addInputParam("@i_modo", ICTSTypes.SQLINT2, aEntityRequest.getModo().toString());
		if (aEntityRequest.getValor() != null)
			request.addInputParam("@i_valor", ICTSTypes.SQLVARCHAR, aEntityRequest.getValor().toString());
		if (aEntityRequest.getEnte() != null)
			request.addInputParam("@i_ente", ICTSTypes.SQLINT4, aEntityRequest.getEnte().toString());
		if (aEntityRequest.getNombre() != null)
			request.addInputParam("@i_nombre", ICTSTypes.SQLVARCHAR, aEntityRequest.getNombre().toString());
		if (aEntityRequest.getS_nombre() != null)
			request.addInputParam("@i_s_nombre", ICTSTypes.SQLVARCHAR, aEntityRequest.getS_nombre().toString());
		if (aEntityRequest.getP_apellido() != null)
			request.addInputParam("@i_p_apellido", ICTSTypes.SQLVARCHAR, aEntityRequest.getP_apellido().toString());
		if (aEntityRequest.getS_apellido() != null)
			request.addInputParam("@i_s_apellido", ICTSTypes.SQLVARCHAR, aEntityRequest.getS_apellido().toString());
		if (aEntityRequest.getC_apellido() != null)
			request.addInputParam("@i_c_apellido", ICTSTypes.SQLVARCHAR, aEntityRequest.getC_apellido().toString());
		if (aEntityRequest.getCed_ruc() != null)
			request.addInputParam("@i_ced_ruc", ICTSTypes.SQLVARCHAR, aEntityRequest.getCed_ruc().toString());
		if (aEntityRequest.getOficina() != null)
			request.addInputParam("@i_oficina", ICTSTypes.SQLINT2, aEntityRequest.getOficina().toString());
		if (aEntityRequest.getNombre_completo() != null)
			request.addInputParam("@i_nombre_completo", ICTSTypes.SQLVARCHAR,
					aEntityRequest.getNombre_completo().toString());
		if (aEntityRequest.getPasaporte() != null)
			request.addInputParam("@i_pasaporte", ICTSTypes.SQLVARCHAR, aEntityRequest.getPasaporte().toString());
		if (aEntityRequest.getEs_cliente() != null)
			request.addInputParam("@i_es_cliente", ICTSTypes.SQLVARCHAR, aEntityRequest.getEs_cliente().toString());
		if (aEntityRequest.getStatus_ente() != null)
			request.addInputParam("@i_status_ente", ICTSTypes.SQLVARCHAR, aEntityRequest.getStatus_ente().toString());

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

	private EntityResponse transformToEntityResponse(IProcedureResponse aProcedureResponse, String SubTipo) {
		EntityResponse EntityResp = new EntityResponse();
		List<FullEntity> fullEntityCollection = new ArrayList<FullEntity>();
		FullEntity aFullEntity = null;
		if (logger.isDebugEnabled()) {
			logger.logDebug("ProcedureResponse: " + aProcedureResponse.getProcedureResponseAsString());
		}
		IResultSetRow[] rowsEntity = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

		if (SubTipo.equals("P")) {
			for (IResultSetRow iResultSetRow : rowsEntity) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				aFullEntity = new FullEntity();
				aFullEntity.setNumber(columns[COL_NUMBER_P].getValue());
				aFullEntity.setFirstLast(columns[COL_FIRST_LAST_P].getValue());
				aFullEntity.setSecondName(columns[COL_SECOND_NAME_P].getValue());
				aFullEntity.setMarriedSurname(columns[COL_MARRIED_SURNAME_P].getValue());
				aFullEntity.setFirstName(columns[COL_FIRST_NAME_P].getValue());
				aFullEntity.setMiddleName(columns[COL_MIDDLE_NAME_P].getValue());
				aFullEntity.setId(columns[COL_ID_P].getValue());
				aFullEntity.setTypeId(columns[COL_TYPE_ID_P].getValue());
				aFullEntity.setOfficial(columns[COL_OFFICIAL_P].getValue());
				aFullEntity.setOfficialName(columns[COL_OFFICIAL_NAME_P].getValue());
				aFullEntity.setLocked(columns[COL_LOCKED_P].getValue());
				aFullEntity.setStatus(columns[COL_STATUS_P].getValue());
				aFullEntity.setCustomer(columns[COL_CUSTOMER_P].getValue());
				aFullEntity.setDescriptionStatus(columns[COL_DESCRIPTION_STATUS_P].getValue());
				fullEntityCollection.add(aFullEntity);
			}
		}

		if (SubTipo.equals("C")) {
			for (IResultSetRow iResultSetRow : rowsEntity) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				aFullEntity = new FullEntity();
				aFullEntity.setNumber(columns[COL_NUMBER_C].getValue());
				aFullEntity.setCompanyName(columns[COL_COMPANY_NAME_C].getValue());
				aFullEntity.setMarriedSurname(columns[COL_MARRIED_SURNAME_C].getValue());
				aFullEntity.setFirstName(columns[COL_FIRST_NAME_C].getValue());
				aFullEntity.setMiddleName(columns[COL_MIDDLE_NAME_C].getValue());
				aFullEntity.setBusinessName(columns[COL_BUSINESS_NAME_C].getValue());
				aFullEntity.setId(columns[COL_ID_C].getValue());
				aFullEntity.setTypeId(columns[COL_TYPE_ID_C].getValue());
				aFullEntity.setOfficial(columns[COL_OFFICIAL_C].getValue());
				aFullEntity.setOfficialName(columns[COL_OFFICIAL_NAME_C].getValue());
				aFullEntity.setLocked(columns[COL_LOCKED_C].getValue());
				aFullEntity.setCustomer(columns[COL_STATUS_C].getValue());
				aFullEntity.setStatus(columns[COL_CUSTOMER_C].getValue());
				aFullEntity.setDescriptionStatus(columns[COL_DESCRIPTION_STATUS_C].getValue());
				fullEntityCollection.add(aFullEntity);
			}
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

	@Override
	public EntityResponse GetEntityId(EntityRequest entityRequest)
			throws CTSServiceException, CTSInfrastructureException {

		IProcedureResponse pResponse = Execution(SP_NAME, entityRequest);
		EntityResponse aEntityResponse = transformToEntityResponse(pResponse);
		return aEntityResponse;
	}

	private IProcedureResponse Execution(String SpName, EntityRequest entityRequest)
			throws CTSServiceException, CTSInfrastructureException {

		IProcedureRequest request = initProcedureRequest(entityRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800269");
		request.setSpName(SpName);
		request.addInputParam("@i_operacion", ICTSTypes.SYBCHAR, "V");
		request.addInputParam("@i_valor_cedula", ICTSTypes.SYBVARCHAR, entityRequest.getCed_ruc());
		request.addOutputParam("@o_ente_mis", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_nombre", ICTSTypes.SYBVARCHAR,
				"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Request:*** " + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Start Request *** ");
		}

		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response *** ");
		}
		return pResponse;
	}

	@SuppressWarnings("null")
	private EntityResponse transformToEntityResponse(IProcedureResponse aProcedureResponse) {
		EntityResponse aEntityResponse = new EntityResponse();
		Entity aEntity = new Entity();

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: *** ==> " + aProcedureResponse.getProcedureResponseAsString());
		}

		if (aProcedureResponse.getReturnCode() == 0) {
			if (!aProcedureResponse.readValueParam("@o_ente_mis").equals(null)) {
				if (logger.isInfoEnabled())
					logger.logInfo("aProcedureResponse.readValueParam(@o_ente_mis) ==> "
							+ aProcedureResponse.readValueParam("@o_ente_mis"));
				aEntity.setEnte(Integer.parseInt(aProcedureResponse.readValueParam("@o_ente_mis").toString()));
			}
			if (!aProcedureResponse.readValueParam("@o_nombre").equals(null)) {
				if (logger.isInfoEnabled())
					logger.logInfo("aProcedureResponse.readValueParam(@o_nombre) ==> "
							+ aProcedureResponse.readValueParam("@o_nombre"));
				aEntity.setName(aProcedureResponse.readValueParam("@o_nombre"));
			}
			aEntityResponse.setEntity(aEntity);
		} else {
			aEntityResponse.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		}

		aEntityResponse.setReturnCode(aProcedureResponse.getReturnCode());
		return aEntityResponse;
	}
}
