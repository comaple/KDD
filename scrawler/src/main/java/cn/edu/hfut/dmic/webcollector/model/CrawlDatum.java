/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.edu.hfut.dmic.webcollector.model;

import org.apache.avro.reflect.Nullable;

/**
 *
 * @author hu
 */
public class CrawlDatum {
    @Nullable public String url;
    @Nullable public int status=Page.STATUS_UNDEFINED;
    @Nullable public long fetchtime=Page.FETCHTIME_UNDEFINED;
    public boolean needFetch = true;//leilongyan修改，此属性主要解决next函数递归调用太深的问题
}
