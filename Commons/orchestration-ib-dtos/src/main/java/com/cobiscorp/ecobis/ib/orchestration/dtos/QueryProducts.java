/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author gcondo
 * @since Nov 10, 2014
 * @version 1.0.0
 */
public class QueryProducts {
	
	private Integer code;
	private String official;
	private Product product;
	private Client client;

	/**
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(Integer code) {
		this.code = code;
	}
	/**
	 * @return the official
	 */
	public String getOfficial() {
		return official;
	}
	/**
	 * @param official the official to set
	 */
	public void setOfficial(String official) {
		this.official = official;
	}
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
	
	

}
