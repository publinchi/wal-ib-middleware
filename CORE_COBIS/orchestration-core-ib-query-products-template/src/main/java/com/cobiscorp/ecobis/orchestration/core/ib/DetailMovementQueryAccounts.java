/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobisv.commons.context.CobisSession;
import com.cobiscorp.cobisv.commons.context.Context;
import com.cobiscorp.cobisv.commons.context.ContextManager;
import com.cobiscorp.ecobis.ib.application.dtos.DetailsMovementsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.DetailsMovementsResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountStatement;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceDetailsMovementsQuery;

@Component(name = "DetailMovementQueryAccounts", immediate = false)
@Service(value = { ICoreServiceDetailsMovementsQuery.class })
@Properties(value = { @Property(name = "service.description", value = "DetailMovementQueryAccounts"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "DetailMovementQueryAccounts") })
public class DetailMovementQueryAccounts extends SPJavaOrchestrationBase implements ICoreServiceDetailsMovementsQuery {

	/**
	 * Instance logger component
	 */
	private static ILogger logger = LogFactory.getLogger(DetailMovementQueryAccounts.class);

	public IProcedureResponse getMovementsDetailSavingAccount(IProcedureRequest procedureRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: getMovementsDetailSavingAccount");
			logger.logInfo("RESPUESTA DUMMY GENERADA");
		}

		IProcedureResponse procedureResponse = SpUtilitario.crearProcedureResponse(procedureRequest);
		SpUtilitario.crearResultSet(procedureResponse);

		if (logger.isInfoEnabled())
			logger.logInfo("Antes de crearColumna");

		SpUtilitario.crearColumna(procedureResponse, 1, "NUMBER_ACCOUNT", ICTSTypes.SQLVARCHAR, 24);
		SpUtilitario.crearColumna(procedureResponse, 1, "DATE", ICTSTypes.SQLCHAR, 10);
		SpUtilitario.crearColumna(procedureResponse, 1, "HOUR", ICTSTypes.SQLCHAR, 8);
		SpUtilitario.crearColumna(procedureResponse, 1, "DESCRIPTION", ICTSTypes.SQLVARCHAR, 255);
		SpUtilitario.crearColumna(procedureResponse, 1, "CONCEPT", ICTSTypes.SQLVARCHAR, 64);
		SpUtilitario.crearColumna(procedureResponse, 1, "CAUSE", ICTSTypes.SQLVARCHAR, 255);
		SpUtilitario.crearColumna(procedureResponse, 1, "AMMOUNT", ICTSTypes.SQLMONEY, 21);
		SpUtilitario.crearColumna(procedureResponse, 1, "DOCUMENT_NUMBER", ICTSTypes.SQLINT4, 11);
		SpUtilitario.crearColumna(procedureResponse, 1, "TYPEDC", ICTSTypes.SQLVARCHAR, 255);
		SpUtilitario.crearColumna(procedureResponse, 1, "OFFICE", ICTSTypes.SQLVARCHAR, 64);
		SpUtilitario.crearColumna(procedureResponse, 1, "OWN_CHECKS_BALANCE", ICTSTypes.SQLMONEY, 21);
		SpUtilitario.crearColumna(procedureResponse, 1, "LOCAL_CHECKS_BALANCE", ICTSTypes.SQLMONEY, 21);
		SpUtilitario.crearColumna(procedureResponse, 1, "INTERNATIONAL_CHECKS_BALANCE", ICTSTypes.SQLMONEY, 21);
		SpUtilitario.crearColumna(procedureResponse, 1, "TOTAL_CHECKS_BALANCE", ICTSTypes.SQLMONEY, 21);
		SpUtilitario.crearColumna(procedureResponse, 1, "CAUSE_ID", ICTSTypes.SQLVARCHAR, 10);

		if (logger.isInfoEnabled())
			logger.logInfo("Antes de Ingresar datos");

		SpUtilitario.crearFilaDato(procedureResponse, 1,
				new Object[] { "10410108275249013", "25/10/2013", "15:16:00", "PAGO ITBMS CTAAHO Dummy",
						"Prueba CTAAHO-Dummy", "Movimiento realizado en ventanilla CTAAHO", 25.000, 655970403,
						"Debito Dummy", "BANCA VIRTUAL-Dummy", 0.0000, 0.0000, 0.0000, 0.0000, "106" });

		if (logger.isInfoEnabled())
			logger.logInfo("Despues de Ingresar datos");
                
		procedureResponse.setReturnCode(0);

		if (logger.isInfoEnabled())
			logger.logInfo("TERMINANDO SERVICIO: getMovementsDetailSavingAccount");
		return procedureResponse;
	}

	public IProcedureResponse getMovementsDetailCheckingAccount(IProcedureRequest procedureRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: getMovementsDetailCheckingAccount");
			logger.logInfo("RESPUESTA DUMMY GENERADA");
		}

		IProcedureResponse procedureResponse = SpUtilitario.crearProcedureResponse(procedureRequest);
		SpUtilitario.crearResultSet(procedureResponse);

		SpUtilitario.crearColumna(procedureResponse, 1, "NUMBER_ACCOUNT", ICTSTypes.SQLVARCHAR, 24);
		SpUtilitario.crearColumna(procedureResponse, 1, "DATE", ICTSTypes.SQLCHAR, 10);
		SpUtilitario.crearColumna(procedureResponse, 1, "HOUR", ICTSTypes.SQLCHAR, 8);
		SpUtilitario.crearColumna(procedureResponse, 1, "DESCRIPTION", ICTSTypes.SQLVARCHAR, 255);
		SpUtilitario.crearColumna(procedureResponse, 1, "CONCEPT", ICTSTypes.SQLVARCHAR, 64);
		SpUtilitario.crearColumna(procedureResponse, 1, "CAUSE", ICTSTypes.SQLVARCHAR, 255);
		SpUtilitario.crearColumna(procedureResponse, 1, "AMMOUNT", ICTSTypes.SQLMONEY, 21);
		SpUtilitario.crearColumna(procedureResponse, 1, "DOCUMENT_NUMBER", ICTSTypes.SQLINT4, 11);
		SpUtilitario.crearColumna(procedureResponse, 1, "TYPEDC", ICTSTypes.SQLVARCHAR, 255);
		SpUtilitario.crearColumna(procedureResponse, 1, "OFFICE", ICTSTypes.SQLVARCHAR, 64);
		SpUtilitario.crearColumna(procedureResponse, 1, "OWN_CHECKS_BALANCE", ICTSTypes.SQLMONEY, 21);
		SpUtilitario.crearColumna(procedureResponse, 1, "LOCAL_CHECKS_BALANCE", ICTSTypes.SQLMONEY, 21);
		SpUtilitario.crearColumna(procedureResponse, 1, "INTERNATIONAL_CHECKS_BALANCE", ICTSTypes.SQLMONEY, 21);
		SpUtilitario.crearColumna(procedureResponse, 1, "TOTAL_CHECKS_BALANCE", ICTSTypes.SQLMONEY, 21);
		SpUtilitario.crearColumna(procedureResponse, 1, "CAUSE_ID", ICTSTypes.SQLVARCHAR, 10);
		SpUtilitario.crearColumna(procedureResponse, 1, "IMAGE", ICTSTypes.SQLVARCHAR, 1);

		SpUtilitario.crearFilaDato(procedureResponse, 1,
				new Object[] { "10410108275406111", "25/10/2013", "15:16:00", "PAGO ITBMS Dummy", "Prueba Erica-Dummy",
						"Movimiento realizado en ventanilla", 25.000, 655970403, "Debito Dummy", "BANCA VIRTUAL-Dummy",
						0.0000, 0.0000, 0.0000, 0.0000, "106", "N" });

		procedureResponse.setReturnCode(0);

		if (logger.isInfoEnabled())
			logger.logInfo("TERMINANDO SERVICIO: getMovementsDetailCheckingAccount");
		return procedureResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceDetailsMovementsQuery#getMovementsDetailSavingAccount(com.
	 * cobiscorp.ecobis.ib.application.dtos.DetailsMovementsRequest)
	 */
	@Override
	public DetailsMovementsResponse getMovementsDetailSavingAccount(DetailsMovementsRequest detailMovementsRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("**************jv ProcedureResponse: " + detailMovementsRequest.getwAccountStatement());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("**************jv ProcedureResponse: " + detailMovementsRequest.getwEnquiryRequest());
		}
		
 
		String formattedDate=detailMovementsRequest.getwEnquiryRequest().getTransactionDate();
		try {
		    DateFormat format = new SimpleDateFormat("dd/MM/yyyy");	
		    Date fechaSearch=format.parse(detailMovementsRequest.getwEnquiryRequest().getTransactionDate());
		    formattedDate= new SimpleDateFormat("MM/dd/yyyy").format(fechaSearch);
		}catch(Exception xe) {
			logger.logInfo("Error de conversion en date");
			logger.logInfo(xe);
		  formattedDate=detailMovementsRequest.getwEnquiryRequest().getTransactionDate();
		}
		
		
		Context context = ContextManager.getContext();
		CobisSession session = (CobisSession) context.getSession();
		IProcedureRequest executionRequest = new ProcedureRequestAS();
		executionRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800019");
		executionRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		executionRequest.setSpName("cob_ahorros..sp_ahdetalle_trn_bv");
		executionRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "18866");
		executionRequest.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, session.getServer());
		executionRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, session.getOffice());
		executionRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, session.getUser());
		executionRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, session.getTerminal());
		executionRequest.addInputParam("@s_ssn", ICTSTypes.SQLINT4, session.getSessionNumber());
		executionRequest.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR,
				detailMovementsRequest.getwEnquiryRequest().getProductNumber());
		executionRequest.addInputParam("@i_fecha_trn", ICTSTypes.SQLDATETIME,formattedDate);
		executionRequest.addInputParam("@i_ssn", ICTSTypes.SQLINT4,
				detailMovementsRequest.getwAccountStatement().getSequential().toString());
		executionRequest.addInputParam("@i_alt", ICTSTypes.SQLINT4,
				detailMovementsRequest.getwAccountStatement().getAlternateCode().toString());
		executionRequest.addInputParam("@i_trn", ICTSTypes.SQLINT4,
				detailMovementsRequest.getwAccountStatement().getOperationType().toString());

		IProcedureResponse response = executeCoreBanking(executionRequest);
		return transformToDetailsMovementsResponse(response);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceDetailsMovementsQuery#getMovementsDetailCheckingAccount(com.
	 * cobiscorp.ecobis.ib.application.dtos.DetailsMovementsRequest)
	 */
	@Override
	public DetailsMovementsResponse getMovementsDetailCheckingAccount(DetailsMovementsRequest detailMovementsRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("**************jv ProcedureResponse: " + detailMovementsRequest.getwAccountStatement());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("**************jv ProcedureResponse: " + detailMovementsRequest.getwEnquiryRequest());
		}
		Context context = ContextManager.getContext();
		CobisSession session = (CobisSession) context.getSession();
		IProcedureRequest executionRequest = new ProcedureRequestAS();
		executionRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800019");
		executionRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		executionRequest.setSpName("cob_cuentas..sp_ccdetalle_trn_bv");
		executionRequest.addInputParam("@t_trn", ICTSTypes.SQLINT4, "18865");
		executionRequest.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, session.getServer());
		executionRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, session.getOffice());
		executionRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, session.getUser());
		executionRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, session.getTerminal());
		executionRequest.addInputParam("@s_ssn", ICTSTypes.SQLINT4, session.getSessionNumber());
		executionRequest.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR,
				detailMovementsRequest.getwEnquiryRequest().getProductNumber());
		executionRequest.addInputParam("@i_fecha_trn", ICTSTypes.SQLVARCHAR,
				detailMovementsRequest.getwEnquiryRequest().getTransactionDate());
		executionRequest.addInputParam("@i_ssn", ICTSTypes.SQLINT4,
				detailMovementsRequest.getwAccountStatement().getSequential().toString());
		executionRequest.addInputParam("@i_alt", ICTSTypes.SQLINT4,
				detailMovementsRequest.getwAccountStatement().getAlternateCode().toString());
		executionRequest.addInputParam("@i_trn", ICTSTypes.SQLINT4,
				detailMovementsRequest.getwAccountStatement().getOperationType().toString());

		IProcedureResponse response = executeCoreBanking(executionRequest);		
		return transformToDetailsMovementsResponse(response);
	}

	/**
	 * @param response
	 * @return
	 */
	private DetailsMovementsResponse transformToDetailsMovementsResponse(IProcedureResponse aProcedureResponse) {		
		DetailsMovementsResponse detailsMovementsResponse = new DetailsMovementsResponse();
		List<AccountStatement> AccountStatementsCollection = new ArrayList<AccountStatement>();
		AccountStatement wAccountStatement = null;
		Message[] messages = Utils.returnArrayMessage(aProcedureResponse);

		if (logger.isInfoEnabled()) {
			logger.logInfo("**************jv ProcedureResponse: " + aProcedureResponse.getProcedureResponseAsString());
		}
		if (!aProcedureResponse.hasError() && (aProcedureResponse.getReturnCode() == 0)) {
			IResultSetRow[] rows = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();
			IResultSetRowColumnData[] columns = rows[0].getColumnsAsArray();

			wAccountStatement = new AccountStatement();
			wAccountStatement.setAccount(columns[0].getValue() == null ? "" : columns[0].getValue());
			wAccountStatement.setStringDate(columns[1].getValue() == null ? "" : columns[1].getValue());
			wAccountStatement.setHour(columns[2].getValue() == null ? "" : columns[2].getValue());
			wAccountStatement.setDescription(columns[3].getValue() == null ? "" : columns[3].getValue());
			wAccountStatement.setConcept(columns[4].getValue() == null ? "" : columns[4].getValue());
			wAccountStatement.setCause(columns[5].getValue() == null ? "" : columns[5].getValue());
			wAccountStatement.setAmount(
					columns[6].getValue() == null ? new BigDecimal(0) : new BigDecimal(columns[6].getValue()));
			wAccountStatement.setDocumentNumber(columns[7].getValue() == null ? "" : columns[7].getValue());
			wAccountStatement.setTypeDC(columns[8].getValue() == null ? "" : columns[8].getValue());
			wAccountStatement.setOffice(columns[9].getValue() == null ? "" : columns[9].getValue());
			wAccountStatement.setOwnChecksBalance(columns[10].getValue() == null ? "" : columns[10].getValue());
			wAccountStatement.setLocalChecksBalance(columns[11].getValue() == null ? "" : columns[11].getValue());
			wAccountStatement
					.setInternationalCheckBookBalance(columns[12].getValue() == null ? "" : columns[12].getValue());
			wAccountStatement.setTotalChecksBalance(columns[13].getValue() == null ? "" : columns[13].getValue());
			wAccountStatement.setCauseId(columns[14].getValue() == null ? "" : columns[14].getValue());
			wAccountStatement.setRastreo(columns[15].getValue() == null ? "" : columns[15].getValue());
			
			wAccountStatement.setMontoTran(columns[16].getValue() == null ? "" : columns[16].getValue());
			wAccountStatement.setBeneficiario(columns[17].getValue() == null ? "" : columns[17].getValue());
			wAccountStatement.setCuentaDest(columns[18].getValue() == null ? "" : columns[18].getValue());
			wAccountStatement.setCuentaOrig(columns[19].getValue() == null ? "" : columns[19].getValue());
			wAccountStatement.setComisionTran(columns[20].getValue() == null ? "" : columns[20].getValue());
			wAccountStatement.setIvaTran(columns[21].getValue() == null ? "" : columns[21].getValue());
			wAccountStatement.setMensajeTran(columns[22].getValue() == null ? "" : columns[22].getValue());
			
			wAccountStatement.setOriginAccountProp(columns[23].getValue() == null ? "" : columns[23].getValue());
			wAccountStatement.setCurrencySymbol(columns[24].getValue() == null ? "" : columns[24].getValue());
			wAccountStatement.setReferenceNumber(columns[25].getValue() == null ? "" : columns[25].getValue());
			wAccountStatement.setDestinationAccountType(columns[26].getValue() == null ? "" : columns[26].getValue());
			wAccountStatement.setOriginAccountType(columns[27].getValue() == null ? "" : columns[27].getValue());
			wAccountStatement.setBank(columns[28].getValue() == null ? "" : columns[28].getValue());
			
			AccountStatementsCollection.add(wAccountStatement);
			detailsMovementsResponse.setAccountStatementsCollection(AccountStatementsCollection);
		}
		detailsMovementsResponse.setMessages(messages);
		detailsMovementsResponse.setReturnCode(aProcedureResponse.getReturnCode());
		return detailsMovementsResponse;
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
