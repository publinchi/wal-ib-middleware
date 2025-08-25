package com.cobiscorp.ecobis.orchestration.core.ib.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
	private ICacheManager cacheManager;
	private static ILogger logger = LogFactory.getLogger(ParametrizationSPEI.class);
	
	public ParametrizationSPEI(ICacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

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
	
	public void getSpeiParameters(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		  
		if ( logger.isDebugEnabled() ) {
			 logger.logDebug("Begin flow, getSpeiParameters");
		}
		Object cache = CacheUtils.getCacheValue(cacheManager, Constants.SPEI_CONF, Constants.SPEI_PARAMS);
		if ( Objects.isNull(cache) )
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
			Map<String, Object> parametrization = (Map<String, Object>)cache;
			aBagSPJavaOrchestration.putAll(parametrization);
			if (logger.isDebugEnabled()) {
				logger.logDebug("Cache already exists for SPEI_IN: "+parametrization.toString());
			}
		}
		if ( logger.isDebugEnabled() ) {
			 logger.logDebug("End flow, getSpeiParameters");
		}
		
	 }
	public Map<String, Object> getSpeiCatalog(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration, String catalog) {
		Map<String, Object> catalogs = new HashMap<String, Object>();  
		if ( logger.isDebugEnabled() ) {
			 logger.logDebug("Begin flow, getSpeiCatalog");
		}
		int modo = 0;
		String codigo = null;
		boolean queryRepet = true; 
		Object cache = CacheUtils.getCacheValue(cacheManager, Constants.SPEI_CONF, catalog);
		if ( Objects.isNull(cache) ) {
			while(queryRepet) {
				IProcedureRequest reqTMPCentral = (initProcedureRequest(anOriginalRequest));		
				reqTMPCentral.setSpName("cobis..sp_catalogo");
				reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_CENTRAL);
				reqTMPCentral.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "Q");
				reqTMPCentral.addInputParam("@i_tabla",ICTSTypes.SQLVARCHAR, catalog);
				reqTMPCentral.addInputParam("@i_codigo",ICTSTypes.SQLVARCHAR, codigo);	
				reqTMPCentral.addInputParam("@i_modo",ICTSTypes.SQLINT4, String.valueOf(modo));	
	
			    IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);
			    if (logger.isDebugEnabled()) {
					logger.logDebug("wProcedureResponseCentral, getSpeiCatalog: " + wProcedureResponseCentral.getProcedureResponseAsString());
				}
										  		  
			    if (!wProcedureResponseCentral.hasError() && wProcedureResponseCentral.getResultSetListSize() > 1) {
						
			    	IResultSetRow[] resultSetRows = wProcedureResponseCentral.getResultSet(2).getData().getRowsAsArray();
					
					if (resultSetRows.length > 0) {
						Map<String, Object> catalogsTmp = new HashMap<String, Object>();
						for (IResultSetRow row : resultSetRows) {
							IResultSetRowColumnData[] columns = row.getColumnsAsArray();
							if (columns.length > 1) {
								catalogsTmp.put(columns[0].getValue(), columns[1].getValue());
								codigo = columns[0].getValue();
							}
						}
						modo = 1;
						catalogs.putAll(catalogsTmp);
					} else {
						queryRepet = false;
					}
				}else
					queryRepet = false;
		    }
			CacheUtils.putCacheValue(cacheManager, Constants.SPEI_CONF, catalog, catalogs);
		} else {
			Map<String, Object> parametrization = (Map<String, Object>) cache;
			aBagSPJavaOrchestration.put(catalog, parametrization);
			if (logger.isDebugEnabled()) {
				logger.logDebug("Cache already exists for catalog("+catalog+"):"+parametrization.toString());
			}
		}
		
		return catalogs;
	 }
}
