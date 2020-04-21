package com.cobiscorp.ecobis.ib.orchestration.batch.scheduledpayment;

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
import com.cobiscorp.ecobis.ib.application.dtos.ScheduledPaymentRequest;

public class BatchScheduledPaymentLoan extends SPJavaOrchestrationBase {

	private static final String CANAL = "1";
	private static ILogger logger = LogFactory.getLogger(BatchScheduledPaymentLoan.class);
	private static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";

	/******* Funcion para manejar el pago de programado de prestamos *******/
	public IProcedureResponse executeLoans(ScheduledPaymentRequest scheduledPayment,
			Map<String, Object> aBagSPJavaOrchestration) throws Exception {

		IProcedureResponse pResponse = null;
		IProcedureRequest pRequest = this.setParametersLoans(scheduledPayment, aBagSPJavaOrchestration);
		aBagSPJavaOrchestration.put("request", pRequest);
		// pRequest = this.getLoanBanlance(scheduledPayment,
		// aBagSPJavaOrchestration);

		try {
			if (logger.isInfoEnabled())
				logger.logInfo("Request executeLoans : " + pRequest);
			pResponse = executeCoreBanking(pRequest);
			if (logger.isInfoEnabled())
				logger.logInfo("Response executeLoans : " + pResponse);

		} catch (Exception e) {
			pResponse.setReturnCode(1);
			pResponse.setText("Error al ejecutar orquestacion de pago de prestamos");
			if (logger.isDebugEnabled()) {
				logger.logDebug("Error al ejecutar orquestacion de pago de prestamos: " + e);
			}
		}
		return pResponse;
	}

	private IProcedureRequest setParametersLoans(ScheduledPaymentRequest scheduledPayment,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo("INICIANDO METODO: setParametersLoans");
		IProcedureRequest originalRquest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		IProcedureRequest pRquest = new ProcedureRequestAS();

		if (logger.isInfoEnabled())
			logger.logInfo("ORIGINAL REQUEST EN EL METODO setParametersLoans : " + pRquest);
		pRquest.setSpName("cob_procesador..sp_tr_pago_prestamo_cca");
		pRquest.addFieldInHeader("trn", ICOBISTS.HEADER_NUMBER_TYPE, "1800025");
		pRquest.addFieldInHeader("servicio", ICOBISTS.HEADER_NUMBER_TYPE, CANAL);
		pRquest.addFieldInHeader("cliente", ICOBISTS.HEADER_NUMBER_TYPE,
				scheduledPayment.getUser().getEntityId().toString());
		pRquest.addFieldInHeader("user", ICOBISTS.HEADER_STRING_TYPE, originalRquest.readValueParam("@s_user"));

		pRquest.addInputParam("@s_cliente", ICTSTypes.SQLINT4, scheduledPayment.getUser().getEntityId().toString());// Ejemplo:
																													// 277
		pRquest.addInputParam("@s_servicio", ICTSTypes.SQLINT2, CANAL);
		pRquest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, originalRquest.readValueParam("@s_user"));
		pRquest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "1800025");

		pRquest.addInputParam("@i_tercero_asociado", ICTSTypes.SQLCHAR, "N");
		pRquest.addInputParam("@i_tercero", ICTSTypes.SQLCHAR, "N");
		pRquest.addInputParam("@i_doble_autorizacion", ICTSTypes.SQLCHAR, "N");
		pRquest.addInputParam("@i_prod", ICTSTypes.SQLINT2,
				scheduledPayment.getDebitProduct().getProductType().toString());
		pRquest.addInputParam("@i_recurr_id", ICTSTypes.SQLINT4, scheduledPayment.getId().toString());
		pRquest.addInputParam("@i_mon", ICTSTypes.SQLINT2,
				scheduledPayment.getDebitProduct().getCurrency().getCurrencyId().toString());
		pRquest.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, scheduledPayment.getClient().getLogin());
		pRquest.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, scheduledPayment.getDebitProduct().getProductNumber());
		pRquest.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, scheduledPayment.getConcept());
		pRquest.addInputParam("@i_nom_cliente_benef", ICTSTypes.SQLVARCHAR, scheduledPayment.getBeneficiaryName());
		pRquest.addInputParam("@i_val", ICTSTypes.SQLMONEY, scheduledPayment.getAmount().toString());
		pRquest.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR,
				scheduledPayment.getCreditProduct().getProductNumber());
		pRquest.addInputParam("@i_prod_des", ICTSTypes.SQLINT2,
				scheduledPayment.getCreditProduct().getProductType().toString());
		pRquest.addInputParam("@i_mon_des", ICTSTypes.SQLINT2,
				scheduledPayment.getCreditProduct().getCurrency().getCurrencyId().toString());
		pRquest.addInputParam("@i_monto", ICTSTypes.SQLMONEY, scheduledPayment.getAmount().toString());
		pRquest.addInputParam("@i_monto_mpg", ICTSTypes.SQLMONEY, scheduledPayment.getAmount().toString());
		pRquest.addInputParam("@i_ente", ICTSTypes.SQLINT4, scheduledPayment.getUser().getEntityId().toString());
		pRquest.addInputParam("@i_origen_fondos", ICTSTypes.SQLVARCHAR, scheduledPayment.getFundsSource());
		pRquest.addInputParam("@i_dest_fondos", ICTSTypes.SQLVARCHAR, scheduledPayment.getFundsUse());
		pRquest.addInputParam("@t_ejec", ICTSTypes.SQLVARCHAR, "R");
		pRquest.addInputParam("@t_rty", ICTSTypes.SQLVARCHAR, "N");

		if (logger.isInfoEnabled())
			logger.logInfo("Request setParametersLoans : " + pRquest);

		if (logger.isInfoEnabled())
			logger.logInfo("FINALIZANDO METODO: setParametersLoans");
		return pRquest;
	}

	private IProcedureRequest getLoanBanlance(ScheduledPaymentRequest scheduledPayment,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo("Iniciando el metodo  : getLoanBanlance");

		IProcedureResponse pResponse = null;
		IProcedureRequest pRequestToReturn = null;
		IProcedureRequest request = new ProcedureRequestAS();

		request.setSpName("cob_procesador..sp_tr_consulta_prestamo_cca");
		request.addFieldInHeader("trn", ICOBISTS.HEADER_NUMBER_TYPE, "1800024");
		request.addFieldInHeader("servicio", ICOBISTS.HEADER_NUMBER_TYPE, CANAL);
		request.addFieldInHeader("cliente", ICOBISTS.HEADER_NUMBER_TYPE,
				scheduledPayment.getUser().getEntityId().toString());

		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, "1800024");
		request.addInputParam("@s_cliente", ICTSTypes.SQLINT4, scheduledPayment.getUser().getEntityId().toString());// Ejemplo:
																													// 277
		request.addInputParam("@s_servicio", ICTSTypes.SQLINT2, CANAL);
		request.addInputParam("@i_prod", ICTSTypes.SQLINT1,
				scheduledPayment.getCreditProduct().getProductType().toString());
		request.addInputParam("@i_mon", ICTSTypes.SQLINT2,
				scheduledPayment.getCreditProduct().getCurrency().getCurrencyId().toString());
		request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, scheduledPayment.getClient().getLogin());
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, scheduledPayment.getCreditProduct().getProductNumber());
		request.addInputParam("@i_valida_des", ICTSTypes.SQLVARCHAR, "N");
		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "C");
		request.addInputParam("@t_ejec", ICTSTypes.SQLCHAR, "R");
		request.addInputParam("@t_rty", ICTSTypes.SQLCHAR, "N");
		if (logger.isInfoEnabled())
			logger.logInfo("Request  loanBalanceDetailPaymentQueryOrchestationCore: " + request);

		try {
			if (logger.isInfoEnabled())
				logger.logInfo("Request getLoanBanlance: " + request);
			pResponse = executeCoreBanking(request);
			if (logger.isInfoEnabled()) {
				logger.logInfo("Response  loanBalanceDetailPaymentQueryOrchestationCore: " + pResponse.toString());
				logger.logInfo("Codigo de error ejecuacion de la consulta de los saldos del prestamo:"
						+ pResponse.getReturnCode());
			}

			pRequestToReturn = (IProcedureRequest) aBagSPJavaOrchestration.get("request");
			if (pResponse.getReturnCode() == 0) {
				IResultSetRow[] rowsBalanceDatailPayment = pResponse.getResultSet(1).getData().getRowsAsArray();
				for (IResultSetRow iResultSetRow : rowsBalanceDatailPayment) {

					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

					if (columns[18].getValue() != null) {
						if (Double.parseDouble(columns[18].getValue()) > 0) {
							if (logger.isInfoEnabled())
								logger.logInfo("El valor del prestamo es mayor que cero : " + columns[18].getValue());
							pRequestToReturn.setValueParam("@i_monto_mpg", columns[18].getValue());
							pRequestToReturn.setValueParam("@i_monto", columns[18].getValue());
							pRequestToReturn.setValueParam("@i_val", columns[18].getValue());
						}
					}
					pRequestToReturn.addInputParam("@i_cotizacion", ICTSTypes.SQLFLT8i, columns[19].getValue());
				}
			}

		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.logDebug("Error al ejecutar getLoanBanlance: " + e.toString());
			}
		}
		if (logger.isInfoEnabled())
			logger.logInfo("respuesta final a devolver: " + pRequestToReturn);
		return pRequestToReturn;
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
