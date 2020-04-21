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
import com.cobiscorp.ecobis.ib.application.dtos.BalanceDetailPaymentRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BalanceDetailPaymentResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.BalanceDetailPayment;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceLoanBalanceDetail;

/**
 * @author kmezat
 * @since Oct 13, 2014
 * @version 1.0.0
 */

@Component(name = "LoanBalanceDetailPaymentQueryOrchestationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "LoanBalanceDetailPaymentQueryOrchestationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "LoanBalanceDetailPaymentQueryOrchestationCore") })

public class LoanBalanceDetailPaymentQueryOrchestationCore extends QueryBaseTemplate {

	@Reference(referenceInterface = ICoreServiceLoanBalanceDetail.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceLoanBalanceDetail coreService;
	ILogger logger = this.getLogger();

	public void bindCoreService(ICoreServiceLoanBalanceDetail service) {
		coreService = service;
	}

	public void unbindCoreService(ICoreServiceLoanBalanceDetail service) {
		coreService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	private BalanceDetailPaymentRequest transformBalanceDetailPaymentRequest(IProcedureRequest aRequest) {
		BalanceDetailPaymentRequest balanceDetailPaymentReq = new BalanceDetailPaymentRequest();
		Product product = new Product();
		product.setProductNumber(aRequest.readValueParam("@i_cta"));
		balanceDetailPaymentReq.setProductNumber(product);
		balanceDetailPaymentReq.setCodeTransactionalIdentifier(aRequest.readValueParam("@i_operacion"));
		balanceDetailPaymentReq.setProductId(Integer.parseInt(aRequest.readValueParam("@i_prod")));
		balanceDetailPaymentReq.setCurrencyID(Integer.parseInt(aRequest.readValueParam("@i_mon")));
		balanceDetailPaymentReq.setUserName(aRequest.readValueParam("@i_login"));
		balanceDetailPaymentReq.setValidateAccount(aRequest.readValueParam("@i_valida_des"));

		return balanceDetailPaymentReq;
	}

	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub

		String messageError = null;
		String messageLog = null;
		String queryName = null;
		BalanceDetailPaymentResponse aBalanceDetailPaymentResponse = null;
		BalanceDetailPaymentRequest aLoanDetailRequest = transformBalanceDetailPaymentRequest(request.clone());
		try {
			messageError = "getBalancDetail: ERROR EXECUTING SERVICE";
			messageLog = "getBalancDetail " + aLoanDetailRequest.getProductNumber().getProductNumber();
			queryName = "getBalancDetail";
			aBalanceDetailPaymentResponse = coreService.getBalanceDetail(aLoanDetailRequest);
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
		return transformProcedureResponse(aBalanceDetailPaymentResponse, aBagSPJavaOrchestration);
	}

	private IProcedureResponse transformProcedureResponse(BalanceDetailPaymentResponse aBalanceDetailPaymentResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		// GCO-Manejo de mensajes de Error 19-Dic-2014
		if (aBalanceDetailPaymentResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aBalanceDetailPaymentResponse.getMessages())); // COLOCA
																							// ERRORES
																							// COMO
																							// RESPONSE
																							// DE
																							// LA
																							// TRANSACCIÓN
			wProcedureResponse = Utils.returnException(aBalanceDetailPaymentResponse.getMessages());
		} else {
			// Agregar Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();
			metaData.addColumnMetaData(new ResultSetHeaderColumn("aditionalData", ICTSTypes.SQLVARCHAR, 20));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("productNumber", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("entityName", ICTSTypes.SQLVARCHAR, 34));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("operationType", ICTSTypes.SQLVARCHAR, 34));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("initialAmount", ICTSTypes.SQLMONEY, 8));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("monthlyPaymentDay", ICTSTypes.SQLVARCHAR, 16));// GCO-
																													// se
																													// modifica
																													// tipo
																													// de
																													// dato
																													// para
																													// coincida
																													// con
																													// el
																													// response
																													// del
																													// sp
			metaData.addColumnMetaData(new ResultSetHeaderColumn("status", ICTSTypes.SQLVARCHAR, 8));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("lastPaymentDate", ICTSTypes.SQLVARCHAR, 34));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("expirationDate", ICTSTypes.SQLVARCHAR, 34));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("executive", ICTSTypes.SQLVARCHAR, 34));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("initialDate", ICTSTypes.SQLVARCHAR, 34));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("arrearsDays", ICTSTypes.SQLINT1, 8));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("overdueCapital", ICTSTypes.SQLMONEY, 8));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("overdueInterest", ICTSTypes.SQLMONEY, 8));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("overdueArrearsValue", ICTSTypes.SQLMONEY, 8));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("overdueAnotherItems", ICTSTypes.SQLMONEY, 8));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("overdueTotal", ICTSTypes.SQLMONEY, 8));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("nextPaymentDate", ICTSTypes.SQLVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("nextPaymentValue", ICTSTypes.SQLMONEY, 8));
			// metaData.addColumnMetaData(new
			// ResultSetHeaderColumn("nextProjectedPaymentValue",
			// ICTSTypes.SQLMONEY, 8));

			metaData.addColumnMetaData(new ResultSetHeaderColumn("ordinaryInterestRate", ICTSTypes.SQLMONEY, 8));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("arrearsInterestRate", ICTSTypes.SQLMONEY, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("capitalBalance", ICTSTypes.SQLMONEY, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("totalBalance", ICTSTypes.SQLMONEY, 8));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("originalTerm", ICTSTypes.SQLVARCHAR, 34));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("sector", ICTSTypes.SQLVARCHAR, 34));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("operationDescription", ICTSTypes.SQLVARCHAR, 34));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("tax", ICTSTypes.SQLMONEY, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("totalAmountCancel", ICTSTypes.SQLMONEY, 10));

			metaData.addColumnMetaData(new ResultSetHeaderColumn("capital", ICTSTypes.SQLMONEY, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("interest", ICTSTypes.SQLMONEY, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("moratorium", ICTSTypes.SQLMONEY, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("insurance", ICTSTypes.SQLMONEY, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("other", ICTSTypes.SQLMONEY, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("desmoney", ICTSTypes.SQLVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("nextduedate", ICTSTypes.SQLVARCHAR, 10));

			for (BalanceDetailPayment aBalanceDetailPayment : aBalanceDetailPaymentResponse.getBalanceDetailList()) {

				if (!IsValidBalanceDetailPaymentResponse(aBalanceDetailPayment))
					return null;

				IResultSetRow row = new ResultSetRow();
				row.addRowData(1, new ResultSetRowColumnData(false, aBalanceDetailPayment.getAditionalData()));
				row.addRowData(2,
						new ResultSetRowColumnData(false, aBalanceDetailPayment.getProductNumber().getProductNumber()));
				row.addRowData(3, new ResultSetRowColumnData(false, aBalanceDetailPayment.getEntityName()));
				row.addRowData(4, new ResultSetRowColumnData(false, aBalanceDetailPayment.getOperationType()));
				row.addRowData(5,
						new ResultSetRowColumnData(false, aBalanceDetailPayment.getInitialAmount().toString()));
				row.addRowData(6,
						new ResultSetRowColumnData(false, aBalanceDetailPayment.getMonthlyPaymentDay().toString()));
				row.addRowData(7, new ResultSetRowColumnData(false, aBalanceDetailPayment.getStatus()));
				row.addRowData(8, new ResultSetRowColumnData(false, aBalanceDetailPayment.getLastPaymentDate()));
				row.addRowData(9, new ResultSetRowColumnData(false, aBalanceDetailPayment.getExpirationDate()));
				row.addRowData(10, new ResultSetRowColumnData(false, aBalanceDetailPayment.getExecutive()));
				row.addRowData(11, new ResultSetRowColumnData(false, aBalanceDetailPayment.getInitialDate()));
				row.addRowData(12,
						new ResultSetRowColumnData(false, aBalanceDetailPayment.getArrearsDays().toString()));
				row.addRowData(13,
						new ResultSetRowColumnData(false, aBalanceDetailPayment.getOverdueCapital().toString()));
				row.addRowData(14,
						new ResultSetRowColumnData(false, aBalanceDetailPayment.getOverdueInterest().toString()));
				row.addRowData(15,
						new ResultSetRowColumnData(false, aBalanceDetailPayment.getOverdueArrearsValue().toString()));
				row.addRowData(16,
						new ResultSetRowColumnData(false, aBalanceDetailPayment.getOverdueAnotherItems().toString()));
				row.addRowData(17,
						new ResultSetRowColumnData(false, aBalanceDetailPayment.getOverdueTotal().toString()));
				row.addRowData(18, new ResultSetRowColumnData(false, aBalanceDetailPayment.getNextPaymentDate()));
				row.addRowData(19,
						new ResultSetRowColumnData(false, aBalanceDetailPayment.getNextPaymentValue().toString()));
				// row.addRowData(20, new ResultSetRowColumnData(false,
				// aBalanceDetailPayment.getNextProjectedPaymentValue().toString()));
				row.addRowData(20,
						new ResultSetRowColumnData(false, aBalanceDetailPayment.getOrdinaryInterestRate().toString()));
				row.addRowData(21, new ResultSetRowColumnData(false,
						aBalanceDetailPayment.getArrearsInterestRate().toEngineeringString()));
				row.addRowData(22,
						new ResultSetRowColumnData(false, aBalanceDetailPayment.getCapitalBalance().toString()));
				row.addRowData(23,
						new ResultSetRowColumnData(false, aBalanceDetailPayment.getTotalBalance().toString()));
				row.addRowData(24, new ResultSetRowColumnData(false, aBalanceDetailPayment.getOriginalTerm()));
				row.addRowData(25, new ResultSetRowColumnData(false, aBalanceDetailPayment.getSector()));
				row.addRowData(26, new ResultSetRowColumnData(false, aBalanceDetailPayment.getOperationDescription()));
				row.addRowData(27, new ResultSetRowColumnData(false, aBalanceDetailPayment.getTax().toString()));
				row.addRowData(28,
						new ResultSetRowColumnData(false, aBalanceDetailPayment.getTotalAmountCancel().toString()));

				row.addRowData(29, new ResultSetRowColumnData(false, aBalanceDetailPayment.getCapital().toString()));
				row.addRowData(30, new ResultSetRowColumnData(false, aBalanceDetailPayment.getInterest().toString()));
				row.addRowData(31, new ResultSetRowColumnData(false, aBalanceDetailPayment.getMoratorium().toString()));
				row.addRowData(32, new ResultSetRowColumnData(false, aBalanceDetailPayment.getInsurance().toString()));
				row.addRowData(33, new ResultSetRowColumnData(false, aBalanceDetailPayment.getOther().toString()));
				row.addRowData(34, new ResultSetRowColumnData(false, aBalanceDetailPayment.getDesMoney().toString()));
				row.addRowData(35,
						new ResultSetRowColumnData(false, aBalanceDetailPayment.getNextDueDate().toString()));
				data.addRow(row);
			}
			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock);
		}
		wProcedureResponse.setReturnCode(aBalanceDetailPaymentResponse.getReturnCode());
		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}

	private boolean IsValidBalanceDetailPaymentResponse(BalanceDetailPayment aBalanceDetailPaymentResponse) {
		// TODO Auto-generated method stub
		String messageError = null;
		messageError = aBalanceDetailPaymentResponse.getProductNumber() == null ? "ProductNumber can't be null" : "OK";
		messageError = aBalanceDetailPaymentResponse.getInitialDate() == null ? "InitialDate can't be null" : "OK";
		messageError = aBalanceDetailPaymentResponse.getArrearsInterestRate() == null
				? "ArrearsInterestRate can't be null" : "OK";
		messageError = aBalanceDetailPaymentResponse.getInitialAmount() == null ? "InitialAmount can't be null" : "OK";
		messageError = aBalanceDetailPaymentResponse.getOrdinaryInterestRate() == null
				? "OrdinaryInterestRate can't be null" : "OK";
		messageError = aBalanceDetailPaymentResponse.getAditionalData() == null ? "AditionalData can't be null" : "OK";
		messageError = aBalanceDetailPaymentResponse.getEntityName() == null ? "EntityName can't be null" : "OK";
		messageError = aBalanceDetailPaymentResponse.getExecutive() == null ? "Executive can't be null" : "OK";
		messageError = aBalanceDetailPaymentResponse.getExpirationDate() == null ? "ExpirationDate can't be null"
				: "OK";
		messageError = aBalanceDetailPaymentResponse.getLastPaymentDate() == null ? "LastPaymentDate can't be null"
				: "OK";
		messageError = aBalanceDetailPaymentResponse.getNextPaymentDate() == null ? "NextPaymentDate can't be null"
				: "OK";

		messageError = aBalanceDetailPaymentResponse.getOperationDescription() == null
				? "OperationDescription can't be null" : "OK";
		messageError = aBalanceDetailPaymentResponse.getOperationType() == null ? "OperationType can't be null" : "OK";
		messageError = aBalanceDetailPaymentResponse.getOriginalTerm() == null ? "OriginalTerm can't be null" : "OK";
		messageError = aBalanceDetailPaymentResponse.getSector() == null ? "Sector can't be null" : "OK";
		messageError = aBalanceDetailPaymentResponse.getStatus() == null ? "Status can't be null" : "OK";
		messageError = aBalanceDetailPaymentResponse.getArrearsDays() == null ? "OgetArrearsDays can't be null" : "OK";
		messageError = aBalanceDetailPaymentResponse.getCapitalBalance() == null ? "CapitalBalance can't be null"
				: "OK";
		messageError = aBalanceDetailPaymentResponse.getInitialAmount() == null ? "InitialAmount can't be null" : "OK";
		messageError = aBalanceDetailPaymentResponse.getMonthlyPaymentDay() == null ? "MonthlyPaymentDay can't be null"
				: "OK";
		messageError = aBalanceDetailPaymentResponse.getNextPaymentValue() == null ? "NextPaymentValue can't be null"
				: "OK";
		// messageError =
		// aBalanceDetailPaymentResponse.getNextProjectedPaymentValue()== null ?
		// "NextProjectedPaymentValue can't be null":"OK";
		messageError = aBalanceDetailPaymentResponse.getOverdueAnotherItems() == null
				? "OverdueAnotherItems can't be null" : "OK";
		messageError = aBalanceDetailPaymentResponse.getOverdueCapital() == null ? "OverdueCapital can't be null"
				: "OK";
		messageError = aBalanceDetailPaymentResponse.getOverdueInterest() == null ? "OverdueInterest can't be null"
				: "OK";
		messageError = aBalanceDetailPaymentResponse.getOverdueTotal() == null ? "OverdueTotal can't be null" : "OK";
		messageError = aBalanceDetailPaymentResponse.getTax() == null ? "Tax can't be null" : "OK";
		messageError = aBalanceDetailPaymentResponse.getTotalBalance() == null ? "TotalBalance can't be null" : "OK";
		messageError = aBalanceDetailPaymentResponse.getTotalAmountCancel() == null ? "TotalAmountCancel can't be null"
				: "OK";

		if (!messageError.equals("OK"))
			throw new IllegalArgumentException(messageError);

		return true;

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub

		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
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
				(BalanceDetailPaymentResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION"),
				aBagSPJavaOrchestration);
		aBagSPJavaOrchestration.remove(RESPONSE_TRANSACTION);
		return response;

	}

}
