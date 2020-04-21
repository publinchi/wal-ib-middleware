package com.cobiscorp.ecobis.orchestration.core.ib.loans;

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
import com.cobiscorp.ecobis.ib.application.dtos.LoanAmortizationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.LoanAmortizationResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.LoanAmortization;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceLoanAmortizationQuery;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;

@Component(name = "LoanAmortizationQuery", immediate = false)
@Service(value = { ICoreServiceLoanAmortizationQuery.class })
@Properties(value = { @Property(name = "service.description", value = "LoanAmortizationQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "LoanAmortizationQuery") })

public class LoanAmortizationQuery extends SPJavaOrchestrationBase implements ICoreServiceLoanAmortizationQuery {
	private static final String COBIS_CONTEXT = "COBIS";
	private static ILogger logger = LogFactory.getLogger(LoanAmortizationQuery.class);
	private static final int COL_OPERATION_NUMBER = 0;
	private static final int COL_DIVIDEND = 1;
	private static final int COL_DATE = 2;
	private static final int COL_CAPITAL = 3;
	private static final int COL_INTEREST = 4;
	private static final int COL_MORA = 5;
	private static final int COL_TAX = 6;
	private static final int COL_ASSURED = 7;
	private static final int COL_OTHER = 8;
	private static final int COL_CAPITAL_AMOUNT = 9;
	private static final int ADJUSTMENT = 10;
	private static final int STATE = 11;
	private static final int PAYMENT = 13;

	/**
	 * M&eacute;todo GetLoanAmortization En este m&eacute;todo obtenemos la
	 * Amortizaci&oacute;n del Pr&eacute;stamo, enviamos un objeto de tipo
	 * LoanAmortizationRequest y obtenemos de respuesta un objeto de tipo
	 * LoanAmortizationResponse, para m&aacute;s detalle de los objetos, revisar
	 * las siguientes referencias:
	 * 
	 * @see LoanAmortizationRequest
	 * @see LoanAmortizationResponse
	 */

	@Override
	public LoanAmortizationResponse GetLoanAmortization(LoanAmortizationRequest aLoanAmortizationReques)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: GetLoanAmortization");
			logger.logInfo("RESPUESTA DUMMY COBIS GENERADA");
		}

		IProcedureRequest request = new ProcedureRequestAS();

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "7056");// request.readValueParam("@t_trn"));

		request.setSpName("cob_cartera..sp_imp_tabla_amort");
		request.addInputParam("@t_trn", ICTSTypes.SYBINT4, "7056"); // request.readValueParam("@t_trn"));
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR,
				aLoanAmortizationReques.getCodeTransactionalIdentifier());
		request.addInputParam("@i_banco", ICTSTypes.SQLVARCHAR,
				aLoanAmortizationReques.getProduct().getProductNumber());
		request.addInputParam("@i_formato_fecha", ICTSTypes.SQLINT4,
				aLoanAmortizationReques.getDateFormatId().toString());
		request.addInputParam("@i_dividendo", ICTSTypes.SQLINT2, aLoanAmortizationReques.getSequential().toString());

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request: " + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("Start Request");
		}

		/* Ejecuta y obtiene la respuesta */
		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Amortizacion: " + pResponse.getProcedureResponseAsString());
		}

		LoanAmortizationResponse loanAmortizationResponse = transformToLoanAmortizationResponse(pResponse);
		return loanAmortizationResponse;
	}

	private LoanAmortizationResponse transformToLoanAmortizationResponse(IProcedureResponse aProcedureResponse) {
		LoanAmortizationResponse LoanAmortizationResp = new LoanAmortizationResponse();
		List<LoanAmortization> aloanAmortizationCollection = new ArrayList<LoanAmortization>();
		LoanAmortization aLoanAmortization = null;
		Product product = null;
		if (logger.isDebugEnabled()) {
			logger.logDebug("ProcedureResponse: " + aProcedureResponse.getProcedureResponseAsString());
		}

		if (aProcedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsLoanAmortization = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();
			for (IResultSetRow iResultSetRow : rowsLoanAmortization) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				aLoanAmortization = new LoanAmortization();
				product = new Product();
				product.setProductNumber(columns[COL_OPERATION_NUMBER].getValue());
				aLoanAmortization.setOperationNumber(product);
				aLoanAmortization.setDividend(Integer.parseInt(columns[COL_DIVIDEND].getValue()));
				aLoanAmortization.setDate(columns[COL_DATE].getValue());
				aLoanAmortization.setCapital(Double.parseDouble(columns[COL_CAPITAL].getValue()));
				aLoanAmortization.setInterest(Double.parseDouble(columns[COL_INTEREST].getValue()));
				aLoanAmortization.setMora(Double.parseDouble(columns[COL_MORA].getValue()));
				aLoanAmortization.setTax(Double.parseDouble(columns[COL_TAX].getValue()));
				aLoanAmortization.setInsurance(Double.parseDouble(columns[COL_ASSURED].getValue()));
				aLoanAmortization.setOthers(Double.parseDouble(columns[COL_OTHER].getValue()));
				aLoanAmortization.setCapitalAmount(Double.parseDouble(columns[COL_CAPITAL_AMOUNT].getValue()));
				aLoanAmortization.setAdjustment(Double.parseDouble(columns[ADJUSTMENT].getValue()));
				aLoanAmortization.setState(columns[STATE].getValue());
				aLoanAmortization.setPayment(Double.parseDouble(columns[PAYMENT].getValue()));
				aloanAmortizationCollection.add(aLoanAmortization);
			}
			LoanAmortizationResp.setLoanAmortizationCollection(aloanAmortizationCollection);
		} else {
			LoanAmortizationResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		}
		LoanAmortizationResp.setReturnCode(aProcedureResponse.getReturnCode());
		return LoanAmortizationResp;
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
