/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

import java.math.BigDecimal;


/**
 * 
 * 
<script type="text/javascript">
	var nombreClase       = "BatchRefreshCheckbookTypes";
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
 * @since Mar 13, 2015
 * @version 1.0.0
 * @category application dto
 */
public class BatchRefreshCheckbookTypes {

	private String checkingType; //Tipo de chequera
	private Currency currency; //Moneda
	private String description; //Descripcion
	private String status; //Estado
	private int numberOfChecks; //Numero de Cheques
	private String associatedAccount; //Cuenta Puente => Asociada con otra cuenta: S o N
	private int lowerRange; //Rango inferior
	private int higherRange; //Rango Superior
	/**
	 * @return the checkingType
	 */
	public String getCheckingType() {
		return checkingType;
	}
	/**
	 * @param checkingType the checkingType to set
	 */
	public void setCheckingType(String checkingType) {
		this.checkingType = checkingType;
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
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
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
	 * @return the numberOfChecks
	 */
	public int getNumberOfChecks() {
		return numberOfChecks;
	}
	/**
	 * @param numberOfChecks the numberOfChecks to set
	 */
	public void setNumberOfChecks(int numberOfChecks) {
		this.numberOfChecks = numberOfChecks;
	}
	/**
	 * @return the associatedAccount
	 */
	public String getAssociatedAccount() {
		return associatedAccount;
	}
	/**
	 * @param associatedAccount the associatedAccount to set
	 */
	public void setAssociatedAccount(String associatedAccount) {
		this.associatedAccount = associatedAccount;
	}
	/**
	 * @return the lowerRange
	 */
	public int getLowerRange() {
		return lowerRange;
	}
	/**
	 * @param lowerRange the lowerRange to set
	 */
	public void setLowerRange(int lowerRange) {
		this.lowerRange = lowerRange;
	}
	/**
	 * @return the higherRange
	 */
	public int getHigherRange() {
		return higherRange;
	}
	/**
	 * @param higherRange the higherRange to set
	 */
	public void setHigherRange(int higherRange) {
		this.higherRange = higherRange;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BatchRefreshCheckbookTypes [checkingType=" + checkingType
				+ ", currency=" + currency + ", description=" + description
				+ ", status=" + status + ", numberOfChecks=" + numberOfChecks
				+ ", associatedAccount=" + associatedAccount + ", lowerRange="
				+ lowerRange + ", higherRange=" + higherRange + "]";
	}
	
}
