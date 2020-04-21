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
import com.cobiscorp.ecobis.ib.application.dtos.LoanStatementRequest;
import com.cobiscorp.ecobis.ib.application.dtos.LoanStatementResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.LoanStatement;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceLoanStatement;

/**
 * @author wsanchez
 * @since Sep 12, 2014
 * @version 1.0.0
 */

@Component(name = "LoanStatementQueryOrchestationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "LoanStatementQueryOrchestationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "LoanStatementQueryOrchestationCore") })

public class LoanStatementQueryOrchestationCore extends QueryBaseTemplate {
	@Override
	protected IProcedureResponse validateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		// TODO Auto-generated method stub
		IProcedureResponse response = initProcedureResponse(anOriginalRequest);
		response.setReturnCode(0);
		return response;
	}

	@Reference(referenceInterface = ICoreServiceLoanStatement.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceLoanStatement coreService;
	ILogger logger = this.getLogger();

	public void bindCoreService(ICoreServiceLoanStatement service) {
		coreService = service;
	}

	public void unbindCoreService(ICoreServiceLoanStatement service) {
		coreService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		String messageError = null;
		String messageLog = null;
		String queryName = null;

		LoanStatementResponse aLoanStatementResponse = null;
		LoanStatementRequest aLoanStatementRequest = transformLoanStatementRequest(request.clone());

		try {

			messageError = "getLoanStament: ERROR EXECUTING SERVICE";
			messageLog = "getLoanStament " + aLoanStatementRequest.getProductNumber().getProductNumber();
			queryName = "getLoanStament";
			aLoanStatementResponse = coreService.getLoanStatement(aLoanStatementRequest);

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
		return transformProcedureResponse(aLoanStatementResponse);

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		// Valida Inyecci�n de dependencias

		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		Utils.validateComponentInstance(mapInterfaces);

		try {
			@SuppressWarnings("unused")
			IProcedureResponse response = executeStepsQueryBase(anOrginalRequest, aBagSPJavaOrchestration);
			return processResponse(anOrginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			return Utils.returnExceptionService(anOrginalRequest, e);
		} catch (CTSInfrastructureException e) {

			return Utils.returnExceptionService(anOrginalRequest, e);
		}

	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = (IProcedureResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION");
		return response;
	}

	private LoanStatementRequest transformLoanStatementRequest(IProcedureRequest aRequest) {
		LoanStatementRequest LoanStatementReq = new LoanStatementRequest();
		Product product = new Product();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		product.setProductNumber(aRequest.readValueParam("@i_banco"));

		LoanStatementReq.setProductNumber(product);
		LoanStatementReq.setCodeTransactionalIdentifier(aRequest.readValueParam("@i_operacion"));
		LoanStatementReq.setDateFormatId(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha")));
		LoanStatementReq.setSequential(Integer.parseInt(aRequest.readValueParam("@i_siguiente")));

		return LoanStatementReq;
	}

	private IProcedureResponse transformProcedureResponse(LoanStatementResponse aLoanStatementResponse) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		// Agregar Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("operationNumber", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("paymentDate", ICTSTypes.SQLVARCHAR, 30));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("movementType", ICTSTypes.SQLVARCHAR, 34));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("normalInterest", ICTSTypes.SQLMONEY, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("arrearsInterest", ICTSTypes.SQLMONEY, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("capital", ICTSTypes.SQLMONEY, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("others", ICTSTypes.SQLMONEY, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("amount", ICTSTypes.SQLMONEY, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("capitalBalance", ICTSTypes.SQLMONEY, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("period", ICTSTypes.SQLMONEY, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("tax", ICTSTypes.SQLMONEY, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("assured", ICTSTypes.SQLMONEY, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("description", ICTSTypes.SQLVARCHAR, 64));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("PreviousRat", ICTSTypes.SQLMONEY, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("previousStatus", ICTSTypes.SQLVARCHAR, 64));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("currentStatus", ICTSTypes.SQLVARCHAR, 64));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("currentRate", ICTSTypes.SQLMONEY, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("sequential", ICTSTypes.SQLMONEY, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("paymentType", ICTSTypes.SQLVARCHAR, 10));

		for (LoanStatement aLoanStatement : aLoanStatementResponse.getLoanStatementCollection()) {

			if (!IsValidLoanSatatementResponse(aLoanStatement))
				return null;

			IResultSetRow row = new ResultSetRow();

			row.addRowData(1, new ResultSetRowColumnData(false, "")); // productNumber
			row.addRowData(2, new ResultSetRowColumnData(false, aLoanStatement.getPaymentDate()));
			row.addRowData(3, new ResultSetRowColumnData(false, ""));
			row.addRowData(4, new ResultSetRowColumnData(false, aLoanStatement.getNormalInterest().toString()));
			row.addRowData(5, new ResultSetRowColumnData(false, aLoanStatement.getArrearsInterest().toString()));
			row.addRowData(6, new ResultSetRowColumnData(false, ""));
			row.addRowData(7, new ResultSetRowColumnData(false, ""));
			row.addRowData(8, new ResultSetRowColumnData(false, aLoanStatement.getAmount().toString()));
			row.addRowData(9, new ResultSetRowColumnData(false, ""));
			row.addRowData(10, new ResultSetRowColumnData(false, ""));
			row.addRowData(11, new ResultSetRowColumnData(false, ""));
			row.addRowData(12, new ResultSetRowColumnData(false, ""));
			row.addRowData(13, new ResultSetRowColumnData(false, ""));
			row.addRowData(14, new ResultSetRowColumnData(false, ""));
			row.addRowData(15, new ResultSetRowColumnData(false, ""));
			row.addRowData(16, new ResultSetRowColumnData(false, ""));
			row.addRowData(17, new ResultSetRowColumnData(false, ""));
			row.addRowData(18, new ResultSetRowColumnData(false, aLoanStatement.getSequential().toString()));
			row.addRowData(19, new ResultSetRowColumnData(false, aLoanStatement.getPaymentType()));

			data.addRow(row);
		}

		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

		wProcedureResponse.addResponseBlock(resultBlock);

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;

	}

	private boolean IsValidLoanSatatementResponse(LoanStatement aLoanStatementResponse) {
		String messageError = null;

		messageError = aLoanStatementResponse.getPaymentDate() == null ? "PaymentDate can't be null" : "OK";
		messageError = aLoanStatementResponse.getNormalInterest() == null ? "NormalInterest can't be null" : "OK";
		messageError = aLoanStatementResponse.getArrearsInterest() == null ? "ArrearsInterest can't be null" : "OK";
		messageError = aLoanStatementResponse.getAmount() == null ? "Amount can't be null" : "OK";
		messageError = aLoanStatementResponse.getSequential() == null ? "Sequential can't be null" : "OK";
		// messageError = aLoanStatementResponse.getPaymentType() == null ?
		// "PaymentType can't be null":"OK";

		if (!messageError.equals("OK"))
			throw new IllegalArgumentException(messageError);

		return true;
	}

}
