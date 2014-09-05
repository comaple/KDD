package com.easyminning.tag;

import com.easyminning.etl.mahout.util.distance.EditDistance;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2014/9/5.
 */
public class StepTagSimilarity {
    private static StepTagSimilarity stepTagSimilarity = new StepTagSimilarity();

    private DocWordWeightService docWordWeightService;
    private EditDistance editDistance;
    private StepTagService stepTagService;

    public static StepTagSimilarity getInstance() {
        return stepTagSimilarity;
    }

    private StepTagSimilarity() {
        docWordWeightService = DocWordWeightService.getInstance();
        editDistance = EditDistance.getIntance();
        stepTagService = StepTagService.getInstance();
    }


    public void analysis() {
        List<String> words = docWordWeightService.findWordAll();
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
