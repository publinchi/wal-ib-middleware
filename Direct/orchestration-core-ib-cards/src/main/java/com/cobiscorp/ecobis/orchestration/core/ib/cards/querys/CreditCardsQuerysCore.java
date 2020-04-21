package com.cobiscorp.ecobis.orchestration.core.ib.cards.querys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.IMessageBlock;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.MessageBlock;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.ecobis.ib.application.dtos.SummaryCreditCardRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SummaryCreditCardResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCardsQuery;

/**
 * This class implement methods for get information credit cards
 * 
 * @author schancay
 * @since Jun 19, 2014
 * @version 1.0.0
 */
@Component(name = "CreditCardsQuerysCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "CreditCardsQuerysCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CreditCardsQuerysCore") })
public class CreditCardsQuerysCore extends SPJavaOrchestrationBase {
	ILogger logger = this.getLogger();
	private static final String CLASS_NAME = " >-----> ";
	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceCardsQuery.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	private ICoreServiceCardsQuery coreServiceCardsQuery;

	/**
	 * Instance Service Interface
	 * 
	 * @param service
	 */
	protected void bindCoreService(ICoreServiceCardsQuery service) {
		coreServiceCardsQuery = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	protected void unbindCoreService(ICoreServiceCardsQuery service) {
		coreServiceCardsQuery = null;
	}

	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";

	static final String OPERATION1_REQUEST = "OPERATION1_REQUEST";
	static final String OPERATION1_RESPONSE = "OPERATION1_RESPONSE";

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		if (logger.isInfoEnabled())
			logger.logInfo(
					"Component-Name: " + this.getClass().getSimpleName() + ", Component-Version: $$project.version$$");
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeJavaOrchestration");
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		Map<String, Object> wprocedureResponse1 = procedureResponse1(anOriginalRequest, aBagSPJavaOrchestration);
		Boolean wSuccessExecutionOperation1 = (Boolean) wprocedureResponse1.get("SuccessExecutionOperation");
		IProcedureResponse wIProcedureResponse1 = (IProcedureResponse) wprocedureResponse1.get("IProcedureResponse");
		IProcedureResponse wErrorProcedureResponse = (IProcedureResponse) wprocedureResponse1
				.get("ErrorProcedureResponse");

		if (wErrorProcedureResponse != null) {
			return wErrorProcedureResponse;
		}
		if (!wSuccessExecutionOperation1) {
			return wIProcedureResponse1;
		}
		wIProcedureResponse1 = (IProcedureResponse) aBagSPJavaOrchestration.get(OPERATION1_RESPONSE);
		return wIProcedureResponse1;

	}

	protected Map<String, Object> procedureResponse1(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "procedureResponse1");
		HashMap<String, Object> ret = new HashMap<String, Object>();
		ret.put("IProcedureResponse", null);
		ret.put("SuccessExecutionOperation", null);
		ret.put("ErrorProcedureResponse", null);

		IProcedureResponse wErrorProcedureResponse = validateParameters(aBagSPJavaOrchestration,
				new String[] { "@i_operacion", "@i_cliente" });

		if (wErrorProcedureResponse != null) {
			ret.put("ErrorProcedureResponse", wErrorProcedureResponse);
			return ret;
		}

		boolean wSuccessExecutionOperation1 = executeOperation1(aBagSPJavaOrchestration);
		ret.put("SuccessExecutionOperation", wSuccessExecutionOperation1);

		if (logger.isDebugEnabled())
			logger.logDebug("result exeuction operation 1: " + wSuccessExecutionOperation1);

		IProcedureResponse wProcedureResponseOperation1 = initProcedureResponse(anOriginalRequest);
		wProcedureResponseOperation1.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE,
				ICSP.ERROR_EXECUTION_SERVICE);
		ret.put("IProcedureResponse", wProcedureResponseOperation1);

		return ret;
	}

	protected boolean executeOperation1(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeOperation1");

		SummaryCreditCardRequest wProcedureRequest = transformOperation(aBagSPJavaOrchestration);
		SummaryCreditCardResponse wProcedureResponse;
		try {
			wProcedureResponse = coreServiceCardsQuery.getSummaryCreditCard(wProcedureRequest);
			aBagSPJavaOrchestration.put(OPERATION1_RESPONSE, wProcedureResponse);

			return wProcedureResponse.getSuccess();
		} catch (CTSServiceException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSServiceException:" + e.getMessage());
			e.printStackTrace();
			aBagSPJavaOrchestration.put(OPERATION1_RESPONSE, null);
			return false;
		} catch (CTSInfrastructureException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSInfrastructureException:" + e.getMessage());
			aBagSPJavaOrchestration.put(OPERATION1_RESPONSE, null);
			return false;
		}
	}

	protected IProcedureRequest transformOperation1(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "transformOperation1");
		ProcedureRequestAS wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		ProcedureRequestAS wProcedureRequest = (ProcedureRequestAS) initProcedureRequest(wOriginalRequest);
		return wProcedureRequest;
	}

	protected SummaryCreditCardRequest transformOperation(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "transformOperation");
		ProcedureRequestAS wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		SummaryCreditCardRequest summaryCreditCardRequest = new SummaryCreditCardRequest();
		Client client = new Client();
		client.setId(wOriginalRequest.readValueParam("@i_cliente"));
		summaryCreditCardRequest.setClient(client);
		return summaryCreditCardRequest;
	}

	public IProcedureResponse validateParameters(Map<String, Object> aBagSPJavaOrchestration, String[] aParams) {

		IProcedureRequest wOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		if (logger.isDebugEnabled()) {
			logger.logDebug("validate parameters sp: " + wOriginalRequest.getSpName());
		}

		List<MessageBlock> wErrorMessages = new ArrayList<MessageBlock>(aParams.length);
		boolean wError = false;
		for (int i = 0; i < aParams.length; i++) {
			if (logger.isDebugEnabled()) {
				logger.logDebug("validate parameter: " + aParams[i]);
			}

			if (wOriginalRequest.readParam(aParams[i]) == null) {
				wError = true;
				MessageBlock wMessageBlock = new MessageBlock();
				// 201 number of error in sybase when a parameter is expected
				wMessageBlock.setMessageNumber(201);
				wMessageBlock.setMessageText("Procedure " + wOriginalRequest.getSpName() + " expects parameter"
						+ aParams[i] + " , which was not supplied.");
				wErrorMessages.add(wMessageBlock);
			}

		}

		if (wError == false)
			return null;

		IProcedureResponse wProcedureResponse = processResponse(wOriginalRequest, aBagSPJavaOrchestration);
		wProcedureResponse.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
		Iterator<MessageBlock> wIterator = wErrorMessages.iterator();

		while (wIterator.hasNext()) {
			MessageBlock wMessageBlockTemp = wIterator.next();
			wProcedureResponse.addMessage(wMessageBlockTemp.getMessageNumber(), wMessageBlockTemp.getMessageText());
		}
		wProcedureResponse.setReturnCode(201);

		return wProcedureResponse;
	}

	/**
	 * Copy messages from sourceResponse to targetResponse
	 * 
	 * @param aSourceResponse
	 * @param aTargetResponse
	 */
	public static void addMessagesFromResponse(IProcedureResponse aSourceResponse, IProcedureResponse aTargetResponse) {
		@SuppressWarnings("rawtypes")
		Iterator wIterator = aSourceResponse.getMessages().iterator();
		while (wIterator.hasNext()) {
			Object wOMessage = wIterator.next();
			if (wOMessage instanceof IMessageBlock) {
				IMessageBlock wBlock = (IMessageBlock) wOMessage;
				aTargetResponse.addMessage(wBlock.getMessageNumber(), wBlock.getMessageText());
			}
		}
		aTargetResponse.setReturnCode(aSourceResponse.getReturnCode());
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalProcedureReq);
		return wProcedureRespFinal;
	}

}
