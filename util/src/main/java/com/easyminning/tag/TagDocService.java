package com.easyminning.tag;

import com.easyminning.mongodbclient2.driver.MongoDBDriver;
import com.easyminning.mongodbclient2.sample.SimpleMongoDBClient2;
import com.easyminning.tag.TagDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-8-30
 * Time: 下午3:14
 * To change this template use File | Settings | File Templates.
 */
@Service
public class TagDocService extends SimpleMongoDBClient2<TagDoc> {

    @PostConstruct
    public void init() {
        super.setDataBaseName("kdd");
        super.setCollectionName("tagdoc");
        super.init();
    }

    @Autowired
    public void setDriver(MongoDBDriver driver) {
        super.setDriver(driver);
    }

}
