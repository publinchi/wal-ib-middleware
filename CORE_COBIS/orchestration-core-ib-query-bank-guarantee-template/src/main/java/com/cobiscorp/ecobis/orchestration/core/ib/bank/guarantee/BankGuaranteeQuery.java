package com.cobiscorp.ecobis.orchestration.core.ib.bank.guarantee;

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
import com.cobiscorp.ecobis.ib.application.dtos.BankGuaranteeRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BankGuaranteeResponse;
import com.cobiscorp.ecobis.ib.application.dtos.QueryBankGuaranteeRequest;
import com.cobiscorp.ecobis.ib.application.dtos.QueryBankGuaranteeResponse;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceQueryBankGuarantee;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.GRB;

@Component(name = "BankGuaranteeQuery", immediate = false)
@Service(value = { ICoreServiceQueryBankGuarantee.class })
@Properties(value = { @Property(name = "service.description", value = "BankGuaranteeQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "BankGuaranteeQuery") })
public class BankGuaranteeQuery extends SPJavaOrchestrationBase implements ICoreServiceQueryBankGuarantee {
	private static ILogger logger = LogFactory.getLogger(BankGuaranteeQuery.class);
	private static final String SP_NAME = "cobis..sp_bv_grb";

	protected static final String ORIGEN_IB = "IB";
	protected static final String ORIGEN_ADMIN = "AD";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCreditLine
	 * #getLines(com.cobiscorp.ecobis.ib.application.dtos.CreditLineRequest)
	 * *****
	 * *********************************************************************
	 * *******************************************************************
	 */
	@Override
	public QueryBankGuaranteeResponse getBankGuarantees(QueryBankGuaranteeRequest aQueryBankGuaranteeReq)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getBankGuarantees");
		}

		IProcedureResponse pResponse = Execution(SP_NAME, aQueryBankGuaranteeReq);
		QueryBankGuaranteeResponse bankGuaranteeResponse = transformToQueryBankGuaranteeResponse(pResponse);
		return bankGuaranteeResponse;
	}

	private IProcedureResponse Execution(String SpName, QueryBankGuaranteeRequest aQueryBankGuaranteeReq)
			throws CTSServiceException, CTSInfrastructureException {
		;
		IProcedureRequest request = initProcedureRequest(aQueryBankGuaranteeReq.getOriginalRequest());

		// IProcedureRequest request = new ProcedureRequestAS();
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1801049");

		request.setSpName(SpName);

		request.addInputParam("@i_cliente", ICTSTypes.SYBINT4, aQueryBankGuaranteeReq.getEntity().getEnte().toString());
		request.addInputParam("@i_operacion", ICTSTypes.SYBCHAR, aQueryBankGuaranteeReq.getOperation());

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

	private QueryBankGuaranteeResponse transformToQueryBankGuaranteeResponse(IProcedureResponse aProcedureResponse) {
		QueryBankGuaranteeResponse aQueryBankGuaranteeResp = new QueryBankGuaranteeResponse();
		List<GRB> bankGuaranteeCollection = new ArrayList<GRB>();
		GRB aGRB = null;

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + aProcedureResponse.getProcedureResponseAsString());
		}

		if (aProcedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsLines = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

			for (IResultSetRow iResultSetRow : rowsLines) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				aGRB = new GRB();
				aGRB.setId(Integer.parseInt(columns[0].getValue()));
				aGRB.setOperation(columns[1].getValue());
				aGRB.setName(columns[2].getValue());
				aGRB.setLaunchingdate(columns[3].getValue());
				aGRB.setExpirationdate(columns[4].getValue());
				aGRB.setCurrency(columns[5].getValue());
				aGRB.setCurrencyCode(Integer.parseInt(columns[6].getValue()));
				aGRB.setAmount(columns[7].getValue());

				bankGuaranteeCollection.add(aGRB);
			}
			aQueryBankGuaranteeResp.setBankGuaranteeCollection(bankGuaranteeCollection);
		} else {
			aQueryBankGuaranteeResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		}

		aQueryBankGuaranteeResp.setReturnCode(aProcedureResponse.getReturnCode());

		return aQueryBankGuaranteeResp;
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
