package com.easyminning.etl.mahout.docparse2word.reduce;

import com.easyminning.etl.mahout.util.Constant;
import com.easyminning.etl.mahout.writable.DocumentWritable;
import com.easyminning.tag.ResultDocument;
import com.easyminning.tag.ResultDocumentService;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * Created by ZhangShengtao on 14-7-21.
 */
public class SplitReducer extends Reducer<Text, DocumentWritable, Text, Text> {

    private ResultDocumentService resultDocumentService = ResultDocumentService.getInstance();
    private int resultNum = 1000;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
        //初始化mogodb
    }

    @Override
    protected void reduce(Text key, Iterable<DocumentWritable> values, Context context) throws IOException, InterruptedException {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder docContent = new StringBuilder();
        DocumentWritable doc = new DocumentWritable();
        for (DocumentWritable documentWritable : values) {
            stringBuilder.append(documentWritable.getResult().toString() + " ");
            docContent.append(documentWritable.getDocContent().toString());
            doc = documentWritable;
        }
        doc.setResult(new Text(stringBuilder.toString()));
        doc.setDocContent(new Text(docContent.toString()));
//        Collections.sort(list, new Comparator<DocumentWritable>() {
//            @Override
//            public int compare(DocumentWritable o, DocumentWritable o2) {
//                double result = o.getWeihgt().get() - o2.getWeihgt().get();
//                if (result > 0) {
//                    return 1;
//                } else {
//                    return -1;
//                }
//            }
//        });
//
//        list = list.subList(0, list.size() <= (resultNum) ? list.size() - 1 : resultNum - 1);
        ResultDocument resultDocument = constructDoc(doc);
        System.out.println(resultDocument.getDocId());

        //写mongodb
        resultDocumentService.save(resultDocument);
        //写reduce 文件
        context.write(doc.getDocId(), doc.getResult());

//        context.write(doc.getResult(), new Text());


    }

    private ResultDocument constructDoc(DocumentWritable documentWritable) {
        ResultDocument resultDocument = new ResultDocument();
        resultDocument.setAuthor(documentWritable.getAuthor().toString());
        resultDocument.setDocId(documentWritable.getDocId().toString());
        resultDocument.setDocContent(documentWritable.getDocContent().toString());
        resultDocument.setIssue(documentWritable.getIssue().toString());
        resultDocument.setKeyWord(documentWritable.getKeyWord().toString());
        resultDocument.setResult(documentWritable.getResult().toString());
        resultDocument.setSourceContent(documentWritable.getSourceContent().toString());
        resultDocument.setSummary(documentWritable.getSummary().toString());
        resultDocument.setTitle(documentWritable.getTitle().toString());
        resultDocument.setUrl(documentWritable.getUrl().toString());
        resultDocument.setWeight(documentWritable.getWeihgt().get());
        resultDocument.setType(documentWritable.getType().toString());
        return resultDocument;
    }

    public static void main(String[] args) {
        Text t = new Text();
        System.out.println(t.toString());
        boolean is_bol=true;
        is_bol=Boolean.valueOf("true");
        System.out.println(is_bol=false);
    }

}
