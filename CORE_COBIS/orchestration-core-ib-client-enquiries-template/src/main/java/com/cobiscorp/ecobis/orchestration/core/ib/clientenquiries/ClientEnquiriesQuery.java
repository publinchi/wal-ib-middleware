package com.cobiscorp.ecobis.orchestration.core.ib.clientenquiries;

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
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceClientEnquiries;
import com.cobiscorp.ecobis.ib.application.dtos.ClientEnquiriesRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ClientEnquiriesResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CreditLineRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CreditLineResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ClientEnquiries;

@Component(name = "ClientEnquiriesQuery", immediate = false)
@Service(value = { ICoreServiceClientEnquiries.class })
@Properties(value = { @Property(name = "service.description", value = "ClientEnquiriesQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ClientEnquiriesQuery") })
public class ClientEnquiriesQuery extends SPJavaOrchestrationBase implements ICoreServiceClientEnquiries {
	private static ILogger logger = LogFactory.getLogger(ClientEnquiriesQuery.class);
	private static final String SP_NAME = "cobis..sp_bv_cons_solicitudes";

	/*
	 * Execute Stored Procedure and Return IProcedureResponse
	 */
	private IProcedureResponse Execution(String SpName, ClientEnquiriesRequest aClientEnquiriesRequest)
			throws CTSServiceException, CTSInfrastructureException {
		IProcedureRequest request = initProcedureRequest(aClientEnquiriesRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1875069");
		request.setSpName(SpName);

		request.addInputParam("@i_tipo", ICTSTypes.SYBVARCHAR,
				aClientEnquiriesRequest.getSearchOption().getCriteria().toString());
		request.addInputParam("@i_fecha_ini", ICTSTypes.SYBVARCHAR,
				aClientEnquiriesRequest.getSearchOption().getInitialDate().toString());
		request.addInputParam("@i_fecha_fin", ICTSTypes.SYBVARCHAR,
				aClientEnquiriesRequest.getSearchOption().getFinalDate().toString());
		request.addInputParam("@i_formato_fecha", ICTSTypes.SYBINT4,
				aClientEnquiriesRequest.getDateFormatId().toString());
		request.addInputParam("@i_nregistros", ICTSTypes.SYBINT4,
				aClientEnquiriesRequest.getNumberofRegisters().toString());
		request.addInputParam("@i_siguiente", ICTSTypes.SYBINT4, aClientEnquiriesRequest.getNext().toString());
		request.addInputParam("@i_siguiente_aux", ICTSTypes.SYBINT4, aClientEnquiriesRequest.getId_aux().toString());
		request.addInputParam("@i_cliente", ICTSTypes.SYBINT4, aClientEnquiriesRequest.getMISClientId().toString());

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Request:*** " + request.getProcedureRequestAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Start Request *** ");
		}

		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response *** ");
		}
		return pResponse;
	}

	private ClientEnquiriesResponse transformToClientEnquiriesResponse(IProcedureResponse aProcedureResponse) {
		ClientEnquiriesResponse ClientEnquiriesResp = new ClientEnquiriesResponse();
		List<ClientEnquiries> clientenquiriesCollection = new ArrayList<ClientEnquiries>();
		ClientEnquiries aClientEnquiries = null;

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + aProcedureResponse.getProcedureResponseAsString());
		}

		if (aProcedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsLines = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

			for (IResultSetRow iResultSetRow : rowsLines) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				aClientEnquiries = new ClientEnquiries();
				aClientEnquiries.setId(Integer.parseInt(columns[0].getValue()));
				aClientEnquiries.setEnquiryDate(columns[1].getValue());
				aClientEnquiries.setAccount(columns[2].getValue());
				aClientEnquiries.setAmount(new Double(columns[3].getValue()));
				aClientEnquiries.setState(columns[4].getValue());
				aClientEnquiries.setBeneficiary(columns[5].getValue());
				aClientEnquiries.setQuantity(columns[6].getValue());

				if (columns[7].isNull())
					aClientEnquiries.setIdAux(0);
				else
					aClientEnquiries.setIdAux(Integer.parseInt(columns[7].getValue()));

				aClientEnquiries.setCurrencyId(Integer.parseInt(columns[8].getValue()));
				aClientEnquiries.setEndorsementType(columns[9].getValue());
				aClientEnquiries.setEndorsement(columns[10].getValue());
				aClientEnquiries.setSubType(columns[11].getValue());
				clientenquiriesCollection.add(aClientEnquiries);
			}
			ClientEnquiriesResp.setClientEnquiriesCollection(clientenquiriesCollection);
		} else {

			ClientEnquiriesResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		}

		ClientEnquiriesResp.setReturnCode(aProcedureResponse.getReturnCode());

		return ClientEnquiriesResp;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceClientEnquiries#getClientEnquiries(com.cobiscorp.ecobis.ib.
	 * application.dtos.ClientEnquiriesRequest)
	 */
	@Override
	public ClientEnquiriesResponse getClientEnquiries(ClientEnquiriesRequest aClientEnquiriesRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getClientEnquiries");
		}

		IProcedureResponse pResponse = Execution(SP_NAME, aClientEnquiriesRequest);
		ClientEnquiriesResponse clientEnquiriesResponse = transformToClientEnquiriesResponse(pResponse);
		return clientEnquiriesResponse;
	}
}
