package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * Contains information about Client used in transactions.
 * 
 * @author djarrin
 * @since Aug 13, 2014
 * @version 1.0.0
 */
public class Client {
	/**
	 * id of client.(ente_mis)
	 */
	private String id;
	/**
	 * id of client.(ente_ib)
	 */
	private String idCustomer;
	/**
	 * User name of core.
	 */
	private String login;
	/**
	 * Password encrypt of login.
	 */
	private String password;
	/**
	 * First name of client.
	 */
	private String firtsName;
	/**
	 * Last name of client.
	 */
	private String lastName;
	/**
	 * Complete name of client.
	 */
	private String completeName;

	/**
	 * Get Ci or Ruc Number
	 */
	private String identification;

	private String phone;
	private String mail;
	private String phoneOp;
	public String getPhoneOp() {
		return phoneOp;
	}

	public void setPhoneOp(String phoneOp) {
		this.phoneOp = phoneOp;
	}

	public String getMailOp() {
		return mailOp;
	}

	public void setMailOp(String mailOp) {
		this.mailOp = mailOp;
	}

	private String mailOp;
	private String birthDate;

	public String getCompleteName() {
		return completeName;
	}

	public String getFirtsName() {
		return firtsName;
	}

	public String getId() {
		return id;
	}

	public String getIdCustomer() {
		return idCustomer;
	}

	/**
	 * @return the identification
	 */
	public String getIdentification() {
		return identification;
	}

	public String getLastName() {
		return lastName;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

	public void setCompleteName(String completeName) {
		this.completeName = completeName;
	}

	public void setFirtsName(String firtsName) {
		this.firtsName = firtsName;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setIdCustomer(String idCustomer) {
		this.idCustomer = idCustomer;
	}

	/**
	 * @param identification the identification to set
	 */
	public void setIdentification(String identification) {
		this.identification = identification;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = birthDate;
	}

	@Override
	public String toString() {
		return "Client [id=" + id + ", idCustomer=" + idCustomer + ", login=" + login + ", password=" + password + ", firtsName=" + firtsName + ", lastName=" + lastName + ", completeName="
				+ completeName + ", identification=" + identification + ", phone=" + phone + ", mail=" + mail + ",phoneOp=" + phoneOp + ", mailOp=" + mailOp + ", birthDate=" + birthDate + "]";
	}
}
