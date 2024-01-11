package com.cobiscorp.ecobis.orchestration.core.ib.query.get.movements.detail;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.CISResponseManagmentHelper;
import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
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
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;

/**
 * Register Account
 * 
 * @since Abr 1, 2023
 * @author dcollaguazo
 * @version 1.0.0
 * 
 */
@Component(name = "RegisterAccountQueryOrchestationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "RegisterAccountQueryOrchestationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "RegisterAccountQueryOrchestationCore") })
public class RegisterAccountQueryOrchestationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(RegisterAccountQueryOrchestationCore.class);
	private static final String CLASS_NAME = "RegisterAccountQueryOrchestationCore--->";

	CISResponseManagmentHelper cisResponseHelper = new CISResponseManagmentHelper();

	protected static final String CHANNEL_REQUEST = "8";

	/**
	 * Read configuration of parent component
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
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

		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);

		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();
		
		anProcedureResponse = registerAccount(anOriginalRequest, aBagSPJavaOrchestration);
		
		return processResponseRegister(anProcedureResponse);

	}

	public IProcedureResponse processResponseRegister(IProcedureResponse anOriginalProcedureRes) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(" start processResponseAccounts--->");
		}

		IProcedureResponse anOriginalProcedureResponse = new ProcedureResponseAS();

		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));

		// Agregar Header 2
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));
		
		IResultSetHeader metaData3 = new ResultSetHeader();
		IResultSetData data3 = new ResultSetData();
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("uniqueId", ICTSTypes.SQLINT4, 10));
		
		String uniqueId = anOriginalProcedureRes.readValueParam("@o_siguiente_tercero");

		IResultSetRow row = new ResultSetRow();
		row.addRowData(1,
				new ResultSetRowColumnData(false, anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).getValue()));
		row.addRowData(2, new ResultSetRowColumnData(false, anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).getValue()));

		data.addRow(row);

		IResultSetRow row2 = new ResultSetRow();
		row2.addRowData(1, new ResultSetRowColumnData(false, anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue()));
		data2.addRow(row2);
		
		IResultSetRow row3 = new ResultSetRow();
		row3.addRowData(1, new ResultSetRowColumnData(false, uniqueId));
		data3.addRow(row3);

		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
		IResultSetBlock resultsetBlock3 = new ResultSetBlock(metaData3, data3);
		
		anOriginalProcedureResponse.setReturnCode(200);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock2);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock3);

		return anOriginalProcedureResponse;
	}


	private IProcedureResponse registerAccount(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en registerAccount");
		}
		
		String type = aRequest.readValueParam("@i_banco");
		logger.logInfo(CLASS_NAME + " xdx" + aRequest.readValueParam("@i_banco"));
		if (type.equals("0") || type == null)
			type = "B";
		else
			type = "O";
			
		IProcedureResponse wAccountsResp = new ProcedureResponseAS();
		
		wAccountsResp = getCurpByAccount(aRequest, aBagSPJavaOrchestration, type);
		
		if (wAccountsResp.getResultSetRowColumnData(2, 1, 1).getValue().equals("0")){
			IProcedureResponse wAccountsRespInsert = new ProcedureResponseAS();
			wAccountsRespInsert = insertAccount(aRequest, aBagSPJavaOrchestration, type);
			return wAccountsRespInsert; 
		}
		

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Response " + wAccountsResp.toString());
			logger.logInfo(CLASS_NAME + " Saliendo de registerAccount");
		}

		return wAccountsResp;
	}
	
	private IProcedureResponse insertAccount(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration, String type) {

		IProcedureRequest request = new ProcedureRequestAS();
		String curp, beneficiary, product = null;

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en insertAccount");
			logger.logInfo(CLASS_NAME + " CURP: " + aBagSPJavaOrchestration.get("o_curp"));
			logger.logInfo(CLASS_NAME + " BENEFICIARY: " + aBagSPJavaOrchestration.get("o_beneficiary"));
			logger.logInfo(CLASS_NAME + " PRODUCT: " + aBagSPJavaOrchestration.get("o_producto"));
		}

		curp = (String) aBagSPJavaOrchestration.get("o_curp");
		beneficiary = (String) aBagSPJavaOrchestration.get("o_beneficiary");
		product = (String) aBagSPJavaOrchestration.get("o_producto");
		
		request.setSpName("cob_bvirtual..sp_registra_tercero_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");

		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_ente"));
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta"));
		request.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta_des"));
		request.addInputParam("@i_tipo_des", ICTSTypes.SQLVARCHAR, type);
		request.addInputParam("@s_servicio", ICTSTypes.SQLCHAR, CHANNEL_REQUEST);
		request.addInputParam("@t_trn", ICTSTypes.SQLCHAR, "18500110");
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "I");
		request.addInputParam("@i_lote", ICTSTypes.SQLVARCHAR, "0");
		if (type.equals("B")){
		request.addInputParam("@i_beneficiario", ICTSTypes.SQLVARCHAR, beneficiary.trim());
		request.addInputParam("@i_id_beneficiario", ICTSTypes.SQLVARCHAR, curp.trim());
		request.addInputParam("@i_banco", ICTSTypes.SQLINTN, "0");
		}
		else{
		request.addInputParam("@i_tipo_transf", ICTSTypes.SQLVARCHAR, "A");
		request.addInputParam("@i_nombre", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_product_alias"));
		request.addInputParam("@i_banco", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_banco"));
		}
		request.addInputParam("@i_prod", ICTSTypes.SQLVARCHAR, product);
		request.addInputParam("@i_prod_des", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_tipo_tercero"));
		request.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, "usuariobv");
		
		request.addOutputParam("@o_siguiente_tercero", ICTSTypes.SQLINTN, "0");
				
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Unique Id es " +  wProductsQueryResp.readValueParam("@o_siguiente_tercero"));
		}		

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de insertAccount");
		}

		return wProductsQueryResp;
	}

	private IProcedureResponse getCurpByAccount(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration, String typeOp) {

		IProcedureRequest request = new ProcedureRequestAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getCurpByAccount");
		}

		request.setSpName("cobis..sp_bv_valida_destino_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		request.addInputParam("@x_request_id", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_request_id"));
		request.addInputParam("@x_end_user_request_date", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_end_user_request_date"));
		request.addInputParam("@x_end_user_ip", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_end_user_ip"));
		request.addInputParam("@x_channel", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_channel"));
		
		request.addInputParam("@i_ente", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_ente"));
		request.addInputParam("@i_cta_des", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta_des"));
		request.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_cta"));
		request.addInputParam("@i_type_option", ICTSTypes.SQLCHAR, typeOp);
		request.addOutputParam("@o_curp", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_beneficiary", ICTSTypes.SQLVARCHAR, "X");
		request.addOutputParam("@o_producto", ICTSTypes.SQLVARCHAR, "X");
		request.addInputParam("@i_prod_des", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_tipo_tercero"));
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);
		
		aBagSPJavaOrchestration.put("o_curp", wProductsQueryResp.readValueParam("@o_curp"));
		aBagSPJavaOrchestration.put("o_beneficiary", wProductsQueryResp.readValueParam("@o_beneficiary"));
		aBagSPJavaOrchestration.put("o_producto", wProductsQueryResp.readValueParam("@o_producto"));
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de getCurpByAccount");
		}

		return wProductsQueryResp;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {

		return null;
	}

}
