package com.easyminning.algorithm.simhash;

import com.easyminning.tag.TagDoc;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2014/9/11.
 */
public class DuplicateDocFilter {

    /**
     * 获取文章的局部哈希值
     * @return
     */
    public static  String getDocHash(String content) {
        String docHash = "";
        try {
            SimHash hash = new SimHash(content, 64);
            docHash = hash.getStrSimHash();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return docHash;
    }

    /**
     * 返回文章是否在数据库已经存在
     * @param content
     * @return
     */
    public static boolean isSimilarByDoc(String content) {
        int distance = 4;
        if (distance <= 3 ) {
            return true;
        } else {
            return  false;
        }
    }

    /**
     * 返回文章是否在数据库已经存在
     * @param docHash
     * @return
     */
    public static boolean isSimilarByDocHash(String docHash) {
        return true;
    }

    /**
     * 过滤掉重复的文章
     * @param tagDocList
     */
    public static void filterDuplicatedDoc(List<TagDoc> tagDocList) {

    }

}
