package com.cobiscorp.ecobis.orchestration.core.ib.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MerchantData {

	@JsonProperty("SERVICE_LOCATION")
	private String serviceLocation;

	@JsonProperty("ACCEPTOR_ID")
	private String acceptorId;

	@JsonProperty("TERMINAL_ID")
	private String terminalId;

	public String getServiceLocation() {
		return serviceLocation;
	}

	public void setServiceLocation(String serviceLocation) {
		this.serviceLocation = serviceLocation;
	}

	public String getAcceptorId() {
		return acceptorId;
	}

	public void setAcceptorId(String acceptorId) {
		this.acceptorId = acceptorId;
	}

	public String getTerminalId() {
		return acceptorId;
	}

	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("MerchantData {").append("serviceLocation='").append(serviceLocation).append('\'')
				.append(", acceptorId='").append(acceptorId).append('\'').append(", terminalId='").append(terminalId)
				.append('\'').append('}');
		return sb.toString();
	}
}