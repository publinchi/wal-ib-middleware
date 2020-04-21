/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author jlvidal
 * @since 4/11/2014
 * @version 1.0.0
 */
public class User {

	private Integer entityId;
	private String name;
	private Integer serviceId;
	/**
	 * @return the entityId
	 */
	public Integer getEntityId() {
		return entityId;
	}
	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	public Integer getServiceId() {
		return serviceId;
	}
	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	@Override
	public String toString() {
		return "User [entityId=" + entityId + ", name=" + name + ", serviceId="
				+ serviceId + "]";
	}
	
}
