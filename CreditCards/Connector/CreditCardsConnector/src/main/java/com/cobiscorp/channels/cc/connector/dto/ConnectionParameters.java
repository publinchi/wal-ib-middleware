package com.cobiscorp.channels.cc.connector.dto;

public class ConnectionParameters {

	private String url;
	private Integer connectionTimeout; 
    private Integer readTimeout;
    
    
	public ConnectionParameters(String url, Integer connectionTimeout, Integer readTimeout) {
		super();
		this.url = url;
		this.connectionTimeout = connectionTimeout;
		this.readTimeout = readTimeout;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Integer getConnectionTimeout() {
		return connectionTimeout;
	}
	public void setConnectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	public Integer getReadTimeout() {
		return readTimeout;
	}
	public void setReadTimeout(Integer readTimeout) {
		this.readTimeout = readTimeout;
	}
    
    
	
}
