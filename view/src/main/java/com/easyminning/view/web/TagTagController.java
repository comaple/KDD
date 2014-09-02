package com.easyminning.view.web;

import com.easyminning.tag.TagTag;
import com.easyminning.tag.TagTagService;
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
public class TagTagController extends BaseController {

    @Autowired
    private TagTagService tagTagService;

    /**
     * 传tag名称
     * @param tag
     */
    @RequestMapping(value = "/tagtag", method = RequestMethod.GET)
    public void test(String tag,Integer size) {
        if (tag == null || "".equals(tag.trim())) {renderJson(new ArrayList<Map>());return;};
        if (size == null || size < 0) size = 20;

        QueryBuilder queryBuilder = QueryBuilder.start("tagItem1").is(tag);
        List<TagTag> tagTagList = tagTagService.select(queryBuilder,1,size,TagTag.class);
        List<Map> result = new ArrayList<Map>();
        Map<String,String> map1 = new HashMap<String,String>();
        map1.put("tag", "标签1");
        map1.put("weight", "10");
        result.add(map1);

        Map<String,String> map2 = new HashMap<String, String>();
        map2.put("tag", "标签2");
        map2.put("weight", "9");
        result.add(map2);
        renderJson(result);
    }

}