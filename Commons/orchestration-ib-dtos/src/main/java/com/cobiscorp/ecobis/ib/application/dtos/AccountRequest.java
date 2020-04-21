/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.List;


/**
 <!--	Autor: Isaac Torres
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "ModuleRequest";
	var tipoDato          = ["String"];
	var nombreAtributo    = ["idOperation"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
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
  		}</script></td>
  </tr>
  
  <tr>
    <td>Descripci&oacute;n:</td>
    <td><script type="text/javascript">document.writeln(descripcionClase);</script></td>
  </tr>
</tbody></table>
 * @author dmorla
 * @since Jun 05, 2015
 * @version 1.0.0
 */
public class AccountRequest extends BaseRequest{
	
	private String idOperation;
	private Integer codModule;
	private Integer codCriteria;
	private String code0;
	private String code1;
	private String code2;
	private String code3;
	private String code4;
	private String code5;
	private String code6;
	private String code7;
	private String code8;
	private String code9;
	

	/**
	 * @return the idOperation
	 */
	public String getIdOperation() {
		return idOperation;
	}

	/**
	 * @param idOperation the idOperation to set
	 */
	public void setIdOperation(String idOperation) {
		this.idOperation = idOperation;
	}

	/**
	 * @return the codModule
	 */
	public Integer getCodModule() {
		return codModule;
	}

	/**
	 * @param codModule the codModule to set
	 */
	public void setCodModule(Integer codModule) {
		this.codModule = codModule;
	}

	/**
	 * @return the codCriteria
	 */
	public Integer getCodCriteria() {
		return codCriteria;
	}

	/**
	 * @param codCriteria the codCriteria to set
	 */
	public void setCodCriteria(Integer codCriteria) {
		this.codCriteria = codCriteria;
	}
	
	/**
	 * @return the code0
	 */
	public String getCode0() {
		return code0;
	}

	/**
	 * @param code the code0 to set
	 */
	public void setCode0(String code0) {
		this.code0 = code0;
	}

	/**
	 * @return the code1
	 */
	public String getCode1() {
		return code1;
	}

	/**
	 * @param code1 the code1 to set
	 */
	public void setCode1(String code1) {
		this.code1 = code1;
	}

	/**
	 * @return the code2
	 */
	public String getCode2() {
		return code2;
	}

	/**
	 * @param code2 the code2 to set
	 */
	public void setCode2(String code2) {
		this.code2 = code2;
	}

	/**
	 * @return the code3
	 */
	public String getCode3() {
		return code3;
	}

	/**
	 * @param code3 the code3 to set
	 */
	public void setCode3(String code3) {
		this.code3 = code3;
	}

	/**
	 * @return the code4
	 */
	public String getCode4() {
		return code4;
	}

	/**
	 * @param code4 the code4 to set
	 */
	public void setCode4(String code4) {
		this.code4 = code4;
	}

	/**
	 * @return the code5
	 */
	public String getCode5() {
		return code5;
	}

	/**
	 * @param code5 the code5 to set
	 */
	public void setCode5(String code5) {
		this.code5 = code5;
	}

	/**
	 * @return the code6
	 */
	public String getCode6() {
		return code6;
	}

	/**
	 * @param code6 the code6 to set
	 */
	public void setCode6(String code6) {
		this.code6 = code6;
	}

	/**
	 * @return the code7
	 */
	public String getCode7() {
		return code7;
	}

	/**
	 * @param code7 the code7 to set
	 */
	public void setCode7(String code7) {
		this.code7 = code7;
	}

	/**
	 * @return the code8
	 */
	public String getCode8() {
		return code8;
	}

	/**
	 * @param code8 the code8 to set
	 */
	public void setCode8(String code8) {
		this.code8 = code8;
	}

	/**
	 * @return the code9
	 */
	public String getCode9() {
		return code9;
	}

	/**
	 * @param code9 the code9 to set
	 */
	public void setCode9(String code9) {
		this.code9 = code9;
	}

	
}
