package com.easyminning.etl.mahout.util.distance;


import com.easyminning.tag.StepSeedCache;
import com.easyminning.tag.StepTag;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EditDistance {

    private static EditDistance editDistance = new EditDistance();

    private  Map<String,Map<String,Double>> stepSeedMap;

    /**
     * 步骤 -> 标签 -> 权重
     */

    public static void main(String[] args) {
        String word2 = "中国";

        StepSeedCache stepSeedCache = new StepSeedCache();
        stepSeedCache.init();
        Map<String,Map<String,Double>> STEP_SEED_MAP = StepSeedCache.STEP_SEED_MAP;
      //  String step = getTagSimilarityStep("澳大利亚");
      //  System.out.println(step);
    }

    private EditDistance() {
        StepSeedCache stepSeedCache = new StepSeedCache();
        stepSeedCache.init();
        this.stepSeedMap = StepSeedCache.STEP_SEED_MAP;
    }

    public static EditDistance getIntance() {
        return editDistance;
    }

    /**
     * 返回标签近似的步骤
     * @param calTag 计算的标签
     * @return
     */
    public  StepTag getTagSimilarityStep(String calTag) {
        Set<String> stepSet = stepSeedMap.keySet();
        String selectStepName = "";
        Double selectStepWeight = Integer.MAX_VALUE + 0.0;

        for (String step : stepSet) {
            Set<String> tagSet = stepSeedMap.get(step).keySet();
            int currentStepWeight = getWordInstance(calTag,tagSet);
            if (currentStepWeight < selectStepWeight) {
                selectStepWeight = currentStepWeight + 0.0;
                selectStepName = step;
            }
        }

        StepTag stepTag = new StepTag();
        stepTag.setStepItem(selectStepName);
        stepTag.setTagItem(calTag);
        stepTag.setWeight(selectStepWeight);
        return stepTag;
    }


    /**
     * 计算单词到集合的编辑距离
     * @param word
     * @param wordSet
     * @return
     */
    private static int getWordInstance(String word, Set<String> wordSet) {
        int distance = Integer.MAX_VALUE;
        for (String tmp : wordSet) {
            int dis = getWordIntance(word,tmp);
            if (dis < distance) {
                distance = dis;
            }
        }
        return distance;
    }


    /**
     * 计算两个词之间的编辑距离。
     * @param word1
     * @param word2
     * @return
     */
    private static int getWordIntance(String word1, String word2) {
        int distance = 0;
        char[] mychar1 = word1.toCharArray();
        char[] mychar2 = word2.toCharArray();
        int len1 = mychar1.length;
        int len2 = mychar2.length;
        int cost;
        int boundaryx = len1 + 1;
        int boundaryy = len2 + 1;
        int[][] dis = new int[boundaryx][boundaryy];
        for (int i = 0; i <= len1; i++) dis[i][0] = i;
        for (int j = 0; j <= len2; j++) dis[0][j] = j;
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (mychar1[i - 1] == mychar2[j - 1]) cost = 0;
                else cost = 1;
                dis[i][j] = min(dis[i - 1][j] + 1, dis[i][j - 1] + 1, dis[i - 1][j - 1] + cost);
            }
        }

        distance = dis[len1][len2];
        return distance;
    }

    //返回三个int的最小数
    private static int min(int a, int b, int c) {
        int min = a;
        if (b < min) min = b;
        if (c < min) min = c;
        return min;
    }
}

