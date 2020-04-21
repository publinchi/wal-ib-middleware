/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

/**
 * @author bborja
 * @since 02/06/2015
 * @version 1.0.0
 */
public class AuthenticationDeviceStockDissociateResponse extends BaseResponse{

	private int ssn;

	/**
	 * @return the ssn
	 */
	public int getSsn() {
		return ssn;
	}

	/**
	 * @param ssn the ssn to set
	 */
	public void setSsn(int ssn) {
		this.ssn = ssn;
	}
	
	
}
