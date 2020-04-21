/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Secuential;

/**
     <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "TimeDepositsPayableInterestsRequest";
	var tipoDato          = ["Integer","Product","Secuential","Integer","String"];
	var nombreAtributo    = ["dateFormatId","productNumber","sequential","productId","userName"];
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
 * @author jmoreta
 * @since Oct 20, 2014
 * @version 1.0.0
 */
public class TimeDepositsPayableInterestsRequest extends BaseRequest {

	private Integer dateFormatId;
	/**String productNumber*/
	private Product productNumber;
	/**Integer sequential*/
	private Secuential sequential;
	
	private Integer productId;
	private String userName;
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
	 * @return the productNumber
	 */
	public Product getProductNumber() {
		return productNumber;
	}
	/**
	 * @param productNumber the productNumber to set
	 */
	public void setProductNumber(Product productNumber) {
		this.productNumber = productNumber;
	}
	/**
	 * @return the sequential
	 */
	public Secuential getSequential() {
		return sequential;
	}
	/**
	 * @param sequential the sequential to set
	 */
	public void setSequential(Secuential sequential) {
		this.sequential = sequential;
	}
	/**
	 * @return the productId
	 */
	public Integer getProductId() {
		return productId;
	}
	/**
	 * @param productId the productId to set
	 */
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TimeDepositsPayableInterestsRequest [dateFormatId="
				+ dateFormatId + ", productNumber=" + productNumber
				+ ", sequential=" + sequential + ", productId=" + productId
				+ ", userName=" + userName + "]";
	}

}
