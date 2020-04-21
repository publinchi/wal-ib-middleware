package com.cobiscorp.cobis.ib.middleware.installer.validators.impl;

import java.io.File;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;

import com.cobiscorp.cobis.ib.middleware.installer.commons.Constants;
import com.cobiscorp.cobis.ib.middleware.installer.jobs.impl.XPathUpdate;
import com.cobiscorp.cobis.ib.middleware.installer.validators.AbstractDataValidator;
import com.izforge.izpack.installer.AutomatedInstallData;

public class EnvironmentValidator extends AbstractDataValidator{
	
	Logger logger = Logger.getLogger(EnvironmentValidator.class);
	static final String CTS_MF_INFRAESTRUCTURE = "/CTS_MF/infrastructure/";
	static final String CIS_CTS_TRANS_SERVICE = "/CIS/SERVICES/CTSTRANSFORMATION/services/";	
	static final String CTS_SERVICES_AS = "/CTS_MF/services-as/";
	
	private XPath xPath = null;
	private Document doc = null;
	
	public Status validateData(AutomatedInstallData data) {
		
		try{
			
			Properties properties = new Properties();
			properties.load(getClass().getResourceAsStream("/log4j.properties"));
			PropertyConfigurator.configure(properties);
			
			logger.info("===================== START INSTALLATION =====================");
			logger.info("======= START VALIDATE REQUIRED FILES =======");
			logger.info("======= Internet Banking =======");
			
			String cobisHome = data.getInstallPath().trim();
			XPathUpdate updateConfigurations= new XPathUpdate();
			Integer ctsVersion = null;
			String pathFile = null;
			ctsVersion = updateConfigurations.getCtsVersion(cobisHome);
			if(null==ctsVersion)
				return Status.ERROR;
			
			if( ctsVersion >= 3226)
			{
				pathFile = cobisHome.concat(CTS_MF_INFRAESTRUCTURE.concat("cts-ccm-plan-config.xml"));
				if(!new File(pathFile).exists()){
					logger.error("******* ERROR, File does not exist: " + pathFile);
					this.setErrorId(Constants.ERROR_18001);
					return Status.ERROR;
				}
			
			}else{
				pathFile = cobisHome.concat(CTS_MF_INFRAESTRUCTURE.concat("cts-ccm-config.xml"));
				if(!new File(pathFile).exists()){					
					logger.error("*******ERROR, File does not exist: " + pathFile);
					this.setErrorId(Constants.ERROR_18002);
					return Status.ERROR;
				}
				
				pathFile = cobisHome.concat(CTS_MF_INFRAESTRUCTURE.concat("cts-ccm-client-config.xml"));
				if(!new File(pathFile).exists()){
					logger.error("*******ERROR, File does not exist: " + pathFile);
					this.setErrorId(Constants.ERROR_18003);
					return Status.ERROR;
				}
			}
			
			if( ctsVersion < 3229)
			{
				pathFile = cobisHome.concat(CTS_SERVICES_AS.concat("trlogger/tr-logger-config.xml"));
				if(!new File(pathFile).exists()){
					logger.error("*******ERROR, File does not exist: " + pathFile);
					this.setErrorId(Constants.ERROR_18004);
					return Status.ERROR;
				}
				
				pathFile = cobisHome.concat(CTS_SERVICES_AS.concat("authorization/bv-services-authorization-service-config.xml"));
				if(!new File(pathFile).exists()){
					logger.error("*******ERROR, File does not exist: " + pathFile);
					this.setErrorId(Constants.ERROR_18005);
					return Status.ERROR;
				}
				
				pathFile = cobisHome.concat(CTS_SERVICES_AS.concat("authorization/cobisbv-authorization-service-config.xml"));
				if(!new File(pathFile).exists()){
					logger.error("*******ERROR, File does not exist: " + pathFile);
					this.setErrorId(Constants.ERROR_18006);
					return Status.ERROR;
				}
			}
			
			/*pathFile = cobisHome.concat("/CIS/SERVICES/ORCHESTRATOR/master-orchestrator-config.xml");
			if(!new File(pathFile).exists()){
				logger.error("*******ERROR, File does not exist: " + pathFile);
				this.setErrorId(Constants.ERROR_18007);
				return Status.ERROR;
			}*/
			
			/*pathFile = cobisHome.concat("/CIS/SERVICES/ORCHESTRATOR/infrastructure/csp-ccm-config.xml");
			if(!new File(pathFile).exists()){
				logger.error("*******ERROR, File does not exist: " + pathFile);
				this.setErrorId(Constants.ERROR_18008);
				return Status.ERROR;
			}*/
			
			pathFile = cobisHome.concat(CTS_SERVICES_AS.concat("session/session-manager-service-config.xml"));
			if(!new File(pathFile).exists()){
				logger.error("*******ERROR, File does not exist: " + pathFile);
				this.setErrorId(Constants.ERROR_18009);
				return Status.ERROR;
			}
			
			//Start Buffer Size
			pathFile = cobisHome.concat(CTS_SERVICES_AS.concat("spexecutor/spexecutor-service-config.xml"));
			if(!new File(pathFile).exists()){
				logger.error("*******ERROR, File does not exist: " + pathFile);
				this.setErrorId(Constants.ERROR_18010);
				return Status.ERROR;
			}else{
				if(!updateConfigurations.getReviewBufferSize(pathFile))
				{
					logger.error("*******ERROR, Configuration does not exist: " + pathFile);
					this.setErrorId(Constants.ERROR_18030);	
					return Status.ERROR;
				}
			}		
			
			pathFile = cobisHome.concat(CTS_SERVICES_AS.concat("spexecutor/spexecutor-service-light-config.xml"));
			if(!new File(pathFile).exists()){
				logger.error("*******ERROR, File does not exist: " + pathFile);
				this.setErrorId(Constants.ERROR_18011);
				return Status.ERROR;
			}else{
				if(!updateConfigurations.getReviewBufferSize(pathFile))
				{
					logger.error("******* ERROR, Configuration does not exist: " + pathFile);
					this.setErrorId(Constants.ERROR_18030);
					return Status.ERROR;
				}
			}		
			
			pathFile = cobisHome.concat(CTS_SERVICES_AS.concat("spexecutor/spexecutor-service-object-config.xml"));
			if(!new File(pathFile).exists()){
				logger.error("*******ERROR, File does not exist: " + pathFile);
				this.setErrorId(Constants.ERROR_18012);
				return Status.ERROR;
			}else{
				if(!updateConfigurations.getReviewBufferSize(pathFile))
				{
					logger.error("******* ERROR, Configuration does not exist: " + pathFile);
					this.setErrorId(Constants.ERROR_18030);
					return Status.ERROR;
				}
			}
			//Finish Buffer Size
			pathFile = cobisHome.concat(CIS_CTS_TRANS_SERVICE.concat("csp-ctstransformation-service-config.xml"));
			if(!new File(pathFile).exists()){
				logger.error("*******ERROR, File does not exist: " + pathFile);
				this.setErrorId(Constants.ERROR_18013);
				return Status.ERROR;
			}
			
			pathFile = cobisHome.concat("/CIS/SERVICES/CSPROUTING/infrastructure/csp-ccm-client-config.xml");
			if(!new File(pathFile).exists()){
				logger.error("******* ERROR File does not exist: " + pathFile);
				this.setErrorId(Constants.ERROR_18014);
				return Status.ERROR;
			}
			
			pathFile = cobisHome.concat(CTS_SERVICES_AS.concat("authentication/cobisbv-authentication-service-config.xml"));
			if(!new File(pathFile).exists()){
				logger.error("*******ERROR, File does not exist: " + pathFile);
				this.setErrorId(Constants.ERROR_18015);
				return Status.ERROR;
			}
			
			pathFile = cobisHome.concat(CTS_SERVICES_AS.concat("servexecutor/services/services.xml"));
			if(!new File(pathFile).exists()){
				logger.error("*******ERROR, File does not exist: " + pathFile);
				this.setErrorId(Constants.ERROR_18016);
				return Status.ERROR;
			}
			
			pathFile = cobisHome.concat(CTS_SERVICES_AS.concat("reentry/reentry-immediate-service-config.xml"));
			if(!new File(pathFile).exists()){
				logger.error("*******ERROR, File does not exist: " + pathFile);
				this.setErrorId(Constants.ERROR_18017);
				return Status.ERROR;
			}
			
			pathFile = cobisHome.concat(CTS_SERVICES_AS.concat("session/cobisbv-session-manager-config.xml"));
			if(!new File(pathFile).exists()){
				logger.error("*******ERROR, File does not exist: " + pathFile);
				this.setErrorId(Constants.ERROR_18018);
				return Status.ERROR;
			}
			
			pathFile = cobisHome.concat(CTS_SERVICES_AS.concat("utils/cobis-ssn-unique-config.xml"));
			if(!new File(pathFile).exists()){
				logger.error("*******ERROR, File does not exist: " + pathFile);
				this.setErrorId(Constants.ERROR_18019);
				return Status.ERROR;
			}
			
			pathFile = cobisHome.concat(CTS_SERVICES_AS.concat("utils/cobisbv-ssn-unique-config.xml"));
			if(!new File(pathFile).exists()){
				logger.error("*******ERROR, File does not exist: " + pathFile);
				this.setErrorId(Constants.ERROR_18020);
				return Status.ERROR;
			}
			
			pathFile = cobisHome.concat(CTS_SERVICES_AS.concat("servexecutor/business-service-executor-config.xml"));
			if(!new File(pathFile).exists()){
				logger.error("*******ERROR, File does not exist: " + pathFile);
				this.setErrorId(Constants.ERROR_18021);
				return Status.ERROR;
			}
			
			pathFile = cobisHome.concat(CTS_SERVICES_AS.concat("servexecutor/service-executor-config.xml"));
			if(!new File(pathFile).exists()){
				logger.error("*******ERROR, File does not exist: " + pathFile);
				this.setErrorId(Constants.ERROR_18022);
				return Status.ERROR;
			}
			
			// VALIDATE INSTALL OTP			
			if (!ValidateInstallOTP(cobisHome)) {				
				this.setErrorId(Constants.ERROR_18000);
				return Status.ERROR;
			}
			
			logger.info("======= FINISH VALIDATE REQUIRED FILES =======");				
			return Status.OK;
		
		} catch (Exception e) {
			logger.error("******* ERROR VALIDATE REQUIRED FILES *******");
			logger.error("******* ERROR: " + e.getLocalizedMessage());
			logger.error("******* ERROR: " + e);
			return Status.ERROR;			
		}
	}
	
	
	/**
	 * Permite verificar si se encuentra instalado OTP
	 * 
	 * @param cobisHome
	 * @return
	 */
	public boolean ValidateInstallOTP(String cobisHome) {
		System.out.println("======= START VALIDATE INSTALL OTP =======");
		boolean success = false;

		try {
			String xmlFilePath = cobisHome.concat(CTS_MF_INFRAESTRUCTURE
					.concat("cts-ccm-plan-config.xml"));
			File xmlFile = new File(xmlFilePath);
			this.readDocument(xmlFile);

			String message = "======= OK CTS Configuration File cts-ccm-plan-config.xml: \n"
					+ xmlFilePath;

			if (!(Boolean) xPath.evaluate(
					"/ccm-plan-config/own/plan[@id='IB-TOKEN']", doc,
					XPathConstants.BOOLEAN)) {

				message = "****** ERROR CTS Configuration File cts-ccm-plan-config.xml:\n"
						+ xmlFilePath
						+ "\n OTP not installed. Before install IB you should install OTP \nContact to System Administrator.";
				logger.error(message);								
			} else {
				success = true;
			}
		} catch (Exception ex) {
			logger.error(ex);
		} finally {
			System.out.println("======= FINALL VALIDATE INSTALL OTP =======");
		}
		return success;
	}

	public void readDocument(File xmlFile) throws Exception {
		DocumentBuilderFactory domFactory = DocumentBuilderFactory
				.newInstance();
		domFactory.setNamespaceAware(true); // never forget this!
		DocumentBuilder builder = domFactory.newDocumentBuilder();
		doc = (Document) builder.parse(xmlFile);
		xPath = XPathFactory.newInstance().newXPath();
	}	
}