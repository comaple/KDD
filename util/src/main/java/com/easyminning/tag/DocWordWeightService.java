package com.easyminning.tag;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-8-31
 * Time: 下午2:23
 * To change this template use File | Settings | File Templates.
 */
public class DocWordWeightService extends AbstractService<DocWordWeightModel> {

    private static DocWordWeightService docWordWeightService = new DocWordWeightService();

    private DocWordWeightService() {
        this.init();
    }

    public static DocWordWeightService getInstance() {
        return docWordWeightService;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = "docwordweight";
    }

    public void save(DocWordWeightModel docWordWeightModel) {
        simpleMongoDBClient2.insert(docWordWeightModel);
    }

    public List<String> findWordAll() {
        List<String> res = simpleMongoDBClient2.collection.distinct("word");
        return res;
    }



    public static void main(String[] args) {
        DocWordWeightService docWordWeightService = new DocWordWeightService();

        docWordWeightService.init();

      // docWordWeightService.save(new DocWordWeightModel());
       List<String> models = docWordWeightService.findWordAll();

    }

}
