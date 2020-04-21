package com.cobiscorp.ecobis.orchestration.core.ib.loans;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.CISResponseManagmentHelper;
import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;

/**
 * 
 * @author hsalazar
 * @since Nov 01, 2017
 * @version 1.0.0
 */

@Component(name = "LoanMovementsQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "LoanMovementsQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "LoanMovementsQueryOrchestrationCore") })
public class LoanMovementsQueryOrchestrationCore extends SPJavaOrchestrationBase {
	private static ILogger LOGGER = LogFactory.getLogger(LoanMovementsQueryOrchestrationCore.class);

	private static final String CLASS_NAME = "--->";
	static final String ORIGINAL_REQUEST = "originalRequest";
	static final String RESPONSE_LOANMOVEMENTS = "responseLoanMovements";

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		if (LOGGER.isInfoEnabled())
			LOGGER.logInfo("Ejecucion de loadConfiguration");
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.logDebug("Inicia executeJavaOrchestration");
		}

		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);
		try {
			this.getLoanMovements(aBagSPJavaOrchestration);
		} catch (CTSServiceException ex) {
			LOGGER.logError(ex);
		} catch (CTSInfrastructureException ex) {
			LOGGER.logError(ex);
		} finally {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.logDebug("Finaliza executeJavaOrchestration");
			}
		}

		return this.processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}

	private void getLoanMovements(Map<String, Object> aBagSPJavaOrchestration)
			throws CTSServiceException, CTSInfrastructureException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.logDebug("Inicia getLoanMovements");
		}
		IProcedureRequest wOriginalRequest = (IProcedureRequest) (aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));
		IProcedureRequest wProcedureRequest = initProcedureRequest(wOriginalRequest);

		wProcedureRequest.setSpName("cobis..BCU_sp_consulta_prestamo_his");
		wProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		wProcedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1890024");

		Utils.copyParam("@i_operacion", wOriginalRequest, wProcedureRequest);
		Utils.copyParam("@i_fecha_inicial", wOriginalRequest, wProcedureRequest);
		Utils.copyParam("@i_fecha_final", wOriginalRequest, wProcedureRequest);
		Utils.copyParam("@i_banco", wOriginalRequest, wProcedureRequest);
		Utils.copyParam("@i_secuencial", wOriginalRequest, wProcedureRequest);
		Utils.copyParam("@i_formato_fecha", wOriginalRequest, wProcedureRequest);
		Utils.copyParam("@i_rowcount", wOriginalRequest, wProcedureRequest);
		Utils.copyParam("@i_tipo_tran", wOriginalRequest, wProcedureRequest);

		IProcedureResponse wLoanMovementsResponse = executeCoreBanking(wProcedureRequest);
		aBagSPJavaOrchestration.put(RESPONSE_LOANMOVEMENTS, wLoanMovementsResponse);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.logDebug("Finaliza getLoanMovements");
			LOGGER.logDebug("Respuesta servicio consulta de movimientos de prestamos "
					+ wLoanMovementsResponse.getProcedureResponseAsString());
		}
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.logDebug(CLASS_NAME + " Inicia processResponse");
		}

		IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
		IProcedureResponse wLoanMovementsResponse = (IProcedureResponse) aBagSPJavaOrchestration
				.get(RESPONSE_LOANMOVEMENTS);

		String[] aProcedureRespKeys = { RESPONSE_LOANMOVEMENTS };
		CISResponseManagmentHelper.addResultsetsResponseS(wProcedureRespFinal, aProcedureRespKeys,
				aBagSPJavaOrchestration);

		if (wLoanMovementsResponse.hasError()) {
			CISResponseManagmentHelper.addMessagesS(wProcedureRespFinal, aProcedureRespKeys, aBagSPJavaOrchestration);
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE,
					ICSP.ERROR_EXECUTION_SERVICE);
		} else {
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE,
					ICSP.SUCCESS);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.logDebug(CLASS_NAME + " Finaliza processResponse");
		}
		return wProcedureRespFinal;
	}
}
