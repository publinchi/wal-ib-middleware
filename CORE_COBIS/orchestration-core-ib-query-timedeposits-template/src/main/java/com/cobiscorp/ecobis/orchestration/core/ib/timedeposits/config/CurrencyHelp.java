package com.cobiscorp.ecobis.orchestration.core.ib.timedeposits.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.ecobis.ib.application.dtos.CurrencyDefinitionRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CurrencyDefinitionResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CurrencyDefinition;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCurrencyDef;

/**
 * @author jchonillo
 * @since Jan 08, 2015
 * @version 1.0.0
 */

@Component(name = "CurrencyHelp ", immediate = false)
@Service(value = { ICoreServiceCurrencyDef.class })
@Properties(value = { @Property(name = "service.description", value = "CurrencyHelp"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CurrencyHelp") })
public class CurrencyHelp extends SPJavaOrchestrationBase implements ICoreServiceCurrencyDef {

	private static ILogger logger = LogFactory.getLogger(CurrencyHelp.class);
	private static final String SP_NAME = "cobis..sp_moneda";
	// getCurrencyHelp
	private static final int COL_CODE = 0;
	private static final int COL_DESCRIPTION = 1;
	private static final int COL_SYMBOL = 2;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCurrencyDef#
	 * getCurrencyHelp(com.cobiscorp.ecobis.ib.application.dtos.
	 * CurrencyDefinitionRequest)
	 */
	@Override
	public CurrencyDefinitionResponse getCurrencyHelp(CurrencyDefinitionRequest currencyDefinitionRequest)
			throws CTSServiceException, CTSInfrastructureException {
		IProcedureResponse pResponse = Execution(SP_NAME, currencyDefinitionRequest, "getCurrencyHelp");
		CurrencyDefinitionResponse currencyDefinitionResponse = transformCurrencyDefinitionResponse(pResponse);
		return currencyDefinitionResponse;
	}

	private IProcedureResponse Execution(String spName, CurrencyDefinitionRequest currencyDefinitionRequest,
			String string) {

		IProcedureRequest request = initProcedureRequest(currencyDefinitionRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1556");

		request.setSpName(spName);

		request.addInputParam("@i_modo", ICTSTypes.SQLINT1, currencyDefinitionRequest.getMode().toString());
		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "H");
		request.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1556");
		request.addInputParam("@i_tipo", ICTSTypes.SQLCHAR, "A");

		if (logger.isDebugEnabled()) {
			logger.logDebug("Request Corebanking: " + request.getProcedureRequestAsString());
		}

		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response Currency Help: *** " + pResponse.getProcedureResponseAsString());
		}

		return pResponse;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.commons.components.ICOBISComponent#loadConfiguration(
	 * com.cobiscorp.cobis.commons.configuration.IConfigurationReader)
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase#
	 * executeJavaOrchestration(com.cobiscorp.cobis.cts.domains.
	 * IProcedureRequest, java.util.Map)
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase#
	 * processResponse(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param pResponse
	 * @return
	 */
	private CurrencyDefinitionResponse transformCurrencyDefinitionResponse(IProcedureResponse pResponse) {
		CurrencyDefinitionResponse currencyDefResponse = new CurrencyDefinitionResponse();
		List<CurrencyDefinition> listCurrencyDef = new ArrayList<CurrencyDefinition>();		

		if (pResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsSimulation = pResponse.getResultSet(1).getData().getRowsAsArray();
			for (int i = 0; i < rowsSimulation.length; i++) {

				CurrencyDefinition currencyDefinition = new CurrencyDefinition();
				IResultSetRow iResultSetRow = rowsSimulation[i];
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();

				currencyDefinition.setCode(
						columns[COL_CODE].getValue() == null ? 0 : Integer.parseInt(columns[COL_CODE].getValue()));
				currencyDefinition.setDescription(columns[COL_DESCRIPTION].getValue());
				currencyDefinition.setSimbol(columns[COL_SYMBOL].getValue());

				listCurrencyDef.add(currencyDefinition);
			}

			currencyDefResponse.setSuccess(true);
			currencyDefResponse.setListCurrencyDef(listCurrencyDef);
		} else
			currencyDefResponse.setSuccess(false);

		currencyDefResponse.setReturnCode(pResponse.getReturnCode());
		Message[] message = Utils.returnArrayMessage(pResponse);
		currencyDefResponse.setMessages(message);		
		return currencyDefResponse;
	}
}
