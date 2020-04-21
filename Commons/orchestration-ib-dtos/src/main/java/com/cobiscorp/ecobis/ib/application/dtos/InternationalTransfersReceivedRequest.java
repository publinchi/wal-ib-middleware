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
	var nombreClase       = "InternationalTransfersReceivedRequest";
	var tipoDato          = ["Integer","String","String","String","String","Integer"];
	var nombreAtributo    = ["entityId","operation","initialDate","finalDate","criteria","dateFormat"];
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
  		}</script></td>
  </tr>
  
  <tr>
    <td>Descripci&oacute;n:</td>
    <td><script type="text/javascript">document.writeln(descripcionClase);</script></td>
  </tr>
</tbody></table>
 * @author itorres
 * @since Nov 4, 2014
 * @version 1.0.0
 */
public class InternationalTransfersReceivedRequest extends BaseRequest {
	
	private Integer entityId;
	private String operation;
	private String initialDate;
	private String finalDate;
	private String 	criteria;
	private Integer dateFormat;
	/**
	 * @return the entityId
	 */
	public Integer getEntityId() {
		return entityId;
	}
	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
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
	 * @return the criteria
	 */
	public String getCriteria() {
		return criteria;
	}
	/**
	 * @param criteria the criteria to set
	 */
	public void setCriteria(String criteria) {
		this.criteria = criteria;
	}
	/**
	 * @return the dateFormat
	 */
	public Integer getDateFormat() {
		return dateFormat;
	}
	/**
	 * @param dateFormat the dateFormat to set
	 */
	public void setDateFormat(Integer dateFormat) {
		this.dateFormat = dateFormat;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InternationalTransfersReceivedRequest [entityId=" + entityId
				+ ", operation=" + operation + ", initialDate=" + initialDate
				+ ", finalDate=" + finalDate + ", criteria=" + criteria
				+ ", dateFormat=" + dateFormat + "]";
	}
	
	
	

}
