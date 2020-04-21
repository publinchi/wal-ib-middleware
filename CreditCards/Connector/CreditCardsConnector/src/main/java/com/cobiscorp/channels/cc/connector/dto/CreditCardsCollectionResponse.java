package com.cobiscorp.channels.cc.connector.dto;

import java.util.ArrayList;

public class CreditCardsCollectionResponse extends BaseResponse {


	private ArrayList<CreditCardResponse> creditcardsList;

	public ArrayList<CreditCardResponse> getCreditcardsList() {
		return creditcardsList;
	}

	public void setCreditCardsList(ArrayList<CreditCardResponse> creditcardsList) {
		this.creditcardsList = creditcardsList;
	}

	@Override
	public String toString() {
		return "CreditCardsCollectionResponse [creditcardsList=" + creditcardsList + "]";
	}

}
