package com.easyminning.extractor;

import cn.edu.hfut.dmic.webcollector.model.Page;
import com.easyminning.conf.ConfLoader;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
        if(!isArticlePage(page.url)){
            return null;
        }

        Extractor extractor = null;
        String comPath = getUrlCommonPath(page.url);
        //判断是否有缓存抽取器
        if(pageExtrators.containsKey(comPath)){
            extractor = pageExtrators.get(comPath);
        }
        if(null == extractor){
            //判断page解析是否有对应的模式可以使用
            String templateReg = isUseTemplate(page.url);
            if(null == templateReg || templateReg.equals("")) {
                extractor = new StatisticsExtractor();
            }else{
                extractor = new TemplateExtractor(templateReg);
            }
            pageExtrators.put(comPath,extractor);
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
        String comPath = url;
        comPath = url.substring(0,url.lastIndexOf('/'));
        return comPath;
    }

    public static String isUseTemplate(String url){
        String templateReg = null;
        /*HashMap<String,String> templateMap = ConfLoader.templateMap;
        for (Map.Entry<String,String> te : templateMap.entrySet()){
            Pattern p = Pattern.compile(te.getKey());
            Matcher m = p.matcher(url);
            if(m.find()){
                templateReg = te.getValue();
                break;
            }
        }*/
        return templateReg;
    }

    public static boolean isArticlePage(String url){
        boolean isArticle = false;
        HashSet<String> topicRegexSet = ConfLoader.topicRegexSet;
        for (String topicRegx : topicRegexSet){
            Pattern p = Pattern.compile(topicRegx);
            Matcher m = p.matcher(url);
            if(m.find()){
                isArticle = true;
                break;
            }
        }
        return isArticle;
    }
}
