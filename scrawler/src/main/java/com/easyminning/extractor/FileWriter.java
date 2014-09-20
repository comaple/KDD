package com.easyminning.extractor;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-8-31
 * Time: 下午5:21
 * To change this template use File | Settings | File Templates.
 */
public class FileWriter  implements Runnable {


    private static List<Filter> filterList = new ArrayList<Filter>(){{
        add(new ContentFilter());
    }};

    // 标题,发布时间,url,作者，抽取正文，原文
    private static String FILE_HEAD = "title||==||publishDate||==||url||==||author||==||context||==||contextWithTag||==||type";

    private static String SEPERATOR = "||==||";

    private Integer count = 0; // 行，计数

    private static String SRC_HOME = "/home/bigdata/program/hdfsupload/data";

    // 队列大小
    private static int QU_SIZE = 10000;


    // 日志队列
    private  BlockingDeque<Article> ARTICLE_QUEUE = new LinkedBlockingDeque<Article>(QU_SIZE);


    private File currentFile;
    private String currentFileName;

    private static Integer FILE_LINE_NUM = 100;

    private static FileWriter fileWriter = new FileWriter();

    public static FileWriter getInstance() {
       return fileWriter;
    }


    private FileWriter() {
        try {
            PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(
                    FileWriter.class.getClassLoader().getResource("configuration-util.properties"));
            SRC_HOME = propertiesConfiguration.getString("hdfsupload.localpath");

            Thread thread = new Thread(this);
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.clearTmpFile();
    }

    public void writeArticle(Article article) {
        try {
            if (ARTICLE_QUEUE.size() < QU_SIZE) {
                ARTICLE_QUEUE.put(article);
            }
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    @Override
    public void run() {
        boolean flag = true;
        while (true) {
            try {
                Article article = ARTICLE_QUEUE.take();

                for(Filter filter : filterList) {
                    flag = filter.filter(article);
                    if (!flag) continue;
                }

                this.writeLine(article);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Thread.sleep(1000L);
                } catch (Exception e1){
                    e1.printStackTrace();
                }
            }
        }
    }


    /**
     * 文件中写入一篇文章
     *
     * @param article
     */
    private void writeLine(Article article) {
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


    private void clearTmpFile() {
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
        sbuilder.append(SEPERATOR);
        String type = article.type;
        if (type == null) type = "1";
        sbuilder.append(type);
        sbuilder.append(SEPERATOR);
        sbuilder.append(String.valueOf(System.currentTimeMillis()));
        return sbuilder.toString();
    }


    public static void main(String[] args) {
        FileWriter fileWriter = FileWriter.getInstance();
        for (int i=0;i<200;i++) {
            Article article = new Article();
            article.context = "dd"+i;
            fileWriter.writeArticle(article);
        }
    }


}
