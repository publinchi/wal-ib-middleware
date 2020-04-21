/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.User;


/**
 * @author bborja
 * @since 02/06/2015
 * @version 1.0.0
 */
public class AuthenticationDeviceStockDissociateRequest extends BaseRequest{

	private String referenceNumber;	
	private String date;
	private User user;
	private String lostDevice;
	private String dissociateType;
	private String dissociateFinal;
	private String dissociateDuplicate;
	private String dissociateGroup;
	private String dissociateLocation;

	/**
	 * @return the referenceNumber
	 */
	public String getReferenceNumber() {
		return referenceNumber;
	}
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
	/**
	 * @return the lostDevice
	 */
	public String getLostDevice() {
		return lostDevice;
	}
	/**
	 * @param lostDevice the lostDevice to set
	 */
	public void setLostDevice(String lostDevice) {
		this.lostDevice = lostDevice;
	}
	/**
	 * @param referenceNumber the referenceNumber to set
	 */
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	/**
	 * @return the dissociateType
	 */
	public String getDissociateType() {
		return dissociateType;
	}
	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}
	/**
	 * @param dissociateType the dissociateType to set
	 */
	public void setDissociateType(String dissociateType) {
		this.dissociateType = dissociateType;
	}
	/**
	 * @return the dissociateFinal
	 */
	public String getDissociateFinal() {
		return dissociateFinal;
	}
	/**
	 * @param dissociateFinal the dissociateFinal to set
	 */
	public void setDissociateFinal(String dissociateFinal) {
		this.dissociateFinal = dissociateFinal;
	}
	/**
	 * @return the dissociateDuplicate
	 */
	public String getDissociateDuplicate() {
		return dissociateDuplicate;
	}
	/**
	 * @param dissociateDuplicate the dissociateDuplicate to set
	 */
	public void setDissociateDuplicate(String dissociateDuplicate) {
		this.dissociateDuplicate = dissociateDuplicate;
	}
	/**
	 * @return the dissociateGroup
	 */
	public String getDissociateGroup() {
		return dissociateGroup;
	}
	/**
	 * @param dissociateGroup the dissociateGroup to set
	 */
	public void setDissociateGroup(String dissociateGroup) {
		this.dissociateGroup = dissociateGroup;
	}
	/**
	 * @return the dissociateLocation
	 */
	public String getDissociateLocation() {
		return dissociateLocation;
	}
	/**
	 * @param dissociateLocation the dissociateLocation to set
	 */
	public void setDissociateLocation(String dissociateLocation) {
		this.dissociateLocation = dissociateLocation;
	}

}
