package com.easyminning.etl.mahout.kmeans.map;

import org.apache.hadoop.hive.serde2.columnar.BytesRefArrayWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by ZhangShengtao on 14-4-14.
 */

public class ReadHiveMapper2 implements Mapper<LongWritable, BytesRefArrayWritable, Text, Text> {
    private static final Pattern SPLAT_PATTERN = Pattern.compile("[,]");


    @Override
    public void map(LongWritable key, BytesRefArrayWritable value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
        int length = value.size();
        String field = null;
        StringBuilder stringBuilder = new StringBuilder();
        if (length >= 1) {
            String userid = new String(value.get(0).getBytesCopy());
            if (length == 1) {
                System.err.println("the bytesRefArray don't have the values for filed :" + userid);
            }
            for (int i = 1; i < length; i++) {
                field = new String(value.get(i).getBytesCopy());
                String[] tokens = SPLAT_PATTERN.split(field);
                for (String token : tokens) {
                    stringBuilder.append(token + " ");
                }
            }
            output.collect(new Text(userid), new Text(stringBuilder.toString()));
        }
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public void configure(JobConf job) {

    }
}
