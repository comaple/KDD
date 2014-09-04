package com.easyminning.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.ReflectionUtils;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-9-3
 * Time: 下午10:17
 * To change this template use File | Settings | File Templates.
 */
public class SequenceFileUtil {

    /**
     *
     * @param pathStr
     * @param conf
     * @return
     */
    public static Map<Writable,Writable> readSequenceFile(String pathStr,Configuration conf) {
        Map<Writable,Writable> res = new HashMap<Writable, Writable>();
        SequenceFile.Reader reader = null;
        try {
            FileSystem fs = FileSystem.get(conf);
            Path path = new Path(pathStr);
            reader = new SequenceFile.Reader(fs,path,conf);
            Writable key = (Writable) ReflectionUtils.newInstance(reader.getKeyClass(),conf);
            Writable value = (Writable)ReflectionUtils.newInstance(reader.getValueClass(),conf);
            reader.getPosition(); // 移动偏移量
            while (reader.next(key,value)) {
                res.put(key,value);
                reader.getPosition(); // beginning of next record
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                IOUtils.closeStream(reader);
            }
        }
        return res;
    }

}
