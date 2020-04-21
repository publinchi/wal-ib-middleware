/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author kmeza
 * @since Oct 21, 2014
 * @version 1.0.0
 */
public class PaymentLoan {
	
	
	private Integer returnValue;
	private Integer conditionId;
	private Integer reference;
	private Integer authorizationRequeried;
	private Integer branchSsn;
	
	/**
	 * @return the returnValue
	 */
	public Integer getReturnValue() {
		return returnValue;
	}
	/**
	 * @param returnValue the returnValue to set
	 */
	public void setReturnValue(Integer returnValue) {
		this.returnValue = returnValue;
	}
	/**
	 * @return the conditionId
	 */
	public Integer getConditionId() {
		return conditionId;
	}
	/**
	 * @param conditionId the conditionId to set
	 */
	public void setConditionId(Integer conditionId) {
		this.conditionId = conditionId;
	}
	/**
	 * @return the reference
	 */
	public Integer getReference() {
		return reference;
	}
	/**
	 * @param reference the reference to set
	 */
	public void setReference(Integer reference) {
		this.reference = reference;
	}
	/**
	 * @return the authorizationRequeried
	 */
	public Integer getAuthorizationRequeried() {
		return authorizationRequeried;
	}
	/**
	 * @param authorizationRequeried the authorizationRequeried to set
	 */
	public void setAuthorizationRequeried(Integer authorizationRequeried) {
		this.authorizationRequeried = authorizationRequeried;
	}
	/**
	 * @return the branchSsn
	 */
	public Integer getBranchSsn() {
		return branchSsn;
	}
	/**
	 * @param branchSsn the branchSsn to set
	 */
	public void setBranchSsn(Integer branchSsn) {
		this.branchSsn = branchSsn;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PaymentLoan [returnValue=" + returnValue + ", conditionId="
				+ conditionId + ", reference=" + reference
				+ ", authorizationRequeried=" + authorizationRequeried
				+ ", branchSsn=" + branchSsn + "]";
	}
	
	
	
	
	
	

}
