package com.easyminning.tag;

import com.mongodb.QueryBuilder;

import java.util.ArrayList;
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

    public void setCollectionName(String collectionName) {
        this.collectionName = "steptag";
    }

    public void saveList(List<StepTag> stepTagList) {
        VersionStamp versionStamp = versionStampService.getUnFinshedVersionStamp();
        if (versionStamp == null) {
            log.error("versionstamp is null");
            return ;
        }
        List<StepTag> tempList = new ArrayList<StepTag>();

        for (StepTag stepTag : stepTagList) {
            stepTag.setVersionStamp(versionStamp.getVersionStamp());
            tempList.add(stepTag);

            //
            if (tempList.size() % BATCH_SIZE_MAX == 0) {
                this.simpleMongoDBClient2.insert(tempList);
                tempList.clear();
            }
        }

        if (tempList.size()>0) {
            this.simpleMongoDBClient2.insert(tempList);
        }
    }

//
//
//    public List<StepTag> findAll() {
//        List<StepTag> stepTagList = this.simpleMongoDBClient2.select(QueryBuilder.start(),0,10,StepTag.class);
//        return stepTagList;
//    }


    public List<StepTag> findStepTagByStep(String stepItem, Integer pageNo, Integer pageSize) {
        VersionStamp versionStamp = versionStampService.getUnFinshedVersionStamp();
        if (versionStamp == null) {
            log.error("versionstamp is null");
            return new ArrayList<StepTag>();
        }

        QueryBuilder queryBuilder = QueryBuilder.start("stepItem").is(stepItem);
        QueryBuilder queryBuilderSort = QueryBuilder.start("weight").is(1);
        queryBuilder.and("versionStamp").is(versionStamp.getVersionStamp());
        List<StepTag> stepTagList = this.simpleMongoDBClient2.select(queryBuilder,queryBuilderSort,(pageNo-1)*pageSize,pageSize,StepTag.class);
        return stepTagList;
    }


    public static void main(String[] args) {
        //stepTagService.findAll();
    }




}
