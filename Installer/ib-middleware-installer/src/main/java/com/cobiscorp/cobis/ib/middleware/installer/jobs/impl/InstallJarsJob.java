package com.cobiscorp.cobis.ib.middleware.installer.jobs.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.cobiscorp.cobis.ib.middleware.installer.commons.GeneralFunctions;
import com.cobiscorp.cobis.ib.middleware.installer.jobs.AbstractProcessJob;
import com.cobiscorp.cobis.ib.middleware.installer.validators.exceptions.ValidatorException;

public class InstallJarsJob extends AbstractProcessJob {

	private static final String CTS_MF_INFRAESTRUCTURE = "/CTS_MF/infrastructure/";
	private static final String CIS_SERVICE = "/CIS/SERVICES/";
	private static final String CIS_CTS_TRANS_SERVICE = CIS_SERVICE.concat("CTSTRANSFORMATION/services/");
	private static final String CTS_SERVICES_AS = "/CTS_MF/services-as/";
	private static final String XML_EXTENSION = ".xml";
	private static final String SQLCTS = "SQLCTS";
	private static final String SYBCTS = "SYBCTS";
	private static final String CTS_MF = "/CTS_MF/";

	@Override
	protected boolean execute(String[] args) {

		Logger logger = Logger.getLogger(InstallJarsJob.class);
		XPathUpdate updateConfigurations = new XPathUpdate();
		String cobisHome = args[0].toString();
		String selectedLang = args[1].toString();
		boolean sucessInstall = false;		
		
		//**********************************************************************
		//--CTS FILES
		//**********************************************************************
		String MasterCts = cobisHome.concat(CTS_MF.concat("master-cts-mf-as.xml"));
		String ctsPlanConfig = cobisHome.concat(CTS_MF_INFRAESTRUCTURE.concat("cts-ccm-plan-config.xml"));
		String dbmsFile = cobisHome.concat(CTS_MF_INFRAESTRUCTURE.concat("cts-dbms-config.xml"));
		String ctsCcmConf= cobisHome.concat(CTS_MF_INFRAESTRUCTURE.concat("cts-ccm-config.xml"));
		String sessionManager = cobisHome.concat(CTS_SERVICES_AS.concat("session/session-manager-service-config.xml"));
		String authenticationConf = cobisHome.concat(CTS_SERVICES_AS.concat("authentication/cobisbv-authentication-service-config.xml"));		
		String iBServices = cobisHome.concat(CTS_SERVICES_AS.concat("servexecutor/services/IB-services.xml"));
		String services = cobisHome.concat(CTS_SERVICES_AS.concat("servexecutor/services/services.xml"));
		String configChannelAdmin = cobisHome.concat(CTS_SERVICES_AS.concat("channel-admin/config-channel-adm.xml"));		
		String reentryConf = cobisHome.concat(CTS_SERVICES_AS.concat("reentry/reentry-immediate-service-config.xml"));
		String cobisSessionManager = cobisHome.concat(CTS_SERVICES_AS.concat("session/cobisbv-session-manager-config.xml"));
		String cobisSsnUnique = cobisHome.concat(CTS_SERVICES_AS.concat("utils/cobis-ssn-unique-config.xml"));
		String cobisBvSsnUnique = cobisHome.concat(CTS_SERVICES_AS.concat("utils/cobisbv-ssn-unique-config.xml"));
		String businessServExecutor = cobisHome.concat(CTS_SERVICES_AS.concat("servexecutor/business-service-executor-config.xml"));
		String servExecutor = cobisHome.concat(CTS_SERVICES_AS.concat("servexecutor/service-executor-config.xml"));
		String integServConfig = cobisHome.concat(CTS_SERVICES_AS.concat("integration/integration-service-config.xml"));
		String socketSsnConfig = cobisHome.concat(CTS_SERVICES_AS.concat("socketssn/socket-ssn-config.xml"));
				
		//**********************************************************************
		//--CIS FILES
		//**********************************************************************
		String ctsTransformation = cobisHome.concat(CIS_CTS_TRANS_SERVICE.concat("csp-ctstransformation-service-config.xml"));
		String confBvAuthentication = cobisHome.concat(CIS_CTS_TRANS_SERVICE.concat("configuracion-bv-authentication.xml"));
		String confRuteoServicios = cobisHome.concat(CIS_CTS_TRANS_SERVICE.concat("configuracion-ruteo-servicios.xml"));
		String confRuteoServiciosBatch = cobisHome.concat(CIS_CTS_TRANS_SERVICE.concat("configuracion-ruteo-servicios-batch.xml"));
		String confExecutorChannelAdmin = cobisHome.concat(CIS_CTS_TRANS_SERVICE.concat("configuracion-executor-channel-admin.xml"));
		String jarsFilesPath = cobisHome.concat(CIS_SERVICE.concat("plugins/IBOrchestration/"));
		//String masterOrchestrator = cobisHome.concat("/CIS/SERVICES/ORCHESTRATOR/master-orchestrator-config.xml");
		//String cspCcmOrchestrator = cobisHome.concat("/CIS/SERVICES/ORCHESTRATOR/infrastructure/csp-ccm-config.xml");
		
		// OTHER VARIABLES		
		String backupSessionManager = null;		
		String backupCtsTransformation = null;
		String backupCtsPlanConfig = null;		
		String backupConfBvAuthentication = null;		
		String backupConfRuteoServicios = null;		
		String backupConfRuteoServiciosBatch = null;		
		String backupconfExecutorChannelAdmin = null;		
		String backupAuthenticationConf = null;		
		String backupIBServices = null;		
		String bakcupServices = null;		
		Map<String, String> mapBackupJARFiles = null;
		String backupCobisSessionManager = null;		
		String backupCobisSsnUnique = null;		
		String bkCobisBvSsnUnique = null;		
		String bkBusinessServExecutor = null;		
		String bkServExecutor = null;
		String bkConfigChannelAdmin = null;
		String bkMasterCts = null;		
		String pluginsIB = null;
		String backupPlugins = null;
		Integer ctsVersion = null;

		try {			
			//**********************************************************************
			//--DELETE OLD BACKUP FILES -- STEPS 1 - 15
			//***********************************************************************
			logger.info("======= START Delete Old Backup Files =======");
			logger.info("Paso 1");
			updateConfigurations.deleteOldBackupFiles(cobisHome.concat(CTS_SERVICES_AS.concat("session/")), "session-manager-service-config");
			logger.info("Paso 2");
			updateConfigurations.deleteOldBackupFiles(cobisHome.concat(CIS_CTS_TRANS_SERVICE), "csp-ctstransformation-service-config");
			logger.info("Paso 3");
			updateConfigurations.deleteOldBackupFiles(cobisHome.concat(CIS_SERVICE.concat("CSPROUTING/infrastructure/")), "csp-ccm-client-config");
			logger.info("Paso 4");
			updateConfigurations.deleteOldBackupFiles(cobisHome.concat(CIS_CTS_TRANS_SERVICE), "configuracion-bv-authentication");
			logger.info("Paso 5");
			updateConfigurations.deleteOldBackupFiles(cobisHome.concat(CIS_CTS_TRANS_SERVICE), "configuracion-ruteo-servicios");
			logger.info("Paso 6");
			updateConfigurations.deleteOldBackupFiles(cobisHome.concat(CTS_SERVICES_AS.concat("authentication/")), "cobisbv-authentication-service-config");
			logger.info("Paso 7");
			updateConfigurations.deleteOldBackupFiles(cobisHome.concat(CTS_SERVICES_AS.concat("servexecutor/services/")), "IB-services");
			logger.info("Paso 8");
			updateConfigurations.deleteOldBackupFiles(cobisHome.concat(CTS_SERVICES_AS.concat("servexecutor/services/")), "services");
			logger.info("Paso 9");
			updateConfigurations.deleteOldBackupFiles(cobisHome.concat(CTS_SERVICES_AS.concat("session/")), "cobisbv-session-manager-config");
			logger.info("Paso 10");
			updateConfigurations.deleteOldBackupFiles(cobisHome.concat(CTS_SERVICES_AS.concat("utils/")), "cobis-ssn-unique-config");
			logger.info("Paso 11");
			updateConfigurations.deleteOldBackupFiles(cobisHome.concat(CTS_SERVICES_AS.concat("utils/")), "cobisbv-ssn-unique-config");
			logger.info("Paso 12");
			updateConfigurations.deleteOldBackupFiles(cobisHome.concat(CTS_SERVICES_AS.concat("servexecutor/")), "business-service-executor-config");
			logger.info("Paso 13");
			updateConfigurations.deleteOldBackupFiles(cobisHome.concat(CTS_SERVICES_AS.concat("channel-admin/")), "config-channel-adm");
			logger.info("Paso 14");
			updateConfigurations.deleteOldBackupIBPlugins(cobisHome.concat("/CTS_MF/plugins"));
			logger.info("Paso 15");
			updateConfigurations.deleteOldBackupFiles(cobisHome.concat(CTS_MF), "master-cts-mf-as");
			
			logger.info("======= FINISH Delete Old Backup Files =======");

			// READ a XML FILE INSIDE JAR ARCHIVE
			InputStream fileConfigurations = getClass().getResourceAsStream("/modules/FileConfigurations.xml");
			if (null != fileConfigurations) {
				if (updateConfigurations.getMergeInstall(fileConfigurations)) {
					pluginsIB = cobisHome.concat("/CTS_MF/plugins/IB");
					if (new File(pluginsIB).exists()) {
						logger.info("======= START Backup IB Plugins Directory =======");
						backupPlugins = updateConfigurations
								.backupIBPlugins(pluginsIB);
						logger.info("======= FINISH Backup IB Plugins Directory =======");
					}
				}
				
				// CTS VERSION
				ctsVersion = updateConfigurations.getCtsVersion(cobisHome);
				if (null == ctsVersion) {
					return sucessInstall;
				}
				
				if (ctsVersion.intValue() >= 3226) {
					
					//**********************************************************************
					//--BACKUP FILES -- STEPS 16 - 31
					//**********************************************************************
					logger.info("======= START Backup Files =======");
					if (ctsVersion.intValue() >= 3226) {
						updateConfigurations.deleteOldBackupFiles(cobisHome.concat(CTS_MF_INFRAESTRUCTURE), "cts-ccm-plan-config");
						backupCtsPlanConfig = updateConfigurations.getBackupFilePath(new File(ctsPlanConfig), XML_EXTENSION);
					}	
					
					logger.info("Paso 16");
					backupSessionManager = updateConfigurations.getBackupFilePath(new File(sessionManager), XML_EXTENSION);
					logger.info("Paso 17");
					backupCtsTransformation = updateConfigurations.getBackupFilePath(new File(ctsTransformation), XML_EXTENSION);					
					logger.info("Paso 18");
					backupConfBvAuthentication = updateConfigurations.getBackupFilePath(new File(confBvAuthentication), XML_EXTENSION);
					logger.info("Paso 19");
					backupconfExecutorChannelAdmin = updateConfigurations.getBackupFilePath(new File(confExecutorChannelAdmin), XML_EXTENSION);
					logger.info("Paso 20");
					backupConfRuteoServicios = updateConfigurations.getBackupFilePath(new File(confRuteoServicios), XML_EXTENSION);
					logger.info("Paso 21");
					updateConfigurations.deleteFile(new File(confRuteoServicios));
					logger.info("Paso 22");
					logger.info(confRuteoServiciosBatch);
					backupConfRuteoServiciosBatch = updateConfigurations.getBackupFilePath(new File(confRuteoServiciosBatch), XML_EXTENSION);
					logger.info("Paso 23");
					updateConfigurations.deleteFile(new File(confRuteoServiciosBatch));
					
					logger.info("Paso 24");
					backupAuthenticationConf = updateConfigurations.getBackupFilePath(new File(authenticationConf), XML_EXTENSION);
					logger.info("Paso 25");
					backupIBServices = updateConfigurations.getBackupFilePath(new File(iBServices), XML_EXTENSION);
					logger.info("Paso 26");
					bakcupServices = updateConfigurations.getBackupFilePath(new File(services), XML_EXTENSION);
					logger.info("Paso 27");
					backupCobisSessionManager = updateConfigurations.getBackupFilePath(new File(cobisSessionManager), XML_EXTENSION);
					logger.info("Paso 28");
					backupCobisSsnUnique = updateConfigurations.getBackupFilePath(new File(cobisSsnUnique), XML_EXTENSION);
					logger.info("Paso 27");
					bkCobisBvSsnUnique = updateConfigurations.getBackupFilePath(new File(cobisBvSsnUnique), XML_EXTENSION);
					logger.info("Paso 29");
					bkBusinessServExecutor = updateConfigurations.getBackupFilePath(new File(businessServExecutor), XML_EXTENSION);
					logger.info("Paso 30");
					bkServExecutor = updateConfigurations.getBackupFilePath(new File(servExecutor), XML_EXTENSION);
					logger.info("Paso 31");
					bkConfigChannelAdmin = updateConfigurations.getBackupFilePath(new File(configChannelAdmin), XML_EXTENSION);
					logger.info("Paso 32");
					bkMasterCts = updateConfigurations.getBackupFilePath(new File(MasterCts), XML_EXTENSION);
					logger.info("======= FINISH Backup Files =======");

					//**********************************************************************
					//--VERIFY FILES SESSION MANAGER
					//**********************************************************************
					logger.info("======= START Review Cobis Session Manager =======");
					updateConfigurations.editCobisSessionManager(cobisSessionManager, "culture", "bv_cultura");
					updateConfigurations.editCobisSessionManager(cobisSessionManager, "client_mis", "bv_cliente_mis");
					updateConfigurations.editCobisSessionManager(cobisSessionManager, "client", "bv_cliente");
					updateConfigurations.editCobisSessionManager(cobisSessionManager, "profile", "bv_perfil");
					logger.info("======= FINISH Review Cobis Session Manager =======");

					//**********************************************************************
					//--VERIFY FILES - DBMS
					//**********************************************************************					
					logger.info("======= START Verifing Files =======");
					updateConfigurations.verifyDbmsName(dbmsFile, SQLCTS);					
					//updateConfigurations.verifyMasterOrchestrator(masterOrchestrator);
					//updateConfigurations.verifyCspCcmConfig(cspCcmOrchestrator);
					
					//VERSION IB UX
					//updateConfigurations.verifyCtsCcmConfig(ctsCcmConf);
					updateConfigurations.verifyCtsIntegrationServiceConfig(integServConfig);
					updateConfigurations.verifySocketSsnConfig(socketSsnConfig);					
					
					updateConfigurations.verifyReentryConfig(reentryConf);
					updateConfigurations.editDbmsName(cobisSessionManager, SQLCTS);
					updateConfigurations.editDbmsName(cobisSsnUnique, SYBCTS);
					updateConfigurations.editDbmsName(cobisBvSsnUnique, SQLCTS);
					updateConfigurations.editDbmsName(businessServExecutor, SYBCTS);
					updateConfigurations.editDualMode(businessServExecutor);
					updateConfigurations.editDualMode(servExecutor);
					
					logger.info("Antes de editServices");
					updateConfigurations.editServices(services,"IB-services.xml");
					logger.info("Despues  de editServices");

					if (ctsVersion < 3229) {
						String trLogger = cobisHome.concat(CTS_SERVICES_AS.concat("trlogger/tr-logger-config.xml"));
						updateConfigurations.editDbmsName(trLogger, SQLCTS);
						String bvServAuthConf = cobisHome.concat(CTS_SERVICES_AS.concat("authorization/bv-services-authorization-service-config.xml"));
						updateConfigurations.editDbmsName(bvServAuthConf, SQLCTS);
						String cobisBvAuthConf = cobisHome.concat(CTS_SERVICES_AS.concat("authorization/cobisbv-authorization-service-config.xml"));
						updateConfigurations.editDbmsName(cobisBvAuthConf, SQLCTS);
					}				
					
					// VERIFY REENTRY CONFIG
					fileConfigurations.close();
					fileConfigurations = getClass().getResourceAsStream("/modules/FileConfigurations.xml");
					updateConfigurations.verifyReentryPlanConfig(ctsPlanConfig, fileConfigurations);
					logger.info("======= FINISH Verifing Files =======");
					
					// PATH FILE session-manager-service-config.xml
					updateConfigurations.editSessionManager(sessionManager);
					
					// PATH FILE csp-ctstransformation-service-config.xml
					fileConfigurations.close();
					fileConfigurations = getClass().getResourceAsStream("/modules/FileConfigurations.xml");
					updateConfigurations.editCtstransformation(ctsTransformation, fileConfigurations);
					
					// BACKUP JARs ORCHESTRATIONS
					fileConfigurations.close();
					fileConfigurations = getClass().getResourceAsStream("/modules/FileConfigurations.xml");
					mapBackupJARFiles = updateConfigurations.backupJarCisClient(jarsFilesPath, fileConfigurations);
					
					logger.info("Paso 33");
					if (ctsVersion.intValue() >= 3226) {
						// CONFIGURATIONS planConfig
						logger.info("Paso 34");
						fileConfigurations.close();
						fileConfigurations = getClass().getResourceAsStream("/modules/FileConfigurations.xml");
						updateConfigurations.editPlanConfig(ctsPlanConfig, fileConfigurations);
					}
					
					if (ctsVersion.intValue() >= 3221) {	
						//**********************************************************************
						//--EDIT PLAN CONFIG
						//**********************************************************************						
						logger.info("Paso 35");
						updateConfigurations.removePlanConfigCIS(ctsPlanConfig,"IB-CIS");
						updateConfigurations.removePlanConfigCIS(ctsPlanConfig,"IB-TEMPLATES");
						updateConfigurations.removePlanConfigCIS(ctsPlanConfig,"IB-AUTHENTICATE");
						updateConfigurations.removePlanConfigCIS(ctsPlanConfig,"api-rest-orchestrations");
												
						logger.info("Paso 36");
						fileConfigurations.close();
						fileConfigurations = getClass().getResourceAsStream("/modules/FileConfigurations.xml");
						updateConfigurations.editPlanConfigCIS(ctsPlanConfig, fileConfigurations, "IB-TEMPLATES", "orchestrationsTemplate");					
						
						logger.info("Paso 37");
						fileConfigurations.close();
						fileConfigurations = getClass().getResourceAsStream("/modules/FileConfigurations.xml");
						updateConfigurations.editPlanConfigCIS(ctsPlanConfig, fileConfigurations, "IB-CIS", "orchestrations");
						
						logger.info("Paso 38");
						fileConfigurations.close();
						fileConfigurations = getClass().getResourceAsStream("/modules/FileConfigurations.xml");
						updateConfigurations.editPlanConfigCIS(ctsPlanConfig, fileConfigurations, "IB-AUTHENTICATE", "orchestrationsAuthenticate");
				
						logger.info("Paso 39");
						fileConfigurations.close();
						fileConfigurations = getClass().getResourceAsStream("/modules/FileConfigurations.xml");
						updateConfigurations.editPlanConfigCISApiRest(ctsPlanConfig, fileConfigurations, "api-rest-orchestrations", "api-rest-orchest");						
					
					}
					
					logger.info("Paso 39");					
					// DELETE JARs ORCHESTRATIONS
					updateConfigurations.deleteJarCisClient(mapBackupJARFiles);
				} else {
					String message = "LA VERSION A INSTALAR NO ESTA ACTUALIZADA - CONSULTE CON EL ADMINISTRADOR DE IB";
					logger.error("===================== " + message
							+ "=====================");
					throw new ValidatorException(message);
				}			
			}
			sucessInstall = true;
			logger.info("===================== FINISH INSTALLATION =====================");
		}
		catch (Exception e) {
			
			// RESTORE FILES
			logger.error("******* START Restore Files *******");
			if (null != backupSessionManager) {
				updateConfigurations.restoreFile(sessionManager, backupSessionManager);
			}
			if (null != backupCtsTransformation) {
				updateConfigurations.restoreFile(ctsTransformation, backupCtsTransformation);
			}			
			if (null != backupCtsPlanConfig) {
				updateConfigurations.restoreFile(ctsPlanConfig, backupCtsPlanConfig);
			}			
			if (null != backupConfBvAuthentication) {
				updateConfigurations.restoreFile(confBvAuthentication, backupConfBvAuthentication);
			}
			if (null != backupConfRuteoServicios) {
				updateConfigurations.restoreFile(confRuteoServicios, backupConfRuteoServicios);
			}
			if (null != backupConfRuteoServiciosBatch) {
				updateConfigurations.restoreFile(confRuteoServiciosBatch, backupConfRuteoServiciosBatch);
			}
			if (null != backupconfExecutorChannelAdmin) {
				updateConfigurations.restoreFile(confExecutorChannelAdmin, backupconfExecutorChannelAdmin);
			}
			if (null != backupAuthenticationConf) {
				updateConfigurations.restoreFile(authenticationConf, backupAuthenticationConf);
			}
			if (null != backupIBServices) {
				updateConfigurations.restoreFile(iBServices, backupIBServices);
			}
			if (null != bakcupServices) {
				updateConfigurations.restoreFile(services, bakcupServices);
			}
			if (null != mapBackupJARFiles) {
				updateConfigurations.restoreJarCisClient(mapBackupJARFiles);
			}
			if (null != backupCobisSessionManager) {
				updateConfigurations.restoreFile(cobisSessionManager, backupCobisSessionManager);
			}
			if (null != backupCobisSsnUnique) {
				updateConfigurations.restoreFile(cobisSsnUnique, backupCobisSsnUnique);
			}
			if (null != bkCobisBvSsnUnique) {
				updateConfigurations.restoreFile(cobisBvSsnUnique, bkCobisBvSsnUnique);
			}
			if (null != bkBusinessServExecutor) {
				updateConfigurations.restoreFile(businessServExecutor, bkBusinessServExecutor);
			}
			if (null != bkServExecutor) {
				updateConfigurations.restoreFile(servExecutor, bkServExecutor);
			}
			if (null != backupPlugins) {
				try {
					FileUtils.copyDirectory(new File(backupPlugins), new File(
							pluginsIB));
				} catch (IOException ex) {
					logger.error("******* ERROR in restore backups plugins files: \n"
							+ ex.getMessage());
				}
			}
			logger.error("******* ERROR in configurations files: \n" + e.getMessage());
			logger.error("******* ERROR e: \n" + e);
			logger.error("******* FINISH Restore Files *******");
			//throw new ValidatorException("******* ERROR in configurations files: \n" + e.getMessage());
			
			// RECOVER ERROR AND SHOW MESSAGE USER
			GeneralFunctions generalFunctions = new GeneralFunctions();			
			String message = generalFunctions.getResourceLangPack(selectedLang, e.getMessage());
			logger.error("Error message-->" + message);			
			this.handler.emitError("Error", message);
		}
		return sucessInstall;
	}

	//@Override
	public void finishProcess() {
		// TODO Auto-generated method stub
	}

	//@Override
	public void finishProcessing(boolean unlockPrev, boolean unlockNext) {
		// TODO Auto-generated method stub
	}

	//@Override
	public void logOutput(String message, boolean stderr) {
		// TODO Auto-generated method stub
	}

	//@Override
	public void startProcess(String name) {
		// TODO Auto-generated method stub
	}

	//@Override
	public void startProcessing(int no_of_processes) {
		// TODO Auto-generated method stub
	}

	//@Override
	public int askQuestion(String title, String question, int choices) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public int askQuestion(String title, String question, int choices, int default_choice) {
		// TODO Auto-generated method stub
		return 0;
	}

	//@Override
	public void emitError(String title, String message) {
		// TODO Auto-generated method stub		
	}

	//@Override
	public void emitErrorAndBlockNext(String title, String message) {
		// TODO Auto-generated method stub
	}

	//@Override
	public void emitNotification(String message) {
		// TODO Auto-generated method stub
	}

	//@Override
	public boolean emitWarning(String title, String message) {
		// TODO Auto-generated method stub
		return false;
	}
}