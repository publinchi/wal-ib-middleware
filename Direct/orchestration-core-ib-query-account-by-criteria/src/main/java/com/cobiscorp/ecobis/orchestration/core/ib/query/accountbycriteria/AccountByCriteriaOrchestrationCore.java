package com.cobiscorp.ecobis.orchestration.core.ib.query.accountbycriteria;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.AccountRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AccountResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountDetail;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.IWSAccounts;
import com.cobiscorp.ecobis.orchestration.ws.base.SintesisBaseTemplate;

/**
 * Credit Lines
 * 
 * @since Jul 14, 2015
 * @author itorres
 * @version 1.0.0
 * 
 */
@Component(name = "AccountByCriteriaOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "AccountByCriteriaOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "AccountByCriteriaOrchestrationCore") })
public class AccountByCriteriaOrchestrationCore extends SintesisBaseTemplate {

	private static ILogger logger = LogFactory.getLogger(AccountByCriteriaOrchestrationCore.class);
	private static final String CLASS_NAME = "ModulesOrchestrationCore--->";
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	private java.util.Properties properties;

	/**
	 * Read configuration of parent component
	 */

	public void loadConfiguration(IConfigurationReader arg0) {
		super.loadConfiguration(arg0);
		this.properties = arg0.getProperties("//property");
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = IWSAccounts.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceAccounts", unbind = "unbindCoreServiceAccounts")
	protected IWSAccounts coreServiceAccounts;

	/**
	 * Instance Service Interface
	 * 
	 * @param service
	 */
	public void bindCoreServiceAccounts(IWSAccounts service) {
		coreServiceAccounts = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	public void unbindCoreServiceAccounts(IWSAccounts service) {
		coreServiceAccounts = null;
	}

	/**
	 * /** Execute transfer first step of service
	 * <p>
	 * This method is the main executor of transactional contains the original
	 * input parameters.
	 * 
	 * @param anOriginalRequest
	 *            - Information original sended by user's.
	 * @param aBagSPJavaOrchestration
	 *            - Object dictionary transactional steps.
	 * 
	 * @return
	 *         <ul>
	 *         <li>IProcedureResponse - Represents the service execution.</li>
	 *         </ul>
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeJavaOrchestration");
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);

		try {
			Map<String, Object> mapInterfaces = new HashMap<String, Object>();
			mapInterfaces.put("coreServiceAccounts", coreServiceAccounts);

			Utils.validateComponentInstance(mapInterfaces);

			if (anOriginalRequest == null)
				if (logger.isInfoEnabled())
					logger.logInfo(CLASS_NAME + "Original Request ISNULL");

			executeSteps(aBagSPJavaOrchestration);

			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.logInfo("*********  Error en " + e.getMessage(), e);
			}

			IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
			ErrorBlock eb = new ErrorBlock(-1, "Service is not available");
			wProcedureRespFinal.addResponseBlock(eb);
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
			wProcedureRespFinal.setReturnCode(-1);
			return wProcedureRespFinal;
		}
	}

	@Override
	public IProcedureResponse executeWSMethod(Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		IProcedureRequest anOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		IProcedureResponse responseProc = null;
		try {

			// consulta de stocks
			responseProc = queryProviderTransaction(anOriginalRequest, aBagSPJavaOrchestration);

			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, responseProc);

			if (Utils.flowError("updateProviderTransaction", responseProc)) {

				return responseProc;
			}

			return responseProc;
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.logInfo("*********  Error en " + e.getMessage(), e);
			}

			IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
			ErrorBlock eb = new ErrorBlock(-1, "Service is not available");
			wProcedureRespFinal.addResponseBlock(eb);
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
			wProcedureRespFinal.setReturnCode(-1);
			return wProcedureRespFinal;
		}
	}

	/**
	 * name executeProviderTransaction
	 * 
	 * @param anOriginalRequest
	 * @param aBag
	 * @return IProcedureResponse
	 */
	private IProcedureResponse queryProviderTransaction(IProcedureRequest anOriginalRequest, Map<String, Object> aBag) {

		AccountResponse wAccountResp = new AccountResponse();
		AccountRequest wAccountRequest = transformAccountRequest(anOriginalRequest.clone());

		try {
			wAccountRequest.setOriginalRequest(anOriginalRequest);
			wAccountResp = coreServiceAccounts.getAccountByCriteria(wAccountRequest, aBag, properties);
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}

		return transformAccountResponse(wAccountResp, aBag);
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
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> aBagSPJavaOrchestration) {

		return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
	}

	/******************
	 * Transformación de ProcedureRequest a AccountRequest
	 ********************/

	private AccountRequest transformAccountRequest(IProcedureRequest aRequest) {
		AccountRequest wAccountRequest = new AccountRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());
		if (aRequest.readValueParam("@i_id_operativo") != null)
			wAccountRequest.setIdOperation(aRequest.readValueParam("@i_id_operativo"));
		if (aRequest.readValueParam("@i_id_modulo") != null)
			wAccountRequest.setCodModule(Integer.parseInt(aRequest.readValueParam("@i_id_modulo")));
		if (aRequest.readValueParam("@i_id_criterio") != null)
			wAccountRequest.setCodCriteria(Integer.parseInt(aRequest.readValueParam("@i_id_criterio")));
		if (aRequest.readValueParam("@i_codigo0") != null)
			wAccountRequest.setCode0(aRequest.readValueParam("@i_codigo0"));
		if (aRequest.readValueParam("@i_codigo1") != null)
			wAccountRequest.setCode1(aRequest.readValueParam("@i_codigo1"));
		if (aRequest.readValueParam("@i_codigo2") != null)
			wAccountRequest.setCode2(aRequest.readValueParam("@i_codigo2"));
		if (aRequest.readValueParam("@i_codigo3") != null)
			wAccountRequest.setCode3(aRequest.readValueParam("@i_codigo3"));
		if (aRequest.readValueParam("@i_codigo4") != null)
			wAccountRequest.setCode4(aRequest.readValueParam("@i_codigo4"));
		if (aRequest.readValueParam("@i_codigo5") != null)
			wAccountRequest.setCode5(aRequest.readValueParam("@i_codigo5"));
		if (aRequest.readValueParam("@i_codigo6") != null)
			wAccountRequest.setCode6(aRequest.readValueParam("@i_codigo6"));
		if (aRequest.readValueParam("@i_codigo7") != null)
			wAccountRequest.setCode7(aRequest.readValueParam("@i_codigo7"));
		if (aRequest.readValueParam("@i_codigo8") != null)
			wAccountRequest.setCode8(aRequest.readValueParam("@i_codigo8"));
		if (aRequest.readValueParam("@i_codigo9") != null)
			wAccountRequest.setCode9(aRequest.readValueParam("@i_codigo9"));

		return wAccountRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformAccountResponse(AccountResponse aAccountResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response transformAccountResponse");

		if (aAccountResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, Utils.returnException(aAccountResponse.getMessages()));
			wProcedureResponse.addMessage(aAccountResponse.getReturnCode(), aAccountResponse.getMessageError());
		} else {
			// Agregar Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("CODIGO", ICTSTypes.SYBVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("NOMBRE", ICTSTypes.SYBVARCHAR, 30));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("DETALLE", ICTSTypes.SYBVARCHAR, 30));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("SERVICIO", ICTSTypes.SYBINT4, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("DESCSERVICIO", ICTSTypes.SYBVARCHAR, 30));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("COD_MONEDA", ICTSTypes.SYBINT4, 10));
			for (AccountDetail aAccountDetail : aAccountResponse.getAccountDetailCollection()) {
				IResultSetRow row = new ResultSetRow();

				row.addRowData(1, new ResultSetRowColumnData(false, aAccountDetail.getCodAccount().toString()));
				row.addRowData(2, new ResultSetRowColumnData(false, aAccountDetail.getClientName()));
				row.addRowData(3, new ResultSetRowColumnData(false, aAccountDetail.getDetail()));
				row.addRowData(4, new ResultSetRowColumnData(false, aAccountDetail.getCodServices().toString()));
				row.addRowData(5, new ResultSetRowColumnData(false, aAccountDetail.getDescService()));
				row.addRowData(6, new ResultSetRowColumnData(false, aAccountDetail.getCodCurrency().toString()));

				data.addRow(row);
			}

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock);

			wProcedureResponse.addParam("@o_num_operacion", ICTSTypes.SQLINT4, 1,
					aAccountResponse.getNumOperation().toString());
			wProcedureResponse.addParam("@o_fecha_operativa", ICTSTypes.SQLINT4, 1,
					aAccountResponse.getOperationDate().toString());

		}
		if (aAccountResponse.getCodError() != 0) {
			wProcedureResponse.addParam("@o_coderror", ICTSTypes.SQLINT4, 1, aAccountResponse.getCodError().toString());
			wProcedureResponse.addParam("@o_mensaje", ICTSTypes.SQLVARCHAR, 1, aAccountResponse.getMessageError());
		}
		wProcedureResponse.setReturnCode(aAccountResponse.getReturnCode());
		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;

	}

}
