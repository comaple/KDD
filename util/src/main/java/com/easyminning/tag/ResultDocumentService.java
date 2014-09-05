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
public class ResultDocumentService extends AbstractService<ResultDocument> {

    private static ResultDocumentService resultDocumentService = new ResultDocumentService();

    private ResultDocumentService() {
        this.init();
    }

    public static ResultDocumentService getInstance() {
        return resultDocumentService;
    }


    public SimpleMongoDBClient2<ResultDocument> simpleMongoDBClient2;

    public void setCollectionName(String collectionName) {
        this.collectionName = "resultdocument";
    }



    public void save(ResultDocument resultDocument) {
        simpleMongoDBClient2.insert(resultDocument);
    }

    public ResultDocument getDocumentByDocId(String docId) {
        return simpleMongoDBClient2.selectOne(QueryBuilder.start("docId").is(docId),ResultDocument.class);
    }

    public static void main(String[] args) {
        ResultDocumentService resultDocumentService = new ResultDocumentService();
        resultDocumentService.init();


        ResultDocument resultDocument = new ResultDocument();
        resultDocument.setKeyWord("test");
        resultDocumentService.save(resultDocument);
    }

}
