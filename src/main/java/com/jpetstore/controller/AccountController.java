package com.jpetstore.controller;

import com.jpetstore.common.JwtUtil;
import com.jpetstore.common.Result;
import com.jpetstore.domain.Account;
import com.jpetstore.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestParam String username, @RequestParam String password) {
        Account account = accountService.login(username, password);
        if (account != null) {
            String token = jwtUtil.generateToken(username, account.getRole());
            return Result.success("登录成功", Map.of("token", token, "user", account));
        }
        return Result.unauthorized("用户名或密码错误");
    }

    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody Account account) {
        boolean success = accountService.register(account);
        if (success) return Result.success("注册成功", "注册成功");
        return Result.error(500, "注册失败");
    }

    @GetMapping("/current")
    public Result<Account> getCurrentUser(@RequestAttribute(value = "currentUser", required = false) Account user) {
        if (user != null) return Result.success(user);
        return Result.unauthorized("未登录");
    }

    @GetMapping("/{userid}")
    public Result<Account> getAccountByUsername(@PathVariable String userid) {
        Account account = accountService.getAccountByUsername(userid);
        if (account != null) return Result.success(account);
        return Result.notFound("用户不存在");
    }

    @PutMapping("/{userid}")
    public Result<String> updateAccount(@PathVariable String userid, @Valid @RequestBody Account account,
                                         @RequestAttribute(value = "currentUser", required = false) Account currentUser) {
        account.setUserid(userid);
        boolean success = accountService.updateAccount(account);
        return success ? Result.success("更新成功") : Result.error(500, "更新失败");
    }

    @DeleteMapping("/{userid}")
    public Result<String> deleteAccount(@PathVariable String userid) {
        boolean success = accountService.deleteAccount(userid);
        return success ? Result.success("删除成功") : Result.error(500, "删除失败");
    }

    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success("登出成功", "登出成功");
    }
}
