package com.cobiscorp.ecobis.orchestration.core.accounts;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.AccountStatementRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AccountStatementResponse;
import com.cobiscorp.ecobis.ib.application.dtos.AccountingParameterRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AccountingParameterResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountStatement;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountingParameter;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMovementsQuery;
import com.cobiscorp.ecobis.ib.utils.dtos.Utils;

/**
 *
 * @author gyagual
 * @since Jul 24, 2014
 * @version 1.0.0
 */

@Component(name = "MovementsAccountsQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "MovementsAccountsQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "MovementsAccountsQueryOrchestrationCore") })
public class MovementsAccountsQueryOrchestrationCore extends SPJavaOrchestrationBase {
	private static ILogger logger = LogFactory.getLogger(MovementsAccountsQueryOrchestrationCore.class);

	private static final String CLASS_NAME = "--->";
	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceMovementsQuery.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	private ICoreServiceMovementsQuery coreServiceMovementsQuery;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreService(ICoreServiceMovementsQuery service) {
		coreServiceMovementsQuery = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreService(ICoreServiceMovementsQuery service) {
		coreServiceMovementsQuery = null;
	}

	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";

	static final String OPERATION1_REQUEST = "OPERATION1_REQUEST";
	static final String OPERATION1_RESPONSE = "OPERATION1_RESPONSE";
	private static final int PRODUCT_CTACTE = 3;
	private static final int PRODUCT_CTAAHO = 4;
	private static final int DEBIT_CHECKING_ACCOUNT = 50;
	private static final int DEBIT_SAVING_ACCOUNT = 264;
	private static final int CREDIT_CHECKING_ACCOUNT = 48;
	private static final int CREDIT_SAVING_ACCOUNT = 253;

	private Map<String, Integer> trxProducts = new HashMap<String, Integer>();

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		if (logger.isInfoEnabled())
			logger.logInfo(
					"Component-Name: " + this.getClass().getSimpleName() + ", Component-Version: $$project.version$$");
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		Map<String, Object> wprocedureResponse1 = procedureResponse1(anOriginalRequest, aBagSPJavaOrchestration);
		Boolean wSuccessExecutionOperation1 = (Boolean) wprocedureResponse1.get("SuccessExecutionOperation");
		IProcedureResponse wIProcedureResponse1 = (IProcedureResponse) wprocedureResponse1.get("IProcedureResponse");
		IProcedureResponse wErrorProcedureResponse = (IProcedureResponse) wprocedureResponse1
				.get("ErrorProcedureResponse");

		if (wErrorProcedureResponse != null) {
			return wErrorProcedureResponse;
		}
		if (!wSuccessExecutionOperation1) {
			return wIProcedureResponse1;
		}
		wIProcedureResponse1 = (IProcedureResponse) aBagSPJavaOrchestration.get(OPERATION1_RESPONSE);
		return wIProcedureResponse1;
	}

	protected Map<String, Object> procedureResponse1(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "procedureResponse1");
		HashMap<String, Object> ret = new HashMap<String, Object>();
		ret.put("IProcedureResponse", null);
		ret.put("SuccessExecutionOperation", null);
		ret.put("ErrorProcedureResponse", null);

		boolean wSuccessExecutionOperation1 = executeOperation1(anOriginalRequest, aBagSPJavaOrchestration);
		ret.put("SuccessExecutionOperation", wSuccessExecutionOperation1);

		if (logger.isDebugEnabled())
			logger.logDebug("result exeuction operation 1: " + wSuccessExecutionOperation1);

		IProcedureResponse wProcedureResponseOperation1 = initProcedureResponse(anOriginalRequest);
		wProcedureResponseOperation1.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE,
				ICSP.ERROR_EXECUTION_SERVICE);
		ret.put("IProcedureResponse", wProcedureResponseOperation1);

		return ret;
	}

	protected boolean executeOperation1(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeOperation1");

		IProcedureResponse wProcedureResponse = initProcedureResponse(anOriginalRequest);
		AccountStatementResponse accountStatementResponse = new AccountStatementResponse();
		try {

			String wAccountType = anOriginalRequest.readValueParam("@i_prod");
			int wIntAccountType = Integer.parseInt(wAccountType.trim());
			String wType = anOriginalRequest.readValueParam("@i_tipo");

			AccountStatementRequest accountStatementRequest = transformRequestToDto(aBagSPJavaOrchestration);
			if (logger.isInfoEnabled())
				logger.logInfo(CLASS_NAME + "SOLO PARA CHEQUES DE GERENCIA, CONSULTAMOS LA CAUSA");
			if (wType.toString().equals("G")) {
				accountStatementRequest.setCause(getTransactionCause(aBagSPJavaOrchestration));
			}

			switch (wIntAccountType) {
			case PRODUCT_CTACTE:
				accountStatementResponse = coreServiceMovementsQuery
						.getMovementsCheckingAccount(accountStatementRequest);
				break;
			case PRODUCT_CTAAHO:
				accountStatementResponse = coreServiceMovementsQuery.getMovementsSavingAccount(accountStatementRequest);
				break;
			default:
				break;
			}

			wProcedureResponse = transformDtoToResponse(accountStatementResponse, aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put(OPERATION1_RESPONSE, wProcedureResponse);

			return !wProcedureResponse.hasError();
		} catch (CTSServiceException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSServiceException:" + e.getMessage());
			e.printStackTrace();
			aBagSPJavaOrchestration.put(OPERATION1_RESPONSE, null);
			return false;
		} catch (CTSInfrastructureException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSInfrastructureException:" + e.getMessage());
			aBagSPJavaOrchestration.put(OPERATION1_RESPONSE, null);
			return false;
		}
	}

	public String getTransactionCause(Map<String, Object> aBag) {
		String cause = "";
		// TODO Auto-generated method stub
		IProcedureRequest wOriginalRequest = (ProcedureRequestAS) aBag.get(ORIGINAL_REQUEST);
		IProcedureRequest request = initProcedureRequest(wOriginalRequest);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800111");
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1800111");

		if (logger.isInfoEnabled()) {
			logger.logInfo("START getTransactionCause");
		}
		request.setSpName("cob_bvirtual..sp_bv_ing_serv");
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "ST");
		request.addInputParam("@i_transaccion", ICTSTypes.SQLINT4, "1800120");
		request.addInputParam("@i_producto", ICTSTypes.SQLINT4,
				String.valueOf(wOriginalRequest.readValueParam("@i_prod")));

		if (logger.isDebugEnabled()) {
			logger.logDebug("REQUEST getTransactionCause: -->" + request.getProcedureRequestAsString());
		}

		IProcedureResponse wResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("RESPONSE getTransactionCause: " + wResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("FINISH getTransactionCause");
		}

		IResultSetRow[] rows = wResponse.getResultSet(1).getData().getRowsAsArray();

		if (rows.length != 0) {
			for (IResultSetRow iResultSetRow : rows) {
				IResultSetRowColumnData[] cols = iResultSetRow.getColumnsAsArray();
				cause = cols[3].getValue();
			}
		}

		return cause;

	}

	private IProcedureResponse transformDtoToResponse(AccountStatementResponse accountStatementResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Salida:" + accountStatementResponse);
		IProcedureResponse pResponse = initProcedureResponse(
				(IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));
		// setea errores
		Utils.transformBaseResponseToIprocedureResponse(accountStatementResponse, pResponse);

		IResultSetHeader metaData = new ResultSetHeader();
		metaData = new ResultSetHeader();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("NUMBER_OF_RESULTS", ICTSTypes.SQLINT4, 4));

		IResultSetRow row = new ResultSetRow();
		IResultSetBlock resultBlock;
		if (accountStatementResponse != null && accountStatementResponse.getNumberOfResult() != null) {
			row.addRowData(1,
					new ResultSetRowColumnData(false, accountStatementResponse.getNumberOfResult().toString()));
			IResultSetData data1 = new ResultSetData();
			data1.addRow(row);
			resultBlock = new ResultSetBlock(metaData, data1);
			pResponse.addResponseBlock(resultBlock);
		}

		IResultSetData data = new ResultSetData();
		if (accountStatementResponse.getSuccess()) {
			if (accountStatementResponse != null && accountStatementResponse.getAccountStatements().size() > 0) {
				metaData = new ResultSetHeader();

				metaData.addColumnMetaData(new ResultSetHeaderColumn("TRANSACTIONDATE", ICTSTypes.SQLVARCHAR, 20));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("DESCRIPTION", ICTSTypes.SQLVARCHAR, 255));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("OPERATIONTYPE", ICTSTypes.SQLINT1, 1));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("REFERENCE", ICTSTypes.SQLVARCHAR, 255));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("SIGNDC", ICTSTypes.SQLCHAR, 1));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("AMOUNT", ICTSTypes.SQLDECIMAL, 21));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("ACCOUNTINGBALANCE", ICTSTypes.SQLDECIMAL, 21));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("AVAILABLEBALANCE", ICTSTypes.SQLDECIMAL, 21));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("SEQUENTIAL", ICTSTypes.SQLINT4, 6));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("ALTERNATECODE", ICTSTypes.SQLINT4, 6));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("HOUR", ICTSTypes.SQLVARCHAR, 20));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("UNIQUESEQUENTIAL", ICTSTypes.SQLINT4, 6));
				metaData.addColumnMetaData(new ResultSetHeaderColumn("IMAGE", ICTSTypes.SQLVARCHAR, 1));

				for (AccountStatement obj : accountStatementResponse.getAccountStatements()) {
					row = new ResultSetRow();
					row.addRowData(1, new ResultSetRowColumnData(false, obj.getStringDate()));
					row.addRowData(2, new ResultSetRowColumnData(false, obj.getDescription()));
					row.addRowData(3, new ResultSetRowColumnData(false, obj.getTypeOperation().toString()));
					row.addRowData(4, new ResultSetRowColumnData(false, obj.getReference()));
					row.addRowData(5, new ResultSetRowColumnData(false, obj.getTypeTransaction()));
					row.addRowData(6, new ResultSetRowColumnData(false, obj.getAmount().toString()));
					row.addRowData(7, new ResultSetRowColumnData(false, obj.getAccountingBalance().toString()));
					row.addRowData(8, new ResultSetRowColumnData(false, obj.getAvailableBalance().toString()));
					row.addRowData(9, new ResultSetRowColumnData(false, obj.getSequential().toString()));
					row.addRowData(10, new ResultSetRowColumnData(false, obj.getAlternateCode().toString()));
					row.addRowData(11, new ResultSetRowColumnData(false, obj.getHour()));
					if (obj.getUniqueSequential() != null)
						row.addRowData(12, new ResultSetRowColumnData(false, obj.getUniqueSequential().toString()));
					data.addRow(row);
				}
				resultBlock = new ResultSetBlock(metaData, data);
				pResponse.addResponseBlock(resultBlock);
			}
		}
		return pResponse;
	}

	private AccountStatementRequest transformRequestToDto(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Entrada");
		IProcedureRequest wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);

		AccountStatementRequest accountStatementRequest = new AccountStatementRequest();
		AccountStatement accountStatement = new AccountStatement();
		Product product = new Product();
		Currency c = new Currency();
		product.setCurrency(c);

		if (!Utils.isNullOrEmpty(wOriginalRequest.readValueParam("@i_mon")))
			product.getCurrency().setCurrencyId(Integer.parseInt(wOriginalRequest.readValueParam("@i_mon").toString()));

		if (!Utils.isNullOrEmpty(wOriginalRequest.readValueParam("@i_prod")))
			product.setProductType(Integer.parseInt(wOriginalRequest.readValueParam("@i_prod")));

		if (!Utils.isNullOrEmpty(wOriginalRequest.readValueParam("@i_cta")))
			product.setProductNumber(wOriginalRequest.readValueParam("@i_cta"));

		if (!Utils.isNullOrEmpty(wOriginalRequest.readValueParam("@i_login")))
			accountStatementRequest.setLogin(wOriginalRequest.readValueParam("@i_login"));

		if (!Utils.isNullOrEmpty(wOriginalRequest.readValueParam("@i_formato_fecha")))
			accountStatementRequest.setDateFormatId(wOriginalRequest.readValueParam("@i_formato_fecha"));

		if (!Utils.isNullOrEmpty(wOriginalRequest.readValueParam("@i_fecha_ini")))
			accountStatementRequest.setInitialDate(Utils.formatDate(wOriginalRequest.readValueParam("@i_fecha_ini")));

		if (!Utils.isNullOrEmpty(wOriginalRequest.readValueParam("@i_fecha_fin")))
			accountStatementRequest.setFinalDate(Utils.formatDate(wOriginalRequest.readValueParam("@i_fecha_fin")));

		if (!Utils.isNullOrEmpty(wOriginalRequest.readValueParam("@i_sec")))
			accountStatementRequest.setSequential(wOriginalRequest.readValueParam("@i_sec"));

		if (!Utils.isNullOrEmpty(wOriginalRequest.readValueParam("@i_sec_alt")))
			accountStatementRequest.setAlternateCode(wOriginalRequest.readValueParam("@i_sec_alt"));

		if (!Utils.isNullOrEmpty(wOriginalRequest.readValueParam("@i_tipo")))
			accountStatementRequest.setType(wOriginalRequest.readValueParam("@i_tipo"));

		if (!Utils.isNullOrEmpty(wOriginalRequest.readValueParam("@i_nro_registros")))
			accountStatementRequest.setNumberOfMovements(wOriginalRequest.readValueParam("@i_nro_registros"));

		if (!Utils.isNullOrEmpty(wOriginalRequest.readValueParam("@i_sec_unico")))
			accountStatementRequest.setUniqueSequential(wOriginalRequest.readValueParam("@i_sec_unico"));

		if (!Utils.isNullOrEmpty(wOriginalRequest.readValueParam("@i_operacion")))
			accountStatementRequest.setOperationLastMovement(
					Boolean.valueOf(wOriginalRequest.readValueParam("@i_operacion").equals("L")));

		accountStatementRequest.setAccountStatement(accountStatement);
		accountStatementRequest.setProduct(product);
		accountStatementRequest.setOriginalRequest(wOriginalRequest);

		return accountStatementRequest;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalProcedureReq);
		return wProcedureRespFinal;
	}

}
