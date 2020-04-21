package com.cobiscorp.ecobis.orchestration.core.ib.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Dto for set information credit card.
 *
 * @author schancay
 * @since Jul 4, 2014
 * @version 1.0.0
 */
public class ConsolidateCardsDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private String number;
	private String name;
	private String type;
	private String typeName;
	private String datePayment;
	private Integer productId;
	private String productDescription;
	private String productNemonic;
	private String productNumber;
	private BigDecimal ammountPayment;
	private Integer currencyId;

	private String currencyDescription;

	private String currencyNemonic;

	private String cardAP;

	/**
	 * @return the ammountPayment
	 */
	public BigDecimal getAmmountPayment() {
		return ammountPayment;
	}

	/**
	 * @return the cardAP
	 */
	public String getCardAP() {
		return cardAP;
	}

	public String getCurrencyDescription() {
		return currencyDescription;
	}

	/**
	 * @return the currencyId
	 */
	public Integer getCurrencyId() {
		return currencyId;
	}

	/**
	 * @return the currencyNemonic
	 */
	public String getCurrencyNemonic() {
		return currencyNemonic;
	}

	/**
	 * @return the datePayment
	 */
	public String getDatePayment() {
		return datePayment;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the number
	 */
	public String getNumber() {
		return number;
	}

	/**
	 * @return the productDescription
	 */
	public String getProductDescription() {
		return productDescription;
	}

	/**
	 * @return the productId
	 */
	public Integer getProductId() {
		return productId;
	}

	/**
	 * @return the productNemonic
	 */
	public String getProductNemonic() {
		return productNemonic;
	}

	/**
	 * @return the productNumber
	 */
	public String getProductNumber() {
		return productNumber;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the typeName
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * @param ammountPayment
	 *            the ammountPayment to set
	 */
	public void setAmmountPayment(BigDecimal ammountPayment) {
		this.ammountPayment = ammountPayment;
	}

	/**
	 * @param cardAP
	 *            the cardAP to set
	 */
	public void setCardAP(String cardAP) {
		this.cardAP = cardAP;
	}

	public void setCurrencyDescription(String currencyDescription) {
		this.currencyDescription = currencyDescription;
	}

	/**
	 * @param currencyId
	 *            the currencyId to set
	 */
	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}

	/**
	 * @param currencyNemonic
	 *            the currencyNemonic to set
	 */
	public void setCurrencyNemonic(String currencyNemonic) {
		this.currencyNemonic = currencyNemonic;
	}

	/**
	 * @param datePayment
	 *            the datePayment to set
	 */
	public void setDatePayment(String datePayment) {
		this.datePayment = datePayment;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param number
	 *            the number to set
	 */
	public void setNumber(String number) {
		this.number = number;
	}

	/**
	 * @param productDescription
	 *            the productDescription to set
	 */
	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	/**
	 * @param productId
	 *            the productId to set
	 */
	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	/**
	 * @param productNemonic
	 *            the productNemonic to set
	 */
	public void setProductNemonic(String productNemonic) {
		this.productNemonic = productNemonic;
	}

	/**
	 * @param productNumber
	 *            the productNumber to set
	 */
	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @param typeName
	 *            the typeName to set
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ConsolidateCardsDto [number=" + number + ", name=" + name + ", type=" + type + ", typeName=" + typeName
				+ ", datePayment=" + datePayment + ", productId=" + productId + ", productDescription="
				+ productDescription + ", productNemonic=" + productNemonic + ", productNumber=" + productNumber
				+ ", ammountPayment=" + ammountPayment + ", currencyId=" + currencyId + ", currencyDescription="
				+ currencyDescription + ", currencyNemonic=" + currencyNemonic + ", cardAP=" + cardAP + "]";
	}
}
