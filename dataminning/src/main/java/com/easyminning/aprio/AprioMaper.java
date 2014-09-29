package com.easyminning.aprio;

import com.easyminning.tag.StepTagSimilarity;
import com.easyminning.tag.VersionStampService;
import com.easyminning.util.simhash.DuplicateDocFilter;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2014/9/6.
 */
public class AprioMaper extends Mapper<LongWritable,Text,Text,DoubleWritable> {

    // 行单词集合
    public static List<Map<String,Double>> lineWordsMapList = new ArrayList<Map<String,Double>>();

    public static int MAX_WORD_COUNT = 75;

    Pattern digitPattern = Pattern.compile(".*\\d+.*");

    String filterWord = "一个 我的 可以 事情 情况 得 得人 学生 学校 留学 留学网";

    // 一项集支持度
    public static Map<String,Double> oneItemMap = new HashMap<String, Double>();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] words = value.toString().split(",");

        Map<String,Double> lineWordsMap = new HashMap<String, Double>();// 一行的词集
        lineWordsMapList.add(lineWordsMap);

        int count = 0;
        // 遍历一行的词
        for (String word : words) {
            if (hasDigit(word)) continue;
            if (filterWord.contains(word)) continue;
            if (!TagFilter.filterTag(word)) continue;

          //  if (word.contains())

            //
//            if (!lineWordsMap.keySet().contains(word)) {
//                lineWordsMap.put(word,1.0D);
//            } else {
//                lineWordsMap.put(word, lineWordsMap.get(word)+1.0D);
//            }
            lineWordsMap.put(word, 1.0D);
            count++;
            if (count == MAX_WORD_COUNT)break;
        }


        // 根据一项集构造二项集
        Set<String> oneItemSet = lineWordsMap.keySet();
        String[] oneItemArray = oneItemSet.toArray(new String[0]);

        // 构造二项集
        for (String oneItemWord : oneItemSet) {
            for (String word: oneItemArray) {
                if (oneItemWord.trim().equals(word.trim()))continue;
//                TagTagWritable tagTag= new TagTagWritable();
//                tagTag.setTagItem(new Text(oneItemWord));
//                tagTag.setTagItem1(new Text(word));
//                tagTag.setWeight(new DoubleWritable(1.0));
                context.write(new Text(oneItemWord+","+word),new DoubleWritable(1.0D));
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

    // 判断一个字符串是否含有数字
    public boolean hasDigit(String content) {
        boolean flag = false;
        Matcher m = digitPattern.matcher(content);
        if (m.matches())
            flag = true;
        return flag;

    }

    public static void main(String[] args) {
        System.out.println("******** 计算步骤和标签相似度************");
        //StepTagSimilarity.getInstance().analysis();

        System.out.println("********** 删除重复文章*************");
        // 删除重复数据
        DuplicateDocFilter.filter();

        System.out.println("*********** 更新版本号***********");
        //  更新版本号为已经完成
        VersionStampService.getInstance().updateUnFinishedVersion();
    }
}
