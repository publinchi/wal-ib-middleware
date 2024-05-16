/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.register.card.pan.api;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
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
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeaderColumn;
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
 * @since Jun 1, 2023
 * @version 1.0.0
 */
@Component(name = "RegisterCardPanOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "RegisterCardPanOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "RegisterCardPanOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_registerCardPan_api")})
public class RegisterCardPanOrchestrationCore extends SPJavaOrchestrationBase {
	
	private static final String CLASS_NAME = "RegisterCardPanOrchestrationCore";
	protected static final String CHANNEL_REQUEST = "8";
	
	

	private ILogger logger = (ILogger) this.getLogger();
	/**
	 * Procedure response para representar el sub-flujo de respuestas del bloqueo de tarjetas
	 * (SOLO cuando el cambio de estado de cuenta es BM1)
	 */
	
	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		
		logger.logDebug("Begin flow, RegisterCardPanOrchestrationCore starts executeJavaOrchestration...");		
		
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		
		registerCardPan(aBagSPJavaOrchestration);
		
		return processResponseApi(aBagSPJavaOrchestration);
	}
	
	private void registerCardPan(Map<String, Object> aBagSPJavaOrchestration) {
		
		
		
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		return null;
	}
	
	public IProcedureResponse processResponseApi(Map<String, Object> aBagSPJavaOrchestration) {
		
		logger.logInfo("processResponseApi [INI] --->" );
		
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));
		
		IResultSetRow row = new ResultSetRow();
		row.addRowData(1, new ResultSetRowColumnData(false, "true"));
		data.addRow(row);
		
		
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));
		
		IResultSetRow row2 = new ResultSetRow();
		row2.addRowData(1, new ResultSetRowColumnData(false, "0"));
		row2.addRowData(2, new ResultSetRowColumnData(false, "Success"));
		data2.addRow(row2);
		
		
		IResultSetHeader metaData3 = new ResultSetHeader();
		IResultSetData data3 = new ResultSetData();
		
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("unique_id", ICTSTypes.SQLVARCHAR, 100));
		metaData3.addColumnMetaData(new ResultSetHeaderColumn("card_id", ICTSTypes.SQLVARCHAR, 100));
		
		IResultSetRow row3 = new ResultSetRow();
		row3.addRowData(1, new ResultSetRowColumnData(false, "unique_id"));
		row3.addRowData(2, new ResultSetRowColumnData(false, "card_id"));
		data3.addRow(row3);
		
		
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		IResultSetBlock resultsetBlock3 = new ResultSetBlock(metaData3, data3);

		wProcedureResponse.addResponseBlock(resultsetBlock);
		wProcedureResponse.addResponseBlock(resultsetBlock2);
		wProcedureResponse.addResponseBlock(resultsetBlock3);
		
		return wProcedureResponse;		
	}	
}
