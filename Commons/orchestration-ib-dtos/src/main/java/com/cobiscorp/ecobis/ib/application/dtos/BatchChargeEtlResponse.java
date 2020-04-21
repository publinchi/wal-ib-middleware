package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.List;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Assort;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Entity;
import com.cobiscorp.ecobis.ib.orchestration.dtos.EntityService;
import com.cobiscorp.ecobis.ib.orchestration.dtos.Log;

/**
 * @author dmorla
 * @since June 24, 2015
 * @version 1.0.0
 */
public class BatchChargeEtlResponse extends BaseResponse {

	private String Records;
	private Integer totalRecords;
	private Integer maxRecord;
	private List<EntityService> entityServiceCollection;
	private List<Entity> entityCollection;
	private List<Log> logColletion;

	/**
	 * @return the entityServiceCollection
	 */
	public List<EntityService> getEntityServiceCollection() {
		return entityServiceCollection;
	}

	/**
	 * @param entityServiceCollection
	 *            the entityServiceCollection to set
	 */
	public void setEntityServiceCollection(
			List<EntityService> entityServiceCollection) {
		this.entityServiceCollection = entityServiceCollection;
	}

	/**
	 * @return the entityCollection
	 */
	public List<Entity> getEntityCollection() {
		return entityCollection;
	}

	/**
	 * @param entityCollection
	 *            the entityCollection to set
	 */
	public void setEntityCollection(List<Entity> entityCollection) {
		this.entityCollection = entityCollection;
	}

	/**
	 * @return the logColletion
	 */
	public List<Log> getLogColletion() {
		return logColletion;
	}

	/**
	 * @param logColletion
	 *            the logColletion to set
	 */
	public void setLogColletion(List<Log> logColletion) {
		this.logColletion = logColletion;
	}

	/**
	 * @return the records
	 */
	public String getRecords() {
		return Records;
	}

	/**
	 * @param records
	 *            the records to set
	 */
	public void setRecords(String records) {
		Records = records;
	}

	/**
	 * @return the totalRecords
	 */
	public Integer getTotalRecords() {
		return totalRecords;
	}

	/**
	 * @param totalRecords
	 *            the totalRecords to set
	 */
	public void setTotalRecords(Integer totalRecords) {
		this.totalRecords = totalRecords;
	}

	/**
	 * @return the maxRecord
	 */
	public Integer getMaxRecord() {
		return maxRecord;
	}

	/**
	 * @param maxRecord
	 *            the maxRecord to set
	 */
	public void setMaxRecord(Integer maxRecord) {
		this.maxRecord = maxRecord;
	}

}
