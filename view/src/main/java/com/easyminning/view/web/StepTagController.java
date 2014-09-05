package com.easyminning.view.web;

import com.easyminning.tag.StepTagService;
import com.easyminning.tag.StepTag;
import com.mongodb.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


@Controller
@Scope("prototype")
public class StepTagController extends BaseController {

    @Autowired
    private StepTagService stepTagService;

    /**
     * 传步骤编号，例如1，2，3，4，5，6
     * @param step
     */
    @RequestMapping(value = "/steptag", method = RequestMethod.GET)
    public void test(String step,Integer size) {
        if (step == null || "".equals(step.trim())) {renderJson(new ArrayList<Map>());return;};
        if (size == null || size < 0) size = 20;

        QueryBuilder queryBuilder = QueryBuilder.start("step").is(step);
        List<StepTag> stepTagList;// = stepTagService.select(queryBuilder, 1, size, StepTag.class);
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