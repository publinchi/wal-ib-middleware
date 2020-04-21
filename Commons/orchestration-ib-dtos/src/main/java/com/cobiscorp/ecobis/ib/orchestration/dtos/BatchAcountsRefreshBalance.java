/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

import java.math.BigDecimal;

/**
 * @author jbaque
 * @since 18/2/2015
 * @version 1.0.0
 */
public class BatchAcountsRefreshBalance {
	private int secuencial; //RowId
	private int subsidiary; //filial
	private Integer office; //oficina
	private Integer currency; //moneda
	private String bankAccount; //cuenta banco
	private String name; //nombre
	private Integer customer; //cliente
	private String id_ruc; //cedula ruc
	private String state; //estado
	private String category; //categoria
	private String averageRate; //tipo promedio
	private String capitalization; //capitalizacion
	private String interest; //interes
	private Integer prodBank; //pro banc
	private Integer official; //oficial
	private String openingDate; //fecha apertura
	private String lastDateMovement; //fecha ultimo movimiento
	private BigDecimal available; //disponible
	private BigDecimal C12h; //12h
	private BigDecimal C24h; //24h
	private BigDecimal C48h; //48h
	private BigDecimal remittances; //remesas
	private BigDecimal remittancesToday; //remesas hoy
	private BigDecimal rotatingBalance; //saldo girar
	private BigDecimal balanceYesterday; //saldo ayer
	private BigDecimal interestBalance; //saldo interes
	private BigDecimal blockedAmount; //monto bloqueado
	private BigDecimal creditsToday; //creditos hoy
	private BigDecimal creditsMonth; //creditos mes
	private BigDecimal debitsToday; //debitos hoy
	private BigDecimal debitsMonth; //debitos mes
	private BigDecimal averageAvailable; //promedio disponible
	private BigDecimal average1; //promedio 1
	private BigDecimal average2; //promedio 2
	private BigDecimal average3; //promedio 3
	private BigDecimal average4; //promedio 4
	private BigDecimal average5; //promedio 5
	private BigDecimal average6; //promedio 6
	private Integer trxControl; //contrador trx
	private Integer initialDeposit; //deposito inicial
	private Integer locks; //bloqueos
	private Integer numLockAmount; //num bloqueo monto
	private Integer online; //linea
	private Integer numLib; //num lib
	private Integer protests; //protestos
	private Integer justifiedProtests; //protestos justificados
	private Integer protestsAntPeriod; //protestos periodo ant
	private Integer canceled; //anulados
	private Integer revoked; //revocados
	private Integer checkbooks; //chequeras
	private Integer initialCheck; //cheque inicial
	private Integer overdrafts; //sobregiros
	private String officialAcount; //funcionario de cuenta
	private String executionState; //estado ejecucion
	private BigDecimal amountSob; //monto sob
	private BigDecimal availableTo; //disponible a
	private BigDecimal C12hTo; //12h a
	private BigDecimal C24hTo; //24h a
	private BigDecimal remittancesTo; //remesas a
	private Integer overdraftsTo; //sobregiros a
	private BigDecimal blockedAmountTo; //monto bloqueado a
	private String lastDateHost; //fecha ultimo host
	private String dateCuttingProcess; //fecha proceso corte
	private String ownership; //titularidad
	private String uploadDate; //fecha de carga
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BatchAcountsRefreshBalance [subsidiary=" + subsidiary
				+ ", office=" + office + ", currency=" + currency
				+ ", bankAccount=" + bankAccount + ", name=" + name
				+ ", customer=" + customer + ", id_ruc=" + id_ruc + ", state="
				+ state + ", category=" + category + ", average_rate="
				+ averageRate + ", capitalization=" + capitalization
				+ ", interest=" + interest + ", prodBank=" + prodBank
				+ ", official=" + official + ", openingDate=" + openingDate
				+ ", lastDateMovement=" + lastDateMovement + ", available="
				+ available + ", C12h=" + C12h + ", C24h=" + C24h + ", C48h="
				+ C48h + ", remittances=" + remittances + ", remittancesToday="
				+ remittancesToday + ", rotatingBalance=" + rotatingBalance
				+ ", balanceYesterday=" + balanceYesterday
				+ ", interestBalance=" + interestBalance + ", blockedAmount="
				+ blockedAmount + ", creditsToday=" + creditsToday
				+ ", creditsMonth=" + creditsMonth + ", debitsToday="
				+ debitsToday + ", debitsMonth=" + debitsMonth
				+ ", averageAvailable=" + averageAvailable + ", average1="
				+ average1 + ", average2=" + average2 + ", average3="
				+ average3 + ", average4=" + average4 + ", average5="
				+ average5 + ", average6=" + average6 + ", trxControl="
				+ trxControl + ", initialDeposit=" + initialDeposit
				+ ", locks=" + locks + ", numLockAmount=" + numLockAmount
				+ ", online=" + online + ", numLib=" + numLib + ", protests="
				+ protests + ", justifiedProtests=" + justifiedProtests
				+ ", protestsAntPeriod=" + protestsAntPeriod + ", canceled="
				+ canceled + ", revoked=" + revoked + ", checkbooks="
				+ checkbooks + ", initialCheck=" + initialCheck
				+ ", overdrafts=" + overdrafts + ", officialAcount="
				+ officialAcount + ", executionState=" + executionState
				+ ", amountSob=" + amountSob + ", availableTo=" + availableTo
				+ ", C12hTo=" + C12hTo + ", C24hTo=" + C24hTo
				+ ", remittancesTo=" + remittancesTo + ", overdraftsTo="
				+ overdraftsTo + ", blockedAmountTo=" + blockedAmountTo
				+ ", lastDateHost=" + lastDateHost + ", dateCuttingProcess="
				+ dateCuttingProcess + ", ownership=" + ownership
				+ ", uploadDate=" + uploadDate + "]";
	}
	/**
	 * @return the subsidiary
	 */
	public int getSubsidiary() {
		return subsidiary;
	}
	/**
	 * @param subsidiary the subsidiary to set
	 */
	public void setSubsidiary(int subsidiary) {
		this.subsidiary = subsidiary;
	}
	/**
	 * @return the office
	 */
	public Integer getOffice() {
		return office;
	}
	/**
	 * @param office the office to set
	 */
	public void setOffice(Integer office) {
		this.office = office;
	}
	/**
	 * @return the currency
	 */
	public Integer getCurrency() {
		return currency;
	}
	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(Integer currency) {
		this.currency = currency;
	}
	/**
	 * @return the bankAccount
	 */
	public String getBankAccount() {
		return bankAccount;
	}
	/**
	 * @param bankAccount the bankAccount to set
	 */
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return the id_ruc
	 */
	public String getId_ruc() {
		return id_ruc;
	}
	/**
	 * @param id_ruc the id_ruc to set
	 */
	public void setId_ruc(String id_ruc) {
		this.id_ruc = id_ruc;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/**
	 * @return the average_rate
	 */
	public String getAverageRate() {
		return averageRate;
	}
	/**
	 * @param average_rate the average_rate to set
	 */
	public void setAverageRate(String average_rate) {
		this.averageRate = average_rate;
	}
	/**
	 * @return the capitalization
	 */
	public String getCapitalization() {
		return capitalization;
	}
	/**
	 * @param capitalization the capitalization to set
	 */
	public void setCapitalization(String capitalization) {
		this.capitalization = capitalization;
	}
	/**
	 * @return the interest
	 */
	public String getInterest() {
		return interest;
	}
	/**
	 * @param interest the interest to set
	 */
	public void setInterest(String interest) {
		this.interest = interest;
	}
	/**
	 * @return the prodBank
	 */
	public Integer getProdBank() {
		return prodBank;
	}
	/**
	 * @param prodBank the prodBank to set
	 */
	public void setProdBank(Integer prodBank) {
		this.prodBank = prodBank;
	}
	/**
	 * @return the official
	 */
	public Integer getOfficial() {
		return official;
	}
	/**
	 * @param official the official to set
	 */
	public void setOfficial(Integer official) {
		this.official = official;
	}
	/**
	 * @return the openingDate
	 */
	public String getOpeningDate() {
		return openingDate;
	}
	/**
	 * @param openingDate the openingDate to set
	 */
	public void setOpeningDate(String openingDate) {
		this.openingDate = openingDate;
	}
	/**
	 * @return the lastDateMovement
	 */
	public String getLastDateMovement() {
		return lastDateMovement;
	}
	/**
	 * @param lastDateMovement the lastDateMovement to set
	 */
	public void setLastDateMovement(String lastDateMovement) {
		this.lastDateMovement = lastDateMovement;
	}
	/**
	 * @return the available
	 */
	public BigDecimal getAvailable() {
		return available;
	}
	/**
	 * @param available the available to set
	 */
	public void setAvailable(BigDecimal available) {
		this.available = available;
	}
	/**
	 * @return the c12h
	 */
	public BigDecimal getC12h() {
		return C12h;
	}
	/**
	 * @param c12h the c12h to set
	 */
	public void setC12h(BigDecimal c12h) {
		C12h = c12h;
	}
	/**
	 * @return the c24h
	 */
	public BigDecimal getC24h() {
		return C24h;
	}
	/**
	 * @param c24h the c24h to set
	 */
	public void setC24h(BigDecimal c24h) {
		C24h = c24h;
	}
	/**
	 * @return the c48h
	 */
	public BigDecimal getC48h() {
		return C48h;
	}
	/**
	 * @param c48h the c48h to set
	 */
	public void setC48h(BigDecimal c48h) {
		C48h = c48h;
	}
	/**
	 * @return the remittances
	 */
	public BigDecimal getRemittances() {
		return remittances;
	}
	/**
	 * @param remittances the remittances to set
	 */
	public void setRemittances(BigDecimal remittances) {
		this.remittances = remittances;
	}
	/**
	 * @return the remittancesToday
	 */
	public BigDecimal getRemittancesToday() {
		return remittancesToday;
	}
	/**
	 * @param remittancesToday the remittancesToday to set
	 */
	public void setRemittancesToday(BigDecimal remittancesToday) {
		this.remittancesToday = remittancesToday;
	}
	/**
	 * @return the rotatingBalance
	 */
	public BigDecimal getRotatingBalance() {
		return rotatingBalance;
	}
	/**
	 * @param rotatingBalance the rotatingBalance to set
	 */
	public void setRotatingBalance(BigDecimal rotatingBalance) {
		this.rotatingBalance = rotatingBalance;
	}
	/**
	 * @return the balanceYesterday
	 */
	public BigDecimal getBalanceYesterday() {
		return balanceYesterday;
	}
	/**
	 * @param balanceYesterday the balanceYesterday to set
	 */
	public void setBalanceYesterday(BigDecimal balanceYesterday) {
		this.balanceYesterday = balanceYesterday;
	}
	/**
	 * @return the interestBalance
	 */
	public BigDecimal getInterestBalance() {
		return interestBalance;
	}
	/**
	 * @param interestBalance the interestBalance to set
	 */
	public void setInterestBalance(BigDecimal interestBalance) {
		this.interestBalance = interestBalance;
	}
	/**
	 * @return the blockedAmount
	 */
	public BigDecimal getBlockedAmount() {
		return blockedAmount;
	}
	/**
	 * @param blockedAmount the blockedAmount to set
	 */
	public void setBlockedAmount(BigDecimal blockedAmount) {
		this.blockedAmount = blockedAmount;
	}
	/**
	 * @return the creditsToday
	 */
	public BigDecimal getCreditsToday() {
		return creditsToday;
	}
	/**
	 * @param creditsToday the creditsToday to set
	 */
	public void setCreditsToday(BigDecimal creditsToday) {
		this.creditsToday = creditsToday;
	}
	/**
	 * @return the creditsMonth
	 */
	public BigDecimal getCreditsMonth() {
		return creditsMonth;
	}
	/**
	 * @param creditsMonth the creditsMonth to set
	 */
	public void setCreditsMonth(BigDecimal creditsMonth) {
		this.creditsMonth = creditsMonth;
	}
	/**
	 * @return the debitsToday
	 */
	public BigDecimal getDebitsToday() {
		return debitsToday;
	}
	/**
	 * @param debitsToday the debitsToday to set
	 */
	public void setDebitsToday(BigDecimal debitsToday) {
		this.debitsToday = debitsToday;
	}
	/**
	 * @return the debitsMonth
	 */
	public BigDecimal getDebitsMonth() {
		return debitsMonth;
	}
	/**
	 * @param debitsMonth the debitsMonth to set
	 */
	public void setDebitsMonth(BigDecimal debitsMonth) {
		this.debitsMonth = debitsMonth;
	}
	/**
	 * @return the averageAvailable
	 */
	public BigDecimal getAverageAvailable() {
		return averageAvailable;
	}
	/**
	 * @param averageAvailable the averageAvailable to set
	 */
	public void setAverageAvailable(BigDecimal averageAvailable) {
		this.averageAvailable = averageAvailable;
	}
	/**
	 * @return the average1
	 */
	public BigDecimal getAverage1() {
		return average1;
	}
	/**
	 * @param average1 the average1 to set
	 */
	public void setAverage1(BigDecimal average1) {
		this.average1 = average1;
	}
	/**
	 * @return the average2
	 */
	public BigDecimal getAverage2() {
		return average2;
	}
	/**
	 * @param average2 the average2 to set
	 */
	public void setAverage2(BigDecimal average2) {
		this.average2 = average2;
	}
	/**
	 * @return the average3
	 */
	public BigDecimal getAverage3() {
		return average3;
	}
	/**
	 * @param average3 the average3 to set
	 */
	public void setAverage3(BigDecimal average3) {
		this.average3 = average3;
	}
	/**
	 * @return the average4
	 */
	public BigDecimal getAverage4() {
		return average4;
	}
	/**
	 * @param average4 the average4 to set
	 */
	public void setAverage4(BigDecimal average4) {
		this.average4 = average4;
	}
	/**
	 * @return the average5
	 */
	public BigDecimal getAverage5() {
		return average5;
	}
	/**
	 * @param average5 the average5 to set
	 */
	public void setAverage5(BigDecimal average5) {
		this.average5 = average5;
	}
	/**
	 * @return the average6
	 */
	public BigDecimal getAverage6() {
		return average6;
	}
	/**
	 * @param average6 the average6 to set
	 */
	public void setAverage6(BigDecimal average6) {
		this.average6 = average6;
	}
	/**
	 * @return the trxControl
	 */
	public Integer getTrxControl() {
		return trxControl;
	}
	/**
	 * @param trxControl the trxControl to set
	 */
	public void setTrxControl(Integer trxControl) {
		this.trxControl = trxControl;
	}
	/**
	 * @return the initialDeposit
	 */
	public Integer getInitialDeposit() {
		return initialDeposit;
	}
	/**
	 * @param initialDeposit the initialDeposit to set
	 */
	public void setInitialDeposit(Integer initialDeposit) {
		this.initialDeposit = initialDeposit;
	}
	/**
	 * @return the locks
	 */
	public Integer getLocks() {
		return locks;
	}
	/**
	 * @param locks the locks to set
	 */
	public void setLocks(Integer locks) {
		this.locks = locks;
	}
	/**
	 * @return the numLockAmount
	 */
	public Integer getNumLockAmount() {
		return numLockAmount;
	}
	/**
	 * @param numLockAmount the numLockAmount to set
	 */
	public void setNumLockAmount(Integer numLockAmount) {
		this.numLockAmount = numLockAmount;
	}
	/**
	 * @return the online
	 */
	public Integer getOnline() {
		return online;
	}
	/**
	 * @param online the online to set
	 */
	public void setOnline(Integer online) {
		this.online = online;
	}
	/**
	 * @return the numLib
	 */
	public Integer getNumLib() {
		return numLib;
	}
	/**
	 * @param numLib the numLib to set
	 */
	public void setNumLib(Integer numLib) {
		this.numLib = numLib;
	}
	/**
	 * @return the protests
	 */
	public Integer getProtests() {
		return protests;
	}
	/**
	 * @param protests the protests to set
	 */
	public void setProtests(Integer protests) {
		this.protests = protests;
	}
	/**
	 * @return the justifiedProtests
	 */
	public Integer getJustifiedProtests() {
		return justifiedProtests;
	}
	/**
	 * @param justifiedProtests the justifiedProtests to set
	 */
	public void setJustifiedProtests(Integer justifiedProtests) {
		this.justifiedProtests = justifiedProtests;
	}
	/**
	 * @return the protestsAntPeriod
	 */
	public Integer getProtestsAntPeriod() {
		return protestsAntPeriod;
	}
	/**
	 * @param protestsAntPeriod the protestsAntPeriod to set
	 */
	public void setProtestsAntPeriod(Integer protestsAntPeriod) {
		this.protestsAntPeriod = protestsAntPeriod;
	}
	/**
	 * @return the canceled
	 */
	public Integer getCanceled() {
		return canceled;
	}
	/**
	 * @param canceled the canceled to set
	 */
	public void setCanceled(Integer canceled) {
		this.canceled = canceled;
	}
	/**
	 * @return the revoked
	 */
	public Integer getRevoked() {
		return revoked;
	}
	/**
	 * @param revoked the revoked to set
	 */
	public void setRevoked(Integer revoked) {
		this.revoked = revoked;
	}
	/**
	 * @return the checkbooks
	 */
	public Integer getCheckbooks() {
		return checkbooks;
	}
	/**
	 * @param checkbooks the checkbooks to set
	 */
	public void setCheckbooks(Integer checkbooks) {
		this.checkbooks = checkbooks;
	}
	/**
	 * @return the initialCheck
	 */
	public Integer getInitialCheck() {
		return initialCheck;
	}
	/**
	 * @param initialCheck the initialCheck to set
	 */
	public void setInitialCheck(Integer initialCheck) {
		this.initialCheck = initialCheck;
	}
	/**
	 * @return the overdrafts
	 */
	public Integer getOverdrafts() {
		return overdrafts;
	}
	/**
	 * @param overdrafts the overdrafts to set
	 */
	public void setOverdrafts(Integer overdrafts) {
		this.overdrafts = overdrafts;
	}
	/**
	 * @return the officialAcount
	 */
	public String getOfficialAcount() {
		return officialAcount;
	}
	/**
	 * @param officialAcount the officialAcount to set
	 */
	public void setOfficialAcount(String officialAcount) {
		this.officialAcount = officialAcount;
	}
	/**
	 * @return the executionState
	 */
	public String getExecutionState() {
		return executionState;
	}
	/**
	 * @param executionState the executionState to set
	 */
	public void setExecutionState(String executionState) {
		this.executionState = executionState;
	}
	/**
	 * @return the amountSob
	 */
	public BigDecimal getAmountSob() {
		return amountSob;
	}
	/**
	 * @param amountSob the amountSob to set
	 */
	public void setAmountSob(BigDecimal amountSob) {
		this.amountSob = amountSob;
	}
	/**
	 * @return the availableTo
	 */
	public BigDecimal getAvailableTo() {
		return availableTo;
	}
	/**
	 * @param availableTo the availableTo to set
	 */
	public void setAvailableTo(BigDecimal availableTo) {
		this.availableTo = availableTo;
	}
	/**
	 * @return the c12hTo
	 */
	public BigDecimal getC12hTo() {
		return C12hTo;
	}
	/**
	 * @param c12hTo the c12hTo to set
	 */
	public void setC12hTo(BigDecimal c12hTo) {
		C12hTo = c12hTo;
	}
	/**
	 * @return the c24hTo
	 */
	public BigDecimal getC24hTo() {
		return C24hTo;
	}
	/**
	 * @param c24hTo the c24hTo to set
	 */
	public void setC24hTo(BigDecimal c24hTo) {
		C24hTo = c24hTo;
	}
	/**
	 * @return the remittancesTo
	 */
	public BigDecimal getRemittancesTo() {
		return remittancesTo;
	}
	/**
	 * @param remittancesTo the remittancesTo to set
	 */
	public void setRemittancesTo(BigDecimal remittancesTo) {
		this.remittancesTo = remittancesTo;
	}
	/**
	 * @return the overdraftsTo
	 */
	public Integer getOverdraftsTo() {
		return overdraftsTo;
	}
	/**
	 * @param overdraftsTo the overdraftsTo to set
	 */
	public void setOverdraftsTo(Integer overdraftsTo) {
		this.overdraftsTo = overdraftsTo;
	}
	/**
	 * @return the blockedAmountTo
	 */
	public BigDecimal getBlockedAmountTo() {
		return blockedAmountTo;
	}
	/**
	 * @param blockedAmountTo the blockedAmountTo to set
	 */
	public void setBlockedAmountTo(BigDecimal blockedAmountTo) {
		this.blockedAmountTo = blockedAmountTo;
	}
	/**
	 * @return the lastDateHost
	 */
	public String getLastDateHost() {
		return lastDateHost;
	}
	/**
	 * @param lastDateHost the lastDateHost to set
	 */
	public void setLastDateHost(String lastDateHost) {
		this.lastDateHost = lastDateHost;
	}
	/**
	 * @return the dateCuttingProcess
	 */
	public String getDateCuttingProcess() {
		return dateCuttingProcess;
	}
	/**
	 * @param dateCuttingProcess the dateCuttingProcess to set
	 */
	public void setDateCuttingProcess(String dateCuttingProcess) {
		this.dateCuttingProcess = dateCuttingProcess;
	}
	/**
	 * @return the ownership
	 */
	public String getOwnership() {
		return ownership;
	}
	/**
	 * @param ownership the ownership to set
	 */
	public void setOwnership(String ownership) {
		this.ownership = ownership;
	}
	/**
	 * @return the uploadDate
	 */
	public String getUploadDate() {
		return uploadDate;
	}
	/**
	 * @param uploadDate the uploadDate to set
	 */
	public void setUploadDate(String uploadDate) {
		this.uploadDate = uploadDate;
	}
	/**
	 * @return the secuencial
	 */
	public int getSecuencial() {
		return secuencial;
	}
	/**
	 * @param secuencial the secuencial to set
	 */
	public void setSecuencial(int secuencial) {
		this.secuencial = secuencial;
	}

}
