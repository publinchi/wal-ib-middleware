package com.cobiscorp.ecobis.orchestration.core.ib.authorization;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.BlockedAccountRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BlockedAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.PayrollRequest;
import com.cobiscorp.ecobis.ib.application.dtos.PayrollResponse;
import com.cobiscorp.ecobis.ib.application.dtos.PendingTransactionRequest;
import com.cobiscorp.ecobis.ib.application.dtos.PendingTransactionResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceAuthorization;

/**
 * Plugin of between accounts transfers
 *
 * @since Ago 25, 2020
 * @author tbaidal
 * @version 1.0.0
 *
 */
@Component(name = "AuthorizationOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "AuthorizationOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "AuthorizationOrchestrationCore") })

public class AuthorizationOrchestrationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(AuthorizationOrchestrationCore.class);
	private static final String CORESERVICEAUTHORIZATION = "coreServiceAuthorization";
	private static final String CORE_SERVER = "coreServer";
	static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	protected static final String CLASS_NAME = " >-----> ";
	private static final String OPERATION_UNBLOCK = "R";
	private static final String PAYROLL_RESPONSE = "PAYROLL_RESPONSE";
	private static final Object PAYMENT_ACCOUNT = "PAYMENT_ACCOUNT";
	private static final String RESPONSE_UNBLOCK_ACCOUNT = "RESPONSE_UNBLOCK_ACCOUNT";

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Reference(referenceInterface = ICoreServiceAuthorization.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceAuthorization", unbind = "unbindCoreServiceAuthorization")
	protected ICoreServiceAuthorization coreServiceAuthorization;

	public void bindCoreServiceAuthorization(ICoreServiceAuthorization service) {
		coreServiceAuthorization = service;
	}

	public void unbindCoreServiceAuthorization(ICoreServiceAuthorization service) {
		coreServiceAuthorization = null;
	}

	public ICoreServer getCoreServer() {
		return coreServer;
	}

	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreServer(ICoreServer service) {
		coreServer = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreServer(ICoreServer service) {
		coreServer = null;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo("AuthorizationOrchestrationCore: executeJavaOrchestration");
		
		

		try {

			Map<String, Object> mapInterfaces = new HashMap<String, Object>();

			mapInterfaces.put(CORESERVICEAUTHORIZATION, coreServiceAuthorization);
			mapInterfaces.put(CORE_SERVER, coreServer);

			Utils.validateComponentInstance(mapInterfaces);

			aBagSPJavaOrchestration.put(CORESERVICEAUTHORIZATION, coreServiceAuthorization);
			aBagSPJavaOrchestration.put(CORE_SERVER, coreServer);

			executeStepsTransactionsBase(anOriginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}
		return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}

	protected IProcedureResponse executeStepsTransactionsBase(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		IProcedureResponse responseProc = null;
		PayrollResponse payRollResponse = null;
		int contador = 0;
		try {
			if (logger.isInfoEnabled()) {
				logger.logInfo("*********  anOriginalRequest.readValueParam(@i_file_id) " + anOriginalRequest.readValueParam("@i_file_id"));
			}
			
			// LOCAL - Rechazar transacción
			responseProc = executeTransaction(anOriginalRequest, aBagSPJavaOrchestration);
			
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseProc);
			if (Utils.flowError("changeTransactionStatus", responseProc)) {
				return responseProc;
			}
			

			if (anOriginalRequest != null && anOriginalRequest.readValueParam("@i_file_id")!=null && Integer.parseInt(anOriginalRequest.readValueParam("@i_file_id"))!=0) {
				if (logger.isInfoEnabled()) {
					logger.logInfo("*********  anOriginalRequest.readValueParam(@i_file_id) " + anOriginalRequest.readValueParam("@i_file_id"));
				}
				
				// LOCAL - Consultar cuentas
				PayrollRequest payrollRequest = transformToPayrollRequest(anOriginalRequest.clone());
				payRollResponse = coreServiceAuthorization.getPaymentAccounts(payrollRequest);

				StringBuilder messageGetPaymentAccounts = new StringBuilder();
				messageGetPaymentAccounts.append((String) aBagSPJavaOrchestration.get(PAYMENT_ACCOUNT));
				
				aBagSPJavaOrchestration.put(PAYROLL_RESPONSE, payRollResponse);
				if (!payRollResponse.getSuccess()) {
					aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, payRollResponse);
					return Utils.returnException(payRollResponse.getReturnCode(), new StringBuilder(messageGetPaymentAccounts).append(payRollResponse.getMessage()).toString());
				}
				
				for (PaymentAccountResponse paymentAccountResponse : payRollResponse.getPaymentAccountList()) {
					//CENTRAL - Desbloquear cuenta
					BlockedAccountRequest blockedAccountRequest = new BlockedAccountRequest();
					blockedAccountRequest.setFileId(payrollRequest.getFileId());
					blockedAccountRequest.setOperation(OPERATION_UNBLOCK);
					blockedAccountRequest.setAccount(paymentAccountResponse.getAccount());
					blockedAccountRequest.setProductId(paymentAccountResponse.getProductId()!=null ? String.valueOf(paymentAccountResponse.getProductId()) : null);
					blockedAccountRequest.setCurrencyId(paymentAccountResponse.getCurrencyId()!=null ? String.valueOf(paymentAccountResponse.getCurrencyId()) : null);
					blockedAccountRequest.setBlockId(paymentAccountResponse.getBlockId()!=null ? String.valueOf(paymentAccountResponse.getBlockId()) : null);
					blockedAccountRequest.setAmount(paymentAccountResponse.getAmount()!=null ? String.valueOf(paymentAccountResponse.getAmount()) : null );
					blockedAccountRequest.setIndex(contador);
					BlockedAccountResponse blockedAccountResponse = coreServiceAuthorization.unblockAccount(blockedAccountRequest);
					contador = contador +1;
					if (blockedAccountResponse!=null && !blockedAccountResponse.getSuccess()) {
						responseProc.addMessage(blockedAccountResponse.getReturnCode(), "ERROR. ARCHIVO: "+payrollRequest.getFileId()+" CUENTA: "+paymentAccountResponse.getAccount());
					}
				}
				
			}

			return responseProc;
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.logInfo("*********  Error en " + e.getMessage(), e);
			}

			IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
			ErrorBlock eb = new ErrorBlock(-1, "Service is not available");
			wProcedureRespFinal.addResponseBlock(eb);
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
			wProcedureRespFinal.setReturnCode(-1);
			return wProcedureRespFinal;
		}

	}

	private IProcedureResponse executeTransaction(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		PendingTransactionRequest pendingTransactionRequest = transformToPendingTransactionRequest(
				anOriginalRequest.clone());
		PendingTransactionResponse pendingTransactionResponse = new PendingTransactionResponse();

		IProcedureResponse wProcedureResponse = null;
		try {
			Integer fileId = anOriginalRequest.readValueParam("@i_file_id")!=null ? Integer.parseInt(anOriginalRequest.readValueParam("@i_file_id")) : null;

			if (fileId==null || fileId == 0) {
				if (logger.isInfoEnabled()) {
					logger.logInfo("*********  Ingresa a changeTransactionStatus");
				}

				pendingTransactionResponse = coreServiceAuthorization.changeTransactionStatus(pendingTransactionRequest);
			}else {
				if (logger.isInfoEnabled()) {
					logger.logInfo("*********  Ingresa a changeTransactionStatusCash");
				}
				pendingTransactionResponse = coreServiceAuthorization.changeTransactionStatusCash(pendingTransactionRequest);
			}
			
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}

		wProcedureResponse = transformProcedureResponse(pendingTransactionResponse, aBagSPJavaOrchestration);

		return wProcedureResponse;
	}

	private IProcedureResponse transformProcedureResponse(PendingTransactionResponse pendingTransactionResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo("transformProcedureResponse " + pendingTransactionResponse.toString());

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		if (pendingTransactionResponse.getReturnCode() != 0) {
			// Si estamos en linea y hubo error
			wProcedureResponse = Utils.returnException(pendingTransactionResponse.getMessages());
			wProcedureResponse.setReturnCode(pendingTransactionResponse.getReturnCode());

			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, wProcedureResponse);

			return wProcedureResponse;
		}

		if (!pendingTransactionResponse.getSuccess()) {

			wProcedureResponse = Utils.returnException(pendingTransactionResponse.getMessages());
			wProcedureResponse.setReturnCode(pendingTransactionResponse.getReturnCode());
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, wProcedureResponse);
		}

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final PendingTransactionResponse --> "
					+ wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;
	}

	private PendingTransactionRequest transformToPendingTransactionRequest(IProcedureRequest aRequest) {
		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform-->" + aRequest.getProcedureRequestAsString());

		String messageError = null;
		messageError = aRequest.readValueParam("@s_cliente") == null ? " - @s_cliente can't be null" : "";
		messageError += aRequest.readValueParam("@s_ssn") == null ? " - @s_ssn can't be null" : "";
		messageError += aRequest.readValueParam("@s_ssn_branch") == null ? " - @s_ssn_branch can't be null" : "";
		messageError += aRequest.readValueParam("@s_servicio") == null ? " - @s_servicio can't be null" : "";
		messageError += aRequest.readValueParam("@i_operacion") == null ? " - @i_operacion can't be null" : "";
		messageError += aRequest.readValueParam("@i_trn_autorizador") == null ? " - @i_trn_autorizador can't be null"
				: "";
		messageError += aRequest.readValueParam("@i_login") == null ? " - @i_login can't be null" : "";
		messageError += aRequest.readValueParam("@i_motivo") == null ? " - @i_motivo can't be null" : "";

		if (logger.isDebugEnabled())
			logger.logDebug("messageError->" + messageError);

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		if (logger.isDebugEnabled())
			logger.logDebug("aRequest->" + aRequest);

		PendingTransactionRequest pendingTransactionRequest = new PendingTransactionRequest();

		pendingTransactionRequest.setReferenceNumber(aRequest.readValueParam("@s_ssn"));
		pendingTransactionRequest.setReferenceNumberBranch(aRequest.readValueParam("@s_ssn_branch"));
		pendingTransactionRequest.setChannelId(aRequest.readValueParam("@s_servicio"));
		pendingTransactionRequest.setEntityId(aRequest.readValueParam("@s_cliente"));
		pendingTransactionRequest.setLogin(aRequest.readValueParam("@i_login"));
		pendingTransactionRequest.setTransactionId(aRequest.readValueParam("@i_trn_autorizador"));
		pendingTransactionRequest.setOperation(aRequest.readValueParam("@i_operacion"));
		pendingTransactionRequest.setReason(aRequest.readValueParam("@i_motivo"));

		return pendingTransactionRequest;
	}
	
	/**************************************************************************/
	private PayrollRequest transformToPayrollRequest(IProcedureRequest aRequest) {
		PayrollRequest payrollRequest = new PayrollRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("<<<Procedure Request to Transform->>>" + aRequest.getProcedureRequestAsString());
	
		payrollRequest.setFileId(aRequest.readValueParam("@i_file_id"));
		payrollRequest.setOperation("S");
		payrollRequest.setPageRows(aRequest.readValueParam("@i_filas_pagina"));
		payrollRequest.setPendingTransaction(aRequest.readValueParam("@i_trn_autorizador"));
		return payrollRequest;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
	}

}
