/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

/**
  <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "PaymentDetailsTransfInternationalRequest";
	var tipoDato          = ["String","Integer","Integer","String","String","Integer","String","String","String"];
	var nombreAtributo    = ["productNumber","dateFormatId","mode","typeOperation","typeTransaction","transaction","productId","login","currencyId"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 9;
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
 * @author itorres
 * @since Nov 12, 2014
 * @version 1.0.0
 */
public class PaymentDetailsTransfInternationalRequest extends BaseRequest{
	private String productNumber;
	private Integer dateFormatId;
	private Integer mode;
	private String typeOperation;
	private String typeTransaction;
	private Integer transaction;
	
	private String productId;
	private String login;
	private String currencyId;
	/**
	 * @return the productNumber
	 */
	public String getProductNumber() {
		return productNumber;
	}
	/**
	 * @param productNumber the productNumber to set
	 */
	public void setProductNumber(String productNumber) {
		this.productNumber = productNumber;
	}
	/**
	 * @return the dateFormatId
	 */
	public Integer getDateFormatId() {
		return dateFormatId;
	}
	/**
	 * @param dateFormatId the dateFormatId to set
	 */
	public void setDateFormatId(Integer dateFormatId) {
		this.dateFormatId = dateFormatId;
	}
	/**
	 * @return the mode
	 */
	public Integer getMode() {
		return mode;
	}
	/**
	 * @param mode the mode to set
	 */
	public void setMode(Integer mode) {
		this.mode = mode;
	}
	/**
	 * @return the typeOperation
	 */
	public String getTypeOperation() {
		return typeOperation;
	}
	/**
	 * @param typeOperation the typeOperation to set
	 */
	public void setTypeOperation(String typeOperation) {
		this.typeOperation = typeOperation;
	}
	/**
	 * @return the typeTransaction
	 */
	public String getTypeTransaction() {
		return typeTransaction;
	}
	/**
	 * @param typeTransaction the typeTransaction to set
	 */
	public void setTypeTransaction(String typeTransaction) {
		this.typeTransaction = typeTransaction;
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
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}
	/**
	 * @param productId the productId to set
	 */
	public void setProductId(String productId) {
		this.productId = productId;
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
	 * @return the currencyId
	 */
	public String getCurrencyId() {
		return currencyId;
	}
	/**
	 * @param currencyId the currencyId to set
	 */
	public void setCurrencyId(String currencyId) {
		this.currencyId = currencyId;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PaymentDetailsTransfInternationalRequest [productNumber="
				+ productNumber + ", dateFormatId=" + dateFormatId + ", mode="
				+ mode + ", typeOperation=" + typeOperation
				+ ", typeTransaction=" + typeTransaction + ", transaction="
				+ transaction + "]";
	}
	
}
