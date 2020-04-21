package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;

/**
     <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "ReExecutionRequest";
	var tipoDato          = ["String","String","String","String","String","String","TransferRequest","Product","Product","Client","String","IProcedureRequest"];
	var nombreAtributo    = ["priority","ssnBranch","ssnCentral","trn","srv","in_line","transferRequest","OriginProduct","DestinationProduct","cliente","rty","originalRequest"];
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
 * @author dguerra
 * @since Aug 21, 2014
 * @version 1.0.0
 */

public class ReExecutionRequest extends BaseRequest {

	/**
	 * Indicates the priority of the transaction
	 */
	private String priority;
	/*
	 * Indicates the identifier process in the branch
	 */
	private String ssnBranch;
	/*
	 * Indicates the identifier process in the Central
	 */
	private String ssnCentral;
	/*
	 * Indicates the identifier of the transaction
	 */
	private String trn;

	/*
	 * Indicates the identifier of the srv
	 */
	private String srv;

	/*
	 * Indicates if is in line
	 */
	private String in_line;

	/*
	 * Object contain information about transfer
	 */
	private TransferRequest transferRequest;

	/*
	 * object contain informaction about origin Product
	 */

	private Product OriginProduct;
	/*
	 * object contain informaction about destination Product
	 */
	private Product DestinationProduct;

	/*
	 * object contain information about cliente
	 */
	private Client cliente;

	/*
	 * object contain information about reentry
	 */
	private String rty;

	/**
	 * Procedure Request
	 */
	private IProcedureRequest originalRequest;

	public IProcedureRequest getOriginalRequest() {
		return originalRequest;
	}

	public void setOriginalRequest(IProcedureRequest originalRequest) {
		this.originalRequest = originalRequest;
	}

	/**
	 * name of service
	 * 
	 * @return
	 */
	private String serviceName;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getRty() {
		return rty;
	}

	public void setRty(String rty) {
		this.rty = rty;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getSsnBranch() {
		return ssnBranch;
	}

	public void setSsnBranch(String ssnBranch) {
		this.ssnBranch = ssnBranch;
	}

	public String getSsnCentral() {
		return ssnCentral;
	}

	public void setSsnCentral(String ssnCentral) {
		this.ssnCentral = ssnCentral;
	}

	public String getTrn() {
		return trn;
	}

	public void setTrn(String trn) {
		this.trn = trn;
	}

	public String getSrv() {
		return srv;
	}

	public void setSrv(String srv) {
		this.srv = srv;
	}

	public String getIn_line() {
		return in_line;
	}

	public void setIn_line(String in_line) {
		this.in_line = in_line;
	}

	public TransferRequest getTransferRequest() {
		return transferRequest;
	}

	public void setTransferRequest(TransferRequest transferRequest) {
		this.transferRequest = transferRequest;
	}

	public Product getOriginProduct() {
		return OriginProduct;
	}

	public void setOriginProduct(Product originProduct) {
		OriginProduct = originProduct;
	}

	public Product getDestinationProduct() {
		return DestinationProduct;
	}

	public void setDestinationProduct(Product destinationProduct) {
		DestinationProduct = destinationProduct;
	}

	public Client getCliente() {
		return cliente;
	}

	public void setCliente(Client cliente) {
		this.cliente = cliente;
	}

	@Override
	public String toString() {
		return "ReExecutionRequest [priority=" + priority + ", ssnBranch=" + ssnBranch + ", ssnCentral=" + ssnCentral + ", trn=" + trn + ", srv=" + srv + ", in_line=" + in_line + "]";
	}

}
