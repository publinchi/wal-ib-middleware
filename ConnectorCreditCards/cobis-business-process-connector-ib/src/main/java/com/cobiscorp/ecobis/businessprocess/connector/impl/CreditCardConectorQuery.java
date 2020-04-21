package com.cobiscorp.ecobis.businessprocess.connector.impl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.Binding;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.springframework.remoting.soap.SoapFaultException;

import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.csp.services.ICSPExecutorConnector;
import com.cobiscorp.cobis.csp.util.CSPUtil;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseWSAS;
import com.cobiscorp.ecobis.businessprocess.connector.Constants;
import com.cobiscorp.ecobis.ws.client.linkser.ConsultaTarjetasCre;
import com.cobiscorp.ecobis.ws.client.linkser.IWSNet;
import com.cobiscorp.ecobis.ws.client.linkser.ObjectFactory;
import com.cobiscorp.ecobis.ws.client.linkser.Service1;

@Component(name = "CreditCardConectorQuery", immediate = false)
@Service({ com.cobiscorp.cobis.csp.services.ICSPExecutorConnector.class })
@Properties({
		@Property(name = "service.description", value = "CreditCardConectorQuery"),
		@Property(name = "service.vendor", value = "COBISCORP"),
		@Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "CreditCardConectorQuery") })
public class CreditCardConectorQuery implements ICSPExecutorConnector {
	private static final ILogger logger = LogFactory
			.getLogger(CreditCardConectorQuery.class);
	private static java.util.Properties properties;
	private static IWSNet iService = null;

	@Override
	public void loadConfiguration(IConfigurationReader configurationReader) {
		// Obtener las propiedades
		if (logger.isDebugEnabled()) {
			logger.logDebug("Ingreso a loadConfiguration - Orchestrator.MyConfigurationImpl");
		}
		properties = configurationReader
				.getProperties("//config/properties/property");

	}

	@Override
	public IProcedureResponse processResponseProvider(Map<Object, Object> aBagSPJavaOrchestration,
			String arg1) {
		IProcedureResponse procedureResponse = new ProcedureResponseWSAS();

		// Copio parametros de cabecera desde procedureRequest a procedureResponse
		CSPUtil.copyHeaderFields((IProcedureRequest) aBagSPJavaOrchestration.get("anOriginalRequest"), procedureResponse);
		procedureResponse.addParam("@o_message", 39, 1, aBagSPJavaOrchestration.get("returnMessage").toString());
		procedureResponse.addParam("@o_return", 39, 1, aBagSPJavaOrchestration.get("returnCode").toString());

		procedureResponse.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, ICSP.SUCCESS);
		return procedureResponse;
		
	}

	@Override
	public IProcedureResponse transformAndSend(
			IProcedureRequest anOriginalRequest) {
		// TODO Auto-generated method stub
		String xmlResponse = "";
		Map<Object, Object> resultados = new HashMap<Object, Object>();
		// Almaceno transaccion principal en bolsa
		resultados.put("anOriginalRequest", anOriginalRequest);
		ObjectFactory fact = new ObjectFactory();
		ConsultaTarjetasCre consultaTarjetasCre = new ConsultaTarjetasCre();
		consultaTarjetasCre.setPCocigoCliente(fact
				.createConsultaTarjetasCrePCocigoCliente(anOriginalRequest
						.readValueParam("@i_codigo_cliente")));
		ClassLoader bundleClassLoader = super.getClass().getClassLoader();
		ClassLoader originalClassLoader = Thread.currentThread()
				.getContextClassLoader();
		Thread.currentThread().setContextClassLoader(bundleClassLoader);
		Map<Object, Object> resultValues = new HashMap<Object, Object>();
        if (logger.isDebugEnabled())
		logger.logDebug("-->> CREDITCARD-CONEXION => EXECUTION");
		if (!this.getConnection(resultValues)) {
			resultados.put("returnCode", -1);
			resultados.put("returnMessage", resultValues
					.get("ExceptionMessage").toString());
			return processResponseProvider(resultados, null);
		}
		try {
			if (anOriginalRequest.readValueParam("@i_operacion").equals("C")) {
				xmlResponse = iService.consultaTarjetasCre(consultaTarjetasCre
						.getPCocigoCliente().getValue());
                if (logger.isInfoEnabled())
				logger.logInfo("RESPUESTA XML SERVICIO DEL MAL: " + xmlResponse);
			}

		} catch (SoapFaultException e) {
			Thread.currentThread().setContextClassLoader(originalClassLoader);
			iService = null;
            if (logger.isDebugEnabled())
			logger.logDebug(
					"-->> CREDITCARD-CONNECTOR => EXECUTION SOAPFaultException - Ejecución al host remoto - ["
							+ anOriginalRequest.readValueParam("@i_operacion")
							+ "]", e);
			resultados.put("returnCode", -2);
			resultados.put(
					"returnMessage",
					"SOAPFaultException - '" + e.getFaultCode() + "' - "
							+ e.getFaultString());
			return processResponseProvider(resultados, null);
		} catch (WebServiceException e) {
			Thread.currentThread().setContextClassLoader(originalClassLoader);
			iService = null;
            if (logger.isDebugEnabled())
			logger.logDebug(
					"-->> CREDITCARD-CONNECTOR => EXECUTION WebServiceException - Ejecución al host remoto - ["
							+ anOriginalRequest.readValueParam("@i_operacion")
							+ "]", e);
			resultados.put("returnCode", -2);
			resultados.put("returnMessage",
					"WebServiceException - " + e.getMessage());
			return processResponseProvider(resultados, null);

		} catch (Exception e) {
			Thread.currentThread().setContextClassLoader(originalClassLoader);
			iService = null;
            if (logger.isDebugEnabled())
			logger.logDebug(
					"-->> CREDITCARD-CONNECTOR => EXECUTION EXCEPTION - Ejecución al host remoto - ["
							+ anOriginalRequest
									.readValueParam("@i_tipo_reporte") + "]", e);
			resultados.put("returnCode", -2);
			resultados.put("returnMessage",
					"WebServiceException - " + e.getMessage());
			return processResponseProvider(resultados, null);
		} finally {
			iService = null;
			// OJO - Se vuelve a setear el classloader original
			Thread.currentThread().setContextClassLoader(originalClassLoader);
		}

		resultados.put("returnCode", 0);
		resultados.put("returnMessage", xmlResponse);
		return processResponseProvider(resultados, null);
	}

	@SuppressWarnings("rawtypes")
	private boolean getConnection(Map<Object, Object> resultValues) {
		String urlString = "https://10.180.1.120/ServicioFie/WSNet.svc?wsdl";
		// String urlString = properties.getProperty("WSDL");

		if (iService == null) {
			Service1 service = null;
			if (logger.isDebugEnabled()) {
				logger.logDebug("-->> CREDITCARD-CONNECTOR => CONEXION - wsUrl = ["
						+ Constants.URL_STRING + "]");
			}
			try {
				service = new Service1(new URL(Constants.URL_STRING), new QName(
						"http://tempuri.org/", "Service1"));
			} catch (SOAPFaultException e) {
                if (logger.isDebugEnabled())
				logger.logDebug(
						"-->> INFOCRED-CONNECTOR => CONEXION SOAPFaultException - Error al crear la conexión con el Host remoto",
						e);
				resultValues.put("ExceptionMessage", "SOAPFaultException - '"
						+ e.getFault().getFaultCode() + "' - "
						+ e.getFault().getFaultString());
				return false;
			} catch (WebServiceException e) {
                if (logger.isDebugEnabled())
				logger.logDebug(
						"-->> CREDITCARD-CONNECTOR => CONEXION WebServiceException - Error al crear la conexión con el Host remoto",
						e);
				resultValues.put("ExceptionMessage", "WebServiceException - "
						+ e.getMessage());
				return false;
			} catch (MalformedURLException e) {
                if (logger.isDebugEnabled())
				logger.logDebug(
						"-->> CREDITCARD-CONNECTOR => CONEXION MalformedURLException - Error al crear la conexión con el Host remoto",
						e);
				resultValues.put("ExceptionMessage", "MalformedURLException - "
						+ e.getMessage());
				return false;
			} catch (Exception e) {
                if (logger.isDebugEnabled())
				logger.logDebug(
						"-->> CREDITCARD-CONNECTOR => CONEXION Exception - Error al crear la conexión con el Host remoto",
						e);
				resultValues.put("ExceptionMessage",
						"Exception - " + e.getMessage());
				return false;
			}

			if (logger.isDebugEnabled()) {
				logger.logDebug("-->> CREDITCARD-CONNECTOR => CONEXION - Recuperar el EndPoint");
			}
			iService = service.getBasicHttpBindingIWSNet();

			if (logger.isDebugEnabled()) {
				logger.logDebug("-->> CREDITCARD-CONNECTOR => CONEXION - Setear Seguridades");
			}
			Binding binding = ((BindingProvider) iService).getBinding();
			List<Handler> handlerList = binding.getHandlerChain();
			if (handlerList == null)
				handlerList = new ArrayList<Handler>();
			handlerList.add(new CreditCardSecurityHandler(Constants.URL_USER_NAME ,
					Constants.URL_PASSWORD));
			binding.setHandlerChain(handlerList);
		}
		return true;
	}

}
