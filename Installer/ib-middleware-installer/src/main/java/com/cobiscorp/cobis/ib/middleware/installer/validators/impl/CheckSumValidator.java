/**
 * 
 */
package com.cobiscorp.cobis.ib.middleware.installer.validators.impl;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.cobiscorp.cobis.ib.middleware.checksum.validator.CheckSumUtils;
import com.cobiscorp.cobis.ib.middleware.installer.validators.AbstractDataValidator;
import com.izforge.izpack.installer.AutomatedInstallData;

/**
 * @author smejia
 *
 */
public class CheckSumValidator extends AbstractDataValidator  {
	
	Logger logger = Logger.getLogger(CheckSumValidator.class);
	//String Core = "";
	//String path = "";
	
	/* (non-Javadoc)
	 * @see com.izforge.izpack.installer.DataValidator#validateData(com.izforge.izpack.installer.AutomatedInstallData)
	 */
	//@Override
	public Status validateData(AutomatedInstallData arg0) {	
		String TypeCore =  arg0.getVariables().getProperty("core");		
		
		String cobisHome = arg0.getInstallPath().trim();
		CheckSumUtils checkSum = new CheckSumUtils();		
		try {
			boolean flag = checkSum.validateCheckSumFiles(cobisHome, com.cobiscorp.cobis.ib.middleware.checksum.validator.CheckSumValidator.CHECK_SUM_FILES, TypeCore);
			if(!flag){				
				logger.info("******* ERROR CHECK SUM VALIDATOR \n Contact to System Administrator");
				return Status.ERROR;
			}
			return Status.OK;
		} catch (IOException e) {
			logger.error("*******ERROR IOException: " + e.getLocalizedMessage());
			return Status.ERROR;
		}		
	}
}
