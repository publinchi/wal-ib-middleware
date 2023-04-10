
package cobiscorp.ecobis.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cobiscorp.cobis.commons.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;

import java.util.Map;
import java.util.HashMap;

public class HeaderParamsSoapHandler implements
    SOAPHandler<SOAPMessageContext> {

    private static ILogger logger = LogFactory
    .getLogger(HeaderParamsSoapHandler.class);

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
      Boolean isRequest = (Boolean) context
      .get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
      if (!isRequest) {

        String userId = null;
        String applicationId = null;
        String backendId = null;
        String password = null;

        // Getting SOAP headers
        SOAPMessage soapMsg = context.getMessage();
        try {
          SOAPEnvelope soapEnv;
          SOAPPart soapPart = soapMsg.getSOAPPart();

          soapEnv = soapPart.getEnvelope();
          SOAPHeader soapHeader = soapEnv.getHeader();
          Map<String,String> dataMap = new HashMap<String, String>();
          
          if(soapHeader != null){
            NodeList elementsByTagName = soapHeader.getElementsByTagName("cobis:Cobis-Header");

            if(elementsByTagName != null){
                for (int i = 0; i < elementsByTagName.getLength(); i++) {
                    Node item = elementsByTagName.item(i);
				
                    if("Cobis-Header".equals(item.getLocalName())){
                        NodeList childNodes = elementsByTagName.item(i).getChildNodes();
                        for (int j = 0; j < childNodes.getLength(); j++) {
                            Node item2 = childNodes.item(j);
                            if("UserId".equals(item2.getLocalName())){
                                userId=item2.getTextContent();
                            }
                            else if("ApplicationId".equals(item2.getLocalName())){
                                applicationId=item2.getTextContent();
                            }
                            else if("BackendId".equals(item2.getLocalName())){
                                backendId=item2.getTextContent();
                            }
                            else if("Password".equals(item2.getLocalName())){
                                password=item2.getTextContent();
                            }
                            else if(item2.getLocalName()!=null && item2.getTextContent()!=null){
                                dataMap.put(item2.getLocalName(),item2.getTextContent());
                            }
                        }
                    }
                }
            }
              // add a custom property
            context.put("userId", userId);
            context.setScope("userId", MessageContext.Scope.APPLICATION);
            context.put("applicationId", applicationId);
            context.setScope("applicationId", MessageContext.Scope.APPLICATION);
            context.put("backendId", backendId);
            context.setScope("backendId", MessageContext.Scope.APPLICATION);
            context.put("password", password);
            context.setScope("password", MessageContext.Scope.APPLICATION);
            context.put("dataMap", dataMap);
            context.setScope("dataMap", MessageContext.Scope.APPLICATION);
            
            if(logger.isDebugEnabled()){
              logger.logDebug("userId: " + userId + ", applicationId: " + applicationId + ", backendId: " + backendId);
            }
          }
       } catch (SOAPException e) {
            logger.logError("Error processing the message.", e);
            //Stop the process of the message
            return false;
       }

      }
      return true;
    }

    public Set getHeaders() {
        return Collections.EMPTY_SET;
    }

    public boolean handleFault(SOAPMessageContext context) {
        return true;
    }

    public void close(MessageContext context) {
    }

}
