/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.EntityIntegratedResponse;
import com.cobiscorp.ecobis.ib.application.dtos.EntityRequest;

/**
 * @author mvelez
 *
 */
public interface ICoreServiceEntityIntegrated {
	/**  
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>EntityRequest aEntityRequest = new EntityRequest();</li>
		<li>entityReq.setTrn(Integer.parseInt(aRequest.readValueParam("@t_trn")));</li>
	    <li>entityReq.setEnte(Integer.parseInt(aRequest.readValueParam("@i_ente")));</li>
	    <li>entityReq.setFormato_fecha(aRequest.readValueParam("@i_formato"));</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		
		<li>EntityIntegratedResponse aEntityResp = new EntityIntegratedResponse();</li>
		<li>List<EntityIntegrated> aEntityIntegratedCollection = new ArrayList<EntityIntegrated>();</li>
		
		<li>EntityIntegrated aEntityIntegrated = new EntityIntegrated());</li>
		
		<li>aEntityIntegrated.setEnte(11011);</li>                          
		<li>aEntityIntegrated.setNombre_completo_DE("JUAN JOSE PEREZ PEREZ");</li>                              
		<li>aEntityIntegrated.setSubtipo("P");</li>                                         
		<li>aEntityIntegrated.setCed_ruc("09152525252");</li>                                         
		<li>aEntityIntegrated.setTipo_ced("1.1");</li>                                        
		<li>aEntityIntegrated.setFilial(1);</li>                        
		<li>aEntityIntegrated.setFilial_desc("FILIAL BANCO");</li>                                     
		<li>aEntityIntegrated.setOficina(50);</li>                       
		<li>aEntityIntegrated.setOficina_desc("CASA MATRIZ DUMMY");</li>                                    
		<li>aEntityIntegrated.setFecha_crea("31/08/2002");</li>                                      
		<li>aEntityIntegrated.setFecha_mod("01/07/2012");</li>                                      
		<li>aEntityIntegrated.setDireccion(3);</li>                    
		<li>aEntityIntegrated.setReferencia(0);</li>                   
		<li>aEntityIntegrated.setCasilla(3);</li>                      
		<li>aEntityIntegrated.setCasilla_def("74");</li>                                    
		<li>aEntityIntegrated.setTipo_dp("C");</li>                                        
		<li>aEntityIntegrated.setBalance(0);</li>                      
		<li>aEntityIntegrated.setGrupo(376);</li>                        
		<li>aEntityIntegrated.setGrupo_desc("GRUPO DUMMY");</li>                                     
		<li>aEntityIntegrated.setPais(41);</li>                         
		<li>aEntityIntegrated.setPais_desc("COSTA RICA");</li>                                      
		<li>aEntityIntegrated.setNacionalidad("COSTARICENSE");</li>                                   
		<li>aEntityIntegrated.setOficial(202);</li>                      
		<li>aEntityIntegrated.setOficial_desc("JOSE JOSE");</li>                                   
		<li>aEntityIntegrated.setActividad("D023");</li>                                      
		<li>aEntityIntegrated.setActividad_desc("ACTIVIDAD DUMMY");</li>                                 
		<li>aEntityIntegrated.setRetencion("S");</li>                                      
		<li>aEntityIntegrated.setMala_referencia("N");</li>                                
		<li>aEntityIntegrated.setComentario("CTA");</li>                                     
		<li>aEntityIntegrated.setCont_malas(0);</li>                   
		<li>aEntityIntegrated.setNomlar("NOMBRE LARGO DUMMY");</li>                                         
		<li>aEntityIntegrated.setVinculacion("N");</li>                                    
		<li>aEntityIntegrated.setTipo_vinculacion("122");</li>                               
		<li>aEntityIntegrated.setTipo_vinc_desc("DESCRIPCION VINCULACION DUMMY");</li>                                 
		<li>aEntityIntegrated.setPosicion("A");</li>                                       
		<li>aEntityIntegrated.setPosicion_desc("XXXXXXX");</li>                                  
		<li>aEntityIntegrated.setTipo_compania("11");</li>                                  
		<li>aEntityIntegrated.setTipo_compania_desc("XXXXXX DUMY");</li>                             
		<li>aEntityIntegrated.setRep_legal(233);</li>                    
		<li>aEntityIntegrated.setRep_legal_desc("XXXXXX");</li>                                 
		<li>aEntityIntegrated.setActivo(20000.00);</li>                     
		<li>aEntityIntegrated.setPasivo(1500.00);</li>                     
		<li>aEntityIntegrated.setEs_grupo("XXXX");</li>                                       
		<li>aEntityIntegrated.setCapital_social(3000.00);</li>             
		<li>aEntityIntegrated.setReserva_legal(2000.00);</li>              
		<li>aEntityIntegrated.setFecha_const("21/05/2011");</li>                                    
		<li>aEntityIntegrated.setNombre_completo("XXXXXX  DATA DUMMY");</li>                                
		<li>aEntityIntegrated.setPlazo(12);</li>                        
		<li>aEntityIntegrated.setDireccion_domicilio(3);</li>          
		<li>aEntityIntegrated.setFecha_inscrp("01/01/2010");</li>                                   
		<li>aEntityIntegrated.setFecha_aum_capital("05/05/2010");</li>                              
		<li>aEntityIntegrated.setRep_jud(4);</li>                      
		<li>aEntityIntegrated.setRep_jud_desc("REP JUD XXXX");</li>                                   
		<li>aEntityIntegrated.setRep_ex_jud(5);</li>                   
		<li>aEntityIntegrated.setRep_ex_jud_desc("REP EX JUD XXXX");</li>                                
		<li>aEntityIntegrated.setNotaria(5);</li>                      
		<li>aEntityIntegrated.setCapital_inicial(5000.00);</li>            
		<li>aEntityIntegrated.setP_apellido("PEREZ");</li>                                     
		<li>aEntityIntegrated.setS_apellido("PEREZ");</li>                                     
		<li>aEntityIntegrated.setSexo("F");</li>                                           
		<li>aEntityIntegrated.setSexo_desc("FEMENINO");</li>                                      
		<li>aEntityIntegrated.setFecha_nac("01/01/1974");</li>                                      
		<li>aEntityIntegrated.setProfesion("3");</li>                                      
		<li>aEntityIntegrated.setProfesion_desc("PROFESION XYZ DUMMY");</li>                                 
		<li>aEntityIntegrated.setPasaporte("091650210548777");</li>                                      
		<li>aEntityIntegrated.setEstado_civil("S");</li>                                   
		<li>aEntityIntegrated.setEstado_civil_desc("SOLTERO");</li>                              
		<li>aEntityIntegrated.setNum_cargas(5);</li>                   
		<li>aEntityIntegrated.setNivel_ing(2000.00);</li>                  
		<li>aEntityIntegrated.setNivel_egr(1000.00);</li>                  
		<li>aEntityIntegrated.setTipo_persona("P");</li>                                   
		<li>aEntityIntegrated.setTipo_persona_desc("PERSONA NATURAL");</li>                              
		<li>aEntityIntegrated.setPersonal(3);</li>                     
		<li>aEntityIntegrated.setPropiedad(2);</li>                    
		<li>aEntityIntegrated.setTrabajo(2);</li>                      
		<li>aEntityIntegrated.setSoc_hecho(0);</li>                    
		<li>aEntityIntegrated.setFecha_ingreso("01/01/2011");</li>                                  
		<li>aEntityIntegrated.setFecha_expira("01/01/2019");</li>                                   
		<li>aEntityIntegrated.setC_apellido("APELLIDO CASADA DUMMY");</li>                                     
		<li>aEntityIntegrated.setS_nombre("SECOND NOMBRE DUMMY");</li>                                       
		<li>aEntityIntegrated.setCodsuper("0");</li>                                       
		<li>aEntityIntegrated.setTipspub(" ");</li>                                        
		<li>aEntityIntegrated.setSubspub(" ");</li>                                        
		<li>aEntityIntegrated.setCodsuper_desc(" ");</li>                                  
		<li>aEntityIntegrated.setTipspub_desc(" ");</li>                                   
		<li>aEntityIntegrated.setSubspub_desc(" ");</li>                                   
		<li>aEntityIntegrated.setRazon_social(" ");</li>                                   
		<li>aEntityIntegrated.setNombre(" ");</li>                                         
		<li>aEntityIntegrated.setEstado("V");</li>                                         
		<li>aEntityIntegrated.setC_actividad(" ");</li>                                    
		<li>aEntityIntegrated.setC_actividad_desc(" ");</li>                               
		<li>aEntityIntegrated.setEstado_aux(" ");</li>                                     
		
		<li>aEntityIntegratedCollection.add(aEntityIntegrated);</li>
		
		<li>aEntityResp.setEntityIntegratedCollection(EntityIntegratedCollection);</li>
	</ul>
	<b>
		@throws
		-ManejoDeErrores
	</b>
	<ul>
		<li>CTSServiceException</li>
		<li>CTSInfrastructureException</li>
	</ul>
	 */
	EntityIntegratedResponse GetEntityIntegrated (EntityRequest entityRequest)  throws CTSServiceException, CTSInfrastructureException;
}
