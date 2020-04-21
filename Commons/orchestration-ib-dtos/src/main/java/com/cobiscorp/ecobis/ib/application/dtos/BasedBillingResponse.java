/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.List;

import com.cobiscorp.ecobis.ib.orchestration.dtos.BasedBilling;

/**
<!--   Autor: Baque H Jorge
  	   nombreClase	    : Se coloca el nombre de la clase java
	   tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
       nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
       				["altura", "edad", "peso"]
       descripcionClase: Lleva una breve descripción de la clase
       numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "BasedBillingResponse";
	var tipoDato          = ["List<BasedBilling>"];
	var nombreAtributo    = ["basedBillingInformation"];
	var descripcionClase  = "Define los atributos de respuesta para la consulta del pago tipo base facturacion";
	var numeroAtributos   = 1;
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
public class BasedBillingResponse extends BaseResponse {
	
	private List<BasedBilling> basedBillingInformation = null;

	/**
	 * @return the basedBillingInformation
	 */
	public List<BasedBilling> getBasedBillingInformation() {
		return basedBillingInformation;
	}

	/**
	 * @param basedBillingInformation the basedBillingInformation to set
	 */
	public void setBasedBillingInformation(
			List<BasedBilling> basedBillingInformation) {
		this.basedBillingInformation = basedBillingInformation;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BasedBillingResponse [basedBillingInformation="
				+ basedBillingInformation + "]";
	}


	
	
	
}
