package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.Date;

import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountStatement;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
 
/**
 
 <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "AccountStatementRequest";
	var tipoDato          = ["AccountStatement","Product", "String",
	 						"String", "String", "String", "Date", 
	 						"Date", "String", "String", "String", 
	 						"String", "String", "Boolean", "Integer", 
	 						"Integer"];
	var nombreAtributo    = ["accountStatement", "product", "login", 
							"dateFormatId", "sequential", "alternateCode", 
							"initialDate", "finalDate", "initialDateString",
							"finalDateString", "type", "numberOfMovements",
							"uniqueSequential", "operationLastMovement",
							"daily", "mon"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 16;
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
  		}</script></td>
  </tr>
  
  <tr>
    <td>Descripci&oacute;n:</td>
    <td><script type="text/javascript">document.writeln(descripcionClase);</script></td>
  </tr>
</tbody></table>
 *
 * @author eortega
 * @since september 23, 2014
 * @version 1.0.0
 */
public class AccountStatementRequest extends BaseRequest {

	/**
	 * Contains the information about the account
	 */
	private AccountStatement accountStatement;
	private Product product;
	private String login;
	private String dateFormatId;
	private String sequential;
	private String alternateCode;
	private Date initialDate;
	private Date finalDate;
	private String initialDateString;
	private String finalDateString;
	private String type;
	private String numberOfMovements;
	private String uniqueSequential;
	private Boolean operationLastMovement;
    private Integer daily;
    private Integer mon;
    private String cause;

	/**
	 * @return the cause
	 */
	public String getCause() {
		return cause;
	}

	/**
	 * @param cause the cause to set
	 */
	public void setCause(String cause) {
		this.cause = cause;
	}

	public AccountStatement getAccountStatement() {
		return accountStatement;
	}

	public void setAccountStatement(AccountStatement accountStatement) {
		this.accountStatement = accountStatement;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getDateFormatId() {
		return dateFormatId;
	}

	public void setDateFormatId(String dateFormatId) {
		this.dateFormatId = dateFormatId;
	}

	public String getSequential() {
		return sequential;
	}

	public void setSequential(String sequential) {
		this.sequential = sequential;
	}

	public String getAlternateCode() {
		return alternateCode;
	}

	public void setAlternateCode(String alternateCode) {
		this.alternateCode = alternateCode;
	}

	public Date getInitialDate() {
		return initialDate;
	}

	public void setInitialDate(Date initialDate) {
		this.initialDate = initialDate;
	}

	public Date getFinalDate() {
		return finalDate;
	}

	public void setFinalDate(Date finalDate) {
		this.finalDate = finalDate;
	}
	/**
	 * @return the initialDateString
	 */
	public String getInitialDateString() {
		return initialDateString;
	}

	/**
	 * @param initialDateString the initialDateString to set
	 */
	public void setInitialDateString(String initialDateString) {
		this.initialDateString = initialDateString;
	}

	/**
	 * @return the finalDateString
	 */
	public String getFinalDateString() {
		return finalDateString;
	}

	/**
	 * @param finalDateString the finalDateString to set
	 */
	public void setFinalDateString(String finalDateString) {
		this.finalDateString = finalDateString;
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNumberOfMovements() {
		return numberOfMovements;
	}

	public void setNumberOfMovements(String numberOfMovements) {
		this.numberOfMovements = numberOfMovements;
	}

	public String getUniqueSequential() {
		return uniqueSequential;
	}

	public void setUniqueSequential(String uniqueSequential) {
		this.uniqueSequential = uniqueSequential;
	}

	public Boolean getOperationLastMovement() {
		return operationLastMovement;
	}

	public void setOperationLastMovement(Boolean operationLastMovement) {
		this.operationLastMovement = operationLastMovement;
	}

	/**
	 * @return the daily
	 */
	public Integer getDaily() {
		return daily;
	}

	/**
	 * @param daily the daily to set
	 */
	public void setDaily(Integer daily) {
		this.daily = daily;
	}
	/**
	 * @return the Mon
	 */
	public Integer getMon() {
		return mon;
	}

	/**
	 * @param mon the Mon to set
	 */
	public void setMon(Integer mon) {
		this.mon = mon;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AccountStatementRequest [accountStatement=" + accountStatement
				+ ", product=" + product + ", login=" + login
				+ ", dateFormatId=" + dateFormatId + ", sequential="
				+ sequential + ", alternateCode=" + alternateCode
				+ ", initialDate=" + initialDate + ", finalDate=" + finalDate
				+ ", initialDateString=" + initialDateString
				+ ", finalDateString=" + finalDateString + ", type=" + type
				+ ", numberOfMovements=" + numberOfMovements
				+ ", uniqueSequential=" + uniqueSequential
				+ ", operationLastMovement=" + operationLastMovement
				+ ", daily=" + daily + ", mon=" + mon + ", cause=" + cause
				+ "]";
	}
 

}
