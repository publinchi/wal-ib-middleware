/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

import java.math.BigDecimal;


/**
 * 
 * 
<script type="text/javascript">
	var nombreClase       = "BatchRefreshRelationshipWithDollar";
	var tipoDato          = ["String","Currency","String","String","int","String","int","int"];
	var nombreAtributo    = ["checkingType","currency","description","status","numberOfChecks","associatedAccount","lowerRange","higherRange"];
	var descripcionClase  = "DTO de Orquestaci&oacute;n";
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
  		}</script></td>
  <td style=" font-family:'Courier New', Courier, monospace;color:#00F"><script type="text/javascript">
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
</table>
 * 
 * 
 * @author wtoledo
 * @since Mar 24, 2015
 * @version 1.0.0
 * @category application dto
 */
public class BatchRefreshRelationshipWithDollar {
	/*---	cob_tesoreria..te_relacion_dolar	---		*/
	private Currency currencyCode; //rd_cod_moneda    tinyint
	private String initialDate; //rd_fecha_inicial datetime
	private String finalDate; //rd_fecha_final   datetime
	private String status; //rd_estado        char
	private BigDecimal amount; //rd_valor         float
	private Integer sequential; //rd_secuencial    int
	private BigDecimal amountV; //rd_valor_v       float
	private String operator;//rd_operador      char
	private BigDecimal buyingPrice;//rd_cot_comp      float
	private BigDecimal sellingPrice;//rd_cot_vent      float
	private Integer marketCode;//rd_cod_mercado   tinyint
	/**
	 * @return the currencyCode
	 */
	public Currency getCurrencyCode() {
		return currencyCode;
	}
	/**
	 * @param currencyCode the currencyCode to set
	 */
	public void setCurrencyCode(Currency currencyCode) {
		this.currencyCode = currencyCode;
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
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
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
	 * @return the amountV
	 */
	public BigDecimal getAmountV() {
		return amountV;
	}
	/**
	 * @param amountV the amountV to set
	 */
	public void setAmountV(BigDecimal amountV) {
		this.amountV = amountV;
	}
	/**
	 * @return the operator
	 */
	public String getOperator() {
		return operator;
	}
	/**
	 * @param operator the operator to set
	 */
	public void setOperator(String operator) {
		this.operator = operator;
	}
	/**
	 * @return the buyingPrice
	 */
	public BigDecimal getBuyingPrice() {
		return buyingPrice;
	}
	/**
	 * @param buyingPrice the buyingPrice to set
	 */
	public void setBuyingPrice(BigDecimal buyingPrice) {
		this.buyingPrice = buyingPrice;
	}
	/**
	 * @return the sellingPrice
	 */
	public BigDecimal getSellingPrice() {
		return sellingPrice;
	}
	/**
	 * @param sellingPrice the sellingPrice to set
	 */
	public void setSellingPrice(BigDecimal sellingPrice) {
		this.sellingPrice = sellingPrice;
	}
	/**
	 * @return the marketCode
	 */
	public Integer getMarketCode() {
		return marketCode;
	}
	/**
	 * @param marketCode the marketCode to set
	 */
	public void setMarketCode(Integer marketCode) {
		this.marketCode = marketCode;
	}
	
}
