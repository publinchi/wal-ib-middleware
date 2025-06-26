package com.cobiscorp.ecobis.orchestration.core.ib.compensationprocess;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.FileWriter;
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
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
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

	private ILogger loggerProcess = LogFactory.getLogger(CompensationProcessOrchestrationCore.class);
	private static final String CLASS_NAME = "CompensationProcessOrchestrationCore";
	private java.util.Properties properties;
	private static final String VALIDAR_ARCHIVO = "validarArchivo";
	private static final String FALSE = "false";
	private static final String COBIS = "COBIS";
	private static final String O_EN_LINEA = "@o_en_linea";
	private static final String O_FECHA_PROCESO = "@o_fecha_proceso";
	static final String RESPONSE_TRANSACTION = "RESPONSE_TRANSACTION";
	static final String REGISTROS = "registros";
	private static final String NOMBRE_ARCHIVO_REPORTE = "nombre_archivo_reporte";
	private static final String NOMBRE_REPORTE = "nombre_reporte";
	private static final String FILAS = "filas";
	private static final String SIGUIENTE = "siguiente";
	private static final String NUM_RESULSET_REPORT = "NUM_RESULSET_REPORT";
	protected static final String TRN_18500144 = "18500144";
	private static final String O_COMPENSACION_FIN = "@o_compensacion_fin";
	private static final String O_NUM_FILAS = "@o_num_filas";
	private static final String T_TRN = "@t_trn";
	private static final int ERROR40002 = 40002;
	private static final String FILENAME = "filename";
	private static final String I_TIPO = "@i_tipo";
	private static final String I_FILENAME= "@i_fileName";

	@Override
	public void loadConfiguration(IConfigurationReader arg0) {
		if (loggerProcess.isInfoEnabled()) {
			loggerProcess.logInfo(CLASS_NAME + ": loadConfiguration INI");
		}
		properties = arg0.getProperties("//property");
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		String cobisHomePath = CTSGeneralConfiguration.getEnvironmentVariable("COBIS_HOME", 0);
		String localDirectory = cobisHomePath + File.separator + properties.get("PATH").toString();
		String reportPath = cobisHomePath + (String) properties.get("PATH_REPORT");
		aBagSPJavaOrchestration.put("reportPath", reportPath);

		ServerResponse responseServer = null;
		ServerRequest serverRequest = new ServerRequest();
		try {
			responseServer = getServerStatus(serverRequest);
		} catch (CTSServiceException e) {
			if (loggerProcess.isInfoEnabled()) {
				loggerProcess.logError(e.toString());
			}
		} catch (CTSInfrastructureException e) {
			if (loggerProcess.isInfoEnabled()) {
				loggerProcess.logError(e.toString());
			}
		}

		if (responseServer != null && responseServer.getOnLine()) {
			if (loggerProcess.isDebugEnabled()) {
				loggerProcess.logDebug("server is online");
			}
			File directory = new File(localDirectory);
			aBagSPJavaOrchestration.put("numRegistros", properties.get("NUMBER"));

			if (loggerProcess.isInfoEnabled()) {
				loggerProcess.logInfo(CLASS_NAME + ": Begin executeJavaOrchestration");
			}

			if (directory.isDirectory()) {
				aBagSPJavaOrchestration.put("directory", directory);
				processFilesInDirectory(anOriginalRequest, aBagSPJavaOrchestration);
			} else {
				if (loggerProcess.isInfoEnabled()) {
					loggerProcess.logInfo(CLASS_NAME + ": La ruta especificada no es un directorio.");
				}
			}

			if (loggerProcess.isDebugEnabled()) {
				loggerProcess
						.logDebug("REQUEST [anOriginalRequest] " + anOriginalRequest.getProcedureRequestAsString());
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
					if (file.getName().toLowerCase().endsWith(".json")) {
						if (file.exists() && file.length() > 0) {
							aBagSPJavaOrchestration.put(VALIDAR_ARCHIVO, "true");
							aBagSPJavaOrchestration.put(FILENAME, "/" + file.getName());
							anOriginalRequest.addInputParam(I_FILENAME, ICTSTypes.SQLVARCHAR, file.getName());
							anOriginalRequest.addInputParam("fileNameSinJson", ICTSTypes.SQLVARCHAR,
									getFileNameWithoutExtension(file.getName()));
							jsonLoad(anOriginalRequest, aBagSPJavaOrchestration, file);
						} else {
							aBagSPJavaOrchestration.put(VALIDAR_ARCHIVO, FALSE);
							if (loggerProcess.isInfoEnabled()) {
								loggerProcess.logInfo(CLASS_NAME + " Archivo vacio " + file.getName());
							}
						}

						if ("true".equals(aBagSPJavaOrchestration.get(VALIDAR_ARCHIVO).toString())) {
							anOriginalRequest.addInputParam(I_TIPO, ICTSTypes.SQLVARCHAR, "C"); // COMPLETADO
																								// CORRECTAMENTE
						} else {
							anOriginalRequest.addInputParam(I_TIPO, ICTSTypes.SQLVARCHAR, "E"); // ERROR
						}
						execUploapDownloadFile(anOriginalRequest, aBagSPJavaOrchestration, "D", "");
						if ("C".equals(anOriginalRequest.readValueParam(I_TIPO))) {
							generateReportCompensation(anOriginalRequest, aBagSPJavaOrchestration);
						}
					} else {
						if (loggerProcess.isInfoEnabled()) {
							loggerProcess.logInfo(
									CLASS_NAME + " [Archivo Ignorado] " + file.getName() + " no es un archivo JSON.");
						}
					}
				} else if (file.isDirectory()) {
					if (loggerProcess.isInfoEnabled()) {
						loggerProcess.logInfo(CLASS_NAME + " [Directorio] " + file.getName());
					}
				}
			}
			// se sube el reporte al S3 si existe el archivo
			if (aBagSPJavaOrchestration.get(NOMBRE_REPORTE) != null) {
				File file = new File((String) aBagSPJavaOrchestration.get(NOMBRE_REPORTE));
				if (file.exists()) {
					if (loggerProcess.isInfoEnabled()) {
						loggerProcess.logInfo(CLASS_NAME + " [Directorio] "
								+ (String) aBagSPJavaOrchestration.get(NOMBRE_ARCHIVO_REPORTE));
					}
					execUploapDownloadFile(anOriginalRequest, aBagSPJavaOrchestration, "S",
							(String) aBagSPJavaOrchestration.get(NOMBRE_ARCHIVO_REPORTE));
				}
			}
		} else {
			if (loggerProcess.isInfoEnabled()) {
				loggerProcess.logInfo(CLASS_NAME + ": El directorio está vacío o no se puede acceder.");
			}
		}
	}

	public TransactionFile jsonLoad(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration,
			File file) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		TransactionFile transactionFile = null;
		if (loggerProcess.isInfoEnabled()) {
			loggerProcess.logInfo(CLASS_NAME + ": jsonLoad: " + aBagSPJavaOrchestration.toString());
		}
		aBagSPJavaOrchestration.put(REGISTROS, "");
		try {
			transactionFile = objectMapper.readValue(file, TransactionFile.class);
			if (loggerProcess.isInfoEnabled()) {
				loggerProcess.logInfo(CLASS_NAME + ": aBagSPJavaOrchestration: " + aBagSPJavaOrchestration.toString());
			}
			fillingVariables(transactionFile, aBagSPJavaOrchestration);

			String originalFileName = anOriginalRequest.readValueParam("fileNameSinJson");
			String transactionFileName = transactionFile.getFilename();
			if (originalFileName != null && transactionFileName != null
					&& originalFileName.equals(transactionFileName)) {

				List<TransactionContent> contents = transactionFile.getContent();

				if (contents != null) {
					aBagSPJavaOrchestration.put("contents", contents);
					concatenateRecords(anOriginalRequest, aBagSPJavaOrchestration);
					if (FALSE.equals(aBagSPJavaOrchestration.get(VALIDAR_ARCHIVO).toString())) {
						return null;
					}
					if (loggerProcess.isInfoEnabled()) {
						loggerProcess.logDebug("REQUEST [transactionFile] " + transactionFile.toString());
					}
				}
			} else {
				aBagSPJavaOrchestration.put(VALIDAR_ARCHIVO, FALSE);
			}
		} catch (IOException e) {
			aBagSPJavaOrchestration.put(VALIDAR_ARCHIVO, FALSE);
			if (loggerProcess.isInfoEnabled()) {
				loggerProcess.logError("Error en jsonLoad", e);
			}
		}
		return transactionFile;
	}

	private void consCarId(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {

		request.setSpName("cob_atm..sp_cons_card_id");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS);

		request.addInputParam(T_TRN, ICTSTypes.SQLINTN, "18700148");
		request.addInputParam("@i_compensacion", ICTSTypes.SQLVARCHAR,
				aBagSPJavaOrchestration.get(REGISTROS).toString());
		request.addOutputParam(O_COMPENSACION_FIN, ICTSTypes.SQLVARCHAR, "XXXXXXXXXXXXXXXXXXXXXX");

		if (loggerProcess.isInfoEnabled()) {
			loggerProcess.logDebug("Request Corebanking consCarId: " + request.toString());
		}

		IProcedureResponse response = executeCoreBanking(request);

		if (response.getReturnCode() == 0 && response.readValueParam(O_COMPENSACION_FIN) != null) {
			aBagSPJavaOrchestration.put("o_compensacion_fin", response.readValueParam(O_COMPENSACION_FIN));
			compensacionCentral(request, aBagSPJavaOrchestration);
		}

	}

	private void compensacionCentral(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {

		request.setSpName("cob_atm..sp_atm_trn_data_compensation");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS);

		request.addInputParam(T_TRN, ICTSTypes.SQLINTN, "18700150");
		request.addInputParam("@i_file_id", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("fileId").toString());
		request.addInputParam("@i_issuer_id", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("issuerId").toString());
		request.addInputParam("@i_brand", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get("brand").toString());
		request.addInputParam("@i_filename", ICTSTypes.SQLVARCHAR, aBagSPJavaOrchestration.get(FILENAME).toString());
		request.addInputParam("@i_reference_date", ICTSTypes.SQLVARCHAR,
				aBagSPJavaOrchestration.get("referenceDate").toString());
		request.addInputParam("@i_compensacion", ICTSTypes.SQLVARCHAR,
				aBagSPJavaOrchestration.get("o_compensacion_fin").toString());

		IProcedureResponse response = executeCoreBanking(request);
		aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, response);

		if (response.getReturnCode() == 0) {
			aBagSPJavaOrchestration.put(VALIDAR_ARCHIVO, "true");
		} else {
			aBagSPJavaOrchestration.put(VALIDAR_ARCHIVO, FALSE);
		}
	}

	private IProcedureResponse execUploapDownloadFile(IProcedureRequest anOriginalReq,
			Map<String, Object> aBagSPJavaOrchestration, String action, String fileName) {

		IProcedureResponse connectorCardResponse = new ProcedureResponseAS();
		aBagSPJavaOrchestration.remove("trn_virtual");

		try {

			anOriginalReq.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator",
					ICOBISTS.HEADER_STRING_TYPE, "(service.identifier=CompensationProcessOrchestrationCore)");
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
			anOriginalReq.addInputParam("@i_accion", ICTSTypes.SYBINT4, action);
			anOriginalReq.addInputParam("@i_fileNameCSV", ICTSTypes.SQLVARCHAR, fileName);

			// SE EJECUTA CONECTOR
			connectorCardResponse = executeProvider(anOriginalReq, aBagSPJavaOrchestration);

		} catch (Exception e) {
			if (loggerProcess.isInfoEnabled()) {
				this.loggerProcess
						.logInfo("CompensationProcessOrchestrationCore Error Catastrofico de execUpDownloadFile", e);
			}
		} finally {
			if (loggerProcess.isInfoEnabled()) {
				loggerProcess.logInfo("CompensationProcessOrchestrationCore --> Saliendo de execUpDownloadFile");
			}
		}

		return connectorCardResponse;

	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		IProcedureResponse response = new ProcedureResponseAS();
		if (loggerProcess.isInfoEnabled()) {
			loggerProcess.logInfo("Begin [" + CLASS_NAME + "][processResponse]");
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
		aBagSPJavaOrchestration.put(FILENAME, transactionFile.getFilename());
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

			numRegistros = 5;
		}
		@SuppressWarnings("unchecked")
		List<TransactionContent> contents = (List<TransactionContent>) aBagSPJavaOrchestration.get("contents");
		int count = 0;

		for (TransactionContent content : contents) {
			Clearing clearing = content.getClearing();

			if (clearing != null) {
				int actionCode = clearing.getActionCode();

				if (actionCode == 2 || actionCode == 3) {
					String concatenated = String.join(";", content.getTransaction().getPan(),
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
						if ("false".equals(aBagSPJavaOrchestration.get(VALIDAR_ARCHIVO).toString())) {
							return;
						}
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
		IProcedureRequest aServerStatusRequest = createServerStatusRequest(serverRequest);

		if (loggerProcess.isDebugEnabled()) {
			loggerProcess.logDebug("Request to Corebanking: " + aServerStatusRequest.getProcedureRequestAsString());
		}

		IProcedureResponse wServerStatusResp = executeCoreBanking(aServerStatusRequest);

		if (loggerProcess.isDebugEnabled()) {
			loggerProcess.logDebug("Response from Corebanking: " + wServerStatusResp.getProcedureResponseAsString());
		}

		ServerResponse serverResponse = new ServerResponse();
		populateServerResponse(serverResponse, wServerStatusResp);

		if (loggerProcess.isDebugEnabled()) {
			loggerProcess.logDebug("Returned Response: " + serverResponse);
		}

		if (loggerProcess.isInfoEnabled()) {
			loggerProcess.logInfo("TERMINATING SERVICE");
		}

		return serverResponse;
	}

	private IProcedureRequest createServerStatusRequest(ServerRequest serverRequest) {
		IProcedureRequest request = new ProcedureRequestAS();
		request.setSpName("cobis..sp_server_status");
		request.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1800039");
		request.addInputParam(T_TRN, ICTSTypes.SYBINTN, "1800039");
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, "central");
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS);
		request.setValueParam("@s_servicio", serverRequest.getChannelId());
		request.addInputParam("@i_cis", ICTSTypes.SYBCHAR, "S");
		request.addOutputParam(O_EN_LINEA, ICTSTypes.SYBCHAR, "S");
		request.addOutputParam(O_FECHA_PROCESO, ICTSTypes.SYBVARCHAR, "XXXX");
		return request;
	}

	private void populateServerResponse(ServerResponse serverResponse, IProcedureResponse wServerStatusResp) {
		serverResponse.setSuccess(true);
		Utils.transformIprocedureResponseToBaseResponse(serverResponse, wServerStatusResp);
		serverResponse.setReturnCode(wServerStatusResp.getReturnCode());

		if (wServerStatusResp.getReturnCode() == 0) {
			serverResponse.setOfflineWithBalances(true);
			String enLineaParam = wServerStatusResp.readValueParam(O_EN_LINEA);
			serverResponse.setOnLine(enLineaParam != null && enLineaParam.equals("S"));
			setProcessDate(serverResponse, wServerStatusResp);
		} else {
			handleErrorCodes(serverResponse, wServerStatusResp);
		}
	}

	private void setProcessDate(ServerResponse serverResponse, IProcedureResponse wServerStatusResp) {
		String processDateStr = wServerStatusResp.readValueParam(O_FECHA_PROCESO);
		if (processDateStr != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			try {
				serverResponse.setProcessDate(formatter.parse(processDateStr));
			} catch (ParseException e) {
				if (loggerProcess.isInfoEnabled()) {
					loggerProcess.logDebug("Error parsing process date: ", e);
				}
			}
		}
	}

	private void handleErrorCodes(ServerResponse serverResponse, IProcedureResponse wServerStatusResp) {
		int returnCode = wServerStatusResp.getReturnCode();
		serverResponse.setOnLine(false);
		serverResponse.setOfflineWithBalances(returnCode != ERROR40002);
	}

	private void generateReportCompensation(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (loggerProcess.isInfoEnabled()) {
			loggerProcess.logInfo("Inicia generateReportCompensation");
		}

		aBagSPJavaOrchestration.put(SIGUIENTE, "0");
		aBagSPJavaOrchestration.put(FILAS, properties.get(NUM_RESULSET_REPORT));

		String fechaFormateada = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String nombreArchivoCsv = "compensacion_" + fechaFormateada + ".csv";

		aBagSPJavaOrchestration.put(NOMBRE_ARCHIVO_REPORTE, nombreArchivoCsv);
		String reportPath = (String) aBagSPJavaOrchestration.get("reportPath");
		aBagSPJavaOrchestration.put(NOMBRE_REPORTE, reportPath + nombreArchivoCsv);

		boolean hasMoreRecords = true;

		while (hasMoreRecords) {
			IProcedureResponse responseData = dataCompensation(anOriginalRequest, aBagSPJavaOrchestration);

			String numFilasParam = responseData.readValueParam(O_NUM_FILAS);
			if (numFilasParam != null) {
				try {
					int numRegistros = Integer.parseInt(numFilasParam);
					hasMoreRecords = numRegistros > 0;

					if (hasMoreRecords) {
						createReportCsv(responseData, aBagSPJavaOrchestration); // Crea el reporte
					}
				} catch (NumberFormatException e) {
					if (loggerProcess.isInfoEnabled()) {
						loggerProcess.logError("Error al analizar el número de filas: " + numFilasParam, e);
						hasMoreRecords = false; // Detener el bucle en caso de error
					}
				}
			} else {
				hasMoreRecords = false; // No hay más registros
			}
		}
	}

	private IProcedureResponse dataCompensation(IProcedureRequest anOriginalrequest,
			Map<String, Object> aBagSPJavaOrchestration) {

		IProcedureRequest request = initProcedureRequest(anOriginalrequest);
		request.setSpName("cob_atm..sp_atm_reporte_compesacion");
		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS);

		request.addInputParam(T_TRN, ICTSTypes.SQLINTN, "18700148");
		request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "Q");
		request.addInputParam("@i_nombre_archivo", ICTSTypes.SQLVARCHAR,
				(String) aBagSPJavaOrchestration.get(FILENAME));
		request.addInputParam("@i_siguiente", ICTSTypes.SQLINT4, (String) aBagSPJavaOrchestration.get(SIGUIENTE));
		request.addInputParam("@i_filas", ICTSTypes.SQLINT4, (String) aBagSPJavaOrchestration.get(FILAS));
		request.addOutputParam(O_NUM_FILAS, ICTSTypes.SQLINT4, "0");

		IProcedureResponse response = executeCoreBanking(request);

		if (response.getReturnCode() != 0) {
			response = Utils.returnException(Utils.returnArrayMessage(response));
		}
		return response;
	}

	public boolean createReportCsv(IProcedureResponse response, Map<String, Object> aBagSPJavaOrchestration) {
		String csvFile = (String) aBagSPJavaOrchestration.get(NOMBRE_REPORTE);
		FileWriter writer = null;

		try {
			writer = new FileWriter(csvFile, new File(csvFile).exists());

			if (response.getResultSetListSize() > 0) {
				writeHeader(writer, response, csvFile);
				writeData(writer, response, aBagSPJavaOrchestration);
			}
			return true;
		} catch (IOException e) {
			if (loggerProcess.isInfoEnabled()) {
				loggerProcess.logError("Error en la creación de archivo CSV:", e);
			}
			return false;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					if (loggerProcess.isInfoEnabled()) {
						loggerProcess.logError("Error al cerrar el archivo CSV:", e);
					}
				}
			}
		}
	}

	private void writeHeader(FileWriter writer, IProcedureResponse response, String csvFile) throws IOException {
		File file = new File(csvFile);
		if (file.exists() && file.length() > 0) {
			return;
		}

		IResultSetHeader rsHeader = response.getResultSetMetaData(1);

		StringBuilder header = new StringBuilder();
		for (int i = 2; i <= rsHeader.getColumnsNumber(); i++) {
			ResultSetHeaderColumn col = (ResultSetHeaderColumn) rsHeader.getColumnMetaData(i);
			if (i > 2) {
				header.append(";");
			}
			header.append(col.getName());
		}
		writer.append(header.toString()).append("\n");
	}

	private void writeData(FileWriter writer, IProcedureResponse response, Map<String, Object> aBagSPJavaOrchestration)
			throws IOException {
		IResultSetRow[] resultSetRows = response.getResultSet(1).getData().getRowsAsArray();

		for (IResultSetRow row : resultSetRows) {
			IResultSetRowColumnData[] columns = row.getColumnsAsArray();
			StringBuilder data = new StringBuilder();

			for (int j = 1; j < columns.length; j++) {
				if (j > 1) {
					data.append(";");
				}
				data.append(columns[j].getValue());
			}

			writer.append(data.toString()).append("\n");

			if (row == resultSetRows[resultSetRows.length - 1]) {
				aBagSPJavaOrchestration.put(SIGUIENTE, columns[0].getValue());
			}
		}
	}

	private static String getFileNameWithoutExtension(String fileName) {
		int dotIndex = fileName.lastIndexOf('.');
		if (dotIndex > 0) {
			return fileName.substring(0, dotIndex);
		} else {
			return fileName;
		}
	}
}
