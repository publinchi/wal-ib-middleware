/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * 
 * <script type="text/javascript">
	var nombreClase       = "BatchRefreshDocumentType";
	var tipoDato          = ["int","String","String","String","String","String","String","String","String","String","String"];
	var nombreAtributo    = ["sequential","code", "description","mask","operationType","province","fastOpening","blocks","nationality","digit","state"];
	var descripcionClase  = "DTO de Orquestaci&oacute;n";
	var numeroAtributos   = 1;
</script>

<table>
  <table><tbody>
  <tr>
     <th Alignment="center" bgcolor="#CCCCFF">Nombre Clase: 
	    <script type="text/javascript">document.writeln(nombreClase);</script> 
     </th>
  </tr>
  <tr>
      <td Alignment="center" bgcolor="#CCCCFF">Tipo Dato</td>
      <td Alignment="center" bgcolor="#CCCCFF">Nombre Atributo</td>
  </tr>
  <tr>
      <td style="font-family:'Courier New', Courier, monospace; color:#906;"><script type="text/javascript">
  		for(i=0;i<numeroAtributos;i++){ 
  		document.write(tipoDato[i]);
		document.write("<br/>");
  		}</script></td>
  <td style=" font-family:'Courier New', Courier, monospace;color:#00F"><script type="text/javascript">
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
</table>
 * 
 * 
 * @author nvite
 * @since 24/03/2015
 * @version 1.0.0
 */

/**
 	td_secuencial      int     
	td_codigo          char    
	td_descripcion     varchar 
	td_mascara         varchar 
	td_tipoper         char    
	td_provincia       char    
	td_aperrapida      char    
	td_bloquea         char    
	td_nacionalidad    varchar 
	td_digito          char    
	td_estado          char    

**/
public class BatchRefreshDocumentType {
	private int sequential; //Secuencial
	private String code; //Codigo
	private String description; //Descripcion
	private String mask; //Máscara
	private String personType; //Tipo Persona
	private String province; //Provincia
	private String fastOpening; //Apertura Rapida
	private String blocks; //Bloquea
	private String nationality; //Nacionalidad
	private String digit; //Digito
	private String state; //Estado
	/**
	 * @return the sequential
	 */
	public int getSequential() {
		return sequential;
	}
	/**
	 * @param sequential the sequential to set
	 */
	public void setSequential(int sequential) {
		this.sequential = sequential;
	}
	/**
	 * @return the code
	 */
	public String getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
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
	 * @return the mask
	 */
	public String getMask() {
		return mask;
	}
	/**
	 * @param mask the mask to set
	 */
	public void setMask(String mask) {
		this.mask = mask;
	}
	/**
	 * @return the operationType
	 */
	public String getPersonType() {
		return personType;
	}
	/**
	 * @param operationType the operationType to set
	 */
	public void setPersonType(String personType) {
		this.personType = personType;
	}
	/**
	 * @return the province
	 */
	public String getProvince() {
		return province;
	}
	/**
	 * @param province the province to set
	 */
	public void setProvince(String province) {
		this.province = province;
	}
	/**
	 * @return the fastOpening
	 */
	public String getFastOpening() {
		return fastOpening;
	}
	/**
	 * @param fastOpening the fastOpening to set
	 */
	public void setFastOpening(String fastOpening) {
		this.fastOpening = fastOpening;
	}
	/**
	 * @return the blocks
	 */
	public String getBlocks() {
		return blocks;
	}
	/**
	 * @param blocks the blocks to set
	 */
	public void setBlocks(String blocks) {
		this.blocks = blocks;
	}
	/**
	 * @return the nationality
	 */
	public String getNationality() {
		return nationality;
	}
	/**
	 * @param nationality the nationality to set
	 */
	public void setNationality(String nationality) {
		this.nationality = nationality;
	}
	/**
	 * @return the digit
	 */
	public String getDigit() {
		return digit;
	}
	/**
	 * @param digit the digit to set
	 */
	public void setDigit(String digit) {
		this.digit = digit;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BatchRefreshDocumentType [sequential=" + sequential + ", code="
				+ code + ", description=" + description + ", mask=" + mask
				+ ", operationType=" + personType + ", province=" + province
				+ ", fastOpening=" + fastOpening + ", blocks=" + blocks
				+ ", nationality=" + nationality + ", digit=" + digit
				+ ", state=" + state + "]";
	}
	
	
}
