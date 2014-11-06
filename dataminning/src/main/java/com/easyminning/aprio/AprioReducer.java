package com.easyminning.aprio;

import com.easyminning.etl.mahout.writable.TagTagWritable;
import com.easyminning.tag.StepTagSimilarity;
import com.easyminning.tag.TagTag;
import com.easyminning.tag.TagTagService;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.*;

/**
 * Created by Administrator on 2014/9/6.
 */
public class AprioReducer extends Reducer<Text,DoubleWritable,Text,NullWritable> {

    List<TagTag> tagTagList = new ArrayList<TagTag>();

    private static double MIN_SUPPORT = 5.0;
    private Map<String, Double> wordFrequency = new HashMap<String, Double>();
    private int docCount = 0;



    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
        Map wordFrequencyAndDocCount = StepTagSimilarity.getInstance().getWordFrequencyAndDocCount(
                new Path(context.getConfiguration().get("inputPath")),context.getConfiguration());
        wordFrequency = (Map<String,Double>)wordFrequencyAndDocCount.get("wordFrequency");
        docCount = (Integer)wordFrequencyAndDocCount.get("docCount");

    }

    @Override
    protected void reduce(Text key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
        Double sumWeight = 0.0D;
        for (DoubleWritable weight : values) {
            sumWeight = weight.get() +sumWeight;
        }

        if (sumWeight <= MIN_SUPPORT) return;

        TagTag tagTag = new TagTag();
        tagTag.setTagItem(key.toString().split(",")[0]);
        tagTag.setTagItem1(key.toString().split(",")[1]);
      //  sumWeight = sumWeight;///docCount * Math.log(docCount/wordFrequency.get(tagTag.getTagItem1()));
        tagTag.setWeight(sumWeight);
        tagTag.setDocCount(wordFrequency.get(tagTag.getTagItem1()));

        tagTagList.add(tagTag);
        context.write(new Text(key.toString()+ ":" +  sumWeight),NullWritable.get());
       // super.reduce(key, values, context);
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        TagTagService tagTagService = TagTagService.getInstance();
        System.out.println("taglistsize:" + tagTagList.size());
        if (tagTagList.size()>0) {
            tagTagService.saveTagTagList(tagTagList);
        }
        super.cleanup(context);
    }
}
