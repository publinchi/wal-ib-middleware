package com.cobiscorp.ecobis.orchestration.core.ib.accountbycriteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IProvider;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.MessageBlock;
import com.cobiscorp.ecobis.ib.application.dtos.AccountRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AccountResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountDetail;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.IWSAccounts;

@Component(name = "AccountByCriteriaQuery", immediate = false)
@Service(value = { IWSAccounts.class })
@Properties(value = { @Property(name = "service.description", value = "AccountByCriteriaQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "AccountByCriteriaQuery") })
public class AccountByCriteriaQuery extends SPJavaOrchestrationBase implements IWSAccounts {

	private static ILogger logger = LogFactory.getLogger(AccountByCriteriaQuery.class);
	private java.util.Properties properties;
	private static final int COL_COD_ACCOUNT = 0;
	private static final int COL_NAME = 1;
	private static final int COL_DETAIL = 2;
	private static final int COL_COD_SERVICE = 3;
	private static final int COL_DESC_SERVICE = 4;
	private static final int COL_COD_CURRENCY = 5;
	private String code;
	private String codeError;

	static final String ORIGINAL_RESPONSE = "ORIGINAL_RESPONSE";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.IWSAccounts#
	 * getAccountByCriteria
	 * (com.cobiscorp.ecobis.ib.application.dtos.AccountRequest, java.util.Map,
	 * java.util.Properties)
	 */
	@Override
	public AccountResponse getAccountByCriteria(AccountRequest aAccountRequest,
			Map<String, Object> aBagSPJavaOrchestration, java.util.Properties properties)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getAccountByCriteria");
		}
		this.properties = properties;
		IProcedureResponse pResponse = Execution(aAccountRequest, aBagSPJavaOrchestration);
		AccountResponse accountResponse = transformToStockResponse(pResponse);
		return accountResponse;
	}

	private IProcedureResponse Execution(AccountRequest aAccountRequest, Map<String, Object> aBagSPJavaOrchestration)
			throws CTSServiceException, CTSInfrastructureException {

		IProcedureRequest request = initProcedureRequest(aAccountRequest.getOriginalRequest());

		if (aAccountRequest.getCode0() != null) {
			code = aAccountRequest.getCode0();
		}
		if (aAccountRequest.getCode1() != null) {
			code = code + "|" + aAccountRequest.getCode1();
		}
		if (aAccountRequest.getCode2() != null) {
			code = code + "|" + aAccountRequest.getCode2();
		}
		if (aAccountRequest.getCode3() != null) {
			code = code + "|" + aAccountRequest.getCode3();
		}
		if (aAccountRequest.getCode4() != null) {
			code = code + "|" + aAccountRequest.getCode4();
		}
		if (aAccountRequest.getCode5() != null) {
			code = code + "|" + aAccountRequest.getCode5();
		}
		if (aAccountRequest.getCode6() != null) {
			code = code + "|" + aAccountRequest.getCode6();
		}
		if (aAccountRequest.getCode7() != null) {
			code = code + "|" + aAccountRequest.getCode7();
		}
		if (aAccountRequest.getCode8() != null) {
			code = code + "|" + aAccountRequest.getCode8();
		}
		if (aAccountRequest.getCode9() != null) {
			code = code + "|" + aAccountRequest.getCode9();
		}

		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "690");

		// Parametros requeridos por el conector para Sintesis
		request.addInputParam("@i_operacion_connector", ICTSTypes.SYBINT4, "690");
		if (logger.isInfoEnabled()) {
			logger.logInfo("aAccountRequest.getIdOperation() ==>> " + aAccountRequest.getIdOperation());
			logger.logInfo(
					"aAccountRequest.getCodModule().toString() ==>> " + aAccountRequest.getCodModule().toString());
			logger.logInfo(
					"aAccountRequest.getCodCriteria().toString() ==>> " + aAccountRequest.getCodCriteria().toString());
			logger.logInfo("code ==>> " + code.toString());
		}
		request.addInputParam("@i_id_operativo", ICTSTypes.SQLVARCHAR, aAccountRequest.getIdOperation());
		request.addInputParam("@i_codmodulo", ICTSTypes.SQLINT4, aAccountRequest.getCodModule().toString());
		request.addInputParam("@i_criterio", ICTSTypes.SQLINT4, aAccountRequest.getCodCriteria().toString());
		request.addInputParam("@i_codigo", ICTSTypes.SQLVARCHAR, code);

		request.addOutputParam("@o_coderror", ICTSTypes.SYBINT4, "0");
		request.addOutputParam("@o_mensaje", ICTSTypes.SQLVARCHAR, "            ");
		request.addOutputParam("@o_nrooperacion", ICTSTypes.SYBINT4, "0");
		request.addOutputParam("@o_fechaoperativa", ICTSTypes.SYBINT4, "0");

		request.removeFieldInHeader("trn_virtual");
		request.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "690");

		codeError = ((String) this.properties.get("CODE_ERROR_SESSION"));

		request.addFieldInHeader(ICOBISTS.HEADER_TIMEOUT, ICOBISTS.HEADER_STRING_TYPE,
				((String) this.properties.get("HEADER_TIMEOUT")));
		request.addFieldInHeader(IProvider.HEADER_CATALOG_PROVIDER, ICOBISTS.HEADER_STRING_TYPE,
				((String) this.properties.get("HEADER_CATALOG_PROVIDER")));
		aBagSPJavaOrchestration.put(ICISSPBaseOrchestration.CONNECTOR_TYPE,
				((String) this.properties.get("CONNECTOR_TYPE")));
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Request:*** " + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Start Request *** ");
		}

		IProcedureResponse pResponse = executeProvider(request, aBagSPJavaOrchestration);
		aBagSPJavaOrchestration.put(ORIGINAL_RESPONSE, pResponse);
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response *** ");
		}
		return pResponse;
	}

	private AccountResponse transformToStockResponse(IProcedureResponse aProcedureResponse) {
		AccountResponse AccountResp = new AccountResponse();
		List<AccountDetail> accountDetailCollection = new ArrayList<AccountDetail>();
		AccountDetail aAccountDetail = null;
		boolean errorWS = false;
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + aProcedureResponse.getProcedureResponseAsString());
		}

		for (String codigos : codeError.split(",")) {
			if (logger.isInfoEnabled())
				logger.logInfo("Codigos de Error ==> " + codigos);
			if (aProcedureResponse.readValueParam("@o_coderror").equals(codigos)) {
				errorWS = true;
			}
		}

		if (errorWS) {
			AccountResp.setReturnCode(Integer.parseInt(aProcedureResponse.readValueParam("@o_coderror")));
			AccountResp.setCodError(Integer.parseInt(aProcedureResponse.readValueParam("@o_coderror")));
			AccountResp.setMessageError(aProcedureResponse.readValueParam("@o_mensaje"));
			AccountResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		} else {
			aProcedureResponse.setReturnCode(Integer.parseInt(aProcedureResponse.readValueParam("@o_coderror")));
			AccountResp.setCodError(Integer.parseInt(aProcedureResponse.readValueParam("@o_coderror")));
			if (aProcedureResponse.getReturnCode() == 0) {

				IResultSetRow[] rowsStock = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

				for (IResultSetRow iResultSetRow : rowsStock) {
					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
					aAccountDetail = new AccountDetail();
					aAccountDetail.setCodAccount(columns[COL_COD_ACCOUNT].getValue());
					aAccountDetail.setClientName(columns[COL_NAME].getValue());
					aAccountDetail.setDetail(columns[COL_DETAIL].getValue());
					aAccountDetail.setCodServices(Integer.parseInt(columns[COL_COD_SERVICE].getValue()));
					aAccountDetail.setDescService(columns[COL_DESC_SERVICE].getValue());
					aAccountDetail.setCodCurrency(Integer.parseInt(columns[COL_COD_CURRENCY].getValue()));

					accountDetailCollection.add(aAccountDetail);
				}

				AccountResp.setAccountDetailCollection(accountDetailCollection);

				if (aProcedureResponse.readValueParam("@o_nrooperacion") != null) {
					AccountResp.setNumOperation(Integer.parseInt(aProcedureResponse.readValueParam("@o_nrooperacion")));
				}
				if (aProcedureResponse.readValueParam("@o_fechaoperativa") != null) {
					AccountResp
							.setOperationDate(Integer.parseInt(aProcedureResponse.readValueParam("@o_fechaoperativa")));
				}
			} else {
				AccountResp.setReturnCode(Integer.parseInt(aProcedureResponse.readValueParam("@o_coderror")));
				AccountResp.setCodError(Integer.parseInt(aProcedureResponse.readValueParam("@o_coderror")));
				AccountResp.setMessageError(aProcedureResponse.readValueParam("@o_mensaje"));
				AccountResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));
			}

			AccountResp.setReturnCode(aProcedureResponse.getReturnCode());
		}

		return AccountResp;
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
