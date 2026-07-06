package com.jpetstore.service;

import com.jpetstore.domain.Account;
import com.jpetstore.domain.Profile;
import com.jpetstore.domain.SignOn;
import com.jpetstore.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户账户服务类
 */
@Service
public class AccountService {

    @Autowired
    private AccountMapper accountMapper;

    /**
     * 根据用户名获取账户
     */
    public Account getAccountByUsername(String userid) {
        return accountMapper.getAccountByUsername(userid);
    }

    /**
     * 获取所有用户
     */
    public List<Account> getAllUsers() {
        return accountMapper.getAllUsers();
    }

    /**
     * 用户登录验证
     */
    public Account login(String username, String password) {
        return accountMapper.getAccountByUsernameAndPassword(username, password);
    }

    /**
     * 注册用户
     */
    @Transactional
    public boolean register(Account account) {
        try {
            // 插入账户信息
            accountMapper.insertAccount(account);

            // 插入登录信息
            if (account.getSignOn() != null) {
                accountMapper.insertSignOn(account.getSignOn());
            }

            // 插入用户资料
            if (account.getProfile() != null) {
                accountMapper.insertProfile(account.getProfile());
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 更新账户信息
     */
    @Transactional
    public boolean updateAccount(Account account) {
        try {
            // 更新账户信息
            accountMapper.updateAccount(account);

            // 更新登录信息
            if (account.getSignOn() != null) {
                accountMapper.updateSignOn(account.getSignOn());
            }

            // 更新用户资料
            if (account.getProfile() != null) {
                accountMapper.updateProfile(account.getProfile());
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除账户
     */
    @Transactional
    public boolean deleteAccount(String userid) {
        try {
            // 删除登录信息
            accountMapper.deleteSignOn(userid);
            // 删除用户资料
            accountMapper.deleteProfile(userid);
            // 删除账户信息
            accountMapper.deleteAccount(userid);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}