package com.easyminning.tag;

import com.easyminning.mongodbclient2.driver.MongoDBDriver;
import com.easyminning.mongodbclient2.sample.SimpleMongoDBClient2;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by Administrator on 2014/9/5.
 */
public abstract class AbstractService<E> {
    protected   String dataBaseName = "kdd";
    protected   String collectionName = "";

    protected SimpleMongoDBClient2<E> simpleMongoDBClient2;

    protected VersionStampService versionStampService = VersionStampService.getInstance();

    protected static Log log = LogFactory.getLog(AbstractService.class);

    protected int BATCH_SIZE_MAX = 500;

    protected void init() {
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

    protected abstract void setCollectionName(String collectionName);

    public void setDataBaseName(String dataBaseName) {
        //
    }

}
