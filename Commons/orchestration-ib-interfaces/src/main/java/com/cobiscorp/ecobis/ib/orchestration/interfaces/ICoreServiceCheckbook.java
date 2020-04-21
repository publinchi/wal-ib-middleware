package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CheckBookPreAuthRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CheckBookPreAuthResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CheckbookRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CheckbookResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CheckbookSuspendResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CheckbookValidateSuspendRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CheckbookValidateSuspendResponse;
import com.cobiscorp.ecobis.ib.application.dtos.NextLaborDayRequest;
import com.cobiscorp.ecobis.ib.application.dtos.NextLaborDayResponse;
import com.cobiscorp.ecobis.ib.application.dtos.NoPaycheckOrderRequest;
import com.cobiscorp.ecobis.ib.application.dtos.RequestCheckbookRequest;
import com.cobiscorp.ecobis.ib.application.dtos.RequestCheckbookResponse;
import com.cobiscorp.ecobis.ib.application.dtos.TypesOfCheckbookRequest;
import com.cobiscorp.ecobis.ib.application.dtos.TypesOfCheckbookResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ValidationAccountsRelationsRequest;
/** 
 * Esta interfaz contiene metodos necesarios para obtener informaci&oacuten de chequeras, solicitar chequera y suspension de chequera.
 * 
 * */
public interface ICoreServiceCheckbook {
	/**
	 * 
	 *   
	 *   <b>Consulta Chequera.</b>
	 *   
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>CheckbookRequest CheckbookReq = new CheckbookRequest();</li>
		
		<li>Product product  = new Product();</li>
	    <li>product.setProductNumber(aRequest.readValueParam("@i_cta"));</li>
		<li>product.setProductType(Integer.parseInt(aRequest.readValueParam("@i_prod")));</li>
		
		<li>Currency currency = new Currency();</li>
		<li>currency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));</li>
		
		<li>CheckbookReq.setEjec(aRequest.readValueParam("@t_ejec"));</li>
		<li>CheckbookReq.setRty(aRequest.readValueParam("@t_rty"));</li>
		<li>CheckbookReq.setCodeTransactionalIdentifier(aRequest.readValueParam("@t_trn"));</li>
		<li>CheckbookReq.setProductId(product);</li>
		<li>CheckbookReq.setCurrency(currency);</li>
		<li>CheckbookReq.setUserName(aRequest.readValueParam("@i_login"));</li>
		<li>CheckbookReq.setProductNumber(product);</li>
		<li>CheckbookReq.setMode(Integer.parseInt(aRequest.readValueParam("@i_modo")));</li>
		<li>CheckbookReq.setSequential(Integer.parseInt(aRequest.readValueParam("@i_sec")));</li>
		<li>CheckbookReq.setDateFormatId(Integer.parseInt(aRequest.readValueParam("@i_formato_fecha")));</li>
	</ul>		
	<b>
		@return
		-ParametrosDeSalida- 
	</b>
	<ul>
		<li>Checkbook aCheckbook = new Checkbook();</li>
		<li>List<Checkbook> aCheckbookCollection = new ArrayList<Checkbook>(); </li>
		<li>CheckbookResponse aCheckbookResponse = new CheckbookResponse();</li>
		<li>aCheckbook.setCreationDate("01/01/2014");</li>
		<li>aCheckbook.setCreationOffice("02/02/2014");</li>
		<li>aCheckbook.setDeliveryDate("03/03/2014");</li>
		<li>aCheckbook.setInitialCheck(1);</li>
		<li>aCheckbook.setNumberOfChecks(50);</li>
		<li>aCheckbook.setPrintShippingDate("04/04/2014");</li>
		<li>aCheckbook.setReceiptOfficeDate("05/04/2014");</li>
		<li>aCheckbook.setReceiptPrintingDate("06/04/2014");</li>
		<li>aCheckbook.setReceptionOffice("NN");</li>
		<li>aCheckbook.setRunNumber(2);</li>
		<li>aCheckbook.setSequential(3);</li>
		<li>aCheckbook.setStatus("V");</li>
		<li>aCheckbook.setType("C");</li>		
	</ul>
	<b>
		@throws
		-ManejoDeErrores
	</b>
	<ul>
		<li>CTSServiceException</li>
		<li>CTSInfrastructureException</li>
	</ul>		
	**/
	public CheckbookResponse getCheckbook(CheckbookRequest aCheckbookRequest) throws CTSServiceException, CTSInfrastructureException;
	
	/**  
	 * 
	 * 
	 * <b>Valida la relaci&oacuten del cliente con la cuenta.</b>
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>	  
	<ul>
		<li>ValidationAccountsRelationsRequest validationAccountsRelationsRequest = new ValidationAccountsRelationsRequest();</li>	
		<li>validationAccountsRelationsRequest.setEntityId(Integer.parseInt(anOriginalRequest.readValueParam("@i_cliente")));</li>
		<li>validationAccountsRelationsRequest.setProductNumber(anOriginalRequest.readValueParam("@i_cta"));</li>
		<li>validationAccountsRelationsRequest.setOriginalRequest(anOriginalRequest);</li>
	</ul>		
	<b>
		@return
		-ParametrosDeSalida- 
	</b>
	<ul>
		<li>IProcedureResponse pResponse = new ProcedureResponseAS();</li>  
		<li>pResponse.setReturnCode(0);</li>
	</ul>
	<b>
		@throws
		-ManejoDeErrores
	</b>
	<ul>
		<li>CTSServiceException</li>
		<li>CTSInfrastructureException</li>
	</ul>		
	**/
	public IProcedureResponse validateAccountsRelations(ValidationAccountsRelationsRequest aValidationRequest) throws CTSServiceException, CTSInfrastructureException;
	

	/**  
	 * 
	 * 
	 * <b>Solicitud de chequera.</b>
	 * 
	<b>
	   @param
	  -ParametrosDeEntrada	  
	</b>
	<ul>
		<li>RequestCheckbookRequest <b>requestCheckbook</b> = new RequestCheckbookRequest();</li>
		
		<li>Checkbook checkbook  = new Checkbook();</li>
		<li>checkbook.setNumberOfChecks(Integer.parseInt(aRequest.readValueParam("@i_nchqs")));</li>
		<li>checkbook.setType(aRequest.readValueParam("@i_tchq"));</li>
		<li>checkbook.setDeliveryDate(aRequest.readValueParam("@i_dia_entrega"));</li>
		
		<li>Product product = new Product();</li>
		<li>product.setProductNumber(aRequest.readValueParam("@i_cta"));</li>
		
		<li>Currency currency = new Currency();</li>
		<li>currency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));</li>
		
		<li>ProductBanking productBanking = new ProductBanking();</li>
		<li>productBanking.setId(Integer.parseInt(aRequest.readValueParam("@i_prod")));</li>
		
		<li>Client client = new Client();</li>
		<li>client.setLogin(aRequest.readValueParam("@i_login"));</li>
		
		<li>CashiersCheck cashiersCheck = new CashiersCheck();</li>
		<li>cashiersCheck.setAmount(Double.parseDouble(aRequest.readValueParam("@i_monto")));</li>
		<li>cashiersCheck.setPurpose(aRequest.readValueParam("@i_proposito"));</li>
		
		<li>requestCheckbook.setCheckbook(checkbook);</li>
		<li>requestCheckbook.setCurrency(currency);</li>
		<li>requestCheckbook.setCheckbookArt(aRequest.readValueParam("@i_nombre_arte"));</li>
		<li>requestCheckbook.setDeliveryName(aRequest.readValueParam("@i_nombre_entrega"));</li>
		<li>requestCheckbook.setDeliveyId(aRequest.readValueParam("@i_id_entrega"));</li>
		<li>requestCheckbook.setOfficeDelivery(Integer.parseInt(aRequest.readValueParam("@i_ofientr")));</li>
		<li>requestCheckbook.setOperation(aRequest.readValueParam("@i_operacion"));</li>
		<li>requestCheckbook.setProduct(product);</li>
		<li>requestCheckbook.setProductId(productBanking);</li>
		<li>requestCheckbook.setTypeId(aRequest.readValueParam("@i_tipo_id"));</li>
		<li>requestCheckbook.setAmount(cashiersCheck);</li>
		<li>requestCheckbook.setUserName(client);</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida- 
	</b>
	<ul>
		<li>RequestCheckbookResponse aCheckbookResponse = new RequestCheckbookResponse();	</li>	
		<li>aCheckbookResponse.setTypeCheckbook("CHEQUERA DEFINITIVA DOLARES");</li>
		<li>aCheckbookResponse.setReturnCode(0);</li>
	</ul>
	<b>
		@throws
		-ManejoDeErrores
	</b>
	<ul>
		<li>CTSServiceException</li>
		<li>CTSInfrastructureException</li>
	</ul>
	 **/

	public RequestCheckbookResponse getRequestCheckbook(RequestCheckbookRequest aRequestCheckbookRequest) throws CTSServiceException, CTSInfrastructureException;
	/**
	 * 
	 * 
	 * <b>Obtiene los tipos de chequeras.</b>
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>
	<ul>
		<li>TypesOfCheckbookRequest typesOfCheckbookRequest = new TypesOfCheckbookRequest();</li>
		<li>Currency currency = new Currency();</li>
		<li>currency.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_moneda")));</li>
		<li>typesOfCheckbookRequest.setCurrency(currency);</li>
		<li>typesOfCheckbookRequest.setOperation(aRequest.readValueParam("@i_operacion"));</li>	
	</ul>
	<b>
		@return
		-ParametrosDeSalida- 
	</b>
	<ul>
		<li>TypesOfCheckbookResponse <b>aTypesOfCheckbookResponse</b> = new TypesOfCheckbookResponse();</li>
		
		<li>List<TypesOfCheckbook> aTypesOfCheckbookCollection = new ArrayList<TypesOfCheckbook>();</li> 
				
		<li>Type type = new Type();</li>
		<li>type.setIdType("3");</li>
		<li>type.setType("V1");	</li>
		
		<li>Parameters name = new Parameters();</li>
		<li>name.setName("CHQ VOUCHER S/C - OTRO PROVEEDOR");</li>
		
		<li>CheckBookPreAuth state = new CheckBookPreAuth();</li>
		<li>state.setStatus("V1");</li>
		
		<li>Currency currency = new Currency();</li>
		<li>currency.setCurrencyId(8);</li>
		
		<li>TypesOfCheckbook aTypesOfCheckbook = new TypesOfCheckbook();</li>
		<li>aTypesOfCheckbook.setType(type);</li>
		<li>aTypesOfCheckbook.setName(name);</li>
		<li>aTypesOfCheckbook.setArt("N");</li>
		<li>aTypesOfCheckbook.setCustomArt("N");</li>
		<li>aTypesOfCheckbook.setQuantity("S");</li>
		<li>aTypesOfCheckbook.setState(state);</li>
		<li>aTypesOfCheckbook.setTime(8);</li>
		<li>aTypesOfCheckbook.setCurrency(currency);</li>
		<li>aTypesOfCheckbook.setAmount("COLON");</li>
		
		<li>aTypesOfCheckbookCollection.add(aTypesOfCheckbook);</li>		
		<li>aTypesOfCheckbookResponse.setTypesOfCheckbook(aTypesOfCheckbookCollection);</li>
		<li>aTypesOfCheckbookResponse.setReturnCode(0);</li>
	</ul>
	<b>
		@throws
		-ManejoDeErrores
	</b>
	<ul>
		<li>CTSServiceException</li>
		<li>CTSInfrastructureException</li>
	</ul>
	**/
	public TypesOfCheckbookResponse getTypesOfCheckbook(TypesOfCheckbookRequest aTypesOfCheckbookRequest) throws CTSServiceException, CTSInfrastructureException;
	
	
	/**
	 * 
	 * 
	 * <b>Consulta siguiente dia laborable.</b>
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>
	  
	<ul>
		<li>NextLaborDayRequest nextLaborDayRequest = new NextLaborDayRequest();</li>
		<li>LaborDay laborDay  = new LaborDay();</li>
		<li>Office office = new Office();</li>
		
		<li>laborDay.setDate(aRequest.readValueParam("@i_fecha"));</li>
		<li>laborDay.setDay(Integer.parseInt(aRequest.readValueParam("@i_dias")));</li>		
		<li>office.setId(Integer.parseInt(aRequest.readValueParam("@i_oficina")));</li>
		
		<li>nextLaborDayRequest.setCommercial(aRequest.readValueParam("@i_comercial"));</li>
		<li>nextLaborDayRequest.setLaborDay(laborDay);</li>
		<li>nextLaborDayRequest.setOfficeId(office);</li>	
	</ul>
	<b>
		@return
		-ParametrosDeSalida- 
	</b>
	<ul>
		<li>LaborDay aNextLaborDay = new LaborDay();</li>
		<li>List<LaborDay> aLaborDayCollection = new ArrayList<LaborDay>();</li> 
		<li>NextLaborDayResponse aNextLaborDayResponse = new NextLaborDayResponse();</li>	
		
		<li>aNextLaborDay.setDate("03/02/2014");</li>
		<li>aNextLaborDay.setDay(02);</li>
		<li>aNextLaborDay.setMonth(03);</li>
		<li>aNextLaborDay.setYear(2014);	</li>
		
		<li>aLaborDayCollection.add(aNextLaborDay);</li>
		<li>aNextLaborDayResponse.setNextLaborDay(aLaborDayCollection);</li>
		<li>aNextLaborDayResponse.setReturnCode(0);</li>
	</ul>
	<b>
		@throws
		-ManejoDeErrores
	</b>
	<ul>
		<li>CTSServiceException</li>
		<li>CTSInfrastructureException</li>
	</ul>
	**/
	
	public NextLaborDayResponse getNextLaborDay(NextLaborDayRequest aNextLaborDayRequest) throws CTSServiceException, CTSInfrastructureException;
	
	/**
	 * 
	 * 
	 * <b>Pre-autorizaci&oacuten de cheques desde BV.</b>
	<b>
		@param
		-ParametrosDeEntrada
	</b>
	<ul>
		<li>CheckBookPreAuthRequest aCheckBookPreAuthRequest=new CheckBookPreAuthRequest();</li> 
		<li>CheckBookPreAuth wManagerCkeck =new CheckBookPreAuth(); </li>	  	     
		<li>aCheckBookPreAuthRequest.setAccount(aRequest.readValueParam("@i_cta"));</li>
		<li>aCheckBookPreAuthRequest.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));</li>
		<li>aCheckBookPreAuthRequest.setCheckId(Integer.parseInt(aRequest.readValueParam("@i_cheque")));</li>
		<li>aCheckBookPreAuthRequest.setAmount(Double.parseDouble(aRequest.readValueParam("@i_valor")));</li>
	    <li>aCheckBookPreAuthRequest.setBeneficiary(aRequest.readValueParam("@i_beneficiario"));</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida- 
	</b>
	<ul>
		<li>CheckBookPreAuth aCheckBookPreAuth = new CheckBookPreAuth();</li>
		<li>List<CheckBookPreAuth> aCheckbookPreCollection = new ArrayList<CheckBookPreAuth>();</li> 
		<li>CheckBookPreAuthResponse aCheckBookPreAuthResponse = new CheckBookPreAuthResponse();</li>
		
		<li>aCheckBookPreAuth.setStatus("H");</li>
		
		<li>aCheckbookPreCollection.add(aCheckBookPreAuth);</li>
		<li>aCheckBookPreAuthResponse.setList(aCheckbookPreCollection);</li>
		<li>aCheckBookPreAuthResponse.setReturnCode(0);</li>
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
	public CheckBookPreAuthResponse getCheckBookPreAuth(CheckBookPreAuthRequest aCheckBookPreAuthRequest) throws CTSServiceException, CTSInfrastructureException;
	
	/**
	 *
	 * 
	 * <b>Valida el estado de los cheques.</b>
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>
	<ul>
		<li>CheckbookValidateSuspendRequest validateSuspendRequest = new CheckbookValidateSuspendRequest();</li>
		<li>validateSuspendRequest.setAccount(anOriginalRequest.readValueParam("@i_cuenta"));</li>
		<li>validateSuspendRequest.setInitialCheck(Integer.parseInt(anOriginalRequest.readValueParam("@i_cheque_inicio")));</li>
		<li>validateSuspendRequest.setNumberOfChecks(Integer.parseInt(anOriginalRequest.readValueParam("@i_cheque_fin")));</li>
		<li>validateSuspendRequest.setOriginalRequest(anOriginalRequest);</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida- 
	</b>
	<ul>
		<li>Check check = new Check();</li>
		<li>List<Check> listChecks = new ArrayList<Check>();</li>
		<li>CheckbookValidateSuspendResponse checkbookValidateSuspendResponse = new CheckbookValidateSuspendResponse();</li>
		
		<li>check.setCheckNumber("");</li>
		<li>BigDecimal bg = new BigDecimal("0");</li>
		<li>check.setAmount(bg);</li>
		<li>check.setStatus("");</li>
		
		<li>listChecks.add(check);</li>
		
		<li>checkbookValidateSuspendResponse.setChecks(listChecks);</li>
		<li>checkbookValidateSuspendResponse.setReturnCode(0);</li>
	</ul>
	<b>
		@throws
		-ManejoDeErrores
	</b>
	<ul>
		<li>CTSServiceException</li>
		<li>CTSInfrastructureException</li>
	</ul>
	**/
	public CheckbookValidateSuspendResponse validateSuspendCheckBook(CheckbookValidateSuspendRequest aValidateSuspendRequest) throws CTSServiceException, CTSInfrastructureException;
	 
	/**  
	 * 
	 * 
	 * <b>Suspensi&oacuten de cheque.</b>
	 * 
	<b>
		@param
		-ParametrosDeEntrada
	</b>
	<ul>  
	    <li>NoPaycheckOrderRequest wNoPaycheckOrderRequest = new NoPaycheckOrderRequest();</li> 
		<li>wNoPaycheckOrderRequest.setTypeNotif(aRequest.readValueParam("@i_tipo_notif"));</li>
		<li>wNoPaycheckOrderRequest.setProductAbbreviation(aRequest.readValueParam("@i_producto"));</li>
		<li>wNoPaycheckOrderRequest.setProductId(Integer.parseInt(aRequest.readValueParam("@i_prod")));</li>
		<li>wNoPaycheckOrderRequest.setNumberOfChecks(Integer.parseInt(aRequest.readValueParam("@i_num_cheque")));</li>
		<li>wNoPaycheckOrderRequest.setCurrencyId(Integer.parseInt(aRequest.readValueParam("@i_mon")));</li>
		<li>wNoPaycheckOrderRequest.setUserName(aRequest.readValueParam("@i_login"));</li>
		<li>wNoPaycheckOrderRequest.setAuthorizationRequired(aRequest.readValueParam("@i_doble_autorizacion"));</li>
		<li>wNoPaycheckOrderRequest.setAccount(aRequest.readValueParam("@i_cta"));</li>
		<li>wNoPaycheckOrderRequest.setConcept(aRequest.readValueParam("@i_concepto"));</li>
		<li>wNoPaycheckOrderRequest.setInitialCheck(Integer.parseInt(aRequest.readValueParam("@i_cheque_ini")));</li>
		<li>wNoPaycheckOrderRequest.setReason(aRequest.readValueParam("@i_causa"));</li>
	</ul>
	<b>
		@return
		-ParametrosDeSalida- 
	</b>
	<ul>
		<li>CheckbookSuspendResponse cbSuspendResponse = new CheckbookSuspendResponse();</li>
		
		<li>NoPaycheckOrder noPaycheckOrder = new NoPaycheckOrder();</li>
		<li>noPaycheckOrder.setInitialCheck(8);</li>
		<li>noPaycheckOrder.setFinalCheck(1);</li>
		<li>noPaycheckOrder.setAccount("10410108275405315");</li>
		<li>noPaycheckOrder.setReason("1");</li>
		<li>noPaycheckOrder.setSuspensionDate("2013/02/01");</li>
		<li>noPaycheckOrder.setReference(668059843);</li>
		<li>noPaycheckOrder.setCommission(0.0);</li>
		
		<li>List<NoPaycheckOrder>listNoPaycheckOrder = new ArrayList<NoPaycheckOrder>();</li>
		<li>listNoPaycheckOrder.add(noPaycheckOrder);</li>
		
		<li>cbSuspendResponse.setReference(65645481);</li>
		<li>cbSuspendResponse.setListNoPaycheckOrder(listNoPaycheckOrder);</li>
		<li>cbSuspendResponse.setReturnCode(0);</li>
	</ul>
	<b>
		@throws
		-ManejoDeErrores
	</b>
	<ul>
		<li>CTSServiceException</li>
		<li>CTSInfrastructureException</li>
	</ul>
		*
		**/
	public CheckbookSuspendResponse suspendChecks(NoPaycheckOrderRequest aNoPaycheckOrderRequest) throws CTSServiceException, CTSInfrastructureException;
	
}