package com.generate.signature;

import java.io.ObjectStreamException;
import java.security.KeyRep;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.services.lambda.runtime.Context;

public class SignatureGenerator implements RequestStreamHandler {

  private static final ObjectMapper objectMapper = new ObjectMapper();


  public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
    
    SignatureGenerator generator = new SignatureGenerator();
    String consumerId = "e393ea37-f689-46a5-920b-9a0cd51424af";
    String priviateKeyVersion = "1";
    String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCdQeVxkbmkTsT72UQWqxLl7tMWX0gSGvzRRGa7DxgW6XtX6Vobz52v+EMuopbPZyHqvy6SgaR4QOZwie4vkp6bTTw4/bJ474wfVy8AE5cT+Txk2vtm5u38HMRtwsu+LKEeqfazUCmWvT1zW1RT2GuArrxWyFEv1JzWItXIz/oVDIAb4dRj/ByQ6pcHx4VFqnfA7yJx0vNyOlwPCmCSvZaAA2JEGd+m668lvqhEBoHOEcjeZ11rEjOMf1Sf947tw10FROjSCmZ+DQFwgjuLUuRfFLRTPhBth2ANodjCAzf5yN66/E7xM6nuxmDb0GZ6j+UatdfJ0hNcxHTg0ad8G6r7AgMBAAECggEAWMBG+NiQmA2omKafCdgU+1XVJiwyJZ27j3N2Mx4qr0uoRA47v7Rlv1lyuRAj4vvGpZCufRrTstaV92+w0IKEJwvo8BUmM6CmSU3QBdAIlMo17om0Fvg/2eh2Z6fnivXvHLo5tCuKvTsiQkeKCSP8UAzN2ICHaWIp/aR9WS8fad7YizYiJuO7hHl4ADEWj80H17SsX0rAxh33MvWZ7oL4v/UJLeGZvyZPxJWaOMrarp8O2KcxtTNJ8NBjGb5qCvv2N6hxJW6CAP/M6YPF0qENccy5MmWSoMglEyPHxc51Ddq/oat7BI5ANqgE/7TzjZgVbiyCjkFgVpwd1xgWfpnBaQKBgQDQtHOZXZpYX2bNX1aROPbEFJIeV+1kWepjQLSab5/WFJeGphRk2++A6FKUUxE6kdEcDMD/fwJVcK5m43PceJbw+7VTaAtcIsHR6CTYE53DZSnM9gwvXaGvzrHh7MeYjSgjHLosf4KQkHzDf6nrcCMI7XN9S+AlT1yl9HUoEOhw5QKBgQDA5NV3h/3uFHrVyx+dhQhPl6at7jqiwKQ1n9pG7ct4BA8xouqUNbgFDTuxK7vT6JUMzmWErAodcDS1GuBnNL4ddHhtGRoWCU4CnGW598YuuRra2zhrbmjrE0rV0GSuYRPWqDJV0/WS2Hfy1y17tVRp50xwlGJwc2GsQLZagmROXwKBgQC1rlDvkm9ZPPuGDteSJg+TFAE7TvnGIQwMiPhvdHqk51odChi40EkHY7b3jfRykxuBGo0sHYmPjT8VLC3hIEztzYDE8c7pzwwAyeDBBRqSTU99MDfdR9oH7Jwne3Mplr/5CPn84KdtZ4pkuY7W+NBW5jZiPZoLgLG7ejo+vbFLDQKBgBIsvYxQrPVkypogVG9EHJ9bO6JBwxaYriA7Wpa6SySYxVG49v9V/GvFbm1oIH9796dIPzp790wEyYKS8LfouU0PL1WgOtv+rnICiHc4SF0YpdYfN7avpYUYwUFaLFJd5T+hg3Sgzw+yA8NG0QtJ75U27PXilU/hHz/arRPkG01jAoGBAIIF2DUXm+vCor+qyAa9WyArxb5NCoAKCQ+LnedweTwocnoelQn4d/eBKKLUaFHj/CWPaDvA+gNdEzqRtJ29d3Gs5ESUlWb9bWb7vD80qr5iIFcbbNpx6sACh0l+U9lL6yPmUO6+Fh7ayl1lCGObb+4m6xG7X9T2pzbWgkJJzTbw";
    long intimestamp = System.currentTimeMillis();
    System.out.println("intimestamp: " + intimestamp);
    Map<String, String> map = new HashMap<String, String>();
    map.put("WM_CONSUMER.ID", consumerId);
    map.put("WM_CONSUMER.INTIMESTAMP", Long.toString(intimestamp));
    map.put("WM_SEC.KEY_VERSION", priviateKeyVersion);
    String[] array = canonicalize(map);
    String data = null;
    try {
      data = generator.generateSignature(privateKey, array[1]);
    } catch (Exception exception) {}
    System.out.println("Signature: " + data);

    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put("timestamp", intimestamp);
    responseMap.put("key", data);

    String jsonResponse = objectMapper.writeValueAsString(responseMap);

    outputStream.write(jsonResponse.getBytes(StandardCharsets.UTF_8));
       
  }
  public static void main(String[] args) {
    SignatureGenerator generator = new SignatureGenerator();
    String consumerId = "e393ea37-f689-46a5-920b-9a0cd51424af";
    String priviateKeyVersion = "1";
    String privateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCdQeVxkbmkTsT72UQWqxLl7tMWX0gSGvzRRGa7DxgW6XtX6Vobz52v+EMuopbPZyHqvy6SgaR4QOZwie4vkp6bTTw4/bJ474wfVy8AE5cT+Txk2vtm5u38HMRtwsu+LKEeqfazUCmWvT1zW1RT2GuArrxWyFEv1JzWItXIz/oVDIAb4dRj/ByQ6pcHx4VFqnfA7yJx0vNyOlwPCmCSvZaAA2JEGd+m668lvqhEBoHOEcjeZ11rEjOMf1Sf947tw10FROjSCmZ+DQFwgjuLUuRfFLRTPhBth2ANodjCAzf5yN66/E7xM6nuxmDb0GZ6j+UatdfJ0hNcxHTg0ad8G6r7AgMBAAECggEAWMBG+NiQmA2omKafCdgU+1XVJiwyJZ27j3N2Mx4qr0uoRA47v7Rlv1lyuRAj4vvGpZCufRrTstaV92+w0IKEJwvo8BUmM6CmSU3QBdAIlMo17om0Fvg/2eh2Z6fnivXvHLo5tCuKvTsiQkeKCSP8UAzN2ICHaWIp/aR9WS8fad7YizYiJuO7hHl4ADEWj80H17SsX0rAxh33MvWZ7oL4v/UJLeGZvyZPxJWaOMrarp8O2KcxtTNJ8NBjGb5qCvv2N6hxJW6CAP/M6YPF0qENccy5MmWSoMglEyPHxc51Ddq/oat7BI5ANqgE/7TzjZgVbiyCjkFgVpwd1xgWfpnBaQKBgQDQtHOZXZpYX2bNX1aROPbEFJIeV+1kWepjQLSab5/WFJeGphRk2++A6FKUUxE6kdEcDMD/fwJVcK5m43PceJbw+7VTaAtcIsHR6CTYE53DZSnM9gwvXaGvzrHh7MeYjSgjHLosf4KQkHzDf6nrcCMI7XN9S+AlT1yl9HUoEOhw5QKBgQDA5NV3h/3uFHrVyx+dhQhPl6at7jqiwKQ1n9pG7ct4BA8xouqUNbgFDTuxK7vT6JUMzmWErAodcDS1GuBnNL4ddHhtGRoWCU4CnGW598YuuRra2zhrbmjrE0rV0GSuYRPWqDJV0/WS2Hfy1y17tVRp50xwlGJwc2GsQLZagmROXwKBgQC1rlDvkm9ZPPuGDteSJg+TFAE7TvnGIQwMiPhvdHqk51odChi40EkHY7b3jfRykxuBGo0sHYmPjT8VLC3hIEztzYDE8c7pzwwAyeDBBRqSTU99MDfdR9oH7Jwne3Mplr/5CPn84KdtZ4pkuY7W+NBW5jZiPZoLgLG7ejo+vbFLDQKBgBIsvYxQrPVkypogVG9EHJ9bO6JBwxaYriA7Wpa6SySYxVG49v9V/GvFbm1oIH9796dIPzp790wEyYKS8LfouU0PL1WgOtv+rnICiHc4SF0YpdYfN7avpYUYwUFaLFJd5T+hg3Sgzw+yA8NG0QtJ75U27PXilU/hHz/arRPkG01jAoGBAIIF2DUXm+vCor+qyAa9WyArxb5NCoAKCQ+LnedweTwocnoelQn4d/eBKKLUaFHj/CWPaDvA+gNdEzqRtJ29d3Gs5ESUlWb9bWb7vD80qr5iIFcbbNpx6sACh0l+U9lL6yPmUO6+Fh7ayl1lCGObb+4m6xG7X9T2pzbWgkJJzTbw";
    long intimestamp = System.currentTimeMillis();
    System.out.println("intimestamp: " + intimestamp);
    Map<String, String> map = new HashMap<String, String>();
    map.put("WM_CONSUMER.ID", consumerId);
    map.put("WM_CONSUMER.INTIMESTAMP", Long.toString(intimestamp));
    map.put("WM_SEC.KEY_VERSION", priviateKeyVersion);
    String[] array = canonicalize(map);
    String data = null;
    try {
      data = generator.generateSignature(privateKey, array[1]);
    } catch (Exception exception) {}
    System.out.println("Signature: " + data);
  }
  
  public String generateSignature(String key, String stringToSign) throws Exception {
    Signature signatureInstance = Signature.getInstance("SHA256WithRSA");
    ServiceKeyRep keyRep = new ServiceKeyRep(KeyRep.Type.PRIVATE, "RSA", "PKCS#8", Base64.decodeBase64(key));
    PrivateKey resolvedPrivateKey = (PrivateKey)keyRep.readResolve();
    signatureInstance.initSign(resolvedPrivateKey);
    byte[] bytesToSign = stringToSign.getBytes("UTF-8");
    signatureInstance.update(bytesToSign);
    byte[] signatureBytes = signatureInstance.sign();
    String signatureString = Base64.encodeBase64String(signatureBytes);
    return signatureString;
  }
  
  protected static String[] canonicalize(Map<String, String> headersToSign) {
    StringBuffer canonicalizedStrBuffer = new StringBuffer();
    StringBuffer parameterNamesBuffer = new StringBuffer();
    Set<String> keySet = headersToSign.keySet();
    SortedSet<String> sortedKeySet = new TreeSet<String>(keySet);
    for (String key : sortedKeySet) {
      Object val = headersToSign.get(key);
      parameterNamesBuffer.append(key.trim()).append(";");
      canonicalizedStrBuffer.append(val.toString().trim()).append("\n");
    } 
    return new String[] { parameterNamesBuffer.toString(), canonicalizedStrBuffer.toString() };
  }
  
  class ServiceKeyRep extends KeyRep {
    private static final long serialVersionUID = -7213340660431987616L;
    
    public ServiceKeyRep(KeyRep.Type type, String algorithm, String format, byte[] encoded) {
      super(type, algorithm, format, encoded);
    }
    
    protected Object readResolve() throws ObjectStreamException {
      return super.readResolve();
    }
  }
}
