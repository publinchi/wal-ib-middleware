package com.cobiscorp.ecobis.orchestration.core.ib;

import java.math.BigDecimal;
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
import com.cobiscorp.ecobis.ib.application.dtos.BalanceProductRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BalanceProductResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BalanceProduct;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceQuery;
import com.cobiscorp.ecobis.ib.utils.dtos.Utils;

@Component(name = "AccountsQuerys", immediate = false)
@Service(value = { ICoreServiceQuery.class })
@Properties(value = { @Property(name = "service.description", value = "AccountsQuerys"), @Property(name = "service.vendor", value = "COBISCORP"),
		@Property(name = "service.version", value = "4.6.1.0"), @Property(name = "service.identifier", value = "AccountsQuerys") })
public class AccountsQuerys extends SPJavaOrchestrationBase implements ICoreServiceQuery {
	private static ILogger logger = LogFactory.getLogger(AccountsQuerys.class);
	private static final String CLASS_NAME = "AccountsQuerys >-----> ";
	protected static final String COBIS_CONTEXT = "COBIS";

	@Override
	public BalanceProductResponse getSavingAccountBalanceByAccount(BalanceProductRequest balanceProductRequest) throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "INICIANDO SERVICIO");

		IProcedureResponse response = getBalanceSavingProductByAccount(balanceProductRequest);
		BalanceProductResponse balanceProductResponse = transformResponseToBalanceProductResponseDto(response);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "TERMINANDO SERVICIO:" + balanceProductResponse);
		return balanceProductResponse;
	}

	private IProcedureResponse getBalanceSavingProductByAccount(BalanceProductRequest balanceProductRequest) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando Consulta CORE COBIS");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Informacion recibida:" + balanceProductRequest);

		Context context = ContextManager.getContext();
		CobisSession session = (CobisSession) context.getSession();

		String CODE_TRN = "18380";

		IProcedureRequest request = new ProcedureRequestAS();
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, CODE_TRN);
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.setSpName("cob_ahorros..sp_tr04_cons_saldo");

		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, CODE_TRN);
		request.addInputParam("@t_online", ICTSTypes.SYBCHAR, "S");
		request.addInputParam("@i_comision", ICTSTypes.SYBMONEY, "0");
		request.addInputParam("@i_servicio", ICTSTypes.SYBINT2, balanceProductRequest.getChannelId());
		request.addInputParam("@i_solo_disponible", ICTSTypes.SYBCHAR, "N");

		request.addInputParam("@s_srv", ICTSTypes.SYBCHAR, context.getServer().getServer());
		request.addInputParam("@s_user", ICTSTypes.SYBCHAR, session.getUser());
		request.addInputParam("@s_term", ICTSTypes.SYBCHAR, session.getTerminal());
		request.addInputParam("@s_ofi", ICTSTypes.SYBINT2, session.getOffice());

		Product product = balanceProductRequest.getProduct();
		request.addInputParam("@i_prod", ICTSTypes.SYBINT2, product.getProductType().toString());
		request.addInputParam("@i_cta", ICTSTypes.SYBVARCHAR, product.getProductNumber());
		request.addInputParam("@i_mon", ICTSTypes.SYBINT2, product.getCurrency().getCurrencyId().toString());

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Data enviada a ejecutar:" + request.getProcedureRequestAsString());

		IProcedureResponse response = executeCoreBanking(request);

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Respuesta Devuelta del Core:" + response.getProcedureResponseAsString());
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecucion Terminada");
		return response;
	}

	private BalanceProductResponse transformResponseToBalanceProductResponseDto(IProcedureResponse response) {
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Iniciando método transformResponseToBalanceProductResponseDto: " + response.getProcedureResponseAsString());

		BalanceProductResponse balanceProductResponse = new BalanceProductResponse();
		Utils.transformIprocedureResponseToBaseResponse(balanceProductResponse, response);

		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + "Respuesta de método transformIprocedureResponseToBaseResponse: " + balanceProductResponse.getSuccess());
			logger.logDebug(CLASS_NAME + "Respuesta de método transformIprocedureResponseToBaseResponse: " + balanceProductResponse.getReturnCode());
		}

		if ((!response.hasError() && (response.getReturnCode() == 0)) || response.getReturnCode() == 40004) {
			BalanceProduct balanceProduct = new BalanceProduct();

			// Balance
			IResultSetBlock rsBalance = response.getResultSet(1);
			IResultSetRow[] rowsBalance = rsBalance.getData().getRowsAsArray();
			if (rowsBalance.length == 1) {
				IResultSetRowColumnData[] rows = rowsBalance[0].getColumnsAsArray();
				Product product = new Product();
				Client client = new Client();

				if (!Utils.isNullOrEmpty(rows[0].getValue()))
					if (rows[0].getValue() != null)
						product.setProductName(rows[0].getValue().toString());
				if (!Utils.isNullOrEmpty(rows[1].getValue()))
					if (rows[1].getValue() != null)
						balanceProduct.setDateLastMovent(rows[1].getValue().toString());
				if (!Utils.isNullOrEmpty(rows[2].getValue()))
					if (rows[2].getValue() != null)
						balanceProduct.setState(rows[2].getValue().toString());
				if (!Utils.isNullOrEmpty(rows[3].getValue()))
					if (rows[3].getValue() != null)
						balanceProduct.setAccountingBalance(new BigDecimal(rows[3].getValue().toString()));
				if (!Utils.isNullOrEmpty(rows[4].getValue()))
					if (rows[4].getValue() != null)
						balanceProduct.setInExchangeBalance(new BigDecimal(rows[4].getValue().toString()));
				if (!Utils.isNullOrEmpty(rows[5].getValue()))
					if (rows[5].getValue() != null)
						balanceProduct.setAvailableBalance(new BigDecimal(rows[5].getValue().toString()));
				if (!Utils.isNullOrEmpty(rows[6].getValue()))
					if (rows[6].getValue() != null)
						client.setIdentification(rows[6].getValue().toString());
				if (!Utils.isNullOrEmpty(rows[7].getValue()))
					if (rows[7].getValue() != null)
						balanceProduct.setBalance24H(new BigDecimal(rows[7].getValue().toString()));
				if (!Utils.isNullOrEmpty(rows[8].getValue()))
					if (rows[8].getValue() != null)
						balanceProduct.setBalance12H(new BigDecimal(rows[8].getValue().toString()));
				if (!Utils.isNullOrEmpty(rows[9].getValue()))
					if (rows[9].getValue() != null)
						balanceProduct.setRotateBalance(new BigDecimal(rows[9].getValue().toString()));
				if (!Utils.isNullOrEmpty(rows[10].getValue()))
					if (rows[10].getValue() != null)
						balanceProduct.setOpeningDate(rows[10].getValue().toString());
				if (!Utils.isNullOrEmpty(rows[11].getValue()))
					if (rows[11].getValue() != null)
						balanceProduct.setOverdraftBalance(new BigDecimal(rows[11].getValue().toString()));
				if (!Utils.isNullOrEmpty(rows[12].getValue()))
					if (rows[12].getValue() != null)
						balanceProduct.setBlockedNumber(Integer.parseInt(rows[12].getValue().toString()));
				if (!Utils.isNullOrEmpty(rows[13].getValue()))
					if (rows[13].getValue() != null)
						balanceProduct.setBlockedAmmount(new BigDecimal(rows[13].getValue().toString()));
				if (!Utils.isNullOrEmpty(rows[14].getValue()))
					if (rows[14].getValue() != null)
						balanceProduct.setDeliveryAddress(rows[14].getValue().toString());
				if (!Utils.isNullOrEmpty(rows[15].getValue()))
					if (rows[15].getValue() != null)
						balanceProduct.setCheckBalance(new BigDecimal(rows[15].getValue().toString()));
				if (!Utils.isNullOrEmpty(rows[16].getValue()))
					if (rows[16].getValue() != null)
						balanceProduct.setEmbargoedBalance(new BigDecimal(rows[16].getValue().toString()));

				balanceProduct.setClient(client);
				balanceProduct.setProduct(product);
			}

			balanceProductResponse.setBalanceProduct(balanceProduct);
		}

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Objeto devuelto:" + balanceProductResponse);
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Finalizada");
		return balanceProductResponse;
	}

	@Override
	public BalanceProductResponse getCheckingAccountBalanceByAccount(BalanceProductRequest balanceProductRequest) throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "INICIANDO SERVICIO");

		IProcedureResponse response = getBalanceChekingProductByAccount(balanceProductRequest);
		BalanceProductResponse balanceProductResponse = transformResponseToBalanceProductResponseDto(response);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "TERMINANDO SERVICIO:" + balanceProductResponse);
		return balanceProductResponse;
	}

	private IProcedureResponse getBalanceChekingProductByAccount(BalanceProductRequest balanceProductRequest) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando Consulta CORE COBIS");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Informacion recibida:" + balanceProductRequest);

		Context context = ContextManager.getContext();
		CobisSession session = (CobisSession) context.getSession();

		String CODE_TRN = "18302";

		IProcedureRequest request = new ProcedureRequestAS();
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, CODE_TRN);
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.setSpName("cob_cuentas..sp_tr03_cons_saldo");

		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, CODE_TRN);
		request.addInputParam("@t_online", ICTSTypes.SYBCHAR, "S");
		request.addInputParam("@i_comision", ICTSTypes.SYBMONEY, "0");
		request.addInputParam("@i_servicio", ICTSTypes.SYBINT2, balanceProductRequest.getChannelId());
		request.addInputParam("@i_solo_disponible", ICTSTypes.SYBCHAR, "N");

		request.addInputParam("@s_srv", ICTSTypes.SYBCHAR, context.getServer().getServer());
		request.addInputParam("@s_user", ICTSTypes.SYBCHAR, session.getUser());
		request.addInputParam("@s_term", ICTSTypes.SYBCHAR, session.getTerminal());
		request.addInputParam("@s_ofi", ICTSTypes.SYBINT2, session.getOffice());

		Product product = balanceProductRequest.getProduct();
		request.addInputParam("@i_prod", ICTSTypes.SYBINT2, product.getProductType().toString());
		request.addInputParam("@i_cta", ICTSTypes.SYBVARCHAR, product.getProductNumber());
		request.addInputParam("@i_mon", ICTSTypes.SYBINT2, product.getCurrency().getCurrencyId().toString());

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Data enviada a ejecutar:" + request.getProcedureRequestAsString());

		IProcedureResponse response = executeCoreBanking(request);

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Respuesta Devuelta del Core:" + response.getProcedureResponseAsString());
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecucion Terminada");
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase# executeJavaOrchestration(com.cobiscorp.cobis.cts.domains.
	 * IProcedureRequest, java.util.Map)
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase# processResponse(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
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
	 * @see com.cobiscorp.cobis.commons.components.ICOBISComponent#loadConfiguration( com.cobiscorp.cobis.commons.configuration.IConfigurationReader)
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}
}
