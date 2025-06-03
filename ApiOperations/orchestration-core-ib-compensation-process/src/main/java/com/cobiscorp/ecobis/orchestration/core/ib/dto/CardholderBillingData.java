package com.cobiscorp.ecobis.orchestration.core.ib.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CardholderBillingData {

	@JsonProperty("BILLING_VALUE")
	private double billingValue;

	@JsonProperty("BILLING_CURRENCY")
	private int billingCurrency;

	@JsonProperty("BILLING_CONVERSION_RATE")
	private double billingConversionRate;

	public double getBillingValue() {
		return billingValue;
	}

	public void setBillingValue(double billingValue) {
		this.billingValue = billingValue;
	}

	public int getBillingCurrency() {
		return billingCurrency;
	}

	public void setBillingCurrency(int billingCurrency) {
		this.billingCurrency = billingCurrency;
	}

	public double getBillingConversionRate() {
		return billingConversionRate;
	}

	public void setBillingConversionRate(double billingConversionRate) {
		this.billingConversionRate = billingConversionRate;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("CardholderBillingData {").append("billingValue=").append(billingValue).append(", billingCurrency=")
				.append(billingCurrency).append(", billingConversionRate=").append(billingConversionRate).append('}');
		return sb.toString();
	}
}