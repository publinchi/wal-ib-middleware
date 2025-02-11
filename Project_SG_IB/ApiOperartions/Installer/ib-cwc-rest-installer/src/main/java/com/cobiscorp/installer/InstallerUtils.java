package com.cobiscorp.installer;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public class InstallerUtils {
	public static final Logger logger = Logger.getLogger(InstallerUtils.class);

	public static boolean deleteDir(File dir) {
		try {
			FileUtils.deleteDirectory(dir);
		} catch (IOException e) {
			logger.error(e, e);
			return false;
		}
		return true;
	}

	public static StringBuilder getPathPlugins(String cobisHome) {
		StringBuilder pathPlugins = new StringBuilder();
		pathPlugins.append(cobisHome);
		pathPlugins.append(File.separator);
		pathPlugins.append("cwc/plugins/designer");
		return pathPlugins;
	}

	public static boolean copyDir(File dirSrc, File dirDest) {
		try {
			InstallerUtils.logger.info("destino puede escribir:" + dirDest.canWrite());
			dirDest.setWritable(true);
			FileUtils.copyDirectory(dirSrc, dirDest, true);
		} catch (IOException ex) {
			logger.error(ex, ex);
			return false;
		}
		return true;
	}

	public static boolean copyFile(File srcFile, File destFile) {
		try {
			InstallerUtils.logger.info("destino copyFile:" + destFile);
			FileUtils.copyFile(srcFile, destFile);
		} catch (IOException ex) {
			logger.error(ex, ex);
			return false;
		}
		return true;
	}

	public static String getPathXmlConfig(String cobisHome) {
		StringBuilder pathPlugins = new StringBuilder();
		pathPlugins.append(cobisHome);
		pathPlugins.append(File.separator);
		pathPlugins.append("cwc/services-as/designer");
		return pathPlugins.toString();
	}

	public static String getPathPlanConfig(String cobisHome) {
		StringBuilder pathPlugins = new StringBuilder();
		pathPlugins.append(cobisHome);
		pathPlugins.append(File.separator);
		pathPlugins.append("cwc/infrastructure/cwc-ccm-plan-config.xml");
		return pathPlugins.toString();
	}
}
