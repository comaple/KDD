package com.easyminning.tag;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-8-30
 * Time: 下午4:48
 * To change this template use File | Settings | File Templates.
 */
public class TagDoc extends BaseModel  {

    private String tagItem;

    private String docItem;

    private Double weight;

    public String getTagItem() {
        return tagItem;
    }

    public void setTagItem(String tagItem) {
        this.tagItem = tagItem;
    }

    public String getDocItem() {
        return docItem;
    }

    public void setDocItem(String docItem) {
        this.docItem = docItem;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
