package com.cobiscorp.ecobis.orchestration.core.ib.query.account.items;

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
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.ContractRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ContractResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ModuleRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ModuleResponse;
import com.cobiscorp.ecobis.ib.application.dtos.QueryAccountItemsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.QueryAccountItemsResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Contract;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Module;
import com.cobiscorp.ecobis.ib.orchestration.dtos.QueryAccountItem;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceContract;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.IWSModules;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.IWSQueryAccountItem;
import com.cobiscorp.ecobis.orchestration.ws.base.SintesisBaseTemplate;

/**
 * Account Items
 * 
 * @since Jul 24, 2015
 * @author dmorla
 * @version 1.0.0
 * 
 */
@Component(name = "AccountItemsOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "AccountItemsOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "AccountItemsOrchestrationCore") })
public class AccountItemsOrchestrationCore extends SintesisBaseTemplate {

	private static ILogger logger = LogFactory.getLogger(AccountItemsOrchestrationCore.class);
	private static final String CLASS_NAME = "AccountItemsOrchestrationCore--->";
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	private java.util.Properties properties;

	/**
	 * Read configuration of parent component
	 */

	public void loadConfiguration(IConfigurationReader arg0) {
		super.loadConfiguration(arg0);
		this.properties = arg0.getProperties("//property");
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = IWSQueryAccountItem.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceAccountItem", unbind = "unbindCoreServiceModules")
	protected IWSQueryAccountItem coreServiceAccountItem;

	/**
	 * Instance Service Interface
	 * 
	 * @param service
	 */
	public void bindCoreServiceAccountItem(IWSQueryAccountItem service) {
		coreServiceAccountItem = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */

	public void unbindCoreServiceModules(IWSQueryAccountItem service) {
		coreServiceAccountItem = null;
	}

	/**
	 * /** Execute transfer first step of service
	 * <p>
	 * This method is the main executor of transactional contains the original
	 * input parameters.
	 * 
	 * @param anOriginalRequest
	 *            - Information original sended by user's.
	 * @param aBagSPJavaOrchestration
	 *            - Object dictionary transactional steps.
	 * 
	 * @return
	 *         <ul>
	 *         <li>IProcedureResponse - Represents the service execution.</li>
	 *         </ul>
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeJavaOrchestration");
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		try {
			Map<String, Object> mapInterfaces = new HashMap<String, Object>();
			mapInterfaces.put("coreServiceAccountItem", coreServiceAccountItem);

			Utils.validateComponentInstance(mapInterfaces);

			if (anOriginalRequest == null)
				if (logger.isInfoEnabled())
					logger.logInfo(CLASS_NAME + "Original Request ISNULL");

			executeSteps(aBagSPJavaOrchestration);

			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.logInfo("*********  Error en " + e.getMessage(), e);
			}

			IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
			ErrorBlock eb = new ErrorBlock(-1, "Service is not available");
			wProcedureRespFinal.addResponseBlock(eb);
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
			wProcedureRespFinal.setReturnCode(-1);
			return wProcedureRespFinal;
		}
	}

	@Override
	public IProcedureResponse executeWSMethod(Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		IProcedureRequest anOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		IProcedureResponse responseProc = null;
		try {

			// consulta de items
			responseProc = queryProviderTransaction(anOriginalRequest, aBagSPJavaOrchestration);

			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseProc);

			if (Utils.flowError("updateProviderTransaction", responseProc)) {

				return responseProc;
			}

			return responseProc;
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.logInfo("*********  Error en " + e.getMessage(), e);
			}

			IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
			ErrorBlock eb = new ErrorBlock(-1, "Service is not available");
			wProcedureRespFinal.addResponseBlock(eb);
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
			wProcedureRespFinal.setReturnCode(-1);
			return wProcedureRespFinal;
		}
	}

	/**
	 * name executeProviderTransaction
	 * 
	 * @param anOriginalRequest
	 * @param aBag
	 * @return IProcedureResponse
	 */
	private IProcedureResponse queryProviderTransaction(IProcedureRequest anOriginalRequest, Map<String, Object> aBag) {

		QueryAccountItemsResponse wQueryAccountItemsResp = new QueryAccountItemsResponse();
		QueryAccountItemsRequest wQueryAccountItemsRequest = transformModuleRequest(anOriginalRequest.clone());

		try {
			wQueryAccountItemsRequest.setOriginalRequest(anOriginalRequest);
			wQueryAccountItemsResp = coreServiceAccountItem.getAccountItems(wQueryAccountItemsRequest, aBag,
					properties);
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}

		return transformModuleResponse(wQueryAccountItemsResp, aBag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase
	 * #processResponse(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> aBagSPJavaOrchestration) {

		return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
	}

	/******************
	 * Transformación de ProcedureRequest a StockRequest
	 ********************/

	private QueryAccountItemsRequest transformModuleRequest(IProcedureRequest aRequest) {
		QueryAccountItemsRequest wQueryAccountItemsRequest = new QueryAccountItemsRequest();
		Module wModule = new Module();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());
		if (aRequest.readValueParam("@i_id_operativo") != null)
			wQueryAccountItemsRequest.setIdOperativo(aRequest.readValueParam("@i_id_operativo"));
		if (aRequest.readValueParam("@i_codmodulo") != null) {
			wModule.setCodModule(Integer.parseInt(aRequest.readValueParam("@i_codmodulo")));
			wQueryAccountItemsRequest.setModule(wModule);
		}
		if (aRequest.readValueParam("@i_nrooperacion") != null)
			wQueryAccountItemsRequest.setOperation(Integer.parseInt(aRequest.readValueParam("@i_nrooperacion")));
		if (aRequest.readValueParam("@i_fechaoperativa") != null)
			wQueryAccountItemsRequest.setDate(Integer.parseInt(aRequest.readValueParam("@i_fechaoperativa")));
		if (aRequest.readValueParam("@i_cuenta") != null)
			wQueryAccountItemsRequest.setAccount(aRequest.readValueParam("@i_cuenta"));
		if (aRequest.readValueParam("@i_servicio") != null)
			wQueryAccountItemsRequest.setService(Integer.parseInt(aRequest.readValueParam("@i_servicio")));
		return wQueryAccountItemsRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformModuleResponse(QueryAccountItemsResponse aQueryAccountItemsResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		if (aQueryAccountItemsResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aQueryAccountItemsResponse.getMessages()));
			wProcedureResponse.addMessage(aQueryAccountItemsResponse.getReturnCode(),
					aQueryAccountItemsResponse.getMessageError());

		} else {

			// Agregar Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();
			IResultSetRow row = new ResultSetRow();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("DEPENDE_ITEM", ICTSTypes.SQLVARCHAR, 5));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("DESCRIPCION", ICTSTypes.SQLVARCHAR, 60));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("FORMA_PAGO", ICTSTypes.SQLVARCHAR, 3));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("MONEDA", ICTSTypes.SQLVARCHAR, 30));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("MONTO", ICTSTypes.SQLMONEY, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("ITEM", ICTSTypes.SQLVARCHAR, 10));

			for (QueryAccountItem aQueryAccountItem : aQueryAccountItemsResponse.getItemsCollection()) {

				row = new ResultSetRow();

				row.addRowData(1, new ResultSetRowColumnData(false, aQueryAccountItem.getDependency()));
				row.addRowData(2, new ResultSetRowColumnData(false, aQueryAccountItem.getItemDescription()));
				row.addRowData(3, new ResultSetRowColumnData(false, aQueryAccountItem.getPaymentMethod()));
				row.addRowData(4, new ResultSetRowColumnData(false, aQueryAccountItem.getCurrency()));
				row.addRowData(5, new ResultSetRowColumnData(false, aQueryAccountItem.getAmount().toString()));
				row.addRowData(6, new ResultSetRowColumnData(false, aQueryAccountItem.getItemPending()));

				data.addRow(row);

			}

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock);
			wProcedureResponse.addParam("@o_coderror", ICTSTypes.SQLINT4,
					aQueryAccountItemsResponse.getCodError().toString().length(),
					aQueryAccountItemsResponse.getCodError().toString());
			wProcedureResponse.addParam("@o_mensaje", ICTSTypes.SQLVARCHAR,
					aQueryAccountItemsResponse.getMessageError().length(),
					aQueryAccountItemsResponse.getMessageError());
			if (aQueryAccountItemsResponse.getCodError() == 0) {
				wProcedureResponse.addParam("@o_nitfac", ICTSTypes.SQLVARCHAR,
						aQueryAccountItemsResponse.getNitFac().length(), aQueryAccountItemsResponse.getNitFac());
				wProcedureResponse.addParam("@o_nombrefac", ICTSTypes.SQLVARCHAR,
						aQueryAccountItemsResponse.getNameFac().length(), aQueryAccountItemsResponse.getNameFac());
				wProcedureResponse.addParam("@o_cambiarnitynombrefac", ICTSTypes.SQLVARCHAR,
						aQueryAccountItemsResponse.getChangeNitFac().length(),
						aQueryAccountItemsResponse.getChangeNitFac());
				wProcedureResponse.addParam("@o_tienerequisito", ICTSTypes.SQLVARCHAR,
						aQueryAccountItemsResponse.getRequirement().length(),
						aQueryAccountItemsResponse.getRequirement());
			}
		}
		wProcedureResponse.setReturnCode(aQueryAccountItemsResponse.getReturnCode());
		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;

	}
}
