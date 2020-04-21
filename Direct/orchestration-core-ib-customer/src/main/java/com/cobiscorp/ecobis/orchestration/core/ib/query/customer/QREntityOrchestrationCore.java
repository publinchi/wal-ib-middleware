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
import com.cobiscorp.ecobis.ib.application.dtos.EntityRequest;
import com.cobiscorp.ecobis.ib.application.dtos.QREntityResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.QREntity;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceQREntity;

/**
 * @author mvelez
 *
 */
@Component(name = "QREntityOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "QREntityOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "QREntityOrchestrationCore") })

public class QREntityOrchestrationCore extends SPJavaOrchestrationBase {
	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceQREntity.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceQREntity coreService;

	ILogger logger = this.getLogger();

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreServiceQREntity service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreServiceQREntity service) {
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

			QREntityResponse wEntityResponse = null;
			EntityRequest wEntityRequest = transformEntityRequest(anOrginalRequest.clone());

			messageLog = "GetQREntity " + wEntityRequest.getEnte().toString();
			wEntityRequest.setOriginalRequest(anOrginalRequest);

			if (logger.isDebugEnabled())
				logger.logDebug(messageLog);

			wEntityResponse = coreService.GetQREntity(wEntityRequest);

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
	 * Transform a Procedure Request in EntityRequest
	 */
	private EntityRequest transformEntityRequest(IProcedureRequest aRequest) {
		EntityRequest entityReq = new EntityRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("<<<Procedure Request to Transform->>>" + aRequest.getProcedureRequestAsString());

		String messageError = null;

		messageError = aRequest.readValueParam("@i_ente") == null ? " - @i_ente can't be null" : "";
		messageError = aRequest.readValueParam("@t_trn") == null ? " - @t_trn can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		entityReq.setEnte(Integer.parseInt(aRequest.readValueParam("@i_ente")));
		entityReq.setTrn(Integer.parseInt(aRequest.readValueParam("@t_trn")));

		return entityReq;
	}

	/*******************************************************************************/
	private IProcedureResponse transformProcedureResponse(QREntityResponse entityResponse) {
		// if (!IsValidLoanAmortizationResponse(loanAmortizationResponse))
		// return null;

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("<<<METHOD: transformProcedureResponse>>>");

		// Add Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 50)); /* nombre_completo */
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR,
				50)); /* apellido casada */
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 2)); /* subtipo */
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR,
				50)); /* ced ruc */
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 2)); /* retencion */
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR, 2)); /* mala_referencia */
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR,
				50)); /* nombre largo */
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR,
				50)); /* nombre corto */
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR,
				50)); /* razon social */
		metaData.addColumnMetaData(new ResultSetHeaderColumn("", ICTSTypes.SQLVARCHAR,
				50)); /* grupo econ */
		for (QREntity aQREntity : entityResponse.getQREntityCollection()) {
			// if (!IsValidAccountStatementResponse(aFullEntity)) return null;
			IResultSetRow row = new ResultSetRow();

			row.addRowData(1, new ResultSetRowColumnData(false, aQREntity.getNombre_completo()));
			row.addRowData(2, new ResultSetRowColumnData(false, aQREntity.getApellido_casada()));
			row.addRowData(3, new ResultSetRowColumnData(true, aQREntity.getSubtype()));
			row.addRowData(4, new ResultSetRowColumnData(true, aQREntity.getCed_ruc()));
			row.addRowData(5, new ResultSetRowColumnData(true, aQREntity.getRetencion()));
			row.addRowData(6, new ResultSetRowColumnData(true, aQREntity.getMala_referencia()));
			row.addRowData(7, new ResultSetRowColumnData(true, aQREntity.getNombre_largo()));
			row.addRowData(8, new ResultSetRowColumnData(true, aQREntity.getNombre_corto()));
			row.addRowData(9, new ResultSetRowColumnData(true, aQREntity.getRazon_social()));
			row.addRowData(10, new ResultSetRowColumnData(true, aQREntity.getGrupo_econ()));

			data.addRow(row);
		} // for

		IResultSetBlock resultBlock1 = new ResultSetBlock(metaData, data);

		wProcedureResponse.addResponseBlock(resultBlock1);

		if (logger.isDebugEnabled())
			logger.logDebug("<<<ORCHESTRATION: Response Final >>>" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}
}
