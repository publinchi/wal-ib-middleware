package com.cobiscorp.ecobis.orchestration.core.ib.transfers;

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
import com.cobiscorp.ecobis.ib.application.dtos.PaymentDetailsTransfInternationalRequest;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentDetailsTransfInternationalResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountOperation;
import com.cobiscorp.ecobis.ib.orchestration.dtos.PaymentDetailsTransfInternational;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServicePaymentDetailsTransfInternational;

@Component(name = "PaymentDetailsTransfInternationalOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = {
		@Property(name = "service.description", value = "PaymentDetailsTransfInternationalOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "PaymentDetailsTransfInternationalOrchestrationCore") })

public class PaymentDetailsTransfInternationalOrchestrationCore extends QueryBaseTemplate {
	@Reference(referenceInterface = ICoreServicePaymentDetailsTransfInternational.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServicePaymentDetailsTransfInternational coreService;
	Integer MODE;
	ILogger logger = LogFactory.getLogger(PaymentDetailsTransfInternationalOrchestrationCore.class);

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreServicePaymentDetailsTransfInternational service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreServicePaymentDetailsTransfInternational service) {
		coreService = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.base.query.QueryBaseTemplate#
	 * validateLocalExecution(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	protected IProcedureResponse validateLocalExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		IProcedureResponse response = new ProcedureResponseAS();
		response.setReturnCode(0);
		return response;
	}

	@Override
	protected IProcedureResponse executeQuery(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		String messageError = null;
		String messageLog = null;
		String queryName = null;

		PaymentDetailsTransfInternationalResponse aPaymentDetailsTransfInternationalResponse = new PaymentDetailsTransfInternationalResponse();
		PaymentDetailsTransfInternationalRequest aPaymentDetailsTransfInternationalRequest = transformPaymentDetailsTransfInternationalRequest(
				request.clone());

		try {
			messageError = "getProductNumber: ERROR EXECUTING SERVICE";
			messageLog = "getProductNumber " + aPaymentDetailsTransfInternationalRequest.getProductNumber();
			queryName = "getProductNumber";

			aPaymentDetailsTransfInternationalRequest.setOriginalRequest(request);
			aPaymentDetailsTransfInternationalResponse = coreService
					.getPaymentDetailsTransfInternational(aPaymentDetailsTransfInternationalRequest);
			MODE = aPaymentDetailsTransfInternationalRequest.getMode();
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
		return transformProcedureResponse(aPaymentDetailsTransfInternationalResponse, aBagSPJavaOrchestration);
	}

	private PaymentDetailsTransfInternationalRequest transformPaymentDetailsTransfInternationalRequest(
			IProcedureRequest aRequest) {
		PaymentDetailsTransfInternationalRequest aPaymentDetailsTransfInternationalRequest = new PaymentDetailsTransfInternationalRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		String messageError = null;

		messageError = aRequest.readValueParam("@i_opeban") == null ? " - @i_opeban can't be null" : "";
		messageError += aRequest.readValueParam("@i_fdate") == null ? " - @i_fdate can't be null" : "";
		messageError += aRequest.readValueParam("@i_modo") == null ? " - @i_modo can't be null" : "";

		if (!messageError.equals(""))
			throw new IllegalArgumentException(messageError);

		aPaymentDetailsTransfInternationalRequest.setProductNumber(aRequest.readValueParam("@i_opeban"));
		aPaymentDetailsTransfInternationalRequest
				.setDateFormatId(Integer.parseInt(aRequest.readValueParam("@i_fdate")));
		aPaymentDetailsTransfInternationalRequest.setMode(Integer.parseInt(aRequest.readValueParam("@i_modo")));
		aPaymentDetailsTransfInternationalRequest.setTypeOperation(aRequest.readValueParam("@i_tope"));
		aPaymentDetailsTransfInternationalRequest.setTypeTransaction(aRequest.readValueParam("@i_ttrn"));
		aPaymentDetailsTransfInternationalRequest.setTransaction(Integer.parseInt(aRequest.readValueParam("@t_trn")));

		return aPaymentDetailsTransfInternationalRequest;
	}

	private IProcedureResponse transformProcedureResponse(
			PaymentDetailsTransfInternationalResponse aPaymentDetailsTransfInternationalResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		// Agregar Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		if (MODE == 17) {
			metaData.addColumnMetaData(new ResultSetHeaderColumn("PaymentDate", ICTSTypes.SQLVARCHAR, 13));
		} else if (MODE == 2) {
			metaData.addColumnMetaData(new ResultSetHeaderColumn("TransactionSeuqential", ICTSTypes.SQLINT4, 3));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Number", ICTSTypes.SQLINT4, 11));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Term", ICTSTypes.SQLVARCHAR, 1));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("PaymentDetailSequential", ICTSTypes.SQLINT4, 3));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("PaymentType", ICTSTypes.SQLVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("PaymentTypeDetail", ICTSTypes.SQLVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("ExtraAmount", ICTSTypes.SQLDECIMAL, 21));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Currency", ICTSTypes.SQLVARCHAR, 3));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("CurrencyType", ICTSTypes.SQLDECIMAL, 85));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("LocalAmount", ICTSTypes.SQLDECIMAL, 21));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Detail", ICTSTypes.SQLVARCHAR, 255));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("PaymentDateSequential", ICTSTypes.SQLINT4, 3));
		} else if (MODE == 1) {
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Parameter", ICTSTypes.SQLVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("ParameterDescription", ICTSTypes.SQLVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Factor", ICTSTypes.SQLDECIMAL, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Concept", ICTSTypes.SQLVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Amount", ICTSTypes.SQLDECIMAL, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("SequentialOperation", ICTSTypes.SQLINT4, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Number", ICTSTypes.SQLINT4, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Term", ICTSTypes.SQLVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("DetailSequentialTransaction", ICTSTypes.SQLINT4, 64));
			metaData.addColumnMetaData(
					new ResultSetHeaderColumn("DetailSequentialPaymentDate", ICTSTypes.SQLDECIMAL, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("Detail", ICTSTypes.SQLVARCHAR, 64));
		}
		if (aPaymentDetailsTransfInternationalResponse.getReturnCode() == 0) {
			if (MODE == 17) {
				IResultSetRow row = new ResultSetRow();
				if (aPaymentDetailsTransfInternationalResponse.getPaymentDate() != null) {
					row.addRowData(1, new ResultSetRowColumnData(false,
							aPaymentDetailsTransfInternationalResponse.getPaymentDate()));
				}
				data.addRow(row);
			} else if (MODE == 2) {
				for (PaymentDetailsTransfInternational aPaymentDetailsTransfInternational : aPaymentDetailsTransfInternationalResponse
						.getPaymentDetailsCollection()) {
					if (!IsValidCheckbookResponse(aPaymentDetailsTransfInternational))
						return null;

					IResultSetRow row = new ResultSetRow();

					if (aPaymentDetailsTransfInternational.getTransactionSeuqential() != null) {
						row.addRowData(1, new ResultSetRowColumnData(false,
								aPaymentDetailsTransfInternational.getTransactionSeuqential().toString()));
					} else {
						row.addRowData(1, new ResultSetRowColumnData(false, " "));
					}
					if (aPaymentDetailsTransfInternational.getNumber() != null) {
						row.addRowData(2, new ResultSetRowColumnData(false,
								aPaymentDetailsTransfInternational.getNumber().toString()));
					} else {
						row.addRowData(2, new ResultSetRowColumnData(false, " "));
					}
					if (aPaymentDetailsTransfInternational.getTerm() != null) {
						row.addRowData(3,
								new ResultSetRowColumnData(false, aPaymentDetailsTransfInternational.getTerm()));
					} else {
						row.addRowData(3, new ResultSetRowColumnData(false, " "));
					}
					if (aPaymentDetailsTransfInternational.getPaymentDetailSequential() != null) {
						row.addRowData(4, new ResultSetRowColumnData(false,
								aPaymentDetailsTransfInternational.getPaymentDetailSequential().toString()));
					} else {
						row.addRowData(4, new ResultSetRowColumnData(false, " "));
					}
					if (aPaymentDetailsTransfInternational.getPaymentType() != null) {
						row.addRowData(5,
								new ResultSetRowColumnData(false, aPaymentDetailsTransfInternational.getPaymentType()));
					} else {
						row.addRowData(5, new ResultSetRowColumnData(false, " "));
					}
					if (aPaymentDetailsTransfInternational.getPaymentTypeDetail() != null) {
						row.addRowData(6, new ResultSetRowColumnData(false,
								aPaymentDetailsTransfInternational.getPaymentTypeDetail()));
					} else {
						row.addRowData(6, new ResultSetRowColumnData(false, " "));
					}
					if (aPaymentDetailsTransfInternational.getExtraAmount() != null) {
						row.addRowData(7, new ResultSetRowColumnData(false,
								aPaymentDetailsTransfInternational.getExtraAmount().toString()));
					} else {
						row.addRowData(7, new ResultSetRowColumnData(false, " "));
					}
					if (aPaymentDetailsTransfInternational.getCurrency() != null) {
						row.addRowData(8,
								new ResultSetRowColumnData(false, aPaymentDetailsTransfInternational.getCurrency()));
					} else {
						row.addRowData(8, new ResultSetRowColumnData(false, " "));
					}
					if (aPaymentDetailsTransfInternational.getCurrencyType() != null) {
						row.addRowData(9, new ResultSetRowColumnData(false,
								aPaymentDetailsTransfInternational.getCurrencyType().toString()));
					} else {
						row.addRowData(9, new ResultSetRowColumnData(false, " "));
					}
					if (aPaymentDetailsTransfInternational.getLocalAmount() != null) {
						row.addRowData(10, new ResultSetRowColumnData(false,
								aPaymentDetailsTransfInternational.getLocalAmount().toString()));
					} else {
						row.addRowData(10, new ResultSetRowColumnData(false, " "));
					}
					if (aPaymentDetailsTransfInternational.getDetail() != null) {
						row.addRowData(11,
								new ResultSetRowColumnData(false, aPaymentDetailsTransfInternational.getDetail()));
					} else {
						row.addRowData(11, new ResultSetRowColumnData(false, " "));
					}
					if (aPaymentDetailsTransfInternational.getPaymentDateSequential() != null) {
						row.addRowData(12, new ResultSetRowColumnData(false,
								aPaymentDetailsTransfInternational.getPaymentDateSequential().toString()));
					} else {
						row.addRowData(12, new ResultSetRowColumnData(false, " "));
					}

					data.addRow(row);
				}
			} else if (MODE == 1) {
				for (AccountOperation aAccountOperation : aPaymentDetailsTransfInternationalResponse
						.getAccountOperationCollection()) {
					IResultSetRow row = new ResultSetRow();

					if (aAccountOperation.getParameter() != null) {
						row.addRowData(1, new ResultSetRowColumnData(false, aAccountOperation.getParameter()));
					} else {
						row.addRowData(1, new ResultSetRowColumnData(false, " "));
					}
					if (aAccountOperation.getParameterDescription() != null) {
						row.addRowData(2,
								new ResultSetRowColumnData(false, aAccountOperation.getParameterDescription()));
					} else {
						row.addRowData(2, new ResultSetRowColumnData(false, " "));
					}
					if (aAccountOperation.getFactor() != null) {
						row.addRowData(3, new ResultSetRowColumnData(false, aAccountOperation.getFactor().toString()));
					} else {
						row.addRowData(3, new ResultSetRowColumnData(false, " "));
					}
					if (aAccountOperation.getConcept() != null) {
						row.addRowData(4, new ResultSetRowColumnData(false, aAccountOperation.getConcept()));
					} else {
						row.addRowData(4, new ResultSetRowColumnData(false, " "));
					}
					if (aAccountOperation.getAmount() != null) {
						row.addRowData(5, new ResultSetRowColumnData(false, aAccountOperation.getAmount().toString()));
					} else {
						row.addRowData(5, new ResultSetRowColumnData(false, " "));
					}
					if (aAccountOperation.getSequentialOperation() != null) {
						row.addRowData(6, new ResultSetRowColumnData(false,
								aAccountOperation.getSequentialOperation().toString()));
					} else {
						row.addRowData(6, new ResultSetRowColumnData(false, " "));
					}
					if (aAccountOperation.getNumber() != null) {
						row.addRowData(7, new ResultSetRowColumnData(false, aAccountOperation.getNumber().toString()));
					} else {
						row.addRowData(7, new ResultSetRowColumnData(false, " "));
					}
					if (aAccountOperation.getTerm() != null) {
						row.addRowData(8, new ResultSetRowColumnData(false, aAccountOperation.getTerm()));
					} else {
						row.addRowData(8, new ResultSetRowColumnData(false, " "));
					}
					if (aAccountOperation.getDetailSequentialTransaction() != null) {
						row.addRowData(9, new ResultSetRowColumnData(false,
								aAccountOperation.getDetailSequentialTransaction().toString()));
					} else {
						row.addRowData(9, new ResultSetRowColumnData(false, " "));
					}
					if (aAccountOperation.getDetailSequentialPaymentDate() != null) {
						row.addRowData(10,
								new ResultSetRowColumnData(false, aAccountOperation.getDetailSequentialPaymentDate()));
					} else {
						row.addRowData(10, new ResultSetRowColumnData(false, " "));
					}

					data.addRow(row);
				}
			}
			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock);
		} else {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aPaymentDetailsTransfInternationalResponse.getMessages()));
			wProcedureResponse = Utils.returnException(aPaymentDetailsTransfInternationalResponse.getMessages());
		}
		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Response Final -->" + wProcedureResponse.getProcedureResponseAsString());
		return wProcedureResponse;
	}

	private boolean IsValidCheckbookResponse(PaymentDetailsTransfInternational aPaymentDetailsTransfInternational) {
		return true;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOrginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreService", coreService);
		Utils.validateComponentInstance(mapInterfaces);

		try {
			aBagSPJavaOrchestration.put(LOG_MESSAGE, "CONSULTA DE TRANSFERENCIAS RECIBIDAS DEL EXTERIOR");
			executeStepsQueryBase(anOrginalRequest, aBagSPJavaOrchestration);
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
}
