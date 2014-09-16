package com.easyminning.tag;

import com.easyminning.mongodbclient2.driver.MongoDBDriver;
import com.easyminning.mongodbclient2.sample.SimpleMongoDBClient2;
import com.easyminning.util.simhash.DuplicateDocFilter;
import com.mongodb.*;

import java.util.ArrayList;
import java.util.List;

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


    public void setCollectionName(String collectionName) {
        this.collectionName = "resultdocument";
    }

    public void save(ResultDocument resultDocument) {
        resultDocument.setFingerMsg(DuplicateDocFilter.getAnasysisDocHash(resultDocument));
        resultDocument.setVersionStamp(versionStampService.getUnFinshedVersionStamp().getVersionStamp());
        simpleMongoDBClient2.insert(resultDocument);
    }

    public ResultDocument getDocumentByDocId(String docId) {
        QueryBuilder queryBuilder = QueryBuilder.start("docId").is(docId);
        VersionStamp versionStamp = versionStampService.getLatestFinshedVersionStamp();
        if (versionStamp != null) {
            queryBuilder.and("versionStamp").is(versionStamp.getVersionStamp());
        }
        return simpleMongoDBClient2.selectOne(queryBuilder,ResultDocument.class);
    }

    public void deleteDocIds(List<String> docIds) {
        TagDocService.getInstance().deleteDocIds(docIds);
        this.simpleMongoDBClient2.collection.remove(new BasicDBObject("docId",
                new BasicDBObject(QueryOperators.IN,docIds.toArray())));
    }


    public void updateDocRepeatCount(List<ResultDocument> resultDocuments) {
        for (ResultDocument resultDocument : resultDocuments) {
            this.simpleMongoDBClient2.collection.update(new BasicDBObject("docId", resultDocument.getDocId()),
                    new BasicDBObject("repeatCount", resultDocument.getRepeatCount()),false,false);
        }
    }



    public List<ResultDocument> getFingerMsgList() {
        List<ResultDocument> resultDocuments = new ArrayList<ResultDocument>();
        VersionStamp versionStamp = versionStampService.getLatestFinshedVersionStamp();
        DBCursor dbCursor = null;
        if (versionStamp != null) {
            dbCursor = simpleMongoDBClient2.collection.find(new BasicDBObject("versionStamp",versionStamp.getVersionStamp()),new BasicDBObject("fingerMsg",true).append("docId",true));
        } else {
            dbCursor = simpleMongoDBClient2.collection.find(null,new BasicDBObject("fingerMsg",true).append("docId",true));
        }

        while (dbCursor.hasNext())  {
            DBObject dbObject = dbCursor.next();
            ResultDocument resultDocument = new ResultDocument();
            resultDocument.setFingerMsg(dbObject.get("fingerMsg").toString());
            resultDocument.setDocId(dbObject.get("docId").toString());
            resultDocuments.add(resultDocument);
        }
        //return simpleMongoDBClient2.selectOne(queryBuilder,ResultDocument.class);
        return resultDocuments;
    }


    public static void main(String[] args) {
        ResultDocumentService resultDocumentService = ResultDocumentService.getInstance();

        ResultDocument resultDocument = new ResultDocument();
        resultDocument.setKeyWord("test");
        resultDocument.setWeight(10.0);
        resultDocumentService.save(resultDocument);
    }

}
