package com.cobiscorp.ecobis.orchestration.core.ib.timedeposits;

import java.util.HashMap;
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
import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.ecobis.ib.application.dtos.CdPeriodicityResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CdRateResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CdSimulationResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CdTypeResponse;
import com.cobiscorp.ecobis.ib.application.dtos.CertificateDepositCommonRequest;
import com.cobiscorp.ecobis.ib.application.dtos.CertificateDepositResponse;
import com.cobiscorp.ecobis.ib.application.dtos.DetailCdResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Category;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CertificateDeposit;
import com.cobiscorp.ecobis.ib.orchestration.dtos.CertificateDepositResult;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Entity;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Parameters;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Periodicity;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Rate;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Type;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceTimeDepositConfig;

/**
 * @author jveloz
 *
 */
@Component(name = "TimeDepositValidationsOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "TimeDepositValidationsOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "TimeDepositValidationsOrchestrationCore") })
public class TimeDepositValidationsOrchestrationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(TimeDepositValidationsOrchestrationCore.class);
	static final String ORIGINAL_REQUEST = "ORIGINAL_REQUEST";
	private static final String CLASS_NAME = "--->";

	@Reference(referenceInterface = ICoreServiceTimeDepositConfig.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreServiceTimeDepositConfig coreServiceTimeDepositConfig;

	protected void bindCoreService(ICoreServiceTimeDepositConfig service) {
		coreServiceTimeDepositConfig = service;
	}

	protected void unbindCoreService(ICoreServiceTimeDepositConfig service) {
		coreServiceTimeDepositConfig = null;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {

			logger.logInfo("executeJavaOrchestration Validation CD" + anOriginalRequest.toString());
		}

		aBagSPJavaOrchestration.put(ORIGINAL_REQUEST, anOriginalRequest);
		if (logger.isDebugEnabled())
			logger.logDebug("executeJavaOrchestration");

		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreServiceTimeDepositConfig", coreServiceTimeDepositConfig);
		com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils.validateComponentInstance(mapInterfaces);
		Map<String, Object> wprocedureResponse1 = procedureResponse(anOriginalRequest, aBagSPJavaOrchestration);
		//

		Boolean wSuccessExecutionOperation1 = (Boolean) wprocedureResponse1.get("SuccessExecutionOperation");
		IProcedureResponse wIProcedureResponse1 = (IProcedureResponse) wprocedureResponse1.get("IProcedureResponse");

		IProcedureResponse wErrorProcedureResponse = (IProcedureResponse) wprocedureResponse1
				.get("ErrorProcedureResponse");

		if (wErrorProcedureResponse != null) {
			return wErrorProcedureResponse;
		}
		if (!wSuccessExecutionOperation1) {
			return wIProcedureResponse1;
		}
		;
		wIProcedureResponse1 = (IProcedureResponse) aBagSPJavaOrchestration.get("SIMULATION_RESPONSE");
		return wIProcedureResponse1;
	}

	protected Map<String, Object> procedureResponse(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "Opening Deposit Methods");
		}
		HashMap<String, Object> ret = new HashMap<String, Object>();
		ret.put("IProcedureResponse", null);
		ret.put("SuccessExecutionOperation", null);
		ret.put("ErrorProcedureResponse", null);
		String wOperacion = null;
		wOperacion = anOriginalRequest.readValueParam("@i_operacion");
		if (logger.isInfoEnabled()) {
			logger.logInfo("lee parametro del servicio " + wOperacion);
		}
		// GetCDType
		if (wOperacion.equals("H")) {
			// boolean wSuccessExecutionOperation =
			// executeGetCertificateDepositType(anOriginalRequest,
			// aBagSPJavaOrchestration);
			IProcedureResponse wProcedureResponseOperation1 = executeGetCertificateDepositType(anOriginalRequest,
					aBagSPJavaOrchestration);
			ret.put("SuccessExecutionOperation", wProcedureResponseOperation1.hasError());
			if (logger.isDebugEnabled())
				logger.logDebug("result execution operation 1 operacion H: " + wProcedureResponseOperation1.hasError());

			// IProcedureResponse wProcedureResponseOperation1 =
			// initProcedureResponse(anOriginalRequest);
			wProcedureResponseOperation1.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE,
					ICSP.ERROR_EXECUTION_SERVICE);
			ret.put("IProcedureResponse", wProcedureResponseOperation1);
		} else {
			// GetDetailCD
			if (wOperacion.equals("S")) {
				// boolean wSuccessExecutionOperation =
				// executeGetDetailCertificateDeposit(anOriginalRequest,
				// aBagSPJavaOrchestration);
				IProcedureResponse wProcedureResponseOperation1 = executeGetDetailCertificateDeposit(anOriginalRequest,
						aBagSPJavaOrchestration);
				ret.put("SuccessExecutionOperation", wProcedureResponseOperation1.hasError());
				if (logger.isDebugEnabled())
					logger.logDebug(
							"result execution operation 2 operacion S: " + wProcedureResponseOperation1.hasError());
				//
				// IProcedureResponse wProcedureResponseOperation1 =
				// initProcedureResponse(anOriginalRequest);
				wProcedureResponseOperation1.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT,
						ICOBISTS.HEADER_STRING_TYPE, ICSP.ERROR_EXECUTION_SERVICE);
				ret.put("IProcedureResponse", wProcedureResponseOperation1);
			} else {
				// GetCDPeriodicity
				if (wOperacion.equals("P")) {
					// boolean wSuccessExecutionOperation =
					// executeGetCertificateDepositPeriodicity(anOriginalRequest,
					// aBagSPJavaOrchestration);
					IProcedureResponse wProcedureResponseOperation1 = executeGetCertificateDepositPeriodicity(
							anOriginalRequest, aBagSPJavaOrchestration);
					ret.put("SuccessExecutionOperation", wProcedureResponseOperation1.hasError());
					if (logger.isDebugEnabled())
						logger.logDebug(
								"result execution operation 3 operacion P: " + wProcedureResponseOperation1.hasError());
					//
					// IProcedureResponse wProcedureResponseOperation1 =
					// initProcedureResponse(anOriginalRequest);
					wProcedureResponseOperation1.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT,
							ICOBISTS.HEADER_STRING_TYPE, ICSP.ERROR_EXECUTION_SERVICE);
					ret.put("IProcedureResponse", wProcedureResponseOperation1);
				} else {
					// ExecuteCDSimulation
					if (wOperacion.equals("E")) {
						// boolean wSuccessExecutionOperation =
						// executeCertificateDepositSimulation(anOriginalRequest,
						// aBagSPJavaOrchestration);
						IProcedureResponse wProcedureResponseOperation1 = executeCertificateDepositSimulation(
								anOriginalRequest, aBagSPJavaOrchestration);
						ret.put("SuccessExecutionOperation", wProcedureResponseOperation1.hasError());
						if (logger.isDebugEnabled())
							logger.logDebug("result execution operation 6 operacion E: "
									+ wProcedureResponseOperation1.hasError());
						//
						// IProcedureResponse wProcedureResponseOperation1 =
						// initProcedureResponse(anOriginalRequest);
						wProcedureResponseOperation1.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT,
								ICOBISTS.HEADER_STRING_TYPE, ICSP.ERROR_EXECUTION_SERVICE);
						ret.put("IProcedureResponse", wProcedureResponseOperation1);
					} else {
						// GetCDTerm
						if (wOperacion.equals("T")) {
							// boolean wSuccessExecutionOperation =
							// executeGetCertificateDepositTerm(anOriginalRequest,
							// aBagSPJavaOrchestration);
							IProcedureResponse wProcedureResponseOperation1 = executeGetCertificateDepositTerm(
									anOriginalRequest, aBagSPJavaOrchestration);
							ret.put("SuccessExecutionOperation", wProcedureResponseOperation1.hasError());
							if (logger.isDebugEnabled())
								logger.logDebug("result execution operation 4 operacion T: "
										+ wProcedureResponseOperation1.hasError());
							//
							// IProcedureResponse wProcedureResponseOperation1 =
							// initProcedureResponse(anOriginalRequest);
							wProcedureResponseOperation1.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT,
									ICOBISTS.HEADER_STRING_TYPE, ICSP.ERROR_EXECUTION_SERVICE);
							ret.put("IProcedureResponse", wProcedureResponseOperation1);
						} else {
							// GetCDRate
							if (wOperacion.equals("C")) {
								// boolean wSuccessExecutionOperation =
								// executeGetCertificateDepositRate(anOriginalRequest,
								// aBagSPJavaOrchestration);
								IProcedureResponse wProcedureResponseOperation1 = executeGetCertificateDepositRate(
										anOriginalRequest, aBagSPJavaOrchestration);

								ret.put("SuccessExecutionOperation", wProcedureResponseOperation1.hasError());
								if (logger.isDebugEnabled())
									logger.logDebug("result execution operation 5 operacion C: "
											+ wProcedureResponseOperation1.hasError()); // +
																						// wSuccessExecutionOperation);
								//
								// IProcedureResponse
								// wProcedureResponseOperation1 =
								// initProcedureResponse(anOriginalRequest);
								wProcedureResponseOperation1.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT,
										ICOBISTS.HEADER_STRING_TYPE, ICSP.ERROR_EXECUTION_SERVICE);
								ret.put("IProcedureResponse", wProcedureResponseOperation1);
							}
						}
					}
				}
			}
		}
		if (logger.isInfoEnabled())
			logger.logInfo("retorno ProcedureResponse " + ret.toString());
		return ret;
	}

	// executeGetCertificateDepositType

	// GetCDType
	protected IProcedureResponse executeGetCertificateDepositType(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeOperation1 - executeGetCertificateDepositType");

		IProcedureResponse wProcedureResponse = initProcedureResponse(anOriginalRequest);
		CdTypeResponse wCdTypeResponse = new CdTypeResponse();
		try {
			CertificateDepositCommonRequest certificateDepositCommonRequest = transformRequestToDto(
					aBagSPJavaOrchestration);

			wCdTypeResponse = coreServiceTimeDepositConfig.getCertificateDepositType(certificateDepositCommonRequest);

			wProcedureResponse = transformDtoToResponse1(wCdTypeResponse, aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put("SIMULATION_RESPONSE", wProcedureResponse);

			return wProcedureResponse; // !wProcedureResponse.hasError();
		} catch (CTSServiceException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSServiceException:" + e.getMessage());
			e.printStackTrace();
			aBagSPJavaOrchestration.put("SIMULATION_RESPONSE", null);
			return wProcedureResponse; // false;
		} catch (CTSInfrastructureException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSInfrastructureException:" + e.getMessage());
			aBagSPJavaOrchestration.put("SIMULATION_RESPONSE", null);
			return wProcedureResponse; // false;
		}
	};

	// executeGetDetailCertificateDeposit
	// GetDetailCD
	protected IProcedureResponse executeGetDetailCertificateDeposit(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeOperation2 - executeGetDetailCertificateDeposit");

		IProcedureResponse wProcedureResponse = initProcedureResponse(anOriginalRequest);
		DetailCdResponse wDetailCdResponse = new DetailCdResponse();
		try {
			CertificateDepositCommonRequest certificateDepositCommonRequest = transformRequestToDto(
					aBagSPJavaOrchestration);
			wDetailCdResponse = coreServiceTimeDepositConfig
					.getDetailCertificateDeposit(certificateDepositCommonRequest);

			wProcedureResponse = transformDtoToResponse2(wDetailCdResponse, aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put("SIMULATION_RESPONSE", wProcedureResponse);

			return wProcedureResponse; // !wProcedureResponse.hasError();
		} catch (CTSServiceException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSServiceException:" + e.getMessage());
			e.printStackTrace();
			aBagSPJavaOrchestration.put("SIMULATION_RESPONSE", null);
			return wProcedureResponse; // false;
		} catch (CTSInfrastructureException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSInfrastructureException:" + e.getMessage());
			aBagSPJavaOrchestration.put("SIMULATION_RESPONSE", null);
			return wProcedureResponse; // false;
		}
	};

	// executeGetCertificateDepositPeriodicity

	// GetCDPeriodicity
	protected IProcedureResponse executeGetCertificateDepositPeriodicity(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeOperation3 - executeGetCertificateDepositPeriodicity");

		IProcedureResponse wProcedureResponse = initProcedureResponse(anOriginalRequest);
		CdPeriodicityResponse wCdPeriodicityResponse = new CdPeriodicityResponse();
		try {
			CertificateDepositCommonRequest certificateDepositCommonRequest = transformRequestToDtoPeriodicity(
					aBagSPJavaOrchestration);

			wCdPeriodicityResponse = coreServiceTimeDepositConfig
					.getCertificateDepositPeriodicity(certificateDepositCommonRequest);

			wProcedureResponse = transformDtoToResponse3(wCdPeriodicityResponse, aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put("SIMULATION_RESPONSE", wProcedureResponse);

			return wProcedureResponse; // !wProcedureResponse.hasError();
		} catch (CTSServiceException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSServiceException:" + e.getMessage());
			e.printStackTrace();
			aBagSPJavaOrchestration.put("SIMULATION_RESPONSE", null);
			return wProcedureResponse; // false;
		} catch (CTSInfrastructureException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSInfrastructureException:" + e.getMessage());
			aBagSPJavaOrchestration.put("SIMULATION_RESPONSE", null);
			return wProcedureResponse; // false;
		}
	};

	// GetCDTerm
	// executeGetCertificateDepositTerm
	protected IProcedureResponse executeGetCertificateDepositTerm(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeOperation4 - executeGetCertificateDepositTerm");

		IProcedureResponse wProcedureResponse = initProcedureResponse(anOriginalRequest);
		CertificateDepositResponse wCertificateDepositResponse = new CertificateDepositResponse();
		try {
			CertificateDepositCommonRequest certificateDepositCommonRequest = transformRequestToDtoTermCD(
					aBagSPJavaOrchestration);

			wCertificateDepositResponse = coreServiceTimeDepositConfig
					.getCertificateDepositTerm(certificateDepositCommonRequest);

			wProcedureResponse = transformDtoToResponse4(wCertificateDepositResponse, aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put("SIMULATION_RESPONSE", wProcedureResponse);

			return wProcedureResponse; // !wProcedureResponse.hasError();
		} catch (CTSServiceException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSServiceException:" + e.getMessage());
			e.printStackTrace();
			aBagSPJavaOrchestration.put("SIMULATION_RESPONSE", null);
			return wProcedureResponse; // false;
		} catch (CTSInfrastructureException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSInfrastructureException:" + e.getMessage());
			aBagSPJavaOrchestration.put("SIMULATION_RESPONSE", null);
			return wProcedureResponse; // false;
		}
	};

	// GetCDRate
	protected IProcedureResponse executeGetCertificateDepositRate(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeOperation5 - executeGetCertificateDepositRate");

		IProcedureResponse wProcedureResponse = initProcedureResponse(anOriginalRequest);
		CdRateResponse wCdRateResponse = new CdRateResponse();
		try {
			CertificateDepositCommonRequest certificateDepositCommonRequest = transformRequestToDtoRateCD(
					aBagSPJavaOrchestration);
			// logger.logInfo("transformRequestToDtoRateCD
			// "+certificateDepositCommonRequest.getCertificateDeposit().getTerm());
			wCdRateResponse = coreServiceTimeDepositConfig.getCertificateDepositRate(certificateDepositCommonRequest);

			wProcedureResponse = transformDtoToResponse5(wCdRateResponse, aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put("SIMULATION_RESPONSE", wProcedureResponse);

			return wProcedureResponse; // !wProcedureResponse.hasError();
		} catch (CTSServiceException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSServiceException:" + e.getMessage());
			e.printStackTrace();
			aBagSPJavaOrchestration.put("SIMULATION_RESPONSE", null);
			return wProcedureResponse; // false;
		} catch (CTSInfrastructureException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSInfrastructureException:" + e.getMessage());
			aBagSPJavaOrchestration.put("SIMULATION_RESPONSE", null);
			return wProcedureResponse; // false;
		}
	};

	// executeCertificateDepositSimulation
	protected IProcedureResponse executeCertificateDepositSimulation(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "executeOperation6 - executeCertificateDepositSimulation");

		IProcedureResponse wProcedureResponse = initProcedureResponse(anOriginalRequest);
		CdSimulationResponse wCdSimulationResponse = new CdSimulationResponse();
		try {
			CertificateDepositCommonRequest certificateDepositCommonRequest = transformRequestToDtoSimulationCD(
					aBagSPJavaOrchestration);

			wCdSimulationResponse = coreServiceTimeDepositConfig.executeSimulation(certificateDepositCommonRequest);

			wProcedureResponse = transformDtoToResponse6(wCdSimulationResponse, aBagSPJavaOrchestration);
			aBagSPJavaOrchestration.put("SIMULATION_RESPONSE", wProcedureResponse);

			return wProcedureResponse; // !wProcedureResponse.hasError();
		} catch (CTSServiceException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSServiceException:" + e.getMessage());
			e.printStackTrace();
			aBagSPJavaOrchestration.put("SIMULATION_RESPONSE", null);
			return wProcedureResponse; // false;
		} catch (CTSInfrastructureException e) {
			if (logger.isDebugEnabled())
				logger.logInfo(CLASS_NAME + "CTSInfrastructureException:" + e.getMessage());
			aBagSPJavaOrchestration.put("SIMULATION_RESPONSE", null);
			return wProcedureResponse; // false;
		}
	};

	/*********************
	 * Transformación de Request a CertificateDepositCommonRequest
	 ***********************/
	private CertificateDepositCommonRequest transformRequestToDto(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Entrada transformRequestToDto");
		IProcedureRequest wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		//
		CertificateDepositCommonRequest certificateDepositCommonRequest = new CertificateDepositCommonRequest();
		CertificateDeposit certificateDeposit = new CertificateDeposit();
		certificateDeposit.setNemonic(wOriginalRequest.readValueParam("@i_nemonico"));
		certificateDepositCommonRequest.setCertificateDeposit(certificateDeposit);

		certificateDepositCommonRequest.setOriginalRequest(wOriginalRequest);
		return certificateDepositCommonRequest;
	};

	/*********************
	 * Transformación de Request a CertificateDepositCommonRequest Periodicity
	 ***********************/
	private CertificateDepositCommonRequest transformRequestToDtoPeriodicity(
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Entrada transformRequestToDtoPeriodicity");
		IProcedureRequest wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		//
		CertificateDepositCommonRequest certificateDepositCommonRequest = new CertificateDepositCommonRequest();
		CertificateDeposit certificateDeposit = new CertificateDeposit();
		certificateDeposit.setNemonic(wOriginalRequest.readValueParam("@i_nemonico"));
		certificateDeposit.setRegType(wOriginalRequest.readValueParam("@i_tipo_reg"));
		certificateDepositCommonRequest.setCertificateDeposit(certificateDeposit);

		certificateDepositCommonRequest.setOriginalRequest(wOriginalRequest);
		return certificateDepositCommonRequest;
	};

	/*********************
	 * Transformación de Request a CertificateDepositCommonRequest Term
	 ***********************/
	private CertificateDepositCommonRequest transformRequestToDtoTermCD(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Entrada transformRequestToDtoTermCD");
		IProcedureRequest wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		//
		CertificateDepositCommonRequest certificateDepositCommonRequest = new CertificateDepositCommonRequest();
		CertificateDeposit certificateDeposit = new CertificateDeposit();
		certificateDeposit.setNemonic(wOriginalRequest.readValueParam("@i_nemonico"));
		certificateDeposit.setTerm(Integer.parseInt(wOriginalRequest.readValueParam("@i_plazo")));
		certificateDeposit.setProcessDate(wOriginalRequest.readValueParam("@i_fecha_valor"));
		certificateDeposit.setExpiration(wOriginalRequest.readValueParam("@i_fecha_ven"));
		certificateDeposit.setTermDate(wOriginalRequest.readValueParam("@i_fecha_plazo"));
		certificateDepositCommonRequest.setCertificateDeposit(certificateDeposit);

		certificateDepositCommonRequest.setOriginalRequest(wOriginalRequest);
		return certificateDepositCommonRequest;
	};

	/*********************
	 * Transformación de Request a CertificateDepositCommonRequest Rate
	 ***********************/
	private CertificateDepositCommonRequest transformRequestToDtoRateCD(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Entrada transformRequestToDtoRateCD");
		IProcedureRequest wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		//
		CertificateDepositCommonRequest certificateDepositCommonRequest = new CertificateDepositCommonRequest();
		CertificateDeposit certificateDeposit = new CertificateDeposit();
		certificateDeposit.setNemonic(wOriginalRequest.readValueParam("@i_nemonico"));
		certificateDeposit.setOffice(wOriginalRequest.readValueParam("@i_oficina"));
		certificateDeposit.setAmount(Double.parseDouble(wOriginalRequest.readValueParam("@i_monto")));
		certificateDeposit.setTerm(Integer.parseInt(wOriginalRequest.readValueParam("@i_plazo")));
		certificateDeposit.setMoney(Integer.parseInt(wOriginalRequest.readValueParam("@i_moneda")));
		certificateDeposit.setRegType(wOriginalRequest.readValueParam("@i_tipo_reg"));
		certificateDepositCommonRequest.setCertificateDeposit(certificateDeposit);
		if (logger.isInfoEnabled())
			logger.logInfo(" transformRequestToDtoRateCD " + aBagSPJavaOrchestration);
		certificateDepositCommonRequest.setOriginalRequest(wOriginalRequest);
		return certificateDepositCommonRequest;
	};

	/*********************
	 * Transformación de Request a CertificateDepositCommonRequest Simulation
	 ***********************/

	private CertificateDepositCommonRequest transformRequestToDtoSimulationCD(
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Entrada transformRequestToDtoSimulationCD ");
		IProcedureRequest wOriginalRequest = (ProcedureRequestAS) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		//
		CertificateDepositCommonRequest certificateDepositCommonRequest = new CertificateDepositCommonRequest();
		CertificateDeposit certificateDeposit = new CertificateDeposit();
		Rate rate = new Rate();
		Entity entity = new Entity();
		certificateDeposit.setNemonic(wOriginalRequest.readValueParam("@i_nemonico"));
		certificateDeposit.setAmount(Double.parseDouble(wOriginalRequest.readValueParam("@i_monto")));
		certificateDeposit.setTerm(Integer.parseInt(wOriginalRequest.readValueParam("@i_plazo")));
		rate.setRate(Double.valueOf(wOriginalRequest.readValueParam("@i_tasa")));
		certificateDeposit.setRate(rate);
		certificateDeposit.setMoney(Integer.parseInt(wOriginalRequest.readValueParam("@i_moneda")));
		certificateDeposit.setCategory(wOriginalRequest.readValueParam("@i_categoria"));
		certificateDeposit.setProcessDate(wOriginalRequest.readValueParam("@i_fecha_valor"));
		entity.setCodCustomer(Integer.parseInt(wOriginalRequest.readValueParam("@i_ente")));
		certificateDepositCommonRequest.setEntity(entity);
		certificateDeposit.setPayDay(Integer.parseInt(wOriginalRequest.readValueParam("@i_dia_pago")));
		certificateDepositCommonRequest.setCertificateDeposit(certificateDeposit);
		certificateDepositCommonRequest.setOriginalRequest(wOriginalRequest);
		return certificateDepositCommonRequest;
	};

	/*********************
	 * Transformación de CdTypeResponse a Response
	 ***********************/
	private IProcedureResponse transformDtoToResponse1(CdTypeResponse response,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Salida CdTypeResponse :");
		IProcedureResponse wResponse = initProcedureResponse(
				(IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));
		IResultSetHeader metaData = new ResultSetHeader();
		metaData = new ResultSetHeader();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("NUMBER_OF_RESULTS", ICTSTypes.SQLINT4, 4));

		IResultSetRow row = new ResultSetRow();
		// IResultSetBlock resultBlock;
		IResultSetData data = new ResultSetData();
		if (response.getReturnCode() == 0) {
			if (response.getListCdType() != null) {

				// Type
				if (response.getListCdType().size() > 0) {
					metaData = new ResultSetHeader();
					metaData.addColumnMetaData(new ResultSetHeaderColumn("TIPO", ICTSTypes.SQLVARCHAR, 20));
					for (Type obj : response.getListCdType()) {
						row = new ResultSetRow();
						row.addRowData(1, new ResultSetRowColumnData(false, obj.getType().toString()));
						data.addRow(row);
					}
					;
					IResultSetBlock resultBlock1 = new ResultSetBlock(metaData, data);
					wResponse.addResponseBlock(resultBlock1);
				}
				;
				// Parameter
				if (response.getListParameters().size() > 0) {
					metaData = new ResultSetHeader();
					metaData.addColumnMetaData(new ResultSetHeaderColumn("VALOR", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("DESCRIPCION", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("FACTOR", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("PORCENTAJE", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("FACTOR_DIAS", ICTSTypes.SQLVARCHAR, 20));
					for (Parameters obj : response.getListParameters()) {
						row = new ResultSetRow();
						row.addRowData(1, new ResultSetRowColumnData(false, obj.getName()));
						row.addRowData(1, new ResultSetRowColumnData(false, ""));
						row.addRowData(1, new ResultSetRowColumnData(false, ""));
						row.addRowData(1, new ResultSetRowColumnData(false, ""));
						row.addRowData(1, new ResultSetRowColumnData(false, ""));
						data.addRow(row);
					}
					;
					IResultSetBlock resultBlock2 = new ResultSetBlock(metaData, data);
					wResponse.addResponseBlock(resultBlock2);
				}
				;
				// Category
				if (response.getListCategory().size() > 0) {
					metaData = new ResultSetHeader();
					metaData.addColumnMetaData(new ResultSetHeaderColumn("CATEGORIA", ICTSTypes.SQLVARCHAR, 20));
					for (Category obj : response.getListCategory()) {
						row = new ResultSetRow();
						row.addRowData(1, new ResultSetRowColumnData(false, obj.getName()));
						data.addRow(row);
					}
					;
					IResultSetBlock resultBlock3 = new ResultSetBlock(metaData, data);
					wResponse.addResponseBlock(resultBlock3);
				}
				;
			}
			;
		}

		// Retorno Código ERROR
		wResponse.setReturnCode(response.getReturnCode());
		if (logger.isInfoEnabled())
			logger.logInfo("CODIGO DE RETORNO TYPE: " + response.getReturnCode());
		// AGREGAR LA DEVOLUCION DE LA COLECCION DE MENSAJES QUE DEVUELVE LA
		// IMPLEMENTACION, PARA MAPEAR EL MENSAJE DE ERROR DEVUELTO
		if (response.getReturnCode() != 0) {
			wResponse = Utils.returnException(response.getMessages());
			return wResponse;
		}
		;
		//
		wResponse.addParam("@o_plazo", ICTSTypes.SQLINT4, 1,
				response.getCertificateDepositResponse().getTerm().toString());
		wResponse.addParam("@o_fecha_ven", ICTSTypes.SQLVARCHAR, 1,
				response.getCertificateDepositResponse().getExpirationDate());

		return wResponse;
	}

	/*********************
	 * Transformación de DetailCdResponse a Response
	 ***********************/
	private IProcedureResponse transformDtoToResponse2(DetailCdResponse response,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Salida DetailCdResponse :");
		IProcedureResponse wResponse = initProcedureResponse(
				(IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));
		IResultSetHeader metaData = new ResultSetHeader();
		metaData = new ResultSetHeader();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("NUMBER_OF_RESULTS", ICTSTypes.SQLINT4, 4));

		IResultSetRow row = new ResultSetRow();
		IResultSetBlock resultBlock;
		IResultSetData data = new ResultSetData();
		//
		if (response.getReturnCode() == 0) {
			if (response.getListCertificateDeposit() != null) {

				if (response.getListCertificateDeposit().size() > 0) {
					metaData = new ResultSetHeader();
					metaData.addColumnMetaData(new ResultSetHeaderColumn("TIPO_DE_DPF", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("DESCRIPCION", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("FORMA_PAGO", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("CAPITALIZACION", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("DIAS_REVERSO", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("BASE_CALCULO", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("CODIGO", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("EMISION_INICIAL", ICTSTypes.SQLVARCHAR, 20));

					metaData.addColumnMetaData(new ResultSetHeaderColumn("MANT_STOCK", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("STOCK", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("PRORROGA_AUT", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("DIAS_GRACIA", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("NUM_DIAS_GRACIA", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("NUMERO_PRORROGAS", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("FECHA_COMERCIAL", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("TASA_VARIABLE", ICTSTypes.SQLVARCHAR, 20));

					metaData.addColumnMetaData(new ResultSetHeaderColumn("TASA_EFECTIVA", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("RETIENE_IMPUESTO", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("TRN_DIA_NO_LABOR", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("PAGA_COMISION", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("MANEJA_CUPON", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("CAMBIO_TASA", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("INCREM_DIMIN", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("AREA_CONTABLE", ICTSTypes.SQLVARCHAR, 20));

					metaData.addColumnMetaData(new ResultSetHeaderColumn("TIPO_PERSONA", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("DIAS_CALENDARIO", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(
							new ResultSetHeaderColumn("TIPO_TASA_VARIABLE", ICTSTypes.SQLVARCHAR, 20));

					for (CertificateDeposit obj : response.getListCertificateDeposit()) {
						row = new ResultSetRow();
						row.addRowData(1, new ResultSetRowColumnData(false, obj.getType()));
						row.addRowData(2, new ResultSetRowColumnData(false, obj.getNemonic()));
						row.addRowData(3, new ResultSetRowColumnData(false, obj.getMethodOfPayment()));
						row.addRowData(4, new ResultSetRowColumnData(false, obj.getCapitalize()));
						row.addRowData(5, new ResultSetRowColumnData(false, ""));
						row.addRowData(6, new ResultSetRowColumnData(false, obj.getCalculationBase()));
						row.addRowData(7, new ResultSetRowColumnData(false, ""));
						row.addRowData(8, new ResultSetRowColumnData(false, ""));
						row.addRowData(9, new ResultSetRowColumnData(false, ""));
						row.addRowData(10, new ResultSetRowColumnData(false, ""));
						row.addRowData(11, new ResultSetRowColumnData(false, obj.getExtendedAut()));
						row.addRowData(12, new ResultSetRowColumnData(false, obj.getGraceDays()));
						row.addRowData(13, new ResultSetRowColumnData(false, obj.getGraceDaysNum()));
						row.addRowData(14, new ResultSetRowColumnData(false, ""));
						row.addRowData(15, new ResultSetRowColumnData(false, ""));
						row.addRowData(16, new ResultSetRowColumnData(false, ""));
						row.addRowData(17, new ResultSetRowColumnData(false, ""));
						row.addRowData(18, new ResultSetRowColumnData(false, obj.getTaxRetention()));
						row.addRowData(19, new ResultSetRowColumnData(false, ""));
						row.addRowData(20, new ResultSetRowColumnData(false, ""));
						row.addRowData(21, new ResultSetRowColumnData(false, ""));
						row.addRowData(22, new ResultSetRowColumnData(false, ""));
						row.addRowData(23, new ResultSetRowColumnData(false, ""));
						row.addRowData(24, new ResultSetRowColumnData(false, ""));
						row.addRowData(25, new ResultSetRowColumnData(false, ""));
						row.addRowData(26, new ResultSetRowColumnData(false, ""));
						row.addRowData(27, new ResultSetRowColumnData(false, ""));
						data.addRow(row);
					}
					;
					resultBlock = new ResultSetBlock(metaData, data);
					wResponse.addResponseBlock(resultBlock);
				}
				;
			}
			;

		}

		// Retorno Código ERROR
		wResponse.setReturnCode(response.getReturnCode());
		if (logger.isInfoEnabled())
			logger.logInfo("CODIGO DE RETORNO DETAIL: " + response.getReturnCode());
		// AGREGAR LA DEVOLUCION DE LA COLECCION DE MENSAJES QUE DEVUELVE LA
		// IMPLEMENTACION, PARA MAPEAR EL MENSAJE DE ERROR DEVUELTO
		if (response.getReturnCode() != 0) {
			wResponse = Utils.returnException(response.getMessages());
			return wResponse;
		}
		;
		//
		wResponse.addParam("@o_plazo", ICTSTypes.SQLINT4, 1,
				response.getCertificateDepositResponse().getTerm().toString());
		wResponse.addParam("@o_fecha_ven", ICTSTypes.SQLVARCHAR, 1,
				response.getCertificateDepositResponse().getExpirationDate());

		return wResponse;
	};

	/*********************
	 * Transformación de CdPeriodicityResponse a Response
	 ***********************/
	private IProcedureResponse transformDtoToResponse3(CdPeriodicityResponse response,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Salida CdPeriodicityResponse :");
		IProcedureResponse wResponse = initProcedureResponse(
				(IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));
		IResultSetHeader metaData = new ResultSetHeader();
		metaData = new ResultSetHeader();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("NUMBER_OF_RESULTS", ICTSTypes.SQLINT4, 4));

		IResultSetRow row = new ResultSetRow();
		IResultSetBlock resultBlock;
		IResultSetData data = new ResultSetData();
		//
		if (response.getReturnCode() == 0) {
			if (response.getListPeriodicity() != null) {

				if (response.getListPeriodicity().size() > 0) {
					metaData = new ResultSetHeader();
					metaData.addColumnMetaData(new ResultSetHeaderColumn("VALOR", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("DESCRIPCION", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("FACTOR", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("PORCENTAJE", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("FACTOR_DIAS", ICTSTypes.SQLVARCHAR, 20));
					for (Periodicity obj : response.getListPeriodicity()) {
						row = new ResultSetRow();
						row.addRowData(1, new ResultSetRowColumnData(false, obj.getValue()));
						row.addRowData(2, new ResultSetRowColumnData(false, obj.getDescription()));
						row.addRowData(3, new ResultSetRowColumnData(false, obj.getFactor()));
						row.addRowData(4, new ResultSetRowColumnData(false, obj.getPercentage()));
						row.addRowData(5, new ResultSetRowColumnData(false, obj.getDaysFactor()));

						data.addRow(row);
					}
					;
					resultBlock = new ResultSetBlock(metaData, data);
					wResponse.addResponseBlock(resultBlock);
				}
				;
			}
			;
		}

		// Retorno Código ERROR
		wResponse.setReturnCode(response.getReturnCode());
		if (logger.isInfoEnabled())
			logger.logInfo("CODIGO DE RETORNO PERIODICITY: " + response.getReturnCode());
		// AGREGAR LA DEVOLUCION DE LA COLECCION DE MENSAJES QUE DEVUELVE LA
		// IMPLEMENTACION, PARA MAPEAR EL MENSAJE DE ERROR DEVUELTO
		if (response.getReturnCode() != 0) {
			wResponse = Utils.returnException(response.getMessages());
			return wResponse;
		}
		;
		//
		wResponse.addParam("@o_plazo", ICTSTypes.SQLINT4, 1,
				response.getCertificateDepositResponse().getTerm().toString());
		wResponse.addParam("@o_fecha_ven", ICTSTypes.SQLVARCHAR, 1,
				response.getCertificateDepositResponse().getExpirationDate());

		return wResponse;
	};

	/*********************
	 * Transformación de CertificateDepositResponse a Response
	 ***********************/
	private IProcedureResponse transformDtoToResponse4(CertificateDepositResponse response,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Salida CertificateDepositResponse :");
		IProcedureResponse wResponse = initProcedureResponse(
				(IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));
		// Retorno Código ERROR
		wResponse.setReturnCode(response.getReturnCode());
		if (logger.isInfoEnabled())
			logger.logInfo("CODIGO DE RETORNO RATE: " + response.getReturnCode());
		// AGREGAR LA DEVOLUCION DE LA COLECCION DE MENSAJES QUE DEVUELVE LA
		// IMPLEMENTACION, PARA MAPEAR EL MENSAJE DE ERROR DEVUELTO
		if (response.getReturnCode() != 0) {
			if (logger.isInfoEnabled())
				logger.logInfo("CODIGO DE RETORNO RATE: " + wResponse.getReturnCode());

			wResponse = Utils.returnException(response.getMessages());
			if (logger.isInfoEnabled())
				logger.logInfo("CODIGO DE RETORNO RATE: " + wResponse.getMessages());
			return wResponse;
		} else {
			wResponse.addParam("@o_plazo", ICTSTypes.SQLINT4, 1, response.getTerm().toString());
			wResponse.addParam("@o_fecha_ven", ICTSTypes.SQLVARCHAR, 1, response.getExpirationDate());
		}

		return wResponse;
	};

	/*********************
	 * Transformación de CdRateResponse a Response
	 ***********************/
	private IProcedureResponse transformDtoToResponse5(CdRateResponse response,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Salida CdRateResponse :");
		IProcedureResponse wResponse = initProcedureResponse(
				(IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));
		IResultSetHeader metaData = new ResultSetHeader();
		metaData = new ResultSetHeader();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("NUMBER_OF_RESULTS", ICTSTypes.SQLINT4, 4));

		IResultSetRow row = new ResultSetRow();
		IResultSetBlock resultBlock;
		IResultSetData data = new ResultSetData();
		//
		if (response.getReturnCode() == 0) {
			if (response.getListRate() != null) {

				if (response.getListRate().size() > 0) {
					metaData = new ResultSetHeader();
					metaData.addColumnMetaData(new ResultSetHeaderColumn("TASA", ICTSTypes.SQLFLT8i, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("TASA_MAX", ICTSTypes.SQLFLT8i, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("TASA_MIN", ICTSTypes.SQLFLT8i, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("DESCUENTO", ICTSTypes.SQLVARCHAR, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("AUTORIZADO", ICTSTypes.SQLVARCHAR, 20));
					for (Rate obj : response.getListRate()) {
						row = new ResultSetRow();
						row.addRowData(1, new ResultSetRowColumnData(false, String.valueOf(obj.getRate())));
						row.addRowData(2, new ResultSetRowColumnData(false, String.valueOf(obj.getMaxRate())));
						row.addRowData(3, new ResultSetRowColumnData(false, String.valueOf(obj.getMinRate())));
						row.addRowData(4, new ResultSetRowColumnData(false, obj.getRateDesc()));
						row.addRowData(5, new ResultSetRowColumnData(false, obj.getRateAuthorization()));

						data.addRow(row);
					}
					resultBlock = new ResultSetBlock(metaData, data);
					wResponse.addResponseBlock(resultBlock);
				}
			}
		}

		// Retorno Código ERROR
		wResponse.setReturnCode(response.getReturnCode());
		if (logger.isInfoEnabled())
			logger.logInfo("CODIGO DE RETORNO RATE: " + response.getReturnCode());
		// AGREGAR LA DEVOLUCION DE LA COLECCION DE MENSAJES QUE DEVUELVE LA
		// IMPLEMENTACION, PARA MAPEAR EL MENSAJE DE ERROR DEVUELTO
		if (response.getReturnCode() != 0) {
			if (logger.isInfoEnabled())
				logger.logInfo("CODIGO DE RETORNO RATE: " + wResponse.getReturnCode());
			wResponse = Utils.returnException(response.getMessages());
			if (logger.isInfoEnabled())
				logger.logInfo("CODIGO DE RETORNO RATE: " + wResponse.getMessages());
			return wResponse;
		} else {
			wResponse.addParam("@o_plazo", ICTSTypes.SQLINT4, 1,
					response.getCertificateDepositResponse().getTerm().toString());
			wResponse.addParam("@o_fecha_ven", ICTSTypes.SQLVARCHAR, 1,
					response.getCertificateDepositResponse().getExpirationDate());
		}

		return wResponse;
	};

	/*********************
	 * Transformación de CdSimulationResponse a Response
	 ***********************/
	private IProcedureResponse transformDtoToResponse6(CdSimulationResponse simulationResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isInfoEnabled())
			logger.logInfo(CLASS_NAME + "Transformando Dto de Salida:");
		IProcedureResponse pResponse = initProcedureResponse(
				(IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST));

		IResultSetHeader metaData = new ResultSetHeader();
		metaData = new ResultSetHeader();
		metaData.addColumnMetaData(new ResultSetHeaderColumn("NUMBER_OF_RESULTS", ICTSTypes.SQLINT4, 4));

		IResultSetRow row = new ResultSetRow();
		IResultSetBlock resultBlock;

		IResultSetData data = new ResultSetData();
		if (simulationResponse.getReturnCode() == 0) {
			if (simulationResponse.getListCertificateDepositResult() != null) {

				if (simulationResponse.getListCertificateDepositResult().size() > 0) {
					metaData = new ResultSetHeader();

					metaData.addColumnMetaData(new ResultSetHeaderColumn("INTERES_ESTIMADO", ICTSTypes.SQLFLTNi, 20));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("TOTAL_INT_ESTIMADO", ICTSTypes.SQLFLTNi, 30));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("IMPUESTO_A_PAGAR", ICTSTypes.SQLMONEY, 20));
					metaData.addColumnMetaData(
							new ResultSetHeaderColumn("FECHA_PAGO_INTEREST", ICTSTypes.SQLVARCHAR, 15));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("DIA_PAGO", ICTSTypes.SQLVARCHAR, 5));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("NUMERO_PAGOS", ICTSTypes.SQLINT4, 5));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("TASA_EFECTIVA", ICTSTypes.SQLFLT8i, 10));
					metaData.addColumnMetaData(
							new ResultSetHeaderColumn("INTERES_ESTIMADO_HOLD", ICTSTypes.SQLMONEY, 6));
					metaData.addColumnMetaData(
							new ResultSetHeaderColumn("TOTAL_INTERES_ESTIMADO_HOLD", ICTSTypes.SQLMONEY, 6));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("INT_GANADO_ACTUAL", ICTSTypes.SQLMONEY, 6));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("NUEVO_TIPO_MONTO", ICTSTypes.SQLVARCHAR, 6));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("NUEVO_TIPO_PLAZO", ICTSTypes.SQLVARCHAR, 6));
					metaData.addColumnMetaData(new ResultSetHeaderColumn("TOTAL_INT_GANADO", ICTSTypes.SQLMONEY, 6));
					metaData.addColumnMetaData(
							new ResultSetHeaderColumn("AMORTIZACION_PERIODO", ICTSTypes.SQLVARCHAR, 6));

					for (CertificateDepositResult obj : simulationResponse.getListCertificateDepositResult()) {
						row = new ResultSetRow();
						row.addRowData(1, new ResultSetRowColumnData(false, obj.getInterestEstimated().toString()));
						row.addRowData(2,
								new ResultSetRowColumnData(false, obj.getInterestEstimatedTotal().toString()));
						row.addRowData(3, new ResultSetRowColumnData(false, ""));// impuesto
																					// a
																					// pagar
						row.addRowData(4, new ResultSetRowColumnData(false, obj.getInterestPayDay()));
						row.addRowData(5, new ResultSetRowColumnData(false, ""));// dia
																					// de
																					// pago
						row.addRowData(6, new ResultSetRowColumnData(false, obj.getNumberOfPayment().toString()));
						row.addRowData(7, new ResultSetRowColumnData(false, obj.getRate().toString()));
						row.addRowData(8, new ResultSetRowColumnData(false, ""));// interest
																					// estimado
																					// hold
						row.addRowData(9, new ResultSetRowColumnData(false, ""));
						row.addRowData(10, new ResultSetRowColumnData(false, ""));
						row.addRowData(11, new ResultSetRowColumnData(false, ""));
						row.addRowData(12, new ResultSetRowColumnData(false, ""));
						row.addRowData(13, new ResultSetRowColumnData(false, ""));
						row.addRowData(14, new ResultSetRowColumnData(false, ""));
						data.addRow(row);
					}
					resultBlock = new ResultSetBlock(metaData, data);
					pResponse.addResponseBlock(resultBlock);
				}
				;
			}
			;
		}

		// Retorno Código ERROR
		pResponse.setReturnCode(simulationResponse.getReturnCode());
		if (logger.isInfoEnabled())
			logger.logInfo("CODIGO DE RETORNO SIMULADOR: " + simulationResponse.getReturnCode());
		// AGREGAR LA DEVOLUCION DE LA COLECCION DE MENSAJES QUE DEVUELVE LA
		// IMPLEMENTACION, PARA MAPEAR EL MENSAJE DE ERROR DEVUELTO
		if (simulationResponse.getReturnCode() != 0) {
			pResponse = Utils.returnException(simulationResponse.getMessages());
			return pResponse;
		}
		;
		//
		pResponse.addParam("@o_plazo", ICTSTypes.SQLINT4, 1,
				simulationResponse.getCertificateDepositResponse().getTerm().toString());
		pResponse.addParam("@o_fecha_ven", ICTSTypes.SQLVARCHAR, 1,
				simulationResponse.getCertificateDepositResponse().getExpirationDate());

		return pResponse;
	};
};
