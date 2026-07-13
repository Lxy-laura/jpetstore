package com.jpetstore.common;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result<Object> handleException(Exception e) {
        e.printStackTrace();
        return Result.error(503, "系统错误：" + e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Result<Object> handleRuntimeException(RuntimeException e) {
        e.printStackTrace();
        return Result.error(503, "运行时错误：" + e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public Result<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        return Result.badRequest("参数错误：" + e.getMessage());
    }
}