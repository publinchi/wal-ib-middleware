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
import com.cobiscorp.ecobis.ib.application.dtos.InternationalTransferRequest;
import com.cobiscorp.ecobis.ib.application.dtos.InternationalTransferResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BalanceProduct;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Office;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceInternationalAccountTransfers;

@Component(name = "InternationalAccountTransfer", immediate = false)
@Service(value = { ICoreServiceInternationalAccountTransfers.class })
@Properties(value = { @Property(name = "service.description", value = "InternationalAccountTransfer"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "InternationalAccountTransfer") })

public class InternationalAccountTransfer extends SPJavaOrchestrationBase
		implements ICoreServiceInternationalAccountTransfers {

	private static ILogger logger = LogFactory.getLogger(InternationalAccountTransfer.class);
	private static final String CLASS_NAME = " >-----> ";
	protected static final String COBIS_CONTEXT = "COBIS";

	@Override
	public InternationalTransferResponse executeInternationalAccountTransfer(
			InternationalTransferRequest internationalAccountTransfer)
			throws CTSServiceException, CTSInfrastructureException {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "INICIANDO SERVICIO");
			logger.logInfo(CLASS_NAME + "RESPUESTA CORE GENERADA");
		}

		InternationalTransferResponse wInternationalAccountTransferResponse = transformResponse(
				executeInternationalAccountTransferCobis(internationalAccountTransfer));

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "TERMINANDO SERVICIO");
		return wInternationalAccountTransferResponse;

	}

	private IProcedureResponse executeInternationalAccountTransferCobis(InternationalTransferRequest request) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando transferencia internacional CORE COBIS");
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1875010");
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_SSN, ICOBISTS.HEADER_NUMBER_TYPE,
				request.getReferenceNumber());
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH, ICOBISTS.HEADER_NUMBER_TYPE,
				request.getReferenceNumberBranch());
		anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		anOriginalRequest.setSpName("cobis..sp_bv_tr09_transferencia_int");
		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "1875010");
		anOriginalRequest.addInputParam("@t_ejec", ICTSTypes.SQLVARCHAR, "R");
		anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SYBINT4, String.valueOf(request.getOfficeCode()));
		anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, request.getUserBv());
		anOriginalRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, request.getTerminal());
		anOriginalRequest.addInputParam("@s_rol", ICTSTypes.SQLINT2, String.valueOf(request.getRole()));
		anOriginalRequest.addInputParam("@s_date", ICTSTypes.SQLDATETIME, request.getDate());
		anOriginalRequest.addInputParam("@s_ssn_branch", ICTSTypes.SQLINT4, request.getReferenceNumberBranch());
		anOriginalRequest.addInputParam("@s_ssn", ICTSTypes.SQLINT4, request.getReferenceNumber());
		anOriginalRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "I");
		anOriginalRequest.addInputParam("@i_moncta", ICTSTypes.SYBINT1,
				request.getOriginProduct().getCurrency().getCurrencyId().toString());
		anOriginalRequest.addInputParam("@i_mon", ICTSTypes.SYBINT1,
				request.getDestinationProduct().getCurrency().getCurrencyId().toString());
		anOriginalRequest.addInputParam("@i_prod", ICTSTypes.SQLINT2,
				request.getOriginProduct().getProductType().toString());
		anOriginalRequest.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, request.getOriginProduct().getProductNumber());

		if (request.getOriginProduct().getProductType() == 3) {
			anOriginalRequest.addInputParam("@i_tcta", ICTSTypes.SQLVARCHAR, "CTE");

		} else if (request.getOriginProduct().getProductType() == 4) {
			anOriginalRequest.addInputParam("@i_tcta", ICTSTypes.SQLVARCHAR, "AHO");
		}
		if (request.getUser().getId() != null)
			anOriginalRequest.addInputParam("@i_ente", ICTSTypes.SQLINT4, request.getUser().getId());
		if (request.getOriginatorFunds() != null)
			anOriginalRequest.addInputParam("@i_origen_fondos", ICTSTypes.SQLVARCHAR,
					request.getOriginatorFunds().toString());
		if (request.getReceiverFunds() != null)
			anOriginalRequest.addInputParam("@i_dest_fondos", ICTSTypes.SQLVARCHAR,
					request.getReceiverFunds().toString());

		if (request.getAmmount() != null)
			anOriginalRequest.addInputParam("@i_val", ICTSTypes.SQLMONEY, request.getAmmount().toString());

		// Beneficiary Info
		anOriginalRequest.addInputParam("@i_tipo_persona", ICTSTypes.SQLVARCHAR, request.getBeneficiaryIDType());
		anOriginalRequest.addInputParam("@i_dniruc", ICTSTypes.SQLVARCHAR, request.getBeneficiaryIDNumber());
		anOriginalRequest.addInputParam("@i_benefi", ICTSTypes.SQLVARCHAR, request.getBeneficiaryName());
		anOriginalRequest.addInputParam("@i_papellido", ICTSTypes.SQLVARCHAR, request.getBeneficiaryFirstLastName());
		anOriginalRequest.addInputParam("@i_sapellido", ICTSTypes.SQLVARCHAR, request.getBeneficiarySecondLastName());
		anOriginalRequest.addInputParam("@i_razon_social", ICTSTypes.SQLVARCHAR, request.getBeneficiaryBusinessName());
		anOriginalRequest.addInputParam("@i_paiben", ICTSTypes.SQLINT2, request.getBeneficiaryCountryCode().toString());
		anOriginalRequest.addInputParam("@i_dirben", ICTSTypes.SQLVARCHAR, request.getBeneficiaryAddress());
		anOriginalRequest.addInputParam("@i_ref_opc", ICTSTypes.SQLVARCHAR,
				request.getDestinationProduct().getProductNumber());

		// Beneficiary Bank Info
		anOriginalRequest.addInputParam("@i_bcoben", ICTSTypes.SQLINT2, request.getBeneficiaryBankCode());
		anOriginalRequest.addInputParam("@i_ofiben", ICTSTypes.SQLINT2,
				request.getBeneficiaryBankOfficeCode().toString());
		anOriginalRequest.addInputParam("@i_nomben", ICTSTypes.SQLVARCHAR, request.getBeneficiaryBankName());
		anOriginalRequest.addInputParam("@i_swtben", ICTSTypes.SQLVARCHAR, request.getBeneficiaryBankSwiftAbaCode());
		anOriginalRequest.addInputParam("@i_tdirben", ICTSTypes.SQLVARCHAR, request.getBeneficiaryBankAddressType());

		// Intermediary Bank Info
		anOriginalRequest.addInputParam("@i_bcoint", ICTSTypes.SQLINT2, request.getIntermediaryBankCode().toString());
		anOriginalRequest.addInputParam("@i_ofiint", ICTSTypes.SQLINT2,
				request.getIntermediaryBankOfficeCode().toString());
		anOriginalRequest.addInputParam("@i_swtint", ICTSTypes.SQLVARCHAR, request.getIntermediaryBankSwiftAbaCode());
		anOriginalRequest.addInputParam("@i_tdirint", ICTSTypes.SQLVARCHAR, request.getIntermediaryBankAddressType());

		anOriginalRequest.addInputParam("@i_observacion", ICTSTypes.SQLVARCHAR, request.getDescriptionTransfer());

		anOriginalRequest.addOutputParam("@o_costo", ICTSTypes.SQLVARCHAR,
				"                                                                ");
		anOriginalRequest.addOutputParam("@o_import_tot", ICTSTypes.SQLVARCHAR,
				"                                                                ");
		anOriginalRequest.addOutputParam("@o_pais_benef", ICTSTypes.SQLVARCHAR,
				"                                                                ");
		anOriginalRequest.addOutputParam("@o_pais_int", ICTSTypes.SQLVARCHAR,
				"                                                                ");
		anOriginalRequest.addOutputParam("@o_dir_int", ICTSTypes.SQLVARCHAR,
				"                                                                ");
		anOriginalRequest.addOutputParam("@o_dir_bco_benef", ICTSTypes.SQLVARCHAR,
				"                                                                ");
		anOriginalRequest.addOutputParam("@o_bco_nom_int", ICTSTypes.SQLVARCHAR,
				"                                                                ");
		anOriginalRequest.addOutputParam("@o_num_transferencia", ICTSTypes.SQLVARCHAR,
				"                                                                ");

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Data enviada a ejecutar:" + anOriginalRequest.toString());
		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (response.getReturnCode() == 0) {
			Utils.addResultSetDataAsParam(1, response, "H");
		}

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta del Core:" + response.getProcedureResponseAsString());

		return response;
	}

	private InternationalTransferResponse transformResponse(IProcedureResponse response) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando Transformacion de Respuesta");
		InternationalTransferResponse responseTransfer = new InternationalTransferResponse();
		IResultSetRow[] rows = null;

		BalanceProduct aBalanceProduct = new BalanceProduct();
		Product product = new Product();
		Office office = new Office();

		if (response == null) {
			if (logger.isInfoEnabled())
				logger.logInfo("InternationalTransfer --> Response null");
			return null;
		}

		if (response.getReturnCode() != 0 && response.getReturnCode() != 40002 && response.getReturnCode() != 40003
				&& response.getReturnCode() != 40004) {
			if (logger.isInfoEnabled())
				logger.logInfo("InternationalTransfer --> Response ErrorCODE:" + response.getReturnCode());
			responseTransfer.setMessages(Utils.returnArrayMessage(response));
		} else {
			if (response.getResultSetListSize() > 0) {
				if (response.getReturnCode() == 0) {
					rows = response.getResultSet(8).getData().getRowsAsArray();
				} else {
					if (response.getReturnCode() == 40004 || response.getReturnCode() == 40003) {
						rows = response.getResultSet(1).getData().getRowsAsArray();
					}
				}

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
				aBalanceProduct
						.setSsnHost(columns[13].getValue() == null ? 0 : Integer.parseInt(columns[13].getValue()));
				aBalanceProduct.setSurplusAmmount(
						columns[14].getValue() == null ? new BigDecimal(0) : new BigDecimal(columns[14].getValue()));

				responseTransfer.setBalanceProduct(aBalanceProduct);
				responseTransfer.setDateHost(columns[15].getValue() == null ? "" : columns[15].getValue());
				responseTransfer.setName(columns[16].getValue() == null ? "" : columns[16].getValue());
				responseTransfer.setDateLastMovement(columns[17].getValue() == null ? "" : columns[17].getValue());
				responseTransfer.setAccountStatus(columns[18].getValue() == null ? "" : columns[18].getValue());

				// Parámetros de salida
				responseTransfer.setTransactionFee(response.readValueParam("@o_costo"));
				responseTransfer.setTransactionTotalAmount(response.readValueParam("@o_import_tot"));
				responseTransfer.setBeneficiaryCountryName(response.readValueParam("@o_pais_benef"));
				responseTransfer.setIntermediaryBankCountryName(response.readValueParam("@o_pais_int"));
				responseTransfer.setIntermediaryBankAddress(response.readValueParam("@o_dir_int"));
				responseTransfer.setBeneficiaryBankAddress(response.readValueParam("@o_dir_bco_benef"));
				responseTransfer.setIntermediaryBankName(response.readValueParam("@o_bco_nom_int"));
			}
		}

		responseTransfer.setReferenceNumber(response.readValueParam("@o_num_transferencia"));
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
}
