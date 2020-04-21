/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author jchonillo
 * @since Nov 11, 2014
 * @version 1.0.0
 */
public class BalanceAccount {

	private Double accountingBalance;
	private Double availableBalance;
	private Double balance12Hours;
	private Double balance24Hours;
	private Double balance24Hours2;
	private Double balanceRemittances;
	private Integer bankingProduct;
	private Double blockedAmount;
	private Double consumptionAmount;
	private Double formedAmount;
	private String nameAccount;
	private Double numberOfBlocksPerAmount;
	private Double numberOfLocks;
	private Integer officeAccount;
	private String patent;
	private String result_submit_rpc;
	private Integer ssnHost;
	private String status;
	private Double toDrawBalance;
	
	/**
	 * @return the accountingBalance
	 */
	public Double getAccountingBalance() {
		return accountingBalance;
	}
	public void setAccountingBalance(Double accountingBalance) {
		this.accountingBalance = accountingBalance;
	}
	
	/**
	 * @return the availableBalance
	 */
	public Double getAvailableBalance() {
		return availableBalance;
	}
	public void setAvailableBalance(Double availableBalance) {
		this.availableBalance = availableBalance;
	}
	
	/**
	 * @return the balance12Hours
	 */
	public Double getBalance12Hours() {
		return balance12Hours;
	}
	public void setBalance12Hours(Double balance12Hours) {
		this.balance12Hours = balance12Hours;
	}
	
	/**
	 * @return the balance24Hours
	 */
	public Double getBalance24Hours() {
		return balance24Hours;
	}
	public void setBalance24Hours(Double balance24Hours) {
		this.balance24Hours = balance24Hours;
	}
	
	/**
	 * @return the balanceRemittances
	 */
	public Double getBalanceRemittances() {
		return balanceRemittances;
	}
	public void setBalanceRemittances(Double balanceRemittances) {
		this.balanceRemittances = balanceRemittances;
	}
	
	/**
	 * @return the bankingProduct
	 */
	public Integer getBankingProduct() {
		return bankingProduct;
	}
	public void setBankingProduct(Integer bankingProduct) {
		this.bankingProduct = bankingProduct;
	}
	
	/**
	 * @return the blockedAmount
	 */
	public Double getBlockedAmount() {
		return blockedAmount;
	}
	public void setBlockedAmount(Double blockedAmount) {
		this.blockedAmount = blockedAmount;
	}
	
	/**
	 * @return the consumptionAmount
	 */
	public Double getConsumptionAmount() {
		return consumptionAmount;
	}
	public void setConsumptionAmount(Double consumptionAmount) {
		this.consumptionAmount = consumptionAmount;
	}
	
	/**
	 * @return the formedAmount
	 */
	public Double getFormedAmount() {
		return formedAmount;
	}
	public void setFormedAmount(Double formedAmount) {
		this.formedAmount = formedAmount;
	}
	
	/**
	 * @return the nameAccount
	 */
	public String getNameAccount() {
		return nameAccount;
	}
	public void setNameAccount(String nameAccount) {
		this.nameAccount = nameAccount;
	}
	
	/**
	 * @return the numberOfBlocksPerAmount
	 */
	public Double getNumberOfBlocksPerAmount() {
		return numberOfBlocksPerAmount;
	}
	public void setNumberOfBlocksPerAmount(Double numberOfBlocksPerAmount) {
		this.numberOfBlocksPerAmount = numberOfBlocksPerAmount;
	}
	
	/**
	 * @return the numberOfLocks
	 */
	public Double getNumberOfLocks() {
		return numberOfLocks;
	}
	public void setNumberOfLocks(Double numberOfLocks) {
		this.numberOfLocks = numberOfLocks;
	}
	
	/**
	 * @return the officeAccount
	 */
	public Integer getOfficeAccount() {
		return officeAccount;
	}
	public void setOfficeAccount(Integer officeAccount) {
		this.officeAccount = officeAccount;
	}
	
	/**
	 * @return the patent
	 */
	public String getPatent() {
		return patent;
	}
	public void setPatent(String patent) {
		this.patent = patent;
	}
	
	/**
	 * @return the result_submit_rpc
	 */
	public String getResult_submit_rpc() {
		return result_submit_rpc;
	}
	public void setResult_submit_rpc(String result_submit_rpc) {
		this.result_submit_rpc = result_submit_rpc;
	}
	
	/**
	 * @return the ssnHost
	 */
	public Integer getSsnHost() {
		return ssnHost;
	}
	public void setSsnHost(Integer ssnHost) {
		this.ssnHost = ssnHost;
	}
	
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	/**
	 * @return the toDrawBalance
	 */
	public Double getToDrawBalance() {
		return toDrawBalance;
	}
	public void setToDrawBalance(Double toDrawBalance) {
		this.toDrawBalance = toDrawBalance;
	}
	
	@Override
	public String toString() {
		return "ResponseBalanceAccount [accountingBalance=" + accountingBalance
				+ ", availableBalance=" + availableBalance
				+ ", balance12Hours=" + balance12Hours + ", balance24Hours="
				+ balance24Hours + ", balanceRemittances=" + balanceRemittances
				+ ", bankingProduct=" + bankingProduct + ", blockedAmount="
				+ blockedAmount + ", consumptionAmount=" + consumptionAmount
				+ ", formedAmount=" + formedAmount + ", nameAccount="
				+ nameAccount + ", numberOfBlocksPerAmount="
				+ numberOfBlocksPerAmount + ", numberOfLocks=" + numberOfLocks
				+ ", officeAccount=" + officeAccount + ", patent=" + patent
				+ ", result_submit_rpc=" + result_submit_rpc + ", ssnHost="
				+ ssnHost + ", status=" + status + ", toDrawBalance="
				+ toDrawBalance + "]";
	}
	/**
	 * @return the balance24Hours2
	 */
	public Double getBalance24Hours2() {
		return balance24Hours2;
	}
	/**
	 * @param balance24Hours2 the balance24Hours2 to set
	 */
	public void setBalance24Hours2(Double balance24Hours2) {
		this.balance24Hours2 = balance24Hours2;
	}
	
}
