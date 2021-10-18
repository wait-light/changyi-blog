package top.changyix.blog.handler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import top.changyix.blog.common_utils.Result;
import top.changyix.blog.common_utils.ResultCode;
import top.changyix.blog.common_utils.ResultMessage;

import java.util.List;


@ResponseBody
@ControllerAdvice //全局异常处理，全局数据绑定，全局数据预处理
public class BasicExceptionHandler {

    @ExceptionHandler({BasicHandler.class})//处理自定义异常
    public Result customError(BasicHandler handler){
        handler.printStackTrace();
        return Result.getInstance()
                .setCode(handler.getCode())
                .setMessage(handler.getMessage())
                .clearData();
    }

    @ExceptionHandler({AccessDeniedException.class})//权限不足异常
    public Result customError(AccessDeniedException handler){
        handler.printStackTrace();
        return Result.getInstance()
                .setCode(ResultCode.FORBIDDEN)
                .setMessage(ResultMessage.DENY)
                .clearData();
    }

    @ExceptionHandler({Exception.class})//处理所有异常
    public Result customError(Exception handler){
        handler.printStackTrace();
        return Result.getInstance()
                .setCode(ResultCode.ERROR)
                .setMessage(ResultMessage.INTERNAL_ERROR)
                .clearData();
    }
}
