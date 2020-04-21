/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author cecheverria
 * @since Sep 2, 2014
 * @version 1.0.0
 */
public class ThirdParty {
	Client client;
	Product product;
	String idBeneficiary;
	String beneficiary;
	
	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}
	/**
	 * @param product the product to set
	 */
	public void setProduct(Product product) {
		this.product = product;
	}
	/**
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}
	/**
	 * @param client the client to set
	 */
	public void setClient(Client client) {
		this.client = client;
	}
	
	/**
	 * @return the idBeneficiary
	 */
	public String getIdBeneficiary() {
		return idBeneficiary;
	}
	/**
	 * @param idBeneficiary the idBeneficiary to set
	 */
	public void setIdBeneficiary(String idBeneficiary) {
		this.idBeneficiary = idBeneficiary;
	}
	
	/**
	 * @return the beneficiary
	 */
	public String getBeneficiary() {
		return beneficiary;
	}
	/**
	 * @param beneficiary the beneficiary to set
	 */
	public void setBeneficiary(String beneficiary) {
		this.beneficiary = beneficiary;
	}

}
