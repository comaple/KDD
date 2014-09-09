package weixincrawler.crawler;

import cn.edu.hfut.dmic.webcollector.fetcher.Fetcher;
import cn.edu.hfut.dmic.webcollector.generator.DbUpdater;
import cn.edu.hfut.dmic.webcollector.generator.Generator;
import cn.edu.hfut.dmic.webcollector.handler.Message;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.Link;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.parser.ParseResult;
import cn.edu.hfut.dmic.webcollector.util.HandlerUtils;
import cn.edu.hfut.dmic.webcollector.util.HttpUtils;
import cn.edu.hfut.dmic.webcollector.util.Log;
import cn.edu.hfut.dmic.webcollector.util.WorkQueue;
import weixincrawler.conf.WeixinConfConstant;
import weixincrawler.conf.WeixinConfLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by leilongyan on 2014/9/9.
 */
public class WeixinFetcher extends Fetcher{
    public WeixinFetcher(String crawl_path){
        super(crawl_path);
    }

    @Override
    protected void start() throws IOException {
        if (needUpdateDb) {
            this.dbUpdater = new WeixinDbUpdater(crawl_path);
            dbUpdater.initUpdater();
            dbUpdater.lock();
        }
        workqueue = new WorkQueue(threads);
    }

    @Override
    public void fetchAll(Generator generator) throws IOException {
        start();
        CrawlDatum crawlDatum = null;
        while ((crawlDatum = generator.next()) != null) {
            if(crawlDatum.needFetch) {//leilongyan修改，加这个判断设计使去掉递归设计，避免栈溢出
                addFetcherThread(crawlDatum.url);
            }
        }
        end();
    }

    @Override
    public void addFetcherThread(String url) {
        WeixinFetcherThread fetcherthread = new WeixinFetcherThread(url);
        workqueue.execute(fetcherthread);
    }

    class WeixinFetcherThread extends Thread {
        String url;
        public WeixinFetcherThread(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            Page page = new Page();
            page.url = url;
            Page response=null;

            response = HttpUtils.fetchHttpResponse(url, conconfig, retry);
            if (response == null) {
                Log.Errors("failed ", WeixinFetcher.this.taskname, page.url);
                HandlerUtils.sendMessage(handler, new Message(WeixinFetcher.FETCH_FAILED, page), true);
                return;
            }

            page=response;
            CrawlDatum crawldatum = new CrawlDatum();//新建的对象
            crawldatum.url=url;
            crawldatum.status = Page.FETCHED;
            page.fetchtime = System.currentTimeMillis();
            crawldatum.fetchtime = page.fetchtime;
            crawldatum.needFetch = true;
            Log.Infos("fetch", WeixinFetcher.this.taskname, page.url);

            if (needUpdateDb) {
                try {
                    dbUpdater.append(crawldatum);
                    //只有种子链接才进一步解析，否则不再解析里面的url
                    String seedRegex = WeixinConfLoader.getProperty(WeixinConfConstant.SEEDREGEX, "href=\"\\?query=.*?type=2.*?page=2.*?\">");
                    int temInx = seedRegex.indexOf("query");
                    temInx = temInx == -1 ? 9 : temInx;
                    seedRegex = "(?is)" + seedRegex.substring(temInx,temInx + 6);//.replace("href=\"\\\\?","").replace("page=2.*?","");
                    if(Pattern.matches(seedRegex,url)) {
                        if (page.headers.containsKey("Content-Type")) {
                            String contenttype = page.headers.get("Content-Type").toString();

                            if (contenttype.contains("text/html")) {
                                WeixinHtmlParser htmlparser = new WeixinHtmlParser();
                                ParseResult parseresult = htmlparser.getParse(page);
                                ArrayList<Link> links = parseresult.links;

                                for (Link link : links) {
                                    CrawlDatum link_crawldatum = new CrawlDatum();
                                    link_crawldatum.url = link.url;
                                    link_crawldatum.status = Page.UNFETCHED;
                                    dbUpdater.append(link_crawldatum);
                                }
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            HandlerUtils.sendMessage(handler, new Message(WeixinFetcher.FETCH_SUCCESS, page),true);
        }
    }
}
