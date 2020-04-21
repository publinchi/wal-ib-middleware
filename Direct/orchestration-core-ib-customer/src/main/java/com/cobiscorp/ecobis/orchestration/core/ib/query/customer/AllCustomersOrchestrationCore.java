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
import com.cobiscorp.cobis.commons.log.LogFactory;
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
import com.cobiscorp.ecobis.ib.orchestration.dtos.FullEntity;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceAllCustomers;

/**
 * 
 * 
 * @author gyagual
 *
 */
@Component(name = "AllCustomersOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "AllCustomersOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "AllCustomersOrchestrationCore") })
public class AllCustomersOrchestrationCore extends SPJavaOrchestrationBase {

	ILogger logger = LogFactory.getLogger(AllCustomersOrchestrationCore.class);

	@Reference(referenceInterface = ICoreServiceAllCustomers.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceAllCustomers coreService;

	protected void bindCoreService(ICoreServiceAllCustomers service) {
		coreService = service;
	}

	protected void unbindCoreService(ICoreServiceAllCustomers service) {
		coreService = null;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {
		return null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		if (logger.isInfoEnabled())
			logger.logInfo(
					"Component-Name: " + this.getClass().getSimpleName() + ", Component-Version: $$project.version$$");
	}

	private IProcedureResponse transformProcedureResponse(EntityResponse wEntityResponse, String wSubType) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		// Agregar Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("No.", ICTSTypes.SQLVARCHAR, 1));
		if (wSubType.equals("P")) {
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Primer Apellido", ICTSTypes.SQLVARCHAR, 200));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Segundo Apellido", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Apellido de Casada", ICTSTypes.SQLVARCHAR, 254));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Primer Nombre", ICTSTypes.SQLVARCHAR, 100));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Segundo Nombre", ICTSTypes.SQLVARCHAR, 1));
		} else {
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Razon Social", ICTSTypes.SQLVARCHAR, 200));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Apellido de Casada", ICTSTypes.SQLVARCHAR, 200));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Primer Nombre", ICTSTypes.SQLVARCHAR, 200));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Segundo Nombre", ICTSTypes.SQLVARCHAR, 200));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Nombre Comercial", ICTSTypes.SQLVARCHAR, 200));
		}

		metaData.addColumnMetaData(new ResultSetHeaderColumn("I.D.", ICTSTypes.SQLVARCHAR, 200));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("Tipo I.D.", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("Oficial", ICTSTypes.SQLVARCHAR, 254));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("Nombre del Oficial", ICTSTypes.SQLVARCHAR, 100));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("Estado", ICTSTypes.SQLVARCHAR, 100));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("Desc. de Estado", ICTSTypes.SQLVARCHAR, 100));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("Tipo Persona", ICTSTypes.SQLVARCHAR, 100));

		for (FullEntity wFullEntity : wEntityResponse.getEntityCollection()) {

			if (!IsValidEntityResponse(wFullEntity, wSubType))
				return null;

			IResultSetRow row = new ResultSetRow();
			row.addRowData(1, new ResultSetRowColumnData(false, wFullEntity.getNumber()));
			if (wSubType.equals("P")) {
				row.addRowData(2, new ResultSetRowColumnData(false, wFullEntity.getFirstLast()));
				row.addRowData(3, new ResultSetRowColumnData(false, wFullEntity.getSecondName()));
				row.addRowData(4, new ResultSetRowColumnData(false, wFullEntity.getMarriedSurname()));
				row.addRowData(5, new ResultSetRowColumnData(false, wFullEntity.getFirstName()));
				row.addRowData(6, new ResultSetRowColumnData(false, wFullEntity.getMiddleName()));
			} else {
				row.addRowData(2, new ResultSetRowColumnData(false, wFullEntity.getFirstLast()));
				row.addRowData(3, new ResultSetRowColumnData(false, wFullEntity.getSecondName()));
				row.addRowData(4, new ResultSetRowColumnData(false, wFullEntity.getMarriedSurname()));
				row.addRowData(5, new ResultSetRowColumnData(false, wFullEntity.getFirstName()));
				row.addRowData(6, new ResultSetRowColumnData(false, wFullEntity.getMiddleName()));
			}

			row.addRowData(7, new ResultSetRowColumnData(false, wFullEntity.getId()));
			row.addRowData(8, new ResultSetRowColumnData(false, wFullEntity.getTypeId()));
			row.addRowData(9, new ResultSetRowColumnData(false, wFullEntity.getOfficial()));
			row.addRowData(10, new ResultSetRowColumnData(false, wFullEntity.getOfficialName()));
			row.addRowData(11, new ResultSetRowColumnData(false, wFullEntity.getStatus()));
			row.addRowData(12, new ResultSetRowColumnData(false, wFullEntity.getDescriptionStatus()));
			row.addRowData(13, new ResultSetRowColumnData(false, wFullEntity.getPersonType()));
			data.addRow(row);
		}
		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

		wProcedureResponse.addResponseBlock(resultBlock);

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// Valida Inyección de dependencias
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		Utils.validateComponentInstance(mapInterfaces);

		try {

			String messageLog = null;
			String wSubType = null;

			EntityResponse wEntityResponse = null;
			EntityRequest wEntityRequest = transformEntityRequest(anOrginalRequest.clone());

			messageLog = "getAllCustomers" + wEntityRequest.getSubtipo();
			wSubType = wEntityRequest.getSubtipo();
			wEntityRequest.setOriginalRequest(anOrginalRequest);

			if (logger.isDebugEnabled())
				logger.logDebug(messageLog);

			wEntityResponse = coreService.getAllCustomers(wEntityRequest);

			return transformProcedureResponse(wEntityResponse, wSubType);

		} catch (CTSServiceException e) {
			e.printStackTrace();
			if (logger.isInfoEnabled())
				logger.logInfo("** CTSServiceException " + e.getMessage(), e);
			return Utils.returnExceptionService(anOrginalRequest, e);

		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
			if (logger.isInfoEnabled())
				logger.logInfo("** CTSInfrastructureException " + e.getMessage(), e);
			return Utils.returnExceptionService(anOrginalRequest, e);
		}
	}

	private EntityRequest transformEntityRequest(IProcedureRequest aRequest) {
		EntityRequest wEntityRequest = new EntityRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;

		messageError = aRequest.readValueParam("@i_subtipo") == null ? " - @i_subtipo can't be null" : "";
		messageError = aRequest.readValueParam("@i_tipo") == null ? " - @i_tipo can't be null" : "";
		messageError = aRequest.readValueParam("@i_modo") == null ? " - @i_modo can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);
		if (aRequest.readValueParam("@i_subtipo") != null)
			wEntityRequest.setSubtipo(aRequest.readValueParam("@i_subtipo"));
		if (aRequest.readValueParam("@i_tipo") != null)
			wEntityRequest.setTipo(new Integer(aRequest.readValueParam("@i_tipo")));
		if (aRequest.readValueParam("@i_modo") != null)
			wEntityRequest.setModo(new Integer(aRequest.readValueParam("@i_modo")));
		if (aRequest.readValueParam("@i_valor") != null)
			wEntityRequest.setValor(aRequest.readValueParam("@i_valor"));
		if (aRequest.readValueParam("@i_ente") != null)
			wEntityRequest.setEnte(new Integer(aRequest.readValueParam("@i_ente")));
		if (aRequest.readValueParam("@i_nombre") != null)
			wEntityRequest.setNombre(aRequest.readValueParam("@i_nombre"));
		if (aRequest.readValueParam("@i_p_apellido") != null)
			wEntityRequest.setP_apellido(aRequest.readValueParam("@i_p_apellido"));
		if (aRequest.readValueParam("@i_s_apellido") != null)
			wEntityRequest.setS_apellido(aRequest.readValueParam("@i_s_apellido"));
		if (aRequest.readValueParam("@i_c_apellido") != null)
			wEntityRequest.setC_apellido(aRequest.readValueParam("@i_c_apellido"));
		if (aRequest.readValueParam("@i_ced_ruc") != null)
			wEntityRequest.setCed_ruc(aRequest.readValueParam("@i_ced_ruc"));
		if (aRequest.readValueParam("@i_nombre_completo") != null)
			wEntityRequest.setNombre_completo(aRequest.readValueParam("@i_nombre_completo"));
		if (aRequest.readValueParam("@i_pasaporte") != null)
			wEntityRequest.setPasaporte(aRequest.readValueParam("@i_pasaporte"));
		if (aRequest.readValueParam("@i_departamento") != null)
			wEntityRequest.setDepartamento(aRequest.readValueParam("@i_departamento"));

		return wEntityRequest;
	}

	private boolean IsValidEntityResponse(FullEntity wFullEntity, String wSubType) {
		String messageError = null;

		messageError = wFullEntity.getId() == null ? " - No. can't be null" : "";
		messageError += wFullEntity.getTypeId() == null ? " - TypeId can't be null" : "";
		messageError += wFullEntity.getOfficial() == null ? " - Official can't be null" : "";
		messageError += wFullEntity.getOfficialName() == null ? " - Official Name can't be null" : "";
		messageError += wFullEntity.getStatus() == null ? " - Status can't be null" : "";
		messageError += wFullEntity.getDescriptionStatus() == null ? " - DescriptionStatus can't be null" : "";
		messageError += wFullEntity.getPersonType() == null ? " - Person Type can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		return true;
	}
}
