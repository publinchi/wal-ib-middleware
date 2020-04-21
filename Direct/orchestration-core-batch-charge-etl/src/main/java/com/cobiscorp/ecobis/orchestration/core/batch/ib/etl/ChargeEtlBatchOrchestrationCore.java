package com.cobiscorp.ecobis.orchestration.core.batch.ib.etl;

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

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.components.ComponentLocator;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.db.IDBServiceFactory;
import com.cobiscorp.cobis.commons.db.IDBServiceProvider;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.exceptions.COBISInfrastructureRuntimeException;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.BatchChargeEtlRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BatchChargeEtlResponse;
import com.cobiscorp.ecobis.ib.application.dtos.BatchEtlTotalResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BatchEtlTotal;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Entity;
import com.cobiscorp.ecobis.ib.orchestration.dtos.EntityService;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Log;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceBatchChargeEtl;

/**
 * @author dmorla
 * @since June 24, 2015
 * @version 1.0.0
 */
@Component(name = "ChargeEtlBatchOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "ChargeEtlBatchOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ChargeEtlBatchOrchestrationCore") })
public class ChargeEtlBatchOrchestrationCore extends SPJavaOrchestrationBase {

	ILogger logger = this.getLogger();
	private static final String CLASS_NAME = "ChargeEtlBatchOrchestrationCore--->";
	static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	protected static final String RECORDS = "RECORDS";
	protected static final String LAST_RECORD = "LAST_RECORD";
	private static IDBServiceProvider dbServiceProvider;
	private static List<Integer> dataString;
	private static List<Integer> dataInt;
	private static List<Integer> dataDate;
	protected static String dbms;
	protected static IDBServiceFactory dbServiceFactory;
	private static ComponentLocator componentLocator;
	protected static int platform;
	protected static final String COBIS_CONTEXT = "COBIS";

	/* FIN DE COLUMNAS RESULSET */

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceBatchChargeEtl.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	private ICoreServiceBatchChargeEtl coreServiceBatchChargeEtl;

	/**
	 * Instance Service Interface
	 * 
	 * @param service
	 */
	protected void bindCoreService(ICoreServiceBatchChargeEtl service) {
		coreServiceBatchChargeEtl = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	protected void unbindCoreService(ICoreServiceBatchChargeEtl service) {
		coreServiceBatchChargeEtl = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Start-executeJavaOrchestration");

		try {

			BatchChargeEtlResponse wChargeEtlResponse = null;
			BatchEtlTotalResponse wEtlTotalResponse = null;
			IProcedureResponse wProcedureResponseCharge = new ProcedureResponseAS();
			// IProcedureResponse wProcedureResponseAssort = new
			// ProcedureResponseAS();

			Map<String, Object> mapInterfaces = new HashMap<String, Object>();
			mapInterfaces.put("coreServiceBatchChargeEtl", coreServiceBatchChargeEtl);
			Utils.validateComponentInstance(mapInterfaces);

			BatchChargeEtlRequest wBatchChargeEtlRequest = transformChargeRequest(anOriginalRequest.clone());
			// validacion para ejecutar la implementacion

			List<String> queryList = new ArrayList<String>();
			String query = null;
			loadLocatorConfiguration();

			if (wBatchChargeEtlRequest.getOperation().equals("E")) { // Extractor
				wChargeEtlResponse = coreServiceBatchChargeEtl.chargeEtl(wBatchChargeEtlRequest);
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, wChargeEtlResponse);
				wProcedureResponseCharge = transformProcedureResponseCharge(aBagSPJavaOrchestration);
				query = " delete cobis..bv_ente_servicio_etl ";
				queryList.add(query);
				query = " delete cobis..bv_ente_etl ";
				queryList.add(query);
				executeBdd(queryList);
				Insert(wProcedureResponseCharge, "insert into cobis..bv_ente_servicio_etl values (", 1);
				Insert(wProcedureResponseCharge, "insert into cobis..bv_ente_etl values (", 2);
				Insert(wProcedureResponseCharge, "insert into cobis..bv_log_etl values (", 3);
			} else if (wBatchChargeEtlRequest.getOperation().equals("T")) { // Totales
																			// Cuadre
				wEtlTotalResponse = coreServiceBatchChargeEtl.generateTotalEtl(wBatchChargeEtlRequest);
				aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, wEtlTotalResponse);
				wProcedureResponseCharge = transformProcedureResponseTotal(aBagSPJavaOrchestration);
				query = " delete cobis..bv_cuadre_etl ";
				queryList.add(query);
				executeBdd(queryList);
				Insert(wProcedureResponseCharge, "insert into cobis..bv_cuadre_etl values (", 1);
			}

			return wProcedureResponseCharge;

		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.logInfo("*********  Error en " + e.getMessage(), e);
			}
			e.printStackTrace();

			IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
			ErrorBlock eb = new ErrorBlock(-1, "Service is not available");
			wProcedureRespFinal.addResponseBlock(eb);
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
			wProcedureRespFinal.setReturnCode(-1);
			return wProcedureRespFinal;
		}
	}

	// Transform Request
	private BatchChargeEtlRequest transformChargeRequest(IProcedureRequest aRequest) {
		BatchChargeEtlRequest wlChargeEtlRequest = new BatchChargeEtlRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("<<<Procedure Request to Transform->>>" + aRequest.getProcedureRequestAsString());

		if (aRequest.readValueParam("@i_operacion") != null)
			wlChargeEtlRequest.setOperation(aRequest.readValueParam("@i_operacion"));

		if (aRequest.readValueParam("@i_fecha_proceso") != null)
			wlChargeEtlRequest.setDateProcess(aRequest.readValueParam("@i_fecha_proceso"));
		if (aRequest.readValueParam("@i_registros") != null)
			wlChargeEtlRequest.setTotal(Integer.parseInt(aRequest.readValueParam("@i_registros")));
		if (aRequest.readValueParam("@o_secuencial") != null)
			wlChargeEtlRequest.setNext(Integer.parseInt(aRequest.readValueParam("@o_secuencial")));

		return wlChargeEtlRequest;

	}

	private IProcedureResponse transformProcedureResponseCharge(Map<String, Object> aBagSPJavaOrchestration) {

		Integer SequentialId = 0;
		BatchChargeEtlResponse wChargeEtlResponse = (BatchChargeEtlResponse) aBagSPJavaOrchestration
				.get(RESPONSE_TRANSACTION);

		if (wChargeEtlResponse == null) {
			if (logger.isInfoEnabled())
				logger.logInfo("transformToRefreshResponse --> wBalanceChargeEtl null");
			return null;
		}
		IProcedureResponse response = new ProcedureResponseAS();
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("EMPRESA", ICTSTypes.SYBINT4, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("ENTE", ICTSTypes.SYBINT4, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("SERVICIO", ICTSTypes.SYBINT4, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("ESTADO", ICTSTypes.SYBVARCHAR, 1));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("CREADOR", ICTSTypes.SYBVARCHAR, 30));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("FECHA", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("OFICINA", ICTSTypes.SYBINT4, 4));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("CATEGORIA", ICTSTypes.SYBINT4, 4));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("AUX", ICTSTypes.SYBINT4, 4));

		for (EntityService wEntityService : wChargeEtlResponse.getEntityServiceCollection()) {
			IResultSetRow row = new ResultSetRow();
			// SequentialId = wAcountsRefreshBalance.getSecuencial();
			row.addRowData(1, new ResultSetRowColumnData(false, String.valueOf(wEntityService.getCompany())));
			row.addRowData(2, new ResultSetRowColumnData(false, String.valueOf(wEntityService.getEntity())));
			row.addRowData(3, new ResultSetRowColumnData(false, wEntityService.getService().toString()));
			row.addRowData(4, new ResultSetRowColumnData(false, wEntityService.getState().toString()));
			if (wEntityService.getCreator() != null)
				row.addRowData(5, new ResultSetRowColumnData(false, wEntityService.getCreator().toString()));
			if (wEntityService.getDate() != null)
				row.addRowData(6, new ResultSetRowColumnData(false, wEntityService.getDate()));
			if (wEntityService.getOffice() != null)
				row.addRowData(7, new ResultSetRowColumnData(false, wEntityService.getOffice().toString()));

			if (wEntityService.getCategory() != null)
				row.addRowData(8, new ResultSetRowColumnData(false, wEntityService.getCategory().toString()));

			row.addRowData(9, new ResultSetRowColumnData(false,
					wEntityService.getAux() == null ? "" : wEntityService.getAux().toString()));

			data.addRow(row);
		}

		IResultSetBlock resultSetBlock = new ResultSetBlock(metaData, data);

		response.addResponseBlock(resultSetBlock);

		// entity

		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();

		metaData2.addColumnMetaData(new ResultSetHeaderColumn("EMPRESA", ICTSTypes.SYBINT4, 8));
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("ENTE", ICTSTypes.SYBINT4, 8));
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("ENTE_MIS", ICTSTypes.SYBINT4, 8));

		for (Entity wEntity : wChargeEtlResponse.getEntityCollection()) {
			IResultSetRow row = new ResultSetRow();
			// SequentialId = wAcountsRefreshBalance.getSecuencial();
			row.addRowData(1, new ResultSetRowColumnData(false, String.valueOf(wEntity.getCompany())));
			row.addRowData(2, new ResultSetRowColumnData(false, String.valueOf(wEntity.getEnte())));
			row.addRowData(3, new ResultSetRowColumnData(false, wEntity.getCodCustomer().toString()));

			data2.addRow(row);
		}

		IResultSetBlock resultSetBlock2 = new ResultSetBlock(metaData2, data2);

		response.addResponseBlock(resultSetBlock2);

		// log

		IResultSetHeader metaData3 = new ResultSetHeader();
		IResultSetData data3 = new ResultSetData();

		metaData3.addColumnMetaData(new ResultSetHeaderColumn("EMPRESA", ICTSTypes.SYBINT4, 8));
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("TRANSACCION", ICTSTypes.SYBINT4, 8));
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("ENTE", ICTSTypes.SYBINT4, 8));
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("SERVICIO", ICTSTypes.SYBINT4, 8));
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("ESTATUS", ICTSTypes.SYBCHAR, 2));
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("FECHA", ICTSTypes.SYBCHAR, 10));
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("SECUENCIAL", ICTSTypes.SYBINT4, 8));
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("HORA", ICTSTypes.SYBCHAR, 20));
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("PRODUCTO", ICTSTypes.SYBINT4, 8));
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("MONEDA", ICTSTypes.SYBINT4, 8));
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("CUENTA", ICTSTypes.SYBVARCHAR, 32));
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("COMISION", ICTSTypes.SYBMONEY, 10));
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("OFICINA", ICTSTypes.SYBINT4, 8));
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("AUX", ICTSTypes.SYBINT4, 8));
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("ORIGEN", ICTSTypes.SYBVARCHAR, 70));
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("DESTINO", ICTSTypes.SYBVARCHAR, 70));

		for (Log wLog : wChargeEtlResponse.getLogColletion()) {
			IResultSetRow row = new ResultSetRow();
			SequentialId = wLog.getSequency();

			row.addRowData(1, new ResultSetRowColumnData(false, String.valueOf(wLog.getCompany())));
			row.addRowData(2, new ResultSetRowColumnData(false, String.valueOf(wLog.getTransaction())));
			row.addRowData(3, new ResultSetRowColumnData(false, wLog.getEntity().toString()));
			row.addRowData(4, new ResultSetRowColumnData(false, String.valueOf(wLog.getService())));
			row.addRowData(5, new ResultSetRowColumnData(false, String.valueOf(wLog.getStatus())));
			row.addRowData(6, new ResultSetRowColumnData(false, wLog.getDate()));
			row.addRowData(7, new ResultSetRowColumnData(false, wLog.getSequency().toString()));
			row.addRowData(8, new ResultSetRowColumnData(false, wLog.getHour()));
			row.addRowData(9,
					new ResultSetRowColumnData(false, wLog.getProduct() == null ? "" : wLog.getProduct().toString()));
			row.addRowData(10, new ResultSetRowColumnData(false, String.valueOf(wLog.getMoney())));
			row.addRowData(11, new ResultSetRowColumnData(false, wLog.getAccount() == null ? "" : wLog.getAccount()));
			row.addRowData(12, new ResultSetRowColumnData(false, wLog.getFee().toString()));
			row.addRowData(13, new ResultSetRowColumnData(false, wLog.getOffice().toString()));
			row.addRowData(14,
					new ResultSetRowColumnData(false, wLog.getAux() == null ? "" : wLog.getAux().toString()));
			row.addRowData(15, new ResultSetRowColumnData(false,
					wLog.getOriginatorFunds() == null ? "" : wLog.getOriginatorFunds()));
			row.addRowData(16,
					new ResultSetRowColumnData(false, wLog.getReceiverFunds() == null ? "" : wLog.getReceiverFunds()));

			data3.addRow(row);
		}

		IResultSetBlock resultSetBlock3 = new ResultSetBlock(metaData3, data3);

		response.addResponseBlock(resultSetBlock3);

		response.addParam("@o_secuencial", ICTSTypes.SQLINT4, SequentialId.toString().length(),
				SequentialId.toString());

		return response;
	}

	private IProcedureResponse transformProcedureResponseTotal(Map<String, Object> aBagSPJavaOrchestration) {
		BatchEtlTotalResponse wEtlTotalResponse = (BatchEtlTotalResponse) aBagSPJavaOrchestration
				.get(RESPONSE_TRANSACTION);

		IProcedureResponse response = new ProcedureResponseAS();
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("empresa", ICTSTypes.SYBINT4, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("fecha_proceso", ICTSTypes.SYBVARCHAR, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("modulo", ICTSTypes.SYBINT4, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("tabla_fuente", ICTSTypes.SYBVARCHAR, 1));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("campo_criterio", ICTSTypes.SYBVARCHAR, 30));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("valor_criterio", ICTSTypes.SYBVARCHAR, 10));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("num_reg", ICTSTypes.SYBINT4, 4));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("valor", ICTSTypes.SYBMONEY, 4));

		for (BatchEtlTotal wEtlTotal : wEtlTotalResponse.getEtlTotalCollection()) {
			IResultSetRow row = new ResultSetRow();

			row.addRowData(1, new ResultSetRowColumnData(false, String.valueOf(wEtlTotal.getCompany())));
			row.addRowData(2, new ResultSetRowColumnData(false, String.valueOf(wEtlTotal.getProcessDate())));
			row.addRowData(3, new ResultSetRowColumnData(false, String.valueOf(wEtlTotal.getModule())));
			row.addRowData(4, new ResultSetRowColumnData(false, String.valueOf(wEtlTotal.getTable())));
			row.addRowData(5, new ResultSetRowColumnData(false, String.valueOf(wEtlTotal.getCriteria())));
			row.addRowData(6, new ResultSetRowColumnData(false, String.valueOf(wEtlTotal.getCriteriaValue())));
			row.addRowData(7, new ResultSetRowColumnData(false, String.valueOf(wEtlTotal.getNumberOfRecords())));
			row.addRowData(8, new ResultSetRowColumnData(false, String.valueOf(wEtlTotal.getValue())));

			data.addRow(row);

		}
		IResultSetBlock resultSetBlock = new ResultSetBlock(metaData, data);
		response.addResponseBlock(resultSetBlock);

		return response;
	}

	IResultSetBlock transformPR(IProcedureResponse responseOriginal, int[] cols) {
		if (logger.isDebugEnabled())
			logger.logDebug("--->transformPR " + responseOriginal.getProcedureResponseAsString());

		// IProcedureResponse responseFinal = new ProcedureResponseAS();

		IResultSetHeader metaDataFinal = new ResultSetHeader();
		IResultSetData dataFinal = new ResultSetData();

		StringBuilder sb = new StringBuilder();
		sb.append("*");
		for (int v : cols) {
			sb.append(String.valueOf(v - 1));
			sb.append("*");
		}
		String arrayString = sb.toString();

		IResultSetBlock resulsetOrigin = responseOriginal.getResultSet(1);
		IResultSetRow[] rowsTemp = resulsetOrigin.getData().getRowsAsArray();

		IResultSetHeader header = resulsetOrigin.getMetaData();
		IResultSetHeaderColumn[] headerColumns = header.getColumnsMetaDataAsArray();

		for (int a = 0; a < headerColumns.length; a++) {
			if (isNotInArray(arrayString, a))
				metaDataFinal.addColumnMetaData(headerColumns[a]);

		}
		if (logger.isDebugEnabled())
			logger.logDebug("--->Datos ");

		for (IResultSetRow iResultSetRow : rowsTemp) {
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

			IResultSetRow rowFinal = new ResultSetRow();
			int b = 1;

			for (int i = 0; i < columns.length; i++) {
				if (isNotInArray(arrayString, i)) {
					rowFinal.addRowData(b, columns[i]);
					b++;
				}
			}

			dataFinal.addRow(rowFinal);
		}

		IResultSetBlock resultBlockFinal = new ResultSetBlock(metaDataFinal, dataFinal);
		// responseFinal.addResponseBlock(resultBlockFinal);

		// logger.logDebug("--->responseFinal transformPR"
		// + responseFinal.getProcedureResponseAsString());
		return resultBlockFinal;
	}

	private boolean isNotInArray(String arrayString, int a) {
		// TODO Auto-generated method stub
		int indexOf = arrayString.indexOf("*" + String.valueOf(a) + "*");
		if (indexOf == -1)
			return true;
		return false;
	}

	private void Insert(IProcedureResponse aProcedureResponse, String cadena, int rs) {
		if (logger.isDebugEnabled())
			logger.logDebug(
					" **getProcedureResponse  " + cadena + " -->" + aProcedureResponse.getProcedureResponseAsString());

		IResultSetBlock resultSet = aProcedureResponse.getResultSet(rs);
		IResultSetRow[] rows = resultSet.getData().getRowsAsArray();
		IResultSetHeader header = resultSet.getMetaData();
		if (logger.isDebugEnabled())
			logger.logDebug(" ProcedureResponse Insert --> " + aProcedureResponse.toString());
		List<String> inserts = new ArrayList<String>();
		if (logger.isDebugEnabled())
			logger.logDebug("resultSet" + resultSet);
		for (IResultSetRow row : rows) {
			if (logger.isDebugEnabled()) {
				logger.logDebug("row" + row);
				logger.logDebug("header" + header);
			}
			inserts.add(prepareBatch(row, header, cadena));
		}
		if (inserts.isEmpty()) {
			if (logger.isDebugEnabled())
				logger.logWarning("No se tiene registros para insertar en la tabla ");
		}

		executeBdd(inserts);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Se ha ingresado los datos correctamente.");
		}
	}

	private String prepareBatch(IResultSetRow resultRow, IResultSetHeader header, String insertTable) {
		if (logger.isDebugEnabled())
			logger.logDebug("Se ha ingresado a prepareBatch");
		// String methodInfo = "[prepareBatch]";
		String insert = insertTable; // createInsertTemporal();

		int columns = resultRow.getColumnsNumber();
		if (logger.isDebugEnabled())
			logger.logDebug("columns" + String.valueOf(columns));
		for (int i = 1; i <= columns; i++) {
			String data = resultRow.getRowData(i).getValue().trim();
			int dataType = header.getColumnMetaData(i).getType();

			if ((data == null) || (data.equals("")) || (data.equals("null"))) {
				insert = insert + " null,";
			} else if (dataString.contains(Integer.valueOf(dataType)) || data.contains("/")) {
				insert = insert + "'" + data + "',";
			} else if (dataDate.contains(Integer.valueOf(dataType))) {
				insert = insert + "'" + data + "',";
			} else if (dataInt.contains(Integer.valueOf(dataType))) {
				insert = insert + data + ",";
			}
		}
		insert = insert.substring(0, insert.length() - 1);
		insert = insert + ")";
		return insert;
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
			if (logger.isDebugEnabled())
				logger.logInfo("No se pudieron guardar los datos en BD . ERROR : " + e);
			throw new COBISInfrastructureRuntimeException(
					methodInfo + "No se pudieron guardar los datos en BD . ERROR : " + e.getMessage());
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

	private void loadData() {
		dataString = new ArrayList<Integer>();
		dataString.add(Integer.valueOf(47));
		dataString.add(Integer.valueOf(39));
		dataString.add(Integer.valueOf(35));

		dataInt = new ArrayList<Integer>();
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

		dataDate = new ArrayList<Integer>();
		dataDate.add(Integer.valueOf(61));
		dataDate.add(Integer.valueOf(58));
		dataDate.add(Integer.valueOf(111));

	}

	public void loadLocatorConfiguration() {
		if (logger.isDebugEnabled())
			logger.logInfo("Ingresa a loadConfiguration");
		componentLocator = ComponentLocator.getInstance(this);
		dbServiceFactory = (IDBServiceFactory) componentLocator.find(IDBServiceFactory.class);
		loadDbms();
		platform = 10;
		loadData();

	}

	private void loadDbms() {
		dbms = "SYBCTS";
		if (logger.isDebugEnabled())
			logger.logInfo("dbms:" + dbms);
		if (dbms == null) {
			throw new COBISInfrastructureRuntimeException("Database Name is null");
		}

		String dbmsServiceProvider = "DataSource";
		dbServiceProvider = dbServiceFactory.getDBServiceProvider(dbms, dbmsServiceProvider);
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
