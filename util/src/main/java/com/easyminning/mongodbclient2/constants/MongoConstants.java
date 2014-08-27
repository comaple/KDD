package com.easyminning.mongodbclient2.constants;

public class MongoConstants {
  /**
   * MONGO日志
   */
  public static final String COMMON_MONGO = "MONGODB";

  /**
   * 单次批量插入操作:允许最大数据量
   */
  public final static int INSERT_LIST_MAX_SIZE = 1000;

  //配置默认值
  public final static String DEFAULT_MONGO_DB_HOSTNAME = "localhost";
  public final static String DEFAULT_MONGO_DB_PORT = "27017";
  public final static String DEFAULT_MONGO_DB_DATABASE_NAME = "test";
  public final static String DEFAULT_CONNECTIONS_PER_HOST = "500";
  public final static String DEFAULT_MIN_POOL_SIZE = "50";
  public final static String DEFAULT_THREADSALLOWEDTOBLOCK_FOR_CONNECTIONMULTIPLIER = "20";
  
  public final static String DEFAULT_MAX_WAIT_TIME = "120000";
  public final static String DEFAULT_CONNECT_TIMEOUT = "0";
  public final static String DEFAULT_SOCKET_TIMEOUT = "0";
  public final static String DEFAULT_AUTO_CONNECT_RETRY = "false";
}
