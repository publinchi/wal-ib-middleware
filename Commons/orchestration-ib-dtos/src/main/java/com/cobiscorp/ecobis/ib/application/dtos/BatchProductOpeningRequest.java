
package com.cobiscorp.ecobis.ib.application.dtos;

import java.math.BigDecimal;
import java.sql.Date;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Batch;

/**
 * @author mmoya
 * @since Jan 27, 2015
 * @version 1.0.0
 */
public class BatchProductOpeningRequest extends BaseRequest {
	
	private String fIni;
	private String fFin;
	private Integer rowsCount;
	private Integer servicio;
    private Batch batch;
    private Integer customer;
    private Integer prodcutId;
    private Integer moneyId;
    private String  account;
    
    
  

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BatchProductOpeningRequest [fIni=" + fIni + ", fFin=" + fFin
				+ ", rowsCount=" + rowsCount + ", servicio=" + servicio
				+ ", batch=" + batch + ", customer=" + customer
				+ ", prodcutId=" + prodcutId + ", moneyId=" + moneyId
				+ ", account=" + account + "]";
	}
	/**
	 * @return the customer
	 */
	public Integer getCustomer() {
		return customer;
	}
	/**
	 * @param customer the customer to set
	 */
	public void setCustomer(Integer customer) {
		this.customer = customer;
	}
	/**
	 * @return the prodcutId
	 */
	public Integer getProdcutId() {
		return prodcutId;
	}
	/**
	 * @param prodcutId the prodcutId to set
	 */
	public void setProdcutId(Integer prodcutId) {
		this.prodcutId = prodcutId;
	}
	/**
	 * @return the moneyId
	 */
	public Integer getMoneyId() {
		return moneyId;
	}
	/**
	 * @param moneyId the moneyId to set
	 */
	public void setMoneyId(Integer moneyId) {
		this.moneyId = moneyId;
	}
	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}
	/**
	 * @param account the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}
	public String getfIni() {
		return fIni;
	}
	public void setfIni(String fIni) {
		this.fIni = fIni;
	}
	public String getfFin() {
		return fFin;
	}
	public void setfFin(String fFin) {
		this.fFin = fFin;
	}
	public Integer getRowsCount() {
		return rowsCount;
	}
	public void setRowsCount(Integer rowsCount) {
		this.rowsCount = rowsCount;
	}
	public Integer getServicio() {
		return servicio;
	}
	public void setServicio(Integer servicio) {
		this.servicio = servicio;
	}
	public Batch getBatch() {
		return batch;
	}
	public void setBatch(Batch batch) {
		this.batch = batch;
	}
	
    
	
}
