package com.cobiscorp.ecobis.ib.orchestration.base.utils.commons;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESCrypt

{
	private SecretKey key;
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String FIXED_IV = "1234567890123456"; // IV fijo de 16 bytes para AES

	public AESCrypt(String key)
	{
		this.key = convertStringToSecretKey(key);
	}
	
	 public String encryptData(String data)  
	 {  
		try
		{
			Cipher cipher;
			cipher = Cipher.getInstance(TRANSFORMATION);
			IvParameterSpec ivSpec = new IvParameterSpec(FIXED_IV.getBytes());
	        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
	        byte[] encrypted = cipher.doFinal(data.getBytes());
	        return Base64.getEncoder().encodeToString(encrypted);
		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		} catch (NoSuchPaddingException e)
		{
			e.printStackTrace();
		} catch (InvalidKeyException e)
		{
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }

    public String decryptData(String encryptedData) 
    {
       
		try
		{
			Cipher cipher;
			cipher = Cipher.getInstance(TRANSFORMATION);
			IvParameterSpec ivSpec = new IvParameterSpec(FIXED_IV.getBytes());
	        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
	        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
	        return new String(decrypted);
		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		} catch (NoSuchPaddingException e)
		{
			e.printStackTrace();
		} catch (InvalidKeyException e)
		{
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e)
		{
			e.printStackTrace();
		} catch (IllegalBlockSizeException e)
		{
			e.printStackTrace();
		} catch (BadPaddingException e)
		{
			e.printStackTrace();
		}
       return null;
    }
	    
    private SecretKey convertStringToSecretKey(String secretKeyString) {
        byte[] decodedKey = Base64.getDecoder().decode(secretKeyString);
        return new SecretKeySpec(decodedKey, ALGORITHM);
    }
}
