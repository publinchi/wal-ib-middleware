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
import com.cobiscorp.ecobis.ib.application.dtos.TypesOfCheckbookRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TypesOfCheckbookResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.TypesOfCheckbook;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCheckbook;

/**
 * @author jmoreta
 *
 */
@Component(name = "TypesOfCheckbookQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "TypesOfCheckbookQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TypesOfCheckbookQueryOrchestrationCore") })

public class TypesOfCheckbookQueryOrchestrationCore extends SPJavaOrchestrationBase {

	@Reference(referenceInterface = ICoreServiceCheckbook.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceCheckbook coreService;
	ILogger logger = LogFactory.getLogger(TypesOfCheckbookQueryOrchestrationCore.class);

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

		TypesOfCheckbookResponse aTypesOfCheckbookResponse = null;
		TypesOfCheckbookRequest aTypesOfCheckbookRequest = transformTypesOfCheckbookRequest(request.clone());
		if (logger.isDebugEnabled())
			logger.logDebug("OBJ: " + aTypesOfCheckbookRequest.getCurrency().getCurrencyId().toString());

		try {
			if (logger.isDebugEnabled())
				logger.logDebug("executeQuery");
			messageError = "get: ERROR EXECUTING SERVICE";
			if (logger.isDebugEnabled())
				logger.logDebug("request TypesOfCheckbook: " + request);
			messageLog = "getTypesOfCheckbook: " + aTypesOfCheckbookRequest.getCurrency().getCurrencyId().toString();
			queryName = "getTypesOfCheckbook";
			aTypesOfCheckbookRequest.setOriginalRequest(request);
			aTypesOfCheckbookResponse = coreService.getTypesOfCheckbook(aTypesOfCheckbookRequest);
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

		return transformProcedureResponse(aTypesOfCheckbookResponse);

	}

	/******************
	 * Transformación de ProcedureRequest a TypesOfCheckbook
	 ********************/
	private TypesOfCheckbookRequest transformTypesOfCheckbookRequest(IProcedureRequest aRequest) {

		TypesOfCheckbookRequest typesOfCheckbookRequest = new TypesOfCheckbookRequest();
		Currency currency = new Currency();

		if (logger.isDebugEnabled())
			logger.logDebug(
					"Procedure Request to transformTypesOfCheckbook->" + aRequest.getProcedureRequestAsString());

		String messageError = null;

		messageError = aRequest.readValueParam("@i_moneda") == null ? " - @i_moneda can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		currency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_moneda")));

		typesOfCheckbookRequest.setCurrency(currency);
		typesOfCheckbookRequest.setOperation(aRequest.readValueParam("@i_operacion"));

		return typesOfCheckbookRequest;
	}

	/*********************
	 * Transformación de Response a TypesOfCheckbookResponse
	 ***********************/
	private IProcedureResponse transformProcedureResponse(TypesOfCheckbookResponse aTypesOfCheckbookResponse) {

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		// Agregar Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("idType", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("name", ICTSTypes.SYBVARCHAR, 500));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("type", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("art", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("customArt", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("quantity", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("state", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("time", ICTSTypes.SQLINT4, 4));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("currencyId", ICTSTypes.SQLINT4, 4));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("amount", ICTSTypes.SYBVARCHAR, 10));

		for (TypesOfCheckbook typesOfCheckbook : aTypesOfCheckbookResponse.getTypesOfCheckbook()) {

			if (!IsValidTypesOfCheckbookResponse(typesOfCheckbook))
				return null;

			IResultSetRow row = new ResultSetRow();
			row.addRowData(1, new ResultSetRowColumnData(false, typesOfCheckbook.getType().getIdType()));
			row.addRowData(2, new ResultSetRowColumnData(false, typesOfCheckbook.getName().getName()));
			row.addRowData(3, new ResultSetRowColumnData(false, typesOfCheckbook.getType().getType()));
			row.addRowData(4, new ResultSetRowColumnData(false, typesOfCheckbook.getArt()));
			row.addRowData(5, new ResultSetRowColumnData(false, typesOfCheckbook.getCustomArt()));
			row.addRowData(6, new ResultSetRowColumnData(false, typesOfCheckbook.getQuantity()));
			row.addRowData(7, new ResultSetRowColumnData(false, typesOfCheckbook.getState().getStatus()));
			row.addRowData(8, new ResultSetRowColumnData(false, typesOfCheckbook.getTime().toString()));
			row.addRowData(9,
					new ResultSetRowColumnData(false, typesOfCheckbook.getCurrency().getCurrencyId().toString()));
			row.addRowData(10, new ResultSetRowColumnData(false, typesOfCheckbook.getAmount()));

			data.addRow(row);
		}

		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

		wProcedureResponse.addResponseBlock(resultBlock);

		// AGREGAR LA DEVOLUCION DE LA COLECCION DE MENSAJES QUE DEVUELVE LA
		// IMPLEMENTACION, PARA MAPEAR EL MENSAJE DE ERROR DEVUELTO
		if (aTypesOfCheckbookResponse.getReturnCode() != 0) {
			wProcedureResponse = Utils.returnException(aTypesOfCheckbookResponse.getMessages());
		}

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure TypesOfCheckbookResponse Final --> "
					+ wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;
	}

	private boolean IsValidTypesOfCheckbookResponse(TypesOfCheckbook typesOfCheckbook) {
		String messageError = null;
		String msgErr = null;

		messageError = typesOfCheckbook.getType().getIdType() == null ? " Type can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = typesOfCheckbook.getName().getName() == null ? " Name can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = typesOfCheckbook.getType().getType() == null ? " Type can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = typesOfCheckbook.getArt() == null ? " Art can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = typesOfCheckbook.getCustomArt() == null ? " CustomArt can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = typesOfCheckbook.getQuantity() == null ? " Quantity can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = typesOfCheckbook.getState().getStatus() == null ? " State can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = typesOfCheckbook.getTime() == null ? " Time can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = typesOfCheckbook.getCurrency().getCurrencyId() == null ? " Currency can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = typesOfCheckbook.getAmount() == null ? " Amount can't be null," : "OK";
		msgErr = msgErr + messageError;

		if (!messageError.equals("OK"))
			throw new IllegalArgumentException(msgErr);
		return true;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration TypesOfCheckbook");
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		Utils.validateComponentInstance(mapInterfaces);
		return executeBaseJavaOrchestration(anOrginalRequest, aBagSPJavaOrchestration);
	}

}
