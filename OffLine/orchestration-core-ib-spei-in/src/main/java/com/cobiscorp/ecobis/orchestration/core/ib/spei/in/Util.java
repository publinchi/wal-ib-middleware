package com.cobiscorp.ecobis.orchestration.core.ib.spei.in;

import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.cobis.csp.domains.ICSP;
import com.cobiscorp.cobis.cts.domains.ICOBISTS;
import com.cobiscorp.cobis.cts.domains.ICTSTypes;
import com.cobiscorp.cobis.cts.domains.IProcedureResponse;
import com.cobiscorp.cobis.cts.dtos.ErrorBlock;
import com.cobiscorp.cobis.cts.dtos.ProcedureResponseAS;
import java.nio.charset.Charset;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.UUID;

public class Util {
    private Util(){

    }
    private static final Charset UTF8 = Charset.forName("UTF-8");
    public static final UUID NAMESPACE_URL = UUID.fromString("143ef603-a5d4-4b27-8031-cf51b265f010");
    
    private static final ILogger logger = LogFactory.getLogger(Util.class);

    public static IProcedureResponse returnCorrectResponse(IProcedureResponse responseData) {
        String wInfo = "[Util [returnException ";
        if (logger.isDebugEnabled())
            logger.logDebug( wInfo+"Success service: returnCorrectResponse: "+ responseData.getProcedureResponseAsString());

        IProcedureResponse wProcedureRespFinal = new ProcedureResponseAS();

        wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "0");

        wProcedureRespFinal.addParam("@o_resultado", ICTSTypes.SQLINT4, 50, "0");
        wProcedureRespFinal.addParam("@o_folio", ICTSTypes.SQLVARCHAR, 50, responseData.readValueParam("@o_id_interno"));
        String wDescription = responseData.readValueParam("@o_descripcion");
        String wRefundDescription = responseData.readValueParam("@o_descripcion_error");
        wProcedureRespFinal.addParam("@o_descripcion", ICTSTypes.SQLVARCHAR, 50, wDescription != null ? wDescription : wRefundDescription);
        wProcedureRespFinal.addParam("@o_id_causa_devolucion", ICTSTypes.SQLVARCHAR, 50, responseData.readValueParam("@o_id_causa_devolucion"));

        wProcedureRespFinal.setReturnCode(0);

        return wProcedureRespFinal;

    }

    public static IProcedureResponse returnException(int returnCode, String messageError) {
        String wInfo = "[Util][returnException] ";
        if (logger.isDebugEnabled())
            logger.logDebug( wInfo+"ERROR EXECUTING SERVICE MessageError: "+ messageError);

        IProcedureResponse wProcedureRespFinal = new ProcedureResponseAS();

        ErrorBlock eb = new ErrorBlock(returnCode, messageError);
        wProcedureRespFinal.addResponseBlock(eb);
        wProcedureRespFinal.addFieldInHeader(ICSP.SERVICE_EXECUTION_RESULT, ICOBISTS.HEADER_STRING_TYPE, "1");
        wProcedureRespFinal.setReturnCode(returnCode);
        wProcedureRespFinal.addMessage(returnCode, messageError);

        logger.logError(new IllegalArgumentException(messageError).getMessage());
        return wProcedureRespFinal;
    }
    
    public static String sessionID() {
    	String sesion = "";
    	String name = NAMESPACE_URL.toString();
    	UUID namespace = UUID.randomUUID();
    	
    	UUID uuid = nameUUIDFromNamespaceAndString(namespace, name);    	
    	sesion = uuid.toString();
    	
    	return sesion;
    }
    
    public static UUID nameUUIDFromNamespaceAndString(UUID namespace, String name) {
        return nameUUIDFromNamespaceAndBytes(namespace, Objects.requireNonNull(name, "name == null").getBytes(UTF8));
    }

    public static UUID nameUUIDFromNamespaceAndBytes(UUID namespace, byte[] name) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException nsae) {
            throw new InternalError("SHA-256 not supported");
        }
        md.update(toBytes(Objects.requireNonNull(namespace, "namespace is null")));
        md.update(Objects.requireNonNull(name, "name is null"));
        byte[] shaBytes = md.digest();
        shaBytes[6] &= 0x0f;  /* clear version        */
        shaBytes[6] |= 0x50;  /* set to version 5     */
        shaBytes[8] &= 0x3f;  /* clear variant        */
        shaBytes[8] |= 0x80;  /* set to IETF variant  */
        return fromBytes(shaBytes);
    }
    
    private static UUID fromBytes(byte[] data) {
        // Based on the private UUID(bytes[]) constructor
        long msb = 0;
        long lsb = 0;
        assert data.length >= 16;
        for (int i = 0; i < 8; i++)
            msb = (msb << 8) | (data[i] & 0xff);
        for (int i = 8; i < 16; i++)
            lsb = (lsb << 8) | (data[i] & 0xff);
        return new UUID(msb, lsb);
    }
    
    private static byte[] toBytes(UUID uuid) {
        // inverted logic of fromBytes()
        byte[] out = new byte[16];
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        for (int i = 0; i < 8; i++)
            out[i] = (byte) ((msb >> ((7 - i) * 8)) & 0xff);
        for (int i = 8; i < 16; i++)
            out[i] = (byte) ((lsb >> ((15 - i) * 8)) & 0xff);
        return out;
    }
}
