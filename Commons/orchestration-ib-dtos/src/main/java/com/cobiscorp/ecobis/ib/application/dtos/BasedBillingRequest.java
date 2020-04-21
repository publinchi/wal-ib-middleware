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
	var nombreClase       = "BasedBillingRequest";
	var tipoDato          = ["Integer","String","String","String","Integer","String"];
	var nombreAtributo    = ["dateFormat","criteria","identification","name","contractId","operation"];
	var descripcionClase  = "Dto para obtener los datos de pago para el tipo de convenio base facturacion";
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
 * @author rperero
 * @since Feb 10, 2015
 * @version 1.0.0
 */

public class BasedBillingRequest extends BaseRequest{
	private Integer dateFormat = null;
	private String criteria = null;
	private String identification = null;
	private String name = null; 
	private Integer contractId = null;
	private String operation = null;
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
	 * @return the identification
	 */
	public String getIdentification() {
		return identification;
	}
	/**
	 * @param identification the identification to set
	 */
	public void setIdentification(String identification) {
		this.identification = identification;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the contractId
	 */
	public Integer getContractId() {
		return contractId;
	}
	/**
	 * @param contractId the contractId to set
	 */
	public void setContractId(Integer contractId) {
		this.contractId = contractId;
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
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BasedBillingRequest [dateFormat=" + dateFormat + ", criteria="
				+ criteria + ", identification=" + identification + ", name="
				+ name + ", contractId=" + contractId + ", operation="
				+ operation + "]";
	}
}
