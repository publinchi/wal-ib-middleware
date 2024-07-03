package com.cobiscorp.ecobis.orchestration.core.ib.dispacher.spei;

import static com.cobiscorp.cobis.cts.domains.ICOBISTS.COBIS_HOME;

import java.io.File;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.regex.Pattern;

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
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
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
import com.cobiscorp.cobis.csp.services.IProvider;

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
            aBagSPJavaOrchestration.put("jksurl", pathCertificado);
        }else
        {
        	if (logger.isDebugEnabled())
    		{
    			logger.logDebug("No existe archivo jks:"+pathCertificado);
    		}
        }
        if (logger.isDebugEnabled())
		{
			logger.logDebug("jksAlgncon:"+pathAlgnJks);
			logger.logDebug("jksurl:"+pathCertificado);
		}
        
		aBagSPJavaOrchestration.put(TRANSFER_NAME, "SPEI DISPACHER");
		mensaje message = null;

		try {
			// METODO GUARDAR XML
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

		IProcedureResponse valuesOutput = new ProcedureResponseAS();
		valuesOutput.addParam("@o_result", 39, 1, aBagSPJavaOrchestration.get("result").toString());

		return valuesOutput;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> aBagSPJavaOrchestration) {
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

	private void logDebug(String msjlog) {
		if (logger.isDebugEnabled()) {
			logger.logDebug(msjlog);
		}
	}

	@Override
	protected IProcedureResponse executeTransaction(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
		return null;
	}
	
	protected void executeCreditTransferOrchest(IProcedureRequest request,
			Map<String, Object> aBagSPJavaOrchestration) {
	}

	@Override
	protected Boolean doSignature(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		return false;
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
		if( validateFields(request,aBagSPJavaOrchestration))
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
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, procedureResponseLocal.getReturnCode());
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "Error en la actualizacion de la transferencia spei");
			}else
			{
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 0);
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "");
			}
			
		}
		
		responseXml.setErrCodigo(Integer.valueOf( String.valueOf( aBagSPJavaOrchestration.get(Constans.VALIDATE_CODE))));
		responseXml.setErrDescripcion(String.valueOf( aBagSPJavaOrchestration.get(Constans.MESSAJE_CODE)));
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
		String paramInsBen = getParam(request, "CBCCDK", "AHO");
		String codTarDeb = getParam(request, "CODTAR", "BVI");
		aBagSPJavaOrchestration.put("codTarDeb", codTarDeb);
		aBagSPJavaOrchestration.put("paramInsBen", paramInsBen);
		if(logger.isDebugEnabled())
			logger.logInfo("BER Id:"+msjIn.getOrdenpago().getId());
		
		DispatcherUtil util = new DispatcherUtil();
		String sign = util.doSignature(request, aBagSPJavaOrchestration);
		
		if( validateFields(request, aBagSPJavaOrchestration))
		{
			if(logger.isDebugEnabled())
			{
				logger.logDebug("firma armada:"+sign);
				logger.logDebug("firma request:"+msjIn.getOrdenpago().getOpFirmaDig());
			}
			if(!sign.equals(msjIn.getOrdenpago().getOpFirmaDig()))
			{
				responseXml.setErrCodigo(Integer.valueOf(8));
				responseXml.setErrDescripcion("La firma del mensaje no es correcta");
			}else
			{	//llamar sp cambio de estado transfer spei 
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
				procedureRequest.addInputParam("@i_institucionBeneficiaria", ICTSTypes.SYBINT4, paramInsBen);
				procedureRequest.addInputParam("@i_idSpei", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getId());
				procedureRequest.addInputParam("@i_claveRastreo", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpCveRastreo());
				procedureRequest.addInputParam("@i_nombreOrdenante", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpNomOrd());
				procedureRequest.addInputParam("@i_rfcCurpOrdenante", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpRfcCurpOrd());
				procedureRequest.addInputParam("@i_referenciaNumerica", ICTSTypes.SQLVARCHAR, String.valueOf(msjIn.getOrdenpago().getOpRefNumerica()));
				procedureRequest.addInputParam("@i_idTipoPago", ICTSTypes.SYBINT4,String.valueOf(msjIn.getOrdenpago().getOpTpClave()));
				procedureRequest.addInputParam("@i_string_request", ICTSTypes.SQLVARCHAR, toStringXmlObject(msjIn));
				procedureRequest.addInputParam("@i_tipoCuentaBeneficiario", ICTSTypes.SQLVARCHAR, String.valueOf(msjIn.getOrdenpago().getOpTcClaveBen()));
				procedureRequest.addOutputParam("@o_id_interno", ICTSTypes.SQLINT4, "");
				procedureRequest.addOutputParam("@o_nombre_beneficiario", ICTSTypes.SQLVARCHAR, "");
				procedureRequest.addOutputParam("@o_rfc_curp_beneficiario", ICTSTypes.SQLVARCHAR, "");
				procedureRequest.addOutputParam("@o_descripcion_error", ICTSTypes.SQLVARCHAR, "");
				procedureRequest.addOutputParam("@o_resultado_error", ICTSTypes.SQLINT4, "");
				procedureRequest.addOutputParam("@o_id_causa_devolucion", ICTSTypes.SQLVARCHAR, "-1");
				procedureRequest.addOutputParam("@o_descripcion", ICTSTypes.SQLVARCHAR, "Error desconocido");
				
				
				procedureRequest.addInputParam("@i_operacion", ICTSTypes.SYBVARCHAR, "A");			
				procedureRequest.addInputParam("@i_clave_rastreo", ICTSTypes.SYBVARCHAR, msjIn.getOrdenpago().getOpCveRastreo());
				
				IProcedureResponse procedureResponseLocal = executeCoreBanking(procedureRequest);
				
				if(logger.isDebugEnabled())
					logger.logInfo("Response tranfer spei in: "+procedureResponseLocal.getProcedureResponseAsString());
				
				if(procedureResponseLocal.readValueParam("@o_id_causa_devolucion")!=null && Integer.parseInt(procedureResponseLocal.readValueParam("@o_id_causa_devolucion"))==0)
				{
					//falta implementar las tablas de auditoria y generacion de secuenciales
					IProcedureResponse responseCda = getWsEsice(request, aBagSPJavaOrchestration);
					if(responseCda.getReturnCode()!=0)
					{
						if(logger.isDebugEnabled()) {
							logger.logInfo("CDA mensaje: "+responseCda.readValueParam("@o_msj_respuesta"));
							logger.logInfo("CDA respuesta: "+responseCda.readValueParam("@o_cod_respuesta"));
						}
						
					}
				}
					
				responseXml.setErrCodigo(Integer.parseInt(procedureResponseLocal.readValueParam("@o_id_causa_devolucion")));
				responseXml.setErrDescripcion(procedureResponseLocal.readValueParam("@o_descripcion"));
			}
		} else {
			responseXml.setErrCodigo(Integer.valueOf( String.valueOf( aBagSPJavaOrchestration.get(Constans.VALIDATE_CODE))));
			responseXml.setErrDescripcion(String.valueOf( aBagSPJavaOrchestration.get(Constans.MESSAJE_CODE)));
		}
		
		responseXml.setFechaOper(msjIn.getOrdenpago().getOpFechaOper());
		responseXml.setId(msjIn.getOrdenpago().getId());
		msg.setCategoria(Constans.ODPS_LIQUIDADAS_ABONOS_RESPUESTA);
		msg.setRespuesta(responseXml);
		
		if(responseXml.getErrCodigo() != 0)
		{
			//llamado a devolucion de un abono no acreditado
			IProcedureResponse responsePaymentReturn = callPaymentInReturn(request, aBagSPJavaOrchestration, msjIn);
			if(logger.isDebugEnabled()) {
				logger.logInfo("responsePaymentReturn: "+responsePaymentReturn.getProcedureResponseAsString());
			}
		}
		
		response = toStringXmlObject(msg);  
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
	
	private boolean validateAccountType(IProcedureRequest anOriginalRequest, String accountType, Map<String, Object> aBagSPJavaOrchestration) 
	{
		if (logger.isDebugEnabled()) 
		{
			logger.logDebug("Begin validateAccountType");
		}
		boolean validate = true ;
		IProcedureRequest reqTMPCentral = (initProcedureRequest(anOriginalRequest));		
		reqTMPCentral.setSpName("cob_bvirtual..sp_valida_tipo_destino");
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID,  ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_CENTRAL);
		reqTMPCentral.addFieldInHeader(ICOBISTS.HEADER_TRN, 'N', "18500163");
		reqTMPCentral.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500163");
		reqTMPCentral.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "V");
		reqTMPCentral.addInputParam("@i_tipo_destino", ICTSTypes.SQLVARCHAR, accountType);

	    IProcedureResponse wProcedureResponseCentral = executeCoreBanking(reqTMPCentral);
		
		if (logger.isDebugEnabled()) 
		{
			logger.logDebug("Ending flow, validateAccountType with wProcedureResponseCentral: " + wProcedureResponseCentral.getProcedureResponseAsString());
		}
		
		if (wProcedureResponseCentral.hasError()) {
			validate = false;
		}else
		{
			if (wProcedureResponseCentral.getResultSetListSize() > 0) {
			
				IResultSetRow[] resultSetRows = wProcedureResponseCentral.getResultSet(1).getData().getRowsAsArray();
				
				if (resultSetRows.length > 0) {
					IResultSetRowColumnData[] columns = resultSetRows[0].getColumnsAsArray();
					aBagSPJavaOrchestration.put("tipoDestino", columns[0].getValue());
				} 
			} 
		}
		
		return validate;
	}
	private String toStringXmlObject(Object obj)
	{
		StringWriter wOrdenPago = new StringWriter();
		JAXB.marshal(obj, wOrdenPago);
		String xmlOrdenPago = wOrdenPago.toString();
		xmlOrdenPago = xmlOrdenPago.replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>", "");
		return xmlOrdenPago;
	}
	
	private Boolean validateFields(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration)
	{
		
		mensaje msjIn = (mensaje) aBagSPJavaOrchestration.get("speiTransaction");
		Boolean validate = true; 
		String opTcClaveBen = String.format("%02d", msjIn.getOrdenpago().getOpTcClaveBen());
		
		if("ODPS_LIQUIDADAS_CARGOS".equals(msjIn.getCategoria()))
		{
			if(msjIn.getOrdenpago().getOpFechaOper()==null)
			{
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 93);
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La fecha de operación  es obligatoria");
				validate = false;
			}else
			if(Integer.valueOf(msjIn.getOrdenpago().getOpFolio())==null)
			{
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 445);
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "El Folio CoDi es obligatorio");
				validate = false;
			}else
			if(Integer.valueOf(msjIn.getOrdenpago().getOpInsClave())==null)
			{
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 5);
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La clave de institución  ordenante es obligatoria para este Tipo de Pago");
				validate = false; 
			}else
			if(Integer.valueOf(msjIn.getOrdenpago().getOpTpClave())==null)
			{
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 81);
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "El tipo de operación  es obligatorio para este Tipo de Pago");
				validate = false;
			}else
			if(msjIn.getOrdenpago().getOpCveRastreo()==null)
			{
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 92);
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La clave de rastreo es obligatoria");
				validate = false;
			}else
			if(msjIn.getOrdenpago().getOpEstado()==null)
			{
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 98);
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "El Estado del envío  es obligatorio");
				validate = false;
			}else
			if(msjIn.getOrdenpago().getOpTipoOrden()==null)
			{
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 106);
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "Tipo de orden es requerido.");
				validate = false; 
			}else
			if(Integer.valueOf(msjIn.getOrdenpago().getOpPrioridad())==null)
			{
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 85);
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La prioridad de la orden es un dato obligatorio");
				validate = false;	 
			}else
			if(Integer.valueOf(msjIn.getOrdenpago().getOpMeClave())==null)
			{
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 93);
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La fecha de operación  es obligatoria");
				validate = false;	 
			}if(msjIn.getOrdenpago().getOpTopologia()==null)
			{
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 87);
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La Topología  de la orden es obligatorio");
				validate = false;	 
			}else
			if(msjIn.getOrdenpago().getOpUsuClave()==null)
			{
				aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 158);
				aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La clave del banco usuario es obligatoria para este Tipo de Pago");
				validate = false;	 
			}
		}else
			if("ODPS_LIQUIDADAS_ABONOS".equals(msjIn.getCategoria()))
			{

				if(msjIn.getOrdenpago().getOpFechaOper()==null)
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 93);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La fecha de operación  es obligatoria");
					validate = false;
				}else
				if(Integer.valueOf(msjIn.getOrdenpago().getOpFolio())==null)
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 445);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "El Folio CoDi es obligatorio");
					validate = false;
				}else
				if(Integer.valueOf(msjIn.getOrdenpago().getOpInsClave())==null)
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 5);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La clave de institución  ordenante es obligatoria para este Tipo de Pago");
					validate = false; 
				}else
				if(msjIn.getOrdenpago().getOpMonto()==null)
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 9);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "El Monto es obligatorio");
					validate = false;
				}else
				if(Integer.valueOf(msjIn.getOrdenpago().getOpTpClave())==null)
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 57);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La clave del pago es obligatorio para este Tipo de Pago");
					validate = false;
				}else
				if(msjIn.getOrdenpago().getOpCveRastreo()==null)
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 92);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La clave de rastreo es obligatoria");
					validate = false;
				}else
				if(msjIn.getOrdenpago().getOpEstado()==null)
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 98);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "El Estado del envío  es obligatorio");
					validate = false;
				}else
				if(msjIn.getOrdenpago().getOpTipoOrden()==null)
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 106);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "Tipo de orden es requerido.");
					validate = false; 
				}else
				if(Integer.valueOf(msjIn.getOrdenpago().getOpPrioridad())==null)
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 85);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La prioridad de la orden es un dato obligatorio");
					validate = false;	 
				}else
				if(Integer.valueOf(msjIn.getOrdenpago().getOpMeClave())==null)
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 93);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La fecha de operación  es obligatoria");
					validate = false;	 
				}if(msjIn.getOrdenpago().getOpTopologia()==null)
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 87);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La Topología  de la orden es obligatorio");
					validate = false;	 
				}else
				if(msjIn.getOrdenpago().getOpUsuClave()==null)
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 158);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La clave del banco usuario es obligatoria para este Tipo de Pago");
					validate = false;	 
				}else
				//valida tipo de cuentas entrantes
				if(!validateAccountType(request, opTcClaveBen, aBagSPJavaOrchestration ))
				{
					aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 400602);
					aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "El tipo de destino no existe en el catalogo [bv_tipo_cuenta_spei].");
					validate = false;	
				}else
				{ 
					if(opTcClaveBen.equals(aBagSPJavaOrchestration.get("codTarDeb")))
					{
						if( !digitValidateNum(msjIn.getOrdenpago().getOpCuentaBen()))
						{
							aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 34);
							aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "La cuenta del beneficiario solo puede ser numérica");
							validate = false;	
						}else
							if(!(msjIn.getOrdenpago().getOpCuentaBen().length()==16))
							{
								aBagSPJavaOrchestration.put(Constans.VALIDATE_CODE, 38);
								aBagSPJavaOrchestration.put(Constans.MESSAJE_CODE, "Para tipo de cuenta Tarjeta de Debito la cuenta del beneficiario debe ser de 16 dígitos.");
								validate = false;	
							}
					}
						
				}

			}
		
		return validate;
	}
	public boolean digitValidateNum(String cadena) 
	{
	    Pattern patron = Pattern.compile("^\\d+$");
	    return patron.matcher(cadena).matches();
    }
	
	private IProcedureResponse getWsEsice(IProcedureRequest anOriginalReq, Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureResponse connectorResponse = null;

		IProcedureRequest anOriginalRequest = anOriginalReq.clone();
		aBagSPJavaOrchestration.remove("trn_virtual");
		mensaje msjIn = (mensaje) aBagSPJavaOrchestration.get("speiTransaction");
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " Entrando en getWsEsice");
		}
		 LocalTime horaActual = LocalTime.now();
        // Formatear la hora en formato hhmmss
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmmss");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("HHmmssSSS");
        String horaHHmmss = horaActual.format(formatter);
        String horaHHmmssSSS = horaActual.format(formatter2);
        
        SimpleDateFormat sdfEntrada = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdfSalida = new SimpleDateFormat("ddMMyyyy");
        Date opFechaOperAux;
        String opFechaOper ="";       
        
		try {

			opFechaOperAux = sdfEntrada.parse(msjIn.getOrdenpago().getOpFechaOper());
			opFechaOper = sdfSalida.format(opFechaOperAux);
			
			anOriginalRequest.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "CDA");
			// etiquetas se maneja dentro de nuestra base de datos OJO cambiar		
			anOriginalRequest.addInputParam("@i_id_cda", ICTSTypes.SQLINT4, msjIn.getOrdenpago().getId());
			anOriginalRequest.addInputParam("@i_id_mensaje", ICTSTypes.SQLINT4, msjIn.getOrdenpago().getId());
			//Atributos	
			anOriginalRequest.addInputParam("@i_op_fecha_oper", ICTSTypes.SQLINT4, opFechaOper);
			anOriginalRequest.addInputParam("@i_op_hora_abono", ICTSTypes.SQLVARCHAR, horaHHmmss);
			anOriginalRequest.addInputParam("@i_op_cve_rastreo", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpCveRastreo());
			anOriginalRequest.addInputParam("@i_op_folio_orig_odp", ICTSTypes.SQLINT4,String.valueOf( msjIn.getOrdenpago().getOpFolio()));
			anOriginalRequest.addInputParam("@i_op_folio_orig_paq", ICTSTypes.SQLINT4,String.valueOf( msjIn.getOrdenpago().getPaqFolioOri()));
			anOriginalRequest.addInputParam("@i_op_clave_emisor", ICTSTypes.SQLINT4, String.valueOf( msjIn.getOrdenpago().getOpInsClave()));
			anOriginalRequest.addInputParam("@i_op_nombre_emisor", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpNomOrd());
			
			anOriginalRequest.addInputParam("@i_op_nom_ord", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpNomOrd());
			anOriginalRequest.addInputParam("@i_op_tp_cta_ord", ICTSTypes.SQLINT4, String.valueOf(msjIn.getOrdenpago().getOpTcClaveOrd()));
			anOriginalRequest.addInputParam("@i_op_cuenta_ord", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpCuentaOrd());
			anOriginalRequest.addInputParam("@i_op_rfc_curp_ord", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpRfcCurpOrd());
			anOriginalRequest.addInputParam("@i_op_nombre_receptor", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpNomBen());
			anOriginalRequest.addInputParam("@i_op_nom_ben", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpNomBen());
			anOriginalRequest.addInputParam("@i_op_tp_cta_ben", ICTSTypes.SQLINT4,String.valueOf( msjIn.getOrdenpago().getOpTcClaveBen()));
			anOriginalRequest.addInputParam("@i_op_cuenta_ben", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpCuentaBen());
			anOriginalRequest.addInputParam("@i_op_rfc_curp_ben", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpRfcCurpOrd());
			anOriginalRequest.addInputParam("@i_op_concepto_pag", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpConceptoPag2());
			anOriginalRequest.addInputParam("@i_op_tipo_pag", ICTSTypes.SQLINT4, String.valueOf( msjIn.getOrdenpago().getOpTpClave()));
			anOriginalRequest.addInputParam("@i_op_iva", ICTSTypes.SQLMONEY, "0");
			anOriginalRequest.addInputParam("@i_op_monto", ICTSTypes.SQLMONEY, String.valueOf( msjIn.getOrdenpago().getOpMonto()));
			anOriginalRequest.addInputParam("@i_op_hora00", ICTSTypes.SQLVARCHAR, horaHHmmssSSS);
			anOriginalRequest.addInputParam("@i_op_fecha_abono", ICTSTypes.SQLVARCHAR, opFechaOper);
			
			// SE HACE LA LLAMADA AL CONECTOR
			
			anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TIMEOUT, ICOBISTS.HEADER_STRING_TYPE, "30000");
			anOriginalRequest.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "18500164");
			anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500164");// 1890018
			
			aBagSPJavaOrchestration.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorESICE)");	
	    
			// SE EJECUTA CONECTOR
			connectorResponse = executeProvider(anOriginalRequest, aBagSPJavaOrchestration);

			if (logger.isDebugEnabled())
				logger.logDebug("getWsEsice response: " + connectorResponse);

		} catch (Exception e) {
			e.printStackTrace();
			aBagSPJavaOrchestration.put("@o_result", "999");
			connectorResponse = null;
			logger.logInfo(CLASS_NAME +" Error Catastrofico de getWsEsice");

		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + "--> getWsEsice");
			}
		}
		return connectorResponse;
	}
	
	private IProcedureResponse callPaymentInReturn(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration, mensaje msjIn) {
		// SE INICIALIZA VARIABLE
		if (logger.isInfoEnabled()) 
		{
			logger.logInfo("Entrando a callPaymentInReturn");
		}
		IProcedureResponse connectorSpeiResponse = null;
		try 
		{
			Integer opInsClave = msjIn.getOrdenpago().getOpInsClave();
			IProcedureRequest procedureRequest = anOriginalRequest.clone();
			aBagSPJavaOrchestration.remove("trn_virtual");
			//SPEI REQUEST DEOVOLUCION KARPAY
			procedureRequest.addInputParam("@i_fecha_operacion", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpFechaOper());
			procedureRequest.addInputParam("@i_institucion_contraparte", ICTSTypes.SQLVARCHAR, opInsClave!=null?opInsClave.toString():"");
			procedureRequest.addInputParam("@i_monto", ICTSTypes.SQLMONEY, String.valueOf(msjIn.getOrdenpago().getOpMonto()));
			procedureRequest.addInputParam("@i_tipo_pago", ICTSTypes.SQLVARCHAR, "0");
			procedureRequest.addInputParam("@i_clave_rastreo_connection", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpCveRastreo()) ;
			
			procedureRequest.addInputParam("@i_estado", ICTSTypes.SQLVARCHAR, "L");
			procedureRequest.addInputParam("@i_tipo_orden", ICTSTypes.SQLVARCHAR,"E");
			procedureRequest.addInputParam("@i_prioridad", ICTSTypes.SQLVARCHAR,"0");
			procedureRequest.addInputParam("@i_op_me_clave", ICTSTypes.SQLVARCHAR,"8");
			procedureRequest.addInputParam("@i_op_topologia", ICTSTypes.SQLVARCHAR,"V");
			procedureRequest.addInputParam("@i_id", ICTSTypes.SQLVARCHAR,msjIn.getOrdenpago().getId());
			procedureRequest.addInputParam("@i_op_firma_dig", ICTSTypes.SQLVARCHAR,msjIn.getOrdenpago().getOpFirmaDig());
			
			procedureRequest.addInputParam("@i_op_cd_clave", ICTSTypes.SQLVARCHAR,"1");
			procedureRequest.addInputParam("@i_categoria", ICTSTypes.SQLVARCHAR, "CARGAR_ODP");	
			procedureRequest.addInputParam("@i_operatingInstitution", ICTSTypes.SQLVARCHAR, getParam(anOriginalRequest, "CBCCDK", "AHO"));
			// SE HACE LA LLAMADA AL CONECTOR
			aBagSPJavaOrchestration.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorSpei)");		

			procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TIMEOUT, ICOBISTS.HEADER_STRING_TYPE, "30000");
			procedureRequest.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "18500115");
			procedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500115");
			


			// SE EJECUTA
			connectorSpeiResponse = executeProvider(procedureRequest, aBagSPJavaOrchestration);
			// SE VALIDA LA RESPUESTA
			if (!connectorSpeiResponse.hasError()) 
			{
				if (logger.isDebugEnabled()) {
					logger.logDebug("success CISConnectorSpei: true");
					logger.logDebug("connectorSpeiResponse: " + connectorSpeiResponse.getParams());
				}

				connectorSpeiResponse.readValueParam("@o_cod_respuesta");
				connectorSpeiResponse.readValueParam("@o_msj_respuesta");
				
				
			} else {

				if (logger.isDebugEnabled()) {
					logger.logDebug("Error Catastrifico respuesta de BANPAY");
					logger.logDebug("Error connectorSpeiResponse Catastrifico: " + connectorSpeiResponse);
				}
			}
		} catch (Exception e) {
			logger.logError(e);
			logger.logInfo("Error Catastrofico de banpayExecution");
			e.printStackTrace();
			logger.logInfo("Error Catastrofico de banpayExecution");

		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo("Saliendo de banpayExecution");
			}
		}
		// SE REGRESA RESPUESTA
		return connectorSpeiResponse;
	}

}
