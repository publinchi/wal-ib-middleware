package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * Class used to save all the conditions of signers to send a transaction
 * @author djarrin
 *
 */
public class Signer {
	
	/**
	 * condition
	 */
	private String condition;

	/**
	 * @return the condition
	 */
	public String getCondition() {
		return condition;
	}

	/**
	 * @param condition the condition to set
	 */
	public void setCondition(String condition) {
		this.condition = condition;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Signer [condition=" + condition + "]";
	}
	
	

}
