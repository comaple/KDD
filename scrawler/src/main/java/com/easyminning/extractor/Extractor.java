package com.easyminning.extractor;

import cn.edu.hfut.dmic.webcollector.model.Page;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jerry on 2014/8/30.
 */
public abstract class Extractor {
    //页面html的前缀 对应的 抽取器
    public static HashMap<String,Extractor> pageExtrators = new HashMap<String,Extractor>();

    public abstract Article extractArticle(Page page);

    public static Article extract(Page page){
        if(null == page || page.url == null || page.html == null){
            return null;
        }
        //判断是否是文章页，是文章页才需要获取正文
        if(!isArticlePage(page.html)){
            return null;
        }

        Extractor extractor = null;
        String comPath = getUrlCommonPath(page.url);
        //判断是否有缓存抽取器
        if(pageExtrators.containsKey(comPath)){//
            extractor = pageExtrators.get(comPath);
        }
        if(null == extractor){
            //判断page是否有对应的模式可以使用
            boolean isUseTemplate = false;
            if(!isUseTemplate) {
                extractor = new StatisticsExtractor();
            }else{
                extractor = new TemplateExtractor();
            }
            pageExtrators.put(comPath,extractor);//
        }

        Article article = null;
        if(null != extractor) {
            article = extractor.extractArticle(page);
        }
        System.out.println("----------------华丽分割线-----------------");
        System.out.println(article.title);
        System.out.println("----------------华丽分割线-----------------");
        System.out.println(article.publishDate);
        System.out.println("----------------华丽分割线-----------------");
        System.out.println(article.context);
        return article;
    }

    public static String getUrlCommonPath(String url){
        String comPath = "";
        comPath = url.substring(0,url.lastIndexOf('/'));
        return comPath;
    }

    public static boolean isArticlePage(String html){
        boolean isArticle = true;
        int dateCount = 0;
        int unCompleteDateCount = 0;
        int h1Count = 0;
        Pattern p = Pattern.compile(
                "((\\d{4}|\\d{2})(\\\\|\\-|\\/)\\d{1,2}\\3\\d{1,2})(\\s?\\d{2}:\\d{2})?|(\\d{2,4}年\\d{1,2}月\\d{1,2}日)(\\s?\\d{2}:\\d{2})?",
                Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(html);
        while(m.find()){
            dateCount++;
        }

        //匹配不完全的时间，往往导航页的链接后方居多
        p = Pattern.compile(
                "(\\d{2,4})?(\\\\|\\-|\\/)?(\\d{1,2})(\\\\|\\-|\\/)\\d{1,2}(\\s?\\d{2}:\\d{2})?|(\\d{2,4}?\\d{1,2}月\\d{1,2}日)(\\s?\\d{2}:\\d{2})?",
                Pattern.CASE_INSENSITIVE);
        m = p.matcher(html);
        while(m.find()){
            unCompleteDateCount++;
        }

        p = Pattern.compile("<h1.*?>.*?</h1>",Pattern.CASE_INSENSITIVE);
        m = p.matcher(html);
        while (m.find()){
            h1Count++;
        }

        /*if(h1Count > 0 && (dateCount >= 1 && dateCount <= 5)){
            isArticle = true;
        }else*/

        if(h1Count == 0 && dateCount == 0){
            isArticle = false;
        }else if(dateCount > 15 || unCompleteDateCount > 15){
            isArticle = false;
        }
        return isArticle;
    }
}
