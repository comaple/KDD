package com.easyminning.tag;

import com.mongodb.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/9/26.
 */
public class HotTagService extends AbstractService<HotTag> {

    private static HotTagService hotTagService = new HotTagService();

    private HotTagService() {
        this.init();
    }

    public static HotTagService getInstance() {
        return hotTagService;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = "hottag";
    }

    public void save(HotTag hotTag) {
        simpleMongoDBClient2.insert(hotTag);
    }


    public List<HotTag> findHotTagList( Integer pageNo, Integer pageSize) {
        VersionStamp versionStamp = versionStampService.getLatestFinshedVersionStamp();
        if (versionStamp == null) {
            log.error("versionstamp is null");
            return null ;
        }
        QueryBuilder queryBuilder = QueryBuilder.start("versionStamp").is(versionStamp.getVersionStamp());
        QueryBuilder queryBuilderSort = QueryBuilder.start("weight").is(-1);
        List<HotTag> hotTagList = this.simpleMongoDBClient2.select(queryBuilder,queryBuilderSort,(pageNo-1)*pageSize,pageSize,HotTag.class);
        return hotTagList;
    }

    public void saveHotTagList( List<HotTag> hotTagList) {
        VersionStamp versionStamp = versionStampService.getUnFinshedVersionStamp();
//        versionStamp = new VersionStamp();
//        versionStamp.setVersionStamp("201410190000");
        if (versionStamp == null) {
            log.error("versionstamp is null");
            return ;
        }
        List<HotTag> tempList = new ArrayList<HotTag>();
        for (HotTag temp : hotTagList) {
            temp.setVersionStamp(versionStamp.getVersionStamp());
            tempList.add(temp);

            if (tempList.size() % BATCH_SIZE_MAX == 0) {
                simpleMongoDBClient2.insert(tempList);
                tempList.clear();
            }
        }

        if (tempList.size() > 0 ) {
            simpleMongoDBClient2.insert(tempList);
        }
    }
}
