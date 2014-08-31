package com.easyminning.tag;

import com.easyminning.mongodbclient2.driver.MongoDBDriver;
import com.easyminning.mongodbclient2.sample.SimpleMongoDBClient2;
import com.mongodb.QueryBuilder;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-8-31
 * Time: 下午2:23
 * To change this template use File | Settings | File Templates.
 */
public class ResultDocumentService {

    private static String DATABASE_NEME = "kdd";
    private static String COLLECTION_NAME = "resultdocument";

    public SimpleMongoDBClient2<ResultDocument> simpleMongoDBClient2;

    public void init() {
        MongoDBDriver mongoDBDriver = new MongoDBDriver();
        mongoDBDriver.setConfigFile("configuration-util.properties");
        mongoDBDriver.init();

        simpleMongoDBClient2 = new SimpleMongoDBClient2<ResultDocument>();
        simpleMongoDBClient2.setDriver(mongoDBDriver);
        simpleMongoDBClient2.setDataBaseName(DATABASE_NEME);
        simpleMongoDBClient2.setCollectionName(COLLECTION_NAME);
        simpleMongoDBClient2.init();
    }

    public void save(ResultDocument resultDocument) {
        simpleMongoDBClient2.save(QueryBuilder.start(), resultDocument);
    }


    public static void main(String[] args) {
        ResultDocumentService resultDocumentService = new ResultDocumentService();
        resultDocumentService.init();


        ResultDocument resultDocument = new ResultDocument();
        resultDocument.setKeyWord("test");
        resultDocumentService.save(resultDocument);
    }

}
