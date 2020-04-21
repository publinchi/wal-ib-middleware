package com.cobiscorp.ecobis.orchestration.core.ib.all;

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
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceAllCustomers;

@Component(name = "AllCustomersQuery", immediate = false)
@Service(value = { ICoreServiceAllCustomers.class })
@Properties(value = { @Property(name = "service.description", value = "ProspectiveCustomerQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ProspectiveCustomerQuery") })
public class AllCustomersQuery extends SPJavaOrchestrationBase implements ICoreServiceAllCustomers {
	private static final String COBIS_CONTEXT = "COBIS";
	private static ILogger logger = LogFactory.getLogger(AllCustomersQuery.class);

	@Override
	public EntityResponse getAllCustomers(EntityRequest aEntityRequest)
			throws CTSServiceException, CTSInfrastructureException {

		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: getAllCustomers");
			logger.logInfo("*RESPUESTA CORE COBIS GENERADA");
		}
		IProcedureRequest request = new ProcedureRequestAS();
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1241");

		request.setSpName("sp_se_ente_ofi");

		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, "1241");
		request.addInputParam("@i_tipo", ICTSTypes.SQLINT1, aEntityRequest.getTipo().toString());
		request.addInputParam("@i_subtipo", ICTSTypes.SQLVARCHAR, aEntityRequest.getSubtipo());
		request.addInputParam("@i_modo", ICTSTypes.SQLINT1, aEntityRequest.getModo().toString());
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
		if (aEntityRequest.getNombre_completo() != null)
			request.addInputParam("@i_nombre_completo", ICTSTypes.SQLVARCHAR, aEntityRequest.getNombre_completo());
		if (aEntityRequest.getPasaporte() != null)
			request.addInputParam("@i_pasaporte", ICTSTypes.SQLVARCHAR, aEntityRequest.getPasaporte());
		if (aEntityRequest.getDepartamento() != null)
			request.addInputParam("@i_departamento", ICTSTypes.SQLVARCHAR, aEntityRequest.getDepartamento());

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request: " + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Start Request");
		}
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

			aFullEntity.setNumber(columns[0].getValue());
			if (aSubtipo == "P") {
				aFullEntity.setFirstLast(columns[1].getValue());
				aFullEntity.setSecondName(columns[2].getValue());
				aFullEntity.setMarriedSurname(columns[3].getValue());
				aFullEntity.setFirstName(columns[4].getValue());
				aFullEntity.setMiddleName(columns[5].getValue());
				aFullEntity.setId(columns[6].getValue());
				aFullEntity.setTypeId(columns[7].getValue());
				aFullEntity.setOfficial(columns[8].getValue());
				aFullEntity.setOfficialName(columns[9].getValue());
				aFullEntity.setStatus(columns[10].getValue());
				aFullEntity.setDescriptionStatus(columns[11].getValue());
				aFullEntity.setPersonType(columns[12].getValue());
			} else {
				aFullEntity.setCompanyName(columns[1].getValue());
				aFullEntity.setBusinessName(columns[2].getValue());
				aFullEntity.setId(columns[3].getValue());
				aFullEntity.setTypeId(columns[4].getValue());
				aFullEntity.setOfficial(columns[5].getValue());
				aFullEntity.setOfficialName(columns[6].getValue());
				aFullEntity.setStatus(columns[7].getValue());
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
