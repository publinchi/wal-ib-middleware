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
		@Property(name = "service.spName", value = "cob_procesador..sp_get_data_account_status_api")})
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
	}
	
	private IProcedureResponse updateAccountStatusDock(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en updateAccountStatusDock: ");
		}
		aBagSPJavaOrchestration.put("externalCustomerId", aRequest.readValueParam("@i_external_customer_id"));
		aBagSPJavaOrchestration.put("accountStatus", aRequest.readValueParam("@i_account_status"));
		aBagSPJavaOrchestration.put("accountNumber", aRequest.readValueParam("@i_account_number"));
		aBagSPJavaOrchestration.put("blockingValue", aRequest.readValueParam("@i_blockingValue"));
		aBagSPJavaOrchestration.put("period", aRequest.readValueParam("@i_period"));
		String accountStatus = aRequest.readValueParam("@i_account_status");
	
		IProcedureResponse wAccountsResp = new ProcedureResponseAS();
		
		wAccountsResp = valDataCentral(aRequest, aBagSPJavaOrchestration);
		
		logger.logInfo(CLASS_NAME + " code resp account dock: " + wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue());
		if (wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			
			if (accountStatus.trim().equals("BV") || accountStatus.trim().equals("BM"))
			{
				return wAccountsResp;
			}
			IProcedureResponse wAccountsValDataLocal = new ProcedureResponseAS();
			wAccountsValDataLocal = valDataLocal(aRequest, aBagSPJavaOrchestration);
			
			if (wAccountsValDataLocal.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")) {
				
				IProcedureResponse wAccountStatusCobAhorros = new ProcedureResponseAS();
				wAccountStatusCobAhorros = updateAccountStatusCobAhorros(aRequest, aBagSPJavaOrchestration);
				
				if (wAccountStatusCobAhorros.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")) {
				
					IProcedureResponse wAccountsRespConector = new ProcedureResponseAS();
					wAccountsRespConector = updateAccountStatusDockConector(aRequest, aBagSPJavaOrchestration);
					
					return wAccountsRespConector;
					
				} else {
					
					return wAccountStatusCobAhorros; 
				}
				
			} else {
				
				return wAccountsValDataLocal;
			}
			
		}
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Response " + wAccountsResp.toString());
			logger.logInfo(CLASS_NAME + " Saliendo de updateAccountStatusDock");
		}

		return wAccountsResp;
	}
	
	private IProcedureResponse valDataCentral(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en valData");
		}

		request.setSpName("cob_ahorros..sp_val_acc_status_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_external_customer_id"));
		request.addInputParam("@i_accountStatus", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account_status"));
		request.addInputParam("@i_accountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account_number"));
		request.addInputParam("@i_blockingValue", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_blockingValue"));
		request.addInputParam("@i_period", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_period"));
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de valData");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse valDataLocal(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en valData");
		}

		request.setSpName("cob_atm..sp_val_data_account_status_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_external_customer_id", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_external_customer_id"));
		request.addInputParam("@i_account_status", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account_status"));
		request.addInputParam("@i_account", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account_number"));
		
		request.addOutputParam("@o_account_dock_id", ICTSTypes.SQLVARCHAR, "X");		
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("account dock(1) id es " +  wProductsQueryResp.readValueParam("@o_account_dock_id"));
		}
		
		aBagSPJavaOrchestration.put("accountDockId", wProductsQueryResp.readValueParam("@o_account_dock_id"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de valData");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse updateAccountStatusCobAhorros(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en updateAccountStatusCobAhorros");
		}
		
		request.setSpName("cob_ahorros..sp_u_acc_status_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_external_customer_id"));
		request.addInputParam("@i_accountStatus", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account_status"));
		request.addInputParam("@i_accountNumber", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account_number"));
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de valData");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse updateAccountStatusDockConector(IProcedureRequest anOriginalReq, Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureResponse connectorAccountResponse = null;
		String accountDockId = null;
		
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		aBagSPJavaOrchestration.remove("trn_virtual");
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("account dock(2) id es " + aBagSPJavaOrchestration.get("accountDockId").toString());
		}
		
		accountDockId = aBagSPJavaOrchestration.containsKey("accountDockId")? aBagSPJavaOrchestration.get("accountDockId").toString():null;;
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en execute AccountStatus update ");
		}
		try {
			// PARAMETROS DE ENTRADA
			anOriginalRequest.addInputParam("@i_ente", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("externalCustomerId").toString());
			anOriginalRequest.addInputParam("@i_account_dock_id", ICTSTypes.SQLVARCHAR, accountDockId);
			anOriginalRequest.addInputParam("@i_status", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("accountStatus").toString());
			anOriginalRequest.addInputParam("@i_operation", ICTSTypes.SQLVARCHAR, "UAS");
			
			// VARIABLES DE SALIDA
			anOriginalRequest.addOutputParam("@o_cod_respuesta", ICTSTypes.SQLVARCHAR, "0");
			anOriginalRequest.addOutputParam("@o_desc_respuesta", ICTSTypes.SQLVARCHAR, "X");
			
			anOriginalRequest.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
					"(service.identifier=UpdateAccountStatusDockOrchestrationCore)");
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
			anOriginalRequest.setSpName("cob_procesador..sp_account_status_api");

			anOriginalRequest.addInputParam("@trn_virtual", ICTSTypes.SYBINT4, "18500112");
			anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500112");

			logger.logDebug("accountDock--> request update account status: " + anOriginalRequest.toString());
			
			
			// SE EJECUTA CONECTOR
			connectorAccountResponse = executeProvider(anOriginalRequest, aBagSPJavaOrchestration);

			if (logger.isDebugEnabled())
				logger.logDebug("connectorUpdateAccountStatusResponse: " + connectorAccountResponse);

			if (connectorAccountResponse.readValueParam("@o_account_id") != null)
				aBagSPJavaOrchestration.put("accountId", connectorAccountResponse.readValueParam("@o_account_id"));
			else
				aBagSPJavaOrchestration.put("accountId", "null");

			if (connectorAccountResponse.readValueParam("@o_success") != null)
				aBagSPJavaOrchestration.put("success", connectorAccountResponse.readValueParam("@o_account_id"));
			
		} catch (Exception e) {
			e.printStackTrace();
			connectorAccountResponse = null;
			logger.logInfo(CLASS_NAME +" Error Catastrofico de updateAccountStatusExecution");

		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + "--> updateAccountStatusExecution");
			}
		}

		return connectorAccountResponse	;

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
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("accountNumber").toString());
		request.addInputParam("@i_fecha_reg", ICTSTypes.SQLVARCHAR, fechaActual);
		request.addInputParam("@i_fecha_mod", ICTSTypes.SQLVARCHAR, fechaActual);
		request.addInputParam("@i_modo", ICTSTypes.SQLVARCHAR, "UAS");

		request.addInputParam("@i_cuenta_id", ICTSTypes.SQLVARCHAR, reponseAccount.readValueParam("@o_account_id"));
		request.addInputParam("@i_request_cd", ICTSTypes.SQLVARCHAR, reponseAccount.readValueParam("@o_request_update_account"));
		request.addInputParam("@i_estado_cuenta", ICTSTypes.SQLVARCHAR, reponseAccount.readValueParam("@o_account_status"));
		request.addInputParam("@i_estado_upd", ICTSTypes.SQLVARCHAR, reponseAccount.readValueParam("@o_success"));
		
		String message = reponseAccount.readValueParam("@o_response_update_account")!=null?reponseAccount.readValueParam("@o_response_update_account"):reponseAccount.getResultSetRowColumnData(2, 1, 2).getValue();
		request.addInputParam("@i_response_cd", ICTSTypes.SQLVARCHAR, message);
		
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
		return null;
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

		
		if (codeReturn == 0) {
			
		Boolean flag = aBagSPJavaOrchestration.containsKey("success");
		
		logger.logDebug("response conector dock: " + anOriginalProcedureRes.toString());
		logger.logDebug("code o_assign_date: " + flag);
		logger.logDebug("return code response: " + anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1));
		
			if(flag == true){
				logger.logDebug("Ending flow, processResponse success with code: ");
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, "true"));
				data.addRow(row);
				
				IResultSetRow row2 = new ResultSetRow();
				row2.addRowData(1, new ResultSetRowColumnData(false, "0"));
				row2.addRowData(2, new ResultSetRowColumnData(false, "Success"));
				data2.addRow(row2);
				
			} else {
				logger.logDebug("Ending flow, processResponse error");
				
				String success = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).isNull()?"false":anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue();
				String code = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).isNull()?"400218":anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).getValue();
				String message = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).isNull()?"Service execution error":anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).getValue();
				
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
