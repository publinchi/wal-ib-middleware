/**
 * 
 */
package com.cobiscorp.ecobis.orchestration.core.ib.encryptdata;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;

import com.cobiscorp.cobis.cis.sp.java.orchestration.ICISSPBaseOrchestration;
import com.cobiscorp.cobis.cis.sp.java.orchestration.SPJavaOrchestrationBase;

import com.cobiscorp.cobis.commons.configuration.IConfigurationReader;
import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.csp.services.inproc.IOrchestrator;

import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureRequest;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetBlock;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetData;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetHeader;
import com.cobiscorp.cobis.cts.domains.sp.IResultSetRow;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetBlock;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetData;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeader;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetHeaderColumn;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRow;
import com.cobiscorp.cobis.cts.dtos.sp.ResultSetRowColumnData;
import com.cobiscorp.mobile.services.impl.utils.SimpleRSA;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * @author cecheverria
 * @since Sep 2, 2014
 * @version 1.0.0
 */
@Component(name = "EncryptDataOrchestrationCore", immediate = false)
@Service(value = { ICISSPBaseOrchestration.class, IOrchestrator.class })
@Properties(value = { @Property(name = "service.description", value = "EncryptDataOrchestrationCore"),
		@Property(name = "service.vendor", value = "COBISCORP"), @Property(name = "service.version", value = "1.0.0"),
		@Property(name = "service.identifier", value = "EncryptDataOrchestrationCore"),
		@Property(name = "service.spName", value = "cob_procesador..sp_encryptData")
})
public class EncryptDataOrchestrationCore extends SPJavaOrchestrationBase {// SPJavaOrchestrationBase
	
	private ILogger logger = (ILogger) this.getLogger();
	private SimpleRSA crypto = new SimpleRSA();	

	@Override
	public void loadConfiguration(IConfigurationReader aConfigurationReader) {
		
	}
	
	@Override
	public IProcedureResponse executeJavaOrchestration(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		logger.logDebug("Begin flow, EncryptDataOrchestrationCore start.");
		boolean wQueryEncrypData;
		aBagSPJavaOrchestration.put("anOriginalRequest", anOriginalRequest);
		wQueryEncrypData = queryEncrypData(aBagSPJavaOrchestration);
		
		if (wQueryEncrypData) {
			logger.logDebug("Ending flow, executeJavaOrchestration success.");
			return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
		}
		
		logger.logDebug("Ending flow, executeJavaOrchestration failed.");
		return processResponse(anOriginalRequest, aBagSPJavaOrchestration);
	}
	
	private Boolean queryEncrypData(java.util.Map aBagSPJavaOrchestration) {
		
		IProcedureRequest wQueryEncrypDataRequest = (IProcedureRequest) aBagSPJavaOrchestration.get("anOriginalRequest");
		String password = wQueryEncrypDataRequest.readValueParam("@i_password");
		logger.logDebug("Begin flow, queryEncrypData with password: " + password);
		
		IProcedureResponse wProcedureResponse = new ProcedureResponseAS();

		IResultSetHeader metaData = new ResultSetHeader();
		IResultSetData data = new ResultSetData();
		IResultSetRow row = new ResultSetRow();

		metaData.addColumnMetaData(new ResultSetHeaderColumn("success", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("password", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("message", ICTSTypes.SYBVARCHAR, 255));
		metaData.addColumnMetaData(new ResultSetHeaderColumn("code", ICTSTypes.SYBINT2, 2));
		if (password.equals(""))
		{
			row.addRowData(1, new ResultSetRowColumnData(false, "false"));
			row.addRowData(2, new ResultSetRowColumnData(false, ""));
			row.addRowData(3, new ResultSetRowColumnData(false, "Data must not be empty"));
			row.addRowData(4, new ResultSetRowColumnData(false, "40001"));
			data.addRow(row);

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock);

			aBagSPJavaOrchestration.put("wQueryEncrypDataResp", 
					 wProcedureResponse);
			
			logger.logDebug("Ending flow, queryEncrypData failed.");
			return false;
		}
		
		//Encrypt
		String passEncrypt = encryptPassword(password);
		if (passEncrypt.equals(""))
		{
			row.addRowData(1, new ResultSetRowColumnData(false, "false"));
			row.addRowData(2, new ResultSetRowColumnData(false, ""));
			row.addRowData(3, new ResultSetRowColumnData(false, "Error encrypting data"));
			row.addRowData(4, new ResultSetRowColumnData(false, "50001"));
			data.addRow(row);

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock);

			aBagSPJavaOrchestration.put("wQueryEncrypDataResp", 
					 wProcedureResponse);
			
			logger.logDebug("Ending flow, queryEncrypData failed.");
			return false;
		} else {
			row.addRowData(1, new ResultSetRowColumnData(false, "true"));
			row.addRowData(2, new ResultSetRowColumnData(false, passEncrypt));
			row.addRowData(3, new ResultSetRowColumnData(false, "SUCCESS"));
			row.addRowData(4, new ResultSetRowColumnData(false, "0"));
			data.addRow(row);

			IResultSetBlock resultBlock = new ResultSetBlock(metaData, data);
			wProcedureResponse.addResponseBlock(resultBlock);

			aBagSPJavaOrchestration.put("wQueryEncrypDataResp", 
					 wProcedureResponse);
			
			logger.logDebug("Ending flow, queryEncrypData success.");
			return true;
		}
	}

	@Override
	public IProcedureResponse processResponse(IProcedureRequest anOriginalRequest, Map<String, Object> aBagSPJavaOrchestration) {
		return (IProcedureResponse) aBagSPJavaOrchestration.get("wQueryEncrypDataResp");
	}

	public String encryptPassword(String password)
	{
		logger.logDebug("Begin flow, encryptPassword start.");
		String publickey = getPublicKey();
		if (!publickey.equals(""))
		{
			try {			
				String encrypt = crypto.encrypt(password, publickey);
				logger.logDebug("Ending flow, encryptPassword encrypt: " + encrypt);
				return encrypt;
			} catch (Exception e) {
				// TODO Auto-generated catch block	
				e.printStackTrace();
			}
		}
		
		logger.logDebug("Ending flow, encryptPassword failed");
		return "";
	}
	
	public String getPublicKey() 
	{
		logger.logDebug("Begin flow, getPublicKey start.");
		File xmlFile = new File("/cobis/cobishome/CTS_MF/services-as/securityBM/security-config.xml");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
			Document doc = builder.parse(xmlFile);
			String publickey = doc.getElementsByTagName("public-keyrsa").item(0).getTextContent();
			logger.logDebug("Ending flow, getPublicKey publickey: " + publickey);
			return publickey;
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger.logDebug("Ending flow, getPublicKey failed.");
		return "";
	}
}
