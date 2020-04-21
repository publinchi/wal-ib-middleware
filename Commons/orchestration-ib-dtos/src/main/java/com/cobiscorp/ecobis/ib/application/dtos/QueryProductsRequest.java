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
	var nombreClase       = "QueryProductsRequest";
	var tipoDato          = ["Integer","String","Integer","String","Integer","Integer"];
	var nombreAtributo    = ["trn","operation","product","clientType","origen","cliente"];
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
 * @author gcondo
 * @since Nov 10, 2014
 * @version 1.0.0
 */
public class QueryProductsRequest extends BaseRequest {
	private Integer trn;
	private String operation;
	private Integer product;
	private String clientType;
	private Integer origen;
	private Integer cliente;
	private String type;
	private String client1;
	private String client2;
	private String client3;
	private Integer code;
	
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
	 * @return the product
	 */
	public Integer getProduct() {
		return product;
	}
	/**
	 * @param product the product to set
	 */
	public void setProduct(Integer product) {
		this.product = product;
	}
	/**
	 * @return the clientType
	 */
	public String getClientType() {
		return clientType;
	}
	/**
	 * @param clientType the clientType to set
	 */
	public void setClientType(String clientType) {
		this.clientType = clientType;
	}
	/**
	 * @return the origen
	 */
	public Integer getOrigen() {
		return origen;
	}
	/**
	 * @param origen the origen to set
	 */
	public void setOrigen(Integer origen) {
		this.origen = origen;
	}
	/**
	 * @return the cliente
	 */
	public Integer getCliente() {
		return cliente;
	}
	/**
	 * @param cliente the cliente to set
	 */
	public void setCliente(Integer cliente) {
		this.cliente = cliente;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the client1
	 */
	public String getClient1() {
		return client1;
	}
	/**
	 * @param client1 the client1 to set
	 */
	public void setClient1(String client1) {
		this.client1 = client1;
	}
	/**
	 * @return the client2
	 */
	public String getClient2() {
		return client2;
	}
	/**
	 * @param client2 the client2 to set
	 */
	public void setClient2(String client2) {
		this.client2 = client2;
	}
	/**
	 * @return the client3
	 */
	public String getClient3() {
		return client3;
	}
	/**
	 * @param client3 the client3 to set
	 */
	public void setClient3(String client3) {
		this.client3 = client3;
	}
	/**
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(Integer code) {
		this.code = code;
	}
	
	

}
