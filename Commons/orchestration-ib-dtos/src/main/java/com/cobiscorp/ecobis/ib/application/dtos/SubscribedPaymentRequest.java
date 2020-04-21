package com.cobiscorp.ecobis.ib.application.dtos;

/**
 <!--	Autor: Gisella Yagual
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "SubscribedPaymentRequest";
	var tipoDato          = ["String", "Entity"];
	var nombreAtributo    = ["origin", "entity"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 2;
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

 * @author gyagual
 * @since Aug 28, 2015
 * @version 1.0.0
 */
public class SubscribedPaymentRequest  extends BaseRequest{

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	 
	private Integer contractId;
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
	 * @return the numDoc
	 */
	public String getNumDoc() {
		return numDoc;
	}
	/**
	 * @param numDoc the numDoc to set
	 */
	public void setNumDoc(String numDoc) {
		this.numDoc = numDoc;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the entity
	 */
	public Integer getEntity() {
		return entity;
	}
	/**
	 * @param entity the entity to set
	 */
	public void setEntity(Integer entity) {
		this.entity = entity;
	}
	/**
	 * @return the entityBV
	 */
	public Integer getEntityBV() {
		return entityBV;
	}
	/**
	 * @param entityBV the entityBV to set
	 */
	public void setEntityBV(Integer entityBV) {
		this.entityBV = entityBV;
	}
	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}
	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
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
	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}
	/**
	 * @return the typeDoc
	 */
	public String getTypeDoc() {
		return typeDoc;
	}
	/**
	 * @param typeDoc the typeDoc to set
	 */
	public void setTypeDoc(String typeDoc) {
		this.typeDoc = typeDoc;
	}
	/**
	 * @return the ref1
	 */
	public String getRef1() {
		return ref1;
	}
	/**
	 * @param ref1 the ref1 to set
	 */
	public void setRef1(String ref1) {
		this.ref1 = ref1;
	}
	/**
	 * @return the ref2
	 */
	public String getRef2() {
		return ref2;
	}
	/**
	 * @param ref2 the ref2 to set
	 */
	public void setRef2(String ref2) {
		this.ref2 = ref2;
	}
	/**
	 * @return the ref3
	 */
	public String getRef3() {
		return ref3;
	}
	/**
	 * @param ref3 the ref3 to set
	 */
	public void setRef3(String ref3) {
		this.ref3 = ref3;
	}
	/**
	 * @return the ref4
	 */
	public String getRef4() {
		return ref4;
	}
	/**
	 * @param ref4 the ref4 to set
	 */
	public void setRef4(String ref4) {
		this.ref4 = ref4;
	}
	/**
	 * @return the ref5
	 */
	public String getRef5() {
		return ref5;
	}
	/**
	 * @param ref5 the ref5 to set
	 */
	public void setRef5(String ref5) {
		this.ref5 = ref5;
	}
	/**
	 * @return the ref6
	 */
	public String getRef6() {
		return ref6;
	}
	/**
	 * @param ref6 the ref6 to set
	 */
	public void setRef6(String ref6) {
		this.ref6 = ref6;
	}
	/**
	 * @return the ref7
	 */
	public String getRef7() {
		return ref7;
	}
	/**
	 * @param ref7 the ref7 to set
	 */
	public void setRef7(String ref7) {
		this.ref7 = ref7;
	}
	/**
	 * @return the ref8
	 */
	public String getRef8() {
		return ref8;
	}
	/**
	 * @param ref8 the ref8 to set
	 */
	public void setRef8(String ref8) {
		this.ref8 = ref8;
	}
	/**
	 * @return the ref9
	 */
	public String getRef9() {
		return ref9;
	}
	/**
	 * @param ref9 the ref9 to set
	 */
	public void setRef9(String ref9) {
		this.ref9 = ref9;
	}
	/**
	 * @return the ref10
	 */
	public String getRef10() {
		return ref10;
	}
	/**
	 * @param ref10 the ref10 to set
	 */
	public void setRef10(String ref10) {
		this.ref10 = ref10;
	}
	/**
	 * @return the ref11
	 */
	public String getRef11() {
		return ref11;
	}
	/**
	 * @param ref11 the ref11 to set
	 */
	public void setRef11(String ref11) {
		this.ref11 = ref11;
	}
	/**
	 * @return the ref12
	 */
	public String getRef12() {
		return ref12;
	}
	/**
	 * @param ref12 the ref12 to set
	 */
	public void setRef12(String ref12) {
		this.ref12 = ref12;
	}
	private String categoryId;
	/**
	 * @return the categoryId
	 */
	public String getCategoryId() {
		return categoryId;
	}
	/**
	 * @param categoryId the categoryId to set
	 */
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	private String numDoc;
	private String description;
	private Integer entity;
	private Integer entityBV;
	private String login;
	private String documentId;
	private String key;
	private String typeDoc;
	private String ref1;
	private String ref2;
	private String ref3;
	private String ref4;
	private String ref5;
	private String ref6;
	private String ref7;
	private String ref8;
	private String ref9;
	private String ref10;
	private String ref11;
	private String ref12;
	private Integer sequential;
	private String operation;
	/**
	 * @return the sequential
	 */
	public Integer getSequential() {
		return sequential;
	}
	/**
	 * @param sequential the sequential to set
	 */
	public void setSequential(Integer sequential) {
		this.sequential = sequential;
	}
	private String interfaceType;
	/**
	 * @return the interfaceType
	 */
	public String getInterfaceType() {
		return interfaceType;
	}
	/**
	 * @param interfaceType the interfaceType to set
	 */
	public void setInterfaceType(String interfaceType) {
		this.interfaceType = interfaceType;
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
	
	
 
}
