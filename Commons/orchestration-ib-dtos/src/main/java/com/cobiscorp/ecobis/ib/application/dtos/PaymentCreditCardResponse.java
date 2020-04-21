/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.math.BigDecimal;

/**
 * 
   <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "PaymentCreditCardResponse";
	var tipoDato          = ["float","BigDecimal","int","int","String"];
	var nombreAtributo    = ["exchangeRate","convertValue","reference","ssnBranch","authorizationRequired"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 5;
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
 * @author cecheverria
 * @since Sep 25, 2014
 * @version 1.0.0
 */
public class PaymentCreditCardResponse extends BaseResponse {

	float exchangeRate;
	BigDecimal convertValue;
	int reference;
	int ssnBranch;
	String authorizationRequired;
	/**
	 * @return the exchangeRate
	 */
	public float getExchangeRate() {
		return exchangeRate;
	}
	/**
	 * @param exchangeRate the exchangeRate to set
	 */
	public void setExchangeRate(float exchangeRate) {
		this.exchangeRate = exchangeRate;
	}
	/**
	 * @return the convertValue
	 */
	public BigDecimal getConvertValue() {
		return convertValue;
	}
	/**
	 * @param convertValue the convertValue to set
	 */
	public void setConvertValue(BigDecimal convertValue) {
		this.convertValue = convertValue;
	}
	/**
	 * @return the reference
	 */
	public int getReference() {
		return reference;
	}
	/**
	 * @param reference the reference to set
	 */
	public void setReference(int reference) {
		this.reference = reference;
	}
	/**
	 * @return the ssnBranch
	 */
	public int getSsnBranch() {
		return ssnBranch;
	}
	/**
	 * @param ssnBranch the ssnBranch to set
	 */
	public void setSsnBranch(int ssnBranch) {
		this.ssnBranch = ssnBranch;
	}
	/**
	 * @return the authorizationRequired
	 */
	public String getAuthorizationRequired() {
		return authorizationRequired;
	}
	/**
	 * @param authorizationRequired the authorizationRequired to set
	 */
	public void setAuthorizationRequired(String authorizationRequired) {
		this.authorizationRequired = authorizationRequired;
	}
	
}
