/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

/**
 * @author wsanchez
 * @since 01/09/2015
 * @version 1.0.0
 */
public class ParametersACHRequest  extends BaseRequest{
	private Integer office;
	private Integer ente;
	
	
	/**
	 * @return the office
	 */
	public Integer getOffice() {
		return office;
	}
	/**
	 * @param office the office to set
	 */
	public void setOffice(Integer office) {
		this.office = office;
	}
	/**
	 * @return the ente
	 */
	public Integer getEnte() {
		return ente;
	}
	/**
	 * @param ente the ente to set
	 */
	public void setEnte(Integer ente) {
		this.ente = ente;
	}
}
