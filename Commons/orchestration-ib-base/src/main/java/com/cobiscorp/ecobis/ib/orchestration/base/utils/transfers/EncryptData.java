package com.cobiscorp.ecobis.ib.orchestration.base.utils.transfers;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;

public class EncryptData {

	private static byte[] iv = null;
	private static SecretKey secretKey = null;
	private static byte[] scrKey = null;
    private static ILogger logger = LogFactory.getLogger(EncryptData.class);
    
    public static Map<String, Object> encryptWithAESGCM(String pan, String pk) {
    	
    	logger.logDebug("[INI]: encryptWithAESGCM-- ");
    	
        String OUTPUT_FORMAT = "%-15s:%s";
        //String pk = "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUF2Z1pFMlhCZjBFbHFFUzd6VjBVNApLa0IrdjBsd1g5aFFYMWtNcSs3WEhQTk0vZThRNjUrN1RxL0piTS9UNEpNZnQ5a0x5UGV5YTNtM1o5TVd3RGFiCkh0eUlvYTlmS056WjNxZUh2VVIxWkRiSllkSHRNelJydXVYdnI2OElDQm5rOHhET0FEUVJHemMwWlRYZDllcW8KdktoUVFrRnFJUlBWMWhzWGZFdzEzN1Q2NjAyNmswTmZodWpMcGtPZVVwUndYNWJMbEQxeGFsdE1WK2t4UTZsMgpsMDExcnVkdU9pMkI2RWduVlBhYk50a3ZPaU1pbXZLKzY5ODg4ZTBWZzNpZGNURTNqRURpYnBMMXNaam5wd0N4CkJMNDdXQURqSGtDODliR2VhQ2w3bHl4aW8wZ05RQ1VsUkQxOWlqazg2WTRCNnR1TGNwQUlrK1Exb3d5OHo5aU8KMVFJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==";
        Map<String, Object> dataMapEncrypt = new HashMap<String, Object>();
        Base64.Decoder decoder = Base64.getDecoder();
        
        byte[] aes = null;
        
        try {
        	logger.logDebug("[INI]: SecretKey encryptWithAESGCM.. " + scrKey);
            if(scrKey == null){
            	logger.logDebug("[pulbicKey]:: NO CONTENT");
	            secretKey = generateAESKey(256);
            	PemReader publicPemReader = new PemReader(new StringReader(new String(decoder.decode(pk.getBytes()))));
                PemObject publicPemObjEncr = publicPemReader.readPemObject();
                publicPemReader.close();	
                scrKey = publicPemObjEncr.getContent();
            }
            
            aes = secretKey.getEncoded();
            iv = getRandomNonce(12);

            KeyFactory publicKeyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(scrKey);
            PublicKey publicKey = publicKeyFactory.generatePublic(keySpec);

            //encrypt with OAEP
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
            OAEPParameterSpec OAEPParams = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey, OAEPParams);
            byte[] aesCipher = cipher.doFinal(aes);

            Cipher cipherGCM = Cipher.getInstance("AES/GCM/NoPadding");
            cipherGCM.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));
            byte[] enc = cipherGCM.doFinal(pan.getBytes(StandardCharsets.UTF_8));

            dataMapEncrypt.put("pan", Base64.getEncoder().encodeToString(enc));
            dataMapEncrypt.put("iv", Base64.getEncoder().encodeToString(iv));
            dataMapEncrypt.put("aes", Base64.getEncoder().encodeToString(aesCipher));
            
            logger.logDebug( (OUTPUT_FORMAT) + " ENCRYPT " + Base64.getEncoder().encodeToString((enc)) );
            logger.logDebug( (OUTPUT_FORMAT) + " IV " + Base64.getEncoder().encodeToString(iv));
            logger.logDebug( (OUTPUT_FORMAT) + " KEY ENCRYPTED"+ Base64.getEncoder().encodeToString(aesCipher));

            logger.logDebug("[INI]: before encryptWithAESGCM ");
            
        } catch (Exception e) {
        	logger.logDebug("[ERORR MESSAGGE]:::" + e.getMessage());
        }
        
        return dataMapEncrypt;
    }
    
    public static SecretKey generateAESKey(int size) {
        byte[] keyBytes = new byte[size / 8];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(keyBytes);
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static byte[] getRandomNonce(int numBytes) {
        byte[] nonce = new byte[numBytes];
        new SecureRandom().nextBytes(nonce);
        return nonce;
    }
    
    public static Map<String, Object> encryptPan(String pan ) {
        Map<String, Object> dataMapEncrypt = new HashMap<String, Object>();
        String publicKeyBase64 = "LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUF2Z1pFMlhCZjBFbHFFUzd6VjBVNApLa0IrdjBsd1g5aFFYMWtNcSs3WEhQTk0vZThRNjUrN1RxL0piTS9UNEpNZnQ5a0x5UGV5YTNtM1o5TVd3RGFiCkh0eUlvYTlmS056WjNxZUh2VVIxWkRiSllkSHRNelJydXVYdnI2OElDQm5rOHhET0FEUVJHemMwWlRYZDllcW8KdktoUVFrRnFJUlBWMWhzWGZFdzEzN1Q2NjAyNmswTmZodWpMcGtPZVVwUndYNWJMbEQxeGFsdE1WK2t4UTZsMgpsMDExcnVkdU9pMkI2RWduVlBhYk50a3ZPaU1pbXZLKzY5ODg4ZTBWZzNpZGNURTNqRURpYnBMMXNaam5wd0N4CkJMNDdXQURqSGtDODliR2VhQ2w3bHl4aW8wZ05RQ1VsUkQxOWlqazg2WTRCNnR1TGNwQUlrK1Exb3d5OHo5aU8KMVFJREFRQUIKLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==";
        try {
            // Decodificar la clave pública
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            // Generar clave AES y IV
            SecretKey secretKey = generateAESKey();
            byte[] iv = generateIV();

            // Encriptar la clave AES con RSA-OAEP
            Cipher rsaCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] aesCipher = rsaCipher.doFinal(secretKey.getEncoded());

            // Encriptar el PAN con AES-GCM
            Cipher aesCipherGCM = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(128, iv);
            aesCipherGCM.init(Cipher.ENCRYPT_MODE, secretKey, gcmParameterSpec);
            byte[] encryptedPAN = aesCipherGCM.doFinal(pan.getBytes(StandardCharsets.UTF_8));

            // Colocar los datos en el mapa
            dataMapEncrypt.put("pan", Base64.getEncoder().encodeToString(encryptedPAN));
            dataMapEncrypt.put("iv", Base64.getEncoder().encodeToString(iv));
            dataMapEncrypt.put("aes", Base64.getEncoder().encodeToString(aesCipher));

            logger.logDebug(  " ENCRYPT " + Base64.getEncoder().encodeToString((encryptedPAN)) );
            logger.logDebug(  " IV " + Base64.getEncoder().encodeToString(iv));
            logger.logDebug( " KEY ENCRYPTED"+ Base64.getEncoder().encodeToString(aesCipher));

            
            return dataMapEncrypt; // Asegurarse de devolver los datos cifrados

        } catch (Exception e) {
            e.printStackTrace(); // Manejo básico de errores
            return null; // En caso de error, devuelve null
        }
    }

    private static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        return keyGen.generateKey();
    }

    private static byte[] generateIV() {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[12]; // Tamaño de IV para GCM
        random.nextBytes(iv);
        return iv;
    }

}