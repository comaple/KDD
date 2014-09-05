package com.easyminning.etl.mahout.writable;

import com.easyminning.mongodbclient2.driver.MongoDBDriver;
import com.easyminning.mongodbclient2.sample.SimpleMongoDBClient2;
import com.easyminning.tag.AbstractService;
import com.easyminning.tag.ResultDocument;
import com.mongodb.QueryBuilder;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-8-31
 * Time: 下午2:23
 * To change this template use File | Settings | File Templates.
 */
public class DocWordWeightService extends AbstractService<DocWordWeightModel> {

    public void setCollectionName(String collectionName) {
        this.collectionName = "docwordweight";
    }

    public void save(DocWordWeightModel docWordWeightModel) {
        simpleMongoDBClient2.insert(docWordWeightModel);
    }

    public List<DocWordWeightModel> findAll() {
        List<DocWordWeightModel> docWordWeightModelList = simpleMongoDBClient2.select(QueryBuilder.start(),
                1,Integer.MAX_VALUE, DocWordWeightModel.class);
        return docWordWeightModelList;
    }


    public static void main(String[] args) {
        DocWordWeightService docWordWeightService = new DocWordWeightService();

        docWordWeightService.init();

       docWordWeightService.save(new DocWordWeightModel());
       List<DocWordWeightModel> models = docWordWeightService.findAll();
    }

}
