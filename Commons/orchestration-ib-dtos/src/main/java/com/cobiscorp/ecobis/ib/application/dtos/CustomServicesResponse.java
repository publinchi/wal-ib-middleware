/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.List;

import com.cobiscorp.ecobis.ib.orchestration.dtos.CustomServices;

/**
 * @author itorres
 * @since Mar 3, 2015
 * @version 1.0.0
 */
public class CustomServicesResponse extends BaseResponse{
	private List<CustomServices> customServicesCollection;

	/**
	 * @return the customServicesVollection
	 */
	public List<CustomServices> getCustomServicesCollection() {
		return customServicesCollection;
	}

	/**
	 * @param customServicesVollection the customServicesVollection to set
	 */
	public void setCustomServicesCollection(
			List<CustomServices> customServicesCollection) {
		this.customServicesCollection = customServicesCollection;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CustomServicesResponse [customServicesVollection="
				+ customServicesCollection + "]";
	}	

}
