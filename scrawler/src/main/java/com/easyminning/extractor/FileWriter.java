package com.easyminning.extractor;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-8-31
 * Time: 下午5:21
 * To change this template use File | Settings | File Templates.
 */
public class FileWriter  {

    // 标题,发布时间,url,作者，抽取正文，原文
    private static String FILE_HEAD = "title||==||publishDate||==||url||==||author||==||context||==||contextWithTag";

    private static String SEPERATOR = "||==||";

    private Integer count = 0; // 行，计数

    private static String SRC_HOME = "/home/bigdata/program/hdfsupload/data";


    private File currentFile;
    private String currentFileName;

    private static Integer FILE_LINE_NUM = 100;


    public FileWriter() {

        try {
            PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(
                    FileWriter.class.getClassLoader().getResource("configuration-util.properties"));
            SRC_HOME = propertiesConfiguration.getString("hdfsupload.localpath");

        } catch (Exception e) {
            e.printStackTrace();
        }

        this.clearTmpFile();
    }


    /**
     * 文件中写入一篇文章
     *
     * @param article
     */
    public void writeLine(Article article) {
        try {
            if (currentFile == null) {
                currentFileName = SRC_HOME + File.separator + "craw_" + new Date().getTime() + ".csv.tmp";
                currentFile = new File(currentFileName);
                FileUtils.writeStringToFile(currentFile, FILE_HEAD+"\r\n");
            }

            FileUtils.writeStringToFile(currentFile, covertArticleToString(article)+"\r\n",true);
            count++;

            // 满100行，该文件名
            if (count % FILE_LINE_NUM == 0) {
                currentFile.renameTo(new File(currentFileName.substring(0, currentFileName.indexOf(".tmp"))));
                currentFile = null;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void clearTmpFile() {
        try {
            // 启动自动检测.tmp结尾文件
            File file = new File(SRC_HOME);
            String[] files = file.list();

            // 定时检测以.tmp结尾的文件
            for (String tmp : files) {
                if ("bak".equals(tmp)) continue;
                if (new File(file, tmp).isDirectory()) continue;
                if (!tmp.endsWith(".tmp")) continue; //
                new File(file, tmp).renameTo(new File(file, tmp.substring(0, tmp.indexOf(".tmp"))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 转换文章为字符串
     *
     * @param article
     * @return
     */
    private String covertArticleToString(Article article) {
        StringBuilder sbuilder = new StringBuilder();
        String title = article.title;
        if (title == null) title = "";
        sbuilder.append(title);
        sbuilder.append(SEPERATOR);
        String publishDate = article.publishDate;
        if (publishDate == null) publishDate = "";
        sbuilder.append(publishDate);
        sbuilder.append(SEPERATOR);
        String url = article.url;
        if (url == null) url = "";
        sbuilder.append(url);
        sbuilder.append(SEPERATOR);
        String author = article.author;
        if (author == null) author = "";
        sbuilder.append(author);
        sbuilder.append(SEPERATOR);
        String context = article.context;
        if (context == null) context = "";
        sbuilder.append(context);
        sbuilder.append(SEPERATOR);
        String contextWithTag = article.contextWithTag;
        if (contextWithTag == null) contextWithTag = "";
        sbuilder.append(contextWithTag);
        return sbuilder.toString();
    }


    public static void main(String[] args) {
        FileWriter fileWriter = new FileWriter();
        for (int i=0;i<200;i++) {
            Article article = new Article();
            article.context = "dd"+i;
            fileWriter.writeLine(article);
        }
    }


}
