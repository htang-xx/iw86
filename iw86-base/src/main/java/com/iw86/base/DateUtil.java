/**
 * 
 */
package com.iw86.base;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.iw86.lang.Conver;
import com.iw86.lang.StringUtil;

/**
 * 日期处理类
 * @author tanghuang
 */
public class DateUtil {
	
	/** 标准日期格式 */
	public final static String DATE_PATTERN = "yyyy-MM-dd";
	/** 标准日期时间格式，精确到秒 */
	public final static String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	/** js标准日期时间格式，精确到秒 */
	public final static String DATETIME_PATTERN_JS = "yyyy/MM/dd HH:mm:ss";
	/** 字符串标准日期时间格式，精确到毫秒 */
	public final static String DATETIME_PATTERN_STR = "yyyyMMddHHmmssSSS";
	/** 年月格式 */
	public final static String DATETIME_PATTERN_YM = "yyMM";
	
	public static ThreadLocal<SimpleDateFormat> DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
		synchronized protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(DATE_PATTERN);
		};
	};
	public static ThreadLocal<SimpleDateFormat> DATETIME_FORMAT = new ThreadLocal<SimpleDateFormat>() {
		synchronized protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(DATETIME_PATTERN);
		};
	};
	public static ThreadLocal<SimpleDateFormat> DATETIME_JS_FORMAT = new ThreadLocal<SimpleDateFormat>() {
		synchronized protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(DATETIME_PATTERN_JS);
		};
	};
	public static ThreadLocal<SimpleDateFormat> DATETIME_STR_FORMAT = new ThreadLocal<SimpleDateFormat>() {
		synchronized protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(DATETIME_PATTERN_STR);
		};
	};
	public static ThreadLocal<SimpleDateFormat> DATETIME_YM_FORMAT = new ThreadLocal<SimpleDateFormat>() {
		synchronized protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(DATETIME_PATTERN_YM);
		};
	};
	
	/**
	 * unix时间戳
	 * @return
	 */
	public static int unixtime() {
		return (int)(System.currentTimeMillis() / 1000);
	}

	/**
	 * 取n天前/后日期，格式“yyyy-MM-dd”
	 * @param date null表示取当天
	 * @param n
	 * @return
	 */
	public static String getDate(Date date, int n) {
		if (date == null)
			date = now();
		if (n != 0)
			date = nextDay(date, n);
		return format(date, DATE_FORMAT.get());
	}

	/**
	 * 取当前n天前/后日期，格式“yyyy-MM-dd”
	 * @param n
	 * @return
	 */
	public static String getDate(int n) {
		return getDate(null, n);
	}

	/**
	 * 获得js格式的n天前/后日期串，格式“yyyy/MM/dd HH:mm:ss”
	 * @param date null表示取当天
	 * @param n
	 * @return
	 */
	public static String getJsDate(Date date, int n) {
		if (date == null)
			date = now();
		if (n != 0)
			date = nextDay(date, n);
		return format(date, DATETIME_JS_FORMAT.get());
	}

	/**
	 * 获得标准格式的当前日期串，格式“yyyy-MM-dd”
	 * @return
	 */
	public static String getCurrentDate() {
		return getDate(0);
	}

	/**
	 * 获得完整的当前日期串，格式“yyyy-MM-dd HH:mm:ss”
	 * @return
	 */
	public static String getCurrentTime() {
		return format(now(), DATETIME_FORMAT.get());
	}

	/**
	 * 获得一个当前日期字符串，可用来做临时文件名，特定的标志等，格式“yyyyMMddHHmmssSSS”
	 * @return
	 */
	public static String getCurrentStr() {
		return format(now(), DATETIME_STR_FORMAT.get());
	}

	/**
	 * 获得一个当前日期的字符串,只取年月,格式"yyMM"
	 * @return
	 */
	public static String getCurrentYM() {
		return format(now(), DATETIME_YM_FORMAT.get());
	}

	/**
	 * 获得当前日
	 */
	public static String getDay() {
		Calendar today = Calendar.getInstance();
		return String.valueOf(today.get(Calendar.DATE));
	}

	/**
	 * 只取日期字符串的年月
	 * @param dateStr 只针对yyyy-MM-dd HH:mm:ss格式
	 * @return
	 */
	public static String getYM(String dateStr) {
		Date date;
		try {
			date = DATETIME_FORMAT.get().parse(dateStr);
		} catch (Exception e) {
			date = new Date();
		}
		return format(date, DATETIME_YM_FORMAT.get());
	}

	/**
	 * 处理时间戳为‘yyyy-MM-dd HH:mm:ss’
	 * @param timeMillis
	 * @return
	 */
	public static String getLongDateStr(String timeMillis) {
		if (timeMillis.length() < 13) { // 没有13位，则补足
			int n = 13 - timeMillis.length();
			for (int i = 0; i < n; i++) {
				timeMillis = timeMillis + "0";
			}
		}
		long tms = Conver.toLong(timeMillis, 0L);
		return format(fromLong(tms), DATETIME_FORMAT.get());
	}

	/**
	 * 以指定格式格式化日期
	 * @param aDate
	 * @param aFormat
	 * @return
	 */
	public static String format(Date aDate, SimpleDateFormat aFormat) {
		if (aDate == null || aFormat == null)
			return "";
		return aFormat.format(aDate);
	}

	/**
	 * 以指定格式格式化日期. String formatText 为格式字符串，如："yyyy-MM-dd HH:mm:ss"
	 * @return String 返回格式好的字符串日期
	 */
	public static String format(Date date, String formatText) {
		SimpleDateFormat f = new SimpleDateFormat(formatText);
		return format(date, f);
	}

	/**
	 * 获得当前日期
	 * @return
	 */
	public static Date now() {
		return Calendar.getInstance().getTime();
	}

	/**
	 * 将指定格式的日期字符串转换为日期
	 * @param dateStr
	 * @param formatText
	 * @return
	 */
	public static Date parse(String dateStr, String formatText) {
		if (!StringUtil.isEmpty(dateStr)) {
			try {
				SimpleDateFormat f = new SimpleDateFormat(formatText);
				return f.parse(dateStr);
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 解析格式“yyyy-MM-dd”的日期字符串
	 * @param dateStr
	 * @return
	 */
	public static Date parseDate(String dateStr) {
		if (!StringUtil.isEmpty(dateStr)) {
			try {
				return DATE_FORMAT.get().parse(dateStr);
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 解析格式“yyyy-MM-dd HH:mm:ss”的日期字符串
	 * @param dateStr
	 * @return
	 */
	public static Date parseDTime(String dateStr) {
		if (!StringUtil.isEmpty(dateStr)) {
			try {
				return DATETIME_FORMAT.get().parse(dateStr);
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * 取得指定日期过n个时间段后的日期(当 n为负数表示指定日期之前)
	 * @param date 日期
	 * @param field 时间段，如Calendar.DAY_OF_MONTH，Calendar.HOUR_OF_DAY
	 * @param n
	 * @return
	 */
	public static Date next(Date date, int field, int n) {
		Calendar cal = Calendar.getInstance();
		if (date != null)  cal.setTime(date);
		cal.add(field, n);
		return cal.getTime();
	}

	/**
	 * 取得指定日期过 day 天后的日期 (当 day 为负数表示指定月之前);
	 * @param date 日期 为null时表示当天
	 * @param day 相加(相减)的天数
	 */
	public static Date nextDay(Date date, int day) {
		return next(date, Calendar.DAY_OF_MONTH, day);
	}

	/**
	 * 取某个日期当月第一天的日期
	 * @param date null时表示取当天
	 * @return
	 */
	public static Date getFirstDayOfMonth(Date date) {
		Calendar c = Calendar.getInstance();
		if (date != null) c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
	}

	/**
	 * 取某个日期当月最后一天的日期
	 * @param date null时表示取当天
	 * @return
	 */
	public static Date getLastDayOfMonth(Date date) {
		Calendar c = Calendar.getInstance();
		if (date != null) c.setTime(date);
		int d = c.getActualMaximum(Calendar.DAY_OF_MONTH);
		c.set(Calendar.DAY_OF_MONTH, d);
		return c.getTime();
	}
	
	/**
	 * 取某个日期当前周第一天(中国：周一)
	 * @return
	 */
	public static Date getFirstDayOfWeek(Date date) {
		Calendar c = Calendar.getInstance();
		if (date != null) c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, -1); //解决周日会出现 并到下一周的情况    
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); 
		return c.getTime();
	}
	
	/**
	 * 取某个日期当前周最后一天(中国：周日)
	 * @return
	 */
	public static Date getLastDayOfWeek(Date date) {
		Calendar c = Calendar.getInstance();
		if (date != null) c.setTime(date);
		c.add(Calendar.DAY_OF_MONTH, -1); //解决周日会出现 并到下一周的情况    
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        c.add(Calendar.DAY_OF_MONTH, 6);
		return c.getTime();
	}

	/**
	 * 时间戳转换日期
	 * @param timeMillis
	 * @return
	 */
	public static Date fromLong(long timeMillis) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timeMillis);
		return cal.getTime();
	}

	/**
	 * 获得两个时间字符串的时间差
	 * @param date1
	 * @param date2
	 * @param dformat
	 * @return
	 */
	public static long subtraction(String date1, String date2, String dformat)
			throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat(dformat);
		long time1 = dateFormat.parse(date1).getTime();
		long time2 = dateFormat.parse(date2).getTime();
		return time2 - time1;
	}

	/**
	 * 获得两个时间字符串的时间差(以小时为单位,如果是两个小时段需为24小时内的)
	 * @param date1
	 * @param date2
	 * @param dformat
	 * @return
	 */
	public static String subHouse(String date1, String date2, String dformat) {
		try {
			NumberFormat numFormat = new DecimalFormat("0.0");
			Double subh = (double) subtraction(date1, date2, dformat) / 3600000;
			if (subh > 0)
				return numFormat.format(subh);
			else
				return numFormat.format(subh + 24);
		} catch (Exception e) {
		}
		return "";
	}

	/**
	 * 根据生日获取星座
	 * @param birth 生日字符串，格式YYYY-mm-dd
	 * @return
	 */
	public static String getAstro(String birth) {
		if (StringUtil.isEmpty(birth)) return "";
		Date date = parseDate(birth);
		if(date==null) return "";
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		String s = "魔羯水瓶双鱼牡羊金牛双子巨蟹狮子处女天秤天蝎射手魔羯";
		int[] arr = { 20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22 };
		int start = month * 2 - (day < arr[month - 1] ? 2 : 0);
		return s.substring(start, start + 2) + "座";
	}
	
	/**
	 * 获得指定日期年份和季节<br>
	 * 格式：[20131]表示2013年第一季度
	 * @param cal 日期
	 */
	public static String yearAndSeason(Date date) {
		Calendar c = Calendar.getInstance();
		if (date != null) c.setTime(date);
		return new StringBuilder().append(c.get(Calendar.YEAR)).append(c.get(Calendar.MONTH) / 3 + 1).toString();
	}
	
	/**
	 * 以友好的方式显示时间（如:2小时前）
	 * @param time
	 * @return
	 */
	public static String friendly_time(Date time) {
	    if(time == null) return "未知";
	    int ct = (int)((System.currentTimeMillis() - time.getTime())/1000);
	    if(ct < 3600)
	        return StringUtil.str(Math.max(ct/60,1),"分钟前");
	    if(ct >= 3600 && ct < 86400)
	        return StringUtil.str(ct/3600,"小时前");
	    if(ct >= 86400 && ct < 2592000){ //86400 * 30
	        int day = ct / 86400 ;         
	        return (day>1)?"昨天":StringUtil.str(day,"天前");
	    }
	    if(ct >= 2592000 && ct < 31104000) //86400 * 30
	        return StringUtil.str(ct/2592000,"个月前");
	    return StringUtil.str(ct/31104000,"年前");    
	}

}
