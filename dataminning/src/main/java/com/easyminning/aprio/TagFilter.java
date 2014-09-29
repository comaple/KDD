package com.easyminning.aprio;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Administrator on 2014/9/29.
 */
public class TagFilter {

    private static Set<String> containWords = new HashSet<String>(){{
        add("大学");
        add("申请");
        add("选择");
        add("时间");
        add("国际");
        add("专业");
        add("出国");
        add("学习");
        add("介绍");
        add("教育");
        add("留学生");
        add("中国");
        add("同学");
        add("包括");
        add("小编");
        add("希望");
        add("参加");
        add("精彩内容");
        add("万元");
        add("平均");
        add("能力");
         }};

    public static boolean filterTag(String tag) {
        if (containWords.contains(tag)) return false;
        return true;
    }


}
