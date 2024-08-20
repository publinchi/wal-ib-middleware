package com.cobiscorp.ecobis.orchestration.core.ib.dispacher.spei;

import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.ecobis.ib.orchestration.dtos.mensaje;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class ManejoBytes {

    private static ILogger logger = LogFactory.getLogger(ManejoBytes.class);
 
   	public static byte[] armaTramaBytes(mensaje msj, Map<String, Object> aBagSPJavaOrchestration) throws Exception {
        final String METHOD_NAME = "[ArmaTramaBytes]";
        byte wFirma[];
        String tipoFirma= aBagSPJavaOrchestration.get("tipoFirma")!=null?aBagSPJavaOrchestration.get("tipoFirma").toString():""; 
        //FechaOperacion + CveInstOrd + CveInstBen +
        //CveRastreo + Monto + CuentaOrd + CuentaBen
        if(logger.isDebugEnabled())
        {
        	logger.logDebug("OpFechaOper:"+getDate(msj.getOrdenpago().getOpFechaOper()));
        	logger.logDebug("opinsclave:"+msj.getOrdenpago().getOpInsClave());
        	logger.logDebug("opinsclaveben:"+Integer.parseInt(aBagSPJavaOrchestration.get("paramInsBen").toString()));
        	logger.logDebug("OpCveRastreo:"+msj.getOrdenpago().getOpCveRastreo());
        	logger.logDebug("OpMonto:"+msj.getOrdenpago().getOpMonto());
        	logger.logDebug("OpCuentaOrd:"+msj.getOrdenpago().getOpCuentaOrd());
        	logger.logDebug("OpCuentaBen:"+msj.getOrdenpago().getOpCuentaBen());
        	logger.logDebug("OpCuentaBen2:"+msj.getOrdenpago().getOpCuentaBen2());
        	logger.logDebug("OpCuentaParticipanteOrd:"+msj.getOrdenpago().getOpCuentaParticipanteOrd());
        	logger.logDebug("OpCuentaEmisorRemesa:"+msj.getOrdenpago().getOpCuentaEmisorRemesa());
        }
        wFirma = ByteBuffer.allocate(0).array();
        
        //todos los tipos de pagos tienen estos datos incluida la firma 1
        wFirma = concatByte(wFirma, formateoFrima(getDate(msj.getOrdenpago().getOpFechaOper()))); //OpFechaOper
        wFirma = concatByte(wFirma, formateoFrima(msj.getOrdenpago().getOpInsClave()));//opinsclave
        wFirma = concatByte(wFirma, formateoFrima(Integer.parseInt(aBagSPJavaOrchestration.get("paramInsBen").toString())));//opinsclaveben
        wFirma = concatByte(wFirma, formateoFrima(msj.getOrdenpago().getOpCveRastreo()));//clave rastreo
        wFirma = concatByte(wFirma, formateoFrima(msj.getOrdenpago().getOpMonto().doubleValue()));//opmonto
        
        //firma bv_tipo_firma catalogo
        if("2".equals(tipoFirma ))
        {
        	wFirma = concatByte(wFirma, formateoFrima(msj.getOrdenpago().getOpCuentaOrd())); //OpCuentaOrd
        }else
    	if("3".equals(tipoFirma))
    	{
    		wFirma = concatByte(wFirma, formateoFrima(msj.getOrdenpago().getOpCuentaBen())); //OpCuentaBen
    	}else
		if("4".equals(tipoFirma))
		{
			wFirma = concatByte(wFirma, formateoFrima(msj.getOrdenpago().getOpCuentaOrd())); //OpCuentaOrd
			wFirma = concatByte(wFirma, formateoFrima(msj.getOrdenpago().getOpCuentaBen())); //OpCuentaBen
		}else
		if("5".equals(tipoFirma))
		{
			wFirma = concatByte(wFirma, formateoFrima(msj.getOrdenpago().getOpCuentaBen())); //OpCuentaBen
			wFirma = concatByte(wFirma, formateoFrima(msj.getOrdenpago().getOpCuentaBen2())); //OpCuentaBen2
		}else
        if("6".equals(tipoFirma))
		{
			wFirma = concatByte(wFirma, formateoFrima(msj.getOrdenpago().getOpCuentaOrd())); //CuentaOrd
			wFirma = concatByte(wFirma, formateoFrima(msj.getOrdenpago().getOpCuentaBen())); //OpCuentaBen
			wFirma = concatByte(wFirma, formateoFrima(msj.getOrdenpago().getOpCuentaBen2())); //OpCuentaBen2
		}else
		if("7".equals(tipoFirma))
		{
			wFirma = concatByte(wFirma, formateoFrima(msj.getOrdenpago().getOpCuentaOrd())); //CuentaOrd
			//CuentaPartOrd falta este campo
		}else
		if("8".equals(tipoFirma))
		{
			wFirma = concatByte(wFirma, formateoFrima(msj.getOrdenpago().getOpCuentaOrd())); //CuentaOrd
			wFirma = concatByte(wFirma, formateoFrima(msj.getOrdenpago().getOpCuentaBen())); //OpCuentaBen
			wFirma = concatByte(wFirma, formateoFrima(msj.getOrdenpago().getOpCuentaParticipanteOrd()));//CuentaPartOrd
			
		}
		else
		if("9".equals(tipoFirma))
		{
			wFirma = concatByte(wFirma, formateoFrima(msj.getOrdenpago().getOpCuentaOrd())); //CuentaOrd
			wFirma = concatByte(wFirma, formateoFrima(msj.getOrdenpago().getOpCuentaBen())); //OpCuentaBen
			wFirma = concatByte(wFirma, formateoFrima(msj.getOrdenpago().getOpCuentaParticipanteOrd()));//OpCuentaParticipanteOrd
			wFirma = concatByte(wFirma, formateoFrima(msj.getOrdenpago().getOpCuentaEmisorRemesa()));//OpCuentaEmisorRemesa
		}
        logDebug(METHOD_NAME,toString(wFirma));        
        return wFirma;
        
    }
   	private static Date getDate(String aDate) throws ParseException {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        String wAño = aDate.substring(0, 4);
        String wMes = aDate.substring(4, 6);
        String wDia = aDate.substring(6, 8);
        Date dFechaOperacion = formato.parse(wDia + "/" + wMes + "/" + wAño);
        return dFechaOperacion;
    }
    private static byte[] concatByte(byte[] aPrimero, byte[] aSegundo) {
        // System.out.println(toString(aPrimero));
        // System.out.println(toString(aSegundo));
        ByteBuffer byteBuffer = ByteBuffer.allocate(aPrimero.length + aSegundo.length);
        byteBuffer.put(aPrimero);
        byteBuffer.put(aSegundo);
        return byteBuffer.array();
    }
    private static byte[] formateoFrima(Object aObjeto) {
        if (aObjeto instanceof String)
            return formateoFrima(aObjeto, "S");
        if (aObjeto instanceof Integer)
            return formateoFrima(aObjeto, "I");
        if (aObjeto instanceof Long)
            return formateoFrima(aObjeto, "L");
        if (aObjeto instanceof Double)
            return formateoFrima(aObjeto, "M");
        if (aObjeto instanceof Date)
            return formateoFrima(aObjeto, "D");

        return ByteBuffer.allocate(0).array();
    }
    private static byte[] formateoFrima(Object aObjeto, String aType) {
        if ("D".equals(aType)) {
            return formatearDate(aObjeto);
        } else if ("L".equals(aType)) {
            return formatearLong(aObjeto);
        } else if ("I".equals(aType)) {
            return formatearInt(aObjeto);
        } else if ("S".equals(aType)) {
            return formatearString(aObjeto);
        } else if ("M".equals(aType)) {
            return formatearMoney(aObjeto);
        }
        return ByteBuffer.allocate(0).array();
    }

    private static byte[] formatearDate(Object aObjeto) {
        Date dFecha = (Date) aObjeto;
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(dFecha);
      
        //Obtiene el Año
        Integer iAño = new Integer(calendario.get(Calendar.YEAR));
        //Obtiene el mes del 0 - 11
        Integer iMes = new Integer(calendario.get(Calendar.MONTH)) + 1;
        // Obtiene el dia del 1 al 31
        Integer iDia = new Integer(calendario.get(Calendar.DAY_OF_MONTH));
        short sAño = iAño.shortValue();
        byte bMes = iMes.byteValue();
        byte bDia = iDia.byteValue();
        byte[] bytes = ByteBuffer.allocate(4).put(bDia).put(bMes).putShort(sAño).array();
      //  logInfo("[formatearDate]", "Formatter - Transformar tipoDato[date] - valorTransformar[" + iAño + iMes + iDia + "] - bytes[" + toString(bytes) + "]");
        return bytes;
    }

    private static byte[] formatearLong(Object aObjeto) {
        int lInstOrd = ((Long) aObjeto).intValue();
        byte[] bytes = ByteBuffer.allocate(8).putInt(lInstOrd).array();
        //logInfo("[formatearLong]", "Formatter - Transformar tipoDato[long] - valorTransformar[" + (Long) aObjeto + "] - bytes[" + toString(bytes) + "]");
        return bytes;
    }

    private static byte[] formatearInt(Object aObjeto) {
        int lInstOrd = ((Integer) aObjeto).intValue();
        byte[] bytes = ByteBuffer.allocate(4).putInt(lInstOrd).array();
        //logInfo("[formatearInt]", "Formatter - Transformar tipoDato[int] - valorTransformar[" + (Integer) aObjeto + "] - bytes[" + toString(bytes) + "]");
        return bytes;
    }

    private static byte[] formatearString(Object aObjeto) {
        byte[] bytes = ((String) aObjeto).concat("\0").getBytes();
        //logInfo("[formatearString]", "Formatter - Transformar tipoDato[string] - valorTransformar[" + (String) aObjeto + "] - bytes[" + toString(bytes) + "]");
        return bytes;
    }

    private static byte[] formatearMoney(Object aObjeto) {
        
        int esNeg = 0;
        int digitos = 8; //(SIN CONTAR DECIMALES)

        BigDecimal money = new BigDecimal((Double) aObjeto);
        double montoDouble = money.doubleValue();
        if (montoDouble < 0) {
            esNeg = 1;
            montoDouble *= -1;
        }
        //logDebug(METHOD_NAME, "Monto Original: " + montoDouble);
        String auxiliarstring = String.format("%.2f", montoDouble);
        //SE QUITA LAS PUNTUACIONES SOLO NUMEROS
        auxiliarstring = auxiliarstring.replace(",", "");
        auxiliarstring = auxiliarstring.replace(".", "");
        // logDebug(METHOD_NAME, "Monto Cadena: " + auxiliarstring);
        int longitud = auxiliarstring.length();
        //    logDebug(METHOD_NAME, "longitud Monto Cadena: " + longitud);
        int L1 = 0;
        int L2 = 0;
        if (longitud > digitos) {
            L1 = Integer.parseInt(auxiliarstring.substring(0, longitud - digitos));
            L2 = Integer.parseInt(auxiliarstring.substring(longitud - digitos, longitud));
            //logDebug(METHOD_NAME, "Alta: " + L1);
            //logDebug(METHOD_NAME, "Baja: " + L2);
        } else {
            L1 = 0;
            L2 = Integer.parseInt(auxiliarstring);
        }
        if (esNeg == 1)
            if (L1 > 0)
                L1 *= -1;
            else
                L2 *= -1;

        byte[] bytes = ByteBuffer.allocate(8).putInt(L1).putInt(L2).array();
        
        return bytes;
    }
    public static String toString(byte[] a) {
        if (a == null)
            return "null";
        //logDebug("[toString]" , a.length);
        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    private static void logDebug(String aMethod, String aMensaje) {
        if (logger.isDebugEnabled()) {
            logger.logDebug(aMethod + ": " + aMensaje);
        }
    }

}
