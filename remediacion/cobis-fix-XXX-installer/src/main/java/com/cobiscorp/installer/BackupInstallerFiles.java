package com.cobiscorp.installer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.izforge.izpack.event.SimpleInstallerListener;
import com.izforge.izpack.installer.AutomatedInstallData;
import com.izforge.izpack.util.AbstractUIProgressHandler;

public class BackupInstallerFiles extends SimpleInstallerListener {

	Logger logger = Logger.getLogger(BackupInstallerFiles.class);

	public void beforePacks(AutomatedInstallData idata, Integer npacks,
			AbstractUIProgressHandler handler) throws Exception {
		super.beforePacks(idata, npacks, handler);
		String cobisHome = idata.getInstallPath().trim();
		this.configureLog(cobisHome);
		// files .jar
		StringBuilder pathPlugins = InstallerUtils.getPathPlugins(cobisHome);
		File directoryPlugins = new File(pathPlugins.toString());
		if (directoryPlugins.exists() && directoryPlugins.isDirectory()) {
			String directoryBackup = pathPlugins.toString() + "_bak";
			File bak = new File(directoryBackup);
			if (bak.exists() && bak.isDirectory()) {
				InstallerUtils.deleteDir(bak);
			}
			InstallerUtils.copyDir(directoryPlugins, bak);
		}
		// copy files services-as .xml
		String pathConfig = InstallerUtils.getPathXmlConfig(cobisHome);
		File directoryConfig = new File(pathConfig);
		if (directoryConfig.exists() && directoryConfig.isDirectory()) {
			String directoryBackupConfig = directoryConfig + "_bak";
			File bakXml = new File(directoryBackupConfig);
			if (bakXml.exists() && bakXml.isDirectory()) {
				InstallerUtils.deleteDir(bakXml);
			}
			InstallerUtils.copyDir(directoryConfig, bakXml);
		}
		// copy files infrasstructure .xml
		String planConfig = InstallerUtils.getPathPlanConfig(cobisHome);
		File filePlanConfig = new File(planConfig);
		if (filePlanConfig.exists()) {
			String backupPlanConfig = filePlanConfig + "_bak";
			File bakXml = new File(backupPlanConfig);
			if (bakXml.exists()) {
				bakXml.delete();
			}
			InstallerUtils.copyFile(filePlanConfig, bakXml);
		}
	}

	private void configureLog(String cobisHome) {
		InputStream inputStream = getClass().getResourceAsStream(
				"/log4j.properties");
		if (inputStream != null) {
			Properties properties = new Properties();
			try {
				properties.load(inputStream);
			} catch (IOException e) {
				logger.error("....::ERROR::....",e);
			}
			properties.put("log4j.appender.file.File", cobisHome
					+ File.separator + "cobis-installer.log");
			PropertyConfigurator.configure(properties);
		} else {
			BasicConfigurator.configure();
		}
		logger.info("home directory [" + cobisHome + "]");
	}
}
