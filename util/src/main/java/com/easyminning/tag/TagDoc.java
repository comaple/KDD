package com.easyminning.tag;

/**
 * Created by comaple on 14-9-4.
 */
public class TagDoc extends BaseModel implements Comparable {
    private String docItem;
    private String tagItem;
    private Double weight;

    public TagDoc() {
    }

    public TagDoc(String docItem, String tagItem, Double weight) {
        this.docItem = docItem;
        this.tagItem = tagItem;
        this.weight = weight;
    }

    public String getDocItem() {
        return docItem;
    }

    public void setDocItem(String docItem) {
        this.docItem = docItem;
    }

    public String getTagItem() {
        return tagItem;
    }

    public void setTagItem(String tagItem) {
        this.tagItem = tagItem;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    @Override
    public int compareTo(Object o) {
        TagDoc other = null;
        if (o instanceof TagDoc) {
            other = (TagDoc) o;

        } else {
            return -65536;
        }
        Double res = this.getWeight() - other.getWeight();
        if (res > 0) {
            return 1;
        } else {
            return -1;
        }


    }
}
