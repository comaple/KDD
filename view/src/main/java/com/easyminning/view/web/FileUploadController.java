package com.easyminning.view.web;

import com.easyminning.hdfs.HDFSService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created with IntelliJ IDEA.
 * User: xdx
 * Date: 14-8-30
 * Time: 下午7:51
 * To change this template use File | Settings | File Templates.
 */

@Controller
@Scope("prototype")
public class FileUploadController extends BaseController {

    @RequestMapping(value = "/fileToHdfs", method = RequestMethod.GET)
    public void test(String filepath) {
        HDFSService.put("/Volumes/study/test/1.csv");
    }

}
