package com.cobiscorp.ecobis.orchestration.core.ib.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PresentationData {

	@JsonProperty("DAY_COUNTER")
	private int dayCounter;

	public int getDayCounter() {
		return dayCounter;
	}

	public void setDayCounter(int dayCounter) {
		this.dayCounter = dayCounter;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PresentationData {").append("dayCounter=").append(dayCounter).append('}');
		return sb.toString();
	}
}