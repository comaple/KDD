package com.easyminning.etl.mahout.bayes.map;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: comaple
 * Date: 14-2-28
 * Time: 下午1:41
 * To change this template use File | Settings | File Templates.
 */
public class ReadSeq2TextMapper extends Mapper<Text, Text, Text, Text> {

    @Override
    protected void map(Text key, Text value, Context context) throws IOException, InterruptedException {

    }
}
