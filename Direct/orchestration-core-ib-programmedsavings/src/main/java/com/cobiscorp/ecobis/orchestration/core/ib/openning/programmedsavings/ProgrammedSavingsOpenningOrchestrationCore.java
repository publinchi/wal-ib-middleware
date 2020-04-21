package com.cobiscorp.ecobis.orchestration.core.ib.openning.programmedsavings;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsAddProgrammedSavingsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsAddProgrammedSavingsResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.openings.OpeningsBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ProgrammedSavings;
import com.cobiscorp.ecobis.ib.orchestration.dtos.User;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceNotification;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceProgrammedSavingsOpenning;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;

/**
 * @author jbaque
 * @since Nov 14, 2014
 * @version 1.0.0
 */

@Component(name = "ProgrammedSavingsOpenningOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "ProgrammedSavingsOpenningOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ProgrammedSavingsOpenningOrchestrationCore") })
public class ProgrammedSavingsOpenningOrchestrationCore extends OpeningsBaseTemplate {

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceProgrammedSavingsOpenning.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceProgrammedSavingsOpenning coreService;

	@Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindICoreService", unbind = "unbindICoreService")
	protected ICoreService wIcoreService;

	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindICoreServer", unbind = "unbindICoreServer")
	protected ICoreServer wIcoreServer;

	@Reference(referenceInterface = ICoreServiceNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindICoreServiceNotification", unbind = "unbindICoreServiceNotification")
	protected ICoreServiceNotification wICoreServiceNotification;

	ILogger logger = this.getLogger();

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindICoreServer(ICoreServer service) {
		wIcoreServer = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindICoreServer(ICoreServer service) {
		wIcoreServer = null;
	}

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindICoreServiceNotification(ICoreServiceNotification service) {
		wICoreServiceNotification = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindICoreServiceNotification(ICoreServiceNotification service) {
		wICoreServiceNotification = null;
	}

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindICoreService(ICoreService service) {
		wIcoreService = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindICoreService(ICoreService service) {
		wIcoreService = null;
	}

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreServiceProgrammedSavingsOpenning service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreServiceProgrammedSavingsOpenning service) {
		coreService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected IProcedureResponse executeOpening(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "------>JBA-inicia executeOpening de ProgrammedSavings:");
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		String messageError = null;
		String messageLog = null;
		String lsOperacion = null;

		lsOperacion = request.readValueParam("@i_operacion");
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "------>JBA-@i_operacion: " + lsOperacion);

		/** Objetos para AddProgrammedSavings */
		ProgrammedSavingsAddProgrammedSavingsRequest aProgrammedSavingsAddProgrammedSavingsRequest = null;
		ProgrammedSavingsAddProgrammedSavingsResponse aProgrammedSavingsAddProgrammedSavingsResponse = null;

		if (lsOperacion.equals("I")) {
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + "------>JBA-iniciando transformrequest 4:");
			aProgrammedSavingsAddProgrammedSavingsRequest = transformAddProgrammedSavingsRequest(request.clone());
		}

		try {
			messageError = "getProgrammedSavings: ERROR EXECUTING SERVICE";
			messageLog = "getProgrammedSavings ";

			aBagSPJavaOrchestration.put(LOG_MESSAGE, messageLog);
			if (lsOperacion.equals("I")) {
				if (logger.isInfoEnabled())
					logger.logInfo(CLASS_NAME + "------>JBA-ejecuta operacion I");
				aProgrammedSavingsAddProgrammedSavingsRequest.setOriginalRequest(request);
				aProgrammedSavingsAddProgrammedSavingsResponse = coreService
						.addProgrammedSavings(aProgrammedSavingsAddProgrammedSavingsRequest);
				IProcedureResponse returno = transformProcedureAddProgrammedSavingsResponse(
						aProgrammedSavingsAddProgrammedSavingsResponse, aBagSPJavaOrchestration);
				if (logger.isInfoEnabled()) {
					logger.logInfo(CLASS_NAME + "------>***********************JBA-listo para return");
					logger.logInfo(returno);
				}
				return returno;
			}
		} catch (CTSServiceException e) {
			// TODO: handle exception
			e.printStackTrace();
			if (logger.isDebugEnabled())
				logger.logDebug(messageError);
			return null;
		} catch (CTSInfrastructureException e) {
			// TODO: handle exception
			e.printStackTrace();
			if (logger.isDebugEnabled())
				logger.logDebug(messageError);
			return null;
		}
		return wProcedureResponse;

	}

	/**
	 * executeJavaOrchestration
	 **********************************************************************************************************/
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		mapInterfaces.put("wICoreServiceNotification", wICoreServiceNotification);

		try {
			Utils.validateComponentInstance(mapInterfaces);
			aBagSPJavaOrchestration.put(LOG_MESSAGE, "");
			executeStepsOpeningBase(anOrginalRequest, aBagSPJavaOrchestration);
			return processResponse(anOrginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.base.openings.OpeningsBaseTemplate#
	 * sendOpeningMail(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	protected IProcedureResponse sendOpeningMail(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		// TODO Auto-generated method stub
		IProcedureResponse responseTransaction = (IProcedureResponse) bag.get(RESPONSE_TRANSACTION);
		IProcedureRequest sendMailReq = initProcedureRequest(anOriginalRequest);
		sendMailReq.addInputParam("@i_ente", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_cliente"));
		sendMailReq.addOutputParam("@o_body", ICTSTypes.SQLVARCHAR, responseTransaction.readValueParam("@o_body"));
		sendMailReq.addInputParam("@i_mail", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_mail"));
		return super.sendOpeningMail(sendMailReq, bag);
	}

	@Override
	protected IProcedureResponse updateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		// TODO Auto-generated method stub
		// return super.updateLocalExecution(anOriginalRequest, bag);
		IProcedureResponse responseTransaction = new ProcedureResponseAS();
		responseTransaction.setReturnCode(0);
		return responseTransaction;
	}

	/**
	 * processResponse
	 **********************************************************************************************************/
	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "------>JBA-Antes de RESPONSE_TRANSACTION");
		IProcedureResponse response = (IProcedureResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION");

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "------>JBA-despues de RESPONSE_TRANSACTION");
			logger.logInfo(response.getProcedureResponseAsString());
		}
		return response;
	}

	/**
	 * transformAddProgrammedSavingsRequest
	 **********************************************************************************************************/
	private ProgrammedSavingsAddProgrammedSavingsRequest transformAddProgrammedSavingsRequest(
			IProcedureRequest aRequest) {
		ProgrammedSavingsAddProgrammedSavingsRequest aProgrammedSavingsAddProgrammedSavingsRequest = new ProgrammedSavingsAddProgrammedSavingsRequest();
		User user = new User();
		ProgrammedSavings programmedSavings = new ProgrammedSavings();
		Product product = new Product();
		Product product2 = new Product();
		Currency currency = new Currency();
		Currency currency2 = new Currency();
		user.setEntityId(new Integer(aRequest.readValueParam("@i_cliente")));
		user.setName(aRequest.readValueParam("@i_login"));
		programmedSavings.setFrequency(aRequest.readValueParam("@i_frecuencia"));
		programmedSavings.setAmount(new Double(aRequest.readValueParam("@i_monto")));
		currency.setCurrencyId(new Integer(aRequest.readValueParam("@i_moneda")));
		programmedSavings.setCurrency(currency);
		programmedSavings.setConcept(aRequest.readValueParam("@i_concepto"));
		programmedSavings.setInitialDate(aRequest.readValueParam("@i_fecha_ini"));
		programmedSavings.setTerm(aRequest.readValueParam("@i_plazo"));
		programmedSavings.setExpirationDate(aRequest.readValueParam("@i_fecha_ven"));
		programmedSavings.setMail(aRequest.readValueParam("@i_mail"));
		programmedSavings.setBranch(new Integer(aRequest.readValueParam("@i_sucursal")));
		programmedSavings.setIdBeneficiary(aRequest.readValueParam("@i_id_beneficiary"));
		product2.setProductNumber(aRequest.readValueParam("@i_cta_deb"));
		product2.setProductId(new Integer(aRequest.readValueParam("@i_prod_deb")));
		currency2.setCurrencyId(new Integer(aRequest.readValueParam("@i_mon_deb")));
		product2.setCurrency(currency2);
		product.setProductNumber(aRequest.readValueParam("@i_cta_ahoprog"));
		aProgrammedSavingsAddProgrammedSavingsRequest
				.setCodeTransactionalIdentifier(aRequest.readValueParam("@i_operacion"));
		aProgrammedSavingsAddProgrammedSavingsRequest.setUser(user);
		aProgrammedSavingsAddProgrammedSavingsRequest.setProduct1(product);
		aProgrammedSavingsAddProgrammedSavingsRequest.setProduct2(product2);
		aProgrammedSavingsAddProgrammedSavingsRequest.setProgrammedSavings(programmedSavings);
		return aProgrammedSavingsAddProgrammedSavingsRequest;
	}

	/**
	 * transformProcedureMinimumAmountResponse
	 * 
	 * @param aBagSPJavaOrchestration
	 **********************************************************************************************************/
	private IProcedureResponse transformProcedureAddProgrammedSavingsResponse(
			ProgrammedSavingsAddProgrammedSavingsResponse aProgrammedSavingsAddProgrammedSavingsResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (aProgrammedSavingsAddProgrammedSavingsResponse.getReturnCode() == 0) {
			if (logger.isInfoEnabled())
				logger.logDebug("Iniciando transformProcedureAddProgrammedSavingsResponse");
			wProcedureResponse.addParam("@o_cta_ahoprog", ICTSTypes.SQLVARCHAR, 0,
					aProgrammedSavingsAddProgrammedSavingsResponse.getTransferResponse().getProductNumber().toString());
			wProcedureResponse.addParam("@o_body", ICTSTypes.SQLVARCHAR, 1,
					aProgrammedSavingsAddProgrammedSavingsResponse.getTransferResponse().getBody().toString());
			if (logger.isInfoEnabled())
				logger.logDebug(
						"Procedure Account Response Final -->" + wProcedureResponse.getProcedureResponseAsString());
		} else {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aProgrammedSavingsAddProgrammedSavingsResponse.getMessages()));
			wProcedureResponse = Utils.returnException(aProgrammedSavingsAddProgrammedSavingsResponse.getMessages());
		}
		wProcedureResponse.setReturnCode(aProgrammedSavingsAddProgrammedSavingsResponse.getReturnCode());
		return wProcedureResponse;

	}

	@Override
	protected ICoreService getCoreService() {
		// TODO Auto-generated method stub
		return wIcoreService;
	}

	@Override
	protected ICoreServiceMonetaryTransaction getCoreServiceMonetaryTransaction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ICoreServiceNotification getCoreServiceNotification() {
		// TODO Auto-generated method stub
		return wICoreServiceNotification;
	}

	@Override
	protected ICoreServer getCoreServer() {
		// TODO Auto-generated method stub
		return wIcoreServer;
	}

	@Override
	protected IProcedureResponse validateCentralExecution(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IProcedureResponse executeTransaction(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		// TODO Auto-generated method stub
		return executeOpening(request, aBagSPJavaOrchestration);
	}

	@Override
	public IProcedureResponse validateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		ProcedureResponseAS response = new ProcedureResponseAS();
		response.setReturnCode(0);
		return response;
	}

}
