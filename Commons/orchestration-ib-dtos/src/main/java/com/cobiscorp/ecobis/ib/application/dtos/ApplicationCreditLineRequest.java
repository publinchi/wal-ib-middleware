/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

import com.cobiscorp.ecobis.ib.orchestration.dtos.Product;
import java.math.BigDecimal;

/**
 * @author wsanchez
 * @since 15/07/2015
 * @version 1.0.0
 */
public class ApplicationCreditLineRequest extends BaseRequest {

	private String login;
	private String beneficiaryId;	//	@i_campo_1;
	private int    campo3;
	private Product productCreditLine; // @i_campo_2 
	private BigDecimal montoSolicitado;
	private BigDecimal montoDisponible;
									//@i_campo_4=null,
	private int    codProcess;		// siguiente;
	
	
	/**
	 * @return the login
	 */
	public String getLogin() {
		return login;
	}
	/**
	 * @param login the login to set
	 */
	public void setLogin(String login) {
		this.login = login;
	}
	/**
	 * @return the beneficiaryId
	 */
	public String getBeneficiaryId() {
		return beneficiaryId;
	}
	/**
	 * @param beneficiaryId the beneficiaryId to set
	 */
	public void setBeneficiaryId(String beneficiaryId) {
		this.beneficiaryId = beneficiaryId;
	}
	/**
	 * @return the campo3
	 */
	public int getCampo3() {
		return campo3;
	}
	/**
	 * @param campo3 the campo3 to set
	 */
	public void setCampo3(int campo3) {
		this.campo3 = campo3;
	}
	/**
	 * @return the creditLineNumber
	 */
	public Product getProductCreditLine() {
		return productCreditLine;
	}
	/**
	 * @param creditLineNumber the creditLineNumber to set
	 */
	public void setProductCreditLine(Product productCreditLine) {
		this.productCreditLine = productCreditLine;
	}
	/**
	 * @return the montoSolicitado
	 */
	public BigDecimal getMontoSolicitado() {
		return montoSolicitado;
	}
	/**
	 * @param montoSolicitado the montoSolicitado to set
	 */
	public void setMontoSolicitado(BigDecimal montoSolicitado) {
		this.montoSolicitado = montoSolicitado;
	}
	/**
	 * @return the montoDisponible
	 */
	public BigDecimal getMontoDisponible() {
		return montoDisponible;
	}
	/**
	 * @param montoDisponible the montoDisponible to set
	 */
	public void setMontoDisponible(BigDecimal montoDisponible) {
		this.montoDisponible = montoDisponible;
	}
	/**
	 * @return the codProcess
	 */
	public int getCodProcess() {
		return codProcess;
	}
	/**
	 * @param codProcess the codProcess to set
	 */
	public void setCodProcess(int codProcess) {
		this.codProcess = codProcess;
	}
}
