package com.cobiscorp.channels.atm.test;
/**
 * ATM ISO transaction representations
 * @author fabad
 *
 */
public enum  ATMISOFinancialTransaction {
	GET_BALANCE("31"),
	WITHDRAWAL("01"),
	PURCHASE("00"),
	TRANSFER("40");
	
	/**
	 * Code that should be sent in the frame
	 */
	public final String code;   
    ATMISOFinancialTransaction(String aCode) {
    	code = aCode;

    }

}
