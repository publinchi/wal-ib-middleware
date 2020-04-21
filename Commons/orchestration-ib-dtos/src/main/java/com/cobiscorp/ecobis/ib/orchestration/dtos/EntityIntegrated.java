/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author mvelez
 * @since Nov 21, 2014
 * @version 1.0.0
 */
public class EntityIntegrated {	
	   //datos genericos
	private Integer ente;                                             //1
	private String  nombre_completo_DE;                               //2
	private String  subtipo;                                          //3
	private String  ced_ruc;                                          //4
	private String  tipo_ced;                                         //5
	private Integer filial;                                           //6
	private String  filial_desc;                                      //7
	private Integer oficina;                                          //8
	private String  oficina_desc;                                     //9
	private String  fecha_crea;                                       //10
	private String  fecha_mod;                                        //11
	private Integer direccion;                                        //12
	private Integer referencia;                                       //13
	private Integer casilla;                                          //14
	private String  casilla_def;                                      //15
	private String  tipo_dp;                                          //16
	private Integer balance;                                          //17 
	private Integer grupo;                                            //18
	private String  grupo_desc;                                       //19
	private Integer pais;                                             //20
	private String  pais_desc;                                        //21
	private String  nacionalidad;                                     //22
	private Integer oficial;                                          //23
	private String  oficial_desc;                                     //24
	private String  actividad;                                        //25
	private String  actividad_desc;                                   //26
	private String  retencion;                                        //27
	private String  mala_referencia;                                  //28
	private String  comentario;                                       //29
	private Integer cont_malas;                                       //30
	private String  nomlar;                                           //31
	private String  vinculacion;                                      //32
	private String  tipo_vinculacion;                                 //33
	private String  tipo_vinc_desc;                                   //34

	   //--datos de companias
	private String  posicion;                                         //35
	private String  posicion_desc;                                    //36
	private String  tipo_compania;                                    //37
	private String  tipo_compania_desc;                               //38
	private Integer rep_legal;                                        //39
	private String  rep_legal_desc;                                   //40
	private Double  activo;                                           //41
	private Double  pasivo;                                           //42
	private String  es_grupo;                                         //43
	private Double  capital_social;                                   //44
	private Double  reserva_legal;                                    //45
	private String  fecha_const;                                       //46
	private String  nombre_completo;                                   //47
	private Integer plazo;                                             //48
	private Integer direccion_domicilio;                               //49
	private String  fecha_inscrp;                                      //50
	private String  fecha_aum_capital;                                 //51
	private Integer rep_jud;                                           //52
	private String  rep_jud_desc;                                      //53
	private Integer rep_ex_jud;                                        //54
	private String  rep_ex_jud_desc;                                   //55
	private Integer notaria;                                           //56
	private Double  capital_inicial;                                   //57
	   
	   //--datos de personas
	private String  p_apellido;                                        //58
	private String  s_apellido;                                        //59
	private String  sexo;                                              //60
	private String  sexo_desc;                                         //61
	private String  fecha_nac;                                         //62
	private String  profesion;                                         //63
	private String  profesion_desc;                                    //64
	private String  pasaporte;                                         //65
	private String  estado_civil;                                      //66
	private String  estado_civil_desc;                                 //67
	private Integer num_cargas;                                        //68
	private Double  nivel_ing;                                         //69
	private Double  nivel_egr;                                         //70
	private String  tipo_persona;                                      //71
	private String  tipo_persona_desc;                                 //72
	private Integer personal;                                          //73
	private Integer propiedad;                                         //74
	private Integer trabajo;                                           //75
	private Integer soc_hecho;                                         //76
	private String  fecha_ingreso;                                     //77
	private String  fecha_expira;                                      //78

	   //--Nuevos campos
	private String  c_apellido;                                        //79
	private String  s_nombre;                                          //80
	private String  codsuper;                                          //81
	private String  tipspub;                                           //82
	private String  subspub;                                           //83
	private String  codsuper_desc;                                     //84
	private String  tipspub_desc;                                      //85
	private String  subspub_desc;                                      //86
	private String  razon_social;                                      //87 
	private String  nombre;                                            //88 
	private String  estado;                                            //89
	private String  c_actividad;                                       //90
	private String  c_actividad_desc;                                  //91   
	private String  estado_aux;                                        //92 ESTADO DE CL_ENTE_AUX
	
	private String email;
	private String segmento;
	private String lineaNegocio;
	private Integer apoderadoLegal;
	private String cedRucAnt;

	
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
	 * @return the nombre_completo_DE
	 */
	public String getNombre_completo_DE() {
		return nombre_completo_DE;
	}
	/**
	 * @param nombre_completo_DE the nombre_completo_DE to set
	 */
	public void setNombre_completo_DE(String nombre_completo_DE) {
		this.nombre_completo_DE = nombre_completo_DE;
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
	 * @return the tipo_ced
	 */
	public String getTipo_ced() {
		return tipo_ced;
	}
	/**
	 * @param tipo_ced the tipo_ced to set
	 */
	public void setTipo_ced(String tipo_ced) {
		this.tipo_ced = tipo_ced;
	}
	/**
	 * @return the filial
	 */
	public Integer getFilial() {
		return filial;
	}
	/**
	 * @param filial the filial to set
	 */
	public void setFilial(Integer filial) {
		this.filial = filial;
	}
	/**
	 * @return the filial_desc
	 */
	public String getFilial_desc() {
		return filial_desc;
	}
	/**
	 * @param filial_desc the filial_desc to set
	 */
	public void setFilial_desc(String filial_desc) {
		this.filial_desc = filial_desc;
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
	 * @return the oficina_desc
	 */
	public String getOficina_desc() {
		return oficina_desc;
	}
	/**
	 * @param oficina_desc the oficina_desc to set
	 */
	public void setOficina_desc(String oficina_desc) {
		this.oficina_desc = oficina_desc;
	}
	/**
	 * @return the fecha_crea
	 */
	public String getFecha_crea() {
		return fecha_crea;
	}
	/**
	 * @param fecha_crea the fecha_crea to set
	 */
	public void setFecha_crea(String fecha_crea) {
		this.fecha_crea = fecha_crea;
	}
	/**
	 * @return the fecha_mod
	 */
	public String getFecha_mod() {
		return fecha_mod;
	}
	/**
	 * @param fecha_mod the fecha_mod to set
	 */
	public void setFecha_mod(String fecha_mod) {
		this.fecha_mod = fecha_mod;
	}
	/**
	 * @return the direccion
	 */
	public Integer getDireccion() {
		return direccion;
	}
	/**
	 * @param direccion the direccion to set
	 */
	public void setDireccion(Integer direccion) {
		this.direccion = direccion;
	}
	/**
	 * @return the referencia
	 */
	public Integer getReferencia() {
		return referencia;
	}
	/**
	 * @param referencia the referencia to set
	 */
	public void setReferencia(Integer referencia) {
		this.referencia = referencia;
	}
	/**
	 * @return the casilla
	 */
	public Integer getCasilla() {
		return casilla;
	}
	/**
	 * @param casilla the casilla to set
	 */
	public void setCasilla(Integer casilla) {
		this.casilla = casilla;
	}
	/**
	 * @return the casilla_def
	 */
	public String getCasilla_def() {
		return casilla_def;
	}
	/**
	 * @param casilla_def the casilla_def to set
	 */
	public void setCasilla_def(String casilla_def) {
		this.casilla_def = casilla_def;
	}
	/**
	 * @return the tipo_dp
	 */
	public String getTipo_dp() {
		return tipo_dp;
	}
	/**
	 * @param tipo_dp the tipo_dp to set
	 */
	public void setTipo_dp(String tipo_dp) {
		this.tipo_dp = tipo_dp;
	}
	/**
	 * @return the balance
	 */
	public Integer getBalance() {
		return balance;
	}
	/**
	 * @param balance the balance to set
	 */
	public void setBalance(Integer balance) {
		this.balance = balance;
	}
	/**
	 * @return the grupo
	 */
	public Integer getGrupo() {
		return grupo;
	}
	/**
	 * @param grupo the grupo to set
	 */
	public void setGrupo(Integer grupo) {
		this.grupo = grupo;
	}
	/**
	 * @return the grupo_desc
	 */
	public String getGrupo_desc() {
		return grupo_desc;
	}
	/**
	 * @param grupo_desc the grupo_desc to set
	 */
	public void setGrupo_desc(String grupo_desc) {
		this.grupo_desc = grupo_desc;
	}
	/**
	 * @return the pais
	 */
	public Integer getPais() {
		return pais;
	}
	/**
	 * @param pais the pais to set
	 */
	public void setPais(Integer pais) {
		this.pais = pais;
	}
	/**
	 * @return the pais_desc
	 */
	public String getPais_desc() {
		return pais_desc;
	}
	/**
	 * @param pais_desc the pais_desc to set
	 */
	public void setPais_desc(String pais_desc) {
		this.pais_desc = pais_desc;
	}
	/**
	 * @return the nacionalidad
	 */
	public String getNacionalidad() {
		return nacionalidad;
	}
	/**
	 * @param nacionalidad the nacionalidad to set
	 */
	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}
	/**
	 * @return the oficial
	 */
	public Integer getOficial() {
		return oficial;
	}
	/**
	 * @param oficial the oficial to set
	 */
	public void setOficial(Integer oficial) {
		this.oficial = oficial;
	}
	/**
	 * @return the oficial_desc
	 */
	public String getOficial_desc() {
		return oficial_desc;
	}
	/**
	 * @param oficial_desc the oficial_desc to set
	 */
	public void setOficial_desc(String oficial_desc) {
		this.oficial_desc = oficial_desc;
	}
	/**
	 * @return the actividad
	 */
	public String getActividad() {
		return actividad;
	}
	/**
	 * @param actividad the actividad to set
	 */
	public void setActividad(String actividad) {
		this.actividad = actividad;
	}
	/**
	 * @return the actividad_desc
	 */
	public String getActividad_desc() {
		return actividad_desc;
	}
	/**
	 * @param actividad_desc the actividad_desc to set
	 */
	public void setActividad_desc(String actividad_desc) {
		this.actividad_desc = actividad_desc;
	}
	/**
	 * @return the retencion
	 */
	public String getRetencion() {
		return retencion;
	}
	/**
	 * @param retencion the retencion to set
	 */
	public void setRetencion(String retencion) {
		this.retencion = retencion;
	}
	/**
	 * @return the mala_referencia
	 */
	public String getMala_referencia() {
		return mala_referencia;
	}
	/**
	 * @param mala_referencia the mala_referencia to set
	 */
	public void setMala_referencia(String mala_referencia) {
		this.mala_referencia = mala_referencia;
	}
	/**
	 * @return the comentario
	 */
	public String getComentario() {
		return comentario;
	}
	/**
	 * @param comentario the comentario to set
	 */
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	/**
	 * @return the cont_malas
	 */
	public Integer getCont_malas() {
		return cont_malas;
	}
	/**
	 * @param cont_malas the cont_malas to set
	 */
	public void setCont_malas(Integer cont_malas) {
		this.cont_malas = cont_malas;
	}
	/**
	 * @return the nomlar
	 */
	public String getNomlar() {
		return nomlar;
	}
	/**
	 * @param nomlar the nomlar to set
	 */
	public void setNomlar(String nomlar) {
		this.nomlar = nomlar;
	}
	/**
	 * @return the vinculacion
	 */
	public String getVinculacion() {
		return vinculacion;
	}
	/**
	 * @param vinculacion the vinculacion to set
	 */
	public void setVinculacion(String vinculacion) {
		this.vinculacion = vinculacion;
	}
	/**
	 * @return the tipo_vinculacion
	 */
	public String getTipo_vinculacion() {
		return tipo_vinculacion;
	}
	/**
	 * @param tipo_vinculacion the tipo_vinculacion to set
	 */
	public void setTipo_vinculacion(String tipo_vinculacion) {
		this.tipo_vinculacion = tipo_vinculacion;
	}
	/**
	 * @return the tipo_vinc_desc
	 */
	public String getTipo_vinc_desc() {
		return tipo_vinc_desc;
	}
	/**
	 * @param tipo_vinc_desc the tipo_vinc_desc to set
	 */
	public void setTipo_vinc_desc(String tipo_vinc_desc) {
		this.tipo_vinc_desc = tipo_vinc_desc;
	}
	/**
	 * @return the posicion
	 */
	public String getPosicion() {
		return posicion;
	}
	/**
	 * @param posicion the posicion to set
	 */
	public void setPosicion(String posicion) {
		this.posicion = posicion;
	}
	/**
	 * @return the posicion_desc
	 */
	public String getPosicion_desc() {
		return posicion_desc;
	}
	/**
	 * @param posicion_desc the posicion_desc to set
	 */
	public void setPosicion_desc(String posicion_desc) {
		this.posicion_desc = posicion_desc;
	}
	/**
	 * @return the tipo_compania
	 */
	public String getTipo_compania() {
		return tipo_compania;
	}
	/**
	 * @param tipo_compania the tipo_compania to set
	 */
	public void setTipo_compania(String tipo_compania) {
		this.tipo_compania = tipo_compania;
	}
	/**
	 * @return the tipo_compania_desc
	 */
	public String getTipo_compania_desc() {
		return tipo_compania_desc;
	}
	/**
	 * @param tipo_compania_desc the tipo_compania_desc to set
	 */
	public void setTipo_compania_desc(String tipo_compania_desc) {
		this.tipo_compania_desc = tipo_compania_desc;
	}
	/**
	 * @return the rep_legal
	 */
	public Integer getRep_legal() {
		return rep_legal;
	}
	/**
	 * @param rep_legal the rep_legal to set
	 */
	public void setRep_legal(Integer rep_legal) {
		this.rep_legal = rep_legal;
	}
	/**
	 * @return the rep_legal_desc
	 */
	public String getRep_legal_desc() {
		return rep_legal_desc;
	}
	/**
	 * @param rep_legal_desc the rep_legal_desc to set
	 */
	public void setRep_legal_desc(String rep_legal_desc) {
		this.rep_legal_desc = rep_legal_desc;
	}
	/**
	 * @return the activo
	 */
	public Double getActivo() {
		return activo;
	}
	/**
	 * @param activo the activo to set
	 */
	public void setActivo(Double activo) {
		this.activo = activo;
	}
	/**
	 * @return the pasivo
	 */
	public Double getPasivo() {
		return pasivo;
	}
	/**
	 * @param pasivo the pasivo to set
	 */
	public void setPasivo(Double pasivo) {
		this.pasivo = pasivo;
	}
	/**
	 * @return the es_grupo
	 */
	public String getEs_grupo() {
		return es_grupo;
	}
	/**
	 * @param es_grupo the es_grupo to set
	 */
	public void setEs_grupo(String es_grupo) {
		this.es_grupo = es_grupo;
	}
	/**
	 * @return the capital_social
	 */
	public Double getCapital_social() {
		return capital_social;
	}
	/**
	 * @param capital_social the capital_social to set
	 */
	public void setCapital_social(Double capital_social) {
		this.capital_social = capital_social;
	}
	/**
	 * @return the reserva_legal
	 */
	public Double getReserva_legal() {
		return reserva_legal;
	}
	/**
	 * @param reserva_legal the reserva_legal to set
	 */
	public void setReserva_legal(Double reserva_legal) {
		this.reserva_legal = reserva_legal;
	}
	/**
	 * @return the fecha_const
	 */
	public String getFecha_const() {
		return fecha_const;
	}
	/**
	 * @param fecha_const the fecha_const to set
	 */
	public void setFecha_const(String fecha_const) {
		this.fecha_const = fecha_const;
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
	 * @return the plazo
	 */
	public Integer getPlazo() {
		return plazo;
	}
	/**
	 * @param plazo the plazo to set
	 */
	public void setPlazo(Integer plazo) {
		this.plazo = plazo;
	}
	/**
	 * @return the direccion_domicilio
	 */
	public Integer getDireccion_domicilio() {
		return direccion_domicilio;
	}
	/**
	 * @param direccion_domicilio the direccion_domicilio to set
	 */
	public void setDireccion_domicilio(Integer direccion_domicilio) {
		this.direccion_domicilio = direccion_domicilio;
	}
	/**
	 * @return the fecha_inscrp
	 */
	public String getFecha_inscrp() {
		return fecha_inscrp;
	}
	/**
	 * @param fecha_inscrp the fecha_inscrp to set
	 */
	public void setFecha_inscrp(String fecha_inscrp) {
		this.fecha_inscrp = fecha_inscrp;
	}
	/**
	 * @return the fecha_aum_capital
	 */
	public String getFecha_aum_capital() {
		return fecha_aum_capital;
	}
	/**
	 * @param fecha_aum_capital the fecha_aum_capital to set
	 */
	public void setFecha_aum_capital(String fecha_aum_capital) {
		this.fecha_aum_capital = fecha_aum_capital;
	}
	/**
	 * @return the rep_jud
	 */
	public Integer getRep_jud() {
		return rep_jud;
	}
	/**
	 * @param rep_jud the rep_jud to set
	 */
	public void setRep_jud(Integer rep_jud) {
		this.rep_jud = rep_jud;
	}
	/**
	 * @return the rep_jud_desc
	 */
	public String getRep_jud_desc() {
		return rep_jud_desc;
	}
	/**
	 * @param rep_jud_desc the rep_jud_desc to set
	 */
	public void setRep_jud_desc(String rep_jud_desc) {
		this.rep_jud_desc = rep_jud_desc;
	}
	/**
	 * @return the rep_ex_jud
	 */
	public Integer getRep_ex_jud() {
		return rep_ex_jud;
	}
	/**
	 * @param rep_ex_jud the rep_ex_jud to set
	 */
	public void setRep_ex_jud(Integer rep_ex_jud) {
		this.rep_ex_jud = rep_ex_jud;
	}
	/**
	 * @return the rep_ex_jud_desc
	 */
	public String getRep_ex_jud_desc() {
		return rep_ex_jud_desc;
	}
	/**
	 * @param rep_ex_jud_desc the rep_ex_jud_desc to set
	 */
	public void setRep_ex_jud_desc(String rep_ex_jud_desc) {
		this.rep_ex_jud_desc = rep_ex_jud_desc;
	}
	/**
	 * @return the notaria
	 */
	public Integer getNotaria() {
		return notaria;
	}
	/**
	 * @param notaria the notaria to set
	 */
	public void setNotaria(Integer notaria) {
		this.notaria = notaria;
	}
	/**
	 * @return the capital_inicial
	 */
	public Double getCapital_inicial() {
		return capital_inicial;
	}
	/**
	 * @param capital_inicial the capital_inicial to set
	 */
	public void setCapital_inicial(Double capital_inicial) {
		this.capital_inicial = capital_inicial;
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
	 * @return the sexo
	 */
	public String getSexo() {
		return sexo;
	}
	/**
	 * @param sexo the sexo to set
	 */
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	/**
	 * @return the sexo_desc
	 */
	public String getSexo_desc() {
		return sexo_desc;
	}
	/**
	 * @param sexo_desc the sexo_desc to set
	 */
	public void setSexo_desc(String sexo_desc) {
		this.sexo_desc = sexo_desc;
	}
	/**
	 * @return the fecha_nac
	 */
	public String getFecha_nac() {
		return fecha_nac;
	}
	/**
	 * @param fecha_nac the fecha_nac to set
	 */
	public void setFecha_nac(String fecha_nac) {
		this.fecha_nac = fecha_nac;
	}
	/**
	 * @return the profesion
	 */
	public String getProfesion() {
		return profesion;
	}
	/**
	 * @param profesion the profesion to set
	 */
	public void setProfesion(String profesion) {
		this.profesion = profesion;
	}
	/**
	 * @return the profesion_desc
	 */
	public String getProfesion_desc() {
		return profesion_desc;
	}
	/**
	 * @param profesion_desc the profesion_desc to set
	 */
	public void setProfesion_desc(String profesion_desc) {
		this.profesion_desc = profesion_desc;
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
	 * @return the estado_civil
	 */
	public String getEstado_civil() {
		return estado_civil;
	}
	/**
	 * @param estado_civil the estado_civil to set
	 */
	public void setEstado_civil(String estado_civil) {
		this.estado_civil = estado_civil;
	}
	/**
	 * @return the estado_civil_desc
	 */
	public String getEstado_civil_desc() {
		return estado_civil_desc;
	}
	/**
	 * @param estado_civil_desc the estado_civil_desc to set
	 */
	public void setEstado_civil_desc(String estado_civil_desc) {
		this.estado_civil_desc = estado_civil_desc;
	}
	/**
	 * @return the num_cargas
	 */
	public Integer getNum_cargas() {
		return num_cargas;
	}
	/**
	 * @param num_cargas the num_cargas to set
	 */
	public void setNum_cargas(Integer num_cargas) {
		this.num_cargas = num_cargas;
	}
	/**
	 * @return the nivel_ing
	 */
	public Double getNivel_ing() {
		return nivel_ing;
	}
	/**
	 * @param nivel_ing the nivel_ing to set
	 */
	public void setNivel_ing(Double nivel_ing) {
		this.nivel_ing = nivel_ing;
	}
	/**
	 * @return the nivel_egr
	 */
	public Double getNivel_egr() {
		return nivel_egr;
	}
	/**
	 * @param nivel_egr the nivel_egr to set
	 */
	public void setNivel_egr(Double nivel_egr) {
		this.nivel_egr = nivel_egr;
	}
	/**
	 * @return the tipo_persona
	 */
	public String getTipo_persona() {
		return tipo_persona;
	}
	/**
	 * @param tipo_persona the tipo_persona to set
	 */
	public void setTipo_persona(String tipo_persona) {
		this.tipo_persona = tipo_persona;
	}
	/**
	 * @return the tipo_persona_desc
	 */
	public String getTipo_persona_desc() {
		return tipo_persona_desc;
	}
	/**
	 * @param tipo_persona_desc the tipo_persona_desc to set
	 */
	public void setTipo_persona_desc(String tipo_persona_desc) {
		this.tipo_persona_desc = tipo_persona_desc;
	}
	/**
	 * @return the personal
	 */
	public Integer getPersonal() {
		return personal;
	}
	/**
	 * @param personal the personal to set
	 */
	public void setPersonal(Integer personal) {
		this.personal = personal;
	}
	/**
	 * @return the propiedad
	 */
	public Integer getPropiedad() {
		return propiedad;
	}
	/**
	 * @param propiedad the propiedad to set
	 */
	public void setPropiedad(Integer propiedad) {
		this.propiedad = propiedad;
	}
	/**
	 * @return the trabajo
	 */
	public Integer getTrabajo() {
		return trabajo;
	}
	/**
	 * @param trabajo the trabajo to set
	 */
	public void setTrabajo(Integer trabajo) {
		this.trabajo = trabajo;
	}
	/**
	 * @return the soc_hecho
	 */
	public Integer getSoc_hecho() {
		return soc_hecho;
	}
	/**
	 * @param soc_hecho the soc_hecho to set
	 */
	public void setSoc_hecho(Integer soc_hecho) {
		this.soc_hecho = soc_hecho;
	}
	/**
	 * @return the fecha_ingreso
	 */
	public String getFecha_ingreso() {
		return fecha_ingreso;
	}
	/**
	 * @param fecha_ingreso the fecha_ingreso to set
	 */
	public void setFecha_ingreso(String fecha_ingreso) {
		this.fecha_ingreso = fecha_ingreso;
	}
	/**
	 * @return the fecha_expira
	 */
	public String getFecha_expira() {
		return fecha_expira;
	}
	/**
	 * @param fecha_expira the fecha_expira to set
	 */
	public void setFecha_expira(String fecha_expira) {
		this.fecha_expira = fecha_expira;
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
	 * @return the codsuper
	 */
	public String getCodsuper() {
		return codsuper;
	}
	/**
	 * @param codsuper the codsuper to set
	 */
	public void setCodsuper(String codsuper) {
		this.codsuper = codsuper;
	}
	/**
	 * @return the tipspub
	 */
	public String getTipspub() {
		return tipspub;
	}
	/**
	 * @param tipspub the tipspub to set
	 */
	public void setTipspub(String tipspub) {
		this.tipspub = tipspub;
	}
	/**
	 * @return the subspub
	 */
	public String getSubspub() {
		return subspub;
	}
	/**
	 * @param subspub the subspub to set
	 */
	public void setSubspub(String subspub) {
		this.subspub = subspub;
	}
	/**
	 * @return the codsuper_desc
	 */
	public String getCodsuper_desc() {
		return codsuper_desc;
	}
	/**
	 * @param codsuper_desc the codsuper_desc to set
	 */
	public void setCodsuper_desc(String codsuper_desc) {
		this.codsuper_desc = codsuper_desc;
	}
	/**
	 * @return the tipspub_desc
	 */
	public String getTipspub_desc() {
		return tipspub_desc;
	}
	/**
	 * @param tipspub_desc the tipspub_desc to set
	 */
	public void setTipspub_desc(String tipspub_desc) {
		this.tipspub_desc = tipspub_desc;
	}
	/**
	 * @return the subspub_desc
	 */
	public String getSubspub_desc() {
		return subspub_desc;
	}
	/**
	 * @param subspub_desc the subspub_desc to set
	 */
	public void setSubspub_desc(String subspub_desc) {
		this.subspub_desc = subspub_desc;
	}
	/**
	 * @return the razon_social
	 */
	public String getRazon_social() {
		return razon_social;
	}
	/**
	 * @param razon_social the razon_social to set
	 */
	public void setRazon_social(String razon_social) {
		this.razon_social = razon_social;
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
	 * @return the estado
	 */
	public String getEstado() {
		return estado;
	}
	/**
	 * @param estado the estado to set
	 */
	public void setEstado(String estado) {
		this.estado = estado;
	}
	/**
	 * @return the c_actividad
	 */
	public String getC_actividad() {
		return c_actividad;
	}
	/**
	 * @param c_actividad the c_actividad to set
	 */
	public void setC_actividad(String c_actividad) {
		this.c_actividad = c_actividad;
	}
	/**
	 * @return the c_actividad_desc
	 */
	public String getC_actividad_desc() {
		return c_actividad_desc;
	}
	/**
	 * @param c_actividad_desc the c_actividad_desc to set
	 */
	public void setC_actividad_desc(String c_actividad_desc) {
		this.c_actividad_desc = c_actividad_desc;
	}
	/**
	 * @return the estado_aux
	 */
	public String getEstado_aux() {
		return estado_aux;
	}
	/**
	 * @param estado_aux the estado_aux to set
	 */
	public void setEstado_aux(String estado_aux) {
		this.estado_aux = estado_aux;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return the segmento
	 */
	public String getSegmento() {
		return segmento;
	}
	/**
	 * @param segmento the segmento to set
	 */
	public void setSegmento(String segmento) {
		this.segmento = segmento;
	}
	/**
	 * @return the lineaNegocio
	 */
	public String getLineaNegocio() {
		return lineaNegocio;
	}
	/**
	 * @param lineaNegocio the lineaNegocio to set
	 */
	public void setLineaNegocio(String lineaNegocio) {
		this.lineaNegocio = lineaNegocio;
	}
	/**
	 * @return the apoderadoLegal
	 */
	public Integer getApoderadoLegal() {
		return apoderadoLegal;
	}
	/**
	 * @param apoderadoLegal the apoderadoLegal to set
	 */
	public void setApoderadoLegal(Integer apoderadoLegal) {
		this.apoderadoLegal = apoderadoLegal;
	}
	/**
	 * @return the cedRucAnt
	 */
	public String getCedRucAnt() {
		return cedRucAnt;
	}
	/**
	 * @param cedRucAnt the cedRucAnt to set
	 */
	public void setCedRucAnt(String cedRucAnt) {
		this.cedRucAnt = cedRucAnt;
	}
	
	


}
