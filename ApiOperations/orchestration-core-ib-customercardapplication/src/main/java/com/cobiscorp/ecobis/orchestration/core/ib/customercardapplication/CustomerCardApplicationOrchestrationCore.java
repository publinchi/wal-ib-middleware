/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.customercardapplication;

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
 * @author cecheverria
 * @since Sep 2, 2014
 * @version 1.0.0
 */
@Component(name = "CustomerCardApplicationOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "CustomerCardApplicationOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "CustomerCardApplicationOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_card_application_api")
})
public class CustomerCardApplicationOrchestrationCore extends SPJavaOrchestrationBase {// SPJavaOrchestrationBase
	
	private ILogger logger = (ILogger) this.getLogger();
	private static final String CLASS_NAME = "CustomerCardApplicationOrchestrationCore";
	protected static final String CHANNEL_REQUEST = "8";

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
		
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		logger.logDebug("Begin flow, CustomerCardApplication start.");		
		
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		
		
		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();
		
		anProcedureResponse = registerCardApplication(anOriginalRequest, aBagSPJavaOrchestration);
		
		return processResponseApi(anProcedureResponse,aBagSPJavaOrchestration);
		//return processResponseCardAppl(anProcedureResponse);
	}
	
	private IProcedureResponse registerCardApplication(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en registerAccount");
		}
		
		
		IProcedureResponse wAccountsResp = new ProcedureResponseAS();
		
		wAccountsResp = getDataClient(aRequest, aBagSPJavaOrchestration);
		
		if (wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			IProcedureResponse wAccountsRespInsert = new ProcedureResponseAS();
			wAccountsRespInsert = createCardApplication(aRequest, aBagSPJavaOrchestration);
			return wAccountsRespInsert; 
		}
		

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Response " + wAccountsResp.toString());
			logger.logInfo(CLASS_NAME + " Saliendo de registerAccount");
		}

		return wAccountsResp;
	}
	
	private IProcedureResponse createCardApplication(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse connectorCardResponse = null;
		//IProcedureRequest request = new ProcedureRequestAS();
		try {

			// PARAMETROS DE ENTRADA
			anOriginalRequest.addInputParam("@i_ente", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_externalCustomerId"));
			anOriginalRequest.addInputParam("@i_curp", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_curp").toString());
			anOriginalRequest.addInputParam("@i_full_name", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_full_name").toString());
			anOriginalRequest.addInputParam("@i_birth_date", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_birth_date").toString());
			anOriginalRequest.addInputParam("@i_cod_document", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_cod_document").toString());
			anOriginalRequest.addInputParam("@i_account_number", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_account_number").toString());
			anOriginalRequest.addInputParam("@i_mode_create", ICTSTypes.SQLVARCHAR, "PYS");
			
			// VARIABLES DE SALIDA
			anOriginalRequest.addOutputParam("@o_cod_respuesta", ICTSTypes.SQLVARCHAR, "0");
			anOriginalRequest.addOutputParam("@o_desc_respuesta", ICTSTypes.SQLVARCHAR, "X");
			//request.addOutputParam("@o_id_solicitud", ICTSTypes.SQLVARCHAR, "0"); 
			
			anOriginalRequest.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
					"(service.identifier=CustomerCardApplicationOrchestrationCore)");
			anOriginalRequest.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "executeTransaction");
			anOriginalRequest.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, "");
			anOriginalRequest.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequest.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalRequest.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingOrchestrator");
			
			// SE HACE LA LLAMADA AL CONECTOR
			aBagSPJavaOrchestration.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorLighthouse)");
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

	private IProcedureResponse getDataClient(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getDataClient");
		}

		request.setSpName("cobis..sp_get_data_client_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_externalCustomerId"));
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_accountNumber"));
		request.addOutputParam("@o_curp", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_full_name", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_birth_date", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_cod_document", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_account_number", ICTSTypes.SQLVARCHAR, "X");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		aBagSPJavaOrchestration.put("o_curp", wProductsQueryResp.readValueParam("@o_curp"));
		aBagSPJavaOrchestration.put("o_full_name", wProductsQueryResp.readValueParam("@o_full_name"));
		aBagSPJavaOrchestration.put("o_birth_date", wProductsQueryResp.readValueParam("@o_birth_date"));
		aBagSPJavaOrchestration.put("o_cod_document", wProductsQueryResp.readValueParam("@o_cod_document"));
		aBagSPJavaOrchestration.put("o_account_number", wProductsQueryResp.readValueParam("@o_account_number"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getDataClient");
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
		ArrayList<String> keyList = new ArrayList<String>(aBagSPJavaOrchestration.keySet());
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		IResultSetRow row = new ResultSetRow();
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		

		Integer codeReturn = anOriginalProcedureRes.getReturnCode();
		
		logger.logInfo("xdcxv2 --->" + codeReturn );
		
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
		
		logger.logDebug("response xcxcv: " + anOriginalProcedureRes.toString());
		logger.logDebug("code xcxcv: " + flag);
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
		return wProcedureResponse;		
	}
}
