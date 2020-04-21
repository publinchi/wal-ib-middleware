/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.authenticationtypes;

import java.util.ArrayList;
import java.util.List;
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
import com.cobiscorp.ecobis.ib.application.dtos.AuthenticationTypeRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AuthenticationTypeResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AuthenticationType;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceAuthenticationType;

@Component(name = "AuthenticationTypeQuery", immediate = false)
@Service(value = { ICoreServiceAuthenticationType.class })
@Properties(value = { @Property(name = "service.description", value = "AuthenticationTypeQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "AuthenticationTypeQuery") })

public class AuthenticationTypesQuery extends SPJavaOrchestrationBase implements ICoreServiceAuthenticationType {

	private static final String COBIS_CONTEXT = "COBIS";

	/**
	 * Instance logger component
	 */
	private static ILogger logger = LogFactory.getLogger(AuthenticationTypesQuery.class);

	private AuthenticationTypeResponse transformToAuthenticationTypeResponse(String wOperationType,
			IProcedureResponse aProcedureResponse) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("--->>>START--->>>transformToAuthenticationTypeResponse");
		}

		AuthenticationTypeResponse AuthenticationTypeResp = new AuthenticationTypeResponse();
		List<AuthenticationType> aAuthenticationTypeCollection = new ArrayList<AuthenticationType>();
		AuthenticationType aAuthenticationType = null;

		if (aProcedureResponse == null) {
			if (logger.isInfoEnabled())
				logger.logInfo("transformToAuthenticationTypeResponse --> Response null");
			return null;
		}

        if (logger.isInfoEnabled())
		logger.logInfo("transformToAuthenticationTypeResponse aProcedureResponse.size: "
				+ aProcedureResponse.getResultSetListSize());
		if (logger.isDebugEnabled()) {
			logger.logDebug("ProcedureResponse: " + aProcedureResponse.getProcedureResponseAsString());
		}

		if (aProcedureResponse.getReturnCode() != 0)
			AuthenticationTypeResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		else {
			if (aProcedureResponse.getResultSetListSize() > 0) {
				IResultSetRow[] rowsAuthenticationType = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

				for (IResultSetRow iResultSetRow : rowsAuthenticationType) {
					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
					aAuthenticationType = new AuthenticationType();

					if (logger.isDebugEnabled()) {
						logger.logDebug("---->>>>>Size columns:" + columns.length);
					}

					if (wOperationType.equals("P")) {
						aAuthenticationType.setCheckValue(Short.parseShort(columns[0].getValue()));
						aAuthenticationType.setProductCode(Short.parseShort(columns[1].getValue()));
						aAuthenticationType.setInstrumentCode(Integer.parseInt(columns[2].getValue()));
						aAuthenticationType.setSubTypeCode(Integer.parseInt(columns[3].getValue()));
						aAuthenticationType.setSubTypeName(columns[4].getValue());
						aAuthenticationType.setLiteralSeries(columns[5].getValue());
						aAuthenticationType.setSeriesFrom(Integer.parseInt(columns[6].getValue()));
						aAuthenticationType.setSeriesTo(Integer.parseInt(columns[7].getValue()));
						aAuthenticationType.setAvailableQty(Integer.parseInt(columns[8].getValue()));
					}

					if (wOperationType.equals("V")) {
						aAuthenticationType.setSeriesFrom(Integer.parseInt(columns[0].getValue()));
						aAuthenticationType.setSubTypeName(columns[1].getValue());
					}

					if (logger.isDebugEnabled()) {
						logger.logDebug("---->>>>TIPO AUTENTICACION:" + aAuthenticationType.toString());
					}

					aAuthenticationTypeCollection.add(aAuthenticationType);
				}

				if (aAuthenticationTypeCollection.size() > 0)
					AuthenticationTypeResp.setAuthenticationTypeCollection(aAuthenticationTypeCollection);
			}
		}
		AuthenticationTypeResp.setReturnCode(aProcedureResponse.getReturnCode());
		AuthenticationTypeResp.setSuccess(aProcedureResponse.getReturnCode() == 0 ? true : false);

		if (logger.isDebugEnabled()) {
			logger.logDebug("---->>>>AuthenticationTypeResp:" + AuthenticationTypeResp);
		}
		return AuthenticationTypeResp;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceAuthenticationType#getAuthenticationTypes(com.cobiscorp.
	 * ecobis.ib.application.dtos.AuthenticationTypeRequest)
	 */
	@Override
	public AuthenticationTypeResponse getAuthenticationTypes(AuthenticationTypeRequest wAuthenticationTypeRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: getAuthenticationTypes");
			logger.logInfo("RESPUESTA CORE COBIS GENERADA");
		}

		IProcedureRequest request = new ProcedureRequestAS();

		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "29291");
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);

		request.setSpName("cob_sbancarios..sp_buscar_instrumentos_inv");
		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, "29291");
		request.addInputParam("@s_ofi", ICTSTypes.SQLINT2, String.valueOf(wAuthenticationTypeRequest.getOfficeCode()));
		request.addInputParam("@i_tipo", ICTSTypes.SQLCHAR, wAuthenticationTypeRequest.getOperationType());

		if (String.valueOf(wAuthenticationTypeRequest.getProductCode()) != null)
			request.addInputParam("@i_producto", ICTSTypes.SQLINT1,
					String.valueOf(wAuthenticationTypeRequest.getProductCode()));

		if (String.valueOf(wAuthenticationTypeRequest.getInstrumentCode()) != null)
			request.addInputParam("@i_instrumento", ICTSTypes.SQLINT2,
					String.valueOf(wAuthenticationTypeRequest.getInstrumentCode()));

		if (String.valueOf(wAuthenticationTypeRequest.getSubTypeCode()) != null)
			request.addInputParam("@i_sub_tipo", ICTSTypes.SQLINT4,
					String.valueOf(wAuthenticationTypeRequest.getSubTypeCode()));

		if (wAuthenticationTypeRequest.getLiteralSeries() != null)
			request.addInputParam("@i_serie_literal", ICTSTypes.SQLVARCHAR,
					wAuthenticationTypeRequest.getLiteralSeries());

		if (String.valueOf(wAuthenticationTypeRequest.getSeriesFrom()) != null)
			request.addInputParam("@i_serie_desde", ICTSTypes.SQLMONEY,
					String.valueOf(wAuthenticationTypeRequest.getSeriesFrom()));

		if (String.valueOf(wAuthenticationTypeRequest.getSeriesTo()) != null)
			request.addInputParam("@i_serie_hasta", ICTSTypes.SQLMONEY,
					String.valueOf(wAuthenticationTypeRequest.getSeriesTo()));

		if (String.valueOf(wAuthenticationTypeRequest.getArea()) != null)
			request.addInputParam("@i_area", ICTSTypes.SQLINT2, String.valueOf(wAuthenticationTypeRequest.getArea()));

		if (String.valueOf(wAuthenticationTypeRequest.getAreaOfficerCode()) != null)
			request.addInputParam("@i_func_area", ICTSTypes.SQLINT4,
					String.valueOf(wAuthenticationTypeRequest.getAreaOfficerCode()));

		if (String.valueOf(wAuthenticationTypeRequest.getMode()) != null)
			request.addInputParam("@i_modo", ICTSTypes.SQLINT1, String.valueOf(wAuthenticationTypeRequest.getMode()));

		if (String.valueOf(wAuthenticationTypeRequest.getParameter()) != null)
			request.addInputParam("@i_parametros", ICTSTypes.SQLINT1,
					String.valueOf(wAuthenticationTypeRequest.getParameter()));

		request.addInputParam("@i_oficina", ICTSTypes.SQLINT2,
				String.valueOf(wAuthenticationTypeRequest.getOfficeCode()));

		if (wAuthenticationTypeRequest.getUserBv() != null)
			request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, wAuthenticationTypeRequest.getUserBv());

		if (wAuthenticationTypeRequest.getRemittanceType() != null)
			request.addInputParam("@i_tipo_remesas", ICTSTypes.SQLCHAR, wAuthenticationTypeRequest.getRemittanceType());

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request: " + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Start Request");
		}

		/* Ejecuta y obtiene la respuesta */
		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("--->>>>ResponseCOREBANKING: " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Finalize Response");
		}

		AuthenticationTypeResponse wAuthenticationTypeResponse = transformToAuthenticationTypeResponse(
				wAuthenticationTypeRequest.getOperationType(), pResponse);
		if (logger.isInfoEnabled()) {
			logger.logInfo("---->>>>TransformResponse--->>>>" + wAuthenticationTypeResponse);
		}
		return wAuthenticationTypeResponse;

	}

}
