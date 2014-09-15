package weixincrawler.crawler;

import cn.edu.hfut.dmic.webcollector.model.Page;
import com.easyminning.conf.ConfConstant;
import com.easyminning.conf.ConfLoader;
import com.easyminning.extractor.*;
import com.easyminning.mongodbclient2.util.DateUtil;
import weixincrawler.conf.WeixinConfLoader;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by leilongyan on 2014/9/9.
 */
public abstract class WeixinExtractor extends Extractor{
    private static Extractor extractor;
    private static int articleNum = 0;

    static {
        initExtrator();
    }

    public static void initExtrator(){
        HashMap<String,String> templateReg = getTemplates();
        extractor = new TemplateExtractor(templateReg);
    }

    public static Article extract(Page page){
        if(null == page || page.url == null || page.html == null){
            return null;
        }
        //判断是否是文章页，是文章页才需要获取正文
        if(!isArticlePage(page.url)){
            return null;
        }

        if(articleNum % 500 == 0 || extractor == null){
            initExtrator();//及时重新初始化，可使得更新的配置及时得到更新
        }

        Article article = null;
        if(null != extractor) {
            article = extractor.extractArticle(page);
        }
        //如果用模板抽取出的文章为空，那尝试使用统计方法抽取。可防止页面模板发生了变化而抽取不出内容
        if((article == null || (article.context == null || article.context.equals(""))
                || article.publishDate == null) &&
                extractor instanceof TemplateExtractor){
            System.out.println("###############StatisticsExtractor###############");
            extractor = new StatisticsExtractor();
            article = extractor.extractArticle(page);
            extractor = null;
        }
        if(article == null || article.publishDate == null || article.context == null){
            return null;
        }
        if(article.context != null && !article.context.equals("")){
            articleNum++;
            FileWriter.getInstance().writeArticle(article);
        }

        System.out.println("----------------标题-----------------");
        System.out.println(article.title);
        System.out.println("----------------发布时间-----------------");
        System.out.println(article.publishDate);
        System.out.println("----------------内容-----------------");
        System.out.println(article.context);
        return article;
    }

    public static boolean isArticlePage(String url){
        boolean isArticle = false;
        String topicReg = WeixinConfLoader.getProperty(ConfConstant.TOPICREGEX,"http://mp.weixin.qq.com/mp/appmsg/show\\?.*?");
        if(Pattern.matches(topicReg,url)){
            isArticle = true;
        }
        return isArticle;
    }

    public static HashMap<String,String> getTemplates(){
        HashMap<String,String> templates = new HashMap<String, String>();
        templates.put(ConfConstant.TITLE,WeixinConfLoader.getProperty(ConfConstant.TITLE,""));
        templates.put(ConfConstant.PUBLISHDATE,WeixinConfLoader.getProperty(ConfConstant.PUBLISHDATE,""));
        templates.put(ConfConstant.AUTHOR,WeixinConfLoader.getProperty(ConfConstant.AUTHOR,""));
        templates.put(ConfConstant.MAINCONTENT,WeixinConfLoader.getProperty(ConfConstant.MAINCONTENT,""));
        return templates;
    }
}
