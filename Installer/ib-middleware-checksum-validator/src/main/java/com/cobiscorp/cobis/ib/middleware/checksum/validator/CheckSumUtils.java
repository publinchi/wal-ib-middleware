/**
 * 
 */
package com.cobiscorp.cobis.ib.middleware.checksum.validator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.cobiscorp.cobis.ib.middleware.checksum.validator.dto.CheckSumFile;

/**
 * @author smejia
 * 
 */
public class CheckSumUtils {

	Logger logger = Logger.getLogger(CheckSumUtils.class);
	

	/**
	 * @author smejia Sandra Mejia J.
	 * @throws IOException
	 */
	public void setProperties() throws IOException {
		Properties properties = new Properties();
		properties.load(getClass().getResourceAsStream("/log4j.properties"));
		PropertyConfigurator.configure(properties);
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param fis
	 * @return
	 * @throws IOException
	 */
	public String md5File(InputStream fis) throws IOException {
		String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
		return md5;
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public InputStream readFile(String path) throws IOException {
		InputStream is = getClass().getResourceAsStream(path);
		return is;
	}

	/**
	 * @author smejia Sandra Mejia J.
	 * @param filePath
	 * @param checkSumFiles
	 * @return
	 */
	public List<CheckSumFile> getListCVS(String filePath,  String checkSumFiles) {
		List<CheckSumFile> csfList = new ArrayList<CheckSumFile>();
		
		try {			
			InputStream in = getClass().getResourceAsStream(checkSumFiles);
			InputStreamReader isr = new InputStreamReader(in);			
			BufferedReader br = new BufferedReader(isr);
			String stringRead = br.readLine();

			while (stringRead != null) {
				StringTokenizer st = new StringTokenizer(stringRead, ",");
				String fileName = st.nextToken();
				String version = st.nextToken();
				String filePathCTS = st.nextToken();
				String filePathLastVersion = st.nextToken();

				CheckSumFile temp = new CheckSumFile(fileName, version,
						filePathCTS, filePathLastVersion);
				csfList.add(temp);
				stringRead = br.readLine();
			}
			br.close();
		} catch (IOException e) {
			logger.error("******* ERROR  failed copy source file: \n"
					+ filePath);
			logger.error("******* ERROR  Message: \n" + e.getMessage());
		}
		return csfList;
	}
	
	/**
	 * @author smejia Sandra Mejia J.
	 * @param cobisHome
	 * @param checkSumFiles
	 * @return
	 * @throws IOException
	 */
	public boolean validateCheckSumFiles(String cobisHome, String checkSumFiles, String Core) throws IOException{
		boolean error = true;
		String PathOrchestration = "/modules/CIS/SERVICES/plugins/IBOrchestration";
		List<CheckSumFile> csfList = this.getListCVS(cobisHome,checkSumFiles);    	
    	for (CheckSumFile item : csfList) 
    	{
    		String version = item.getVersion();
    		String fileName = null;
    		
    		if("0".equals(version)){        		
    			fileName = item.getFileName();
    		}else{
    			fileName = item.getFileName().concat("-").concat(version).concat(".jar");
    		}
    		String filePath = "";   		
    		String fileValida = item.getFilePathLastVersion();   
    		
    		if (fileValida.equals(PathOrchestration)){
    			filePath = item.getFilePathLastVersion().concat("/"+Core+"/"+fileName);
    		} else{
    			filePath = item.getFilePathLastVersion().concat("/"+fileName);
    		}     		
    		try{
    			InputStream lastVersionFile = this.readFile(filePath);
            	String lastVersion = this.md5File(lastVersionFile);
            	String fileCTSPath = item.getFilePathCTS().concat("/"+fileName);
            	File actualVersionFile = new File(cobisHome.concat(fileCTSPath));
            	if(actualVersionFile.exists()){        	
    	        	FileInputStream actualVersionFis = new FileInputStream(actualVersionFile);
    	        	String actualVersion = this.md5File(actualVersionFis);
    	        	if(!lastVersion.equals(actualVersion))
    	        	{
    	        		error = false;
    	        		logger.error("*******ERROR, File does not match with the last version: " + actualVersionFile.getAbsolutePath());
    	        	}
            	}else{
            		error = false;
            		logger.error("*******ERROR, File does not exist: " + actualVersionFile.getAbsolutePath());
            	}     
    		}catch(Exception e){
    			error = false;
    			logger.error("*******ERROR, File does not exist: " + filePath);
    		}    	      
    	}
    	
    	if(error){
    		logger.info("===================== CHECKSUM VALIDATION SUCCESSFUL =====================");
    	}
    	else{
    		logger.info("*******ERROR, There are some outdated files, please review the log file");
    	}
    	
    	return error;
	}
}