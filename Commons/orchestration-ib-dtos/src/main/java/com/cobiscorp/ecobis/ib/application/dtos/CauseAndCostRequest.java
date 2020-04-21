/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

/**
<!--	Autor: Torres Isaac
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "CauseAndCostRequest";
	var tipoDato          = ["int"];
	var nombreAtributo    = ["transaction"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 1;
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
<table><tbody>
 * @author itorres
 * @since Sep 24, 2014
 * @version 1.0.0
 */
public class CauseAndCostRequest extends BaseRequest {
	private Integer trn;
	private String operation;
	private Integer transBefore;
	private Integer transaction;
	private Integer product;
	private String service;
	private String type;
	private String cause;
	private String costTransaction;
	private Integer dateFormat;
	/**
	 * @return the trn
	 */
	public Integer getTrn() {
		return trn;
	}
	/**
	 * @param trn the trn to set
	 */
	public void setTrn(Integer trn) {
		this.trn = trn;
	}
	/**
	 * @return the operation
	 */
	public String getOperation() {
		return operation;
	}
	/**
	 * @param operation the operation to set
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}
	/**
	 * @return the transBefore
	 */
	public Integer getTransBefore() {
		return transBefore;
	}
	/**
	 * @param transBefore the transBefore to set
	 */
	public void setTransBefore(Integer transBefore) {
		this.transBefore = transBefore;
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
	 * @return the product
	 */
	public Integer getProduct() {
		return product;
	}
	/**
	 * @param product the product to set
	 */
	public void setProduct(Integer product) {
		this.product = product;
	}
	/**
	 * @return the service
	 */
	public String getService() {
		return service;
	}
	/**
	 * @param service the service to set
	 */
	public void setService(String service) {
		this.service = service;
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
	/**
	 * @return the costTransaction
	 */
	public String getCostTransaction() {
		return costTransaction;
	}
	/**
	 * @param costTransaction the costTransaction to set
	 */
	public void setCostTransaction(String costTransaction) {
		this.costTransaction = costTransaction;
	}
	/**
	 * @return the dateFormat
	 */
	public Integer getDateFormat() {
		return dateFormat;
	}
	/**
	 * @param dateFormat the dateFormat to set
	 */
	public void setDateFormat(Integer dateFormat) {
		this.dateFormat = dateFormat;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CauseAndCostRequest [trn=" + trn + ", operation=" + operation
				+ ", transBefore=" + transBefore + ", transaction="
				+ transaction + ", product=" + product + ", service=" + service
				+ ", type=" + type + ", cause=" + cause + ", costTransaction="
				+ costTransaction + ", dateFormat=" + dateFormat + "]";
	}	
		
}
