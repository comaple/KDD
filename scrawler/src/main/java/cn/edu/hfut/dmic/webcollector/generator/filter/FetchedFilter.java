package cn.edu.hfut.dmic.webcollector.generator.filter;

import cn.edu.hfut.dmic.webcollector.generator.Generator;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.Page;

/**
 * Created by leilongyan on 2014/9/9.
 */
public class FetchedFilter extends Filter{
    public FetchedFilter(Generator generator) {
        super(generator);
    }
    @Override
    public CrawlDatum next() {
        CrawlDatum crawldatum=generator.next();
        if(crawldatum==null){
            return null;
        }
        if(crawldatum.status== Page.UNFETCHED){
            return crawldatum;
        }
        crawldatum.needFetch = false;
        return crawldatum;
    }
}
