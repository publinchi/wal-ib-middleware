/**
 * 
 */
package com.cobiscorp.channels.bv.orchestration7x24.test

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test
import org.junit.rules.ExternalResource;

import cobiscorp.ecobis.commons.dto.ServiceRequestTO
import cobiscorp.ecobis.commons.dto.ServiceResponseTO
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.DeliveryMethod;
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.Notification;
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.NotificationDelivery;
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.ProductNotification;
import cobiscorp.ecobis.internetbanking.webapp.admin.dto.User
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.ReceiptInfo;
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.Receipts;
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.SearchReceipts;
import cobiscorp.ecobis.internetbanking.webapp.commons.dto.TransactionRequest;
import cobiscorp.ecobis.internetbanking.webapp.utils.dto.SearchOption

import com.cobiscorp.cobisv.commons.exceptions.SystemException;
import com.cobiscorp.test.SetUpTestEnvironment
import com.cobiscorp.test.VirtualBankingBase
import com.cobiscorp.test.CTSEnvironment
import com.cobiscorp.test.utils.VirtualBankingUtil

/**
 * @author jveloz
 *
 */
class Test_ib_company_receipts_reprint_query {
	private static final String ALL_RECEIPTS			 	= "S";
	private static final String TRANSACTIONS_RECEIPTS 		= "L";

	@ClassRule
	public static ExternalResource setUpEnvironment = new SetUpTestEnvironment();
	static	VirtualBankingBase virtualBankingBase= new VirtualBankingBase()
	def initSession

	@Before
	void setUp(){
		initSession= virtualBankingBase.initSessionNatural();
	}

	@AfterClass
	static void closeResources(){
		virtualBankingBase.closeConnections();
	}

	@After
	void finallySession(){
		virtualBankingBase.closeSessionNatural();
	}
	
	/**
	 * Metodo que ejecuta el servicio Consulta de Movimientos
	 */
	@Test
	void testReciepts(){
		println ' ****** Prueba Regresión testReciepts Persona Juridica ************* '
		//def ServiceName = 'GetAllReceipts'
		try {
			String message='';
			String wOperacion= new String();
			wOperacion="S";
			//S - obtiene todos los recibos
			//L - IMPRIMIR cada uno de los recibos
			String wMethod= new String();
			//wMethod="GetTransferReceipt";
			int op =2;
			//solo para la operacion L 
			//0  GetInternationalTransferReceipt  --> SearchReceiptsInternationalTransfersFlow 
			//1  GetTransferReceipt  --> SearchReceiptsTransfers
			//2  GetNoPaymentReceipt  --> SearchReceiptsNoPayment
			//3  GetBuyAndSellReceipt  --> SearchReceiptsBuyAndSell
			//4  GetLoanReceipt  --> SearchReceiptsLoanPayment
			//5  GetCardReceipt  --> SearchReceiptsCardPayment
			//6  GetServicePaymentReceipt --> SearchReceiptsServices
			def codeError='';
			// Preparo ejecución del servicio
			ServiceRequestTO serviceRequestTO = new ServiceRequestTO();
			serviceRequestTO.setSessionId(initSession);
			//
			SearchReceipts wSearchReceipts = new SearchReceipts();
			SearchOption wSearchOption = new SearchOption();
			TransactionRequest wTransactionRequest= new TransactionRequest();
			//-----------------------------------
			wSearchOption.numberOfResults=20;
			wSearchOption.sequential=0;
			//wSearchOption.criteria="P";
			//-----------------------------------
			if (wOperacion =="S"){
				wSearchReceipts.paymentType="H";
				/*
				 	A          SUSPENSION PAGO DE CHEQUES      2                                                                                                                                                                                                                                V      (null)     (null)     (null)     
					F          TRANSFERENCIA A MIS CUENTAS DAVIVIENDA  1                                                                                                                                                                                                                        V      (null)     (null)     (null)     
					H          TRANSFERENCIA ACH                                                                                                                                                                                                                                               E      (null)     (null)     (null)     
					I          TRANSFERENCIAS AL EXTERIOR  0                                                                                                                                                                                                                                    V      (null)     (null)     (null)     
					J          PAGO DE TARJETAS            5                                                                                                                                                                                                                                    V      (null)     (null)     (null)     
					N          TRANSFERENCIA SINPE     1                                                                                                                                                                                                                                        V      (null)     (null)     (null)     
					P          PAGO DE PRESTAMOS       4                                                                                                                                                                                                                                           V      (null)     (null)     (null)     
					S          PAGO DE SERVICIOS PUBLICOS     6                                                                                                                                                                                                                                 V      (null)     (null)     (null)     
					T          TRANSFERENCIAS A OTRAS CUENTAS DAVIVIENDA    1                                                                                                                                                                                                                   V      (null)     (null)     (null)     
					V          COMPRA/VENTA DE DIVISAS     3
				 */
				
				wSearchReceipts.debitAccount="10410108275407019";//A -->CTSEnvironment.bvAccCtaAhoNumber;
				
				//wSearchReceipts.debitAccount="10410108275405315";//A,F,V -->CTSEnvironment.bvAccCtaAhoNumber;
				//wSearchReceipts.debitAccount="10410108275402005";//H,I,P,T
				//wSearchReceipts.debitAccount="4249032200000125";//J
				//wSearchReceipts.debitAccount="10410000041216309";//N
				//wSearchReceipts.startDate="04/02/2013";//A,F
				//wSearchReceipts.debitAccount="10410108640405011";//S
				//wSearchReceipts.startDate="01/03/2013";//I,J,N,P
				wSearchReceipts.startDate="01/02/2012";//H
				wSearchReceipts.endDate="09/02/2013";
				wSearchReceipts.paidService=null;//CTSEnvironment.bvDestinationAccCtaCteNumber;
				wSearchReceipts.idService=0;//H,I,J,N,P,A,F				
				wSearchOption.sequential=0; //A
				//wSearchReceipts.idService=67;//S				
				serviceRequestTO.setServiceId('InternetBanking.WebApp.Receipts.Service.Receipts.GetAllReceipts');
				serviceRequestTO.addValue('inSearchReceipts',wSearchReceipts);
				serviceRequestTO.addValue('inSearchOption',wSearchOption);
			}else{
				if(wOperacion =="L"){
				switch ( op ) {
					case 0:
					println "----------- GetInternationalTransferReceipt -----------";
					wSearchOption.sequential=5668152;
					wSearchOption.criteria="I";
					serviceRequestTO.setServiceId('InternetBanking.WebApp.Receipts.Service.Receipts.GetInternationalTransferReceipt');
					break;
					
					case 1:
					println "----------- GetTransferReceipt -----------";
					wTransactionRequest.dateFormatId=103;
					//wSearchOption.sequential=374131;//F
					wSearchOption.sequential=10296737;//N
					//wSearchOption.sequential=455517;//T
					wSearchOption.criteria="N";
					serviceRequestTO.setServiceId('InternetBanking.WebApp.Receipts.Service.Receipts.GetTransferReceipt');
					serviceRequestTO.addValue('inTransactionRequest',wTransactionRequest);
					break;
					
					case 2:
					println "----------- GetNoPaymentReceipt -----------";
					wSearchOption.sequential=8951613;
					wSearchOption.criteria="A";
					serviceRequestTO.setServiceId('InternetBanking.WebApp.Receipts.Service.Receipts.GetNoPaymentReceipt');
					break;
					
					case 3:
					println "----------- GetBuyAndSellReceipt -----------";
					wSearchOption.sequential=10590534;
					wSearchOption.criteria="V";
					serviceRequestTO.setServiceId('InternetBanking.WebApp.Receipts.Service.Receipts.GetBuyAndSellReceipt');
					break;
					
					case 4:
					println "----------- GetLoanReceipt -----------";
					wSearchOption.sequential=6023201;
					wSearchOption.criteria="P";
					serviceRequestTO.setServiceId('InternetBanking.WebApp.Receipts.Service.Receipts.GetLoanReceipt');
					break;
					
					case 5:
					println "----------- GetCardReceipt -----------";
					wSearchOption.sequential=10324662;
					wSearchOption.criteria="J";
					serviceRequestTO.setServiceId('InternetBanking.WebApp.Receipts.Service.Receipts.GetCardReceipt');
					break;
					
					case 6:
					println "----------- GetServicePaymentReceipt -----------";
					wSearchOption.sequential=8441774;
					wSearchOption.criteria="S";
					serviceRequestTO.setServiceId('InternetBanking.WebApp.Receipts.Service.Receipts.GetServicePaymentReceipt');
					break;
					
					default:
					println "----------- No existe Servicio -----------";
					break;
					
					}
				}
			};
			serviceRequestTO.addValue('inSearchOption',wSearchOption);
			ServiceResponseTO serviceResponseTO = new VirtualBankingUtil().executeService(serviceRequestTO);
			//Valido si fue exitoso la ejecucion
			if (serviceResponseTO.messages.toList().size()>0){
			message='Ejecucion del Servicion Fallido>>'+((cobiscorp.ecobis.commons.dto.MessageTO)serviceResponseTO.messages.toList().get(0)).message
			}
			//obteniendo valores del servicio respectivo
			Assert.assertTrue(message, serviceResponseTO.success);
			
			if (wOperacion =="S"){
				Receipts [] oResponseReceipts= serviceResponseTO.data.get('returnReceipts').collect().toArray();
				
				for (var in oResponseReceipts) {
					println "Fecha "+ var.date;
					println "Tipo de Pago "+ var.paymentType;
					println "Recibo "+ var.receipt;
					println "Monto "+ var.amount;
					println "Moneda "+ var.currency;
					println "Hora "+ var.dateAndTime;
					println "Imprimir "+ var.print;
					println "ID "+ var.paymentId;
					println "Descripcion Moneda "+ var.currencyDescription;
					println "Documento "+ var.documentNumber;
					println "ID Moneda "+ var.currencyId;
					println "ID Moneda Credito "+ var.currencyIdCredit;
					println "Factura "+ var.invoice;
					
					println "_____________________" ;
					};
			}else{
				if (wOperacion =="L"){
					ReceiptInfo [] oResponseReceiptInfo= serviceResponseTO.data.get('returnReceiptInfo').collect().toArray();
					
					for (var in oResponseReceiptInfo) {
						println "Tipo de Pago "+ var.paymentType;
						//println "Cheque "+ var.cheque;//va en GetNoPaymentReceipt, no va GetLoanReceipt, GetCardReceipt, GetBuyAndSellReceiptexchange
						//println "Servicio "+ var.loan;//va en GetServicePaymentReceipt, no va GetNoPaymentReceipt, GetCardReceipt, GetBuyAndSellReceiptexchange
						//println "Propietario o Perteneciente "+ var.owner;//no va GetLoanReceipt, GetBuyAndSellReceiptexchange
						println "Cuenta de Debito "+ var.debitAccount;
						//println "Detalle Beneficiario "+ var.detailRecipient;//no va GetServicePaymentReceipt,GetNoPaymentReceipt, GetLoanReceipt,GetBuyAndSellReceiptexchange
						//println "Cuenta Destino o Trajeta "+ var.creditAccount;//no va GetNoPaymentReceipt, GetLoanReceipt, GetCardReceipt
						//println "Banco Beneficiario "+ var.beneficiaryBank;//no va GetTransferReceipt, GetServicePaymentReceipt, GetNoPaymentReceipt, GetLoanReceipt, GetCardReceipt,GetBuyAndSellReceiptexchange
						//println "Monto "+ var.amount;//no va GetNoPaymentReceipt, GetBuyAndSellReceiptexchange
						//println "Monto1 "+ var.amountDebited;//va en GetBuyAndSellReceipt
						//println "Monto2 "+ var.amountCredited;//va en GetBuyAndSellReceipt
						//println "Tipo de Cambio "+ var.exchange;//va en GetBuyAndSellReceiptexchange
						//println "Moneda "+ var.currency;//no va GetBuyAndSellReceiptexchange
						//println "Concepto o Motivo Suspension o Operacion "+ var.concept;//no va GetCardReceipt
						println "Fecha "+ var.dateAndTime;
						println "Referencia "+ var.reference;
						println "ID Moneda "+ var.currencyId;
						println "ID Moneda Credito "+ var.currencyIdCredit;
						//println "Factura "+ var.invoice;//no va GetTransferReceipt,GetServicePaymentReceipt,GetNoPaymentReceipt, GetLoanReceipt, GetCardReceipt,GetBuyAndSellReceiptexchange
						switch ( op ) {
							case 0:
								println "----------- GetInternationalTransferReceipt -----------";
								println "Propietario "+ var.owner;
								println "Detalle Beneficiario "+ var.detailRecipient;
								println "Cuenta Destino "+ var.creditAccount;
								println "Banco Beneficiario "+ var.beneficiaryBank;
								println "Monto "+ var.amount;
								println "Moneda "+ var.currency;
								println "Concepto "+ var.concept;
								println "Factura "+ var.invoice;//14
							break;
							
							case 1:
								println "----------- GetTransferReceipt -----------";
								println "Propietario "+ var.owner;
								println "Detalle Beneficiario "+ var.detailRecipient;
								println "Cuenta Destino "+ var.creditAccount;
								println "Monto "+ var.amount;
								println "Moneda "+ var.currency;
								println "Concepto "+ var.concept;//12
							break;
							
							case 2:
								println "----------- GetNoPaymentReceipt -----------";
								println "Cheque "+ var.cheque;
								println "Perteneciente "+ var.owner;
								println "Moneda "+ var.currency;
								println "Descripcion Cheque "+ var.cheque;
								println "Motivo Suspension "+ var.concept;//11
							break;
							
							case 3:
								println "----------- GetBuyAndSellReceipt -----------";
								println "Monto1 "+ var.amountDebited;
								println "Monto2 "+ var.amountCredited;
								println "Cuenta Credito "+ var.creditAccount;
								println "Tipo de Cambio "+ var.exchange;
								println "Operacion "+ var.concept;//11
							break;
							
							case 4:
								println "----------- GetLoanReceipt -----------";
								println "Prestamo "+ var.loan;
								println "Monto "+ var.amount;
								println "Moneda "+ var.currency;
								println "Concepto "+ var.concept;//10
							break;
							
							case 5:
								println "----------- GetCardReceipt -----------";
								println "Tarjeta "+ var.creditAccount;
								println "Monto "+ var.amount;
								println "Moneda "+ var.currency;
								println "Propietario "+ var.owner;
								println "Detalle Beneficiario "+ var.detailRecipient;//11
							break;
							
							case 6:
								println "----------- GetServicePaymentReceipt -----------";
								println "Servicio "+ var.loan;
								println "Propietario "+ var.owner;
								println "Monto "+ var.amount;
								println "Moneda "+ var.currency;
								println "Concepto "+ var.concept;
								println "Tarjeta "+ var.creditAccount;//12
							break;
							};
						};
				}
			};
			
		 } catch (Exception e) {
			def msg=e.message;
			println "${ServiceName} Exception--> ${msg}"
			virtualBankingBase.closeSessionNatural(initSession);
			Assert.fail();
		}
	}
}
