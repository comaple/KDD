package com.easyminning.etl.mahout.lda.reduce;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by ZhangShengtao on 14-8-7.
 */
public class StatisticsReducer extends Reducer<Text, Text, Text, Text> {

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        int count = 0;
        for (Text value : values) {
            count++;
        }
        System.err.println(key.toString());
        /**
         *  取每个传过来的topic-id作为key输出
         */
        context.write(new Text(key.toString()), new Text(String.valueOf(count)));
    }
}
