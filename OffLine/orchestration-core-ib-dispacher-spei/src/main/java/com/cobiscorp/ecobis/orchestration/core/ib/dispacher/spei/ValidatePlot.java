package com.cobiscorp.ecobis.orchestration.core.ib.dispacher.spei;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.ecobis.ib.orchestration.dtos.mensaje;

/**
 * Plugin of Dispacher Spei
 *
 * @since Dec 29, 2022
 * @author jolmos
 * @version 1.0.0
 *
 */

public class ValidatePlot { 

	private static ILogger logger = LogFactory.getLogger(ValidatePlot.class);


	  public ValidatePlot() {}	


	  public mensaje getDataMessage(String plot) throws Exception {

		  JAXBContext jaxbContext = null;
		  mensaje message = null;

		  try {

			  logger.logInfo("INICIA  Get Data Message " + plot);

			  plot = plot.replace("<?xml version='1.0' encoding='UTF-8'?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><ns1:ordenpago xmlns:ns1=\"http://www.praxis.com.mx/\">", "").replace("</respuesta></soap:Body></soap:Envelope><?xml version=\"1.0\" encoding=\"Cp850\"?>", "");
			  plot = plot.replace("<?xml version=\"1.0\" encoding=\"Cp850\"?>", "").replace("xsi:", "");
			  plot = plot.replace("&lt;", "<");
			  plot = plot.replace("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"", "");
			  plot = plot.replace("</ns1:ordenpago></soapenv:Body></soapenv:Envelope>", "");

			  logger.logInfo("INICIA  Marshal");
			  logger.logInfo("LIMPIOX " + plot);

			  jaxbContext = JAXBContext.newInstance(mensaje.class);
			  Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			  message = (mensaje) jaxbUnmarshaller.unmarshal(new StringReader(plot));

			  logger.logInfo("Termina  Marshal");

			  logger.logInfo("Mensaje: " + message.getCategoria());
			  logger.logInfo("Clave Rastreo: " + message.getOrdenpago().getOpCveRastreo());
			  logger.logInfo("Beneficiario: " + message.getOrdenpago().getOpNomBen());
			  logger.logInfo(message);

		  } catch (Exception xe) {
			  logger.logInfo("Error  Marshal");
			  logger.logError(xe);
			  throw xe;
		  } finally {
			  logger.logInfo("FINALIZA  Get Data Message ");
		  }
		  return message;
	  }

}
