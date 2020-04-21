package com.cobiscorp.ecobis.orchestration.core.ib.enquiries.detail;

import java.math.BigDecimal;
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
import com.cobiscorp.ecobis.ib.application.dtos.EnquiriesDetailRequest;
import com.cobiscorp.ecobis.ib.application.dtos.EnquiriesDetailResponse;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceEnquiriesDetail;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.EnquiriesDetail;

@Component(name = "EnquiriesDetailQuery", immediate = false)
@Service(value = { ICoreServiceEnquiriesDetail.class })
@Properties(value = { @Property(name = "service.description", value = "EnquiriesDetailQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "EnquiriesDetailQuery") })
public class EnquiriesDetailQuery extends SPJavaOrchestrationBase implements ICoreServiceEnquiriesDetail {
	private static ILogger logger = LogFactory.getLogger(EnquiriesDetailQuery.class);
	private static final String SP_NAME = "cobis..sp_bv_cons_detalle_solicitud";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceStocksQuery
	 * #getDetail(com.cobiscorp.ecobis.ib.application.dtos.StockRequest)
	 * *********
	 * *****************************************************************
	 * *******************************************************************
	 */
	@Override
	public EnquiriesDetailResponse getDetail(EnquiriesDetailRequest aEnquiriesDetailRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getDetail");
		}

		IProcedureResponse pResponse = Execution(SP_NAME, aEnquiriesDetailRequest);
		EnquiriesDetailResponse enquiriesDetailResponse = transformToDetailResponse(pResponse,
				aEnquiriesDetailRequest.getOperation());
		return enquiriesDetailResponse;
	}
    
	private IProcedureResponse Execution(String SpName, EnquiriesDetailRequest aEnquiriesDetailRequest)
			throws CTSServiceException, CTSInfrastructureException {

		IProcedureRequest request = initProcedureRequest(aEnquiriesDetailRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1875070");
		request.setSpName(SpName);
		request.addInputParam("@i_tipo_solicitud", ICTSTypes.SYBCHAR, aEnquiriesDetailRequest.getOperation());
		request.addInputParam("@i_cta", ICTSTypes.SYBVARCHAR, aEnquiriesDetailRequest.getAccount());
		if (aEnquiriesDetailRequest.getCheckbook() != null)
			request.addInputParam("@i_chequera", ICTSTypes.SYBINT4, aEnquiriesDetailRequest.getCheckbook().toString());
		if (aEnquiriesDetailRequest.getId() != null)
			request.addInputParam("@i_id", ICTSTypes.SYBINT4, aEnquiriesDetailRequest.getId().toString());

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
    
	private EnquiriesDetailResponse transformToDetailResponse(IProcedureResponse aProcedureResponse, String operation) {
		EnquiriesDetailResponse EnquiriesDetailResp = new EnquiriesDetailResponse();
		List<EnquiriesDetail> detailCollection = new ArrayList<EnquiriesDetail>();
		EnquiriesDetail aEnquiriesDetail = null;

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + aProcedureResponse.getProcedureResponseAsString());
		}

		if (aProcedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsDetail = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();
			for (IResultSetRow iResultSetRow : rowsDetail) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

				aEnquiriesDetail = new EnquiriesDetail();
				if (operation.equals("CHQERA")) {
					aEnquiriesDetail.setAccount(columns[0].getValue());
					aEnquiriesDetail.setCheckbookTipe(columns[1].getValue());
					aEnquiriesDetail.setChecks(Integer.parseInt(columns[2].getValue()));
					aEnquiriesDetail.setDelivery(columns[3].getValue());
					aEnquiriesDetail.setState(columns[4].getValue());
				} else if (operation.equals("CHQGER")) {
					aEnquiriesDetail.setApplicationNumber(Integer.parseInt(columns[0].getValue()));
					aEnquiriesDetail.setAccount(columns[1].getValue());
					aEnquiriesDetail.setAmount(new BigDecimal(columns[2].getValue()));
					aEnquiriesDetail.setDelivery(columns[3].getValue());
					aEnquiriesDetail.setPurpose(columns[4].getValue());
					aEnquiriesDetail.setBeneficiary(columns[5].getValue());
					aEnquiriesDetail.setState(columns[6].getValue());
					aEnquiriesDetail.setThirdIdentification(columns[7].getValue());
					aEnquiriesDetail.setName(columns[8].getValue());
				} else if (operation.equals("DSLCRE")) {
					aEnquiriesDetail.setApplicationNumber(Integer.parseInt(columns[0].getValue()));
					aEnquiriesDetail.setAccount(columns[1].getValue());
					aEnquiriesDetail.setAmount(new BigDecimal(columns[2].getValue()));
					aEnquiriesDetail.setState(columns[3].getValue());
				} else if (operation.equals("BOLGAR")) {
					aEnquiriesDetail.setApplicationNumber(Integer.parseInt(columns[0].getValue()));
					aEnquiriesDetail.setAccount(columns[1].getValue());
					aEnquiriesDetail.setType(columns[2].getValue());
					aEnquiriesDetail.setSubtype(columns[3].getValue());
					aEnquiriesDetail.setTerm(Integer.parseInt(columns[4].getValue()));
					aEnquiriesDetail.setEndDate(columns[5].getValue());
					aEnquiriesDetail.setBeneficiary(columns[6].getValue());
					aEnquiriesDetail.setGuarantee(columns[7].getValue());
					aEnquiriesDetail.setAmount(new BigDecimal(columns[8].getValue()));
					aEnquiriesDetail.setEndorsementType(columns[9].getValue());
					aEnquiriesDetail.setEndorsement(columns[10].getValue());
					aEnquiriesDetail.setState(columns[11].getValue());
				}
				detailCollection.add(aEnquiriesDetail);
			}
			EnquiriesDetailResp.setEnquiriesDetailCollection(detailCollection);
		} else {
			EnquiriesDetailResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		}
		EnquiriesDetailResp.setReturnCode(aProcedureResponse.getReturnCode());
		return EnquiriesDetailResp;
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
