package com.easyminning.tag;

import com.mongodb.QueryBuilder;

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
public class VersionStampService extends AbstractService<VersionStamp> {

    private static VersionStampService versionStampService = new VersionStampService();

    private  static ConcurrentHashMap<String, String> lastestVersion = new ConcurrentHashMap<String, String>();

    private VersionStampService() {
        this.init();
    }

    public static VersionStampService getInstance() {
        return versionStampService;
    }


    public void setCollectionName(String collectionName) {
        this.collectionName = "versionstamp";
    }


    public void save(VersionStamp versionStamp) {
        simpleMongoDBClient2.insert(versionStamp);
    }

    public VersionStamp getLatestVersionStamp() {
        VersionStamp versionStamp = null;
        if (lastestVersion.size() == 0) {
            List<VersionStamp> list = simpleMongoDBClient2.select(QueryBuilder.start(),QueryBuilder.start().is(1),1,1,VersionStamp.class);
            if (list != null && list.size() ==0) {
                return null;
            }
            lastestVersion.put("version", list.get(0).getVersionStamp() + "," + System.currentTimeMillis());
            return list.get(0);
        }

        String version = lastestVersion.get("version").split(",")[0];
        String time = lastestVersion.get("version").split(",") [1];
        if ((System.currentTimeMillis() - Long.parseLong(time))/1000 < 60*60*3) {
            versionStamp = new VersionStamp();
            versionStamp.setVersionStamp(version);
            return versionStamp;
        }

        //
        List<VersionStamp> list = simpleMongoDBClient2.select(QueryBuilder.start(),QueryBuilder.start().is(1),1,1,VersionStamp.class);
        if (list != null && list.size() ==0) {
            return null;
        }
        lastestVersion.put("version", list.get(0).getVersionStamp() + "," + System.currentTimeMillis());
        return list.get(0);
    }

}
