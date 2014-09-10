package com.easyminning.tag;

import java.util.Map;

/**
 * Created by Administrator on 2014/9/5.
 */
public class BaseModel {

    private Map _id;

    public Map get_id() {
        return _id;
    }

    public void set_id(Map _id) {
        this._id = _id;
    }

    private String versionStamp; // 版本时间戳

    public String getVersionStamp() {
        return versionStamp;
    }

    public void setVersionStamp(String versionStamp) {
        this.versionStamp = versionStamp;
    }
}
