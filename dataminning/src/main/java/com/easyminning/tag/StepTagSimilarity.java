package com.easyminning.tag;

import com.easyminning.etl.mahout.util.distance.EditDistance;
import com.easyminning.etl.mahout.writable.DocWordWeightService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2014/9/5.
 */
public class StepTagSimilarity {

    public void analysis() {
        DocWordWeightService docWordWeightService = new DocWordWeightService();
        docWordWeightService.init();

        List<String> words = docWordWeightService.findWordAll();

        EditDistance editDistance = EditDistance.getIntance();

        StepTagService stepTagService = StepTagService.getInstance();

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
