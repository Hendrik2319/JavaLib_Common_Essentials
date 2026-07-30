package net.schwarzbaer.java.lib.system;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Locale;
import java.util.Vector;

public class DateTimeFormatter
{
	public static String getTimeStr(long millis, boolean withTextDay, boolean withDate, boolean dateIsLong, boolean withTime, boolean withTimeZone) {
		/*
		cal.setTimeInMillis(millis);
		return getTimeStr(cal, Locale.ENGLISH, withTextDay, withDate, dateIsLong, withTime, withTimeZone);
		*/
		return getTimeStr(millis, Locale.ENGLISH, getFormatStr(withTextDay, withDate, dateIsLong, withTime, withTimeZone));
	}

	public static String getTimeStr(long millis, Locale locale, boolean withTextDay, boolean withDate, boolean dateIsLong, boolean withTime, boolean withTimeZone) {
		/*
		cal.setTimeInMillis(millis);
		return getTimeStr(cal, locale, withTextDay, withDate, dateIsLong, withTime, withTimeZone);
		*/
		return getTimeStr(millis, locale, getFormatStr(withTextDay, withDate, dateIsLong, withTime, withTimeZone));
	}

	public static String getTimeStr(long millis, Locale locale, String format) {
		/*
		cal.setTimeInMillis(millis);
		return String.format(locale, format, cal);
		*/
		return String.format(locale, format, ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault()));
	}

	public static String getTimeStr(Calendar cal, boolean withTextDay, boolean withDate, boolean dateIsLong, boolean withTime, boolean withTimeZone) {
		return getTimeStr(cal, Locale.ENGLISH, withTextDay, withDate, dateIsLong, withTime, withTimeZone);
	}

	public static String getTimeStr(Calendar cal, Locale locale, boolean withTextDay, boolean withDate, boolean dateIsLong, boolean withTime, boolean withTimeZone) {
		String format = getFormatStr(withTextDay, withDate, dateIsLong, withTime, withTimeZone);
		return String.format(locale, format, cal);
	}

	public static String getSortableFormatStr(boolean withDate, boolean withTime, String delimiterD, String delimiterD2T, String delimiterT) {
		delimiterD   = delimiterD==null ? "" : delimiterD;
		delimiterT   = delimiterT==null ? "" : delimiterT;
		delimiterD2T = delimiterD2T==null || !withDate || !withTime ? "" : delimiterD2T;
		String dateStr = !withDate ? "" : "%s%s%s%s%s".formatted("%1$tY",delimiterD,"%1$tm",delimiterD,"%1$td");
		String timeStr = !withTime ? "" : "%s%s%s%s%s".formatted("%1$tH",delimiterT,"%1$tM",delimiterT,"%1$tS");
		return "%s%s%s".formatted(dateStr, delimiterD2T, timeStr);
	}

	public static String getFormatStr(boolean withTextDay, boolean withDate, boolean dateIsLong, boolean withTime, boolean withTimeZone) {
		Vector<String> formatParts = new Vector<>(10);
		if (withTextDay) formatParts.add("%1$tA"+getColon(withDate || withTime || withTimeZone));
		if (withDate) {
			if (dateIsLong) {
				formatParts.add("%1$te.");
				formatParts.add("%1$tb" );
				formatParts.add("%1$tY"+getColon(withTime || withTimeZone));
			} else{
				formatParts.add("%1$td.%1$tm.%1$ty"+getColon(withTime || withTimeZone));
			}
		}
		if (withTime) formatParts.add("%1$tT");
		if (withTimeZone) formatParts.add("[%1$tZ:%1$tz]");
		
		String format = String.join(" ", formatParts);
		return format;
	}

	private static String getColon(boolean b) {
		return b ? "," : "";
	}
	
	public  static String getDurationStr_ms(long duration_ms) { return getDurationStr(duration_ms/1000, duration_ms % 1000); }
	public  static String getDurationStr(double duration_sec) {
		long duration_sec2 = Math.round(Math.floor(duration_sec));
		long duration_ms = Math.round(Math.floor((duration_sec-Math.floor(duration_sec))*1000));
		return getDurationStr( duration_sec2, duration_ms==0 ? null : duration_ms );
	}
	public  static String getDurationStr(long duration_sec) { return getDurationStr(duration_sec, null); }
	private static String getDurationStr(long duration_sec, Long duration_ms) {
		if (duration_sec<0) throw new IllegalArgumentException("DateTimeFormatter.getDurationStr( duration_sec ): duration_sec must not be negatiive");
		long s =  duration_sec      %60;
		long m = (duration_sec/60  )%60;
		long h =  duration_sec/3600;

		String msStr = duration_ms==null ? "" : String.format(".%03d", duration_ms);
		
		if (duration_sec < 60) {
			return String.format("%d%s s", s, msStr);
		}
		
		if (duration_sec < 3600)
			return String.format("%d:%02d%s min", m, s, msStr);
		
		return String.format("%d:%02d:%02d%s h", h, m, s, msStr);
	}

	public static long getTimeInMillis(int year, int month, int date, int hourOfDay, int minute, int second) {
		/*
		cal.clear();
		cal.set(year, month-1, date, hourOfDay, minute, second);
		return cal.getTimeInMillis();
		*/
		ZonedDateTime dateTime = ZonedDateTime.of(year, month, date, hourOfDay, minute, second, 0, ZoneId.systemDefault());
		return dateTime.toInstant().toEpochMilli();
	}
}
