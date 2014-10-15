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
    private TagTagService tagTagService;
    private HotTagService hotTagService;

    public static StepTagSimilarity getInstance() {
        return stepTagSimilarity;
    }

    private StepTagSimilarity() {
        tagDocService = TagDocService.getInstance();
        editDistance = EditDistance.getIntance();
        stepTagService = StepTagService.getInstance();
        tagTagService = TagTagService.getInstance();
        hotTagService = HotTagService.getInstance();
    }

    public void analysis(Path inputPath, Configuration conf) {
        Map<String,Double> wordFrequency = this.getWordFrequency(inputPath, conf);
        Set<String> words = wordFrequency.keySet();
        List<StepTag> stepTagList = new ArrayList<StepTag>();
        for (String word : words) {
            if (word == null || "".equals(word.trim())) continue;
            if (!TagFilter.filterTag(word)) continue;
            List<StepTag> stepTagList1 = editDistance.getTagSimilarityStep(word);
            for (StepTag temp : stepTagList1) {
                temp.setTagFrequency(wordFrequency.get(word));
            }
            stepTagList.addAll(stepTagList1);
        }
        stepTagService.saveList(stepTagList);
    }



    public void analysis2(Path inputPath, Configuration conf) {
        Map wordFrequencyAndDocCount = StepTagSimilarity.getInstance().getWordFrequencyAndDocCount(inputPath,conf);
        Map<String,Double> wordFrequency = (Map<String,Double>)wordFrequencyAndDocCount.get("wordFrequency");
        int docCount = (Integer)wordFrequencyAndDocCount.get("docCount");
        //Map<String,Double> wordFrequency = this.getWordFrequency(inputPath, conf);

        int count = 0;
        // 按词频率排序
        List<HotTag> hotTagList = new ArrayList<HotTag>();
        for (String word : wordFrequency.keySet()) {
            HotTag hotTag = new HotTag();
            hotTag.setTagItem(word);
            hotTag.setWeight(wordFrequency.get(word));
            hotTagList.add(hotTag);
        }
        Collections.sort(hotTagList);

        List<HotTag> result = new ArrayList<HotTag>();
        for (HotTag hotTag : hotTagList) {
            Map map = new HashMap();
            if (hotTag == null || "".equals(hotTag.getTagItem().trim())) continue;
            if (!TagFilter.filterTag(hotTag.getTagItem())) continue;

            List<TagTag> tagTagList = tagTagService.findTagByTag(hotTag.getTagItem(),1,100);

            StringBuilder sb = new StringBuilder();
            for (TagTag tagTag : tagTagList) {
                sb.append(tagTag.getTagItem1());
                sb.append(":");
                sb.append(tagTag.getWeight() );
//                sb.append(tagTag.getWeight()/docCount * Math.log(docCount/wordFrequency.get(tagTag.getTagItem1())));
//                tagTag.setWeight(tagTag.getWeight()/docCount * Math.log(docCount/wordFrequency.get(tagTag.getTagItem1())));
                sb.append(",");
            }
//            Collections.sort(tagTagList);
//            StringBuilder stringBuilder = new StringBuilder();
//            for (TagTag tagTag : tagTagList) {
//                sb.append(tagTag.getTagItem1());
//                stringBuilder.append(tagTag.getTagItem1());
//                sb.append(",");
//            }
            //System.out.println(stringBuilder.toString());
            count++;
            map.put("articleCount", wordFrequency.get(hotTag.getTagItem()));
            map.put("relatedTag", sb.toString());
            hotTag.setTagInfo(map.toString());
            result.add(hotTag);
            if (count > 100) break;
        }
        hotTagService.saveHotTagList(result);

    }


    /**
     * 统计词的词频
     * @param path
     * @param conf
     * @return
     */
    public   Map getWordFrequencyAndDocCount(Path path, Configuration conf) {
        Map wordFrequencyAndDocCount = new HashMap();
        Map<String,Double> res = new HashMap<String, Double>();
        int count = 0;
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
                        count++;
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
        wordFrequencyAndDocCount.put("docCount", new Integer(count));
        wordFrequencyAndDocCount.put("wordFrequency", res);
        return wordFrequencyAndDocCount;
    }




    /**
     * 统计词的词频
     * @param path
     * @param conf
     * @return
     */
    public   Map<String,Double> getWordFrequency(Path path, Configuration conf) {
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
    public static void main(String[] args) throws Exception {
//        // window下连接hadoop2需要
        File file = new File(".");
        System.getProperties().put("hadoop.home.dir", file.getAbsolutePath());
        new File("./bin").mkdirs();
        new File("./bin/winutils.exe").createNewFile();
        Configuration con = new Configuration();
        con.set("fs.default.name", "hdfs://master:9000");
        String sequenceFilePath = "/test";
        StepTagSimilarity.getInstance().analysis2(new Path(sequenceFilePath), con);
        Map wordFrequencyAndDocCount = StepTagSimilarity.getInstance().getWordFrequencyAndDocCount(new Path(sequenceFilePath),con);
       Map wordFrequency = (Map<String,Double>)wordFrequencyAndDocCount.get("wordFrequency");
        int docCount = (Integer)wordFrequencyAndDocCount.get("docCount");
//        System.out.println(docCount);
    }

}
