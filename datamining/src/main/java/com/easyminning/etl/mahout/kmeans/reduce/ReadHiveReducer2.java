package com.easyminning.etl.mahout.kmeans.reduce;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by ZhangShengtao on 14-4-14.
 */
public class ReadHiveReducer2 implements Reducer<Text, Text, Text, Text> {


    @Override
    public void reduce(Text key, Iterator<Text> values, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        while (values.hasNext()) {
            stringBuilder.append(values.next().toString().trim() + " ");
        }
        output.collect(key, new Text(stringBuilder.toString()));
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void configure(JobConf job) {

    }
}
