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
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.ecobis.ib.application.dtos.ScheduledPaymentRequest;

public class BatchScheduledPaymentTransfers extends SPJavaOrchestrationBase {

	private static final String CANAL = "1";
	private static ILogger logger = LogFactory.getLogger(BatchScheduledPaymentTransfers.class);

	/*******
	 * Funcion para manejar el pago de programado de transferencias
	 *******/
	public IProcedureResponse executeTransfers(ScheduledPaymentRequest scheduledPayment,
			Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureResponse pResponse = null;
		IProcedureRequest pRequest = this.setParametersTransfers(scheduledPayment, aBagSPJavaOrchestration);

		try {
			if (logger.isInfoEnabled())
				logger.logInfo("Request executeTransfers " + pRequest);
			pResponse = executeCoreBanking(pRequest);
			if (logger.isInfoEnabled())
				logger.logInfo("Response executeTransfers " + pResponse);

		} catch (Exception e) {
			pResponse.setReturnCode(1);
			pResponse.setText("Error al ejecutar orquestacion transferencias");
			if (logger.isDebugEnabled()) {
				logger.logDebug("Error al ejecutar orquestacion de transferencias: " + e);
			}
		}
		return pResponse;
	}

	private IProcedureRequest setParametersTransfers(ScheduledPaymentRequest scheduledPayment,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled())
			logger.logInfo("INICIANDO METODOS: setParametersTransfers");

		// IProcedureRequest pRquest = (IProcedureRequest)
		// aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		IProcedureRequest pRquest = new ProcedureRequestAS();
		pRquest.setSpName("cob_procesador..sp_tr_transferencias_ter");
		pRquest.addFieldInHeader("trn", ICOBISTS.HEADER_NUMBER_TYPE, "1800012");
		pRquest.addFieldInHeader("servicio", ICOBISTS.HEADER_NUMBER_TYPE, CANAL);
		pRquest.addFieldInHeader("cliente", ICOBISTS.HEADER_NUMBER_TYPE,
				scheduledPayment.getUser().getEntityId().toString());

		pRquest.addInputParam("@s_cliente", ICTSTypes.SQLINT4, scheduledPayment.getUser().getEntityId().toString());// Ejemplo:
																													// 277
		pRquest.addInputParam("@s_servicio", ICTSTypes.SQLINT2, CANAL);

		pRquest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "1800012");
		pRquest.addInputParam("@i_recurr_id", ICTSTypes.SQLINT4, scheduledPayment.getId().toString());
		pRquest.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, scheduledPayment.getClient().getLogin());
		pRquest.addInputParam("@i_ente", ICTSTypes.SQLVARCHAR, scheduledPayment.getUser().getEntityId().toString());

		if (logger.isInfoEnabled())
			logger.logInfo(
					"setParametersTransfers() -> pRquest.readValueParam(@t_trn)=" + pRquest.readValueParam("@t_trn"));
		pRquest.addInputParam("@i_mon", ICTSTypes.SQLINT2,
				scheduledPayment.getDebitProduct().getCurrency().getCurrencyId().toString());
		pRquest.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, scheduledPayment.getDebitProduct().getProductNumber());
		pRquest.addInputParam("@i_prod", ICTSTypes.SQLINT2,
				scheduledPayment.getDebitProduct().getProductType().toString());
		pRquest.addInputParam("@i_mon_des", ICTSTypes.SQLINT2,
				scheduledPayment.getCreditProduct().getCurrency().getCurrencyId().toString());
		pRquest.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR,
				scheduledPayment.getCreditProduct().getProductNumber());
		pRquest.addInputParam("@i_prod_des", ICTSTypes.SQLINT2,
				scheduledPayment.getCreditProduct().getProductType().toString());
		pRquest.addInputParam("@i_val", ICTSTypes.SQLMONEY, scheduledPayment.getAmount().toString());
		pRquest.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, scheduledPayment.getConcept());
		pRquest.addInputParam("@i_origen_fondos", ICTSTypes.SQLVARCHAR, scheduledPayment.getFundsSource());
		pRquest.addInputParam("@i_dest_fondos", ICTSTypes.SQLVARCHAR, scheduledPayment.getFundsUse());

		if (logger.isInfoEnabled())
			logger.logInfo("FINALIZANDO METODO: setParametersTransfers");
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
