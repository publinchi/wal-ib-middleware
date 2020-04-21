/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

/**
 <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "LoanSimulationRequest";
	var tipoDato          = ["Integer","String","Double","String","String",
							"Integer","String","String","String","Integer",
							"Integer","String"];
	var nombreAtributo    = ["trn","operation","ammount","sector","operation_type",
							"currency_id","initial_date","code","amortization_type",
							"cuota","term","entity_type"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 12;
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
 * @since Dec 3, 2014
 * @version 1.0.0
 */
public class LoanSimulationRequest extends BaseRequest  {
	private Integer trn;
	private String  operation;
	private Double  ammount;
	private String  sector;
	private String  operation_type;
	private Integer currency_id;
	private String  initial_date;
	private String  code;
	private String  amortization_type;
	private Integer cuota;
	private Integer term;                     /*plazo*/	
	private String  entity_type;              /* Subtipo de cliente: natural (P), juridica (C), cifrada (I)*/
	/**
	 * @return the trn
	 */
	public Integer getTrn() {
		return trn;
	}
	/**
	 * @param trn the trn to set
	 */
	public void setTrn(Integer trn) {
		this.trn = trn;
	}
	/**
	 * @return the operation
	 */
	public String getOperation() {
		return operation;
	}
	/**
	 * @param operation the operation to set
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}
	/**
	 * @return the ammount
	 */
	public Double getAmmount() {
		return ammount;
	}
	/**
	 * @param ammount the ammount to set
	 */
	public void setAmmount(Double ammount) {
		this.ammount = ammount;
	}
	/**
	 * @return the sector
	 */
	public String getSector() {
		return sector;
	}
	/**
	 * @param sector the sector to set
	 */
	public void setSector(String sector) {
		this.sector = sector;
	}
	/**
	 * @return the operation_type
	 */
	public String getOperation_type() {
		return operation_type;
	}
	/**
	 * @param operation_type the operation_type to set
	 */
	public void setOperation_type(String operation_type) {
		this.operation_type = operation_type;
	}
	/**
	 * @return the currency_id
	 */
	public Integer getCurrency_id() {
		return currency_id;
	}
	/**
	 * @param currency_id the currency_id to set
	 */
	public void setCurrency_id(Integer currency_id) {
		this.currency_id = currency_id;
	}
	/**
	 * @return the initial_date
	 */
	public String getInitial_date() {
		return initial_date;
	}
	/**
	 * @param initial_date the initial_date to set
	 */
	public void setInitial_date(String initial_date) {
		this.initial_date = initial_date;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
	}
	/**
	 * @return the amortization_type
	 */
	public String getAmortization_type() {
		return amortization_type;
	}
	/**
	 * @param amortization_type the amortization_type to set
	 */
	public void setAmortization_type(String amortization_type) {
		this.amortization_type = amortization_type;
	}
	/**
	 * @return the cuota
	 */
	public Integer getCuota() {
		return cuota;
	}
	/**
	 * @param cuota the cuota to set
	 */
	public void setCuota(Integer cuota) {
		this.cuota = cuota;
	}
	/**
	 * @return the term
	 */
	public Integer getTerm() {
		return term;
	}
	/**
	 * @param term the term to set
	 */
	public void setTerm(Integer term) {
		this.term = term;
	}
	/**
	 * @return the entity_type
	 */
	public String getEntity_type() {
		return entity_type;
	}
	/**
	 * @param entity_type the entity_type to set
	 */
	public void setEntity_type(String entity_type) {
		this.entity_type = entity_type;
	}	
	
}
