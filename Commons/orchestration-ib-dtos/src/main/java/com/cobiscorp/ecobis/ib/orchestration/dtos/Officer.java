package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * Contains information about office
 * 
 * @author djarrin
 * @since Aug 19, 2014
 * @version 1.0.0
 */
public class Officer {

	/**
	 * Contains E-mail Address of account
	 */
	private String acountEmailAdress;
	/**
	 * Contains E-mail Address of the Officer
	 */
	private String officerEmailAdress;
	/**
	 * @return the acountEmailAdress
	 */
	public String getAcountEmailAdress() {
		return acountEmailAdress;
	}
	/**
	 * @param acountEmailAdress the acountEmailAdress to set
	 */
	public void setAcountEmailAdress(String acountEmailAdress) {
		this.acountEmailAdress = acountEmailAdress;
	}
	/**
	 * @return the officerEmailAdress
	 */
	public String getOfficerEmailAdress() {
		return officerEmailAdress;
	}
	/**
	 * @param officerEmailAdress the officerEmailAdress to set
	 */
	public void setOfficerEmailAdress(String officerEmailAdress) {
		this.officerEmailAdress = officerEmailAdress;
	}
	
}
