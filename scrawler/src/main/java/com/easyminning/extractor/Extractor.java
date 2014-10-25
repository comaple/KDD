package com.easyminning.extractor;

import cn.edu.hfut.dmic.webcollector.model.Page;
import com.easyminning.conf.ConfLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jerry on 2014/8/30.
 */
public abstract class Extractor {

    private static Log log = LogFactory.getLog(Extractor.class);

    private static List<Filter> filterList = new ArrayList<Filter>(){{
        add(new ContentFilter());
        add(new DateFilter());
    }};

    private static Filter caseFilter = new CaseContentFilter();

    //页面html的前缀 对应的 抽取器
    public static HashMap<String,Extractor> pageExtrators = new HashMap<String,Extractor>();

    public static int ARTICLENUM = 0;

    public static HashSet<String> discardUrls = new HashSet<String>();
    public static Set<String> conDiscardUrls = Collections.synchronizedSet(discardUrls);

    // url前缀->错类类型:1->内容为null,2->时间为null,3->时间和内容都为null,Integer存放类型错误的次数
    public static Map<String,Map<String,Integer>> errorUrls = new HashMap<String,Map<String,Integer>>();

    public abstract Article extractArticle(Page page);

    public static Article extract(Page page){
        if(null == page || page.url == null || page.html == null){
            return null;
        }
        //判断是否是文章页，是文章页才需要获取正文
        String articleFlag = isArticlePage(page.url);
        if(articleFlag.equals("0")){
            return null;
        }

        if(ARTICLENUM == 0){
            pageExtrators.clear();//某个周期清空一次，可使得最新的模板及时得到更新
            errorUrls.clear();
        }

        Extractor extractor = null;
        String comPath = getUrlCommonPath(page.url);

        //判断是否有缓存抽取器
        if(pageExtrators.containsKey(comPath)){
            extractor = pageExtrators.get(comPath);
        }
        if(null == extractor){
            HashMap<String,String> templateReg = useTemplate(page.url);
            extractor = new TemplateExtractor(templateReg);
            pageExtrators.put(comPath,extractor);
        }

        Article article = null;
        if(null != extractor) {
            article = extractor.extractArticle(page);
        }

        //如果用模板抽取出的文章为空，那尝试使用统计方法抽取。可防止页面模板发生了变化而抽取不出内容
        Integer errorTime = 0;
        if(article == null || article.context == null ||
                article.context.equals("")|| article.publishDate == null){
            Map<String,Integer> typeErrorMap = errorUrls.get(comPath);
            if (typeErrorMap == null) {
                typeErrorMap = new HashMap<String,Integer>();
                errorUrls.put(comPath,typeErrorMap);
            }

            if (article == null || ((article.context == null || article.context.equals(""))&& article.publishDate == null)){
                errorTime = typeErrorMap.get("3");
                if (errorTime == null) {
                    typeErrorMap.put("3", 0);
                } else {
                    typeErrorMap.put("3", errorTime+1);
                }
            } else if (article.context == null || article.context.equals("")) {
                errorTime = typeErrorMap.get("1");
                if (errorTime == null) {
                    typeErrorMap.put("1", 0);
                } else {
                    typeErrorMap.put("1", errorTime+1);
                }
            } else if (article.publishDate == null) {
                errorTime = typeErrorMap.get("2");
                if (errorTime == null) {
                    typeErrorMap.put("2", 0);
                } else {
                    typeErrorMap.put("2", errorTime+1);
                }
            }
            if (log.isWarnEnabled()) {
                    int len = article.context.length() >= 30 ? 30 : article.context.length();
                    log.warn("模板解析错误：url:" + article.url + ",title:" + article.title +
                            ",publishDate:" + article.publishDate + ",content" + article.context.substring(0, len));
            }
            conDiscardUrls.add(page.url);
            return null;

        }


        boolean flag = true;
        for(Filter filter : filterList) {
            flag = filter.filter(article);
            if (!flag) {
                conDiscardUrls.add(page.url);
                if (log.isInfoEnabled()) {
                    int len = article.context.length() >= 30 ? 30 : article.context.length();
                    log.info("被过滤器拦截：url:" + article.url + ",title:" + article.title +
                            ",publishDate:" + article.publishDate + ",content" + article.context.substring(0,len));
                }
                return null;
            }
        }
        if(articleFlag.equals(Article.TYPE_CASE)){//如果案例过滤
            flag = caseFilter.filter(article);
            if (!flag) {
                if (log.isInfoEnabled()) {
                    int len = article.context.length() >= 30 ? 30 : article.context.length();
                    log.info("被案例拦截器过滤器拦截：url:" + article.url + ",title:" + article.title +
                            ",publishDate:" + article.publishDate + ",content" + article.context.substring(0,len));
                }
                conDiscardUrls.add(page.url);
                return null;
            }
        }

        article.type = articleFlag;//文章类型 1新闻资讯和其他 2案例，3问答
        ARTICLENUM++;
        FileWriter.getInstance().writeArticle(article);

        if (log.isDebugEnabled()) {
            int len = article.context.length() >= 30 ? 30 : article.context.length();
            log.debug("正常采集, url:" + article.url + ",title:" + article.title +
                    ",publishDate:" + article.publishDate + ",content" + article.context.substring(0,len));
        }
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

    //返回值 0:非主题页，1:新闻资讯文章，2:案例，3:既匹配新闻又匹配案例
    private static String isArticlePage(String url){
        String type = Article.TYPE_NO;
        Pattern p = null;
        Matcher m = null;
        for (String topicRegx : ConfLoader.urlTypeMap.keySet()){
            p = Pattern.compile(topicRegx);
            m = p.matcher(url);
            if(m.find()){
                type = ConfLoader.urlTypeMap.get(topicRegx);
                break;
            }
        }
        return type;
    }
}
