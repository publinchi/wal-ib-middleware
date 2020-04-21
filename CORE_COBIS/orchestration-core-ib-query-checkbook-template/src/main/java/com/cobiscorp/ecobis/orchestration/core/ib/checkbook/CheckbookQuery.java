package com.cobiscorp.ecobis.orchestration.core.ib.checkbook;

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
import com.cobiscorp.ecobis.ib.application.dtos.CheckBookPreAuthRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CheckBookPreAuthResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CheckbookRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CheckbookResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CheckbookSuspendResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CheckbookValidateSuspendRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CheckbookValidateSuspendResponse;
import com.cobiscorp.ecobis.ib.application.dtos.NextLaborDayRequest;
import com.cobiscorp.ecobis.ib.application.dtos.NextLaborDayResponse;
import com.cobiscorp.ecobis.ib.application.dtos.NoPaycheckOrderRequest;
import com.cobiscorp.ecobis.ib.application.dtos.RequestCheckbookRequest;
import com.cobiscorp.ecobis.ib.application.dtos.RequestCheckbookResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TypesOfCheckbookRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TypesOfCheckbookResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ValidationAccountsRelationsRequest;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Check;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CheckBookPreAuth;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Checkbook;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.LaborDay;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NoPaycheckOrder;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Parameters;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Type;
import com.cobiscorp.ecobis.ib.orchestration.dtos.TypesOfCheckbook;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCheckbook;

@Component(name = "CheckbookQuery", immediate = false)
@Service(value = { ICoreServiceCheckbook.class })
@Properties(value = { @Property(name = "service.description", value = "CheckbookQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CheckbookQuery") })

public class CheckbookQuery extends SPJavaOrchestrationBase implements ICoreServiceCheckbook {

	private static final String CLASS_NAME = " >-----> ";
	private static ILogger logger = LogFactory.getLogger(CheckbookQuery.class);
	/**** Output: getNextLaborDay *****/
	private static final String SP_NAME_NEXTLABORDAY = "cob_bvirtual..sp_siguiente_dia_habil";
	private static final int COL_DATE = 0;
	private static final int COL_DAY = 1;
	private static final int COL_MONTH = 2;
	private static final int COL_YEAR = 3;
	/**** Output: getRequestCheckbook *****/
	private static final String SP_NAME_REQUESTCHECKBOOK = "cob_cuentas..sp_solicitud_chequera_bv";
	/**** validateAccountsRelations- *****/
	private static final String SP_NAME_VALIDATE = "cobis..sp_bv_relacion_ctas";
	/**** validateSuspend- *****/
	private static final String SP_NAME_VALIDATE_SUSPEND = "cob_cuentas..sp_bv_valida_cheque";
	private static final int COL_CHECK_NUMBER = 0;
	private static final int COL_AMOUNT = 1;
	private static final int COL_STATUS = 2;
	/**** TypesOfCheckbook- *****/
	private static final String SP_NAME_TYPEOFCHECKBOOK = "cobis..sp_bv_tipo_chequera";
	private static final int COL_IDTYPE = 0;
	private static final int COL_NAME = 1;
	private static final int COL_TYPE = 2;
	private static final int COL_ART = 3;
	private static final int COL_CUSTOMART = 4;
	private static final int COL_QUANTITY = 5;
	private static final int COL_STATE = 6;
	private static final int COL_TIME = 7;
	private static final int COL_CURRENCYID = 8;
	private static final int COL_AMOUNT_T = 9;
	/**** Suspend Checks- *****/
	private static final String SP_NAME_SUSPEND_CHECK = "cob_cuentas..sp_anula_cheque_bv";
	private static final int COL_INITIAL_CHECK = 0;
	private static final int COL_FINAL_CHECK = 1;
	private static final int COL_ACCOUNT = 2;
	private static final int COL_REASON = 3;
	private static final int COL_SUSPENSION_DATE = 4;
	private static final int COL_REFERENCE = 5;
	private static final int COL_COMMISSION = 6;
	private static final int COL_RESULT_SUBMIT_RPC = 0;
	private static final int COL_AVAILABLE_BALANCE = 1;
	private static final int COL_ACCOUNTING_BALANCE = 2;
	private static final int COL_TO_DRAW_BALANCE = 3;
	private static final int COL_BALANCE_12_HOURS = 4;
	private static final int COL_BALANCE_24_HOURS = 5;
	private static final int COL_BALANCE_24_HOURS2 = 6;
	private static final int COL_BALANCE_REMITTANCES = 7;
	private static final int COL_BLOCKED_AMOUNT = 8;
	private static final int COL_BANKING_PRODUCT = 9;
	private static final int COL_OFFICE_ACCOUNT = 10;
	private static final int COL_STATUS_SUSPEND_CHECKS = 11;
	private static final int COL_NAME_ACCOUNT = 12;
	private static final int COL_FORMED_ACCOUNT = 13;
	private static final int COL_CONSUMPTION_AMOUNT = 14;
	private static final int COL_NUMBER_OF_LOCKS = 15;
	private static final int COL_NUMBER_OF_BLOCKS_PER_AMOUNT = 16;
	private static final int COL_SSN_HOST = 17;
	private static final int COL_PATENT = 18;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCheckbook#
	 * getCheckbook(com.cobiscorp.ecobis.ib.application.dtos.CheckbookRequest)
	 */
	@Override
	public CheckbookResponse getCheckbook(CheckbookRequest aCheckbookRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Iniciando Servicio getCheckbook");
		IProcedureRequest request = initProcedureRequest(aCheckbookRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, "73");
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setSpName("cob_cuentas..sp_tr_cons_chequera");

		request.addInputParam("@t_trn", ICTSTypes.SQLINT2, "73");
		request.addInputParam("@t_ejec", ICTSTypes.SQLCHAR, aCheckbookRequest.getEjec());
		request.addInputParam("@t_rty", ICTSTypes.SQLCHAR, aCheckbookRequest.getRty());
		request.addInputParam("@i_prod", ICTSTypes.SQLINT2,
				aCheckbookRequest.getProductId().getProductType().toString());
		request.addInputParam("@i_mon", ICTSTypes.SQLINT2, aCheckbookRequest.getCurrency().getCurrencyId().toString());
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aCheckbookRequest.getProductNumber().getProductNumber());
		request.addInputParam("@i_modo", ICTSTypes.SQLINT1, aCheckbookRequest.getMode().toString());
		request.addInputParam("@i_sec", ICTSTypes.SQLINT2, aCheckbookRequest.getSequential().toString());
		request.addInputParam("@i_formato_fecha", ICTSTypes.SQLINT4, aCheckbookRequest.getDateFormatId().toString());

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Obtencion del servicio");

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Enviando solicitud al Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request a enviar: " + request.getProcedureRequestAsString());

		IProcedureResponse response = executeCoreBanking(request);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Data devuelta del Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Response obtenido: " + response.getProcedureResponseAsString());

		return transformToCheckbookResponse(response);
	}

	private CheckbookResponse transformToCheckbookResponse(IProcedureResponse aProcedureResponse) {
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "RESPONSE TO TRANSFORM: " + aProcedureResponse.getProcedureResponseAsString());
		CheckbookResponse CheckbookResp = new CheckbookResponse();
		Checkbook aCheckbook = null;
		List<Checkbook> aCheckbookCollection = new ArrayList<Checkbook>();

		IResultSetRow[] rowsCheckbook = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

		for (IResultSetRow iResultSetRow : rowsCheckbook) {
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
			aCheckbook = new Checkbook();

			aCheckbook.setSequential(Integer.parseInt(columns[0].getValue()));
			aCheckbook.setInitialCheck(Integer.parseInt(columns[1].getValue()));
			aCheckbook.setNumberOfChecks(Integer.parseInt(columns[2].getValue()));
			aCheckbook.setType(columns[3].getValue());
			aCheckbook.setCreationDate(columns[4].getValue());
			aCheckbook.setDeliveryDate(columns[5].getValue());
			aCheckbook.setStatus(columns[6].getValue());
			aCheckbook.setPrintShippingDate(columns[7].getValue());
			aCheckbook.setReceiptPrintingDate(columns[8].getValue());
			aCheckbook.setReceiptOfficeDate(columns[9].getValue());
			aCheckbook.setCreationOffice(columns[10].getValue());
			aCheckbook.setReceptionOffice(columns[11].getValue());
			aCheckbook.setRunNumber(columns[12].getValue() == null ? 0 : Integer.parseInt(columns[12].getValue()));
			aCheckbookCollection.add(aCheckbook);
		}
		CheckbookResp.setCheckbooksCollection(aCheckbookCollection);

		Message[] message = Utils.returnArrayMessage(aProcedureResponse);
		CheckbookResp.setMessages(message);

		return CheckbookResp;

	}

	// ************************CheckBookPreAuthResponse
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCheckbook#
	 * getCheckBookPreAuth(com.cobiscorp.ecobis.ib.application.dtos.
	 * CheckBookPreAuthRequest)
	 */
	@Override
	public CheckBookPreAuthResponse getCheckBookPreAuth(CheckBookPreAuthRequest aCheckBookPreAuthRequest)
			throws CTSServiceException, CTSInfrastructureException {

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Iniciando Servicio getCheckBookPreAuth");
		IProcedureRequest request = initProcedureRequest(aCheckBookPreAuthRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, "73");
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");

		request.setSpName("cobis..sp_preaut_cheque_bv");

		request.addInputParam("@t_trn", ICTSTypes.SQLINT2, "1801001");
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aCheckBookPreAuthRequest.getAccount().toString());
		request.addInputParam("@i_moneda", ICTSTypes.SQLINT2, aCheckBookPreAuthRequest.getCurrencyId().toString());
		request.addInputParam("@i_valor", ICTSTypes.SQLMONEY, aCheckBookPreAuthRequest.getAmount().toString());
		request.addInputParam("@i_beneficiario", ICTSTypes.SQLVARCHAR,
				aCheckBookPreAuthRequest.getBeneficiary().toString());
		request.addInputParam("@i_cheque", ICTSTypes.SQLINT4, aCheckBookPreAuthRequest.getCheckId().toString());
		request.addOutputParam("@o_estado_cheque", ICTSTypes.SQLCHAR, "A");

		IProcedureResponse response = executeCoreBanking(request);
		return transformToCheckBookPreAuthResponse(response);
	}

	private CheckBookPreAuthResponse transformToCheckBookPreAuthResponse(IProcedureResponse response) {

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "RESPONSE TO TRANSFORM: " + response.getProcedureResponseAsString());
		CheckBookPreAuthResponse CheckbookResp = new CheckBookPreAuthResponse();
		CheckBookPreAuth aCheckBookPreAuth = null;
		List<CheckBookPreAuth> aCheckBookPreAuthCollection = new ArrayList<CheckBookPreAuth>();

		aCheckBookPreAuth = new CheckBookPreAuth();
		aCheckBookPreAuth.setStatus(response.readValueParam("@o_estado_cheque"));

		aCheckBookPreAuthCollection.add(aCheckBookPreAuth);
		CheckbookResp.setList(aCheckBookPreAuthCollection);

		if (response.getReturnCode() == 0)
			CheckbookResp.setSuccess(true);
		else
			CheckbookResp.setSuccess(false);

		CheckbookResp.setReturnCode(response.getReturnCode());

		Message[] message = Utils.returnArrayMessage(response);
		CheckbookResp.setMessages(message);

		return CheckbookResp;
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

	public IProcedureResponse validateAccountsRelations(ValidationAccountsRelationsRequest aValidationRequest)
			throws CTSServiceException, CTSInfrastructureException {

		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: validateAccountsRelations");
		}
		IProcedureResponse pResponse = Execution(SP_NAME_VALIDATE, aValidationRequest, "validateAccountsRelations");
		if (logger.isDebugEnabled())
			logger.logDebug("validateAccountsRelations Response" + pResponse.getProcedureResponseAsString());
		return pResponse;

	}

	private IProcedureResponse Execution(String spName, ValidationAccountsRelationsRequest aValidationRequest,
			String method) {
		IProcedureRequest request = initProcedureRequest(aValidationRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1875041");
		request.setSpName(spName);

		request.addInputParam("@i_cliente", ICTSTypes.SQLINT4, aValidationRequest.getEntityId().toString());
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aValidationRequest.getProductNumber());

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request Corebanking: " + request.getProcedureRequestAsString());
		}

		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response Validation: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response Validation*** ");
		}

		return pResponse;
	}

	@Override
	public RequestCheckbookResponse getRequestCheckbook(RequestCheckbookRequest aRequestCheckbookRequest)
			throws CTSServiceException, CTSInfrastructureException {

		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getRequestCheckbook");
		}

		IProcedureResponse pResponse = Execution(SP_NAME_REQUESTCHECKBOOK, aRequestCheckbookRequest,
				"getRequestCheckbook");
		RequestCheckbookResponse requestCheckbookResponse = transformToRequestCheckbookResponse(pResponse,
				"getRequestCheckbook");
		return requestCheckbookResponse;
	}

	private IProcedureResponse Execution(String spNameRequestcheckbook,
			RequestCheckbookRequest aRequestCheckbookRequest, String string) {
		IProcedureRequest request = initProcedureRequest(aRequestCheckbookRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800005");
		request.setSpName(spNameRequestcheckbook);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1800005");

		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, aRequestCheckbookRequest.getOperation());
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aRequestCheckbookRequest.getProduct().getProductNumber());
		request.addInputParam("@i_mon", ICTSTypes.SQLINT4,
				(aRequestCheckbookRequest.getCurrency().getCurrencyId() == null ? "0"
						: aRequestCheckbookRequest.getCurrency().getCurrencyId()).toString());
		request.addInputParam("@i_tchq", ICTSTypes.SQLVARCHAR, aRequestCheckbookRequest.getCheckbook().getType());
		request.addInputParam("@i_nchqs", ICTSTypes.SQLINT4,
				(aRequestCheckbookRequest.getCheckbook().getNumberOfChecks() == null ? "0"
						: aRequestCheckbookRequest.getCheckbook().getNumberOfChecks()).toString());
		request.addInputParam("@i_ofientr", ICTSTypes.SQLINT4, (aRequestCheckbookRequest.getOfficeDelivery() == null
				? "0" : aRequestCheckbookRequest.getOfficeDelivery()).toString());
		request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, aRequestCheckbookRequest.getUserName().getLogin());
		request.addInputParam("@i_dia_entrega", ICTSTypes.SQLVARCHAR,
				aRequestCheckbookRequest.getCheckbook().getDeliveryDate());
		request.addInputParam("@i_id_entrega", ICTSTypes.SQLVARCHAR, aRequestCheckbookRequest.getDeliveyId());
		request.addInputParam("@i_nombre_entrega", ICTSTypes.SQLVARCHAR, aRequestCheckbookRequest.getDeliveryName());
		request.addInputParam("@i_nombre_arte", ICTSTypes.SQLVARCHAR, aRequestCheckbookRequest.getCheckbookArt());
		request.addInputParam("@i_tipo_id", ICTSTypes.SQLVARCHAR, aRequestCheckbookRequest.getTypeId());
		if (aRequestCheckbookRequest.getCauseComi() != null) {
			request.addInputParam("@i_causa_comi", ICTSTypes.SQLVARCHAR, aRequestCheckbookRequest.getCauseComi());
			request.addInputParam("@i_servicio_costo", ICTSTypes.SQLVARCHAR, aRequestCheckbookRequest.getServiceCost());
		}

		request.addInputParam("@i_prod", ICTSTypes.SQLINT4, (aRequestCheckbookRequest.getProductId().getId() == null
				? "0" : aRequestCheckbookRequest.getProductId().getId()).toString());

		request.addInputParam("@i_monto", ICTSTypes.SQLDECIMAL, "1.00");// aRequestCheckbookRequest.getAmount());
		request.addInputParam("@i_proposito", ICTSTypes.SQLVARCHAR, "CH");//
		request.addOutputParam("@o_tipo_chequera", ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXXXXXXXXX");

		if (logger.isDebugEnabled()) {
			logger.logDebug("@i_operacion: " + aRequestCheckbookRequest.getOperation());
			logger.logDebug("@i_cta: " + aRequestCheckbookRequest.getProduct().getProductNumber());
			logger.logDebug("@i_mon: " + aRequestCheckbookRequest.getCurrency().getCurrencyId().toString());
			logger.logDebug("@i_tchq: " + aRequestCheckbookRequest.getCheckbook().getType());
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request Corebanking RequestCheckbookRequest: " + request.getProcedureRequestAsString());
		}

		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response RequestCheckbook: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response RequestCheckbook *** ");
			logger.logInfo("JEFF Response: " + pResponse.toString());
		}
		return pResponse;
	}

	private RequestCheckbookResponse transformToRequestCheckbookResponse(IProcedureResponse pResponse, String method) {
		RequestCheckbookResponse requestCheckbookResponse = new RequestCheckbookResponse();

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: transformToRequestCheckbookResponse***"
					+ pResponse.getProcedureResponseAsString());
		}

		if (method.equals("getRequestCheckbook")) {

			requestCheckbookResponse.setTypeCheckbook(pResponse.readValueParam("@o_tipo_chequera"));
			// requestCheckbookResponse.setReference(Integer.parseInt(pResponse.readValueParam("@o_referencia")));
		}

		if (pResponse.getReturnCode() == 0)
			requestCheckbookResponse.setSuccess(true);
		else
			requestCheckbookResponse.setSuccess(false);

		requestCheckbookResponse.setReturnCode(pResponse.getReturnCode());

		Message[] message = Utils.returnArrayMessage(pResponse);
		requestCheckbookResponse.setMessages(message);

		return requestCheckbookResponse;
	}

	@Override
	public NextLaborDayResponse getNextLaborDay(NextLaborDayRequest aNextLaborDayRequest)
			throws CTSServiceException, CTSInfrastructureException {

		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getNextLaborDay");
		}

		IProcedureResponse pResponse = Execution(SP_NAME_NEXTLABORDAY, aNextLaborDayRequest, "getNextLaborDay");
		NextLaborDayResponse nextLaborDayResponse = transformToNextLaborDayResponse(pResponse, "getNextLaborDay");
		return nextLaborDayResponse;
	}

	private IProcedureResponse Execution(String spNameNextlaborday, NextLaborDayRequest aNextLaborDayRequest,
			String string) throws CTSServiceException, CTSInfrastructureException {

		IProcedureRequest request = initProcedureRequest(aNextLaborDayRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800137");
		request.setSpName(spNameNextlaborday);

		request.addInputParam("@i_fecha", ICTSTypes.SQLVARCHAR, aNextLaborDayRequest.getLaborDay().getDate());
		request.addInputParam("@i_dias", ICTSTypes.SQLINT2, (aNextLaborDayRequest.getLaborDay().getDay() == null ? "0"
				: aNextLaborDayRequest.getLaborDay().getDay()).toString());
		request.addInputParam("@i_oficina", ICTSTypes.SQLINT4,
				(aNextLaborDayRequest.getOfficeId().getId() == null ? "0" : aNextLaborDayRequest.getOfficeId().getId())
						.toString());
		request.addInputParam("@i_comercial", ICTSTypes.SQLVARCHAR,
				aNextLaborDayRequest.getCommercial() == null ? "N" : aNextLaborDayRequest.getCommercial().toString());
		if (aNextLaborDayRequest.getCommercial().equals("X"))
			request.addOutputParam("@o_dias", ICTSTypes.SQLINT4, (aNextLaborDayRequest.getLaborDay().getDay() == null
					? "0" : aNextLaborDayRequest.getLaborDay().getDay()).toString());

		if (logger.isDebugEnabled()) {
			logger.logDebug("@i_fecha: " + aNextLaborDayRequest.getLaborDay().getDate());
			logger.logDebug("@i_dias: " + aNextLaborDayRequest.getLaborDay().getDay().toString());
			logger.logDebug("@i_oficina: " + aNextLaborDayRequest.getOfficeId().getId().toString());
			logger.logDebug("@i_comercial: " + aNextLaborDayRequest.getCommercial());
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request Corebanking NextLaborDayRequest: " + request.getProcedureRequestAsString());
		}

		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response NextLaborDayRequest: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response NextLaborDayRequest*** ");
		}

		return pResponse;
	}

	private NextLaborDayResponse transformToNextLaborDayResponse(IProcedureResponse pResponse, String method) {
		NextLaborDayResponse nextLaborDayResp = new NextLaborDayResponse();
		LaborDay laborDay = null;
		List<LaborDay> listNextLaborDay = null;
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + pResponse.getProcedureResponseAsString());
		}

		nextLaborDayResp.setDays(pResponse.readValueParam("@o_dias") == null ? 0
				: Integer.parseInt(pResponse.readValueParam("@o_dias")));
		IResultSetRow[] rowsNextLaborDay = pResponse.getResultSet(1).getData().getRowsAsArray();
		if (method.equals("getNextLaborDay")) {
			listNextLaborDay = new ArrayList<LaborDay>();

			for (int i = 0; i < rowsNextLaborDay.length; i++) {
				laborDay = new LaborDay();
				IResultSetRow iResultSetRow = rowsNextLaborDay[i];
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

				laborDay.setDate(columns[COL_DATE].getValue());
				laborDay.setDay(
						columns[COL_DAY].getValue() == null ? 0 : Integer.parseInt(columns[COL_DAY].getValue()));
				laborDay.setMonth(
						columns[COL_MONTH].getValue() == null ? 0 : Integer.parseInt(columns[COL_MONTH].getValue()));
				laborDay.setYear(
						columns[COL_YEAR].getValue() == null ? 0 : Integer.parseInt(columns[COL_YEAR].getValue()));
				listNextLaborDay.add(laborDay);
			}
		}

		nextLaborDayResp.setNextLaborDay(listNextLaborDay);
		nextLaborDayResp.setReturnCode(pResponse.getReturnCode());

		Message[] message = Utils.returnArrayMessage(pResponse);
		nextLaborDayResp.setMessages(message);

		return nextLaborDayResp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCheckbook#
	 * validateSuspendCheckBook(com.cobiscorp.ecobis.ib.application.dtos.
	 * CheckbookValidateSuspendRequest)
	 */
	@Override
	public CheckbookValidateSuspendResponse validateSuspendCheckBook(
			CheckbookValidateSuspendRequest aValidateSuspendRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: validateSuspend");
		}

		IProcedureResponse pResponse = Execution(SP_NAME_VALIDATE_SUSPEND, aValidateSuspendRequest, "validateSuspend");
		CheckbookValidateSuspendResponse wValidateSuspendResponse = transformToDTOResponse(pResponse,
				"validateSuspend");
		return wValidateSuspendResponse;
	}

	/**
	 * @param String
	 *            spName
	 * @param CheckbookValidateSuspendRequest
	 * @param string
	 * @return
	 */
	private IProcedureResponse Execution(String spName, CheckbookValidateSuspendRequest validateSuspendRequest,
			String method) {
		IProcedureRequest request = initProcedureRequest(validateSuspendRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800123");
		request.setSpName(spName);

		request.addInputParam("@i_cuenta", ICTSTypes.SQLVARCHAR, validateSuspendRequest.getAccount());
		request.addInputParam("@i_cheque_inicio", ICTSTypes.SQLINT4,
				validateSuspendRequest.getInitialCheck().toString());
		request.addInputParam("@i_cheque_fin", ICTSTypes.SQLINT4,
				validateSuspendRequest.getNumberOfChecks().toString());

		if (logger.isDebugEnabled()) {
			logger.logDebug("@i_tpersona: " + validateSuspendRequest.getAccount());

		}
		if (logger.isDebugEnabled()) {
			logger.logDebug("Request Corebanking: " + request.getProcedureRequestAsString());
		}

		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response ValidateSuspend: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize ValidateSuspend*** ");
		}

		return pResponse;
	}

	private CheckbookValidateSuspendResponse transformToDTOResponse(IProcedureResponse pResponse, String method) {
		CheckbookValidateSuspendResponse validateSuspendResponse = new CheckbookValidateSuspendResponse();
		Check checks = new Check();
		List<Check> listChecks = new ArrayList<Check>();
		BigDecimal bigDecimal = null;
		if (logger.isInfoEnabled())
			logger.logInfo("*** ProcedureResponse: ***" + pResponse.getProcedureResponseAsString());

		if (pResponse.getReturnCode() == 0) {

			IResultSetRow[] rowsChecks = pResponse.getResultSet(1).getData().getRowsAsArray();

			if (method.equals("validateSuspend")) {

				for (int i = 0; i < rowsChecks.length; i++) {
					checks = new Check();
					IResultSetRow iResultSetRow = rowsChecks[i];
					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

					checks.setCheckNumber(columns[COL_CHECK_NUMBER].getValue());
					bigDecimal = new BigDecimal(columns[COL_AMOUNT].getValue());
					checks.setAmount(bigDecimal);
					checks.setStatus(columns[COL_STATUS].getValue());

					listChecks.add(checks);
				}

				validateSuspendResponse.setChecks(listChecks);

			}

		}

		validateSuspendResponse.setReturnCode(pResponse.getReturnCode());

		Message[] message = Utils.returnArrayMessage(pResponse);
		validateSuspendResponse.setMessages(message);

		return validateSuspendResponse;
	}

	/** @autor jmoreta */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCheckbook#
	 * getTypesOfCheckbook(com.cobiscorp.ecobis.ib.application.dtos.
	 * TypesOfCheckbookRequest)
	 */
	@Override
	public TypesOfCheckbookResponse getTypesOfCheckbook(TypesOfCheckbookRequest aTypesOfCheckbookRequest)
			throws CTSServiceException, CTSInfrastructureException {

		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getTypesOfCheckbook");
		}

		IProcedureResponse pResponse = Execution(SP_NAME_TYPEOFCHECKBOOK, aTypesOfCheckbookRequest,
				"getTypesOfCheckbook");
		TypesOfCheckbookResponse typesOfCheckbookResponse = transformToTypesOfCheckbookResponse(pResponse,
				"getTypesOfCheckbook");
		return typesOfCheckbookResponse;
	}

	/**
	 * @autor jmoreta
	 * @param spNameTypeofcheckbook
	 * @param aTypesOfCheckbookRequest
	 * @param method
	 * @return
	 */
	private IProcedureResponse Execution(String spNameTypeofcheckbook, TypesOfCheckbookRequest aTypesOfCheckbookRequest,
			String method) {

		IProcedureRequest request = initProcedureRequest(aTypesOfCheckbookRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800003");
		request.setSpName(spNameTypeofcheckbook);
		// request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1800003");

		request.addInputParam("@i_moneda", ICTSTypes.SQLINT1,
				aTypesOfCheckbookRequest.getCurrency().getCurrencyId().toString());
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "S");//

		if (logger.isDebugEnabled()) {
			logger.logDebug("@i_moneda: " + aTypesOfCheckbookRequest.getCurrency().getCurrencyId().toString());
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request Corebanking TypesOfCheckbookRequest: " + request.getProcedureRequestAsString());
		}

		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response TypesOfCheckbookRequest: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response TypesOfCheckbookRequest*** ");
		}

		return pResponse;
	}

	/**
	 * @autor jmoreta
	 * @param pResponse
	 * @param string
	 * @return
	 */
	private TypesOfCheckbookResponse transformToTypesOfCheckbookResponse(IProcedureResponse pResponse, String method) {
		TypesOfCheckbookResponse typesOfCheckbookResp = new TypesOfCheckbookResponse();
		TypesOfCheckbook typesOfCheckbook = null;
		List<TypesOfCheckbook> listTypesOfCheckbook = null;
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + pResponse.getProcedureResponseAsString());
		}

		if (pResponse.getReturnCode() == 0) {

			IResultSetRow[] rowsTypesOfCheckbook = pResponse.getResultSet(1).getData().getRowsAsArray();
			if (method.equals("getTypesOfCheckbook")) {
				listTypesOfCheckbook = new ArrayList<TypesOfCheckbook>();

				typesOfCheckbookResp.setSuccess(true);

				for (int i = 0; i < rowsTypesOfCheckbook.length; i++) {
					typesOfCheckbook = new TypesOfCheckbook();
					IResultSetRow iResultSetRow = rowsTypesOfCheckbook[i];
					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
					Type type = new Type();
					Parameters parameters = new Parameters();
					CheckBookPreAuth preAuth = new CheckBookPreAuth();
					Currency currency = new Currency();

					type.setIdType(columns[COL_IDTYPE].getValue());
					type.setType(columns[COL_TYPE].getValue());
					parameters.setName(columns[COL_NAME].getValue());
					preAuth.setStatus(columns[COL_STATE].getValue());
					currency.setCurrencyId(columns[COL_CURRENCYID].getValue() == null ? 0
							: Integer.parseInt(columns[COL_CURRENCYID].getValue()));

					typesOfCheckbook.setType(type);
					typesOfCheckbook.setName(parameters);
					typesOfCheckbook.setArt(columns[COL_ART].getValue());
					typesOfCheckbook.setCustomArt(columns[COL_CUSTOMART].getValue());
					typesOfCheckbook.setQuantity(columns[COL_QUANTITY].getValue());
					typesOfCheckbook.setState(preAuth);
					typesOfCheckbook.setTime(
							columns[COL_TIME].getValue() == null ? 0 : Integer.parseInt(columns[COL_TIME].getValue()));
					typesOfCheckbook.setCurrency(currency);
					typesOfCheckbook.setAmount(columns[COL_AMOUNT_T].getValue());

					listTypesOfCheckbook.add(typesOfCheckbook);
				}
			}
		} else {
			Message[] message = Utils.returnArrayMessage(pResponse);
			typesOfCheckbookResp.setMessages(message);
			typesOfCheckbookResp.setSuccess(false);
		}

		typesOfCheckbookResp.setTypesOfCheckbook(listTypesOfCheckbook);

		typesOfCheckbookResp.setReturnCode(pResponse.getReturnCode());
		return typesOfCheckbookResp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCheckbook#
	 * suspendChecks(com.cobiscorp.ecobis.ib.application.dtos.
	 * NoPaycheckOrderRequest)
	 */
	@Override
	public CheckbookSuspendResponse suspendChecks(NoPaycheckOrderRequest aNoPaycheckOrderRequest)
			throws CTSServiceException, CTSInfrastructureException {

		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: suspendChecks");
		}

		IProcedureResponse pResponse = Execution(SP_NAME_SUSPEND_CHECK, aNoPaycheckOrderRequest, "suspendChecks");
		CheckbookSuspendResponse cbSuspendResponse = transformprocedureToDTOResponse(pResponse);
		return cbSuspendResponse;
	}

	/**
	 * @author jchonillo
	 * @param String
	 *            spNameSuspendCheck
	 * @param NoPaycheckOrderRequest
	 *            aNoPaycheckOrderRequest
	 * @param String
	 *            method
	 * @return
	 */
	private IProcedureResponse Execution(String spNameSuspendCheck, NoPaycheckOrderRequest aNoPaycheckOrderRequest,
			String method) {

		IProcedureRequest request = initProcedureRequest(aNoPaycheckOrderRequest.getOriginalRequest());
		// IProcedureRequest request = new ProcedureRequestAS();
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800023");
		request.setSpName(spNameSuspendCheck);
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "18413");
		request.addInputParam("@i_razon", ICTSTypes.SQLCHAR, aNoPaycheckOrderRequest.getReason());

		if (aNoPaycheckOrderRequest.getCauseComi() != null) {
			request.addInputParam("@i_causa_comi", ICTSTypes.SQLVARCHAR, aNoPaycheckOrderRequest.getCauseComi());
			request.addInputParam("@i_servicio_costo", ICTSTypes.SQLVARCHAR, aNoPaycheckOrderRequest.getServiceCost());
		}
		request.addInputParam("@i_desde", ICTSTypes.SQLINT4, aNoPaycheckOrderRequest.getInitialCheck().toString());
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aNoPaycheckOrderRequest.getAccount());
		request.addInputParam("@i_mon", ICTSTypes.SQLINT2, aNoPaycheckOrderRequest.getCurrencyId().toString());
		request.addInputParam("@i_hasta", ICTSTypes.SQLINT4, aNoPaycheckOrderRequest.getNumberOfChecks().toString());
		request.addInputParam("@i_prod", ICTSTypes.SQLINT1, aNoPaycheckOrderRequest.getProductId().toString());
		request.addOutputParam("@o_referencia", ICTSTypes.SQLINT4, "0");

		if (logger.isDebugEnabled()) {

			logger.logDebug("@i_producto: " + aNoPaycheckOrderRequest.getProductAbbreviation());
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request Corebanking NoPaycheckOrderRequest: " + request.getProcedureRequestAsString());
		}

		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response NoPaycheckOrderRequest: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response NoPaycheckOrderRequest*** ");
			logger.logInfo("Joel Response: " + pResponse.toString());
		}

		return pResponse;
	}

	private CheckbookSuspendResponse transformprocedureToDTOResponse(IProcedureResponse procedureResponse) {
		CheckbookSuspendResponse cbSuspendResponse = new CheckbookSuspendResponse();
		List<NoPaycheckOrder> listNoPaycheckOrder = new ArrayList<NoPaycheckOrder>();

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + procedureResponse.getProcedureResponseAsString());
		}

		if (procedureResponse.getReturnCode() == 0) {

			IResultSetRow[] rowsNoPaycheckOrder = procedureResponse.getResultSet(1).getData().getRowsAsArray();
if (logger.isDebugEnabled()) 
			logger.logDebug("*** getMessageListSize: ***" + procedureResponse.getMessageListSize());

			cbSuspendResponse.setSuccess(true);

			for (int i = 0; i < rowsNoPaycheckOrder.length; i++) {
				NoPaycheckOrder noPaycheckOrder = new NoPaycheckOrder();
				IResultSetRow iResultSetRow = rowsNoPaycheckOrder[i];
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

				noPaycheckOrder.setInitialCheck(columns[COL_INITIAL_CHECK].getValue() == null ? 0
						: Integer.parseInt(columns[COL_INITIAL_CHECK].getValue()));
				noPaycheckOrder.setFinalCheck(columns[COL_FINAL_CHECK].getValue() == null ? 0
						: Integer.parseInt(columns[COL_FINAL_CHECK].getValue()));
				noPaycheckOrder.setAccount(columns[COL_ACCOUNT].getValue());
				noPaycheckOrder.setReason(columns[COL_REASON].getValue());
				noPaycheckOrder.setSuspensionDate(columns[COL_SUSPENSION_DATE].getValue());
				noPaycheckOrder.setReference(columns[COL_REFERENCE].getValue() == null ? 0
						: Integer.parseInt(columns[COL_REFERENCE].getValue()));
				noPaycheckOrder.setCommission(columns[COL_COMMISSION].getValue() == null ? 0.0
						: Double.parseDouble(columns[COL_COMMISSION].getValue()));
				listNoPaycheckOrder.add(noPaycheckOrder);
			}
		} else
			cbSuspendResponse.setSuccess(false);

		cbSuspendResponse.setReturnCode(procedureResponse.getReturnCode());
		cbSuspendResponse.setReference(procedureResponse.readValueParam("@o_referencia") == null ? 0
				: Integer.parseInt(procedureResponse.readValueParam("@o_referencia")));

		cbSuspendResponse.setListNoPaycheckOrder(listNoPaycheckOrder);

		Message[] message = Utils.returnArrayMessage(procedureResponse);
		cbSuspendResponse.setMessages(message);

		return cbSuspendResponse;
	};
}
