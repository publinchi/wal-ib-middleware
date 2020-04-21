package com.cobiscorp.ecobis.ib.orchestration.base.utils.transaction;
import java.util.Map;

import com.cobiscorp.ecobis.ib.application.dtos.ReExecutionRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ReExecutionResponse;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;


public class UtilsTransaction {

	public static ReExecutionRequest transformReExecutionRequest(IProcedureRequest anOriginalRequest) {
		IProcedureRequest wOriginalRequest = anOriginalRequest;
		ReExecutionRequest reExecutionRequest = new ReExecutionRequest();

		reExecutionRequest.setPriority("5");
		if (!Utils.isNullOrEmty(wOriginalRequest.readValueFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH)))
			reExecutionRequest.setSsnBranch(wOriginalRequest.readValueFieldInHeader(ICOBISTS.HEADER_SSN_BRANCH));

		if (!Utils.isNullOrEmty(wOriginalRequest.readValueFieldInHeader(ICOBISTS.HEADER_SSN)))
			reExecutionRequest.setSsnCentral(wOriginalRequest.readValueFieldInHeader(ICOBISTS.HEADER_SSN));

		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@t_trn")))
			reExecutionRequest.setTrn(wOriginalRequest.readValueParam("@t_trn"));

		if (!Utils.isNullOrEmty(wOriginalRequest.readValueParam("@s_srv")))
			reExecutionRequest.setSrv(wOriginalRequest.readValueParam("@s_srv"));
		reExecutionRequest.setIn_line("N");

		return reExecutionRequest;
	}
}
