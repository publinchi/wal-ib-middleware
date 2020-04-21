/**
 *
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.BalanceProduct;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Currency;

/**
 <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "ConsolidateRequest";
	var tipoDato          = ["Integer", "Client", "Currency","BalanceProduct"];
	var nombreAtributo    = ["numberRegister", "client", "currency","balanceProduct"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 4;
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
 * @author schancay
 * @since Sep 2, 2014
 * @version 1.0.0
 */
public class ConsolidateRequest extends BaseRequest {
	/**
	 * Number of records
	 */
	private Integer numberRegister;

	/**
	 * Information of User
	 */
	private Client client;

	/**
	 * Information of money query products
	 */
	private Currency currency;
	
	private BalanceProduct balanceProduct;

	/**
	 * @return the client
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * object type BalanceProduct
	 */
	
	public BalanceProduct getBalanceProduct() {
		return balanceProduct;
	}

	public void setBalanceProduct(BalanceProduct balanceProduct) {
		this.balanceProduct = balanceProduct;
	}

	/**
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * @return the numberRegister
	 */
	public Integer getNumberRegister() {
		return numberRegister;
	}

	/**
	 * @param client
	 *            the client to set
	 */
	public void setClient(Client client) {
		this.client = client;
	}

	/**
	 * @param currency
	 *            the currency to set
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	/**
	 * @param numberRegister
	 *            the numberRegister to set
	 */
	public void setNumberRegister(Integer numberRegister) {
		this.numberRegister = numberRegister;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ConsolidateRequest [numberRegister=" + numberRegister + ", client=" + client + ", currency=" + currency + "]";
	}
	private Boolean haveToAddCountCte = true;

	public Boolean getHaveToAddCountCte() {
		return haveToAddCountCte;
	}

	public void setHaveToAddCountCte(Boolean haveToAddCountCte) {
		this.haveToAddCountCte = haveToAddCountCte;
	}
}
