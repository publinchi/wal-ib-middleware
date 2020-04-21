/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;


import java.util.List;

import com.cobiscorp.ecobis.ib.orchestration.dtos.BatchEtlTotal;


/**
 * @author cecheverria
 * @since 07/01/2016
 * @version 1.0.0
 */

public class BatchEtlTotalResponse extends BaseResponse {
	private List<BatchEtlTotal> etlTotalCollection;

	public List<BatchEtlTotal> getEtlTotalCollection() {
		return etlTotalCollection;
	}

	public void setEtlTotalCollection(List<BatchEtlTotal> etlTotalCollection) {
		this.etlTotalCollection = etlTotalCollection;
	}


	
}
