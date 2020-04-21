package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.SearchOption;


/**
 <!--	Autor: Gisella Yagual
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "CreditLineRequest";
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
 * @since Aug 14, 2015
 * @version 1.0.0
 */
public class ClientEnquiriesRequest  extends BaseRequest{

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ClientEnquiriesRequest [searchOption=" + searchOption
				+ ", numberofRegisters=" + numberofRegisters + ", next=" + next
				+ ", dateFormatId=" + dateFormatId + "]";
	}
	SearchOption searchOption;
	Integer numberofRegisters;
	Integer next;
	Integer id_aux;
	Integer MISClientId;
	/**
	 * @return the mISClientId
	 */
	public Integer getMISClientId() {
		return MISClientId;
	}
	/**
	 * @param mISClientId the mISClientId to set
	 */
	public void setMISClientId(Integer mISClientId) {
		MISClientId = mISClientId;
	}
	/**
	 * @return the id_aux
	 */
	public Integer getId_aux() {
		return id_aux;
	}
	/**
	 * @param id_aux the id_aux to set
	 */
	public void setId_aux(Integer id_aux) {
		this.id_aux = id_aux;
	}
	Integer dateFormatId;
	/**
	 * @return the searchOption
	 */
	public SearchOption getSearchOption() {
		return searchOption;
	}
	/**
	 * @param searchOption the searchOption to set
	 */
	public void setSearchOption(SearchOption searchOption) {
		this.searchOption = searchOption;
	}
	/**
	 * @return the numberofRegisters
	 */
	public Integer getNumberofRegisters() {
		return numberofRegisters;
	}
	/**
	 * @param numberofRegisters the numberofRegisters to set
	 */
	public void setNumberofRegisters(int numberofRegisters) {
		this.numberofRegisters = numberofRegisters;
	}
	/**
	 * @return the next
	 */
	public Integer getNext() {
		return next;
	}
	/**
	 * @param next the next to set
	 */
	public void setNext(int next) {
		this.next = next;
	}
	/**
	 * @return the dateFormatId
	 */
	public Integer getDateFormatId() {
		return dateFormatId;
	}
	/**
	 * @param dateFormatId the dateFormatId to set
	 */
	public void setDateFormatId(int dateFormatId) {
		this.dateFormatId = dateFormatId;
	}
}
