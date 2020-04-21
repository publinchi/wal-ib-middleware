/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

import java.math.BigDecimal;


/**
 * 
 * 
<script type="text/javascript">
	var nombreClase       = "BatchRefreshForexRates";
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
public class BatchRefreshForexRates {
	/*---	cob_tesoreria..te_tasa_divisas	---		*/
	private Integer rateId; //td_num_tasa            int
	private Currency currency; //td_moneda              tinyint
	private Integer marketCode; //td_cod_mercado         tinyint
	private String recordDate; //td_fecha               datetime
	private String recordHour; //td_hora                datetime
	private BigDecimal buyingRate; //td_tasa_compra         float
	private BigDecimal sellingRate; //td_tasa_venta          float
	private BigDecimal buyingRateBill; //td_tasa_compra_billete float
	private BigDecimal sellingRateBill; //td_tasa_venta_billete  float
	private BigDecimal internalCost; //td_costo_interno       float
	private String authorized; //td_autorizado          char
	
	/**
	 * @return the rateId
	 */
	public Integer getRateId() {
		return rateId;
	}

	/**
	 * @param rateId the rateId to set
	 */
	public void setRateId(Integer rateId) {
		this.rateId = rateId;
	}


	/**
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}


	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
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


	/**
	 * @return the recordDate
	 */
	public String getRecordDate() {
		return recordDate;
	}


	/**
	 * @param recordDate the recordDate to set
	 */
	public void setRecordDate(String recordDate) {
		this.recordDate = recordDate;
	}


	/**
	 * @return the recordHour
	 */
	public String getRecordHour() {
		return recordHour;
	}


	/**
	 * @param recordHour the recordHour to set
	 */
	public void setRecordHour(String recordHour) {
		this.recordHour = recordHour;
	}


	/**
	 * @return the buyingRate
	 */
	public BigDecimal getBuyingRate() {
		return buyingRate;
	}


	/**
	 * @param buyingRate the buyingRate to set
	 */
	public void setBuyingRate(BigDecimal buyingRate) {
		this.buyingRate = buyingRate;
	}


	/**
	 * @return the sellingRate
	 */
	public BigDecimal getSellingRate() {
		return sellingRate;
	}


	/**
	 * @param sellingRate the sellingRate to set
	 */
	public void setSellingRate(BigDecimal sellingRate) {
		this.sellingRate = sellingRate;
	}


	/**
	 * @return the buyingRateBill
	 */
	public BigDecimal getBuyingRateBill() {
		return buyingRateBill;
	}


	/**
	 * @param buyingRateBill the buyingRateBill to set
	 */
	public void setBuyingRateBill(BigDecimal buyingRateBill) {
		this.buyingRateBill = buyingRateBill;
	}


	/**
	 * @return the sellingRateBill
	 */
	public BigDecimal getSellingRateBill() {
		return sellingRateBill;
	}


	/**
	 * @param sellingRateBill the sellingRateBill to set
	 */
	public void setSellingRateBill(BigDecimal sellingRateBill) {
		this.sellingRateBill = sellingRateBill;
	}


	/**
	 * @return the internalCost
	 */
	public BigDecimal getInternalCost() {
		return internalCost;
	}


	/**
	 * @param internalCost the internalCost to set
	 */
	public void setInternalCost(BigDecimal internalCost) {
		this.internalCost = internalCost;
	}


	/**
	 * @return the authorized
	 */
	public String getAuthorized() {
		return authorized;
	}


	/**
	 * @param authorized the authorized to set
	 */
	public void setAuthorized(String authorized) {
		this.authorized = authorized;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BatchRefreshForexRates [rateId=" + rateId + ", currency="
				+ currency + ", marketCode=" + marketCode + ", recordDate="
				+ recordDate + ", recordHour=" + recordHour + ", buyingRate="
				+ buyingRate + ", sellingRate=" + sellingRate
				+ ", buyingRateBill=" + buyingRateBill + ", sellingRateBill="
				+ sellingRateBill + ", internalCost=" + internalCost
				+ ", authorized=" + authorized + "]";
	}
	
	
}
