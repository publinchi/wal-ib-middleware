package com.cobiscorp.ecobis.orchestration.core.ib.account.subitem;

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
import com.cobiscorp.ecobis.ib.application.dtos.AccountSubitemsRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AccountSubitemsResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AccountSubitem;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.IWSQueryAccountSubitem;

@Component(name = "AccountSubitemQuery", immediate = false)
@Service(value = { IWSQueryAccountSubitem.class })
@Properties(value = { @Property(name = "service.description", value = "AccountSubitemQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "AccountSubitemQuery") })
public class AccountSubitemQuery extends SPJavaOrchestrationBase implements IWSQueryAccountSubitem {

	private static ILogger logger = LogFactory.getLogger(AccountSubitemQuery.class);
	private java.util.Properties properties;
	private static final int COL_DESCRIPTION = 0;
	private static final int COL_AMMOUNT = 1;
	static final String ORIGINAL_RESPONSE = "ORIGINAL_RESPONSE";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.IWSQueryAccountSubitem#
	 * getAccountSubitems(com.cobiscorp.ecobis.ib.application.dtos.
	 * AccountSubitemsRequest, java.util.Map, java.util.Properties)
	 */
	@Override
	public AccountSubitemsResponse getAccountSubitems(AccountSubitemsRequest aQueryAccountSubitemRequest,
			Map<String, Object> aBagSPJavaOrchestration, java.util.Properties properties)
			throws CTSServiceException, CTSInfrastructureException {

		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getAccountItem");
		}

		this.properties = properties;
		IProcedureResponse pResponse = Execution(aQueryAccountSubitemRequest, aBagSPJavaOrchestration);
		AccountSubitemsResponse accountSubitemResponse = transformToAccountItemResponse(pResponse);

		return accountSubitemResponse;
	}

	private IProcedureResponse Execution(AccountSubitemsRequest aQueryAccountSubitemsRequest,
			Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {

		IProcedureRequest request = initProcedureRequest(aQueryAccountSubitemsRequest.getOriginalRequest());

		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "692");

		request.addInputParam("@i_id_operativo", ICTSTypes.SQLVARCHAR, aQueryAccountSubitemsRequest.getIdOperativo());
		request.addInputParam("@i_operacion_connector", ICTSTypes.SYBINT4, "692");
		request.addInputParam("@i_codmodulo", ICTSTypes.SYBVARCHAR,
				aQueryAccountSubitemsRequest.getModule().getCodModule().toString());
		request.addInputParam("@i_nrooperacion", ICTSTypes.SYBINT4,
				aQueryAccountSubitemsRequest.getOperation().toString());
		request.addInputParam("@i_fechaoperativa", ICTSTypes.SYBINT4,
				aQueryAccountSubitemsRequest.getDate().toString());
		request.addInputParam("@i_cuenta", ICTSTypes.SYBVARCHAR, aQueryAccountSubitemsRequest.getAccount());
		request.addInputParam("@i_servicio", ICTSTypes.SYBINT4, aQueryAccountSubitemsRequest.getService().toString());
		request.addInputParam("@i_nroitem", ICTSTypes.SYBVARCHAR, aQueryAccountSubitemsRequest.getItem());
		request.addInputParam("@i_cantidadmonto", ICTSTypes.SYBMONEY,
				aQueryAccountSubitemsRequest.getAmount().toString());

		request.addOutputParam("@o_coderror", ICTSTypes.SYBINT4, "0");
		request.addOutputParam("@o_mensaje", ICTSTypes.SQLVARCHAR, "                ");
		request.addOutputParam("@o_totaltem", ICTSTypes.SQLMONEY, "0");

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

		aBagSPJavaOrchestration.put(ORIGINAL_RESPONSE, pResponse);

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response *** ");
		}

		return pResponse;
	}

	private AccountSubitemsResponse transformToAccountItemResponse(IProcedureResponse aProcedureResponse) {
		AccountSubitemsResponse aAccountSubitemsResp = new AccountSubitemsResponse();
		List<AccountSubitem> itemCollection = new ArrayList<AccountSubitem>();
		AccountSubitem aAccountSubitem = null;

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + aProcedureResponse.getProcedureResponseAsString());
		}

		aProcedureResponse.setReturnCode(Integer.parseInt(aProcedureResponse.readValueParam("@o_coderror")));

		if (aProcedureResponse.getReturnCode() == 0) {

			IResultSetRow[] rowsSubitems = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

			for (IResultSetRow iResultSetRow : rowsSubitems) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				aAccountSubitem = new AccountSubitem();
				aAccountSubitem.setDescription(columns[COL_DESCRIPTION].getValue());
				aAccountSubitem.setAmount(new BigDecimal(columns[COL_AMMOUNT].getValue()));
				itemCollection.add(aAccountSubitem);
			}

			aAccountSubitemsResp.setSubitemsCollection(itemCollection);
			aAccountSubitemsResp.setCodError(Integer.parseInt(aProcedureResponse.readValueParam("@o_coderror")));
			aAccountSubitemsResp.setMessageError(aProcedureResponse.readValueParam("@o_mensaje"));
			aAccountSubitemsResp.setTotalItem(new BigDecimal(aProcedureResponse.readValueParam("@o_totaltem")));

		} else {
			aAccountSubitemsResp.setMessageError(aProcedureResponse.readValueParam("@o_mensaje"));
			aAccountSubitemsResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		}

		aAccountSubitemsResp.setReturnCode(aProcedureResponse.getReturnCode());

		return aAccountSubitemsResp;
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

}
