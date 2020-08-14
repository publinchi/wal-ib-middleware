package com.cobiscorp.ecobis.orchestration.core.ib.transaction.monetary;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.commons.ssn.ISSNService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.ecobis.ib.application.dtos.AccountingParameterRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AccountingParameterResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TransactionMonetaryRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TransactionMonetaryResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ValidationAccountsRequest;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountingParameter;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;

@Component(name = "TransactionMonetaryExecutor", immediate = false)
@Service(value = { ICoreServiceMonetaryTransaction.class })
@Properties(value = { @Property(name = "service.description", value = "TransactionMonetaryExecutor"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TransactionMonetaryExecutor") })
public class TransactionMonetaryExecutor extends SPJavaOrchestrationBase implements ICoreServiceMonetaryTransaction {
	private static final String COBIS_CONTEXT = "COBIS";
	private static final int CHECKING_ACCOUNT = 3;
	private static final int SAVING_ACCOUNT = 4;
	private static final int DEBIT_CHECKING_ACCOUNT = 50;
	private static final int DEBIT_SAVING_ACCOUNT = 264;
	private static final int CREDIT_CHECKING_ACCOUNT = 48;
	private static final int CREDIT_SAVING_ACCOUNT = 253;
	private ISSNService ssnService;
	private static ILogger logger = LogFactory.getLogger(TransactionMonetaryExecutor.class);

	private Map<String, Integer> trxProducts = new HashMap<String, Integer>();

	private AccountingParameterResponse transformAccountingParameterResponse(IProcedureResponse aResponse) {
		trxProducts.put("3D", DEBIT_CHECKING_ACCOUNT);
		trxProducts.put("4D", DEBIT_SAVING_ACCOUNT);
		trxProducts.put("3C", CREDIT_CHECKING_ACCOUNT);
		trxProducts.put("4C", CREDIT_SAVING_ACCOUNT);

		AccountingParameterResponse response = new AccountingParameterResponse();
		List<AccountingParameter> accountingParameters = new ArrayList<AccountingParameter>();
		IResultSetRow[] rows = aResponse.getResultSet(1).getData().getRowsAsArray();

		if (rows.length != 0) {
			for (IResultSetRow iResultSetRow : rows) {
				IResultSetRowColumnData[] cols = iResultSetRow.getColumnsAsArray();
				AccountingParameter accountingParameter = new AccountingParameter();
				accountingParameter.setCause(cols[3].getValue());
				accountingParameter.setProductId(Integer.parseInt(cols[1].getValue()));
				accountingParameter.setService(cols[0].getValue());
				accountingParameter.setSign(cols[4].getValue());
				accountingParameter.setTypeCost(cols[7].getValue());
				accountingParameter.setTypeCausa(cols[4].getValue());
				if (cols[1].isNull() || cols[4].isNull())
					accountingParameter.setTransaction(0);
				else
					accountingParameter.setTransaction(trxProducts.get(cols[1].getValue() + cols[4].getValue()));
				accountingParameters.add(accountingParameter);

			}
		}

		response.setAccountingParameters(accountingParameters);
		response.setReturnCode(aResponse.getReturnCode());
		response.setMessages(Utils.returnArrayMessage(aResponse));

		if (aResponse.getReturnCode() == 0) {
			response.setSuccess(true);
		} else {
			response.setSuccess(false);
		}

		return response;
	}

	private TransactionMonetaryResponse transformTransactionMonetaryResponse(IProcedureResponse aResponse) {
		TransactionMonetaryResponse response = new TransactionMonetaryResponse();
		response.setReferenceNumber(aResponse.readValueFieldInHeader("serviceExecutionId"));
		response.setReferenceNumberBranch(aResponse.readValueFieldInHeader("ssn_branch"));
		response.setMessages(Utils.returnArrayMessage(aResponse));
		if (aResponse.getReturnCode() == 0) {
			response.setSuccess(true);
			response.setReturnCode(aResponse.getReturnCode());
		} else {
			response.setSuccess(false);
			response.setReturnCode(aResponse.getReturnCode());
		}

		return response;
	}

	@Override
	public AccountingParameterResponse getAccountingParameter(AccountingParameterRequest anAccountingParameterRequest) {
		// TODO Auto-generated method stub

		IProcedureRequest request = initProcedureRequest(anAccountingParameterRequest.getOriginalRequest());
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800111");
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1800111");

		if (logger.isInfoEnabled()) {
			logger.logInfo("START getAccountingParameter");
		}
		request.setSpName("cob_bvirtual..sp_bv_ing_serv");
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "SI");
		request.addInputParam("@i_transaccion", ICTSTypes.SQLINT4,
				String.valueOf(anAccountingParameterRequest.getTransaction()));

		if (logger.isDebugEnabled()) {
			logger.logDebug("REQUEST ACCOUNTING PARAMETER: -->" + request.getProcedureRequestAsString());
		}

		IProcedureResponse wResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("RESPONSE ACCOUNTING PARAMETER: " + wResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("FINISH getAccountingParameter");
		}

		return transformAccountingParameterResponse(wResponse);

	}

	private int getSsnNumber(String sequentialServer, String ssnName) {

		int ssnToReturn = 0;

		try {
			if (null != ssnService) {
				if ((null != sequentialServer) && !"".equals(sequentialServer)) {
					ssnToReturn = (int) ssnService.getSSN(sequentialServer);
				} else {
					ssnToReturn = (int) ssnService.getSSN();
				}
				if (0 == ssnToReturn)
					logger.logError("Error " + ssnName + " inválido");
			} else {
				logger.logError("Es null el ssnService");
			}
		} catch (Exception e) {
			logger.logError("Error al obtener " + ssnName + ":", e);
		}

		return ssnToReturn;

	}

	@Override
	public TransactionMonetaryResponse debitCreditAccount(TransactionMonetaryRequest aTransactionMonetaryRequest) {

		IProcedureRequest request = initProcedureRequest(aTransactionMonetaryRequest.getOriginalRequest());

		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN,
				String.valueOf(aTransactionMonetaryRequest.getTransaction()));
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		if (!aTransactionMonetaryRequest.getCorrection().equals("S")) {
			request.addFieldInHeader(ICOBISTS.HEADER_SSN, ICOBISTS.HEADER_NUMBER_TYPE,
					aTransactionMonetaryRequest.getReferenceNumber());
			request.addFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH, ICOBISTS.HEADER_NUMBER_TYPE,
					aTransactionMonetaryRequest.getReferenceNumberBranch());
		} else {
			request.removeFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH);
			request.removeParam("@s_ssn_branch");
		}

		request.setSpName("cob_remesas..sp_channels_ndc_automatic");

		if (aTransactionMonetaryRequest.getCorrection().equals("S")) {
			request.addInputParam("@s_ssn", ICTSTypes.SYBINT4, aTransactionMonetaryRequest.getReferenceNumber());
			request.addInputParam("@t_corr", ICTSTypes.SYBVARCHAR,
					String.valueOf(aTransactionMonetaryRequest.getCorrection()));
			request.addInputParam("@t_ssn_corr", ICTSTypes.SYBINT4,
					String.valueOf(aTransactionMonetaryRequest.getSsnCorrection()));
		} else
			request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");

		request.addInputParam("@t_trn", ICTSTypes.SYBINT4,
				String.valueOf(aTransactionMonetaryRequest.getTransaction()));

		request.addInputParam("@i_causa", ICTSTypes.SYBVARCHAR, aTransactionMonetaryRequest.getCause());
		//request.addInputParam("@i_causa", ICTSTypes.SYBVARCHAR, "185");
		request.addInputParam("@i_val", ICTSTypes.SYBMONEY, aTransactionMonetaryRequest.getAmmount().toString());
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR,
				aTransactionMonetaryRequest.getProduct().getProductNumber());
		request.addInputParam("@i_mon", ICTSTypes.SQLINT2, aTransactionMonetaryRequest.getProduct().getCurrency().getCurrencyId().toString());
		//request.addInputParam("@i_moneda_destino", ICTSTypes.SQLINT2, String.valueOf(aTransactionMonetaryRequest.getPayCurrency()));
		

		request.addInputParam("@i_fecha", ICTSTypes.SQLDATETIME, request.readValueFieldInHeader("date"));
		request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, aTransactionMonetaryRequest.getConcept());
		request.addInputParam("@i_turno", ICTSTypes.SYBINT1, "1");
		request.addInputParam("@i_alt", ICTSTypes.SYBINT1,
				String.valueOf(aTransactionMonetaryRequest.getAlternateCode()));
		request.addInputParam("@i_imp", ICTSTypes.SQLVARCHAR, "N");
		request.addInputParam("@i_origen_fon", ICTSTypes.SQLVARCHAR, aTransactionMonetaryRequest.getSourceFunds());
		request.addInputParam("@i_destino_fon", ICTSTypes.SQLVARCHAR, aTransactionMonetaryRequest.getUseFunds());
		request.addInputParam("@i_levanta_form", ICTSTypes.SQLVARCHAR, "N");
		request.addInputParam("@i_canal", ICTSTypes.SQLINT1, request.readValueFieldInHeader("channel"));
		request.addInputParam("@i_producto", ICTSTypes.SQLINT1,
				String.valueOf(aTransactionMonetaryRequest.getProduct().getProductType()));
		request.addInputParam("@i_causa_comi", ICTSTypes.SQLVARCHAR, aTransactionMonetaryRequest.getCauseComi());
		if (!aTransactionMonetaryRequest.getCauseComi().equals("0")) {
			request.addInputParam("@i_val_comi", ICTSTypes.SQLMONEY,
					aTransactionMonetaryRequest.getAmmountCommission().toString());
		}

		IProcedureResponse wResponse = executeCoreBanking(request);

		return transformTransactionMonetaryResponse(wResponse);

	}

	public Map<String, AccountingParameter> existsAccountingParameter(
			AccountingParameterResponse anAccountingParameterResponse, int product, String type, String typeCa) {

		Map<String, AccountingParameter> map = null;

		if (anAccountingParameterResponse.getAccountingParameters().size() == 0)
			return map;

		for (AccountingParameter parameter : anAccountingParameterResponse.getAccountingParameters()) {
			if (logger.isDebugEnabled())
				logger.logDebug(" TRN: " + String.valueOf(parameter.getTransaction()) + " CAUSA: "
						+ parameter.getCause() + " TIPO :" + parameter.getTypeCost());
			if (type.equals("C")) {

				if (parameter.getTypeCost().equals(type) && parameter.getProductId() == product) {
					map = new HashMap<String, AccountingParameter>();
					map.put("ACCOUNTING_PARAM", parameter);
					break;
				}

			} else {

				if (parameter.getTypeCost().equals(type) && parameter.getProductId() == product
						&& parameter.getTypeCausa().equals(typeCa)) {
					map = new HashMap<String, AccountingParameter>();
					map.put("ACCOUNTING_PARAM", parameter);
					break;

				}

			}

		}
		return map;
	}

	@Override
	public IProcedureResponse getBalancesToSynchronize(ValidationAccountsRequest validationAccountsRequest) {

		IProcedureRequest anOriginalRequest = initProcedureRequest(validationAccountsRequest.getOriginalRequest());
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800050");
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.setSpName("cobis..sp_resultados_bv");
		String t_trn = validationAccountsRequest.getCodeTransactionalIdentifier();

		if (logger.isDebugEnabled())
			logger.logDebug("t_trn a evaluar: " + t_trn);

		anOriginalRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "R");
		anOriginalRequest.addInputParam("@i_ofi", ICTSTypes.SQLINT2, anOriginalRequest.readValueFieldInHeader("ofi"));
		anOriginalRequest.addInputParam("@i_fecha", ICTSTypes.SQLDATETIME,
				anOriginalRequest.readValueFieldInHeader("date"));
		anOriginalRequest.addInputParam("@i_cuenta", ICTSTypes.SQLVARCHAR,
				validationAccountsRequest.getOriginProduct().getProductNumber());
		anOriginalRequest.addInputParam("@i_producto", ICTSTypes.SQLINT2,
				validationAccountsRequest.getOriginProduct().getProductType().toString());
		anOriginalRequest.addInputParam("@i_servicio", ICTSTypes.SQLINT2, validationAccountsRequest.getChannelId());

		// PARA TRANSFERENCIAS CUENTAS PROPIAS Y PAGOS A TERCERLOS SE ENVIAN LOS
		// PARAMETROS DE CUENTA DESTINO
		if ("1800008".equals(t_trn) || "1800009".equals(t_trn) || "1800011".equals(t_trn) || "1800012".equals(t_trn)) {
			anOriginalRequest.addInputParam("@i_mon_d", ICTSTypes.SQLINT2,
					validationAccountsRequest.getDestinationProduct().getCurrency().getCurrencyId().toString());
			anOriginalRequest.addInputParam("@i_cuenta_d", ICTSTypes.SQLVARCHAR,
					validationAccountsRequest.getDestinationProduct().getProductNumber());
			anOriginalRequest.addInputParam("@i_producto_d", ICTSTypes.SQLINT2,
					validationAccountsRequest.getDestinationProduct().getProductType().toString());
		}

		// TRANSFERENCIAS ACH
		if ("1800015".equals(t_trn)) {
			anOriginalRequest.addInputParam("@i_ruta_trans",
					anOriginalRequest.readParam("@i_ruta_transito").getDataType(),
					anOriginalRequest.readValueParam("@i_ruta_transito"));
		}

		if (logger.isDebugEnabled())
			logger.logDebug("Data enviada a ejecutar:" + anOriginalRequest.toString());
		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo("Respuesta Devuelta del Core:" + response.getProcedureResponseAsString());

		if (((response.getReturnCode() == 0) && Utils.validateErrorCode(response, 0))
				|| Utils.validateErrorCode(response, 40004)) {
			Integer product = validationAccountsRequest.getOriginProduct().getProductType();
			if (logger.isDebugEnabled())
				logger.logDebug("Producto: " + product);

		}

		return response;
	}

	@Override
	public BigDecimal getCost(AccountingParameter accountingParameter, Currency currency, Product product) {
		IProcedureRequest request = new ProcedureRequestAS();
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800050");
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.setSpName("cobis..sp_bv_calc_cost_terc");
		request.addInputParam("@i_transaccion", ICTSTypes.SQLINT4,
				String.valueOf(accountingParameter.getTransaction()));
		request.addInputParam("@i_servicio", ICTSTypes.SQLVARCHAR, accountingParameter.getService());
		request.addInputParam("@i_rubro", ICTSTypes.SQLVARCHAR, accountingParameter.getCause());

		if (currency.getCurrencyId() != null)
			request.addInputParam("@i_moneda", ICTSTypes.SQLINT2, currency.getCurrencyId().toString());

		if (product.getProductNumber() != null) {
			request.addInputParam("@i_producto", ICTSTypes.SQLINT2, product.getProductType().toString());
			request.addInputParam("@i_cuenta", ICTSTypes.SQLVARCHAR, product.getProductNumber());
		}
		request.addOutputParam("@o_costo", ICTSTypes.SQLMONEY, "0");

		IProcedureResponse response = executeCoreBanking(request);

		String cost = "";
		if (response.getReturnCode() == 0)
			cost = response.readValueParam("@o_costo");

		if (cost == null)
			return new BigDecimal(0);
		else if (cost.equals(""))
			return new BigDecimal(0);
		else
			return new BigDecimal(cost);
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {

		return null;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {

		return null;
	}

}
