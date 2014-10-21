package com.easyminning.extractor;

import cn.edu.hfut.dmic.webcollector.model.Page;
import com.easyminning.conf.ConfConstant;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jerry on 2014/8/30.
 */
public class TemplateExtractor extends Extractor {
    private HashMap<String,String> templateRex;

    private static String SEPERATOR = "%%%";

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
        String author = templateRex.get(ConfConstant.AUTHOR);
        int authorGroup = 0;
        String authorRegx = author;
        if (author.contains(SEPERATOR)) {
            authorRegx = author.split(SEPERATOR)[0];
            authorGroup = Integer.parseInt(author.split(SEPERATOR)[1]);
        }
        Pattern p = Pattern.compile(authorRegx);
        Matcher m = p.matcher(html);
        if(m.find()){
            String authorHtml = m.group(authorGroup);
            article.author = authorHtml.replaceAll("<.*?>","").replaceAll("\\s*\n\\s*"," ").replaceAll("\\s*\r\\s*"," ").trim();
        }
        //发布时间
        String publishTime = templateRex.get(ConfConstant.PUBLISHDATE);
        int publishTimeGroup = 0;
        String publishTimeRegx = publishTime;
        if (publishTime.contains(SEPERATOR)) {
            publishTimeRegx = publishTime.split(SEPERATOR)[0];
            publishTimeGroup = Integer.parseInt(publishTime.split(SEPERATOR)[1]);
        }
        p = Pattern.compile(publishTimeRegx);
        m = p.matcher(html);
        if(m.find()){
            String dateHtml = m.group(publishTimeGroup);
            article.publishDate = dateHtml.replaceAll("<.*?>","").replaceAll("\\s*\n\\s*"," ").replaceAll("\\s*\r\\s*"," ").trim();
        }
        //标题
        String title = templateRex.get(ConfConstant.TITLE);
        int titleGroup = 0;
        String titleRegx = title;
        if (title.contains(SEPERATOR)) {
            titleRegx = title.split(SEPERATOR)[0];
            titleGroup = Integer.parseInt(title.split(SEPERATOR)[1]);
        }
        p = Pattern.compile(titleRegx);
        m = p.matcher(html);
        if(m.find()){
            String titleHtml = m.group(titleGroup);
            article.title = titleHtml.replaceAll("<.*?>","").replaceAll("\\s*\n\\s*"," ").replaceAll("\\s*\r\\s*"," ").trim();
        }
        //正文
        String content = templateRex.get(ConfConstant.MAINCONTENT);
        int contentGroup = 0;
        String contentRegx = content;
        if (content.contains(SEPERATOR)) {
            contentRegx = content.split(SEPERATOR)[0];
            contentGroup = Integer.parseInt(content.split(SEPERATOR)[1]);
        }
        p = Pattern.compile(contentRegx);
        m = p.matcher(html);
        if(m.find()){
            article.contextWithTag = m.group(contentGroup).replaceAll("\\s*\n\\s*"," ").replaceAll("\\s*\r\\s*"," ").trim();
            //article.context = article.contextWithTag.replaceAll("<.*?>","").trim();
            article.context = article.contextWithTag.replaceAll("<.*?>","").replaceAll("\\s*\n\\s*"," ").replaceAll("\\s*\r\\s*"," ").trim();
        }
        return article;
    }



}
