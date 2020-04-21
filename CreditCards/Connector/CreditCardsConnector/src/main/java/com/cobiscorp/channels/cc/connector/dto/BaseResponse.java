package com.cobiscorp.channels.cc.connector.dto;

public class BaseResponse {

	private Boolean success;
	private Message message;

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

}
