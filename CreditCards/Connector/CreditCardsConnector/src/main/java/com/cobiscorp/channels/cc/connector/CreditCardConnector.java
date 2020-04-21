package com.cobiscorp.channels.cc.connector;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.channels.cc.connector.dto.ConnectionParameters;
import com.cobiscorp.channels.cc.connector.dto.CreditCard;
import com.cobiscorp.channels.cc.connector.dto.CreditCardRequest;
import com.cobiscorp.channels.cc.connector.dto.CreditCardResponse;
import com.cobiscorp.channels.cc.connector.dto.Movement;
import com.cobiscorp.channels.cc.connector.dto.MovementsResponse;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.ICSPExecutorConnector;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseWSAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;

@Component(name = "CreditCardConnector", immediate = false)
@Service({ com.cobiscorp.cobis.csp.services.ICSPExecutorConnector.class })
@Properties({ @Property(name = "service.description", value = "CreditCardConnector"), @Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
	@Property(name = "service.identifier", value = "CreditCardConnector") })  
public class CreditCardConnector implements ICSPExecutorConnector {

	private static final String EXECUTION_RESULT = "executionResult";
	private static final ILogger logger = LogFactory.getLogger(CreditCardConnector.class);
	private static String ADMIN_WSDL;
	private static String SERVICE_URL;
	private static String SERVICE_LOCAL_PART;
	private static Integer CONNECTIONTIMEOUT;
	private static Integer READTIMEOUT;

	//@Reference(bind = "setCreditCardServiceClient", unbind = "unsetCreditCardServiceClient", cardinality = ReferenceCardinality.MANDATORY_UNARY, policy = ReferencePolicy.DYNAMIC)
	//private ICreditCardServiceClient creditCardServiceClient;

	/*
	protected ICreditCardServiceClient unsetAdminServiceClient(ICreditCardServiceClient creditCardServiceClient) {
		return creditCardServiceClient = null;
	}

	protected void setCreditCardServiceClient(ICreditCardServiceClient creditCardServiceClient) {
		this.creditCardServiceClient = creditCardServiceClient;
	}
	protected ICreditCardServiceClient unsetCreditCardServiceClient(ICreditCardServiceClient creditCardServiceClient) {
		return creditCardServiceClient = null;
	}
	 */


	public void loadConfiguration(IConfigurationReader configurationReader) {
/*		
		java.util.Properties properties = configurationReader.getProperties("//property");
		ADMIN_WSDL = properties.getProperty("adminWSDL"); 
		SERVICE_URL = properties.getProperty("serviceURL");
		SERVICE_LOCAL_PART = properties.getProperty("serviceLocalPart");
		CONNECTIONTIMEOUT = Integer.parseInt(properties.getProperty("connectionTimeout"));
		READTIMEOUT = Integer.parseInt(properties.getProperty("readTimeout"));
		
		file: C:\cobishome\CTS_MF\services-as\orchestrator\credit-card-connector.xml
*/		
	}

	
	// 1ro que se ejecuta
	public IProcedureResponse transformAndSend(IProcedureRequest anOriginalRequest) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Inicia metodo transformAndSend en SimpleConnector");
		}

		String operation = anOriginalRequest.readValueParam("@i_tipo_operacion");
		IProcedureResponse procedureResponse = new ProcedureResponseWSAS();
		
		if (Constants.QUERYCREDITCARDS.equals(operation)) {
			procedureResponse = queryCreditCards(anOriginalRequest);
		} else if (Constants.QUERYMOVEMENTS.equals(operation)) {
			procedureResponse = queryMovements(anOriginalRequest);
		}
		if (logger.isDebugEnabled()) {			
			logger.logDebug("Finaliza metodo transformAndSend en SimpleConnector");
			logger.logDebug("transformAndSend - procedureResponse: " + procedureResponse);
		}
		return procedureResponse;
	}

	public IProcedureResponse queryCreditCards(IProcedureRequest anOriginalRequest) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Inicia metodo queryCreditCards en CreditCardConnector");
		}

		CreditCardResponse creditCardResponse = new CreditCardResponse();

		CreditCardRequest creditCardRequest =  createQueryCreditCardRequest(anOriginalRequest);
		try {
			ServiceClient serviceClient = new ServiceClient();
			creditCardResponse = serviceClient.queryCreditCards(creditCardRequest);
			
			if (logger.isInfoEnabled()){
				logger.logInfo("cifrarResponseMap:" + creditCardResponse);
			}

		} catch (Exception e) {
			logger.logError(e);
		}
		
		IProcedureResponse response = transformCreditCardResponse(creditCardResponse); 
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Finaliza metodo queryCreditCards en CreditCardConnector");
			logger.logDebug(response.getProcedureResponseAsString());
		}
		return response;
	}
		
	private IProcedureResponse transformCreditCardResponse(CreditCardResponse creditCardResponse){
	   
		IProcedureResponse wProcedureResponse = new ProcedureResponseWSAS();
		if (logger.isDebugEnabled()) logger.logDebug("<<<Transform Procedure Response Cards>>>");

		wProcedureResponse.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
		wProcedureResponse.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
		
		// Add Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("PRODUCTNUMBER", ICTSTypes.SQLINT2, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("PRODUCTID", ICTSTypes.SQLINT2, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("CURRENCYNAME", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("PRODUCTNAME", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("ACCOUNTNAME", ICTSTypes.SQLVARCHAR, 60));

		metaData.addColumnMetaData(new ResultSetHeaderColumn("ACCOUNTINGBALANCE", ICTSTypes.SQLMONEY, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("DRAWALANCE", ICTSTypes.SQLMONEY, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("AVAILABLEBALANCE", ICTSTypes.SQLMONEY, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("CAPITALBALANCE", ICTSTypes.SQLMONEY, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("EXPIRATIONDATE", ICTSTypes.SQLINT2, 20));

		metaData.addColumnMetaData(new ResultSetHeaderColumn("RATE", ICTSTypes.SQLINT2, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("CAPITALBALANCEMATURITY", ICTSTypes.SQLINT2, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("MONTHLYPAYMENTDAY", ICTSTypes.SQLINT2, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("STATUS", ICTSTypes.SQLINT2, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("NEXTPAYMENTDATE", ICTSTypes.SQLVARCHAR, 20));

		metaData.addColumnMetaData(new ResultSetHeaderColumn("NEXTPAYMENTVALUE", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("TOTALBALANCE", ICTSTypes.SQLINT2, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("PRODUCTTYPE", ICTSTypes.SQLINT2, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("ALIAS", ICTSTypes.SQLVARCHAR, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("CURRENCYID", ICTSTypes.SQLINT2, 20));
		
		for (CreditCard aCreditCardElem : creditCardResponse.getCreditCardsList()) {   
		   IResultSetRow row = new ResultSetRow();
		   row.addRowData(1,  new ResultSetRowColumnData(false, aCreditCardElem.getProductNumber()));       
		   row.addRowData(2,  new ResultSetRowColumnData(false, aCreditCardElem.getProductId()));       
		   row.addRowData(3,  new ResultSetRowColumnData(false, aCreditCardElem.getCurrencyName()));       
		   row.addRowData(4,  new ResultSetRowColumnData(false, aCreditCardElem.getProductName()));       
		   row.addRowData(5,  new ResultSetRowColumnData(false, aCreditCardElem.getProductName()));  
		   
		   row.addRowData(6,  new ResultSetRowColumnData(false, aCreditCardElem.getAccountingBalance().toString()));       
		   row.addRowData(7,  new ResultSetRowColumnData(false, aCreditCardElem.getDrawBalance().toString()));       
		   row.addRowData(8,  new ResultSetRowColumnData(false, aCreditCardElem.getAvailableBalance().toString()));       
		   row.addRowData(9,  new ResultSetRowColumnData(false, aCreditCardElem.getDrawBalance().toString()));       
		   row.addRowData(10,  new ResultSetRowColumnData(false, aCreditCardElem.getExpirationDate()));       
		   
		   row.addRowData(11,  new ResultSetRowColumnData(false, "0"));       
		   row.addRowData(12,  new ResultSetRowColumnData(false, "0"));       
		   row.addRowData(13,  new ResultSetRowColumnData(false, "0"));       
		   row.addRowData(14,  new ResultSetRowColumnData(false, "0"));       
		   row.addRowData(15,  new ResultSetRowColumnData(false, "0"));       
		   
		   row.addRowData(16,  new ResultSetRowColumnData(false, aCreditCardElem.getDrawBalance().toString()));       
		   row.addRowData(17,  new ResultSetRowColumnData(false, aCreditCardElem.getDrawBalance().toString()));       
		   row.addRowData(18,  new ResultSetRowColumnData(false, aCreditCardElem.getProductId()));       
		   row.addRowData(19,  new ResultSetRowColumnData(false, aCreditCardElem.getAliasName()));       
		   row.addRowData(20,  new ResultSetRowColumnData(false, aCreditCardElem.getCurrencyId()));                                           
		   data.addRow(row);
		} //for

		IResultSetBlock resultBlock1 = new ResultSetBlock(metaData, data);
		wProcedureResponse.addResponseBlock(resultBlock1);
				
		return wProcedureResponse;			
	}

	public CreditCardRequest createQueryCreditCardRequest(IProcedureRequest anOriginalRequest) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Inicia metodo createQueryRequest en CreditCardConnector");
		}
		CreditCardRequest creditCardRequest = new CreditCardRequest();
		creditCardRequest.setCustomerId(anOriginalRequest.readValueParam("@i_cedula"));

		ConnectionParameters connectionParameters =  new ConnectionParameters(SERVICE_URL, CONNECTIONTIMEOUT, READTIMEOUT);
		creditCardRequest.setConnectionParameters(connectionParameters);

		if (logger.isDebugEnabled()) {
			logger.logDebug("ADMIN_WSDL: " + ADMIN_WSDL);
			logger.logDebug("SERVICE URL: " + SERVICE_URL);
			logger.logDebug("SERVICE_LOCAL_PART: " + SERVICE_LOCAL_PART);
			logger.logDebug("CONNECTIONTIMEOUT: " + CONNECTIONTIMEOUT);
			logger.logDebug("READTIMEOUT: " + READTIMEOUT);
			logger.logDebug("creditCardRequest: " + creditCardRequest);
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("Finaliza metodo createQueryRequest en CreditCardConnector");
		}
		return creditCardRequest;
	}

	public IProcedureResponse queryMovements(IProcedureRequest anOriginalRequest) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Inicia metodo queryMovements en CreditCardConnector");
		}

		MovementsResponse movementsResponse = new MovementsResponse();
		
		CreditCardRequest creditCardRequest = createQueryMovementsCreditCardRequest(anOriginalRequest);
		try {
			ServiceClient serviceClient = new ServiceClient();
			movementsResponse = serviceClient.queryMovements(creditCardRequest);
			if (logger.isInfoEnabled()){
				logger.logInfo("cifrarResponseMap:" + movementsResponse);
			}

		} catch (Exception e) {
			logger.logError(e);
		}
		
		IProcedureResponse response = transformMovementsResponse(movementsResponse);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Finaliza metodo queryMovements en CreditCardConnector");
			logger.logDebug(response.getProcedureResponseAsString());
		}
		return response;
	}

	private IProcedureResponse transformMovementsResponse(MovementsResponse movementsResponse){

		IProcedureResponse wProcedureResponse = new ProcedureResponseWSAS();
		if (logger.isDebugEnabled()) logger.logDebug("<<<Transform Procedure Response Credit Card Movements >>>");

		wProcedureResponse.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
		wProcedureResponse.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
		
		// Add Header
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("ACCOUNT", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("CAUSE", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("CAUSEID", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("CONCEPT", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("CREDITSAMOUNT", ICTSTypes.SQLINT2, 20));
		
		metaData.addColumnMetaData(new ResultSetHeaderColumn("DEBITSAMOUNT", ICTSTypes.SQLINT2, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("ALTERNATECODE", ICTSTypes.SQLINT2, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("DESCRIPTION", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("REFERENCE", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("AMOUNT", ICTSTypes.SQLMONEY, 20));
		
		metaData.addColumnMetaData(new ResultSetHeaderColumn("ACCOUNTINGBALANCE", ICTSTypes.SQLMONEY, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("AVAILABLEBALANCE", ICTSTypes.SQLMONEY, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("DOCUMENTNUMBER", ICTSTypes.SQLINT2, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("PROCESSDATE", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("TRANSACTIONDATE", ICTSTypes.SQLVARCHAR, 60));
		
		metaData.addColumnMetaData(new ResultSetHeaderColumn("HOUR", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("IMAGE", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("INTERNATIONALCHECKSBALANCE", ICTSTypes.SQLINT2, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("LOCALCHECKSBALANCE", ICTSTypes.SQLINT2, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("NUMBEROFMOVEMENTS", ICTSTypes.SQLINT2, 20));
		
		metaData.addColumnMetaData(new ResultSetHeaderColumn("OFFICE", ICTSTypes.SQLINT2, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("OPERATIONTYPE", ICTSTypes.SQLINT2, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("OWNCHECKSBALANCE", ICTSTypes.SQLINT2, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("SEQUENTIAL", ICTSTypes.SQLINT2, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("SIGNDC", ICTSTypes.SQLVARCHAR, 60));
		
		metaData.addColumnMetaData(new ResultSetHeaderColumn("TOTALCHECKSBALANCE", ICTSTypes.SQLINT2, 20));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("TYPE", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("TYPEDC", ICTSTypes.SQLVARCHAR, 60));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("UNIQUESEQUENTIAL", ICTSTypes.SQLINT2, 20));
		
		for (Movement aMovementElem : movementsResponse.getMovementsList()) {   
		   IResultSetRow row = new ResultSetRow();

		   row.addRowData(1,  new ResultSetRowColumnData(false, aMovementElem.getTransactionDate()));
		   row.addRowData(2,  new ResultSetRowColumnData(false, aMovementElem.getDescription()));
		   row.addRowData(3,  new ResultSetRowColumnData(false, "0")); //aMovementElem.getOperationType()));
		   row.addRowData(4,  new ResultSetRowColumnData(false, aMovementElem.getReference()));
		   row.addRowData(5,  new ResultSetRowColumnData(false, "0")); //aMovementElem.getSignDC()));

		   row.addRowData(6,  new ResultSetRowColumnData(false, aMovementElem.getAmount().toString()));
		   row.addRowData(7,  new ResultSetRowColumnData(false, aMovementElem.getAccountingBalance().toString()));
		   row.addRowData(8,  new ResultSetRowColumnData(false, aMovementElem.getAvailableBalance().toString()));
		   row.addRowData(9,  new ResultSetRowColumnData(false, "0")); //aMovementElem.getSequential()));
		   row.addRowData(10,  new ResultSetRowColumnData(false, "0")); //aMovementElem.getAlternateCode()));

		   row.addRowData(11,  new ResultSetRowColumnData(false, aMovementElem.getHour()));
		   row.addRowData(12,  new ResultSetRowColumnData(false, "0")); //aMovementElem.getUniqueSequentia()));		   
		   row.addRowData(13,  new ResultSetRowColumnData(false, aMovementElem.getImage()));
/*
		   row.addRowData(1,  new ResultSetRowColumnData(false, aMovementElem.getAccount()));
		   row.addRowData(2,  new ResultSetRowColumnData(false, aMovementElem.getCause()));
		   row.addRowData(3,  new ResultSetRowColumnData(false, aMovementElem.getCauseId()));
		   row.addRowData(4,  new ResultSetRowColumnData(false, aMovementElem.getConcept()));
		   row.addRowData(5,  new ResultSetRowColumnData(false, aMovementElem.getCreditsAmount().toString()));
		   
		   row.addRowData(6,  new ResultSetRowColumnData(false, aMovementElem.getDebitsAmount().toString()));
		   
		   row.addRowData(13,  new ResultSetRowColumnData(false, "0")); //aMovementElem.getDocumentNumber().toString()));
		   row.addRowData(14,  new ResultSetRowColumnData(false, aMovementElem.getProcessDate()));
		   
		   row.addRowData(18,  new ResultSetRowColumnData(false, "0")); //aMovementElem.getInternationalChecksBalance()));
		   row.addRowData(19,  new ResultSetRowColumnData(false, "0")); //aMovementElem.getLocalChecksBalance()));
		   row.addRowData(20,  new ResultSetRowColumnData(false, "0")); //aMovementElem.getNumberOfMovements()));
		   
		   row.addRowData(21,  new ResultSetRowColumnData(false, "0")); //aMovementElem.getOffice()));
		   row.addRowData(23,  new ResultSetRowColumnData(false, "0")); //aMovementElem.getOwnChecksBalance()));
		   
		   row.addRowData(26,  new ResultSetRowColumnData(false, "0")); //aMovementElem.getTotalChecksBalance()));
		   row.addRowData(27,  new ResultSetRowColumnData(false, aMovementElem.getType()));
		   row.addRowData(28,  new ResultSetRowColumnData(false, aMovementElem.getTypeDC()));
*/		   		   
		   data.addRow(row);
		} //for

		IResultSetBlock resultBlock1 = new ResultSetBlock(metaData, data);
		wProcedureResponse.addResponseBlock(resultBlock1);
				
		return wProcedureResponse;
	}
	
	public CreditCardRequest createQueryMovementsCreditCardRequest(IProcedureRequest anOriginalRequest) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Inicia metodo createQueryRequest en CreditCardConnector");
		}
		CreditCardRequest creditCardRequest = new CreditCardRequest();
		creditCardRequest.setId(anOriginalRequest.readValueParam("@i_tarjeta_num"));

		ConnectionParameters connectionParameters =  new ConnectionParameters(SERVICE_URL, CONNECTIONTIMEOUT, READTIMEOUT);
		creditCardRequest.setConnectionParameters(connectionParameters);

		if (logger.isDebugEnabled()) {
			logger.logDebug("ADMIN_WSDL: " + ADMIN_WSDL);
			logger.logDebug("SERVICE URL: " + SERVICE_URL);
			logger.logDebug("SERVICE_LOCAL_PART: " + SERVICE_LOCAL_PART);
			logger.logDebug("CONNECTIONTIMEOUT: " + CONNECTIONTIMEOUT);
			logger.logDebug("READTIMEOUT: " + READTIMEOUT);
			logger.logDebug("creditCardRequest: " + creditCardRequest);
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("Finaliza metodo createQueryRequest en CreditCardConnector");
		}
		return creditCardRequest;
	}

	public IProcedureResponse processResponseProvider(Map<Object, Object> arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}


}
