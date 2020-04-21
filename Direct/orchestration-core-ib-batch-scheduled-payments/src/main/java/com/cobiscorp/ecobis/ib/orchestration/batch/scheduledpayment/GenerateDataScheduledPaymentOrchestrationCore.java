package com.cobiscorp.ecobis.ib.orchestration.batch.scheduledpayment;

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

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.commons.components.ComponentLocator;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.db.IDBServiceFactory;
import com.cobiscorp.cobis.commons.db.IDBServiceProvider;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.exceptions.COBISInfrastructureRuntimeException;
import com.cobiscorp.cobis.commons.log.LogFactory;
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
import com.cobiscorp.ecobis.ib.application.dtos.BatchScheduledPaymentRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BatchScheduledPaymentResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ScheduledPaymentRequest;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Batch;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceBatchScheduledPayment;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;

import java.sql.Connection;
import java.sql.Statement;

/**
 * 
 * @author itorres
 * @since Ago 06, 2014
 * @version 1.0.0
 */

@Component(name = "GenerateDataScheduledPaymentOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "GenerateDataScheduledPaymentOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "GenerateDataScheduledPaymentOrchestrationCore") })

public class GenerateDataScheduledPaymentOrchestrationCore extends QueryBaseTemplate {

	protected static String dbms;
	private static List<Integer> dataString = new ArrayList<Integer>();
	private static List<Integer> dataInt = new ArrayList<Integer>();
	private static IDBServiceProvider dbServiceProvider;
	protected static IDBServiceFactory dbServiceFactory;
	private String tableName = "cob_bvirtual..bv_procesar_cliente";
	private String tableNameEnte = "cobis..bv_ente_afiliado";
	private String tableNameAccount = "cobis..bv_cuentas_ente_pago_prog";
	private static ComponentLocator componentLocator;
	private String operacion;
	private int numBatch;

	ILogger logger = LogFactory.getLogger(GenerateDataScheduledPaymentOrchestrationCore.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate#
	 * validateLocalExecution(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	protected IProcedureResponse validateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		IProcedureResponse response = new ProcedureResponseAS();
		response.setReturnCode(0);
		return response;
	}

	@Reference(referenceInterface = ICoreServiceBatchScheduledPayment.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceBatchScheduledPayment coreService;

	protected void bindCoreService(ICoreServiceBatchScheduledPayment service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	protected void unbindCoreService(ICoreServiceBatchScheduledPayment service) {
		coreService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		if (logger.isInfoEnabled())
			logger.logInfo(
					"Component-Name: " + this.getClass().getSimpleName() + ", Component-Version: $$project.version$$");
	}

	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		String messageError = null;
		String messageLog = null;
		String queryName = null;
		BatchScheduledPaymentResponse aBatchScheduledPaymentResponse = new BatchScheduledPaymentResponse();
		BatchScheduledPaymentRequest aBatchScheduledPaymentRequest = transformToBatchScheduledPaymentRequest(
				request.clone());

		try {
			messageError = "executeBatchScheduledPayment: ERROR EXECUTING SERVICE";
			messageLog = "executeBatchScheduledPayment " + aBatchScheduledPaymentRequest.getNext();
			queryName = "executeBatchScheduledPayment";
			aBatchScheduledPaymentRequest.setOriginalRequest(request);

			aBatchScheduledPaymentResponse = coreService.executeBatchScheduledPayment(aBatchScheduledPaymentRequest);
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
		aBagSPJavaOrchestration.put(QUERY_NAME, queryName);

		IProcedureResponse response = transformProcedureResponse(aBatchScheduledPaymentResponse,
				aBagSPJavaOrchestration);

		/*****
		 * PROCESO PARA ACTUALIZAR TABLA cob_bvirtual..bv_proceso_batch
		 *******/
		List<String> queryList = new ArrayList<String>();
		String query = null;

		if (aBatchScheduledPaymentRequest.getNext() == 0) {
			query = " update cob_bvirtual..bv_proceso_batch " + " set pb_tot_reg_procesar = "
					+ aBatchScheduledPaymentResponse.getTotalRecords() + " ,pb_reg_max = "
					+ aBatchScheduledPaymentResponse.getMaxRecord() + " where pb_cod = " + numBatch;
			queryList.add(query);
		}

		if (aBatchScheduledPaymentRequest.getNext() < aBatchScheduledPaymentResponse.getTotalRecords()) {
			query = " update cob_bvirtual..bv_proceso_batch set pb_secuencial = "
					+ aBatchScheduledPaymentResponse.getNext() + " where pb_cod = " + numBatch;
			queryList.add(query);
		}
		if (logger.isInfoEnabled())
			logger.logInfo(" QUERYS UPDATE BV_PROCESO_BATCH --> " + queryList.toString());
		executeBdd(queryList, "SQLCTS");

		return response;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// Valida Inyección de dependencias
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		Utils.validateComponentInstance(mapInterfaces);
		try {
			aBagSPJavaOrchestration.put(LOG_MESSAGE, "GENERACION DE DATOS DE CLIENTES NUEVOS");
			executeStepsQueryBase(anOrginalRequest, aBagSPJavaOrchestration);

			return processResponse(anOrginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		}
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureRespFinal = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
		return wProcedureRespFinal;
	}

	/******************
	 * Transformación de ProcedureRequest a GenerateCustomerDataRequest
	 ********************/
	private BatchScheduledPaymentRequest transformToBatchScheduledPaymentRequest(IProcedureRequest aRequest) {
		BatchScheduledPaymentRequest aBatchScheduledPaymentRequest = new BatchScheduledPaymentRequest();

		Batch aBatch = new Batch();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;

		messageError = aRequest.readValueParam("@i_operacion") == null ? " - @i_operacion can't be null" : "";
		messageError += aRequest.readValueParam("@i_siguiente") == null ? " - @i_siguiente can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		operacion = aRequest.readValueParam("@i_operacion");
		numBatch = Integer.parseInt(aRequest.readValueParam("@i_batch"));
		aBatch.setBatch(Integer.parseInt(aRequest.readValueParam("@i_batch")));
		aBatch.setSarta(Integer.parseInt(aRequest.readValueParam("@i_sarta")));
		aBatch.setIntento(Integer.parseInt(aRequest.readValueParam("@i_intento")));
		aBatch.setCorrida(Integer.parseInt(aRequest.readValueParam("@i_corrida")));
		aBatch.setSecuencial(Integer.parseInt(aRequest.readValueParam("@i_secuencial")));

		aBatchScheduledPaymentRequest.setBatch(aBatch);
		aBatchScheduledPaymentRequest.setRecordNumber(Integer.parseInt(aRequest.readValueParam("@i_numRows")));
		aBatchScheduledPaymentRequest.setOperation(aRequest.readValueParam("@i_operacion"));
		aBatchScheduledPaymentRequest.setNext(Integer.parseInt(aRequest.readValueParam("@i_siguiente")));

		if (aBatchScheduledPaymentRequest.getNext() == 0 && operacion.equals("E")) {
			List<String> deleteList = new ArrayList<String>();
			String delete;

			delete = " delete " + tableNameEnte;
			deleteList.add(delete);

			delete = " delete " + tableNameAccount;
			deleteList.add(delete);
			executeBdd(deleteList, "SYBCTS");
		}

		return aBatchScheduledPaymentRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/
	private IProcedureResponse transformProcedureResponse(BatchScheduledPaymentResponse aBatchScheduledPaymentResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		List<String> inserts = new ArrayList<String>();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		if (aBatchScheduledPaymentResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aBatchScheduledPaymentResponse.getMessages())); // COLOCA
																							// ERRORES
																							// COMO
																							// RESPONSE
																							// DE
																							// LA
																							// TRANSACCIÓN
			Utils.returnException(aBatchScheduledPaymentResponse.getMessages());
		} else {
			if (logger.isInfoEnabled())
				logger.logInfo("Tranforma el procedure");
			// Agregar Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("ente_mis", ICTSTypes.SQLINT4, 10));
			if (operacion.equals("E")) {
				metaData.addColumnMetaData(new ResultSetHeaderColumn("fecha_afiliacion", ICTSTypes.SQLVARCHAR, 10));
			} else {
				metaData.addColumnMetaData(new ResultSetHeaderColumn("producto", ICTSTypes.SQLINT4, 10));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("moneda", ICTSTypes.SQLINT4, 10));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("cuenta", ICTSTypes.SQLVARCHAR, 24));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("tipo", ICTSTypes.SQLVARCHAR, 10));
			}

			for (ScheduledPaymentRequest aScheduledPaymentRequest : aBatchScheduledPaymentResponse
					.getListScheduledPayment()) {
				if (!IsValidEntityIntegratedResponse(aScheduledPaymentRequest))
					return null;

				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false,
						aScheduledPaymentRequest.getId() != null ? aScheduledPaymentRequest.getId().toString() : ""));
				if (operacion.equals("E")) {
					row.addRowData(2,
							new ResultSetRowColumnData(false, aScheduledPaymentRequest.getModifiedDate() != null
									? aScheduledPaymentRequest.getModifiedDate() : ""));
				} else {
					row.addRowData(2,
							new ResultSetRowColumnData(false,
									aScheduledPaymentRequest.getDebitProduct().getProductId() != null
											? aScheduledPaymentRequest.getDebitProduct().getProductId().toString()
											: ""));
					row.addRowData(3,
							new ResultSetRowColumnData(false,
									aScheduledPaymentRequest.getDebitProduct().getCurrency().getCurrencyId() != null
											? aScheduledPaymentRequest.getDebitProduct().getCurrency().getCurrencyId()
													.toString()
											: ""));
					row.addRowData(4, new ResultSetRowColumnData(false, aScheduledPaymentRequest.getAccount() != null
							? aScheduledPaymentRequest.getAccount() : ""));
					row.addRowData(5, new ResultSetRowColumnData(false,
							aScheduledPaymentRequest.getType() != null ? aScheduledPaymentRequest.getType() : ""));
				}

				data.addRow(row);
			}
			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock);

			wProcedureResponse.setReturnCode(aBatchScheduledPaymentResponse.getReturnCode());

			IResultSetBlock resultSet = wProcedureResponse.getResultSet(1);
			IResultSetRow[] dataInsert = wProcedureResponse.getResultSet(1).getData().getRowsAsArray();
			IResultSetHeader headerInsert = resultSet.getMetaData();

			Integer lastEnte = 0;
			for (IResultSetRow iResultSetRow : dataInsert) {
				lastEnte = aBatchScheduledPaymentResponse.getNext();
				if (operacion.equals("E")) {
					inserts.add(prepareInsertBatch(tableNameEnte, iResultSetRow, headerInsert));
				} else {
					inserts.add(prepareInsertBatch(tableNameAccount, iResultSetRow, headerInsert));
				}
			}

			aBagSPJavaOrchestration.put("LAST_RECORD", lastEnte);

			if (!inserts.isEmpty()) {
				executeBdd(inserts, "SYBCTS");
			} else {

			}
		}

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}

	// Prepara las sentencias para el insert
	private String prepareInsertBatch(String tableName, IResultSetRow resultRow, IResultSetHeader header) {
		String methodInfo = "[prepareBatch]";
		String insert = "insert into " + tableName + " values (";

		loadData();
		int columns = resultRow.getColumnsNumber();
		for (int i = 1; i <= columns; i++) {
			String data = resultRow.getRowData(i).getValue();
			int dataType = header.getColumnMetaData(i).getType();

			if ((data == null) || (data.equals("")) || (data.equals("null"))) {
				if (i == 1)
					insert = insert + "null";
				else
					insert = insert + ", null";
			} else if (dataInt.contains(Integer.valueOf(dataType))) {
				if (i == 1)
					insert = insert + data;
				else
					insert = insert + "," + data;

			} else if (dataString.contains(Integer.valueOf(dataType))) {
				if (i == 1)
					insert = insert + "'" + data + "'";
				else
					insert = insert + ",'" + data + "'";
			}
		}
		insert = insert + ")";
		if (logger.isDebugEnabled())
			logger.logDebug(methodInfo + "insert generado para la tabla " + tableName + insert);
		return insert;
	}

	private void loadData() {
		dataString.add(Integer.valueOf(47));
		dataString.add(Integer.valueOf(39));
		dataString.add(Integer.valueOf(35));

		dataInt.add(Integer.valueOf(106));
		dataInt.add(Integer.valueOf(62));
		dataInt.add(Integer.valueOf(109));
		dataInt.add(Integer.valueOf(48));
		dataInt.add(Integer.valueOf(52));
		dataInt.add(Integer.valueOf(56));
		dataInt.add(Integer.valueOf(38));
		dataInt.add(Integer.valueOf(60));
		dataInt.add(Integer.valueOf(122));
		dataInt.add(Integer.valueOf(110));
		dataInt.add(Integer.valueOf(108));
	}

	private void executeBdd(List<String> inserts, String base) {
		String methodInfo = "[executeBdd]";
		Connection connection = null;

		componentLocator = ComponentLocator.getInstance(this);
		dbServiceFactory = (IDBServiceFactory) componentLocator.find(IDBServiceFactory.class);
		dbServiceProvider = dbServiceFactory.getDBServiceProvider(base, "DataSource");
		Statement stmt = null;
		try {
			connection = dbServiceProvider.getDBConnection();
			connection.setAutoCommit(false);
			stmt = connection.createStatement();
			for (String insert : inserts) {
				stmt.addBatch(insert);
			}
			stmt.executeBatch();
			connection.commit();
			connection.setAutoCommit(true);
		} catch (Exception e) {
			if (operacion.equals("E")) {
				tableName = tableNameEnte;
			} else {
				tableName = tableNameAccount;
			}
			throw new COBISInfrastructureRuntimeException(
					methodInfo + "No se pudieron guardar los datos en " + tableName + e);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (connection != null)
					connection.close();
			} catch (Exception e) {
				throw new COBISInfrastructureRuntimeException(
						methodInfo + "No se puede cerrar la conexion a la BDD: " + dbms + e);
			}
		}
	}

	// Valida que la data retornada no sea nula
	private boolean IsValidEntityIntegratedResponse(ScheduledPaymentRequest aScheduledPaymentRequest) {
		String messageError = null;

		messageError = aScheduledPaymentRequest.getId() == null ? "Ente can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);
		return true;
	}
}
