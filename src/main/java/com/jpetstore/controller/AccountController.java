package com.jpetstore.controller;

import com.jpetstore.common.Result;
import com.jpetstore.domain.Account;
import com.jpetstore.service.AccountService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/login")
    public Result<Account> login(@RequestParam String username, @RequestParam String password, HttpSession session) {
        Account account = accountService.login(username, password);
        if (account != null) {
            session.setAttribute("user", account);
            return Result.success("登录成功", account);
        }
        return Result.unauthorized("用户名或密码错误");
    }

    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody Account account) {
        boolean success = accountService.register(account);
        if (success) {
            return Result.success("注册成功", "注册成功");
        }
        return Result.error("注册失败");
    }

    @GetMapping("/current")
    public Result<Account> getCurrentUser(HttpSession session) {
        Account user = (Account) session.getAttribute("user");
        if (user != null) {
            return Result.success(user);
        }
        return Result.unauthorized("未登录");
    }

    @GetMapping("/{userid}")
    public Result<Account> getAccountByUsername(@PathVariable String userid) {
        Account account = accountService.getAccountByUsername(userid);
        if (account != null) {
            return Result.success(account);
        }
        return Result.notFound("用户不存在");
    }

    @PutMapping("/{userid}")
    public Result<String> updateAccount(@PathVariable String userid, @Valid @RequestBody Account account, HttpSession session) {
        account.setUserid(userid);
        boolean success = accountService.updateAccount(account);
        if (success) {
            session.setAttribute("user", accountService.getAccountByUsername(userid));
            return Result.success("更新成功", "更新成功");
        }
        return Result.error("更新失败");
    }

    @DeleteMapping("/{userid}")
    public Result<String> deleteAccount(@PathVariable String userid) {
        boolean success = accountService.deleteAccount(userid);
        if (success) {
            return Result.success("删除成功", "删除成功");
        }
        return Result.error("删除失败");
    }

    @PostMapping("/logout")
    public Result<String> logout(HttpSession session) {
        session.invalidate();
        return Result.success("登出成功", "登出成功");
    }
}