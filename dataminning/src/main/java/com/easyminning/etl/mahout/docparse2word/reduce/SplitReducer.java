package com.easyminning.etl.mahout.docparse2word.reduce;

import com.easyminning.etl.mahout.util.Constant;
import com.easyminning.etl.mahout.writable.DocumentWritable;
import com.easyminning.tag.ResultDocument;
import com.easyminning.tag.ResultDocumentService;
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

    private ResultDocumentService resultDocumentService = null;
    private int resultNum = 1000;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        super.setup(context);
        resultNum = Integer.parseInt(context.getConfiguration().get(Constant.RESULT_NUM) == null ? "1000" : context.getConfiguration().get(Constant.RESULT_NUM));
        //初始化mogodb
        resultDocumentService = new ResultDocumentService();
        resultDocumentService.init();

    }

    @Override
    protected void reduce(Text key, Iterable<DocumentWritable> values, Context context) throws IOException, InterruptedException {
        List<DocumentWritable> list = new LinkedList<DocumentWritable>();
        for (DocumentWritable documentWritable : values) {
            list.add(documentWritable);
        }
        Collections.sort(list, new Comparator<DocumentWritable>() {
            @Override
            public int compare(DocumentWritable o, DocumentWritable o2) {
                double result = o.getWeihgt().get() - o2.getWeihgt().get();
                if (result > 0) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });

        list = list.subList(0, resultNum - 1);
        for (DocumentWritable doc : list) {
            ResultDocument resultDocument = constructDoc(doc);
            //写mongodb
            resultDocumentService.save(resultDocument);
            //写reduce 文件
            context.write(doc.getDocId(), doc.getResult());
        }

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
        return resultDocument;
    }

    public static void main(String[] args) {
        Text t = new Text();
        System.out.println(t.toString());
    }

}
