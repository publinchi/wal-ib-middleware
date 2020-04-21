/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.grb;

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
import com.cobiscorp.ecobis.ib.application.dtos.GRBOperationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.GRBOperationResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.GRB;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceGRBOperation;

@Component(name = "GRBQuery", immediate = false)
@Service(value = { ICoreServiceGRBOperation.class })
@Properties(value = { @Property(name = "service.description", value = "SubTypesQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "SubTypesQuery") })
public class GRBQuery extends SPJavaOrchestrationBase implements ICoreServiceGRBOperation {
	private static ILogger logger = LogFactory.getLogger(GRBQuery.class);
	private static final String SP_NAME = "cobis..sp_bv_grb";
	private static final int COL_ID = 0;
	private static final int COL_OPERATION = 1;
	private static final int COL_CURRENCY = 2;
	private static final int COL_CURRENCY_CODE = 3;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceBankGuarantee
	 * #getSubTypes(com.cobiscorp.ecobis.ib.application.dtos.
	 * BankGuaranteeRequest) *********
	 * *****************************************************************
	 * *******************************************************************
	 */
	@Override
	public GRBOperationResponse getOperation(GRBOperationRequest aGRBOperationRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getOperation");
		}

		IProcedureResponse pResponse = Execution(SP_NAME, aGRBOperationRequest);
		GRBOperationResponse grbOperationResponse = transformToOperationResponse(pResponse);
		return grbOperationResponse;
	}
    
	private IProcedureResponse Execution(String SpName, GRBOperationRequest aGRBOperationRequest)
			throws CTSServiceException, CTSInfrastructureException {

		IProcedureRequest request = initProcedureRequest(aGRBOperationRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);

		request.setSpName(SpName);
		request.addInputParam("@i_cliente", ICTSTypes.SYBINT2, aGRBOperationRequest.getEntity().getEnte().toString());

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
		return pResponse;
	}

	private GRBOperationResponse transformToOperationResponse(IProcedureResponse aProcedureResponse) {
		GRBOperationResponse GRBOperationResp = new GRBOperationResponse();
		List<GRB> grbOperationCollection = new ArrayList<GRB>();
		GRB aGRB = null;

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + aProcedureResponse.getProcedureResponseAsString());
		}

		if (aProcedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsGRB = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();
			for (IResultSetRow iResultSetRow : rowsGRB) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				aGRB = new GRB();
				aGRB.setId(Integer.parseInt(columns[COL_ID].getValue()));
				aGRB.setOperation(columns[COL_OPERATION].getValue());
				aGRB.setCurrency(columns[COL_CURRENCY].getValue());
				aGRB.setCurrencyCode(Integer.parseInt(columns[COL_CURRENCY_CODE].getValue()));
				grbOperationCollection.add(aGRB);
			}
			GRBOperationResp.setgrbCollection(grbOperationCollection);
		} else {
			GRBOperationResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		}
		GRBOperationResp.setReturnCode(aProcedureResponse.getReturnCode());
		return GRBOperationResp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.commons.components.ICOBISComponent#loadConfiguration
	 * (com.cobiscorp.cobis.commons.configuration.IConfigurationReader)
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase
	 * #executeJavaOrchestration
	 * (com.cobiscorp.cobis.cts.domains.IProcedureRequest, java.util.Map)
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase
	 * #processResponse(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		return null;
	}
}
