/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;
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
	var nombreClase       = "SentInternationalTransferRequest";
	var tipoDato          = ["String","Integer","String","Integer","String","String","Integer","String","Product","String"];
	var nombreAtributo    = ["criteria","initialCheck","criteria2","sequential","initialDate","finalDate","mode","lastResult","product","numberOfResult"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 10;
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
 * @since Nov 6, 2014
 * @version 1.0.0
 */
public class SentInternationalTransferRequest extends TransferRequest {
	private String  criteria; 
	private Integer initialCheck;
	private String  criteria2;
	private Integer sequential;
	private String  initialDate;
	private String  finalDate;
	private Integer mode;
	private String  lastResult;
	private Product product;
	private String  numberOfResult;
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
	public Integer getInitialCheck() {
		return initialCheck;
	}
	/**
	 * @param initialCheck the initialCheck to set
	 */
	public void setInitialCheck(Integer initialCheck) {
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
	public Integer getSequential() {
		return sequential;
	}
	/**
	 * @param sequential the sequential to set
	 */
	public void setSequential(Integer sequential) {
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
	 * @return the product
	 */
	public Product getProduct() {
		return product;
	}
	/**
	 * @param product the product to set
	 */
	public void setProduct(Product product) {
		this.product = product;
	}
	/**
	 * @return the numberOfResult
	 */
	public String getNumberOfResult() {
		return numberOfResult;
	}
	/**
	 * @param numberOfResult the numberOfResult to set
	 */
	public void setNumberOfResult(String numberOfResult) {
		this.numberOfResult = numberOfResult;
	}
	
}
