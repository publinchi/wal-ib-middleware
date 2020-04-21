/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.programmed.savings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
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
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsAccountRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsExpirationDateRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsExpirationDateResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsMinimumAmountRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsMinimumAmountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ProgrammedSavingsResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ProgrammedSavings;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ProgrammedSavingsAccount;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceProgrammedSavings;

/**
 * @author mvelez
 *
 */

@Component(name = "ProgrammedSavingsQuery", immediate = false)
@Service(value = { ICoreServiceProgrammedSavings.class })
@Properties(value = { @Property(name = "service.description", value = "ProgrammedSavingsQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ProgrammedSavingsQuery") })

public class ProgrammedSavingsQuery extends SPJavaOrchestrationBase implements ICoreServiceProgrammedSavings {

	private static ILogger logger = LogFactory.getLogger(ProgrammedSavingsQuery.class);
	private static final String SP_PROGRAMMED_SAVINGS = "cob_bvirtual..sp_tr4_ahorro_programado";

	/*** Return pProgrammedSavings ***/
	private static final int COL_SEQUENTIAL = 0;
	private static final int COL_SAVING_TIME = 1;
	private static final int COL_PAYMENT_DATE = 2;
	private static final int COL_AMOUNT = 3;
	private static final int COL_CURRENCY = 4;
	private static final int COL_EXECUTED = 5;

	/*** Return pProgrammedSavingsAccount ***/
	private static final int COL_ACCOUNT = 1;
	private static final int COL_CURRENCY_ID = 2;
	private static final int COL_CLIENT_NAME = 3;
	private static final int COL_PRODUCT_BALANCE = 4;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceProgrammedSavings#getProgrammedSavings(com.cobiscorp.ecobis.
	 * ib.application.dtos.ProgrammedSavingsRequest)
	 ***********************************************************************************************************************************************************/
	@Override
	public ProgrammedSavingsResponse getProgrammedSavings(ProgrammedSavingsRequest aProgrammedSavingsRequest)
			throws CTSServiceException, CTSInfrastructureException {

		if (logger.isInfoEnabled()) {
			logger.logInfo("Iniciando Servicio DUMMY ACOPLADO CORE COBIS: getProgrammedSavings");
		}
		IProcedureResponse pResponse = Execution1(SP_PROGRAMMED_SAVINGS, aProgrammedSavingsRequest);
		ProgrammedSavingsResponse programmedSavingsResponse = transformToProgrammedSavingsResponse(pResponse);
		return programmedSavingsResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceProgrammedSavings#ProgrammedSavingsAccount(com.cobiscorp.
	 * ecobis.ib.application.dtos.ProgrammedSavingsAccountRequest)
	 ***********************************************************************************************************************************************************/
	@Override
	public ProgrammedSavingsAccountResponse ProgrammedSavingsAccount(
			ProgrammedSavingsAccountRequest aProgrammedSavingsAccountRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("Iniciando Servicio DUMMY ACOPLADO CORE COBIS: ProgrammedSavingsAccount");
		}
		IProcedureResponse pResponse = Execution2(SP_PROGRAMMED_SAVINGS, aProgrammedSavingsAccountRequest);
		ProgrammedSavingsAccountResponse programmedSavingsAccountResponse = transformToProgrammedSavingsAccountResponse(
				pResponse);
		return programmedSavingsAccountResponse;
	}

	/*
	 * Execute Stored Procedure and Return IProcedureResponse
	 ***********************************************************************************************************************************************************/
	private IProcedureResponse Execution1(String SpName, ProgrammedSavingsRequest aProgrammedSavingsRequest)
			throws CTSServiceException, CTSInfrastructureException {
		;
		IProcedureRequest request = new ProcedureRequestAS();
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);

		request.setSpName(SpName);
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR,
				aProgrammedSavingsRequest.getCodeTransactionalIdentifier());
		request.addInputParam("@i_cta_ahoprog", ICTSTypes.SQLINT1,
				aProgrammedSavingsRequest.getProductNumber().getProductNumber());

		if (logger.isDebugEnabled()) {
			logger.logDebug("<<<Request: >>>" + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("<<<Start Request>>>");
		}

		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("<<<Response: >>>" + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("<<<Finalize Response>>>");
		}
		return pResponse;
	}

	/*
	 * Execute Stored Procedure and Return IProcedureResponse
	 ***********************************************************************************************************************************************************/
	private IProcedureResponse Execution2(String SpName,
			ProgrammedSavingsAccountRequest aProgrammedSavingsAccountRequest)
			throws CTSServiceException, CTSInfrastructureException {
		;
		IProcedureRequest request = new ProcedureRequestAS();
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);

		request.setSpName(SpName);
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR,
				aProgrammedSavingsAccountRequest.getCodeTransactionalIdentifier());
		request.addInputParam("@i_cliente", ICTSTypes.SQLINT1,
				aProgrammedSavingsAccountRequest.getClient().getIdCustomer());
		request.addInputParam("@i_mon", ICTSTypes.SQLINT2, aProgrammedSavingsAccountRequest.getMode().toString());

		if (logger.isDebugEnabled()) {
			logger.logDebug("<<<Request: >>>" + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("<<<Start Request>>>");
		}

		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("<<<Response: >>>" + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("<<<Finalize Response>>>");
		}
		return pResponse;
	}

	/*
	 * 
	 ***********************************************************************************************************************************************************/
	private ProgrammedSavingsResponse transformToProgrammedSavingsResponse(IProcedureResponse aProcedureResponse) {
		ProgrammedSavingsResponse ProgrammedSavingsResp = new ProgrammedSavingsResponse();
		List<ProgrammedSavings> programmedSavingsCollection = new ArrayList<ProgrammedSavings>();

		ProgrammedSavings pProgrammedSavings = null;

		if (logger.isDebugEnabled()) {
			logger.logDebug("<<<ProcedureResponse: >>>" + aProcedureResponse.getProcedureResponseAsString());
		}
		IResultSetRow[] rowsProgrammedSavings = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

		pProgrammedSavings = new ProgrammedSavings();
		Currency currency = new Currency();

		for (IResultSetRow iResultSetRow : rowsProgrammedSavings) {
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
			currency.setCurrencyId(Integer.parseInt(columns[COL_CURRENCY].getValue()));
			pProgrammedSavings.setSequential(Integer.parseInt(columns[COL_SEQUENTIAL].getValue()));
			pProgrammedSavings.setSavingsTime(columns[COL_SAVING_TIME].getValue());
			pProgrammedSavings.setPaymentDate(columns[COL_PAYMENT_DATE].getValue());
			pProgrammedSavings.setAmount(Double.parseDouble(columns[COL_AMOUNT].getValue()));
			pProgrammedSavings.setCurrency(currency);
			pProgrammedSavings.setExecuted(columns[COL_EXECUTED].getValue());

			programmedSavingsCollection.add(pProgrammedSavings);
		}
		ProgrammedSavingsResp.setProgrammendSavingsCollection(programmedSavingsCollection);

		return ProgrammedSavingsResp;
	}

	/*
	 * 
	 ***********************************************************************************************************************************************************/
	private ProgrammedSavingsAccountResponse transformToProgrammedSavingsAccountResponse(
			IProcedureResponse aProcedureResponse) {
		ProgrammedSavingsAccountResponse ProgrammedSavingsAccountResp = new ProgrammedSavingsAccountResponse();
		List<ProgrammedSavingsAccount> programmedSavingsAccountCollection = new ArrayList<ProgrammedSavingsAccount>();

		ProgrammedSavingsAccount pProgrammedSavingsAccount = null;

		if (logger.isDebugEnabled()) {
			logger.logDebug("<<<ProcedureResponse: >>>" + aProcedureResponse.getProcedureResponseAsString());
		}
		IResultSetRow[] rowsProgrammedSavings = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

		pProgrammedSavingsAccount = new ProgrammedSavingsAccount();
		Currency currency = new Currency();
		Client client = new Client();

		for (IResultSetRow iResultSetRow : rowsProgrammedSavings) {
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
			currency.setCurrencyId(Integer.parseInt(columns[COL_CURRENCY_ID].getValue()));
			client.setCompleteName(columns[COL_CLIENT_NAME].getValue());

			pProgrammedSavingsAccount.setAccount(columns[COL_ACCOUNT].getValue());
			pProgrammedSavingsAccount.setCurrencyId(currency);
			pProgrammedSavingsAccount.setClient(client);
			pProgrammedSavingsAccount.setProductBalance(Double.parseDouble(columns[COL_PRODUCT_BALANCE].getValue()));

			programmedSavingsAccountCollection.add(pProgrammedSavingsAccount);
		}
		ProgrammedSavingsAccountResp.setProgrammedSavingsAccountCollection(programmedSavingsAccountCollection);

		return ProgrammedSavingsAccountResp;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		return null;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		return null;
	}

	@Override
	public ProgrammedSavingsExpirationDateResponse getExpirationDate(
			ProgrammedSavingsExpirationDateRequest aProgrammedSavingsExpirationDateRequest)
			throws CTSServiceException, CTSInfrastructureException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProgrammedSavingsMinimumAmountResponse getMinimunAmount(
			ProgrammedSavingsMinimumAmountRequest aProgrammedSavingsMinimumAmountRequest)
			throws CTSServiceException, CTSInfrastructureException {
		// TODO Auto-generated method stub
		return null;
	}

}
