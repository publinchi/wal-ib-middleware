package com.cobiscorp.ecobis.ib.application.dtos;

import java.util.List;

public class PayRollResponse extends BaseResponse{

	private List<PaymentAccountResponse> paymentAccountList;

	/**
	 * @return the paymentaccountList
	 */
	public List<PaymentAccountResponse> getPaymentAccountList() {
		return paymentAccountList;
	}

	/**
	 * @param paymentaccountList the paymentaccountList to set
	 */
	public void setPaymentAccountList(List<PaymentAccountResponse> paymentaccountList) {
		this.paymentAccountList = paymentaccountList;
	}
	
	
	
}
