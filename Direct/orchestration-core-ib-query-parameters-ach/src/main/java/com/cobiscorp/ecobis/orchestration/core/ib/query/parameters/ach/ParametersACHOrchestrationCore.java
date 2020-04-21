package com.cobiscorp.ecobis.orchestration.core.ib.query.parameters.ach;

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
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
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
import com.cobiscorp.ecobis.ib.application.dtos.ParametersACHRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ParametersACHResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceParametersACH;

@Component(name = "ParametersACHOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "ParametersACHOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1"),
		@Property(name = "service.identifier", value = "ParametersACHOrchestrationCore") })

public class ParametersACHOrchestrationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(ParametersACHOrchestrationCore.class);
	private static final String CLASS_NAME = "ParametersACHOrchestrationCore--->";
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	@Reference(referenceInterface = ICoreServiceParametersACH.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceParametersACH", unbind = "unbindCoreServiceParametersACH")
	protected ICoreServiceParametersACH coreServiceParametersACH;

	/**
	 * Instance Service Interface
	 * 
	 * @param service
	 */
	public void bindCoreServiceParametersACH(ICoreServiceParametersACH service) {
		coreServiceParametersACH = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	public void unbindCoreServiceParametersACH(ICoreServiceParametersACH service) {
		coreServiceParametersACH = null;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeJavaOrchestration");
		ParametersACHRequest aParametersACHRequest = new ParametersACHRequest();
		ParametersACHResponse aParametersACHResponse = new ParametersACHResponse();
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		try {
			Map<String, Object> mapInterfaces = new HashMap<String, Object>();
			mapInterfaces.put("coreServiceParametersACH", coreServiceParametersACH);

			Utils.validateComponentInstance(mapInterfaces);

			if (anOriginalRequest == null)
				if (logger.isInfoEnabled())
					logger.logInfo(CLASS_NAME + "Original Request ISNULL");

			aParametersACHRequest = transformParametersACHRequest(anOriginalRequest);
			// aParametersACHRequest.setOriginalRequest(anOriginalRequest);
			aParametersACHResponse = coreServiceParametersACH.searchParametersACH(aParametersACHRequest);
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, aParametersACHResponse);

			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
			// return transformProcedureResponse(aParametersACHResponse,
			// aBagSPJavaOrchestration);

		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.logInfo("*********  Error en " + e.getMessage(), e);
			}
			return Utils.returnException("Service is not available");
		}
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		return transformProcedureResponse((ParametersACHResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION"),
				aBagSPJavaOrchestration);
	}

	private ParametersACHRequest transformParametersACHRequest(IProcedureRequest aRequest) {
		ParametersACHRequest requestParametersACH = new ParametersACHRequest();
		requestParametersACH.setOriginalRequest(aRequest);

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;
		messageError = aRequest.readValueParam("@i_ente_origen") == null ? " - @i_ente_origen can't be null" : "";
		messageError = aRequest.readValueParam("@i_oficina") == null ? " - @i_oficina can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		requestParametersACH.setEnte(Integer.parseInt(aRequest.readValueParam("@i_ente_origen")));
		requestParametersACH.setOffice(Integer.parseInt(aRequest.readValueParam("@i_oficina")));

		return requestParametersACH;
	}

	private IProcedureResponse transformProcedureResponse(ParametersACHResponse aParametersACHResponse,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response ParametersACHResponse");

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		if (aParametersACHResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aParametersACHResponse.getMessages()));

		} else {

			IResultSetData data = new ResultSetData();
			if (aParametersACHResponse != null) {
				IResultSetRow row = new ResultSetRow();
				IResultSetHeader metaData = new ResultSetHeader();

				metaData.addColumnMetaData(new ResultSetHeaderColumn("modulo", ICTSTypes.SQLVARCHAR, 20));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("red_ach", ICTSTypes.SQLVARCHAR, 20));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("ciudad", ICTSTypes.SQLVARCHAR, 20));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("pais", ICTSTypes.SQLVARCHAR, 20));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("tipo_orden_env", ICTSTypes.SQLVARCHAR, 20));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("tipo_cta_orig", ICTSTypes.SQLVARCHAR, 20));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("tipo_doc", ICTSTypes.SQLVARCHAR, 20));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("num_id", ICTSTypes.SQLVARCHAR, 20));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("banco", ICTSTypes.SQLVARCHAR, 20));

				row.addRowData(1, new ResultSetRowColumnData(false, aParametersACHResponse.getModule()));
				row.addRowData(2, new ResultSetRowColumnData(false, aParametersACHResponse.getNetwork()));
				row.addRowData(3, new ResultSetRowColumnData(false, aParametersACHResponse.getCity()));
				row.addRowData(4, new ResultSetRowColumnData(false, aParametersACHResponse.getCountry()));
				row.addRowData(5, new ResultSetRowColumnData(false, aParametersACHResponse.getOrderType()));
				row.addRowData(6, new ResultSetRowColumnData(false, aParametersACHResponse.getAccountType()));
				row.addRowData(7, new ResultSetRowColumnData(false, aParametersACHResponse.getDocumentType()));
				row.addRowData(8, new ResultSetRowColumnData(false, aParametersACHResponse.getNumId()));
				row.addRowData(9, new ResultSetRowColumnData(false, aParametersACHResponse.getBankId()));

				data.addRow(row);
				IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
				wProcedureResponse.addResponseBlock(resultBlock);
			}
		}

		wProcedureResponse.setReturnCode(aParametersACHResponse.getReturnCode());

		return wProcedureResponse;
	}

}
