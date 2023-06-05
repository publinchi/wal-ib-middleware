/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.update.account.status.dock.api;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;

/**
 * @author Sochoa
 * @since Jun 1, 2023
 * @version 1.0.0
 */
@Component(name = "UpdateAccountStatusDockOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "UpdateAccountStatusDockOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "UpdateAccountStatusDockOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_card_application_api")
})
public class UpdateAccountStatusDockOrchestrationCore extends SPJavaOrchestrationBase {
	
	private ILogger logger = (ILogger) this.getLogger();
	private static final String CLASS_NAME = "UpdateAccountStatusDockOrchestrationCore";
	protected static final String CHANNEL_REQUEST = "8";
	protected static final String UPDATE_ACCOUNT_STATUS	= "UPDATE_ACCOUNT_STATUS";
	protected static final String MODE_OPERATION = "PYS";

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
		
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		logger.logDebug("Begin flow, UpdateAccountStatusDock starts...");		
		
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		
		
		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();
		
		anProcedureResponse = updateAccountStatusDock(anOriginalRequest, aBagSPJavaOrchestration);
		
		return processResponseApi(anProcedureResponse,aBagSPJavaOrchestration);
		//return processResponseCardAppl(anProcedureResponse);
	}
	
	private IProcedureResponse updateAccountStatusDock(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		String flowData = null;
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en updateAccountStatusDock: " + aRequest.readValueParam("@i_org_eje") + " - "+aBagSPJavaOrchestration.containsKey("o_account_id_dock"));
		}
		aBagSPJavaOrchestration.put("externalCustomerId", aRequest.readValueParam("@i_external_customer_id"));
		
		IProcedureResponse wAccountsResp = new ProcedureResponseAS();
		
		
		wAccountsResp = valData(aRequest, aBagSPJavaOrchestration);
		
		/*
		logger.logInfo(CLASS_NAME + " SetRowColumnData xdc: " + wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue());
		if (wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			IProcedureResponse wAccountsRespConector = new ProcedureResponseAS();
			wAccountsRespConector = updateAccountStatusDockConector(aRequest, aBagSPJavaOrchestration);
			
			return wAccountsRespConector; 
		}
		*/

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Response " + wAccountsResp.toString());
			logger.logInfo(CLASS_NAME + " Saliendo de updateAccountStatusDock");
		}

		return wAccountsResp;
	}
	
	private IProcedureResponse valData(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en valData");
		}

		request.setSpName("cob_atm..sp_get_data_account_status_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam(" @i_external_customer_id", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_externalCustomerId"));
		request.addInputParam("@i_account_status", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account_status"));
		request.addInputParam("@i_account", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account"));
		request.addOutputParam("@o_account_dock_id", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_account", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_dock_flow", ICTSTypes.SQLVARCHAR, "X");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		aBagSPJavaOrchestration.put("accountDockId", wProductsQueryResp.readValueParam("@o_account_dock_id"));
		aBagSPJavaOrchestration.put("account", wProductsQueryResp.readValueParam("@o_account"));
		aBagSPJavaOrchestration.put("dockFlow", wProductsQueryResp.readValueParam("@o_dock_flow"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de valData");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse updateAccountStatusDockConector(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse connectorCardResponse = null;
		
		String accountDockId = aBagSPJavaOrchestration.containsKey("accountDockId")? aBagSPJavaOrchestration.get("accountDockId").toString():null;
		String dockFlow = aBagSPJavaOrchestration.containsKey("dockFlow")? aBagSPJavaOrchestration.get("dockFlow").toString():null;

		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en updateAccountStatusDockConector " + accountDockId );
		}
		try {
			// PARAMETROS DE ENTRADA
			anOriginalRequest.addInputParam("@i_external_customer_id", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_external_customer_id"));
			anOriginalRequest.addInputParam("@i_account_dock_id", ICTSTypes.SQLVARCHAR, accountDockId);
			
			
			if(null!=(anOriginalRequest.readValueParam("@i_org_eje"))){
				anOriginalRequest.addInputParam("@i_mode_create", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_mode_create"));

			}
			else{
				anOriginalRequest.addInputParam("@i_account_id", ICTSTypes.SQLVARCHAR, null);
				anOriginalRequest.addInputParam("@i_person_id", ICTSTypes.SQLVARCHAR, null);
				anOriginalRequest.addInputParam("@i_card_id", ICTSTypes.SQLVARCHAR, null);
				anOriginalRequest.addInputParam("@i_mode_create", ICTSTypes.SQLVARCHAR, MODE_OPERATION);
				anOriginalRequest.addInputParam("@i_flow_dock", ICTSTypes.SQLVARCHAR, UPDATE_ACCOUNT_STATUS);
				if(!aBagSPJavaOrchestration.get("accountDockId").equals("X")){
					anOriginalRequest.addInputParam("@i_dock_flow", ICTSTypes.SQLVARCHAR, dockFlow);
								
				}
			}
			
			// VARIABLES DE SALIDA
			anOriginalRequest.addOutputParam("@o_cod_respuesta", ICTSTypes.SQLVARCHAR, "0");
			anOriginalRequest.addOutputParam("@o_desc_respuesta", ICTSTypes.SQLVARCHAR, "X");
			//request.addOutputParam("@o_id_solicitud", ICTSTypes.SQLVARCHAR, "0"); 
			
			anOriginalRequest.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
					"(service.identifier=UpdateCardDockOrchestrationCore)");
			anOriginalRequest.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "executeTransaction");
			anOriginalRequest.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, "");
			anOriginalRequest.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequest.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequest.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingOrchestrator");
			
			// SE HACE LA LLAMADA AL CONECTOR
			aBagSPJavaOrchestration.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorDock)");
			anOriginalRequest.setSpName("cob_procesador..sp_card_application_api");

			anOriginalRequest.addInputParam("@trn_virtual", ICTSTypes.SYBINT4, "18500112");
			anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500112");


			logger.logDebug("xcxc--> request card app: " + anOriginalRequest.toString());
			// SE EJECUTA CONECTOR
			connectorCardResponse = executeProvider(anOriginalRequest, aBagSPJavaOrchestration);

			if (logger.isDebugEnabled())
				logger.logDebug("jcos--> connectorCreateCardApplicationResponse: " + connectorCardResponse);

				if (connectorCardResponse.readValueParam("@o_person_id") != null)
					aBagSPJavaOrchestration.put("o_person_id",connectorCardResponse.readValueParam("@o_person_id"));
				else
					aBagSPJavaOrchestration.put("o_person_id","null");
				
				if (connectorCardResponse.readValueParam("@o_account_id") != null)
					aBagSPJavaOrchestration.put("o_account_id",connectorCardResponse.readValueParam("@o_account_id"));
				else
					aBagSPJavaOrchestration.put("o_account_id","null");				
				
				if (connectorCardResponse.readValueParam("@o_card_id") != null)
					aBagSPJavaOrchestration.put("o_card_id",connectorCardResponse.readValueParam("@o_card_id"));
				else
					aBagSPJavaOrchestration.put("o_card_id","null");				
				
				if (connectorCardResponse.readValueParam("@o_assign_date") != null)
					aBagSPJavaOrchestration.put("o_assign_date",connectorCardResponse.readValueParam("@o_assign_date"));
				else
					aBagSPJavaOrchestration.put("o_assign_date","null");
						
						
		} catch (Exception e) {
			e.printStackTrace();
			connectorCardResponse = null;
			logger.logInfo(CLASS_NAME +" Error Catastrofico de cacaoExecution");

		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + "--> Saliendo de cacaoExecution");
			}
		}

		return connectorCardResponse;

	}

	public IProcedureResponse processResponseCardAppl(IProcedureResponse anOriginalProcedureRes) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(" start processResponseAccounts--->");
		}

		IProcedureResponse anOriginalProcedureResponse = new ProcedureResponseAS();
		String code,message,success;
		Integer codeReturn = anOriginalProcedureRes.getReturnCode();
		
		logger.logInfo("xdcxv1 --->" + codeReturn );
		if (codeReturn == 0){
			code = "0";
			message = "Success";
			success = "true";
		} else {
			success = "false";
			code = String.valueOf(codeReturn);
			message = anOriginalProcedureRes.getMessage(1).getMessageText();
		}
		
		// Agregar Header y data 1
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));

		// Agregar Header y data 2
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));

		// Agregar info 1
		IResultSetRow row = new ResultSetRow();
		row.addRowData(1, new ResultSetRowColumnData(false, code));
		row.addRowData(2, new ResultSetRowColumnData(false, message));
		data.addRow(row);

		// Agregar info 2
		IResultSetRow row2 = new ResultSetRow();
		row2.addRowData(1, new ResultSetRowColumnData(false, success));
		data2.addRow(row2);

		// Agregar resulBlock
		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);

		
		anOriginalProcedureResponse.setReturnCode(200);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock2);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock);
	
		return anOriginalProcedureResponse;
	}

	private void registerLogBd(IProcedureResponse reponseAccount, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en registerLogBd");
		}

		request.setSpName("cob_atm..sp_insert_data_dock_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		LocalDateTime fechaHoraActual = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String fechaActual = fechaHoraActual.format(formato);
		 
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aBagSPJavaOrchestration.get("externalCustomerId").toString());
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("account").toString());
		request.addInputParam("@i_fecha_reg", ICTSTypes.SQLVARCHAR, fechaActual);
		request.addInputParam("@i_fecha_mod", ICTSTypes.SQLVARCHAR, fechaActual);
		request.addInputParam("@i_modo", ICTSTypes.SQLVARCHAR, "UAS");

		request.addInputParam("@i_cuenta_id", ICTSTypes.SQLVARCHAR, reponseAccount.readValueParam("accountDockId"));
		request.addInputParam("@i_request_cd", ICTSTypes.SQLVARCHAR, reponseAccount.readValueParam("requestUpdateAccountStatus"));
		request.addInputParam("@i_response_cd", ICTSTypes.SQLVARCHAR, reponseAccount.readValueParam("responseUpdateAccountStatus"));

		
		if(!reponseAccount.getResultSetRowColumnData(2, 1, 2).isNull() && null==reponseAccount.readValueParam("@o_responseCreatePerson"))
		request.addInputParam("@i_response_cd", ICTSTypes.SQLVARCHAR, reponseAccount.getResultSetRowColumnData(2, 1, 2).getValue());
		
		
		//request.addOutputParam("@o_curp", ICTSTypes.SQLVARCHAR, "X");
		//request.addOutputParam("@o_full_name", ICTSTypes.SQLVARCHAR, "X");
		logger.logDebug("Request Corebanking registerLog: " + request.toString());
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de registerLogBd");
		}
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		ArrayList<String> keyList = new ArrayList<String>(aBagSPJavaOrchestration.keySet());
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		IResultSetRow row = new ResultSetRow();
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SYBINT4, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("cardId", ICTSTypes.SYBINT4, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("cardNumber", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("customerName", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("cardApplication", ICTSTypes.SYBINT4, 255));
		
		if (keyList.get(0).equals("0")) {
			logger.logDebug("Ending flow, processResponse success with code: " + keyList.get(0));
			row.addRowData(1, new ResultSetRowColumnData(false, "true"));
			row.addRowData(2, new ResultSetRowColumnData(false, "0"));
			row.addRowData(3, new ResultSetRowColumnData(false, "Success"));
			row.addRowData(4, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get("o_cod_respuesta").toString()));
			row.addRowData(5, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get("o_desc_respuesta").toString()));
			row.addRowData(6, new ResultSetRowColumnData(false, null));
			row.addRowData(7, new ResultSetRowColumnData(false, null));
			data.addRow(row);

		} else {
			logger.logDebug("Ending flow, processResponse failed with code: " + keyList.get(0));
			row.addRowData(1, new ResultSetRowColumnData(false, "false"));
			row.addRowData(2, new ResultSetRowColumnData(false, keyList.get(0)));
			row.addRowData(3, new ResultSetRowColumnData(false, (String) aBagSPJavaOrchestration.get(keyList.get(0))));
			row.addRowData(4, new ResultSetRowColumnData(false, null));
			row.addRowData(5, new ResultSetRowColumnData(false, null));
			row.addRowData(6, new ResultSetRowColumnData(false, null));
			row.addRowData(7, new ResultSetRowColumnData(false, null));
			data.addRow(row);
		}
		
		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
		wProcedureResponse.addResponseBlock(resultBlock);			
		return wProcedureResponse;		
	}
	
	public IProcedureResponse processResponseApi(IProcedureResponse anOriginalProcedureRes, Map<String, Object> aBagSPJavaOrchestration) {
		
		logger.logInfo("processResponseApi [INI] --->" );
		ArrayList<String> keyList = new ArrayList<String>(aBagSPJavaOrchestration.keySet());
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		IResultSetRow row = new ResultSetRow();
		
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		Integer codeReturn = anOriginalProcedureRes.getReturnCode();
		
		logger.logInfo("return code resp Conector --->" + codeReturn );
		
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SYBINT4, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SYBVARCHAR, 255));

		if (codeReturn == 0) {
			
		Boolean flag = aBagSPJavaOrchestration.containsKey("success");
		
		logger.logDebug("response conector: " + anOriginalProcedureRes.toString());
		logger.logDebug("code o_assign_date: " + flag);
		logger.logDebug("retunr code response: " + anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1));
		
			if(flag!=null){
				logger.logDebug("Ending flow, processResponse success with code: " + keyList.get(0));
				row.addRowData(1, new ResultSetRowColumnData(false, "true"));
				row.addRowData(2, new ResultSetRowColumnData(false, "0"));
				row.addRowData(3, new ResultSetRowColumnData(false, "Success"));
				data.addRow(row);
			}
			else{
				
				logger.logDebug("Ending flow, processResponse error");
				
				String success = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue();
				String code = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).getValue();
				String message = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).getValue();
		
				row.addRowData(1, new ResultSetRowColumnData(false, success));
				row.addRowData(2, new ResultSetRowColumnData(false, code));
				row.addRowData(3, new ResultSetRowColumnData(false, message));
				data.addRow(row);
			}
		} else {
			
			logger.logDebug("Ending flow, processResponse failed with code: " + keyList.get(0));
			
			row.addRowData(1, new ResultSetRowColumnData(false, "false"));
			row.addRowData(2, new ResultSetRowColumnData(false, codeReturn.toString()));
			row.addRowData(3, new ResultSetRowColumnData(false, anOriginalProcedureRes.getMessage(1).getMessageText()));
			data.addRow(row);
		}
		
		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
		wProcedureResponse.addResponseBlock(resultBlock);			
		
		registerLogBd(anOriginalProcedureRes, aBagSPJavaOrchestration);
		
		return wProcedureResponse;		
	}
}
