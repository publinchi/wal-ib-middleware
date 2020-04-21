package com.cobiscorp.channels.cc.connector.dto;

public class BaseRequest {

	private String entityId;
	private String customerId;
	private String culture;
	private String login;
	private String dateFormatId;
	private ConnectionParameters connectionParameters;

	public ConnectionParameters getConnectionParameters() {
		return connectionParameters;
	}

	public void setConnectionParameters(ConnectionParameters connectionParameters) {
		this.connectionParameters = connectionParameters;
	}

	public String getCulture() {
		return culture;
	}

	public void setCulture(String culture) {
		this.culture = culture;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	
	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getDateFormatId() {
		return dateFormatId;
	}

	public void setDateFormatId(String dateFormatId) {
		this.dateFormatId = dateFormatId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	
	
}
