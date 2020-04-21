/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

/**
 <!--	Autor: Baque H Jorge
   		nombreClase	    : Se coloca el nombre de la clase java
		tipoDato	    : Es un arreglo de tipo de datos ["String", "List", "int",...]
        nombreAtributo  : Es un arreglo que contiene los nombre de atributos 
        				["altura", "edad", "peso"]
        descripcionClase: Lleva una breve descripción de la clase
        numeroAtributos : Numero total de atributos de [1,...n]-->

<script type="text/javascript">
	var nombreClase       = "EntityRequest";
	var tipoDato          = ["Integer","String","Integer","Integer","String","Integer","String","String",
							"String","String","String","String","Integer","String","String","String",
							"String","String","String" ];
	var nombreAtributo    = ["trn","subtipo","tipo","modo","valor","ente","nombre","s_nombre","p_apellido",
							"s_apellido","c_apellido","ced_ruc","oficina","nombre_completo","pasaporte",
							"es_cliente","status_ente","departamento","formato_fecha"];
	var descripcionClase  = "DTO de Aplicaci&oacute;n";
	var numeroAtributos   = 19;
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
 * @author mvelez
 * @since Nov 18, 2014
 * @version 1.0.0
 */
public class EntityRequest extends BaseRequest {
	
	private Integer trn;
	private String  subtipo;              /* Subtipo de cliente: natural (P), juridica (C), cifrada (I)*/
    private Integer tipo;                 /* Tipo de consulta*/
    private Integer modo;                 /* Modo de busqueda*/
    private String  valor;                /* Criterio de Busqueda*/
    private Integer ente;                 /* Codigo secuencial del cliente*/
    private String  nombre;               /* Primer nombre del cliente*/
    private String  s_nombre;             /* Segundo nombre del cliente*/
    private String  p_apellido;           /* Primer apellido del cliente*/
    private String  s_apellido;           /* Segundo apellido del cliente*/
    private String  c_apellido;           /* Apellido de casada del cliente, si fuera el caso*/
    private String  ced_ruc;              /* Numero de identificacion del cliente*/
    private Integer oficina;              /* Codigo de la oficina*/
    private String  nombre_completo;      /* Nombre completo del cliente*/
    private String  pasaporte;            /* Numero de pasaporte*/
    private String  es_cliente;           /* Filtro para clientes y prospectos --AAV*/
    private String  status_ente;          /* Busqueda por estado del cliente*/
    private String  departamento;          
    private String  formato_fecha;         

	/**
	 * @return the trn
	 */
	public Integer getTrn() {
		return trn;
	}
	/**
	 * @param trn the trn to set
	 */
	public void setTrn(Integer trn) {
		this.trn = trn;
	}
	/**
	 * @return the subtipo
	 */
	public String getSubtipo() {
		return subtipo;
	}
	/**
	 * @param subtipo the subtipo to set
	 */
	public void setSubtipo(String subtipo) {
		this.subtipo = subtipo;
	}
	/**
	 * @return the tipo
	 */
	public Integer getTipo() {
		return tipo;
	}
	/**
	 * @param tipo the tipo to set
	 */
	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}
	/**
	 * @return the modo
	 */
	public Integer getModo() {
		return modo;
	}
	/**
	 * @param modo the modo to set
	 */
	public void setModo(Integer modo) {
		this.modo = modo;
	}
	/**
	 * @return the valor
	 */
	public String getValor() {
		return valor;
	}
	/**
	 * @param valor the valor to set
	 */
	public void setValor(String valor) {
		this.valor = valor;
	}
	/**
	 * @return the ente
	 */
	public Integer getEnte() {
		return ente;
	}
	/**
	 * @param ente the ente to set
	 */
	public void setEnte(Integer ente) {
		this.ente = ente;
	}
	/**
	 * @return the nombre
	 */
	public String getNombre() {
		return nombre;
	}
	/**
	 * @param nombre the nombre to set
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	/**
	 * @return the s_nombre
	 */
	public String getS_nombre() {
		return s_nombre;
	}
	/**
	 * @param s_nombre the s_nombre to set
	 */
	public void setS_nombre(String s_nombre) {
		this.s_nombre = s_nombre;
	}
	/**
	 * @return the p_apellido
	 */
	public String getP_apellido() {
		return p_apellido;
	}
	/**
	 * @param p_apellido the p_apellido to set
	 */
	public void setP_apellido(String p_apellido) {
		this.p_apellido = p_apellido;
	}
	/**
	 * @return the s_apellido
	 */
	public String getS_apellido() {
		return s_apellido;
	}
	/**
	 * @param s_apellido the s_apellido to set
	 */
	public void setS_apellido(String s_apellido) {
		this.s_apellido = s_apellido;
	}
	/**
	 * @return the c_apellido
	 */
	public String getC_apellido() {
		return c_apellido;
	}
	/**
	 * @param c_apellido the c_apellido to set
	 */
	public void setC_apellido(String c_apellido) {
		this.c_apellido = c_apellido;
	}
	/**
	 * @return the ced_ruc
	 */
	public String getCed_ruc() {
		return ced_ruc;
	}
	/**
	 * @param ced_ruc the ced_ruc to set
	 */
	public void setCed_ruc(String ced_ruc) {
		this.ced_ruc = ced_ruc;
	}
	/**
	 * @return the oficina
	 */
	public Integer getOficina() {
		return oficina;
	}
	/**
	 * @param oficina the oficina to set
	 */
	public void setOficina(Integer oficina) {
		this.oficina = oficina;
	}
	/**
	 * @return the nombre_completo
	 */
	public String getNombre_completo() {
		return nombre_completo;
	}
	/**
	 * @param nombre_completo the nombre_completo to set
	 */
	public void setNombre_completo(String nombre_completo) {
		this.nombre_completo = nombre_completo;
	}
	/**
	 * @return the pasaporte
	 */
	public String getPasaporte() {
		return pasaporte;
	}
	/**
	 * @param pasaporte the pasaporte to set
	 */
	public void setPasaporte(String pasaporte) {
		this.pasaporte = pasaporte;
	}
	/**
	 * @return the es_cliente
	 */
	public String getEs_cliente() {
		return es_cliente;
	}
	/**
	 * @param es_cliente the es_cliente to set
	 */
	public void setEs_cliente(String es_cliente) {
		this.es_cliente = es_cliente;
	}
	/**
	 * @return the status_ente
	 */
	public String getStatus_ente() {
		return status_ente;
	}
	/**
	 * @param status_ente the status_ente to set
	 */
	public void setStatus_ente(String status_ente) {
		this.status_ente = status_ente;
	}
	/**
	 * @return the departamento
	 */
	public String getDepartamento() {
		return departamento;
	}
	/**
	 * @param subtipo the departamento to set
	 */
	public void setDepartamento(String departamento) {
		this.departamento = departamento;		
	}
	/**
	 * @return the formato_fecha
	 */
	public String getFormato_fecha() {
		return formato_fecha;
	}
	/**
	 * @param formato_fecha the formato_fecha to set
	 */
	public void setFormato_fecha(String formato_fecha) {
		this.formato_fecha = formato_fecha;
	}
}
