/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author areinoso
 * @since Nov 18, 2014
 * @version 1.0.0
 */
public class SimulationExpiration {
	
	private Integer additionalDays;
	private String expirationDate;
	private String expirationDateHold;
	private String processDate;
	private String processDateHold;
	private String result;
	private Integer termHold;
	private Integer numberOfLaborsDays;
	/**
	 * @return the additionalDays
	 */
	public Integer getAdditionalDays() {
		return additionalDays;
	}
	/**
	 * @param additionalDays the additionalDays to set
	 */
	public void setAdditionalDays(Integer additionalDays) {
		this.additionalDays = additionalDays;
	}
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
	 * @return the expirationDateHold
	 */
	public String getExpirationDateHold() {
		return expirationDateHold;
	}
	/**
	 * @param expirationDateHold the expirationDateHold to set
	 */
	public void setExpirationDateHold(String expirationDateHold) {
		this.expirationDateHold = expirationDateHold;
	}
	/**
	 * @return the processDate
	 */
	public String getProcessDate() {
		return processDate;
	}
	/**
	 * @param processDate the processDate to set
	 */
	public void setProcessDate(String processDate) {
		this.processDate = processDate;
	}
	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}
	/**
	 * @return the termHold
	 */
	public Integer getTermHold() {
		return termHold;
	}
	/**
	 * @param termHold the termHold to set
	 */
	public void setTermHold(Integer termHold) {
		this.termHold = termHold;
	}
	/**
	 * @return the numberOfLaborsDays
	 */
	public Integer getNumberOfLaborsDays() {
		return numberOfLaborsDays;
	}
	/**
	 * @param numberOfLaborsDays the numberOfLaborsDays to set
	 */
	public void setNumberOfLaborsDays(Integer numberOfLaborsDays) {
		this.numberOfLaborsDays = numberOfLaborsDays;
	}
	/**
	 * @return the processDateHold
	 */
	public String getProcessDateHold() {
		return processDateHold;
	}
	/**
	 * @param processDateHold the processDateHold to set
	 */
	public void setProcessDateHold(String processDateHold) {
		this.processDateHold = processDateHold;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SimulationExpiration [additionalDays=" + additionalDays
				+ ", expirationDate=" + expirationDate
				+ ", expirationDateHold=" + expirationDateHold
				+ ", processDate=" + processDate + ", processDateHold="
				+ processDateHold + ", result=" + result + ", termHold="
				+ termHold + ", numberOfLaborsDays=" + numberOfLaborsDays + "]";
	}

	
	
}
