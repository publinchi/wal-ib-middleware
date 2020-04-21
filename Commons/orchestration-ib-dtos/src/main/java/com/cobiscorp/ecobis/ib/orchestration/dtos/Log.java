/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

import java.math.BigDecimal;

/**
 * @author dmorla
 * @since 18/06/2015
 * @version 1.0.0
 */
public class Log {

	private Integer company;
	private Integer transaction;
	private Integer entity;
	private Integer service;
	private String status;
	private String date;
	private Integer sequency;
	private String hour;
	private Integer product;
	private Integer money;
	private String account;
	private BigDecimal fee;
	private Integer office;
	private Integer aux;
	private String originatorFunds;
	private String receiverFunds;
	
	/**
	 * @return the company
	 */
	public Integer getCompany() {
		return company;
	}

	/**
	 * @param company
	 *            the company to set
	 */
	public void setCompany(Integer company) {
		this.company = company;
	}

	/**
	 * @return the transaction
	 */
	public Integer getTransaction() {
		return transaction;
	}

	/**
	 * @param transaction
	 *            the transaction to set
	 */
	public void setTransaction(Integer transaction) {
		this.transaction = transaction;
	}

	/**
	 * @return the entity
	 */
	public Integer getEntity() {
		return entity;
	}

	/**
	 * @param entity
	 *            the entity to set
	 */
	public void setEntity(Integer entity) {
		this.entity = entity;
	}

	/**
	 * @return the service
	 */
	public Integer getService() {
		return service;
	}

	/**
	 * @param service
	 *            the service to set
	 */
	public void setService(Integer service) {
		this.service = service;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return the sequency
	 */
	public Integer getSequency() {
		return sequency;
	}

	/**
	 * @param sequency
	 *            the sequency to set
	 */
	public void setSequency(Integer sequency) {
		this.sequency = sequency;
	}

	/**
	 * @return the hour
	 */
	public String getHour() {
		return hour;
	}

	/**
	 * @param hour
	 *            the hour to set
	 */
	public void setHour(String hour) {
		this.hour = hour;
	}

	/**
	 * @return the product
	 */
	public Integer getProduct() {
		return product;
	}

	/**
	 * @param product
	 *            the product to set
	 */
	public void setProduct(Integer product) {
		this.product = product;
	}

	/**
	 * @return the money
	 */
	public Integer getMoney() {
		return money;
	}

	/**
	 * @param money
	 *            the money to set
	 */
	public void setMoney(Integer money) {
		this.money = money;
	}

	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * @param account
	 *            the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	/**
	 * @return the fee
	 */
	public BigDecimal getFee() {
		return fee;
	}

	/**
	 * @param fee
	 *            the fee to set
	 */
	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	/**
	 * @return the office
	 */
	public Integer getOffice() {
		return office;
	}

	/**
	 * @param office
	 *            the office to set
	 */
	public void setOffice(Integer office) {
		this.office = office;
	}

	/**
	 * @return the aux
	 */
	public Integer getAux() {
		return aux;
	}

	/**
	 * @param aux
	 *            the aux to set
	 */
	public void setAux(Integer aux) {
		this.aux = aux;
	}

	/**
	 * @return the originatorFunds
	 */
	public String getOriginatorFunds() {
		return originatorFunds;
	}

	/**
	 * @param originatorFunds the originatorFunds to set
	 */
	public void setOriginatorFunds(String originatorFunds) {
		this.originatorFunds = originatorFunds;
	}

	/**
	 * @return the receiverFunds
	 */
	public String getReceiverFunds() {
		return receiverFunds;
	}

	/**
	 * @param receiverFunds the receiverFunds to set
	 */
	public void setReceiverFunds(String receiverFunds) {
		this.receiverFunds = receiverFunds;
	}

}
