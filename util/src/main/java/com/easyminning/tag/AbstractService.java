package com.easyminning.tag;

import com.easyminning.mongodbclient2.driver.MongoDBDriver;
import com.easyminning.mongodbclient2.sample.SimpleMongoDBClient2;

/**
 * Created by Administrator on 2014/9/5.
 */
public abstract class AbstractService<E> {
    protected   String dataBaseName = "kdd";
    protected   String collectionName = "";

    protected SimpleMongoDBClient2<E> simpleMongoDBClient2;

    public void init() {
        MongoDBDriver mongoDBDriver = new MongoDBDriver();
        mongoDBDriver.setConfigFile("configuration-util.properties");
        mongoDBDriver.init();

        simpleMongoDBClient2 = new SimpleMongoDBClient2<E>();
        simpleMongoDBClient2.setDriver(mongoDBDriver);
        simpleMongoDBClient2.setDataBaseName(dataBaseName);
        this.setCollectionName("");
        simpleMongoDBClient2.setCollectionName(collectionName);
        simpleMongoDBClient2.init();
    }

    public abstract void setCollectionName(String collectionName);

    public void setDataBaseName(String dataBaseName) {
        //
    }

}
