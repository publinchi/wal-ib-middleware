package com.cobiscorp.ecobis.orchestration.core.ib.common;

import java.util.Map;

import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;

public class SaveAdditionalDataImpl extends SaveAdditionalData {

    public Boolean saveData(String movementType, boolean isOnline, Map<String, String> data) {
        return saveAdditionalData(movementType, isOnline, data);
    }

	public Boolean saveData(String movementType, boolean isOnline, Map<String, String> data, String operation) {
		return saveAdditionalData(movementType, isOnline, data, operation);
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		
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