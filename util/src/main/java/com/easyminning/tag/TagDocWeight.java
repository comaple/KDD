package com.easyminning.tag;

/**
 * Created by comaple on 14-9-4.
 */
public class TagDocWeight extends BaseModel implements Comparable {
    private String docname;
    private String word;
    private Double weight;

    public TagDocWeight() {
    }

    public TagDocWeight(String docname, String word, Double weight) {
        this.docname = docname;
        this.word = word;
        this.weight = weight;

    }


    public String getDocname() {
        return docname;
    }

    public void setDocname(String docname) {
        this.docname = docname;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }


    @Override
    public int compareTo(Object o) {
        TagDocWeight other = null;
        if (o instanceof TagDocWeight) {
            other = (TagDocWeight) o;

        } else {
            return -65536;
        }
        Double res = this.getWeight() - other.getWeight();
        if (res > 0) {
            return -1;
        } else {
            return 1;
        }


    }
}
