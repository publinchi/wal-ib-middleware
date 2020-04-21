package com.cobiscorp.ecobis.ib.orchestration.reverse.ach.transfer;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.ACHTransferRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ACHTransferResponse;
import com.cobiscorp.ecobis.ib.application.dtos.AccountingParameterRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AccountingParameterResponse;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ValidationAccountsRequest;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Notification;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NotificationDetail;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Secuential;
import com.cobiscorp.ecobis.ib.orchestration.dtos.User;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceACHTransfer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServicePayment;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;

@Component(name = "ReverseAchTransferOrchestationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "ReverseAchTransferOrchestationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ReverseAchTransferOrchestationCore") })
public class ReverseAchTransferOrchestationCore extends SPJavaOrchestrationBase {

	private static String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	private static String RESPONSE_BALANCE = "RESPONSE_BALANCE";
	private static final String COBIS_CONTEXT = "COBIS";

	private static ILogger logger = LogFactory.getLogger(ReverseAchTransferOrchestationCore.class);

	//

	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	public void bindCoreServer(ICoreServer service) {
		coreServer = service;
	}

	public void unbindCoreServer(ICoreServer service) {
		coreServer = null;
	}

	@Reference(referenceInterface = ICoreServiceSendNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceNotification", unbind = "unbindCoreServiceNotification")
	protected ICoreServiceSendNotification coreServiceNotification;

	public void bindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = service;
	}

	public void unbindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = null;
	}

	///
	@Reference(referenceInterface = ICoreServiceMonetaryTransaction.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceMonetaryTransaction", unbind = "unbindCoreServiceMonetaryTransaction")
	protected ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction;

	public void bindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = service;
	}

	public void unbindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = null;
	}

	@Reference(referenceInterface = ICoreServiceACHTransfer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceAchTransfer", unbind = "unbindCoreServiceAchTransfer")
	protected ICoreServiceACHTransfer coreServiceAchTransfer;

	public void bindCoreServiceAchTransfer(ICoreServiceACHTransfer service) {
		coreServiceAchTransfer = service;
	}

	public void unbindCoreServiceAchTransfer(ICoreServiceACHTransfer service) {
		coreServiceAchTransfer = null;
	}

	protected IProcedureResponse getBalancesToSynchronize(IProcedureRequest anOriginalRequest) {
		ValidationAccountsRequest validations = new ValidationAccountsRequest();
		validations = transformToValidationAccountRequest(anOriginalRequest);
		IProcedureResponse response = coreServiceMonetaryTransaction.getBalancesToSynchronize(validations);

		if (logger.isDebugEnabled())
			logger.logDebug("RESPONSE getBalancesToSynchronize -->" + response.getProcedureResponseAsString());

		return response;
	}

	protected ValidationAccountsRequest transformToValidationAccountRequest(IProcedureRequest anOriginalRequest) {

		ValidationAccountsRequest request = new ValidationAccountsRequest();

		Product originProduct = new Product();
		Currency originCurrency = new Currency();
		if (anOriginalRequest.readValueParam("@i_cta") != null)
			originProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta"));
		if (anOriginalRequest.readValueParam("@i_prod") != null)
			originProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod").toString()));
		if (anOriginalRequest.readValueParam("@i_mon") != null)
			originCurrency.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon").toString()));

		originProduct.setCurrency(originCurrency);

		Secuential originSSn = new Secuential();
		if (anOriginalRequest.readValueParam("@s_ssn") != null)
			originSSn.setSecuential(anOriginalRequest.readValueParam("@s_ssn").toString());
		if (anOriginalRequest.readValueParam("@s_servicio") != null)
			request.setChannelId(anOriginalRequest.readValueParam("@s_servicio"));
		if (anOriginalRequest.readValueParam("@t_trn") != null)
			request.setCodeTransactionalIdentifier(anOriginalRequest.readValueParam("@t_trn"));

		request.setSecuential(originSSn);
		request.setOriginProduct(originProduct);
		request.setOriginalRequest(anOriginalRequest);
		return request;

	}

	private ACHTransferRequest transformToACHTransferRequest(IProcedureResponse responseDataAchTransfer,
			IProcedureRequest anOriginalRequest) {
		ACHTransferRequest request = new ACHTransferRequest();
		if (responseDataAchTransfer.getResultSetListSize() > 0) {
			/*
			 * select pa_ente --0 ,pa_login --1 ,pa_referencia --2
			 * ,pa_identif_benef --3 ,pa_beneficiario --4 ,pa_bco_destino --5
			 * ,pa_cuenta_cr --6 ,pa_moneda_cr --7 ,pa_producto_cr --8
			 */

			if (logger.isDebugEnabled())
				logger.logDebug("Datos a Reversar --> " + responseDataAchTransfer.getProcedureResponseAsString());

			Client user = new Client();
			Currency destinationCurrency = new Currency();
			Currency currencyProduct = new Currency();
			Product originProduct = new Product();
			Product destinationProduct = new Product();

			IResultSetRow[] rows = responseDataAchTransfer.getResultSet(1).getData().getRowsAsArray();
			IResultSetRowColumnData[] columns = rows[0].getColumnsAsArray();

			currencyProduct.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon").toString()));
			destinationCurrency.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon").toString()));
			request.setAmmount(new BigDecimal(anOriginalRequest.readValueParam("@i_val")));
			request.setCurrencyId(Integer.parseInt(anOriginalRequest.readValueParam("@i_mon_trn")));
			request.setCommisionAmmount(new BigDecimal(anOriginalRequest.readValueParam("@i_comision")));

			originProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta"));
			originProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")));
			originProduct.setCurrency(currencyProduct);

			user.setId(columns[0].getValue());
			user.setLogin(columns[1].getValue());
			request.setReferenceNumberBranch(columns[2].getValue());
			request.setReferenceNumber(columns[2].getValue());
			request.setDocumentIdBeneficiary(columns[3].getValue());
			request.setBeneficiaryName(columns[4].getValue());

			destinationProduct.setProductNumber(columns[6].getValue());
			destinationProduct.setProductType(new Integer(columns[8].getValue()));
			destinationProduct.setCurrency(destinationCurrency);

			request.setOriginProduct(originProduct);
			request.setDestinationProduct(destinationProduct);
			request.setCause(anOriginalRequest.readValueParam("@i_cau_rev_trx"));
			request.setCauseComi(anOriginalRequest.readValueParam("@i_cau_rev_com"));

			request.setReverseTransaction(Utils.isNullOrEmpty(anOriginalRequest.readValueParam("@i_rev_trx")) ? "N"
					: anOriginalRequest.readValueParam("@i_rev_trx"));
			request.setReverseTaxTransaction(Utils.isNullOrEmpty(anOriginalRequest.readValueParam("@i_rev_imp_trx"))
					? "N" : anOriginalRequest.readValueParam("@i_rev_imp_trx"));
			request.setReverseCommission(Utils.isNullOrEmpty(anOriginalRequest.readValueParam("@i_rev_com")) ? "N"
					: anOriginalRequest.readValueParam("@i_rev_com"));

			request.setUser(user);
			request.setOriginatorFunds("NO APLICA");
			request.setReceiverFunds("NO APLICA");
			Utils.setSessionParameters(request, anOriginalRequest);

		}

		request.setOperation("S");

		return request;
	}

	private IProcedureResponse transformToProcedureResponse(ACHTransferResponse aResponse) {

		if (!aResponse.getSuccess())
			return Utils.returnException(aResponse.getMessages());

		IProcedureResponse response = new ProcedureResponseAS();
		return response;
	}

	protected IProcedureResponse updateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		IProcedureRequest request = initProcedureRequest(anOriginalRequest);

		IProcedureResponse responseBalance = (IProcedureResponse) bag.get(RESPONSE_BALANCE);

		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, anOriginalRequest.readValueParam("@t_trn"));
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.addFieldInHeader(ICOBISTS.HEADER_CLIENT, ICOBISTS.HEADER_NUMBER_TYPE, bag.get("ENTE").toString());
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);

		request.setSpName("cob_bvirtual..sp_bv_transaccion");

		request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, bag.get("LOGIN").toString());
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, anOriginalRequest.readValueParam("@t_trn"));

		if (anOriginalRequest.readValueParam("@i_graba_notif") != null) {
			Utils.copyParam("@i_graba_notif", anOriginalRequest, request);
		}

		if (anOriginalRequest.readValueParam("@i_graba_tranmonet") != null) {
			Utils.copyParam("@i_graba_tranmonet", anOriginalRequest, request);
		} else {
			request.addInputParam("@i_graba_tranmonet", ICTSTypes.SQLVARCHAR, "S");
		}

		request.addInputParam("@t_corr", ICTSTypes.SQLVARCHAR, "S");
		request.addInputParam("@t_ssn_corr", ICTSTypes.SQLINT4, bag.get("SSN_CORRECCION").toString());

		request.addInputParam("@i_graba_log", ICTSTypes.SQLVARCHAR, "S");
		request.addInputParam("@i_sinc_cta_des", ICTSTypes.SQLVARCHAR, "N");
		request.addInputParam("@i_tipo_notif", ICTSTypes.SQLVARCHAR, "P");
		Utils.copyParam("@i_cta", anOriginalRequest, request);
		Utils.copyParam("@i_prod", anOriginalRequest, request);
		Utils.copyParam("@i_mon", anOriginalRequest, request);
		Utils.copyParam("@i_concepto", anOriginalRequest, request);
		Utils.copyParam("@i_val", anOriginalRequest, request);
		Utils.addInputParam(request, "@t_rty", ICTSTypes.SQLVARCHAR, "N");

		// copia variables r_ como parámetros de entrada para sincronizar saldos
		if (!Utils.isNull(responseBalance)) {
			if (responseBalance.getResultSetListSize() > 0) {
				IResultSetHeaderColumn[] columns = responseBalance.getResultSet(1).getMetaData()
						.getColumnsMetaDataAsArray();
				IResultSetRow[] rows = responseBalance.getResultSet(1).getData().getRowsAsArray();
				IResultSetRowColumnData[] cols = rows[0].getColumnsAsArray();

				int i = 0;
				for (IResultSetHeaderColumn iResultSetHeaderColumn : columns) {
					if (!iResultSetHeaderColumn.getName().equals(""))
						if (cols[i].getValue() != null) {
							if (logger.isDebugEnabled())
								logger.logDebug("PARAMETROS AÑADIDOS :"
										+ iResultSetHeaderColumn.getName().replaceFirst("r_", "@i_") + " VALOR: "
										+ cols[i].getValue());
							Utils.addInputParam(request, iResultSetHeaderColumn.getName().replaceFirst("r_", "@i_"),
									iResultSetHeaderColumn.getType(), cols[i].getValue());
						}
					i++;
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("Update local, request: " + request.getProcedureRequestAsString());
		}
		/* Ejecuta y obtiene la respuesta */
		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Update local, response: " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Finalize Update local");
		}
		return pResponse;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {

	}

	ACHTransferRequest trasformToAchRequest(IProcedureResponse response) {
		ACHTransferRequest request = new ACHTransferRequest();

		return request;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		ACHTransferRequest aACHTransferRequest;
		ACHTransferResponse achTransferResponse;
		IProcedureResponse response = null;
		IProcedureResponse responseBalance = null;
		IProcedureResponse responseDataAchTransfer = null;
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreServer", coreServer);
		mapInterfaces.put("coreServiceMonetaryTransaction", coreServiceMonetaryTransaction);
		mapInterfaces.put("coreServiceAchTransfer", coreServiceAchTransfer);

		Utils.validateComponentInstance(mapInterfaces);

		ServerRequest serverRequest = new ServerRequest();
		serverRequest.setChannelId(anOriginalRequest.readValueFieldInHeader("servicio"));

		try {
			// SE VALIDA QUE NO SE EJECUTE EN OFFLINE
			if (!coreServer.getServerStatus(serverRequest).getOnLine()) {
				response = Utils.returnException("REVERSE IS NOT ALLOWED WHEN CORE IS OFFLINE");
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, response);
				return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
			}

			AccountingParameterRequest anAccountingParameterRequest = new AccountingParameterRequest();
			anAccountingParameterRequest.setCodeTransactionalIdentifier(String
					.valueOf(Utils.getTransactionMenu(Integer.parseInt(anOriginalRequest.readValueParam("@t_trn")))));

			// OBTENGO TRANSFERENCIA ACH A PARTIR DEL NUMERO DE LA ORDEN
			responseDataAchTransfer = getAchTransfer(anOriginalRequest, aBagSPJavaOrchestration);
			if (Utils.flowError("getAchTransfer", responseDataAchTransfer)) {
				response = responseDataAchTransfer;
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseDataAchTransfer);
				return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
			}

			// TRANSFORMO RESPONSE DE SALIDA EN DTO DE ENTRADA PARA REVERSO DE
			// ACH
			aACHTransferRequest = transformToACHTransferRequest(responseDataAchTransfer, anOriginalRequest);

			aBagSPJavaOrchestration.put("ENTE", aACHTransferRequest.getUser().getId());
			aBagSPJavaOrchestration.put("LOGIN", aACHTransferRequest.getUser().getLogin());
			aBagSPJavaOrchestration.put("SSN_CORRECCION", aACHTransferRequest.getReferenceNumberBranch());

			// EJECUTO REVERSO DE TRANSFERENCIA
			achTransferResponse = coreServiceAchTransfer.executeACHAccountTransfer(aACHTransferRequest);
			if (!achTransferResponse.getSuccess()) {
				response = Utils.returnException(achTransferResponse.getMessages());
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, response);
				return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
			}

			// OBTENGO LOS SALDOS DEL CENTRAL
			responseBalance = getBalancesToSynchronize(anOriginalRequest);
			aBagSPJavaOrchestration.put(RESPONSE_BALANCE, responseBalance);

			// SINCRONIZO LOS SALDOS
			response = updateLocalExecution(anOriginalRequest, aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, response);

		} catch (CTSServiceException e) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException(e.getMessage()));
			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);

		} catch (CTSInfrastructureException e) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException(e.getMessage()));
			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
		}

		return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}

	private IProcedureResponse getAchTransfer(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureResponse response = null;
		IProcedureRequest request = new ProcedureRequestAS();

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);

		request.setSpName("cob_bvirtual..sp_bv_data_ach_transfer");
		request.addInputParam("@i_orden", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_orden"));
		request.addInputParam("@i_val", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_val"));
		request.addInputParam("@i_mon", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_mon"));

		response = executeCoreBanking(request);
		return response;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
		if (logger.isDebugEnabled())
			logger.logDebug("RESPONSE FINAL -->" + response.getProcedureResponseAsString());
		if (response.readValueParam("@o_ssn_branch") == null) {
			response.addParam("@o_ssn_branch", ICTSTypes.SQLINT4, 0, response.readValueFieldInHeader("ssn_branch"));
		}

		if (response.readValueParam("@o_referencia") == null) {
			response.addParam("@o_referencia", ICTSTypes.SQLINT4, 0, response.readValueFieldInHeader("ssn_branch"));
		}

		return response;
	}

}
