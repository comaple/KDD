package com.easyminning.util.date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-9-28
 * Time: 下午8:51
 * To change this template use File | Settings | File Templates.
 */
public class DateUtil {

    /**
     * 获取当前时间友好时间格式
     * @return
     */
    public static String getCurrentFriendlyTime() {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return df.format(date);
    }

}
