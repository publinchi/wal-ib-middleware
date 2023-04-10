
package cobiscorp.ecobis.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.ws.LogicalMessage;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.LogicalMessageContext;
import javax.xml.ws.handler.MessageContext;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;

public class SchemaValidationLogicalHandler implements
		LogicalHandler<LogicalMessageContext> {
	private static Validator validator = null;
	private static ILogger logger = LogFactory
			.getLogger(SchemaValidationLogicalHandler.class);

	public SchemaValidationLogicalHandler() {
		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();

		StreamSource[] sources = new StreamSource[4];
    InputStream schemaDto1 = parentClassLoader.getResourceAsStream("WEB-INF/wsdl/commonDTO1.xsd");
    sources[0] = new StreamSource(schemaDto1);
    InputStream schemaDto2 = parentClassLoader.getResourceAsStream("WEB-INF/wsdl/commonDTO2.xsd");
    sources[1] = new StreamSource(schemaDto2);
    
		InputStream schemaDto3 = parentClassLoader.getResourceAsStream("WEB-INF/wsdl/cobiscorp.ecobis.bankingservicesoperations.dto.xsd");
		sources[2] = new StreamSource(schemaDto3);

		InputStream schemaDto4 = parentClassLoader.getResourceAsStream("WEB-INF/wsdl/services.xsd");
		sources[3] = new StreamSource(schemaDto4);
		try {
			Schema schema = sf.newSchema(sources);
			validator = schema.newValidator();
		} catch (SAXException ex) {
			logger.logError(ex.getMessage(), ex);
		}
	}

	@Override
	public boolean handleMessage(LogicalMessageContext context) {
		if (validator == null)
			return true;
		if (((Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY))
				.booleanValue()) {
			// only validate incoming messages
			return true;
		}
		LogicalMessage message = context.getMessage();
		Source payload = message.getPayload();
		try {
			validator.validate(payload);
		} catch (SAXParseException ex) {
			if (logger.isDebugEnabled()) {
				try {
					String sourceAsString = getSourceAsString(payload);
					logger.logDebug(ex.getMessage() + ' ' + sourceAsString);
				} catch (Exception ex1) {
					logger.logError(ex1.getMessage(), ex1);
				}
			}
			throw new WebServiceException(ex.getMessage());
		} catch (SAXException ex) {
			throw new WebServiceException(ex.getMessage());
		} catch (IOException ex) {
			throw new WebServiceException(ex.getMessage());
		}
		return true;
	}

	@Override
	public boolean handleFault(LogicalMessageContext context) {
		return true;
	}

	@Override
	public void close(MessageContext context) {

	}

	private String getSourceAsString(Source s) throws Exception {
		Transformer transformer = TransformerFactory.newInstance()
				.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		OutputStream out = new ByteArrayOutputStream();
		StreamResult streamResult = new StreamResult();
		streamResult.setOutputStream(out);
		transformer.transform(s, streamResult);
		return streamResult.getOutputStream().toString();
	}

}

  