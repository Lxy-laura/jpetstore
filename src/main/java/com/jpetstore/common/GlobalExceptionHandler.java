package com.jpetstore.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 处理 @Valid 校验失败 → 返回 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Result<Object> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return Result.badRequest("参数校验失败: " + errors.toString());
    }

    // 处理缺少必需请求参数 → 返回 400
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Result<Object> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return Result.badRequest("缺少必需参数: " + e.getParameterName());
    }

    // 处理参数错误 → 返回 400
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public Result<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        return Result.badRequest("参数错误：" + e.getMessage());
    }

    // 处理运行时异常 → 返回 200 + code:503
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public Result<Object> handleRuntimeException(RuntimeException e) {
        e.printStackTrace();
        return Result.error(503, "运行时错误：" + e.getMessage());
    }

    // 处理其他所有异常 → 返回 200 + code:503
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result<Object> handleException(Exception e) {
        e.printStackTrace();
        return Result.error(503, "系统错误：" + e.getMessage());
    }
}