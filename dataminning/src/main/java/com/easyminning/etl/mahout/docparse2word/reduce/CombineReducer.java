package com.easyminning.etl.mahout.docparse2word.reduce;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;

/**
 * Created by ZhangShengtao on 14-7-21.
 */
public class CombineReducer extends Reducer<Text, Text, Text, Text> {
    private static Lexeme lexeme = null;

    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder stringBuilder_lines = new StringBuilder();
        for (Text line : values) {
            stringBuilder_lines.append(line + " ");
        }
        StringReader reader = new StringReader(stringBuilder_lines.toString());
        IKSegmenter segmenter = new IKSegmenter(reader, true);

        while ((lexeme = segmenter.next()) != null && lexeme.getLexemeText().length() != 1) {
            if (isNumber(lexeme.getLexemeText())) {
                continue;
            }
            stringBuilder.append(lexeme.getLexemeText() + " ");
        }
        int uid = 0;
        try {
            uid = Integer.parseInt(key.toString());
        } catch (Exception e) {
            uid = 0;
            System.err.println(key.toString());
        }
        context.write(key, new Text(stringBuilder.toString()));
    }

    /**
     * 判断是否是数字，纯数字对于话题无意义直接干掉
     *
     * @param src
     * @return
     */
    private boolean isNumber(String src) {
        boolean flage = true;
        for (Character c : src.toCharArray()) {
            if (!Character.isDigit(c)) {
                flage = false;
                break;
            }
        }
        return flage;
    }

}
