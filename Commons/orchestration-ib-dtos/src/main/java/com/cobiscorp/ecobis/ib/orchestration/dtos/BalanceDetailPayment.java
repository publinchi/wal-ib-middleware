/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

import java.math.BigDecimal;


/**
 * @author kmeza
 * @since Oct 9, 2014
 * @version 1.0.0
 */
public class BalanceDetailPayment {
	private String aditionalData;
	private Product productNumber;
	private String entityName;
	private String operationType;
	private BigDecimal initialAmount;
	private String monthlyPaymentDay;
	private String status;
	private String lastPaymentDate;
	private String expirationDate;
	private String executive;
	private String initialDate;
	private Integer  arrearsDays;
	private BigDecimal overdueCapital;
	private BigDecimal overdueInterest;
	private BigDecimal overdueArrearsValue;
	private BigDecimal overdueAnotherItems;
	private BigDecimal overdueTotal;
	private String nextPaymentDate;
	private BigDecimal nextPaymentValue;
	private BigDecimal ordinaryInterestRate;
	private BigDecimal arrearsInterestRate;
	private BigDecimal capitalBalance;
	private BigDecimal totalBalance;
	private String  originalTerm;
	private String sector;
	private String operationDescription;
	private   BigDecimal tax;
	private BigDecimal totalAmountCancel;
	
	private BigDecimal capital;
	private BigDecimal interest;
	private BigDecimal moratorium;
	private BigDecimal insurance;
	private BigDecimal other;
	private String desMoney;
	private String nextDueDate;
	
  @Override
public String toString() {
	return "BalanceDetailPayment [aditionalData=" + aditionalData
			+ ", productNumber=" + productNumber + ", entityName=" + entityName
			+ ", operationType=" + operationType + ", initialAmount="
			+ initialAmount + ", monthlyPaymentDay=" + monthlyPaymentDay
			+ ", status=" + status + ", lastPaymentDate=" + lastPaymentDate
			+ ", expirationDate=" + expirationDate + ", executive=" + executive
			+ ", initialDate=" + initialDate + ", arrearsDays=" + arrearsDays
			+ ", overdueCapital=" + overdueCapital + ", overdueInterest="
			+ overdueInterest + ", overdueArrearsValue=" + overdueArrearsValue
			+ ", overdueAnotherItems=" + overdueAnotherItems
			+ ", overdueTotal=" + overdueTotal + ", nextPaymentDate="
			+ nextPaymentDate + ", nextPaymentValue=" + nextPaymentValue
			+ ", ordinaryInterestRate=" + ordinaryInterestRate
			+ ", arrearsInterestRate=" + arrearsInterestRate
			+ ", capitalBalance=" + capitalBalance + ", totalBalance="
			+ totalBalance + ", originalTerm=" + originalTerm + ", sector="
			+ sector + ", operationDescription=" + operationDescription
			+ ", tax=" + tax + ", totalAmountCancel=" + totalAmountCancel
			+ ", capital=" + capital + ", interest=" + interest
			+ ", moratorium=" + moratorium + ", insurance=" + insurance
			+ ", nextDueDate=" + nextDueDate + ", getAditionalData()="
			+ getAditionalData() + ", getProductNumber()=" + getProductNumber()
			+ ", getEntityName()=" + getEntityName() + ", getOperationType()="
			+ getOperationType() + ", getInitialAmount()=" + getInitialAmount()
			+ ", getMonthlyPaymentDay()=" + getMonthlyPaymentDay()
			+ ", getStatus()=" + getStatus() + ", getLastPaymentDate()="
			+ getLastPaymentDate() + ", getExpirationDate()="
			+ getExpirationDate() + ", getExecutive()=" + getExecutive()
			+ ", getInitialDate()=" + getInitialDate() + ", getArrearsDays()="
			+ getArrearsDays() + ", getOverdueCapital()=" + getOverdueCapital()
			+ ", getOverdueInterest()=" + getOverdueInterest()
			+ ", getOverdueArrearsValue()=" + getOverdueArrearsValue()
			+ ", getOverdueAnotherItems()=" + getOverdueAnotherItems()
			+ ", getOverdueTotal()=" + getOverdueTotal()
			+ ", getNextPaymentDate()=" + getNextPaymentDate()
			+ ", getNextPaymentValue()=" + getNextPaymentValue()
			+ ", getOrdinaryInterestRate()=" + getOrdinaryInterestRate()
			+ ", getArrearsInterestRate()=" + getArrearsInterestRate()
			+ ", getCapitalBalance()=" + getCapitalBalance()
			+ ", getTotalBalance()=" + getTotalBalance()
			+ ", getOriginalTerm()=" + getOriginalTerm() + ", getSector()="
			+ getSector() + ", getOperationDescription()="
			+ getOperationDescription() + ", getTax()=" + getTax()
			+ ", getTotalAmountCancel()=" + getTotalAmountCancel()
			+ ", getCapital()=" + getCapital() + ", getInterest()="
			+ getInterest() + ", getMoratorium()=" + getMoratorium()
			+ ", getInsurance()=" + getInsurance() + ", getNextDueDate()="
			+ getNextDueDate() + ", getClass()=" + getClass() + ", hashCode()="
			+ hashCode() + ", toString()=" + super.toString() + "]";
}
	/**
	 * @return the aditionalData
	 */
	public String getAditionalData() {
		return aditionalData;
	}
	/**
	 * @param aditionalData the aditionalData to set
	 */
	public void setAditionalData(String aditionalData) {
		this.aditionalData = aditionalData;
	}
	/**
	 * @return the productNumber
	 */
	public Product getProductNumber() {
		return productNumber;
	}
	/**
	 * @param productNumber the productNumber to set
	 */
	public void setProductNumber(Product productNumber) {
		this.productNumber = productNumber;
	}
	/**
	 * @return the entityName
	 */
	public String getEntityName() {
		return entityName;
	}
	/**
	 * @param entityName the entityName to set
	 */
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	/**
	 * @return the operationType
	 */
	public String getOperationType() {
		return operationType;
	}
	/**
	 * @param operationType the operationType to set
	 */
	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}
	/**
	 * @return the initialAmount
	 */
	public BigDecimal getInitialAmount() {
		return initialAmount;
	}
	/**
	 * @param initialAmount the initialAmount to set
	 */
	public void setInitialAmount(BigDecimal initialAmount) {
		this.initialAmount = initialAmount;
	}
	/**
	 * @return the monthlyPaymentDay
	 */
	public String getMonthlyPaymentDay() {
		return monthlyPaymentDay;
	}
	/**
	 * @param monthlyPaymentDay the monthlyPaymentDay to set
	 */
	public void setMonthlyPaymentDay(String monthlyPaymentDay) {
		this.monthlyPaymentDay = monthlyPaymentDay;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the lastPaymentDate
	 */
	public String getLastPaymentDate() {
		return lastPaymentDate;
	}
	/**
	 * @param lastPaymentDate the lastPaymentDate to set
	 */
	public void setLastPaymentDate(String lastPaymentDate) {
		this.lastPaymentDate = lastPaymentDate;
	}
	/**
	 * @return the expirationDate
	 */
	public String getExpirationDate() {
		return expirationDate;
	}
	/**
	 * @param expirationDate the expirationDate to set
	 */
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	/**
	 * @return the executive
	 */
	public String getExecutive() {
		return executive;
	}
	/**
	 * @param executive the executive to set
	 */
	public void setExecutive(String executive) {
		this.executive = executive;
	}
	/**
	 * @return the initialDate
	 */
	public String getInitialDate() {
		return initialDate;
	}
	/**
	 * @param initialDate the initialDate to set
	 */
	public void setInitialDate(String initialDate) {
		this.initialDate = initialDate;
	}
	/**
	 * @return the arrearsDays
	 */
	public Integer getArrearsDays() {
		return arrearsDays;
	}
	/**
	 * @param arrearsDays the arrearsDays to set
	 */
	public void setArrearsDays(Integer arrearsDays) {
		this.arrearsDays = arrearsDays;
	}
	/**
	 * @return the overdueCapital
	 */
	public BigDecimal getOverdueCapital() {
		return overdueCapital;
	}
	/**
	 * @param overdueCapital the overdueCapital to set
	 */
	public void setOverdueCapital(BigDecimal overdueCapital) {
		this.overdueCapital = overdueCapital;
	}
	/**
	 * @return the overdueInterest
	 */
	public BigDecimal getOverdueInterest() {
		return overdueInterest;
	}
	/**
	 * @param overdueInterest the overdueInterest to set
	 */
	public void setOverdueInterest(BigDecimal overdueInterest) {
		this.overdueInterest = overdueInterest;
	}
	/**
	 * @return the overdueArrearsValue
	 */
	public BigDecimal getOverdueArrearsValue() {
		return overdueArrearsValue;
	}
	/**
	 * @param overdueArrearsValue the overdueArrearsValue to set
	 */
	public void setOverdueArrearsValue(BigDecimal overdueArrearsValue) {
		this.overdueArrearsValue = overdueArrearsValue;
	}
	/**
	 * @return the overdueAnotherItems
	 */
	public BigDecimal getOverdueAnotherItems() {
		return overdueAnotherItems;
	}
	/**
	 * @param overdueAnotherItems the overdueAnotherItems to set
	 */
	public void setOverdueAnotherItems(BigDecimal overdueAnotherItems) {
		this.overdueAnotherItems = overdueAnotherItems;
	}
	/**
	 * @return the overdueTotal
	 */
	public BigDecimal getOverdueTotal() {
		return overdueTotal;
	}
	/**
	 * @param overdueTotal the overdueTotal to set
	 */
	public void setOverdueTotal(BigDecimal overdueTotal) {
		this.overdueTotal = overdueTotal;
	}
	/**
	 * @return the nextPaymentDate
	 */
	public String getNextPaymentDate() {
		return nextPaymentDate;
	}
	/**
	 * @param nextPaymentDate the nextPaymentDate to set
	 */
	public void setNextPaymentDate(String nextPaymentDate) {
		this.nextPaymentDate = nextPaymentDate;
	}
	/**
	 * @return the nextPaymentValue
	 */
	public BigDecimal getNextPaymentValue() {
		return nextPaymentValue;
	}
	/**
	 * @param nextPaymentValue the nextPaymentValue to set
	 */
	public void setNextPaymentValue(BigDecimal nextPaymentValue) {
		this.nextPaymentValue = nextPaymentValue;
	}
	/**
	 * @return the ordinaryInterestRate
	 */
	public BigDecimal getOrdinaryInterestRate() {
		return ordinaryInterestRate;
	}
	/**
	 * @param ordinaryInterestRate the ordinaryInterestRate to set
	 */
	public void setOrdinaryInterestRate(BigDecimal ordinaryInterestRate) {
		this.ordinaryInterestRate = ordinaryInterestRate;
	}
	/**
	 * @return the arrearsInterestRate
	 */
	public BigDecimal getArrearsInterestRate() {
		return arrearsInterestRate;
	}
	/**
	 * @param arrearsInterestRate the arrearsInterestRate to set
	 */
	public void setArrearsInterestRate(BigDecimal arrearsInterestRate) {
		this.arrearsInterestRate = arrearsInterestRate;
	}
	/**
	 * @return the capitalBalance
	 */
	public BigDecimal getCapitalBalance() {
		return capitalBalance;
	}
	/**
	 * @param capitalBalance the capitalBalance to set
	 */
	public void setCapitalBalance(BigDecimal capitalBalance) {
		this.capitalBalance = capitalBalance;
	}
	/**
	 * @return the totalBalance
	 */
	public BigDecimal getTotalBalance() {
		return totalBalance;
	}
	/**
	 * @param totalBalance the totalBalance to set
	 */
	public void setTotalBalance(BigDecimal totalBalance) {
		this.totalBalance = totalBalance;
	}
	/**
	 * @return the originalTerm
	 */
	public String getOriginalTerm() {
		return originalTerm;
	}
	/**
	 * @param originalTerm the originalTerm to set
	 */
	public void setOriginalTerm(String originalTerm) {
		this.originalTerm = originalTerm;
	}
	/**
	 * @return the sector
	 */
	public String getSector() {
		return sector;
	}
	/**
	 * @param sector the sector to set
	 */
	public void setSector(String sector) {
		this.sector = sector;
	}
	/**
	 * @return the operationDescription
	 */
	public String getOperationDescription() {
		return operationDescription;
	}
	/**
	 * @param operationDescription the operationDescription to set
	 */
	public void setOperationDescription(String operationDescription) {
		this.operationDescription = operationDescription;
	}
	/**
	 * @return the tax
	 */
	public BigDecimal getTax() {
		return tax;
	}
	/**
	 * @param tax the tax to set
	 */
	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}
	
	public BigDecimal getTotalAmountCancel() {
		return totalAmountCancel;
	}
	
	public void setTotalAmountCancel(BigDecimal totalAmountCancel) {
		this.totalAmountCancel = totalAmountCancel;
	}
	public BigDecimal getCapital() {
		return capital;
	}
	public void setCapital(BigDecimal capital) {
		this.capital = capital;
	}
	public BigDecimal getInterest() {
		return interest;
	}
	public void setInterest(BigDecimal interest) {
		this.interest = interest;
	}
	public BigDecimal getMoratorium() {
		return moratorium;
	}
	public void setMoratorium(BigDecimal moratorium) {
		this.moratorium = moratorium;
	}
	public BigDecimal getInsurance() {
		return insurance;
	}
	public void setInsurance(BigDecimal insurance) {
		this.insurance = insurance;
	}
	public String getNextDueDate() {
		return nextDueDate;
	}
	public void setNextDueDate(String nextDueDate) {
		this.nextDueDate = nextDueDate;
	}
	public BigDecimal getOther() {
		return other;
	}
	public void setOther(BigDecimal other) {
		this.other = other;
	}
	public String getDesMoney() {
		return desMoney;
	}
	public void setDesMoney(String desMoney) {
		this.desMoney = desMoney;
	} 
	
	
	
}
