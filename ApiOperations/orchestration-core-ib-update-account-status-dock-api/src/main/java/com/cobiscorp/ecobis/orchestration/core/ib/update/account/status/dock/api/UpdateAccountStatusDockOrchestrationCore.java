/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.update.account.status.dock.api;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
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
		@Property(name = "service.spName", value = "cob_procesador..sp_update_account_status_api")})
public class UpdateAccountStatusDockOrchestrationCore extends SPJavaOrchestrationBase {
	
	private static final String CLASS_NAME = "UpdateAccountStatusDockOrchestrationCore";
	protected static final String CHANNEL_REQUEST = "8";
	protected static final String UPDATE_ACCOUNT_STATUS	= "UPDATE_ACCOUNT_STATUS";
	protected static final String MODE_OPERATION = "PYS";
	
	private ILogger logger = (ILogger) this.getLogger();

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		
		logger.logDebug(CLASS_NAME + "Begin flow, UpdateAccountStatusDock starts executeJavaOrchestration...");		
		
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();
		
		// Cambiar el estado de una cuenta
		anProcedureResponse = updateAccountStatusDock(anOriginalRequest, aBagSPJavaOrchestration);
		
		String accountStatus = String.valueOf(aBagSPJavaOrchestration.get("accountStatus"));
		String blokingValue = String.valueOf(aBagSPJavaOrchestration.get("blockingValue"));
		String changedStateDate =  String.valueOf(aBagSPJavaOrchestration.get("o_changedStateDate"));
		
		// Cambiar el estado de las tarjetas asociadas a la cuenta, si el estado es BM1
		if(accountStatus.equals("BM") && blokingValue.equals("1") && !changedStateDate.equals("null") ) {
			
			logger.logDebug(CLASS_NAME + "Begin flow, UpdateAccountStatusDock starts blocking cards...");		
			IProcedureResponse anProcedureResponse2 = new ProcedureResponseAS();
			
			//TODO: MO - Moving
			//anProcedureResponse2 = getCardsByCustomer(anOriginalRequest, aBagSPJavaOrchestration);
			
			// Agregar entradas invariables para Bloquear tarjeta(s)
			// Datos que ya estan en el request: @i_ente AS @i_external_customer_id,  @i_account_number AS @i_account_number
			aBagSPJavaOrchestration.put("i_ente", anOriginalRequest.readValueParam("@i_external_customer"));
			aBagSPJavaOrchestration.put("i_account_number", anOriginalRequest.readValueParam("@i_account_number"));
			
			// Datos con valores predefinidos
			aBagSPJavaOrchestration.put("i_mode", "X");
			aBagSPJavaOrchestration.put("i_card_status", "B");
			aBagSPJavaOrchestration.put("i_status_reason", "OW");

								
			anProcedureResponse2 = updaterCardStatus(anOriginalRequest, aBagSPJavaOrchestration);
			Integer codeReturn2 = anProcedureResponse2.getReturnCode();
			
			if (codeReturn2 == 0) {
				
				Boolean flag = aBagSPJavaOrchestration.containsKey("o_success");

				logger.logDebug(CLASS_NAME + "[executeJavaOrchestration] response conector dock: " + anProcedureResponse2.toString());
				logger.logDebug(CLASS_NAME + "[executeJavaOrchestration] code o_success: " + flag);
				logger.logDebug(CLASS_NAME + "[executeJavaOrchestration] return code response: " + anProcedureResponse2.getResultSetRowColumnData(2, 1, 1));

				if(flag == true){
					//Imprime si bloqueo de tarjeta es exitoso
					logger.logInfo(CLASS_NAME + "[executeJavaOrchestration] Ending flow, processResponse success with data: ");
					logger.logInfo(CLASS_NAME + "[executeJavaOrchestration] return o_card_available: " + aBagSPJavaOrchestration.get("o_card_available"));
					logger.logInfo(CLASS_NAME + "[executeJavaOrchestration] return o_id_card_dock: " + aBagSPJavaOrchestration.get("o_id_card_dock"));
				
				}
				else {
					//Imprime si bloqueo de tarjeta da algun error
					logger.logDebug(CLASS_NAME + "[executeJavaOrchestration] Ending flow, processResponse error");
					String success = anProcedureResponse2.getResultSetRowColumnData(1, 1, 1).isNull()?"false":anProcedureResponse2.getResultSetRowColumnData(1, 1, 1).getValue();
					String code = anProcedureResponse2.getResultSetRowColumnData(2, 1, 1).isNull()?"400218":anProcedureResponse2.getResultSetRowColumnData(2, 1, 1).getValue();
					String message = anProcedureResponse2.getResultSetRowColumnData(2, 1, 2).isNull()?"Service execution error":anProcedureResponse2.getResultSetRowColumnData(2, 1, 2).getValue();
					
					logger.logWarning(CLASS_NAME + "[executeJavaOrchestration] Ending flow, processResponse : " + anProcedureResponse2.toString());
					logger.logWarning(CLASS_NAME + "[executeJavaOrchestration] Ending flow, processResponse with ErrorData: ");
					logger.logWarning(CLASS_NAME + "[executeJavaOrchestration] return success: " + success);
					logger.logWarning(CLASS_NAME + "[executeJavaOrchestration] return code: " + code );
					logger.logWarning(CLASS_NAME + "[executeJavaOrchestration] return message: " + message );
					
				}
			}
			
			// TODO: Agregar log o response en caso de error
			logger.logInfo(CLASS_NAME + "[executeJavaOrchestration] Terminado updaterCardStatus. "
					+ "Codigo=" + codeReturn2);
		}
		
		return processResponseApi(anProcedureResponse,aBagSPJavaOrchestration, anOriginalRequest);
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
			
			if (accountStatus.trim().equals("BV") || accountStatus.trim().equals("BM") || accountStatus.trim().equals("EBM"))
			{
				aBagSPJavaOrchestration.put("success", "success");
				
				String changedStateDate = wAccountsResp.readValueParam("@o_changedStateDate");
				logger.logInfo(CLASS_NAME + " AccountStatus Changed TO: " + String.valueOf(accountStatus.trim())
						+ ", AT changedStateDate: " + String.valueOf(changedStateDate));
				if (changedStateDate != null)
					aBagSPJavaOrchestration.put("o_changedStateDate", changedStateDate);
				else
					aBagSPJavaOrchestration.put("o_changedStateDate", "null");
				
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
			logger.logInfo(CLASS_NAME + " Entrando en valDataCentral");
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
		
		//Add output params
		request.addOutputParam("@o_changedStateDate", ICTSTypes.SQLVARCHAR, "X");
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
		request.addOutputParam("@o_bv_ente", ICTSTypes.SQLINTN, "0");		
		request.addOutputParam("@o_login", ICTSTypes.SQLVARCHAR, "X");		
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("account dock(1) id es " +  wProductsQueryResp.readValueParam("@o_account_dock_id"));
		}
		
		aBagSPJavaOrchestration.put("accountDockId", wProductsQueryResp.readValueParam("@o_account_dock_id"));
		aBagSPJavaOrchestration.put("bv_ente", wProductsQueryResp.readValueParam("@o_bv_ente"));
		aBagSPJavaOrchestration.put("login", wProductsQueryResp.readValueParam("@o_login"));
		
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
		
		aBagSPJavaOrchestration.put("o_changedStateDate2", fechaActual );

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
	
	public IProcedureResponse processResponseApi(IProcedureResponse anOriginalProcedureRes, Map<String, Object> aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
		
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
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("accountStatusChangeTime", ICTSTypes.SYBVARCHAR, 255));
		
		Boolean flag = aBagSPJavaOrchestration.containsKey("success");
		String accountStatus = aBagSPJavaOrchestration.get("accountStatus").toString();
		Boolean flagSubBloqueo = accountStatus.trim().equals("BV") || accountStatus.trim().equals("BM") || accountStatus.trim().equals("EBM");
		
		if (codeReturn == 0) {
		
		logger.logDebug("response conector dock: " + anOriginalProcedureRes.toString());
		logger.logDebug("code o_assign_date: " + flag);
		logger.logDebug("return code response: " + anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1));
		logger.logDebug("account status: " + String.valueOf(flagSubBloqueo) + "flag sub-bloqueo: " + String.valueOf(flagSubBloqueo));
		
			if(flag == true){
				logger.logDebug("Ending flow, processResponse success with code: ");
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, "true"));
				data.addRow(row);
				
				IResultSetRow row2 = new ResultSetRow();
				row2.addRowData(1, new ResultSetRowColumnData(false, "0"));
				row2.addRowData(2, new ResultSetRowColumnData(false, "Success"));
				data2.addRow(row2);
				
				// Fecha de cambio de estado por un sub-bloqueo
				if (flagSubBloqueo == true)
				{
					logger.logDebug("Ending flow, processResponse success with code: changedStateDate ");
					IResultSetRow row3 = new ResultSetRow();
					String changedStateDate =  aBagSPJavaOrchestration.get("o_changedStateDate").toString();
					row3.addRowData(1, new ResultSetRowColumnData(false, changedStateDate));
					data3.addRow(row3);
				}
				
				sendMail(anOriginalRequest, aBagSPJavaOrchestration);
				
				
			} else {
				logger.logDebug("Ending flow, processResponse error");
				
				String success = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).isNull()?"false":anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue();
				String code = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).isNull()?"400218":anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).getValue();
				String message = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).isNull()?"Service execution error":anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).getValue();
				
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, success));
				data.addRow(row);
				
				if (flagSubBloqueo == true)
				{
				IResultSetRow row2 = new ResultSetRow();
				row2.addRowData(1, new ResultSetRowColumnData(false, code));
				row2.addRowData(2, new ResultSetRowColumnData(false, message));
				data2.addRow(row2);
				}
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
		
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		
		wProcedureResponse.addResponseBlock(resultsetBlock);
		wProcedureResponse.addResponseBlock(resultsetBlock2);
		
		registerLogBd(anOriginalProcedureRes, aBagSPJavaOrchestration);
		
		// Fecha de cambio de estado sub-bloqueo
		if (flag == true && flagSubBloqueo == true) {
			logger.logDebug("Ending flow, processResponse success with code for sub-blocking state");
			IResultSetBlock resultsetBlock3 = new ResultSetBlock(metaData3, data3);
			wProcedureResponse.addResponseBlock(resultsetBlock3);
		} 
		else if (flag == true && flagSubBloqueo == false) // Fecha de cambio de estado general
		{
			logger.logDebug("Ending flow, processResponse success with code for general state");
			IResultSetRow row3 = new ResultSetRow();
			String changedStateDate =  aBagSPJavaOrchestration.get("o_changedStateDate2").toString();
			row3.addRowData(1, new ResultSetRowColumnData(false, changedStateDate));
			data3.addRow(row3);
			IResultSetBlock resultsetBlock3 = new ResultSetBlock(metaData3, data3);
			wProcedureResponse.addResponseBlock(resultsetBlock3);
		}
		else
		{
			logger.logDebug("Ending flow, processResponse error or failed");
			IResultSetRow row3 = new ResultSetRow();
			row3.addRowData(1, new ResultSetRowColumnData(false, "Na"));
			data3.addRow(row3);
			IResultSetBlock resultsetBlock3 = new ResultSetBlock(metaData3, data3);
			wProcedureResponse.addResponseBlock(resultsetBlock3);
		}
		
		
		return wProcedureResponse;		
	}

	private void sendMail(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en sendMail");
		}

		request.setSpName("cob_bvirtual..sp_bv_enviar_notif_ib_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		String status = aBagSPJavaOrchestration.get("accountStatus").toString();
		String value =  aBagSPJavaOrchestration.get("blockingValue").toString();
		String titulo = "";
		if (status.equals("A")) {
			titulo = "Cuenta Activada";
		} else if (status.equals("B")) {
			titulo = "Cuenta Bloqueada";
		} else if (status.equals("C")) {
			titulo = "Cuenta Cancelada";
		} else if (status.equals("BV")) {
			titulo = "Cuenta Bloqueada por valores, por el valor de: " + value;
		} else if (status.equals("BM")) {
			if (value.equals("1")) {
				titulo = "Cuenta Bloqueada por movimientos: contra credito";
			} else if (value.equals("2")) {
				titulo = "Cuenta Bloqueada por movimientos: contra debito";
			} else if (value.equals("3")) {
				titulo = "Cuenta Bloqueada por movimientos: contra credito y debito";
			}
			
		} 
		
		
		request.addInputParam("@i_titulo", ICTSTypes.SQLVARCHAR, titulo);
		request.addInputParam("@i_servicio", ICTSTypes.SQLINTN, "8");
		request.addInputParam("@i_ente_mis", ICTSTypes.SQLINTN, aBagSPJavaOrchestration.get("externalCustomerId").toString());
		request.addInputParam("@i_ente_ib", ICTSTypes.SQLINTN, aBagSPJavaOrchestration.get("bv_ente") != null ? aBagSPJavaOrchestration.get("bv_ente").toString() : "0");
		request.addInputParam("@i_notificacion", ICTSTypes.SQLVARCHAR, "N45");
		
		
		request.addInputParam("@i_producto", ICTSTypes.SQLINTN, "3");
		request.addInputParam("@i_num_producto", ICTSTypes.SQLVARCHAR, "");
		request.addInputParam("@i_tipo_mensaje", ICTSTypes.SQLVARCHAR, "F");
		request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("login") != null ? aBagSPJavaOrchestration.get("login").toString() : "login");
		request.addInputParam("@i_tipo", ICTSTypes.SQLVARCHAR, "M");
		request.addInputParam("@i_mensaje", ICTSTypes.SQLVARCHAR, "Cliente Afiliad");
		request.addInputParam("@i_c1", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("accountNumber").toString());
		request.addInputParam("@i_aux1", ICTSTypes.SQLVARCHAR, "ayuda 1");
		request.addInputParam("@i_print", ICTSTypes.SQLVARCHAR, "S");
		request.addInputParam("@s_culture", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_culture"));
		request.addInputParam("@s_date", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_date"));
		
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de sendMail");
		}
		
	}
	
	// ------------------------------------------------------------------------------
	// Bloqueo de Tarjetas Virtuales y Fisicas para SuBbloqueo BM1-(Pre-cancelación)
	// ------------------------------------------------------------------------------
	
	private IProcedureResponse updaterCardStatus(IProcedureRequest aRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
	
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en updaterCardStatus: " );
		}
		
		aBagSPJavaOrchestration.put("ente_mis", aBagSPJavaOrchestration.get("@i_external_customer_id"));
		
		IProcedureResponse wAccountsResp = new ProcedureResponseAS();
		//TODO: MO - Qué INDICA "S"?
		String flag = "S";
		
		// Obtine el card id y card type por cada cliente para cada tarjeta
		wAccountsResp = getCardsByCustomer(aRequest, aBagSPJavaOrchestration);
				
		// MO - ? no se que hace esto
		/*if(wAccountsResp.getResultSets().size()>1 && !wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			return wAccountsResp;
		}*/
				
		IResultSetHeaderColumn[]  headers = (IResultSetHeaderColumn[]) aBagSPJavaOrchestration.get("cardsResultSetHeaders");
		logger.logDebug(CLASS_NAME + "Response headers: updaterCardStatus : headers: " + headers);
		
		IResultSetRow[] cardsData = (IResultSetRow[]) aBagSPJavaOrchestration.get("cardsResultSetsData");

		// MO - Llama los metodos para continuar con el bloqueo, por CADA tarjeta
		for (int i = 0; i < cardsData.length; i++) {
			IResultSetRow iResultSetRow = cardsData[i];
			IResultSetRowColumnData[] cardDataColumns = iResultSetRow.getColumnsAsArray();
			logger.logDebug(CLASS_NAME + " updaterCardStatus : cardDataColumns[]: " + cardDataColumns);
			//Datos de una tarjeta
			String cardId = cardDataColumns[0].getValue();
			String estado = cardDataColumns[3].getValue();
			String tipoPHVI = cardDataColumns[18].getValue();
			String mensaje = String.format("La tarjeta con ID %s está en estado %s y es de tipo %s.", cardId, estado, tipoPHVI);
			logger.logDebug(CLASS_NAME + " updaterCardStatus : " + mensaje );
			
			// Agrega los datos VARIABLES del bundle por CADA tarjeta
			aBagSPJavaOrchestration.put("i_type_card",tipoPHVI);
			aBagSPJavaOrchestration.put("i_card_id",cardId);
			
			wAccountsResp = getDataCardDock(aRequest, aBagSPJavaOrchestration);
			logger.logInfo( CLASS_NAME + " code resp card dock: " + wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue());
			
			//if (wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")) {
			if (wAccountsResp.getReturnCode()==0 ){
					if(!wAccountsResp.readValueParam("@o_id_card_atm").equals("0") && flag.equals("S")) {
						IProcedureResponse wAccountsRespInsert = new ProcedureResponseAS();
						wAccountsRespInsert = executeUpdateCard(aRequest, aBagSPJavaOrchestration);
						if(aRequest.readValueParam("@i_card_status").equals("C")){
							// TODO: MO - Por decidir que hacer
							//cancelCardAtm(aRequest, aBagSPJavaOrchestration);
						}
						else{
							updateStatusAtm(aRequest, aBagSPJavaOrchestration);	
						}
						//updateStatusAtm(aRequest, aBagSPJavaOrchestration);
						return wAccountsRespInsert;
					}else if(wAccountsResp.readValueParam("@o_type_card").toString().equals("VIRTUAL") && !wAccountsResp.readValueParam("@o_id_card_dock").toString().equals("X") && flag.equals("S")) {
						IProcedureResponse wAccountsRespInsert = new ProcedureResponseAS();
						wAccountsRespInsert = executeUpdateCard(aRequest, aBagSPJavaOrchestration);
						if(aRequest.readValueParam("@i_card_status").equals("C")){
							// TODO: MO - Por decidir que hacer
							//cancelCardAtm(aRequest, aBagSPJavaOrchestration);
							logger.logInfo(CLASS_NAME + "Flujo de cancelacion - No considerado ... QUE HACER");
						}
						else{
							updateStatusAtm(aRequest, aBagSPJavaOrchestration);	
						}
						return wAccountsRespInsert;
					}
			}
			
		}
	
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Response " + wAccountsResp.toString());
			logger.logInfo(CLASS_NAME + " Saliendo de updaterCardStatus");
		}
	
		return wAccountsResp;
	}

	/**
	 * Llamada a SP para obtener tarjetas asociadas a una cuenta.
	 * @param anOriginalRequest
	 * @param aBagSPJavaOrchestration
	 * @return IProcedureResponse
	 */
	private IProcedureResponse getCardsByCustomer (IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
	    // Crear una instancia de la solicitud del procedimiento
	    IProcedureRequest request = new ProcedureRequestAS();
	
	    
	    if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getCardsByCustomer");
		}
	  	
	  	request.setSpName("cob_atm..sp_atm_cli_tarjetas");
	    
	
	  	request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
	  			IMultiBackEndResolverService.TARGET_LOCAL);
	  	request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
	
	  	request.addInputParam("@i_app_org", ICTSTypes.SQLVARCHAR, "BV");
	  	request.addInputParam("@i_cliente", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_external_customer_id"));
	
	    // Ejecutar el SP y obtener la respuesta
	    IProcedureResponse wProcedureResponse = executeCoreBanking(request);
	
	    if (logger.isInfoEnabled()) {
	        logger.logDebug("Ending flow, getCardsByCustomer with wProcedureResponse as String: " + wProcedureResponse.getProcedureResponseAsString());
	    }
	    
	    if (!wProcedureResponse.hasError()) {
	        
	    	Integer codeReturn =  wProcedureResponse.getReturnCode();
	        logger.logDebug("Response Code: getCardsByCustomer with wProcedureResponse: " + codeReturn);
	        
	    	IResultSetHeaderColumn[] resutlSetHeaders = wProcedureResponse.getResultSet(1).getMetaData().getColumnsMetaDataAsArray();
	    	logger.logDebug("Response Code: getCardsByCustomer : resutlSetHeader: " + resutlSetHeaders);
	    	 
	        IResultSetRow[] resultSetRows = wProcedureResponse.getResultSet(1).getData().getRowsAsArray();
	        logger.logDebug("Response Code: getCardsByCustomer : Cantidad Filas/Tarjetas: " + resultSetRows.length);
	        logger.logDebug("Response Code: getCardsByCustomer : IResultSetRow[]: " + resultSetRows);
	     
	         
	        aBagSPJavaOrchestration.put("cardsResultSetHeaders", resutlSetHeaders);
	        aBagSPJavaOrchestration.put("cardsResultSetsData", resultSetRows);
	        logger.logDebug("Response Data: getCardsByCustomer added aBagSPJavaOrchestration" );
	   }
	    
	    return wProcedureResponse;      
	           
	}

	private IProcedureResponse getDataCardDock(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();
		String mode;
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getDataCardDock");
		}
		
		if(aRequest.readValueParam("@i_mode")!=null){
			mode = aRequest.readValueParam("@i_mode").equals("null")?"X":aRequest.readValueParam("@i_mode").toString();
			aBagSPJavaOrchestration.put("mode", mode);
		}
		else
			mode= "X";

		logger.logInfo(CLASS_NAME + " mode_getDataCardDock" + mode);
		
		request.setSpName("cob_atm..sp_get_data_card_dock_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@x_request_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_request_id"));
		request.addInputParam("@x_end_user_request_date", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_end_user_request_date"));
		request.addInputParam("@x_end_user_ip", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_end_user_ip"));
		request.addInputParam("@x_channel", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_channel"));
		request.addInputParam("@x_val", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_val")!=null?aRequest.readValueParam("@x_val"):null);

		//Ya vienen en request de updateAccountStatus con el mismo nombre
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_external_customer_id"));
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account_number"));
		
		//Predefinidos al empezar orquestacion en executeJavaOrchestration
		request.addInputParam("@i_status", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("i_card_status").toString());
		request.addInputParam("@i_reason_status", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("i_status_reason").toString());
		request.addInputParam("@i_mode", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_mode"));
		
		//Variables obtenidas de otros SPs
		request.addInputParam("@i_type_card", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("i_type_card").toString());
		request.addInputParam("@i_card_id", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("i_card_id").toString());
		
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
		request.addOutputParam("@o_assigned_card_id", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_incomm_card_id", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_cancel", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_assigned", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_incomm_card", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_accreditation", ICTSTypes.SQLVARCHAR, "X");

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
		aBagSPJavaOrchestration.put("account_number", wProductsQueryResp.readValueParam("@o_account"));
		aBagSPJavaOrchestration.put("o_assigned_card_id", wProductsQueryResp.readValueParam("@o_assigned_card_id"));
		aBagSPJavaOrchestration.put("o_incomm_card_id", wProductsQueryResp.readValueParam("@o_incomm_card_id"));
		aBagSPJavaOrchestration.put("o_cancel", wProductsQueryResp.readValueParam("@o_cancel"));
		aBagSPJavaOrchestration.put("o_assigned", wProductsQueryResp.readValueParam("@o_assigned"));
		aBagSPJavaOrchestration.put("o_incomm_card", wProductsQueryResp.readValueParam("@o_incomm_card"));
		aBagSPJavaOrchestration.put("o_accreditation", wProductsQueryResp.readValueParam("@o_accreditation"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking getDataCardDock: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getDataCardDock");
		}

		return wProductsQueryResp;
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
			anOriginalRequest.addInputParam("@i_type_card", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("i_type_card").toString());
			anOriginalRequest.addInputParam("@i_operation", ICTSTypes.SQLVARCHAR, "UCS");
			
			
		//TODO: Validar el valor de @i_operation y el service.identifier del conector, es el mismo 
			//de esta api(UpdateAccountStatusDockOrchestrationCore) o la de tarjetas( UpdateCardDockOrchestrationCore)
			
			
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
	
	private IProcedureResponse updateStatusAtm(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();
		Integer trn = 0;
		String process = null, reason = null;

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en updateStatusAtm");
		}

		if(aBagSPJavaOrchestration.get("i_card_status").toString().equals("N"))
		{
			trn = 16537;
			process = "LBW";
			reason = "SCL";
		}else if (aBagSPJavaOrchestration.get("i_card_status").toString().equals("B")){
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
		
}
