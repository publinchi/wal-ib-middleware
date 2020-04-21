package com.cobiscorp.ecobis.orchestration.core.ib.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class TotalDetailsAccountDto implements Serializable {

	private static final long serialVersionUID = 1L;
	private BigDecimal assets;
	private BigDecimal liabilities;
	private Integer currencyId;
	private String currencyDescription;

	public BigDecimal getAssets() {
		return assets;
	}

	public void setAssets(BigDecimal assets) {
		this.assets = assets;
	}

	public BigDecimal getLiabilities() {
		return liabilities;
	}

	public void setLiabilities(BigDecimal liabilities) {
		this.liabilities = liabilities;
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
}
