package com.easyminning.algorithm.fpm.pfpgrowth.result;

import com.easyminning.algorithm.fpm.pfpgrowth.convertors.string.TopKStringPatterns;
import com.easyminning.tag.TagTag;
import com.easyminning.tag.TagTagService;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.mahout.common.Pair;

import javax.swing.text.html.HTML;
import java.io.IOException;
import java.util.*;

/**
 * Created by Administrator on 2014/9/6.
 */
public class FpgrowthResultMaper extends Mapper<Text,TopKStringPatterns,Text,TopKStringPatterns> {

    public static Integer TOP_K = 50;

    private TagTagService tagTagService = TagTagService.getInstance();

    @Override
    protected void map(Text key, TopKStringPatterns value, Context context) throws IOException, InterruptedException {
        List<Pair<List<String>,Long>> pairs =  value.getPatterns();
        Map<String,TagTag> tagTagMap = new HashMap<String, TagTag>();

        for (Pair<List<String>,Long> pair : pairs) {
            List<String> words = pair.getFirst();
            Long weight = pair.getSecond();
           for (String word : words) {
               if (word.equals(key.toString())) continue;
               TagTag tagTag = null;

               // 存在，更新权重
               if (tagTagMap.containsKey(word)) {
                   tagTag = tagTagMap.get(word);
                   tagTag.setWeight(weight + tagTag.getWeight());
               } else { // 不存在，新建
                   tagTag = new TagTag();
                   tagTag.setWeight(weight + 0.0);
                   tagTag.setTagItem(key.toString());
                   tagTag.setTagItem1(word);
               }
               tagTagMap.put(word, tagTag);
           }
        }

        Collection<TagTag> collection = tagTagMap.values();
        List<TagTag> tagTagList = new ArrayList<TagTag>();
        for (TagTag tag : collection) {
            tagTagList.add(tag);
        }
        if (tagTagList.size() > 0)
            tagTagService.saveTagTagList(tagTagList);
    }

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
    }
}
