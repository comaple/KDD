package com.easyminning.extractor;

import cn.edu.hfut.dmic.webcollector.model.Page;

/**
 * Created by jerry on 2014/8/30.
 */
public class TemplateExtractor extends Extractor {
    private String templateRex;

    public TemplateExtractor(String templateRex){
        this.templateRex = templateRex;
    }


    @Override
    public Article extractArticle(Page page) {
        return null;
    }
}
