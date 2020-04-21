/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

/**
<!--   Autor: Baque H Jorge
       nombreClase     : Se coloca el nombre de la clase java
       tipoDato        : Es un arreglo de tipo de datos ["String", "List", "int",...]
       nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
                                                                ["altura", "edad", "peso"]
       descripcionClase: Lleva una breve descripción de la clase
       numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
                var nombreClase       = "TransferACHRequest";
                var tipoDato          = ["String","String","String","Integer","Integer"];
                var nombreAtributo    = ["productNumber","initialDate","finalDate","dateFormatId","secuential"];
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
                                               }</script>
                </td>
</tr>

 <tr>
   <td>Descripci&oacute;n:</td>
   <td><script type="text/javascript">document.writeln(descripcionClase);</script></td>
</tr>
</tbody></table>
**/

/**
 * @author kmeza
 * @since Jan 13, 2015
 * @version 1.0.0
 */
public class TransferACHRequest extends BaseRequest {

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TransferACHRequest [productNumber=" + productNumber
				+ ", initialDate=" + initialDate + ", finalDate=" + finalDate
				+ ", dateFormatId=" + dateFormatId + ", secuential="
				+ secuential + "]";
	}
	private String productNumber;
	private String initialDate;
	private String finalDate;
	private Integer dateFormatId;
	private Integer secuential;
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
	 * @return the secuential
	 */
	public Integer getSecuential() {
		return secuential;
	}
	/**
	 * @param secuential the secuential to set
	 */
	public void setSecuential(Integer secuential) {
		this.secuential = secuential;
	}
	public TransferACHRequest() {
		// TODO Auto-generated constructor stub
	}

}
