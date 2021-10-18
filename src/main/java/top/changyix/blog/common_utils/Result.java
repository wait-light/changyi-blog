package top.changyix.blog.common_utils;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


import java.util.HashMap;
import java.util.Map;


public class Result {
    /**
     * 数据返回格式
     * {
     * 	"sucess":布尔值, 	//表示是否响应成功
     * 	"code":数字,     	//响应码
     * 	"message":字符串,	//返回信息
     * 	"data":map, 	//返回的数据
     * }
     *
     * 创建成员变量
     */
    private boolean success;
    private int code;
    private String message;
    private Map data = new HashMap<Object,Object>();
    private Result(){}
    //单例
    @JsonIgnore

    public boolean isSuccess() {
        return success;
    }
    //返回Result，便于链式调用。
    public Result setSuccess(boolean success) {
        this.success = success;
        return this;
    }
    public int getCode() {
        return code;
    }

    public Result setCode(int code) {
        this.code = code;
        return this;
    }
    public String getMessage() {
        return message;
    }

    public Result setMessage(String message) {
        this.message = message;
        return this;
    }

    public Map getData() {
        return data;
    }

    public Result setData(Map data) {
        this.data = data;
        return this;
    }
    @JsonIgnore
    public static Result success(Map data){
        return success()
                .setData(data);
    };
    @JsonIgnore
    public static Result success(Object key,Object value){
        return success()
                .append(key,value);
    }
    public static Result success(){
        return new Result()
                .setSuccess(true)
                .setMessage(ResultMessage.SUCCESS)
                .setCode(ResultCode.SUCCESS);
    }
    public static Result success(String message){
        return success()
                .setMessage(message);
    }
    public static Result error(Map data){
        return error()
                .setData(data);
    }
    public static Result error(Object key,Object value){
        return error()
                .append(key,value);
    }
    public static Result error(String message){
        return error()
                .setMessage(message);
    }
    public static Result error(){
        return new Result()
                .setMessage(ResultMessage.ERROR)
                .setCode(ResultCode.ERROR)
                .setSuccess(false);
    }
    public static Result deny(){
        return new Result()
                .setMessage(ResultMessage.DENY)
                .setSuccess(false)
                .setCode(ResultCode.FORBIDDEN);
    }
    public static Result getInstance(){
        return new Result();
    }
    public static Result deny(Object key,Object value){
        return deny()
                .append(key,value);

    }
    public static Result deny(String message){
        return deny()
                .setMessage(message);
    }
    public static Result common(boolean success,int code,String message,Map data){
        return new Result()
                .setSuccess(success)
                .setCode(code)
                .setMessage(message)
                .setData(data);
    }
    public Result clearData(){
        data.clear();
        return this;
    }
    public Result append(Object key,Object value){
        data.put(key,value);
        return this;
    }

    public static  Result logicError(String message){
        return logicError()
                .setMessage(message);
    }

    public static Result logicError(){
        return new Result()
                .setMessage(ResultMessage.LOGIC_ERROR)
                .setSuccess(false)
                .setCode(ResultCode.LOGIC_ERROR);
    }
}
