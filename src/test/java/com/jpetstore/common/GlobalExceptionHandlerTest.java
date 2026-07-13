package com.jpetstore.common;

import com.jpetstore.controller.AccountController;
import com.jpetstore.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @Test
    void testHandleRuntimeException() throws Exception {
        when(accountService.getAccountByUsername("runtime"))
                .thenThrow(new RuntimeException("运行时异常"));

        mockMvc.perform(get("/api/account/runtime"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("运行时错误：运行时异常"));
    }

    @Test
    void testHandleIllegalArgumentException() throws Exception {
        when(accountService.getAccountByUsername("illegal"))
                .thenThrow(new IllegalArgumentException("参数错误"));

        mockMvc.perform(get("/api/account/illegal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("参数错误：参数错误"));
    }

    @Test
    void testHandleGenericException() throws Exception {
        when(accountService.getAccountByUsername("general"))
                .thenThrow(new Exception("系统异常") {
                });

        mockMvc.perform(get("/api/account/general"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("系统错误：系统异常"));
    }
}