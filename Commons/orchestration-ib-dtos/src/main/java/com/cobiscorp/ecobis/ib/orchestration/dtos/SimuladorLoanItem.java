/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author areinoso
 * @since Dec 19, 2014
 * @version 1.0.0
 */
public class SimuladorLoanItem {
	
	private String concept;
	private String description;
	private String itemType;
	private Double percentage ;
	
	//Loan
	private String operationType;
	private String productName;
	private String sector;
	/**
	 * @return the concept
	 */
	public String getConcept() {
		return concept;
	}
	/**
	 * @param concept the concept to set
	 */
	public void setConcept(String concept) {
		this.concept = concept;
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
	 * @return the itemType
	 */
	public String getItemType() {
		return itemType;
	}
	/**
	 * @param itemType the itemType to set
	 */
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	/**
	 * @return the percentage
	 */
	public Double getPercentage() {
		return percentage;
	}
	/**
	 * @param percentage the percentage to set
	 */
	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}
	/**
	 * @return the operationType
	 */
	public String getOperationType() {
		return operationType;
	}
	/**
	 * @param operationType the operationType to set
	 */
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}
	/**
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}
	/**
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}
	/**
	 * @return the sector
	 */
	public String getSector() {
		return sector;
	}
	/**
	 * @param sector the sector to set
	 */
	public void setSector(String sector) {
		this.sector = sector;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SimuladorLoanItem [concept=" + concept + ", description="
				+ description + ", itemType=" + itemType + ", percentage="
				+ percentage + ", operationType=" + operationType
				+ ", productName=" + productName + ", sector=" + sector + "]";
	}
	
	

	
}
