package com.easyminning.tag;

import com.easyminning.aprio.TagFilter;
import com.easyminning.etl.mahout.util.distance.EditDistance;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.ReflectionUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

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


    public void analysis(Path inputPath, Configuration conf) {
        Map<String,Double> wordFrequency = this.getWordFrequency(inputPath, conf);
        Set<String> words = wordFrequency.keySet();
        List<StepTag> stepTagList = new ArrayList<StepTag>();
        for (String word : words) {
            if (word == null || "".equals(word.trim())) continue;
            if (!TagFilter.filterTag(word)) continue;
            StepTag stepTag = editDistance.getTagSimilarityStep(word);
            stepTag.setTagFrequency(wordFrequency.get(word));
            stepTagList.add(stepTag);
        }
        stepTagService.saveList(stepTagList);
    }

    /**
     * 统计词的词频
     * @param path
     * @param conf
     * @return
     */
    private  Map<String,Double> getWordFrequency(Path path, Configuration conf) {
        Map<String,Double> res = new HashMap<String, Double>();
        try {
            FileSystem fs = FileSystem.get(conf);
            FileStatus[] fileStatuses =  fs.listStatus(path);
            for (FileStatus fileStatus : fileStatuses) {
                FSDataInputStream fsDataInputStream = null;
                BufferedReader bis = null;
                try {
                    if (fileStatus.isDir()) continue;
                    fsDataInputStream = fs.open(fileStatus.getPath());
                    bis = new BufferedReader(new InputStreamReader(fsDataInputStream,"UTF-8"));
                    String temp;
                    while ((temp = bis.readLine()) != null) {
                        String[] words = temp.split(",");
                        for (String word :words) {
                            if (res.containsKey(word)) {
                                res.put(word,res.get(word) + 1.0);
                            } else {
                                res.put(word,1.0D);
                            }
                        }
                    } // while
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fsDataInputStream != null) {
                        fsDataInputStream.close();
                    }
                    if (bis != null) {
                        bis.close();
                    }
                }

            } // filestatus
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
    public static void main(String[] args) {
        Configuration con = new Configuration();
        con.set("fs.default.name", "hdfs://master:9000");
        String sequenceFilePath = "/test";
        //getWordFrequency(sequenceFilePath, con);
    }




}
