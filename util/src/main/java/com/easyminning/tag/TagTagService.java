package com.easyminning.tag;

import com.easyminning.mongodbclient2.driver.MongoDBDriver;
import com.easyminning.mongodbclient2.sample.SimpleMongoDBClient2;
import com.easyminning.tag.TagTag;
import com.mongodb.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-8-30
 * Time: 下午3:14
 * To change this template use File | Settings | File Templates.
 */
public class TagTagService extends AbstractService<TagTag> {

    private static TagTagService tagTagService = TagTagService.getInstance();

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
        this.simpleMongoDBClient2.insert(tagTag);
    }

    public void saveTagTagList(List<TagTag> tagTagList) {
        this.simpleMongoDBClient2.insert(tagTagList);
    }

    public List<TagTag> findTagByTag(String tagItem, Integer pageNo, Integer pageSize) {
        QueryBuilder queryBuilder = QueryBuilder.start("tagItem").is(tagItem);
        QueryBuilder queryBuilderSort = QueryBuilder.start("weight").is(1);
        List<TagTag> tagTagList = this.simpleMongoDBClient2.select(queryBuilder,queryBuilderSort,pageNo,pageSize,TagTag.class);
        return tagTagList;
    }
}


