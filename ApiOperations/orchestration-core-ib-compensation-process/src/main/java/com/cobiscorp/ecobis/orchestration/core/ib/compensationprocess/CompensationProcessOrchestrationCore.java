package com.cobiscorp.ecobis.orchestration.core.ib.compensationprocess;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.io.File;

import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.configuration.CTSGeneralConfiguration;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.ServerRequest;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.orchestration.core.ib.dto.Clearing;
import com.cobiscorp.ecobis.orchestration.core.ib.dto.TransactionContent;
import com.cobiscorp.ecobis.orchestration.core.ib.dto.TransactionFile;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component(name = "CompensationProcessOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "CompensationProcessOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "CompensationProcessOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_compensation_process") })
public class CompensationProcessOrchestrationCore extends SPJavaOrchestrationBase {

	private ILogger logger = LogFactory.getLogger(CompensationProcessOrchestrationCore.class);
	private static final String CLASS_NAME = "CompensationProcessOrchestrationCore";
	private java.util.Properties properties;
	private static final String VALIDAR_ARCHIVO = "validarArchivo";
	private static final String FALSE = "false";
	private static final String COBIS = "COBIS";
	private static final String O_EN_LINEA = "@o_en_linea";
	private static final String O_FECHA_PROCESO = "@o_fecha_proceso";
	static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	static final String REGISTROS = "registros";
	protected static final String TRN_18500144 = "18500144";
	private static final String O_COMPENSACION_FIN = "@o_compensacion_fin";
	private static final String T_TRN = "@t_trn";
	private static final int ERROR40004 = 40004;
	private static final int ERROR40003 = 40003;
	private static final int ERROR40002 = 40002;

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + ": loadConfiguration INI");
		}
		properties = arg0.getProperties("//property");
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		String algnPath = CTSGeneralConfiguration.getEnvironmentVariable("COBIS_HOME", 0);
		String localDirectory = algnPath + properties.get("PATH").toString();
		ServerResponse responseServer = null;
		ServerRequest serverRequest = new ServerRequest();
		try {
			responseServer = getServerStatus(serverRequest);
		} catch (CTSServiceException e) {
		    logger.logError(e.toString());
		} catch (CTSInfrastructureException e) {
		    logger.logError(e.toString());
		}
		if (responseServer != null && responseServer.getOnLine()) {
			if (logger.isDebugEnabled()) {
				logger.logDebug("server is online");
			}
			File directory = new File(localDirectory);
			aBagSPJavaOrchestration.put("numRegistros", properties.get("NUMBER"));

			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + ": Begin executeJavaOrchestration");
			}

			if (directory.isDirectory()) {
				aBagSPJavaOrchestration.put("directory", directory);
				processFilesInDirectory(anOriginalRequest, aBagSPJavaOrchestration);
			} else {
				logger.logInfo(CLASS_NAME + ": La ruta especificada no es un directorio.");
			}

			if (logger.isDebugEnabled()) {
				logger.logDebug("REQUEST [anOriginalRequest] " + anOriginalRequest.getProcedureRequestAsString());
			}
		}
		return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}

	private void processFilesInDirectory(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		File[] files = ((File) aBagSPJavaOrchestration.get("directory")).listFiles();

		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					aBagSPJavaOrchestration.put(VALIDAR_ARCHIVO, "true");
					aBagSPJavaOrchestration.put("FileName", "/" + file.getName());
					jsonLoad(anOriginalRequest, aBagSPJavaOrchestration, file);

					if ("true".equals(aBagSPJavaOrchestration.get(VALIDAR_ARCHIVO).toString())) {
						aBagSPJavaOrchestration.put("tipo", "C");
					} else {
						aBagSPJavaOrchestration.put("tipo", "E");
					}
					execUpDownloadFile(anOriginalRequest, aBagSPJavaOrchestration);
					logger.logInfo(CLASS_NAME + " [Archivo] " + file.getName());
				} else if (file.isDirectory()) {
					logger.logInfo(CLASS_NAME + " [Directorio] " + file.getName());
				}
			}
		} else {
			logger.logInfo(CLASS_NAME + ": El directorio está vacío o no se puede acceder.");
		}
	}

	public TransactionFile jsonLoad(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration,
			File file) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		TransactionFile transactionFile = null;

		aBagSPJavaOrchestration.put(REGISTROS, "");
		try {
			transactionFile = objectMapper.readValue(file, TransactionFile.class);
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + ": aBagSPJavaOrchestration: " + aBagSPJavaOrchestration.toString());
			}
			fillingVariables(transactionFile, aBagSPJavaOrchestration);

			List<TransactionContent> contents = transactionFile.getContent();

			if (contents != null) {
				aBagSPJavaOrchestration.put("contents", contents);
				concatenateRecords(anOriginalRequest, aBagSPJavaOrchestration);

				if (logger.isInfoEnabled()) {
					logger.logDebug("REQUEST [transactionFile] " + transactionFile.toString());
				}
			}
		} catch (IOException e) {
			aBagSPJavaOrchestration.put(VALIDAR_ARCHIVO, VALIDAR_ARCHIVO);
			logger.logError("Error en jsonLoad", e);
		}
		return transactionFile;
	}

	private void consCarId(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en consCarId");
			logger.logInfo(CLASS_NAME + " consCarId registros:\n" + aBagSPJavaOrchestration.get(REGISTROS));
		}

		request.setSpName("cob_atm..sp_cons_card_id");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS);

		request.addInputParam(T_TRN, ICTSTypes.SQLINTN, "18700148");
		request.addInputParam("@i_compensacion", ICTSTypes.SQLVARCHAR,
				aBagSPJavaOrchestration.get(REGISTROS).toString());
		request.addOutputParam(O_COMPENSACION_FIN, ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXX");

		if (logger.isInfoEnabled()) {
			logger.logDebug("Request Corebanking consCarId: " + request.toString());
		}

		IProcedureResponse response = executeCoreBanking(request);
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + "Respuesta Devuelta del Core api:" + response.getProcedureResponseAsString());

			logger.logInfo(
					CLASS_NAME + "Parametro @o_compensacion_fin: " + response.readValueParam(O_COMPENSACION_FIN));
		}
		if (response.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(VALIDAR_ARCHIVO, FALSE);
			response = Utils.returnException(Utils.returnArrayMessage(response));

		}
		if (response.getReturnCode() == 0 && response.readValueParam(O_COMPENSACION_FIN) != null) {
			aBagSPJavaOrchestration.put("o_compensacion_fin", response.readValueParam(O_COMPENSACION_FIN));
			compensacionCentral(request, aBagSPJavaOrchestration);
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de consCarId");
		}

	}

	private void compensacionCentral(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en compensacionCentral");
			logger.logInfo(CLASS_NAME + "compensacionCentral registros:\n" + aBagSPJavaOrchestration.get(REGISTROS));
		}

		request.setSpName("cob_atm..sp_atm_trn_data_compensation");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS);

		request.addInputParam(T_TRN, ICTSTypes.SQLINTN, "18700150");
		request.addInputParam("@i_file_id", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("fileId").toString());
		request.addInputParam("@i_issuer_id", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("issuerId").toString());
		request.addInputParam("@i_brand", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("brand").toString());
		request.addInputParam("@i_filename", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("filename").toString());
		request.addInputParam("@i_reference_date", ICTSTypes.SQLVARCHAR,
				aBagSPJavaOrchestration.get("referenceDate").toString());
		request.addInputParam("@i_compensacion", ICTSTypes.SQLVARCHAR,
				aBagSPJavaOrchestration.get("o_compensacion_fin").toString());

		IProcedureResponse response = executeCoreBanking(request);
		aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, response);
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " CompensacionCentral Respuesta Devuelta del Core api: "
					+ response.getProcedureResponseAsString());
		}

		if (response.getReturnCode() != 0) {
			aBagSPJavaOrchestration.put(VALIDAR_ARCHIVO, FALSE);
			response = Utils.returnException(Utils.returnArrayMessage(response));
		}
		if (response.getReturnCode() == 0) {
			aBagSPJavaOrchestration.put(VALIDAR_ARCHIVO, "true");
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Saliendo de compensacionCentral");
		}

	}

	private IProcedureResponse execUpDownloadFile(IProcedureRequest anOriginalReq,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse connectorCardResponse = new ProcedureResponseAS();

		aBagSPJavaOrchestration.remove("trn_virtual");

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en execUpDownloadFile");
		}
		try {

			anOriginalReq.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator",
					ICOBISTS.HEADER_STRING_TYPE, "(service.identifier=CompensationDownloadOrchestrationCore)");
			anOriginalReq.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "executeTransaction");
			anOriginalReq.addFieldInHeader("t_corr", ICOBISTS.HEADER_STRING_TYPE, "");
			anOriginalReq.addFieldInHeader("executionResult", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalReq.addFieldInHeader("externalProvider", ICOBISTS.HEADER_STRING_TYPE, "0");
			anOriginalReq.addFieldInHeader("idzone", ICOBISTS.HEADER_STRING_TYPE, "routingOrchestrator");

			anOriginalReq.addFieldInHeader("trn", ICOBISTS.HEADER_STRING_TYPE, TRN_18500144);
			anOriginalReq.setValueFieldInHeader(ICOBISTS.HEADER_TRN, TRN_18500144);

			anOriginalReq.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, TRN_18500144);

			// SE HACE LA LLAMADA AL CONECTOR
			aBagSPJavaOrchestration.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorCompensacion)");
			anOriginalReq.setSpName("cob_procesador..sp_compensation");

			anOriginalReq.addInputParam("@trn_virtual", ICTSTypes.SYBINT4, TRN_18500144);
			anOriginalReq.addInputParam(T_TRN, ICTSTypes.SYBINT4, TRN_18500144);
			anOriginalReq.addInputParam("@i_accion", ICTSTypes.SYBINT4, "D");

			if (logger.isDebugEnabled())
				logger.logDebug("Compensation--> request execUpDownloadFile app: " + anOriginalReq.toString());
			// SE EJECUTA CONECTOR
			connectorCardResponse = executeProvider(anOriginalReq, aBagSPJavaOrchestration);

		} catch (Exception e) {
			this.logger.logInfo("CompensationDownloadOrchestrationCore Error Catastrofico de execUpDownloadFile", e);

		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo("CompensationDownloadOrchestrationCore --> Saliendo de execUpDownloadFile");
			}
		}

		return connectorCardResponse;

	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = new ProcedureResponseAS();
		if (logger.isInfoEnabled()) {
			logger.logInfo("Begin [" + CLASS_NAME + "][processResponse]");
		}
		if (aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION) == null) {

			ErrorBlock eb = new ErrorBlock(-1, "Service is not available");
			response.addResponseBlock(eb);
			response.setReturnCode(-1);
		} else {
			response = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
		}
		return response;
	}

	private void fillingVariables(TransactionFile transactionFile, Map<String, Object> aBagSPJavaOrchestration) {
		aBagSPJavaOrchestration.put("fileId", transactionFile.getFileId());
		aBagSPJavaOrchestration.put("issuerId", transactionFile.getIssuerId());
		aBagSPJavaOrchestration.put("clientId", transactionFile.getClientId());
		aBagSPJavaOrchestration.put("idSubemissor", transactionFile.getIdSubemissor());
		aBagSPJavaOrchestration.put("brand", transactionFile.getBrand());
		aBagSPJavaOrchestration.put("filename", transactionFile.getFilename());
		aBagSPJavaOrchestration.put("sequence", transactionFile.getSequence());
		aBagSPJavaOrchestration.put("fileNumber", transactionFile.getFileNumber());
		aBagSPJavaOrchestration.put("totalFiles", transactionFile.getTotalFiles());
		aBagSPJavaOrchestration.put("referenceDate", transactionFile.getReferenceDate());
		aBagSPJavaOrchestration.put("recordsTotal", transactionFile.getRecordsTotal());
		aBagSPJavaOrchestration.put("recordsAmnt", transactionFile.getRecordsAmnt());
	}

	private void concatenateRecords(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		StringBuilder groupBuilder = new StringBuilder();
		int numRegistros;
        Object numRegistrosObj = aBagSPJavaOrchestration.get("numRegistros");
        if (numRegistrosObj instanceof String) {
            numRegistros = Integer.parseInt((String) numRegistrosObj);
        } else {

            logger.logError("El valor de 'numRegistros' no es un String válido.");
            numRegistros = 0; 
        }
		@SuppressWarnings("unchecked")
		List<TransactionContent> contents = (List<TransactionContent>) aBagSPJavaOrchestration.get("contents");
		int count = 0;

		for (TransactionContent content : contents) {
			Clearing clearing = content.getClearing();

			if (clearing != null) {
				int actionCode = clearing.getActionCode();

				if (actionCode == 2 || actionCode == 3) {
					String concatenated = String.join("*", content.getTransaction().getPan(),
							content.getTransaction().getCardId(),
							content.getTransaction().getTransactionTypeIndicator(),
							content.getTransaction().getAuthorization(),
							String.valueOf(content.getTransaction().getSourceCurrency()),
							String.valueOf(content.getTransaction().getSourceValue()),
							String.valueOf(content.getTransaction().getDestCurrency()),
							String.valueOf(content.getTransaction().getDestValue()),
							String.valueOf(content.getTransaction().getPurchaseValue()),
							content.getTransaction().getAuthorizationDate(),
							String.valueOf(content.getTransaction().getIssuerExchangeRate()),
							String.valueOf(content.getTransaction().getOperationType()),
							content.getTransaction().getOperationCode(), String.valueOf(clearing.isInternational()),
							String.join(",", clearing.getReasonList()), String.valueOf(clearing.getActionCode()),
							String.valueOf(clearing.getTotalPartialTransaction()),
							String.valueOf(clearing.isFlagPartialSettlement()), String.valueOf(clearing.isCancel()),
							String.valueOf(clearing.isConfirm()), String.valueOf(clearing.isAdd()),
							String.valueOf(clearing.isCredit()), String.valueOf(clearing.isDebit()),
							String.valueOf(content.getRecordCode()),
							String.valueOf(content.getTransaction().getMerchant()));

					if (groupBuilder.length() > 0) {
						groupBuilder.append("|");
					}
					groupBuilder.append(concatenated);
					count++;

					if (count % numRegistros == 0) {
						aBagSPJavaOrchestration.put(REGISTROS, groupBuilder.toString());
						consCarId(anOriginalRequest, aBagSPJavaOrchestration);
						groupBuilder.setLength(0);
					}
				}
			}
		}
		if (groupBuilder.length() > 0) {
			aBagSPJavaOrchestration.put(REGISTROS, groupBuilder.toString());
			consCarId(anOriginalRequest, aBagSPJavaOrchestration);
		}
	}

	public ServerResponse getServerStatus(ServerRequest serverRequest)
			throws CTSServiceException, CTSInfrastructureException {
		IProcedureRequest aServerStatusRequest = new ProcedureRequestAS();
		aServerStatusRequest.setSpName("cobis..sp_server_status");
		aServerStatusRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800039");
		aServerStatusRequest.addInputParam(T_TRN, ICTSTypes.SYBINTN, "1800039");
		aServerStatusRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, "central");
		aServerStatusRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS);
		aServerStatusRequest.setValueParam("@s_servicio", serverRequest.getChannelId());
		aServerStatusRequest.addInputParam("@i_cis", ICTSTypes.SYBCHAR, "S");
		aServerStatusRequest.addOutputParam(O_EN_LINEA, ICTSTypes.SYBCHAR, "S");
		aServerStatusRequest.addOutputParam(O_FECHA_PROCESO, ICTSTypes.SYBVARCHAR, "XXXX");
		if (logger.isDebugEnabled())
			logger.logDebug("Request Corebanking: " + aServerStatusRequest.getProcedureRequestAsString());
		IProcedureResponse wServerStatusResp = executeCoreBanking(aServerStatusRequest);
		if (logger.isDebugEnabled())
			logger.logDebug("Response Corebanking: " + wServerStatusResp.getProcedureResponseAsString());
		ServerResponse serverResponse = new ServerResponse();

		serverResponse.setSuccess(true);
		Utils.transformIprocedureResponseToBaseResponse(serverResponse, wServerStatusResp);
		serverResponse.setReturnCode(wServerStatusResp.getReturnCode());
		if (wServerStatusResp.getReturnCode() == 0) {
			serverResponse.setOfflineWithBalances(true);
			if (wServerStatusResp.readValueParam(O_EN_LINEA) != null)
				serverResponse.setOnLine(wServerStatusResp.readValueParam(O_EN_LINEA).equals("S") ? true : false);
			if (wServerStatusResp.readValueParam(O_FECHA_PROCESO) != null) {
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				try {
					serverResponse.setProcessDate(formatter.parse(wServerStatusResp.readValueParam(O_FECHA_PROCESO)));
				} catch (ParseException e) {
					logger.logDebug("Error Devuelto: ", e);
				}
			}
		} else if (wServerStatusResp.getReturnCode() == ERROR40002 || wServerStatusResp.getReturnCode() == ERROR40003
				|| wServerStatusResp.getReturnCode() == ERROR40004) {
			serverResponse.setOnLine(false);
			serverResponse.setOfflineWithBalances(wServerStatusResp.getReturnCode() == ERROR40002 ? false : true);
		}
		if (logger.isDebugEnabled())
			logger.logDebug("Respuesta Devuelta: " + serverResponse);
		if (logger.isInfoEnabled())
			logger.logInfo("TERMINANDO SERVICIO");
		return serverResponse;
	}
}
