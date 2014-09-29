package com.easyminning.tag;

import com.mongodb.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/9/26.
 */
public class LogRecordService extends AbstractService<LogRecord> {

    private static LogRecordService logRecordService = new LogRecordService();

    private LogRecordService() {
        this.init();
    }

    public static LogRecordService getInstance() {
        return logRecordService;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = "logRecord";
    }

    public void save(LogRecord logRecord) {
        simpleMongoDBClient2.insert(logRecord);
    }

    public List<LogRecord> findListByType(String type, Integer pageNo, Integer pageSize) {


        QueryBuilder queryBuilder = QueryBuilder.start("type").is(type);
        QueryBuilder queryBuilderSort = QueryBuilder.start("operTime").is(-1);
        List<LogRecord> logRecordList = this.simpleMongoDBClient2.select(queryBuilder,queryBuilderSort,pageNo,pageSize,LogRecord.class);
        return logRecordList;
    }
}
