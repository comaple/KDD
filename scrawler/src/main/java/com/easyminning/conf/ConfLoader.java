package com.easyminning.conf;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.reloading.ReloadingStrategy;
import org.apache.commons.io.FileUtils;
import cn.edu.hfut.dmic.webcollector.util.Log;
import java.io.*;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

/**
 * Created by jerry on 2014/8/31.
 */
public class ConfLoader {
    public static HashSet<String> seedSet = new HashSet<String>();
    public static HashSet<String> repeatableRegexSet = new HashSet<String>();
    public static HashSet<String> positiveRegexSet = new HashSet<String>();
    public static HashSet<String> negativeRegexSet = new HashSet<String>();
    public static HashSet<String> topicRegexSet = new HashSet<String>();
    //<使用模板的url正则,模板文件>
    public static HashMap<String,HashMap<String,String>> templateMap = new HashMap<String, HashMap<String,String>>();

    public static PropertiesConfiguration prop = null;

    static {
        loadConf();
    }

    public static void loadConf(){
        Log.Infos("info", "loading crawler conf...");
        try {
            prop = new PropertiesConfiguration("crawl.properties");
            FileChangedReloadingStrategy strategy  =new FileChangedReloadingStrategy(){
                public void reloadingPerformed(){
                    super.reloadingPerformed();
                    reloadConf();
                    Log.Infos("info", "properties file reloading...");
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
            repeatableRegexSet = analyzeConf(ConfConstant.REPEATABLEREGEX,ConfConstant.ObjectSplit,prop,repeatableRegexSet);
            positiveRegexSet = analyzeConf(ConfConstant.POSITIVEREGEX, ConfConstant.ObjectSplit, prop, positiveRegexSet);
            negativeRegexSet = analyzeConf(ConfConstant.NEGATIVEREGEX, ConfConstant.ObjectSplit, prop, negativeRegexSet);
            topicRegexSet = analyzeConf(ConfConstant.TOPICREGEX, ConfConstant.ObjectSplit, prop, topicRegexSet);

            HashSet<String> templateSet = new HashSet<String>();
            templateSet = analyzeConf(ConfConstant.TEMPLATES, ConfConstant.ObjectSplit, prop, templateSet);
            for (String template : templateSet) {
                String[] regFiles = template.split(ConfConstant.TemplateSplit);
                InputStream inputStream = ConfLoader.class.getClassLoader().getResourceAsStream(regFiles[1]);
                if (inputStream == null) {
                    Log.Infos("info", "file:" + regFiles[1] + " not exists");
                    continue;
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                HashMap<String,String> regs = new HashMap<String,String>();
                while ((line = reader.readLine()) != null) {
                    int index = line.indexOf('=');
                    regs.put(line.substring(0,index).trim(),line.substring(index + 1).trim());
                }
                templateMap.put(regFiles[0], regs);
                reader.close();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static HashSet<String> analyzeConf(String proKey,String objectSplit,
                        PropertiesConfiguration prop, HashSet<String> propSet) throws IOException, URISyntaxException {
        if(prop == null){
            return propSet;
        }
        propSet.clear();
        String proValue = getProperty(proKey,null);
        if(proValue != null) {
            String[] values = proValue.split(objectSplit);
            for (String value : values) {
                //propSet.add(value.trim());
                File file = new File(ConfLoader.class.getClassLoader().getResource(value).toURI());
                if(!file.exists()){
                    Log.Infos("info", "file:" + value + " not exists");
                    continue;
                }
                List<String> confs = FileUtils.readLines(file);
                for(String conf : confs){
                    propSet.add(conf.trim());
                }
            }
        }
        return propSet;
    }

    public static String getProperty(String key, String defaultValue){
        if(prop == null){
            return defaultValue;
        }
        Object proValue = prop.getProperty(key);
        if(null != proValue){
            return proValue.toString();
        }
        return defaultValue;
    }

    public static void main(String []args){
        System.out.println(ConfLoader.seedSet);
        System.out.println(ConfLoader.positiveRegexSet);
        System.out.println(ConfLoader.negativeRegexSet);
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
