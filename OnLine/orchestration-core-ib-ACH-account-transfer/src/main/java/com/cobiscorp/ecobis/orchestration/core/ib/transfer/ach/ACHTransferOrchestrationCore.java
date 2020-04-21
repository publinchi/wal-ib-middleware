package com.cobiscorp.ecobis.orchestration.core.ib.transfer.ach;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.cobiscorp.ecobis.ach.engine.AchMessage;
import com.cobiscorp.ecobis.ach.engine.AchMessageHeader;
import com.cobiscorp.ecobis.ach.engine.IAchWorkflow;
import com.cobiscorp.ecobis.ib.application.dtos.ACHTransferRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ACHTransferResponse;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.utils.transfers.UtilsTransfers;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Notification;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NotificationDetail;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceACHTransfer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;
import com.cobiscorp.ecobis.orchestration.core.ib.transfer.template.TransferOnlineTemplate;

/**
 * Plugin of third transfers
 *
 * @since Sep 1, 2014
 * @author gyagual
 * @version 1.0.0
 *
 */
@Component(name = "ACHTransferOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "ACHTransferOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.5.0"),
		@Property(name = "service.identifier", value = "ACHTransferOrchestrationCore") })
public class ACHTransferOrchestrationCore extends TransferOnlineTemplate {

	/**
	 * Read configuration of parent component
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
	}

	/**
	 * Instance logger component
	 */
	private static ILogger logger = LogFactory.getLogger(ACHTransferOrchestrationCore.class);
	private static final String CORESERVICEMONETARYTRANSACTION = "coreServiceMonetaryTransaction";

	/**
	 * Instance plugin to use services other core banking
	 */

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
	@Reference(referenceInterface = ICoreServiceACHTransfer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceACHAccountTransfers", unbind = "unbindCoreServiceACHAccountTransfers")
	private ICoreServiceACHTransfer CoreServiceACHAccountTransfers;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreServiceACHAccountTransfers(ICoreServiceACHTransfer service) {
		CoreServiceACHAccountTransfers = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreServiceACHAccountTransfers(ICoreServiceACHTransfer service) {
		CoreServiceACHAccountTransfers = null;
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

	/////////// ACH

	@Reference(referenceInterface = IAchWorkflow.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindAchWorkflow", unbind = "unbindAchWorkflow")
	protected IAchWorkflow achWorkflow;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindAchWorkflow(IAchWorkflow service) {
		achWorkflow = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindAchWorkflow(IAchWorkflow service) {
		achWorkflow = null;
	}

	public IAchWorkflow getAchWorkflow() {
		return achWorkflow;
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

		if (logger.isInfoEnabled())
			logger.logInfo("ThirdAccountTransferOrchestrationCore: executeJavaOrchestration INICIO --> ");
		SUPPORT_OFFLINE = true;

		Map<String, Object> mapInterfaces = new HashMap<String, Object>();

		mapInterfaces.put("coreServer", coreServer);
		mapInterfaces.put("coreService", coreService);
		mapInterfaces.put("coreServiceNotification", coreServiceNotification);
		mapInterfaces.put("CoreServiceACHAccountTransfers", CoreServiceACHAccountTransfers);
		mapInterfaces.put("achWorkflow", achWorkflow);
		if (logger.isDebugEnabled())
			logger.logDebug("Valida interfaces");

		Utils.validateComponentInstance(mapInterfaces);
		aBagSPJavaOrchestration.put(TRANSFER_NAME, "TRANSFERENCIA ACH");
		aBagSPJavaOrchestration.put(CORESERVICEMONETARYTRANSACTION, coreServiceMonetaryTransaction);

		try {
			/*
			 * AchMessage msg = new AchMessage(null);
			 * achWorkflow.createOrderACH(msg);
			 */
			response = executeStepsTransactionsBase(anOriginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
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
		ACHTransferResponse wACHTransferResponse = null;
		ACHTransferRequest wACHTransferRequest = null;

		int responseOrderAch = 0;

		try {
			wACHTransferRequest = UtilsTransfers.transformACHAccountTransferRequest(aBagSPJavaOrchestration,
					ORIGINAL_REQUEST, RESPONSE_LOCAL_VALIDATION);
			wACHTransferResponse = CoreServiceACHAccountTransfers.executeACHAccountTransfer(wACHTransferRequest);
			// responseOrderAch =
			// executeOrderACH((IProcedureRequest)aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));
			// TODO TEMPORAL
			responseOrderAch = 0;

			if (responseOrderAch != 0) {
				if (logger.isInfoEnabled())
					logger.logInfo("ACHTransferOrchestrationCore: Error al generar la orden ACH");
				wACHTransferRequest.setOperation("S");
				wACHTransferResponse = CoreServiceACHAccountTransfers.executeACHAccountTransfer(wACHTransferRequest);
				if (wACHTransferResponse.getReturnCode() == 0) {
					wACHTransferResponse.setReturnCode(responseOrderAch);
				}
			}

		} catch (CTSServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /*
			 * catch (ParseException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); }
			 */
		return transformToProcedureResponse(wACHTransferResponse, aBagSPJavaOrchestration);
	}

	private IProcedureResponse transformToProcedureResponse(ACHTransferResponse aACHTransferResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = new ProcedureResponseAS();
		response.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
		IProcedureResponse responseLocalValidation = (IProcedureResponse) aBagSPJavaOrchestration
				.get(RESPONSE_LOCAL_VALIDATION);
		IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

		response.setReturnCode(aACHTransferResponse.getReturnCode());

		if (!(response.getReturnCode() == 0 || response.getReturnCode() == 40004
				|| response.getReturnCode() == 40003)) {
			response = Utils.returnException(aACHTransferResponse.getMessages());
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, response);

		}

		else {
			IResultSetData data = new ResultSetData();

			if (aACHTransferResponse != null && !"".equals(aACHTransferResponse.getAccountStatus())) {
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
				metaData.addColumnMetaData(new ResultSetHeaderColumn("r_fecha_host", ICTSTypes.SQLVARCHAR, 20));

				metaData.addColumnMetaData(new ResultSetHeaderColumn("r_nombre", ICTSTypes.SQLVARCHAR, 20));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("r_fecha_ultmov", ICTSTypes.SQLVARCHAR, 20));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("r_estado_cta", ICTSTypes.SQLVARCHAR, 20));

				row.addRowData(1, new ResultSetRowColumnData(false, "submit_rpc"));
				row.addRowData(2, new ResultSetRowColumnData(false,
						aACHTransferResponse.getBalanceProduct().getAvailableBalance().toString()));
				row.addRowData(3, new ResultSetRowColumnData(false,
						aACHTransferResponse.getBalanceProduct().getAccountingBalance().toString()));
				row.addRowData(4, new ResultSetRowColumnData(false,
						aACHTransferResponse.getBalanceProduct().getRotateBalance().toString()));
				row.addRowData(5, new ResultSetRowColumnData(false,
						aACHTransferResponse.getBalanceProduct().getBalance12H().toString()));
				row.addRowData(6, new ResultSetRowColumnData(false,
						aACHTransferResponse.getBalanceProduct().getBalance24H().toString()));
				row.addRowData(7, new ResultSetRowColumnData(false,
						aACHTransferResponse.getBalanceProduct().getRemittancesBalance().toString()));
				row.addRowData(8, new ResultSetRowColumnData(false,
						aACHTransferResponse.getBalanceProduct().getBlockedAmmount().toString()));
				row.addRowData(9, new ResultSetRowColumnData(false,
						aACHTransferResponse.getBalanceProduct().getBlockedNumber().toString()));
				row.addRowData(10, new ResultSetRowColumnData(false,
						aACHTransferResponse.getBalanceProduct().getBlockedNumberAmmount().toString()));
				row.addRowData(11, new ResultSetRowColumnData(false,
						aACHTransferResponse.getBalanceProduct().getOfficeAccount().getId().toString()));
				row.addRowData(12, new ResultSetRowColumnData(false,
						aACHTransferResponse.getBalanceProduct().getProduct().getProductType().toString()));
				row.addRowData(13,
						new ResultSetRowColumnData(false, aACHTransferResponse.getBalanceProduct().getState()));
				row.addRowData(14, new ResultSetRowColumnData(false,
						aACHTransferResponse.getBalanceProduct().getSsnHost().toString()));
				row.addRowData(15, new ResultSetRowColumnData(false,
						aACHTransferResponse.getBalanceProduct().getSurplusAmmount().toString()));
				row.addRowData(16, new ResultSetRowColumnData(false, aACHTransferResponse.getDateHost()));

				row.addRowData(17, new ResultSetRowColumnData(false, aACHTransferResponse.getName()));
				row.addRowData(18, new ResultSetRowColumnData(false, aACHTransferResponse.getDateLastMovement()));
				row.addRowData(19, new ResultSetRowColumnData(false, aACHTransferResponse.getAccountStatus()));

				data.addRow(row);
				IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
				response.addResponseBlock(resultBlock);

				response.addParam("@o_referencia", ICTSTypes.SYBINT4, 0,
						String.valueOf(originalRequest.readValueParam("@s_ssn_branch")));
				response.addParam("@o_retorno", ICTSTypes.SYBINT4, 0,
						String.valueOf(responseLocalValidation.readValueParam("@o_retorno")));
				response.addParam("@o_condicion", ICTSTypes.SYBINT4, 0,
						String.valueOf(responseLocalValidation.readValueParam("@o_condicion")));
				response.addParam("@o_autorizacion", ICTSTypes.SYBVARCHAR, 0, "N");
				response.addParam("@o_ssn_branch", ICTSTypes.SYBINT4, 0,
						String.valueOf(responseLocalValidation.readValueParam("@o_ssn_branch")));

			}

		}
		response.setReturnCode(aACHTransferResponse.getReturnCode());

		return response;
	}

	@Override
	protected IProcedureResponse validateCentralExecution(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		return null;
	}

	private int executeOrderACH(IProcedureRequest aRequest) throws ParseException {

		com.cobiscorp.ecobis.achnetworkmanagement.bsl.dto.Request wParameter = new com.cobiscorp.ecobis.achnetworkmanagement.bsl.dto.Request();
		wParameter.setInfo(new HashMap(1));

		String network = aRequest.readValueParam("@i_redAch");
		String module = aRequest.readValueParam("@i_modulo");
		String channel = aRequest.readValueParam("@i_canal");
		String orderType = aRequest.readValueParam("@i_tipo_orden");
		String operationType = aRequest.readValueParam("@i_tipo_operacion");
		Integer feeId = Integer.parseInt(aRequest.readValueParam("@i_comision_id").toString());

		AchMessageHeader header = new AchMessageHeader(channel, module, operationType, network);
		AchMessage requestAchMessage = new AchMessage(header);

		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Date date = formatter.parse(aRequest.readValueParam("@s_date").toString());

		// Campos para la creación de la orden
		Map wOrderMap = new HashMap();
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: @s_ssn: " + aRequest.readValueParam("@s_ssn"));
		if (aRequest.readValueParam("@s_ssn") != null)
			wOrderMap.put("ssn", Integer.parseInt(aRequest.readValueParam("@s_ssn"))); // Secuencial
																						// de
																						// la
																						// transacción
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: @s_date: " + aRequest.readValueParam("@s_date"));
		if (aRequest.readValueParam("@s_date") != null)
			wOrderMap.put("date", aRequest.readValueParam("@s_date"));
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: @s_date: " + aRequest.readValueParam("@s_date"));
		if (aRequest.readValueParam("@s_date") != null)
			wOrderMap.put("processDate", date); // fecha de proceso de la
												// transacción
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: @i_login: " + aRequest.readValueParam("@i_login"));
		if (aRequest.readValueParam("@i_login") != null)
			wOrderMap.put("user", aRequest.readValueParam("@i_login")); // login
																		// usuario
																		// que
																		// realiza
																		// la
																		// creación
																		// de la
																		// orden
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: @i_office: " + aRequest.readValueParam("@i_office"));
		if (aRequest.readValueParam("@i_office") != null)
			wOrderMap.put("office", aRequest.readValueParam("@i_office")); // oficina
																			// en
																			// la
																			// que
																			// se
																			// realizo
																			// la
																			// transacción
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: @i_cod_banco: " + aRequest.readValueParam("@i_cod_banco"));
		if (aRequest.readValueParam("@i_cod_banco") != null)
			wOrderMap.put("originatorCode", aRequest.readValueParam("@i_cod_banco")); // código
																						// del
																						// baco
																						// originante

		wOrderMap.put("network", network); // RED ACH
		wOrderMap.put("channel", channel); // canal origen
		wOrderMap.put("module", module);
		// modulo origen
		if (logger.isInfoEnabled())
			logger.logInfo(
					"ACHTransferOrchestrationCore: @i_ciudad_orig: " + aRequest.readValueParam("@i_ciudad_orig"));
		if (aRequest.readValueParam("@i_ciudad_orig") != null)
			wOrderMap.put("originatorBranchCode", aRequest.readValueParam("@i_ciudad_orig")); // ciudad
																								// origen
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: @i_pais: " + aRequest.readValueParam("@i_pais"));
		if (aRequest.readValueParam("@i_pais") != null) {
			wOrderMap.put("originatorCountryCode", aRequest.readValueParam("@i_pais")); // pais
																						// origen
			wOrderMap.put("receiverCountryCode", aRequest.readValueParam("@i_pais")); // pais
																						// destino
		}

		wOrderMap.put("orderType", orderType); // tipo de orden

		wOrderMap.put("operationType", operationType); // tipo de operación
														// (Déposito, Pago,
														// Préstamo, Pago TC)

		// Datos del origen de la orden
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: @i_cta: " + aRequest.readValueParam("@i_cta"));
		if (aRequest.readValueParam("@i_cta") != null)
			wOrderMap.put("originatorAccountNumber", aRequest.readValueParam("@i_cta")); // número
																							// de
																							// la
																							// cuenta
																							// origen
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: @i_mon: " + aRequest.readValueParam("@i_mon"));
		if (aRequest.readValueParam("@i_mon") != null)
			wOrderMap.put("originatorCurrencyCode", aRequest.readValueParam("@i_mon")); // moneda
																						// de
																						// la
																						// cuenta
																						// origen
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: @i_prod: " + aRequest.readValueParam("@i_prod"));
		if (aRequest.readValueParam("@i_prod") != null)
			wOrderMap.put("originatorAccountProduct", aRequest.readValueParam("@i_prod")); // producto
																							// de
																							// la
																							// cuenta
																							// origen
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: @i_tipo_cta: " + aRequest.readValueParam("@i_tipo_cta"));
		if (aRequest.readValueParam("@i_tipo_cta") != null)
			wOrderMap.put("originatorAccountType", aRequest.readValueParam("@i_tipo_cta")); // tipo
																							// de
																							// cuenta
																							// origen
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: @i_ente: " + aRequest.readValueParam("@i_ente"));
		if (aRequest.readValueParam("@i_ente") != null)
			wOrderMap.put("clientCode", Integer.parseInt(aRequest.readValueParam("@i_ente"))); // codigo
																								// del
																								// cliente
																								// del
																								// titular
																								// de
																								// la
																								// cuenta
																								// de
																								// origen
		if (logger.isInfoEnabled())
			logger.logInfo(
					"ACHTransferOrchestrationCore: @i_nombre_cliente: " + aRequest.readValueParam("@i_nombre_cliente"));
		if (aRequest.readValueParam("@i_nombre_cliente") != null)
			wOrderMap.put("originatorClientName", aRequest.readValueParam("@i_nombre_cliente")); // nombre
																									// o
																									// razon
																									// social
																									// del
																									// titula
																									// de
																									// la
																									// cuenta
																									// origen
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: @i_tipo_doc: " + aRequest.readValueParam("@i_tipo_doc"));
		if (aRequest.readValueParam("@i_tipo_doc") != null)
			wOrderMap.put("originatorIdentificationType", aRequest.readValueParam("@i_tipo_doc")); // tipo
																									// de
																									// identificación
																									// del
																									// titular
																									// de
																									// la
																									// cuenta
																									// origen
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: @i_doc: " + aRequest.readValueParam("@i_doc"));
		if (aRequest.readValueParam("@i_doc") != null)
			wOrderMap.put("originatorIdentificationNumber", aRequest.readValueParam("@i_doc")); // DNI
																								// del
																								// titular
																								// de
																								// la
																								// cuenta
																								// de
																								// origen
		if (logger.isInfoEnabled())
			logger.logInfo(
					"ACHTransferOrchestrationCore: @i_tipo_persona: " + aRequest.readValueParam("@i_tipo_persona"));
		if (aRequest.readValueParam("@i_tipo_persona") != null)
			wOrderMap.put("personType", aRequest.readValueParam("@i_tipo_persona")); // Tipo
																						// de
																						// persona
		if (logger.isInfoEnabled())
			logger.logInfo(
					"ACHTransferOrchestrationCore: @i_origen_fondos: " + aRequest.readValueParam("@i_origen_fondos"));
		if (aRequest.readValueParam("@i_origen_fondos") != null)
			wOrderMap.put("originatorFunds", aRequest.readValueParam("@i_origen_fondos")); // Origen
																							// de
																							// los
																							// fondos

		// Datos de la transacción
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: @i_val: " + aRequest.readValueParam("@i_val"));
		if (aRequest.readValueParam("@i_val") != null)
			wOrderMap.put("amount", new BigDecimal(aRequest.readValueParam("@i_val"))); // Importe
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: @i_mon_trn: " + aRequest.readValueParam("@i_mon_trn"));
		if (aRequest.readValueParam("@i_mon_trn") != null)
			wOrderMap.put("currencyCode", aRequest.readValueParam("@i_mon_trn")); // Moneda
																					// de
																					// la
																					// transacción
		wOrderMap.put("feeId", Integer.parseInt(feeId.toString())); // Identificador
																	// de la
																	// comisión
																	// ACH
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: @i_excep_com: " + aRequest.readValueParam("@i_excep_com"));
		if (aRequest.readValueParam("@i_excep_com") != null)
			wOrderMap.put("feeExceptionId", Integer.parseInt(aRequest.readValueParam("@i_excep_com"))); // Identificador
																										// de
																										// la
																										// excepción
																										// de
																										// la
																										// comisión
																										// ACH
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: @i_comision: " + aRequest.readValueParam("@i_comision"));
		if (aRequest.readValueParam("@i_comision") != null)
			wOrderMap.put("fee", new BigDecimal(aRequest.readValueParam("@i_comision"))); // valor
																							// de
																							// la
																							// comisión
																							// ACH
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: @i_mon_com: " + aRequest.readValueParam("@i_mon_com"));
		if (aRequest.readValueParam("@i_mon_com") != null)
			wOrderMap.put("feeCurrencyCode", aRequest.readValueParam("@i_mon_com")); // moneda
																						// de
																						// la
																						// comisión
																						// ACH

		// Datos de la cuenta destino o destinatario
		if (logger.isInfoEnabled())
			logger.logInfo(
					"ACHTransferOrchestrationCore: @i_nombre_benef: " + aRequest.readValueParam("@i_nombre_benef"));
		if (aRequest.readValueParam("@i_nombre_benef") != null)
			wOrderMap.put("receiverClientName", aRequest.readValueParam("@i_nombre_benef")); // Nombre
																								// del
																								// titular
																								// de
																								// la
																								// cuenta
																								// destino
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: @i_doc_benef: " + aRequest.readValueParam("@i_doc_benef"));
		if (aRequest.readValueParam("@i_doc_benef") != null)
			wOrderMap.put("receiverIdentificationNumber", aRequest.readValueParam("@i_doc_benef")); // DNI
																									// del
																									// titula
																									// de
																									// la
																									// cuenta
																									// destino
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: @i_cta_des: " + aRequest.readValueParam("@i_cta_des"));
		if (aRequest.readValueParam("@i_cta_des") != null) {
			wOrderMap.put("receiverAccountNumber", aRequest.readValueParam("@i_cta_des")); // Número
																							// de
																							// la
																							// cuenta
																							// destino
																							// (Tarjeta
																							// de
																							// crédito
																							// encriptada)
			wOrderMap.put("receiverMaskAccount", aRequest.readValueParam("@i_cta_des")); // Número
																							// de
																							// la
																							// cuenta
																							// destino
																							// enmascarada
																							// (Tarjeta
																							// de
																							// crédito)
		}

		if (logger.isInfoEnabled())
			logger.logInfo(
					"ACHTransferOrchestrationCore: @i_cod_banco_des: " + aRequest.readValueParam("@i_cod_banco_des"));
		if (aRequest.readValueParam("@i_cod_banco_des") != null)
			wOrderMap.put("receiverCode", aRequest.readValueParam("@i_cod_banco_des")); // Banco
																						// destino
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: @i_concepto: " + aRequest.readValueParam("@i_concepto"));
		if (aRequest.readValueParam("@i_concepto") != null)
			wOrderMap.put("description", aRequest.readValueParam("@i_concepto")); // Motivo
																					// de
																					// la
																					// transferencia
																					// ACH
		if (logger.isInfoEnabled())
			logger.logInfo(
					"ACHTransferOrchestrationCore: @i_dest_fondos: " + aRequest.readValueParam("@i_dest_fondos"));
		if (aRequest.readValueParam("@i_dest_fondos") != null)
			wOrderMap.put("receiverFunds", aRequest.readValueParam("@i_dest_fondos")); // Destino
																						// de
																						// fodos

		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: put wOrderMap");

		wParameter.getInfo().put("order", wOrderMap);

		// Carga los datos del mensaje a ser enviados a la orquestacion
		requestAchMessage.getData().put("Info", wParameter.getInfo());
		if (logger.isDebugEnabled())
			logger.logError(" requestAchMessage...." + requestAchMessage);
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: INICIO createOrderACH");
		AchMessage AchMessageResp = achWorkflow.createOrderACH(requestAchMessage);
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: FIN createOrderACH");

		Map wResponse = ((Map) AchMessageResp.getData());
		Map wResponseInfo = ((Map) wResponse.get("Info"));

		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: " + wResponse);
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: " + wResponseInfo);
		if (logger.isInfoEnabled())
			logger.logInfo("ACHTransferOrchestrationCore: AchMessageResp " + AchMessageResp.getErrorCode());

		return AchMessageResp.getErrorCode();

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
			notification.setId("N28");
		else
			notification.setId("N29");

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

		if (!Utils.isNull(anOriginalRequest.readParam("@i_val")))
			notificationDetail.setValue(anOriginalRequest.readValueParam("@i_val"));

		if (!Utils.isNull(anOriginalRequest.readParam("@s_date")))
			notificationDetail.setDateNotification(anOriginalRequest.readValueParam("@s_date"));
		if (!Utils.isNull(anOriginalRequest.readParam("@s_ssn_branch")))
			notificationDetail.setReference(anOriginalRequest.readValueParam("@s_ssn_branch"));
		if (!Utils.isNull(anOriginalRequest.readParam("@i_nombre_benef")))
			notificationDetail.setAuxiliary1(anOriginalRequest.readValueParam("@i_nombre_benef"));

		notificationRequest.setClient(client);
		notificationRequest.setNotification(notification);
		notificationRequest.setNotificationDetail(notificationDetail);
		notificationRequest.setOriginProduct(product);
		return notificationRequest;
	}

	public ICoreServiceMonetaryTransaction getCoreServiceMonetaryTransaction() {
		// TODO Auto-generated method stub
		return coreServiceMonetaryTransaction;
	}

	@Override
	protected void addParametersRequestUpdateLocal(IProcedureRequest aProcedureRequest,
			IProcedureRequest anOriginalRequest) {
		// TODO Auto-generated method stub

	}

}
