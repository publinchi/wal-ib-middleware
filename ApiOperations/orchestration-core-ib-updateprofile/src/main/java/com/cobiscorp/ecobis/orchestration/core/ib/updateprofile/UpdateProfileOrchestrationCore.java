/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.updateprofile;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;

/**
 * @author cecheverria
 * @since Sep 2, 2014
 * @version 1.0.0
 */
@Component(name = "UpdateProfileOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "UpdateProfileOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "UpdateProfileOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_updateProfile")
})
public class UpdateProfileOrchestrationCore extends SPJavaOrchestrationBase {// SPJavaOrchestrationBase
	
	private ILogger logger = (ILogger) this.getLogger();

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
		
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		logger.logDebug("Begin flow, UpdateProfileOrchestrationCore start.");
		boolean wQueryUpdateProfile;
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		wQueryUpdateProfile = queryUpdateProfile(aBagSPJavaOrchestration);
		
		if (wQueryUpdateProfile) {
			logger.logDebug("Ending flow, executeJavaOrchestration failed.");
			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
		}
		
		logger.logDebug("Ending flow, executeJavaOrchestration success.");
		return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}
	
	private Boolean queryUpdateProfile(Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureRequest wQueryUpdateProfileRequest = (IProcedureRequest) aBagSPJavaOrchestration.get("anOriginalRequest");
		String idCustomer = wQueryUpdateProfileRequest.readValueParam("@i_externalCustomerId");
		
		if (idCustomer.equals(""))
			return true;
		logger.logDebug("Begin flow, queryUpdateProfile with id: " + idCustomer);
		
		IProcedureRequest reqTMP = (initProcedureRequest(wQueryUpdateProfileRequest));
		reqTMP.setSpName("cobis..sp_updateProfile");
		reqTMP.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "central");
		reqTMP.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500092");
		reqTMP.addInputParam("@i_externalCustomerId", ICTSTypes.SQLINT1, idCustomer);
		IProcedureResponse wProcedureResponse = executeCoreBanking(reqTMP);
		if (logger.isInfoEnabled()) {
			logger.logDebug("Ending flow, queryUpdateProfile with wProcedureResponse: " + wProcedureResponse.getProcedureResponseAsString());
		}
		aBagSPJavaOrchestration.put("wQueryUpdateProfileResp", wProcedureResponse);
		return wProcedureResponse.hasError();		
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		return null;
		
	}
}
