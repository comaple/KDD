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

    public static int MAX_WORD_COUNT = 10;

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

            //
//            if (!lineWordsMap.keySet().contains(word)) {
//                lineWordsMap.put(word,1.0D);
//            } else {
//                lineWordsMap.put(word, lineWordsMap.get(word)+1.0D);
//            }
            lineWordsMap.put(word,1.0D);
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

    public static void main(String[] args) {
        String[] words = "工程,计算机,教育,美国留学,申请,会计,选择,毕业生,多伦多大学,数学,职业,研究,发展,行业,就业前景,市场,工作,新加坡,工程师,精算,企业,平均,成绩,海外,未来,需求量,本科,商科,科学,石油,英国,院校,顾问,滑铁卢大学,起薪,管理,奖学金,提供,课程,旗下,公司,澳大利亚,生物,中介,澳洲,免费,名校,学院,阿尔伯塔大学,西安大略大学,商学院,能力,学习,优秀,国际,中国学生,酒店,专家,mba,化学,联盟,培训,拥有,全球,管理人员,管理学院,中国,分析".toString().split(",",5);
        System.out.println(words.length);
    }
}
