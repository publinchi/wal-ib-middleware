package com.cobiscorp.ecobis.orchestration.core.ib.common;

import java.util.HashMap;
import java.util.Map;

import com.cobiscorp.cobis.cache.ICacheManager;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.CacheUtils;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Constants;

public class ParametrizationSPEI extends SPJavaOrchestrationBase
{
	public ParametrizationSPEI() {
		super();
	}

	private static ILogger logger = LogFactory.getLogger(ParametrizationSPEI.class);
	
	
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		return null;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		return null;
	}
	
	public void getSpeiParameters(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration, ICacheManager cacheManager) {
		  
		if ( logger.isDebugEnabled() ) {
			 logger.logDebug("Begin flow, getSpeiParameters");
		}
		
		if ( CacheUtils.getCacheValue(cacheManager, Constants.SPEI_CONF, Constants.SPEI_PARAMS) == null )
		{
			IProcedureRequest requestProcedureLocal = (initProcedureRequest(anOriginalRequest));  
			requestProcedureLocal.setSpName("cob_bvirtual..sp_bv_get_params_spei");
			requestProcedureLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
			    IMultiBackEndResolverService.TARGET_LOCAL);
			requestProcedureLocal.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "Q");  
	
			IProcedureResponse wProcedureResponseLocaL = executeCoreBanking(requestProcedureLocal);
			if ( logger.isDebugEnabled() ) {
				 logger.logDebug(" getSpeiParameters wProcedureResponseLocaL: "+wProcedureResponseLocaL.getCTSMessageAsString());
			}
			  
		    if (!wProcedureResponseLocaL.hasError() && wProcedureResponseLocaL.getReturnCode() == 0) {
			   			
				if ( wProcedureResponseLocaL.getResultSetListSize() > 0 ) {
					IResultSetRow[] resultSetRows = wProcedureResponseLocaL.getResultSet(1).getData().getRowsAsArray();
					
					if (resultSetRows.length > 0) {
						Map<String, Object> params = new HashMap<String, Object>();
						for (IResultSetRow row : resultSetRows) {
							IResultSetRowColumnData[] columns = row.getColumnsAsArray();
							if (columns.length > 1) {
								params.put(columns[0].getValue(), columns[1].getValue());
							}
						}
						CacheUtils.putCacheValue(cacheManager, Constants.SPEI_CONF,Constants.SPEI_PARAMS, params);
						aBagSPJavaOrchestration.putAll(params);
					} 
				} 	
		    } 
		} else {
			Map<String, Object> parametrization = (Map<String, Object>) CacheUtils.getCacheValue(cacheManager, Constants.SPEI_CONF, Constants.SPEI_PARAMS);
			aBagSPJavaOrchestration.putAll(parametrization);
			if (logger.isDebugEnabled()) {
				logger.logDebug("Cache already exists for SPEI_IN: "+parametrization.toString());
			}
		}
		if ( logger.isDebugEnabled() ) {
			 logger.logDebug("End flow, getSpeiParameters");
		}
		
	 }
	public Map<String, Object> getSpeiCatalog(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration, ICacheManager cacheManager) {
		Map<String, Object> catalogs = new HashMap<String, Object>();  
		if ( logger.isDebugEnabled() ) {
			 logger.logDebug("Begin flow, getSpeiParameters");
		}
		
		if ( CacheUtils.getCacheValue(cacheManager, Constants.SPEI_CONF, Constants.SPEI_PARAMS) == null )
		{
			IProcedureRequest requestProcedureLocal = (initProcedureRequest(anOriginalRequest));  
			requestProcedureLocal.setSpName("cob_bvirtual..sp_bv_get_params_spei");
			requestProcedureLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
			    IMultiBackEndResolverService.TARGET_LOCAL);
			requestProcedureLocal.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "Q");  
	
			IProcedureResponse wProcedureResponseLocaL = executeCoreBanking(requestProcedureLocal);
			if ( logger.isDebugEnabled() ) {
				 logger.logDebug(" getSpeiParameters wProcedureResponseLocaL: "+wProcedureResponseLocaL.getCTSMessageAsString());
			}
			  
		    if (!wProcedureResponseLocaL.hasError() && wProcedureResponseLocaL.getReturnCode() == 0) {
			   			
				if ( wProcedureResponseLocaL.getResultSetListSize() > 0 ) {
					IResultSetRow[] resultSetRows = wProcedureResponseLocaL.getResultSet(1).getData().getRowsAsArray();
					
					if (resultSetRows.length > 0) {
						Map<String, Object> params = new HashMap<String, Object>();
						for (IResultSetRow row : resultSetRows) {
							IResultSetRowColumnData[] columns = row.getColumnsAsArray();
							if (columns.length > 1) {
								params.put(columns[0].getValue(), columns[1].getValue());
							}
						}
						CacheUtils.putCacheValue(cacheManager, Constants.SPEI_CONF,Constants.SPEI_PARAMS, params);
						aBagSPJavaOrchestration.putAll(params);
					} 
				} 	
		    } 
		} else {
			Map<String, Object> parametrization = (Map<String, Object>) CacheUtils.getCacheValue(cacheManager, Constants.SPEI_CONF, Constants.SPEI_PARAMS);
			aBagSPJavaOrchestration.putAll(parametrization);
			if (logger.isDebugEnabled()) {
				logger.logDebug("Cache already exists for SPEI_IN: "+parametrization.toString());
			}
		}
		if ( logger.isDebugEnabled() ) {
			 logger.logDebug("End flow, getSpeiParameters");
		}
		return catalogs;
	 }
}
