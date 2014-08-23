package com.easyminning.etl.mahout.kmeans.map;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by ZhangShengtao on 14-4-14.
 */

public class ReadHiveMapper extends Mapper<LongWritable, Text, Text, Text> {
    private static final Pattern SPLAT_PATTERN = Pattern.compile("[,|\\s]");


    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] tokens = SPLAT_PATTERN.split(value.toString());
        if (tokens.length > 1) {
            String userid = tokens[0];
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 1; i < tokens.length; i++) {
                stringBuilder.append(tokens[i].trim().replace(".", "") + " ");
            }
            context.write(new Text(userid), new Text(stringBuilder.toString()));
        }
    }

    public static void main(String[] args) {
        String s = "12345 .doc,.xml,.ppt";
        String[] tokens = SPLAT_PATTERN.split(s);
        for (int i = 0; i < tokens.length; i++) {
            System.out.println(tokens[i].trim().replace(".", ""));
        }
    }
}
