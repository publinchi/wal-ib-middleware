/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * Resultado Cuenta (Sintesis)
 * 
 * @author itorres
 * @since Jul 14, 2015
 * @version 1.0.0
 */
public class AccountDetail {
	private String  codAccount;
	private String  clientName;
	private String  detail;
	private Integer codServices;
	private String  descService;
	private Integer codCurrency;
	/**
	 * @return the codAccount
	 */
	public String getCodAccount() {
		return codAccount;
	}
	/**
	 * @param codAccount the codAccount to set
	 */
	public void setCodAccount(String codAccount) {
		this.codAccount = codAccount;
	}
	/**
	 * @return the clientName
	 */
	public String getClientName() {
		return clientName;
	}
	/**
	 * @param clientName the clientName to set
	 */
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	/**
	 * @return the detail
	 */
	public String getDetail() {
		return detail;
	}
	/**
	 * @param detail the detail to set
	 */
	public void setDetail(String detail) {
		this.detail = detail;
	}
	/**
	 * @return the codServices
	 */
	public Integer getCodServices() {
		return codServices;
	}
	/**
	 * @param codServices the codServices to set
	 */
	public void setCodServices(Integer codServices) {
		this.codServices = codServices;
	}
	/**
	 * @return the descService
	 */
	public String getDescService() {
		return descService;
	}
	/**
	 * @param descService the descService to set
	 */
	public void setDescService(String descService) {
		this.descService = descService;
	}
	/**
	 * @return the codCurrency
	 */
	public Integer getCodCurrency() {
		return codCurrency;
	}
	/**
	 * @param codCurrency the codCurrency to set
	 */
	public void setCodCurrency(Integer codCurrency) {
		this.codCurrency = codCurrency;
	}
	
	
}
