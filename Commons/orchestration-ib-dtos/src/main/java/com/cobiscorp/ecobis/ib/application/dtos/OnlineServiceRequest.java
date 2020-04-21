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
	var nombreClase       = "OnlineServiceRequest";
	var tipoDato          = ["String","Integer","Integer","String","String","String"];
	var nombreAtributo    = ["name","office","contractId","documentType","thirdPartyServiceKey","documentId"];
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
 * @since Feb 12, 2015
 * @version 1.0.0
 */
public class OnlineServiceRequest extends BaseRequest{
	private String name = null;
	private Integer office = null;
	private Integer contractId = null;
	private String documentType = null;
	private String thirdPartyServiceKey = null;
	private String documentId = null;
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
	 * @return the office
	 */
	public Integer getOffice() {
		return office;
	}
	/**
	 * @param office the office to set
	 */
	public void setOffice(Integer office) {
		this.office = office;
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
	 * @return the documentType
	 */
	public String getDocumentType() {
		return documentType;
	}
	/**
	 * @param documentType the documentType to set
	 */
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	/**
	 * @return the thirdPartyServiceKey
	 */
	public String getThirdPartyServiceKey() {
		return thirdPartyServiceKey;
	}
	/**
	 * @param thirdPartyServiceKey the thirdPartyServiceKey to set
	 */
	public void setThirdPartyServiceKey(String thirdPartyServiceKey) {
		this.thirdPartyServiceKey = thirdPartyServiceKey;
	}
	/**
	 * @return the documentId
	 */
	public String getDocumentId() {
		return documentId;
	}
	/**
	 * @param documentId the documentId to set
	 */
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "OnlineServiceRequest [name=" + name + ", office=" + office
				+ ", contractId=" + contractId + ", documentType="
				+ documentType + ", thirdPartyServiceKey="
				+ thirdPartyServiceKey + ", documentId=" + documentId + "]";
	}
	

}
