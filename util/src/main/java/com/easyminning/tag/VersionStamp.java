package com.easyminning.tag;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-9-10
 * Time: 下午10:52
 * To change this template use File | Settings | File Templates.
 */
public class VersionStamp extends BaseModel {

    // 0. 表示正在进行执行分析的版本，1，表示已经可以使用的版本
    private Integer finshedVersion;

    public Integer getFinshedVersion() {
        return finshedVersion;
    }

    public void setFinshedVersion(Integer finshedVersion) {
        this.finshedVersion = finshedVersion;
    }
}
