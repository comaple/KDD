package com.easyminning.tag;

import org.jcp.xml.dsig.internal.dom.DOMUtils;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-8-31
 * Time: 下午12:54
 * To change this template use File | Settings | File Templates.
 */
public class ResultDocument {

    private String docId = new String();

    // 文章正文
    private String docContent = new String();
    // 标题
    private String title = new String();
    // 关键词
    private String keyWord = new String();
    // 摘要
    private String summary = new String();
    //原文
    private String sourceContent = new String();
    //url 地址
    private String url = new String();
    //发布时间
    private String issue = new String();
    //分词结果
    private String result = new String();
    //作者
    private String author = new String();

    // 权重
    private Double weight = 0.0;

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getDocContent() {
        return docContent;
    }

    public void setDocContent(String docContent) {
        this.docContent = docContent;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSourceContent() {
        return sourceContent;
    }

    public void setSourceContent(String sourceContent) {
        this.sourceContent = sourceContent;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
