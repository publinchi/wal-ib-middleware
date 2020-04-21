/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.List;

import com.cobiscorp.ecobis.ib.orchestration.dtos.SimuladorLoanItem;
import com.cobiscorp.ecobis.ib.orchestration.dtos.SimulationLoan;

/**
<!--   Autor: Baque H Jorge
       nombreClase     : Se coloca el nombre de la clase java
       tipoDato        : Es un arreglo de tipo de datos ["String", "List", "int",...]
       nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
                                                                ["altura", "edad", "peso"]
       descripcionClase: Lleva una breve descripción de la clase
       numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
                var nombreClase       = "SimulationLoanResponse";
                var tipoDato          = ["List<SimulationLoan>","List<SimuladorLoanItem>"];
                var nombreAtributo    = ["simulationLoan","simuladorLoanItem"];
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
 * @author areinoso
 * @since Dec 18, 2014
 * @version 1.0.0
 */
public class SimulationLoanResponse extends BaseResponse {
	
	private List<SimulationLoan> simulationLoan;
	private List<SimuladorLoanItem> simuladorLoanItem;
	/**
	 * @return the simulationLoan
	 */
	public List<SimulationLoan> getSimulationLoan() {
		return simulationLoan;
	}
	/**
	 * @param simulationLoan the simulationLoan to set
	 */
	public void setSimulationLoan(List<SimulationLoan> simulationLoan) {
		this.simulationLoan = simulationLoan;
	}
	/**
	 * @return the simuladorLoanItem
	 */
	public List<SimuladorLoanItem> getSimuladorLoanItem() {
		return simuladorLoanItem;
	}
	/**
	 * @param simuladorLoanItem the simuladorLoanItem to set
	 */
	public void setSimuladorLoanItem(List<SimuladorLoanItem> simuladorLoanItem) {
		this.simuladorLoanItem = simuladorLoanItem;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SimulationLoanResponse [simulationLoan=" + simulationLoan
				+ ", simuladorLoanItem=" + simuladorLoanItem + "]";
	}
	
	
	
		
}
