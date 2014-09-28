package weixincrawler.crawler;

import cn.edu.hfut.dmic.webcollector.crawler.BreadthCrawler;
import cn.edu.hfut.dmic.webcollector.fetcher.Fetcher;
import cn.edu.hfut.dmic.webcollector.generator.Generator;
import cn.edu.hfut.dmic.webcollector.generator.filter.FetchedFilter;
import cn.edu.hfut.dmic.webcollector.generator.filter.UniqueFilter;
import cn.edu.hfut.dmic.webcollector.handler.Handler;
import cn.edu.hfut.dmic.webcollector.handler.Message;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.util.Log;
import com.easyminning.conf.ConfConstant;
import com.easyminning.conf.ConfLoader;
import com.easyminning.extractor.Extractor;
import com.easyminning.tag.LogRecord;
import com.easyminning.tag.LogRecordService;
import weixincrawler.conf.WeixinConfConstant;
import weixincrawler.conf.WeixinConfLoader;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by leilongyan on 2014/9/9.
 */
public class WeixinArticleCrawler  extends BreadthCrawler {
    public WeixinArticleCrawler(){
        super();
    }

    @Override
    protected void visit(Page page) {
        if(null != WeixinExtractor.extract(page)){//文章抽取
            super.visit(page);
        }
    }

    @Override
    public void start(int depth) throws IOException {
        if(WeixinConfLoader.getProperty(ConfConstant.TOPICREGEX,"").isEmpty()){
            Log.Infos("error:"+"Please add at least one topic regex rule");
            return;
        }
        if(WeixinConfLoader.getProperty(WeixinConfConstant.SEEDREGEX,"").isEmpty()){
            Log.Infos("error:"+"Please add at least one seed regex rule");
            return;
        }

        status=RUNNING;
        //leilongyan修改 可以循环无限运行
        int count = 0;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat df2 = new SimpleDateFormat("HH:mm:ss");
        while(true){
            count++;
            long startTime = System.currentTimeMillis();
            Log.Infos("info","Number:"+count+" starting...");
            LogRecordService.getInstance().save(new LogRecord("2",df.format(new Date()),"周期"+count+"微信抓取开始"));
            List<String> seeds = SeedsGenerator.generatorSeedsFromWords();
            for(String seed : seeds){
                addSeed(seed);
            }
            inject();

            int depths = Integer.parseInt(WeixinConfLoader.getProperty(ConfConstant.DEPTH,"2"));
            for (int i = 0; i < depths; i++) {
                if(status==STOPED){
                    break;
                }
                Log.Infos("info","[Number:"+count+"] Starting depth "+(i+1));
                Generator generator=getGenerator();
                fetcher=getFecther();
                fetcher.fetchAll(generator);
            }
            long endTime = System.currentTimeMillis();
            LogRecordService.getInstance().save(new LogRecord("2",df.format(new Date()),
                    "周期"+count+"微信抓取结束,抓取文章"+ WeixinExtractor.articleNum +"篇,耗时"+df2.format(endTime-startTime)));
            WeixinExtractor.articleNum = 0;
            if(status==STOPED){
                break;
            }
            try {
                //睡眠一个interval时间 10天
                int interval = Integer.parseInt(WeixinConfLoader.getProperty(WeixinConfConstant.INTERVALDAY,"10"));
                long intervaltime = interval * 24 * 3600 * 1000;
                Log.Infos("info","sleep " + intervaltime + " mills...");
                Thread.sleep(intervaltime);//intervaltime
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
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
        Fetcher fetcher=new WeixinFetcher(crawl_path);
        fetcher.setHandler(fetch_handler);
        conconfig = new CommonConnectionConfig();
        fetcher.setTaskname(taskname);
        int threads = Integer.parseInt(WeixinConfLoader.getProperty(ConfConstant.THREADS,"5"));
        fetcher.setThreads(threads);
        fetcher.setConconfig(conconfig);
        return fetcher;
    }

    @Override
    protected Generator getGenerator(){
        Generator generator = new WeixinStandardGenerator(crawl_path);
        generator=new UniqueFilter(new FetchedFilter(generator));
        generator.setTaskname(taskname);
        return generator;
    }

    public static void execute(){
        Thread crawlthread = new Thread() {
            @Override
            public void run() {
                BreadthCrawler crawler=new WeixinArticleCrawler();
                //在此设定的参数都不支持配置文件动态自动更新
                crawler.setRoot(WeixinConfLoader.getProperty(ConfConstant.DOWNLOADPATH, "weixindownload"));
                crawler.setCrawl_path(WeixinConfLoader.getProperty(ConfConstant.CRAWLDBPATH, "weixincrawldb"));
                String resu = WeixinConfLoader.getProperty(ConfConstant.RESUMABLE,"true");
                crawler.setResumable(Boolean.parseBoolean(resu));
                try {
                    crawler.start(0);//Integer.parseInt(depth)
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };
        crawlthread.start();
    }

    public static void main(String[] args) throws IOException {
        execute();
    }
}
