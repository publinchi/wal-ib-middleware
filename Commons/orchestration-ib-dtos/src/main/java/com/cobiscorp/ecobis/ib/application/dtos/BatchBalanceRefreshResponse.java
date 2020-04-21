
package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.List;

import com.cobiscorp.ecobis.ib.orchestration.dtos.BatchAcountsRefreshBalance;


/**
 * @author jbaque
 * @since Feb 11, 2015
 * @version 1.0.0
 */
public class BatchBalanceRefreshResponse extends BaseResponse {
	private List<BatchAcountsRefreshBalance> BatchAcountsRefreshBalanceCollection;
	private String Records;
	private Integer totalRecords;
	private Integer maxRecord;
	
	/**
	 * @return the batchAcountsRefreshBalanceCollection
	 */
	public List<BatchAcountsRefreshBalance> getBatchAcountsRefreshBalanceCollection() {
		return BatchAcountsRefreshBalanceCollection;
	}

	/**
	 * @param batchAcountsRefreshBalanceCollection the batchAcountsRefreshBalanceCollection to set
	 */
	public void setBatchAcountsRefreshBalanceCollection(
			List<BatchAcountsRefreshBalance> batchAcountsRefreshBalanceCollection) {
		BatchAcountsRefreshBalanceCollection = batchAcountsRefreshBalanceCollection;
	}

	/**
	 * @return the records
	 */
	public String getRecords() {
		return Records;
	}

	/**
	 * @param records the records to set
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
	 * @param totalRecords the totalRecords to set
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
	 * @param maxRecord the maxRecord to set
	 */
	public void setMaxRecord(Integer maxRecord) {
		this.maxRecord = maxRecord;
	}	
	
}
