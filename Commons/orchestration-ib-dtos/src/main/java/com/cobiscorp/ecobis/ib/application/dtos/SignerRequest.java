/**
 *
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.math.BigDecimal;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
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
	var nombreClase       = "SignerRequest";
	var tipoDato          = ["Client", "Product","BigDecimal"];
	var nombreAtributo    = ["user", "originProduct", "ammount"];
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
 * @since Aug 29, 2014
 * @version 1.0.0
 */
public class SignerRequest extends BaseRequest {
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SignerRequest [originProduct=" + originProduct + ", ammount=" + ammount + ", user=" + user + "]";
	}
	private Client user;
	private Product originProduct;
	private BigDecimal ammount;
	/**
	 * @return the originProduct
	 */
	public Product getOriginProduct() {
		return originProduct;
	}

	/**
	 * @param originProduct
	 *            the originProduct to set
	 */
	public void setOriginProduct(Product originProduct) {
		this.originProduct = originProduct;
	}

	

	/**
	 * @return the ammount
	 */
	public BigDecimal getAmmount() {
		return ammount;
	}

	/**
	 * @param ammount
	 *            the ammount to set
	 */
	public void setAmmount(BigDecimal ammount) {
		this.ammount = ammount;
	}

	

	/**
	 * @return the user
	 */
	public Client getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(Client user) {
		this.user = user;
	}
}
