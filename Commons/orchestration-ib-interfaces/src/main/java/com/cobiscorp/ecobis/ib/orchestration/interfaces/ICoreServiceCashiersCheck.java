/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.CashiersCheckRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CashiersCheckResponse;

/**
 * @author jveloz
 *
 */
public interface ICoreServiceCashiersCheck {
	
	/**
	 * 
	 *   
	 *   <b>Solicitud de cheque de gerencia.</b>
	 *   
    <b>
	   @param
	  -Parametros de entrada
	</b>
	  
	<ul>
		<li>CashiersCheckRequest <b>wManagerCheckRequest</b> =new CashiersCheckRequest();</li>
		
		<li>Entity wEntity = new Entity();</li>
		<li>wEntity.setCodCustomer(Integer.parseInt(aRequest.readValueParam("@i_cod_cliente")));</li>
		<li>wEntity.setEnte(Integer.parseInt(aRequest.readValueParam("@i_ente")));</li>
		
		<li>Currency wCurrency = new Currency();</li>
		<li>wCurrency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));</li>
		
		<li>Product wProduct=new Product();</li>
		<li>wProduct.setProductNumber(aRequest.readValueParam("@i_cta"));</li>
		<li>wProduct.setProductType(Integer.parseInt(aRequest.readValueParam("@i_prod")));</li>
		<li>wProduct.setCurrency(wCurrency);</li>
		<li>wProduct.setProductAlias(aRequest.readValueParam("@i_producto"));;</li>
		
		<li>CashiersCheck wManagerCkeck =new CashiersCheck();</li>
		<li>wManagerCkeck.setAmount(Double.parseDouble(aRequest.readValueParam("@i_monto")));</li>
		<li>wManagerCkeck.setBeneficiary(aRequest.readValueParam("@i_beneficiario"));</li>
		<li>wManagerCkeck.setBeneficiaryId(aRequest.readValueParam("@i_code_ben"));</li>
		<li>wManagerCkeck.setBeneficiaryTypeId(aRequest.readValueParam("@i_tipo_id_ben"));//</li>
		<li>wManagerCkeck.setBeneficiaryId(aRequest.readValueParam("@i_id_ben"));</li>
		<li>wManagerCkeck.setAuthorizedPhoneNumber(aRequest.readValueParam("@i_tel_benef"));</li>
		<li>wManagerCkeck.setDestinationOfficeId(Integer.parseInt(aRequest.readValueParam("@i_ofi_destino")));</li>
		<li>wManagerCkeck.setAuthorizedTypeId(aRequest.readValueParam("@i_retira_tipo_id"));</li>
		<li>wManagerCkeck.setAuthorizedPhoneNumber(aRequest.readValueParam("@i_retira_telef"));</li>
		<li>wManagerCkeck.setAuthorizedId(aRequest.readValueParam("@i_retira_id"));</li>
		<li>wManagerCkeck.setAuthorized(aRequest.readValueParam("@i_retira_nombre"));</li>
		<li>wManagerCkeck.setEmail(aRequest.readValueParam("@i_retira_correo"));</li>
		<li>wManagerCkeck.setPurpose(aRequest.readValueParam("@i_proposito"));</li>
		
		<li>wManagerCheckRequest.setUserName(aRequest.readValueParam("@i_login"));</li>
		<li>wManagerCheckRequest.setProduct(wProduct);</li>
		<li>wManagerCheckRequest.setAuthorizationRequired(aRequest.readValueParam("@i_doble_autorizacion"));</li>
		<li>wManagerCheckRequest.setManagerCheck(wManagerCkeck);</li>
		<li>wManagerCheckRequest.setEntity(wEntity);</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida-
    </b>
    <ul>
		<b>if (cashiersCheck.getUserName() == "testCts")</b>
		<li>aCashiersCheckResponse.setAuthorizationRequired("N");</li>
		<li>aCashiersCheckResponse.setBatchId(116);</li>
		<li>aCashiersCheckResponse.setBranchSSN(0);</li>
		<li>aCashiersCheckResponse.setReference(657508980);</li>
		<li>aCashiersCheckResponse.setConditionId(null);</li>
		<b>else</b>
		<b>if (cashiersCheck.getUserName() == "testCtsEmp")</b>
		<li>aCashiersCheckResponse.setAuthorizationRequired("N");</li>
		<li>aCashiersCheckResponse.setBatchId(120);</li>
		<li>aCashiersCheckResponse.setBranchSSN(0);</li>
		<li>aCashiersCheckResponse.setReference(657513221);</li>
		<li>aCashiersCheckResponse.setConditionId(null);</li>
		<b>else</b>
		<li>aCashiersCheckResponse.setAuthorizationRequired("N");</li>
		<li>aCashiersCheckResponse.setBatchId(119);</li>
		<li>aCashiersCheckResponse.setBranchSSN(0);</li>
		<li>aCashiersCheckResponse.setReference(657511401);</li>
		<li>aCashiersCheckResponse.setConditionId(null);</li>
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
	public CashiersCheckResponse aplicationCashiersCheck(CashiersCheckRequest cashiersCheck)throws CTSServiceException, CTSInfrastructureException;
}
