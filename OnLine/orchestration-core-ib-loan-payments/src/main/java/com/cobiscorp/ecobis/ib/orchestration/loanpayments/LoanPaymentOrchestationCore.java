package com.cobiscorp.ecobis.ib.orchestration.loanpayments;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.management.Notification;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.AccountingParameterResponse;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentLoanRequest;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentLoanResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountingParameter;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NotificationDetail;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServicePaymentLoan;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;
import com.cobiscorp.ecobis.orchestration.core.ib.payment.template.PaymentOnlineTemplate;

/**
 * @author kmeza
 * @description This class implement logic to apply a payment
 */

@Component(name = "LoanPaymentOrchestationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "LoanPaymentOrchestationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.0"),
		@Property(name = "service.identifier", value = "LoanPaymentOrchestationCore") })
public class LoanPaymentOrchestationCore extends PaymentOnlineTemplate {
	protected static final String ACCOUNTING_PARAMETER = "ACCOUNTING_PARAMETER";
	protected static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	private static final String CORESERVICEMONETARYTRANSACTION = "coreServiceMonetaryTransaction";

	private static ILogger logger = LogFactory.getLogger(LoanPaymentOrchestationCore.class);

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Reference(referenceInterface = ICoreServiceMonetaryTransaction.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceMonetaryTransaction", unbind = "unbindCoreServiceMonetaryTransaction")
	protected ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction;

	public void bindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = service;
	}

	public void unbindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = null;
	}

	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	public void bindCoreServer(ICoreServer service) {
		coreServer = service;
	}

	public void unbindCoreServer(ICoreServer service) {
		coreServer = null;
	}

	// inyecta codigo para la validacion de la cuenta
	@Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreService coreService;

	public void bindCoreService(ICoreService service) {
		coreService = service;
	}

	public void unbindCoreService(ICoreService service) {
		coreService = null;
	}

	// inyecta codigo para notificacion
	@Reference(referenceInterface = ICoreServiceSendNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceNotification", unbind = "unbindCoreServiceNotification")
	protected ICoreServiceSendNotification coreServiceNotification;

	public void bindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = service;
	}

	public void unbindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = null;
	}

	// inyecta codigo para apgo de prestamo
	@Reference(referenceInterface = ICoreServicePaymentLoan.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreSericePaymentLoan", unbind = "unbindCoreSericePaymentLoan")
	protected ICoreServicePaymentLoan coreServicePayment;

	public void bindCoreSericePaymentLoan(ICoreServicePaymentLoan service) {
		coreServicePayment = service;
	}

	public void unbindCoreSericePaymentLoan(ICoreServicePaymentLoan service) {
		coreServicePayment = null;
	}

	@Override
	protected ICoreServer getCoreServer() {

		return coreServer;
	}

	@Override
	protected ICoreService getCoreService() {
		return coreService;
	}

	@Override
	protected ICoreServiceSendNotification getCoreServiceNotification() {

		return coreServiceNotification;
	}

	// Método para sobreescirtura si usted tiene que considerar una validación
	// previa para ejecutar el pago

	/**
	 * Obtener NotificationRequest para Pago del prestamo
	 */
	@Override
	protected NotificationRequest transformNotificationRequest(IProcedureRequest anOriginalRequest,
			OfficerByAccountResponse anOfficer) {

		NotificationRequest notificationRequest = new NotificationRequest();
		Notification notification = new Notification();

		Product product = new Product();
		product.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")));

		if (product.getProductType() == 3)
			notification.setId("N36");
		else
			notification.setId("N37");

		/*
		 * notification.setNotificationType("N1");
		 * notification.setMessageType("F");
		 */

		NotificationDetail notificationDetail = new NotificationDetail();

		if (!Utils.isNull(anOfficer.getOfficer().getOfficerEmailAdress()))
			notificationDetail.setEmailClient(anOfficer.getOfficer().getOfficerEmailAdress());
		if (!Utils.isNull(anOfficer.getOfficer().getAcountEmailAdress()))
			notificationDetail.setEmailOficial(anOfficer.getOfficer().getAcountEmailAdress());
		if (!Utils.isNull(anOriginalRequest.readParam("@i_producto")))
			notificationDetail.setProductId(anOriginalRequest.readValueParam("@i_producto"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_cta")))
			notificationDetail.setAccountNumberDebit(anOriginalRequest.readValueParam("@i_cta"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_oficial_cli")))
			notificationDetail.setEmailClient(anOriginalRequest.readValueParam("@i_oficial_cli"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_oficial_cta")))
			notificationDetail.setEmailOficial(anOriginalRequest.readValueParam("@i_oficial_cta"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_mon")))
			notificationDetail.setCurrencyId1(anOriginalRequest.readValueParam("@i_mon"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_c1")))
			notificationDetail.setAccountNumberDebit(anOriginalRequest.readValueParam("@i_c1"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_c2")))
			notificationDetail.setAccountNumberDebit(anOriginalRequest.readValueParam("@i_c2"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_v2")))
			notificationDetail.setValue(anOriginalRequest.readValueParam("@i_v2"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_aux1")))
			notificationDetail.setEmailBeneficiary(anOriginalRequest.readValueParam("@i_aux1"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_r")))
			notificationDetail.setReference(anOriginalRequest.readValueParam("@i_r"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_s")))
			notificationDetail.setReference(anOriginalRequest.readValueParam("@i_s"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_f")))
			notificationDetail.setDateNotification(anOriginalRequest.readValueParam("@i_f"));

		notificationRequest.setNotification(notification);
		notificationRequest.setNotificationDetail(notificationDetail);
		notificationRequest.setOriginalRequest(anOriginalRequest);
		Product originProduct = new Product();
		originProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")));
		if (!Utils.isNull(anOriginalRequest.readParam("@i_cta")))
			originProduct.setAccountNumberDebit(anOriginalRequest.readValueParam("@i_cta"));
		notificationRequest.setOriginProduct(originProduct);
		return notificationRequest;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(

			IProcedureRequest anOrginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		@SuppressWarnings("unused")
		IProcedureResponse response = null;
		try {
			Map<String, Object> mapInterfaces = new HashMap<String, Object>();
			mapInterfaces.put("coreServer", coreServer);
			mapInterfaces.put("coreService", coreService);
			mapInterfaces.put("coreServiceNotification", coreServiceNotification);
			mapInterfaces.put("coreServicePayment", coreServicePayment);
			Utils.validateComponentInstance(mapInterfaces);
			if (logger.isInfoEnabled())
				logger.logInfo("**************validandointerfaces");
			SUPPORT_OFFLINE = false;
			aBagSPJavaOrchestration.put(PAYMENT_NAME, "PAGO DE PRESTAMOS");
			aBagSPJavaOrchestration.put(CORESERVICEMONETARYTRANSACTION, coreServiceMonetaryTransaction);

			response = executeStepsPaymentBase(anOrginalRequest, aBagSPJavaOrchestration);

			return processResponse(anOrginalRequest, aBagSPJavaOrchestration);

		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {

			return Utils.returnExceptionService(anOrginalRequest, e);
		}

	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureResponse response = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
		if (logger.isDebugEnabled())
			logger.logDebug("RESPONSE FINAL -->" + response.getProcedureResponseAsString());
		return response;
	}

	@Override
	protected IProcedureResponse executePayment(IProcedureRequest aProcedureRequest,
			Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo("INGRESA executePayment");

		PaymentLoanResponse aPaymentLoanResponse = coreServicePayment
				.executePaymentLoan(transformToPaymentLoanRequest(aProcedureRequest, aBagSPJavaOrchestration));

		return transformToProcedureResponse(aPaymentLoanResponse, aBagSPJavaOrchestration);
	}

	private PaymentLoanRequest transformToPaymentLoanRequest(IProcedureRequest aRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		PaymentLoanRequest apaymentLoanReq = new PaymentLoanRequest();

		Map<String, AccountingParameter> map = null;
		// TransactionMonetaryExecutor AccParameter = new
		// TransactionMonetaryExecutor();
		PaymentLoanRequest aPaymentLoanRequest = new PaymentLoanRequest();
		IProcedureRequest anOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		AccountingParameterResponse responseAccountingParameters = null;
		responseAccountingParameters = (AccountingParameterResponse) aBagSPJavaOrchestration.get(ACCOUNTING_PARAMETER);

		map = null;
		// map=
		// AccParameter.existsAccountingParameter(responseAccountingParameters,
		// Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")),
		// "T","C");
		map = coreServiceMonetaryTransaction.existsAccountingParameter(responseAccountingParameters,
				Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")), "T", "C");

		if (!Utils.isNull(map)) {
			aPaymentLoanRequest.setCausa(map.get("ACCOUNTING_PARAM").getCause());

		}
		map = null;
		// map=
		// AccParameter.existsAccountingParameter(responseAccountingParameters,
		// Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")),
		// "C",null);
		map = coreServiceMonetaryTransaction.existsAccountingParameter(responseAccountingParameters,
				Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")), "C", null);

		if (!Utils.isNull(map)) {
			aPaymentLoanRequest.setCausaComi(map.get("ACCOUNTING_PARAM").getCause());

		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("<<<<<<<<<<<<< CAUSA : " + aPaymentLoanRequest.getCausa());
			logger.logInfo("<<<<<<<<<<<<< CAUSA COMI : " + aPaymentLoanRequest.getCausaComi());
		}
		if (aRequest.readValueParam("@i_prod") != null) {
			apaymentLoanReq.setProductid(Integer.parseInt(aRequest.readValueParam("@i_prod")));
		}
		if (aRequest.readValueParam("@i_mon") != null) {
			apaymentLoanReq.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));
		}
		if (aRequest.readValueParam("@i_login") != null) {
			apaymentLoanReq.setUserName(aRequest.readValueParam("@i_login"));
		}
		if (aRequest.readValueParam("@i_cta") != null) {
			apaymentLoanReq.setAccount(aRequest.readValueParam("@i_cta"));
		}
		if (aRequest.readValueParam("@i_concepto") != null) {
			apaymentLoanReq.setConcept(aRequest.readValueParam("@i_concepto"));
		}
		if (aRequest.readValueParam("@i_nom_cliente_benef") != null) {
			apaymentLoanReq.setProductName(aRequest.readValueParam("@i_nom_cliente_benef"));
		}
		if (aRequest.readValueParam("@i_val") != null) {
			apaymentLoanReq.setAmmount(new BigDecimal(aRequest.readValueParam("@i_val")));
		}
		if (aRequest.readValueParam("@i_cta_des") != null) {
			apaymentLoanReq.setLoanNumber(aRequest.readValueParam("@i_cta_des"));
		}
		if (aRequest.readValueParam("@i_prod_des") != null) {
			apaymentLoanReq.setDestProduct(Integer.parseInt(aRequest.readValueParam("@i_prod_des")));
		}
		if (aRequest.readValueParam("@i_mon_des") != null) {
			apaymentLoanReq.setLoanCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon_des")));
		}
		if (aRequest.readValueParam("@i_ente") != null) {
			apaymentLoanReq.setEntityId(Integer.parseInt(aRequest.readValueParam("@i_ente")));
		}
		if (aRequest.readValueParam("@i_producto") != null) {
			apaymentLoanReq.setProductAbbreviation(aRequest.readValueParam("@i_producto"));
		}
		if (aRequest.readValueParam("@i_doble_autorizacion") != null) {
			apaymentLoanReq.setAuthorizationRequired(aRequest.readValueParam("@i_doble_autorizacion"));
		}
		if (aRequest.readValueParam("@i_tercero_asociado") != null) {
			apaymentLoanReq.setThirdPartyAssociated(aRequest.readValueParam("@i_tercero_asociado"));
		}
		if (aRequest.readValueParam("@i_tercero") != null) {
			apaymentLoanReq.setIsThirdParty(aRequest.readValueParam("@i_tercero"));
		}
		if (aRequest.readValueParam("@i_monto_mpg") != null) {
			apaymentLoanReq.setLoanPaymentAmount(new BigDecimal(aRequest.readValueParam("@i_monto_mpg")));
		}
		if (aRequest.readValueParam("@i_monto") != null) {
			apaymentLoanReq.setCreditAmount(new BigDecimal(aRequest.readValueParam("@i_monto")));
		}
		if (aRequest.readValueParam("@i_cotizacion") != null) {
			apaymentLoanReq.setRateValue(Float.parseFloat(aRequest.readValueParam("@i_cotizacion").toString()));
		}
		if (aRequest.readValueParam("@i_valida_des") != null) {
			apaymentLoanReq.setValidateAccount(aRequest.readValueParam("@i_valida_des"));
		}
		if (aRequest.readValueParam("@s_ssn_branch") != null)
			apaymentLoanReq.setReferenceNumberBranch(aRequest.readValueParam("@s_ssn_branch"));

		if (aRequest.readValueParam("@s_ssn") != null)
			apaymentLoanReq.setReferenceNumber(aRequest.readValueParam("@s_ssn"));

		if (aRequest.readValueParam("@s_user") != null)
			apaymentLoanReq.setUserBv(aRequest.readValueParam("@s_user"));

		apaymentLoanReq.setOriginalRequest(aRequest);

		return apaymentLoanReq;
	}

	private IProcedureResponse transformToProcedureResponse(PaymentLoanResponse aPaymentLoanResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = new ProcedureResponseAS();
		response.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
		IResultSetData data = new ResultSetData();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		if (aPaymentLoanResponse.getReturnCode() == 0) {
			IResultSetRow row = new ResultSetRow();
			IResultSetHeader metaData = new ResultSetHeader();
			metaData.addColumnMetaData(new ResultSetHeaderColumn("result_submit_rpc", ICTSTypes.SQLMONEY, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("r_sld_disp", ICTSTypes.SQLMONEY, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("r_sld_cont", ICTSTypes.SQLMONEY, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("r_sld_girar", ICTSTypes.SQLMONEY, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("r_sld_12h", ICTSTypes.SQLMONEY, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("r_sld_24h", ICTSTypes.SQLMONEY, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("r_sld_rem", ICTSTypes.SQLMONEY, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("r_monto_blq", ICTSTypes.SQLMONEY, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("r_num_bloq", ICTSTypes.SQLINT2, 6));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("r_num_blqmonto", ICTSTypes.SQLINT2, 6));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("r_ofi_cta", ICTSTypes.SQLINT4, 6));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("r_pro_ban", ICTSTypes.SQLINT2, 4));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("r_estado", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("r_ssn_host", ICTSTypes.SQLINT4, 4));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("r_monto_sob", ICTSTypes.SQLMONEY, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("r_idcierre", ICTSTypes.SQLINT4, 4));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("r_sldcaja", ICTSTypes.SQLINT4, 4));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("r_fecha_host", ICTSTypes.SQLVARCHAR, 20));

			row.addRowData(1, new ResultSetRowColumnData(false, "submit_rpc"));
			row.addRowData(2, new ResultSetRowColumnData(false,
					aPaymentLoanResponse.getBalanceProduct().getAvailableBalance().toString()));
			row.addRowData(3, new ResultSetRowColumnData(false,
					aPaymentLoanResponse.getBalanceProduct().getAccountingBalance().toString()));
			row.addRowData(4, new ResultSetRowColumnData(false,
					aPaymentLoanResponse.getBalanceProduct().getRotateBalance().toString()));
			row.addRowData(5, new ResultSetRowColumnData(false,
					aPaymentLoanResponse.getBalanceProduct().getBalance12H().toString()));
			row.addRowData(6, new ResultSetRowColumnData(false,
					aPaymentLoanResponse.getBalanceProduct().getBalance24H().toString()));
			row.addRowData(7, new ResultSetRowColumnData(false,
					aPaymentLoanResponse.getBalanceProduct().getRemittancesBalance().toString()));
			row.addRowData(8, new ResultSetRowColumnData(false,
					aPaymentLoanResponse.getBalanceProduct().getBlockedAmmount().toString()));
			row.addRowData(9, new ResultSetRowColumnData(false,
					aPaymentLoanResponse.getBalanceProduct().getBlockedNumber().toString()));
			row.addRowData(10, new ResultSetRowColumnData(false,
					aPaymentLoanResponse.getBalanceProduct().getBlockedNumberAmmount().toString()));
			row.addRowData(11, new ResultSetRowColumnData(false,
					aPaymentLoanResponse.getBalanceProduct().getOfficeAccount().getId().toString()));
			row.addRowData(12, new ResultSetRowColumnData(false,
					aPaymentLoanResponse.getBalanceProduct().getProduct().getProductType().toString()));
			row.addRowData(13, new ResultSetRowColumnData(false, aPaymentLoanResponse.getBalanceProduct().getState()));
			row.addRowData(14, new ResultSetRowColumnData(false,
					aPaymentLoanResponse.getBalanceProduct().getSsnHost().toString()));
			row.addRowData(15, new ResultSetRowColumnData(false,
					aPaymentLoanResponse.getBalanceProduct().getSurplusAmmount().toString()));
			// row.addRowData(16, new ResultSetRowColumnData(false,
			// aPaymentLoanResponse.getBalanceProduct().getIdClosed().toString()));

			row.addRowData(16,
					new ResultSetRowColumnData(false,
							aPaymentLoanResponse.getBalanceProduct().getIdClosed().toString() != null
									? aPaymentLoanResponse.getBalanceProduct().getIdClosed().toString() : ""));

			row.addRowData(17,
					new ResultSetRowColumnData(false,
							aPaymentLoanResponse.getBalanceProduct().getCashBalance().toString() != null
									? aPaymentLoanResponse.getBalanceProduct().getCashBalance().toString() : ""));
			row.addRowData(18,
					new ResultSetRowColumnData(false, aPaymentLoanResponse.getBalanceProduct().getProcessDate() != null
							? sdf.format(aPaymentLoanResponse.getBalanceProduct().getProcessDate()) : ""));

			data.addRow(row);
			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
			response.addResponseBlock(resultBlock);

			if (!IsValidLoanSatatementResponse(aPaymentLoanResponse))
				return null;

			if (aPaymentLoanResponse.getReference() != null) {
				response.addParam("@o_referencia", ICTSTypes.SQLINT4, 0,
						aPaymentLoanResponse.getReference().toString());
			}
			if (logger.isInfoEnabled())
				logger.logInfo("REFERENCIA: " + "" + aPaymentLoanResponse.getReference());

			if (aPaymentLoanResponse.getReturnValue() != null) {
				response.addParam("@o_secuencial_pag", ICTSTypes.SQLINT4, 0,
						aPaymentLoanResponse.getReturnValue().toString());
			}
			if (logger.isInfoEnabled())
				logger.logInfo("SECUENCIAL: " + "" + aPaymentLoanResponse.getReturnValue());

		} else {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aPaymentLoanResponse.getMessages()));
			response = Utils.returnException(aPaymentLoanResponse.getMessages());
		}

		response.setReturnCode(aPaymentLoanResponse.getReturnCode());
		if (logger.isInfoEnabled())
			logger.logInfo("RESPUESTA SECUENCIAL" + "" + response.getProcedureResponseAsString());

		return response;
	}

	private boolean IsValidLoanSatatementResponse(PaymentLoanResponse aPaymentLoanResponse) {
		String messageError = null;

		messageError = aPaymentLoanResponse.getReturnValue() == null ? "Return can't be null" : "OK";

		messageError = aPaymentLoanResponse.getReference() == null ? "Reference can't be null" : "OK";

		if (!messageError.equals("OK"))
			throw new IllegalArgumentException(messageError);
		return true;
	}

	@Override
	protected ICoreServiceMonetaryTransaction getCoreServiceMonetaryTransaction() {
		// TODO Auto-generated method stub
		return coreServiceMonetaryTransaction;
	}

	@Override
	protected IProcedureResponse payDestinationProduct(IProcedureRequest aProcedureRequest,
			Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void addParametersRequestUpdateLocal(IProcedureRequest aProcedureRequest,
			IProcedureRequest anOriginalRequest) {
		// TODO Auto-generated method stub

	}

	@Override
	protected IProcedureResponse validatePreviousExecution(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		return null;
	}

}
