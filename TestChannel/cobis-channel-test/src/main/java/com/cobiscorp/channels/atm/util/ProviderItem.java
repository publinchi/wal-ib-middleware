package com.cobiscorp.channels.atm.util;

import com.cobiscorp.cis.connector.model.BuiltTransaction;
/**
 * Represents response from a tcphandler
 * @author fabad
 *
 */
public class ProviderItem {
	/**
	 * identifier for the response
	 */
	public String key;
	/**
	 * frame parsed
	 */
	public BuiltTransaction builtTransaction;
	


}
