package com.cobiscorp.ecobis.orchestration.core.ib;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceLoanBalance;

@Component(name = "DetailsLoanBalanceQuery", immediate = false)
@Service(value = { ICoreServiceLoanBalance.class })
@Properties(value = { @Property(name = "service.description", value = "DetailsLoanBalanceQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "DetailsLoanBalanceQuery") })
public class DetailsLoanBalanceQuery implements ICoreServiceLoanBalance {
	/**
	 * Instance logger component
	 */
	private static ILogger logger = LogFactory.getLogger(DetailsLoanBalanceQuery.class);

	@Override
	public IProcedureResponse getDetailsLoanBalance(IProcedureRequest procedureRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: getDetailLoanBalance");
			logger.logInfo("RESPUESTA DUMMY GENERADA");
		}

		IProcedureResponse procedureResponse = SpUtilitario.crearProcedureResponse(procedureRequest);
		SpUtilitario.crearResultSet(procedureResponse);
		SpUtilitario.crearColumna(procedureResponse, 1, "RESULT_SUBMIT", ICTSTypes.SQLINT4, 6);
		SpUtilitario.crearColumna(procedureResponse, 1, "PRODUCT_NUMBER", ICTSTypes.SQLVARCHAR, 24);
		SpUtilitario.crearColumna(procedureResponse, 1, "ENTITY_NAME", ICTSTypes.SQLVARCHAR, 45);
		SpUtilitario.crearColumna(procedureResponse, 1, "OPERATION_TYPE", ICTSTypes.SQLVARCHAR, 35);
		SpUtilitario.crearColumna(procedureResponse, 1, "INITIAL_AMOUNT", ICTSTypes.SQLDECIMAL, 21);
		SpUtilitario.crearColumna(procedureResponse, 1, "MONTHLY_PAYMENT_DAY", ICTSTypes.SQLVARCHAR, 16);
		SpUtilitario.crearColumna(procedureResponse, 1, "STATUS", ICTSTypes.SQLVARCHAR, 255);
		SpUtilitario.crearColumna(procedureResponse, 1, "LAST_PAYMENT_DATE", ICTSTypes.SQLVARCHAR, 10);
		SpUtilitario.crearColumna(procedureResponse, 1, "EXPIRATION_DATE", ICTSTypes.SQLVARCHAR, 10);
		SpUtilitario.crearColumna(procedureResponse, 1, "EXECUTIVE", ICTSTypes.SQLVARCHAR, 60);
		SpUtilitario.crearColumna(procedureResponse, 1, "INITIAL_DATE", ICTSTypes.SQLVARCHAR, 10);
		SpUtilitario.crearColumna(procedureResponse, 1, "ARREARS_DAYS", ICTSTypes.SQLINT4, 6);
		SpUtilitario.crearColumna(procedureResponse, 1, "OVERDUE_CAPITAL", ICTSTypes.SQLDECIMAL, 21);
		SpUtilitario.crearColumna(procedureResponse, 1, "OVERDUE_INTEREST", ICTSTypes.SQLDECIMAL, 21);
		SpUtilitario.crearColumna(procedureResponse, 1, "OVERDUE_ARREARS_VALUE", ICTSTypes.SQLDECIMAL, 21);
		SpUtilitario.crearColumna(procedureResponse, 1, "OVERDUE_ANOTHER_ITEMS", ICTSTypes.SQLDECIMAL, 21);
		SpUtilitario.crearColumna(procedureResponse, 1, "OVERDUE_TOTAL", ICTSTypes.SQLDECIMAL, 21);
		SpUtilitario.crearColumna(procedureResponse, 1, "NEXT_PAYMENT_DATE", ICTSTypes.SQLVARCHAR, 10);
		SpUtilitario.crearColumna(procedureResponse, 1, "NEXT_PAYMENT_VALUE", ICTSTypes.SQLDECIMAL, 21);
		SpUtilitario.crearColumna(procedureResponse, 1, "ORDINARY_INTEREST_RATE", ICTSTypes.SQLDECIMAL, 85);
		SpUtilitario.crearColumna(procedureResponse, 1, "ARREARS_INTEREST_RATE", ICTSTypes.SQLDECIMAL, 85);
		SpUtilitario.crearColumna(procedureResponse, 1, "CAPITAL_BALANCE", ICTSTypes.SQLDECIMAL, 21);
		SpUtilitario.crearColumna(procedureResponse, 1, "TOTAL_BALANCE", ICTSTypes.SQLDECIMAL, 21);
		SpUtilitario.crearColumna(procedureResponse, 1, "ORIGINAL_TERM", ICTSTypes.SQLVARCHAR, 64);
		SpUtilitario.crearColumna(procedureResponse, 1, "SECTOR", ICTSTypes.SQLVARCHAR, 10);
		SpUtilitario.crearColumna(procedureResponse, 1, "OPERATION_DESCRIPTION", ICTSTypes.SQLVARCHAR, 10);
		SpUtilitario.crearColumna(procedureResponse, 1, "FECI", ICTSTypes.SQLDECIMAL, 21);
		SpUtilitario.crearFilaDato(procedureResponse, 1,
				new Object[] { 0.00, "10410000041700201", "CLIENTE (TEST_REGRESION_DESACOPLADO)",
						"HIPOTECARIO DESACOPLADO", 1234567.0000, "5", null, null, "10/05/2037", null, "10/08/2007", 0,
						0.00, 0.00, 0.00, 0.00, 0.0000, "05/21/2014", 1234567.0100, 10.0, 5.0, 1234567.4500,
						1234567.2900, "123 M", "BHVI", "HVI", 0.0000 });

		procedureResponse.setReturnCode(0);

		if (logger.isInfoEnabled())
			logger.logInfo("TERMINANDO SERVICIO: getDetailLoanBalance");
		return procedureResponse;
	}
}
