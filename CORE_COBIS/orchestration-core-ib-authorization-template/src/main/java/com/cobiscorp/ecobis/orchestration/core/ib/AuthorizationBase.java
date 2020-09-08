/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.ecobis.ib.application.dtos.BlockedAccountRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BlockedAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.PayrollRequest;
import com.cobiscorp.ecobis.ib.application.dtos.PayrollResponse;
import com.cobiscorp.ecobis.ib.application.dtos.PendingTransactionRequest;
import com.cobiscorp.ecobis.ib.application.dtos.PendingTransactionResponse;
import com.cobiscorp.ecobis.ib.application.dtos.UnblockedFundsResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceAuthorization;

/**
 * @author tbaidal
 *
 */

@Component(name = "AuthorizationBase", immediate = false)
@Service(value = { ICoreServiceAuthorization.class })
@Properties(value = { @Property(name = "service.description", value = "AuthorizationBase"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "AuthorizationBase") })
public class AuthorizationBase extends SPJavaOrchestrationBase implements ICoreServiceAuthorization {

	private static ILogger logger = LogFactory.getLogger(AuthorizationBase.class);
	private static final String CLASS_NAME = " >-----> ";
	protected static final String COBIS_CONTEXT = "COBIS";
	protected static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	private static final int COL_CUENTA = 0;
	private static final int COL_MONEDA = 1;
	private static final int COL_PRODUCTO = 2;
	private static final int COL_ID_BLOQUEO = 3;
	private static final int COL_MONTO = 4;

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public PendingTransactionResponse changeTransactionStatus(PendingTransactionRequest pendingTransactionRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando rechazo de transaccion LOCAL COBIS.");

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_SSN, ICOBISTS.HEADER_NUMBER_TYPE,
				String.valueOf(pendingTransactionRequest.getReferenceNumber()));
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH, ICOBISTS.HEADER_NUMBER_TYPE,
				String.valueOf(pendingTransactionRequest.getReferenceNumberBranch()));
		anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18790");
		anOriginalRequest.setSpName("cob_bvirtual..sp_autoriza_pendientes_cw_bv");

		anOriginalRequest.addInputParam("@s_cliente", ICTSTypes.SYBINT4, pendingTransactionRequest.getEntityId());
		anOriginalRequest.addInputParam("@s_servicio", ICTSTypes.SYBINT2, pendingTransactionRequest.getChannelId());
		anOriginalRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, pendingTransactionRequest.getOperation());
		anOriginalRequest.addInputParam("@i_trn_autorizador", ICTSTypes.SQLVARCHAR,
				pendingTransactionRequest.getTransactionId());
		anOriginalRequest.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, pendingTransactionRequest.getLogin());
		anOriginalRequest.addInputParam("@i_motivo", ICTSTypes.SQLVARCHAR, pendingTransactionRequest.getReason());

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta del Core:" + response.getProcedureResponseAsString());

		PendingTransactionResponse pendingTransactionResponse = transformToPendingResponse(response);
		return pendingTransactionResponse;
	}

	public PayrollResponse getPaymentAccounts(PayrollRequest paymentAccountRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: GetPaymentAccounts");
		}

		IProcedureRequest request = new ProcedureRequestAS();

		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);

		request.setSpName("cob_bvirtual..sp_cons_carga_nomina");
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, paymentAccountRequest.getOperation());
		request.addInputParam("@i_trn_autorizador", ICTSTypes.SQLINT4, paymentAccountRequest.getPendingTransaction());
		request.addInputParam("@i_filas_pagina", ICTSTypes.SQLINT4, paymentAccountRequest.getPageRows());

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request:" + request.getProcedureRequestAsString());
		}

		/* Ejecuta y obtiene la respuesta */
		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("--->>>>ResponseCOREBANKING: " + pResponse.getProcedureResponseAsString());
		}

		PayrollResponse paymentsAccountsResponse = transformToPayrollResponse(pResponse);
		if (logger.isInfoEnabled()) {
			logger.logInfo("---->>>>TransformResponse--->>>>" + paymentsAccountsResponse);
		}
		return paymentsAccountsResponse;
	}

	private PendingTransactionResponse transformToPendingResponse(IProcedureResponse response) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando Transformacion de Respuesta");

		PendingTransactionResponse pendingTransactionResponse = new PendingTransactionResponse();

		if (response == null) {
			if (logger.isInfoEnabled())
				logger.logInfo("PendingTransactionResponse --> Response null");
			return null;
		}

		if (response.getReturnCode() != 0)
			pendingTransactionResponse.setMessages(Utils.returnArrayMessage(response));

		pendingTransactionResponse.setReturnCode(response.getReturnCode());
		pendingTransactionResponse.setSuccess(response.getReturnCode() == 0 ? true : false);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta -> " + pendingTransactionResponse);
		return pendingTransactionResponse;
	}

	private PayrollResponse transformToPayrollResponse(IProcedureResponse aProcedureResponse) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("--->>>START--->>>transformToPayrollResponse");
		}
		
		if (logger.isInfoEnabled())
			logger.logInfo("aProcedureResponse.getResultSetListSize(): " + aProcedureResponse.getResultSetListSize());
		if (logger.isDebugEnabled()) {
			logger.logDebug(" aProcedureResponse.getProcedureResponseAsString(): "
					+ aProcedureResponse.getProcedureResponseAsString());
		}

		PayrollResponse paymentsAccountsResponse = new PayrollResponse();
		List<PaymentAccountResponse> paymentAccountResponseList = new ArrayList<PaymentAccountResponse>();
		PaymentAccountResponse paymentAccountResponse = null;
		
		if (paymentsAccountsResponse == null) {
			if (logger.isInfoEnabled())
				logger.logInfo("PendingTransactionResponse --> Response null");
			return null;
		}
		
		IResultSetRow[] rowsPaymentAccounts = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

		for (IResultSetRow iResultSetRow : rowsPaymentAccounts) {
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
			paymentAccountResponse = new PaymentAccountResponse();

			if (logger.isDebugEnabled()) {
				logger.logDebug("---->>>>>Size columns:" + columns.length);
			}
			if (logger.isDebugEnabled()) {
				logger.logDebug("---->>>>COL_NANE:" + columns[COL_CUENTA].getValue());
				logger.logDebug("---->>>>COL_NANE:" + columns[COL_MONEDA].getValue());
				logger.logDebug("---->>>>COL_NANE:" + columns[COL_PRODUCTO].getValue());
				logger.logDebug("---->>>>COL_NANE:" + columns[COL_ID_BLOQUEO].getValue());
				logger.logDebug("---->>>>COL_NANE:" + columns[COL_MONTO].getValue());
			}
			paymentAccountResponse.setAccount(columns[COL_CUENTA].getValue());
			paymentAccountResponse.setCurrencyId(
					columns[COL_MONEDA].getValue() != null ? Integer.parseInt(columns[COL_MONEDA].getValue()) : null);
			paymentAccountResponse.setProductId(
					columns[COL_PRODUCTO].getValue() != null ? Integer.parseInt(columns[COL_PRODUCTO].getValue())
							: null);
			paymentAccountResponse.setBlockId(
					columns[COL_ID_BLOQUEO].getValue() != null ? Integer.parseInt(columns[COL_ID_BLOQUEO].getValue())
							: null);
			paymentAccountResponse.setAmount(
					columns[COL_MONTO].getValue() != null ? new BigDecimal(columns[COL_MONTO].getValue()) : null);

			paymentAccountResponseList.add(paymentAccountResponse);
		}

		paymentsAccountsResponse.setPaymentAccountList(paymentAccountResponseList);
		
		if (aProcedureResponse.getReturnCode() != 0) {
			paymentsAccountsResponse.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		}
		
		paymentsAccountsResponse.setReturnCode(aProcedureResponse.getReturnCode());
		paymentsAccountsResponse.setSuccess(aProcedureResponse.getReturnCode() == 0 ? true : false);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("---->>>>paymentsAccountsResponse:" + paymentsAccountsResponse);
		}
		return paymentsAccountsResponse;

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	
	private UnblockedFundsResponse transformToUnblockedFundsResponse(IProcedureResponse aProcedureResponse) {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: transformToUnblockedFundsResponse");
		}

		UnblockedFundsResponse unblockedFundsResponse = new UnblockedFundsResponse();
		
		if (aProcedureResponse.getReturnCode() != 0)
			unblockedFundsResponse.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		
		unblockedFundsResponse.setSuccess(aProcedureResponse.getReturnCode() == 0 ? true : false);
		unblockedFundsResponse.setReturnCode(aProcedureResponse.getReturnCode());

		return unblockedFundsResponse;
	}

	@Override
	public BlockedAccountResponse saveBlockedAccountTmp(BlockedAccountRequest blockedAccountRequest)
			throws CTSServiceException, CTSInfrastructureException {
		
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: saveBlockedAccountTmp");
		}

		IProcedureRequest request = new ProcedureRequestAS();

		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);

		request.setSpName("cobis..sp_bv_cuenta_pago_nomina");
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, blockedAccountRequest.getOperation());
		request.addInputParam("@i_file_id", ICTSTypes.SQLINT4, blockedAccountRequest.getFileId());
		request.addInputParam("@i_numero_cuenta", ICTSTypes.SQLVARCHAR, blockedAccountRequest.getAccount());
		request.addInputParam("@i_moneda", ICTSTypes.SQLINT4, blockedAccountRequest.getCurrencyId());
		request.addInputParam("@i_producto", ICTSTypes.SQLINT4, blockedAccountRequest.getProductId());
		request.addInputParam("@i_bloqueo", ICTSTypes.SQLINT4, blockedAccountRequest.getBlockId());
		request.addInputParam("@i_monto", ICTSTypes.SQLMONEY, blockedAccountRequest.getAmount());
		
		//request.addOutputParam("@o_msg", ICTSTypes.SQLVARCHAR, "");

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request: " + request.getProcedureRequestAsString());
		}

		/* Ejecuta y obtiene la respuesta */
		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("--->>>>ResponseCOREBANKING: " + pResponse.getProcedureResponseAsString());
		}

		BlockedAccountResponse blockedAccountResponse = transformToBlockedAccountResponse(pResponse);
		if (logger.isInfoEnabled()) {
			logger.logInfo("---->>>>blockedFundResponse--->>>>" + blockedAccountResponse);
		}
		return blockedAccountResponse;
	}

	private BlockedAccountResponse transformToBlockedAccountResponse(IProcedureResponse pResponse) {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: transformToBlockedAccountResponse");
		}
		
		BlockedAccountResponse blockedAccountResponse = new BlockedAccountResponse();
		
		if (pResponse.getReturnCode() != 0)
			blockedAccountResponse.setMessages(Utils.returnArrayMessage(pResponse));
		
		blockedAccountResponse.setSuccess(pResponse.getReturnCode() == 0 ? true : false);
		blockedAccountResponse.setReturnCode(pResponse.getReturnCode());

		return blockedAccountResponse;
	}

	@Override
	public UnblockedFundsResponse unblockFunds(PayrollRequest payrollRequest) {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: unblockFunds");
		}
		
		IProcedureRequest request = new ProcedureRequestAS();

		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);

		request.setSpName("cobis..sp_bc_payment_commission");
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, payrollRequest.getOperation());
		request.addInputParam("@i_servicio", ICTSTypes.SQLINT4, payrollRequest.getChannel());
		request.addInputParam("@i_file_id", ICTSTypes.SQLINT4, payrollRequest.getFileId());
		request.addInputParam("@i_masivo", ICTSTypes.SQLINT4, payrollRequest.getMassive());
		request.addOutputParam("@o_msg", ICTSTypes.SQLVARCHAR, "");

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request: " + request.getProcedureRequestAsString());
		}

		/* Ejecuta y obtiene la respuesta */
		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("--->>>>ResponseCOREBANKING: " + pResponse.getProcedureResponseAsString());
		}

		UnblockedFundsResponse unblockedFundsResponse = transformToUnblockedFundsResponse(pResponse);
		if (logger.isInfoEnabled()) {
			logger.logInfo("---->>>>blockedFundResponse--->>>>" + unblockedFundsResponse);
		}
		return unblockedFundsResponse;
	}
}
