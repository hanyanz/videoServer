package com.videoserver.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class DateUtil {	
	public static final String FORMAT_DEFAULT = "yyyy-MM-dd";
	public static final String FORMAT_ALL = "yyyy-MM-dd-HH_mm_ss";
	public static final String FORMAT_TIME = "HH_mm_ss";
	public static final String FORMAT_TIME_HOUR = "HH";
	public static final String FORMAT_TIME_MINUTE = "mm";
	
	public static String getToday(){
		return new SimpleDateFormat(FORMAT_DEFAULT).format(Calendar.getInstance().getTime());
	}
	
	public static String getNowTime(){
		return new SimpleDateFormat(FORMAT_TIME).format(Calendar.getInstance().getTime());
	}
	
	public static String getFullTime(){
		return new SimpleDateFormat(FORMAT_ALL).format(Calendar.getInstance().getTime());
	}
	
	public static long date2Long(String date) throws ParseException{
		SimpleDateFormat format = new SimpleDateFormat(FORMAT_ALL);
        Date bDate = format.parse(date);
        Calendar d1 = new GregorianCalendar();
        d1.setTime(bDate);
        return d1.getTimeInMillis();
	}
	
	public static String long2DateStr(long msel,String format) {
		Date date = new Date(msel);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(date);
	}
}
