/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.query.customer;

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
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.EntityIntegratedResponse;
import com.cobiscorp.ecobis.ib.application.dtos.EntityRequest;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.EntityIntegrated;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceEntityIntegrated;

/**
 * @author mvelez
 *
 */
@Component(name = "EntityIntegratedOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "EntityIntegratedOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "EntityIntegratedOrchestrationCore") })

public class EntityIntegratedOrchestrationCore extends SPJavaOrchestrationBase {
	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceEntityIntegrated.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceEntityIntegrated coreService;
	ILogger logger = this.getLogger();

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreServiceEntityIntegrated service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreServiceEntityIntegrated service) {
		coreService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		Utils.validateComponentInstance(mapInterfaces);

		try {

			String messageLog = null;

			EntityIntegratedResponse wEntityResponse = null;
			EntityRequest wEntityRequest = transformEntityRequest(anOrginalRequest.clone());

			messageLog = "GetEntityIntegrated " + wEntityRequest.getEnte().toString();
			wEntityRequest.setOriginalRequest(anOrginalRequest);

			if (logger.isDebugEnabled())
				logger.logDebug(messageLog);

			wEntityResponse = coreService.GetEntityIntegrated(wEntityRequest);

			return transformProcedureResponse(wEntityResponse);

		} catch (CTSServiceException e) {
			e.printStackTrace();
			return Utils.returnExceptionService(anOrginalRequest, e);

		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
			return Utils.returnExceptionService(anOrginalRequest, e);
		}
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		return null;

	}

	/*******************************************************************************/
	/*
	 * Transform a Procedure Request in transformEntityRequest
	 */
	private EntityRequest transformEntityRequest(IProcedureRequest aRequest) {
		EntityRequest entityReq = new EntityRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("<<<Procedure Request to Transform->>>" + aRequest.getProcedureRequestAsString());

		String messageError = null;

		messageError = aRequest.readValueParam("@i_ente") == null ? " - @i_ente can't be null" : "";
		messageError = aRequest.readValueParam("@i_formato") == null ? " - @i_formato can't be null" : "";
		messageError = aRequest.readValueParam("@t_trn") == null ? " - @t_trn can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		entityReq.setTrn(Integer.parseInt(aRequest.readValueParam("@t_trn")));
		entityReq.setEnte(Integer.parseInt(aRequest.readValueParam("@i_ente")));
		entityReq.setFormato_fecha(aRequest.readValueParam("@i_formato"));

		return entityReq;
	}

	/*******************************************************************************/
	private IProcedureResponse transformProcedureResponse(EntityIntegratedResponse entityResponse) {
		// if (!IsValidLoanAmortizationResponse(loanAmortizationResponse))
		// return null;

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("<<<METHOD: transformProcedureResponse>>>");

		// Add Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLINT2, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 50));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 50));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 50));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLINT2, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 50));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLINT2, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLINT2, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLINT2, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLINT2, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 100));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLINT2, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLINT2, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 50));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLINT2, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 30));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 30));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLINT2, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 30));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 100));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 150));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLINT2, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 100));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 100));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 100));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 100));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 50));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 100));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLINT2, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 50));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLMONEY4, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLMONEY4, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 50));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLMONEY4, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLMONEY4, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLINT2, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLINT2, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLINT2, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLINT2, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLINT2, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLMONEY4, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLINT2, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLMONEY4, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLMONEY4, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLINT2, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLINT2, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLINT2, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLINT2, 0));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 100));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 20));

		for (EntityIntegrated aEntityIntegrated : entityResponse.getEntityIntegratedCollection()) {
			// if (!IsValidAccountStatementResponse(aFullEntity)) return null;
			IResultSetRow row = new ResultSetRow();
			row.addRowData(1, new ResultSetRowColumnData(false, aEntityIntegrated.getEnte().toString()));
			row.addRowData(2, new ResultSetRowColumnData(true, aEntityIntegrated.getNombre_completo_DE()));
			row.addRowData(3, new ResultSetRowColumnData(true, aEntityIntegrated.getSubtipo()));
			row.addRowData(4, new ResultSetRowColumnData(true, aEntityIntegrated.getCed_ruc()));
			row.addRowData(5, new ResultSetRowColumnData(true, aEntityIntegrated.getTipo_ced()));
			row.addRowData(6, new ResultSetRowColumnData(true, aEntityIntegrated.getFilial().toString()));
			row.addRowData(7, new ResultSetRowColumnData(true, aEntityIntegrated.getFilial_desc()));
			row.addRowData(8, new ResultSetRowColumnData(true, aEntityIntegrated.getOficina().toString()));
			row.addRowData(9, new ResultSetRowColumnData(true, aEntityIntegrated.getOficina_desc()));
			row.addRowData(10, new ResultSetRowColumnData(true, aEntityIntegrated.getFecha_crea()));
			row.addRowData(11, new ResultSetRowColumnData(true, aEntityIntegrated.getFecha_mod()));
			row.addRowData(12, new ResultSetRowColumnData(true, aEntityIntegrated.getDireccion().toString()));
			row.addRowData(13, new ResultSetRowColumnData(true, aEntityIntegrated.getReferencia().toString()));
			row.addRowData(14, new ResultSetRowColumnData(true, aEntityIntegrated.getCasilla().toString()));
			row.addRowData(15, new ResultSetRowColumnData(true, aEntityIntegrated.getCasilla_def()));
			row.addRowData(16, new ResultSetRowColumnData(true, aEntityIntegrated.getTipo_dp()));
			row.addRowData(17, new ResultSetRowColumnData(true, aEntityIntegrated.getBalance().toString()));
			row.addRowData(18, new ResultSetRowColumnData(true, aEntityIntegrated.getGrupo().toString()));
			row.addRowData(19, new ResultSetRowColumnData(true, aEntityIntegrated.getGrupo_desc()));
			row.addRowData(20, new ResultSetRowColumnData(true, aEntityIntegrated.getPais().toString()));
			row.addRowData(21, new ResultSetRowColumnData(true, aEntityIntegrated.getPais_desc()));
			row.addRowData(22, new ResultSetRowColumnData(true, aEntityIntegrated.getNacionalidad()));
			row.addRowData(23, new ResultSetRowColumnData(true, aEntityIntegrated.getOficial().toString()));
			row.addRowData(24, new ResultSetRowColumnData(true, aEntityIntegrated.getOficial_desc()));
			row.addRowData(25, new ResultSetRowColumnData(true, aEntityIntegrated.getActividad()));
			row.addRowData(26, new ResultSetRowColumnData(true, aEntityIntegrated.getActividad_desc()));
			row.addRowData(27, new ResultSetRowColumnData(true, aEntityIntegrated.getRetencion()));
			row.addRowData(28, new ResultSetRowColumnData(true, aEntityIntegrated.getMala_referencia()));
			row.addRowData(29, new ResultSetRowColumnData(true, aEntityIntegrated.getComentario()));
			row.addRowData(30, new ResultSetRowColumnData(true, aEntityIntegrated.getCont_malas().toString()));
			row.addRowData(31, new ResultSetRowColumnData(true, aEntityIntegrated.getNomlar()));
			row.addRowData(32, new ResultSetRowColumnData(true, aEntityIntegrated.getVinculacion()));
			row.addRowData(33, new ResultSetRowColumnData(true, aEntityIntegrated.getTipo_vinculacion()));
			row.addRowData(34, new ResultSetRowColumnData(true, aEntityIntegrated.getTipo_vinc_desc()));
			row.addRowData(35, new ResultSetRowColumnData(true, aEntityIntegrated.getPosicion()));
			row.addRowData(36, new ResultSetRowColumnData(true, aEntityIntegrated.getPosicion_desc()));
			row.addRowData(37, new ResultSetRowColumnData(true, aEntityIntegrated.getTipo_compania()));
			row.addRowData(38, new ResultSetRowColumnData(true, aEntityIntegrated.getTipo_compania_desc()));
			row.addRowData(39, new ResultSetRowColumnData(true, aEntityIntegrated.getRep_legal().toString()));
			row.addRowData(40, new ResultSetRowColumnData(true, aEntityIntegrated.getRep_legal_desc()));
			row.addRowData(41, new ResultSetRowColumnData(true, aEntityIntegrated.getActivo().toString()));
			row.addRowData(42, new ResultSetRowColumnData(true, aEntityIntegrated.getPasivo().toString()));
			row.addRowData(43, new ResultSetRowColumnData(true, aEntityIntegrated.getEs_grupo()));
			row.addRowData(44, new ResultSetRowColumnData(true, aEntityIntegrated.getCapital_social().toString()));
			row.addRowData(45, new ResultSetRowColumnData(true, aEntityIntegrated.getReserva_legal().toString()));
			row.addRowData(46, new ResultSetRowColumnData(true, aEntityIntegrated.getFecha_const()));
			row.addRowData(47, new ResultSetRowColumnData(true, aEntityIntegrated.getNombre_completo()));
			row.addRowData(48, new ResultSetRowColumnData(true, aEntityIntegrated.getPlazo().toString()));
			row.addRowData(49, new ResultSetRowColumnData(true, aEntityIntegrated.getDireccion_domicilio().toString()));
			row.addRowData(50, new ResultSetRowColumnData(true, aEntityIntegrated.getFecha_inscrp()));
			row.addRowData(51, new ResultSetRowColumnData(true, aEntityIntegrated.getFecha_aum_capital()));
			row.addRowData(52, new ResultSetRowColumnData(true, aEntityIntegrated.getRep_jud().toString()));
			row.addRowData(53, new ResultSetRowColumnData(true, aEntityIntegrated.getRep_jud_desc()));
			row.addRowData(54, new ResultSetRowColumnData(true, aEntityIntegrated.getRep_ex_jud().toString()));
			row.addRowData(55, new ResultSetRowColumnData(true, aEntityIntegrated.getRep_ex_jud_desc()));
			row.addRowData(56, new ResultSetRowColumnData(true, aEntityIntegrated.getNotaria().toString()));
			row.addRowData(57, new ResultSetRowColumnData(true, aEntityIntegrated.getCapital_inicial().toString()));
			row.addRowData(58, new ResultSetRowColumnData(true, aEntityIntegrated.getP_apellido()));
			row.addRowData(59, new ResultSetRowColumnData(true, aEntityIntegrated.getS_apellido()));
			row.addRowData(60, new ResultSetRowColumnData(true, aEntityIntegrated.getSexo()));
			row.addRowData(61, new ResultSetRowColumnData(true, aEntityIntegrated.getSexo_desc()));
			row.addRowData(62, new ResultSetRowColumnData(true, aEntityIntegrated.getFecha_nac()));
			row.addRowData(63, new ResultSetRowColumnData(true, aEntityIntegrated.getProfesion()));
			row.addRowData(64, new ResultSetRowColumnData(true, aEntityIntegrated.getProfesion_desc()));
			row.addRowData(65, new ResultSetRowColumnData(true, aEntityIntegrated.getPasaporte()));
			row.addRowData(66, new ResultSetRowColumnData(true, aEntityIntegrated.getEstado_civil()));
			row.addRowData(67, new ResultSetRowColumnData(true, aEntityIntegrated.getEstado_civil_desc()));
			row.addRowData(68, new ResultSetRowColumnData(true, aEntityIntegrated.getNum_cargas().toString()));
			row.addRowData(69, new ResultSetRowColumnData(true, aEntityIntegrated.getNivel_ing().toString()));
			row.addRowData(70, new ResultSetRowColumnData(true, aEntityIntegrated.getNivel_egr().toString()));
			row.addRowData(71, new ResultSetRowColumnData(true, aEntityIntegrated.getTipo_persona()));
			row.addRowData(72, new ResultSetRowColumnData(true, aEntityIntegrated.getTipo_persona_desc()));
			row.addRowData(73, new ResultSetRowColumnData(true, aEntityIntegrated.getPersonal().toString()));
			row.addRowData(74, new ResultSetRowColumnData(true, aEntityIntegrated.getPropiedad().toString()));
			row.addRowData(75, new ResultSetRowColumnData(true, aEntityIntegrated.getTrabajo().toString()));
			row.addRowData(76, new ResultSetRowColumnData(true, aEntityIntegrated.getSoc_hecho().toString()));
			row.addRowData(77, new ResultSetRowColumnData(true, aEntityIntegrated.getFecha_ingreso()));
			row.addRowData(78, new ResultSetRowColumnData(true, aEntityIntegrated.getFecha_expira()));
			row.addRowData(79, new ResultSetRowColumnData(true, aEntityIntegrated.getC_apellido()));
			row.addRowData(80, new ResultSetRowColumnData(true, aEntityIntegrated.getS_nombre()));
			row.addRowData(81, new ResultSetRowColumnData(true, aEntityIntegrated.getCodsuper()));
			row.addRowData(82, new ResultSetRowColumnData(true, aEntityIntegrated.getTipspub()));
			row.addRowData(83, new ResultSetRowColumnData(true, aEntityIntegrated.getSubspub()));
			row.addRowData(84, new ResultSetRowColumnData(true, aEntityIntegrated.getCodsuper_desc()));
			row.addRowData(85, new ResultSetRowColumnData(true, aEntityIntegrated.getTipspub_desc()));
			row.addRowData(86, new ResultSetRowColumnData(true, aEntityIntegrated.getSubspub_desc()));
			row.addRowData(87, new ResultSetRowColumnData(true, aEntityIntegrated.getRazon_social()));
			row.addRowData(88, new ResultSetRowColumnData(true, aEntityIntegrated.getNombre()));
			row.addRowData(89, new ResultSetRowColumnData(true, aEntityIntegrated.getEstado()));
			row.addRowData(90, new ResultSetRowColumnData(true, aEntityIntegrated.getC_actividad()));
			row.addRowData(91, new ResultSetRowColumnData(true, aEntityIntegrated.getC_actividad_desc()));
			row.addRowData(92, new ResultSetRowColumnData(true, aEntityIntegrated.getEstado_aux()));

			data.addRow(row);
		} // for

		IResultSetBlock resultBlock1 = new ResultSetBlock(metaData, data);

		wProcedureResponse.addResponseBlock(resultBlock1);

		if (logger.isDebugEnabled())
			logger.logDebug("<<<ORCHESTRATION: Response Final >>>" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}

}
