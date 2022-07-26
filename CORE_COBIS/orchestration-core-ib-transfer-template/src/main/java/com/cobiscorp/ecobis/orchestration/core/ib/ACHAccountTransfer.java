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
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.ecobis.ib.application.dtos.ACHTransferRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ACHTransferResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BalanceProduct;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Office;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceACHTransfer;

@Component(name = "ACHAccountTransfer", immediate = false)
@Service(value = { ICoreServiceACHTransfer.class })
@Properties(value = { @Property(name = "service.description", value = "ACHAccountTransfer"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ACHAccountTransfer") })

public class ACHAccountTransfer extends SPJavaOrchestrationBase implements ICoreServiceACHTransfer {
	private static ILogger logger = LogFactory.getLogger(ACHAccountTransfer.class);
	private static final String CLASS_NAME = " >-----> ";
	protected static final String COBIS_CONTEXT = "COBIS";

	@Override
	public ACHTransferResponse executeACHAccountTransfer(ACHTransferRequest ACHAccountTransfer)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "INICIANDO SERVICIO");
			logger.logInfo(CLASS_NAME + "RESPUESTA CORE GENERADA");
		}

		ACHTransferResponse wACHAccountTransferResponse = transformResponse(
				executeACHAccountTransferCobis(ACHAccountTransfer));

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "TERMINANDO SERVICIO");
		return wACHAccountTransferResponse;
	}

	private IProcedureResponse executeACHAccountTransferCobis(ACHTransferRequest request) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando transferencia ACH CORE COBIS");
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18844");
		if (!request.getOperation().equals("S")) {
			anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_SSN, ICOBISTS.HEADER_NUMBER_TYPE,
					request.getReferenceNumber());
			anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH, ICOBISTS.HEADER_NUMBER_TYPE,
					request.getReferenceNumberBranch());
		}

		anOriginalRequest.setSpName("cob_cuentas..sp_tr03_transferencia_ach");
		anOriginalRequest.addInputParam("@t_online", ICTSTypes.SYBCHAR, "S");
		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18844");
		anOriginalRequest.addInputParam("@t_ejec", ICTSTypes.SYBVARCHAR, "R");
		if (String.valueOf(request.getOperation()) != null) {
			anOriginalRequest.addInputParam("@t_corr", ICTSTypes.SQLVARCHAR, request.getOperation());
			if (request.getOperation().equals("S")) {
				anOriginalRequest.addInputParam("@s_ssn", ICTSTypes.SYBINT4, request.getReferenceNumber());
				anOriginalRequest.addInputParam("@t_ssn_corr", ICTSTypes.SYBINT4, request.getReferenceNumberBranch());
				anOriginalRequest.addInputParam("@i_rev_trx", ICTSTypes.SQLVARCHAR,
						request.getReverseTransaction() != null ? request.getReverseTransaction() : "N");
				anOriginalRequest.addInputParam("@i_rev_imp_trx", ICTSTypes.SQLVARCHAR,
						request.getReverseTaxTransaction() != null ? request.getReverseTaxTransaction() : "N");
				anOriginalRequest.addInputParam("@i_rev_com", ICTSTypes.SQLVARCHAR,
						request.getReverseCommission() != null ? request.getReverseCommission() : "N");
			} else
				anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		} else
			anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");

		anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SYBINT4, String.valueOf(request.getOfficeCode()));
		anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, request.getUserBv());
		anOriginalRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, request.getTerminal());

		if (request.getRole() != 0)
			anOriginalRequest.addInputParam("@t_filial", ICTSTypes.SQLINT2, String.valueOf(request.getRole()));

        anOriginalRequest.addInputParam("@i_causa_com", ICTSTypes.SQLVARCHAR, request.getCauseComi());
		anOriginalRequest.addInputParam("@i_causa_org", ICTSTypes.SQLVARCHAR, request.getCause());		
		anOriginalRequest.addInputParam("@i_mon_com", ICTSTypes.SQLINT2, request.getComissionCurrency());

		if (request.getOriginProduct().getCurrency().getCurrencyId() != null)
			anOriginalRequest.addInputParam("@i_mon", ICTSTypes.SQLINT2,
					request.getOriginProduct().getCurrency().getCurrencyId().toString());
		if (request.getOriginProduct().getProductType() != null)
			anOriginalRequest.addInputParam("@i_prod", ICTSTypes.SQLINT2,
					request.getOriginProduct().getProductType().toString());
		if (request.getOriginProduct().getProductNumber() != null)
			anOriginalRequest.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR,
					request.getOriginProduct().getProductNumber());

		if (request.getDestinationProduct().getProductType() != null)
			anOriginalRequest.addInputParam("@i_prod_des", ICTSTypes.SQLINT2,
					request.getDestinationProduct().getProductType().toString());
		if (request.getDestinationProduct().getProductNumber() != null)
			anOriginalRequest.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR,
					request.getDestinationProduct().getProductNumber());
		if (request.getAmmount() != null)
			anOriginalRequest.addInputParam("@i_val", ICTSTypes.SQLMONEY, request.getAmmount().toString());

		if (request.getCommisionAmmount() != null)
			anOriginalRequest.addInputParam("@i_comision", ICTSTypes.SQLMONEY,
					request.getCommisionAmmount().toString());
		if (request.getDestinationProduct().getCurrency().getCurrencyId() != null)
			anOriginalRequest.addInputParam("@i_mon_des", ICTSTypes.SQLINT2, String.valueOf(request.getCurrencyId()));
		// request.getDestinationProduct().getCurrency().getCurrencyId().toString());

		if (request.getOriginatorFunds() != null)
			anOriginalRequest.addInputParam("@i_origen_fondos", ICTSTypes.SQLVARCHAR, request.getOriginatorFunds());
		if (request.getReceiverFunds() != null)
			anOriginalRequest.addInputParam("@i_dest_fondos", ICTSTypes.SQLVARCHAR, request.getReceiverFunds());
		if (request.getDescriptionTransfer() != null)
			anOriginalRequest.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, request.getDescriptionTransfer());
		if (request.getTransitRoute() != null)
			anOriginalRequest.addInputParam("@i_ruta_trans", ICTSTypes.SQLVARCHAR, request.getTransitRoute());
		if (request.getDestinationBankName() != null)
			anOriginalRequest.addInputParam("@i_nom_banco_des", ICTSTypes.SQLVARCHAR, request.getDestinationBankName());
		if (request.getBeneficiaryName() != null)
			anOriginalRequest.addInputParam("@i_nombre_benef", ICTSTypes.SQLVARCHAR, request.getBeneficiaryName());

		if (request.getDocumentIdBeneficiary() != null)
			anOriginalRequest.addInputParam("@i_doc_benef", ICTSTypes.SQLVARCHAR, request.getDocumentIdBeneficiary());
		if (request.getDestinationBankPhone() != null)
			anOriginalRequest.addInputParam("@i_telefono_benef", ICTSTypes.SQLVARCHAR,
					request.getDestinationBankPhone());
		if (request.getChargeAccount() != null)
			anOriginalRequest.addInputParam("@i_cta_cobro", ICTSTypes.SQLVARCHAR, request.getChargeAccount());
		if (String.valueOf(request.getChargeProduct()) != null)
			anOriginalRequest.addInputParam("@i_prod_cobro", ICTSTypes.SQLINT1,
					String.valueOf(request.getChargeProduct()));
		if (String.valueOf(request.getClientCoreCode()) != null)
			anOriginalRequest.addInputParam("@i_ente", ICTSTypes.SQLINT4, String.valueOf(request.getClientCoreCode()));

		Integer channelId = 0;
		if (request.getChannelId() != null)
			channelId = Integer.parseInt(request.getChannelId());

		switch (channelId) {
		case 1:
		case 6:
		case 7:
			anOriginalRequest.addInputParam("@i_servicio", ICTSTypes.SYBINT4, "1");
			break;
		case 8:
			anOriginalRequest.addInputParam("@i_servicio", ICTSTypes.SYBINT4, "8");
			break;
		default:
			anOriginalRequest.addInputParam("@i_servicio", ICTSTypes.SYBINT4, channelId.toString());
			break;
		}

		anOriginalRequest.addOutputParam("@o_cotizacion", ICTSTypes.SYBFLT8i, "0.000000000000");
		anOriginalRequest.addOutputParam("@o_val_convert", ICTSTypes.SYBMONEY, "0.000000000000");
		anOriginalRequest.addOutputParam("@o_accountCurrencyAmount", ICTSTypes.SYBMONEY, "0.000000000000");
		anOriginalRequest.addOutputParam("@o_accountCurrencyFee", ICTSTypes.SYBMONEY, "0.000000000000");
		anOriginalRequest.addOutputParam("@o_buyQuote", ICTSTypes.SYBMONEY, "0.000000000000");
		anOriginalRequest.addOutputParam("@o_sellQuote", ICTSTypes.SYBMONEY, "0.000000000000");
		anOriginalRequest.addOutputParam("@o_feeBuyQuote", ICTSTypes.SYBMONEY, "0.000000000000");
		anOriginalRequest.addOutputParam("@o_feeSellQuote", ICTSTypes.SYBMONEY, "0.000000000000");
		anOriginalRequest.addOutputParam("@o_taxName1", ICTSTypes.SYBVARCHAR, "");
		anOriginalRequest.addOutputParam("@o_taxValue1", ICTSTypes.SYBMONEY, "0.000000000000");
		anOriginalRequest.addOutputParam("@o_taxName2", ICTSTypes.SYBVARCHAR, "");
		anOriginalRequest.addOutputParam("@o_taxValue2", ICTSTypes.SYBMONEY, "0.000000000000");

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Data enviada a ejecutar:" + anOriginalRequest.toString());
		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta del Core:" + response.getProcedureResponseAsString());

		return response;
	}

	private ACHTransferResponse transformResponse(IProcedureResponse response) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando Transformacion de Respuesta");

		/* DTO */
		ACHTransferResponse responseTransfer = new ACHTransferResponse();
		BalanceProduct aBalanceProduct = new BalanceProduct();

		Product product = new Product();
		Office office = new Office();

		if (response == null) {
			if (logger.isInfoEnabled())
				logger.logInfo("ACHAccountTransfer --> Response null");
			return null;
		}

		if (response.getReturnCode() != 0 && response.getReturnCode() != 40002 && response.getReturnCode() != 40003
				&& response.getReturnCode() != 40004) {
			if (logger.isInfoEnabled())
				logger.logInfo("InternationalTransfer --> Response ErrorCODE:" + response.getReturnCode());
			responseTransfer.setMessages(Utils.returnArrayMessage(response));
		} else {

			if (response.getResultSetListSize() > 0) {
				int maxRS = response.getResultSetListSize();

				IResultSetRow[] rows = response.getResultSet(maxRS).getData().getRowsAsArray();
				IResultSetRowColumnData[] columns = rows[0].getColumnsAsArray();

				office.setId(columns[10].getValue() == null ? 0 : Integer.parseInt(columns[10].getValue()));
				product.setProductType(columns[11].getValue() == null ? 0 : Integer.parseInt(columns[11].getValue()));

				// Producto Origen
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
				aBalanceProduct
						.setSsnHost(columns[13].getValue() == null ? 0 : Integer.parseInt(columns[13].getValue()));
				
				//Se comenta ya que viene un dato tipo fecha
				//aBalanceProduct.setSurplusAmmount(columns[14].getValue() == null ? new BigDecimal(0) : new BigDecimal(columns[14].getValue()));
				aBalanceProduct.setSurplusAmmount(new BigDecimal(0));

				// ACHTransferResponse
				responseTransfer.setBalanceProduct(aBalanceProduct);
				responseTransfer.setDateHost(columns[15].getValue() == null ? "" : columns[15].getValue());
				responseTransfer.setName(columns[16].getValue() == null ? "" : columns[16].getValue());
				responseTransfer.setDateLastMovement(columns[17].getValue() == null ? "" : columns[17].getValue());
				responseTransfer.setAccountStatus(columns[18].getValue() == null ? "" : columns[18].getValue());
			}
		}
		if (response.getReturnCode() != 0 && response.getReturnCode() != 40002)
			responseTransfer.setMessages(Utils.returnArrayMessage(response));

		responseTransfer.setReferenceNumber(response.readValueParam("@o_referencia"));
		responseTransfer.setAccountCurrencyAmount(response.readValueParam("@o_accountCurrencyAmount") != null
				? Double.parseDouble(response.readValueParam("@o_accountCurrencyAmount")) : null);
		responseTransfer.setAccountCurrencyFee(response.readValueParam("@o_accountCurrencyFee") != null
				? Double.parseDouble(response.readValueParam("@o_accountCurrencyFee")) : null);
		responseTransfer.setBuyQuote(response.readValueParam("@o_buyQuote") != null
				? Double.parseDouble(response.readValueParam("@o_buyQuote")) : null);
		responseTransfer.setSellQuote(response.readValueParam("@o_sellQuote") != null
				? Double.parseDouble(response.readValueParam("@o_sellQuote")) : null);
		responseTransfer.setFeeBuyQuote(response.readValueParam("@o_feeBuyQuote") != null
				? Double.parseDouble(response.readValueParam("@o_feeBuyQuote")) : null);
		responseTransfer.setFeeSellQuote(response.readValueParam("@o_feeSellQuote") != null
				? Double.parseDouble(response.readValueParam("@o_feeSellQuote")) : null);
		responseTransfer.setTaxName1(response.readValueParam("@o_taxName1"));
		responseTransfer.setTaxValue1(response.readValueParam("@o_taxValue1") != null
				? Double.parseDouble(response.readValueParam("@o_taxValue1")) : null);
		responseTransfer.setTaxName2(response.readValueParam("@o_taxName2"));
		responseTransfer.setTaxValue2(response.readValueParam("@o_taxValue2") != null
				? Double.parseDouble(response.readValueParam("@o_taxValue2")) : null);
		responseTransfer.setSuccess(response.getReturnCode() == 0 ? true : false);
		responseTransfer.setReturnCode(response.getReturnCode());

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta" + responseTransfer);
		return responseTransfer;
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

	@Override
	public ACHTransferResponse executeACHPayLoanTransfer(ACHTransferRequest aACHTransferRequest)
			throws CTSServiceException, CTSInfrastructureException {
		// TODO Auto-generated method stub
		return null;
	}
}
