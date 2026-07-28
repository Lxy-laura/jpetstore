package com.jpetstore.domain;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
@TableName("account")
public class Account implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId
    private String userid;
    @NotBlank(message = "??????")
    @Email(message = "???????")
    private String email;
    @NotBlank(message = "??????")
    private String firstname;
    @NotBlank(message = "??????")
    private String lastname;
    private String status;
    @NotBlank(message = "??????")
    private String addr1;
    private String addr2;
    @NotBlank(message = "??????")
    private String city;
    private String state;
    private String zip;
    private String country;
    @NotBlank(message = "??????")
    private String phone;
    private String role;

    @TableField(exist = false)
    private SignOn signOn;
    @TableField(exist = false)
    private Profile profile;

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public void setAdmin(boolean b) {
        this.role = b ? "ADMIN" : "USER";
    }

    public String getFullName() {
        return (firstname != null ? firstname : "") + " " + (lastname != null ? lastname : "");
    }
}
