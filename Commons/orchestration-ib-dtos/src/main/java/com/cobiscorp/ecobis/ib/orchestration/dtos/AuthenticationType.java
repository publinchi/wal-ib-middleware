/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;


/**
 * @author bborja
 * @since 20/05/2015
 * @version 1.0.0
 */
public class AuthenticationType{

	private short checkValue;
	private short productCode;
	private int instrumentCode;
	private int subTypeCode;
	private String subTypeName;
	private String literalSeries;
	private int seriesFrom;
	private int seriesTo;
	private int availableQty;
	/**
	 * @return the checkValue
	 */
	public short getCheckValue() {
		return checkValue;
	}
	/**
	 * @param checkValue the checkValue to set
	 */
	public void setCheckValue(short checkValue) {
		this.checkValue = checkValue;
	}
	/**
	 * @return the productCode
	 */
	public short getProductCode() {
		return productCode;
	}
	/**
	 * @param productCode the productCode to set
	 */
	public void setProductCode(short productCode) {
		this.productCode = productCode;
	}
	/**
	 * @return the instrumentCode
	 */
	public int getInstrumentCode() {
		return instrumentCode;
	}
	/**
	 * @param instrumentCode the instrumentCode to set
	 */
	public void setInstrumentCode(int instrumentCode) {
		this.instrumentCode = instrumentCode;
	}
	/**
	 * @return the subTypeCode
	 */
	public int getSubTypeCode() {
		return subTypeCode;
	}
	/**
	 * @param subTypeCode the subTypeCode to set
	 */
	public void setSubTypeCode(int subTypeCode) {
		this.subTypeCode = subTypeCode;
	}
	/**
	 * @return the subTypeName
	 */
	public String getSubTypeName() {
		return subTypeName;
	}
	/**
	 * @param subTypeName the subTypeName to set
	 */
	public void setSubTypeName(String subTypeName) {
		this.subTypeName = subTypeName;
	}
	/**
	 * @return the literalSeries
	 */
	public String getLiteralSeries() {
		return literalSeries;
	}
	/**
	 * @param literalSeries the literalSeries to set
	 */
	public void setLiteralSeries(String literalSeries) {
		this.literalSeries = literalSeries;
	}
	/**
	 * @return the seriesFrom
	 */
	public int getSeriesFrom() {
		return seriesFrom;
	}
	/**
	 * @param seriesFrom the seriesFrom to set
	 */
	public void setSeriesFrom(int seriesFrom) {
		this.seriesFrom = seriesFrom;
	}
	/**
	 * @return the seriesTo
	 */
	public int getSeriesTo() {
		return seriesTo;
	}
	/**
	 * @param seriesTo the seriesTo to set
	 */
	public void setSeriesTo(int seriesTo) {
		this.seriesTo = seriesTo;
	}
	/**
	 * @return the availableQty
	 */
	public int getAvailableQty() {
		return availableQty;
	}
	/**
	 * @param availableQty the availableQty to set
	 */
	public void setAvailableQty(int availableQty) {
		this.availableQty = availableQty;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AuthenticationType [checkValue=" + checkValue
				+ ", productCode=" + productCode + ", instrumentCode="
				+ instrumentCode + ", subTypeCode=" + subTypeCode
				+ ", subTypeName=" + subTypeName + ", literalSeries="
				+ literalSeries + ", seriesFrom=" + seriesFrom + ", seriesTo="
				+ seriesTo + ", availableQty=" + availableQty + "]";
	}

}
