/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Batch;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;

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
	var tipoDato          = ["Batch","codigo","condicion","valor_condicion","productInfo","fecha_proceso","filial"];
	var nombreAtributo    = ["batchInfo","codigo","condicion","valor_condicion","productInfo","fecha_proceso","filial"];
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
 * @author wsanchez
 * @since Jan 30, 2015
 * @version 1.0.0
 */
public class BatchNotificationRequest extends BaseRequest{

	private Batch batchInfo;
	private String codigo;
	private String condicion;
	private String valor_condicion;
	private Product productInfo;
	private String fecha_proceso;
	private int filial;
	
	/**
	 * @return the batch
	 */
	public Batch getBatchInfo() {
		return batchInfo;
	}
	/**
	 * @param batch the batch to set
	 */
	public void setBatchInfo(Batch batch) {
		this.batchInfo = batch;
	}
	/**
	 * @return the codigo
	 */
	public String getCodigo() {
		return codigo;
	}
	/**
	 * @param codigo the codigo to set
	 */
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	/**
	 * @return the condicion
	 */
	public String getCondicion() {
		return condicion;
	}
	/**
	 * @param condicion the condicion to set
	 */
	public void setCondicion(String condicion) {
		this.condicion = condicion;
	}
	/**
	 * @return the valor_condicion
	 */
	public String getValor_condicion() {
		return valor_condicion;
	}
	/**
	 * @param valor_condicion the valor_condicion to set
	 */
	public void setValor_condicion(String valor_condicion) {
		this.valor_condicion = valor_condicion;
	}
	/**
	 * @return the productInfo
	 */
	public Product getProductInfo() {
		return productInfo;
	}
	/**
	 * @param productInfo the productInfo to set
	 */
	public void setProductInfo(Product productInfo) {
		this.productInfo = productInfo;
	}
	/**
	 * @return the fecha_proceso
	 */
	public String getFecha_proceso() {
		return fecha_proceso;
	}
	/**
	 * @param fecha_proceso the fecha_proceso to set
	 */
	public void setFecha_proceso(String fecha_proceso) {
		this.fecha_proceso = fecha_proceso;
	}
	/**
	 * @return the filial
	 */
	public int getFilial() {
		return filial;
	}
	/**
	 * @param filial the filial to set
	 */
	public void setFilial(int filial) {
		this.filial = filial;
	}
	
	
	
}
