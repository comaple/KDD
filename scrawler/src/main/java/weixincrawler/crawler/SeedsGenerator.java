package weixincrawler.crawler;

import cn.edu.hfut.dmic.webcollector.model.Page;
import com.easyminning.conf.ConfConstant;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import weixincrawler.conf.WeixinConfConstant;
import weixincrawler.conf.WeixinConfLoader;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by leilongyan on 2014/9/9.
 */
public class SeedsGenerator {
    private static final String url = "http://weixin.sogou.com/weixin";
    private static HttpClient hc = new DefaultHttpClient();

    public static List<String> generatorSeedsFromWords(){
        List<String> seeds = new ArrayList<String>();
        String wordStr = WeixinConfLoader.getProperty(WeixinConfConstant.SEEDWORDS,"");
        if(!wordStr.equals("")){
            String []words = wordStr.split(ConfConstant.ObjectSplit);
            for(String word : words){
                seeds.addAll(generatorSeedsFromWord(word));
            }
        }
        return seeds;
    }

    public static List<String> generatorSeedsFromWord(String word){
        List<String> seeds = new ArrayList<String>();

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("query", word));//URLEncoder.encode("留学", "UTF-8")
        params.add(new BasicNameValuePair("_asf", "www.sogou.com"));
        params.add(new BasicNameValuePair("w", "01019900"));
        params.add(new BasicNameValuePair("p", "40040100"));
        params.add(new BasicNameValuePair("ie", "utf8"));
        params.add(new BasicNameValuePair("type", "2"));
        int d = Math.round(new Date().getTime()/1000);
        params.add(new BasicNameValuePair("_ast", String.valueOf(d)));
        //sut=773&sst0=1410187149660&lkt=0%2C0%2C0
        //params.add(new BasicNameValuePair("sut", "3936"));
        params.add(new BasicNameValuePair("sst0", String.valueOf(new Date().getTime())));
        params.add(new BasicNameValuePair("lkt", "0,0,0"));

        String body = get(url, params);
        String pageUrl = getPageUrl(body);
        int pageNum = Integer.parseInt(WeixinConfLoader.getProperty(WeixinConfConstant.PAGENUM,"4"));
        String replacedStr = WeixinConfLoader.getProperty(WeixinConfConstant.SEEDREGEX,"href=\"\\?query=.*?type=2.*?page=2.*?\">");
        int tmpIndex = replacedStr.indexOf("page");
        replacedStr = replacedStr.substring(tmpIndex,tmpIndex + 6);
        for(int index = 1;index <= pageNum; index++){
            seeds.add(pageUrl.replace(replacedStr,"page="+index));
        }
        return seeds;
    }

    public static String getPageUrl(String body){
        String pagerUrl = null;
        String seedRegex = WeixinConfLoader.getProperty(WeixinConfConstant.SEEDREGEX,"href=\"\\?query=.*?type=2.*?page=2.*?\">");
        Pattern p = Pattern.compile(seedRegex);
        Matcher m = p.matcher(body);
        if(m.find()){
            String urlStr = m.group();
            pagerUrl = urlStr.replace("href=\"","").replace("\">","");
        }
        return url + pagerUrl;
    }

    public static String get(String url, List<NameValuePair> params) {
        String body = null;
        //byte [] body = null;
        try {
            // Get请求
            HttpGet httpget = new HttpGet(url);
            httpget.setHeader("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
            httpget.setHeader("Accept",
                    "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
            httpget.setHeader("Accept-Language",
                    "zh-CN,zh;q=0.8,en-GB;q=0.6,en;q=0.4,zh-TW;q=0.2");
            httpget.setHeader("Referer",
                    "http://weixin.sogou.com/");
            httpget.setHeader("Host",
                    "weixin.sogou.com");
            // 设置参数
            String str = EntityUtils.toString(new UrlEncodedFormEntity(params, "utf-8"));
            httpget.setURI(new URI(httpget.getURI().toString() + "?" + str));
            // 发送请求
            HttpResponse httpresponse = hc.execute(httpget);
            // 获取返回数据
            HttpEntity entity = httpresponse.getEntity();
            body = EntityUtils.toString(entity);
            //body = EntityUtils.toByteArray(entity);
            if (entity != null) {
                entity.consumeContent();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return body;
    }

    public static String post(String url, List<NameValuePair> params) {
        String body = null;
        try {
            // Post请求
            HttpPost httppost = new HttpPost(url);
            // 设置参数
            httppost.setEntity(new UrlEncodedFormEntity(params,"utf-8"));
            // 发送请求
            HttpResponse httpresponse = hc.execute(httppost);
            // 获取返回数据
            HttpEntity entity = httpresponse.getEntity();
            body = EntityUtils.toString(entity);
            if (entity != null) {
                entity.consumeContent();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return body;
    }

    public static void main(String []args){
        generatorSeedsFromWords();
    }
}
