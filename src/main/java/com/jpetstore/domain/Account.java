package com.jpetstore.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 用户账户实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank(message = "用户ID不能为空")
    private String userid;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "名不能为空")
    private String firstname;

    @NotBlank(message = "姓不能为空")
    private String lastname;

    private String status;

    @NotBlank(message = "地址不能为空")
    private String addr1;

    private String addr2;

    @NotBlank(message = "城市不能为空")
    private String city;

    @NotBlank(message = "州/省不能为空")
    private String state;

    @NotBlank(message = "邮编不能为空")
    private String zip;

    @NotBlank(message = "国家不能为空")
    private String country;

    @NotBlank(message = "电话不能为空")
    private String phone;

    private String role;

    // 关联对象
    private Profile profile;
    private SignOn signOn;

    /**
     * 获取全名
     */
    public String getFullName() {
        return firstname + " " + lastname;
    }

    /**
     * 判断是否为管理员
     */
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}