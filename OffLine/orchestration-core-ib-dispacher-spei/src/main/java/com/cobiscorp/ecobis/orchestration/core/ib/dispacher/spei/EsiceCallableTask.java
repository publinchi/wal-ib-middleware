package com.cobiscorp.ecobis.orchestration.core.ib.dispacher.spei;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
import com.cobiscorp.ecobis.ib.orchestration.dtos.mensaje;
import com.cobiscorp.ecobis.orchestration.core.ib.dispacher.dto.Constans;

public class EsiceCallableTask extends SPJavaOrchestrationBase implements Callable<IProcedureResponse> {
    private IProcedureRequest request;
    private Map<String, Object> aBagSPJavaOrchestration;
	private static ILogger logger = LogFactory.getLogger(EsiceCallableTask.class);
	
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
    public EsiceCallableTask(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
        this.request = request;
        this.aBagSPJavaOrchestration = aBagSPJavaOrchestration;
     
    }

    @Override
    public IProcedureResponse call() {
        // Llamar a otra función dentro del método call()
    	
        return callWsEsice(request, aBagSPJavaOrchestration);
    }

    private IProcedureResponse callWsEsice(IProcedureRequest anOriginalReq, Map<String, Object> aBagSPJavaOrchestration)
    {
		Integer idLog =	logEntryApi(request, aBagSPJavaOrchestration, "I", "ESICE", null, null, null, null, null);
		
		IProcedureResponse responseCda = getWsEsice(request, aBagSPJavaOrchestration);
		if(responseCda.getReturnCode()!=0)
		{
			if(logger.isDebugEnabled()) {
				logger.logDebug("CDA mensaje: "+responseCda.readValueParam("@o_msj_respuesta"));
				logger.logDebug("CDA respuesta: "+responseCda.readValueParam("@o_cod_respuesta"));
			}
			
		}
		String returnCodeMsj = responseCda.readValueParam("@o_cod_respuesta")+" - "+responseCda.readValueParam("@o_msj_respuesta");
		//llamada a log update
		logEntryApi(request, aBagSPJavaOrchestration, "U", "ESICE", null, returnCodeMsj,  responseCda.readValueParam("@o_response"), idLog, responseCda.readValueParam("@o_request"));
		return responseCda;
	}
    private IProcedureResponse getWsEsice(IProcedureRequest anOriginalReq, Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureResponse connectorResponse = null;

		IProcedureRequest anOriginalRequest = anOriginalReq.clone();
		aBagSPJavaOrchestration.remove("trn_virtual");
		mensaje msjIn = (mensaje) aBagSPJavaOrchestration.get("speiTransaction");
		if (logger.isDebugEnabled()) {
			logger.logDebug(" Entrando en getWsEsice");
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
			anOriginalRequest.addInputParam("@i_op_fecha_abono", ICTSTypes.SQLVARCHAR, opFechaOper);
			anOriginalRequest.addInputParam("@i_op_hora_abono", ICTSTypes.SQLVARCHAR, horaHHmmss);
			anOriginalRequest.addInputParam("@i_op_cve_rastreo", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpCveRastreo());
			anOriginalRequest.addInputParam("@i_op_folio_orig_odp", ICTSTypes.SQLINT4,String.valueOf( msjIn.getOrdenpago().getOpFolio()));
			anOriginalRequest.addInputParam("@i_op_folio_orig_paq", ICTSTypes.SQLINT4,String.valueOf( msjIn.getOrdenpago().getPaqFolio()));		
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
			
			
			//tipo pago  30 7 
			if(msjIn.getOrdenpago().getOpTpClave()==30)
			{
				anOriginalRequest.addInputParam("@i_op_nom_part_indirecto_ord", ICTSTypes.SQLVARCHAR,  msjIn.getOrdenpago().getOpNomParticipanteOrd());
	         	anOriginalRequest.addInputParam("@i_op_cta_part_indirecto_ord", ICTSTypes.SQLVARCHAR, msjIn.getOrdenpago().getOpCuentaParticipanteOrd());
	         	anOriginalRequest.addInputParam("@i_op_rfc_curp_part_indirecto_ord", ICTSTypes.SQLVARCHAR,  msjIn.getOrdenpago().getOpRfcParticipanteOrd());
	         	anOriginalRequest.addInputParam("@i_op_nom_part_indirecto_ben", ICTSTypes.SQLVARCHAR,  msjIn.getOrdenpago().getOpNomBen());
	         	anOriginalRequest.addInputParam("@i_op_cta_part_indirecto_ben", ICTSTypes.SQLVARCHAR,  msjIn.getOrdenpago().getOpCuentaBen());
	         	anOriginalRequest.addInputParam("@i_op_rfc_curp_part_indirecto_ben", ICTSTypes.SQLVARCHAR,msjIn.getOrdenpago().getOpRfcCurpOrd());
			}
         	//tipo pago 36
			if(msjIn.getOrdenpago().getOpTpClave()==36)
			{
	         	anOriginalRequest.addInputParam("@i_op_id_remesa", ICTSTypes.SQLVARCHAR,  msjIn.getOrdenpago().getOpIdRemesa());
	         	anOriginalRequest.addInputParam("@i_op_pais", ICTSTypes.SQLVARCHAR,  msjIn.getOrdenpago().getOpPais());
	         	anOriginalRequest.addInputParam("@i_op_divisa", ICTSTypes.SQLVARCHAR,  msjIn.getOrdenpago().getOpDivisa());
	         	anOriginalRequest.addInputParam("@i_op_nom_emisor_rem", ICTSTypes.SQLVARCHAR,  msjIn.getOrdenpago().getOpNomEmisorRemesa());
	         	anOriginalRequest.addInputParam("@i_op_cta_emisor_rem", ICTSTypes.SQLVARCHAR,  msjIn.getOrdenpago().getOpCuentaEmisorRemesa());
	         	anOriginalRequest.addInputParam("@i_op_rfc_curp_emisor_rem", ICTSTypes.SQLVARCHAR,  msjIn.getOrdenpago().getOpRfcCurpEmisorRemesa());
	         	anOriginalRequest.addInputParam("@i_op_nom_ben_rem", ICTSTypes.SQLVARCHAR,  msjIn.getOrdenpago().getOpNomBenRemesa());
	         	anOriginalRequest.addInputParam("@i_op_cta_ben_rem", ICTSTypes.SQLVARCHAR,  msjIn.getOrdenpago().getOpCuentaBen());
	           	anOriginalRequest.addInputParam("@i_op_rfc_curp_ben_rem", ICTSTypes.SQLVARCHAR,  msjIn.getOrdenpago().getOpRfcCurpOrd());    	
	         	anOriginalRequest.addInputParam("@i_op_nom_prov_rem_ext", ICTSTypes.SQLVARCHAR,  msjIn.getOrdenpago().getOpNomProvRemesaExtranjera());
	         	anOriginalRequest.addInputParam("@i_op_nom_prov_rem_nac", ICTSTypes.SQLVARCHAR,  msjIn.getOrdenpago().getOpNomProvRemesaNacional());
			}
			anOriginalRequest.addInputParam("@i_op_hora00", ICTSTypes.SQLVARCHAR, horaHHmmssSSS);
			anOriginalRequest.addFieldInHeader(ICOBISTS.HEADER_TIMEOUT, ICOBISTS.HEADER_STRING_TYPE, "4000");
			anOriginalRequest.addFieldInHeader("trn_virtual", ICOBISTS.HEADER_STRING_TYPE, "18500164");
			anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500164");// 1890018
			
			aBagSPJavaOrchestration.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorESICE)");	
			// SE HACE LA LLAMADA AL CONECTOR
			// SE EJECUTA CONECTOR
			connectorResponse = executeProvider(anOriginalRequest, aBagSPJavaOrchestration);

			if (logger.isDebugEnabled())
				logger.logDebug("getWsEsice response: " + connectorResponse);

		} catch (Exception e) {
			e.printStackTrace();
			aBagSPJavaOrchestration.put("@o_result", "999");
			connectorResponse = null;
			logger.logError(" Error de getWsEsice",e);

		} finally {
			if (logger.isDebugEnabled()) {
				logger.logDebug("--> getWsEsice");
			}
		}
		return connectorResponse;
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
	        {
	        	opCuentaBen = card.maskNumber(msjIn.getOrdenpago().getOpCuentaBen());
	        	if(request != null)
	        		request = request.replace(msjIn.getOrdenpago().getOpCuentaBen(), card.maskNumber(msjIn.getOrdenpago().getOpCuentaBen()));
	        }
	        else
	        	opCuentaBen = msjIn.getOrdenpago().getOpCuentaBen();
	        
	        if( msjIn.getOrdenpago().getOpTcClaveOrd()==3)
	        {
	        	opCuentaOrd = card.maskNumber(msjIn.getOrdenpago().getOpCuentaOrd());
	        	if(request != null)
	        		request = request.replace(msjIn.getOrdenpago().getOpCuentaOrd(), card.maskNumber(msjIn.getOrdenpago().getOpCuentaOrd()));
	        }
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
	
}
