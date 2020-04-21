package com.cobiscorp.ecobis.ib.orchestration.batch.scheduledpayment;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.components.ComponentLocator;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.db.IDBServiceFactory;
import com.cobiscorp.cobis.commons.db.IDBServiceProvider;
import com.cobiscorp.cobis.commons.exceptions.COBISInfrastructureRuntimeException;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.BatchScheduledPaymentRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BatchScheduledPaymentResponse;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentServiceRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ScheduledPaymentRequest;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Batch;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.User;

@Component(name = "BatchScheduledPaymentOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "BatchScheduledPaymentOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "BatchScheduledPaymentOrchestrationCore") })

public class BatchScheduledPaymentOrchestrationCore extends SPJavaOrchestrationBase {

	private static final String COBIS_CONTEXT = "COBIS";
	private static final String NUMERO_TRANSACCION = "1890001";
	private static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	private static ComponentLocator componentLocator;
	private static IDBServiceProvider dbServiceProvider;
	protected static IDBServiceFactory dbServiceFactory;

	private static ILogger logger = LogFactory.getLogger(BatchScheduledPaymentOrchestrationCore.class);

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);
		try {
			if (logger.isInfoEnabled())
				logger.logInfo("Esto llega a la orquestacion batch pp " + anOriginalRequest);
			return this.executeScheduledPayments(transformToBatchScheduledPaymentRequest(anOriginalRequest),
					aBagSPJavaOrchestration);
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.logInfo("Se produjo un error en el proceso del método executeScheduledPayments " + e);
				logger.logInfo(" ERROR ORQUESTACION --> " + e.getMessage());
			}
			e.printStackTrace();
			return Utils.returnExceptionService(anOriginalRequest, e);
		}
	}

	/** Funcion para manejar el proceso de batch pagos programados **/
	private IProcedureResponse executeScheduledPayments(BatchScheduledPaymentRequest batchRequest,
			Map<String, Object> aBagSPJavaOrchestration) throws CTSInfrastructureException, CTSServiceException {

		IProcedureResponse pResponse = new ProcedureResponseAS();
		Message[] messages;
		Message msg = new Message();
		String reference = "";
		Integer sequentialId = 0;

		BatchScheduledPaymentResponse batchResponse = this.getScheduledPaymentToProcess(batchRequest,
				aBagSPJavaOrchestration);
		if (logger.isInfoEnabled())
			logger.logInfo("BATCH RESPONSE EN  executeScheduledPayments" + batchResponse);

		if (logger.isInfoEnabled())
			logger.logInfo("Codigo que  retorna getScheduledPaymentToProcess " + batchResponse.getReturnCode());
		if (batchResponse.getReturnCode() != 0) {
			if (logger.isInfoEnabled())
				logger.logInfo("Error al extraer registros");
			pResponse.setReturnCode(batchResponse.getReturnCode());
			return pResponse;
		}

		if (batchResponse.getListScheduledPayment() != null)
			if (logger.isInfoEnabled()) {
				logger.logInfo(
						"PAGOS A PROCESAR --> " + String.valueOf(batchResponse.getListScheduledPayment().size()));

				logger.logInfo("Continua ejecución ***");
			}
		for (ScheduledPaymentRequest scheduledPayment : batchResponse.getListScheduledPayment()) {
			try {
				if (logger.isInfoEnabled())
					logger.logInfo("Ingresa al for : ID: *** " + scheduledPayment.getId());
				sequentialId = scheduledPayment.getId(); // va obteniendo el
															// ultimo secuencial
															// hasta llegar al
															// ultimo
				switch (scheduledPayment.getType().charAt(0)) {
				case 'T':
					BatchScheduledPaymentTransfers batchTransfers = new BatchScheduledPaymentTransfers();
					pResponse = batchTransfers.executeTransfers(scheduledPayment, aBagSPJavaOrchestration);
					break;
				case 'P':
					BatchScheduledPaymentLoan batchLoans = new BatchScheduledPaymentLoan();
					pResponse = batchLoans.executeLoans(scheduledPayment, aBagSPJavaOrchestration);
					break;
				case 'S':
					BatchScheduledPaymentService batchPaymentService = new BatchScheduledPaymentService();
					pResponse = batchPaymentService.executePaymentService(scheduledPayment, aBagSPJavaOrchestration);
					break;
				}

				if (pResponse.getReturnCode() == 0) {
					if (logger.isInfoEnabled())
						logger.logInfo("La ejecucion del pago programado CON ID :  " + scheduledPayment.getId()
								+ " resulto exitoso: Response Final: " + pResponse);
					/*
					 * if("T".equals(scheduledPayment.getType())||"S".equals(
					 * scheduledPayment.getType())){ reference =
					 * pResponse.readValueParam("@o_ssn_branch"); }else {
					 * reference = pResponse.readValueParam("@o_referencia"); }
					 */
					reference = pResponse.readValueParam("@o_referencia");
					msg.setCode("0");
					msg.setDescription("Ejecucion de Pago programado exitoso - REFERENCIA: " + reference);

				} else {
					messages = Utils.returnArrayMessage(pResponse);
					msg.setCode("1");
					msg.setDescription(
							"CODE: " + messages[0].getCode() + "--DESCRIPTION: " + messages[0].getDescription());
					if (logger.isInfoEnabled())
						logger.logInfo("El response del pago programado devolvio codigo diferente de 0 : Mensaje : "
								+ msg.getDescription());
				}
			} catch (Exception e) {
				pResponse.setReturnCode(1);
				// pResponse.setText("Error ejecutando pago programado de
				// servicios");
				messages = Utils.returnArrayMessage(pResponse);
				msg.setCode("-1");
				msg.setDescription("CODE: " + messages[0].getCode() + "--DESCRIPTION: " + messages[0].getDescription());
				if (logger.isDebugEnabled())
					logger.logDebug("Error ejecutando pago programado de servicios : " + e);
			}
			this.updateScheduledPayment(scheduledPayment, msg, batchRequest);
		}

		/*****
		 * PROCESO PARA ACTUALIZAR TABLA cob_bvirtual..bv_proceso_batch
		 *******/
		loadLocatorConfiguration();
		List<String> queryList = new ArrayList<String>();
		String query = null;
		if (logger.isInfoEnabled())
			logger.logInfo(" TOTAL RECORDS --> " + batchResponse.getTotalRecords());

		if (batchRequest.getNext() == 0) {
			query = " update cob_bvirtual..bv_proceso_batch " + " set pb_tot_reg_procesar = "
					+ batchResponse.getTotalRecords() + " ,pb_reg_max = " + batchResponse.getMaxRecord()
					+ " where pb_cod = " + batchRequest.getBatch().getBatch();
			queryList.add(query);
		}

		if (batchRequest.getNext() < batchResponse.getMaxRecord()) {
			if (logger.isInfoEnabled())
				logger.logInfo(" SECUENCIAL *** --> " + sequentialId);
			query = " update cob_bvirtual..bv_proceso_batch " + " set pb_secuencial = " + sequentialId
					+ " where pb_cod = " + batchRequest.getBatch().getBatch();
			queryList.add(query);
		}
		if (logger.isInfoEnabled())
			logger.logInfo(" QUERYS UPDATE BV_PROCESO_BATCH --> " + queryList.toString());
		executeBdd(queryList);
		/*****
		 * FIN PROCESO PARA ACTUALIZAR TABLA cob_bvirtual..bv_proceso_batch
		 *******/

		// SI llego hasta aca quiere decir que no hubo error en toda la
		// ejecución
		IProcedureResponse returnProcedureResponse = new ProcedureResponseAS();
		returnProcedureResponse.setReturnCode(0);

		return returnProcedureResponse;
	}

	/*** Funcion que actualiza el pago recurrente **/
	private void updateScheduledPayment(ScheduledPaymentRequest scheduledPayment, Message msg,
			BatchScheduledPaymentRequest batchRequest) {

		if (logger.isInfoEnabled())
			logger.logInfo("INICIANDO SERVICIO: updateScheduledPayment");
		if (logger.isInfoEnabled())
			logger.logInfo("updateScheduledPayment scheduledPayment: " + scheduledPayment);
		if (logger.isInfoEnabled())
			logger.logInfo("updateScheduledPayment Message: " + msg);
		if (logger.isInfoEnabled())
			logger.logInfo("updateScheduledPayment batchRequest: " + batchRequest);

		IProcedureRequest pRequest = new ProcedureRequestAS();
		pRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, NUMERO_TRANSACCION);

		pRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		pRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);

		pRequest.setSpName("cob_bvirtual..sp_batch_pagos_recur_bv");
		pRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, NUMERO_TRANSACCION);
		pRequest.addInputParam("@i_id", ICTSTypes.SQLINT4, scheduledPayment.getId().toString());
		pRequest.addInputParam("@i_msg", ICTSTypes.SQLVARCHAR, msg.getDescription());
		pRequest.addInputParam("@i_periodicidad", ICTSTypes.SQLINT4, scheduledPayment.getFrecuencyId());
		pRequest.addInputParam("@i_fecha_act", ICTSTypes.SQLDATETIME, scheduledPayment.getNextPaymentDate());
		pRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "U");
		pRequest.addInputParam("@i_error", ICTSTypes.SQLINT2, msg.getCode());
		pRequest.addInputParam("@i_sarta", ICTSTypes.SQLINT4, batchRequest.getBatch().getSarta().toString());
		pRequest.addInputParam("@i_batch", ICTSTypes.SQLINT4, batchRequest.getBatch().getBatch().toString());
		pRequest.addInputParam("@i_secuencial", ICTSTypes.SQLINT4, batchRequest.getBatch().getSecuencial().toString());

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request: " + pRequest.getProcedureRequestAsString());
		}
		IProcedureResponse pResponse = executeCoreBanking(pRequest);
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response: " + pResponse.getProcedureResponseAsString());
		}

		if (pResponse.getReturnCode() != 0) {
			if (logger.isInfoEnabled())
				logger.logInfo("Error al actualizar pago programado");
		}
	}

	private BatchScheduledPaymentResponse getScheduledPaymentToProcess(BatchScheduledPaymentRequest batchRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo("INICIANDO SERVICIO: getScheduledPaymentToProcess");
		if (logger.isInfoEnabled())
			logger.logInfo("Request getScheduledPaymentToProcess " + batchRequest);

		IProcedureRequest request = new ProcedureRequestAS();
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, NUMERO_TRANSACCION);
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);

		request.setSpName("cob_bvirtual..sp_batch_pagos_recur_bv");
		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, NUMERO_TRANSACCION);
		request.addInputParam("@i_numero_registros", ICTSTypes.SQLINT4, batchRequest.getRecordNumber().toString());
		request.addInputParam("@i_siguiente", ICTSTypes.SQLINT4, batchRequest.getNext().toString());
		request.addInputParam("@i_tipo_pago", ICTSTypes.SQLVARCHAR, batchRequest.getScheduledPayment().getType());
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "S");
		request.addOutputParam("@o_total_registros", ICTSTypes.SQLINT4, "0");
		request.addOutputParam("@o_registro_max", ICTSTypes.SQLINT4, "0");

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request: " + request.getProcedureRequestAsString());
		}

		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isInfoEnabled())
			logger.logInfo("getScheduledPaymentToProcess : pResponse.getReturnCode()=" + pResponse.getReturnCode());
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response: " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled())
			logger.logInfo("FINALIZANDO SERVICIO: getScheduledPaymentToProcess");

		return transforToBatchScheduledPaymentResponse(pResponse);
	}

	public BatchScheduledPaymentResponse transforToBatchScheduledPaymentResponse(IProcedureResponse response) {

		// IProcedureRequest originalRequest = (IProcedureRequest)
		// aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		BatchScheduledPaymentResponse batchScheduledPaymentResponse = new BatchScheduledPaymentResponse();

		ScheduledPaymentRequest scheduledPayment;
		PaymentServiceRequest paymentService;
		User user;
		Client client;
		Currency debitCurrency;
		Product debitProduct;
		Currency creditCurrency;
		Product creditProduct;
		List<ScheduledPaymentRequest> scheduledPaymentList = new ArrayList<ScheduledPaymentRequest>();

		if (logger.isDebugEnabled()) {
			logger.logDebug("ProcedureResponse: " + response.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled())
			logger.logInfo("transforToBatchScheduledPaymentResponse(...) :: ReturnCode=" + response.getReturnCode());

		if (response.getReturnCode() == 0) {

			IResultSetRow[] rowQueryBatchScheduledPayment = response.getResultSet(1).getData().getRowsAsArray();
			for (IResultSetRow iResultSetRow : rowQueryBatchScheduledPayment) {
				try {

					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

					user = new User();
					client = new Client();
					debitCurrency = new Currency();
					debitProduct = new Product();
					creditCurrency = new Currency();
					creditProduct = new Product();
					paymentService = new PaymentServiceRequest();
					scheduledPayment = new ScheduledPaymentRequest();
					/** datos de la tabla pago recurrente **/
					scheduledPayment.setId(Integer.parseInt(columns[0].getValue()));// pr_id
					user.setEntityId(Integer.parseInt(columns[1].getValue()));// pr_ente
					debitProduct.setProductNumber(columns[2].getValue());// pr_cuenta
					debitCurrency.setCurrencyId(Integer.parseInt(columns[3].getValue()));// pr_moneda
					debitProduct.setProductType(Integer.parseInt(columns[4].getValue()));// pr_producto
					scheduledPayment.setType(columns[5].getValue());// pr_tipo
					scheduledPayment.setCode(Integer.parseInt(columns[6].getValue()));// pr_codigo
					scheduledPayment.setItem(columns[7].getValue());// pr_item
					creditProduct.setProductNumber(columns[8].getValue());// pr_cuenta_cr
					creditCurrency.setCurrencyId(Integer.parseInt(columns[9].getValue()));// pr_moneda_cr
					creditProduct.setProductType(Integer.parseInt(columns[10].getValue()));// pr_producto_cr
					scheduledPayment.setAmount(Double.parseDouble(columns[11].getValue()));// pr_valor
					scheduledPayment.setInitialDate(columns[12].getValue());// pr_fecha_ini
					scheduledPayment.setPaymentsNumber(Integer.parseInt(columns[13].getValue()));// pr_num_pagos
					scheduledPayment.setFrecuencyId(columns[14].getValue());// pr_num_dias
					scheduledPayment.setQuantityOfDonePayments(Integer.parseInt(columns[15].getValue()));// pr_pagos_realizados
					scheduledPayment.setRegisterDate(columns[16].getValue());// pr_fecha_reg
					scheduledPayment.setModifiedDate(columns[17].getValue());// pr_fecha_mod
					scheduledPayment.setStatus(columns[18].getValue());// pr_estado
					scheduledPayment.setProcessedStatus(columns[19].getValue());// pr_estado_proc
					scheduledPayment.setConcept(columns[20].getValue());// pr_concepeto
					// falta pr_au_trn de ser necesario
					client.setLogin(columns[22].getValue());// pr_login
					scheduledPayment.setOption(columns[23].getValue());// pr_opcion
					scheduledPayment.setBeneficiaryName(columns[24].getValue());// pr_beneficiario
					scheduledPayment.setReceiveNotification(columns[25].getValue());// pr_notificacion
					scheduledPayment.setDayToNotify(Integer.parseInt(columns[26].getValue()));// pr_dias_notif
					scheduledPayment.setRecoveryRetryFailed(columns[27].getValue());// pr_reintentar_cobro
					scheduledPayment.setDayToRecoveryRetry(columns[28].getValue());// pr_dias_reintento
					scheduledPayment.setDayToProcesedRecoveryRetry(columns[29].getValue());// pr_dias_reint_proc
					scheduledPayment.setNextPaymentDate(columns[30].getValue());// pr_fecha_prox_pago
					scheduledPayment.setLastRecoveryDate(columns[31].getValue());// pr_fech_ult_cobro
					scheduledPayment.setErrorMessage(columns[32].getValue());// pr_mensaje_error
					if (columns[33].getValue() != null) {
						paymentService.setContractId(Integer.parseInt(columns[33].getValue()));// pr_convenio
					}
					paymentService.setInterfaceType(columns[34].getValue());// pr_tipo_interfaz
					paymentService.setCategoryId(columns[35].getValue());// pr_categoria
					if (columns[36].getValue() != null) {
						paymentService.setFundsSource(columns[36].getValue());// pr_origen_fondos
					}
					if (columns[37].getValue() != null) {
						paymentService.setFundsUse(columns[37].getValue());// pr_dest_fondos
					}

					if (scheduledPayment.getType().equals("S")) {
						/*** datos de la tabla param ***/
						paymentService.setDocumentType(columns[36].getValue());// pp_tipo_doc
						scheduledPayment.setKey(columns[37].getValue()); // pp_llave
						paymentService.setDocumentId(columns[38].getValue());// pp_num_doc
						paymentService.setRef1(columns[39].getValue());
						paymentService.setRef2(columns[40].getValue());
						paymentService.setRef3(columns[41].getValue());
						paymentService.setRef4(columns[42].getValue());
						paymentService.setRef5(columns[43].getValue());
						paymentService.setRef6(columns[44].getValue());
						paymentService.setRef7(columns[45].getValue());
						paymentService.setRef8(columns[46].getValue());
						paymentService.setRef9(columns[47].getValue());
						paymentService.setRef10(columns[48].getValue());
						paymentService.setRef11(columns[49].getValue());
						paymentService.setRef12(columns[50].getValue());
					}
					debitProduct.setCurrency(debitCurrency);
					creditProduct.setCurrency(creditCurrency);
					scheduledPayment.setDebitProduct(debitProduct);
					scheduledPayment.setCreditProduct(creditProduct);
					scheduledPayment.setPaymentService(paymentService);
					scheduledPayment.setUser(user);
					scheduledPayment.setClient(client);

					if (logger.isInfoEnabled())
						logger.logInfo("transforToBatchScheduledPaymentResponse(...) :: scheduledPayment="
								+ scheduledPayment.toString());

					scheduledPaymentList.add(scheduledPayment);
				} catch (Exception e) {
					if (logger.isInfoEnabled())
						logger.logInfo("Excecpcion rperero(...) :: scheduledPayment=" + e);
				}
			}
			batchScheduledPaymentResponse.setListScheduledPayment(scheduledPaymentList);
			batchScheduledPaymentResponse.setSuccess(true);
		} else {
			if (logger.isInfoEnabled())
				logger.logInfo("Error al transformar en metodo transforToBatchScheduledPaymentResponse ");
			Message[] messages = Utils.returnArrayMessage(response);
			batchScheduledPaymentResponse.setMessages(messages);
			batchScheduledPaymentResponse.setSuccess(false);
		}

		if (response.readValueParam("@o_total_registros") != null) {
			batchScheduledPaymentResponse
					.setTotalRecords(Integer.parseInt(response.readValueParam("@o_total_registros")));
		}

		if (response.readValueParam("@o_registro_max") != null) {
			batchScheduledPaymentResponse.setMaxRecord(Integer.parseInt(response.readValueParam("@o_registro_max")));
		}

		batchScheduledPaymentResponse.setReturnCode(response.getReturnCode());
		if (logger.isInfoEnabled())
			logger.logInfo("termina el metodo BatchScheduledPaymentResponse y retorna: "
					+ batchScheduledPaymentResponse.toString());
		return batchScheduledPaymentResponse;
	}

	/** Funcion para procesar respuesta final **/
	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		return null;
	}

	/**
	 * funcion para transformar de un IprocedureRequest a
	 * BatchScheduledPaymentRequest
	 **/
	private BatchScheduledPaymentRequest transformToBatchScheduledPaymentRequest(IProcedureRequest aProcedureRequest) {

		BatchScheduledPaymentRequest request = new BatchScheduledPaymentRequest();
		ScheduledPaymentRequest scheduledPayment = new ScheduledPaymentRequest();
		Batch batch = new Batch();
		if (aProcedureRequest.readValueParam("@i_numero_registros") != null) {
			request.setRecordNumber(Integer.parseInt(aProcedureRequest.readValueParam("@i_numero_registros")));
		}
		if (aProcedureRequest.readValueParam("@i_siguiente") != null) {
			request.setNext(Integer.parseInt(aProcedureRequest.readValueParam("@i_siguiente")));
		}
		if (aProcedureRequest.readValueParam("@i_tipo_pago") != null) {
			scheduledPayment.setType(aProcedureRequest.readValueParam("@i_tipo_pago"));
		}
		if (aProcedureRequest.readValueParam("@i_sarta") != null) {
			batch.setSarta(Integer.parseInt(aProcedureRequest.readValueParam("@i_sarta")));
		}
		if (aProcedureRequest.readValueParam("@i_batch") != null) {
			batch.setBatch(Integer.parseInt(aProcedureRequest.readValueParam("@i_batch")));
		}
		if (aProcedureRequest.readValueParam("@i_secuencial") != null) {
			batch.setSecuencial(Integer.parseInt(aProcedureRequest.readValueParam("@i_secuencial")));
		}
		request.setScheduledPayment(scheduledPayment);
		request.setBatch(batch);
		if (logger.isInfoEnabled())
			logger.logInfo("Retorno de metodo transformToBatchScheduledPaymentRequest : " + request);
		return request;
	}

	public void loadLocatorConfiguration() {
		if (logger.isInfoEnabled())
			logger.logInfo("Ingresa a loadConfiguration");
		componentLocator = ComponentLocator.getInstance(this);
		dbServiceFactory = (IDBServiceFactory) componentLocator.find(IDBServiceFactory.class);
		String dbms = "SQLCTS";
		String dbmsServiceProvider = "DataSource";
		dbServiceProvider = dbServiceFactory.getDBServiceProvider(dbms, dbmsServiceProvider);
	}

	private void executeBdd(List<String> inserts) {
		String methodInfo = "[executeBdd]";
		Connection connection = null;
		Statement stmt = null;
		try {
			connection = dbServiceProvider.getDBConnection();
			if (logger.isInfoEnabled())
				logger.logInfo("connection *** :" + connection);
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
