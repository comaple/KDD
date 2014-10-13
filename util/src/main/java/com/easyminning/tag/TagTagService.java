package com.easyminning.tag;

import com.easyminning.mongodbclient2.driver.MongoDBDriver;
import com.easyminning.mongodbclient2.sample.SimpleMongoDBClient2;
import com.easyminning.tag.TagTag;
import com.mongodb.BasicDBObject;
import com.mongodb.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-8-30
 * Time: 下午3:14
 * To change this template use File | Settings | File Templates.
 */
public class TagTagService extends AbstractService<TagTag> {

    private static TagTagService tagTagService = new TagTagService();

    private TagTagService() {
        this.init();
    }

    public static TagTagService getInstance() {
        return tagTagService;
    }

    @Override
    protected void setCollectionName(String collectionName) {
        this.collectionName = "tagtag";
    }

    public void saveTagTag(TagTag tagTag) {
        VersionStamp versionStamp = versionStampService.getUnFinshedVersionStamp();

        if (versionStamp == null) {
            log.error("versionstamp is null");
            return ;
        }

        tagTag.setVersionStamp(versionStamp.getVersionStamp());
        this.simpleMongoDBClient2.insert(tagTag);
    }

    public void saveTagTagList(List<TagTag> tagTagList) {
        VersionStamp versionStamp = versionStampService.getUnFinshedVersionStamp();

        if (versionStamp == null) {
            log.error("versionstamp is null");
            return ;
        }

        List<TagTag> tempList = new ArrayList<TagTag>();
        for (TagTag tagTag : tagTagList) {
            tagTag.setVersionStamp(versionStamp.getVersionStamp());
            tempList.add(tagTag);

            //
            if (tempList.size() % BATCH_SIZE_MAX == 0) {
                this.simpleMongoDBClient2.insert(tempList);
                tempList.clear();
            }
        }

        if (tempList.size()>0) {
            this.simpleMongoDBClient2.insert(tempList);
        }

    }

    public List<TagTag> findHotTag(Integer pageNo, Integer pageSize) {
        VersionStamp versionStamp = versionStampService.getLatestFinshedVersionStamp();
        if (versionStamp == null) {
            return new ArrayList<TagTag>();
        }

        QueryBuilder queryBuilder = QueryBuilder.start("versionStamp").is(versionStamp.getVersionStamp());
        QueryBuilder queryBuilderSort = QueryBuilder.start("weight").is(-1);

        List<TagTag> tagTagList = this.simpleMongoDBClient2.select(queryBuilder,queryBuilderSort,pageNo,pageSize,TagTag.class);
        return tagTagList;
    }


    public List<TagTag> findTagByTag(String tagItem, Integer pageNo, Integer pageSize) {
        VersionStamp versionStamp = versionStampService.getLatestFinshedVersionStamp();
        if (versionStamp == null) {
            return new ArrayList<TagTag>();
        }

        QueryBuilder queryBuilder = QueryBuilder.start("tagItem").is(tagItem);
        QueryBuilder queryBuilderSort = QueryBuilder.start("weight").is(-1);
        queryBuilder.and("versionStamp").is(versionStamp.getVersionStamp());

        List<TagTag> tagTagList = this.simpleMongoDBClient2.select(queryBuilder,queryBuilderSort,pageNo,pageSize,TagTag.class);
        return tagTagList;
    }

    public List<TagTag> findTagByTag(String[] tagItem, Integer pageNo, Integer pageSize) {
        VersionStamp versionStamp = versionStampService.getLatestFinshedVersionStamp();
        if (versionStamp == null) {
            return new ArrayList<TagTag>();
        }

        QueryBuilder queryBuilder = QueryBuilder.start("tagItem").in(tagItem);
        QueryBuilder queryBuilderSort = QueryBuilder.start("weight").is(-1);
        queryBuilder.and("versionStamp").is(versionStamp.getVersionStamp());
        List<TagTag> tagTagList = this.simpleMongoDBClient2.select(queryBuilder,queryBuilderSort,pageNo,pageSize,TagTag.class);
        return tagTagList;
    }
}


