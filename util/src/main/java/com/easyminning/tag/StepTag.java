package com.easyminning.tag;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-8-30
 * Time: 下午4:48
 * To change this template use File | Settings | File Templates.
 */
public class StepTag extends BaseModel {

    private String stepItem;

    private String tagItem;

    private Double weight;

    private Double tagFrequency;

    public String getStepItem() {
        return stepItem;
    }

    public void setStepItem(String stepItem) {
        this.stepItem = stepItem;
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


    public Double getTagFrequency() {
        return tagFrequency;
    }

    public void setTagFrequency(Double tagFrequency) {
        this.tagFrequency = tagFrequency;
    }
}
