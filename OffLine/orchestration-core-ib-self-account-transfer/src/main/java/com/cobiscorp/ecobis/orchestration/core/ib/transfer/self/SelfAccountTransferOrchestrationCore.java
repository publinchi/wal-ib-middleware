package com.cobiscorp.ecobis.orchestration.core.ib.transfer.self;

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
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.SelfAccountTransferResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.utils.transfers.UtilsTransfers;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Notification;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NotificationDetail;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceReexecutionComponent;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSelfAccountTransfers;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;
import com.cobiscorp.ecobis.orchestration.core.ib.transfer.template.TransferOfflineTemplate;

/**
 * Plugin of between accounts transfers
 *
 * @since Dec 05, 2014
 * @author mvelez
 * @version 1.0.0
 *
 */
@Component(name = "SelfAccountTransferOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "SelfAccountTransferOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "SelfAccountTransferOrchestrationCore") })
public class SelfAccountTransferOrchestrationCore extends TransferOfflineTemplate {

	/**
	 * Read configuration of parent component
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
	}

	/**
	 * Instance logger component
	 */
	private static ILogger logger = LogFactory.getLogger(SelfAccountTransferOrchestrationCore.class);
	private static final String CORESERVICEMONETARYTRANSACTION = "coreServiceMonetaryTransaction";

	@Reference(referenceInterface = ICoreServiceMonetaryTransaction.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceMonetaryTransaction", unbind = "unbindCoreServiceMonetaryTransaction")
	protected ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction;

	public void bindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = service;
	}

	public void unbindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = null;
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreServer(ICoreServer service) {
		coreServer = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreServer(ICoreServer service) {
		coreServer = null;
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceSelfAccountTransfers.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceSelfAccountTransfers", unbind = "unbindCoreServiceSelfAccountTransfers")
	private ICoreServiceSelfAccountTransfers coreServiceSelfAccountTransfers;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreServiceSelfAccountTransfers(ICoreServiceSelfAccountTransfers service) {
		coreServiceSelfAccountTransfers = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreServiceSelfAccountTransfers(ICoreServiceSelfAccountTransfers service) {
		coreServiceSelfAccountTransfers = null;
	}

	@Override
	public ICoreService getCoreService() {
		return coreService;
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreService coreService;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreService service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreService service) {
		coreService = null;
	}

	/**
	 * /** Execute transfer first step of service
	 * <p>
	 * This method is the main executor of transactional contains the original
	 * input parameters.
	 *
	 * @param anOriginalRequest
	 *            - Information original sended by user's.
	 * @param aBagSPJavaOrchestration
	 *            - Object dictionary transactional steps.
	 *
	 * @return
	 *         <ul>
	 *         <li>IProcedureResponse - Represents the service execution.</li>
	 *         </ul>
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = null;
		IProcedureRequest originalByNotify = anOriginalRequest;

		if (logger.isInfoEnabled())
			logger.logInfo("SelfAccountTransferOrchestrationCore: executeJavaOrchestration");

		Map<String, Object> mapInterfaces = new HashMap<String, Object>();

		mapInterfaces.put("coreServer", coreServer);
		mapInterfaces.put("coreService", coreService);
		mapInterfaces.put("coreServiceNotification", coreServiceNotification);
		mapInterfaces.put("coreServiceSelfAccountTransfers", coreServiceSelfAccountTransfers);

		Utils.validateComponentInstance(mapInterfaces);
		aBagSPJavaOrchestration.put(TRANSFER_NAME, "TRANFERENCIA PROPIAS");
		aBagSPJavaOrchestration.put(CORESERVICEMONETARYTRANSACTION, coreServiceMonetaryTransaction);
		try {

			response = executeStepsTransactionsBase(anOriginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}
		
		if(response!=null && !response.hasError()) {
			notifySelfAccountTransfer(originalByNotify,aBagSPJavaOrchestration);
		}
		return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
	}

	@Override
	protected ICoreServiceSendNotification getCoreServiceNotification() {

		return coreServiceNotification;
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceSendNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceNotification", unbind = "unbindCoreServiceNotification")
	public ICoreServiceSendNotification coreServiceNotification;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = null;
	}

	@Override
	public ICoreServer getCoreServer() {
		return coreServer;
	}

	public IProcedureResponse executeTransfer(Map<String, Object> aBagSPJavaOrchestration) {// throws
																							// CTSServiceException,
																							// CTSInfrastructureException
		SelfAccountTransferResponse SelfAccountTransferResponse = null;
		try {
			SelfAccountTransferResponse = coreServiceSelfAccountTransfers.executeSelfAccountTransfer(
					UtilsTransfers.transformSelfAccountTransferRequest(aBagSPJavaOrchestration, ORIGINAL_REQUEST,
							RESPONSE_LOCAL_VALIDATION));

			logger.logInfo("applay date 02 "+ SelfAccountTransferResponse.getApplyDate());

		} catch (CTSServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return transformToProcedureResponse(SelfAccountTransferResponse, aBagSPJavaOrchestration);
	}

	private IProcedureResponse transformToProcedureResponse(SelfAccountTransferResponse aSelfAccountTransferResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = new ProcedureResponseAS();
		response.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
		IProcedureResponse responseLocalValidation = (IProcedureResponse) aBagSPJavaOrchestration
				.get(RESPONSE_LOCAL_VALIDATION);
		IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		ServerResponse serverResponse = (ServerResponse) aBagSPJavaOrchestration.get(RESPONSE_SERVER);
		
		if (serverResponse.getOnLine() && aSelfAccountTransferResponse.getReturnCode() != 0) {
			// Si estamos en linea y hubo error
			response = Utils.returnException(aSelfAccountTransferResponse.getMessages());
			response.setReturnCode(aSelfAccountTransferResponse.getReturnCode());
			logger.logInfo("fecha_hora transaccion "+aSelfAccountTransferResponse.getApplyDate());
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, response);
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + "Respuesta transformToProcedureResponse -->"
						+ response.getProcedureResponseAsString());

			return response;
		}
		aBagSPJavaOrchestration.put("APPLY_DATE", aSelfAccountTransferResponse.getApplyDate());


		// Si estamos en fuera de linea consaldos o estamos en línea
		if ((!serverResponse.getOnLine() && serverResponse.getOfflineWithBalances())
				|| (serverResponse.getOnLine() && aSelfAccountTransferResponse.getReturnCode() == 0)) {
			IResultSetData data = new ResultSetData();

			if (aSelfAccountTransferResponse != null && !"".equals(aSelfAccountTransferResponse.getAccountStatus())) {
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

				metaData.addColumnMetaData(new ResultSetHeaderColumn("r_sld_disp_2", ICTSTypes.SQLMONEY, 10));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("r_sld_cont_2", ICTSTypes.SQLMONEY, 10));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("r_sld_girar_2", ICTSTypes.SQLMONEY, 10));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("r_sld_12h_2", ICTSTypes.SQLMONEY, 10));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("r_sld_24h_2", ICTSTypes.SQLMONEY, 10));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("r_sld_rem_2", ICTSTypes.SQLMONEY, 10));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("r_monto_blq_2", ICTSTypes.SQLMONEY, 10));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("r_num_bloq_2", ICTSTypes.SQLINT2, 6));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("r_num_blqmonto_2", ICTSTypes.SQLINT2, 6));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("r_ofi_cta_2", ICTSTypes.SQLINT4, 6));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("r_pro_ban_2", ICTSTypes.SQLINT2, 4));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("r_estado_2", ICTSTypes.SQLVARCHAR, 20));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("r_monto_sob_2", ICTSTypes.SQLMONEY, 20));

				metaData.addColumnMetaData(new ResultSetHeaderColumn("r_nombre", ICTSTypes.SQLVARCHAR, 20));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("r_fecha_ultmov", ICTSTypes.SQLVARCHAR, 20));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("r_estado_cta", ICTSTypes.SQLVARCHAR, 20));

				row.addRowData(1, new ResultSetRowColumnData(false, "submit_rpc"));
				row.addRowData(2, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProduct().getAvailableBalance().toString()));
				row.addRowData(3, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProduct().getAccountingBalance().toString()));
				row.addRowData(4, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProduct().getRotateBalance().toString()));
				row.addRowData(5, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProduct().getBalance12H().toString()));
				row.addRowData(6, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProduct().getBalance24H().toString()));
				row.addRowData(7, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProduct().getRemittancesBalance().toString()));
				row.addRowData(8, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProduct().getBlockedAmmount().toString()));
				row.addRowData(9, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProduct().getBlockedNumber().toString()));
				row.addRowData(10, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProduct().getBlockedNumberAmmount().toString()));
				row.addRowData(11, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProduct().getOfficeAccount().getId().toString()));
				row.addRowData(12, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProduct().getProduct().getProductType().toString()));
				row.addRowData(13,
						new ResultSetRowColumnData(false, aSelfAccountTransferResponse.getBalanceProduct().getState()));
				row.addRowData(14, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProduct().getSsnHost().toString()));
				row.addRowData(15, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProduct().getSurplusAmmount().toString()));
				row.addRowData(16, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProduct().getIdClosed().toString()));
				row.addRowData(17, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProduct().getCashBalance().toString()));
				row.addRowData(18, new ResultSetRowColumnData(false, aSelfAccountTransferResponse.getDateHost()));
				row.addRowData(19, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProductDest().getAvailableBalance().toString()));
				row.addRowData(20, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProductDest().getAccountingBalance().toString()));
				row.addRowData(21, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProductDest().getRotateBalance().toString()));
				row.addRowData(22, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProductDest().getBalance12H().toString()));
				row.addRowData(23, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProductDest().getBalance24H().toString()));
				row.addRowData(24, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProductDest().getRemittancesBalance().toString()));
				row.addRowData(25, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProductDest().getBlockedAmmount().toString()));
				row.addRowData(26, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProductDest().getBlockedNumber().toString()));
				row.addRowData(27, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProductDest().getBlockedNumberAmmount().toString()));
				row.addRowData(28, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProductDest().getOfficeAccount().getId().toString()));
				row.addRowData(29, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProductDest().getProduct().getProductType().toString()));
				row.addRowData(30, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProductDest().getState()));
				row.addRowData(31, new ResultSetRowColumnData(false,
						aSelfAccountTransferResponse.getBalanceProduct().getSurplusAmmount().toString()));

				row.addRowData(32, new ResultSetRowColumnData(false, aSelfAccountTransferResponse.getName()));
				row.addRowData(33,
						new ResultSetRowColumnData(false, aSelfAccountTransferResponse.getDateLastMovement()));
				row.addRowData(34, new ResultSetRowColumnData(false, aSelfAccountTransferResponse.getAccountStatus()));

				data.addRow(row);
				IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
				response.addResponseBlock(resultBlock);
			}
		}

		response.addParam("@o_referencia", ICTSTypes.SYBINT4, 0,
				String.valueOf(originalRequest.readValueParam("@s_ssn")));
		response.addParam("@o_retorno", ICTSTypes.SYBINT4, 0,
				String.valueOf(responseLocalValidation.readValueParam("@o_retorno")));
		response.addParam("@o_condicion", ICTSTypes.SYBINT4, 0,
				String.valueOf(responseLocalValidation.readValueParam("@o_condicion")));
		response.addParam("@o_autorizacion", ICTSTypes.SYBVARCHAR, 0,
				responseLocalValidation.readValueParam("@o_autorizacion"));
		response.addParam("@o_ssn_branch", ICTSTypes.SYBINT4, 0,
				String.valueOf(originalRequest.readValueParam("@s_ssn_branch")));

		response.addParam("@applay_date", ICTSTypes.SYBVARCHAR, 0,
				aSelfAccountTransferResponse.getApplyDate());


		response.setReturnCode(aSelfAccountTransferResponse.getReturnCode());
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta transformToProcedureResponse -->"
					+ response.getProcedureResponseAsString());

		return response;
	}

	@Override
	public ICoreServiceReexecutionComponent getCoreServiceReexecutionComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IProcedureResponse validateCentralExecution(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NotificationRequest transformNotificationRequest(IProcedureRequest anOriginalRequest,
			OfficerByAccountResponse anOfficer, Map<String, Object> aBagSPJavaOrchestration) {
		
		NotificationRequest notificationRequest = new NotificationRequest();
		notificationRequest.setOriginalRequest(anOriginalRequest);
		Notification notification = new Notification();

		Client client = new Client();
		client.setIdCustomer(anOriginalRequest.readValueParam("@s_cliente"));

		Product product = new Product();
		product.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")));
		if (!Utils.isNull(anOriginalRequest.readParam("@i_cta"))) {
			product.setProductNumber(anOriginalRequest.readValueParam("@i_cta"));
		}
		if (product.getProductType() == 3)
			notification.setId("N19");
		else
			notification.setId("N20");

		NotificationDetail notificationDetail = new NotificationDetail();

		if (!Utils.isNull(anOfficer.getOfficer().getOfficerEmailAdress()))
			notificationDetail.setEmailClient(anOfficer.getOfficer().getOfficerEmailAdress());
		if (!Utils.isNull(anOfficer.getOfficer().getAcountEmailAdress()))
			notificationDetail.setEmailOficial(anOfficer.getOfficer().getAcountEmailAdress());

		if (!Utils.isNull(anOriginalRequest.readParam("@i_cta")))
			notificationDetail.setAccountNumberDebit(anOriginalRequest.readValueParam("@i_cta"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_cta_des")))
			notificationDetail.setAccountNumberCredit(anOriginalRequest.readValueParam("@i_cta_des"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_concepto")))
			notificationDetail.setNote(anOriginalRequest.readValueParam("@i_concepto"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_mon"))) {
			notificationDetail.setCurrencyId1(anOriginalRequest.readValueParam("@i_mon"));
			notificationDetail.setCurrencyId2(anOriginalRequest.readValueParam("@i_mon"));
		}

		if (!Utils.isNull(anOriginalRequest.readParam("@i_val")))
			notificationDetail.setValue(anOriginalRequest.readValueParam("@i_val"));

		if (!Utils.isNull(anOriginalRequest.readParam("@s_date")))
			notificationDetail.setDateNotification(anOriginalRequest.readValueParam("@s_date"));
		if (!Utils.isNull(anOriginalRequest.readParam("@s_ssn")))
			notificationDetail.setReference(anOriginalRequest.readValueParam("@s_ssn"));
		if (!Utils.isNull(anOriginalRequest.readParam("@i_nom_cliente_benef")))
			notificationDetail.setAuxiliary1(anOriginalRequest.readValueParam("@i_nom_cliente_benef"));

		notificationRequest.setClient(client);
		notificationRequest.setNotification(notification);
		notificationRequest.setNotificationDetail(notificationDetail);
		notificationRequest.setOriginProduct(product);



		return notificationRequest;
	}

	@Override
	protected void addParametersRequestUpdateLocal(IProcedureRequest aProcedureRequest,
			IProcedureRequest anOriginalRequest) {
		// TODO Auto-generated method stub

	}
	
	private void notifySelfAccountTransfer (IProcedureRequest anOriginalRequest, java.util.Map map) {

		try {

			logger.logInfo("jcos Enviando notificacion cuentas propias");

			IProcedureRequest procedureRequest = initProcedureRequest(anOriginalRequest);

			String cuentaClave=anOriginalRequest.readValueParam("@i_cta_des");

			logger.logInfo("jcos using clabe account account "+cuentaClave);

			procedureRequest.setSpName("cob_bvirtual..sp_bv_enviar_notif_ib");
			procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S',"local");
			procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N',"1800195");

			procedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "1800195");
			procedureRequest.addInputParam("@i_servicio", ICTSTypes.SQLINT1, "8");
			// procedureRequest.addInputParam("@i_num_producto", Types.VARCHAR, "");
			procedureRequest.addInputParam("@i_tipo_mensaje", ICTSTypes.SQLCHAR, "F");
			procedureRequest.addInputParam("@i_notificacion", ICTSTypes.SYBVARCHAR, "N146");    		
			procedureRequest.addInputParam("@i_tipo", ICTSTypes.SQLVARCHAR, "I");
			procedureRequest.addInputParam("@i_producto", ICTSTypes.SQLINT1, "18");
			//procedureRequest.addInputParam("@i_transaccion_id", ICTSTypes.SQLINT1, "0");
			procedureRequest.addInputParam("@i_canal", ICTSTypes.SQLINT1, "8");
			procedureRequest.addInputParam("@i_origen", ICTSTypes.SQLVARCHAR, "A");
			//procedureRequest.addInputParam("@i_clabe", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cta_des"));    			 
			procedureRequest.addInputParam("@i_s", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_referenciaNumerica"));
			procedureRequest.addInputParam("@i_c1", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cta"));
			procedureRequest.addInputParam("@i_c2", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cta_des"));
			procedureRequest.addInputParam("@i_v2", ICTSTypes.SQLVARCHAR, String.valueOf(  anOriginalRequest.readValueParam("@i_val")));
			procedureRequest.addInputParam("@i_r", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_concepto"));
			procedureRequest.addInputParam("@i_m", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_mon"));
			
			//procedureRequest.addInputParam("@i_aux9", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_claveRastreo"));
			//procedureRequest.addInputParam("@i_aux8", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_nombreOrdenante"));
			IProcedureResponse procedureResponseLocal = executeCoreBanking(procedureRequest);

			logger.logInfo("jcos proceso de notificaciom terminado");

		}catch(Exception xe) {

			logger.logInfo("jcos Error en la notificacion cuentas propias");
			logger.logError(xe);
		}
	}

}

