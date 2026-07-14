package com.jpetstore.domain;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class Account implements Serializable {
    private static final long serialVersionUID = 1L;

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

    private SignOn signOn;
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