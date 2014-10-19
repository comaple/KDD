package com.easyminning.tag;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-8-30
 * Time: 下午3:11
 * To change this template use File | Settings | File Templates.
 */
public class TagTag extends BaseModel  implements Comparable  {

    private String tagItem;

    private String tagItem1;

    private Double weight;

    private Double docCount;

    public String getTagItem() {
        return tagItem;
    }

    public void setTagItem(String tagItem) {
        this.tagItem = tagItem;
    }

    public String getTagItem1() {
        return tagItem1;
    }

    public void setTagItem1(String tagItem1) {
        this.tagItem1 = tagItem1;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getDocCount() {
        return docCount;
    }

    public void setDocCount(Double docCount) {
        this.docCount = docCount;
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
