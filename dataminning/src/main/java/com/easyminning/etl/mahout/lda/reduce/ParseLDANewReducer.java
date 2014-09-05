package com.easyminning.etl.mahout.lda.reduce;

import com.easyminning.etl.mahout.util.Constant;
import com.easyminning.tag.DocWordWeightModel;
import com.easyminning.tag.DocWordWeightService;
import com.easyminning.etl.mahout.writable.UidPrefWritable;
import com.easyminning.tag.LDAResultParser;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by comaple on 14-9-3.
 */
public class ParseLDANewReducer extends Reducer<Text, UidPrefWritable, Text, Text> {

    // topic 和 word的对应关系
    private Map<String, Map<String, Double>> topicKeyVlues = new HashMap<String, Map<String, Double>>();
    //默认是10个主题
    private int k = 0;
    // mongo db service
    DocWordWeightService docWordWeightService = DocWordWeightService.getInstance();

    @Override

    protected void setup(Context context) throws IOException, InterruptedException {
        //读取分布式缓存中得数据
        topicKeyVlues = LDAResultParser.getMap(Constant.TOPIC_PATH);
        k = context.getConfiguration().getInt(Constant.TOPIC_K, 10);
    }

    @Override
    protected void reduce(Text key, Iterable<UidPrefWritable> values, Context context) throws IOException, InterruptedException {
        String docId = key.toString();
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
        List<DocWordWeightModel> wordWeightModels = new LinkedList<DocWordWeightModel>();
        StringBuilder stringBuilder = new StringBuilder();
        for (String topic : topicWeights) {
            String[] topicKV = topic.split(":");
            if (topicKV.length != 2) {
                continue;
            }
            Map<String, Double> words = topicKeyVlues.get(topicKV[0]);
            for (String k : words.keySet()) {
                DocWordWeightModel model = new DocWordWeightModel(docname, k, words.get(k) * Double.parseDouble(topicKV[1]));
                wordWeightModels.add(model);
                stringBuilder.append(k + ",");
            }
        }

        //插入mongodb
        for (DocWordWeightModel docWordWeightModel : wordWeightModels) {
            docWordWeightService.save(docWordWeightModel);
        }

        context.write(new Text(docname), new Text(stringBuilder.toString()));

    }
}
