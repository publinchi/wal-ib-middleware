package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * Contains information about message returned.
 * 
 * @author schancay
 * @since Aug 13, 2014
 * @version 1.0.0
 */
public class Message {
	/**
	 * Description of message.
	 */
	private String description;
	/**
	 * Code Error.
	 */
	private String code;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Message [description=" + description + ", code=" + code + "]";
	}
	
}
