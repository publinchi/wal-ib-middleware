/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.cities;

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
import com.cobiscorp.ecobis.ib.application.dtos.CityRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CityResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.City;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Country;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCities;

@Service(value = { ICoreServiceCities.class, ICISSPBaseOrchestration.class, IOrchestrator.class })
@Component(name = "CitiesByCountryQuery", immediate = false)
@Properties(value = { @Property(name = "service.description", value = "CitiesByCountryQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CitiesByCountryQuery") })
public class CitiesByCountryQuery extends SPJavaOrchestrationBase implements ICoreServiceCities {

	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	protected static final String COBIS_CONTEXT = "COBIS";
	private static final String CLASS_NAME = " >-----> ";
	private static ILogger logger = LogFactory.getLogger(CitiesByCountryQuery.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCities#
	 * getCitiesByCountry(com.cobiscorp.ecobis.ib.application.dtos.CityRequest)
	 */
	@Override
	public CityResponse getCitiesByCountry(CityRequest cityRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "INICIANDO SERVICIO");
			logger.logInfo(CLASS_NAME + "RESPUESTA CORE GENERADA");
		}

		CityResponse wCityResponse = transformResponse(executeGetCitiesByCountryCobis(cityRequest));

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "TERMINANDO SERVICIO");
		return wCityResponse;

	}

	private IProcedureResponse executeGetCitiesByCountryCobis(CityRequest cityRequest) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando consulta de ciudades por pais CORE COBIS");

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);

		anOriginalRequest.setSpName("cobis..sp_bv_get_cities");
		if (cityRequest.getCountryCode() != null)
			anOriginalRequest.addInputParam("@i_pais", ICTSTypes.SQLINT4, cityRequest.getCountryCode().toString());
		if (cityRequest.getContinentCode() != null)
			anOriginalRequest.addInputParam("@i_cont", ICTSTypes.SQLVARCHAR, cityRequest.getContinentCode());
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Data enviada a ejecutar:" + anOriginalRequest.toString());

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta del Core:" + response.getProcedureResponseAsString());

		return response;
	}

	private CityResponse transformResponse(IProcedureResponse response) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando Transformacion de Respuesta" + response);

		CityResponse wCityResponse = new CityResponse();
		List<City> cityCollection = new ArrayList<City>();

		if (!response.hasError()) {
			IResultSetBlock resulsetOriginBalance = response.getResultSet(1);
			IResultSetRow[] rowsTemp = resulsetOriginBalance.getData().getRowsAsArray();

			if (rowsTemp.length > 0) {

				for (IResultSetRow iResultSetRow : rowsTemp) {
					IResultSetRowColumnData[] rows = iResultSetRow.getColumnsAsArray();
					City city = new City();
					Country country = new Country();

					if (rows[0].getValue() != null)
						city.setCodeCity(new Integer(rows[0].getValue().toString()));
					if (rows[1].getValue() != null)
						city.setNameCity(rows[1].getValue());

					if (rows[2].getValue() != null)
						country.setCode(new Integer(rows[2].getValue().toString()));
					if (rows[3].getValue() != null)
						country.setName(rows[3].getValue());
					if (rows[4].getValue() != null)
						country.setContinentCode(rows[4].getValue());
					if (rows[5].getValue() != null)
						country.setContinent(rows[5].getValue());

					city.setCountry(country);
					cityCollection.add(city);
				}
			}
			wCityResponse.setCityCollection(cityCollection);
			wCityResponse.setSuccess(response.getReturnCode() == 0);
		}

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta" + wCityResponse);
		return wCityResponse;
	}

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
