package com.cobiscorp.channels.cc.connector;

import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Map;

import com.cobiscorp.channels.cc.connector.dto.ConnectionParameters;
import com.cobiscorp.channels.cc.connector.dto.CreditCardRequest;
import com.cobiscorp.channels.cc.connector.dto.CreditCardResponse;

public class ConnectorPrueba {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ServiceClient serviceClient= new ServiceClient();

		try {

			CreditCardRequest creditCardRequest =  new CreditCardRequest();
			creditCardRequest.setId("123456789");
			String url = "http://ip:puerto/cliente-creditcard-express/ClienteCCExpress?WSDL";
			Integer connectionTimeout = 20000;
			Integer readTimeout = 60000;
			ConnectionParameters connectionParameters =  new ConnectionParameters(url, connectionTimeout, readTimeout);
			creditCardRequest.setConnectionParameters(connectionParameters);

			CreditCardResponse creditCollectionResponse = serviceClient.queryCreditCards(creditCardRequest);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e);
		}

	}

}
