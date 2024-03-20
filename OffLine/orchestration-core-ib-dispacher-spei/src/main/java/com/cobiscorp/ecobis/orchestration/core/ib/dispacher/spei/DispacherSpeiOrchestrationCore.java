package com.cobiscorp.ecobis.orchestration.core.ib.dispacher.spei;

import static com.cobiscorp.cobis.cts.domains.ICOBISTS.COBIS_HOME;

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXB;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.crypt.ReadAlgn;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
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
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.application.dtos.ServerResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.mensaje;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSelfAccountTransfers;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;
import com.cobiscorp.ecobis.orchestration.core.ib.dispacher.dto.Constans;
import com.cobiscorp.ecobis.orchestration.core.ib.dispacher.dto.Mensaje;
import com.cobiscorp.ecobis.orchestration.core.ib.dispacher.dto.Respuesta;
import com.cobiscorp.ecobis.orchestration.core.ib.transfer.template.DispatcherSpeiOfflineTemplate;



/**
 * Plugin of Dispacher Spei
 *
 * @since Dec 29, 2022
 * @author jolmos
 * @version 1.0.0
 *
 */
@Component(name = "DispacherSpeiOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "DispacherSpeiOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
		@Property(name = "service.identifier", value = "DispacherSpeiOrchestrationCore") })
public class DispacherSpeiOrchestrationCore extends DispatcherSpeiOfflineTemplate {

	private java.util.Properties properties = null;
	
	@Override
	public void loadConfiguration(IConfigurationReader reader) {
		 this.properties = reader.getProperties("//property");
	}

	/**
	 * Instance logger component
	 */
	private static ILogger logger = LogFactory.getLogger(DispacherSpeiOrchestrationCore.class);
	private static final String CORESERVICEMONETARYTRANSACTION = "coreServiceMonetaryTransaction";

	@Reference(referenceInterface = ICoreServiceMonetaryTransaction.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceMonetaryTransaction", unbind = "unbindCoreServiceMonetaryTransaction")
	protected ICoreServiceMonetaryTransaction coreServiceMonetaryTransaction;

	public void bindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = service;
	}

	public void unbindCoreServiceMonetaryTransaction(ICoreServiceMonetaryTransaction service) {
		coreServiceMonetaryTransaction = null;
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServer.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServer", unbind = "unbindCoreServer")
	protected ICoreServer coreServer;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreServer(ICoreServer service) {
		coreServer = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreServer(ICoreServer service) {
		coreServer = null;
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreServiceSelfAccountTransfers.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceSelfAccountTransfers", unbind = "unbindCoreServiceSelfAccountTransfers")
	private ICoreServiceSelfAccountTransfers coreServiceSelfAccountTransfers;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	protected void bindCoreServiceSelfAccountTransfers(ICoreServiceSelfAccountTransfers service) {
		coreServiceSelfAccountTransfers = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	protected void unbindCoreServiceSelfAccountTransfers(ICoreServiceSelfAccountTransfers service) {
		coreServiceSelfAccountTransfers = null;
	}

	@Override
	public ICoreService getCoreService() {
		return coreService;
	}

	/**
	 * Instance plugin to use services other core banking
	 */
	@Reference(referenceInterface = ICoreService.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreService", unbind = "unbindCoreService")
	protected ICoreService coreService;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreService(ICoreService service) {
		coreService = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreService(ICoreService service) {
		coreService = null;
	}

	@Reference(referenceInterface = ICoreServiceSendNotification.class, cardinality = ReferenceCardinality.OPTIONAL_UNARY, bind = "bindCoreServiceNotification", unbind = "unbindCoreServiceNotification")
	public ICoreServiceSendNotification coreServiceNotification;

	/**
	 * Instance Service Interface
	 *
	 * @param service
	 */
	public void bindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = service;
	}

	/**
	 * Deleting Service Interface
	 *
	 * @param service
	 */
	public void unbindCoreServiceNotification(ICoreServiceSendNotification service) {
		coreServiceNotification = null;
	}

	/**
	 * /** Execute transfer first step of service
	 * <p>
	 * This method is the main executor of transactional contains the original input
	 * parameters.
	 *
	 * @param anOriginalRequest       - Information original sended by user's.
	 * @param aBagSPJavaOrchestration - Object dictionary transactional steps.
	 *
	 * @return
	 *         <ul>
	 *         <li>IProcedureResponse - Represents the service execution.</li>
	 *         </ul>
	 */
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		
		if (logger.isDebugEnabled())
		{
			logger.logDebug("JCOS DispacherSpeiOrchestrationCore: executeJavaOrchestration");
			logger.logDebug(anOriginalRequest);
		}
		 //archivo credenciales llave jks
        String pathAlgnJks = System.getProperty(COBIS_HOME) + properties.getProperty("jksAlgncon");
        //archivo path llave jks
        String pathCertificado = System.getProperty(COBIS_HOME) + properties.getProperty("jksurl");
        File jksAlgn = new File(pathAlgnJks);
        if (jksAlgn.exists()) {
            ReadAlgn rjksAlgncon = new ReadAlgn(pathAlgnJks);
            aBagSPJavaOrchestration.put("alias", rjksAlgncon.leerParametros().getProperty("l"));
            aBagSPJavaOrchestration.put("keyPass", rjksAlgncon.leerParametros().getProperty("p"));
            aBagSPJavaOrchestration.put("jksAlgncon", pathAlgnJks);
        }else
        {
        	if (logger.isDebugEnabled())
    		{
    			logger.logDebug("jksAlgncon:"+pathAlgnJks);
    			logger.logDebug("jksurl:"+pathCertificado);
    		}
        }
		// METODO GUARDAR XML
        if (logger.isDebugEnabled())
		{
			logger.logDebug("No existe archivo jks:"+pathCertificado);
		}
		aBagSPJavaOrchestration.put(TRANSFER_NAME, "SPEI DISPACHER");
		mensaje message = null;

		try {

			String xmls = anOriginalRequest.readValueParam("@i_pay_order");
			DispatcherUtil plot = new DispatcherUtil();
			message = plot.getDataMessage(xmls);
			if (message != null) {
				aBagSPJavaOrchestration.put("speiTransaction", message);
				executeStepsTransactionsBase(anOriginalRequest, aBagSPJavaOrchestration);
			}
			

		}catch (CTSServiceException e) {
			e.printStackTrace();
		} catch (CTSInfrastructureException e) {
			e.printStackTrace();
		}catch (Exception xe) {
			logger.logError(xe);
		}

	/*	if (response != null && !response.hasError() && response.getReturnCode() == 0) {
			String idDevolucion = response.readValueParam("@o_id_causa_devolucion");
			if (null == idDevolucion || "0".equals(idDevolucion)) {
				notifySpei(anOriginalRequest, aBagSPJavaOrchestration);
			}
		}*/
		
		IProcedureResponse valuesOutput = new ProcedureResponseAS();
		valuesOutput.addParam("@o_result", 39, 1, aBagSPJavaOrchestration.get("result").toString());

		return valuesOutput;//processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
	}

	@Override
	protected ICoreServiceSendNotification getCoreServiceNotification() {
		return coreServiceNotification;
	}

	@Override
	public ICoreServer getCoreServer() {
		return coreServer;
	}

	public IProcedureResponse executeTransfer(Map<String, Object> aBagSPJavaOrchestration) {// throws
																							// CTSServiceException,
																							// CTSInfrastructureException
		IProcedureResponse response = null;
		try {
			IProcedureRequest anOriginalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
			response = mappingResponse(executeTransferSpeiIn(anOriginalRequest, aBagSPJavaOrchestration),
					aBagSPJavaOrchestration);
		} catch (Exception e) {
			logger.logError("AN ERROR OCURRED: ", e);
		}

		return response;
	}

	private IProcedureResponse mappingResponse(IProcedureResponse aResponse,
			Map<String, Object> aBagSPJavaOrchestration) {
		String wInfo = CLASS_NAME + "[mappingResponse] ";
		logger.logInfo(wInfo + INIT_TASK);

		return null;
	}

	private IProcedureResponse executeTransferSpeiIn(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		String wInfo = CLASS_NAME + "[executeTransferSpeiIn] ";
		logger.logInfo(wInfo + INIT_TASK);
		IProcedureResponse response = new ProcedureResponseAS();

		IProcedureRequest requestTransfer = this.getRequestTransfer(anOriginalRequest);

		if (logger.isDebugEnabled()) {
			logger.logDebug(wInfo + "Request accountTransfer: " + requestTransfer.getProcedureRequestAsString());
		}

		response = executeCoreBanking(requestTransfer);

		if (logger.isDebugEnabled()) {
			logger.logDebug(wInfo + "aBagSPJavaOrchestration SPEI: " + aBagSPJavaOrchestration.toString());
			logger.logDebug(wInfo + "response de central: " + response);
		}

		logger.logInfo(wInfo + END_TASK);

		return response;

	}

	private IProcedureRequest getRequestTransfer(IProcedureRequest anOriginalRequest) {
		String wInfo = CLASS_NAME + "[getRequestTransfer] ";
		logger.logInfo(wInfo + INIT_TASK);
		IProcedureRequest procedureRequest = initProcedureRequest(anOriginalRequest);
		procedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500069");
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		procedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, COBIS_CONTEXT);
		procedureRequest.addFieldInHeader(KEEP_SSN, ICOBISTS.HEADER_STRING_TYPE, "Y");
		boolean isReentryExecution = "Y".equals(anOriginalRequest.readValueFieldInHeader(REENTRY_EXE));

		procedureRequest.setSpName("cob_ahorros..sp_ah_spei_entrante");
		procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "253");
		procedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "253");
		procedureRequest.addInputParam("@i_cta", ICTSTypes.SYBVARCHAR,
				anOriginalRequest.readValueParam("@i_cuentaBeneficiario"));
		procedureRequest.addInputParam("@i_val", ICTSTypes.SYBMONEY, anOriginalRequest.readValueParam("@i_monto"));
		procedureRequest.addInputParam("@i_causa", ICTSTypes.SYBVARCHAR, "249");
		procedureRequest.addInputParam("@i_causa_comi", ICTSTypes.SYBVARCHAR, "250");
		procedureRequest.addInputParam("@i_mon", ICTSTypes.SYBINT4, "0");
		procedureRequest.addInputParam("@i_fecha", ICTSTypes.SYBDATETIME,
				anOriginalRequest.readValueParam("@i_fechaOperacion"));
		procedureRequest.addInputParam("@i_canal", ICTSTypes.SYBINT4, "9");

		procedureRequest.addInputParam("@i_cuenta_beneficiario", ICTSTypes.SQLVARCHAR,
				anOriginalRequest.readValueParam("@i_cuentaBeneficiario"));
		procedureRequest.addInputParam("@i_cuenta_ordenante", ICTSTypes.SQLVARCHAR,
				anOriginalRequest.readValueParam("@i_cuentaOrdenante"));
		procedureRequest.addInputParam("@i_concepto_pago", ICTSTypes.SQLVARCHAR,
				anOriginalRequest.readValueParam("@i_conceptoPago"));
		procedureRequest.addInputParam("@i_monto", ICTSTypes.SQLMONEY4, anOriginalRequest.readValueParam("@i_monto"));
		procedureRequest.addInputParam("@i_institucion_ordenante", ICTSTypes.SYBINT4,
				anOriginalRequest.readValueParam("@i_institucionOrdenante"));
		procedureRequest.addInputParam("@i_institucion_beneficiaria", ICTSTypes.SYBINT4,
				anOriginalRequest.readValueParam("@i_institucionBeneficiaria"));
		procedureRequest.addInputParam("@i_id_spei", ICTSTypes.SQLVARCHAR,
				anOriginalRequest.readValueParam("@i_idSpei"));
		procedureRequest.addInputParam("@i_clave_rastreo", ICTSTypes.SQLVARCHAR,
				anOriginalRequest.readValueParam("@i_claveRastreo"));
		procedureRequest.addInputParam("@i_nombre_ordenante", ICTSTypes.SQLVARCHAR,
				anOriginalRequest.readValueParam("@i_nombreOrdenante"));
		procedureRequest.addInputParam("@i_rfc_curp_ordenante", ICTSTypes.SQLVARCHAR,
				anOriginalRequest.readValueParam("@i_rfcCurpOrdenante"));
		procedureRequest.addInputParam("@i_referencia_numerica", ICTSTypes.SQLVARCHAR,
				anOriginalRequest.readValueParam("@i_referenciaNumerica"));
		procedureRequest.addInputParam("@i_tipo", ICTSTypes.SYBINT4, anOriginalRequest.readValueParam("@i_idTipoPago"));
		procedureRequest.addInputParam("@i_cta", ICTSTypes.SQLVARCHAR,
				anOriginalRequest.readValueParam("@i_cuenta_cobis"));
		procedureRequest.addInputParam("@i_operacion", ICTSTypes.SYBCHAR, "I");
		procedureRequest.addInputParam("@i_xml_request", ICTSTypes.SQLVARCHAR,
				anOriginalRequest.readValueParam("@i_string_request"));
		procedureRequest.addInputParam("@i_tipo_ejecucion", ICTSTypes.SQLVARCHAR, isReentryExecution ? "F" : "L");

		procedureRequest.addOutputParam("@o_id_interno", ICTSTypes.SQLINT4, "");
		procedureRequest.addOutputParam("@o_nombre_beneficiario", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_rfc_curp_beneficiario", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_descripcion_error", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_resultado_error", ICTSTypes.SQLINT4, "");
		procedureRequest.addOutputParam("@o_id_causa_devolucion", ICTSTypes.SQLVARCHAR, "");
		procedureRequest.addOutputParam("@o_descripcion", ICTSTypes.SQLVARCHAR, "");

		logger.logInfo(wInfo + END_TASK);

		return procedureRequest;
	}

	private void notifySpei(IProcedureRequest anOriginalRequest, java.util.Map map) {

		try {
			ServerResponse serverResponse = (ServerResponse) map.get(RESPONSE_SERVER);

			// Por definicion funcional no se notifica en modo offline
			if (Boolean.FALSE.equals(serverResponse.getOnLine())) {
				return;
			}

			logger.logInfo(CLASS_NAME + "Enviando notificacion spei");

			IProcedureRequest procedureRequest = initProcedureRequest(anOriginalRequest);

			String cuentaClave = anOriginalRequest.readValueParam("@i_cuenta_beneficiario");

			logger.logInfo(CLASS_NAME + "using clabe account account " + cuentaClave);

			procedureRequest.setSpName("cob_bvirtual..sp_bv_enviar_notif_ib");
			procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "local");
			procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "1800195");

			procedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "1800195");
			procedureRequest.addInputParam("@i_servicio", ICTSTypes.SQLINT1, "8");
			// procedureRequest.addInputParam("@i_num_producto", Types.VARCHAR, "");
			procedureRequest.addInputParam("@i_tipo_mensaje", ICTSTypes.SQLCHAR, "F");
			procedureRequest.addInputParam("@i_notificacion", ICTSTypes.SYBVARCHAR, "N145");
			procedureRequest.addInputParam("@i_tipo", ICTSTypes.SQLVARCHAR, "I");
			procedureRequest.addInputParam("@i_producto", ICTSTypes.SQLINT1, "18");
			procedureRequest.addInputParam("@i_transaccion_id", ICTSTypes.SQLINT1, "0");
			procedureRequest.addInputParam("@i_canal", ICTSTypes.SQLINT1, "8");
			procedureRequest.addInputParam("@i_origen", ICTSTypes.SQLVARCHAR, "spei");
			procedureRequest.addInputParam("@i_clabe", ICTSTypes.SQLVARCHAR, cuentaClave);
			procedureRequest.addInputParam("@i_s", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@i_referenciaNumerica"));
			procedureRequest.addInputParam("@i_c1", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@i_cuentaOrdenante"));
			procedureRequest.addInputParam("@i_c2", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@i_cuentaBeneficiario"));
			procedureRequest.addInputParam("@i_v2", ICTSTypes.SQLVARCHAR,
					String.valueOf(anOriginalRequest.readValueParam("@i_monto")));
			procedureRequest.addInputParam("@i_r", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@i_conceptoPago"));
			procedureRequest.addInputParam("@i_aux9", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@i_claveRastreo"));
			procedureRequest.addInputParam("@i_aux8", ICTSTypes.SQLVARCHAR,
					anOriginalRequest.readValueParam("@i_nombreOrdenante"));

			IProcedureResponse procedureResponseLocal = executeCoreBanking(procedureRequest);

			logger.logInfo("jcos proceso de notificaciom terminado");

		} catch (Exception xe) {
			logger.logError("Error en la notficación de spei recibida", xe);
		}
	}

	private void logDebug(Object aMessage) {
		if (logger.isDebugEnabled()) {
			logger.logDebug(aMessage);
		}
	}





	@Override
	protected IProcedureResponse executeTransaction(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected void executeCreditTransferOrchest(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		
		
		
		
		
	}

	@Override
	protected Boolean doSignature(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub
	
		Boolean isValid=false;		
	
		
        DispatcherUtil util=new DispatcherUtil();
        String sign=  util.doSignature(request, aBagSPJavaOrchestration);
		
		
		return isValid;
		
	}
	


	@Override
	protected Object chargesSettled(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		
		//select ts_estado,* from cob_bvirtual..bv_transfer_spei order by ts_fecha_real desc
		//cambiar por el estado por A
		
		
		Mensaje msg = new Mensaje();
		Respuesta responseXml = new Respuesta();
		mensaje msjIn = (mensaje) aBagSPJavaOrchestration.get("speiTransaction");
		String response;
		if(logger.isDebugEnabled())
			logger.logInfo("BER Id:"+msjIn.getOrdenpago().getId());
		if( validateFields(aBagSPJavaOrchestration))
		{
			//llamar sp cambio de estado transfer spei 
			IProcedureRequest procedureRequest = initProcedureRequest(request);

			procedureRequest.setSpName("cob_bvirtual..sp_act_transfer_spei");
			procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID,  ICOBISTS.HEADER_STRING_TYPE,
					IMultiBackEndResolverService.TARGET_LOCAL);
			procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500161");
			procedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500161");
			procedureRequest.addInputParam("@i_operacion", ICTSTypes.SYBVARCHAR, "A");			
			procedureRequest.addInputParam("@i_clave_rastreo", ICTSTypes.SYBVARCHAR, msjIn.getOrdenpago().getOpCveRastreo());
			
			IProcedureResponse procedureResponseLocal = executeCoreBanking(procedureRequest);
			
			if(procedureResponseLocal.getReturnCode()!=0)
			{
				aBagSPJavaOrchestration.put("validateCode", procedureResponseLocal.getReturnCode());
				aBagSPJavaOrchestration.put("messajeCode", "Error en la actualizacion de la transferencia spei");
			}else
			{
				aBagSPJavaOrchestration.put("validateCode", 0);
				aBagSPJavaOrchestration.put("messajeCode", "");
			}
			
		}
		
		responseXml.setErrCodigo(Integer.valueOf( String.valueOf( aBagSPJavaOrchestration.get("validateCode"))));
		responseXml.setErrDescripcion(String.valueOf( aBagSPJavaOrchestration.get("messajeCode")));
		responseXml.setFechaOper(msjIn.getOrdenpago().getOpFechaOper());
		responseXml.setId(msjIn.getOrdenpago().getId());
		msg.setCategoria(Constans.ODPS_LIQUIDADAS_CARGOS_RESPUESTA);
		msg.setRespuesta(responseXml);
		
		response = toStringXmlObject( msg);  
		aBagSPJavaOrchestration.put("result", response);
		return response;
	}

	@Override
	protected Object paymentIn(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration)
	{
		// TODO validar firma
		Mensaje msg = new Mensaje();
		Respuesta responseXml = new Respuesta();
		mensaje msjIn = (mensaje) aBagSPJavaOrchestration.get("speiTransaction");
		String response;
		if(logger.isDebugEnabled())
			logger.logInfo("BER Id:"+msjIn.getOrdenpago().getId());
		if( validateFields(aBagSPJavaOrchestration))
		{
			//llamar sp cambio de estado transfer spei 
			IProcedureRequest procedureRequest = initProcedureRequest(request);

			procedureRequest.setSpName("cob_procesador..sp_bv_spei_transaction");
			procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500069");
			procedureRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500069");
			request.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "18500069");
			request.addFieldInHeader("trn_origen", ICOBISTS.HEADER_STRING_TYPE, "API_IN");
			request.addFieldInHeader("target", ICOBISTS.HEADER_STRING_TYPE, "SPExecutor");
			request.addFieldInHeader(ICOBISTS.HEADER_MESSAGE_TYPE, ICOBISTS.HEADER_STRING_TYPE, "ProcedureRequest");
			request.addFieldInHeader("com.cobiscorp.cobis.csp.services.IOrchestrator", ICOBISTS.HEADER_STRING_TYPE,
					"(service.identifier=SpeiInTransferOrchestrationCore)");
			request.addFieldInHeader("serviceMethodName", ICOBISTS.HEADER_STRING_TYPE, "executeTransaction");
			procedureRequest.addInputParam("@i_cuentaBeneficiario", ICTSTypes.SYBVARCHAR, msjIn.getOrdenpago().getOpCuentaBen());
			procedureRequest.addInputParam("@i_monto", ICTSTypes.SYBMONEY, msjIn.getOrdenpago().getOpMonto().toString());
			procedureRequest.addInputParam("@i_fechaOperacion", ICTSTypes.SYBDATETIME, msjIn.getOrdenpago().getOpFechaOper());
			procedureRequest.addInputParam("@i_cuentaOrdenante", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpCuentaOrd());
			procedureRequest.addInputParam("@i_conceptoPago", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpConceptoPag2());
			procedureRequest.addInputParam("@i_institucionOrdenante", ICTSTypes.SYBINT4, String.valueOf(msjIn.getOrdenpago().getOpInsClave()));
			procedureRequest.addInputParam("@i_institucionBeneficiaria", ICTSTypes.SYBINT4, getParam(request, "CBCCDK", "AHO"));
			procedureRequest.addInputParam("@i_idSpei", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getId());
			procedureRequest.addInputParam("@i_claveRastreo", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpCveRastreo());
			procedureRequest.addInputParam("@i_nombreOrdenante", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpNomOrd());
			procedureRequest.addInputParam("@i_rfcCurpOrdenante", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpRfcCurpOrd());
			procedureRequest.addInputParam("@i_referenciaNumerica", ICTSTypes.SQLVARCHAR, String.valueOf(msjIn.getOrdenpago().getOpRefNumerica()));
			procedureRequest.addInputParam("@i_idTipoPago", ICTSTypes.SYBINT4,String.valueOf(msjIn.getOrdenpago().getOpTpClave()));
			procedureRequest.addInputParam("@i_string_request", ICTSTypes.SQLVARCHAR, toStringXmlObject(msjIn));

			procedureRequest.addOutputParam("@o_id_interno", ICTSTypes.SQLINT4, "");
			procedureRequest.addOutputParam("@o_nombre_beneficiario", ICTSTypes.SQLVARCHAR, "");
			procedureRequest.addOutputParam("@o_rfc_curp_beneficiario", ICTSTypes.SQLVARCHAR, "");
			procedureRequest.addOutputParam("@o_descripcion_error", ICTSTypes.SQLVARCHAR, "");
			procedureRequest.addOutputParam("@o_resultado_error", ICTSTypes.SQLINT4, "");
			procedureRequest.addOutputParam("@o_id_causa_devolucion", ICTSTypes.SQLVARCHAR, "");
			procedureRequest.addOutputParam("@o_descripcion", ICTSTypes.SQLVARCHAR, "");
			
			
			procedureRequest.addInputParam("@i_operacion", ICTSTypes.SYBVARCHAR, "A");			
			procedureRequest.addInputParam("@i_clave_rastreo", ICTSTypes.SYBVARCHAR, msjIn.getOrdenpago().getOpCveRastreo());
			
			IProcedureResponse procedureResponseLocal = executeCoreBanking(procedureRequest);
			
			if(logger.isDebugEnabled())
				logger.logInfo("Response tranfer spei in: "+procedureResponseLocal.getProcedureResponseAsString());
			
			responseXml.setErrCodigo(Integer.parseInt(procedureResponseLocal.readValueParam("@o_id_causa_devolucion")));
			responseXml.setErrDescripcion(procedureResponseLocal.readValueParam("@o_descripcion"));
		} else {
			responseXml.setErrCodigo(Integer.valueOf( String.valueOf( aBagSPJavaOrchestration.get("validateCode"))));
			responseXml.setErrDescripcion(String.valueOf( aBagSPJavaOrchestration.get("messajeCode")));
		}
		
		responseXml.setFechaOper(msjIn.getOrdenpago().getOpFechaOper());
		responseXml.setId(msjIn.getOrdenpago().getId());
		msg.setCategoria(Constans.ODPS_LIQUIDADAS_ABONOS_RESPUESTA);
		msg.setRespuesta(responseXml);
		
		response = toStringXmlObject( msg);  
		aBagSPJavaOrchestration.put("result", response);
		return response;
	}
	
	private String getParam(IProcedureRequest anOriginalRequest, String nemonico, String producto) {
    	logger.logDebug("Begin flow, getOperatingInstitutionFromParameters");
		
		IProcedureRequest reqTMPCentral = (initProcedureRequest(anOriginalRequest));		
		reqTMPCentral.setSpName("cobis..sp_parametro");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, 'S', "central");
		reqTMPCentral.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "Q");
		reqTMPCentral.addInputParam("@i_nemonico",ICTSTypes.SQLVARCHAR, nemonico);
		reqTMPCentral.addInputParam("@i_producto",ICTSTypes.SQLVARCHAR, producto);	 
	    reqTMPCentral.addInputParam("@i_modo",ICTSTypes.SQLINT4, "4");

	    IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);
		
		if (logger.isInfoEnabled()) {
			logger.logDebug("Ending flow, getOperatingInstitutionFromParameters with wProcedureResponseCentral: " + wProcedureResponseCentral.getProcedureResponseAsString());
		}
		
		if (!wProcedureResponseCentral.hasError()) {
			
			if (wProcedureResponseCentral.getResultSetListSize() > 0) {
				IResultSetRow[] resultSetRows = wProcedureResponseCentral.getResultSet(1).getData().getRowsAsArray();
				
				if (resultSetRows.length > 0) {
					IResultSetRowColumnData[] columns = resultSetRows[0].getColumnsAsArray();
					return columns[2].getValue();
				} 
			} 
		} 
		
		return "";
	}
	
	private String toStringXmlObject(Object obj)
	{
		StringWriter wOrdenPago = new StringWriter();
		JAXB.marshal(obj, wOrdenPago);
		String xmlOrdenPago = wOrdenPago.toString();
		xmlOrdenPago = xmlOrdenPago.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "");
		return xmlOrdenPago;
	}
	
	private Boolean validateFields(Map<String, Object> aBagSPJavaOrchestration)
	{
		
		mensaje msjIn = (mensaje) aBagSPJavaOrchestration.get("speiTransaction");
		Boolean validate = true; 
		
		if("ODPS_LIQUIDADAS_CARGOS".equals(msjIn.getCategoria()))
		{
			if(msjIn.getOrdenpago().getOpFechaOper()==null)
			{
				aBagSPJavaOrchestration.put("validateCode", 93);
				aBagSPJavaOrchestration.put("messajeCode", "La fecha de operación  es obligatoria");
				validate = false;
			}else
			if(Integer.valueOf(msjIn.getOrdenpago().getOpFolio())==null)
			{
				aBagSPJavaOrchestration.put("validateCode", 445);
				aBagSPJavaOrchestration.put("messajeCode", "El Folio CoDi es obligatorio");
				validate = false;
			}else
			if(Integer.valueOf(msjIn.getOrdenpago().getOpInsClave())==null)
			{
				aBagSPJavaOrchestration.put("validateCode", 5);
				aBagSPJavaOrchestration.put("messajeCode", "La clave de institución  ordenante es obligatoria para este Tipo de Pago");
				validate = false; 
			}else
			if(Integer.valueOf(msjIn.getOrdenpago().getOpTpClave())==null)
			{
				aBagSPJavaOrchestration.put("validateCode", 81);
				aBagSPJavaOrchestration.put("messajeCode", "El tipo de operación  es obligatorio para este Tipo de Pago");
				validate = false;
			}else
			if(msjIn.getOrdenpago().getOpCveRastreo()==null)
			{
				aBagSPJavaOrchestration.put("validateCode", 92);
				aBagSPJavaOrchestration.put("messajeCode", "La clave de rastreo es obligatoria");
				validate = false;
			}else
			if(msjIn.getOrdenpago().getOpEstado()==null)
			{
				aBagSPJavaOrchestration.put("validateCode", 98);
				aBagSPJavaOrchestration.put("messajeCode", "El Estado del envío  es obligatorio");
				validate = false;
			}else
			if(msjIn.getOrdenpago().getOpTipoOrden()==null)
			{
				aBagSPJavaOrchestration.put("validateCode", 106);
				aBagSPJavaOrchestration.put("messajeCode", "Tipo de orden es requerido.");
				validate = false; 
			}else
			if(Integer.valueOf(msjIn.getOrdenpago().getOpPrioridad())==null)
			{
				aBagSPJavaOrchestration.put("validateCode", 85);
				aBagSPJavaOrchestration.put("messajeCode", "La prioridad de la orden es un dato obligatorio");
				validate = false;	 
			}else
			if(Integer.valueOf(msjIn.getOrdenpago().getOpMeClave())==null)
			{
				aBagSPJavaOrchestration.put("validateCode", 93);
				aBagSPJavaOrchestration.put("messajeCode", "La fecha de operación  es obligatoria");
				validate = false;	 
			}if(msjIn.getOrdenpago().getOpTopologia()==null)
			{
				aBagSPJavaOrchestration.put("validateCode", 87);
				aBagSPJavaOrchestration.put("messajeCode", "La Topología  de la orden es obligatorio");
				validate = false;	 
			}else
			if(msjIn.getOrdenpago().getOpUsuClave()==null)
			{
				aBagSPJavaOrchestration.put("validateCode", 158);
				aBagSPJavaOrchestration.put("messajeCode", "La clave del banco usuario es obligatoria para este Tipo de Pago");
				validate = false;	 
			}
		}else
			if("ODPS_LIQUIDADAS_ABONOS".equals(msjIn.getCategoria()))
			{

				if(msjIn.getOrdenpago().getOpFechaOper()==null)
				{
					aBagSPJavaOrchestration.put("validateCode", 93);
					aBagSPJavaOrchestration.put("messajeCode", "La fecha de operación  es obligatoria");
					validate = false;
				}else
				if(Integer.valueOf(msjIn.getOrdenpago().getOpFolio())==null)
				{
					aBagSPJavaOrchestration.put("validateCode", 445);
					aBagSPJavaOrchestration.put("messajeCode", "El Folio CoDi es obligatorio");
					validate = false;
				}else
				if(Integer.valueOf(msjIn.getOrdenpago().getOpInsClave())==null)
				{
					aBagSPJavaOrchestration.put("validateCode", 5);
					aBagSPJavaOrchestration.put("messajeCode", "La clave de institución  ordenante es obligatoria para este Tipo de Pago");
					validate = false; 
				}else
				if(msjIn.getOrdenpago().getOpMonto()==null)
				{
					aBagSPJavaOrchestration.put("validateCode", 9);
					aBagSPJavaOrchestration.put("messajeCode", "El Monto es obligatorio");
					validate = false;
				}else
				if(Integer.valueOf(msjIn.getOrdenpago().getOpTpClave())==null)
				{
					aBagSPJavaOrchestration.put("validateCode", 57);
					aBagSPJavaOrchestration.put("messajeCode", "La clave del pago es obligatorio para este Tipo de Pago");
					validate = false;
				}else
				if(msjIn.getOrdenpago().getOpCveRastreo()==null)
				{
					aBagSPJavaOrchestration.put("validateCode", 92);
					aBagSPJavaOrchestration.put("messajeCode", "La clave de rastreo es obligatoria");
					validate = false;
				}else
				if(msjIn.getOrdenpago().getOpEstado()==null)
				{
					aBagSPJavaOrchestration.put("validateCode", 98);
					aBagSPJavaOrchestration.put("messajeCode", "El Estado del envío  es obligatorio");
					validate = false;
				}else
				if(msjIn.getOrdenpago().getOpTipoOrden()==null)
				{
					aBagSPJavaOrchestration.put("validateCode", 106);
					aBagSPJavaOrchestration.put("messajeCode", "Tipo de orden es requerido.");
					validate = false; 
				}else
				if(Integer.valueOf(msjIn.getOrdenpago().getOpPrioridad())==null)
				{
					aBagSPJavaOrchestration.put("validateCode", 85);
					aBagSPJavaOrchestration.put("messajeCode", "La prioridad de la orden es un dato obligatorio");
					validate = false;	 
				}else
				if(Integer.valueOf(msjIn.getOrdenpago().getOpMeClave())==null)
				{
					aBagSPJavaOrchestration.put("validateCode", 93);
					aBagSPJavaOrchestration.put("messajeCode", "La fecha de operación  es obligatoria");
					validate = false;	 
				}if(msjIn.getOrdenpago().getOpTopologia()==null)
				{
					aBagSPJavaOrchestration.put("validateCode", 87);
					aBagSPJavaOrchestration.put("messajeCode", "La Topología  de la orden es obligatorio");
					validate = false;	 
				}else
				if(msjIn.getOrdenpago().getOpUsuClave()==null)
				{
					aBagSPJavaOrchestration.put("validateCode", 158);
					aBagSPJavaOrchestration.put("messajeCode", "La clave del banco usuario es obligatoria para este Tipo de Pago");
					validate = false;	 
				}
			}
		
		return validate;
	}

}
