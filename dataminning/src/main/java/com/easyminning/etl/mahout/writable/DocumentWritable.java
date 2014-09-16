package com.easyminning.etl.mahout.writable;

import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.mahout.math.VarLongWritable;

import javax.print.Doc;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by comaple on 14-8-31.
 */
public class DocumentWritable extends VarLongWritable {
    // 文章ID
    private Text docId = new Text();
    // 文章正文
    private Text docContent = new Text();
    // 标题
    private Text title = new Text();
    // 关键词
    private Text keyWord = new Text();
    // 摘要
    private Text summary = new Text();
    //原文
    private Text sourceContent = new Text();
    //url 地址
    private Text url = new Text();
    //发布时间
    private Text issue = new Text();
    //结果
    private Text result = new Text();
    //作者
    private Text author = new Text();
    //权重
    private DoubleWritable weihgt = new DoubleWritable();

    // 文章类型
    private Text type = new Text();


    public DocumentWritable() {
    }

    @Override
    public void write(DataOutput out) throws IOException {
        super.write(out);
        this.docId.write(out);
        this.docContent.write(out);
        this.sourceContent.write(out);
        this.issue.write(out);
        this.keyWord.write(out);
        this.result.write(out);
        this.summary.write(out);
        this.title.write(out);
        this.url.write(out);
        this.author.write(out);
        this.weihgt.write(out);
        this.type.write(out);

    }

    @Override
    public void readFields(DataInput in) throws IOException {
        super.readFields(in);
        this.docId.readFields(in);
        this.docContent.readFields(in);
        this.sourceContent.readFields(in);
        this.issue.readFields(in);
        this.keyWord.readFields(in);
        this.result.readFields(in);
        this.summary.readFields(in);
        this.title.readFields(in);
        this.url.readFields(in);
        this.author.readFields(in);
        this.weihgt.readFields(in);
        this.type.readFields(in);
    }

    public Text getDocId() {
        return docId;
    }

    public void setDocId(Text docId) {
        this.docId = docId;
    }

    public Text getDocContent() {
        return docContent;
    }

    public void setDocContent(Text docContent) {
        this.docContent = docContent;
    }

    public Text getTitle() {
        return title;
    }

    public void setTitle(Text title) {
        this.title = title;
    }

    public Text getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(Text keyWord) {
        this.keyWord = keyWord;
    }

    public Text getSummary() {
        return summary;
    }

    public void setSummary(Text summary) {
        this.summary = summary;
    }

    public Text getSourceContent() {
        return sourceContent;
    }

    public void setSourceContent(Text sourceContent) {
        this.sourceContent = sourceContent;
    }

    public Text getUrl() {
        return url;
    }

    public void setUrl(Text url) {
        this.url = url;
    }

    public Text getIssue() {
        return issue;
    }

    public void setIssue(Text issue) {
        this.issue = issue;
    }

    public Text getResult() {
        return result;
    }

    public void setResult(Text result) {
        this.result = result;
    }

    public Text getAuthor() {
        return author;
    }

    public void setAuthor(Text author) {
        this.author = author;
    }

    public DoubleWritable getWeihgt() {
        return weihgt;
    }

    public void setWeihgt(DoubleWritable weihgt) {
        this.weihgt = weihgt;
    }

    public Text getType() {
        return type;
    }

    public void setType(Text type) {
        this.type = type;
    }
}
