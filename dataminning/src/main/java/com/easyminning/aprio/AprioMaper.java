package com.easyminning.aprio;

import com.easyminning.etl.mahout.writable.TagTagWritable;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.*;

/**
 * Created by Administrator on 2014/9/6.
 */
public class AprioMaper extends Mapper<LongWritable,Text,TagTagWritable,DoubleWritable> {

    // 行单词集合
    public static List<Map<String,Double>> lineWordsMapList = new ArrayList<Map<String,Double>>();

    // 一项集支持度
    public static Map<String,Double> oneItemMap = new HashMap<String, Double>();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] words = value.toString().split(",");
        Map<String,Double> lineWordsMap = new HashMap<String, Double>();// 一行的词集
        lineWordsMapList.add(lineWordsMap);

        // 遍历一行的词
        for (String word : words) {

            //
//            if (!lineWordsMap.keySet().contains(word)) {
//                lineWordsMap.put(word,1.0D);
//            } else {
//                lineWordsMap.put(word, lineWordsMap.get(word)+1.0D);
//            }
            lineWordsMap.put(word,1.0D);
        }


        // 根据一项集构造二项集
        Set<String> oneItemSet = lineWordsMap.keySet();
        String[] oneItemArray = oneItemSet.toArray(new String[0]);

        // 构造二项集
        for (String oneItemWord : oneItemSet) {
            for (String word: oneItemArray) {
                if (oneItemWord.trim().equals(word.trim()))continue;
                TagTagWritable tagTag= new TagTagWritable();
                tagTag.setTagItem(new Text(oneItemWord));
                tagTag.setTagItem1(new Text(word));
                tagTag.setWeight(new DoubleWritable(1.0));
                context.write(tagTag,new DoubleWritable(1.0D));
            }
        }

        // 清空
      //  oneItemMap.clear();


    }

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
//        Set<String> oneItemSet = oneItemMap.keySet();
//        String[] oneItemArray = oneItemSet.toArray(new String[0]);
//
//        Map<TagTagWritable,NullWritable> writableMap = new HashMap<TagTagWritable, NullWritable>();
//
//        // 构造二项集
//        for (String key : oneItemSet) {
//            for (String word: oneItemArray) {
//                TagTagWritable tagTag= new TagTagWritable();
//                tagTag.setTagItem(new Text(key));
//                tagTag.setTagItem1(new Text(word));
//                tagTag.setWeight(new DoubleWritable(1.0));
//                writableMap.put(tagTag, NullWritable.get());
//            }
//        }
//
//        // 遍历数据库
//        for (Map lineWordMap: lineWordsMapList) {
//            Set<String> keySet = lineWordMap.keySet();
//
//            // 遍历相关二项集
//            for (TagTagWritable tag : writableMap.keySet()) {
//                if (keySet.contains(tag.getTagItem()) && keySet.contains(tag.getTagItem1())) {
//                    tag.setWeight(new DoubleWritable(tag.getWeight().get()+1.0));
//                }
//            }
//        }
//
//        for (TagTagWritable temp : writableMap.keySet())  {
//            context.write(temp,temp.getWeight());
//        }

        super.cleanup(context);
    }
}
