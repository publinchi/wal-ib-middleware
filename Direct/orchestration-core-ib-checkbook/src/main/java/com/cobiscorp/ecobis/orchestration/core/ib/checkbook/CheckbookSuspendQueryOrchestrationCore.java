package com.cobiscorp.ecobis.orchestration.core.ib.checkbook;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
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
import com.cobiscorp.ecobis.ib.application.dtos.CheckbookSuspendResponse;
import com.cobiscorp.ecobis.ib.application.dtos.NoPaycheckOrderRequest;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.applications.ApplicationsBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountingParameter;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NoPaycheckOrder;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Notification;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NotificationDetail;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCheckbook;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceNotification;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;

/**
 * 
 * @author jveloz
 *
 */
@Component(name = "CheckbookSuspendQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "CheckbookSuspendQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CheckbookSuspendQueryOrchestrationCore") })
public class CheckbookSuspendQueryOrchestrationCore extends ApplicationsBaseTemplate {

	@Reference(referenceInterface = ICoreServiceCheckbook.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceCheckbook", unbind = "unbindCoreServiceCheckbook")
	protected ICoreServiceCheckbook coreServiceCheckbook;

	@Reference(referenceInterface = ICoreServiceNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceNotification", unbind = "unbindCoreServiceNotification")
	protected ICoreServiceNotification coreServiceNotification;

	@Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreService coreService;

	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	@Reference(referenceInterface = ICoreServiceMonetaryTransaction.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceMonetaryTransaction", unbind = "unbindCoreServiceMonetaryTransaction")
	protected ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction;

	@Reference(referenceInterface = ICoreServiceSendNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceSendNotification", unbind = "unbindCoreServiceSendNotification")
	protected ICoreServiceSendNotification coreServiceSendNotification;

	ILogger logger = this.getLogger();

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreServiceCheckbook(ICoreServiceCheckbook service) {
		if (logger.isInfoEnabled())
			logger.logInfo("*********bindCoreServiceCheckbook**********" + service);
		coreServiceCheckbook = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreServiceCheckbook(ICoreServiceCheckbook service) {
		if (logger.isInfoEnabled())
			logger.logInfo("*********unbindCoreServiceCheckbook**********" + service);
		coreServiceCheckbook = null;
	}

	/**
	 * Instance ServiceNotification Interface
	 *
	 * @param service
	 */
	public void bindCoreServiceNotification(ICoreServiceNotification service) {
		if (logger.isInfoEnabled())
			logger.logInfo("*********bindCoreServiceNotification**********" + service);
		coreServiceNotification = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreServiceNotification(ICoreServiceNotification service) {
		if (logger.isInfoEnabled())
			logger.logInfo("*********unbindCoreServiceNotification**********" + service);
		coreServiceNotification = null;
	}

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreService service) {
		if (logger.isInfoEnabled())
			logger.logInfo("*********bindCoreService**********" + service);
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreService service) {
		if (logger.isInfoEnabled())
			logger.logInfo("*********unbindCoreService**********" + service);
		coreService = null;
	}

	/**
	 * Instance CoreServer Interface
	 * 
	 * @param service
	 */
	public void bindCoreServer(ICoreServer service) {
		if (logger.isInfoEnabled())
			logger.logInfo("*********bindCoreServer**********" + service);
		coreServer = service;
	}

	public void unbindCoreServer(ICoreServer service) {
		if (logger.isInfoEnabled())
			logger.logInfo("*********unbindCoreServer**********" + service);
		coreServer = null;
	}

	/**
	 * Instance ServiceMonetaryTransaction Interface
	 *
	 * @param service
	 */
	public void bindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = service;
	}

	/**
	 * Deleting ServiceMonetaryTransaction Interface
	 *
	 * @param service
	 */
	public void unbindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = null;
	}

	/**
	 * Instance ServiceSendNotification Interface
	 *
	 * @param service
	 */
	public void bindCoreServiceSendNotification(ICoreServiceSendNotification service) {
		coreServiceSendNotification = service;
	}

	/**
	 * Deleting ServiceSendNotification Interface
	 *
	 * @param service
	 */
	public void unbindCoreServiceSendNotification(ICoreServiceSendNotification service) {
		coreServiceSendNotification = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	protected IProcedureResponse executeApplication(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {

		String messageError = null;
		String messageLog = null;
		String queryName = null;

		CheckbookSuspendResponse checkbookSuspendResponse = null;
		NoPaycheckOrderRequest noPaycheckOrderRequest = transformProcedureToDTORequest(request);
		if (logger.isDebugEnabled())
			logger.logDebug("OBJ: " + noPaycheckOrderRequest.getAccount());

		try {
			if (logger.isDebugEnabled())
				logger.logDebug("executeApplication");
			messageError = "get: ERROR EXECUTING SERVICE";
			if (logger.isDebugEnabled())
				logger.logDebug("request RequestCheckbook: " + request);
			messageLog = "getNoPaycheckOrderRequest: " + noPaycheckOrderRequest.getAuthorizationRequired();
			queryName = "getNoPaycheckOrderRequest";

			noPaycheckOrderRequest.setOriginalRequest(request);

			if (aBagSPJavaOrchestration.get(ACCOUNTING_PARAMETER) != null) {
				AccountingParameter accountingParameter = (AccountingParameter) aBagSPJavaOrchestration
						.get(ACCOUNTING_PARAMETER);
				if (logger.isInfoEnabled())
					logger.logInfo(accountingParameter.toString());
				noPaycheckOrderRequest.setCause(accountingParameter.getCause());
			}

			if (aBagSPJavaOrchestration.get(ACCOUNTING_PARAMETER_COMMISSION) != null) {
				AccountingParameter accountingParameterCommssion = (AccountingParameter) aBagSPJavaOrchestration
						.get(ACCOUNTING_PARAMETER_COMMISSION);
				if (logger.isInfoEnabled())
					logger.logInfo(accountingParameterCommssion.toString());
				noPaycheckOrderRequest.setCauseComi(accountingParameterCommssion.getCause());
				noPaycheckOrderRequest.setServiceCost(accountingParameterCommssion.getService());
			}
			checkbookSuspendResponse = coreServiceCheckbook.suspendChecks(noPaycheckOrderRequest);

		} catch (CTSServiceException e) {
			e.printStackTrace();
			if (logger.isDebugEnabled())
				logger.logDebug(messageError);
			return null;
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
			if (logger.isDebugEnabled())
				logger.logDebug(messageError);
			return null;
		}

		aBagSPJavaOrchestration.put(LOG_MESSAGE, messageLog);
		aBagSPJavaOrchestration.put(APPLICATION_NAME, queryName);

		return transformDTOResponseToProcedure(checkbookSuspendResponse);
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// Valida Inyección de dependencias
		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration");
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		mapInterfaces.put("coreServiceNotification", coreServiceNotification);
		mapInterfaces.put("coreServiceCheckbook", coreServiceCheckbook);
		Utils.validateComponentInstance(mapInterfaces);

		try {
			aBagSPJavaOrchestration.put(LOG_MESSAGE, "SUSPENSION DE CHEQUES");
			executeStepsApplicationBase(anOrginalRequest, aBagSPJavaOrchestration);

			return processResponse(anOrginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {

			return Utils.returnExceptionService(anOrginalRequest, e);
		}

	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = (IProcedureResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION");
		return response;

	}

	/******************
	 * Transformación de ProcedureRequest a NoPaycheckOrderRequest
	 ********************/

	private NoPaycheckOrderRequest transformProcedureToDTORequest(IProcedureRequest aRequest) {
		NoPaycheckOrderRequest wNoPaycheckOrderRequest = new NoPaycheckOrderRequest();
		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		wNoPaycheckOrderRequest.setTypeNotif(aRequest.readValueParam("@i_tipo_notif"));
		wNoPaycheckOrderRequest.setProductAbbreviation(aRequest.readValueParam("@i_producto"));

		if (!aRequest.readValueParam("@i_prod").equals(null))
			wNoPaycheckOrderRequest.setProductId(Integer.parseInt(aRequest.readValueParam("@i_prod")));

		if (!aRequest.readValueParam("@i_num_cheque").equals(null))
			wNoPaycheckOrderRequest.setNumberOfChecks(Integer.parseInt(aRequest.readValueParam("@i_num_cheque")));

		if (!aRequest.readValueParam("@i_mon").equals(null))
			wNoPaycheckOrderRequest.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));

		wNoPaycheckOrderRequest.setUserName(aRequest.readValueParam("@i_login"));

		wNoPaycheckOrderRequest.setAuthorizationRequired(aRequest.readValueParam("@i_doble_autorizacion"));
		wNoPaycheckOrderRequest.setAccount(aRequest.readValueParam("@i_cta"));
		wNoPaycheckOrderRequest.setConcept(aRequest.readValueParam("@i_concepto"));

		if (!aRequest.readValueParam("@i_cheque_ini").equals(null))
			wNoPaycheckOrderRequest.setInitialCheck(Integer.parseInt(aRequest.readValueParam("@i_cheque_ini")));

		wNoPaycheckOrderRequest.setReason(aRequest.readValueParam("@i_causa"));

		return wNoPaycheckOrderRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformDTOResponseToProcedure(CheckbookSuspendResponse aResponse) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isInfoEnabled())
			logger.logInfo(
					CLASS_NAME + "Transformando Dto de Salida CheckbookSuspendResponse :" + aResponse.toString());

		IResultSetHeader metaData = new ResultSetHeader();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("NUMBER_OF_RESULTS", ICTSTypes.SQLINT4, 4));
		IResultSetRow row = null;
		IResultSetData data = new ResultSetData();
		// NoPaycheckOrderResponse

		wProcedureResponse.addParam("@o_referencia", ICTSTypes.SQLINT4, 1, aResponse.getReference().toString());

		if (aResponse != null && aResponse.getListNoPaycheckOrder().size() > 0) {
			metaData = new ResultSetHeader();
			metaData.addColumnMetaData(new ResultSetHeaderColumn("initialCheck", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("finalCheck", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("account", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("reason", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("suspensionDate", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("reference", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("commission", ICTSTypes.SQLVARCHAR, 20));
			for (NoPaycheckOrder obj : aResponse.getListNoPaycheckOrder()) {
				row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, obj.getInitialCheck().toString()));
				row.addRowData(2, new ResultSetRowColumnData(false, obj.getFinalCheck().toString()));
				row.addRowData(3, new ResultSetRowColumnData(false, obj.getAccount()));
				row.addRowData(4, new ResultSetRowColumnData(false, obj.getReason()));
				row.addRowData(5, new ResultSetRowColumnData(false, obj.getSuspensionDate()));
				row.addRowData(6, new ResultSetRowColumnData(false, obj.getReference().toString()));
				row.addRowData(7, new ResultSetRowColumnData(false, obj.getCommission().toString()));
				data.addRow(row);
			}
			;
			IResultSetBlock resultBlock1 = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock1);
		}
		;

		wProcedureResponse.setReturnCode(aResponse.getReturnCode());
		if (logger.isInfoEnabled())
			logger.logInfo("*******************CODIGO DE RETORNO: " + aResponse.getReturnCode().toString());

		// AGREGAR LA DEVOLUCION DE LA COLECCION DE MENSAJES QUE DEVUELVE LA
		// IMPLEMENTACION, PARA MAPEAR EL MENSAJE DE ERROR DEVUELTO
		if (aResponse.getReturnCode() != 0) { // 201171
			// logger.logInfo("*******************Longitud :
			// "+aResponse.getMessages().length );
			// wProcedureResponse.addMessage(aResponse.getReturnCode(),
			// "201171-CHEQUES HAN SIDO SUSPENDIDOS PREVIAMENTE");
			wProcedureResponse = Utils.returnException(aResponse.getMessages());
		}

		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;
	}

	@Override
	protected ICoreService getCoreService() {
		// TODO Auto-generated method stub
		return coreService;
	}

	@Override
	public ICoreServiceNotification getCoreServiceNotification() {
		// TODO Auto-generated method stub
		return coreServiceNotification;
	}

	@Override
	protected ICoreServer getCoreServer() {
		// TODO Auto-generated method stub
		return coreServer;
	}

	@Override
	protected ICoreServiceMonetaryTransaction getCoreServiceMonetaryTransaction() {
		// TODO Auto-generated method stub
		return coreServiceMonetaryTransaction;
	}

	@Override
	public ICoreServiceSendNotification getCoreServiceSendNotification() {
		// TODO Auto-generated method stub
		return coreServiceSendNotification;
	}

	@Override
	public NotificationRequest transformNotificationRequest(IProcedureRequest anOriginalRequest,
			OfficerByAccountResponse anOfficer, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureResponse responseUpdateLocal = (IProcedureResponse) aBagSPJavaOrchestration
				.get(RESPONSE_UPDATE_LOCAL);

		NotificationDetail notificationDetail = new NotificationDetail();

		if (!Utils.isNull(anOfficer.getOfficer().getOfficerEmailAdress()))
			notificationDetail.setEmailClient(anOfficer.getOfficer().getAcountEmailAdress());// comun

		if (!Utils.isNull(anOfficer.getOfficer().getAcountEmailAdress()))
			notificationDetail.setEmailOficial(anOfficer.getOfficer().getOfficerEmailAdress());// comun

		if (!Utils.isNull(anOriginalRequest.readParam("@i_mon")))
			notificationDetail.setCurrencyDescription1(anOriginalRequest.readValueParam("@i_mon"));// comun

		if (!Utils.isNull(anOriginalRequest.readParam("@i_cta")))
			notificationDetail.setAccountNumberDebit(anOriginalRequest.readValueParam("@i_cta")); // comun

		if (!Utils.isNull(anOriginalRequest.readParam("@s_date")))
			notificationDetail.setDateNotification(anOriginalRequest.readValueParam("@s_date")); // comun

		if (!Utils.isNull(anOriginalRequest.readParam("@i_val")))
			notificationDetail.setValue(anOriginalRequest.readValueParam("@i_val")); // comun

		if (!Utils.isNull(anOriginalRequest.readParam("@i_concepto")))
			notificationDetail.setNote(anOriginalRequest.readValueParam("@i_concepto")); // comun

		if (!Utils.isNull(anOriginalRequest.readParam("@s_ssn_branch")))
			notificationDetail.setReference(anOriginalRequest.readValueParam("@s_ssn_branch"));// comun

		if (!Utils.isNull(anOriginalRequest.readParam("@t_trn")))
			notificationDetail.setTransaccionId(Integer.parseInt(anOriginalRequest.readValueParam("@t_trn")));// comun

		// parametros de la transaccion

		if (!Utils.isNull(anOriginalRequest.readParam("@i_cheque_ini")))
			notificationDetail.setAuxiliary1(anOriginalRequest.readValueParam("@i_cheque_ini"));

		if (!Utils.isNull(anOriginalRequest.readParam("@o_chq_hasta")))
			notificationDetail.setAuxiliary2(anOriginalRequest.readValueParam("@o_chq_hasta"));

		if (!Utils.isNull(anOriginalRequest.readParam("@o_causal")))
			notificationDetail.setAuxiliary3(anOriginalRequest.readValueParam("@o_causal"));

		if (!Utils.isNull(responseUpdateLocal.readParam("@o_comision")))
			notificationDetail.setCost(responseUpdateLocal.readValueParam("@o_comision"));

		if (!Utils.isNull(responseUpdateLocal.readParam("@i_mon")))
			notificationDetail.setCost1(anOriginalRequest.readValueParam("@i_mon"));

		Notification notification = new Notification();
		notification.setNotificationType("F"); // comun
		notification.setId("N38");

		Client client = new Client();
		client.setIdCustomer(anOriginalRequest.readValueParam("@s_cliente"));

		Product originProduct = new Product();
		originProduct.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_cta")))
			originProduct.setProductNumber(anOriginalRequest.readValueParam("@i_cta"));

		NotificationRequest notificationRequest = new NotificationRequest();
		if (!Utils.isNull(anOriginalRequest.readParam("@s_culture")))
			notificationRequest.setCulture(anOriginalRequest.readValueParam("@s_culture"));

		if (!Utils.isNull(anOriginalRequest.readParam("@s_ofi")))
			notificationRequest.setOfficeCode(Integer.parseInt(anOriginalRequest.readValueParam("@s_ofi")));

		if (!Utils.isNull(anOriginalRequest.readParam("@s_rol")))
			notificationRequest.setRole(Integer.parseInt(anOriginalRequest.readValueParam("@s_rol")));

		if (!Utils.isNull(anOriginalRequest.readParam("@s_ssn")))
			notificationRequest.setSessionIdCore(anOriginalRequest.readValueParam("@s_ssn"));

		if (!Utils.isNull(anOriginalRequest.readValueFieldInHeader("sessionId")))
			notificationRequest.setSessionIdIB(anOriginalRequest.readValueFieldInHeader("sessionId"));

		if (!Utils.isNull(anOriginalRequest.readValueFieldInHeader("term")))
			notificationRequest.setTerminal(anOriginalRequest.readValueFieldInHeader("term"));

		if (!Utils.isNull(anOriginalRequest.readParam("@s_user")))
			notificationRequest.setUserBv(anOriginalRequest.readValueParam("@s_user"));

		if (!Utils.isNull(anOriginalRequest.readParam("@t_trn")))
			notificationRequest.setCodeTransactionalIdentifier(anOriginalRequest.readValueParam("@t_trn"));

		notificationRequest.setClient(client);
		notificationRequest.setNotification(notification);
		notificationRequest.setNotificationDetail(notificationDetail);
		notificationRequest.setOriginalRequest(anOriginalRequest);
		notificationRequest.setOriginProduct(originProduct);

		return notificationRequest;
	}

}
