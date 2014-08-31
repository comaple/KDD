package com.easyminning.conf;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.reloading.ReloadingStrategy;
import org.apache.commons.io.FileUtils;
import org.mortbay.log.Log;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;

/**
 * Created by jerry on 2014/8/31.
 */
public class ConfLoader {
    public static HashSet<String> seedSet = new HashSet<String>();
    public static HashSet<String> filterRegexSet = new HashSet<String>();
    public static HashSet<String> topicRegexSet = new HashSet<String>();
    //<使用模板的url正则,模板文件>
    public static HashMap<String,HashMap<String,String>> templateMap = new HashMap<String, HashMap<String,String>>();

    public static PropertiesConfiguration prop = null;

    static {
        loadConf();
    }

    public static void loadConf(){
        Log.info("loading crawler conf...");
        try {
            prop = new PropertiesConfiguration("crawl.properties");
            FileChangedReloadingStrategy strategy  =new FileChangedReloadingStrategy(){
                public void reloadingPerformed(){
                    super.reloadingPerformed();
                    reloadConf();
                }
            };
            prop.setReloadingStrategy(strategy);

            reloadConf();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void reloadConf(){
        try {
            seedSet = analyzeConf(ConfConstant.SEEDS, ConfConstant.ObjectSplit, prop, seedSet);
            filterRegexSet = analyzeConf(ConfConstant.FILTERREGEX, ConfConstant.ObjectSplit, prop, filterRegexSet);
            topicRegexSet = analyzeConf(ConfConstant.TOPICREGEX, ConfConstant.ObjectSplit, prop, topicRegexSet);

            HashSet<String> templateSet = new HashSet<String>();
            templateSet = analyzeConf(ConfConstant.TEMPLATES, ConfConstant.ObjectSplit, prop, templateSet);
            for (String template : templateSet) {
                String[] regFiles = template.split(ConfConstant.TemplateSplit);
                InputStream inputStream = ConfLoader.class.getClassLoader().getResourceAsStream(regFiles[1]);
                if (inputStream == null) {
                    continue;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                HashMap<String,String> regs = new HashMap<String,String>();
                while ((line = reader.readLine()) != null) {
                    int index = line.indexOf('=');
                    regs.put(line.substring(0,index),line.substring(index + 1));
                }
                templateMap.put(regFiles[0], regs);
                reader.close();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static HashSet<String> analyzeConf(String proKey,String objectSplit,
                        PropertiesConfiguration prop, HashSet<String> propSet){
        if(prop == null){
            return propSet;
        }
        propSet.clear();
        Object proValue = prop.getProperty(proKey);
        if(proValue != null) {
            String[] values = proValue.toString().split(objectSplit);
            for (String value : values) {
                propSet.add(value.trim());
            }
        }
        return propSet;
    }

    public static void main(String []args){
        System.out.println(ConfLoader.seedSet);
        System.out.println(ConfLoader.filterRegexSet);
        System.out.println(ConfLoader.topicRegexSet);
        System.out.println(ConfLoader.templateMap);
        String s = "author=<span class=\"author\">.*?</span>";
        int index = s.indexOf('=');
        System.out.println(s.substring(0,index)+"***"+s.substring(index));

        try {
            while(true) {
                Thread.sleep(2000);
                System.out.println(ConfLoader.topicRegexSet);
                System.out.println(prop.getProperty(ConfConstant.TOPICREGEX));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
