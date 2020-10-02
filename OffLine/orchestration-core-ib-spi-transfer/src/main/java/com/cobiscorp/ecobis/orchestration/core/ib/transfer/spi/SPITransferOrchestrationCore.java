package com.cobiscorp.ecobis.orchestration.core.ib.transfer.spi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
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
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.NotificationRequest;
import com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Client;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Notification;
import com.cobiscorp.ecobis.ib.orchestration.dtos.NotificationDetail;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceReexecutionComponent;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;
import com.cobiscorp.ecobis.orchestration.core.ib.transfer.template.TransferOfflineTemplate;


//import com.cobiscorp.ecobis.orchestration7x24.commons.utils.ProcedureUtils;

@Component(name = "SPITransferOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "SPITransferOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "SPITransferOrchestrationCore") })
public class SPITransferOrchestrationCore extends TransferOfflineTemplate {

	private static final String S_SSN_BRANCH = "@s_ssn_branch";
	private static final String I_NOMBRE_BENEF = "@i_nombre_benef";
	private static final String T_RTY = "@t_rty";
	private static final String T_EJEC = "@t_ejec";
	private static final String S_OFI = "@s_ofi";
	private static final String S_DATE_LOCAL = "@s_date";
	private static final String S_SRV = "@s_srv";
	private static final String S_ROL = "@s_rol";
	private static final String S_TERM = "@s_term";
	private static final String S_USER = "@s_user";
	private static final String I_MON_DES_LOCAL = "@i_mon_des";
	private static final String I_PROD_DES_LOCAL = "@i_prod_des";
	private static final String I_CTA_DES_LOCAL = "@i_cta_des";
	private static final String I_BANCO_BEN = "@i_banco_ben";
	private static final String I_DOC_BENEF = "@i_doc_benef";
	private static final String I_CONCEPTO_LOCAL = "@i_concepto";
	private static final String I_VAL_LOCAL = "@i_val";
	private static final String I_MON_LOCAL = "@i_mon";
	private static final String I_CTA_LOCAL = "@i_cta";
	private static final String S_SERVICIO_LOCAL = "@s_servicio";
	private static final String I_PROD_LOCAL = "@i_prod";
	private static final String ERROR_SPEI = "ERROR EN TRANSFERENCIA SPEI";
	/** Instancia del Logger */
	private static ILogger logger = LogFactory.getLogger(SPITransferOrchestrationCore.class);

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	protected void bindCoreServer(ICoreServer service) {
		coreServer = service;
	}

	protected void unbindCoreServer(ICoreServer service) {
		coreServer = null;
	}

	@Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreService coreService;

	public void bindCoreService(ICoreService service) {
		coreService = service;
	}

	public void unbindCoreService(ICoreService service) {
		coreService = null;
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceSendNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceNotification", unbind = "unbindCoreServiceNotification")
	public ICoreServiceSendNotification coreServiceNotification;

	public void bindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = service;
	}

	public void unbindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = null;
	}

	private static final String CORESERVICEMONETARYTRANSACTION = "coreServiceMonetaryTransaction";

	@Reference(referenceInterface = ICoreServiceMonetaryTransaction.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceMonetaryTransaction", unbind = "unbindCoreServiceMonetaryTransaction")
	protected ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction;

	public void bindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = service;
	}

	public void unbindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = null;
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled()) {
			logger.logInfo("Inicia executeJavaOrchestration");
		}

		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreServer", coreServer);
		mapInterfaces.put("coreService", coreService);
		mapInterfaces.put("coreServiceNotification", coreServiceNotification);
		Utils.validateComponentInstance(mapInterfaces);
		aBagSPJavaOrchestration.put(TRANSFER_NAME, "TRANFERENCIA SPI");
		aBagSPJavaOrchestration.put(CORESERVICEMONETARYTRANSACTION, coreServiceMonetaryTransaction);

		try {
			executeStepsTransactionsBase(anOriginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			logger.logError(e);
		} catch (CTSInfrastructureException e) {
			logger.logError(e);
		} finally {
			if (logger.isDebugEnabled()) {
				logger.logInfo("Fin executeJavaOrchestration");
			}
		}
		return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}

	@Override
	protected IProcedureResponse executeTransfer(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Inicia executeTransfer");
		}
		IProcedureResponse responseTransfer = null;
		try {
			IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
			ServerResponse serverResponse = (ServerResponse) aBagSPJavaOrchestration.get(RESPONSE_SERVER);

			// SE EJECUTA LA NOTA DE DEBITO CENTRAL
			responseTransfer = this.executeTransferSPI(originalRequest, aBagSPJavaOrchestration);

			responseTransfer = transformToProcedureResponse(responseTransfer, aBagSPJavaOrchestration);

			// JCOS VALIDACION PARA FL
			if (serverResponse.getOnLine()) {

				if (logger.isDebugEnabled()) {
					logger.logDebug(":::: Se aplicara transaccion reetry o on line SPEI ");
				}
					//if (originalRequest.readValueParam(T_RTY).equals("N")) // VALIDACION DE REENTRY
						responseTransfer = executeBanpay(aBagSPJavaOrchestration, responseTransfer, originalRequest);
				} else {

					if (logger.isDebugEnabled()) {
						logger.logDebug("Se envia a reentry por fuera de linea JCOS");
					}
					// si el saldo disponible le alcanza se aplica transaccion con el proveedor JCOS
					// TODO
					IProcedureResponse validationData = (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_LOCAL_VALIDATION);

					if (validationData != null) {

						if (validationData != null && originalRequest.readValueParam(T_RTY).equals("N")
								&& validationData.readValueParam("@o_aplica_tran").equals("S")) {

							if (logger.isDebugEnabled()) {
								logger.logDebug(":::: Se aplicara servicio spei por que tiene saldo en local");
							}
							executeBanpayOffLine(aBagSPJavaOrchestration, responseTransfer, originalRequest);
						}
					} else {

						if (logger.isDebugEnabled()) {
							logger.logDebug("DATA VALIDATE IS NULL!!!");
						}
					}
				
			}

		} catch (CTSServiceException e) {

			logger.logError(e);
		} catch (CTSInfrastructureException e) {
			logger.logError(e);
		} finally {
			if (logger.isDebugEnabled()) {
				logger.logDebug("Fin executeTransfer");
			}
		}

		return responseTransfer;
	}

	private IProcedureResponse executeBanpay(Map<String, Object> aBagSPJavaOrchestration, IProcedureResponse responseTransfer,
			IProcedureRequest originalRequest) {
		// SE LLAMA LA SERVICIO DE BANPAY
		List<String> respuesta = banpayExecution(originalRequest, aBagSPJavaOrchestration);
		// SE ACTUALIZA TABLA DE SECUENCIAL SPEI
		speiSec(originalRequest, aBagSPJavaOrchestration);
		// SE HACE LA VALIDACION DE LA RESPUESTA
		if (respuesta != null) {
			if (!respuesta.get(0).equals("00")) {
				// SE CAMBIA ESTADO DE REGISTRO
				speiGetDataRB(originalRequest, aBagSPJavaOrchestration);
				// SE HACELA REVERSA DE LA NOTA DE DEBITO
				speiRollback(originalRequest, aBagSPJavaOrchestration);
				if (logger.isDebugEnabled()) {
					logger.logDebug("Error SPEI");
				}
				
				return Utils.returnException(1, ERROR_SPEI);
			} else {
				if (logger.isDebugEnabled()) {
					logger.logDebug("Paso exitoso");
				}
				// SE ADJUNTA LA CLAVE DE RASTREO
				responseTransfer.addParam("@o_clave_rastreo", ICTSTypes.SQLVARCHAR, respuesta.get(2).length(),
						respuesta.get(2));
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.logDebug("List<String> respuesta error o null");
			}
			// SE CAMBIA ESTADO DE REGISTRO
			speiGetDataRB(originalRequest, aBagSPJavaOrchestration);
			// SE HACELA REVERSA DE LA NOTA DE DEBITO
			speiRollback(originalRequest, aBagSPJavaOrchestration);
			
			return Utils.returnException(1, ERROR_SPEI);
		}
		return responseTransfer;
	}

	private void executeBanpayOffLine(Map<String, Object> aBagSPJavaOrchestration, IProcedureResponse responseTransfer,
			IProcedureRequest originalRequest) {
		// SE LLAMA LA SERVICIO DE BANPAY
		List<String> respuesta = banpayExecution(originalRequest, aBagSPJavaOrchestration);
		// SE HACE LA VALIDACION DE LA RESPUESTA
		if (respuesta != null) {
			if (!respuesta.get(0).equals("00")) {

				responseTransfer.addParam("@i_fail_provider", ICTSTypes.SQLVARCHAR, 1, "S");
			}

			if (logger.isDebugEnabled()) {
				logger.logDebug("Paso exitoso");
			}
			// SE ACTUALIZA TABLA DE SECUENCIAL SPEI
			speiSec(originalRequest, aBagSPJavaOrchestration);
			// SE ADJUNTA LA CLAVE DE RASTREO
			responseTransfer.addParam("@o_clave_rastreo", ICTSTypes.SQLVARCHAR, respuesta.get(2).length(),
					respuesta.get(2));

		} else {
			if (logger.isDebugEnabled()) {
				logger.logDebug("List<String> respuesta error o null " + respuesta);
			}

			// SE HACELA REVERSA DE LA NOTA DE DEBITO
			responseTransfer.addParam("@i_fail_provider", ICTSTypes.SQLVARCHAR, 1, "S");

		}
	}

	protected List<String> banpayExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		// SE INICIALIZA VARIABLE
		List<String> response = null;

		if (logger.isInfoEnabled()) {
			logger.logInfo("Entrando a banpayExecution");
		}
		try {

			// SE OBTIENE LA DATA FALTANTE
			List<String> data = speiData(anOriginalRequest, bag);

			// SE SETEAN LOS PARAMETROS DE ENTRADA
			anOriginalRequest.addInputParam("@i_concepto_pago", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@i_concepto"));
			anOriginalRequest.addInputParam("@i_cuenta_beneficiario", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@i_cta_des"));
			anOriginalRequest.addInputParam("@i_cuenta_ordenante", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@i_cta"));
			anOriginalRequest.addInputParam("@i_fecha_operacion", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@s_date"));
			anOriginalRequest.addInputParam("@i_institucion_contraparte", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@i_banco_ben"));
			anOriginalRequest.addInputParam("@i_institucion_operante", ICTSTypes.SQLVARCHAR, data.get(0));
			anOriginalRequest.addInputParam("@i_monto", ICTSTypes.SQLMONEY, anOriginalRequest.readValueParam("@i_val"));
			anOriginalRequest.addInputParam("@i_nombre_beneficiario", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@i_nombre_benef"));
			anOriginalRequest.addInputParam("@i_nombre_ordenante", ICTSTypes.SQLVARCHAR, data.get(1));
			anOriginalRequest.addInputParam("@i_referencia_numerica", ICTSTypes.SQLVARCHAR, ""); // OPCIONAL
			anOriginalRequest.addInputParam("@i_rfc_curp_beneficiario", ICTSTypes.SQLVARCHAR, ""); // OPCIONAL
			anOriginalRequest.addInputParam("@i_rfc_curp_ordenante", ICTSTypes.SQLVARCHAR, data.get(2));
			anOriginalRequest.addInputParam("@i_tipo_cuenta_beneficiario", ICTSTypes.SQLINT1,
					anOriginalRequest.readValueParam("@i_prod_des"));
			anOriginalRequest.addInputParam("@i_tipo_cuenta_ordenante", ICTSTypes.SQLINT1, data.get(3));

			anOriginalRequest.addInputParam("@i_tipo_pago", ICTSTypes.SQLINT1, "1");
			anOriginalRequest.addInputParam("@i_id", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@s_ssn"));
			// VARIABLES DE SALIDA
			anOriginalRequest.addOutputParam("@o_cod_respuesta", ICTSTypes.SQLVARCHAR, "X");
			anOriginalRequest.addOutputParam("@o_msj_respuesta", ICTSTypes.SQLVARCHAR, "X");
			anOriginalRequest.addOutputParam("@o_clave_rastreo", ICTSTypes.SQLVARCHAR, "X");
			anOriginalRequest.addOutputParam("@o_id", ICTSTypes.SQLINT1, "0");
			anOriginalRequest.addOutputParam("@o_descripcion_error", ICTSTypes.SQLVARCHAR, "X");

			// SE HACE LA LLAMADA AL CONECTOR
			bag.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorSpei)");
			anOriginalRequest.setSpName("cob_procesador..sp_orq_banpay_spei");
			anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1870013");
			anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "1870013");

			// SE EJECUTA
			IProcedureResponse connectorSpeiResponse = executeProvider(anOriginalRequest, bag);
			// SE VALIDA LA RESPUESTA
			if (!connectorSpeiResponse.hasError()) {
				if (logger.isDebugEnabled()) {
					logger.logDebug("success CISConnectorSpei: true");
					logger.logDebug("connectorSpeiResponse: " + connectorSpeiResponse.getParams());
				}
				// SE MAPEAN LAS VARIABLES DE SALIDA
				response = new ArrayList<String>();
				response.add(connectorSpeiResponse.readValueParam("@o_cod_respuesta"));
				logger.logDebug(
						"readValueParam @o_cod_respuesta: " + connectorSpeiResponse.readValueParam("@o_cod_respuesta"));
				logger.logDebug(
						"readValueParam @o_cod_respuesta: " + connectorSpeiResponse.readParam("@o_cod_respuesta"));
				response.add(connectorSpeiResponse.readValueParam("@o_msj_respuesta"));
				response.add(connectorSpeiResponse.readValueParam("@o_clave_rastreo"));
				response.add(connectorSpeiResponse.readValueParam("@o_id"));
				response.add(connectorSpeiResponse.readValueParam("@o_descripcion_error"));

				// SE ALMACENA EL DATO DE CLAVE DE RASTREO
				bag.put("@i_clave_rastreo", connectorSpeiResponse.readValueParam("@o_clave_rastreo"));
				bag.put("@i_msj_respuesta", connectorSpeiResponse.readValueParam("@o_msj_respuesta"));
				bag.put("@i_cod_respuesta", connectorSpeiResponse.readValueParam("@o_cod_respuesta"));
				bag.put("@i_id", connectorSpeiResponse.readValueParam("@o_id"));
				bag.put("@i_descripcion_error", connectorSpeiResponse.readValueParam("@o_descripcion_error"));
				data = null;
			} else {

				if (logger.isDebugEnabled()) {
					logger.logDebug("Error Catastrifico respuesta de BANPAY");
					logger.logDebug("Error connectorSpeiResponse Catastrifico: " + connectorSpeiResponse);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = null;
			logger.logInfo("Error Catastrofico de banpayExecution");

		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo("Saliendo de banpayExecution");
			}
		}
		// SE REGRESA RESPUESTA
		return response;
	}

	@Override
	public void loadConfiguration(IConfigurationReader arg) {
		if (logger.isInfoEnabled())
			logger.logInfo("LOAD CONFIGUATION");
	}

	public IProcedureResponse executeTransferSPI(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) throws CTSServiceException, CTSInfrastructureException {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Inicia executeTransferSPI");
		}
		IProcedureResponse response = new ProcedureResponseAS();
		IProcedureResponse responseBank = executeCoreBanking(this.getRequestBank(anOriginalRequest));
		if (logger.isDebugEnabled()) {
			logger.logDebug("Response Bank --> " + responseBank.getProcedureResponseAsString());
		}

		response.setReturnCode(responseBank.getReturnCode());
		if (responseBank.getReturnCode() != 0) {
			response = Utils.returnException(Utils.returnArrayMessage(responseBank));

		}

		if (responseBank.getReturnCode() == 0 && responseBank.getResultSetListSize() > 0) {

			IResultSetRow[] rows = responseBank.getResultSet(responseBank.getResultSetListSize()).getData()
					.getRowsAsArray();
			IResultSetRowColumnData[] columns = rows[0].getColumnsAsArray();

			IProcedureResponse responseLocalValidation = (IProcedureResponse) aBagSPJavaOrchestration
					.get(RESPONSE_LOCAL_VALIDATION);
			IProcedureRequest requestTransfer = this.getRequestTransfer(anOriginalRequest, responseLocalValidation);

			requestTransfer.addInputParam("@i_nom_banco_des", ICTSTypes.SYBVARCHAR, columns[0].getValue());
			aBagSPJavaOrchestration.put("@i_banco_dest", columns[0].getValue());
			requestTransfer.addInputParam("@i_ruta_trans", ICTSTypes.SYBVARCHAR, columns[2].getValue());

			response = executeCoreBanking(requestTransfer);
			if (logger.isDebugEnabled()) {
				logger.logDebug("Request accountTransfer: " + anOriginalRequest.getProcedureRequestAsString());
				logger.logDebug("aBagSPJavaOrchestration SPEI: " + aBagSPJavaOrchestration.toString());
			}
		}

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response accountTransfer:" + response.getProcedureResponseAsString());
			logger.logDebug("Fin executeTransferSPI");
		}

		return response;
	}

	/**
	 * Permite obtener el request para obtener los datos del banco
	 * 
	 * @param anOriginalRequest
	 * @return
	 */
	private IProcedureRequest getRequestBank(IProcedureRequest anOriginalRequest) {
		IProcedureRequest requestBank = new ProcedureRequestAS();

		requestBank.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1870013");
		requestBank.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		requestBank.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		requestBank.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");

		requestBank.setSpName("cob_bvirtual..sp_mant_ifis");
		requestBank.addInputParam("@t_online", ICTSTypes.SQLCHAR, "S");
		requestBank.addInputParam("@t_trn", ICTSTypes.SYBINTN, "1870009");

		requestBank.addInputParam("@i_cod_ban", ICTSTypes.SQLINT4, anOriginalRequest.readValueParam("@i_banco_ben"));
		requestBank.addInputParam("@i_grupo", ICTSTypes.SQLINT4, "1");
		requestBank.addInputParam("@i_tip_tran", ICTSTypes.SQLVARCHAR, "S");
		requestBank.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "S");

		return requestBank;
	}

	/**
	 * Método que permite crear un request para ser enviado al Corebanking
	 * 
	 * @param anOriginalRequest       Request original
	 * @param lastResponse            Último response recibido.
	 * @param aBagSPJavaOrchestration Objetos que son resultado de la ejecución de
	 *                                los métodos.
	 */
	private IProcedureRequest getRequestTransfer(IProcedureRequest anOriginalRequest,
			IProcedureResponse responseLocalValidation) {
		if (logger.isInfoEnabled()) {
			logger.logInfo("Inicia transfer SPI");
		}

		IProcedureRequest requestTransfer = new ProcedureRequestAS();

		requestTransfer.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "1870013");
		requestTransfer.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		requestTransfer.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		requestTransfer.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");

		requestTransfer.setSpName("cob_ahorros..sp_tr04_transferencia_ob");
		requestTransfer.addInputParam("@t_online", ICTSTypes.SQLCHAR, "S");
		requestTransfer.addInputParam("@t_trn", ICTSTypes.SYBINTN, "18340");

		requestTransfer.addInputParam(S_USER, anOriginalRequest.readParam(S_USER).getDataType(),
				anOriginalRequest.readValueParam(S_USER));
		requestTransfer.addInputParam(S_TERM, anOriginalRequest.readParam(S_TERM).getDataType(),
				anOriginalRequest.readValueParam(S_TERM));
		requestTransfer.addInputParam(S_ROL, anOriginalRequest.readParam(S_ROL).getDataType(),
				anOriginalRequest.readValueParam(S_ROL));
		requestTransfer.addInputParam(S_SRV, anOriginalRequest.readParam(S_SRV).getDataType(),
				anOriginalRequest.readValueParam(S_SRV));
		requestTransfer.addInputParam(S_DATE_LOCAL, anOriginalRequest.readParam(S_DATE_LOCAL).getDataType(),
				anOriginalRequest.readValueParam(S_DATE_LOCAL));
		requestTransfer.addInputParam(S_OFI, anOriginalRequest.readParam(S_OFI).getDataType(),
				anOriginalRequest.readValueParam(S_OFI));
		requestTransfer.addInputParam(S_SRV, anOriginalRequest.readParam(S_SRV).getDataType(),
				anOriginalRequest.readValueParam(S_SRV));
		requestTransfer.addInputParam(T_EJEC, anOriginalRequest.readParam(T_EJEC).getDataType(),
				anOriginalRequest.readValueParam(T_EJEC));
		requestTransfer.addInputParam(T_RTY, anOriginalRequest.readParam(T_RTY).getDataType(),
				anOriginalRequest.readValueParam(T_RTY));

		if (logger.isInfoEnabled())
			logger.logInfo("PRE COMISION --->   RECUPERADA");

		if (anOriginalRequest != null && anOriginalRequest.readValueParam("@i_comision") != null) {

			logger.logInfo("ENTRA VALIDACION COMISION");

			if (logger.isInfoEnabled())
				logger.logInfo(
						"Llegada de comisiom 3.1416 SPEIDO ---> " + anOriginalRequest.readValueParam("@i_comision"));

			requestTransfer.addInputParam("@i_comision", ICTSTypes.SYBMONEY,
					anOriginalRequest.readValueParam("@i_comision"));
		} else {
			logger.logInfo("NO ENTRA VALIDACION COMISION > 0");
			requestTransfer.addInputParam("@i_comision", ICTSTypes.SYBMONEY, "0");
		}

		if ("1".equals(anOriginalRequest.readValueParam(S_SERVICIO_LOCAL))
				|| "8".equals(anOriginalRequest.readValueParam(S_SERVICIO_LOCAL))
				|| "10".equals(anOriginalRequest.readValueParam(S_SERVICIO_LOCAL))) {

			logger.logInfo("ENTRA VALIDACION TIPO SERVICIO 1,8,10");
			// CUENTA ORIGEN
			requestTransfer.addInputParam(I_CTA_LOCAL, anOriginalRequest.readParam(I_CTA_LOCAL).getDataType(),
					anOriginalRequest.readValueParam(I_CTA_LOCAL));
			requestTransfer.addInputParam(I_PROD_LOCAL, anOriginalRequest.readParam(I_PROD_LOCAL).getDataType(),
					anOriginalRequest.readValueParam(I_PROD_LOCAL));
			requestTransfer.addInputParam(I_MON_LOCAL, anOriginalRequest.readParam(I_MON_LOCAL).getDataType(),
					anOriginalRequest.readValueParam(I_MON_LOCAL));

			// CUENTA DESTINO
			requestTransfer.addInputParam(I_CTA_DES_LOCAL, anOriginalRequest.readParam(I_CTA_DES_LOCAL).getDataType(),
					anOriginalRequest.readValueParam(I_CTA_DES_LOCAL));

			if (anOriginalRequest.readValueParam(I_PROD_DES_LOCAL) != null) {
				logger.logInfo("ENTRA VALIDACION I_PROD_DES_LOCAL");
				requestTransfer.addInputParam(I_PROD_DES_LOCAL,
						anOriginalRequest.readParam(I_PROD_DES_LOCAL).getDataType(),
						anOriginalRequest.readValueParam(I_PROD_DES_LOCAL));
			}

			requestTransfer.addInputParam(I_MON_DES_LOCAL, anOriginalRequest.readParam(I_MON_DES_LOCAL).getDataType(),
					anOriginalRequest.readValueParam(I_MON_DES_LOCAL));

			// VALORES DE TRANSACCION
			requestTransfer.addInputParam(I_VAL_LOCAL, anOriginalRequest.readParam(I_VAL_LOCAL).getDataType(),
					anOriginalRequest.readValueParam(I_VAL_LOCAL));
			requestTransfer.addInputParam(I_CONCEPTO_LOCAL, anOriginalRequest.readParam(I_CONCEPTO_LOCAL).getDataType(),
					anOriginalRequest.readValueParam(I_CONCEPTO_LOCAL));
			requestTransfer.addInputParam(I_NOMBRE_BENEF, anOriginalRequest.readParam(I_NOMBRE_BENEF).getDataType(),
					anOriginalRequest.readValueParam(I_NOMBRE_BENEF));
			/*
			 * requestTransfer.addInputParam("@i_ced_ruc_ben",
			 * anOriginalRequest.readParam(I_DOC_BENEF).getDataType(),
			 * anOriginalRequest.readValueParam(I_DOC_BENEF));
			 */
			requestTransfer.addInputParam(I_BANCO_BEN, anOriginalRequest.readParam(I_BANCO_BEN).getDataType(),
					anOriginalRequest.readValueParam(I_BANCO_BEN));
			requestTransfer.addInputParam("@i_servicio", anOriginalRequest.readParam(S_SERVICIO_LOCAL).getDataType(),
					anOriginalRequest.readValueParam(S_SERVICIO_LOCAL));
		}

		if ("6".equals(anOriginalRequest.readValueParam(S_SERVICIO_LOCAL))
				|| "7".equals(anOriginalRequest.readValueParam(S_SERVICIO_LOCAL))) {
			logger.logInfo("ENTRA VALIDACION TIPO SERVICIO 6,7");
			requestTransfer.addInputParam(I_MON_LOCAL, responseLocalValidation.readParam("@o_mon").getDataType(),
					responseLocalValidation.readValueParam("@o_mon"));
			requestTransfer.addInputParam("@i_prod_org", responseLocalValidation.readParam("@o_prod").getDataType(),
					responseLocalValidation.readValueParam("@o_prod"));
			requestTransfer.addInputParam("@i_cta_org", responseLocalValidation.readParam("@o_cta").getDataType(),
					responseLocalValidation.readValueParam("@o_cta"));
			requestTransfer.addInputParam(I_PROD_DES_LOCAL,
					responseLocalValidation.readParam("@o_prod_des").getDataType(),
					responseLocalValidation.readValueParam("@o_prod_des"));
			requestTransfer.addInputParam(I_CTA_DES_LOCAL,
					responseLocalValidation.readParam("@o_cta_des").getDataType(),
					responseLocalValidation.readValueParam("@o_cta_des"));
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo("Fin transfer SPI");
		}
		return requestTransfer;
	}

	/**
	 * Arma la respuesta al servicio
	 * 
	 * @param responseTransfer
	 * @param aBagSPJavaOrchestration
	 * @return
	 */
	private IProcedureResponse transformToProcedureResponse(IProcedureResponse responseTransfer,
			Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Inicia transformToProcedureResponse");
		}

		IProcedureResponse response = new ProcedureResponseAS();
		response.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
		IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
		ServerResponse serverResponse = (ServerResponse) aBagSPJavaOrchestration.get(RESPONSE_SERVER);

		response.setReturnCode(responseTransfer.getReturnCode());
		if (serverResponse.getOnLine() && responseTransfer.getReturnCode() != 0) {
			// ONLINE Y HUBO ERROR
			response = Utils.returnException(Utils.returnArrayMessage(responseTransfer));

		} else {
			response.addParam("@o_referencia", ICTSTypes.SYBINT4, 0,
					String.valueOf(originalRequest.readValueParam(S_SSN_BRANCH)));
			response.setReturnCode(responseTransfer.getReturnCode());
			aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, response);
		}

		aBagSPJavaOrchestration.put(RESPONSE_TRANSACTION, response);

		if (logger.isDebugEnabled()) {
			logger.logDebug(CLASS_NAME + "Respuesta transformToProcedureResponse -->"
					+ response.getProcedureResponseAsString());
			logger.logDebug("Fin transformToProcedureResponse");
		}
		return response;
	}

	@Override
	public ICoreServiceReexecutionComponent getCoreServiceReexecutionComponent() {
		return null;
	}

	@Override
	protected ICoreServiceSendNotification getCoreServiceNotification() {
		return coreServiceNotification;
	}

	@Override
	public ICoreService getCoreService() {
		return coreService;
	}

	@Override
	public ICoreServer getCoreServer() {
		return coreServer;
	}

	@Override
	protected IProcedureResponse validateCentralExecution(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cobiscorp.ecobis.ib.orchestration.base.transfers.TransferBaseTemplate
	 * #transformNotificationRequest(com.cobiscorp.cobis.cts.domains.
	 * IProcedureRequest,
	 * com.cobiscorp.ecobis.ib.application.dtos.OfficerByAccountResponse,
	 * java.util.Map)
	 */
	@Override
	public NotificationRequest transformNotificationRequest(IProcedureRequest anOriginalRequest,
			OfficerByAccountResponse anOfficer, Map<String, Object> aBagSPJavaOrchestration) {
		NotificationRequest notificationRequest = new NotificationRequest();
		notificationRequest.setOriginalRequest(anOriginalRequest);
		Notification notification = new Notification();

		Client client = new Client();
		client.setIdCustomer(anOriginalRequest.readValueParam("@s_cliente"));

		Product product = new Product();
		product.setProductType(Integer.parseInt(anOriginalRequest.readValueParam("@i_prod")));
		if (!Utils.isNull(anOriginalRequest.readParam("@i_cta"))) {
			product.setProductNumber(anOriginalRequest.readValueParam("@i_cta"));
		}
		if (product.getProductType() == 3)
			notification.setId("N90");
		else
			notification.setId("N91");

		NotificationDetail notificationDetail = new NotificationDetail();

		if (!Utils.isNull(anOfficer.getOfficer().getOfficerEmailAdress()))
			notificationDetail.setEmailClient(anOfficer.getOfficer().getOfficerEmailAdress());
		if (!Utils.isNull(anOfficer.getOfficer().getAcountEmailAdress()))
			notificationDetail.setEmailOficial(anOfficer.getOfficer().getAcountEmailAdress());

		if (!Utils.isNull(anOriginalRequest.readParam("@i_cta")))
			notificationDetail.setAccountNumberDebit(anOriginalRequest.readValueParam("@i_cta"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_cta_des")))
			notificationDetail.setAccountNumberCredit(anOriginalRequest.readValueParam("@i_cta_des"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_concepto")))
			notificationDetail.setNote(anOriginalRequest.readValueParam("@i_concepto"));

		if (!Utils.isNull(anOriginalRequest.readParam("@i_mon"))) {
			notificationDetail.setCurrencyId1(anOriginalRequest.readValueParam("@i_mon"));
			notificationDetail.setCurrencyId2(anOriginalRequest.readValueParam("@i_mon"));
		}

		if (!Utils.isNull(anOriginalRequest.readParam("@i_val")))
			notificationDetail.setValue(anOriginalRequest.readValueParam("@i_val"));

		if (!Utils.isNull(anOriginalRequest.readParam("@s_date")))
			notificationDetail.setDateNotification(anOriginalRequest.readValueParam("@s_date"));
		if (!Utils.isNull(anOriginalRequest.readParam(S_SSN_BRANCH)))
			notificationDetail.setReference(anOriginalRequest.readValueParam(S_SSN_BRANCH));
		if (!Utils.isNull(anOriginalRequest.readParam(I_NOMBRE_BENEF)))
			notificationDetail.setAuxiliary1(anOriginalRequest.readValueParam(I_NOMBRE_BENEF));

		notificationRequest.setClient(client);
		notificationRequest.setNotification(notification);
		notificationRequest.setNotificationDetail(notificationDetail);
		notificationRequest.setOriginProduct(product);
		return notificationRequest;
	}

	@Override
	protected void addParametersRequestUpdateLocal(IProcedureRequest aProcedureRequest,
			IProcedureRequest anOriginalRequest) {
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
	}

	protected List<String> speiData(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		// SE INICIALIZA LA LISTA DE STRINGS
		List<String> fres = new ArrayList<String>();
		if (logger.isInfoEnabled()) {
			logger.logInfo("Entrando a speiData");
		}
		try {
			IProcedureRequest request = initProcedureRequest(anOriginalRequest.clone());

			// SE SETEAN DATOS
			request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
					IMultiBackEndResolverService.TARGET_LOCAL);
			request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
			request.setSpName("cob_bvirtual..sp_registra_spei");
			request.addInputParam("@t_trn", ICTSTypes.SQLINTN, "18010");
			request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "E");
			request.addInputParam("@i_ente_bv", ICTSTypes.SYBINT4, anOriginalRequest.readValueParam("@s_cliente"));
			// SE SETEA VARIABLE DE SALIDA
			request.addOutputParam("@o_salida", ICTSTypes.SYBVARCHAR, "XXX");
			request.addOutputParam("@o_nom_ordenante", ICTSTypes.SYBVARCHAR, "XXX");
			request.addOutputParam("@o_curp_ordenante", ICTSTypes.SYBVARCHAR, "XXX");
			request.addOutputParam("@o_tipo_cuenta_ord", ICTSTypes.SYBVARCHAR, "XXX");
			// SE EJECUTA Y SE OBTIENE LA RESPUESTA
			IProcedureResponse pResponse = executeCoreBanking(request);

			// SE OBTIENE LA RESPUESTA
			fres.add(pResponse.readValueParam("@o_salida"));
			fres.add(pResponse.readValueParam("@o_nom_ordenante"));
			fres.add(pResponse.readValueParam("@o_curp_ordenante"));
			fres.add(pResponse.readValueParam("@o_tipo_cuenta_ord"));

		} catch (Exception e) {
			e.printStackTrace();
			logger.logInfo("Error de speiData");
		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo("Saliendo de speiData");
			}
		}
		// SE REGRESA RESPUESTA
		return fres;
	}

	protected boolean speiRollback(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		// SE INICIALIZA VARIABLE
		boolean response = false;
		if (logger.isInfoEnabled()) {
			logger.logInfo("Entrando a speiRollback");
		}
		try {
			IProcedureRequest request = initProcedureRequest(anOriginalRequest.clone());

			// SE SETEAN DATOS
			request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_CENTRAL);
			request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
			request.setSpName("cob_bvirtual..sp_reverso_spei");
			request.addInputParam("@t_trn", ICTSTypes.SQLINTN, "18009");
			
			// DATOS CUENTA ORIGEN
			request.addInputParam("@i_cuenta_ori", ICTSTypes.SQLVARCHAR, bag.get("@o_cuenta_ori").toString());
			request.addInputParam("@i_concepto", ICTSTypes.SQLVARCHAR, ERROR_SPEI);
			request.addInputParam("@i_monto", ICTSTypes.SQLMONEY, bag.get("@o_monto").toString());
			request.addInputParam("@i_mon", ICTSTypes.SQLINT1, bag.get("@o_mon").toString());
			request.addInputParam("@i_servicio", ICTSTypes.SQLINT1, anOriginalRequest.readValueParam(S_SERVICIO_LOCAL));
			request.addInputParam("@i_tipo_error", ICTSTypes.SQLINTN, "7");
			//VALIDA COMISION
			if (bag.get("@o_comision") != null) {
				request.addInputParam("@i_comision", ICTSTypes.SQLMONEY, bag.get("@o_comision").toString());
			} else {
				request.addInputParam("@i_comision", ICTSTypes.SQLMONEY, "0");
			}
			request.addInputParam("@s_ssn_branch", ICTSTypes.SQLINTN, bag.get("@o_ssn_branch").toString());
			// CLAVE DE RASTREO
			request.addInputParam("@i_clave_rastreo", ICTSTypes.SQLVARCHAR, bag.get("@i_clave_rastreo").toString());
			logger.logInfo("@i_clave_rastreo bag: " + bag.get("@i_clave_rastreo"));
			//VARIABLES DE CTS
			request.addInputParam("@s_ssn", ICTSTypes.SQLINTN, anOriginalRequest.readValueParam("@s_ssn"));
			request.addInputParam("@s_srv", ICTSTypes.SQLINTN, anOriginalRequest.readValueParam("@s_srv"));			
			request.addInputParam("@s_user", ICTSTypes.SQLINTN, anOriginalRequest.readValueParam("@s_user"));
			request.addInputParam("@s_term", ICTSTypes.SQLINTN, anOriginalRequest.readValueParam("@s_term"));
			request.addInputParam("@s_rol", ICTSTypes.SQLINTN, anOriginalRequest.readValueParam("@s_rol"));
			request.addInputParam("@s_date", ICTSTypes.SQLDATETIME, anOriginalRequest.readValueParam("@s_date"));

			// SE EJECUTA Y SE OBTIENE LA RESPUESTA
			IProcedureResponse pResponse = executeCoreBanking(request);

			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.logInfo("Error de speiRollback");
			response = false;
		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo("Saliendo de speiRollback");
			}
		}
		// SE REGRESA RESPUESTA
		return response;
	}

	protected boolean speiGetDataRB(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		// SE INICIALIZA VARIABLE
		boolean response = false;
		if (logger.isInfoEnabled()) {
			logger.logInfo("Entrando a speiGetDataRB");
		}
		try {
			IProcedureRequest request = initProcedureRequest(anOriginalRequest.clone());

			// SE SETEAN DATOS
			request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, IMultiBackEndResolverService.TARGET_CENTRAL);
			request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
			request.setSpName("cob_bvirtual..sp_secuencial_spei");
			request.addInputParam("@t_trn", ICTSTypes.SQLINTN, "18011");
			request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "C");

			request.addInputParam("@i_clave_rastreo", ICTSTypes.SQLVARCHAR, bag.get("@i_clave_rastreo").toString());
			logger.logInfo("@i_clave_rastreo bag: " + bag.get("@i_clave_rastreo"));
			
			request.addInputParam("@i_cuenta_ori", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cta"));

			request.addInputParam("@i_estatus_respuesta", ICTSTypes.SQLINTN, bag.get("@i_cod_respuesta").toString());
			request.addInputParam("@i_descripcion_error", ICTSTypes.SQLVARCHAR, ERROR_SPEI);
			
			//SE SETEAN VARIABLES DE SALIDA
			request.addOutputParam("@o_cuenta_ori", ICTSTypes.SQLVARCHAR, "XXXX");
			request.addOutputParam("@o_monto", ICTSTypes.SQLMONEY, "0");
			request.addOutputParam("@o_mon", ICTSTypes.SQLINTN, "0");
			request.addOutputParam("@o_comision", ICTSTypes.SQLMONEY, "0");
			request.addOutputParam("@o_proceso_origen", ICTSTypes.SQLINTN, "0");
			request.addOutputParam("@o_tipo_error", ICTSTypes.SQLINTN, "0");
			request.addOutputParam("@o_ssn_branch", ICTSTypes.SQLINTN, "0");

			// SE EJECUTA Y SE OBTIENE LA RESPUESTA
			IProcedureResponse pResponse = executeCoreBanking(request);
			
			//SE OBTIENEN LAS VARIABLES DE SALIDA
			bag.put("@o_cuenta_ori", pResponse.readValueParam("@o_cuenta_ori"));
			bag.put("@o_monto", pResponse.readValueParam("@o_monto"));
			bag.put("@o_mon", pResponse.readValueParam("@o_mon"));
			bag.put("@o_comision", pResponse.readValueParam("@o_comision"));
			bag.put("@o_proceso_origen", pResponse.readValueParam("@o_proceso_origen"));
			bag.put("@o_tipo_error", pResponse.readValueParam("@o_tipo_error"));
			bag.put("@o_ssn_branch", pResponse.readValueParam("@o_ssn_branch"));
			
			logger.logInfo("@o_cuenta_ori bag: " + bag.get("@o_cuenta_ori"));
			logger.logInfo("@o_monto bag: " + bag.get("@o_monto"));
			logger.logInfo("@o_mon bag: " + bag.get("@o_mon"));
			logger.logInfo("@o_comision bag: " + bag.get("@o_comision"));
			logger.logInfo("@o_proceso_origen bag: " + bag.get("@o_proceso_origen"));
			logger.logInfo("@o_tipo_error bag: " + bag.get("@o_tipo_error"));
			logger.logInfo("@o_ssn_branch bag: " + bag.get("@o_ssn_branch"));

			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			response = false;
			logger.logInfo("Error de speiGetDataRB");
		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo("Saliendo de speiGetDataRB");
			}
		}
		// SE REGRESA RESPUESTA
		return response;
	}

	protected boolean speiUpdateTmp(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		// SE INICIALIZA VARIABLE
		boolean response = false;
		if (logger.isInfoEnabled()) {
			logger.logInfo("Entrando a speiUpdateTmp");
		}
		try {
			IProcedureRequest request = initProcedureRequest(anOriginalRequest.clone());

			// SE SETEAN DATOS
			request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
					IMultiBackEndResolverService.TARGET_LOCAL);
			request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
			request.setSpName("cob_bvirtual..sp_registra_spei");
			request.addInputParam("@t_trn", ICTSTypes.SQLINTN, "18010");
			request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "F");

			request.addInputParam("@i_clave_rastreo_tmp", ICTSTypes.SQLVARCHAR,
					bag.get("@i_clave_rastreo_tmp").toString());
			logger.logInfo("@i_clave_rastreo_tmp bag: " + bag.get("@i_clave_rastreo_tmp"));

			// SE EJECUTA Y SE OBTIENE LA RESPUESTA
			IProcedureResponse pResponse = executeCoreBanking(request);

			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			response = false;
			logger.logInfo("Error de speiUpdateTmp");
		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo("Saliendo de speiUpdateTmp");
			}
		}
		// SE REGRESA RESPUESTA
		return response;
	}

	protected boolean speiSec(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		// SE INICIALIZA VARIABLE
		boolean response = false;
		if (logger.isInfoEnabled()) {
			logger.logInfo("Entrando a speiSec");
		}
		try {
			IProcedureRequest request = initProcedureRequest(anOriginalRequest.clone());

			// SE SETEAN DATOS
			request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
					IMultiBackEndResolverService.TARGET_CENTRAL);
			request.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
			request.setSpName("cob_bvirtual..sp_secuencial_spei");

			request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "B");
			request.addInputParam("@t_trn", ICTSTypes.SQLINTN, "18011");
			
			request.addInputParam("@i_cuenta_ori", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_cta"));

			request.addInputParam("@i_clave_rastreo", ICTSTypes.SQLVARCHAR, bag.get("@i_clave_rastreo").toString());
			logger.logInfo("@i_clave_rastreo bag: " + bag.get("@i_clave_rastreo"));

			// SE EJECUTA Y SE OBTIENE LA RESPUESTA
			IProcedureResponse pResponse = executeCoreBanking(request);

			response = true;
		} catch (Exception e) {
			e.printStackTrace();
			logger.logInfo("Error de speiSec");
			response = false;
		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo("Saliendo de speiSec");
			}
		}
		// SE REGRESA RESPUESTA
		return response;
	}
}
