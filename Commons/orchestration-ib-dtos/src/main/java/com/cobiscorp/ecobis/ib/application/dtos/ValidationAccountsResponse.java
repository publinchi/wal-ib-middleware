/**
 *
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.BalanceProduct;

/**
     <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "ValidationAccountsResponse";
	var tipoDato          = ["BalanceProduct","BalanceProduct","BalanceProduct"];
	var nombreAtributo    = ["originBalanceProduct","destinationLastBalanceProduct","destinationOldBalanceProduct"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 3;
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
 * @since Aug 27, 2014
 * @version 1.0.0
 */
public class ValidationAccountsResponse extends BaseResponse {
	private BalanceProduct originBalanceProduct;
	private BalanceProduct destinationLastBalanceProduct;
	private BalanceProduct destinationOldBalanceProduct;
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ValidationAccountsResponse [originBalanceProduct=" + originBalanceProduct + ", destinationLastBalanceProduct=" + destinationLastBalanceProduct + ", destinationOldBalanceProduct=" + destinationOldBalanceProduct + "]";
	}

	/**
	 * @return the originBalanceProduct
	 */
	public BalanceProduct getOriginBalanceProduct() {
		return originBalanceProduct;
	}

	/**
	 * @param originBalanceProduct
	 *            the originBalanceProduct to set
	 */
	public void setOriginBalanceProduct(BalanceProduct originBalanceProduct) {
		this.originBalanceProduct = originBalanceProduct;
	}

	/**
	 * @return the destinationLastBalanceProduct
	 */
	public BalanceProduct getDestinationLastBalanceProduct() {
		return destinationLastBalanceProduct;
	}

	/**
	 * @param destinationLastBalanceProduct
	 *            the destinationLastBalanceProduct to set
	 */
	public void setDestinationLastBalanceProduct(BalanceProduct destinationLastBalanceProduct) {
		this.destinationLastBalanceProduct = destinationLastBalanceProduct;
	}

	/**
	 * @return the destinationOldBalanceProduct
	 */
	public BalanceProduct getDestinationOldBalanceProduct() {
		return destinationOldBalanceProduct;
	}

	/**
	 * @param destinationOldBalanceProduct
	 *            the destinationOldBalanceProduct to set
	 */
	public void setDestinationOldBalanceProduct(BalanceProduct destinationOldBalanceProduct) {
		this.destinationOldBalanceProduct = destinationOldBalanceProduct;
	}



}
