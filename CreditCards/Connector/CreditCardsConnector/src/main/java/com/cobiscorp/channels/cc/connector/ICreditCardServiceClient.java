package com.cobiscorp.channels.cc.connector;

import com.cobiscorp.channels.cc.connector.dto.CreditCardResponse;
import com.cobiscorp.channels.cc.connector.dto.CreditCardsCollectionResponse;
import com.cobiscorp.channels.cc.connector.dto.MovementsResponse;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import com.cobiscorp.channels.cc.connector.dto.ConnectionParameters;
import com.cobiscorp.channels.cc.connector.dto.CreditCardRequest;

public interface ICreditCardServiceClient {
	CreditCardResponse queryCreditCards(CreditCardRequest creditCardRequest) throws CreditCardConnectorServiceException;
	
	CreditCardResponse queryCreditCardBalance(CreditCardRequest creditCardRequest) throws CreditCardConnectorServiceException;

	MovementsResponse queryMovements(CreditCardRequest creditCardRequest) throws CreditCardConnectorServiceException;
	
	URL initializeService(final ConnectionParameters connectionParameters) throws MalformedURLException;

}