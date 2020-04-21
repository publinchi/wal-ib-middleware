/**
 *
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author jchonillo
 * @since Jan 08, 2015
 * @version 1.0.0
 */
public class CurrencyDefinition {
	/**
	 * 
	 */
	private Integer code;
	private String description;
	private String symbol;
	
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSimbol(String symbol) {
		this.symbol = symbol;
	}
	@Override
	public String toString() {
		return "CurrencyDefinition [code=" + code + ", description="
				+ description + ", symbol=" + symbol + "]";
	}

	
	
}
