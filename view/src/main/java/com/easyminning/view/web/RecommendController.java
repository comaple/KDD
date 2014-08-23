package com.easyminning.view.web;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-8-23
 * Time: 下午6:54
 * To change this template use File | Settings | File Templates.
 */

@Controller
@Scope("prototype")
public class RecommendController extends BaseController {

    @RequestMapping(value = "/recommend", method = RequestMethod.GET)
    public void test() {
        renderJson("{id:1,name:2}");
    }


}
