/**
 *
 */
package com.cobiscorp.ecobis.ib.orchestration.base.utils.transfers;

import java.math.BigDecimal;
import java.util.Map;

import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ACHTransferRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AccountingParameterResponse;
import com.cobiscorp.ecobis.ib.application.dtos.InternationalTransferRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SelfAccountTransferRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ThirdPartyTransferRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ValidationAccountsResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountingParameter;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;

/**
 * @author schancay
 * @since Aug 27, 2014
 * @version 1.0.0
 */
public class UtilsTransfers {

	private static final String CORESERVICEMONETARYTRANSACTION = "coreServiceMonetaryTransaction";
	private static ILogger logger = LogFactory.getLogger(UtilsTransfers.class);
	private static final String CLASS_NAME = " >-----> ";
	protected static final String COBIS_CONTEXT = "COBIS";
	protected static final String ACCOUNTING_PARAMETER = "ACCOUNTING_PARAMETER";
	protected static final String SERVICETRN_TYPE_D = "D";
	protected static final String SERVICETRN_COST_T = "T";
	protected static final String SERVICETRN_COST_C = "C";

	/**
	 * Get SelfAccountTransferRequest with information from context only with parameters to transfers online.
	 *
	 * @param aBagSPJavaOrchestration
	 * @param ORIGINAL_REQUEST
	 * @param RESPONSE_LOCAL_VALIDATION
	 * @return SelfAccountTransferRequest
	 */
	public static SelfAccountTransferRequest transformSelfAccountTransferRequest(Map<String, Object> aBagSPJavaOrchestration, String ORIGINAL_REQUEST, String RESPONSE_LOCAL_VALIDATION) {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Creando Request de Transferencia");

		SelfAccountTransferRequest selfAccountTransferRequest = new SelfAccountTransferRequest();

		if (logger.isInfoEnabled())
		logger.logInfo("***** ENTRA A TRANSFERNCIAS PROPIAS!!!");
		IProcedureResponse localValidation = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_LOCAL_VALIDATION);
		IProcedureRequest anOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		AccountingParameterResponse responseAccountingParameters = null;

		responseAccountingParameters = (AccountingParameterResponse) aBagSPJavaOrchestration.get(ACCOUNTING_PARAMETER);
		ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction = (ICoreServiceMonetaryTransaction) aBagSPJavaOrchestration.get(CORESERVICEMONETARYTRANSACTION);

		Map<String, AccountingParameter> map = coreServiceMonetaryTransaction.existsAccountingParameter(responseAccountingParameters, Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")),
				"T", "C");

		if (!Utils.isNull(map)) {
			selfAccountTransferRequest.setCauseDes(map.get("ACCOUNTING_PARAM").getCause());
		}

		map = null;
		map = coreServiceMonetaryTransaction.existsAccountingParameter(responseAccountingParameters, Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")), "T", "D");
		if (!Utils.isNull(map)) {
			selfAccountTransferRequest.setCause(map.get("ACCOUNTING_PARAM").getCause());
		}
		map = null;
		map = coreServiceMonetaryTransaction.existsAccountingParameter(responseAccountingParameters, Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")), "C", null);

		if (!Utils.isNull(map)) {
			selfAccountTransferRequest.setCauseComi(map.get("ACCOUNTING_PARAM").getCause());
			selfAccountTransferRequest.setServiceCost(map.get("ACCOUNTING_PARAM").getService());
		}

		Currency currencyProduct = new Currency();
		currencyProduct.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon").toString()));

		Currency currencyDestProduct = new Currency();
		currencyDestProduct.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon_des").toString()));

		Product originProduct = new Product();
		originProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta").toString());
		originProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod").toString()));
		originProduct.setCurrency(currencyProduct);

		Product destinationProduct = new Product();
		destinationProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta_des"));
		destinationProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod_des").toString()));
		destinationProduct.setCurrency(currencyDestProduct);

		if (localValidation.readParam("@o_comision") != null)
			selfAccountTransferRequest.setCommisionAmmount(new BigDecimal(localValidation.readValueParam("@o_comision").toString()));
		selfAccountTransferRequest.setAmmount(new BigDecimal(anOriginalRequest.readValueParam("@i_val").toString()));
		selfAccountTransferRequest.setOriginProduct(originProduct);
		selfAccountTransferRequest.setDestinationProduct(destinationProduct);
		selfAccountTransferRequest.setOriginatorFunds(anOriginalRequest.readValueParam("@i_origen_fondos"));
		selfAccountTransferRequest.setReceiverFunds(anOriginalRequest.readValueParam("@i_dest_fondos"));
		selfAccountTransferRequest.setDescriptionTransfer(anOriginalRequest.readValueParam("@i_concepto"));

		selfAccountTransferRequest.setChannelId(anOriginalRequest.readValueParam("@s_servicio"));

		if (anOriginalRequest.readValueParam("@s_ssn_branch") != null)
			selfAccountTransferRequest.setReferenceNumberBranch(anOriginalRequest.readValueParam("@s_ssn_branch"));

		if (anOriginalRequest.readValueParam("@s_ssn") != null)
			selfAccountTransferRequest.setReferenceNumber(anOriginalRequest.readValueParam("@s_ssn"));

		selfAccountTransferRequest = (SelfAccountTransferRequest) Utils.setSessionParameters(selfAccountTransferRequest, anOriginalRequest);
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request de Transferencia:" + selfAccountTransferRequest + anOriginalRequest);
		return selfAccountTransferRequest;
	}

	/**
	 * Configure header request for execute stored procedure [cob_bvirtual..sp_bv_transaccion], obtained a IProcedureRequest empty and the response of
	 * validate central execution.
	 *
	 * @param newRequest - IProcedureRequest empty or new
	 * @param validationAccountsResponse - Response obtained from context
	 * @return IProcedureRequest
	 */
	public static IProcedureRequest getRequestLocalExecution(IProcedureRequest request, ValidationAccountsResponse validationAccountsResponse, IProcedureRequest originalRequest) {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Iniciando armado de la esctructura de la peticion");
		IProcedureRequest newRequest = request;
		Utils.removeOutputparams(newRequest);
		Utils.removeParam(newRequest, "@i_producto");
		Utils.removeParam(newRequest, "@i_ente");
		Utils.removeParam(newRequest, "@i_doble_autorizacion");
		Utils.removeParam(newRequest, "@i_factor");

		newRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800049");
		newRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_LOCAL);
		newRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		newRequest.setSpName("cob_bvirtual..sp_bv_transaccion");

		String t_trn = "1800049";
		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + "t_trn a evaluar: " + t_trn);
			logger.logDebug(CLASS_NAME + "Los valores de la bv_transaccion :" + validationAccountsResponse);
		}
		newRequest.addInputParam("@t_trn", ICTSTypes.SQLINTN, "1800049");
		newRequest.addInputParam("@s_error", ICTSTypes.SQLINTN, validationAccountsResponse.getMessage().getCode());
		newRequest.addInputParam("@s_msg", ICTSTypes.SQLVARCHAR, validationAccountsResponse.getMessage().getDescription());
		newRequest.addInputParam("@i_graba_log", ICTSTypes.SQLVARCHAR, "S");
		// newRequest.addInputParam("@s_servicio", ICTSTypes.SQLVARCHAR,
		// originalRequest.readValueParam("@s_servicio"));
		// newRequest.addInputParam("@i_val", ICTSTypes.SQLMONEY,
		// originalRequest.readValueParam("@i_val"));

		// LIMITES
		newRequest.addInputParam("@i_valida_limites", ICTSTypes.SQLCHAR, "S");
		newRequest.addInputParam("@i_factor", ICTSTypes.SQLINT4, "1");
		newRequest.addInputParam("@t_rty", ICTSTypes.SQLVARCHAR, "N");
		newRequest.addInputParam("@t_corr", ICTSTypes.SQLVARCHAR, "N");
		newRequest.addInputParam("@i_prod", ICTSTypes.SQLINT2, originalRequest.readValueParam("@i_prod").toString());
		newRequest.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, originalRequest.readValueParam("@i_cta").toString());
		newRequest.addInputParam("@i_mon", ICTSTypes.SQLINT2, originalRequest.readValueParam("@i_mon").toString());
		newRequest.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR, originalRequest.readValueParam("@i_cta_des").toString());
		newRequest.addInputParam("@i_mon_des", ICTSTypes.SQLINT2, originalRequest.readValueParam("@i_mon_des").toString());
		newRequest.addInputParam("@i_prod_des", ICTSTypes.SQLINT2, originalRequest.readValueParam("@i_prod_des").toString());

		// PARAMETROS PARA BANCA MOVIL
		if ("6".equals(newRequest.readValueParam("@s_servicio")) || "7".equals(newRequest.readValueParam("@s_servicio")))
			newRequest.addInputParam("@i_genera_clave", ICTSTypes.SQLVARCHAR, "S");

		/* Agrega parametros de salida (output) */
		newRequest.addOutputParam("@o_referencia", ICTSTypes.SQLINT4, "0");
		newRequest.addOutputParam("@o_saldo_para_girar", ICTSTypes.SQLMONEY, "0");
		newRequest.addOutputParam("@o_tipo_mensaje", ICTSTypes.SQLCHAR, "F");
		newRequest.addOutputParam("@o_numero_producto", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		newRequest.addOutputParam("@o_nombre_cr", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		newRequest.addOutputParam("@o_error_ejec", ICTSTypes.SQLCHAR, "X");

		if (logger.isDebugEnabled())
			logger.logDebug("Request Armado:" + newRequest.getProcedureRequestAsString());

		return newRequest;
	}

	/**
	 * Get ThirdPartyTransferRequest with information from context only with parameters to transfers online.
	 *
	 * @param aBagSPJavaOrchestration
	 * @param ORIGINAL_REQUEST
	 * @param RESPONSE_LOCAL_VALIDATION
	 * @return ThirdPartyTransferRequest
	 */
	public static ThirdPartyTransferRequest transformThirdAccountTransferRequest(Map<String, Object> aBagSPJavaOrchestration, String ORIGINAL_REQUEST, String RESPONSE_LOCAL_VALIDATION) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Creando Request de Transferencia TERCEROS");

		ThirdPartyTransferRequest ThirdPartyTransferRequest = new ThirdPartyTransferRequest();
		IProcedureResponse localValidation = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_LOCAL_VALIDATION);
		IProcedureRequest anOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

		AccountingParameterResponse responseAccountingParameters = null;

		responseAccountingParameters = (AccountingParameterResponse) aBagSPJavaOrchestration.get(ACCOUNTING_PARAMETER);
		
		if (logger.isInfoEnabled())
		logger.logInfo("************VALOR" + "" + aBagSPJavaOrchestration.get(ACCOUNTING_PARAMETER));

		ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction = (ICoreServiceMonetaryTransaction) aBagSPJavaOrchestration.get(CORESERVICEMONETARYTRANSACTION);

		Map<String, AccountingParameter> map = coreServiceMonetaryTransaction.existsAccountingParameter(responseAccountingParameters, Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")),
				"T", "C");
				if (logger.isInfoEnabled())
		logger.logInfo("*************MAPA" + "" + map.get("ACCOUNTING_PARAM"));

		AccountingParameter accountingParameter = map.get("ACCOUNTING_PARAM");

		if (!Utils.isNull(map)) {
			ThirdPartyTransferRequest.setCauseDes(accountingParameter.getCause());
		}
		map = null;
		map = coreServiceMonetaryTransaction.existsAccountingParameter(responseAccountingParameters, Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")), "T", "D");
		if (!Utils.isNull(map)) {
			ThirdPartyTransferRequest.setCause(map.get("ACCOUNTING_PARAM").getCause());
		}
		map = null;
		map = coreServiceMonetaryTransaction.existsAccountingParameter(responseAccountingParameters, Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")), "C", "D");
		if (!Utils.isNull(map)) {
			ThirdPartyTransferRequest.setCauseComi(map.get("ACCOUNTING_PARAM").getCause());
			ThirdPartyTransferRequest.setServiceCost(map.get("ACCOUNTING_PARAM").getService());
		}

		Currency currencyProduct = new Currency();
		currencyProduct.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon").toString()));

		Product originProduct = new Product();
		originProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta").toString());
		originProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod").toString()));
		originProduct.setCurrency(currencyProduct);

		Currency currencyDest = new Currency();
		currencyDest.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon_des").toString()));

		Product destinationProduct = new Product();
		destinationProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta_des"));
		destinationProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod_des").toString()));
		destinationProduct.setCurrency(currencyDest);

		if (localValidation.readParam("@o_comision") != null)
			ThirdPartyTransferRequest.setCommisionAmmount(new BigDecimal(localValidation.readValueParam("@o_comision").toString()));
		ThirdPartyTransferRequest.setAmmount(new BigDecimal(anOriginalRequest.readValueParam("@i_val").toString()));
		ThirdPartyTransferRequest.setOriginProduct(originProduct);
		ThirdPartyTransferRequest.setDestinationProduct(destinationProduct);
		ThirdPartyTransferRequest.setDescriptionTransfer(anOriginalRequest.readValueParam("@i_concepto"));
		Client cliente = new Client();
		cliente.setId(anOriginalRequest.readValueParam("@i_ente"));
		ThirdPartyTransferRequest.setUser(cliente);
		ThirdPartyTransferRequest.setOriginatorFunds(anOriginalRequest.readValueParam("@i_origen_fondos"));
		ThirdPartyTransferRequest.setReceiverFunds(anOriginalRequest.readValueParam("@i_dest_fondos"));

		ThirdPartyTransferRequest.setChannelId(anOriginalRequest.readValueParam("@s_servicio"));

		if (anOriginalRequest.readValueParam("@s_ssn_branch") != null)
			ThirdPartyTransferRequest.setReferenceNumberBranch(anOriginalRequest.readValueParam("@s_ssn_branch"));

		if (anOriginalRequest.readValueParam("@s_ssn") != null)
			ThirdPartyTransferRequest.setReferenceNumber(anOriginalRequest.readValueParam("@s_ssn"));

		ThirdPartyTransferRequest = (ThirdPartyTransferRequest) Utils.setSessionParameters(ThirdPartyTransferRequest, anOriginalRequest);
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request de Transferencia:" + ThirdPartyTransferRequest + anOriginalRequest);
		return ThirdPartyTransferRequest;
	}

	/**
	 * Get International PartyTransferRequest with information from context only with parameters to transfers online.
	 *
	 * @param aBagSPJavaOrchestration
	 * @param ORIGINAL_REQUEST
	 * @param RESPONSE_LOCAL_VALIDATION
	 * @return InternationalPartyTransferRequest
	 * 
	 */
	public static InternationalTransferRequest transformInternationalAccountTransferRequest(Map<String, Object> aBagSPJavaOrchestration, String ORIGINAL_REQUEST, String RESPONSE_LOCAL_VALIDATION) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Creando Request de Transferencia Internacional");

		InternationalTransferRequest aInternationalTransferRequest = new InternationalTransferRequest();
		IProcedureRequest anOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		AccountingParameterResponse responseAccountingParameters = null;

		responseAccountingParameters = (AccountingParameterResponse) aBagSPJavaOrchestration.get(ACCOUNTING_PARAMETER);
		if (logger.isInfoEnabled())
		logger.logInfo("************VALOR" + "" + aBagSPJavaOrchestration.get(ACCOUNTING_PARAMETER));

		ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction = (ICoreServiceMonetaryTransaction) aBagSPJavaOrchestration.get(CORESERVICEMONETARYTRANSACTION);

		Map<String, AccountingParameter> map = coreServiceMonetaryTransaction.existsAccountingParameter(responseAccountingParameters, Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")),
				"T", "C");

		if (!Utils.isNull(map)) {
			aInternationalTransferRequest.setCause(map.get("ACCOUNTING_PARAM").getCause());
		}
		map = null;
		map = coreServiceMonetaryTransaction.existsAccountingParameter(responseAccountingParameters, Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")), "C", null);

		if (!Utils.isNull(map)) {
			aInternationalTransferRequest.setCauseComi(map.get("ACCOUNTING_PARAM").getCause());
			aInternationalTransferRequest.setServiceCost(map.get("ACCOUNTING_PARAM").getService());
		}

		if (logger.isInfoEnabled())
		{
		logger.logInfo("************* CAUSA ORIGEN: " + "" + aInternationalTransferRequest.getCause());
		logger.logInfo("************* CAUSA COMISION:" + "" + aInternationalTransferRequest.getCauseComi());
		}

		Currency currencyProduct = new Currency();
		currencyProduct.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon").toString()));

		Currency destinationCurrency = new Currency();
		destinationCurrency.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_moncta").toString()));

		Product originProduct = new Product();
		originProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta").toString());
		originProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod").toString()));
		originProduct.setCurrency(currencyProduct);

		Product destinationProduct = new Product();
		destinationProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta_des"));
		destinationProduct.setCurrency(destinationCurrency);

		// Beneficiary Info

		aInternationalTransferRequest.setBeneficiaryIDNumber(anOriginalRequest.readValueParam("@i_dniruc"));
		aInternationalTransferRequest.setBeneficiaryName(anOriginalRequest.readValueParam("@i_benefi"));
		aInternationalTransferRequest.setBeneficiaryFirstLastName(anOriginalRequest.readValueParam("@i_papellido"));
		aInternationalTransferRequest.setBeneficiarySecondLastName(anOriginalRequest.readValueParam("@i_sapellido"));
		aInternationalTransferRequest.setBeneficiaryBusinessName(anOriginalRequest.readValueParam("@i_razon_social"));
		aInternationalTransferRequest.setBeneficiaryCountryCode(Integer.parseInt(anOriginalRequest.readValueParam("@i_paiben")));
		aInternationalTransferRequest.setBeneficiaryAddress(anOriginalRequest.readValueParam("@i_dirben"));

		// Beneficiary Bank Info
		aInternationalTransferRequest.setBeneficiaryBankCode(anOriginalRequest.readValueParam("@i_bcoben"));
		aInternationalTransferRequest.setBeneficiaryBankOfficeCode(Integer.parseInt(anOriginalRequest.readValueParam("@i_ofiben")));
		aInternationalTransferRequest.setBeneficiaryBankName(anOriginalRequest.readValueParam("@i_nomben"));
		aInternationalTransferRequest.setBeneficiaryBankSwiftAbaCode(anOriginalRequest.readValueParam("@i_swtben"));
		aInternationalTransferRequest.setBeneficiaryBankAddressType(anOriginalRequest.readValueParam("@i_tdirben"));

		// Intermediary Bank Info
		aInternationalTransferRequest.setIntermediaryBankCode(Integer.parseInt(anOriginalRequest.readValueParam("@i_bcoint")));
		aInternationalTransferRequest.setIntermediaryBankOfficeCode(Integer.parseInt(anOriginalRequest.readValueParam("@i_ofiint")));
		aInternationalTransferRequest.setIntermediaryBankSwiftAbaCode(anOriginalRequest.readValueParam("@i_swtint"));
		aInternationalTransferRequest.setIntermediaryBankAddressType(anOriginalRequest.readValueParam("@i_tdirint"));

		aInternationalTransferRequest.setAmmount(new BigDecimal(anOriginalRequest.readValueParam("@i_val").toString()));
		aInternationalTransferRequest.setOriginProduct(originProduct);
		aInternationalTransferRequest.setDestinationProduct(destinationProduct);
		aInternationalTransferRequest.setDescriptionTransfer(anOriginalRequest.readValueParam("@i_concepto"));

		Client cliente = new Client();
		cliente.setId(anOriginalRequest.readValueParam("@i_ente"));
		aInternationalTransferRequest.setUser(cliente);
		aInternationalTransferRequest.setOriginatorFunds(anOriginalRequest.readValueParam("@i_origen_fondos"));
		aInternationalTransferRequest.setReceiverFunds(anOriginalRequest.readValueParam("@i_dest_fondos"));

		aInternationalTransferRequest.setChannelId(anOriginalRequest.readValueParam("@s_servicio"));
		aInternationalTransferRequest.setDate(anOriginalRequest.readValueParam("@s_date"));

		if (anOriginalRequest.readValueParam("@s_ssn_branch") != null)
			aInternationalTransferRequest.setReferenceNumberBranch(anOriginalRequest.readValueParam("@s_ssn_branch"));

		if (anOriginalRequest.readValueParam("@s_ssn") != null)
			aInternationalTransferRequest.setReferenceNumber(anOriginalRequest.readValueParam("@s_ssn"));

		aInternationalTransferRequest = (InternationalTransferRequest) Utils.setSessionParameters(aInternationalTransferRequest, anOriginalRequest);
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request de Transferencia:" + aInternationalTransferRequest + anOriginalRequest);
		return aInternationalTransferRequest;
	}

	/**
	 * Get ACHTransferRequest with information from context only with parameters to transfers online.
	 *
	 * @param aBagSPJavaOrchestration
	 * @param ORIGINAL_REQUEST
	 * @param RESPONSE_LOCAL_VALIDATION
	 * @return ACHTransferRequest
	 * 
	 */
	public static ACHTransferRequest transformACHAccountTransferRequest(Map<String, Object> aBagSPJavaOrchestration, String ORIGINAL_REQUEST, String RESPONSE_LOCAL_VALIDATION) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Creando Request de Transferencia ACH");

		ACHTransferRequest aACHTransferRequest = new ACHTransferRequest();
		IProcedureRequest anOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "ORIGINAL_REQUEST ACH" + anOriginalRequest.getProcedureRequestAsString());

		 AccountingParameterResponse responseAccountingParameters = null;
		  
		 responseAccountingParameters = (AccountingParameterResponse) aBagSPJavaOrchestration.get(ACCOUNTING_PARAMETER);
		 if (logger.isInfoEnabled()) 
			 logger.logInfo(CLASS_NAME + " VALOR" + aBagSPJavaOrchestration.get(ACCOUNTING_PARAMETER));
		 
		 ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction = (ICoreServiceMonetaryTransaction)
		 aBagSPJavaOrchestration.get(CORESERVICEMONETARYTRANSACTION);
		 
		 Map<String,AccountingParameter> map= coreServiceMonetaryTransaction.existsAccountingParameter( responseAccountingParameters,
		 Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")), SERVICETRN_COST_T, SERVICETRN_COST_C);
		 
		 map= null; map= coreServiceMonetaryTransaction.existsAccountingParameter( responseAccountingParameters,
		 Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")), SERVICETRN_COST_T, SERVICETRN_TYPE_D);
		 
		 if (!Utils.isNull(map)){ 
			 aACHTransferRequest.setCause(map.get("ACCOUNTING_PARAM").getCause()); 
		 } 
		 map= null; 
		 map= coreServiceMonetaryTransaction.existsAccountingParameter( responseAccountingParameters,
		 Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")), SERVICETRN_COST_C, SERVICETRN_TYPE_D);
		 if (!Utils.isNull(map)){
			 aACHTransferRequest.setCauseComi(map.get("ACCOUNTING_PARAM").getCause ()); 
			 if (logger.isInfoEnabled()) {
				 logger.logInfo(CLASS_NAME + " map.get(ACCOUNTING_PARAM).getCause() "+""+ map.get("ACCOUNTING_PARAM").getCause()); 
				 logger.logInfo(CLASS_NAME + " map.get(ACCOUNTING_PARAM).getCauseComi() "+""+ map.get("ACCOUNTING_PARAM").getCauseComi());
			 }
			 
		} 
			 
		IProcedureResponse anLocalValidationResponse = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_LOCAL_VALIDATION);

		Currency currencyProduct = new Currency();
		currencyProduct.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon").toString()));

		Currency destinationCurrency = new Currency();
		destinationCurrency.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon").toString()));

		Product originProduct = new Product();
		originProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta").toString());
		originProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod").toString()));
		originProduct.setCurrency(currencyProduct);

		Product destinationProduct = new Product();
		destinationProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta_des"));
		destinationProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod_des").toString()));
		destinationProduct.setCurrency(destinationCurrency);

		aACHTransferRequest.setOriginProduct(originProduct);
		aACHTransferRequest.setDestinationProduct(destinationProduct);

		/* ACH 86 */
		aACHTransferRequest.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon_trn").toString()));
		if (logger.isInfoEnabled())
			logger.logInfo("Moneda de la Transaccion " + anOriginalRequest.readValueParam("@i_mon_trn").toString());
		aACHTransferRequest.setClientCoreCode(Integer.parseInt(anOriginalRequest.readValueParam("@i_ente")));
		aACHTransferRequest.setCommisionAmmount(new BigDecimal(anOriginalRequest.readValueParam("@i_comision")));
		aACHTransferRequest.setOperation("N");

		aACHTransferRequest.setComissionCurrency(anOriginalRequest.readValueParam("@i_mon_com"));
		if (logger.isInfoEnabled())
			logger.logInfo("causa transaccion" + aACHTransferRequest.getCause());
		if (logger.isInfoEnabled())
			logger.logInfo("causa comision" + aACHTransferRequest.getCauseComi());
		if (logger.isInfoEnabled())
			logger.logInfo("MONEDA comision" + aACHTransferRequest.getComissionCurrency());

		aACHTransferRequest.setAmmount(new BigDecimal(anOriginalRequest.readValueParam("@i_val")));
		aACHTransferRequest.setDescriptionTransfer(anOriginalRequest.readValueParam("@i_concepto"));
		aACHTransferRequest.setDestinationBankName(anOriginalRequest.readValueParam("@i_nom_banco_des"));
		aACHTransferRequest.setBeneficiaryName(anOriginalRequest.readValueParam("@i_nombre_benef"));
		aACHTransferRequest.setDocumentIdBeneficiary(anOriginalRequest.readValueParam("@i_doc_benef"));
		aACHTransferRequest.setDestinationBankPhone(anOriginalRequest.readValueParam("@i_telefono_benef"));
		aACHTransferRequest.setOriginatorFunds(anOriginalRequest.readValueParam("@i_origen_fondos"));
		aACHTransferRequest.setReceiverFunds(anOriginalRequest.readValueParam("@i_dest_fondos"));
		aACHTransferRequest.setChargeAccount(anLocalValidationResponse.readValueParam("@o_cta_cobro"));
		aACHTransferRequest.setChargeProduct(Short.parseShort(anLocalValidationResponse.readValueParam("@o_prod_cobro")));

		aACHTransferRequest.setChannelId(anOriginalRequest.readValueParam("@s_servicio"));
		if (anOriginalRequest.readValueParam("@s_filial") != null)
			aACHTransferRequest.setRole(Integer.parseInt(anOriginalRequest.readValueParam("@s_filial")));
		if (anOriginalRequest.readValueParam("@s_ssn_branch") != null)
			aACHTransferRequest.setReferenceNumberBranch(anOriginalRequest.readValueParam("@s_ssn_branch"));
		if (anOriginalRequest.readValueParam("@s_ssn") != null)
			aACHTransferRequest.setReferenceNumber(anOriginalRequest.readValueParam("@s_ssn"));

		aACHTransferRequest = (ACHTransferRequest) Utils.setSessionParameters(aACHTransferRequest, anOriginalRequest);
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request de Transferencia ACH:" + aACHTransferRequest + anOriginalRequest);

		return aACHTransferRequest;
	}
}
