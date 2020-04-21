
package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test
import org.junit.rules.ExternalResource

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.common.dto.Beneficiary
import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.utils.VirtualBankingUtil

import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.utils.BDD
import com.cobiscorp.test.utils.VirtualBankingUtil

class Test_ib_add_beneficiary {
	
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment()
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionGroup()
	}

	@AfterClass
	static void closeResources(){
		virtualBankingBase.closeConnections()
	}

	@After
	void finallySession(){
		virtualBankingBase.closeSessionGroup(initSession)
	}

	@Test
	void TestAddBeneficiary(){
		def ServiceName='TestAddBeneficiary'
		try{
			println "Test ---> ${ServiceName}"
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO()
			serviceRequestTO.setSessionId(initSession)
			serviceRequestTO.setServiceId('InternetBanking.WebApp.ProgrammedSavings.Service.ProgrammedSavingsOperations.AddBeneficiary')
			Beneficiary beneficiary=new Beneficiary()
			beneficiary.idBeneficiary="Natural"
			beneficiary.primerApellido="Apellido Test"
			beneficiary.segundoApellido="Segundo Apellido Test"
			beneficiary.nombre="Nombre Test"
			beneficiary.primerApellido="Apellido Test"
			beneficiary.tipoIdentificacion="TipoIdentificacion Test"
			beneficiary.identificacion="Tipodireccion Test"
			beneficiary.idRelacion="IdRelacion Test"
			beneficiary.participacion=25
			beneficiary.tipoDireccion="Tipodireccion Test"
			beneficiary.direccion="direccion Test"
			
			serviceRequestTO.addValue('inBeneficiary', beneficiary)
		
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO)
			
			//Valido si fue exitoso la ejecucion
			def message=''
			if (serviceResponseTO.messages.toList().size()>0){
				message='Ejecucion del Servicio Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			
			Assert.assertTrue(message, serviceResponseTO.success)
			
			println "Test ---> Ejecutado con param. de entrada:"
			println "Test ---> idBeneficiary-> " + beneficiary.idBeneficiary
			println "Test ---> primerApellido-> " + beneficiary.primerApellido
			println "Test ---> segundoApellido-> " + beneficiary.segundoApellido
			println "Test ---> nombre-> " + beneficiary.nombre
			println "Test ---> primerApellido-> " + beneficiary.primerApellido
			println "Test ---> tipoIdentificacion-> " + beneficiary.tipoIdentificacion
			println "Test ---> identificacion-> " + beneficiary.identificacion
			println "Test ---> idRelacion-> " + beneficiary.idRelacion
			println "Test ---> participacion-> " + beneficiary.participacion
			println "Test ---> tipoDireccion-> " + beneficiary.tipoDireccion
			println "Test ---> direccion-> " + beneficiary.direccion
			
			def wOref = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_guid")
			Assert.assertNotNull("No se obtuvo Numero de idBeneficiary",wOref)
			println ('RESPUESTA: @o_guid----->' + wOref.toString().trim()+'<----')
			
			def sql = BDD.getInstance(CTSEnvironment.TARGETID_LOCAL)
			def wRowbeneficiary=null
			wRowbeneficiary = sql.rows("select be_id_beneficiario from cob_bvirtual"+CTSEnvironment.DB_SEPARATOR+"bv_tmp_beneficiario where be_id_beneficiario=?",wOref.toString().trim())
			Assert.assertEquals("No se encontro registro en la bv_tmp_beneficiario",wRowbeneficiary.size(), 1)
			println ('RESPUESTA: wRowBeneficiary----->'+wRowbeneficiary)
			
		}catch(Exception e){
			def msg=e.message
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionGroup(initSession)
		}
	}
}