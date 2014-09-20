package com.easyminning.extractor;

import cn.edu.hfut.dmic.webcollector.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-9-20
 * Time: 上午10:28
 * To change this template use File | Settings | File Templates.
 */
public class ContentFilter implements Filter {

    private static int CONTENT_LENGTH = 200;

    @Override
    public boolean filter(Article article) {
        if (article == null)  return false;
        if (article.title == null || "".equals(article.title)
                || article.publishDate == null || article.context == null) {
            Log.Infos("extraterror","extrat failure,some attr is null:" + article.url);
            return false;
        }
        if (article.context == null || article.context.length() < CONTENT_LENGTH) {
            Log.Infos("extraterror","extrat context too small:" + article.url);
            return false;
        }

        return true;
    }

}
