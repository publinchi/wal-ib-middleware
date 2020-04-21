/**
 * 
 */
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
import com.cobiscorp.ecobis.ib.application.dtos.BatchAtmCardsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BatchAtmCardsResponse;
import com.cobiscorp.ecobis.ib.application.dtos.AtmCardsRequest;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceBatchAtmCards;

@Service(value = { ICoreServiceBatchAtmCards.class, ICISSPBaseOrchestration.class, IOrchestrator.class })
@Component(name = "GenerateDataAtmCards", immediate = false)
@Properties(value = { @Property(name = "service.description", value = "GenerateDataAtmCards"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "GenerateDataAtmCards") })
public class GenerateDataAtmCards extends SPJavaOrchestrationBase implements ICoreServiceBatchAtmCards {
	private static final String CLASS_NAME = " >-----> ";
	private String operacion;

	private static ILogger logger = LogFactory.getLogger(GenerateDataAtmCards.class);

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
	public BatchAtmCardsResponse executeBatchAtmCards(BatchAtmCardsRequest aBatchAtmCardsRequest)
			throws CTSServiceException, CTSInfrastructureException {
		// Context context = ContextManager.getContext();
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		String CODE_TRN = "1850036";
		operacion = aBatchAtmCardsRequest.getOperation();

		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, CODE_TRN);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		anOriginalRequest.setSpName("cob_atm..sp_atm_gen_data_tarj_cta_ej");

		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, CODE_TRN);

		anOriginalRequest.addInputParam("@i_numRows", ICTSTypes.SQLINT4,
				aBatchAtmCardsRequest.getRecordNumber().toString());

		anOriginalRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, aBatchAtmCardsRequest.getOperation());

		anOriginalRequest.addInputParam("@i_siguiente", ICTSTypes.SQLINT4, aBatchAtmCardsRequest.getNext().toString());

		anOriginalRequest.addInputParam("@i_sarta", ICTSTypes.SQLINT4,
				aBatchAtmCardsRequest.getBatch().getSarta().toString());

		anOriginalRequest.addInputParam("@i_batch", ICTSTypes.SQLINT4,
				aBatchAtmCardsRequest.getBatch().getBatch().toString());

		anOriginalRequest.addInputParam("@i_secuencial", ICTSTypes.SQLINT4,
				aBatchAtmCardsRequest.getBatch().getSecuencial().toString());

		anOriginalRequest.addInputParam("@i_corrida", ICTSTypes.SQLINT4,
				aBatchAtmCardsRequest.getBatch().getCorrida().toString());

		anOriginalRequest.addInputParam("@i_intento", ICTSTypes.SQLINT4,
				aBatchAtmCardsRequest.getBatch().getIntento().toString());

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

		return transformToAtmCardsResponse(response);
	}

	/**
	 * @param response
	 * @return
	 */
	private BatchAtmCardsResponse transformToAtmCardsResponse(IProcedureResponse aProcedureResponse) {

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "BatchScheduledPaymentResponse - RESPONSE TO TRANSFORM: "
					+ aProcedureResponse.getProcedureResponseAsString());

		AtmCardsRequest aAtmCardsRequest = null;
		List<AtmCardsRequest> aAtmCardsRequestCollection = new ArrayList<AtmCardsRequest>();
		BatchAtmCardsResponse aBatchAtmCardsResponse = new BatchAtmCardsResponse();

		if (aProcedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsAtmCardsData = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

			for (IResultSetRow iResultSetRow : rowsAtmCardsData) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				aAtmCardsRequest = new AtmCardsRequest();

				Integer orderCol = new Integer(0);

				if (columns[orderCol].getValue() != null) {
					aAtmCardsRequest.setSequential(Integer.parseInt(columns[orderCol].getValue()));
				}

				orderCol = orderCol + 1;
				if (columns[orderCol].getValue() != null) {
					aAtmCardsRequest.setBank(Integer.parseInt(columns[orderCol].getValue()));
				}

				orderCol = orderCol + 1;
				if (columns[orderCol].getValue() != null) {
					aAtmCardsRequest.setCardId(Integer.parseInt(columns[orderCol].getValue()));
				}

				orderCol = orderCol + 1;
				if (columns[orderCol].getValue() != null) {
					aAtmCardsRequest.setMaskCode(columns[orderCol].getValue());
				}

				orderCol = orderCol + 1;
				if (columns[orderCol].getValue() != null) {
					aAtmCardsRequest.setTypeCard(columns[orderCol].getValue());
				}

				orderCol = orderCol + 1;
				if (columns[orderCol].getValue() != null) {
					aAtmCardsRequest.setNameCard(columns[orderCol].getValue());
				}

				orderCol = orderCol + 1;
				if (columns[orderCol].getValue() != null) {
					aAtmCardsRequest.setStatusCard(columns[orderCol].getValue());
				}

				orderCol = orderCol + 1;
				if (columns[orderCol].getValue() != null) {
					aAtmCardsRequest.setCustomer(Integer.parseInt(columns[orderCol].getValue()));
				}

				orderCol = orderCol + 1;
				if (columns[orderCol].getValue() != null) {
					aAtmCardsRequest.setOwner(Integer.parseInt(columns[orderCol].getValue()));
				}

				orderCol = orderCol + 1;
				if (columns[orderCol].getValue() != null) {
					aAtmCardsRequest.setCustomerName(columns[orderCol].getValue());
				}

				orderCol = orderCol + 1;
				if (columns[orderCol].getValue() != null) {
					aAtmCardsRequest.setIdentification(columns[orderCol].getValue());
				}

				orderCol = orderCol + 1;
				if (columns[orderCol].getValue() != null) {
					aAtmCardsRequest.setExpirationDate(columns[orderCol].getValue());
				}

				orderCol = orderCol + 1;
				if (columns[orderCol].getValue() != null) {
					aAtmCardsRequest.setCobisProduct(Integer.parseInt(columns[orderCol].getValue()));
				}

				orderCol = orderCol + 1;
				if (columns[orderCol].getValue() != null) {
					aAtmCardsRequest.setAccountNumber(columns[orderCol].getValue());
				}

				aAtmCardsRequestCollection.add(aAtmCardsRequest);
			}

			aBatchAtmCardsResponse.setListAtmCards(aAtmCardsRequestCollection);
		} else {
			Message[] message = Utils.returnArrayMessage(aProcedureResponse);
			aBatchAtmCardsResponse.setMessages(message);
		}

		if (aProcedureResponse.readValueParam("@o_max_registros") != null) {
			aBatchAtmCardsResponse
					.setMaxRecord(Integer.parseInt(aProcedureResponse.readValueParam("@o_max_registros")));
		}
		if (aProcedureResponse.readValueParam("@o_tot_registros") != null) {
			aBatchAtmCardsResponse
					.setTotalRecords(Integer.parseInt(aProcedureResponse.readValueParam("@o_tot_registros")));
		}

		if (aProcedureResponse.readValueParam("@o_siguiente") != null) {
			aBatchAtmCardsResponse.setNext(Integer.parseInt(aProcedureResponse.readValueParam("@o_siguiente")));
		}
		if (aProcedureResponse.readValueParam("@o_rowcount") != null) {
			aBatchAtmCardsResponse.setRowcount(Integer.parseInt(aProcedureResponse.readValueParam("@o_rowcount")));
		}

		aBatchAtmCardsResponse.setReturnCode(aProcedureResponse.getReturnCode());

		return aBatchAtmCardsResponse;
	}

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
