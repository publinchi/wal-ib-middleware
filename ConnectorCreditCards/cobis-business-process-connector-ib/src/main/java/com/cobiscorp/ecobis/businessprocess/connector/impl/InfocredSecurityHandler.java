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

//CODIGO TOMADO DE LAS SIGUIENTE DIRECCIONE
//http://stackoverflow.com/questions/3096183/jax-ws-password-type-passwordtext
//REF 01 - SOLUCION A UN ERROR QUE PRESENTA EL CODIGO ORIGINAL
//http://www.coderanch.com/t/580531/Web-Services/java/Error-MustUnderstand-headers-http-docs
//http://stackoverflow.com/questions/9364428/soapfaultexception-mustunderstand-headers-oasis-200401-wss-wssecurity-secext-1
//REF 02 - SOLUCION A UN ERROR QUE PRESENTA CUANDO SE PASO AL CONECTOR
//http://servercoredump.com/question/25320390/soap-handler-implementation-breaks-webservice

public class InfocredSecurityHandler implements SOAPHandler<SOAPMessageContext> {

	private static final ILogger logger = LogFactory.getLogger(InfocredSecurityHandler.class);

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
				SOAPElement security = header.addChildElement("Security", "wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
				SOAPElement userToken = security.addChildElement("UsernameToken", "wsse");
				userToken.addChildElement("Username", "wsse").addTextNode(MyConfigurationImpl.getInfocredUser());
				userToken.addChildElement("Password", "wsse").addTextNode(MyConfigurationImpl.getInfocredPassword());
			} catch (Exception e) {
				if (logger.isDebugEnabled()) {
					logger.logDebug("-->> INFOCRED-CONNECTOR => SOAPHandler<SOAPMessageContext> - handleMessage");
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
		QName securityHeader = new QName("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "Security", "wsse");
		HashSet headers = new HashSet();
		headers.add(securityHeader);
		return headers;
	}

}
