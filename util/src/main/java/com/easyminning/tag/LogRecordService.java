package com.easyminning.tag;

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
}
