package com.cobiscorp.ecobis.orchestration.core.ib.query.detail;

import java.util.HashMap;
import java.util.Map;

import com.cobiscorp.ecobis.ib.orchestration.dtos.ExpensesAccount;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ExpensesAccountRequest;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ExpensesAccountResponse;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.IExpensesAccounts;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
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
import com.cobiscorp.ecobis.ib.application.dtos.EnquiriesDetailRequest;
import com.cobiscorp.ecobis.ib.application.dtos.EnquiriesDetailResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.EnquiriesDetail;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceEnquiriesDetail;

/**
 * Enquiries Detail
 * 
 * @since Dic 16, 2021
 * @author jvelasquez
 * @version 1.0.0
 * 
 */
@Component(name = "EnquiriesDetailOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "EnquiriesDetailOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "EnquiriesDetailOrchestrationCore") })
public class EnquiriesDetailOrchestrationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(EnquiriesDetailOrchestrationCore.class);
	private static final String CLASS_NAME = "EnquiriesDetailOrchestrationCore--->";
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";

	/**
	 * Read configuration of parent component
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = IExpensesAccounts.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindExpensesAccounts", unbind = "unbindExpensesAccounts")
	protected IExpensesAccounts coreExpensesAccounts;

	/**
	 * Instance Service Interface
	 * 
	 * @param service
	 */
	public void bindExpensesAccounts(IExpensesAccounts service) {
		coreExpensesAccounts = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	public void unbindExpensesAccounts(IExpensesAccounts service) {
		coreExpensesAccounts = null;
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
			mapInterfaces.put("coreExpensesAccounts", coreExpensesAccounts);

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

	private IProcedureResponse executeSteps(Map<String, Object> aBag) {

		IProcedureRequest anOriginalRequest = (IProcedureRequest) aBag.get(ORIGINAL_REQUEST);
		IProcedureResponse responseProc = null;
		try {

			// consulta de subtipos

			responseProc = queryExecution(anOriginalRequest, aBag);

			aBag.put(RESPONSE_TRANSACTION, responseProc);

			if (Utils.flowError("queryExecution", responseProc)) {

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
	private IProcedureResponse queryExecution(IProcedureRequest anOriginalRequest, Map<String, Object> aBag) {

		ExpensesAccountResponse wExpensesAccountResponse = new ExpensesAccountResponse();
		ExpensesAccountRequest wExpensesAccountRequest = transformExpensesAccountRequest(anOriginalRequest.clone());

		try {
			wExpensesAccountRequest.setOriginalRequest(anOriginalRequest);
			wExpensesAccountResponse = coreExpensesAccounts.getExpensesAccounts(wExpensesAccountRequest);
		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}

		return transformExpensesAccountResponse(wExpensesAccountResponse, aBag);
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
	 * Transformación de ProcedureRequest a StockRequest
	 ********************/

	private ExpensesAccountRequest transformExpensesAccountRequest(IProcedureRequest aRequest) {
		ExpensesAccountRequest wExpensesAccountRequest = new ExpensesAccountRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());


		wExpensesAccountRequest.setOperation(aRequest.readValueParam("@i_operacion"));
		wExpensesAccountRequest.setExpensesAccountId(Integer.parseInt(aRequest.readValueParam("@i_cta_gasto_id")));
		wExpensesAccountRequest.setMasterAccount(aRequest.readValueParam("@i_cuenta_principal"));
		wExpensesAccountRequest.setGroupCode(Integer.parseInt(aRequest.readValueParam("@i_codigo_grupo")));
		if(null != aRequest.readValueParam("@i_saldo")){
			wExpensesAccountRequest.setBalance(new Double(aRequest.readValueParam("@i_saldo")));
		}

		return wExpensesAccountRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformExpensesAccountResponse(ExpensesAccountResponse aExpensesAccountResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		if (aExpensesAccountResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aExpensesAccountResponse.getMessages()));

		} else {

			// Agregar Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("CUENTA ORIGEN", ICTSTypes.SYBVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("CUENTA", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("NOMBRE COMPLETO", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("SALDO", ICTSTypes.SYBVARCHAR, 128));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("EMAIL", ICTSTypes.SYBVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("GRUPO", ICTSTypes.SYBINT4, 128));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("TARJETA", ICTSTypes.SYBVARCHAR, 128));


			for (ExpensesAccount aExpensesAccount : aExpensesAccountResponse.getExpensesAccountList()) {

				IResultSetRow row = new ResultSetRow();

				row.addRowData(1, new ResultSetRowColumnData(false,
						aExpensesAccount.getMasterAccountNumber() == null ? "" : aExpensesAccount.getMasterAccountNumber()));
				row.addRowData(2,
						new ResultSetRowColumnData(false, aExpensesAccount.getExpensesAccountNumber() == null ? "" : aExpensesAccount.getExpensesAccountNumber()));
				row.addRowData(3, new ResultSetRowColumnData(false,
						aExpensesAccount.getOwnerAccountName() == null ? "" :aExpensesAccount.getOwnerAccountName()));
				/*row.addRowData(4, new ResultSetRowColumnData(false,
						aExpensesAccount. == null ? "" : aDetail.getCheckbookTipe()));*/
				row.addRowData(5, new ResultSetRowColumnData(false,
						aExpensesAccount.getEmail() == null ? "" : aExpensesAccount.getEmail()));
				/*row.addRowData(6,
						new ResultSetRowColumnData(false, aExpensesAccount.getGroupCode() == null ? "" : aExpensesAccount.getGroupCode()));
				row.addRowData(7,
						new ResultSetRowColumnData(false, ""));*/
				data.addRow(row);
			}

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock);

		}
		wProcedureResponse.setReturnCode(aExpensesAccountResponse.getReturnCode());
		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;

	}

}
