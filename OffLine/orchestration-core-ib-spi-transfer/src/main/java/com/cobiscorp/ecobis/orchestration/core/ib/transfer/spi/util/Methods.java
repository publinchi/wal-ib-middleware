package com.cobiscorp.ecobis.orchestration.core.ib.transfer.spi.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Methods {
    public static String getActualDateYyyymmdd(){
        Date actualDate = new Date();

        return new SimpleDateFormat("yyyyMMdd").format(actualDate);
    }
}
