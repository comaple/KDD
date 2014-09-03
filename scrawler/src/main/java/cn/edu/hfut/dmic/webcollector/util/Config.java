/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.edu.hfut.dmic.webcollector.util;

/**
 *
 * @author hu
 */
public class Config {
    public static final String old_info_path="old/info.avro";//leilongyan修改 去掉crawldb/
    public static final String current_info_path="current/info.avro";//leilongyan修改 去掉crawldb/
    public static final String segment_prepath="segment";
    public static int maxsize=1000*1000;
    //public static long interval=1*60*60*1000;//1000*60*3;   leilongyan修改
    public static final String lock_path="lock";//leilongyan修改 去掉crawldb/
    //public static Integer topN=null; leilongyan修改
}
