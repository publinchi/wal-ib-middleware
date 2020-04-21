package com.cobiscorp.ecobis.orchestration.core.ib.foreignExchange;

import java.math.BigDecimal;
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
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.TransferRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TransferResponse;
import com.cobiscorp.ecobis.orchestration.core.ib.opening.template.OpeningOnlineTemplate;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.SearchOption;
import com.cobiscorp.ecobis.ib.orchestration.dtos.User;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceForeignExchange;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceNotification;

/**
 * Purchase Sale Foreign Exchange
 *
 * @author jchonillo
 * @since Jan 17, 2015
 * @version 1.0.0
 */
@Component(name = "PurchaseSaleForeignExchangeOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "PurchaseSaleForeignExchangeOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.0"),
		@Property(name = "service.identifier", value = "PurchaseSaleForeignExchangeOrchestrationCore") })
public class PurchaseSaleForeignExchangeOrchestrationCore extends OpeningOnlineTemplate {

	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	static final String OPERATION1_RESPONSE = "OPERATION1_RESPONSE";
	static final String OPERATION1_REQUEST = "OPERATION1_REQUEST";
	static final String OPERATION2_RESPONSE = "OPERATION2_RESPONSE";
	static final String OPERATION3_RESPONSE = "OPERATION3_RESPONSE";
	static final String COBIS_CONTEXT = "COBIS";

	/**
	 * Instance logger component
	 */
	private static ILogger logger = LogFactory.getLogger(PurchaseSaleForeignExchangeOrchestrationCore.class);

	@Reference(referenceInterface = ICoreServiceForeignExchange.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	private ICoreServiceForeignExchange coreServiceForeignExchange;

	@Reference(referenceInterface = ICoreServiceNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceNotification", unbind = "unbindCoreServiceNotification")
	protected ICoreServiceNotification coreServiceNotification;

	@Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreService coreService;

	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	@Reference(referenceInterface = ICoreServiceMonetaryTransaction.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceMonetaryTransaction", unbind = "unbindCoreServiceMonetaryTransaction")
	protected ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreService(ICoreServiceForeignExchange service) {
		coreServiceForeignExchange = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreService(ICoreServiceForeignExchange service) {
		coreServiceForeignExchange = null;
	}

	/**
	 * Instance ServiceNotification Interface
	 *
	 * @param service
	 */
	public void bindCoreServiceNotification(ICoreServiceNotification service) {
		coreServiceNotification = service;
	}

	/**
	 * Deleting ServiceNotification Interface
	 *
	 * @param service
	 */
	public void unbindCoreServiceNotification(ICoreServiceNotification service) {
		coreServiceNotification = null;
	}

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreService service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */

	public void unbindCoreService(ICoreService service) {
		coreService = null;
	}

	public void bindCoreServer(ICoreServer service) {
		coreServer = service;
	}

	public void unbindCoreServer(ICoreServer service) {
		coreServer = null;
	}

	/**
	 * Instance ServiceMonetaryTransaction Interface
	 *
	 * @param service
	 */
	public void bindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = service;
	}

	/**
	 * Deleting ServiceMonetaryTransaction Interface
	 *
	 * @param service
	 */
	public void unbindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected ICoreService getCoreService() {
		// TODO Auto-generated method stub
		return coreService;
	}

	@Override
	protected ICoreServer getCoreServer() {
		// TODO Auto-generated method stub
		return coreServer;
	}

	@Override
	public ICoreServiceNotification getCoreServiceNotification() {
		// TODO Auto-generated method stub
		return coreServiceNotification;
	}

	@Override
	protected ICoreServiceMonetaryTransaction getCoreServiceMonetaryTransaction() {
		// TODO Auto-generated method stub
		return coreServiceMonetaryTransaction;
	}

	@Override
	protected IProcedureResponse validateCentralExecution(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected IProcedureResponse executeOpening(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration)
			throws CTSServiceException, CTSInfrastructureException {
		// TODO Auto-generated method stub

		String messageError = null;

		TransferResponse transferResponse = null;
		TransferRequest transferRequest = transformTransferRequest(request.clone());

		try {
			if (logger.isDebugEnabled())
				logger.logDebug("executeApplication");
			messageError = "get: ERROR EXECUTING SERVICE";
			transferRequest.setOriginalRequest(request);
			transferResponse = coreServiceForeignExchange.foreignExchange(transferRequest);
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

		return transformProcedureResponse(transferResponse);
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration");
		IProcedureResponse response = null;
		try {
			Map<String, Object> mapInterfaces = new HashMap<String, Object>();
			mapInterfaces.put("coreServiceForeignExchange", coreServiceForeignExchange);
			mapInterfaces.put("coreServer", coreServer);
			mapInterfaces.put("coreService", coreService);
			mapInterfaces.put("coreServiceNotification", coreServiceNotification);
			mapInterfaces.put("coreServiceMonetaryTransaction", coreServiceMonetaryTransaction);
			Utils.validateComponentInstance(mapInterfaces);
			if (logger.isDebugEnabled())
				logger.logDebug("INICIO anOrginalRequest" + anOriginalRequest);
			response = executeStepsOpeningBase(anOriginalRequest, aBagSPJavaOrchestration);
			if (logger.isDebugEnabled())
				logger.logDebug("executeJavaOrchestration");
			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOriginalRequest, e);
		} catch (CTSInfrastructureException e) {

			return Utils.returnExceptionService(anOriginalRequest, e);
		}

	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		IProcedureResponse response = (IProcedureResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION");
		return response;
	}

	@Override
	public IProcedureResponse sendOpeningMail(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureResponse pr = new ProcedureResponseAS();
		pr.setReturnCode(0);
		return pr;
	}

	private IProcedureResponse transformProcedureResponse(TransferResponse aResponse) {
		if (logger.isInfoEnabled())
			logger.logInfo("transformProcedureResponse TransferResponse " + aResponse);

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (aResponse.getReturnCode() == 0) {
			wProcedureResponse.addParam("@o_referencia", ICTSTypes.SQLINT4, 1, aResponse.getReferenceNumber());
			wProcedureResponse.addParam("@o_monto_operacion", ICTSTypes.SQLMONEY, 1, aResponse.getAmount().toString());
			wProcedureResponse.addParam("@o_tasa", ICTSTypes.SQLFLT8i, 1, aResponse.getCommission().toString());
		} else
			wProcedureResponse = Utils.returnException(aResponse.getMessages());

		return wProcedureResponse;
	}

	private TransferRequest transformTransferRequest(IProcedureRequest request) {

		Currency currencyDeb = new Currency();
		currencyDeb.setCurrencyId(Integer.parseInt(request.readValueParam("@i_mon_deb")));

		Product productDeb = new Product();
		productDeb.setProductId(Integer.parseInt(request.readValueParam("@i_prod_deb")));
		productDeb.setProductNumber(request.readValueParam("@i_cta_deb"));
		productDeb.setCurrency(currencyDeb);
		productDeb.setProductAlias(request.readValueParam("@i_alias"));

		Currency currencyCre = new Currency();
		currencyCre.setCurrencyId(Integer.parseInt(request.readValueParam("@i_mon_cre")));

		Product productCre = new Product();
		productCre.setProductId(Integer.parseInt(request.readValueParam("@i_prod_cre")));
		productCre.setProductNumber(request.readValueParam("@i_cta_cre"));
		productCre.setCurrency(currencyCre);

		SearchOption searchOption = new SearchOption();
		searchOption.setCriteria(request.readValueParam("@i_tipo_op"));
		searchOption.setExchangeRate(Double.parseDouble(request.readValueParam("@i_tasa_cambio")));
		searchOption.setNotes(request.readValueParam("@i_notas"));

		User transferUser = new User();
		transferUser.setEntityId(Integer.parseInt(request.readValueParam("@i_cliente")));
		transferUser.setName(request.readValueParam("@i_login"));
		transferUser.setServiceId(Integer.parseInt(request.readValueParam("@i_servicio")));

		TransferResponse transferResponse = new TransferResponse();
		transferResponse.setReference(Integer.parseInt(request.readValueParam("@o_referencia")));
		transferResponse.setAmount(Double.parseDouble(request.readValueParam("@o_monto_operacion")));
		transferResponse.setCommission(Double.parseDouble(request.readValueParam("@o_tasa")));

		TransferRequest transferRequest = new TransferRequest();
		if (logger.isDebugEnabled())
			logger.logDebug(request.readValueParam("@i_sec_preautori"));
		if (!request.readValueParam("@i_sec_preautori").equals("0"))
			transferRequest.setReference(Integer.parseInt(request.readValueParam("@i_sec_preautori")));

		BigDecimal bdAmount = new BigDecimal(request.readValueParam("@i_monto"));
		transferRequest.setAmmount(bdAmount);
		transferRequest.setOperation(request.readValueParam("@i_operacion"));

		transferRequest.setOriginProduct(productDeb);
		transferRequest.setDestinationProduct(productCre);
		transferRequest.setSearchOption(searchOption);
		transferRequest.setUserTransferRequest(transferUser);
		transferRequest.setTransferResponse(transferResponse);
		if (logger.isInfoEnabled())
			logger.logInfo("transformTransferRequest " + transferRequest);

		return transferRequest;
	}

}
