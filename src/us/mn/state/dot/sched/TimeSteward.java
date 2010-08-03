/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2010  Minnesota Department of Transportation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package us.mn.state.dot.sched;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * The time steward provides static methods dealing with time sources.
 * To use this class correctly, there are several standard library methods
 * which must be avoided (or used only carefully).
 *
 * @see java.lang.System.currentTimeMillis()
 * @see java.lang.Object.wait(long)
 * @see java.lang.Object.wait(long, int)
 * @see java.lang.Thread.join(long)
 * @see java.lang.Thread.join(long, int)
 * @see java.lang.Thread.sleep(long)
 * @see java.lang.Thread.sleep(long, int)
 * @see java.net.DatagramSocket.setSoTimeout(int)
 * @see java.net.Socket.connect(java.net.SocketAddress, int)
 * @see java.net.Socket.setSoTimeout(int)
 * @see java.net.URLConnection.setConnectTimeout(int)
 * @see java.net.URLConnection.setReadTimeout(int)
 * @see java.util.Calendar.getInstance()
 * @see java.util.Date.Date()
 *
 * @author Douglas Lau
 */
public final class TimeSteward {

	/** Time source */
	static protected TimeSource source = new SystemTimeSource();

	/** Don't allow instantiation */
	private TimeSteward() { }

	/** Set the time source */
	static public void setTimeSource(TimeSource ts) {
		assert ts != null;
		source = ts;
	}

	/** Get the current time */
	static public long currentTimeMillis() {
		return source.currentTimeMillis();
	}

	/** Get a date instance from the time source */
	static public Date getDateInstance() {
		return new Date(currentTimeMillis());
	}

	/** Get a calendar instance from the time source */
	static public Calendar getCalendarInstance() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(currentTimeMillis());
		return cal;
	}

	/** Get the current local minute-of-day as an int */
	static public int currentMinuteOfDayInt() {
		Calendar cal = getCalendarInstance();
		return cal.get(Calendar.HOUR_OF_DAY) * 60 +
		       cal.get(Calendar.MINUTE);
	}

	/** Get the current local second-of-day as an int */
	static public int currentSecondOfDayInt() {
		return secondOfDayInt(currentTimeMillis());
	}

	/** Get the local second-of-day as an int */
	static public int secondOfDayInt(long time) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		return cal.get(Calendar.HOUR_OF_DAY) * 3600 +
		       cal.get(Calendar.MINUTE) * 60 +
		       cal.get(Calendar.SECOND);
	}

	/** Get the current local date as a short YYYYMMDD string */
	static public String currentDateShortString() {
		return dateShortString(currentTimeMillis());
	}

	/** Get the local date as a short YYYYMMDD string */
	static public String dateShortString(long date) {
		SimpleDateFormat sdf = createDateFormat("yyyyMMdd", true);
		return sdf.format(new Date(date));
	}

	/** Format a date to a string.
	 * @param format Format specifier.
	 * @param local Use local time or UTC.
	 * @param date Date to format.
	 * @return Formatted string. */
	static private SimpleDateFormat createDateFormat(String format,
		boolean local)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setTimeZone(getTimeZone(local));
		return sdf;
	}

	/** Get a time zone */
	static private TimeZone getTimeZone(boolean local) {
		if(local)
			return TimeZone.getDefault();
		else
			return TimeZone.getTimeZone("UTC");
	}
}
