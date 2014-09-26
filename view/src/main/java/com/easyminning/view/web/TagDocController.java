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
    public void test(String tagItem, String type,Integer pageNo, Integer pageSize) {
        if (tagItem == null || "".equals(tagItem.trim())) {renderJson(new ArrayList<Map>());return;};
        if (pageSize == null || pageSize < 0) pageSize = DEFAULT_PAGE_SIZE;
        if (pageNo == null || pageNo < 0) pageNo = DEFAULT_PAGE_NO;

        String[] tagItemArray = tagItem.split(",");
       // List<TagDoc> tagDocList = tagDocService.findDocByTag(tagItem,pageNo,pageSize);
        List<TagDoc> tagDocList = tagDocService.findDocByTag(tagItemArray,pageNo,pageSize);
        List<Map> result = new ArrayList<Map>();

        for (TagDoc tagDoc : tagDocList) {
            ResultDocument resultDocument = resultDocumentService.getDocumentByDocId(tagDoc.getDocItem());
            Map<String, String> map = new HashMap<String, String>();
            map.put("tagItem", tagDoc.getTagItem() == null ? "" : tagDoc.getTagItem());
            map.put("docItem", tagDoc.getDocItem() == null ? "" : tagDoc.getDocItem());
            map.put("weight", tagDoc.getWeight() == null ? "" : tagDoc.getWeight().toString());
            if (resultDocument == null) continue;
            map.put("docContent",resultDocument.getSourceContent() == null ? "" : resultDocument.getSourceContent());
            map.put("title",resultDocument.getTitle() == null ? "" : resultDocument.getTitle());
            map.put("url", resultDocument.getUrl() == null ? "" :resultDocument.getUrl());
            map.put("keyWord",resultDocument.getKeyWord() == null ? "" : resultDocument.getKeyWord());
            map.put("author", resultDocument.getUrl() == null ? "" : resultDocument.getAuthor());
            result.add(map);
        }
        renderJson(result);
    }


    // 获取热门文章
    @RequestMapping(value = "/hotdoc", method = RequestMethod.GET)
    public void test2(Integer pageNo,Integer pageSize) {
        if (pageNo == null || pageNo < 0) pageNo = DEFAULT_PAGE_NO;
        if (pageSize == null || pageSize < 0) pageSize = DEFAULT_PAGE_SIZE;
        List<Map> resultDocumentMapList = new ArrayList<Map>();
        List<ResultDocument> resultDocumentList = ResultDocumentService.getInstance().getHotDocList(pageNo, pageSize);
        for (ResultDocument resultDocument : resultDocumentList) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("docContent",resultDocument.getSourceContent() == null ? "" : resultDocument.getSourceContent());
            map.put("title",resultDocument.getTitle() == null ? "" : resultDocument.getTitle());
            map.put("url", resultDocument.getUrl() == null ? "" :resultDocument.getUrl());
            map.put("keyWord",resultDocument.getKeyWord() == null ? "" : resultDocument.getKeyWord());
            map.put("author", resultDocument.getUrl() == null ? "" : resultDocument.getAuthor());
            resultDocumentMapList.add(map);
        }
        renderJson(resultDocumentMapList);
    }

    @RequestMapping(value = "/doctag", method = RequestMethod.POST)
    public void test3(String content) throws Exception{
        List<TagDoc> tagDocList = tagDocService.parseWords(content);
        List<Map<String,String>> list = new ArrayList<Map<String,String>>();

        for (TagDoc tagDoc : tagDocList) {
            Map<String,String> map = new HashMap<String,String>();
            map.put("tagItem",tagDoc.getTagItem());
            map.put("weight", String.valueOf(tagDoc.getWeight()));
            list.add(map);
        }
        renderJson(list);
    }

    public static void main(String[] args) throws Exception {
        TagDocController tagDocController = new TagDocController();
        String str = "　在日本留学的同学们都可参加日本的国民健康保险。在此希望同学们能够加入（留学生加入的费用很低）\n" +
                "\n" +
                "　　国民健康保险的加入，原则是以到达日本之日起计算的。国民健康保险的加入是以到达日本之日计算为原则的。所以说即使晚去办加入申请，也有可能被要求支付来日后至今的保险费。\n" +
                "国保所支付医疗费的范围\n" +
                "\n" +
                "　　国民健康保险的加入者在因生病或受伤到医院或诊疗所时，只要支付医疗费总额的30％就可以了。余下70%由保险负担。\n" +
                "\n" +
                "　　只是在健康保险中，也有所需费用的全额必须由自己负担的时候。如住进医院的单人房等时的「差额床费」、使用在健康保险中没有被承认的高额的特殊治疗药时及牙科需要装金冠等的特殊治疗，分娩、人工堕胎也是全额由自己负担。\n" +
                "\n" +
                "　　退还医疗费（支付疗养费用）\n" +
                "\n" +
                "　　以下的情况，就算支付了全额的医疗费，如果申请而经审查通过的话，保险诊疗费的70％可以退回来。\n" +
                "\n" +
                "　　1.     因突然事故而受伤，在没有国民健康保险资格的医院接受了治疗，或者急病以及旅行中生病．受伤、忘记拿保险证接受治疗时。\n" +
                "\n" +
                "　　2.     因病情严重的理由，医生认为需要护理时的护理人员费用。\n" +
                "\n" +
                "　　3.     以医生指示接受按摩．针灸等的治疗，骨折及扭伤时接受接骨院的治疗。\n" +
                "\n" +
                "　　4.     做整形矫正等的费用。\n" +
                "\n" +
                "　　5.     不能接受正常渠道所共给的营养而输血的血费。\n" +
                "\n" +
                "　　6.     重病人的入院．转院等的移送费。\n" +
                "\n" +
                "　　高额医疗费的补助\n" +
                "\n" +
                "　　同一个人在同一家医院支付医疗费的月负担额，如果超过一定金额时（比如在东京都是63,600日元，１９９９年度／没有支付住民税的家庭是35,400元圆），申请的话，可以享受叫作高额医疗费补助的制度，即超过上述金额的部分可以退回来。不过，门诊治疗时的诊疗费，差额床费在对象之外。关于申请方法，请向地方政府的国民健康保险课询问。\n" +
                "\n" +
                "　　分娩．死亡时\n" +
                "\n" +
                "　　加入者分娩时，作为分娩补助金一次性支给42万日元。如果怀孕４个月以上（85天）的话，就算流产．死产，根据医生证明也支给同样的金额。在加入者死亡时，举行丧礼的人，可以获得５～７万日元丧葬费\n" +
                "\n" +
                "　　国民健康保险（国保）的加入方法\n" +
                "\n" +
                "　　向办理外国人登录的市、区、乡、村政府的国民健康保险课提出加入的申请。办理时需要外国人登录证明书。持有在留期间1年的「留学」或「就学」在留资格的人马上就可以加入，但拿6个月的「就学」在留资格的人，根据办理地的政府的不同可能会要求提出记载有1年以上在学预定期间的在学证明书。\n" +
                "\n" +
                "　　如果有同居的家属时，家属也要一起加入。要好好的确认在健康保险证上有没有记上家属的名字。\n" +
                "\n" +
                "　　住址变更时\n" +
                "\n" +
                "　　搬家后，要到新住址的政府去领新的保险证\n" +
                "\n" +
                "　　请在新住址的政府窗口提出以前的国民健康保险证以换领新的保险证。没有这个手续的话，则不能享受国民健康保险待遇。\n" +
                "\n" +
                " ";
       tagDocController.test3(str);
    }







}