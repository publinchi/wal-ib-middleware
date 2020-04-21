package com.cobiscorp.ecobis.orchestration.core.ib.based.billing;

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
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.ecobis.ib.application.dtos.BasedBillingRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BasedBillingResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BasedBilling;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreBasedBillingQuery;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;

/**
 * @author rperero
 * @since Feb 10, 2015
 * @version 1.0.0
 */

@Component(name = "BasedBillingQuery", immediate = false)
@Service(value = { ICoreBasedBillingQuery.class })
@Properties(value = { @Property(name = "service.description", value = "BasedBillingQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "BasedBillingQuery") })
public class BasedBillingQuery extends SPJavaOrchestrationBase implements ICoreBasedBillingQuery {

	private static final String CLASS_NAME = " >-----> PaymentServiceTemplate";
	private static final String COBIS_CONTEXT = "COBIS";
	private static ILogger logger = LogFactory.getLogger(BasedBillingQuery.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreBasedBillingQuery#
	 * getBasedBilling(com.cobiscorp.ecobis.ib.application.dtos.
	 * BasedBillingRequest)
	 */
	@Override
	public BasedBillingResponse getBasedBilling(BasedBillingRequest basedBillingRequest)
			throws CTSServiceException, CTSInfrastructureException {

		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: getBasedBilling");
			logger.logInfo("RESPUESTA CORE COBIS GENERADA");
		}
		IProcedureRequest request = new ProcedureRequestAS();
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "668");
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.setSpName("cob_remesas..sp_consulta_base_fact");
		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, "668");
		request.addInputParam("@i_formato_fecha", ICTSTypes.SQLINT4, basedBillingRequest.getDateFormat().toString());
		request.addInputParam("@i_busqueda1", ICTSTypes.SQLVARCHAR, basedBillingRequest.getCriteria());
		request.addInputParam("@i_identificacion", ICTSTypes.SQLVARCHAR, basedBillingRequest.getIdentification());
		request.addInputParam("@i_nombre", ICTSTypes.SQLVARCHAR, basedBillingRequest.getName());
		request.addInputParam("@i_convenio", ICTSTypes.SQLINT4, basedBillingRequest.getContractId().toString());
		if (logger.isDebugEnabled()) {
			logger.logDebug("Request: " + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Start Request");
		}
		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response: " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Finalize Response");
		}

		return transforToBasedBillingResponse(pResponse);
	}

	public BasedBillingResponse transforToBasedBillingResponse(IProcedureResponse response) {

		BasedBillingResponse basedBillingResponse = new BasedBillingResponse();
		BasedBilling basedBilling = null;
		List<BasedBilling> basedBillingList = new ArrayList<BasedBilling>();
		if (logger.isDebugEnabled()) {
			logger.logDebug("ProcedureResponse: " + response.getProcedureResponseAsString());
            logger.logDebug("Codigo ejecucion que devuelve la consulta " + response.getReturnCode());
		}
		
		if (response.getReturnCode() == 0) {

			IResultSetRow[] rowQueryBaseBilling = response.getResultSet(1).getData().getRowsAsArray();
			for (IResultSetRow iResultSetRow : rowQueryBaseBilling) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				basedBilling = new BasedBilling();
				basedBilling.setIdentification(columns[0].getValue());
				basedBilling.setDebtorName(columns[1].getValue());
				basedBilling.setReference1(columns[2].getValue());
				basedBilling.setReference2(columns[3].getValue());
				basedBilling.setReference3(columns[4].getValue());
				basedBilling.setAmount(new BigDecimal(columns[5].getValue()));
				basedBilling.setPaymentDay(columns[6].getValue());
				basedBilling.setSequential(Integer.parseInt(columns[7].getValue()));
				basedBillingList.add(basedBilling);
			}

			basedBillingResponse.setBasedBillingInformation(basedBillingList);
			basedBillingResponse.setSuccess(true);
			basedBillingResponse.setReturnCode(response.getReturnCode());
		} else {			
			Message[] messages = Utils.returnArrayMessage(response);
			basedBillingResponse.setReturnCode(response.getReturnCode());
			basedBillingResponse.setMessages(messages);
			basedBillingResponse.setSuccess(false);
		}
		return basedBillingResponse;
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
}
