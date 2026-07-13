package com.jpetstore.controller;

import com.jpetstore.domain.Account;
import com.jpetstore.domain.Profile;
import com.jpetstore.domain.SignOn;
import com.jpetstore.mapper.AccountMapper;
import com.jpetstore.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.CsvSource;
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

    @MockitoBean
    private com.jpetstore.mapper.CategoryMapper categoryMapper;

    @MockitoBean
    private com.jpetstore.mapper.ProductMapper productMapper;

    @MockitoBean
    private com.jpetstore.mapper.ItemMapper itemMapper;

    @MockitoBean
    private com.jpetstore.mapper.OrderMapper orderMapper;

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

    // ==================== 以下为补充的测试用例 ====================

    // --- 技巧1: 参数化测试 - 用一组数据跑多个测试用例 ---

    /**
     * 参数化测试：用不同的无效用户名测试登录失败
     * 相当于一次写了4个测试用例
     */
    @ParameterizedTest
    @ValueSource(strings = {"nonexistent", "admin' OR '1'='1", "", "   "})
    void testLoginWithInvalidUsernames(String username) throws Exception {
        when(accountService.login(username, "password")).thenReturn(null);

        mockMvc.perform(post("/api/account/login")
                        .param("username", username)
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));
    }

    /**
     * 参数化测试：用不同的无效密码测试登录失败
     */
    @ParameterizedTest
    @ValueSource(strings = {"wrongpass", "123456", "password!", ""})
    void testLoginWithInvalidPasswords(String password) throws Exception {
        when(accountService.login("testuser", password)).thenReturn(null);

        mockMvc.perform(post("/api/account/login")
                        .param("username", "testuser")
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    // --- 技巧2: 边界值测试 - 测试注册时的字段校验 ---

    /**
     * 注册时 email 为空 -> 应返回校验错误
     */
    @Test
    void testRegisterWithBlankEmail() throws Exception {
        String json = "{" +
                "\"userid\":\"testuser2\"," +
                "\"email\":\"\"," +
                "\"firstname\":\"Test\"," +
                "\"lastname\":\"User\"," +
                "\"addr1\":\"123 Test St\"," +
                "\"city\":\"Test City\"," +
                "\"state\":\"TS\"," +
                "\"zip\":\"12345\"," +
                "\"country\":\"USA\"," +
                "\"phone\":\"555-1234\"}";

        mockMvc.perform(post("/api/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    /**
     * 注册时 email 格式不正确 -> 应返回校验错误
     */
    @ParameterizedTest
    @ValueSource(strings = {"notanemail", "test@", "@test.com", "test@@test.com", "test test@test.com"})
    void testRegisterWithInvalidEmailFormat(String email) throws Exception {
        String json = "{" +
                "\"userid\":\"testuser3\"," +
                "\"email\":\"" + email + "\"," +
                "\"firstname\":\"Test\"," +
                "\"lastname\":\"User\"," +
                "\"addr1\":\"123 Test St\"," +
                "\"city\":\"Test City\"," +
                "\"state\":\"TS\"," +
                "\"zip\":\"12345\"," +
                "\"country\":\"USA\"," +
                "\"phone\":\"555-1234\"}";

        mockMvc.perform(post("/api/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    /**
     * 注册时缺少必填字段 -> 应返回校验错误
     */
    @ParameterizedTest
    @CsvSource({
            // 缺少 firstname
            "'', Test, User, 123 St, City, ST, 12345, USA, 555-1234",
            // 缺少 lastname
            "TestEmail@test.com, Test, '', 123 St, City, ST, 12345, USA, 555-1234",
            // 缺少 addr1
            "TestEmail@test.com, Test, User, '', City, ST, 12345, USA, 555-1234",
            // 缺少 city
            "TestEmail@test.com, Test, User, 123 St, '', ST, 12345, USA, 555-1234",
    })
    void testRegisterMissingRequiredFields(String email, String firstname, String lastname,
                                           String addr1, String city, String state,
                                           String zip, String country, String phone) throws Exception {
        String json = String.format(
                "{\"userid\":\"testuser4\",\"email\":\"%s\",\"firstname\":\"%s\",\"lastname\":\"%s\"," +
                        "\"addr1\":\"%s\",\"city\":\"%s\",\"state\":\"%s\",\"zip\":\"%s\",\"country\":\"%s\",\"phone\":\"%s\"}",
                email, firstname, lastname, addr1, city, state, zip, country, phone);

        mockMvc.perform(post("/api/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // --- 技巧3: 缺少参数的测试 ---

    /**
     * 登录时不传 username 参数
     */
    @Test
    void testLoginWithoutUsernameParam() throws Exception {
        mockMvc.perform(post("/api/account/login")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    /**
     * 登录时不传 password 参数
     */
    @Test
    void testLoginWithoutPasswordParam() throws Exception {
        mockMvc.perform(post("/api/account/login")
                        .param("username", "testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    /**
     * 登录时两个参数都不传
     */
    @Test
    void testLoginWithoutAnyParams() throws Exception {
        mockMvc.perform(post("/api/account/login"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500));
    }

    // --- 技巧4: 更新操作的异常路径 ---

    /**
     * 更新用户信息时 service 返回失败
     */
    @Test
    void testUpdateAccountFailure() throws Exception {
        when(accountService.updateAccount(any(Account.class))).thenReturn(false);

        String json = "{\"email\":\"updated@example.com\",\"firstname\":\"Updated\",\"lastname\":\"User\"," +
                "\"addr1\":\"456 Updated St\",\"city\":\"Updated City\",\"state\":\"US\"," +
                "\"zip\":\"54321\",\"country\":\"USA\",\"phone\":\"555-5678\"}";

        mockMvc.perform(put("/api/account/testuser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("更新失败"));
    }

    /**
     * 删除用户时 service 返回失败
     */
    @Test
    void testDeleteAccountFailure() throws Exception {
        when(accountService.deleteAccount("testuser")).thenReturn(false);

        mockMvc.perform(delete("/api/account/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("删除失败"));
    }

    // --- 技巧5: 验证返回数据的完整性 ---

    /**
     * 登录成功后验证完整的用户信息字段
     */
    @Test
    void testLoginSuccessVerifyAllFields() throws Exception {
        when(accountService.login("testuser", "password")).thenReturn(testAccount);

        mockMvc.perform(post("/api/account/login")
                        .param("username", "testuser")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userid").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.firstname").value("Test"))
                .andExpect(jsonPath("$.data.lastname").value("User"))
                .andExpect(jsonPath("$.data.addr1").value("123 Test St"))
                .andExpect(jsonPath("$.data.city").value("Test City"))
                .andExpect(jsonPath("$.data.state").value("TS"))
                .andExpect(jsonPath("$.data.zip").value("12345"))
                .andExpect(jsonPath("$.data.country").value("USA"))
                .andExpect(jsonPath("$.data.phone").value("555-1234"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    /**
     * 获取用户信息时验证所有字段
     */
    @Test
    void testGetAccountVerifyAllFields() throws Exception {
        when(accountService.getAccountByUsername("testuser")).thenReturn(testAccount);

        mockMvc.perform(get("/api/account/testuser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.userid").value("testuser"))
                .andExpect(jsonPath("$.data.signOn.username").value("testuser"))
                .andExpect(jsonPath("$.data.profile.langpref").value("english"))
                .andExpect(jsonPath("$.data.profile.favcategory").value("DOGS"));
    }
}