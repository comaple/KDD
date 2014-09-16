package com.easyminning.util.simhash;

import com.easyminning.tag.ResultDocument;
import com.easyminning.tag.ResultDocumentService;
import com.easyminning.tag.TagDoc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2014/9/11.
 */
public class DuplicateDocFilter {


    private static Map<String,List<ResultDocument>> stringListMap = new HashMap<String, List<ResultDocument>>();


    /**
     * 获取文章的局部哈希值
     * @return
     */
    public static  String getAnasysisDocHash(String analysisResult) {
        String docHash = "";
        try {
            SimHash hash = new SimHash(analysisResult,true);
            docHash = hash.getStrSimHash();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return docHash;
    }

    /**
     * 获取文章的局部哈希值
     * @return
     */
    public static  String getAnasysisDocHash(ResultDocument resultDocument) {
        String docHash = "";
        try {
            SimHash hash = new SimHash(resultDocument.getResult(),true);
            docHash = hash.getStrSimHash();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return docHash;
    }

    /**
     * 获取文章的局部哈希值
     * @return
     */
    public static  String getDocHash(String content) {
        String docHash = "";
        try {
            SimHash hash = new SimHash(content,true);
            docHash = hash.getStrSimHash();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return docHash;
    }

    /**
     * 返回文章是否在数据库已经存在
     * @param content
     * @return
     */
    public static boolean isSimilarByDoc(String content) {
        int distance = 4;
        if (distance <= 3 ) {
            return true;
        } else {
            return  false;
        }
    }



    /**
     * 返回文章是否在数据库已经存在
     * @param docHash
     * @return
     */
    public static boolean isSimilarByDocHash(String docHash) {
        return true;
    }

    /**
     * 过滤掉重复的文章
     * @param tagDocList
     */
    public static void filterDuplicatedDoc(List<TagDoc> tagDocList) {

    }

    public static void filter() {
        ResultDocumentService resultDocumentService = ResultDocumentService.getInstance();
        List<ResultDocument> resultDocuments = resultDocumentService.getFingerMsgList();
        List<String> deleteDocIds = new ArrayList<String>();
        Map<String,ResultDocument> resultDocumentMap = new HashMap<String, ResultDocument>();
        for (ResultDocument resultDocument : resultDocuments) {
            resultDocumentMap.put(resultDocument.getDocId(),resultDocument);
            String[] fingerMsgArray = new String[4];
            String fingerMsg = resultDocument.getFingerMsg();
            fingerMsgArray[0] = fingerMsg.substring(0,16);
            fingerMsgArray[1] = fingerMsg.substring(16,32);
            fingerMsgArray[2] = fingerMsg.substring(32,48);
            fingerMsgArray[3] = fingerMsg.substring(48,64);

            boolean repeat = false;
            for (String fingerMsgGroup : fingerMsgArray) {
                List<ResultDocument> resultDocumentList = stringListMap.get(fingerMsgGroup) ;
                if (resultDocumentList == null) continue;

                for (ResultDocument temp : resultDocumentList) {
                    int distance = SimHash.getDistance(temp.getFingerMsg(),resultDocument.getFingerMsg());
                    if (distance<=3) {
                        repeat = true;
                        deleteDocIds.add(resultDocument.getDocId());
                        resultDocumentMap.get(temp.getDocId()).
                                setRepeatCount(resultDocumentMap.get(temp.getDocId()).getRepeatCount() + 1);
//                        System.out.println("docId->fingerMsg:" + resultDocument.getDocId()+ "->" + resultDocument.getFingerMsg()
//                                + " is repeated " + " simalar:" + temp.getDocId() + "->" + temp.getFingerMsg() + " distance：" + distance);
                        break;
                    }
                }
                if (repeat) break;
            }

            //
            if (!repeat) {
                for (String fingerMsgGroup : fingerMsgArray) {
                    List<ResultDocument> resultDocumentList = stringListMap.get(fingerMsgGroup) ;
                    if (resultDocumentList == null) {
                        resultDocumentList = new ArrayList<ResultDocument>();
                        stringListMap.put(fingerMsgGroup,resultDocumentList);
                    }
                    resultDocumentList.add(resultDocument);
                }
            }

        }

        List<ResultDocument> updateResultDocumentList = new ArrayList<ResultDocument>();
        for(ResultDocument resultDocument : resultDocumentMap.values()) {
            if (resultDocument.getRepeatCount() > 0) {
                updateResultDocumentList.add(resultDocument);
                System.out.println("update resultdocument: " + resultDocument.getDocId() + "->" + resultDocument.getRepeatCount());
            }
        }
        System.out.println("delete: " + deleteDocIds);
        ResultDocumentService.getInstance().deleteDocIds(deleteDocIds);
        ResultDocumentService.getInstance().updateDocRepeatCount(updateResultDocumentList);
    }

}
