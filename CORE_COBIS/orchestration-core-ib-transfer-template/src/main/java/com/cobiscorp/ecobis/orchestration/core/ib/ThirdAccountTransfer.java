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
import com.cobiscorp.ecobis.ib.application.dtos.ThirdPartyTransferRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ThirdPartyTransferResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BalanceProduct;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Office;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceThirdAccountTransfers;

@Component(name = "ThirdAccountTransfer", immediate = false)
@Service(value = { ICoreServiceThirdAccountTransfers.class })
@Properties(value = { @Property(name = "service.description", value = "ThirdAccountTransfer"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ThirdAccountTransfer") })
public class ThirdAccountTransfer extends SPJavaOrchestrationBase implements ICoreServiceThirdAccountTransfers {
	private static ILogger logger = LogFactory.getLogger(ThirdAccountTransfer.class);
	private static final String CLASS_NAME = " >-----> ";
	protected static final String COBIS_CONTEXT = "COBIS";

	@Override
	public ThirdPartyTransferResponse executeThirdAccountTransfer(ThirdPartyTransferRequest thirdAccountTransfer)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "INICIANDO SERVICIO");
			logger.logInfo(CLASS_NAME + "RESPUESTA CORE GENERADA");
		}

		ThirdPartyTransferResponse wSelfAccountTransferResponse = transformResponse(
				executeThirdAccountTransferCobis(thirdAccountTransfer));

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "TERMINANDO SERVICIO");
		return wSelfAccountTransferResponse;
	}

	private IProcedureResponse executeThirdAccountTransferCobis(ThirdPartyTransferRequest request) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando transferencia a terceros CORE COBIS" + request);

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_SSN, ICOBISTS.HEADER_NUMBER_TYPE,
				request.getReferenceNumber());
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH, ICOBISTS.HEADER_NUMBER_TYPE,
				request.getReferenceNumberBranch());
		anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18306");

		anOriginalRequest.setSpName("cob_cuentas..sp_tr03_pago_terceros"); 

		anOriginalRequest.addInputParam("@t_online", ICTSTypes.SYBCHAR, "S");
		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18306");
		anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SYBINT4, String.valueOf(request.getOfficeCode()));
		anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, request.getUserBv());
		anOriginalRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, request.getTerminal());
		anOriginalRequest.addInputParam("@i_causa_org", ICTSTypes.SQLVARCHAR, request.getCause());
		anOriginalRequest.addInputParam("@i_causa_des", ICTSTypes.SQLVARCHAR, request.getCauseDes());
		anOriginalRequest.addInputParam("@i_causa_comi", ICTSTypes.SQLVARCHAR, request.getCauseComi());
		if (logger.isInfoEnabled()) {
			logger.logInfo("********** CAUSALES DE TRANSFERENCIA *************");
			logger.logInfo("********** CAUSA ORIGEN --->>> " + request.getCause());
			logger.logInfo("********** CAUSA COMISI --->>> " + request.getCauseComi());
			logger.logInfo("********** CAUSA DESTIN --->>> " + request.getCauseDes());

			logger.logInfo("********** CLIENTE CORE --->>> " + request.getUser().getId());
			logger.logInfo("********** ORIGEN --->>> " + request.getOriginatorFunds());
			logger.logInfo("********** DESTINO --->>> " + request.getReceiverFunds());
		}
		if (request.getServiceCost() != null) {
			if (logger.isInfoEnabled())
				logger.logInfo("********** SERVICIO COSTO --->>> " + request.getServiceCost());
			anOriginalRequest.addInputParam("@i_servicio_costo", ICTSTypes.SQLVARCHAR, request.getServiceCost());
		}

		if (request.getDescriptionTransfer() != null)
			anOriginalRequest.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, request.getDescriptionTransfer());

		if (request.getUser().getId() != null)
			anOriginalRequest.addInputParam("@i_ente", ICTSTypes.SQLINT4, request.getUser().getId());
		if (request.getOriginatorFunds() != null)
			anOriginalRequest.addInputParam("@i_origen_fondos", ICTSTypes.SQLVARCHAR,
					request.getOriginatorFunds().toString());
		if (request.getReceiverFunds() != null)
			anOriginalRequest.addInputParam("@i_dest_fondos", ICTSTypes.SQLVARCHAR,
					request.getReceiverFunds().toString());

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "SSN_BRANCH enviado al local " + request.getReferenceNumberBranch());

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

		anOriginalRequest.addOutputParam("@o_fecha_tran", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXX");
		
		if (request.getCommisionAmmount() != null) {
			if (logger.isDebugEnabled())
				logger.logDebug(CLASS_NAME + "Se envia Comission:" + request.getCommisionAmmount().toString());
			anOriginalRequest.addInputParam("@i_comision", ICTSTypes.SQLMONEY, request.getCommisionAmmount().toString());
		}
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
			logger.logDebug(CLASS_NAME + "Data enviada a ejecutar:" + anOriginalRequest.toString());
		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta del Core:" + response.getProcedureResponseAsString());

		return response;
	}

	private ThirdPartyTransferResponse transformResponse(IProcedureResponse response) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando Transformacion de Respuesta");

		/* DTO */
		ThirdPartyTransferResponse responseTransfer = new ThirdPartyTransferResponse();
		BalanceProduct aBalanceProduct = new BalanceProduct();
		BalanceProduct aBalanceProductDest = new BalanceProduct();

		Product product = new Product();
		Product productDest = new Product();
		Office office = new Office();
		Office officeDest = new Office();

		if (response == null) {
			if (logger.isInfoEnabled())
				logger.logInfo("ThirdAccountTransfer --> Response null");
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

			// ThirdPartyTransferResponse
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

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta Responseeeee" + response);

		if (response.readFieldInHeader("ssn_branch") != null) {
			responseTransfer.setBranchSSN(Integer.parseInt(response.readValueFieldInHeader("ssn_branch").toString()));
		}

		responseTransfer.setSuccess(response.getReturnCode() == 0 ? true : false);
		responseTransfer.setReturnCode(response.getReturnCode());
		responseTransfer.setApplyDate(response.readValueParam("@o_fecha_tran"));

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta hsa" + responseTransfer);
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