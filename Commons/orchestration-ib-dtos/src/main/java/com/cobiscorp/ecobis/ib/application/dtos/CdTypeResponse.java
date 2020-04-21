/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.List;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Category;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Parameters;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Type;

/**
 <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "CdTypeResponse";
	var tipoDato          = ["List < Type >", "List< Parameters >" , "List < Category >" , "CertificateDepositResponse"];
	var nombreAtributo    = ["listCdType", "listParameters", "listCategory", "certificateDepositResponse"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 4;
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
 * @author jveloz
 * @since Nov 6, 2014
 * @version 1.0.0
 */
public class CdTypeResponse extends BaseResponse{
	
	private List<Type> listCdType;
	private List<Parameters> listParameters;
	private List<Category> listCategory;
	private CertificateDepositResponse certificateDepositResponse;
	/**
	 * @return the listCdType
	 */
	public List<Type> getListCdType() {
		return listCdType;
	}
	/**
	 * @param listCdType the listCdType to set
	 */
	public void setListCdType(List<Type> listCdType) {
		this.listCdType = listCdType;
	}
	/**
	 * @return the listParameters
	 */
	public List<Parameters> getListParameters() {
		return listParameters;
	}
	/**
	 * @param listParameters the listParameters to set
	 */
	public void setListParameters(List<Parameters> listParameters) {
		this.listParameters = listParameters;
	}
	/**
	 * @return the listCategory
	 */
	public List<Category> getListCategory() {
		return listCategory;
	}
	/**
	 * @param listCategory the listCategory to set
	 */
	public void setListCategory(List<Category> listCategory) {
		this.listCategory = listCategory;
	}
	/**
	 * @return the certificateDepositResponse
	 */
	public CertificateDepositResponse getCertificateDepositResponse() {
		return certificateDepositResponse;
	}
	/**
	 * @param certificateDepositResponse the certificateDepositResponse to set
	 */
	public void setCertificateDepositResponse(
			CertificateDepositResponse certificateDepositResponse) {
		this.certificateDepositResponse = certificateDepositResponse;
	}
	
}
