package com.easyminning.mongodbclient2;

public class UpdateOperation {
  /**
  *
  */
  public static final String PUSHALL = "$pushAll";
  /**
   * 更新指定属性的内容
   */
  public static final String SET = "$set";

  /**
  *
  */
  public static final String INC = "$inc";

  /**
   * 用法：{ $unset : { field : 1} }
   */
  public static final String UNSET = "$unset";

  /**
   * 追加数组数据 用法：{ $push : { field : value } }
   */
  public static final String PUSH = "$push";
  /**
   * 删除最后一个值：{ $pop : { field : 1 } } 删除第一个值：{ $pop : { field : -1 } }
   */
  public static final String POP = "$pop";

  /**
   * 用法：{ $addToSet : { field : value } }
   */
  public static final String ADDTOSET = "$addToSet";
  /**
   * 用法：$pull : { field : value } } 从数组field内删除一个等于value值
   */
  public static final String PULL = "$pull";

  /**
   * 用法：{ $pullAll : { field : value_array } } 同$pull,可以一次删除数组内的多个值。
   */
  public static final String PULLALL = "$pullall";

}
