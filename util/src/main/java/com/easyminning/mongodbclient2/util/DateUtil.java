package com.easyminning.mongodbclient2.util;

import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日期转换的工具类
 * @author lishuai
 * @version 1.0
 * @since 1.0
 *
 */
public class DateUtil {
  public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException
  {
    Date currentDate = DateUtil.createDate("2010-8-20 12:01:01",YYYYMMDD_HHMMSS);
    Date d =  DateUtil.getAfterHoursDate(currentDate,1);
    System.out.println(DateUtil.format(d, YYYYMMDD_HHMMSS));
  }
  private static final Logger logger = Logger.getLogger(DateUtil.class);
  public static final String YYYYMMDD_HHMMSS = "yyyy-MM-dd HH:mm:ss";
  public static final String YYYYMMDD = "yyyy-MM-dd";
  public static final String YYYYMMDD_000000 = "yyyy-MM-dd 00:00:00";
  public static Date getAfterHoursDate(Date currentDate,int hours)
  {
    Date returnDate = null;
    Calendar c = Calendar.getInstance();
    c.setTime(currentDate);
    c.add(Calendar.HOUR, hours);
    Date d = c.getTime();
    SimpleDateFormat sdf = new SimpleDateFormat(YYYYMMDD_HHMMSS);
    try {
      returnDate = sdf.parse(sdf.format(d));
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return returnDate;
  }
  /**
   * 获取一个时间范围内的所有日期
   * @param beginDate 开始日期
   * @param endDate 结束日期
   * @return
   */
  public static List<Date> getDates(Date beginDate,Date endDate)
  {
    beginDate = formatToDate(beginDate,YYYYMMDD);
    endDate = formatToDate(endDate,YYYYMMDD);
    List<Date> dates = new ArrayList<Date>();;
    if(endDate.compareTo(beginDate)>0)
    {
      Date index = formatToDate(beginDate,YYYYMMDD);
      while(index.compareTo(endDate)<=0){
        dates.add(index);
        index = getTomorrow(index);
      }
    }
    else if(endDate.compareTo(beginDate)==0){
      dates.add(formatToDate(beginDate,YYYYMMDD));
    }
    return dates;
  }
  
  
  /**
   * 获取输入日期的前一个月
   * @param currentDate 输入日期
   * @return 输入日期的前一个月的日期
   */
  public static Date getDateBeforeMonth(Date currentDate)
  {
    Calendar c = Calendar.getInstance();
    c.setTime(currentDate);
    c.add(Calendar.MONTH, -1);
    Date d = c.getTime();
    SimpleDateFormat sdf = new SimpleDateFormat(YYYYMMDD);
    return createDate(sdf.format(d),YYYYMMDD);
  }
  /**
   * 获取输入日期的前一天
   * @param currentDate 输入日期
   * @return 输入日期的前一天
   */
  public static Date getYesterday(Date currentDate)
  {
    Calendar c = Calendar.getInstance();
    c.setTime(currentDate);
    c.add(Calendar.DATE, -1);
    Date d = c.getTime();
    SimpleDateFormat sdf = new SimpleDateFormat(YYYYMMDD);
    return createDate(sdf.format(d),YYYYMMDD);
  }
  /**
   * 获取输入日期的后一天
   * @param currentDate 输入日期
   * @return 输入日期的前一天
   */
  public static Date getTomorrow(Date currentDate)
  {
    Calendar c = Calendar.getInstance();
    c.setTime(currentDate);
    c.add(Calendar.DATE, 1);
    Date d = c.getTime();
    SimpleDateFormat sdf = new SimpleDateFormat(YYYYMMDD);
    return createDate(sdf.format(d),YYYYMMDD);
  }
  /**
   * 获取输入日期的当月第一天
   * @param currentDate 输入日期
   * @return 输入日期的当月第一天
   */
  public static Date getFirstDayOfMonth(Date currentDate)
  {
    Calendar c = Calendar.getInstance();
    c.setTime(currentDate);
    c.set(Calendar.DATE, 1);
    Date d = c.getTime();
    SimpleDateFormat sdf = new SimpleDateFormat(YYYYMMDD);
    return createDate(sdf.format(d),YYYYMMDD);
  }
  /**
   * 将时间格式的字符串，转化为Date
   * @param dateFormat exp: 2010-7-22
   * @return java.util.Date
   */
  public static Date createDate(String dateString,String dateFormat)
  {
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    Date d = null;
    try {
      d = sdf.parse(dateString);
    } catch (ParseException e) {
      logger.error("字符串转化为Date失败,[string=" + dateString + "]", e);
    }
    return d;
  }
  /**
   * 将时间转化为指定格式的字符串
   * @author lishuai
   * @param date 输入日期
   * @param format 日期格式
   * @return 字符串格式的日期
   */
  public static String format(Date date,String format)
  {
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    return sdf.format(date);
  }
  public static Date formatToDate(Date date,String format)
  {
    SimpleDateFormat sdf = new SimpleDateFormat(format);
    return createDate(sdf.format(date),format);
  }

    public static Date createDate(String dateString,String[] dateFormats)
    {
        Date d = null;
        for(String format : dateFormats) {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            try {
                d = sdf.parse(dateString);
                if(null != d)
                    return d;
            } catch (ParseException e) {
                //logger.error("字符串转化为Date失败,[string=" + dateString + "]", e);
            }
        }
        return d;
    }
}
