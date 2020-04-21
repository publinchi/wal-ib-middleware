package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ChannelContext;

/**
 <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "BaseRequest";
	var tipoDato          = ["IProcedureRequest", "String", "String", "String","String","int","int"];
	var nombreAtributo    = ["originalRequest", "culture", "codeTransactionalIdentifier","userBv","term","officeCode","role"];
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
 * @author djarrin
 * @since Aug 13, 2014
 * @version 1.0.0
 */
public class BaseRequest extends ChannelContext {
	private IProcedureRequest originalRequest;
	private String culture;
	private String codeTransactionalIdentifier;
	public String term;
    public int officeCode;
    public int role;
    public String userBv;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */

	/**
	 * @return the codeTransactionalIdentifier
	 */
	public String getCodeTransactionalIdentifier() {
		return codeTransactionalIdentifier;
	}

	/**
	 * @param codeTransactionalIdentifier
	 *            the codeTransactionalIdentifier to set
	 */
	public void setCodeTransactionalIdentifier(String codeTransactionalIdentifier) {
		this.codeTransactionalIdentifier = codeTransactionalIdentifier;
	}

	public IProcedureRequest getOriginalRequest() {
		return originalRequest;
	}

	public void setOriginalRequest(IProcedureRequest originalRequest) {
		this.originalRequest = originalRequest;
	}

	/**
	 * @return the culture
	 */
	public String getCulture() {
		return culture;
	}

	/**
	 * @param culture
	 *            the culture to set
	 */
	public void setCulture(String culture) {
		this.culture = culture;
	}

    
    /**
    * @return the user
    */
    public String getUserBv() {
        return userBv;
    }

    /**
    * @param user the user to set
    */
    public void setUserBv(String user) {
        this.userBv = user;
    }

    /**
    * @return the term
    */
    public String getTerminal() {
    	return term;
    }

    /**
    * @param term the term to set
    */
    public void setTerminal(String term) {
        this.term = term;
    }

    /**
    * @return the office
    */
    public int getOfficeCode() {
        return officeCode;
    }

    /**
    * @param office the office to set
    */
    public void setOfficeCode(int office) {
        this.officeCode = office;
    }

    /**
    * @return the role
    */
    public int getRole() {
        return role;
    }

    /**
    * @param role the role to set
    */
    public void setRole(int role) {
        this.role = role;
    }

}
