/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.edu.hfut.webcollector.generator;

import cn.edu.hfut.dmic.webcollector.generator.Injector;
import cn.edu.hfut.dmic.webcollector.generator.StandardGenerator;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.Page;
import java.io.IOException;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.easyminning.conf.ConfConstant;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import weixincrawler.crawler.SeedsGenerator;

/**
 *
 * @author hu
 */

public class TestStandardGenerator {
    @Test
    public void testGenerator() throws IOException{
        /*String crawl_path="/home/hu/data/webcollector_test";
        ArrayList<String> seeds=new ArrayList<String>();
        seeds.add("http://www.sina.com.cn/");
        seeds.add("http://www.xinhuanet.com/");
        
        Injector injector=new Injector(crawl_path);
        injector.inject(seeds);
       
        StandardGenerator generator=new StandardGenerator(crawl_path);
        CrawlDatum crawldatum=null;
        ArrayList<CrawlDatum> datums=new ArrayList<CrawlDatum>();
        while((crawldatum=generator.next())!=null){
            datums.add(crawldatum);
        }
        
        Assert.assertEquals(seeds.size(),datums.size());
        for(int i=0;i<seeds.size();i++){           
            Assert.assertEquals(-1, datums.get(i).fetchtime);
            Assert.assertEquals(Page.UNFETCHED, datums.get(i).status);
            Assert.assertEquals(seeds.get(i), datums.get(i).url);
        }*/

        String s = "complete reference.        \n" +
                "\n       " +
                "To follow along with this guide";
        System.out.println(s.replaceAll("\\s*\n\\s*",""));

        if(Pattern.matches("http://news.liuxue360.com/", "http://news.liuxue360.com/")){
            System.out.println("affafaef");
        }

        String ss = "2012-9";//
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.ENGLISH);
        try {
            System.out.println(sdf.parse(ss));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //发布时间
        String html = "title=\"2014-9-16 19:10:17\">   \"2014-9-13 21:06:27\">3&nbsp";
        Pattern p = Pattern.compile("(?is)20\\d{2}-\\d{1,2}-\\d{1,2}\\s+\\d{1,2}:\\d{1,2}:\\d{1,2}");
        Matcher m = p.matcher(html);
        if(m.find()){
            System.out.println(m.group());
        }
    }
    
    @Test
    public void test1() throws Exception{//http://www.chuchuguo.com/scholarship/anli/show/37038/

        HttpClient hc = new DefaultHttpClient();
        HttpGet httpget = new HttpGet("http://edu.dzwww.com/lxym/lxzx/201410/t20141021_11217943.html");
        // 设置参数
        String str = EntityUtils.toString(new UrlEncodedFormEntity(new ArrayList<NameValuePair>(), "utf-8"));
        httpget.setURI(new URI(httpget.getURI().toString() + "?" + str));
        // 发送请求
        HttpResponse httpresponse = hc.execute(httpget);
        // 获取返回数据
        HttpEntity entity = httpresponse.getEntity();
        String body = EntityUtils.toString(entity,"gbk");
        System.out.println(body);

        /*Pattern p = Pattern.compile("(?is)20\\d{2}-\\d{2}-\\d{2}");
        Matcher m = p.matcher(body);
        if(m.find()){
            String dateHtml = m.group();
            System.out.println(dateHtml.replaceAll("<.*?>","").replaceAll("\\s*\n\\s*"," ").replaceAll("\\s*\r\\s*"," ").trim());
        }*/
    }

    @Test
    public void test2(){
        long ms = 60010000;
        long a = ms/3600000;
        System.out.println(ms/3600000+"时"+(ms-a*3600000)/60000+"分");
        SimpleDateFormat df2 = new SimpleDateFormat("dd-HH:mm:ss");
        df2.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(ms);
        //System.out.println(df2.format(calendar.getTime()));
        System.out.println(df2.format(calendar.getTime()));
        System.out.println(df2.format(ms));
    }
}
