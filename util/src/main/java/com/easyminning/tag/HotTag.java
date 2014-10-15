package com.easyminning.tag;

/**
 * Created by Administrator on 2014/9/26.
 */
public class HotTag extends BaseModel implements Comparable  {
    private String tagItem;

    private String tagInfo;

    private Double weight;

    public String getTagItem() {
        return tagItem;
    }

    public void setTagItem(String tagItem) {
        this.tagItem = tagItem;
    }

    public String getTagInfo() {
        return tagInfo;
    }

    public void setTagInfo(String tagInfo) {
        this.tagInfo = tagInfo;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    @Override
    public int compareTo(Object o) {
        HotTag other = null;
        if (o instanceof HotTag) {
            other = (HotTag) o;

        } else {
            return -65536;
        }
        Double res = this.getWeight() - other.getWeight();
        if (res > 0) {
            return -1;
        } else if (res == 0) {
            return 0;
        } else {
            return 1;
        }


    }
}
