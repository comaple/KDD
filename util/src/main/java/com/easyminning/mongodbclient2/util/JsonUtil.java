package com.easyminning.mongodbclient2.util;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;

import java.util.List;

public class JsonUtil<E> {
  private final static Logger logger = Logger.getLogger(JsonUtil.class);
  private static ObjectMapper mapper = new ObjectMapper();
  public DBObject[] toDBObjectList(List<E> beanList){
    String jsonList = null;
    try {
      jsonList = mapper.writeValueAsString(beanList);
    } catch (Exception e) {
      logger.error(e.getMessage());
      return null;
    }
    BasicDBList arr = (BasicDBList) JSON.parse(jsonList);
    DBObject[] dbObjArr = new DBObject[arr.toArray().length];
    return arr.toArray(dbObjArr);
  }
  @SuppressWarnings("unchecked")
  public List<E> toObjectList(List<DBObject> dbObjList,Class<List> listClass,Class<E> beanClass){
    List<E> result = null;
    try {
      result = mapper.readValue(dbObjList.toString(), TypeFactory.collectionType(listClass, beanClass));
    } catch (Exception e) {
      logger.error(e.getMessage());
      return null;
    }
    return result;
  }
  @SuppressWarnings("unchecked")
  public List<E> toObjectList(String dbObjListJsonString,Class<List> listClass,Class<E> beanClass){
    List<E> result = null;
    try {
      result = mapper.readValue(dbObjListJsonString, TypeFactory.collectionType(listClass, beanClass));
    } catch (Exception e) {
      logger.error(e.getMessage());
      return null;
    }
    return result;
  }
  public E toObject(String jsonString,Class<E> beanCLass){
    E e = null;
    try {
      e = mapper.readValue(jsonString, beanCLass);
    } catch (Exception err) {
      logger.error(err.getMessage());
      return null;
    }
    return e;
  }
  public DBObject toDBObject(E bean){
    String jsonString = toJson(bean);
    DBObject dbObj = (DBObject) JSON.parse(jsonString);
    return dbObj;
  }
  public String toJson(E bean){
    String jsonString = null;
    try {
      jsonString = mapper.writeValueAsString(bean);
    } catch (Exception err) {
      logger.error(err.getMessage());
      return null;
    } 
    return jsonString;
  }
}
