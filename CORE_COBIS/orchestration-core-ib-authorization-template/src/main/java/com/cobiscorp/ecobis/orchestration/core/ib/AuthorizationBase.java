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
import com.cobiscorp.ecobis.ib.application.dtos.PaymentAccountRequest;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.PayRollResponse;
import com.cobiscorp.ecobis.ib.application.dtos.PendingTransactionRequest;
import com.cobiscorp.ecobis.ib.application.dtos.PendingTransactionResponse;
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
public class AuthorizationBase extends SPJavaOrchestrationBase implements ICoreServiceAuthorization{

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
		anOriginalRequest.addInputParam("@i_trn_autorizador", ICTSTypes.SQLVARCHAR, pendingTransactionRequest.getTransactionId());
		anOriginalRequest.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, pendingTransactionRequest.getLogin());
		anOriginalRequest.addInputParam("@i_motivo", ICTSTypes.SQLVARCHAR, pendingTransactionRequest.getReason());
		
		
		IProcedureResponse response = executeCoreBanking(anOriginalRequest);
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta del Core:" + response.getProcedureResponseAsString());

		PendingTransactionResponse pendingTransactionResponse = transformResponse(response);
		return pendingTransactionResponse;
	}
	
	
	public PayRollResponse getPaymentAccounts(PaymentAccountRequest paymentAccountRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: GetPaymentAccounts");
		}

		IProcedureRequest request = new ProcedureRequestAS();

		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		
		request.setSpName("cob_bvirtual..sp_cons_carga_nomina");
		request.addInputParam("@i_operacion", ICTSTypes.SQLINT4, paymentAccountRequest.getOperation());
		request.addInputParam("@i_file_id", ICTSTypes.SQLINT4, paymentAccountRequest.getFileId());
		request.addInputParam("@i_filas_pagina", ICTSTypes.SQLINT4, paymentAccountRequest.getPageRows());

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request: " + request.getProcedureRequestAsString());
		}
		
		/* Ejecuta y obtiene la respuesta */
		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("--->>>>ResponseCOREBANKING: " + pResponse.getProcedureResponseAsString());
		}

		PayRollResponse paymentsAccountsResponse = transformToPaymentAccountResponse(pResponse);
		if (logger.isInfoEnabled()) {
			logger.logInfo("---->>>>TransformResponse--->>>>" + paymentsAccountsResponse);
		}
		return paymentsAccountsResponse;
	}

	
	private PendingTransactionResponse transformResponse(IProcedureResponse response) {
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
	
	private PayRollResponse transformToPaymentAccountResponse(IProcedureResponse aProcedureResponse) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("--->>>START--->>>transformToPaymentAccountResponse");
		}

		PayRollResponse paymentsAccountsResponse = new PayRollResponse();
		List<PaymentAccountResponse> paymentAccountResponseList = new ArrayList<PaymentAccountResponse>();
		PaymentAccountResponse paymentAccountResponse = null;
		if (logger.isInfoEnabled())
			logger.logInfo("aProcedureResponse.getResultSetListSize(): " + aProcedureResponse.getResultSetListSize());
		if (logger.isDebugEnabled()) {
			logger.logDebug(" aProcedureResponse.getProcedureResponseAsString(): " + aProcedureResponse.getProcedureResponseAsString());
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
			paymentAccountResponse.setCurrencyId(columns[COL_MONEDA].getValue()!=null ? Integer.parseInt(columns[COL_MONEDA].getValue()) : null);
			paymentAccountResponse.setProductId(columns[COL_PRODUCTO].getValue()!=null ? Integer.parseInt(columns[COL_PRODUCTO].getValue()) : null);
			paymentAccountResponse.setBlockId(columns[COL_ID_BLOQUEO].getValue()!=null ? Integer.parseInt(columns[COL_ID_BLOQUEO].getValue()) : null);
			paymentAccountResponse.setAmount(columns[COL_MONTO].getValue()!=null ? new BigDecimal(columns[COL_MONTO].getValue()) : null);
			
			paymentAccountResponseList.add(paymentAccountResponse);
		}

		paymentsAccountsResponse.setPaymentAccountList(paymentAccountResponseList);
		paymentsAccountsResponse.setReturnCode(0);;
		paymentsAccountsResponse.setSuccess(true);
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
	
	



}
