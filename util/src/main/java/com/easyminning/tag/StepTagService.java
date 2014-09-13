package com.easyminning.tag;

import com.mongodb.QueryBuilder;

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
        String version = versionStampService.getUnFinshedVersionStamp().getVersionStamp();
        if (version != null) {
            for (StepTag stepTag : stepTagList) {
                stepTag.setVersionStamp(version);
            }
        }
        this.simpleMongoDBClient2.insert(stepTagList);
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = "steptag";
    }

    public List<StepTag> findAll() {
        List<StepTag> stepTagList = this.simpleMongoDBClient2.select(QueryBuilder.start(),1,10,StepTag.class);
        return stepTagList;
    }


    public List<StepTag> findStepTagByStep(String stepItem, Integer pageNo, Integer pageSize) {
        QueryBuilder queryBuilder = QueryBuilder.start("stepItem").is(stepItem);
        QueryBuilder queryBuilderSort = QueryBuilder.start("weight").is(1);
        VersionStamp versionStamp = versionStampService.getLatestFinshedVersionStamp();
        if (versionStamp != null) {
            queryBuilder.and("versionStamp").is(versionStamp.getVersionStamp());
        }
        List<StepTag> stepTagList = this.simpleMongoDBClient2.select(queryBuilder,queryBuilderSort,pageNo,pageSize,StepTag.class);
        return stepTagList;
    }


    public static void main(String[] args) {
        stepTagService.findAll();
    }




}
