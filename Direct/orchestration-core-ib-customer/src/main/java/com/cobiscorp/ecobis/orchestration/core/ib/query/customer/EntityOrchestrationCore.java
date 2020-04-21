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
import com.cobiscorp.ecobis.ib.application.dtos.EntityRequest;
import com.cobiscorp.ecobis.ib.application.dtos.EntityResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.FullEntity;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceEntity;

/**
 * 
 * @author mvelez
 * @since Nov 16, 2014
 * @version 1.0.0
 */

@Component(name = "EntityOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "EntityOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "EntityOrchestrationCore") })

public class EntityOrchestrationCore extends QueryBaseTemplate {
	/**
	 * Instance plugin to use services other core banking
	 */

	private static final String PERSON = "P";
	private static final String COMPANY = "C";

	@Reference(referenceInterface = ICoreServiceEntity.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceEntity coreService;

	ILogger logger = this.getLogger();

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreServiceEntity service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreServiceEntity service) {
		coreService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		String messageLog = null;
		String queryName = null;

		// Valida Inyección de dependencias
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		Utils.validateComponentInstance(mapInterfaces);

		try {

			EntityResponse wEntityResponse = null;
			EntityRequest aEntityRequest = transformEntityRequest(anOrginalRequest.clone());
			if (logger.isDebugEnabled()) {
				logger.logDebug(anOrginalRequest.readValueParam("@t_trn"));
				logger.logDebug(anOrginalRequest.readValueParam("@i_tipo"));
				logger.logDebug("<<<---------------->>>");
				logger.logDebug("<<<Exec GetEntity>>>");
			}
			messageLog = "<<<@i_tipo>>> " + anOrginalRequest.readValueParam("@i_tipo");
			queryName = "GetEntity";
			wEntityResponse = coreService.GetEntity(aEntityRequest);

			aBagSPJavaOrchestration.put(LOG_MESSAGE, messageLog);
			aBagSPJavaOrchestration.put(QUERY_NAME, queryName);
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, wEntityResponse);
			return processResponse(anOrginalRequest, aBagSPJavaOrchestration);

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
		String PersonType = null;
		PersonType = anOriginalProcedureReq
				.readValueParam("@i_subtipo"); /* P, C */

		IProcedureResponse response = transformProcedureResponse(
				(EntityResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION"), PersonType);
		aBagSPJavaOrchestration.remove(RESPONSE_TRANSACTION);
		return response;

	}

	/*******************************************************************************/
	/*
	 * Transform a Procedure Request in AccountStatementRequest
	 */
	private EntityRequest transformEntityRequest(IProcedureRequest aRequest) {
		EntityRequest entityReq = new EntityRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("<<<Procedure Request to Transform->>>" + aRequest.getProcedureRequestAsString());

		if (aRequest.readValueParam("@t_trn") != null)
			entityReq.setTrn(Integer.parseInt(aRequest.readValueParam("@t_trn")));
		if (aRequest.readValueParam("@i_subtipo") != null)
			entityReq.setSubtipo(aRequest.readValueParam("@i_subtipo"));
		if (aRequest.readValueParam("@i_tipo") != null)
			entityReq.setTipo(Integer.parseInt(aRequest.readValueParam("@i_tipo")));
		if (aRequest.readValueParam("@i_modo") != null)
			entityReq.setModo(Integer.parseInt(aRequest.readValueParam("@i_modo")));
		if (aRequest.readValueParam("@i_valor") != null)
			entityReq.setValor(aRequest.readValueParam("@i_valor"));
		if (aRequest.readValueParam("@i_ente") != null)
			entityReq.setEnte(Integer.parseInt(aRequest.readValueParam("@i_ente")));
		if (aRequest.readValueParam("@i_nombre") != null)
			entityReq.setNombre(aRequest.readValueParam("@i_nombre"));
		if (aRequest.readValueParam("@i_s_nombre") != null)
			entityReq.setS_nombre(aRequest.readValueParam("@i_s_nombre"));
		if (aRequest.readValueParam("@i_p_apellido") != null)
			entityReq.setP_apellido(aRequest.readValueParam("@i_p_apellido"));
		if (aRequest.readValueParam("@i_s_apellido") != null)
			entityReq.setS_apellido(aRequest.readValueParam("@i_s_apellido"));
		if (aRequest.readValueParam("@i_c_apellido") != null)
			entityReq.setC_apellido(aRequest.readValueParam("@i_c_apellido"));
		if (aRequest.readValueParam("@i_ced_ruc") != null)
			entityReq.setCed_ruc(aRequest.readValueParam("@i_ced_ruc"));
		if (aRequest.readValueParam("@i_oficina") != null)
			entityReq.setOficina(Integer.parseInt(aRequest.readValueParam("@i_oficina")));
		if (aRequest.readValueParam("@i_nombre_completo") != null)
			entityReq.setNombre_completo(aRequest.readValueParam("@i_nombre_completo"));
		if (aRequest.readValueParam("@i_pasaporte") != null)
			entityReq.setPasaporte(aRequest.readValueParam("@i_pasaporte"));
		if (aRequest.readValueParam("@i_es_cliente") != null)
			entityReq.setEs_cliente(aRequest.readValueParam("@i_es_cliente"));
		if (aRequest.readValueParam("@i_status_ente") != null)
			entityReq.setStatus_ente(aRequest.readValueParam("@i_status_ente"));

		return entityReq;
	}

	/*******************************************************************************/
	private IProcedureResponse transformProcedureResponse(EntityResponse entityResponse, String personType) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("<<<METHOD: transformProcedureResponse>>>");

		// Add Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		if (personType.equals(PERSON)) {
			metaData.addColumnMetaData(new ResultSetHeaderColumn("No.", ICTSTypes.SQLINT2, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Primer Apellido", ICTSTypes.SQLVARCHAR, 50));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Segundo Apellido", ICTSTypes.SQLVARCHAR, 50));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Apellido de Casada", ICTSTypes.SQLVARCHAR, 50));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Primer Nombre", ICTSTypes.SQLVARCHAR, 50));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Segundo Nombre", ICTSTypes.SQLVARCHAR, 50));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("I.D.", ICTSTypes.SQLVARCHAR, 25));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Tipo I.D.", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Oficial", ICTSTypes.SQLINT2, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Nombre del Oficial", ICTSTypes.SQLVARCHAR, 50));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Bloqueado", ICTSTypes.SQLVARCHAR, 1));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Estado", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Es cliente", ICTSTypes.SQLVARCHAR, 1));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Desc. de Estado", ICTSTypes.SQLVARCHAR, 50));

			for (FullEntity aFullEntity : entityResponse.getEntityCollection()) {
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, aFullEntity.getNumber()));
				row.addRowData(2, new ResultSetRowColumnData(true, aFullEntity.getFirstLast()));
				row.addRowData(3, new ResultSetRowColumnData(true, aFullEntity.getSecondName()));
				row.addRowData(4, new ResultSetRowColumnData(true, aFullEntity.getMarriedSurname()));
				row.addRowData(5, new ResultSetRowColumnData(true, aFullEntity.getFirstName()));
				row.addRowData(6, new ResultSetRowColumnData(true, aFullEntity.getMiddleName()));
				row.addRowData(7, new ResultSetRowColumnData(true, aFullEntity.getId()));
				row.addRowData(8, new ResultSetRowColumnData(true, aFullEntity.getTypeId()));
				row.addRowData(9, new ResultSetRowColumnData(true, aFullEntity.getOfficial()));
				row.addRowData(10, new ResultSetRowColumnData(true, aFullEntity.getOfficialName()));
				row.addRowData(11, new ResultSetRowColumnData(true, aFullEntity.getLocked()));
				row.addRowData(12, new ResultSetRowColumnData(true, aFullEntity.getStatus()));
				row.addRowData(13, new ResultSetRowColumnData(true, aFullEntity.getCustomer()));
				row.addRowData(14, new ResultSetRowColumnData(true, aFullEntity.getDescriptionStatus()));
				data.addRow(row);
			} // for
		}
		if (personType.equals(COMPANY)) {
			metaData.addColumnMetaData(new ResultSetHeaderColumn("No.", ICTSTypes.SQLINT2, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Razón Social", ICTSTypes.SQLVARCHAR, 50));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Apellido de Casada", ICTSTypes.SQLVARCHAR, 50));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Primer Nombre", ICTSTypes.SQLVARCHAR, 50));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Segundo Nombre", ICTSTypes.SQLVARCHAR, 50));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Nombre Comercial", ICTSTypes.SQLVARCHAR, 50));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("No.I.D.", ICTSTypes.SQLVARCHAR, 25));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Tipo I.D.", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Oficial", ICTSTypes.SQLINT2, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Nombre del Oficial", ICTSTypes.SQLVARCHAR, 50));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Bloqueado", ICTSTypes.SQLVARCHAR, 1));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Es cliente", ICTSTypes.SQLVARCHAR, 1));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Estado", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Desc. de Estado", ICTSTypes.SQLVARCHAR, 50));

			for (FullEntity aFullEntity : entityResponse.getEntityCollection()) {
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, aFullEntity.getNumber()));
				row.addRowData(2, new ResultSetRowColumnData(true, aFullEntity.getCompanyName()));
				row.addRowData(3, new ResultSetRowColumnData(true, aFullEntity.getMarriedSurname()));
				row.addRowData(4, new ResultSetRowColumnData(true, aFullEntity.getFirstName()));
				row.addRowData(5, new ResultSetRowColumnData(true, aFullEntity.getMiddleName()));
				row.addRowData(6, new ResultSetRowColumnData(true, aFullEntity.getBusinessName()));
				row.addRowData(7, new ResultSetRowColumnData(true, aFullEntity.getId()));
				row.addRowData(8, new ResultSetRowColumnData(true, aFullEntity.getTypeId()));
				row.addRowData(9, new ResultSetRowColumnData(true, aFullEntity.getOfficial()));
				row.addRowData(10, new ResultSetRowColumnData(true, aFullEntity.getOfficialName()));
				row.addRowData(11, new ResultSetRowColumnData(true, aFullEntity.getLocked()));
				row.addRowData(12, new ResultSetRowColumnData(true, aFullEntity.getCustomer()));
				row.addRowData(13, new ResultSetRowColumnData(true, aFullEntity.getStatus()));
				row.addRowData(14, new ResultSetRowColumnData(true, aFullEntity.getDescriptionStatus()));
				data.addRow(row);
			} // for

		}

		IResultSetBlock resultBlock1 = new ResultSetBlock(metaData, data);

		wProcedureResponse.addResponseBlock(resultBlock1);

		if (logger.isDebugEnabled())
			logger.logDebug("<<<ORCHESTRATION: Response Final >>>" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}

	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		return null;
	}
}
