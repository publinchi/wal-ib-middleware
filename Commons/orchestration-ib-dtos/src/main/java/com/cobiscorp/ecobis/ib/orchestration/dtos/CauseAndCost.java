/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author gyagual
 * @since Jan 29, 2015
 * @version 1.0.0
 */
public class CauseAndCost {
	private String service;
	private Integer product;
	private String descriptionService;
	private String descriptionProduct;
	private String cause;
	private String type;
	private String creationDate;
	private String modificationDate;
	/**
	 * @return the service
	 */
	public String getService() {
		return service;
	}
	/**
	 * @param service the service to set
	 */
	public void setService(String service) {
		this.service = service;
	}
	/**
	 * @return the product
	 */
	public Integer getProduct() {
		return product;
	}
	/**
	 * @param product the product to set
	 */
	public void setProduct(Integer product) {
		this.product = product;
	}
	/**
	 * @return the descriptionService
	 */
	public String getDescriptionService() {
		return descriptionService;
	}
	/**
	 * @param descriptionService the descriptionService to set
	 */
	public void setDescriptionService(String descriptionService) {
		this.descriptionService = descriptionService;
	}
	/**
	 * @return the descriptionProduct
	 */
	public String getDescriptionProduct() {
		return descriptionProduct;
	}
	/**
	 * @param descriptionProduct the descriptionProduct to set
	 */
	public void setDescriptionProduct(String descriptionProduct) {
		this.descriptionProduct = descriptionProduct;
	}
	/**
	 * @return the cause
	 */
	public String getCause() {
		return cause;
	}
	/**
	 * @param cause the cause to set
	 */
	public void setCause(String cause) {
		this.cause = cause;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the creationDate
	 */
	public String getCreationDate() {
		return creationDate;
	}
	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	/**
	 * @return the modificationDate
	 */
	public String getModificationDate() {
		return modificationDate;
	}
	/**
	 * @param modificationDate the modificationDate to set
	 */
	public void setModificationDate(String modificationDate) {
		this.modificationDate = modificationDate;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CauseAndCost [service=" + service + ", product="
				+ product + ", descriptionService=" + descriptionService
				+ ", descriptionProduct=" + descriptionProduct + ", cause="
				+ cause + ", type=" + type + ", creationDate=" + creationDate
				+ ", modificationDate=" + modificationDate + "]";
	}
}
