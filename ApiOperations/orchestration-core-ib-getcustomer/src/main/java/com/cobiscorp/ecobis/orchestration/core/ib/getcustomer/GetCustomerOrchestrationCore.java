/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.getcustomer;

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
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;

/**
 * @author cecheverria
 * @since Sep 2, 2014
 * @version 1.0.0
 */
@Component(name = "GetCustomerOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "GetCustomerOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "GetCustomerOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_getCustomer")
})
public class GetCustomerOrchestrationCore extends SPJavaOrchestrationBase {// SPJavaOrchestrationBase
	
	private ILogger logger = (ILogger) this.getLogger();

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
		
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		logger.logDebug("Begin flow, GetCustomerOrchestrationCore start.");
		boolean wQueryGetCustomer;
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		wQueryGetCustomer = queryGetCustomer(aBagSPJavaOrchestration);
		
		if (wQueryGetCustomer) {
			logger.logDebug("Ending flow, executeJavaOrchestration failed.");
			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
		}
		
		logger.logDebug("Ending flow, executeJavaOrchestration success.");
		return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}
	
	private Boolean queryGetCustomer(Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureRequest wQueryGetCustomerRequest = (IProcedureRequest) aBagSPJavaOrchestration.get("anOriginalRequest");
		String idCustomer = wQueryGetCustomerRequest.readValueParam("@i_externalCustomerId");
		
		if (idCustomer.equals(""))
			return true;
		logger.logDebug("Begin flow, queryGetCustomer with id: " + idCustomer);
		
		IProcedureRequest reqTMP = (initProcedureRequest(wQueryGetCustomerRequest));
		reqTMP.setSpName("cobis..sp_getCustomer");
		reqTMP.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "central");
		reqTMP.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500092");
		reqTMP.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT1, idCustomer);
		IProcedureResponse wProcedureResponse = executeCoreBanking(reqTMP);
		if (logger.isInfoEnabled()) {
			logger.logDebug("Ending flow, queryGetCustomer with wProcedureResponse: " + wProcedureResponse.getProcedureResponseAsString());
		}
		aBagSPJavaOrchestration.put("wQueryGetCustomerResp", wProcedureResponse);
		return wProcedureResponse.hasError();		
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse wQueryGetCustomerResp =  (IProcedureResponse) aBagSPJavaOrchestration.get("wQueryGetCustomerResp");
		
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		IResultSetRow row = new ResultSetRow();
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("registrationDate", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("modifyDate", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("completeName", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("entityType", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("identityCard", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("firstSurName", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("secondSurName", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("gender", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("birthDate", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("firstName", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("secondName", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("RFC", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("mail", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("phoneNumber", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("customerLevel", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("addressId", ICTSTypes.SYBINT2, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SYBINT2, 2));
		
		if (wQueryGetCustomerResp != null && !wQueryGetCustomerResp.hasError() && wQueryGetCustomerResp.getResultSet(1).getData().getRowsAsArray().length > 0) {
			IResultSetRow resultSetRow = wQueryGetCustomerResp.getResultSet(1).getData().getRowsAsArray()[0];
			IResultSetRowColumnData[] columns = resultSetRow.getColumnsAsArray();
			logger.logDebug("Begin flow, processResponse with resultSetRow: " + columns[2].getValue());
			logger.logDebug("Begin flow, processResponse with resultSetRow: " + columns[5].getValue());
			logger.logDebug("Begin flow, processResponse with resultSetRow: " + columns[6].getValue());
			logger.logDebug("Begin flow, processResponse with resultSetRow: " + columns[9].getValue());
			logger.logDebug("Begin flow, processResponse with resultSetRow: " + columns[10].getValue());
			row.addRowData(1, new ResultSetRowColumnData(false, columns[0].getValue()));
			row.addRowData(2, new ResultSetRowColumnData(false, columns[1].getValue()));
			row.addRowData(3, new ResultSetRowColumnData(false, columns[2].getValue()));
			row.addRowData(4, new ResultSetRowColumnData(false, columns[3].getValue()));
			row.addRowData(5, new ResultSetRowColumnData(false, columns[4].getValue()));
			row.addRowData(6, new ResultSetRowColumnData(false, columns[5].getValue()));
			row.addRowData(7, new ResultSetRowColumnData(false, columns[6].getValue()));
			row.addRowData(8, new ResultSetRowColumnData(false, columns[7].getValue()));
			row.addRowData(9, new ResultSetRowColumnData(false, columns[8].getValue()));
			row.addRowData(10, new ResultSetRowColumnData(false, columns[9].getValue()));
			row.addRowData(11, new ResultSetRowColumnData(false, columns[10].getValue()));
			row.addRowData(12, new ResultSetRowColumnData(false, columns[11].getValue()));
			row.addRowData(13, new ResultSetRowColumnData(false, columns[12].getValue()));
			row.addRowData(14, new ResultSetRowColumnData(false, columns[13].getValue()));
			row.addRowData(15, new ResultSetRowColumnData(false, columns[14].getValue()));
			row.addRowData(16, new ResultSetRowColumnData(false, columns[15].getValue()));
			row.addRowData(17, new ResultSetRowColumnData(false, "true"));
			row.addRowData(18, new ResultSetRowColumnData(false, "0"));
			row.addRowData(19, new ResultSetRowColumnData(false, "SUCCESS"));
			data.addRow(row);

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock);			
			logger.logDebug("Ending flow, processResponse success.");
			return wProcedureResponse;
		} 
		else if (wQueryGetCustomerResp != null && wQueryGetCustomerResp.hasError())
		{
			row.addRowData(1, new ResultSetRowColumnData(false, null));
			row.addRowData(2, new ResultSetRowColumnData(false, null));
			row.addRowData(3, new ResultSetRowColumnData(false, null));
			row.addRowData(4, new ResultSetRowColumnData(false, null));
			row.addRowData(5, new ResultSetRowColumnData(false, null));
			row.addRowData(6, new ResultSetRowColumnData(false, null));
			row.addRowData(7, new ResultSetRowColumnData(false, null));
			row.addRowData(8, new ResultSetRowColumnData(false, null));
			row.addRowData(9, new ResultSetRowColumnData(false, null));
			row.addRowData(10, new ResultSetRowColumnData(false, null));
			row.addRowData(11, new ResultSetRowColumnData(false, null));
			row.addRowData(12, new ResultSetRowColumnData(false, null));
			row.addRowData(13, new ResultSetRowColumnData(false, null));
			row.addRowData(14, new ResultSetRowColumnData(false, null));
			row.addRowData(15, new ResultSetRowColumnData(false, null));
			row.addRowData(16, new ResultSetRowColumnData(false, null));
			row.addRowData(17, new ResultSetRowColumnData(false, "false"));
			row.addRowData(18, new ResultSetRowColumnData(false, "50002"));
			row.addRowData(19, new ResultSetRowColumnData(false, "Error retrieving information about the customer"));
			data.addRow(row);

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock);			
			logger.logDebug("Ending flow, processResponse failed.");
			return wProcedureResponse;
		} 
		else
		{
			row.addRowData(1, new ResultSetRowColumnData(false, null));
			row.addRowData(2, new ResultSetRowColumnData(false, null));
			row.addRowData(3, new ResultSetRowColumnData(false, null));
			row.addRowData(4, new ResultSetRowColumnData(false, null));
			row.addRowData(5, new ResultSetRowColumnData(false, null));
			row.addRowData(6, new ResultSetRowColumnData(false, null));
			row.addRowData(7, new ResultSetRowColumnData(false, null));
			row.addRowData(8, new ResultSetRowColumnData(false, null));
			row.addRowData(9, new ResultSetRowColumnData(false, null));
			row.addRowData(10, new ResultSetRowColumnData(false, null));
			row.addRowData(11, new ResultSetRowColumnData(false, null));
			row.addRowData(12, new ResultSetRowColumnData(false, null));
			row.addRowData(13, new ResultSetRowColumnData(false, null));
			row.addRowData(14, new ResultSetRowColumnData(false, null));
			row.addRowData(15, new ResultSetRowColumnData(false, null));
			row.addRowData(16, new ResultSetRowColumnData(false, null));
			row.addRowData(17, new ResultSetRowColumnData(false, "false"));
			row.addRowData(18, new ResultSetRowColumnData(false, "40012"));
			row.addRowData(19, new ResultSetRowColumnData(false, 
					"Customer with externalCustomerId: " + 
					((IProcedureRequest) aBagSPJavaOrchestration.get("anOriginalRequest")).readValueParam("@i_externalCustomerId") + 
					" does not exist"));
			data.addRow(row);

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock);			
			logger.logDebug("Ending flow, processResponse failed.");
			return wProcedureResponse;
		}
	}
}
