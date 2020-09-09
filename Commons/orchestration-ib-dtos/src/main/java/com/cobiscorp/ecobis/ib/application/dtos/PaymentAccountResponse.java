package com.cobiscorp.ecobis.ib.application.dtos;

import java.math.BigDecimal;

/**
 * @author tbaidal
 *
 */
public class PaymentAccountResponse extends BaseResponse{
	private String account;
	private Integer currencyId;
	private Integer productId;
	private Integer blockId;
	private BigDecimal amount;
	/**
	 * @return the account
	 */
	public String getAccount() {
		return account;
	}
	/**
	 * @param account the account to set
	 */
	public void setAccount(String account) {
		this.account = account;
	}
	/**
	 * @return the currencyId
	 */
	public Integer getCurrencyId() {
		return currencyId;
	}
	/**
	 * @param currencyId the currencyId to set
	 */
	public void setCurrencyId(Integer currencyId) {
		this.currencyId = currencyId;
	}
	/**
	 * @return the productId
	 */
	public Integer getProductId() {
		return productId;
	}
	/**
	 * @param productId the productId to set
	 */
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	/**
	 * @return the blockId
	 */
	public Integer getBlockId() {
		return blockId;
	}
	/**
	 * @param blockId the blockId to set
	 */
	public void setBlockId(Integer blockId) {
		this.blockId = blockId;
	}
	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	@Override
	public String toString() {
		return "PaymentAccountResponse [account=" + account + ", currencyId=" + currencyId + ", productId=" + productId
				+ ", blockId=" + blockId + ", amount=" + amount + "]";
	}
	
	
	
}
