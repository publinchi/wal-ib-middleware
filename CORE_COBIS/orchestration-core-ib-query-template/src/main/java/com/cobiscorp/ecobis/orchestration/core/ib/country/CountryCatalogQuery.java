package com.cobiscorp.ecobis.orchestration.core.ib.country;

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
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.ecobis.ib.application.dtos.CountryRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CountryResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Country;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCountryCatalog;

@Service(value = { ICoreServiceCountryCatalog.class, ICISSPBaseOrchestration.class, IOrchestrator.class })
@Component(name = "CountryCatalogQuery", immediate = false)
@Properties(value = { @Property(name = "service.description", value = "CountryCatalogQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CountryCatalogQuery") })
public class CountryCatalogQuery extends SPJavaOrchestrationBase implements ICoreServiceCountryCatalog {

	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	protected static final String COBIS_CONTEXT = "COBIS";
	private static final String CLASS_NAME = " >-----> ";
	private static ILogger logger = LogFactory.getLogger(CountryCatalogQuery.class);

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
	 * ICoreServiceCountryCatalog#searchCountryCatalog(com.cobiscorp.ecobis.ib.
	 * application.dtos.CountryRequest)
	 */
	@Override
	public CountryResponse searchCountryCatalog(CountryRequest countryRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "INICIANDO SERVICIO");
			logger.logInfo(CLASS_NAME + "RESPUESTA CORE GENERADA");
		}

		CountryResponse wCountryResponse = transformResponse(executeGetCountryCatalogCobis(countryRequest));

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "TERMINANDO SERVICIO");
		return wCountryResponse;
	}

	private IProcedureResponse executeGetCountryCatalogCobis(CountryRequest countryRequest) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando consulta de paises en CORE COBIS");

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.setSpName("cobis..sp_pais_bv");
		if (countryRequest.getMode() != null)
			anOriginalRequest.addInputParam("@i_modo", ICTSTypes.SQLINT4, countryRequest.getMode().toString());
		if (countryRequest.getDescripcion() != null)
			anOriginalRequest.addInputParam("@i_descripcion", ICTSTypes.SQLVARCHAR, countryRequest.getDescripcion());
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Data enviada a ejecutar:" + anOriginalRequest.toString());

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta del Core:" + response.getProcedureResponseAsString());

		return response;
	}

	private CountryResponse transformResponse(IProcedureResponse response) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando Transformacion de Respuesta" + response);

		CountryResponse wCountryResponse = new CountryResponse();
		List<Country> countryCollection = new ArrayList<Country>();

		if (!response.hasError()) {
			IResultSetBlock resulsetOriginBalance = response.getResultSet(1);
			IResultSetRow[] rowsTemp = resulsetOriginBalance.getData().getRowsAsArray();

			if (rowsTemp.length > 0) {

				for (IResultSetRow iResultSetRow : rowsTemp) {
					IResultSetRowColumnData[] rows = iResultSetRow.getColumnsAsArray();
					Country country = new Country();
					if (rows[0].getValue() != null)
						country.setCode(new Integer(rows[0].getValue().toString()));
					if (rows[1].getValue() != null)
						country.setName(rows[1].getValue());
					if (rows[2].getValue() != null)
						country.setNationality(rows[2].getValue());
					if (rows[3].getValue() != null)
						country.setContinentCode(rows[3].getValue());
					if (rows[4].getValue() != null)
						country.setContinent(rows[4].getValue());
					countryCollection.add(country);
				}
			}
			wCountryResponse.setCountryCollection(countryCollection);
			wCountryResponse.setSuccess(response.getReturnCode() == 0);
		}

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta" + wCountryResponse);
		return wCountryResponse;
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
