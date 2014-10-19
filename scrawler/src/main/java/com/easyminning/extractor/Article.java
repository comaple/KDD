package com.easyminning.extractor;

/**
 * Created by jerry on 2014/8/30.
 */
public class Article {

    public static String TYPE_NO = "0";
    public static String TYPE_NEWS = "1";
    public static String TYPE_CASE = "2";
    public static String TYPE_QUESTION = "3";

    public String title;
    public String context;
    public String contextWithTag;
    public String publishDate;
    public String url;
    public String author;
    public String type; // 文章类型,1,新闻，其它，2,案例，3,问题
    public String scrawDate; // 抓取时间


    public static void main(String[] args) {
        System.out.println("ÔÚÏ¤ÄáµÄÖÐ¹úÁôÑ§Éú ¿ÉÒÔÔÚ°ÄÖÞ¾³ÄÚÇ©Ö¤È¥ÆäËû¹ú¼ÒÂÃÓÎÂð£¿£¨ÀýÈç º«¹ú ÈÕ±¾ ÃÀ¹úµÈ£©Ð»Ð»£¡".length());
    }
}
