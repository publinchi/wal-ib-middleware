 package com.cobiscorp.ecobis.orchestration.core.ib.customservices;

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
import com.cobiscorp.ecobis.ib.application.dtos.CustomServicesRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CustomServicesResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CustomServices;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceCustomServices;
import com.cobiscorp.ecobis.orchestration.core.ib.executecauseandcost.ExecuteCuaseAndCostAdmin;

@Service(value = { ICoreServiceCustomServices.class, ICISSPBaseOrchestration.class, IOrchestrator.class })
@Component(name = "searchCustomServices", immediate = false)
@Properties(value = { @Property(name = "service.description", value = "searchCustomServices"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.5.0"),
		@Property(name = "service.identifier", value = "searchCustomServices") })
public class searchCustomServices extends SPJavaOrchestrationBase implements ICoreServiceCustomServices {

	private static final String CLASS_NAME = " >-----> ";
	private static ILogger logger = LogFactory.getLogger(ExecuteCuaseAndCostAdmin.class);

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
	 * ICoreServiceCustomServices#searchCustomServicesAdmin(com.cobiscorp.ecobis
	 * .ib.application.dtos.CustomServicesRequest)
	 */
	@Override
	public CustomServicesResponse searchCustomServicesAdmin(CustomServicesRequest aCustomServicesRequest)
			throws CTSServiceException, CTSInfrastructureException {
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		String CODE_TRN = "4029"; // aCustomServicesRequest.getTrn().toString();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, CODE_TRN);
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.setSpName("cob_remesas..sp_ins_serv_pe");
		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, CODE_TRN);
		anOriginalRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, aCustomServicesRequest.getOperation());
		anOriginalRequest.addInputParam("@i_modo", ICTSTypes.SQLINT4, aCustomServicesRequest.getMode().toString());
		if (aCustomServicesRequest.getNemonic() != null)
			anOriginalRequest.addInputParam("@i_nemonico", ICTSTypes.SQLVARCHAR, aCustomServicesRequest.getNemonic());
		if (logger.isInfoEnabled())
			logger.logInfo("************** aCustomServicesRequest.getCode() ==> " + aCustomServicesRequest.getCode());

		if (aCustomServicesRequest.getCode() != null) {
			anOriginalRequest.addInputParam("@i_codigo", ICTSTypes.SQLINT4,
					aCustomServicesRequest.getCode().toString());
			anOriginalRequest.addInputParam("@s_sesn", ICTSTypes.SQLINT4, aCustomServicesRequest.getSsesn().toString());
			anOriginalRequest.addInputParam("@s_ssn", ICTSTypes.SQLINT4, aCustomServicesRequest.getSssn().toString());
			anOriginalRequest.addInputParam("@s_date", ICTSTypes.SQLVARCHAR, aCustomServicesRequest.getSdate());
			anOriginalRequest.addInputParam("@s_org", ICTSTypes.SQLVARCHAR, aCustomServicesRequest.getSorg());
		}
		anOriginalRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, aCustomServicesRequest.getTerminal());
		anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT4, aCustomServicesRequest.getOffice().toString());
		anOriginalRequest.addInputParam("@s_rol", ICTSTypes.SQLINT4, aCustomServicesRequest.getRol().toString());

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Enviando solicitud al Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Request a enviar: " + anOriginalRequest.getProcedureRequestAsString());

		IProcedureResponse response = executeCoreBanking(anOriginalRequest);

		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Data devuelta del Core");
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "Response obtenido: " + response.getProcedureResponseAsString());

		return transformToCustomServicesResponse(response);
	}

	/**
	 * @param response
	 * @return
	 */
	private CustomServicesResponse transformToCustomServicesResponse(IProcedureResponse aProcedureResponse) {
		if (logger.isDebugEnabled())
			logger.logDebug(CLASS_NAME + "RESPONSE TO TRANSFORM: " + aProcedureResponse.getProcedureResponseAsString());
		CustomServices aCustomServices = null;
		List<CustomServices> aCustomServicesList = new ArrayList<CustomServices>();
		CustomServicesResponse aCustomServicesResponse = new CustomServicesResponse();
		if (aProcedureResponse.getReturnCode() == 0) {
			if (aProcedureResponse.getResultSet(1) != null) {
				IResultSetRow[] rowsCustomServices = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

				for (IResultSetRow iResultSetRow : rowsCustomServices) {
					IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
					aCustomServices = new CustomServices();
					if (columns[0].getValue() != null) {
						aCustomServices.setCodeService(Integer.parseInt((columns[0].getValue())));
					}
					if (columns[1].getValue() != null) {
						aCustomServices.setNemonic((columns[1].getValue()));
					}
					if (columns[2].getValue() != null) {
						aCustomServices.setDescription((columns[2].getValue()));
					}
					if (columns[3].getValue() != null) {
						aCustomServices.setState((columns[3].getValue()));
					}
					if (columns[4].getValue() != null) {
						aCustomServices.setInternalCost(Double.parseDouble((columns[4].getValue())));
					}
					if (columns[5].getValue() != null) {
						aCustomServices.setItemNumber(Integer.parseInt((columns[5].getValue())));
					}

					aCustomServicesList.add(aCustomServices);
				}
				aCustomServicesResponse.setCustomServicesCollection(aCustomServicesList);
			}

		} else {
			Message[] message = Utils.returnArrayMessage(aProcedureResponse);
			aCustomServicesResponse.setMessages(message);
		}
		aCustomServicesResponse.setReturnCode(aProcedureResponse.getReturnCode());

		return aCustomServicesResponse;
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
