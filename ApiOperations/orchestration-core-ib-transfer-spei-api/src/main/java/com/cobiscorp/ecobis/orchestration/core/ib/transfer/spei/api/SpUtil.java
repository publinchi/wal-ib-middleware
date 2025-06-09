/**
 * Archivo: SpUtil.java
 * Fecha..: 
 * Autor..: Team Evac
 *
 * Esta aplicacion es parte de los paquetes bancarios propiedad de COBISCORP.
 * Su uso no autorizado queda expresamente prohibido asi como cualquier
 * alteracion o agregado hecho por alguno de sus usuarios sin el debido
 * consentimiento por escrito de COBISCORP.
 * Este programa esta protegido por la ley de derechos de autor y por las
 * convenciones internacionales de propiedad intelectual. Su uso no
 * autorizado dara derecho a COBISCORP para obtener ordenes de secuestro
 * o retencion y para perseguir penalmente a los autores de cualquier infraccion.
 */

package com.cobiscorp.ecobis.orchestration.core.ib.transfer.spei.api;

import com.cobis.trfspeiservice.bsl.dto.RegisterSpeiSpResponse;
import com.cobis.trfspeiservice.bsl.dto.SpeiMappingRequest;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.IProcedureResponseParam;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.services.orchestrator.ISPOrchestrator;

import com.cobiscorp.cobisv.commons.context.ContextManager;
import com.cobiscorp.cobisv.commons.context.Context;
import com.cobiscorp.cobisv.commons.context.CobisSession;
import com.cobiscorp.cobis.cts.domains.IMessageBlock;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;

import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.Iterator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SpUtil {
	private static ILogger logger = LogFactory.getLogger(SpUtil.class);
	protected static final String SP_EXECUTOR_FILTER_FIELD = "SPExecutorServiceFactoryFilter";
	protected static final String SP_EXECUTOR_FILTER_VALUE = "(service.impl=object)";

	protected SpUtil() {
		// private constructor because it is an utility class
	}

	protected static IProcedureResponse executeProcedure(IProcedureRequest aProcedureRequest,
			ISPOrchestrator aSpOrchestrator) {
		String wInfo = "[SPUtil][executeProcedure]";

		if (logger.isDebugEnabled()) {
			logger.logDebug(wInfo + "<<<<<<<<<< Before execute ProcedureRequest >>>>>>>>>> "
					+ aProcedureRequest.getProcedureRequestAsString());
		}

		IProcedureResponse wProcedureResponse = aSpOrchestrator.execute(aProcedureRequest, null, null);

		if (logger.isDebugEnabled()) {
			logger.logDebug(wInfo + "<<<<<<<<<< wProcedureResponse >>>>>>>>>> " + wProcedureResponse);
		}

		wProcedureResponse = wProcedureResponse.parseMessageData();

		if (logger.isDebugEnabled()) {
			logger.logDebug(
					wInfo + "<<<<<<<<<< wProcedureResponse.hasError() >>>>>>>>>> " + wProcedureResponse.hasError());
			logger.logDebug(wInfo + "<<<<<<<<<< After execute ProcedureRequest - ProcedureResponse >>>>>>>>>> "
					+ wProcedureResponse.getProcedureResponseAsString());
		}
		return wProcedureResponse;
	}

	
	protected static void initialize(IProcedureRequest aProcedureRequest, String databaseName, String spName,
			String targetId){
		
		String wInfo = "[SPUtil][initialize]";
		
		if (logger.isDebugEnabled()){ 
			logger.logDebug(wInfo + "<<<<<<<<<< Inicializa llamada del sp " + databaseName + ".." + spName +" >>>>>>>>>>");
		}
		
		aProcedureRequest.addFieldInHeader("isFormatterEnabled", ICOBISTS.HEADER_STRING_TYPE, "false");
		aProcedureRequest.addFieldInHeader(SP_EXECUTOR_FILTER_FIELD, ICOBISTS.HEADER_STRING_TYPE, SP_EXECUTOR_FILTER_VALUE);
		
		Context wContext = ContextManager.getContext();
		CobisSession wSession = (CobisSession) wContext.getSession();
		
		if(null != wSession) {
			aProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_USER, ICOBISTS.HEADER_STRING_TYPE, wSession.getUser());
			aProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_OFFICE, ICOBISTS.HEADER_NUMBER_TYPE, wSession.getOffice());
			aProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_BRANCH_SERVER, ICOBISTS.HEADER_STRING_TYPE, wSession.getServer());
			aProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TERMINAL, ICOBISTS.HEADER_STRING_TYPE, wSession.getTerminal());
		}
		
		aProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, targetId);
		
		aProcedureRequest.setSpName(databaseName + ".." + spName);
		
	}

	protected static void initialize(IProcedureRequest aProcedureRequest, SpeiMappingRequest speiRequest, String databaseName, String spName,
									 String targetId) {

		String wInfo = "[SPUtil][initialize]";

		if (logger.isDebugEnabled()) {
			logger.logDebug(wInfo + "<<<<<<<<<< Inicializa llamada del sp " + databaseName + ".." + spName + " >>>>>>>>>>");
		}

		aProcedureRequest.addFieldInHeader("isFormatterEnabled", ICOBISTS.HEADER_STRING_TYPE, "false");
		aProcedureRequest.addFieldInHeader(SP_EXECUTOR_FILTER_FIELD, ICOBISTS.HEADER_STRING_TYPE, SP_EXECUTOR_FILTER_VALUE);

		aProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_USER, ICOBISTS.HEADER_STRING_TYPE, speiRequest.getUser());
		aProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_OFFICE, ICOBISTS.HEADER_NUMBER_TYPE, speiRequest.getOffice());
		aProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_BRANCH_SERVER, ICOBISTS.HEADER_STRING_TYPE, speiRequest.getServer());
		aProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TERMINAL, ICOBISTS.HEADER_STRING_TYPE, speiRequest.getTerminal());

		aProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, targetId);

		aProcedureRequest.setSpName(databaseName + ".." + spName);

	}

	protected static void initialize(IProcedureRequest aProcedureRequest, RegisterSpeiSpResponse speiRequest, String databaseName, String spName,
									 String targetId) {

		String wInfo = "[SPUtil][initialize]";

		if (logger.isDebugEnabled()) {
			logger.logDebug(wInfo + "<<<<<<<<<< Inicializa llamada del sp " + databaseName + ".." + spName + " >>>>>>>>>>");
		}

		aProcedureRequest.addFieldInHeader("isFormatterEnabled", ICOBISTS.HEADER_STRING_TYPE, "false");
		aProcedureRequest.addFieldInHeader(SP_EXECUTOR_FILTER_FIELD, ICOBISTS.HEADER_STRING_TYPE, SP_EXECUTOR_FILTER_VALUE);

		aProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_USER, ICOBISTS.HEADER_STRING_TYPE, speiRequest.getUser());
		aProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_OFFICE, ICOBISTS.HEADER_NUMBER_TYPE, speiRequest.getOffice());
		aProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_BRANCH_SERVER, ICOBISTS.HEADER_STRING_TYPE, speiRequest.getServer());
		aProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TERMINAL, ICOBISTS.HEADER_STRING_TYPE, speiRequest.getTerminal());

		aProcedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, targetId);

		aProcedureRequest.setSpName(databaseName + ".." + spName);

	}
	
	protected static IMessageBlock evaluateResponseStatusSP(IProcedureResponse aProcedureResponse) {

		int wMessageNumber;

		IMessageBlock wMessageBlock = null;
		Collection responseBlocks = aProcedureResponse.getResponseBlocks();
		Iterator it = responseBlocks.iterator();

		while (it.hasNext()) {
			Object msgBlock = it.next();
			if (msgBlock instanceof IMessageBlock) {
				wMessageBlock = (IMessageBlock) msgBlock;
				wMessageNumber = wMessageBlock.getMessageNumber();

				if (wMessageNumber != 0) {
					return wMessageBlock;
				}
			}

		}
		return wMessageBlock;
	}
	
	public static List<List<Map<String,Object>>> getResultSets(IProcedureResponse wProcedureResponseAS){
	    String wInfo = "[SpUtil][getResultSets]";
			Iterator it =  wProcedureResponseAS.getResultSets().iterator();
			
			if (logger.isDebugEnabled()) {
				logger.logDebug(wInfo + "<<<<<<<<<< wProcedureResponseAS.getResultSets() >>>>>>>>>> "+ wProcedureResponseAS.getResultSets());
			}
			
			List<List<Map<String,Object>>> wResultSets = new ArrayList<List<Map<String, Object>>>();
			
			while (it.hasNext()){
				IResultSetBlock rs = (IResultSetBlock) it.next();
				if (logger.isDebugEnabled()) {
					logger.logDebug(wInfo + "<<<<<<<<<< rs >>>>>>>>>> "+ rs);
					logger.logDebug(wInfo + "<<<<<<<<<< rs.getMetaData().getColumnsNumber() >>>>>>>>>> "+ rs.getMetaData().getColumnsNumber());
					logger.logDebug(wInfo + "<<<<<<<<<< rs.getData().getRowsNumber() >>>>>>>>>> "+ rs.getData().getRowsNumber());
				}
				
				if( rs.getMetaData().getColumnsNumber() > 0) {
					List<Map<String, Object>> wRows = mapResultSet(rs);
					wResultSets.add(wRows);
				}
				
			}

			if (logger.isDebugEnabled()) {
				logger.logDebug(wInfo + "<<<<<<<<<< wResultSets >>>>>>>>>> "+ wResultSets);
			}
			return wResultSets;
		}
	
	
	private static List<Map<String, Object>> mapResultSet(IResultSetBlock rs) {
	    String wInfo = "[SpUtil][mapResultSet]";
	    List<Map<String, Object>> wRows = new ArrayList<Map<String, Object>>(rs.getData().getRowsNumber());
	    String tmpColumnName;

	    for(int j=1; j<=rs.getData().getRowsNumber(); j++) {
	      Map <String,Object> wRowResult  = new HashMap<String, Object>();
	      for(int i=1; i<=rs.getMetaData().getColumnsNumber(); i++) {
	            
	        int type = rs.getMetaData().getColumnMetaData(i).getType();
	        String name = rs.getMetaData().getColumnMetaData(i).getName();
	  
	        if ("".equals(name)) {
	          name = "COLUMN" + i;
	        }
	  
	        if (rs.getData().getRow(j).getRowData(i).getValue() == null || "".equals(rs.getData().getRow(j).getRowData(i).getValue())){
	          wRowResult.put(name,null);
	        } else {
	          if (type == ICTSTypes.SQLINT1 || type == ICTSTypes.SQLINT2 || type == ICTSTypes.SQLINT4 || type == ICTSTypes.SQLBIT){
	            wRowResult.put(name,new Integer(rs.getData().getRow(j).getRowData(i).getValue()));
	          }
	          if (type == ICTSTypes.SQLCHAR || type == ICTSTypes.SQLVARCHAR || type == ICTSTypes.SQLTEXT){
	            wRowResult.put(name,rs.getData().getRow(j).getRowData(i).getValue());
	          }
	          if (type == ICTSTypes.SQLDATETIME || type == ICTSTypes.SQLDATETIME4){
	        	wRowResult.put(name,new Date(rs.getData().getRow(j).getRowData(i).getValue()));
	          }
	          if (type == ICTSTypes.SQLMONEY || type == ICTSTypes.SQLMONEY4){
	            String wTempValueMoney = rs.getData().getRow(j).getRowData(i).getValue();
	            wTempValueMoney = wTempValueMoney.replace(",", "");
	            wRowResult.put(name,new BigDecimal(wTempValueMoney));
	          }
	        }
	  
	        tmpColumnName = rs.getMetaData().getColumnMetaData(i).getName();
	    
	        if(tmpColumnName!=null && tmpColumnName.startsWith("@o_")) {
	           wRowResult.put("@O", 1);
	        } else {
	          wRowResult.put("@O", 0);
	        }
	      }
	    wRows.add(wRowResult);
	    }
	    
	    if (logger.isDebugEnabled()) {
	      logger.logDebug(wInfo + "<<<<<<<<<< params >>>>>>>>>> "+ wRows);
	    }
	    
	    return wRows;
	  }
	
	
    public static List<Map<String, Object>> getParams(IProcedureResponse wProcedureResponseAS){
		  String wInfo = "[SpUtil][getParams]";
          Iterator wIt = wProcedureResponseAS.getParams().iterator();
          List<Map<String, Object>> wParams = new ArrayList<Map<String, Object>>();
          Map wOutputParams = new HashMap();
         
          while(wIt.hasNext()) {
                 IProcedureResponseParam w = (IProcedureResponseParam) wIt.next();
                 if (logger.isDebugEnabled()) {
                        logger.logDebug(wInfo + "<<<<<<<<<< wProcedureResponseAS.getResultSets() >>>>>>>>>> "+ wProcedureResponseAS.getResultSets());
                        logger.logDebug(wInfo + "<<<<<<<<<< w.getValue() >>>>>>>>>> "+ w.getValue());    
                        logger.logDebug(wInfo + "<<<<<<<<<< w.getValue() >>>>>>>>>> "+ w.getName());     
                 }
                 wOutputParams.put(w.getName(), w.getValue());
          }
         
          wParams.add(wOutputParams);
         
          if (logger.isDebugEnabled()) {
                 logger.logDebug(wInfo + "<<<<<<<<<< wParams >>>>>>>>>> "+ wParams);
          }
         
          return wParams;
    }

	private static void logDebug(String aMenssage) {
		if (logger.isDebugEnabled()) {
			logger.logDebug(aMenssage);
		}
	}
}
