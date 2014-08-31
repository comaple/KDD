package com.easyminning.etl.mahout.util.similarity;

import java.util.Map;

/**
 * Created by comaple on 14-8-31.
 */
public interface Similarity {

    double Similarity(Map<String, Double> sourceMap, Map<String, Double> targetMap);
}
