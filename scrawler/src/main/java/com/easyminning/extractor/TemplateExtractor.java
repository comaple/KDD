package com.easyminning.extractor;

import cn.edu.hfut.dmic.webcollector.model.Page;
import com.easyminning.conf.ConfConstant;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jerry on 2014/8/30.
 */
public class TemplateExtractor extends Extractor {
    private HashMap<String,String> templateRex;

    private final static String [][] filters = {
            {"(?is)<!DOCTYPE.*?>", ""},
            {"(?is)<script.*?>.*?</script>", ""},
            {"(?is)<style.*?>.*?</style>", ""},
            {"(?is)<!--.*?-->", ""},
            {"&.{2,5};|&#.{2,5};", ""},
            {"&nbsp;", " "}
    };

    public TemplateExtractor(HashMap<String,String> templateRex){
        this.templateRex = templateRex;
    }


    @Override
    public Article extractArticle(Page page) {
        if(null == page){
            return null;
        }
        String html = page.html;
        //过滤样式，脚本等不相干标签
        for(String [] filter : filters){
            html = html.replaceAll(filter[0],filter[1]);
        }

        Article article = new Article();
        article.url = page.url;
        //匹配作者
        Pattern p = Pattern.compile(templateRex.get(ConfConstant.AUTHOR));
        Matcher m = p.matcher(html);
        if(m.find()){
            String authorHtml = m.group();
            article.author = authorHtml.replaceAll("<.*?>","").trim();
        }
        //发布时间
        p = Pattern.compile(templateRex.get(ConfConstant.PUBLISHDATE));
        m = p.matcher(html);
        if(m.find()){
            String dateHtml = m.group();
            article.publishDate = dateHtml.replaceAll("<.*?>","").trim();
        }
        //标题
        p = Pattern.compile(templateRex.get(ConfConstant.TITLE));
        m = p.matcher(html);
        if(m.find()){
            String titleHtml = m.group();
            article.title = titleHtml.replaceAll("<.*?>","").replaceAll("\\s*\n\\s*"," ").replaceAll("\\s*\r\\s*"," ").trim();
        }
        //正文
        p = Pattern.compile(templateRex.get(ConfConstant.MAINCONTENT));
        m = p.matcher(html);
        if(m.find()){
            article.contextWithTag = m.group().replaceAll("\\s*\n\\s*"," ").replaceAll("\\s*\r\\s*"," ").trim();
            //article.context = article.contextWithTag.replaceAll("<.*?>","").trim();
            article.context = article.contextWithTag.replaceAll("<.*?>","").replaceAll("\\s*\n\\s*"," ").replaceAll("\\s*\r\\s*"," ").trim();
        }
        return article;
    }
}
