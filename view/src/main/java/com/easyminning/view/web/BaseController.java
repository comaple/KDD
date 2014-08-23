package com.easyminning.view.web;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * User: ybli
 * Date: 13-12-3
 * Description: 基础控制类
 */
public class BaseController {

    //header 常量定义
    private static final String ENCODING_PREFIX = "encoding:";
    private static final String NOCACHE_PREFIX = "no-cache:";
    private static final String ENCODING_DEFAULT = "UTF-8";
    private static final boolean NOCACHE_DEFAULT = true;

    protected String ADD_SUCCESS_PAGE = "common/add_success";
    protected String UPDATE_SUCCESS_PAGE = "common/update_success";
    protected String DELETE_SUCCESS_PAGE = "common/del_success";

    protected String ADD_ERROR_PAGE = "common/add_error";

    protected HttpServletRequest request;
    protected HttpServletResponse response;
    protected HttpSession session;

    public static Logger logger = LoggerFactory.getLogger(BaseController.class);

    public  static DateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    // 响应体
    protected Map<Object, Object> body = new HashMap<Object, Object>();


    @InitBinder
    protected void initBinder(HttpServletRequest request,
                              ServletRequestDataBinder binder) throws Exception {

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        CustomDateEditor dateEditor = new CustomDateEditor(format, true);
        binder.registerCustomEditor(Date.class, dateEditor);

        DateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        CustomDateEditor customDateEditor = new CustomDateEditor(timestampFormat, true);
        binder.registerCustomEditor(java.sql.Timestamp.class,customDateEditor);
    }




    /**
     * ModelAttribute
     * 1) 放置在方法的形参上：表示引用Model中的数据
     * 2) 放置在方法上面：表示请求该类的每个Action前都会首先执行它，
     * 也可以将一些准备数据的操作放置在该方法里面。
     *
     * @param request
     * @param response
     */
    @ModelAttribute
    public void setReqAndRes(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
        this.session = request.getSession();
    }



    // 绕过jsp/freemaker直接输出文本的函数 //

    /**
     * 直接输出内容的简便函数.

     * eg.
     * render("text/plain", "hello", "encoding:GBK");
     * render("text/plain", "hello", "no-cache:false");
     * render("text/plain", "hello", "encoding:GBK", "no-cache:false");
     *
     * @param headers 可变的header数组，目前接受的值为"encoding:"或"no-cache:",默认值分别为UTF-8和true.
     */
    public void render(final String contentType, final String content, final String... headers) {
        try {
            //分析headers参数
            String encoding = ENCODING_DEFAULT;
            boolean noCache = NOCACHE_DEFAULT;
            for (String header : headers) {
                String headerName = StringUtils.substringBefore(header, ":");
                String headerValue = StringUtils.substringAfter(header, ":");

                if (StringUtils.equalsIgnoreCase(headerName, ENCODING_PREFIX)) {
                    encoding = headerValue;
                } else if (StringUtils.equalsIgnoreCase(headerName, NOCACHE_PREFIX)) {
                    noCache = Boolean.parseBoolean(headerValue);
                } else
                    throw new IllegalArgumentException(headerName + "不是一个合法的header类型");
            }

            //设置headers参数
            String fullContentType = contentType + ";charset=" + encoding;
            response.setContentType(fullContentType);
            if (noCache) {
                response.setHeader("Pragma", "No-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setDateHeader("Expires", 0);
            }
            System.out.println("============" + content + "====================");
            response.getWriter().write(content);

        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 直接输出文本.
     *
     * @see #render(String, String, String...)
     */
    public void renderText(final String text, final String... headers) {
        render("text/plain", text, headers);
    }

    /**
     * 直接输出HTML.
     *
     * @see #render(String, String, String...)
     */
    public void renderHtml(final String html, final String... headers) {
        render("text/html", html, headers);
    }

    /**
     * 直接输出XML.
     *
     * @see #render(String, String, String...)
     */
    public void renderXml(final String xml, final String... headers) {
        render("text/xml", xml, headers);
    }

    /**
     * 直接输出JSON.
     *
     * @param string json字符串.
     * @see #render(String, String, String...)
     */
    public void renderJson(final String string, final String... headers) {
        if (string == null) {
            render("application/json", "", headers);
            return;
        }
        if (string.contains("null")) {
            String result = "";
            result = string.replaceAll("\"null\"", "\"\"");
            result = string.replaceAll("null", "\"\"");
            render("application/json", result, headers);
            return;
        }
        render("application/json", string, headers);
    }

    /**
     * 直接输出JSON.
     *
     * @param map Map对象,将被转化为json字符串.
     * @see #render(String, String, String...)
     */
    public void renderJson(final Map map, final String... headers) {
        String jsonString = JSON.toJSONString(map);
        renderJson(jsonString, headers);
    }

    /**
     * 直接输出JSON.
     *
     * @param object Java对象,将被转化为json字符串.
     * @see #render(String, String, String...)
     */
    public void renderJson(final Object object, final String... headers) {

        String jsonString = JSON.toJSONString(object);
        renderJson(jsonString, headers);
    }

    public void addBody(String fieldName, Object fieldValue) {
        body.put(fieldName, fieldValue);
    }


}
