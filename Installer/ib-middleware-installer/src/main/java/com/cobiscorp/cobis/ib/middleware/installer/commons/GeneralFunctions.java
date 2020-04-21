package com.cobiscorp.cobis.ib.middleware.installer.commons;

import java.io.StringReader;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class GeneralFunctions {

	Logger logger = Logger.getLogger(GeneralFunctions.class);
	private String commonError = "XPathUpdate.error_18100";

	/**
	 * Allow get message internacionalization for code
	 * 
	 * @param selectedLang
	 *            Selected Language for user
	 * @param id
	 *            resource identifier
	 * @return traduced message to selected language
	 */
	public String getResourceLangPack(String selectedLang, String id) {
		logger.info("Inicia getResourceLangPack");
		String message = id;
		String expresionEvaluate = null;
		XPath xPath = null;

		try {
			logger.info("Selected Language " + selectedLang);
			String path = "langpacks/Custom.Langpack." + selectedLang + ".xml";
			String xmlString = IOUtils.toString(getClass().getClassLoader()
					.getResourceAsStream(path));

			xPath = XPathFactory.newInstance().newXPath();
			Node node = null;
			try {
				expresionEvaluate = "/langpack/str[@id='" + id + "']";
				node = (Node) xPath.evaluate(expresionEvaluate,
						new InputSource(new StringReader(xmlString)),
						XPathConstants.NODE);

				if (node == null) {
					expresionEvaluate = "/langpack/str[@id='" + commonError
							+ "']";
					node = (Node) xPath.evaluate(expresionEvaluate,
							new InputSource(new StringReader(xmlString)),
							XPathConstants.NODE);
				}
			} catch (XPathExpressionException e) {
				logger.error("Error XPathExpressionException " + e);
			}
			if (node != null) {
				String messageWithoutEnc = ((Element) node).getAttribute("txt");
				message = new String(messageWithoutEnc.getBytes(), "UTF-8");
			}

		} catch (Exception e) {
			logger.error("Error Exception " + e);
		} finally {
			logger.info("Finaliza getResourceLangPack");
		}
		return message;
	}
}