
package com.cobiscorp.ecobis.orchestration.core.ib.define.security.qa.api;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
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
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;

import cobiscorp.ecobis.cts.integration.services.ICTSServiceIntegration;

/**
 * Generated Transaction Factor
 * 
 * @since May 15, 2023
 * @author sochoa
 * @version 1.0.0
 * 
 */
@Component(name = "DefineSecurityQAApiOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "DefineSecurityQAApiOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "DefineSecurityQAApiOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_define_security_qa_api") })
public class DefineSecurityQAApiOrchestrationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(DefineSecurityQAApiOrchestrationCore.class);
	private static final String CLASS_NAME = "DefineSecurityQAApiOrchestrationCore--->";
	
	CISResponseManagmentHelper cisResponseHelper = new CISResponseManagmentHelper();
	
	protected static final String CHANNEL_REQUEST = "8";
	
	/**
	 * Instance of ICTSServiceIntegration
	 */
	@Reference(bind = "setServiceIntegration", unbind = "unsetServiceIntegration", cardinality = ReferenceCardinality.OPTIONAL_UNARY)
	private ICTSServiceIntegration serviceIntegration;
	
	/**
	 * Method that set the instance of ICTSServiceIntegration
	 */
	public void setServiceIntegration(ICTSServiceIntegration serviceIntegration) {
		this.serviceIntegration = serviceIntegration;
	}
	
	/**
	 * Method that unset the instance of ICTSServiceIntegration
	 */
	public void unsetServiceIntegration(ICTSServiceIntegration serviceIntegration) {
		this.serviceIntegration = null;
	}

	/**
	 * Read configuration of parent component
	 */
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);

		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();
		anProcedureResponse = defineSecurityQa(anOriginalRequest);
		
		if (anProcedureResponse.getResultSets().size()>2) {
			
			return processTransformationResponse(anProcedureResponse);
		} else {
			return processResponseError(anProcedureResponse);
		}

	}
	
	public IProcedureResponse processResponseError(IProcedureResponse anOriginalProcedureRes) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(" start processResponseDefineSecurityQA--->");
		}

		IProcedureResponse anOriginalProcedureResponse = new ProcedureResponseAS();
		
		// Agregar Header 1
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));
	
		// Agregar Header 2
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));
		
		
		/***************************************success***************************************/

		IResultSetRow row = new ResultSetRow();
		
		row.addRowData(1, 
				new ResultSetRowColumnData(false, anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue()));
		
		data.addRow(row);
		
		/***************************************code/message************************************/
		
		IResultSetRow row2 = new ResultSetRow();

		row2.addRowData(1,
				new ResultSetRowColumnData(false, anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).getValue()));
		row2.addRowData(2, 
				new ResultSetRowColumnData(false, anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).getValue()));

		data2.addRow(row2);
		
		
		//return
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
	
		
		anOriginalProcedureResponse.setReturnCode(200);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock2);


		return anOriginalProcedureResponse;
	}

	private IProcedureResponse defineSecurityQa(IProcedureRequest aRequest) {
		
		IProcedureRequest request = new ProcedureRequestAS();
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en defineSecurityQa");
		}
	
		request.setSpName("cob_bvirtual..sp_define_security_qa_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		
		request.addInputParam("@i_external_customer_id", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_external_customer_id"));
		
		request.addInputParam("@i_question_1_id", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_question_1_id"));
		request.addInputParam("@i_answer_1_id", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_answer_1_id"));
		
		request.addInputParam("@i_question_2_id", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_question_2_id"));
		request.addInputParam("@i_answer_2_id", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_answer_2_id"));
		
		request.addInputParam("@i_question_desc", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_question_desc"));
		request.addInputParam("@i_answer_desc", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@i_answer_desc"));
		
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de defineSecurityQa");
		}

		return wProductsQueryResp;
	}
	
	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {

		return null;
	}

	public IProcedureResponse processTransformationResponse(IProcedureResponse anOriginalProcedureRes) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(" start processTransformationResponse--->");
		}

		IProcedureResponse anOriginalProcedureResponse = new ProcedureResponseAS();

		if (anOriginalProcedureRes != null) {

			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + " ProcessResponse original anOriginalProcedureRes:"
						+ anOriginalProcedureRes.getProcedureResponseAsString());
			}

		}

		//Agregar Header 1
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));

		// Agregar Header 2
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));
		
		// Agregar Header 3
		IResultSetHeader metaData3 = new ResultSetHeader();
		IResultSetData data3 = new ResultSetData();
		
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("customQuestionId", ICTSTypes.SQLINT4, 5));
		
		/***************************************success***************************************/
		
		IResultSetRow row = new ResultSetRow();
		row.addRowData(1, new ResultSetRowColumnData(false, "true"));
		data.addRow(row);
		
		/***************************************code/message************************************/
		
		IResultSetRow row2 = new ResultSetRow();
		row2.addRowData(1, new ResultSetRowColumnData(false, "0"));
		row2.addRowData(2, new ResultSetRowColumnData(false, "Success"));
		data2.addRow(row2);

		/*************************************customQuestionId************************************/

		IResultSetRow row3 = new ResultSetRow();
		row3.addRowData(1, new ResultSetRowColumnData(false, anOriginalProcedureRes.getResultSetRowColumnData(3, 1, 1).getValue()));
		data3.addRow(row3);
		
		//return		
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		IResultSetBlock resultsetBlock3 = new ResultSetBlock(metaData3, data3);
		
		anOriginalProcedureResponse.setReturnCode(200);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock2);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock3);

		logger.logInfo(CLASS_NAME + "processTransformationResponse final dco" + anOriginalProcedureResponse.getProcedureResponseAsString());
		return anOriginalProcedureResponse;
	}

}
