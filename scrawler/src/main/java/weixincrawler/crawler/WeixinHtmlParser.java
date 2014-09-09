package weixincrawler.crawler;

import cn.edu.hfut.dmic.webcollector.model.Link;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.parser.LinkUtils;
import cn.edu.hfut.dmic.webcollector.parser.ParseResult;
import cn.edu.hfut.dmic.webcollector.parser.Parser;
import cn.edu.hfut.dmic.webcollector.util.CharsetDetector;
import com.easyminning.conf.ConfConstant;
import com.easyminning.conf.ConfLoader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import weixincrawler.conf.WeixinConfLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by leilongyan on 2014/9/9.
 */
public class WeixinHtmlParser  extends Parser {
    @Override
    public ParseResult getParse(Page page) throws Exception {
        String charset = CharsetDetector.guessEncoding(page.content);
        page.html = new String(page.content, charset);
        page.doc = Jsoup.parse(page.html);
        page.doc.setBaseUri(page.url);
        //ArrayList<Link> links = topNFilter(LinkUtils.getAll(page));//getAll会将a img css都抓下来
        ArrayList<Link> links = getLinks(page.doc);
        ParseResult parseresult = new ParseResult(page.doc.title(), links);
        return parseresult;
    }

    private static ArrayList<Link> getLinks(Document doc){
        ArrayList<Link> links = new ArrayList<Link>();
        Set<String> urls = new HashSet<String>();
        Elements link_elements = doc.select("a[href]");
        String topicRegex = WeixinConfLoader.getProperty(ConfConstant.TOPICREGEX,"");
        for (Element link : link_elements) {
            String anchor=link.text();
            //String href=link.attr("abs:href");
            String href=link.attr("href");//leilongyan修改 并在此加入正则过滤 下载的时候不需要重新正则过滤
            if(Pattern.matches(topicRegex, href)){
                if(!urls.contains(href)) {
                    urls.add(href);
                    links.add(new Link(anchor, href));
                }
            }
        }
        return links;
    }

    public Page getParsedPage(Page page) throws Exception {
        String charset = CharsetDetector.guessEncoding(page.content);
        page.html = new String(page.content, charset);
        return page;
    }
}
