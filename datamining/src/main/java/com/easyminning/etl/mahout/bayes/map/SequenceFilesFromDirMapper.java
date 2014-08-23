package com.easyminning.etl.mahout.bayes.map;


import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: comaple
 * Date: 14-2-25
 * Time: 上午10:08
 * To change this template use File | Settings | File Templates.
 */
public class SequenceFilesFromDirMapper extends Mapper<LongWritable, Text, Text, Text> {
    private Text fileValue = new Text();
    private static final Pattern SPLAT_PATTERN = Pattern.compile("[\\|]");


    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] values = SPLAT_PATTERN.split(value.toString());
        if (values.length == 2) {
            fileValue.set(values[1].toString());
            context.write(new Text("/" + values[0].toString() + "/" + key), fileValue);
        } else {
            // System.err.print("the values is :" + Arrays.toString(values));
        }
    }

//
//    public static void main(String[] args) {
//        try {
////        String[] vs = SPLAT_PATTERN.split("12|234 5346 54 43");
//            URL url = SequenceFilesFromDirMapper.class.getClassLoader().getResource("test.xml");
//            System.out.println(url.toString());
////        System.out.println(vs[0]);
////        System.out.println(vs[1]);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
