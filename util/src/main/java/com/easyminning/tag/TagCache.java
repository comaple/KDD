package com.easyminning.tag;

import java.util.Map;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-8-30
 * Time: 下午5:19
 * To change this template use File | Settings | File Templates.
 */
public class TagCache {

    public static Map<String,String> stepNoNames = new HashMap<String,String>(){{
            put("1", "国家");
            put("2", "学校");
            put("3", "专业");
        }
    };
}
