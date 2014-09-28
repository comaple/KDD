package com.easyminning.extractor;

/**
 * Created by jerry on 2014/9/22.
 */
public class CaseContentFilter implements Filter {
    @Override
    public boolean filter(Article article) {
        if(article.title.contains("案例"))
            return true;
        int count = article.context.contains("案例")?1:0;
        count += article.context.contains("申请")?1:0;
        count += article.context.contains("录取")?1:0;
        return count >= 2;
    }
}
