package com.easyminning.extractor;

/**
 * Created by jerry on 2014/9/22.
 */
public class CaseContentFilter implements Filter {
    @Override
    public boolean filter(Article article) {
        boolean containCase = article.context.contains("案例");
        return containCase;
    }
}
