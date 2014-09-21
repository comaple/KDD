package com.easyminning.extractor;

import cn.edu.hfut.dmic.webcollector.util.Log;
import com.easyminning.conf.ConfConstant;
import com.easyminning.conf.ConfLoader;
import com.easyminning.mongodbclient2.util.DateUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-9-20
 * Time: 上午10:50
 * To change this template use File | Settings | File Templates.
 */
public class DateFilter implements Filter {

    public static String []formats = {"yyyy年MM月dd日",
            "yyyy/MM/dd",
            "yyyy-MM-dd",
            "yyyy\\MM\\dd",
            "yyyy MM dd",
            "dd MMM yyyy",
            "E,dd MMM yyyy",
            "MMMddyyyy"};

    @Override
    public boolean filter(Article article) {
        if (article.publishDate == null || "".equals(article.publishDate)) {
            return false;
        }

        Date publishDate = DateUtil.createDate(article.publishDate, formats);
        if(null == publishDate){
            Log.Infos("extraterror","extrat failure,publishdate is unvalid string:" + article.url);
            return false;
        }

        int span = getUrlTimeSpan(article.url);
        Calendar now = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.DATE,-span);
        Date nowD = DateUtil.formatToDate(now.getTime(),"yyyy-MM-dd");
        Date endD = DateUtil.formatToDate(end.getTime(),"yyyy-MM-dd");

        //不在时间范围内的文章过滤掉
        if(nowD.compareTo(publishDate) < 0 || endD.compareTo(publishDate) > 0){
            Log.Infos("extraterror","extrat failure,publishdate is too long:" + article.url);
            return false;
        }
        article.publishDate = DateUtil.format(publishDate,"yyyy-MM-dd HH:mm:ss");
        return true;
    }

    //获取某个主题下的时间长度
    private static int getUrlTimeSpan(String url){
        int span = Integer.parseInt(ConfLoader.getProperty(ConfConstant.TIMESPAN, "30"));
        for (String topicRegx : ConfLoader.urlTimeSpanMap.keySet()){
            Pattern p = Pattern.compile(topicRegx);
            Matcher m = p.matcher(url);
            if(m.find()){
                span = ConfLoader.urlTimeSpanMap.get(topicRegx);
                break;
            }
        }
        return span;
    }
}
