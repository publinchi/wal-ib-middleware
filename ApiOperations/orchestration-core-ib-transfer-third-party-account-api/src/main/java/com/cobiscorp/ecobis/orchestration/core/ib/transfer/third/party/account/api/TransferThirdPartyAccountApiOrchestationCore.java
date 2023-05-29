package com.cobiscorp.ecobis.orchestration.core.ib.transfer.third.party.account.api;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.CISResponseManagmentHelper;
import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
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
 * Register Account
 * 
 * @since Abr 1, 2023
 * @author dcollaguazo
 * @version 1.0.0
 * 
 */
@Component(name = "TransferThirdPartyAccountApiOrchestationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "TransferThirdPartyAccountApiOrchestationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TransferThirdPartyAccountApiOrchestationCore") })
public class TransferThirdPartyAccountApiOrchestationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(TransferThirdPartyAccountApiOrchestationCore.class);
	private static final String CLASS_NAME = "TransferThirdPartyAccountApiOrchestationCore--->";

	CISResponseManagmentHelper cisResponseHelper = new CISResponseManagmentHelper();

	protected static final String CHANNEL_REQUEST = "8";

	/**
	 * Read configuration of parent component
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
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

		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);

		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();
		
		anProcedureResponse = transferThirdAccount(anOriginalRequest, aBagSPJavaOrchestration);
		
		return processResponseTransfer(anProcedureResponse);

	}

	public IProcedureResponse processResponseTransfer(IProcedureResponse anOriginalProcedureRes) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(" start processResponseAccounts--->");
			logger.logInfo("xdcxv --->" + anOriginalProcedureRes.readValueParam("@o_referencia") );
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


	private IProcedureResponse transferThirdAccount(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en transferThirdAccount");
		}
			
		IProcedureResponse wAccountsResp = new ProcedureResponseAS();
		IProcedureResponse wAccountsRespVal = new ProcedureResponseAS();
		
		wAccountsResp = getDataAccountReq(aRequest, aBagSPJavaOrchestration);		
		logger.logInfo(CLASS_NAME + " dataLocal "+ wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue());
		
		if (wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")) {
			aRequest.removeParam("@i_cta_des");
			aRequest.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR, wAccountsResp.getResultSetRowColumnData(3, 1, 1).getValue());
			wAccountsRespVal = getValAccountReq(aRequest, aBagSPJavaOrchestration);		
			logger.logInfo(CLASS_NAME + " validaCentral "+ wAccountsRespVal.getResultSetRowColumnData(2, 1, 1).getValue());
		}
		else
		{
			return wAccountsResp;
		}
		
		if (wAccountsRespVal.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			IProcedureResponse wTransferResponse = new ProcedureResponseAS();
			
			logger.logInfo(CLASS_NAME + " XDCX " + aBagSPJavaOrchestration.get("o_prod") +
			aBagSPJavaOrchestration.get("o_mon") +
			aBagSPJavaOrchestration.get("o_prod_des") +
			aBagSPJavaOrchestration.get("o_mon_des") +
			aBagSPJavaOrchestration.get("o_prod_alias") +
			aBagSPJavaOrchestration.get("o_nom_beneficiary") +
			aBagSPJavaOrchestration.get("o_login") +
			aBagSPJavaOrchestration.get("o_ente_bv"));
			
			wTransferResponse = executeTransfer(aRequest, aBagSPJavaOrchestration);
			return wTransferResponse; 
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Response " + wAccountsResp.toString());
			logger.logInfo(CLASS_NAME + " Saliendo de transferThirdAccount");
		}

		return wAccountsRespVal;
	}
	
	private IProcedureResponse executeTransfer(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();
		final String METHOD_NAME = "[executeTransferThirdAccount]";

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en executeTransfer");
		}

		request.addInputParam("@s_cliente", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_ente_bv").toString().trim());
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_ente"));
		request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("o_login").toString().trim());
		
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta"));
		request.addInputParam("@i_mon", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_mon").toString().trim());
		request.addInputParam("@i_prod", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_prod").toString().trim());
		
		request.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta_des"));
		request.addInputParam("@i_mon_des", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_mon_des").toString().trim());
		request.addInputParam("@i_prod_des", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_prod_des").toString().trim());
		request.addInputParam("@i_nom_beneficiary", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_nom_beneficiary").toString().trim());
		request.addInputParam("@i_nom_cliente_benef", ICTSTypes.SQLINT4, aBagSPJavaOrchestration.get("o_prod_alias").toString().trim());
		
		request.addInputParam("@i_val", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_val"));
		request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_concepto"));
		request.addInputParam("@i_detalle", ICTSTypes.SQLVARCHAR, "null / null");
		request.addInputParam("@i_comision", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_comision"));
		request.addInputParam("@i_latitud", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_latitud"));
		request.addInputParam("@i_longitud", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_longitud"));
		
		request.addInputParam("@t_ejec", ICTSTypes.SQLINT4, "R");
		request.addInputParam("@t_rty", ICTSTypes.SQLVARCHAR, "N");
		request.addInputParam("@t_trn", ICTSTypes.SQLVARCHAR, "1800012");

		logger.logInfo(METHOD_NAME + " Datos Cabecera");
		// Date fecha = new Date();
		request.setSpName("cob_procesador..p_tr_transferencias_ter");
		// request.addFieldInHeader(ICOBISTS.HEADER_PROCESS_DATE,
		// ICOBISTS.HEADER_DATE_TYPE, forma.format("01/05/2023"));
		request.addFieldInHeader(ICOBISTS.HEADER_MESSAGE_TYPE, ICOBISTS.HEADER_STRING_TYPE, "ProcedureRequest");
		request.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
				"(service.identifier=ThirdPartyTransferOrchestrationCore)");
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
				"InternetBanking.WebApp.Transfers.Transfer.TransferToThird");
		request.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingOrchestrator");
		request.addFieldInHeader("filial", ICOBISTS.HEADER_NUMBER_TYPE, "1");
		request.addFieldInHeader("servicio", ICOBISTS.HEADER_NUMBER_TYPE, "8");
		request.addFieldInHeader("org", ICOBISTS.HEADER_STRING_TYPE, "U");
		request.addFieldInHeader("contextId", ICOBISTS.HEADER_STRING_TYPE, "COBISBV");
		// request.addFieldInHeader("sessionId", ICOBISTS.HEADER_STRING_TYPE,
		// "S");
		request.addFieldInHeader("serviceName", ICOBISTS.HEADER_STRING_TYPE, "cob_procesador..sp_tr_transferencias_ter");
		request.addFieldInHeader("perfil", ICOBISTS.HEADER_NUMBER_TYPE, "13");
		// request.addFieldInHeader("ssn_branch", ICOBISTS.HEADER_NUMBER_TYPE,
		// request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("target", ICOBISTS.HEADER_STRING_TYPE, "SPExecutor");

		request.addFieldInHeader("trn", ICOBISTS.HEADER_NUMBER_TYPE, "1800012");
		request.addFieldInHeader("ofi", ICOBISTS.HEADER_NUMBER_TYPE, "1");
		// request.addFieldInHeader("serviceExecutionId",
		// ICOBISTS.HEADER_STRING_TYPE, request.readValueParam("@ssn_branch"));
		request.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
		request.addFieldInHeader("srv", ICOBISTS.HEADER_STRING_TYPE, "BRANCHSRV");
		request.addFieldInHeader("culture", ICOBISTS.HEADER_STRING_TYPE, "ES-EC");
		request.addFieldInHeader("spType", ICOBISTS.HEADER_STRING_TYPE, "Sybase");
		request.addFieldInHeader("lsrv", ICOBISTS.HEADER_STRING_TYPE, "CTSSRV");
		request.addFieldInHeader("user", ICOBISTS.HEADER_STRING_TYPE, "usuariobv");

		request.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "1800012");
		request.addFieldInHeader("trn_origen", ICOBISTS.HEADER_STRING_TYPE, "API_IN");

		logger.logInfo(request);

		IProcedureResponse responseTransferThirdAccount = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + responseTransferThirdAccount.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de executeTransfer");
		}

		return responseTransferThirdAccount;
	}

	private IProcedureResponse getDataAccountReq(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getDataAccountReq");
		}

		request.setSpName("cob_bvirtual..sp_get_data_account_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_ente"));
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta"));
		request.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta_des"));
		request.addInputParam("@i_val", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_val"));
		request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_concepto"));
		request.addInputParam("@i_detalle", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_detalle"));
		request.addInputParam("@i_comision", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_comision"));
		request.addInputParam("@i_latitud", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_latitud"));
		request.addInputParam("@i_longitud", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_longitud"));
		
		request.addOutputParam("@o_prod", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_prod_des", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_mon", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_mon_des", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_ente_bv", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_login", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_prod_alias", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_nom_beneficiary", ICTSTypes.SQLVARCHAR, "X");
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		aBagSPJavaOrchestration.put("o_prod", wProductsQueryResp.readValueParam("@o_prod"));
		aBagSPJavaOrchestration.put("o_mon", wProductsQueryResp.readValueParam("@o_mon"));
		aBagSPJavaOrchestration.put("o_prod_des", wProductsQueryResp.readValueParam("@o_prod_des"));
		aBagSPJavaOrchestration.put("o_mon_des", wProductsQueryResp.readValueParam("@o_mon_des"));
		aBagSPJavaOrchestration.put("o_prod_alias", wProductsQueryResp.readValueParam("@o_prod_alias"));
		aBagSPJavaOrchestration.put("o_nom_beneficiary", wProductsQueryResp.readValueParam("@o_nom_beneficiary"));
		aBagSPJavaOrchestration.put("o_login", wProductsQueryResp.readValueParam("@o_login"));
		aBagSPJavaOrchestration.put("o_ente_bv", wProductsQueryResp.readValueParam("@o_ente_bv"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking getDataAccountReq DCO : " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getDataAccountReq");
		}

		return wProductsQueryResp;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {

		return null;
	}

	private IProcedureResponse getValAccountReq(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
	
		IProcedureRequest request = new ProcedureRequestAS();
	
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getValAccountReq");
		}
	
		request.setSpName("cobis..sp_val_data_account_api");
	
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_ente"));
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta"));
		request.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta_des"));
		request.addInputParam("@i_val", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_val"));
		request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_concepto"));
		request.addInputParam("@i_detalle", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_detalle"));
		request.addInputParam("@i_comision", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_comision"));
		request.addInputParam("@i_latitud", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_latitud"));
		request.addInputParam("@i_longitud", ICTSTypes.SQLMONEY, aRequest.readValueParam("@i_longitud"));
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking getValAccountReq DCO : " + wProductsQueryResp.getProcedureResponseAsString());
		}
	
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getValAccountReq");
		}
	
		return wProductsQueryResp;
	}

}
