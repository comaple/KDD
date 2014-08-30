package com.easyminning.etl.mahout.docparse2word.map;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created by ZhangShengtao on 14-7-21.
 */
public class CombineMapper extends Mapper<LongWritable, Text, Text, Text> {

    private static Pattern SPLIT = Pattern.compile("\t");
    private static Text userid = new Text();
    private static Text fileNames = new Text();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] useridAndFileName = SPLIT.split(value.toString());
        FileSplit fileSplit = (FileSplit) context.getInputSplit();
        String fileName = fileSplit.getPath().getName();
        String name = fileName.substring(fileName.lastIndexOf(File.separator), fileName.length() - 1);

        if (useridAndFileName.length != 2) {
            return;
        }
        userid.set(useridAndFileName[0]);
        fileNames.set(useridAndFileName[1]);
        context.write(userid, fileNames);
    }
}
