/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.get.transaction.limit.api;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;

/**
 * @author Sochoa
 * @since Ene 17, 2024
 * @version 1.0.0
 */
@Component(name = "GetTransactionLimitOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "GetTransactionLimitOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "GetTransactionLimitOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_get_transaction_limit_api")})
public class GetTransactionLimitOrchestrationCore extends SPJavaOrchestrationBase {
	
	private ILogger logger = (ILogger) this.getLogger();
	private static final String CLASS_NAME = "GetTransactionLimitOrchestrationCore";
	protected static final String CHANNEL_REQUEST = "8";
	protected static final String GET_TRANSACTION_LIMIT = "GET_TRANSACTION_LIMIT";
	protected static final String MODE_OPERATION = "PYS";

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		
		logger.logDebug("Begin flow, GetTransactionLimit starts...");		
		
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		
		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();
		
		anProcedureResponse = getTransactionLimit(anOriginalRequest, aBagSPJavaOrchestration);
		
		return  anProcedureResponse; //processResponseApi(anOriginalRequest, anProcedureResponse, aBagSPJavaOrchestration);
	}
	
	private IProcedureResponse getTransactionLimit(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getTransactionLimit: ");
		}
		
		IProcedureResponse wValDataCentral = new ProcedureResponseAS();
		wValDataCentral = valDataCentral(aRequest, aBagSPJavaOrchestration);
		
		logger.logInfo(CLASS_NAME + " code resp auth: " + wValDataCentral.getResultSetRowColumnData(2, 1, 1).getValue());
		if (wValDataCentral.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			
			IProcedureResponse wGetDataLocal = new ProcedureResponseAS();
			wGetDataLocal = getDataLocal(aRequest, aBagSPJavaOrchestration);
			
			return wGetDataLocal;
		}
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Response " + wValDataCentral.toString());
			logger.logInfo(CLASS_NAME + " Saliendo de getTransactionLimit...");
		}

		return wValDataCentral;
	}
	
	private IProcedureResponse valDataCentral(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en valDataCentral");
		}
		
		request.setSpName("cobis..sp_val_trn_req_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@x_request_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_request_id"));
		request.addInputParam("@x_end_user_request_date", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_end_user_request_date"));
		request.addInputParam("@x_end_user_ip", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_end_user_ip"));
		request.addInputParam("@x_channel", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_channel"));
		
		request.addInputParam("@i_external_customer_id", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_external_customer_id"));
		request.addInputParam("@i_account_number", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_account_number"));
		request.addInputParam("@i_transaction_type", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_transaction_type"));
		request.addInputParam("@i_transaction_subtype", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_transaction_subtype"));
		
	/*	request.addInputParam("@i_operation", ICTSTypes.SQLVARCHAR, "GTL");
		
		request.addOutputParam("@o_trn_subtype_1", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_trn_subtype_2", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_trn_subtype_3", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_trn_subtype_4", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_trn_subtype_5", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_trn_subtype_6", ICTSTypes.SQLVARCHAR, "X");
				
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("subtype1 es " +  wProductsQueryResp.readValueParam("@o_trn_subtype_1"));
			logger.logDebug("subtype2 es " +  wProductsQueryResp.readValueParam("@o_trn_subtype_2"));
			logger.logDebug("subtype3 es " +  wProductsQueryResp.readValueParam("@o_trn_subtype_3"));
			logger.logDebug("subtype4 es " +  wProductsQueryResp.readValueParam("@o_trn_subtype_4"));
			logger.logDebug("subtype5 es " +  wProductsQueryResp.readValueParam("@o_trn_subtype_5"));
			logger.logDebug("subtype6 es " +  wProductsQueryResp.readValueParam("@o_trn_subtype_6"));
		}
		
		aBagSPJavaOrchestration.put("subtype1", wProductsQueryResp.readValueParam("@o_trn_subtype_1"));
		aBagSPJavaOrchestration.put("subtype2", wProductsQueryResp.readValueParam("@o_trn_subtype_2"));
		aBagSPJavaOrchestration.put("subtype3", wProductsQueryResp.readValueParam("@o_trn_subtype_3"));
		aBagSPJavaOrchestration.put("subtype4", wProductsQueryResp.readValueParam("@o_trn_subtype_4"));
		aBagSPJavaOrchestration.put("subtype5", wProductsQueryResp.readValueParam("@o_trn_subtype_5"));
		aBagSPJavaOrchestration.put("subtype6", wProductsQueryResp.readValueParam("@o_trn_subtype_6"));
	*/
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking valDataLocal: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de valDataCentral");
		}

		return wProductsQueryResp;
	}
	
	private IProcedureResponse getDataLocal(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getDataLocal");
		}

		request.setSpName("cob_bvirtual..sp_transaction_limits");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@i_external_customer_id", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_external_customer_id"));
		request.addInputParam("@i_transaction_type", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_transaction_type"));
		request.addInputParam("@i_trn_type_sub", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_transaction_subtype"));
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getDataLocal");
		}

		return wProductsQueryResp;
	}
	

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		return null;
	}	

}
