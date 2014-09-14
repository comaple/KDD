package com.easyminning.tag;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-9-3
 * Time: 下午11:30
 * To change this template use File | Settings | File Templates.
 */
public class LDAResultParser {

    /**
     * @param path
     * @return
     */
    public static Map<String, Map<String, Double>> getMap(String path) {
        Map<String, Map<String, Double>> res = new HashMap<String, Map<String, Double>>();
        try {
            // add by comaple.zhang 20140905
            FileReader reader = new FileReader(path);
            BufferedReader br = new BufferedReader(reader);
            String line = br.readLine();

            while (line != null) {
                String topicId = line.substring(0, line.indexOf("\t"));
                String value = line.substring(line.indexOf("{"));
                value = value.substring(1, value.length() - 1);
                String[] wordWeightArray = value.split(",");

                Map<String, Double> wordWeightMap = new HashMap<String, Double>();
                res.put(topicId, wordWeightMap);
                for (String wordWeight : wordWeightArray) {
                    wordWeightMap.put(wordWeight.split(":")[0], Double.valueOf(wordWeight.split(":")[1]));
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.printf("error :" + e.getMessage());
            System.out.printf("map:" + res);
        }
        return res;
    }


    public static void main(String[] args) {
     Map<String,Map<String,Double>>  map= getMap("/data/KDD/dataminning/src/main/resources/topic_result.txt");
        System.out.println(map.get("10"));
    }

}
