package com.cobiscorp.simple.orchestrator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.IProvider;
import com.cobiscorp.cobis.csp.services.IThreshold;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.QueryProductsResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;

@Component(name = "SimpleOrchestrator", immediate = false)
@Service({ com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration.class, com.cobiscorp.cobis.csp.services.inproc.IOrchestrator.class })
@Properties({ @Property(name = "service.description", value = "CreditCardsOrchestrator"), @Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
	@Property(name = "service.identifier", value = "CreditCardsOrchestrator") })
@SuppressWarnings({ "unchecked", "rawtypes" })
public class CreditCardsOrchestrator extends SPJavaOrchestrationBase {

	private static final String MAIN_TRANSACTION = "1801071";
	private static final String MAIN_TRANSACTION_MOVS = "1801072";
	private static final String SECONDARY_TRANSACTION = "1801073";

	private final ILogger logger = LogFactory.getLogger(CreditCardsOrchestrator.class);

	public void loadConfiguration(IConfigurationReader configurationReader) {
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, java.util.Map aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled()){
			logger.logDebug("Inicia CreditCards Orchestration");
		}
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		creditCardsConnectorExecution(aBagSPJavaOrchestration, anOriginalRequest);
		if (logger.isDebugEnabled()){
			logger.logDebug("Finaliza CreditCards Orchestration");
		}
		return (IProcedureResponse) aBagSPJavaOrchestration.get("CreditCardResponse");  // devuelve el result al orchestrator
	}

	private IProcedureResponse creditCardsConnectorExecution(Map aBagSPJavaOrchestration, IProcedureRequest anOriginalRequest) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Inicia metodo creditCardsConnectorExecution en CreditCardsOrchestration");
		}
		return executeConnector(aBagSPJavaOrchestration, "(service.identifier=CreditCardConnector)", SECONDARY_TRANSACTION, anOriginalRequest);
	}
	
	private IProcedureResponse executeConnector(Map aBagSPJavaOrchestration, String serviceIdentifier, String transactionNumber, IProcedureRequest anOriginalRequest) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Inicia metodo executeConnector en SimpleOrchestration");
		}

		IProcedureRequest procedureRequest = null; 
	    procedureRequest = anOriginalRequest;
		
		setGenericBindingHeaderFields(procedureRequest, transactionNumber);
		aBagSPJavaOrchestration.put(CONNECTOR_TYPE, serviceIdentifier);
		
		executeProvider(procedureRequest, aBagSPJavaOrchestration);
				
		IProcedureResponse creditCardConnectorResponse = executeProvider(procedureRequest, aBagSPJavaOrchestration);  //xxxxxxxx
		aBagSPJavaOrchestration.put("CreditCardResponse", creditCardConnectorResponse);

		if (logger.isInfoEnabled()){
		logger.logInfo("executeConnector thirdCreditCardResponse : " + creditCardConnectorResponse.getProcedureResponseAsString());
		}
		
		return creditCardConnectorResponse;
	}

	private void setGenericBindingHeaderFields(IProcedureRequest procedureRequest, String transactionNumber) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Inicia metodo setGenericBindingHeaderFields en CreditCardOrchestration");
		}
		procedureRequest.addFieldInHeader(IProvider.EXTERNAL_PROVIDER, ICOBISTS.HEADER_STRING_TYPE, "0");
		procedureRequest.addFieldInHeader(IThreshold.CHANNEL, ICOBISTS.HEADER_STRING_TYPE, "E");
				
		// agragr segun el operacion 71 o 72
		String operation = procedureRequest.readValueParam("@i_tipo_operacion");
		if (logger.isDebugEnabled()) {
			logger.logDebug("setGenericBindingHeaderFields @i_tipo_operacion: " + operation);
		}

		if(operation.equals("Q")) {
			procedureRequest.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, MAIN_TRANSACTION);
		}else {
			procedureRequest.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, MAIN_TRANSACTION_MOVS);
		}

		procedureRequest.addFieldInHeader(IProvider.HEADER_CATALOG_PROVIDER, ICOBISTS.HEADER_STRING_TYPE, "mc4_error_catalog");
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, transactionNumber);
		if (logger.isDebugEnabled()) {
			logger.logDebug("Finaliza metodo setGenericBindingHeaderFields en CreditCardOrchestration");
		}
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq, java.util.Map aBagSPJavaOrchestration) {
		throw new UnsupportedOperationException();
	}

}
