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
import com.cobiscorp.ecobis.ib.application.dtos.NegociationLoanAccounRequest;
import com.cobiscorp.ecobis.ib.application.dtos.NegociationLoanAccounResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NegotiationLoanAccount;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceNegociationLoanAccount;

/**
 * @author kmezat
 * @since Nov 19, 2014
 * @version 1.0.0
 */
@Component(name = "NegociationLoanAccountQueryOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = " NegociationLoanAccountQueryOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "NegociationLoanAccountQueryOrchestrationCore") })
public class NegociationLoanAccountQueryOrchestrationCore extends QueryBaseTemplate {
	@Reference(referenceInterface = ICoreServiceNegociationLoanAccount.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceNegociationLoanAccount coreServiceLoanAccount;
	String OPERACION;
	ILogger logger = this.getLogger();

	public void bindCoreService(ICoreServiceNegociationLoanAccount service) {
		coreServiceLoanAccount = service;
	}

	public void unbindCoreService(ICoreServiceNegociationLoanAccount service) {
		coreServiceLoanAccount = null;
	}

	private NegociationLoanAccounRequest transformLoanAccountRequest(
			IProcedureRequest aRequest) {

		NegociationLoanAccounRequest loanAccountReq = new NegociationLoanAccounRequest();

		Product product = new Product();
		loanAccountReq.setOperation(aRequest.readValueParam("@i_operacion"));				
		loanAccountReq.setLoanNumber(aRequest.readValueParam("@i_banco"));
		loanAccountReq.setUserName(aRequest.readValueParam("@i_login"));
		loanAccountReq.setCurrencyId(0);// (Integer.parseInt(aRequest.readValueParam("@i_mon")));
		product.setProductNumber(aRequest.readValueParam("@i_cta"));

		loanAccountReq.setCompleteQuota(aRequest
				.readValueParam("@i_cuota_completa"));
		loanAccountReq.setChargeRate(aRequest.readValueParam("@i_tipo_cobro"));
		loanAccountReq.setReductionRate(aRequest
				.readValueParam("@i_tipo_reduccion"));
		loanAccountReq.setPaymentEffect(aRequest
				.readValueParam("@i_efecto_pago"));
		loanAccountReq.setPriorityRate(aRequest
				.readValueParam("@i_tipo_prioridad"));
		loanAccountReq.setAdvancePayment(aRequest
				.readValueParam("@i_aceptar_anticipos"));
		if (aRequest.readValueParam("@i_operacion").equals("D"))
			loanAccountReq.setTransactionId(Integer.parseInt(aRequest
					.readValueParam("@i_transaction_id")));

		return loanAccountReq;

	}

	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {

		String messageError = null;
		String messageLog = null;
		String queryName = null;
		NegociationLoanAccounResponse aLoanAccountResponse = null;
		NegociationLoanAccounRequest aLoanAccountRequest = transformLoanAccountRequest(request
				.clone());

		try {
			messageError = "getLoanAccount: ERROR EXECUTING SERVICE";
			messageLog = "getLoanAccount "
					+ aLoanAccountRequest.getLoanNumber();
			queryName = "getLoanAccount";

			aLoanAccountRequest.setOriginalRequest(request);
			aLoanAccountResponse = coreServiceLoanAccount
					.GetNegociationLoanAccount(aLoanAccountRequest);
			OPERACION = aLoanAccountRequest.getOperation();

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
		return transformProcedureResponse(aLoanAccountResponse);
	}

	private IProcedureResponse transformProcedureResponse(NegociationLoanAccounResponse aLoanAccountResponse) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		if (aLoanAccountResponse.getReturnCode() != 0) {
			wProcedureResponse.setReturnCode(aLoanAccountResponse.getReturnCode());
			return wProcedureResponse;
		}

		if (OPERACION.equals("M")) {

			if (aLoanAccountResponse.getSuccess()) {
				wProcedureResponse.setReturnCode(0);

			} else
				wProcedureResponse.setReturnCode(1);

		} else if (OPERACION.equals("N")) {
			metaData.addColumnMetaData(new ResultSetHeaderColumn("account", ICTSTypes.SQLVARCHAR, 20));
		} else if (OPERACION.equals("P")) {
			metaData.addColumnMetaData(new ResultSetHeaderColumn("chargeRate", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("advancePayment", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("reductionRate", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("aplicationRate", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("completeQuota", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("dateEnd", ICTSTypes.SQLDATETIME, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("priorityRate", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("paymentEffect", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("currencyId", ICTSTypes.SQLINT1, 2));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("currencyName", ICTSTypes.SQLVARCHAR, 10));

		} else if (OPERACION.equals("D")) {

			metaData.addColumnMetaData(new ResultSetHeaderColumn("quota", ICTSTypes.SQLMONEY, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("concept", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("state", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("amount", ICTSTypes.SQLMONEY, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("amountMN", ICTSTypes.SQLMONEY, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("currencyId", ICTSTypes.SQLINT1, 10));

		}

		if (OPERACION.equals("N")) {

			IResultSetRow row = new ResultSetRow();
			if (aLoanAccountResponse.getAccount() != null) {
				row.addRowData(1, new ResultSetRowColumnData(false, aLoanAccountResponse.getAccount()));
			}
			data.addRow(row);
		} else if (OPERACION.equals("P") || OPERACION.equals("D")) {

			aLoanAccountResponse.setReturnCode(aLoanAccountResponse.getReturnCode());

			for (NegotiationLoanAccount aNegotiationLoanAccount : aLoanAccountResponse.getNegotiationDateList()) {
				if (OPERACION.equals("P")) {

					IResultSetRow row = new ResultSetRow();
					if (aNegotiationLoanAccount.getChargeRate() != null) {
						row.addRowData(1, new ResultSetRowColumnData(false, aNegotiationLoanAccount.getChargeRate()));
					} else {
						row.addRowData(1, new ResultSetRowColumnData(false, " "));
					}
					if (aNegotiationLoanAccount.getAdvancePayment() != null) {
						row.addRowData(2,
								new ResultSetRowColumnData(false, aNegotiationLoanAccount.getAdvancePayment()));
					} else {
						row.addRowData(2, new ResultSetRowColumnData(false, " "));
					}
					if (aNegotiationLoanAccount.getReductionRate() != null) {
						row.addRowData(3,
								new ResultSetRowColumnData(false, aNegotiationLoanAccount.getReductionRate()));
					} else {
						row.addRowData(3, new ResultSetRowColumnData(false, " "));
					}
					if (aNegotiationLoanAccount.getAplicationRate() != null) {
						row.addRowData(4,
								new ResultSetRowColumnData(false, aNegotiationLoanAccount.getAplicationRate()));
					} else {
						row.addRowData(4, new ResultSetRowColumnData(false, " "));
					}
					if (aNegotiationLoanAccount.getCompleteQuota() != null) {
						row.addRowData(5,
								new ResultSetRowColumnData(false, aNegotiationLoanAccount.getCompleteQuota()));
					} else {
						row.addRowData(5, new ResultSetRowColumnData(false, " "));
					}
					if (aNegotiationLoanAccount != null) {
						row.addRowData(6, new ResultSetRowColumnData(false, " "));
					}
					if (aNegotiationLoanAccount.getPriorityRate() != null) {
						row.addRowData(7, new ResultSetRowColumnData(false, aNegotiationLoanAccount.getPriorityRate()));
					} else {
						row.addRowData(7, new ResultSetRowColumnData(false, " "));
					}
					if (aNegotiationLoanAccount.getPaymentEffect() != null) {
						row.addRowData(8,
								new ResultSetRowColumnData(false, aNegotiationLoanAccount.getPaymentEffect()));
					} else {
						row.addRowData(8, new ResultSetRowColumnData(false, " "));
					}
					if (aNegotiationLoanAccount.getCurrencyId() != null) {
						row.addRowData(9,
								new ResultSetRowColumnData(false, aNegotiationLoanAccount.getCurrencyId().toString()));
					} else {
						row.addRowData(9, new ResultSetRowColumnData(false, " "));
					}
					if (aNegotiationLoanAccount.getCurrencyName() != null) {
						row.addRowData(10,
								new ResultSetRowColumnData(false, aNegotiationLoanAccount.getCurrencyName()));
					} else {
						row.addRowData(10, new ResultSetRowColumnData(false, " "));
					}

					data.addRow(row);
				} else if (OPERACION.equals("D")) {
					if (!IsValidLoanAccountResponse(aNegotiationLoanAccount))
						return null;

					IResultSetRow row = new ResultSetRow();
					if (aNegotiationLoanAccount.getQuota() != null) {
						row.addRowData(1,
								new ResultSetRowColumnData(false, aNegotiationLoanAccount.getQuota().toString()));
					} else {
						row.addRowData(1, new ResultSetRowColumnData(false, " "));
					}
					if (aNegotiationLoanAccount.getConcept() != null) {
						row.addRowData(2, new ResultSetRowColumnData(false, aNegotiationLoanAccount.getConcept()));
					} else {
						row.addRowData(2, new ResultSetRowColumnData(false, " "));
					}
					if (aNegotiationLoanAccount.getState() != null) {
						row.addRowData(3, new ResultSetRowColumnData(false, aNegotiationLoanAccount.getState()));
					} else {
						row.addRowData(3, new ResultSetRowColumnData(false, " "));
					}
					if (aNegotiationLoanAccount.getAmount() != null) {
						row.addRowData(4,
								new ResultSetRowColumnData(false, aNegotiationLoanAccount.getAmount().toString()));
					} else {
						row.addRowData(4, new ResultSetRowColumnData(false, " "));
					}
					if (aNegotiationLoanAccount.getAmountMN() != null) {
						row.addRowData(5,
								new ResultSetRowColumnData(false, aNegotiationLoanAccount.getAmountMN().toString()));
					} else {
						row.addRowData(5, new ResultSetRowColumnData(false, " "));
					}
					if (aNegotiationLoanAccount.getCurrencyId() != null) {
						row.addRowData(6,
								new ResultSetRowColumnData(false, aNegotiationLoanAccount.getCurrencyId().toString()));
					} else {
						row.addRowData(6, new ResultSetRowColumnData(false, " "));
					}
					data.addRow(row);
				}
			}
		}
		/*
		 * if(aLoanAccountResponse.getReturnCode() != 0){ ((IProcedureResponse)
		 * aLoanAccountResponse).addMessage(aLoanAccountResponse.getReturnCode()
		 * , "VERIFICAR PARAMETRIZACION DE DPF DETAIL"); }
		 * aLoanAccountResponse.setReturnCode(aLoanAccountResponse.getReturnCode
		 * ());
		 */
		IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
		wProcedureResponse.addResponseBlock(resultBlock);

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;

	}

	private boolean IsValidLoanAccountResponse(NegotiationLoanAccount aLoanAccountResponse) {

		String messageError = null;
		messageError = aLoanAccountResponse.getAccount() == null ? "Accounnt can't be null" : "OK";
		messageError = aLoanAccountResponse.getChargeRate() == null ? "ChargeRate can't be null" : "OK";
		messageError = aLoanAccountResponse.getAplicationRate() == null ? "AplicationRate can't be null" : "OK";
		messageError = aLoanAccountResponse.getAdvancePayment() == null ? "AdvancePayment can't be null" : "OK";
		messageError = aLoanAccountResponse.getCompleteQuota() == null ? "CompleteQuota can't be null" : "OK";
		messageError = aLoanAccountResponse.getCurrencyName() == null ? "CurrencyName can't be null" : "OK";
		messageError = aLoanAccountResponse.getPaymentEffect() == null ? "PaymentEffect can't be null" : "OK";
		messageError = aLoanAccountResponse.getPriorityRate() == null ? "PriorityRate can't be null" : "OK";
		messageError = aLoanAccountResponse.getReductionRate() == null ? "ReductionRate can't be null" : "OK";
		messageError = aLoanAccountResponse.getCurrencyId() == null ? "CurrencyId can't be null" : "OK";
		messageError = aLoanAccountResponse.getQuota() == null ? "Quota can't be null" : "OK";
		messageError = aLoanAccountResponse.getAmount() == null ? "Amount can't be null" : "OK";
		messageError = aLoanAccountResponse.getAmountMN() == null ? "AmountMN can't be null" : "OK";
		messageError = aLoanAccountResponse.getCurrencyId() == null ? "CurrencyId can't be null" : "OK";
		messageError = aLoanAccountResponse.getState() == null ? "State can't be null" : "OK";

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
				(NegociationLoanAccounResponse) aBagSPJavaOrchestration.get("RESPONSE_TRANSACTION"));
		aBagSPJavaOrchestration.remove(RESPONSE_TRANSACTION);
		return response;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}
}
