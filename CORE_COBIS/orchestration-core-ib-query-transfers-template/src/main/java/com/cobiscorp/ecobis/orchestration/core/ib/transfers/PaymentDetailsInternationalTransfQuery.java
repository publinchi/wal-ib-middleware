package com.cobiscorp.ecobis.orchestration.core.ib.transfers;

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
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobisv.commons.context.CobisSession;
import com.cobiscorp.cobisv.commons.context.Context;
import com.cobiscorp.cobisv.commons.context.ContextManager;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentDetailsTransfInternationalRequest;
import com.cobiscorp.ecobis.ib.application.dtos.PaymentDetailsTransfInternationalResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountOperation;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.dtos.PaymentDetailsTransfInternational;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServicePaymentDetailsTransfInternational;

@Service(value = { ICoreServicePaymentDetailsTransfInternational.class, ICISSPBaseOrchestration.class,
		IOrchestrator.class })
@Component(name = "PaymentDetailsInternationalTransfQuery", immediate = false)
@Properties(value = { @Property(name = "service.description", value = "PaymentDetailsInternationalTransfQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "PaymentDetailsInternationalTransfQuery") })

public class PaymentDetailsInternationalTransfQuery extends SPJavaOrchestrationBase
		implements ICoreServicePaymentDetailsTransfInternational {
	ILogger logger = LogFactory.getLogger(PaymentDetailsInternationalTransfQuery.class);
	private static final String CLASS_NAME = " >-----> ";

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
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServicePaymentDetailsTransfInternational#
	 * getPaymentDetailsTransfInternational(com.cobiscorp.ecobis.ib.application.
	 * dtos.PaymentDetailsTransfInternationalRequest)
	 */
	@Override
	public PaymentDetailsTransfInternationalResponse getPaymentDetailsTransfInternational(
			PaymentDetailsTransfInternationalRequest aPaymentDetailsTransfInternationalRequest)
			throws CTSServiceException, CTSInfrastructureException {
		
		Context context = ContextManager.getContext();
		CobisSession session = (CobisSession) context.getSession();

		String CODE_TRN = aPaymentDetailsTransfInternationalRequest.getCodeTransactionalIdentifier();
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, CODE_TRN);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		anOriginalRequest.setSpName("cob_comext..sp_qtu_trn_reporte");
		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN,
				aPaymentDetailsTransfInternationalRequest.getTransaction().toString()); // CODE_TRN);
		anOriginalRequest.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, session.getServer());
		anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, session.getOffice());
		anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, session.getUser());
		anOriginalRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, session.getTerminal());
		anOriginalRequest.addInputParam("@s_rol", ICTSTypes.SQLINT4, session.getRole());

		anOriginalRequest.addInputParam("@i_opeban", ICTSTypes.SQLVARCHAR,
				aPaymentDetailsTransfInternationalRequest.getProductNumber());
		anOriginalRequest.addInputParam("@i_fdate", ICTSTypes.SQLINT4,
				aPaymentDetailsTransfInternationalRequest.getDateFormatId().toString());
		anOriginalRequest.addInputParam("@i_modo", ICTSTypes.SQLINT4,
				aPaymentDetailsTransfInternationalRequest.getMode().toString());
		anOriginalRequest.addInputParam("@i_tope", ICTSTypes.SQLVARCHAR,
				aPaymentDetailsTransfInternationalRequest.getTypeOperation());
		if (aPaymentDetailsTransfInternationalRequest.getMode() == 2
				|| aPaymentDetailsTransfInternationalRequest.getMode() == 1)
			anOriginalRequest.addInputParam("@i_ttrn", ICTSTypes.SQLVARCHAR,
					aPaymentDetailsTransfInternationalRequest.getTypeTransaction());

		Integer MODE = aPaymentDetailsTransfInternationalRequest.getMode();
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Obtencion del servicio");

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Enviando solicitud al Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request a enviar: " + anOriginalRequest.getProcedureRequestAsString());

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Data devuelta del Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Response obtenido: " + response.getProcedureResponseAsString());
		
		return transformToPaymentDetailsTransfInternationalResponse(response, MODE);
	}

	/**
	 * @param response
	 * @return
	 */
	private PaymentDetailsTransfInternationalResponse transformToPaymentDetailsTransfInternationalResponse(
			IProcedureResponse aProcedureResponse, Integer MODE) {		
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "RESPONSE TO TRANSFORM: " + aProcedureResponse.getProcedureResponseAsString());

		PaymentDetailsTransfInternational aPaymentDetailsTransfInternational = null;
		AccountOperation aAccountOperation = null;
		List<PaymentDetailsTransfInternational> aPaymentDetailsTransfInternationalList = new ArrayList<PaymentDetailsTransfInternational>();
		List<AccountOperation> aAccountOperationList = new ArrayList<AccountOperation>();
		PaymentDetailsTransfInternationalResponse aPaymentDetailsTransfInternationalResponse = new PaymentDetailsTransfInternationalResponse();

		IResultSetRow[] rowsTransfersInternational = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();
		for (IResultSetRow iResultSetRow : rowsTransfersInternational) {
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

			if (MODE == 17) {
				if (columns[0].getValue() != null) {
					aPaymentDetailsTransfInternationalResponse.setPaymentDate(columns[0].getValue());
				}
			} else if (MODE == 2) {
				aPaymentDetailsTransfInternational = new PaymentDetailsTransfInternational();
				if (columns[0].getValue() != null) {
					aPaymentDetailsTransfInternational
							.setTransactionSeuqential(Integer.parseInt(columns[0].getValue()));
				}
				if (columns[1].getValue() != null) {
					aPaymentDetailsTransfInternational.setNumber(Integer.parseInt(columns[1].getValue()));
				}
				if (columns[2].getValue() != null) {
					aPaymentDetailsTransfInternational.setTerm(columns[2].getValue());
				}
				if (columns[3].getValue() != null) {
					aPaymentDetailsTransfInternational
							.setPaymentDetailSequential(Integer.parseInt(columns[3].getValue()));
				}
				if (columns[4].getValue() != null) {
					aPaymentDetailsTransfInternational.setPaymentType(columns[4].getValue());
				}
				if (columns[5].getValue() != null) {
					aPaymentDetailsTransfInternational.setPaymentTypeDetail(columns[5].getValue());
				}
				if (columns[6].getValue() != null) {
					aPaymentDetailsTransfInternational.setExtraAmount(Double.parseDouble(columns[6].getValue()));
				}
				if (columns[7].getValue() != null) {
					aPaymentDetailsTransfInternational.setCurrency(columns[7].getValue());
				}
				if (columns[8].getValue() != null) {
					aPaymentDetailsTransfInternational.setCurrencyType(Double.parseDouble(columns[8].getValue()));
				}
				if (columns[9].getValue() != null) {
					aPaymentDetailsTransfInternational.setLocalAmount(Double.parseDouble(columns[9].getValue()));
				}
				if (columns[10].getValue() != null) {
					aPaymentDetailsTransfInternational.setDetail(columns[10].getValue());
				}
				if (columns[11].getValue() != null) {
					aPaymentDetailsTransfInternational
							.setPaymentDateSequential(Integer.parseInt(columns[11].getValue()));
				}
				aPaymentDetailsTransfInternationalList.add(aPaymentDetailsTransfInternational);
			} else if (MODE == 1) {
				aAccountOperation = new AccountOperation();
				if (columns[0].getValue() != null) {
					aAccountOperation.setParameter(columns[0].getValue());
				}
				if (columns[1].getValue() != null) {
					aAccountOperation.setParameterDescription(columns[1].getValue());
				}
				if (columns[2].getValue() != null) {
					aAccountOperation.setFactor(Double.parseDouble(columns[2].getValue()));
				}
				if (columns[3].getValue() != null) {
					aAccountOperation.setConcept(columns[3].getValue());
				}
				if (columns[4].getValue() != null) {
					aAccountOperation.setAmount(Double.parseDouble(columns[4].getValue()));
				}
				if (columns[5].getValue() != null) {
					aAccountOperation.setSequentialOperation(Integer.parseInt(columns[5].getValue()));
				}
				if (columns[6].getValue() != null) {
					aAccountOperation.setNumber(Integer.parseInt(columns[6].getValue()));
				}
				if (columns[7].getValue() != null) {
					aAccountOperation.setTerm(columns[7].getValue());
				}
				if (columns[8].getValue() != null) {
					aAccountOperation.setDetailSequentialTransaction(Integer.parseInt(columns[8].getValue()));
				}
				if (columns[9].getValue() != null) {
					aAccountOperation.setDetailSequentialPaymentDate(columns[9].getValue());
				}
				aAccountOperationList.add(aAccountOperation);
			}
		}
		if (MODE == 2) {
			aPaymentDetailsTransfInternationalResponse
					.setPaymentDetailsCollection(aPaymentDetailsTransfInternationalList);
		} else if (MODE == 1) {
			aPaymentDetailsTransfInternationalResponse.setAccountOperationCollection(aAccountOperationList);
		}
		aPaymentDetailsTransfInternationalResponse.setReturnCode(aProcedureResponse.getReturnCode());
		Message[] message = Utils.returnArrayMessage(aProcedureResponse);
		aPaymentDetailsTransfInternationalResponse.setMessages(message);		
		return aPaymentDetailsTransfInternationalResponse;
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
}
