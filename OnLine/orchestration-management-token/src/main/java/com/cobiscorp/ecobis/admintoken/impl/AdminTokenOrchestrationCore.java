package com.cobiscorp.ecobis.admintoken.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;
import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IMessageBlock;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.MessageBlock;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.admintoken.dto.DataTokenRequest;
import com.cobiscorp.ecobis.admintoken.dto.DataTokenResponse;
import com.cobiscorp.ecobis.admintoken.interfaces.IAdminTokenUser;

@Component(name = "AdminTokenOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "Service to call create function token"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.trn", value = "1875902"),
		@Property(name = "service.identifier", value = "AdminTokenOrchestrationCore") })
public class AdminTokenOrchestrationCore extends SPJavaOrchestrationBase {

	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	static final String RESPONSE_CANCEL_FLOW_DOUBLE_AUTHORIZATION = "RESPONSE_CANCEL_FLOW_DOUBLE_AUTHORIZATION";
	static final String TRN_INSERT_FILE = "1875902";
	private static ILogger logger = LogFactory.getLogger(AdminTokenOrchestrationCore.class);

	@Reference(bind = "setAdminTokenUser", unbind = "unsetAdminTokeUser", cardinality = ReferenceCardinality.MANDATORY_UNARY)
	private IAdminTokenUser adminTokenUser;

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);
		IProcedureResponse responseGenerateToken = callGenerateToken(aBagSPJavaOrchestration);
		return responseGenerateToken;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(
					"Component-Name: " + this.getClass().getSimpleName() + ", Component-Version: $$project.version$$");
		}
	}

	private IProcedureResponse callGenerateToken(Map<String, Object> aBagSPJavaOrchestration) {
		ProcedureRequestAS wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		DataTokenRequest dtoRequest = new DataTokenRequest();
		IProcedureResponse pr = new ProcedureResponseAS();

		if (logger.isInfoEnabled()) {
			logger.logInfo("Starting updating status file");
			logger.logInfo("Validation input parameters");
		}
		IProcedureResponse wValidateProcedureResponse = validateParameters(aBagSPJavaOrchestration,
				new String[] { "@i_operacion", "@i_login" });
		if (wValidateProcedureResponse != null) {
			return wValidateProcedureResponse;
		}

		String operation = wOriginalRequest.readValueParam("@i_operacion");
		dtoRequest.setLogin(wOriginalRequest.readValueParam("@i_login"));
		dtoRequest.setChannel(1);
		if ("I".equals(operation)) {
			DataTokenResponse generateTokenUser = adminTokenUser.generateTokenUser(dtoRequest);
			if (!generateTokenUser.getSuccess()) {
				if (logger.isInfoEnabled())
					logger.logInfo("Error Create Token");
				pr.setText(generateTokenUser.getMessage().getDescription());
				pr.setReturnCode(Integer.parseInt(generateTokenUser.getMessage().getCode()));
			}
		} else if ("Q".equals(operation)) {
			dtoRequest.setToken(wOriginalRequest.readValueParam("@i_token"));
			DataTokenResponse generateTokenUser = adminTokenUser.validateTokenUser(dtoRequest);
			if (!generateTokenUser.getSuccess()) {
				if (logger.isInfoEnabled())
					logger.logInfo("Error Get Token");
				pr.setText(generateTokenUser.getMessage().getDescription());
				pr.addParam("@o_token_valido", ICTSTypes.SQLVARCHAR, 0, "N");
			} else {
				pr.setReturnCode(0);
				pr.addParam("@o_token_valido", ICTSTypes.SQLVARCHAR, 0, "S");
			}
		}
		return pr;
	}

	/**
	 * Copy messages from sourceResponse to targetResponse
	 * 
	 * @param aSourceResponse
	 * @param aTargetResponse
	 */
	protected void addMessagesFromResponse(IProcedureResponse aSourceResponse, IProcedureResponse aTargetResponse) {
		@SuppressWarnings("rawtypes")
		Iterator wIterator = aSourceResponse.getMessages().iterator();
		while (wIterator.hasNext()) {
			Object wOMessage = wIterator.next();
			if (wOMessage instanceof IMessageBlock) {
				IMessageBlock wBlock = (IMessageBlock) wOMessage;
				aTargetResponse.addMessage(wBlock.getMessageNumber(), wBlock.getMessageText());
			}
		}
		aTargetResponse.setReturnCode(aSourceResponse.getReturnCode());
	}

	/**
	 * Validate not null parameteres
	 * 
	 * @param aBagSPJavaOrchestration
	 * @param aParams
	 * @return
	 */
	protected IProcedureResponse validateParameters(Map<String, Object> aBagSPJavaOrchestration, String[] aParams) {

		IProcedureRequest wOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		if (logger.isDebugEnabled()) {
			logger.logDebug("validate parameters sp: " + wOriginalRequest.getSpName());
		}

		List<MessageBlock> wErrorMessages = new ArrayList<MessageBlock>(aParams.length);
		boolean wError = false;
		for (int i = 0; i < aParams.length; i++) {
			if (logger.isDebugEnabled()) {
				logger.logDebug("validate parameter: " + aParams[i]);
			}

			if (wOriginalRequest.readParam(aParams[i]) == null) {
				wError = true;
				MessageBlock wMessageBlock = new MessageBlock();
				// 201 number of error in sybase when a parameter is expected
				wMessageBlock.setMessageNumber(201);
				wMessageBlock.setMessageText("Procedure " + wOriginalRequest.getSpName() + " expects parameter"
						+ aParams[i] + " , which was not supplied.");
				wErrorMessages.add(wMessageBlock);
			}

		}

		if (wError == false) {
			return null;
		}

		IProcedureResponse wProcedureResponse = processResponse(wOriginalRequest, aBagSPJavaOrchestration);
		wProcedureResponse.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
		Iterator<MessageBlock> wIterator = wErrorMessages.iterator();

		while (wIterator.hasNext()) {
			MessageBlock wMessageBlockTemp = wIterator.next();
			wProcedureResponse.addMessage(wMessageBlockTemp.getMessageNumber(), wMessageBlockTemp.getMessageText());
		}
		wProcedureResponse.setReturnCode(201);

		return wProcedureResponse;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	protected void setAdminTokenUser(IAdminTokenUser adminTokenUser) {
		this.adminTokenUser = adminTokenUser;
	}

	protected void unsetAdminTokeUser(IAdminTokenUser adminTokenUser) {
		if (this.adminTokenUser == adminTokenUser) {
			this.adminTokenUser = null;
		}

	}
}
