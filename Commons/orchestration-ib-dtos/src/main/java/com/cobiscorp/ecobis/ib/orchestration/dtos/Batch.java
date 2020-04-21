/**
 * 
 */
package com.cobiscorp.ecobis.ib.orchestration.dtos;

/**
 * @author gyagual
 * @since Jan 29, 2015
 * @version 1.0.0
 */
public class Batch {
	private Integer batch;
	private Integer sarta;
	private Integer secuencial;
	private Integer corrida;
	private Integer intento;
	/**
	 * @return the batch
	 */
	public Integer getBatch() {
		return batch;
	}
	/**
	 * @param batch the batch to set
	 */
	public void setBatch(Integer batch) {
		this.batch = batch;
	}
	/**
	 * @return the sarta
	 */
	public Integer getSarta() {
		return sarta;
	}
	/**
	 * @param sarta the sarta to set
	 */
	public void setSarta(Integer sarta) {
		this.sarta = sarta;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Batch [batch=" + batch + ", sarta=" + sarta + ", secuencial="
				+ secuencial + ", corrida=" + corrida + ", intento=" + intento
				+ "]";
	}
	/**
	 * @return the secuencial
	 */
	public Integer getSecuencial() {
		return secuencial;
	}
	/**
	 * @param secuencial the secuencial to set
	 */
	public void setSecuencial(Integer secuencial) {
		this.secuencial = secuencial;
	}
	/**
	 * @return the corrida
	 */
	public Integer getCorrida() {
		return corrida;
	}
	/**
	 * @param corrida the corrida to set
	 */
	public void setCorrida(Integer corrida) {
		this.corrida = corrida;
	}
	/**
	 * @return the intento
	 */
	public Integer getIntento() {
		return intento;
	}
	/**
	 * @param intento the intento to set
	 */
	public void setIntento(Integer intento) {
		this.intento = intento;
	}
}
