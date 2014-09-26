package com.easyminning.tag;

/**
 * Created by Administrator on 2014/9/26.
 */
public class LogRecord extends BaseModel  {
    private String type;

    private String operTime;

    private String content;

    public LogRecord(String type, String operTime,String content) {
        this.type = type;
        this.operTime = operTime;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOperTime() {
        return operTime;
    }

    public void setOperTime(String operTime) {
        this.operTime = operTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
