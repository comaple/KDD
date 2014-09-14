package com.easyminning.tag;

import com.mongodb.QueryBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
    private Log log = LogFactory.getLog(VersionStampService.class);

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

        //  删除未完成的版本号
        simpleMongoDBClient2.delete(QueryBuilder.start("finishedVersion").is(0));
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
        VersionStamp versionStamp = new VersionStamp();
        versionStamp.setFinishedVersion(0);
        versionStamp.setVersionStamp(dateFormat.format(new Date()));

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
            versionStamp = simpleMongoDBClient2.selectOne(QueryBuilder
                    .start("finishedVersion").is(0),VersionStamp.class);
            if (versionStamp == null) {
                return new VersionStamp();
            }
            return versionStamp;
        } catch (Exception e) {
            e.printStackTrace();
            return new VersionStamp();
        }
    }


    /**
     * 最后一个任务执行后，修改版本号为已完成版本号
     */
    public void updateUnFinishedVersion() {
        VersionStamp versionStamp = this.getUnFinshedVersionStamp();
        if (versionStamp == null || versionStamp.getVersionStamp() == null
                || "".equals(versionStamp.getVersionStamp().trim())) {
            return;
        }
        versionStamp.setFinishedVersion(1);
        versionStampService.simpleMongoDBClient2.marge(
                QueryBuilder.start("_id").is(versionStamp.get_id().get("$oid")), versionStamp);
        log.info("update finished version: " + versionStamp.getVersionStamp());
    }

    public VersionStamp getLatestFinshedVersionStamp() {
        return CURRENT_FINNISHED_VERSION;
    }


    @Override
    public void run() {
       while (true) {
           try {
               List<VersionStamp> list = simpleMongoDBClient2.select(QueryBuilder
                       .start("finishedVersion").is(1), QueryBuilder.start("versionStamp").is(-1),
                       0, 1, VersionStamp.class);

               if (list != null && list.size() > 0) {
                    CURRENT_FINNISHED_VERSION = list.get(0);
               }
            Thread.sleep(1000*60*60*3);
          } catch (Exception e) {
               e.printStackTrace();
               try {
                   Thread.sleep(1000*60*60*3);
               } catch (InterruptedException e1) {
                   e1.printStackTrace();
               }
           }
       }
    }

    public static void main(String[] args) {
        VersionStampService versionStampService = VersionStampService.getInstance();
        versionStampService.genUnFinshedVersionStamp();
        VersionStamp versionstamp = versionStampService.getUnFinshedVersionStamp();
        versionStampService.updateUnFinishedVersion();
    }


}
