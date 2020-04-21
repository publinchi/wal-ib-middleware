/**
 *
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author schancay
 * @since Sep 3, 2014
 * @version 1.0.0
 */
public class CardType {
	/**
	 * Card type
	 */
	private String cardType;
	/**
	 * Card type description
	 */
	private String cardTypeDescription;

	/**
	 * @return the cardType
	 */
	public String getCardType() {
		return cardType;
	}

	/**
	 * @return the cardTypeDescription
	 */
	public String getCardTypeDescription() {
		return cardTypeDescription;
	}

	/**
	 * @param cardType
	 *            the cardType to set
	 */
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}

	/**
	 * @param cardTypeDescription
	 *            the cardTypeDescription to set
	 */
	public void setCardTypeDescription(String cardTypeDescription) {
		this.cardTypeDescription = cardTypeDescription;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CardType [cardType=" + cardType + ", cardTypeDescription=" + cardTypeDescription + "]";
	}

}
