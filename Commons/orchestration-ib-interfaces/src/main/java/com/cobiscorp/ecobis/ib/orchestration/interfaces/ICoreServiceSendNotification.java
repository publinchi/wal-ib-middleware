package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationResponse;

/**
 * This interface contains the methods needed to get information of credit
 * cards.
 * 
 * @author eortega
 * @since Agu 14, 2014
 * @version 1.0.0
 */
public interface ICoreServiceSendNotification {
	/**
	 * Send Notification.
	 * 
		<b>
           @param
           -ParametrosDeEntrada
       </b>        
       <ul>
          <li>Client client = new Client();</li>
          <li>NotificationRequest notificationRequest = new NotificationRequest();</li>
		  <li>Notification notification = new Notification();</li>
		  <li>NotificationDetail notificationDetail = new NotificationDetail();</li>
		  <li>Product originProduct = new Product();</li>
		  <li>Currency currency = new Currency();</li>
		  <li>client.setIdCustomer(anOriginalRequest.readValueParam("@s_cliente"));</li>
		  <li>client.setLogin(anOriginalRequest.readValueParam("@i_login"));</li>
		  <li>notification.setNotificationType("N1");</li>
		  <li>notification.setMessageType("F");</li>
		  <li>notificationDetail.setEmailClient(anOfficer.getOfficer().getOfficerEmailAdress());</li>
		  <li>notificationDetail.setEmailOficial(anOfficer.getOfficer().getAcountEmailAdress());</li>
		  <li>notificationDetail.setProductId("18");</li>
		  <li>notificationDetail.setAccountNumberDebit(aProcedureRequest.readValueParam("@i_cta"));</li>
		  <li>notificationDetail.setCurrencyId1(aProcedureRequest.readValueParam("@i_mon"));</li>
		  <li>notificationRequest.setCodeTransactionalIdentifier(aProcedureRequest.readValueParam("@t_trn"));</li>
		  <li>notificationRequest.setCulture(aProcedureRequest.readValueParam("@s_culture"));</li>
		  <li>notificationRequest.setOfficeCode(Integer.parseInt(aProcedureRequest.readValueParam("@s_ofi")));</li>
		  <li>notificationRequest.setRole(Integer.parseInt(aProcedureRequest.readValueParam("@s_rol")));</li>
		  <li>notificationRequest.setSessionIdCore(aProcedureRequest.readValueParam("@s_ssn"));</li>
		  <li>notificationRequest.setSessionIdIB(aProcedureRequest.readValueFieldInHeader("sessionId"));</li>
		  <li>notificationRequest.setTerminal(aProcedureRequest.readValueFieldInHeader("term"));</li>
		  <li>notificationRequest.setUserBv(aProcedureRequest.readValueParam("@s_user"));</li>
		  <li>originProduct.setProductType(Integer.parseInt(aProcedureRequest.readValueParam("@i_prod")));</li>
		  <li>originProduct.setProductNumber(aProcedureRequest.readValueParam("@i_cta"));</li>
		  <li>currency.setCurrencyId(Integer.parseInt(aProcedureRequest.readValueParam("@i_mon")));</li>
		  <li>notificationRequest.setNotification(notification);</li>
		  <li>notificationRequest.setNotificationDetail(notificationDetail);</li>
		  <li>notificationRequest.setOriginalRequest(aProcedureRequest);</li>
		  <li>notificationRequest.setOriginProduct(originProduct);</li>
       </ul>
       <b>
          @return
          -ParametrosDeSalida-
       </b>    
       <ul>
         <li>NotificationResponse notificationResponse = new NotificationResponse();</li>
         <li>notificationResponse.setSuccess(true)</li>
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
	NotificationResponse sendNotification(NotificationRequest notification) throws CTSServiceException, CTSInfrastructureException;

}
