package com.easyminning.etl.mahout.lda.reduce;

import com.easyminning.etl.mahout.util.Constant;
import com.easyminning.tag.TagDoc;
import com.easyminning.tag.TagDocService;
import com.easyminning.etl.mahout.writable.UidPrefWritable;
import com.easyminning.tag.LDAResultParser;
import org.apache.avro.generic.GenericData;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.*;

/**
 * Created by comaple on 14-9-3.
 */
public class ParseLDANewReducer extends Reducer<Text, UidPrefWritable, Text, NullWritable> {

    // topic 和 word的对应关系
    private Map<String, Map<String, Double>> topicKeyVlues = new HashMap<String, Map<String, Double>>();
    //默认是10个主题
    private int k = 0;
    // mongo db service
    TagDocService tagDocService = TagDocService.getInstance();
    private int topN = 0;

    List<TagDoc> tagDocList = new ArrayList<TagDoc>();

    @Override

    protected void setup(Context context) throws IOException, InterruptedException {
        //读取分布式缓存中得数据
        topicKeyVlues = LDAResultParser.getMap(Constant.TOPIC_PATH);
        k = context.getConfiguration().getInt(Constant.TOPIC_K, 10);
        topN = context.getConfiguration().getInt(Constant.TOP_N, 100);

    }

    @Override
    protected void reduce(Text key, Iterable<UidPrefWritable> values, Context context) throws IOException, InterruptedException {
        String docname = "";
        Text vector = new Text();
        String vectorStr = "";
        for (UidPrefWritable uidPrefWritable : values) {
            System.err.println(uidPrefWritable.getVectorWritable().toString());
            if (uidPrefWritable.getFlage().toString().equals(Constant.FLAG_DOC)) {
                vector = uidPrefWritable.getVectorWritable();
                vectorStr = vector.toString().replace("{", "").replace("}", "");
            } else if (uidPrefWritable.getFlage().toString().equals(Constant.FLAG_MATRIX)) {
                docname = uidPrefWritable.getUidValue().toString();
            }
        }
        String[] topicWeights = vectorStr.split(",");
        if (topicWeights == null || topicWeights.length != k) {
            return;
        }
        Map<String, TagDoc> wordWeightModels = new HashMap<String, TagDoc>();
        StringBuilder stringBuilder = new StringBuilder();

        for (String topic : topicWeights) {
            String[] topicKV = topic.split(":");
            if (topicKV.length != 2) {
                continue;
            }
            Map<String, Double> words = topicKeyVlues.get(topicKV[0]);

            for (String k : words.keySet()) {
                if (NumberUtils.isNumber(k)) {
                    continue;
                }
                TagDoc model = new TagDoc(docname, k, words.get(k) * Double.parseDouble(topicKV[1]));
                if (wordWeightModels.containsKey(k)) {
                    model.setWeight(model.getWeight() + wordWeightModels.get(k).getWeight());
                }
                wordWeightModels.put(k, model);
            }
        }
        List<TagDoc> list = new ArrayList();
        list.addAll(wordWeightModels.values());

        Collections.sort(list);

        int index = 0;
        //插入mongodb
        for (TagDoc tagDoc : list) {
            if (index > topN) {
                break;
            }
            tagDocList.add(tagDoc);
            stringBuilder.append(tagDoc.getTagItem() + ",");
            index++;
        }
        String content = stringBuilder.toString().substring(0, stringBuilder.toString().lastIndexOf(","));
        context.write(new Text(content), NullWritable.get());

    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        tagDocService.saveList(tagDocList);
        super.cleanup(context);
    }

    public static void main(String[] args) {
        List<TagDoc> wordWeightModels = new LinkedList<TagDoc>();
        wordWeightModels.add(new TagDoc("S", "DSF", 2d));
        wordWeightModels.add(new TagDoc("S", "DSasdf", 3d));
        Collections.sort(wordWeightModels);
        System.out.println(wordWeightModels);
    }
}
