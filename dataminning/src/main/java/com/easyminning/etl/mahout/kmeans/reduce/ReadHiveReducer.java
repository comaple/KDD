package com.easyminning.etl.mahout.kmeans.reduce;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by ZhangShengtao on 14-4-14.
 */
public class ReadHiveReducer extends Reducer<Text, Text, Text, Text> {
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        StringBuilder stringBuilder = new StringBuilder();
        for (Text v : values) {
            stringBuilder.append(v.toString().trim() + " ");
        }
        context.write(key, new Text(stringBuilder.toString()));
    }


}
