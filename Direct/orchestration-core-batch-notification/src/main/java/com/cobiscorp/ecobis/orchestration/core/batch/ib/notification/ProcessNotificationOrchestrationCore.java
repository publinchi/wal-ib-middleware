package com.cobiscorp.ecobis.orchestration.core.batch.ib.notification;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cache.ICacheManager;
import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.components.ComponentLocator;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.db.IDBServiceFactory;
import com.cobiscorp.cobis.commons.db.IDBServiceProvider;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.exceptions.COBISInfrastructureRuntimeException;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceBatchNotification;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;
import com.cobiscorp.ecobis.orchestration.core.batch.ib.fixedterm.FixedTermBatchQueryOrchestrationCore;
import com.cobiscorp.ecobis.orchestration.core.batch.ib.loans.LoanExpirationQueryOrchestrationCore;
import com.cobiscorp.ecobis.orchestration.core.batch.ib.minimumbalance.MinimumBalanceNotificationBatch;

@Component(name = "ProcessNotificationOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "ProcessNotificationOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ProcessNotificationOrchestrationCore") })
public class ProcessNotificationOrchestrationCore extends SPJavaOrchestrationBase {

	MinimumBalanceNotificationBatch MinBalance = new MinimumBalanceNotificationBatch();
	LoanExpirationQueryOrchestrationCore LoanExpiration = new LoanExpirationQueryOrchestrationCore();
	FixedTermBatchQueryOrchestrationCore FixedTerm = new FixedTermBatchQueryOrchestrationCore();
	private static ILogger logger = LogFactory.getLogger(ProcessNotificationOrchestrationCore.class);
	protected static IDBServiceFactory dbServiceFactory;
	private static ComponentLocator componentLocator;
	private static IDBServiceProvider dbServiceProvider;
	private static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";

	@Reference(referenceInterface = ICoreServiceBatchNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceB", unbind = "unbindCoreServiceB")
	protected ICoreServiceBatchNotification coreServiceBatchNotification;

	@Reference(referenceInterface = ICoreServiceSendNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceSN", unbind = "unbindCoreServiceSN")
	protected ICoreServiceSendNotification coreServiceNotification;

	@Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreService coreService;

	@Reference(referenceInterface = ICacheManager.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "setCacheManager", unbind = "unsetCacheManager")
	private ICacheManager cacheManager;

	public void bindCoreServiceSN(ICoreServiceSendNotification service) {
		coreServiceNotification = service;
	}

	public void unbindCoreServiceSN(ICoreServiceSendNotification service) {
		coreServiceNotification = null;
	}

	public void bindCoreServiceB(ICoreServiceBatchNotification service) {
		coreServiceBatchNotification = service;
	}

	public void unbindCoreServiceB(ICoreServiceBatchNotification service) {
		coreServiceBatchNotification = null;
	}

	public void bindCoreService(ICoreService service) {
		coreService = service;
	}

	public void unbindCoreService(ICoreService service) {
		coreService = null;
	}

	public void setCacheManager(ICacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public void unsetCacheManager(ICacheManager cacheManager) {
		this.cacheManager = null;
	}

	private IProcedureResponse getProductsToNotify(int numberOfRecords, int nextRecord) {

		IProcedureRequest request = new ProcedureRequestAS();
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1875052");
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "cobis");
		request.setSpName("cob_bvirtual..sp_bv_cons_productos_notificar");

		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, "1875052");
		request.addInputParam("@i_numero_registros", ICTSTypes.SQLINT2, String.valueOf(numberOfRecords));
		request.addInputParam("@i_siguiente", ICTSTypes.SQLINT2, String.valueOf(nextRecord));
		request.addOutputParam("@o_total_registros", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_registro_max", ICTSTypes.SQLINT4, "0");

		IProcedureResponse response = executeCoreBanking(request);
		if (logger.isDebugEnabled())
			logger.logDebug("response de getProductsToNotify  " + response);
		return response;

	}

	public IProcedureResponse executeProductsToNotify(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse responseProductsToNotify = getProductsToNotify(
				Integer.parseInt(request.readValueParam("@i_numero_registros")),
				Integer.parseInt(request.readValueParam("@i_siguiente")));
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		IProcedureResponse responseProcessing = new ProcedureResponseAS();
		String NotificationType = null;
		Integer NotificationProduct = 0;
		Integer NotificationId = 0;
		if (responseProductsToNotify == null) {
			if (logger.isDebugEnabled())
				logger.logDebug("responseProductsToNotify is null");
		}

		if (responseProductsToNotify.getResultSet(1) == null) {
			if (logger.isDebugEnabled())
				logger.logDebug("responseProductsToNotify.getResultSet(1) is null");
		}

		IResultSetBlock resulsetOrigin = responseProductsToNotify.getResultSet(1);
		IResultSetRow[] rowsTemp = resulsetOrigin.getData().getRowsAsArray();

		if (rowsTemp.length > 0) {
			for (IResultSetRow iResultSetRow : rowsTemp) {
				IResultSetRowColumnData[] rows = iResultSetRow.getColumnsAsArray();
				anOriginalRequest.addInputParam("@i_sarta", ICTSTypes.SQLINT4, "18010");
				anOriginalRequest.addInputParam("@i_batch", ICTSTypes.SQLINT4, request.readValueParam("@i_batch"));
				anOriginalRequest.addInputParam("@i_secuencial", ICTSTypes.SQLINT4, "1");
				anOriginalRequest.addInputParam("@i_corrida", ICTSTypes.SQLINT4, "1");
				anOriginalRequest.addInputParam("@i_intento", ICTSTypes.SQLINT4, "1");

				anOriginalRequest.addInputParam("@i_cuenta", ICTSTypes.SQLVARCHAR, rows[3].getValue());
				anOriginalRequest.addInputParam("@i_producto", ICTSTypes.SQLINT4, rows[1].getValue());
				anOriginalRequest.addInputParam("@i_limite", ICTSTypes.SQLVARCHAR, rows[5].getValue());
				anOriginalRequest.addInputParam("@i_condicion", ICTSTypes.SQLVARCHAR, rows[6].getValue());
				anOriginalRequest.addInputParam("@i_cod_cliente", ICTSTypes.SQLVARCHAR, rows[10].getValue());

				anOriginalRequest.addInputParam("@i_des_producto", ICTSTypes.SQLVARCHAR, rows[9].getValue());
				anOriginalRequest.addInputParam("@i_alias", ICTSTypes.SQLVARCHAR, rows[7].getValue());
				anOriginalRequest.addInputParam("@i_cultura", ICTSTypes.SQLVARCHAR, rows[8].getValue());
				anOriginalRequest.addInputParam("@i_notificacion", ICTSTypes.SQLVARCHAR, rows[2].getValue());
				anOriginalRequest.addInputParam("@i_fecha_proceso", ICTSTypes.SQLVARCHAR,
						request.readValueParam("@i_fecha_proceso"));
				// anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT4,
				// request.readValueParam("@s_ofi"));
				anOriginalRequest.addInputParam("@i_filial", ICTSTypes.SQLINT4, request.readValueParam("@i_filial"));

				NotificationType = rows[2].getValue().trim();
				NotificationId = Integer.parseInt(rows[0].getValue());
				NotificationProduct = Integer.parseInt(rows[1].getValue());

				if (NotificationType.equals("N2")) {
					anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SQLVARCHAR, "1889994");
					MinBalance.executeJavaOrchestration(anOriginalRequest, aBagSPJavaOrchestration);
				}
				if (NotificationType.equals("N4")) {
					anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SQLVARCHAR, "1889994");
					MinBalance.executeJavaOrchestration(anOriginalRequest, aBagSPJavaOrchestration);
				}

				if (NotificationType.equals("N6") && NotificationProduct.equals(14)) {
					anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SQLVARCHAR, "1889995");
					FixedTerm.executeJavaOrchestration(anOriginalRequest, aBagSPJavaOrchestration);
				}

				if (NotificationType.equals("N5")) {
					anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SQLVARCHAR, "1889996");
					LoanExpiration.executeJavaOrchestration(anOriginalRequest, aBagSPJavaOrchestration);
				}

				if (NotificationType.equals("N7")) {
					anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SQLVARCHAR, "1889997");
					LoanExpiration.executeJavaOrchestration(anOriginalRequest, aBagSPJavaOrchestration);
				}

			}
		}
		responseProcessing.setReturnCode(0);
		responseProcessing.addParam("@o_siguiente", ICTSTypes.SQLINT4, 0, NotificationId.toString());

		//
		loadLocatorConfiguration();
		updateProcesarBatch(responseProductsToNotify, aBagSPJavaOrchestration,
				Integer.parseInt(request.readValueParam("@i_siguiente")), NotificationId);
		//

		return responseProcessing;

	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest originalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, originalRequest);
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreServiceNotification", coreServiceNotification);
		mapInterfaces.put("coreService", coreService);
		mapInterfaces.put("coreServiceBatchNotification", coreServiceBatchNotification);

		Utils.validateComponentInstance(mapInterfaces);

		MinBalance.setBatchNotificationService(coreServiceBatchNotification);
		MinBalance.setNotificationService(coreServiceNotification);
		MinBalance.setCoreService(coreService);
		MinBalance.setCacheManager(cacheManager);
		FixedTerm.setBatchNotificationService(coreServiceBatchNotification);
		FixedTerm.setNotificationService(coreServiceNotification);
		FixedTerm.setCoreService(coreService);
		// FixedTerm.setCacheManager(cacheManager);
		LoanExpiration.setBatchNotificationService(coreServiceBatchNotification);
		LoanExpiration.setNotificationService(coreServiceNotification);
		LoanExpiration.setCoreService(coreService);
		// LoanExpiration.setCacheManager(cacheManager);
		return executeProductsToNotify(originalRequest, aBagSPJavaOrchestration);

	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	public void loadLocatorConfiguration() {

		componentLocator = ComponentLocator.getInstance(this);
		dbServiceFactory = (IDBServiceFactory) componentLocator.find(IDBServiceFactory.class);
		String dbms = "SQLCTS";
		String dbmsServiceProvider = "DataSource";
		dbServiceProvider = dbServiceFactory.getDBServiceProvider(dbms, dbmsServiceProvider);
	}

	private void updateProcesarBatch(IProcedureResponse aProcedureResponse, Map<String, Object> aBagSPJavaOrchestration,
			Integer secuencial, Integer lastRecord) {
		/*****
		 * PROCESO PARA ACTUALIZAR TABLA cob_bvirtual..bv_proceso_batch
		 *******/
		Integer registroMaximo = 0;
		IProcedureRequest orginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		List<String> queryList = new ArrayList<String>();
		String query = null;

		if (aProcedureResponse.readValueParam("@o_registro_max") != null)
			registroMaximo = Integer.parseInt(aProcedureResponse.readValueParam("@o_registro_max"));

		if (secuencial == 0) {
			query = " update cob_bvirtual..bv_proceso_batch " + " set pb_tot_reg_procesar = "
					+ aProcedureResponse.readValueParam("@o_total_registros") + " ,pb_reg_max = " + registroMaximo
					+ " where pb_cod = " + orginalRequest.readValueParam("@i_batch");
			queryList.add(query);
		}

		if (secuencial < registroMaximo) {
			query = " update cob_bvirtual..bv_proceso_batch set pb_secuencial = " + lastRecord + " where pb_cod = "
					+ orginalRequest.readValueParam("@i_batch");
			queryList.add(query);
		}

		if (queryList == null)
			if (logger.isDebugEnabled())
				logger.logDebug("Query List is null ");
		if (queryList.size() > 0) {
			executeBdd(queryList);
		} else if (logger.isDebugEnabled())
			logger.logDebug("Query List Size = 0");
	}

	private void executeBdd(List<String> inserts) {
		String methodInfo = "[executeBdd]";
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = dbServiceProvider.getDBConnection();
			connection.setAutoCommit(false);
			stmt = connection.createStatement();
			for (String insert : inserts)
				stmt.addBatch(insert);
			stmt.executeBatch();
			connection.commit();
			connection.setAutoCommit(true);
		} catch (Exception e) {
			throw new COBISInfrastructureRuntimeException(
					methodInfo + "No se pudo ejecutar las sentencias sql " + e.getMessage());
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (connection != null)
					connection.close();
			} catch (Exception e) {
				throw new COBISInfrastructureRuntimeException(methodInfo + "No se puede cerrar la conexion a la BDD");
			}
		}
	}

}
