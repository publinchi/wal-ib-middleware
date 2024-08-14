package com.cobiscorp.ecobis.orchestration.core.ib.dispacher.spei;

import java.util.Map;
import java.util.concurrent.Callable;

import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.ecobis.ib.orchestration.dtos.mensaje;

public class CallableTask extends SPJavaOrchestrationBase implements Callable<IProcedureResponse> {
    private IProcedureRequest request;
    private Map<String, Object> aBagSPJavaOrchestration;
	private static ILogger logger = LogFactory.getLogger(CallableTask.class);
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
    public CallableTask(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration, mensaje msjIn,  String codeBank) {
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
		if (logger.isInfoEnabled()) 
		{
			logger.logInfo("Entrando a callPaymentInReturn future");
		}
		IProcedureResponse connectorSpeiResponse = null;
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
				
			} else {

				if (logger.isDebugEnabled()) {
					logger.logDebug("Error Catastrifico respuesta de callPaymentInReturn future");
					logger.logDebug("Error connectorSpeiResponse Catastrifico callPaymentInReturn future: " + connectorSpeiResponse);
				}
			}
		} catch (Exception e) {
			logger.logError(e);
			logger.logInfo("Error Catastrofico de callPaymentInReturn future");
			e.printStackTrace();
			logger.logInfo("Error Catastrofico de callPaymentInReturn future");

		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo("Saliendo de callPaymentInReturn future");
			}
		}
		// SE REGRESA RESPUESTA
		return connectorSpeiResponse;
	}

	
}
