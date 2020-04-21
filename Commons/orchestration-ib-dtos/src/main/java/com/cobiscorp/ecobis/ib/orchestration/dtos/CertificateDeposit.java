/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author jveloz
 * @since Oct 28, 2014
 * @version 1.0.0
 */
public class CertificateDeposit {

	private Integer term;//@i_plazo
	private String regType;//@i_tipo_reg
	private String category;//@i_categoria
	private Integer money;//@i_moneda
	private Double amount;//@i_monto
	private String nemonic;//@i_nemonico
	private Rate rate;//@i_tasa
	private String processDate;//@i_fecha_valor
	private String capitalize;//@i_capitaliza
	private Integer payDay;//@i_dia_pago
	private String methodOfPayment;//@i_forma_pago
	private String mail;//@i_mail
	private String office;//@i_oficina
	private String periodicityId;//@i_periodicidad
	private String expiration ;//@i_fecha_ven
	private String termDate;//@i_fecha_plazo
	private String type;//
	private String taxRetention;
	private String graceDaysNum;
	private String graceDays;
	private String extendedAut;
	private String calculationBase;
	private String daysBack;
	private String code;
	private String initialIssuance;
	private String maintenanceStock;
	private String stock;
	private String numberExtensions;
	private String comercialDate;
	private String trnDayNoWork;
	private String payCommission;
	private String handlesCoupon;
	private String increasesDecreases;
	private String accountingArea;
	private String typePerson;
	private String calendarDays;
	
	
	/**
	 * @return the term
	 */
	public Integer getTerm() {
		return term;
	}
	/**
	 * @param term the term to set
	 */
	public void setTerm(Integer term) {
		this.term = term;
	}
	/**
	 * @return the regType
	 */
	public String getRegType() {
		return regType;
	}
	/**
	 * @param regType the regType to set
	 */
	public void setRegType(String regType) {
		this.regType = regType;
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
	 * @return the money
	 */
	public Integer getMoney() {
		return money;
	}
	/**
	 * @param money the money to set
	 */
	public void setMoney(Integer money) {
		this.money = money;
	}
	/**
	 * @return the amount
	 */
	public Double getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	/**
	 * @return the nemonic
	 */
	public String getNemonic() {
		return nemonic;
	}
	/**
	 * @param nemonic the nemonic to set
	 */
	public void setNemonic(String nemonic) {
		this.nemonic = nemonic;
	}
	/**
	 * @return the rate
	 */
	public Rate getRate() {
		return rate;
	}
	/**
	 * @param rate the rate to set
	 */
	public void setRate(Rate rate) {
		this.rate = rate;
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
	/**
	 * @return the capitalize
	 */
	public String getCapitalize() {
		return capitalize;
	}
	/**
	 * @param capitalize the capitalize to set
	 */
	public void setCapitalize(String capitalize) {
		this.capitalize = capitalize;
	}
	/**
	 * @return the payDay
	 */
	public Integer getPayDay() {
		return payDay;
	}
	/**
	 * @param payDay the payDay to set
	 */
	public void setPayDay(Integer payDay) {
		this.payDay = payDay;
	}
	/**
	 * @return the methodOfPayment
	 */
	public String getMethodOfPayment() {
		return methodOfPayment;
	}
	/**
	 * @param methodOfPayment the methodOfPayment to set
	 */
	public void setMethodOfPayment(String methodOfPayment) {
		this.methodOfPayment = methodOfPayment;
	}
	/**
	 * @return the mail
	 */
	public String getMail() {
		return mail;
	}
	/**
	 * @param mail the mail to set
	 */
	public void setMail(String mail) {
		this.mail = mail;
	}
	
	/**
	 * @return the office
	 */
	public String getOffice() {
		return office;
	}
	/**
	 * @param office the office to set
	 */
	public void setOffice(String office) {
		this.office = office;
	}
	/**
	 * @return the periodicityId
	 */
	public String getPeriodicityId() {
		return periodicityId;
	}
	/**
	 * @param periodicityId the periodicityId to set
	 */
	public void setPeriodicityId(String periodicityId) {
		this.periodicityId = periodicityId;
	}
	/**
	 * @return the expiration
	 */
	public String getExpiration() {
		return expiration;
	}
	/**
	 * @param expiration the expiration to set
	 */
	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}
	/**
	 * @return the termDate
	 */
	public String getTermDate() {
		return termDate;
	}
	/**
	 * @param termDate the termDate to set
	 */
	public void setTermDate(String termDate) {
		this.termDate = termDate;
	}
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
	 * @return the taxRetention
	 */
	public String getTaxRetention() {
		return taxRetention;
	}
	/**
	 * @param taxRetention the taxRetention to set
	 */
	public void setTaxRetention(String taxRetention) {
		this.taxRetention = taxRetention;
	}
	/**
	 * @return the graceDaysNum
	 */
	public String getGraceDaysNum() {
		return graceDaysNum;
	}
	/**
	 * @param graceDaysNum the graceDaysNum to set
	 */
	public void setGraceDaysNum(String graceDaysNum) {
		this.graceDaysNum = graceDaysNum;
	}
	/**
	 * @return the graceDays
	 */
	public String getGraceDays() {
		return graceDays;
	}
	/**
	 * @param graceDays the graceDays to set
	 */
	public void setGraceDays(String graceDays) {
		this.graceDays = graceDays;
	}
	/**
	 * @return the extendedAut
	 */
	public String getExtendedAut() {
		return extendedAut;
	}
	/**
	 * @param extendedAut the extendedAut to set
	 */
	public void setExtendedAut(String extendedAut) {
		this.extendedAut = extendedAut;
	}
	/**
	 * @return the calculationBase
	 */
	public String getCalculationBase() {
		return calculationBase;
	}
	/**
	 * @param calculationBase the calculationBase to set
	 */
	public void setCalculationBase(String calculationBase) {
		this.calculationBase = calculationBase;
	}
	/**
	 * @return the daysBack
	 */
	public String getDaysBack() {
		return daysBack;
	}
	/**
	 * @param daysBack the daysBack to set
	 */
	public void setDaysBack(String daysBack) {
		this.daysBack = daysBack;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the initialIssuance
	 */
	public String getInitialIssuance() {
		return initialIssuance;
	}
	/**
	 * @param initialIssuance the initialIssuance to set
	 */
	public void setInitialIssuance(String initialIssuance) {
		this.initialIssuance = initialIssuance;
	}
	/**
	 * @return the maintenanceStock
	 */
	public String getMaintenanceStock() {
		return maintenanceStock;
	}
	/**
	 * @param maintenanceStock the maintenanceStock to set
	 */
	public void setMaintenanceStock(String maintenanceStock) {
		this.maintenanceStock = maintenanceStock;
	}
	/**
	 * @return the stock
	 */
	public String getStock() {
		return stock;
	}
	/**
	 * @param stock the stock to set
	 */
	public void setStock(String stock) {
		this.stock = stock;
	}
	/**
	 * @return the numberExtensions
	 */
	public String getNumberExtensions() {
		return numberExtensions;
	}
	/**
	 * @param numberExtensions the numberExtensions to set
	 */
	public void setNumberExtensions(String numberExtensions) {
		this.numberExtensions = numberExtensions;
	}
	/**
	 * @return the comercialDate
	 */
	public String getComercialDate() {
		return comercialDate;
	}
	/**
	 * @param comercialDate the comercialDate to set
	 */
	public void setComercialDate(String comercialDate) {
		this.comercialDate = comercialDate;
	}
	/**
	 * @return the trnDayNoWork
	 */
	public String getTrnDayNoWork() {
		return trnDayNoWork;
	}
	/**
	 * @param trnDayNoWork the trnDayNoWork to set
	 */
	public void setTrnDayNoWork(String trnDayNoWork) {
		this.trnDayNoWork = trnDayNoWork;
	}
	/**
	 * @return the payCommission
	 */
	public String getPayCommission() {
		return payCommission;
	}
	/**
	 * @param payCommission the payCommission to set
	 */
	public void setPayCommission(String payCommission) {
		this.payCommission = payCommission;
	}
	/**
	 * @return the handlesCoupon
	 */
	public String getHandlesCoupon() {
		return handlesCoupon;
	}
	/**
	 * @param handlesCoupon the handlesCoupon to set
	 */
	public void setHandlesCoupon(String handlesCoupon) {
		this.handlesCoupon = handlesCoupon;
	}
	/**
	 * @return the increasesDecreases
	 */
	public String getIncreasesDecreases() {
		return increasesDecreases;
	}
	/**
	 * @param increasesDecreases the increasesDecreases to set
	 */
	public void setIncreasesDecreases(String increasesDecreases) {
		this.increasesDecreases = increasesDecreases;
	}
	/**
	 * @return the accountingArea
	 */
	public String getAccountingArea() {
		return accountingArea;
	}
	/**
	 * @param accountingArea the accountingArea to set
	 */
	public void setAccountingArea(String accountingArea) {
		this.accountingArea = accountingArea;
	}
	/**
	 * @return the typePerson
	 */
	public String getTypePerson() {
		return typePerson;
	}
	/**
	 * @param typePerson the typePerson to set
	 */
	public void setTypePerson(String typePerson) {
		this.typePerson = typePerson;
	}
	/**
	 * @return the calendarDays
	 */
	public String getCalendarDays() {
		return calendarDays;
	}
	/**
	 * @param calendarDays the calendarDays to set
	 */
	public void setCalendarDays(String calendarDays) {
		this.calendarDays = calendarDays;
	}
	
		
}
