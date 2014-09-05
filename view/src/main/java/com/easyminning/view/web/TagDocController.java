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
     * @param tagItem
     */
    @RequestMapping(value = "/tagDoc", method = RequestMethod.GET)
    public void test(String tagItem, Integer pageNo, Integer pageSize) {
        if (tagItem == null || "".equals(tagItem.trim())) {renderJson(new ArrayList<Map>());return;};
        if (pageSize == null || pageSize < 0) pageSize = DEFAULT_PAGE_SIZE;
        if (pageNo == null || pageNo < 0) pageNo = DEFAULT_PAGE_NO;

        List<TagDoc> tagDocList = tagDocService.findDocByTag(tagItem,pageNo,pageSize);
        List<Map> result = new ArrayList<Map>();

        for (TagDoc tagDoc : tagDocList) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("tagItem", tagDoc.getTagItem());
            map.put("docItem", tagDoc.getDocItem());
            map.put("weight", tagDoc.getWeight().toString());
        }
        renderJson(result);
    }

}