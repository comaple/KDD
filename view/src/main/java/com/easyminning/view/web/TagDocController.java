package com.easyminning.view.web;

import com.easyminning.tag.TagDoc;
import com.easyminning.tag.TagDocService;
import com.mongodb.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Scope("prototype")
public class TagDocController extends BaseController {

    @Autowired
    private TagDocService tagDocService;

    /**
     * 传tag名称，例如美国
     * @param tag
     */
    @RequestMapping(value = "/tagdoc", method = RequestMethod.GET)
    public void test(String tag, Integer size) {
        if (tag == null || "".equals(tag.trim())) {renderJson(new ArrayList<Map>());return;};
        if (size == null || size < 0) size = 20;

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