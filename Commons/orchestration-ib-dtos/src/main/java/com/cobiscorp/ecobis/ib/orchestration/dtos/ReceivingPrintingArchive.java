package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * contain information about bank
 *
 * @author itorres
 * @since september 16, 2014
 * @version 1.0.0
 */
public class ReceivingPrintingArchive {

	private Integer lote;
	private Integer numCard;
	private String user;
	private String observation;
	/**
	 * @return the lote
	 */
	public Integer getLote() {
		return lote;
	}
	/**
	 * @param lote the lote to set
	 */
	public void setLote(Integer lote) {
		this.lote = lote;
	}
	/**
	 * @return the numCard
	 */
	public Integer getNumCard() {
		return numCard;
	}
	/**
	 * @param numCard the numCard to set
	 */
	public void setNumCard(Integer numCard) {
		this.numCard = numCard;
	}
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}
	/**
	 * @return the observation
	 */
	public String getObservation() {
		return observation;
	}
	/**
	 * @param observation the observation to set
	 */
	public void setObservation(String observation) {
		this.observation = observation;
	}
	
	
	
}
