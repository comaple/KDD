package com.easyminning.view.web;

import com.easyminning.tag.LogRecord;
import com.easyminning.tag.LogRecordService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by Administrator on 2014/9/29.
 */
public class LogRecordController extends BaseController {

    LogRecordService logRecordService = LogRecordService.getInstance();

    @RequestMapping(value = "/logRecord", method = RequestMethod.GET)
    public void test(String type, Integer pageSize, Integer pageNo) {
        List<LogRecord> list = logRecordService.findListByType(type, pageNo,pageSize);
        renderJson(list);
    }
}
