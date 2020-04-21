package com.cobiscorp.ecobis.orchestration.core.batch.scheduled.payment;

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
import com.cobiscorp.ecobis.ib.application.dtos.AddressRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AddressResponse;
import com.cobiscorp.ecobis.ib.application.dtos.BatchScheduledPaymentRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BatchScheduledPaymentResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ScheduledPaymentRequest;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceBatchScheduledPayment;

@Service(value = { ICoreServiceBatchScheduledPayment.class, ICISSPBaseOrchestration.class, IOrchestrator.class })
@Component(name = "GenerateDataScheduledPayment", immediate = false)
@Properties(value = { @Property(name = "service.description", value = "GenerateDataScheduledPayment"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "GenerateDataScheduledPayment") })
public class GenerateDataScheduledPayment extends SPJavaOrchestrationBase implements ICoreServiceBatchScheduledPayment {
	private static final String CLASS_NAME = " >-----> ";
	private String operacion;

	private static ILogger logger = LogFactory.getLogger(GenerateDataScheduledPayment.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.commons.components.ICOBISComponent#loadConfiguration
	 * (com.cobiscorp.cobis.commons.configuration.IConfigurationReader)
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceBatchScheduledPayment
	 * #getGenerateCustomerData(com.cobiscorp.ecobis
	 * .ib.application.dtos.GenerateCustomerDataRequest)
	 */
	@Override
	public BatchScheduledPaymentResponse executeBatchScheduledPayment(
			BatchScheduledPaymentRequest aBatchScheduledPaymentRequest)
			throws CTSServiceException, CTSInfrastructureException {
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		String CODE_TRN = "1850035";
		operacion = aBatchScheduledPaymentRequest.getOperation();

		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, CODE_TRN);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		anOriginalRequest.setSpName("cob_bvirtual..sp_bv_gen_data_pago_prog_ej");

		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, CODE_TRN);

		anOriginalRequest.addInputParam("@i_numRows", ICTSTypes.SQLINT4,
				aBatchScheduledPaymentRequest.getRecordNumber().toString());

		anOriginalRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR,
				aBatchScheduledPaymentRequest.getOperation());

		anOriginalRequest.addInputParam("@i_siguiente", ICTSTypes.SQLINT4,
				aBatchScheduledPaymentRequest.getNext().toString());

		anOriginalRequest.addInputParam("@i_sarta", ICTSTypes.SQLINT4,
				aBatchScheduledPaymentRequest.getBatch().getSarta().toString());

		anOriginalRequest.addInputParam("@i_batch", ICTSTypes.SQLINT4,
				aBatchScheduledPaymentRequest.getBatch().getBatch().toString());

		anOriginalRequest.addInputParam("@i_secuencial", ICTSTypes.SQLINT4,
				aBatchScheduledPaymentRequest.getBatch().getSecuencial().toString());

		anOriginalRequest.addInputParam("@i_corrida", ICTSTypes.SQLINT4,
				aBatchScheduledPaymentRequest.getBatch().getCorrida().toString());

		anOriginalRequest.addInputParam("@i_intento", ICTSTypes.SQLINT4,
				aBatchScheduledPaymentRequest.getBatch().getIntento().toString());

		anOriginalRequest.addOutputParam("@o_max_registros", ICTSTypes.SQLINT4, "0");
		anOriginalRequest.addOutputParam("@o_tot_registros", ICTSTypes.SQLINT4, "0");
		anOriginalRequest.addOutputParam("@o_siguiente", ICTSTypes.SQLINT4, "0");
		anOriginalRequest.addOutputParam("@o_rowcount", ICTSTypes.SQLINT4, "0");

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Enviando solicitud al Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request a enviar: " + anOriginalRequest.getProcedureRequestAsString());

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Data devuelta del Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Response obtenido: " + response.getProcedureResponseAsString());

		return transformToScheduledPaymentResponse(response);
	}

	/**
	 * @param response
	 * @return
	 */
	private BatchScheduledPaymentResponse transformToScheduledPaymentResponse(IProcedureResponse aProcedureResponse) {

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "BatchScheduledPaymentResponse - RESPONSE TO TRANSFORM: "
					+ aProcedureResponse.getProcedureResponseAsString());

		ScheduledPaymentRequest aScheduledPaymentRequest = null;
		Product aProductos = null;
		Currency aCurrency1 = null;
		List<ScheduledPaymentRequest> aScheduledPaymentRequestCollection = new ArrayList<ScheduledPaymentRequest>();
		BatchScheduledPaymentResponse aBatchScheduledPaymentResponse = new BatchScheduledPaymentResponse();

		if (aProcedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsScheduledPaymentData = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

			for (IResultSetRow iResultSetRow : rowsScheduledPaymentData) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				aScheduledPaymentRequest = new ScheduledPaymentRequest();

				if (columns[0].getValue() != null) {
					aScheduledPaymentRequest.setId(Integer.parseInt(columns[0].getValue()));
				}

				if (operacion.equals("E")) {
					if (columns[1].getValue() != null) {
						aScheduledPaymentRequest.setModifiedDate(columns[1].getValue());
					}
				} else {
					aProductos = new Product();
					aCurrency1 = new Currency();

					if (columns[1].getValue() != null) {
						aProductos.setProductId(Integer.parseInt(columns[1].getValue()));
					}
					if (columns[2].getValue() != null) {
						aCurrency1.setCurrencyId(Integer.parseInt(columns[2].getValue()));
						aProductos.setCurrency(aCurrency1);
					}
					if (columns[3].getValue() != null) {
						aScheduledPaymentRequest.setAccount(columns[3].getValue());
					}
					if (columns[4].getValue() != null) {
						aScheduledPaymentRequest.setType(columns[4].getValue());
					}
					aScheduledPaymentRequest.setDebitProduct(aProductos);
				}

				aScheduledPaymentRequestCollection.add(aScheduledPaymentRequest);
			}

			aBatchScheduledPaymentResponse.setListScheduledPayment(aScheduledPaymentRequestCollection);
		} else {
			Message[] message = Utils.returnArrayMessage(aProcedureResponse);
			aBatchScheduledPaymentResponse.setMessages(message);
		}

		if (aProcedureResponse.readValueParam("@o_max_registros") != null) {
			aBatchScheduledPaymentResponse
					.setMaxRecord(Integer.parseInt(aProcedureResponse.readValueParam("@o_max_registros")));
		}
		if (aProcedureResponse.readValueParam("@o_tot_registros") != null) {
			aBatchScheduledPaymentResponse
					.setTotalRecords(Integer.parseInt(aProcedureResponse.readValueParam("@o_tot_registros")));
		}

		if (aProcedureResponse.readValueParam("@o_siguiente") != null) {
			aBatchScheduledPaymentResponse.setNext(Integer.parseInt(aProcedureResponse.readValueParam("@o_siguiente")));
		}
		if (aProcedureResponse.readValueParam("@o_rowcount") != null) {
			aBatchScheduledPaymentResponse
					.setRowcount(Integer.parseInt(aProcedureResponse.readValueParam("@o_rowcount")));
		}

		aBatchScheduledPaymentResponse.setReturnCode(aProcedureResponse.getReturnCode());
		return aBatchScheduledPaymentResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase
	 * #executeJavaOrchestration
	 * (com.cobiscorp.cobis.cts.domains.IProcedureRequest, java.util.Map)
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
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase
	 * #processResponse(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}
