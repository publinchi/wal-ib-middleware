
package cobiscorp.ecobis.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Util {
	private static SimpleDateFormat sdfDefLarge = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.s");
	private static SimpleDateFormat sdfDefShort = new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat sdfDefTime = new SimpleDateFormat("hh:mm:ss.s");
	private static SimpleDateFormat sdfLarge = new SimpleDateFormat("MMM dd yyyy hh:mmaa");
	private static SimpleDateFormat sdfShort = new SimpleDateFormat("MMM dd yyyy");
	private static SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mmaa");

	public static Calendar convertStringToCalendar(String s) {
		if (s == null)
			return null;
		SimpleDateFormat sdf = null;
		int length = s.length();
		Date d1 = null;
		if (length >= 21) {
			// yyyy-MM-dd hh:mm:ss.s
			sdf = sdfDefLarge;
		}
		else if (length >= 17) {
			// MMM dd yyyy hh:mmaa
			sdf = sdfLarge;
		} else if (length >= 10) {
			if (s.contains("-")) {
				// yyyy-MM-dd
				sdf = sdfDefShort;
			} else if (s.contains(":")){
				// hh:mm:ss.s
				sdf = sdfDefTime;
			} else {
				// MMM dd yyyy
				sdf = sdfShort;
			}
				
		} else if (length >= 6) {
			// hh:mmaa
			sdf = sdfTime;
		}
		if (sdf == null)
			return null;
		try {
			d1 = sdf.parse(s);
		} catch (ParseException e) {
			return null;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(d1);
		return cal;
	}

}
	