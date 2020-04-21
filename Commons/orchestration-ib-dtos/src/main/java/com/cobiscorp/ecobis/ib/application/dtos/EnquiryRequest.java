/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;


/**
<!--   Autor: Baque H Jorge
  	   nombreClase	    : Se coloca el nombre de la clase java
	   tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
       nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
       				["altura", "edad", "peso"]
       descripcionClase: Lleva una breve descripción de la clase
       numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "EnquiryRequest";
	var tipoDato          = ["Integer","Integer","String","Integer","String","String"];
	var nombreAtributo    = ["productId","currencyId","productNumber","dateFormatId","transactionDate","userName"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 6;
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
 * @author jlvidal
 * @since 22/1/2015
 * @version 1.0.0
 */
public class EnquiryRequest {
	
	private Integer productId;
	private Integer currencyId;
	private String productNumber;
	private Integer dateFormatId;
	private String transactionDate;
	private String userName;
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
	 * @return the transactionDate
	 */
	public String getTransactionDate() {
		return transactionDate;
	}
	/**
	 * @param transactionDate the transactionDate to set
	 */
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
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
		return "EnquiryRequest [productId=" + productId + ", currencyId="
				+ currencyId + ", productNumber=" + productNumber
				+ ", dateFormatId=" + dateFormatId + ", transactionDate="
				+ transactionDate + ", userName=" + userName + "]";
	}
	
}
