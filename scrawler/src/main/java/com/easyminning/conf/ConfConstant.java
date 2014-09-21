package com.easyminning.conf;

/**
 * Created by jerry on 2014/8/31.
 */
public class ConfConstant {
    public static final String ObjectSplit = ";;;"; //配置文件中一个对象的分隔符
    public static final String TemplateSplit = "%%%"; //模板配置中正则和模板文件的分隔符
    public static final String CommentFlag = "#";//dat文件的注释的标识


    public static final String CRAWLDBPATH = "crawldbpath";
    public static final String DOWNLOADPATH = "downloadpath";
    public static final String RESUMABLE = "resumable";


    //从这以下的参数都是支持配置文件动态更新的
    public static final String SEEDS = "seeds";
    public static final String DEPTH = "depth";
    public static final String THREADS = "threads";
    public static final String TIMESPAN = "timespan";
    public static final String MAXARTICLENUM = "maxarticlenum";
    public static final String REPEATABLEREGEX = "repeatableregex";
    public static final String POSITIVEREGEX = "positiveregex";
    public static final String NEGATIVEREGEX = "negativeregex";
    public static final String TOPICREGEX = "topicregex";
    public static final String CASETOPICREGEX = "casetopicregex";
    public static final String TEMPLATES = "templates";
    public static final String TOPN = "topN";
    public static final String INTERVAL = "interval";

    public static final String TITLE = "title";
    public static final String PUBLISHDATE = "publishDate";
    public static final String AUTHOR = "author";
    public static final String MAINCONTENT = "mainContent";


}
