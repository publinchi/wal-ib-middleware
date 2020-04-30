package com.cobiscorp.ecobis.ib.orchestration.dtos;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Contains information about the Balances of the product
 *
 * @author djarrin
 * @since Aug 19, 2014
 * @version 1.0.0
 */
public class BalanceProduct {
	/**
	 * Indicate delivery address
	 */
	private String deliveryAddress;
	/**
	 * Indicate check balance
	 */
	private BigDecimal checkBalance;

	/**
	 * Indicate overdraft balance
	 */
	private BigDecimal overdraftBalance;

	/**
	 * Indicate the openning date of account
	 */
	private String openingDate;
	/**
	 * Indicates the balance in exchange
	 */
	private BigDecimal inExchangeBalance;

	/**
	 * Indicates the balance embargoed
	 */
	private BigDecimal embargoedBalance;

	/**
	 * Indicates the balance available in account
	 */
	private Product product;
	/**
	 * Indicates the Cliente of account
	 */
	private Client client;

	/**
	 * Indicates the balance total in account
	 */
	private BigDecimal totalBalance;

	/**
	 * Indicates the balance draw in account
	 */
	private BigDecimal drawBalance;
	/**
	 * Indicates the rate
	 */
	private String rate;

	/**
	 * Indicates the expiration Date
	 */
	private Date expirationDate;

	/**
	 * Indicates the balance available in account
	 */
	private BigDecimal availableBalance;

	/**
	 * Indicates the balance equity in account
	 */
	private BigDecimal equityBalance;

	/**
	 * Indicates the balance rotate in account
	 */
	private BigDecimal rotateBalance;
	/**
	 * Indicates the balance of account in last 12 hour
	 */
	private BigDecimal balance12H;

	/**
	 * Indicates the balance of account in last 24 hour
	 */
	private BigDecimal balance24H;

	/**
	 * Indicates the balance remittances
	 */
	private BigDecimal remittancesBalance;

	/**
	 * Indicates the ammount blocked in account
	 */
	private BigDecimal blockedAmmount;

	/**
	 * Indicates the identifier of blocked
	 */
	private Integer blockedNumber;

	/**
	 * Indicate the identifier of blocked by ammounts
	 */
	private Integer blockedNumberAmmount;

	/**
	 * Indicate the process date of system
	 */
	private Date processDate;

	/**
	 * Indicates the state account
	 */
	private String state;

	/**
	 * Indicates the ammount surplus
	 */
	private BigDecimal surplusAmmount;

	/**
	 * Indicates the code id cash balance close
	 */
	private Integer idClosed;

	/**
	 * Indicates the cash Balance
	 */
	private BigDecimal cashBalance;

	/**
	 * Indicates the next value of payment
	 */
	private BigDecimal nextPaymentValue;

	/**
	 * Indicates the date of last movent
	 */
	private String dateLastMovent;
	/**
	 * Indicate the office of account
	 */
	private Office officeAccount;

	/**
	 * ProductBanking
	 */
	private ProductBanking productBanking;

	/**
	 * ssnHost Identifier Getting process
	 */
	private Integer ssnHost;

	/**
	 * Indicates the balance draw in account
	 */
	private BigDecimal accountingBalance;	

	/**
	 * Indicates the type of account: Single, indistinct or joint
	 */
	private String type;   
	/**
	 * Indicates the type of account: Single, indistinct or joint
	 */
	private String oficial;   
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the accountingBalance
	 */
	public BigDecimal getAccountingBalance() {
		return accountingBalance;
	}

	/**
	 * @return the availableBalance
	 */
	public BigDecimal getAvailableBalance() {
		return availableBalance;
	}

	/**
	 * @return the balance12H
	 */
	public BigDecimal getBalance12H() {
		return balance12H;
	}

	/**
	 * @return the balance24H
	 */
	public BigDecimal getBalance24H() {
		return balance24H;
	}

	/**
	 * @return the blockedAmmount
	 */
	public BigDecimal getBlockedAmmount() {
		return blockedAmmount;
	}

	/**
	 * @return the blockedNumber
	 */
	public Integer getBlockedNumber() {
		return blockedNumber;
	}

	/**
	 * @return the blockedNumberAmmount
	 */
	public Integer getBlockedNumberAmmount() {
		return blockedNumberAmmount;
	}

	/**
	 * @return the cashBalance
	 */
	public BigDecimal getCashBalance() {
		return cashBalance;
	}

	/**
	 * @return the checkBalance
	 */
	public BigDecimal getCheckBalance() {
		return checkBalance;
	}

	/**
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * @return the dateLastMovent
	 */
	public String getDateLastMovent() {
		return dateLastMovent;
	}

	public BigDecimal getNextPaymentValue() {
		return nextPaymentValue;
	}

	public void setNextPaymentValue(BigDecimal nextPaymentValue) {
		this.nextPaymentValue = nextPaymentValue;
	}

	/**
	 * @return the deliveryAddress
	 */
	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	/**
	 * @return the drawBalance
	 */
	public BigDecimal getDrawBalance() {
		return drawBalance;
	}

	/**
	 * @return the embargoedBalance
	 */
	public BigDecimal getEmbargoedBalance() {
		return embargoedBalance;
	}

	/**
	 * @return the equityBalance
	 */
	public BigDecimal getEquityBalance() {
		return equityBalance;
	}

	/**
	 * @return the idClosed
	 */
	public Integer getIdClosed() {
		return idClosed;
	}

	/**
	 * @return the inExchangeBalance
	 */
	public BigDecimal getInExchangeBalance() {
		return inExchangeBalance;
	}

	/**
	 * @return the officeAccount
	 */
	public Office getOfficeAccount() {
		return officeAccount;
	}

	/**
	 * @return the openingDate
	 */
	public String getOpeningDate() {
		return openingDate;
	}

	/**
	 * @return the overdraftBalance
	 */
	public BigDecimal getOverdraftBalance() {
		return overdraftBalance;
	}

	/**
	 * @return the processDate
	 */
	public Date getProcessDate() {
		return processDate;
	}

	/**
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}

	/**
	 * @return the productBanking
	 */
	public ProductBanking getProductBanking() {
		return productBanking;
	}

	/**
	 * @return the remittancesBalance
	 */
	public BigDecimal getRemittancesBalance() {
		return remittancesBalance;
	}

	/**
	 * @return the rotateBalance
	 */
	public BigDecimal getRotateBalance() {
		return rotateBalance;
	}

	/**
	 * @return the ssnHost
	 */
	public Integer getSsnHost() {
		return ssnHost;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @return the surplusAmmount
	 */
	public BigDecimal getSurplusAmmount() {
		return surplusAmmount;
	}

	/**
	 * @return the totalBalance
	 */
	public BigDecimal getTotalBalance() {
		return totalBalance;
	}

	/**
	 * @param accountingBalance
	 *            the accountingBalance to set
	 */
	public void setAccountingBalance(BigDecimal accountingBalance) {
		this.accountingBalance = accountingBalance;
	}

	/**
	 * @param availableBalance
	 *            the availableBalance to set
	 */
	public void setAvailableBalance(BigDecimal availableBalance) {
		this.availableBalance = availableBalance;
	}

	/**
	 * @param balance12h
	 *            the balance12H to set
	 */
	public void setBalance12H(BigDecimal balance12h) {
		balance12H = balance12h;
	}

	/**
	 * @param balance24h
	 *            the balance24H to set
	 */
	public void setBalance24H(BigDecimal balance24h) {
		balance24H = balance24h;
	}

	/**
	 * @param blockedAmmount
	 *            the blockedAmmount to set
	 */
	public void setBlockedAmmount(BigDecimal blockedAmmount) {
		this.blockedAmmount = blockedAmmount;
	}

	/**
	 * @param blockedNumber
	 *            the blockedNumber to set
	 */
	public void setBlockedNumber(Integer blockedNumber) {
		this.blockedNumber = blockedNumber;
	}

	/**
	 * @param blockedNumberAmmount
	 *            the blockedNumberAmmount to set
	 */
	public void setBlockedNumberAmmount(Integer blockedNumberAmmount) {
		this.blockedNumberAmmount = blockedNumberAmmount;
	}

	/**
	 * @param cashBalance
	 *            the cashBalance to set
	 */
	public void setCashBalance(BigDecimal cashBalance) {
		this.cashBalance = cashBalance;
	}

	/**
	 * @param checkBalance
	 *            the checkBalance to set
	 */
	public void setCheckBalance(BigDecimal checkBalance) {
		this.checkBalance = checkBalance;
	}

	/**
	 * @param client
	 *            the client to set
	 */
	public void setClient(Client client) {
		this.client = client;
	}

	/**
	 * @param dateLastMovent
	 *            the dateLastMovent to set
	 */
	public void setDateLastMovent(String dateLastMovent) {
		this.dateLastMovent = dateLastMovent;
	}

	/**
	 * @param deliveryAddress
	 *            the deliveryAddress to set
	 */
	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	/**
	 * @param drawBalance
	 *            the drawBalance to set
	 */
	public void setDrawBalance(BigDecimal drawBalance) {
		this.drawBalance = drawBalance;
	}

	/**
	 * @param embargoedBalance
	 *            the embargoedBalance to set
	 */
	public void setEmbargoedBalance(BigDecimal embargoedBalance) {
		this.embargoedBalance = embargoedBalance;
	}

	/**
	 * @param equityBalance
	 *            the equityBalance to set
	 */
	public void setEquityBalance(BigDecimal equityBalance) {
		this.equityBalance = equityBalance;
	}

	/**
	 * @param idClosed
	 *            the idClosed to set
	 */
	public void setIdClosed(Integer idClosed) {
		this.idClosed = idClosed;
	}

	/**
	 * @param inExchangeBalance
	 *            the inExchangeBalance to set
	 */
	public void setInExchangeBalance(BigDecimal inExchangeBalance) {
		this.inExchangeBalance = inExchangeBalance;
	}

	/**
	 * @param officeAccount
	 *            the officeAccount to set
	 */
	public void setOfficeAccount(Office officeAccount) {
		this.officeAccount = officeAccount;
	}

	/**
	 * @param openingDate
	 *            the openingDate to set
	 */
	public void setOpeningDate(String openingDate) {
		this.openingDate = openingDate;
	}

	/**
	 * @param overdraftBalance
	 *            the overdraftBalance to set
	 */
	public void setOverdraftBalance(BigDecimal overdraftBalance) {
		this.overdraftBalance = overdraftBalance;
	}

	/**
	 * @param processDate
	 *            the processDate to set
	 */
	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}

	/**
	 * @param product
	 *            the product to set
	 */
	public void setProduct(Product product) {
		this.product = product;
	}

	/**
	 * @param productBanking
	 *            the productBanking to set
	 */
	public void setProductBanking(ProductBanking productBanking) {
		this.productBanking = productBanking;
	}

	/**
	 * @param remittancesBalance
	 *            the remittancesBalance to set
	 */
	public void setRemittancesBalance(BigDecimal remittancesBalance) {
		this.remittancesBalance = remittancesBalance;
	}

	/**
	 * @param rotateBalance
	 *            the rotateBalance to set
	 */
	public void setRotateBalance(BigDecimal rotateBalance) {
		this.rotateBalance = rotateBalance;
	}

	/**
	 * @param ssnHost
	 *            the ssnHost to set
	 */
	public void setSsnHost(Integer ssnHost) {
		this.ssnHost = ssnHost;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @param surplusAmmount
	 *            the surplusAmmount to set
	 */
	public void setSurplusAmmount(BigDecimal surplusAmmount) {
		this.surplusAmmount = surplusAmmount;
	}

	/**
	 * @param totalBalance
	 *            the totalBalance to set
	 */
	public void setTotalBalance(BigDecimal totalBalance) {
		this.totalBalance = totalBalance;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getOficial() {
		return oficial;
	}

	public void setOficial(String oficial) {
		this.oficial = oficial;
	}

	@Override
	public String toString() {
		return "BalanceProduct [deliveryAddress=" + deliveryAddress + ", checkBalance=" + checkBalance + ", overdraftBalance=" + overdraftBalance + ", openingDate=" + openingDate + ", inExchangeBalance=" + inExchangeBalance + ", embargoedBalance=" + embargoedBalance + ", product=" + product + ", client=" + client + ", totalBalance=" + totalBalance + ", drawBalance=" + drawBalance + ", rate="
				+ rate + ", expirationDate=" + expirationDate + ", availableBalance=" + availableBalance + ", equityBalance=" + equityBalance + ", rotateBalance=" + rotateBalance + ", balance12H=" + balance12H + ", balance24H=" + balance24H + ", remittancesBalance=" + remittancesBalance + ", blockedAmmount=" + blockedAmmount + ", blockedNumber=" + blockedNumber + ", blockedNumberAmmount="
				+ blockedNumberAmmount + ", processDate=" + processDate + ", state=" + state + ", surplusAmmount=" + surplusAmmount + ", idClosed=" + idClosed + ", cashBalance=" + cashBalance + ", nextPaymentValue=" + nextPaymentValue + ", dateLastMovent=" + dateLastMovent + ", officeAccount=" + officeAccount + ", productBanking=" + productBanking + ", ssnHost=" + ssnHost + ", accountingBalance="
				+ accountingBalance +",oficial="+ oficial + "]";
	}

}
