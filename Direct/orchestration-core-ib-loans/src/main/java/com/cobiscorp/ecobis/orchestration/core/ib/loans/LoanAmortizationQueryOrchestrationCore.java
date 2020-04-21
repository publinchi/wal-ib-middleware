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
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.LoanAmortization;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.application.dtos.LoanAmortizationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.LoanAmortizationResponse;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceLoanAmortizationQuery;

/**
 * @author mvelez
 * @since Sep 12, 2014
 * @version 1.0.0
 */
@Component(name = "LoanAmortizationQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "LoanAmortizationQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "LoanAmortizationQueryOrchestrationCore") })

public class LoanAmortizationQueryOrchestrationCore extends QueryBaseTemplate {
	@Override
	protected IProcedureResponse validateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		// TODO Auto-generated method stub
		IProcedureResponse response = initProcedureResponse(anOriginalRequest);
		response.setReturnCode(0);
		return response;
	}

	ILogger logger = this.getLogger();

	@Reference(referenceInterface = ICoreServiceLoanAmortizationQuery.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceLoanAmortizationQuery coreService;

	public void bindCoreService(ICoreServiceLoanAmortizationQuery service) {
		coreService = service;
	}

	public void unbindCoreService(ICoreServiceLoanAmortizationQuery service) {
		coreService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	/*******************************************************************************/
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// Valida InyecciÃ³n de dependencias
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		Utils.validateComponentInstance(mapInterfaces);

		try {
			return executeStepsQueryBase(anOrginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		}
	}

	/*******************************************************************************/
	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		String messageError = null;
		String messageLog = null;
		String queryName = null;
		LoanAmortizationResponse loanAmortizationResponse = null;
		// Transform the response
		LoanAmortizationRequest loanAmortizationRequest = transformloanAmortizationRequest(request.clone());
		try {
			messageError = "GetLoanAmortization: ERROR EXECUTING SERVICE";
			messageLog = "Get Loan Amortization " + loanAmortizationRequest.getProduct().getProductNumber();
			queryName = "GetLoanAmortization";
			loanAmortizationResponse = coreService.GetLoanAmortization(loanAmortizationRequest);

		} catch (CTSServiceException e) {
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
		aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, loanAmortizationResponse);
		return processResponse(request, aBagSPJavaOrchestration);
	}

	/*******************************************************************************/
	/*
	 * Transform a Procedure Request in LoanAmortizationRequest
	 */
	private LoanAmortizationRequest transformloanAmortizationRequest(IProcedureRequest aRequest) {
		LoanAmortizationRequest loanAmortizationReq = new LoanAmortizationRequest();

		Product product = new Product();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		product.setProductNumber(aRequest.readValueParam("@i_banco"));
		loanAmortizationReq.setProduct(product);
		loanAmortizationReq.setCodeTransactionalIdentifier(aRequest.readValueParam("@i_operacion"));
		loanAmortizationReq.setDateFormatId(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha")));
		loanAmortizationReq.setSequential(Integer.parseInt(aRequest.readValueParam("@i_dividendo")));

		return loanAmortizationReq;
	}

	/*******************************************************************************/
	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = transformProcedureResponse(
				(LoanAmortizationResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION"),
				aBagSPJavaOrchestration);
		aBagSPJavaOrchestration.remove(RESPONSE_TRANSACTION);
		return response;
	}

	/*******************************************************************************/
	private IProcedureResponse transformProcedureResponse(LoanAmortizationResponse loanAmortizationResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		// if (!IsValidLoanAmortizationResponse(loanAmortizationResponse))
		// return null;

		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		if (loanAmortizationResponse.getReturnCode() != 0) {

			wProcedureResponse = Utils.returnException(loanAmortizationResponse.getMessages());
			wProcedureResponse.setReturnCode(loanAmortizationResponse.getReturnCode());
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, wProcedureResponse);

			return wProcedureResponse;

		} else {
			if (logger.isDebugEnabled())
				logger.logDebug("Transform Procedure Response");
			// Agregar Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("operation", ICTSTypes.SQLINT4, 0));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("dividend", ICTSTypes.SQLINT2, 0));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("date", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("capital", ICTSTypes.SQLMONEY4, 0));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("interest", ICTSTypes.SQLMONEY4, 0));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("mora", ICTSTypes.SQLMONEY4, 0));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("tax", ICTSTypes.SQLMONEY4, 0));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("insurance", ICTSTypes.SQLMONEY4, 0));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("others", ICTSTypes.SQLMONEY4, 0));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("capitalAmount", ICTSTypes.SQLMONEY4, 0));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("adjustment", ICTSTypes.SQLMONEY4, 0));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("state", ICTSTypes.SQLVARCHAR, 24));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("payment", ICTSTypes.SQLMONEY4, 0));

			// LoanAmortization aLoanAmortization = new LoanAmortization();

			for (LoanAmortization aLoanAmortization : loanAmortizationResponse.getLoanAmortizationCollection()) {
				Product product = aLoanAmortization.getOperationNumber();

				if (!IsValidLoanAmortizationResponse(aLoanAmortization))
					return null;

				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, product.getProductNumber().toString()));
				row.addRowData(2, new ResultSetRowColumnData(false, aLoanAmortization.getDividend().toString()));
				row.addRowData(3, new ResultSetRowColumnData(false, aLoanAmortization.getDate()));
				row.addRowData(4, new ResultSetRowColumnData(false, aLoanAmortization.getCapital().toString()));
				row.addRowData(5, new ResultSetRowColumnData(false, aLoanAmortization.getInterest().toString()));
				row.addRowData(6, new ResultSetRowColumnData(false, aLoanAmortization.getMora().toString()));
				row.addRowData(7, new ResultSetRowColumnData(false, aLoanAmortization.getTax().toString()));
				row.addRowData(8, new ResultSetRowColumnData(false, aLoanAmortization.getInsurance().toString()));
				row.addRowData(9, new ResultSetRowColumnData(false, aLoanAmortization.getOthers().toString()));
				row.addRowData(10, new ResultSetRowColumnData(false, aLoanAmortization.getCapitalAmount().toString()));
				row.addRowData(11, new ResultSetRowColumnData(false, aLoanAmortization.getAdjustment().toString()));
				row.addRowData(12, new ResultSetRowColumnData(false, aLoanAmortization.getState()));
				row.addRowData(13, new ResultSetRowColumnData(false, aLoanAmortization.getPayment().toString()));
				data.addRow(row);
			} // for

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock);
		}

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;

	}

	/*******************************************************************************/
	private boolean IsValidLoanAmortizationResponse(LoanAmortization loanAmortization) {
		String messageError = null;
		String msgErr = null;
		// messageError =
		// loanAmortization.getProductNumber().getProductNumber()==null ?
		// "ProductNumber can't be null":"OK";
		messageError = loanAmortization.getDividend() == null ? " Dividend can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = loanAmortization.getDate() == null ? " Date can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = loanAmortization.getCapital() == null ? " Capital can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = loanAmortization.getInterest() == null ? " Interest can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = loanAmortization.getMora() == null ? " Mora can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = loanAmortization.getTax() == null ? " Tax can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = loanAmortization.getInsurance() == null ? " Insurance can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = loanAmortization.getOthers() == null ? " Others can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = loanAmortization.getCapitalAmount() == null ? " CapitalAmount can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = loanAmortization.getAdjustment() == null ? " Adjustment can't be null," : "OK";
		msgErr = msgErr + messageError;
		messageError = loanAmortization.getState() == null ? " State can't be null" : "OK";
		msgErr = msgErr + messageError;

		if (!messageError.equals("OK"))
			// throw new IllegalArgumentException(messageError);
			throw new IllegalArgumentException(msgErr);
		return true;
	}
}