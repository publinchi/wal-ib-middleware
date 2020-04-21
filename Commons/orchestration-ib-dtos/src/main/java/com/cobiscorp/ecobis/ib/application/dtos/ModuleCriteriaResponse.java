/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.List;

import com.cobiscorp.ecobis.ib.orchestration.dtos.ModuleCriteria;

 

/**
 * @author gyagual
 * @since 01/07/2015
 * @version 1.0.0
 */
public class ModuleCriteriaResponse extends BaseResponse {
	private List<ModuleCriteria> mCriteriaCollection;
	private List<ModuleCriteria> mLabelCollection;

	/**
	 * @return the mLabelCollection
	 */
	public List<ModuleCriteria> getmLabelCollection() {
		return mLabelCollection;
	}

	/**
	 * @param mLabelCollection the mLabelCollection to set
	 */
	public void setmLabelCollection(List<ModuleCriteria> mLabelCollection) {
		this.mLabelCollection = mLabelCollection;
	}

	/**
	 * @return the mCriteriaCollection
	 */
	public List<ModuleCriteria> getmCriteriaCollection() {
		return mCriteriaCollection;
	}

	/**
	 * @param mCriteriaCollection the mCriteriaCollection to set
	 */
	public void setmCriteriaCollection(List<ModuleCriteria> mCriteriaCollection) {
		this.mCriteriaCollection = mCriteriaCollection;
	}
}
