package com.easyminning.aprio;

import com.easyminning.etl.mahout.writable.TagTagWritable;
import com.easyminning.tag.TagTag;
import com.easyminning.tag.TagTagService;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/9/6.
 */
public class AprioReducer extends Reducer<Text,DoubleWritable,Text,NullWritable> {

    List<TagTag> tagTagList = new ArrayList<TagTag>();

    private static double MIN_SUPPORT = 5.0;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);    //To change body of overridden methods use File | Settings | File Templates.
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
        tagTag.setWeight(sumWeight);

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
