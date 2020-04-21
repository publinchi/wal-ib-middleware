package com.cobiscorp.ecobis.orchestration.core.ib.subscribedpayments;

import java.util.HashMap;
import java.util.Map;

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
import com.cobiscorp.ecobis.ib.application.dtos.SubscribedContractResponse;
import com.cobiscorp.ecobis.ib.application.dtos.SubscribedPaymentRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SubscribedPaymentResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.SubscribedContract;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSubscribedPayment;

/**
 * SubscribedPayment
 * 
 * @since Aug 14, 2015
 * @author gyagual
 * @version 1.0.0
 * 
 */
@Component(name = "SubscribedPaymentOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "SubscribedPaymentOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "SubscribedPaymentOrchestrationCore") })
public class SubscribedPaymentOrchestrationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(SubscribedPaymentOrchestrationCore.class);
	private static final String CLASS_NAME = "SubscribedPaymentOrchestrationCore--->";
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
	@Reference(referenceInterface = ICoreServiceSubscribedPayment.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceSubscribedPayment", unbind = "unbindCoreServiceSubscribedPayment")
	protected ICoreServiceSubscribedPayment coreServiceSubscribedPayment;

	/**
	 * Instance Service Interface
	 * 
	 * @param service
	 */
	public void bindCoreServiceSubscribedPayment(ICoreServiceSubscribedPayment service) {
		coreServiceSubscribedPayment = service;
	}

	/**
	 * Deleting Service Interface
	 * 
	 * @param service
	 */
	public void unbindCoreServiceSubscribedPayment(ICoreServiceSubscribedPayment service) {
		coreServiceSubscribedPayment = null;
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
			mapInterfaces.put("coreServiceSubscribedPayment", coreServiceSubscribedPayment);

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
			// consulta de solicitudes del cliente
			responseProc = executeTransaction(anOriginalRequest, aBag);

			aBag.put(RESPONSE_TRANSACTION, responseProc);
			if (Utils.flowError("updateProviderTransaction", responseProc)) {

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
	 * name executeTransaction
	 * 
	 * @param anOriginalRequest
	 * @param aBag
	 * @return IProcedureResponse
	 */
	private IProcedureResponse executeTransaction(IProcedureRequest anOriginalRequest, Map<String, Object> aBag) {

		SubscribedPaymentResponse wSubscribedPaymentResp = new SubscribedPaymentResponse();
		SubscribedContractResponse wSubscribedContractResp = new SubscribedContractResponse();
		SubscribedPaymentRequest wSubscribedPaymentReq = transformSubscribedPaymentRequest(anOriginalRequest.clone());
		String operation = anOriginalRequest.readValueParam("@i_operacion");
		IProcedureResponse wProcedureResponse = null;
		try {
			wSubscribedPaymentReq.setOriginalRequest(anOriginalRequest);
			if (operation.equals("I") || operation.equals("U") || operation.equals("D"))
				wSubscribedPaymentResp = coreServiceSubscribedPayment.subscribePayment(wSubscribedPaymentReq);

			if (operation.equals("S"))
				wSubscribedContractResp = coreServiceSubscribedPayment.getSubscribedContracts(wSubscribedPaymentReq);

		} catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}
		if (operation.equals("I") || operation.equals("U") || operation.equals("D"))
			wProcedureResponse = transformSubscribedPaymentResponse(wSubscribedPaymentResp, aBag);
		if (operation.equals("S"))
			wProcedureResponse = transformSubscribedContractResponse(wSubscribedContractResp, aBag);
		return wProcedureResponse;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformSubscribedContractResponse(
			SubscribedContractResponse aSubscribedContractResponse, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response");

		if (aSubscribedContractResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aSubscribedContractResponse.getMessages()));

		} else {

			// Agregar Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("CATEGORIA", ICTSTypes.SYBVARCHAR, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("CONVENIO", ICTSTypes.SYBINT4, 10));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("NUM_DOC", ICTSTypes.SYBVARCHAR, 32));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("DESCRIPCION", ICTSTypes.SYBVARCHAR, 50));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("NOMBRE", ICTSTypes.SYBVARCHAR, 64));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("ENTE", ICTSTypes.SYBINT4, 4));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("LOGIN", ICTSTypes.SYBVARCHAR, 14));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("ENTE_BV", ICTSTypes.SYBINT4, 4));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("REF1", ICTSTypes.SYBVARCHAR, 32));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("REF2", ICTSTypes.SYBVARCHAR, 32));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("REF3", ICTSTypes.SYBVARCHAR, 32));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("REF4", ICTSTypes.SYBVARCHAR, 32));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("REF5", ICTSTypes.SYBVARCHAR, 32));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("REF6", ICTSTypes.SYBVARCHAR, 32));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("REF7", ICTSTypes.SYBVARCHAR, 32));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("REF8", ICTSTypes.SYBVARCHAR, 32));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("REF9", ICTSTypes.SYBVARCHAR, 32));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("REF10", ICTSTypes.SYBVARCHAR, 32));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("REF11", ICTSTypes.SYBVARCHAR, 32));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("REF12", ICTSTypes.SYBVARCHAR, 32));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("LLAVE", ICTSTypes.SYBVARCHAR, 32));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("TIPO_DOC", ICTSTypes.SYBVARCHAR, 32));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("ID", ICTSTypes.SYBINT4, 4));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("SECUENCIAL", ICTSTypes.SYBINT4, 4));

			for (SubscribedContract aSubscribedContract : aSubscribedContractResponse
					.getSubscribedContractCollection()) {

				IResultSetRow row = new ResultSetRow();

				row.addRowData(1, new ResultSetRowColumnData(false, aSubscribedContract.getCategoryId()));
				row.addRowData(2, new ResultSetRowColumnData(false, aSubscribedContract.getContractId().toString()));
				row.addRowData(3, new ResultSetRowColumnData(false, aSubscribedContract.getNumDoc()));
				row.addRowData(4, new ResultSetRowColumnData(false, aSubscribedContract.getDescription()));
				row.addRowData(5, new ResultSetRowColumnData(false, aSubscribedContract.getName()));
				row.addRowData(6, new ResultSetRowColumnData(false, aSubscribedContract.getEntity().toString()));
				row.addRowData(7, new ResultSetRowColumnData(false, aSubscribedContract.getLogin()));
				row.addRowData(8, new ResultSetRowColumnData(false, aSubscribedContract.getEntityBV().toString()));

				row.addRowData(9, new ResultSetRowColumnData(false,
						aSubscribedContract.getRef1() != null ? aSubscribedContract.getRef1().toString() : ""));
				row.addRowData(10, new ResultSetRowColumnData(false,
						aSubscribedContract.getRef2() != null ? aSubscribedContract.getRef2().toString() : ""));
				row.addRowData(11, new ResultSetRowColumnData(false,
						aSubscribedContract.getRef3() != null ? aSubscribedContract.getRef3().toString() : ""));
				row.addRowData(12, new ResultSetRowColumnData(false,
						aSubscribedContract.getRef4() != null ? aSubscribedContract.getRef4().toString() : ""));
				row.addRowData(13, new ResultSetRowColumnData(false,
						aSubscribedContract.getRef5() != null ? aSubscribedContract.getRef5().toString() : ""));
				row.addRowData(14, new ResultSetRowColumnData(false,
						aSubscribedContract.getRef6() != null ? aSubscribedContract.getRef6().toString() : ""));
				row.addRowData(15, new ResultSetRowColumnData(false,
						aSubscribedContract.getRef7() != null ? aSubscribedContract.getRef7().toString() : ""));
				row.addRowData(16, new ResultSetRowColumnData(false,
						aSubscribedContract.getRef8() != null ? aSubscribedContract.getRef8().toString() : ""));
				row.addRowData(17, new ResultSetRowColumnData(false,
						aSubscribedContract.getRef9() != null ? aSubscribedContract.getRef9().toString() : ""));
				row.addRowData(18, new ResultSetRowColumnData(false,
						aSubscribedContract.getRef10() != null ? aSubscribedContract.getRef10().toString() : ""));
				row.addRowData(19, new ResultSetRowColumnData(false,
						aSubscribedContract.getRef11() != null ? aSubscribedContract.getRef11().toString() : ""));
				row.addRowData(20, new ResultSetRowColumnData(false,
						aSubscribedContract.getRef12() != null ? aSubscribedContract.getRef12().toString() : ""));
				row.addRowData(21, new ResultSetRowColumnData(false, aSubscribedContract.getKey()));
				row.addRowData(22, new ResultSetRowColumnData(false, aSubscribedContract.getTypeDoc()));
				row.addRowData(23, new ResultSetRowColumnData(false, aSubscribedContract.getiSequential().toString()));
				row.addRowData(24, new ResultSetRowColumnData(false, aSubscribedContract.getSequential().toString()));
				data.addRow(row);
			}

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);

			wProcedureResponse.addResponseBlock(resultBlock);

		}
		wProcedureResponse.setReturnCode(aSubscribedContractResponse.getReturnCode());
		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;

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
	 * Transformación de ProcedureRequest a ClientEnquiriesRequest
	 ********************/

	private SubscribedPaymentRequest transformSubscribedPaymentRequest(IProcedureRequest aRequest) {
		SubscribedPaymentRequest wSubscribedPaymentRequest = new SubscribedPaymentRequest();

		if (logger.isDebugEnabled())
			logger.logDebug("Procedure Request to Transform->" + aRequest.getProcedureRequestAsString());

		wSubscribedPaymentRequest.setContractId(Integer.parseInt(aRequest.readValueParam("@i_id_convenio")));
		if (!Utils.isNull(aRequest.readValueParam("@i_tipo_doc")))
			wSubscribedPaymentRequest.setTypeDoc(aRequest.readValueParam("@i_tipo_doc"));

		if (!Utils.isNull(aRequest.readValueParam("@i_num_doc")))
			wSubscribedPaymentRequest.setNumDoc(aRequest.readValueParam("@i_num_doc"));
		if (!Utils.isNull(aRequest.readValueParam("@i_secuencial")))
			wSubscribedPaymentRequest.setSequential(Integer.parseInt(aRequest.readValueParam("@i_secuencial")));
		if (!Utils.isNull(aRequest.readValueParam("@i_operacion")))
			wSubscribedPaymentRequest.setOperation(aRequest.readValueParam("@i_operacion"));
		wSubscribedPaymentRequest.setEntity(Integer.parseInt(aRequest.readValueParam("@i_ente")));
		wSubscribedPaymentRequest.setLogin(aRequest.readValueParam("@i_login"));
		if (!Utils.isNull(aRequest.readValueParam("@i_ente_bv")))
			wSubscribedPaymentRequest.setEntityBV(Integer.parseInt(aRequest.readValueParam("@i_ente_bv")));
		if (!Utils.isNull(aRequest.readValueParam("@i_llave")))
			wSubscribedPaymentRequest.setKey(aRequest.readValueParam("@i_llave"));
		if (!Utils.isNull(aRequest.readValueParam("@i_descripcion")))
			wSubscribedPaymentRequest.setDescription(aRequest.readValueParam("@i_descripcion"));
		wSubscribedPaymentRequest.setInterfaceType(aRequest.readValueParam("@i_id_categoria"));
		if (!Utils.isNull(aRequest.readValueParam("@i_referencia1")))
			wSubscribedPaymentRequest.setRef1(aRequest.readValueParam("@i_referencia1"));
		if (!Utils.isNull(aRequest.readValueParam("@i_referencia2")))
			wSubscribedPaymentRequest.setRef2(aRequest.readValueParam("@i_referencia2"));
		if (!Utils.isNull(aRequest.readValueParam("@i_referencia3")))
			wSubscribedPaymentRequest.setRef3(aRequest.readValueParam("@i_referencia3"));
		if (!Utils.isNull(aRequest.readValueParam("@i_referencia4")))
			wSubscribedPaymentRequest.setRef4(aRequest.readValueParam("@i_referencia4"));
		if (!Utils.isNull(aRequest.readValueParam("@i_referencia5")))
			wSubscribedPaymentRequest.setRef5(aRequest.readValueParam("@i_referencia5"));
		if (!Utils.isNull(aRequest.readValueParam("@i_referencia6")))
			wSubscribedPaymentRequest.setRef6(aRequest.readValueParam("@i_referencia6"));
		if (!Utils.isNull(aRequest.readValueParam("@i_referencia7")))
			wSubscribedPaymentRequest.setRef7(aRequest.readValueParam("@i_referencia7"));
		if (!Utils.isNull(aRequest.readValueParam("@i_referencia8")))
			wSubscribedPaymentRequest.setRef8(aRequest.readValueParam("@i_referencia8"));
		if (!Utils.isNull(aRequest.readValueParam("@i_referencia9")))
			wSubscribedPaymentRequest.setRef9(aRequest.readValueParam("@i_referencia9"));
		if (!Utils.isNull(aRequest.readValueParam("@i_referencia10")))
			wSubscribedPaymentRequest.setRef10(aRequest.readValueParam("@i_referencia10"));
		if (!Utils.isNull(aRequest.readValueParam("@i_referencia11")))
			wSubscribedPaymentRequest.setRef11(aRequest.readValueParam("@i_referencia11"));
		if (!Utils.isNull(aRequest.readValueParam("@i_referencia12")))
			wSubscribedPaymentRequest.setRef12(aRequest.readValueParam("@i_referencia12"));

		return wSubscribedPaymentRequest;
	}

	/*********************
	 * Transformación de Response a ProcedureResponse
	 ***********************/

	private IProcedureResponse transformSubscribedPaymentResponse(SubscribedPaymentResponse aSubscribedPaymentResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();
		if (logger.isDebugEnabled())
			logger.logDebug("Transform Procedure Response" + aSubscribedPaymentResponse.toString());

		if (aSubscribedPaymentResponse.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION,
					Utils.returnException(aSubscribedPaymentResponse.getMessages()));
			wProcedureResponse = Utils.returnException(aSubscribedPaymentResponse.getMessages());

		}

		wProcedureResponse.setReturnCode(aSubscribedPaymentResponse.getReturnCode());

		if (logger.isInfoEnabled()) {
			logger.logInfo("Response transformSubscribedPaymentResponse - Orquestacion: "
					+ aSubscribedPaymentResponse.getReturnCode());
			logger.logInfo("Response transformSubscribedPaymentResponse - Orquestacion: "
					+ aSubscribedPaymentResponse.getMessages());
			logger.logInfo("Response transformSubscribedPaymentResponse - Orquestacion: "
					+ aSubscribedPaymentResponse.toString());
		}

		if (logger.isDebugEnabled())
			logger.logDebug("transformProcedureResponse Final -->" + wProcedureResponse.getProcedureResponseAsString());

		return wProcedureResponse;

	}

}
