package com.cobiscorp.ecobis.orchestration.core.ib.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InstallmentData {

	@JsonProperty("INSTALLMENT_TYPE")
	private String installmentType;

	@JsonProperty("GRACE_PERIOD")
	private int gracePeriod;

	public String getInstallmentType() {
		return installmentType;
	}

	public void setInstallmentType(String installmentType) {
		this.installmentType = installmentType;
	}

	public int getGracePeriod() {
		return gracePeriod;
	}

	public void setGracePeriod(int gracePeriod) {
		this.gracePeriod = gracePeriod;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("InstallmentData {").append("installmentType='").append(installmentType).append('\'')
				.append(", gracePeriod=").append(gracePeriod).append('}');
		return sb.toString();
	}
}