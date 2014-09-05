package com.easyminning.tag;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-8-30
 * Time: 下午3:14
 * To change this template use File | Settings | File Templates.
 */
public class StepTagService extends AbstractService<StepTag> {

    private static StepTagService stepTagService = new StepTagService();

    private StepTagService() {
        this.init();
    }

    public static StepTagService getInstance() {
        return stepTagService;
    }


    public void saveList(List<StepTag> stepTagList) {
        this.simpleMongoDBClient2.insert(stepTagList);
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = "steptag";
    }


}
