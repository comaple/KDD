package com.easyminning.hdfs;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: 日志缓存队列
 * Date: 14-6-11
 * Time: 下午3:33
 * To change this template use File | Settings | File Templates.
 */
public class HDFSService implements Runnable {

    public static String DEST_PATH = "/kdd/scraw/";  // hdfs 存放目录

    public static String SRC_PATH = "/Volumes/study/test"; // 本地上传目录

    public static String FS_DEFAULT_NAME = "";

    public static DateFormat DF = new SimpleDateFormat("yyyyMMdd");

    public static Log log = LogFactory.getLog(HDFSService.class);

    private UpLoadUtil upLoadUtil; // 上传文件工具类


    public void startThread() {
        try {
            PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration(
                    HDFSService.class.getClassLoader().getResource("configuration-util.properties"));
            SRC_PATH = propertiesConfiguration.getString("hdfsupload.localpath");
            DEST_PATH = propertiesConfiguration.getString("hdfsupload.hdfspath");
            FS_DEFAULT_NAME = propertiesConfiguration.getString("hdfsupload.defaultname");
        } catch (Exception e) {
            e.printStackTrace();
        }

        upLoadUtil = new UpLoadUtil();
        upLoadUtil.init(FS_DEFAULT_NAME);

        Thread thread = new Thread(this);
        thread.start();
    }


    @Override
    public void run() {
        while (true) {
            try {
               File srcPath = new File(SRC_PATH);
               String[] fileList = srcPath.list(); // 获取源目录文件夹下所有文件

               for (String file : fileList) {
                   if ("bak".equals(file)) continue;
                   if (new File(srcPath+File.separator + file).isDirectory()) continue;
                   if (file.endsWith("tmp")) continue; //
                   Date now = new Date();
                   String nowStr = DF.format(now);
                   log.info("上传文件：" + srcPath + File.separator + file + " 目标文件夹：" + DEST_PATH + nowStr);
                   upLoadUtil.UploadLocalFileToHdfs(srcPath + File.separator + file, DEST_PATH + nowStr + File.separator + file);
               }
               Thread.sleep(1000*5L); // 睡眠5秒
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    Thread.sleep(1000*5L);
                } catch (Exception e1){
                    log.error(e1.getMessage());
                }
            }
        }
    }


    public static void main(String[] args) {
        HDFSService hdfsService = new HDFSService();
        hdfsService.startThread();
    }

}
