package com.cobiscorp.ecobis.orchestration.core.ib.card.activate;

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
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSInfrastructureException;
import com.cobiscorp.cobis.cts.commons.exceptions.CTSServiceException;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.ecobis.ib.orchestration.base.commons.Utils;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServer;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreService;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceMonetaryTransaction;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceReexecutionComponent;
import com.cobiscorp.ecobis.ib.orchestration.interfaces.ICoreServiceSendNotification;
import com.cobiscorp.ecobis.orchestration.core.ib.transfer.template.ActivateCardOfflineTemplate;

@Component(name = "sp_activate_card", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "cob_procesador..sp_activate_card"), @Property(name = "service.vendor", value = "COBISCORP"),
		@Property(name = "service.version", value = "4.6.1.0"), @Property(name = "service.identifier", value = "cob_procesador..sp_activate_card") })
public class ActivateCardOrchestrationCore extends ActivateCardOfflineTemplate {

	/** Instancia del Logger */
	private static ILogger logger = LogFactory.getLogger(ActivateCardOrchestrationCore.class);

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
	public IProcedureResponse processResponse(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		return (IProcedureResponse) aBagSPJavaOrchestration.get(RESPONSE_TRANSACTION);
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
	public void loadConfiguration(IConfigurationReader arg) {
		if (logger.isInfoEnabled())
			logger.logInfo("LOAD CONFIGUATION");
	}

	// metodo principal
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled()) {
			logger.logInfo("rfl--> Inicia executeJavaOrchestration");
		}

		Map<String, Object> mapInterfaces = new HashMap<String, Object>();
		mapInterfaces.put("coreServer", coreServer);
		mapInterfaces.put("coreService", coreService);
		mapInterfaces.put("coreServiceNotification", coreServiceNotification);
		Utils.validateComponentInstance(mapInterfaces);
		aBagSPJavaOrchestration.put(TRANSFER_NAME, "TRANFERENCIA SPI");
		aBagSPJavaOrchestration.put(CORESERVICEMONETARYTRANSACTION, coreServiceMonetaryTransaction);

		try {
			executeConnectorActivateCardBase(anOriginalRequest, aBagSPJavaOrchestration);
		} catch (CTSServiceException e) {
			logger.logError(e);
		} catch (CTSInfrastructureException e) {
			logger.logError(e);
		} finally {
			if (logger.isDebugEnabled()) {
				logger.logInfo("rfl--> Fin executeJavaOrchestration");
			}
		}
		return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}

	@Override
	protected IProcedureResponse executeTransfer(Map<String, Object> aBagSPJavaOrchestration) {
		if (logger.isDebugEnabled()) {
			logger.logDebug("rfl--> Inicia executeTransfer");
		}
		IProcedureResponse responseTransfer = new ProcedureResponseAS();
		try {
			IProcedureRequest originalRequest = (IProcedureRequest) aBagSPJavaOrchestration.get(ORIGINAL_REQUEST);
			IProcedureRequest originalRequestClone = originalRequest.clone();

			responseTransfer = executeCacao(aBagSPJavaOrchestration, responseTransfer, originalRequestClone);

		} catch (Exception e) {
			if (logger.isDebugEnabled())
				logger.logError("rfl--> error executeTransfer: " + e);
		} finally {
			if (logger.isDebugEnabled()) {
				logger.logDebug("rfl--> Fin executeTransfer");
			}
		}

		return responseTransfer;
	}

	private IProcedureResponse executeCacao(Map<String, Object> aBagSPJavaOrchestration, IProcedureResponse responseTransfer, IProcedureRequest originalRequest) {
		if (logger.isDebugEnabled())
			logger.logDebug("jcos -> Activate inicio metodo executeCacao");

		// SE LLAMA LA SERVICIO DE CACAO //CORRECCIONES SERVICIO DE ATIVACIÓN CACAO
		List<String> respuesta = cacaoExecution(originalRequest, aBagSPJavaOrchestration);

		if (logger.isDebugEnabled()) {
			logger.logDebug("jcos-->Activate respuesta: " + respuesta);
		}
		
		

		// SE HACE LA VALIDACION DE LA RESPUESTA (0000 exito)
		if (respuesta != null && respuesta.size() > 0) {
			if (!respuesta.get(1).equals("0000")) {
				/*if (logger.isDebugEnabled()) {
					logger.logDebug("rfl--> error nip");
				}
				return Utils.returnException(1, ERROR_CACAO);*/
				String codeError=respuesta.get(1);
			 /*  if(codeError.equals("1109")) {
				   
				   return Utils.returnException(1, "La Tarjeta o Medio de Acceso ya se encuentra ativo");
				   
			   }else if(codeError.equals("1018")) {
				   
				   return Utils.returnException(1, "La Tarjeta o Medio de Acceso No Existe");
			   } */				
				if (logger.isDebugEnabled()) {
					logger.logDebug("jcos--> code Error "+respuesta.get(1));
				}
				
				responseTransfer.addParam("@o_cod_respuesta", ICTSTypes.SQLVARCHAR, respuesta.get(2).length(), respuesta.get(2));
				responseTransfer.addParam("@o_desc_respuesta", ICTSTypes.SQLVARCHAR, respuesta.get(1).length(), respuesta.get(1));
			   
			} else {
				if (logger.isDebugEnabled()) {
					logger.logDebug("jcos--> ok nip");
				}
				// SE ADJUNTA NIP
				if (respuesta.size() > 3) {
					
					responseTransfer.addParam("@o_ValorNIP", ICTSTypes.SQLVARCHAR, respuesta.get(3).length(), respuesta.get(3));
					responseTransfer.addParam("@o_cod_respuesta", ICTSTypes.SQLVARCHAR, respuesta.get(2).length(), respuesta.get(2));
					responseTransfer.addParam("@o_desc_respuesta", ICTSTypes.SQLVARCHAR, respuesta.get(1).length(), respuesta.get(1));
				}
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.logDebug("jcos--> List<String> respuesta error o null");
			}
			return Utils.returnException(1, ERROR_CACAO);
		}

		if (logger.isDebugEnabled())
			logger.logDebug("jcos fin metodo executeCacao");

		return responseTransfer;

	}

	public void registroCacaoResponse(){



	}

	protected List<String> cacaoExecution(IProcedureRequest anOriginalRequest, Map<String, Object> bag) {
		List<String> response = null;

		if (logger.isInfoEnabled()) {
			logger.logInfo("jcos--> Entrando a cacaoExecution Activation");
		}

		try {

			// PARAMETROS DE ENTRADA
			anOriginalRequest.addInputParam("@i_modo", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_modo"));
			anOriginalRequest.addInputParam("@i_id_solicitud", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_id_solicitud"));
			anOriginalRequest.addInputParam("@i_tarjeta", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_tarjeta"));
			anOriginalRequest.addInputParam("@i_fecha_nacimiento", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_fecha_nacimiento"));
			anOriginalRequest.addInputParam("@i_ValorNIP", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_ValorNIP"));
			anOriginalRequest.addInputParam("@i_tipo_actualizacion", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_tipo_actualizacion"));
			anOriginalRequest.addInputParam("@i_clave_empresa", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_clave_empresa"));
			anOriginalRequest.addInputParam("@i_nombre_embozar", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_nombre_embozar"));
			anOriginalRequest.addInputParam("@i_genero", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_genero"));
			anOriginalRequest.addInputParam("@i_correo", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_correo"));
			anOriginalRequest.addInputParam("@i_ciudad", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_ciudad"));
			anOriginalRequest.addInputParam("@i_curp", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_curp"));
			anOriginalRequest.addInputParam("@i_NIP_nuevo", ICTSTypes.SQLVARCHAR, anOriginalRequest.readValueParam("@i_NIP_nuevo"));
			// VARIABLES DE SALIDA
			anOriginalRequest.addOutputParam("@o_cod_respuesta", ICTSTypes.SQLVARCHAR, "0");
			anOriginalRequest.addOutputParam("@o_desc_respuesta", ICTSTypes.SQLVARCHAR, "X");
			anOriginalRequest.addOutputParam("@o_id_solicitud", ICTSTypes.SQLVARCHAR, "0"); // "0"
			anOriginalRequest.addOutputParam("@o_tarjeta", ICTSTypes.SQLVARCHAR, "X");
			anOriginalRequest.addOutputParam("@o_ValorNIP", ICTSTypes.SQLVARCHAR, "0");

			// SE HACE LA LLAMADA AL CONECTOR
			bag.put(CONNECTOR_TYPE, "(service.identifier=CISConnectorCacao)");
			anOriginalRequest.setSpName("cob_procesador..sp_orq_creditcard");
			anOriginalRequest.setValueFieldInHeader(ICOBISTS.HEADER_TRN, "18500045");
			anOriginalRequest.addInputParam("@t_trn", ICTSTypes.SYBINT4, "18500045");

			// SE EJECUTA CONECTOR
			IProcedureResponse connectorSpeiResponse = executeProvider(anOriginalRequest, bag);

			if (logger.isDebugEnabled())
				logger.logDebug("jcos--> connectorCacaoActivationResponse: " + connectorSpeiResponse);

			// SE VALIDA LA RESPUESTA
			if (!connectorSpeiResponse.hasError()) {
				if (logger.isDebugEnabled()) {
					logger.logDebug("jcos--> success CISConnectorCacao: true");
					logger.logDebug("jcos--> connectorCacaoResponse: " + connectorSpeiResponse.getParams());
				}

				// SE MAPEAN LAS VARIABLES DE SALIDA
				response = new ArrayList<String>();
				response.add(connectorSpeiResponse.readValueParam("@o_id_solicitud"));
				response.add(connectorSpeiResponse.readValueParam("@o_cod_respuesta"));
				response.add(connectorSpeiResponse.readValueParam("@o_desc_respuesta"));

				if (connectorSpeiResponse.readValueParam("@o_ValorNIP") != null)
					response.add(connectorSpeiResponse.readValueParam("@o_ValorNIP"));

				String responseCacao=connectorSpeiResponse.readValueParam("@o_json_response");

				if(responseCacao!=null){

					logger.logInfo("Cacao responde On Json -> "+responseCacao);

				}else{

					logger.logInfo("jcos--> No existe JSON de respuesta de CACAO::: ");
				}

			} else {

				if (logger.isDebugEnabled()) {
					logger.logDebug("jcos--> Error connectorCacaoResponse: " + connectorSpeiResponse);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			response = null;
			logger.logInfo("jcos--> Error Catastrofico de cacaoExecution");

		} finally {
			if (logger.isInfoEnabled()) {
				logger.logInfo("jcos--> Saliendo de cacaoExecution");
			}
		}

		return response;
	}

}
