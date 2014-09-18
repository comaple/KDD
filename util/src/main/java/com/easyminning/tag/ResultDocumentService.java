package com.easyminning.tag;

import com.easyminning.mongodbclient2.driver.MongoDBDriver;
import com.easyminning.mongodbclient2.sample.SimpleMongoDBClient2;
import com.easyminning.util.simhash.DuplicateDocFilter;
import com.easyminning.util.simhash.SimHash;
import com.mongodb.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
                    new BasicDBObject("$set",new BasicDBObject("repeatCount", resultDocument.getRepeatCount())), false, false);
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


    /**
     * 返回热门文章列表
     * @param pageNo
     * @param pageSize
     * @return
     */
    public List<ResultDocument> getHotDocList(Integer pageNo, Integer pageSize) {
        QueryBuilder queryBuilder = QueryBuilder.start();
        QueryBuilder queryBuilderSort = QueryBuilder.start("repeatCount").is(-1);
        VersionStamp versionStamp = versionStampService.getLatestFinshedVersionStamp();
        if (versionStamp != null) {
            queryBuilder.and("versionStamp").is(versionStamp.getVersionStamp());
        }
        List<ResultDocument> resultDocumentList  = this.simpleMongoDBClient2.select(queryBuilder,queryBuilderSort,
                (pageNo-1)*pageSize,pageSize,ResultDocument.class);
        return resultDocumentList;
    }


    public static void main(String[] args) {
        ResultDocumentService resultDocumentService = ResultDocumentService.getInstance();

        ResultDocument resultDocument1 = new ResultDocument();
        resultDocument1.setDocId(UUID.randomUUID().toString());
        resultDocument1.setKeyWord("test");
        resultDocument1.setWeight(10.0);
        resultDocument1.setSourceContent("zheshi yige meili de guojia ");
        resultDocumentService.save(resultDocument1);

        ResultDocument resultDocument2 = new ResultDocument();
        resultDocument2.setDocId(UUID.randomUUID().toString());
        resultDocument2.setKeyWord("test");
        resultDocument2.setWeight(10.0);
        resultDocument2.setResult("\"假设输入的是一个文档的特征集合，每个特征有一定的权重。\"\n" +
                "                + \"传统干扰4的 hash 算法只负责将原始内容尽量均匀随机地映射为一个签名值，\"\n" +
                "                + \"原理上这次差异有多大呢3相当于伪随机数产生算法。产生的两个签名，如果相等，\"\n" +
                "                + \"说明原始内容在一定概 率 下是相等的；如果不相等，除了说明原始内容不相等外，不再提供任何信息，\"");
        resultDocument2.setSourceContent("zheshi yige meili de guojia ");
        resultDocumentService.save(resultDocument2);

        ResultDocument resultDocument3 = new ResultDocument();
        resultDocument3.setDocId(UUID.randomUUID().toString());
        resultDocument3.setKeyWord("test");
        resultDocument3.setWeight(10.0);
        resultDocument3.setResult("imhash算法的输入是一个向量，输出是一个 f 位的签名值。为了陈述方便，"
                + "假设输入的是一个文档的特征集合，每个特征有一定的权重。"
                + "传统干扰4的 hash 算法只负责将原始内容尽量均匀随机地映射为一个签名值，");
        resultDocument3.setSourceContent("imhash算法的输入是一个向量，输出是一个 f 位的签名值。为了陈述方便，"
                + "假设输入的是一个文档的特征集合，每个特征有一定的权重。"
                + "传统干扰4的 hash 算法只负责将原始内容尽量均匀随机地映射为一个签名值，"
                + "原理上这次差异有多大呢3相当于伪随机数产生算法。产生的两个签名，如果相等，"
                + "说明原始内容在一定概 率 下是相等的；如果不相等，除了说明原始内容不相等外，不再提供任何信息，"
                + "因为即使原始内容只相差一个字节，所产生的签名也很可能差别极大。从这个意义 上来 说，"
                + "要设计一个 hash 算法，对相似的内容产生的签名也相近，是更为艰难的任务，因为它的签名值除了提供原始"
                + "内容是否相等的信息外，干扰1还能额外提供不相等的 原始再来干扰2内容的差异程度的信息。");
        resultDocumentService.save(resultDocument3);



    }

}
