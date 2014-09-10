package weixincrawler.crawler;

import cn.edu.hfut.dmic.webcollector.generator.DbReader;
import cn.edu.hfut.dmic.webcollector.generator.DbUpdater;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.util.Config;
import com.easyminning.conf.ConfLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by leilongyan on 2014/9/9.
 */
public class WeixinDbUpdater extends DbUpdater{
    public WeixinDbUpdater(String crawl_path){
        super(crawl_path);
    }

    //合并current_info_path中的数据，使其url唯一
    @Override
    public void merge() throws IOException {
        File currentfile=new File(crawl_path, Config.current_info_path);
        DbReader reader=new DbReader(currentfile);

        HashMap<String,Integer> indexmap=new HashMap<String, Integer>();
        ArrayList<CrawlDatum> origin_datums=new ArrayList<CrawlDatum>();
        CrawlDatum crawldatum=null;
        while(reader.hasNext()){
            crawldatum=reader.readNext();
            String url=crawldatum.url;
            if(indexmap.containsKey(crawldatum.url)){
                int preindex=indexmap.get(url);
                CrawlDatum pre_datum=origin_datums.get(preindex);
                if(crawldatum.status== Page.UNFETCHED){  //之前有的，现在的又是unfetched状态的是重复页面，不予继续抓取
                    continue;
                }else if(pre_datum.fetchtime>crawldatum.fetchtime){//leilongyan修改去掉=
                    continue;
                }else{
                    origin_datums.set(preindex, crawldatum);
                    indexmap.put(url, preindex);
                }

            }else{
                origin_datums.add(crawldatum);
                indexmap.put(url, origin_datums.size()-1);
            }
        }
        updateAll(origin_datums);
    }
}
