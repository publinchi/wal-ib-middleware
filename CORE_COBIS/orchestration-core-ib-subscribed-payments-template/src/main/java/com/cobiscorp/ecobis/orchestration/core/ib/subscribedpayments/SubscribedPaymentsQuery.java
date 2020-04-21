package com.cobiscorp.ecobis.orchestration.core.ib.subscribedpayments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSubscribedPayment;
import com.cobiscorp.ecobis.ib.application.dtos.SubscribedContractResponse;
import com.cobiscorp.ecobis.ib.application.dtos.SubscribedPaymentRequest;
import com.cobiscorp.ecobis.ib.application.dtos.SubscribedPaymentResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.SubscribedContract;


@Component(name = "SubscribedPaymentsQuery", immediate = false)
@Service(value = { ICoreServiceSubscribedPayment.class })
@Properties(value = { @Property(name = "service.description", value = "SubscribedPaymentsQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "SubscribedPaymentsQuery") })
public class SubscribedPaymentsQuery extends SPJavaOrchestrationBase implements ICoreServiceSubscribedPayment {
	private static ILogger logger = LogFactory.getLogger(SubscribedPaymentsQuery.class);
	private static final String SP_NAME = "cobis..sp_bv_convenio_inscrito";

	/**** Output: getSubTypes *****/
	private static final int COL_CATEGORY_ID = 0;
	private static final int COL_CONTRACT_ID = 1;
	private static final int COL_NUM_DOC = 2;
	private static final int COL_DESCRIPTION = 3;
	private static final int COL_NAME = 4;
	private static final int COL_ENTITY = 5;
	private static final int COL_LOGIN = 6;
	private static final int COL_ENTITY_BV = 7;
	private static final int COL_REF1 = 8;
	private static final int COL_REF2 = 9;
	private static final int COL_REF3 = 10;
	private static final int COL_REF4 = 11;
	private static final int COL_REF5 = 12;
	private static final int COL_REF6 = 13;
	private static final int COL_REF7 = 14;
	private static final int COL_REF8 = 15;
	private static final int COL_REF9 = 16;
	private static final int COL_REF10 = 17;
	private static final int COL_REF11 = 18;
	private static final int COL_REF12 = 19;
	private static final int COL_KEY = 20;
	private static final int COL_TYPE_DOC = 21;
	private static final int COL_ID = 22;
	private static final int COL_SEQUENTIAL = 23;
    
	@Override
	public SubscribedContractResponse getSubscribedContracts(SubscribedPaymentRequest aSubscribedPayment)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isDebugEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getSubscribedContracts");
		}

		IProcedureResponse pResponse = ExecutionQuery(SP_NAME, aSubscribedPayment);
		SubscribedContractResponse subscribedContractResponse = transformToSubscribedContractResponse(pResponse);
		return subscribedContractResponse;
	}
    
	private IProcedureResponse Execution(String SpName, SubscribedPaymentRequest aSubscribedPaymentRequest)
			throws CTSServiceException, CTSInfrastructureException {
		IProcedureRequest request = initProcedureRequest(aSubscribedPaymentRequest.getOriginalRequest());

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1801033");
		request.setSpName(SpName);
        if (logger.isDebugEnabled()) {
		    logger.logInfo("aSubscribedPaymentRequest" + aSubscribedPaymentRequest.toString());
        }

		request.addInputParam("@i_id_convenio", ICTSTypes.SYBINT4,
				aSubscribedPaymentRequest.getContractId().toString());
		request.addInputParam("@i_id_categoria", ICTSTypes.SYBVARCHAR,
				aSubscribedPaymentRequest.getInterfaceType().toString());
		request.addInputParam("@i_ente", ICTSTypes.SYBINT4, aSubscribedPaymentRequest.getEntity().toString());
		request.addInputParam("@i_login", ICTSTypes.SYBVARCHAR, aSubscribedPaymentRequest.getLogin().toString());
		request.addInputParam("@i_operacion", ICTSTypes.SYBVARCHAR, aSubscribedPaymentRequest.getOperation());

		if (aSubscribedPaymentRequest.getSequential() != null) {
			request.addInputParam("@i_secuencial", ICTSTypes.SYBINT4,
					aSubscribedPaymentRequest.getSequential().toString());
		}

		if (!aSubscribedPaymentRequest.getOperation().equals("D")) {
			if (aSubscribedPaymentRequest.getTypeDoc() != null)
				request.addInputParam("@i_tipo_doc", ICTSTypes.SYBVARCHAR,
						aSubscribedPaymentRequest.getTypeDoc().toString());

			if (aSubscribedPaymentRequest.getNumDoc() != null)
				request.addInputParam("@i_num_doc", ICTSTypes.SYBVARCHAR,
						aSubscribedPaymentRequest.getNumDoc().toString());
            
			request.addInputParam("@i_ente_bv", ICTSTypes.SYBINT4, aSubscribedPaymentRequest.getEntityBV().toString());

			if (aSubscribedPaymentRequest.getKey() != null)
				request.addInputParam("@i_llave", ICTSTypes.SYBVARCHAR, aSubscribedPaymentRequest.getKey().toString());

			request.addInputParam("@i_descripcion", ICTSTypes.SYBVARCHAR,
					aSubscribedPaymentRequest.getDescription().toString());

			if (aSubscribedPaymentRequest.getRef1() != null)
				request.addInputParam("@i_referencia1", ICTSTypes.SYBVARCHAR,
						aSubscribedPaymentRequest.getRef1().toString());

			if (aSubscribedPaymentRequest.getRef2() != null)
				request.addInputParam("@i_referencia2", ICTSTypes.SYBVARCHAR,
						aSubscribedPaymentRequest.getRef2().toString());

			if (aSubscribedPaymentRequest.getRef3() != null)
				request.addInputParam("@i_referencia3", ICTSTypes.SYBVARCHAR,
						aSubscribedPaymentRequest.getRef3().toString());

			if (aSubscribedPaymentRequest.getRef4() != null)
				request.addInputParam("@i_referencia4", ICTSTypes.SYBVARCHAR,
						aSubscribedPaymentRequest.getRef4().toString());

			if (aSubscribedPaymentRequest.getRef5() != null)
				request.addInputParam("@i_referencia5", ICTSTypes.SYBVARCHAR,
						aSubscribedPaymentRequest.getRef5().toString());

			if (aSubscribedPaymentRequest.getRef6() != null)
				request.addInputParam("@i_referencia6", ICTSTypes.SYBVARCHAR,
						aSubscribedPaymentRequest.getRef6().toString());

			if (aSubscribedPaymentRequest.getRef7() != null)
				request.addInputParam("@i_referencia7", ICTSTypes.SYBVARCHAR,
						aSubscribedPaymentRequest.getRef7().toString());

			if (aSubscribedPaymentRequest.getRef8() != null)
				request.addInputParam("@i_referencia8", ICTSTypes.SYBVARCHAR,
						aSubscribedPaymentRequest.getRef8().toString());

			if (aSubscribedPaymentRequest.getRef9() != null)
				request.addInputParam("@i_referencia9", ICTSTypes.SYBVARCHAR,
						aSubscribedPaymentRequest.getRef9().toString());

			if (aSubscribedPaymentRequest.getRef10() != null)
				request.addInputParam("@i_referencia10", ICTSTypes.SYBVARCHAR,
						aSubscribedPaymentRequest.getRef10().toString());

			if (aSubscribedPaymentRequest.getRef11() != null)
				request.addInputParam("@i_referencia11", ICTSTypes.SYBVARCHAR,
						aSubscribedPaymentRequest.getRef11().toString());

			if (aSubscribedPaymentRequest.getRef12() != null)
				request.addInputParam("@i_referencia12", ICTSTypes.SYBVARCHAR,
						aSubscribedPaymentRequest.getRef12().toString());
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Request:*** " + request.getProcedureRequestAsString());
		}
		if (logger.isDebugEnabled()) {
			logger.logInfo("*** Start Request *** ");
		}

		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isDebugEnabled()) {
			logger.logInfo("*** Finalize Response *** ");
		}
		return pResponse;
	}
    
	private IProcedureResponse ExecutionQuery(String SpName, SubscribedPaymentRequest aSubscribedPaymentRequest)
			throws CTSServiceException, CTSInfrastructureException {
		IProcedureRequest request = initProcedureRequest(aSubscribedPaymentRequest.getOriginalRequest());

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1801033");
		request.setSpName(SpName);
		request.addInputParam("@i_id_categoria", ICTSTypes.SYBVARCHAR, aSubscribedPaymentRequest.getInterfaceType());
		request.addInputParam("@i_id_convenio", ICTSTypes.SYBINT4,
				aSubscribedPaymentRequest.getContractId().toString());
		request.addInputParam("@i_secuencial", ICTSTypes.SYBINT4, aSubscribedPaymentRequest.getSequential().toString());
		request.addInputParam("@i_ente", ICTSTypes.SYBINT4, aSubscribedPaymentRequest.getEntity().toString());
		request.addInputParam("@i_login", ICTSTypes.SYBVARCHAR, aSubscribedPaymentRequest.getLogin().toString());
		request.addInputParam("@i_operacion", ICTSTypes.SYBVARCHAR, "S");

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Request:*** " + request.getProcedureRequestAsString());
		}
		if (logger.isDebugEnabled()) {
			logger.logInfo("*** Start Request *** ");
		}

		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isDebugEnabled()) {
			logger.logInfo("*** Finalize Response *** ");
		}
		return pResponse;
	}

	private SubscribedContractResponse transformToSubscribedContractResponse(IProcedureResponse aProcedureResponse) {
		SubscribedContractResponse SubscribedContractResp = new SubscribedContractResponse();
		List<SubscribedContract> SubscribedContractCollection = new ArrayList<SubscribedContract>();
		SubscribedContract aSubscribedContract = null;

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + aProcedureResponse.getProcedureResponseAsString());
		}

		if (aProcedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsSubscribedContract = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

			for (IResultSetRow iResultSetRow : rowsSubscribedContract) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				aSubscribedContract = new SubscribedContract();
				aSubscribedContract.setCategoryId(columns[COL_CATEGORY_ID].getValue());
				aSubscribedContract.setContractId(Integer.parseInt(columns[COL_CONTRACT_ID].getValue()));
				aSubscribedContract.setNumDoc(columns[COL_NUM_DOC].getValue());
				aSubscribedContract.setDescription(columns[COL_DESCRIPTION].getValue());
				aSubscribedContract.setName(columns[COL_NAME].getValue());
				aSubscribedContract.setEntity(Integer.parseInt(columns[COL_ENTITY].getValue()));
				aSubscribedContract.setLogin(columns[COL_LOGIN].getValue());
				aSubscribedContract.setEntityBV(Integer.parseInt(columns[COL_ENTITY_BV].getValue()));
				aSubscribedContract.setRef1(columns[COL_REF1].getValue());
				aSubscribedContract.setRef2(columns[COL_REF2].getValue());
				aSubscribedContract.setRef3(columns[COL_REF3].getValue());
				aSubscribedContract.setRef4(columns[COL_REF4].getValue());
				aSubscribedContract.setRef5(columns[COL_REF5].getValue());
				aSubscribedContract.setRef6(columns[COL_REF6].getValue());
				aSubscribedContract.setRef7(columns[COL_REF7].getValue());
				aSubscribedContract.setRef8(columns[COL_REF8].getValue());
				aSubscribedContract.setRef9(columns[COL_REF9].getValue());
				aSubscribedContract.setRef10(columns[COL_REF10].getValue());
				aSubscribedContract.setRef11(columns[COL_REF11].getValue());
				aSubscribedContract.setRef12(columns[COL_REF12].getValue());
				aSubscribedContract.setKey(columns[COL_KEY].getValue());
				aSubscribedContract.setTypeDoc(columns[COL_TYPE_DOC].getValue());
				aSubscribedContract.setiSequential(Integer.parseInt(columns[COL_ID].getValue()));
				aSubscribedContract.setSequential(Integer.parseInt(columns[COL_SEQUENTIAL].getValue()));
				SubscribedContractCollection.add(aSubscribedContract);
			}

			SubscribedContractResp.setSubscribedContractCollection(SubscribedContractCollection);
		} else {
			SubscribedContractResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		}
		SubscribedContractResp.setReturnCode(aProcedureResponse.getReturnCode());
		return SubscribedContractResp;
	}
	
	private SubscribedPaymentResponse transformToSubscribedPaymentResponse(IProcedureResponse aProcedureResponse) {
		SubscribedPaymentResponse SubscribedPaymentResp = new SubscribedPaymentResponse();

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + aProcedureResponse.getProcedureResponseAsString());
			logger.logDebug("*** ProcedureResponsegetReturnCode" + aProcedureResponse.getReturnCode());
		}

		if (logger.isDebugEnabled()) {
			logger.logInfo("Response en transformToSubscribedPaymentResponse - Implementacion: "
					+ aProcedureResponse.getReturnCode());
			logger.logInfo("Response en transformToSubscribedPaymentResponse - Implementacion: "
					+ aProcedureResponse.getMessages());
			logger.logInfo("Response en transformToSubscribedPaymentResponse - Implementacion: "
					+ aProcedureResponse.toString());
		}

		if (aProcedureResponse.getReturnCode() != 0) {
			SubscribedPaymentResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		}

		SubscribedPaymentResp.setReturnCode(aProcedureResponse.getReturnCode());
        if (logger.isDebugEnabled()) {
		    logger.logDebug("*** ProcedureResponse: ***" + aProcedureResponse.getMessages());
        }
		return SubscribedPaymentResp;
	}

	
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		return null;
	}
	
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		return null;
	}
	
	@Override
	public SubscribedPaymentResponse subscribePayment(SubscribedPaymentRequest aSubscribedPaymentReq)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isDebugEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: subscribePayment");
		}

		IProcedureResponse pResponse = Execution(SP_NAME, aSubscribedPaymentReq);
		if (logger.isDebugEnabled()) {
			logger.logInfo("Response despues de ejecutar el sp - Implementacion: " + pResponse.getReturnCode());
			logger.logInfo("Response despues de ejecutar el sp - Implementacion: " + pResponse.getMessages());
			logger.logInfo("Response despues de ejecutar el sp - Implementacion: " + pResponse.toString());
		}
		SubscribedPaymentResponse subscribedPaymentResponse = transformToSubscribedPaymentResponse(pResponse);
		return subscribedPaymentResponse;
	}
}
