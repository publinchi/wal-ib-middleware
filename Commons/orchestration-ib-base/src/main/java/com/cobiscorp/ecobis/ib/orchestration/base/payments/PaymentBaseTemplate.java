package com.cobiscorp.ecobis.ib.orchestration.base.payments;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.AccountingParameterRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AccountingParameterResponse;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationResponse;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TransactionMonetaryRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TransactionMonetaryResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ValidationAccountsRequest;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountingParameter;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Secuential;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;
import com.cobiscorp.ecobis.orchestration.core.ib.common.AccountCoreSignersValidation;

/**
 * @author cecheverria
 * @description This class implement logic to apply a payment
 */

public abstract class PaymentBaseTemplate extends SPJavaOrchestrationBase {

	protected static final String CLASS_NAME = " >-----> ";
	private static final String COBIS_CONTEXT = "COBIS";
	protected static final String SSN_BRANCH = "SSN_BRANCH";
	protected static final String SSN = "SSN";
	protected static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	protected static final String RESPONSE_SERVER = "RESPONSE_SERVER";
	protected static final String RESPONSE_OFFLINE = "RESPONSE_OFFLINE";
	protected static final String RESPONSE_CORE_SIGNERS = "RESPONSE_CORE_SIGNERS";
	protected static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	protected static final String RESPONSE_BALANCE = "RESPONSE_BALANCE";
	protected static final String RESPONSE_VALIDATE_LOCAL = "RESPONSE_VALIDATE_LOCAL";
	protected static final String RESPONSE_UPDATE_LOCAL = "RESPONSE_UPDATE_LOCAL";
	protected static final String RESPONSE_NOTIFICATION = "RESPONSE_NOTIFICATION";
	protected static final String RESPONSE_BV_TRANSACTION = "RESPONSE_BV_TRANSACTION";
	protected static final String PAYMENT_NAME = "PAYMENT_NAME";
	protected static final String LOG_MESSAGE = "LOG_MESSAGE";
	protected static final String REENTRY_EXE = "reentryExecution";
	protected static final String ACCOUNTING_PARAMETER = "ACCOUNTING_PARAMETER";
	protected static final String ESTADO = "ESTADO";
	
	protected static final int CODE_OFFLINE = 40004;
	protected static final int CODE_OFFLINE_NO_BAL = 40002;
	public boolean SUPPORT_OFFLINE = false;
	public boolean VALIDATE_PREVIOUS = false;

	private static ILogger logger = LogFactory.getLogger(PaymentBaseTemplate.class);

	/**
	 * Methods to get Depends Inyection
	 */
	protected abstract ICoreServer getCoreServer();

	protected abstract ICoreService getCoreService();

	protected abstract ICoreServiceSendNotification getCoreServiceNotification();

	protected abstract ICoreServiceMonetaryTransaction getCoreServiceMonetaryTransaction();

	/**
	 * Method to validate some info in Core , this not apply all transactions
	 */
	protected abstract IProcedureResponse validatePreviousExecution(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration);

	protected abstract IProcedureResponse executeTransaction(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException;

	/**
	 * Transform Notification Request input param
	 */
	protected abstract NotificationRequest transformNotificationRequest(IProcedureRequest aProcedureRequest, OfficerByAccountResponse anOfficer);

	/**
	 * Afecct payment in Destinations product
	 */
	protected abstract IProcedureResponse payDestinationProduct(IProcedureRequest aProcedureRequest, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException;

	protected abstract void addParametersRequestUpdateLocal(IProcedureRequest aProcedureRequest, IProcedureRequest anOriginalRequest);

	private Map<String, AccountingParameter> existsAccountingParameter(AccountingParameterResponse anAccountingParameterResponse, int product, String type) {

		Map<String, AccountingParameter> map = null;

		if (anAccountingParameterResponse.getAccountingParameters().size() == 0)
			return map;

		for (AccountingParameter parameter : anAccountingParameterResponse.getAccountingParameters()) {
			if (logger.isDebugEnabled())
				logger.logDebug(" TRN: " + String.valueOf(parameter.getTransaction()) + " CAUSA: " + parameter.getCause() + " TIPO :" + parameter.getTypeCost());
			if (parameter.getTypeCost().equals(type) && parameter.getProductId() == product) {
				map = new HashMap<String, AccountingParameter>();
				map.put("ACCOUNTING_PARAM", parameter);
				break;
			}
		}
		return map;
	}

	/**
	 * Execute Payment (getAccountingParameter, debitAccount,
	 * getValueCommission, DebitCommission
	 */
	protected IProcedureResponse executePayment(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando executePayment: " + request);

		AccountingParameterResponse responseAccountingParameters = null;
		AccountingParameterRequest anAccountingParameterRequest = null;
		TransactionMonetaryRequest aTransactionMonetaryRequest = null;
		TransactionMonetaryResponse aTransactionMonetaryResponse = null;
		IProcedureResponse responseExecutePayment = null;

		IProcedureResponse responsePayDestinationProduct = null;
		Map<String, AccountingParameter> map = null;

		StringBuilder messageErrorPayment = new StringBuilder();
		messageErrorPayment.append((String) aBagSPJavaOrchestration.get(PAYMENT_NAME));

		//Obtiene Transacciones y Causa del movimiento a aplicar
		anAccountingParameterRequest = new AccountingParameterRequest();
		anAccountingParameterRequest.setOriginalRequest(request);
		anAccountingParameterRequest.setTransaction(Integer.parseInt(request.readValueParam("@t_trn")));

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Executing executePayment: Obtiene Transacciones y Causas getAccountingParameter " + anAccountingParameterRequest);

		responseAccountingParameters = getCoreServiceMonetaryTransaction().getAccountingParameter(anAccountingParameterRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Executing executePayment: Respuesta Transacciones y Causas getAccountingParameter " + responseAccountingParameters.getAccountingParameters());

		if (!responseAccountingParameters.getSuccess())
			return Utils.returnException(responseAccountingParameters.getMessages());

		//Valida que exista la parametrizacion de la TRANSACCION
		map = existsAccountingParameter(responseAccountingParameters, Integer.parseInt(request.readValueParam("@i_prod")), "T");
		if (Utils.isNull(map)) {
			responseExecutePayment = Utils.returnException(new StringBuilder(messageErrorPayment).append(" ERROR OBTENIENDO PARAMETROS TRANSACCION").toString());
			return responseExecutePayment;
		}
		
		//Ejecuta el debito respectivo a la cuenta origen
		aTransactionMonetaryRequest = new TransactionMonetaryRequest();
		aTransactionMonetaryRequest.setOriginalRequest(request);
		Product product = new Product();
		Currency currency = new Currency();
		product.setProductNumber(request.readValueParam("@i_cta"));
		product.setProductType(Integer.parseInt(request.readValueParam("@i_prod")));
		currency.setCurrencyId(Integer.parseInt(request.readValueParam("@i_mon")));
		product.setCurrency(currency);
		aTransactionMonetaryRequest.setProduct(product);
		aTransactionMonetaryRequest.setConcept(request.readValueParam("@i_concepto"));
		aTransactionMonetaryRequest.setAlternateCode(0);
		aTransactionMonetaryRequest.setChannelId("1");
		aTransactionMonetaryRequest.setTransaction(map.get("ACCOUNTING_PARAM").getTransaction());
		aTransactionMonetaryRequest.setCause(map.get("ACCOUNTING_PARAM").getCause());
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Executing executePayment CAUSA PARA GENERAL " + aTransactionMonetaryRequest.getCause());

		aTransactionMonetaryRequest.setAmmount(new BigDecimal(request.readValueParam("@i_val")));
		aTransactionMonetaryRequest.setCorrection("N");
		aTransactionMonetaryRequest.setSourceFunds(request.readValueParam("@i_origen_fondos"));
		aTransactionMonetaryRequest.setUseFunds(request.readValueParam("@i_dest_fondos"));
		// Envio de moneda del monto a pagar para compra venta implicita
		if (request.readValueParam("@i_mon_pag") != null)
			aTransactionMonetaryRequest.setPayCurrency(Integer.parseInt(request.readValueParam("@i_mon_pag")));
		else
			aTransactionMonetaryRequest.setPayCurrency(Integer.parseInt(request.readValueParam("@i_mon")));

		//Valida que exista la parametrizacion de la COMISION
		map = null;
		map = existsAccountingParameter(responseAccountingParameters, Integer.parseInt(request.readValueParam("@i_prod")), "C");
		aTransactionMonetaryRequest.setCauseComi("0");
		if (!Utils.isNull(map)) {
				aTransactionMonetaryRequest.setCauseComi(map.get("ACCOUNTING_PARAM").getCause());
				aTransactionMonetaryRequest.setAmmountCommission(new BigDecimal(request.readValueParam("@i_comi_val")));
		}
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "Executing executePayment Values of ssn_branch: " + request.readValueParam("@s_ssn_branch"));
			logger.logInfo(CLASS_NAME + "Executing executePayment Values of ssn: " + request.readValueParam("@s_ssn"));
		}

		if (request.readValueParam("@s_ssn_branch") != null) {
			aTransactionMonetaryRequest.setReferenceNumberBranch(request.readValueParam("@s_ssn_branch"));
			aBagSPJavaOrchestration.put(SSN_BRANCH, request.readValueParam("@s_ssn_branch"));
		}			
			
		if (request.readValueParam("@s_ssn") != null) {
			aTransactionMonetaryRequest.setReferenceNumber(request.readValueParam("@s_ssn"));
			aBagSPJavaOrchestration.put(SSN, request.readValueParam("@s_ssn"));
		}
			

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Executing executePayment Ejecuta el DEBITO " + aTransactionMonetaryRequest.toString());

		if (request.readValueParam("@i_reversa") != null &&  !"S".equals(request.readValueParam("@i_reversa"))) {
			aTransactionMonetaryResponse = getCoreServiceMonetaryTransaction().debitCreditAccount(aTransactionMonetaryRequest);
		
			if (logger.isInfoEnabled()) {
			   logger.logInfo(CLASS_NAME + "Executing executePayment JCB Reversa " + aTransactionMonetaryResponse.toString());
			   logger.logInfo(CLASS_NAME + "Executing executePayment Respuesta de ejecucion del DEBITO " + aTransactionMonetaryResponse.toString());
			}

			if (!aTransactionMonetaryResponse.getSuccess())
			   return Utils.returnException(aTransactionMonetaryResponse.getMessages());
			
			responsePayDestinationProduct = payDestinationProduct(request, aBagSPJavaOrchestration);
			//Object codeResponse = aBagSPJavaOrchestration.get("codigoResponse"); // se-establece-en-pagoTarjetas

			if (logger.isDebugEnabled()) {
				logger.logDebug(CLASS_NAME + " responsePayDestinationProduct.getProcedureResponseAsString()" + responsePayDestinationProduct.getProcedureResponseAsString());
				logger.logDebug(CLASS_NAME + " responsePayDestinationProduct.getParams()" + responsePayDestinationProduct.getParams());
			}
			
			if("01".equals(responsePayDestinationProduct.readValueParam("@o_cod_respuesta"))) {
				aBagSPJavaOrchestration.put(ESTADO, "R");
				responsePayDestinationProduct.addParam("@o_trn_estado", ICTSTypes.SQLCHAR, 0, "R");
			} else if ("82".equals(responsePayDestinationProduct.readValueParam("@o_cod_respuesta"))) {
				aBagSPJavaOrchestration.put(ESTADO, "P");
				responsePayDestinationProduct.addParam("@o_trn_estado", ICTSTypes.SQLCHAR, 0, "P");
			} else {
				aBagSPJavaOrchestration.put(ESTADO, "C");
				responsePayDestinationProduct.addParam("@o_trn_estado", ICTSTypes.SQLCHAR, 0, "C");
				if(aTransactionMonetaryRequest.getAmmount().compareTo(BigDecimal.ZERO) != 0 && aTransactionMonetaryRequest.getAmmountCommission().compareTo(BigDecimal.ZERO) != 0) {
					//Ejecuta el reverso del DEBITO		
					if (logger.isDebugEnabled()) {
						logger.logDebug(CLASS_NAME + "Executing executePayment Ejecuta REVERSO DEL DEBITO " + aTransactionMonetaryRequest.toString());					
					}
					logger.logError(CLASS_NAME + messageErrorPayment);
					aTransactionMonetaryRequest.setCorrection("S");
					aTransactionMonetaryRequest.setSsnCorrection(Integer.parseInt(request.readValueParam("@s_ssn_branch")));            //
					aTransactionMonetaryRequest.setAlternateCode(0);
					
					aTransactionMonetaryResponse = getCoreServiceMonetaryTransaction().debitCreditAccount(aTransactionMonetaryRequest);
					
					if (logger.isInfoEnabled())
						logger.logInfo(CLASS_NAME + "Executing executePayment Respuesta de ejecucion del REVERSO DEL DEBITO " + aTransactionMonetaryResponse.toString());

					if (!aTransactionMonetaryResponse.getSuccess())
						return Utils.returnException(aTransactionMonetaryResponse.getMessages());
				}
			}
			
			saveTranPagoServ(request, responsePayDestinationProduct, aBagSPJavaOrchestration); 
		}	
		
		if (request.readValueParam("@i_reversa") != null &&  "S".equals(request.readValueParam("@i_reversa"))) {
			/*
			 * Ejecuta el reverso del DEBITO
			 */
			logger.logError(CLASS_NAME + messageErrorPayment);
			aTransactionMonetaryRequest.setCorrection("S");
			aTransactionMonetaryRequest.setSsnCorrection(Integer.parseInt(request.readValueParam("@i_ssn_branch")));
            //aTransactionMonetaryRequest.setReferenceNumber(request.readValueParam("@i_ssn"));
			aTransactionMonetaryRequest.setAlternateCode(0);

			if (logger.isDebugEnabled())
				logger.logDebug(CLASS_NAME + "Executing executePayment Ejecuta REVERSO DEL DEBITO " + aTransactionMonetaryRequest.toString());

			aTransactionMonetaryResponse = getCoreServiceMonetaryTransaction().debitCreditAccount(aTransactionMonetaryRequest);

			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + "Executing executePayment Respuesta de ejecucion del REVERSO DEL DEBITO " + aTransactionMonetaryResponse.toString());

			if (!aTransactionMonetaryResponse.getSuccess())
				return Utils.returnException(aTransactionMonetaryResponse.getMessages());
		}		
		
		responseExecutePayment = responsePayDestinationProduct;

		return responseExecutePayment;
	}

	protected ValidationAccountsRequest transformToValidationAccountRequest(IProcedureRequest anOriginalRequest) {

		ValidationAccountsRequest request = new ValidationAccountsRequest();

		Product originProduct = new Product();
		Currency originCurrency = new Currency();
		if (anOriginalRequest.readValueParam("@i_cta") != null)
			originProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta"));
		if (anOriginalRequest.readValueParam("@i_prod") != null)
			originProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod").toString()));
		if (anOriginalRequest.readValueParam("@i_mon") != null)
			originCurrency.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon").toString()));

		originProduct.setCurrency(originCurrency);

		Product destinationProduct = new Product();
		Currency destinationCurrency = new Currency();
		if (anOriginalRequest.readValueParam("@i_cta_des") != null)
			destinationProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta_des"));
		if (anOriginalRequest.readValueParam("@i_prod_des") != null)
			destinationProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod_des").toString()));
		if (anOriginalRequest.readValueParam("@i_mon_des") != null)
			destinationCurrency.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon_des").toString()));

		destinationProduct.setCurrency(destinationCurrency);

		Secuential originSSn = new Secuential();
		if (anOriginalRequest.readValueParam("@s_ssn") != null)
			originSSn.setSecuential(anOriginalRequest.readValueParam("@s_ssn").toString());
		if (anOriginalRequest.readValueParam("@s_servicio") != null)
			request.setChannelId(anOriginalRequest.readValueParam("@s_servicio"));
		if (anOriginalRequest.readValueParam("@t_trn") != null)
			request.setCodeTransactionalIdentifier(anOriginalRequest.readValueParam("@t_trn"));

		request.setSecuential(originSSn);
		request.setOriginProduct(originProduct);
		request.setDestinationProduct(destinationProduct);
		request.setOriginalRequest(anOriginalRequest);
		return request;

	}

	protected IProcedureResponse getBalancesToSynchronize(IProcedureRequest anOriginalRequest) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + " Ejecutando getBalancesToSynchronize: " + anOriginalRequest);

		ValidationAccountsRequest validations = new ValidationAccountsRequest();
		validations = transformToValidationAccountRequest(anOriginalRequest);
		IProcedureResponse response = getCoreServiceMonetaryTransaction().getBalancesToSynchronize(validations);

		if (logger.isDebugEnabled())
			logger.logDebug("RESPONSE getBalancesToSynchronize -->" + response.getProcedureResponseAsString());

		return response;
	}

	/**
	 * This method has to be override to implement reexecution logic
	 */
	// protected abstract IProcedureResponse executeOffline(IProcedureRequest
	// request, Map<String, Object> aBagSPJavaOrchestration) ;

	/**
	 * Contains primary steps for execution of Payment.
	 * 
	 * @param anOriginalRequest
	 * @param aBagSPJavaOrchestration
	 * @return
	 * @throws CTSInfrastructureException
	 * @throws CTSServiceException
	 */
	public IProcedureResponse executeStepsPaymentBase(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + " Ejectando executeStepsPaymentBase: " + anOriginalRequest.getProcedureRequestAsString());

		IProcedureResponse responseOffline = null;
		IProcedureResponse responseLocalExecution = null;
		IProcedureResponse responseNotification = null;
		IProcedureResponse responseValidateLocalExecution = null;
		IProcedureResponse responseExecuteTransaction = null;
		AccountingParameterRequest requestAccountingParameters = new AccountingParameterRequest();
		AccountingParameterResponse responseAccountingParameters = null;
		requestAccountingParameters.setOriginalRequest(anOriginalRequest);
		// StringBuilder messageErrorTransfer = new StringBuilder();
		// messageErrorTransfer.append((String)
		// aBagSPJavaOrchestration.get(PAYMENT_NAME));
		StringBuilder messageErrorPayment = new StringBuilder();
		messageErrorPayment.append((String) aBagSPJavaOrchestration.get(PAYMENT_NAME));

		requestAccountingParameters.setTransaction(Integer.parseInt(anOriginalRequest.readValueParam("@t_trn")));

		ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction = (ICoreServiceMonetaryTransaction) aBagSPJavaOrchestration.get("coreServiceMonetaryTransaction");

		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setChannelId(anOriginalRequest.readValueFieldInHeader("servicio"));

		ServerResponse responseServer = getCoreServer().getServerStatus(serverRequest);
		aBagSPJavaOrchestration.put(RESPONSE_SERVER, responseServer);

		if (logger.isInfoEnabled())
			logger.logInfo(new StringBuilder(CLASS_NAME).append("START").toString());
		
		//Valida el fuera de línea
		
		if (logger.isInfoEnabled())
			logger.logInfo("Llama a la funcion validateBvTransaction");
		
		String responseSupportOffline = validateBvTransaction(aBagSPJavaOrchestration); 
		
		if (logger.isInfoEnabled())
			logger.logInfo("responseSupportOffline ---> " + responseSupportOffline);
		
		if(responseSupportOffline == null || responseSupportOffline == "") {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException("Ha ocurrido un error intentando validar si el pago de servicios permite fuera de línea"));
			return Utils.returnException("Ha ocurrido un error intentando validar si el pago de servicios permite fuera de línea");
		}
	
		if(responseSupportOffline.equals("S")) {
			SUPPORT_OFFLINE = true;
		}else {
			SUPPORT_OFFLINE = false;
		}

		// if is not Online and if is reentryExecution , have to leave
		if (getFromReentryExcecution(aBagSPJavaOrchestration) && !responseServer.getOnLine() && !SUPPORT_OFFLINE) {
			IProcedureResponse resp = Utils.returnException(CODE_OFFLINE, "El pago de servicios no se puede ejecutar mientras el servidor se encuentre fuera de línea");
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, resp);
			return resp;
		}

		IProcedureResponse responseValidateCoreSigners = new ProcedureResponseAS();
		/*if (responseServer.getOnLine()) {
			responseValidateCoreSigners = AccountCoreSignersValidation.validateCoreSigners(getCoreService(), aBagSPJavaOrchestration);
			if (Utils.flowError(messageErrorPayment.append(" --> validateCoreSigners").toString(), responseValidateCoreSigners)) {
				if (logger.isInfoEnabled())
					logger.logInfo(CLASS_NAME + messageErrorPayment);
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseValidateCoreSigners);
				return responseValidateCoreSigners;
			}
		} else {
			responseValidateCoreSigners.setReturnCode(0);
			responseValidateCoreSigners.addParam("@o_condiciones_firmantes", ICTSTypes.SQLVARCHAR, 0, "");
		}*/
		
		responseValidateCoreSigners.setReturnCode(0);
		responseValidateCoreSigners.addParam("@o_condiciones_firmantes", ICTSTypes.SQLVARCHAR, 0, "");
		
		aBagSPJavaOrchestration.put(RESPONSE_CORE_SIGNERS, responseValidateCoreSigners);

        if(anOriginalRequest != null && !"S".equals(anOriginalRequest.readValueParam("@i_reversa"))) {
        	
    		if (logger.isDebugEnabled())
    			logger.logDebug(CLASS_NAME + " Ejectando executeStepsPaymentBase: Validaciones en el Local " + anOriginalRequest.getProcedureRequestAsString());

    		responseValidateLocalExecution = validateLocalExecution(anOriginalRequest, aBagSPJavaOrchestration);

    		if (Utils.flowError(messageErrorPayment.append(" --> validateLocalExecution").toString(), responseValidateLocalExecution)) {
    			if (logger.isInfoEnabled())
    				logger.logInfo(CLASS_NAME + messageErrorPayment);
    			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseValidateLocalExecution);
    			return responseValidateLocalExecution;
    		}
    		aBagSPJavaOrchestration.put(RESPONSE_VALIDATE_LOCAL, responseValidateLocalExecution);

    		if ("S".equals(responseValidateLocalExecution.readValueParam("@o_autorizacion"))) {
    			if (logger.isInfoEnabled())
    				logger.logInfo("Fin del flujo. Requiere autorizacion");
    			aBagSPJavaOrchestration.put("RESPONSE_TRANSACTION", responseValidateLocalExecution);//cmeg 03/12/2018 para que no se caiga
    			return Utils.returnException("TRANSACCIÃ“N NECESITA AUTORIZACIÃ“N");
    		}
    		aBagSPJavaOrchestration.put(RESPONSE_VALIDATE_LOCAL, responseValidateLocalExecution);
        	
        }

		/*
		 * Obtiene Transacciones y Causa del movimiento a aplicar
		 */
		requestAccountingParameters = new AccountingParameterRequest();
		requestAccountingParameters.setOriginalRequest(anOriginalRequest);
		requestAccountingParameters.setTransaction(Utils.getTransactionMenu(Integer.parseInt(anOriginalRequest.readValueParam("@t_trn"))));
		if (logger.isDebugEnabled())
			logger.logDebug("Ejecutando executeStepsPaymentBase: Obtiene Transacciones y Causas getAccountingParameter --->" + requestAccountingParameters);

		coreServiceMonetaryTransaction = getCoreServiceMonetaryTransaction();
		responseAccountingParameters = coreServiceMonetaryTransaction.getAccountingParameter(requestAccountingParameters);

		if (logger.isDebugEnabled())
			logger.logDebug("Ejecutando executeStepsPaymentBase: Respuesta Transacciones y Causas getAccountingParameter -->" + responseAccountingParameters.getAccountingParameters().toString());

		if (!responseAccountingParameters.getSuccess())
			return Utils.returnException(new StringBuilder(messageErrorPayment).append(responseAccountingParameters.getMessage()).toString());

		aBagSPJavaOrchestration.put(ACCOUNTING_PARAMETER, responseAccountingParameters);

		// Ejecucion en Core
		responseExecuteTransaction = executeTransaction(anOriginalRequest, aBagSPJavaOrchestration);

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + " Executing executeStepsPaymentBase: Respuesta executeTransaction: " + responseExecuteTransaction);

		if (Utils.flowError(messageErrorPayment.append(" --> responseExecuteTransaction").toString(), responseExecuteTransaction)) {
			if (responseServer.getOnLine()) {
				if (!getFromReentryExcecution(aBagSPJavaOrchestration)) {
					logger.logError(CLASS_NAME + " ************ ERROR Fin prematuro del flujo. No se ejecuto la transferencia." + responseExecuteTransaction);
					return responseExecuteTransaction;
				}
			} else {
				if (getFromReentryExcecution(aBagSPJavaOrchestration)) {
					logger.logError(CLASS_NAME + " ************ ERROR NO EJECUTA REENTRY POR ESTAR EN OFFLINE!!!" + responseExecuteTransaction);
					return responseExecuteTransaction;
				}
			}
		}

		if (logger.isInfoEnabled())
			if (!Utils.isNull(responseExecuteTransaction))
				logger.logInfo(new StringBuilder("RESPONSE_TRANSACTION --> ").append(responseExecuteTransaction.getProcedureResponseAsString()));

		if(anOriginalRequest != null && !"S".equals(anOriginalRequest.readValueParam("@i_reversa"))) {
		responseLocalExecution = updateLocalExecution(anOriginalRequest, aBagSPJavaOrchestration);

		aBagSPJavaOrchestration.put(RESPONSE_UPDATE_LOCAL, responseLocalExecution);
		if (Utils.flowError(messageErrorPayment.append(" --> updateLocalExecution").toString(), responseLocalExecution)) {
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + messageErrorPayment);
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseLocalExecution);
			return responseLocalExecution;
		}
		}
		responseNotification = sendNotification(anOriginalRequest, aBagSPJavaOrchestration);

		if (Utils.flowError(messageErrorPayment.append(" --> sendNotification").toString(), responseNotification)) {
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + messageErrorPayment);
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseNotification);
			return responseNotification;
		}

		if (!Utils.isNull(responseOffline))
			aBagSPJavaOrchestration.put(RESPONSE_OFFLINE, responseOffline);

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "  Executing executeStepsPaymentBase: " + ((IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION)).toString());
		return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
	}

	protected Boolean getFromReentryExcecution(Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureRequest request = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		return ("Y".equals(request.readValueFieldInHeader(REENTRY_EXE)));
	}

	/**
	 * Validate Local Execution .
	 */
	protected IProcedureResponse validateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {

		IProcedureRequest request = initProcedureRequest(anOriginalRequest);
		IProcedureResponse responseCoreSigners = (IProcedureResponse) bag.get(RESPONSE_CORE_SIGNERS);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, anOriginalRequest.readValueParam("@t_trn"));
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_LOCAL);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, anOriginalRequest.readValueParam("@t_trn"));

		request.setSpName("cob_bvirtual..sp_bv_validacion");

		request.addInputParam("@s_cliente", ICTSTypes.SYBINT4, anOriginalRequest.readValueParam("@s_cliente"));
		request.addInputParam("@s_perfil", ICTSTypes.SYBINT2, anOriginalRequest.readValueParam("@s_perfil"));
		request.addInputParam("@s_servicio", ICTSTypes.SYBINT1, anOriginalRequest.readValueParam("@s_servicio"));

		if (getFromReentryExcecution(bag)) {
			Utils.addInputParam(request, "@t_rty", ICTSTypes.SQLVARCHAR, "S");
		} else { // no es ejecucion de reentry
			Utils.addInputParam(request, "@t_rty", ICTSTypes.SQLVARCHAR, "N");
		}
		
		Utils.copyParam("@i_cta", anOriginalRequest, request);
		Utils.copyParam("@i_mon", anOriginalRequest, request);
		Utils.copyParam("@i_prod", anOriginalRequest, request);
		Utils.copyParam("@i_concepto", anOriginalRequest, request);
		Utils.copyParam("@i_doble_autorizacion", anOriginalRequest, request);
		Utils.copyParam("@i_val", anOriginalRequest, request);
		request.addInputParam("@i_valida_limites", ICTSTypes.SQLVARCHAR, "S");
		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_tercero_asociado")))
			Utils.copyParam("@i_tercero_asociado", anOriginalRequest, request);
		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_tercero")))
			Utils.copyParam("@i_tercero", anOriginalRequest, request);
		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_dividendo")))
			Utils.copyParam("@i_dividendo", anOriginalRequest, request);
		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_concepto")))
			Utils.copyParam("@i_concepto", anOriginalRequest, request);

		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_login")))
			request.addInputParam("@i_login", anOriginalRequest.readParam("@i_login").getDataType(), anOriginalRequest.readValueParam("@i_login"));
		else
			request.addInputParam("@i_login", anOriginalRequest.readParam("@s_user").getDataType(), anOriginalRequest.readValueParam("@s_user"));

		if (!getFromReentryExcecution(bag))
			request.addInputParam("@i_genera_clave", ICTSTypes.SYBVARCHAR, "S");

		request.addOutputParam("@o_cliente_mis", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_prod", ICTSTypes.SQLINT1, "0");
		request.addOutputParam("@o_cta", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		request.addOutputParam("@o_mon", ICTSTypes.SQLINT2, "0");
		request.addOutputParam("@o_prod_des", ICTSTypes.SQLINT1, "0");
		request.addOutputParam("@o_cta_des", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		request.addOutputParam("@o_mon_des", ICTSTypes.SQLINT2, "0");
		request.addOutputParam("@o_retorno", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_condicion", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_fecha_ini", ICTSTypes.SQLDATETIME, "01/01/1900");
		request.addOutputParam("@o_fecha_fin", ICTSTypes.SQLDATETIME, "01/01/1900");
		request.addOutputParam("@o_ult_fecha", ICTSTypes.SQLDATETIME, "01/01/1900");
		request.addOutputParam("@o_srv_host", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXX");
		request.addOutputParam("@o_autorizacion", ICTSTypes.SQLCHAR, "X");
		request.addOutputParam("@o_ssn_branch", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_cta_cobro", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXX");
		request.addOutputParam("@o_prod_cobro", ICTSTypes.SQLINT1, "0");
		request.addOutputParam("@o_cod_mis", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_clave_bv", ICTSTypes.SYBINT4, "0");

		if (!Utils.isNull(responseCoreSigners.readParam("@o_condiciones_firmantes")))
			if (!Utils.isNull(responseCoreSigners.readValueParam("@o_condiciones_firmantes")))
				request.addInputParam("@i_cond_firmas", responseCoreSigners.readParam("@o_condiciones_firmantes").getDataType(), responseCoreSigners.readValueParam("@o_condiciones_firmantes"));

		if (logger.isDebugEnabled()) {
			logger.logDebug("Validate local, request: " + request.getProcedureRequestAsString());
		}
		/* Ejecuta y obtiene la respuesta */
		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Validate local, response: " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Finalize Validate local");
		}
		return pResponse;
	}

	/**
	 * Save transaction log .
	 */
	protected IProcedureResponse updateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando updateLocalExecution" + anOriginalRequest.getProcedureRequestAsString());

		IProcedureRequest request = initProcedureRequest(anOriginalRequest);

		IProcedureResponse responseBalance = (IProcedureResponse) bag.get(RESPONSE_BALANCE);
		IProcedureResponse responseTransaction = (IProcedureResponse) bag.get(RESPONSE_TRANSACTION);
		IProcedureResponse responseLocalValidation = (IProcedureResponse) bag.get(RESPONSE_VALIDATE_LOCAL);

		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, anOriginalRequest.readValueParam("@t_trn"));
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_LOCAL);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, anOriginalRequest.readValueParam("@t_trn"));

		request.setSpName("cob_bvirtual..sp_bv_transaccion");

		if (anOriginalRequest.readValueParam("@i_graba_notif") != null) {
			Utils.copyParam("@i_graba_notif", anOriginalRequest, request);
		} else
			request.addInputParam("@i_graba_notif", ICTSTypes.SQLVARCHAR, "S");

		if (anOriginalRequest.readValueParam("@i_graba_tranmonet") != null) {
			Utils.copyParam("@i_graba_tranmonet", anOriginalRequest, request);
		} else {
			request.addInputParam("@i_graba_tranmonet", ICTSTypes.SQLVARCHAR, "S");
		}

		if (anOriginalRequest.readValueParam("@i_recurr_id") != null) {
			Utils.copyParam("@i_recurr_id", anOriginalRequest, request);
		}
		
		if (anOriginalRequest.readValueParam("@s_servicio") != null) {
			request.addInputParam("@s_servicio", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_servicio"));			
		}

		if (anOriginalRequest.readValueParam("@i_mon_des") != null) {
			request.addInputParam("@i_mon_2", ICTSTypes.SQLINT1, anOriginalRequest.readValueParam("@i_mon_des")); // moneda
																													// credito
		}

		request.addInputParam("@i_graba_log", ICTSTypes.SQLVARCHAR, "S");
		request.addInputParam("@i_sinc_cta_des", ICTSTypes.SQLVARCHAR, "N");

		if (anOriginalRequest.readValueParam("@i_convenio") != null) {
			Utils.copyParam("@i_convenio", anOriginalRequest, request);
			request.addInputParam("@i_tipo_notif", ICTSTypes.SQLVARCHAR, "S"); // servicios
			request.addInputParam("@i_nombre_benef", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_ref_5")); // nombreCliente
			request.addInputParam("@i_num_doc", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_ref_8"));// NIT-Fac
			request.addInputParam("@i_nombre_cr", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_ref_1")); // descripConvenio
			request.addInputParam("@i_telefono_benef", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_ref_9")); // cod-convenio-cedula,telefono
		} else
			request.addInputParam("@i_tipo_notif", ICTSTypes.SQLVARCHAR, "P"); // prestamos

		Utils.copyParam("@i_cta", anOriginalRequest, request);
		Utils.copyParam("@i_tipo_interfaz", anOriginalRequest, request);
		Utils.copyParam("@i_prod", anOriginalRequest, request);
		Utils.copyParam("@i_mon", anOriginalRequest, request); // moneda debito
		Utils.copyParam("@i_concepto", anOriginalRequest, request); // nombre_convenio
		Utils.copyParam("@i_val", anOriginalRequest, request);

		// Envio secuencial para bv_log generado en sp_bv_validacion
		if (!getFromReentryExcecution(bag)) {
			if (!(Utils.isNullOrEmpty(responseLocalValidation.readValueParam("@o_clave_bv"))))
				if (Integer.parseInt(responseLocalValidation.readValueParam("@o_clave_bv")) > 0) {
					request.addInputParam("@i_genera_clave", ICTSTypes.SQLVARCHAR, "N");
					request.addInputParam("@i_clave_bv", ICTSTypes.SQLINT4, responseLocalValidation.readValueParam("@o_clave_bv"));
				}
		} else {
			request.addInputParam("@i_genera_clave", ICTSTypes.SQLVARCHAR, "N");
			request.addInputParam("@i_clave_bv", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_clave_bv"));
		}

		addParametersRequestUpdateLocal(request, anOriginalRequest);

		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_login")))
			request.addInputParam("@i_login", anOriginalRequest.readParam("@i_login").getDataType(), anOriginalRequest.readValueParam("@i_login"));
		else
			request.addInputParam("@i_login", anOriginalRequest.readParam("@s_user").getDataType(), anOriginalRequest.readValueParam("@s_user"));

		// obtener returnCode de ejecucion de Core
		if (responseTransaction.getReturnCode() != 0) { // error en ejec. core
			Utils.addInputParam(request, "@s_error", ICTSTypes.SQLVARCHAR, (String.valueOf(responseTransaction.getReturnCode())));
			if (responseTransaction.getMessageListSize() > 0)
				Utils.addInputParam(request, "@s_msg", ICTSTypes.SQLVARCHAR, (responseTransaction.getMessage(1).getMessageText()));

		}

		// envio de t_rty
		if (logger.isDebugEnabled()) 
			logger.logDebug("Update local param reentryExecution" + request.readValueFieldInHeader("reentryExecution"));
		request.removeParam("@t_rty");
		if (getFromReentryExcecution(bag)) {
			Utils.addInputParam(request, "@t_rty", ICTSTypes.SQLVARCHAR, "S");
		} else { // no es ejecucion de reentry
			Utils.addInputParam(request, "@t_rty", ICTSTypes.SQLVARCHAR, "N");
		}

		// copia variables r_ como parÃ¡metros de entrada para sincronizar saldos
		if (!Utils.isNull(responseBalance)) {
			if (responseBalance.getResultSetListSize() > 0) {
				IResultSetHeaderColumn[] columns = responseBalance.getResultSet(1).getMetaData().getColumnsMetaDataAsArray();
				IResultSetRow[] rows = responseBalance.getResultSet(1).getData().getRowsAsArray();
				IResultSetRowColumnData[] cols = rows[0].getColumnsAsArray();

				int i = 0;
				for (IResultSetHeaderColumn iResultSetHeaderColumn : columns) {
					if (!iResultSetHeaderColumn.getName().equals(""))
						if (cols[i].getValue() != null) {
							if (logger.isDebugEnabled())
								logger.logDebug("PARAMETROS AÃ‘ADIDOS :" + iResultSetHeaderColumn.getName().replaceFirst("r_", "@i_") + " VALOR: " + cols[i].getValue());
							Utils.addInputParam(request, iResultSetHeaderColumn.getName().replaceFirst("r_", "@i_"), iResultSetHeaderColumn.getType(), cols[i].getValue());
						}
					i++;
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("Update local, request: " + request.getProcedureRequestAsString());
		}

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Ejecutando updateLocalExecution Insert en el local bv_pagos" + request.getProcedureRequestAsString());

		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Ejecutando updateLocalExecution Respuesta Insert en el local bv_pagos" + pResponse.toString());

		return pResponse;
	}

	private OfficerByAccountResponse findOfficers(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		OfficerByAccountRequest request = new OfficerByAccountRequest();
		Product product = new Product();

		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_cta")))
			product.setProductNumber(anOriginalRequest.readValueParam("@i_cta"));
		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_prod")))
			product.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod").toString()));
		request.setProduct(product);

		return getCoreService().getOfficerByAccount(request);

	}

	protected IProcedureResponse sendNotification(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {

		StringBuilder messageErrorPayment = new StringBuilder();
		messageErrorPayment.append((String) aBagSPJavaOrchestration.get(PAYMENT_NAME));
		ServerResponse responseServer = (ServerResponse) aBagSPJavaOrchestration.get(RESPONSE_SERVER);
		IProcedureResponse responseTransaction = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);

		OfficerByAccountResponse findOfficersExecutionResponse = findOfficers(anOriginalRequest.clone(), aBagSPJavaOrchestration);
		if (!findOfficersExecutionResponse.getSuccess())
			Utils.returnException("Error al obtener oficiales");
		NotificationRequest notificationRequest = transformNotificationRequest(anOriginalRequest, findOfficersExecutionResponse);
		Client client = new Client();
		client.setIdCustomer(anOriginalRequest.readValueParam("@s_cliente"));
		if (!Utils.isNull(anOriginalRequest.readValueParam("@i_login"))) {
			client.setLogin(anOriginalRequest.readValueParam("@i_login"));
		}
		notificationRequest.setClient(client);
		notificationRequest.setChannelId(anOriginalRequest.readValueFieldInHeader("servicio"));

		if (!responseServer.getOnLine())
			notificationRequest.getNotification().setNotificationType("O"); // offline
		else {
			if (responseTransaction.getReturnCode() != 0) {
				if (getFromReentryExcecution(aBagSPJavaOrchestration)) {
					notificationRequest.getNotification().setNotificationType("E"); // en
																					// linea,
																					// con
																					// error
																					// y
																					// por
																					// reentry
					if (responseTransaction.getMessageListSize() > 0)
						notificationRequest.getNotificationDetail().setAuxiliary10(generaMensaje(responseTransaction.getMessage(1).getMessageText()));
				}
			} else
				notificationRequest.getNotification().setNotificationType("F"); // en
																				// linea
																				// y
																				// ok
		}

		NotificationResponse notificationResponse = getCoreServiceNotification().sendNotification(notificationRequest);

		if (!notificationResponse.getSuccess()) {
			if (logger.isDebugEnabled()) {
				logger.logDebug(" ERROR enviando notificaciÃ³n: " + notificationResponse.getMessage().getCode() + " - " + notificationResponse.getMessage().getDescription());
			}
		}

		IProcedureResponse response = new ProcedureResponseAS();
		response.setReturnCode(0);

		return response;

	}

	public String generaMensaje(String vars) {
		vars = vars.substring(vars.indexOf("]") + 1, vars.length());
		return vars;
	}
	
	/**
	 * validateBvTransaction: local account, virtual signers checking
	 * 
	 * @param aBagSPJavaOrchestration
	 * @return String
	 */
	protected String validateBvTransaction(Map<String, Object> aBagSPJavaOrchestration) {
		
		if (logger.isInfoEnabled()) {
			logger.logInfo("Initialize method validateBvTransaction");
		}
		
		String responseSupportOffline = "N";
		
		//valida la parametria de la tabla bv_transaccion
		IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		IProcedureRequest request = initProcedureRequest(originalRequest);

		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800090");
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setSpName("cob_bvirtual..sp_bv_transaction_context");

		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "IB");
		request.addInputParam("@i_transaccion", ICTSTypes.SQLINTN, originalRequest.readValueParam("@t_trn"));
		request.addInputParam("@s_servicio", ICTSTypes.SYBINT1, originalRequest.readValueParam("@s_servicio"));
		
		request.addOutputParam("@o_autenticacion", ICTSTypes.SYBCHAR, "N");
		request.addOutputParam("@o_fuera_de_linea", ICTSTypes.SYBCHAR, "N");
		request.addOutputParam("@o_doble_autorizacion", ICTSTypes.SYBCHAR, "N");
		request.addOutputParam("@o_sincroniza_saldos", ICTSTypes.SYBCHAR, "N");
		request.addOutputParam("@o_mostrar_costo", ICTSTypes.SYBCHAR, "N");
		request.addOutputParam("@o_tipo_costo", ICTSTypes.SYBCHAR, "N");
		
		if (logger.isInfoEnabled()) {
			logger.logInfo("Finalize method validateBvTransaction");
		}
		
		// Ejecuta validacion a la tabla bv_transaccion
		IProcedureResponse tResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Validacion local, response: " + tResponse.getProcedureResponseAsString());
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Finaliza validacion local");
		
		responseSupportOffline = tResponse.readValueParam("@o_fuera_de_linea");
		
		aBagSPJavaOrchestration.put(RESPONSE_BV_TRANSACTION, tResponse);

		// Valida si ocurrio un error en la ejecucion
		if (Utils.flowError("validateBvTransaction", tResponse)) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, tResponse);
		}
		
		return responseSupportOffline;
	}
	
	protected void saveTranPagoServ(IProcedureRequest anOriginalRequest, IProcedureResponse anOriginalResponse,Map<String, Object> aBagSPJavaOrchestration) {
		
		boolean response = false;
		
		if (logger.isInfoEnabled()) {
			logger.logInfo("Initialize method saveTranPagoServ");
			logger.logDebug(CLASS_NAME + " anOriginalRequest.getProcedureRequestAsString()" + anOriginalRequest.getProcedureRequestAsString());
			logger.logDebug(CLASS_NAME + " anOriginalRequest.getParams()" + anOriginalRequest.getParams());
			logger.logDebug(CLASS_NAME + " anOriginalResponse.getProcedureResponseAsString()" + anOriginalResponse.getProcedureResponseAsString());
			logger.logDebug(CLASS_NAME + " anOriginalResponse.getParams()" + anOriginalResponse.getParams());
			logger.logDebug(CLASS_NAME + " anOriginalResponse.getProcedureResponseAsString()" + aBagSPJavaOrchestration.toString());
		}
		
		try {
			IProcedureRequest request = initProcedureRequest(anOriginalRequest.clone());
			
			request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_LOCAL);
			request.addFieldInHeader(KEEP_SSN,ICOBISTS.HEADER_STRING_TYPE, "Y");
			
			request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18017");
			request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
			
			request.setSpName("cob_bvirtual..sp_bv_administra_pago_gestopago");
			
			request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "I");
			request.addInputParam("@i_estado", ICTSTypes.SQLCHAR, aBagSPJavaOrchestration.get(ESTADO).toString());
			request.addInputParam("@i_id_pago_serv", ICTSTypes.SQLINTN, anOriginalRequest.readValueParam("@s_ssn_branch"));
			request.addInputParam("@i_cod_resp", ICTSTypes.SQLVARCHAR, anOriginalResponse.readValueParam("@o_cod_respuesta"));
			request.addInputParam("@i_msj_resp", ICTSTypes.SQLVARCHAR, anOriginalResponse.readValueParam("@o_msj_respuesta"));
			request.addInputParam("@i_ente_mis", ICTSTypes.SQLINTN, anOriginalRequest.readValueParam("@i_ente"));
			request.addInputParam("@i_ente_bv", ICTSTypes.SQLINTN, anOriginalRequest.readValueParam("@i_ref_7"));
			request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_login"));
			request.addInputParam("@i_com_serv", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_comi_val"));
			request.addInputParam("@i_phone_udid", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_ref_6"));
			request.addInputParam("@i_id_cat_serv", ICTSTypes.SQLINTN, anOriginalRequest.readValueParam("@i_ref_8"));
			request.addInputParam("@i_tipo_front", ICTSTypes.SQLINTN, anOriginalRequest.readValueParam("@i_ref_9"));
			if("a".equals(anOriginalRequest.readValueParam("@i_ref_10"))) 
				request.addInputParam("@i_field_req", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_ref_1"));
			else 
				request.addInputParam("@i_field_req", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_ref_5"));
			request.addInputParam("@i_precio", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_val"));
			request.addInputParam("@i_num_aut", ICTSTypes.SQLVARCHAR, anOriginalResponse.readValueParam("@o_num_autorizacion"));
			request.addInputParam("@i_saldo", ICTSTypes.SQLMONEY, anOriginalResponse.readValueParam("@o_saldo"));
			request.addInputParam("@i_saldo_cliente", ICTSTypes.SQLMONEY, anOriginalResponse.readValueParam("@o_saldo_final"));
			request.addInputParam("@i_com_prov", ICTSTypes.SQLMONEY, anOriginalResponse.readValueParam("@o_comision"));
			request.addInputParam("@i_xml_req", ICTSTypes.SQLVARCHAR, anOriginalResponse.readValueParam("@o_xml_req"));
			request.addInputParam("@i_xml_resp", ICTSTypes.SQLVARCHAR, anOriginalResponse.readValueParam("@o_xml_resp"));
			request.addInputParam("@i_fecha", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_date"));
			request.addInputParam("@i_ssn", ICTSTypes.SQLINTN, anOriginalRequest.readValueParam("@s_ssn"));
			request.addInputParam("@i_img_serv", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_ref_11"));
			request.addInputParam("@i_img_ayuda", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_ref_12"));			
			request.addInputParam("@i_est_prov", ICTSTypes.SQLBIT, "1");
			request.addInputParam("@i_pin", ICTSTypes.SQLVARCHAR, anOriginalResponse.readValueParam("@o_pin"));
			request.addInputParam("@i_instruc", ICTSTypes.SQLVARCHAR, anOriginalResponse.readValueParam("@o_instrucciones"));
			request.addInputParam("@i_id_trn_prov", ICTSTypes.SQLINTN, anOriginalResponse.readValueParam("@o_transaccion_ID"));
			request.addInputParam("@i_cuenta", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cta"));
			request.addInputParam("@i_canal", ICTSTypes.SQLINTN, anOriginalRequest.readValueParam("@i_canal"));
			request.addInputParam("@i_servicio_id", ICTSTypes.SQLINTN, anOriginalRequest.readValueParam("@i_ref_2"));
			request.addInputParam("@i_servicio_desc", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_ref_13"));
			request.addInputParam("@i_producto_id", ICTSTypes.SQLINTN, anOriginalRequest.readValueParam("@i_ref_3"));
			request.addInputParam("@i_producto_desc", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_ref_14"));
					
			
			IProcedureResponse tResponse = executeCoreBanking(request);

			if (logger.isDebugEnabled())
				logger.logDebug(CLASS_NAME + "saveTranPagoServ, response: " + tResponse.getProcedureResponseAsString());			
				
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.logInfo("Error de saveTranPagoServ");
		}
		finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo("Finalize method saveTranPagoServ");
			}
		}
	}

}