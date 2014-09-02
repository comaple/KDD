package com.easyminning.etl.mahout.docparse2word.map;

import com.easyminning.etl.mahout.util.Constant;
import com.easyminning.etl.mahout.writable.DocumentWritable;
import com.easyminning.mongodbclient2.util.DateUtil;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Date;

/**
 * Created by ZhangShengtao on 14-8-31.
 */
public class SplitMapper extends Mapper<LongWritable, Text, Text, DocumentWritable> {
    private static Lexeme lexeme = null;
    private static Text docId = new Text("1");
    private StringBuilder stringBuilder = new StringBuilder();
    private DocumentWritable documentWritable = new DocumentWritable();


    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        FileSplit fileSplit = (FileSplit) context.getInputSplit();
//        DocumentWritable documentWritable = new DocumentWritable();
        String fileName = fileSplit.getPath().getName();
//        String name = fileName.substring(fileName.lastIndexOf(File.separator), fileName.length() - 1);
        StringReader reader = new StringReader(value.toString());
        IKSegmenter segmenter = new IKSegmenter(reader, true);
//        System.err.println("-----------");
//        System.err.println(value.toString().length());
        while ((lexeme = segmenter.next()) != null && lexeme.getLexemeText().length() != 1 && !lexeme.getLexemeText().equals("nbsp")) {

            stringBuilder.append(lexeme.getLexemeText().trim().replaceAll("nbsp", "") + " ");
        }
//        System.err.println(stringBuilder.toString());
        documentWritable.setDocId(new Text(fileName));
        documentWritable.setDocContent(value);
        documentWritable.setResult(new Text(stringBuilder.toString()));
        documentWritable.setIssue(new Text(DateUtil.format(new Date(), DateUtil.YYYYMMDD)));
        documentWritable.setWeihgt(new DoubleWritable(1));
//        context.write(docId, documentWritable);

    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        context.write(docId, documentWritable);

    }
}
