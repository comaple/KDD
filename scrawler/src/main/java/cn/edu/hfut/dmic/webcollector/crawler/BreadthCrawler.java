/*
 * Copyright (C) 2014 hu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package cn.edu.hfut.dmic.webcollector.crawler;

import cn.edu.hfut.dmic.webcollector.fetcher.Fetcher;

import cn.edu.hfut.dmic.webcollector.generator.Generator;
import cn.edu.hfut.dmic.webcollector.generator.Injector;
import cn.edu.hfut.dmic.webcollector.generator.StandardGenerator;
import cn.edu.hfut.dmic.webcollector.generator.filter.IntervalFilter;
import cn.edu.hfut.dmic.webcollector.generator.filter.URLRegexFilter;
import cn.edu.hfut.dmic.webcollector.generator.filter.UniqueFilter;
import cn.edu.hfut.dmic.webcollector.handler.Handler;
import cn.edu.hfut.dmic.webcollector.handler.Message;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.output.FileSystemOutput;
import cn.edu.hfut.dmic.webcollector.util.Config;
import cn.edu.hfut.dmic.webcollector.util.ConnectionConfig;
import cn.edu.hfut.dmic.webcollector.util.Log;
import cn.edu.hfut.dmic.webcollector.util.RandomUtils;
import com.bson.types.Symbol;
import com.easyminning.conf.ConfConstant;
import com.easyminning.conf.ConfLoader;
import com.easyminning.extractor.Extractor;
import com.easyminning.tag.LogRecord;
import com.easyminning.tag.LogRecordService;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;


/**
 *The web crawler that executes a breadth-first crawling.
 * 
 * @author hu
 */
public class BreadthCrawler {
    
    /**
     *
     */
    public BreadthCrawler(){
        taskname=RandomUtils.getTimeString();
    }

    protected String taskname;
    protected String crawl_path = "crawl";
    private String root = "data";
    private String cookie = null;
    private String useragent = "Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:26.0) Gecko/20100101 Firefox/26.0";

    private int threads=10;
    protected boolean resumable;
    protected Fetcher fetcher;

    protected ArrayList<String> regexs = new ArrayList<String>();
    protected ArrayList<String> seeds = new ArrayList<String>();

    public void addSeed(String seed) {
        seeds.add(seed);
    }

    public void addRegex(String regex) {
        regexs.add(regex);
    }
    /*
    public void autoRegex() {
        for (String seed : seeds) {
            try {
                URL _URL = new URL(seed);
                String host = _URL.getHost();
                if (host.startsWith("www.")) {
                    host = host.substring(4);
                    host = ".*" + host;
                }
                String autoregex = _URL.getProtocol() + "://" + host + ".*";

                regexs.add(autoregex);
                System.out.println("autoregex:" + autoregex);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    */
    public ConnectionConfig conconfig = null;

    public void configCon(HttpURLConnection con) {
        con.setRequestProperty("User-Agent", useragent);
        if (cookie != null) {
            con.setRequestProperty("Cookie", cookie);
        }

    }

    protected void visit(Page page) {
        FileSystemOutput fsoutput = new FileSystemOutput(root);
        Log.Infos("visit",this.taskname,page.url);
        fsoutput.output(page);
    }
    
    protected void failed(Page page){
       
    }
    

    public final static int RUNNING=1;
    public final static int STOPED=2;
    public int status;

    /**
     * start the crawler
     * @param depth depth in bread-first search
     * @throws IOException
     */
    public void start(int depth) throws IOException {
        if (!resumable) {
            if (seeds.isEmpty()) {
                Log.Infos("error:"+"Please add at least one seed");
                return;
            }
           
        }
        //if (regexs.isEmpty()) {
        if(ConfLoader.positiveRegexSet.isEmpty()){//leilongyan修改 为了支持修改配置文件自动加载功能
                Log.Infos("error:"+"Please add at least one regex rule");
                return;
        }
        inject();
        status=RUNNING;
        //leilongyan修改 可以循环无限运行
        int count = 0;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //SimpleDateFormat df2 = new SimpleDateFormat("HH:mm:ss");
        //df2.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        while(true){
            count++;
            long startTime = System.currentTimeMillis();
            Log.Infos("info","Number:"+count+" starting...");
            LogRecordService.getInstance().save(new LogRecord("1",df.format(new Date()),"周期"+count+"抓取开始"));
            int depths = Integer.parseInt(ConfLoader.getProperty(ConfConstant.DEPTH,"2"));//leilongyan修改
            int maxArticleNum = Integer.parseInt(ConfLoader.getProperty(ConfConstant.MAXARTICLENUM,"5000"));//leilongyan修改
            for (int i = 0; i < depths; i++) {
                if(status==STOPED){
                    break;
                }
                Log.Infos("info","[Number:"+count+"] Starting depth "+(i+1));
                Generator generator=getGenerator();
                fetcher=getFecther();
                fetcher.fetchAll(generator);
                //一个周期内最大允许爬取的文章数
                if(Extractor.ARTICLENUM >= maxArticleNum) {
                    Extractor.ARTICLENUM = 0;
                    break;
                }
            }
            Extractor.ARTICLENUM = Extractor.ARTICLENUM==0?maxArticleNum:Extractor.ARTICLENUM;
            long endTime = System.currentTimeMillis();
            long duration = endTime-startTime;
            long hour = duration/3600000;
            LogRecordService.getInstance().save(new LogRecord("1",df.format(new Date()),
                    "周期"+count+"抓取结束,抓取文章"+Extractor.ARTICLENUM+"篇,耗时"+ hour +"时"+(duration-hour*3600000)/60000 + "分"));
            Extractor.ARTICLENUM = 0;
            if(status==STOPED){
                break;
            }
            try {
                //睡眠一个interval时间 1小时
                int interval = Integer.parseInt(ConfLoader.getProperty(ConfConstant.INTERVAL,"3600000"));
                Log.Infos("info","sleep " + interval + " mills...");
                Thread.sleep(interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        /*for (int i = 0; i < depth; i++) {
           if(status==STOPED){
               break;
           }
            Log.Infos("info","starting depth "+(i+1));
            Generator generator=getGenerator();
            fetcher=getFecther();
            fetcher.fetchAll(generator);
        }*/
    }

    /**
     *
     * @throws IOException
     */
    public void stop() throws IOException{
       fetcher.stop();
       status=STOPED;
    }
    
    protected void inject() throws IOException {
        Injector injector = new Injector(crawl_path);     
        injector.inject(seeds,resumable);
    }

    public class CommonConnectionConfig implements ConnectionConfig{
        @Override
        public void config(HttpURLConnection con) {
                configCon(con);
            }
    }

    protected Fetcher getFecther(){
         Handler fetch_handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Page page = (Page) msg.obj;
                switch(msg.what){
                    case Fetcher.FETCH_SUCCESS:
                        
                        visit(page);
                        break;
                    case Fetcher.FETCH_FAILED:
                        failed(page);
                        break;
                    default:
                        break;
                }
            }
        };
        Fetcher fetcher=new Fetcher(crawl_path);
        fetcher.setHandler(fetch_handler);
        conconfig = new CommonConnectionConfig();
        fetcher.setTaskname(taskname);
        int threads = Integer.parseInt(ConfLoader.getProperty(ConfConstant.THREADS,"10"));//leilongyan修改
        fetcher.setThreads(threads);
        fetcher.setConconfig(conconfig);
        return fetcher;
    }

    protected Generator getGenerator(){
        Generator generator = new StandardGenerator(crawl_path);
        //generator=new UniqueFilter(new IntervalFilter(new URLRegexFilter(generator, regexs)));
        generator=new UniqueFilter(new IntervalFilter(generator));//leilongyan修改 不需要URLRegexFilter了
        generator.setTaskname(taskname);
        return generator;
    }
   

    

    public static void main(String[] args) throws IOException {
        String crawl_path = "crawl";//  /home/hu/data/crawl_hfut1
        String root = "F:\\OwnerProjects\\KDD\\download";// /home/hu/data/hfut1
        BreadthCrawler crawler=new BreadthCrawler();
        crawler.taskname=RandomUtils.getTimeString()+"hfut";
        //crawler.addSeed("http://news.hfut.edu.cn/");
        //crawler.addRegex("http://news.hfut.edu.cn/.*");
        crawler.addSeed("http://roll.edu.sina.com.cn/a/lxcg/lxzx/default/index.shtml");
        crawler.addRegex("http://roll.edu.sina.com.cn/a/lxcg/lxzx/.*");
        crawler.addRegex("http://edu.sina.com.cn/a/2014-08-29/.*");
        crawler.setRoot(root);
        crawler.setCrawl_path(crawl_path);
        crawler.setResumable(true);      
        crawler.start(2);
    }

    public String getUseragent() {
        return useragent;
    }

    public void setUseragent(String useragent) {
        this.useragent = useragent;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public String getCrawl_path() {
        return crawl_path;
    }

    public void setCrawl_path(String crawl_path) {
        this.crawl_path = crawl_path;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public boolean isResumable() {
        return resumable;
    }

    public void setResumable(boolean resumable) {
        this.resumable = resumable;
    }

    public ConnectionConfig getConconfig() {
        return conconfig;
    }

    public void setConconfig(ConnectionConfig conconfig) {
        this.conconfig = conconfig;
    }

    public String getTaskname() {
        return taskname;
    }

    public void setTaskname(String taskname) {
        this.taskname = taskname;
    }

    
    
}
