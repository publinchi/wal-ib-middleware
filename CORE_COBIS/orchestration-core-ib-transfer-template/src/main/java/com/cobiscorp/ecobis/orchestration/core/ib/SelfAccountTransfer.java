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
import com.cobiscorp.cobisv.commons.context.CobisSession;
import com.cobiscorp.cobisv.commons.context.Context;
import com.cobiscorp.cobisv.commons.context.ContextManager;
import com.cobiscorp.ecobis.ib.application.dtos.SelfAccountTransferRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SelfAccountTransferResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BalanceProduct;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Office;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSelfAccountTransfers;

@Component(name = "SelfAccountTransfer", immediate = false)
@Service(value = { ICoreServiceSelfAccountTransfers.class })
@Properties(value = { @Property(name = "service.description", value = "SelfAccountTransfer"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "SelfAccountTransfer") })
public class SelfAccountTransfer extends SPJavaOrchestrationBase implements ICoreServiceSelfAccountTransfers {
	private static ILogger logger = LogFactory.getLogger(SelfAccountTransfer.class);
	private static final String CLASS_NAME = " >-----> ";
	protected static final String COBIS_CONTEXT = "COBIS";
	protected static final int CODE_OFFLINE = 40004;

	@Override
	public SelfAccountTransferResponse executeSelfAccountTransfer(SelfAccountTransferRequest selfAccountTransfer)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "INICIANDO SERVICIO");
			logger.logInfo(CLASS_NAME + "RESPUESTA CORE GENERADA");
		}

		SelfAccountTransferResponse wSelfAccountTransferResponse = transformResponse(
				executeSelfAccountTransferCobis(selfAccountTransfer));

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "TERMINANDO SERVICIO");

		return wSelfAccountTransferResponse;
	}

	private IProcedureResponse executeSelfAccountTransferCobis(SelfAccountTransferRequest request) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando transferencia propias CORE COBIS");

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_SSN, ICOBISTS.HEADER_NUMBER_TYPE,
				request.getReferenceNumber());
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH, ICOBISTS.HEADER_NUMBER_TYPE,
				request.getReferenceNumberBranch());
		anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18305");
		anOriginalRequest.setSpName("cob_cuentas..sp_tr03_transferencia");
		anOriginalRequest.addInputParam("@t_online", ICTSTypes.SYBCHAR, "S");
		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18305");
		anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SYBINT4, String.valueOf(request.getOfficeCode()));
		anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, request.getUserBv());
		anOriginalRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, request.getTerminal());
		anOriginalRequest.addInputParam("@s_rol", ICTSTypes.SQLINT2, String.valueOf(request.getRole()));
		anOriginalRequest.addInputParam("@s_date", ICTSTypes.SQLDATETIME, String.valueOf(request.getSystemDate()));
		anOriginalRequest.addInputParam("@i_causa_com", ICTSTypes.SQLVARCHAR, request.getCauseComi());
		anOriginalRequest.addInputParam("@i_causa_org", ICTSTypes.SQLVARCHAR, request.getCause());
		anOriginalRequest.addInputParam("@i_causa_des", ICTSTypes.SQLVARCHAR, request.getCauseDes());

		if (request.getServiceCost() != null) {			
			anOriginalRequest.addInputParam("@i_servicio_costo", ICTSTypes.SQLVARCHAR, request.getServiceCost());
		}

		if (request.getDescriptionTransfer() != null)
			anOriginalRequest.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, request.getDescriptionTransfer());

		if (request.getCommisionAmmount() == null) {
			anOriginalRequest.addInputParam("@i_comision", ICTSTypes.SYBMONEY, "0");
		} else {
			anOriginalRequest.addInputParam("@i_comision", ICTSTypes.SYBMONEY,
					request.getCommisionAmmount().toString());
		}

		anOriginalRequest.addInputParam("@i_origen_fondos", ICTSTypes.SQLVARCHAR, request.getOriginatorFunds());
		anOriginalRequest.addInputParam("@i_dest_fondos", ICTSTypes.SQLVARCHAR, request.getReceiverFunds());
		if (request.getReferenceNumberBranch() != null)
			anOriginalRequest.addInputParam("@s_ssn_branch", ICTSTypes.SQLINT4, request.getReferenceNumberBranch());
		if (request.getReferenceNumber() != null)
			anOriginalRequest.addInputParam("@s_ssn", ICTSTypes.SQLINT4, request.getReferenceNumber());

		if (request.getOriginProduct().getCurrency().getCurrencyId() != null)
			anOriginalRequest.addInputParam("@i_mon", ICTSTypes.SQLINT2,
					request.getOriginProduct().getCurrency().getCurrencyId().toString());
		if (request.getOriginProduct().getProductType() != null)
			anOriginalRequest.addInputParam("@i_prod_org", ICTSTypes.SQLINT2,
					request.getOriginProduct().getProductType().toString());
		if (request.getOriginProduct().getProductNumber() != null)
			anOriginalRequest.addInputParam("@i_cta_org", ICTSTypes.SQLVARCHAR,
					request.getOriginProduct().getProductNumber());
		if (request.getDestinationProduct().getCurrency().getCurrencyId() != null)
			anOriginalRequest.addInputParam("@i_mon_des", ICTSTypes.SQLINT2,
					request.getDestinationProduct().getCurrency().getCurrencyId().toString());
		if (request.getDestinationProduct().getProductType() != null)
			anOriginalRequest.addInputParam("@i_prod_des", ICTSTypes.SQLINT2,
					request.getDestinationProduct().getProductType().toString());
		if (request.getDestinationProduct().getProductNumber() != null)
			anOriginalRequest.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR,
					request.getDestinationProduct().getProductNumber());
		if (request.getAmmount() != null)
			anOriginalRequest.addInputParam("@i_val", ICTSTypes.SQLMONEY, request.getAmmount().toString());
		if (request.getDescriptionTransfer() != null)
			anOriginalRequest.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, request.getDescriptionTransfer());

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

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Data enviada a ejecutar:" + anOriginalRequest.getProcedureRequestAsString());

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta del Core:" + response.getProcedureResponseAsString());

		return response;
	}

	private SelfAccountTransferResponse transformResponse(IProcedureResponse response) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando Transformacion de Respuesta");

		SelfAccountTransferResponse responseTransfer = new SelfAccountTransferResponse();
		BalanceProduct aBalanceProduct = new BalanceProduct();
		BalanceProduct aBalanceProductDest = new BalanceProduct();

		Product product = new Product();
		Product productDest = new Product();
		Office office = new Office();
		Office officeDest = new Office();

		if (response == null) {
			if (logger.isInfoEnabled())
				logger.logInfo("SelfAccountTransfer --> Response null");
			return null;
		}

		if (response.getResultSetListSize() > 0) {
			IResultSetRow[] rows = response.getResultSet(response.getResultSetListSize()).getData().getRowsAsArray();
			IResultSetRowColumnData[] columns = rows[0].getColumnsAsArray();
			office.setId(columns[10].getValue() == null ? 0 : Integer.parseInt(columns[10].getValue()));
			product.setProductType(columns[11].getValue() == null ? 0 : Integer.parseInt(columns[11].getValue()));
			officeDest.setId(columns[26].getValue() == null ? 0 : Integer.parseInt(columns[26].getValue()));
			productDest.setProductType(columns[27].getValue() == null ? 0 : Integer.parseInt(columns[27].getValue()));

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
			aBalanceProduct.setSsnHost(columns[13].getValue() == null ? 0 : Integer.parseInt(columns[13].getValue()));
			aBalanceProduct.setSurplusAmmount(
					columns[14].getValue() == null ? new BigDecimal(0) : new BigDecimal(columns[14].getValue()));
			aBalanceProduct.setIdClosed(columns[15].getValue() == null ? 0 : Integer.parseInt(columns[15].getValue()));
			aBalanceProduct.setCashBalance(
					columns[16].getValue() == null ? new BigDecimal(0) : new BigDecimal(columns[16].getValue()));
			// Producto Destino
			aBalanceProductDest.setAvailableBalance(
					columns[18].getValue() == null ? new BigDecimal(0) : new BigDecimal(columns[18].getValue()));
			aBalanceProductDest.setAccountingBalance(
					columns[19].getValue() == null ? new BigDecimal(0) : new BigDecimal(columns[19].getValue()));
			aBalanceProductDest.setRotateBalance(
					columns[20].getValue() == null ? new BigDecimal(0) : new BigDecimal(columns[20].getValue()));
			aBalanceProductDest.setBalance12H(
					columns[21].getValue() == null ? new BigDecimal(0) : new BigDecimal(columns[21].getValue()));
			aBalanceProductDest.setBalance24H(
					columns[22].getValue() == null ? new BigDecimal(0) : new BigDecimal(columns[22].getValue()));
			aBalanceProductDest.setRemittancesBalance(
					columns[23].getValue() == null ? new BigDecimal(0) : new BigDecimal(columns[23].getValue()));
			aBalanceProductDest.setBlockedAmmount(
					columns[24].getValue() == null ? new BigDecimal(0) : new BigDecimal(columns[24].getValue()));
			aBalanceProductDest
					.setBlockedNumber(columns[25].getValue() == null ? 0 : Integer.parseInt(columns[25].getValue()));
			aBalanceProductDest.setBlockedNumberAmmount(
					columns[26].getValue() == null ? 0 : Integer.parseInt(columns[26].getValue()));
			aBalanceProductDest.setOfficeAccount(officeDest);
			aBalanceProductDest.setProduct(productDest);
			aBalanceProduct.setState(columns[29].getValue() == null ? "" : columns[29].getValue());
			aBalanceProduct.setSurplusAmmount(
					columns[30].getValue() == null ? new BigDecimal(0) : new BigDecimal(columns[30].getValue()));
			// OwnAccountTransferResponse
			responseTransfer.setBalanceProduct(aBalanceProduct);
			responseTransfer.setBalanceProductDest(aBalanceProductDest);
			responseTransfer.setDateHost(columns[17].getValue() == null ? "" : columns[17].getValue());
			responseTransfer.setName(columns[31].getValue() == null ? "" : columns[31].getValue());
			responseTransfer.setDateLastMovement(columns[32].getValue() == null ? "" : columns[32].getValue());
			responseTransfer.setAccountStatus(columns[33].getValue() == null ? "" : columns[33].getValue());
		}

		if (response.getReturnCode() != 0 && response.getReturnCode() != 40002)
			responseTransfer.setMessages(Utils.returnArrayMessage(response));

		responseTransfer.setReferenceNumber(response.readValueParam("@o_referencia"));
		responseTransfer.setReturnCode(response.getReturnCode());
		responseTransfer.setSuccess(response.getReturnCode() == 0 ? true : false);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta " + responseTransfer);
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
