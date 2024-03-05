/**
 * 
 */
package com.loan.aggregator.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 
 */
public class DateUtility {
	
	public static Calendar getCalendarInUTCZone() {
		Calendar calendar=Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		return calendar;
	}

	public static long getDaysDifference(Date d1, Date d2) {
		Calendar cal = getCalendarInUTCZone();
		cal.setTime(d1);
		long t1 = cal.getTimeInMillis();
		cal.setTime(d2);
		long t2 = cal.getTimeInMillis();
		long diff = Math.abs(t2 - t1);
		final int ONE_DAY = 1000 * 60 * 60 * 24;
		long d = diff / ONE_DAY;
		return d;
	}
}
