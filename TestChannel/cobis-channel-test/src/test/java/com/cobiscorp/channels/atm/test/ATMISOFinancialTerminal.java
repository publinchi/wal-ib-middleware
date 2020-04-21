package com.cobiscorp.channels.atm.test;
/**
 * Financial Terminal representation for ISO
 * @author fabad
 *
 */
public enum  ATMISOFinancialTerminal {
	POS("02"),
	ATM("01");
	/**
	 * Code that should be sent in frame
	 */
	public final String code;   
    ATMISOFinancialTerminal(String aCode) {
    	code = aCode;

    }

}
