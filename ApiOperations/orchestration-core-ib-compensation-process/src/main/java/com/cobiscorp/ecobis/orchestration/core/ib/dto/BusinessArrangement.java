package com.cobiscorp.ecobis.orchestration.core.ib.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BusinessArrangement {
	@JsonProperty("CARD_ACCEPTOR_TAX_ID")
	private String cardAcceptorTaxId;

	public String getCardAcceptorTaxId() {
		return cardAcceptorTaxId;
	}

	public void setCardAcceptorTaxId(String cardAcceptorTaxId) {
		this.cardAcceptorTaxId = cardAcceptorTaxId;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BusinessArrangement {").append("cardAcceptorTaxId='").append(cardAcceptorTaxId).append('\'')
				.append('}');
		return sb.toString();
	}
}