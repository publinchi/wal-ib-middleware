package com.cobiscorp.ecobis.orchestration.core.ib.cards.querys;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMovementsCardsQuery;

@Component(name = "MovementsCardsQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "MovementsCardsQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "MovementsCardsQueryOrchestrationCore") })
public class MovementsCardsQueryOrchestrationCore extends SPJavaOrchestrationBase {

	ILogger logger = this.getLogger();
	private static final String CLASS_NAME = "MovementsCardsQueryOrchestrationCore--->";
	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceMovementsCardsQuery.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	private ICoreServiceMovementsCardsQuery coreServiceMovementsQuery;

	/**
	 * Instance Service Interface
	 * 
	 * @param service
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
        if (logger.isInfoEnabled())
		logger.logInfo("Component-Name: " + this.getClass().getSimpleName() + ", Component-Version: $$project.version$$");
	}

	protected void bindCoreService(ICoreServiceMovementsCardsQuery service) {
		coreServiceMovementsQuery = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	protected void unbindCoreService(ICoreServiceMovementsCardsQuery service) {
		coreServiceMovementsQuery = null;
	}

	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	static final String OPERATION1_REQUEST = "OPERATION1_REQUEST";
	static final String OPERATION1_RESPONSE = "OPERATION1_RESPONSE";

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeJavaOrchestration");
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);
		IProcedureResponse wProcedureResponse = null;

		try {
			wProcedureResponse = coreServiceMovementsQuery.getDetailMovementCreditCards(anOriginalRequest);

			aBagSPJavaOrchestration.put("DETAIL_RESPONSE", wProcedureResponse);
			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);

		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.logInfo("*********  Error en " + e.getMessage(), e);
			}

			IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
			ErrorBlock eb = new ErrorBlock(-1, "Service is not available");
			wProcedureRespFinal.addResponseBlock(eb);
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
			wProcedureRespFinal.setReturnCode(-1);
			return wProcedureRespFinal;
		}

	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureRespFinal = (IProcedureResponse) aBagSPJavaOrchestration.get("DETAIL_RESPONSE");
		return wProcedureRespFinal;
	}

}
