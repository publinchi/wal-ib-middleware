/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author jveloz
 * @since Nov 6, 2014
 * @version 1.0.0
 */
public class Rate {

	private double rate;
	private double maxRate;
	private double minRate;
	private String rateDesc;
	private String rateAuthorization;
	private String variableRate; //
	private double effectiveRate;//
	private String changeRate;//
	private String typeVariableRate;//
	/**
	 * @return the rate
	 */
	public double getRate() {
		return rate;
	}
	/**
	 * @param rate the rate to set
	 */
	public void setRate(double rate) {
		this.rate = rate;
	}
	/**
	 * @return the maxRate
	 */
	public double getMaxRate() {
		return maxRate;
	}
	/**
	 * @param maxRate the maxRate to set
	 */
	public void setMaxRate(double maxRate) {
		this.maxRate = maxRate;
	}
	/**
	 * @return the minRate
	 */
	public double getMinRate() {
		return minRate;
	}
	/**
	 * @param minRate the minRate to set
	 */
	public void setMinRate(double minRate) {
		this.minRate = minRate;
	}
	/**
	 * @return the rateAuthorization
	 */
	public String getRateAuthorization() {
		return rateAuthorization;
	}
	/**
	 * @param rateAuthorization the rateAuthorization to set
	 */
	public void setRateAuthorization(String rateAuthorization) {
		this.rateAuthorization = rateAuthorization;
	}
	/**
	 * @return the variableRate
	 */
	public String getVariableRate() {
		return variableRate;
	}
	/**
	 * @param variableRate the variableRate to set
	 */
	public void setVariableRate(String variableRate) {
		this.variableRate = variableRate;
	}
	/**
	 * @return the effectiveRate
	 */
	public double getEffectiveRate() {
		return effectiveRate;
	}
	/**
	 * @param effectiveRate the effectiveRate to set
	 */
	public void setEffectiveRate(double effectiveRate) {
		this.effectiveRate = effectiveRate;
	}
	/**
	 * @return the changeRate
	 */
	public String getChangeRate() {
		return changeRate;
	}
	/**
	 * @param changeRate the changeRate to set
	 */
	public void setChangeRate(String changeRate) {
		this.changeRate = changeRate;
	}
	/**
	 * @return the typeVariableRate
	 */
	public String getTypeVariableRate() {
		return typeVariableRate;
	}
	/**
	 * @param typeVariableRate the typeVariableRate to set
	 */
	public void setTypeVariableRate(String typeVariableRate) {
		this.typeVariableRate = typeVariableRate;
	}
	/**
	 * @return the rateDesc
	 */
	public String getRateDesc() {
		return rateDesc;
	}
	/**
	 * @param rateDesc the rateDesc to set
	 */
	public void setRateDesc(String rateDesc) {
		this.rateDesc = rateDesc;
	}
	
	
}

