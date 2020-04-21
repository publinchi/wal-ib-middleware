package com.cobiscorp.orchestration.core.ib.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
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
import com.cobiscorp.cobisv.commons.context.CobisSession;
import com.cobiscorp.cobisv.commons.context.Context;
import com.cobiscorp.cobisv.commons.context.ContextManager;
import com.cobiscorp.ecobis.ib.application.dtos.CurrencyRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CurrencyResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceGetCurrencies;

@Service(value = { ICoreServiceGetCurrencies.class, ICISSPBaseOrchestration.class, IOrchestrator.class })
@Component(name = "GetCurrenciesQuery", immediate = false)
@Properties(value = { @Property(name = "service.description", value = "GetCurrenciesQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "GetCurrenciesQuery") })
public class GetCurrenciesQuery extends SPJavaOrchestrationBase implements ICoreServiceGetCurrencies {
	ILogger logger = LogFactory.getLogger(GetCurrenciesQuery.class);
	private static final String CLASS_NAME = " >-----> ";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.commons.components.ICOBISComponent#loadConfiguration(
	 * com.cobiscorp.cobis.commons.configuration.IConfigurationReader)
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceGetCurrencies#GetCurrencies(com.cobiscorp.ecobis.ib.
	 * application.dtos.CurrencyRequest)
	 */
	@Override
	public CurrencyResponse GetCurrencies(CurrencyRequest aCurrencyRequest)
			throws CTSServiceException, CTSInfrastructureException {

		Context context = ContextManager.getContext();
		CobisSession session = (CobisSession) context.getSession();

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1555");
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		anOriginalRequest.setSpName("cobis..sp_moneda");
		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1555");
		anOriginalRequest.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, session.getServer());
		anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, session.getOffice());
		anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, session.getUser());
		anOriginalRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, session.getTerminal());
		anOriginalRequest.addInputParam("@s_rol", ICTSTypes.SQLINT4, session.getRole());

		anOriginalRequest.addInputParam("@i_modo", ICTSTypes.SQLINT4, aCurrencyRequest.getMode().toString());
		anOriginalRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "S");

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Obtencion del servicio");

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Enviando solicitud al Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request a enviar: " + anOriginalRequest.getProcedureRequestAsString());

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Data devuelta del Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Response obtenido: " + response.getProcedureResponseAsString());
		return transformToCurrencyResponse(response);
	}

	/**
	 * @param response
	 * @return
	 */
	private CurrencyResponse transformToCurrencyResponse(IProcedureResponse aProcedureResponse) {		
		Currency aCurrency = null;
		List<Currency> aCurrencyList = new ArrayList<Currency>();
		CurrencyResponse aCurrencyResponse = new CurrencyResponse();

		IResultSetRow[] rowsCurrency = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

		for (IResultSetRow iResultSetRow : rowsCurrency) {
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
			aCurrency = new Currency();
			if (columns[0].getValue() != null) {
				aCurrency.setCurrencyId(Integer.parseInt(columns[0].getValue()));
			}
			if (columns[1].getValue() != null) {
				aCurrency.setCurrencyDescription(columns[1].getValue());
			}
			if (columns[2].getValue() != null) {
				aCurrency.setCurrencySymbol(columns[2].getValue());
			}
			if (columns[3].getValue() != null) {
				aCurrency.setCurrencyNemonic(columns[3].getValue());
			}
			if (columns[4].getValue() != null) {
				aCurrency.setCountryCode(Integer.parseInt(columns[4].getValue()));
			}
			if (columns[5].getValue() != null) {
				aCurrency.setCountry(columns[5].getValue());
			}
			if (columns[6].getValue() != null) {
				aCurrency.setState(columns[6].getValue());
			}
			if (columns[7].getValue() != null) {
				aCurrency.setHasDecimal(columns[7].getValue());
			}
			aCurrencyList.add(aCurrency);
		}

		aCurrencyResponse.setCurrencyCollection(aCurrencyList);
		return aCurrencyResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase#
	 * executeJavaOrchestration(com.cobiscorp.cobis.cts.domains.
	 * IProcedureRequest, java.util.Map)
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase#
	 * processResponse(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}