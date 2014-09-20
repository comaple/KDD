package com.easyminning.extractor;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-9-20
 * Time: 上午10:28
 * To change this template use File | Settings | File Templates.
 */
public class ContentFilter implements Filter {

    @Override
    public boolean filter(Article article) {
        if (article == null)  return false;
        if (article.title == null || "".equals(article.title)) {
            return false;
        }
        if (article.context == null || article.context.length() < 200) {
            return false;
        }
        return false;
    }

}
