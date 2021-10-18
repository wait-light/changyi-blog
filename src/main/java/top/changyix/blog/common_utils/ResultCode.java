package top.changyix.blog.common_utils;

//结果状态码
public interface ResultCode {
    //成功
    public static final int SUCCESS = 200;
    //服务器错误
    public static final int ERROR = 500;
    //拒绝访问
    public static final int FORBIDDEN = 403;
    //未找到
    public static final  int NOT_FOUND = 404;
    //逻辑错误，就是不允许此操作，例如有同名的标签，则拒绝
    public static final  int LOGIC_ERROR = 600;
}
