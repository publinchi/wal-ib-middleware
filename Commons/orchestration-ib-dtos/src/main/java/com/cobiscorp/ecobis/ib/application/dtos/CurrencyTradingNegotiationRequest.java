/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;

/**
   <!--	Autor: Baque H Jorge
   		nombreClase	    : CurrencyTradingNegotiationRequest
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Dto para consulta de negoaciación de tipo de cambio
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "CurrencyTradingNegotiationRequest";
	var tipoDato          = [ "Integer","Integer","String","String","String","String","Integer"];
	var nombreAtributo    = ["client","currencyId","module","optionType","executionType","option","preAuthorizationSecuential"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 7;
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
 * @author kmeza
 * @since Nov 20, 2014
 * @version 1.0.0
 */
public class CurrencyTradingNegotiationRequest extends BaseRequest{

	private Integer client;
	private Integer currencyId;
	private String module;
	private String optionType;
	private String executionType;
	private String option;
	private Integer preAuthorizationSecuential;
	/**
	 * @return the client
	 */
	public Integer getClient() {
		return client;
	}
	/**
	 * @param client the client to set
	 */
	public void setClient(Integer client) {
		this.client = client;
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
	 * @return the module
	 */
	public String getModule() {
		return module;
	}
	/**
	 * @param module the module to set
	 */
	public void setModule(String module) {
		this.module = module;
	}
	/**
	 * @return the optionType
	 */
	public String getOptionType() {
		return optionType;
	}
	/**
	 * @param optionType the optionType to set
	 */
	public void setOptionType(String optionType) {
		this.optionType = optionType;
	}
	/**
	 * @return the executionType
	 */
	public String getExecutionType() {
		return executionType;
	}
	/**
	 * @param executionType the executionType to set
	 */
	public void setExecutionType(String executionType) {
		this.executionType = executionType;
	}
	/**
	 * @return the option
	 */
	public String getOption() {
		return option;
	}
	/**
	 * @param option the option to set
	 */
	public void setOption(String option) {
		this.option = option;
	}
	/**
	 * @return the preAuthorizationSecuential
	 */
	public Integer getPreAuthorizationSecuential() {
		return preAuthorizationSecuential;
	}
	/**
	 * @param preAuthorizationSecuential the preAuthorizationSecuential to set
	 */
	public void setPreAuthorizationSecuential(Integer preAuthorizationSecuential) {
		this.preAuthorizationSecuential = preAuthorizationSecuential;
	}
	
	

}
