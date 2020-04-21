package com.cobiscorp.channels.cc.connector.dto;

import java.util.ArrayList;

public class CreditCardResponse extends BaseResponse{
	
	private ArrayList<CreditCard> creditCardsList;
	
	public CreditCardResponse(ArrayList<CreditCard> creditCardsList) {
		super();
		this.creditCardsList = creditCardsList;
	}

	public CreditCardResponse() {
		super();
	}

	public ArrayList<CreditCard> getCreditCardsList() {
		return creditCardsList;
	}

	public void setCreditCardsList(ArrayList<CreditCard> creditCardsList) {
		this.creditCardsList = creditCardsList;
	}

	@Override
	public String toString() {
		return "CreditCardResponse [creditCardsList=" + creditCardsList + "]";
	}

	
}
