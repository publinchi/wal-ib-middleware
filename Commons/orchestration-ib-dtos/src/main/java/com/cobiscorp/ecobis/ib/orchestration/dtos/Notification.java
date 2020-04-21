package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * Contains information about Client used in transactions.
 * 
 * @author eortega
 * @since Aug 14, 2014
 * @version 1.0.0
 */
public class Notification {
	/**
	 * notification id
	 */
	private String id;
	/**
	 * type of notification
	 */
	private String notificationType;
	private String messageType;
	/**
	 * type process
	 */
	private String isBatch;

	/**
	 * @return the messageType
	 */
	public String getMessageType() {
		return messageType;
	}

	/**
	 * @param messageType the messageType to set
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}

	public String getIsBatch() {
		return isBatch;
	}

	public void setIsBatch(String isBatch) {
		this.isBatch = isBatch;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(String notificationType) {
		this.notificationType = notificationType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Notification [id=" + id + ", notificationType="
				+ notificationType + ", messageType=" + messageType
				+ ", isBatch=" + isBatch + "]";
	}

	

}
