package com.cobiscorp.channels.atm.test;
/**
 * Financial Product representation for ISO
 * @author fabad
 *
 */
public enum  ATMISOFinancialProduct {
	SAVINGS("10"),
	CHECKING("20"),
	DEFAULT("00");
	/**
	 * Code that should be sent in frame
	 */
	public final String code;   
    ATMISOFinancialProduct(String aCode) {
    	code = aCode;

    }

}
