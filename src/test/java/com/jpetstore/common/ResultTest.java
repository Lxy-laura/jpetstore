package com.jpetstore.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ResultTest {

    @Test
    void testSuccessNoData() {
        Result<String> result = Result.success();
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(result.getData());
        assertNotNull(result.getTimestamp());
    }

    @Test
    void testSuccessWithData() {
        Result<String> result = Result.success("hello");
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals("hello", result.getData());
    }

    @Test
    void testSuccessWithMessageAndData() {
        Result<String> result = Result.success("自定义消息", "data");
        assertEquals(200, result.getCode());
        assertEquals("自定义消息", result.getMessage());
        assertEquals("data", result.getData());
    }

    @Test
    void testError() {
        Result<String> result = Result.error(503, "出错了");
        assertEquals(500, result.getCode());
        assertEquals("出错了", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testErrorWithCode() {
        Result<String> result = Result.error(503, "服务不可用");
        assertEquals(503, result.getCode());
        assertEquals("服务不可用", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testBadRequest() {
        Result<String> result = Result.badRequest("参数错误");
        assertEquals(400, result.getCode());
        assertEquals("参数错误", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testUnauthorized() {
        Result<String> result = Result.unauthorized("未登录");
        assertEquals(401, result.getCode());
        assertEquals("未登录", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testNotFound() {
        Result<String> result = Result.notFound("不存在");
        assertEquals(404, result.getCode());
        assertEquals("不存在", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testConstructor() {
        Result<String> result = new Result<>(200, "test", "data");
        assertEquals(200, result.getCode());
        assertEquals("test", result.getMessage());
        assertEquals("data", result.getData());
        assertNotNull(result.getTimestamp());
    }

    @Test
    void testNoArgsConstructor() {
        Result<String> result = new Result<>();
        result.setCode(201);
        result.setMessage("created");
        result.setData("new data");

        assertEquals(201, result.getCode());
        assertEquals("created", result.getMessage());
        assertEquals("new data", result.getData());
    }
}