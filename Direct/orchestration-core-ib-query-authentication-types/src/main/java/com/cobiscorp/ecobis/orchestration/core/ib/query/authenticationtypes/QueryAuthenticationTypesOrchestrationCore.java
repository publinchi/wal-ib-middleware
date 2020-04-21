package com.cobiscorp.ecobis.orchestration.core.ib.query.authenticationtypes;

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
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.csp.util.CSPUtil;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.AuthenticationTypeRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AuthenticationTypeResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AuthenticationType;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceAuthenticationType;

/**
 * @author gcondo
 * @since Nov 10, 2014
 * @version 1.0.0
 */
@Component(name = "QueryAuthenticationTypesOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "QueryAuthenticationTypesOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "QueryAuthenticationTypesOrchestrationCore") })
public class QueryAuthenticationTypesOrchestrationCore extends SPJavaOrchestrationBase {

	ILogger logger = this.getLogger();
	private static final String CLASS_NAME = "QueryAuthenticationTypesOrchestrationCore--->";
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceAuthenticationType.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	private ICoreServiceAuthenticationType coreServiceAuthenticationType;

	/**
	 * Instance Service Interface
	 * 
	 * @param service
	 */
	protected void bindCoreService(ICoreServiceAuthenticationType service) {
		coreServiceAuthenticationType = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	protected void unbindCoreService(ICoreServiceAuthenticationType service) {
		coreServiceAuthenticationType = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeJavaOrchestration");
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		AuthenticationTypeResponse wAuthTypeResp = null;

		try {
			Map<String, Object> mapInterfaces = new HashMap<String, Object>();
			mapInterfaces.put("coreServiceAuthenticationType", coreServiceAuthenticationType);
			Utils.validateComponentInstance(mapInterfaces);

			if (anOriginalRequest == null)
				if (logger.isInfoEnabled())
					logger.logInfo(CLASS_NAME + "Original Request ISNULL");

			AuthenticationTypeRequest wAuthenticationTypeRequest = transformAuthenticationTypeRequest(
					anOriginalRequest.clone());

			wAuthTypeResp = this.coreServiceAuthenticationType.getAuthenticationTypes(wAuthenticationTypeRequest);
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, wAuthTypeResp);

			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.logInfo("*********  Error en " + e.getMessage(), e);
			}

			IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
			ErrorBlock eb = new ErrorBlock(-1, "Service is not available");
			wProcedureRespFinal.addResponseBlock(eb);
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
			wProcedureRespFinal.setReturnCode(-1);
			return wProcedureRespFinal;
		}

	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureResponse response = transformProcedureResponse(aBagSPJavaOrchestration);
		CSPUtil.copyHeaderFields((IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST), response);
		aBagSPJavaOrchestration.remove(RESPONSE_TRANSACTION);

		return response;
	}

	/**
	 * @param aRequest
	 * @return
	 */
	private AuthenticationTypeRequest transformAuthenticationTypeRequest(IProcedureRequest aRequest) {
		AuthenticationTypeRequest AuthTypeReq = new AuthenticationTypeRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("<<<Procedure Request to Transform->>>" + aRequest.getProcedureRequestAsString());

		AuthTypeReq.setOperationType(aRequest.readValueParam("@i_tipo"));
		if (aRequest.readValueParam("@i_modo") != null)
			AuthTypeReq.setMode(Short.parseShort(aRequest.readValueParam("@i_modo")));
		AuthTypeReq.setProductCode(Short.parseShort(aRequest.readValueParam("@i_producto")));
		AuthTypeReq.setInstrumentCode(Short.parseShort(aRequest.readValueParam("@i_instrumento")));
		AuthTypeReq.setSubTypeCode(Short.parseShort(aRequest.readValueParam("@i_sub_tipo")));

		if (aRequest.readValueParam("@i_serie_literal") != null)
			AuthTypeReq.setLiteralSeries(aRequest.readValueParam("@i_serie_literal"));

		if (aRequest.readValueParam("@i_serie_desde") != null)
			AuthTypeReq.setSeriesFrom(Double.parseDouble(aRequest.readValueParam("@i_serie_desde")));

		if (aRequest.readValueParam("@i_serie_hasta") != null)
			AuthTypeReq.setSeriesTo(Double.parseDouble(aRequest.readValueParam("@i_serie_hasta")));

		if (aRequest.readValueParam("@i_area") != null)
			AuthTypeReq.setArea(Integer.parseInt(aRequest.readValueParam("@i_area")));

		if (aRequest.readValueParam("@i_func_area") != null)
			AuthTypeReq.setAreaOfficerCode(Integer.parseInt(aRequest.readValueParam("@i_func_area")));

		if (aRequest.readValueParam("@i_parametros") != null)
			AuthTypeReq.setParameter(Short.parseShort(aRequest.readValueParam("@i_parametros")));

		if (aRequest.readValueParam("@i_oficina") != null)
			AuthTypeReq.setOfficeCode(Short.parseShort(aRequest.readValueParam("@i_oficina")));
		else
			AuthTypeReq.setOfficeCode(Short.parseShort(aRequest.readValueParam("@s_ofi")));

		if (aRequest.readValueParam("@i_login") != null)
			AuthTypeReq.setUserBv(aRequest.readValueParam("@i_login"));

		if (aRequest.readValueParam("@i_tipo_remesas") != null)
			AuthTypeReq.setRemittanceType(aRequest.readValueParam("@i_tipo_remesas"));

		return AuthTypeReq;
	}

	/**
	 * @param aBagSPJavaOrchestration
	 * @return
	 */
	private IProcedureResponse transformProcedureResponse(Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("<<<Transform Procedure Response>>");

		IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		AuthenticationTypeResponse wAuthTypeResp = (AuthenticationTypeResponse) aBagSPJavaOrchestration
				.get(RESPONSE_TRANSACTION);

		if (wAuthTypeResp.getReturnCode() != 0) {
			// Si hubo error
			wProcedureResponse = Utils.returnException(wAuthTypeResp.getMessages());
			wProcedureResponse.setReturnCode(wAuthTypeResp.getReturnCode());
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, wProcedureResponse);

			return wProcedureResponse;
		}

		if (wAuthTypeResp.getAuthenticationTypeCollection() != null) {
			// Add Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			if (originalRequest.readValueParam("@i_tipo").equals("P")) {

				metaData.addColumnMetaData(new ResultSetHeaderColumn("CHECK", ICTSTypes.SQLINT1, 20));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("PRODUCTO", ICTSTypes.SQLINT1, 20));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("FORMA", ICTSTypes.SQLINT2, 20));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("COD.SUBTIPO", ICTSTypes.SQLINT4, 20));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("SUBTIPO", ICTSTypes.SQLVARCHAR, 20));
				// metaData.addColumnMetaData(new ResultSetHeaderColumn("SERIE
				// LITERAL", ICTSTypes.SQLVARCHAR, 20));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("SERIE DESDE", ICTSTypes.SQLVARCHAR, 20));
				// metaData.addColumnMetaData(new ResultSetHeaderColumn("SERIE
				// HASTA", ICTSTypes.SQLVARCHAR, 20));
				// metaData.addColumnMetaData(new
				// ResultSetHeaderColumn("DISPONIBLE", ICTSTypes.SQLINT4, 20));

				for (AuthenticationType aAuthenticationTypeResp : wAuthTypeResp.getAuthenticationTypeCollection()) {

					IResultSetRow row = new ResultSetRow();

					row.addRowData(1,
							new ResultSetRowColumnData(false, String.valueOf(aAuthenticationTypeResp.getCheckValue())));
					row.addRowData(2, new ResultSetRowColumnData(false,
							String.valueOf(aAuthenticationTypeResp.getProductCode())));
					row.addRowData(3, new ResultSetRowColumnData(false,
							String.valueOf(aAuthenticationTypeResp.getInstrumentCode())));
					row.addRowData(4, new ResultSetRowColumnData(false,
							String.valueOf(aAuthenticationTypeResp.getSubTypeCode())));
					row.addRowData(5, new ResultSetRowColumnData(false, aAuthenticationTypeResp.getSubTypeName()));
					// row.addRowData(6, new ResultSetRowColumnData(false,
					// aAuthenticationTypeResp.getLiteralSeries()));
					row.addRowData(6,
							new ResultSetRowColumnData(false, String.valueOf(aAuthenticationTypeResp.getSeriesFrom())));
					// row.addRowData(8, new ResultSetRowColumnData(false,
					// String.valueOf(aAuthenticationTypeResp.getSeriesTo())));
					// row.addRowData(9, new ResultSetRowColumnData(false,
					// String.valueOf(aAuthenticationTypeResp.getAvailableQty())));
					data.addRow(row);
				}
			}

			if (originalRequest.readValueParam("@i_tipo").equals("V")) {
				metaData.addColumnMetaData(new ResultSetHeaderColumn("NUM DISP", ICTSTypes.SQLVARCHAR, 20));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("DESC SUBTIPO", ICTSTypes.SQLVARCHAR, 20));

				for (AuthenticationType aAuthenticationTypeResp : wAuthTypeResp.getAuthenticationTypeCollection()) {

					IResultSetRow row = new ResultSetRow();

					row.addRowData(1,
							new ResultSetRowColumnData(false, String.valueOf(aAuthenticationTypeResp.getSeriesFrom())));
					row.addRowData(2, new ResultSetRowColumnData(false, aAuthenticationTypeResp.getSubTypeName()));
					data.addRow(row);
				}
			}

			IResultSetBlock resultBlock1 = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock1);
		}
		wAuthTypeResp.setReturnCode(wAuthTypeResp.getReturnCode());
		wAuthTypeResp.setSuccess(wAuthTypeResp.getReturnCode() == 0 ? true : false);

		if (logger.isDebugEnabled())
			logger.logDebug("<<<Procedure Response Final -->>>" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}
}
