package com.cobiscorp.ecobis.orchestration.core.ib.ach.detail;

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
import com.cobiscorp.ecobis.ib.application.dtos.ACHDetailRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ACHDetailResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AchDetail;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceACHDetail;

@Component(name = "ACHDetailQuery", immediate = false)
@Service(value = { ICoreServiceACHDetail.class })
@Properties(value = { @Property(name = "service.description", value = "SubTypesQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "SubTypesQuery") })
public class ACHDetailQuery extends SPJavaOrchestrationBase implements ICoreServiceACHDetail {
	private static ILogger logger = LogFactory.getLogger(ACHDetailQuery.class);
	private static final String SP_NAME = "cobis..sp_bv_cons_status_ach";

	/**** Output: getOperation *****/
	private static final int COL_NUMBER_ORDER = 0;
	private static final int COL_STATUS = 1;

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
	public ACHDetailResponse getACHDetail(ACHDetailRequest aACHDetailRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getACHDetail");
		}

		IProcedureResponse pResponse = Execution(SP_NAME, aACHDetailRequest);
		ACHDetailResponse achDetailResponse = transformToDetailResponse(pResponse);
		return achDetailResponse;
	}

	private IProcedureResponse Execution(String SpName, ACHDetailRequest aACHDetailRequest)
			throws CTSServiceException, CTSInfrastructureException {

		IProcedureRequest request = initProcedureRequest(aACHDetailRequest.getOriginalRequest());

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setSpName(SpName);

		request.addInputParam("@i_number_order", ICTSTypes.SYBVARCHAR, aACHDetailRequest.getNumberOrder());

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

	private ACHDetailResponse transformToDetailResponse(IProcedureResponse aProcedureResponse) {
		ACHDetailResponse ACHDetailResp = new ACHDetailResponse();
		List<AchDetail> achDetailCollection = new ArrayList<AchDetail>();
		AchDetail aAchDetail = null;

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + aProcedureResponse.getProcedureResponseAsString());
		}

		if (aProcedureResponse.getReturnCode() == 0) {

			IResultSetRow[] rowsAchDetail = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

			for (IResultSetRow iResultSetRow : rowsAchDetail) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

				aAchDetail = new AchDetail();
				aAchDetail.setNumberOrder(columns[COL_NUMBER_ORDER].getValue());
				aAchDetail.setStatus(columns[COL_STATUS].getValue());
				achDetailCollection.add(aAchDetail);
			}

			ACHDetailResp.setAchDetailCollection(achDetailCollection);
		} else {

			ACHDetailResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		}

		ACHDetailResp.setReturnCode(aProcedureResponse.getReturnCode());

		return ACHDetailResp;
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
