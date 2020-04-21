/**
 *
 */
package com.cobiscorp.ecobis.ib.orchestration.core.ib.commons;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.ecobis.ib.application.dtos.ClientInformationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ClientInformationResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceClient;
import com.cobiscorp.ecobis.ib.utils.dtos.Utils;

/**
 * @author schancay
 * @since Sep 4, 2014
 * @version 1.0.0
 */
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class, ICoreServiceClient.class })
@Component(name = "ClientIBCore", immediate = false)
@Properties(value = { @Property(name = "service.description", value = "ClientIBCore"), @Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "ClientIBCore") })
public class ClientIBCore extends SPJavaOrchestrationBase implements ICoreServiceClient {

	private static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	private static final String CLASS_NAME = " >-----> ";
	private static ILogger logger = LogFactory.getLogger(ClientIBCore.class);

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Iniciando obtencion de informacion del cliente");
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, request);

		IProcedureResponse response = getInformationUserIB(request, aBagSPJavaOrchestration);
		return response;
	}

	@Override
	public ClientInformationResponse getInformationClientBv(ClientInformationRequest clientRequest, Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = transformClientToRequest(clientRequest);
		IProcedureResponse response = executeJavaOrchestration(request, aBagSPJavaOrchestration);
		return transformResponseToClient(response);
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {

	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		return null;
	}

	/**
	 * Get information from database local with params:<br>
	 * User Virtual Banking<br>
	 * Login Virtual Banking<br>
	 * Identifier Channel<br>
	 *
	 * @param client
	 * @param aBagSPJavaOrchestration
	 * @return Information necessary for send information basic of Core Banking.
	 */
	private IProcedureResponse getInformationUserIB(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Data de entrada:" + request.getProcedureRequestAsString());

		IProcedureResponse response = executeCoreBanking(request);

		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Response obtenido: " + response.getProcedureResponseAsString());

		return response;
	}

	private IProcedureRequest transformClientToRequest(ClientInformationRequest clientRequest) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando objeto a IProcedureRequest");
		IProcedureRequest request = new ProcedureRequestAS();
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_LOCAL);
		request.addFieldInHeader(ICOBISTS.HEADER_TRN, ICOBISTS.HEADER_NUMBER_TYPE, "18500");

		request.setSpName("cob_bvirtual..sp_cons_clientes_bv");
		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, "18500");
		request.addInputParam("@i_valor_codigo", ICTSTypes.SQLINT4, clientRequest.getClient().getId());
		request.addInputParam("@i_login", ICTSTypes.SQLVARCHAR, clientRequest.getClient().getLogin());
		request.addInputParam("@i_canal", ICTSTypes.SQLINT2, clientRequest.getChannelId());
		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "C");
		return request;
	}

	private ClientInformationResponse transformResponseToClient(IProcedureResponse response) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando objeto a ClientInformationResponse");
		ClientInformationResponse clientInformationResponse = new ClientInformationResponse();
		Utils.transformIprocedureResponseToBaseResponse(clientInformationResponse, response);

		Client client = new Client();

		if (!response.hasError()) {
			if (response.getResultSets().size() > 0) {
				IResultSetBlock resulset = response.getResultSet(1);
				IResultSetRow[] rowsTemp = resulset.getData().getRowsAsArray();

				if (rowsTemp.length == 1) {
					IResultSetRowColumnData[] rows = rowsTemp[0].getColumnsAsArray();
					if (!Utils.isNullOrEmpty(rows[0]))
						client.setId(rows[0].getValue());
					if (!Utils.isNullOrEmpty(rows[1]))
						client.setIdCustomer(rows[1].getValue());
				}
			}
		}

		clientInformationResponse.setClient(client);
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Objeto Transformado:" + clientInformationResponse);
		return clientInformationResponse;
	}
}