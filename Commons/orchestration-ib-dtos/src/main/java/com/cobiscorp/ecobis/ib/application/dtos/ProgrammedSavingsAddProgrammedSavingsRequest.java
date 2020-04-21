/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ProgrammedSavings;
import com.cobiscorp.ecobis.ib.orchestration.dtos.User;

/**
     <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "ProgrammedSavingsAddProgrammedSavingsRequest";
	var tipoDato          = ["User","ProgrammedSavings","Product","Product"];
	var nombreAtributo    = ["user","programmedSavings","product1","product2"];
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
 * @author jbaque
 * @since 12/11/2014
 * @version 1.0.0
 */
public class ProgrammedSavingsAddProgrammedSavingsRequest extends BaseRequest {
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProgrammedSavingsAddProgrammedSavingsRequest [user=" + user
				+ ", programmedSavings=" + programmedSavings + ", product1="
				+ product1 + ", product2=" + product2 + "]";
	}
	private User user;
	private ProgrammedSavings programmedSavings;
	private Product product1;
	private Product product2;
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
	/**
	 * @return the programmedSavings
	 */
	public ProgrammedSavings getProgrammedSavings() {
		return programmedSavings;
	}
	/**
	 * @param programmedSavings the programmedSavings to set
	 */
	public void setProgrammedSavings(ProgrammedSavings programmedSavings) {
		this.programmedSavings = programmedSavings;
	}
	/**
	 * @return the product1
	 */
	public Product getProduct1() {
		return product1;
	}
	/**
	 * @param product1 the product1 to set
	 */
	public void setProduct1(Product product1) {
		this.product1 = product1;
	}
	/**
	 * @return the product2
	 */
	public Product getProduct2() {
		return product2;
	}
	/**
	 * @param product2 the product2 to set
	 */
	public void setProduct2(Product product2) {
		this.product2 = product2;
	}
	
}
