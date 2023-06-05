/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.update.card.dock.api;

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
 * @author nelsonJ
 * @since May 2, 2023
 * @version 1.0.0
 */
@Component(name = "UpdateCardDockOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "UpdateCardDockOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "UpdateCardDockOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_card_status_api")
})
public class UpdateCardDockOrchestrationCore extends SPJavaOrchestrationBase {
	
	private ILogger logger = (ILogger) this.getLogger();
	private static final String CLASS_NAME = "UpdateCardDockOrchestrationCore";
	protected static final String CHANNEL_REQUEST = "8";
	protected static final String CREATE_PERSON	= "CREATE_PERSON";
	protected static final String MODE_OPERATION = "PYS";

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
		
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		logger.logDebug("Begin flow, CustomerCardApplication start.");		
		
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		
		
		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();
		
		anProcedureResponse = updaterCardStatus(anOriginalRequest, aBagSPJavaOrchestration);
		
		return processResponseApi(anProcedureResponse,aBagSPJavaOrchestration);
		//return processResponseCardAppl(anProcedureResponse);
	}
	
	private IProcedureResponse updaterCardStatus(IProcedureRequest aRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en updaterCardStatus: " );
		}
		aBagSPJavaOrchestration.put("ente_mis", aRequest.readValueParam("@i_ente"));
		aBagSPJavaOrchestration.put("account_number", aRequest.readValueParam("@i_account_number"));
		
		IProcedureResponse wAccountsResp = new ProcedureResponseAS();

		wAccountsResp = getDataCardDock(aRequest, aBagSPJavaOrchestration);

		logger.logInfo(
				CLASS_NAME + " code resp card dock: " + wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue());
		if (wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")) {
			IProcedureResponse wAccountsRespInsert = new ProcedureResponseAS();
			wAccountsRespInsert = executeUpdateCard(aRequest, aBagSPJavaOrchestration);

			return wAccountsRespInsert;
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Response " + wAccountsResp.toString());
			logger.logInfo(CLASS_NAME + " Saliendo de updaterCardStatus");
		}

		return wAccountsResp;
	}
	
	private IProcedureResponse executeUpdateCard(IProcedureRequest anOriginalReq, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse connectorCardResponse = null;
		String idCardDock = null, status = null, reasonStatus = null, acccountNumber = null;
		
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		aBagSPJavaOrchestration.remove("trn_virtual");
		
		idCardDock = aBagSPJavaOrchestration.containsKey("o_id_card_dock")? aBagSPJavaOrchestration.get("o_id_card_dock").toString():null;
		status = aBagSPJavaOrchestration.containsKey("o_detail_status")? aBagSPJavaOrchestration.get("o_detail_status").toString():null;
		reasonStatus = aBagSPJavaOrchestration.containsKey("o_det_reason_stat")? aBagSPJavaOrchestration.get("o_det_reason_stat").toString():null;
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en executeUpdateCard " + acccountNumber);
		}
		try {
			// PARAMETROS DE ENTRADA
			anOriginalRequest.addInputParam("@i_ente", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("ente_mis").toString());
			anOriginalRequest.addInputParam("@i_id_card_dock", ICTSTypes.SQLVARCHAR, idCardDock);
			anOriginalRequest.addInputParam("@i_status", ICTSTypes.SQLVARCHAR, status);
			anOriginalRequest.addInputParam("@i_reason_status", ICTSTypes.SQLVARCHAR, reasonStatus);
			anOriginalRequest.addInputParam("@i_type_card", ICTSTypes.SQLVARCHAR, anOriginalReq.readValueParam("@i_type_card"));
			anOriginalRequest.addInputParam("@i_operation", ICTSTypes.SQLVARCHAR, "UCS");
			
			// VARIABLES DE SALIDA
			anOriginalRequest.addOutputParam("@o_cod_respuesta", ICTSTypes.SQLVARCHAR, "0");
			anOriginalRequest.addOutputParam("@o_desc_respuesta", ICTSTypes.SQLVARCHAR, "X");
			
			anOriginalRequest.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
					"(service.identifier=UpdateCardDockOrchestrationCore)");
			anOriginalRequest.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "executeTransaction");
			anOriginalRequest.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, "");
			anOriginalRequest.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequest.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequest.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingOrchestrator");

			anOriginalRequest.addFieldInHeader("trn", ICOBISTS.HEADER_STRING_TYPE, "18500112");
			anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500112");

			anOriginalRequest.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "18500112");
			
			// SE HACE LA LLAMADA AL CONECTOR
			aBagSPJavaOrchestration.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorDock)");
			anOriginalRequest.setSpName("cob_procesador..sp_card_status_api");

			anOriginalRequest.addInputParam("@trn_virtual", ICTSTypes.SYBINT4, "18500112");
			anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500112");

			logger.logDebug("cardDock--> request update card app: " + anOriginalRequest.toString());
			// SE EJECUTA CONECTOR
			connectorCardResponse = executeProvider(anOriginalRequest, aBagSPJavaOrchestration);

			if (logger.isDebugEnabled())
				logger.logDebug("jcos--> connectorUpdateCardApplicationResponse: " + connectorCardResponse);

			if (connectorCardResponse.readValueParam("@o_card_id") != null)
				aBagSPJavaOrchestration.put("o_card_id", connectorCardResponse.readValueParam("@o_card_id"));
			else
				aBagSPJavaOrchestration.put("o_card_id", "null");

			if (connectorCardResponse.readValueParam("@o_success") != null)
				aBagSPJavaOrchestration.put("o_success", connectorCardResponse.readValueParam("@o_card_id"));
			
		} catch (Exception e) {
			e.printStackTrace();
			connectorCardResponse = null;
			logger.logInfo(CLASS_NAME +" Error Catastrofico de updateCardStatusExecution");

		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + "--> Saliendo de updateCardStatusExecution");
			}
		}

		return connectorCardResponse;

	}

	private IProcedureResponse getDataCardDock(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getDataCardDock");
		}

		request.setSpName("cob_atm..sp_get_data_card_dock_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_ente"));
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account_number"));
		request.addInputParam("@i_status", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_card_status"));
		request.addInputParam("@i_reason_status", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_status_reason"));
		request.addInputParam("@i_type_card", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_type_card"));
		
		request.addOutputParam("@o_id_card_dock", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_detail_status", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_det_reason_stat", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_account", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_type_card", ICTSTypes.SQLVARCHAR, "X");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		aBagSPJavaOrchestration.put("o_id_card_dock", wProductsQueryResp.readValueParam("@o_id_card_dock"));
		aBagSPJavaOrchestration.put("o_detail_status", wProductsQueryResp.readValueParam("@o_detail_status"));
		aBagSPJavaOrchestration.put("o_det_reason_stat", wProductsQueryResp.readValueParam("@o_det_reason_stat"));
		aBagSPJavaOrchestration.put("o_account_number", wProductsQueryResp.readValueParam("@o_account"));
		aBagSPJavaOrchestration.put("o_type_card", wProductsQueryResp.readValueParam("@o_type_card"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getDataCardDock");
		}

		return wProductsQueryResp;
	}

	private void registerLogBd(IProcedureResponse reponseCard, Map<String, Object> aBagSPJavaOrchestration) {

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
        String fechaACtual = fechaHoraActual.format(formato);
		 
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aBagSPJavaOrchestration.get("ente_mis").toString());
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_account_number").toString());
		request.addInputParam("@i_fecha_reg", ICTSTypes.SQLVARCHAR, fechaACtual);
		request.addInputParam("@i_fecha_mod", ICTSTypes.SQLVARCHAR, fechaACtual);
		request.addInputParam("@i_modo", ICTSTypes.SQLVARCHAR, "UCS");
		
		request.addInputParam("@i_tarjeta_id", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_card_id"));
		request.addInputParam("@i_request_td", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_requestUpdateCard"));
		request.addInputParam("@i_estado_tarjeta", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_card_status"));
		request.addInputParam("@i_estado_upd", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_success"));

		String message = reponseCard.readValueParam("@o_responseUpdateCard")!=null?reponseCard.readValueParam("@o_responseUpdateCard"):reponseCard.getResultSetRowColumnData(2, 1, 2).getValue();
		request.addInputParam("@i_response_td", ICTSTypes.SQLVARCHAR, message);
		String typeCard = aBagSPJavaOrchestration.get("o_type_card")!=null?aBagSPJavaOrchestration.get("o_type_card").toString():null;
		request.addInputParam("@i_tipo_tarjeta", ICTSTypes.SQLVARCHAR, typeCard);
		
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
		
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		Integer codeReturn = anOriginalProcedureRes.getReturnCode();
		
		logger.logInfo("return code resp Conector --->" + codeReturn );

		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));

		
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));

		/*
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SYBINT4, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SYBVARCHAR, 255));
		*/
		if (codeReturn == 0) {
			
		Boolean flag = aBagSPJavaOrchestration.containsKey("o_success");
		
		logger.logDebug("response conector dock: " + anOriginalProcedureRes.toString());
		logger.logDebug("code o_assign_date: " + flag);
		logger.logDebug("retunr code response: " + anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1));
		
			if(flag == true){
				logger.logDebug("Ending flow, processResponse success with code: ");
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, "true"));
				data.addRow(row);
				
				IResultSetRow row2 = new ResultSetRow();
				row2.addRowData(1, new ResultSetRowColumnData(false, "0"));
				row2.addRowData(2, new ResultSetRowColumnData(false, "Success"));
				data2.addRow(row2);
				
			}
			else{
				logger.logDebug("Ending flow, processResponse error");
				
				String message = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).isNull()?"Service execution error":anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).getValue();
				String success = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).isNull()?"false":anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue();
				String code = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).isNull()?"400218":anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).getValue();
				
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, success));
				data.addRow(row);
				
				IResultSetRow row2 = new ResultSetRow();
				row2.addRowData(1, new ResultSetRowColumnData(false, code));
				row2.addRowData(2, new ResultSetRowColumnData(false, message));
				data2.addRow(row2);
			}
		} else {
			
			logger.logDebug("Ending flow, processResponse failed with code: ");
			
			IResultSetRow row = new ResultSetRow();
			row.addRowData(1, new ResultSetRowColumnData(false, "false"));
			data.addRow(row);
			
			IResultSetRow row2 = new ResultSetRow();
			row2.addRowData(1, new ResultSetRowColumnData(false, codeReturn.toString()));
			row2.addRowData(2, new ResultSetRowColumnData(false, anOriginalProcedureRes.getMessage(1).getMessageText()));
			data2.addRow(row2);
		}

		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);

		wProcedureResponse.addResponseBlock(resultsetBlock);
		wProcedureResponse.addResponseBlock(resultsetBlock2);
		
		registerLogBd(anOriginalProcedureRes, aBagSPJavaOrchestration);
		return wProcedureResponse;		
	}
}
