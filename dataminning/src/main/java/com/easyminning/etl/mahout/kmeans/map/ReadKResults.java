package com.easyminning.etl.mahout.kmeans.map;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.mahout.clustering.iterator.ClusterWritable;

import java.io.IOException;

/**
 * Created by ZhangShengtao on 14-3-19.
 */
public class ReadKResults extends Mapper<IntWritable, ClusterWritable, Text, Text> {

    @Override
    protected void map(IntWritable key, ClusterWritable value, Context context) throws IOException, InterruptedException {
        StringBuilder stringBuilder = new StringBuilder();
        int id = value.getValue().getId();
        String contant = value.getValue().toString();
        value.getValue().getCenter().toString();
        stringBuilder.append(value.getValue().getNumObservations() + "\t");
        stringBuilder.append(value.getValue().getCenter() + "\t");
        stringBuilder.append(value.getValue().getRadius());
        context.write(new Text(id + ""), new Text(stringBuilder.toString()));
    }
}
