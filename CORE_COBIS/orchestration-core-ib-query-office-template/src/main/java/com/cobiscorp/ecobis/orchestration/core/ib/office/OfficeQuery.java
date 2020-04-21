/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.office;

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
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceOffice;
import com.cobiscorp.ecobis.ib.application.dtos.CreditLineRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CreditLineResponse;
import com.cobiscorp.ecobis.ib.application.dtos.OfficeRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficeResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Office;

@Component(name = "OfficeQuery", immediate = false)
@Service(value = { ICoreServiceOffice.class })
@Properties(value = { @Property(name = "service.description", value = "OfficeQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "OfficeQuery") })
public class OfficeQuery extends SPJavaOrchestrationBase implements ICoreServiceOffice {
	private static ILogger logger = LogFactory.getLogger(OfficeQuery.class);
	private static final String SP_NAME = "cobis..sp_bv_oficina";
	private static final int COL_CODE = 0;
	private static final int COL_DESCP = 1;

	private IProcedureResponse Execution(String SpName, OfficeRequest aOfficeRequest)
			throws CTSServiceException, CTSInfrastructureException {
		;
		IProcedureRequest request = initProcedureRequest(aOfficeRequest.getOriginalRequest());

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800035");

		request.setSpName(SpName);
		request.addInputParam("@i_region", ICTSTypes.SYBCHAR, aOfficeRequest.getRegion());

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceOffice#
	 * getOffice(com.cobiscorp.ecobis.ib.application.dtos.OfficeRequest)
	 */
	@Override
	public OfficeResponse getOffice(OfficeRequest aOfficeRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getLines");
		}

		IProcedureResponse pResponse = Execution(SP_NAME, aOfficeRequest);
		OfficeResponse officeResponse = transformToOfficeResponse(pResponse, aOfficeRequest.getRegion());
		return officeResponse;
	}

	/*
	 * 
	 * *************************************************************************
	 * ********************************************************************
	 */
	private OfficeResponse transformToOfficeResponse(IProcedureResponse aProcedureResponse, String region) {
		OfficeResponse OfficeResp = new OfficeResponse();
		List<Office> officeCollection = new ArrayList<Office>();
		Office aOffice = null;

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + aProcedureResponse.getProcedureResponseAsString());
		}

		if (aProcedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsOffice = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

			for (IResultSetRow iResultSetRow : rowsOffice) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				aOffice = new Office();
				aOffice.setId(Integer.parseInt(columns[COL_CODE].getValue()));
				aOffice.setDescription(columns[COL_DESCP].getValue());

				officeCollection.add(aOffice);
			}
			OfficeResp.setOfficeCollection(officeCollection);
		} else {

			OfficeResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		}

		OfficeResp.setReturnCode(aProcedureResponse.getReturnCode());

		return OfficeResp;
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
