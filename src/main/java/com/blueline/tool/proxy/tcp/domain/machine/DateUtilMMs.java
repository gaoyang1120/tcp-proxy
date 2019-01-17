package com.blueline.tool.proxy.tcp.domain.machine;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtilMMs {
	
	private static final SimpleDateFormat sdf_date_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final SimpleDateFormat sdf_date = new SimpleDateFormat("yyyyMMdd");
	private static final SimpleDateFormat sdf_date10 = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat sdf_time = new SimpleDateFormat("HHmmss");
	private static final SimpleDateFormat sdf_time8 = new SimpleDateFormat("HH:mm:ss");
	
	public static String get8Date(Date d){
		return sdf_date.format(d);
	}
	public static String get10Date(Date d){
		return sdf_date10.format(d);
	}
	
	public static String get6Time(Date d){
		return sdf_time.format(d);
	}
	public static String get8Time(Date d){
		return sdf_time8.format(d);
	}
	/**
	 * 
	 * @param s "yyyy-MM-dd HH:mm:ss"
	 * @return
	 */
	public static Date getDateForString(String s){
		try {
			return sdf_date_time.parse(s);
		} catch (ParseException e) {
			throw new BaseException("日期["+s+"]解析失败", e);
		}
	}
	/**
	 * 
	 * @return "yyyy-MM-dd HH:mm:ss"
	 */
	public static String getStrForDate(Date date){
		return sdf_date_time.format(date);
	}
	
	public static Date addHours(Date d,  int hours){
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(Calendar.HOUR_OF_DAY, hours);
		return c.getTime();
	}
	
	public static Date addMiutis(Date d,  int minu){
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.add(Calendar.MINUTE, minu);
		return c.getTime();
	}
	/**
	 * 毫秒数
	 * @param str
	 * @return
	 */
	public static long getTimeMillis(String str){
		Date dateForString = getDateForString(str);
		long timeout = dateForString.getTime() - System.currentTimeMillis();
		return timeout;
	}
	
	/**
	 * 分钟数
	 * @param str
	 * @return
	 */
	public static long getTimeMins(Date dateForString){
		long timeout = dateForString.getTime() - System.currentTimeMillis();
		timeout = timeout/1000/60;
		return timeout;
	}
	
}
