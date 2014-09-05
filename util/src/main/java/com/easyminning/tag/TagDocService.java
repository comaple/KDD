package com.easyminning.tag;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-8-31
 * Time: 下午2:23
 * To change this template use File | Settings | File Templates.
 */
public class TagDocService extends AbstractService<TagDocWeight> {

    private static TagDocService tagDocService = new TagDocService();

    private TagDocService() {
        this.init();
    }

    public static TagDocService getInstance() {
        return tagDocService;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = "docwordweight";
    }

    public void save(TagDocWeight tagDocWeight) {
        simpleMongoDBClient2.insert(tagDocWeight);
    }

    public List<String> findWordAll() {
        List<String> res = simpleMongoDBClient2.collection.distinct("word");
        return res;
    }



    public static void main(String[] args) {
        TagDocService tagDocService = new TagDocService();

        tagDocService.init();

      // docWordWeightService.save(new DocWordWeightModel());
       List<String> models = tagDocService.findWordAll();

    }

}
