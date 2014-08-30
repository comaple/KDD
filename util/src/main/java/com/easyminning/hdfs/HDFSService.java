package com.easyminning.hdfs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
@Service
public class HDFSService implements Runnable {

    /**
     *
     */
    public static String FS_DEFAULT_NAME = "http://db1:9000";

    public static String DEST_PATH = "/kdd/scraw/";

    public static DateFormat DF = new SimpleDateFormat("yyyyMMdd");

    // 队列大小
    public static int QU_SIZE = 10000;

    public static Log log = LogFactory.getLog(HDFSService.class);

    // 日志队列
    public static BlockingDeque<String> logQueque = new LinkedBlockingDeque<String>(QU_SIZE);

    @PostConstruct
    public void startThread() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public static void put(String filePath) {
        try {
            if (logQueque.size() < QU_SIZE) {
                logQueque.put(filePath);
            } else {
                log.error("文件数过多，丢弃文件:" + filePath);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    @Override
    public void run() {
        UpLoadUtil upLoadUtil = new UpLoadUtil();
        upLoadUtil.init();
        while (true) {
            try {
               String filePath = logQueque.take();
               Date now = new Date();
               String nowStr = DF.format(now);
               log.info("上传文件：" + filePath + " 目标文件夹：" + DEST_PATH + nowStr);
               upLoadUtil.UploadLocalFileToHdfs(filePath, DEST_PATH + nowStr);
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
}
