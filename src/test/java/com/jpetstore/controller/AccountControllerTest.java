package com.jpetstore.controller;

import com.jpetstore.domain.Account;
import com.jpetstore.domain.Profile;
import com.jpetstore.domain.SignOn;
import com.jpetstore.mapper.AccountMapper;
import com.jpetstore.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 账户控制器集成测试
 */
@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private AccountMapper accountMapper;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setUserid("testuser");
        testAccount.setEmail("test@example.com");
        testAccount.setFirstname("Test");
        testAccount.setLastname("User");
        testAccount.setAddr1("123 Test St");
        testAccount.setCity("Test City");
        testAccount.setState("TS");
        testAccount.setZip("12345");
        testAccount.setCountry("USA");
        testAccount.setPhone("555-1234");

        SignOn signOn = new SignOn();
        signOn.setUsername("testuser");
        signOn.setPassword("password");
        testAccount.setSignOn(signOn);

        Profile profile = new Profile();
        profile.setUserid("testuser");
        profile.setLangpref("english");
        profile.setFavcategory("DOGS");
        testAccount.setProfile(profile);
    }

    @Test
    void testLoginSuccess() throws Exception {
        when(accountService.login("testuser", "password")).thenReturn(testAccount);

        mockMvc.perform(post("/api/account/login")
                        .param("username", "testuser")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data.userid").value("testuser"));
    }

    @Test
    void testLoginFailure() throws Exception {
        when(accountService.login("testuser", "wrongpassword")).thenReturn(null);

        mockMvc.perform(post("/api/account/login")
                        .param("username", "testuser")
                        .param("password", "wrongpassword"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));
    }

    @Test
    void testRegisterSuccess() throws Exception {
        when(accountService.register(any(Account.class))).thenReturn(true);

        String accountJson = "{" +
                "\"userid\":\"testuser\"," +
                "\"email\":\"test@example.com\"," +
                "\"firstname\":\"Test\"," +
                "\"lastname\":\"User\"," +
                "\"addr1\":\"123 Test St\"," +
                "\"city\":\"Test City\"," +
                "\"state\":\"TS\"," +
                "\"zip\":\"12345\"," +
                "\"country\":\"USA\"," +
                "\"phone\":\"555-1234\"," +
                "\"signOn\":{\"username\":\"testuser\",\"password\":\"password\"}," +
                "\"profile\":{\"userid\":\"testuser\",\"langpref\":\"english\",\"favcategory\":\"DOGS\"}" +
                "}";

        mockMvc.perform(post("/api/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(accountJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"));
    }

    @Test
    void testRegisterFailure() throws Exception {
        when(accountService.register(any(Account.class))).thenReturn(false);

        String accountJson = "{" +
                "\"userid\":\"testuser\"," +
                "\"email\":\"test@example.com\"," +
                "\"firstname\":\"Test\"," +
                "\"lastname\":\"User\"," +
                "\"addr1\":\"123 Test St\"," +
                "\"city\":\"Test City\"," +
                "\"state\":\"TS\"," +
                "\"zip\":\"12345\"," +
                "\"country\":\"USA\"," +
                "\"phone\":\"555-1234\"" +
                "}";

        mockMvc.perform(post("/api/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(accountJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("注册失败"));
    }

    @Test
    void testGetAccountByUsername() throws Exception {
        when(accountService.getAccountByUsername("testuser")).thenReturn(testAccount);

        mockMvc.perform(get("/api/account/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userid").value("testuser"));
    }

    @Test
    void testGetAccountByUsernameNotFound() throws Exception {
        when(accountService.getAccountByUsername("notexist")).thenReturn(null);

        mockMvc.perform(get("/api/account/notexist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }

    @Test
    void testUpdateAccount() throws Exception {
        when(accountService.updateAccount(any(Account.class))).thenReturn(true);
        when(accountService.getAccountByUsername("testuser")).thenReturn(testAccount);

        String json = "{\"email\":\"updated@example.com\",\"firstname\":\"Updated\",\"lastname\":\"User\"," +
                "\"addr1\":\"456 Updated St\",\"city\":\"Updated City\",\"state\":\"US\"," +
                "\"zip\":\"54321\",\"country\":\"USA\",\"phone\":\"555-5678\"}";

        mockMvc.perform(put("/api/account/testuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"));
    }

    @Test
    void testDeleteAccount() throws Exception {
        when(accountService.deleteAccount("testuser")).thenReturn(true);

        mockMvc.perform(delete("/api/account/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    @Test
    void testLogout() throws Exception {
        mockMvc.perform(post("/api/account/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登出成功"));
    }
}