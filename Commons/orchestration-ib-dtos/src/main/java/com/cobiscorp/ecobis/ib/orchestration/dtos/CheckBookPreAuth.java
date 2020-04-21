/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author areinoso
 * @since Nov 5, 2014
 * @version 1.0.0
 */
public class CheckBookPreAuth {

	private String Status;

	/**
	 * @return the status
	 */
	public String getStatus() {
		return Status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		Status = status;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CheckBookPreAuthResponse [Status=" + Status + "]";
	}
}
