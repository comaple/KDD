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

    private TagTagService tagTagService = TagTagService.getInstance();

    /**
     * 传tag名称
     * @param tagItem
     */
    @RequestMapping(value = "/tagtag", method = RequestMethod.GET)
    public void test(String tagItem,Integer pageNo, Integer pageSize) {
        if (tagItem == null || "".equals(tagItem.trim())) {renderJson(new ArrayList<Map>());return;};
        if (pageNo == null || pageNo < 0) pageNo = DEFAULT_PAGE_NO;
        if (pageSize == null || pageSize < 0) pageSize = DEFAULT_PAGE_SIZE;
        List<TagTag> tagTagList = tagTagService.findTagByTag(tagItem,pageNo,pageSize);

        List<Map> result = new ArrayList<Map>();
        for (TagTag tagTag : tagTagList) {
            Map<String,String> map = new HashMap<String,String>();
            map.put("tagItem", tagTag.getTagItem());
            map.put("tagItem1", tagTag.getTagItem1());
            map.put("weight", tagTag.getWeight().toString());
            result.add(map);
        }
        renderJson(result);
    }

}