package com.cobiscorp.ecobis.businessprocess.connector.impl;

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;

public class CreditCardSecurityHandler implements SOAPHandler<SOAPMessageContext> {

	private static final ILogger logger = LogFactory.getLogger(InfocredSecurityHandler.class);
	private String username, password;

	public CreditCardSecurityHandler() {
	}

	public CreditCardSecurityHandler(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext msgCtx) {
		// Indicator telling us which direction this message is going in
		Boolean outInd = (Boolean) msgCtx.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

		// Handler must only add security headers to outbound messages
		if (outInd.booleanValue()) {
			try {
				// Get the SOAP Envelope
				SOAPEnvelope envelope = msgCtx.getMessage().getSOAPPart().getEnvelope();

				// Header may or may not exist yet
				SOAPHeader header = envelope.getHeader();
				if (header == null)
					header = envelope.addHeader();

				// Add WSS Usertoken Element Tree
				SOAPElement security = header.addChildElement("Security", "wsse",
						"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
				SOAPElement userToken = security.addChildElement("UsernameToken", "wsse");
				if (logger.isDebugEnabled()) {
					logger.logDebug("-->> CREDITCARD-CONNECTOR => USER:" + username);
					logger.logDebug("-->> CREDITCARD-CONNECTOR => PASSWORD:" + password);
				}
				userToken.addChildElement("Username", "wsse").addTextNode(username);
				userToken.addChildElement("Password", "wsse").addTextNode(password);
			} catch (Exception e) {
				if (logger.isDebugEnabled()) {
					logger.logDebug("-->> CREDITCARD-CONNECTOR => SOAPHandler<SOAPMessageContext> - handleMessage");
					logger.logDebug(e.getMessage());
				}
				// return false; -->> REF 02
			}
		}
		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		return false;
	}

	@Override
	public void close(MessageContext context) {

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Set<QName> getHeaders() {
		// -->> REF 01
		QName securityHeader = new QName(
				"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security",
				"wsse");
		HashSet headers = new HashSet();
		headers.add(securityHeader);
		return headers;
	}

}
