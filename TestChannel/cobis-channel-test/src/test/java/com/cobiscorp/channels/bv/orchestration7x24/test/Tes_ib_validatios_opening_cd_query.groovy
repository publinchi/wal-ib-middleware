/**
 * 
 */
package com.cobiscorp.channels.bv.orchestration7x24.test

import java.text.SimpleDateFormat
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Identification;
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Parameter
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.Periodicity;
import cobiscorp.ecobis.internetbanking.webapp.products.dto.CertificateDeposit;
import cobiscorp.ecobis.internetbanking.webapp.products.dto.CertificateDepositResult;
import cobiscorp.ecobis.internetbanking.webapp.services.dto.EnquiryRequest
import cobiscorp.ecobis.internetbanking.webapp.services.dto.FixedTermDepositBalance
import cobiscorp.ecobis.internetbanking.webapp.services.dto.PaymentDetailSchedule

import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil
import com.cobiscorp.test.VirtualBankingBase;

/**
 * @author jveloz
 *
 */
class Tes_ib_validatios_opening_cd_query {
	private static final String OPERATION_EXE_SIMULATION 	= "E";
	private static final String OPERATION_GET_DETAIL 		= "S";
	private static final String OPERATION_GET_CD_TYPE 		= "H";
	private static final String OPERATION_GET_PERIODICITY	= "P";
	private static final String OPERATION_GET_TERM			= "T";//
	private static final String OPERATION_GET_RATE			= "C";//
	
	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	/**
	 *
	 */
	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionNatural();
	}

	@AfterClass
	static void closeResources(){
		virtualBankingBase.closeConnections()
	}

	@After
	void finallySession(){
		virtualBankingBase.closeSessionNatural(initSession);
	}

	/**
	 * Metodo consultas de la apertura de deposito a plazo
	 */
	@Test
	void testValidationsCertificateDeposit() {
		println ' ****** Prueba Regresión consulta de apertura de deposito Persona Natural************* '
		def ServiceName= 'testValidationsCertificateDeposit'
		try{
			String wOperacion= new String();
			//"E"-->SIMULACION
			//"S"-->DETAIL
			//"H"-->TYPE
			//"P"-->PERIIODICITY
			//"T"-->TERM--parametrizacion pendiente
			//"C"-->RATE
			wOperacion="H";
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			
			//parametro de entrada general para todos
			CertificateDeposit wCertificateDeposit= new CertificateDeposit();
			wCertificateDeposit.nemonic="PERF1";
			
			
			//SIMULACION
			if(wOperacion==OPERATION_EXE_SIMULATION)
			{
				wCertificateDeposit.nemonic="PERF2";
				wCertificateDeposit.amount=100000
				wCertificateDeposit.term=3
				wCertificateDeposit.rate=1.0;
				wCertificateDeposit.money =CTSEnvironment.bvAccDpfCurrencyId
				wCertificateDeposit.category="hh";
				Calendar fecha = new GregorianCalendar(2015,11,10,00,00,00);
				//Calendar fecha =new Date("10/11/2015");
				wCertificateDeposit.date =fecha
				wCertificateDeposit.entityId=13036
				wCertificateDeposit.payDay="4"
				serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.ExecuteCDSimulation');
			}else{//DETALLE
				if(wOperacion==OPERATION_GET_DETAIL)
				{
					serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.GetDetailCD');
				}else{//TIPO
					if(wOperacion==OPERATION_GET_CD_TYPE)
					{
						serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.GetCDType');
					}else{//PERIODO
						if(wOperacion==OPERATION_GET_PERIODICITY)
						{
						    //CAT-->valor-descripcion categoria
							//MOT-->valor-descripcion motivo
							//OFI
							//PLA--> valor-descripcion plazo
							//PPE-->todo
							wCertificateDeposit.regType="PPE";
							serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.GetCDPeriodicity');
						}else{//PLAZO
							if(wOperacion==OPERATION_GET_TERM)
							{
								wCertificateDeposit.nemonic="PERJ1";
								wCertificateDeposit.term=5;
								Calendar wProcessDate = new GregorianCalendar(2014,10,01,00,00,00);
								wCertificateDeposit.processDate=wProcessDate;//fechaProceso;
								Calendar wExpiration = new GregorianCalendar(2015,10,01,00,00,00);
								wCertificateDeposit.expiration=wExpiration;
								wCertificateDeposit.termDate="F";
								serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.GetCDTerm');
							}else{//TAZA
								if(wOperacion==OPERATION_GET_RATE)
								{
									wCertificateDeposit.nemonic="PERF2";
									wCertificateDeposit.office=CTSEnvironment.bvAccCtaAhoOfficeId;
									wCertificateDeposit.amount=100000;
									wCertificateDeposit.term=6;
									wCertificateDeposit.money=CTSEnvironment.bvAccDpfCurrencyId;
									serviceRequestTO.setServiceId('InternetBanking.WebApp.EnterpriseServices.Service.Simulator.GetCDRate');
								}
							}
						}
					}
				}
			};
		    //ejecutando el servicio
			serviceRequestTO.addValue('inCertificateDeposit',wCertificateDeposit);
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
			
			def message='';
			
			//Valido si fue exitoso la ejecucion
			if (serviceResponseTO.messages.toList().size()>0){
			message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			//obteniendo valores del servicio respectivo
			Assert.assertTrue(message, serviceResponseTO.success);
			if(wOperacion==OPERATION_EXE_SIMULATION)
			{
				CertificateDepositResult [] oResponse= serviceResponseTO.data.get('returnCertificateDepositResult').collect().toArray();
					for (var in oResponse) {
						println "Rate "+ var.rate;
						println "Number Of Payment "+ var.numberOfPayment;
						println "Interest Pay Day "+ var.interestPayDay;
						println "Interest Estimated Total "+ var.interestEstimatedTotal;
						println "Interest Estimated "+ var.interestEstimated;
					}
			}else{//DETALLE
				if(wOperacion==OPERATION_GET_DETAIL)
				{
					CertificateDeposit[] oResponse= serviceResponseTO.data.get('returnCertificateDeposit').collect().toArray();
					for (var in oResponse) {
						println "Type "+ var.type;
						println "Tax Retention "+ var.taxRetention;
						println "Nemonic "+ var.nemonic;
						println "Method Of Payment "+ var.methodOfPayment;
						println "Grace Days Num "+ var.graceDaysNum;
						println "Grace Days "+ var.graceDays;
						println "Extended Aut "+ var.extendedAut;
						println "Capitalize "+ var.capitalize;
						println "Calculation Base "+ var.calculationBase;
					}
				}else{//TIPO
					if(wOperacion==OPERATION_GET_CD_TYPE)
					{
						CertificateDeposit[] oResponse1= serviceResponseTO.data.get('returnCertificateDeposit').collect().toArray();
						Parameter[] oResponse2= serviceResponseTO.data.get('returnParameter').collect().toArray();
						for (var1 in oResponse1) {
							println "Tipo "+ var1.type;
						}
						for (var2 in oResponse2) {
							println "Valor/Categoria "+ var2.name;
						}
						
						println "No SE COMO RETORNAR";
					}else{//PERIODO
						if(wOperacion==OPERATION_GET_PERIODICITY)
						{
							Periodicity[] oResponse= serviceResponseTO.data.get('returnPeriodicity').collect().toArray();
							for (var in oResponse) {
								println "Value "+ var.value;
								println "Description "+ var.description;
								println "Factor "+ var.factor;
								println "Percentage "+ var.percentage;
								println "Days Factor "+ var.daysFactor;
							}
						}else{//PLAZO
							if(wOperacion==OPERATION_GET_TERM)
							{
								println "No retorna nada";
							}else{//TAZA
								if(wOperacion==OPERATION_GET_RATE)
								{
									CertificateDeposit[] oResponse= serviceResponseTO.data.get('returnCertificateDeposit').collect().toArray();
									for (var in oResponse) {
										println "Rate "+ var.rate;
										println "Max Rate "+ var.maxRate;
										println "Min Rate "+ var.minRate;
										println "Rate Desc "+ var.rateDesc;
										println "Rate Auth "+ var.rateAuth;
									}
								}
							}
						}
					}
				}
			};
		  
			def wNumberOfPayment = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_plazo");
			def wExpirationDate = serviceResponseTO.data.get("com.cobiscorp.cobis.cts.service.response.output").getAt("@o_fecha_ven");
			println "Number Of Payment "+ wNumberOfPayment;
			println "Expiration Date "+ wExpirationDate;
			
		}catch(Exception e){
			def msg=e.message;
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession);
			Assert.fail();
		}
	}
}
