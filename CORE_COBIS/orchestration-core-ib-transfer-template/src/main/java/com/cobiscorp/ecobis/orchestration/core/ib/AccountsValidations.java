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
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.SignerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SignerResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ValidationAccountsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ValidationAccountsResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BalanceProduct;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Office;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Officer;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ProductBanking;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Signer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.utils.dtos.Utils;

@Component(name = "AccountsValidations", immediate = false)
@Service(value = { ICoreService.class })
@Properties(value = { @Property(name = "service.description", value = "AccountsValidations"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "AccountsValidations") })
public class AccountsValidations extends SPJavaOrchestrationBase implements ICoreService {
	private static final String CLASS_NAME = " >-----> ";
	protected static final String COBIS_CONTEXT = "COBIS";
	private static ILogger logger = LogFactory.getLogger(AccountsValidations.class);

	@Override
	public ValidationAccountsResponse executeValidationAccounts(ValidationAccountsRequest validationAccountsRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "INICIANDO SERVICIO");
			logger.logInfo(CLASS_NAME + "RESPUESTA CORE GENERADA");
		}

		ValidationAccountsResponse wValidationAccountsResponse = transformResponseValidationAccounts(
				executeValidationAccountsCobis(validationAccountsRequest),
				validationAccountsRequest.getOriginProduct());

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "TERMINANDO SERVICIO");
		return wValidationAccountsResponse;
	}

	private IProcedureResponse executeValidationAccountsCobis(ValidationAccountsRequest validationAccountsRequest) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando transferencia CORE COBIS");

		Context context = ContextManager.getContext();
		CobisSession session = (CobisSession) context.getSession();

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800050");
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");

		anOriginalRequest.setSpName("cobis..sp_resultados_bv");
		String t_trn = validationAccountsRequest.getCodeTransactionalIdentifier();

		if (logger.isDebugEnabled())
			logger.logDebug("t_trn a evaluar: " + t_trn);

		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1800050");
		anOriginalRequest.addInputParam("@s_ssn_host", ICTSTypes.SQLINT4,
				validationAccountsRequest.getSecuential().getSecuential());
		anOriginalRequest.addInputParam("@t_ejec", ICTSTypes.SQLVARCHAR, "R");
		anOriginalRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "R");
		anOriginalRequest.addInputParam("@i_ofi", ICTSTypes.SQLINT2, session.getOffice());
		anOriginalRequest.addInputParam("@i_fecha", ICTSTypes.SQLDATETIME, context.getProcessDate());
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
			logger.logDebug(CLASS_NAME + "Data enviada a ejecutar:" + anOriginalRequest.toString());
		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta del Core:" + response.getProcedureResponseAsString());

		if (((response.getReturnCode() == 0) && Utils.validateErrorCode(response, 0))
				|| Utils.validateErrorCode(response, 40004)) {
			Integer product = validationAccountsRequest.getOriginProduct().getProductType();
			if (logger.isDebugEnabled())
				logger.logDebug("Producto: " + product);

			if (product == 3)
				Utils.addResultSetDataAsParam(2, response, "H");
			if (product == 4)
				Utils.addResultSetDataAsParam(1, response, "H");
		}

		return response;
	}

	private ValidationAccountsResponse transformResponseValidationAccounts(IProcedureResponse response,
			Product product) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando data");
		ValidationAccountsResponse validationAccountsResponse = new ValidationAccountsResponse();
		Utils.transformIprocedureResponseToBaseResponse(validationAccountsResponse, response);

		BalanceProduct destinationLastBalanceProduct = new BalanceProduct();
		BalanceProduct destinationOldBalanceProduct = new BalanceProduct();
		BalanceProduct originBalanceProduct = new BalanceProduct();
		if (!response.hasError()) {
			Office office = new Office();
			ProductBanking productBanking = new ProductBanking();

			Office officeLast = new Office();
			ProductBanking productBankingLast = new ProductBanking();
			Product productLast = new Product();

			Office officeOld = new Office();
			ProductBanking productBankingOld = new ProductBanking();

			IResultSetBlock resulsetOriginBalance = response.getResultSet(1);
			IResultSetBlock resulsetAllBalance = response.getResultSet(2);

			if (logger.isInfoEnabled())
				logger.logInfo("resulsetOriginBalance" + resulsetOriginBalance);

			IResultSetRow[] rowsTemp = resulsetOriginBalance.getData().getRowsAsArray();
			if (rowsTemp.length == 1) {
				IResultSetRowColumnData[] rows = rowsTemp[0].getColumnsAsArray();

				if (rows[1].getValue() != null)
					originBalanceProduct.setAvailableBalance(new BigDecimal(rows[1].getValue().toString()));
				if (rows[2].getValue() != null)
					originBalanceProduct.setEquityBalance(new BigDecimal(rows[2].getValue().toString()));
				if (rows[3].getValue() != null)
					originBalanceProduct.setRotateBalance(new BigDecimal(rows[3].getValue().toString()));
				if (rows[4].getValue() != null)
					originBalanceProduct.setBalance12H(new BigDecimal(rows[4].getValue().toString()));
				if (rows[5].getValue() != null)
					originBalanceProduct.setBalance24H(new BigDecimal(rows[5].getValue().toString()));
				if (rows[6].getValue() != null)
					originBalanceProduct.setRemittancesBalance(new BigDecimal(rows[6].getValue().toString()));
				if (rows[7].getValue() != null)
					originBalanceProduct.setBlockedAmmount(new BigDecimal(rows[7].getValue().toString()));
				if (rows[8].getValue() != null)
					originBalanceProduct.setBlockedNumber(Integer.parseInt(rows[8].getValue().toString()));
				if (rows[9].getValue() != null)
					originBalanceProduct.setBlockedNumberAmmount(Integer.parseInt(rows[9].getValue().toString()));
				if (rows[10].getValue() != null)
					office.setId(Integer.parseInt(rows[10].getValue().toString()));
				if (rows[11].getValue() != null)
					productBanking.setId(Integer.parseInt(rows[11].getValue().toString()));
				if (rows[12].getValue() != null)
					originBalanceProduct.setState(rows[12].getValue().toString());
				if (rows[13].getValue() != null)
					originBalanceProduct.setSsnHost(Integer.parseInt(rows[13].getValue().toString()));
				if (rows[14].getValue() != null)
					originBalanceProduct.setSurplusAmmount(new BigDecimal(rows[14].getValue().toString()));
				if (rows[16].getValue() != null)
					product.setProductName(rows[16].getValue().toString());
				if (rows[15].getValue() != null)
					originBalanceProduct.setProcessDate(Utils.formatDate(rows[15].getValue().toString()));
				if (rows[17].getValue() != null)
					originBalanceProduct.setDateLastMovent(rows[17].getValue().toString());
				if (rows[18].getValue() != null) {
					if (logger.isInfoEnabled())
						logger.logInfo(rows[18].getValue());
				}

			}
			originBalanceProduct.setOfficeAccount(office);
			originBalanceProduct.setProductBanking(productBanking);
			originBalanceProduct.setProduct(product);

			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + originBalanceProduct);
			if (resulsetAllBalance != null) {

				IResultSetRow[] rowsTempLast = resulsetAllBalance.getData().getRowsAsArray();
				if (rowsTempLast.length == 1) {
					IResultSetRowColumnData[] rowsLast = rowsTempLast[0].getColumnsAsArray();

					destinationLastBalanceProduct
							.setAvailableBalance(new BigDecimal(rowsLast[1].getValue().toString()));
					if (rowsLast[1].getValue() != null)
						destinationLastBalanceProduct
								.setAvailableBalance(new BigDecimal(rowsLast[1].getValue().toString()));
					if (rowsLast[2].getValue() != null)
						destinationLastBalanceProduct
								.setEquityBalance(new BigDecimal(rowsLast[2].getValue().toString()));
					if (rowsLast[3].getValue() != null)
						destinationLastBalanceProduct
								.setRotateBalance(new BigDecimal(rowsLast[3].getValue().toString()));
					if (rowsLast[4].getValue() != null)
						destinationLastBalanceProduct.setBalance12H(new BigDecimal(rowsLast[4].getValue().toString()));
					if (rowsLast[5].getValue() != null)
						destinationLastBalanceProduct.setBalance24H(new BigDecimal(rowsLast[5].getValue().toString()));
					if (rowsLast[6].getValue() != null)
						destinationLastBalanceProduct
								.setRemittancesBalance(new BigDecimal(rowsLast[6].getValue().toString()));
					if (rowsLast[7].getValue() != null)
						destinationLastBalanceProduct
								.setBlockedAmmount(new BigDecimal(rowsLast[7].getValue().toString()));
					if (rowsLast[8].getValue() != null)
						destinationLastBalanceProduct
								.setBlockedNumber(Integer.parseInt(rowsLast[8].getValue().toString()));
					if (rowsLast[9].getValue() != null)
						destinationLastBalanceProduct
								.setBlockedNumberAmmount(Integer.parseInt(rowsLast[9].getValue().toString()));
					if (rowsLast[10].getValue() != null)
						officeLast.setId(Integer.parseInt(rowsLast[10].getValue().toString()));
					if (rowsLast[11].getValue() != null)
						productBankingLast.setId(Integer.parseInt(rowsLast[11].getValue().toString()));
					if (rowsLast[12].getValue() != null)
						destinationLastBalanceProduct.setState(rowsLast[12].getValue().toString());
					if (rowsLast[13].getValue() != null)
						destinationLastBalanceProduct.setSsnHost(Integer.parseInt(rowsLast[13].getValue().toString()));
					if (rowsLast[14].getValue() != null)
						destinationLastBalanceProduct
								.setSurplusAmmount(new BigDecimal(rowsLast[14].getValue().toString()));
					if (rowsLast[15].getValue() != null)
						destinationLastBalanceProduct.setIdClosed(Integer.parseInt(rowsLast[15].getValue().toString()));
					if (rowsLast[17].getValue() != null)
						destinationLastBalanceProduct
								.setProcessDate(Utils.formatDate(rowsLast[17].getValue().toString()));
					if (rowsLast[18].getValue() != null)
						destinationOldBalanceProduct
								.setAvailableBalance(new BigDecimal(rowsLast[18].getValue().toString()));
					if (rowsLast[19].getValue() != null)
						destinationOldBalanceProduct
								.setEquityBalance(new BigDecimal(rowsLast[19].getValue().toString()));
					if (rowsLast[20].getValue() != null)
						destinationOldBalanceProduct
								.setRotateBalance(new BigDecimal(rowsLast[20].getValue().toString()));
					if (rowsLast[21].getValue() != null)
						destinationOldBalanceProduct.setBalance12H(new BigDecimal(rowsLast[21].getValue().toString()));
					if (rowsLast[22].getValue() != null)
						destinationOldBalanceProduct.setBalance24H(new BigDecimal(rowsLast[22].getValue().toString()));
					if (rowsLast[23].getValue() != null)
						destinationOldBalanceProduct
								.setRemittancesBalance(new BigDecimal(rowsLast[23].getValue().toString()));
					if (rowsLast[24].getValue() != null)
						destinationOldBalanceProduct
								.setBlockedAmmount(new BigDecimal(rowsLast[24].getValue().toString()));
					if (rowsLast[25].getValue() != null)
						destinationOldBalanceProduct
								.setBlockedNumber(Integer.parseInt(rowsLast[25].getValue().toString()));
					if (rowsLast[26].getValue() != null)
						destinationOldBalanceProduct
								.setBlockedNumberAmmount(Integer.parseInt(rowsLast[26].getValue().toString()));
					if (rowsLast[27].getValue() != null)
						officeOld.setId(Integer.parseInt(rowsLast[27].getValue().toString()));
					if (rowsLast[28].getValue() != null)
						productBankingOld.setId(Integer.parseInt(rowsLast[28].getValue().toString()));
					if (rowsLast[29].getValue() != null)
						destinationOldBalanceProduct.setState(rowsLast[29].getValue().toString());
					if (rowsLast[30].getValue() != null)
						destinationOldBalanceProduct
								.setSurplusAmmount(new BigDecimal(rowsLast[30].getValue().toString()));
					if (rowsLast[31].getValue() != null)
						productLast.setProductName(rowsLast[31].getValue().toString());
					if (rowsLast[32].getValue() != null)
						destinationLastBalanceProduct.setDateLastMovent(rowsLast[32].getValue().toString());
				}
				destinationLastBalanceProduct.setOfficeAccount(officeLast);
				destinationLastBalanceProduct.setProductBanking(productBankingLast);
				destinationLastBalanceProduct.setProduct(productLast);
				destinationOldBalanceProduct.setOfficeAccount(officeOld);
				destinationOldBalanceProduct.setProductBanking(productBankingOld);
			}
		}
		validationAccountsResponse.setDestinationLastBalanceProduct(destinationLastBalanceProduct);
		validationAccountsResponse.setDestinationOldBalanceProduct(destinationOldBalanceProduct);
		validationAccountsResponse.setOriginBalanceProduct(originBalanceProduct);

		return validationAccountsResponse;
	}

	@Override
	public OfficerByAccountResponse getOfficerByAccount(OfficerByAccountRequest request)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "INICIANDO SERVICIO");
			logger.logInfo(CLASS_NAME + "RESPUESTA COBIS GENERADA");
		}

		IProcedureResponse response = executeCoreBanking(transformOfficerByAccountRequest(request));
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Respuesta Corebanking:" + response.getProcedureResponseAsString());

		OfficerByAccountResponse procedureResponse = transformOfficerByAccountResponse(response);

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Respuesta Devuelta:" + procedureResponse);
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "TERMINANDO SERVICIO");
		return procedureResponse;
	}

	private OfficerByAccountResponse transformOfficerByAccountResponse(IProcedureResponse procedureResponse) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Iniciando Transformacion Response");

		OfficerByAccountResponse response = new OfficerByAccountResponse();
		Utils.transformIprocedureResponseToBaseResponse(response, procedureResponse);

		Officer oficer = new Officer();
		if (procedureResponse.readValueParam("@o_ofi_cta") != null)
			oficer.setAcountEmailAdress(procedureResponse.readValueParam("@o_ofi_cta").toString());
		if (procedureResponse.readValueParam("@o_ofi_cli") != null)
			oficer.setOfficerEmailAdress(procedureResponse.readValueParam("@o_ofi_cli").toString());

		response.setOfficer(oficer);
		return response;
	}

	private IProcedureRequest transformOfficerByAccountRequest(OfficerByAccountRequest request) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Iniciando Transformacion Request");

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800196");
		anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		anOriginalRequest.setSpName("cobis..sp_bv_buscar_oficiales");
		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1800196");

		Product product = request.getProduct();

		if (product.getProductNumber() != null)
			anOriginalRequest.addInputParam("@i_cuenta", ICTSTypes.SQLVARCHAR, product.getProductNumber());
		if (product.getProductType() != null)
			anOriginalRequest.addInputParam("@i_producto", ICTSTypes.SQLINT2, product.getProductType().toString());

		anOriginalRequest.addOutputParam("@o_ofi_cta", ICTSTypes.SYBVARCHAR,
				"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		anOriginalRequest.addOutputParam("@o_ofi_cli", ICTSTypes.SYBVARCHAR,
				"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

		return anOriginalRequest;
	}

	@Override
	public SignerResponse getSignatureCondition(SignerRequest signerRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "INICIANDO SERVICIO: getSignatureCondition");
		}

		IProcedureResponse response = executeCoreBanking(transformSignerRequest(signerRequest));
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Respuesta Corebanking:" + response.getProcedureResponseAsString());

		SignerResponse signerResponse = transformSignerResponse(response);

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Respuesta Devuelta:" + signerResponse);
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "TERMINANDO SERVICIO");
		return signerResponse;
	}

	private IProcedureRequest transformSignerRequest(SignerRequest signerRequest) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Iniciando Transformacion Request");

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800126");
		anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		anOriginalRequest.setSpName("firmas..sp_consulta_firmantes_bv");

		anOriginalRequest.addInputParam("@s_servicio", ICTSTypes.SYBINT1, signerRequest.getChannelId());
		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1800126");
		if ("1".equals(signerRequest.getChannelId())) {
			anOriginalRequest.addInputParam("@i_cuenta", ICTSTypes.SYBVARCHAR,
					signerRequest.getOriginProduct().getProductNumber());
			anOriginalRequest.addInputParam("@i_producto", ICTSTypes.SYBVARCHAR,
					signerRequest.getOriginProduct().getProductNemonic());
		}
		anOriginalRequest.addInputParam("@i_ente", ICTSTypes.SYBINTN, signerRequest.getUser().getId());

		if (signerRequest.getAmmount() != null)
			anOriginalRequest.addInputParam("@i_monto", ICTSTypes.SQLMONEY, signerRequest.getAmmount().toString());
		anOriginalRequest.addOutputParam("@o_condiciones_firmantes", ICTSTypes.SYBVARCHAR,
				"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		return anOriginalRequest;
	}

	private SignerResponse transformSignerResponse(IProcedureResponse procedureResponse) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Iniciando Transformacion Response");

		SignerResponse response = new SignerResponse();

		Utils.transformIprocedureResponseToBaseResponse(response, procedureResponse);

		Signer signer = new Signer();
		if (procedureResponse.readValueParam("@o_condiciones_firmantes") != null)
			signer.setCondition(procedureResponse.readValueParam("@o_condiciones_firmantes").toString());

		response.setSigner(signer);
		return response;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
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
