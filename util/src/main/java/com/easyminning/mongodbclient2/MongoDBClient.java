package com.easyminning.mongodbclient2;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.QueryBuilder;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author lishuai
 *
 * @param <E> bean泛型
 */
public interface MongoDBClient<E> {
  public final static String DEFAULT_CONFIG_FILE              = "mongo.properties";
  public final static String DEFAULT_MONGO_DB_HOSTNAME        = "localhost";
  public final static String DEFAULT_MONGO_DB_PORT            = "27017";
  public final static String DEFAULT_MONGO_DB_DATABASE_NAME   = "test";
  public final static String DEFAULT_MONGO_DB_COLLECTION_NAME = "test";
  
  /**
   * 插入一个对象
   * @param bean 插入内容 
   * @return 是否成功
   */
  public boolean insert(E bean);
  /**
   * 插入一组对象
   * @param beanCollection 数据集合
   * @return 是否成功
   */
  public boolean insert(List<E> beanCollection);
  /**
   * 无则插入,有则更新指定字段
   * @param condition 查询条件
   * @param bean 插入实体
   * @param attributeOperationMap 更新字段
   * @return 是否成功
   */
  public boolean insertOrUpdate(QueryBuilder condition, E bean, Map<String, String> attributeOperationMap);
  /**
   * 保存或更新一个对象(只更新一条记录)
   * @param condition 查询条件
   * @param bean 要更新的内容
   * @return 是否成功
   */
  public boolean save(QueryBuilder condition, E bean);
  /**
   * 根据条件删除数据
   * @param condition 删除条件
   * @return 是否成功
   */
  public boolean delete(QueryBuilder condition);
  /**
   * 根据条件删除数据
   * @param bean 数据内容
   * @return 是否成功
   */
  public boolean delete(E bean);
  
  /**
   * 根据条件更新指定属性的数据
   * @param condition 条件
   * @param bean 数据
   * @param attributeOperationMap 指定属性的更新操作,key=属性名,value=操作类型
   * @return 是否成功
   */
  public boolean update(QueryBuilder condition, E bean, Map<String, String> attributeOperationMap);
  /**
   * 根据条件,更新数据
   * @param condition 条件
   * @param bean 更新的数据
   * @return 是否成功
   */
  public boolean marge(QueryBuilder condition, E bean);
  
  /**
   * 满足条件的数据集合
   * @param condition 查询条件
   * @param index  开始位置
   * @param size 每次取的数量
   * @param beanClass 对象的类对象
   * @return 满足条件的集合
   */
  public List<E> select(QueryBuilder condition, int index, int size, Class<E> beanClass);
  /**
   * 满足条件的数据集合
   * @param condition 查询条件
   * @param sort 排序规则
   * @param index 开始位置
   * @param size 每次取的数量
   * @param beanClass 对象的类对象
   * @return 满足条件的集合
   */
  public List<E> select(QueryBuilder condition, QueryBuilder sort, int index, int size, Class<E> beanClass);
  /**
   * 根据条件返回一个满足条件的数据
   * @param condition 条件
   * @param beanClass 返回对象的类对象
   * @return 是否成功
   */
  public E selectOne(QueryBuilder condition, Class<E> beanClass);
  
  /**
   * 根据条件,获取满足条件的记录数
   * @param condition 统计条件
   * @return 记录数
   */
  public long selectCount(QueryBuilder condition);
  
  /**
   * group分组操作 类似 SQL的 group by
   * @param <G> 结果集的泛型
   * @param key DBObject keyDBObj = new BasicDBObject();keyDBObj.put("sku", true);
   * @param condition DBObject cond = QueryBuilder.start("sku").in(longArr).get();
   * @param initial DBObject initial = new BasicDBObject();initial.put("totalTime", 0);initial.put("averagePerson", 0);
   * @param reduce String reduce = "function(doc,out){out.averagePerson++;out.totalTime+=doc.averageDay;}"
   * @param finalize String finalize = "function(out){out.averageDay=out.totalTime/out.averagePerson;}";
   * @param gClass 返回类型的类对象
   * @return 满足条件的结果集
   */
  public <G> List<G> group(DBObject key, DBObject condition, DBObject initial, String reduce, String finalize, Class<G> gClass);
  /**
   * 去除重复记录 例如：针对sku做去重操作 List<Long> result = client.distinct(SKU_KEY,query(这个查询条件))
   * @param <T> 结果集的泛型
   * @param key 制定去重字段名
   * @param query 条件
   * @return 返回满足条件的结果集
   */
  public <T> List<T> distinct(String key, QueryBuilder query);
  
  
  public DBCollection getCollection();
  
  public void requestStart();
  public void requestDone();
  /**
   * 关闭ALL连接
   */
  public void close();
}
