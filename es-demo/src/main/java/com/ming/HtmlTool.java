package com.ming;

import com.ming.bean.HtmlBean;
import com.ming.service.IndexService;
import net.htmlparser.jericho.CharacterReference;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import java.io.File;

public class HtmlTool {

    public static final String filePath = "C:/Tools/data/news.cctv.com/2017/11/03/ARTIYyjmczxyRM2gH3T4ztgD171103.shtml";

    public static void main(String[] args) {

        try {
            System.out.println(parseHtml(filePath).getContent());
        }catch (Throwable e){
            e.printStackTrace();
        }
    }

    public static HtmlBean parseHtml(String path) throws Throwable{
        HtmlBean bean = new HtmlBean();
        Source source = new Source(new File(path));
        source.fullSequentialParse();
        Element titleElement =source.getFirstElement(HTMLElementName.TITLE);
        if(null == titleElement){
            return null;
        }else {
            String title = CharacterReference.decodeCollapseWhiteSpace(titleElement.getContent());
            bean.setTitle(title);
        }
        String content = source.getTextExtractor().setIncludeAttributes(true).toString();
        String url = path.substring(IndexService.DATA_DIR.length());
        bean.setContent(content);
        bean.setUrl(url);
        return bean;
    }
}
