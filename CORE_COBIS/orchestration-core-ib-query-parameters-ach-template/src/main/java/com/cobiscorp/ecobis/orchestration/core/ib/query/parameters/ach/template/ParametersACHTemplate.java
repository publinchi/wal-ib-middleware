/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.query.parameters.ach.template;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.ParametersACHRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ParametersACHResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceParametersACH;

/**
 * @author wsanchez
 * @since 01/09/2015
 * @version 1.0.0
 */

@Component(name = "ParametersACHTemplate", immediate = false)
@Service(value = { ICoreServiceParametersACH.class })
@Properties(value = { @Property(name = "service.description", value = "ParametersACHTemplate"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ParametersACHTemplate") })

public class ParametersACHTemplate extends SPJavaOrchestrationBase implements ICoreServiceParametersACH {
	private static ILogger logger = LogFactory.getLogger(ParametersACHTemplate.class);

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
	 * ICoreServiceParametersACH#searchParametersACH(com.cobiscorp.ecobis.ib.
	 * application.dtos.ParametersACHRequest)
	 */
	@Override
	public ParametersACHResponse searchParametersACH(ParametersACHRequest aParametersACH)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo("*** Start Request *** " + aParametersACH.getOriginalRequest());
		IProcedureRequest request = initProcedureRequest(aParametersACH.getOriginalRequest());

		// IProcedureRequest request = new ProcedureRequestAS();
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1875071");
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1875071");
		request.setSpName("cobis..sp_bv_parametros_ach");

		// request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1801019");
		request.addInputParam("@i_ente_origen", ICTSTypes.SQLINT4, aParametersACH.getEnte().toString());
		request.addInputParam("@i_oficina", ICTSTypes.SQLINT4, aParametersACH.getOffice().toString());

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Request:*** " + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Start Request *** ");
		}

		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response *** ");
		}

		return transformParametersACHResponse(pResponse);
	}

	private ParametersACHResponse transformParametersACHResponse(IProcedureResponse response) {
		if (logger.isInfoEnabled())
			logger.logInfo("Ejecutando Transformacion de Respuesta");

		ParametersACHResponse responseParametersACH = new ParametersACHResponse();

		if (response == null) {
			if (logger.isInfoEnabled())
				logger.logInfo("ParametersACH --> Response null");
			return null;
		}

		if (response.getReturnCode() != 0)
			responseParametersACH.setMessages(Utils.returnArrayMessage(response));
		else {
			if (response.getResultSetListSize() > 0) {
				IResultSetRow[] rows = response.getResultSet(response.getResultSetListSize()).getData()
						.getRowsAsArray();
				IResultSetRowColumnData[] columns = rows[0].getColumnsAsArray();

				responseParametersACH.setModule(columns[0].getValue() == null ? "" : columns[0].getValue());
				responseParametersACH.setNetwork(columns[1].getValue() == null ? "" : columns[1].getValue());
				responseParametersACH.setCity(columns[2].getValue() == null ? "" : columns[2].getValue());
				responseParametersACH.setCountry(columns[3].getValue() == null ? "" : columns[3].getValue());
				responseParametersACH.setOrderType(columns[4].getValue() == null ? "" : columns[4].getValue());
				responseParametersACH.setAccountType(columns[5].getValue() == null ? "" : columns[5].getValue());
				responseParametersACH.setDocumentType(columns[6].getValue() == null ? "" : columns[6].getValue());
				responseParametersACH.setNumId(columns[7].getValue() == null ? "" : columns[7].getValue());
				responseParametersACH.setBankId(columns[8].getValue() == null ? "" : columns[8].getValue());
			}
		}

		if (logger.isInfoEnabled())
			logger.logInfo("Respuesta Devuelta Responsee" + response);

		responseParametersACH.setSuccess(response.getReturnCode() == 0 ? true : false);
		responseParametersACH.setReturnCode(response.getReturnCode());

		if (logger.isInfoEnabled())
			logger.logInfo("Respuesta Devuelta responseParametersACH" + responseParametersACH);
		return responseParametersACH;
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
