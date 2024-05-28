package com.cobiscorp.ecobis.ib.orchestration.base.utils.commons;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.util.Base64;

import javax.crypto.SecretKey;

public class JKeyStore
{
	 public String getSecretKeyStringFromKeyStore(String keystorePath, String keystorePassword, String alias) {
	        try {
	            // Cargar el KeyStore
	            FileInputStream fis = new FileInputStream(keystorePath);
	            KeyStore keyStore = KeyStore.getInstance("JCEKS");
	            keyStore.load(fis, keystorePassword.toCharArray());

	            // Obtener la clave secreta utilizando el alias y la contraseña del almacén de claves
	            KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(keystorePassword.toCharArray());
	            KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(alias, protParam);

	            if (secretKeyEntry != null) {
	                SecretKey secretKey = secretKeyEntry.getSecretKey();
	                byte[] secretKeyBytes = secretKey.getEncoded();
	                return Base64.getEncoder().encodeToString(secretKeyBytes);
	            }
	            fis.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	    }

}
