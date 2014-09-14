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
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

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

        String ss = "14年6月23日 23:34:4";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日", Locale.ENGLISH);
        try {
            System.out.println(sdf.parse(ss));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
    
   
}
