package com.cobiscorp.orchestration.core.ib.admin;

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
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.IdentificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.IdentificationResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Identification;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMask;

@Component(name = "MaskQuery", immediate = false)
@Service(value = { ICoreServiceMask.class })
@Properties(value = { @Property(name = "service.description", value = "MaskQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "MaskQuery") })
public class MaskQuery extends SPJavaOrchestrationBase implements ICoreServiceMask {

	private static ILogger logger = LogFactory.getLogger(MaskQuery.class);
	private static final String SP_NAME = "cobis..sp_tipo_iden";
	private static final int COL_TYPE = 0;
	private static final int COL_NAME = 1;
	private static final int COL_MASK = 2;
	private static final int COL_CUSTOMER_TYPE = 3;
	private static final int COL_PROVINCE_VALIDATE = 4;
	private static final int COL_QUICK_OPENING = 5;
	private static final int COL_LOCK_CUSTOMER = 6;
	private static final int COL_NATIONALITY = 7;
	private static final int COL_CHECK_SUM = 8;

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
	 * com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMask#getMask
	 * (com.cobiscorp.ecobis.ib.application.dtos.IdentificationRequest)
	 */
	@Override
	public IdentificationResponse getMask(IdentificationRequest aIdentificationRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getMask");
		}

		IProcedureResponse pResponse = Execution(SP_NAME, aIdentificationRequest, "getMask");
		IdentificationResponse wIdentificationResponse = transformToMaskResponse(pResponse, "getMask");
		return wIdentificationResponse;
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

	private IProcedureResponse Execution(String spName, IdentificationRequest aIdentificationRequest, String method) {
		IProcedureRequest request = initProcedureRequest(aIdentificationRequest.getOriginalRequest());
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				aIdentificationRequest.getTarget());
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1445");
		request.setSpName(spName);

		request.addInputParam("@i_operacion", ICTSTypes.SQLCHAR, "H");
		request.addInputParam("@t_trn", ICTSTypes.SQLINT4, "1445");
		request.addInputParam("@i_tpersona", ICTSTypes.SQLVARCHAR, aIdentificationRequest.getTypePerson());

		if (logger.isDebugEnabled()) {
			logger.logDebug("@i_tpersona: " + aIdentificationRequest.getTypePerson());

		}
		if (logger.isDebugEnabled()) {
			logger.logDebug("Request Corebanking: " + request.getProcedureRequestAsString());
		}

		IProcedureResponse pResponse = executeCoreBanking(request);
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response Identification: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isInfoEnabled()) {
			logger.logInfo("*** Finalize Response Identification*** ");
		}

		return pResponse;
	}

	/**
	 * @param pResponse
	 * @param string
	 * @return
	 */
	private IdentificationResponse transformToMaskResponse(IProcedureResponse pResponse, String method) {
		IdentificationResponse wIdentificationResponse = new IdentificationResponse();

		List<Identification> listIdentification = null;
		if (logger.isInfoEnabled())
			logger.logInfo("*** ProcedureResponse: ***" + pResponse.getProcedureResponseAsString());
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + pResponse.getProcedureResponseAsString());
		}
		IResultSetRow[] rowsIdentification = pResponse.getResultSet(1).getData().getRowsAsArray();
		if (method.equals("getMask")) {
			listIdentification = new ArrayList<Identification>();
			for (int i = 0; i < rowsIdentification.length; i++) {
				Identification wIdentification = new Identification();
				IResultSetRow iResultSetRow = rowsIdentification[i];
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				wIdentification.setType(columns[COL_TYPE].getValue());
				wIdentification.setName(columns[COL_NAME].getValue());
				wIdentification.setMask(columns[COL_MASK].getValue());
				wIdentification.setCustomerType(columns[COL_CUSTOMER_TYPE].getValue());
				wIdentification.setProvinceValidate(columns[COL_PROVINCE_VALIDATE].getValue());
				wIdentification.setQuickOpening(columns[COL_QUICK_OPENING].getValue());
				wIdentification.setLockCustomer(columns[COL_LOCK_CUSTOMER].getValue());
				wIdentification.setNationality(columns[COL_NATIONALITY].getValue());
				wIdentification.setCheckSum(columns[COL_CHECK_SUM].getValue());
				listIdentification.add(wIdentification);
			}
		}
		wIdentificationResponse.setListIdentification(listIdentification);
		return wIdentificationResponse;
	}
}
