/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.third.party;

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
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ThirdParty;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.application.dtos.ThirdPartyRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ThirdPartyResponse;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceThirdParty;

/**
 * @author cecheverria
 * @since Sep 2, 2014
 * @version 1.0.0
 */
@Component(name = "ThirdPartyQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "ThirdPartyQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ThirdPartyQueryOrchestrationCore") })
public class ThirdPartyQueryOrchestrationCore extends QueryBaseTemplate {// SPJavaOrchestrationBase
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate#
	 * validateLocalExecution(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	protected IProcedureResponse validateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		IProcedureResponse response = initProcedureResponse(anOriginalRequest);
		response.setReturnCode(0);
		return response;
	}

	ILogger logger = this.getLogger();
	private static final int TRN_VALIDATE_INTERNAL = 1801015;
	private static final int TRN_VALIDATE_EXTERNAL = 1875060;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate#
	 * executeQueryService(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceThirdParty.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceThirdParty coreService;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreServiceThirdParty service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreServiceThirdParty service) {
		coreService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	/* 
	 * 
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		try {
			// Valida Inyección de dependencias
			Map<String, Object> mapInterfaces = new HashMap<String, Object>();
			mapInterfaces.put("coreService", coreService);

			Utils.validateComponentInstance(mapInterfaces);
			IProcedureResponse response = executeStepsQueryBase(anOrginalRequest, aBagSPJavaOrchestration);
			return processResponse(anOrginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {

			return Utils.returnExceptionService(anOrginalRequest, e);
		}

	}

	/*
	 * This method execute implementation of Query Service
	 */
	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		String messageError = null;
		String messageLog = null;
		String queryName = null;
		ThirdPartyResponse thirdPartyResponse = null;
		// Transform the response
		ThirdPartyRequest thirdPartyRequest = transformThirdPartyRequest(request.clone());
		try {

			Integer wTrn = Integer.parseInt(request.readValueParam("@t_trn"));
			if (logger.isDebugEnabled())
				logger.logDebug("TRANSACCION " + String.valueOf(wTrn));
			switch (wTrn) {
			case TRN_VALIDATE_INTERNAL:
				messageError = "validateInternalThirdParty: ERROR EXECUTING SERVICE";
				messageLog = "VALIDA TERCERO DEL BANCO"
						+ thirdPartyRequest.getThirdParty().getProduct().getProductNumber();
				queryName = "validateInternalThirdParty";
				if (logger.isDebugEnabled())
					logger.logDebug("ANTES DE LLAMADA A coreService.validateInternalThirdParty");
				thirdPartyResponse = coreService.validateInternalThirdParty(thirdPartyRequest);
				if (logger.isDebugEnabled())
					logger.logDebug("DESPUES DE LLAMADA A coreService.validateInternalThirdParty");

				break;
			case TRN_VALIDATE_EXTERNAL:
				messageLog = "VALIDA TERCERO DE OTRO BANCO"
						+ thirdPartyRequest.getThirdParty().getProduct().getProductNumber();
				messageError = "validateExternalThirdParty: ERROR EXECUTING SERVICE";
				queryName = "validateExternalThirdParty";
				thirdPartyResponse = coreService.validateExternalThirdParty(thirdPartyRequest);
				break;
			default:
				break;
			}
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
		aBagSPJavaOrchestration.put(LOG_MESSAGE, messageLog);
		aBagSPJavaOrchestration.put(QUERY_NAME, queryName);

		return transformProcedureResponse(thirdPartyResponse);

	}

	/*
	 * Proccess Response
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureResponse response = (IProcedureResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION");
		return response;

	}

	/*
	 * Valid Third Party Response
	 */
	private boolean IsValidThirdPartyResponse(ThirdPartyResponse thirdPartyResponse) {
		String messageError = null;

		messageError = thirdPartyResponse.getThirdParty().getIdBeneficiary() == null ? "IdBeneficiary can't be null"
				: "OK";
		messageError = thirdPartyResponse.getThirdParty().getBeneficiary() == null ? "Beneficiary can't be null" : "OK";
		messageError = thirdPartyResponse.getThirdParty().getProduct().getCurrency().getCurrencyId() == null
				? "CurrencyId can't be null" : "OK";
		messageError = thirdPartyResponse.getThirdParty().getProduct().getProductType() == null
				? "ProductType can't be null" : "OK";
		messageError = thirdPartyResponse.getThirdParty().getProduct().getProductNumber() == null
				? "ProductNumber can't be null" : "OK";
		messageError = thirdPartyResponse.getThirdParty().getProduct().getProductNemonic() == null
				? "ProductNemonic can't be null" : "OK";
		messageError = thirdPartyResponse.getThirdParty().getProduct().getProductDescription() == null
				? "ProductDescription can't be null" : "OK";
		messageError = thirdPartyResponse.getThirdParty().getProduct().getCurrency().getCurrencyDescription() == null
				? "CurrencyDescription can't be null" : "OK";

		if (!messageError.equals("OK"))
			throw new IllegalArgumentException(messageError);

		return true;
	}

	/*
	 * Transform a Procedure Request in ThirdPartyRequest
	 */
	private ThirdPartyRequest transformThirdPartyRequest(IProcedureRequest aRequest) {
		ThirdPartyRequest thirdPartyReq = new ThirdPartyRequest();
		ThirdParty thirdParty = new ThirdParty();
		Client client = new Client();
		Product product = new Product();
		Currency currency = new Currency();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		currency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon_des")));

		product.setProductNumber(aRequest.readValueParam("@i_cta_des"));
		product.setProductType(Integer.parseInt(aRequest.readValueParam("@i_prod_des")));
		product.setCurrency(currency);

		client.setLogin(aRequest.readValueParam("@i_login"));

		thirdParty.setProduct(product);
		thirdParty.setClient(client);
		thirdPartyReq.setThirdParty(thirdParty);

		return thirdPartyReq;
	}

	/*
	 * Transform a ThirdPartyResponse in IProcedureResponse
	 */
	private IProcedureResponse transformProcedureResponse(ThirdPartyResponse thirdPartyResponse) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (thirdPartyResponse.getReturnCode() == 0) {
			if (!IsValidThirdPartyResponse(thirdPartyResponse))
				return null;

			if (logger.isInfoEnabled())
				logger.logInfo("TRANSFORM PROCEDURE RESPONSE");
			// Agregar Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("idBeneficiary", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("beneficiary", ICTSTypes.SQLVARCHAR, 100));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("currencyId", ICTSTypes.SQLINT2, 4));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("productType", ICTSTypes.SQLINT2, 4));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("productNumber", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("productNemonic", ICTSTypes.SQLVARCHAR, 3));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("productDescription", ICTSTypes.SQLVARCHAR, 24));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("currencyDescription", ICTSTypes.SQLVARCHAR, 24));

			IResultSetRow row = new ResultSetRow();
			row.addRowData(1, new ResultSetRowColumnData(false, thirdPartyResponse.getThirdParty().getIdBeneficiary())); // idBeneficiary
			row.addRowData(2, new ResultSetRowColumnData(false, thirdPartyResponse.getThirdParty().getBeneficiary())); // beneficiary
			row.addRowData(3, new ResultSetRowColumnData(false,
					thirdPartyResponse.getThirdParty().getProduct().getCurrency().getCurrencyId().toString())); // currencyId
			row.addRowData(4, new ResultSetRowColumnData(false,
					thirdPartyResponse.getThirdParty().getProduct().getProductType().toString())); // productType
			row.addRowData(5, new ResultSetRowColumnData(false,
					thirdPartyResponse.getThirdParty().getProduct().getProductNumber())); // productNumber
			row.addRowData(6, new ResultSetRowColumnData(false,
					thirdPartyResponse.getThirdParty().getProduct().getProductNemonic())); // productNemonic
			row.addRowData(7, new ResultSetRowColumnData(false,
					thirdPartyResponse.getThirdParty().getProduct().getProductDescription())); // productDescription
			row.addRowData(8, new ResultSetRowColumnData(false,
					thirdPartyResponse.getThirdParty().getProduct().getCurrency().getCurrencyDescription())); // currencyDescription

			data.addRow(row);
			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock);

		} else {
			wProcedureResponse = Utils.returnException(thirdPartyResponse.getMessages());
		}

		wProcedureResponse.setReturnCode(thirdPartyResponse.getReturnCode());
		if (logger.isInfoEnabled())
			logger.logInfo("RESPONSE FINAL -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;
	}

}
