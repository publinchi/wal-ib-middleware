package com.cobiscorp.ecobis.orchestration.core.ib.dispacher.utils;

import java.io.Serializable;

import com.cobiscorp.cobis.cache.ICache;
import com.cobiscorp.cobis.cache.ICacheManager;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;

public class CacheUtils
{

	private static final ILogger logger = LogFactory.getLogger(CacheUtils.class);

	public static Object getCacheValue(ICacheManager cacheManager, String parent, String code)
	{
		ICache cache = cacheManager.getCache(parent);
		if (logger.isDebugEnabled()) {
			logger.logDebug("El cache manager es: " + cache.toString());
		}

		if (cache.get(code) != null) {
			if (logger.isDebugEnabled())
			{
				logger.logDebug("Se obtiene cache de clave " + code);
			}
			return cache.get(code);
		} else {
			return null;
		}

	}

	public static Boolean putCacheValue(ICacheManager cacheManager, String parent, String code, Object value)
	{
		if (logger.isDebugEnabled()) {
			logger.logDebug("Iniciando el metodo putCacheValue");
		}
		ICache cache = cacheManager.getCache(parent);

		if (cache != null && cache.get(code) == null) {
			cache.put(code, (Serializable) value);
			return true;
		} else {
			return false;
		}

	}

	public static boolean deleteCacheValue(ICacheManager cacheManager, String parent)
	{
		if (logger.isDebugEnabled()) {
			logger.logDebug("Iniciando el metodo deleteCacheValue");
		}

		ICache cache = cacheManager.getCache(parent);

		if (cache.get(parent) != null) {
			cache.remove(parent);
		}

		return true;

	}

}