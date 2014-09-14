package com.easyminning.extractor;

import cn.edu.hfut.dmic.webcollector.model.Page;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jerry on 2014/8/30.
 */
public class StatisticsExtractor extends Extractor {
    //是否使用追加模式,使用追加模式后，会将符合过滤条件的所有文本提取出来
    private boolean appendMode;
    private int depth;  //按行分析的深度
    //字符限定数，当分析的文本数量达到限定数则认为进入正文内容
    private int limitCount;
    //确定文章正文头部时，向上查找，连续的空行到达headEmptyLines，则停止查找
    private int headEmptyLines;
    //用于确定文章结束的字符数
    private int endLimitCharCount;
    private boolean isEnglish;
    private final static String [][] filters = {
            {"(?is)<!DOCTYPE.*?>", ""},
            {"(?is)<script.*?>.*?</script>", ""},
            {"(?is)<style.*?>.*?</style>", ""},
            {"(?is)<!--.*?-->", ""},
            {"&.{2,5};|&#.{2,5};", ""},
            {"(?is)</a>", "</a>\n"}// 针对链接密集型的网站的处理，主要是门户类的网站，降低链接干扰
    };

    public StatisticsExtractor(){
        appendMode = false;
        depth = 3;
        limitCount = 150;
        headEmptyLines = 2;
        endLimitCharCount = 20;
        isEnglish = false;
    }

    @Override
    public Article extractArticle(Page page) {
        if(null == page){
            return null;
        }
        String html = page.html;
        //处理压缩的html
        //获取html，body标签内容
        String body = "";
        String bodyFilter = "(?is)<body.*?</body>";
        Pattern p = Pattern.compile("(?is)<body.*?</body>");
        Matcher m = p.matcher(html);
        if(m.find()){
            body = m.group();
        }else{
            body = html;
        }
        //过滤样式，脚本等不相干标签
        for(String [] filter : filters){
            body = body.replaceAll(filter[0],filter[1]);
        }

        //标签规整化处理 处理形如以下的标签：
        //  <a
        //   href='http://www.baidu.com'
        //   class='test' >
        // 处理后为
        //  <a href='http://www.baidu.com' class='test'>
        p = Pattern.compile("<\\w+(\\s*[^<>]*\\s*\\n\\s*[^<>]*)+>");
        m = p.matcher(body);
        while (m.find()){
            body = body.replace(m.group(),m.group().replaceAll("\\s*\\n\\s*","  "));
        }

        String [] contents = getMainContent(body);
        Article article = new Article();
        article.context = contents[0].trim();
        article.contextWithTag = contents[1];
        article.url = page.url;
        article.title = getTitle(html).trim();
        article.publishDate = getPublishDate(body).trim();
        article.author = "";
        return article;
    }

    //获取正文
    public String[] getMainContent(String bodyText){
        String [] orgLines = bodyText.split("\n");
        String [] lines = new String[orgLines.length];
        //字数最多的一行的长度
        int lineMaxCount = 0;
        //去除每行的空白字符，剔除标签
        for(int i = 0; i < orgLines.length; i++){
            String lineInfo = orgLines[i];
            lineInfo = lineInfo.replaceAll("(?is)</p>|<br.*?/>","[crlf]");
            lines[i] = lineInfo.replaceAll("(?is)<.*?>","").trim();
            int len = lines[i].length();
            if(isEnglish) {
                len = lines[i].split(" ").length;
            }
            if(len > lineMaxCount){
                lineMaxCount = len;
            }
        }

        StringBuffer sb = new StringBuffer();
        StringBuffer orgSb = new StringBuffer();

        int preTextLen = 0; //记录上一次统计的字符数量
        int startPos = -1;  //记录文章正文的起始位置

        for(int i = 0; i < lines.length - depth; i++){
            int len = 0;
            for(int j = 0; j < depth; j++){
                if(!isEnglish) {
                    len += lines[i + j].length();
                }else{
                    len += lines[i + j].split(" ").length;//如果是英文则应该是计算单词数
                }
            }

            if(startPos == -1){  //还没有找到文章起始位置，需要判断起始位置
                //如果上次查找的文本数量超过了限定字数，且当前行数字符数不为0，则认为是开始位置
                if((preTextLen >= lineMaxCount || preTextLen >= limitCount) && len > 0){  //limitCount
                    // 查找文章起始位置, 如果向上查找，发现2行连续的空行则认为是头部
                    int emptyCount = 0;
                    for(int j = i - 1; j > 0; j--){
                        if(lines[j].equals("")){
                            emptyCount++;
                        }else{
                            emptyCount = 0;
                        }
                        if(emptyCount == headEmptyLines){
                            startPos = j + headEmptyLines;
                            break;
                        }
                    }
                    // 如果没有定位到文章头，则以当前查找位置作为文章头
                    if(startPos == -1){
                        startPos = i;
                    }
                    // 填充发现的文章起始部分
                    for (int j = startPos; j <= i; j++)
                    {
                        sb.append(lines[j]);
                        orgSb.append(orgLines[j]);
                    }
                }
            }else{
                //结束条件还得仔细斟酌
                if (len <= endLimitCharCount && preTextLen < endLimitCharCount)
                {
                    if (!appendMode)
                    {
                        break;
                    }
                    startPos = -1;
                }
                sb.append(lines[i]);
                orgSb.append(orgLines[i]);
            }
            preTextLen = len;
        }
        String result = sb.toString();
        // 处理回车符，更好的将文本格式化输出
        //String content = result.replaceAll("\\[crlf\\]", "\n");
        String content = result.replaceAll("\\s*\\[crlf\\]\\s*", "").replaceAll("\\s*\n\\s*"," ");
        // 输出带标签文本
        String contentWithTags = orgSb.toString();
        return new String[]{content,contentWithTags};
    }

    public String getTitle(String html)
    {
        String titleFilter = "<title>[\\s\\S]*?</title>";
        String h1Filter = "<h1.*?>.*?</h1>";
        String clearFilter = "<.*?>";

        String title = "";
        Pattern p = Pattern.compile(titleFilter,Pattern.CASE_INSENSITIVE);
        Matcher match = p.matcher(html);
        if (match.find())
        {
            title = match.group().replaceAll(clearFilter, "");
        }

        // 正文的标题一般在h1中，比title中的标题更干净
        p = Pattern.compile(h1Filter,Pattern.CASE_INSENSITIVE);
        match = p.matcher(html);
        int count = 0;
        while (match.find())
        {
            count++;
            String h1 = match.group().replaceAll(clearFilter, "").trim();
            if (!h1.equals("") && title.startsWith(h1))
            {
                title = h1;
                break;
            }
            if(count > 5){
                break;
            }
        }
        return title;
    }

    public String getPublishDate(String html){
        String date = "";
        html = html.replaceAll("(?is)<.*?>", "");
        Pattern p = Pattern.compile(
                "((\\d{4}|\\d{2})(\\\\|\\-|\\/)\\d{1,2}\\3\\d{1,2})(\\s?\\d{2}:\\d{2})?|(\\d{2,4}年\\d{1,2}月\\d{1,2}日)(\\s?\\d{2}:\\d{2})?",
                Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(html);
        if(m.find()){
            date = m.group();
        }
        return date;
    }


    public void setAppendMode(boolean appendMode){
        this.appendMode = appendMode;
    }
    public void setDepth(int depth){
        this.depth = depth;
    }
    public void setLimitCount(int limitCount){
        this.limitCount = limitCount;
    }
    public void setHeadEmptyLines(int headEmptyLines){
        this.headEmptyLines = headEmptyLines;
    }
    public void setEndLimitCharCount(int endLimitCharCount){
        this.endLimitCharCount = endLimitCharCount;
    }
    public void setEnglish(boolean isEnglish){
        this.isEnglish = isEnglish;
    }
}
