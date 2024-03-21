package com.cobiscorp.ecobis.orchestration.core.ib.dispacher.spei;

import static com.cobiscorp.cobis.cts.domains.ICOBISTS.COBIS_HOME;

import java.io.FileInputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Calendar;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.ecobis.ib.orchestration.dtos.mensaje;

/**
 * Plugin of Dispacher Spei
 *
 * @since Dec 29, 2022
 * @author jolmos
 * @version 1.0.0
 *
 */

public class DispatcherUtil {

	private static ILogger logger = LogFactory.getLogger(DispatcherUtil.class);	
	 
	
	  public DispatcherUtil() {}	


	  public mensaje getDataMessage(String plot) throws Exception {
		  
			JAXBContext jaxbContext=null;
			mensaje message=null;
			
			try {
				
				 logger.logInfo("INICIA  Get Data Message "+plot);	
				
				plot=plot.replace("<?xml version='1.0' encoding='UTF-8'?><soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"><soapenv:Body><ns1:ordenpago xmlns:ns1=\"http://www.praxis.com.mx/\">","").replace("</respuesta></soap:Body></soap:Envelope><?xml version=\"1.0\" encoding=\"Cp850\"?>","");
				plot=plot.replace("<?xml version=\"1.0\" encoding=\"Cp850\"?>","").replace("xsi:","");
				plot=plot.replace("&lt;","<");
				plot=plot.replace( "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"","");
				plot=plot.replace( "</ns1:ordenpago></soapenv:Body></soapenv:Envelope>","");				
				
				 logger.logInfo("INICIA  Marshal");	
				 logger.logInfo("LIMPIOX "+plot);	

				  jaxbContext = JAXBContext.newInstance(mensaje.class); 
				  Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
				  message = (mensaje)  jaxbUnmarshaller.unmarshal(new StringReader(plot));
			      
			      logger.logInfo("Termina  Marshal");	
			      
			      logger.logInfo(message.getCategoria()+" "+message.getOrdenpago().getOpNomBen());
				  logger.logInfo(message);	
				  
		}catch(Exception xe ) {
			
			logger.logInfo("Error  Marshal");	
			
			logger.logError(xe);
			  throw xe;
			
		}
			return message;
		  
	  }
	  
    
	    public static Calendar getCalendarFromStringAndFormat(String aDate, SimpleDateFormat aFormat) {
	        Calendar calendar = Calendar.getInstance();

	        try{
	            if(null != aDate && !"".equals(aDate)){
	                calendar.setTime(aFormat.parse(aDate));
	                return calendar;
	            }
	        }catch (Exception e){
	            logger.logError("Error fecha: ",e);
	        }


	        return null;
	    }
	  
		public String doSignature(IProcedureRequest request, Map<String, Object> aBagSPJavaOrchestration) {
			String signed="";
			try 
			{		
				mensaje msj = (mensaje)aBagSPJavaOrchestration.get("speiTransaction");
				byte [] byteArray= ManejoBytes.armaTramaBytes(msj, aBagSPJavaOrchestration);
				signed = signDataPrivateKey(byteArray, aBagSPJavaOrchestration);

			}catch (Exception xe) {
				logger.logInfo("::::::::::Error al FIRMAR::::::::::");
				logger.logError(xe);
				
			}
			
			return signed;
			
		}  
	   
	   public static String signDataPrivateKey(byte[] firmaDigital, Map<String, Object> aBagSPJavaOrchestration) {
	       	       
	        String signed = "";
	        try {
	        	//signDataPrivateKey();
	        	String keystorePath = aBagSPJavaOrchestration.get("jksurl").toString();
	        	
	  	        // Contraseña del keystore
	  	        String keystorePassword = aBagSPJavaOrchestration.get("keyPass").toString();
	  	        // Alias de la clave privada en el keystore
	  	        String alias = aBagSPJavaOrchestration.get("alias").toString();

	  	        // Cargar el keystore desde el archivo
	  	        KeyStore keystore;
	  			keystore = KeyStore.getInstance("JKS");
			    keystore.load(new FileInputStream(keystorePath), keystorePassword.toCharArray());
	
		        // Obtener la clave privada y el certificado asociado
		        KeyStore.PrivateKeyEntry privateKeyEntry = (KeyStore.PrivateKeyEntry) keystore.getEntry(alias,
		                new KeyStore.PasswordProtection(keystorePassword.toCharArray()));
	
		        PrivateKey finalKey = privateKeyEntry.getPrivateKey();
		        
		        if(logger.isDebugEnabled())
		        	logger.logDebug("Signature: " + finalKey.getAlgorithm());	
		      
		       if (finalKey instanceof PrivateKey) {
                    PrivateKey pk = (PrivateKey) finalKey;
	                signed = sign(firmaDigital, pk);
	            } else {
	            	 if(logger.isDebugEnabled())
	 		        	logger.logDebug("Error Signature" );	
	            }

	        } catch (Exception xe) {
	        	 if(logger.isDebugEnabled())
		        	logger.logDebug("Signature error: " + xe.getMessage());	
	        	
	        } 
	        if(logger.isDebugEnabled())
	        	logger.logDebug("Signature: " +signed);	
	        
	        return signed;
	    }
	    private static String sign(byte[] in, PrivateKey PrivateKey) throws Exception {
	        Signature signed = Signature.getInstance("SHA256withRSA");
	        signed.initSign(PrivateKey);
	        signed.update(in);
	        byte[] signdata = signed.sign();
	        return Base64.getEncoder().encodeToString(signdata);
	    }


}
