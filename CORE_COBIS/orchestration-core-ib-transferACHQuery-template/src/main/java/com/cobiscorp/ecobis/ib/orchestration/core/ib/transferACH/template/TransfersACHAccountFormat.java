package com.cobiscorp.ecobis.ib.orchestration.core.ib.transferACH.template;

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
import com.cobiscorp.cobisv.commons.context.CobisSession;
import com.cobiscorp.cobisv.commons.context.Context;
import com.cobiscorp.cobisv.commons.context.ContextManager;
import com.cobiscorp.ecobis.ib.application.dtos.AchAccountFormatRequest;
import com.cobiscorp.ecobis.ib.application.dtos.AchAccountFormatRespon;
import com.cobiscorp.ecobis.ib.orchestration.dtos.AchAccountFormat;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreSerciceAchAccountFormat;

@Component(name = "TransfersACHAccountFormat", immediate = false)
@Service(value = { ICoreSerciceAchAccountFormat.class })
@Properties(value = { @Property(name = "service.description", value = "TransfersACHAccountFormat"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TransfersACHAccountFormat") })

public class TransfersACHAccountFormat extends SPJavaOrchestrationBase implements ICoreSerciceAchAccountFormat {
	private static final String COBIS_CONTEXT = "COBIS";
	private static ILogger logger = LogFactory.getLogger(TransfersACHAccountFormat.class);

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public AchAccountFormatRespon getACHAcoountResponse(AchAccountFormatRequest aAchAccountFormatRequest)
			throws CTSServiceException, CTSInfrastructureException {

		if (logger.isInfoEnabled()) {
			logger.logInfo("INICIANDO SERVICIO: GetACHAccountFormat");
		}

		Context context = ContextManager.getContext();
		CobisSession session = (CobisSession) context.getSession();
		IProcedureRequest anOriginalRequest = new ProcedureRequestAS();
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, anOriginalRequest.readValueParam("@t_trn"));
		anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);

		anOriginalRequest.setSpName("cob_remesas..sp_tr_long_cta_ach");
		anOriginalRequest.addInputParam("@s_srv", ICTSTypes.SQLVARCHAR, session.getServer());
		anOriginalRequest.addInputParam("@s_ofi", ICTSTypes.SQLINT2, session.getOffice());
		anOriginalRequest.addInputParam("@s_user", ICTSTypes.SQLVARCHAR, session.getUser());
		anOriginalRequest.addInputParam("@s_term", ICTSTypes.SQLVARCHAR, session.getTerminal());
		anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINTN, "618"); // anOriginalRequest.readValueParam("@t_trn"));
		anOriginalRequest.addInputParam("@i_banco", ICTSTypes.SQLINT2, aAchAccountFormatRequest.getId().toString());
		IProcedureResponse pResponse = executeCoreBanking(anOriginalRequest);
		AchAccountFormatRespon aAchAccountFormatresponse = transformToAchAccountFormatResponse(pResponse);

		return aAchAccountFormatresponse;
	}

	private AchAccountFormatRespon transformToAchAccountFormatResponse(IProcedureResponse pResponse) {
		AchAccountFormatRespon achAccountFormatRespon = new AchAccountFormatRespon();
		List<AchAccountFormat> atransferACHLIST = new ArrayList<AchAccountFormat>();
		AchAccountFormat aAchAccountFormat = null;
		if (logger.isDebugEnabled()) {
			logger.logDebug("ProcedureResponse: " + pResponse.getProcedureResponseAsString());
		}

		if (pResponse.getReturnCode() == 0) {
			IResultSetRow[] rowsAchAccountFormat = pResponse.getResultSet(1).getData().getRowsAsArray();
			for (IResultSetRow iResultSetRow : rowsAchAccountFormat) {
				IResultSetRowColumnData[] columns = iResultSetRow.getColumnsAsArray();
				aAchAccountFormat = new AchAccountFormat();
				aAchAccountFormat.setId(Integer.parseInt(columns[0].getValue()));
				aAchAccountFormat.setDescription(columns[1].getValue());
				aAchAccountFormat.setSubsidiary(Integer.parseInt(columns[3].getValue()));
				aAchAccountFormat.setStatus(columns[4].getValue());
				aAchAccountFormat.setAccountTypeId(Integer.parseInt(columns[5].getValue()));
				aAchAccountFormat.setAccountType(columns[6].getValue());
				aAchAccountFormat.setLengthAccount(Integer.parseInt(columns[7].getValue()));
				atransferACHLIST.add(aAchAccountFormat);
			}
			achAccountFormatRespon.setAchAccountFormatCollection(atransferACHLIST);
		}

		achAccountFormatRespon.setReturnCode(pResponse.getReturnCode());
		return achAccountFormatRespon;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}
