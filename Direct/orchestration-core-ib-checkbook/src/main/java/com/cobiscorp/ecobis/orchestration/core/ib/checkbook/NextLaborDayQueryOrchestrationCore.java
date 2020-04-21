package com.cobiscorp.ecobis.orchestration.core.ib.checkbook;

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
import com.cobiscorp.ecobis.ib.application.dtos.NextLaborDayRequest;
import com.cobiscorp.ecobis.ib.application.dtos.NextLaborDayResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.LaborDay;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Office;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCheckbook;

/**
 * @author jmoreta
 *
 */
@Component(name = "NextLaborDayQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "NextLaborDayQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "NextLaborDayQueryOrchestrationCore") })

public class NextLaborDayQueryOrchestrationCore extends SPJavaOrchestrationBase {

	@Reference(referenceInterface = ICoreServiceCheckbook.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceCheckbook coreService;
	ILogger logger = LogFactory.getLogger(NextLaborDayQueryOrchestrationCore.class);

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreServiceCheckbook service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreServiceCheckbook service) {
		coreService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		String messageError = null;
		String messageLog = null;
		String queryName = null;

		NextLaborDayResponse aNextLaborDayResponse = null;
		NextLaborDayRequest aNextLaborDayRequest = transformNextLaborDayRequest(request.clone());
		if (logger.isDebugEnabled())
			logger.logDebug("OBJ: " + aNextLaborDayRequest.getLaborDay().getDay());

		try {
			if (logger.isDebugEnabled())
				logger.logDebug("executeQuery");
			messageError = "get: ERROR EXECUTING SERVICE";
			if (logger.isDebugEnabled())
				logger.logDebug("request NextLaborDay: " + request);
			messageLog = "getNextLaborDay: " + aNextLaborDayRequest.getLaborDay().getDay();
			queryName = "getNextLaborDay";
			aNextLaborDayRequest.setOriginalRequest(request);
			aNextLaborDayResponse = coreService.getNextLaborDay(aNextLaborDayRequest);
		} catch (CTSServiceException e) {
			e.printStackTrace();
			if (logger.isDebugEnabled())
				logger.logDebug(messageError);
			return null;
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
			if (logger.isDebugEnabled())
				logger.logDebug(messageError);
			return null;
		}

		aBagSPJavaOrchestration.put(BEGIN_OPERATION, messageLog);
		aBagSPJavaOrchestration.put(BRANCH_NAME, queryName);// .put(METHOD_TCP,
															// queryName);

		return transformProcedureResponse(aNextLaborDayResponse);
	}

	/******************
	 * Transformación de ProcedureRequest a NextLaborDayRequest
	 ********************/

	private NextLaborDayRequest transformNextLaborDayRequest(IProcedureRequest aRequest) {

		NextLaborDayRequest nextLaborDayRequest = new NextLaborDayRequest();
		LaborDay laborDay = new LaborDay();
		Office office = new Office();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;

		messageError = aRequest.readValueParam("@i_fecha") == null ? " - @i_fecha can't be null" : "";
		/*
		 * messageError += aRequest.readValueParam("@i_dias") == null ?
		 * " - @i_dias can't be null":""; messageError +=
		 * aRequest.readValueParam("@i_oficina") == null ?
		 * " - @i_oficina can't be null":""; messageError +=
		 * aRequest.readValueParam("@i_comercial") == null ?
		 * " - @i_comercial can't be null":"";
		 */
		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		laborDay.setDate(aRequest.readValueParam("@i_fecha"));
		laborDay.setDay(Integer.parseInt(aRequest.readValueParam("@i_dias")));
		office.setId(Integer.parseInt(aRequest.readValueParam("@i_oficina")));

		nextLaborDayRequest.setCommercial(aRequest.readValueParam("@i_comercial"));
		nextLaborDayRequest.setLaborDay(laborDay);
		nextLaborDayRequest.setOfficeId(office);

		return nextLaborDayRequest;
	}

	/*********************
	 * Transformación de Response a NextLaborDayResponse
	 ***********************/
	private IProcedureResponse transformProcedureResponse(NextLaborDayResponse aNextLaborDayResponse) {

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		wProcedureResponse.addParam("@o_dias", ICTSTypes.SQLINT2, 1, aNextLaborDayResponse.getDays().toString());

		// Agregar Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("date", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("day", ICTSTypes.SQLINT4, 4));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("month", ICTSTypes.SQLINT4, 4));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("year", ICTSTypes.SQLINT4, 4));

		for (LaborDay laborDay : aNextLaborDayResponse.getNextLaborDay()) {

			if (!IsValidTimeDepositsPayableInterestsResponse(laborDay))
				return null;

			IResultSetRow row = new ResultSetRow();
			row.addRowData(1, new ResultSetRowColumnData(false, laborDay.getDate()));
			row.addRowData(2, new ResultSetRowColumnData(false, laborDay.getDay().toString()));
			row.addRowData(3, new ResultSetRowColumnData(false, laborDay.getMonth().toString()));
			row.addRowData(4, new ResultSetRowColumnData(false, laborDay.getYear().toString()));

			data.addRow(row);
		}

		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

		wProcedureResponse.addResponseBlock(resultBlock);

		// AGREGAR LA DEVOLUCION DE LA COLECCION DE MENSAJES QUE DEVUELVE LA
		// IMPLEMENTACION, PARA MAPEAR EL MENSAJE DE ERROR DEVUELTO
		if (aNextLaborDayResponse.getReturnCode() != 0) {
			wProcedureResponse = Utils.returnException(aNextLaborDayResponse.getMessages());
		}

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final --> " + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}

	private boolean IsValidTimeDepositsPayableInterestsResponse(LaborDay laborDay) {
		String messageError = null;
		String msgErr = null;

		messageError = laborDay.getDate() == null ? " Date can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = laborDay.getDay() == null ? " Day can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = laborDay.getMonth() == null ? " Month can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = laborDay.getYear() == null ? " Year can't be null," : "OK";
		msgErr = msgErr + messageError;

		if (!messageError.equals("OK"))
			throw new IllegalArgumentException(msgErr);
		return true;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration NextLaborDay");
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		Utils.validateComponentInstance(mapInterfaces);
		return executeBaseJavaOrchestration(anOrginalRequest, aBagSPJavaOrchestration);
	}

}
