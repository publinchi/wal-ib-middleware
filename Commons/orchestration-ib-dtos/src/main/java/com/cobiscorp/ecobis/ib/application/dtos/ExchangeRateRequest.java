/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

/**
 * @author bborja
 * @since 20/2/2015
 * @version 1.0.0
 */
public class ExchangeRateRequest extends BaseRequest {
	private String server;
	private String user;
	private String processDate;
	private String target;
	private String spName;
	private int trnCode;
	private String orgCurrency;
	private String destCurrency;
	
	/**
	 * @return the orgCurrency
	 */
	public String getOrgCurrency() {
		return orgCurrency;
	}
	/**
	 * @param orgCurrency the orgCurrency to set
	 */
	public void setOrgCurrency(String orgCurrency) {
		this.orgCurrency = orgCurrency;
	}
	/**
	 * @return the destCurrency
	 */
	public String getDestCurrency() {
		return destCurrency;
	}
	/**
	 * @param destCurrency the destCurrency to set
	 */
	public void setDestCurrency(String destCurrency) {
		this.destCurrency = destCurrency;
	}
	
	/**
	 * @return the spName
	 */
	public String getSpName() {
		return spName;
	}
	/**
	 * @param spName the spName to set
	 */
	public void setSpName(String spName) {
		this.spName = spName;
	}
	/**
	 * @return the trnCode
	 */
	public int getTrnCode() {
		return trnCode;
	}
	/**
	 * @param trnCode the trnCode to set
	 */
	public void setTrnCode(int trnCode) {
		this.trnCode = trnCode;
	}
	/**
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}
	/**
	 * @param target the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}
	/**
	 * @return the server
	 */
	public String getServer() {
		return server;
	}
	/**
	 * @param server the server to set
	 */
	public void setServer(String server) {
		this.server = server;
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
	 * @return the processDate
	 */
	public String getProcessDate() {
		return processDate;
	}
	/**
	 * @param processDate the processDate to set
	 */
	public void setProcessDate(String processDate) {
		this.processDate = processDate;
	}

}
