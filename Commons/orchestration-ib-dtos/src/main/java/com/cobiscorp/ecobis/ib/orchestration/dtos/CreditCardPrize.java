/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author jlvidal
 * @since Oct 15, 2014
 * @version 1.0.0
 */
public class CreditCardPrize {
	private Card card;
	private Integer totalPrize;
	private String cutoffDate;
	/**
	 * @return the card
	 */
	public Card getCard() {
		return card;
	}
	/**
	 * @param card the card to set
	 */
	public void setCard(Card card) {
		this.card = card;
	}
	/**
	 * @return the totalPrize
	 */
	public Integer getTotalPrize() {
		return totalPrize;
	}
	/**
	 * @param totalPrize the totalPrize to set
	 */
	public void setTotalPrize(Integer totalPrize) {
		this.totalPrize = totalPrize;
	}
	/**
	 * @return the cutoffDate
	 */
	public String getCutoffDate() {
		return cutoffDate;
	}
	/**
	 * @param cutoffDate the cutoffDate to set
	 */
	public void setCutoffDate(String cutoffDate) {
		this.cutoffDate = cutoffDate;
	}
	
}
