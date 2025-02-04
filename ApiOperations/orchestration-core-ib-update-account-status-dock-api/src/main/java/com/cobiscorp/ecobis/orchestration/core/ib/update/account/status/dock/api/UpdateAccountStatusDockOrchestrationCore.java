/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.update.account.status.dock.api;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
	/**
	 * Procedure response para representar el sub-flujo de respuestas del bloqueo de tarjetas
	 * (SOLO cuando el cambio de estado de cuenta es BM1)
	 */
	private IProcedureResponse anProcedureResponse2;
	private ArrayList<IProcedureResponse> wAccountsRespInsertArray;
	
	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled()){logger.logDebug("Begin flow, UpdateAccountStatusDock starts executeJavaOrchestration...");}		
		
		String skipNotification = "";
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();
		
		Collection<?> params = anOriginalRequest.getParams();
		if (logger.isDebugEnabled()){logger.logDebug("REQUEST INFO: " + params);}	
		
		//Obtenemos el valor para enviar o no notificaciones
		if (anOriginalRequest.readValueParam("@i_skipNotification") != null) {
			skipNotification = anOriginalRequest.readValueParam("@i_skipNotification");			
		}
		
		//Asignamos el valor de notificaciones
		aBagSPJavaOrchestration.put("skipNotification", skipNotification);		
		
		// Sub flujo 1: Cambiar el estado de una cuenta
		anProcedureResponse = updateAccountStatusDock(anOriginalRequest, aBagSPJavaOrchestration);
		Integer codeReturn = anProcedureResponse.getReturnCode();
		
		String accountStatus = String.valueOf(aBagSPJavaOrchestration.get("accountStatus"));
		String blokingValue = String.valueOf(aBagSPJavaOrchestration.get("blockingValue"));
		String changedStateDate =  String.valueOf(aBagSPJavaOrchestration.get("o_changedStateDate"));
		
		if (logger.isDebugEnabled()){
			logger.logDebug("End sub-flow, changing state, Codigo=" + String.valueOf(codeReturn) 
			                 + ", o_changedStateDate hasta aqui: " +  changedStateDate);	
		}
		
		//  Sub flujo 2: Cambiar el estado de las tarjetas asociadas a la cuenta, si el estado es BM1
		if(accountStatus.equals("BM") && blokingValue.equals("1")) {			
			if (logger.isDebugEnabled()){logger.logDebug("Begin sub-flow, UpdateAccountStatusDock starts blocking cards...");}		
			anProcedureResponse2 = new ProcedureResponseAS();

			// Agregar entradas invariables para Bloquear tarjeta(s)
			// Datos que ya estan en el request: @i_ente AS @i_external_customer_id,  @i_account_number AS @i_account_number
			aBagSPJavaOrchestration.put("i_ente", anOriginalRequest.readValueParam("@i_external_customer"));
			aBagSPJavaOrchestration.put("i_account_number", anOriginalRequest.readValueParam("@i_account_number"));
			
			// Datos con valores predefinidos
			aBagSPJavaOrchestration.put("i_mode", "X");
			aBagSPJavaOrchestration.put("i_card_status", "B");
			aBagSPJavaOrchestration.put("i_status_reason", "OW");
			
			// Actualizar Card Status para bloqueo de tarjetas
			//Procedure response para representar el flujo de respuestas del bloqueo de tarjetas (cuando el cambio de estado de cuenta es BM1
			anProcedureResponse2 = updaterCardStatus(anOriginalRequest, aBagSPJavaOrchestration);
			Integer codeReturn2 = anProcedureResponse2.getReturnCode();
			
			if (codeReturn2 == 0) { //Codigo general de la respuesta del SP				
				Boolean flag = aBagSPJavaOrchestration.containsKey("o_success");
				
				if (logger.isDebugEnabled()){
					logger.logDebug("[executeJavaOrchestration] response conector dock to block card: " + anProcedureResponse2.toString());
					logger.logDebug("[executeJavaOrchestration] code o_success: " + flag);
					logger.logDebug("[executeJavaOrchestration] general response code: " + anProcedureResponse2.getResultSetRowColumnData(2, 1, 1));
				}
				
				if(Boolean.TRUE.equals(flag)){
					if (logger.isInfoEnabled()){
						//Imprime si bloqueo de tarjeta es exitoso
						logger.logInfo("[executeJavaOrchestration] Ending sub-flow, with success in block card: ");
						logger.logInfo(String.format("[executeJavaOrchestration] return o_card_available: %s, return o_id_card_dock: %s",  aBagSPJavaOrchestration.get("o_card_available"), aBagSPJavaOrchestration.get("o_id_card_dock")));
					}
				}
				else {
					//Imprime si bloqueo de tarjeta da algun error
					if (logger.isDebugEnabled()){logger.logDebug("[executeJavaOrchestration] Ending sub-flow, with error in block card");}
					
					String success = anProcedureResponse2.getResultSetRowColumnData(1, 1, 1).isNull()?"false":anProcedureResponse2.getResultSetRowColumnData(1, 1, 1).getValue();
					String code = anProcedureResponse2.getResultSetRowColumnData(2, 1, 1).isNull()?"400218":anProcedureResponse2.getResultSetRowColumnData(2, 1, 1).getValue();
					String message = anProcedureResponse2.getResultSetRowColumnData(2, 1, 2).isNull()?"Service execution error":anProcedureResponse2.getResultSetRowColumnData(2, 1, 2).getValue();
					
					logger.logWarning(String.format("[executeJavaOrchestration] processResponse2 to block card ErrorData: success: %s, code: %s, message: %s", success, code, message));
					
				}
			}
			if (logger.isInfoEnabled()){
				logger.logInfo("End flow, Terminado updaterCardStatus.Sub-flujo Cambiar Estado: Codigo respuesta=" + codeReturn);			
				logger.logInfo("End flow, Terminado updaterCardStatus.Sub-flujo Bloquear Tarjetas: Codigo respuesta=" + codeReturn2);
			}
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
		
		if (logger.isInfoEnabled()){
			logger.logInfo(CLASS_NAME + " code resp account dock: " + wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue());}
		
		if (wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			if (accountStatus.trim().equals("BV") || accountStatus.trim().equals("BM") || accountStatus.trim().equals("EBM"))
			{
				aBagSPJavaOrchestration.put("success", "success");				
				String changedStateDate = wAccountsResp.readValueParam("@o_changedStateDate");
				
				if (logger.isInfoEnabled()){
					logger.logInfo(CLASS_NAME + " AccountStatus Changed TO: " + String.valueOf(accountStatus.trim())
					+ ", AT changedStateDate: " + String.valueOf(changedStateDate));
				}
				
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
		
		aBagSPJavaOrchestration.put("accountDockId", wProductsQueryResp.readValueParam("@o_account_dock_id"));
		aBagSPJavaOrchestration.put("bv_ente", wProductsQueryResp.readValueParam("@o_bv_ente"));
		aBagSPJavaOrchestration.put("login", wProductsQueryResp.readValueParam("@o_login"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("account dock(1) id es " +  wProductsQueryResp.readValueParam("@o_account_dock_id"));
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

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en execute AccountStatus update ");
		}
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("account dock(2) id es " + aBagSPJavaOrchestration.get("accountDockId").toString());
		}
		
		accountDockId = aBagSPJavaOrchestration.containsKey("accountDockId")? aBagSPJavaOrchestration.get("accountDockId").toString():null;;
		
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

			if (logger.isDebugEnabled()){logger.logDebug("accountDock--> request update account status: " + anOriginalRequest.toString());}
						
			// SE EJECUTA CONECTOR
			connectorAccountResponse = executeProvider(anOriginalRequest, aBagSPJavaOrchestration);

			if (logger.isDebugEnabled()) {
				logger.logDebug("connectorUpdateAccountStatusResponse: " + connectorAccountResponse);
			}
			
			if (connectorAccountResponse.readValueParam("@o_account_id") != null) {
				aBagSPJavaOrchestration.put("accountId", connectorAccountResponse.readValueParam("@o_account_id"));}
			else {
				aBagSPJavaOrchestration.put("accountId", "null");}

			if (connectorAccountResponse.readValueParam("@o_success") != null) {
				aBagSPJavaOrchestration.put("success", connectorAccountResponse.readValueParam("@o_account_id"));}
			
		} catch (Exception e) {
			e.printStackTrace();
			connectorAccountResponse = null;
			if (logger.isInfoEnabled()) {logger.logInfo(CLASS_NAME +" Error Catastrofico de updateAccountStatusExecution");}

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
		
		if (logger.isDebugEnabled()){logger.logDebug("Request Corebanking registerLog: " + request.toString());}
		
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
		if (logger.isInfoEnabled()){logger.logInfo("processResponseApi [INI] --->" );}
		String sendNotification = aBagSPJavaOrchestration.get("skipNotification").toString();
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		// Response del flujo principal update account status
		Integer codeReturn = anOriginalProcedureRes.getReturnCode();
		// Response del flujo secundario, bloqueo de tarjetas (SOLO Si el cambio de estado era BM1, debe verificar el response de tarjetas)
		Integer codeReturn2 = anProcedureResponse2 != null ? anProcedureResponse2.getReturnCode() : 0;
		
		if (logger.isInfoEnabled()){
			logger.logInfo("return code resp Conector - UpdateStatusAccount --->" + codeReturn );
			logger.logInfo("return code resp Conector - UpdateCardStatus--->" + codeReturn2 );
		}

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
		String blockingValue = anOriginalRequest.readValueParam("@i_blockingValue");
		
		Boolean flagSubBloqueo = accountStatus.trim().equals("BV") || accountStatus.trim().equals("BM") || accountStatus.trim().equals("EBM");
		
		// Valida que no hayan errores bloqueando las tarjetas en caso de ser un cambio de estado BM1
		if (codeReturn2 != 0) {			
			if (logger.isDebugEnabled()){logger.logDebug("Ending sub-flow, processResponse2 failed with code: ");}
			
			IResultSetRow row = new ResultSetRow();
			row.addRowData(1, new ResultSetRowColumnData(false, "false"));
			data.addRow(row);
			
			IResultSetRow row2 = new ResultSetRow();
			row2.addRowData(1, new ResultSetRowColumnData(false, codeReturn2.toString()));
			row2.addRowData(2, new ResultSetRowColumnData(false, anProcedureResponse2.getMessage(1).getMessageText()));
			data2.addRow(row2);
		
		}
		else if (codeReturn == 0) {
			if (logger.isDebugEnabled()){
				logger.logDebug("response conector dock: " + anOriginalProcedureRes.toString());
				logger.logDebug("code o_assign_date: " + flag);
				logger.logDebug("flag respose: " + flag);
				logger.logDebug("return code response: " + anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1));
				logger.logDebug("account status: " + String.valueOf(flagSubBloqueo) + "flag sub-bloqueo: " + String.valueOf(flagSubBloqueo));
				logger.logDebug("account status: " + String.valueOf(accountStatus) + ", flag sub-bloqueo: " + String.valueOf(flagSubBloqueo));
			}
			
			if(Boolean.TRUE.equals(flag)){
				if (logger.isDebugEnabled()){logger.logDebug("Ending flow, processResponse success with code: ");}
				
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, "true"));
				data.addRow(row);
				
				IResultSetRow row2 = new ResultSetRow();
				row2.addRowData(1, new ResultSetRowColumnData(false, "0"));
				row2.addRowData(2, new ResultSetRowColumnData(false, "Success"));
				data2.addRow(row2);
				
				// Fecha de cambio de estado por un sub-bloqueo
				if (Boolean.TRUE.equals(flagSubBloqueo))
				{
					if (logger.isDebugEnabled()){logger.logDebug("Ending flow, processResponse success with code: changedStateDate ");}
					IResultSetRow row3 = new ResultSetRow();
					String changedStateDate =  aBagSPJavaOrchestration.get("o_changedStateDate").toString();
					row3.addRowData(1, new ResultSetRowColumnData(false, changedStateDate));
					data3.addRow(row3);
				}
				
				if (!accountStatus.equals("C") && !sendNotification.equals("S")) {			           
		            if (accountStatus.equals("BM") || accountStatus.equals("EBM")) {		               
		                if (blockingValue != null && !blockingValue.equals("1")) {		                   
		                	sendMail(anOriginalRequest, aBagSPJavaOrchestration);
		                }		                
		            } else {		                
		            	sendMail(anOriginalRequest, aBagSPJavaOrchestration);
		            }
		        }			
				
			} else { // Error de VALIDACION en el procedure
				if (logger.isDebugEnabled()){logger.logDebug("Ending flow, processResponse error");}
				
				String success = anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).isNull()?"false":anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue();
				String code = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).isNull()?"400218":anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).getValue();
				String message = anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).isNull()?"Service execution error":anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).getValue();
				
				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, success));
				data.addRow(row);
				
				// Agrega codigo y mensaje de error para cualquier validacion del procedure
				IResultSetRow row2 = new ResultSetRow();
				row2.addRowData(1, new ResultSetRowColumnData(false, code));
				row2.addRowData(2, new ResultSetRowColumnData(false, message));
				data2.addRow(row2);
			}			
		} else { //Error en la EJECUCION del procedure de datos
			if (logger.isDebugEnabled()){logger.logDebug("Ending flow, processResponse failed with code: ");}
			
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
		
		// Registra log change status account
		registerLogBd(anOriginalProcedureRes, aBagSPJavaOrchestration);
		
		// Registra logs bloqueo de Tarjetas cuando estado de cuenta es BM1 
		if (wAccountsRespInsertArray!=null && !wAccountsRespInsertArray.isEmpty()) {
			for (IProcedureResponse iProcedureResponse : wAccountsRespInsertArray) {
				if (logger.isDebugEnabled()){logger.logDebug("Data sent to registerLogBd2: {}"+ iProcedureResponse.toString());}
				registerLogBd2(iProcedureResponse, aBagSPJavaOrchestration);
			}
		}			
	  
		// Fecha de cambio de estado sub-bloqueo
		if (Boolean.TRUE.equals(flag) && Boolean.TRUE.equals(flagSubBloqueo)) {
			if (logger.isDebugEnabled()){logger.logDebug("Ending flow, processResponse success with code for sub-blocking state");}
		
			IResultSetBlock resultsetBlock3 = new ResultSetBlock(metaData3, data3);
			wProcedureResponse.addResponseBlock(resultsetBlock3);
		} 
		else if (Boolean.TRUE.equals(flag) && Boolean.FALSE.equals(flagSubBloqueo)) // Fecha de cambio de estado general
		{
			if (logger.isDebugEnabled()){logger.logDebug("Ending flow, processResponse success with code for general state");}
			
			IResultSetRow row3 = new ResultSetRow();
			String changedStateDate =  aBagSPJavaOrchestration.get("o_changedStateDate2").toString();
			row3.addRowData(1, new ResultSetRowColumnData(false, changedStateDate));
			data3.addRow(row3);
			IResultSetBlock resultsetBlock3 = new ResultSetBlock(metaData3, data3);
			wProcedureResponse.addResponseBlock(resultsetBlock3);
		}
		else{
			if (logger.isDebugEnabled()){logger.logDebug("Ending flow, processResponse error or failed");}
			
			IResultSetRow row3 = new ResultSetRow();
			//NA, para que en el service sea removida esta fila de la respueta Json
			row3.addRowData(1, new ResultSetRowColumnData(false, "NA"));
			data3.addRow(row3);
			IResultSetBlock resultsetBlock3 = new ResultSetBlock(metaData3, data3);
			wProcedureResponse.addResponseBlock(resultsetBlock3);
		}		
		return wProcedureResponse;		
	}

	private void sendMail(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()){logger.logInfo(CLASS_NAME + " Entrando en sendMail");}
		
		String status = aBagSPJavaOrchestration.get("accountStatus").toString();
		String value =  aBagSPJavaOrchestration.get("blockingValue").toString();
		String titulo = null;
		
		if (value.isEmpty() || aBagSPJavaOrchestration.get("blockingValue") == null) {
			value = "0";
		}
		
		titulo = getTitle(status, value);
		
		request.setSpName("cob_bvirtual..sp_bv_enviar_notif_ib_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@s_culture", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_culture"));
		request.addInputParam("@s_date", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_date"));
		
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
		request.addInputParam("@i_mensaje", ICTSTypes.SQLVARCHAR, "Actualización de cuenta");
		request.addInputParam("@i_c1", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("accountNumber").toString());
		request.addInputParam("@i_aux1", ICTSTypes.SQLVARCHAR, value);
		request.addInputParam("@i_print", ICTSTypes.SQLVARCHAR, "S");
		
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
	
	private IProcedureResponse updaterCardStatus(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled()){logger.logInfo(CLASS_NAME + " Entrando en updaterCardStatus: " );}
		
		aBagSPJavaOrchestration.put("ente_mis", aRequest.readValueParam("@i_external_customer_id"));
		IProcedureResponse wAccountsResp = new ProcedureResponseAS();
		String flag = "S";
		
		// Obtine el card id y card type por cada cliente para cada tarjeta
		wAccountsResp = getCardsByCustomer(aRequest, aBagSPJavaOrchestration);
		
		IResultSetHeaderColumn[]  headers = (IResultSetHeaderColumn[]) aBagSPJavaOrchestration.get("cardsResultSetHeaders");
		
		if (logger.isDebugEnabled()){logger.logDebug(CLASS_NAME + "Response headers: updaterCardStatus : headers: " + headers);}
		
		IResultSetRow[] cardsData = (IResultSetRow[]) aBagSPJavaOrchestration.get("cardsResultSetsData");

		wAccountsRespInsertArray = new ArrayList<IProcedureResponse>();
			
		// MO - Llama los metodos para continuar con el bloqueo, por CADA tarjeta
		for (int i = 0; i < cardsData.length; i++) 
		{
			IResultSetRow iResultSetRow = cardsData[i];
			IResultSetRowColumnData[] cardDataColumns = iResultSetRow.getColumnsAsArray();
			if (logger.isDebugEnabled()){logger.logDebug(CLASS_NAME + " updaterCardStatus : cardDataColumns[]: " + cardDataColumns);}
			
			//Datos de una tarjeta
			String cardId = cardDataColumns[0].getValue();
			String estado = cardDataColumns[3].getValue();
			String tipoPHVI = cardDataColumns[18].getValue().equals("PHYSICAL")?"PH":"VI"; //Por defecto, intenta con Virutal(VI)
			String mensaje = String.format("La tarjeta con ID %s tiene estado %s y es tipo %s.", cardId, estado, tipoPHVI);
			
			if (logger.isDebugEnabled()){logger.logDebug(CLASS_NAME + " updaterCardStatus : " + mensaje );}
			
			// Agrega los datos VARIABLES del bundle por CADA tarjeta para que pueda procesar getDataCardDock
			aBagSPJavaOrchestration.put("i_type_card", tipoPHVI);
			aBagSPJavaOrchestration.put("i_card_id",cardId);
						
			wAccountsResp = getDataCardDock(aRequest, aBagSPJavaOrchestration);
			if (logger.isInfoEnabled()){logger.logInfo( CLASS_NAME + " code resp card dock: " + wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue());}			
			
			if (wAccountsResp.getReturnCode()==0 && flag.equals("S") && (!wAccountsResp.readValueParam("@o_id_card_atm").equals("0") || 
				(wAccountsResp.readValueParam("@o_type_card").toString().equals("VIRTUAL") && 
				!wAccountsResp.readValueParam("@o_id_card_dock").toString().equals("X"))))
			{			
				IProcedureResponse wAccountsRespInsert = new ProcedureResponseAS();
				wAccountsRespInsert = executeUpdateCard(aRequest, aBagSPJavaOrchestration);
				if(aBagSPJavaOrchestration.get("i_card_status").toString().equals("C")){
					if (logger.isInfoEnabled()){logger.logInfo(CLASS_NAME + "Flujo de cancelacion - No deberia entrar aqui en un bloqueo");}
				}
				else{
					updateStatusAtm(aRequest, aBagSPJavaOrchestration);	
				}						
				wAccountsRespInsertArray.add(wAccountsRespInsert);
			}			
		}//end for
		
		// Retorna el response del ultimo bloqueo de tarjeta
		if (wAccountsRespInsertArray != null && !wAccountsRespInsertArray.isEmpty()) {
            return wAccountsRespInsertArray.get(wAccountsRespInsertArray.size() - 1);
        } else {
        	if (logger.isWarningEnabled()){logger.logWarning("La lista de tarjetas es vacía o es nula.");}
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
	private IProcedureResponse getCardsByCustomer(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
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
	    	if (logger.isDebugEnabled()){logger.logDebug("Response Code: getCardsByCustomer with wProcedureResponse: " + codeReturn);}
	        
	        
	        IResultSetHeaderColumn[] resutlSetHeaders = null;
	        IResultSetRow[] resultSetRows = null;
	       	        
        
	        if ( wProcedureResponse.getResultSet(1) != null) { //Un output que no aplica
	        	resutlSetHeaders = wProcedureResponse.getResultSet(1).getMetaData().getColumnsMetaDataAsArray();
	        	resultSetRows = wProcedureResponse.getResultSet(1).getData().getRowsAsArray();
	        	
	        	if (logger.isDebugEnabled()){
		        	logger.logDebug("Response SP: getCardsByCustomer1 : resutlSetHeader: " + resutlSetHeaders);
		        	logger.logDebug("Response SP: getCardsByCustomer1 : OutputsSP: " + resultSetRows.length);
		        	logger.logDebug("Response SP: getCardsByCustomer1 : IResultSetRow[]: " + resultSetRows);
	        	}
	        }

	        if ( wProcedureResponse.getResultSet(2) != null) { //Filas con tajertas 
	        	resutlSetHeaders = wProcedureResponse.getResultSet(2).getMetaData().getColumnsMetaDataAsArray();
	        	resultSetRows = wProcedureResponse.getResultSet(2).getData().getRowsAsArray();
	        	IResultSetRowColumnData[] firstRow = resultSetRows != null ? resultSetRows[0].getColumnsAsArray() : null ;
	        	
	        	if (logger.isDebugEnabled()){
		        	logger.logDebug("Response SP: getCardsByCustomer2 : resutlSetHeader #columnas="+resutlSetHeaders.length + " columnas=" + Arrays.toString(resutlSetHeaders));
		        	logger.logDebug("Response SP: getCardsByCustomer2 : Cantidad Filas/Tarjetas: " + resultSetRows.length);
		        	logger.logDebug("Response SP: getCardsByCustomer2 : FirstCard: " + Arrays.toString(firstRow));
	        	}
	        }
	        
	        aBagSPJavaOrchestration.put("cardsResultSetHeaders", resutlSetHeaders);
	        aBagSPJavaOrchestration.put("cardsResultSetsData", resultSetRows);
	        
	        if (logger.isDebugEnabled()){logger.logDebug("Response Data: getCardsByCustomer added aBagSPJavaOrchestration" );}
	    }    
	    return wProcedureResponse; 
	}

	private IProcedureResponse getDataCardDock(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureRequest request = new ProcedureRequestAS();
		String mode;
		
		if (logger.isInfoEnabled()) {logger.logInfo(CLASS_NAME + " Entrando en getDataCardDock");}
		
		if(aRequest.readValueParam("@i_mode")!=null){
			mode = aRequest.readValueParam("@i_mode").equals("null")?"X":aRequest.readValueParam("@i_mode").toString();
			aBagSPJavaOrchestration.put("mode", mode);
		}
		else {
			mode= "X";}

		if (logger.isInfoEnabled()) {logger.logInfo(CLASS_NAME + " mode_getDataCardDock" + mode);}
		
		request.setSpName("cob_atm..sp_get_data_card_dock_api");
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");	
			
		// Flag con valor de X para evitar validacion de headers
		request.addInputParam("@x_val", ICTSTypes.SQLVARCHAR, "X");

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
		String idCardDock = null;
		String status = null; 
		String reasonStatus = null;
		
		IProcedureRequest request = new ProcedureRequestAS();
		aBagSPJavaOrchestration.remove("trn_virtual");
		
		idCardDock = aBagSPJavaOrchestration.containsKey("o_id_card_dock")? aBagSPJavaOrchestration.get("o_id_card_dock").toString():null;
		status = aBagSPJavaOrchestration.containsKey("o_detail_status")? aBagSPJavaOrchestration.get("o_detail_status").toString():null;
		reasonStatus = aBagSPJavaOrchestration.containsKey("o_det_reason_stat")? aBagSPJavaOrchestration.get("o_det_reason_stat").toString():"X";
		
		if (logger.isInfoEnabled()) {
			String mapData = "{";
	        for (Map.Entry<String, Object> entry : aBagSPJavaOrchestration.entrySet()) {
	        	mapData += entry.getKey() + ": " + String.valueOf(entry.getValue()) + ", ";
	        }
	        mapData += "}";
			logger.logInfo(CLASS_NAME + " Entrando en executeUpdateCard: aBagSPJavaOrchestration Size = " + aBagSPJavaOrchestration.size());
			logger.logInfo(CLASS_NAME + " aBagSPJavaOrchestration = " + mapData);
		}
		try {
			// PARAMETROS DE ENTRADA
			request.addInputParam("@i_ente", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("ente_mis").toString());
			request.addInputParam("@i_id_card_dock", ICTSTypes.SQLVARCHAR, idCardDock);
			request.addInputParam("@i_status", ICTSTypes.SQLVARCHAR, status);
			request.addInputParam("@i_reason_status", ICTSTypes.SQLVARCHAR, reasonStatus);
			request.addInputParam("@i_type_card", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("i_type_card").toString());
			request.addInputParam("@i_operation", ICTSTypes.SQLVARCHAR, "UCS");		

			// VARIABLES DE SALIDA
			request.addOutputParam("@o_cod_respuesta", ICTSTypes.SQLVARCHAR, "0");
			request.addOutputParam("@o_desc_respuesta", ICTSTypes.SQLVARCHAR, "X");
			
			request.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
					"(service.identifier=UpdateAccountStatusDockOrchestrationCore)");
			request.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "executeTransaction");
			request.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, "");
			request.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
			request.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
			request.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingOrchestrator");

			request.addFieldInHeader("trn", ICOBISTS.HEADER_STRING_TYPE, "18500112");
			request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500112");

			request.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "18500112");
			
			// SE HACE LA LLAMADA AL CONECTOR
			aBagSPJavaOrchestration.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorDock)");
			request.setSpName("cob_procesador..sp_card_status_api");

			request.addInputParam("@trn_virtual", ICTSTypes.SYBINT4, "18500112");
			request.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500112");

			if (logger.isDebugEnabled()) {
				logger.logDebug(CLASS_NAME + "executeUpdateCard : cardDock--> request update card app: " + request.toString());
			}
			
			// SE EJECUTA CONECTOR
			connectorCardResponse = executeProvider(request, aBagSPJavaOrchestration);

			if (logger.isDebugEnabled()) {
				logger.logDebug("jcos--> connectorUpdateCardApplicationResponse: " + connectorCardResponse);}

			if (connectorCardResponse.readValueParam("@o_card_id") != null) {
				aBagSPJavaOrchestration.put("o_card_id", connectorCardResponse.readValueParam("@o_card_id"));}
			else {
				aBagSPJavaOrchestration.put("o_card_id", "null");}

			if (connectorCardResponse.readValueParam("@o_success") != null) {
				aBagSPJavaOrchestration.put("o_success", connectorCardResponse.readValueParam("@o_card_id"));}
			
		} catch (Exception e) {
			e.printStackTrace();
			connectorCardResponse = null;
			
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME +" Error Catastrofico de updateCardStatusExecution: " + e.getMessage());
				}
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
		String process = null;
		String reason = null;

		if (logger.isInfoEnabled()) {logger.logInfo(CLASS_NAME + " Entrando en updateStatusAtm");}

		if(aBagSPJavaOrchestration.get("i_card_status").toString().equals("N")){
			trn = 16537;
			process = "LBW";
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
	
	private void registerLogBd2(IProcedureResponse reponseCard, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en registerLogBd2");
			logger.logInfo(CLASS_NAME + " Entrando en reponseCard" + reponseCard.toString());
		}

		if (logger.isInfoEnabled()) {
			String mapData = "{";
	        for (Map.Entry<String, Object> entry : aBagSPJavaOrchestration.entrySet()) {
	        	mapData += entry.getKey() + ": " + String.valueOf(entry.getValue()) + ", ";
	        }
	        mapData += "}";
			logger.logInfo(CLASS_NAME + " Entrando en registerLogBd2: aBagSPJavaOrchestration Size = " + aBagSPJavaOrchestration.size());
			logger.logInfo(CLASS_NAME + " aBagSPJavaOrchestration = " + mapData);
		}
		
		request.setSpName("cob_atm..sp_insert_data_dock_api");
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		LocalDateTime fechaHoraActual = LocalDateTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String fechaACtual = fechaHoraActual.format(formato);
        
        // Toma el cardId y el typeCard del reponseCard recibido para CADA tarjeta
     	String typeCard = reponseCard.readValueParam("@o_type_card"); // VIRTUAL o PHYSICAL
     	if (reponseCard.readValueParam("@o_card_type")!=null ){
     		if(reponseCard.readValueParam("@o_card_type").equals("VI")) {
     			typeCard = "VIRTUAL";}
    		else { 		
     			typeCard = "PHYSICAL";}
     	}     		
     		 
        request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aBagSPJavaOrchestration.get("externalCustomerId").toString());
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("accountNumber").toString());
		request.addInputParam("@i_fecha_reg", ICTSTypes.SQLVARCHAR, fechaACtual);
		request.addInputParam("@i_fecha_mod", ICTSTypes.SQLVARCHAR, fechaACtual);
		request.addInputParam("@i_modo", ICTSTypes.SQLVARCHAR, "UCS");
		request.addInputParam("@i_tarjeta_id", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_card_id"));
		request.addInputParam("@i_request_td", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_requestUpdateCard"));
		request.addInputParam("@i_estado_tarjeta", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_card_status"));
		request.addInputParam("@i_estado_upd", ICTSTypes.SQLVARCHAR, reponseCard.readValueParam("@o_success"));
		
		String message = reponseCard.readValueParam("@o_responseUpdateCard");
		request.addInputParam("@i_response_td", ICTSTypes.SQLVARCHAR, message);
		request.addInputParam("@i_tipo_tarjeta", ICTSTypes.SQLVARCHAR,typeCard);
		
		logger.logDebug("Request Corebanking registerLog: " + request.toString());
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de registerLogBd2");
		}
	}
	
	private String getTitle(String status, String value) {
		String title = "";
		
		if (status.equals("A")) {			
			title = "Cuenta activada exitosamente";
		}
		if (status.equals("B")) {			
			title = "Cuenta bloqueada exitosamente";
		}
		if (status.equals("C")) {			
			title = "Cuenta cancelada exitosamente";
		}
		if (status.equals("BV")) {			
			title = "Cuenta bloqueada por valores";
		}
		if (status.equals("EBV")) {
			title = "Cuenta desbloqueada por valores";
		}
		
		if (status.equals("BM")){
			if (value.equals("1")) {
				title = "Cuenta bloqueada por movimientos: contra crédito";
			} else if (value.equals("2")) {
				title = "Cuenta bloqueada por movimientos: contra débito";
			} else if (value.equals("3")) {
				title = "Cuenta bloqueada por movimientos: contra crédito y débito";
			}
		}
		
		if (status.equals("EBM")) {
			if (value.equals("1")) {
				title = "Cuenta desbloqueada por movimientos: contra crédito";
			} else if (value.equals("2")) {
				title = "Cuenta desbloqueada por movimientos: contra débito";
			} else if (value.equals("3")) {
				title = "Cuenta desbloqueada por movimientos: contra crédito y débito";
			}
		}
		
		return title;
	}
		
}
