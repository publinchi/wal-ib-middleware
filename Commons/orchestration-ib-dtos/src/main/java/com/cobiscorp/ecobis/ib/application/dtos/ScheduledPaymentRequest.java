package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.User;
/**
<!--   Autor: Baque H Jorge
  	   nombreClase	    : Se coloca el nombre de la clase java
	   tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
       nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
       				["altura", "edad", "peso"]
       descripcionClase: Lleva una breve descripción de la clase
       numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "ScheduledPaymentRequest";
	var tipoDato          = ["String","String","Integer","Integer","User","Client","Product","Product","String","Integer","Double","String","Integer","String","String",
							 "String","String","String","String","Integer","String","String","String","String","String","PaymentServiceRequest"];
	var nombreAtributo    = ["ReferenceNumberBranch","ReferenceNumber","id","transaction","user","client","debitProduct","creditProduct","type","code","amount",
							"initialDate","paymentsNumber","frecuencyId","concept","item","login","option","receiveNotification","dayToNotify","recoveryRetryFailed",
							"nextPaymentDate","beneficiaryName",
							"operation","key","paymentService"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 26;
</script>
	
<table><tbody>
 <tr>
   <th colspan="2" bgcolor="#CCCCFF"><div>Nombre Clase:
	<script type="text/javascript">document.writeln(nombreClase);</script> 
   </th>
 </tr>
 <tr>
   <td colspan="2"><div>Atributos</div></td>
 </tr>
 <tr>
   <td width="auto" bgcolor="#CCCCFF"><div>Tipo de Dato</div></td>
   <td width="auto" bgcolor="#CCCCFF"><div>Nombre</div></td>
 </tr>
 <tr>
	 <td style="font-family:'Courier New', Courier, monospace; color:#906;"><div align="left"><script type="text/javascript">
	 		for(i=0;i<numeroAtributos;i++){ 
	 		document.write(tipoDato[i]);
			document.write("<br />");
	 		}</script></td>
	 <td style=" font-family:'Courier New', Courier, monospace;color:#00F"><div align="left"><script type="text/javascript">
	 		for(i=0;i<numeroAtributos;i++){ 
	 		document.write(nombreAtributo[i]);
			document.write("<br />");
	 		}</script>
	</td>
 </tr>
 
 <tr>
   <td>Descripci&oacute;n:</td>
   <td><script type="text/javascript">document.writeln(descripcionClase);</script></td>
 </tr>
</tbody></table>
**/

public class ScheduledPaymentRequest extends BaseRequest {


	private String ReferenceNumberBranch;
	private String ReferenceNumber;	
	private Integer transaction; 
	
	private User user;
	private Client client;
	private Product debitProduct;
	private Product creditProduct;	
	
	private Integer id;
	private String type;//tipo de pago programado (Transferencia, prestamo, pago servicio)
	private Integer code;	
	private Double amount;
	private String initialDate;
	private Integer paymentsNumber;
	private String frecuencyId;//nro de dias
	private String concept;
	private String item;
	private String login;
	private String option;
	private String receiveNotification;
	private Integer dayToNotify;
	private String recoveryRetryFailed;//reintentar cobro
	private String nextPaymentDate;
	private String beneficiaryName;
	private String operation;
	private String key;
	
	private String errorMessage;
	private String status;
	private Integer quantityOfDonePayments; //nro de pagos realizados
	private String registerDate;//fecha de registro 
	private String modifiedDate;//fecha de modificacion
	private String processedStatus;//estado procesado
	private String dayToRecoveryRetry;//dias de reintento de cobro
	private String dayToProcesedRecoveryRetry;//dias de reintento estado procesado
	/**
	 * @return the fundsSource
	 */
	public String getFundsSource() {
		return fundsSource;
	}
	/**
	 * @param fundsSource the fundsSource to set
	 */
	public void setFundsSource(String fundsSource) {
		this.fundsSource = fundsSource;
	}
	private String lastRecoveryDate;//fecha del ultimo cobro
	private String account;
	private String fundsSource;
	private String fundsUse;

	
	/**
	 * @return the fundsUse
	 */
	public String getFundsUse() {
		return fundsUse;
	}
	/**
	 * @param fundsUse the fundsUse to set
	 */
	public void setFundsUse(String fundsUse) {
		this.fundsUse = fundsUse;
	}
	private PaymentServiceRequest paymentService;
	
	/**
	 * @return the referenceNumberBranch
	 */	
	public String getReferenceNumberBranch() {
		return ReferenceNumberBranch;		
	}
	/**
	 * @param referenceNumberBranch the referenceNumberBranch to set
	 */
	public void setReferenceNumberBranch(String referenceNumberBranch) {
		ReferenceNumberBranch = referenceNumberBranch;
	}
	/**
	 * @return the referenceNumber
	 */
	public String getReferenceNumber() {
		return ReferenceNumber;
	}
	/**
	 * @param referenceNumber the referenceNumber to set
	 */
	public void setReferenceNumber(String referenceNumber) {
		ReferenceNumber = referenceNumber;
	}
	/**
	 * @return the id
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	 * @return the transaction
	 */
	public Integer getTransaction() {
		return transaction;
	}
	/**
	 * @param transaction the transaction to set
	 */
	public void setTransaction(Integer transaction) {
		this.transaction = transaction;
	}
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}	
	
	/**
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}
	/**
	 * @param client the client to set
	 */
	public void setClient(Client client) {
		this.client = client;
	}
	/**
	 * @return the debitProduct
	 */
	public Product getDebitProduct() {
		return debitProduct;
	}
	/**
	 * @param debitProduct the debitProduct to set
	 */
	public void setDebitProduct(Product debitProduct) {
		this.debitProduct = debitProduct;
	}
	/**
	 * @return the creditProduct
	 */
	public Product getCreditProduct() {
		return creditProduct;
	}
	/**
	 * @param creditProduct the creditProduct to set
	 */
	public void setCreditProduct(Product creditProduct) {
		this.creditProduct = creditProduct;
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
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(Integer code) {
		this.code = code;
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
	 * @return the paymentsNumber
	 */
	public Integer getPaymentsNumber() {
		return paymentsNumber;
	}
	/**
	 * @param paymentsNumber the paymentsNumber to set
	 */
	public void setPaymentsNumber(Integer paymentsNumber) {
		this.paymentsNumber = paymentsNumber;
	}
	/**
	 * @return the frecuencyId
	 */
	public String getFrecuencyId() {
		return frecuencyId;
	}
	/**
	 * @param frecuencyId the frecuencyId to set
	 */
	public void setFrecuencyId(String frecuencyId) {
		this.frecuencyId = frecuencyId;
	}
	/**
	 * @return the concept
	 */
	public String getConcept() {
		return concept;
	}
	/**
	 * @param concept the concept to set
	 */
	public void setConcept(String concept) {
		this.concept = concept;
	}
	/**
	 * @return the item
	 */
	public String getItem() {
		return item;
	}
	/**
	 * @param item the item to set
	 */
	public void setItem(String item) {
		this.item = item;
	}
	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}
	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}
	/**
	 * @return the option
	 */
	public String getOption() {
		return option;
	}
	/**
	 * @param option the option to set
	 */
	public void setOption(String option) {
		this.option = option;
	}
	/**
	 * @return the receiveNotification
	 */
	public String getReceiveNotification() {
		return receiveNotification;
	}
	/**
	 * @param receiveNotification the receiveNotification to set
	 */
	public void setReceiveNotification(String receiveNotification) {
		this.receiveNotification = receiveNotification;
	}
	/**
	 * @return the dayToNotify
	 */
	public Integer getDayToNotify() {
		return dayToNotify;
	}
	/**
	 * @param dayToNotify the dayToNotify to set
	 */
	public void setDayToNotify(Integer dayToNotify) {
		this.dayToNotify = dayToNotify;
	}
	/**
	 * @return the recoveryRetryFailed
	 */
	public String getRecoveryRetryFailed() {
		return recoveryRetryFailed;
	}
	/**
	 * @param recoveryRetryFailed the recoveryRetryFailed to set
	 */
	public void setRecoveryRetryFailed(String recoveryRetryFailed) {
		this.recoveryRetryFailed = recoveryRetryFailed;
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
	 * @return the beneficiaryName
	 */
	public String getBeneficiaryName() {
		return beneficiaryName;
	}
	/**
	 * @param beneficiaryName the beneficiaryName to set
	 */
	public void setBeneficiaryName(String beneficiaryName) {
		this.beneficiaryName = beneficiaryName;
	}
	/**
	 * @return the operacion
	 */
	public String getOperation() {
		return operation;
	}
	/**
	 * @param operacion the operacion to set
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}	
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return the paymentService
	 */
	public PaymentServiceRequest getPaymentService() {
		return paymentService;
	}
	/**
	 * @param paymentService the paymentService to set
	 */
	public void setPaymentService(PaymentServiceRequest paymentService) {
		this.paymentService = paymentService;
	}
	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}
	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
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
	 * @return the quantityOfDonePayments
	 */
	public Integer getQuantityOfDonePayments() {
		return quantityOfDonePayments;
	}
	/**
	 * @param quantityOfDonePayments the quantityOfDonePayments to set
	 */
	public void setQuantityOfDonePayments(Integer quantityOfDonePayments) {
		this.quantityOfDonePayments = quantityOfDonePayments;
	}
	/**
	 * @return the registerDate
	 */
	public String getRegisterDate() {
		return registerDate;
	}
	/**
	 * @param registerDate the registerDate to set
	 */
	public void setRegisterDate(String registerDate) {
		this.registerDate = registerDate;
	}
	/**
	 * @return the modifiedDate
	 */
	public String getModifiedDate() {
		return modifiedDate;
	}
	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	/**
	 * @return the processedStatus
	 */
	public String getProcessedStatus() {
		return processedStatus;
	}
	/**
	 * @param processedStatus the processedStatus to set
	 */
	public void setProcessedStatus(String processedStatus) {
		this.processedStatus = processedStatus;
	}
	/**
	 * @return the dayToRecoveryRetry
	 */
	public String getDayToRecoveryRetry() {
		return dayToRecoveryRetry;
	}
	/**
	 * @param dayToRecoveryRetry the dayToRecoveryRetry to set
	 */
	public void setDayToRecoveryRetry(String dayToRecoveryRetry) {
		this.dayToRecoveryRetry = dayToRecoveryRetry;
	}
	/**
	 * @return the dayToProcesedRecoveryRetry
	 */
	public String getDayToProcesedRecoveryRetry() {
		return dayToProcesedRecoveryRetry;
	}
	/**
	 * @param dayToProcesedRecoveryRetry the dayToProcesedRecoveryRetry to set
	 */
	public void setDayToProcesedRecoveryRetry(String dayToProcesedRecoveryRetry) {
		this.dayToProcesedRecoveryRetry = dayToProcesedRecoveryRetry;
	}
	/**
	 * @return the lastRecoveryDate
	 */
	public String getLastRecoveryDate() {
		return lastRecoveryDate;
	}
	/**
	 * @param lastRecoveryDate the lastRecoveryDate to set
	 */
	public void setLastRecoveryDate(String lastRecoveryDate) {
		this.lastRecoveryDate = lastRecoveryDate;
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
	
	
	
}

