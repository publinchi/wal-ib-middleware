package com.cobiscorp.ecobis.orchestration.core.ib.status;

import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureRequestParam;

public class Util {

	public static void copyParam(String wParamName, IProcedureRequest wIProcedureRequestSource,
			IProcedureRequest wIProcedureRequestResult) {
		IProcedureRequestParam wPRParam = wIProcedureRequestSource.readParam(wParamName);
		if (wPRParam != null) {
			wIProcedureRequestResult.addParam(wPRParam.getName(), wPRParam.getDataType(), wPRParam.getIOType(),
					wPRParam.getLen(), wPRParam.getValue());
		}

	}
}
