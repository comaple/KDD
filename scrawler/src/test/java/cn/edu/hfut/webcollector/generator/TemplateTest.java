package cn.edu.hfut.webcollector.generator;

import cn.edu.hfut.dmic.webcollector.model.Page;
import com.easyminning.conf.ConfConstant;
import com.easyminning.conf.ConfLoader;
import com.easyminning.extractor.Article;
import com.easyminning.extractor.TemplateExtractor;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2014/10/21.
 */
public class TemplateTest {

    @Test
    public void testTemplate() {
        HashMap<String,String> regexMap = new HashMap<String, String>();
        regexMap.put(ConfConstant.AUTHOR, "");
        regexMap.put(ConfConstant.PUBLISHDATE, "(?is)20\\d{2}-\\d{2}-\\d{2}");
        regexMap.put(ConfConstant.TITLE, "(?is)<title>.*?</title>");
        regexMap.put(ConfConstant.MAINCONTENT,"(?is)</ul>\\s*<p>.*?</p>\\s*<p class=\"added_question\">");

        TemplateExtractor templateExtractor = new TemplateExtractor(regexMap);

        String testUrl = "http://www.liuxue360.com/faq/question-00334304.html";
        Page page = new Page();
        page.url = testUrl;
        page.html = getUrlContent(testUrl);


        Article article = templateExtractor.extractArticle(page);

        System.out.println("author:" + article.author + ",publishdate:" + article.publishDate+",title:" + article.title + "article.context:" + article.context);
    }


    @Test
    public void testTemplate2() throws Exception {
        HashMap<String,String> regexMap = new HashMap<String, String>();
        String path = "/Volumes/work/KDD/KDD2/scrawler/src/main/resources/template/question/";
        File file = new File(path,"faqen.template");
        String testUrl ="http://faq.en.com.cn/question-00216156.html";

        List<String> list = FileUtils.readLines(file);

        for (String str : list) {
            int index = str.indexOf('=');
            regexMap.put(str.substring(0,index).trim(),str.substring(index + 1).trim());
        }
;
        TemplateExtractor templateExtractor = new TemplateExtractor(regexMap);

        Page page = new Page();
        page.url = testUrl;
        page.html = getUrlContent(testUrl);


        Article article = templateExtractor.extractArticle(page);

        System.out.println("author:" + article.author + ",publishdate:" + article.publishDate+",title:"
                + article.title + "context:" + article.context + ",tagContext:" + article.contextWithTag);
    }

    private String getUrlContent(String url) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,  120000);//连接时间120s
       // httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 120000);//数据传输时间120s
        HttpGet httpGet = new HttpGet(url);
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String body = EntityUtils.toString(entity, "GBK");
            return body;
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return "";
    }

}
