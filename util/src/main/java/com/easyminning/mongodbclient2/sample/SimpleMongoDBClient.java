package com.easyminning.mongodbclient2.sample;

import com.easyminning.mongodbclient2.MongoDBClient;
import com.easyminning.mongodbclient2.util.DateUtil;
import com.easyminning.mongodbclient2.util.JsonUtil;
import com.mongodb.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

public class SimpleMongoDBClient<E> implements MongoDBClient<E>{
  /**
   * 单次批量插入操作:允许最大数据量
   */
  public final static  int            INSERT_LIST_MAX_SIZE = 1000;
  /**
   * 开启连接的索引值
   */
  private static       long           globalIndex = 0;
  /**
   * 本实例的索引值
   */
  private              long           myIndex = 0;
  /**
   * 数据库地址
   */
  private String       hostname       = DEFAULT_MONGO_DB_HOSTNAME;
  /**
   * 数据库端口
   */
  private String       port           = DEFAULT_MONGO_DB_PORT;
  /**
   * 数据库名称
   */
  private String       databaseName   = DEFAULT_MONGO_DB_DATABASE_NAME;
  /**
   * 表名称
   */
  private String       collectionName = DEFAULT_MONGO_DB_COLLECTION_NAME;
  /**
   * 用户名
   */
  private String       userName       = null;
  /**
   * 密码
   */
  private String       password       = null;
  /**
   * 日志名称
   */
  private String       logName        = null;
  /**
   * 记录日志对象
   */
  private Logger       logger         = null;
  /**
   * Mongo对象
   */
  private Mongo        mongo          = null;
  /**
   * Address
   */
  private ServerAddress address       = null;
  /**
   * 数据库对象
   */
  private DB           database       = null;
  /**
   * 数据表对象
   */
  private DBCollection collection     = null;
  /**
   * 配置文件名
   */
  private String configFile           = null;
  /**
   * 配置对象
   */
  private Properties mongoConfig      = null;
  
  private String isReconstructMongoId  = "false";
  private String connectionsPerHost    = "4000";
  private String threadsAllowedToBlockForConnectionMultiplier = "100";
  
  public SimpleMongoDBClient(Properties configProperties)
  {
    globalIndex++;
    myIndex = globalIndex;
    this.mongoConfig = configProperties;
    load(configProperties);
    if(logName==null||"".equals(logName))
      this.logger = Logger.getLogger(SimpleMongoDBClient.class);
    else
      this.logger = Logger.getLogger(logName);
    logger.info("SimpleMongoDBClient2(String "+configProperties+") create index="+myIndex);
  }
  public SimpleMongoDBClient(String configFile){
    globalIndex++;
    myIndex = globalIndex;
    this.configFile = configFile;
    load(configFile);
    if(logName==null||"".equals(logName))
      this.logger = Logger.getLogger(SimpleMongoDBClient.class);
    else
      this.logger = Logger.getLogger(logName);
    logger.info("SimpleMongoDBClient2("+configFile+") create index="+myIndex);
  }
  @Override
  public DBCollection getCollection() {
    return collection;
  }
  public void requestStart(){
    //mongo.getConnector().requestStart();
  }
  public void requestDone(){
//    mongo.getConnector().requestDone();
//    DBPortPool pool = mongo.getConnector().getDBPortPool(address);
//    DBPort port = pool.get();
//    pool.cleanup(port);
  }
  @Override
  public void close()
  {
    logger.info("SimpleMongoDBClient2("+(configFile==null?mongoConfig:configFile)+") close index="+myIndex);
    mongo.close();
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <T> List<T> distinct(String key,QueryBuilder query) {
    List<T> result = this.collection.distinct(key, query.get());
    return result;
  }
  @Override
  public <G> List<G> group(DBObject key, DBObject condition, DBObject initial,
      String reduce, String finalize,Class<G> gClass) {
    DBObject result = collection.group(key, condition, initial, reduce, finalize);
    JsonUtil<G> util = new JsonUtil<G>();
    List<G> resultList = util.toObjectList(result.toString(), List.class, gClass);
    return resultList;
  }
  @Override
  public boolean delete(QueryBuilder condition) {
    WriteResult result = collection.remove(condition.get());
    if(result.getError()!=null){
      logger.error("MongoDB 删除异常:"+result.getError());
      return false;
    }
    else{
      return true;
    }
  }

  @Override
  public boolean delete(E bean) {
    JsonUtil<E> util = new JsonUtil<E>();
    DBObject dbObj = util.toDBObject(bean);
    
    WriteResult result = collection.remove(dbObj);
    if(result.getError()!=null){
      logger.error("MongoDB 删除异常:"+result.getError());
      return false;
    }
    else{
      return true;
    }
  }

  @Override
  public boolean insert(E bean) {
    JsonUtil<E> util = new JsonUtil<E>();
    DBObject dbObj = util.toDBObject(bean);
    WriteResult result = collection.insert(dbObj);
    if(result.getError()!=null){
      logger.error("MongoDB 插入异常:"+result.getError());
      return false;
    }
    else{
      return true;
    }
  }

  @Override
  public boolean insert(List<E> beanCollection) {
    boolean isSuccess = false;
    //System.out.println(beanCollection);
    if(beanCollection==null||beanCollection.size()<INSERT_LIST_MAX_SIZE){
      JsonUtil<E> util = new JsonUtil<E>();
      WriteResult result = collection.insert(util.toDBObjectList(beanCollection));
      if(result.getError()!=null){
        logger.error("MongoDB 批量插入异常:"+result.getError());
        isSuccess = false;
      }
      else{
        isSuccess = true;
      }
    }
    else{
      logger.error("MongoDB 批量插入异常:数据量超出最大值或数据为空");
      isSuccess = false;
    }
    return isSuccess;
  }
  
  @Override
  public boolean insertOrUpdate(QueryBuilder condition, E bean,
      Map<String, String> attributeOperationMap) {
    boolean isSuccess = false;
    JsonUtil<E> util = new JsonUtil<E>();
    if(collection.insert(util.toDBObject(bean)).getError()!=null)
    {
      if(update(condition, bean, attributeOperationMap))
        isSuccess = true;
    }
    else{
      isSuccess = true;
    }
    return isSuccess;
  }
  
  @Override
  public boolean marge(QueryBuilder condition, E bean) {
    return save(condition,bean);
  }

  @Override
  public boolean save(QueryBuilder condition, E bean) {
    JsonUtil<E> util = new JsonUtil<E>();
    DBObject oldDBObj = collection.findOne(condition.get());
    DBObject newDBObj = util.toDBObject(bean);
    if(oldDBObj!=null)
      newDBObj.put("_id", oldDBObj.get("_id"));
    WriteResult result = collection.save(newDBObj);
    if(result.getError()!=null){
      logger.error("MongoDB save异常:"+result.getError());
      return false;
    }
    else{
      return true;
    }
  }

  @Override
  public List<E> select(QueryBuilder condition, int index, int size,Class<E> beanClass) {
    
    List<DBObject> dbObjList = collection.find(condition.get(), reconstructMongoId()).skip(index).limit(size).toArray();
    JsonUtil<E> util = new JsonUtil<E>();
    List<E> result = util.toObjectList(dbObjList, List.class, beanClass);
    return result;
  }

  @Override
  public long selectCount(QueryBuilder condition) {
    return collection.count(condition.get());
  }

  @Override
  public E selectOne(QueryBuilder condition,Class<E> beanClass) {
    
    DBObject dbObj = collection.findOne(condition.get(),reconstructMongoId());
    JsonUtil<E> util = new JsonUtil<E>();
    return dbObj!=null?util.toObject(dbObj.toString(), beanClass):null;
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public boolean update(QueryBuilder condition, E bean,Map<String, String> attributeOperationMap) {
    boolean isSuccess = true;
    //oneself
    for(Field f:bean.getClass().getDeclaredFields())
    {
      BasicDBObjectBuilder newDBObj = BasicDBObjectBuilder.start();
      f.setAccessible(true);
      String fieldName = f.getName();
      String operation = attributeOperationMap.get(fieldName);
      if(operation!=null){
        DBObject append = new BasicDBObject();
        try {
          JsonUtil util = new JsonUtil();
          if(f.getType().equals(List.class))
            append.put(fieldName, util.toDBObject(f.get(bean)));
          else if(f.getType().equals(Date.class))
            append.put(fieldName, DateUtil.format((Date) f.get(bean), DateUtil.YYYYMMDD_HHMMSS));
          else
            append.put(fieldName, f.get(bean));
        } catch (Exception e) {
          logger.error("更新属性:"+fieldName+"失败.", e);
          e.printStackTrace();
        }
        newDBObj.append(operation, append);
        WriteResult result = collection.update(condition.get(), newDBObj.get(), false, true);
        if(result.getError()!=null){
          logger.error("MongoDB update异常:"+result.getError());
          isSuccess = false&isSuccess;
          break;
        }
        else{
          isSuccess = true&isSuccess;
        }
      }
    }
    //superClass
    for(Field f:bean.getClass().getSuperclass().getDeclaredFields())
    {
      BasicDBObjectBuilder newDBObj = BasicDBObjectBuilder.start();
      f.setAccessible(true);
      String fieldName = f.getName();
      String operation = attributeOperationMap.get(fieldName);
      if(operation!=null){
        DBObject append = new BasicDBObject();
        try {
          JsonUtil util = new JsonUtil();
          if(f.getType().equals(List.class))
            append.put(fieldName, util.toDBObject(f.get(bean)));
          else if(f.getType().equals(Date.class))
            append.put(fieldName, DateUtil.format((Date) f.get(bean), DateUtil.YYYYMMDD_HHMMSS));
          else
            append.put(fieldName, f.get(bean));
        } catch (Exception e) {
          logger.error("更新属性:"+fieldName+"失败.", e);
          e.printStackTrace();
        }
        newDBObj.append(operation, append);
        WriteResult result = collection.update(condition.get(), newDBObj.get(), false, true);
        if(result.getError()!=null){
          logger.error("MongoDB update异常:"+result.getError());
          isSuccess = false&isSuccess;
          break;
        }
        else{
          isSuccess = true&isSuccess;
        }
      }
    }
    return isSuccess;
    
  }
  private DBObject reconstructMongoId()
  {
    return Boolean.valueOf(isReconstructMongoId)?QueryBuilder.start().get():QueryBuilder.start("_id").is(0).get();
  }
  private void load(Properties mongoConfig)
  {
    //System.out.println(mongoConfig);
    try {
      initFields(mongoConfig);
      initMongo();
    } catch (Exception e) {
      logger.error("无法加载配置文件:"+mongoConfig, e);
    }
  }
  private void load(String configFile)
  {
    Properties config = new Properties();
    try {
      config.load(SimpleMongoDBClient.class.getClassLoader().getResourceAsStream(configFile));
      initFields(config);
      initMongo();
    } catch (IOException e) {
      logger.error("无法加载配置文件:"+configFile, e);
    }
  }
  
  /**
   * 为SimpleMongoDBClient初始化属性
   * @param config 初始化属性值
   */
  private void initFields(Properties config)
  {
    Class<?> c = SimpleMongoDBClient.class;
    for(Field f:c.getDeclaredFields())
    {
      //System.out.println(f.getName());
      String key = c.getName()+"."+f.getName();
      String value = config.getProperty(key);
      if(value!=null&&value.trim().length()>0)
      {
        f.setAccessible(true);
        try {
          f.set(this,value);
        } catch (Exception e) {
          logger.error("初始化数据库参数["+f.getName()+"="+value+"]失败",e);
        }
      }
    }
  }
  /**
   * 初始化mongo,database和collection实例
   */
  private void initMongo()
  {
    try {
      MongoOptions options = new MongoOptions();
      options.connectionsPerHost = Integer.valueOf(this.connectionsPerHost);
      options.threadsAllowedToBlockForConnectionMultiplier = Integer.valueOf(threadsAllowedToBlockForConnectionMultiplier);
      if(!hostname.contains(",")){
        address = new ServerAddress(hostname,Integer.valueOf(port));
        this.mongo = new Mongo(address,options);
      }
      else{
        String[] hostNames = hostname.split(",");
        List<ServerAddress> addresses = new ArrayList<ServerAddress>();
        for(String host:hostNames){
          addresses.add(new ServerAddress(host,Integer.valueOf(port)));
        }
        this.mongo = new Mongo(addresses,options);
      }
      this.database = mongo.getDB(databaseName);
      if(userName!=null && userName.trim().length()>0 &&
          password!=null && password.trim().length()>0)
      {
        if(!database.authenticate(userName, password.toCharArray()))
        {
          logger.error("帐号或密码不正确,userName="+userName+",password="+password);
          throw new RuntimeException("Unable to authenticate with MongoDB server.");
        }
      }
      this.collection = database.getCollection(collectionName);
    } catch (Exception e) {
      logger.error("初始化Mongo失败",e);
    }
  }
  @Override
  public List<E> select(QueryBuilder condition, QueryBuilder sort, int index,
      int size, Class<E> beanClass) {
    List<DBObject> dbObjList = null;
    if(sort!=null)
      dbObjList = collection.find(condition.get(), reconstructMongoId()).sort(sort.get()).skip(index).limit(size).toArray();
    else
      dbObjList = collection.find(condition.get(), reconstructMongoId()).skip(index).limit(size).toArray();
    JsonUtil<E> util = new JsonUtil<E>();
    List<E> result = util.toObjectList(dbObjList, List.class, beanClass);
    return result;
  }
  
}
