package com.cobiscorp.ecobis.ib.orchestration.base.utils.commons;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import javax.crypto.Cipher;

import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.ecobis.ib.orchestration.base.utils.transfers.EncryptData;


public class RSAKeyLoader {
    private static ILogger logger = LogFactory.getLogger(EncryptData.class);
    private PublicKey publicKey;
   
    public RSAKeyLoader(String publicFilekey, String privateFileKey)
    {
    	loadPublicKey(publicFilekey);
    }
    //devuelve en base 64
	public String encryptData(String pan)
	{
		String dataCrip = null;
		try 
		{
			Cipher encryptCipher = Cipher.getInstance("RSA");
		    encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
		    byte[] encryptedMessage = encryptCipher.doFinal(pan.getBytes());
		    dataCrip = Base64.getEncoder().encodeToString(encryptedMessage);
		} catch (Exception e) {
			if(logger.isDebugEnabled())
			{
				logger.logDebug("[ERORR MESSAGGE]:::" + e.getMessage());
			}
        }
		return dataCrip;
	}
	//metodo que carga llave publica
	public void loadPublicKey(String fileName)  
	{
        String key;
        KeyFactory keyFactory = null;
        X509EncodedKeySpec spec;
        
		try
		{
			Path path = Paths.get(fileName);
	        if (!Files.exists(path)) 
	        {
	        	if(logger.isDebugEnabled())
				{
					logger.logDebug("El archivo de clave pública no se encuentra: " + fileName);
				}
	        }else
	        {
	        	key = new String(Files.readAllBytes(Paths.get(fileName)))
			        .replaceAll("-----BEGIN PUBLIC KEY-----", "")
			        .replaceAll("-----END PUBLIC KEY-----", "")
			        .replaceAll("\\s", "");
	        	byte[] keyBytes = Base64.getDecoder().decode(key);
	        	spec = new X509EncodedKeySpec(keyBytes);
	        	keyFactory = KeyFactory.getInstance("RSA");
	        	publicKey = keyFactory.generatePublic(spec);
	        }
	        
		} catch (IOException e)
		{
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e)
		{
			
			e.printStackTrace();
		} catch (InvalidKeySpecException e)
		{
			e.printStackTrace();
		}
    }
	public PublicKey getPrivateKey()
	{
		return publicKey;
	}
	public void setPrivateKey(PublicKey publicKey)
	{
		this.publicKey = publicKey;
	}
	
}
