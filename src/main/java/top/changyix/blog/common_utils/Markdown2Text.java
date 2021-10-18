package top.changyix.blog.common_utils;

//markdown转纯文本
public class Markdown2Text {
    public static String convert(String markdown){
        return Html2Text.convert(Markdown2Html.convert(markdown));
    }
}
