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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.easyminning.conf.ConfConstant;
import org.junit.Assert;
import org.junit.Test;

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

        String ss = "Sep152014";//
                SimpleDateFormat sdf = new SimpleDateFormat("MMMddyyyy", Locale.ENGLISH);
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

        HashSet<String> discardUrls = new HashSet<String>();
        Set<String> conDiscardUrls = Collections.synchronizedSet(discardUrls);
        conDiscardUrls.add("a");
        conDiscardUrls.add("b");
        conDiscardUrls.add("c");
        System.out.println(discardUrls);
    }
    
    
   
}
