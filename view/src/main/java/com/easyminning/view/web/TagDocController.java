package com.easyminning.view.web;

import com.easyminning.tag.TagDoc;
import com.easyminning.tag.TagDocService;
import com.mongodb.QueryBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class TagDocController extends BaseController {
    private TagDocService tagDocService = TagDocService.getInstance();

    /**
     * 传tag名称，例如美国
     * @param tag
     */
    @RequestMapping(value = "/tagDoc", method = RequestMethod.GET)
    public void test(String tag, Integer pageNo, Integer pageSize) {
        if (tag == null || "".equals(tag.trim())) {renderJson(new ArrayList<Map>());return;};
        if (pageSize == null || pageSize < 0) pageSize = DEFAULT_PAGE_SIZE;
        if (pageNo == null || pageNo < 0) pageNo = DEFAULT_PAGE_NO;

        QueryBuilder queryBuilder = QueryBuilder.start("tagItem1").is(tag);
        List<TagDoc> tagDocList ;//= tagDocService.select(queryBuilder,1,size,TagDoc.class);
        List<Map> result = new ArrayList<Map>();
        Map<String,String> map1 = new HashMap<String,String>();
        map1.put("title", "标题1");
        map1.put("abstract", "摘要1");
        map1.put("body", "正文1");
        map1.put("weight", "10");
        result.add(map1);

        Map<String,String> map2 = new HashMap<String, String>();
        map2.put("abstract", "摘要1");
        map2.put("body", "正文1");
        map2.put("weight", "10");
        renderJson(result);
    }

}