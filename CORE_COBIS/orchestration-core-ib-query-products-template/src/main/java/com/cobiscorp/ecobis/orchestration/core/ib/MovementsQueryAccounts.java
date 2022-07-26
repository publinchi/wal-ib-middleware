/**
 *
 */
package com.cobiscorp.ecobis.orchestration.core.ib;

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
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobisv.commons.context.CobisSession;
import com.cobiscorp.cobisv.commons.context.Context;
import com.cobiscorp.cobisv.commons.context.ContextManager;
import com.cobiscorp.ecobis.ib.application.dtos.AccountStatementRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AccountStatementResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountStatement;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMovementsQuery;
import com.cobiscorp.ecobis.ib.utils.dtos.Utils;

@Component(name = "MovementsQueryAccounts", immediate = false)
@Service(value = { ICoreServiceMovementsQuery.class })
@Properties(value = { @Property(name = "service.description", value = "MovementsQueryAccounts"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "MovementsQueryAccounts") })
public class MovementsQueryAccounts extends SPJavaOrchestrationBase implements ICoreServiceMovementsQuery {

	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	protected static final String COBIS_CONTEXT = "COBIS";
	private static final String CLASS_NAME = " >-----> ";
	private static ILogger logger = LogFactory.getLogger(MovementsQueryAccounts.class);

	@Override
	public AccountStatementResponse getMovementsCheckingAccount(AccountStatementRequest accountStatementRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "INICIANDO SERVICIO");
			logger.logInfo(CLASS_NAME + "RESPUESTA CORE GENERADA");
		}
		AccountStatementResponse wAccountStatementResponse = transformResponse(
				executeGetMovementsCheckingAccount(accountStatementRequest));

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "TERMINANDO SERVICIO getMovementsCheckingAccount ");
		return wAccountStatementResponse;
	}

	@Override
	public AccountStatementResponse getMovementsSavingAccount(AccountStatementRequest accountStatementRequest)
			throws CTSServiceException, CTSInfrastructureException {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "INICIANDO SERVICIO");
			logger.logInfo(CLASS_NAME + "RESPUESTA CORE GENERADA");
		}
		AccountStatementResponse wAccountStatementResponse = transformResponse(
				executeGetMovementsSavingAccount(accountStatementRequest));

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "TERMINANDO SERVICIO getMovementsSavingAccount ");
		return wAccountStatementResponse;
	}

	private IProcedureResponse executeGetMovementsSavingAccount(AccountStatementRequest accountStatementRequest) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando consulta de movimientos ctas ahorros CORE COBIS");

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800057");
		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1800057");
		anOriginalRequest.addInputParam("@t_online", ICTSTypes.SYBCHAR, "S");
		anOriginalRequest.addInputParam("@i_comision", ICTSTypes.SYBMONEYN, "0");
		anOriginalRequest.addInputParam("@i_servicio", ICTSTypes.SQLINT1,
				accountStatementRequest.getOriginalRequest().readValueParam("@s_servicio"));

		anOriginalRequest.setSpName("cob_ahorros..sp_tr04_consulta_mov_ah");
		Context context = ContextManager.getContext();
		CobisSession session = (CobisSession) context.getSession();

		anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SYBINT4, session.getOffice());
		anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, session.getUser());
		anOriginalRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, session.getTerminal());
		anOriginalRequest.addInputParam("@s_rol", ICTSTypes.SYBINT4, session.getRole());
		anOriginalRequest.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, session.getServer());

		Product product = accountStatementRequest.getProduct();

		if (product.getCurrency().getCurrencyId() != null)
			anOriginalRequest.addInputParam("@i_mon", ICTSTypes.SQLINT2,
					product.getCurrency().getCurrencyId().toString());

		if (product.getProductType() != null)
			anOriginalRequest.addInputParam("@i_prod", ICTSTypes.SQLINT1, product.getProductType().toString());

		if (product.getProductNumber() != null)
			anOriginalRequest.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, product.getProductNumber());

		if (accountStatementRequest.getLogin() != null)
			anOriginalRequest.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, accountStatementRequest.getLogin());

		if (accountStatementRequest.getDateFormatId() != null)
			anOriginalRequest.addInputParam("@i_formato_fecha", ICTSTypes.SQLINT1,
					accountStatementRequest.getDateFormatId());

		if (accountStatementRequest.getSequential() != null)
			anOriginalRequest.addInputParam("@i_sec", ICTSTypes.SQLINT4, accountStatementRequest.getSequential());

		if (accountStatementRequest.getAlternateCode() != null)
			anOriginalRequest.addInputParam("@i_sec_alt", ICTSTypes.SQLINT4,
					accountStatementRequest.getAlternateCode());

		if (accountStatementRequest.getOperationLastMovement() != null)
			anOriginalRequest.addInputParam("@i_operacion", ICTSTypes.SQLCHAR,
					accountStatementRequest.getOperationLastMovement() ? "L" : "");

		if (accountStatementRequest.getNumberOfMovements() != null)
			anOriginalRequest.addInputParam("@i_nro_registros", ICTSTypes.SQLINT4,
					accountStatementRequest.getNumberOfMovements());

		if (accountStatementRequest.getType() != null)
			anOriginalRequest.addInputParam("@i_tipo", ICTSTypes.SQLCHAR, accountStatementRequest.getType());

		if (accountStatementRequest.getInitialDate() != null)
			anOriginalRequest.addInputParam("@i_fecha_ini", ICTSTypes.SQLDATETIME,
					Utils.formatDateToString(accountStatementRequest.getInitialDate()));

		if (accountStatementRequest.getFinalDate() != null)
			anOriginalRequest.addInputParam("@i_fecha_fin", ICTSTypes.SQLDATETIME,
					Utils.formatDateToString(accountStatementRequest.getFinalDate()));

		if (accountStatementRequest.getUniqueSequential() != null)
			anOriginalRequest.addInputParam("@i_sec_unico", ICTSTypes.SQLINT4,
					accountStatementRequest.getUniqueSequential());

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		//response.addParam("RASTREO",ICTSTypes.SQLVARCHAR,255,"PENDIENTE");


		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta del Core:" + response.getProcedureResponseAsString());

		return response;
	}

	private IProcedureResponse executeGetMovementsCheckingAccount(AccountStatementRequest accountStatementRequest) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando consulta de movimientos ctas corrientes CORE COBIS");

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);

		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800056");
		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1800056");
		anOriginalRequest.addInputParam("@t_online", ICTSTypes.SYBCHAR, "S");
		anOriginalRequest.addInputParam("@i_comision", ICTSTypes.SYBMONEYN, "0");
		anOriginalRequest.addInputParam("@i_servicio", ICTSTypes.SQLINT1,
				accountStatementRequest.getOriginalRequest().readValueParam("@s_servicio"));

		anOriginalRequest.setSpName("cob_cuentas..sp_tr03_cons_movimientos_cc");

		Product product = accountStatementRequest.getProduct();

		if (product.getCurrency().getCurrencyId() != null)
			anOriginalRequest.addInputParam("@i_mon", ICTSTypes.SQLINT2,
					product.getCurrency().getCurrencyId().toString());

		if (product.getProductType() != null)
			anOriginalRequest.addInputParam("@i_prod", ICTSTypes.SQLINT1, product.getProductType().toString());

		if (product.getProductNumber() != null)
			anOriginalRequest.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, product.getProductNumber());

		if (accountStatementRequest.getLogin() != null)
			anOriginalRequest.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, accountStatementRequest.getLogin());

		if (Utils.isNullOrEmpty(accountStatementRequest.getDateFormatId()))
			anOriginalRequest.addInputParam("@i_formato_fecha", ICTSTypes.SQLINT1,
					accountStatementRequest.getDateFormatId());

		if (accountStatementRequest.getInitialDate() != null)
			anOriginalRequest.addInputParam("@i_fecha_ini", ICTSTypes.SQLDATETIME,
					Utils.formatDateToString(accountStatementRequest.getInitialDate()));

		if (accountStatementRequest.getFinalDate() != null)
			anOriginalRequest.addInputParam("@i_fecha_fin", ICTSTypes.SQLDATETIME,
					Utils.formatDateToString(accountStatementRequest.getFinalDate()));

		if (accountStatementRequest.getSequential() != null)
			anOriginalRequest.addInputParam("@i_sec", ICTSTypes.SQLINT4, accountStatementRequest.getSequential());

		if (accountStatementRequest.getAlternateCode() != null)
			anOriginalRequest.addInputParam("@i_sec_alt", ICTSTypes.SQLINT4,
					accountStatementRequest.getAlternateCode());

		if (accountStatementRequest.getType() != null)
			anOriginalRequest.addInputParam("@i_tipo", ICTSTypes.SQLCHAR, accountStatementRequest.getType());

		if (accountStatementRequest.getNumberOfMovements() != null)
			anOriginalRequest.addInputParam("@i_nro_registros", ICTSTypes.SQLINT4,
					accountStatementRequest.getNumberOfMovements());

		if (accountStatementRequest.getUniqueSequential() != null)
			anOriginalRequest.addInputParam("@i_sec_unico", ICTSTypes.SQLINT4,
					accountStatementRequest.getUniqueSequential());

		if (accountStatementRequest.getOperationLastMovement() != null)
			anOriginalRequest.addInputParam("@i_operacion", ICTSTypes.SQLCHAR,
					accountStatementRequest.getOperationLastMovement() ? "L" : "");

		if (accountStatementRequest.getCause() != null)
			anOriginalRequest.addInputParam("@i_causa", ICTSTypes.SQLVARCHAR, accountStatementRequest.getCause());

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta del Core:" + response.getProcedureResponseAsString());

		return response;
	}

	private AccountStatementResponse transformResponse(IProcedureResponse response) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Ejecutando Transformacion de Respuesta" + response);

		AccountStatementResponse wAccountStatementResponse = new AccountStatementResponse();
		List<AccountStatement> accountStatementCollection = new ArrayList<AccountStatement>();
		Utils.transformIprocedureResponseToBaseResponse(wAccountStatementResponse, response);
		if (!response.hasError()) {
			IResultSetBlock resulsetOrigin = response.getResultSet(2);
			IResultSetRow[] rowsTemp = resulsetOrigin.getData().getRowsAsArray();

			if (rowsTemp.length > 0) {

				for (IResultSetRow iResultSetRow : rowsTemp) {
					IResultSetRowColumnData[] rows = iResultSetRow.getColumnsAsArray();
					AccountStatement accountStatement = new AccountStatement();

					if (rows[0].getValue() != null)
						accountStatement.setStringDate(rows[0].getValue());
					if (rows[1].getValue() != null)
						accountStatement.setDescription(rows[1].getValue().toString());
					if (rows[2].getValue() != null)
						accountStatement.setTypeOperation(new Integer(rows[2].getValue().toString()));
					if (rows[3].getValue() != null)
						accountStatement.setReference(rows[3].getValue().toString());
					if (rows[4].getValue() != null)
						accountStatement.setTypeTransaction(rows[4].getValue().toString());
					if (rows[5].getValue() != null)
						accountStatement.setAmount(new BigDecimal(rows[5].getValue().toString()));
					if (rows[6].getValue() != null)
						accountStatement.setAccountingBalance(new BigDecimal(rows[6].getValue().toString()));
					if (rows[7].getValue() != null)
						accountStatement.setAvailableBalance(new BigDecimal(rows[7].getValue().toString()));
					if (rows[8].getValue() != null)
						accountStatement.setSequential(new Integer(rows[8].getValue().toString()));
					if (rows[9].getValue() != null)
						accountStatement.setAlternateCode(new Integer(rows[9].getValue().toString()));
					if (rows[10].getValue() != null)
						accountStatement.setHour(rows[10].getValue().toString());
					if (rows[11].getValue() != null)
						accountStatement.setUniqueSequential(new Integer(rows[11].getValue().toString()));
					if (rows[12].getValue() != null)
						accountStatement.setImage(rows[12].getValue().toString());
					if (rows[13].getValue() != null)
						accountStatement.setConcept(rows[13].getValue().toString());
					//	accountStatement.setRastreo(rastreoSpei(new Integer(rows[8].getValue().toString())));
                    if(rows[14].getValue() != null)
						accountStatement.setRastreo(rows[14].getValue().toString());
					if(rows[15].getValue() != null)
						accountStatement.setTarjetNumber(rows[15].getValue().toString());
					if(rows[16].getValue() != null)
						accountStatement.setBeneficiario(rows[16].getValue().toString());
					accountStatementCollection.add(accountStatement);
				}
			}

			resulsetOrigin = response.getResultSet(1);
			rowsTemp = resulsetOrigin.getData().getRowsAsArray();

			if (rowsTemp.length > 0) {
				for (IResultSetRow iResultSetRow : rowsTemp) {
					IResultSetRowColumnData[] rows = iResultSetRow.getColumnsAsArray();

					if (rows[0].getValue() != null)
						wAccountStatementResponse.setNumberOfResult(new Integer(rows[0].getValue()));
				}
			}

			wAccountStatementResponse.setAccountStatements(accountStatementCollection);
		}

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta" + wAccountStatementResponse);
		return wAccountStatementResponse;
	}

	private String rastreoSpei(int secuencial){

		String rastreo="";

		logger.logInfo("jcos- OBTENIENDO RASTREO SPEI");
		String transaccion="0";

		transaccion=String.valueOf(secuencial);


		IProcedureRequest request = new ProcedureRequestAS();
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);		// SE SETEAN DATOS
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setSpName("cob_bvirtual..sp_registra_spei");
		request.addInputParam("@t_trn", ICTSTypes.SQLINTN, "18010");
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "R");
		request.addInputParam("@i_transaccion_spei", ICTSTypes.SYBINT4, transaccion);
		// SE SETEA VARIABLE DE SALIDA
		request.addOutputParam("@o_rastreo", ICTSTypes.SYBVARCHAR, "XXX");
		// SE EJECUTA Y SE OBTIENE LA RESPUESTA
		IProcedureResponse pResponse = executeCoreBanking(request);
		rastreo=pResponse.readValueParam("@o_rastreo");

		logger.logInfo("jcos- TERMINA OBTENIENDO RASTREO SPEI");

		return rastreo;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}
