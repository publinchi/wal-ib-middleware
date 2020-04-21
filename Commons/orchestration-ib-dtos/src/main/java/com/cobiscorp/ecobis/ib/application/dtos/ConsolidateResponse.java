/**
 *
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.List;

import com.cobiscorp.ecobis.ib.orchestration.dtos.ProductConsolidate;

/**
 <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "ConsolidateResponse";
	var tipoDato          = ["List < ProductConsolidate >"];
	var nombreAtributo    = ["productCollection"];
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
</tbody></table>
 * @author schancay
 * @since Sep 2, 2014
 * @version 1.0.0
 */
public class ConsolidateResponse extends BaseResponse {
	
	public StringBuilder getSbProducts() {
		return sbProducts;
	}

	public void setSbProducts(StringBuilder sbProducts) {
		this.sbProducts = sbProducts;
	}

	private StringBuilder sbProducts;
	
	/**
	 * List od products of client
	 */
	private List<ProductConsolidate> productCollection;

	/**
	 * @return the productCollection
	 */
	public List<ProductConsolidate> getProductCollection() {
		return productCollection;
	}

	/**
	 * @param productCollection
	 *            the productCollection to set
	 */
	public void setProductCollection(List<ProductConsolidate> productCollection) {
		this.productCollection = productCollection;
	}

	@Override
	public String toString() {
		return "ConsolidateResponse [sbProducts=" + sbProducts
				+ ", productCollection=" + productCollection + "]";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	
}
