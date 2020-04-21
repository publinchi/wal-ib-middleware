package com.cobiscorp.ecobis.orchestration.core.ib.pay.online;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.OnlineServiceRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OnlineServiceResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.OnlinePaymentDetail;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreOnlineServiceInvoicingQuery;

@Component(name = "PayOnlineQuery", immediate = false)
@Service(value = { ICoreOnlineServiceInvoicingQuery.class })
@Properties(value = { @Property(name = "service.description", value = "PayOnlineQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "PayOnlineQuery") })
public class PayOnlineQuery extends SPJavaOrchestrationBase implements ICoreOnlineServiceInvoicingQuery {

	private static final String CLASS_NAME = " >-----> PayOnlineQuery";
	private static final String COBIS_CONTEXT = "COBIS";
	private static ILogger logger = LogFactory.getLogger(PayOnlineQuery.class);

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
	 * ICoreOnlineServiceInvoicingQuery#getOnlineService(com.cobiscorp.ecobis.ib
	 * .application.dtos.OnlineServiceRequest)
	 */
	@Override
	public OnlineServiceResponse getOnlineService(OnlineServiceRequest onlineServiceRequest)
			throws CTSServiceException, CTSInfrastructureException {

		IProcedureRequest request = new ProcedureRequestAS();
		IProcedureResponse pResponse = new ProcedureResponseAS();

		return transforToOnlineServiceResponse(pResponse);
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

	public OnlineServiceResponse transforToOnlineServiceResponse(IProcedureResponse response) {
		if (logger.isDebugEnabled())
			logger.logDebug("Inicio proceso transformacion transforToOnlineServiceResponse");
		OnlineServiceResponse onlineServiceResponse = new OnlineServiceResponse();
		OnlinePaymentDetail onlinePaymentDetail = new OnlinePaymentDetail();
		List<OnlinePaymentDetail> onlinePaymentDetailList = new ArrayList<OnlinePaymentDetail>();
		onlinePaymentDetail.setContractId("123456789");
		onlinePaymentDetail.setNumber("1234");
		onlinePaymentDetail.setReceipts(1);
		onlinePaymentDetail.setTotalPay(new BigDecimal("100"));
		onlinePaymentDetail.setPeriod("2015-01-01");
		onlinePaymentDetail.setExpirationDate("2015-01-02");
		onlinePaymentDetail.setReceiptNumber("123456789");
		onlinePaymentDetail.setTotalPayment(new BigDecimal("150"));
		onlinePaymentDetail.setSelf("123");
		onlinePaymentDetail.setThridPartyPaymentKey("1234");
		onlinePaymentDetail.setCollectorId(10);
		onlinePaymentDetail.setOffice(93);
		onlinePaymentDetail.setNumber("0001");
		onlinePaymentDetail.setIdentification("0927858731");
		onlinePaymentDetail.setName("Ronald");
		onlinePaymentDetail.setNumberService(112);
		onlinePaymentDetail.setState(0);
		onlinePaymentDetail.setResponse("ok");
		onlinePaymentDetailList.add(onlinePaymentDetail);
		onlineServiceResponse.setOnlinePaymentDetailList(onlinePaymentDetailList);
		onlineServiceResponse.setSuccess(true);
		onlineServiceResponse.setReturnCode(0);
		if (logger.isDebugEnabled()) {
			logger.logDebug("termina proceso transformacion transforToOnlineServiceResponse");
			logger.logDebug(onlineServiceResponse.toString());
		}
		return onlineServiceResponse;
	}
}
