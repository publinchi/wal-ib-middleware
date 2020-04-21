/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.List;

import com.cobiscorp.ecobis.ib.orchestration.dtos.AchAccountFormat;

/**
   <!--	Autor: Baque H Jorge
 
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->
        
	<script type="text/javascript">
		var nombreClase       = "AchAccountFormatRespon";
		var tipoDato          = ["List<AchAccountFormat>"];
		var nombreAtributo    = ["achAccountFormatCollection"];
		var descripcionClase  = "Respuesta de formatos de cuenta";
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
			  		}</script>
			  	</td>
				<td style=" font-family:'Courier New', Courier, monospace;color:#00F"><script type="text/javascript">
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
	</table>
        
 **/

/**
 * @author kmeza
 * @since Jan 16, 2015
 * @version 1.0.0
 */
public class AchAccountFormatRespon extends BaseResponse{

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	

	List<AchAccountFormat>achAccountFormatCollection;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AchAccountFormatRespon [achAccountFormatCollection="
				+ achAccountFormatCollection + "]";
	}

	/**
	 * @return the achAccountFormatCollection
	 */
	public List<AchAccountFormat> getAchAccountFormatCollection() {
		return achAccountFormatCollection;
	}

	/**
	 * @param achAccountFormatCollection the achAccountFormatCollection to set
	 */
	public void setAchAccountFormatCollection(
			List<AchAccountFormat> achAccountFormatCollection) {
		this.achAccountFormatCollection = achAccountFormatCollection;
	}

	
	
	

}
