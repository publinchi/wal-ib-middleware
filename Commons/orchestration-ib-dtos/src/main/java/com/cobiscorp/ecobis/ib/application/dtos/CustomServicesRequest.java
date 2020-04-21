/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

/**
 * @author itorres
 * @since Mar 3, 2015
 * @version 1.0.0
 */
public class CustomServicesRequest extends BaseRequest{
	private Integer trn;
	private String operation;
	private Integer mode;
	private String nemonic;
	private String terminal;
	private Integer office;
	private Integer rol;
	private Integer code;
	private Integer ssesn;
	private Integer sssn;
	private String sdate;
	private String sorg;
	/**
	 * @return the trn
	 */
	public Integer getTrn() {
		return trn;
	}
	/**
	 * @param trn the trn to set
	 */
	public void setTrn(Integer trn) {
		this.trn = trn;
	}
	/**
	 * @return the operation
	 */
	public String getOperation() {
		return operation;
	}
	/**
	 * @param operation the operation to set
	 */
	public void setOperation(String operation) {
		this.operation = operation;
	}
	/**
	 * @return the mode
	 */
	public Integer getMode() {
		return mode;
	}
	/**
	 * @param mode the mode to set
	 */
	public void setMode(Integer mode) {
		this.mode = mode;
	}
	/**
	 * @return the nemonic
	 */
	public String getNemonic() {
		return nemonic;
	}
	/**
	 * @param nemonic the nemonic to set
	 */
	public void setNemonic(String nemonic) {
		this.nemonic = nemonic;
	}	
	
	/**
	 * @return the terminal
	 */
	public String getTerminal() {
		return terminal;
	}
	/**
	 * @param terminal the terminal to set
	 */
	public void setTerminal(String terminal) {
		this.terminal = terminal;
	}
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
	 * @return the rol
	 */
	public Integer getRol() {
		return rol;
	}
	/**
	 * @param rol the rol to set
	 */
	public void setRol(Integer rol) {
		this.rol = rol;
	}
	
	/**
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(Integer code) {
		this.code = code;
	}
	/**
	 * @return the ssesn
	 */
	public Integer getSsesn() {
		return ssesn;
	}
	/**
	 * @param ssesn the ssesn to set
	 */
	public void setSsesn(Integer ssesn) {
		this.ssesn = ssesn;
	}
	/**
	 * @return the sssn
	 */
	public Integer getSssn() {
		return sssn;
	}
	/**
	 * @param sssn the sssn to set
	 */
	public void setSssn(Integer sssn) {
		this.sssn = sssn;
	}
	/**
	 * @return the sdate
	 */
	public String getSdate() {
		return sdate;
	}
	/**
	 * @param sdate the sdate to set
	 */
	public void setSdate(String sdate) {
		this.sdate = sdate;
	}
	/**
	 * @return the sorg
	 */
	public String getSorg() {
		return sorg;
	}
	/**
	 * @param sorg the sorg to set
	 */
	public void setSorg(String sorg) {
		this.sorg = sorg;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CustomServicesRequest [trn=" + trn + ", operation=" + operation
				+ ", mode=" + mode + ", nemonic=" + nemonic + ", terminal="
				+ terminal + ", office=" + office + ", rol=" + rol + ", code="
				+ code + ", ssesn=" + ssesn + ", sssn=" + sssn + ", sdate="
				+ sdate + ", sorg=" + sorg + "]";
	}
	
}
