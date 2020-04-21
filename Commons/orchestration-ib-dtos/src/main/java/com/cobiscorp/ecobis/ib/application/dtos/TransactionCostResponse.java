/**
 * 
 */
package com.cobiscorp.ecobis.ib.application.dtos;

/**
 * @author bborja
 * @since 27/2/2015
 * @version 1.0.0
 */
public class TransactionCostResponse extends BaseResponse {
	private double cost;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TransactionCostResponse [cost=" + cost + "]";
	}

	/**
	 * @return the cost
	 */
	public double getCost() {
		return cost;
	}

	/**
	 * @param cost the cost to set
	 */
	public void setCost(double cost) {
		this.cost = cost;
	}

}
