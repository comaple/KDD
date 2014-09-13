package com.easyminning.tag;

import com.mongodb.QueryBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-9-10
 * Time: 下午10:53
 * To change this template use File | Settings | File Templates.
 */
public class VersionStampService extends AbstractService<VersionStamp> implements Runnable {

    private static VersionStampService versionStampService = new VersionStampService();

    // current finished version
    public static VersionStamp CURRENT_FINNISHED_VERSION;
    private VersionStampService() {
        this.init();
        Thread thread = new Thread(this);
        thread.start();
    }

    public static VersionStampService getInstance() {
        return versionStampService;
    }


    public void setCollectionName(String collectionName) {
        this.collectionName = "versionstamp";
    }


    /**
     * 第一个任务开始执行时，生成新的版本号
     * @return
     */
    public VersionStamp genUnFinshedVersionStamp() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        VersionStamp versionStamp = new VersionStamp();
        versionStamp.setFinshedVersion(0);
        versionStamp.setVersionStamp(dateFormat.format(new Date()));
        simpleMongoDBClient2.delete(QueryBuilder.start("finshedVersion").is(0));
        simpleMongoDBClient2.insert(versionStamp);
        return versionStamp;
    }

    /**
     *  中间任务获取第一个任务生成的版本号
     * @return
     */
    public VersionStamp getUnFinshedVersionStamp() {
        VersionStamp versionStamp = null;

        //
        try {
            List<VersionStamp> list = simpleMongoDBClient2.select(QueryBuilder
                    .start("finshedVersion").is(0),QueryBuilder.start().is(1),1,1,VersionStamp.class);
            if (list == null || list.size() == 0) {
                return new VersionStamp();
            }
            return list.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return new VersionStamp();
        }
    }


    /**
     * 最后一个任务执行后，修改版本号为已完成版本号
     */
    public void updateUnFishedVersion() {
        VersionStamp versionStamp = this.getUnFinshedVersionStamp();
        simpleMongoDBClient2.delete(QueryBuilder.start("finshedVersion").is(0));
        simpleMongoDBClient2.insert(versionStamp);

    }

    public VersionStamp getLatestFinshedVersionStamp() {
        return CURRENT_FINNISHED_VERSION;
    }


    @Override
    public void run() {
       while (true) {
           List<VersionStamp> list = simpleMongoDBClient2.select(QueryBuilder
                   .start(), QueryBuilder.start().is(1), 1, 1, VersionStamp.class);
           CURRENT_FINNISHED_VERSION = list.get(0);
          try {
            Thread.sleep(1000*60*60*3);
          } catch (Exception e) {
              e.printStackTrace();
          }
       }
    }
}
