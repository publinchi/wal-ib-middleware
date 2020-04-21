package com.cobiscorp.channels.cc.connector.dto;

public class CreditCardRequest extends BaseRequest {
	
	private String id;
	private String operation;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	

}
