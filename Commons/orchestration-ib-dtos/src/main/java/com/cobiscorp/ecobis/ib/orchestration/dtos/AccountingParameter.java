/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author cecheverria
 * @since Sep 24, 2014
 * @version 1.0.0
 */
public class AccountingParameter {

	int transaction ;
	/**
	 * @return the transaction
	 */
	public int getTransaction() {
		return transaction;
	}
	/**
	 * @param transaction the transaction to set
	 */
	public void setTransaction(int transaction) {
		this.transaction = transaction;
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
	 * @return the productId
	 */
	public int getProductId() {
		return productId;
	}
	/**
	 * @param productId the productId to set
	 */
	public void setProductId(int productId) {
		this.productId = productId;
	}
	/**
	 * @return the typeCost
	 */
	public String getTypeCost() {
		return typeCost;
	}
	@Override
	public String toString() {
		return "AccountingParameter [transaction=" + transaction + ", cause="
				+ cause + ", service=" + service + ", productId=" + productId
				+ ", typeCost=" + typeCost + ", sign=" + sign + ", causeComi="
				+ causeComi + ", causeDes=" + causeDes + ", typeCausa="
				+ typeCausa + "]";
	}
	/**
	 * @param typeCost the typeCost to set
	 */
	public void setTypeCost(String typeCost) {
		this.typeCost = typeCost;
	}
	/**
	 * @return the sign
	 */
	public String getSign() {
		return sign;
	}
	/**
	 * @param sign the sign to set
	 */
	public void setSign(String sign) {
		this.sign = sign;
	}
	/**
	 * @return the cost
	 */
	
	public String getCauseComi() {
		return causeComi;
	}
	public void setCauseComi(String causeComi) {
		this.causeComi = causeComi;
	}
	public String getCauseDes() {
		return causeDes;
	}
	public void setCauseDes(String causeDes) {
		this.causeDes = causeDes;
	}
	
	String  cause ;
	String service;
	int productId;
	String typeCost;
	String sign;
	String  causeComi ;
	String  causeDes ;
	String typeCausa;
	/**
	 * @return the typeCausa
	 */
	public String getTypeCausa() {
		return typeCausa;
	}
	/**
	 * @param typeCausa the typeCausa to set
	 */
	public void setTypeCausa(String typeCausa) {
		this.typeCausa = typeCausa;
	}
	
	
}
