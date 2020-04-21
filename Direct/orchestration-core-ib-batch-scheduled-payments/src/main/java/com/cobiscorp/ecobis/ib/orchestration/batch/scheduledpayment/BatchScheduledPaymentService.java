package com.cobiscorp.ecobis.ib.orchestration.batch.scheduledpayment;

import java.math.BigDecimal;
import java.util.Map;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.ScheduledPaymentRequest;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BasedBilling;
import com.cobiscorp.ecobis.ib.orchestration.dtos.OnlinePaymentDetail;

public class BatchScheduledPaymentService extends SPJavaOrchestrationBase {

	private static final String CANAL = "1";
	private static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";

	private static ILogger logger = LogFactory.getLogger(BatchScheduledPaymentService.class);

	/******* Funcion para manejar el pago de programado de servicios *******/
	public IProcedureResponse executePaymentService(ScheduledPaymentRequest scheduledPayment,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo("INICIANDO METODO: executePaymentService");

		IProcedureResponse pResponse = null;
		IProcedureRequest pRequest = null;
		pRequest = this.setParametersPaymentService(scheduledPayment, aBagSPJavaOrchestration);

		char interfazType = scheduledPayment.getPaymentService().getInterfaceType().charAt(0);
		switch (interfazType) {

		case 'B':
			BasedBilling basedBilling = this.getDataBaseInvoincing(scheduledPayment, aBagSPJavaOrchestration);
			if (basedBilling != null) {
				if (basedBilling.getSequential() != null) {
					pRequest.setValueParam("@i_secuencial", basedBilling.getSequential().toString());
				}
				if (basedBilling.getAmount() != null) {
					// si el valor de la consulta es mayor que cero entonces se
					// reemplaza el valor
					if (basedBilling.getAmount().compareTo(new BigDecimal("0")) > 0) {
						pRequest.setValueParam("@i_val", basedBilling.getAmount().toString());
					}
				}
			} else {
				if (logger.isInfoEnabled()) {
					logger.logInfo("Consulta a basedBilling devuelve null. No se encontraron datos");
				}
			}
			break;

		case 'L':
			OnlinePaymentDetail onlinePayment = this.getDataPayServiceOnline(scheduledPayment, aBagSPJavaOrchestration);
			if (onlinePayment != null) {
				if (onlinePayment.getTotalPay() != null) {
					if (onlinePayment.getTotalPay().compareTo(new BigDecimal("0")) > 0) {
						pRequest.setValueParam("@i_val", onlinePayment.getTotalPay().toString());
					}
				}
			} else {
				if (logger.isInfoEnabled()) {
					logger.logInfo("Consulta a onlinePaymentDetail devuelve null. No se encontraron datos");
				}
			}
			break;
		case 'N':
			break;

		}

		try {
			if (logger.isDebugEnabled()) {
				logger.logDebug("Request ServicePaymentOrchestationCore: " + pRequest);
			}
			pResponse = executeCoreBanking(pRequest);
			if (logger.isDebugEnabled()) {
				logger.logDebug("Response ServicePaymentOrchestationCore: " + pResponse);
			}

		} catch (Exception e) {
			pResponse.setReturnCode(1);
			pResponse.setText("Error al ejecutar orquestacion de pago de servicios");
			if (logger.isDebugEnabled()) {
				logger.logDebug("Error al ejecutar orquestacion de pago de servicios: " + e);
			}
		}
		if (logger.isInfoEnabled())
			logger.logInfo("FINALIZANDO METODO: executePaymentService");
		return pResponse;
	}

	public BasedBilling getDataBaseInvoincing(ScheduledPaymentRequest scheduledPaymentRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo("INICIANDO METODO: getDataBaseInvoincing");

		IProcedureResponse pResponse = new ProcedureResponseAS();
		IProcedureRequest pRequest = new ProcedureRequestAS();
		// IProcedureRequest pRequest = (IProcedureRequest)
		// aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		pRequest.setSpName("cob_procesador..sp_consulta_base_fact");
		pRequest.addFieldInHeader("trn", ICOBISTS.HEADER_NUMBER_TYPE, "668");

		pRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "668");
		pRequest.addInputParam("@i_formato_fecha", ICTSTypes.SQLVARCHAR, "103");
		pRequest.addInputParam("@i_busqueda1", ICTSTypes.SQLVARCHAR,
				scheduledPaymentRequest.getPaymentService().getDocumentId());
		pRequest.addInputParam("@i_identificacion", ICTSTypes.SQLVARCHAR,
				scheduledPaymentRequest.getPaymentService().getDocumentType());
		pRequest.addInputParam("@i_nombre", ICTSTypes.SQLVARCHAR, scheduledPaymentRequest.getKey());
		pRequest.addInputParam("@i_convenio", ICTSTypes.SQLINT4,
				scheduledPaymentRequest.getPaymentService().getContractId().toString());
		pRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "S");

		if (logger.isInfoEnabled())
			logger.logInfo("Request getDataBaseInvoincing : " + pRequest);
		pResponse = executeCoreBanking(pRequest);
		if (logger.isInfoEnabled())
			logger.logInfo("Response getDataBaseInvoincing : " + pResponse);
		if (logger.isInfoEnabled())
			logger.logInfo("FINALIZANDO METODO: getDataBaseInvoincing");
		return this.transformProcedureResponseToBaseBilling(pResponse);
	}

	/**
	 * Configurar parametros de entrada para la orquestacion de pagos de
	 * servicios
	 ***/
	private BasedBilling transformProcedureResponseToBaseBilling(IProcedureResponse pResponse) {

		BasedBilling basedBilling = new BasedBilling();
		if (logger.isInfoEnabled())
			logger.logInfo("INICIANDO METODO: transformProcedureResponseToBaseBilling");

		if (pResponse.getReturnCode() == 0) {

			IResultSetRow[] rowQueryBasedBilling = pResponse.getResultSet(1).getData().getRowsAsArray();
			for (IResultSetRow iResultSetRow : rowQueryBasedBilling) {

				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				basedBilling.setIdentification(columns[0].getValue());
				basedBilling.setDebtorName(columns[1].getValue());
				basedBilling.setReference1(columns[2].getValue());
				basedBilling.setReference2(columns[3].getValue());
				basedBilling.setReference3(columns[4].getValue());
				basedBilling.setAmount(new BigDecimal(columns[5].getValue()));
				basedBilling.setPaymentDay(columns[6].getValue());
				basedBilling.setSequential(Integer.parseInt(columns[7].getValue()));
			}
		}

		if (logger.isInfoEnabled())
			logger.logInfo("Response METODO: transformProcedureResponseToBaseBilling : " + basedBilling);
		if (logger.isInfoEnabled())
			logger.logInfo("FINALIZANDO METODO: transformProcedureResponseToBaseBilling");
		return basedBilling;
	}

	public OnlinePaymentDetail getDataPayServiceOnline(ScheduledPaymentRequest scheduledPaymentRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo("INICIANDO METODO: getDataPayServiceOnline");

		IProcedureResponse pResponse = new ProcedureResponseAS();
		IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		IProcedureRequest pRequest = new ProcedureRequestAS();

		pRequest.setSpName("cob_procesador..sp_consulta_servicio_enlinea");
		pRequest.addFieldInHeader("trn", ICOBISTS.HEADER_NUMBER_TYPE, "670");

		pRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "670");
		pRequest.addInputParam("@i_oficina", ICTSTypes.SQLVARCHAR, originalRequest.readValueParam("@s_ofi"));
		pRequest.addInputParam("@i_convenio", ICTSTypes.SQLINT4,
				scheduledPaymentRequest.getPaymentService().getContractId().toString());
		pRequest.addInputParam("@i_tipo_doc", ICTSTypes.SQLVARCHAR,
				scheduledPaymentRequest.getPaymentService().getDocumentType());
		pRequest.addInputParam("@i_llave", ICTSTypes.SQLVARCHAR,
				scheduledPaymentRequest.getPaymentService().getDocumentType());
		pRequest.addInputParam("@i_num_doc", ICTSTypes.SQLVARCHAR,
				scheduledPaymentRequest.getPaymentService().getDocumentId());
		pRequest.addInputParam("@i_usuario", ICTSTypes.SQLVARCHAR, scheduledPaymentRequest.getClient().getLogin());

		if (logger.isInfoEnabled())
			logger.logInfo("Request getDataPayServiceOnline : " + pRequest);
		pResponse = executeCoreBanking(pRequest);
		if (logger.isInfoEnabled())
			logger.logInfo("Response getDataPayServiceOnline : " + pResponse);

		if (logger.isInfoEnabled())
			logger.logInfo("FINALIZANDO METODO: getDataPayServiceOnline");
		return this.transformProcedureResponseToOnlinePayment(pResponse);
	}

	private OnlinePaymentDetail transformProcedureResponseToOnlinePayment(IProcedureResponse pResponse) {

		if (logger.isInfoEnabled())
			logger.logInfo("INICIANDO METODO: transformProcedureResponseToOnlinePayment");
		OnlinePaymentDetail onlinePaymentDetail = new OnlinePaymentDetail();

		if (pResponse.getReturnCode() == 0) {

			IResultSetRow[] rowQueryOnlinePayment = pResponse.getResultSet(2).getData().getRowsAsArray();
			for (IResultSetRow iResultSetRow : rowQueryOnlinePayment) {

				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				onlinePaymentDetail.setContractId(columns[0].getValue());
				onlinePaymentDetail.setNumber(columns[1].getValue());
				onlinePaymentDetail.setReceipts(Integer.parseInt(columns[2].getValue()));
				onlinePaymentDetail.setTotalPay(new BigDecimal(columns[3].getValue()));
				onlinePaymentDetail.setPeriod(columns[4].getValue());
				onlinePaymentDetail.setExpirationDate(columns[5].getValue());
				onlinePaymentDetail.setReceiptNumber(columns[6].getValue());
				onlinePaymentDetail.setTotalPayment(new BigDecimal(columns[7].getValue()));
				onlinePaymentDetail.setSelf(columns[8].getValue());
				onlinePaymentDetail.setThridPartyPaymentKey(columns[9].getValue());
			}
		}

		if (logger.isInfoEnabled())
			logger.logInfo("FINALIZANDO METODO: transformProcedureResponseToOnlinePayment");
		return onlinePaymentDetail;

	}

	/**
	 * Configurar parametros de entrada para la orquestacion de pagos de
	 * servicios
	 ***/
	private IProcedureRequest setParametersPaymentService(ScheduledPaymentRequest scheduledPayment,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo("INICIANDO METODO: setParametersPaymentService ");

		IProcedureRequest originalRquest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		IProcedureRequest pRquest = new ProcedureRequestAS();

		pRquest.setSpName("cob_procesador..sp_tr_recaudo_bd");
		pRquest.addFieldInHeader("trn", ICOBISTS.HEADER_NUMBER_TYPE, "1801035");
		pRquest.addFieldInHeader("servicio", ICOBISTS.HEADER_NUMBER_TYPE, CANAL);
		pRquest.addFieldInHeader("cliente", ICOBISTS.HEADER_NUMBER_TYPE,
				scheduledPayment.getUser().getEntityId().toString());

		pRquest.addFieldInHeader("srv", ICOBISTS.HEADER_STRING_TYPE, originalRquest.readValueParam("@s_srv"));
		pRquest.addFieldInHeader("ofi", ICOBISTS.HEADER_NUMBER_TYPE, originalRquest.readValueParam("@s_ofi"));
		pRquest.addFieldInHeader("rol", ICOBISTS.HEADER_NUMBER_TYPE, originalRquest.readValueParam("@s_rol"));

		if (logger.isInfoEnabled())
			logger.logInfo("ORIGINAL REQUEST EN EL METODO setParametersPaymentService : " + pRquest);

		pRquest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "1801035");// numero
																		// de
																		// transaccion
																		// para
																		// pago
																		// de
																		// servicios
		pRquest.addInputParam("@s_cliente", ICTSTypes.SQLINT4, scheduledPayment.getUser().getEntityId().toString());// Ejemplo:
																													// 277
		pRquest.addInputParam("@s_servicio", ICTSTypes.SQLINT2, CANAL);
		pRquest.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, originalRquest.readValueParam("@s_srv"));
		pRquest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, originalRquest.readValueParam("@s_ofi"));
		pRquest.addInputParam("@s_rol", ICTSTypes.SQLINT2, originalRquest.readValueParam("@s_rol"));

		pRquest.addInputParam("@t_ejec", ICTSTypes.SQLCHAR, "R");
		pRquest.addInputParam("@t_rty", ICTSTypes.SQLCHAR, "N");
		pRquest.addInputParam("@i_result", ICTSTypes.SQLCHAR, "T");
		pRquest.addInputParam("@i_doble_autorizacion", ICTSTypes.SQLCHAR, "N");
		pRquest.addInputParam("@i_tercero_asociado", ICTSTypes.SQLCHAR, "N");
		pRquest.addInputParam("@i_tercero", ICTSTypes.SQLCHAR, "S");

		pRquest.addInputParam("@i_recurr_id", ICTSTypes.SQLINT4, scheduledPayment.getId().toString());
		pRquest.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, scheduledPayment.getClient().getLogin());
		pRquest.addInputParam("@i_ente", ICTSTypes.SQLVARCHAR, scheduledPayment.getUser().getEntityId().toString());

		pRquest.addInputParam("@i_canal", ICTSTypes.SQLVARCHAR, CANAL);
		pRquest.addInputParam("@i_ejecuta_consulta", ICTSTypes.SQLVARCHAR, "N");

		pRquest.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, scheduledPayment.getDebitProduct().getProductNumber());
		pRquest.addInputParam("@i_mon", ICTSTypes.SQLINT4,
				scheduledPayment.getDebitProduct().getCurrency().getCurrencyId().toString());
		pRquest.addInputParam("@i_prod", ICTSTypes.SQLINT4,
				scheduledPayment.getDebitProduct().getProductType().toString());

		pRquest.addInputParam("@i_val", ICTSTypes.SQLVARCHAR, scheduledPayment.getAmount().toString());
		pRquest.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, scheduledPayment.getConcept());
		pRquest.addInputParam("@i_convenio", ICTSTypes.SQLINT4,
				scheduledPayment.getPaymentService().getContractId().toString());
		pRquest.addInputParam("@i_llave", ICTSTypes.SQLVARCHAR, scheduledPayment.getKey());
		pRquest.addInputParam("@i_tipo_doc", ICTSTypes.SQLVARCHAR,
				scheduledPayment.getPaymentService().getDocumentType());
		pRquest.addInputParam("@i_num_doc", ICTSTypes.SQLVARCHAR, scheduledPayment.getPaymentService().getDocumentId());
		pRquest.addInputParam("@i_tipo_interfaz", ICTSTypes.SQLVARCHAR,
				scheduledPayment.getPaymentService().getInterfaceType());
		pRquest.addInputParam("@i_ref_1", ICTSTypes.SQLVARCHAR, scheduledPayment.getPaymentService().getRef1());
		pRquest.addInputParam("@i_ref_2", ICTSTypes.SQLVARCHAR, scheduledPayment.getPaymentService().getRef2());
		pRquest.addInputParam("@i_ref_3", ICTSTypes.SQLVARCHAR, scheduledPayment.getPaymentService().getRef3());
		pRquest.addInputParam("@i_ref_4", ICTSTypes.SQLVARCHAR, scheduledPayment.getPaymentService().getRef4());
		pRquest.addInputParam("@i_ref_5", ICTSTypes.SQLVARCHAR, scheduledPayment.getPaymentService().getRef5());
		pRquest.addInputParam("@i_ref_6", ICTSTypes.SQLVARCHAR, scheduledPayment.getPaymentService().getRef6());
		pRquest.addInputParam("@i_ref_7", ICTSTypes.SQLVARCHAR, scheduledPayment.getPaymentService().getRef7());
		pRquest.addInputParam("@i_ref_8", ICTSTypes.SQLVARCHAR, scheduledPayment.getPaymentService().getRef8());
		pRquest.addInputParam("@i_ref_9", ICTSTypes.SQLVARCHAR, scheduledPayment.getPaymentService().getRef9());
		pRquest.addInputParam("@i_ref_10", ICTSTypes.SQLVARCHAR, scheduledPayment.getPaymentService().getRef10());
		pRquest.addInputParam("@i_ref_11", ICTSTypes.SQLVARCHAR, scheduledPayment.getPaymentService().getRef11());
		pRquest.addInputParam("@i_ref_12", ICTSTypes.SQLVARCHAR, scheduledPayment.getPaymentService().getRef12());

		pRquest.addInputParam("@i_secuencial", ICTSTypes.SQLINT4, "0"); // se le
																		// pone
																		// valor
																		// 0 x
																		// default
		if (logger.isInfoEnabled())
			logger.logInfo("Request setParametersPaymentService : " + pRquest);

		if (logger.isInfoEnabled())
			logger.logInfo("FINALIZANDO METODO: setParametersPaymentService");
		return pRquest;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}
