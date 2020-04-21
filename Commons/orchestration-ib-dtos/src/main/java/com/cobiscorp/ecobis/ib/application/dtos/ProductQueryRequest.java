/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.BalanceProduct;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;

/**
   <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "ProductQueryRequest";
	var tipoDato          = ["BalanceProduct","Client","int","int"];
	var nombreAtributo    = [""balanceProductRequest","user","queryResultsNumber","nextQueryNumber""];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 4;
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
 * @author djarrin
 * @since Sep 19, 2014
 * @version 1.0.0
 */
public class ProductQueryRequest extends ServerRequest{
	
	/**
	 * Information to Request the balance of Product
	 */
    private BalanceProduct balanceProductRequest;
    /**
     * User information to to the query
     */
    private Client user;
    
    /**
     * Number to limit the results of the query
     */
    private int queryResultsNumber;
    
    /**
     * Number used to report from what registration number should bring us the query
     */
     private int nextQueryNumber;
     
     
	/**
	 * @return the queryResultsNumber
	 */
	public int getQueryResultsNumber() {
		return queryResultsNumber;
	}
	/**
	 * @param queryResultsNumber the queryResultsNumber to set
	 */
	public void setQueryResultsNumber(int queryResultsNumber) {
		this.queryResultsNumber = queryResultsNumber;
	}
	/**
	 * @return the nextQueryNumber
	 */
	public int getNextQueryNumber() {
		return nextQueryNumber;
	}
	/**
	 * @param nextQueryNumber the nextQueryNumber to set
	 */
	public void setNextQueryNumber(int nextQueryNumber) {
		this.nextQueryNumber = nextQueryNumber;
	}
	/**
	 * @return the balanceProductRequest
	 */
	public BalanceProduct getBalanceProductRequest() {
		return balanceProductRequest;
	}
	/**
	 * @param balanceProductRequest the balanceProductRequest to set
	 */
	public void setBalanceProductRequest(BalanceProduct balanceProductRequest) {
		this.balanceProductRequest = balanceProductRequest;
	}
	/**
	 * @return the user
	 */
	public Client getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(Client user) {
		this.user = user;
	}
    
    
    
    
}
