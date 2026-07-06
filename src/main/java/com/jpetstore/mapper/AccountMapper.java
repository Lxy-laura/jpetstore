package com.jpetstore.mapper;

import com.jpetstore.domain.Account;
import com.jpetstore.domain.Profile;
import com.jpetstore.domain.SignOn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户账户Mapper接口
 */
@Mapper
public interface AccountMapper {

    /**
     * 根据用户名获取账户
     */
    Account getAccountByUsername(@Param("userid") String userid);

    /**
     * 根据用户名和密码获取账户
     */
    Account getAccountByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    /**
     * 获取所有用户
     */
    List<Account> getAllUsers();

    /**
     * 插入账户
     */
    int insertAccount(Account account);

    /**
     * 更新账户
     */
    int updateAccount(Account account);

    /**
     * 删除账户
     */
    int deleteAccount(@Param("userid") String userid);

    /**
     * 插入用户资料
     */
    int insertProfile(Profile profile);

    /**
     * 更新用户资料
     */
    int updateProfile(Profile profile);

    /**
     * 删除用户资料
     */
    int deleteProfile(@Param("userid") String userid);

    /**
     * 插入登录信息
     */
    int insertSignOn(SignOn signOn);

    /**
     * 更新登录信息
     */
    int updateSignOn(SignOn signOn);

    /**
     * 删除登录信息
     */
    int deleteSignOn(@Param("username") String username);
}