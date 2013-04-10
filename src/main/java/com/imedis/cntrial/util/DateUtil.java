package com.imedis.cntrial.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;


/**
 * Date Utility Class
 * This is used to convert Strings to Dates and Timestamps
 *
 * <p>
 * <a href="DateUtil.java.html"><i>View Source</i></a>
 * </p>
 * 
 * @author <a href="mailto:matt@raibledesigns.com">Matt Raible</a>
 *  Modified by <a href="mailto:dan@getrolling.com">Dan Kibler </a> 
 *   to correct time pattern. Minutes should be mm not MM
 * 	(MM is month). 
 * @version $Revision: 1.1 $ $Date: 2007/10/22 23:52:58 $
 */
public class DateUtil {
    //~ Static fields/initializers =============================================
    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);
    private static String defaultDatePattern = null;
    private static String timePattern = "HH:mm";


    /**
     * Return default datePattern (MM/dd/yyyy)
     * @return a string representing the date pattern on the UI
     */
    public static String getDatePattern() {
        Locale locale = LocaleContextHolder.getLocale();
        try {
            defaultDatePattern = ResourceBundle.getBundle(Constants.BUNDLE_KEY, locale)
                .getString("date.format");
        } catch (MissingResourceException mse) {
            defaultDatePattern = "yyyy-MM-dd";
        }
        
        return defaultDatePattern;
    }
    
    //~ Methods ================================================================

    /**
     * This method removes the separator of the dateString
     * in the format you specify on input
     *
     * @param dateString 
     * @return a string afterRemoving
     */
    public static String removeSeparator(String dateString) {
    	
    	String datePattern = getDatePattern();
        
    	if(datePattern.indexOf('-') > 0)
    	{
    		dateString = dateString.replace("-", "");
    	}
    	else if(datePattern.indexOf('/') > 0)
    	{
    		dateString = dateString.replace("/", "");
    	}
        
    	return dateString;
     }
   

    /**
     * This method adds the separator to the string
     * in the format you specify on input
     *
     * @param string 
     * @return a dateString afterAdding
     */
    public static String addSeparator(String string) {
    	
    	String datePattern = getDatePattern();
    	datePattern = "yyyy-MM-dd";
    	String separator = "";
        
    	if(datePattern.indexOf('-') > 0)
    	{
    		separator = "-";
    	}
    	else if(datePattern.indexOf('/') > 0)
    	{
    		separator = "/";
    	}
    	String dateString = string; 
    	int fromIndex = 0;
    	int index = datePattern.indexOf(separator,fromIndex);
    	while(index > 0)
    	{
    		dateString = dateString.substring(0, index).concat(separator).concat(dateString.substring(index));
    		fromIndex = index+1;
    		index = datePattern.indexOf(separator,fromIndex);
    	}
        
    	return dateString;
     }
   

    
    public static String getDateTimePattern() {
        return DateUtil.getDatePattern() + " HH:mm:ss.S";
    }

    /**
     * This method attempts to convert an Oracle-formatted date
     * in the form dd-MMM-yyyy to mm/dd/yyyy.
     *
     * @param aDate date from database as a string
     * @return formatted string for the ui
     */
    public static final String getDate(Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "";

        if (aDate != null) {
            df = new SimpleDateFormat(getDatePattern());
            returnValue = df.format(aDate);
        }

        return (returnValue);
    }

    /**
     * This method generates a string representation of a date/time
     * in the format you specify on input
     *
     * @param aMask the date pattern the string is in
     * @param strDate a string representation of a date
     * @return a converted Date object
     * @see java.text.SimpleDateFormat
     * @throws java.text.ParseException
     */
    public static final Date convertStringToDate(String aMask, String strDate)
      throws ParseException {
        SimpleDateFormat df = null;
        Date date = null;
        df = new SimpleDateFormat(aMask);

        if (logger.isDebugEnabled()) {
            logger.debug("converting '" + strDate + "' to date with mask '"
                      + aMask + "'");
        }

        try {
            date = df.parse(strDate);
        } catch (ParseException pe) {
            //log.error("ParseException: " + pe);
            throw new ParseException(pe.getMessage(), pe.getErrorOffset());
        }

        return (date);
    }

    /**
     * This method returns the current date time in the format:
     * MM/dd/yyyy HH:MM a
     *
     * @param theTime the current time
     * @return the current date/time
     */
    public static String getTimeNow(Date theTime) {
        return getDateTime(timePattern, theTime);
    }

    /**
     * This method returns the current date in the format: MM/dd/yyyy
     * 
     * @return the current date
     * @throws java.text.ParseException
     */
    public static Calendar getToday() throws ParseException {
        Date today = new Date();
        SimpleDateFormat df = new SimpleDateFormat(getDatePattern());

        // This seems like quite a hack (date -> string -> date),
        // but it works ;-)
        String todayAsString = df.format(today);
        Calendar cal = new GregorianCalendar();
        cal.setTime(convertStringToDate(todayAsString));

        return cal;
    }

    /**
     * This method generates a string representation of a date's date/time
     * in the format you specify on input
     *
     * @param aMask the date pattern the string is in
     * @param aDate a date object
     * @return a formatted string representation of the date
     * 
     * @see java.text.SimpleDateFormat
     */
    public static final String getDateTime(String aMask, Date aDate) {
        SimpleDateFormat df = null;
        String returnValue = "";

        if (aDate == null) {
            logger.error("aDate is null!");
        } else {
            df = new SimpleDateFormat(aMask);
            returnValue = df.format(aDate);
        }

        return (returnValue);
    }

    /**
     * This method generates a string representation of a date based
     * on the System Property 'dateFormat'
     * in the format you specify on input
     * 
     * @param aDate A date to convert
     * @return a string representation of the date
     */
    public static final String convertDateToString(Date aDate) {
        return getDateTime(getDatePattern(), aDate);
    }

    /**
     * This method converts a String to a date using the datePattern
     * 
     * @param strDate the date to convert (in format MM/dd/yyyy)
     * @return a date object
     * 
     * @throws java.text.ParseException
     */
    public static Date convertStringToDate(String strDate)
      throws ParseException {
        Date aDate = null;

        try {
            if (logger.isDebugEnabled()) {
                logger.debug("converting date with pattern: " + getDatePattern());
            }

            aDate = convertStringToDate(getDatePattern(), strDate);
        } catch (ParseException pe) {
            logger.error("Could not convert '" + strDate
                      + "' to a date, throwing exception");
            pe.printStackTrace();
            throw new ParseException(pe.getMessage(),
                                     pe.getErrorOffset());
                    
        }

        return aDate;
    }
  
//     public static void main(String args[])
//     {
//    	 addSeparator("19821220");
//     }
    /**
	 * 将页面上传过来的字符串型时间转化成long类型
	 * 
	 * @param dateInString
	 *            字符串型时间
	 * @return
	 * datecode == "yyyy-MM-dd" or datecode == "yyyy-MM-dd HH:mm:ss"
	 */
	public static Long convertDateInStringToLong(String dateInString, String datecode) {
		if (dateInString == null || dateInString.equals(""))
			return null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(datecode);
		try {
			return new Long(dateFormat.parse(dateInString).getTime()/1000);
		} catch (ParseException parseException) {
			return new Long(new Date().getTime());
		}
	}

	/**
	 * 将Long表示的时间转换为日期字符串
	 * 
	 * @param dateTime
	 * @return 转换后的字符串 如果Long为null，则返回null
	 * datecode == "yyyy-MM-dd" or datecode == "yyyy-MM-dd HH:mm:ss"
	 * @throws java.text.ParseException
	 */
	public static String convertLongToDateString(Long dateTime, String datecode) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(datecode);
		String dateStr = null;
		if (dateTime != null) {
			dateStr = sdf.format(new Date(dateTime*1000));
		}
		return dateStr;
	}
	
	/**
	 * 将long型的时间戳转化为日历类型
	 * <p>
	 * @param currentTimestamp:当前时戳
	 * @return Calendar 日历对象
	 * @author zhaochunjiao
	 */
	private static Calendar convertLongToCalendar(long currentTimestamp)
	{
		//将毫秒数转化为日期
		Date date = new Date(currentTimestamp);
		Calendar cal = Calendar.getInstance(Locale.CHINA);
		cal.setTime(date);
		//System.out.println("初始："+new SimpleDateFormat("yyyy-MM-dd E HH:mm:ss").format(cal.getTime()));
		return cal;
	}
	
	/**
	 * 得到给定日期（long型的时间戳）所在星期的星期一的开始时戳和星期天的结束时戳
	 * <p>
	 * @param currentTimestamp:当前时戳
	 * @return 星期一的开始时戳和星期天的结束时戳组成的数组
	 * @author zhaochunjiao
	 */
	@SuppressWarnings("static-access")
	public static long[] getMondayAndSundayOfWeek(long currentTimestamp){
		Calendar cal = convertLongToCalendar(currentTimestamp);

		long[] dayArray = new long[2];
		//System.out.println("Tools--dayInWeek: ");
		try {
			int days = cal.get(cal.DAY_OF_WEEK) - 2;
			int dates = cal.get(cal.DAY_OF_MONTH);
			int months = cal.get(cal.MONTH);
			int years = cal.get(cal.YEAR);
			
			cal.set(years, months, dates - days + 0, 0, 0 ,0);
			long monday = cal.getTime().getTime();
			//System.out.println(new SimpleDateFormat("yyyy-MM-dd E HH:mm:ss").format(cal.getTime()));
			cal.set(years, months, dates - days + 6, 23, 59, 59);
			long sunday = cal.getTime().getTime();
			//System.out.println(new SimpleDateFormat("yyyy-MM-dd E HH:mm:ss").format(cal.getTime()));
			dayArray[0] = monday;
			dayArray[1] = sunday;
		} catch (Exception ex) {
			//System.out.println("Tools--dayInWeek: " + ex.toString());
		}
		return dayArray;
	}
	
	/**
	 * 得到给定日期（long型的时间戳）所在月份的第一天和最后一天的时戳
	 * <p>
	 * @param currentTimestamp:当前时戳
	 * @return 月份的第一天和最后一天的时戳组成的数组
	 * @author zhaochunjiao
	 */
	@SuppressWarnings("static-access")
	public static long[] getFirstAndLastDaysOfMonth(long currentTimestamp) {
		Calendar cal = convertLongToCalendar(currentTimestamp);
		int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		long[] days = new long[2];
		
		int months = cal.get(cal.MONTH);
		int years = cal.get(cal.YEAR);
		cal.set(years, months, 1, 0 ,0 ,0);
		long firstDay = cal.getTime().getTime();
		//System.out.println(new SimpleDateFormat("yyyy-MM-dd E HH:mm:ss").format(cal.getTime()));
		cal.set(years, months, maxDay, 23, 59, 59);
		long lastDay = cal.getTime().getTime();
		//System.out.println(new SimpleDateFormat("yyyy-MM-dd E HH:mm:ss").format(cal.getTime()));
		days[0] = firstDay;
		days[1] = lastDay;

		return days;
	}
	
	/**
	 * 得到给定日期（long型的时间戳）所在年份的第一天和最后一天的时戳
	 * <p>
	 * @param currentTimestamp:当前时戳
	 * @return 年份的第一天和最后一天的时戳组成的数组
	 * @author zhaochunjiao
	 */
	@SuppressWarnings("static-access")
	public static long[] getFirstAndLastDaysOfYear(long currentTimestamp) {
		Calendar cal = convertLongToCalendar(currentTimestamp);
		long[] days = new long[2];
		
		int years = cal.get(cal.YEAR);
		cal.set(years, 0, 1, 0 ,0 ,0);
		long firstDay = cal.getTime().getTime();
		//System.out.println(new SimpleDateFormat("yyyy-MM-dd E HH:mm:ss").format(cal.getTime()));
		cal.set(years, 11, 31, 23, 59, 59);
		long lastDay = cal.getTime().getTime();
		//System.out.println(new SimpleDateFormat("yyyy-MM-dd E HH:mm:ss").format(cal.getTime()));
		days[0] = firstDay;
		days[1] = lastDay;

		return days;
	}
	
    /** 
     * 获取某年第一天日期 
     * @param year 年份 
     * @return Date 
     */  
    public static Date getCurrYearFirst(int year){  
        Calendar calendar = Calendar.getInstance();  
        calendar.clear();  
        calendar.set(Calendar.YEAR, year);  
        Date currYearFirst = calendar.getTime();  
        return currYearFirst;  
    }  
      
    /** 
     * 获取某年最后一天日期 
     * @param year 年份 
     * @return Date 
     */  
    public static Date getCurrYearLast(int year){  
        Calendar calendar = Calendar.getInstance();  
        calendar.clear();  
        calendar.set(Calendar.YEAR, year);  
        calendar.roll(Calendar.DAY_OF_YEAR, -1);  
        Date currYearLast = calendar.getTime();  
          
        return currYearLast;  
    }  
	
	/**
	 * 得到给定日期（long型的时间戳） 的零点和24点的时戳
	 * <p>
	 * @param currentTimestamp:当前时戳
	 * @return 该日期的零点和24点的时戳数组
	 * @author zhaochunjiao
	 */
	@SuppressWarnings("static-access")
	public static long[] getOneDayTimestamp(long currentTimestamp) {
		Calendar cal = convertLongToCalendar(currentTimestamp);
		long[] timeArray = new long[2];
		
		int dates = cal.get(cal.DATE);
		int months = cal.get(cal.MONTH);
		int years = cal.get(cal.YEAR);
		cal.set(years, months, dates, 0, 0 ,0);
		long startTime = cal.getTime().getTime();
		//System.out.println(new SimpleDateFormat("yyyy-MM-dd E HH:mm:ss").format(cal.getTime()));
		cal.set(years, months, dates, 23, 59, 59);
		long endTime = cal.getTime().getTime();
		//System.out.println(new SimpleDateFormat("yyyy-MM-dd E HH:mm:ss").format(cal.getTime()));
		timeArray[0] = startTime;
		timeArray[1] = endTime;

		return timeArray;
	}
	
	/**
	 * 得到给定日期（long型的时间戳） 的n天后23:59:59的时戳<br >
	 * <font color = "red">
	 * 给定的日期算一天，如：给定日期是2012-2-15，要得到7天后23:59:59的时戳时戳 <br >
	 * 就是得到2012-2-21 23:59:59的时戳
	 * </font>
	 * @param currentTimestamp:当前时戳
	 * @param days: 天数
	 * @return n天后23:59:59的时戳
	 * @author zhaochunjiao
	 * @create 2012-2-15 上午09:08:47
	 */
	public static long getTimestampOfAfterDay(long currentTimestamp, int days) {
		long mills = (days-1)*24*60*60*1000;//n天的毫秒数
		long sevenDaysTime = currentTimestamp + mills;//n天后的毫秒数
		long[] oneDayTimestamp = getOneDayTimestamp(sevenDaysTime);//得到给定日期（long型的时间戳） 的零点和24点的时戳

		return oneDayTimestamp[1];
	}
	
	
	public static int getCurrentTime(){
		return (int) (System.currentTimeMillis()/1000);
	}
}
