package com.cobiscorp.ecobis.orchestration.core.ib.subtypes;

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
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceBankGuarantee;
import com.cobiscorp.ecobis.ib.application.dtos.BankGuaranteeRequest;
import com.cobiscorp.ecobis.ib.application.dtos.BankGuaranteeResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.SubTypeGuarantee;

@Component(name = "SubTypesQuery", immediate = false)
@Service(value = { ICoreServiceBankGuarantee.class })
@Properties(value = { @Property(name = "service.description", value = "SubTypesQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "SubTypesQuery") })
public class SubTypesQuery extends SPJavaOrchestrationBase implements ICoreServiceBankGuarantee {
	private static ILogger logger = LogFactory.getLogger(SubTypesQuery.class);
	private static final String SP_NAME = "cob_comext..sp_clase_tipo_grb";	
	private static final int COL_ID = 3;
	private static final int COL_VALUE = 2;

	@Override
	public BankGuaranteeResponse getSubTypes(BankGuaranteeRequest aBankGuaranteeRequest)
			throws CTSServiceException, CTSInfrastructureException {
		if (logger.isDebugEnabled()) {
			logger.logInfo("INICIANDO SERVICIO CORE-COBIS: getSubTypes");
		}

		IProcedureResponse pResponse = Execution(SP_NAME, aBankGuaranteeRequest);
		BankGuaranteeResponse bankGuaranteeResponse = transformToSubTypesResponse(pResponse);
		return bankGuaranteeResponse;
	}
	
	private IProcedureResponse Execution(String SpName, BankGuaranteeRequest aBankGuaranteeRequest)
			throws CTSServiceException, CTSInfrastructureException {

		IProcedureRequest request = initProcedureRequest(aBankGuaranteeRequest.getOriginalRequest());		
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "9999");

		request.setSpName(SpName);

		request.addInputParam("@t_trn", ICTSTypes.SYBINT4, "9999");
		request.addInputParam("@i_opcion", ICTSTypes.SYBCHAR, "S");
		request.addInputParam("@i_estado", ICTSTypes.SYBCHAR, "V");
		request.addInputParam("@i_cod_clase", ICTSTypes.SYBCHAR, aBankGuaranteeRequest.getCondition());
                
		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Request:*** " + request.getProcedureRequestAsString());
		}
		if (logger.isDebugEnabled()) {
			logger.logInfo("*** Start Request *** ");
		}

		IProcedureResponse pResponse = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** Response: *** " + pResponse.getProcedureResponseAsString());
		}
		if (logger.isDebugEnabled()) {
			logger.logInfo("*** Finalize Response *** ");
		}
		return pResponse;
	}
	
	private BankGuaranteeResponse transformToSubTypesResponse(IProcedureResponse aProcedureResponse) {
		BankGuaranteeResponse BankGuaranteeResp = new BankGuaranteeResponse();
		List<SubTypeGuarantee> subTypeGuaranteeCollection = new ArrayList<SubTypeGuarantee>();
		SubTypeGuarantee aSubTypeGuarantee = null;

		if (logger.isDebugEnabled()) {
			logger.logDebug("*** ProcedureResponse: ***" + aProcedureResponse.getProcedureResponseAsString());
		}

		if (aProcedureResponse.getReturnCode() == 0) {

			IResultSetRow[] rowsSubType = aProcedureResponse.getResultSet(1).getData().getRowsAsArray();

			for (IResultSetRow iResultSetRow2 : rowsSubType) {
				IResultSetRowColumnData[] columns2 = iResultSetRow2.getColumnsAsArray();				
				aSubTypeGuarantee = new SubTypeGuarantee();
				aSubTypeGuarantee.setId(columns2[COL_ID].getValue());
				aSubTypeGuarantee.setValue(columns2[COL_VALUE].getValue());
				subTypeGuaranteeCollection.add(aSubTypeGuarantee);				
			}
			BankGuaranteeResp.setSubTypeGuaranteeCollection(subTypeGuaranteeCollection);
		} else {

			BankGuaranteeResp.setMessages(Utils.returnArrayMessage(aProcedureResponse));
		}

		BankGuaranteeResp.setReturnCode(aProcedureResponse.getReturnCode());

		return BankGuaranteeResp;
	}
    
	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		return null;
	}
	
	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		return null;
	}
}
