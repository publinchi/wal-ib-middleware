/**
 *
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author schancay
 * @since Sep 3, 2014
 * @version 1.0.0
 */
public class Card {
	/**
	 * Information of product
	 */
	private Product product;
	/**
	 * Information card about payment
	 */
	private Payment payment;

	/**
	 * Information currency of card
	 */
	private Currency currency;

	/**
	 * Card number
	 */
	private String cardNumber;
	/**
	 * Type of card
	 */
	private CardType cardType;
	/**
	 * Card name
	 */
	private String cardName;

	/**
	 * @return the cardName
	 */
	public String getCardName() {
		return cardName;
	}

	/**
	 * @return the cardNumber
	 */
	public String getCardNumber() {
		return cardNumber;
	}

	/**
	 * @return the cardType
	 */
	public CardType getCardType() {
		return cardType;
	}

	/**
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * @return the payment
	 */
	public Payment getPayment() {
		return payment;
	}

	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}

	/**
	 * @param cardName
	 *            the cardName to set
	 */
	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	/**
	 * @param cardNumber
	 *            the cardNumber to set
	 */
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	/**
	 * @param cardType
	 *            the cardType to set
	 */
	public void setCardType(CardType cardType) {
		this.cardType = cardType;
	}

	/**
	 * @param currency
	 *            the currency to set
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	/**
	 * @param payment
	 *            the payment to set
	 */
	public void setPayment(Payment payment) {
		this.payment = payment;
	}

	/**
	 * @param product
	 *            the product to set
	 */
	public void setProduct(Product product) {
		this.product = product;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Card [product=" + product + ", payment=" + payment + ", currency=" + currency + ", cardNumber=" + cardNumber + ", cardType=" + cardType + ", cardName=" + cardName + "]";
	}
}
