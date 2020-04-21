/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

import java.math.BigDecimal;


/**
 * 
 * 
<script type="text/javascript">
	var nombreClase       = "BatchRefreshForexPosition";
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
public class BatchRefreshForexPosition {
	/*---	cob_tesoreria..te_posicion_divisa	---		*/
	//33
	private int positionCode; //pd_cod_posicion        int
	private Currency currency; //pd_moneda              tinyint
	private Office officeCode; //pd_cod_oficina         smallint
	private BigDecimal forexAmountToBuy; //pd_monto_c_divisa      money
	private BigDecimal sucreAmountToBuy; //pd_monto_c_sucre       money		XXXXXXXXXXXXXXXX
	private BigDecimal forexAmountToSell; //pd_monto_v_divisa      money
	private BigDecimal sucreAmountToSell; //pd_monto_v_sucre       money	XXXXXXXXXXXXXXXX
	private BigDecimal openingBalance; //pd_saldo_inicial       money
	private BigDecimal openingBalanceMn; //pd_saldo_inicial_mn    money
	private BigDecimal costPosition; //pd_costo_posicion      float
	private BigDecimal costToBuy; //pd_costo_compra        float	
	private BigDecimal costToSell; //pd_costo_venta         float	
	private BigDecimal exchangeDifference; //pd_dif_cambiario       float
	private BigDecimal buyingInterest; //pd_compra_int          money
	private BigDecimal sellingInterest; //pd_venta_int           money
	private BigDecimal marketBuying; //pd_compra_mer          money			XXXXXXXXXXXXXXXX
	private BigDecimal marketSelling; //pd_venta_merc          money		XXXXXXXXXXXXXXXX
	private BigDecimal incomeReevaluated; //pd_utilidad_revalua    float	XXXXXXXXXXXXXXXX
	private BigDecimal rateCross; //pd_tasa_cruzada        money,
	private String recordDate; //pd_fecha               datetime,
	private BigDecimal rateBC; //pd_tasa_bc             float,				XXXXXXXXXXXXXXXX
	private BigDecimal finalBalance; //pd_saldo_final         money,
	private BigDecimal finalBalanceMn; //pd_saldo_final_mn      money,		XXXXXXXXXXXXXXXX
	private BigDecimal incomeOfBuying; //pd_utilidad_compra     float,
	private BigDecimal incomeOfSelling; //pd_utilidad_venta      float,
	private BigDecimal diffExchangeOfSelling; //pd_dif_cambiario_venta float,XXXXXXXXXXXXXXXX
	private BigDecimal monthlyIncome; //pd_utilidad_mensual    float,
	private BigDecimal dollarFinalBalance; //pd_saldo_final_dol     money,	XXXXXXXXXXXXXXXX
	private BigDecimal costPosDol; //pd_costo_pos_dol       float,			XXXXXXXXXXXXXXXX
	private BigDecimal amountDollarToBuy; //pd_monto_c_dol         money ,	XXXXXXXXXXXXXXXX
	private BigDecimal costDollarToBuy; //pd_costo_c_dol         float,		XXXXXXXXXXXXXXXX
	private BigDecimal amountDollarToSell; //pd_monto_v_dol         money ,	XXXXXXXXXXXXXXXX
	private BigDecimal costDollarToSell; //pd_costo_v_dol         float		XXXXXXXXXXXXXXXX
	/**
	 * @return the positionCode
	 */
	public int getPositionCode() {
		return positionCode;
	}
	/**
	 * @param positionCode the positionCode to set
	 */
	public void setPositionCode(int positionCode) {
		this.positionCode = positionCode;
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
	 * @return the officeCode
	 */
	public Office getOfficeCode() {
		return officeCode;
	}
	/**
	 * @param officeCode the officeCode to set
	 */
	public void setOfficeCode(Office officeCode) {
		this.officeCode = officeCode;
	}
	/**
	 * @return the forexAmountToBuy
	 */
	public BigDecimal getForexAmountToBuy() {
		return forexAmountToBuy;
	}
	/**
	 * @param forexAmountToBuy the forexAmountToBuy to set
	 */
	public void setForexAmountToBuy(BigDecimal forexAmountToBuy) {
		this.forexAmountToBuy = forexAmountToBuy;
	}
	/**
	 * @return the sucreAmountToBuy
	 */
	public BigDecimal getSucreAmountToBuy() {
		return sucreAmountToBuy;
	}
	/**
	 * @param sucreAmountToBuy the sucreAmountToBuy to set
	 */
	public void setSucreAmountToBuy(BigDecimal sucreAmountToBuy) {
		this.sucreAmountToBuy = sucreAmountToBuy;
	}
	/**
	 * @return the forexAmountToSell
	 */
	public BigDecimal getForexAmountToSell() {
		return forexAmountToSell;
	}
	/**
	 * @param forexAmountToSell the forexAmountToSell to set
	 */
	public void setForexAmountToSell(BigDecimal forexAmountToSell) {
		this.forexAmountToSell = forexAmountToSell;
	}
	/**
	 * @return the sucreAmountToSell
	 */
	public BigDecimal getSucreAmountToSell() {
		return sucreAmountToSell;
	}
	/**
	 * @param sucreAmountToSell the sucreAmountToSell to set
	 */
	public void setSucreAmountToSell(BigDecimal sucreAmountToSell) {
		this.sucreAmountToSell = sucreAmountToSell;
	}
	/**
	 * @return the openingBalance
	 */
	public BigDecimal getOpeningBalance() {
		return openingBalance;
	}
	/**
	 * @param openingBalance the openingBalance to set
	 */
	public void setOpeningBalance(BigDecimal openingBalance) {
		this.openingBalance = openingBalance;
	}
	/**
	 * @return the openingBalanceMn
	 */
	public BigDecimal getOpeningBalanceMn() {
		return openingBalanceMn;
	}
	/**
	 * @param openingBalanceMn the openingBalanceMn to set
	 */
	public void setOpeningBalanceMn(BigDecimal openingBalanceMn) {
		this.openingBalanceMn = openingBalanceMn;
	}
	/**
	 * @return the costPosition
	 */
	public BigDecimal getCostPosition() {
		return costPosition;
	}
	/**
	 * @param costPosition the costPosition to set
	 */
	public void setCostPosition(BigDecimal costPosition) {
		this.costPosition = costPosition;
	}
	/**
	 * @return the costToBuy
	 */
	public BigDecimal getCostToBuy() {
		return costToBuy;
	}
	/**
	 * @param costToBuy the costToBuy to set
	 */
	public void setCostToBuy(BigDecimal costToBuy) {
		this.costToBuy = costToBuy;
	}
	/**
	 * @return the costToSell
	 */
	public BigDecimal getCostToSell() {
		return costToSell;
	}
	/**
	 * @param costToSell the costToSell to set
	 */
	public void setCostToSell(BigDecimal costToSell) {
		this.costToSell = costToSell;
	}
	/**
	 * @return the exchangeDifference
	 */
	public BigDecimal getExchangeDifference() {
		return exchangeDifference;
	}
	/**
	 * @param exchangeDifference the exchangeDifference to set
	 */
	public void setExchangeDifference(BigDecimal exchangeDifference) {
		this.exchangeDifference = exchangeDifference;
	}
	/**
	 * @return the buyingInterest
	 */
	public BigDecimal getBuyingInterest() {
		return buyingInterest;
	}
	/**
	 * @param buyingInterest the buyingInterest to set
	 */
	public void setBuyingInterest(BigDecimal buyingInterest) {
		this.buyingInterest = buyingInterest;
	}
	/**
	 * @return the sellingInterest
	 */
	public BigDecimal getSellingInterest() {
		return sellingInterest;
	}
	/**
	 * @param sellingInterest the sellingInterest to set
	 */
	public void setSellingInterest(BigDecimal sellingInterest) {
		this.sellingInterest = sellingInterest;
	}
	/**
	 * @return the marketBuying
	 */
	public BigDecimal getMarketBuying() {
		return marketBuying;
	}
	/**
	 * @param marketBuying the marketBuying to set
	 */
	public void setMarketBuying(BigDecimal marketBuying) {
		this.marketBuying = marketBuying;
	}
	/**
	 * @return the marketSelling
	 */
	public BigDecimal getMarketSelling() {
		return marketSelling;
	}
	/**
	 * @param marketSelling the marketSelling to set
	 */
	public void setMarketSelling(BigDecimal marketSelling) {
		this.marketSelling = marketSelling;
	}
	/**
	 * @return the incomeReevaluated
	 */
	public BigDecimal getIncomeReevaluated() {
		return incomeReevaluated;
	}
	/**
	 * @param incomeReevaluated the incomeReevaluated to set
	 */
	public void setIncomeReevaluated(BigDecimal incomeReevaluated) {
		this.incomeReevaluated = incomeReevaluated;
	}
	/**
	 * @return the rateCross
	 */
	public BigDecimal getRateCross() {
		return rateCross;
	}
	/**
	 * @param rateCross the rateCross to set
	 */
	public void setRateCross(BigDecimal rateCross) {
		this.rateCross = rateCross;
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
	 * @return the rateBC
	 */
	public BigDecimal getRateBC() {
		return rateBC;
	}
	/**
	 * @param rateBC the rateBC to set
	 */
	public void setRateBC(BigDecimal rateBC) {
		this.rateBC = rateBC;
	}
	/**
	 * @return the finalBalance
	 */
	public BigDecimal getFinalBalance() {
		return finalBalance;
	}
	/**
	 * @param finalBalance the finalBalance to set
	 */
	public void setFinalBalance(BigDecimal finalBalance) {
		this.finalBalance = finalBalance;
	}
	/**
	 * @return the finalBalanceMn
	 */
	public BigDecimal getFinalBalanceMn() {
		return finalBalanceMn;
	}
	/**
	 * @param finalBalanceMn the finalBalanceMn to set
	 */
	public void setFinalBalanceMn(BigDecimal finalBalanceMn) {
		this.finalBalanceMn = finalBalanceMn;
	}
	/**
	 * @return the incomeOfBuying
	 */
	public BigDecimal getIncomeOfBuying() {
		return incomeOfBuying;
	}
	/**
	 * @param incomeOfBuying the incomeOfBuying to set
	 */
	public void setIncomeOfBuying(BigDecimal incomeOfBuying) {
		this.incomeOfBuying = incomeOfBuying;
	}
	/**
	 * @return the incomeOfSelling
	 */
	public BigDecimal getIncomeOfSelling() {
		return incomeOfSelling;
	}
	/**
	 * @param incomeOfSelling the incomeOfSelling to set
	 */
	public void setIncomeOfSelling(BigDecimal incomeOfSelling) {
		this.incomeOfSelling = incomeOfSelling;
	}
	/**
	 * @return the diffExchangeOfSelling
	 */
	public BigDecimal getDiffExchangeOfSelling() {
		return diffExchangeOfSelling;
	}
	/**
	 * @param diffExchangeOfSelling the diffExchangeOfSelling to set
	 */
	public void setDiffExchangeOfSelling(BigDecimal diffExchangeOfSelling) {
		this.diffExchangeOfSelling = diffExchangeOfSelling;
	}
	/**
	 * @return the monthlyIncome
	 */
	public BigDecimal getMonthlyIncome() {
		return monthlyIncome;
	}
	/**
	 * @param monthlyIncome the monthlyIncome to set
	 */
	public void setMonthlyIncome(BigDecimal monthlyIncome) {
		this.monthlyIncome = monthlyIncome;
	}
	/**
	 * @return the dollarFinalBalance
	 */
	public BigDecimal getDollarFinalBalance() {
		return dollarFinalBalance;
	}
	/**
	 * @param dollarFinalBalance the dollarFinalBalance to set
	 */
	public void setDollarFinalBalance(BigDecimal dollarFinalBalance) {
		this.dollarFinalBalance = dollarFinalBalance;
	}
	/**
	 * @return the costPosDol
	 */
	public BigDecimal getCostPosDol() {
		return costPosDol;
	}
	/**
	 * @param costPosDol the costPosDol to set
	 */
	public void setCostPosDol(BigDecimal costPosDol) {
		this.costPosDol = costPosDol;
	}
	/**
	 * @return the amountDollarToBuy
	 */
	public BigDecimal getAmountDollarToBuy() {
		return amountDollarToBuy;
	}
	/**
	 * @param amountDollarToBuy the amountDollarToBuy to set
	 */
	public void setAmountDollarToBuy(BigDecimal amountDollarToBuy) {
		this.amountDollarToBuy = amountDollarToBuy;
	}
	/**
	 * @return the costDollarToBuy
	 */
	public BigDecimal getCostDollarToBuy() {
		return costDollarToBuy;
	}
	/**
	 * @param costDollarToBuy the costDollarToBuy to set
	 */
	public void setCostDollarToBuy(BigDecimal costDollarToBuy) {
		this.costDollarToBuy = costDollarToBuy;
	}
	/**
	 * @return the amountDollarToSell
	 */
	public BigDecimal getAmountDollarToSell() {
		return amountDollarToSell;
	}
	/**
	 * @param amountDollarToSell the amountDollarToSell to set
	 */
	public void setAmountDollarToSell(BigDecimal amountDollarToSell) {
		this.amountDollarToSell = amountDollarToSell;
	}
	/**
	 * @return the costDollarToSell
	 */
	public BigDecimal getCostDollarToSell() {
		return costDollarToSell;
	}
	/**
	 * @param costDollarToSell the costDollarToSell to set
	 */
	public void setCostDollarToSell(BigDecimal costDollarToSell) {
		this.costDollarToSell = costDollarToSell;
	}
	
	
}
