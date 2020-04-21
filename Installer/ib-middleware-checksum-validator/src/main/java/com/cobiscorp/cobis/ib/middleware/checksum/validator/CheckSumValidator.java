package com.cobiscorp.cobis.ib.middleware.checksum.validator;

import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * Hello world!
 *
 */
public class CheckSumValidator 
{	
	public static final String CHECK_SUM_FILES = "/checkSumFiles.csv";
	
	/**
	 * @author smejia Sandra Mejia J.
	 * @param args
	 */
    public static void main( String[] args )
    {
    	Logger logger = Logger.getLogger(CheckSumValidator.class);    	
    	String cobisHome = args[0];
        CheckSumUtils checkSum = new CheckSumUtils();
        logger.info("===================== START CHECKSUM VALIDATION =====================");
		
        try {
        	checkSum.setProperties();
        	checkSum.validateCheckSumFiles(cobisHome, CHECK_SUM_FILES, "");
        	logger.info("===================== FINISH CHECKSUM VALIDATION =====================");
			
		} catch (IOException e) {
			logger.error("*******ERROR IOException: " + e.getLocalizedMessage());
		}
    }
}