/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.List;

import com.cobiscorp.ecobis.ib.orchestration.dtos.GRB;
 
/**
 * @author gyagual
 * @since 16/10/2015
 * @version 1.0.0
 */
public class QueryBankGuaranteeResponse extends BaseResponse {
	private List<GRB> bankGuaranteeCollection;

	/**
	 * @return the bankGuaranteeCollection
	 */
	public List<GRB> getBankGuaranteeCollection() {
		return bankGuaranteeCollection;
	}

	/**
	 * @param bankGuaranteeCollection the bankGuaranteeCollection to set
	 */
	public void setBankGuaranteeCollection(List<GRB> bankGuaranteeCollection) {
		this.bankGuaranteeCollection = bankGuaranteeCollection;
	}
}
