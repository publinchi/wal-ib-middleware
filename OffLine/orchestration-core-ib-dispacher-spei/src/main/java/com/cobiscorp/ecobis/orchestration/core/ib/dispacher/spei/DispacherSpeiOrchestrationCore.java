package com.cobiscorp.ecobis.orchestration.core.ib.dispacher.spei;

import static com.cobiscorp.cobis.cts.domains.ICOBISTS.COBIS_HOME;

import java.io.File;
import java.io.StringWriter;
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
				procedureRequest.addInputParam("@i_tipo_destino", ICTSTypes.SQLVARCHAR, String.valueOf(msjIn.getOrdenpago().getOpTcClaveBen()));
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

}
