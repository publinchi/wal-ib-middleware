package com.cobiscorp.ecobis.ib.orchestration.dtos;

public class Authentication {
	/**
	 * Indicates the token which is used with different authentication methods	
	 */
	private String token;
	/**
	 * Indicates when the user use a method of authentication.	
	 */
	private Boolean active;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
	@Override
	public String toString() {
		return "Authentication [token=" + token + ", active=" + active + "]";
	}
	
	
	

}
