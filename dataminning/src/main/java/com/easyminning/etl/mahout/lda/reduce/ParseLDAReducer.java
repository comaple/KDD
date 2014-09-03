package com.easyminning.etl.mahout.lda.reduce;


import com.easyminning.etl.mahout.util.Constant;
import com.easyminning.etl.mahout.writable.UidPrefWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by ZhangShengtao on 14-8-7.
 */
public class ParseLDAReducer extends Reducer<Text, UidPrefWritable, LongWritable, Text> {

    @Override
    protected void reduce(Text key, Iterable<UidPrefWritable> values, Context context) throws IOException, InterruptedException {
        String docId = key.toString();
        String userId = "";
        Text vector = new Text();
        String vectorStr = "";

        for (UidPrefWritable uidPrefWritable : values) {
            System.err.println(uidPrefWritable.getVectorWritable().toString());
            if (uidPrefWritable.getFlage().toString().equals(Constant.FLAG_DOC)) {
                vector = uidPrefWritable.getVectorWritable();
                vectorStr = vector.toString().replace("{", "").replace("}", "");
                System.err.println(vector.toString());
            } else if (uidPrefWritable.getFlage().toString().equals(Constant.FLAG_MATRIX)) {
                userId = uidPrefWritable.getUidValue().toString();
            }
        }

        String[] items = vectorStr.split(",");
        Arrays.sort(items, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String[] kv1 = o1.split(":");
                String[] kv2 = o2.split(":");
                if (kv1.length != kv2.length || kv1.length != 2 || kv2.length != 2) {
                    return -1;
                }
                double res = (Double.parseDouble(kv2[1]) - Double.parseDouble(kv1[1]));
                if (res > 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
        System.err.println("-------------------------------");
        System.err.println(Arrays.toString(items));
        if (items.length >= 2) {
            vectorStr = items[0].concat("," + items[1]);
            context.write(new LongWritable(Long.parseLong(userId)), new Text(vectorStr));
        }
    }

    public static void main(String[] args) {
        String[] itmes = new String[]{"1:2", "3:5", "1:3", "5:4"};
        Arrays.sort(itmes, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String[] kv1 = o1.split(":");
                String[] kv2 = o2.split(":");
                if (kv1.length != kv2.length || kv1.length != 2 || kv2.length != 2) {
                    return -1;
                }
                return Integer.parseInt(kv2[1]) - Integer.parseInt(kv1[1]);
            }
        });
        System.out.println(Arrays.toString(itmes));
    }

}
