package com.easyminning.etl.mahout.lda.map;

import com.easyminning.etl.mahout.util.Constant;
import com.easyminning.etl.mahout.writable.UidPrefWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Created by ZhangShengtao on 14-8-7.
 */
public class MatrixMapper extends Mapper<IntWritable, Text, Text, UidPrefWritable> {
    private static String FLAG = "index";

    @Override
    protected void map(IntWritable key, Text value, Context context) throws IOException, InterruptedException {
        context.write(new Text(key.toString()), new UidPrefWritable(key.get(), Constant.FLAG_MATRIX, value.toString(), new Text("")));
    }
}
