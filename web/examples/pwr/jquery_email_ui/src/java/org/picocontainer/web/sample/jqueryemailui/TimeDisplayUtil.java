package org.picocontainer.web.sample.jqueryemailui;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeDisplayUtil {
	
	public static Timestamp getTimeStamp(long lTime)
	{
		if (lTime == 0)
		{
			return null;
		}
		else
		{
			return new Timestamp(lTime);
		}
	}

	public static String formatTime(long lTime, String format, String timeZoneId)
	{
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		formatter.setTimeZone(TimeZone.getTimeZone(timeZoneId));
		return formatter.format(getTimeStamp(lTime));
	}

	public static String formatTime(Date lDate, String format, String timeZoneId)
	{
		return formatTime(lDate.getTime(), format, timeZoneId);
	}
}
