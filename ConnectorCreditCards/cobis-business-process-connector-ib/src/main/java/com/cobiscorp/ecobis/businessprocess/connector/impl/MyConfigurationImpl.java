package com.cobiscorp.ecobis.businessprocess.connector.impl;

import java.util.Properties;

import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.ecobis.businessprocess.connector.Constants;
import com.cobiscorp.ecobis.businessprocess.connector.MyConfiguration;

public class MyConfigurationImpl implements MyConfiguration {
	private static final ILogger logger = LogFactory.getLogger(MyConfigurationImpl.class);
	private static String InfocredUrl;
	private static String InfocredUser;
	private static String InfocredPassword;
	private static String CreditCardUrl;
	private static String CreditCardUser;
	private static String CreditCardPassword;
	//private static Integer InfocredRequestTimeout = 0;
	//private static Integer InfocredConnectTimeout = 0;

	
	@Override
	public void loadConfiguration(IConfigurationReader configurationReader) {
		// Obtener las propiedades
		if (logger.isDebugEnabled()) {
			logger.logDebug("Ingreso a loadConfiguration - MyConfigurationImpl");
		}
		Properties properties = configurationReader.getProperties(Constants.CONFIG_PROPERTIES_PATH);
		
		// Leer cada propiedad
		InfocredUrl = ((String)properties.get("BIC.Url"));
		//value = (String) properties.get("cifin.request-timeout"); if (value != null) { InfocredRequestTimeout = Integer.valueOf(value); }
		//value = (String) properties.get("cifin.connect-timeout"); if (value != null) { InfocredConnectTimeout = Integer.valueOf(value); }
		InfocredUser = ((String)properties.get("BIC.User"));
		InfocredPassword = ((String)properties.get("BIC.Pass"));
		CreditCardUrl = ((String)properties.get("Service1.Url"));
		CreditCardUser = ((String)properties.get("Service1.User"));
		CreditCardPassword = ((String)properties.get("Service1.Pass"));
	}


	public static String getInfocredUrl() {
		return InfocredUrl;
	}

	public static String getInfocredUser() {
		return InfocredUser;
	}

	public static String getInfocredPassword() {
		return InfocredPassword;
	}

	public static String getCreditCardUrl() {
		return CreditCardUrl;
	}

	public static String getCreditCardUser() {
		return CreditCardUser;
	}
	
	public static String getCreditCardPassword() {
		return CreditCardPassword;
	}


	
}
