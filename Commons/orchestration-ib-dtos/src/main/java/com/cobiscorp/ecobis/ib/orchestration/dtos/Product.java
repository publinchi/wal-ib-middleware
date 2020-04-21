package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * Contains information about products used in transactions.
 *
 * @author djarrin
 * @since Aug 13, 2014
 * @version 1.0.0
 */
public class Product {
	/**
	 * Alias account
	 */
	private String productAlias;

	/**
	 * @return the productAlias
	 */
	public String getProductAlias() {
		return productAlias;
	}

	/**
	 * @param productAlias the productAlias to set
	 */
	public void setProductAlias(String productAlias) {
		this.productAlias = productAlias;
	}

	/**
	 * Name product or name account
	 */
	private String productName;

	/**
	 * Product code (Eg.3=CheckingAccount,4=SavingAccount)
	 */
	private Integer productType;

	/**
	 * Number of account (Eg. 9466516)
	 */
	private String productNumber;

	/**
	 * Account's name
	 */
	private String productDescription;

	private Integer productId;

	/**
	 * @return the productId
	 */
	public Integer getProductId() {
		return productId;
	}

	/**
	 * @param productId the productId to set
	 */
	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	private int bankProductId;
	private String bankProduct;

	public int getBankProductId() {
		return bankProductId;
	}

	public void setBankProductId(int bankProductId) {
		this.bankProductId = bankProductId;
	}

	public String getBankProduct() {
		return bankProduct;
	}

	public void setBankProduct(String bankProduct) {
		this.bankProduct = bankProduct;
	}

	private Currency currency;
	/**
	 * Prefix of product
	 */
	private String productNemonic;

	/**
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * @return the name
	 */
	public String getProductName() {
		return productName;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public String getProductNemonic() {
		return productNemonic;
	}

	public String getProductNumber() {
		return productNumber;
	}

	public Integer getProductType() {
		return productType;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	/**
	 * @param productName the name to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public void setProductNemonic(String productNemonic) {
		this.productNemonic = productNemonic;
	}

	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}

	public void setProductType(Integer productType) {
		this.productType = productType;
	}

	private String productTypeAccount; // ITO

	/**
	 * @return the productTypeAccount
	 */
	public String getProductTypeAccount() {
		return productTypeAccount;
	}

	/**
	 * @param productTypeAccount the productTypeAccount to set
	 */
	public void setProductTypeAccount(String productTypeAccount) {
		this.productTypeAccount = productTypeAccount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Product [productAlias=" + productAlias + ", productName=" + productName + ", productType=" + productType + ", productNumber=" + productNumber + ", productDescription="
				+ productDescription + ", productId=" + productId + ", currency=" + currency + ", productNemonic=" + productNemonic + ", productTypeAccount=" + productTypeAccount + ", bankProductId="
				+ bankProductId + ", bankProduct=" + bankProduct + "]";
	}

}
