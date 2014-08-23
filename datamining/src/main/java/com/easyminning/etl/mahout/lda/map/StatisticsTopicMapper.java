package com.easyminning.etl.mahout.lda.map;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Created by ZhangShengtao on 14-8-7.
 */
public class StatisticsTopicMapper extends Mapper<LongWritable, Text, Text, Text> {
    Text count = new Text("1");

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] kv = value.toString().split(",");
        if (kv.length == 2) {
            context.write(new Text(kv[0]), count);
            context.write(new Text(kv[1]), count);
        }
    }
}
