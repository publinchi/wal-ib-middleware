package com.cobiscorp.ecobis.ib.orchestration.interfaces;

import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.ecobis.ib.application.dtos.ParametersACHRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ParametersACHResponse;

public interface ICoreServiceParametersACH {

	ParametersACHResponse searchParametersACH(ParametersACHRequest aParametersACH) throws CTSServiceException, CTSInfrastructureException;
}
