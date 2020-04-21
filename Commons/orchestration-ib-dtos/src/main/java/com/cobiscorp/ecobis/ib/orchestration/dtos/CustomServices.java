/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author itorres
 * @since Mar 3, 2015
 * @version 1.0.0
 */
public class CustomServices {
	private Integer codeService;
	private String nemonic;
	private String description;
	private String state;
	private Double internalCost;
	private Integer itemNumber;
	/**
	 * @return the codeService
	 */
	public Integer getCodeService() {
		return codeService;
	}
	/**
	 * @param codeService the codeService to set
	 */
	public void setCodeService(Integer codeService) {
		this.codeService = codeService;
	}
	/**
	 * @return the nemonic
	 */
	public String getNemonic() {
		return nemonic;
	}
	/**
	 * @param nemonic the nemonic to set
	 */
	public void setNemonic(String nemonic) {
		this.nemonic = nemonic;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return the internalCost
	 */
	public Double getInternalCost() {
		return internalCost;
	}
	/**
	 * @param internalCost the internalCost to set
	 */
	public void setInternalCost(Double internalCost) {
		this.internalCost = internalCost;
	}
	/**
	 * @return the itemNumber
	 */
	public Integer getItemNumber() {
		return itemNumber;
	}
	/**
	 * @param itemNumber the itemNumber to set
	 */
	public void setItemNumber(Integer itemNumber) {
		this.itemNumber = itemNumber;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CustomServices [codeService=" + codeService + ", nemonic="
				+ nemonic + ", description=" + description + ", state=" + state
				+ ", internalCost=" + internalCost + ", itemNumber="
				+ itemNumber + "]";
	}
	
}
