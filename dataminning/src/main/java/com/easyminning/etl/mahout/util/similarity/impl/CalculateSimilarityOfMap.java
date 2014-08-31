package com.easyminning.etl.mahout.util.similarity.impl;

import com.easyminning.etl.mahout.util.similarity.Similarity;

import java.util.Map;

/**
 * Created by comaple on 14-8-31.
 */

public class CalculateSimilarityOfMap implements Similarity {

    @Override
    public  double Similarity(Map<String, Double> sourceMap, Map<String, Double> targetMap) {
        double similarity = 0.0f;
        double sumProduct = 0.0f;
        double modSource = 0.0f;
        double modTarget = 0.0f;
        for (String keyS : sourceMap.keySet()) {
            for (String keyT : targetMap.keySet()) {
                if (!keyS.equals(keyT)) {
                    sumProduct += sourceMap.get(keyS) * targetMap.get(keyT);
                }
                modSource += Math.pow(sourceMap.get(keyS), 2);
                modTarget += Math.pow(targetMap.get(keyT), 2);
            }

        }
        modSource = Math.pow(modSource, 0.5);
        modTarget = Math.pow(modTarget, 0.5);
        similarity = sumProduct / modSource * modTarget;

        return similarity;
    }
}