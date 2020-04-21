/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Secuential;
import com.cobiscorp.ecobis.ib.orchestration.dtos.TimeDepositsHistoricals;

/**
     <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "TimeDepositsHistoricalsRequest";
	var tipoDato          = [""TimeDepositsHistoricals","Product","Secuential","Integer","Integer""];
	var nombreAtributo    = ["timeDepositsHistoricals","product","secuential","dateFormat","productId"];
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
 * @author jchonillo
 * @since Oct 20, 2014
 * @version 1.0.0
 */
public class TimeDepositsHistoricalsRequest extends BaseRequest {

	private TimeDepositsHistoricals timeDepositsHistoricals;
	private Product product;
    private Secuential secuential;
    private Integer dateFormat;
    private Integer productId;
    
	/**
	 * @return the timeDepositsHistoricals
	 */
	public TimeDepositsHistoricals getTimeDepositsHistoricals() {
		return timeDepositsHistoricals;
	}
	/**
	 * set the timeDepositsHistoricals
	 */
	public void setTimeDepositsHistoricals(
			TimeDepositsHistoricals timeDepositsHistoricals) {
		this.timeDepositsHistoricals = timeDepositsHistoricals;
	}
	/**
	 * @return the Product
	 */
	public Product getProduct() {
		return product;
	}
	/**
	 * set the Product
	 */
	public void setProduct(Product product) {
		this.product = product;
	}
	/**
	 * @return the Secuential
	 */
	public Secuential getSecuential() {
		return secuential;
	}
	/**
	 * set the Secuential
	 */
	public void setSecuential(Secuential secuential) {
		this.secuential = secuential;
	}
	/**
	 * @return the dateFormat
	 */
	public Integer getDateFormat() {
		return dateFormat;
	}
	/**
	 * set the Secuential (Integer)
	 */
	public void setDateFormat(Integer dateFormat) {
		this.dateFormat = dateFormat;
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
	
	@Override
	public String toString() {
		return "TimeDepositsHistoricalsRequest [timeDepositsHistoricals="
				+ timeDepositsHistoricals + ", product=" + product
				+ ", secuential=" + secuential + ", dateFormat=" + dateFormat
				+ ", productId=" + productId + "]";
	}

}
