/**
 *
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author schancay
 * @since Sep 1, 2014
 * @version 1.0.0
 */
public class Secuential {
	private String secuential;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Secuential [secuential=" + secuential + "]";
	}

	/**
	 * @return the secuential
	 */
	public String getSecuential() {
		return secuential;
	}

	/**
	 * @param secuential
	 *            the secuential to set
	 */
	public void setSecuential(String secuential) {
		this.secuential = secuential;
	}
}
