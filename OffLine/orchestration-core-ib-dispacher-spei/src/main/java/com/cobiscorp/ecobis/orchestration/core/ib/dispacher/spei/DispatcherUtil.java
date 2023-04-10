package com.cobiscorp.ecobis.orchestration.core.ib.dispacher.spei;

import static com.cobiscorp.cobis.cts.domains.ICOBISTS.COBIS_HOME;

import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.ecobis.ib.application.dtos.respuesta;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ValidaSpei;
import com.cobiscorp.ecobis.ib.orchestration.dtos.mensaje;

/**
 * Plugin of Dispacher Spei
 *
 * @since Dec 29, 2022
 * @author jolmos
 * @version 1.0.0
 *
 */

public class DispatcherUtil{
 
	private static ILogger logger = LogFactory.getLogger(DispatcherUtil.class);

	protected static final String XML_REQUEST = "XML_REQUEST";
	protected static final String XML_RESPONSE = "XML_RESPONSE";
	protected static final String SPEI_TRANSACTION = "speiTransaction";
	protected static final String RESULT_VALIDACION_SPEI = "RESULT_VALIDACION_SPEI";

	public DispatcherUtil() {
	}

	public mensaje getDataMessage(String plot) throws Exception {

		JAXBContext jaxbContext = null;
		mensaje message = null;

		try {

			logger.logInfo("INICIA  Get Data Message " + plot);

			plot = plot.replace(
					"<?xml version='1.0' encoding='UTF-8'?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><ns1:ordenpago xmlns:ns1=\"http://www.praxis.com.mx/\">",
					"")
					.replace("</respuesta></soap:Body></soap:Envelope><?xml version=\"1.0\" encoding=\"Cp850\"?>", "");
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

			logger.logInfo(message.getCategoria() + " " + message.getOrdenpago().getOpNomBen());
			logger.logInfo(message);

		} catch (Exception xe) {

			logger.logInfo("Error  Marshal");

			logger.logError(xe);
			throw xe;

		}
		return message;

	}

	public String getDataReponse(Map<String, Object> aBagSPJavaOrchestration) throws Exception {
		final String METHOD_NAME = "[getDataReponse]";
		logInfo(METHOD_NAME + "[INI]");
		String dataResponse = "";

		try {
			dataResponse = "REPLAY";

			mensaje mensajeRespuesta = new mensaje();
			respuesta respuesta = new respuesta();

			// OBTENER DATOS
			mensaje mensajeSpei = (mensaje) aBagSPJavaOrchestration.get(SPEI_TRANSACTION);
			ValidaSpei validacionSpei = (ValidaSpei)aBagSPJavaOrchestration.get(RESULT_VALIDACION_SPEI);

			respuesta.setId("" + mensajeSpei.getOrdenpago().getOpClave());
			respuesta.setFechaOper(mensajeSpei.getOrdenpago().getOpFechaOper());
			respuesta.setErrCodigo(validacionSpei.getCodigoError());
			respuesta.setErrDescripcion(validacionSpei.getDescripcionError());

			mensajeRespuesta.setRespuesta(respuesta);
			mensajeRespuesta.setCategoria(mensajeSpei.getCategoria() + "_RESPUESTA");

			JAXBContext jaxbContext = null;
			jaxbContext = JAXBContext.newInstance(mensaje.class);

			Marshaller marshaller = jaxbContext.createMarshaller();

			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);

			StringWriter stringWriter = new StringWriter();
			marshaller.marshal(mensajeRespuesta, stringWriter);

			dataResponse = stringWriter.toString();

		} catch (Exception xe) {
			logger.logInfo("Error  Marshal");
			logger.logError(xe);
			throw xe;
		} finally {
			logInfo(METHOD_NAME + "[FIN]");
		}
		aBagSPJavaOrchestration.put(XML_RESPONSE, dataResponse);
		return dataResponse;
	}

	public String doSignature(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
		// TODO Auto-generated method stub

		String signed = "";

		try {

			mensaje message = (mensaje) aBagSPJavaOrchestration.get("speiTransaction");
			byte[] byteArray = ManejoBytes.ArmaTramaBytes(message.getOrdenpago());
 
			//String privateKeyFileName = System.getProperty(COBIS_HOME) + "/CTS_MF/security/certificado";
			String privateKeyFileName = System.getProperty(COBIS_HOME) + "/spei/security/certificadoSpeiIn";
			logger.logInfo("Pathx: " + privateKeyFileName);

			byte[] key = Files.readAllBytes(Paths.get(privateKeyFileName));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
			PrivateKey finalKey = keyFactory.generatePrivate(keySpec);
			logger.logInfo(finalKey.getAlgorithm());

			if (finalKey instanceof PrivateKey) {
				PrivateKey pk = (PrivateKey) finalKey;
				signed = this.sign(byteArray, pk);
				logger.logInfo("FIRMA DIGITAL A COMPARAR ::::" + signed);
			} else {
				logger.logInfo("No se recupero el PRIVATE KEY ERROR EN FIRMA!!!::::::::::::::::::::::::::::::::");
			}

		} catch (Exception xe) {

			logger.logInfo("::::::::::Error al FIRMAR::::::::::");
			logger.logError(xe);

		}

		return signed;

	}

	private String sign(byte[] in, PrivateKey PrivateKey) throws Exception {
		Signature signed = Signature.getInstance("SHA256withRSA");
		signed.initSign(PrivateKey);
		signed.update(in);
		byte[] signdata = signed.sign();
		return Base64.getEncoder().encodeToString(signdata);
	}
	
    public static Calendar getCalendarFromStringAndFormat(String aDate, SimpleDateFormat aFormat) {
        Calendar calendar = Calendar.getInstance();

        try{
            if(null != aDate && !"".equals(aDate)){
                calendar.setTime(aFormat.parse(aDate));
                return calendar;
            }
        }catch (Exception e){
            logger.logError("getCalendarFromStringAndFormat Error: ",e);
        }


        return null;
    }
    

	public static <T extends Enum<T>> T validaEnumerador(Class<T> enumType,
														 String name) {
		try {
			return Enum.valueOf(enumType, name);
		} catch (Exception e) {
			logger.logError("validaEnumerador Error: ",e);
			return null;
		}
	}

	public static boolean isNumeric(String aString) {
		try {
			BigInteger numero = new BigInteger(aString);
			logger.logInfo("es numero: " + numero);
			return true;
		} catch (Exception e) {
			logger.logError("isNumeric Error: ",e);
			return false;
		}
	}

	public static boolean validaCuentaVacia(String aString) {
		try {
			if(aString.isEmpty())
				return true;

			BigInteger valor = new BigInteger(aString);
			return (valor.intValue() == 0 ? true : false);
		} catch (Exception e) {
			logger.logError("validaCuentaVacia Error: ",e);
			return true;
		}
	}

	public static boolean isEmpty(String aString) {
		if (aString == null)
			return true;

		String validaBlancos = aString.replace(" ", "");
		return validaBlancos.isEmpty();
	}

	public static boolean validaCaracteres(String aString) {
		if (aString == null)
			return false;

		// FALTA
		return true;
	}

	public static Date convertStringToDate(String aFecha) {
		try {
			SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
			String sDia = aFecha.substring(0, 4);
			String sMes = aFecha.substring(4, 6);
			String sAnio = aFecha.substring(6, 8);
			return formato.parse(sDia + "/" + sMes + "/" + sAnio);
		} catch (Exception e) {
			logger.logError("convertStringToDate Error: ",e);
			return null;
		}
	}

	private void logDebug(Object aMessage) {
		if (logger.isDebugEnabled()) {
			logger.logDebug(aMessage);
		}
	}

	private void logInfo(Object aMessage) {
		if (logger.isInfoEnabled()) {
			logger.logInfo(aMessage);
		}
	}

}
