package com.easyminning.tag;

import com.mongodb.QueryBuilder;

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

        String version = versionStampService.getUnFinshedVersionStamp().getVersionStamp();
        if (version != null) {
           tagDoc.setVersionStamp(version);
        }
        simpleMongoDBClient2.insert(tagDoc);
    }

    public List<String> findWordAll() {
        List<String> res = simpleMongoDBClient2.collection.distinct("tagItem");
        return res;
    }

    public List<TagDoc> findDocByTag(String tagItem, Integer pageNo, Integer pageSize) {
        QueryBuilder queryBuilder = QueryBuilder.start("tagItem").is(tagItem);
        QueryBuilder queryBuilderSort = QueryBuilder.start("weight").is(-1);
        VersionStamp versionStamp = versionStampService.getLatestFinshedVersionStamp();
        if (versionStamp != null) {
            queryBuilder.and("versionStamp").is(versionStamp.getVersionStamp());
        }
        List<TagDoc> tagDocList = this.simpleMongoDBClient2.select(queryBuilder,queryBuilderSort,pageNo,pageSize,TagDoc.class);
        return tagDocList;
    }



    public static void main(String[] args) {
        TagDocService tagDocService = TagDocService.getInstance();
        tagDocService.save(new TagDoc());

      // docWordWeightService.save(new DocWordWeightModel());
      // List<String> models = tagDocService.findWordAll();

    }

}
