package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.CertificateDepositRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CertificateDepositResponse;

/**
 * @author jveloz
 *
 */
public interface ICoreServiceOpeningCertificateDeposit {
	/**  
	 * 
	 * 
	 * <b>Solicitud de apertura de plazo fijo.</b>
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>CertificateDepositRequest wCertificateDepositRequest=new CertificateDepositRequest();</li>
		
		<li>BeneficiaryCertificateDeposit wBeneficiaryCertificateDeposit1= new BeneficiaryCertificateDeposit();</li>
		<li>BeneficiaryCertificateDeposit wBeneficiaryCertificateDeposit2= new BeneficiaryCertificateDeposit();</li>
		<li>BeneficiaryCertificateDeposit wBeneficiaryCertificateDeposit3= new BeneficiaryCertificateDeposit();</li>
		<li>BeneficiaryCertificateDeposit wBeneficiaryCertificateDeposit4= new BeneficiaryCertificateDeposit();</li>
		
		<li>Entity wEntity = new Entity();</li>
		<li>wEntity.setEnte(Integer.parseInt(aRequest.readValueParam("@i_ente")));</li>
		
		<li>CertificateDeposit wCertificateDeposit =new CertificateDeposit();</li>
		<li>wCertificateDepositRequest.setEntity(wEntity);</li>
		<li>wCertificateDepositRequest.setUserName(aRequest.readValueParam("@i_login"));</li>
		<li>wCertificateDepositRequest.setAuthorizationRequired("@i_doble_autorizacion");</li>
		
		<li>Currency wCurrency = new Currency();</li>
		<li>wCurrency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_moneda")));</li>
		
		<li>Product wProduct=new Product();</li>
		<li>wProduct.setProductNumber(aRequest.readValueParam("@i_cta_debito"));</li>
		<li>wProduct.setCurrency(wCurrency);</li>
		<li>wProduct.setProductType(Integer.parseInt(aRequest.readValueParam("@i_pro_debito")));</li>
		
		<li>Rate wRate= new Rate();</li>
		<li>wRate.setRate(Double.parseDouble(aRequest.readValueParam("@i_tasa")));</li>
		
		<li>wCertificateDepositRequest.setProduct(wProduct);	</li>		
		<li>wCertificateDeposit.setCapitalize(aRequest.readValueParam("@i_capitaliza"));</li>
		<li>wCertificateDeposit.setTerm(Integer.parseInt(aRequest.readValueParam("@i_plazo")));</li>
		<li>wCertificateDeposit.setAmount(Double.parseDouble(aRequest.readValueParam("@i_monto")));</li>
		<li>wCertificateDeposit.setNemonic(aRequest.readValueParam("@i_nemonico"));</li>		
		<li>wCertificateDeposit.setRate(wRate);</li>
		<li>wCertificateDeposit.setProcessDate(aRequest.readValueParam("@i_fecha_valor"));</li>
		<li>wCertificateDeposit.setPayDay(Integer.parseInt(aRequest.readValueParam("@i_dia_pago")));</li>
		<li>wCertificateDeposit.setMethodOfPayment(aRequest.readValueParam("@i_forma_pago"));</li>
		<li>wCertificateDeposit.setMail(aRequest.readValueParam("@i_mail"));</li>
		<li>wCertificateDeposit.setOffice(aRequest.readValueParam("@i_oficina"));</li>
		<li>wCertificateDeposit.setPeriodicityId(aRequest.readValueParam("@i_periodicidad"));</li>
		<li>wCertificateDepositRequest.setCertificateDeposit(wCertificateDeposit);</li>
		
		<li>//beneficiary1</li>
		<li>wBeneficiaryCertificateDeposit1.setCedula(aRequest.readValueParam("@i_cedula1"));</li>
		<li>wBeneficiaryCertificateDeposit1.setName(aRequest.readValueParam("@i_nombre1"));</li>
		<li>wBeneficiaryCertificateDeposit1.setFirstSurname(aRequest.readValueParam("@i_p_apellido1"));</li>
		<li>wBeneficiaryCertificateDeposit1.setRelation(aRequest.readValueParam("@i_parentesco1"));</li>
		<li>wBeneficiaryCertificateDeposit1.setPercentage(Double.parseDouble(aRequest.readValueParam("@i_porcentaje1")));</li>
		<li>wBeneficiaryCertificateDeposit1.setLastSurname(aRequest.readValueParam("@i_s_apellido1"));</li>
		<li>//beneficiary2</li>
		<li>wBeneficiaryCertificateDeposit2.setCedula(aRequest.readValueParam("@i_cedula2"));</li>
		<li>wBeneficiaryCertificateDeposit2.setName(aRequest.readValueParam("@i_nombre2"));</li>
		<li>wBeneficiaryCertificateDeposit2.setFirstSurname(aRequest.readValueParam("@i_p_apellido2"));</li>
		<li>wBeneficiaryCertificateDeposit2.setRelation(aRequest.readValueParam("@i_parentesco2"));</li>
		<li>wBeneficiaryCertificateDeposit2.setPercentage(Double.parseDouble(aRequest.readValueParam("@i_porcentaje2")));</li>
		<li>wBeneficiaryCertificateDeposit2.setLastSurname(aRequest.readValueParam("@i_s_apellido2"));</li>
		<li>//beneficiary3</li>
		<li>wBeneficiaryCertificateDeposit3.setCedula(aRequest.readValueParam("@i_cedula3"));</li>
		<li>wBeneficiaryCertificateDeposit3.setName(aRequest.readValueParam("@i_nombre3"));</li>
		<li>wBeneficiaryCertificateDeposit3.setFirstSurname(aRequest.readValueParam("@i_p_apellido3"));</li>
		<li>wBeneficiaryCertificateDeposit3.setRelation(aRequest.readValueParam("@i_parentesco3"));</li>
		<li>wBeneficiaryCertificateDeposit3.setPercentage(Double.parseDouble(aRequest.readValueParam("@i_porcentaje3")));</li>
		<li>wBeneficiaryCertificateDeposit3.setLastSurname(aRequest.readValueParam("@i_s_apellido3"));</li>
		<li>//beneficiary4</li>
		<li>wBeneficiaryCertificateDeposit4.setCedula(aRequest.readValueParam("@i_cedula4"));</li>
		<li>wBeneficiaryCertificateDeposit4.setName(aRequest.readValueParam("@i_nombre4"));</li>
		<li>wBeneficiaryCertificateDeposit4.setFirstSurname(aRequest.readValueParam("@i_p_apellido4"));</li>
		<li>wBeneficiaryCertificateDeposit4.setRelation(aRequest.readValueParam("@i_parentesco4"));</li>
		<li>wBeneficiaryCertificateDeposit4.setPercentage(Double.parseDouble(aRequest.readValueParam("@i_porcentaje4")));</li>
		<li>wBeneficiaryCertificateDeposit4.setLastSurname(aRequest.readValueParam("@i_s_apellido4"));</li>

		<li>wCertificateDepositRequest.setBeneficiaryCertificateDeposit1(wBeneficiaryCertificateDeposit1);</li>
		<li>wCertificateDepositRequest.setBeneficiaryCertificateDeposit2(wBeneficiaryCertificateDeposit2);</li>
		<li>wCertificateDepositRequest.setBeneficiaryCertificateDeposit3(wBeneficiaryCertificateDeposit3);</li>
		<li>wCertificateDepositRequest.setBeneficiaryCertificateDeposit4(wBeneficiaryCertificateDeposit4);</li>
	</ul>
	<b>
		@return
	    -ParametrosDeSalida-
	</b>    
	<ul>
		<li>CertificateDepositResponse wCertificateDepositResponse = new CertificateDepositResponse();</li>
		<li>wCertificateDepositResponse.setOperationNumber("010DPFDESACOPLADO10000");</li>
		<li>wCertificateDepositResponse.setReturnCode(0);</li>
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
	public CertificateDepositResponse aplicationOpenningCertificateDeposit(CertificateDepositRequest cashiersCheck)throws CTSServiceException, CTSInfrastructureException;
}
