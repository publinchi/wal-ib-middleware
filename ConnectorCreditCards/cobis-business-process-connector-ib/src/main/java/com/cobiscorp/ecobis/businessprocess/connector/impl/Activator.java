package com.cobiscorp.ecobis.businessprocess.connector.impl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.cobiscorp.cobis.plugin.activator.ConfigurationActivator;
import com.cobiscorp.ecobis.businessprocess.connector.MyConfiguration;
import com.cobiscorp.ecobis.businessprocess.connector.Constants;

public class Activator extends ConfigurationActivator {

	private ServiceRegistration<MyConfiguration> serviceRegistration;

	@Override
	protected void loadConfiguration() throws Exception {
		this.registerService(MyConfiguration.class, Constants.PATH_CONFIG_FILE );
	}

	@Override
	protected void continueStart() throws Exception {
		BundleContext context = this.getBundleContext();
		MyConfiguration myConfiguration = new MyConfigurationImpl();
		this.serviceRegistration = context.registerService(MyConfiguration.class, myConfiguration, null);
	}

	@Override
	protected void continueStop() throws Exception {
		this.serviceRegistration.unregister();
	}
}
