package com.cobiscorp.ecobis.orchestration.core.ib.query.datacustomer;

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
import com.cobiscorp.ecobis.ib.application.dtos.BatchGenerateCustomerDataRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BatchGenerateCustomerDataResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Batch;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Entity;
import com.cobiscorp.ecobis.ib.orchestration.dtos.EntityIntegrated;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceBatchGenerateCustomerData;

import java.sql.Connection;
import java.sql.Statement;

/**
 * 
 * @author itorres
 * @since Ago 06, 2014
 * @version 1.0.0
 */

@Component(name = "GenerateDataCustomerOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "GenerateDataCustomerOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "GenerateDataCustomerOrchestrationCore") })

public class GenerateDataCustomerOrchestrationCore extends QueryBaseTemplate {

	protected static String dbms;
	private static List<Integer> dataString = new ArrayList<Integer>();
	private static List<Integer> dataInt = new ArrayList<Integer>();
	private static IDBServiceProvider dbServiceProvider;
	protected static IDBServiceFactory dbServiceFactory;
	private String tableName = "cob_bvirtual..bv_procesar_cliente";
	private static ComponentLocator componentLocator;

	ILogger logger = LogFactory.getLogger(GenerateDataCustomerOrchestrationCore.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate#
	 * validateLocalExecution(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	protected IProcedureResponse validateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		IProcedureResponse response = new ProcedureResponseAS();
		response.setReturnCode(0);
		return response;
	}

	@Reference(referenceInterface = ICoreServiceBatchGenerateCustomerData.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceBatchGenerateCustomerData coreService;

	protected void bindCoreService(ICoreServiceBatchGenerateCustomerData service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	protected void unbindCoreService(ICoreServiceBatchGenerateCustomerData service) {
		coreService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		if (logger.isInfoEnabled())
			logger.logInfo(
					"Component-Name: " + this.getClass().getSimpleName() + ", Component-Version: $$project.version$$");
	}

	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		String messageError = null;
		String messageLog = null;
		String queryName = null;
		BatchGenerateCustomerDataResponse aGenerateCustomerDataResponse = new BatchGenerateCustomerDataResponse();
		BatchGenerateCustomerDataRequest aGenerateCustomerDataRequest = transformToGenerateCustomerDataRequest(
				request.clone());

		try {
			messageError = "getGenerateCustomerData: ERROR EXECUTING SERVICE";
			messageLog = "getGenerateCustomerData " + aGenerateCustomerDataRequest.getEntityCollection().getEnte();
			queryName = "getGenerateCustomerData";
			aGenerateCustomerDataRequest.setOriginalRequest(request);
			aGenerateCustomerDataResponse = coreService.getGenerateCustomerData(aGenerateCustomerDataRequest);
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

		IProcedureResponse response = transformProcedureResponse(aGenerateCustomerDataResponse,
				aBagSPJavaOrchestration);

		/*****
		 * PROCESO PARA ACTUALIZAR TABLA cob_bvirtual..bv_proceso_batch
		 *******/
		List<String> queryList = new ArrayList<String>();
		String query = null;

		if (logger.isInfoEnabled())
			logger.logInfo(" TOTAL RECORDS --> " + aGenerateCustomerDataResponse.getTotalRecords());
		if (aGenerateCustomerDataRequest.getNext() == 0) {
			query = " update cob_bvirtual..bv_proceso_batch " + " set pb_tot_reg_procesar = "
					+ aGenerateCustomerDataResponse.getTotalRecords() + " ,pb_reg_max = "
					+ aGenerateCustomerDataResponse.getMaxRecord() + " where pb_cod = "
					+ aGenerateCustomerDataRequest.getBatchCollection().getBatch();
			queryList.add(query);
		}

		if (aGenerateCustomerDataRequest.getNext() < aGenerateCustomerDataResponse.getMaxRecord()) {
			if (logger.isInfoEnabled())
				logger.logInfo("LAST RECORD -->" + aBagSPJavaOrchestration.get("LAST_RECORD").toString());
			String lastRecord = aBagSPJavaOrchestration.get("LAST_RECORD").toString();
			if (logger.isInfoEnabled())
				logger.logInfo(" SECUENCIAL *** --> " + lastRecord);
			query = " update cob_bvirtual..bv_proceso_batch set pb_secuencial = " + lastRecord + " where pb_cod = "
					+ aGenerateCustomerDataRequest.getBatchCollection().getBatch();
			queryList.add(query);
		}
		if (logger.isInfoEnabled())
			logger.logInfo(" QUERYS UPDATE BV_PROCESO_BATCH --> " + queryList.toString());
		executeBdd(queryList);

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
	private BatchGenerateCustomerDataRequest transformToGenerateCustomerDataRequest(IProcedureRequest aRequest) {
		BatchGenerateCustomerDataRequest aGenerateCustomerDataRequest = new BatchGenerateCustomerDataRequest();
		// Batch aBatch = new Batch();
		Entity aEntity = new Entity();
		Batch aBatch = new Batch();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;

		messageError = aRequest.readValueParam("@i_fecha_ingreso") == null ? " - @i_fecha_ingreso can't be null" : "";
		messageError += aRequest.readValueParam("@i_numero_registros_procesar") == null
				? " - @i_numero_registros_procesar can't be null" : "";
		// messageError += aRequest.readValueParam("@i_ente_mis") == null ? " -
		// @i_ente_mis can't be null":"";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		// aEntity.setEnte(Integer.parseInt(aRequest.readValueParam("@i_ente_mis")));
		aBatch.setBatch(Integer.parseInt(aRequest.readValueParam("@i_batch")));
		aBatch.setSarta(Integer.parseInt(aRequest.readValueParam("@i_sarta")));
		aBatch.setIntento(Integer.parseInt(aRequest.readValueParam("@i_intento")));
		aBatch.setCorrida(Integer.parseInt(aRequest.readValueParam("@i_corrida")));
		aBatch.setSecuencial(Integer.parseInt(aRequest.readValueParam("@i_secuencial")));
		aGenerateCustomerDataRequest.setEntityCollection(aEntity);
		aGenerateCustomerDataRequest.setBatchCollection(aBatch);
		aGenerateCustomerDataRequest.setDateProcess(aRequest.readValueParam("@i_fecha_proceso"));
		aGenerateCustomerDataRequest.setDateAdmission(aRequest.readValueParam("@i_fecha_ingreso"));
		aGenerateCustomerDataRequest.setNext(Integer.parseInt(aRequest.readValueParam("@i_siguiente")));
		aGenerateCustomerDataRequest
				.setRecordNumber(Integer.parseInt(aRequest.readValueParam("@i_numero_registros_procesar")));
		// aGenerateCustomerDataRequest.setRowcount(Integer.parseInt(aRequest.readValueParam("@i_numero_registros_procesar")));
		return aGenerateCustomerDataRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/
	private IProcedureResponse transformProcedureResponse(
			BatchGenerateCustomerDataResponse aGenerateCustomerDataResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		List<String> inserts = new ArrayList<String>();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		if (aGenerateCustomerDataResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aGenerateCustomerDataResponse.getMessages())); // COLOCA
																							// ERRORES
																							// COMO
																							// RESPONSE
																							// DE
																							// LA
																							// TRANSACCIÓN
			Utils.returnException(aGenerateCustomerDataResponse.getMessages());
		} else {
			if (logger.isInfoEnabled())
				logger.logInfo("Tranforma el procedure");
			// Agregar Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("pc_fecha", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pc_estado", ICTSTypes.SQLVARCHAR, 1));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pc_ente_mis", ICTSTypes.SQLINT4, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pc_subtipo", ICTSTypes.SQLVARCHAR, 1));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pc_nombre", ICTSTypes.SQLVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pc_ced_ruc", ICTSTypes.SQLVARCHAR, 30));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pc_pasaporte", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pc_oficina", ICTSTypes.SQLINT4, 5));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pc_fecha_nac", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pc_email", ICTSTypes.SQLVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pc_oficial", ICTSTypes.SQLINT4, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pc_tipo_ced", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pc_pnombre", ICTSTypes.SQLVARCHAR, 32));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pc_papellido", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pc_sapellido", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pc_segmento", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pc_linea_negocio", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pc_apoderado_legal", ICTSTypes.SQLINT4, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pc_ced_ruc_ant", ICTSTypes.SQLVARCHAR, 30));

			for (EntityIntegrated aEntityIntegrated : aGenerateCustomerDataResponse.getEntityIntegrateList()) {
				if (!IsValidEntityIntegratedResponse(aEntityIntegrated))
					return null;

				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false,
						aEntityIntegrated.getFecha_crea() != null ? aEntityIntegrated.getFecha_crea() : ""));
				row.addRowData(2, new ResultSetRowColumnData(false,
						aEntityIntegrated.getEstado() != null ? aEntityIntegrated.getEstado() : ""));
				row.addRowData(3, new ResultSetRowColumnData(false,
						aEntityIntegrated.getEnte() != null ? aEntityIntegrated.getEnte().toString() : "0"));
				row.addRowData(4, new ResultSetRowColumnData(false,
						aEntityIntegrated.getSubtipo() != null ? aEntityIntegrated.getSubtipo() : ""));
				row.addRowData(5, new ResultSetRowColumnData(false,
						aEntityIntegrated.getNombre_completo() != null ? aEntityIntegrated.getNombre_completo() : ""));
				row.addRowData(6, new ResultSetRowColumnData(false,
						aEntityIntegrated.getCed_ruc() != null ? aEntityIntegrated.getCed_ruc() : ""));
				row.addRowData(7, new ResultSetRowColumnData(false,
						aEntityIntegrated.getPasaporte() != null ? aEntityIntegrated.getPasaporte() : ""));
				row.addRowData(8, new ResultSetRowColumnData(false,
						aEntityIntegrated.getOficina() != null ? aEntityIntegrated.getOficina().toString() : "0"));
				row.addRowData(9, new ResultSetRowColumnData(false,
						aEntityIntegrated.getFecha_nac() != null ? aEntityIntegrated.getFecha_nac() : ""));
				row.addRowData(10, new ResultSetRowColumnData(false,
						aEntityIntegrated.getEmail() != null ? aEntityIntegrated.getEmail() : ""));
				row.addRowData(11, new ResultSetRowColumnData(false,
						aEntityIntegrated.getOficial() != null ? aEntityIntegrated.getOficial().toString() : "0"));
				row.addRowData(12, new ResultSetRowColumnData(false,
						aEntityIntegrated.getTipo_ced() != null ? aEntityIntegrated.getTipo_ced() : ""));
				row.addRowData(13, new ResultSetRowColumnData(false,
						aEntityIntegrated.getNombre() != null ? aEntityIntegrated.getNombre() : ""));
				row.addRowData(14, new ResultSetRowColumnData(false,
						aEntityIntegrated.getP_apellido() != null ? aEntityIntegrated.getP_apellido() : ""));
				row.addRowData(15, new ResultSetRowColumnData(false,
						aEntityIntegrated.getS_apellido() != null ? aEntityIntegrated.getS_apellido() : ""));
				row.addRowData(16, new ResultSetRowColumnData(false,
						aEntityIntegrated.getSegmento() != null ? aEntityIntegrated.getSegmento() : ""));
				row.addRowData(17, new ResultSetRowColumnData(false,
						aEntityIntegrated.getLineaNegocio() != null ? aEntityIntegrated.getLineaNegocio() : ""));
				row.addRowData(18, new ResultSetRowColumnData(false,
						aEntityIntegrated.getCedRucAnt() != null ? aEntityIntegrated.getCedRucAnt() : ""));
				row.addRowData(19, new ResultSetRowColumnData(false, aEntityIntegrated.getApoderadoLegal() != null
						? aEntityIntegrated.getApoderadoLegal().toString() : ""));

				data.addRow(row);
			}
			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock);

			// if (
			// String.valueOf(aGenerateCustomerDataResponse.getMaxRecord())!=
			// null)
			// {
			// wProcedureResponse.addParam("@o_max_registros",
			// ICTSTypes.SQLINT4, 0,
			// String.valueOf(aGenerateCustomerDataResponse.getMaxRecord()));
			// }
			// if (
			// String.valueOf(aGenerateCustomerDataResponse.getMaxRecord())!=
			// null)
			// {
			// wProcedureResponse.addParam("@o_tot_registros",
			// ICTSTypes.SQLINT4, 0,
			// String.valueOf(aGenerateCustomerDataResponse.getTotalRecords()));
			// }
			wProcedureResponse.setReturnCode(aGenerateCustomerDataResponse.getReturnCode());

			IResultSetBlock resultSet = wProcedureResponse.getResultSet(1);
			IResultSetRow[] dataInsert = wProcedureResponse.getResultSet(1).getData().getRowsAsArray();
			IResultSetHeader headerInsert = resultSet.getMetaData();

			Integer lastEnte = 0;
			for (IResultSetRow iResultSetRow : dataInsert) {
				if (logger.isInfoEnabled()) {
					logger.logInfo("iResultSetRow --" + iResultSetRow);
					logger.logInfo("iResultSetRow.getRowData(3) --" + iResultSetRow.getRowData(3));
				}
				lastEnte = Integer.parseInt(iResultSetRow.getRowData(3).getValue());
				inserts.add(prepareInsertBatch(tableName, iResultSetRow, headerInsert));
			}

			aBagSPJavaOrchestration.put("LAST_RECORD", lastEnte);

			//

			if (!inserts.isEmpty()) {
				executeBdd(inserts);
			} else {

			}
		}
		/*
		 * if ( String.valueOf(aGenerateCustomerDataResponse.getMaxEnte())!=
		 * null) { wProcedureResponse.addParam("@o_siguiente",
		 * ICTSTypes.SQLINT4, 0,
		 * String.valueOf(aGenerateCustomerDataResponse.getMaxEnte())); } if (
		 * String.valueOf(aGenerateCustomerDataResponse.getMaxEnte())!= null) {
		 * wProcedureResponse.addParam("@o_rowcount", ICTSTypes.SQLINT4, 0,
		 * String.valueOf(aGenerateCustomerDataResponse.getRowCount())); }
		 * wProcedureResponse.setReturnCode(aGenerateCustomerDataResponse.
		 * getReturnCode());
		 * 
		 * IResultSetBlock resultSet = wProcedureResponse.getResultSet(1);
		 * IResultSetRow[] dataInsert =
		 * wProcedureResponse.getResultSet(1).getData().getRowsAsArray();
		 * IResultSetHeader headerInsert = resultSet.getMetaData();
		 * for(IResultSetRow iResultSetRow : dataInsert){
		 * inserts.add(prepareInsertBatch(tableName, iResultSetRow,
		 * headerInsert)); }
		 * 
		 * if (!inserts.isEmpty()) { executeBdd(inserts); }else{
		 * 
		 * }
		 */

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}

	// Prepara las sentencias para el insert
	private String prepareInsertBatch(String tableName, IResultSetRow resultRow, IResultSetHeader header) {
		String methodInfo = "[prepareBatch]";
		String insert = "insert into " + tableName + " values (";
		/*
		 * if (ssn_branch != null) insert = insert + ssn_branch; else insert =
		 * insert + "null";
		 */
		// String messageError = null;
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

	private void executeBdd(List<String> inserts) {
		String methodInfo = "[executeBdd]";
		Connection connection = null;

		componentLocator = ComponentLocator.getInstance(this);
		dbServiceFactory = (IDBServiceFactory) componentLocator.find(IDBServiceFactory.class);
		dbServiceProvider = dbServiceFactory.getDBServiceProvider("SQLCTS", "DataSource");
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
	private boolean IsValidEntityIntegratedResponse(EntityIntegrated aEntityIntegrated) {
		String messageError = null;

		messageError = aEntityIntegrated.getEnte() == null ? "Ente can't be null" : "";
		messageError += aEntityIntegrated.getFecha_crea() == null ? " - Fecha can't be null" : "";
		messageError += aEntityIntegrated.getEstado() == null ? " - Estado can't be null" : "";
		messageError += aEntityIntegrated.getSubtipo() == null ? " - Sub Tipo can't be null" : "";
		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);
		return true;
	}
}
