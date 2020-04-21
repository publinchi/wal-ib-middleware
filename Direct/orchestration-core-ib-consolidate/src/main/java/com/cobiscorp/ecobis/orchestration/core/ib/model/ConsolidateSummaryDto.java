package com.cobiscorp.ecobis.orchestration.core.ib.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class ConsolidateSummaryDto implements Serializable {

	private static final long serialVersionUID = 1L;

	public ConsolidateSummaryDto() {
		super();
	}

	public ConsolidateSummaryDto(Integer numberId, Integer productId, BigDecimal balance, Integer currencyId) {
		super();
		this.numberId = numberId;
		this.productId = productId;
		this.balance = balance;
		this.currencyId = currencyId;
	}

	private Integer numberId;
	private Integer productId;
	private BigDecimal balance;
	private String morgage;
	private Integer currencyId;
	private String currencyDescription;
	private String productDescription;

	private BigDecimal cardsAmmountPayment;

	public BigDecimal getCardsAmmountPayment() {
		return cardsAmmountPayment;
	}

	public void setCardsAmmountPayment(BigDecimal cardsAmmountPayment) {
		this.cardsAmmountPayment = cardsAmmountPayment;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public Integer getNumberId() {
		return numberId;
	}

	public void setNumberId(Integer numberId) {
		this.numberId = numberId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getMorgage() {
		return morgage;
	}

	public void setMorgage(String morgage) {
		this.morgage = morgage;
	}

	public Integer getCurrencyId() {
		return currencyId;
	}

	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}

	public String getCurrencyDescription() {
		return currencyDescription;
	}

	public void setCurrencyDescription(String currencyDescription) {
		this.currencyDescription = currencyDescription;
	}

	@Override
	public String toString() {
		return "ConsolidateSummaryDto [numberId=" + numberId + ", productId=" + productId + ", balance=" + balance
				+ ", morgage=" + morgage + ", currencyId=" + currencyId + ", currencyDescription=" + currencyDescription
				+ ", productDescription=" + productDescription + ", cardsAmmountPayment=" + cardsAmmountPayment + "]";
	}

}
