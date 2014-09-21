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
public class AprioReducer extends Reducer<TagTagWritable,DoubleWritable,NullWritable,NullWritable> {

    List<TagTag> tagTagList = new ArrayList<TagTag>();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void reduce(TagTagWritable key, Iterable<DoubleWritable> values, Context context) throws IOException, InterruptedException {
        Double sumWeight = 0.0D;
        for (DoubleWritable weight : values) {
            sumWeight = weight.get() +sumWeight;
        }

        TagTag tagTag = new TagTag();
        tagTag.setTagItem(key.getTagItem().toString());
        tagTag.setTagItem1(key.getTagItem1().toString());
        tagTag.setWeight(sumWeight);

        TagTag tagTag1 = new TagTag();
        tagTag1.setTagItem(key.getTagItem1().toString());
        tagTag1.setTagItem1(key.getTagItem().toString());
        tagTag1.setWeight(sumWeight);
        tagTagList.add(tagTag);
        tagTagList.add(tagTag1);



        super.reduce(key, values, context);
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        TagTagService tagTagService = TagTagService.getInstance();
        tagTagService.saveTagTagList(tagTagList);
        super.cleanup(context);
    }
}
