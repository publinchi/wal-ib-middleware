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
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ProgrammedSavings;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ProgrammedSavingsAccount;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceProgrammedSavings;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;

@Component(name = "ProgrammedSavingsQuery", immediate = false)
@Service(value = { ICoreServiceProgrammedSavings.class })
@Properties(value = { @Property(name = "service.description", value = "ProgrammedSavingsQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ProgrammedSavingsQuery") })

public class ProgrammedSavingsQuery extends SPJavaOrchestrationBase implements ICoreServiceProgrammedSavings {

	private static ILogger logger = LogFactory.getLogger(ProgrammedSavingsQuery.class);
	private static final String SP_PROGRAMMED_SAVINGS = "cobis..sp_tr4_ahorro_programado";
	private static final int COL_SEQUENTIAL = 0;
	private static final int COL_SAVING_TIME = 1;
	private static final int COL_PAYMENT_DATE = 2;
	private static final int COL_AMOUNT = 3;
	private static final int COL_CURRENCY = 4;
	private static final int COL_EXECUTED = 5;
	private static final int COL_ACCOUNT = 0;
	private static final int COL_CURRENCY_ID = 1;
	private static final int COL_CLIENT_NAME = 2;
	private static final int COL_PRODUCT_BALANCE = 3;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceProgrammedSavings#getProgrammedSavings(com.cobiscorp.ecobis.
	 * ib.application.dtos.ProgrammedSavingsRequest)
	 ***********************************************************************************************************************************************************/
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {

	}

	/**
	 * M&eacute;todo getProgrammedSavings En este m&eacute;todo obtenemos el
	 * ahorro programado, enviamos un objeto de tipo ProgrammedSavingsRequest y
	 * obtenemos de respuesta un objeto de tipo ProgrammedSavingsResponse, para
	 * m&aacute;s detalle de los objetos, revisar las siguientes referencias:
	 * 
	 * @see ProgrammedSavingsRequest
	 * @see ProgrammedSavingsResponse
	 */
	@Override
	public ProgrammedSavingsResponse getProgrammedSavings(ProgrammedSavingsRequest aProgrammedSavingsRequest)
			throws CTSServiceException, CTSInfrastructureException {		
		
		IProcedureResponse pResponse = GetProgrammingSavingProcess(SP_PROGRAMMED_SAVINGS, aProgrammedSavingsRequest);
		ProgrammedSavingsResponse programmedSavingsResponse = transformToProgrammedSavingsResponse(pResponse);
		return programmedSavingsResponse;
	}

	/**
	 * M&eacute;todo ProgrammedSavings En este m&eacute;todo obtenemos el ahorro
	 * programado, enviamos un objeto de tipo ProgrammedSavingsAccountRequest y
	 * obtenemos de respuesta un objeto de tipo ProgrammedSavingsResponse, para
	 * m&aacute;s detalle de los objetos, revisar las siguientes referencias:
	 * 
	 * @see ProgrammedSavingsRequest
	 * @see ProgrammedSavingsResponse
	 */
	@Override
	public ProgrammedSavingsAccountResponse ProgrammedSavingsAccount(
			ProgrammedSavingsAccountRequest aProgrammedSavingsAccountRequest)
			throws CTSServiceException, CTSInfrastructureException {		
		IProcedureResponse pResponse = ProgrammedSavingsAcountProcess(SP_PROGRAMMED_SAVINGS,
				aProgrammedSavingsAccountRequest);
		ProgrammedSavingsAccountResponse programmedSavingsAccountResponse = transformToProgrammedSavingsAccountResponse(
				pResponse);
		return programmedSavingsAccountResponse;
	}

	/**
	 * M&eacute;todo getExpirationDate En este m&eacute;todo obtenemos la fecha
	 * de expiraci&oacute;n, enviamos un objeto de tipo
	 * ProgrammedSavingsExpirationDateRequest y obtenemos de respuesta un objeto
	 * de tipo ProgrammedSavingsExpirationDateResponse, para m&aacute;s detalle
	 * de los objetos, revisar las siguientes referencias:
	 * 
	 * @see ProgrammedSavingsExpirationDateRequest
	 * @see ProgrammedSavingsExpirationDateResponse
	 */
	@Override
	public ProgrammedSavingsExpirationDateResponse getExpirationDate(
			ProgrammedSavingsExpirationDateRequest aProgrammedSavingsExpirationDateRequest)
			throws CTSServiceException, CTSInfrastructureException {
		IProcedureResponse pResponse = GetExpirationDateProcess(SP_PROGRAMMED_SAVINGS,
				aProgrammedSavingsExpirationDateRequest);
		ProgrammedSavingsExpirationDateResponse aProgrammedSavingsExpirationDateResponse = transformToProgrammedSavingsExpirationDateResponse(
				pResponse);
		return aProgrammedSavingsExpirationDateResponse;
	}

	/**
	 * M&eacute;todo getMinimunAmount En este m&eacute;todo obtenemos el monto
	 * mínimo, enviamos un objeto de tipo ProgrammedSavingsMinimumAmountRequest
	 * y obtenemos de respuesta un objeto de tipo
	 * ProgrammedSavingsMinimumAmountResponse, para m&aacute;s detalle de los
	 * objetos, revisar las siguientes referencias:
	 * 
	 * @see ProgrammedSavingsMinimumAmountRequest
	 * @see ProgrammedSavingsMinimumAmountResponse
	 */
	@Override
	public ProgrammedSavingsMinimumAmountResponse getMinimunAmount(
			ProgrammedSavingsMinimumAmountRequest aProgrammedSavingsMinimumAmountRequest)
			throws CTSServiceException, CTSInfrastructureException {
		IProcedureResponse pResponse = GetMinimumAmountProcess(SP_PROGRAMMED_SAVINGS,
				aProgrammedSavingsMinimumAmountRequest);
		ProgrammedSavingsMinimumAmountResponse aProgrammedSavingsMinimumAmountResponse = transformToProgrammedSavingsMinimumAmountResponse(
				pResponse);
		return aProgrammedSavingsMinimumAmountResponse;
	}

	private IProcedureResponse GetProgrammingSavingProcess(String SpName,
			ProgrammedSavingsRequest aProgrammedSavingsRequest) throws CTSServiceException, CTSInfrastructureException {
		;
		IProcedureRequest request = initProcedureRequest(aProgrammedSavingsRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1875017");
		request.setSpName(SpName);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1875017");
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR,
				aProgrammedSavingsRequest.getCodeTransactionalIdentifier());
		request.addInputParam("@i_cta_ahoprog", ICTSTypes.SQLVARCHAR,
				aProgrammedSavingsRequest.getProductNumber().getProductNumber());
		request.addOutputParam("@o_descripcion_moneda", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		request.addOutputParam("@o_nombre_cta", ICTSTypes.SQLVARCHAR,
				"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		request.addOutputParam("@o_tipo_cta", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
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

	private IProcedureResponse ProgrammedSavingsAcountProcess(String SpName,
			ProgrammedSavingsAccountRequest aProgrammedSavingsAccountRequest)
			throws CTSServiceException, CTSInfrastructureException {
		;
		IProcedureRequest request = initProcedureRequest(aProgrammedSavingsAccountRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1875017");
		request.setSpName(SpName);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1875017");
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR,
				aProgrammedSavingsAccountRequest.getCodeTransactionalIdentifier());
		request.addInputParam("@i_cliente", ICTSTypes.SQLINT4,
				aProgrammedSavingsAccountRequest.getUser().getEntityId().toString());
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

	private IProcedureResponse GetExpirationDateProcess(String SpName,
			ProgrammedSavingsExpirationDateRequest aProgrammedSavingsExpirationDateRequest)
			throws CTSServiceException, CTSInfrastructureException {
		;
		IProcedureRequest request = initProcedureRequest(aProgrammedSavingsExpirationDateRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1801019");
		request.setSpName(SpName);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1801019");
		request.addInputParam("@i_fecha_ini", ICTSTypes.SQLVARCHAR,
				aProgrammedSavingsExpirationDateRequest.getInitialDate());
		request.addInputParam("@i_plazo", ICTSTypes.SQLVARCHAR, aProgrammedSavingsExpirationDateRequest.getTerm());
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR,
				aProgrammedSavingsExpirationDateRequest.getCodeTransactionalIdentifier());
		request.addOutputParam("@o_fecha_ven", ICTSTypes.SQLVARCHAR, "XXXXXXXXXX");
		if (logger.isDebugEnabled()) {
			logger.logDebug("<<<Request JBA: >>>" + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("<<<Start Request JBA>>>");
		}
		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("<<<Response JBA: >>>" + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("<<<Finalize Response JBA>>>");
		}
		return pResponse;
	}

	private IProcedureResponse GetMinimumAmountProcess(String SpName,
			ProgrammedSavingsMinimumAmountRequest aProgrammedSavingsMinimumAmountRequest)
			throws CTSServiceException, CTSInfrastructureException {
		;
		IProcedureRequest request = initProcedureRequest(aProgrammedSavingsMinimumAmountRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1801019");
		request.setSpName(SpName);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1801019");
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR,
				aProgrammedSavingsMinimumAmountRequest.getCodeTransactionalIdentifier());
		request.addOutputParam("@o_monto_min", ICTSTypes.SQLMONEY4, "0");
		if (logger.isDebugEnabled()) {
			logger.logDebug("<<<Request JBA: >>>" + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("<<<Start Request JBA>>>");
		}
		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("<<<Response JBA: >>>" + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("<<<Finalize Response JBA>>>");
		}
		return pResponse;
	}

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
			currency.setCurrencyNemonic(columns[COL_CURRENCY].getValue());
			pProgrammedSavings.setSequential(Integer.parseInt(columns[COL_SEQUENTIAL].getValue()));
			pProgrammedSavings.setSavingsTime(columns[COL_SAVING_TIME].getValue());
			pProgrammedSavings.setPaymentDate(columns[COL_PAYMENT_DATE].getValue());
			pProgrammedSavings.setAmount(Double.parseDouble(columns[COL_AMOUNT].getValue()));
			pProgrammedSavings.setCurrency(currency);
			pProgrammedSavings.setExecuted(columns[COL_EXECUTED].getValue());
			programmedSavingsCollection.add(pProgrammedSavings);
		}
		ProgrammedSavingsResp.setProgrammendSavingsCollection(programmedSavingsCollection);
		ProgrammedSavingsResp.setCurrencyDescription(aProcedureResponse.readValueParam("@o_descripcion_moneda"));
		ProgrammedSavingsResp.setAccountName(aProcedureResponse.readValueParam("@o_nombre_cta"));
		ProgrammedSavingsResp.setAccountType(aProcedureResponse.readValueParam("@o_tipo_cta"));
		Message[] message = Utils.returnArrayMessage(aProcedureResponse);
		ProgrammedSavingsResp.setMessages(message);
		ProgrammedSavingsResp.setReturnCode(aProcedureResponse.getReturnCode());
		return ProgrammedSavingsResp;
	}

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
		Message[] message = Utils.returnArrayMessage(aProcedureResponse);
		ProgrammedSavingsAccountResp.setMessages(message);
		ProgrammedSavingsAccountResp.setReturnCode(aProcedureResponse.getReturnCode());
		return ProgrammedSavingsAccountResp;
	}

	private ProgrammedSavingsExpirationDateResponse transformToProgrammedSavingsExpirationDateResponse(
			IProcedureResponse aProcedureResponse) {
		ProgrammedSavingsExpirationDateResponse aProgrammedSavingsExpirationDateResponse = new ProgrammedSavingsExpirationDateResponse();
		aProgrammedSavingsExpirationDateResponse.setExpirationDates(aProcedureResponse.readValueParam("@o_fecha_ven"));
		Message[] message = Utils.returnArrayMessage(aProcedureResponse);
		aProgrammedSavingsExpirationDateResponse.setMessages(message);
		aProgrammedSavingsExpirationDateResponse.setReturnCode(aProcedureResponse.getReturnCode());
		return aProgrammedSavingsExpirationDateResponse;
	}

	private ProgrammedSavingsMinimumAmountResponse transformToProgrammedSavingsMinimumAmountResponse(
			IProcedureResponse aProcedureResponse) {
		ProgrammedSavingsMinimumAmountResponse aProgrammedSavingsMinimumAmountResponse = new ProgrammedSavingsMinimumAmountResponse();
		aProgrammedSavingsMinimumAmountResponse
				.setMinimumAmount(new Double(aProcedureResponse.readValueParam("@o_monto_min")));
		Message[] message = Utils.returnArrayMessage(aProcedureResponse);
		aProgrammedSavingsMinimumAmountResponse.setMessages(message);
		aProgrammedSavingsMinimumAmountResponse.setReturnCode(aProcedureResponse.getReturnCode());
		return aProgrammedSavingsMinimumAmountResponse;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		return null;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		return null;
	}
}
