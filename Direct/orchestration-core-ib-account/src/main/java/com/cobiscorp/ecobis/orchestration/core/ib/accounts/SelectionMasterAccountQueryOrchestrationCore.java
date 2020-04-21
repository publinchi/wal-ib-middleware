package com.cobiscorp.ecobis.orchestration.core.ib.accounts;

import java.util.HashMap;
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
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.MasterAccountRequest;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Entity;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.User;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceAccountStatementQuery;

/**
 * @author jmoreta
 *
 */
@Component(name = "SelectionMasterAccountQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "SelectionMasterAccountQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "SelectionMasterAccountQueryOrchestrationCore") })

public class SelectionMasterAccountQueryOrchestrationCore extends SPJavaOrchestrationBase {

	@Reference(referenceInterface = ICoreServiceAccountStatementQuery.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceAccountStatementQuery coreService;
	ILogger logger = LogFactory.getLogger(SelectionMasterAccountQueryOrchestrationCore.class);

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreServiceAccountStatementQuery service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreServiceAccountStatementQuery service) {
		coreService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		String messageError = null;
		String messageLog = null;
		String queryName = null;

		IProcedureResponse aIProcedureResponse = null;
		MasterAccountRequest aMasterAccountRequest = transformSelectionMasterAccountRequest(request.clone());
		if (logger.isDebugEnabled())
			logger.logDebug("OBJ: " + aMasterAccountRequest);

		try {
			if (logger.isDebugEnabled())
				logger.logDebug("executeQuery");
			messageError = "get: ERROR EXECUTING SERVICE";
			if (logger.isDebugEnabled())
				logger.logDebug("request SelectionMasterAccountRequest: " + request.toString());
			messageLog = "getSelectionMasterAccount: " + aMasterAccountRequest.getServiceId();
			queryName = "getSelectionMasterAccount";
			aMasterAccountRequest.setOriginalRequest(request);
			aIProcedureResponse = coreService.getSelectionMasterAccount(aMasterAccountRequest);
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

		aBagSPJavaOrchestration.put(BEGIN_OPERATION, messageLog);
		aBagSPJavaOrchestration.put(BRANCH_NAME, queryName);

		return transformProcedureResponse(aIProcedureResponse);
	}

	/******************
	 * Transformación de ProcedureRequest a SelectionMasterAccount Request
	 ********************/

	private MasterAccountRequest transformSelectionMasterAccountRequest(IProcedureRequest aRequest) {

		MasterAccountRequest masterAccountRequest = new MasterAccountRequest();
		// LaborDay laborDay = new LaborDay();
		Currency currency = new Currency();
		Entity entity = new Entity();
		Product product = new Product();
		User user = new User();

		if (logger.isDebugEnabled())
			logger.logDebug(
					"Procedure Request to Transform-SelectionMasterAccount->" + aRequest.getProcedureRequestAsString());

		String messageError = null;

		messageError = aRequest.readValueParam("@i_moneda") == null ? " - @i_moneda can't be null" : "";
		messageError += aRequest.readValueParam("@i_cliente") == null ? " - @i_cliente can't be null" : "";
		messageError += aRequest.readValueParam("@i_producto") == null ? " - @i_producto can't be null" : "";
		messageError += aRequest.readValueParam("@i_servicio") == null ? " - @i_servicio can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		currency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_moneda")));
		entity.setEnte(Integer.parseInt(aRequest.readValueParam("@i_cliente")));
		product.setProductAlias(aRequest.readValueParam("@i_alias"));
		product.setProductId(Integer.parseInt(aRequest.readValueParam("@i_producto")));
		product.setProductNumber(aRequest.readValueParam("@i_cuenta"));
		user.setName(aRequest.readValueParam("@i_login"));

		masterAccountRequest.setCurrencyId(currency);
		masterAccountRequest.setEntityId(entity);
		masterAccountRequest.setProduct(product);
		masterAccountRequest.setServiceId(Integer.parseInt(aRequest.readValueParam("@i_servicio")));
		masterAccountRequest.setUserName(user);

		return masterAccountRequest;
	}

	/*********************
	 * Transformación de Response a SelectionMasterAccountResponse
	 ***********************/
	private IProcedureResponse transformProcedureResponse(IProcedureResponse aIProcedureResponse) {

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response SelectionMasterAccount");

		// Retorno Código ERROR
		wProcedureResponse.setReturnCode(aIProcedureResponse.getReturnCode());

		// AGREGAR LA DEVOLUCION DE LA COLECCION DE MENSAJES QUE DEVUELVE LA
		// IMPLEMENTACION, PARA MAPEAR EL MENSAJE DE ERROR DEVUELTO
		if (aIProcedureResponse.getReturnCode() != 0) {
			wProcedureResponse.addMessage(aIProcedureResponse.getReturnCode(),
					aIProcedureResponse.getReturnCode() + " ERROR SELECTIONMASTERACCOUNT SERVICIO PERSONALIZABLE");
		}

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final SelectionMasterAccount --> "
					+ wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration SelectionMasterAccount");
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		Utils.validateComponentInstance(mapInterfaces);
		return executeBaseJavaOrchestration(anOrginalRequest, aBagSPJavaOrchestration);
	}

}
