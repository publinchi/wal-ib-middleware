package com.cobiscorp.ecobis.ib.application.dtos;

/**
 * 
 */


/**
 * @author bborja
 * @since 19/5/2015
 * @version 1.0.0
 */
public class AuthenticationTypeRequest extends BaseRequest {

	private String operationType;
	private short productCode;
	private int instrumentCode;
	private int subTypeCode;
	private String literalSeries;
	private double seriesFrom;
	private double seriesTo;
	private int area;
	private int areaOfficerCode;
	private short mode;
	private short parameter;
	private String remittanceType;

	/**
	 * @return the operationType
	 */
	public String getOperationType() {
		return operationType;
	}
	/**
	 * @param operationType the operationType to set
	 */
	public void setOperationType(String operationType) {
		this.operationType = operationType;
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
	public double getSeriesFrom() {
		return seriesFrom;
	}
	/**
	 * @param seriesFrom the seriesFrom to set
	 */
	public void setSeriesFrom(double seriesFrom) {
		this.seriesFrom = seriesFrom;
	}
	/**
	 * @return the seriesTo
	 */
	public double getSeriesTo() {
		return seriesTo;
	}
	/**
	 * @param seriesTo the seriesTo to set
	 */
	public void setSeriesTo(double seriesTo) {
		this.seriesTo = seriesTo;
	}
	/**
	 * @return the area
	 */
	public int getArea() {
		return area;
	}
	/**
	 * @param area the area to set
	 */
	public void setArea(int area) {
		this.area = area;
	}
	/**
	 * @return the areaOfficerCode
	 */
	public int getAreaOfficerCode() {
		return areaOfficerCode;
	}
	/**
	 * @param areaOfficerCode the areaOfficerCode to set
	 */
	public void setAreaOfficerCode(int areaOfficerCode) {
		this.areaOfficerCode = areaOfficerCode;
	}
	/**
	 * @return the mode
	 */
	public short getMode() {
		return mode;
	}
	/**
	 * @param mode the mode to set
	 */
	public void setMode(short mode) {
		this.mode = mode;
	}
	/**
	 * @return the parameter
	 */
	public short getParameter() {
		return parameter;
	}
	/**
	 * @param parameter the parameter to set
	 */
	public void setParameter(short parameter) {
		this.parameter = parameter;
	}
	/**
	 * @return the remittanceType
	 */
	public String getRemittanceType() {
		return remittanceType;
	}
	/**
	 * @param remittanceType the remittanceType to set
	 */
	public void setRemittanceType(String remittanceType) {
		this.remittanceType = remittanceType;
	}

	
	
}
