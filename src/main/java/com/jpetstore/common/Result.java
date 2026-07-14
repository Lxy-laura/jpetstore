package com.jpetstore.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
    private T data;
    private long timestamp = System.currentTimeMillis();

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success() { return new Result<>(200, "操作成功", null); }
    public static <T> Result<T> success(T data) { return new Result<>(200, "操作成功", data); }
    public static <T> Result<T> success(String message, T data) { return new Result<>(200, message, data); }

    // 唯一改动：500 → i，让传入的 code 生效
    public static <T> Result<T> error(int i, String message) { return new Result<>(i, message, null); }

    public static <T> Result<T> badRequest(String message) { return new Result<>(400, message, null); }
    public static <T> Result<T> unauthorized(String message) { return new Result<>(401, message, null); }
    public static <T> Result<T> forbidden(String message) { return new Result<>(403, message, null); }
    public static <T> Result<T> notFound(String message) { return new Result<>(404, message, null); }
}