package com.cobiscorp.ecobis.orchestration.core.ib.transfer.spi.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Methods {
    public static String getActualDateYyyymmdd() {
        Date actualDate = new Date();

        return new SimpleDateFormat("yyyyMMdd").format(actualDate);
    }

    public static Date getFechaProceso(Date aFechaProceso, String aHoraInicioProceso) {
        if (aFechaProceso == null)
            aFechaProceso = Calendar.getInstance().getTime();

        if (aHoraInicioProceso == null || aHoraInicioProceso.isEmpty())
            return aFechaProceso;

        int hora = Integer.parseInt(aHoraInicioProceso.split(":")[0]);
        int minuto = Integer.parseInt(aHoraInicioProceso.split(":")[1]);
        Calendar aCalendar = Calendar.getInstance();
        int horaActual = aCalendar.getTime().getHours();
        int minActual = aCalendar.getTime().getMinutes();
        int diaActual = aCalendar.get(Calendar.DAY_OF_WEEK);
        // SI ES FIN DE SEMANA SE TOMA LA FECHA DE PROCESO ACTUAL
        if(diaActual == Calendar.SATURDAY || diaActual == Calendar.SUNDAY)
            return aFechaProceso;

        // SETEO DE FECHA PROCESO CON HORA ACTUAL
        aCalendar.setTime(aFechaProceso);
        Date aFechaActual = aCalendar.getTime();
        aFechaActual.setHours(horaActual);
        aFechaActual.setMinutes(minActual);
        // SETEO DE HORA DE CAMBIO
        Date aFechaNueva = aCalendar.getTime();
        aFechaNueva.setHours(hora);
        aFechaNueva.setMinutes(minuto);
        // COMPARACION DE FECHA
        int resultado = aFechaActual.compareTo(aFechaNueva);
        // SI ES MAYOR DE 0 INDICA QUE LA FECHA ACTUAL ES MAYOR POR LO QUE SE DEBE OBTENER LA SIGUIENTE FECHA HABIL
        if (resultado > 0) {
            int addDay = 1;
            // SI ES VIERNES SE PASA A LUNES COMO SIGUIENTE FECHA HABIL
            if(aCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY){
                addDay = 3;
            }
            aCalendar.add(Calendar.DATE, addDay);
            aFechaNueva = aCalendar.getTime();
            return aFechaNueva;
        }

        return aFechaProceso;
    }
}
