/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author jbaque
 * @since 10/11/2014
 * @version 1.0.0
 */
public class ProgrammedSavingsAditionalData {
	
	private String expirationDate;
	private Double minimumAmount;
	/**
	 * @return the expirationDate
	 */
	public String getExpirationDate() {
		return expirationDate;
	}
	/**
	 * @param expirationDate the expirationDate to set
	 */
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	/**
	 * @return the minimumAmount
	 */
	public Double getMinimumAmount() {
		return minimumAmount;
	}
	/**
	 * @param minimumAmount the minimumAmount to set
	 */
	public void setMinimumAmount(Double minimumAmount) {
		this.minimumAmount = minimumAmount;
	}

}
