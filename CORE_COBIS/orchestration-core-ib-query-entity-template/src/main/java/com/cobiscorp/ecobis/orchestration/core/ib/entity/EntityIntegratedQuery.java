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
import com.cobiscorp.ecobis.ib.application.dtos.EntityIntegratedResponse;
import com.cobiscorp.ecobis.ib.application.dtos.EntityRequest;
import com.cobiscorp.ecobis.ib.orchestration.dtos.EntityIntegrated;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceEntityIntegrated;

@Component(name = "EntityIntegratedQuery", immediate = false)
@Service(value = { ICoreServiceEntityIntegrated.class })
@Properties(value = { @Property(name = "service.description", value = "EntityIntegratedQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "EntityIntegratedQuery") })

public class EntityIntegratedQuery extends SPJavaOrchestrationBase implements ICoreServiceEntityIntegrated {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.commons.components.ICOBISComponent#loadConfiguration(
	 * com.cobiscorp.cobis.commons.configuration.IConfigurationReader)
	 */
	private static final String COBIS_CONTEXT = "COBIS";

	/**
	 * Instance logger component
	 */
	private static ILogger logger = LogFactory.getLogger(EntityIntegratedQuery.class);

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public EntityIntegratedResponse GetEntityIntegrated(EntityRequest aEntityRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: GetEntityIntegrated");
			logger.logInfo("RESPUESTA CORE COBIS GENERADA");
		}
		IProcedureRequest request = new ProcedureRequestAS();

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");

		request.setSpName("cobis..sp_query_ente_int");
		request.addInputParam("@t_trn", ICTSTypes.SQLINT1, "1386");
		if (aEntityRequest.getEnte() != null)
			request.addInputParam("@i_ente", ICTSTypes.SQLINT4, aEntityRequest.getEnte().toString());
		if (aEntityRequest.getFormato_fecha() != null)
			request.addInputParam("@i_formato", ICTSTypes.SQLVARCHAR, aEntityRequest.getFormato_fecha().toString());

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

		EntityIntegratedResponse entityResponse = transformToEntityResponse(pResponse);
		return entityResponse;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	private EntityIntegratedResponse transformToEntityResponse(IProcedureResponse aProcedureResponse) {
		EntityIntegratedResponse EntityResp = new EntityIntegratedResponse();
		List<EntityIntegrated> EntityIntegratedCollection = new ArrayList<EntityIntegrated>();
		EntityIntegrated aEntityIntegrated = null;
		if (logger.isDebugEnabled()) {
			logger.logDebug("ProcedureResponse: " + aProcedureResponse.getProcedureResponseAsString());
		}
		IResultSetRow[] rowsEntity = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

		for (IResultSetRow iResultSetRow : rowsEntity) {
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
			aEntityIntegrated = new EntityIntegrated();

			aEntityIntegrated.setEnte(Integer.parseInt((columns[0].getValue())));
			aEntityIntegrated.setNombre_completo_DE((columns[1].getValue()));
			aEntityIntegrated.setSubtipo((columns[2].getValue()));
			aEntityIntegrated.setCed_ruc((columns[3].getValue()));
			aEntityIntegrated.setTipo_ced((columns[4].getValue()));
			aEntityIntegrated.setFilial(Integer.parseInt((columns[5].getValue())));
			aEntityIntegrated.setFilial_desc((columns[6].getValue()));
			aEntityIntegrated.setOficina(Integer.parseInt((columns[7].getValue())));
			aEntityIntegrated.setOficina_desc((columns[8].getValue()));

			aEntityIntegrated.setFecha_crea(columns[9].getValue() == null ? "" : columns[9].getValue());
			aEntityIntegrated.setFecha_mod(columns[10].getValue() == null ? "" : columns[10].getValue());
			aEntityIntegrated
					.setDireccion(columns[11].getValue() == null ? 0 : Integer.parseInt((columns[11].getValue())));
			aEntityIntegrated
					.setReferencia(columns[12].getValue() == null ? 0 : Integer.parseInt((columns[12].getValue())));
			aEntityIntegrated
					.setCasilla(columns[13].getValue() == null ? 0 : Integer.parseInt((columns[13].getValue())));
			aEntityIntegrated.setCasilla_def(columns[14].getValue() == null ? "" : (columns[14].getValue()));
			aEntityIntegrated.setTipo_dp(columns[15].getValue() == null ? "" : (columns[15].getValue()));
			aEntityIntegrated
					.setBalance(columns[16].getValue() == null ? 0 : (Integer.parseInt((columns[16].getValue()))));
			aEntityIntegrated
					.setGrupo(columns[17].getValue() == null ? 0 : (Integer.parseInt((columns[17].getValue()))));
			aEntityIntegrated.setGrupo_desc(columns[18].getValue() == null ? "" : (columns[18].getValue()));
			aEntityIntegrated
					.setPais(columns[19].getValue() == null ? 0 : (Integer.parseInt((columns[19].getValue()))));
			aEntityIntegrated.setPais_desc(columns[20].getValue() == null ? "" : (columns[20].getValue()));
			aEntityIntegrated.setNacionalidad(columns[21].getValue() == null ? "" : (columns[21].getValue()));
			aEntityIntegrated
					.setOficial(columns[22].getValue() == null ? 0 : (Integer.parseInt((columns[22].getValue()))));
			aEntityIntegrated.setOficial_desc(columns[23].getValue() == null ? "" : (columns[23].getValue()));
			aEntityIntegrated.setActividad(columns[24].getValue() == null ? "" : (columns[24].getValue()));
			aEntityIntegrated.setActividad_desc(columns[25].getValue() == null ? "" : (columns[25].getValue()));
			aEntityIntegrated.setRetencion(columns[26].getValue() == null ? "" : (columns[26].getValue()));
			aEntityIntegrated.setMala_referencia(columns[27].getValue() == null ? "" : (columns[27].getValue()));
			aEntityIntegrated.setComentario(columns[28].getValue() == null ? "" : (columns[28].getValue()));
			aEntityIntegrated
					.setCont_malas(columns[29].getValue() == null ? 0 : (Integer.parseInt((columns[29].getValue()))));
			aEntityIntegrated.setNomlar(columns[30].getValue() == null ? "" : (columns[30].getValue()));
			aEntityIntegrated.setVinculacion(columns[31].getValue() == null ? "" : (columns[31].getValue()));
			aEntityIntegrated.setTipo_vinculacion(columns[32].getValue() == null ? "" : (columns[32].getValue()));
			aEntityIntegrated.setTipo_vinc_desc(columns[33].getValue() == null ? "" : (columns[33].getValue()));
			aEntityIntegrated.setPosicion(columns[34].getValue() == null ? "" : (columns[34].getValue()));
			aEntityIntegrated.setPosicion_desc(columns[35].getValue() == null ? "" : (columns[35].getValue()));
			aEntityIntegrated.setTipo_compania(columns[36].getValue() == null ? "" : (columns[36].getValue()));
			aEntityIntegrated.setTipo_compania_desc(columns[37].getValue() == null ? "" : (columns[37].getValue()));
			aEntityIntegrated
					.setRep_legal(columns[38].getValue() == null ? 0 : (Integer.parseInt((columns[38].getValue()))));
			aEntityIntegrated.setRep_legal_desc(columns[39].getValue() == null ? "" : (columns[39].getValue()));
			aEntityIntegrated
					.setActivo(columns[40].getValue() == null ? 0.0 : (Double.parseDouble((columns[40].getValue()))));
			aEntityIntegrated
					.setPasivo(columns[41].getValue() == null ? 0.0 : (Double.parseDouble((columns[41].getValue()))));
			aEntityIntegrated.setEs_grupo(columns[42].getValue() == null ? "" : (columns[42].getValue()));
			aEntityIntegrated.setCapital_social(
					columns[43].getValue() == null ? 0.0 : (Double.parseDouble((columns[43].getValue()))));
			aEntityIntegrated.setReserva_legal(
					columns[44].getValue() == null ? 0.0 : (Double.parseDouble((columns[44].getValue()))));
			aEntityIntegrated.setFecha_const(columns[45].getValue() == null ? "" : (columns[45].getValue()));
			aEntityIntegrated.setNombre_completo(columns[46].getValue() == null ? "" : (columns[46].getValue()));
			aEntityIntegrated
					.setPlazo(columns[47].getValue() == null ? 0 : (Integer.parseInt((columns[47].getValue()))));
			aEntityIntegrated.setDireccion_domicilio(
					columns[48].getValue() == null ? 0 : (Integer.parseInt((columns[48].getValue()))));
			aEntityIntegrated.setFecha_inscrp(columns[49].getValue() == null ? "" : (columns[49].getValue()));
			aEntityIntegrated.setFecha_aum_capital(columns[50].getValue() == null ? "" : (columns[50].getValue()));
			aEntityIntegrated
					.setRep_jud(columns[51].getValue() == null ? 0 : (Integer.parseInt((columns[51].getValue()))));
			aEntityIntegrated.setRep_jud_desc(columns[52].getValue() == null ? "" : (columns[52].getValue()));
			aEntityIntegrated
					.setRep_ex_jud(columns[53].getValue() == null ? 0 : (Integer.parseInt((columns[53].getValue()))));
			aEntityIntegrated.setRep_ex_jud_desc(columns[54].getValue() == null ? "" : (columns[54].getValue()));
			aEntityIntegrated
					.setNotaria(columns[55].getValue() == null ? 0 : (Integer.parseInt((columns[55].getValue()))));
			aEntityIntegrated.setCapital_inicial(
					columns[56].getValue() == null ? 0.0 : (Double.parseDouble((columns[56].getValue()))));
			aEntityIntegrated.setP_apellido(columns[57].getValue() == null ? "" : (columns[57].getValue()));
			aEntityIntegrated.setS_apellido(columns[58].getValue() == null ? "" : (columns[58].getValue()));
			aEntityIntegrated.setSexo(columns[59].getValue() == null ? "" : (columns[59].getValue()));
			aEntityIntegrated.setSexo_desc(columns[60].getValue() == null ? "" : (columns[60].getValue()));
			aEntityIntegrated.setFecha_nac(columns[61].getValue() == null ? "" : (columns[61].getValue()));
			aEntityIntegrated.setProfesion(columns[62].getValue() == null ? "" : (columns[62].getValue()));
			aEntityIntegrated.setProfesion_desc(columns[63].getValue() == null ? "" : (columns[63].getValue()));
			aEntityIntegrated.setPasaporte(columns[64].getValue() == null ? "" : (columns[64].getValue()));
			aEntityIntegrated.setEstado_civil(columns[65].getValue() == null ? "" : (columns[65].getValue()));
			aEntityIntegrated.setEstado_civil_desc(columns[66].getValue() == null ? "" : (columns[66].getValue()));
			aEntityIntegrated
					.setNum_cargas(columns[67].getValue() == null ? 0 : (Integer.parseInt((columns[67].getValue()))));
			aEntityIntegrated.setNivel_ing(
					columns[68].getValue() == null ? 0.0 : (Double.parseDouble((columns[68].getValue()))));
			aEntityIntegrated.setNivel_egr(
					columns[69].getValue() == null ? 0.0 : (Double.parseDouble((columns[69].getValue()))));
			aEntityIntegrated.setTipo_persona(columns[70].getValue() == null ? "" : (columns[70].getValue()));
			aEntityIntegrated.setTipo_persona_desc(columns[71].getValue() == null ? "" : (columns[71].getValue()));
			aEntityIntegrated
					.setPersonal(columns[72].getValue() == null ? 0 : (Integer.parseInt((columns[72].getValue()))));
			aEntityIntegrated
					.setPropiedad(columns[73].getValue() == null ? 0 : (Integer.parseInt((columns[73].getValue()))));
			aEntityIntegrated
					.setTrabajo(columns[74].getValue() == null ? 0 : (Integer.parseInt((columns[74].getValue()))));
			aEntityIntegrated
					.setSoc_hecho(columns[75].getValue() == null ? 0 : (Integer.parseInt((columns[75].getValue()))));
			aEntityIntegrated.setFecha_ingreso(columns[76].getValue() == null ? "" : (columns[76].getValue()));
			aEntityIntegrated.setFecha_expira(columns[77].getValue() == null ? "" : (columns[77].getValue()));
			aEntityIntegrated.setC_apellido(columns[78].getValue() == null ? "" : (columns[78].getValue()));
			aEntityIntegrated.setS_nombre(columns[79].getValue() == null ? "" : (columns[79].getValue()));
			aEntityIntegrated.setCodsuper(columns[80].getValue() == null ? "" : (columns[80].getValue()));
			aEntityIntegrated.setTipspub(columns[81].getValue() == null ? "" : (columns[81].getValue()));
			aEntityIntegrated.setSubspub(columns[82].getValue() == null ? "" : (columns[82].getValue()));
			aEntityIntegrated.setCodsuper_desc(columns[83].getValue() == null ? "" : (columns[83].getValue()));
			aEntityIntegrated.setTipspub_desc(columns[84].getValue() == null ? "" : (columns[84].getValue()));
			aEntityIntegrated.setSubspub_desc(columns[85].getValue() == null ? "" : (columns[85].getValue()));
			aEntityIntegrated.setRazon_social(columns[86].getValue() == null ? "" : (columns[86].getValue()));
			aEntityIntegrated.setNombre(columns[87].getValue() == null ? "" : (columns[87].getValue()));
			aEntityIntegrated.setEstado(columns[88].getValue() == null ? "" : (columns[88].getValue()));
			aEntityIntegrated.setC_actividad(columns[89].getValue() == null ? "" : (columns[89].getValue()));
			aEntityIntegrated.setC_actividad_desc(columns[90].getValue() == null ? "" : (columns[90].getValue()));
			aEntityIntegrated.setEstado_aux(columns[91].getValue() == null ? "" : (columns[91].getValue()));
			EntityIntegratedCollection.add(aEntityIntegrated);
		}
		EntityResp.setEntityIntegratedCollection(EntityIntegratedCollection);
		return EntityResp;
	}
}
