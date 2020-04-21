package com.cobiscorp.ecobis.orchestration.core.ib.transfers;

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
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobisv.commons.context.CobisSession;
import com.cobiscorp.cobisv.commons.context.Context;
import com.cobiscorp.cobisv.commons.context.ContextManager;
import com.cobiscorp.ecobis.ib.application.dtos.InternationalTransfersReceivedRequest;
import com.cobiscorp.ecobis.ib.application.dtos.InternationalTransfersReceivedResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.InternationalTransfersReceived;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Message;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceInternationalTransfersReceived;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;

@Service(value = { ICoreServiceInternationalTransfersReceived.class, ICISSPBaseOrchestration.class,
		IOrchestrator.class })
@Component(name = "InternationalTransfersReceivedQuery", immediate = false)
@Properties(value = { @Property(name = "service.description", value = "InternationalTransfersReceivedQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "InternationalTransfersReceivedQuery") })

public class InternationalTransfersReceivedQuery extends SPJavaOrchestrationBase
		implements ICoreServiceInternationalTransfersReceived {
	ILogger logger = LogFactory.getLogger(InternationalTransfersReceivedQuery.class);
	private static final String CLASS_NAME = " >-----> ";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cobiscorp.ecobis.ib.orchestration.interfaces.
	 * ICoreServiceInternationalTransfersReceived#
	 * searchInternationalTransfersReceived(com.cobiscorp.ecobis.ib.application.
	 * dtos.InternationalTransfersReceivedRequest)
	 */
	@Override
	public InternationalTransfersReceivedResponse searchInternationalTransfersReceived(
			InternationalTransfersReceivedRequest aInternationalTransfersReceivedRequest)
			throws CTSServiceException, CTSInfrastructureException {
		Context context = ContextManager.getContext();
		CobisSession session = (CobisSession) context.getSession();

		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "9655");
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		anOriginalRequest.setSpName("cob_comext..sp_qtu_trr_reporte");
		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, "9655"); // CODE_TRN);
		anOriginalRequest.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, session.getServer());
		anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, session.getOffice());
		anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, session.getUser());
		anOriginalRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, session.getTerminal());
		anOriginalRequest.addInputParam("@s_rol", ICTSTypes.SQLINT4, session.getRole());

		anOriginalRequest.addInputParam("@i_opeban", ICTSTypes.SQLVARCHAR,
				aInternationalTransfersReceivedRequest.getOperation().toString());
		anOriginalRequest.addInputParam("@i_fdate", ICTSTypes.SQLINT4,
				aInternationalTransfersReceivedRequest.getDateFormat().toString());
		anOriginalRequest.addInputParam("@i_modo", ICTSTypes.SQLINT4, "0");

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
		return transformToInternationalTransfersReceivedResponse(response);
	}

	/**
	 * @param response
	 * @return
	 */
	private InternationalTransfersReceivedResponse transformToInternationalTransfersReceivedResponse(
			IProcedureResponse aProcedureResponse) {

		InternationalTransfersReceived aInternationalTransfersReceived = null;
		List<InternationalTransfersReceived> transfersReceivedList = new ArrayList<InternationalTransfersReceived>();
		InternationalTransfersReceivedResponse aInternationalTransfersReceivedResponse = new InternationalTransfersReceivedResponse();

		IResultSetRow[] rowsTransfersInternationalReceived = aProcedureResponse.getResultSet(1).getData()
				.getRowsAsArray();
		for (IResultSetRow iResultSetRow : rowsTransfersInternationalReceived) {
			IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
			aInternationalTransfersReceived = new InternationalTransfersReceived();

			if (columns[0].getValue() != null) {
				aInternationalTransfersReceived.setOfficeNumber(Integer.parseInt(columns[0].getValue()));
			}
			if (columns[1].getValue() != null) {
				aInternationalTransfersReceived.setOfficeDescription(columns[1].getValue());
			}
			if (columns[2].getValue() != null) {
				aInternationalTransfersReceived.setReference(columns[2].getValue());
			}
			if (columns[3].getValue() != null) {
				aInternationalTransfersReceived.setConcept(columns[3].getValue());
			}
			if (columns[4].getValue() != null) {
				aInternationalTransfersReceived.setLastBeneficiary(columns[4].getValue());
			}
			if (columns[5].getValue() != null) {
				aInternationalTransfersReceived.setVerificationDate(columns[5].getValue());
			}
			if (columns[6].getValue() != null) {
				aInternationalTransfersReceived.setAgreement(columns[6].getValue());
			}
			if (columns[7].getValue() != null) {
				aInternationalTransfersReceived.setAccountType(columns[7].getValue());
			}
			if (columns[8].getValue() != null) {
				aInternationalTransfersReceived.setAccount(columns[8].getValue());
			}
			if (columns[9].getValue() != null) {
				aInternationalTransfersReceived.setPreliminaryAgreementDate(columns[9].getValue());
			}
			if (columns[10].getValue() != null) {
				aInternationalTransfersReceived.setOfficial(Integer.parseInt(columns[10].getValue()));
			}
			if (columns[11].getValue() != null) {
				aInternationalTransfersReceived.setOfficialDescription(columns[11].getValue());
			}
			if (columns[12].getValue() != null) {
				aInternationalTransfersReceived.setCategory(columns[12].getValue());
			}
			if (columns[13].getValue() != null) {
				aInternationalTransfersReceived.setCategoryDescription(columns[13].getValue());
			}
			if (columns[14].getValue() != null) {
				aInternationalTransfersReceived.setOperationNumber(Integer.parseInt(columns[14].getValue()));
			}
			if (columns[15].getValue() != null) {
				aInternationalTransfersReceived.setOperationDescription(columns[15].getValue());
			}
			if (columns[16].getValue() != null) {
				aInternationalTransfersReceived.setOfficeBelongNumber(Integer.parseInt(columns[16].getValue()));
			}
			if (columns[17].getValue() != null) {
				aInternationalTransfersReceived.setOfficeName(columns[17].getValue());
			}
			if (columns[18].getValue() != null) {
				aInternationalTransfersReceived.setOriginatorNumber(Integer.parseInt(columns[18].getValue()));
			}
			if (columns[19].getValue() != null) {
				aInternationalTransfersReceived.setOriginatorName(columns[19].getValue());
			}
			if (columns[20].getValue() != null) {
				aInternationalTransfersReceived.setIdNumber(columns[20].getValue());
			}
			if (columns[21].getValue() != null) {
				aInternationalTransfersReceived.setOriginatorAddressNumber(Integer.parseInt(columns[21].getValue()));
			}
			if (columns[22].getValue() != null) {
				aInternationalTransfersReceived.setOriginatorAddress(columns[22].getValue());
			}
			if (columns[23].getValue() != null) {
				aInternationalTransfersReceived.setBeneficiaryName(columns[23].getValue());
			}
			if (columns[24].getValue() != null) {
				aInternationalTransfersReceived.setBeneficiaryAddress(columns[24].getValue());
			}
			if (columns[25].getValue() != null) {
				aInternationalTransfersReceived.setContinent(columns[25].getValue());
			}
			if (columns[26].getValue() != null) {
				aInternationalTransfersReceived.setBeneficiaryContinent(columns[26].getValue());
			}
			if (columns[27].getValue() != null) {
				aInternationalTransfersReceived.setBeneficiaryCountryNumber(Integer.parseInt(columns[27].getValue()));
			}
			if (columns[28].getValue() != null) {
				aInternationalTransfersReceived.setBeneficiaryCountryName(columns[28].getValue());
			}
			if (columns[29].getValue() != null) {
				aInternationalTransfersReceived.setBeneficiaryCityNumber(Integer.parseInt(columns[29].getValue()));
			}
			if (columns[30].getValue() != null) {
				aInternationalTransfersReceived.setBeneficiaryCityName(columns[30].getValue());
			}
			if (columns[31].getValue() != null) {
				aInternationalTransfersReceived.setAmount(Double.parseDouble(columns[31].getValue()));
			}
			if (columns[32].getValue() != null) {
				aInternationalTransfersReceived.setCurrencyNumber(Integer.parseInt(columns[32].getValue()));
			}
			if (columns[33].getValue() != null) {
				aInternationalTransfersReceived.setCurrencyName(columns[33].getValue());
			}
			if (columns[34].getValue() != null) {
				aInternationalTransfersReceived.setPriority(columns[34].getValue());
			}
			if (columns[35].getValue() != null) {
				aInternationalTransfersReceived.setMessage(columns[35].getValue());
			}
			if (columns[36].getValue() != null) {
				aInternationalTransfersReceived.setMessageName(columns[36].getValue());
			}
			if (columns[37].getValue() != null) {
				aInternationalTransfersReceived.setIssueDate(columns[37].getValue());
			}
			if (columns[38].getValue() != null) {
				aInternationalTransfersReceived.setTerm(Integer.parseInt(columns[38].getValue()));
			}
			if (columns[39].getValue() != null) {
				aInternationalTransfersReceived.setDueDate(columns[39].getValue());
			}
			if (columns[40].getValue() != null) {
				aInternationalTransfersReceived.setNotification(columns[40].getValue());
			}
			if (columns[41].getValue() != null) {
				aInternationalTransfersReceived.setOnBehalfOfAddress(columns[41].getValue());
			}
			if (columns[42].getValue() != null) {
				aInternationalTransfersReceived.setLastShipmentDate(columns[42].getValue());
			}
			if (columns[43].getValue() != null) {
				aInternationalTransfersReceived.setIssueReference(columns[43].getValue());
			}
			if (columns[44].getValue() != null) {
				aInternationalTransfersReceived.setTransactionDate(columns[44].getValue());
			}
			transfersReceivedList.add(aInternationalTransfersReceived);
		}
		aInternationalTransfersReceivedResponse.setReturnCode(aProcedureResponse.getReturnCode());
		Message[] message = Utils.returnArrayMessage(aProcedureResponse);
		aInternationalTransfersReceivedResponse.setMessages(message);

		aInternationalTransfersReceivedResponse.setInternationalTransfersReceivedCollection(transfersReceivedList);
		return aInternationalTransfersReceivedResponse;
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
