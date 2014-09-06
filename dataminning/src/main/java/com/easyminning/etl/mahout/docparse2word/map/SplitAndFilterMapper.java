package com.easyminning.etl.mahout.docparse2word.map;

import com.easyminning.etl.mahout.util.Constant;
import com.easyminning.etl.mahout.util.similarity.Similarity;
import com.easyminning.etl.mahout.util.similarity.impl.CalculateSimilarityOfMap;
import com.easyminning.etl.mahout.writable.DocumentWritable;
import com.easyminning.tag.StepSeedCache;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by comaple on 14-8-31.
 */

public class SplitAndFilterMapper extends Mapper<LongWritable, Text, Text, DocumentWritable> {
    private Map<String, Double> targetMap = null;
    private Similarity similarity = null;
    private StepSeedCache stepSeedCache = null;
    private static Text docId = new Text("0");
    private Double threshold = 0.0;


    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
        String patternStr = context.getConfiguration().get(Constant.PATTERN_STR);
        //设置默认值为-1，代表不用根据默认值，出权重。
        threshold = Double.parseDouble(context.getConfiguration().get(Constant.THRSHOLD) == null ? "-1" : context.getConfiguration().get(Constant.THRSHOLD));
        pattern = Pattern.compile(patternStr);
        //初始化相似度度量程序
        similarity = new CalculateSimilarityOfMap();
        //初始化配置文件读取程序
        stepSeedCache = new StepSeedCache();
        stepSeedCache.init();
    }

    /**
     * 文章格式：  文章id,标题,关键词,摘要,正文,原文,url,发布时间,作者  , 分词结果
     *
     * @param key
     * @param value
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        int count = 0;
        StringBuilder stringBuilder = new StringBuilder();
        String[] fields = pattern.split(value.toString());
        if (fields.length != 9) {
            return;
        }
        DocumentWritable documentWritable = parse2Doc(fields);
        StringReader reader = new StringReader(documentWritable.getSourceContent().toString());
        IKSegmenter segmenter = new IKSegmenter(reader, true);
        // 分词并记录 count 总数，计算word权重
        while ((lexeme = segmenter.next()) != null && lexeme.getLexemeText().length() != 1) {
            count++;
            String word = lexeme.getLexemeText();
            if (targetMap.containsKey(word)) {
                targetMap.put(word, targetMap.get(word) + 1d);
            } else {
                targetMap.put(word, 1d);
            }
            stringBuilder.append(word + " ");
        }

        //权重归一化处理
        for (String word : targetMap.keySet()) {
            targetMap.put(word, targetMap.get(word) / count);
        }
        Double weight = similarity.Similarity(StepSeedCache.SEED_MAP, targetMap);
        //设置分词结果，以空格分隔
        documentWritable.setResult(new Text(stringBuilder.toString()));
        // 设置权重
        documentWritable.setWeihgt(new DoubleWritable(weight));

        if (threshold != -1 && threshold <= weight) {
            context.write(docId, documentWritable);
        } else {
            context.write(docId, documentWritable);
        }
    }


    /**
     * 解析为documentWritable
     *
     * @param fields
     * @return
     */
    private DocumentWritable parse2Doc(String[] fields) {
        DocumentWritable documentWritable = new DocumentWritable();
        documentWritable.setDocId(new Text(fields[0]));
        documentWritable.setTitle(new Text(fields[1]));
        documentWritable.setKeyWord(new Text(fields[2]));
        documentWritable.setSummary(new Text(fields[3]));
        documentWritable.setDocContent(new Text(fields[4]));
        documentWritable.setSourceContent(new Text(fields[5]));
        documentWritable.setUrl(new Text(fields[6]));
        documentWritable.setIssue(new Text(fields[7]));
        documentWritable.setAuthor(new Text(fields[8]));
        return documentWritable;
    }

    //ik分词器
    private static Lexeme lexeme = null;

    //参数，原始数据分隔符
    private static Pattern pattern = null;

}
