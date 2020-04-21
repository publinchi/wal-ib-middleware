package com.cobiscorp.channels.atm.test;
/**
 * Financial Acceptor Terminal ID for ISO
 * @author fabad
 *
 */
public enum  ATMISOFinancialAcceptorTerminalID {
	/**
	 * Local
	 */
	LOCAL("CR&7100         "),
	/**
	 * International
	 */
	INTERNATIONAL("11023           ");
	/**
	 * Code that should be sent in frame
	 */
	public final String code;
	
    ATMISOFinancialAcceptorTerminalID(String aCode) {
    	code = aCode;

    }

}
