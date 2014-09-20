package com.easyminning.extractor;

import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.util.Log;
import com.easyminning.conf.ConfConstant;
import com.easyminning.conf.ConfLoader;
import com.easyminning.mongodbclient2.util.DateUtil;
import org.apache.commons.lang.time.DateUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jerry on 2014/8/30.
 */
public abstract class Extractor {

    private static List<Filter> filterList = new ArrayList<Filter>(){{
        add(new ContentFilter());
        add(new DateFilter());
    }};

    //页面html的前缀 对应的 抽取器
    public static HashMap<String,Extractor> pageExtrators = new HashMap<String,Extractor>();

    public static int ARTICLENUM = 0;

    public static HashSet<String> discardUrls = new HashSet<String>();
    private static Set<String> conDiscardUrls = Collections.synchronizedSet(discardUrls);

    public abstract Article extractArticle(Page page);

    public static Article extract(Page page){
        if(null == page || page.url == null || page.html == null){
            return null;
        }
        //判断是否是文章页，是文章页才需要获取正文
        if(!isArticlePage(page.url)){
            return null;
        }

        if(ARTICLENUM == 0){
            pageExtrators.clear();//某个周期清空一次，可使得最新的模板及时得到更新
        }

        Extractor extractor = null;
        String comPath = getUrlCommonPath(page.url);
        //判断是否有缓存抽取器
        if(pageExtrators.containsKey(comPath)){
            extractor = pageExtrators.get(comPath);
        }
        if(null == extractor){
            //判断page解析是否有对应的模式可以使用
            HashMap<String,String> templateReg = useTemplate(page.url);
            if(null == templateReg || templateReg.size() <= 0) {
                //System.out.println("###############StatisticsExtractor###############");
                extractor = new StatisticsExtractor();
            }else{
                //System.out.println("###############TemplateExtractor###############");
                extractor = new TemplateExtractor(templateReg);
            }
            pageExtrators.put(comPath,extractor);
        }
        Log.Infos("info","extrat url:" + page.url);

        Article article = null;
        if(null != extractor) {
            article = extractor.extractArticle(page);
        }
        //如果用模板抽取出的文章为空，那尝试使用统计方法抽取。可防止页面模板发生了变化而抽取不出内容
        if((article == null || (article.context == null || article.context.equals(""))
                || article.publishDate == null) &&
                extractor instanceof TemplateExtractor){
            extractor = new StatisticsExtractor();
            article = extractor.extractArticle(page);
            if(article.context != null && !article.context.equals("")) {
                pageExtrators.put(comPath, extractor);
            }
        }

        boolean flag = true;
        for(Filter filter : filterList) {
            flag = filter.filter(article);
            if (!flag) {
                //Log.Infos("extraterror", "extrat failure :" + page.url);
                conDiscardUrls.add(page.url);
                return null;
            }
        }

        ARTICLENUM++;
        FileWriter.getInstance().writeArticle(article);
        Log.Infos("info","article url:" + article.url);
        Log.Infos("info","article title:" + article.title);
        Log.Infos("info","article publishdate:" + article.publishDate);
        Log.Infos("info","article part content:" + article.context.substring(0,30) + "...");
        return article;
    }

    public static String getUrlCommonPath(String url){
        String comPath = url;
        comPath = url.substring(0,url.lastIndexOf('/'));
        return comPath;
    }

    //使用最长的正则匹配，如果某域名有个模板匹配，当需要为该域名下某些网页定制模板时
    //可以直接配置该子模板，虽然该url能匹配域名模板和子模板，但是它会选择最长正则匹配
    public static HashMap<String,String> useTemplate(String url){
        HashMap<String,String> templateReg = null;
        HashMap<String,HashMap<String,String>> templateMap = ConfLoader.templateMap;
        int maxMatchLen = 0;
        for (Map.Entry<String,HashMap<String,String>> te : templateMap.entrySet()){
            Pattern p = Pattern.compile(te.getKey());
            Matcher m = p.matcher(url);
            if(m.find()){
                int len = m.group().length();
                if(maxMatchLen < len){ //url使用最长的正则匹配
                    maxMatchLen = len;
                    templateReg = te.getValue();
                }
            }
        }
        return templateReg;
    }

    public static boolean isArticlePage(String url){
        boolean isArticle = false;
        for (String topicRegx : ConfLoader.topicRegexSet){
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
