package com.easyminning.view.web;

import com.easyminning.tag.ResultDocument;
import com.easyminning.tag.ResultDocumentService;
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
    private ResultDocumentService resultDocumentService = ResultDocumentService.getInstance();

    /**
     * 传tag名称，例如美国
     * @param tagItem
     */
    @RequestMapping(value = "/tagdoc", method = RequestMethod.GET)
    public void test(String tagItem, Integer pageNo, Integer pageSize) {
        if (tagItem == null || "".equals(tagItem.trim())) {renderJson(new ArrayList<Map>());return;};
        if (pageSize == null || pageSize < 0) pageSize = DEFAULT_PAGE_SIZE;
        if (pageNo == null || pageNo < 0) pageNo = DEFAULT_PAGE_NO;

        List<TagDoc> tagDocList = tagDocService.findDocByTag(tagItem,pageNo,pageSize);
        List<Map> result = new ArrayList<Map>();

        for (TagDoc tagDoc : tagDocList) {
            ResultDocument resultDocument = resultDocumentService.getDocumentByDocId(tagDoc.getDocItem());
            Map<String, String> map = new HashMap<String, String>();
            map.put("tagItem", tagDoc.getTagItem() == null ? "" : tagDoc.getTagItem());
            map.put("docItem", tagDoc.getDocItem() == null ? "" : tagDoc.getDocItem());
            map.put("weight", tagDoc.getWeight() == null ? "" : tagDoc.getWeight().toString());
            if (resultDocument == null) continue;
            map.put("docContent",resultDocument.getDocContent() == null ? "" : resultDocument.getDocContent());
            map.put("title",resultDocument.getTitle() == null ? "" : resultDocument.getTitle());
            map.put("url", resultDocument.getUrl() == null ? "" :resultDocument.getUrl());
            map.put("keyWord",resultDocument.getKeyWord() == null ? "" : resultDocument.getKeyWord());
            map.put("author", resultDocument.getUrl() == null ? "" : resultDocument.getAuthor());
            result.add(map);
        }
        renderJson(result);
    }

}