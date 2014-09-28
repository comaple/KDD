package com.easyminning.tag;

import com.easyminning.etl.mahout.util.distance.EditDistance;
import com.easyminning.util.date.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/9/5.
 */
public class StepTagSimilarity {
    private static StepTagSimilarity stepTagSimilarity = new StepTagSimilarity();

    private TagDocService tagDocService;
    private EditDistance editDistance;
    private StepTagService stepTagService;

    public static StepTagSimilarity getInstance() {
        return stepTagSimilarity;
    }

    private StepTagSimilarity() {
        tagDocService = TagDocService.getInstance();
        editDistance = EditDistance.getIntance();
        stepTagService = StepTagService.getInstance();
    }


    public void analysis() {
        List<String> words = tagDocService.findWordAll();
        LogRecordService.getInstance().save(new LogRecord("2", DateUtil.getCurrentFriendlyTime()," 计算步骤与标签相似度，标签总数：" + words.size()));
        List<StepTag> stepTagList = new ArrayList<StepTag>();
        for (String word : words) {
            if (word == null || "".equals(word.trim())) continue;
            StepTag stepTag = editDistance.getTagSimilarityStep(word);
            stepTagList.add(stepTag);
        }
        stepTagService.saveList(stepTagList);
    }

    public static void main(String[] args) {
        StepTagSimilarity stepTagSimilarity = new StepTagSimilarity();
        stepTagSimilarity.analysis();
    }




}
