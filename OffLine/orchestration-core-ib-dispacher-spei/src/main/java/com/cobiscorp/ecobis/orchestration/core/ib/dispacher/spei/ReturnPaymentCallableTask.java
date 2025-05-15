package com.cobiscorp.ecobis.orchestration.core.ib.dispacher.spei;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.Callable;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.ecobis.ib.orchestration.base.utils.commons.CardPAN;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.ecobis.ib.orchestration.dtos.mensaje;
import com.cobiscorp.ecobis.orchestration.core.ib.dispacher.dto.Constans;

public class ReturnPaymentCallableTask extends SPJavaOrchestrationBase implements Callable<IProcedureResponse> {
    private IProcedureRequest request;
    private Map<String, Object> aBagSPJavaOrchestration;
	private static ILogger logger = LogFactory.getLogger(ReturnPaymentCallableTask.class);
	private mensaje msjIn;
	String codeBank;
	@Override
	public void loadConfiguration(IConfigurationReader arg0)
	{
				
	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest arg0, Map<String, Object> arg1)
	{
		return null;
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest arg0, Map<String, Object> arg1)
	{
		return null;
	}
	
    // Constructor que acepta parámetros
    public ReturnPaymentCallableTask(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration, mensaje msjIn,  String codeBank) {
        this.request = request;
        this.aBagSPJavaOrchestration = aBagSPJavaOrchestration;
        this.msjIn = msjIn;
        this.codeBank = codeBank;
    }

    @Override
    public IProcedureResponse call() {
        // Llamar a otra función dentro del método call()
    	
        return callPaymentInReturn(request, aBagSPJavaOrchestration, msjIn, codeBank);
    }


    private IProcedureResponse callPaymentInReturn(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration, mensaje msjIn,
                   String codeBank ) {
		// SE INICIALIZA VARIABLE
		if (logger.isDebugEnabled()) 
		{
			logger.logDebug("Entrando a callPaymentInReturn future");
		}
		IProcedureResponse connectorSpeiResponse = null;
		Integer logIDDev = 0;
		//llamada a log entrante
		Integer idLog =	logEntryApi(request, aBagSPJavaOrchestration, "I", "Return Payment in", null, null, null, null, null);
	
		try 
		{
			Integer opInsClave = msjIn.getOrdenpago().getOpInsClave();
			Integer opCdClave = msjIn.getOrdenpago().getOpCdClave();
			IProcedureRequest procedureRequest = anOriginalRequest.clone();
			aBagSPJavaOrchestration.remove("trn_virtual");
			//SPEI REQUEST DEOVOLUCION KARPAY
			procedureRequest.addInputParam("@i_fecha_operacion", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpFechaOper());
			procedureRequest.addInputParam("@i_institucion_contraparte", ICTSTypes.SQLVARCHAR, opInsClave!=null?opInsClave.toString():"");
			procedureRequest.addInputParam("@i_monto", ICTSTypes.SQLMONEY, String.valueOf(msjIn.getOrdenpago().getOpMonto()));
			procedureRequest.addInputParam("@i_tipo_pago", ICTSTypes.SQLVARCHAR, "0");
			procedureRequest.addInputParam("@i_clave_rastreo_connection", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpCveRastreo()) ;
			
			procedureRequest.addInputParam("@i_estado", ICTSTypes.SQLVARCHAR, "A");
			procedureRequest.addInputParam("@i_tipo_orden", ICTSTypes.SQLVARCHAR,"E");
			procedureRequest.addInputParam("@i_prioridad", ICTSTypes.SQLVARCHAR,"0");
			procedureRequest.addInputParam("@i_op_me_clave", ICTSTypes.SQLVARCHAR,"8");
			procedureRequest.addInputParam("@i_op_topologia", ICTSTypes.SQLVARCHAR,"V");
			procedureRequest.addInputParam("@i_id", ICTSTypes.SQLVARCHAR,msjIn.getOrdenpago().getId());
			procedureRequest.addInputParam("@i_op_firma_dig", ICTSTypes.SQLVARCHAR,msjIn.getOrdenpago().getOpFirmaDig());
			procedureRequest.addInputParam("@i_op_cd_clave", ICTSTypes.SQLVARCHAR,opCdClave!=null?opCdClave.toString():"-1");
			procedureRequest.addInputParam("@i_categoria", ICTSTypes.SQLVARCHAR, "CARGAR_ODP");	
			procedureRequest.addInputParam("@i_operatingInstitution", ICTSTypes.SQLVARCHAR,codeBank );
			// SE HACE LA LLAMADA AL CONECTOR
			aBagSPJavaOrchestration.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorSpei)");		

			procedureRequest.addFieldInHeader(ICOBISTS.HEADER_TIMEOUT, ICOBISTS.HEADER_STRING_TYPE, "30000");
			procedureRequest.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "18500115");
			procedureRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500115");
			
			procedureRequest.addOutputParam("@o_spei_response", ICTSTypes.SQLVARCHAR, "X");
			procedureRequest.addOutputParam("@o_spei_response", ICTSTypes.SQLVARCHAR, "X");
			procedureRequest.addOutputParam("@o_cod_respuesta", ICTSTypes.SQLVARCHAR, "0");
			procedureRequest.addOutputParam("@o_msj_respuesta", ICTSTypes.SQLVARCHAR, "");
			// SE EJECUTA
			connectorSpeiResponse = executeProvider(procedureRequest, aBagSPJavaOrchestration);
			// SE VALIDA LA RESPUESTA
			if (!connectorSpeiResponse.hasError()) 
			{
				if (logger.isDebugEnabled()) {
					logger.logDebug("success CISConnectorSpei: true future");
					logger.logDebug("connectorSpeiResponse future: " + connectorSpeiResponse.getParams());
				}
				String responseConnector = connectorSpeiResponse.readValueParam("@o_spei_response");
				String requestConnector = connectorSpeiResponse.readValueParam("@o_spei_request");	
				String returnCodeMsj = connectorSpeiResponse.readValueParam("@o_cod_respuesta")+" - "+connectorSpeiResponse.readValueParam("@o_msj_respuesta");
				//llamada a log update
				String returnCode = connectorSpeiResponse.readValueParam("@o_cod_respuesta");
				logEntryApi(request, aBagSPJavaOrchestration, "U", "Return Payment in", null, returnCodeMsj, responseConnector, idLog, requestConnector);
				registerDevolution(request, aBagSPJavaOrchestration, "I", returnCode!=null?Integer.valueOf(returnCode):-1, connectorSpeiResponse.readValueParam("@o_msj_respuesta"));
				
				//Validamos si se genero el registro de la devolucion
				if(aBagSPJavaOrchestration.get("logIdDev") != null) {
					logIDDev = Integer.parseInt(aBagSPJavaOrchestration.get("logIdDev").toString());
				}
				
				if (logIDDev > 0) {
					//SPEI_RETURN EXITOSO
			        aBagSPJavaOrchestration.put("idLog", idLog);
			        aBagSPJavaOrchestration.put("eventWH", "S");
			        
					registerTransactionWebHook(anOriginalRequest, aBagSPJavaOrchestration, msjIn);
				}
			} else {

				if (logger.isDebugEnabled()) {
					logger.logDebug("Error Catastrifico respuesta de callPaymentInReturn future");
					logger.logDebug("Error connectorSpeiResponse Catastrifico callPaymentInReturn future: " + connectorSpeiResponse);
				}
			}
			
		} catch (Exception e) {
			logger.logError("Error de callPaymentInReturn future",e);
		} finally {
			if (logger.isDebugEnabled()) {
				logger.logDebug("Saliendo de callPaymentInReturn future");
			}
		}
		// SE REGRESA RESPUESTA
		return connectorSpeiResponse;
	}
    private int logEntryApi(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration, 
			String operacion, String tipoEntrada, String firma, String error, String response, Integer id, String request ) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Begin flow, singType");
		}
		Integer logId = 0;
		mensaje msjIn = (mensaje) aBagSPJavaOrchestration.get("speiTransaction");
		CardPAN card= new CardPAN();
		IProcedureRequest requestProcedureLocal = (initProcedureRequest(anOriginalRequest));		
		requestProcedureLocal.setSpName("cob_bvirtual..sp_bv_log_conn_karpay");
		requestProcedureLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, 
				IMultiBackEndResolverService.TARGET_LOCAL);
		
		requestProcedureLocal.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18700121");
		requestProcedureLocal.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, operacion);
		requestProcedureLocal.addInputParam("@i_lc_tipo_entrada",ICTSTypes.SQLVARCHAR, tipoEntrada);
		requestProcedureLocal.addInputParam("@i_lc_categoria",ICTSTypes.SQLVARCHAR, msjIn.getCategoria());
				
		if("I".equals(operacion) && 
			   (Constans.ODPS_LIQUIDADAS_CARGOS.equals( msjIn.getCategoria())|| 
				Constans.ODPS_CANCELADAS_X_BANXICO.equals( msjIn.getCategoria())||
				Constans.ODPS_LIQUIDADAS_ABONOS.equals( msjIn.getCategoria())||
				Constans.ODPS_CANCELADAS_LOCAL.equals( msjIn.getCategoria())))
		{
			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	        
	        // Convierte la fecha de String a LocalDate
	        LocalDate date = LocalDate.parse(msjIn.getOrdenpago().getOpFechaOper(), inputFormatter);
	        
	        // Formatea la fecha a MM/dd/yyyy
	        String processDate = date.format(outputFormatter);
	        
	        //enmascara las tarjetas
	        String opCuentaBen="";
			String opCuentaOrd="";
			
	        if( msjIn.getOrdenpago().getOpTcClaveBen()==3)
	        	opCuentaBen = card.maskNumber(msjIn.getOrdenpago().getOpCuentaBen());
	        else
	        	opCuentaBen = msjIn.getOrdenpago().getOpCuentaBen();
	        
	        if( msjIn.getOrdenpago().getOpTcClaveOrd()==3)
	        	opCuentaOrd = card.maskNumber(msjIn.getOrdenpago().getOpCuentaOrd());
	        else
	        	opCuentaOrd = msjIn.getOrdenpago().getOpCuentaOrd();
			
	        requestProcedureLocal.addInputParam("@i_lc_clave_rastreo",ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpCveRastreo());
			requestProcedureLocal.addInputParam("@i_lc_tipo_pago",ICTSTypes.SQLINT4, String.valueOf( msjIn.getOrdenpago().getOpTpClave()));
			requestProcedureLocal.addInputParam("@i_lc_cuenta_ordenante",ICTSTypes.SQLVARCHAR, opCuentaOrd);
			requestProcedureLocal.addInputParam("@i_lc_institucion_ordenante",ICTSTypes.SQLVARCHAR, String.valueOf( msjIn.getOrdenpago().getOpInsClave()));
			requestProcedureLocal.addInputParam("@i_lc_cuenta_beneficiaria",ICTSTypes.SQLVARCHAR,  opCuentaBen);
			requestProcedureLocal.addInputParam("@i_lc_monto",ICTSTypes.SQLMONEY4,  String.valueOf(msjIn.getOrdenpago().getOpMonto()));
			requestProcedureLocal.addInputParam("@i_lc_firmarequest",ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpFirmaDig());
			requestProcedureLocal.addInputParam("@i_lc_fecha_proceso",ICTSTypes.SQLDATETIME, processDate);
		}else
			if("U".equals(operacion) )
			{
				requestProcedureLocal.addInputParam("@i_lc_firma",ICTSTypes.SQLVARCHAR, firma);
				requestProcedureLocal.addInputParam("@i_lc_error",ICTSTypes.SQLVARCHAR, error);
				requestProcedureLocal.addInputParam("@i_lc_request",ICTSTypes.SQLVARCHAR, request);	
				requestProcedureLocal.addInputParam("@i_lc_response",ICTSTypes.SQLVARCHAR, response);
				requestProcedureLocal.addInputParam("@i_lc_id",ICTSTypes.SQLINT4, id.toString());
			}
		
		requestProcedureLocal.addOutputParam("@o_lc_id", ICTSTypes.SQLINT4, "0");
	        
	    IProcedureResponse wProcedureResponseLocal = executeCoreBanking(requestProcedureLocal);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Ending flow, singType: " + wProcedureResponseLocal.getProcedureResponseAsString());
		}
		
		if (wProcedureResponseLocal.getReturnCode()==0) {
			
			logId = Integer.parseInt(wProcedureResponseLocal.readValueParam("@o_lc_id"));
		} 
		return logId;
		
	}
	private int registerDevolution(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration, 
			 String operacion, Integer error, String errorDes ) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("Begin flow, registerDevolution callable");
		}
		Integer logId = 0;
		mensaje msjIn = (mensaje) aBagSPJavaOrchestration.get("speiTransaction");
		
		Integer opToClave= msjIn.getOrdenpago().getOpToClave();
		Integer opInsClave = msjIn.getOrdenpago().getOpInsClave();
		Integer opCdClave = msjIn.getOrdenpago().getOpCdClave();
		
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        LocalDate date = LocalDate.parse(msjIn.getOrdenpago().getOpFechaOper(), inputFormatter);
        String processDate = date.format(outputFormatter);
		
		IProcedureRequest requestProcedureLocal = (initProcedureRequest(anOriginalRequest));		
		requestProcedureLocal.setSpName("cob_bvirtual..sp_bv_devoluciones");
		requestProcedureLocal.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, 
				IMultiBackEndResolverService.TARGET_LOCAL);
		
		requestProcedureLocal.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18700121");
		requestProcedureLocal.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, operacion);
		requestProcedureLocal.addInputParam("@i_de_clave_rastreo",ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpCveRastreo());
		requestProcedureLocal.addInputParam("@i_de_tipo_pago",ICTSTypes.SQLINT4, opToClave!=null?opToClave.toString():"");
		requestProcedureLocal.addInputParam("@i_de_causa",ICTSTypes.SQLINT4, opCdClave!=null?opCdClave.toString():"");
		requestProcedureLocal.addInputParam("@i_de_estado",ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpEstado());
		requestProcedureLocal.addInputParam("@i_de_inst_benef",ICTSTypes.SQLINT4, opInsClave!=null?opInsClave.toString():"");
		requestProcedureLocal.addInputParam("@i_de_monto",ICTSTypes.SQLMONEY, String.valueOf(msjIn.getOrdenpago().getOpMonto()));
		requestProcedureLocal.addInputParam("@i_de_error",ICTSTypes.SQLINT4, error!=null?error.toString():"-1");
		requestProcedureLocal.addInputParam("@i_de_error_descripcion",ICTSTypes.SQLVARCHAR, errorDes);
		requestProcedureLocal.addInputParam("@i_de_usuario",ICTSTypes.SQLVARCHAR, "");
		requestProcedureLocal.addInputParam("@i_de_fecha_operacion",ICTSTypes.SQLDATETIME, processDate);
		
		
		requestProcedureLocal.addOutputParam("@o_de_id", ICTSTypes.SQLINT4, "0");
	        
	    IProcedureResponse wProcedureResponseLocal = executeCoreBanking(requestProcedureLocal);
		
		if (logger.isDebugEnabled()) {
			logger.logDebug("Ending flow, registerDevolution callable: " + wProcedureResponseLocal.getProcedureResponseAsString());
		}
		
		if (wProcedureResponseLocal.getReturnCode()==0) {
			
			logId = Integer.parseInt(wProcedureResponseLocal.readValueParam("@o_de_id"));
		} 
		
		aBagSPJavaOrchestration.put("logIdDev", logId.toString());
		
		return logId;
		
	}
	
	public void registerTransactionWebHook(IProcedureRequest aRequest,  Map<String, Object> aBagSPJavaOrchestration, mensaje msjSPEI_In) {	
        String movementType = "SPEI_RETURN";
        String typeEvent = "S";
        String codeError = "0";
		String messageError = "";
		String logKarpay = "0";

        if (logger.isDebugEnabled()) {
            logger.logDebug(" Entrando en registerTransactionWebHook");
        }

        if (aBagSPJavaOrchestration.get("eventWH") != null) {
        	typeEvent = aBagSPJavaOrchestration.get("eventWH").toString();
        }
        
        if (aBagSPJavaOrchestration.get("idLog") != null) {
        	logKarpay = aBagSPJavaOrchestration.get("idLog").toString();
        }

    	try {
        	if (typeEvent.equals("F")) {
        		if (aBagSPJavaOrchestration.get("code_error")!= null) {
    				codeError = aBagSPJavaOrchestration.get("code_error").toString();
    			}
    			
    			if (aBagSPJavaOrchestration.get("message_error")!= null) {
    				messageError = aBagSPJavaOrchestration.get("message_error").toString();
    			}    			
        	}
        	 
			IProcedureRequest request = new ProcedureRequestAS();
			
			request.setSpName("cob_bvirtual..sp_bv_devolucion_spei_webhook");
			request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE, "local");			
			request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
			
			request.addInputParam("@i_tipotrn", ICTSTypes.SQLVARCHAR, typeEvent);
			request.addInputParam("@i_operacion", ICTSTypes.SQLVARCHAR, "I");
			request.addInputParam("@i_operationType", ICTSTypes.SQLVARCHAR , "C");
			request.addInputParam("@i_movementType", ICTSTypes.SQLVARCHAR, movementType);
			request.addInputParam("@i_causal", ICTSTypes.SQLVARCHAR, "2040");
			request.addInputParam("@i_logKarpay", ICTSTypes.SQLINT4, logKarpay);
			request.addInputParam("@i_description", ICTSTypes.SQLVARCHAR, movementType);
			request.addInputParam("@i_currency", ICTSTypes.SQLVARCHAR , "MXN");
			request.addInputParam("@i_movementId", ICTSTypes.SQLINTN , aRequest.readValueParam("@s_ssn"));
			request.addInputParam("@i_clientRequestId", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_request_id"));
			request.addInputParam("@i_typeAccountBenf", ICTSTypes.SQLVARCHAR, String.valueOf(msjSPEI_In.getOrdenpago().getOpTcClaveBen()));
			request.addInputParam("@i_typeAccountOrig", ICTSTypes.SQLVARCHAR, String.valueOf(msjSPEI_In.getOrdenpago().getOpTcClaveOrd()));
			request.addInputParam("@i_accountNameOrig", ICTSTypes.SQLVARCHAR, msjSPEI_In.getOrdenpago().getOpNomOrd()); 
			request.addInputParam("@i_speiTranckingId", ICTSTypes.SQLVARCHAR,  msjSPEI_In.getOrdenpago().getOpCveRastreo());
			request.addInputParam("@i_speiReferenceCode", ICTSTypes.SQLVARCHAR, msjSPEI_In.getOrdenpago().getId());
			request.addInputParam("@i_request_trans_success", ICTSTypes.SQLVARCHAR, "{}");			
			request.addInputParam("@i_errorDetailsCode", ICTSTypes.SQLVARCHAR, codeError);
			request.addInputParam("@i_errorDetailsMessage", ICTSTypes.SQLVARCHAR, messageError);
			
			if (aRequest.readValueParam("@x_end_user_ip") != null) {
				request.addInputParam("@i_deviceIp", ICTSTypes.SQLVARCHAR, aRequest.readValueParam("@x_end_user_ip"));
	        }
			
			IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

			if (logger.isDebugEnabled()) {
				logger.logDebug("Response Corebanking registerAllTransactionFailed: " + wProductsQueryResp.getProcedureResponseAsString());
			}
			
			if (logger.isInfoEnabled()) {
				logger.logInfo(" Saliendo de registerTransactionWebHook");
			}

        }catch(Exception e){
            logger.logError(" Error Catastrofico en registerTransactionWebHook SPEI_RETURN");
        }	
    }	
}
