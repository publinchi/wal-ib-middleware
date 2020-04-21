
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * Contains atributes for the Card Inquerie.
 * @author jlvidal
 * @since Oct 13, 2014
 * @version 1.0.0
 */
public class CreditCardBalance {
	/**
	 * Local Balance
	 */
	private Double localBalance;
	/**
	 * Minimun amount for the payment
	 */
	private Double localMinimutmPayment;
	/**
	 * cash Payment Local Currency
	 */
	private Double cashPaymentLocalCurrency; 
	/**
	 * available Local
	 */
	private Double availableLocal;
	/**
	 * international Balance
	 */
	private Double internationalBalance;
	/**
	 * international Minimum Payment
	 */
	private Double internationalMinimumPayment;
	/**
	 * cash Payment International Currency
	 */
	private Double cashPaymentInternationalCurrency;
	/**
	 * available International
	 */
	private Double availableInternational;
	/**
	 * payment Date
	 */
	private String paymentDate;
	/**
	 * available Local EF
	 */
	private Double availableLocalEF;
	/**
	 * available International EF
	 */
	private Double availableInternationalEF;
	/**
	 * debit Local Transit
	 */
	private Double debitLocalTransit;
	/**
	 * debit International Transit;
	 */
	private Double debitInternationalTransit;
	/**
	 * response Code
	 */
	private String responseCode;
	
	private String fechavencimiento;
	
	private String fechacorte;
	
	private String descripcion;
	
	private Double pagominimolocal;
	
	private Double pagocontadolocal;
	
	private Integer error;
	
	private Double pagominimoint;
	
	private Double pagocontadoint;
	
	private Double saldolocal;
	
	private Double saldointernacional;
	
	private Double saltotcortelocal;
	
	private Double saltotcorteinter;
	
	private Double disponibleeflocal;
	
	private Double disponibleefinter;
	
	private Double debitotransitolocal;
	
	private Double debitotransitointer;
	
	
	
	
	/**
	 * @return the fechavencimiento
	 */
	public String getFechavencimiento() {
		return fechavencimiento;
	}
	/**
	 * @param fechavencimiento the fechavencimiento to set
	 */
	public void setFechavencimiento(String fechavencimiento) {
		this.fechavencimiento = fechavencimiento;
	}
	/**
	 * @return the fechacorte
	 */
	public String getFechacorte() {
		return fechacorte;
	}
	/**
	 * @param fechacorte the fechacorte to set
	 */
	public void setFechacorte(String fechacorte) {
		this.fechacorte = fechacorte;
	}
	/**
	 * @return the descripcion
	 */
	public String getDescripcion() {
		return descripcion;
	}
	/**
	 * @param descripcion the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	/**
	 * @return the pagominimolocal
	 */
	public Double getPagominimolocal() {
		return pagominimolocal;
	}
	/**
	 * @param pagominimolocal the pagominimolocal to set
	 */
	public void setPagominimolocal(Double pagominimolocal) {
		this.pagominimolocal = pagominimolocal;
	}
	/**
	 * @return the pagocontadolocal
	 */
	public Double getPagocontadolocal() {
		return pagocontadolocal;
	}
	/**
	 * @param pagocontadolocal the pagocontadolocal to set
	 */
	public void setPagocontadolocal(Double pagocontadolocal) {
		this.pagocontadolocal = pagocontadolocal;
	}
	/**
	 * @return the error
	 */
	public Integer getError() {
		return error;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(Integer error) {
		this.error = error;
	}
	/**
	 * @return the pagominimoint
	 */
	public Double getPagominimoint() {
		return pagominimoint;
	}
	/**
	 * @param pagominimoint the pagominimoint to set
	 */
	public void setPagominimoint(Double pagominimoint) {
		this.pagominimoint = pagominimoint;
	}
	/**
	 * @return the pagocontadoint
	 */
	public Double getPagocontadoint() {
		return pagocontadoint;
	}
	/**
	 * @param pagocontadoint the pagocontadoint to set
	 */
	public void setPagocontadoint(Double pagocontadoint) {
		this.pagocontadoint = pagocontadoint;
	}
	/**
	 * @return the saldolocal
	 */
	public Double getSaldolocal() {
		return saldolocal;
	}
	/**
	 * @param saldolocal the saldolocal to set
	 */
	public void setSaldolocal(Double saldolocal) {
		this.saldolocal = saldolocal;
	}
	/**
	 * @return the saldointernacional
	 */
	public Double getSaldointernacional() {
		return saldointernacional;
	}
	/**
	 * @param saldointernacional the saldointernacional to set
	 */
	public void setSaldointernacional(Double saldointernacional) {
		this.saldointernacional = saldointernacional;
	}
	/**
	 * @return the saltotcortelocal
	 */
	public Double getSaltotcortelocal() {
		return saltotcortelocal;
	}
	/**
	 * @param saltotcortelocal the saltotcortelocal to set
	 */
	public void setSaltotcortelocal(Double saltotcortelocal) {
		this.saltotcortelocal = saltotcortelocal;
	}
	/**
	 * @return the saltotcorteinter
	 */
	public Double getSaltotcorteinter() {
		return saltotcorteinter;
	}
	/**
	 * @param saltotcorteinter the saltotcorteinter to set
	 */
	public void setSaltotcorteinter(Double saltotcorteinter) {
		this.saltotcorteinter = saltotcorteinter;
	}
	/**
	 * @return the disponibleeflocal
	 */
	public Double getDisponibleeflocal() {
		return disponibleeflocal;
	}
	/**
	 * @param disponibleeflocal the disponibleeflocal to set
	 */
	public void setDisponibleeflocal(Double disponibleeflocal) {
		this.disponibleeflocal = disponibleeflocal;
	}
	/**
	 * @return the disponibleefinter
	 */
	public Double getDisponibleefinter() {
		return disponibleefinter;
	}
	/**
	 * @param disponibleefinter the disponibleefinter to set
	 */
	public void setDisponibleefinter(Double disponibleefinter) {
		this.disponibleefinter = disponibleefinter;
	}
	/**
	 * @return the debitotransitolocal
	 */
	public Double getDebitotransitolocal() {
		return debitotransitolocal;
	}
	/**
	 * @param debitotransitolocal the debitotransitolocal to set
	 */
	public void setDebitotransitolocal(Double debitotransitolocal) {
		this.debitotransitolocal = debitotransitolocal;
	}
	/**
	 * @return the debitotransitointer
	 */
	public Double getDebitotransitointer() {
		return debitotransitointer;
	}
	/**
	 * @param debitotransitointer the debitotransitointer to set
	 */
	public void setDebitotransitointer(Double debitotransitointer) {
		this.debitotransitointer = debitotransitointer;
	}
	/**
	 * @return the localBalance
	 */
	public Double getLocalBalance() {
		return localBalance;
	}
	/**
	 * @param localBalance the localBalance to set
	 */
	public void setLocalBalance(Double localBalance) {
		this.localBalance = localBalance;
	}
	/**
	 * @return the localMinimutmPayment
	 */
	public Double getLocalMinimutmPayment() {
		return localMinimutmPayment;
	}
	/**
	 * @param localMinimutmPayment the localMinimutmPayment to set
	 */
	public void setLocalMinimutmPayment(Double localMinimutmPayment) {
		this.localMinimutmPayment = localMinimutmPayment;
	}
	/**
	 * @return the cashPaymentLocalCurrency
	 */
	public Double getCashPaymentLocalCurrency() {
		return cashPaymentLocalCurrency;
	}
	/**
	 * @param cashPaymentLocalCurrency the cashPaymentLocalCurrency to set
	 */
	public void setCashPaymentLocalCurrency(Double cashPaymentLocalCurrency) {
		this.cashPaymentLocalCurrency = cashPaymentLocalCurrency;
	}
	/**
	 * @return the availableLocal
	 */
	public Double getAvailableLocal() {
		return availableLocal;
	}
	/**
	 * @param availableLocal the availableLocal to set
	 */
	public void setAvailableLocal(Double availableLocal) {
		this.availableLocal = availableLocal;
	}
	/**
	 * @return the internationalBalance
	 */
	public Double getInternationalBalance() {
		return internationalBalance;
	}
	/**
	 * @param internationalBalance the internationalBalance to set
	 */
	public void setInternationalBalance(Double internationalBalance) {
		this.internationalBalance = internationalBalance;
	}
	/**
	 * @return the internationalMinimumPayment
	 */
	public Double getInternationalMinimumPayment() {
		return internationalMinimumPayment;
	}
	/**
	 * @param internationalMinimumPayment the internationalMinimumPayment to set
	 */
	public void setInternationalMinimumPayment(Double internationalMinimumPayment) {
		this.internationalMinimumPayment = internationalMinimumPayment;
	}
	/**
	 * @return the cashPaymentInternationalCurrency
	 */
	public Double getCashPaymentInternationalCurrency() {
		return cashPaymentInternationalCurrency;
	}
	/**
	 * @param cashPaymentInternationalCurrency the cashPaymentInternationalCurrency to set
	 */
	public void setCashPaymentInternationalCurrency(
			Double cashPaymentInternationalCurrency) {
		this.cashPaymentInternationalCurrency = cashPaymentInternationalCurrency;
	}
	/**
	 * @return the availableInternational
	 */
	public Double getAvailableInternational() {
		return availableInternational;
	}
	/**
	 * @param availableInternational the availableInternational to set
	 */
	public void setAvailableInternational(Double availableInternational) {
		this.availableInternational = availableInternational;
	}
	/**
	 * @return the paymentDate
	 */
	public String getPaymentDate() {
		return paymentDate;
	}
	/**
	 * @param paymentDate the paymentDate to set
	 */
	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}
	/**
	 * @return the availableLocalEF
	 */
	public Double getAvailableLocalEF() {
		return availableLocalEF;
	}
	/**
	 * @param availableLocalEF the availableLocalEF to set
	 */
	public void setAvailableLocalEF(Double availableLocalEF) {
		this.availableLocalEF = availableLocalEF;
	}
	/**
	 * @return the availableInternationalEF
	 */
	public Double getAvailableInternationalEF() {
		return availableInternationalEF;
	}
	/**
	 * @param availableInternationalEF the availableInternationalEF to set
	 */
	public void setAvailableInternationalEF(Double availableInternationalEF) {
		this.availableInternationalEF = availableInternationalEF;
	}
	/**
	 * @return the debitLocalTransit
	 */
	public Double getDebitLocalTransit() {
		return debitLocalTransit;
	}
	/**
	 * @param debitLocalTransit the debitLocalTransit to set
	 */
	public void setDebitLocalTransit(Double debitLocalTransit) {
		this.debitLocalTransit = debitLocalTransit;
	}
	/**
	 * @return the debitInternationalTransit
	 */
	public Double getDebitInternationalTransit() {
		return debitInternationalTransit;
	}
	/**
	 * @param debitInternationalTransit the debitInternationalTransit to set
	 */
	public void setDebitInternationalTransit(Double debitInternationalTransit) {
		this.debitInternationalTransit = debitInternationalTransit;
	}
	/**
	 * @return the responseCode
	 */
	public String getResponseCode() {
		return responseCode;
	}
	/**
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	
	
	
}
