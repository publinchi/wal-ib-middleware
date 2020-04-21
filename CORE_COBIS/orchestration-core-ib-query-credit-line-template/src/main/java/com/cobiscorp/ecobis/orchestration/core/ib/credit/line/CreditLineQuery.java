package com.cobiscorp.ecobis.orchestration.core.ib.credit.line;

import java.math.BigDecimal;
import java.math.BigInteger;
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
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCreditLine;
import com.cobiscorp.ecobis.ib.application.dtos.CreditLineRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CreditLineResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CreditLine;

@Component(name = "CreditLineQuery", immediate = false)
@Service(value = { ICoreServiceCreditLine.class })
@Properties(value = { @Property(name = "service.description", value = "CreditLineQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CreditLineQuery") })
public class CreditLineQuery extends SPJavaOrchestrationBase implements ICoreServiceCreditLine {
	private static ILogger logger = LogFactory.getLogger(CreditLineQuery.class);
	private static final String SP_NAME = "cobis..sp_bv_linea";

	/**** Output: getLines *****/
	private static final int COL_CODE = 0;
	private static final int COL_LINE = 1;
	private static final int COL_MONEY = 2;
	private static final int COL_AVAILABLE = 3;
	private static final int COL_CURRENCY = 4;
	private static final int COL_OPENINGDATE = 5;
	private static final int COL_EXPIRATIONDATE = 6;
	private static final int COL_AMOUNT = 7;
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
	public CreditLineResponse getLines(CreditLineRequest aCreditLineRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getLines");
		}

		IProcedureResponse pResponse = Execution(SP_NAME, aCreditLineRequest);
		CreditLineResponse creditLineResponse = transformToCreditLineResponse(pResponse,
				aCreditLineRequest.getOrigin());
		return creditLineResponse;
	}

	private IProcedureResponse Execution(String SpName, CreditLineRequest aCreditLineRequest)
			throws CTSServiceException, CTSInfrastructureException {
		;
		IProcedureRequest request = initProcedureRequest(aCreditLineRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800035");

		request.setSpName(SpName);

		request.addInputParam("@i_cliente", ICTSTypes.SYBINT4, aCreditLineRequest.getEntity().getEnte().toString());
		request.addInputParam("@i_origen", ICTSTypes.SYBCHAR, aCreditLineRequest.getOrigin());

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

	private CreditLineResponse transformToCreditLineResponse(IProcedureResponse aProcedureResponse, String origen) {
		CreditLineResponse CreditLineResp = new CreditLineResponse();
		List<CreditLine> lineCollection = new ArrayList<CreditLine>();
		CreditLine aCreditLine = null;

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + aProcedureResponse.getProcedureResponseAsString());
		}

		if (aProcedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsLines = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

			for (IResultSetRow iResultSetRow : rowsLines) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				aCreditLine = new CreditLine();
				aCreditLine.setCode(Integer.parseInt(columns[COL_CODE].getValue()));
				aCreditLine.setCredit(columns[COL_LINE].getValue());
				aCreditLine.setMoney(columns[COL_MONEY].getValue());
				if (origen.equals(ORIGEN_IB)) {
					aCreditLine.setAvailable(new BigDecimal(columns[COL_AVAILABLE].getValue()));
					aCreditLine.setCurrency(Integer.parseInt(columns[COL_CURRENCY].getValue()));

					aCreditLine.setOpeningDate(columns[COL_OPENINGDATE].getValue());
					aCreditLine.setExpirationDate(columns[COL_EXPIRATIONDATE].getValue());
					aCreditLine.setAmount(new BigDecimal(columns[COL_AMOUNT].getValue()));

				} else {
					aCreditLine.setCurrency(Integer.parseInt(columns[3].getValue()));
				}
				lineCollection.add(aCreditLine);
			}
			CreditLineResp.setCreditLineCollection(lineCollection);
		} else {

			CreditLineResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		}

		CreditLineResp.setReturnCode(aProcedureResponse.getReturnCode());
		return CreditLineResp;
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
