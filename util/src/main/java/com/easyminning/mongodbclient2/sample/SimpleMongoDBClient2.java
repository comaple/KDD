package com.easyminning.mongodbclient2.sample;

import com.easyminning.mongodbclient2.MongoDBClient;
import com.easyminning.mongodbclient2.constants.MongoConstants;
import com.easyminning.mongodbclient2.driver.MongoDBDriver;
import com.easyminning.mongodbclient2.util.DateUtil;
import com.easyminning.mongodbclient2.util.JsonUtil;
import com.mongodb.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.util.*;

public class SimpleMongoDBClient2<E> implements MongoDBClient<E> {
  @Override
  public void close() {
    logger.warn("SimpleMongoDBClient2 不能真正关闭连接请调用MongoDBDriver close的方法");
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

  @SuppressWarnings("unchecked")
  @Override
  public <T> List<T> distinct(String key, QueryBuilder query) {
    return collection.distinct(key, query.get());
  }

  

  @Override
  public <G> List<G> group(DBObject key, DBObject condition, DBObject initial,
      String reduce, String finalize, Class<G> gClass) {
    DBObject result = collection.group(key, condition, initial, reduce, finalize);
    JsonUtil<G> util = new JsonUtil<G>();
    List<G> resultList = util.toObjectList(result.toString(), List.class, gClass);
    return resultList;
  }

  @Override
  public boolean insert(E bean) {
    JsonUtil<E> util = new JsonUtil<E>();
    DBObject dbObj = util.toDBObject(bean);
    WriteResult result = collection.insert(dbObj);
    if (result.getError() != null) {
      logger.error("MongoDB 插入异常:" + result.getError());
      return false;
    }
    return true;
  }

  @Override
  public boolean insert(List<E> beanCollection) {
    if (beanCollection != null && beanCollection.size() < MongoConstants.INSERT_LIST_MAX_SIZE) {
      JsonUtil<E> util = new JsonUtil<E>();
      WriteResult result = collection.insert(util.toDBObjectList(beanCollection));
      if (result.getError() != null) {
        throw new RuntimeException("MongoDB 批量插入异常:" + result.getError());
      }
      return true;
    } 
    else {

      throw new RuntimeException("MongoDB batch insert exception! data is null or out max number");
    }
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
    return save(condition, bean);
  }

  @Override
  public void requestDone() {
    logger.warn("TODO Auto-generated method stub");
    
  }

  @Override
  public void requestStart() {
    logger.warn("TODO Auto-generated method stub");
    
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
  public List<E> select(QueryBuilder condition, int index, int size,
      Class<E> beanClass) {
    List<DBObject> dbObjList = collection.find(condition.get(), reconstructMongoId()).skip(index).limit(size).toArray();
    JsonUtil<E> util = new JsonUtil<E>();
    List<E> result = util.toObjectList(dbObjList, List.class, beanClass);
    return result;
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

  @Override
  public long selectCount(QueryBuilder condition) {
    return collection.count(condition.get());
  }

  @Override
  public E selectOne(QueryBuilder condition, Class<E> beanClass) {
    DBObject dbObj = collection.findOne(condition.get(),reconstructMongoId());
    JsonUtil<E> util = new JsonUtil<E>();
    return dbObj!=null?util.toObject(dbObj.toString(), beanClass):null;
  }
  @SuppressWarnings("unchecked")
  @Override
  public boolean update(QueryBuilder condition, E bean,Map<String, String> attributeOperationMap) {
    boolean isSuccess = true;
    
    Set<String> fieldNames = attributeOperationMap.keySet();
    Map<String,DBObject> newDBObj = new HashMap<String,DBObject>();
    for(String fieldName:fieldNames){
    	String operation = attributeOperationMap.get(fieldName);
    	if(operation!=null){
    		DBObject append = newDBObj.containsKey(operation)?newDBObj.get(operation):new BasicDBObject();
    		try {
    			Field field = null;
	    		try{
	    			field = bean.getClass().getDeclaredField(fieldName);
	    		}
	    		catch(NoSuchFieldException fe){
	    			field = null;
	    		}
		    	if(field==null){
		    		field = bean.getClass().getSuperclass().getDeclaredField(fieldName);
		    	}
		    	field.setAccessible(true);
	          JsonUtil util = new JsonUtil();
	          if(field.getType().equals(List.class))
	            append.put(fieldName, util.toDBObject(field.get(bean)));
	          else if(field.getType().equals(Date.class))
	            append.put(fieldName, DateUtil.format((Date) field.get(bean), DateUtil.YYYYMMDD_HHMMSS));
	          else
	            append.put(fieldName, field.get(bean));
	        } catch (Exception e) {
	          logger.error("更新属性:"+fieldName+"失败.", e);
	          e.printStackTrace();
	        }
	        newDBObj.put(operation, append);
    	}
    }
    WriteResult result = collection.update(condition.get(), BasicDBObjectBuilder.start(newDBObj).get(), false, true);
    if(result.getError()!=null){
      logger.error("MongoDB update异常:"+result.getError());
      isSuccess = false&isSuccess;
    }
    else{
      isSuccess = true&isSuccess;
    }
    return isSuccess;
    
  }
  public void init(){
    try{
      dataBase = driver.getMongo().getDB(dataBaseName);
      if(this.userName!=null&&!"".equals(userName)&&this.password!=null&&!"".equals(password)){
        dataBase.authenticate(userName, password.toCharArray());
      }
      collection = dataBase.getCollection(collectionName);
    }catch(Exception e){
      logger.error("SimpleMongoDBClient2 初始化失败", e);
    }
  }
  private DBObject reconstructMongoId()
  {
    return reconstructMongoId?QueryBuilder.start().get():QueryBuilder.start("_id").is(0).get();
  }
  @Override
  public DBCollection getCollection() {
    return collection;
  }
  public DB getDataBase() {
    return dataBase;
  }
  
  public void setUserName(String userName) {
    this.userName = userName;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setCollectionName(String collectionName) {
    this.collectionName = collectionName;
  }
  public void setDataBaseName(String dataBaseName) {
    this.dataBaseName = dataBaseName;
  }
  public void setDriver(MongoDBDriver driver) {
    this.driver = driver;
  }

  public void setReconstructMongoId(boolean reconstructMongoId) {
    this.reconstructMongoId = reconstructMongoId;
  }

  private boolean reconstructMongoId = true;
  private MongoDBDriver driver;
  private String userName = null;
  private String password = null;
  private String collectionName;
  private String dataBaseName;
  
  
  public DBCollection collection;
  private DB dataBase;
  public static final Log logger = LogFactory.getLog(MongoConstants.COMMON_MONGO);
  /**
   * @param args
   */
  public static void main(String[] args) {
    
    
  }
}
