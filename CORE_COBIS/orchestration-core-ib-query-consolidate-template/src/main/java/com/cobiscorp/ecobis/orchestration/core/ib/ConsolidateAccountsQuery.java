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
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobisv.commons.context.CobisSession;
import com.cobiscorp.cobisv.commons.context.Context;
import com.cobiscorp.cobisv.commons.context.ContextManager;
import com.cobiscorp.ecobis.ib.application.dtos.ConsolidateRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ConsolidateResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BalanceProduct;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ProductConsolidate;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreConsolidateAccountsQuery;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;

@Component(name = "ConsolidateAccountsQuery", immediate = false)
@Service(value = { ICoreConsolidateAccountsQuery.class })
@Properties(value = { @Property(name = "service.description", value = "ConsolidateAccountsQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ConsolidateAccountsQuery") })
public class ConsolidateAccountsQuery extends SPJavaOrchestrationBase implements ICoreConsolidateAccountsQuery {

	/**
	 * Instance logger component
	 */
	private static ILogger logger = LogFactory.getLogger(ConsolidateAccountsQuery.class);

	private static final String CLASS_NAME = "AccountsQuerys >-----> ";
	protected static final String COBIS_CONTEXT = "COBIS";
	private static final int CHECKING_ACCOUNT = 3;
	private static final int SAVING_ACCOUNT = 4;
	private static final int LOAN = 7;
	private static final int TIME_DEPOSIT = 14;
	private static final int CREDIT_CARD = 83;
	private int countCtacte = 0;
	private int countCtaAho = 0;
	private Boolean haveToAddCountCte = true;

	@Override
	public ConsolidateResponse getConsolidateCheckingAccountByClient(ConsolidateRequest consolidateRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: getConsolidateCheckingAccountByClient ");
		}

		Product product = new Product();
		product.setProductType(CHECKING_ACCOUNT);
		BalanceProduct balanceProduct = new BalanceProduct();
		balanceProduct.setProduct(product);
		consolidateRequest.setBalanceProduct(balanceProduct);
		countCtacte = 0;
		haveToAddCountCte = consolidateRequest.getHaveToAddCountCte();
		ConsolidateResponse consolidateResponse = this.getSummaryBalances(consolidateRequest);

		if (logger.isInfoEnabled())
			logger.logInfo("TERMINANDO SERVICIO: getConsolidateCheckingAccountByClient ");

		return consolidateResponse;
	}

	@Override
	public ConsolidateResponse getConsolidateSavingAccountByClient(ConsolidateRequest consolidateRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: getConsolidateSavingAccountByClient ");
		}

		Product product = new Product();
		product.setProductType(SAVING_ACCOUNT);
		BalanceProduct balanceProduct = new BalanceProduct();
		balanceProduct.setProduct(product);
		consolidateRequest.setBalanceProduct(balanceProduct);
		countCtaAho = 0;
		haveToAddCountCte = consolidateRequest.getHaveToAddCountCte();
		ConsolidateResponse consolidateResponse = this.getSummaryBalances(consolidateRequest);

		if (logger.isInfoEnabled())
			logger.logInfo("TERMINANDO SERVICIO: getConsolidateSavingAccountByClient  ");
		return consolidateResponse;
	}

	@Override
	public ConsolidateResponse getConsolidateLoanAccountByClient(ConsolidateRequest consolidateRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: getConsolidateLoanAccountByClient   ");
		}

		Product product = new Product();
		product.setProductType(LOAN);
		BalanceProduct balanceProduct = new BalanceProduct();
		balanceProduct.setProduct(product);
		consolidateRequest.setBalanceProduct(balanceProduct);

		ConsolidateResponse consolidateResponse = this.getSummaryBalances(consolidateRequest);

		if (logger.isInfoEnabled())
			logger.logInfo("TERMINANDO SERVICIO: getConsolidateLoanAccountByClient  ");
		return consolidateResponse;
	}

	@Override
	public ConsolidateResponse getConsolidateFixedTermDepositAccountByClient(ConsolidateRequest consolidateRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: getConsolidateFixedTermDepositAccountByClient");
		}

		Product product = new Product();
		product.setProductType(TIME_DEPOSIT);
		BalanceProduct balanceProduct = new BalanceProduct();
		balanceProduct.setProduct(product);
		consolidateRequest.setBalanceProduct(balanceProduct);

		ConsolidateResponse consolidateResponse = this.getSummaryBalances(consolidateRequest);

		if (logger.isInfoEnabled())
			logger.logInfo("TERMINANDO SERVICIO: getConsolidateFixedTermDepositAccountByClient ");
		return consolidateResponse;
	}

	@Override
	public ConsolidateResponse getConsolidateCreditCardByClient(ConsolidateRequest consolidateRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: getConsolidateCreditCardByClient");
		}

		Product product = new Product();
		product.setProductType(CREDIT_CARD);
		BalanceProduct balanceProduct = new BalanceProduct();
		balanceProduct.setProduct(product);
		consolidateRequest.setBalanceProduct(balanceProduct);

		ConsolidateResponse consolidateResponse = this.getSummaryBalances(consolidateRequest);

		if (logger.isInfoEnabled())
			logger.logInfo("TERMINANDO SERVICIO: getConsolidateCreditCardByClient");
		return consolidateResponse;
	}

	private ConsolidateResponse getSummaryBalances(ConsolidateRequest consolidateRequest)
			throws CTSServiceException, CTSInfrastructureException {

		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: entra a getSummaryBalances  ");
		}

		ConsolidateResponse consolidateResponse = new ConsolidateResponse();
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getSummaryBalances ");
		}

		Context context = ContextManager.getContext();
		CobisSession session = null;
		if (context != null && context.getSession() != null) // validacion si llamada viene del Admin no hay sesion
			session = (CobisSession) context.getSession();

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_SSN, consolidateRequest.getSessionIdCore());
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH, consolidateRequest.getSessionIdIB());
		if (consolidateRequest.getBalanceProduct() != null
				&& consolidateRequest.getBalanceProduct().getProduct() != null
				&& consolidateRequest.getBalanceProduct().getProduct().getProductType() == CHECKING_ACCOUNT)
			anOriginalRequest.setSpName("cob_cuentas..cons_resumen_ctas_cte");

		if (consolidateRequest.getBalanceProduct() != null
				&& consolidateRequest.getBalanceProduct().getProduct() != null
				&& consolidateRequest.getBalanceProduct().getProduct().getProductType() == SAVING_ACCOUNT)
			anOriginalRequest.setSpName("cob_ahorros..cons_resumen_ctas_aho");

		if (consolidateRequest.getBalanceProduct() != null
				&& consolidateRequest.getBalanceProduct().getProduct() != null
				&& consolidateRequest.getBalanceProduct().getProduct().getProductType() == LOAN)
			anOriginalRequest.setSpName("cob_cartera..cons_resumen_prestamo");

		if (consolidateRequest.getBalanceProduct() != null
				&& consolidateRequest.getBalanceProduct().getProduct() != null
				&& consolidateRequest.getBalanceProduct().getProduct().getProductType() == TIME_DEPOSIT)
			anOriginalRequest.setSpName("cobis..cons_resumen_ctas_dpf");

		if (consolidateRequest.getBalanceProduct() != null
				&& consolidateRequest.getBalanceProduct().getProduct() != null
				&& consolidateRequest.getBalanceProduct().getProduct().getProductType() == CREDIT_CARD)
			anOriginalRequest.setSpName("cob_atm..cons_resumen_tar_cred");

		if (consolidateRequest.getClient() != null) {
			anOriginalRequest.addInputParam("@i_cliente", ICTSTypes.SYBINTN, consolidateRequest.getClient().getId());
		}

		anOriginalRequest.addInputParam("@i_operacion", ICTSTypes.SYBVARCHAR, "C");

		if (consolidateRequest != null && consolidateRequest.getCurrency() != null
				&& consolidateRequest.getCurrency().getCurrencyId() != null
				&& consolidateRequest.getCurrency().getCurrencyId() != -1) {
			anOriginalRequest.addInputParam("@i_moneda", ICTSTypes.SYBINTN,
					"" + consolidateRequest.getCurrency().getCurrencyId());
		}

		if (session != null)
			anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SYBINT4, "" + session.getOffice());
		else
			anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SYBINT4, "" + "0");

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Data enviada a ejecutar :" + anOriginalRequest.toString());

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking: " + response.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled())
			logger.logInfo("TERMINANDO SERVICIO: getSummaryBalances ");

		consolidateResponse = transformResponseToDto(response);

		return consolidateResponse;

	}

	/**
	 * Transform Reponse to ConsolidateResponse
	 *
	 * @param IprocedureResponse
	 * @return ConsolidateResponse
	 */
	private ConsolidateResponse transformResponseToDto(IProcedureResponse response) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + " Ejecutando Transformacion de IProcedureResponse a DTO consolidateResponse:"
					+ response);

		ConsolidateResponse consolidateResponse = new ConsolidateResponse();
		List<ProductConsolidate> productConsolidateCollection = new ArrayList<ProductConsolidate>();
		Utils.transformIprocedureResponseToBaseResponse(consolidateResponse, response);
		int i = 0;
        
		// GCO-manejo de mensajes de Error
		if (response.getReturnCode() == 0) {
			if (logger.isDebugEnabled())
				logger.logDebug(CLASS_NAME + "*** Crea ConsolidateResponse-ValidationErrorCode 0,Code:"
						+ response.getReturnCode());
			IResultSetBlock resulsetProductBalance = response.getResultSet(1);
			IResultSetRow[] rowsTemp = resulsetProductBalance.getData().getRowsAsArray();

			if (rowsTemp.length > 0) {
				StringBuilder sbProducts = new StringBuilder(rowsTemp.length * 6);
				for (IResultSetRow iResultSetRow : rowsTemp) {
					IResultSetRowColumnData[] rows = iResultSetRow.getColumnsAsArray();
					ProductConsolidate productConsolidate = new ProductConsolidate();
					BalanceProduct balanceProduct = new BalanceProduct();
					Product product = new Product();
					Currency currency = new Currency();
					if (rows[0].getValue() != null)
						product.setProductType(new Integer(rows[0].getValue().toString()));
					if (rows[1].getValue() != null) {
						product.setProductDescription(rows[1].getValue().toString());
						product.setProductName(rows[1].getValue().toString());
					}
					if (rows[2].getValue() != null)
						currency.setCurrencyId(new Integer(rows[2].getValue().toString()));
					if (rows[3].getValue() != null)
						currency.setCurrencyDescription((rows[3].getValue().toString()));
					if (rows[4].getValue() != null)
						currency.setCurrencyNemonic(rows[4].getValue().toString());
					if (rows[5].getValue() != null)
						product.setProductNumber(rows[5].getValue().toString());
					if (rows[6].getValue() != null)
						product.setProductAlias(rows[6].getValue().toString());
					if (rows[7].getValue() != null)
						balanceProduct.setAvailableBalance(new BigDecimal(rows[7].getValue().toString()));
					if (rows[8].getValue() != null)
						balanceProduct.setEquityBalance(new BigDecimal(rows[8].getValue().toString()));
					if (rows[9].getValue() != null)
						balanceProduct.setDrawBalance(new BigDecimal(rows[9].getValue().toString()));
					if (rows[0].getValue().equals("3") || rows[0].getValue().equals("4")) {
						if (rows[10].getValue() != null)
							balanceProduct.setExpirationDate(Utils.formatDate(rows[10].getValue().toString()));
						if (rows[11].getValue() != null)
							balanceProduct.setRate(rows[11].getValue().toString());
						if (rows[12].getValue() != null)
							product.setBankProductId(new Integer(rows[12].getValue().toString()));
						if (rows[13].getValue() != null)
							product.setBankProduct(rows[13].getValue().toString());
					}
					if (rows[0].getValue().equals("7")) {
						if (rows[11].getValue() != null)
							balanceProduct.setTotalBalance(new BigDecimal(rows[11].getValue().toString()));
						if (rows[10].getValue() != null)
							balanceProduct.setDateLastMovent(rows[10].getValue().toString());
						if (rows[12].getValue() != null)
							balanceProduct.setNextPaymentValue(new BigDecimal(rows[12].getValue().toString()));
						if (rows[13].getValue() != null)
							balanceProduct.setExpirationDate(Utils.formatDate(rows[13].getValue().toString()));
					}
					if (rows[0].getValue().equals("14")) { // plazo fijo
						if (rows[10].getValue() != null)
							balanceProduct.setExpirationDate(Utils.formatDate(rows[10].getValue().toString()));
						if (rows[11].getValue() != null)
							balanceProduct.setRate(rows[11].getValue().toString());
						if (rows[12].getValue() != null)
							balanceProduct.setCashBalance(new BigDecimal(rows[12].getValue().toString()));
						if (rows[13].getValue() != null)
							balanceProduct.setDateLastMovent(rows[13].getValue().toString());
						if (rows[14].getValue() != null)
							balanceProduct.setState(rows[14].getValue().toString());
					}
					// grabo la posición y el número del producto en el
					// stringBuilder
					sbProducts.append(product.getProductNumber());
					sbProducts.append("/");
					sbProducts.append(currency.getCurrencyId().toString());
					sbProducts.append("/");
					if (product.getProductType().intValue() == SAVING_ACCOUNT && haveToAddCountCte)
						sbProducts.append(String.valueOf(i + countCtacte));
					else
						sbProducts.append(String.valueOf(i));

					if (product.getProductType().intValue() == CHECKING_ACCOUNT)
						countCtacte++;
					if (product.getProductType().intValue() == SAVING_ACCOUNT)
						countCtaAho++;

					sbProducts.append("~");
					i++;
					productConsolidate.setCurrency(currency);
					productConsolidate.setProduct(product);
					productConsolidate.setBalance(balanceProduct);

					productConsolidateCollection.add(productConsolidate);
				}
				consolidateResponse.setSbProducts(sbProducts);
			}
			consolidateResponse.setProductCollection(productConsolidateCollection);
		} else {
			consolidateResponse.setMessages(Utils.returnArrayMessage(response)); 
			if (logger.isDebugEnabled())
				logger.logDebug(CLASS_NAME + "*** Error al transformarResponse en Implementacion-ErrorCode:"
						+ response.getReturnCode());

		}
		consolidateResponse.setReturnCode(response.getReturnCode());

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + " Respuesta Devuelta productQueryResponse" + consolidateResponse);
		return consolidateResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.commons.components.ICOBISComponent#loadConfiguration
	 * (com.cobiscorp.cobis.commons.configuration.IConfigurationReader)
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase
	 * #executeJavaOrchestration
	 * (com.cobiscorp.cobis.cts.domains.IProcedureRequest, java.util.Map)
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase
	 * #processResponse(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}