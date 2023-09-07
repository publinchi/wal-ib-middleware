
package com.cobiscorp.ecobis.orchestration.core.ib.validate.all.security.qa.api;

import java.util.Map;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Service;

import com.cobiscorp.cobis.cis.sp.java.orchestration.CISResponseManagmentHelper;
import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;
import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;
import com.cobiscorp.cobis.cts.commons.configuration.CTSGeneralConfiguration;
import com.cobiscorp.cobis.cts.commons.services.IMultiBackEndResolverService;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRowColumnData;
import com.cobiscorp.cobis.cts.dtos.ProcedureRequestAS;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import cobiscorp.ecobis.cts.integration.services.ICTSServiceIntegration;

/**
 * Generated Transaction Factor
 * 
 * @since May 15, 2023
 * @author sochoa
 * @version 1.0.0
 * 
 */
@Component(name = "ValidateAllSecurityQAApiOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "ValidateAllSecurityQAApiOrchestrationCore"),
	@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "4.6.1.0"),
	@Property(name = "service.identifier", value = "ValidateAllSecurityQAApiOrchestrationCore"),
	@Property(name = "service.spName", value = "cob_procesador..sp_val_all_security_qa_api") })
public class ValidateAllSecurityQAApiOrchestrationCore extends SPJavaOrchestrationBase {

	private static ILogger logger = LogFactory.getLogger(ValidateAllSecurityQAApiOrchestrationCore.class);
	private static final String CLASS_NAME = "ValidateAllSecurityQAApiOrchestrationCore--->";
	
	public static final String ALGORITHM = "RSA";
	private static final String UTF_8 = "UTF-8";
	private byte[] privateKey; 
	
	CISResponseManagmentHelper cisResponseHelper = new CISResponseManagmentHelper();
	
	protected static final String CHANNEL_REQUEST = "8";
	
	/**
	 * Instance of ICTSServiceIntegration
	 */
	@Reference(bind = "setServiceIntegration", unbind = "unsetServiceIntegration", cardinality = ReferenceCardinality.OPTIONAL_UNARY)
	private ICTSServiceIntegration serviceIntegration;
	
	/**
	 * Method that set the instance of ICTSServiceIntegration
	 */
	public void setServiceIntegration(ICTSServiceIntegration serviceIntegration) {
		this.serviceIntegration = serviceIntegration;
	}
	
	/**
	 * Method that unset the instance of ICTSServiceIntegration
	 */
	public void unsetServiceIntegration(ICTSServiceIntegration serviceIntegration) {
		this.serviceIntegration = null;
	}

	/**
	 * Read configuration of parent component
	 */
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {

	}

	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest,
			Map<String, Object> aBagSPJavaOrchestration) {
		
		//params decryption
		
		String dQuestion1= anOriginalRequest.readValueParam("@i_question_1_id");
		String dAnswer1= anOriginalRequest.readValueParam("@i_answer_1_id");
		
		String dQuestion2= anOriginalRequest.readValueParam("@i_question_2_id");
		String dAnswer2= anOriginalRequest.readValueParam("@i_answer_2_id");
		
		String dQuestion3= anOriginalRequest.readValueParam("@i_question_3_id");
		String dAnswer3= anOriginalRequest.readValueParam("@i_answer_desc");
		
		if (logger.isInfoEnabled()) {
			logger.logDebug("Calling decryption method...");
		}
		
		try {
			
			logger.logInfo(dQuestion1);	
			dQuestion1 = decrypt(dQuestion1);
			logger.logInfo(dQuestion1);
			
			logger.logInfo(dAnswer1);
			dAnswer1 = decrypt(dAnswer1);
			logger.logInfo(dAnswer1);
			
			logger.logInfo(dQuestion2);
			dQuestion2 = decrypt(dQuestion2);
			logger.logInfo(dQuestion2);
			
			logger.logInfo(dAnswer2);
			dAnswer2 = decrypt(dAnswer2);
			logger.logInfo(dAnswer2);
			
			logger.logInfo(dQuestion3);
			dQuestion3 = decrypt(dQuestion3);
			logger.logInfo(dQuestion3);
			
			logger.logInfo(dAnswer3);
			dAnswer3 = decrypt(dAnswer3);
			logger.logInfo(dAnswer3);
			
		} catch (Exception e) {
			
			logger.logError("Exception is: "+e);
				
			dQuestion1 = "I";
			dAnswer1 = "I";
			dQuestion2 = "I";
			dAnswer2 = "I";
			dQuestion3 = "I";
			dAnswer3 = "I";
		}
		
		if (dAnswer1 != "I") {
			
			dAnswer1 = anOriginalRequest.readValueParam("@i_answer_1_id");
		}
		
		if (dAnswer2 != "I") {
			
			dAnswer2 = anOriginalRequest.readValueParam("@i_answer_2_id");
		}
		
		if (dAnswer3 != "I") {
			
			dAnswer3 = anOriginalRequest.readValueParam("@i_answer_desc");
		}
		
		aBagSPJavaOrchestration.put("dQuestion1", dQuestion1);
		aBagSPJavaOrchestration.put("answer1",   dAnswer1);
		aBagSPJavaOrchestration.put("dQuestion2", dQuestion2);
		aBagSPJavaOrchestration.put("answer2", dAnswer2);
		aBagSPJavaOrchestration.put("dQuestion3", dQuestion3);
		aBagSPJavaOrchestration.put("answer3", dAnswer3);

		IProcedureResponse anProcedureResponse = new ProcedureResponseAS();
		anProcedureResponse = validateAllSecurityQa(anOriginalRequest, aBagSPJavaOrchestration);
		
		if (anProcedureResponse.getResultSets().size()>2) {
			
			return processTransformationResponse(anProcedureResponse);
		} else {
			return processResponseError(anProcedureResponse);
		}
	}

	private IProcedureResponse validateAllSecurityQa(IProcedureRequest aRequest, Map<String, Object> aBagSPJavaOrchestration) {
		
		IProcedureRequest request = new ProcedureRequestAS();
		
		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " entering defineSecurityQa");
		}
	
		request.setSpName("cob_bvirtual..sp_val_all_security_qa_api");

		request.addFieldInHeader(ICOBISTS.HEADER_TARGET_ID, ICOBISTS.HEADER_STRING_TYPE,
				IMultiBackEndResolverService.TARGET_LOCAL);
		request.setValueFieldInHeader(ICOBISTS.HEADER_CONTEXT_ID, "COBIS");
		
		
		request.addInputParam("@i_external_customer_id", ICTSTypes.SQLINTN, aRequest.readValueParam("@i_external_customer_id"));
		
		request.addInputParam("@i_question_1_id", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("dQuestion1"));
		request.addInputParam("@i_answer_1_id", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("answer1"));
		
		request.addInputParam("@i_question_2_id", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("dQuestion2"));
		request.addInputParam("@i_answer_2_id", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("answer2"));
		
		request.addInputParam("@i_question_3_id", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("dQuestion3"));
		request.addInputParam("@i_answer_3_id", ICTSTypes.SQLVARCHAR, (String) aBagSPJavaOrchestration.get("answer3"));
		
		
		IProcedureResponse wProductsQueryResp = executeCoreBanking(request);

		if (logger.isDebugEnabled()) {
			logger.logDebug("Response validateAllSecurityQa corebanking DCO: " + wProductsQueryResp.getProcedureResponseAsString());
		}

		if (logger.isInfoEnabled()) {
			logger.logInfo(CLASS_NAME + " leaving validateAllSecurityQa");
		}

		return wProductsQueryResp;
	}
	
	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalProcedureReq,
			Map<String, Object> aBagSPJavaOrchestration) {

		return null;
	}

	public IProcedureResponse processTransformationResponse(IProcedureResponse anOriginalProcedureRes) {
		if (logger.isInfoEnabled()) {
			logger.logInfo("Starting processTransformationResponse--->");
		}

		IProcedureResponse anOriginalProcedureResponse = new ProcedureResponseAS();

		if (anOriginalProcedureRes != null) {

			if (logger.isInfoEnabled()) {
				logger.logInfo(CLASS_NAME + " processResponse original anOriginalProcedureRes"
						+ anOriginalProcedureRes.getProcedureResponseAsString());
			}

		}

		//Agregar Header 1
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));

		// Agregar Header 2
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));
			
		/***************************************success***************************************/
		
		IResultSetRow row = new ResultSetRow();
		row.addRowData(1, new ResultSetRowColumnData(false, "true"));
		data.addRow(row);
		
		/***************************************code/message************************************/
		
		IResultSetRow row2 = new ResultSetRow();
		row2.addRowData(1, new ResultSetRowColumnData(false, "0"));
		row2.addRowData(2, new ResultSetRowColumnData(false, "Success"));
		data2.addRow(row2);
		
		//return		
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
		
		anOriginalProcedureResponse.setReturnCode(200);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock2);
		
		logger.logInfo(CLASS_NAME + " processTransformationResponse final DCO response" + anOriginalProcedureResponse.getProcedureResponseAsString());
		return anOriginalProcedureResponse;
	}
	
	public IProcedureResponse processResponseError(IProcedureResponse anOriginalProcedureRes) {
		if (logger.isInfoEnabled()) {
			logger.logInfo("Starting processResponseError validateAllSecurityQA--->");
		}

		IProcedureResponse anOriginalProcedureResponse = new ProcedureResponseAS();
		
		// Agregar Header 1
		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		
		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SQLBIT, 5));
	
		// Agregar Header 2
		IResultSetHeader metaData2 = new ResultSetHeader();
		IResultSetData data2 = new ResultSetData();
		
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SQLINT4, 8));
		metaData2.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SQLVARCHAR, 100));
		
		
		/***************************************success***************************************/

		IResultSetRow row = new ResultSetRow();
		
		row.addRowData(1, 
				new ResultSetRowColumnData(false, anOriginalProcedureRes.getResultSetRowColumnData(1, 1, 1).getValue()));
		
		data.addRow(row);
		
		/***************************************code/message************************************/
		
		IResultSetRow row2 = new ResultSetRow();

		row2.addRowData(1,
				new ResultSetRowColumnData(false, anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 1).getValue()));
		row2.addRowData(2, 
				new ResultSetRowColumnData(false, anOriginalProcedureRes.getResultSetRowColumnData(2, 1, 2).getValue()));

		data2.addRow(row2);
		
		
		//return
		IResultSetBlock resultsetBlock = new ResultSetBlock(metaData, data);
		IResultSetBlock resultsetBlock2 = new ResultSetBlock(metaData2, data2);
	
		
		anOriginalProcedureResponse.setReturnCode(200);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock);
		anOriginalProcedureResponse.addResponseBlock(resultsetBlock2);


		return anOriginalProcedureResponse;
	}
	
	public String decrypt(String cifrado) throws Exception {

		if (logger.isInfoEnabled()) {
			logger.logDebug("Starting decryption..., private Key ValidateAllSecurityQA path: "
					+ CTSGeneralConfiguration.getEnvironmentVariable("COBIS_HOME", 0));
		}

		if (privateKey == null) {
			privateKey = Files.readAllBytes(Paths.get(CTSGeneralConfiguration.getEnvironmentVariable("COBIS_HOME", 0)
					+ "/CTS_MF/services-as/securityAPI/Questions_Private.key"));
		}

		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);

		PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(keySpec);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);

		byte[] encryptedBytes = Base64.getDecoder().decode(cifrado);
		byte[] plainText = cipher.doFinal(encryptedBytes);

		String decryptedText = new String(plainText, StandardCharsets.UTF_8);

		logger.logDebug("Ending decryption, param decryption: " + decryptedText);

		return decryptedText;
	}
}
