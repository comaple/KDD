package cn.edu.hfut.dmic.webcollector.crawler;

import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.output.FileSystemOutput;
import cn.edu.hfut.dmic.webcollector.util.Config;
import cn.edu.hfut.dmic.webcollector.util.Log;
import cn.edu.hfut.dmic.webcollector.util.RandomUtils;
import com.easyminning.conf.ConfConstant;
import com.easyminning.conf.ConfLoader;
import com.easyminning.extractor.Extractor;

import java.io.IOException;

/**
 * Created by jerry on 2014/8/30.
 */
public class ArticleCrawler extends BreadthCrawler {
    public ArticleCrawler(){
        super();
    }

    @Override
    protected void visit(Page page) {
        super.visit(page);
        Extractor.extract(page); //文章抽取
    }

    public static void execute(){
        Thread crawlthread = new Thread() {
            @Override
            public void run() {
                BreadthCrawler crawler=new ArticleCrawler();
                //在此设定的参数都不支持配置文件动态自动更新
                crawler.setRoot(ConfLoader.getProperty(ConfConstant.DOWNLOADPATH,"download"));
                String resu = ConfLoader.getProperty(ConfConstant.RESUMABLE,"true");
                crawler.setResumable(Boolean.parseBoolean(resu));
                crawler.setCrawl_path(ConfLoader.getProperty(ConfConstant.CRAWLDBPATH,"crawl"));
                String depth = ConfLoader.getProperty(ConfConstant.DEPTH,"3");
                String threads = ConfLoader.getProperty(ConfConstant.THREADS,"10");

                crawler.setThreads(Integer.parseInt(threads));
                for(String seed : ConfLoader.seedSet){
                    crawler.addSeed(seed);
                }

                try {
                    crawler.start(Integer.parseInt(depth));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };
        crawlthread.start();
    }

    public static void main(String[] args) throws IOException {
        execute();
        /*String crawl_path = "crawl";//  /home/hu/data/crawl_hfut1
        String root = "F:\\OwnerProjects\\KDD\\download";// /home/hu/data/hfut1
        //Config.topN=500;
        BreadthCrawler crawler=new ArticleCrawler();
        crawler.setTaskname(RandomUtils.getTimeString()+"hfut");
        //crawler.addSeed("http://news.hfut.edu.cn/");
        //crawler.addRegex("http://news.hfut.edu.cn/.*");
        crawler.addSeed("http://roll.edu.sina.com.cn/a/lxcg/lxzx/default/index.shtml");
        crawler.addRegex("http://roll.edu.sina.com.cn/a/lxcg/lxzx/default/.*");
        crawler.addRegex("http://edu.sina.com.cn/a/2014-08-29/.*");
        crawler.setRoot(root);
        crawler.setCrawl_path(crawl_path);
        crawler.setResumable(true);
        crawler.start(2);*/
    }
}
