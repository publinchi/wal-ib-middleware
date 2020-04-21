package com.cobiscorp.ecobis.orchestration.core.country;

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
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.CityRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CityResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.City;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCities;

/**
 * get catalog of cities by country in core Cobis
 * 
 * @author dguerra
 * @since Sep 18, 2014
 * @version 1.0.0
 */
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Component(name = "CitiesByCountryQueryCore", immediate = false)
@Properties(value = { @Property(name = "service.description", value = "CitiesByCountryQueryCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CitiesByCountryQueryCore") })
public class CitiesByCountryQueryCore extends SPJavaOrchestrationBase {

	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	private static final String CLASS_NAME = " >-----> ";
	private static ILogger logger = LogFactory.getLogger(CitiesByCountryQueryCore.class);

	@Reference(referenceInterface = ICoreServiceCities.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceCities", unbind = "unbindCoreServiceCities")
	private ICoreServiceCities coreServiceCities;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreServiceCities(ICoreServiceCities service) {
		coreServiceCities = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreServiceCities(ICoreServiceCities service) {
		coreServiceCities = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Iniciando solicitud de ejecucion del servicio");
		return getCitiesByCountry(anOriginalRequest, aBagSPJavaOrchestration);
	}

	protected IProcedureResponse getCitiesByCountry(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Iniciando Servicio");
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		Map<String, Object> wprocedureResponse1;
		try {
			wprocedureResponse1 = procedureResponse1(anOriginalRequest, aBagSPJavaOrchestration);

			Boolean wSuccessExecutionOperation1 = (Boolean) wprocedureResponse1.get("SuccessExecutionOperation");
			IProcedureResponse wIProcedureResponse1 = (IProcedureResponse) wprocedureResponse1
					.get("IProcedureResponse");
			IProcedureResponse wErrorProcedureResponse = (IProcedureResponse) wprocedureResponse1
					.get("ErrorProcedureResponse");

			if (wSuccessExecutionOperation1) {
				return wIProcedureResponse1;
			} else {
				return wErrorProcedureResponse;
			}
		} catch (CTSServiceException e) {
			if (logger.isErrorEnabled())
				logger.logError("Consulta Ciudades Error:" + e.toString());
			IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
			ErrorBlock eb = new ErrorBlock(-1, "Error en ejecucion del servicio");
			wProcedureRespFinal.addResponseBlock(eb);
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
			wProcedureRespFinal.setReturnCode(-1);
			return wProcedureRespFinal;

		} catch (CTSInfrastructureException e) {
			if (logger.isErrorEnabled())
				logger.logError("Consulta Ciudades Error:" + e.toString());
			IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
			ErrorBlock eb = new ErrorBlock(-1, "Error de Infrestructura");
			wProcedureRespFinal.addResponseBlock(eb);
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
			wProcedureRespFinal.setReturnCode(-1);
			return wProcedureRespFinal;
		}
	}

	protected Map<String, Object> procedureResponse1(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Consumiendo Servicio");

		HashMap<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("IProcedureResponse", null);
		returnMap.put("SuccessExecutionOperation", false);
		returnMap.put("ErrorProcedureResponse", null);

		IProcedureResponse wProcedureResponse = getCitiesByCountry(transformRequestToDto(aBagSPJavaOrchestration),
				aBagSPJavaOrchestration);
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Respuesta devuelta del servicio:"
					+ wProcedureResponse.getProcedureResponseAsString());

		if ((wProcedureResponse == null) || wProcedureResponse.hasError()) {
			if (logger.isDebugEnabled())
				logger.logDebug(CLASS_NAME + "Error en servicio" + wProcedureResponse.getProcedureResponseAsString());
			returnMap.put("ErrorProcedureResponse", wProcedureResponse);
			return returnMap;
		}
		returnMap.put("SuccessExecutionOperation", true);
		returnMap.put("IProcedureResponse", wProcedureResponse);
		return returnMap;
	}

	private IProcedureResponse getCitiesByCountry(CityRequest cityRequest, Map<String, Object> aBagSPJavaOrchestration)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Data de consulta:" + cityRequest.toString());
		CityResponse cityResponse = coreServiceCities.getCitiesByCountry(cityRequest);
		IProcedureResponse pResponse = transformDtoToResponse(cityResponse, aBagSPJavaOrchestration);
		return pResponse;
	}

	private IProcedureResponse transformDtoToResponse(CityResponse cityResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Salida:" + cityResponse);
		IProcedureResponse pResponse = initProcedureResponse(
				(IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));

		IResultSetData data = new ResultSetData();
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "mensaje darwin:" + cityResponse.getSuccess());
		if (cityResponse.getSuccess()) {
			if (cityResponse != null && cityResponse.getCityCollection().size() > 0) {
				IResultSetHeader metaData = new ResultSetHeader();

				metaData.addColumnMetaData(new ResultSetHeaderColumn("CIUDAD", ICTSTypes.SYBINT2, 6));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("DESCRIPCION_CIUDAD", ICTSTypes.SYBVARCHAR, 42));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("PAIS", ICTSTypes.SYBINT2, 6));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("DESCRIPCION_PAIS", ICTSTypes.SYBVARCHAR, 42));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("CONT", ICTSTypes.SYBVARCHAR, 5));
				metaData.addColumnMetaData(
						new ResultSetHeaderColumn("DESCRIPCION_CONTINENTE", ICTSTypes.SYBVARCHAR, 42));

				for (City obj : cityResponse.getCityCollection()) {
					IResultSetRow row = new ResultSetRow();
					row.addRowData(1, new ResultSetRowColumnData(false, obj.getCodeCity().toString()));
					row.addRowData(2, new ResultSetRowColumnData(false, obj.getNameCity()));
					row.addRowData(3, new ResultSetRowColumnData(false, obj.getCountry().getCode().toString()));
					row.addRowData(4, new ResultSetRowColumnData(false, obj.getCountry().getName()));
					row.addRowData(5, new ResultSetRowColumnData(false, obj.getCountry().getContinentCode()));
					row.addRowData(6, new ResultSetRowColumnData(false, obj.getCountry().getContinent()));
					data.addRow(row);
				}
				IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
				pResponse.addResponseBlock(resultBlock);
			}
			pResponse.setReturnCode(0);
		}
		return pResponse;
	}

	private CityRequest transformRequestToDto(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Entrada");
		IProcedureRequest wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

		CityRequest cityRequest = new CityRequest();

		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_cont")))
			cityRequest.setContinentCode(wOriginalRequest.readValueParam("@i_cont"));
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@i_pais")))
			cityRequest.setCountryCode(Integer.parseInt(wOriginalRequest.readValueParam("@i_pais").toString()));

		cityRequest.setOriginalRequest(wOriginalRequest);
		return cityRequest;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

}
