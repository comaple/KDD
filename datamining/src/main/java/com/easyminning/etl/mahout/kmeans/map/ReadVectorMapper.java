package com.easyminning.etl.mahout.kmeans.map;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.mahout.math.VectorWritable;

import java.io.IOException;

/**
 * Created by ZhangShengtao on 14-4-17.
 */
public class ReadVectorMapper extends Mapper<Text, VectorWritable, Text, Text> {
    @Override
    protected void map(Text key, VectorWritable value, Context context) throws IOException, InterruptedException {
        String vector = value.get().toString().replace("{", "").replace("}", "");

    }
}
