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
     * @param stepItem
     */
    @RequestMapping(value = "/steptag", method = RequestMethod.GET)
    public void test(String stepItem,Integer pageNo, Integer pageSize) {
        if (stepItem == null || "".equals(stepItem.trim())) {renderJson(new ArrayList<Map>());return;};
        if (pageNo == null || pageNo < 0) pageNo = DEFAULT_PAGE_NO;
        if (pageSize == null || pageSize < 0) pageSize = DEFAULT_PAGE_SIZE;

        List<StepTag> stepTagList = stepTagService.findStepTagByStep(stepItem, pageNo, pageSize);
        List<Map> result = new ArrayList<Map>();
        for (StepTag stepTag : stepTagList) {
            Map<String,String> map = new HashMap<String,String>();
            map.put("stepItem", stepTag.getStepItem() == null ? "" : stepTag.getStepItem());
            map.put("tagItem", stepTag.getTagItem() == null ? "" : stepTag.getTagItem());
            map.put("weight", stepTag.getWeight() == null ? "" : stepTag.getWeight().toString());
            result.add(map);
        }
        renderJson(result);
    }

}