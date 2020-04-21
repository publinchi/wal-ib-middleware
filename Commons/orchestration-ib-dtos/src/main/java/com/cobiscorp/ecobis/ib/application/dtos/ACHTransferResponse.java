/**
 * 
 */

package com.cobiscorp.ecobis.ib.application.dtos;


/**
<!--	Autor: Baque H Jorge

	 nombreClase	    : Se coloca el nombre de la clase java
	 tipoDato	        : Es un arreglo de tipo de datos ["String", "List", "int",...]
     nombreAtributo  	: Es un arreglo que contiene los nombre de atributos ["altura", "edad", "peso"]
     descripcionClase	: Lleva una breve descripción de la clase
     numeroAtributos : Numero total de atributos de [1,...n]-->
     
	<script type="text/javascript">
		var nombreClase       = "ACHTransferResponse";
		var tipoDato          = ["-"];
		var nombreAtributo    = ["-"];
		var descripcionClase  = "Transferencias ACH";
		var numeroAtributos   = 1;
	</script>     
 
 <table>
		<table><tbody>
			<tr>
  			<th Alignment="center" bgcolor="#CCCCFF">Nombre Clase: 
	    			<script type="text/javascript">document.writeln(nombreClase);</script> 
  			</th>
			</tr>
			<tr>
   			<td Alignment="center" bgcolor="#CCCCFF">Tipo Dato</td>
   			<td Alignment="center" bgcolor="#CCCCFF">Nombre Atributo</td>
			</tr>
			<tr>
   			<td style="font-family:'Courier New', Courier, monospace; color:#906;"><script type="text/javascript">
			  		for(i=0;i<numeroAtributos;i++){ 
			  		document.write(tipoDato[i]);
					document.write("<br/>");
			  		}</script>
			  	</td>
				<td style=" font-family:'Courier New', Courier, monospace;color:#00F"><script type="text/javascript">
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
	</table>
     
**/

/**
 * @author bborja
 * @since 15/1/2015
 * @version 1.0.0
*/



public class ACHTransferResponse extends TransferResponse {
	private Double accountCurrencyAmount;
	private Double accountCurrencyFee;
	private Double buyQuote;
	private Double sellQuote;
	private Double feeBuyQuote;
	private Double feeSellQuote;
	private String taxName1;
	private Double taxValue1;
	private String taxName2;
	private Double taxValue2;
	/**
	 * @return the accountCurrencyAmount
	 */
	public Double getAccountCurrencyAmount() {
		return accountCurrencyAmount;
	}
	/**
	 * @param accountCurrencyAmount the accountCurrencyAmount to set
	 */
	public void setAccountCurrencyAmount(Double accountCurrencyAmount) {
		this.accountCurrencyAmount = accountCurrencyAmount;
	}
	/**
	 * @return the accountCurrencyFee
	 */
	public Double getAccountCurrencyFee() {
		return accountCurrencyFee;
	}
	/**
	 * @param accountCurrencyFee the accountCurrencyFee to set
	 */
	public void setAccountCurrencyFee(Double accountCurrencyFee) {
		this.accountCurrencyFee = accountCurrencyFee;
	}
	/**
	 * @return the buyQuote
	 */
	public Double getBuyQuote() {
		return buyQuote;
	}
	/**
	 * @param buyQuote the buyQuote to set
	 */
	public void setBuyQuote(Double buyQuote) {
		this.buyQuote = buyQuote;
	}
	/**
	 * @return the sellQuote
	 */
	public Double getSellQuote() {
		return sellQuote;
	}
	/**
	 * @param sellQuote the sellQuote to set
	 */
	public void setSellQuote(Double sellQuote) {
		this.sellQuote = sellQuote;
	}
	/**
	 * @return the feeBuyQuote
	 */
	public Double getFeeBuyQuote() {
		return feeBuyQuote;
	}
	/**
	 * @param feeBuyQuote the feeBuyQuote to set
	 */
	public void setFeeBuyQuote(Double feeBuyQuote) {
		this.feeBuyQuote = feeBuyQuote;
	}
	/**
	 * @return the feeSellQuote
	 */
	public Double getFeeSellQuote() {
		return feeSellQuote;
	}
	/**
	 * @param feeSellQuote the feeSellQuote to set
	 */
	public void setFeeSellQuote(Double feeSellQuote) {
		this.feeSellQuote = feeSellQuote;
	}
	/**
	 * @return the taxName1
	 */
	public String getTaxName1() {
		return taxName1;
	}
	/**
	 * @param taxName1 the taxName1 to set
	 */
	public void setTaxName1(String taxName1) {
		this.taxName1 = taxName1;
	}
	/**
	 * @return the taxValue1
	 */
	public Double getTaxValue1() {
		return taxValue1;
	}
	/**
	 * @param taxValue1 the taxValue1 to set
	 */
	public void setTaxValue1(Double taxValue1) {
		this.taxValue1 = taxValue1;
	}
	/**
	 * @return the taxName2
	 */
	public String getTaxName2() {
		return taxName2;
	}
	/**
	 * @param taxName2 the taxName2 to set
	 */
	public void setTaxName2(String taxName2) {
		this.taxName2 = taxName2;
	}
	/**
	 * @return the taxValue2
	 */
	public Double getTaxValue2() {
		return taxValue2;
	}
	/**
	 * @param taxValue2 the taxValue2 to set
	 */
	public void setTaxValue2(Double taxValue2) {
		this.taxValue2 = taxValue2;
	}


}
