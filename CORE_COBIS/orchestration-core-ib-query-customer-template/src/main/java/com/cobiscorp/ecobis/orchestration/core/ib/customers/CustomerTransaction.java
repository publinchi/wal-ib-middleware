package com.cobiscorp.ecobis.orchestration.core.ib.customers;

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
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
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
import com.cobiscorp.cobisv.commons.context.CobisSession;
import com.cobiscorp.cobisv.commons.context.Context;
import com.cobiscorp.cobisv.commons.context.ContextManager;
import com.cobiscorp.ecobis.ib.application.dtos.AddressRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AddressResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Address;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCustomerTransaction;

@Service(value = { ICoreServiceCustomerTransaction.class, ICISSPBaseOrchestration.class, IOrchestrator.class })
@Component(name = "CustomerTransaction", immediate = false)
@Properties(value = { @Property(name = "service.description", value = "CustomerTransaction"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "CustomerTransaction") })

public class CustomerTransaction extends SPJavaOrchestrationBase implements ICoreServiceCustomerTransaction {
	private static final String CLASS_NAME = " >-----> ";
	private static ILogger logger = LogFactory.getLogger(CustomerInformation.class);
	protected static final String COBIS_CONTEXT = "COBIS";

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
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceCustomerTransaction#setCustomerInformation(com.cobiscorp.
	 * ecobis.ib.application.dtos.AddressRequest)
	 */
	@Override
	public AddressResponse setCustomerTransaction(AddressRequest addressRequest)
			throws CTSServiceException, CTSInfrastructureException {		
		
		Context context = ContextManager.getContext();
		CobisSession session = (CobisSession) context.getSession();		
		String CODE_TRN = addressRequest.getCodeTransactionalIdentifier();
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800037");
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		anOriginalRequest.setSpName("cobis..sp_upd_datos_cliente_bv");
		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1800037");
		anOriginalRequest.addInputParam("@i_cliente", ICTSTypes.SQLINT2,
				addressRequest.getClientCollection().getIdCustomer().toString());
		anOriginalRequest.addInputParam("@i_cod_telefono", ICTSTypes.SQLINT2,
				addressRequest.getAddressCollection().getPhoneCode().toString());
		anOriginalRequest.addInputParam("@i_cod_direccion", ICTSTypes.SQLINT2,
				addressRequest.getAddressCollection().getAddressCode().toString());
		anOriginalRequest.addInputParam("@i_cod_email", ICTSTypes.SQLINT2,
				addressRequest.getAddressCollection().getEmailCode().toString());
		anOriginalRequest.addInputParam("@i_telefono", ICTSTypes.SQLVARCHAR,
				addressRequest.getAddressCollection().getPhone());
		anOriginalRequest.addInputParam("@i_barrio", ICTSTypes.SQLVARCHAR,
				addressRequest.getAddressCollection().getNeighborhood());
		anOriginalRequest.addInputParam("@i_calle", ICTSTypes.SQLVARCHAR,
				addressRequest.getAddressCollection().getStreet());
		anOriginalRequest.addInputParam("@i_casa", ICTSTypes.SQLVARCHAR,
				addressRequest.getAddressCollection().getHouse());
		anOriginalRequest.addInputParam("@i_descripcion", ICTSTypes.SQLVARCHAR,
				addressRequest.getAddressCollection().getBuilding());
		anOriginalRequest.addInputParam("@i_email", ICTSTypes.SQLVARCHAR,
				addressRequest.getAddressCollection().getEmail());
		anOriginalRequest.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, session.getServer());
		anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, session.getOffice());
		anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, session.getUser());
		anOriginalRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, session.getTerminal());

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Obtencion del servicio");

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Enviando solicitud al Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request a enviar: " + anOriginalRequest.getProcedureRequestAsString());

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Data devuelta del Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Response obtenido: " + response.getProcedureResponseAsString());

		return transformToCustomerTransactionResponse(response);
	}

	private AddressResponse transformToCustomerTransactionResponse(IProcedureResponse aProcedureResponse) {
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "RESPONSE TO TRANSFORM: " + aProcedureResponse.getProcedureResponseAsString());
		AddressResponse AddressResp = new AddressResponse();
		Address aAddress = null;
		Client aClient = new Client();		

		if (aProcedureResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsAddress = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

			for (IResultSetRow iResultSetRow : rowsAddress) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				aAddress = new Address();

				aAddress.setAdditionalInformation("V");
				if (columns[1].getValue() != null) {
					aClient.setCompleteName(columns[1].getValue());
				} else {
					aClient.setCompleteName("   ");
				}
				if (columns[2].getValue() != null) {
					aAddress.setPhone(columns[2].getValue());
				} else {
					aAddress.setPhone("   ");
				}
				if (columns[3].getValue() != null) {
					aAddress.setBuilding(columns[3].getValue());
				} else {
					aAddress.setBuilding("   ");
				}
				if (columns[4].getValue() != null) {
					aAddress.setEmail(columns[4].getValue());
				} else {
					aAddress.setEmail("   ");
				}
			}
		} else {
			AddressResp.setMessages(Utils.returnArrayMessage(aProcedureResponse)); 
		}

		AddressResp.setClientCollection(aClient);
		AddressResp.setAddressCollection(aAddress);
		AddressResp.setReturnCode(aProcedureResponse.getReturnCode());

		return AddressResp;
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
