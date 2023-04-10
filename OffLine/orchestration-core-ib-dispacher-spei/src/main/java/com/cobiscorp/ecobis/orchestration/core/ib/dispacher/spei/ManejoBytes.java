package com.cobiscorp.ecobis.orchestration.core.ib.dispacher.spei;


import com.cobiscorp.cobis.commons.domains.log.ILogger;
import com.cobiscorp.cobis.commons.log.LogFactory;
import com.cobiscorp.ecobis.ib.orchestration.dtos.mensaje;
import com.cobiscorp.ecobis.ib.orchestration.dtos.ordenpago;


import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ManejoBytes {

    private static ILogger logger = LogFactory.getLogger(ManejoBytes.class);

    public static byte[] ArmaTramaBytes(ordenpago pago) throws Exception {
        final String METHOD_NAME = "[ArmaTramaBytes]";
        logInfo(METHOD_NAME, "[INI]");
        byte wFirma[]; 
        
        try {
            wFirma = ByteBuffer.allocate(0).array();
            wFirma = concatByte(wFirma, formateoFrima(getDate(pago.getOpFechaOper())));
            //90715- TRAFALGAR
            //715- GEM-TRAFALGAR
            wFirma = concatByte(wFirma, formateoFrima(Integer.parseInt("90715")));
            wFirma = concatByte(wFirma, formateoFrima(pago.getOpInsClave()));
            wFirma = concatByte(wFirma, formateoFrima(pago.getOpCveRastreo()));
            wFirma = concatByte(wFirma, formateoFrima(pago.getOpMonto().doubleValue()));
            //40 Cuenta clabe 18 posiciones
            //10 cuenta movil 10 posiciones
            //3 cuenta debito 16 posciciones
            wFirma = concatByte(wFirma, formateoFrima(pago.getOpCuentaOrd()));
            wFirma = concatByte(wFirma, formateoFrima(pago.getOpCuentaBen()));

            logInfo(METHOD_NAME, "Formatter - Bytes Concatenados [" + toString(wFirma) + "]");
            return wFirma;
        } finally {
            logInfo(METHOD_NAME, "[FIN]");
        }
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
        //Separacion de dias mes año
        /*
        String wAño = ((String) aObjeto).substring(0, 4);
        short sAño = new Short(wAño).shortValue();
        String wMes = ((String) aObjeto).substring(4, 6);
        byte bMes = new Byte(wMes).byteValue();
        String wDia = ((String) aObjeto).substring(6, 8);
        byte bDia = new Byte(wDia).byteValue();
         */
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
        logInfo("[formatearDate]", "Formatter - Transformar tipoDato[date] - valorTransformar[" + iAño + iMes + iDia + "] - bytes[" + toString(bytes) + "]");
        return bytes;
    }

    private static byte[] formatearLong(Object aObjeto) {
        int lInstOrd = ((Long) aObjeto).intValue();
        byte[] bytes = ByteBuffer.allocate(8).putInt(lInstOrd).array();
        logInfo("[formatearLong]", "Formatter - Transformar tipoDato[long] - valorTransformar[" + (Long) aObjeto + "] - bytes[" + toString(bytes) + "]");
        return bytes;
    }

    private static byte[] formatearInt(Object aObjeto) {
        int lInstOrd = ((Integer) aObjeto).intValue();
        byte[] bytes = ByteBuffer.allocate(4).putInt(lInstOrd).array();
        logInfo("[formatearInt]", "Formatter - Transformar tipoDato[int] - valorTransformar[" + (Integer) aObjeto + "] - bytes[" + toString(bytes) + "]");
        return bytes;
    }

    private static byte[] formatearString(Object aObjeto) {
        byte[] bytes = ((String) aObjeto).concat("\0").getBytes();
        logInfo("[formatearString]", "Formatter - Transformar tipoDato[string] - valorTransformar[" + (String) aObjeto + "] - bytes[" + toString(bytes) + "]");
        return bytes;
    }

    private static byte[] formatearMoney(Object aObjeto) {
        final String METHOD_NAME = "[formatearMoney]";
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
        logInfo(METHOD_NAME, "Formatter - Transformar tipoDato[money] - valorTransformar[" + (Double) aObjeto + "] - bytes[" + toString(bytes) + "]");
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

    private static Date getFechaOperacion(byte[] aBytes) throws ParseException {
        final String METHOD_NAME = "[getFechaOperacion]";
        Byte bDia = aBytes[0]; //Posicion 0 Dia
        Byte bMes = aBytes[1]; //Posicion 1 Mes
        byte[] bAnio = Arrays.copyOfRange(aBytes, 2, 4); //Posicion 2 y 3 forman el año
        // logDebug(METHOD_NAME, "" + bDia);
        // logDebug(METHOD_NAME, "" + bMes);
        // logDebug(METHOD_NAME, "" + toString(bAnio));
        Short sAnio = ByteBuffer.allocate(2).put(bAnio).getShort(0);
        // logDebug(METHOD_NAME, "Fecha " + bDia + "/" + bMes + "/" + sAnio);
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        Date dFechaOperacion = formato.parse(bDia + "/" + bMes + "/" + sAnio);
        logInfo(METHOD_NAME, "Formatter - Transformar tipoDato[Date] - valorTransformar[" + toString(Arrays.copyOfRange(aBytes, 0, 4)) + "] - FechaOperacion[" + dFechaOperacion + "]");
        return dFechaOperacion;
    }

    private static Integer getInstOrdenante(byte[] aBytes) {
        final String METHOD_NAME = "[getInstOrdenante]";
        byte[] bIntsOrd = Arrays.copyOfRange(aBytes, 4, 8); //Posicion 4 y 7 Institucion Ordenante
        Integer iInstOrd = ByteBuffer.wrap(bIntsOrd).getInt();
        logInfo(METHOD_NAME, "Formatter - Transformar tipoDato[Int] - valorTransformar[" + toString(bIntsOrd) + "] - CveInstOrd[" + iInstOrd + "]");
        return iInstOrd;
    }

    private static Integer getInstBeneficiario(byte[] aBytes) {
        final String METHOD_NAME = "[getInstOrdenante]";
        byte[] bIntsBen = Arrays.copyOfRange(aBytes, 8, 12); //Posicion 8 y 11 Institucion Beneficiario
        Integer iInstBen = ByteBuffer.wrap(bIntsBen).getInt();
        logInfo(METHOD_NAME, "Formatter - Transformar tipoDato[Int] - valorTransformar[" + toString(bIntsBen) + "] - CveInstBen[" + iInstBen + "]");
        return iInstBen;
    }

    private static String getClaveRastreo(byte[] aBytes) {
        final String METHOD_NAME = "[getClaveRastreo]";
        // Empieza desde la posicion 12 hasta el  byte para limite de cadenas|0
        int iFinClave = getFinString(aBytes, 12); // obteniendo el fin de cadena
        byte[] bClaveRastreo = Arrays.copyOfRange(aBytes, 12, iFinClave + 1); //Posicion 12 hasta el fin de cadena
        String sClaveRastreo = new String(bClaveRastreo);
        logInfo(METHOD_NAME, "Formatter - Transformar tipoDato[String] - valorTransformar[" + toString(bClaveRastreo) + "] - CveRastreo[" + sClaveRastreo + "]");
        return sClaveRastreo;
    }

    private static int getFinString(byte[] aBytes, int aIndexInicial) {
        for (int i = aIndexInicial; i < aBytes.length; i++) {
            if (aBytes[i] == 0)
                return i;
        }
        return -1;
    }

    private static String getMonto(byte[] aBytes) {
        final String METHOD_NAME = "[getMonto]";
        // Empieza desde la posicion 12 hasta el  byte para limite de cadenas|0
        int iFinClave = getFinString(aBytes, 12);
        //8 bytes Empieza desde el fin de clave de rastreo
        byte[] bMonto = Arrays.copyOfRange(aBytes, iFinClave + 1, iFinClave + 9); //Posicion 2 y 3 forman el año
        // logDebug(METHOD_NAME, toString(bMonto));
        //REVERSO
        // logDebug(METHOD_NAME, "Longitud de bytes: " + bMonto.length);
        // se divide en dos arreglos de 4
        byte[] byteAlta = Arrays.copyOfRange(bMonto, 0, 4);
        byte[] byteBaja = Arrays.copyOfRange(bMonto, 4, 8);
        // logDebug(METHOD_NAME, "Alta: " + toString(byteAlta));
        // logDebug(METHOD_NAME, "Baja: " + toString(byteBaja));
        //Convertir a Int
        ByteBuffer wrappedAlta = ByteBuffer.wrap(byteAlta);
        ByteBuffer wrappedBaja = ByteBuffer.wrap(byteBaja);
        int I1 = wrappedAlta.getInt();
        int I2 = wrappedBaja.getInt();
        // logDebug(METHOD_NAME, "Numero Alta: " + I1);
        // logDebug(METHOD_NAME, "Numero Baja: " + I2);
        //VALIDA NEGATIVOS
        int esNeg = 0;
        if (I1 < 0) {
            esNeg = 1;
            I1 *= -1;
        }
        if (I2 < 0) {
            esNeg = 1;
            I2 *= -1;
        }
        // logDebug(METHOD_NAME, "Es Negativo?: " + esNeg);
        // CONVERTIMOS A STRING Y CONCATENAMOS
        String monto = null;
        if (I1 > 0)
            monto = String.valueOf(I1).concat(String.valueOf(I2));
        else
            monto = String.valueOf(I2);
        // logDebug(METHOD_NAME, "Nuevo Monto: " + monto);
        //Convertir a double con dos decimales
        double montoFinal = (Double.parseDouble(monto)) / 100;
        if (esNeg == 1)
            montoFinal *= -1;
        logInfo(METHOD_NAME, "Formatter - Transformar tipoDato[Money] - valorTransformar[" + toString(bMonto) + "] - Monto[" + montoFinal + "]");
        return null;
    }

    private static List<String> getCuentas(byte[] aBytes) {
        final String METHOD_NAME = "[getCuentas]";
        // Empieza desde la posicion 12 hasta el  byte para limite de cadenas|0
        int iFinCadena = getFinString(aBytes, 12); // Fin de Clave de Rastreo
        List<String> lString = new ArrayList<String>();
        String sCadena = null;
        for (int iInicioCadena = iFinCadena + 9; iInicioCadena < aBytes.length; iInicioCadena++) { // + 8 del Monto
            iFinCadena = getFinString(aBytes, iInicioCadena);
            sCadena = new String(Arrays.copyOfRange(aBytes, iInicioCadena, iFinCadena));
            logInfo(METHOD_NAME, "Formatter - Transformar tipoDato[String] - valorTransformar[" + toString(Arrays.copyOfRange(aBytes, iInicioCadena, iFinCadena + 1)) + "] - Cuenta[" + sCadena + "]");
            lString.add(sCadena);
            iInicioCadena = iFinCadena;
        }
        return lString;
    }

    private static void logDebug(String aMethod, String aMensaje) {
        if (logger.isDebugEnabled()) {
            logger.logDebug(aMethod + ": " + aMensaje);
        }
    }

    private static void logInfo(String aMethod, String aMensaje) {
        if (logger.isInfoEnabled()) {
            logger.logInfo(aMethod + ": " + aMensaje);
        }
    }

}
