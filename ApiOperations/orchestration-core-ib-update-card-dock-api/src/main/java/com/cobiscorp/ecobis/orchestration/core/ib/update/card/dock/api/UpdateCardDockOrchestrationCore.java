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
		
		if(anProcedureResponse.getReturnCode()==0){
			
			anProcedureResponse = processResponseApi(anProcedureResponse,aBagSPJavaOrchestration);
		}
		
		return anProcedureResponse;
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
		String flag = "S";
		wAccountsResp = getDataCardDock(aRequest, aBagSPJavaOrchestration);

		if(wAccountsResp.getResultSets().size()>1 && !wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			return wAccountsResp;
		}
		
		//ACTIVAR TARJETA COBIS
		if(aBagSPJavaOrchestration.containsKey("o_status_atm") && aBagSPJavaOrchestration.get("mode").toString().equals("N")){
			if(aBagSPJavaOrchestration.get("o_status_atm").toString().equals("reg"))
			{
				if(!aBagSPJavaOrchestration.get("o_card_available").equals("X")){
					IProcedureResponse wAccountsRespDock = registerAtmCobis(aBagSPJavaOrchestration);
					
					if(wAccountsRespDock.getReturnCode()==0){
						wAccountsRespDock = executeAssingCard(aRequest, aBagSPJavaOrchestration);
						registerAssingLogBd(wAccountsRespDock,aBagSPJavaOrchestration);
						registerLogBd(wAccountsRespDock, aBagSPJavaOrchestration);
						flag = "N";
						aBagSPJavaOrchestration.put("flag_log",flag);
						
						if(wAccountsRespDock.getReturnCode()!=0)
							return wAccountsRespDock;
					}
					else{
						return wAccountsRespDock;
					}
				} else if (wAccountsResp.readValueParam("@o_type_card").toString().equals("VIRTUAL") && wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
						IProcedureResponse wAccountsRespDock = registerAtmCobis(aBagSPJavaOrchestration);
					
						if(wAccountsRespDock.getReturnCode()==0){
							wAccountsRespDock = executeAssingCard(aRequest, aBagSPJavaOrchestration);
							registerAssingLogBd(wAccountsRespDock,aBagSPJavaOrchestration);
							registerLogBd(wAccountsRespDock, aBagSPJavaOrchestration);
							flag = "N";
							aBagSPJavaOrchestration.put("flag_log",flag);
							
							if(wAccountsRespDock.getReturnCode()!=0)
								return wAccountsRespDock;
						}
						else{
							return wAccountsRespDock;
						}
				}
			}
		}		
		
		logger.logInfo(
				CLASS_NAME + " code resp card dock: " + wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue());
		//if (wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")) {
		if (wAccountsResp.getReturnCode()==0 ){ 
				if(!wAccountsResp.readValueParam("@o_id_card_atm").equals("0") && flag.equals("S")) {
					IProcedureResponse wAccountsRespInsert = new ProcedureResponseAS();
					wAccountsRespInsert = executeUpdateCard(aRequest, aBagSPJavaOrchestration);
					updateStatusAtm(aRequest, aBagSPJavaOrchestration);
					return wAccountsRespInsert;
				}else if(wAccountsResp.readValueParam("@o_type_card").toString().equals("VIRTUAL") && !wAccountsResp.readValueParam("@o_id_card_dock").toString().equals("X") && flag.equals("S")) {
					IProcedureResponse wAccountsRespInsert = new ProcedureResponseAS();
					wAccountsRespInsert = executeUpdateCard(aRequest, aBagSPJavaOrchestration);
					updateStatusAtm(aRequest, aBagSPJavaOrchestration);
					return wAccountsRespInsert;
				}
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
		reasonStatus = aBagSPJavaOrchestration.containsKey("o_det_reason_stat")? aBagSPJavaOrchestration.get("o_det_reason_stat").toString():"X";
		
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

	private IProcedureResponse executeAssingCard(IProcedureRequest anOriginalReq, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse connectorCardResponse = null;
		String typeCard = null;
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		
		aBagSPJavaOrchestration.remove("trn_virtual");
		typeCard = anOriginalReq.readValueParam("@i_type_card").equals("VI")?"VIRTUAL":"PHYSICAL";
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en executeAssingCard typeCard: " + typeCard);
		}
		try {
			// PARAMETROS DE ENTRADA
			anOriginalRequest.addInputParam("@i_ente", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("ente_mis").toString());
			if (typeCard.equals("PHYSICAL")){
				anOriginalRequest.addInputParam("@i_id_card_dock", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_card_available").toString());
			}else{
				anOriginalRequest.addInputParam("@i_id_card_dock", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_id_card_dock").toString());
				anOriginalRequest.addInputParam("@i_status", ICTSTypes.SQLVARCHAR, "NORMAL");
			}
			anOriginalRequest.addInputParam("@i_reason_status", ICTSTypes.SQLVARCHAR, "X");
			anOriginalRequest.addInputParam("@i_id_person_dock", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_id_person_dock").toString());
			anOriginalRequest.addInputParam("@i_id_account_dock", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_id_account_dock").toString());
			
			anOriginalRequest.addInputParam("@i_type_card", ICTSTypes.SQLVARCHAR, typeCard);
			anOriginalRequest.addInputParam("@i_operation", ICTSTypes.SQLVARCHAR, "ASC");
			anOriginalRequest.addInputParam("@i_account", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_account_number").toString());
			
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

			logger.logDebug("cardDock--> request executeAssingCard app: " + anOriginalRequest.toString());
			// SE EJECUTA CONECTOR
			connectorCardResponse = executeProvider(anOriginalRequest, aBagSPJavaOrchestration);

			if (logger.isDebugEnabled())
				logger.logDebug("Dock--> connectorUpdateCardApplicationResponse: " + connectorCardResponse);

			if (connectorCardResponse.readValueParam("@o_person_id") != null)
				aBagSPJavaOrchestration.put("o_person_id", connectorCardResponse.readValueParam("@o_person_id"));
			else
				aBagSPJavaOrchestration.put("o_person_id", "null");
			
			if (connectorCardResponse.readValueParam("@o_account_id") != null)
				aBagSPJavaOrchestration.put("o_account_id", connectorCardResponse.readValueParam("@o_account_id"));
			else
				aBagSPJavaOrchestration.put("o_account_id", "null");
			
			if (connectorCardResponse.readValueParam("@o_card_id") != null)
				aBagSPJavaOrchestration.put("o_card_id", connectorCardResponse.readValueParam("@o_card_id"));
			else
				aBagSPJavaOrchestration.put("o_card_id", "null");
			
			if (connectorCardResponse.readValueParam("@o_card_type") != null)
				aBagSPJavaOrchestration.put("o_card_type", connectorCardResponse.readValueParam("@o_card_type"));
			else
				aBagSPJavaOrchestration.put("o_card_type", "null");
			
			if (connectorCardResponse.readValueParam("@o_assign_date") != null)
				aBagSPJavaOrchestration.put("o_assign_date", connectorCardResponse.readValueParam("@o_assign_date"));
			else
				aBagSPJavaOrchestration.put("o_assign_date", "null");
			
			if (connectorCardResponse.readValueParam("@o_requestAssingCard") != null)
				aBagSPJavaOrchestration.put("o_requestAssingCard", connectorCardResponse.readValueParam("@o_requestAssingCard"));
			else
				aBagSPJavaOrchestration.put("o_requestAssingCard", "null");
			
			if (connectorCardResponse.readValueParam("@o_responseAssingCard") != null)
				aBagSPJavaOrchestration.put("o_responseAssingCard", connectorCardResponse.readValueParam("@o_responseAssingCard"));
			else
				aBagSPJavaOrchestration.put("o_responseAssingCard", "null");
			
			if (connectorCardResponse.readValueParam("@o_card_status") != null)
				aBagSPJavaOrchestration.put("o_card_status", connectorCardResponse.readValueParam("@o_card_status"));
			else
				aBagSPJavaOrchestration.put("o_card_status", "null");
			
			if (connectorCardResponse.readValueParam("@o_requestUpdateCard") != null)
				aBagSPJavaOrchestration.put("o_requestUpdateCard", connectorCardResponse.readValueParam("@o_requestUpdateCard"));
			else
				aBagSPJavaOrchestration.put("o_requestUpdateCard", "null");
			
			if (connectorCardResponse.readValueParam("@o_responseUpdateCard") != null)
				aBagSPJavaOrchestration.put("o_responseUpdateCard", connectorCardResponse.readValueParam("@o_responseUpdateCard"));
			else
				aBagSPJavaOrchestration.put("o_responseUpdateCard", "null");
			

			if (connectorCardResponse.readValueParam("@o_success") != null)
				aBagSPJavaOrchestration.put("o_success", connectorCardResponse.readValueParam("@o_success"));
			else
				aBagSPJavaOrchestration.put("o_success", "null");

			
		} catch (Exception e) {
			e.printStackTrace();
			connectorCardResponse = null;
			logger.logInfo(CLASS_NAME +" Error Catastrofico de executeAssingCard");

		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + "--> Saliendo de executeAssingCard");
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
		request.addInputParam("@i_mode", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_mode"));
		request.addInputParam("@i_card_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_card_id"));
		
		request.addOutputParam("@o_id_card_dock", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_detail_status", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_det_reason_stat", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_account", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_type_card", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_status_atm", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_card_available", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_id_person_dock", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_id_account_dock", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_id_card_atm", ICTSTypes.SQLINT4, "0");

		String mode = aRequest.readValueParam("@i_mode").equals("null")?"X":aRequest.readValueParam("@i_mode").toString();
		aBagSPJavaOrchestration.put("mode", mode);
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		
		aBagSPJavaOrchestration.put("o_id_card_dock", wProductsQueryResp.readValueParam("@o_id_card_dock"));
		aBagSPJavaOrchestration.put("o_detail_status", wProductsQueryResp.readValueParam("@o_detail_status"));
		aBagSPJavaOrchestration.put("o_det_reason_stat", wProductsQueryResp.readValueParam("@o_det_reason_stat"));
		aBagSPJavaOrchestration.put("o_account_number", wProductsQueryResp.readValueParam("@o_account"));
		aBagSPJavaOrchestration.put("o_type_card", wProductsQueryResp.readValueParam("@o_type_card"));
		aBagSPJavaOrchestration.put("o_status_atm", wProductsQueryResp.readValueParam("@o_status_atm"));
		aBagSPJavaOrchestration.put("o_card_available", wProductsQueryResp.readValueParam("@o_card_available"));
		aBagSPJavaOrchestration.put("o_id_person_dock", wProductsQueryResp.readValueParam("@o_id_person_dock"));
		aBagSPJavaOrchestration.put("o_id_account_dock", wProductsQueryResp.readValueParam("@o_id_account_dock"));
		aBagSPJavaOrchestration.put("o_id_card_atm", wProductsQueryResp.readValueParam("@o_id_card_atm"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking getDataCardDock: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getDataCardDock");
		}

		return wProductsQueryResp;
	}

	private IProcedureResponse updateStatusAtm(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();
		Integer trn = 0;
		String process = null, reason = null;

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en updateStatusAtm");
		}

		if(aRequest.readValueParam("@i_card_status").equals("N"))
		{
			trn = 16537;
			process = "LBW";
			reason = "SCL";
		}else if (aRequest.readValueParam("@i_card_status").equals("B")){
			trn = 16507;
			process = "BLW";
			reason = "SCL";
		}else{
			trn = 16507;
			process = "BLW";
			reason = "SCL";
		}
		
		
		request.setSpName("cob_atm..sp_atm_bloqueo_tarjeta");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		//request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_ente"));
		request.addInputParam("@i_proceso", ICTSTypes.SQLVARCHAR, process);
		request.addInputParam("@i_banco", ICTSTypes.SQLINT4, "1");
		request.addInputParam("@i_tarjeta", ICTSTypes.SQLVARCHAR,aBagSPJavaOrchestration.get("o_id_card_atm").toString());
		request.addInputParam("@i_motivo", ICTSTypes.SQLVARCHAR, reason);
		request.addInputParam("@i_oficina", ICTSTypes.SQLINT4, "1");
		request.addInputParam("@i_retener", ICTSTypes.SQLVARCHAR, "N");
		
		request.addInputParam("@s_ofi", ICTSTypes.SQLINT4, "1");
		request.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		request.addInputParam("@t_trn", ICTSTypes.SQLVARCHAR, String.valueOf(trn));
		
		request.addOutputParam("@o_secuencial", ICTSTypes.SQLVARCHAR, "0");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking updateStatusAtm: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de updateStatusAtm");
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

		//String message = reponseCard.readValueParam("@o_responseUpdateCard")!=null?reponseCard.readValueParam("@o_responseUpdateCard"):reponseCard.getResultSetRowColumnData(2, 1, 2).getValue();
		String message = reponseCard.readValueParam("@o_responseUpdateCard");
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

	private void registerAssingLogBd(IProcedureResponse reponseCard, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en registerAssingLogBd");
		}

		request.setSpName("cob_atm..sp_insert_data_dock_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		LocalDateTime fechaHoraActual = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String fechaACtual = fechaHoraActual.format(formato);
		 
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aBagSPJavaOrchestration.get("ente_mis").toString());
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("account_number").toString());
		request.addInputParam("@i_fecha_reg", ICTSTypes.SQLVARCHAR, fechaACtual);
		request.addInputParam("@i_fecha_mod", ICTSTypes.SQLVARCHAR, fechaACtual);
		request.addInputParam("@i_modo", ICTSTypes.SQLVARCHAR, "CCA");
		request.addInputParam("@i_tarjeta_id", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_card_id"));
		
		request.addInputParam("@i_asig_date", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_assign_date"));
		request.addInputParam("@i_request_ad", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_requestAssingCard"));
		request.addInputParam("@i_response_ad", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_responseAssingCard"));
	
		String typeCard = aBagSPJavaOrchestration.get("o_card_type")!=null?aBagSPJavaOrchestration.get("o_card_type").toString():null;
		request.addInputParam("@i_tipo_tarjeta", ICTSTypes.SQLVARCHAR, typeCard);
		
		logger.logDebug("Request Corebanking registerAssingLogBd: " + request.toString());
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking registerAssingLogBd: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de registerAssingLogBd");
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

		IResultSetHeader metaData3 = new ResultSetHeader();
		IResultSetData data3 = new ResultSetData();
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("cardId", ICTSTypes.SQLVARCHAR, 80));
		
		/*
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SYBINT4, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SYBVARCHAR, 255));
		*/
		if(aBagSPJavaOrchestration.containsKey("flag_log"))
			registerLogBd(anOriginalProcedureRes, aBagSPJavaOrchestration);
		
		if (codeReturn == 0) {
			
		Boolean flag = aBagSPJavaOrchestration.containsKey("o_success");
		
		logger.logDebug("response conector dock: " + anOriginalProcedureRes.toString());
		logger.logDebug("code o_assign_date: " + flag);
		logger.logDebug("retunr code response: " + anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1));
		logger.logDebug("retunr o_card_available: " + aBagSPJavaOrchestration.get("o_card_available"));
		logger.logDebug("retunr o_id_card_dock: " + aBagSPJavaOrchestration.get("o_id_card_dock"));
		logger.logDebug("request mode: " + aBagSPJavaOrchestration.get("mode"));
		
		
			if(flag == true){
				logger.logDebug("Ending flow, processResponse success with code: ");
				
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, "true"));
				data.addRow(row);
				
				IResultSetRow row2 = new ResultSetRow();
				row2.addRowData(1, new ResultSetRowColumnData(false, "0"));
				row2.addRowData(2, new ResultSetRowColumnData(false, "Success"));
				data2.addRow(row2);
				
				if(!aBagSPJavaOrchestration.get("o_card_available").toString().equals("X")){
					IResultSetRow row3 = new ResultSetRow();
					row3.addRowData(1, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get("o_card_available").toString()));
					data3.addRow(row3);
				}
				
				if(!aBagSPJavaOrchestration.get("o_id_card_dock").toString().equals("X")){
					IResultSetRow row3 = new ResultSetRow();
					row3.addRowData(1, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get("o_id_card_dock").toString()));
					data3.addRow(row3);
				}
								
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
		
		if(!aBagSPJavaOrchestration.get("o_card_available").toString().equals("X") && aBagSPJavaOrchestration.get("mode").toString().equals("N")){
			IResultSetBlock resultsetBlock3 = new ResultSetBlock(metaData3, data3);
			wProcedureResponse.addResponseBlock(resultsetBlock3);
		}

		if(!aBagSPJavaOrchestration.get("o_id_card_dock").toString().equals("X") && aBagSPJavaOrchestration.get("mode").toString().equals("N")){
			IResultSetBlock resultsetBlock3 = new ResultSetBlock(metaData3, data3);
			wProcedureResponse.addResponseBlock(resultsetBlock3);
		}
		
		return wProcedureResponse;		
	}
	
	private IProcedureResponse registerAtmCobis(Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en registerAtmCobis");
		}
		
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
				
		wProcedureResponse = getApplicationCard(aBagSPJavaOrchestration);
		
		wProcedureResponse = grabarSolicitud(aBagSPJavaOrchestration);
		
		if(wProcedureResponse.getReturnCode()!=0)
			return wProcedureResponse;
		
		if(wProcedureResponse.readValueParam("@o_numero").equals("0") || wProcedureResponse.getReturnCode()!=0)
			return wProcedureResponse;
		
		wProcedureResponse = aprobacionDefault(aBagSPJavaOrchestration);
		
		wProcedureResponse = getCustomerCardByAccount(aBagSPJavaOrchestration);
		
		//IProcedureResponse wProcedureRes = getActivationState(aBagSPJavaOrchestration);
		
		//if(null==wProcedureRes){
		wProcedureResponse = setActivationState(aBagSPJavaOrchestration);
		//}
		
		wProcedureResponse = insertCardActivationApplication(aBagSPJavaOrchestration);
		
		wProcedureResponse = activateCardAtm(aBagSPJavaOrchestration);
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Response: " + wProcedureResponse.toString());
			logger.logInfo(CLASS_NAME + " Saliendo de registerAtmCobis");
		}
		
		return wProcedureResponse;
		
	}

	private IProcedureResponse getApplicationCard(Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getApplicationCard");
		}

		request.setSpName("cob_bvirtual..sp_consulta_solicitud");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "Q");
		request.addInputParam("@i_cliente", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("ente_mis").toString());
		request.addInputParam("@i_modo", ICTSTypes.SQLINT4, "0");

		request.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		request.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0.0.0.0");
		request.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, "");
		request.addInputParam("@s_ofi", ICTSTypes.SQLINT2, "1");
		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, "18500048");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getApplicationCard ->getResultSetListSize " + wProductsQueryResp.getResultSetListSize() + "  getResultSets: "+wProductsQueryResp.getResultSets().size());
		}

		if (null!=wProductsQueryResp.getResultSetRowColumnData(1, 1, 1)) {
			logger.logInfo(CLASS_NAME + " Data(1, 1, 1) cod_application"+ wProductsQueryResp.getResultSetRowColumnData(1, 1, 1).getValue());
			aBagSPJavaOrchestration.put("cod_application",wProductsQueryResp.getResultSetRowColumnData(1, 1, 1).getValue());
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse grabarSolicitud(Map<String, Object> aBagSPJavaOrchestration) {
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Inicia grabarSolicitud");
		}

		ProcedureRequestAS wProcedureRequest = new ProcedureRequestAS();
		
		wProcedureRequest.setSpName("cob_atm..sp_atm_graba_solicitud");

		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		wProcedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, "16503");
		wProcedureRequest.addInputParam("@t_trn", 56, "16503");
		wProcedureRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		
		wProcedureRequest.addInputParam("@i_numero", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("cod_application").toString());
		wProcedureRequest.addInputParam("@i_num_detalles", ICTSTypes.SQLINT2, "1");
		wProcedureRequest.addInputParam("@i_num_producto", ICTSTypes.SQLINT2, "1");
		wProcedureRequest.addInputParam("@i_num_excepcion", ICTSTypes.SQLINT2, "0");
		wProcedureRequest.addInputParam("@i_ofi_entrega", ICTSTypes.SQLINT2, "1");
		wProcedureRequest.addInputParam("@i_autorizado", ICTSTypes.SQLVARCHAR, "usuariobv");
		wProcedureRequest.addInputParam("@i_tipo_ent", ICTSTypes.SQLCHAR, "O");
		wProcedureRequest.addInputParam("@i_motivo", ICTSTypes.SQLVARCHAR, "ETI");
		wProcedureRequest.addInputParam("@i_chip", ICTSTypes.SQLVARCHAR, "N");
		wProcedureRequest.addInputParam("@i_oficial_neg", ICTSTypes.SQLVARCHAR, "usuariobv");
		String cardIdDock = aBagSPJavaOrchestration.get("o_type_card").equals("VIRTUAL")?aBagSPJavaOrchestration.get("o_id_card_dock").toString():aBagSPJavaOrchestration.get("o_card_available").toString();
		wProcedureRequest.addInputParam("@i_num_plastico", ICTSTypes.SQLVARCHAR, cardIdDock);

		wProcedureRequest.addOutputParam("@o_numero", ICTSTypes.SQLINT4, "0");
		wProcedureRequest.addOutputParam("@o_valor_costo", ICTSTypes.SQLMONEY, "0000");
		wProcedureRequest.addOutputParam("@o_cta_debito", ICTSTypes.SQLVARCHAR, "000000000000000000000000");
		wProcedureRequest.addOutputParam("@o_prod_debito", ICTSTypes.SQLINT2, "0");
		wProcedureRequest.addOutputParam("@o_moneda_deb", ICTSTypes.SQLINT2, "0");
		wProcedureRequest.addOutputParam("@o_causal", ICTSTypes.SQLVARCHAR, "0000000000000000");
		wProcedureRequest.addOutputParam("@o_ofi_comprobante", ICTSTypes.SQLINT2, "00000"); 
		wProcedureRequest.addOutputParam("@o_categoria", ICTSTypes.SQLCHAR, "X");
		wProcedureRequest.addOutputParam("@o_tipo_solicitud", ICTSTypes.SQLVARCHAR, "XXXX");
		wProcedureRequest.addOutputParam("@o_param_oficina", ICTSTypes.SQLVARCHAR, "XXXX");
		wProcedureRequest.addOutputParam("@o_tarjeta", ICTSTypes.SQLINT4, "0");
		wProcedureRequest.addOutputParam("@o_valor_seguro", ICTSTypes.SQLMONEY, "0000");
		wProcedureRequest.addOutputParam("@o_causal_seguro", ICTSTypes.SQLVARCHAR, "0000000000000000");
		wProcedureRequest.addOutputParam("@o_simbolo_mon", ICTSTypes.SQLVARCHAR, "XXXXXXXXXX");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(wProcedureRequest);

		aBagSPJavaOrchestration.put("o_categoria", wProductsQueryResp.readValueParam("@o_categoria"));
		aBagSPJavaOrchestration.put("o_numero", wProductsQueryResp.readValueParam("@o_numero"));
		aBagSPJavaOrchestration.put("o_tipo_solicitud", wProductsQueryResp.readValueParam("@o_tipo_solicitud"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking grabarSolicitud: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de grabarSolicitud");
		}

		return wProductsQueryResp;
	}
	
	protected IProcedureResponse aprobacionDefault(Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Inicia aprobacionDefault");
		}

		ProcedureRequestAS wProcedureRequest = new ProcedureRequestAS();
		wProcedureRequest.setSpName("cob_atm..sp_atm_aprobacion_default");
		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		wProcedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, "18503");
		
		wProcedureRequest.addInputParam("@i_categoria", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_categoria").toString());
		wProcedureRequest.addInputParam("@i_num_solicitud", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_numero").toString());
		wProcedureRequest.addInputParam("@i_tipo_solicitud",ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_tipo_solicitud").toString());
		wProcedureRequest.addInputParam("@i_motivo", ICTSTypes.SQLVARCHAR, "ETI");
		wProcedureRequest.addInputParam("@i_chip", ICTSTypes.SQLVARCHAR, "N");
		
		wProcedureRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		wProcedureRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0.0.0.0");
		wProcedureRequest.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, "");
		wProcedureRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, "1");
		
		wProcedureRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "18503");
		
		wProcedureRequest.addOutputParam("@o_cod_mascara", ICTSTypes.SQLVARCHAR, "000000000000000000000000");
		wProcedureRequest.addOutputParam("@o_tarjeta", ICTSTypes.SQLINT4, "0");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(wProcedureRequest);

		aBagSPJavaOrchestration.put("o_categoria", wProductsQueryResp.readValueParam("@o_categoria"));
		aBagSPJavaOrchestration.put("o_numero", wProductsQueryResp.readValueParam("@o_numero"));
		aBagSPJavaOrchestration.put("o_tipo_solicitud", wProductsQueryResp.readValueParam("@o_tipo_solicitud"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking aprobacionDefault: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de aprobacionDefault");
		}

		return wProductsQueryResp;
	}
	
	protected IProcedureResponse getCustomerCardByAccount(Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Inicia getCustomerCardByAccount");
		}

		ProcedureRequestAS wProcedureRequest = new ProcedureRequestAS();
		wProcedureRequest.setSpName("cob_atm..sp_atm_cons_tarjeta");
		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		wProcedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, "16520");
		wProcedureRequest.addInputParam("@i_valor", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_account_number").toString());
		wProcedureRequest.addInputParam("@i_valortiny", ICTSTypes.SQLINT2, "4");
		wProcedureRequest.addInputParam("@i_tipo",ICTSTypes.SQLINT2, "6");
		wProcedureRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		wProcedureRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0.0.0.0");
		wProcedureRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, "1");

		wProcedureRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "16520");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(wProcedureRequest);

		if(!wProductsQueryResp.getResultSetRowColumnData(1, 1, 1).isNull())
		{	
			logger.logInfo(CLASS_NAME + " Data(1, 1, 1) num_tarj" + wProductsQueryResp.getResultSetRowColumnData(1, 1, 1).getValue());
			logger.logInfo(CLASS_NAME + " Data(1, 1, 2) mask" + wProductsQueryResp.getResultSetRowColumnData(1, 1, 2).getValue());
			aBagSPJavaOrchestration.put("o_card_id", wProductsQueryResp.getResultSetRowColumnData(1, 1, 1).getValue());
			aBagSPJavaOrchestration.put("o_mask", wProductsQueryResp.getResultSetRowColumnData(1, 1, 2).getValue());
		}
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking getCustomerCardByAccount(Map<String, Object>): " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getCustomerCardByAccount(Map<String, Object>)");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse getActivationState(Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Inicia getActivationState");
		}

		ProcedureRequestAS wProcedureRequest = new ProcedureRequestAS();
		wProcedureRequest.setSpName("cob_bvirtual..sp_activa_tarjeta");
		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);

		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, "18500047");
		wProcedureRequest.addInputParam("@i_tarjeta", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_card_id").toString());
		wProcedureRequest.addInputParam("@i_cliente", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("ente_mis").toString());
		wProcedureRequest.addInputParam("@i_operacion",ICTSTypes.SQLCHAR, "Q");
		wProcedureRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		wProcedureRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0.0.0.0");
		wProcedureRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, "1");

		wProcedureRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "18500047");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(wProcedureRequest);

		logger.logInfo(CLASS_NAME + " Inicia getActivationState");
		if(wProductsQueryResp.getReturnCode() == 0)
		{	
			logger.logInfo(CLASS_NAME + " Data(1, 1, 1) cliente_cv" + wProductsQueryResp.getResultSetRowColumnData(1, 1, 1).getValue());
			logger.logInfo(CLASS_NAME + " Data(1, 1, 2) tarjeta_cv" + wProductsQueryResp.getResultSetRowColumnData(1, 1, 2).getValue());
			aBagSPJavaOrchestration.put("o_cliente_cv", wProductsQueryResp.getResultSetRowColumnData(1, 1, 1).getValue());
			aBagSPJavaOrchestration.put("o_tarjeta_cv", wProductsQueryResp.getResultSetRowColumnData(1, 1, 2).getValue());
		}
		else{
			wProductsQueryResp = null;
		}
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking getActivationState: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getActivationState");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse setActivationState(Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Inicia setActivationState");
		}

		ProcedureRequestAS wProcedureRequest = new ProcedureRequestAS();
		wProcedureRequest.setSpName("cob_bvirtual..sp_activa_tarjeta");
		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);

		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, "18500047");
		wProcedureRequest.addInputParam("@i_tarjeta", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_card_id").toString());
		wProcedureRequest.addInputParam("@i_cliente", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("ente_mis").toString());
		wProcedureRequest.addInputParam("@i_operacion",ICTSTypes.SQLCHAR, "I");
		wProcedureRequest.addInputParam("@i_modo",ICTSTypes.SQLINT2, "0");
		wProcedureRequest.addInputParam("@i_cacao_actua",ICTSTypes.SQLCHAR, "S");
		wProcedureRequest.addInputParam("@i_cacao_activ",ICTSTypes.SQLCHAR, "S");
		
		wProcedureRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		wProcedureRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0.0.0.0");
		wProcedureRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, "1");

		wProcedureRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "18500047");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(wProcedureRequest);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking setActivationState: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de setActivationState");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse insertCardActivationApplication(Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Inicia insertCardActivationApplication");
		}

		ProcedureRequestAS wProcedureRequest = new ProcedureRequestAS();
		wProcedureRequest.setSpName("cob_atm..sp_atm_sol_activacion");
		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);

		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, "16759");
		wProcedureRequest.addInputParam("@i_grupo1", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_mask").toString() 
				+ "@" + aBagSPJavaOrchestration.get("o_card_id").toString() + "@");
		wProcedureRequest.addInputParam("@i_oficina", ICTSTypes.SQLINT4, "1");
		wProcedureRequest.addInputParam("@i_operacion",ICTSTypes.SQLCHAR, "I");
		wProcedureRequest.addInputParam("@i_tipo",ICTSTypes.SQLINT2, "0");
		
		wProcedureRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		wProcedureRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, "0.0.0.0");
		//wProcedureRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, "1");

		wProcedureRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "16759");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(wProcedureRequest);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking insertCardActivationApplication: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de insertCardActivationApplication");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse activateCardAtm(Map<String, Object> aBagSPJavaOrchestration) {

		ProcedureRequestAS request = new ProcedureRequestAS();
		
		final String METHOD_NAME = "[activateCardAtm]";

		logger.logInfo(METHOD_NAME + "INICIA INVOCACION");

		IProcedureResponse connectorSpeiResponse = null;

		request.addInputParam("@i_util_sobre", ICTSTypes.SQLCHAR, "S");
		request.addInputParam("@i_tarjeta", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_card_id").toString());
		request.addInputParam("@i_servicio", ICTSTypes.SQLVARCHAR, "MAN");
		request.addInputParam("@i_proceso", ICTSTypes.SQLVARCHAR, "ENA");
		request.addInputParam("@i_operacion_pin", ICTSTypes.SQLINT2, "0");
		request.addInputParam("@i_oficina", ICTSTypes.SQLINT2, "1");
		request.addInputParam("@i_motivo", ICTSTypes.SQLVARCHAR, "EMI");
		request.addInputParam("@i_comentario", ICTSTypes.SQLVARCHAR, "ACTIVACION API");
		request.addInputParam("@i_banco", ICTSTypes.SQLINT2, "1");

		request.setSpName("cob_procesador..sp_exec_atm_activacion");
		// request.addFieldInHeader(ICOBISTS.HEADER_PROCESS_DATE,
		// ICOBISTS.HEADER_DATE_TYPE, forma.format(fecha));
		request.addFieldInHeader(ICOBISTS.HEADER_MESSAGE_TYPE, ICOBISTS.HEADER_STRING_TYPE, "ProcedureRequest");
		request.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
				"(service.identifier=cob_procesador..sp_exec_atm_activacion)");
		request.addFieldInHeader(ICOBISTS.HEADER_SOURCE, ICOBISTS.HEADER_NUMBER_TYPE, "13");
		request.addFieldInHeader(ICOBISTS.HEADER_TROL, ICOBISTS.HEADER_NUMBER_TYPE, "96");
		request.addFieldInHeader(ICOBISTS.HEADER_LOGIN, ICOBISTS.HEADER_STRING_TYPE, "COBISBV"); // *
		request.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "executeTransaction");
		request.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, ""); // *
		request.addFieldInHeader("rol", ICOBISTS.HEADER_NUMBER_TYPE, "96");
		// request.addFieldInHeader("ssn", ICOBISTS.HEADER_NUMBER_TYPE,
		// request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("originalRequestIsCobProcesador", ICOBISTS.HEADER_STRING_TYPE, "true");
		// request.addFieldInHeader("ssnLog", ICOBISTS.HEADER_NUMBER_TYPE,
		// request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("sesn", ICOBISTS.HEADER_NUMBER_TYPE, "0");
		request.addFieldInHeader("authorizationService", ICOBISTS.HEADER_STRING_TYPE, "COBISBV");
		request.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
		request.addFieldInHeader("supportOffline", ICOBISTS.HEADER_CHARACTER_TYPE, "N");
		request.addFieldInHeader("term", ICOBISTS.HEADER_STRING_TYPE, "0:0:0:0:0:0:0:1");
		request.addFieldInHeader("serviceId", ICOBISTS.HEADER_STRING_TYPE,
				"InternetBanking.WebApp.Enquiries.Service.Enquiries.ExecuteCardActivation");
		request.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingOrchestrator");
		request.addFieldInHeader("filial", ICOBISTS.HEADER_NUMBER_TYPE, "1");
		request.addFieldInHeader("servicio", ICOBISTS.HEADER_NUMBER_TYPE, "8");
		request.addFieldInHeader("org", ICOBISTS.HEADER_STRING_TYPE, "U");
		request.addFieldInHeader("contextId", ICOBISTS.HEADER_STRING_TYPE, "COBISBV");
		// request.addFieldInHeader("sessionId", ICOBISTS.HEADER_STRING_TYPE,
		// "S");
		request.addFieldInHeader("serviceName", ICOBISTS.HEADER_STRING_TYPE, "cob_procesador..sp_exec_atm_activacion");
		request.addFieldInHeader("perfil", ICOBISTS.HEADER_NUMBER_TYPE, "13");
		// request.addFieldInHeader("ssn_branch", ICOBISTS.HEADER_NUMBER_TYPE,
		// request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("target", ICOBISTS.HEADER_STRING_TYPE, "SPExecutor");

		request.addFieldInHeader("trn", ICOBISTS.HEADER_NUMBER_TYPE, "16061");
		request.addFieldInHeader("ofi", ICOBISTS.HEADER_NUMBER_TYPE, "1");
		// request.addFieldInHeader("serviceExecutionId",
		// ICOBISTS.HEADER_STRING_TYPE, request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
		request.addFieldInHeader("srv", ICOBISTS.HEADER_STRING_TYPE, "BRANCHSRV");
		request.addFieldInHeader("culture", ICOBISTS.HEADER_STRING_TYPE, "ES-EC");
		request.addFieldInHeader("spType", ICOBISTS.HEADER_STRING_TYPE, "Sybase");
		request.addFieldInHeader("lsrv", ICOBISTS.HEADER_STRING_TYPE, "CTSSRV");
		request.addFieldInHeader("user", ICOBISTS.HEADER_STRING_TYPE, "usuariobv");

		request.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "16061");
		request.addFieldInHeader("trn_origen", ICOBISTS.HEADER_STRING_TYPE, "API_IN");

		logger.logInfo("REQUEST activateCardAtm" + request);

		connectorSpeiResponse = executeCoreBanking(request);

		logger.logInfo(METHOD_NAME + "TERMINA ORQUESTRATOR activateCardAtm");

		logger.logInfo(METHOD_NAME + "RESPONSE activateCardAtm: " + connectorSpeiResponse);

		logger.logInfo(METHOD_NAME + "CODE RETURN activateCardAtm " + connectorSpeiResponse.getReturnCode());
		
		return connectorSpeiResponse;

	}
	
	private IProcedureResponse getCardDockAviable(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getCardDockAviable");
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
		request.addOutputParam("@o_status_atm", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_card_available", ICTSTypes.SQLVARCHAR, "X");

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		aBagSPJavaOrchestration.put("o_id_card_dock", wProductsQueryResp.readValueParam("@o_id_card_dock"));
		aBagSPJavaOrchestration.put("o_detail_status", wProductsQueryResp.readValueParam("@o_detail_status"));
		aBagSPJavaOrchestration.put("o_det_reason_stat", wProductsQueryResp.readValueParam("@o_det_reason_stat"));
		aBagSPJavaOrchestration.put("o_account_number", wProductsQueryResp.readValueParam("@o_account"));
		aBagSPJavaOrchestration.put("o_type_card", wProductsQueryResp.readValueParam("@o_type_card"));
		aBagSPJavaOrchestration.put("o_status_atm", wProductsQueryResp.readValueParam("@o_status_atm"));
		aBagSPJavaOrchestration.put("o_card_available", wProductsQueryResp.readValueParam("@o_card_available"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking getCardDockAviable: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getCardDockAviable");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse assingCardAtm(Map<String, Object> aBagSPJavaOrchestration) {

		ProcedureRequestAS request = new ProcedureRequestAS();
		
		final String METHOD_NAME = "[activateCardAtm]";

		logger.logInfo(METHOD_NAME + "INICIA INVOCACION");

		IProcedureResponse connectorSpeiResponse = null;

		request.addInputParam("@i_util_sobre", ICTSTypes.SQLCHAR, "S");
		request.addInputParam("@i_tarjeta", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_card_id").toString());
		request.addInputParam("@i_servicio", ICTSTypes.SQLVARCHAR, "MAN");
		request.addInputParam("@i_proceso", ICTSTypes.SQLVARCHAR, "ENA");
		request.addInputParam("@i_operacion_pin", ICTSTypes.SQLINT2, "0");
		request.addInputParam("@i_oficina", ICTSTypes.SQLINT2, "1");
		request.addInputParam("@i_motivo", ICTSTypes.SQLVARCHAR, "EMI");
		request.addInputParam("@i_comentario", ICTSTypes.SQLVARCHAR, "ACTIVACION API");
		request.addInputParam("@i_banco", ICTSTypes.SQLINT2, "1");

		request.setSpName("cob_procesador..sp_exec_atm_activacion");
		// request.addFieldInHeader(ICOBISTS.HEADER_PROCESS_DATE,
		// ICOBISTS.HEADER_DATE_TYPE, forma.format(fecha));
		request.addFieldInHeader(ICOBISTS.HEADER_MESSAGE_TYPE, ICOBISTS.HEADER_STRING_TYPE, "ProcedureRequest");
		request.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
				"(service.identifier=cob_procesador..sp_exec_atm_activacion)");
		request.addFieldInHeader(ICOBISTS.HEADER_SOURCE, ICOBISTS.HEADER_NUMBER_TYPE, "13");
		request.addFieldInHeader(ICOBISTS.HEADER_TROL, ICOBISTS.HEADER_NUMBER_TYPE, "96");
		request.addFieldInHeader(ICOBISTS.HEADER_LOGIN, ICOBISTS.HEADER_STRING_TYPE, "COBISBV"); // *
		request.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "executeTransaction");
		request.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, ""); // *
		request.addFieldInHeader("rol", ICOBISTS.HEADER_NUMBER_TYPE, "96");
		// request.addFieldInHeader("ssn", ICOBISTS.HEADER_NUMBER_TYPE,
		// request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("originalRequestIsCobProcesador", ICOBISTS.HEADER_STRING_TYPE, "true");
		// request.addFieldInHeader("ssnLog", ICOBISTS.HEADER_NUMBER_TYPE,
		// request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("sesn", ICOBISTS.HEADER_NUMBER_TYPE, "0");
		request.addFieldInHeader("authorizationService", ICOBISTS.HEADER_STRING_TYPE, "COBISBV");
		request.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
		request.addFieldInHeader("supportOffline", ICOBISTS.HEADER_CHARACTER_TYPE, "N");
		request.addFieldInHeader("term", ICOBISTS.HEADER_STRING_TYPE, "0:0:0:0:0:0:0:1");
		request.addFieldInHeader("serviceId", ICOBISTS.HEADER_STRING_TYPE,
				"InternetBanking.WebApp.Enquiries.Service.Enquiries.ExecuteCardActivation");
		request.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingOrchestrator");
		request.addFieldInHeader("filial", ICOBISTS.HEADER_NUMBER_TYPE, "1");
		request.addFieldInHeader("servicio", ICOBISTS.HEADER_NUMBER_TYPE, "8");
		request.addFieldInHeader("org", ICOBISTS.HEADER_STRING_TYPE, "U");
		request.addFieldInHeader("contextId", ICOBISTS.HEADER_STRING_TYPE, "COBISBV");
		// request.addFieldInHeader("sessionId", ICOBISTS.HEADER_STRING_TYPE,
		// "S");
		request.addFieldInHeader("serviceName", ICOBISTS.HEADER_STRING_TYPE, "cob_procesador..sp_exec_atm_activacion");
		request.addFieldInHeader("perfil", ICOBISTS.HEADER_NUMBER_TYPE, "13");
		// request.addFieldInHeader("ssn_branch", ICOBISTS.HEADER_NUMBER_TYPE,
		// request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("target", ICOBISTS.HEADER_STRING_TYPE, "SPExecutor");

		request.addFieldInHeader("trn", ICOBISTS.HEADER_NUMBER_TYPE, "16061");
		request.addFieldInHeader("ofi", ICOBISTS.HEADER_NUMBER_TYPE, "1");
		// request.addFieldInHeader("serviceExecutionId",
		// ICOBISTS.HEADER_STRING_TYPE, request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
		request.addFieldInHeader("srv", ICOBISTS.HEADER_STRING_TYPE, "BRANCHSRV");
		request.addFieldInHeader("culture", ICOBISTS.HEADER_STRING_TYPE, "ES-EC");
		request.addFieldInHeader("spType", ICOBISTS.HEADER_STRING_TYPE, "Sybase");
		request.addFieldInHeader("lsrv", ICOBISTS.HEADER_STRING_TYPE, "CTSSRV");
		request.addFieldInHeader("user", ICOBISTS.HEADER_STRING_TYPE, "usuariobv");

		request.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "16061");
		request.addFieldInHeader("trn_origen", ICOBISTS.HEADER_STRING_TYPE, "API_IN");

		logger.logInfo("REQUEST activateCardAtm" + request);

		connectorSpeiResponse = executeCoreBanking(request);

		logger.logInfo(METHOD_NAME + "TERMINA ORQUESTRATOR activateCardAtm");

		logger.logInfo(METHOD_NAME + "RESPONSE activateCardAtm: " + connectorSpeiResponse);

		logger.logInfo(METHOD_NAME + "CODE RETURN activateCardAtm " + connectorSpeiResponse.getReturnCode());
		
		return connectorSpeiResponse;

	}
	
}
