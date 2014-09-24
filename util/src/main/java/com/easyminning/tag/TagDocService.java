package com.easyminning.tag;

import com.mongodb.BasicDBObject;
import com.mongodb.QueryBuilder;
import com.mongodb.QueryOperators;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-8-31
 * Time: 下午2:23
 * To change this template use File | Settings | File Templates.
 */
public class TagDocService extends AbstractService<TagDoc> {

    private static TagDocService tagDocService = new TagDocService();

    private TagDocService() {
        this.init();
    }

    public static TagDocService getInstance() {
        return tagDocService;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = "docwordweight";
    }

    public void save(TagDoc tagDoc) {
        VersionStamp versionStamp = versionStampService.getUnFinshedVersionStamp();
        if (versionStamp == null) {
            log.error("versionstamp is null");
            return ;
        }

        tagDoc.setVersionStamp(versionStamp.getVersionStamp());
        simpleMongoDBClient2.insert(tagDoc);
    }

    public void saveList(List<TagDoc> tagDocList) {
        VersionStamp versionStamp = versionStampService.getUnFinshedVersionStamp();
        if (versionStamp == null) {
            log.error("versionstamp is null");
            return ;
        }
        List<TagDoc> tempList = new ArrayList<TagDoc>();
        for (TagDoc temp : tagDocList) {
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

    public List<String> findWordAll() {
        VersionStamp versionStamp = versionStampService.getUnFinshedVersionStamp();
        if (versionStamp == null) {
            log.error("versionstamp is null");
            return null;
        }
        List<String> res = simpleMongoDBClient2.collection.distinct("tagItem",new BasicDBObject("versionStamp", versionStamp.getVersionStamp()));
        return res;
    }

    public void deleteDocIds(List<String> docIds,VersionStamp versionStamp) {
        BasicDBObject queryCondition = new BasicDBObject();
        queryCondition.put("versionStamp", versionStamp.getVersionStamp());
        queryCondition.put("docItem",new BasicDBObject(QueryOperators.IN,docIds.toArray()));
        this.simpleMongoDBClient2.collection.remove(queryCondition);
    }

//    public List<TagDoc> findDocByTag(String tagItem, Integer pageNo, Integer pageSize) {
//        QueryBuilder queryBuilder = QueryBuilder.start("tagItem").is(tagItem);
//        QueryBuilder queryBuilderSort = QueryBuilder.start("weight").is(-1);
//        VersionStamp versionStamp = versionStampService.getLatestFinshedVersionStamp();
//        if (versionStamp != null) {
//            queryBuilder.and("versionStamp").is(versionStamp.getVersionStamp());
//        }
//        List<TagDoc> tagDocList = this.simpleMongoDBClient2.select(queryBuilder,queryBuilderSort,pageNo,pageSize,TagDoc.class);
//        return tagDocList;
//    }

    public List<TagDoc> findDocByTag(String[] tagItem, Integer pageNo, Integer pageSize) {
        QueryBuilder queryBuilder = QueryBuilder.start("tagItem").in(tagItem);
        QueryBuilder queryBuilderSort = QueryBuilder.start("weight").is(-1);
        VersionStamp versionStamp = versionStampService.getLatestFinshedVersionStamp();
        if (versionStamp == null) {
            log.error("versionstamp is null");
            return new ArrayList<TagDoc>();
        }

        queryBuilder.and("versionStamp").is(versionStamp.getVersionStamp());
        List<TagDoc> tagDocList = this.simpleMongoDBClient2.select(queryBuilder,queryBuilderSort,pageNo,pageSize,TagDoc.class);
        return tagDocList;
    }




    public static void main(String[] args) {
        TagDocService tagDocService = TagDocService.getInstance();
      //  tagDocService.save(new TagDoc());

      // docWordWeightService.save(new DocWordWeightModel());
      // List<String> models = tagDocService.findWordAll();

    }

}
