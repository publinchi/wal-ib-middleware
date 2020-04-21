package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * Contains information about the channel which sends the transaction
 *
 * @author djarrin
 * @since Aug 13, 2014
 * @version 1.0.0
 */
public class ChannelContext {
	private String sessionIdIB;
	private String sessionIdCore;
	private String channelId;

	/**
	 * @return the channelId
	 */
	public String getChannelId() {
		return channelId;
	}

	/**
	 * @param channelId
	 *            the channelId to set
	 */
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ChannelContext [sessionIdIB=" + sessionIdIB + ", sessionIdCore=" + sessionIdCore + ", channelId=" + channelId + "]";
	}

	/**
	 * @return the sessionIdIB
	 */
	public String getSessionIdIB() {
		return sessionIdIB;
	}

	/**
	 * @param sessionIdIB
	 *            the sessionIdIB to set
	 */
	public void setSessionIdIB(String sessionIdIB) {
		this.sessionIdIB = sessionIdIB;
	}

	/**
	 * @return the sessionIdCore
	 */
	public String getSessionIdCore() {
		return sessionIdCore;
	}

	/**
	 * @param sessionIdCore
	 *            the sessionIdCore to set
	 */
	public void setSessionIdCore(String sessionIdCore) {
		this.sessionIdCore = sessionIdCore;
	}

}
