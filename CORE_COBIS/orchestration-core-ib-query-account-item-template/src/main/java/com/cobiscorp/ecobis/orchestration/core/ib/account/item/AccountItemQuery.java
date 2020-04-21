
package com.cobiscorp.ecobis.orchestration.core.ib.account.item;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IProvider;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.ModuleCriteriaRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ModuleCriteriaResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ModuleRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ModuleResponse;
import com.cobiscorp.ecobis.ib.application.dtos.QueryAccountItemsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.QueryAccountItemsResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Module;
import com.cobiscorp.ecobis.ib.orchestration.dtos.QueryAccountItem;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.IWSModules;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.IWSQueryAccountItem;

@Component(name = "AccountItemQuery", immediate = false)
@Service(value = { IWSQueryAccountItem.class })
@Properties(value = { @Property(name = "service.description", value = "AccountItemQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "AccountItemQuery") })
public class AccountItemQuery extends SPJavaOrchestrationBase implements IWSQueryAccountItem {
	private static ILogger logger = LogFactory.getLogger(AccountItemQuery.class);

	private java.util.Properties properties;
	/**** Output: getAccountItems *****/
	private static final int COL_ITEM_PENDING = 0;
	private static final int COL_DESCRIPTION = 1;
	private static final int COL_CURRENCY = 2;
	private static final int COL_DEPENDENCY = 3;
	private static final int COL_PAYMENT_METHOD = 4;
	private static final int COL_AMMOUNT = 5;
	static final String ORIGINAL_RESPONSE = "ORIGINAL_RESPONSE";
	private String code;
	private String codeError;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.IWSModules#getModule
	 * (com.cobiscorp.ecobis.ib.application.dtos.ModuleRequest)
	 * ******************
	 * ********************************************************
	 * *******************************************************************
	 */
	@Override
	public QueryAccountItemsResponse getAccountItems(QueryAccountItemsRequest aQueryAccountItemsRequest,
			Map<String, Object> aBagSPJavaOrchestration, java.util.Properties properties)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getAccountItem");
		}
		this.properties = properties;
		IProcedureResponse pResponse = Execution(aQueryAccountItemsRequest, aBagSPJavaOrchestration);
		QueryAccountItemsResponse accountItemResponse = transformToAccountItemResponse(pResponse);
		return accountItemResponse;
	}

	private IProcedureResponse Execution(QueryAccountItemsRequest aQueryAccountItemsRequest,
			Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		;
		IProcedureRequest request = initProcedureRequest(aQueryAccountItemsRequest.getOriginalRequest());

		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "691");

		request.addInputParam("@i_id_operativo", ICTSTypes.SQLVARCHAR, aQueryAccountItemsRequest.getIdOperativo());
		request.addInputParam("@i_operacion_connector", ICTSTypes.SYBINT4, "691");
		request.addInputParam("@i_codmodulo", ICTSTypes.SYBVARCHAR,
				aQueryAccountItemsRequest.getModule().getCodModule().toString());
		request.addInputParam("@i_nrooperacion", ICTSTypes.SYBINT4,
				aQueryAccountItemsRequest.getOperation().toString());
		request.addInputParam("@i_fechaoperativa", ICTSTypes.SYBINT4, aQueryAccountItemsRequest.getDate().toString());
		request.addInputParam("@i_cuenta", ICTSTypes.SYBVARCHAR, aQueryAccountItemsRequest.getAccount());
		request.addInputParam("@i_servicio", ICTSTypes.SYBINT4, aQueryAccountItemsRequest.getService().toString());

		request.addOutputParam("@o_coderror", ICTSTypes.SYBINT4, "0");
		request.addOutputParam("@o_mensaje", ICTSTypes.SQLVARCHAR, "            ");
		request.addOutputParam("@o_nitfac", ICTSTypes.SQLVARCHAR, "            ");
		request.addOutputParam("@o_nombrefac", ICTSTypes.SQLVARCHAR, "            ");
		request.addOutputParam("@o_cambiarnitynombrefac", ICTSTypes.SQLVARCHAR, "            ");
		request.addOutputParam("@o_tienerequisito", ICTSTypes.SQLVARCHAR, "            ");

		request.addFieldInHeader(ICOBISTS.HEADER_TIMEOUT, ICOBISTS.HEADER_STRING_TYPE,
				((String) this.properties.get("HEADER_TIMEOUT")));
		request.addFieldInHeader(IProvider.HEADER_CATALOG_PROVIDER, ICOBISTS.HEADER_STRING_TYPE,
				((String) this.properties.get("HEADER_CATALOG_PROVIDER")));
		aBagSPJavaOrchestration.put(ICISSPBaseOrchestration.CONNECTOR_TYPE,
				((String) this.properties.get("CONNECTOR_TYPE")));
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Request:*** " + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Start Request *** ");
		}

		IProcedureResponse pResponse = executeProvider(request, aBagSPJavaOrchestration);

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response: *** " + pResponse.getProcedureResponseAsString());
		}

		aBagSPJavaOrchestration.put(ORIGINAL_RESPONSE, pResponse);

		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response *** ");
		}
		return pResponse;
	}

	private QueryAccountItemsResponse transformToAccountItemResponse(IProcedureResponse aProcedureResponse) {
		QueryAccountItemsResponse QueryAccountItemsResp = new QueryAccountItemsResponse();
		List<QueryAccountItem> itemCollection = new ArrayList<QueryAccountItem>();
		QueryAccountItem aQueryAccountItem = null;

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + aProcedureResponse.getProcedureResponseAsString());
		}

		aProcedureResponse.setReturnCode(Integer.parseInt(aProcedureResponse.readValueParam("@o_coderror")));

		if (aProcedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsItems = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

			for (IResultSetRow iResultSetRow : rowsItems) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				aQueryAccountItem = new QueryAccountItem();
				aQueryAccountItem.setDependency(columns[COL_DEPENDENCY].getValue());
				aQueryAccountItem.setAmount(new BigDecimal(columns[COL_AMMOUNT].getValue()));
				aQueryAccountItem.setCurrency(columns[COL_CURRENCY].getValue());
				aQueryAccountItem.setItemDescription(columns[COL_DESCRIPTION].getValue());
				aQueryAccountItem.setItemPending(columns[COL_ITEM_PENDING].getValue());
				aQueryAccountItem.setPaymentMethod(columns[COL_PAYMENT_METHOD].getValue());
				itemCollection.add(aQueryAccountItem);
			}

			QueryAccountItemsResp.setItemsCollection(itemCollection);
			QueryAccountItemsResp.setNitFac(aProcedureResponse.readValueParam("@o_nitfac"));
			QueryAccountItemsResp.setCodError(Integer.parseInt(aProcedureResponse.readValueParam("@o_coderror")));
			QueryAccountItemsResp.setMessageError(aProcedureResponse.readValueParam("@o_mensaje"));
			QueryAccountItemsResp.setNameFac(aProcedureResponse.readValueParam("@o_nombrefac"));
			QueryAccountItemsResp.setChangeNitFac(aProcedureResponse.readValueParam("@o_cambiarnitynombrefac"));
			QueryAccountItemsResp.setRequirement(aProcedureResponse.readValueParam("@o_tienerequisito"));

		} else {
			QueryAccountItemsResp.setMessageError(aProcedureResponse.readValueParam("@o_mensaje"));
			QueryAccountItemsResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		}
		QueryAccountItemsResp.setReturnCode(aProcedureResponse.getReturnCode());

		return QueryAccountItemsResp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.commons.components.ICOBISComponent#loadConfiguration
	 * (com.cobiscorp.cobis.commons.configuration.IConfigurationReader)
	 */
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase
	 * #executeJavaOrchestration
	 * (com.cobiscorp.cobis.cts.domains.IProcedureRequest, java.util.Map)
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase
	 * #processResponse(com.cobiscorp.cobis.cts.domains.IProcedureRequest,
	 * java.util.Map)
	 */
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		return null;
	}

}
