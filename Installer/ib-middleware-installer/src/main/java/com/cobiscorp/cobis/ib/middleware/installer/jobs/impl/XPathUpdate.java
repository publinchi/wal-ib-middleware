package com.cobiscorp.cobis.ib.middleware.installer.jobs.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cobiscorp.cobis.ib.middleware.installer.commons.Constants;
import com.cobiscorp.cobis.ib.middleware.installer.validators.exceptions.ValidatorException;

public class XPathUpdate {

	Logger logger = Logger.getLogger(XPathUpdate.class);

	private Document doc = null;
	private XPath xPath = null;
	private static final String DUAL_MODE = "DUAL";	

	public void readDocument(File xmlFile) throws Exception {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		doc = (Document) builder.parse(xmlFile);
		xPath = XPathFactory.newInstance().newXPath();
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param fileConfigurations
	 * @return
	 * @throws JDOMException
	 * @throws IOException
	 */
	public org.jdom.Element getRootElement(InputStream fileConfigurations) throws JDOMException, IOException {
		SAXBuilder builder = new SAXBuilder();
		org.jdom.Document docConf = (org.jdom.Document) builder.build(fileConfigurations);
		return docConf.getRootElement();
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param xmlFilePath
	 * @param fileConfigurations
	 * @param parent
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void editCcmAndClientConfig(String xmlFilePath, InputStream fileConfigurations, String parent) throws Exception {
		File xmlFile = new File(xmlFilePath);
		this.readDocument(xmlFile);

		org.jdom.Element root1 = getRootElement(fileConfigurations);
		org.jdom.Element elementT;
		List<org.jdom.Element> confElement;

		// Ccm Client Config - ccm-client-config
		// Ccm Config - ccm-config

		elementT = root1.getChild(parent);
		confElement = elementT.getChildren("staff");
		Element generalElement = (Element) xPath.evaluate("/" + parent + "/general", doc, XPathConstants.NODE);

		if (null != generalElement) {
			// Remove all plugins
			for (org.jdom.Element element : confElement) {
				String pluginName = element.getAttributeValue("name");
				Node fileNode = (Node) xPath.evaluate("/" + parent + "/general/plugin[@name='" + pluginName + "']", doc, XPathConstants.NODE);
				if (null != fileNode) {
					generalElement.removeChild(fileNode);
				}
			}
			// Add all plugins
			for (org.jdom.Element element : confElement) {
				String pluginName = element.getAttributeValue("name");
				String actualVersion = element.getAttributeValue("version");
				String pathJar = element.getAttributeValue("path") + "/" + pluginName + "-" + actualVersion + ".jar";

				Element pluginElement = doc.createElement("plugin");
				generalElement.appendChild(pluginElement);
				pluginElement.setAttribute("name", pluginName);
				pluginElement.setAttribute("path", pathJar);
			}
		}

		if (this.WriteDoc(doc, xmlFile)) logger.info("======= OK successful upgrade file: \n" + xmlFilePath);
		else logger.error("******* ERROR  failed upgrade file: \n" + xmlFilePath);
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param xmlFilePath
	 * @param fileConfigurations
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void editPlanConfig(String xmlFilePath, InputStream fileConfigurations) throws Exception {
		String planId = "IB";
		File xmlFile = new File(xmlFilePath);
		this.readDocument(xmlFile);

		org.jdom.Element root1 = getRootElement(fileConfigurations);
		org.jdom.Element elementT;
		List<org.jdom.Element> confElement;

		// Plan Config
		elementT = root1.getChild("plan-config");
		confElement = elementT.getChildren("staff");
		Element planIdResult = (Element) xPath.evaluate("/ccm-plan-config/own/plan[@id='" + planId + "']", doc, XPathConstants.NODE);

		if (null == planIdResult) {
			// Create plan id IB
			logger.info("======= OK create plan id: \n" + planId);
			Element ownElement = (Element) xPath.evaluate("/ccm-plan-config/own[last()]", doc, XPathConstants.NODE);
			Comment comment = doc.createComment("Plan IB");
			ownElement.appendChild(comment);
			Element planElement = doc.createElement("plan");
			ownElement.appendChild(planElement);
			planElement.setAttribute("id", planId);

			for (org.jdom.Element element : confElement) {
				String pluginName = element.getAttributeValue("name");
				String actualVersion = element.getAttributeValue("version");
				String jarPath = element.getAttributeValue("path") + "/" + pluginName + "-" + actualVersion + ".jar";

				Element pluginElement = doc.createElement("plugin");
				planElement.appendChild(pluginElement);
				pluginElement.setAttribute("name", pluginName);
				pluginElement.setAttribute("path", jarPath);
			}
		}
		else {
			NodeList nodeList = planIdResult.getChildNodes();
			// Remove all plugins
			for (int i = 0; i < nodeList.getLength(); i++) {
				if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element pluginElement = (Element) nodeList.item(i);
					planIdResult.removeChild(pluginElement);
				}
			}
			// Add all plugins
			logger.info("======= OK edit plan id: \n" + planId);
			for (org.jdom.Element element : confElement) {
				String pluginName = element.getAttributeValue("name");
				String actualVersion = element.getAttributeValue("version");
				String jarPath = element.getAttributeValue("path") + "/" + pluginName + "-" + actualVersion + ".jar";

				Element pluginElement = doc.createElement("plugin");
				planIdResult.appendChild(pluginElement);
				pluginElement.setAttribute("name", pluginName);
				pluginElement.setAttribute("path", jarPath);
			}

		}

		Element ownElement = (Element) xPath.evaluate("/ccm-plan-config/own", doc, XPathConstants.NODE);
		if (null != ownElement) {
			planId = "BUSINESS INTERNET BANKING";
			logger.info("======= OK delete plan id: \n" + planId);
			Element planIdBIBResult = (Element) xPath.evaluate("/ccm-plan-config/own/plan[@id='" + planId + "']", doc, XPathConstants.NODE);
			if (null != planIdBIBResult) {
				ownElement.removeChild(planIdBIBResult);
			}
		}

		// Channel Commons
		String planIdChannel = "ChannelAdmCommons";
		logger.info(">>>> Create plan id planIdChannel " + planIdChannel);

		logger.info(">>>> Create plan id root1Channel " + root1);

		// Plan Config
		elementT = root1.getChild("adm-channels");
		confElement = elementT.getChildren("staff");
		Element planIdResultChannel = (Element) xPath.evaluate("/ccm-plan-config/own/plan[@id='" + planIdChannel + "']", doc, XPathConstants.NODE);

		if (null == planIdResultChannel) {
			logger.info(">>>> Create plan id ChannelAdmCommons");
			// Create plan id ChannelAdmCommons
			logger.info("======= OK create plan id: \n" + planIdChannel);
			Element ownElementChannel = (Element) xPath.evaluate("/ccm-plan-config/own[last()]", doc, XPathConstants.NODE);
			Comment comment = doc.createComment("Plan ChannelAdmCommons");
			ownElementChannel.appendChild(comment);
			Element planElement = doc.createElement("plan");
			ownElementChannel.appendChild(planElement);
			planElement.setAttribute("id", planIdChannel);

			for (org.jdom.Element element : confElement) {
				String pluginName = element.getAttributeValue("name");
				String actualVersion = element.getAttributeValue("version");
				String jarPath = element.getAttributeValue("path") + "/" + pluginName + "-" + actualVersion + ".jar";

				Element pluginElement = doc.createElement("plugin");
				planElement.appendChild(pluginElement);
				pluginElement.setAttribute("name", pluginName);
				pluginElement.setAttribute("path", jarPath);
			}
		}
		else {
			NodeList nodeList = planIdResultChannel.getChildNodes();
			// Remove all plugins
			for (int i = 0; i < nodeList.getLength(); i++) {
				if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element pluginElement = (Element) nodeList.item(i);
					planIdResultChannel.removeChild(pluginElement);
				}
			}
			// Add all plugins
			logger.info("======= OK edit plan id: \n" + planIdChannel);
			for (org.jdom.Element element : confElement) {
				String pluginName = element.getAttributeValue("name");
				String actualVersion = element.getAttributeValue("version");
				String jarPath = element.getAttributeValue("path") + "/" + pluginName + "-" + actualVersion + ".jar";

				Element pluginElement = doc.createElement("plugin");
				planIdResultChannel.appendChild(pluginElement);
				pluginElement.setAttribute("name", pluginName);
				pluginElement.setAttribute("path", jarPath);
			}

		}
		if (this.WriteDoc(doc, xmlFile)) logger.info("======= OK successful upgrade file: \n" + xmlFilePath);
		else logger.error("******* ERROR  failed upgrade file: \n" + xmlFilePath);
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param xmlFilePath
	 * @param fileConfigurations
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void editCisClient(String xmlFilePath, InputStream fileConfigurations) throws Exception {
		File xmlFile = new File(xmlFilePath);
		this.readDocument(xmlFile);

		org.jdom.Element root1 = getRootElement(fileConfigurations);
		org.jdom.Element elementT;
		List<org.jdom.Element> confElement;

		// Java Orchestrations
		Element tranResult = (Element) xPath.evaluate("/ccm-client-config/general", doc, XPathConstants.NODE);
		if (null != tranResult) {
			// Remove all JARs
			logger.info("======= Remove configurations ");
			elementT = root1.getChild("orchestrations");
			confElement = elementT.getChildren("staff");
			for (org.jdom.Element element : confElement) {
				String pluginName = element.getAttributeValue("name");
				Element pluginResult = (Element) xPath.evaluate("/ccm-client-config/general/plugin[@name='" + pluginName + "']", doc, XPathConstants.NODE);
				if (null != pluginResult) tranResult.removeChild(pluginResult);
			}

			elementT = root1.getChild("deleteOrchestrations");
			confElement = elementT.getChildren("staff");
			for (org.jdom.Element element : confElement) {
				String pluginName = element.getAttributeValue("name");
				Element pluginResult = (Element) xPath.evaluate("/ccm-client-config/general/plugin[@name='" + pluginName + "']", doc, XPathConstants.NODE);
				if (null != pluginResult) tranResult.removeChild(pluginResult);
			}

			// Add all JARs
			logger.info("======= Add configurations ");
			elementT = root1.getChild("orchestrations");
			confElement = elementT.getChildren("staff");
			for (org.jdom.Element element : confElement) {
				String pluginName = element.getAttributeValue("name");
				String actualVersion = element.getAttributeValue("version");
				String jarPath = "../../plugins/IBOrchestration/" + pluginName + "-" + actualVersion + ".jar";
				// Create plugin
				Element newPlugin = doc.createElement("plugin");
				newPlugin.setAttribute("name", pluginName);
				newPlugin.setAttribute("path", jarPath);
				tranResult.appendChild(newPlugin);
			}
		}

		if (this.WriteDoc(doc, xmlFile)) logger.info("======= OK successful upgrade file: \n" + xmlFilePath);
		else logger.error("******* ERROR  failed upgrade file: \n" + xmlFilePath);

	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param jarsFilesPath
	 * @param fileConfigurations
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> backupJarCisClient(String jarsFilesPath, InputStream fileConfigurations) throws Exception {
		Map<String, String> mapFiles = new HashMap<String, String>();
		org.jdom.Element root1 = getRootElement(fileConfigurations);
		org.jdom.Element elementT;
		List<org.jdom.Element> confElement;

		elementT = root1.getChild("deleteOrchestrations");
		confElement = elementT.getChildren("staff");

		// Backup Java Orchestrations
		for (org.jdom.Element element : confElement) {
			String fileName = element.getAttributeValue("name");
			String sourceFilePath = jarsFilesPath.concat(fileName.concat("-".concat(element.getAttributeValue("version").concat(".jar"))));
			File backupFile = new File(sourceFilePath);
			if (null != backupFile) {
				// Delete old backups files
				deleteOldBackupFiles(jarsFilesPath, fileName);
				String backupFilePath = getBackupFilePath(backupFile, ".jar");
				if (null != backupFilePath) mapFiles.put(sourceFilePath, backupFilePath);
			}
		}
		return mapFiles;
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param mapFiles
	 */
	public void deleteJarCisClient(Map<String, String> mapFiles) {
		logger.info("======= START Delete Jars Files");
		Iterator<String> keySetIterator = mapFiles.keySet().iterator();
		while (keySetIterator.hasNext()) {
			String sourceFilePath = keySetIterator.next();
			File deleteSourceFile = new File(sourceFilePath);
			if (null != deleteSourceFile) deleteFile(deleteSourceFile);
		}

		logger.info("======= FINISH Delete Jars Files");
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param mapFiles
	 */
	public void restoreJarCisClient(Map<String, String> mapFiles) {
		logger.info("======= START Restore Jars Files");

		Iterator<String> keySetIterator = mapFiles.keySet().iterator();

		while (keySetIterator.hasNext()) {
			String sourceFilePath = keySetIterator.next();
			String backupFilePath = mapFiles.get(sourceFilePath);

			logger.info("======= OK backupFilePath: \n" + backupFilePath);
			logger.info("======= OK sourceFilePath: \n" + sourceFilePath);

			restoreFile(sourceFilePath, backupFilePath);
		}

		logger.info("======= FINISH Restore Jars Files");
	}

	/**
	 * Patron de busqueda de version [0-9].[0-9].[0-9]*[0-9]
	 * 
	 * @author smejia Sandra Mejia J.
	 * @param jarName
	 * @param requiredVersion
	 * @return
	 */
	public boolean getUpdateVersion(String jarName, Integer requiredVersion) {
		Integer oldVersion = null;
		String version = null;
		boolean updateVersion = false;
		Pattern pattern = Pattern.compile("[0-9].[0-9].[0-9]*[0-9]");
		Matcher matcher = pattern.matcher(jarName);

		if (matcher.find()) {
			version = matcher.group(0);
			oldVersion = Integer.valueOf(version.replace(".", ""));
			if (oldVersion < requiredVersion) updateVersion = true;
		}
		return updateVersion;
	}

	/**
	 * @author smejia Optimizacion a metodo existente
	 * @param xmlFilePath
	 * @throws Exception
	 */
	public void verifyMasterOrchestrator(String xmlFilePath) throws Exception {
		File xmlFile = new File(xmlFilePath);
		this.readDocument(xmlFile);
		String message = "======= OK CIS Configuration File master-orchestrator-config.xml: \n" + xmlFilePath;

		if (!(Boolean) xPath.evaluate("/Configuration/config[@name='BVExecutor']", doc, XPathConstants.BOOLEAN)) {
			message = "******* ERROR CIS Configuration File master-orchestrator-config.xml: \n" + xmlFilePath + "\nContact to System Administrator.";
			logger.error(message);			
			throw new ValidatorException(getError(Constants.ERROR_18040));
		}
		else {
			logger.info(message);
		}
	}

	/**
	 * @author smejia Optimizacion metodo existente
	 * @param xmlFilePath
	 * @throws Exception
	 */
	public void verifyCspCcmConfig(String xmlFilePath) throws Exception {
		File xmlFile = new File(xmlFilePath);
		this.readDocument(xmlFile);
		String message = "======= OK CIS Configuration File csp-ccm-config.xml: \n"
				+ xmlFilePath;

		// DynamicOrchestrator
		/*if (!(Boolean) xPath.evaluate(
				"/ccm-config/general/plugin[@name='DynamicOrchestrator']", doc,
				XPathConstants.BOOLEAN)) {
			
			message = "******* ERROR CIS Configuration File csp-ccm-config.xml:\n"
					+ xmlFilePath
					+ "\n No exists plugin with property DynamicOrchestrator \nContact to System Administrator.";
			logger.error(message);
			throw new ValidatorException(message);
		} */
		// BVExecutor 
		if (!(Boolean) xPath.evaluate(
				"/ccm-config/general/plugin[@name='BVExecutor']", doc,
				XPathConstants.BOOLEAN)) {
			message = "******* ERROR CIS Configuration File csp-ccm-config.xml:\n"
					+ xmlFilePath
					+ "\n No exists plugin with property BVExecutor \nContact to System Administrator.";
			logger.error(message);	
			throw new ValidatorException(getError(Constants.ERROR_18041));			
		}
		
		// CTSAuthorization
		else if (!(Boolean) xPath.evaluate(
				"/ccm-config/general/plugin[@name='CTSAuthorization']", doc,
				XPathConstants.BOOLEAN)) {
			message = "******* ERROR CIS Configuration File csp-ccm-config.xml:\n"
					+ xmlFilePath
					+ "No exists plugin with property CTSAuthorization \nContact to System Administrator.";
			logger.error(message);
			throw new ValidatorException(getError(Constants.ERROR_18041));
		} 
		// CTSOrchestrator
		else if (!(Boolean) xPath.evaluate(
				"/ccm-config/general/plugin[@name='CTSOrchestrator']", doc,
				XPathConstants.BOOLEAN)) {
			message = "******* ERROR CIS Configuration File csp-ccm-config.xml:\n"
					+ xmlFilePath
					+ "No exists plugin with property CTSOrchestrator \nContact to System Administrator.";
			logger.error(message);
			throw new ValidatorException(getError(Constants.ERROR_18041));
		} else {
			logger.info(message);
		}	
	}
	
	/** Check plugins in cts-ccm-config.xml file
	 * @param xmlFilePath
	 * @throws Exception
	 */
	public void verifyCtsCcmConfig(String xmlFilePath) throws Exception {
		File xmlFile = new File(xmlFilePath);
		this.readDocument(xmlFile);
		String message = "======= OK CIS Configuration File cts-ccm-config.xml: \n"
				+ xmlFilePath;

		// CTSOrchestrator
		if (!(Boolean) xPath.evaluate(
				"/ccm-config/general/plugin[@name='CTSOrchestrator']", doc,
				XPathConstants.BOOLEAN)) {
			
			message = "******* ERROR CIS Configuration File cts-ccm-config.xml:\n"
					+ xmlFilePath
					+ "\n No exists plugin with property CTSOrchestrator \nContact to System Administrator.";
			logger.error(message);
			throw new ValidatorException(getError(Constants.ERROR_18042));
		} else {
			logger.info(message);
		}
	}
	
	/**
	 * Check plugins in integration-service-config.xml file
	 * 
	 * @param xmlFilePath
	 * @throws Exception
	 */
	public void verifyCtsIntegrationServiceConfig(String xmlFilePath)
			throws Exception {
		File xmlFile = new File(xmlFilePath);
		this.readDocument(xmlFile);
		String message = "======= OK CIS Configuration File integration-service-config.xml: \n"
				+ xmlFilePath;

		// Search Own Label
		if (!(Boolean) xPath.evaluate("/config/own", doc,
				XPathConstants.BOOLEAN)) {

			message = "******* ERROR CIS Configuration File integration-service-config.xml:\n"
					+ xmlFilePath
					+ "\n No exists Own Label \nContact to System Administrator.";
			logger.error(message);
			throw new ValidatorException(getError(Constants.ERROR_18043));
		} else {
			logger.info(message);
		}
	}

	/**
	 * Check plugins in socket-ssn-config.xml file
	 * 
	 * @param xmlFilePath
	 * @throws Exception
	 */
	public void verifySocketSsnConfig(String xmlFilePath) throws Exception {
		File xmlFile = new File(xmlFilePath);
		this.readDocument(xmlFile);
		String message = "======= OK CIS Configuration File socket-ssn-config.xml: \n"
				+ xmlFilePath;

		// Search Server Label with default = true attribute
		if (!(Boolean) xPath.evaluate("/config/own/server[@default='true']",
				doc, XPathConstants.BOOLEAN)) {

			message = "****** ERROR CIS Configuration File socket-ssn-config.xml:\n"
					+ xmlFilePath
					+ "\n No exists Ssn Server with property default = true \nContact to System Administrator.";
			logger.error(message);
			throw new ValidatorException(getError(Constants.ERROR_18044));
		}
		// inSocketQueue Label
		else if (!(Boolean) xPath.evaluate(
				"/config/own/server[@default='true']/inSocketQueue", doc,
				XPathConstants.BOOLEAN)) {

			message = "******* ERROR CIS Configuration File socket-ssn-config.xml:\n"
					+ xmlFilePath
					+ "\n No exists inSocketQueue label in Ssn Server \nContact to System Administrator.";
			logger.error(message);
			throw new ValidatorException(getError(Constants.ERROR_18044));
		}
		// outSocketQueue Label
		else if (!(Boolean) xPath.evaluate(
				"/config/own/server[@default='true']/outSocketQueue", doc,
				XPathConstants.BOOLEAN)) {

			message = "******* ERROR CIS Configuration File socket-ssn-config.xml:\n"
					+ xmlFilePath
					+ "\n No exists property outSocketQueue in Ssn Server \nContact to System Administrator.";
			logger.error(message);
			throw new ValidatorException(getError(Constants.ERROR_18044));
		}
		// socketConnection Label
		else if (!(Boolean) xPath.evaluate(
				"/config/own/server[@default='true']/socketConnection", doc,
				XPathConstants.BOOLEAN)) {

			message = "******* ERROR CIS Configuration File socket-ssn-config.xml:\n"
					+ xmlFilePath
					+ "\n No exists property socketConnection in Ssn Server \nContact to System Administrator.";
			logger.error(message);
			throw new ValidatorException(getError(Constants.ERROR_18044));
		} else {
			logger.info(message);
		}
	}
	
	/**
	 * @author smejia Sandra Mejia J.
	 * @param jarName
	 * @return
	 */
	public Integer getJarVersion(String jarName) {
		String version = null;
		Integer versionNumber = null;
		Pattern pattern = Pattern.compile("[0-9].[0-9].[0-9]*[0-9]");
		Matcher matcher = pattern.matcher(jarName);

		if (matcher.find()) {
			version = matcher.group(0);
			versionNumber = Integer.valueOf(version.replace(".", ""));
		}
		return versionNumber;
	}

	/**
	 * @author smejia Optimizacion metodo existente
	 * @param xmlFilePath
	 * @throws Exception
	 */
	public void editSessionManager(String xmlFilePath) throws Exception {

		File xmlFile = new File(xmlFilePath);
		this.readDocument(xmlFile);

		Element result = (Element) xPath.evaluate("/config/own/enable-anonymous-user", doc, XPathConstants.NODE);
		if (null != result) {
			result.setTextContent("true");
		}
		else {
			result = (Element) xPath.evaluate("/config/own", doc, XPathConstants.NODE);
			Element newElement = doc.createElement("enable-anonymous-user");
			newElement.setTextContent("true");
			result.appendChild(newElement);
		}

		if (this.WriteDoc(doc, xmlFile)) logger.info("======= OK successful upgrade file: \n" + xmlFilePath);
		else logger.error("******* ERROR  failed upgrade file: \n" + xmlFilePath);

	}

	/**
	 * @author smejia Optimizacion metodo existente
	 * @param xmlFilePath
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void editCtstransformation(String xmlFilePath, InputStream fileConfigurations) throws Exception {

		File xmlFile = new File(xmlFilePath);
		this.readDocument(xmlFile);

		org.jdom.Element root1 = getRootElement(fileConfigurations);
		org.jdom.Element elementT;
		List<org.jdom.Element> confElement;

		elementT = root1.getChild("ctstransformation");
		confElement = elementT.getChildren("staff");
		Element result = (Element) xPath.evaluate("config/own/ctstransformation-files", doc, XPathConstants.NODE);
		if (null != result) {
			for (org.jdom.Element element : confElement) {
				String name = element.getAttributeValue("name");
				if (!(Boolean) xPath.evaluate("config/own/ctstransformation-files/file[@path='" + name + "']", doc, XPathConstants.BOOLEAN)) {
					Element newElement = doc.createElement("file");
					newElement.setAttribute("path", name);
					result.appendChild(newElement);
				}
			}
		}

		if (this.WriteDoc(doc, xmlFile)) logger.info("======= OK successful upgrade file: \n" + xmlFilePath);
		else logger.error("******* ERROR  failed upgrade file: \n" + xmlFilePath);
	}
	
	/**
	 * @author smejia Sandra Mejia J.
	 * @param mapFiles
	 */
	public void deleteTransactionsAndTemplatesFiles(Map<String, String> mapFiles) {
		logger.info("======= START Delete Transactions And Templates Files");

		Iterator<String> keySetIterator = mapFiles.keySet().iterator();
		while (keySetIterator.hasNext()) {
			String pathSourceFile = keySetIterator.next();
			File deleteSourceFile = new File(pathSourceFile);
			if (null != deleteSourceFile) deleteFile(deleteSourceFile);
		}

		logger.info("======= FINISH Delete Transactions And Templates Files");
	}

	
	/**
	 * @author smejia Sandra Mejia J.
	 * @param mapFiles
	 * @throws Exception
	 */
	public void restoreTransactionsAndTemplatesFiles(Map<String, String> mapFiles) {
		logger.info("======= START Restore Transactions And Templates Files");

		Iterator<String> keySetIterator = mapFiles.keySet().iterator();

		while (keySetIterator.hasNext()) {
			String sourceFilePath = keySetIterator.next();
			String backupFilePath = mapFiles.get(sourceFilePath);

			logger.info("======= OK backupFilePath: \n" + backupFilePath);
			logger.info("======= OK sourceFilePath: \n" + sourceFilePath);

			restoreFile(sourceFilePath, backupFilePath);
		}

		logger.info("======= FINISH Restore Transactions And Templates Files");
	}

	public boolean WriteDoc(Document doc, File xmlFile) {

		try {
			OutputFormat format = new OutputFormat(doc);
			format.setIndenting(true);
			// format.setIndent(4);
			Writer out = new StringWriter();
			XMLSerializer serializer = new XMLSerializer(out, format);
			serializer.serialize(doc);
			String xmlString = out.toString();
			FileOutputStream fwriter = new FileOutputStream(xmlFile);
			fwriter.write(xmlString.getBytes());
			fwriter.flush();
			fwriter.close();
			return true;
		}
		catch (Exception e) {
			return false;
		}
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param file
	 */
	public void deleteFile(File file) {
		if (file.exists()) {
			file.delete();
			logger.info("======= OK file deleted: " + file.getName());
		}
		else {
			logger.warn("******* WARNING the file does not exist: [" + file.getName() + "], to be removed");
		}
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param sourceFilePath
	 * @param destinationFilePath
	 */
	public void copyFile(String sourceFilePath, String destinationFilePath) {
		try {
			File f1 = new File(sourceFilePath);
			File f2 = new File(destinationFilePath);
			InputStream in = new FileInputStream(f1);
			OutputStream out = new FileOutputStream(f2);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			logger.info("======= OK successful copy source file: \n" + sourceFilePath);
			logger.info("======= OK successful copy destination file: \n" + destinationFilePath);
		}
		catch (FileNotFoundException ex) {
			logger.error("******* ERROR  failed copy source file: \n" + sourceFilePath);
			logger.error("******* ERROR  failed copy destination file: \n" + destinationFilePath);
			logger.error("******* ERROR  Message: \n" + ex.getMessage());
		}
		catch (IOException e) {
			logger.error("******* ERROR  failed copy source file: \n" + sourceFilePath);
			logger.error("******* ERROR  failed copy destination file: \n" + destinationFilePath);
			logger.error("******* ERROR  Message: \n" + e.getMessage());
		}
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param sourceFile
	 * @param extension
	 * @return
	 */
	public String getBackupFilePath(File sourceFile, String extension) {
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMMdd-HH-mm-ss");
		String now = formatter.format(currentDate.getTime());
		String backupFilePath = null;

		if (sourceFile.exists()) {
			String backupName = sourceFile.getName().replace(extension, "").concat("-".concat(now.concat(extension)));
			backupFilePath = sourceFile.getPath().replace(sourceFile.getName(), backupName);

			logger.info("======= OK sourceFile: [" + sourceFile.getName() + "]");
			logger.info("======= OK backupName: [" + backupName + "]");
			logger.info("======= OK backupFilePath: \n" + backupFilePath);

			copyFile(sourceFile.getAbsolutePath(), backupFilePath);
		}
		else {
			logger.warn("******* WARNING the file does not exist: [" + sourceFile.getName() + "], to be copied");
		}

		return backupFilePath;
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param sourceFilePath
	 * @param backupFilePath
	 */
	public void restoreFile(String sourceFilePath, String backupFilePath) {
		logger.info("======= OK backupFilePath: \n" + backupFilePath);
		logger.info("======= OK sourceFilePath: \n" + sourceFilePath);

		copyFile(backupFilePath, sourceFilePath);
		File deleteBackupFile = new File(backupFilePath);
		if (null != deleteBackupFile) deleteFile(deleteBackupFile);
	}

	/**
	 * Returns CTS Version
	 * 
	 * @author smejia Sandra Mejia J.
	 * @param cobisHome
	 * @return
	 * @throws IOException
	 */
	public Integer getCtsVersion(String cobisHome) throws IOException {
		Integer ctsVersion = null;
		// CTS version
		logger.info("======= START CTS Version =======");
		BufferedReader bf = new BufferedReader(new FileReader(cobisHome.concat("/CTS_MF/version.txt")));
		String version = bf.readLine();
		logger.info("======= CTS Version: " + version);
		String completeVersion = "";
		StringTokenizer tokens = new StringTokenizer(version, ".");
		int lengthToken = tokens.countTokens();
		String[] token = new String[lengthToken];
		int i = 0;
		while (tokens.hasMoreTokens()) {
			String str = tokens.nextToken();
			token[i] = str;
			logger.info("=======Token: " + token[i]);
			if (i <= 4) {
				completeVersion += str;
			}
			i++;
		}		
		int versionLength =  completeVersion.length();		
		if (completeVersion.length() < 4) {
			for (int j = 0; j < (4 - versionLength); j++) {
				completeVersion += "0";
			}
		}
		//completeVersion = token[0] + token[1] + token[2] + token[3];		
		
		bf.close();
		ctsVersion = Integer.parseInt(completeVersion);
		logger.info("======= Short CTS Version: " + ctsVersion);
		logger.info("======= FINISH CTS Version =======");

		return ctsVersion;
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param xmlFilePath
	 * @return
	 * @throws Exception
	 */
	public boolean getReviewBufferSize(String xmlFilePath) throws Exception {
		boolean bufferSize = true;
		File xmlFile = new File(xmlFilePath);
		this.readDocument(xmlFile);
		String message = "======= OK CTS spexecutor-service file: \n" + xmlFilePath;

		Element eBufferSize = (Element) xPath.evaluate("/config/own/bufferSize", doc, XPathConstants.NODE);

		if (null != eBufferSize) {
			Integer bSize = Integer.valueOf(eBufferSize.getTextContent().toString());
			if (bSize < 200000) {
				message = "CTS spexecutor-service file:\n" + xmlFilePath + "\nContact to System Administrator.";
				message = message.concat("\nThe buffer size is: " + bSize);
				logger.error(message);
				bufferSize = false;
			}
			else {
				message = message.concat("\nThe buffer size is: " + bSize);
				logger.info(message);
			}
		}
		return bufferSize;
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param xmlFilePath
	 * @param fileConfigurations
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void verifyCisClientConfig(String xmlFilePath, InputStream fileConfigurations) throws Exception {
		File xmlFile = new File(xmlFilePath);
		this.readDocument(xmlFile);
		String message = "======= OK CIS Client Configuration File csp-ccm-client-config.xml: \n" + xmlFilePath;

		org.jdom.Element root1 = getRootElement(fileConfigurations);
		org.jdom.Element elementT;
		List<org.jdom.Element> confElement;

		Element tranResult = (Element) xPath.evaluate("/ccm-client-config/general", doc, XPathConstants.NODE);
		if (null != tranResult) {
			// Review prerequisite JARs
			logger.info("======= Review prerequisite configurations ");
			elementT = root1.getChild("preRequisite");
			confElement = elementT.getChildren("staff");

			for (org.jdom.Element element : confElement) {
				String pluginName = element.getAttributeValue("name");
				if (!(Boolean) xPath.evaluate("/ccm-client-config/general/plugin[@name='" + pluginName + "']", doc, XPathConstants.BOOLEAN)) {
					message = "******* ERROR CIS Client Configuration File csp-ccm-client-config.xml: \n" + xmlFilePath + "\n. Contact to System Administrator.";
					message = message + "\n Prerequisite not found: " + pluginName;
					logger.error(message);
					throw new ValidatorException(getError(Constants.ERROR_18045));
				}
				else {
					logger.info(message);
				}
			}
		}

	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param backupFilePath
	 * @param fileName
	 */
	public void deleteOldBackupFiles(String backupFilePath, String fileName) {
		File filePath = new File(backupFilePath);
		logger.info("======= Delete old backups files");
		if (filePath.exists()) {
			for (File file : filePath.listFiles()) {
				if (file.getName().indexOf("atm")==0){
					if (file.getName().startsWith(fileName.concat("-"))) {
						logger.info(file.getName());
						deleteFile(file);
					}
				}
			}
		}
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param xmlFilePath
	 * @throws Exception
	 */
	public void verifyReentryConfig(String xmlFilePath) throws Exception {
		File xmlFile = new File(xmlFilePath);
		this.readDocument(xmlFile);
		String message = "======= OK Reentry Configuration in File reentry-immediate-service-config.xml: \n" + xmlFilePath;

		Element element = (Element) xPath.evaluate("/config/own/instances/reentry/filter", doc, XPathConstants.NODE);

		if (null != element) {
			logger.info("/config/own/instances/reentry/filter: " + element.getTextContent());
			if (element.getTextContent().isEmpty()) {
				message = "******* ERROR Reentry Configuration in File reentry-immediate-service-config.xml:\n" + xmlFilePath + "\n. Contact to System Administrator.";
				logger.error(message);
				throw new ValidatorException(getError(Constants.ERROR_18046));
			}
			else logger.info(message);
		}
		else {
			message = "******* ERROR Reentry Configuration in File reentry-immediate-service-config.xml:\n" + xmlFilePath + "\n. Contact to System Administrator.";
			logger.error(message);
			throw new ValidatorException(getError(Constants.ERROR_18046));
		}
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param xmlFilePath
	 * @param fileConfigurations
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void verifyReentryPlanConfig(String xmlFilePath, InputStream fileConfigurations) throws Exception {
		File xmlFile = new File(xmlFilePath);
		this.readDocument(xmlFile);
		String message = "======= OK Review Reentry Configuration in File cts-ccm-plan-config.xml: \n" + xmlFilePath;
		String planId = "Reentry";

		org.jdom.Element root1 = getRootElement(fileConfigurations);
		org.jdom.Element elementT;
		List<org.jdom.Element> confElement;

		// Reentry Plan Config
		elementT = root1.getChild("reentry");
		confElement = elementT.getChildren("staff");
		Element planIdResult = (Element) xPath.evaluate("/ccm-plan-config/own/plan[@id='" + planId + "']", doc, XPathConstants.NODE);
		logger.info(message);
		if (null != planIdResult) {
			for (org.jdom.Element element : confElement) {
				String pluginName = element.getAttributeValue("name");
				if (!(Boolean) xPath.evaluate("/ccm-plan-config/own/plan/plugin[@name='" + pluginName + "']", doc, XPathConstants.BOOLEAN)) {
					message = "******* ERROR The attribute: [" + pluginName + "] does not exist in file cts-ccm-plan-config.xml:\n" + xmlFilePath + "\n. Contact to System Administrator.";
					logger.error(message);
					throw new ValidatorException(getError(Constants.ERROR_18047));
				}
				else {
					message = "======= OK Attribute: " + pluginName;
					logger.info(message);
				}
			}
		}
		else {
			message = "******* ERROR The planId: [" + planId + "] does not exist in file cts-ccm-plan-config.xml:\n" + xmlFilePath + "\n. Contact to System Administrator.";
			logger.error(message);
			throw new ValidatorException(getError(Constants.ERROR_18048));
		}
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param xmlFilePath
	 * @param newDbmsName
	 * @throws Exception
	 */
	public void editDbmsName(String xmlFilePath, String newDbmsName) throws Exception {
		File xmlFile = new File(xmlFilePath);
		this.readDocument(xmlFile);
		String message = "======= OK Session Manager Configuration in File: \n" + xmlFilePath;

		Element element = (Element) xPath.evaluate("/config/general/dbms/dbms-name", doc, XPathConstants.NODE);

		if (null != element) {
			String dbmsName = element.getTextContent();

			logger.info("/config/general/dbms/dbms-name: " + dbmsName);

			if (dbmsName.isEmpty()) {
				element.setTextContent(newDbmsName);
				logger.info(message);
			}
			else {
				if (!dbmsName.equals(newDbmsName)) {
					element.setTextContent(newDbmsName);
					logger.info(message);
				}
				else logger.info(message);
			}
		}
		else {
			message = "******* ERROR Session Manager Configuration in File:\n" + xmlFilePath + "\n. Contact to System Administrator.";
			logger.error(message);
			throw new ValidatorException(getError(Constants.ERROR_18049));
		}

		if (this.WriteDoc(doc, xmlFile)) logger.info("======= OK successful upgrade file: \n" + xmlFilePath);
		else logger.error("******* ERROR  failed upgrade file: \n" + xmlFilePath);
	}
	
	
	/**
	 * @author smejia Sandra Mejia J.
	 * @param xmlFilePath
	 * @param newDbmsName
	 * @throws Exception
	 */
		public void editServices(String xmlFilePath, String ibServices)
	            throws Exception {
	     File xmlFile = new File(xmlFilePath);
	     Boolean existe = false;
	     this.readDocument(xmlFile);
	     String message = "======= OK Session Manager Configuration in File: \n"
	                   + xmlFilePath;
	
	     xPath.setNamespaceContext(new NamespaceContext() {
	            public String getNamespaceURI(String prefix) {
	                   // if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
	                   return "http://www.example.org/services";
	
	                   // }else
	                   // return XMLConstants.NULL_NS_URI;
	            }
	
	            public String getPrefix(String namespaceURI) {
	                   if (namespaceURI.equals("http://www.example.org/services")) {
	                          return "srv";
	                   }
	
	                   return null;
	            }
	
	            public Iterator<String> getPrefixes(String namespaceURI) {
	                   ArrayList<String> list = new ArrayList<String>();
	
	                   if (namespaceURI.equals("http://www.example.org/services")) {
	                          list.add("srv");
	                   }
	
	                   return list.iterator();
	            }
	     });
	     Element element = (Element) xPath.evaluate("//srv:services", doc,
	                   XPathConstants.NODE);
	
	     if (null != element) {
	
	            NodeList children = element.getChildNodes();
	            Node current = null;
	            int count = children.getLength();
	
	            for (int i = 1; i < count; i++) {
	                   current = children.item(i);
	                   if (current.getNodeName().equals("import")
	                                && current.getAttributes().getNamedItem("file")
	                                              .getTextContent().equals(ibServices)) {
	                          existe = true;
	                   }
	
	            }
	
	            // Boolean imp = (Boolean) xPath.evaluate(
	            // "//srv:services/import[@file='" + ibServices + "']", doc,
	            // XPathConstants.BOOLEAN);
	            if (!existe) {
	
	                   logger.info(message);
	                   Element importElement = doc.createElement("import");
	                   element.appendChild(importElement);
	
	                   importElement.setAttribute("file", ibServices);
	
	            }
	     }
	
	     if (this.WriteDoc(doc, xmlFile))
	            logger.info("======= OK successful upgrade file: \n" + xmlFilePath);
	     else
	            logger.error("******* ERROR  failed upgrade file: \n" + xmlFilePath);
	}


	
	/**
	 * @author smejia Sandra Mejia J.
	 * @param xmlFilePath
	 * @param newDbmsName
	 * @throws Exception
	 */
	public void editMasterCts(String xmlFilePath, String newDbmsName) throws Exception {
		File xmlFile = new File(xmlFilePath);
		this.readDocument(xmlFile);
		String message = "======= OK Session Manager Configuration in File: \n" + xmlFilePath;

		Element element = (Element) xPath.evaluate("/config/general/dbms/dbms-name", doc, XPathConstants.NODE);

		if (null != element) {
			String dbmsName = element.getTextContent();

			logger.info("/config/general/dbms/dbms-name: " + dbmsName);

			if (dbmsName.isEmpty()) {
				element.setTextContent(newDbmsName);
				logger.info(message);
			}
			else {
				if (!dbmsName.equals(newDbmsName)) {
					element.setTextContent(newDbmsName);
					logger.info(message);
				}
				else logger.info(message);
			}
		}
		else {
			message = "******* ERROR Session Manager Configuration in File:\n" + xmlFilePath + "\n. Contact to System Administrator.";
			logger.error(message);			
			throw new ValidatorException(getError(Constants.ERROR_18050));
		}

		if (this.WriteDoc(doc, xmlFile)) logger.info("======= OK successful upgrade file: \n" + xmlFilePath);
		else logger.error("******* ERROR  failed upgrade file: \n" + xmlFilePath);
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param xmlFilePath
	 * @throws Exception
	 */
	public void editDualMode(String xmlFilePath) throws Exception {
		File xmlFile = new File(xmlFilePath);
		this.readDocument(xmlFile);
		String message = "======= OK Review DUAL Mode in File: \n" + xmlFilePath;

		Element element = (Element) xPath.evaluate("/config/own/mode", doc, XPathConstants.NODE);

		if (null != element) {
			String mode = element.getTextContent();

			logger.info("/config/own/mode: " + mode);

			if (mode.isEmpty()) {
				element.setTextContent(DUAL_MODE);
				logger.info(message);
			}
			else {
				if (!mode.equals(DUAL_MODE)) {
					element.setTextContent(DUAL_MODE);
					logger.info(message);
				}
				else logger.info(message);
			}
		}
		else {
			message = "******* ERROR Review DUAL Mode in File:\n" + xmlFilePath + "\n. Contact to System Administrator.";
			logger.error(message);			
			throw new ValidatorException(getError(Constants.ERROR_18051));			
		}

		if (this.WriteDoc(doc, xmlFile)) logger.info("======= OK successful upgrade file: \n" + xmlFilePath);
		else logger.error("******* ERROR  failed upgrade file: \n" + xmlFilePath);
	}
	
	/**
	 * @author smejia Sandra Mejia J.
	 * @param xmlFilePath
	 * @param name
	 * @param value
	 * @throws Exception
	 */
	public void editCobisSessionManager(String xmlFilePath, String name, String value) throws Exception {
		File xmlFile = new File(xmlFilePath);
		this.readDocument(xmlFile);
		String message = "======= OK Session Manager Configuration in File: \n" + xmlFilePath;

		Element element = (Element) xPath.evaluate("/config/own", doc, XPathConstants.NODE);

		if (null != element) {
			Node sessionAttribute = (Node) xPath.evaluate("/config/own/session-attribute[@dataType='varchar' and @name='" + name + "']", doc, XPathConstants.NODE);
			if (null != sessionAttribute) {
				String sessionValue = sessionAttribute.getTextContent();
				logger.info(message);
				logger.info("/config/own/session-attribute: " + sessionValue);
				sessionAttribute.setTextContent(value);
			}
			else {
				logger.info(message);
				Element sessionElement = doc.createElement("session-attribute");
				element.appendChild(sessionElement);
				sessionElement.setAttribute("dataType", "varchar");
				sessionElement.setAttribute("name", name);
				sessionElement.setTextContent(value);
			}
		}

		if (this.WriteDoc(doc, xmlFile)) {
			logger.info("======= OK successful upgrade file: \n" + xmlFilePath);
		} else {
			logger.error("******* ERROR  failed upgrade file: \n" + xmlFilePath);
		}
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param pluginsIB
	 * @return
	 * @throws IOException
	 */
	public String backupIBPlugins(String pluginsIB) throws IOException {
		Calendar currentDate = Calendar.getInstance();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMMdd-HH-mm-ss");
		String now = formatter.format(currentDate.getTime());
		String backupPlugins = pluginsIB.concat("-" + now);
		FileUtils.copyDirectory(new File(pluginsIB), new File(backupPlugins));
		FileUtils.deleteQuietly(new File(pluginsIB));
		return backupPlugins;
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param plugins
	 */
	public void deleteOldBackupIBPlugins(String plugins) {
		File filePath = new File(plugins);
		logger.info("======= Delete old backups plugins files");
		if (filePath.exists() && filePath.isDirectory()) {
			for (File file : filePath.listFiles()) {
				if (null != file) {
					if (file.isDirectory() && file.getName().startsWith("IB-")) {
						logger.info(file.getName());
						FileUtils.deleteQuietly(file);
					}
				}
			}
		}
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param fileConfigurations
	 * @return
	 * @throws IOException
	 * @throws JDOMException
	 */
	@SuppressWarnings("unchecked")
	public boolean getMergeInstall(InputStream fileConfigurations) throws IOException, JDOMException {
		boolean mergeInstall = false;
		org.jdom.Element root1 = getRootElement(fileConfigurations);
		org.jdom.Element elementT;
		List<org.jdom.Element> confElement;
		elementT = root1.getChild("merge-install");
		confElement = elementT.getChildren("ib-bib");
		if (null != confElement) {
			mergeInstall = Boolean.valueOf(confElement.get(0).getAttributeValue("value"));
		}
		return mergeInstall;
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param xmlFilePath
	 * @param dbmsName
	 * @throws Exception
	 */
	public void verifyDbmsName(String xmlFilePath, String dbmsName) throws Exception {
		File xmlFile = new File(xmlFilePath);
		this.readDocument(xmlFile);
		String message = "======= OK Find DBMS Configuration in File: \n" + xmlFilePath;

		Element element = (Element) xPath.evaluate("/config/dbms", doc, XPathConstants.NODE);

		if (null != element) {
			Element dbmsElement = (Element) xPath.evaluate("/config/dbms[@name='" + dbmsName + "']", doc, XPathConstants.NODE);
			if (null == dbmsElement) {
				message = "******* ERROR Find DBMS Configuration in File:\n" + xmlFilePath;
				message = message + "\nThere are not DBMS Configuration: " + dbmsName + "\n. Contact to System Administrator.";
				logger.error(message);
				
				throw new ValidatorException(getError(Constants.ERROR_18052));
			}
		}
		else {
			message = "******* ERROR Find DBMS Configuration in File:\n" + xmlFilePath;
			message = message + "\nThere are not DBMS Configuration: " + dbmsName + "\n. Contact to System Administrator.";
			logger.error(message);
			throw new ValidatorException(getError(Constants.ERROR_18052));			
		}
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param fileConfigurations
	 * @return installDemo
	 * @throws IOException
	 * @throws JDOMException
	 */
	@SuppressWarnings("unchecked")
	public boolean getInstallDemo(InputStream fileConfigurations) throws IOException, JDOMException {
		boolean installDemo = false;
		org.jdom.Element root1 = getRootElement(fileConfigurations);
		org.jdom.Element elementT;
		List<org.jdom.Element> confElement;
		elementT = root1.getChild("install-demo");
		confElement = elementT.getChildren("ib-bib-demo");
		if (null != confElement) {
			installDemo = Boolean.valueOf(confElement.get(0).getAttributeValue("value"));
		}
		return installDemo;
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param xmlFilePath
	 * @param fileConfigurations
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void editPlanConfigCIS(String xmlFilePath,
			InputStream fileConfigurations) throws Exception {
		
		String planId = "IB-CIS";
		File xmlFile = new File(xmlFilePath);
		this.readDocument(xmlFile);

		org.jdom.Element root1 = getRootElement(fileConfigurations);
		org.jdom.Element elementT;
		List<org.jdom.Element> confElement;

		// Plan Config
		elementT = root1.getChild("orchestrations");
		confElement = elementT.getChildren("staff");
		Element planIdResult = (Element) xPath.evaluate("/ccm-plan-config/own/plan[@id='" + planId + "']", doc, XPathConstants.NODE);

		if (null == planIdResult) {
			// Create plan id IB
			logger.info("======= OK create plan id: \n" + planId);
			Element ownElement = (Element) xPath.evaluate("/ccm-plan-config/own[last()]", doc, XPathConstants.NODE);
			Comment comment = doc.createComment("Plan IB-CIS");
			ownElement.appendChild(comment);
			Element planElement = doc.createElement("plan");
			ownElement.appendChild(planElement);
			planElement.setAttribute("id", planId);

			for (org.jdom.Element element : confElement) {
				String pluginName = element.getAttributeValue("name");
				String actualVersion = element.getAttributeValue("version");
				String jarPath = "../../CIS/SERVICES/plugins/IBOrchestration/" + pluginName + "-" + actualVersion + ".jar";

				Element pluginElement = doc.createElement("plugin");
				planElement.appendChild(pluginElement);
				pluginElement.setAttribute("name", pluginName);
				pluginElement.setAttribute("path", jarPath);
			}
		}
		else {
			NodeList nodeList = planIdResult.getChildNodes();
			// Remove all plugins
			for (int i = 0; i < nodeList.getLength(); i++) {
				if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element pluginElement = (Element) nodeList.item(i);
					planIdResult.removeChild(pluginElement);
				}
			}
			// Add all plugins
			logger.info("======= OK edit plan id: \n" + planId);
			for (org.jdom.Element element : confElement) {
				String pluginName = element.getAttributeValue("name");
				String actualVersion = element.getAttributeValue("version");
				String jarPath = "../../CIS/SERVICES/plugins/IBOrchestration/" + pluginName + "-" + actualVersion + ".jar";

				Element pluginElement = doc.createElement("plugin");
				planIdResult.appendChild(pluginElement);
				pluginElement.setAttribute("name", pluginName);
				pluginElement.setAttribute("path", jarPath);
			}
		}

		if (this.WriteDoc(doc, xmlFile)) {
			logger.info("======= OK successful upgrade file: \n" + xmlFilePath);
		} else {
			logger.error("******* ERROR  failed upgrade file: \n" + xmlFilePath);
		}
	}
	
	/**
	 * @author Carlos Mauricio Echeverría Goyes.
	 * @param xmlFilePath
	 * @param objPlanId
	 * @throws Exception
	 */
	public void removePlanConfigCIS(String xmlFilePath, String objPlanId)
			throws Exception {
	
		String planId = objPlanId;
		File xmlFile = new File(xmlFilePath);
		this.readDocument(xmlFile);	

		Node planIdResult = (Node) xPath.evaluate("/ccm-plan-config/own/plan[@id='" + planId + "']", doc, XPathConstants.NODE);
		if (planIdResult != null) {
			Node parent = planIdResult.getParentNode();
			parent.removeChild(planIdResult);
		}

		if (this.WriteDoc(doc, xmlFile)) {
			logger.info("======= OK successful remove " + objPlanId
					+ " file: \n" + xmlFilePath);
		} else {
			logger.error("******* ERROR  failed remove " + objPlanId
					+ " file: \n" + xmlFilePath);
		}
	}
	
	/**
	 * @author Isaac A. Torres M.
	 * @param xmlFilePath
	 * @param fileConfigurations
	 * @param objPlanId
	 * @param objelementT
	 * @throws Exception
	 */	 
	@SuppressWarnings("unchecked")
	public void editPlanConfigCIS(String xmlFilePath,
			InputStream fileConfigurations, String objPlanId, String objelementT)
			throws Exception {
		String planId = objPlanId;
		File xmlFile = new File(xmlFilePath);
		this.readDocument(xmlFile);

		org.jdom.Element root1 = getRootElement(fileConfigurations);
		org.jdom.Element elementT;
		List<org.jdom.Element> confElement;

		// Plan Config
		elementT = root1.getChild(objelementT);
		confElement = elementT.getChildren("staff");
		Element planIdResult = (Element) xPath.evaluate("/ccm-plan-config/own/plan[@id='" + planId + "']", doc, XPathConstants.NODE);

		if (null == planIdResult) {
			// Create plan id IB
			logger.info("======= OK create plan id: \n" + planId);
			Element ownElement = (Element) xPath.evaluate("/ccm-plan-config/own[last()]", doc, XPathConstants.NODE);
			Comment comment = doc.createComment("Plan " + objPlanId);
			ownElement.appendChild(comment);
			Element planElement = doc.createElement("plan");
			ownElement.appendChild(planElement);
			planElement.setAttribute("id", planId);

			for (org.jdom.Element element : confElement) {
				String pluginName = element.getAttributeValue("name");
				String actualVersion = element.getAttributeValue("version");
				String jarPath = "../../CIS/SERVICES/plugins/IBOrchestration/" + pluginName + "-" + actualVersion + ".jar";
					
				Element pluginElement = doc.createElement("plugin");
				planElement.appendChild(pluginElement);
				pluginElement.setAttribute("name", pluginName);
				pluginElement.setAttribute("path", jarPath);
			}
		}
		else {
			NodeList nodeList = planIdResult.getChildNodes();
			// Remove all plugins
			for (int i = 0; i < nodeList.getLength(); i++) {
				if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element pluginElement = (Element) nodeList.item(i);
					planIdResult.removeChild(pluginElement);
				}
			}
			// Add all plugins
			logger.info("======= OK edit plan id: \n" + planId);
			for (org.jdom.Element element : confElement) {
				String pluginName = element.getAttributeValue("name");
				String actualVersion = element.getAttributeValue("version");
				String jarPath = "../../CIS/SERVICES/plugins/IBOrchestration/" + pluginName + "-" + actualVersion + ".jar";

				Element pluginElement = doc.createElement("plugin");
				planIdResult.appendChild(pluginElement);
				pluginElement.setAttribute("name", pluginName);
				pluginElement.setAttribute("path", jarPath);
			}
		}

		if (this.WriteDoc(doc, xmlFile)) {
			logger.info("======= OK successful upgrade file: \n" + xmlFilePath);
		} else {
			logger.error("******* ERROR  failed upgrade file: \n" + xmlFilePath);
		}
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param xmlFilePath
	 * @param fileConfigurations
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void removeCisClient(String xmlFilePath,
			InputStream fileConfigurations) throws Exception {
		File xmlFile = new File(xmlFilePath);
		this.readDocument(xmlFile);

		org.jdom.Element root1 = getRootElement(fileConfigurations);
		org.jdom.Element elementT;
		List<org.jdom.Element> confElement;

		// Java Orchestrations
		Element tranResult = (Element) xPath.evaluate(
				"/ccm-client-config/general", doc, XPathConstants.NODE);
		if (null != tranResult) {
			// Remove all JARs
			logger.info("======= Remove configurations ");
			elementT = root1.getChild("orchestrations");
			confElement = elementT.getChildren("staff");
			for (org.jdom.Element element : confElement) {
				String pluginName = element.getAttributeValue("name");
				Element pluginResult = (Element) xPath.evaluate(
						"/ccm-client-config/general/plugin[@name='"
								+ pluginName + "']", doc, XPathConstants.NODE);
				if (null != pluginResult)
					tranResult.removeChild(pluginResult);
			}
		}

		if (this.WriteDoc(doc, xmlFile)) {
			logger.info("======= OK successful upgrade file: \n" + xmlFilePath);
		}else {
			logger.error("******* ERROR  failed upgrade file: \n" + xmlFilePath);
		}
	}
	
	/**
	 * @author Carlos Mauricio Echeverría Goyes.
	 * @param xmlFilePath
	 * @param objPlanId
	 * @throws Exception
	 */
	public void editMasterCTS(String xmlFilePath, String name, String interfaz,
			String path, String description, String filter) throws Exception {

		try {
			File xmlFile = new File(xmlFilePath);
			this.readDocument(xmlFile);
			logger.info("======= Iniciando Instalacion ==>  " + xmlFilePath);

			// Elimina si existe el nodo
			Node nodeResult = (Node) xPath.evaluate(
					"/Configuration/config[@name='" + name + "']", doc,
					XPathConstants.NODE);
			if (nodeResult != null) {
				logger.info("Eliminando Nodo " + name);
				Node parent = nodeResult.getParentNode();
				parent.removeChild(nodeResult);
			}

			// Inserta nuevo nodo
			logger.info("Creando Nodo " + name);
			Element myRoot = (Element) xPath.evaluate("/Configuration", doc,
					XPathConstants.NODE);
			if (myRoot != null) {
				Element configElement = doc.createElement("config");
				myRoot.appendChild(configElement);
				configElement.setAttribute("name", name);
				configElement.setAttribute("interface", interfaz);
				configElement.setAttribute("path", path);
				configElement.setAttribute("description", description);
				Element configFilter = doc.createElement("filter");
				configElement.appendChild(configFilter);
				configFilter.setTextContent(filter);
			} else {
				throw new ValidatorException(getError(Constants.ERROR_18050));
			}

			if (this.WriteDoc(doc, xmlFile)) {
				logger.info("======= OK successful insert " + name
						+ " file: \n" + xmlFilePath);

			} else {
				logger.error("******* ERROR  failed insert " + name
						+ " file: \n" + xmlFilePath);
			}
		} catch (Exception e) {
			logger.error(e);
			throw new ValidatorException(getError(Constants.ERROR_18050));
		}
	}

	/**
	 * Get Id Error String
	 * 
	 * @param errorId
	 * @return
	 */
	private String getError(String errorId) {
		return getClass().getSimpleName() + "." + errorId;
	}
}