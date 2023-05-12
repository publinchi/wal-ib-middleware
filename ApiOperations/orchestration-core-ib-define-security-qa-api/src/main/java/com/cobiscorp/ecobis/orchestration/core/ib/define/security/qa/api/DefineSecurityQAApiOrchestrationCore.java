
package com.cobiscorp.ecobis.orchestration.core.ib.define.security.qa.api;

import java.util.HashMap;
import java.util.Map;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.QueryProducts;
import com.cobiscorp.ecobis.orchestration.core.ib.define.security.qa.api.Util;
import com.cobiscorp.cobis.cis.sp.java.orchestration.CISResponseManagmentHelper;

@Component(name = "DefineSecurityQAApiOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "DefineSecurityQAApiOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "DefineSecurityQAApiOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_define_security_qa_api") })
public class DefineSecurityQAApiOrchestrationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(DefineSecurityQAApiOrchestrationCore.class);
	private static final String CLASS_NAME = "DefineSecurityQAApiOrchestrationCore--->";
	private String errorCode;
	private String errorMessage;
	
	protected static final String CHANNEL_REQUEST = "8";

	public void loadConfiguration(IConfigurationReader aConfigurationReader) {

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		boolean isFailed = false;

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Start-executeJavaOrchestration---> encryptdata");
		}

		try {
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + " Request original:" + anOriginalRequest.getProcedureRequestAsString());
			}

			if (anOriginalRequest.readValueParam("@i_catalog") == null
					|| anOriginalRequest.readValueParam("@i_catalog").trim().equals("")) {

				errorCode = Util.codeNullTable;
				errorMessage = Util.messageNullTable;
				isFailed = true;
			}

			if (!isFailed) {
				aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
				isFailed = getCatalogData(aBagSPJavaOrchestration);
			}

			if (!isFailed) {

				IProcedureResponse registros = (IProcedureResponse) aBagSPJavaOrchestration.get("responseCatalog");
				if (registros == null || registros.getResultSetListSize() <= 0
						|| registros.getResultSet(1).getData().getRowsAsArray().length <= 0) {

					errorCode = Util.codeEmptyCatalog;
					errorMessage = Util.messagEmptyCatalog;
					isFailed = true;
				}
			}
			if (isFailed) {

				logger.logError(CLASS_NAME + " Error isFailed getCatalogData");
				return processResponse(anOriginalRequest, aBagSPJavaOrchestration);

			}
			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);

		} catch (Exception e) {

			logger.logError(CLASS_NAME + " *********  Error in " + e.getMessage(), e);

			e.printStackTrace();

			IProcedureResponse wProcedureRespFinal = initProcedureResponse(anOriginalRequest);
			// Agregar Header
			IResultSetHeader metaData = new ResultSetHeader();
			IResultSetData data = new ResultSetData();

			ResultSetHeader metaData2 = new ResultSetHeader();
			IResultSetData data2 = new ResultSetData();

			metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
			metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));

			metaData2.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));

			IResultSetRow row = new ResultSetRow();
			row.addRowData(2, new ResultSetRowColumnData(false, Util.codeInternalError));
			row.addRowData(1, new ResultSetRowColumnData(false, Util.messagInternalError));

			IResultSetRow row2 = new ResultSetRow();
			row2.addRowData(1, new ResultSetRowColumnData(false, "false"));

			IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
			IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);

			wProcedureRespFinal.setReturnCode(500);

			wProcedureRespFinal.addResponseBlock(resultsetBlock);
			wProcedureRespFinal.addResponseBlock(resultsetBlock2);

			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE,
					ICSP.ERROR_EXECUTION_SERVICE);
			return wProcedureRespFinal;
		}

	}

	private Boolean getCatalogData(java.util.Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " getCatalogData");
		}
		IProcedureRequest iProcedureRequest = ((IProcedureRequest) (aBagSPJavaOrchestration.get("anOriginalRequest")));
		IProcedureRequest reqTMP = (initProcedureRequest(iProcedureRequest));
		reqTMP.setSpName("cob_bvirtual..sp_define_security_qa_api");
		reqTMP.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "local");
		reqTMP.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500123");
		reqTMP.addInputParam("@i_catalog", ICTSTypes.SQLVARCHAR, iProcedureRequest.readValueParam("@i_catalog"));
		IProcedureResponse responseCatalog = executeCoreBanking(reqTMP);
		if (logger.isInfoEnabled()) {
			logger.logInfo(
					CLASS_NAME + " Response executeCoreBanking:" + responseCatalog.getProcedureResponseAsString());
		}
		aBagSPJavaOrchestration.put("responseCatalog", responseCatalog);
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " getCatalogData Final");
		}
		return responseCatalog.hasError();
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(" start processResponse--->");
		}

		IProcedureResponse wProcedureRespFinal = ((IProcedureResponse) (aBagSPJavaOrchestration
				.get("responseCatalog")));

		if (wProcedureRespFinal != null) {

			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + " ProcessResponse wProcedureRespFinal:"
						+ wProcedureRespFinal.getProcedureResponseAsString());
			}

		}
		// Agregar Header 1

		// IResultSetHeader metaData0 = new ResultSetHeader();
		// metaData0.addColumnMetaData(new ResultSetHeaderColumn("codigo",
		// ICTSTypes.SQLVARCHAR, 100));
		// metaData0.addColumnMetaData(new ResultSetHeaderColumn("valor",
		// ICTSTypes.SQLVARCHAR, 100));
		// IResultSetData data0 = new ResultSetData();

		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));

		// Agregar Header 2
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));

		if (wProcedureRespFinal != null && wProcedureRespFinal.getResultSet(1).getData().getRowsAsArray().length > 0) {
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + " wProcedureRespFinal is not null");
			}

			IResultSetRow row = new ResultSetRow();

			row.addRowData(1, new ResultSetRowColumnData(false, "0"));
			row.addRowData(2, new ResultSetRowColumnData(false, "success"));

			data.addRow(row);

			IResultSetRow row2 = new ResultSetRow();
			row2.addRowData(1, new ResultSetRowColumnData(false, "true"));
			data2.addRow(row2);

			IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
			IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
			wProcedureRespFinal.setReturnCode(200);
			wProcedureRespFinal.addResponseBlock(resultsetBlock);
			wProcedureRespFinal.addResponseBlock(resultsetBlock2);
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + " Response final: " + wProcedureRespFinal.getProcedureResponseAsString());
			}
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE,
					ICSP.ERROR_EXECUTION_SERVICE);
		} else if (this.errorCode != null) {

			logger.logWarning(CLASS_NAME + " wProcedureRespFinal is null");

			// IResultSetBlock resultsetBlock0 = new ResultSetBlock(metaData0,data0);
			// IResultSetRow row0 = new ResultSetRow();
			// data0.addRow(row0);
			if(wProcedureRespFinal==null) {
				  wProcedureRespFinal = new ProcedureResponseAS();
				  
					 IResultSetHeader metaData0 = new ResultSetHeader();
					 metaData0.addColumnMetaData(new ResultSetHeaderColumn("codigo",
					 ICTSTypes.SQLVARCHAR, 100));
					 metaData0.addColumnMetaData(new ResultSetHeaderColumn("valor",
					 ICTSTypes.SQLVARCHAR, 100));
					 IResultSetData data0 = new ResultSetData();
				 
					 IResultSetBlock resultsetBlock0 = new ResultSetBlock(metaData0,data0);					 
					 wProcedureRespFinal.addResponseBlock(resultsetBlock0);
			}
			
			

			IResultSetRow row = new ResultSetRow();

			row.addRowData(1, new ResultSetRowColumnData(false, this.errorCode));
			row.addRowData(2, new ResultSetRowColumnData(false, this.errorMessage));

			data.addRow(row);

			IResultSetRow row2 = new ResultSetRow();
			row2.addRowData(1, new ResultSetRowColumnData(false, "true"));
			data2.addRow(row2);

			IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
			IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);

		
			
			
			wProcedureRespFinal.setReturnCode(200);
			// wProcedureRespFinal.addResponseBlock(resultsetBlock0);
			wProcedureRespFinal.addResponseBlock(resultsetBlock);
			wProcedureRespFinal.addResponseBlock(resultsetBlock2);

			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + " Response final: " + wProcedureRespFinal.getProcedureResponseAsString());
			}
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE,
					ICSP.SUCCESS);

		} else {

			logger.logWarning(CLASS_NAME + " wProcedureRespFinal is null");

			// IResultSetBlock resultsetBlock0 = new ResultSetBlock(metaData0,data0);

			IResultSetRow row = new ResultSetRow();

			row.addRowData(1, new ResultSetRowColumnData(false, Util.codeEmptyCatalog));
			row.addRowData(2, new ResultSetRowColumnData(false, Util.messagEmptyCatalog));

			data.addRow(row);

			IResultSetRow row2 = new ResultSetRow();
			row2.addRowData(1, new ResultSetRowColumnData(false, "false"));
			data2.addRow(row2);

			IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
			IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);

			wProcedureRespFinal.setReturnCode(200);
			// wProcedureRespFinal.addResponseBlock(resultsetBlock0);
			wProcedureRespFinal.addResponseBlock(resultsetBlock);
			wProcedureRespFinal.addResponseBlock(resultsetBlock2);

			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + " Response final: " + wProcedureRespFinal.getProcedureResponseAsString());
			}
			wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE,
					ICSP.SUCCESS);
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " processResponse Final");
		}
		return wProcedureRespFinal;
	}

}
