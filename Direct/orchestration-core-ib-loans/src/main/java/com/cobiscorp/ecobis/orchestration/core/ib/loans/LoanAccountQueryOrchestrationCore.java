package com.cobiscorp.ecobis.orchestration.core.ib.loans;

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
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.LoanAccountRequest;
import com.cobiscorp.ecobis.ib.application.dtos.LoanAccountResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.LoanAccount;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceLoanAccount;

/**
 * @author kmezat
 * @since Nov 19, 2014
 * @version 1.0.0
 */
@Component(name = "LoanAccountQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "LoanAccountQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "LoanAccountQueryOrchestrationCore") })

public class LoanAccountQueryOrchestrationCore extends QueryBaseTemplate {

	@Reference(referenceInterface = ICoreServiceLoanAccount.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceLoanAccount coreServiceLoanAccount;
	ILogger logger = this.getLogger();

	public void bindCoreService(ICoreServiceLoanAccount service) {
		coreServiceLoanAccount = service;
	}

	public void unbindCoreService(ICoreServiceLoanAccount service) {
		coreServiceLoanAccount = null;
	}

	private LoanAccountRequest transformLoanAccountRequest(IProcedureRequest aRequest) {

		LoanAccountRequest loanAccountReq = new LoanAccountRequest();

		Product product = new Product();
		loanAccountReq.setLoanNumber(aRequest.readValueParam("@i_banco"));
		loanAccountReq.setUserName(aRequest.readValueParam("@i_login"));
		loanAccountReq.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));
		product.setProductNumber(aRequest.readValueParam("@i_cta"));

		return loanAccountReq;
	}

	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {

		String messageError = null;
		String messageLog = null;
		String queryName = null;
		LoanAccountResponse aLoanAccountResponse = null;
		LoanAccountRequest aLoanAccountRequest = transformLoanAccountRequest(request.clone());

		try {
			messageError = "getLoanAccount: ERROR EXECUTING SERVICE";
			messageLog = "getLoanAccount " + aLoanAccountRequest.getLoanNumber();
			queryName = "getLoanAccount";
			aLoanAccountResponse = coreServiceLoanAccount.GetLoanAccount(aLoanAccountRequest);
		}

		catch (CTSServiceException e) {
			e.printStackTrace();
			if (logger.isDebugEnabled())
				logger.logDebug(messageError);
			return null;
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
			if (logger.isDebugEnabled())
				logger.logDebug(messageError);
			return null;
		}

		aBagSPJavaOrchestration.put(LOG_MESSAGE, messageLog);
		aBagSPJavaOrchestration.put(QUERY_NAME, queryName);
		return transformProcedureResponse(aLoanAccountResponse);
	}

	private IProcedureResponse transformProcedureResponse(LoanAccountResponse aLoanAccountResponse) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("account", ICTSTypes.SQLVARCHAR, 20));

		LoanAccount aLoanAccount = new LoanAccount();
		aLoanAccount.setAccount(aLoanAccountResponse.getAccount());
		if (!IsValidLoanAccountResponse(aLoanAccount))
			return null;
		IResultSetRow row = new ResultSetRow();
		row.addRowData(1, new ResultSetRowColumnData(false, aLoanAccount.getAccount()));
		data.addRow(row);
		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
		wProcedureResponse.addResponseBlock(resultBlock);

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;

	}

	private boolean IsValidLoanAccountResponse(LoanAccount aLoanAccountResponse) {

		String messageError = null;
		messageError = aLoanAccountResponse.getAccount() == null ? "Accounnt can't be null" : "OK";

		if (!messageError.equals("OK"))
			throw new IllegalArgumentException(messageError);
		return true;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreServiceLoanAccount", coreServiceLoanAccount);
		Utils.validateComponentInstance(mapInterfaces);

		try {
			// IProcedureResponse response =
			// executeStepsQueryBase(anOrginalRequest, aBagSPJavaOrchestration);
			return executeStepsQueryBase(anOrginalRequest, aBagSPJavaOrchestration);
			// return processResponse(anOrginalRequest,
			// aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {

			return Utils.returnExceptionService(anOrginalRequest, e);
		}
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = transformProcedureResponse(
				(LoanAccountResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION"));
		aBagSPJavaOrchestration.remove(RESPONSE_TRANSACTION);
		return response;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}
}
