package com.cobiscorp.ecobis.orchestration.core.ib.third.party;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.ecobis.ib.application.dtos.ThirdPartyRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ThirdPartyResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ThirdParty;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceThirdParty;

@Component(name = "ThirdPartyQuery", immediate = false)
@Service(value = { ICoreServiceThirdParty.class })
@Properties(value = {

		@Property(name = "service.description", value = "ThirdPartyQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ThirdPartyQuery") })
public class ThirdPartyQuery extends SPJavaOrchestrationBase implements ICoreServiceThirdParty {

	private static final String COBIS_CONTEXT = "COBIS";
	private static ILogger logger = LogFactory.getLogger(ThirdPartyQuery.class);

	@Override
	public ThirdPartyResponse validateInternalThirdParty(ThirdPartyRequest aThirdPartyRequest)
			throws CTSServiceException, CTSInfrastructureException {

		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: validateInternalThirdParty");
			logger.logInfo("RESPUESTA DUMMY COBIS GENERADA");
		}

		IProcedureRequest request = new ProcedureRequestAS();
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, request.readValueParam("@t_trn"));
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, request.readValueParam("@t_trn"));
		request.setSpName("cobis..sp_bv_valida_destino");
		request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR,
				aThirdPartyRequest.getThirdParty().getClient().getLogin());
		request.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR,
				aThirdPartyRequest.getThirdParty().getProduct().getProductNumber());
		request.addInputParam("@i_mon_des", ICTSTypes.SQLINT2,
				aThirdPartyRequest.getThirdParty().getProduct().getCurrency().getCurrencyId().toString());
		request.addInputParam("@i_prod_des", ICTSTypes.SQLINT1,
				aThirdPartyRequest.getThirdParty().getProduct().getProductType().toString());

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request: " + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Start Request");
		}

		/* Ejecuta y obtiene la respuesta */
		IProcedureResponse wResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response: " + wResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Finalize Response");
		}
		return transformThirdPartyResponse(wResponse);
	}

	private ThirdPartyResponse transformThirdPartyResponse(IProcedureResponse aProcedureResponse) {
		ThirdPartyResponse response = new ThirdPartyResponse();
		if (aProcedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsLoanStatement = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();
			for (IResultSetRow iResultSetRow : rowsLoanStatement) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

				ThirdParty thirdParty = new ThirdParty();
				Product product = new Product();
				Currency currency = new Currency();
				currency.setCurrencyId(Integer.parseInt(columns[2].getValue()));
				currency.setCurrencyDescription(columns[7].getValue());
				product.setCurrency(currency);
				product.setProductNemonic(columns[5].getValue());
				product.setProductType(Integer.parseInt(columns[3].getValue()));
				product.setProductNumber(columns[4].getValue());
				product.setProductDescription(columns[6].getValue());
				thirdParty.setIdBeneficiary(columns[0].getValue());
				thirdParty.setBeneficiary(columns[1].getValue());
				thirdParty.setProduct(product);
				response.setThirdParty(thirdParty);
			}
		} else {
			response.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		}
		response.setReturnCode(aProcedureResponse.getReturnCode());
		return response;
	}

	@Override
	public ThirdPartyResponse validateExternalThirdParty(ThirdPartyRequest aThirdPartyRequest)
			throws CTSServiceException, CTSInfrastructureException {

		ThirdPartyResponse response = new ThirdPartyResponse();
		ThirdParty thirdParty = new ThirdParty();
		Client client = new Client();
		Product product = new Product();
		Currency currency = new Currency();
		currency.setCurrencyId(0);
		currency.setCurrencyDescription("DOLARES");
		currency.setCurrencyNemonic("$");
		product.setCurrency(currency);
		product.setProductNemonic("CTE");
		product.setProductType(3);
		product.setProductNumber(aThirdPartyRequest.getThirdParty().getProduct().getProductNumber());
		product.setProductDescription(
				"Propietario de " + aThirdPartyRequest.getThirdParty().getProduct().getProductNumber());
		client.setLogin(aThirdPartyRequest.getThirdParty().getClient().getLogin());
		thirdParty.setClient(client);
		thirdParty.setIdBeneficiary("0917583775");
		thirdParty
				.setBeneficiary("Propietario de " + aThirdPartyRequest.getThirdParty().getProduct().getProductNumber());
		thirdParty.setProduct(product);
		response.setThirdParty(thirdParty);
		response.setResponseCodeExternalSystem("0");
		return response;
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
