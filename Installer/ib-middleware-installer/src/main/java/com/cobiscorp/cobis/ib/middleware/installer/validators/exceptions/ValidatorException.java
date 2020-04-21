/**
 * 
 */
package com.cobiscorp.cobis.ib.middleware.installer.validators.exceptions;


/**
 * @author smejia
 *
 */
public class ValidatorException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8224252473657836765L;
	//Logger logger = LoggerFactory.getLogger(ValidatorException.class);    

	public ValidatorException(String message){
		super(message);
		/*logger.error("*************************** ERROR Message ***************************");
		logger.error(message);*/
    }
}
