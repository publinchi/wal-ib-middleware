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

		IProcedureResponse wAccountsResp = new ProcedureResponseAS();

		wAccountsResp = getDataCardDock(aRequest, aBagSPJavaOrchestration);

		/*
		logger.logInfo(
				CLASS_NAME + " SetRowColumnData xdc: " + wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue());
		if (wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")) {
			IProcedureResponse wAccountsRespInsert = new ProcedureResponseAS();
			wAccountsRespInsert = createCardApplication(aRequest, aBagSPJavaOrchestration);

			return wAccountsRespInsert;
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Response " + wAccountsResp.toString());
			logger.logInfo(CLASS_NAME + " Saliendo de updaterCardStatus");
		}
*/
		return wAccountsResp;
	}
	
	private IProcedureResponse createCardApplication(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse connectorCardResponse = null;
		String curp = null, fullName = null, birthDate = null, codDocument = null, acccountNumber = null;
		
		fullName = aBagSPJavaOrchestration.containsKey("o_full_name")? aBagSPJavaOrchestration.get("o_full_name").toString():null;
		curp = aBagSPJavaOrchestration.containsKey("o_curp")? aBagSPJavaOrchestration.get("o_curp").toString():null;
		birthDate = aBagSPJavaOrchestration.containsKey("o_birth_date")? aBagSPJavaOrchestration.get("o_birth_date").toString():null;
		codDocument = aBagSPJavaOrchestration.containsKey("o_cod_document")? aBagSPJavaOrchestration.get("o_cod_document").toString():null;
		acccountNumber = aBagSPJavaOrchestration.containsKey("o_account_number")? aBagSPJavaOrchestration.get("o_account_number").toString():null;
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en createCardApplication " + acccountNumber);
		}
		try {
			// PARAMETROS DE ENTRADA
			anOriginalRequest.addInputParam("@i_ente", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_externalCustomerId"));
			anOriginalRequest.addInputParam("@i_curp", ICTSTypes.SQLVARCHAR, curp);
			anOriginalRequest.addInputParam("@i_full_name", ICTSTypes.SQLVARCHAR, fullName);
			anOriginalRequest.addInputParam("@i_birth_date", ICTSTypes.SQLVARCHAR, birthDate);
			anOriginalRequest.addInputParam("@i_cod_document", ICTSTypes.SQLVARCHAR, codDocument);
			anOriginalRequest.addInputParam("@i_account_number", ICTSTypes.SQLVARCHAR, acccountNumber);			
			
			if(null!=(anOriginalRequest.readValueParam("@i_org_eje"))){
				anOriginalRequest.addInputParam("@i_mode_create", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_mode_create"));

			}
			else{
				anOriginalRequest.addInputParam("@i_account_id", ICTSTypes.SQLVARCHAR, null);
				anOriginalRequest.addInputParam("@i_person_id", ICTSTypes.SQLVARCHAR, null);
				anOriginalRequest.addInputParam("@i_card_id", ICTSTypes.SQLVARCHAR, null);
				anOriginalRequest.addInputParam("@i_mode_create", ICTSTypes.SQLVARCHAR, MODE_OPERATION);
				anOriginalRequest.addInputParam("@i_estado_flujo", ICTSTypes.SQLVARCHAR, CREATE_PERSON);
				if(!aBagSPJavaOrchestration.get("o_account_id_dock").equals("X")){
					anOriginalRequest.addInputParam("@i_estado_flujo", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_flujo_dock").toString());
					anOriginalRequest.addInputParam("@i_person_id", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_person_id_dock").toString());
					anOriginalRequest.addInputParam("@i_account_id", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_account_id_dock").toString());					
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

		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		aBagSPJavaOrchestration.put("o_id_card_dock", wProductsQueryResp.readValueParam("@o_id_card_dock"));
		aBagSPJavaOrchestration.put("o_detail_status", wProductsQueryResp.readValueParam("@o_detail_status"));
		aBagSPJavaOrchestration.put("o_det_reason_stat", wProductsQueryResp.readValueParam("@o_det_reason_stat"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getDataCardDock");
		}

		return wProductsQueryResp;
	}

	private IProcedureResponse getDataClientRegister(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getDataClientRegister");
		}

		request.setSpName("cob_atm..sp_get_data_reg_client_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_externalCustomerId"));
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_accountNumber"));
		request.addOutputParam("@o_account", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_person_id", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_account_id", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_flujo_dock", ICTSTypes.SQLVARCHAR, "X");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		aBagSPJavaOrchestration.put("o_account_number", wProductsQueryResp.readValueParam("@o_account"));
		aBagSPJavaOrchestration.put("o_person_id_dock", wProductsQueryResp.readValueParam("@o_person_id"));
		aBagSPJavaOrchestration.put("o_account_id_dock", wProductsQueryResp.readValueParam("@o_account_id"));
		aBagSPJavaOrchestration.put("o_flujo_dock", wProductsQueryResp.readValueParam("@o_flujo_dock"));		
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getDataClientRegister");
		}

		return wProductsQueryResp;
	}

	public IProcedureResponse processResponseCardAppl(IProcedureResponse anOriginalProcedureRes) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(" start processResponseAccounts--->");
		}

		IProcedureResponse anOriginalProcedureResponse = new ProcedureResponseAS();
		String code,message,success,referenceCode;
		Integer codeReturn = anOriginalProcedureRes.getReturnCode();
		referenceCode = anOriginalProcedureRes.readValueParam("@o_referencia");
		logger.logInfo("xdcxv2 --->" + referenceCode );
		logger.logInfo("xdcxv3 --->" + codeReturn );
		if (codeReturn == 0){
			if(null!=referenceCode) {
				code = "0";
				message = "Success";
				success = "true";
				referenceCode = anOriginalProcedureRes.readValueParam("@o_referencia");
			}
			else{
				code = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).getValue();
				message = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).getValue();
				success = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue();
			}
			
		}
		else
		{
			code = String.valueOf(codeReturn);
			message = anOriginalProcedureRes.getMessage(1).getMessageText();
			success = "false";
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

		// Agregar Header y data 3
		IResultSetHeader metaData3 = new ResultSetHeader();
		IResultSetData data3 = new ResultSetData();
		// Agregar resulBlock
		IResultSetBlock resultsetBlock3 = new ResultSetBlock(metaData3, data3);
		
		if (referenceCode != null) {
			
			metaData3.addColumnMetaData(new ResultSetHeaderColumn("referenceCode", ICTSTypes.SQLINTN, 5));

			// Agregar info 3
			IResultSetRow row3 = new ResultSetRow();
			row3.addRowData(1, new ResultSetRowColumnData(false,
					anOriginalProcedureRes.readValueParam("@o_referencia").toString().trim()));
			data3.addRow(row3);			
		}

		anOriginalProcedureResponse.setReturnCode(200);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock2);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock3);
		

		return anOriginalProcedureResponse;
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

		request.addInputParam("@i_tarjeta_id", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_card_id"));
		request.addInputParam("@i_tipo_tarjeta", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_card_type"));
		request.addInputParam("@i_request_td", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_requestCreateCard"));
		request.addInputParam("@i_response_td", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_responseCreateCard"));

		request.addInputParam("@i_persona_id", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_person_id"));
		request.addInputParam("@i_request_pd", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_requestCreatePerson"));
		request.addInputParam("@i_response_pd", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_responseCreatePerson"));

		request.addInputParam("@i_cuenta_id", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_account_id"));
		request.addInputParam("@i_request_cd", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_requestCreateAccount"));
		request.addInputParam("@i_response_cd", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_responseCreateAccount"));

		request.addInputParam("@i_asig_date", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_assign_date"));
		request.addInputParam("@i_request_ad", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_requestAssingCard"));
		request.addInputParam("@i_response_ad", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_responseAssingCard"));

		if(!reponseCard.getResultSetRowColumnData(2, 1, 2).isNull() && null==reponseCard.readValueParam("@o_responseCreatePerson"))
		request.addInputParam("@i_response_pd", ICTSTypes.SQLVARCHAR, reponseCard.getResultSetRowColumnData(2, 1, 2).getValue());
		
		
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
		metaData.addColumnMetaData(new ResultSetHeaderColumn("cardId", ICTSTypes.SYBINT4, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("personId", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("accountId", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("assignmentDate", ICTSTypes.SYBINT4, 255));
		
		if (codeReturn == 0) {
			
		String flag = null;
		if(aBagSPJavaOrchestration.containsKey("o_assign_date")){
			flag = aBagSPJavaOrchestration.get("o_assign_date").toString();
		}
		
		logger.logDebug("response conector: " + anOriginalProcedureRes.toString());
		logger.logDebug("code o_assign_date: " + flag);
		logger.logDebug("retunr code response: " + anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1));
		
			if(flag!=null){
				logger.logDebug("Ending flow, processResponse success with code: " + keyList.get(0));
				row.addRowData(1, new ResultSetRowColumnData(false, "true"));
				row.addRowData(2, new ResultSetRowColumnData(false, "0"));
				row.addRowData(3, new ResultSetRowColumnData(false, "Success"));
				row.addRowData(4, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get("o_card_id").toString()));
				row.addRowData(5, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get("o_person_id").toString()));
				row.addRowData(6, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get("o_account_id").toString()));
				row.addRowData(7, new ResultSetRowColumnData(false, aBagSPJavaOrchestration.get("o_assign_date").toString()));
				data.addRow(row);
			}
			else{
				logger.logDebug("Ending flow, processResponse error");
				
				String message = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).getValue();
				String success = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue();
				String code = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).getValue();
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
			row.addRowData(4, new ResultSetRowColumnData(false, null));
			row.addRowData(5, new ResultSetRowColumnData(false, null));
			row.addRowData(6, new ResultSetRowColumnData(false, null));
			row.addRowData(7, new ResultSetRowColumnData(false, null));
			data.addRow(row);
		}
		
		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
		wProcedureResponse.addResponseBlock(resultBlock);			
		
		registerLogBd(anOriginalProcedureRes, aBagSPJavaOrchestration);
		return wProcedureResponse;		
	}
}
