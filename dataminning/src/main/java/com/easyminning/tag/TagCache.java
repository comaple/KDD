package com.easyminning.tag;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-8-31
 * Time: 上午11:29
 * To change this template use File | Settings | File Templates.
 */
public class TagCache {

    /**
     * 标签库
     */
    public static Set<String> TAG_LIB_SET = new HashSet<String>();
    protected static Log logger = LogFactory.getLog(TagCache.class);
    private static String TAG_SEED_FILE_NAME = "tag.csv";

    static  {
        try {
            File file = new File(TagCache.class.getClassLoader()
                            .getResource(TAG_SEED_FILE_NAME).toURI());
            List<String> lines = FileUtils.readLines(file);
            for (String line : lines)  {
                try {
                    if (line == null || "".equals(line.trim())) continue;
                    String tag = line.trim();
                    TAG_LIB_SET.add(tag);
                } catch (Exception e) {
                    logger.warn("解析标签行出错：" + line);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean contain(String tag) {
        if (TAG_LIB_SET.contains(tag)) {
            return true;
        }
        return false;
    }


    public static void main(String[] args) {
        System.out.println(contain("美国"));
    }


}
