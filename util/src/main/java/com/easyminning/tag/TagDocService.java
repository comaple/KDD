package com.easyminning.tag;

import com.easyminning.util.filter.ResultDocumentFilter;
import com.mongodb.BasicDBObject;
import com.mongodb.QueryBuilder;
import com.mongodb.QueryOperators;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.StringReader;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-8-31
 * Time: 下午2:23
 * To change this template use File | Settings | File Templates.
 */
public class TagDocService extends AbstractService<TagDoc> {

    private static String HIGH_FREQUENCY_WORDS = "留学,学校";

    private static TagDocService tagDocService = new TagDocService();

    private TagDocService() {
        this.init();
    }

    public static TagDocService getInstance() {
        return tagDocService;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = "docwordweight";
    }

    public void save(TagDoc tagDoc) {
        VersionStamp versionStamp = versionStampService.getUnFinshedVersionStamp();
        if (versionStamp == null) {
            log.error("versionstamp is null");
            return ;
        }

        tagDoc.setVersionStamp(versionStamp.getVersionStamp());
        simpleMongoDBClient2.insert(tagDoc);
    }

    public void saveList(List<TagDoc> tagDocList) {
        VersionStamp versionStamp = versionStampService.getUnFinshedVersionStamp();
        if (versionStamp == null) {
            log.error("versionstamp is null");
            return ;
        }
        List<TagDoc> tempList = new ArrayList<TagDoc>();
        for (TagDoc temp : tagDocList) {
            temp.setVersionStamp(versionStamp.getVersionStamp());
            tempList.add(temp);

            if (tempList.size() % BATCH_SIZE_MAX == 0) {
                simpleMongoDBClient2.insert(tempList);
                tempList.clear();
            }
        }

        if (tempList.size() > 0 ) {
            simpleMongoDBClient2.insert(tempList);
        }

    }

    public List<String> findWordAll() {
        VersionStamp versionStamp = versionStampService.getUnFinshedVersionStamp();
        if (versionStamp == null) {
            log.error("versionstamp is null");
            return null;
        }
        List<String> res = simpleMongoDBClient2.collection.distinct("tagItem",new BasicDBObject("versionStamp", versionStamp.getVersionStamp()));
        return res;
    }

    public void deleteDocIds(List<String> docIds,VersionStamp versionStamp) {
        BasicDBObject queryCondition = new BasicDBObject();
        queryCondition.put("versionStamp", versionStamp.getVersionStamp());
        queryCondition.put("docItem",new BasicDBObject(QueryOperators.IN,docIds.toArray()));
        this.simpleMongoDBClient2.collection.remove(queryCondition);
    }

//    public List<TagDoc> findDocByTag(String tagItem, Integer pageNo, Integer pageSize) {
//        QueryBuilder queryBuilder = QueryBuilder.start("tagItem").is(tagItem);
//        QueryBuilder queryBuilderSort = QueryBuilder.start("weight").is(-1);
//        VersionStamp versionStamp = versionStampService.getLatestFinshedVersionStamp();
//        if (versionStamp != null) {
//            queryBuilder.and("versionStamp").is(versionStamp.getVersionStamp());
//        }
//        List<TagDoc> tagDocList = this.simpleMongoDBClient2.select(queryBuilder,queryBuilderSort,pageNo,pageSize,TagDoc.class);
//        return tagDocList;
//    }

    public List<TagDoc> findDocByTag(String[] tagItem, Integer pageNo, Integer pageSize) {
        QueryBuilder queryBuilder = QueryBuilder.start("tagItem").in(tagItem);
        QueryBuilder queryBuilderSort = QueryBuilder.start("weight").is(-1);
        VersionStamp versionStamp = versionStampService.getLatestFinshedVersionStamp();
        if (versionStamp == null) {
            log.error("versionstamp is null");
            return new ArrayList<TagDoc>();
        }

        queryBuilder.and("versionStamp").is(versionStamp.getVersionStamp());
        List<TagDoc> tagDocList = this.simpleMongoDBClient2.select(queryBuilder,queryBuilderSort,pageNo,pageSize,TagDoc.class);
        return tagDocList;
    }

    public List<TagDoc> parseWords(String content) throws Exception {
        StringReader reader = new StringReader(content);
        IKSegmenter segmenter = new IKSegmenter(reader, true);
        Lexeme lexeme = null;
        Map<String, Double> targetMap = new HashMap<String, Double>();
        List<TagDoc> tagDocList = new ArrayList<TagDoc>();

        // 分词并记录 count 总数，计算word权重
        while ((lexeme = segmenter.next()) != null) {
            if (lexeme.getLexemeText().length() == 1) {
                continue;
            }
            String word = lexeme.getLexemeText();

            // 过滤掉一些分词
            if (!ResultDocumentFilter.filterLexeme(word)) continue;
            if (targetMap.containsKey(word)) {
                targetMap.put(word, targetMap.get(word) + 1d);
            } else {
                targetMap.put(word, 1d);
            }
        }

        for (String word : targetMap.keySet()) {
            if (HIGH_FREQUENCY_WORDS.contains(word)) continue;
            TagDoc tagDoc = new TagDoc();
            tagDoc.setTagItem(word);
            tagDoc.setWeight(targetMap.get(word));
            tagDocList.add(tagDoc);
        }
        Collections.sort(tagDocList);
        if (tagDocList.size() > 5) {
            return tagDocList.subList(0, 5);
        } else {
            return tagDocList;
        }

    }




    public static void main(String[] args) throws Exception {
        TagDocService tagDocService = TagDocService.getInstance();
      //  tagDocService.save(new TagDoc());

      // docWordWeightService.save(new DocWordWeightModel());
      // List<String> models = tagDocService.findWordAll();
        String str = " 　在日本留学\n" ;

        List<TagDoc> tagDocList = tagDocService.parseWords(str);
        for (TagDoc tagDoc : tagDocList) {
            System.out.println(tagDoc.getTagItem());
        }

    }

}
