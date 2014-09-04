/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.edu.hfut.dmic.webcollector.generator.filter;

import cn.edu.hfut.dmic.webcollector.generator.Generator;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.util.Config;
import com.easyminning.conf.ConfConstant;
import com.easyminning.conf.ConfLoader;

import java.util.regex.Pattern;

/**
 *
 * @author hu
 */
public class IntervalFilter extends Filter{

    public IntervalFilter(Generator generator) {
        super(generator);
    }

    @Override
    public CrawlDatum next() {
        
        CrawlDatum crawldatum=generator.next();
        
         if(crawldatum==null){
            return null;
        }
         
        
        if(crawldatum.status==Page.UNFETCHED){
            return crawldatum;
        }

        //if(Config.interval==-1){  //应该 除了种子外，其他的页面已经抓取过了就不再抓取了
        int interval = Integer.parseInt(ConfLoader.getProperty(ConfConstant.INTERVAL,"-1")); //leilongyan修改 若是-1则对可重复抓取的页面不予重复抓取
        if(interval==-1){
            //return next();leilongyan修改 深度递归将导致栈溢出
            crawldatum.needFetch = false;
            return crawldatum;
        }
       
        Long lasttime=crawldatum.fetchtime;
        //if(lasttime+Config.interval>System.currentTimeMillis()){
        if(lasttime+interval>System.currentTimeMillis()){//每隔interval后种子相关页面可重新抓取，默认一小时
            //return next();leilongyan修改 深度递归将导致栈溢出
            crawldatum.needFetch = false;
            return crawldatum;
        }

        //leilongyan修改，增加功能，除了种子相关页可以重复抓取外，其他的页面不可重新抓取
        boolean isRepeatable = false;
        for(String rregex: ConfLoader.repeatableRegexSet){//
            if(Pattern.matches(rregex, crawldatum.url)){
                isRepeatable = true;
                break;
            }
        }
        if(!isRepeatable){
            //return next();leilongyan修改 深度递归将导致栈溢出
            crawldatum.needFetch = false;
            return crawldatum;
        }
        //System.out.println("########################"+crawldatum.url+"########################");
        return crawldatum;
    }

   
    
}
