package com.easyminning.etl.mahout.lda.map;

import com.easyminning.etl.mahout.util.Constant;
import com.easyminning.etl.mahout.writable.UidPrefWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.mahout.math.VectorWritable;

import java.io.IOException;

/**
 * Created by ZhangShengtao on 14-8-7.
 */
public class LDADocMapper extends Mapper<IntWritable, VectorWritable, Text, UidPrefWritable> {
    private static String FLAG = "doc";

    @Override
    protected void map(IntWritable key, VectorWritable value, Context context) throws IOException, InterruptedException {

        System.err.println(value.toString());
        context.write(new Text(key.toString()), new UidPrefWritable(key.get(), Constant.FLAG_DOC, "0", new Text(value.toString())));

    }
}
