package com.easyminning.mongodb;

import com.easyminning.tag.TagTagService;
import com.mongodb.QueryBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.Serializable;
import com.easyminning.tag.TagTag;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-8-28
 * Time: 下午11:08
 * To change this template use File | Settings | File Templates.
 */
public class MongoDBTest {

    public static void main(String[] args) {

        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-util.xml");
        TagTagService tagDAO = context.getBean(TagTagService.class);
        TagTag tagTag = new TagTag();
        tagTag.setTagItem("美国");
        tagTag.setTagItem1("哈弗");
        tagTag.setWeight(0.9);
       // tagDAO.save(QueryBuilder.start(),tagTag);
    }

    static class User implements Serializable {
      public String name;
      public Integer age;


        String getName() {
            return name;
        }

        void setName(String name) {
            this.name = name;
        }

        Integer getAge() {
            return age;
        }

        void setAge(Integer age) {
            this.age = age;
        }
    }
}
