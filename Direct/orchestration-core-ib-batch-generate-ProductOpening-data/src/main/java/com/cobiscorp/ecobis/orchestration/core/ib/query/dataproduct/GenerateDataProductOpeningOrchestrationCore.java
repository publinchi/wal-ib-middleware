package com.cobiscorp.ecobis.orchestration.core.ib.query.dataproduct;

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
import com.cobiscorp.ecobis.ib.application.dtos.BatchProductOpeningRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BatchProductOpeningResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Batch;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BatchProductOpening;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceBatchProductOpening;

import java.sql.Connection;
import java.sql.Statement;

/**
 * 
 * @author kmeza
 * @since ene. 19, 2015
 * @version 1.0.0
 */

@Component(name = "GenerateDataProductOpeningOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "GenerateDataProductOpeningOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "GenerateDataProductOpeningOrchestrationCore") })

public class GenerateDataProductOpeningOrchestrationCore extends QueryBaseTemplate {

	protected static String dbms;
	private static List<Integer> dataString = new ArrayList<Integer>();
	private static List<Integer> dataInt = new ArrayList<Integer>();
	private static IDBServiceProvider dbServiceProvider;
	protected static IDBServiceFactory dbServiceFactory;
	private String tableName = "cob_bvirtual..bv_procesar_producto";
	private static ComponentLocator componentLocator;
	private Integer batch, numReg;

	ILogger logger = LogFactory.getLogger(GenerateDataProductOpeningOrchestrationCore.class);

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

	@Reference(referenceInterface = ICoreServiceBatchProductOpening.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceBatchProductOpening coreService;

	protected void bindCoreService(ICoreServiceBatchProductOpening service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	protected void unbindCoreService(ICoreServiceBatchProductOpening service) {
		coreService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		if (logger.isInfoEnabled())
			logger.logInfo(
					"Component-Name: " + this.getClass().getSimpleName() + ", Component-Version: $$project.version$$");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate#
	 * updateLocalExecution(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	protected IProcedureResponse updateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		// TODO Auto-generated method stub

		anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		return super.updateLocalExecution(anOriginalRequest, bag);
	}

	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		String messageError = null;
		String messageLog = null;
		String queryName = null;
		BatchProductOpeningResponse aBatchProductOpeningResponse = new BatchProductOpeningResponse();
		BatchProductOpeningRequest aBatchProductOpeningRequest = transformToGenerateProductOpeningRequest(
				request.clone());

		try {
			messageError = "executeBatchProductOpening: ERROR EXECUTING SERVICE";
			messageLog = "executeBatchProductOpening " + aBatchProductOpeningRequest.getBatch().getBatch();
			queryName = "executeBatchProductOpening";
			// aBatchProductOpeningRequest.setOriginalRequest(request);
			aBatchProductOpeningResponse = coreService.executeBatchProductOpening(aBatchProductOpeningRequest);
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

		return transformProcedureResponse(aBatchProductOpeningResponse, aBagSPJavaOrchestration);
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// Valida Inyección de dependencias
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		Utils.validateComponentInstance(mapInterfaces);
		try {
			aBagSPJavaOrchestration.put(LOG_MESSAGE, "GENERACION DE PRODUCTOS");
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
	private BatchProductOpeningRequest transformToGenerateProductOpeningRequest(IProcedureRequest aRequest) {
		BatchProductOpeningRequest aBatchProductOpeningRequest = new BatchProductOpeningRequest();
		// Batch aBatch = new Batch();
		Batch aBatch = new Batch();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;

		messageError = aRequest.readValueParam("@i_fecha_ini") == null ? " - @i_fecha_ini can't be null" : "";
		messageError += aRequest.readValueParam("@i_numRows") == null ? " - @i_numRows can't be null" : "";
		messageError += aRequest.readValueParam("@i_secuencial") == null ? " - @i_secuencial can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		// aEntity.setEnte(Integer.parseInt(aRequest.readValueParam("@i_ente_mis")));
		batch = Integer.parseInt(aRequest.readValueParam("@i_batch"));
		numReg = Integer.parseInt(aRequest.readValueParam("@i_numRows"));
		aBatch.setBatch(Integer.parseInt(aRequest.readValueParam("@i_batch")));
		aBatch.setSarta(Integer.parseInt(aRequest.readValueParam("@i_sarta")));
		aBatch.setIntento(Integer.parseInt(aRequest.readValueParam("@i_intento")));
		aBatch.setCorrida(Integer.parseInt(aRequest.readValueParam("@i_corrida")));
		aBatch.setSecuencial(Integer.parseInt(aRequest.readValueParam("@i_secuencial")));
		// aGenerateCustomerDataRequest.setEntityCollection(aEntity);
		aBatchProductOpeningRequest.setBatch(aBatch);
		aBatchProductOpeningRequest.setfIni(aRequest.readValueParam("@i_fecha_ini"));
		aBatchProductOpeningRequest.setfFin(aRequest.readValueParam("@i_fecha_fin"));
		aBatchProductOpeningRequest.setRowsCount(Integer.parseInt(aRequest.readValueParam("@i_numRows")));
		aBatchProductOpeningRequest.setServicio(Integer.parseInt(aRequest.readValueParam("@i_servicio")));

		// Se toma la variable Customer para enviar el siguiente registro a
		// consultar, este tendra un secuencial
		aBatchProductOpeningRequest.setCustomer(Integer.parseInt(aRequest.readValueParam("@i_cliente")));
		return aBatchProductOpeningRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/
	private IProcedureResponse transformProcedureResponse(BatchProductOpeningResponse aBatchProductOpeningResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		List<String> inserts = new ArrayList<String>();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		if (aBatchProductOpeningResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aBatchProductOpeningResponse.getMessages())); // COLOCA
																						// ERRORES
																						// COMO
																						// RESPONSE
																						// DE
																						// LA
																						// TRANSACCIÓN
			Utils.returnException(aBatchProductOpeningResponse.getMessages());
		} else {
			// Agregar Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("pp_fecha", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pp_estado", ICTSTypes.SQLVARCHAR, 1));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pp_ente_mis", ICTSTypes.SQLINT4, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pp_producto", ICTSTypes.SQLINT4, 5));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pp_moneda", ICTSTypes.SQLINT4, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pp_cuenta", ICTSTypes.SQLVARCHAR, 30));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pp_estado_prod", ICTSTypes.SQLVARCHAR, 1));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pp_oficina", ICTSTypes.SQLINT4, 5));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pp_cuenta_desc", ICTSTypes.SQLVARCHAR, 17));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pp_tipo_firma", ICTSTypes.SQLVARCHAR, 1));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("pp_tipo_cuenta", ICTSTypes.SQLVARCHAR, 1));

			for (BatchProductOpening aBatchProductOpening : aBatchProductOpeningResponse.getBatchProductOpeningList()) {
				if (!IsValidBatchProductOpeningResponse(aBatchProductOpening))
					return null;

				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false,
						aBatchProductOpening.getDate() != null ? aBatchProductOpening.getDate() : ""));
				row.addRowData(2, new ResultSetRowColumnData(false,
						aBatchProductOpening.getStatus() != null ? aBatchProductOpening.getStatus() : ""));
				row.addRowData(3, new ResultSetRowColumnData(false, aBatchProductOpening.getCustomerId() != null
						? aBatchProductOpening.getCustomerId().toString() : ""));
				row.addRowData(4, new ResultSetRowColumnData(false, aBatchProductOpening.getProductId() != null
						? aBatchProductOpening.getProductId().toString() : ""));
				row.addRowData(5, new ResultSetRowColumnData(false, aBatchProductOpening.getCurrencyId() != null
						? aBatchProductOpening.getCurrencyId().toString() : ""));
				row.addRowData(6, new ResultSetRowColumnData(false,
						aBatchProductOpening.getAccount() != null ? aBatchProductOpening.getAccount() : ""));
				row.addRowData(7, new ResultSetRowColumnData(false, aBatchProductOpening.getStatusproduct() != null
						? aBatchProductOpening.getStatusproduct() : ""));
				row.addRowData(8, new ResultSetRowColumnData(false, aBatchProductOpening.getOfficeId() != null
						? aBatchProductOpening.getOfficeId().toString() : ""));
				row.addRowData(9, new ResultSetRowColumnData(false,
						aBatchProductOpening.getDestAccount() != null ? aBatchProductOpening.getDestAccount() : ""));
				row.addRowData(10, new ResultSetRowColumnData(false, aBatchProductOpening.getTypeSignature() != null
						? aBatchProductOpening.getTypeSignature() : ""));
				row.addRowData(11, new ResultSetRowColumnData(false,
						aBatchProductOpening.getTypeAccount() != null ? aBatchProductOpening.getTypeAccount() : ""));

				data.addRow(row);
			}
			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock);

			if (String.valueOf(aBatchProductOpeningResponse.getMaxCustomer()) != null) {
				wProcedureResponse.addParam("@o_cliente", ICTSTypes.SQLINT4, 0,
						String.valueOf(aBatchProductOpeningResponse.getMaxCustomer().toString()));
			}
			if (String.valueOf(aBatchProductOpeningResponse.getMaxProduct()) != null) {
				wProcedureResponse.addParam("@o_producto", ICTSTypes.SQLINT4, 0,
						String.valueOf(aBatchProductOpeningResponse.getMaxProduct().toString()));
			}
			if (String.valueOf(aBatchProductOpeningResponse.getMaxCurrency()) != null) {
				wProcedureResponse.addParam("@o_moneda", ICTSTypes.SQLINT4, 0,
						String.valueOf(aBatchProductOpeningResponse.getMaxCurrency().toString()));
			}
			if (String.valueOf(aBatchProductOpeningResponse.getMaxAccount()) != null) {
				wProcedureResponse.addParam("@o_cuenta", ICTSTypes.SQLVARCHAR, 0,
						String.valueOf(aBatchProductOpeningResponse.getMaxAccount()));
			}
			if (String.valueOf(aBatchProductOpeningResponse.getSecuential()) != null) {
				wProcedureResponse.addParam("@o_secuencial", ICTSTypes.SQLINT4, 0,
						String.valueOf(aBatchProductOpeningResponse.getSecuential().toString()));
			}
			wProcedureResponse.setReturnCode(aBatchProductOpeningResponse.getReturnCode());

			/*****
			 * PROCESO PARA ACTUALIZAR TABLA cob_bvirtual..bv_proceso_batch
			 *******/
			List<String> queryList = new ArrayList<String>();
			String query = null;

			if (aBatchProductOpeningResponse.getSecuential() != 0) {
				query = " update cob_bvirtual..bv_proceso_batch " + " set  pb_num_reg = " + numReg.toString()
						+ " ,pb_secuencial  = " + aBatchProductOpeningResponse.getSecuential().toString()
						+ ", pb_tot_reg_procesar = " + aBatchProductOpeningResponse.getMaxCustomer().toString()
						+ " where pb_cod = " + batch.toString();
				queryList.add(query);
			}

			executeBdd(queryList);

			/*****
			 * FIN PROCESO PARA ACTUALIZAR TABLA cob_bvirtual..bv_proceso_batch
			 *******/

			IResultSetBlock resultSet = wProcedureResponse.getResultSet(1);
			IResultSetRow[] dataInsert = wProcedureResponse.getResultSet(1).getData().getRowsAsArray();
			IResultSetHeader headerInsert = resultSet.getMetaData();
			for (IResultSetRow iResultSetRow : dataInsert) {
				inserts.add(prepareInsertBatch(tableName, iResultSetRow, headerInsert));
			}

			if (!inserts.isEmpty()) {
				executeBdd(inserts);
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

	private void executeBdd(List<String> inserts) {
		String methodInfo = "[executeBdd]";
		Connection connection = null;

		componentLocator = ComponentLocator.getInstance(this);
		dbServiceFactory = (IDBServiceFactory) componentLocator.find(IDBServiceFactory.class);
		dbServiceProvider = dbServiceFactory.getDBServiceProvider("SQLCANALES", "DataSource");
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
	private boolean IsValidBatchProductOpeningResponse(BatchProductOpening aBatchProductOpening) {
		String messageError = null;

		messageError = aBatchProductOpening.getProductId() == null ? "ProductId can't be null" : "";
		messageError += aBatchProductOpening.getDate() == null ? " - Date can't be null" : "";
		messageError += aBatchProductOpening.getDestAccount() == null ? " - DestAccount can't be null" : "";
		messageError += aBatchProductOpening.getStatus() == null ? " - Status can't be null" : "";
		messageError += aBatchProductOpening.getStatusproduct() == null ? " - Nombre Completo can't be null" : "";
		messageError += aBatchProductOpening.getTypeSignature() == null ? " - Oficina can't be null" : "";
		messageError += aBatchProductOpening.getCurrencyId() == null ? " - Fecha de Nacimiento can't be null" : "";
		messageError += aBatchProductOpening.getCustomerId() == null ? " - Oficial can't be null" : "";
		messageError += aBatchProductOpening.getOfficeId() == null ? " - Tipo Ced can't be null" : "";
		messageError += aBatchProductOpening.getProductId() == null ? " - Nombre can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);
		return true;
	}
}
