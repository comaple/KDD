package com.easyminning.etl.mahout.docparse2word.map;

import com.easyminning.etl.mahout.util.Constant;
import com.easyminning.etl.mahout.util.similarity.Similarity;
import com.easyminning.etl.mahout.util.similarity.impl.CalculateSimilarityOfMap;
import com.easyminning.etl.mahout.writable.DocumentWritable;
import com.easyminning.tag.*;
import com.easyminning.util.date.DateUtil;
import com.mongodb.util.StringBuilderPool;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by comaple on 14-8-31.
 */

public class SplitAndFilterMapper extends Mapper<LongWritable, Text, Text, DocumentWritable> {
    private Map<String, Double> targetMap = null;
    private Similarity similarity = null;
    private StepSeedCache stepSeedCache = null;
    private Double threshold = 0.0;
    private String title = "contextwithtag";
    private String patternStr = "";

    private static int CONTENT_MIN_LENGTH = 200;

    private static String HIGH_FREQUENCY_WORDS = "留学,学校";

    private int docCount = 0;
    private int shortDocCount = 0;
    private int shortWeightDocCount = 0;
    private int goodDocCount = 0;


    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);

        //设置默认值为-1，代表不用根据默认值，出权重。
        threshold = Double.parseDouble(context.getConfiguration().get(Constant.THRSHOLD) == null ? "-1" : context.getConfiguration().get(Constant.THRSHOLD));
        patternStr = "\\|\\|==\\|\\|";
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
        targetMap = new HashMap<String, Double>();
        StringBuilder stringBuilder = new StringBuilder();
        if (value.toString().toLowerCase().contains(title.toLowerCase())) {
            return;
        }

        docCount++;


        String[] fields = value.toString().split(patternStr);
        DocumentWritable documentWritable = parse2Doc(fields);
        if (documentWritable == null) {
            System.err.println("parse 2 doc object error , source file line is : \n " + value.toString());
            shortDocCount++;
            return;
        }

        if (documentWritable.getDocContent() == null) {
            shortDocCount++;
            return;
        }

        //  // 问答比较短，不经常长度校验
        if (!"3".equals(documentWritable.getType().toString())) {
            if (documentWritable.getDocContent().getLength() < CONTENT_MIN_LENGTH) {
                shortDocCount++;
                return;
            }
        }


        StringReader reader = new StringReader(documentWritable.getDocContent().toString());
        IKSegmenter segmenter = new IKSegmenter(reader, true);
        // 分词并记录 count 总数，计算word权重
        while ((lexeme = segmenter.next()) != null) {
            if (lexeme.getLexemeText().length() == 1) {
                continue;
            }
            String word = lexeme.getLexemeText();

            // 过滤掉一些分词
            if (!ResultDocumentFilter.filterLexeme(word)) continue;
            stringBuilder.append(word + " ");

            if (targetMap.containsKey(word)) {
                targetMap.put(word, targetMap.get(word) + 1d);
            } else {
                targetMap.put(word, 1d);
            }
        }
        //权重归一化处理
//        for (String word : targetMap.keySet()) {
//            targetMap.put(word, targetMap.get(word));
//        }

        Double weight = similarity.Similarity(StepSeedCache.SEED_MAP, targetMap);

        //设置分词结果，以空格分隔
        documentWritable.setResult(new Text(stringBuilder.toString()));

        // 设置权重
        documentWritable.setWeihgt(new DoubleWritable(weight));

        // 关键词
        List<TagDoc> tagDocList = new ArrayList<TagDoc>();
        for (String word : targetMap.keySet()) {
            TagDoc tagDoc = new TagDoc();
            tagDoc.setTagItem(word);
            tagDoc.setWeight(targetMap.get(word));
            tagDocList.add(tagDoc);
        }
        Collections.sort(tagDocList);
        if (tagDocList.size() > 5) {
            tagDocList = tagDocList.subList(0, 5);
        }

        StringBuilder content = new StringBuilder();
        for (TagDoc tagDoc : tagDocList) {
            content.append(tagDoc.getTagItem());
            content.append(" ");
        }

        documentWritable.setKeyWord(new Text(content.toString()));
        if (threshold != -1) {
            if (threshold < weight) {
                context.write(documentWritable.getDocId(), documentWritable);
                goodDocCount++;
            } else {
                shortWeightDocCount++;
            }

        } else {
            context.write(documentWritable.getDocId(), documentWritable);
            goodDocCount++;
        }
    }


    /**
     * 解析为documentWritable
     * title publishdate url author context contextwithtag
     *
     * @param fields
     * @return
     */

    private DocumentWritable parse2Doc(String[] fields) {
        try {
            DocumentWritable documentWritable = new DocumentWritable();
            documentWritable.setDocId(new Text(UUID.randomUUID().toString()));
            documentWritable.setTitle(new Text(fields[0]));
            documentWritable.setKeyWord(new Text(""));
            documentWritable.setSummary(new Text(""));
            documentWritable.setDocContent(new Text(fields[4]));
            documentWritable.setSourceContent(new Text(fields[5]));
            documentWritable.setUrl(new Text(fields[2]));
            documentWritable.setIssue(new Text(fields[1]));
            documentWritable.setAuthor(new Text(fields[3]));
            if (fields.length > 6) {
                documentWritable.setType(new Text(fields[6]));
            }
            if (fields.length > 7) {
                documentWritable.setScrawDate(new Text(fields[7]));
            }
            return documentWritable;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println(e.getStackTrace());
            System.err.println(Arrays.toString(fields));
            return null;
        }
    }

    //ik分词器
    private static Lexeme lexeme = null;

    //参数，原始数据分隔符
    private static Pattern pattern = null;

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        StringBuilder sb = new StringBuilder();
        sb.append("map文章总数：" + docCount);
        sb.append("，短文章数：" + shortDocCount);
        sb.append("，权重较小的文章数：" + shortWeightDocCount);
        sb.append("，好的文章数：" + goodDocCount);
        LogRecordService.getInstance().save(new LogRecord("2", DateUtil.getCurrentFriendlyTime(),"分词算法执行中，分词结果为: " + sb.toString()));
        super.cleanup(context);
    }

    public static void main(String[] args) throws Exception {
        StringReader reader = new StringReader("去英国留学的趋势是怎么样的同学们选择去英国留学，就要了解它的发展趋势，看看自己应该做怎样的准备。  2014年留学英国申请准备已经开始，赴英留学又有哪些变换和趋势。以下是留学专家总结2014年英国留学的四大趋势。  去英国留学趋势一：留学政策开放，门槛逐步降低  自2014年起五年内，英国政府拟将前往英国大学学习的外国学生人数提高20%，新增人数达到9万，而中国是其中一个扩招目标市场。这就意味着，英国大学将对有意向的中国学生放低门槛，同时扩大各类奖学金的比重，鼓励中国青年才俊到英国学习。  去英国留学趋势二：竞争白热化申请名校多做准备  留学人数的增加一定会使竞争升级，申请海外名校已经从提前准备硬指标，升级为提前准备科研成果、实习经验、实践背景等条件。  去英国留学趋势三：留学趋于小龄化  低龄孩子提前出国享受国际化的优质教育，是2014年留学以及今后几年的大趋势，尤其是出国读本科的学生比例会越来越大。  去英国留学趋势四：留学目的的分化愈演愈烈  过去一年，选择留学时，两极分化的情况比较严重。受到经济危机影响较大的留学家庭，变得更为审慎、理性，咨询过程中更多地关注留学回报、奖学金、带薪实习机会;而受影响较少的人群，对孩子出国留学品质要求更高，希望申请全球前30的世界名校，帮孩子铺平留学后的出路。本文来源：http://gb.533.com/191/438257.html    在2014年的留学申请中选择一门适合自已的专业非常重要，但同时也要考虑到自已的兴趣爱好和职业规划，以及当前英国的人气热门专业，现在将英国最热门的专业推荐给大家：会计与金融， 电力与电子工程， 商务管理， 建筑， 法学， 教育学， 翻译与口译， 艺术设计，传媒。\", \"keyWord\" : \"\", \"title\" : \"去英国留学的趋势是怎么样的");
        StringBuilder stringBuilder = new StringBuilder();
        IKSegmenter segmenter = new IKSegmenter(reader, true);
        int count = 0;
        while ((lexeme = segmenter.next()) != null) {
            if (lexeme.getLexemeText().length() == 1) {
                continue;
            }
            count++;
            String word = lexeme.getLexemeText();

            stringBuilder.append(word + " ");
        }

        System.out.println(stringBuilder.toString());

    }

}
