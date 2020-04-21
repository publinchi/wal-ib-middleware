
package com.cobiscorp.ecobis.ib.application.dtos;


import java.util.List;

import com.cobiscorp.ecobis.ib.orchestration.dtos.BatchProductOpening;

/**
 * @author mmoya
 * @since Jan 27, 2015
 * @version 1.0.0
 */
public class BatchProductOpeningResponse extends BaseResponse {
	
private List<BatchProductOpening> batchProductOpeningList;
private Integer maxProduct;
private Integer maxCustomer;
private Integer maxCurrency;
private String  maxAccount;
private Integer  secuential;

/**
 * @return the maxProduct
 */
public Integer getMaxProduct() {
	return maxProduct;
}

public Integer getMaxCustomer() {
	return maxCustomer;
}

public void setMaxCustomer(Integer maxCustomer) {
	this.maxCustomer = maxCustomer;
}

public Integer getMaxCurrency() {
	return maxCurrency;
}

public void setMaxCurrency(Integer maxCurrency) {
	this.maxCurrency = maxCurrency;
}

public String getMaxAccount() {
	return maxAccount;
}

public void setMaxAccount(String maxAccount) {
	this.maxAccount = maxAccount;
}

/**
 * @param maxProduct the maxProduct to set
 */
public void setMaxProduct(Integer maxProduct) {
	this.maxProduct = maxProduct;
}

/**
 * @return the secuential
 */
public Integer getSecuential() {
	return secuential;
}

/**
 * @param secuential the secuential to set
 */
public void setSecuential(Integer secuential) {
	this.secuential = secuential;
}

/**
 * @return the batchProductOpeningList
 */
public List<BatchProductOpening> getBatchProductOpeningList() {
	return batchProductOpeningList;
}

/**
 * @param batchProductOpeningList the batchProductOpeningList to set
 */
public void setBatchProductOpeningList(
		List<BatchProductOpening> batchProductOpeningList) {
	this.batchProductOpeningList = batchProductOpeningList;
}

/* (non-Javadoc)
 * @see java.lang.Object#toString()
 */
@Override
public String toString() {
	return "BatchProductOpeningResponse [batchProductOpeningList="
			+ batchProductOpeningList + ", maxProduct=" + maxProduct + "]";
}



}