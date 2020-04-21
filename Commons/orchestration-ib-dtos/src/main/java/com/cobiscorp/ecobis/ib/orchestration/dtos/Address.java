/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * Contains information about Address used in transactions.
 *
 * @author itorres
 * @since Oct 9, 2014
 * @version 1.0.0
 */
public class Address {
	
	/**
	 * additionalInformation
	 */
	private String additionalInformation;	
	
	/**
	 * description phone
	 */
	private String phone; 

	/**
	 * description neighborhood
	 */
	private String neighborhood;

	/**
	 * description street
	 */
	private String street;

	/**
	 * description building
	 */
	private String building;

	/**
	 * description house
	 */
	private String house;

	/**
	 * description email
	 */
	private String email;	
	
	/**
	 * code of phone
	 */
	private Integer phoneCode;
	
	/**
	 * code of address
	 */
	private Integer addressCode;
	
	/**
	 * code of email
	 */
	private Integer emailCode;
	
	/**
	 * description Address
	 */
	private String description;	

	/**
	 * @return the additionalInformation
	 */
	public String getAdditionalInformation() {
		return additionalInformation;
	}

	/**
	 * @param additionalInformation the additionalInformation to set
	 */
	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return the neighborhood
	 */
	public String getNeighborhood() {
		return neighborhood;
	}

	/**
	 * @param neighborhood the neighborhood to set
	 */
	public void setNeighborhood(String neighborhood) {
		this.neighborhood = neighborhood;
	}

	/**
	 * @return the street
	 */
	public String getStreet() {
		return street;
	}

	/**
	 * @param street the street to set
	 */
	public void setStreet(String street) {
		this.street = street;
	}

	/**
	 * @return the building
	 */
	public String getBuilding() {
		return building;
	}

	/**
	 * @param building the building to set
	 */
	public void setBuilding(String building) {
		this.building = building;
	}

	/**
	 * @return the house
	 */
	public String getHouse() {
		return house;
	}

	/**
	 * @param house the house to set
	 */
	public void setHouse(String house) {
		this.house = house;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the phoneCode
	 */
	public Integer getPhoneCode() {
		return phoneCode;
	}

	/**
	 * @param phoneCode the phoneCode to set
	 */
	public void setPhoneCode(Integer phoneCode) {
		this.phoneCode = phoneCode;
	}

	/**
	 * @return the addressCode
	 */
	public Integer getAddressCode() {
		return addressCode;
	}

	/**
	 * @param addressCode the addressCode to set
	 */
	public void setAddressCode(Integer addressCode) {
		this.addressCode = addressCode;
	}

	/**
	 * @return the emailCode
	 */
	public Integer getEmailCode() {
		return emailCode;
	}

	/**
	 * @param emailCode the emailCode to set
	 */
	public void setEmailCode(Integer emailCode) {
		this.emailCode = emailCode;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}				

}
