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
import com.cobiscorp.ecobis.ib.application.dtos.CheckbookRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CheckbookResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Checkbook;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCheckbook;

@Component(name = "CheckbookQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "CheckbookQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CheckbookQueryOrchestrationCore") })

public class CheckbookQueryOrchestrationCore extends QueryBaseTemplate {

	@Reference(referenceInterface = ICoreServiceCheckbook.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceCheckbook coreService;
	ILogger logger = LogFactory.getLogger(CheckbookQueryOrchestrationCore.class);

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
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {

		String messageError = null;
		String messageLog = null;
		String queryName = null;

		CheckbookResponse aCheckbookResponse = null;
		CheckbookRequest aCheckbookRequest = transformCheckbookRequest(request.clone());

		try {

			messageError = "getCheckbook: ERROR EXECUTING SERVICE";
			messageLog = "getCheckbook " + aCheckbookRequest.getProductNumber().getProductNumber();
			queryName = "getCheckbook";

			aCheckbookRequest.setOriginalRequest(request);
			aCheckbookResponse = coreService.getCheckbook(aCheckbookRequest);

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

		aBagSPJavaOrchestration.put(LOG_MESSAGE, messageLog);
		aBagSPJavaOrchestration.put(QUERY_NAME, queryName);
		return transformProcedureResponse(aCheckbookResponse);
		// return null;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// Valida Inyección de dependencias

		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		Utils.validateComponentInstance(mapInterfaces);

		try {
			aBagSPJavaOrchestration.put(LOG_MESSAGE, "CONSULTA DE CHEQUERAS ");
			executeStepsQueryBase(anOrginalRequest, aBagSPJavaOrchestration);

			return processResponse(anOrginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {

			return Utils.returnExceptionService(anOrginalRequest, e);
		}

	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = (IProcedureResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION");
		return response;

	}

	/******************
	 * Transformación de ProcedureRequest a CheckbookRequest
	 ********************/

	private CheckbookRequest transformCheckbookRequest(IProcedureRequest aRequest) {
		CheckbookRequest CheckbookReq = new CheckbookRequest();
		Product product = new Product();
		Currency currency = new Currency();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;

		messageError = aRequest.readValueParam("@i_cta") == null ? " - @i_cta can't be null" : "";
		messageError += aRequest.readValueParam("@i_mon") == null ? " - @i_mon can't be null" : "";
		messageError += aRequest.readValueParam("@t_ejec") == null ? " - @t_ejec can't be null" : "";
		messageError += aRequest.readValueParam("@t_rty") == null ? " - @t_rty can't be null" : "";
		messageError += aRequest.readValueParam("@t_trn") == null ? " - @t_trn can't be null" : "";
		messageError += aRequest.readValueParam("@i_prod") == null ? " - @i_prod can't be null" : "";
		messageError += aRequest.readValueParam("@i_login") == null ? " - @i_login can't be null" : "";
		messageError += aRequest.readValueParam("@i_modo") == null ? " - @i_modo can't be null" : "";
		messageError += aRequest.readValueParam("@i_sec") == null ? " - @i_sec can't be null" : "";
		messageError += aRequest.readValueParam("@i_formato_fecha") == null ? " - @i_formato_fecha can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		product.setProductNumber(aRequest.readValueParam("@i_cta"));
		product.setProductType(Integer.parseInt(aRequest.readValueParam("@i_prod")));
		currency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));

		CheckbookReq.setEjec(aRequest.readValueParam("@t_ejec"));
		CheckbookReq.setRty(aRequest.readValueParam("@t_rty"));
		CheckbookReq.setCodeTransactionalIdentifier(aRequest.readValueParam("@t_trn"));
		CheckbookReq.setProductId(product);
		CheckbookReq.setCurrency(currency);
		CheckbookReq.setUserName(aRequest.readValueParam("@i_login"));
		CheckbookReq.setProductNumber(product);
		CheckbookReq.setMode(Integer.parseInt(aRequest.readValueParam("@i_modo")));
		CheckbookReq.setSequential(Integer.parseInt(aRequest.readValueParam("@i_sec")));
		CheckbookReq.setDateFormatId(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha")));

		return CheckbookReq;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformProcedureResponse(CheckbookResponse aCheckbookResponse) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		// Agregar Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("sequential", ICTSTypes.SQLINT4, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("initialCheck", ICTSTypes.SQLINT4, 30));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("numberOfChecks", ICTSTypes.SQLINT4, 34));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("type", ICTSTypes.SQLVARCHAR, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("creationDate", ICTSTypes.SQLVARCHAR, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("deliveryDate", ICTSTypes.SQLVARCHAR, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("status", ICTSTypes.SQLVARCHAR, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("printShippingDate", ICTSTypes.SQLVARCHAR, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("receiptPrintingDate", ICTSTypes.SQLVARCHAR, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("receiptOfficeDate", ICTSTypes.SQLVARCHAR, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("creationOffice", ICTSTypes.SQLVARCHAR, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("receptionOffice", ICTSTypes.SQLVARCHAR, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("runNumber", ICTSTypes.SQLINT4, 64));

		for (Checkbook aCheckbook : aCheckbookResponse.getCheckbooksCollection()) {

			if (!IsValidCheckbookResponse(aCheckbook))
				return null;

			IResultSetRow row = new ResultSetRow();

			row.addRowData(1, new ResultSetRowColumnData(false, aCheckbook.getSequential().toString())); // productNumber
			row.addRowData(2, new ResultSetRowColumnData(false, aCheckbook.getInitialCheck().toString()));
			row.addRowData(3, new ResultSetRowColumnData(false, aCheckbook.getNumberOfChecks().toString()));
			row.addRowData(4, new ResultSetRowColumnData(false, aCheckbook.getType().toString()));
			row.addRowData(5, new ResultSetRowColumnData(false, aCheckbook.getCreationDate()));
			row.addRowData(6, new ResultSetRowColumnData(false, aCheckbook.getDeliveryDate()));
			row.addRowData(7, new ResultSetRowColumnData(false, aCheckbook.getStatus()));
			row.addRowData(8, new ResultSetRowColumnData(false, aCheckbook.getPrintShippingDate()));
			row.addRowData(9, new ResultSetRowColumnData(false, aCheckbook.getReceiptPrintingDate()));
			row.addRowData(10, new ResultSetRowColumnData(false, aCheckbook.getReceiptOfficeDate()));
			row.addRowData(11, new ResultSetRowColumnData(false, aCheckbook.getCreationOffice()));
			row.addRowData(12, new ResultSetRowColumnData(false, aCheckbook.getReceptionOffice()));
			row.addRowData(13, new ResultSetRowColumnData(false, aCheckbook.getRunNumber().toString()));

			data.addRow(row);

		}

		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

		wProcedureResponse.addResponseBlock(resultBlock);

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;

	}

	private boolean IsValidCheckbookResponse(Checkbook aCheckbook) {
		String messageError = null;

		messageError = aCheckbook.getNumberOfChecks() == null ? " - NumberOfChecks can't be null" : "";
		messageError += aCheckbook.getInitialCheck() == null ? " - InitialCheck can't be null" : "";
		messageError += aCheckbook.getSequential() == null ? " - Sequential can't be null" : "";
		messageError += aCheckbook.getStatus() == null ? " - Status can't be null" : "";
		messageError += aCheckbook.getCreationDate() == null ? " - CreationDate can't be null" : "";
		messageError += aCheckbook.getType() == null ? " - Type can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		return true;
	}

}
