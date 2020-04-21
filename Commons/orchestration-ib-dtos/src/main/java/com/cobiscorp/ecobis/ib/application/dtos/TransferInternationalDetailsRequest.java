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
	var nombreClase       = "TransferInternationalDetailsRequest";
	var tipoDato          = ["String","String","String","String","String","String","String","String","String","String","String","String","String"];
	var nombreAtributo    = ["criteria","initialCheck","criteria2","sequential","initialDate","finalDate","mode","lastResult","productNumber","numberOfResults","productId","login","currencyId"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 13;
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
 * @since Nov 6, 2014
 * @version 1.0.0
 */
public class TransferInternationalDetailsRequest extends BaseRequest{
	private String criteria;
	private String initialCheck;
	private String criteria2;
	private String sequential;
	private String initialDate;
	private String finalDate;
	private String mode;
	private String lastResult;
	private String productNumber;
	private String numberOfResults;
	private String productId;
	private String login;
	private String currencyId;
	/**
	 * @return the criteria
	 */
	public String getCriteria() {
		return criteria;
	}
	/**
	 * @param criteria the criteria to set
	 */
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}
	/**
	 * @return the initialCheck
	 */
	public String getInitialCheck() {
		return initialCheck;
	}
	/**
	 * @param initialCheck the initialCheck to set
	 */
	public void setInitialCheck(String initialCheck) {
		this.initialCheck = initialCheck;
	}
	/**
	 * @return the criteria2
	 */
	public String getCriteria2() {
		return criteria2;
	}
	/**
	 * @param criteria2 the criteria2 to set
	 */
	public void setCriteria2(String criteria2) {
		this.criteria2 = criteria2;
	}
	/**
	 * @return the sequential
	 */
	public String getSequential() {
		return sequential;
	}
	/**
	 * @param sequential the sequential to set
	 */
	public void setSequential(String sequential) {
		this.sequential = sequential;
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
	 * @return the finalDate
	 */
	public String getFinalDate() {
		return finalDate;
	}
	/**
	 * @param finalDate the finalDate to set
	 */
	public void setFinalDate(String finalDate) {
		this.finalDate = finalDate;
	}
	/**
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}
	/**
	 * @param mode the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}
	/**
	 * @return the lastResult
	 */
	public String getLastResult() {
		return lastResult;
	}
	/**
	 * @param lastResult the lastResult to set
	 */
	public void setLastResult(String lastResult) {
		this.lastResult = lastResult;
	}
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
	 * @return the numberOfResults
	 */
	public String getNumberOfResults() {
		return numberOfResults;
	}
	/**
	 * @param numberOfResults the numberOfResults to set
	 */
	public void setNumberOfResults(String numberOfResults) {
		this.numberOfResults = numberOfResults;
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
		return "transferInternationalDetailsRequest [criteria=" + criteria
				+ ", initialCheck=" + initialCheck + ", criteria2=" + criteria2
				+ ", sequential=" + sequential + ", initialDate=" + initialDate
				+ ", finalDate=" + finalDate + ", mode=" + mode
				+ ", lastResult=" + lastResult + ", productNumber="
				+ productNumber + ", numberOfResults=" + numberOfResults
				+ ", productId=" + productId + ", login=" + login
				+ ", currencyId=" + currencyId + "]";
	}
}
