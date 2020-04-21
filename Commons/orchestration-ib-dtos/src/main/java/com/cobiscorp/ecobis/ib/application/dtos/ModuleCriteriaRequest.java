/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

/**
 * @author gyagual
 * @since 01/07/2015
 * @version 1.0.0
 */
public class ModuleCriteriaRequest  extends BaseRequest {

	private String operativeId;
	private Integer moduleCode;
	/**
	 * @return the operativeId
	 */
	public String getOperativeId() {
		return operativeId;
	}
	/**
	 * @param operativeId the operativeId to set
	 */
	public void setOperativeId(String operativeId) {
		this.operativeId = operativeId;
	}
	/**
	 * @return the moduleCode
	 */
	public Integer getModuleCode() {
		return moduleCode;
	}
	/**
	 * @param moduleCode the moduleCode to set
	 */
	public void setModuleCode(Integer moduleCode) {
		this.moduleCode = moduleCode;
	}
	
}
