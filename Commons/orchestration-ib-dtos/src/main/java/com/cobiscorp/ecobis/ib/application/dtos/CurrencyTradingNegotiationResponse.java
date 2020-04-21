/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.math.BigDecimal;
import java.util.List;

import com.cobiscorp.ecobis.ib.orchestration.dtos.FullEntity;

/**
 <!--	Autor: Baque H Jorge
   		nombreClase	    : CurrencyTradingNegotiationResponse
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "CurrencyTradingNegotiationResponse";
	var tipoDato          = ["float","BigDecimal","BigDecimal","String","String","BigDecimal","Integer","String"];
	var nombreAtributo    = ["quotedRate","amount","factor","observations","negotiationDate","otherBuyAmount","currencyId","currencyName"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 8;
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
 * @author mvelez
 * @since Nov 18, 2014
 * @version 1.0.0
 */
public class CurrencyTradingNegotiationResponse extends BaseResponse {
	
	private float quotedRate;
	private BigDecimal amount;
	private BigDecimal factor;
	private String observations;
	private String negotiationDate;
	private BigDecimal otherBuyAmount;
	private Integer currencyId;
	private String currencyName;
	/**
	 * @return the quotedRate
	 */
	public float getQuotedRate() {
		return quotedRate;
	}
	/**
	 * @param quotedRate the quotedRate to set
	 */
	public void setQuotedRate(float quotedRate) {
		this.quotedRate = quotedRate;
	}
	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	/**
	 * @return the factor
	 */
	public BigDecimal getFactor() {
		return factor;
	}
	/**
	 * @param factor the factor to set
	 */
	public void setFactor(BigDecimal factor) {
		this.factor = factor;
	}
	/**
	 * @return the observations
	 */
	public String getObservations() {
		return observations;
	}
	/**
	 * @param observations the observations to set
	 */
	public void setObservations(String observations) {
		this.observations = observations;
	}
	/**
	 * @return the negotiationDate
	 */
	public String getNegotiationDate() {
		return negotiationDate;
	}
	/**
	 * @param negotiationDate the negotiationDate to set
	 */
	public void setNegotiationDate(String negotiationDate) {
		this.negotiationDate = negotiationDate;
	}
	/**
	 * @return the otherBuyAmount
	 */
	public BigDecimal getOtherBuyAmount() {
		return otherBuyAmount;
	}
	/**
	 * @param otherBuyAmount the otherBuyAmount to set
	 */
	public void setOtherBuyAmount(BigDecimal otherBuyAmount) {
		this.otherBuyAmount = otherBuyAmount;
	}
	/**
	 * @return the currencyId
	 */
	public Integer getCurrencyId() {
		return currencyId;
	}
	/**
	 * @param currencyId the currencyId to set
	 */
	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}
	/**
	 * @return the currencyName
	 */
	public String getCurrencyName() {
		return currencyName;
	}
	/**
	 * @param currencyName the currencyName to set
	 */
	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}
	
	
}
