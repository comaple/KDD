package weixincrawler.crawler;

import cn.edu.hfut.dmic.webcollector.generator.*;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.util.Config;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by leilongyan on 2014/9/9.
 */
public class WeixinStandardGenerator  extends Generator {
    public String crawl_path;
    DbReader dbreader;

    public WeixinStandardGenerator(String crawl_path){
        this.crawl_path=crawl_path;
        try {
            backup();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        DbUpdater dbupdater=new WeixinDbUpdater(crawl_path);
        try {
            if(dbupdater.isLocked()){
                dbupdater.merge();
                dbupdater.unlock();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            initReader();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void backup() throws IOException {
        DbUpdater.backup(crawl_path);
    }

    @Override
    public CrawlDatum next(){
        if(!dbreader.hasNext())
            return null;
        CrawlDatum crawldatum=dbreader.readNext();
        if(crawldatum==null){
            return null;
        }
        return crawldatum;
    }

    public void initReader() throws IOException{
        File oldfile=new File(crawl_path, Config.old_info_path);
        dbreader=new DbReader(oldfile);
    }
}
