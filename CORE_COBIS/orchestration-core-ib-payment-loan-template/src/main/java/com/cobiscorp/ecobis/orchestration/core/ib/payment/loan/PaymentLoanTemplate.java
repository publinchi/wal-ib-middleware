package com.cobiscorp.ecobis.orchestration.core.ib.payment.loan;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
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
import com.cobiscorp.ecobis.ib.application.dtos.PaymentLoanRequest;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentLoanResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BalanceProduct;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Office;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServicePaymentLoan;

@Component(name = "PaymentLoanTemplate", immediate = false)
@Service(value = { ICoreServicePaymentLoan.class })
@Properties(value = { @Property(name = "service.description", value = "PaymentLoanTemplate"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "PaymentLoanTemplate") })
public class PaymentLoanTemplate extends SPJavaOrchestrationBase implements ICoreServicePaymentLoan {
	private static final String CLASS_NAME = " >-----> ";
	private static ILogger logger = LogFactory.getLogger(PaymentLoanTemplate.class);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServicePaymentLoan
	 * #executePaymentLoan
	 * (com.cobiscorp.ecobis.ib.application.dtos.PaymentLoanRequest)
	 */
	@Override
	public PaymentLoanResponse executePaymentLoan(PaymentLoanRequest aPaymentLoanRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + " Ejecutando executePaymentLoan numberBranch: "
					+ aPaymentLoanRequest.getReferenceNumberBranch() + " ReferenceNumber: "
					+ aPaymentLoanRequest.getReferenceNumber());
		
		if (logger.isInfoEnabled()) logger.logInfo("::: EJECUTANDO TRANSACCION CENTRAL->LOAN");

		// Context context = ContextManager.getContext();
		// CobisSession session = (CobisSession) context.getSession();
		IProcedureRequest executionRequest = new ProcedureRequestAS();

		executionRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "7173");
		executionRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		executionRequest.addFieldInHeader(ICOBISTS.HEADER_SSN, ICOBISTS.HEADER_NUMBER_TYPE,
				aPaymentLoanRequest.getReferenceNumber());
		executionRequest.addFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH, ICOBISTS.HEADER_NUMBER_TYPE,
				aPaymentLoanRequest.getReferenceNumberBranch());
		executionRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);

		executionRequest.setSpName("cob_cartera..sp_abono_bv_cca");
		executionRequest.addInputParam("@t_online", ICTSTypes.SQLVARCHAR, "S");
		executionRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "7173");
		executionRequest.addInputParam("@t_ejec", ICTSTypes.SYBCHAR, "R");
		executionRequest.addInputParam("@s_ofi", ICTSTypes.SYBINT4,
				String.valueOf(aPaymentLoanRequest.getOfficeCode()));
		executionRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, aPaymentLoanRequest.getUserBv());
		executionRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, aPaymentLoanRequest.getTerminal());
		executionRequest.addInputParam("@s_rol", ICTSTypes.SQLINT2, String.valueOf(aPaymentLoanRequest.getRole()));

		executionRequest.addInputParam("@s_ssn_branch", ICTSTypes.SQLINT4,
				aPaymentLoanRequest.getReferenceNumberBranch());
		executionRequest.addInputParam("@s_ssn", ICTSTypes.SQLINT4, aPaymentLoanRequest.getReferenceNumber());
		executionRequest.addOutputParam("@o_referencia", ICTSTypes.SQLINT4, "0000");
		executionRequest.addOutputParam("@o_secuencial_pag", ICTSTypes.SQLINT4, "0000");
		executionRequest.addInputParam("@i_prod", ICTSTypes.SQLINT1, aPaymentLoanRequest.getProductid().toString());
		executionRequest.addInputParam("@i_mon", ICTSTypes.SQLINT2, aPaymentLoanRequest.getCurrencyId().toString());
		executionRequest.addInputParam("@i_banco", ICTSTypes.SQLVARCHAR, aPaymentLoanRequest.getLoanNumber());
		executionRequest.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aPaymentLoanRequest.getAccount());
		executionRequest.addInputParam("@i_origen_fondos", ICTSTypes.SQLVARCHAR, aPaymentLoanRequest.getSourceFunds());
		executionRequest.addInputParam("@i_dest_fondos", ICTSTypes.SQLVARCHAR, aPaymentLoanRequest.getUseFunds());
		executionRequest.addInputParam("@i_canal", ICTSTypes.SQLINT1, "1");
		executionRequest.addInputParam("@i_fecha_vig", ICTSTypes.SQLVARCHAR,
				aPaymentLoanRequest.getOriginalRequest().readValueParam("@s_date"));
		executionRequest.addInputParam("@i_monto_mpg", ICTSTypes.SQLMONEY,
				aPaymentLoanRequest.getLoanPaymentAmount().toString());
		executionRequest.addInputParam("@i_cotizacion", ICTSTypes.SQLFLT8i,
				String.valueOf(aPaymentLoanRequest.getRateValue()));
		if (aPaymentLoanRequest.getCreditAmount() != null) {
			executionRequest.addInputParam("@i_monto", ICTSTypes.SQLMONEY,
					aPaymentLoanRequest.getCreditAmount().toString());
		}
		executionRequest.addInputParam("@i_cotizacion", ICTSTypes.SQLFLT8i,
				String.valueOf(aPaymentLoanRequest.getRateValue()));
		if (aPaymentLoanRequest.getCreditAmount() != null) {
			executionRequest.addInputParam("@i_valida_des", ICTSTypes.SQLVARCHAR,
					aPaymentLoanRequest.getValidateAccount());
		}

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Ejecutando executePaymentLoan Request a enviar: "
					+ executionRequest.getProcedureRequestAsString());
		if (logger.isDebugEnabled())
			logger.logDebug("Request a enviar: " + executionRequest.toString());
		IProcedureResponse response = executeCoreBanking(executionRequest);
		if (logger.isInfoEnabled()) logger.logInfo("::: EJECUTADO CENTRAL");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + " Ejecutando executePaymentLoan Respuesta: "
					+ response.getProcedureResponseAsString());

		return transformToPaymentLoanResponse(response);
	}

	/**
	 * @param response
	 * @return
	 */
	private PaymentLoanResponse transformToPaymentLoanResponse(IProcedureResponse aProcedureResponse) {
		BalanceProduct aBalanceProduct = new BalanceProduct();

		PaymentLoanResponse aPaymentLoanResponse = new PaymentLoanResponse();

		Product product = new Product();
		Office office = new Office();

		if (aProcedureResponse.getResultSetListSize() > 0) {
			if (logger.isInfoEnabled())
				logger.logInfo("=============>>>>> INGRESO A transformToCustomerPaymentLoanResponse "
						+ aProcedureResponse.toString());

			IResultSetRow[] rows = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();
			IResultSetRowColumnData[] columns = rows[0].getColumnsAsArray();

			office.setId(columns[10].getValue() == null ? 0 : Integer.parseInt(columns[10].getValue()));
			product.setProductType(columns[11].getValue() == null ? 0 : Integer.parseInt(columns[11].getValue()));

			aBalanceProduct.setAvailableBalance(
					columns[1].getValue() == null ? new BigDecimal(0) : new BigDecimal(columns[1].getValue()));
			aBalanceProduct.setAccountingBalance(
					columns[2].getValue() == null ? new BigDecimal(0) : new BigDecimal(columns[2].getValue()));
			aBalanceProduct.setRotateBalance(
					columns[3].getValue() == null ? new BigDecimal(0) : new BigDecimal(columns[3].getValue()));
			aBalanceProduct.setBalance12H(
					columns[4].getValue() == null ? new BigDecimal(0) : new BigDecimal(columns[4].getValue()));
			aBalanceProduct.setBalance24H(
					columns[5].getValue() == null ? new BigDecimal(0) : new BigDecimal(columns[5].getValue()));
			aBalanceProduct.setRemittancesBalance(
					columns[6].getValue() == null ? new BigDecimal(0) : new BigDecimal(columns[6].getValue()));
			aBalanceProduct.setBlockedAmmount(
					columns[7].getValue() == null ? new BigDecimal(0) : new BigDecimal(columns[7].getValue()));
			aBalanceProduct
					.setBlockedNumber(columns[8].getValue() == null ? 0 : Integer.parseInt(columns[8].getValue()));
			aBalanceProduct.setBlockedNumberAmmount(
					columns[9].getValue() == null ? 0 : Integer.parseInt(columns[9].getValue()));
			aBalanceProduct.setOfficeAccount(office);
			aBalanceProduct.setProduct(product);
			aBalanceProduct.setState(columns[12].getValue() == null ? "" : columns[12].getValue());
			aBalanceProduct.setSsnHost(columns[13].getValue() == null ? 0 : Integer.parseInt(columns[13].getValue()));

			if (columns[14].getValue() != null && columns[14].getValue().compareTo("null") == 0)
				aBalanceProduct.setSurplusAmmount(new BigDecimal(0));
			else
				aBalanceProduct.setSurplusAmmount(new BigDecimal(columns[14].getValue()));

			aBalanceProduct.setIdClosed(columns[16].getValue() == null || columns[16].getValue().trim().equals("") ? 0
					: Integer.parseInt(columns[16].getValue()));
			aBalanceProduct.setCashBalance(columns[18].getValue() == null || columns[16].getValue().trim().equals("")
					? new BigDecimal(0) : new BigDecimal(columns[18].getValue()));
			aPaymentLoanResponse.setBalanceProduct(aBalanceProduct);
			aPaymentLoanResponse
					.setDateHost((columns[15].getValue() == null || columns[15].getValue().trim().equals("")) ? ""
							: columns[15].getValue());
		}
		if (aProcedureResponse.readFieldInHeader("ssn") != null) {
			aPaymentLoanResponse
					.setReference(Integer.parseInt(aProcedureResponse.readValueFieldInHeader("ssn").toString()));
		}
		if (aProcedureResponse.readValueParam("@o_secuencial_pag") != null) {
			aPaymentLoanResponse
					.setReturnValue(Integer.parseInt(aProcedureResponse.readValueParam("@o_secuencial_pag")));
		}

		if (aProcedureResponse.getReturnCode() != 0 && aProcedureResponse.getReturnCode() != 40002) {
			aPaymentLoanResponse.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		}
		aPaymentLoanResponse.setReturnCode(aProcedureResponse.getReturnCode());
		aPaymentLoanResponse.setSuccess(aProcedureResponse.getReturnCode() == 0 ? true : false);

		if (logger.isInfoEnabled())
			logger.logInfo(aPaymentLoanResponse.toString());

		return aPaymentLoanResponse;
	}
}
